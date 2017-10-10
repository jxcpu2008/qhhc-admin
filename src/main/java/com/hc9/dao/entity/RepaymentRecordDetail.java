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
 * 还款记录详情实体类
 * repaymentrecordparticulars表记录的是融资人在标的的某一期对所有投资人应付还款的记录
 * @author Jerry Wong
 *
 */
@Entity
@Table(name = "repaymentrecordparticulars")
public class RepaymentRecordDetail implements java.io.Serializable {

	private static final long serialVersionUID = 4056999063291848112L;
	
	private Long id;
 	private Repaymentrecord repaymentrecord;
	private Userbasicsinfo userbasicsinfo;
	private Double money;
	private Double preRepayMoney;
	private Double realMoney;
	private Double middleMoney;
	private Double middlePreRepayMoney;
	private Double middleRealMoney;
	private Double afterMoney;
	private Double afterPreRepayMoney;
	private Double afterRealMoney;
	private String pipsBillNo;
	private String pipsTime1;
	private String pipsTime2;
	private Integer repState;
	private Integer loanType ;	// 投资类型 1 优先 2 夹层 3劣后
	private Loanrecord loanrecord;
	private Double fee;	// 还款手续费
	
	/** default constructor */
	public RepaymentRecordDetail() {
	}

	/** full constructor */
	public RepaymentRecordDetail(
			Loanrecord loanrecord,
			Repaymentrecord repaymentrecord,
			Userbasicsinfo userbasicsinfo, Double money, Double preRepayMoney,
			Double realMoney, Double middleMoney, Double middlePreRepayMoney,
			Double middleRealMoney, Double afterMoney,
			Double afterPreRepayMoney, Double afterRealMoney,
			String pipsBillNo, String pipsTime1, String pipsTime2,
			Integer repState) {
		this.loanrecord=loanrecord;
		this.repaymentrecord = repaymentrecord;
		this.userbasicsinfo = userbasicsinfo;
		this.money = money;
		this.preRepayMoney = preRepayMoney;
		this.realMoney = realMoney;
		this.middleMoney = middleMoney;
		this.middlePreRepayMoney = middlePreRepayMoney;
		this.middleRealMoney = middleRealMoney;
		this.afterMoney = afterMoney;
		this.afterPreRepayMoney = afterPreRepayMoney;
		this.afterRealMoney = afterRealMoney;
		this.pipsBillNo = pipsBillNo;
		this.pipsTime1 = pipsTime1;
		this.pipsTime2 = pipsTime2;
		this.repState = repState;
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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "loanrecordId")
	public Loanrecord getLoanrecord() {
		return loanrecord;
	}

	public void setLoanrecord(Loanrecord loanrecord) {
		this.loanrecord = loanrecord;
	}
	

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "repaymentrecordId")
	public Repaymentrecord getRepaymentrecord() {
		return this.repaymentrecord;
	}

	public void setRepaymentrecord(
			Repaymentrecord repaymentrecord) {
		this.repaymentrecord = repaymentrecord;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userId")
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	@Column(name = "money", precision = 18, scale = 4)
	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	@Column(name = "preRepayMoney", precision = 18, scale = 4)
	public Double getPreRepayMoney() {
		return this.preRepayMoney;
	}

	public void setPreRepayMoney(Double preRepayMoney) {
		this.preRepayMoney = preRepayMoney;
	}

	@Column(name = "realMoney", precision = 18, scale = 4)
	public Double getRealMoney() {
		return this.realMoney;
	}

	public void setRealMoney(Double realMoney) {
		this.realMoney = realMoney;
	}

	@Column(name = "middleMoney", precision = 18, scale = 4)
	public Double getMiddleMoney() {
		return this.middleMoney;
	}

	public void setMiddleMoney(Double middleMoney) {
		this.middleMoney = middleMoney;
	}

	@Column(name = "middlePreRepayMoney", precision = 18, scale = 4)
	public Double getMiddlePreRepayMoney() {
		return this.middlePreRepayMoney;
	}

	public void setMiddlePreRepayMoney(Double middlePreRepayMoney) {
		this.middlePreRepayMoney = middlePreRepayMoney;
	}

	@Column(name = "middleRealMoney", precision = 10, scale = 4)
	public Double getMiddleRealMoney() {
		return this.middleRealMoney;
	}

	public void setMiddleRealMoney(Double middleRealMoney) {
		this.middleRealMoney = middleRealMoney;
	}

	@Column(name = "afterMoney", precision = 10, scale = 4)
	public Double getAfterMoney() {
		return this.afterMoney;
	}

	public void setAfterMoney(Double afterMoney) {
		this.afterMoney = afterMoney;
	}

	@Column(name = "afterPreRepayMoney", precision = 10, scale = 4)
	public Double getAfterPreRepayMoney() {
		return this.afterPreRepayMoney;
	}

	public void setAfterPreRepayMoney(Double afterPreRepayMoney) {
		this.afterPreRepayMoney = afterPreRepayMoney;
	}

	@Column(name = "afterRealMoney", precision = 10, scale = 4)
	public Double getAfterRealMoney() {
		return this.afterRealMoney;
	}

	public void setAfterRealMoney(Double afterRealMoney) {
		this.afterRealMoney = afterRealMoney;
	}

	@Column(name = "pIpsBillNo", length = 30)
	public String getPipsBillNo() {
		return this.pipsBillNo;
	}

	public void setPipsBillNo(String pipsBillNo) {
		this.pipsBillNo = pipsBillNo;
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

	@Column(name = "repState")
	public Integer getRepState() {
		return this.repState;
	}

	public void setRepState(Integer repState) {
		this.repState = repState;
	}
	
	@Column(name = "loanType")
	public Integer getLoanType() {
		return loanType;
	}

	public void setLoanType(Integer loanType) {
		this.loanType = loanType;
	}
	
	@Column(name = "fee")
	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}
}