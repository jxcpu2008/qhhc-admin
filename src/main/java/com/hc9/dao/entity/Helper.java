package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Link
 */
@Entity
@Table(name = "helper")
public class Helper implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	/**
	 * 编号
	 */
	private Long id;

	private Integer level_Id;

	private String levelOne;

	private String secondary;

	private String remark;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "level_Id", length = 10)
	public Integer getLevel_Id() {
		return level_Id;
	}

	public void setLevel_Id(Integer level_Id) {
		this.level_Id = level_Id;
	}

	@Column(name = "levelOne", length = 32)
	public String getLevelOne() {
		return levelOne;
	}

	public void setLevelOne(String levelOne) {
		this.levelOne = levelOne;
	}

	@Column(name = "secondary", length = 32)
	public String getSecondary() {
		return secondary;
	}

	public void setSecondary(String secondary) {
		this.secondary = secondary;
	}

	@Column(name = "remark", length = 32)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}