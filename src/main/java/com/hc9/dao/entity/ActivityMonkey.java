package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/** 新春猴给力活动信息表 */
@Entity
@Table(name = "activity_monkey")
public class ActivityMonkey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	/** 主键id，自增 */
	private Long id;
	
	/** 用户id */
	private Long userId;
	
	/** 手机号 */
	private String mobilePhone;
	
	/** 投资金额 */
	private Double money;
	
	/** 类型1:一鸣惊人,2:一锤定音,3:周第一名,4:周第二名,5:周第三名,6:达人第一名,7:达人第二名,8:达人第三名,9:理财师10:现金2元,11:翻牌抽奖； */
	private Integer type;
	
	/** 标id */
	private Long loanId;
	
	/** 标名称 */
	private String loanName;
	
	/** 投资记录id */
	private Long loanRecordId;
	
	/** 现金奖励金额 */
	private Double rewardMoney;
	
	/** 发放状态：0:待发放,1:已发放，2:发放待确认 -1 发放失败*/
	private Integer status = 0;
	
	/** 审核状态：0、未审核；1、通过； */
	private Integer examineStatus = 0;
	
	/***
	 * 审核人
	 */
	private  Long examineAdminId; 
	
	/***
	 * 审核时间
	 */
	private String examineTime;
	
	/***
	 * 发放人
	 */
	private  Long grantAdminId;
	
	
	/** 发放时间 */
	private String grantTime;
	
	/**获奖时间*/
	private String createTime;
	
	/**活动二:金袍加身使用 第几周中奖 从1开始**/
	private Integer week;
	
	/***
	 * 发放奖励订单号
	 */
	private  String orderNum;
	
	/***
	 * 转账失败原因
	 */
	private  String failreason;
	
	/**
	 * 被推荐人编号
	 */
	private Userbasicsinfo byUser;
	

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "userId", length = 20)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "mobilePhone", length = 32)
	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	@Column(name = "money", precision = 10, scale = 2)
	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	@Column(name = "type", length = 1)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "loanId", length = 20)
	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	@Column(name = "loanName", length = 20)
	public String getLoanName() {
		return loanName;
	}

	public void setLoanName(String loanName) {
		this.loanName = loanName;
	}

	@Column(name = "loanRecordId", length = 20)
	public Long getLoanRecordId() {
		return loanRecordId;
	}

	public void setLoanRecordId(Long loanRecordId) {
		this.loanRecordId = loanRecordId;
	}

	@Column(name = "rewardMoney", precision = 10, scale = 2)
	public Double getRewardMoney() {
		return rewardMoney;
	}

	public void setRewardMoney(Double rewardMoney) {
		this.rewardMoney = rewardMoney;
	}

	@Column(name = "status", length = 1)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "examineStatus", length = 1)
	public Integer getExamineStatus() {
		return examineStatus;
	}

	public void setExamineStatus(Integer examineStatus) {
		this.examineStatus = examineStatus;
	}

	@Column(name = "examineAdminId")
	public Long getExamineAdminId() {
		return examineAdminId;
	}

	public void setExamineAdminId(Long examineAdminId) {
		this.examineAdminId = examineAdminId;
	}

	@Column(name = "examineTime")
	public String getExamineTime() {
		return examineTime;
	}

	public void setExamineTime(String examineTime) {
		this.examineTime = examineTime;
	}

	@Column(name = "grantAdminId")
	public Long getGrantAdminId() {
		return grantAdminId;
	}

	public void setGrantAdminId(Long grantAdminId) {
		this.grantAdminId = grantAdminId;
	}

	@Column(name = "grantTime")
	public String getGrantTime() {
		return grantTime;
	}

	public void setGrantTime(String grantTime) {
		this.grantTime = grantTime;
	}

	@Column(name = "createTime")
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "week", length = 1)
	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	@Column(name = "orderNum")
	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	@Column(name = "failreason")
	public String getFailreason() {
		return failreason;
	}

	public void setFailreason(String failreason) {
		this.failreason = failreason;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "byUserId")
	public Userbasicsinfo getByUser() {
		return byUser;
	}

	public void setByUser(Userbasicsinfo byUser) {
		this.byUser = byUser;
	}

}