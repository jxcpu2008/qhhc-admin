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
 * Comment
 */
@Entity
@Table(name = "loansign_comment")
public class LoansignComment implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * id
     */
    private Long id;

    /**
     * 用户基本信息
     */
    private Userbasicsinfo userbasicsinfo;

    /**
     * 借款标信息表
     */
    private Loansign loansign;

    /**
     * 后台用户信息
     */
    private Adminuser adminuser;

    /**
     * 评论内容
     */
    private String cmtContent;

    /**
     * 是否显示：1显示、0屏蔽
     */
    private String cmtIsShow;

    /**
     * 回复内容
     */
    private String cmtReply;

    /**
     * 评论时间
     */
    private String commentTime;

    /**
     * 回复时间
     */
    private String replyTime;

    // Constructors

    /** default constructor */
    public LoansignComment() {
    }

    /**
     * full constructor
     * @param userbasicsinfo    userbasicsinfo
     * @param loansign  loansign
     * @param adminuser adminuser
     * @param cmtContent        cmtContent
     * @param cmtIsShow cmtIsShow
     * @param cmtReply  cmtReply
     * @param commentTime   commentTime
     * @param replyTime replyTime
     */
    public LoansignComment(Userbasicsinfo userbasicsinfo, Loansign loansign,
            Adminuser adminuser, String cmtContent, String cmtIsShow,
            String cmtReply, String commentTime, String replyTime) {
        this.userbasicsinfo = userbasicsinfo;
        this.loansign = loansign;
        this.adminuser = adminuser;
        this.cmtContent = cmtContent;
        this.cmtIsShow = cmtIsShow;
        this.cmtReply = cmtReply;
        this.commentTime = commentTime;
        this.replyTime = replyTime;
    }

    /**
     * Property accessors
     * @return  id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * id
     * @param id    id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * userbasicsinfo
     * @return  userbasicsinfo
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentator_id")
    public Userbasicsinfo getUserbasicsinfo() {
        return this.userbasicsinfo;
    }

    /**
     * userbasicsinfo
     * @param userbasicsinfo    userbasicsinfo
     */
    public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }

    /**
     * loansign
     * @return  loansign
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loanSign_id")
    public Loansign getLoansign() {
        return this.loansign;
    }

    /**
     * loansign
     * @param loansign  loansign
     */
    public void setLoansign(Loansign loansign) {
        this.loansign = loansign;
    }

    /**
     * adminuser
     * @return  adminuser
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "replyer_id")
    public Adminuser getAdminuser() {
        return this.adminuser;
    }

    /**
     * adminuser
     * @param adminuser adminuser
     */
    public void setAdminuser(Adminuser adminuser) {
        this.adminuser = adminuser;
    }

    /**
     * cmtContent
     * @return  cmtContent
     */
    @Column(name = "cmtContent")
    public String getCmtContent() {
        return this.cmtContent;
    }

    /**
     * cmtContent
     * @param cmtContent    cmtContent
     */
    public void setCmtContent(String cmtContent) {
        this.cmtContent = cmtContent;
    }

    /**
     * cmtIsShow
     * @return  cmtIsShow
     */
    @Column(name = "cmtIsShow")
    public String getCmtIsShow() {
        return this.cmtIsShow;
    }

    /**
     * cmtIsShow
     * @param cmtIsShow cmtIsShow
     */
    public void setCmtIsShow(String cmtIsShow) {
        this.cmtIsShow = cmtIsShow;
    }

    /**
     * cmtReply 
     * @return  cmtReply
     */
    @Column(name = "cmtReply")
    public String getCmtReply() {
        return this.cmtReply;
    }

    /**
     * cmtReply
     * @param cmtReply  cmtReply
     */
    public void setCmtReply(String cmtReply) {
        this.cmtReply = cmtReply;
    }

    /**
     * commentTime
     * @return  commentTime
     */
    @Column(name = "commentTime")
    public String getCommentTime() {
        return this.commentTime;
    }

    /**
     * commentTime
     * @param commentTime   commentTime
     */
    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    /**
     * replyTime
     * @return  replyTime
     */
    @Column(name = "replyTime")
    public String getReplyTime() {
        return this.replyTime;
    }

    
    /**
     * replyTime
     * @param replyTime replyTime
     */
    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

}