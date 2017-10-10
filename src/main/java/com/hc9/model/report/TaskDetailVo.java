package com.hc9.model.report;

/** 任务明细辅助类 */
public class TaskDetailVo {
	/** 任务进度*/
	private String taskProgress;
	
	/** 完成时间 */
	private String completeTime;
	
	/** 获得奖励 */
	private String prizeDetail;
	
	/** 奖励领取 */
	private String receiveStus;
	
	/** 领取时间 */
	private String receiveTime;

	public String getTaskProgress() {
		return taskProgress;
	}

	public void setTaskProgress(String taskProgress) {
		this.taskProgress = taskProgress;
	}

	public String getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
	}

	public String getPrizeDetail() {
		return prizeDetail;
	}

	public void setPrizeDetail(String prizeDetail) {
		this.prizeDetail = prizeDetail;
	}

	public String getReceiveStus() {
		return receiveStus;
	}

	public void setReceiveStus(String receiveStus) {
		this.receiveStus = receiveStus;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}
}
