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
 * <p>
 * Title:Messagesetting
 * </p>
 * <p>
 * Description: 消息设置实体类
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>

 */
@Entity
@Table(name = "messagesetting")
public class Messagesetting implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键 */
    private Long id;
    /** 用户基本信息 */
    private Userbasicsinfo userbasicsinfo;
    /** 消息类型 */
    private Messagetype messagetype;
    /** 是否是系统消息 */
    private Boolean sysIsEnable;
    /** 是否通过邮件发送 */
    private Boolean emailIsEnable;
    /** 是否通过短信发送 */
    private Boolean smsIsEnable;
    /** 备注 */
    private String expandGuid;

    // Constructors

    /** default constructor */
    public Messagesetting() {
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param userbasicsinfo
     *            用户基本信息
     * @param messagetype
     *            消息类型
     * @param sysIsEnable
     *            是否是系统消息
     * @param emailIsEnable
     *            是否通过邮件发送
     * @param smsIsEnable
     *            是否通过短信发送
     * @param expandGuid
     *            备注
     */
    public Messagesetting(Userbasicsinfo userbasicsinfo,
            Messagetype messagetype, Boolean sysIsEnable,
            Boolean emailIsEnable, Boolean smsIsEnable, String expandGuid) {
        this.userbasicsinfo = userbasicsinfo;
        this.messagetype = messagetype;
        this.sysIsEnable = sysIsEnable;
        this.emailIsEnable = emailIsEnable;
        this.smsIsEnable = smsIsEnable;
        this.expandGuid = expandGuid;
    }

    // Property accessors
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
     * Title: getUserbasicsinfo
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return userbasicsinfo
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public Userbasicsinfo getUserbasicsinfo() {
        return this.userbasicsinfo;
    }

    /**
     * <p>
     * Title: setUserbasicsinfo
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param userbasicsinfo
     *            userbasicsinfo
     */
    public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }

    /**
     * <p>
     * Title: getMessagetype
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return messagetype
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "messagetype_id")
    public Messagetype getMessagetype() {
        return this.messagetype;
    }

    /**
     * <p>
     * Title: setMessagetype
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param messagetype
     *            messagetype
     */
    public void setMessagetype(Messagetype messagetype) {
        this.messagetype = messagetype;
    }

    /**
     * <p>
     * Title: getSysIsEnable
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return sysIsEnable
     */
    @Column(name = "sysIsEnable")
    public Boolean getSysIsEnable() {
        return this.sysIsEnable;
    }

    /**
     * <p>
     * Title: setSysIsEnable
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param sysIsEnable
     *            sysIsEnable
     */
    public void setSysIsEnable(Boolean sysIsEnable) {
        this.sysIsEnable = sysIsEnable;
    }

    /**
     * <p>
     * Title: getEmailIsEnable
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return emailIsEnable
     */
    @Column(name = "emailIsEnable")
    public Boolean getEmailIsEnable() {
        return this.emailIsEnable;
    }

    /**
     * <p>
     * Title: setEmailIsEnable
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param emailIsEnable
     *            emailIsEnable
     */
    public void setEmailIsEnable(Boolean emailIsEnable) {
        this.emailIsEnable = emailIsEnable;
    }

    /**
     * <p>
     * Title: getSmsIsEnable
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return smsIsEnable
     */
    @Column(name = "smsIsEnable")
    public Boolean getSmsIsEnable() {
        return this.smsIsEnable;
    }

    /**
     * <p>
     * Title: setSmsIsEnable
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param smsIsEnable
     *            smsIsEnable
     */
    public void setSmsIsEnable(Boolean smsIsEnable) {
        this.smsIsEnable = smsIsEnable;
    }

    /**
     * <p>
     * Title: getExpandGuid
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return expandGuid
     */
    @Column(name = "expand_guid")
    public String getExpandGuid() {
        return this.expandGuid;
    }

    /**
     * <p>
     * Title: setExpandGuid
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param expandGuid
     *            expandGuid
     */
    public void setExpandGuid(String expandGuid) {
        this.expandGuid = expandGuid;
    }

}