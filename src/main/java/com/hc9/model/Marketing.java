package com.hc9.model;


/***
 * 销售业绩查询
 * @author  lkl
 *
 */
public class Marketing implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 真实姓名
	 */
	private String name;
	
	/**
	 * 手机号码
	 */
	private String phone;
	
	/***
	 * 注册手机号认购业绩
	 */
	private Double loanRecrodPerformance;
	
	/***
	 * 其推荐人认购业绩
	 */
	private Double genergerPerformance;
	
	/***
	 * 其推荐注册人数
	 */
	private Integer genergerCountUid;
	
	/***
	 * 注册手机号年化业绩
	 */
	private Double phonePerformance;
	
	/***
	 * 其推荐人年化业绩
	 */
	private Double uidPerformance;
	
	/***
	 * 其推荐认购人数
	 */
	private Integer genergerNumber;
	
	/***
	 * 注册手机号认购单数
	 */
	private Integer loanRecordNumber;
	
	/***
	 * 其推荐人认购单数
	 */
	private Integer genergerLoanRecordNumber;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Double getLoanRecrodPerformance() {
		return loanRecrodPerformance;
	}

	public void setLoanRecrodPerformance(Double loanRecrodPerformance) {
		this.loanRecrodPerformance = loanRecrodPerformance;
	}

	public Double getGenergerPerformance() {
		return genergerPerformance;
	}

	public void setGenergerPerformance(Double genergerPerformance) {
		this.genergerPerformance = genergerPerformance;
	}

	public Double getPhonePerformance() {
		return phonePerformance;
	}

	public void setPhonePerformance(Double phonePerformance) {
		this.phonePerformance = phonePerformance;
	}

	public Double getUidPerformance() {
		return uidPerformance;
	}

	public void setUidPerformance(Double uidPerformance) {
		this.uidPerformance = uidPerformance;
	}

	public Integer getGenergerNumber() {
		return genergerNumber;
	}

	public void setGenergerNumber(Integer genergerNumber) {
		this.genergerNumber = genergerNumber;
	}

	public Integer getLoanRecordNumber() {
		return loanRecordNumber;
	}

	public void setLoanRecordNumber(Integer loanRecordNumber) {
		this.loanRecordNumber = loanRecordNumber;
	}

	public Integer getGenergerLoanRecordNumber() {
		return genergerLoanRecordNumber;
	}

	public void setGenergerLoanRecordNumber(Integer genergerLoanRecordNumber) {
		this.genergerLoanRecordNumber = genergerLoanRecordNumber;
	}

	public Integer getGenergerCountUid() {
		return genergerCountUid;
	}

	public void setGenergerCountUid(Integer genergerCountUid) {
		this.genergerCountUid = genergerCountUid;
	}
	
	

}
