package com.hc9.common.redis.activity.year2016.month05;

/** 用户信息辅助对象 */
public class GenInvestUserVo {
	/** 投资用户id */
	private long userId;
	
	/** 投资项目id */
	private long loanSignId;
	
	/** 投资记录id */
	private long loanRecordId;
	
	/** 投资金额 */
	private double investMoney;
	
	/** 推荐人 */
	private long genUserId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getLoanSignId() {
		return loanSignId;
	}

	public void setLoanSignId(long loanSignId) {
		this.loanSignId = loanSignId;
	}

	public long getLoanRecordId() {
		return loanRecordId;
	}

	public void setLoanRecordId(long loanRecordId) {
		this.loanRecordId = loanRecordId;
	}

	public double getInvestMoney() {
		return investMoney;
	}

	public void setInvestMoney(double investMoney) {
		this.investMoney = investMoney;
	}

	public long getGenUserId() {
		return genUserId;
	}

	public void setGenUserId(long genUserId) {
		this.genUserId = genUserId;
	}
	
}