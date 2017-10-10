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
 * 店铺关注表
 */
@Entity
@Table(name = "shop_attention")
public class ShopAttention implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long userId;
	
	private Shop shop;
	
	private Integer success;
	
	/**
	 * 关注时间
	 */
	private String createTime; 


	/** default constructor */
	public ShopAttention() {
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

	@Column(name = "user_id")
	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "success")
	public Integer getSuccess() {
		return this.success;
	}

	public void setSuccess(Integer success) {
		this.success = success;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	@Column(name = "createTime")
	public String getCreateTime() {
		return createTime;
	}


	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}