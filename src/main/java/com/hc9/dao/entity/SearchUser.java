package com.hc9.dao.entity;

public class SearchUser {

	private String loanfloor;

	private String loanup;

	private String loanTimeFloor;

	private String loanTimeup;

	private Integer loanCountFloor;

	private Integer loanCountUp;

	private String regTimeFoolr;

	private String regTimeUp;
	
	private Integer liveness;   // 1、近一周内登录过3、 近一周内没有登录过 2、近一个月内登录过  4、近一个月内没有登录过
	
	public String getLoanfloor() {
		return loanfloor;
	}

	public void setLoanfloor(String loanfloor) {
		this.loanfloor = loanfloor;
	}

	public String getLoanup() {
		return loanup;
	}

	public void setLoanup(String loanup) {
		this.loanup = loanup;
	}

	public String getLoanTimeFloor() {
		return loanTimeFloor;
	}

	public void setLoanTimeFloor(String loanTimeFloor) {
		this.loanTimeFloor = loanTimeFloor;
	}

	public String getLoanTimeup() {
		return loanTimeup;
	}

	public void setLoanTimeup(String loanTimeup) {
		this.loanTimeup = loanTimeup;
	}

	public Integer getLoanCountFloor() {
		return loanCountFloor;
	}

	public void setLoanCountFloor(Integer loanCountFloor) {
		this.loanCountFloor = loanCountFloor;
	}

	public Integer getLoanCountUp() {
		return loanCountUp;
	}

	public void setLoanCountUp(Integer loanCountUp) {
		this.loanCountUp = loanCountUp;
	}

	public String getRegTimeFoolr() {
		return regTimeFoolr;
	}

	public void setRegTimeFoolr(String regTimeFoolr) {
		this.regTimeFoolr = regTimeFoolr;
	}

	public String getRegTimeUp() {
		return regTimeUp;
	}

	public void setRegTimeUp(String regTimeUp) {
		this.regTimeUp = regTimeUp;
	}

	public Integer getLiveness() {
		return liveness;
	}

	public void setLiveness(Integer liveness) {
		this.liveness = liveness;
	}
}