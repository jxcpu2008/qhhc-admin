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

/**
 * LoansignAttach entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "loansign_attach")
public class LoansignAttach implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Loansign loansign;
	private String originalName;
	private String attachmentName;
	private Integer attachmentType;
	private String uploadTime;
	private Long adminuserId;

	// Constructors

	/** default constructor */
	public LoansignAttach() {
	}

	/** full constructor */
	public LoansignAttach(Loansign loansign, String originalName,
			String attachmentName, Integer attachmentType, String uploadTime,
			Long adminuserId) {
		this.loansign = loansign;
		this.originalName = originalName;
		this.attachmentName = attachmentName;
		this.attachmentType = attachmentType;
		this.uploadTime = uploadTime;
		this.adminuserId = adminuserId;
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
	@JoinColumn(name = "loansign_id")
	public Loansign getLoansign() {
		return this.loansign;
	}

	public void setLoansign(Loansign loansign) {
		this.loansign = loansign;
	}

	@Column(name = "originalName")
	public String getOriginalName() {
		return this.originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	@Column(name = "attachmentName")
	public String getAttachmentName() {
		return this.attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	@Column(name = "attachmentType")
	public Integer getAttachmentType() {
		return this.attachmentType;
	}

	public void setAttachmentType(Integer attachmentType) {
		this.attachmentType = attachmentType;
	}

	@Column(name = "uploadTime", length = 30)
	public String getUploadTime() {
		return this.uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	@Column(name = "adminuser_id")
	public Long getAdminuserId() {
		return this.adminuserId;
	}

	public void setAdminuserId(Long adminuserId) {
		this.adminuserId = adminuserId;
	}

}