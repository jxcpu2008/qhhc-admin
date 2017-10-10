package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * RegBonus entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "reg_bonus")
public class RegBonus implements java.io.Serializable {

	// Fields
	/***/
	private Long id;
	/**用户*/
	private Userbasicsinfo userbasicsinfo;
	
	/**推广用户*/
	private Userbasicsinfo referrer;
	/**后台操作人员*/
	private Adminuser adminuser;
	/**奖励金额*/
	private Double bouns;
	/**奖励发放时间*/
	private String releaseTime;
	/**奖励发放状态*/
	private Integer releaseStatus;

	// Constructors

	/** default constructor */
	public RegBonus() {
	}

	/** minimal constructor */
	public RegBonus(Long id) {
		this.id = id;
	}

	/** full constructor */
	public RegBonus(Long id, Userbasicsinfo userbasicsinfo,Userbasicsinfo referrer,
			Adminuser adminuser, Double bouns, String releaseTime,
			Integer releaseStatus) {
		this.id = id;
		this.userbasicsinfo = userbasicsinfo;
		this.referrer = referrer;
		this.adminuser = adminuser;
		this.bouns = bouns;
		this.releaseTime = releaseTime;
		this.releaseStatus = releaseStatus;
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

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "referrer_id")
	public Userbasicsinfo getReferrer() {
		return referrer;
	}

	public void setReferrer(Userbasicsinfo referrer) {
		this.referrer = referrer;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	public Adminuser getAdminuser() {
		return this.adminuser;
	}

	public void setAdminuser(Adminuser adminuser) {
		this.adminuser = adminuser;
	}

	@Column(name = "bouns", precision = 22, scale = 0)
	public Double getBouns() {
		return this.bouns;
	}

	public void setBouns(Double bouns) {
		this.bouns = bouns;
	}

	@Column(name = "release_time", length = 30)
	public String getReleaseTime() {
		return this.releaseTime;
	}

	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}

	@Column(name = "release_status")
	public Integer getReleaseStatus() {
		return this.releaseStatus;
	}

	public void setReleaseStatus(Integer releaseStatus) {
		this.releaseStatus = releaseStatus;
	}

}