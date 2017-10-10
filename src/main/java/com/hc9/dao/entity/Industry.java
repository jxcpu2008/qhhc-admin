package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Industry entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "industry")
public class Industry implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String industryType;

	// Constructors

	/** default constructor */
	public Industry() {
	}

	public Industry(String name, String industryType) {
		this.name = name;
		this.industryType = industryType;
	}
	
	/** full constructor */
	public Industry(Integer id, String name, String industryType) {
		this.id=id;
		this.name = name;
		this.industryType = industryType;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", length = 128)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "industryType", length = 128)
	public String getIndustryType() {
		return this.industryType;
	}

	public void setIndustryType(String industryType) {
		this.industryType = industryType;
	}

}