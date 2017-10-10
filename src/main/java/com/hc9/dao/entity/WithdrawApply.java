package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name="withdraw_apply")
public class WithdrawApply {
	/**ID*/
	private Long id;
	
	/**余额*/
	private double cash;
	
	/**申请提现的数目*/
	private double applyNum;
	
	/**
	 * 申请结果
	 * 0不通过，1通过 2-已提现 
	 * */
	private int result;
	
	/**
	 * 申请状态
	 * 0未审核，1已审核 2-取消订单
	 */
	private int status;
	
	/**申请时间*/
	private String applyTime;
	
	/**审核时间*/
	private String answerTime;
	
	/**申请人*/
	private Userbasicsinfo userbasicsinfo;
	
	/***
	 * 提现手续费 元/笔
	 */
	private Double fee;
	
	
	/**
	 * 审核人
	 */
	private String  adminuser;
	
	
	
	
	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "cash")
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	
	@Column(name = "apply_num")
	public double getApplyNum() {
		return applyNum;
	}
	public void setApplyNum(double applyNum) {
		this.applyNum = applyNum;
	}
	
	@Column(name = "result")
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	
	@Column(name = "status")
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userbasic_id")
	@JsonIgnore
	public Userbasicsinfo getUserbasicsinfo() {
		return userbasicsinfo;
	}
	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}
	
	@Column(name = "apply_time")
	public String getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}
	
	@Column(name = "answer_time")
	public String getAnswerTime() {
		return answerTime;
	}
	public void setAnswerTime(String answerTime) {
		this.answerTime = answerTime;
	}
	
	@Column(name = "fee")
	public Double getFee() {
		return fee;
	}
	public void setFee(Double fee) {
		this.fee = fee;
	}
	
	
	@Column(name = "admin_userId")
	public String getAdminuser() {
		return adminuser;
	}
	public void setAdminuser(String adminuser) {
		this.adminuser = adminuser;
	}
	

	
	
}
