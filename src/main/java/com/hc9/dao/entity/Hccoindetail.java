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

@Entity
@Table(name = "hccoindetail")
public class Hccoindetail implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	
	/***
	 * 用户
	 */
	private Userbasicsinfo userbasicsinfo; 
	
	/***
	 * 红筹个数
	 */
	private Long number;
	
	/***
	 * 来源类型：1、推荐注册；2、投资；3、推荐投资；4、微课堂；5、全勤奖；6、图书馆；7、活动奖励；8、拍卖会；9、其他
	 */
	private Integer sourceType;
	
	/***
	 * 时间
	 */
	private  String receiveTime;
	
	/***
	 * 投资
	 */
	private Long loanrecordId;
	
	//备注
	private String remark;
	

	public Hccoindetail() {
	}

	public Hccoindetail(Long id, Userbasicsinfo userbasicsinfo, Long number,
			Integer sourceType, String receiveTime) {
		super();
		this.id = id;
		this.userbasicsinfo = userbasicsinfo;
		this.number = number;
		this.sourceType = sourceType;
		this.receiveTime = receiveTime;
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	public Userbasicsinfo getUserbasicsinfo() {
		return userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}
	
	@Column(name = "number")
	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	@Column(name = "sourceType")
	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	@Column(name = "receiveTime")
	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	@Column(name = "loanrecordId")
	public Long getLoanrecordId() {
		return loanrecordId;
	}

	public void setLoanrecordId(Long loanrecordId) {
		this.loanrecordId = loanrecordId;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	
	

}
