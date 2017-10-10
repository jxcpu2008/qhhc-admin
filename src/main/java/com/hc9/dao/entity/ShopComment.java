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
 * Commentshop entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "shop_comment")
public class ShopComment implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String cmtContent; // 评论内容
	private Integer cmtIsShow; // 1显示、0屏蔽
	private String cmtReply; // 回复内容
	private String commentTime;// 评论时间
	private String replyTime;// 回复时间
	private Userbasicsinfo commentator; // 评论人
	private Shop shop; // 店铺
	private Userbasicsinfo replyer; // 回复人
	private Long refCommId;   // 评论子项（应用本表id）

	// Constructors

	/** default constructor */
	public ShopComment() {
	}

	public ShopComment(Long id) {
		this.id = id;
	}


	public ShopComment(Long id, String cmtContent, Integer cmtIsShow,
			String cmtReply, String commentTime, String replyTime,
			Userbasicsinfo commentator, Shop shop, Userbasicsinfo replyer) {
		super();
		this.id = id;
		this.cmtContent = cmtContent;
		this.cmtIsShow = cmtIsShow;
		this.cmtReply = cmtReply;
		this.commentTime = commentTime;
		this.replyTime = replyTime;
		this.commentator = commentator;
		this.shop = shop;
		this.replyer = replyer;
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

	@Column(name = "cmtContent", length = 65535)
	public String getCmtContent() {
		return this.cmtContent;
	}

	public void setCmtContent(String cmtContent) {
		this.cmtContent = cmtContent;
	}

	@Column(name = "cmtIsShow")
	public Integer getCmtIsShow() {
		return this.cmtIsShow;
	}

	public void setCmtIsShow(Integer cmtIsShow) {
		this.cmtIsShow = cmtIsShow;
	}

	@Column(name = "cmtReply", length = 65535)
	public String getCmtReply() {
		return this.cmtReply;
	}

	public void setCmtReply(String cmtReply) {
		this.cmtReply = cmtReply;
	}

	@Column(name = "commentTime")
	public String getCommentTime() {
		return this.commentTime;
	}

	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}

	@Column(name = "replyTime")
	public String getReplyTime() {
		return this.replyTime;
	}

	public void setReplyTime(String replyTime) {
		this.replyTime = replyTime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "commentator_id")
	public Userbasicsinfo getCommentator() {
		return commentator;
	}

	public void setCommentator(Userbasicsinfo commentator) {
		this.commentator = commentator;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "replyer_id")
	public Userbasicsinfo getReplyer() {
		return this.replyer;
	}

	public void setReplyer(Userbasicsinfo replyer) {
		this.replyer = replyer;
	}

	@Column(name = "ref_comm_id")
	public Long getRefCommId() {
		return refCommId;
	}

	public void setRefCommId(Long refCommId) {
		this.refCommId = refCommId;
	}

}