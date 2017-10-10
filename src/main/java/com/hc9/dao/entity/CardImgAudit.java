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

/***
 * 身份证审核表
 * @author lkl
 *
 */
@Entity
@Table(name = "cardimgaudit")
public class CardImgAudit implements java.io.Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 主键
     */
    private Long id;
    
    /***
     * 用户Id
     */
    private Userbasicsinfo userbasicsinfo;
    
    /***
     * 身份证图片路径
     */
    private String cardImg;
    
    /***
     * 身份证审核状态0-待审核 1-审核通过 2-审核不通过
     */
    private Integer cardImgState;
    
    /***
     * 身份证审核不通过原因
     */
    private String cardImgRemark;
	
    /***
     * 身份证申请审核时间
     */
    private String cardImgTime; 
    
    /***
     * 身份证审核人
     */
    private Long cardImgAudit; 
    
    /***
     * 身份证审核时间
     */
    private String cardImgAuditTime;
    
    
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	@Column(name="cardImg")
	public String getCardImg() {
		return cardImg;
	}

	public void setCardImg(String cardImg) {
		this.cardImg = cardImg;
	}
	
	public CardImgAudit() {
	}


	public CardImgAudit(Long id, Userbasicsinfo userbasicsinfo, String cardImg,
			Integer cardImgState, String cardImgRemark, String cardImgTime,
			Long cardImgAudit, String cardImgAuditTime) {
		super();
		this.id = id;
		this.userbasicsinfo = userbasicsinfo;
		this.cardImg = cardImg;
		this.cardImgState = cardImgState;
		this.cardImgRemark = cardImgRemark;
		this.cardImgTime = cardImgTime;
		this.cardImgAudit = cardImgAudit;
		this.cardImgAuditTime = cardImgAuditTime;
	}

	@Column(name="cardImgState")
	public Integer getCardImgState() {
		return cardImgState;
	}

	public void setCardImgState(Integer cardImgState) {
		this.cardImgState = cardImgState;
	}
	
	@Column(name="cardImgRemark")
	public String getCardImgRemark() {
		return cardImgRemark;
	}

	public void setCardImgRemark(String cardImgRemark) {
		this.cardImgRemark = cardImgRemark;
	}

	@Column(name="cardImgTime")
	public String getCardImgTime() {
		return cardImgTime;
	}

	public void setCardImgTime(String cardImgTime) {
		this.cardImgTime = cardImgTime;
	}

	@Column(name="cardImgAudit")
	public Long getCardImgAudit() {
		return cardImgAudit;
	}

	public void setCardImgAudit(Long cardImgAudit) {
		this.cardImgAudit = cardImgAudit;
	}

	@Column(name="cardImgAuditTime")
	public String getCardImgAuditTime() {
		return cardImgAuditTime;
	}

	public void setCardImgAuditTime(String cardImgAuditTime) {
		this.cardImgAuditTime = cardImgAuditTime;
	}
	

}
