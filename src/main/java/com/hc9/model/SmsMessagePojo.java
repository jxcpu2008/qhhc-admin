package com.hc9.model;

/** 消息、邮箱相关实体对象 */
public class SmsMessagePojo {
	/** 项目主键id */
	private Long loansignId;
	
	/** 还款记录表对应的id */
	private Long repaymentrecordId;
	
	/** 项目名称 */
	private String loansignName;
	
	/** 项目借款总额 */
	private Double issueLoan;
	
	/** 回购期限 */
	private Integer remonth;
	
	/** 预计还款日期 */
	private String preRepayDate;
	
	/** 本期还款总额 */
	private Double repayMoney;
	
	/** 借款人id */
	private Long loanUserId;

	public Long getLoansignId() {
		return loansignId;
	}

	public void setLoansignId(Long loansignId) {
		this.loansignId = loansignId;
	}

	public Long getRepaymentrecordId() {
		return repaymentrecordId;
	}

	public void setRepaymentrecordId(Long repaymentrecordId) {
		this.repaymentrecordId = repaymentrecordId;
	}

	public String getLoansignName() {
		return loansignName;
	}

	public void setLoansignName(String loansignName) {
		this.loansignName = loansignName;
	}

	public Double getIssueLoan() {
		return issueLoan;
	}

	public void setIssueLoan(Double issueLoan) {
		this.issueLoan = issueLoan;
	}

	public Integer getRemonth() {
		return remonth;
	}

	public void setRemonth(Integer remonth) {
		this.remonth = remonth;
	}

	public String getPreRepayDate() {
		return preRepayDate;
	}

	public void setPreRepayDate(String preRepayDate) {
		this.preRepayDate = preRepayDate;
	}

	public Double getRepayMoney() {
		return repayMoney;
	}

	public void setRepayMoney(Double repayMoney) {
		this.repayMoney = repayMoney;
	}

	public Long getLoanUserId() {
		return loanUserId;
	}

	public void setLoanUserId(Long loanUserId) {
		this.loanUserId = loanUserId;
	}
}