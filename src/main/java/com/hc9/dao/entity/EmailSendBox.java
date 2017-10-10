package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/** 邮件发件箱 */
@Entity
@Table(name = "emailsendbox")
public class EmailSendBox {
	/** 主键id，自增 */
	private Long id;
	
	/** 发送计划对应的id(smsEmailSendPlan的id) */
	private Long sendPlanId;
	
	/** 接收人用户id */
	private Long receiverUserId;
	
	/** 接收人email */
	private String receiverEmail;
	
	/** 邮件内容(用于支持个性化字段填充的邮件，方便事后查询) */
	private String emailContent;
	
	/** 发送状态：1、待发送；2、终止发送；3、发送成功；-1、软删除； */
	private Integer sendStatus;
	
	/** 创建时间 */
	private String createTime;
	
	/** 发送成功时间 */
	private String successTime;

	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "sendPlanId", nullable = false)
	public Long getSendPlanId() {
		return sendPlanId;
	}

	public void setSendPlanId(Long sendPlanId) {
		this.sendPlanId = sendPlanId;
	}

	@Column(name = "receiverUserId", nullable = false)
	public Long getReceiverUserId() {
		return receiverUserId;
	}

	public void setReceiverUserId(Long receiverUserId) {
		this.receiverUserId = receiverUserId;
	}

	@Column(name = "receiverEmail", nullable = false)
	public String getReceiverEmail() {
		return receiverEmail;
	}

	public void setReceiverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}

	@Column(name = "emailContent", nullable = false)
	public String getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}

	@Column(name = "sendStatus", nullable = false)
	public Integer getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(Integer sendStatus) {
		this.sendStatus = sendStatus;
	}

	@Column(name = "createTime", nullable = false)
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "successTime", nullable = false)
	public String getSuccessTime() {
		return successTime;
	}

	public void setSuccessTime(String successTime) {
		this.successTime = successTime;
	}
}