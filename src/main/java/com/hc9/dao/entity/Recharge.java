package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Recharge
 */
@Entity
@Table(name = "recharge")
public class Recharge implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键id
	 */
	private Long id;
	/**
	 * 用户基础信息
	 */
	private Userbasicsinfo userbasicsinfo;
	/**
	 * 充值时间
	 */
	private String time;
	/**
	 * 充值金额
	 */
	private Double rechargeAmount;
	/**
	 * 是否充值成功(0 未充值 1充值成功 -1充值失败)
	 */
	private Integer status;
	// private Integer tpstatus;
	/**
	 * 充值流水号
	 */
	private String orderNum;

	private String code;

	/***
	 * 宝付收取手续费
	 */
	private Double fee;

	/** 费用承担方(宝付收取的费用) */
	private int fee_taken_on;

	/** 其它信息 */
	private String additional_info;

	/***
	 * 商户收取手续费
	 */
	private Double mer_fee;

	/**
	 * 实际到账金额
	 */
	private Double incash_money;

	/***
	 * 宝付充值成功时间 年月日十分秒
	 */
	private String succTime;

	// Constructors

	/** default constructor */
	public Recharge() {
	}

	// Property accessors
	/**
	 * 
	 * @return Long
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	/**
	 * 
	 * @param id
	 *            主键id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * @return Userbasicsinfo
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	public Userbasicsinfo getUserbasicsinfo() {
		return userbasicsinfo;
	}

	/**
	 * 
	 * @param userbasicsinfo
	 *            用户基本信息
	 */
	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	/**
	 * 
	 * @return String
	 */
	@Column(name = "time", length = 32)
	public String getTime() {
		return this.time;
	}

	/**
	 * 
	 * @param time
	 *            充值时间
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * 
	 * @return Double
	 */
	@Column(name = "rechargeAmount", precision = 18, scale = 4)
	public Double getRechargeAmount() {
		return this.rechargeAmount;
	}

	/**
	 * 
	 * @param rechargeAmount
	 *            充值金额
	 */
	public void setRechargeAmount(Double rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	/**
	 * 
	 * @return Integer
	 */
	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	/**
	 * 
	 * @param status
	 *            状态
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	// @Column(name = "tpstatus")
	// public Integer getTpstatus() {
	// return this.tpstatus;
	// }
	//
	// public void setTpstatus(Integer tpstatus) {
	// this.tpstatus = tpstatus;
	// }

	@Column(name = "code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "fee")
	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	@Column(name = "feetakenon")
	public int getFee_taken_on() {
		return fee_taken_on;
	}

	public void setFee_taken_on(int fee_taken_on) {
		this.fee_taken_on = fee_taken_on;
	}

	@Column(name = "additional_info")
	public String getAdditional_info() {
		return additional_info;
	}

	public void setAdditional_info(String additional_info) {
		this.additional_info = additional_info;
	}

	@Column(name = "merfee")
	public Double getMer_fee() {
		return mer_fee;
	}

	public void setMer_fee(Double mer_fee) {
		this.mer_fee = mer_fee;
	}

	@Column(name = "reAccount")
	public Double getIncash_money() {
		return incash_money;
	}

	public void setIncash_money(Double incash_money) {
		this.incash_money = incash_money;
	}

	/**
	 * 
	 * @return String
	 */
	@Column(name = "orderNum")
	public String getOrderNum() {
		return this.orderNum;
	}

	/**
	 * 
	 * @param orderNum
	 *            订单号
	 */
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	@Column(name = "succ_time")
	public String getSuccTime() {
		return succTime;
	}

	public void setSuccTime(String succTime) {
		this.succTime = succTime;
	}

}