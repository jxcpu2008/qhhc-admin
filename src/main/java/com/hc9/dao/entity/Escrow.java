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
import javax.persistence.OneToMany;
import javax.persistence.Table;
/**
 * 第三方担保
 * @author LKL
 *
 */
@Entity
@Table(name = "escrow")
public class Escrow implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	// Fields

	private Long id;
	/**担保公司名称*/
	private String name;
	/**简介*/
	private String brief;
	/**发展历史*/
	private String history;
	/**管理团队*/
	private String team;
	/**使命*/
	private String mission;
	/**手机**/
	private String phone;
	/**地址**/
	private String address;
	/**员工姓名*/
	private String staffName;
	/**员工手机，非空*/
	private String staffPhone;
	/**员工身份证，非空*/
	private String staffIdcard;
	/**员工邮箱，非空*/
	private String staffEmail;
	/**员工开的宝付帐号*/
	private String staffBaofu;
	/**宝付开通时间*/
	private String staffBaofuCreateTime;
	/**帐号余额*/
	private Double staffMoney;
	/**开户银行名称*/
	private String staffBankNo;
	/**开户银行名称*/
	private String  staffBankName;
	/**开户支行*/
	private String staffAddress;
	/**开户城市*/
	private String staffCity;
	/**开户省份*/
	private  String staffPro;
	/**授权状态0-待授权  1-已授权  -1-授权失败**/
	private  Integer inAccredit;
	/**授权时间**/
	private String accreditTime;
	/**是否验证手机 0-待验证 1-验证成功 -1-验证失败*/
	private Integer inPhone;
	/**手机验证时间*/
	private String phoneTime;
	/**是否验证身份证 0-待验证 1-验证成功 -1 验证失败*/
	private Integer inBankNo;
	/**身份证验证时间*/
	private String banknoTime;
	/**是否注册宝付 0-待注册 1-注册成 -1注册失败*/
	private Integer inBaofu;
	/**注册用户名6位*/
	private String staffUserName;
	/**赔付记录*/
	private List<Liquidation> liquidations = new ArrayList<Liquidation>(0);

	/** default constructor */
	public Escrow() {
	}

	/** full constructor */


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

	@Column(name = "name", length = 128)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "brief", length = 65535)
	public String getBrief() {
		return this.brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	@Column(name = "history", length = 65535)
	public String getHistory() {
		return this.history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	@Column(name = "team", length = 65535)
	public String getTeam() {
		return this.team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	@Column(name = "mission", length = 65535)
	public String getMission() {
		return this.mission;
	}

	public void setMission(String mission) {
		this.mission = mission;
	}

	@Column(name = "phone")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "address")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	@Column(name = "staff_name", nullable = false, length = 128)
	public String getStaffName() {
		return this.staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	@Column(name = "staff_phone", nullable = false, length = 20)
	public String getStaffPhone() {
		return this.staffPhone;
	}

	public void setStaffPhone(String staffPhone) {
		this.staffPhone = staffPhone;
	}

	@Column(name = "staff_IDcard", nullable = false, length = 20)
	public String getStaffIdcard() {
		return this.staffIdcard;
	}

	public void setStaffIdcard(String staffIdcard) {
		this.staffIdcard = staffIdcard;
	}

	@Column(name = "staff_email", nullable = false )
	public String getStaffEmail() {
		return this.staffEmail;
	}

	public void setStaffEmail(String staffEmail) {
		this.staffEmail = staffEmail;
	}

	@Column(name = "staff_baofu", length = 50)
	public String getStaffBaofu() {
		return this.staffBaofu;
	}

	public void setStaffBaofu(String staffBaofu) {
		this.staffBaofu = staffBaofu;
	}

	@Column(name = "staff_baofu_create_time", length = 50)
	public String getStaffBaofuCreateTime() {
		return this.staffBaofuCreateTime;
	}

	public void setStaffBaofuCreateTime(String staffBaofuCreateTime) {
		this.staffBaofuCreateTime = staffBaofuCreateTime;
	}

	@Column(name = "staff_money", precision = 20, scale = 4)
	public Double getStaffMoney() {
		return this.staffMoney;
	}

	public void setStaffMoney(Double staffMoney) {
		this.staffMoney = staffMoney;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "escrow")
	public List<Liquidation> getLiquidations() {
		return this.liquidations;
	}

	public void setLiquidations(List<Liquidation> liquidations) {
		this.liquidations = liquidations;
	}

	@Column(name = "staff_bankNo")
	public String getStaffBankNo() {
		return staffBankNo;
	}

	public void setStaffBankNo(String staffBankNo) {
		this.staffBankNo = staffBankNo;
	}

	@Column(name = "staff_bankName")
	public String getStaffBankName() {
		return staffBankName;
	}

	public void setStaffBankName(String staffBankName) {
		this.staffBankName = staffBankName;
	}

	@Column(name = "staff_address")
	public String getStaffAddress() {
		return staffAddress;
	}

	public void setStaffAddress(String staffAddress) {
		this.staffAddress = staffAddress;
	}

	@Column(name = "staff_city")
	public String getStaffCity() {
		return staffCity;
	}

	public void setStaffCity(String staffCity) {
		this.staffCity = staffCity;
	}

	@Column(name = "staff_pro")
	public String getStaffPro() {
		return staffPro;
	}

	public void setStaffPro(String staffPro) {
		this.staffPro = staffPro;
	}

	@Column(name = "in_accredit")
	public Integer getInAccredit() {
		return inAccredit;
	}

	public void setInAccredit(Integer inAccredit) {
		this.inAccredit = inAccredit;
	}

	@Column(name = "accredit_time")
	public String getAccreditTime() {
		return accreditTime;
	}

	public void setAccreditTime(String accreditTime) {
		this.accreditTime = accreditTime;
	}

	@Column(name = "in_phone")
	public Integer getInPhone() {
		return inPhone;
	}

	public void setInPhone(Integer inPhone) {
		this.inPhone = inPhone;
	}
	@Column(name = "phone_time")
	public String getPhoneTime() {
		return phoneTime;
	}

	public void setPhoneTime(String phoneTime) {
		this.phoneTime = phoneTime;
	}
	@Column(name = "in_bankno")
	public Integer getInBankNo() {
		return inBankNo;
	}

	public void setInBankNo(Integer inBankNo) {
		this.inBankNo = inBankNo;
	}
	@Column(name = "bankno_time")
	public String getBanknoTime() {
		return banknoTime;
	}

	public void setBanknoTime(String banknoTime) {
		this.banknoTime = banknoTime;
	}
	@Column(name = "in_baofu")
	public Integer getInBaofu() {
		return inBaofu;
	}

	public void setInBaofu(Integer inBaofu) {
		this.inBaofu = inBaofu;
	}
	@Column(name = "staff_username")
	public String getStaffUserName() {
		return staffUserName;
	}

	public void setStaffUserName(String staffUserName) {
		this.staffUserName = staffUserName;
	}

	
}