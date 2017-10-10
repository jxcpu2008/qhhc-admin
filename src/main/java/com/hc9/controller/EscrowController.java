package com.hc9.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.constant.PayURL;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.DateUtil;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Escrow;
import com.hc9.model.PageModel;
import com.hc9.model.RegisterInfo;
import com.hc9.model.crs;
import com.hc9.service.BaoFuService;
import com.hc9.service.EscrowService;
import com.hc9.service.PayLogService;
import com.hc9.service.RechargeInfoService;

/**
 * <p>
 * Title:AdminController
 * </p>
 * <p>
 * Description: 后台用户控制层
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>

 */
@Controller
@RequestMapping(value = { "/escrow" })
@CheckLogin(value=CheckLogin.ADMIN)
public class EscrowController {
    @Resource
     private  EscrowService  escrowService;
    
	@Resource
	private BaoFuService baoFuService;
	
	@Resource
	private PayLogService payLogService;
	
	List<NameValuePair> nvps;
     
 	@RequestMapping(value = { "openEscrow" })
	public ModelAndView escrow() {
		return new ModelAndView("WEB-INF/views/admin/escrow/escrow");
	}
 	
 	
 	/****
 	 * 查询第三担保方信息
 	 * @param limit
 	 * @param start
 	 * @param request
 	 * @param page
 	 * @param escrow
 	 * @return
 	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("escrowList")
	@ResponseBody
	public JSONObject escrowList(String limit, String start,
			HttpServletRequest request, PageModel page, Escrow escrow) {
		JSONObject resultjson = new JSONObject();
		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}
		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}
		Adminuser loginuser = (Adminuser) request.getSession() .getAttribute(Constant.ADMINLOGIN_SUCCESS);
		// 分页数据源
		List list = escrowService.getEscrowList(page, escrow,loginuser);
		JSONArray jsonlist = new JSONArray();
		String titles = "id,name,brief,history,team,mission,phone,address,staffName,staffPhone,staffIdcard,staffEmail,staffBaofu,staffBaofuCreateTime,inAccredit,accreditTime,inBaofu,staffUsername,staffMoney";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}
 	
	/***
	 * 删除第三方担保信息
	 * @param request
	 * @param ids
	 * @return
	 */
    @ResponseBody
    @RequestMapping("/delEscrow")
	public String  delEscrow(HttpServletRequest request,String ids){
		return escrowService.delEscrow(ids);
	}
    
    
    /**
     * 查询第三方担保信息
     * @param ids
     * @param request
     * @return
     */
    @RequestMapping("query")
    public String query(String id, HttpServletRequest request) {
    	Escrow escrow=escrowService.queryEscrowByid(Long.valueOf(id));
        request.setAttribute("escrow",escrow);
        request.setAttribute("uptState", escrow.getInBaofu());
        return "/WEB-INF/views/admin/escrow/addOrUptEscrow";
    }
    
    /**
     * 添加第三方担保
     * @param chattel
     * @return
     */
    @RequestMapping("add")
    public ModelAndView add(Escrow escrow, HttpServletRequest request) {
    	 request.setAttribute("uptState", "0");
    	ModelAndView returnModelAndView = new ModelAndView(
				"WEB-INF/views/admin/escrow/addOrUptEscrow");
		return returnModelAndView;

    }
    
    @ResponseBody
    @RequestMapping("addOrUptEscrow")
    public JSONObject addOrUptEscrow(Escrow escrow) {
        JSONObject json = new JSONObject();
        // 修改联系信息
        escrowService.udapteEscrow(escrow);
		json.element("statusCode", "200");
		json.element("message", "更新成功");
		json.element("navTabId", "main29");
		json.element("callbackType", "closeCurrent");
        return json;
    }
    
    
    /***
     * 根据宝付个人账户查询余额
     * @param request
     * @param userId
     * @return
     */
    @RequestMapping("/ipsSelEscrowMoney")
    public String ipsSelEscrowMoney(HttpServletRequest request, String id){
    	Escrow  escrow=escrowService.queryEscrowByid(Long.valueOf(id));
    	try{
    		crs cr = baoFuService.getCasbalance(escrow.getStaffBaofu());
    		escrow.setStaffMoney(cr.getBalance());
    	    escrowService.udapteEscrow(escrow);
        	request.setAttribute("escrow", escrow);
        	return "/WEB-INF/views/admin/escrow/selEscrowMoney";
    	}catch(Exception e){
    		e.printStackTrace();
    		return "WEB-INF/views/failure";
    	}
    }
    
    /***
     * 根据宝付个人账户查询银行卡信息
     * @param request
     * @param userId
     * @return
     */
    @RequestMapping("/ipsSelEscrowBank")
    public  String ipsselEscrowBank(HttpServletRequest request, String id){
    	Escrow  escrow=escrowService.queryEscrowByid(Long.valueOf(id));
    	nvps = new ArrayList<NameValuePair>();
    	try{
    		nvps.add(new BasicNameValuePair("requestParams", escrow.getStaffBaofu()));
        	nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(escrow.getStaffBaofu() + "~|~" + ParameterIps.getmerchantKey())));
        	String result = CommonUtil.excuteRequest(PayURL.GETBANKCARURL, nvps);
        	System.out.println("绑定验证码="+result);
        	Document doc = DocumentHelper.parseText(result);
        	Element rootElt = doc.getRootElement(); // 获取根节点
        	System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
            // 拿到crs节点下的子节点code值
            String code = rootElt.elementTextTrim("code"); 
            String msg =  rootElt.elementTextTrim("msg"); 
            String sign =  rootElt.elementTextTrim("sign"); 
            String infoXml=rootElt.elementTextTrim("info");
            String Md5sign = CommonUtil.MD5(code+ "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
			if (sign.equals(Md5sign)) {
					if (code.equals("CSD000")) {
				            String info=CommonUtil.aesDecryptKey16(infoXml, ParameterIps.getmerchantKey());
				            System.out.println(info);
				            Document infoDoc = DocumentHelper.parseText(info);
				        	Element rootInfoElt = infoDoc.getRootElement(); // 获取根节点
				        	System.out.println("根节点：" + rootInfoElt.getName()); // 拿到根节点的名称
			                Iterator iteratorOrder = rootInfoElt.elementIterator("bank"); // 获取子节点result下的子节点order
			                while (iteratorOrder.hasNext()) {
			                	  Element elementOrder = (Element) iteratorOrder.next();
			                      String bank_no = elementOrder.elementTextTrim("bank_no"); 
			                      String bank_name = elementOrder.elementTextTrim("bank_name");
			                      String pro_value=elementOrder.elementTextTrim("pro_value");
			                      String city_value=elementOrder.elementTextTrim("city_value");
			                      String bank_address=elementOrder.elementTextTrim("bank_address");
			                      escrow.setStaffBankNo(bank_no);
			                      escrow.setStaffBankName(bank_name);
			                      escrow.setStaffAddress(bank_address);
			                      escrow.setStaffPro(pro_value);
			                      escrow.setStaffCity(city_value);
			                      escrowService.udapteEscrow(escrow);
			                      request.setAttribute("escrow", escrow);
			            }
			            return "/WEB-INF/views/admin/escrow/selEscrowBank";
					}else{
						  request.setAttribute("msg", msg);
						  return "/WEB-INF/views/admin/fund/SelUserBankError";
					}
            }else{
            	   request.setAttribute("msg", msg);
            	  return "/WEB-INF/views/admin/fund/SelUserBankError";
            }
    	}catch (Exception e) {
    		e.printStackTrace();
    		request.setAttribute("msg", "平台处理出错，请联系管理员");
    		return "/WEB-INF/views/admin/fund/SelUserBankError";
		}
    }
    
    
    /***
     * 注册宝付账户
     * @param id
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/ipsRegisterEscrow")
    public  String ipsRegisterEscrow(String id ,HttpServletRequest request){
    	Escrow  escrow=escrowService.queryEscrowByid(Long.valueOf(id));
    	RegisterInfo registerInfo = new RegisterInfo();
    	Long user_id = new Date().getTime();
    	registerInfo.setBf_account(escrow.getStaffPhone());
		registerInfo.setId_card(escrow.getStaffIdcard());
		registerInfo.setName(escrow.getStaffName());
		registerInfo.setUser_id(String.valueOf(user_id));
		registerInfo.setAccount_type("1");
		registerInfo.setHas_bf_account("0");
		registerInfo.setBind_code("0");
    	nvps = new ArrayList<NameValuePair>();
    	try{
    		String rechargeInfoxml = CommonUtil.aesEncryptKey16(ParseXML.registrationXml(registerInfo),ParameterIps.getmerchantKey());
        	nvps.add(new BasicNameValuePair("requestParams",rechargeInfoxml));
    		nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(rechargeInfoxml + "~|~"+ ParameterIps.getmerchantKey())));
    		String result = CommonUtil.excuteRequest(PayURL.QUICKREGISTERURL,nvps);
    		System.out.println("用户注册=" + result);
			Document doc = DocumentHelper.parseText(result);
			Element rootElt = doc.getRootElement(); // 获取根节点
			System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
			// 拿到crs节点下的子节点code值
			String code = rootElt.elementTextTrim("code");
			String msg = rootElt.elementTextTrim("msg");
			String sign = rootElt.elementTextTrim("sign");
			String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
			if (sign.equals(Md5sign)) {
				if (code.equals("CSD000")) {
					crs cr = baoFuService.getCasbalance(String.valueOf(registerInfo.getUser_id()));
		    		escrow.setStaffMoney(cr.getBalance());
		    		escrow.setInBaofu(1);
		    		escrow.setStaffBaofuCreateTime(DateUtil.format("yyyy-MM-dd HH:mm:ss"));
		    		escrow.setStaffBaofu(String.valueOf(registerInfo.getUser_id()));
					 escrowService.udapteEscrow(escrow);
					 LOG.error("注册过宝付成功，第三方担保用户Id=" + escrow.getId());
					return "0";
				}else if(code.equals("BD001")){  //已注册
					LOG.error("已注册过宝付，第三方担保用户Id=" + escrow.getId());
					return msg; //已注册
				}else{
					LOG.error("宝付注册处理失败，第三方担保用户Id=" + escrow.getId());
					return  msg; //注册失败
				}
			} else {
				LOG.error("非宝付注册返回数据，第三方担保用户Id=" + escrow.getId());
				return  msg; 
			}
    	}catch(Exception e){
    		e.printStackTrace();
    		LOG.error("宝付注册处理失败，用户Id=" + escrow.getId());
    		return "1";
    	}
    }
    
    /****
     * 授权接口(页面)
     * @param id
     * @param request
     * @return
     */
	@RequestMapping("ipsInAccredit.htm")
	@ResponseBody
    public  String ipsInAccredit(String id,HttpServletRequest request){
    	Escrow  escrow=escrowService.queryEscrowByid(Long.valueOf(id));
    	Map<String, String> map = null;
    	try{
    		map = RechargeInfoService.inAccreditCall(escrow.getStaffBaofu());
    		payLogService.savePayLog(escrow.getStaffBaofu()+"_"+escrow.getName(), escrow.getId(),  escrow.getId(), 16,
    				escrow.getStaffBaofu(), 0.00, 0.00, 0.00); // 保存xml报文
    		map.put("url", PayURL.INACCREDITURL);
    		request.getSession().setAttribute("map", map);
    		return "member/callcentralInAccredit.htm";
    	}catch(Exception e){
    		e.printStackTrace();
    		return "1";
    	}
    }
	
	

	
	
    
    
}
