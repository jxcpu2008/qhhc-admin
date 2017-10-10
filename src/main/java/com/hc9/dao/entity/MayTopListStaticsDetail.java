package com.hc9.dao.entity;

/**5月榜单列表查询辅助类*/
public class MayTopListStaticsDetail implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	/**名次*/
	private Integer ranking;
	/**用户名*/
	private String userName;
	/** 真实姓名*/
	private String name;
	/*** 手机号码*/
	private String mobilePhone;
	/** 注册时间*/
	private String createTime;
	/** 累计周投资金额 */
	private long weekMoney;
	/** 累计周年化投资金额 */
	private double weekYearMoney;
	/** 奖励 */
	private String rewardMoney;
	/** 获奖时间*/
	private Integer weekNum;

	public Integer getRanking() {
		return ranking;
	}
	public void setRanking(Integer ranking) {
		this.ranking = ranking;
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
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	public long getWeekMoney() {
		return weekMoney;
	}
	public void setWeekMoney(long weekMoney) {
		this.weekMoney = weekMoney;
	}
	public double getWeekYearMoney() {
		return weekYearMoney;
	}
	public void setWeekYearMoney(double weekYearMoney) {
		this.weekYearMoney = weekYearMoney;
	}
	
	public String getRewardMoney() {
		return rewardMoney;
	}
	public void setRewardMoney(String rewardMoney) {
		this.rewardMoney = rewardMoney;
	}
	public Integer getWeekNum() {
		return weekNum;
	}
	public void setWeekNum(Integer weekNum) {
		this.weekNum = weekNum;
	}
	
}