package com.hc9.model.report;

/** 新手任务辅助类 */
public class NewerTaskVo {

	/**
	 * 用户ID
	 */
	private Long id;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 真实姓名
	 */
	private String name;
	/**
	 * 手机号
	 */
	private String mobilePhone;
	/**
	 * 注册时间
	 */
	private String createTime;

	/**
	 * 宝付授权 0 待确认 1 是 -1 失败
	 */
	private Integer isAuthIps;
	/**
	 * 推荐人用户名
	 */
	private String recommendUserName;
	/**
	 * 推荐人真实姓名
	 */
	private String recommendName;
	/**
	 * 推荐人部门 所属部门 1 总裁办， 2财务部，3行政部, 4 副总办, 5运营中心, 6培训部, 7风控部 ， 8 IT部 ，9
	 * 摄影部，10推广部,11项目部，12客服部，13事业一部，14事业二部,15 离职员工
	 */
	private Integer department;
	/**
	 * 来源 1、PC 2、H5; 8、android，9、ios；
	 */
	private Integer registerSource;
	/**
	 * 任务完成进度: 1-已完成,2未完成
	 */
	private Integer taskCompleteSchedule;

	private String beginTime;
	private String endTime;

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

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Integer getIsAuthIps() {
		return isAuthIps;
	}

	public void setIsAuthIps(Integer isAuthIps) {
		this.isAuthIps = isAuthIps;
	}

	public Integer getDepartment() {
		return department;
	}

	public void setDepartment(Integer department) {
		this.department = department;
	}

	public Integer getRegisterSource() {
		return registerSource;
	}

	public void setRegisterSource(Integer registerSource) {
		this.registerSource = registerSource;
	}

	public Integer getTaskCompleteSchedule() {
		return taskCompleteSchedule;
	}

	public void setTaskCompleteSchedule(Integer taskCompleteSchedule) {
		this.taskCompleteSchedule = taskCompleteSchedule;
	}

	public String getRecommendUserName() {
		return recommendUserName;
	}

	public void setRecommendUserName(String recommendUserName) {
		this.recommendUserName = recommendUserName;
	}

	public String getRecommendName() {
		return recommendName;
	}

	public void setRecommendName(String recommendName) {
		this.recommendName = recommendName;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
