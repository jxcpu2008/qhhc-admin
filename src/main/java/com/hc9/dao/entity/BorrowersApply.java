package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * BorrowersApply entity. @author MyEclipse Persistence Tools
 */
/**
 * @author Administrator
 * 
 */
@Entity
@Table(name = "borrowers_apply")
public class BorrowersApply implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	/** 主键 */
	private Long id;
	/** 用户基本信息 */
	private Userbasicsinfo userbasicsinfo;
	/** 申请时间 */
	private String time;
	/** 申请金额 */
	private Double money;
	/**
	 * 申请类型
	 */
	private Integer loanType;
	/** 状态[0:未提交，1审核中,2:已通过,3:未通过] */
	private Integer status;
	/** 0 */
	private Integer state;
	/** 融资周期 */
	private Integer borrowmonth;
	/** 融资用途 */
	private String behoof;
	/** 企业名称 */
	private String corporatename;
	/** 手机号码 */
	private String telphone;

	private Adminuser adminuser_idborr;

	// Constructors

	/** default constructor */
	public BorrowersApply() {
	}

	/** minimal constructor */
	public BorrowersApply(Long id, Userbasicsinfo userbasicsinfo, String time,
			Double money, Integer status, String refunway) {
		this.id = id;
		this.userbasicsinfo = userbasicsinfo;
		this.time = time;
		this.money = money;
		this.status = status;
	}

	/** full constructor */

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BorrowersApply(Long id, Userbasicsinfo userbasicsinfo, String time,
			Double money, Integer status, Integer state, Integer borrowmonth,
			String behoof, String corporatename, String telphone,
			Adminuser adminuser_idborr) {
		super();
		this.id = id;
		this.userbasicsinfo = userbasicsinfo;
		this.time = time;
		this.money = money;
		this.status = status;
		this.state = state;
		this.borrowmonth = borrowmonth;
		this.behoof = behoof;
		this.corporatename = corporatename;
		this.telphone = telphone;
		this.adminuser_idborr = adminuser_idborr;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	public Userbasicsinfo getUserbasicsinfo() {
		return userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	@Column(name = "telphone", length = 20)
	public String getTelphone() {
		return this.telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	@Column(name = "time", nullable = false, length = 20)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Column(name = "money", nullable = false, precision = 20, scale = 4)
	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	@Column(name = "status", nullable = false)
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "state")
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Column(name = "borrowmonth")
	public Integer getBorrowmonth() {
		return this.borrowmonth;
	}

	public void setBorrowmonth(Integer borrowmonth) {
		this.borrowmonth = borrowmonth;
	}

	@Column(name = "behoof", length = 500)
	public String getBehoof() {
		return this.behoof;
	}

	public void setBehoof(String behoof) {
		this.behoof = behoof;
	}

	@Column(name = "corporatename", length = 255)
	public String getCorporatename() {
		return corporatename;
	}

	public void setCorporatename(String corporatename) {
		this.corporatename = corporatename;
	}

	@ManyToOne()
	@JoinColumn(name = "adminuser_idborr")
	public Adminuser getAdminuser_idborr() {
		return adminuser_idborr;
	}

	public void setAdminuser_idborr(Adminuser adminuser_idborr) {
		this.adminuser_idborr = adminuser_idborr;
	}

	public Integer getLoanType() {
		return loanType;
	}

	public void setLoanType(Integer loanType) {
		this.loanType = loanType;
	}

}