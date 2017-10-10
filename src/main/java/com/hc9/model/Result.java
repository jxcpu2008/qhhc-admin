package com.hc9.model;

public class Result {
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 错误码
	 */
	private String errorCode;
	/**
	 * 错误信息
	 */
	private String errorMessage;
	/**
	 * 返回值
	 */
	private String returnValue;
	
	public Result(){
		
	}
	public Result(String status, String errorCode, String errorMessage) {
		this.status = status;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	
}