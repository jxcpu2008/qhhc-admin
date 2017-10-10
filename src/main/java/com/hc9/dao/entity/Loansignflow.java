package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Loansignflow entity. @author frank
 */
@Entity
@Table(name = "loansignflow")
public class Loansignflow implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long flowid;//id
	private Long userDebt;//债权转让人
	private Long userAuth;//债权接手人
	private String shiftTime;//接手时间
	private Long loanId;//借款标id
	private Double tenderMoney;//原借款标金额
	private String loanCount;//借款标数量
	private Integer auditResult;//0"不通过",1"通过",2"待定"
	private Integer auditStatus;//审核状态1"未审核",2"正在审核",3"已审核"
	private Integer flowstate;//流转状态  1 未添加，2 已添加到债权转让里
	private Integer pcrettype;//转让状态 1：全部转让 2：部分转让
	private Integer share;  //转让份数
	private Integer distype;// 折扣
	private Double appreciation;//升值
	private Double discountMoney; //折扣金额
	private Long loansignId; //新标ID
	private Long userId;	//原融资人
	private Integer loanType;//1.优先，2夹层，3.劣后
	

	// Constructors

	/** default constructor */
	public Loansignflow() {
	}

	/** full constructor */
	public Loansignflow(Long flowid, Long userDebt, Long userAuth,
			String shiftTime, Long loanId, Double tenderMoney,
			String loanCount, Integer auditResult, Integer auditStatus,
			Integer flowstate, Integer pcrettype, Integer share,
			Integer distype, Double appreciation,Long loansignId,Long userId,Integer loanType) {
		super();
		this.flowid = flowid;
		this.userDebt = userDebt;
		this.userAuth = userAuth;
		this.shiftTime = shiftTime;
		this.loanId = loanId;
		this.tenderMoney = tenderMoney;
		this.loanCount = loanCount;
		this.auditResult = auditResult;
		this.auditStatus = auditStatus;
		this.flowstate = flowstate;
		this.pcrettype = pcrettype;
		this.share = share;
		this.distype = distype;
		this.appreciation = appreciation;
		this.loansignId = loansignId;
		this.userId=userId;
		this.loanType=loanType;
	}

	
	// Property accessors
	 @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "flowid", unique = true, nullable = false)
	public Long getId() {
		return this.flowid;
	}

	public void setId(Long flowid) {
		this.flowid = flowid;
	}

	@Column(name = "user_debt")
	public Long getUserDebt() {
		return this.userDebt;
	}

	public void setUserDebt(Long userDebt) {
		this.userDebt = userDebt;
	}

	@Column(name = "user_auth")
	public Long getUserAuth() {
		return this.userAuth;
	}

	public void setUserAuth(Long userAuth) {
		this.userAuth = userAuth;
	}

	@Column(name = "shift_time")
	public String getShiftTime() {
		return this.shiftTime;
	}

	public void setShiftTime(String shiftTime) {
		this.shiftTime = shiftTime;
	}

	@Column(name = "loan_id")
	public Long getLoanId() {
		return this.loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	@Column(name = "tenderMoney", precision = 18, scale = 4)
	public Double getTenderMoney() {
		return this.tenderMoney;
	}

	public void setTenderMoney(Double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}

	@Column(name = "loanCount")
	public String getLoanCount() {
		return this.loanCount;
	}

	public void setLoanCount(String loanCount) {
		this.loanCount = loanCount;
	}

	@Column(name = "auditResult")
	public Integer getAuditResult() {
		return this.auditResult;
	}

	public void setAuditResult(Integer auditResult) {
		this.auditResult = auditResult;
	}

	@Column(name = "auditStatus")
	public Integer getAuditStatus() {
		return this.auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}
	@Column(name = "flowstate")
	public Integer getFlowstate() {
		return flowstate;
	}

	public void setFlowstate(Integer flowstate) {
		this.flowstate = flowstate;
	}
	@Column(name = "appreciation")
	public Double getAppreciation() {
		return appreciation;
	}

	public void setAppreciation(Double appreciation) {
		this.appreciation = appreciation;
	}

	@Column(name = "pcrettype")
	public Integer getPcrettype() {
		return pcrettype;
	}

	public void setPcrettype(Integer pcrettype) {
		this.pcrettype = pcrettype;
	}

	@Column(name = "share")
	public Integer getShare() {
		return share;
	}

	public void setShare(Integer share) {
		this.share = share;
	}
	
	@Column(name = "distype")
	public Integer getDistype() {
		return distype;
	}

	public void setDistype(Integer distype) {
		this.distype = distype;
	}
	@Column(name = "discountMoney")
	public Double getDiscountMoney() {
		return discountMoney;
	}

	public void setDiscountMoney(Double discountMoney) {
		this.discountMoney = discountMoney;
	}
	@Column(name = "loanSign_id")
	public Long getLoansignId() {
		return loansignId;
	}

	public void setLoansignId(Long loansignId) {
		this.loansignId = loansignId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	@Column(name="loanType")
	public Integer getLoanType() {
		return loanType;
	}

	public void setLoanType(Integer loanType) {
		this.loanType = loanType;
	}


	
	
	

}