package com.hc9.model;

/**
 * 各渠道注册人数VO
 * @author Administrator
 *
 */
public class ChannelRegNum {
	
	// 各渠道注册人数
	private Long channelRegNum;
	
	// 渠道
	private String channelName;

	public Long getChannelRegNum() {
		return channelRegNum;
	}

	public void setChannelRegNum(Long channelRegNum) {
		this.channelRegNum = channelRegNum;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
}