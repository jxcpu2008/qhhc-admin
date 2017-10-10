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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Validcodeinfo
 */
@Entity
@Table(name = "validcodeinfo")
@JsonIgnoreProperties(value = {"userbasicsinfo"})
public class Validcodeinfo implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 主键id
     */
    private Long id;
    /**
     * 用户基本信息
     */
    private Userbasicsinfo userbasicsinfo;
    /**
     * email再次发送时间
     */
    private Long emailagaintime;
    /**
     * 邮件验证码
     */
    private String emailcode; 
    /**
     * email发送超时时间
     */
    private Long emailovertime;
    /**
     * 短信验证码
     */
    private String smsCode;
    /**
     * 再次发送短信提示时间
     */
    private Long smsagainTime;
    
    /**
     * 发送验证码的手机号
     */
    private String smsPhone;
    
    /**
     * 短信超时时间
     */
    private Long smsoverTime;

    // Constructors

    /** default constructor */
    public Validcodeinfo() {
    }

    /** full constructor */
    /**
     * 
     * @param userbasicsinfo 用户基本信息
     * @param emailagaintime email再次发送时间
     * @param emailcode 邮件验证码
     * @param emailovertime email发送超时时间
     * @param smsCode 短信验证码
     * @param smsagainTime 再次发送短信提示时间
     * @param smsoverTime 短信超时时间
     */
    public Validcodeinfo(Userbasicsinfo userbasicsinfo, Long emailagaintime,
            String emailcode, Long emailovertime, String smsCode,
            Long smsagainTime, Long smsoverTime) {
        this.userbasicsinfo = userbasicsinfo;
        this.emailagaintime = emailagaintime;
        this.emailcode = emailcode;
        this.emailovertime = emailovertime;
        this.smsCode = smsCode;
        this.smsagainTime = smsagainTime;
        this.smsoverTime = smsoverTime;
    }

    // Property accessors
    /**
     * 
     * @return Long
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * 
     * @param id 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    
    
    
    /**
     * 发送短信的手机号
     * @return
     */
    @Column(name = "smsPhone")
    public String getSmsPhone() {
		return smsPhone;
	}

	public void setSmsPhone(String smsPhone) {
		this.smsPhone = smsPhone;
	}

	/**
     * 
     * @return Userbasicsinfo
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public Userbasicsinfo getUserbasicsinfo() {
        return this.userbasicsinfo;
    }

    /**
     * 
     * @param userbasicsinfo 用户基本信息
     */
    public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }

    /**
     * 
     * @return Long
     */
    @Column(name = "emailagaintime")
    public Long getEmailagaintime() {
        return this.emailagaintime;
    }

    /**
     * 
     * @param emailagaintime email再次发送时间
     */
    public void setEmailagaintime(Long emailagaintime) {
        this.emailagaintime = emailagaintime;
    }

    /**
     * 
     * @return Long
     */
    @Column(name = "emailcode")
    public String getEmailcode() {
        return this.emailcode;
    }

    /**
     * 
     * @param emailcode 邮箱验证码
     */
    public void setEmailcode(String emailcode) {
        this.emailcode = emailcode;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "emailovertime", length = 50)
    public Long getEmailovertime() {
        return this.emailovertime;
    }

    /**
     * 
     * @param emailovertime email发送超时时间
     */
    public void setEmailovertime(Long emailovertime) {
        this.emailovertime = emailovertime;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "smsCode", length = 50)
    public String getSmsCode() {
        return this.smsCode;
    }

    /**
     * 
     * @param smsCode 短信验证码
     */
    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    /**
     * 
     * @return Long
     */
    @Column(name = "smsagainTime")
    public Long getSmsagainTime() {
        return this.smsagainTime;
    }

    /**
     * 
     * @param smsagainTime 再次发送短信提示时间
     */
    public void setSmsagainTime(Long smsagainTime) {
        this.smsagainTime = smsagainTime;
    }

    /**
     * 
     * @return Long
     */
    @Column(name = "smsoverTime")
    public Long getSmsoverTime() {
        return this.smsoverTime;
    }

    /**
     * 
     * @param smsoverTime 短信超时时间
     */
    public void setSmsoverTime(Long smsoverTime) {
        this.smsoverTime = smsoverTime;
    }

}