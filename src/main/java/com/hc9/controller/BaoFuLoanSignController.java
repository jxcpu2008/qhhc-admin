package com.hc9.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.annotation.CheckLoginOnMethod;
import com.hc9.common.constant.Constant;
import com.hc9.common.constant.PayURL;
import com.hc9.common.log.LOG;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Generalize;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Paylog;
import com.hc9.dao.entity.Recharge;
import com.hc9.dao.entity.RegBonus;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Withdraw;
import com.hc9.model.AcctTrans;
import com.hc9.model.P2pQuery;
import com.hc9.model.crs;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.BaoFuService;
import com.hc9.service.BaseLoansignService;
import com.hc9.service.BonusService;
import com.hc9.service.ExpenseRatioService;
import com.hc9.service.GeneralizeMoneyServices;
import com.hc9.service.GeneralizeService;
import com.hc9.service.InterestIncreaseCardService;
import com.hc9.service.LoanInfoService;
import com.hc9.service.LoanManageService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.LoanSignRepayMentRecordService;
import com.hc9.service.LoanSignService;
import com.hc9.service.LoanrecordService;
import com.hc9.service.PayLogService;
import com.hc9.service.PlankService;
import com.hc9.service.RechargeInfoService;
import com.hc9.service.RepayMentServices;
import com.hc9.service.ShareLoanRepayMentService;
import com.hc9.service.SmsService;
import com.hc9.service.UserInfoServices;
import com.hc9.service.UserbasicsinfoService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Controller
@RequestMapping("/baoFuLoanSign")
@CheckLogin(value=CheckLogin.ADMIN)
public class BaoFuLoanSignController {
	
	@Resource
	private LoanSignQuery loanSignQuery;
	
	@Resource
	private PlankService plankService;
	
	@Resource
	private PayLogService payLogService;
	
	@Resource
	private LoanSignService loanSignService;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	private BaoFuService baoFuService;
	
	@Resource
	private UserInfoServices userInfoServices;
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	
	@Resource
	private LoanrecordService loanrecordService;
	
	@Resource
	private GeneralizeMoneyServices generalizeMoneyServices;
	
	@Resource
	private GeneralizeService generalizeService;
	
	@Resource
	private BonusService bonusService;
	
	@Resource
	private SmsService smsService;
	
	@Resource
	private ExpenseRatioService expenseRatioService;
	
	@Resource
	private LoanSignQuery loansignQuery;
	
	
	@Resource
	private ShareLoanRepayMentService shareLoanRepayMentService;
	
	@Resource
	private RepayMentServices repayMentServices;
	
	@Resource
	private BaseLoansignService baseLoansignService;
	
	@Resource
	private  LoanManageService loanManageService;
	
	@Resource
	private LoanSignRepayMentRecordService repayMentRecordService;
	
	@Resource
	private LoanInfoService loanInfoService;
	
	@Resource
	private InterestIncreaseCardService interestIncreaseCardService;
	
	List<NameValuePair> nvps;
	
	/***
	 * 收取平台服务费单个处理方法
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("isCompanyFee.htm")
	public String isCompanyFee(HttpServletRequest request,HttpServletResponse response){
		Double companyFee=40000.00;
		Loansign loansign=loanSignQuery.getLoansign("209");
		// 获取后台操作人员信息
		String orderNum = "FW" + StringUtil.getDateTime(loansign.getUserbasicsinfo().getId(), loansign.getId());// 收取平台服务费转账订单号
		AcctTrans acctTran = new AcctTrans();
		acctTran.setMerchant_id(ParameterIps.getCert());
		acctTran.setOrder_id(orderNum);
		acctTran.setPayer_user_id(loansign.getUserbasicsinfo().getpMerBillNo());
		acctTran.setPayee_user_id(ParameterIps.getCert());// 收款
		acctTran.setPayer_type(0);
		acctTran.setPayee_type(1);// 收款
		acctTran.setAmount(companyFee);
		acctTran.setFee(0.00);
		acctTran.setFee_taken_on(1);
		acctTran.setReq_time(new Date().getTime());
		try {
			String registerXml = ParseXML.accttrans(acctTran);
			ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("requestParams",registerXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml + "~|~"+ ParameterIps.getmerchantKey())));
			payLogService.savePayLog(registerXml, loansign.getUserbasicsinfo().getId(),loansign.getId(), 21, orderNum, 0.00, 0.00,companyFee);
			String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
			result = result.replace("\"", "\'");
			LOG.error("平台收取服务费处理返回报文："+result);
			crs cr = new crs();
			XStream xss = new XStream(new DomDriver());
			xss.alias(cr.getClass().getSimpleName(), cr.getClass());
			cr = (crs) xss.fromXML(result);
			String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~"+ cr.getMsg() + "~|~"+ ParameterIps.getDes_algorithm());
			if (cr.getSign().equals(Md5sign)) {
				if (cr.getCode().equals("CSD000")) {
						
						Accountinfo accountUser = new Accountinfo();
					    accountUser.setExpenditure(companyFee);
					    accountUser.setExplan("平台收取服务费");
						accountUser.setIncome(0.00);
						accountUser.setIpsNumber(orderNum);
						accountUser.setLoansignId(String.valueOf(loansign.getId()));// 标id（项目id）
						accountUser.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
						accountUser.setAccounttype(plankService.accounttype(17L));
						accountUser.setUserbasicsinfo(loansign.getUserbasicsinfo());
						plankService.saveAccount(accountUser);// 添加流水账余额
						loansign.setFeeState(Constant.STATUES_ONE);  //收取状态
						loansign.setFee(companyFee);
						loansign.setAdminFee(Long.valueOf(10));   //操作Id
						loanSignService.updateLoangn(loansign);
					    System.out.println("收取平台服务费="+companyFee);
						payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("平台收取服务费报错");
		} 
		return "WEB-INF/views/success";
		
	}
	
	/***
	 * 退款单个操作
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("ipsTranCert.htm")
	public String ipsTranCert(HttpServletRequest request,
			HttpServletResponse response) {
		Double money=500.00;
		Userbasicsinfo userbasicsinfo = userbasicsinfoService.queryUserById(Long.valueOf(202));
		String orderNum = "TK" + userbasicsinfo.getId()+"_"+new Date().getTime();// 佣金转账订单号
		AcctTrans acctTran = new AcctTrans();
		acctTran.setMerchant_id(ParameterIps.getCert());
		acctTran.setOrder_id(orderNum);
		acctTran.setPayer_user_id(ParameterIps.getCert());
		acctTran.setPayee_user_id(userbasicsinfo.getpMerBillNo());// 收款
		acctTran.setPayer_type(1);
		acctTran.setPayee_type(0);// 收款
		acctTran.setAmount(money);
		acctTran.setFee(0.00);
		acctTran.setFee_taken_on(1);
		acctTran.setReq_time(new Date().getTime());
		try {
			String registerXml = ParseXML.accttrans(acctTran);
			ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("requestParams", registerXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml+ "~|~" + ParameterIps.getmerchantKey())));
			payLogService.savePayLog(registerXml, userbasicsinfo.getId(),userbasicsinfo.getId(), 25, orderNum, 0.00, 0.00, money);
			String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
			result = result.replace("\"", "\'");
			crs cr = new crs();
			XStream xss = new XStream(new DomDriver());
			xss.alias(cr.getClass().getSimpleName(), cr.getClass());
			cr = (crs) xss.fromXML(result);
			String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~" + cr.getMsg()+ "~|~" + ParameterIps.getDes_algorithm());
			if (cr.getSign().equals(Md5sign)) {
				if (cr.getCode().equals("CSD000")) {
					// 流水账
					Accountinfo account = new Accountinfo();
					account.setExpenditure(0.00);
					account.setExplan("退款");
					account.setIncome(money);
					account.setIpsNumber(orderNum);
					account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
					account.setUserbasicsinfo(userbasicsinfo);
					account.setAccounttype(plankService.accounttype(19L));
					account.setFee(0.00);
					// 余额查询
					crs crs = baoFuService.getCasbalance(userbasicsinfo.getpMerBillNo());
					userbasicsinfo.getUserfundinfo().setCashBalance(crs.getBalance());
					userbasicsinfo.getUserfundinfo().setOperationMoney(crs.getBalance());
					account.setMoney(cr.getBalance());
					userbasicsinfoService.update(userbasicsinfo);
					plankService.saveAccount(account);// 保存流水账余额
					payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "WEB-INF/views/success";
	}
	
	
	/***
	 * 还款单个操作平台转出
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("ipsTranCertRepayMent.htm")
	public String ipsTranCertRepayMent(HttpServletRequest request, HttpServletResponse response,String uid,String monney) {
		Double money=Double.parseDouble(monney);
		Userbasicsinfo userbasicsinfo = userbasicsinfoService.queryUserById(Long.valueOf(uid));
		String orderNum = "HK" +"42_"+ userbasicsinfo.getId()+"_"+new Date().getTime();// 佣金转账订单号
		AcctTrans acctTran = new AcctTrans();
		acctTran.setMerchant_id(ParameterIps.getCert());
		acctTran.setOrder_id(orderNum);
		acctTran.setPayer_user_id(ParameterIps.getCert());
		acctTran.setPayee_user_id(userbasicsinfo.getpMerBillNo());// 收款
		acctTran.setPayer_type(1);
		acctTran.setPayee_type(0);// 收款
		acctTran.setAmount(money);
		acctTran.setFee(0.00);
		acctTran.setFee_taken_on(1);
		acctTran.setReq_time(new Date().getTime());
		try {
			String registerXml = ParseXML.accttrans(acctTran);
			ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("requestParams", registerXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml+ "~|~" + ParameterIps.getmerchantKey())));
			payLogService.savePayLog(registerXml, userbasicsinfo.getId(),userbasicsinfo.getId(), 7, orderNum, 0.00, 0.00, money);
			String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
			result = result.replace("\"", "\'");
			crs cr = new crs();
			XStream xss = new XStream(new DomDriver());
			xss.alias(cr.getClass().getSimpleName(), cr.getClass());
			cr = (crs) xss.fromXML(result);
			String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~" + cr.getMsg()+ "~|~" + ParameterIps.getDes_algorithm());
			if (cr.getSign().equals(Md5sign)) {
				if (cr.getCode().equals("CSD000")) {
					// 流水账
					Accountinfo account = new Accountinfo();
					account.setExpenditure(0.00);
					account.setExplan("还款");
					account.setIncome(money);
					account.setIpsNumber(orderNum);
					account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
					account.setUserbasicsinfo(userbasicsinfo);
					account.setAccounttype(plankService.accounttype(4L));
					account.setFee(0.00);
					// 余额查询
					crs crs = baoFuService.getCasbalance(userbasicsinfo.getpMerBillNo());
					userbasicsinfo.getUserfundinfo().setCashBalance(crs.getBalance());
					userbasicsinfo.getUserfundinfo().setOperationMoney(crs.getBalance());
					account.setMoney(cr.getBalance());
					userbasicsinfoService.update(userbasicsinfo);
					plankService.saveAccount(account);// 保存流水账余额
					payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "WEB-INF/views/success";
	}
	
	
	/***
	 * 还款单个操作平台转入
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("ipsTranCertRepayInTo.htm")
	public String ipsTranCertRepayOut(HttpServletRequest request, HttpServletResponse response,String uid,String monney) {
		Double money=Double.parseDouble(monney);
		Userbasicsinfo userbasicsinfo = userbasicsinfoService.queryUserById(Long.valueOf(uid));
		String orderNum = "tk" +"42_"+ userbasicsinfo.getId()+"_"+new Date().getTime();// 佣金转账订单号
		AcctTrans acctTran = new AcctTrans();
		acctTran.setMerchant_id(ParameterIps.getCert());
		acctTran.setOrder_id(orderNum);
		acctTran.setPayer_user_id(userbasicsinfo.getpMerBillNo());
		acctTran.setPayee_user_id(ParameterIps.getCert());// 收款
		acctTran.setPayer_type(0);
		acctTran.setPayee_type(1);// 收款
		acctTran.setAmount(money);
		acctTran.setFee(0.00);
		acctTran.setFee_taken_on(1);
		acctTran.setReq_time(new Date().getTime());
		try {
			String registerXml = ParseXML.accttrans(acctTran);
			ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("requestParams", registerXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml+ "~|~" + ParameterIps.getmerchantKey())));
			payLogService.savePayLog(registerXml, userbasicsinfo.getId(),userbasicsinfo.getId(), 25, orderNum, 0.00, 0.00, money);
			String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
			result = result.replace("\"", "\'");
			crs cr = new crs();
			XStream xss = new XStream(new DomDriver());
			xss.alias(cr.getClass().getSimpleName(), cr.getClass());
			cr = (crs) xss.fromXML(result);
			String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~" + cr.getMsg()+ "~|~" + ParameterIps.getDes_algorithm());
			if (cr.getSign().equals(Md5sign)) {
				if (cr.getCode().equals("CSD000")) {
					// 流水账
					Accountinfo account = new Accountinfo();
					account.setExpenditure(money);
					account.setExplan("转账(退款)");
					account.setIncome(0.00);
					account.setIpsNumber(orderNum);
					account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
					account.setUserbasicsinfo(userbasicsinfo);
					account.setAccounttype(plankService.accounttype(19L));
					account.setFee(0.00);
					// 余额查询
					crs crs = baoFuService.getCasbalance(userbasicsinfo.getpMerBillNo());
					userbasicsinfo.getUserfundinfo().setCashBalance(crs.getBalance());
					userbasicsinfo.getUserfundinfo().setOperationMoney(crs.getBalance());
					account.setMoney(cr.getBalance());
					userbasicsinfoService.update(userbasicsinfo);
					plankService.saveAccount(account);// 保存流水账余额
					payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "WEB-INF/views/success";
	}
	
	
	
	/***
	 * 循环投标
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
//	@RequestMapping("testLolly.htm")
	public String testLolly(HttpServletRequest request,HttpServletResponse response,Long id) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		user = userInfoServices.queryBasicsInfoById(user.getId().toString());
		int i = 0;
		while (i < 30) {
			baoFuLoansignService.ipsGetLoanInfoUser(user,Long.valueOf(299), 100.00, 1, request, response);
			i++;
		}
		i = 0;
		while (i <15) {
			baoFuLoansignService.ipsGetLoanInfoUser(user,Long.valueOf(299), 100.00, 2, request, response);
			i++;
		}
		i = 0;
		while (i < 5) {
			baoFuLoansignService.ipsGetLoanInfoUser(user,Long.valueOf(299), 100.00, 3, request,response);
			i++;
		}
		return "WEB-INF/views/success";
	}
	
	/***
	 * 继续投标
	 * 
	 * @param request
	 * @param response
	 * @param orderNum
	 * @return
	 */
//	@RequestMapping("ipsContinueToPay.htm")
	@ResponseBody
	@CheckLoginOnMethod
	public String ipsContinueToPay(HttpServletRequest request,HttpServletResponse response, String orderNum) {
		// 获取投标信息
		Loanrecord loanRecordNum = loanrecordService.getLoanRecordOrderNum(orderNum);
		// 获取当前用户
		Userbasicsinfo userbasicsinfo = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
		Userbasicsinfo user = userInfoServices.queryBasicsInfoById(userbasicsinfo.getId().toString());
		// 判断当前用户个人资金账户>购买金额
		if (user.getUserfundinfo().getCashBalance() < loanRecordNum.getTenderMoney()) {
			return "1";
		}
		Paylog payLog = payLogService.queryPaylogByOrderSn(orderNum);
		// 添加项目的信息
		Map<String, String> map = new HashMap<String, String>();
		try {
			map = RechargeInfoService.loanBidCall(payLog.getMessage(),ParameterIps.getmerchantKey(), orderNum);
			map.put("url", PayURL.BIDTESTURL);
			request.getSession().setAttribute("map", map);
			return "3"; // 处理成功
		} catch (Exception e) {
			e.printStackTrace();
			return "4"; // 处理失败
		}
	}
	
	/**
	 * 转账查询
	 * @return
	 */
	@ResponseBody
	@RequestMapping("TransferState.htm")
	public String TransferState(String orderid) {
 		P2pQuery p2pQuery = new P2pQuery(orderid, 7);
		nvps = new ArrayList<NameValuePair>();
		try {
			String  transferXml = ParseXML.p2pQueryXml(p2pQuery);
			nvps.add(new BasicNameValuePair("requestParams", transferXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(transferXml+ "~|~" + ParameterIps.getmerchantKey())));
			String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL,nvps);
			System.out.println("返回信息" + result);
			Document doc = DocumentHelper.parseText(result);
			Element rootElt = doc.getRootElement(); // 获取根节点
			System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
			// 拿到crs节点下的子节点code值
			String code = rootElt.elementTextTrim("code");
			String msg = rootElt.elementTextTrim("msg");
			String sign = rootElt.elementTextTrim("sign");
			// 获取子节点crs下的子节点result
			Iterator iteratorResult = rootElt.elementIterator("result");
			boolean Judge = false; // 判断是否有值
			String state = "0"; // 0-失败 1-成功
			while (iteratorResult.hasNext()) {
				Element itemEle = (Element) iteratorResult.next();
				Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
				while (iteratorOrder.hasNext()) {
					Element elementOrder = (Element) iteratorOrder.next();
					state = elementOrder.elementTextTrim("state");
					String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
					if (sign.equals(Md5sign)) {
						if (code.equals("CSD000")) {
							Judge = true;
							if (state.equals("1")) {
								return "CSD000";
							}
						} else if (code.equals("CSD333")) {
							return "CSD333";
						} else {
							LOG.error("查询处理失败--" + result + "----->订单号----->");
							return "CSD222";
						}
					} else {
						LOG.error("查询返回数据--" + result + "----->订单号----->");
						return "CSD222";
					}
				}
			}
			if (!Judge && state.equals("0")) {
				return "CSD333";
			}
			return "CSD222";
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("查询失败----->订单号----->");
			return "CSD222";
		}
	}
	
	/***
	 * 提现循环查询操作
	 * @param request
	 * @return
	 */
	@ResponseBody
//	@RequestMapping("saveWithdraw")
	public String saveWithdraw(HttpServletRequest request){
		List<Withdraw> list=baoFuLoansignService.getListWithdraw();
		for (int i = 0; i < list.size(); i++) {
			Withdraw withdraw=list.get(i);
			baoFuLoansignService.returnWithdrawNumService(request, withdraw.getId().toString());
		}
		return "success";
	}
	
	/***
	 * 充值循环查询
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
//	@RequestMapping("saveRecharge")
	public String saveRecharge(HttpServletRequest request) throws Exception{
		List<Recharge> list=baoFuLoansignService.getListRecharge();
		for (int i = 0; i < list.size(); i++) {
			Recharge recharge=list.get(i);
			baoFuLoansignService.ipsCustomerRechargeNum(request, recharge.getId().toString());
		}
		return "success";
	}

	/***
	 * 根据标号生成佣金数据
	 * @param request
	 * @param loanId
	 * @return
	 */
	@ResponseBody
//	@RequestMapping("saveGeneralizemoney")
	public String saveGeneralizemoney(HttpServletRequest request,Long loanId){
		// 获取费用表的信息
		Costratio costratio = loanSignQuery.queryCostratio();
		List<Loanrecord>list =baoFuLoansignService.getLoanRecordList(loanId);
		for (int i = 0; i < list.size(); i++) {
			Loanrecord loanrecord=list.get(i);
			if(costratio.getGeneralizeState()==1){
			     generalizeService.saveGeneralizeMoney(loanrecord);
			}else{
					Userbasicsinfo userGen = generalizeService.queryPromoterByPromotedId(loanrecord.getUserbasicsinfo().getId());
					if(userGen!=null){
						 if (userGen.getUserType() == 2) {
								generalizeMoneyServices.saveGeneralizemoney(loanrecord,costratio.getBusiness(), userGen.getId(),Constant.STATUES_TWO);
							}
					}
					if(loanrecord.getUserbasicsinfo().getUserType()==2){
						  generalizeMoneyServices.saveGeneralizemoney(loanrecord,costratio.getBusiness(), loanrecord.getUserbasicsinfo().getId(),Constant.STATUES_TWO);
					}
        	}
		}
		return "success";
	}
	
	/***
	 * 批量生成注册奖励数据
	 * @param request
	 * @param loanId
	 * @return
	 */
	@ResponseBody
//	@RequestMapping("saveRegBonus")
	public String saveRegBonus(HttpServletRequest request,Long loanId){
		List<Generalize> listGenList=baoFuLoansignService.getGeneralizeList(loanId);
		for (int i = 0; i < listGenList.size(); i++) {
			Generalize genUser=listGenList.get(i);
			Generalize generalize=bonusService.queryGeneralize(genUser.getByUser().getId());
			if(generalize!=null){
				Userbasicsinfo referrer=userbasicsinfoService.queryUserById(generalize.getGenuid());
				RegBonus regBonus = new RegBonus();
				regBonus.setUserbasicsinfo(genUser.getByUser());
				regBonus.setReferrer(referrer);
				regBonus.setReleaseStatus(0);
				Costratio cos = expenseRatioService.findCostratio();
				regBonus.setBouns(null != cos.getRegBonu()
						&& !"".equals(cos.getRegBonu()) ? cos.getRegBonu() : 0);
				bonusService.saveReg(regBonus); // 保存信息
			}
		}
		return "success";
	}
	
	/***
	 * 红包转账
	 * @param request
	 * @param loanId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("ipsTranCertHBZZ.htm")
	public String ipsTranCertHBZZ(HttpServletRequest request,String loanId){
		Loansign loan=loansignQuery.getLoansignById(loanId);
		if(loan.getRedEnvelopeMoney()>0){
			if(loansignQuery.queryRed(loan)){
				baoFuLoansignService.ipsTranCertHBZZ(loan);
			}else{
				return "2";
			}
		}else{
			return "3";
		}
		return "1";
	}
	
	/***
	 * 去掉宝付
	 * @param repayId
	 * @param request
	 * @return
	 */
	@ResponseBody
//	@RequestMapping("shareLoanCopy.htm")
	  public  String shareLoanRepayMentCopy(Long repayId,  HttpServletRequest request){
		// 获取后台操作人员信息
	  	Adminuser admin = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
	  	if(admin.getId()!=2){
	  		return "5";
	  	}else{
	  		return shareLoanRepayMentService.shareLoanRepayMentCopy(repayId);
	  	}
	  }
	
	/***
	 * 查询还款状态
	 * @param LoanId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("shareBonusState.htm")
	public  String shareBonusState(Long repayId,  HttpServletRequest request){
		//得到还款期数信息；
	 	Repaymentrecord repaymentrecord = baseLoansignService .getRepaymentId(repayId);
		Paylog paylog=payLogService.queryPaylogLoan(repaymentrecord.getLoansign().getId(), 7,repaymentrecord.getId());
		if(paylog==null){
			return "1";
		}else{
			String resultStr = repayMentServices.shareBonusState(paylog.getOrderSn(),
					repaymentrecord.getLoansign(),repaymentrecord.getLoansign().getUserbasicsinfo());
			Long loanId = null;
			if(repaymentrecord != null && repaymentrecord.getLoansign() != null) {
				loanId = repaymentrecord.getLoansign().getId();
			}
			repayMentRecordService.updateUserRedisDataAfterRepayMent(loanId, repayId);
			loanInfoService.getLoanLoandynamic();
			return resultStr;
		}
	}
	
	/***
	 * 查询佣金转账
	 * @param orderNum
	 * @param loanId
	 * @param userId
	 * @param money
	 * @return
	 */
	@ResponseBody
	@RequestMapping("transBonusesState.htm")
	public  boolean transBonusesState(String orderNum,String loanId,Long userId,Double money){
		return baoFuLoansignService.transBonusesState(orderNum, loanId, userId, money,26);
	}
}
