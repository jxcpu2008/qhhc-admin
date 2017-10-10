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
 * 收藏列表
 * @author Administrator
 *
 */
@Entity
@Table(name = "loansign_collected")
public class LoansignCollected implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	private Long id;
	/**
	 * 借款标信息
	 */
	private Loansign loansign;
	/**
	 * 会员基本信息
	 */
	private Userbasicsinfo userbasicsinfo;
	/**
	 * 收藏时间
	 */
	private String collectTime; 
	
	public LoansignCollected(){}
	
	/**
	 * 
	 * @param id
	 * @param loansign 借款标信息
	 * @param userbasicsinfo 会员基本信息
	 * @param collectTime 收藏时间
	 */
	public LoansignCollected(Long id,Loansign loansign,Userbasicsinfo userbasicsinfo, String collectTime){
		this.id=id;
		this.loansign=loansign;
		this.userbasicsinfo=userbasicsinfo;
		this.collectTime=collectTime;
	}
	/**
	 * 
	 * @param loansign
	 * @param userbasicsinfo
	 */
	public LoansignCollected(Loansign loansign,Userbasicsinfo userbasicsinfo,String collectTime){
		this.loansign=loansign;
		this.userbasicsinfo=userbasicsinfo;
		this.collectTime=collectTime;
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
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "loanSign_id")
	public Loansign getLoansign() {
		return loansign;
	}
	public void setLoansign(Loansign loansign) {
		this.loansign = loansign;
	}
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name= "userbasicinfo_id")
	public Userbasicsinfo getUserbasicsinfo() {
		return userbasicsinfo;
	}
	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	@Column(name = "collecttime")
	public String getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}
}
