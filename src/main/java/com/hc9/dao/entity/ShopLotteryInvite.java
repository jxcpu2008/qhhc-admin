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
 * ShopLotteryInvite entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "shop_lottery_invite")
public class ShopLotteryInvite implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	/**
	 * 发邀请用户的投资记录
	 */
	private ShopRecord shopRecord;
	/**
	 * 发邀请用户
	 */
	private Long invitUserId;
	/**
	 * 受邀用户
	 */
	private Long invitedUserId;
	/**
	 * 店铺
	 */
	private Long shopId;
	/**
	 * 抽奖开始时间
	 */
	private String startTime;
	/**
	 * 抽奖结束时间
	 */
	private String endTime;
	

	// Constructors

	/** default constructor */
	public ShopLotteryInvite() {
	}

	/** full constructor */
	public ShopLotteryInvite(ShopRecord shopRecord, Long invitUserId,
			Long invitedUserId, Long shopId) {
		this.shopRecord = shopRecord;
		this.invitUserId = invitUserId;
		this.invitedUserId = invitedUserId;
		this.shopId = shopId;
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
	@JoinColumn(name = "shop_record_id")
	public ShopRecord getShopRecord() {
		return this.shopRecord;
	}

	public void setShopRecord(ShopRecord shopRecord) {
		this.shopRecord = shopRecord;
	}

	@Column(name = "invit_user_id")
	public Long getInvitUserId() {
		return this.invitUserId;
	}

	public void setInvitUserId(Long invitUserId) {
		this.invitUserId = invitUserId;
	}

	@Column(name = "invited_user_id")
	public Long getInvitedUserId() {
		return this.invitedUserId;
	}

	public void setInvitedUserId(Long invitedUserId) {
		this.invitedUserId = invitedUserId;
	}

	@Column(name = "shop_id")
	public Long getShopId() {
		return this.shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}
	@Column(name = "start_time")
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	@Column(name = "end_time")
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}