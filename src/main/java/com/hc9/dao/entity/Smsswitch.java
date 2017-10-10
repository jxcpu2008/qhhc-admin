package com.hc9.dao.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "smsswitch")
public class Smsswitch implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	/**
	 * 触发选项：1.亿美，2梦网，3互亿
	 */
	private Integer triger;
	/**
	 * 营销选项：1.亿美，2梦网
	 */
	private Integer marketing;


	// Constructors

	/** default constructor */
	public Smsswitch() {
	}

	public Smsswitch(Integer id, Integer triger, Integer marketing) {
		this.id = id;
		this.triger = triger;
		this.marketing = marketing;
	}

	// Property accessors
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "triger", nullable = false)
	public Integer getTriger() {
		return this.triger;
	}

	public void setTriger(Integer triger) {
		this.triger = triger;
	}

	@Column(name = "marketing", nullable = false)
	public Integer getMarketing() {
		return this.marketing;
	}

	public void setMarketing(Integer marketing) {
		this.marketing = marketing;
	}

}