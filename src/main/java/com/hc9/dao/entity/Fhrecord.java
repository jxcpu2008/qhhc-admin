package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Fhrecord entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "fhrecord")
public class Fhrecord implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Double amount;
	private Double money;
	private String fhTime;
	private Long userId;
	private Long projectId;
	private Double ratio;

	// Constructors

	/** default constructor */
	public Fhrecord() {
	}

	/** full constructor */
	public Fhrecord(Double amount, Double money, String fhTime, Long userId,
			Long projectId, Double ratio) {
		this.amount = amount;
		this.money = money;
		this.fhTime = fhTime;
		this.userId = userId;
		this.projectId = projectId;
		this.ratio = ratio;
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

	@Column(name = "amount", precision = 20, scale = 4)
	public Double getAmount() {
		return this.amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "money", precision = 20, scale = 4)
	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	@Column(name = "fhTime", length = 20)
	public String getFhTime() {
		return this.fhTime;
	}

	public void setFhTime(String fhTime) {
		this.fhTime = fhTime;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "project_id")
	public Long getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Column(name = "ratio", precision = 10, scale = 4)
	public Double getRatio() {
		return this.ratio;
	}

	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}

}