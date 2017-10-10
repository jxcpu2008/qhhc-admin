package com.hc9.model;

/** 邮件中的项目信息 */
public class EmaiLoansignInfo {
	/** 项目id */
	private Long loansignId;
	
	/** 项目名称 */
	private String loansignName;
	
	/** 年化利率 */
	private double prioRate;
	
	/** 优先奖励 */
	private double prioAwordRate;

	/** 总奖励 */
	private double totalRate;
	
	public Long getLoansignId() {
		return loansignId;
	}

	public void setLoansignId(Long loansignId) {
		this.loansignId = loansignId;
	}

	public String getLoansignName() {
		return loansignName;
	}

	public void setLoansignName(String loansignName) {
		this.loansignName = loansignName;
	}

	public double getPrioRate() {
		return prioRate;
	}

	public void setPrioRate(double prioRate) {
		this.prioRate = prioRate;
	}

	public double getPrioAwordRate() {
		return prioAwordRate;
	}

	public void setPrioAwordRate(double prioAwordRate) {
		this.prioAwordRate = prioAwordRate;
	}

	public double getTotalRate() {
		return totalRate;
	}

	public void setTotalRate(double totalRate) {
		this.totalRate = totalRate;
	}	
	
}