package com.hc9.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hc9.common.constant.Constant;
import com.hc9.common.constant.PayURL;
import com.hc9.common.util.Arith;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Automatic;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Paylog;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Userfundinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.Action;
import com.hc9.model.BidInfo;
import com.hc9.model.crs;
import com.hc9.service.sms.ym.BaseSmsService;

//TODO automaticRelease 的if (loansign.getType() != 3 判断要重新做
/***
 * 自动投标设置
 * 
 * @author frank 2014-1-2
 * 
 * 
 */
@Service
@Transactional
public class AutomaticService {

	/** commonDao */
	@Resource
	private HibernateSupport commonDao;

	/** userInfoQuery */
	@Resource
	private UserInfoQuery userInfoQuery;

	/** loanSignFund */
	@Autowired
	private LoanSignFund loanSignFund;

	@Resource
	private UserInfoServices userInfoServices;

	@Resource
	private LoanSignQuery loanSignQuery;

	@Resource
	private PayLogService payLogService;

	@Resource
	private PlankService plankService;

	@Resource
	private UserbasicsinfoService userbasicsinfoService;

	@Resource
	private BaoFuService baoFuService;

	@Resource
	private ProcessingService processingService;

	@Resource
	private LoanrecordService loanrecordService;

	@Resource
	private GeneralizeService generalizeService;

	@Resource
	private BonusService bonusService;

	@Resource
	private BaseSmsService baseSmsService;

	@Resource
	private SmsService smsService;

	List<NameValuePair> nvps;

	/**
	 * 普通标发布时调用的方法*******只针对于普通标
	 * 
	 * @param loansign
	 *            借款标
	 * @return 是否成功
	 */
	public boolean automaticRelease(Loansign loansign,
			List<Automatic> automList, HttpServletRequest request) {

		try {
			Object obj = null;
			Object obj1 = null;
			Double loanMoney = 0.00;
			// 判断投标金额
			StringBuffer sb = new StringBuffer(
					"SELECT  loanUnit FROM loansign ls where ls.id=")
					.append(loansign.getId());
			obj = (Object) commonDao.findObjectBySql(sb.toString());
			// 查询优先夹层总额
			StringBuffer sb1 = new StringBuffer(
					"SELECT  sum(priority+middle) FROM loansign ls where ls.id=")
					.append(loansign.getId());
			obj1 = (Object) commonDao.findObjectBySql(sb1.toString());
			for (Automatic automatic : automList) {
				Userfundinfo userfundinfo = userInfoQuery
						.getUserFundInfoBybasicId(automatic.getUserbasicsinfo()
								.getId());
				Userbasicsinfo userbasic = userInfoServices
						.queryBasicsInfoById(automatic.getUserbasicsinfo()
								.getId().toString());
				// 判断投标的参数是否起作用
				// 自己的借款标自己不能认购
				if (automatic.getUserbasicsinfo().getId()
						.equals(loansign.getUserbasicsinfo().getId())) {
					continue;
				}

				// 判断账号余额
				if (automatic.getpSAmtQuota() != null
						&& userfundinfo != null
						&& userfundinfo.getCashBalance() < Double
								.valueOf(automatic.getpSAmtQuota())) {
					continue;
				}

				// 判断期限范围
				if (automatic.getAutoVildType() == 1) {
					if (loansign.getRemonth() < automatic.getpSTrdCycleValue()
							|| loansign.getRemonth() > automatic
									.getpETrdCycleValue()) {
						continue;
					}
				}

				Double count = 0.00;
				Double money = 0.00;
				Double acoumt = 0.00;
				// 获取到该用户最多可以买多少-----------
				if (automatic.getAutoMoneyType() == 1) {// 余额投资
					count = Double.valueOf(obj.toString());// 最低投资
					money = Double.valueOf(userfundinfo.getCashBalance());// 余额
					acoumt = Double.valueOf(obj1.toString());// 优先夹层总和
					loanMoney = Arith.mul(Math.floor(Arith.div(money, count)),
							count);
					if (loanMoney > acoumt) {
						loanMoney = acoumt;
					}

				} else {// 固定金额投资
					count = Double.valueOf(obj.toString());// 最低投资
					money = Double.valueOf(automatic.getpSAmtQuota());// 设定金额
					acoumt = Double.valueOf(obj1.toString());// 优先夹层总和
					loanMoney = Arith.mul(Math.floor(Arith.div(money, count)),
							count);
					if (loanMoney > acoumt) {
						loanMoney = acoumt;
					}
				}

				// 判断投资类型
				if (automatic.getAutoLoanType() == 1) {
					// 优先
					// 判断年化利率
					if ((loansign.getPrioRate() + loansign.getPrioAwordRate()) * 100 < Double
							.valueOf(automatic.getpSIRQuota())) {
						continue;
					}
					if ((loansign.getPrioRate() + loansign.getPrioAwordRate()) * 100 > Double
							.valueOf(automatic.getpEIRQuota())) {
						continue;
					}

					// 调用投资方法
					ipsGetLoanInfo(loansign.getId(), loanMoney,
							automatic.getAutoLoanType(), userbasic, loansign,
							request);

				} else if (automatic.getAutoLoanType() == 2) {
					// 夹层
					// 调用投资方法
					ipsGetLoanInfo(loansign.getId(), loanMoney,
							automatic.getAutoLoanType(), userbasic, loansign,
							request);
				}

			}
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	// 自动投标方法
	public synchronized String ipsGetLoanInfo(Long loanId, Double money,
			Integer subType, Userbasicsinfo user, Loansign loansigninfo,
			HttpServletRequest request) {
		// 查询loansign表数据
		Loansign loan = loanSignQuery.getLoansignById(loanId.toString());

		Double subMoney = 0.00;
		// 审核更新数据
		loan.setState(loansigninfo.getState());// 更新审核状态
		loan.setStatus(loansigninfo.getStatus());
		loan.setAdminuserId(loansigninfo.getAdminuserId());
		loan.setPublishTime(loansigninfo.getPublishTime());
		loan.setContractNo(loansigninfo.getContractNo());
		loan.setRealRate(loansigninfo.getRealRate());

		Integer isType = 0; // 0-默认 1-优先转夹层 2-夹层转优先

		// 判断优先剩余金额+夹层剩余金额是否>=购买金额
		Double sumPrioRestAddMiddle = Arith.add(loan.getPrioRestMoney(),
				loan.getMidRestMoney());
		if (sumPrioRestAddMiddle >= money) {
			if (subType == 1) { // 优先
				if (loan.getPrioRestMoney() < money) { // 夹层转优先
					subMoney = Arith.sub(money, loan.getPrioRestMoney()); // 购买金额-优先剩余金额=差额
					loan.setMiddle(Arith.sub(loan.getMiddle(), subMoney)); // 夹层总金额-差额
					loan.setMidRestMoney(Arith.sub(loan.getMidRestMoney(),
							subMoney)); // 夹层剩余金额-差额
					loan.setPriority(Arith.add(loan.getPriority(), subMoney)); // 优先总金额+差额
					loan.setPrioRestMoney(0.00); // 优先剩余金额=0
					isType = 2;
				}
				if (loan.getPrioRestMoney() >= money) {
					loan.setPrioRestMoney(Arith.sub(loan.getPrioRestMoney(),
							money));
				}
			} else if (subType == 2) { // 夹层
				if (loan.getMidRestMoney() < money) { // 优先转夹层
					subMoney = Arith.sub(money, loan.getMidRestMoney()); // 购买金额-夹层剩余金额=差额
					loan.setPrioRestMoney(Arith.sub(loan.getPrioRestMoney(),
							subMoney)); // 优先剩余金额-差额
					loan.setPriority(Arith.sub(loan.getPriority(), subMoney)); // 优先剩余金额-差额
					loan.setMiddle(Arith.add(loan.getMiddle(), subMoney)); // 夹层总金额+差额
					loan.setMidRestMoney(0.00); // 夹层剩余金额=0
					isType = 1;
				}
				if (loan.getMidRestMoney() >= money) {
					loan.setMidRestMoney(Arith.sub(loan.getMidRestMoney(),
							money));
				}
			}
		} else {
			return "2";
		}

		// 投标流水号
		String orderNum = "TB"
				+ StringUtil.getDateTime(user.getId(), loan.getId());
		// 获取费用表的信息
		Costratio costratio = loanSignQuery.queryCostratio();
		// 计算服务费
		Double fee = Arith.mul(money, costratio.getLoanInvestment());
		// 保存购买记录信息
		Loanrecord loanrecord = new Loanrecord();
		loanrecord
				.setIsPrivilege(userInfoQuery.isPrivilege(user) ? Constant.STATUES_ONE
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
		loanrecord.setWebOrApp(5); // 1-web 2-app 5-自动投标
		loanrecord.setOrder_id(orderNum);
		loanrecord.setSubMoney(subMoney);// 差额

		// 添加个人购买信息
		List<Action> listAction = new ArrayList<Action>();
		Action action = new Action(user.getpMerBillNo(), user.getName(), money);
		listAction.add(action);

		// 添加项目的信息
		BidInfo bidInfo = new BidInfo(loan, orderNum, fee, "1", listAction);
		nvps = new ArrayList<NameValuePair>();
		try {
			// 剩余金额-购买金额
			Double restMoney = Arith.sub(loan.getRestMoney(), money);
			loan.setRestMoney(restMoney); // 借款剩余金额
			loanSignQuery.saveOrUpdateLoanRecord(loanrecord, loan);
			String bidinfoXml = ParseXML.bidInfoXML(bidInfo);
			// 添加日志
			payLogService.savePayLog(bidinfoXml, user.getId(), loan.getId(), 4,
					orderNum, Double.valueOf(fee), 0.00, money); // 保存xml报文
			nvps.add(new BasicNameValuePair("requestParams", bidinfoXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(bidinfoXml
					+ "~|~" + ParameterIps.getmerchantKey())));
			String result = CommonUtil.excuteRequest(PayURL.REPAYMRNTTESTURL,
					nvps);
			System.out.println("项目投资业务查询=" + result);
			Document doc = DocumentHelper.parseText(result);
			Element rootElt = doc.getRootElement(); // 获取根节点
			System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
			// 拿到crs节点下的子节点code值
			String code = rootElt.elementTextTrim("code");
			String msg = rootElt.elementTextTrim("msg");
			String sign = rootElt.elementTextTrim("sign");
			String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"
					+ ParameterIps.getDes_algorithm());
			if (sign.equals(Md5sign)) {
				// 获取投标信息
				Loanrecord loanRecordNum = loanrecordService
						.getLoanRecordOrderNum(orderNum);
				Paylog payLog = payLogService.queryPaylogByOrderSn(orderNum);
				if (code.equals("CSD000")) {
					try {
						if (loanRecordNum.getIsSucceed() == 0) {
							loanRecordNum.setIsSucceed(Constant.STATUES_ONE);
							loanRecordNum.setUpdateTime(DateUtils
									.format("yyyy-MM-dd HH:mm:ss"));
							// 投资按100元计1分
							Integer product = (int) (payLog.getAmount() / 100);
							plankService.saveAutointegralBuyProject(
									loanRecordNum.getUserbasicsinfo(),
									payLog.getAmount(),
									loanRecordNum.getSubType()); // 保存积分记录
							// 余额查询
							crs cr = baoFuService.getCasbalance(loanRecordNum
									.getUserbasicsinfo().getpMerBillNo());
							loanRecordNum.getUserbasicsinfo().getUserfundinfo()
									.setCashBalance(cr.getBalance()); // 宝付的余额
							loanRecordNum.getUserbasicsinfo().getUserfundinfo()
							.setOperationMoney(cr.getBalance()); // 宝付的余额
							loanRecordNum.getUserbasicsinfo().setUserintegral(
									loanRecordNum.getUserbasicsinfo()
											.getUserintegral() + product); // 积分计算
							// userbasicsinfoService.update(loanRecordNum.getUserbasicsinfo());
							// 银行流水
							Accountinfo account = new Accountinfo();
							account.setExpenditure(payLog.getAmount());
							if (loanRecordNum.getLoanType() == 2) {
								account.setExplan("项目购买");
							} else if (loanRecordNum.getLoanType() == 3) {
								account.setExplan("天标购买");
							}
							account.setIncome(0.00);
							account.setIpsNumber(orderNum);
							account.setLoansignId(loan.getId().toString());
							account.setFee(payLog.getFee());
							account.setTime(DateUtils
									.format("yyyy-MM-dd HH:mm:ss"));
							account.setUserbasicsinfo(loanRecordNum
									.getUserbasicsinfo());
							if (loanRecordNum.getLoanType() == 2) {
								account.setAccounttype(plankService
										.accounttype(5L));
							} else if (loanRecordNum.getLoanType() == 3) {
								account.setAccounttype(plankService
										.accounttype(15L));
							}
							account.setMoney(cr.getBalance());// 流水记录表
							plankService.update(loanRecordNum, account,
									loanRecordNum.getUserbasicsinfo(), loan);
							// 保存佣金
							generalizeService
									.saveGeneralizeMoney(loanRecordNum);
							// 判断是否融资成功
							Double tendMoney = loanSignQuery
									.getSumLoanTenderMoney(loan.getId()
											.toString());
							Double subMoneyNum = Arith.sub(loan.getIssueLoan(),
									tendMoney);
							if (subMoneyNum == 0) {
								loan.setStatus(2); // 融资成功
								loan.setFullTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
								processingService.updateLoan(loan);
								Map<String, String> map = new HashMap<String, String>();
								map.put("loanNum", loan.getName());
								String content = smsService.getSmsResources(
										"check-fullBid.ftl", map);
								if (costratio.getBidPhone().indexOf(",") >= 0) {
									String[] strArrayPhone = costratio
											.getBidPhone().split(",");
									baseSmsService.sendSMS(content,
											strArrayPhone);
								} else {
									baseSmsService.sendSMS(content,
											costratio.getBidPhone());
								}
							}
							// 更新支付报文信息
							payLogService.updatePayLog(orderNum,
									Constant.STATUES_ONE);
						}
						LOG.error("宝付项目购买处理成功");
						return "3";
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("宝付项目购买处理成功---->平台数据处理失败-------->订单号----->"
								+ orderNum);
						return "5";
					}
				} else if (code.equals("CSD333")) {
					if (loanRecordNum.getIsSucceed() == 0) {
						// 剩余金额
						loan.setRestMoney(Arith.add(loan.getRestMoney(),
								loanRecordNum.getTenderMoney()));
						if (loanRecordNum.getSubType() == 1) { // 优先
							if (loanRecordNum.getIsType() == 0) { // 默认
								loan.setPrioRestMoney(Arith.add(
										loan.getPrioRestMoney(),
										loanRecordNum.getTenderMoney()));
							} else if (loanRecordNum.getIsType() == 2) { // 夹层转优先
								Double moneyNum = Arith.sub(
										loanRecordNum.getTenderMoney(),
										loanRecordNum.getSubMoney()); // 购买金额-差额=优先剩余金额
								loan.setMiddle(Arith.add(loan.getMiddle(),
										loanRecordNum.getSubMoney())); // 夹层总额+差额
								loan.setMidRestMoney(Arith.add(
										loan.getMidRestMoney(),
										loanRecordNum.getSubMoney())); // 夹层剩余金额+差额
								loan.setPriority(Arith.sub(loan.getPriority(),
										loanRecordNum.getSubMoney())); // 优先总额-差额
								loan.setPrioRestMoney(Arith.add(
										loan.getPrioRestMoney(), moneyNum)); // 优先剩余金额
							}
						} else if (loanRecordNum.getSubType() == 2) { // 夹层
							if (loanRecordNum.getIsType() == 0) {
								loan.setMidRestMoney(Arith.add(
										loan.getMidRestMoney(),
										loanRecordNum.getTenderMoney()));
							} else if (loanRecordNum.getIsType() == 1) { // 优先转夹层
								Double moneyNum = Arith.sub(
										loanRecordNum.getTenderMoney(),
										loanRecordNum.getSubMoney()); // 购买金额-差额=夹层剩余金额
								loan.setPriority(Arith.add(loan.getPriority(),
										loanRecordNum.getSubMoney())); // 优先总金额+差额
								loan.setPrioRestMoney(Arith.add(
										loan.getPrioRestMoney(),
										loanRecordNum.getSubMoney())); // 优先剩余总金额+差额
								loan.setMiddle(Arith.sub(loan.getMiddle(),
										loanRecordNum.getSubMoney())); // 夹层总额-差额
								loan.setMidRestMoney(Arith.add(
										loan.getMidRestMoney(), moneyNum)); // 夹层剩余金额
							}
						} else if (loanRecordNum.getSubType() == 3) { // 劣后
							loan.setAfterRestMoney(Arith.add(
									loan.getAfterRestMoney(),
									loanRecordNum.getTenderMoney()));
						}
						loanRecordNum.setIsSucceed(-1);
						loanRecordNum.setUpdateTime(DateUtils
								.format("yyyy-MM-dd HH:mm:ss"));
						loanSignQuery.updateLoanRecord(loanRecordNum, loan);
						// 更新支付报文信息
						payLogService.updatePayLog(orderNum, -1);
						LOG.error("宝付项目购买处理失败");
					}
					return "WEB-INF/views/failure";
				} else {
					LOG.error("宝付项目购买处理失败--订单号----->" + orderNum);
					return "WEB-INF/views/failure";
				}
			} else {
				LOG.error("非宝付项目购买返回数据---->订单号----->" + orderNum);
				return "WEB-INF/views/failure";
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("处理失败--订单号----->" + orderNum);
			return "WEB-INF/views/failure";
		}
	}
}