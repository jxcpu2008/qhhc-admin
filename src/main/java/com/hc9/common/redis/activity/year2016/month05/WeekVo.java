package com.hc9.common.redis.activity.year2016.month05;

/** 周周有惊喜相关vo类 */
public class WeekVo {
	/** 投资人用户id */
	private long userId;
	
	/** 手机号 */
	private String phone;
	
	/** 累计周投资金额 */
	private long weekMoney;
	
	/** 累计周年化投资金额 */
	private double weekYearMoney;
	
	/** 待收奖励 */
	private String toInCome;
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public long getWeekMoney() {
		return weekMoney;
	}

	public void setWeekMoney(long weekMoney) {
		this.weekMoney = weekMoney;
	}

	public double getWeekYearMoney() {
		return weekYearMoney;
	}

	public void setWeekYearMoney(double weekYearMoney) {
		this.weekYearMoney = weekYearMoney;
	}

	public String getToInCome() {
		return toInCome;
	}

	public void setToInCome(String toInCome) {
		this.toInCome = toInCome;
	}
}