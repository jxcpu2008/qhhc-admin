package com.hc9.model;

/** 登录相关缓存对象 */
public class LoginRelVo {
	/** 用户主键id */
	private Long id;
	
	/** 用户登录账号 */
	private String userName;
	
	/** 用户手机号码 */
	private String phone;
	
	/** 显示名称 */
	private String name;
	
	/** 用户密码 */
	private String password;
	
	/** 是否被锁定状态 */
	private Integer isLock;
	
	/** 实名认证状态 */
	private Integer cardStatus;
	
	/** 邮箱是否通过验证 1通过 0不通过 */
	private Integer emailisPass;
	
	/** 宝付授权 */
	private Integer isAuthIps;
	
	/** 商户宝付账号 */
	private String pMerBillNo;
	
	/** 用工编码 */
	private String staffNo;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getIsLock() {
		return isLock;
	}

	public void setIsLock(Integer isLock) {
		this.isLock = isLock;
	}

	public Integer getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(Integer cardStatus) {
		this.cardStatus = cardStatus;
	}

	public Integer getEmailisPass() {
		return emailisPass;
	}

	public void setEmailisPass(Integer emailisPass) {
		this.emailisPass = emailisPass;
	}

	public Integer getIsAuthIps() {
		return isAuthIps;
	}

	public void setIsAuthIps(Integer isAuthIps) {
		this.isAuthIps = isAuthIps;
	}

	public String getpMerBillNo() {
		return pMerBillNo;
	}

	public void setpMerBillNo(String pMerBillNo) {
		this.pMerBillNo = pMerBillNo;
	}

	public String getStaffNo() {
		return staffNo;
	}

	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}
	
}