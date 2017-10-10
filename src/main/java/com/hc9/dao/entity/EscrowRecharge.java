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

import org.codehaus.jackson.annotate.JsonIgnore;

/***
 * 第三方担保充值记录
 * @author LKL
 *
 */
@Entity
@Table(name = "escrow_recharge")
public class EscrowRecharge implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	/**充值时间*/
	private String time;
	/**宝付收取手续费*/
	private Double fee;
	/**充值金额*/
	private Double rechargeAmount;
	/**宝付收取手续费*/
	private Double mer_fee;
	/**费用承担方(宝付收取的费用) */
	private int fee_taken_on; 
	/**其它信息 */
	private String additional_info;  
	/** 实际到账金额*/
	private Double incash_money;
	/**宝付充值成功时间   年月日十分秒*/
	private String succTime;
	/**充值流水号*/
    private String orderNum;
    /**是否充值成功(0 未充值 1充值成功 -1充值失败)*/
    private Integer status;
    /**第三方担保*/
    private Escrow escrow;
    
	public EscrowRecharge() {
	}
	
	

	public EscrowRecharge(Long id, String time, Double fee,
			Double rechargeAmount, Double mer_fee, int fee_taken_on,
			String additional_info, Double incash_money, String succTime,
			String orderNum, Integer status, Escrow escrow) {
		super();
		this.id = id;
		this.time = time;
		this.fee = fee;
		this.rechargeAmount = rechargeAmount;
		this.mer_fee = mer_fee;
		this.fee_taken_on = fee_taken_on;
		this.additional_info = additional_info;
		this.incash_money = incash_money;
		this.succTime = succTime;
		this.orderNum = orderNum;
		this.status = status;
		this.escrow = escrow;
	}


	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "time")
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	@Column(name = "fee")
	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}
	@Column(name = "rechargeAmount")
	public Double getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(Double rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}
	@Column(name = "merfee")
	public Double getMer_fee() {
		return mer_fee;
	}

	public void setMer_fee(Double mer_fee) {
		this.mer_fee = mer_fee;
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
	@Column(name = "reAccount")
	public Double getIncash_money() {
		return incash_money;
	}

	public void setIncash_money(Double incash_money) {
		this.incash_money = incash_money;
	}
	@Column(name = "succ_time")
	public String getSuccTime() {
		return succTime;
	}

	public void setSuccTime(String succTime) {
		this.succTime = succTime;
	}
	@Column(name = "orderNum")
	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "escrow_id")
	@JsonIgnore
	public Escrow getEscrow() {
		return escrow;
	}

	public void setEscrow(Escrow escrow) {
		this.escrow = escrow;
	} 
	
	
    
    
}
