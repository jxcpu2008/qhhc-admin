package com.hc9.model;

import java.io.Serializable;

import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Userbasicsinfo;

/**
 * 还款请求Value Object
 * @author Administrator
 * 
 */
public class RepaymentRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	// 标的主键id
	private long loanId;
	
	// 还款记录主键id
	private long repayRecordId;

	// 标的名称
	private String loanName;
	
	// 标的金额
	private double loanAmount;
	
	// 月标，借款期限
	private int loanPeriods;
	
	// 月标，实际期限
	private int realPeriods;
	
	// 还款对应的期限
	private int repayPeriod;
	
	// 还款金额
	private double repayAmount;
	
	// 实际还款金额
	private double realRepayAmount;
	
	// 标的类型
	private int loanType;
	
	// 天标，借款期限
	private int loanDays;
	
	// 天标，实际期限
	private int realDays;
	
	// 还款利息
	private double expectInterest;
	
	// 实际还款利息
	private double realInterest;
	
	// 费用设置，对应DB中的costratio表
	private Costratio defaultFeeConfig;
	
	// 登陆用户
	private Userbasicsinfo loginUser;
	
	// 融资用户
	private Userbasicsinfo loanUser;
	
	// 标的
	private Loansign loan;
	
	// 收取平台服务费状态 1-收取  2-不收取
	private int feeState;
	
	// 还款方式1-按月 2-按季度 3-天标还款
	private int refundWay;
	
	// 逾期天数
	private int overdueDays;
	
	private String repayFlag;
	
	private String repayAction;
	
	// 逾期利息
	private double overdueAmount;
	
	private long userId;

	public long getLoanId() {
		return loanId;
	}

	public void setLoanId(long loanId) {
		this.loanId = loanId;
	}

	public long getRepayRecordId() {
		return repayRecordId;
	}

	public void setRepayRecordId(long repayRecordId) {
		this.repayRecordId = repayRecordId;
	}

	public String getLoanName() {
		return loanName;
	}

	public void setLoanName(String loanName) {
		this.loanName = loanName;
	}

	public int getLoanPeriods() {
		return loanPeriods;
	}

	public void setLoanPeriods(int loanPeriods) {
		this.loanPeriods = loanPeriods;
	}

	public int getRealPeriods() {
		return realPeriods;
	}

	public void setRealPeriods(int realPeriods) {
		this.realPeriods = realPeriods;
	}

	public int getRepayPeriod() {
		return repayPeriod;
	}

	public void setRepayPeriod(int repayPeriod) {
		this.repayPeriod = repayPeriod;
	}

	public double getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(double repayAmount) {
		this.repayAmount = repayAmount;
	}

	public double getRealRepayAmount() {
		return realRepayAmount;
	}

	public void setRealRepayAmount(double realRepayAmount) {
		this.realRepayAmount = realRepayAmount;
	}

	public int getLoanType() {
		return loanType;
	}

	public void setLoanType(int loanType) {
		this.loanType = loanType;
	}

	public int getLoanDays() {
		return loanDays;
	}

	public void setLoanDays(int loanDays) {
		this.loanDays = loanDays;
	}

	public int getRealDays() {
		return realDays;
	}

	public void setRealDays(int realDays) {
		this.realDays = realDays;
	}

	public double getExpectInterest() {
		return expectInterest;
	}

	public void setExpectInterest(double expectInterest) {
		this.expectInterest = expectInterest;
	}

	public double getRealInterest() {
		return realInterest;
	}

	public void setRealInterest(double realInterest) {
		this.realInterest = realInterest;
	}

	public Costratio getDefaultFeeConfig() {
		return defaultFeeConfig;
	}

	public void setDefaultFeeConfig(Costratio defaultFeeConfig) {
		this.defaultFeeConfig = defaultFeeConfig;
	}

	public double getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(double loanAmount) {
		this.loanAmount = loanAmount;
	}

	public Userbasicsinfo getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(Userbasicsinfo loginUser) {
		this.loginUser = loginUser;
	}

	public int getFeeState() {
		return feeState;
	}

	public void setFeeState(int feeState) {
		this.feeState = feeState;
	}

	public int getRefundWay() {
		return refundWay;
	}

	public void setRefundWay(int refundWay) {
		this.refundWay = refundWay;
	}

	public int getOverdueDays() {
		return overdueDays;
	}

	public void setOverdueDays(int overdueDays) {
		this.overdueDays = overdueDays;
	}

	public Loansign getLoan() {
		return loan;
	}

	public void setLoan(Loansign loan) {
		this.loan = loan;
	}

	public String getRepayFlag() {
		return repayFlag;
	}

	public void setRepayFlag(String repayFlag) {
		this.repayFlag = repayFlag;
	}

	public String getRepayAction() {
		return repayAction;
	}

	public void setRepayAction(String repayAction) {
		this.repayAction = repayAction;
	}

	public double getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(double overdueAmount) {
		this.overdueAmount = overdueAmount;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Userbasicsinfo getLoanUser() {
		return loanUser;
	}

	public void setLoanUser(Userbasicsinfo loanUser) {
		this.loanUser = loanUser;
	}
}