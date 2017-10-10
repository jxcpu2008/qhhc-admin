package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Bonus entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "bonus")
public class Bonus implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private Long id;
	
	//被推荐人Id
	private Long userId;
	
	//被推荐人姓名
	private String userName;
	
	//被推荐人手机
	private String userPhone;
	
	//被推荐人奖励
	private Double userAmount;
	
	//被推荐人发放奖励日期
	private  String userDate;
	
	//被推荐人发放奖励状态0-待发放 1-已发放 2-奖励为0  3-无推荐人 -1 发放失败 
	private  Integer userState;
	
	//被推荐人发放管理员
	private Long userReleaseId;
	
	//被推荐人投资金额
	private Double tenderMoney;
	
	//被推荐人投资时间
	private String tenderTime;
	
	//推荐人Id
	private Long genUserId;
	
	//推荐人姓名
	private String genUserName;
	
	//推荐人奖励
	private Double genUserAmount;
	
	//推荐人发放奖励日期
	private  String genUserDate;
	
	//推荐人发放奖励状态0-待发放 1-已发放 2-奖励为0  3-无推荐人 -1 发放失败 
	private  Integer genUserState;
	
	//推荐人发放管理员
	private Long genUserReleaseId;
	
	

	// Constructors

	/** default constructor */
	public Bonus() {
	}

	/** minimal constructor */
	public Bonus(Long id) {
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

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "user_name")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "user_phone")
	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	@Column(name = "user_amount")
	public Double getUserAmount() {
		return userAmount;
	}

	public void setUserAmount(Double userAmount) {
		this.userAmount = userAmount;
	}

	@Column(name = "user_date")
	public String getUserDate() {
		return userDate;
	}

	public void setUserDate(String userDate) {
		this.userDate = userDate;
	}

	@Column(name = "user_state")
	public Integer getUserState() {
		return userState;
	}

	public void setUserState(Integer userState) {
		this.userState = userState;
	}

	@Column(name = "user_releaseId")
	public Long getUserReleaseId() {
		return userReleaseId;
	}

	public void setUserReleaseId(Long userReleaseId) {
		this.userReleaseId = userReleaseId;
	}

	@Column(name = "tenderMoney")
	public Double getTenderMoney() {
		return tenderMoney;
	}

	public void setTenderMoney(Double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}

	@Column(name = "tenderTime")
	public String getTenderTime() {
		return tenderTime;
	}

	public void setTenderTime(String tenderTime) {
		this.tenderTime = tenderTime;
	}

	@Column(name = "genuser_id")
	public Long getGenUserId() {
		return genUserId;
	}

	public void setGenUserId(Long genUserId) {
		this.genUserId = genUserId;
	}

	@Column(name = "genuser_name")
	public String getGenUserName() {
		return genUserName;
	}

	public void setGenUserName(String genUserName) {
		this.genUserName = genUserName;
	}
	
	@Column(name = "genuser_amount")
	public Double getGenUserAmount() {
		return genUserAmount;
	}

	public void setGenUserAmount(Double genUserAmount) {
		this.genUserAmount = genUserAmount;
	}

	@Column(name = "genuser_date")
	public String getGenUserDate() {
		return genUserDate;
	}

	public void setGenUserDate(String genUserDate) {
		this.genUserDate = genUserDate;
	}

	@Column(name = "genuser_state")
	public Integer getGenUserState() {
		return genUserState;
	}

	public void setGenUserState(Integer genUserState) {
		this.genUserState = genUserState;
	}

	@Column(name = "genuser_releaseId")
	public Long getGenUserReleaseId() {
		return genUserReleaseId;
	}

	public void setGenUserReleaseId(Long genUserReleaseId) {
		this.genUserReleaseId = genUserReleaseId;
	}
	
	

	
}