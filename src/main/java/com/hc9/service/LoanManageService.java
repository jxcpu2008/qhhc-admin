package com.hc9.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.log.LOG;
import com.hc9.common.redis.IndexDataCache;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Liquidation;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Loansignflow;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Repaymentrecordparticulars;
import com.hc9.dao.entity.Shop;
import com.hc9.dao.entity.Userfundinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.ExpensesInfo;
import com.hc9.model.PageModel;
import com.hc9.model.Payuser;
import com.hc9.model.RepaymentInvestor;

/**
 * 对还款信息封装成对象(只封装了投资者信息)
 * 
 * @author RanQiBing 2014-03-30
 * 
 */
@Service
public class LoanManageService {

	@Resource
	private HibernateSupport dao;

	@Resource
	private LoanSignFund loanSignFund;

	@Resource
	private LoanInfoService infoService;

	private DecimalFormat df = new DecimalFormat("0.00");

	@Resource
	private LoanSignQuery loanSignQuery;

	/**
	 * 根据标编号查询标的所有购买信息
	 * 
	 * @param id
	 *            标编号
	 * @return 返回一个需要还款给借款人的信息集合
	 */
	public List<RepaymentInvestor> listRepayment(Repaymentrecord repaymentrecord) {

		Loansign ls = repaymentrecord.getLoansign();
		// 得到一个标的所有购买记录
		List<ExpensesInfo> expensList = this.investorInteest(repaymentrecord);

		// 创建一个ips还款给投资人的利息
		List<RepaymentInvestor> list = new ArrayList<RepaymentInvestor>();
		// 借款人是否是vip
		// double borrowFee=0.00;
		// 管理费率
		// double
		// pmfeeratio=Arith.div(ls.getShouldPmfee(),ls.getIssueLoan()).doubleValue();
		// 借款费
		// borrowFee=Arith.mul(pmfeeratio,repaymentrecord.getPreRepayMoney()).doubleValue();
		for (ExpensesInfo ex : expensList) {
			RepaymentInvestor repayInfo = new RepaymentInvestor();
			repayInfo.setpCreMerBillNo(ex.getpMerBillNo());
			repayInfo.setpInAcctNo(ex.getIpsNumber());
			Double amt = ex.getInterest() + ex.getMoney();
			repayInfo.setpInAmt(df.format(amt));
			repayInfo.setpInFee("0.00");
			repayInfo
					.setpOutInfoFee(String.valueOf(df.format(ex.getPenalty())));
			list.add(repayInfo);
		}

		return list;
	}

	/**
	 * 获取该标该期所有投资人的还款本金和利息
	 * 
	 * @param user
	 *            用户集合
	 * @param repaymentInfo
	 *            该标的该期的还款信息
	 * @return 用户所要还款的本金和利息集合
	 */
	@SuppressWarnings("unchecked")
	public List<ExpensesInfo> investorInteest(Repaymentrecord repaymentInfo,
			Integer type) {

		List<ExpensesInfo> list = this.getExpenses(repaymentInfo, type);

		return list;
	}

	public List<ExpensesInfo> investorInteest(Repaymentrecord repaymentInfo) {
		List<ExpensesInfo> list = this.getExpenses(repaymentInfo);
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<ExpensesInfo> getExpenses(Repaymentrecord repaymentrecord) {
		List<ExpensesInfo> listRxpenses = new ArrayList<ExpensesInfo>();
		// 得到标的借款记录所有信息
		String hql = "from Loanrecord l where l.loansign.id=? and l.isSucceed=1 ";
		List<Loanrecord> recordLists = dao.find(hql, repaymentrecord
				.getLoansign().getId());

		for (Loanrecord record : recordLists) {
			ExpensesInfo expenses = new ExpensesInfo();
			if (record.getSubType() == 1) {// 投资类型
				// 判断是否存在债权转让
				List<Loansignflow> loansignflows = this.getLoansignflow(record
						.getUserbasicsinfo().getId(), record.getLoansign()
						.getId(), 1);
				expenses = this.monthlyPayInterest(repaymentrecord,
						record.getTenderMoney(), record.getIsPrivilege());
				if (loansignflows.size() > 0) {
					expenses.setUserId(loansignflows.get(0).getUserAuth());
					Userfundinfo user = dao.get(Userfundinfo.class,
							loansignflows.get(0).getUserAuth());
					expenses.setIpsNumber(user.getpIdentNo());
				} else {
					expenses.setUserId(record.getUserbasicsinfo().getId());
					expenses.setIpsNumber(record.getUserbasicsinfo()
							.getUserfundinfo().getpIdentNo());
				}
				expenses.setLoanType(record.getSubType());// 当前用户投资类型
				expenses.setLoanMoney(record.getTenderMoney()); // 当前用户投资金额

			} else if (record.getSubType() == 2 || record.getSubType() == 3) {
				expenses.setUserId(record.getUserbasicsinfo().getId());
				expenses.setIpsNumber(record.getUserbasicsinfo()
						.getUserfundinfo().getpIdentNo());
				expenses.setLoanType(record.getSubType());// 当前用户投资类型
				expenses.setLoanMoney(record.getTenderMoney()); // 当前用户投资金额
				expenses.setMoney(record.getTenderMoney());
				expenses.setManagement(0.00);
				expenses.setPenalty(0.00);
				expenses.setInterest(0.00);
			}
			listRxpenses.add(expenses);
		}
		return listRxpenses;
	}

	/**
	 * 得到项目出借记录
	 * 
	 * @param repaymentInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> queryRecordList(Repaymentrecord repaymentInfo) {

		// 得到借款标的出借记录(相同投资人金额合并)
		String sql = "SELECT l.tenderMoney, u.id, f.pIdentNo, l.pMerBillNo, l.isPrivilege, s.id,l.loanType FROM loanrecord l, loansign s, userbasicsinfo u, userfundinfo f WHERE l.loanSign_id = s.id AND l.userbasicinfo_id = u.id AND u.id = f.id AND loanSign_id = ?";
		List<Object[]> recordList = dao.findBySql(sql, repaymentInfo
				.getLoansign().getId());
		return recordList;
	}

	/**
	 * 获取当前还款的所有投资人本金利息和违约金
	 * 
	 * @param repaymentrecord
	 *            当期还款信息
	 * @return 当期还款给所有投资人的资金信息
	 */
	@SuppressWarnings("unchecked")
	public List<ExpensesInfo> getExpenses(Repaymentrecord repaymentrecord,
			Integer type) {
		List<ExpensesInfo> listRxpenses = new ArrayList<ExpensesInfo>();
		// 得到标的借款记录所有信息
		String hql = "from Loanrecord l where l.loansign.id=? and l.loanType="
				+ type;
		List<Loanrecord> recordLists = dao.find(hql, repaymentrecord
				.getLoansign().getId());
		for (Loanrecord record : recordLists) {
			ExpensesInfo expenses = new ExpensesInfo();
			expenses.setUserId(record.getUserbasicsinfo().getId());
			expenses.setIpsNumber(record.getUserbasicsinfo().getUserfundinfo()
					.getpIdentNo());
			expenses.setLoanType(record.getLoanType());// 当前用户投资类型
			expenses.setLoanMoney(record.getTenderMoney()); // 当前用户投资金额
			listRxpenses.add(expenses);
		}
		return listRxpenses;
	}

	public List<Loansignflow> getLoansignflow(Long userId, Long loanId,
			Integer loanType) {
		String hql2 = "FROM Loansignflow where userDebt=? and loanId=?  and loanType=? and auditResult=1";
		List<Loansignflow> loansignflows = new ArrayList<Loansignflow>();
		loansignflows = dao.find(hql2, userId, loanId, loanType);
		return loansignflows;
	}

	/**
	 * 获取等额本息每一期的本金、利息、管理费
	 * 
	 * @param repaymentrecord
	 *            每一期的还款记录
	 * @param investorMoney
	 *            购买的金额
	 * @param vipNumber
	 *            会员类型(普通会员、特权会员)
	 * @return 返回 ExpensesInfo
	 */
	public ExpensesInfo averageInterest(Repaymentrecord repaymentrecord,
			Double investorMoney, int vipNumber) {
		ExpensesInfo info = new ExpensesInfo();
		// 得到该用户购买的金额和标金额的比例
		/*
		 * BigDecimal big = Arith.div(
		 * repaymentrecord.getLoansign().getIssueLoan(), investorMoney, 4);
		 * info.setInterest(Arith.mul(repaymentrecord.getPreRepayMoney(),
		 * big.doubleValue()).doubleValue());
		 * info.setLoanid(repaymentrecord.getLoansign().getId());
		 * info.setMoney(Arith.mul(repaymentrecord.getMoney(),
		 * big.doubleValue()) .doubleValue()); Double management = loanSignFund
		 * .managementCost( Arith.mul(repaymentrecord.getPreRepayMoney(),
		 * big.doubleValue()), repaymentrecord.getLoansign(),
		 * vipNumber).doubleValue(); info.setManagement(management); // TODO
		 * 还款违约金设定为0 info.setPenalty(0.00);
		 */
		return info;
	}

	/**
	 * 得到相关购买信息的还款本金、利息、管理费(每月付息到期还本)
	 * 
	 * @param repaymentrecord
	 *            还款记录
	 * @param investorMoney
	 *            投资金额
	 * @param vipNumber
	 * @return 返回 ExpensesInfo
	 */
	public ExpensesInfo monthlyPayInterest(Repaymentrecord repaymentrecord,
			Double investorMoney, int vipNumber) {
		ExpensesInfo info = new ExpensesInfo();
		// 利率总和=优先奖励+优先年利率
		Double big = Arith.add(repaymentrecord.getLoansign().getPrioRate(),
				repaymentrecord.getLoansign().getPrioAwordRate());
		// 个人购买金额*利率总和
		Double sumMoney = Arith.mul(investorMoney, big);
		// 计算个人购买的利息
		if (repaymentrecord.getLoansign().getType() == 2) { // 项目
			if (repaymentrecord.getLoansign().getRefunway() == 1) { // 1.按月
																	// 2.按季度
				sumMoney = Arith.div(sumMoney, 12);
			} else if (repaymentrecord.getLoansign().getRefunway() == 2) {
				sumMoney = Arith.div(sumMoney, 4);
			}
		} else if (repaymentrecord.getLoansign().getType() == 3) { // 天标
			sumMoney = Arith.div(sumMoney, 360);
		}
		info.setInterest(sumMoney);
		info.setLoanid(repaymentrecord.getLoansign().getId());
		info.setMoney(investorMoney);
		info.setManagement(0.00);// 管理费
		// TODO 违约金设置为 0
		info.setPenalty(0.00);
		return info;
	}

	/**
	 * 得到相关购买信息的还款本金、利息、管理费(到期一次性付本息)
	 * 
	 * @param repaymentrecord
	 *            还款记录
	 * @param investorMoney
	 *            投资金额
	 * @param vipNumber
	 *            会员类型(普通会员、特权会员)
	 * @return 返回 ExpensesInfo
	 */
	public ExpensesInfo interestOnPrincipal(Repaymentrecord repaymentrecord,
			Double investorMoney, int vipNumber) {
		ExpensesInfo expenses = new ExpensesInfo();
		// 得到当前标的利率
		/*
		 * double interestRate = repaymentrecord.getLoansign().getRate(); //
		 * //得到比例 // BigDecimal big = //
		 * Arith.div(repaymentrecord.getLoansign().getIssueLoan(), //
		 * investorMoney, 4);
		 * 
		 * // 实际利息 Double interest = 0.00; // 违约金 Double penalty = 0.00; // 管理费
		 * Double managementCost = 0.00; int timeNum = 0; try { timeNum =
		 * DateUtils.differenceDate("yyyy-MM-dd",
		 * DateUtils.format("yyyy-MM-dd"), repaymentrecord.getPreRepayDate());
		 * // 提前还款 // 得到所有利息 BigDecimal surplus =
		 * loanSignFund.instalmentInterest( new BigDecimal(investorMoney),
		 * interestRate, repaymentrecord.getLoansign().getMonth(), 1); interest
		 * = surplus.doubleValue(); if (timeNum > 0) { String beginTime =
		 * repaymentrecord.getLoansign() .getLoansignbasics().getCreditTime();
		 * int time = DateUtils.differenceDate("yyyy-MM-dd", beginTime,
		 * DateUtils.format("yyyy-MM-dd")); // 得到天利率 BigDecimal rate =
		 * Arith.div(interestRate, 365); // 实际所得利息 interest = Arith.mul(
		 * Arith.mul(investorMoney, rate.doubleValue()) .doubleValue(),
		 * time).doubleValue(); // 提前违约的金额 penalty =
		 * this.advanceRepayment(interest.doubleValue()); // 计算管理费
		 * managementCost = loanSignFund.managementCost( new
		 * BigDecimal(interest), repaymentrecord.getLoansign(),
		 * vipNumber).doubleValue(); } else if (timeNum < 0) { // 逾期还款 //
		 * 逾期违约的金额 penalty = this.overdueRepayment(investorMoney,
		 * Math.abs(timeNum)); // // 计算管理费 managementCost =
		 * loanSignFund.managementCost( new BigDecimal(interest),
		 * repaymentrecord.getLoansign(), vipNumber).doubleValue(); } else { //
		 * 按时还款 managementCost = loanSignFund.managementCost(surplus,
		 * repaymentrecord.getLoansign(), vipNumber).doubleValue(); }
		 * expenses.setInterest(interest);
		 * expenses.setLoanid(repaymentrecord.getLoansign().getId());
		 * expenses.setManagement(managementCost);
		 * expenses.setMoney(investorMoney); expenses.setPenalty(penalty); }
		 * catch (ParseException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		return expenses;
	}

	/**
	 * 天标的利息、违约金、管理费的算法
	 * 
	 * @param repaymentrecord
	 *            还款记录
	 * @param investorMoney
	 *            还款本金
	 * @param vipNumber
	 *            会员类型(0普通会员、1特权会员)
	 * @return 返回资金信息
	 */
	public ExpensesInfo dayLoan(Repaymentrecord repaymentrecord,
			Double investorMoney, int vipNumber) {
		ExpensesInfo expensesInfo = new ExpensesInfo();
		// 利率
		/*
		 * Double interestRate = repaymentrecord.getLoansign().getRate(); //
		 * 预计使用天数 // int day = repaymentrecord.getLoansign().getUseDay(); // 利息
		 * Double interest = 0.00; // 管理费 Double managementCost = 0.00; //
		 * Double penalty = 0.00; int timeNum = 0; try { timeNum =
		 * DateUtils.differenceDate("yyyy-MM-dd",
		 * DateUtils.format("yyyy-MM-dd"), repaymentrecord.getPreRepayDate());
		 * 
		 * // 得到实际使用天数 int rt = DateUtils.differenceDate("yyyy-MM-dd",
		 * repaymentrecord .getLoansign().getLoansignbasics().getCreditTime(),
		 * DateUtils.format("yyyy-MM-dd")); interest = loanSignFund
		 * .instalmentInterest( new BigDecimal(investorMoney), interestRate,
		 * repaymentrecord.getLoansign().getRealDay() != null ? repaymentrecord
		 * .getLoansign().getRealDay() : rt, 2) .doubleValue(); if (timeNum > 0)
		 * { // 提前还款 // 得到实际使用天数 int time =
		 * DateUtils.differenceDate("yyyy-MM-dd",
		 * repaymentrecord.getLoansign().getLoansignbasics() .getCreditTime(),
		 * DateUtils.format("yyyy-MM-dd")); // 得到实际的利息 interest =
		 * loanSignFund.instalmentInterest( new BigDecimal(investorMoney),
		 * interestRate, time, 2) .doubleValue(); // 提前还款的违约金 penalty =
		 * this.advanceAndOverdue(interest); // 得到管理费 managementCost =
		 * loanSignFund.managementCost( new BigDecimal(interest),
		 * repaymentrecord.getLoansign(), vipNumber).doubleValue(); } else if
		 * (timeNum < 0) { // 逾期还款 // 逾期违约的金额 penalty =
		 * this.overdueRepayment(investorMoney, Math.abs(timeNum)); // 计算管理费
		 * managementCost = loanSignFund.managementCost( new
		 * BigDecimal(interest), repaymentrecord.getLoansign(),
		 * vipNumber).doubleValue(); } else { // 按时还款 // 计算管理费 managementCost =
		 * loanSignFund.managementCost( new BigDecimal(interest),
		 * repaymentrecord.getLoansign(), vipNumber).doubleValue(); }
		 * 
		 * expensesInfo.setInterest(interest);
		 * expensesInfo.setManagement(managementCost);
		 * expensesInfo.setLoanid(repaymentrecord.getLoansign().getId());
		 * expensesInfo.setMoney(investorMoney);
		 * expensesInfo.setPenalty(penalty); } catch (ParseException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
		return expensesInfo;
	}

	/**
	 * 计算天标提前的利息 (提前利息=未支付利息*50%)
	 * 
	 * @param interest
	 *            未支付的利息
	 * @return 返回利息
	 */
	public Double advanceAndOverdue(Double interest) {
		return Arith.mul(interest, 0.05);
	}

	/**
	 * 普通标的提前还款违约利息 (提前还款违约金 = 剩余未支付利息*提前还款比例)
	 * 
	 * @param interest
	 *            剩余未支付利息
	 * @param scale
	 *            提前还款比例
	 * @return 返回逾期违约金额
	 */
	@SuppressWarnings("unchecked")
	public Double advanceRepayment(Double interest) {
		String hql = "from Costratio c";
		List<Costratio> list = dao.find(hql);
		Costratio costratio = list.get(0);
		// return Arith.mul(interest,
		// costratio.getPrepaymentRate()).doubleValue();
		return null;
	}

	/**
	 * 普通标逾期还款违约利息 (逾期违约金额 = 借款金额*逾期利率*逾期天数)
	 * 
	 * @param money
	 *            借款金额
	 * @param scale
	 *            逾期利率
	 * @param day
	 *            逾期天数
	 * @return 返回逾期违约金额
	 */
	public Double overdueRepayment(Double money, int day) {
		String hql = "from Costratio c";
		List<Costratio> list = dao.find(hql);
		Costratio costratio = list.get(0);
		return Arith
				.mul(Arith.mul(money, costratio.getOverdueRepayment()), day);
	}

	/**
	 * 查找指定id的标
	 * 
	 * @return
	 */
	public List<Loansign> getList(Long id) {
		String hql = "from Loansign l WHERE ((l.loanType=1 AND l.refundWay=3) OR l.loanType=2 and l.loanType=3) AND l.loanstate=3 AND l.userbasicsinfo.id=?";
		List<Loansign> loanList = dao.find(hql, id);
		return loanList;
	}

	/**
	 * 得到发布中的借款给标
	 * 
	 * @param request
	 * @beginTime 开始时间
	 * @endTime 结束时间
	 * @return 返回页面路径
	 */
	public PageModel getAchieveLoan(HttpServletRequest request, Long userid,
			String beginTime, String endTime, PageModel page) {
		String sqlCount = "select count(l.id)";
		StringBuffer achieveSql = new StringBuffer(
				"SELECT s.loanNumber, s.loanTitle, l.issueLoan, l.rate, CASE WHEN l.refundWay = 1 THEN '按月等额本息' WHEN l.refundWay = 2 THEN '按月付息到期还本' ELSE '到期一次性还本息' END, l.`month`, l.publishTime, IFNULL(( SELECT sum(tenderMoney) FROM loanrecord WHERE loanrecord.loanSign_id = l.id ), 0 ) / issueLoan ");
		StringBuffer sql = new StringBuffer(
				" FROM loansign l,loansignbasics s WHERE l.id = s.id "
						+ "AND l.loanstate = 2 AND l.userbasicinfo_id = ")
				.append(userid);
		if (null != beginTime && !"".equals(beginTime)) {
			sql.append(" AND l.publishTime>='").append(beginTime).append("'");
		}
		if (null != endTime && !"".equals(endTime)) {
			sql.append(" AND l.publishTime<='").append(endTime).append("'");
		}
		sqlCount = sqlCount + sql.toString();
		achieveSql = achieveSql.append(sql);
		achieveSql
				.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.SRSRUES_TEN).append(",")
				.append(Constant.SRSRUES_TEN);
		page.setTotalCount(dao.queryNumberSql(sqlCount).intValue());
		List<Object[]> list = dao.findBySql(achieveSql.toString());
		page.setList(list);
		return page;
	}

	/**
	 * 得到还款中的借款给标
	 * 
	 * @param request
	 * @beginTime 开始时间
	 * @endTime 结束时间
	 * @return 返回页面路径
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public PageModel getRepaymentLoan(HttpServletRequest request, Long userid,
			String beginTime, String endTime, PageModel page, String month)
			throws ParseException {
		String sqlCount = "select count(r.id) ";
		StringBuffer repaymentSql = new StringBuffer(
				"SELECT r.id, s.loanNumber, s.loanTitle, l.issueLoan, l.rate, CASE WHEN l.refundWay = 1"
						+ " THEN '按月等额本息' WHEN l.refundWay = 2 THEN '按月付息到期还本' ELSE '到期一次性还本息' END, l.`month`,"
						+ " l.useDay, s.creditTime, r.preRepayDate, SUM(r.money + r.preRepayMoney),r.repayState ");
		StringBuffer sql = new StringBuffer(
				"FROM loansign l, repaymentrecord r, loansignbasics s WHERE l.id = r.loanSign_id AND l.id = s.id AND"
						+ " l.loanstate = 3 AND l.userbasicinfo_id = ")
				.append(userid);
		if (null != month && !"".equals(month)) {
			if (Integer.parseInt(month) > 0) {
				String date = DateUtils.add("yyyy-MM-dd", Calendar.MONTH,
						Integer.parseInt(month));
				sql.append(" and r.preRepayDate<='").append(date).append("'");
			}
		}
		if (null != beginTime && !"".equals(beginTime)) {
			sql.append(" and r.preRepayDate>='").append(beginTime).append("'");
		}
		if (null != endTime && !"".equals(endTime)) {
			sql.append(" and r.preRepayDate<='").append(endTime).append("'");
		}

		sqlCount = sqlCount + sql.toString();
		sql.append(" GROUP BY r.id");
		repaymentSql.append(sql);
		repaymentSql
				.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.SRSRUES_TEN).append(",")
				.append(Constant.SRSRUES_TEN);
		page.setTotalCount(dao.queryNumberSql(sqlCount).intValue());
		List<Object[]> list = dao.findBySql(repaymentSql.toString());
		page.setList(list);
		return page;
	}

	// /**
	// * 得到逾期中的借款给标
	// *
	// * @param request
	// * @beginTime 开始时间
	// * @endTime 结束时间
	// * @return 返回页面路径
	// */
	// @SuppressWarnings("unchecked")
	// public PageModel getOverdueLoan(HttpServletRequest request,Long userid,
	// String beginTime, String endTime,PageModel page) {
	// StringBuffer sqlCount = new StringBuffer("select count(r.id)");
	// StringBuffer oberdueSql = new
	// StringBuffer("SELECT r.id, s.loanNumber, s.loanTitle, l.issueLoan, l.rate, CASE WHEN l.refundWay = 1 THEN '按月等额本息' WHEN l.refundWay = 2 THEN '按月付息到期还本' ELSE '到期一次性还本息' END, l.`month`, l.useDay, s.creditTime, r.preRepayDate, SUM(r.money + r.preRepayMoney)");
	// StringBuffer sql = new
	// StringBuffer(" FROM loansign l, repaymentrecord r, loansignbasics s WHERE l.id = r.loanSign_id AND l.id = s.id AND ( r.repayState = 1 OR r.repayState = 3 ) AND r.preRepayDate < '").append(DateUtils.format("yyyy-MM-dd")).append("' AND l.userbasicinfo_id = ").append(userid);
	// if(null!=beginTime&&!"".equals(beginTime)){
	// sql.append(" and r.r.preRepayDate>='").append(beginTime).append("'");
	// }
	// if(null!=endTime&&!"".equals(endTime)){
	// sql.append(" and r.r.preRepayDate<='").append(endTime).append("'");
	// }
	// sql.append(" GROUP BY r.id");
	// sqlCount.append(sql);
	// oberdueSql.append(sql);
	// oberdueSql.append(" LIMIT ").append((page.getPageNum()-Constant.STATUES_ONE)*Constant.SRSRUES_TEN).append(",").append(Constant.SRSRUES_TEN);
	// page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());
	// List<Object[]> list= dao.findBySql(oberdueSql.toString());
	// page.setList(list);
	// return page;
	// }
	/**
	 * 得到还款中的借款给标
	 * 
	 * @param request
	 * @beginTime 开始时间
	 * @endTime 结束时间
	 * @return 返回页面路径
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public PageModel getHasTheRepaymentLoan(HttpServletRequest request,
			Long userid, String beginTime, String endTime, PageModel page)
			throws ParseException {
		String sqlCount = "select count(r.id)";
		StringBuffer repaymentSql = new StringBuffer(
				"SELECT r.id, s.loanNumber, s.loanTitle, l.issueLoan, l.rate, CASE WHEN l.refundWay = 1"
						+ " THEN '按月等额本息' WHEN l.refundWay = 2 THEN '按月付息到期还本' ELSE '到期一次性还本息' END, l.`month`,"
						+ " l.useDay, s.creditTime, r.preRepayDate, SUM(r.money + r.preRepayMoney), r.overdueInterest ");
		StringBuffer sql = new StringBuffer(
				"FROM loansign l, repaymentrecord r, loansignbasics s WHERE l.id = r.loanSign_id AND l.id = s.id AND"
						+ " l.loanstate!=4 and (r.repayState != 1 and r.repayState!=3) AND l.userbasicinfo_id = ")
				.append(userid);
		if (null != beginTime && !"".equals(beginTime)) {
			sql.append(" and r.preRepayDate>='").append(beginTime).append("'");
		}
		if (null != endTime && !"".equals(endTime)) {
			sql.append(" and r.preRepayDate<='").append(endTime).append("'");
		}
		sqlCount = sqlCount + sql.toString();

		repaymentSql.append(sql);
		sql.append(" GROUP BY r.id");
		repaymentSql
				.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.SRSRUES_TEN).append(",")
				.append(Constant.SRSRUES_TEN);
		page.setTotalCount(dao.queryNumberSql(sqlCount).intValue());
		List<Object[]> list = dao.findBySql(repaymentSql.toString());
		page.setList(list);
		return page;
	}

	/**
	 * 得到已完成的借款给标
	 * 
	 * @param request
	 * @beginTime 开始时间
	 * @endTime 结束时间
	 * @return 返回页面路径
	 */
	@SuppressWarnings("unchecked")
	public PageModel getUnderwayLoan(HttpServletRequest request, Long userid,
			String beginTime, String endTime, PageModel page) {
		String sqlCount = "select count(l.id) from loansign l where l.loanstate=4 and l.userbasicinfo_id=?";
		StringBuffer sql = new StringBuffer(
				"SELECT s.loanNumber, s.loanTitle,l.issueLoan,l.rate,CASE WHEN l.refundWay = 1 THEN '按月等额本息' WHEN l.refundWay = 2 THEN '按月付息到期还本' ELSE '到期一次性还本息' END, l.`month`, l.useDay FROM loansign l,loansignbasics s WHERE l.id = s.id AND l.loanstate = 4 AND l.userbasicinfo_id=")
				.append(userid);
		sql.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.SRSRUES_TEN).append(",")
				.append(Constant.SRSRUES_TEN);
		page.setTotalCount(dao.queryNumberSql(sqlCount, userid).intValue());
		List<Object[]> list = dao.findBySql(sql.toString());
		page.setList(list);
		return page;
	}

	/**
	 * 成功案例:项目
	 * 
	 * @param month
	 *            月份
	 * @param loanstate
	 *            状态
	 * @param type
	 *            类型
	 * @param page
	 *            分页对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel getloanList(String loanType, String city, String money,
			PageModel page) {

		StringBuffer sql = new StringBuffer(
				"select ls.id,ls.`name`,s.issueLoan,s.loanimg,ls.status,"
						+ " ls.remoney,ls.getMoneyWay,ls.remark,ls.state ");

		StringBuffer sqlCount = new StringBuffer("select count(ls.id) ");

		StringBuffer sqlsb = new StringBuffer(
				" FROM loansign ls,loansignbasics s WHERE ls.id = s.id AND  ls.status=4");

		if (!"".equals(loanType) && null != loanType) {
			sqlsb.append(" and ls.loanType=")
					.append(Integer.parseInt(loanType));
		}
		if (!"".equals(city) && null != city) {
			sqlsb.append(" and ls.city= '").append(city).append("'");
		}
		if (!"".equals(money) && null != money) {
			if (Integer.parseInt(money) == 1) {
				sqlsb.append(" and s.issueLoan<=").append(100000);
			}
			if (Integer.parseInt(money) == 2) {
				sqlsb.append(" and s.issueLoan>").append(100000)
						.append(" and s.issueLoan<=").append(1000000);
			}
			if (Integer.parseInt(money) == 3) {
				sqlsb.append(" and ls.issueLoan>=").append(1000000)
						.append(" and s.issueLoan<=").append(2000000);
			}

			if (Integer.parseInt(money) == 4) {
				sqlsb.append(" and s.issueLoan>").append(2000000);

			}
		}

		sqlsb.append(" order by ls.id desc,ls.state asc");

		page.setTotalCount(dao
				.queryNumberSql(sqlCount.append(sqlsb).toString()).intValue());

		sqlsb.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.STATUES_THERE).append(",")
				.append(page.getNumPerPage());
		List<Loansign> list = dao.findBySql(sql.append(sqlsb).toString());
		page.setList(list);
		return page;
	}

	/**
	 * 成功案例：店铺
	 * 
	 * @param month
	 * @param loanstate
	 * @param type
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel getprojectList(String loanType, String city, String money,
			PageModel page) {
		StringBuffer sqlshop = new StringBuffer(
				" select pb.project_id,pr.pName,pb.amount,pr.pimages,pr.state ");

		StringBuffer sqlsbshop = new StringBuffer(
				" from projectbase pb,project pr where pb.project_id=pr.id and  pr.state=5 ");

		StringBuffer sqlCountshop = new StringBuffer("select count(pb.id) ");
		if (!"".equals(loanType) && null != loanType) {
			sqlsbshop.append(" and pr.pIndustrie1='").append(loanType)
					.append("'");
		}
		if (!"".equals(city) && null != city) {
			sqlsbshop.append(" and pr.pCity='").append(city).append("'");
		}
		if (!"".equals(money) && null != money) {
			if (Integer.parseInt(money) == 1) {
				sqlsbshop.append(" and pb.amount<=").append(100000);
			}
			if (Integer.parseInt(money) == 2) {
				sqlsbshop.append(" and pb.amount>").append(100000)
						.append(" and pb.amount<=").append(1000000);
			}
			if (Integer.parseInt(money) == 3) {
				sqlsbshop.append(" and pb.amount>=").append(1000000)
						.append(" and pb.amount<=").append(2000000);
			}

			if (Integer.parseInt(money) == 4) {
				sqlsbshop.append(" and pb.amount>").append(2000000);

			}
		}
		sqlsbshop.append(" order by pb.id desc");
		page.setTotalCount(dao.queryNumberSql(
				sqlCountshop.append(sqlsbshop).toString()).intValue());

		sqlsbshop
				.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.STATUES_SIX).append(",")
				.append(page.getNumPerPage());
		List<Shop> list = dao.findBySql(sqlshop.append(sqlsbshop).toString());
		page.setList(list);
		return page;
	}

	/**
	 * 获取项目
	 * 
	 * @param month
	 * @param loanstate
	 * @param type
	 * @param choutype
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel getLoanList(Integer type, Integer state, Integer month,
			Integer rate, PageModel page) {

		StringBuffer sql = new StringBuffer(
				"select ls.id, ls.name,ls.remonth,ls.issueLoan,ls.loanUnit,ls.rest_money,ls.prio_rate,ls.prio_aword_rate,ls.type,ls.status,ls.loansignType_id,ls.activityStatus "
						+ "from loansign ls where ls.state=2  ");
		StringBuffer sqlCount = new StringBuffer(
				"select count(ls.id) from loansign ls where ls.state=2  ");
		StringBuffer sqlsb = new StringBuffer(" ");

		if (type != null) {
			sqlsb.append(" and ls.loansignType_id=").append(type);
		}

		if (rate != null) {
			if (rate == 8) {
				sqlsb.append(" and ls.prio_rate>").append(0.08)
						.append(" and ls.prio_rate<=").append(0.12);
			} else if (rate == 12) {
				sqlsb.append(" and ls.prio_rate>").append(0.12)
						.append(" and ls.prio_rate<= ").append(0.15);
			}

		}

		if (month != null) {
			sqlsb.append(" and ls.remonth=").append(month);
		}

		if (null != state) { // 金额
			if (state == 1) {
				sqlsb.append(" and ls.status in(1,2,3,4,5) ");
			} else if (state == 2) {
				sqlsb.append(" and ls.status in(6,7) ");
			} else if (state == 3) {
				sqlsb.append(" and ls.status in(8) ");
			}

		} else {
			sqlsb.append(" and  ls.status !=0 and ls.status !=9");
		}
		StringBuffer sqlsb1 = new StringBuffer(" ORDER BY ls.rest_money DESC,ls.publish_time desc ");

		page.setTotalCount(dao.queryNumberSql(
				sqlCount.append(sqlsb).append(sqlsb1).toString()).intValue());

		StringBuffer sqlsb2 = new StringBuffer(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());
		List<Loansign> list = dao.findBySql(sql.append(sqlsb).append(sqlsb1)
				.append(sqlsb2).toString());

		page.setList(list);
		return page;
	}

	/**
	 * H5获取项目
	 * 
	 * @param month
	 * @param loanstate
	 * @param type
	 * @param choutype
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel getLoanList(PageModel page) {
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append(" select count(ls.id) from loansign ls where ls.state=2 ");
		sqlCount.append(" and  ls.status !=0 and ls.status !=9 ");
		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select ls.id, ls.name,ls.remonth,ls.issueLoan,ls.loanUnit,ls.rest_money,ls.prio_rate,ls.prio_aword_rate,ls.type,ls.status,ls.loansignType_id,ls.activityStatus ");
		sql.append(" from loansign ls where ls.state=2 ");
		sql.append(" and ls.status !=0 and ls.status !=9 ");
		sql.append(" ORDER BY ls.rest_money DESC,ls.publish_time desc ");
		sql.append(" LIMIT ");
		sql.append((page.getPageNum() - Constant.STATUES_ONE)* page.getNumPerPage());
		sql.append(",");
		sql.append(page.getNumPerPage());
		
		List<Loansign> list = dao.findBySql(sql.toString());
		page.setList(list);
		
		return page;
	}
	
	/**
	 * H5热门推荐
	 * 
	 * @param month
	 * @param loanstate
	 * @param type
	 * @param choutype
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List getLoanRecommandList(String key) {
		StringBuffer sql = new StringBuffer(
				"select ls.id, ls.name,ls.remonth,ls.issueLoan,ls.loanUnit,ls.rest_money,ls.prio_rate,ls.prio_aword_rate,ls.type,ls.status,ls.loansignType_id,ls.activityStatus "
						+ "from loansign ls  where ls.state=2  ");
		sql.append(" and (ls.status >0 and ls.status <9) and ls.recommend=1");
		sql.append(" ORDER BY ls.rest_money DESC,ls.publish_time desc ");
		sql.append(" LIMIT 0,2");
		List loanRecommandList = dao.findBySql(sql.toString());
        try{
        	IndexDataCache.set(key, loanRecommandList);
        }catch(Exception e){
        	LOG.error("更新缓存失败："+e);
        }
		return loanRecommandList;
	}

	@SuppressWarnings("unchecked")
	public PageModel getLoanListCir(String time, String money, PageModel page) {

		StringBuffer sql = new StringBuffer(
				"select ls.id,ls.name,s.issueLoan,s.s.loanimg,ls.status,"
						+ " ls.remoney,ls.getMoneyWay,ls.remark,ls.state ");

		StringBuffer sqlCount = new StringBuffer("select count(ls.id) ");

		StringBuffer sqlsb = new StringBuffer(
				" FROM loansign ls,loansignbasics s WHERE ls.id = s.id  AND ls.isdet=1");

		if (!"".equals(money) && null != money) {
			if (Integer.parseInt(money) == 1) {
				sqlsb.append(" and s.issueLoan<=").append(100000);
			}
			if (Integer.parseInt(money) == 2) {
				sqlsb.append(" and s.issueLoan>").append(100000)
						.append(" and s.issueLoan<=").append(1000000);
			}
			if (Integer.parseInt(money) == 3) {
				sqlsb.append(" and s.issueLoan>=").append(1000000)
						.append(" and s.issueLoan<=").append(2000000);
			}
			if (Integer.parseInt(money) == 4) {
				sqlsb.append(" and s.issueLoan>").append(2000000);
			}
		}
		sqlsb.append(" order by ls.id desc,ls.state asc");

		page.setTotalCount(dao
				.queryNumberSql(sqlCount.append(sqlsb).toString()).intValue());

		sqlsb.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.SRSRUES_TEN).append(",")
				.append(page.getNumPerPage());
		List<Loansign> list = dao.findBySql(sql.append(sqlsb).toString());
		page.setList(list);
		return page;
	}

	/*	*//**
	 * 获取债权转让
	 * 
	 * @param money
	 *            标的借款金额
	 * @param month
	 *            标的期限
	 * @param type
	 *            还款类型
	 * @param rank
	 *            借款者信用等级
	 * @param loanType
	 *            借款标类型
	 * @param page
	 *            分页对象
	 * @return 返回分页对象
	 */
	/*
	 * @SuppressWarnings("unchecked") public PageModel getLoanListCir(String
	 * money, String month, String loanstate, String rank, String loanType,
	 * PageModel page) { StringBuffer sql = new StringBuffer(
	 * "SELECT l.id,s.loanTitle,l.userbasicinfo_id,l.issueLoan,l.rate,l. MONTH,l.useDay,l.loanstate,ROUND(IFNULL((SELECT SUM(loanrecord.tenderMoney) FROM loanrecord WHERE loanrecord.loanSign_id = l.id),0) / l.issueLoan,2),(SELECT typename FROM loansign_type where loansign_type.id=l.loansignType_id),l.loansignType_id,l.loanType,s.views,l.refundWay,ROUND(l.issueLoan/l.loanUnit),(SELECT sum(tenderMoney) as tenderMoney FROM loanrecord where loanrecord.loanSign_id=l.id)"
	 * );
	 * 
	 * StringBuffer sqlCount = new StringBuffer("select count(l.id) ");
	 * StringBuffer sqlsb = new StringBuffer(
	 * " FROM loansign l,loansignbasics s,borrowersbase b WHERE l.id = s.id and l.userbasicinfo_id=b.userbasicinfo_id AND l.loanstate!=1 AND l.loanType = 6 AND l.isShow=1"
	 * ); // 判断金额 if (null != money && !"".equals(money)) { if
	 * (Integer.parseInt(money) == 1) { // 10万以内
	 * sqlsb.append(" and l.issueLoan<=").append(100000); } if
	 * (Integer.parseInt(money) == 2) {// 10万到100万
	 * sqlsb.append(" and l.issueLoan>").append(100000)
	 * .append(" and l.issueLoan>").append(1000000); } if
	 * (Integer.parseInt(money) == 3) {// 100万到200万
	 * sqlsb.append(" and l.issueLoan>").append(1000000)
	 * .append(" and l.issueLoan>").append(2000000); } if
	 * (Integer.parseInt(money) == 4) {// 200万以上
	 * sqlsb.append(" and l.issueLoan>").append(2000000); } }
	 * 
	 * // 判断标的还款类型 if (null != loanstate && !"".equals(loanstate)) { if
	 * (Integer.parseInt(loanstate) == 1) {
	 * sqlsb.append(" and (l.loanstate =2) "); } if (Integer.parseInt(loanstate)
	 * == 2) { sqlsb.append(" and (l.loanstate =2) "); } if
	 * (Integer.parseInt(loanstate) == 3) {
	 * sqlsb.append(" and (l.loanstate =3) "); } if (Integer.parseInt(loanstate)
	 * == 4) { sqlsb.append(" and (l.loanstate =4) "); } } // 判断标的期限 if (null !=
	 * month && !"".equals(month)) { if (Integer.parseInt(month) == 1) {
	 * sqlsb.append(" and (l.`month` <=3) "); } if (Integer.parseInt(month) ==
	 * 2) { sqlsb.append(" and ((l.`month` > 3 and l.`month`< 6) ) "); } if
	 * (Integer.parseInt(month) == 3) {
	 * sqlsb.append(" and ((l.`month` > 6 and l.`month`< 9)) "); } if
	 * (Integer.parseInt(month) == 4) {
	 * sqlsb.append(" and ((l.`month` > 9 and l.`month`< 12)) "); } if
	 * (Integer.parseInt(month) == 5) { sqlsb.append(" and (l.`month` >12) "); }
	 * } // 借款标类型 if (null != loanType && !"".equals(loanType)) { if
	 * (Integer.parseInt(loanType) == 1) { sqlsb.append(" and l.loanType = 2");
	 * } else if (Integer.parseInt(loanType) > 1) {
	 * sqlsb.append(" and l.loanType = 1 and l.loansignType_id=")
	 * .append(Integer.parseInt(loanType) - 1); } } // 借款者信用等级 if (null != rank
	 * && !"".equals(rank)) { if (Integer.parseInt(rank) == 1) {
	 * sqlsb.append(" and b.suminte > 10 and b.suminte <=20"); } else if
	 * (Integer.parseInt(rank) == 2) {
	 * sqlsb.append(" and b.suminte > 30 and b.suminte <=40"); } else if
	 * (Integer.parseInt(rank) == 3) {
	 * sqlsb.append(" and b.suminte > 50 and b.suminte <=60"); } else if
	 * (Integer.parseInt(rank) == 4) {
	 * sqlsb.append(" and b.suminte > 80 and b.suminte <=110"); } else if
	 * (Integer.parseInt(rank) == 5) { sqlsb.append(" and b.suminte > 180"); } }
	 * 
	 * sqlsb.append(" order by l.id desc,l.loanState asc,l.publishTime desc");
	 * page.setTotalCount(dao
	 * .queryNumberSql(sqlCount.append(sqlsb).toString()).intValue());
	 * sqlsb.append(" LIMIT ") .append((page.getPageNum() -
	 * Constant.STATUES_ONE) Constant.STATUES_SIX).append(",")
	 * .append(Constant.STATUES_SIX); List<Loansign> list =
	 * dao.findBySql(sql.append(sqlsb).toString()); page.setList(list); return
	 * page; }
	 */

	@SuppressWarnings("unchecked")
	public PageModel getAppLoanListCir(String money, String month,
			String loanstate, String rank, String loanType, PageModel page) {
		StringBuffer sql = new StringBuffer(
				"SELECT l.id,s.loanTitle,l.userbasicinfo_id,l.issueLoan,l.rate,l. MONTH,l.useDay,l.loanstate,ROUND(IFNULL((SELECT SUM(loanrecord.tenderMoney) FROM loanrecord WHERE loanrecord.loanSign_id = l.id),0) / l.issueLoan,2),(SELECT typename FROM loansign_type where loansign_type.id=l.loansignType_id),l.loansignType_id,l.loanType,s.views,l.refundWay,ROUND(l.issueLoan/l.loanUnit),(SELECT sum(tenderMoney) as tenderMoney FROM loanrecord where loanrecord.loanSign_id=l.id)");
		StringBuffer sqlCount = new StringBuffer("select count(l.id) ");
		StringBuffer sqlsb = new StringBuffer(
				" FROM loansign l,loansignbasics s,borrowersbase b WHERE l.id = s.id and l.userbasicinfo_id=b.userbasicinfo_id AND l.loanstate!=1 AND l.loanType = 6 AND l.isShow=1");
		// 判断金额
		if (null != money && !"".equals(money)) {
			if (Integer.parseInt(money) == 1) { // 10万以内
				sqlsb.append(" and l.issueLoan<=").append(100000);
			}
			if (Integer.parseInt(money) == 2) {// 10万到100万
				sqlsb.append(" and l.issueLoan>").append(100000)
						.append(" and l.issueLoan>").append(1000000);
			}
			if (Integer.parseInt(money) == 3) {// 100万到200万
				sqlsb.append(" and l.issueLoan>").append(1000000)
						.append(" and l.issueLoan>").append(2000000);
			}
			if (Integer.parseInt(money) == 4) {// 200万以上
				sqlsb.append(" and l.issueLoan>").append(2000000);
			}
		}
		// 判断标的还款类型
		if (null != loanstate && !"".equals(loanstate)) {
			if (Integer.parseInt(loanstate) == 1) {
				sqlsb.append(" and (l.loanstate =2) ");
			}
			if (Integer.parseInt(loanstate) == 2) {
				sqlsb.append(" and (l.loanstate =2) ");
			}
			if (Integer.parseInt(loanstate) == 3) {
				sqlsb.append(" and (l.loanstate =3) ");
			}
			if (Integer.parseInt(loanstate) == 4) {
				sqlsb.append(" and (l.loanstate =4) ");
			}
		}
		// 判断标的期限
		if (null != month && !"".equals(month)) {
			if (Integer.parseInt(month) == 1) {
				sqlsb.append(" and (l.`month` <=3) ");
			}
			if (Integer.parseInt(month) == 2) {
				sqlsb.append(" and ((l.`month` > 3 and l.`month`< 6) ) ");
			}
			if (Integer.parseInt(month) == 3) {
				sqlsb.append(" and ((l.`month` > 6 and l.`month`< 9)) ");
			}
			if (Integer.parseInt(month) == 4) {
				sqlsb.append(" and ((l.`month` > 9 and l.`month`< 12)) ");
			}
			if (Integer.parseInt(month) == 5) {
				sqlsb.append(" and (l.`month` >12) ");
			}
		}
		// 借款标类型
		if (null != loanType && !"".equals(loanType)) {
			if (Integer.parseInt(loanType) == 1) {
				sqlsb.append(" and l.loanType = 2");
			} else if (Integer.parseInt(loanType) > 1) {
				sqlsb.append(" and l.loanType = 1 and l.loansignType_id=")
						.append(Integer.parseInt(loanType) - 1);
			}
		}
		// 借款者信用等级
		if (null != rank && !"".equals(rank)) {
			if (Integer.parseInt(rank) == 1) {
				sqlsb.append(" and b.suminte > 10 and b.suminte <=20");
			} else if (Integer.parseInt(rank) == 2) {
				sqlsb.append(" and b.suminte > 30 and b.suminte <=40");
			} else if (Integer.parseInt(rank) == 3) {
				sqlsb.append(" and b.suminte > 50 and b.suminte <=60");
			} else if (Integer.parseInt(rank) == 4) {
				sqlsb.append(" and b.suminte > 80 and b.suminte <=110");
			} else if (Integer.parseInt(rank) == 5) {
				sqlsb.append(" and b.suminte > 180");
			}
		}

		sqlsb.append(" order by l.id desc,l.loanState asc,l.publishTime desc");
		page.setTotalCount(dao
				.queryNumberSql(sqlCount.append(sqlsb).toString()).intValue());
		sqlsb.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.STATUES_SIX).append(",")
				.append(page.getNumPerPage());
		List<Loansign> list = dao.findBySql(sql.append(sqlsb).toString());
		page.setList(list);
		return page;
	}

	/**
	 * 获取上期还款滞纳金
	 * 
	 * @param upPeriodsNum
	 * @param repaymentInfo
	 */
	public double getupPeridosMoney(int upPeriodsNum,
			Repaymentrecord repaymentInfo) {
		double upperidosMoney = 0.00;
		String sql = "SELECT * from repaymentrecord  r WHERE r.loanSign_id=?   and r.periods="
				+ upPeriodsNum + "";
		List<Repaymentrecord> list = dao.findBySql(sql, Repaymentrecord.class,
				repaymentInfo.getLoansign().getId());
		try {
			if (list != null) {
				if (list.get(0).getRepayState() == 4) {
					int diffNum = DateUtils.differenceDateSimple(list.get(0)
							.getPreRepayDate(), list.get(0).getRepayTime());
					if (diffNum > 0) {
						System.out.println("出现滞纳");
						upperidosMoney = repaymentInfo.getLoansign()
								.getIssueLoan() * 0.24 / 365 * diffNum;
					}
				}
			}
		} catch (Exception e) {

		}

		return upperidosMoney;
	}

	/**
	 * 获取上期还款滞纳天数
	 * 
	 * @param upPeriodsNum
	 * @param repaymentInfo
	 */
	public Map<String, String> getupPeridosDateAndMoney(
			Repaymentrecord repaymentInfo, Costratio cost) {
		Map<String, String> map = new HashMap<String, String>();
		Double priOut = 0.0; // 优先滞纳金
		Double midOut = 0.0; // 夹层滞纳金
		Double aftOut = 0.0;// 劣后滞纳金
		// Double feeOut=0.0;//平台收取滞纳金
		int diffNum = 0;
		/**逾期滞纳金比例*/
		Double overdueRepayment = cost.getOverdueRepayment();
		if (repaymentInfo.getLoansign().getType() == 3) {
			// 获取天标滞纳天数
			String sql = "SELECT * from repaymentrecord  r WHERE r.loanSign_id=? ";
			List<Repaymentrecord> list = dao.findBySql(sql,
					Repaymentrecord.class, repaymentInfo.getLoansign().getId());
			try {
				if (list != null) {
					if (list.get(0).getRepayState() == 1) {
						diffNum = DateUtils.differenceDateSimple(list.get(0)
								.getPreRepayDate(), DateUtils
								.formatSimple(new Date()));
						if (diffNum > 0) {
							System.out.println("出现滞纳");
							/** 需要计算滞纳金的优先金额 */
							Double preTotal = Arith.add(repaymentInfo.getMoney(), repaymentInfo.getPreRepayMoney());
							priOut = Arith.round(BigDecimal.valueOf(preTotal * overdueRepayment * diffNum), 2)
									.doubleValue();// 优先滞纳金
							
							/** 需要计算滞纳金的夹层金额 */
							Double midTotal = Arith.add(
									repaymentInfo.getMiddleMoney(), repaymentInfo.getMiddlePreRepayMoney());
							midOut = Arith.round(BigDecimal.valueOf(midTotal * overdueRepayment * diffNum), 2)
									.doubleValue();// 夹层滞纳金
							
							/** 需要计算滞纳金的劣后金额 */
							Double afterTotal = Arith.add(
									repaymentInfo.getAfterMoney(), repaymentInfo.getAfterPreRepayMoney());
							aftOut = Arith.round(BigDecimal.valueOf(afterTotal * overdueRepayment * diffNum), 2)
									.doubleValue();// 劣后滞纳金
							// feeOut=
							// Arith.round(BigDecimal.valueOf(list.get(0).getCompanyPreFee()*
							// cost.getOverdueRepayment()* diffNum),
							// 2).doubleValue(); //计算平台服务费
							System.out.println("滞纳天数=" + diffNum + "优先滞纳金=" + priOut 
									+ "，夹层滞纳金=" + midOut + "，劣后滞纳金=" + aftOut);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (repaymentInfo.getLoansign().getType() == 2) {
			Integer periods = repaymentInfo.getPeriods();
			String sql = "SELECT * from repaymentrecord  r WHERE r.loanSign_id=? and periods="
					+ periods + " ";
			List<Repaymentrecord> list = dao.findBySql(sql,
					Repaymentrecord.class, repaymentInfo.getLoansign().getId());
			try {
				if (list != null) {
					if (list.get(0).getRepayState() == 1) {
						diffNum = DateUtils.differenceDateSimple(list.get(0)
								.getPreRepayDate(), DateUtils
								.formatSimple(new Date()));
						if (diffNum > 0) {
							System.out.println("出现滞纳");
							Repaymentrecord repayVo = list.get(0);
							/** 需要计算滞纳金的优先金额 */
							Double preTotal = Arith.add(repayVo.getMoney(), repayVo.getPreRepayMoney());
							priOut = Arith.round(
									BigDecimal.valueOf(preTotal * overdueRepayment * diffNum), 2)
									.doubleValue();// 优先滞纳金
							/** 需要计算滞纳金的夹层金额 */
							Double midTotal = Arith.add(
									repayVo.getMiddleMoney(), repayVo.getMiddlePreRepayMoney());
							midOut = Arith.round(
									BigDecimal.valueOf(midTotal * overdueRepayment * diffNum), 2)
									.doubleValue();// 夹层滞纳金
							/** 需要计算滞纳金的劣后金额 */
							Double afterTotal = Arith.add(
									repayVo.getAfterMoney(), repayVo.getAfterPreRepayMoney());
							aftOut = Arith.round(
									BigDecimal.valueOf(afterTotal * overdueRepayment * diffNum), 2)
									.doubleValue();// 劣后滞纳金
							// feeOut=
							// Arith.round(BigDecimal.valueOf(list.get(0).getCompanyPreFee()*
							// cost.getOverdueRepayment()* diffNum),
							// 2).doubleValue(); //计算平台服务费
							System.out.println("滞纳天数=" + diffNum + "优先滞纳金="
									+ priOut + "，夹层滞纳金=" + midOut + "，劣后滞纳金="
									+ aftOut);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		map.put("priOut", String.valueOf(priOut));
		map.put("midOut", String.valueOf(midOut));
		map.put("aftOut", String.valueOf(aftOut));
		// map.put("feeOut", String.valueOf(feeOut));
		map.put("total", String.valueOf(priOut + midOut + aftOut));
		map.put("diffNum", String.valueOf(diffNum));
		return map;
	}

	/**
	 * 获取天标滞纳金
	 * 
	 * @param repaymentInfo
	 * @return
	 */
	public double getDayPeridosMoney(Repaymentrecord repaymentInfo) {
		double upperidosMoney = 0.00;
		String sql = "SELECT * from repaymentrecord  r WHERE r.loanSign_id=? ";
		List<Repaymentrecord> list = dao.findBySql(sql, Repaymentrecord.class,
				repaymentInfo.getLoansign().getId());
		try {
			if (list != null) {
				if (list.get(0).getRepayState() == 1) {
					int diffNum = DateUtils.differenceDateSimple(list.get(0)
							.getPreRepayDate(), list.get(0).getRepayTime());
					if (diffNum > 0) {
						System.out.println("出现滞纳");
						upperidosMoney = repaymentInfo.getLoansign()
								.getIssueLoan() * 0.24 / 360 * diffNum;
					}
				}
			}
		} catch (Exception e) {

		}
		return upperidosMoney;
	}

	/**
	 * 保存或则更新还款明细信息
	 * 
	 * @param rcp
	 */
	public void saveLiquidation(Liquidation ld) {
		dao.saveOrUpdate(ld);
	}

	/***
	 * 根据Id和类型得到count
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public Integer getRecordparticularsCount(Long id, Integer type) {
		String sql = "select count(1) from repaymentrecordparticulars rcp where rcp.repaymentrecordId="
				+ id + " and rcp.loanType=" + type + "";
		return loanSignQuery.queryCount(sql);
	}

	/**
	 * 得到当期 还款详情
	 * 
	 * @param repaymentrecord
	 * @return
	 */
	public List<Repaymentrecordparticulars> getRepaymentrecordparticulars(
			Repaymentrecord repaymentrecord) {
		String sql = "SELECT * from repaymentrecordparticulars rcp where rcp.repaymentrecordId=?  and rcp.repState=-1 order by userid asc";
		return dao.findBySql(sql, Repaymentrecordparticulars.class,
				repaymentrecord.getId());
	}

	/***
	 * 更新userfundinfo表
	 */
	public void updateOperationMoney() {
		String sql = "update userfundinfo set operation_money=cashBalance";
		dao.executeSql(sql);
	}

	public Repaymentrecordparticulars getParticulars(Payuser payuserinfo) {
		String sql = "select * from repaymentrecordparticulars rcp where rcp.id=? and rcp.repState=0 ";
		List list = dao.findBySql(sql, Repaymentrecordparticulars.class,
				payuserinfo.getrId());
		return (Repaymentrecordparticulars) list.get(0);
	}

	public double getRealInterest(Repaymentrecord repaymentrecord, int type) {
		String sql = "SELECT * from repaymentrecordparticulars rcp where rcp.repaymentrecordId=? and rcp.loanType=? LIMIT 1";
		List list = dao.findBySql(sql, Repaymentrecordparticulars.class,
				repaymentrecord.getId(), type);
		Repaymentrecordparticulars rpc = null;
		if (list.size() <= 0) {
			return 0.0;
		}
		rpc = (Repaymentrecordparticulars) list.get(0);
		Loansign loansign = repaymentrecord.getLoansign();
		Loanrecord loanrecord = rpc.getLoanrecord();
		if (type == 1) {
			return (rpc.getPreRepayMoney() * loansign.getPriority())
					/ loanrecord.getTenderMoney();
		}
		if (type == 2) {
			if (loansign.getMiddle() == 0) {
				return 0.0;
			} else {
				return (rpc.getMiddlePreRepayMoney() * loansign.getMiddle())
						/ loanrecord.getTenderMoney();
			}
		}
		if (type == 3) {
			return (rpc.getAfterPreRepayMoney() * loansign.getAfter())
					/ loanrecord.getTenderMoney();
		}
		return 0.0;
	}

	/**
	 * 更新还款明细状态
	 * 
	 * @param info
	 */
	public void updateRepaymentrecordParticulars(Repaymentrecordparticulars info) {
		System.out.println(info.getId());
		dao.update(info);
	}

	/**
	 * 根据标的状态查询清盘数据
	 * 
	 * @param lonid
	 * @return
	 */
	public List<Liquidation> getLiquidationbyLoanid(String lonid) {
		String sql = "SELECT * from liquidation  where liquidation.loanId=? and liquidationState!=1";
		return dao.findBySql(sql, Liquidation.class, lonid);
	}

	/**
	 * 是否有还款遗漏
	 * 
	 * @param lonid
	 * @return
	 */
	public double getNotLiquidationCountByLoanid(String lonid) {
		String sql = "SELECT count(*) from liquidation  where liquidation.loanId=? and liquidationState!=1 ";
		return dao.queryNumberSql(sql, lonid);
	}

	/**
	 * 根据标 ID 更新 标的 标的状态 为清盘 -1 & 更新 还款状态 为清盘 -1
	 * 
	 * @param lonid
	 */
	public void updateLoansignAndRepaymentrecordByLoanid(String lonid) {
		// 更新标的状态
		String sql = "UPDATE repaymentrecord  r SET  r.repayState=-1    where  r.loanSign_id=? and r.repayState=1";
		// 更新还款计划状态
		String sql2 = "UPDATE loansign l set l.status =-1 WHERE l.id=?";
		dao.executeSql(sql, lonid);
		dao.executeSql(sql2, lonid);
	}

	/**
	 * 根据标ID 判断当前 标是否有生成 清盘计划
	 * 
	 * @param lonid
	 * @return
	 */
	public double getLoanginLiquidationPlanCount(String lonid) {
		String sql = "SELECT count(*) from liquidation  where liquidation.loanId=? ";
		return dao.queryNumberSql(sql, lonid);
	}

	/**
	 * 根据标的信息或者还没有还款的计划数据
	 * 
	 * @param loansign
	 * @return
	 */
	public List<Repaymentrecord> getNotRepaymentRecords(Loansign loansign) {
		String sql = "SELECT * from repaymentrecord WHERE repaymentrecord.repayState=1 and repaymentrecord.loanSign_id=?";
		return dao.findBySql(sql, Repaymentrecord.class, loansign.getId());
	}
	
	/***
	 * 修改repaymentrecordparticulars表状态
	 * @param repaymentrecordId
	 */
	public void uptRepaymentrecordparticulars(Long repaymentrecordId) {
		String sql = "update repaymentrecordparticulars set  repState=1 where repaymentrecordId=?";
		dao.executeSql(sql, repaymentrecordId);
	}
	
	/***
	 * 更新还款信息
	 * @param repayMentRecordId
	 */
	public void updateRepaymen(Long repayMentRecordId){
		String sql="update repaymentrecord  set realMoney=0,middleRealMoney=0,afterRealMoney=0,companyRealFee=0,repayState=1 where id=?";
		dao.executeSql(sql, repayMentRecordId);
		String sqlRe = "update repaymentrecordparticulars set  repState=-1,realMoney=0,middleRealMoney=0,afterRealMoney=0 where repaymentrecordId=?";
		dao.executeSql(sqlRe, repayMentRecordId);
	}

}
