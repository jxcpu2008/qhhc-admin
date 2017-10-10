package com.hc9.model;

/** 抽奖排行榜相关实体对象 */
public class LotteryRank {
	/** 投资总金额 */
	private Double investTotalMoney;
	
	/** 投资人登录账号 */
	private String userName;

	/** 手机号码 */
	private String mobilePhone;
	
	/** 奖品类型 */
	private int prizeType;
	
	/** 红包金额 */
	private double redEnvelopeMoney;
	
	/** 奖品名称 */
	private String prizeName;
	
	public Double getInvestTotalMoney() {
		return investTotalMoney;
	}

	public void setInvestTotalMoney(Double investTotalMoney) {
		this.investTotalMoney = investTotalMoney;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public int getPrizeType() {
		return prizeType;
	}

	public void setPrizeType(int prizeType) {
		this.prizeType = prizeType;
	}

	public double getRedEnvelopeMoney() {
		return redEnvelopeMoney;
	}

	public void setRedEnvelopeMoney(double redEnvelopeMoney) {
		this.redEnvelopeMoney = redEnvelopeMoney;
	}

	public String getPrizeName() {
		return prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}
}
