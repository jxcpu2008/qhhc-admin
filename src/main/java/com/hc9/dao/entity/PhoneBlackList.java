package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/** 手机黑名单表对应实体类 */
@Entity
@Table(name = "phoneBlackList")
public class PhoneBlackList implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	/** 主键id，自增 */
	private Long id;
	
	/** 手机号码 */
	private String phone;
	
	/** 来源类型：1、注册； */
	private Integer sourceType;
	
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

	@Column(name = "phone")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "sourceType")
	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	@Column(name = "createTime")
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
