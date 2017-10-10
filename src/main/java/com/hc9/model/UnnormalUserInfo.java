package com.hc9.model;

/**
 * 状态异常的用户信息（比如宝付注册状态异常的用户）
 * @author Administrator
 *
 */
public class UnnormalUserInfo {
	
	private Long payLogId;
	
	private Long userId;
	
	private String userName;
	
	private String name;
	
	private int isAuthIps;
	
	private int hasIpsAccount;
	
	private String action;
	
	private String billNo;
	
	private String phone;
	
	private String regTime;
	
	private String operResult;

	public String getOperResult() {
		return operResult;
	}

	public void setOperResult(String operResult) {
		this.operResult = operResult;
	}

	public Long getPayLogId() {
		return payLogId;
	}

	public void setPayLogId(Long payLogId) {
		this.payLogId = payLogId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getIsAuthIps() {
		return isAuthIps;
	}

	public void setIsAuthIps(int isAuthIps) {
		this.isAuthIps = isAuthIps;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
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

	public int getHasIpsAccount() {
		return hasIpsAccount;
	}

	public void setHasIpsAccount(int hasIpsAccount) {
		this.hasIpsAccount = hasIpsAccount;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRegTime() {
		return regTime;
	}

	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}
}