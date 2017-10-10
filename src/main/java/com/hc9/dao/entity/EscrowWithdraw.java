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
 * 第三方担保提现记录
 * @author Administrator
 *
 */
@Entity
@Table(name = "escrow_withdraw")
public class EscrowWithdraw implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	/**
     * 提现平台手续费
     */
    private Double mer_fee;
    
    /**
     * 提现宝付手续费
     */
    private Double fee;
    
    /**
     * 提现返回消息
     */
    private String msg;
    
    /**
     * 提现返回状态值-1失败 1成功 0-待确认  5-转账处理中
     */
    private int state;
    
    /**
     * 手续支付方式
     */
    private int fee_taken_on;
    /**
     * 实际提现金额
     */
    private Double withdrawAmount;
    
    /**
     * 申请提现金额
     */
    private Double amount;
    
    /**
     * applytime
     */
    private String applytime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 提现时间
     */
    private String time;
    /**
     * IPS提现订单号
     */
    private String pIpsBillNo;
    
    
    private Escrow escrow;


	public EscrowWithdraw() {
	}
	

	public EscrowWithdraw(Long id, Double mer_fee, Double fee, String msg,
			int state, int fee_taken_on, Double withdrawAmount, Double amount,
			String applytime, String remark, String time, String pIpsBillNo,
			Escrow escrow) {
		super();
		this.id = id;
		this.mer_fee = mer_fee;
		this.fee = fee;
		this.msg = msg;
		this.state = state;
		this.fee_taken_on = fee_taken_on;
		this.withdrawAmount = withdrawAmount;
		this.amount = amount;
		this.applytime = applytime;
		this.remark = remark;
		this.time = time;
		this.pIpsBillNo = pIpsBillNo;
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

	@Column(name = "mer_fee")
	public Double getMer_fee() {
		return mer_fee;
	}


	public void setMer_fee(Double mer_fee) {
		this.mer_fee = mer_fee;
	}

	@Column(name = "fee")
	public Double getFee() {
		return fee;
	}


	public void setFee(Double fee) {
		this.fee = fee;
	}

	@Column(name = "msg")
	public String getMsg() {
		return msg;
	}


	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Column(name = "state")
	public int getState() {
		return state;
	}


	public void setState(int state) {
		this.state = state;
	}

	@Column(name = "fee_taken_on")
	public int getFee_taken_on() {
		return fee_taken_on;
	}


	public void setFee_taken_on(int fee_taken_on) {
		this.fee_taken_on = fee_taken_on;
	}

	@Column(name = "withdrawAmount")
	public Double getWithdrawAmount() {
		return withdrawAmount;
	}


	public void setWithdrawAmount(Double withdrawAmount) {
		this.withdrawAmount = withdrawAmount;
	}

	@Column(name = "amount")
	public Double getAmount() {
		return amount;
	}


	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "applytime")
	public String getApplytime() {
		return applytime;
	}


	public void setApplytime(String applytime) {
		this.applytime = applytime;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "time")
	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}

	@Column(name = "pIpsBillNo")
	public String getpIpsBillNo() {
		return pIpsBillNo;
	}


	public void setpIpsBillNo(String pIpsBillNo) {
		this.pIpsBillNo = pIpsBillNo;
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
