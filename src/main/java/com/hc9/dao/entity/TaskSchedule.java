package com.hc9.dao.entity;

public class TaskSchedule {
	/**
	 * 任务进度
	 */
	private String taskSchedule;
	/**
	 * 完成时间
	 */
	private String completionTime;
	/**
	 * 获得奖励
	 */
	private Double rewardMoney;
	/**
	 * 奖励领取
	 */
	private String rewardReceive;
	/**
	 * 领取时间
	 */
	private String receiveTime;

	public String getTaskSchedule() {
		return taskSchedule;
	}

	public void setTaskSchedule(String taskSchedule) {
		this.taskSchedule = taskSchedule;
	}

	public String getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(String completionTime) {
		this.completionTime = completionTime;
	}

	public Double getRewardMoney() {
		return rewardMoney;
	}

	public void setRewardMoney(Double rewardMoney) {
		this.rewardMoney = rewardMoney;
	}

	public String getRewardReceive() {
		return rewardReceive;
	}

	public void setRewardReceive(String rewardReceive) {
		this.rewardReceive = rewardReceive;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}



}
