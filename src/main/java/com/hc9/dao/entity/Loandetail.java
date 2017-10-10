package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Projectdetail entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "loandetail")
public class Loandetail implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Integer userId;
	private Integer loanId;
	private String commitTime;
	private String type;
	private Double spend;
	private String use;
	private String credential;
	private String remark;

	// Constructors

	/** default constructor */
	public Loandetail() {
	}

	/** full constructor */
	public Loandetail(Integer userId, Integer loanId, String commitTime,
			String type, Double spend, String use, String credential,
			String remark) {
		this.userId = userId;
		this.loanId = loanId;
		this.commitTime = commitTime;
		this.type = type;
		this.spend = spend;
		this.use = use;
		this.credential = credential;
		this.remark = remark;
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

	@Column(name = "user_id")
	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Column(name = "loan_id")
	public Integer getLoanId() {
		return loanId;
	}

	public void setLoanId(Integer loanId) {
		this.loanId = loanId;
	}

	@Column(name = "commitTime", length = 20)
	public String getCommitTime() {
		return this.commitTime;
	}

	

	public void setCommitTime(String commitTime) {
		this.commitTime = commitTime;
	}

	@Column(name = "type", length = 20)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "spend", precision = 20, scale = 4)
	public Double getSpend() {
		return this.spend;
	}

	public void setSpend(Double spend) {
		this.spend = spend;
	}

	@Column(name = "purpose", length = 20)
	public String getUse() {
		return this.use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	@Column(name = "credential", length = 200)
	public String getCredential() {
		return this.credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	@Column(name = "remark", length = 20)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}