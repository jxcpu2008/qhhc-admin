package com.hc9.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hc9.common.log.LOG;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.DateUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.ChannelSpreadDao;
import com.hc9.dao.entity.ChannelSpreadDetail;
import com.hc9.model.ChannelInfo;

@Service
public class DspService {

	@Resource
	private ChannelSpreadDao spreadDao; 
	
	/**
	 * 
	 * @param request
	 * @param type 操作方式：1注册，2投资
	 * @param param 参数数组
	 */
	public boolean channelSwitch(HttpServletRequest request,int type,String...param){
		/**
		 * 从cookie获取spreadId
		 * 反查该spreadId对应的渠道商
		 * 引用该渠道商对应的方法
		 */
		boolean flag=false;
		String value=this.getValueFromCookie(request);
		if(value==null){
			return flag;
		}
		Map<String,String> map=this.getSpecialValue(value, "@_@");
		String spreadId=map.get("spreadId");
		String channel=spreadDao.getChannelName(spreadId);
		if(channel==null){
			return flag;
		}

		//统一投资参数，0标id，1订单号，2订单时间，3数量，4价格，5标明，6投资id，7用户id
		if ("亿起发CPS".equals(channel) && type==2){
			//productNo,orderNo,orderTime,amount,price,name,loanRecordId, request
			flag=yiQiFaCommitOrder(map,value,channel,param[0],param[1],param[2],param[3],param[4],param[5],param[6],request);
		
		}
		if("易瑞特".equals(channel) && type==1){
			flag=yiRuiTeCommitRegInfo(map, channel,param[param.length-1],request);

		}
		if("领克特".equals(channel) && type==2){
			flag=linktechCommitOrder(map,value,channel,param[7],param[1],param[0],param[4],param[3],param[6],request);

		} 
		if("亿起发DSP".equals(channel)&&type==2){
			flag=yiqifaDspRecord(map, channel, param[param.length-1], param[param.length-2], request);
		}
		return flag;
	}
	
	/**
	 * 亿起发投资信息推送
	 * @param productNo 标id
	 * @param orderNo 订单号
	 * @param orderTime 订单下单时间
	 * @param amount 数量
	 * @param price 金额
	 * @param name 标名称
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public boolean yiQiFaCommitOrder(
			Map<String,String> map,String cookieValue,
			String channel,
			String productNo,String orderNo,String orderTime,String amount,String price,String name,String loanRecordId,
			HttpServletRequest request) {
		//0 value  1source   2channel   3cid  4wi  5sessionId;
		
		String campaignId=map.get("cid");
		String feedback=map.get("wi");
		String uri="http://o.yiqifa.com/servlet/handleCpsInterIn";
		String interId="55d682b37c5cd0b77aa207f3";//一起发提供
		String json="";
		String encoding="GBK";
		JSONObject mainObject=new JSONObject();
		JSONArray orderArray=new JSONArray();
		
		JSONObject productObject=new JSONObject();
		JSONObject orderObject=new JSONObject();
		JSONArray productArray=new JSONArray();
		productObject.put("productNo", productNo);
		productObject.put("name", name);
		productObject.put("amount", amount);
		productObject.put("price", price);
		String category=spreadDao.getLoanRemonthType(productNo);
		productObject.put("category", category);
		productObject.put("commissionType", category);
		productArray.add(productObject);
		
		orderObject.put("orderNo", orderNo);
		orderObject.put("orderTime", DateUtil.formatSimple());
		orderObject.put("updateTime",  DateUtil.formatSimple());
		orderObject.put("campaignId", campaignId);
		orderObject.put("feedback", feedback);
		orderObject.put("fare", "0");
		orderObject.put("favorable", "0");
		orderObject.put("products", productArray);
		orderObject.put("favorableCode", null);
		orderObject.put("orderStatus", "1");
		orderObject.put("paymentStatus", "1");
		orderObject.put("paymentType", "1");
		
//		jsonObject.put("encoding", encoding);
		orderArray.add(orderObject);
		mainObject.put("orders", orderArray);
		json=mainObject.toJSONString();
		try {
			json = URLEncoder.encode(json, encoding);
		} catch (UnsupportedEncodingException e) {
			LOG.error("转码失败"+e.getMessage());
		}
		uri=uri+"?interId="+interId+"&json="+json+"&encoding="+encoding;
		
		this.connectToServerTransData(uri,channel);
		
		ChannelSpreadDetail detail=new ChannelSpreadDetail();
		detail.setLoanRecordId(Long.parseLong(loanRecordId));
		detail.setSessionId(map.get("sessionId"));
		detail.setSpreadId(map.get("spreadId"));
		detail.setCookieValue(cookieValue);
		spreadDao.saveChannelSpreadDetail(detail);	
		return true;
	}
	

	/**
	 * 易瑞特注册信息推送
	 * @param cookieValue cookie值
	 * @param channel 渠道名称
	 * @param uid 用户id
	 * @param request
	 * @return
	 */
	public boolean yiRuiTeCommitRegInfo(Map<String,String> map,String channel,String uid,
			HttpServletRequest request){
		boolean flag=false;
		String uri="http://app.offer99.com/callback/callback_adv/callback_adv_b3387101168abf699887d34319c6381c.php";
		String ad_key="3nj1d83nhl";
		String tid="";
		tid=map.get("tid");//取tid数据
		uid=uid+DateUtils.getHHMMSS();
		String sign=CommonUtil.MD5(tid+uid+ad_key);
		uri=uri+"?tid="+tid+"&uid="+uid+"&sign="+sign;
		flag=connectToServerTransData(uri,channel);

		return flag;
	}
	/**
	 * 领科特投资信息推送
	 * @param request
	 * @param cookieValue cookie 数组
	 * @param channel 渠道名称
	 * @param uid 用户id
	 * @param orderSn 订单号
	 * @param loanId 标id
	 * @param tendMoney 投资金额
	 * @param count 数量
	 * @return
	 */
	public boolean linktechCommitOrder(
			Map<String,String> map, String cookieValue,
			String channel,String uid,String orderSn,String loanId,String tendMoney,String count,String loanRecordId,
			HttpServletRequest request
			){
		boolean flag=false;
		String uri="http://service.linktech.cn/purchase_cps.php";
		
		//cookie数据格式："0channelspreadid:1a_id:2m_id:3c_id:4l_id:5l_type1:6rd:7url:861E6B28B20BA14982A88E797801E1FB6"
		//a_id 数据拼接格式：a_id + "|" + c_id + "|" + l_id + "|" + l_type1 + "|"
		String a_id = map.get("a_id")+"|"+map.get("c_id")+"|"+map.get("l_id")+"|"+map.get("l_type1");
		try {
			a_id=URLEncoder.encode(a_id,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("[URLEncoder 转码失败]");
		}
		String m_id=map.get("m_id");
		String mbr_id="hcUser";//
		String o_cd=orderSn;//订单号
		String p_cd=loanId;//商品编号
		String price=tendMoney;//商品单价
		String it_cnt=count;//商品数量
		String c_cd="1";//商品分类
		
		//参数拼接格式：a_id=a_id&m_id=m_id&c_id=c_id&l_id=l_id&l_type1=l_type1&rd=rd&url=url 
		uri=uri+"?a_id="+a_id+"&m_id=" +m_id+"&mbr_id=" +mbr_id+"&o_cd="+o_cd+"&p_cd="+p_cd+"&price="+price+"&it_cnt="+it_cnt+"&c_cd="+c_cd;
		flag=this.connectToServerTransData(uri, channel);
		
		ChannelSpreadDetail detail=new ChannelSpreadDetail();
		detail.setLoanRecordId(Long.parseLong(loanRecordId));
		detail.setSessionId(map.get("sessionId"));
		detail.setSpreadId(map.get("spreadId"));
		detail.setCookieValue(cookieValue);
		spreadDao.saveChannelSpreadDetail(detail);	
		return flag;
	}
	
	/**
	 * 亿起发DSP操作记录
	 * @param cookieValue cookie数据
	 * @param channel 渠道名
	 * @param type 操作类型 1注册，2投资
	 * @param uid 用户id
	 * @param loanRecordId 投资记录id
	 * @param request
	 * @return
	 */
	public boolean yiqifaDspRecord(Map<String,String> map,String channel, String uid,String loanRecordId,	HttpServletRequest request){
		boolean flag = false;
		ChannelSpreadDetail detail=new ChannelSpreadDetail();
		detail.setLoanRecordId(Long.parseLong(loanRecordId));
		detail.setSessionId(map.get("sessionId"));
		detail.setSpreadId(map.get("spreadId"));
		spreadDao.saveChannelSpreadDetail(detail);	
		return flag;
	}
	/**
	 * 获取cookie中的value
	 * @param request
	 * @return
	 */
	public String getValueFromCookie(HttpServletRequest request){
		Cookie[] cookies=request.getCookies();
		String value=null;
		Cookie cookie=null;
		if(cookies==null) return null;
		
		for(int i=0;i<cookies.length;i++){
			cookie=cookies[i];
			if(cookie.getName().equals("hc9")){
				value=cookie.getValue();
				break;
			}
		}
		return value;
	}
	
	/**
	 * 请求对方服务，发送数据
	 * @param uri
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public boolean connectToServerTransData(String uri,String name) {
		HttpGet httpGet=new HttpGet(uri);
		HttpResponse response;
		boolean flag=false;
		try {
			response = new DefaultHttpClient().execute(httpGet);
			HttpEntity entity=response.getEntity();
			LOG.info(name+"："+EntityUtils.toString(entity));
			flag=true;
		} catch (ClientProtocolException e) {
			LOG.error("连接服务器失败"+e.getMessage());
		} catch (IOException e) {
			LOG.error("IO"+e.getMessage());
		}finally{
			httpGet.releaseConnection();
		}
		
		return flag;
	}
	
	/**
	 * 判断spreadId是不是亿起发的DSP
	 * @param spreadId
	 * @return
	 */
	public boolean isYiqifaDsp(String spreadId) {
		String channel=spreadDao.getChannelName(spreadId);
		if(channel!=null && "亿起发DSP".equals(channel)){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取投资记录
	 * @param spreadId
	 * @return
	 */
	public List<Object[]> getTenderValue(String spreadId) {
		List<Object[]> loanrecordIds=spreadDao.getLoanrecordsBySpreadId(spreadId,null,null);
		
		if(loanrecordIds==null) return null;
		
		List<Object[]> resultList=new ArrayList<>(loanrecordIds.size());
		for(Object objs:loanrecordIds){
			Object[] results=spreadDao.getUserInfoByRecordId(objs);
			resultList.add(results);
		}
		return resultList;
		
	}

	public Map<String, Object> getYiQiFaCpsWithJson(String spreadId,String startTime, String endTime) {
		List<Object[]> loanrecordIds=spreadDao.getLoanrecordsBySpreadId(spreadId,startTime,endTime);
		
		if(loanrecordIds==null) return null;
		
		JSONObject mainObject=new JSONObject();
		JSONArray orderArray=new JSONArray();
		
		for(Object[] objs:loanrecordIds){
			Object[] results=spreadDao.getLoanInfoByRecordId(objs[0]);
			//results[0]lr.tenderTime,results[1]lr.tenderMoney,results[2]lr.loanSign_id,results[3]lr.order_id,results[4]l.`name`,results[5]l.loanUnit
			String cookieValue=(String) objs[1];
			
			if(null==cookieValue) continue;
			
			Map<String,String> map=this.getSpecialValue(cookieValue, "@_@");
			JSONObject orderObject=new JSONObject();
			orderObject.put("orderNo", results[3]);
			orderObject.put("orderTime",results[0]);
			orderObject.put("updateTime",  results[0]);
			orderObject.put("campaignId", map.get("cid"));
			orderObject.put("feedback", map.get("wi"));
			orderObject.put("fare", "0");
			orderObject.put("favorable", "0");
			orderObject.put("favorableCode", null);
			orderObject.put("orderStatus", "1");
			orderObject.put("paymentStatus", "1");
			orderObject.put("paymentType", "1");
			JSONArray productArray=new JSONArray();
			JSONObject productObject=new JSONObject();
			productObject.put("productNo", results[2]);
			productObject.put("name", results[4]);
			productObject.put("amount", Double.parseDouble(results[1].toString())/Double.parseDouble(results[5].toString()));
			productObject.put("price", results[1]);
			String category=spreadDao.getLoanRemonthType(results[2].toString());
			productObject.put("category", category);
			productObject.put("commissionType", category);
			productArray.add(productObject);
			orderObject.put("products", productArray);
			orderArray.add(orderObject);
		}
		
		mainObject.put("orders", orderArray);
		return mainObject;
	}
	/**
	 * 
	 * @param spreadId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Map<String, Object> getyrtCpsWithJson(String spreadId,String startTime, String endTime) {
		//regUserName,regStatus,cookieValue
		List<Object[]> yrtResult=spreadDao.getYrtRegData(spreadId,startTime,endTime);
		if(yrtResult==null) return null;
		
		JSONObject mainObject=new JSONObject();
		JSONArray orderArray=new JSONArray();
		for(Object[] objs:yrtResult){
			String cookieValue=(String) objs[2];
			if(null==cookieValue) continue;
			Map<String,String> map=this.getSpecialValue(cookieValue, "@_@");
			
			JSONObject orderObject=new JSONObject();
			
			JSONArray infoArray=new JSONArray();
			JSONObject infoObject=new JSONObject();
			infoObject.put("UserName", objs[0]);
			if(Integer.valueOf(objs[1].toString())==-1){//注册失败处理
				orderObject.put("errorcode", "-1");
				orderObject.put("errormsg","注册失败");
				infoObject.put("RegisterTime:", "");
				infoObject.put("IsValidateEmail", "");
				infoObject.put("IsValidateMobile", "");
				infoObject.put("IsValidateIdentity", "");
				infoObject.put("IsIpsAuth", "");
				infoObject.put("amount", "");
				infoObject.put("tid", map.get("tid"));
			}else{
				//u.createTime,r.emailisPass,r.phonepass,u.isAuthIps
				orderObject.put("errorcode", "1");
				List<Object[]> results=null;
				try {
					results=spreadDao.getUserInfoByUserName(objs[0]);
				} catch (Exception e) {
					LOG.error(e);
				}
				
				if(null==results || results.size()==0) continue;
				
				Object[] userInfo=results.get(0);
				orderObject.put("errormsg","注册成功");
				infoObject.put("RegisterTime", userInfo[0]);
				boolean IsValidateEmail=false;
				boolean IsValidateMobile=false;
				boolean IsIpsAuth=false;
				if(null!=userInfo[1] && Integer.valueOf(userInfo[1].toString())==1){
					IsValidateEmail=true;
				}
				if(null!=userInfo[2] && Integer.valueOf(userInfo[2].toString())==1){
					IsValidateMobile=true;
				}
				if(null!=userInfo[3] && Integer.valueOf(userInfo[3].toString())==1){
					IsIpsAuth=true;
				}
				infoObject.put("IsValidateEmail", IsValidateEmail);
				infoObject.put("IsValidateMobile", IsValidateMobile);
				infoObject.put("IsValidateIdentity", IsIpsAuth);
				infoObject.put("IsIpsAuth", IsIpsAuth);
				String amount="";
				String month="";
				Object[] objFirst;
				try {
					objFirst=spreadDao.getUserFirstInvestByUserName(objs[0]);
					if(null!=objFirst){
						amount=String.valueOf(objFirst[0]);
						month=String.valueOf(objFirst[1]);
					}
				} catch (Exception e) {
					amount="";
					month="";
					LOG.error("DSP首次投资数据出错"+e);
				}
				infoObject.put("amount", amount);
				infoObject.put("month", month);
				infoObject.put("tid", map.get("tid"));
			}
			infoArray.add(infoObject);

			
			orderObject.put("info", infoObject);
			orderArray.add(orderObject);
		}
		
		mainObject.put("orders", orderArray);
		return mainObject;
	}
	
	public List<String[]> getLinKeTeData(String spreadId,String startTime,String endTime){
		List<Object[]> loanrecordIds=spreadDao.getLoanrecordsBySpreadId(spreadId,startTime,endTime);
		if (null==loanrecordIds) return null;
		List<String[]> results=new ArrayList<String[]>(); 
		//1:check,2:hhmiss,3:a_id,4:o_cd,5:p_cd,6:mbr_id,7:it_cnt,8:price,9:c_cd
		for(Object[] objs:loanrecordIds){
			Object[] dataArray=spreadDao.getLoanInfoByRecordId(objs[0]);

			//dataArray的数据：0-lr.tenderTime,1-lr.tenderMoney,2-lr.loanSign_id,3-lr.order_id,4-l.`name`,5-l.loanUnit
			String cookieValue=(String) objs[1];
			if(null==cookieValue) continue;
			
			Map<String,String> map=this.getSpecialValue(cookieValue, "@_@");
			double it_cnt=Double.parseDouble(dataArray[1].toString())/Double.parseDouble(dataArray[5].toString());
			String[] strs={"2\t",getTime(String.valueOf(dataArray[0]))+"\t",
					map.get("a_id")+"\t",String.valueOf(dataArray[3])+"\t",String.valueOf(dataArray[2])+"\t",
					"hc9User"+"\t",String.valueOf(it_cnt),String.valueOf(dataArray[1])+"\n"
					};
			results.add(strs);
		}
		return results;
	}
	/**
	 * 查询 渠道账户 是否存在
	 * add by xuyh 
	 * at 2015/08/31
	 */
	public String findChannelUserByLoginName(String loginName,String password){
		return spreadDao.findChannelUserByLoginName(loginName,password);
	}
	
	/**
	 * 查询 搜集注册,投资 等 用户信息 
	 * add by xuyh 
	 * at 2015/08/31
	 */
	public List<ChannelInfo> getChannelInfoList(String spreadId,String beginTime,String endTime,Long minPage,Long maxPage){
		return spreadDao.getChannelInfoList(spreadId,beginTime,endTime,minPage,maxPage);
	}
	
	/**
	 * 查询 搜集注册,投资 等 用户信息  总数
	 * add by xuyh 
	 * at 2015/08/31
	 */
	public Long getChannelInfoCount(String spreadId,String beginTime,String endTime){
		return spreadDao.getChannelInfoCount(spreadId, beginTime, endTime);
	}
	
	/**
	 * 拆解cookie的值为k-v
	 * @param cookieValue
	 * @param 字符串拆封标识
	 * @return Map
	 */
    public Map<String,String> getSpecialValue(String cookieValue,String regex){
    	String[] arr=cookieValue.split(regex);
    	Map<String,String> map=new HashMap<String,String>();
    	map.put("spreadId", arr[0]);
    	map.put("sessionId", arr[arr.length-1]);
    	for(int i=1;i<arr.length-1;i++){
    		String[] arr2=arr[i].split("=");
    		map.put(arr2[0], arr2[1]);
    	}
    	return map;
    } 
    private String getTime(String date){
		String[] dateSplit=date.split(" ");
		String[] timeSplit= dateSplit[1].split(":");
		String str="";
		for(int i=0;i<timeSplit.length;i++){
			str+=timeSplit[i];
		}
		return str;
    }
}
