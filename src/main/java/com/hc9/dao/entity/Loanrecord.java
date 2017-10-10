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
 * Loanrecord
 */
@Entity
@Table(name = "loanrecord")
public class Loanrecord implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	/**
     * 
     */
	private Long id;
	/**
	 * 购买借款标信息
	 */
	private Loansign loansign;
	
	
	
	/**
	 * 投标单号
	 */
	private String	order_id;
	/**
	 * 会员基本信息
	 */
	private Userbasicsinfo userbasicsinfo;
	/**
	 * 购买是否成功
	 */
	private Integer isSucceed;
	/**
	 * 购买金额
	 */
	private Double tenderMoney;
	/**
	 * 购买时间
	 */
	private String tenderTime;
	/**
	 * 购买时是否是特权会员（0不是,1.是）
	 */
	private Integer isPrivilege;

	/**
	 * 预计管理费
	 */
	private Double fee;
	// Constructors

	/** 使用红包金额 */
	private Double redEnvelopeMoney=0d;
	
	// 投资类型
	private Integer loanType;
	
	/***
	 * 更新时间
	 */
     private  String updateTime;	
     
     /***
      * 项目合同编号
      */
     private String pContractNo;
     
     /****
      * 0-默认  1-优先转夹层   2-夹层转优先
      */
     private Integer isType;
     
     /***
      *  投标类型: 1-优先，2-夹层，3-列后 4-vip众筹，5-股东众筹，
      */
     private Integer subType;
     
     /***
      * 差额
      */
     private  Double subMoney;
     
     /***
      * 投标模式  1-web   2-app    5-自动投标
      */
     private Integer webOrApp;

	/** default constructor */
	public Loanrecord() {
	}

	/** full constructor */
	/**
	 * 
	 * @param loansign
	 *            借款标信息
	 * @param userbasicsinfo
	 *            会员基本信息
	 * @param isSucceed
	 *            是否购买成功
	 * @param tenderMoney
	 *            购买金额
	 * @param tenderTime
	 *            购买时间
	 * @param isPrivilege
	 *            是否是特权会员
	 */
	public Loanrecord(Loansign loansign, Userbasicsinfo userbasicsinfo,
			Integer isSucceed, Double tenderMoney, String tenderTime,
			Integer isPrivilege, Double fee) {
		this.loansign = loansign;
		this.userbasicsinfo = userbasicsinfo;
		this.isSucceed = isSucceed;
		this.tenderMoney = tenderMoney;
		this.tenderTime = tenderTime;
		this.isPrivilege = isPrivilege;
		this.fee = fee;
	}

	// Property accessors
	/**
	 * 
	 * @return id
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	/**
	 * 
	 * @param id
	 *            id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	
	
	
	@Column(name = "order_id")
	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	/**
	 * loansign
	 * 
	 * @return loansign
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loanSign_id")
	public Loansign getLoansign() {
		return this.loansign;
	}

	/**
	 * 
	 * @param loansign
	 *            loansign
	 */
	public void setLoansign(Loansign loansign) {
		this.loansign = loansign;
	}

	/**
	 * 
	 * @return userbasicsinfo
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userbasicinfo_id")
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	/**
	 * 
	 * @param userbasicsinfo
	 *            userbasicsinfo
	 */
	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	/**
	 * 
	 * @return isSucceed
	 */
	@Column(name = "isSucceed")
	public Integer getIsSucceed() {
		return this.isSucceed;
	}

	/**
	 * 
	 * @param isSucceed
	 *            isSucceed
	 */
	public void setIsSucceed(Integer isSucceed) {
		this.isSucceed = isSucceed;
	}

	/**
	 * tenderMoney
	 * 
	 * @return tenderMoney
	 */
	@Column(name = "tenderMoney", precision = 18, scale = 4)
	public Double getTenderMoney() {
		return this.tenderMoney;
	}

	/**
	 * 
	 * @param tenderMoney
	 *            tenderMoney
	 */
	public void setTenderMoney(Double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}

	/**
	 * 
	 * @return tenderTime
	 */
	@Column(name = "tenderTime", length = 32)
	public String getTenderTime() {
		return this.tenderTime;
	}

	/**
	 * 
	 * @param tenderTime
	 *            tenderTime
	 */
	public void setTenderTime(String tenderTime) {
		this.tenderTime = tenderTime;
	}

	/**
	 * 
	 * @return isPrivilege
	 */
	@Column(name = "isPrivilege")
	public Integer getIsPrivilege() {
		return this.isPrivilege;
	}

	/**
	 * 
	 * @param isPrivilege
	 *            isPrivilege
	 */
	public void setIsPrivilege(Integer isPrivilege) {
		this.isPrivilege = isPrivilege;
	}

	/**
	 * 
	 * @return pMerBillNo
	 */
	@Column(name = "fee", length = 30)
	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	@Column(name = "redEnvelopeMoney")
	public Double getRedEnvelopeMoney() {
		return redEnvelopeMoney;
	}

	public void setRedEnvelopeMoney(Double redEnvelopeMoney) {
		this.redEnvelopeMoney = redEnvelopeMoney;
	}
	
	public Integer getLoanType() {
		return loanType;
	}

	public void setLoanType(Integer loanType) {
		this.loanType = loanType;
	}

	@Column(name = "updateTime")
	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "pContractNo")
	public String getpContractNo() {
		return pContractNo;
	}

	public void setpContractNo(String pContractNo) {
		this.pContractNo = pContractNo;
	}

	@Column(name = "isType")
	public Integer getIsType() {
		return isType;
	}

	public void setIsType(Integer isType) {
		this.isType = isType;
	}

	@Column(name = "subType")
	public Integer getSubType() {
		return subType;
	}

	public void setSubType(Integer subType) {
		this.subType = subType;
	}

	@Column(name = "subMoney")
	public Double getSubMoney() {
		return subMoney;
	}

	public void setSubMoney(Double subMoney) {
		this.subMoney = subMoney;
	}

	@Column(name = "webOrApp")
	public Integer getWebOrApp() {
		return webOrApp;
	}

	public void setWebOrApp(Integer webOrApp) {
		this.webOrApp = webOrApp;
	}

	
	

}