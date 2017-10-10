package com.hc9.dao.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Liquidation entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "liquidation")
public class Liquidation implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Userbasicsinfo userbasicsinfo;
	private Escrow escrow;
	private Loansign loansign;
	private String orderId;
	private BigDecimal amount;
	private BigDecimal fee;
	private Date reqTime;
	private Integer liquidationState;

	// Constructors

	/** default constructor */
	public Liquidation() {
	}

	/** full constructor */
	public Liquidation(Userbasicsinfo userbasicsinfo, Escrow escrow,
			Loansign loansign, String orderId, BigDecimal amount,
			BigDecimal fee, Date reqTime, Integer liquidationState) {
		this.userbasicsinfo = userbasicsinfo;
		this.escrow = escrow;
		this.loansign = loansign;
		this.orderId = orderId;
		this.amount = amount;
		this.fee = fee;
		this.reqTime = reqTime;
		this.liquidationState = liquidationState;
	}

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payeeUserId")
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payerUserId")
	public Escrow getEscrow() {
		return this.escrow;
	}

	public void setEscrow(Escrow escrow) {
		this.escrow = escrow;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loanId")
	public Loansign getLoansign() {
		return this.loansign;
	}

	public void setLoansign(Loansign loansign) {
		this.loansign = loansign;
	}

	@Column(name = "orderId", length = 20)
	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Column(name = "amount", precision = 30, scale = 0)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "fee", precision = 30, scale = 0)
	public BigDecimal getFee() {
		return this.fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "reqTime", length = 10)
	public Date getReqTime() {
		return this.reqTime;
	}

	public void setReqTime(Date reqTime) {
		this.reqTime = reqTime;
	}

	@Column(name = "liquidationState")
	public Integer getLiquidationState() {
		return this.liquidationState;
	}

	public void setLiquidationState(Integer liquidationState) {
		this.liquidationState = liquidationState;
	}

}