package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 预热记录
 */
@Entity
@Table(name = "shop_preheat")
public class ShopPreheat implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	/**
	 * 购买是否成功
	 */
	private Integer isSucceed;
	/**
	 * 投资金额
	 */
	private Double tenderMoney;
	/**
	 * 购买时间
	 */
	private String tenderTime;
	/**
	 * 投资店铺id
	 */
	private Shop shop;
	private Long userId;

	// Constructors

	/** default constructor */
	public ShopPreheat() {
	}

	/** full constructor */
	public ShopPreheat(Integer isSucceed, Double tenderMoney,
			String tenderTime, Long shopId, Long userId) {
		this.isSucceed = isSucceed;
		this.tenderMoney = tenderMoney;
		this.tenderTime = tenderTime;
		this.userId = userId;
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

	@Column(name = "isSucceed")
	public Integer getIsSucceed() {
		return this.isSucceed;
	}

	public void setIsSucceed(Integer isSucceed) {
		this.isSucceed = isSucceed;
	}

	@Column(name = "tenderMoney", precision = 18, scale = 4)
	public Double getTenderMoney() {
		return this.tenderMoney;
	}

	public void setTenderMoney(Double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}

	@Column(name = "tenderTime", length = 32)
	public String getTenderTime() {
		return this.tenderTime;
	}

	public void setTenderTime(String tenderTime) {
		this.tenderTime = tenderTime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	public Shop getShop() {
		return this.shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}