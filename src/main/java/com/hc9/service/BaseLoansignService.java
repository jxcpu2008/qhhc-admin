package com.hc9.service;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hc9.common.constant.Constant;
import com.hc9.common.constant.PayURL;
import com.hc9.common.thread.SendBackMsgThead;
import com.hc9.common.thread.SendMsgThead;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.SMSText;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Escrow;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.dao.entity.Loansignflow;
import com.hc9.dao.entity.MsgReminder;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Repaymentrecordparticulars;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Userrelationinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
import com.hc9.model.RegisterSubject;
import com.hc9.model.Transfer;
import com.ips.security.utility.IpsCrypto;

import freemarker.template.TemplateException;

/**
 * 借款标通用service
 * 
 * @author frank
 * 
 */
@Service
@Transactional
public class BaseLoansignService {

	private static final Logger logger = Logger.getLogger(BaseLoansignService.class);

	/** dao */
	@Resource
	private HibernateSupport dao;

	/** messagesettingService */
	@Resource
	private MessagesettingService messagesettingService;
	/** loanSignQuery */
	@Resource
	private LoanSignQuery loanSignQuery;
	/** loanSignFund */
	@Autowired
	private LoanSignFund loanSignFund;
	/** integralservice */
	@Resource
	private IntegralService integralservice;

	/** automaticService */
	@Resource
	private AutomaticService automaticService;

	@Resource
	private UserInfoQuery userInfoQuery;

	@Resource
	private LoanManageService loanManageService;

	@Resource
	private LoanSignQuery loansignQuery;

	@Resource
	private EscrowService escrowService;

	/**
	 * <p>
	 * Title: getLoansignCount
	 * </p>
	 * <p>
	 * Description: 根据类型查询借款标的条数
	 * </p>
	 * 
	 * @param type
	 *            类型
	 * @return 条数
	 */
	public int getLoansignCount(int type) {
		StringBuffer sb = new StringBuffer(
				"SELECT COUNT(1) FROM loansign where loanType=");
		return loanSignQuery.queryCount(sb.append(type).toString());
	}

	/**
	 * <p>
	 * Title: findExpirLoanSign
	 * </p>
	 * <p>
	 * Description: 通过借款标类型查询到即将到期的借款标
	 * </p>
	 * 
	 * @param page
	 *            分页
	 * @param loanType
	 *            借款标类型
	 * @return 集合
	 */
	public List<Object> findExpirLoanSign(PageModel page, int loanType) {

		StringBuffer sb = new StringBuffer(
				"SELECT loansign.id, loansignbasics.loanNumber, loansignbasics.loanTitle, ");
		sb.append(" userbasicsinfo. NAME, loansign.loanUnit, loansign.issueLoan, loansign.`month`, ");
		// sb.append(" CASE WHEN loansignbasics.loanCategory = 1 THEN '信用标' ELSE '抵押标' END, ");
		sb.append(" loansignbasics.mgtMoney, loansign.publishTime, loansign.rate, loansignbasics.reward,");
		sb.append(" CASE WHEN loansign.refundWay = 1 THEN '按月等额本息' WHEN loansign.refundWay = 2 THEN '按月付息到期还本' ELSE '到期一次性还本息' END,");
		sb.append(" CASE WHEN loansign.loanstate = 1 THEN '未发布' WHEN loansign.loanstate = 2 THEN '进行中' WHEN loansign.loanstate = 3 THEN '回款中' ELSE '已完成' END, ");
		sb.append(" CASE WHEN loansign.loanstate = 3 OR loansign.loanstate = 4 THEN '已放款' ELSE '未还款' END, ");
		sb.append(" loansignbasics.creditTime, CASE WHEN loansign.isShow = 1 THEN '显示' ELSE '不显示' END, CASE WHEN loansign.isRecommand = 1 THEN '推荐' ELSE '不推荐' END");
		sb.append(" FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.id ");
		sb.append(" INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id WHERE loansign.loanType = ? AND loansign.id IN ");
		sb.append(" ( SELECT loanSign_id FROM repaymentrecord WHERE to_days(preRepayDate)");
		sb.append(" - to_days(now()) <= 7 AND to_days(preRepayDate) >= to_days(now()))");
		System.out.println(sb);
		List<Object> param = new ArrayList<Object>();
		param.add(loanType);
		Object[] params = null;
		if (param.size() > 0) {
			params = param.toArray();
		}
		if (page != null) {
			return (List<Object>) dao.pageListBySql(page, sb.toString(), null,
					params);
		} else {
			return dao.findBySql(sb.toString(), params);
		}
	}

	/**
	 * 检查该借款的授信额度，判断是否可以购买
	 * 
	 * @param loan
	 *            借款标
	 * @return true 可以购买 false 不可以
	 */
	public boolean checkCredit(Loansign loan) {
		StringBuffer sb = new StringBuffer(
				"SELECT ( SELECT IFNULL(sum(ls.issueLoan), 0) FROM loansign as ls WHERE ls.loanstate != 4 AND ls.userbasicinfo_id = uf.id ) +")
				// .append(loan.getIssueLoan())
				.append("- IFNULL(uf.credit, 0) FROM userfundinfo as uf WHERE uf.id =")
				.append(loan.getUserbasicsinfo().getId());
		System.out.println(sb.toString());
		Object object = dao.findObjectBySql(sb.toString());
		if (null != object && Double.valueOf(object.toString()) < 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查该借款的授信额度，判断是否可以购买
	 * 
	 * @param loan
	 *            借款标
	 * @return true 可以购买 false 不可以
	 */
	public boolean checkCredit(Loansign loan, Long uid) {
		StringBuffer sb = new StringBuffer(
				"SELECT ( SELECT IFNULL(sum(ls.issueLoan), 0) FROM loansign as ls WHERE ls.loanstate != 4 AND ls.userbasicinfo_id = uf.id ) +")
				// .append(loan.getIssueLoan())
				.append("- IFNULL(uf.credit, 0) FROM userfundinfo as uf WHERE uf.id =")
				.append(uid);
		System.out.println(sb.toString());
		Object object = dao.findObjectBySql(sb.toString());
		if (null != object && Double.valueOf(object.toString()) < 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * Title: changeEnd
	 * </p>
	 * <p>
	 * Description: 如果该标的还款记录中还款状态均不为1，该标状态变为已完成
	 * </p>
	 * 
	 * @param repaymentrecord
	 *            还款记录
	 */
	private void changeEnd(Repaymentrecord repaymentrecord) {
		StringBuffer sb = new StringBuffer(
				"SELECT COUNT(1) from repaymentrecord where repayState=1 and loanSign_id=")
				.append(repaymentrecord.getLoansign().getId())
				.append(" and id!=").append(repaymentrecord.getId());
		Object obj = dao.findObjectBySql(sb.toString());

		if (null != obj && Integer.parseInt(obj.toString()) == 0) {
			Loansign loanSign = repaymentrecord.getLoansign();
			// TODO 修改
			loanSign.setStatus(4);// 4已完成
			dao.update(loanSign);
		}
	}

	/**
	 * 通过传入的还款计划进行回款给用户(针对所有的标)
	 * 
	 * @param repaymentRecord
	 *            还款记录
	 * @param reallyDay
	 *            天标和一次性还款的使用天数 其他就传0
	 */
	private void repay(Repaymentrecord repaymentRecord, Integer reallyDay) {
		// 1.找到该借款标的所有认购人
		StringBuffer sb = new StringBuffer(
				"From Loanrecord where isSucceed=1 and loansign.id=")
				.append(repaymentRecord.getLoansign().getId());
		List<Loanrecord> loanreList = dao.find(sb.toString());

		BigDecimal interest = new BigDecimal(0.00);
		BigDecimal money = new BigDecimal(0.00);
		for (Loanrecord loanrecord : loanreList) {
			// TODO 类型 Type检验
			if (repaymentRecord.getLoansign().getType() == Constant.STATUES_FOUR) {
				// 计算流转标这一阶段的使用时间
				reallyDay = loanSignFund.reallyDay(loanrecord.getTenderTime(),
						repaymentRecord.getPreRepayDate());
			}
			// 1.查询到用户的收款金额
			money = loanSignQuery.queryMoney(repaymentRecord,
					loanrecord.getTenderMoney(), repaymentRecord.getLoansign(),
					reallyDay);

			// 2.改变用户资金明细账和可用余额
			loanSignFund.updateMoney(money, Long.valueOf("4"), DateUtils
					.format(Constant.DEFAULT_TIME_FORMAT), loanrecord
					.getUserbasicsinfo().getId(), "借款标回款", null,
					repaymentRecord.getLoansign().getId());

			// 3.改变平台资金明细账和平台的余额
			loanSignFund.updatePlatformMoney(
					money.multiply(new BigDecimal(-1)), Long.valueOf("4"),
					DateUtils.format(Constant.DEFAULT_TIME_FORMAT), "借款标回款",
					null);

			// 4.求到用户本次所赚得利息interest
			interest = loanSignQuery.queryInterest(repaymentRecord,
					loanrecord.getTenderMoney(), repaymentRecord.getLoansign(),
					reallyDay);

			// 5.求到该用户要支付的管理费
			money = loanSignFund.managementCost(interest,
					repaymentRecord.getLoansign(), loanrecord.getIsPrivilege());

			// 6.改变用户资金明细账和可用余额
			loanSignFund.updateMoney(money.multiply(new BigDecimal(-1)), Long
					.valueOf("13"), DateUtils
					.format(Constant.DEFAULT_TIME_FORMAT), loanrecord
					.getUserbasicsinfo().getId(), "管理费", null, repaymentRecord
					.getLoansign().getId());

			// 7.改变平台资金明细账和平台的余额
			loanSignFund.updatePlatformMoney(money, Long.valueOf("13"),
					DateUtils.format(Constant.DEFAULT_TIME_FORMAT), "投资管理费",
					null);

			// 8.推广的信息---

		}
	}

	/**
	 * <p>
	 * Title: dayOnTimeRepay
	 * </p>
	 * <p>
	 * Description: 天标还款（按照实际的天数进行计算）
	 * </p>
	 * 
	 * @param repaymentRecord
	 *            还款记录
	 * @param repayTime
	 *            还款时间
	 * @return 是否
	 */
	public boolean dayOnTimeRepay(Repaymentrecord repaymentRecord,
			String repayTime) {
		// try {
		Loansign loansign = repaymentRecord.getLoansign();
		// 实际使用天数
		int useDay = loanSignFund.reallyUseDay(
				loanSignQuery.getcreditTime(loansign.getId()), repayTime);
		// 实际还款的利息
		// BigDecimal reallyInterest = loanSignFund.instalmentInterest(
		// new BigDecimal(loansign.getIssueLoan()), loansign.getRate(),
		// useDay, 2);

		repaymentRecord.setRepayTime(repayTime);// 实际还款时间=用户选择的时间
		// 1.资金操作
		repay(repaymentRecord, useDay);
		// 2.给投资者发送消息，告知已经回款(系统消息,邮件)
		String contant = new SMSText().backContant;
		Thread sendBackMsgThead = new SendBackMsgThead(loansign, contant,
				messagesettingService);
		sendBackMsgThead.start();

		// 3.计算写入时间的使用天数并把标改为已完成
		// TODO 状态修改 loansign.setRealDay(useDay);
		loansign.setState(4);
		dao.update(loansign);
		// 4.改还款记录状态和记录实际还款金额
		// repaymentRecord.setRealMoney(reallyInterest.doubleValue());// 实际还款利息
		// repaymentRecord.setRepayState(useDay > loansign.getUseDay() ? 4
		// : (useDay == loansign.getUseDay() ? 2 : 5));
		dao.update(repaymentRecord);
		// 5.检查是否完成
		changeEnd(repaymentRecord);
		// 6.借款人的积分变化(照按时算)
		integralservice.timelyRepaymentIntegral(repaymentRecord,
				new BigDecimal(repaymentRecord.getRealMoney())
						.add(new BigDecimal(repaymentRecord.getMoney())));
		return true;
		// } catch (Exception e) {
		// log.error("BaseLoansignService-按时还款资金操作时出现异常！");
		// e.printStackTrace();
		// return false;
		// }
	}

	/**
	 * 秒标还款(实际的和预计的一样)
	 * 
	 * @param loansign
	 *            借款标
	 * @return 是否成功
	 */
	public boolean secOnTimeRepay(Loansign loansign) {
		// 1.资金操作
		Repaymentrecord repayment = loanSignQuery.getRepaymentByLSId(loansign
				.getId().toString());
		repay(repayment, 0);

		// 2.给投资者发送消息，告知已经回款(系统消息,邮件,短信都会发送)
		String contant = new SMSText().backContant;
		Thread sendBackMsgThead = new SendBackMsgThead(loansign, contant,
				messagesettingService);
		sendBackMsgThead.start();

		// TODO 3.改状态loansign
		loansign.setState(4);// 已经完成
		dao.update(loansign);

		// 4.改状态记录实际还款金额。回款计划表
		repayment.setRepayState(2);
		repayment.setRepayTime(DateUtils.format(Constant.DEFAULT_DATE_FORMAT));
		repayment.setRealMoney(repayment.getPreRepayMoney());// 实际还款金额==预计还款
		dao.update(repayment);
		return true;
	}

	/**
	 * <p>
	 * Title: onTimeRepay
	 * </p>
	 * <p>
	 * Description: 按时还款(普通标和流转标)
	 * </p>
	 * 
	 * @param repaymentRecord
	 *            还款记录
	 * @param repayTime
	 *            还款时间
	 * @return 是否成功
	 */
	public boolean onTimeRepay(Repaymentrecord repaymentRecord, String repayTime) {
		// 1.资金操作
		repaymentRecord.setRepayState(2);// 按时还款

		repay(repaymentRecord, 0);

		// 2.给投资者发送消息，告知已经回款(系统消息,邮件,短信都会发送)
		String contant = new SMSText().backContant;
		Thread sendBackMsgThead = new SendBackMsgThead(
				repaymentRecord.getLoansign(), contant, messagesettingService);
		sendBackMsgThead.start();
		// 3.改状态
		repaymentRecord.setRepayTime(repayTime);// 实际还款时间--用户选择的时间
		repaymentRecord.setRealMoney(repaymentRecord.getPreRepayMoney());// 实际的和预计的一样
		dao.update(repaymentRecord);
		// 4.判断该借款标是否已经全部还款-->如果是就把该标变成已完成
		changeEnd(repaymentRecord);
		// 5.借款人的积分变化
		integralservice.timelyRepaymentIntegral(repaymentRecord,
				new BigDecimal(repaymentRecord.getPreRepayMoney())
						.add(new BigDecimal(repaymentRecord.getMoney())));
		return true;
	}

	/**
	 * <p>
	 * Title: exceedTimeRepay
	 * </p>
	 * <p>
	 * Description: 逾期还款
	 * </p>
	 * 
	 * @param repaymentRecord
	 *            还款记录
	 * @return 是否成功
	 */
	public boolean exceedTimeRepay(Repaymentrecord repaymentRecord) {

		// 1.资金操作
		repaymentRecord.setRepayState(3);// 逾期未还款
		repay(repaymentRecord, 0);
		// 2.给投资者发送消息，告知已经回款(系统消息,邮件,短信都会发送)
		String contant = new SMSText().backContant;
		Thread sendBackMsgThead = new SendBackMsgThead(
				repaymentRecord.getLoansign(), contant, messagesettingService);
		sendBackMsgThead.start();

		// 3.改状态
		dao.update(repaymentRecord);

		// 4.判断该借款标是否已经全部还款-->如果是就把该标变成已完成
		changeEnd(repaymentRecord);

		// 5.借款人的积分变化
		integralservice.overdueRepaymentIntegral(repaymentRecord,
				new BigDecimal(repaymentRecord.getPreRepayMoney())
						.add(new BigDecimal(repaymentRecord.getMoney())));
		return true;
	}

	/**
	 * <p>
	 * Title: advanceRepay
	 * </p>
	 * <p>
	 * Description: 提前还款(普通)
	 * </p>
	 * 
	 * @param repaymentRecord
	 *            还款记录
	 * @param repayTime
	 *            还款时间
	 * @return 是否成功
	 */
	public boolean advanceRepay(Repaymentrecord repaymentRecord,
			String repayTime) {

		Loansign loansign = dao.get(Loansign.class, repaymentRecord
				.getLoansign().getId());
		// 1.资金操作

		// 实际使用天数
		int reallyDay = loanSignFund.reallyUseDay(
				loanSignQuery.getcreditTime(loansign.getId()), repayTime);
		repay(repaymentRecord, reallyDay);

		// 2.给投资者发送消息，告知已经回款(系统消息,邮件,短信都会发送)
		String contant = new SMSText().backContant;
		Thread sendBackMsgThead = new SendBackMsgThead(loansign, contant,
				messagesettingService);
		sendBackMsgThead.start();

		// 3.改loansign 的实际使用天数
		// loansign.setRealDay(reallyDay);
		dao.update(loansign);

		// 4.改状态
		repaymentRecord.setRepayState(5);// 提前还款
		repaymentRecord.setRepayTime(repayTime);
		// 记录实际还款金额
		// repaymentRecord.setRealMoney(loanSignFund.advanceInterest(
		// new BigDecimal(loansign.getIssueLoan()), loansign.getRate(),
		// reallyDay, loansign.getLoanType()).doubleValue());
		dao.update(repaymentRecord);

		// 4.判断该借款标是否已经全部还款-->如果是就把该标变成已完成
		changeEnd(repaymentRecord);

		// 5.借款人的积分变化(按时、普通标 、天标、流转标有)--
		integralservice.timelyRepaymentIntegral(repaymentRecord,
				new BigDecimal(repaymentRecord.getRealMoney())
						.add(new BigDecimal(repaymentRecord.getMoney())));
		return true;
	}

	/**
	 * <p>
	 * Title: save
	 * </p>
	 * <p>
	 * Description: 债权转让
	 * </p>
	 * 
	 * @param loansign
	 *            借款标
	 * @param loansignbasics
	 *            借款标基础信息
	 * @param loansignflow
	 *            费用比例
	 * @return 是否
	 */
	public boolean saveAssignment(Loansign loansign,
			Loansignbasics loansignbasics, Userbasicsinfo user,
			Loansignflow loansignflow) {
		boolean boolTrue = true;
		Loansign LoanSignEntity = this.dao
				.get(Loansign.class, loansign.getId());
		Loansignbasics LoansignbasicsEntity = this.dao.get(
				Loansignbasics.class, loansign.getId());

		Loansignflow fl = this.dao
				.get(Loansignflow.class, loansignflow.getId());

		/*
		 * String tenderMoney = loanSignQuery.getTenderMoney(loansign.getId()
		 * .toString(), userId.toString()); BorrowersApply boor = null;
		 */
		Loansign loansigncir = new Loansign();
		// 1.保存loansign
		loansigncir.setState(LoanSignEntity.getState());
		loansigncir.setIsdet(1);// 债权转让
		loansigncir.setUserbasicsinfo(user);
		loansigncir.setName(LoanSignEntity.getName());
		loansigncir.getLoansignbasics().setPermission(
				LoanSignEntity.getLoansignbasics().getPermission());
		loansigncir.getLoansignbasics().setAddress(
				LoanSignEntity.getLoansignbasics().getAddress());
		loansigncir.getLoansignbasics().setProvince(
				LoanSignEntity.getLoansignbasics().getProvince());
		loansigncir.getLoansignbasics().setCity(
				LoanSignEntity.getLoansignbasics().getCity());
		loansigncir.getLoansignbasics().setLoanType(
				LoanSignEntity.getLoansignbasics().getLoanType());
		loansigncir.getLoansignbasics().setPerson(
				LoanSignEntity.getLoansignbasics().getPerson()); // 公司人数
		loansigncir.getLoansignbasics().setIsUp(
				LoanSignEntity.getLoansignbasics().getIsUp()); // 公司是否成立
		loansigncir.getLoansignbasics().setHaveOther(
				LoanSignEntity.getLoansignbasics().getHaveOther());
		loansigncir.setRestMoney(loansignflow.getTenderMoney());
		loansigncir.setPublishTime(LoanSignEntity.getPublishTime());
		loansigncir.setCreditTime(LoanSignEntity.getCreditTime());
		loansigncir.setAppropriation(LoanSignEntity.getAppropriation());
		loansigncir.getLoansignbasics().setRemark(
				LoanSignEntity.getLoansignbasics().getRemark());
		loansigncir.getLoansignbasics().setLoanimg(
				LoanSignEntity.getLoansignbasics().getLoanimg());
		loansigncir.getLoansignbasics().setProindustry(
				LoanSignEntity.getLoansignbasics().getProindustry());
		loansigncir.setRecommend(LoanSignEntity.getRecommend());
		loansigncir.setTag(LoanSignEntity.getTag());
		loansigncir.getLoansignbasics().setEnteruptime1(
				LoanSignEntity.getLoansignbasics().getEnteruptime1());
		loansigncir.getLoansignbasics().setEnteruptime2(
				LoanSignEntity.getLoansignbasics().getEnteruptime2());
		loansigncir.setState(2);// 审核通过
		loansigncir.setStatus(1);// 未发布

		// 设置当前的费用比例
		/*
		 * if (costratio != null) {
		 * loansign.setPrepaymentRate(costratio.getPrepaymentRate());
		 * loansign.setOverdueRepayment(costratio.getOverdueRepayment()); if
		 * (LoanSignEntity.getLoanType() == 1) {
		 * loansign.setMfeeratio(costratio.getMfeeratio());
		 * loansign.setVipMfeeratio(costratio.getVipMfeeratio());
		 * loansign.setVipMfeetop(costratio.getVipMfeetop());
		 * loansign.setVipPmfeeratio(costratio.getVipPmfeeratio());
		 * loansign.setPmfeeratio(costratio.getPmfeeratio()); if (bool) {
		 * loansign.setShouldPmfee(Arith.mul( costratio.getVipPmfeeratio(),
		 * loansign.getIssueLoan())); if
		 * (Arith.mul(costratio.getVipPmfeeratio(), loansign.getIssueLoan()) <
		 * loansign .getVipPmfeetop()) {
		 * loansign.setShouldPmfee(loansign.getVipPmfeetop()); } } else {
		 * loansign.setShouldPmfee(Arith.mul( costratio.getPmfeeratio(),
		 * loansign.getIssueLoan())); } // 修改该申请金额是否发布了借款标 boor =
		 * userInfoQuery.getBorrowersApply(Integer
		 * .parseInt(LoanSignEntity.getLoansignType().getId() .toString()));
		 * loansign.setBorrowersApply(boor); } else if
		 * (LoanSignEntity.getLoanType() == 2) {
		 * loansign.setPmfeeratio(costratio.getDayRate()); } else {
		 * loansign.setPmfeeratio(Double
		 * .parseDouble(Constant.STATUES_ZERO.toString())); } //
		 * loansign.setOther(costratio.getOther());
		 * loansign.setVipPmfeetop(costratio.getVipPmfeetop()); }
		 */
		Serializable seria = dao.save(loansigncir); // 记住添加项目时的Id
		Long id = loansign.getId();

		// 2.保存Loansignbasics
		Loansignbasics lbs = new Loansignbasics();
		lbs.setId(Long.valueOf(seria.toString()));
		lbs.getLoansign().setIssueLoan(loansignflow.getTenderMoney());
		lbs.setBusinessIntro(LoansignbasicsEntity.getBusinessIntro());
		lbs.setTeamsIntro(LoansignbasicsEntity.getTeamsIntro());
		lbs.setHistory(LoansignbasicsEntity.getHistory());
		lbs.setFuturePlan(LoansignbasicsEntity.getFuturePlan());
		lbs.setProjectAtt1(LoansignbasicsEntity.getProjectAtt1());
		lbs.setProjectAtt2(LoansignbasicsEntity.getProjectAtt2());
		lbs.setFuturePlan(LoansignbasicsEntity.getFuturePlan());
		lbs.getLoansign().setRemonth(
				LoansignbasicsEntity.getLoansign().getRemonth());
		// lbs.setValidity(LoansignbasicsEntity.getValidity());
		lbs.getLoansign().setPriority(
				LoansignbasicsEntity.getLoansign().getPriority());
		lbs.getLoansign().setMiddle(
				LoansignbasicsEntity.getLoansign().getMiddle());
		lbs.getLoansign().setAfter(
				LoansignbasicsEntity.getLoansign().getAfter());
		lbs.getLoansign().setPrioRate(
				LoansignbasicsEntity.getLoansign().getPrioRate());
		lbs.getLoansign().setMidRate(
				LoansignbasicsEntity.getLoansign().getMidRate());
		lbs.getLoansign().setAfterRate(
				LoansignbasicsEntity.getLoansign().getAfterRate());
		lbs.getLoansign().setLoanUnit(
				LoansignbasicsEntity.getLoansign().getLoanUnit());
		lbs.getLoansign().setOutDay(
				LoansignbasicsEntity.getLoansign().getOutDay());
		lbs.getLoansign().setPrioAwordRate(
				LoansignbasicsEntity.getLoansign().getPrioAwordRate());
		lbs.getLoansign().setPrioRestMoney(
				LoansignbasicsEntity.getLoansign().getPrioRestMoney());
		lbs.getLoansign().setMidRestMoney(
				LoansignbasicsEntity.getLoansign().getMidRestMoney());
		lbs.getLoansign().setAfterRestMoney(
				LoansignbasicsEntity.getLoansign().getAfterRestMoney());

		lbs.getLoansign().setAfterRate(
				LoansignbasicsEntity.getLoansign().getAfterRate());
		lbs.getLoansign().setAfterRestMoney(
				LoansignbasicsEntity.getLoansign().getAfterRestMoney());

		dao.save(lbs);

		StringBuffer sb = new StringBuffer(
				"update loansignflow set flowstate=2, user_id="
						+ LoanSignEntity.getUserbasicsinfo().getId()
						+ ", loansign_id=");
		sb.append(seria.toString()).append(" where loan_id=")
				.append(LoanSignEntity.getId());
		dao.executeSql(sb.toString());

		return boolTrue;
	}

	/**
	 * <p>
	 * Title: queryBorrowersbaseList
	 * </p>
	 * <p>
	 * Description: 查询到所有的债权转让
	 * </p>
	 * 
	 * @param page
	 *            page
	 * @param username
	 *            借款人用户名
	 * @param cardno
	 *            身份证号码
	 * @return 借款
	 */
	public Object queryAssignmentbaseList(PageModel page, String username,
			String loanTitle) {
		StringBuffer sb = new StringBuffer(
				"select b.id,c.userName,c.name,a.tenderMoney,b.loanTitle ,b.loanNumber,b.riskAssess,d.isShow,d.rate,d.isRecommand,d.endTime,b.loanCategory,b.assure,b.mgtMoneyScale,b.unassureWay,");
		sb.append("  b.mgtMoney,b.overview,b.behoof,b.loanOrigin,b.riskCtrlWay,b.speech,d.loanUnit from loansignflow a,loansignbasics b,userbasicsinfo c,loansign d where a.loan_id=b.id and a.user_debt=c.id and b.id=d.id and a.flowstate=1 and a.auditResult=1 and a.auditStatus=3 ");
		if (!username.trim().equals("")) {
			sb.append(" and c.username like '%").append(username).append("%'");
		}
		if (!loanTitle.equals("")) {
			sb.append(" and b.loanTitle like '%").append(loanTitle)
					.append("%'");
		}

		return dao.findBySql(sb.toString(), null);

	}

	/**
	 * <p>
	 * Title: queryBorrowersbaseList
	 * </p>
	 * <p>
	 * Description: 查询到所有的借款人
	 * </p>
	 * 
	 * @param page
	 *            page
	 * @param username
	 *            借款人用户名
	 * @param cardno
	 *            身份证号码
	 * @return 借款
	 */
	public Object queryBorrowersbaseLists(PageModel page, String username,
			String cardno) {
		StringBuffer sb = new StringBuffer(
				"SELECT u.id,u.name, u.userName, f.cardId FROM userbasicsinfo u, userrelationinfo f  "
						+ "WHERE   u.id = f.id  AND u.isCreditor=2");
		if (!username.trim().equals("")) {
			sb.append(" and u.name like '%").append(username).append("%'");
		}
		if (!cardno.equals("")) {
			sb.append(" and f.cardId like '%").append(cardno).append("%'");
		}
		sb.append(" LIMIT ").append(page.firstResult()).append(",")
				.append(page.getNumPerPage());
		return dao.findBySql(sb.toString(), null);
	}

	/**
	 * <p>
	 * Title: credit
	 * </p>
	 * <p>
	 * Description: 借款标放款(普通标和天标、流转标)
	 * </p>
	 * 
	 * @param loansign
	 *            借款标
	 * @return 是否成功
	 */
	public boolean credit(Loansign loansign) {

		// 1.生成还款记录
		// this.repaymentRecords(loansign);

		// 2.平台资金减少
		/*
		 * loanSignFund.updatePlatformMoney( new
		 * BigDecimal(loansign.getIssueLoan()), Long.valueOf(14 + ""),
		 * DateUtils.format(Constant.DEFAULT_TIME_FORMAT), "借款标放款", null);//
		 * 14.放款相关
		 */
		// 3.改状态
		if (loansign.getType() != 4) {
			loansign.setState(3);// 回款中
			dao.update(loansign);
		}

		// 4.改还款时间
		Loansignbasics loansignbasics = loanSignQuery
				.getLoansignbasicsById(loansign.getId().toString());
		/*
		 * loansignbasics.setCreditTime(DateUtils
		 * .format(Constant.DEFAULT_TIME_FORMAT));// 放款时间
		 */dao.update(loansignbasics);
		// 4.放款发放奖励(天标和普通标有奖励)
		if (loansign.getType() == 1 || loansign.getType() == 2) {
			// getReward(loansign);
		}
		return true;
	}

	/**
	 * <p>
	 * Title: GetQueryConditions
	 * </p>
	 * <p>
	 * Description: 组装的查询条件
	 * </p>
	 * 
	 * @param loansignbasics
	 *            借款标基础信息
	 * @return sql语句
	 */
	public String getQueryConditions(Loansignbasics loansignbasics,
			String loanType) {
		StringBuffer sb = new StringBuffer();
		if (null != loansignbasics.getLoanNumber()
				&& !"".equals(loansignbasics.getLoanNumber())) {
			sb.append(" and loansignbasics.loanNumber like '%")
					.append(loansignbasics.getLoanNumber()).append("%'");
		}

		if (null != loansignbasics.getViews()
				&& !loansignbasics.getViews().equals("")
				&& !(loansignbasics.getViews() == 0)) {
			sb.append(" and loansign.loansignType_id =").append(
					Integer.parseInt(loansignbasics.getViews().toString()));
		}
		return sb.toString();
	}

	/***
	 * 根据投标类型和标号查询
	 * 
	 * @param loanId
	 * @param subType
	 * @return
	 */
	public Integer getListLoanrecord(Long loanId, Integer subType) {
		String sql = "SELECT count(1) FROM loanrecord  l where  l.loanSign_id="
				+ loanId + " and l.isSucceed=1 and l.subType=" + subType;
		return loanSignQuery.queryCount(sql);
	}

	/**
	 * 还款投投资人详情
	 * 
	 * @param record
	 *            主还款信息
	 * @param isLastPeriod
	 *            最后一期
	 */
	public List<Repaymentrecordparticulars> repaymentRecordsParticulars(
			Repaymentrecord record, boolean isLastPeriod) {
		// 打开当前还款的标的信息；
		Loansign loan = record.getLoansign();
		Double preRepayMoney = record.getPreRepayMoney();// 优先预计还款金额
		Double money = 0.00;// 优先
		Double middlePreRepayMoney = record.getMiddlePreRepayMoney();// 夹层预计还款金额
		Double middleMoney = 0.00; // 夹层
		Double afterPreRepayMoney = record.getAfterPreRepayMoney();// 劣后预计还款金额
		Double afterMoney = 0.00;// 劣后
		// 获得优先/夹层/劣后各还款人数
		int priNum = getListLoanrecord(loan.getId(), 1);
		int midNum = getListLoanrecord(loan.getId(), 2);
		int afterNum = getListLoanrecord(loan.getId(), 3);
		// 找到当前还款信息的 所有投资人；
		String sql = "SELECT * FROM loanrecord  l where  l.loanSign_id=? and l.isSucceed=1";
		List<Loanrecord> loanrecod = dao.findBySql(sql, Loanrecord.class,
				loan.getId());
		List<Repaymentrecordparticulars> repas = new ArrayList<>();
		for (int i = 0; i < loanrecod.size(); i++) {
			Loanrecord lc = loanrecod.get(i);
			Repaymentrecordparticulars repa = new Repaymentrecordparticulars();
			repa.setUserbasicsinfo(lc.getUserbasicsinfo());

			if (lc.getSubType() == 1) {// 优先
				double bid = Arith.div(lc.getTenderMoney(), loan.getPriority());
				if (isLastPeriod) {
					repa.setMoney(lc.getTenderMoney());
				} else {
					repa.setMoney(0.0);
				}
				if (priNum < 2) {
					money = preRepayMoney;
				} else {
					money = Arith.round(
							Arith.mul(record.getPreRepayMoney(), bid), 2);
					preRepayMoney = preRepayMoney - money;
				}
				repa.setPreRepayMoney(money);
				priNum--;
			} else if (lc.getSubType() == 2) {// 夹层
				double bid = Arith.div(lc.getTenderMoney(), loan.getMiddle());
				if (isLastPeriod) {
					repa.setMiddleMoney(lc.getTenderMoney());
				} else {
					repa.setMiddleMoney(0.0);
				}
				if (midNum < 2) {
					middleMoney = middlePreRepayMoney;
				} else {
					middleMoney = Arith.round(
							Arith.mul(record.getMiddlePreRepayMoney(), bid), 2);
					middlePreRepayMoney = middlePreRepayMoney - middleMoney;
				}
				repa.setMiddlePreRepayMoney(middleMoney);
				midNum--;
			} else if (lc.getSubType() == 3) {// 劣后
				double bid = Arith.div(lc.getTenderMoney(), loan.getAfter());
				if (isLastPeriod) {
					repa.setAfterMoney(lc.getTenderMoney());
				} else {
					repa.setAfterMoney(0.0);
				}
				if (afterNum < 2) {
					afterMoney = afterPreRepayMoney;
				} else {
					afterMoney = Arith.round(
							Arith.mul(record.getAfterPreRepayMoney(), bid), 2);
					afterPreRepayMoney = afterPreRepayMoney - afterMoney;
				}
				repa.setAfterPreRepayMoney(afterMoney);
				afterNum--;
			}
			repa.setRepaymentrecord(record);
			repa.setRepState(-1);
			repa.setLoanType(lc.getSubType());
			repa.setLoanrecord(lc);
			repa.setFee(0.00);
			dao.save(repa);
			repas.add(repa);
		}
		return repas;

	}

	/**
	 * 校验实际利息分配最终的总和和预期的总利息是否相等， 如果不等，用实际的利息反推，把误差算入劣后中
	 * 
	 * @param record
	 * @param list
	 * @param loansign
	 * @param loansign
	 */
	public void verifyFundAllocated(Repaymentrecord record,
			List<Repaymentrecordparticulars> particulars) {
		DecimalFormat df = new DecimalFormat("#.##");
		Loansign loansign = record.getLoansign();
		double prioMoney = 0.0;
		double midMoney = 0.0;
		double afterMoney = 0.0;
		for (Repaymentrecordparticulars item : particulars) {
			if (item.getLoanType() == 1) {
				prioMoney += item.getPreRepayMoney();
				continue;
			}
			if (item.getLoanType() == 2) {
				midMoney += item.getMiddlePreRepayMoney();
				continue;
			}
			if (item.getLoanType() == 3) {
				afterMoney += item.getAfterPreRepayMoney();
				continue;
			}
		}

		double sonSum = prioMoney + midMoney + afterMoney;// 实际的利息分配后总额
		double parentSum = record.getPreRepayMoney()
				+ record.getMiddlePreRepayMoney()
				+ record.getAfterPreRepayMoney();// 预期的利息总额
		if (sonSum == parentSum) {
			return;
		}
		double realAfter = 0.0;

		realAfter = parentSum - prioMoney - midMoney;
		for (Repaymentrecordparticulars item : particulars) {
			if (item.getLoanType() == 3) {
				Loanrecord loanrecord = item.getLoanrecord();
				double bid = Arith.div(loanrecord.getTenderMoney(),
						loansign.getAfter());
				item.setAfterPreRepayMoney(Double.parseDouble(df
						.format(realAfter * bid)));
				dao.update(item);
			}
		}
	}

	/**
	 * 更新 还款记录
	 * 
	 * @param loansign
	 *            标的信息
	 */
	public void updateRepaymentRecords(Loansign loansign) {
		// 获取取的还款计划
		List<Repaymentrecord> repaymentrecordList = loanManageService
				.getNotRepaymentRecords(loansign);
		for (int i = 0; i < repaymentrecordList.size(); i++) {
			// 得到还款主记录
			Repaymentrecord repaymentrecord = repaymentrecordList.get(i);
			if (loansign.getType() == 2) {// 普通更新
				double realityRate = 0.00;
				if (loansign.getRefunway() == 1) {
					realityRate = (loansign.getIssueLoan() * loansign
							.getRealRate()) / 12; // 每期实际利息 =总额 * 实际利率 /12
				} else if (loansign.getRefunway() == 2) {
					realityRate = (loansign.getIssueLoan() * loansign
							.getRealRate()) / 4; // 每期实际利息 =总额 * 实际利率 /12
				}
				repaymentrecord
						.setMiddlePreRepayMoney((realityRate - repaymentrecord
								.getPreRepayMoney()) * loansign.getMidRate());// 预计还款利息=（总额利息-优先利息）
				repaymentrecord
						.setAfterPreRepayMoney((realityRate - repaymentrecord
								.getPreRepayMoney()) * loansign.getAfterRate());// 预计还款利息=（总额利息-优先利息）*比例
				repaymentrecord
						.setRepaymentrecordparticulars(updateRepaymentrecordparticulars(
								repaymentrecord, loansign));// 更新明细记录
				dao.update(repaymentrecord);
			} else if (loansign.getType() == 3) {// 天标更新
				double realityRate = (loansign.getIssueLoan() * loansign
						.getRealRate()) / 360 * loansign.getRemonth();
				repaymentrecord
						.setMiddlePreRepayMoney((realityRate - repaymentrecord
								.getPreRepayMoney()) * loansign.getMidRate());// 预计还款利息=（总额利息-优先利息）
				repaymentrecord
						.setAfterPreRepayMoney((realityRate - repaymentrecord
								.getPreRepayMoney()) * loansign.getAfterRate());// 预计还款利息=（总额利息-优先利息）*比例
				repaymentrecord
						.setRepaymentrecordparticulars(updateRepaymentrecordparticulars(
								repaymentrecord, loansign));// 更新明细记录
				dao.update(repaymentrecord);
			}

		}

	}

	/**
	 * @param loansign
	 *            标地信息
	 * @param
	 * @param repaymentrecord
	 * 
	 */
	public Set<Repaymentrecordparticulars> updateRepaymentrecordparticulars(
			Repaymentrecord repaymentrecord, Loansign loansign) {
		DecimalFormat df = new DecimalFormat("#.##");
		// 得到还款明细
		Set<Repaymentrecordparticulars> repaymentrecordparticularsSet = repaymentrecord
				.getRepaymentrecordparticulars();

		// 更新明细记录
		for (Iterator it = repaymentrecordparticularsSet.iterator(); it
				.hasNext();) {
			Repaymentrecordparticulars rcp = (Repaymentrecordparticulars) it
					.next();// 得到每条明细
			Loanrecord lc = rcp.getLoanrecord();// 得到购买信息
			if (lc.getSubType() == 1) {// 优先
				double bid = Arith.div(lc.getTenderMoney(),
						loansign.getPriority());
				rcp.setPreRepayMoney(Double.parseDouble(df
						.format(repaymentrecord.getPreRepayMoney() * bid)));
			} else if (lc.getSubType() == 2) {// 夹层
				double bid = Arith.div(lc.getTenderMoney(),
						loansign.getMiddle());
				rcp.setMiddlePreRepayMoney(Double.parseDouble(df
						.format(repaymentrecord.getMiddlePreRepayMoney() * bid)));
			} else if (lc.getSubType() == 3) {// 劣后
				double bid = Arith
						.div(lc.getTenderMoney(), loansign.getAfter());
				rcp.setAfterPreRepayMoney(Double.parseDouble(df
						.format(repaymentrecord.getAfterPreRepayMoney() * bid)));
			}
		}

		double prioMoney = 0.0;
		double midMoney = 0.0;
		double afterMoney = 0.0;
		for (Iterator it = repaymentrecordparticularsSet.iterator(); it
				.hasNext();) {
			Repaymentrecordparticulars rcp = (Repaymentrecordparticulars) it
					.next();// 得到每条明细
			if (rcp.getLoanType() == 1) {
				prioMoney += rcp.getPreRepayMoney();
				continue;
			}
			if (rcp.getLoanType() == 2) {
				midMoney += rcp.getMiddlePreRepayMoney();
				continue;
			}
			if (rcp.getLoanType() == 3) {
				afterMoney += rcp.getAfterPreRepayMoney();
				continue;
			}
		}
		double sonSum = prioMoney + midMoney + afterMoney;// 实际的利息分配后总额
		double parentSum = repaymentrecord.getPreRepayMoney()
				+ repaymentrecord.getMiddlePreRepayMoney()
				+ repaymentrecord.getAfterPreRepayMoney();// 预期的利息总额
		if (sonSum != parentSum) {
			double realAfter = parentSum - prioMoney - midMoney;
			for (Iterator it = repaymentrecordparticularsSet.iterator(); it
					.hasNext();) {
				Repaymentrecordparticulars rcp = (Repaymentrecordparticulars) it
						.next();// 得到每条明细
				if (rcp.getLoanType() == 3) {
					Loanrecord loanrecord = rcp.getLoanrecord();
					double bid = Arith.div(loanrecord.getTenderMoney(),
							loansign.getAfter());
					rcp.setAfterPreRepayMoney(Double.parseDouble(df
							.format(realAfter * bid)));
				}
			}
		}

		return repaymentrecordparticularsSet;
	}

	/**
	 * 根据借款人编号查询借款人的借款标信息
	 * 
	 * @param id
	 *            用户编号
	 * @param state
	 *            借款标状态
	 * @return 返回借款标集合
	 */
	public List<Loansign> getLoanBorrower(Long id, Integer state) {
		String hql = "from Loansign l where l.userbasicsinfo.id=? and l.loanstate=?";
		List<Loansign> loanList = dao.find(hql, id, state);
		return loanList;
	}

	/**
	 * 根据还款编号查询还款记录
	 * 
	 * @param id
	 *            还款编号
	 * @return 返回还款信息
	 */
	public Repaymentrecord getRepaymentId(Long id) {
		Repaymentrecord repaymentrecord = dao.get(Repaymentrecord.class, id);
		return repaymentrecord;
	}

	/**
	 * 查询给借款人发送信息需要在页面上显示的内容
	 * 
	 * @param id
	 *            标编号
	 * @return 返回一个object数组
	 */
	public Object[] get(Long id) {
		Loansign loan = loanSignQuery.getLoansignById(id.toString());
		String sql = "SELECT b.realName,b.isCard,u.phone,c.newaddress,s.companyName,u.email from borrowersbase b,borrowerscompany s,borrowerscontact c,userrelationinfo u WHERE b.id=s.id AND b.id=c.id AND b.userbasicinfo_id=u.id AND b.userbasicinfo_id=?";
		List<Object[]> list = dao.findBySql(sql, loan.getUserbasicsinfo()
				.getId());
		if (list.size() > 0 && null != list) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 得到该标的所有金额
	 * 
	 * @return 返回金额总数
	 */
	public double sumLoanMoney(Long loanid) {
		String sql = "select sum(tenderMoney) from loanrecord where  isSucceed=1 and loanSign_id=?";
		List<Object> list = dao.findBySql(sql, loanid);
		if (null != list && list.size() > 0) {
			Object obj = list.get(0);
			if (null != obj) {
				return Double.parseDouble(obj.toString());
			}
		}

		return 0.00;
	}

	/**
	 * 用户当前购标金额
	 * 
	 * @param loanid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public double sumMyLoanMoney(Long loanid, Long userId) {
		String sql = "select sum(tenderMoney) from loanrecord where  isSucceed=1 and loanSign_id=? and userbasicinfo_id=?";
		List<Object> list = dao.findBySql(sql, loanid, userId);
		if (null != list && list.size() > 0) {
			Object obj = list.get(0);
			if (null != obj) {
				return Double.parseDouble(obj.toString());
			}
		}

		return 0.00;
	}

	/**
	 * 对发标信息进行加密
	 * 
	 * @param subject
	 *            发标信息
	 * @return 加密后的发标信息
	 * @throws TemplateException
	 * @throws IOException
	 */
	public Map<String, String> encryption(RegisterSubject subject)
			throws IOException, TemplateException {
		// 将充值信息转换成xml文件
		String regSubCall = ParseXML.registerSubjectXml(subject);
		// 加密后的信息
		Map<String, String> map = ipsSubmitParam(regSubCall);
		// 将访问地址放在map里
		map.put("url", PayURL.REGSUB);
		return map;
	}

	/**
	 * 转账信息处理
	 * 
	 * @param transfer
	 * @return 加密后的转账信息
	 * @throws IOException
	 * @throws TemplateException
	 */
	public Map<String, String> transferCall(Transfer transfer)
			throws IOException, TemplateException {
		String transferXml = ParseXML.transFerXml(transfer);
		Map<String, String> map = ipsSubmitParam(transferXml);
		map.put("url", PayURL.TRANSFERURL);
		return map;
	}

	/**
	 * 
	 * @param xmlString
	 *            处理过的XML字符串
	 * @return
	 */
	private Map<String, String> ipsSubmitParam(String xmlString) {
		// 生成xml文件字符串
		// String = ParseXML.registration(entity);
		// 将生成的xml文件进行3des加密
		String desede = IpsCrypto
				.triDesEncrypt(xmlString, ParameterIps.getDes_algorithm(),
						ParameterIps.getDesedevector());
		// 将加密后的字符串不换行
		desede = desede.replaceAll("\r\n", "");
		// 将“ 平台 ”账号 、用户注册信息、证书拼接成一个字符串
		StringBuffer argSign = new StringBuffer(ParameterIps.getCert()).append(
				desede).append(ParameterIps.getMd5ccertificate());
		// 将argSign进行MD5加密
		String md5 = IpsCrypto.md5Sign(argSign.toString());
		// 将参数装进map里面
		Map<String, String> map = new HashMap<String, String>();
		map.put("pMerCode", ParameterIps.getCert());
		map.put("p3DesXmlPara", desede);
		map.put("pSign", md5);
		return map;
	}

	public List<Loanrecord> getLoanrecord(Long loanId) {
		String hql = "from Loanrecord lr where lr.loansign.id=" + loanId;
		List<Loanrecord> loanrecords = dao.find(hql);
		return loanrecords;
	}

	/**
	 * 标结束
	 * 
	 * @param loansign
	 */
	public void endLoan(Loansign loansign) {
		// 修改记录状态
		dao.update(loansign);

		// 短信通知借款人
		String title = "标的结束";
		Loansignbasics loansignbasics = loansign.getLoansignbasics();
		Userrelationinfo user = loansign.getUserbasicsinfo()
				.getUserrelationinfo();
		List<Userrelationinfo> users = new ArrayList<>();
		users.add(user);
		String contant = new SMSText().endLoan.replace("#1#",
				loansign.getName());
		Thread sendMsgThread = new SendMsgThead(users, contant, title, 13l,
				messagesettingService);
		sendMsgThread.start();
	}

	/**
	 * 借款人是否是vip
	 * 
	 * @param loansign
	 * @return
	 */
	public boolean loanerVip(Loansign loansign) {
		return userInfoQuery.isPrivilege(loansign.getUserbasicsinfo());
	}

	/**
	 * 查询借款人带回
	 * 
	 * @param username
	 * @param cardno
	 * @return
	 */
	public Object queryBorrowersbasecounts(String username, String cardno) {
		StringBuffer sb = new StringBuffer(
				"SELECT count(u.id) FROM  userbasicsinfo u, userrelationinfo f WHERE u.id = f.id AND u.isCreditor=2");
		if (!username.trim().equals("")) {
			sb.append(" and u.name like '%").append(username).append("%'");
		}
		if (!cardno.equals("")) {
			sb.append(" and f.cardId like '%").append(cardno).append("%'");
		}
		return dao.findObjectBySql(sb.toString());

	}

	/**
	 * 保存
	 * 
	 * @param loansign
	 * @param loansignbasics
	 * @return
	 */
	public boolean save(Loansign loansign, Loansignbasics loansignbasics) {
		DecimalFormat df = new DecimalFormat("0.00");
		Escrow ew = null;
		// 1.保存loansign
		Serializable seria = dao.save(loansign);
		loansign.setId(Long.valueOf(seria.toString()));
		if (loansign.getEscrow().getId() != null) {
			ew = escrowService.queryEscrowByid(loansign.getEscrow().getId());
		}
		loansignbasics.setId(Long.valueOf(seria.toString()));
		dao.save(loansignbasics);
		return true;
	}

	public boolean update(Loansign loansign, Loansignbasics loansignbasics) {

		dao.update(loansign);
		dao.update(loansignbasics);
		return true;
	}
	
	/** 新增联系人相关 */
	public void addMsgReminder(List<MsgReminder> reminderList, Long loanSignId) {
		if(reminderList != null && reminderList.size() > 0) {
			String sql = "delete from msgreminder where loansignId=" + loanSignId;
			dao.executeSql(sql);
			for(MsgReminder reminder : reminderList) {
				reminder.setStatus(1);
				reminder.setLoansignId(loanSignId);
				reminder.setCreateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
			}
			dao.saveOrUpdateAll(reminderList);
		}
	}
}
