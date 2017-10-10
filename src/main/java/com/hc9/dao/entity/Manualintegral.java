package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Manualintegral entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "manualintegral")
@JsonIgnoreProperties(value = {"userbasicsinfo"})
public class Manualintegral implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Userbasicsinfo userbasicsinfo;
	private Integer amountPoints;
	private Integer bankWaterPoints;
	private String ckVaule;
	private Integer creditCardPoints;
	private Integer houseCardPoints;
	private Integer salesContractInvoicePoints;
	private Integer socialPoints;
	private Integer tgPoints;

	// Constructors

	/** default constructor */
	public Manualintegral() {
	}

	/** full constructor */
	public Manualintegral(Userbasicsinfo userbasicsinfo, Integer amountPoints,
			Integer bankWaterPoints, String ckVaule, Integer creditCardPoints,
			Integer houseCardPoints, Integer salesContractInvoicePoints,
			Integer socialPoints, Integer tgPoints) {
		this.userbasicsinfo = userbasicsinfo;
		this.amountPoints = amountPoints;
		this.bankWaterPoints = bankWaterPoints;
		this.ckVaule = ckVaule;
		this.creditCardPoints = creditCardPoints;
		this.houseCardPoints = houseCardPoints;
		this.salesContractInvoicePoints = salesContractInvoicePoints;
		this.socialPoints = socialPoints;
		this.tgPoints = tgPoints;
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
	@JoinColumn(name = "user_id")
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	@Column(name = "amountPoints")
	public Integer getAmountPoints() {
		return this.amountPoints;
	}

	public void setAmountPoints(Integer amountPoints) {
		this.amountPoints = amountPoints;
	}

	@Column(name = "bankWaterPoints")
	public Integer getBankWaterPoints() {
		return this.bankWaterPoints;
	}

	public void setBankWaterPoints(Integer bankWaterPoints) {
		this.bankWaterPoints = bankWaterPoints;
	}

	@Column(name = "ckVaule")
	public String getCkVaule() {
		return this.ckVaule;
	}

	public void setCkVaule(String ckVaule) {
		this.ckVaule = ckVaule;
	}

	@Column(name = "creditCardPoints")
	public Integer getCreditCardPoints() {
		return this.creditCardPoints;
	}

	public void setCreditCardPoints(Integer creditCardPoints) {
		this.creditCardPoints = creditCardPoints;
	}

	@Column(name = "houseCardPoints")
	public Integer getHouseCardPoints() {
		return this.houseCardPoints;
	}

	public void setHouseCardPoints(Integer houseCardPoints) {
		this.houseCardPoints = houseCardPoints;
	}

	@Column(name = "salesContractInvoicePoints")
	public Integer getSalesContractInvoicePoints() {
		return this.salesContractInvoicePoints;
	}

	public void setSalesContractInvoicePoints(Integer salesContractInvoicePoints) {
		this.salesContractInvoicePoints = salesContractInvoicePoints;
	}

	@Column(name = "socialPoints")
	public Integer getSocialPoints() {
		return this.socialPoints;
	}

	public void setSocialPoints(Integer socialPoints) {
		this.socialPoints = socialPoints;
	}

	@Column(name = "tgPoints")
	public Integer getTgPoints() {
		return this.tgPoints;
	}

	public void setTgPoints(Integer tgPoints) {
		this.tgPoints = tgPoints;
	}

}