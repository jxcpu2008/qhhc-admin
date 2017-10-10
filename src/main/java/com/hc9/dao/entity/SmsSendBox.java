package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "smssendbox")
public class SmsSendBox {

	// ID
	private Long id;

	/**
	 * 发送计划对应的id(smsEmailSendPlan的id)
	 */
	private Long sendPlanId;

	/**
	 * 接收人用户id
	 */
	private Long receiverUserId;

	/**
	 * 接收人手机号
	 */
	private String receiverPhone;

	/**
	 * 短信内容（用于支持个性化字段填充的短信，方便事后查询）
	 */
	private String smsContent;

	/**
	 * 发送状态：1、待发送；2、终止发送；3、发送成功；-1、软删除；
	 */
	private Integer sendStatus;

	/**
	 * 备注
	 */
	private String remark;

	// 创建时间
	private String createTime;

	// 成功时间
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

	@Column(name = "receiverPhone", nullable = false)
	public String getReceiverPhone() {
		return receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	@Column(name = "smsContent", nullable = false)
	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	@Column(name = "sendStatus", nullable = false)
	public Integer getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(Integer sendStatus) {
		this.sendStatus = sendStatus;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
