package com.hc9.model;

public class Payuser {
	private String user_id;
	private String id;
	private Double amount;
	private Double fee;
	private String rId;

	/**
	 * 宝付账户id
	 * 
	 * @return
	 */
	public String getUser_id() {
		return user_id;
	}

	/**
	 * 宝付账户id
	 * 
	 * @param user_id
	 */
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	/**
	 * 投资人用户 id
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 投资人用户 id
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/***
	 * 还款明细表Id
	 * @return
	 */
	public String getrId() {
		return rId;
	}

	public void setrId(String rId) {
		this.rId = rId;
	}

}
