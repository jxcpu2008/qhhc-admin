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
 * <p>
 * Title:Accountinfo
 * </p>
 * <p>
 * Description: Accountinfo 用户流水账
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author LiNing
 *         <p>
 *         date 2014年2月14日
 *         </p>
 */
@Entity
@Table(name = "accountinfo")
public class Accountinfo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */

	private Long id;

	/**
	 * 用户
	 */
	private Userbasicsinfo userbasicsinfo;
	/**
	 * 类型
	 */
	private Accounttype accounttype;
	/**
	 * 项目
	 */
	private String loansignId;
	/**
	 * 余额
	 */
	private Double money;
	/**
	 * 操作时间
	 */
	private String time;
	/**
	 * 支出
	 */
	private Double expenditure;
	/**
	 * 收入
	 */
	private Double income;
	/**
	 * 说明
	 */
	private String explan;
	/**
	 * 店铺编号
	 */
	private Long shopId;
	/**
	 * ips订单号
	 */
	private String ipsNumber;

	/***
	 * 放款服务费
	 */
	private Double fee;

	// Constructors

	/** default constructor */
	public Accountinfo() {
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description: 有参构造方法
	 * </p>
	 * 
	 * @param userbasicsinfo
	 *            会员基本信息
	 * @param accounttype
	 *            流水账类型
	 * @param money
	 *            金额
	 */
	public Accountinfo(Userbasicsinfo userbasicsinfo, Accounttype accounttype,
			Double money) {
		this.userbasicsinfo = userbasicsinfo;
		this.accounttype = accounttype;
		this.money = money;
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description: 有参构造方法
	 * </p>
	 * 
	 * @param userbasicsinfo
	 *            会员基本信息
	 * @param accounttype
	 *            流水账类型
	 * @param money
	 *            金额
	 * @param time
	 *            添加时间
	 * @param expenditure
	 *            支出金额
	 * @param income
	 *            收入金额
	 * @param explan
	 *            备注
	 * @param withdraw
	 *            不知道
	 * @param loansignId
	 *            借款标编号
	 */
	public Accountinfo(Userbasicsinfo userbasicsinfo, Accounttype accounttype,
			Double money, String time, Double expenditure, Double income,
			String explan, Long shopId, String ipsNumber) {
		this.userbasicsinfo = userbasicsinfo;
		this.accounttype = accounttype;
		this.money = money;
		this.time = time;
		this.expenditure = expenditure;
		this.income = income;
		this.explan = explan;
		this.shopId = shopId;
		this.ipsNumber = ipsNumber;
	}

	/**
	 * <p>
	 * Title: getId
	 * </p>
	 * <p>
	 * Description:
	 * </p>
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
	 * <p>
	 * Title: setId
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param id
	 *            id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * <p>
	 * Title: getUserbasicsinfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return userbasicsinfo
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userbasic_id", nullable = false)
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	/**
	 * <p>
	 * Title: setUserbasicsinfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param userbasicsinfo
	 *            userbasicsinfo
	 */
	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	/**
	 * <p>
	 * Title: getAccounttype
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return accounttype
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accounttype_id", nullable = false)
	public Accounttype getAccounttype() {
		return this.accounttype;
	}

	/**
	 * <p>
	 * Title: setAccounttype
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param accounttype
	 *            accounttype
	 */
	public void setAccounttype(Accounttype accounttype) {
		this.accounttype = accounttype;
	}

	/**
	 * <p>
	 * Title: getMoney
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return money
	 */
	@Column(name = "money", precision = 18, scale = 4)
	public Double getMoney() {
		return this.money;
	}

	/**
	 * <p>
	 * Title: setMoney
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param money
	 *            money
	 */
	public void setMoney(Double money) {
		this.money = money;
	}

	/**
	 * <p>
	 * Title: getTime
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return time
	 */
	@Column(name = "time", length = 30)
	public String getTime() {
		return this.time;
	}

	/**
	 * <p>
	 * Title: setTime
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param time
	 *            time
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * <p>
	 * Title: getExpenditure
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return expenditure
	 */
	@Column(name = "expenditure", precision = 18, scale = 4)
	public Double getExpenditure() {
		return this.expenditure;
	}

	/**
	 * <p>
	 * Title: setExpenditure
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param expenditure
	 *            expenditure
	 */
	public void setExpenditure(Double expenditure) {
		this.expenditure = expenditure;
	}

	/**
	 * <p>
	 * Title: getIncome
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return income
	 */
	@Column(name = "income", precision = 18, scale = 4)
	public Double getIncome() {
		return this.income;
	}

	/**
	 * <p>
	 * Title: setIncome
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param income
	 *            income
	 */
	public void setIncome(Double income) {
		this.income = income;
	}

	/**
	 * <p>
	 * Title: getExplan
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return explan
	 */
	@Column(name = "explan", length = 80)
	public String getExplan() {
		return this.explan;
	}

	/**
	 * <p>
	 * Title: setExplan
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param explan
	 *            explan
	 */
	public void setExplan(String explan) {
		this.explan = explan;
	}

	@Column(name = "shop_id")
	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	@Column(name = "loansign_id")
	public String getLoansignId() {
		return this.loansignId;
	}

	public void setLoansignId(String loansignId) {
		this.loansignId = loansignId;
	}

	/**
	 * ips唯一流水号
	 * 
	 * @return ipsNumber
	 */
	@Column(name = "ipsNumber", length = 30)
	public String getIpsNumber() {
		return ipsNumber;
	}

	/**
	 * @param ipsNumber
	 *            ips唯一流水号
	 */
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

}