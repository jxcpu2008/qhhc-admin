package com.hc9.model;

/** 登顶活动相关vo对象 */
public class ClimbVo {
	/** 投资人用户id */
	private long userId;
	
	/** 累计投资金额 */
	private double investMoney;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public double getInvestMoney() {
		return investMoney;
	}

	public void setInvestMoney(double investMoney) {
		this.investMoney = investMoney;
	}
}