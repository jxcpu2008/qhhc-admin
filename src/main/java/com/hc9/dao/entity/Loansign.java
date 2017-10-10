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
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Loansign entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "loansign")
public class Loansign implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Loansignbasics loansignbasics;
	private Userbasicsinfo userbasicsinfo;
	/***
	 * 项目类型
	 */
	private LoansignType loansignType;
	/**项目名称*/
	private String name;
	/**项目总额*/
	private Double issueLoan;
	/**剩余金额*/
	private Double restMoney;
	/**优先金额*/
	private Double priority;
	/**优先百分比*/
	private Double prioRate;
	/**优先奖励*/
	private Double prioAwordRate;
	/**优先剩余金额*/
	private Double prioRestMoney;
	/**夹层金额*/
	private Double middle;
	/**夹层百分比*/
	private Double midRate;
	/**夹层剩余金额*/
	private Double midRestMoney;
	/**劣后*/
	private Double after;
	/**劣后百分比*/
	private Double afterRate;
	/**劣后剩余金额*/
	private Double afterRestMoney;
	/**最低投资金额*/
	private Integer loanUnit;
	/**发布时间*/
	private String publishTime;
	/**放款时间*/
	private String creditTime;
	/**拨款方式*/
	private Integer appropriation;
	/**分红方式 1.按月 2.按季度*/
	private Integer refunway;
	/**热门0不推荐   1推荐*/
	private Integer recommend;
	/**标签*/
	private String tag;
	/**是否为债权转让*/
	private Integer isdet;
	/**初审状态 0未提交  1审核中 2审核通过 3审核不通过*/
	private Integer state;
	/**-1 清盘成功 0未发布 1 进行中 2 融资成功 3-满标审批 4-放款待审批   5-放款待审核   6-已放款  7-还款中 8 已完成   9-流标*/
	private Integer status;
	/**项目审核人员*/
	private Long adminuserId;
	/**项目编号*/
	private String contractNo;
	/**0 未转让 1 转让成功*/
	private Integer transfer;
	/**1-店铺  2-项目 3-天标 4-债权转让*/
	private Integer type;
	/**债权转让*/
	private Integer outDay;
	/**回购期限*/
	private Integer remonth;
	/**借款有效期*/
	private Integer validity;
	/***
	 * 创建时间
	 */
	private String createTime;
	
	/**
	 * 标号，宝付用
	 */
	private String cus_id;
	/***
	 * 审核不通过时间
	 */
	private String examineTime;
	
	/***
	 * 项目满标审批时间
	 */
	private String approvalTime;
	
	/***
	 * 项目满标审批人
	 */
	private Long approvalAdmin;
	
	/***
	 * 项目放款财务审核人
	 */
	private String auditAdmin;
	
	/***
	 * 项目放款财务审核不通过时间
	 */
	private String auditTime;
	
	/**
	 * 给借款人的实际借款利率
	 */
	private Double realRate;
	
	/***
	 * 放款手续费
	 */
	private Double fee;
	
	/**公司服务费年利率**/
	private Double  companyFee;
	/***
	 * 放款流标的ID
	 */
	private String orderSn;
	
	/****
	 * 收取平台手续费  1-收取 2-未收取
	 */
	private Integer feeState;
	
	/***
	 * 满标时间
	 */
	private String fullTime;
	
	/***
	 * 总平台服务费
	 */
	private Double feeMoney;
	/***
	 * 收取平台服务费Id
	 */
	private Long adminFee;
	
	/***
	 * 项目期数
	 */
	private String loanPeriods;
	
	/**
	 * 推荐首页0 不出，1出现
	 */
	private Integer onIndex;
	
	/**
	 * 项目号
	 */
	private String projectNumber; 
	
	/** 使用红包金额 */
	private Double redEnvelopeMoney=0d;
	
	/**
	 *活动状态 0-不参与 1-参与
	 ***/
	private Integer activityStatus=0;
	
	/***
	 * 手续费收取： 1、本金中扣除 ；2、额外支付
	 */
	private Integer feeMethod=1;
	
	
	private List<Repaymentrecord> repaymentrecords = new ArrayList<Repaymentrecord>(
			0);
	private List<Loanrecord> loanrecords = new ArrayList<Loanrecord>(0);
	private List<Attachment> attachments = new ArrayList<Attachment>(0);
	private List<LoansignAttach> loansignAttachs = new ArrayList<LoansignAttach>(0);
	private List<LoansignComment> comments = new ArrayList<LoansignComment>(0);
	private List<LoansignCollected> collectrecords = new ArrayList<LoansignCollected>(0);
	/**
	 * 担保*/
	private Escrow escrow;
	// Constructors

	/** default constructor */
	public Loansign() {
	}
	public Loansign(Long id) {
		this.id=id;
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
	public Loansignbasics getLoansignbasics() {
		return this.loansignbasics;
	}

	public void setLoansignbasics(Loansignbasics loansignbasics) {
		this.loansignbasics = loansignbasics;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userbasicinfo_id")
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loansignType_id")
	public LoansignType getLoansignType() {
		return loansignType;
	}
	public void setLoansignType(LoansignType loansignType) {
		this.loansignType = loansignType;
	}
	@Column(name = "name", length = 20)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "issueLoan", precision = 20, scale = 4)
	public Double getIssueLoan() {
		return this.issueLoan;
	}

	public void setIssueLoan(Double issueLoan) {
		this.issueLoan = issueLoan;
	}

	@Column(name = "rest_money", precision = 20, scale = 4)
	public Double getRestMoney() {
		return this.restMoney;
	}

	public void setRestMoney(Double restMoney) {
		this.restMoney = restMoney;
	}

	@Column(name = "priority", precision = 20, scale = 4)
	public Double getPriority() {
		return this.priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;
	}

	@Column(name = "prio_rate", precision = 10, scale = 4)
	public Double getPrioRate() {
		return this.prioRate;
	}

	public void setPrioRate(Double prioRate) {
		this.prioRate = prioRate;
	}

	@Column(name = "prio_aword_rate", precision = 10, scale = 4)
	public Double getPrioAwordRate() {
		return this.prioAwordRate;
	}

	public void setPrioAwordRate(Double prioAwordRate) {
		this.prioAwordRate = prioAwordRate;
	}

	@Column(name = "prio_rest_money", precision = 20, scale = 4)
	public Double getPrioRestMoney() {
		return this.prioRestMoney;
	}

	public void setPrioRestMoney(Double prioRestMoney) {
		this.prioRestMoney = prioRestMoney;
	}

	@Column(name = "middle", precision = 20, scale = 4)
	public Double getMiddle() {
		return this.middle;
	}

	public void setMiddle(Double middle) {
		this.middle = middle;
	}

	@Column(name = "mid_rate", precision = 10, scale = 4)
	public Double getMidRate() {
		return this.midRate;
	}

	public void setMidRate(Double midRate) {
		this.midRate = midRate;
	}

	@Column(name = "mid_rest_money", precision = 20, scale = 4)
	public Double getMidRestMoney() {
		return this.midRestMoney;
	}

	public void setMidRestMoney(Double midRestMoney) {
		this.midRestMoney = midRestMoney;
	}

	@Column(name = "after", precision = 20, scale = 4)
	public Double getAfter() {
		return this.after;
	}

	public void setAfter(Double after) {
		this.after = after;
	}

	@Column(name = "after_rate", precision = 10, scale = 4)
	public Double getAfterRate() {
		return this.afterRate;
	}

	public void setAfterRate(Double afterRate) {
		this.afterRate = afterRate;
	}

	@Column(name = "after_rest_money", precision = 20, scale = 4)
	public Double getAfterRestMoney() {
		return this.afterRestMoney;
	}

	public void setAfterRestMoney(Double afterRestMoney) {
		this.afterRestMoney = afterRestMoney;
	}

	@Column(name = "loanUnit")
	public Integer getLoanUnit() {
		return this.loanUnit;
	}

	public void setLoanUnit(Integer loanUnit) {
		this.loanUnit = loanUnit;
	}

	@Column(name = "publish_time", length = 50)
	public String getPublishTime() {
		return this.publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	@Column(name = "credit_time", length = 50)
	public String getCreditTime() {
		return this.creditTime;
	}

	public void setCreditTime(String creditTime) {
		this.creditTime = creditTime;
	}

	@Column(name = "appropriation")
	public Integer getAppropriation() {
		return this.appropriation;
	}

	public void setAppropriation(Integer appropriation) {
		this.appropriation = appropriation;
	}

	@Column(name = "refunway")
	public Integer getRefunway() {
		return this.refunway;
	}

	public void setRefunway(Integer refunway) {
		this.refunway = refunway;
	}

	@Column(name = "recommend")
	public Integer getRecommend() {
		return this.recommend;
	}

	public void setRecommend(Integer recommend) {
		this.recommend = recommend;
	}

	@Column(name = "tag", length = 50)
	public String getTag() {
		return this.tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Column(name = "isdet")
	public Integer getIsdet() {
		return this.isdet;
	}

	public void setIsdet(Integer isdet) {
		this.isdet = isdet;
	}

	@Column(name = "state")
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "adminuser_id")
	public Long getAdminuserId() {
		return this.adminuserId;
	}

	public void setAdminuserId(Long adminuserId) {
		this.adminuserId = adminuserId;
	}

	@Column(name = "contractNo", length = 200)
	public String getContractNo() {
		return this.contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	@Column(name = "transfer")
	public Integer getTransfer() {
		return this.transfer;
	}

	public void setTransfer(Integer transfer) {
		this.transfer = transfer;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "outDay")
	public Integer getOutDay() {
		return this.outDay;
	}

	public void setOutDay(Integer outDay) {
		this.outDay = outDay;
	}

	@Column(name = "remonth")
	public Integer getRemonth() {
		return this.remonth;
	}

	public void setRemonth(Integer remonth) {
		this.remonth = remonth;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loansign")
	public List<Repaymentrecord> getRepaymentrecords() {
		return this.repaymentrecords;
	}

	public void setRepaymentrecords(List<Repaymentrecord> repaymentrecords) {
		this.repaymentrecords = repaymentrecords;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loansign")
	public List<Loanrecord> getLoanrecords() {
		return this.loanrecords;
	}

	public void setLoanrecords(List<Loanrecord> loanrecords) {
		this.loanrecords = loanrecords;
	}




	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loansign")
	public List<Attachment> getAttachments() {
		return this.attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loansign")
	public List<LoansignAttach> getLoansignAttachs() {
		return this.loansignAttachs;
	}

	public void setLoansignAttachs(List<LoansignAttach> loansignAttachs) {
		this.loansignAttachs = loansignAttachs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loansign")
	public List<LoansignComment> getComments() {
		return this.comments;
	}

	public void setComments(List<LoansignComment> comments) {
		this.comments = comments;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loansign")
	public List<LoansignCollected> getCollectrecords() {
		return this.collectrecords;
	}
	
	public void setCollectrecords(List<LoansignCollected> collectrecords) {
		this.collectrecords = collectrecords;
	}
	@ManyToOne()
	@JoinColumn(name = "escrow_id")
	public Escrow getEscrow() {
		return escrow;
	}
	public void setEscrow(Escrow escrow) {
		this.escrow = escrow;
	}
	@Column(name = "examine_time")
	public String getExamineTime() {
		return examineTime;
	}
	public void setExamineTime(String examineTime) {
		this.examineTime = examineTime;
	}
	
	@Column(name = "create_time")
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	@Column(name = "validity")
	public Integer getValidity() {
		return validity;
	}
	public void setValidity(Integer validity) {
		this.validity = validity;
	}
	
	@Column(name = "cus_id", nullable = false)
	public String getCus_id() {
		return cus_id;
	}

	public void setCus_id(String cus_id) {
		this.cus_id = cus_id;
	}
	@Column(name = "approval_time")
	public String getApprovalTime() {
		return approvalTime;
	}
	public void setApprovalTime(String approvalTime) {
		this.approvalTime = approvalTime;
	}
	
	@Column(name = "approval_admin")
	public Long getApprovalAdmin() {
		return approvalAdmin;
	}
	public void setApprovalAdmin(Long approvalAdmin) {
		this.approvalAdmin = approvalAdmin;
	}
	
	@Column(name = "audit_admin")
	public String getAuditAdmin() {
		return auditAdmin;
	}
	public void setAuditAdmin(String auditAdmin) {
		this.auditAdmin = auditAdmin;
	}
	
	@Column(name = "audit_time")
	public String getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}
	
	@Column(name = "real_rate")
	public Double getRealRate() {
		return realRate;
	}
	public void setRealRate(Double realRate) {
		this.realRate = realRate;
	}
	
	@Column(name = "order_sn")
	public String getOrderSn() {
		return orderSn;
	}
	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}
	
	@Column(name = "fee")
	public Double getFee() {
		return fee;
	}
	public void setFee(Double fee) {
		this.fee = fee;
	}
	@Column(name = "companyFee")
	public Double getCompanyFee() {
		return companyFee;
	}
	public void setCompanyFee(Double companyFee) {
		this.companyFee = companyFee;
	}
	
	@Column(name = "feeState")
	public Integer getFeeState() {
		return feeState;
	}
	public void setFeeState(Integer feeState) {
		this.feeState = feeState;
	}
	
	@Column(name = "admin_Fee")
	public Long getAdminFee() {
		return adminFee;
	}
	public void setAdminFee(Long adminFee) {
		this.adminFee = adminFee;
	}
	
	@Column(name = "feeMoney")
	public Double getFeeMoney() {
		return feeMoney;
	}
	public void setFeeMoney(Double feeMoney) {
		this.feeMoney = feeMoney;
	}
	
	@Column(name = "full_time")
	public String getFullTime() {
		return fullTime;
	}
	public void setFullTime(String fullTime) {
		this.fullTime = fullTime;
	}
	
	@Column(name = "loanPeriods")
	public String getLoanPeriods() {
		return loanPeriods;
	}
	public void setLoanPeriods(String loanPeriods) {
		this.loanPeriods = loanPeriods;
	}
	
	@Column(name = "projectNumber")
	public String getProjectNumber() {
		return projectNumber;
	}
	
	public void setProjectNumber(String projectNumber) {
		this.projectNumber = projectNumber;
	}
	
	@Column(name = "onIndex")
	public Integer getOnIndex() {
		return onIndex;
	}
	public void setOnIndex(Integer onIndex) {
		this.onIndex = onIndex;
	}
	
	@Column(name = "redEnvelopeMoney")
	public Double getRedEnvelopeMoney() {
		return redEnvelopeMoney;
	}
	
	public void setRedEnvelopeMoney(Double redEnvelopeMoney) {
		this.redEnvelopeMoney = redEnvelopeMoney;
	}
	
	@Column(name = "activityStatus")
	public Integer getActivityStatus() {
		return activityStatus;
	}
	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}
	@Column(name = "feeMethod")
	public Integer getFeeMethod() {
		return feeMethod;
	}
	public void setFeeMethod(Integer feeMethod) {
		this.feeMethod = feeMethod;
	}
	
	
}