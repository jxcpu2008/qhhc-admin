package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Loansignbasics entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "loansignbasics")
public class Loansignbasics implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Loansign loansign;
	/**借款标号*/
	private String loanNumber;
	/**商业模式介绍*/
	private String businessIntro;
	/**创业团队介绍*/
	private String teamsIntro;
	/**历史情况*/
	private String history;
	/**未来计划*/
	private String futurePlan;
	/**项目附件1*/
	private String projectAtt1;
	/**项目附件2*/
	private String projectAtt2;


//	/**分红方式 1.按月 2.按季度*/
//	private Integer refunway;
	/**查看次数*/
	private Long views;
	/**权限 0所有 1批准后看*/
	private Integer permission;
	/**项目类型：1酒店 2 能源 3 文化产业 4互联网创新 5其它*/
	private Integer loanType;
	/**项目状态：0 未启动 1产品开发中、2产品已上市或已上线、3已经有收入、4已经盈利*/
	private Integer projectState;
	/**企业员工人数*/
	private Integer person;
	/**是否成立：0否 1 是*/
	private Integer isUp;
	/**是否有其他项目：1是，2否*/
	private Integer haveOther;
	/**办公地址*/
	private String address;
	/**项目简介*/
	private String remark;
	/**项目图片*/
	private String loanimg;
	/**省份*/
	private String province;
	/**城市*/
	private String city;
	/**项目所属行业*/
	private String proindustry;
	/**企业成立时间1*/
	private String enteruptime1;
	/**企业成立时间2*/
	private String enteruptime2;
	/***借款用途*/
	private String behoof;
	
	/**
	 * 推荐理由
	 * */
	private String recommandReason;
	
	/*
	 * 风控意见
	 */
	private String riskAdvice;

	// Constructors

	/** default constructor */
	public Loansignbasics() {
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
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
	public Loansign getLoansign() {
		return this.loansign;
	}

	public void setLoansign(Loansign loansign) {
		this.loansign = loansign;
	}

	@Column(name = "loanNumber")
	public String getLoanNumber() {
		return this.loanNumber;
	}

	public void setLoanNumber(String loanNumber) {
		this.loanNumber = loanNumber;
	}

	@Column(name = "businessIntro", length = 512)
	public String getBusinessIntro() {
		return this.businessIntro;
	}

	public void setBusinessIntro(String businessIntro) {
		this.businessIntro = businessIntro;
	}

	@Column(name = "teamsIntro", length = 512)
	public String getTeamsIntro() {
		return this.teamsIntro;
	}

	public void setTeamsIntro(String teamsIntro) {
		this.teamsIntro = teamsIntro;
	}

	@Column(name = "history", length = 512)
	public String getHistory() {
		return this.history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	@Column(name = "futurePlan", length = 512)
	public String getFuturePlan() {
		return this.futurePlan;
	}

	public void setFuturePlan(String futurePlan) {
		this.futurePlan = futurePlan;
	}

	@Column(name = "projectAtt1", length = 512)
	public String getProjectAtt1() {
		return this.projectAtt1;
	}

	public void setProjectAtt1(String projectAtt1) {
		this.projectAtt1 = projectAtt1;
	}

	@Column(name = "projectAtt2", length = 512)
	public String getProjectAtt2() {
		return this.projectAtt2;
	}

	public void setProjectAtt2(String projectAtt2) {
		this.projectAtt2 = projectAtt2;
	}




	@Column(name = "views")
	public Long getViews() {
		return this.views;
	}

	public void setViews(Long views) {
		this.views = views;
	}

	@Column(name = "permission")
	public Integer getPermission() {
		return this.permission;
	}

	public void setPermission(Integer permission) {
		this.permission = permission;
	}

	@Column(name = "loanType")
	public Integer getLoanType() {
		return this.loanType;
	}

	public void setLoanType(Integer loanType) {
		this.loanType = loanType;
	}

	@Column(name = "projectState")
	public Integer getProjectState() {
		return this.projectState;
	}

	public void setProjectState(Integer projectState) {
		this.projectState = projectState;
	}

	@Column(name = "person")
	public Integer getPerson() {
		return this.person;
	}

	public void setPerson(Integer person) {
		this.person = person;
	}

	@Column(name = "isUp")
	public Integer getIsUp() {
		return this.isUp;
	}

	public void setIsUp(Integer isUp) {
		this.isUp = isUp;
	}

	@Column(name = "haveOther")
	public Integer getHaveOther() {
		return this.haveOther;
	}

	public void setHaveOther(Integer haveOther) {
		this.haveOther = haveOther;
	}

	@Column(name = "address")
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "remark", length = 65535)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "loanimg", length = 200)
	public String getLoanimg() {
		return this.loanimg;
	}

	public void setLoanimg(String loanimg) {
		this.loanimg = loanimg;
	}

	@Column(name = "province", length = 20)
	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	@Column(name = "city", length = 20)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "proindustry", length = 50)
	public String getProindustry() {
		return this.proindustry;
	}

	public void setProindustry(String proindustry) {
		this.proindustry = proindustry;
	}

	@Column(name = "enteruptime1", length = 50)
	public String getEnteruptime1() {
		return this.enteruptime1;
	}

	public void setEnteruptime1(String enteruptime1) {
		this.enteruptime1 = enteruptime1;
	}

	@Column(name = "enteruptime2", length = 50)
	public String getEnteruptime2() {
		return this.enteruptime2;
	}

	public void setEnteruptime2(String enteruptime2) {
		this.enteruptime2 = enteruptime2;
	}

	@Column(name = "behoof", length = 300)
	public String getBehoof() {
		return behoof;
	}

	public void setBehoof(String behoof) {
		this.behoof = behoof;
	}


	@Column(name = "recommandReason", length = 300)
	public String getRecommandReason() {
		return recommandReason;
	}



	public void setRecommandReason(String recommandReason) {
		this.recommandReason = recommandReason;
	}


	@Column(name = "riskAdvice", length = 300)
	public String getRiskAdvice() {
		return riskAdvice;
	}



	public void setRiskAdvice(String riskAdvice) {
		this.riskAdvice = riskAdvice;
	}


}