package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Costratio 费用比例
 */
@Entity
@Table(name = "costratio")
public class Costratio implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private Long id;
	/**充值手续费*/
	private Double recharge;
	
	/**提现手续费*/
	private Double withdraw;
	
	/**升级vip费用*/
	private Double vipUpgrade;
	
	/**实名认证费用*/
	private Double nameAuth;
	
	/**逾期滞纳金比例*/
	private Double overdueRepayment;
	
	/**众持借款人手续费*/
	private Double borrowerFee;
	
	/**优先利息管理费*/
	private Double prioInvest;
	
	/**夹层分红管理费*/
	private Double midInvest;
	
	/**劣后分红管理费*/
	private Double afterInvest;
	
	/**店铺融资服务费*/
	private Double shopOwerFee;
	
	/**店铺分红*/
	private Double shopDividends;
	
	/**股权转让费*/
	private Double conveyFee;
	
	/**充值手续费收取方式 1-平台支付 2-用户支付*/
	private Integer rechargeMethod;
	
	/**提现手续费收取方式 1-平台支付 2-用户支付*/
	private Integer withdrawMethod;
	
	/**投资手续费*/
	private Double loanInvestment;
	
	/**基金经理年利率提成*/
	private Double business;
	
	/**普通会员年利率提成*/
	private Double member;
	
	/***
	 * 每次还款的人数
	 */
	private Integer repayNum;
	
	/**第三方担保管理费*/
	private Double voucherFee;
	/**第三方担保充值手续费*/
	private Double escrowRecharge;
	/**第三方担保提现手续费*/
	private Double escrowWithdraw;
	/**第三方担保充值手续费收取方式 1-平台支付 2-用户支付*/
	private Integer escrowRechargeMethod;
	/**第三方担保提现手续费收取方式 1-平台支付 2-用户支付*/
	private Integer escrowWithdrawMethod;
	/**清盘天数**/
	private Integer windingNum;
	
	/**满标短信通知**/
	private String bidPhone;
	
	/**夹层利率**/
	private Double middleRate;
	
	/***
	 * 提现手续费
	 */
	private Double withdrawRate;
	
	/***
	 * 提现收费起始时间
	 */
	private String timeDate;
	
	/**
	 * 注册奖励金额
	 */
	private Double regBonu;
	
	/***
	 * 普通用户佣金启用状态    1-开 2-关
	 */
	private Integer generalizeState;
	
	/***
	 * 奖励启用状态    1-开 2-关
	 */
	private Integer bonusState;
	
	/***
	 * 红包奖励开关 1-开 2-关
	 */
	private Integer redState;
	
	/***
	 * 红筹币利率
	 */
	private Double hccoinRate;
	
	/***
	 * 活动起始时间
	 */
	private String starTime;
	
	/***
	 * 活动结束时间
	 */
	private String endTime;
	
	/***
	 * 等额等息逾期利率
	 */
	private Double matchingInterest;
	
	/***
	 * 理财师利率
	 */
	private Double financial;

	// Constructors

	/** default constructor */
	public Costratio() {
	}


	public Costratio(Long id, Double recharge, Double withdraw,
			Double vipUpgrade, Double nameAuth, Double overdueRepayment,
			Double borrowerFee, Double prioInvest, Double midInvest,
			Double afterInvest, Double shopOwerFee, Double shopDividends,
			Double conveyFee, Integer rechargeMethod, Integer withdrawMethod,
			Double loanInvestment) {
		this.id = id;
		this.recharge = recharge;
		this.withdraw = withdraw;
		this.vipUpgrade = vipUpgrade;
		this.nameAuth = nameAuth;
		this.overdueRepayment = overdueRepayment;
		this.borrowerFee = borrowerFee;
		this.prioInvest = prioInvest;
		this.midInvest = midInvest;
		this.afterInvest = afterInvest;
		this.shopOwerFee = shopOwerFee;
		this.shopDividends = shopDividends;
		this.conveyFee = conveyFee;
		this.rechargeMethod = rechargeMethod;
		this.withdrawMethod = withdrawMethod;
		this.loanInvestment = loanInvestment;
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
	/**
	 * 充值手续费
	 * @return
	 */
	@Column(name = "recharge", precision = 22, scale = 0)
	public Double getRecharge() {
		return this.recharge;
	}
	
	/**
	 * 充值手续费
	 * @return
	 */
	public void setRecharge(Double recharge) {
		this.recharge = recharge;
	}
	
	/**
	 * 提现手续费
	 * @return
	 */
	@Column(name = "withdraw", precision = 22, scale = 0)
	public Double getWithdraw() {
		return this.withdraw;
	}

	/**
	 * 提现手续费
	 * @return
	 */
	public void setWithdraw(Double withdraw) {
		this.withdraw = withdraw;
	}

	/**
	 * 升级vip费用
	 * @return
	 */
	@Column(name = "vip_upgrade", precision = 22, scale = 0)
	public Double getVipUpgrade() {
		return this.vipUpgrade;
	}

	/**
	 * 升级vip费用
	 * @return
	 */
	public void setVipUpgrade(Double vipUpgrade) {
		this.vipUpgrade = vipUpgrade;
	}

	/**
	 * 实名认证费用
	 * @return
	 */
	@Column(name = "nameAuth")
	public Double getNameAuth() {
		return this.nameAuth;
	}
	/**
	 * 实名认证费用
	 * @return
	 */
	public void setNameAuth(Double nameAuth) {
		this.nameAuth = nameAuth;
	}

	/**
	 * 逾期滞纳金比例
	 * @return
	 */
	@Column(name = "overdueRepayment", precision = 22, scale = 0)
	public Double getOverdueRepayment() {
		return this.overdueRepayment;
	}
	/**
	 * 逾期滞纳金比例
	 * @return
	 */
	public void setOverdueRepayment(Double overdueRepayment) {
		this.overdueRepayment = overdueRepayment;
	}
	/**
	 * 众持借款人手续费
	 * @return
	 */
	@Column(name = "borrowerFee", precision = 22, scale = 0)
	public Double getBorrowerFee() {
		return this.borrowerFee;
	}
	
	/**
	 * 众持借款人手续费
	 * @return
	 */
	public void setBorrowerFee(Double borrowerFee) {
		this.borrowerFee = borrowerFee;
	}
	/**
	 * 优先利息管理费
	 * @return
	 */
	@Column(name = "prioInvest", precision = 22, scale = 0)
	public Double getPrioInvest() {
		return this.prioInvest;
	}
	/**
	 * 优先利息管理费
	 * @return
	 */
	public void setPrioInvest(Double prioInvest) {
		this.prioInvest = prioInvest;
	}
	/**
	 * 夹层分红管理费
	 * @return
	 */
	@Column(name = "midInvest", precision = 22, scale = 0)
	public Double getMidInvest() {
		return this.midInvest;
	}
	/**
	 * 夹层分红管理费
	 * @param midInvest
	 */
	public void setMidInvest(Double midInvest) {
		this.midInvest = midInvest;
	}
	/**
	 * 劣后分红管理费
	 * @return
	 */
	@Column(name = "afterInvest", precision = 22, scale = 0)
	public Double getAfterInvest() {
		return this.afterInvest;
	}
	/**
	 * 劣后分红管理费
	 * @param afterInvest
	 */
	public void setAfterInvest(Double afterInvest) {
		this.afterInvest = afterInvest;
	}
	/**
	 * 店铺融资服务费
	 * @return
	 */
	@Column(name = "shopOwerFee", precision = 22, scale = 0)
	public Double getShopOwerFee() {
		return this.shopOwerFee;
	}
	/**
	 * 店铺融资服务费
	 * @param shopOwerFee
	 */
	public void setShopOwerFee(Double shopOwerFee) {
		this.shopOwerFee = shopOwerFee;
	}
	/**
	 * 店铺分红
	 * @return
	 */
	@Column(name = "shopDividends", precision = 22, scale = 0)
	public Double getShopDividends() {
		return this.shopDividends;
	}
	/**
	 * 店铺分红
	 * @param shopDividends
	 */
	public void setShopDividends(Double shopDividends) {
		this.shopDividends = shopDividends;
	}
	/**
	 * 股权转让费
	 * @return
	 */
	@Column(name = "conveyFee", precision = 22, scale = 0)
	public Double getConveyFee() {
		return this.conveyFee;
	}
	/**
	 * 股权转让费
	 * @param conveyFee
	 */
	public void setConveyFee(Double conveyFee) {
		this.conveyFee = conveyFee;
	}
	/**
	 * 充值IPS手续费收取方式 1-平台支付 2-用户支付
	 * @return
	 */
	@Column(name = "rechargeMethod")
	public Integer getRechargeMethod() {
		return this.rechargeMethod;
	}
	/**
	 * 充值IPS手续费收取方式 1-平台支付 2-用户支付
	 * @return
	 */
	public void setRechargeMethod(Integer rechargeMethod) {
		this.rechargeMethod = rechargeMethod;
	}
	/**
	 * 提现IPS手续费收取方式 1-平台支付 2-用户支付
	 * @param rechargeMethod
	 */
	@Column(name = "withdrawMethod")
	public Integer getWithdrawMethod() {
		return this.withdrawMethod;
	}
	/**
	 * 提现IPS手续费收取方式 1-平台支付 2-用户支付
	 * @param rechargeMethod
	 */
	public void setWithdrawMethod(Integer withdrawMethod) {
		this.withdrawMethod = withdrawMethod;
	}
	@Column(name = "loanInvestment")
	public Double getLoanInvestment() {
		return loanInvestment;
	}

	public void setLoanInvestment(Double loanInvestment) {
		this.loanInvestment = loanInvestment;
	}

	@Column(name = "business")
	public Double getBusiness() {
		return business;
	}


	public void setBusiness(Double business) {
		this.business = business;
	}

	@Column(name = "repayNum")
	public Integer getRepayNum() {
		return repayNum;
	}

	public void setRepayNum(Integer repayNum) {
		this.repayNum = repayNum;
	}

	@Column(name = "voucherFee")
	public Double getVoucherFee() {
		return voucherFee;
	}

	public void setVoucherFee(Double voucherFee) {
		this.voucherFee = voucherFee;
	}

	@Column(name = "escrowRecharge")
	public Double getEscrowRecharge() {
		return escrowRecharge;
	}


	public void setEscrowRecharge(Double escrowRecharge) {
		this.escrowRecharge = escrowRecharge;
	}

	@Column(name = "escrowWithdraw")
	public Double getEscrowWithdraw() {
		return escrowWithdraw;
	}


	public void setEscrowWithdraw(Double escrowWithdraw) {
		this.escrowWithdraw = escrowWithdraw;
	}

	@Column(name = "escrowRechargeMethod")
	public Integer getEscrowRechargeMethod() {
		return escrowRechargeMethod;
	}


	public void setEscrowRechargeMethod(Integer escrowRechargeMethod) {
		this.escrowRechargeMethod = escrowRechargeMethod;
	}

	@Column(name = "escrowWithdrawMethod")
	public Integer getEscrowWithdrawMethod() {
		return escrowWithdrawMethod;
	}


	public void setEscrowWithdrawMethod(Integer escrowWithdrawMethod) {
		this.escrowWithdrawMethod = escrowWithdrawMethod;
	}

	@Column(name = "windingNum")
	public Integer getWindingNum() {
		return windingNum;
	}


	public void setWindingNum(Integer windingNum) {
		this.windingNum = windingNum;
	}

	@Column(name = "member")
	public Double getMember() {
		return member;
	}


	public void setMember(Double member) {
		this.member = member;
	}

	@Column(name = "bidPhone")
	public String getBidPhone() {
		return bidPhone;
	}

	public void setBidPhone(String bidPhone) {
		this.bidPhone = bidPhone;
	}

	@Column(name = "middleRate")
	public Double getMiddleRate() {
		return middleRate;
	}

	public void setMiddleRate(Double middleRate) {
		this.middleRate = middleRate;
	}

	@Column(name = "withdrawRate")
	public Double getWithdrawRate() {
		return withdrawRate;
	}


	public void setWithdrawRate(Double withdrawRate) {
		this.withdrawRate = withdrawRate;
	}

	@Column(name = "timeDate")
	public String getTimeDate() {
		return timeDate;
	}


	public void setTimeDate(String timeDate) {
		this.timeDate = timeDate;
	}
	
	@Column(name = "regBonu")
	public Double getRegBonu() {
		return regBonu;
	}


	public void setRegBonu(Double regBonu) {
		this.regBonu = regBonu;
	}

	@Column(name = "generalizeState")
	public Integer getGeneralizeState() {
		return generalizeState;
	}


	public void setGeneralizeState(Integer generalizeState) {
		this.generalizeState = generalizeState;
	}

	@Column(name = "bonusState")
	public Integer getBonusState() {
		return bonusState;
	}


	public void setBonusState(Integer bonusState) {
		this.bonusState = bonusState;
	}

	@Column(name = "starTime")
	public String getStarTime() {
		return starTime;
	}


	public void setStarTime(String starTime) {
		this.starTime = starTime;
	}

	@Column(name = "endTime")
	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Column(name = "redState")
	public Integer getRedState() {
		return redState;
	}


	public void setRedState(Integer redState) {
		this.redState = redState;
	}

	@Column(name = "hccoinRate")
	public Double getHccoinRate() {
		return hccoinRate;
	}


	public void setHccoinRate(Double hccoinRate) {
		this.hccoinRate = hccoinRate;
	}
	

	@Column(name = "matchingInterest")
	public Double getMatchingInterest() {
		return matchingInterest;
	}

	public void setMatchingInterest(Double matchingInterest) {
		this.matchingInterest = matchingInterest;
	}
	

	@Column(name = "financial")
	public Double getFinancial() {
		return financial;
	}

	public void setFinancial(Double financial) {
		this.financial = financial;
	}
}