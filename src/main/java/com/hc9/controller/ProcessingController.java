package com.hc9.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hc9.common.constant.Constant;
import com.hc9.common.constant.IntegralType;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.log.LOG;
import com.hc9.common.redis.HcPeachActivitiCache;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.Arith;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.DateUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Escrow;
import com.hc9.dao.entity.EscrowAccountinfo;
import com.hc9.dao.entity.EscrowRecharge;
import com.hc9.dao.entity.EscrowWithdraw;
import com.hc9.dao.entity.Fhrecord;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Paylog;
import com.hc9.dao.entity.Recharge;
import com.hc9.dao.entity.Shop;
import com.hc9.dao.entity.ShopRecord;
import com.hc9.dao.entity.ShopRewardOption;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Withdraw;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.ReturnInfo;
import com.hc9.model.crs;
import com.hc9.service.AdminService;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.BaoFuService;
import com.hc9.service.BaseLoansignService;
import com.hc9.service.BonusService;
import com.hc9.service.BorrowerFundService;
import com.hc9.service.EscrowRechargeService;
import com.hc9.service.EscrowService;
import com.hc9.service.EscrowWithdrawService;
import com.hc9.service.ExpenseRatioService;
import com.hc9.service.GeneralizeMoneyServices;
import com.hc9.service.GeneralizeService;
import com.hc9.service.HccoindetailService;
import com.hc9.service.IMessagePushManageService;
import com.hc9.service.IntegralSevice;
import com.hc9.service.InterestIncreaseCardService;
import com.hc9.service.LoanInfoService;
import com.hc9.service.LoanManageService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.LoanrecordService;
import com.hc9.service.LotteryService;
import com.hc9.service.PayLogService;
import com.hc9.service.PlankService;
import com.hc9.service.ProcessingService;
import com.hc9.service.RedEnvelopeDetailService;
import com.hc9.service.RegistrationService;
import com.hc9.service.ShopManageService;
import com.hc9.service.ShopService;
import com.hc9.service.SmsService;
import com.hc9.service.UserBaseInfoService;
import com.hc9.service.UserInfoServices;
import com.hc9.service.UserbasicsinfoService;
import com.hc9.service.WithdrawServices;
import com.hc9.service.sms.ym.BaseSmsService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import freemarker.template.TemplateException;

/**
 * 处理宝付返回的信息
 * 
 * @author frank 2015-01-2
 * 
 */
@Controller
@RequestMapping("/processing")
public class ProcessingController {
	
	private static final Logger logger = Logger.getLogger(ProcessingController.class);

	@Resource
	private HibernateSupport dao;

	@Resource
	private ProcessingService processingService;

	@Resource
	private UserInfoServices userInfoServices;
	
	@Resource
	private BonusService bonusService;

	@Resource
	private AdminService adminService;

	@Resource
	private WithdrawServices withdrawServices;

	@Resource
	private BaseLoansignService baseLoansignService;

	@Resource
	private LoanManageService loanManageService;

	@Resource
	private BorrowerFundService borrowerFundService;

	@Resource
	private LoanSignQuery loanSignQuery;

	@Resource
	private PlankService plankService;

	@Resource
	private UserInfoQuery infoQuery;

	@Resource
	private LoanInfoService infoService;

	@Resource
	private UserInfoQuery userInfoQuery;

	@Resource
	private UserInfoServices infoServices;

	@Resource
	private UserBaseInfoService userBaseInfoService;

	@Resource
	private ShopService shopService;

	@Resource
	private LoanrecordService loanrecordService;
	
	@Resource
	private SmsService smsService;
	
	@Resource
	private EscrowRechargeService escrowRechargeService;
	
	@Resource
	private EscrowWithdrawService  escrowWithdrawService;

	/**
	 * 注入MyindexService
	 */
	@Resource
	private UserbasicsinfoService userbasicsinfoService;

	@Resource
	private ShopManageService shopManageService;

	@Resource
	private BaoFuService baoFuService;

	@Resource
	private PayLogService payLogService;
	@Resource
	private IntegralSevice integralSevice;
	@Resource
	private EscrowService escrowService;
	
	@Resource
	private LotteryService lotteryService;

	@Resource
	private GeneralizeService generalizeService;
	
	@Resource
	private GeneralizeMoneyServices generalizeMoneyServices;
	
	@Resource
	private 	BaseSmsService baseSmsService;
	
	@Resource
	private ExpenseRatioService expenseRatioService;

	@Resource
	private RedEnvelopeDetailService redEnvelopeDetailService;

	@Resource
	private RegistrationService registrationService;

	List<NameValuePair> nvps;
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	
	@Resource
	private HccoindetailService hccoindetailService;
	
	@Resource
	private InterestIncreaseCardService increaseCardService;
	
	@Autowired
	private IMessagePushManageService messagePushService;
	
	/**
	 * 用户注册信息处理
	 * 
	 * @param pMerCode
	 *            平台账号
	 * @param pErrCode
	 *            充值状态(0000成功，9999失败)
	 * @param pErrMsg
	 *            返回信息
	 * @param p3DesXmlPara
	 *            3des加密报文
	 * @param pSign
	 *            返回报文
	 * @param request
	 *            request
	 * @return 返回页面路径
	 * 
	 */
	@RequestMapping("registration.htm")
	public String registration(ReturnInfo info, HttpServletRequest request) {

		return "WEB-INF/views/success";
	}

	/**
	 * 注册异步处理
	 * 
	 * @param returnInfo
	 *            返回信息
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping("asynchronismRegistration.htm")
	public String asynchronismRegistration(ReturnInfo info, @RequestParam("param") Long userid
			, HttpServletRequest request)
			throws IOException, TemplateException {
		if (info != null) {
				crs resignc = new crs();
				XStream xs = new XStream(new DomDriver());
				xs.alias(resignc.getClass().getSimpleName(), resignc.getClass());
				resignc = (crs) xs.fromXML(info.getResult());
				String Md5sign = CommonUtil.getMd5sign(info.getResult());
				// 校验是否宝付回传数据
				if (info.getSign().equals(Md5sign)) {
					Userbasicsinfo user = userbasicsinfoService.queryUserById(userid);
					if (resignc.getCode().equals("CSD000")) {
							try{
								if(generalizeService.getGeneralizeIsAuthIps(user.getId())){
									user.setUserType(Constant.STATUES_SIX);
								}
								// 余额查询
								/*crs cr = baoFuService.getCasbalance(String.valueOf(resignc.getUser_id()));
								user.getUserfundinfo().setCashBalance(cr.getBalance());*/
								user.setpIpsAcctDate(DateUtil.format("yyyy-MM-dd HH:mm:ss"));
								user.setAuthIpsTime(DateUtil.format("yyyy-MM-dd HH:mm:ss"));
								user.setIsAuthIps(1);//前端页面注册，确定为授权
								user.setHasIpsAccount(1);  //成功
								user.setCardStatus(2);
								user.setpMerBillNo(String.valueOf(resignc.getUser_id()));
								userbasicsinfoService.update(user);
								/** 宝付状态修改：更新session中相关对象 */
								request.getSession().setAttribute(Constant.SESSION_USER, user);
								// 更新支付报文信息
								LOG.error("宝付支付注册成功"+info.getResult()+"----");
								generalizeService.updateGeneralize(user);
								//通过被推广人id反查推广人
								Userbasicsinfo userGen = generalizeService.queryPromoterByPromotedId(user.getId());
								if(userGen!=null){
									//lkl-20150811-添加员工推荐注册增加红筹币
									if(userGen.getUserType() == 2){
										hccoindetailService.saveHccoindetailNumber(userGen);
									}
									if((userGen.getUserType() == 1 || userGen.getUserType() == 3) && userGen.getIsAuthIps() == 1){
										userGen.setUserType(Constant.STATUES_SIX);
										userbasicsinfoService.update(userGen);
									}
								}
								return "WEB-INF/views/success";
							} catch(Exception e) {
								e.printStackTrace();
								LOG.error("宝付注册处理成功---->平台数据处理失败---" + info.getResult());
								return "WEB-INF/views/failure";
							}
						}else if(resignc.getCode().equals("CSD333")){
							   payLogService.updatePayLog(resignc.getOrder_id(),-1,0.00,0.00);
							   LOG.error("宝付注册处理失败----->" + info.getResult());
							   return "WEB-INF/views/failure";
					} else {
							LOG.error("宝付注册处理失败--" + info.getResult());
							return "WEB-INF/views/failure";
						}
				} else {
					LOG.error("非宝付注册返回数据--" + info.getResult() );
					return "WEB-INF/views/failure";
				}
		} else {
			return "WEB-INF/views/failure";
		}
	}

	/**
	 * H5注册异步处理
	 * 
	 * @param returnInfo
	 *            返回信息
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping("h5asynchronismRegistration.htm")
	public String h5asynchronismRegistration(ReturnInfo info, @RequestParam("param") Long userid
			, HttpServletRequest request)
			throws IOException, TemplateException {
		if (info != null) {
				crs resignc = new crs();
				XStream xs = new XStream(new DomDriver());
				xs.alias(resignc.getClass().getSimpleName(), resignc.getClass());
				resignc = (crs) xs.fromXML(info.getResult());
				String Md5sign = CommonUtil.getMd5sign(info.getResult());
				// 校验是否宝付回传数据
				if (info.getSign().equals(Md5sign)) {
					Userbasicsinfo user = userbasicsinfoService.queryUserById(userid);
					if (resignc.getCode().equals("CSD000")) {
							try{
								if(generalizeService.getGeneralizeIsAuthIps(user.getId())){
									user.setUserType(Constant.STATUES_SIX);
								}
								// 余额查询
								/*crs cr = baoFuService.getCasbalance(String.valueOf(resignc.getUser_id()));
								user.getUserfundinfo().setCashBalance(cr.getBalance());*/
								user.setpIpsAcctDate(DateUtil.format("yyyy-MM-dd HH:mm:ss"));
								user.setAuthIpsTime(DateUtil.format("yyyy-MM-dd HH:mm:ss"));
								user.setIsAuthIps(1);//前端页面注册，确定为授权
								user.setHasIpsAccount(1);  //成功
								user.setCardStatus(2);
								user.setpMerBillNo(String.valueOf(resignc.getUser_id()));
								userbasicsinfoService.update(user);
								/** 宝付状态修改：更新session中相关对象 */
								request.getSession().setAttribute(Constant.SESSION_USER, user);
								// 更新支付报文信息
								LOG.error("宝付支付注册成功"+info.getResult()+"----");
								generalizeService.updateGeneralize(user);
								//通过被推广人id反查推广人
								Userbasicsinfo userGen = generalizeService.queryPromoterByPromotedId(user.getId());
								if(userGen!=null){
									//lkl-20150811-添加员工推荐注册增加红筹币
									if(userGen.getUserType() == 2){
										hccoindetailService.saveHccoindetailNumber(userGen);
									}
									if((userGen.getUserType() == 1 || userGen.getUserType() == 3) && userGen.getIsAuthIps() == 1){
										userGen.setUserType(Constant.STATUES_SIX);
										userbasicsinfoService.update(userGen);
									}
								}
								return "redirect:/h5/baofooSuccess.htm";
							} catch(Exception e) {
								e.printStackTrace();
								LOG.error("宝付注册处理成功---->平台数据处理失败---" + info.getResult());
								return "redirect:/h5/baofooSuccess.htm";
							}
						}else if(resignc.getCode().equals("CSD333")){
							   payLogService.updatePayLog(resignc.getOrder_id(),-1,0.00,0.00);
							   LOG.error("宝付注册处理失败----->" + info.getResult());
								return "redirect:/h5/baofooSuccess.htm";
					} else {
							LOG.error("宝付注册处理失败--" + info.getResult());
							return "redirect:/h5/baofooSuccess.htm";
						}
				} else {
					LOG.error("非宝付注册返回数据--" + info.getResult() );
					return "redirect:/h5/baofooSuccess.htm";
				}
		} else {
			return "redirect:/h5/baofooSuccess.htm";
		}
	}

	/**
	 * 用户充值返回处理
	 * 
	 * @param pMerCode
	 *            平台账号
	 * @param pErrCode
	 *            充值状态(0000成功，9999失败)
	 * @param pErrMsg
	 *            返回信息
	 * @param p3DesXmlPara
	 *            3des加密报文
	 * @param pSign
	 *            返回报文
	 * @return 返回页面路径
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping("recharge.htm")
	public  String recharge(ReturnInfo info,
			HttpServletRequest request) {
		return "WEB-INF/views/success_recharge";

	}

	/**
	 * 宝付支付充值异步返回
	 * 
	 * @param returnInfo
	 * @param request
	 */
	@RequestMapping("asynchronismRecharge.htm")
	public  String asynchronismRecharge(ReturnInfo info,HttpServletRequest request) throws IOException, TemplateException {
		if (info != null) {
			crs resignc = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(resignc.getClass().getSimpleName(), resignc.getClass());
			resignc = (crs) xs.fromXML(info.getResult());
			String Md5sign = CommonUtil.getMd5sign(info.getResult());
			if (info.getSign().equals(Md5sign)) {
				// 保存充值记录
				Recharge charge = processingService.findRechargeByOrderId(resignc.getOrder_id());
				if (resignc.getCode().equals("CSD000")) {
					try {
						if (charge.getStatus() == 0) {
								charge.setFee(resignc.getFee()); // 宝付收取费用
								charge.setMer_fee(resignc.getMer_fee()); // 商户收取的手续费
								charge.setIncash_money(resignc.getIncash_money()); // 充值结算金额(实际到账)
								charge.setCode(resignc.getCode());
								charge.setStatus(Constant.STATUES_ONE);
								charge.setAdditional_info(resignc.getAdditional_info());
								charge.setSuccTime(resignc.getSucc_time());
								processingService.updaterecharge(charge);
								Userbasicsinfo user = userbasicsinfoService.queryUserById(charge.getUserbasicsinfo().getId());
								// 流水账
								Accountinfo account = new Accountinfo();
								account.setExpenditure(0.00);
								account.setExplan("充值");
								account.setIncome(resignc.getIncash_money());
								account.setIpsNumber(resignc.getOrder_id());
								account.setTime(resignc.getSucc_time());
								account.setUserbasicsinfo(user);
								account.setFee(charge.getFee());
								account.setAccounttype(plankService.accounttype(6L));
								// 余额查询
								crs cr = baoFuService.getCasbalance(user.getpMerBillNo());
								user.getUserfundinfo().setCashBalance(cr.getBalance());
								user.getUserfundinfo().setOperationMoney(cr.getBalance());
								account.setMoney(cr.getBalance());
								userbasicsinfoService.update(user);
								plankService.saveAccount(account);// 保存流水账余额
								// 更新支付报文信息
								payLogService.updatePayLog(resignc.getOrder_id(),Constant.STATUES_ONE,charge.getIncash_money(), charge.getFee());
								LOG.error("宝付支付充值成功");
						}
						return "WEB-INF/views/success_recharge";
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("宝付充值处理成功---->平台数据处理失败---" + info.getResult()
								+ "----->订单号----->" + resignc.getOrder_id());
						return "WEB-INF/views/failure";
					}
				} else if(resignc.getCode().equals("CSD333")){
						   charge.setStatus(-1);
						   charge.setFee(0.00);
						   processingService.updaterecharge(charge);
						   payLogService.updatePayLog(charge.getOrderNum(),-1,charge.getIncash_money(),charge.getFee());
						   return "WEB-INF/views/failure";
				}else{
					LOG.error("宝付充值处理失败--" + info.getResult()+ "----->订单号----->" + resignc.getOrder_id());
					return  "WEB-INF/views/failure";
				}
			} else {
				LOG.error("非宝付充值返回数据--" + info.getResult() + "----->订单号----->"
						+ resignc.getOrder_id());
				return "WEB-INF/views/failure";
			}
		} else {
			return "WEB-INF/views/failure";
		}
	}

	/**
	 * H5宝付支付充值异步返回
	 * 
	 * @param returnInfo
	 * @param request
	 */
	@RequestMapping("h5asynchronismRecharge.htm")
	public  String h5asynchronismRecharge(ReturnInfo info,HttpServletRequest request) throws IOException, TemplateException {
		if (info != null) {
			crs resignc = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(resignc.getClass().getSimpleName(), resignc.getClass());
			resignc = (crs) xs.fromXML(info.getResult());
			String Md5sign = CommonUtil.getMd5sign(info.getResult());
			if (info.getSign().equals(Md5sign)) {
				// 保存充值记录
				Recharge charge = processingService.findRechargeByOrderId(resignc.getOrder_id());
				if (resignc.getCode().equals("CSD000")) {
					try {
						if (charge.getStatus() == 0) {
								charge.setFee(resignc.getFee()); // 宝付收取费用
								charge.setMer_fee(resignc.getMer_fee()); // 商户收取的手续费
								charge.setIncash_money(resignc.getIncash_money()); // 充值结算金额(实际到账)
								charge.setCode(resignc.getCode());
								charge.setStatus(Constant.STATUES_ONE);
								charge.setAdditional_info(resignc.getAdditional_info());
								charge.setSuccTime(resignc.getSucc_time());
								processingService.updaterecharge(charge);
								Userbasicsinfo user = userbasicsinfoService.queryUserById(charge.getUserbasicsinfo().getId());
								// 流水账
								Accountinfo account = new Accountinfo();
								account.setExpenditure(0.00);
								account.setExplan("充值");
								account.setIncome(resignc.getIncash_money());
								account.setIpsNumber(resignc.getOrder_id());
								account.setTime(resignc.getSucc_time());
								account.setUserbasicsinfo(user);
								account.setFee(charge.getFee());
								account.setAccounttype(plankService.accounttype(6L));
								// 余额查询
								crs cr = baoFuService.getCasbalance(user.getpMerBillNo());
								user.getUserfundinfo().setCashBalance(cr.getBalance());
								user.getUserfundinfo().setOperationMoney(cr.getBalance());
								account.setMoney(cr.getBalance());
								userbasicsinfoService.update(user);
								plankService.saveAccount(account);// 保存流水账余额
								// 更新支付报文信息
								payLogService.updatePayLog(resignc.getOrder_id(),Constant.STATUES_ONE,charge.getIncash_money(), charge.getFee());
								LOG.error("宝付支付充值成功");
						}
						return "redirect:/h5/rechargeSuccess.htm";
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("宝付充值处理成功---->平台数据处理失败---" + info.getResult()
								+ "----->订单号----->" + resignc.getOrder_id());
						return "redirect:/h5/rechargeSuccess.htm";
					}
				} else if(resignc.getCode().equals("CSD333")){
						   charge.setStatus(-1);
						   charge.setFee(0.00);
						   processingService.updaterecharge(charge);
						   payLogService.updatePayLog(charge.getOrderNum(),-1,charge.getIncash_money(),charge.getFee());
							return "redirect:/h5/rechargeSuccess.htm";
				}else{
					LOG.error("宝付充值处理失败--" + info.getResult()+ "----->订单号----->" + resignc.getOrder_id());
					return "redirect:/h5/rechargeSuccess.htm";
				}
			} else {
				LOG.error("非宝付充值返回数据--" + info.getResult() + "----->订单号----->"
						+ resignc.getOrder_id());
				return "redirect:/h5/rechargeSuccess.htm";
			}
		} else {
			return "redirect:/h5/rechargeSuccess.htm";
		}
	}
	
	/**
	 * 第三方担保充值成功
	 * @param info
	 * @param request
	 * @return
	 */
	@RequestMapping("escrowRecharge.htm")
	public  String escrowRecharge(ReturnInfo info,
			HttpServletRequest request) {
		return "WEB-INF/views/success";

	}

	/**
	 * 宝付第三方担保充值异步返回
	 * @param returnInfo
	 * @param request
	 */
	@RequestMapping("asynEscrowRecharge.htm")
	public  String asynEscrowRecharge(ReturnInfo info,HttpServletRequest request) throws IOException, TemplateException {
		if (info != null) {
			crs resignc = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(resignc.getClass().getSimpleName(), resignc.getClass());
			resignc = (crs) xs.fromXML(info.getResult());
			String Md5sign = CommonUtil.getMd5sign(info.getResult());
			if (info.getSign().equals(Md5sign)) {
				// 保存充值记录
				EscrowRecharge charge = escrowRechargeService.escrowRechargeByOrderId(resignc.getOrder_id());
				if (resignc.getCode().equals("CSD000")) {
					try {
						if (charge.getStatus() == 0) {
								charge.setFee(resignc.getFee()); // 宝付收取费用
								charge.setMer_fee(resignc.getMer_fee()); // 商户收取的手续费
								charge.setIncash_money(resignc.getIncash_money()); // 充值结算金额(实际到账)
								charge.setStatus(Constant.STATUES_ONE);
								charge.setAdditional_info(resignc.getAdditional_info());
								charge.setSuccTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
								// 流水账
								EscrowAccountinfo account = new EscrowAccountinfo();
								account.setExpenditure(0.00);
								account.setExplan("第三方担保充值");
								account.setIncome(resignc.getIncash_money());
								account.setIpsNumber(resignc.getOrder_id());
								account.setEscrow(charge.getEscrow());
								account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
								account.setIsRechargeWithdraw(Constant.STATUES_ONE);
								// 余额查询
								crs cr = baoFuService.getCasbalance(charge.getEscrow().getStaffBaofu());
								charge.getEscrow().setStaffMoney(cr.getBalance());
								account.setMoney(cr.getBalance());
								escrowRechargeService.uptEscrowRecharge(charge);
								escrowRechargeService.saveEscrowAccountinfo(account);//保存流水账余额
								// 更新支付报文信息
								payLogService.updatePayLog(resignc.getOrder_id(),Constant.STATUES_ONE,charge.getIncash_money(), charge.getFee());
								LOG.error("宝付支付第三方担保充值成功");
						}
						return "WEB-INF/views/success";
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("宝付第三方担保充值处理成功---->平台数据处理失败---" + info.getResult()
								+ "----->订单号----->" + resignc.getOrder_id());
						return "WEB-INF/views/failure";
					}
				} else if(resignc.getCode().equals("CSD333")){
						   charge.setStatus(-1);
						   charge.setFee(0.00);
						   escrowRechargeService.uptEscrowRecharge(charge);
						   payLogService.updatePayLog(charge.getOrderNum(),-1,charge.getIncash_money(),charge.getFee());
						   return "WEB-INF/views/failure";
				}else{
					LOG.error("宝付第三方担保充值处理失败--" + info.getResult()+ "----->订单号----->" + resignc.getOrder_id());
					return  "WEB-INF/views/failure";
				}
			} else {
				LOG.error("非宝付第三方担保充值返回数据--" + info.getResult() + "----->订单号----->"
						+ resignc.getOrder_id());
				return "WEB-INF/views/failure";
			}
		} else {
			return "WEB-INF/views/failure";
		}
	}


	/**
	 * 用户提现返回处理
	 * 
	 * @param pMerCode
	 *            平台账号
	 * @param pErrCode
	 *            充值状态(0000成功，9999失败)
	 * @param pErrMsg
	 *            返回信息
	 * @param p3DesXmlPara
	 *            3des加密报文
	 * @param pSign
	 *            返回报文
	 * @param request
	 *            request
	 * @return 返回页面路径
	 */
	@RequestMapping("withdrawal.htm")
	public String withdrawal(ReturnInfo returnInfo, HttpServletRequest request) {
		return "WEB-INF/views/success_withdraw";
	}

	/**
	 * 提现的异步处理
	 * 
	 * @param pMerCode
	 * @param pErrCode
	 * @param pErrMsg
	 * @param p3DesXmlPara
	 * @param pSign
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping("withdrawAsynchronous.htm")
	public  String withdrawAsynchronous(ReturnInfo info,HttpServletRequest request)throws IOException, TemplateException {
		if (info != null) {
			String Md5sign = CommonUtil.getMd5sign(info.getResult());
			crs crs = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(crs.getClass().getSimpleName(), crs.getClass());
			crs = (crs) xs.fromXML(info.getResult());
			if (info.getSign().equals(Md5sign)) {
				Withdraw withdraw = processingService.findForchargebyorderid(crs.getOrder_id());
				if (crs.getCode().equals("CSD000")) {
					try {
						if (withdraw.getState() == 0) {
								if(HcPeachActivitiCache.validCurrentDate(new Date()) >= 0){
									Userbasicsinfo userbasicsinfo = withdraw.getUserbasicsinfo();
									userbasicsinfo.setFee(1);
									userbasicsinfoService.update(userbasicsinfo);
									withdraw.setFee(0d);
								}
								withdraw.setState(Constant.STATUES_TWO);
								withdrawServices.uptWithdraw(withdraw);
								// 更新支付报文信息
								payLogService.updatePayLog(crs.getOrder_id(),Constant.STATUES_TWO,0.00,0.00);
								LOG.error("宝付支付提现成功");
						}
						return "WEB-INF/views/success_withdraw";
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("宝付提现处理成功---->平台数据处理失败---" + info.getResult()
								+ "----->订单号----->" + crs.getOrder_id());
						return "WEB-INF/views/failure";
					}
				} else  if(crs.getCode().equals("CSD333")){
						withdraw.setFee(0.00);
	                	withdraw.setState(-1);
	                	withdrawServices.uptWithdraw(withdraw);
	                	// 更新支付报文信息
						payLogService.updatePayLog(withdraw.getStrNum(),-1,withdraw.getWithdrawAmount(), withdraw.getFee());
						return "WEB-INF/views/failure";
				}else{
					LOG.error("宝付提现处理失败--" + info.getResult()+ "----->订单号----->" + crs.getOrder_id());
					return "WEB-INF/views/failure";
				}
			} else {
				LOG.error("非宝付提现返回数据--" + info.getResult() + "----->订单号----->"+ crs.getOrder_id());
				return "WEB-INF/views/failure";
			}
		} else {
			return "WEB-INF/views/failure";
		}
	}

	/**
	 * H5提现的异步处理
	 * 
	 * @param pMerCode
	 * @param pErrCode
	 * @param pErrMsg
	 * @param p3DesXmlPara
	 * @param pSign
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping("h5withdrawAsynchronous.htm")
	public  String h5withdrawAsynchronous(ReturnInfo info,HttpServletRequest request)throws IOException, TemplateException {
		if (info != null) {
			String Md5sign = CommonUtil.getMd5sign(info.getResult());
			crs crs = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(crs.getClass().getSimpleName(), crs.getClass());
			crs = (crs) xs.fromXML(info.getResult());
			if (info.getSign().equals(Md5sign)) {
				Withdraw withdraw = processingService.findForchargebyorderid(crs.getOrder_id());
				if (crs.getCode().equals("CSD000")) {
					try {
						if (withdraw.getState() == 0) {
								if(HcPeachActivitiCache.validCurrentDate(new Date()) >= 0){
									Userbasicsinfo userbasicsinfo = withdraw.getUserbasicsinfo();
									userbasicsinfo.setFee(1);
									userbasicsinfoService.update(userbasicsinfo);
									withdraw.setFee(0d);
								}
								withdraw.setState(Constant.STATUES_TWO);
								withdrawServices.uptWithdraw(withdraw);
								// 更新支付报文信息
								payLogService.updatePayLog(crs.getOrder_id(),Constant.STATUES_TWO,0.00,0.00);
								LOG.error("宝付支付提现成功");
						}
						return "redirect:/h5/withdrawCashSuccess.htm";
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("宝付提现处理成功---->平台数据处理失败---" + info.getResult()
								+ "----->订单号----->" + crs.getOrder_id());
						return "redirect:/h5/withdrawCashSuccess.htm";
					}
				} else  if(crs.getCode().equals("CSD333")){
						withdraw.setFee(0.00);
	                	withdraw.setState(-1);
	                	withdrawServices.uptWithdraw(withdraw);
	                	// 更新支付报文信息
						payLogService.updatePayLog(withdraw.getStrNum(),-1,withdraw.getWithdrawAmount(), withdraw.getFee());
						return "redirect:/h5/withdrawCashSuccess.htm";
				}else{
					LOG.error("宝付提现处理失败--" + info.getResult()+ "----->订单号----->" + crs.getOrder_id());
					return "redirect:/h5/withdrawCashSuccess.htm";
				}
			} else {
				LOG.error("非宝付提现返回数据--" + info.getResult() + "----->订单号----->"+ crs.getOrder_id());
				return "redirect:/h5/withdrawCashSuccess.htm";
			}
		} else {
			return "redirect:/h5/withdrawCashSuccess.htm";
		}
	}
	
	/***
	 * 第三方担保提现同步处理
	 * @param returnInfo
	 * @param request
	 * @return
	 */
	@RequestMapping("escrowWithdraw.htm")
	public String escrowWithdraw(ReturnInfo returnInfo, HttpServletRequest request) {
		return "WEB-INF/views/success";
	}

	/**
	 * 第三方担保提现的异步处理
	 * 
	 * @param pMerCode
	 * @param pErrCode
	 * @param pErrMsg
	 * @param p3DesXmlPara
	 * @param pSign
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping("asynEscrowWithdraw.htm")
	public  String asynEscrowWithdraw(ReturnInfo info,HttpServletRequest request)throws IOException, TemplateException {
		if (info != null) {
			String Md5sign = CommonUtil.getMd5sign(info.getResult());
			crs crs = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(crs.getClass().getSimpleName(), crs.getClass());
			crs = (crs) xs.fromXML(info.getResult());
			if (info.getSign().equals(Md5sign)) {
				EscrowWithdraw withdraw = escrowWithdrawService.escrowWithdrawByOrderId(crs.getOrder_id());
				if (crs.getCode().equals("CSD000")) {
					try {
						if (withdraw.getState() == 0) {
								withdraw.setState(Constant.STATUES_TWO);
								withdraw.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
								escrowWithdrawService.uptEscrowWithdraw(withdraw);
								// 更新支付报文信息
								payLogService.updatePayLog(crs.getOrder_id(),Constant.STATUES_TWO,0.00,0.00);
								LOG.error("宝付第三方担保支付提现成功");
						}
						return "WEB-INF/views/success";
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("宝付第三方担保提现处理成功---->平台数据处理失败---" + info.getResult()
								+ "----->订单号----->" + crs.getOrder_id());
						return "WEB-INF/views/failure";
					}
				} else  if(crs.getCode().equals("CSD333")){
						withdraw.setFee(0.00);
	                	withdraw.setState(-1);
	                	withdraw.setMer_fee(0.00);
	                	withdraw.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
	                	escrowWithdrawService.uptEscrowWithdraw(withdraw);
	                	// 更新支付报文信息
						payLogService.updatePayLog(withdraw.getpIpsBillNo(),-1,withdraw.getWithdrawAmount(), withdraw.getFee());
						return "WEB-INF/views/failure";
				}else{
					LOG.error("宝付第三方担保提现处理失败--" + info.getResult()+ "----->订单号----->" + crs.getOrder_id());
					return "WEB-INF/views/failure";
				}
			} else {
				LOG.error("非宝付第三方担保提现返回数据--" + info.getResult() + "----->订单号----->"+ crs.getOrder_id());
				return "WEB-INF/views/failure";
			}
		} else {
			return "WEB-INF/views/failure";
		}
	}

	/**
	 * 店铺投标页面跳转
	 * 
	 * @param info
	 *            返回异步信息
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping("returnShopBid.htm")
	public String returnShopBid(ReturnInfo info,HttpServletRequest request) {
		return "WEB-INF/views/success";
	}

	/**
	 * 店铺投标异步处理
	 * 
	 * @param info
	 *            返回异步信息
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping("asynchronismShopBid.htm")
	public synchronized String asynchronismShopBid(ReturnInfo info,
			HttpServletRequest request) throws IOException, TemplateException {
		if (info != null) {
			crs crs = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(crs.getClass().getSimpleName(), crs.getClass());
			crs = (crs) xs.fromXML(info.getResult());
			String Md5sign = CommonUtil.MD5(crs.getCode() + "~|~"+ crs.getMsg() + "~|~" + crs.getOrder_id() + "~|~"+ ParameterIps.getDes_algorithm());
			if (crs.getSign().equals(Md5sign)) {
				Paylog payLog = payLogService.queryPaylogByOrderSn(crs.getOrder_id());
				// 根据店铺id查询店铺信息
				Shop shop = shopService.getShopById(payLog.getShopId().toString());
				// 更新购买信息
				ShopRecord shopRecord = shopService.queryShopRecordByorderNum(crs.getOrder_id());
				Userbasicsinfo userinfo = shopRecord.getUserbasicsinfo();
				ShopRewardOption shopRewardOption = shopService.queryShopRewardOption(String.valueOf(shopRecord.getShopRoId()));
				
				int type=shopRecord.getType();//投资类型
				if (crs.getCode().equals("CSD000")) {
						try {
									if (shopRecord.getIsSucceed() == 0) {
									    	shopRecord.setUptTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
									    	shopRecord.setIsSucceed(Constant.STATUES_ONE);
										    shopService.updateShopRecord(shopRecord);
											// 流水账
											Accountinfo account = new Accountinfo();
											account.setExpenditure(Double.valueOf(payLog.getAmount()));
											account.setExplan("店铺投资");
											account.setIncome(0.00);
											account.setIpsNumber(crs.getOrder_id());
											account.setShopId(shop.getId());
											account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
											account.setUserbasicsinfo(userinfo);
											account.setFee(payLog.getFee());
											account.setAccounttype(plankService.accounttype(10L));
											// 根据店铺id查询总购买金额
											Double sumTenderMoney=shopManageService.getSumTenderMoney(shop.getId().toString());
											 if (shop.getRealFunds()<=sumTenderMoney) {
												 //发短信
												 smsService.sendSMS("尊敬的"+shop.getUserbasicsinfo().getName()+"客户，您好：您在【前海红筹】众筹的"+shop.getShopName()+"店铺的总筹集金额已达到您所需筹集的金额，请您到【前海红筹】平台上进行处理，谢谢！【前海红筹】", shop.getUserbasicsinfo().getUserrelationinfo().getPhone());
											 } 
											crs cr = baoFuService.getCasbalance(userinfo.getpMerBillNo());
											userinfo.getUserfundinfo().setCashBalance(cr.getBalance());
											userinfo.getUserfundinfo().setOperationMoney(cr.getBalance());
											account.setMoney(cr.getBalance());
											plankService.saveAccount(account);// 保存流水账余额
											userbasicsinfoService.update(userinfo);
											
											// 更新支付报文信息
											payLogService.updatePayLog(crs.getOrder_id(),Constant.STATUES_ONE);
											
											LOG.error("宝付支付投资成功");
											//接到从宝付返回成功的信息，投资积分
											integralSevice.invest(userinfo,shopRecord.getTenderMoney(),IntegralType.INVEST);
											//如果是抽奖，支付成功后获得抽奖号
											
											if (type==1) {
												if (shopRewardOption.getIstrue() == 1) {
													Integer count = Integer.valueOf(shopService.getLotteryCount(shop.getId()).toString());
													Integer lottery=0;
													if (count >= shopRewardOption.getRewardCount()) {
														lottery=shopRewardOption.getRewardCount();// 直接取当前奖励人数
														shopRewardOption.setRewardCount(shopRewardOption.getRewardCount() + 1);// 更新奖励人数
														shopService.updateShopRewardOption(shopRewardOption);
													} else {
														lottery=lotteryService.generateLottery(shop.getId());

													}
													
													Map<String,String> map= new HashMap<String,String>();
													map.put("user", userinfo.getName());
													map.put("shop_name", shop.getShopName());
													map.put("code", String.valueOf(lottery));
													String content = smsService.getSmsResources("invite-friend.flt", map);
													smsService.sendSMS(content, userinfo.getUserrelationinfo().getPhone());
													shopRecord.setLottery(lottery);
													shopService.saveShopRecord(shopRecord);
												}
											}
											
											
									}
							return "WEB-INF/views/success";
						} catch (Exception e) {
							e.printStackTrace();
							LOG.error("宝付投标处理成功---->平台数据处理失败---" + info.getResult()+ "----->订单号----->" + crs.getOrder_id());
							return "WEB-INF/views/failure";
						}
				}else if(crs.getCode().equals("CSD333")){
					if (shopRecord.getIsSucceed() == 0) {
						shopRecord.setUptTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
				    	shopRecord.setIsSucceed(-1);
					    shopService.updateShopRecord(shopRecord);
					    // 更新支付报文信息
						payLogService.updatePayLog(crs.getOrder_id(),-1);
					}
				    return "WEB-INF/views/failure";
				} else {
					LOG.error("宝付投标处理失败--" + info.getResult()+ "----->订单号----->" + crs.getOrder_id());
					return "WEB-INF/views/failure";
				}
			} else {
				LOG.error("非宝付投标返回数据--" + info.getResult() + "----->订单号----->"+ crs.getOrder_id());
				return "WEB-INF/views/failure";
			}
		} else {
			return "WEB-INF/views/failure";
		}
	}

	/**
	 * 店铺满标页面跳转
	 * 
	 * @param returnInfo
	 * @param request
	 * @return
	 */
	@RequestMapping("shopBidFull.htm")
	public String shopBidFull(ReturnInfo returnInfo, HttpServletRequest request) {
		return "WEB-INF/views/success";
	}

	/**
	 * 店铺满标异步处理
	 * 
	 * @param info
	 * @param investMoney
	 * @param ordernum
	 * @param pId
	 * @param userid
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	@RequestMapping("asyncronisShopBidFull.htm")
	public synchronized String asyncronisShopBidFull(ReturnInfo info,HttpServletRequest request) throws IOException,TemplateException {
		if (info != null) {
			crs crs = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(crs.getClass().getSimpleName(), crs.getClass());
			crs = (crs) xs.fromXML(info.getResult());
			String Md5sign = CommonUtil.MD5(crs.getCode() + "~|~" + crs.getMsg()	+ "~|~" + crs.getOrder_id() + "~|~"	+ ParameterIps.getDes_algorithm());
			    if (crs.getSign().equals(Md5sign)) {
			    	Paylog payLog=payLogService.queryPaylogByOrderSn(crs.getOrder_id());
			    	Shop shop = shopService.getShopById(payLog.getShopId().toString());
						if (crs.getCode().equals("CSD000")) {
								try{
									if(payLog.getStatus()==0){
											//根据店铺id查询店铺信息
											// 流水账
											Accountinfo account = new Accountinfo();
											account.setExpenditure(0.00);
											account.setExplan("店铺放款");
											account.setFee(payLog.getFee());
											account.setIncome(Double.valueOf(payLog.getAmount()));
											account.setIpsNumber(crs.getOrder_id());
											account.setShopId(shop.getId());// 标id（店铺id）
											account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
											account.setUserbasicsinfo(shop.getUserbasicsinfo());
											account.setAccounttype(plankService.accounttype(11L));
											// 查询余额
											crs cr = baoFuService.getCasbalance(shop.getUserbasicsinfo().getpMerBillNo());
											shop.getUserbasicsinfo().getUserfundinfo().setCashBalance(cr.getBalance());
											shop.getUserbasicsinfo().getUserfundinfo().setOperationMoney(cr.getBalance());
											account.setMoney(cr.getBalance());
											plankService.saveAccount(account);// 更新流水账余额
											userbasicsinfoService.update(shop.getUserbasicsinfo());
											
											//更新放款服务费
											shop.setFee(payLog.getFee());
											processingService.updateShop(shop);
											
							            	//修改合同号
							            	List<ShopRecord> shopRecordList=processingService.getShopRecord(shop.getId());
							            	for(int i=0;i<shopRecordList.size();i++){
							            		ShopRecord shopRecord=shopRecordList.get(i);
							            		String number=StringUtil.addZeroForNum(String.valueOf(i+1),4);
							            		shopRecord.setpContractNo(shop.getShopNumber()+"_"+number);
							            		processingService.updateShopRecord(shopRecord);
							            	}
							            	//更新支付报文信息
											payLogService.updatePayLog(crs.getOrder_id(), Constant.STATUES_ONE);
										}
									 return "WEB-INF/views/success";
								}catch(Exception e){
									 e.printStackTrace();
									LOG.error("宝付放款处理成功---->平台数据处理失败---" + info.getResult()+"----->订单号----->"+crs.getOrder_id());
									 return "WEB-INF/views/failure";
								}
						}else if(crs.getCode().equals("CSD000")){
								shop.setState(5);
								shopService.updateShop(shop);
								//更新支付报文信息
								payLogService.updatePayLog(crs.getOrder_id(), -1);
								return "WEB-INF/views/failure";
						}else{
							LOG.error("宝付放款处理失败--" + info.getResult()+"----->订单号----->"+crs.getOrder_id());
							return "WEB-INF/views/failure";
						}
				}else{
					LOG.error("非宝付放款返回数据--" + info.getResult()+"----->订单号----->"+crs.getOrder_id());
					return "WEB-INF/views/failure";
				}
		} else {
			return "WEB-INF/views/failure";
		}
	}

	/***
	 * 店铺流标同步处理
	 * 
	 * @param returnInfo
	 * @param request
	 * @return
	 */
	@RequestMapping("returnBidShopFlow.htm")
	public String returnBidShopFlow(ReturnInfo returnInfo,
			HttpServletRequest request) {
		return "WEB-INF/views/success";
	}

	/***
	 * 店铺流标异步处理
	 * 
	 * @param info
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	@RequestMapping("asynchronismBidShopFlow.htm")
	public synchronized String asynchronismBidShopFlow(ReturnInfo info,HttpServletRequest request) throws IOException,TemplateException {
			if (info != null) {
					crs crs = new crs();
					XStream xs = new XStream(new DomDriver());
					xs.alias(crs.getClass().getSimpleName(), crs.getClass());
					crs = (crs) xs.fromXML(info.getResult());
					String Md5sign = CommonUtil.MD5(crs.getCode() + "~|~" + crs.getMsg() + "~|~" + crs.getOrder_id() + "~|~" + ParameterIps.getDes_algorithm());
					if (crs.getSign().equals(Md5sign)) {
						    Paylog payLog=payLogService.queryPaylogByOrderSn(crs.getOrder_id());
							//根据店铺id查询店铺信息
							 Shop shop = shopService.getShopById(payLog.getShopId().toString());
							 if (crs.getCode().equals("CSD000")) {
									try{
										 if(payLog.getStatus()==0){
											Accountinfo account = new Accountinfo();
											//根据店铺Id查询所有购买记录
											List<ShopRecord> listShopRecord = shopService.queryRecordList(shop.getId().toString());
											//根据店铺Id查询总购买记录
											Double sumMoney=shopManageService.getSumTenderMoney(shop.getId().toString());
												for (ShopRecord shopRecord : listShopRecord) {
												  	   //对流标的收款人操作
											          Userbasicsinfo user = userInfoServices.queryBasicsInfoById(shopRecord.getUserbasicsinfo().getId().toString());
											        	crs cr=baoFuService.getCasbalance(user.getpMerBillNo());
													    user.getUserfundinfo().setCashBalance(cr.getBalance()); 
													    user.getUserfundinfo().setOperationMoney(cr.getBalance()); 
													    userbasicsinfoService.update(user);
														account.setExpenditure(0.00);
														account.setExplan("店铺流标");
														account.setIncome(shopRecord.getTenderMoney());
														account.setShopId(shopRecord.getShop().getId());
														account.setIpsNumber(crs.getOrder_id());
														account.setLoansignId("");// 标id（项目id）
														account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
														account.setUserbasicsinfo(user);
														account.setFee(0.00);
														account.setAccounttype(plankService.accounttype(9L));
														account.setMoney(cr.getBalance());
														plankService.saveAccount(account);// 添加流水账余额
												}
												// 流水账
												account.setExpenditure(sumMoney);
												account.setExplan("店铺流标");
												account.setShopId(shop.getId());
												account.setIncome(0.00);
												account.setIpsNumber(crs.getOrder_id());
												account.setLoansignId("");// 标id（项目id）
												account.setFee(0.00);
												account.setUserbasicsinfo(shop.getUserbasicsinfo());
												account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
												account.setAccounttype(plankService.accounttype(9L));
												// 查询余额
												crs crsShop = baoFuService.getCasbalance(shop.getUserbasicsinfo().getpMerBillNo());
												shop.getUserbasicsinfo().getUserfundinfo().setCashBalance(crsShop.getBalance());
												shop.getUserbasicsinfo().getUserfundinfo().setOperationMoney(crsShop.getBalance());
												account.setMoney(crsShop.getBalance());
												plankService.saveAccount(account);// 更新流水账余额
												userbasicsinfoService.update(shop.getUserbasicsinfo());
												//更新支付报文信息
												payLogService.updatePayLog(crs.getOrder_id(), Constant.STATUES_ONE);
												
												LOG.error("宝付支付店铺流标查询处理成功");
										}
										return "WEB-INF/views/success";
									}catch(Exception e){
										e.printStackTrace();
										LOG.error("宝付流标处理成功---->平台数据处理失败---" + info.getResult()+"----->订单号----->"+ crs.getOrder_id());
										return "WEB-INF/views/failure";
									}
							 }else if(crs.getCode().equals("CSD333")){
									shop.setState(4);
									shopService.updateShop(shop);
									//更新支付报文信息
									payLogService.updatePayLog(crs.getOrder_id(),-1);
									 return "WEB-INF/views/failure";
							 }else{
								 LOG.error("宝付流标处理失败--" + info.getResult()+"----->订单号----->"+ crs.getOrder_id());
								 return "WEB-INF/views/failure";
							 }
					}else{
						LOG.error("非宝付流标返回数据--" + info.getResult()+"----->订单号----->"+ crs.getOrder_id());
						 return "WEB-INF/views/failure";
					}
		} else {
			return "WEB-INF/views/failure";
		}
	}

    /***
     * 项目投标同步处理
     * @param returnInfo
     * @param request
     * @return
     */
	@RequestMapping("returnLoanBid.htm")
	public String returnLoanBid(ReturnInfo info, HttpServletRequest request,@RequestParam("orderNum") String orderNum) {
		//获取投标信息
	    Loanrecord loanRecord = loanrecordService.getLoanRecordOrderNum(orderNum);
	    loanRecord.setIsSucceed(Constant.STATUES_TWO);
		  //更新支付报文信息
		payLogService.updatePayLog(orderNum, Constant.STATUES_TWO);
		return "WEB-INF/views/success_invest";
	}

	/**
	 * 项目投标异步处理
	 * @param info 返回异步信息
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping("asynchronismLoanBid.htm")
	public  String asynchronismLoanBid(ReturnInfo info,HttpServletRequest request) throws IOException,TemplateException {
		if (info != null) {
			crs crsLoan = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(crsLoan.getClass().getSimpleName(), crsLoan.getClass());
			crsLoan = (crs) xs.fromXML(info.getResult());
			String Md5sign = CommonUtil.MD5(crsLoan.getCode() + "~|~" + crsLoan.getMsg()+ "~|~" + crsLoan.getOrder_id() + "~|~"+ ParameterIps.getDes_algorithm());
			if (crsLoan.getSign().equals(Md5sign)) {
				Paylog payLog = payLogService.queryPaylogByOrderSn(crsLoan.getOrder_id());
				// 获取当前用户的最新信息
				Userbasicsinfo userinfo = userInfoServices .queryBasicsInfoById(payLog.getUserId().toString());
				// 获取标的详细信息
				Loansign loan = loanSignQuery.getLoansignById(payLog.getLoansignId().toString());
				//获取投标信息
			    Loanrecord loanRecord = loanrecordService.getLoanRecordOrderNum(crsLoan.getOrder_id());
			    // 获取费用表的信息
				Costratio costratio = loanSignQuery.queryCostratio();
					if (crsLoan.getCode().equals("CSD000")) {
							try{
									if(loanRecord.getIsSucceed()==2){
									    loanRecord.setIsSucceed(Constant.STATUES_ONE);
									    loanRecord.setUpdateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
										// 投资按100元计1分
										Integer product = (int) (payLog.getAmount() / 100);
										plankService.saveAutointegralBuyProject(userinfo, payLog.getAmount(), loanRecord.getSubType()); // 保存积分记录
										// 余额查询
										crs cr = baoFuService.getCasbalance(userinfo.getpMerBillNo());
										userinfo.getUserfundinfo().setCashBalance(cr.getBalance()); // 宝付的余额
										userinfo.getUserfundinfo().setOperationMoney(cr.getBalance());
										userinfo.setUserintegral(userinfo.getUserintegral() + product); // 积分计算
							
										// 银行流水
										Accountinfo account = new Accountinfo();
										account.setExpenditure(payLog.getAmount());
										if (loanRecord.getLoanType() == 2) {
											account.setExplan("项目购买");
										} else if(loanRecord.getLoanType() ==3) {
											account.setExplan("天标购买");
										}
										account.setIncome(0.00);
										account.setIpsNumber(crsLoan.getOrder_id());
										account.setLoansignId(loan.getId().toString());
										account.setFee(payLog.getFee());
										account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
										account.setUserbasicsinfo(userinfo);
										if (loanRecord.getLoanType() == 2) {
											account.setAccounttype(plankService.accounttype(5L));
										} else  if(loanRecord.getLoanType() == 3){
											account.setAccounttype(plankService.accounttype(15L));
										}
										account.setMoney(cr.getBalance());// 流水记录表
										plankService.update(loanRecord, account, userinfo, loan);
										//保存佣金
										generalizeService.saveGeneralizeMoney(loanRecord);
										//判断是否融资成功
									    Double tendMoney=loanSignQuery.getSumLoanTenderMoney(loan.getId().toString());
									    Double subMoney=Arith.sub(loan.getIssueLoan(), tendMoney);
									    if(subMoney==0){
									    	loan.setStatus(2); // 融资成功
									    	processingService.updateLoan(loan);
									    	Map<String, String> map = new HashMap<String, String>();
											map.put("loanNum", loan.getName());
											String content = smsService.getSmsResources("check-fullBid.ftl", map);
											int trigger=Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
											String[] phones=costratio.getBidPhone().split(",");
											for(int i=0;i<phones.length;i++){
												smsService.chooseSmsChannel(trigger, content, phones[i]);
											}
//											baseSmsService.sendSMS(content,costratio.getBidPhone());
									    }
									  //更新支付报文信息
									 payLogService.updatePayLog(crsLoan.getOrder_id(), Constant.STATUES_ONE);
									}
									LOG.error("宝付项目购买处理成功");
									return "WEB-INF/views/success_invest";
							}catch(Exception e){
								e.printStackTrace();
								LOG.error("宝付项目购买处理成功---->平台数据处理失败---" + info.getResult()+"----->订单号----->"+ crsLoan.getOrder_id());
								return "WEB-INF/views/failure";
							}
					} else if(crsLoan.getCode().equals("CSD333")) {
						if(loanRecord.getIsSucceed()==0){
								 //剩余金额
								 loan.setRestMoney(Arith.add(loan.getRestMoney(),loanRecord.getTenderMoney()));
								 if(loanRecord.getSubType()==1){  //优先
									  if(loanRecord.getIsType()==0){   //默认
											  loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(), loanRecord.getTenderMoney()));
										  }else if(loanRecord.getIsType()==2){   //夹层转优先
											  Double money=Arith.sub(loanRecord.getTenderMoney(),loanRecord.getSubMoney());  //购买金额-差额=夹层剩余金额
											  loan.setMiddle(Arith.add(loan.getMiddle(), loanRecord.getSubMoney()));  //夹层总额+差额
											  loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(), loanRecord.getSubMoney()));  //夹层剩余金额+差额
											  loan.setPriority(Arith.sub(loan.getPriority(),  loanRecord.getSubMoney()));  //优先总额-差额
											  loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(), money));  //优先剩余金额
										  }
									 }else if(loanRecord.getSubType()==2){   //夹层
										 if(loanRecord.getIsType()==0){
											  loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(), loanRecord.getTenderMoney()));
										  }else if(loanRecord.getIsType()==1){ //优先转夹层
											  Double money=Arith.sub(loanRecord.getTenderMoney(),loanRecord.getSubMoney());  //购买金额-差额=优先剩余金额
											  loan.setPriority(Arith.add(loan.getPriority(),  loanRecord.getSubMoney()));  //优先总金额+差额
											  loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(),  loanRecord.getSubMoney()));  //优先剩余总金额+差额
											  loan.setMiddle(Arith.sub(loan.getMiddle(), loanRecord.getSubMoney())); //夹层总额-差额
											  loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(), money));  //夹层剩余金额
										  }
								 }else  if(loanRecord.getSubType()==3){  //劣后
									 loan.setAfterRestMoney(Arith.add(loan.getAfterRestMoney(),loanRecord.getTenderMoney()));
								 }
								 loanRecord.setIsSucceed(-1);
							     loanRecord.setUpdateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
								 loanSignQuery.updateLoanRecord(loanRecord,loan);
								 //更新支付报文信息
								 payLogService.updatePayLog(crsLoan.getOrder_id(),-1);
								 LOG.error("宝付项目购买处理失败");
							}
						     return "WEB-INF/views/failure";
					}else {
						 LOG.error("宝付项目购买处理失败--" + info.getResult()+"----->订单号----->"+ crsLoan.getOrder_id());
						return "WEB-INF/views/failure";
					}
			} else {
				LOG.error("非宝付项目购买返回数据--" + info.getResult()+"----->订单号----->"+ crsLoan.getOrder_id());
				return "WEB-INF/views/failure";
			}
		} else {
			return "WEB-INF/views/failure";
		}
	}

	/**
	 * 项目满标页面跳转
	 * 
	 * @param returnInfo
	 * @param request
	 * @return
	 */
	@RequestMapping("returnLoanCredit.htm")
	public String returnLoanCredit(ReturnInfo returnInfo,HttpServletRequest request) {
		logger.info("项目满标放款同步处理页面跳转成功！");
		return "WEB-INF/views/success";
	}

	/**
	 * 项目满标放款异步处理
	 * 
	 * @param info
	 * @param investMoney
	 * @param ordernum
	 * @param pId
	 * @param userid
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 * @throws ParseException
	 */
	@RequestMapping("asynchronismLoanCredit.htm")
	public synchronized String asynchronismLoanCredit(ReturnInfo info,HttpServletRequest request) throws IOException, TemplateException, ParseException {
		
		logger.info("项目满标放款异步处理开始！");
		
		  if (info != null) {
			  logger.info("宝付处理返回结果：JsonUtil.toJsonStr(info) = " + JsonUtil.toJsonStr(info));
			  logger.info("宝付处理返回结果：info.getResult() = " + info.getResult());
			  
				crs crsCredit = new crs();
				XStream xs = new XStream(new DomDriver());
				xs.alias(crsCredit.getClass().getSimpleName(), crsCredit.getClass());
				crsCredit = (crs) xs.fromXML(info.getResult());
				String Md5sign = CommonUtil.MD5(crsCredit.getCode() + "~|~" + crsCredit.getMsg()+ "~|~" + crsCredit.getOrder_id() + "~|~"+ ParameterIps.getDes_algorithm());
					if (crsCredit.getSign().equals(Md5sign)) {
						logger.info("宝付签名认证通过！");
						
						Paylog payLog = payLogService.queryPaylogByOrderSn(crsCredit.getOrder_id());
						Loansign loan = loanSignQuery.getLoansignById(payLog.getLoansignId().toString());
						Userbasicsinfo userinfo = userInfoServices.queryBasicsInfoById(String.valueOf(loan.getUserbasicsinfo().getId()));
								if (crsCredit.getCode().equals("CSD000")) {
									logger.info("宝付处理成功，开始本地处理逻辑！");
									
									try{
										if(payLog.getStatus()==0){
											//查询余额
											crs cr = baoFuService.getCasbalance(userinfo.getpMerBillNo());
										    userinfo.getUserfundinfo().setCashBalance(cr.getBalance());
										    userinfo.getUserfundinfo().setOperationMoney(cr.getBalance());
										    //流水账
											baoFuLoansignService.getSaveAccount(loan, payLog, userinfo, crsCredit.getOrder_id(), cr.getBalance());
											
										    userbasicsinfoService.update(userinfo);
										    
										    if(payLog.getFee()>0&&loan.getRefunway()!=4){
										    	//更新放款服务费
											    loan.setFee(payLog.getFee());
											    loan.setFeeState(Constant.STATUES_ONE);
											    loanSignQuery.updateLoansign(loan);
										    }
										    
										    //更新支付报文信息
											payLogService.updatePayLog(crsCredit.getOrder_id(), Constant.STATUES_ONE);
											
											//红包转账
						            		if(loan.getRedEnvelopeMoney()>0){
						            			baoFuLoansignService.ipsTranCertHBZZ(loan);
							            	}
										    
							            	//修改合同号
											List<Loanrecord> listLoanrecord=processingService.getLoanRecord(loan.getId());
							            	int count=processingService.getLoanRecordContractNoCount(loan);
							            	for(int i=0;i<listLoanrecord.size();i++){
							            		Loanrecord loanrecord=listLoanrecord.get(i);
							            		String number=StringUtil.addZeroForNum(String.valueOf(i+count+1),4);
							            		loanrecord.setpContractNo(loan.getLoansignType().getTypeName()+DateUtils.format("yyyy").substring(2, 4)+StringUtil.addZeroForNum(loan.getProjectNumber(), 4)+"-"+number);
							            		processingService.updateLoanrecord(loanrecord);
							            	}
							            	
										}
										LOG.error("宝付项目满标放款处理成功");
										smsService.sendSmsForFullBidGrantMoney(loan);
										
										// 满标放款提醒推送消息
										messagePushService.pushMessageAfterIssueLoan(loan);
										
										return "WEB-INF/views/success";
									}catch(Exception e){
										LOG.error("宝付项目满标放款处理成功---->平台数据处理失败---" + info.getResult()+"----->订单号----->"+ crsCredit.getOrder_id(), e);
										smsService.sendSmsForFullBidGrantMoney(loan);
										return "WEB-INF/views/failure";
									}
						} else if(crsCredit.getCode().equals("CSD333")){
							loan.setStatus(4);
							loanSignQuery.updateLoansign(loan);
							payLogService.updatePayLog(crsCredit.getOrder_id(),-1);
							LOG.error("宝付项目满标放款处理失败");
							return "WEB-INF/views/failure";
						}else if(crsCredit.getCode().equals("RQ006")){
							loan.setStatus(4);
							loanSignQuery.updateLoansign(loan);
							payLogService.updatePayLog(crsCredit.getOrder_id(),-1);
							LOG.error("宝付项目满标放款处理失败，满标金额，与投资金额不一致");
							return "WEB-INF/views/failure";
						}else {
							 LOG.error("宝付项目满标放款处理失败--" + info.getResult()+"----->订单号----->"+ crsCredit.getOrder_id());
							return "WEB-INF/views/failure";
						}
				} else {
					LOG.error("非宝付项目满标放款返回数据--" + info.getResult()+"----->订单号----->"+ crsCredit.getOrder_id());
					return "WEB-INF/views/failure";
				}
		} else {
			return "WEB-INF/views/failure";
		}
	}
	
	
	/**
	 * 项目流标页面跳转
	 * 
	 */
	@RequestMapping("returnLoanFlow.htm")
	public String returnLoanFlow(ReturnInfo Info,HttpServletRequest request) {
		return "WEB-INF/views/success";
	}

	/***
	 * 项目流标处理
	 * @param info
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	@RequestMapping("asynchronismLoanFlow.htm")
	public  String asynchronismLoanFlow(ReturnInfo info,HttpServletRequest request) throws IOException,
			TemplateException {
		if (info != null) {
			crs crsFlow = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(crsFlow.getClass().getSimpleName(), crsFlow.getClass());
			crsFlow = (crs) xs.fromXML(info.getResult());
			String Md5sign = CommonUtil.MD5(crsFlow.getCode() + "~|~" + crsFlow.getMsg()+ "~|~" + crsFlow.getOrder_id() + "~|~"+ ParameterIps.getDes_algorithm());
			if (crsFlow.getSign().equals(Md5sign)) {
			    Paylog payLog = payLogService.queryPaylogByOrderSn(crsFlow.getOrder_id());
			    Loansign loan = loanSignQuery.getLoansignById(payLog.getLoansignId().toString());
					if (crsFlow.getCode().equals("CSD000")) {
								try{
									    if(payLog.getStatus()==0){
									    	    payLogService.updatePayLog(loan.getOrderSn(),Constant.STATUES_ONE);
												Userbasicsinfo userbasicsinfo = userInfoServices.queryBasicsInfoById(loan.getUserbasicsinfo().getId().toString());
												List<Loanrecord> loancordlist = loanrecordService.findLoanRecordList(loan.getId());
												Double sumTenderMoney=0.00;
												for (Loanrecord loancord : loancordlist) {
													// 对流标的收款人操作
													Userbasicsinfo user = userInfoServices.queryBasicsInfoById(loancord.getUserbasicsinfo().getId().toString());
													crs cr = baoFuService.getCasbalance(user.getpMerBillNo());
													user.getUserfundinfo().setCashBalance(cr.getBalance());
													user.getUserfundinfo().setOperationMoney(cr.getBalance());
													Accountinfo accountOne = new Accountinfo();
													accountOne.setExpenditure(0.00);
													accountOne.setExplan("项目流标");
													Double tenderMoney=loancord.getTenderMoney();
													if(loancord.getRedEnvelopeMoney()>0){
														tenderMoney=Arith.sub(loancord.getTenderMoney(), loancord.getRedEnvelopeMoney());
													}
													sumTenderMoney+=tenderMoney;
													accountOne.setIncome(tenderMoney);
													accountOne.setIpsNumber(crsFlow.getOrder_id());
													accountOne.setLoansignId(loan.getId().toString()); // 标id（项目id）
													accountOne.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
													accountOne.setUserbasicsinfo(user);
													accountOne.setAccounttype(plankService.accounttype(3L));
													accountOne.setMoney(cr.getBalance());
													plankService.saveAccount(accountOne);// 添加流水账余额
													userbasicsinfoService.update(user);
													//修改推广资金记录
													generalizeMoneyServices.updateGeneralizemoney(loancord.getId().toString(), -1);
												}
												
												//更新红包奖励
												redEnvelopeDetailService.listRedEnvelopeDetails(loan.getId());
												
												//更新加息卷 lkl-20150825
												increaseCardService.uptIncreaseCard(loan.getId());
												
												// 流水账
												Accountinfo account = new Accountinfo();
												account.setExpenditure(sumTenderMoney);
												account.setExplan("项目流标");
												account.setIncome(0.00);
												account.setIpsNumber(crsFlow.getOrder_id());
												account.setLoansignId(loan.getId().toString());// 标id（项目id）
												account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
												account.setUserbasicsinfo(userbasicsinfo);
												account.setAccounttype(plankService.accounttype(3L));
												// 查询余额
												crs cr2 = baoFuService.getCasbalance(userbasicsinfo.getpMerBillNo());
												userbasicsinfo.getUserfundinfo().setCashBalance(cr2.getBalance());
												userbasicsinfo.getUserfundinfo().setOperationMoney(cr2.getBalance());
												
												account.setMoney(cr2.getBalance());
												plankService.saveAccount(account);// 添加流水账余额
												userbasicsinfoService.update(userbasicsinfo);
									    }
									    LOG.error("宝付项目流标处理成功");
										return "WEB-INF/views/success";
								}catch(Exception e){
									e.printStackTrace();
									LOG.error("宝付项目流标处理成功---->平台数据处理失败---" + info.getResult()+"----->订单号----->"+ crsFlow.getOrder_id());
									return "WEB-INF/views/failure";
								}
					} else  if(crsFlow.getCode().equals("CSD333")){
								loan.setStatus(1);
								loanSignQuery.updateLoansign(loan);
								payLogService.updatePayLog(crsFlow.getOrder_id(),-1);
						        LOG.error("宝付项目流标处理失败--" + info.getResult()+"----->订单号----->"+ crsFlow.getOrder_id());
						        return "WEB-INF/views/failure";
					} else {
						 LOG.error("宝付项目流标处理失败--" + info.getResult()+"----->订单号----->"+ crsFlow.getOrder_id());
						return "WEB-INF/views/failure";
					}
			} else {
				LOG.error("非宝付项目流标返回数据--" + info.getResult()+"----->订单号----->"+ crsFlow.getOrder_id());
				return "WEB-INF/views/failure";
			}
		} else {
			return "WEB-INF/views/failure";
		}
	}
	
	
	/****
	 * 第三方担保授权同步
	 * @param request
	 * @return
	 */
	@RequestMapping("returnInaccredit.htm")
	public String returnInaccredit(ReturnInfo info,HttpServletRequest request){
		return  "WEB-INF/views/success";
	}
	
	/***
	 * 第三方担保授权异步
	 * @param request
	 * @return
	 */
	@RequestMapping("asynchronismInaccredit.htm")
	public  String asynchronismInaccredit(ReturnInfo info,HttpServletRequest request){
		if (info != null) {
			crs crsFlow = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(crsFlow.getClass().getSimpleName(), crsFlow.getClass());
			crsFlow = (crs) xs.fromXML(info.getResult());
			String Md5sign = CommonUtil.MD5(info.getResult() + "~|~"+ ParameterIps.getDes_algorithm());
			if (info.getSign().equals(Md5sign)) {
				   Escrow escrow=escrowService.queryEscrowStaffBaofu(crsFlow.getUser_id().toString());
					if (crsFlow.getCode().equals("CSD000")) {
						if(escrow.getInAccredit()!=1){
							escrow.setInAccredit(1);
							escrow.setAccreditTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
							escrowService.udapteEscrow(escrow);
							payLogService.updatePayLog(crsFlow.getUser_id().toString(),Constant.STATUES_ONE);
							LOG.error("用户授权成功--" + info.getResult()+"----->订单号----->"+ crsFlow.getUser_id());
						}
						return "WEB-INF/views/success";
					}else{
						escrow.setInAccredit(-1);
						escrow.setAccreditTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
						escrowService.udapteEscrow(escrow);
						payLogService.updatePayLog(crsFlow.getUser_id().toString(),-1);
						LOG.error("用户授权失败--" + info.getResult()+"----->订单号----->"+ crsFlow.getUser_id());
						return "WEB-INF/views/failure";
					}
			}else{
				LOG.error("非用户授权返回数据--" + info.getResult()+"----->订单号----->"+ crsFlow.getUser_id());
				return "WEB-INF/views/failure";
			}
		}else{
			return "WEB-INF/views/failure";
		}
	}
	
	/****
	 * 用户授权同步
	 * @param request
	 * @return
	 */
	@RequestMapping("returnInaccreditUser.htm")
	public String returnInaccreditUser(ReturnInfo info,HttpServletRequest request){
		return  "WEB-INF/views/success";
	}
	
	/***
	 * 用户授权异步
	 * @param request
	 * @return
	 */
	@RequestMapping("asynchronismInaccreditUser.htm")
	public  String asynchronismInaccreditUser(ReturnInfo info,HttpServletRequest request){
		if (info != null) {
			crs crsFlow = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(crsFlow.getClass().getSimpleName(), crsFlow.getClass());
			crsFlow = (crs) xs.fromXML(info.getResult());
			String Md5sign = CommonUtil.MD5(info.getResult() + "~|~"+ ParameterIps.getDes_algorithm());
			if (info.getSign().equals(Md5sign)) {
				 Paylog payLog = payLogService.queryPaylogByOrderSn(crsFlow.getUser_id().toString());
				 Userbasicsinfo userinfo = userInfoServices.queryBasicsInfoById(String.valueOf(payLog.getLoansignId()));
					if (crsFlow.getCode().equals("CSD000")) {
						if(userinfo.getIsAuthIps()!=1){
							userinfo.setIsAuthIps(Constant.STATUES_ONE);
							userinfo.setAuthIpsTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
							//理财师(被推广人)
							if(generalizeService.getGeneralizeIsAuthIps(userinfo.getId())){
								userinfo.setUserType(Constant.STATUES_SIX);
//								generalizeService.delGeneralize(userinfo);
							}
							userbasicsinfoService.update(userinfo);
							payLogService.updatePayLog(crsFlow.getUser_id().toString(),Constant.STATUES_ONE);
							/** 宝付状态修改：更新session中相关对象 */
							request.getSession().setAttribute(Constant.SESSION_USER, userinfo);
							LOG.error("用户授权成功--" + info.getResult()+"----->订单号----->"+ crsFlow.getUser_id());
							//通过被推广人id反查推广人
							Userbasicsinfo userGen = generalizeService.queryPromoterByPromotedId(userinfo.getId());
							if(userGen!=null){
								//lkl-20150811-添加员工推荐注册增加红筹币
								if(userGen.getUserType() == 2){
									hccoindetailService.saveHccoindetailNumber(userGen);
								}
								//理财师(推广人)
								if((userGen.getUserType() == 1 || userGen.getUserType() == 3) && userGen.getIsAuthIps() == 1){
									userGen.setUserType(Constant.STATUES_SIX);
									userbasicsinfoService.update(userGen);
//									generalizeService.delGeneralize(userGen);
								}
							}
						}
						return "WEB-INF/views/success";
					}else{
						userinfo.setIsAuthIps(-1);
						userinfo.setAuthIpsTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
						userbasicsinfoService.update(userinfo);
						payLogService.updatePayLog(crsFlow.getUser_id().toString(),-1);
						/** 宝付状态修改：更新session中相关对象 */
						request.getSession().setAttribute(Constant.SESSION_USER, userinfo);
						LOG.error("用户授权失败--" + info.getResult()+"----->订单号----->"+ crsFlow.getUser_id());
						return "WEB-INF/views/failure";
					}
			}else{
				LOG.error("非用户授权返回数据--" + info.getResult()+"----->订单号----->"+ crsFlow.getUser_id());
				return "WEB-INF/views/failure";
			}
		}else{
			return "WEB-INF/views/failure";
		}
	}

	/***
	 * H5用户授权异步
	 * @param request
	 * @return
	 */
	@RequestMapping("h5asynchronismInaccreditUser.htm")
	public  String h5asynchronismInaccreditUser(ReturnInfo info,HttpServletRequest request){
		if (info != null) {
			crs crsFlow = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(crsFlow.getClass().getSimpleName(), crsFlow.getClass());
			crsFlow = (crs) xs.fromXML(info.getResult());
			String Md5sign = CommonUtil.MD5(info.getResult() + "~|~"+ ParameterIps.getDes_algorithm());
			if (info.getSign().equals(Md5sign)) {
				 Paylog payLog = payLogService.queryPaylogByOrderSn(crsFlow.getUser_id().toString());
				 Userbasicsinfo userinfo = userInfoServices.queryBasicsInfoById(String.valueOf(payLog.getLoansignId()));
					if (crsFlow.getCode().equals("CSD000")) {
						if(userinfo.getIsAuthIps()!=1){
							userinfo.setIsAuthIps(Constant.STATUES_ONE);
							userinfo.setAuthIpsTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
							//理财师(被推广人)
							if(generalizeService.getGeneralizeIsAuthIps(userinfo.getId())){
								userinfo.setUserType(Constant.STATUES_SIX);
//								generalizeService.delGeneralize(userinfo);
							}
							userbasicsinfoService.update(userinfo);
							payLogService.updatePayLog(crsFlow.getUser_id().toString(),Constant.STATUES_ONE);
							/** 宝付状态修改：更新session中相关对象 */
							request.getSession().setAttribute(Constant.SESSION_USER, userinfo);
							LOG.error("用户授权成功--" + info.getResult()+"----->订单号----->"+ crsFlow.getUser_id());
							//通过被推广人id反查推广人
							Userbasicsinfo userGen = generalizeService.queryPromoterByPromotedId(userinfo.getId());
							if(userGen!=null){
								//lkl-20150811-添加员工推荐注册增加红筹币
								if(userGen.getUserType() == 2){
									hccoindetailService.saveHccoindetailNumber(userGen);
								}
								//理财师(推广人)
								if((userGen.getUserType() == 1 || userGen.getUserType() == 3) && userGen.getIsAuthIps() == 1){
									userGen.setUserType(Constant.STATUES_SIX);
									userbasicsinfoService.update(userGen);
//									generalizeService.delGeneralize(userGen);
								}
							}
						}
						return "redirect:/h5/baofooSuccess.htm";
					}else{
						userinfo.setIsAuthIps(-1);
						userinfo.setAuthIpsTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
						userbasicsinfoService.update(userinfo);
						payLogService.updatePayLog(crsFlow.getUser_id().toString(),-1);
						/** 宝付状态修改：更新session中相关对象 */
						request.getSession().setAttribute(Constant.SESSION_USER, userinfo);
						LOG.error("用户授权失败--" + info.getResult()+"----->订单号----->"+ crsFlow.getUser_id());
						return "redirect:/h5/baofooSuccess.htm";
					}
			}else{
				LOG.error("非用户授权返回数据--" + info.getResult()+"----->订单号----->"+ crsFlow.getUser_id());
				return "redirect:/h5/baofooSuccess.htm";
			}
		}else{
			return "redirect:/h5/baofooSuccess.htm";
		}
	}
	
	

	/**
	 * 项目还款页面跳转
	 * 
	 */
	@RequestMapping("returnRepaymentSign.htm")
	public String returnRepaymentSign(ReturnInfo returnInfo,
			HttpServletRequest request) {
		return  "WEB-INF/views/success";
	}
	

	/**
	 * 项目还款异步处理
	 * 
	 */
	@RequestMapping("asynchronismRepaymentSign.htm")
	public String asynchronismRepaymentSign(ReturnInfo info,
			@RequestParam("amount") Double amount, @RequestParam("id") String id) throws IOException, TemplateException {
		if (info != null) {
			crs c = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(c.getClass().getSimpleName(), c.getClass());
			c = (crs) xs.fromXML(info.getResult());

			String Md5sign = CommonUtil.MD5(c.getCode() + "~|~" + c.getMsg() + "~|~" + c.getOrder_id() + "~|~" + ParameterIps.getDes_algorithm());
			Loansign loan = loanSignQuery.getLoansignById(id);
			Userbasicsinfo userinfo = userInfoServices .queryBasicsInfoById(String.valueOf(loan .getUserbasicsinfo().getId()));
			if (c.getSign().equals(Md5sign)) {
				loan.setStatus(3);
				processingService.updateLoan(loan);
				// 流水账
				Accountinfo account = new Accountinfo();
				account.setExpenditure(0.00);
				account.setExplan("项目放款");
				account.setIncome(amount);
				account.setIpsNumber(c.getOrder_id());
				account.setLoansignId(loan.getId().toString());// 标id（项目id）
				// account.setProject_id(""); // 店铺id
				account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
				account.setUserbasicsinfo(userinfo);
				account.setAccounttype(plankService.accounttype(12L));

				crs cr = baoFuService.getCasbalance(userinfo.getpMerBillNo());

				userinfo.getUserfundinfo().setCashBalance(cr.getBalance());
				userinfo.getUserfundinfo().setOperationMoney(cr.getBalance());
				account.setMoney(cr.getBalance());
				plankService.saveAccount(account);// 添加流水账余额
				userbasicsinfoService.update(userinfo);
				if (c.getCode().equals("CSD000")) {
					return "WEB-INF/views/success";
				} else {
					return "WEB-INF/views/failure";
				}
			} else {
				LOG.error("宝付还款--" + info.getResult());
				return "WEB-INF/views/failure";
			}

		} else {
			return "WEB-INF/views/failure";
		}

	}

	/**
	 * 店铺还款页面跳转
	 * 
	 */
	@RequestMapping("returnProRepaymentSign.htm")
	public String returnProRepaymentSign(ReturnInfo returnInfo,
			HttpServletRequest request) {
		return "redirect:/";
	}

	/**
	 * 店铺还款异步处理
	 * 
	 */
	@RequestMapping("asynchronismProRepaymentSign.htm")
	public String asynchronismProRepaymentSign(ReturnInfo info,
			@RequestParam("amount") Double amount,
			@RequestParam("fhmoney") Double fhmoney,
			@RequestParam("pId") String pId) throws IOException,
			TemplateException {
		if (info != null) {
			crs c = new crs();
			XStream xs = new XStream(new DomDriver());
			xs.alias(c.getClass().getSimpleName(), c.getClass());
			c = (crs) xs.fromXML(info.getResult());

			String Md5sign = CommonUtil.MD5(c.getCode() + "~|~" + c.getMsg()
					+ "~|~" + c.getOrder_id() + "~|~"
					+ ParameterIps.getDes_algorithm());
			Shop project = shopService.queryShopById(pId);
			Userbasicsinfo userinfo = project.getUserbasicsinfo();
			Accountinfo account = new Accountinfo();

			if (c.getSign().equals(Md5sign)) {
				if (c.getCode().equals("CSD000")) {

					// 1.根据传过来的店铺Id查询该店铺的详细信息

					// 根据传过来的店铺Id查询投资该店铺的全部的详细信息
					List<ShopRecord> srlist = shopService.queryRecordList(pId);

					for (ShopRecord store : srlist) {

						Userbasicsinfo user = userInfoServices
								.queryBasicsInfoById(store.getUserbasicsinfo()
										.getId().toString());

						Fhrecord fr = new Fhrecord();
						fr.setAmount(fhmoney); // 分红总金额
						Double icon = 0.0000; // 初始化分红收益比例
						fhmoney = fhmoney - (fhmoney * 0.005); // 扣除分红金额的千分之五
						icon = fhmoney
								* (store.getTenderMoney() / project
										.getRealFunds());// 计算分红的收益比例
						java.text.DecimalFormat df = new java.text.DecimalFormat(
								"#.00");
						icon = Double.valueOf(df.format(icon));

						// 查询余额
						crs cr = baoFuService.getCasbalance(user
								.getpMerBillNo());
						user.getUserfundinfo().setCashBalance(cr.getBalance());
						user.getUserfundinfo().setOperationMoney(cr.getBalance());
						userbasicsinfoService.update(user);

						fr.setMoney(icon); // 分红收益
						fr.setProjectId(Long.valueOf(project.getId()));
						fr.setUserId(Long.valueOf(user.getId()));
						fr.setRatio(store.getTenderMoney()
								/ project.getRealFunds());
						fr.setFhTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
						shopService.saveFhRecord(fr); // 生成分红记录
					}

					// 流水账

					account.setExpenditure(0.00);
					account.setExplan("店铺还款");
					account.setIncome(amount);
					account.setIpsNumber(c.getOrder_id());
					account.setLoansignId("");// 标id（项目id）
					// account.setProject_id(project.getId().toString()); //
					// 店铺id
					account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
					account.setUserbasicsinfo(userinfo);
					account.setAccounttype(plankService.accounttype(9L));
				}
				// 查询余额
				crs cr = baoFuService.getCasbalance(userinfo.getpMerBillNo());

				if (cr.getCode().equals("CSD000")) {
					userinfo.getUserfundinfo().setCashBalance(cr.getBalance());
					userinfo.getUserfundinfo().setOperationMoney(cr.getBalance());
					account.setMoney(cr.getBalance());
					plankService.saveAccount(account);// 添加流水账余额
				}

				userbasicsinfoService.update(userinfo);
				if (c.getCode().equals("CSD000")) {
					return "WEB-INF/views/success";
				} else {
					return "WEB-INF/views/failure";
				}
			} else {
				LOG.error("宝付提现--" + info.getResult());
				return "WEB-INF/views/failure";
			}

		} else {
			return "WEB-INF/views/failure";
		}

	}

	/**
	 * 会员升级页面跳转
	 * 
	 * @param info
	 * @return
	 */
	@RequestMapping("returnTransferSign.htm")
	public String returnTransferSign(ReturnInfo info) {

		return "WEB-INF/views/success";
	}

	/**
	 * 会员升级异步处理
	 * 
	 * @param info
	 * @return
	 */
	@RequestMapping("asynchronismTransferSign.htm")
	public String transferSign(ReturnInfo info) {

		return null;
	}

}