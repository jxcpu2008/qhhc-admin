package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 支付记录
 */
@Entity
@Table(name = "paylog")
public class Paylog implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	/**
	 * 发送时间
	 */
	private String sendTime;
	/**
	 * 更新时间
	 */
	private String updateTime;
	/**
	 * 报文
	 */
	private String message;
	/**
	 * 操作动作
	 */
	private String action;
	/**
	 * 状态  0-发送成功  1-宝付确实成功
	 */
	private Integer status;
	/**
	 * 用户
	 */
	private Long userId;
	
    /**
     * 店铺Id
     */
	private Long shopId;
	
	/***
	 * 项目ID
	 */
	private Long loansignId;
	
	/***
	 * 应收金额
	 */
	private Double amount;
	
	/***
	 * 实收金额
	 */
	private Double payAmount;
	
	/***
	 * 平台手续费
	 */
	private Double fee;
	
	/***
	 * 宝付平台收取的手续费
	 */
	private Double payFee;
	
	/**
	 * 订单号
	 */
	private String orderSn;
	/**
	 * 备注
	 */
	private String remark;


	// Constructors

	/** default constructor */
	public Paylog() {
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

	@Column(name = "send_time", length = 50)
	public String getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	
	@Column(name = "update_time", length = 50)
	public String getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "message", length = 65535)
	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Column(name = "action",length = 50)
	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Column(name = "pay_status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "order_sn", length = 50)
	public String getOrderSn() {
		return this.orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}
	@Column(name = "remark", length = 50)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "shop_id")
	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	@Column(name = "amount")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	@Column(name = "pay_amount")
	public Double getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}

	@Column(name = "fee")
	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	@Column(name = "pay_fee")
	public Double getPayFee() {
		return payFee;
	}

	public void setPayFee(Double payFee) {
		this.payFee = payFee;
	}

	@Column(name = "loansign_id")
	public Long getLoansignId() {
		return loansignId;
	}

	public void setLoansignId(Long loansignId) {
		this.loansignId = loansignId;
	}
	
	
	
	
}