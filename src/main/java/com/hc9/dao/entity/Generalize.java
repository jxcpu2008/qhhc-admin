package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Generalize
 */
@Entity
@Table(name = "generalize")
public class Generalize implements java.io.Serializable {

	/**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 编号
     */
    private Long id;
    
    /**
     * 推广时间
     */
	private String adddate;
	
	/**
	 * 推广人编号
	 */
	private Long genuid;
	
	/**
	 * 被推广人编号
	 */
	private Userbasicsinfo byUser;
	
	/**
	 * 被推广人用户名
	 */
	private String uanme;
	/**
	 * 注册时填写的推广码
	 */
	private String code;
	

	/**
	 *推广状态：0待审核；1、关联成功（尚未注册宝付），2、审核通过（已注册宝付）；3、审核不通过；',
	 */
	private Integer state;
	
	/***
	 * 审核Id
	 */
	private Long auditId;
	
	/***
	 * 审核时间
	 */
	private String auditTime;
	/***
	 * 备注
	 */
	private String remark;

	/** default constructor */
	public Generalize() {
	}

	/**
	 * 构造方法
	 * @param adddate 推广时间
	 * @param genuid 推广人编号
	 * @param uid 被推广人编号
	 * @param uanme 被推广人用户名
	 */
	public Generalize(String adddate, Long genuid, String uanme) {
		this.adddate = adddate;
		this.genuid = genuid;
		this.uanme = uanme;
	}

	/**
	 * 编号
	 * @return 编号
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	/**
	 * 编号
	 * @param id 编号
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 推广时间
	 * @return 推广时间
	 */
	@Column(name = "adddate", length = 32)
	public String getAdddate() {
		return this.adddate;
	}

	/**
	 * 推广时间
	 * @param adddate 推广时间
	 */
	public void setAdddate(String adddate) {
		this.adddate = adddate;
	}

	/**
	 * 推广人编号
	 * @return 推广人编号
	 */
	@Column(name = "genuid")
	public Long getGenuid() {
		return this.genuid;
	}

	/**
	 * 推广人编号
	 * @param genuid 推广人编号
	 */
	public void setGenuid(Long genuid) {
		this.genuid = genuid;
	}

	/**
	 * 被推广人用户名
	 * @return 被推广人用户名
	 */
	@Column(name = "uanme", length = 32)
	public String getUanme() {
		return this.uanme;
	}

	/**
	 * 被推广人用户名
	 * @param uanme 被推广人用户名
	 */
	public void setUanme(String uanme) {
		this.uanme = uanme;
	}
	@Column(name = "state")
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uid")
	public Userbasicsinfo getByUser() {
		return byUser;
	}

	public void setByUser(Userbasicsinfo byUser) {
		this.byUser = byUser;
	}
	
	@Column(name = "code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "auditId")
	public Long getAuditId() {
		return auditId;
	}

	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}

	@Column(name = "auditTime")
	public String getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	

}