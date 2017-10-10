package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "channelspreaddetail")
public class ChannelSpreadDetail {

	private Integer id;

	private String spreadId;// 8为随机大小写字母

	private String regUserName; // 推广人名称

	private Integer regStatus; // 1、成功 -1、失败

	private String sessionId;

	private String createTime;
	
	private Long loanRecordId;//投资记录id
	
	private String cookieValue;//cookie值

	public ChannelSpreadDetail() {
		
	}
	
	public ChannelSpreadDetail(String spreadId, String regUserName,
			Integer regStatus, String sessionId, String createTime) {
		super();
		this.spreadId = spreadId;
		this.regUserName = regUserName;
		this.regStatus = regStatus;
		this.sessionId = sessionId;
		this.createTime = createTime;
	}

	@Column(name = "createTime")
	public String getCreateTime() {
		return createTime;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	@Column(name = "regStatus")
	public Integer getRegStatus() {
		return regStatus;
	}

	@Column(name = "regUserName")
	public String getRegUserName() {
		return regUserName;
	}

	@Column(name = "sessionId")
	public String getSessionId() {
		return sessionId;
	}

	@Column(name = "spreadId")
	public String getSpreadId() {
		return spreadId;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setRegStatus(Integer regStatus) {
		this.regStatus = regStatus;
	}

	public void setRegUserName(String regUserName) {
		this.regUserName = regUserName;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setSpreadId(String spreadId) {
		this.spreadId = spreadId;
	}
	

	
	@Column(name = "loanRecordId")
	public Long getLoanRecordId() {
		return loanRecordId;
	}

	public void setLoanRecordId(Long loanRecordId) {
		this.loanRecordId = loanRecordId;
	}
	
	@Column(name = "cookieValue")
	public String getCookieValue() {
		return cookieValue;
	}

	public void setCookieValue(String cookieValue) {
		this.cookieValue = cookieValue;
	}

}
