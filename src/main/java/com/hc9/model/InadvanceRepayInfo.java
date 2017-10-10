package com.hc9.model;

import java.io.Serializable;

/**
 * 提前还款记录Value Object
 * @author Jerry Wong
 *
 */
public class InadvanceRepayInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	// 标的主键id
	private long loanId;
	
	// 标的类型
	private int loanType;
	
	// 融资人主键id
	private long userId;
	
	// 融资人真实姓名
	private String userName;
	
	// 融资人电话号码
	private String phone;
	
	// 标的名称
	private String loanName;
	
	// 借款期限
	private int loanPeriod;
	
	private String loanPeriodDisplay;
	
	// 借款金额
	private double loanAmount;
	
	// 期望利息，即应收利息
	private double expectInterest;
	
	// 还款金额
	private double repayAmount;
	
	// 当期还款记录主键id
	private long repayRecordId;
	
	// 还款期限，即实际借款期限
	private int repayPeriod;
	
	private String repayPeriodDisplay;
	
	// 额外利息
	private double penaltyInterest;
	
	// 账户余额
	private double balance;
	
	// 申请时间
	private String applyTime;

	public long getLoanId() {
		return loanId;
	}

	public void setLoanId(long loanId) {
		this.loanId = loanId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLoanName() {
		return loanName;
	}

	public void setLoanName(String loanName) {
		this.loanName = loanName;
	}

	public int getLoanPeriod() {
		return loanPeriod;
	}

	public void setLoanPeriod(int loanPeriod) {
		this.loanPeriod = loanPeriod;
	}

	public double getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(double loanAmount) {
		this.loanAmount = loanAmount;
	}

	public double getExpectInterest() {
		return expectInterest;
	}

	public void setExpectInterest(double expectInterest) {
		this.expectInterest = expectInterest;
	}

	public double getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(double repayAmount) {
		this.repayAmount = repayAmount;
	}

	public int getRepayPeriod() {
		return repayPeriod;
	}

	public void setRepayPeriod(int repayPeriod) {
		this.repayPeriod = repayPeriod;
	}

	public double getPenaltyInterest() {
		return penaltyInterest;
	}

	public void setPenaltyInterest(double penaltyInterest) {
		this.penaltyInterest = penaltyInterest;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public int getLoanType() {
		return loanType;
	}

	public void setLoanType(int loanType) {
		this.loanType = loanType;
	}

	public String getLoanPeriodDisplay() {
		return loanPeriodDisplay;
	}

	public void setLoanPeriodDisplay(String loanPeriodDisplay) {
		this.loanPeriodDisplay = loanPeriodDisplay;
	}

	public String getRepayPeriodDisplay() {
		return repayPeriodDisplay;
	}

	public void setRepayPeriodDisplay(String repayPeriodDisplay) {
		this.repayPeriodDisplay = repayPeriodDisplay;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getRepayRecordId() {
		return repayRecordId;
	}

	public void setRepayRecordId(long repayRecordId) {
		this.repayRecordId = repayRecordId;
	}
}