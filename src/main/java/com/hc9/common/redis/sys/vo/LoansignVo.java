package com.hc9.common.redis.sys.vo;

import java.util.List;

/** 项目辅助对象 */
public class LoansignVo {
	/** 主键id */
	private Long id;
	
	/**项目名称*/
	private String name;
	
	/**项目总额*/
	private Double issueLoan;
	
	/**剩余金额*/
	private Double restMoney;
	
	/**优先百分比*/
	private Double prioRate;
	
	/**优先奖励*/
	private Double prioAwordRate;
	
	/**优先剩余金额*/
	private Double prioRestMoney;
	
	/**夹层剩余金额*/
	private Double midRestMoney;
	
	/**劣后剩余金额*/
	private Double afterRestMoney;
	
	/**最低投资金额*/
	private Integer loanUnit;
	
	/**发布时间*/
	private String publishTime;
	
	/**放款时间*/
	private String creditTime;
	
	/**-1 清盘成功 0未发布 1 进行中 2 融资成功 3-满标审批 4-放款待审批   5-放款待审核   6-已放款  7-还款中 8 已完成   9-流标*/
	private Integer status;
	
	/**1-店铺  2-项目 3-天标 4-债权转让*/
	private Integer type;
	
	/**回购期限*/
	private Integer remonth;
	
	/**借款有效期*/
	private Integer validity;
	
	/**
	 *活动状态 0-不参与 1-参与
	 ***/
	private Integer activityStatus=0;

	/** 项目基本信息辅助对象 */
	private LoansignbasicsVo loansignbasics;
	
	/** 项目类型 */
	private LoansignTypeVo loansignType;
	
	/** 还款记录相关列表 */
	private List<RepaymentrecordVo> repaymentrecords;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getIssueLoan() {
		return issueLoan;
	}

	public void setIssueLoan(Double issueLoan) {
		this.issueLoan = issueLoan;
	}

	public Double getRestMoney() {
		return restMoney;
	}

	public void setRestMoney(Double restMoney) {
		this.restMoney = restMoney;
	}

	public Double getPrioRate() {
		return prioRate;
	}

	public void setPrioRate(Double prioRate) {
		this.prioRate = prioRate;
	}

	public Double getPrioAwordRate() {
		return prioAwordRate;
	}

	public void setPrioAwordRate(Double prioAwordRate) {
		this.prioAwordRate = prioAwordRate;
	}

	public Double getPrioRestMoney() {
		return prioRestMoney;
	}

	public void setPrioRestMoney(Double prioRestMoney) {
		this.prioRestMoney = prioRestMoney;
	}

	public Double getMidRestMoney() {
		return midRestMoney;
	}

	public void setMidRestMoney(Double midRestMoney) {
		this.midRestMoney = midRestMoney;
	}

	public Double getAfterRestMoney() {
		return afterRestMoney;
	}

	public void setAfterRestMoney(Double afterRestMoney) {
		this.afterRestMoney = afterRestMoney;
	}

	public Integer getLoanUnit() {
		return loanUnit;
	}

	public void setLoanUnit(Integer loanUnit) {
		this.loanUnit = loanUnit;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getCreditTime() {
		return creditTime;
	}

	public void setCreditTime(String creditTime) {
		this.creditTime = creditTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getRemonth() {
		return remonth;
	}

	public void setRemonth(Integer remonth) {
		this.remonth = remonth;
	}

	public Integer getValidity() {
		return validity;
	}

	public void setValidity(Integer validity) {
		this.validity = validity;
	}

	public Integer getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}

	public LoansignbasicsVo getLoansignbasics() {
		return loansignbasics;
	}

	public void setLoansignbasics(LoansignbasicsVo loansignbasics) {
		this.loansignbasics = loansignbasics;
	}

	public LoansignTypeVo getLoansignType() {
		return loansignType;
	}

	public void setLoansignType(LoansignTypeVo loansignType) {
		this.loansignType = loansignType;
	}

	public List<RepaymentrecordVo> getRepaymentrecords() {
		return repaymentrecords;
	}

	public void setRepaymentrecords(List<RepaymentrecordVo> repaymentrecords) {
		this.repaymentrecords = repaymentrecords;
	}
	
}