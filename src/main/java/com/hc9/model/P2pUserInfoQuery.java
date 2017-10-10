package com.hc9.model;

public class P2pUserInfoQuery {
	
	private String billNo;
	
	private int type;
	
	private String start_time;
	
	private String end_time;

	public P2pUserInfoQuery(int type) {
		this.type = type;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
}