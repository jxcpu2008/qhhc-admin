package com.hc9.model;

/**  用户生日祝福手机电子邮箱联系方式 */
public class UserBirthdayLinkInfo {
	/** 用户id */
	private Long userId;
	
	/** 用户名称 */
	private String userName;
	
	/** 用户生日 */
	private String birthday;
	
	/** 手机号码 */
	private String mobilePhoe;
	
	/** 用户email */
	private String email;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getMobilePhoe() {
		return mobilePhoe;
	}

	public void setMobilePhoe(String mobilePhoe) {
		this.mobilePhoe = mobilePhoe;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}