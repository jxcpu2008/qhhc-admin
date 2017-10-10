package com.hc9.model;

import java.util.List;

import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Userbasicsinfo;

/** 投资辅助对象 */
public class InvestVo {
	/** 投资人id */
	private long userId;
	
	/** 标id */
	private Long loanId;
	
	/** 优先投资金额 */
	private Double priorityMoney;
	
	/** 夹层投资金额 */
	private Double middleMoney;
	
	/** 劣后投资金额 */
	private Double afterMoney;
	
	/** 项目记录 */
	private Loansign loan;
	
	/** 投资记录 */
	private List<InvestLoanRecordVo> loanRecordList;
	
	/** 费用信息 */
	private Costratio costratio;
	
	/** 投资用户信息 */
	private Userbasicsinfo user;
	
	/** 1、优先；2、夹层；3、劣后； */
	private int investType;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public Double getPriorityMoney() {
		return priorityMoney;
	}

	public void setPriorityMoney(Double priorityMoney) {
		this.priorityMoney = priorityMoney;
	}

	public Double getMiddleMoney() {
		return middleMoney;
	}

	public void setMiddleMoney(Double middleMoney) {
		this.middleMoney = middleMoney;
	}

	public Double getAfterMoney() {
		return afterMoney;
	}

	public void setAfterMoney(Double afterMoney) {
		this.afterMoney = afterMoney;
	}

	public Loansign getLoan() {
		return loan;
	}

	public void setLoan(Loansign loan) {
		this.loan = loan;
	}

	public List<InvestLoanRecordVo> getLoanRecordList() {
		return loanRecordList;
	}

	public void setLoanRecordList(List<InvestLoanRecordVo> loanRecordList) {
		this.loanRecordList = loanRecordList;
	}

	public Costratio getCostratio() {
		return costratio;
	}

	public void setCostratio(Costratio costratio) {
		this.costratio = costratio;
	}

	public Userbasicsinfo getUser() {
		return user;
	}

	public void setUser(Userbasicsinfo user) {
		this.user = user;
	}

	public int getInvestType() {
		return investType;
	}

	public void setInvestType(int investType) {
		this.investType = investType;
	}
}