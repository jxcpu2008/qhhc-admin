package com.hc9.model;

/**
 * 各渠道投资VO
 * @author Administrator
 *
 */
public class ChannelInvNum {
	
	// 各渠道认购人数
	private Long channelInvNum;
	
	// 各渠道认购金额
	private Double InvestMoney;
	
	// 渠道
	private String channelName;
	
	// 百分比
	private double percentage;

	public Long getChannelInvNum() {
		return channelInvNum;
	}

	public void setChannelInvNum(Long channelInvNum) {
		this.channelInvNum = channelInvNum;
	}

	public Double getInvestMoney() {
		return InvestMoney;
	}

	public void setInvestMoney(Double investMoney) {
		InvestMoney = investMoney;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
}