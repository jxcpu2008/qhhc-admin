package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * Title:Generalizemoney
 * </p>
 * <p>
 * Description: 推广奖金信息表
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author frank
 *         <p>
 *         date 2015年3月29日
 *         </p>
 */
@Entity
@Table(name = "generalizemoney")
public class Generalizemoney implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private Long id;
	
	/**
	 * 被推荐人
	 */
	private Long referedUserid;
	
	/**
	 * 推荐人
	 */
	private Long referUserid;
	
	/**
	 * 被推广人投资金额
	 */
	private Double tenderMoney;
	
	/**
	 * 被推广人投资时间
	 */
	private String tenderTime;
	
	/**
	 * 推广人应得佣金
	 */
	private Double bonuses;
	
	/**
	 * 佣金发放状态
	 */
	private Integer releaseStatus;
	
	/**
	 * 佣金发放时间
	 */
	private String releaseTime;
	/**
	 * Loanreocrd  id
	 */
	private Long loanrecordId;
	
	/**
	 * 投资订单号
	 */
	private String orderNo;
	
	/**
	 * 转帐订单号
	 */
	private String transOrderNo;
	
	/**
	 * 奖金类型：1理财经理自己投的，2被推荐人产生的，6理财师
	 */
	private Integer bonuType;
	
	/**
	 * 实收佣金
	 */
	private  Double paidBonuses;
	
	/***
	 * 支付状态  0-待确认 1-已支付  -1支付失败
	 */
	private Integer payState;
	
	// Constructors

	/** default constructor */
	public Generalizemoney() {
	}

	/** full constructor */
	public Generalizemoney(Long id, Long referedUserid, Long referUserid,
			Double tenderMoney, String tenderTime, Double bonuses,
			Integer releaseStatus, String releaseTime, Long loanrecordId,
			String orderNo, String transOrderNo, Integer bonuType) {
		this.referedUserid = referedUserid;
		this.referUserid = referUserid;
		this.tenderMoney = tenderMoney;
		this.tenderTime = tenderTime;
		this.bonuses = bonuses;
		this.releaseStatus = releaseStatus;
		this.releaseTime = releaseTime;
		this.loanrecordId = loanrecordId;
		this.orderNo = orderNo;
		this.transOrderNo = transOrderNo;
		this.bonuType = bonuType;
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
	
	/**
	 * 被推荐人
	 * @return
	 */
	@Column(name = "refered_userid")
	public Long getReferedUserid() {
		return this.referedUserid;
	}
	/**
	 * 被推荐人
	 * @return
	 */
	public void setReferedUserid(Long referedUserid) {
		this.referedUserid = referedUserid;
	}
	/**
	 * 推广人
	 * @return
	 */
	@Column(name = "refer_userid")
	public Long getReferUserid() {
		return this.referUserid;
	}
	/**
	 * 推广人
	 * @return
	 */
	public void setReferUserid(Long referUserid) {
		this.referUserid = referUserid;
	}
	/**
	 * 被推广人投资金额
	 * @return
	 */
	@Column(name = "tender_money", precision = 18, scale = 4)
	public Double getTenderMoney() {
		return this.tenderMoney;
	}
	/**
	 * 被推广人投资金额
	 * @return
	 */
	public void setTenderMoney(Double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}
	/**
	 * 推广人投资日期
	 * @return
	 */
	@Column(name = "tender_time", length = 32)
	public String getTenderTime() {
		return this.tenderTime;
	}
	/**
	 * 推广人投资日期
	 * @return
	 */
	public void setTenderTime(String tenderTime) {
		this.tenderTime = tenderTime;
	}
	/**
	 * 奖金
	 * @return
	 */
	@Column(name = "bonuses", precision = 18, scale = 4)
	public Double getBonuses() {
		return this.bonuses;
	}
	/**
	 * 奖金
	 * @return
	 */
	public void setBonuses(Double bonuses) {
		this.bonuses = bonuses;
	}
	/**
	 * 佣金支付状态-1,失败，0未支付，1以支付
	 * @return
	 */
	@Column(name = "release_status")
	public Integer getReleaseStatus() {
		return this.releaseStatus;
	}
	/**
	 * 佣金支付状态-1,失败，0未支付，1以支付
	 * @param releaseStatus
	 */
	public void setReleaseStatus(Integer releaseStatus) {
		this.releaseStatus = releaseStatus;
	}
	/**
	 * 佣金发放时间
	 * @return
	 */
	@Column(name = "release_time", length = 32)
	public String getReleaseTime() {
		return this.releaseTime;
	}
	/**
	 * 佣金发放时间
	 * @param releaseTime
	 */
	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}
	/**
	 * loanrecord_id
	 * @return
	 */
	@Column(name = "loanrecord_id")
	public Long getLoanrecordId() {
		return this.loanrecordId;
	}

	public void setLoanrecordId(Long loanrecordId) {
		this.loanrecordId = loanrecordId;
	}
	/**
	 * 投资订单号order_no
	 * @return
	 */
	@Column(name = "order_no", length = 20)
	public String getOrderNo() {
		return this.orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * 转帐订单号
	 * @return
	 */
	@Column(name = "trans_order_no", length = 20)
	public String getTransOrderNo() {
		return this.transOrderNo;
	}

	public void setTransOrderNo(String transOrderNo) {
		this.transOrderNo = transOrderNo;
	}
	/**
	 * 奖励类型
	 * @return
	 */
	@Column(name = "bonu_type")
	public Integer getBonuType() {
		return this.bonuType;
	}

	public void setBonuType(Integer bonuType) {
		this.bonuType = bonuType;
	}

	@Column(name = "paid_bonuses")
	public Double getPaidBonuses() {
		return paidBonuses;
	}

	public void setPaidBonuses(Double paidBonuses) {
		this.paidBonuses = paidBonuses;
	}

	@Column(name = "pay_state")
	public Integer getPayState() {
		return payState;
	}
	
	public void setPayState(Integer payState) {
		this.payState = payState;
	}
	
	
	
}