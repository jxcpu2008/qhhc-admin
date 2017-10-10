package com.hc9.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.redis.Arith;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.CalculateLoan;
import com.hc9.service.LoanSignFund;
import com.hc9.service.LoanSignQuery;

/**
 * 
 * 有关标的一些算法的实现
 * 
 * @author Administrator
 * 
 */
@Service
public class LoanSignFundImpl implements LoanSignFund {

	/** loanSignQuery */
	@Autowired
	private LoanSignQuery loanSignQuery;

	/** dao */
	@Resource
	private HibernateSupport dao;

	/**
	 * * 针对标提前还款的利息算法(只针对普通标的‘到期一次性还本息’、天标) 普通标(提前还款的利息=借款金额*年利率/365*借款天数)
	 * 天标(提前还款的利息=借款金额*天利率*借款天数)
	 * 
	 * @param money
	 *            借款金额
	 * @param rate
	 *            普通标是年利率 天标是天利率
	 * @param date
	 *            借款天数
	 * @param type
	 *            1.普通标2. 天标 =loansign.loanType
	 * @return 利息
	 */
	public BigDecimal advanceInterest(BigDecimal money, double rate,
			Integer date, Integer type) {
		BigDecimal advanceInterest = new BigDecimal(0.00);
		if (type == 1) {
			advanceInterest = money.multiply(Arith.div(rate, 365)).multiply(
					new BigDecimal(date));
		} else if (type == 2) {
			advanceInterest = money.multiply(new BigDecimal(rate)).multiply(
					new BigDecimal(date));
		}
		return advanceInterest;

	}

	/**
	 * 标的逾期利息 (逾期利息=借款金额*逾期的天数*平台定义的逾期利率)
	 * 
	 * @param date
	 *            逾期天数
	 * @param money
	 *            逾期的金额
	 * @return 利息
	 */
	public BigDecimal overdueInterest(BigDecimal money, Integer date) {
		return money.multiply(new BigDecimal(date)).multiply(
				new BigDecimal(Constant.OVERDUE_INTEREST));
	}

	/**
	 * 
	 * 向流水账表中写入数据和修改当前用户余额 (如果插入的金额是支出money就需要传入一个负的金额,收入传入的就为正的金额)
	 * 
	 * @param money
	 *            金额
	 * @param typeId
	 *            操作类型编号
	 * @param date
	 *            操作时间
	 * @param userId
	 *            用户编号
	 * @param explen
	 *            操作说明
	 * @param withdrawId
	 *            withdrawId
	 * @param loansignId
	 *            loansignId
	 * @return 是否
	 */
	public boolean updateMoney(BigDecimal money, Long typeId, String date,
			Long userId, String explen, Long withdrawId, Long loansignId) {
		if (money.doubleValue() > 0) {
			return dao.callProcedureVoid("PROCEDURE_MONEY_UPDATE", money,
					typeId, date, userId, explen, withdrawId, loansignId);
		} else {
			return dao.callProcedureVoid("PROCEDURE_MONEY_UPDATE",
					money.multiply(new BigDecimal(-1)), typeId, date, userId,
					explen, withdrawId, loansignId);
		}
	}

	/**
	 * 
	 * 向平台流水账中插入数据和修改当前平台余额 (如果插入的金额是支出money就需要传入一个负的金额,收入传入的就为正的金额)
	 * 
	 * @param money
	 *            金额
	 * @param typeId
	 *            操作类型编号
	 * @param date
	 *            操作时间
	 * @param explen
	 *            操作说明
	 * @param withdrawId
	 *            withdrawId
	 * @return 是否
	 */
	public boolean updatePlatformMoney(BigDecimal money, Long typeId,
			String date, String explen, Long withdrawId) {
		if (money.doubleValue() > 0) {
			return dao.callProcedureVoid("PROCEDURE_MONEY_UPDATEADMINUSER",
					money, typeId, date, explen, withdrawId);
		} else {
			return dao.callProcedureVoid("PROCEDURE_MONEY_UPDATEADMINUSER",
					money.multiply(new BigDecimal(-1)), typeId, date, explen,
					withdrawId);
		}
	}

	/**
	 * 
	 * 有关标的管理费的算法(针对借款人的)
	 * (2个月内管理费 = 借款本金*该比例)
	 * (2个月以上管理费 =借款本金*2个月内的费用比例+借款本金*（借款月数-2）*该比例)
	 * (天标管理费 = 借款金额*该比例*借款天数)
	 * @param money
	 *            借款金额
	 * @param loan
	 *            标的基本信息
	 * @param isPrivilege
	 *            是否为特权会员 (1是特权会员 0不是特权会员)
	 * @param type
	 *            标类型
	 * @param month
	 *            借款期限
	 * @return 管理费
	 */
	public BigDecimal managementFee(BigDecimal money, Loansign loan,
			int isPrivilege) {
		BigDecimal managementFee = new BigDecimal(0.00);
		// 判断该标的类型
		if (loan.getType() == 1) {
			
	/*		managementFee = money.multiply(new BigDecimal(loan
					.getPmfeeratio()));*/
		
		} else if(loan.getType() == 2) {
	//		managementFee = money.multiply(new BigDecimal(loan
	//				.getPmfeeratio()).multiply(new BigDecimal(loan.getUseDay())));
		}
		// 判断该会员是否为特权会员
		if (isPrivilege == 1) {
	//		managementFee = money.multiply(new BigDecimal(loan
	//				.getVipMfeeratio()));
	//		if (managementFee.doubleValue() > loan.getVipPmfeetop()) {
	//			managementFee = new BigDecimal(loan.getVipPmfeetop());
	//		}
		}
		return managementFee;
	}
	/**
	 * 
	 * 有关标的管理费的算法(针对投资人的)
	 * 
	 * @param money
	 *            利息
	 * @param loan
	 *            标的基本信息
	 * @param isPrivilege
	 *            是否为特权会员 (1是特权会员 0不是特权会员)
	 * @param (管理费=该标的所得利息*管理费用比例)
	 * @return 管理费
	 */
	public BigDecimal managementCost(BigDecimal money, Loansign loan,
			int isPrivilege) {
		// 管理费=该标的所得利息*管理费用比例
		BigDecimal managementFee =null;
//		if (isPrivilege == Constant.STATUES_ONE) { 
			//VIP管理费
	//		managementFee=  money
//					.multiply(new BigDecimal(loan.getVipMfeeratio()));
//			if(managementFee.doubleValue()>loan.getVipMfeetop()){//是否超限
//				managementFee=new BigDecimal(loan.getVipMfeetop());
//			}
//		} else {
//			managementFee = money.multiply(new BigDecimal(loan.getMfeeratio()));
//		}
	//	return managementFee;
		return managementFee;
			
	}
	/**
	 * 有关标的每一期的利息的算法
	 * 
	 * @param money
	 *            借款标的金额
	 * @param rate
	 *            标的利率或年利率
	 * @param periods
	 *            借款期数(天数或月数)
	 * @param type
	 *            标的类型()1.普通标2 天标 3秒标 4 流转标 普通标 (利息=借款金额*年利率/12)[普通标中的等额本息除外] 天标
	 *            (利息=借款金额*天利率*借款天数) 秒标 (利息=借款金额*利率) 流转标
	 *            (利息=借款金额*年利率/365*每阶段的天数)
	 * 
	 * @return 利息
	 */
	public BigDecimal instalmentInterest(BigDecimal money, Double rate,
			int periods, int type) {

		BigDecimal interest = new BigDecimal(0.00);
		if (type == 1) {//按月算
			// 利息=借款金额*年利率/12
			interest = money.multiply(Arith.div(rate, 12));
		} else if (type == 2) {//按季度
			// 利息=借款金额*年利率/12*3
			double suminter = money.multiply(new BigDecimal(rate)).doubleValue();
			interest = Arith.div(suminter,12).multiply(new BigDecimal(3));
			// 利息=借款金额*年利率*（1/(期数/3))		
//			int vet=periods/3;	
//			interest = money.multiply(new BigDecimal(rate)).multiply(Arith.div(1, vet));

			
			// 利息=借款金额*天利率*借款天数
			/*interest = money.multiply(new BigDecimal(rate)).multiply(
					new BigDecimal(periods));*/
		} else if (type == 3) {
			// 利息 = 借款金额*利率
			interest = money.multiply(new BigDecimal(rate));
		} else if (type == 4) {
			// 利息=借款金额*年利率/360*每阶段的天数
			interest = money.multiply(Arith.div(rate, 360)).multiply(
					new BigDecimal(periods));
		}

		return interest;
	}

	/**
	 * 计算等额本息每期需要偿还的利息和本金
	 * 
	 * @param issueLoan
	 *            该借款标的借款金额
	 * @param rate
	 *            利率
	 * @param periods
	 *            借款期数
	 * @return 返回该借款标每月偿还的本金和利息集合
	 */
	public List<CalculateLoan> loanCalculate(BigDecimal issueLoan, double rate,
			int periods) {
		BigDecimal sumMonth = new BigDecimal(12);

		// 年利率
		BigDecimal yearrate = new BigDecimal(rate);

		// 月利率
//		BigDecimal monthrate = yearrate.divide(sumMonth, Arith.DEF_DIV_SCALE,RoundingMode.HALF_EVEN);
		BigDecimal monthrate = yearrate;

		List<CalculateLoan> numlist = new ArrayList<CalculateLoan>();

		// 每一期还款金额
		BigDecimal huankuan = total(issueLoan, rate, periods);

		for (int i = 1; i <= periods; i++) {

			CalculateLoan calculoan = new CalculateLoan();

			// 计算利息
			calculoan.setLixi(Arith.round(issueLoan.multiply(monthrate), 2));

			// 计算本期所还本金
			calculoan.setBenjin(Arith.round(Arith.sub(huankuan, calculoan.getLixi()), 2));

			// 还款期数
			calculoan.setCount(i);

			// 计算未还本金
			calculoan.setNotReturn(Arith.round(Arith.sub(issueLoan, calculoan.getBenjin()), 2));

			// 将本期未还本金，作为下一期的本金
			issueLoan = calculoan.getNotReturn();

			// 将本期还款信息放入集合中
			numlist.add(calculoan);
		}
		if (numlist.get(numlist.size() - 1).getNotReturn().intValue() < 0) {
			numlist.get(numlist.size() - 1).setNotReturn(new BigDecimal(0.00));
		}
		return numlist;
	}

	/**
	 * 等额本息每一期需要还的金额
	 * 
	 * @param issueLoan
	 *            标的借款金额
	 * @param rate
	 *            标的利率
	 * @param periods
	 *            借款期数
	 * @return 金额
	 */
	public BigDecimal total(BigDecimal issueLoan, double rate, int periods) {
		// "[贷款本金×月利率×（1+月利率）^还款月数]÷[（1+月利率）^还款月数—1]"
		// 1
		BigDecimal one = new BigDecimal("1");

		// 年利率
		BigDecimal yearrate = new BigDecimal(Double.toString(rate));

		BigDecimal sumMonth = new BigDecimal(12);

		// 月利率
		BigDecimal monthrate = yearrate;
//		BigDecimal monthrate = yearrate.divide(sumMonth, Arith.DEF_DIV_SCALE,RoundingMode.HALF_EVEN);

		// 贷款本金*月利率
		BigDecimal benmonth = issueLoan.multiply(monthrate);

		// 月利率+1
		BigDecimal bigrate = one.add(monthrate);

		// 被除数
		BigDecimal beichushu = benmonth.multiply(Arith
				.muchmul(bigrate, periods));

		// 除数
		BigDecimal chushu = Arith.muchmul(bigrate, periods);
		chushu = chushu.subtract(one);

		// 计算结果
		BigDecimal totalRate = new BigDecimal(beichushu.divide(chushu,
				Arith.DEF_DIV_SCALE, RoundingMode.HALF_EVEN).doubleValue());
		return totalRate;
	}

	/**
	 * 计算天标和普通标（提前还款）的实际使用天数=还款时间-放款时间
	 * 
	 * @param creditTime
	 *            放款时间
	 * @param repayTime
	 *            还款时间
	 * @author frank 2013-12-27
	 * @return 天数
	 */
	public int reallyUseDay(String creditTime, String repayTime) {
		try {
			// 实际使用天数=还款时间-放款时间
			// 还款时间保留成Constant.DEFAULT_DATE_FORMAT
			return DateUtils.differenceDateSimple(DateUtils.format(
					Constant.DEFAULT_TIME_FORMAT, creditTime,
					Constant.DEFAULT_DATE_FORMAT), repayTime);
		} catch (ParseException e) {
			return 0;
		}

	}

	/**
	 * 计算流转标该阶段的使用天数 天数<30
	 * 
	 * @param tenderTime
	 *            认购时间
	 * @param preRepayDate
	 *            预计还款时间
	 * @return 天数
	 */
	public int reallyDay(String tenderTime, String preRepayDate) {
		try {
			String endTime = DateUtils.format(Constant.DEFAULT_DATE_FORMAT,
					preRepayDate, Constant.DEFAULT_TIME_FORMAT);
			String biginTime = DateUtils.add(Constant.DEFAULT_TIME_FORMAT,
					preRepayDate, Calendar.DATE, -30);
			if (DateUtils.isAfter(Constant.DEFAULT_TIME_FORMAT, tenderTime,
					Constant.DEFAULT_TIME_FORMAT, biginTime)) {// 认购时间<开始时间
				return 30;
			} else if (DateUtils.isDuring(Constant.DEFAULT_TIME_FORMAT,
					biginTime, endTime, tenderTime)) {// 认购时间在这个阶段的时间段里买的
				return DateUtils.differenceDateSimple(tenderTime, endTime);
			}
		} catch (ParseException e) {
			return 0;
		}
		return 0;
	}
}
