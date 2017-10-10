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
 * ShopRepaymentrecord entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "shop_repaymentrecord")
public class ShopRepaymentrecord implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private Long id;
	private Shop shop;
	private Integer periods;
	private String preRepayDate;
	private Integer repayState;
	private String repayTime;
	private Double bonus;
	private String pipsBillNo;
	private String pmerBillNo;
	private String pipsTime1;
	private String pipsTime2;

	// Constructors

	/** default constructor */
	public ShopRepaymentrecord() {
	}

	/** full constructor */
	public ShopRepaymentrecord(Shop shop, Integer periods, String preRepayDate,
			Integer repayState, String repayTime, Double bonus,
			String pipsBillNo, String pmerBillNo, String pipsTime1,
			String pipsTime2) {
		this.shop = shop;
		this.periods = periods;
		this.preRepayDate = preRepayDate;
		this.repayState = repayState;
		this.repayTime = repayTime;
		this.bonus = bonus;
		this.pipsBillNo = pipsBillNo;
		this.pmerBillNo = pmerBillNo;
		this.pipsTime1 = pipsTime1;
		this.pipsTime2 = pipsTime2;
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
	public Shop getShop() {
		return this.shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	@Column(name = "periods")
	public Integer getPeriods() {
		return this.periods;
	}

	public void setPeriods(Integer periods) {
		this.periods = periods;
	}

	@Column(name = "preRepayDate")
	public String getPreRepayDate() {
		return this.preRepayDate;
	}

	public void setPreRepayDate(String preRepayDate) {
		this.preRepayDate = preRepayDate;
	}

	@Column(name = "repayState")
	public Integer getRepayState() {
		return this.repayState;
	}

	public void setRepayState(Integer repayState) {
		this.repayState = repayState;
	}

	@Column(name = "repayTime")
	public String getRepayTime() {
		return this.repayTime;
	}

	public void setRepayTime(String repayTime) {
		this.repayTime = repayTime;
	}

	@Column(name = "bonus", precision = 18, scale = 4)
	public Double getBonus() {
		return this.bonus;
	}

	public void setBonus(Double bonus) {
		this.bonus = bonus;
	}

	@Column(name = "pIpsBillNo", length = 30)
	public String getPipsBillNo() {
		return this.pipsBillNo;
	}

	public void setPipsBillNo(String pipsBillNo) {
		this.pipsBillNo = pipsBillNo;
	}

	@Column(name = "pMerBillNo", length = 30)
	public String getPmerBillNo() {
		return this.pmerBillNo;
	}

	public void setPmerBillNo(String pmerBillNo) {
		this.pmerBillNo = pmerBillNo;
	}

	@Column(name = "pIpsTime1", length = 14)
	public String getPipsTime1() {
		return this.pipsTime1;
	}

	public void setPipsTime1(String pipsTime1) {
		this.pipsTime1 = pipsTime1;
	}

	@Column(name = "pIpsTime2", length = 14)
	public String getPipsTime2() {
		return this.pipsTime2;
	}

	public void setPipsTime2(String pipsTime2) {
		this.pipsTime2 = pipsTime2;
	}

}