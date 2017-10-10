package com.hc9.model;

/**
 *首页标列表模型 
 *
 */
public class LoanlistVo implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**标id*/
	private String id;
	
	/**标名称*/
	private String name;
	
	/**借款期限*/
	private String remonth;
	
	/**借款金额*/
	private String issueLoan;
	
	/**起投金额*/
	private String loanUnit;
	
	/**剩余金额*/
	private String restMoney;
	
	/**优先利率*/
	private String prioRate;
	
	/**优先奖励*/
	private String prioAwordRate;
	
	/**标类型*/
	private String type;
	
	/**标状态*/
	private String status;
	
	/**子类型*/	
	private String typeId;
	
	/**是否参与活动标识*/
	private String activityStatus;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemonth() {
		return remonth;
	}
	public void setRemonth(String remonth) {
		this.remonth = remonth;
	}
	public String getIssueLoan() {
		return issueLoan;
	}
	public void setIssueLoan(String issueLoan) {
		this.issueLoan = issueLoan;
	}
	public String getLoanUnit() {
		return loanUnit;
	}
	public void setLoanUnit(String loanUnit) {
		this.loanUnit = loanUnit;
	}
	public String getRestMoney() {
		return restMoney;
	}
	public void setRestMoney(String restMoney) {
		this.restMoney = restMoney;
	}
	public String getPrioRate() {
		return prioRate;
	}
	public void setPrioRate(String prioRate) {
		this.prioRate = prioRate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getPrioAwordRate() {
		return prioAwordRate;
	}
	public void setPrioAwordRate(String prioAwordRate) {
		this.prioAwordRate = prioAwordRate;
	}
	public String getActivityStatus() {
		return activityStatus;
	}
	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}
	
}
