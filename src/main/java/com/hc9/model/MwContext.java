package com.hc9.model;

public class MwContext {
	private String phone;//手机
	private String message;//短信内容
	
	
	public MwContext(String phone, String message) {
		this.phone = phone;
		this.message = message;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
