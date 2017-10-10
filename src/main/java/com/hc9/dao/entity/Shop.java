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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 店铺信息
 */
@Entity
@Table(name = "shop")
public class Shop implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	/**
	 * id
	 */
	private Long id;
	/**
	 * 店铺发布人
	 */
	private Userbasicsinfo userbasicsinfo;
	/**
	 * 店铺相关文件
	 */
	private ShopFile shopFile;

	/***
	 * 店铺审批/审核
	 */
	private ShopExamine shopExamine;
	/**
	 * 项目名称
	 */
	private String shopName;
	/**
	 * 筹资总额
	 */
	private Double raiseFunds;
	/**
	 * 实际筹集金额
	 */
	private Double realFunds;
	/**
	 * 项目方出资金额
	 */
	private Double selfFunds;
	/**
	 * 项目方出资金额所占比例
	 */
	private Double bis;

	/**
	 * 项目宣传图
	 */
	private String shopPropImage;
	/**
	 * 项目封面图
	 */
	private String shopCoverImage;
	/**
	 * 一句话介绍
	 */
	private String introduct;
	/**
	 * 开店计划
	 */
	private String plan;
	/**
	 * 行业1
	 */
	private String pindustry1;
	/**
	 * 行业2
	 */
	private String pindustry2;
	/**
	 * 店铺简介
	 */
	private String shopDescription;
	/**
	 * 省
	 */
	private String province;
	/**
	 * 市
	 */
	private String city;
	/**
	 * 区县
	 */
	private String county;
	/**
	 * 街道
	 */
	private String street;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 开店数
	 */
	private Integer pshopNum;
	/**
	 * 创始人姓名
	 */
	private String founder;
	/**
	 * 创始人邮件
	 */
	private String founderEmail;
	/**
	 * 创始人头像
	 */
	private String founderPic;
	/**
	 * 创始人简介
	 */
	private String founderBrief;
	/**
	 * 官网地址
	 */
	private String website;
	/**
	 * 退出机制
	 */
	private String optOut;
	/**
	 * 寄语
	 */
	private String crewords;
	/**
	 * 团队介绍
	 */
	private String teamProfiles;
	/**
	 * 店铺创建日期
	 */
	private String createTime;
	/**
	 * 预热天数
	 */
	private Integer preheat;

	/***
	 * 项目方收益比例
	 */
	private Double proIncomeProportion;
	/**
	 * 状态:0-未提交 1-草稿中 2-预热中 3-预热结束(修改) 4-融资中 5-融资成功 6-已放款 7-分红 8-已完成 9-已流标
	 */
	private Integer state;
	/**
	 * 审核客服
	 */
	private Long adminuserIdShop;

	/**
	 * 权限设置:0所有人可见,1会员,2认证投资人
	 */
	private Integer auth;
	/**
	 * 标号，宝付用
	 */
	private String cus_id;

	/***
	 * 放款时间
	 */
	private String lendingTime;

	/***
	 * 预热开始时间
	 */
	private String preheatStarTime;

	/***
	 * 预热结束时间
	 */
	private String preheatEndTime;

	/***
	 * 融资天数
	 */
	private Integer financing;

	/***
	 * 融资实际结束时间
	 */
	private String financingTime;
	/**
	 * 店铺位置的经度
	 */
	private String longitude;
	/**
	 * 店铺位置的纬度
	 */
	private String latitude;

	/***
	 * 放款/流标订单ID
	 */
	private String orderSn;
	
	/***
	 * 店铺编号
	 */
	private String shopNumber;
	
	/***
	 * 放款服务费
	 */
	private Double fee;
	
	/**
	 * 类型：1普通的众筹，2抽奖
	 */
	private Integer type;
	
	/**
	 * 投资起始时间
	 */
	private String startInvestTime;
	
	/**
	 * 投资结束时间
	 */
	private String endInvestTime;
	/**
	 * 奖励模式
	 */
	private List<ShopRewardOption> shopRewardOptions = new ArrayList<ShopRewardOption>(
			0);
	/**
	 * 常见问题
	 */
	private List<ShopCommonProblem> shopCommonProblems = new ArrayList<ShopCommonProblem>(
			0);

	/**
	 * 店铺财务详情
	 */
	private List<ShopFinanceDetail> shopFinanceDetails = new ArrayList<ShopFinanceDetail>(
			0);

	/**
	 * 店铺预热
	 */
	private List<ShopPreheat> shopPreheats = new ArrayList<ShopPreheat>(0);

	/***
	 * 店铺约谈
	 */
	private List<ShopInterview> shopInterviews = new ArrayList<ShopInterview>(0);

	/***
	 * 店铺关注
	 */
	private List<ShopAttention> shopAttentions = new ArrayList<ShopAttention>(0);

	/***
	 * 店铺购买记录
	 */
	private List<ShopRecord> shopRecordList = new ArrayList<ShopRecord>(0);
	
	/***
	 * 店铺评论
	 */
	private List<ShopComment> shopComments = new ArrayList<ShopComment>(0);
	
	/**
	 * 店铺积分
	 */
	private List<ShopPoints> shopPointses = new ArrayList<ShopPoints>(0);
	/**
	 * 店铺抽奖
	 */
	private List<ShopLottery> shopLotteries = new ArrayList<ShopLottery>(0);
	// Constructors

	/** default constructor */
	public Shop() {
	}
	
	public Shop(Long id) {
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	/**
	 * 项目名称
	 */

	@Column(name = "shop_name", length = 128)
	public String getShopName() {
		return this.shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	/**
	 * 筹资总额
	 */
	@Column(name = "raise_funds", precision = 20, scale = 4)
	public Double getRaiseFunds() {
		return this.raiseFunds;
	}

	public void setRaiseFunds(Double raiseFunds) {
		this.raiseFunds = raiseFunds;
	}

	/**
	 * 实际筹集金额
	 * 
	 * @return
	 */
	@Column(name = "real_funds", precision = 20, scale = 4)
	public Double getRealFunds() {
		return this.realFunds;
	}

	public void setRealFunds(Double realFunds) {
		this.realFunds = realFunds;
	}

	/**
	 * 自有金额
	 * 
	 * @return
	 */
	@Column(name = "self_funds", precision = 20, scale = 4)
	public Double getSelfFunds() {
		return this.selfFunds;
	}

	public void setSelfFunds(Double selfFunds) {
		this.selfFunds = selfFunds;
	}

	/**
	 * 自有资金所占比例
	 * 
	 * @return
	 */
	@Column(name = "BIS", precision = 22, scale = 0)
	public Double getBis() {
		return this.bis;
	}

	public void setBis(Double bis) {
		this.bis = bis;
	}

	/**
	 * 项目封面图
	 * 
	 * @return
	 */
	@Column(name = "shop_cover_image", length = 128)
	public String getShopCoverImage() {
		return this.shopCoverImage;
	}

	public void setShopCoverImage(String shopCoverImage) {
		this.shopCoverImage = shopCoverImage;
	}

	/**
	 * 一句话介绍
	 * 
	 * @return
	 */
	@Column(name = "introduct", length = 128)
	public String getIntroduct() {
		return this.introduct;
	}

	public void setIntroduct(String introduct) {
		this.introduct = introduct;
	}

	/**
	 * 开店计划
	 * 
	 * @return
	 */
	@Column(name = "plan", length = 500)
	public String getPlan() {
		return this.plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	/**
	 * 行业1
	 * 
	 * @return
	 */
	@Column(name = "pIndustry1", length = 128)
	public String getPindustry1() {
		return this.pindustry1;
	}

	public void setPindustry1(String pindustry1) {
		this.pindustry1 = pindustry1;
	}

	/**
	 * 行业2
	 * 
	 * @return
	 */
	@Column(name = "pIndustry2", length = 128)
	public String getPindustry2() {
		return this.pindustry2;
	}

	public void setPindustry2(String pindustry2) {
		this.pindustry2 = pindustry2;
	}

	/**
	 * 项目简介
	 * 
	 * @return
	 */
	@Column(name = "shop_description", length = 65535)
	public String getShopDescription() {
		return this.shopDescription;
	}

	public void setShopDescription(String shopDescription) {
		this.shopDescription = shopDescription;
	}

	/**
	 * 省
	 * 
	 * @return
	 */
	@Column(name = "province", length = 128)
	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * 市
	 * 
	 * @return
	 */
	@Column(name = "city", length = 128)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * 国家
	 * 
	 * @return
	 */
	@Column(name = "county", length = 128)
	public String getCounty() {
		return this.county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	/**
	 * 街道
	 * 
	 * @return
	 */
	@Column(name = "street", length = 128)
	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * 地址
	 * 
	 * @return
	 */
	@Column(name = "address", length = 128)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 开店数
	 * 
	 * @return
	 */
	@Column(name = "pShopNum")
	public Integer getPshopNum() {
		return this.pshopNum;
	}

	public void setPshopNum(Integer pshopNum) {
		this.pshopNum = pshopNum;
	}

	/**
	 * 创始人
	 * 
	 * @return
	 */
	@Column(name = "founder")
	public String getFounder() {
		return this.founder;
	}

	public void setFounder(String founder) {
		this.founder = founder;
	}

	/**
	 * 创始人邮件
	 * 
	 * @return
	 */
	@Column(name = "founder_email")
	public String getFounderEmail() {
		return this.founderEmail;
	}

	public void setFounderEmail(String founderEmail) {
		this.founderEmail = founderEmail;
	}

	/**
	 * 创始人头像
	 * 
	 * @return
	 */
	@Column(name = "founder_pic")
	public String getFounderPic() {
		return this.founderPic;
	}

	public void setFounderPic(String founderPic) {
		this.founderPic = founderPic;
	}

	/**
	 * 创始人简介
	 * 
	 * @return
	 */
	@Column(name = "founder_brief")
	public String getFounderBrief() {
		return this.founderBrief;
	}

	public void setFounderBrief(String founderBrief) {
		this.founderBrief = founderBrief;
	}

	/**
	 * 官网
	 * 
	 * @return
	 */
	@Column(name = "website")
	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * 退出机制
	 * 
	 * @return
	 */
	@Column(name = "optOut")
	public String getOptOut() {
		return this.optOut;
	}

	public void setOptOut(String optOut) {
		this.optOut = optOut;
	}
	
	
	/**
	 * 店铺宣传图
	 * @return
	 */
	@Column(name = "shop_prop_image")
	public String getShopPropImage() {
		return shopPropImage;
	}

	public void setShopPropImage(String shopPropImage) {
		this.shopPropImage = shopPropImage;
	}

	/**
	 * 寄语
	 * 
	 * @return
	 */
	@Column(name = "crewords")
	public String getCrewords() {
		return this.crewords;
	}

	public void setCrewords(String crewords) {
		this.crewords = crewords;
	}

	/**
	 * 团队
	 * 
	 * @return
	 */
	@Column(name = "team_profiles", length = 1000)
	public String getTeamProfiles() {
		return this.teamProfiles;
	}

	public void setTeamProfiles(String teamProfiles) {
		this.teamProfiles = teamProfiles;
	}

	/**
	 * 店铺创建时间
	 * 
	 * @return
	 */
	@Column(name = "create_time", length = 50)
	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * 预热
	 * 
	 * @return
	 */
	@Column(name = "preheat")
	public Integer getPreheat() {
		return this.preheat;
	}

	public void setPreheat(Integer preheat) {
		this.preheat = preheat;
	}

	/**
	 * 状态
	 * 
	 * @return
	 */
	@Column(name = "state")
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Column(name = "adminuser_idshop")
	public Long getAdminuserIdShop() {
		return this.adminuserIdShop;
	}

	public void setAdminuserIdShop(Long adminuserIdShop) {
		this.adminuserIdShop = adminuserIdShop;
	}

	/**
	 * 权限
	 * 
	 * @return
	 */
	@Column(name = "auth")
	public Integer getAuth() {
		return this.auth;
	}

	public void setAuth(Integer auth) {
		this.auth = auth;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	public List<ShopRewardOption> getShopRewardOptions() {
		return this.shopRewardOptions;
	}

	public void setShopRewardOptions(List<ShopRewardOption> shopRewardOptions) {
		this.shopRewardOptions = shopRewardOptions;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	@JsonIgnore
	public List<ShopCommonProblem> getShopCommonProblems() {
		return this.shopCommonProblems;
	}

	public void setShopCommonProblems(List<ShopCommonProblem> shopCommonProblems) {
		this.shopCommonProblems = shopCommonProblems;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	@JsonIgnore
	public ShopFile getShopFile() {
		return shopFile;
	}

	public void setShopFile(ShopFile shopFile) {
		this.shopFile = shopFile;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	@JsonIgnore
	public List<ShopFinanceDetail> getShopFinanceDetails() {
		return this.shopFinanceDetails;
	}

	public void setShopFinanceDetails(List<ShopFinanceDetail> shopFinanceDetails) {
		this.shopFinanceDetails = shopFinanceDetails;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	@JsonIgnore
	public List<ShopPreheat> getShopPreheats() {
		return shopPreheats;
	}

	public void setShopPreheats(List<ShopPreheat> shopPreheats) {
		this.shopPreheats = shopPreheats;
	}

	@Column(name = "cus_id", nullable = false)
	public String getCus_id() {
		return cus_id;
	}

	public void setCus_id(String cus_id) {
		this.cus_id = cus_id;
	}

	@Column(name = "proIncomeProportion")
	public Double getProIncomeProportion() {
		return proIncomeProportion;
	}

	public void setProIncomeProportion(Double proIncomeProportion) {
		this.proIncomeProportion = proIncomeProportion;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	@JsonIgnore
	public List<ShopRecord> getShopRecordList() {
		return shopRecordList;
	}

	public void setShopRecordList(List<ShopRecord> shopRecordList) {
		this.shopRecordList = shopRecordList;
	}

	@Column(name = "lending_time")
	public String getLendingTime() {
		return lendingTime;
	}

	public void setLendingTime(String lendingTime) {
		this.lendingTime = lendingTime;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	@JsonIgnore
	public ShopExamine getShopExamine() {
		return shopExamine;
	}

	public void setShopExamine(ShopExamine shopExamine) {
		this.shopExamine = shopExamine;
	}

	@Column(name = "preheatstar_time")
	public String getPreheatStarTime() {
		return preheatStarTime;
	}

	public void setPreheatStarTime(String preheatStarTime) {
		this.preheatStarTime = preheatStarTime;
	}

	@Column(name = "preheatend_time")
	public String getPreheatEndTime() {
		return preheatEndTime;
	}

	public void setPreheatEndTime(String preheatEndTime) {
		this.preheatEndTime = preheatEndTime;
	}

	@Column(name = "financing")
	public Integer getFinancing() {
		return financing;
	}

	public void setFinancing(Integer financing) {
		this.financing = financing;
	}

	@Column(name = "financing_time")
	public String getFinancingTime() {
		return financingTime;
	}

	public void setFinancingTime(String financingTime) {
		this.financingTime = financingTime;
	}

	@Column(name = "longitude")
	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Column(name = "latitude")
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	@JsonIgnore
	public List<ShopInterview> getShopInterviews() {
		return shopInterviews;
	}

	public void setShopInterviews(List<ShopInterview> shopInterviews) {
		this.shopInterviews = shopInterviews;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	@JsonIgnore
	public List<ShopAttention> getShopAttentions() {
		return shopAttentions;
	}

	public void setShopAttentions(List<ShopAttention> shopAttentions) {
		this.shopAttentions = shopAttentions;
	}

	@Column(name = "order_sn")
	public String getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	@JsonIgnore
	public List<ShopComment> getShopComments() {
		return shopComments ;
	}

	public void setShopComments(List<ShopComment> shopComments) {
		this.shopComments = shopComments;
	}

	@Column(name = "shopNumber")
	public String getShopNumber() {
		return shopNumber;
	}

	public void setShopNumber(String shopNumber) {
		this.shopNumber = shopNumber;
	}
	
	@Column(name = "fee")
	public Double getFee() {
		return fee;
	}
	public void setFee(Double fee) {
		this.fee = fee;
	}
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="shop")
	public List<ShopPoints> getShopPointses() {
		return shopPointses;
	}

	public void setShopPointses(List<ShopPoints> shopPointses) {
		this.shopPointses = shopPointses;
	}
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="shop")
	public List<ShopLottery> getShopLotteries() {
		return shopLotteries;
	}

	public void setShopLotteries(List<ShopLottery> shopLotteries) {
		this.shopLotteries = shopLotteries;
	}
	@Column(name = "type")
	public Integer getType() {
		return type;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}
	@Column(name = "start_invest_time")
	public String getStartInvestTime() {
		return startInvestTime;
	}

	public void setStartInvestTime(String startInvestTime) {
		this.startInvestTime = startInvestTime;
	}
	@Column(name = "end_invest_time")
	public String getEndInvestTime() {
		return endInvestTime;
	}

	public void setEndInvestTime(String endInvestTime) {
		this.endInvestTime = endInvestTime;
	}

	
	
}