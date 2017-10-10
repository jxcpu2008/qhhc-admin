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
 * Feedbackinfo
 */
@Entity
@Table(name = "feedbackinfo")
public class Feedbackinfo implements java.io.Serializable {

    // Fields

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private Long id;

    /**
     * 邮件还款类型
     */
    private Feedbacktype feedbacktype;

    /**
     * 上传附件
     */
    private Uploadfile uploadfile;

    /**
     * 反馈人姓名
     */
    private String name;

    /**
     * 反馈人邮件
     */
    private String email;

    /**
     * 反馈人电话
     */
    private String phone;

    /**
     * 反馈内容
     */
    private String context;

    /**
     * 添加时间
     */
    private String addTime;
    
    /**
     * 是否显示
     */
    private Integer replyStatus;

    /** default constructor */
    public Feedbackinfo() {
    }

    /**
     * 构造方法
     * 
     * @param feedbacktype
     *            邮件还款类型
     * @param uploadfile
     *            上传附件
     * @param name
     *            反馈人姓名
     * @param email
     *            反馈人邮箱
     * @param phone
     *            反馈人手机号码
     * @param context
     *            反馈类容
     * @param addTime
     *            添加时间
      * @param replyStatus
     *            反馈状态      
     */
    public Feedbackinfo(Feedbacktype feedbacktype, Uploadfile uploadfile,
            String name, String email, String phone, String context,
            String addTime, Integer replyStatus) {
        this.feedbacktype = feedbacktype;
        this.uploadfile = uploadfile;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.context = context;
        this.addTime = addTime;
        this.replyStatus = replyStatus;
    }

    /**
     * 编号
     * 
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
     * 
     * @param id
     *            编号
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 反馈类型
     * 
     * @return 反馈类型
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "feedbacktype_id")
    public Feedbacktype getFeedbacktype() {
        return this.feedbacktype;
    }

    /**
     * 反馈类型
     * 
     * @param feedbacktype
     *            反馈类型
     */
    public void setFeedbacktype(Feedbacktype feedbacktype) {
        this.feedbacktype = feedbacktype;
    }

    /**
     * 上传附件
     * 
     * @return 上传附件
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id")
    public Uploadfile getUploadfile() {
        return this.uploadfile;
    }

    /**
     * 上传附件
     * 
     * @param uploadfile
     *            上传附件
     */
    public void setUploadfile(Uploadfile uploadfile) {
        this.uploadfile = uploadfile;
    }

    /**
     * 上传人姓名
     * 
     * @return 上传人姓名
     */
    @Column(name = "name", length = 50)
    public String getName() {
        return this.name;
    }

    /**
     * 上传人姓名
     * 
     * @param name
     *            上传人姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 上传人邮箱
     * 
     * @return 上传人邮箱
     */
    @Column(name = "email", length = 60)
    public String getEmail() {
        return this.email;
    }

    /**
     * 上传人邮箱
     * 
     * @param email
     *            上传人邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 上传人手机
     * 
     * @return 上传人手机
     */
    @Column(name = "Phone", length = 30)
    public String getPhone() {
        return this.phone;
    }

    /**
     * 上传人手机
     * 
     * @param phone
     *            上传人手机
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 上传类容
     * 
     * @return 上传类容
     */
    @Column(name = "context", length = 2080)
    public String getContext() {
        return this.context;
    }

    /**
     * 上传类容
     * 
     * @param context
     *            上传类容
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * 添加时间
     * 
     * @return 添加时间
     */
    @Column(name = "addTime", length = 50)
    public String getAddTime() {
        return this.addTime;
    }

    /**
     * 添加时间
     * 
     * @param addTime
     *            添加时间
     */
    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    /**
     * 回复状态
     * 
     * @return 回复状态
     */
    @Column(name = "replyStatus")
    public Integer getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(Integer replyStatus) {
        this.replyStatus = replyStatus;
    }

}