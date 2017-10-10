package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Storerecord entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "shop_record")
public class ShopRecord implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private Integer isSucceed; // 是否成功

	private Double tenderMoney; // 投资金额

	private Double fee; // 手续费

	private String tenderTime; // 加入时间

	private String uptTime; // 宝付返回处理时间

	private Integer isPrivilege; // ,购买时是否是特权会员（0不是,1.是）

	private Userbasicsinfo userbasicsinfo;

	private Shop shop;

	private String orderNum;

	private Integer type; // 出资类型 1. 奖励, 2.股份

	private Integer lottery;// 抽奖号
	private Integer inviteTimes;//邀请次数
	private List<ShopLotteryInvite> shopLotteryInvites = new ArrayList<ShopLotteryInvite>(0);

	/***
	 * 投标模式 1-web 2-app
	 */
	private Integer webOrApp;

	/***
	 * 店铺合同编号
	 */
	private String pContractNo;

	/***
	 * 店铺奖励
	 */
	private Integer shopRoId;

	// Constructors

	/** default constructor */
	public ShopRecord() {
	}

	/** minimal constructor */
	public ShopRecord(Long id) {
		this.id = id;
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

	@Column(name = "isPrivilege")
	public Integer getIsPrivilege() {
		return this.isPrivilege;
	}

	public void setIsPrivilege(Integer isPrivilege) {
		this.isPrivilege = isPrivilege;
	}

	@Column(name = "orderNum")
	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
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

	@Column(name = "fee")
	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	@Column(name = "uptTime")
	public String getUptTime() {
		return uptTime;
	}

	public void setUptTime(String uptTime) {
		this.uptTime = uptTime;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "lottery")
	public Integer getLottery() {
		return lottery;
	}

	public void setLottery(Integer lottery) {
		this.lottery = lottery;
	}

	@Column(name = "shopro_Id")
	public Integer getShopRoId() {
		return shopRoId;
	}

	public void setShopRoId(Integer shopRoId) {
		this.shopRoId = shopRoId;
	}

	@Column(name = "pContractNo")
	public String getpContractNo() {
		return pContractNo;
	}

	public void setpContractNo(String pContractNo) {
		this.pContractNo = pContractNo;
	}

	@Column(name = "webOrApp")
	public Integer getWebOrApp() {
		return webOrApp;
	}

	public void setWebOrApp(Integer webOrApp) {
		this.webOrApp = webOrApp;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shopRecord")
	public List<ShopLotteryInvite> getShopLotteryInvites() {
		return shopLotteryInvites;
	}

	public void setShopLotteryInvites(List<ShopLotteryInvite> shopLotteryInvites) {
		this.shopLotteryInvites = shopLotteryInvites;
	}
	@Column(name = "inviteTimes")
	public Integer getInviteTimes() {
		return inviteTimes;
	}

	public void setInviteTimes(Integer inviteTimes) {
		this.inviteTimes = inviteTimes;
	}

}