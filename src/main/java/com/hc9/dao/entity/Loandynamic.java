package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Loandynamic entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "loandynamic")
public class Loandynamic implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long userId;
	
	/**
	 * 标id
	 */
	private Long loanId;
	
	/**
	 * 内容
	 */
	private String context;
	
	/**
	 * 发布时间
	 */
	private String publishTime;
	
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 动态类型
	 */
	private Integer type;

	// Constructors

	/** default constructor */
	public Loandynamic() {
	}

	/** minimal constructor */
	public Loandynamic(Long id) {
		this.id = id;
	}

	/** full constructor */
	public Loandynamic(Long id, Long userId, Long loanId, String context,
			String publishTime, String title,Integer type) {
		this.id = id;
		this.userId = userId;
		this.loanId = loanId;
		this.context = context;
		this.publishTime = publishTime;
		this.title = title;
		this.type=type;
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
	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "loan_id")
	public Long getLoanId() {
		return this.loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	@Column(name = "context")
	public String getContext() {
		return this.context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Column(name = "publishTime", length = 50)
	public String getPublishTime() {
		return this.publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	@Column(name = "title", length = 300)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}