package com.hc9.dao.entity;

/** 首投活动列表查询辅助类 */
public class FirstInvenstStaticsDetail implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 首投用户名
	 */
	private String userName;
	/**
	 * 真实姓名
	 */
	private String name;
	/**
	 * 手机号
	 */
	private String mobilePhone;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 认购时间
	 */
	private String tenderTime;
	/**
	 * 认购金额
	 */
	private Double tenderMoney;
	/**
	 * 产品属性:1-优先，2-夹层，3-列后 ,4-vip众筹，5-股东众筹，
	 */
	private Integer subType;
	/**
	 * 奖励金额:1-10元,2-30元,3-50元,
	 */
	private Double rewardMoney;
	/**
	 * 活动类型:1-5月首投活动
	 */
	private Integer type; 
	
	
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getTenderTime() {
		return tenderTime;
	}
	public void setTenderTime(String tenderTime) {
		this.tenderTime = tenderTime;
	}
	public Double getTenderMoney() {
		return tenderMoney;
	}
	public void setTenderMoney(Double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}
	
	public Integer getSubType() {
		return subType;
	}
	public void setSubType(Integer subType) {
		this.subType = subType;
	}
	public Double getRewardMoney() {
		return rewardMoney;
	}
	public void setRewardMoney(Double rewardMoney) {
		this.rewardMoney = rewardMoney;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
}
