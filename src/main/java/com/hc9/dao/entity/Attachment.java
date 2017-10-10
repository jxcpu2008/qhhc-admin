package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 附件表
 */
@Entity
@Table(name = "attachment")
public class Attachment implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键 */
    private Long id;
    /** 上传人 */
    private Adminuser adminuser;
    /** 标的编号 */
    private Loansign loansign;
    /** 附件原始名称 */
    private String originalName;
    /** 附件名称 */
    private String attachmentName;
    /** 附件类型 */
    private AttachmentType attachmentType;
    /** 上传时间 */
    private String uploadTime;

    private String etag;
    // Constructors

    /** default constructor */
    public Attachment() {
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param adminuser
     *            上传人
     * @param loansign
     *            标的编号
     * @param originalName
     *            附件原始名称
     * @param attachmentName
     *            附件名称
     * @param attachmentType
     *            附件类型
     * @param uploadTime
     *            上传时间
     */
    public Attachment(Adminuser adminuser, Loansign loansign,
            String originalName, String attachmentName, AttachmentType attachmentType,
            String uploadTime) {
        this.adminuser = adminuser;
        this.loansign = loansign;
        this.originalName = originalName;
        this.attachmentName = attachmentName;
        this.attachmentType = attachmentType;
        this.uploadTime = uploadTime;
    }

    /**
     * <p>
     * Title: getId
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * <p>
     * Title: setId
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * <p>
     * Title: getAdminuser
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return adminuser
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminuser_id")
    public Adminuser getAdminuser() {
        return this.adminuser;
    }

    /**
     * <p>
     * Title: setAdminuser
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param adminuser
     *            adminuser
     */
    public void setAdminuser(Adminuser adminuser) {
        this.adminuser = adminuser;
    }

    /**
     * <p>
     * Title: getLoansign
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return loansign
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loansign_id")
    public Loansign getLoansign() {
        return this.loansign;
    }

    /**
     * <p>
     * Title: setLoansign
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param loansign
     *            loansign
     */
    public void setLoansign(Loansign loansign) {
        this.loansign = loansign;
    }

    /**
     * <p>
     * Title: getOriginalName
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return originalName
     */
    @Column(name = "originalName")
    public String getOriginalName() {
        return this.originalName;
    }

    /**
     * <p>
     * Title: setOriginalName
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param originalName
     *            originalName
     */
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    /**
     * <p>
     * Title: getAttachmentName
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return attachmentName
     */
    @Column(name = "attachmentName")
    public String getAttachmentName() {
        return this.attachmentName;
    }

    /**
     * <p>
     * Title: setAttachmentName
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param attachmentName
     *            attachmentName
     */
    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }


    /**
     * <p>
     * Title: getUploadTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return uploadTime
     */
    @Column(name = "uploadTime", length = 30)
    public String getUploadTime() {
        return this.uploadTime;
    }

    /**
     * <p>
     * Title: setUploadTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param uploadTime
     *            uploadTime
     */
    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attachmentType")
	public AttachmentType getAttachmentType() {
		return this.attachmentType;
	}

	public void setAttachmentType(AttachmentType attachmentType) {
		this.attachmentType = attachmentType;
	}
	@Column(name = "etag", length = 30)
	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}
	
	
	
}