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
 * 店铺财务详情
 */
@Entity
@Table(name = "shop_finance_detail")
public class ShopFinanceDetail implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Userbasicsinfo userbasicsinfo;
	private Shop shop;
	/**
	 * 统计时间
	 */
	private String commitTime;
	/**
	 * 财务类型
	 */
	private String type;
	/**
	 * 
	 */
	private Double spend;
	/**
	 * 用途
	 */
	private String purpose;
	/**
	 * 凭证
	 */
	private String credential;
	/**
	 * 备注
	 */
	private String remark;

	// Constructors

	/** default constructor */
	public ShopFinanceDetail() {
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
	@JoinColumn(name = "user_id")
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	public Shop getShop() {
		return this.shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	@Column(name = "commitTime", length = 50)
	public String getCommitTime() {
		return this.commitTime;
	}

	public void setCommitTime(String commitTime) {
		this.commitTime = commitTime;
	}

	@Column(name = "type", length = 20)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "spend", precision = 20, scale = 4)
	public Double getSpend() {
		return this.spend;
	}

	public void setSpend(Double spend) {
		this.spend = spend;
	}

	@Column(name = "purpose", length = 20)
	public String getPurpose() {
		return this.purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	@Column(name = "credential", length = 200)
	public String getCredential() {
		return this.credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	@Column(name = "remark", length = 20)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}