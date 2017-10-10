package com.hc9.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.constant.HongBaoConstant;
import com.hc9.common.exception.ServicesException;
import com.hc9.common.log.LOG;
import com.hc9.common.redis.HcFinancialActivitiCache;
import com.hc9.common.redis.HcNewyearActivitiCache;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.redis.activity.year2016.month04.HcClimbTopActivityCache;
import com.hc9.common.redis.activity.year2016.month04.HcOpenCardActivityCache;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.InvestDao;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.InterestIncreaseCard;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Paylog;
import com.hc9.dao.entity.RedEnvelopeDetail;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.Action;
import com.hc9.model.BidInfo;
import com.hc9.model.InvestLoanRecordVo;
import com.hc9.model.InvestVo;
import com.hc9.model.crs;

/** 宝付投资服务 */
@Service
public class BaoFuInvestService {
	@Resource
	private HibernateSupport dao;
	
	@Resource
	private RegistrationService registrationService;
	
	@Resource
	private LoanSignQuery loanSignQuery;
	
	@Resource
	private BaoFuService baoFuService;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	private UserInfoServices userInfoServices;
	
	@Resource
	private RedEnvelopeDetailService redEnvelopeDetailService;
	
	@Resource
	private InterestIncreaseCardService increaseCardService;
	
	@Resource
	private PayLogService payLogService;
	
	@Resource
	private UserInfoQuery userInfoQuery;
	
	@Resource
	private PlankService plankService;
	
	@Resource
	private HccoindetailService hccoindetailService;
	
	@Resource
	private GeneralizeService generalizeService;
	
	@Resource
	private GeneralizeMoneyServices generalizeMoneyServices;
	
	@Resource
	private HcFinancialActivitiCache hcFinancialActivitiCache;
	
	@Resource
	private ProcessingService processingService;
	
	@Resource
	private SmsService smsService;
	
	@Resource
	private DspService dspService;
	
	@Resource
	private HcMonkeyActivitiCache hcMonkeyActivitiCache;
	
	@Resource
	private InvestDao investDao;
	
	/** 校验相关参数并保存相关投资记录 */
	public Map<String, Object> validAndSaveInvestRecord(String pcode, String tpwd, 
			Long loanId, Double priority, Double middle, Double after, Long redId, Long cardId, 
			Integer webOrApp, HttpServletRequest request) {
		// 取验证码
		String validate = (String) request.getSession().getAttribute("user_tend");
		// 判断验证码是否正确
		if (validate == null || !validate.equalsIgnoreCase(pcode)) {
			return generateAppData("1", "验证码不正确,投资失败！");
		}
		request.getSession().removeAttribute("user_tend");
		
		// 获取当前用户
		Userbasicsinfo userbasicsinfo = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
		// 验证登录是否成功
		Userbasicsinfo user = registrationService.loginTransPassword(userbasicsinfo.getId(), tpwd);
		if (user == null) {
			return generateAppData("1", "支付密码错误,投资失败！");
		}

		if (redId != null && cardId != null) {
			return generateAppData("1", "不能同时使用加息券和红包,投资失败！");
		}
		
		if (user.getpMerBillNo() == null || user.getpMerBillNo().equals("")) {
			return generateAppData("1", "尚未开通宝付,投资失败！");
		}
				
		// 查询loansign表数据
		Loansign loan = loanSignQuery.getLoansignById(loanId.toString());
		// 判断剩余金额是否等0
		if (loan.getRestMoney() == 0) {
			return generateAppData("1", "当前项目剩余金额为0,投资失败！");
		}
		// 判断是否投自己发的项目
		if (loan.getUserbasicsinfo().getId().equals(user.getId())) {
			return generateAppData("1", "融资人和投资人不能是同一个人,投资失败！");
		}
		
		Double sumMoney = 0.00; //投资总额
		Map<Integer, Double> loanMap = new HashMap<Integer, Double>();
		if (priority != null) {
			loanMap.put(1, priority);
			sumMoney = Arith.add(sumMoney, priority);
		}
		if (middle != null) {
			loanMap.put(2, middle);
			sumMoney = Arith.add(sumMoney, middle);
		}
		if (after != null) {
			loanMap.put(3, after);
			sumMoney = Arith.add(sumMoney, after);
		}
		Userbasicsinfo userbas;
		try {
			crs cr = baoFuService.getCasbalance(user.getpMerBillNo());
			if(cr != null) {
				user.getUserfundinfo().setCashBalance(cr.getBalance()); // 宝付的余额
				user.getUserfundinfo().setOperationMoney(cr.getBalance());
				userbasicsinfoService.update(user);
				userbas = userInfoServices.queryBasicsInfoById(user.getId().toString());
			} else {
				return generateAppData("1", "查询余额过程中报错,投资失败！");
			}
		} catch (Exception e1) {
			LOG.error(loan.getId()+"查询余额报错,错误：",e1);
			return generateAppData("1", "查询余额过程中报错,投资失败！");
		}
		
		// 获取费用表的信息
		Costratio costratio = loanSignQuery.queryCostratio();
		// 除去夹层和劣后 -如果优先投资额   > 账户余额
		Double calc_after = userbas.getUserfundinfo().getCashBalance()-(middle!=null?middle:0D)-(after!=null?after:0D);
		if (priority != null && calc_after < priority && redId != null) {
			RedEnvelopeDetail redEnvelope = redEnvelopeDetailService.getRedEnvelopeDetail(redId);
			if (redEnvelope != null) {  // 如果有使用红包
				Double real_useMoney = calc_after + redEnvelope.getMoney();   // 当前可用金额 + 当前使用红包金额
				if (real_useMoney < priority) {  
					return generateAppData("1", "查询余额过程中报错,投资失败！");
				}
			}
		} else {
			//判断资金总额是否小于投资金额
			if(userbas.getUserfundinfo().getCashBalance() < sumMoney){
				return generateAppData("1", "账户余额不足!");
			}
					
			//判断投资总额是否小于剩余金额
			if(loan.getRestMoney() < sumMoney){
				return generateAppData("1", "当前项目可投金额不足(当前项目剩余" + loan.getRestMoney() +"元)!");
			}
		}
		
		// 判断优先剩余金额+夹层剩余金额是否>=购买金额
		Double sumPrioRestAddMiddle = Arith.add(loan.getPrioRestMoney(), loan.getMidRestMoney());
		//奖励红包
		RedEnvelopeDetail redEnvelopeDetail = null;
		//lkl-20150825-加息券处理
		InterestIncreaseCard increaseCard = null;
		if (priority != null && priority > 0) {
			if (sumPrioRestAddMiddle < priority) {
				return generateAppData("1", "投资失败,请稍后重试!");
			}
			if(redId != null){
				redEnvelopeDetail = redEnvelopeDetailService.getRedEnvelope(redId, user);
				if(redEnvelopeDetail != null){
					 if(redEnvelopeDetail.getLowestUseMoney() > priority){
						 return generateAppData("1", "当前红包最低投资金额为" 
								 + redEnvelopeDetail.getLowestUseMoney() +"元)，投资失败!");
					 }
				} else {
					 return generateAppData("1", "投资失败,请稍后重试!");
				}
			}
			if(cardId != null){
				increaseCard = increaseCardService.getIncreaseCard(cardId, userbas.getId());
				if(increaseCard != null){
					if(increaseCard.getLowestUseMoney() > priority){
						return generateAppData("1", "当前加息券最低投资金额为" 
								 + increaseCard.getLowestUseMoney() +"元)，投资失败!");
					}
				} else {
					 return generateAppData("1", "投资失败,请稍后重试!");
				}
			}
		}
		
		if (middle != null && middle > 0) {
			if (sumPrioRestAddMiddle < middle) {
				return generateAppData("1", "投资失败,请稍后重试!");
			}
		}
		
		/** 劣后相关逻辑判断处理 */
		if (after != null && after > 0) {
			if (user.getIsorgperson() != 1) { // 判断是否是机构投资人
				return generateAppData("1", "投资失败,请稍后重试!");
			}
			// 判断劣后的金额是否等于0
			if (loan.getAfterRestMoney() < after) {
				return generateAppData("1", "投资失败,请稍后重试!");
			}
		}
		
		Double money = 0.00;
		Integer subType = 0;
		boolean resultFlag = false;
		InvestVo investVo = new InvestVo();
		/** 投资记录 */
		int i = 0;
		List<InvestLoanRecordVo> loanRecordList = new ArrayList<InvestLoanRecordVo>();
		for(Map.Entry<Integer, Double> entry : loanMap.entrySet()) {
			/** 投资记录相关信息 */
			InvestLoanRecordVo investLoanRecordVo = new InvestLoanRecordVo();
			money = entry.getValue();
			subType = entry.getKey();
			
			boolean isDetail = false;
			if(subType == 1  && redEnvelopeDetail != null){
				if(redEnvelopeDetail.getLowestUseMoney() <= money){
					isDetail = true;
					investLoanRecordVo.setRedDetail(isDetail);
					investLoanRecordVo.setRedEnvelopeDetail(redEnvelopeDetail);
				}
			}
			
			boolean isCardId = false;
			if(subType == 1  && increaseCard != null){
				if(increaseCard.getLowestUseMoney() <= money){
					isCardId = true;
					investLoanRecordVo.setAddCardId(isCardId);
					investLoanRecordVo.setIncreaseCard(increaseCard);
				}
			}

			Double subMoney = 0.00;
			Integer isType = 0; // 0-默认 1-优先转夹层 2-夹层转优先
			// 根据投标类型进行处理
			/** 修改标的总的剩余金额 */
			String sql = "update loansign set rest_money=rest_money-" + money + " where id=? and rest_money>=?";
			int updateNum = dao.executeSql(sql, loan.getId(), money);
			if(updateNum < 1) {
				throw new ServicesException("项目总剩余金额不足,投资失败，标id:" + loan.getId());
			}
			if (subType == 3) { // 劣后
				/** 修改标的劣后剩余金额 */
				sql = "update loansign set after_rest_money=after_rest_money-" + money + 
						" where id=? and after_rest_money>=?";
				updateNum = dao.executeSql(sql, loan.getId(), money);
				if(updateNum < 1) {
					throw new ServicesException("项目劣后剩余金额不足,投资失败，标id:" + loan.getId());
				}
			} else {
				if (sumPrioRestAddMiddle >= money) {
					if (subType == 1) { // 优先
						double prioRestMoney = investDao.queryPrioRestMoneyByLoanId(loanId);
						if (prioRestMoney < money) { // 夹层转优先
							subMoney = Arith.sub(money, prioRestMoney); // 购买金额-优先剩余金额=差额
							sql = "update loansign set middle=middle-" + subMoney + 
									",mid_rest_money=mid_rest_money-" + subMoney + 
									",priority=priority+" + subMoney + ",prio_rest_money=0 where id=? and mid_rest_money >=?";
							updateNum = dao.executeSql(sql, loan.getId(), subMoney);
							if(updateNum < 1) {
								throw new ServicesException("夹层转优先项目夹层剩余金额不足,投资失败，标id:" + loan.getId());
							}
							isType = 2;
						} else {
							/** 修改优先剩余金额 */
							sql = "update loansign set prio_rest_money=prio_rest_money-" + money + 
									" where id=? and prio_rest_money>=?";
							updateNum = dao.executeSql(sql, loan.getId(), money);
							if(updateNum < 1) {
								throw new ServicesException("项目优先剩余金额不足,投资失败，标id:" + loan.getId());
							}
						}
					} else if (subType == 2) { // 夹层
						double midRestMoney = investDao.queryMidRestMoneyByLoanId(loanId);
						if (midRestMoney < money) { // 优先转夹层
							subMoney = Arith.sub(money, midRestMoney); // 购买金额-夹层剩余金额=差额
							sql = "update loansign set prio_rest_money=prio_rest_money-" + subMoney + 
									",priority=priority-" + subMoney + 
									",middle=middle+" + subMoney + ",mid_rest_money=0 where id=? and prio_rest_money>=?";
							updateNum = dao.executeSql(sql, loan.getId(), subMoney);
							if(updateNum < 1) {
								throw new ServicesException("优先转夹层项目优先剩余金额不足,投资失败，标id:" + loan.getId());
							}
							isType = 1;
						} else {
							/** 修改标的夹层剩余金额 */
							sql = "update loansign set mid_rest_money=mid_rest_money-" + money + 
									" where id=? and mid_rest_money>=?";
							updateNum = dao.executeSql(sql, loan.getId(), money);
							if(updateNum < 1) {
								throw new ServicesException("项目夹层剩余金额不足,投资失败，标id:" + loan.getId());
							}
						}
					}
				}
			}
			
			// 投标流水号
			i = i + 1;
			String orderNum = "TB" + StringUtil.getDateTime(userbas.getId(), loan.getId()) + i;
			// 计算服务费
			Double fee = Arith.mul(money, costratio.getLoanInvestment());
			// 保存购买记录信息
			Loanrecord loanrecord = new Loanrecord();
			Double redMoney=money; //实际金额
			loanrecord.setIsPrivilege(userInfoQuery.isPrivilege(userbas) ? Constant.STATUES_ONE
							: Constant.STATUES_ZERO); // 投标时，记录该投资者是否vip
			loanrecord.setIsSucceed(Constant.STATUES_ZERO); // 预购信息为 0 购买成功为 1
			loanrecord.setLoansign(loan);
			loanrecord.setFee(fee);
			loanrecord.setSubType(subType);
			loanrecord.setIsType(isType); // 0-默认 1-优先转夹层 2-夹层转优先
			loanrecord.setTenderMoney(money);
			loanrecord.setTenderTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
			loanrecord.setUserbasicsinfo(user);
			loanrecord.setLoanType(loan.getType());
			loanrecord.setWebOrApp(1); // 1-web 2-app
			if(webOrApp != null)
				loanrecord.setWebOrApp(webOrApp); // 1-web 2-app
			loanrecord.setOrder_id(orderNum);
			loanrecord.setSubMoney(subMoney);// 差额
			if(isDetail){
				loanrecord.setRedEnvelopeMoney(redEnvelopeDetail.getMoney());
				redMoney=Arith.sub(money, redEnvelopeDetail.getMoney());
			}
			dao.save(loanrecord);
			// 添加个人购买信息
			List<Action> listAction = new ArrayList<Action>();
			Action action = new Action(user.getpMerBillNo(), user.getName(), redMoney);
			listAction.add(action);

			// 添加项目的信息
			BidInfo bidInfo = new BidInfo(loan, orderNum, fee, "1", listAction);
			String bidinfoXml = "";
			try {
				bidinfoXml = ParseXML.bidInfoXML(bidInfo);
			} catch(Exception e) {
				LOG.error("生产订单的投资报文信息过程中出错，订单号为" + orderNum, e);
				throw new RuntimeException("投标信息转换过程中出错！");
			}
			// 添加日志
			payLogService.savePayLog(bidinfoXml, user.getId(),
					loan.getId(), 4, orderNum, Double.valueOf(fee), 0.00, redMoney); // 保存xml报文
			//只有优先才可以使用
			if(isDetail) {
				redEnvelopeDetail.setLoanrecord_id(loanrecord.getId());
				redEnvelopeDetail.setUseFlag(Constant.STATUES_TWO);
				redEnvelopeDetailService.uptRedEnvelope(redEnvelopeDetail);
			}
			if(isCardId) {
				increaseCard.setLoanrecordId(loanrecord.getId());
				increaseCard.setUseFlag(Constant.STATUES_TWO);
				increaseCardService.uptIncreaseCard(increaseCard);
			}
			investLoanRecordVo.setBidinfoXml(bidinfoXml);
			investLoanRecordVo.setLoanrecord(loanrecord);
			loanRecordList.add(investLoanRecordVo);
			resultFlag = true;
		}
		if(resultFlag == true) {
			investVo.setUserId(userbas.getId());
			investVo.setLoanId(loanId);
			investVo.setPriorityMoney(priority);
			investVo.setMiddleMoney(middle);
			investVo.setAfterMoney(after);
			investVo.setLoan(loan);
			investVo.setLoanRecordList(loanRecordList);
			investVo.setCostratio(costratio);
			investVo.setUser(userbas);
			Map<String, Object> resultMap = generateAppData("0", "投资成功!");
			resultMap.put("investVo", investVo);
			return resultMap;
		} else {
			return generateAppData("1", "投资失败,请稍后重试!");
		}
	}
	
	/** 投资成功后相关记录处理 */
	public void handleRecordAfterInvestSuccess(InvestVo investVo, InvestLoanRecordVo investLoanRecordVo, 
			HttpServletRequest request, boolean isLast) {
		Loansign loan = investVo.getLoan();
		Loanrecord loanrecord = investLoanRecordVo.getLoanrecord();
		boolean isDetail= investLoanRecordVo.isRedDetail();
		//奖励红包
		RedEnvelopeDetail redEnvelopeDetail = investLoanRecordVo.getRedEnvelopeDetail();
		boolean isCardId = investLoanRecordVo.isAddCardId();
		//lkl-20150825-加息券处理
		InterestIncreaseCard increaseCard = investLoanRecordVo.getIncreaseCard();
		String orderNum = loanrecord.getOrder_id();//订单号
		Paylog payLog = payLogService.queryPaylogByOrderSn(orderNum);
		Costratio costratio = investVo.getCostratio();
		Userbasicsinfo user = investVo.getUser();
		if (loanrecord.getIsSucceed() == 0) {
			boolean sendFullSmsFlag = false;//是否发送满标短信
			if (isLast) {
				LOG.error(loan.getId() + "开始判断是否满标并更新相关状态！");
				String sql = "update loansign set status=2,full_time=? where id=? and rest_money<=0";
				int updateNum = dao.executeSql(sql, 
						DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"), loan.getId());
				if(updateNum > 0) {
					sendFullSmsFlag = true;
				}
			}
			if(isDetail){
				if(redEnvelopeDetail != null) {
					if(redEnvelopeDetail.getMoney() != null) {
						String sql = "update loansign set redEnvelopeMoney=redEnvelopeMoney+" + 
								redEnvelopeDetail.getMoney() + " where id=?";
						dao.executeSql(sql, loan.getId());
					}
					redEnvelopeDetail.setUseFlag(Constant.STATUES_ONE);
					redEnvelopeDetail.setConsumeTime(loanrecord.getTenderTime());
					redEnvelopeDetailService.uptRedEnvelope(redEnvelopeDetail);
				}
			}
			//lkl-20150825
			if(isCardId){
				if(increaseCard != null) {
					increaseCard.setUseFlag(Constant.STATUES_ONE);
					 increaseCard.setConsumeTime(loanrecord.getTenderTime());
					 increaseCardService.uptIncreaseCard(increaseCard);
				}
			}
			String updateTime = DateUtils.format("yyyy-MM-dd HH:mm:ss");
			loanrecord.setIsSucceed(1);
			loanrecord.setUpdateTime(updateTime);
			// 投资按100元计1分
			Integer product = (int) (payLog.getAmount() / 100);
			plankService.saveAutointegralBuyProject(user,payLog.getAmount(),loanrecord.getSubType()); // 保存积分记录
			// 余额查询
			loanrecord.getUserbasicsinfo().setUserintegral(user.getUserintegral() + product); // 积分计算
			// 银行流水
			Accountinfo account = new Accountinfo();
			account.setExpenditure(payLog.getAmount());
			if (loanrecord.getLoanType() == 2) {
				account.setExplan("项目购买");
			} else if (loanrecord.getLoanType() == 3) {
				account.setExplan("天标购买");
			}
			account.setIncome(0.00);
			account.setIpsNumber(orderNum);
			account.setLoansignId(loan.getId().toString());
			account.setFee(payLog.getFee());
			account.setTime(loanrecord.getTenderTime());
			account.setUserbasicsinfo(user);
			if (loanrecord.getLoanType() == 2) {
				account.setAccounttype(plankService.accounttype(5L));
			} else if (loanrecord.getLoanType() == 3) {
				account.setAccounttype(plankService.accounttype(15L));
			}
			user.getUserfundinfo().setCashBalance(Arith.sub(user.getUserfundinfo().getCashBalance(), payLog.getAmount()));
			account.setMoney(user.getUserfundinfo().getCashBalance());// 流水记录表
			dao.update(loanrecord);
			dao.save(account);
		    dao.update(user);
			payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
			
			//通过被推广人id反查推广人
			Userbasicsinfo userGen = generalizeService.queryPromoterByUidForInvest(loanrecord.getUserbasicsinfo().getId());
			
			//lkl-20150811-添加员工推荐投资红筹币
			if(userGen!=null){
				if (userGen.getUserType() == 2) {
					hccoindetailService.saveHccoindetail(loanrecord, costratio.getHccoinRate(), userGen);
				}
			}
			/** 红筹理财师活动标识 */
			boolean financialFlag = false;
			// 保存佣金
			if(costratio.getGeneralizeState()==1){
				generalizeService.saveGeneralizeMoney(loanrecord);
			} else {
				if(userGen != null){
					if (userGen.getUserType() == 2) {
						generalizeMoneyServices.saveGeneralizemoney(loanrecord,costratio.getBusiness(), userGen.getId(),Constant.STATUES_TWO);
					} else if(userGen.getUserType() == 4){
						generalizeMoneyServices.saveGeneralizemoney(loanrecord,0.015, userGen.getId(),Constant.STATUES_TWO);
					} else if(userGen.getUserType() == 6){
						if(userGen.getIsAuthIps() == 1 && user.getIsAuthIps() == 1){
							// 理财师
							generalizeMoneyServices.saveGeneralizemoney(loanrecord,costratio.getFinancial(), userGen.getId(),Constant.STATUES_SIX);
							financialFlag = true;
							//financial(userGen,loanrecord.getTenderMoney());
						}
					}
				}
				if(loanrecord.getUserbasicsinfo().getUserType()==2){
					generalizeMoneyServices.saveGeneralizemoney(loanrecord,costratio.getBusiness(), loanrecord.getUserbasicsinfo().getId(),Constant.STATUES_TWO);
				}else if(loanrecord.getUserbasicsinfo().getUserType()==4){
					generalizeMoneyServices.saveGeneralizemoney(loanrecord,0.015, loanrecord.getUserbasicsinfo().getId(),Constant.STATUES_TWO);
				}
		    }
			
			/** 满标短信通知及活动相关内容 */
			activityAndFullBidInfo(investLoanRecordVo, user, investVo, loanrecord, userGen, request, financialFlag, sendFullSmsFlag);
		}
		LOG.error("宝付项目购买处理成功");
		
	}
	
	/** 活动及满标短信通知 */
	public void activityAndFullBidInfo(InvestLoanRecordVo investLoanRecordVoParam, Userbasicsinfo userParam, 
			InvestVo investVoParam, Loanrecord loanrecordParam, Userbasicsinfo userGenParam, 
			HttpServletRequest requestParam, boolean financialFlagParam, boolean sendFullSmsFlagParam) {
		try {
			LOG.error("投资过程中异步更新相关信息开始！");
			final InvestVo investVo = investVoParam;
			final Loanrecord loanrecord = loanrecordParam;
			final Userbasicsinfo userGen = userGenParam;
			final HttpServletRequest request = requestParam;
			final boolean financialFlag = financialFlagParam;
			final Loansign loan = investVo.getLoan();
			final Userbasicsinfo user = userParam;
			final InvestLoanRecordVo investLoanRecordVo = investLoanRecordVoParam;
			final boolean sendFullSmsFlag = sendFullSmsFlagParam;
			CacheManagerService.threadPoolExecutor.submit(
                	new Thread() {
                		public void run() {
                			LOG.error("投资更新缓存开始：" + Thread.currentThread());
                			
                			/** 活动相关业务逻辑处理 **/
                			activityBusinessLogic(investLoanRecordVo, user, financialFlag, userGen, 
                					loanrecord.getTenderMoney(), loan, loanrecord);
                			/** 发送满标短信 */
                			if (sendFullSmsFlag) {
                				sendFullBidSmsAfterInvestSuccess(investVo, loan);
                			}
                			/** 投资成功给投资人发送短信 */
                			sendSmsToInvestorAfterSuccess(user, loan, loanrecord);
                			
                			LOG.error("投资更新缓存结束：" + Thread.currentThread());
                			
                			Userbasicsinfo user = investVo.getUser();
                			//渠道信息推送：
							dspService.channelSwitch(request, HongBaoConstant.USER_INVEST,
									//统一投资参数，0标id，1订单号，2订单时间，3数量，4价格，5标明，6投资id，7用户id
									new String[]{
									loan.getId().toString(),
									loanrecord.getOrder_id().toString(), 
									loanrecord.getTenderTime(),
									String.valueOf(loanrecord.getTenderMoney()/100), 
									String.valueOf(loanrecord.getTenderMoney()),
									loanrecord.getLoansign().getName(),
									String.valueOf(loanrecord.getId()),
									String.valueOf(user.getId())
							});
                		}
                	}
                );
		 } catch(Exception e) {
			 LOG.error("投资成功后更新缓存出错！", e);
		 }
	}
	
	/** 发送满标短信 */
	public void sendFullBidSmsAfterInvestSuccess(InvestVo investVo, Loansign loan) {
		try {
			Costratio costratio = investVo.getCostratio();
			Map<String, String> map = new HashMap<String, String>();
			map.put("loanNum", loan.getName());
			String content = smsService.getSmsResources("check-fullBid.ftl", map);
			int trigger = Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
			String[] phones = costratio.getBidPhone().split(",");
			for(int i = 0; i < phones.length; i++) {
				smsService.chooseSmsChannel(trigger, content, phones[i]);
			}
		} catch(Exception e) {
			LOG.error("投资过程中发送满标短信失败！", e);
		}
	}
	
	/** 投资成功后给投资人发送投资成功提醒短信 */
	public void sendSmsToInvestorAfterSuccess(Userbasicsinfo user, Loansign loan, Loanrecord loanrecord) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			String userName = loanrecord.getUserbasicsinfo().getName();
			String loanName = loan.getName();
			int investType = loanrecord.getSubType();//1、优先；2、夹层；3、劣后；
			String loanType = "优先";
			if(investType == 2) {
				loanType = "夹层";
			} else if(investType == 3) {
				loanType = "劣后";
			}
			double investMoney = loanrecord.getTenderMoney();
			/**1-店铺  2-项目 3-天标 4-债权转让*/
			Integer type = loan.getType();
			String reMonth = loan.getRemonth() + "个月";
			if(type.intValue() == 3) {//天标
				reMonth = loan.getRemonth() + "天";
			}
			map.put("userName", userName);
			map.put("loanName", loanName);
			map.put("loanType", loanType);
			map.put("investMoney", "" + investMoney);
			map.put("reMonth", reMonth);
			String content = smsService.getSmsResources("invest/invest-investor.ftl", map);
			int trigger = Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
			String phone = loanrecord.getUserbasicsinfo().getUserrelationinfo().getPhone();
			smsService.chooseSmsChannel(trigger, content, phone);
		} catch(Exception e) {
			LOG.error("投资过程中给投资人" + user.getName() + "发送投资成功提醒短信失败！", e);
		}
	}
	
	/** 活动相关逻辑 */
	public void activityBusinessLogic(InvestLoanRecordVo investLoanRecordVo, Userbasicsinfo user, 
			boolean financialFlag, Userbasicsinfo userGen, Double tenderMoney, 
			Loansign loan, Loanrecord loanrecord) {
		int investType = loanrecord.getSubType();//1、优先；2、夹层；3、劣后；
		Double priority = 0.0;
		Double middle = 0.0;
		if(investType == 1) {
			priority = loanrecord.getTenderMoney();
		}
		if(investType == 2) {
			middle = loanrecord.getTenderMoney();
		}
		// 调用新年抽奖送抽奖次数方法
//		lotteryGiveTime(user.getId(), investType, priority, middle, userGen);
		// 调用新春猴给力活动方法
		moneyYearTime(user, investType, priority, middle, loan, loanrecord.getId());
		if(financialFlag) {
			financial(userGen, tenderMoney);
		}
		
		/** 登顶活动 */
		HcClimbTopActivityCache.increaseUserTotalInvest(user.getId(), loan.getType(), loan.getRemonth(), 
				loanrecord.getSubType(), loanrecord.getTenderMoney());
		final boolean isDetail= investLoanRecordVo.isRedDetail();
		final boolean isCardId = investLoanRecordVo.isAddCardId();
		/** 翻牌抽奖次数 */
		HcOpenCardActivityCache.incrOpenCardLotteryNum(user.getId(), investType, 
				isDetail, isCardId, loanrecord.getTenderMoney(), loanrecord.getId());
	}
	
	/** 宝付处理失败后对相关记录进行处理 */
	public void handleRecordAfterInvestFailure(InvestVo investVo, InvestLoanRecordVo investLoanRecordVo) {
		Loansign loan = investVo.getLoan();
		Loanrecord loanrecord = investLoanRecordVo.getLoanrecord();
		InterestIncreaseCard increaseCard = investLoanRecordVo.getIncreaseCard();
		String orderNum = loanrecord.getOrder_id();
		if (loanrecord.getIsSucceed() == 0) {
			/** 投资失败还原标的剩余金额 */
			String sql = "update loansign set rest_money=rest_money+" + loanrecord.getTenderMoney() + " where id=?";
			dao.executeSql(sql, loan.getId());
			if (loanrecord.getSubType() == 1) { // 优先
				if (loanrecord.getIsType() == 0) { // 默认
					/** 修改优先剩余金额 */
					sql = "update loansign set prio_rest_money=prio_rest_money+" + loanrecord.getTenderMoney() + " where id=?";
					dao.executeSql(sql, loan.getId());
				} else if (loanrecord.getIsType() == 2) { // 夹层转优先
					Double moneyNum = Arith.sub(loanrecord.getTenderMoney(),loanrecord.getSubMoney()); // 购买金额-差额=优先剩余金额
//					loan.setMiddle(Arith.add(loan.getMiddle(),loanrecord.getSubMoney())); // 夹层总额+差额
//					loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(),loanrecord.getSubMoney())); // 夹层剩余金额+差额
//					loan.setPriority(Arith.sub(loan.getPriority(), loanrecord.getSubMoney())); // 优先总额-差额
//					loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(), moneyNum)); // 优先剩余金额
					sql = "update loansign set middle=middle+" + loanrecord.getSubMoney() + 
							",mid_rest_money=mid_rest_money+" + loanrecord.getSubMoney() + 
							",priority=priority-" + loanrecord.getSubMoney() + 
							",prio_rest_money=prio_rest_money+" + moneyNum + " where id=?";
					dao.executeSql(sql, loan.getId());
				}
			} else if (loanrecord.getSubType() == 2) { // 夹层
				if (loanrecord.getIsType() == 0) {
					/** 修改标的夹层剩余金额 */
					sql = "update loansign set mid_rest_money=mid_rest_money+" + loanrecord.getTenderMoney() + " where id=?";
					dao.executeSql(sql, loan.getId());
				} else if (loanrecord.getIsType() == 1) { // 优先转夹层
					Double moneyNum = Arith.sub(loanrecord.getTenderMoney(),loanrecord.getSubMoney()); // 购买金额-差额=夹层剩余金额
//					loan.setPriority(Arith.add(loan.getPriority(),loanrecord.getSubMoney())); // 优先总金额+差额
//					loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(),loanrecord.getSubMoney())); // 优先剩余总金额+差额
//					loan.setMiddle(Arith.sub(loan.getMiddle(),loanrecord.getSubMoney())); // 夹层总额-差额
//					loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(), moneyNum)); // 夹层剩余金额
					
					sql = "update loansign set prio_rest_money=prio_rest_money+" + loanrecord.getSubMoney() + 
							",priority=priority+" + loanrecord.getSubMoney() + 
							",middle=middle-" + loanrecord.getSubMoney() + 
							",mid_rest_money=mid_rest_money+" + moneyNum + " where id=?";
					dao.executeSql(sql, loan.getId());
				}
			} else if (loanrecord.getSubType() == 3) { // 劣后
				/** 修改劣后剩余金额 */
				sql = "update loansign set after_rest_money=after_rest_money+" + loanrecord.getTenderMoney() + " where id=?";
				dao.executeSql(sql, loan.getId());
			}
			/** 修改投资记录的状态为失败 */
			sql = "update loanrecord set isSucceed=-1,updateTime=? where id=?";
			dao.executeSql(sql, DateUtils.format("yyyy-MM-dd HH:mm:ss"), loanrecord.getId());
			if(loanrecord.getRedEnvelopeMoney()>0){
				/** 标的红包金额在投资成功时变更即可 */
				redEnvelopeDetailService.updateRed(loanrecord, Constant.STATUES_ZERO);
			}
			//更新加息劵-lkl-20150825
			if(increaseCard!=null){
				 increaseCard.setUseFlag(Constant.STATUES_ZERO);
				 increaseCard.setLoanrecordId(null);
				 increaseCardService.uptIncreaseCard(increaseCard);
			}
			
			// 更新支付报文信息
			payLogService.updatePayLog(orderNum, -1);
			LOG.error("宝付项目购买处理失败");
		}
	}
	
	/** 新年抽奖活动按规则送抽奖次数 */
	private void lotteryGiveTime(long userId, int investType, Double priority,Double middle, 
			Userbasicsinfo referrer) {
		if(HcNewyearActivitiCache.validCurrentDate(new Date()) >= 0) {
			// 赠送投资人抽奖次数
			HcNewyearActivitiCache.giveLotteryChanceNumForInvest(userId, investType, priority,middle);
			// 活动期间注册并产生首投总送推介人一次抽奖机会
			if(referrer != null) {
				// 是否活动期间注册
				String beginTime = HcNewyearActivitiCache.getActiveBeginDate();
				String endTime = HcNewyearActivitiCache.getActiveEndDate();
				if(isUserRegisterdInActivityTimeArea(userId, beginTime, endTime)) {
					// 是否是首次投资
					boolean isTrue = getLoanRecord(userId);
					if(isTrue) {
						HcNewyearActivitiCache.increasePermanentLotteryChance(referrer.getId(), 1);
					}
				}
			}
		}
	}
	
	/** 判断用户是否在活动期间注册 */
	public boolean isUserRegisterdInActivityTimeArea(Long userId, String beginTime, String endTime){
		String sql="select * from userbasicsinfo where  id=? and DATE_FORMAT(createTime, '%Y-%m-%d')>=DATE_FORMAT('" 
			+ beginTime + "', '%Y-%m-%d')  AND DATE_FORMAT(createTime, '%Y-%m-%d')<=DATE_FORMAT('" 
			+ endTime + "', '%Y-%m-%d') ";
		List<Userbasicsinfo> list = dao.findBySql(sql, Userbasicsinfo.class, userId);
		if(list.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/***
	 * 判断是否已购买过
	 * @param userId
	 * @return
	 */
	public boolean getLoanRecord(Long userId){
		String sql="select * from loanrecord where userbasicinfo_id=? and isSucceed=1";
		List<Loanrecord> listLoanrecord=dao.findBySql(sql, Loanrecord.class, userId);
		if(listLoanrecord.size()==1){
			return true;
		}else{
			return false;
		}
	}
	
	/** 新春猴给力活动 */
	private void moneyYearTime(Userbasicsinfo user, int investType, Double priority, Double middle, Loansign loan, Long loanRecordId) {
		// 优先和夹层
		if(investType == 1 || investType == 2) {
			Double investMoney = investType == 1 ? priority : middle;
			// 活动一:一鸣惊人、一锤定音活动
			if(loan.getActivityStatus() != null && loan.getActivityStatus()==1) {
				hcMonkeyActivitiCache.activityMonkeyMax(loan, user, investMoney, loanRecordId);
			}
	
//			// 活动二（周榜）/三（总榜）
//			if(HcMonkeyActivitiCache.validCurrentDate(new Date()) >= 0) {
//				hcMonkeyActivitiCache.activityMonkeyWeek(loan, user, investMoney, loanRecordId);
//				hcMonkeyActivitiCache.activityMonkeyTotal(loan, user, investMoney, loanRecordId);
//			}
		}
	}
	
	/** 理财师活动 **/
	private void financial(Userbasicsinfo user, Double priority) {
		// 现金奖励
		if(HcFinancialActivitiCache.validCurrentDate(new Date()) >= 0) {
			hcFinancialActivitiCache.activityFinancial(user, priority);
		}
	}
	
	/**
	 * 封装返回参数
	 * @param rcode 操作类型 0成功 1失败 -1失效
	 * @param msg 返回信息
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> generateAppData(String code, String msg) {
		Map<String, Object> result = new HashMap<>();
		result.put("code", code);
		result.put("msg", msg);
		return result;
	}
}