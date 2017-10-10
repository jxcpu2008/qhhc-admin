package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * AttachmentType entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "attachment_type")
public class AttachmentType implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private Long id;
	private String attachmentName;
	private List<Attachment> attachments = new ArrayList<Attachment>(0);

	// Constructors

	/** default constructor */
	public AttachmentType() {
	}

	/** minimal constructor */
	public AttachmentType(Long id) {
		this.id = id;
	}

	/** full constructor */
	public AttachmentType(Long id, String attachmentName, List<Attachment> attachments) {
		this.id = id;
		this.attachmentName = attachmentName;
		this.attachments = attachments;
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

	@Column(name = "attachment_name", length = 50)
	public String getAttachmentName() {
		return this.attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "attachmentType")
	public List<Attachment> getAttachments() {
		return this.attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

}