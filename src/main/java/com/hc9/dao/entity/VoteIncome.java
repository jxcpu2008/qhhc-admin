package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/** 胜利方投票人参与投票所得收益信息表 */
@Entity
@Table(name = "voteincome")
public class VoteIncome {
	/** 主键id，自增 */
	private Long id;
	
	/** 投票人用户id */
	private Long voterId;
	
	/** 活动期间所投项目id */
	private Long loanId;
	
	/** 投资记录id */
	private Long loanRecordId;
	
	/** 收入金额(所得利息收入) */
	private Double incomeMoney;
	
	/**  状态：1、已支付；0、未支付；（默认0） */
	private Integer status;
	
	/** 类型：1、七夕活动投票收益；2、加息券收益；（默认1） */
	private Integer type;
	
	/** 创建时间 */
	private String createTime;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "voterId", nullable = false)
	public Long getVoterId() {
		return voterId;
	}

	public void setVoterId(Long voterId) {
		this.voterId = voterId;
	}

	@Column(name = "loanId", nullable = false)
	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	@Column(name = "loanRecordId", nullable = false)
	public Long getLoanRecordId() {
		return loanRecordId;
	}

	public void setLoanRecordId(Long loanRecordId) {
		this.loanRecordId = loanRecordId;
	}

	@Column(name = "incomeMoney", nullable = false)
	public Double getIncomeMoney() {
		return incomeMoney;
	}

	public void setIncomeMoney(Double incomeMoney) {
		this.incomeMoney = incomeMoney;
	}

	@Column(name = "status", nullable = false)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "createTime", nullable = false)
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
