package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="smsemailplantime")
public class SmsEmailPlanTime {
	
	// ID
	private Long id;
	
	/**
	 * 发送计划对应的id(smsEmailSendPlan的id)
	 */
	private Long sendPlanId;
	
	/**
	 * 发送开始时间
	 */
	private String sendBeginTime;
	
	/**
	 * 发送结束时
	 */
	private String sendEndTime;
	
	/**
	 * 间隔时间（小时为单位）
	 */
	private Integer intervalTime;
	
	/**
	 * 单位时间内发送的消息数量
	 */
	private Integer msgNum;
	
	// 创建时间
	private String createTime;
	
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
	
	@Column(name = "sendBeginTime", nullable = false)
	public String getSendBeginTime() {
		return sendBeginTime;
	}
	
	public void setSendBeginTime(String sendBeginTime) {
		this.sendBeginTime = sendBeginTime;
	}
	
	@Column(name = "sendEndTime", nullable = false)
	public String getSendEndTime() {
		return sendEndTime;
	}
	
	public void setSendEndTime(String sendEndTime) {
		this.sendEndTime = sendEndTime;
	}
	
	@Column(name = "intervalTime", nullable = false)
	public Integer getIntervalTime() {
		return intervalTime;
	}
	
	public void setIntervalTime(Integer intervalTime) {
		this.intervalTime = intervalTime;
	}
	
	@Column(name = "msgNum", nullable = false)
	public Integer getMsgNum() {
		return msgNum;
	}
	
	public void setMsgNum(Integer msgNum) {
		this.msgNum = msgNum;
	}
	
	@Column(name = "createTime", nullable = false)
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
