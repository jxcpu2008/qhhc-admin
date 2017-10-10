package com.hc9.model;

/** 短信邮件发送计划相关实体类 */
public class MsgSendPlanInfo {
	/** 主键id */
	private Long id;
	
	/** 消息标题 */
	private String msgTitle;
	
	/** 模板内容； */
	private String templateContent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMsgTitle() {
		return msgTitle;
	}

	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}

}