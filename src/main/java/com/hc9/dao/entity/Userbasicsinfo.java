package com.hc9.dao.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 会员基本信息
 */
@Entity
@Table(name = "userbasicsinfo")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "memberNumber" })
public class Userbasicsinfo implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 登陆用户名
	 */
	private String userName;

	/**
	 * 真实姓名
	 */
	private String name;

	/**
	 * 登陆密码
	 */
	private String password;

	/**
	 * 交易密码
	 */
	private String transPassword;

	/**
	 * 邮箱激活验证码
	 */
	private String randomCode;

	/**
	 * 注册时间
	 */
	private String createTime;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 是否被锁[1-是，0-否]
	 */
	private Integer isLock;

	/**
	 * 锁定时间
	 */
	private String lockTime;

	/**
	 * 上次登陆失败时间
	 */
	private String failTime;

	/**
	 * 登录错误次数
	 */
	private Integer errorNum;

	/**
	 * 是否提示指引
	 */
	private Integer isPrompt;
	/**
	 * 环讯开户时间
	 */
	private String pIpsAcctDate;

	/**
	 * 商户开户用户id
	 */
	private String pMerBillNo;

	/**
	 * 自动还款签约状态
	 */
	private Integer repaySignStatus;

	/**
	 * 商户订单号
	 */
	private String repayAuthNo;
	/**
	 * 用户资金信息
	 */
	private Userfundinfo userfundinfo;

	/**
	 * 会员关联信息
	 */
	private Userrelationinfo userrelationinfo;

	/**
	 * 验证码存放表
	 */
	private Validcodeinfo validcodeinfo;

	/**
	 * 会员编号
	 */
	private MemberNumber memberNumber;

	/**
	 * 客户类型 1基础投资用户，2基础借款人，（3注册宝付的投资者，4机构投资人，5众筹融资人， 6众持融资人）
	 */
	private Integer isCreditor;

	/**
	 * 用户身份认证：状态[0:默认,1:提交,2:通过,3不通过]
	 */
	private Integer cardStatus;

	/**
	 * 用户身份认证提交时间
	 */
	private String userSubtimer;

	/**
	 * 管理员审核用户身份认证时间
	 */
	private String manatoverifyuser;
	/**
	 * 用户积分
	 */
	private Integer userintegral;
	/**
	 * 登录时间
	 */
	private String loginTime;
	/**
	 * 当日登录次数
	 */
	private Integer dayLoginTimes;

	/** 机构投资人 */
	private Integer isorgperson;

	/** 众筹融资人 0 不是 1 是 */
	private Integer iscrowdfundingperson;

	/** 众持融资人 0 不是 1 是 */
	private Integer iscrowdhold;

	/** 宝付授权 0 待确认 1 是 -1 失败 */
	private Integer isAuthIps;

	/** ips授权时间 */
	private String authIpsTime;

	/** 用户页面注册宝付单号 **/
	private String orderSn;

	/** 注册宝付状态：-1失败，0-待确认，1成功 2-注销 */
	private Integer hasIpsAccount;

	private Adminuser adminuser;

	/** 用户类别: 1普通用户2员工3特别企业  4-居间人 6理财师*/
	private Integer userType;

	/** 员工/公司编号 */
	private String staffNo;

	/**
	 * 所属部门 1 总裁办， 2财务部，3行政部, 4 副总办, 5运营中心, 6培训部, 7风控部 ， 8 IT部 ，9
	 * 摄影部，10推广部,11项目部，12客服部，13事业一部，14事业二部,15 离职员工
	 */
	private Integer department;

	/**
	 * 机构投资限额
	 */
	private Double loanlines;
	/**
	 * 信用评级
	 */
	private Integer creditRate;
	/**
	 * 注册来源
	 */
	private Integer registerSource;

	/**
	 * 首次免手续费
	 */
	private Integer fee = 0;

	/** default constructor */
	public Userbasicsinfo() {
	}

	/**
	 * 构造方法
	 * 
	 * @param id
	 *            会员编号
	 */
	public Userbasicsinfo(Long id) {
		this.id = id;
	}

	/**
	 * author:xiongxiaoli
	 * 
	 * @param id
	 *            编号
	 * @param userName
	 *            用户名
	 * @param name
	 *            真实姓名
	 * @param userrelationinfo
	 *            用户关联表
	 */
	public Userbasicsinfo(Long id, String userName, String name,
			Userrelationinfo userrelationinfo) {
		this.id = id;
		this.userName = userName;
		this.name = name;
		this.userrelationinfo = userrelationinfo;
	}

	/**
	 * 构造方法
	 * 
	 * @param id
	 *            编号
	 * @param userName
	 *            用户名
	 * @param name
	 *            真实姓名
	 * @param password
	 *            密码
	 * @param transPassword
	 *            交易密码
	 * @param randomCode
	 *            激活码
	 * @param createTime
	 *            创建时间
	 * @param nickname
	 *            昵称
	 * @param isLock
	 *            是否锁定
	 * @param lockTime
	 *            锁定时间
	 * @param failTime
	 *            登录错误时间
	 * @param errorNum
	 *            错误次数
	 * @param isCreditor
	 *            是否发布有净值标[1-是，0-否]
	 * @param pIpsAcctDate
	 *            环迅开户时间
	 * @param pMerBillNo
	 *            商户开户流水号
	 * @param userrelationinfo
	 *            验证码存放表
	 * @param memberNumber
	 *            会员编号
	 */

	/**
	 * 编号
	 * 
	 * @return 编号
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public Userbasicsinfo(Long id, String userName, String name,
			String password, String transPassword, String randomCode,
			String createTime, String nickname, Integer isLock,
			String lockTime, String failTime, Integer errorNum,
			Integer isLoanState, String pIpsAcctDate, String pMerBillNo,
			Integer repaySignStatus, String repayAuthNo,
			Userfundinfo userfundinfo, Userrelationinfo userrelationinfo,
			Validcodeinfo validcodeinfo, MemberNumber memberNumber,
			Integer isCreditor, Integer cardStatus, String userSubtimer,
			String manatoverifyuser, Integer userintegral, Adminuser adminuser) {
		super();
		this.id = id;
		this.userName = userName;
		this.name = name;
		this.password = password;
		this.transPassword = transPassword;
		this.randomCode = randomCode;
		this.createTime = createTime;
		this.nickname = nickname;
		this.isLock = isLock;
		this.lockTime = lockTime;
		this.failTime = failTime;
		this.errorNum = errorNum;
		this.isPrompt = isPrompt;
		this.pIpsAcctDate = pIpsAcctDate;
		this.pMerBillNo = pMerBillNo;
		this.repaySignStatus = repaySignStatus;
		this.repayAuthNo = repayAuthNo;
		this.userfundinfo = userfundinfo;
		this.userrelationinfo = userrelationinfo;
		this.validcodeinfo = validcodeinfo;
		this.memberNumber = memberNumber;
		this.isCreditor = isCreditor;
		this.cardStatus = cardStatus;
		this.userSubtimer = userSubtimer;
		this.manatoverifyuser = manatoverifyuser;
		this.userintegral = userintegral;
		this.adminuser = adminuser;
	}

	/**
	 * 编号
	 * 
	 * @param id
	 *            编号
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * userfundinfo
	 * 
	 * @return userfundinfo
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	public Userfundinfo getUserfundinfo() {
		return userfundinfo;
	}

	/**
	 * userfundinfo
	 * 
	 * @param userfundinfo
	 *            userfundinfo
	 */
	public void setUserfundinfo(Userfundinfo userfundinfo) {
		this.userfundinfo = userfundinfo;
	}

	/**
	 * 用户名
	 * 
	 * @return 用户名
	 */
	@Column(name = "userName", length = 50)
	public String getUserName() {
		return this.userName;
	}

	/**
	 * 用户名
	 * 
	 * @param userName
	 *            用户名
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 真实姓名
	 * 
	 * @return 真实姓名
	 */
	@Column(name = "name", length = 50)
	public String getName() {
		return this.name;
	}

	/**
	 * 真实姓名
	 * 
	 * @param name
	 *            真实姓名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 密码
	 * 
	 * @return 密码
	 */
	@Column(name = "password")
	public String getPassword() {
		return this.password;
	}

	/**
	 * 密码
	 * 
	 * @param password
	 *            密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 交易密码
	 * 
	 * @return 交易密码
	 */
	@Column(name = "transPassword")
	public String getTransPassword() {
		return this.transPassword;
	}

	/**
	 * 交易密码
	 * 
	 * @param transPassword
	 *            交易密码
	 */
	public void setTransPassword(String transPassword) {
		this.transPassword = transPassword;
	}

	/**
	 * 激活码
	 * 
	 * @return 激活码
	 */
	@Column(name = "randomCode")
	public String getRandomCode() {
		return this.randomCode;
	}

	/**
	 * 激活码
	 * 
	 * @param randomCode
	 *            激活码
	 */
	public void setRandomCode(String randomCode) {
		this.randomCode = randomCode;
	}

	/**
	 * 创建时间
	 * 
	 * @return 创建时间
	 */
	@Column(name = "createTime", length = 80)
	public String getCreateTime() {
		return this.createTime;
	}

	/**
	 * 创建时间
	 * 
	 * @param createTime
	 *            创建时间
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * 昵称
	 * 
	 * @return 昵称
	 */
	@Column(name = "nickname")
	public String getNickname() {
		return this.nickname;
	}

	/**
	 * 昵称
	 * 
	 * @param nickname
	 *            昵称
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * 是否锁定
	 * 
	 * @return 是否锁定
	 */
	@Column(name = "isLock")
	public Integer getIsLock() {
		return this.isLock;
	}

	/**
	 * 是否锁定
	 * 
	 * @param isLock
	 *            是否锁定
	 */
	public void setIsLock(Integer isLock) {
		this.isLock = isLock;
	}

	/**
	 * 锁定时间
	 * 
	 * @return 锁定时间
	 */
	@Column(name = "lockTime", length = 80)
	public String getLockTime() {
		return this.lockTime;
	}

	/**
	 * 锁定时间
	 * 
	 * @param lockTime
	 *            锁定时间
	 */
	public void setLockTime(String lockTime) {
		this.lockTime = lockTime;
	}

	/**
	 * 登陆错误时间
	 * 
	 * @return 登录错误时间
	 */
	@Column(name = "failTime", length = 80)
	public String getFailTime() {
		return this.failTime;
	}

	/**
	 * 登录错误时间
	 * 
	 * @param failTime
	 *            登录错误时间
	 */
	public void setFailTime(String failTime) {
		this.failTime = failTime;
	}

	/**
	 * 错误次数
	 * 
	 * @return 错误次数
	 */
	@Column(name = "errorNum")
	public Integer getErrorNum() {
		return this.errorNum;
	}

	/**
	 * 错误次数
	 * 
	 * @param errorNum
	 *            错误次数
	 */
	public void setErrorNum(Integer errorNum) {
		this.errorNum = errorNum;
	}

	/**
	 * 是否发布有净值标
	 * 
	 * @return isLoanState
	 */
	@Column(name = "isPrompt")
	public Integer getIsPrompt() {
		return isPrompt;
	}

	public void setIsPrompt(Integer isPrompt) {
		this.isPrompt = isPrompt;
	}

	/**
	 * 会员关联表
	 * 
	 * @return 会员关联表
	 */
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	public Userrelationinfo getUserrelationinfo() {
		return userrelationinfo;
	}

	/**
	 * 会员关联表
	 * 
	 * @param userrelationinfo
	 *            会员关联表
	 */
	public void setUserrelationinfo(Userrelationinfo userrelationinfo) {
		this.userrelationinfo = userrelationinfo;
	}

	/**
	 * 验证码存放表
	 * 
	 * @return 验证码存放表
	 */
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	public Validcodeinfo getValidcodeinfo() {
		return validcodeinfo;
	}

	/**
	 * 验证码存放表
	 * 
	 * @param validcodeinfo
	 *            验证码存放表
	 */
	public void setValidcodeinfo(Validcodeinfo validcodeinfo) {
		this.validcodeinfo = validcodeinfo;
	}

	/**
	 * 环迅开户时间
	 * 
	 * @return 环迅开户时间
	 */
	@Column(name = "pIpsAcctDate", length = 50)
	public String getpIpsAcctDate() {
		return pIpsAcctDate;
	}

	/**
	 * 环迅开户时间
	 * 
	 * @param pIpsAcctDate
	 *            环迅开户时间
	 */
	public void setpIpsAcctDate(String pIpsAcctDate) {
		this.pIpsAcctDate = pIpsAcctDate;
	}

	/**
	 * 用户宝付账号id
	 * 
	 * @return 用户宝付账号id
	 */
	@Column(name = "pMerBillNo", length = 50)
	public String getpMerBillNo() {
		return pMerBillNo;
	}

	/**
	 * 用户宝付账号id
	 * 
	 * @param pMerBillNo
	 *            用户宝付账号id
	 */
	public void setpMerBillNo(String pMerBillNo) {
		this.pMerBillNo = pMerBillNo;
	}

	/**
	 * 会员编码表
	 * 
	 * @return 会员编码表
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_number_id")
	public MemberNumber getMemberNumber() {
		return memberNumber;
	}

	/**
	 * 会员编码表
	 * 
	 * @param memberNumber
	 *            会员编码表
	 */
	public void setMemberNumber(MemberNumber memberNumber) {
		this.memberNumber = memberNumber;
	}

	@Column(name = "repaySignStatus")
	public Integer getRepaySignStatus() {
		return repaySignStatus;
	}

	@Column(name = "isCreditor")
	public Integer getIsCreditor() {
		return isCreditor;
	}

	public void setIsCreditor(Integer isCreditor) {
		this.isCreditor = isCreditor;
	}

	public void setRepaySignStatus(Integer repaySignStatus) {
		this.repaySignStatus = repaySignStatus;
	}

	@Column(name = "repayAuthNo")
	public String getRepayAuthNo() {
		return repayAuthNo;
	}

	public void setRepayAuthNo(String repayAuthNo) {
		this.repayAuthNo = repayAuthNo;
	}

	@Column(name = "cardStatus")
	public Integer getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(Integer cardStatus) {
		this.cardStatus = cardStatus;
	}

	@Column(name = "userSubtimer")
	public String getUserSubtimer() {
		return userSubtimer;
	}

	public void setUserSubtimer(String userSubtimer) {
		this.userSubtimer = userSubtimer;
	}

	@Column(name = "manatoverifyuser")
	public String getManatoverifyuser() {
		return manatoverifyuser;
	}

	public void setManatoverifyuser(String manatoverifyuser) {
		this.manatoverifyuser = manatoverifyuser;
	}

	@Column(name = "userintegral")
	public Integer getUserintegral() {
		return userintegral;
	}

	public void setUserintegral(Integer userintegral) {
		this.userintegral = userintegral;
	}

	@ManyToOne()
	@JoinColumn(name = "adminuser_id")
	public Adminuser getAdminuser() {
		return adminuser;
	}

	public void setAdminuser(Adminuser adminuser) {
		this.adminuser = adminuser;
	}

	@Column(name = "loginTime")
	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	@Column(name = "dayLoginTimes")
	public Integer getDayLoginTimes() {
		return dayLoginTimes;
	}

	public void setDayLoginTimes(Integer dayLoginTimes) {
		this.dayLoginTimes = dayLoginTimes;
	}

	@Column(name = "isOrgPerson")
	public Integer getIsorgperson() {
		return isorgperson;
	}

	@Column(name = "isCrowdfundingPerson")
	public Integer getIscrowdfundingperson() {
		return iscrowdfundingperson;
	}

	@Column(name = "isCrowdHold")
	public Integer getIscrowdhold() {
		return iscrowdhold;
	}

	public void setIsorgperson(Integer isorgperson) {
		this.isorgperson = isorgperson;
	}

	public void setIscrowdfundingperson(Integer iscrowdfundingperson) {
		this.iscrowdfundingperson = iscrowdfundingperson;
	}

	public void setIscrowdhold(Integer iscrowdhold) {
		this.iscrowdhold = iscrowdhold;
	}

	@Column(name = "isAuthIps")
	public Integer getIsAuthIps() {
		return isAuthIps;
	}

	public void setIsAuthIps(Integer isAuthIps) {
		this.isAuthIps = isAuthIps;
	}

	@Column(name = "authIpsTime")
	public String getAuthIpsTime() {
		return authIpsTime;
	}

	public void setAuthIpsTime(String authIpsTime) {
		this.authIpsTime = authIpsTime;
	}

	@Column(name = "hasIpsAccount")
	public Integer getHasIpsAccount() {
		return hasIpsAccount;
	}

	public void setHasIpsAccount(Integer hasIpsAccount) {
		this.hasIpsAccount = hasIpsAccount;
	}

	@Column(name = "orderSn")
	public String getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}

	@Column(name = "user_type")
	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	@Column(name = "staff_no")
	public String getStaffNo() {
		return staffNo;
	}

	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}

	@Column(name = "credit_rate")
	public Integer getCreditRate() {
		return creditRate;
	}

	public void setCreditRate(Integer creditRate) {
		this.creditRate = creditRate;
	}

	@Column(name = "department")
	public Integer getDepartment() {
		return department;
	}

	public void setDepartment(Integer department) {
		this.department = department;
	}

	@Column(name = "loanlines")
	public Double getLoanlines() {
		return loanlines;
	}

	public void setLoanlines(Double loanlines) {
		this.loanlines = loanlines;
	}

	@Column(name = "registerSource")
	public Integer getRegisterSource() {
		return registerSource;
	}

	public void setRegisterSource(Integer registerSource) {
		this.registerSource = registerSource;
	}

	@Column(name = "fee")
	public Integer getFee() {
		return fee;
	}

	public void setFee(Integer fee) {
		this.fee = fee;
	}

}