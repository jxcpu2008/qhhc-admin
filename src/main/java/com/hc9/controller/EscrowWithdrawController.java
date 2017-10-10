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
import com.hc9.dao.entity.EscrowWithdraw;
import com.hc9.model.P2pQuery;
import com.hc9.model.PageModel;
import com.hc9.model.WithdrawalInfo;
import com.hc9.model.crs;
import com.hc9.service.BaoFuService;
import com.hc9.service.EscrowRechargeService;
import com.hc9.service.EscrowService;
import com.hc9.service.EscrowWithdrawService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.PayLogService;
import com.hc9.service.RechargeInfoService;

/***
 * 第三方担保提现
 * @author LKL
 */
@Controller
@RequestMapping(value = { "/escrowWithdraw" })
@CheckLogin(value=CheckLogin.ADMIN)
public class EscrowWithdrawController {
	
	@Resource
	private EscrowService  escrowService;
	
	@Resource
	private EscrowRechargeService escrowRechargeService;
	
	@Resource
	private EscrowWithdrawService escrowWithdrawService;
	
	@Resource
	private LoanSignQuery loanSignQuery;
	
	
	@Resource
	private BaoFuService baoFuService ;
	
    @Resource
    private PayLogService payLogService;
    
	List<NameValuePair> nvps ;
	
 	@RequestMapping(value = { "openEscrowWithdraw" })
	public ModelAndView escrow() {
		return new ModelAndView("WEB-INF/views/admin/escrow/escrowWithdrawRecord");
	}
 	
 	@RequestMapping("queryEscrowWithdraw")
 	public  String queryEscrowRechage(String id, HttpServletRequest request) {
    	Escrow escrow=escrowService.queryEscrowByid(Long.valueOf(id));
        request.setAttribute("escrow",escrow);
        return "/WEB-INF/views/admin/escrow/escrowWithdraw";
    }
 	
 	/***
 	 * 查询第三方担保提现记录
 	 * @param limit
 	 * @param start
 	 * @param escrowRecharge
 	 * @param request
 	 * @param page
 	 * @return
 	 */
	@ResponseBody
	@RequestMapping("escrowWithdrawList")
	public JSONObject escrowWithdrawList(String limit, String start,
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
		Adminuser adminuser = (Adminuser) request.getSession() .getAttribute(Constant.ADMINLOGIN_SUCCESS);
		// 分页数据源
		List datalist = escrowWithdrawService.escrowWithdrawPage(page, escrow, adminuser);
		String titles = "id,name,staff_username,amount,withdrawAmount,remark,time,pIpsBillNo,merfee,feetakenon,state,applytime,fee";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}
 	
 	/***
 	 * 第三方担保提现处理
 	 * @param request
 	 * @param amount
 	 * @param additional_info
 	 * @return
 	 */
	@RequestMapping("ipsEscrowWithdraw.htm")
 	public  String ipsEscrowWithdraw(HttpServletRequest request, Long id,Double amount,String additional_info){
 		Escrow  escrow=escrowService.queryEscrowByid(id);
 		if(escrow==null){
 			return "1"; //判断是否存在第三方担保
 		}
 		if(escrow.getStaffMoney()==0){
 			return "2";   //账户余额不足
 		}
 		Costratio costratio = loanSignQuery.queryCostratio();
		String ordernum = "TX" +  StringUtil.pMerBillNo();
		EscrowWithdraw escrowWithdraw=new EscrowWithdraw();
		 Double fee=costratio.getEscrowWithdraw();
		 escrowWithdraw.setpIpsBillNo(ordernum);
		 escrowWithdraw.setRemark(additional_info);
		 escrowWithdraw.setAmount(amount);
		 escrowWithdraw.setEscrow(escrow);
		 escrowWithdraw.setMer_fee(fee);
		 escrowWithdraw.setState(Constant.STATUES_ZERO);
		 escrowWithdraw.setFee_taken_on(costratio.getEscrowWithdrawMethod());
		 escrowWithdraw.setApplytime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		 escrowWithdrawService.saveEscrowWithdraw(escrowWithdraw);
		 
		WithdrawalInfo withrawlInfo = new WithdrawalInfo();
		withrawlInfo.setOrder_id(ordernum);
		withrawlInfo.setUser_id(escrow.getStaffBaofu());
		withrawlInfo.setAmount(amount);
		withrawlInfo.setFee(fee);
		withrawlInfo.setFee_taken_on(costratio.getEscrowWithdrawMethod().toString());
		withrawlInfo.setMerchant_id(ParameterIps.getCert());
		withrawlInfo.setReturn_url(Constant.ASYNESCROWWITHDRAW);
		withrawlInfo.setPage_url( Constant.ESCROWWITHDRAWAL);
		
		Map<String, String> map = null;
		try {
			String withrawXml = ParseXML.withdrawalXml(withrawlInfo);
			payLogService.savePayLog(withrawXml, escrow.getId(), 19, ordernum,0,fee,0.00,amount);
			map = RechargeInfoService.escorwWithdrawCall(withrawXml, ParameterIps.getmerchantKey());
			map.put("url", PayURL.WITHDRAWALTESTURL);
			request.getSession().setAttribute("map", map);
			return "WEB-INF/views/central_news";
		} catch (Exception e) {
			e.printStackTrace();
			return "WEB-INF/views/failure";
		}
 	}
 	
	
	
	/***
	 * 第三方担保提现业务查询
	 * @param request
	 * @param wId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("ipsEscrowWithdrawNum.htm")
	public String ipsEscrowWithdrawNum(HttpServletRequest request, Long id){
	    EscrowWithdraw withdraw=escrowWithdrawService.queryEscrowWithdraw(id);
	    P2pQuery p2pQuery=new  P2pQuery(withdraw.getpIpsBillNo(), 6);
	    nvps = new ArrayList<NameValuePair>();
		    try{
			    	String withdrawXml = ParseXML.p2pQueryXml(p2pQuery);
			    	nvps.add(new BasicNameValuePair("requestParams", withdrawXml));
			    	nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(withdrawXml + "~|~" + ParameterIps.getmerchantKey())));
			    	String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL, nvps);
			    	System.out.println("第三方担保提现业务查询="+result);
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
		                        state = elementOrder.elementTextTrim("state");
		                        String succ_amount=elementOrder.elementTextTrim("succ_amount");
		                        String succ_time=elementOrder.elementTextTrim("succ_time");
		                        String fee=elementOrder.elementTextTrim("fee");
		                        String baofoo_fee=elementOrder.elementTextTrim("baofoo_fee");
		                        String fee_taken_on=elementOrder.elementTextTrim("fee_taken_on");
		                        String Md5sign = CommonUtil.MD5(code+ "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
		        				if (sign.equals(Md5sign)) {
			        					if (code.equals("CSD000")) {
			        							withdraw.setFee(Double.valueOf(baofoo_fee)!=null?null:0.00);
			        							withdraw.setMer_fee(Double.valueOf(fee)!=null?null:0.00);
			        							if(succ_amount==null){
			        								succ_amount="0";
			        							}
			        							withdraw.setWithdrawAmount(Double.valueOf(succ_amount)!=null?null:0.00);
			        							withdraw.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
			        							if(state.equals("1")){   //转账成功
			        								withdraw.setState(Constant.STATUES_ONE);
			        							}else if(state.equals("-1")){  //-1转账失败
			        								withdraw.setState(-1);
			        							}else if(state.equals("5")){  //转账处理中
			        								withdraw.setState(Constant.STATUES_FIVE);
			        							}else if(state.equals("0")){  //初始化
			        								withdraw.setState(Constant.STATUES_ZERO);
			        							}
			        							withdraw.setFee_taken_on(Integer.valueOf(fee_taken_on));
			        							
			        							if(state.equals("1")){
			        								// 余额查询
				        							crs cr = baoFuService.getCasbalance(withdraw.getEscrow().getStaffBaofu());
				        							withdraw.getEscrow().setStaffMoney(cr.getBalance());
					        						// 流水账
													EscrowAccountinfo account = new EscrowAccountinfo();
													account.setExpenditure(Double.valueOf(succ_amount));
													account.setExplan("第三方担保提现");
													account.setIncome(0.00);
													account.setIpsNumber(order_id);
													account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
													account.setEscrow(withdraw.getEscrow());
													account.setMoney(cr.getBalance());
													escrowRechargeService.saveEscrowAccountinfo(account);  // 保存流水账余额
													escrowService.udapteEscrow(withdraw.getEscrow());
													Judge=true;
			        							}
			        							escrowWithdrawService.uptEscrowWithdraw(withdraw);
			        							// 更新支付报文信息
			        							payLogService.updatePayLog(order_id,Integer.valueOf(state),withdraw.getWithdrawAmount(), withdraw.getFee());
			        							LOG.error("宝付支付第三方担保提现查询处理成功");
			        					}else{
			        						LOG.error("宝付第三方担保提现查询处理失败--" + result+ "----->订单号----->" + withdraw.getpIpsBillNo());
			        						return "-1";
			        					}
		        				}else{
		        					LOG.error("非宝付第三方担保提现查询返回数据--" +result+ "----->订单号----->"+ withdraw.getpIpsBillNo());
		        					return "-1";
		        				}
		                 }
		            }
	                if(!Judge&&state.equals("-1")){
		                	if(withdraw.getState()!=-1){
			                		withdraw.setFee(0.00);
				                	withdraw.setState(-1);
				                	withdraw.setMer_fee(0.00);
				                	withdraw.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
				                	// 余额查询
        							crs cr = baoFuService.getCasbalance(withdraw.getEscrow().getStaffBaofu());
        							withdraw.getEscrow().setStaffMoney(cr.getBalance());
        							escrowService.udapteEscrow(withdraw.getEscrow());
				                	escrowWithdrawService.uptEscrowWithdraw(withdraw);
				                	// 更新支付报文信息
									payLogService.updatePayLog(withdraw.getpIpsBillNo(),Integer.valueOf(state),withdraw.getWithdrawAmount(), withdraw.getFee());
		                	}
	                }
	                return "1";
		    }catch (Exception e) {
				   e.printStackTrace();
				  LOG.error("宝付第三方担保提现查询失败----->订单号----->" + withdraw.getpIpsBillNo());
				 return "-1";
			 }
	}
	
	
 	

}
