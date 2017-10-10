package com.hc9.model.activity;

/** 标一鸣惊人、一锤定音活动相关辅助对象 */
public class LoanPrizeVo {
	/** 中奖人手机号码 */
	private String phone;
	
	/** 项目名称 */
	private String loanName;
	
	/** 奖励名称 */
	private String prizeName;
	
	/** 奖励金额 */
	private Double rewardMoney;

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

	public String getPrizeName() {
		return prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	public Double getRewardMoney() {
		return rewardMoney;
	}

	public void setRewardMoney(Double rewardMoney) {
		this.rewardMoney = rewardMoney;
	}
}