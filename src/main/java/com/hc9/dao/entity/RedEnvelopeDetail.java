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
 * 
 * 红包明细表
 *
 */
@Entity
@Table(name = "redenvelopedetail")
public class RedEnvelopeDetail implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */

	private Long id;

	/**
	 * 用户
	 */
	private Userbasicsinfo userbasicsinfo;
	/**
	 * 红包金额
	 */
	private Double money;
	/**
	 * 投资记录Id
	 */
	private Long loanrecord_id;
	/**
	 * 红包领取时间
	 */
	private String receiveTime;
	/**
	 * 红包使用时间
	 */
	private String consumeTime;
	/**
	 * 开始时间
	 */
	private String beginTime;
	/**
	 * 结束时间
	 */
	private String endTime;
	/**
	 * 是否使用：0、未使用，1、已使用；默认为0
	 */
	private Integer useFlag;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 类型：1、投资2、注册3、奖励4、抽奖
	 */
	private Integer sourceType;
	
	private Double lowestUseMoney=0d;
	

	public RedEnvelopeDetail() {
	}

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
	@JoinColumn(name = "userId", nullable = false)
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	@Column(name = "money")
	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	@Column(name = "loanrecord_id")
	public Long getLoanrecord_id() {
		return this.loanrecord_id;
	}

	public void setLoanrecord_id(Long loanrecord_id) {
		this.loanrecord_id = loanrecord_id;
	}

	@Column(name = "receiveTime")
	public String getReceiveTime() {
		return this.receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	@Column(name = "consumeTime")
	public String getConsumeTime() {
		return this.consumeTime;
	}

	public void setConsumeTime(String consumeTime) {
		this.consumeTime = consumeTime;
	}

	@Column(name = "beginTime")
	public String getBeginTime() {
		return this.beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	@Column(name = "endTime")
	public String getEndTime() {
		return this.endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Column(name = "useFlag")
	public Integer getUseFlag() {
		return useFlag;
	}

	public void setUseFlag(Integer useFlag) {
		this.useFlag = useFlag;
	}

	@Column(name = "createTime")
	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "sourceType")
	public Integer getSourceType() {
		return this.sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}
	@Column(name = "lowestUseMoney")
	public Double getLowestUseMoney() {
		return lowestUseMoney;
	}

	public void setLowestUseMoney(Double lowestUseMoney) {
		this.lowestUseMoney = lowestUseMoney;
	}
	
	

}