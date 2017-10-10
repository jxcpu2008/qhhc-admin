package com.hc9.model;

/** 用户信息相关 */
public class UserInfo {
	/** 用户id  */
	private Long userId;
	
	/** 用户登录账号 */
	private String userName;
	
	/** 用户真实姓名 */
	private String name;
	
	/** 用户类型 */
	private Integer userType;
	
	/** 可用余额 */
	private Double cashBalance;
	
	/** 注册宝付状态：-1失败，0待确认，1成功 */
	private int ipsAccountStatus;
	
	/** 认购产品名称 */
	private String loanSignName;
	
	/** 认购产品状态:-1 清盘成功 0未发布 1 进行中 2 融资成功 3-申请审批 4-已审批(待审核)   5-财务审核   6-已放款  7-还款中 8 已完成   9-流标 */
	private int loanSignStatus;
	
	/** 投资id */
	private Long loanId;
	
	/** 认购产品金额 */
	private Double investMoney;
	
	/** 认购时间 */
	private String investTime;
	
	/** 投资类型： 1 优先 2 夹层 3劣后 */
	private int loanType;
	
	/** 推荐人 */
	private String generalizerName;
	
	/** 推荐人部门 */
	private Integer generalizerDepartment;
	
	/** 此单佣金 */
	private Double commissionMoney;
	
	/** 上次登录时间 */
	private String lastLoginTime;
	
	/**推荐人用户类型**/
	private Integer generalizeUserType;
	
	/**注册时间*/
	private String createTime;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Double getCashBalance() {
		return cashBalance;
	}

	public void setCashBalance(Double cashBalance) {
		this.cashBalance = cashBalance;
	}

	public int getIpsAccountStatus() {
		return ipsAccountStatus;
	}

	public void setIpsAccountStatus(int ipsAccountStatus) {
		this.ipsAccountStatus = ipsAccountStatus;
	}

	public String getLoanSignName() {
		return loanSignName;
	}

	public void setLoanSignName(String loanSignName) {
		this.loanSignName = loanSignName;
	}

	public int getLoanSignStatus() {
		return loanSignStatus;
	}

	public void setLoanSignStatus(int loanSignStatus) {
		this.loanSignStatus = loanSignStatus;
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public Double getInvestMoney() {
		return investMoney;
	}

	public void setInvestMoney(Double investMoney) {
		this.investMoney = investMoney;
	}

	public String getInvestTime() {
		return investTime;
	}

	public void setInvestTime(String investTime) {
		this.investTime = investTime;
	}

	public int getLoanType() {
		return loanType;
	}

	public void setLoanType(int loanType) {
		this.loanType = loanType;
	}

	public String getGeneralizerName() {
		return generalizerName;
	}

	public void setGeneralizerName(String generalizerName) {
		this.generalizerName = generalizerName;
	}

	public Integer getGeneralizerDepartment() {
		return generalizerDepartment;
	}

	public void setGeneralizerDepartment(Integer generalizerDepartment) {
		this.generalizerDepartment = generalizerDepartment;
	}

	public Double getCommissionMoney() {
		return commissionMoney;
	}

	public void setCommissionMoney(Double commissionMoney) {
		this.commissionMoney = commissionMoney;
	}

	public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Integer getGeneralizeUserType() {
		return generalizeUserType;
	}

	public void setGeneralizeUserType(Integer generalizeUserType) {
		this.generalizeUserType = generalizeUserType;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
	
}