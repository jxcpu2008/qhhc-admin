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

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 店铺融资模式选项
 */
@Entity
@Table(name = "shop_reward_option")
public class ShopRewardOption implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Shop shop;
	/**
	 * 出资类型
	 * 1. 奖励, 2.股份
	 */
	private Integer type;
	/**
	 * 出资金额
	 */
	private Double money;
	/**
	 * 出资奖励描述
	 */
	private String remark;

	/**
	 * 分红比例
	 */
	private Double earningsRate;
	/**
	 * 奖励时间
	 */
	private Integer awardTime;
	
	/**
	 * 递增金额
	 */
	private Integer increase;
	
	/***
	 * 索引
	 */
	private Integer indexes;
	
	/***
	 * 人数
	 */
	private Integer rewardCount;
	
	/***
	 * 是否为抽奖 0-否 1-是
	 */
	private Integer istrue;
	/**
	 * 总额
	 */
	private Double total;

	// Constructors

	/** default constructor */
	public ShopRewardOption() {
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
	@JoinColumn(name = "shop_id")
	@JsonIgnore
	public Shop getShop() {
		return this.shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "money", precision = 18, scale = 4)
	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	@Column(name = "remark")
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "earnings_rate", precision = 6)
	public Double getEarningsRate() {
		return this.earningsRate;
	}

	public void setEarningsRate(Double earningsRate) {
		this.earningsRate = earningsRate;
	}

	@Column(name = "awardTime")
	public Integer getAwardTime() {
		return this.awardTime;
	}

	public void setAwardTime(Integer awardTime) {
		this.awardTime = awardTime;
	}

	@Column(name = "increase")
	public Integer getIncrease() {
		return increase;
	}


	public void setIncrease(Integer increase) {
		this.increase = increase;
	}
	@Column(name = "indexes")
	public Integer getIndexes() {
		return indexes;
	}

	public void setIndexes(Integer indexes) {
		this.indexes = indexes;
	}

	@Column(name = "reward_count")
	public Integer getRewardCount() {
		return rewardCount;
	}


	public void setRewardCount(Integer rewardCount) {
		this.rewardCount = rewardCount;
	}

	@Column(name = "istrue")
	public Integer getIstrue() {
		return istrue;
	}

	public void setIstrue(Integer istrue) {
		this.istrue = istrue;
	}

	@Column(name = "total")
	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}
	
	
	
	

}