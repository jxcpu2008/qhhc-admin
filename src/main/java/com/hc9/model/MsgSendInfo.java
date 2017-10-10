package com.hc9.model;

/** 消息短信发送实体类 */
public class MsgSendInfo {
	/** 待发送消息的主键id */
	private Long id;
	
	/** 接收方邮件或短信工具对应的号码 */
	private String smsEmailNo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSmsEmailNo() {
		return smsEmailNo;
	}

	public void setSmsEmailNo(String smsEmailNo) {
		this.smsEmailNo = smsEmailNo;
	}
}