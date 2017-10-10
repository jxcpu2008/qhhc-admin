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

import org.codehaus.jackson.annotate.JsonIgnore;

/***
 * 第三方担保流水表
 * @author LKL
 *
 */
@Entity
@Table(name = "escrow_accountinfo")
public class EscrowAccountinfo implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	/**支出金额*/
	private Double expenditure;
	/**说明*/
	private String explan;
	/**收入金额*/
	private Double income;
	/**当前余额*/
	private Double money;
	/**操作时间*/
	private String time;
	/**操作订单号*/
	private String ipsNumber;
	/**手续费*/
	private Double fee;
	/**类型 1-充值  2-提现*/
	private Integer isRechargeWithdraw;
	/**第三方担保*/
	private Escrow escrow;
	
	public EscrowAccountinfo() {
	}

	public EscrowAccountinfo(Long id, Double expenditure, String explan,
			Double income, Double money, String time, String ipsNumber,
			Double fee, Integer isRechargeWithdraw, Escrow escrow) {
		super();
		this.id = id;
		this.expenditure = expenditure;
		this.explan = explan;
		this.income = income;
		this.money = money;
		this.time = time;
		this.ipsNumber = ipsNumber;
		this.fee = fee;
		this.isRechargeWithdraw = isRechargeWithdraw;
		this.escrow = escrow;
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "expenditure")
	public Double getExpenditure() {
		return expenditure;
	}

	public void setExpenditure(Double expenditure) {
		this.expenditure = expenditure;
	}
	@Column(name = "explan")
	public String getExplan() {
		return explan;
	}

	public void setExplan(String explan) {
		this.explan = explan;
	}
	@Column(name = "income")
	public Double getIncome() {
		return income;
	}

	public void setIncome(Double income) {
		this.income = income;
	}
	@Column(name = "money")
	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}
	@Column(name = "time")
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	@Column(name = "ipsNumber")
	public String getIpsNumber() {
		return ipsNumber;
	}

	public void setIpsNumber(String ipsNumber) {
		this.ipsNumber = ipsNumber;
	}
	@Column(name = "fee")
	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}
	@Column(name = "is_recharge_withdraw")
	public Integer getIsRechargeWithdraw() {
		return isRechargeWithdraw;
	}

	public void setIsRechargeWithdraw(Integer isRechargeWithdraw) {
		this.isRechargeWithdraw = isRechargeWithdraw;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "escrow_id")
	@JsonIgnore
	public Escrow getEscrow() {
		return escrow;
	}

	public void setEscrow(Escrow escrow) {
		this.escrow = escrow;
	}
	
	

}
