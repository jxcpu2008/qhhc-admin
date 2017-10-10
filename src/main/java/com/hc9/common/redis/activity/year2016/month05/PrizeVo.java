package com.hc9.common.redis.activity.year2016.month05;

/** 奖品辅助类 */
public class PrizeVo {
	/** 用户id */
	private long userId;
	
	/** 手机号码 */
	private String phone;
	
	/** 奖品数量 */
	private int prizeNum;

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

	public int getPrizeNum() {
		return prizeNum;
	}

	public void setPrizeNum(int prizeNum) {
		this.prizeNum = prizeNum;
	}
	
}