package com.hc9.model;

/**
 * 存放关于投资人的每个借款标的每期还款信息
 * 
 * @author RanQiBing 2014-04-01
 * 
 */
public class ExpensesInfo {

	/**
	 * 标编号
	 */
	private Long loanid;
	/**
	 * 用户编号
	 */
	private Long userId;
	/**
	 * 本金
	 */
	private Double money;
	/**
	 * 利息
	 */
	private Double interest;
	/**
	 * 违约金
	 */
	private Double penalty;
	/**
	 * 管理费
	 */
	private Double management;
	/**
	 * ips账号
	 */
	private String ipsNumber;
	/**
	 * 还款状态 2按时还款 4逾期已还款 5提前还款
	 */
	private Integer state;

	/** 投标时登记的订单号 */
	private String pMerBillNo;

	private Integer vip;

	/**
	 * 投资类型
	 */
	private Integer loanType;

	/**
	 * 投资的金额
	 */
	private Double loanMoney;
	
	
	


	/**
	 * 标编号
	 * 
	 * @return loanid
	 */
	public Long getLoanid() {
		return loanid;
	}

	/**
	 * 是否发布有净值标
	 */
	public Integer isLoanState;

	/**
	 * @param loanid
	 *            标编号
	 */
	public void setLoanid(Long loanid) {
		this.loanid = loanid;
	}

	/**
	 * 用户编号
	 * 
	 * @return userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            用户编号
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * 本金
	 * 
	 * @return money
	 */
	public Double getMoney() {
		return money;
	}

	/**
	 * @param money
	 *            本金
	 */
	public void setMoney(Double money) {
		this.money = money;
	}

	/**
	 * 利息
	 * 
	 * @return interest
	 */
	public Double getInterest() {
		return interest;
	}

	/**
	 * @param interest
	 *            利息
	 */
	public void setInterest(Double interest) {
		this.interest = interest;
	}

	/**
	 * 违约金
	 * 
	 * @return penalty
	 */
	public Double getPenalty() {
		return penalty;
	}

	/**
	 * @param penalty
	 *            违约金
	 */
	public void setPenalty(Double penalty) {
		this.penalty = penalty;
	}

	/**
	 * 管理费
	 * 
	 * @return management
	 */
	public Double getManagement() {
		return management;
	}

	/**
	 * @param management
	 *            管理费
	 */
	public void setManagement(Double management) {
		this.management = management;
	}

	/**
	 * ips账号
	 * 
	 * @return ipsNumber
	 */
	public String getIpsNumber() {
		return ipsNumber;
	}

	/**
	 * @param ipsNumber
	 *            ips账号
	 */
	public void setIpsNumber(String ipsNumber) {
		this.ipsNumber = ipsNumber;
	}

	/**
	 * 是否发布有净值标
	 * 
	 * @return isLoanState
	 */
	public Integer getIsLoanState() {
		return isLoanState;
	}

	/**
	 * @param isLoanState
	 *            是否发布有净值标
	 */
	public void setIsLoanState(Integer isLoanState) {
		this.isLoanState = isLoanState;
	}

	/**
	 * 还款状态
	 * 
	 * @return state
	 */
	public Integer getState() {
		return state;
	}

	/**
	 * @param state
	 *            还款状态
	 */
	public void setState(Integer state) {
		this.state = state;
	}

	/** 登记债权人时提交的订单号 */
	public String getpMerBillNo() {
		return pMerBillNo;
	}

	/** 登记债权人时提交的订单号 */
	public void setpMerBillNo(String pMerBillNo) {
		this.pMerBillNo = pMerBillNo;
	}

	/** vip */
	public Integer getVip() {
		return vip;
	}

	/** vip */
	public void setVip(Integer vip) {
		this.vip = vip;
	}

	public Integer getLoanType() {
		return loanType;
	}

	public void setLoanType(Integer loanType) {
		this.loanType = loanType;
	}

	public Double getLoanMoney() {
		return loanMoney;
	}

	public void setLoanMoney(Double loanMoney) {
		this.loanMoney = loanMoney;
	}



}
