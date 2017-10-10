package com.hc9.controller;

import java.util.ArrayList;
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
import com.hc9.common.util.Arith;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Escrow;
import com.hc9.dao.entity.EscrowAccountinfo;
import com.hc9.dao.entity.EscrowRecharge;
import com.hc9.model.P2pQuery;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeInfo;
import com.hc9.model.crs;
import com.hc9.service.BaoFuService;
import com.hc9.service.EscrowRechargeService;
import com.hc9.service.EscrowService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.PayLogService;
import com.hc9.service.RechargeInfoService;

/***
 * 第三方担保充值
 * @author LKL
 */
@Controller
@RequestMapping(value = { "/escrowRecharge" })
@CheckLogin(value=CheckLogin.ADMIN)
public class EscrowRechargeController {
	
	@Resource
	private EscrowService  escrowService;
	
	@Resource
	private EscrowRechargeService escrowRechargeService;
	
	@Resource
	private LoanSignQuery loanSignQuery;
	
	
	@Resource
	private BaoFuService baoFuService ;
	
    @Resource
    private PayLogService payLogService;
    
	List<NameValuePair> nvps ;
	
	/***
	 * 第三方担保充值记录
	 * @return
	 */
 	@RequestMapping(value = { "openEscrowRecharge" })
	public ModelAndView escrowRecharge() {
		return new ModelAndView("WEB-INF/views/admin/escrow/escrowRechargeRecord");
	}
 	
 	@RequestMapping("queryEscrowRechage")
 	public  String queryEscrowRechage(String id, HttpServletRequest request) {
    	Escrow escrow=escrowService.queryEscrowByid(Long.valueOf(id));
        request.setAttribute("escrow",escrow);
        return "/WEB-INF/views/admin/escrow/escrowRecharge";
    }
 	
 	
 	/***
 	 * 查询第三方担保充值记录
 	 * @param limit
 	 * @param start
 	 * @param escrowRecharge
 	 * @param request
 	 * @param page
 	 * @return
 	 */
	@ResponseBody
	@RequestMapping("escrowRechargeList")
	public JSONObject escrowRechargeList(String limit, String start,
			Escrow escrow, HttpServletRequest request, PageModel page) {
		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();

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
		List datalist = escrowRechargeService.escrowRechargePage(page, escrow,loginuser);
		String titles = "id,name,staff_username,time,fee,rechargeAmount,merfee,feetakenon,additional_info,reAccount,orderNum,status,succ_time";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}
 	
	/***
	 * 第三方担保充值
	 * @param request
	 * @param amount
	 * @param additional_info
	 * @return
	 */
	@RequestMapping("ipsEscrowRecharge.htm")
 	public String ipsEscrowRecharge(HttpServletRequest request, Long id,Double amount,String additional_info){
 		Escrow  escrow=escrowService.queryEscrowByid(id);
 		if(escrow==null){
 			return "WEB-INF/views/failure"; //判断是否存在第三方担保
 		}
 		Costratio costratio = loanSignQuery.queryCostratio();
		String ordernum = "CZ" +  StringUtil.pMerBillNo();
		Double fee=Arith.mul(amount, costratio.getEscrowRecharge()) ;
		RechargeInfo rechargeInfo = new RechargeInfo();
		rechargeInfo.setMerchant_id(ParameterIps.getCert());
		rechargeInfo.setAmount(amount);
		rechargeInfo.setUser_id(escrow.getStaffBaofu());
		rechargeInfo.setFee_taken_on(costratio.getEscrowRechargeMethod().toString());
		rechargeInfo.setFee(fee);
		rechargeInfo.setAdditional_info(additional_info);
		rechargeInfo.setOrder_id(ordernum);
		rechargeInfo.setReturn_url(Constant.ASYNESCROWRECHARGE);
		rechargeInfo.setPage_url( Constant.ESCROWRECHARGE);
		
		//保存第三方担保充值记录
		EscrowRecharge escrowRecharge=new EscrowRecharge();
		escrowRecharge.setRechargeAmount(amount);
		escrowRecharge.setEscrow(escrow);
		escrowRecharge.setOrderNum(ordernum);
		escrowRecharge.setStatus(Constant.STATUES_ZERO);
		escrowRecharge.setMer_fee(fee);
		escrowRecharge.setFee_taken_on(costratio.getRechargeMethod());
		escrowRecharge.setAdditional_info(additional_info);
		escrowRecharge.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		escrowRechargeService.saveEscrowRecharge(escrowRecharge);
		
		Map<String, String> map = null;
		try {
			String registerXml = ParseXML.rechargeXml(rechargeInfo);
			payLogService.savePayLog(registerXml, escrow.getId(), 18, ordernum,0,fee,0.00,amount);
			map = RechargeInfoService.escrowRechargeCall(registerXml, ParameterIps.getmerchantKey());
			map.put("url", PayURL.RECHARGETESTURL);
			request.getSession().setAttribute("map", map);
			return "WEB-INF/views/hc9/member/trade/central_news";
		} catch (Exception e) {
			e.printStackTrace();
			return "WEB-INF/views/failure";
		}
 	}
 	
 	/***
 	 * 第三方担保充值查询
 	 * @param request
 	 * @param rId
 	 * @return
 	 */
    @ResponseBody
 	@RequestMapping("ipsEscrowRechargeNum.htm")
 	public  String ipsEscrowRechargeNum(HttpServletRequest request,Long id){
 		EscrowRecharge escrowRecharge=escrowRechargeService.queryEscrowRecharge(id);
 		P2pQuery p2pQuery=new  P2pQuery(escrowRecharge.getOrderNum(), 5);
 		nvps = new ArrayList<NameValuePair>();
 		try{
			String escrowRechargeXml = ParseXML.p2pQueryXml(p2pQuery);
	    	nvps.add(new BasicNameValuePair("requestParams", escrowRechargeXml));
	    	nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(escrowRechargeXml + "~|~" + ParameterIps.getmerchantKey())));
	    	String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL, nvps);
	    	System.out.println("充值业务查询="+result);
	    	Document doc = DocumentHelper.parseText(result);
	    	Element rootElt = doc.getRootElement(); // 获取根节点
	    	System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
            // 拿到crs节点下的子节点code值
            String code = rootElt.elementTextTrim("code"); 
            String msg =  rootElt.elementTextTrim("msg"); 
            String sign =  rootElt.elementTextTrim("sign"); 
            // 获取子节点crs下的子节点result
            Iterator iteratorResult = rootElt.elementIterator("result"); 
            boolean Judge=false;  //判断是否有值
            String state="-1";
            // 遍历result节点下的Response节点
            while (iteratorResult.hasNext()) {
                Element itemEle = (Element) iteratorResult.next();
                Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
                while (iteratorOrder.hasNext()) {
                        Element elementOrder = (Element) iteratorOrder.next();
                        String order_id = elementOrder.elementTextTrim("order_id"); 
                        state = elementOrder.elementTextTrim("state");  //1-成功  0-处理中
                        String succ_amount=elementOrder.elementTextTrim("succ_amount");
                        String succ_time=elementOrder.elementTextTrim("succ_time");
                        String fee=elementOrder.elementTextTrim("fee");
                        String baofoo_fee=elementOrder.elementTextTrim("baofoo_fee");
                        String fee_taken_on=elementOrder.elementTextTrim("fee_taken_on");
                        String Md5sign = CommonUtil.MD5(code+ "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
        				if (sign.equals(Md5sign)) {
        					   if(code.equals("CSD000")){
									if(state.equals("1")){
										escrowRecharge.setFee(Double.valueOf(baofoo_fee)); // 宝付收取费用
										escrowRecharge.setMer_fee(Double.valueOf(fee)); // 商户收取的手续费
										escrowRecharge.setIncash_money(Arith.sub(Double.valueOf(succ_amount), Double.valueOf(baofoo_fee))); // 充值结算金额(实际到账)
										escrowRecharge.setStatus(Integer.parseInt(state));
										escrowRecharge.setSuccTime(succ_time);
										escrowRecharge.setFee_taken_on(Integer.valueOf(fee_taken_on));
										// 流水账
										EscrowAccountinfo account = new EscrowAccountinfo();
										account.setExpenditure(0.00);
										account.setExplan("第三方担保充值");
										account.setIncome(Double.valueOf(succ_amount));
										account.setIpsNumber(order_id);
										account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
										account.setEscrow(escrowRecharge.getEscrow());
										account.setIsRechargeWithdraw(Constant.STATUES_ONE);
										// 余额查询
										crs cr = baoFuService.getCasbalance(escrowRecharge.getEscrow().getStaffBaofu());
										escrowRecharge.getEscrow().setStaffMoney(cr.getBalance());
										account.setMoney(cr.getBalance());
										escrowRechargeService.uptEscrowRecharge(escrowRecharge);
										escrowRechargeService.saveEscrowAccountinfo(account);//保存流水账余额
										// 更新支付报文信息
										payLogService.updatePayLog(escrowRecharge.getOrderNum(),Integer.parseInt(state),escrowRecharge.getIncash_money(), escrowRecharge.getFee());
										Judge=true;
										LOG.error("宝付第三方担保充值查询处理成功");
									}else if(state.equals("0")){
										LOG.error("宝付第三方担保充值查询处理中");
										return "2";
									}
        					   }
        				}else{
        					LOG.error("非宝付第三方担保充值查询返回数据--" +result+ "----->订单号----->"+ escrowRecharge.getOrderNum());
        					return "3";
        				}
                 }
            }
            if(!Judge&&state.equals("-1")){
            	   escrowRecharge.setStatus(-1);
				   escrowRecharge.setFee(0.00);
				   escrowRechargeService.uptEscrowRecharge(escrowRecharge);
				   payLogService.updatePayLog(escrowRecharge.getOrderNum(),-1,escrowRecharge.getIncash_money(), escrowRecharge.getFee());
				   return "4";
            }
          return "1";
    }catch (Exception e) {
		   e.printStackTrace();
		  LOG.error("宝付第三方担保充值查询失败----->订单号----->" +  escrowRecharge.getOrderNum());
		 return "5";
	 }
 	} 	
}
