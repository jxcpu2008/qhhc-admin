package com.hc9.model;

public class UserVo implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  Long id;
	/** 用户登录账号 */
	private String userName;
	
	/** 用户真实姓名 */
	private String name;
	
	private String phone;
	private String createTime;
	
	private String genUser;
	
	private String genUserDept;
	
	private String userType;
	
	private String channel;
	
	private String department;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getGenUser() {
		return genUser;
	}

	public void setGenUser(String genUser) {
		this.genUser = genUser;
	}

	public String getGenUserDept() {
		return genUserDept;
	}

	public void setGenUserDept(String genUserDept) {
		this.genUserDept = genUserDept;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
	
	
	
}
