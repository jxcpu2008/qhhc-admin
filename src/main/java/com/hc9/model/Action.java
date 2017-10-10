package com.hc9.model;

import java.text.DecimalFormat;

/**
 * 投标的子循环
 * @author frank
 *
 */
public class Action {
	
	/**
	 * 用户Id
	 */
	private String user_id;
	
	/***
	 * 用户姓名
	 */
	private String user_name;
	
	/**
	 * 金额
	 */
	private String amount;
	
	/***
	 * 手续费
	 */
	private String fee;
	
	/**
	 * 担保人费用
	 */
	private Double voucher_fee;
	/**
	 * 是否为担保人
	 */
	private int is_voucher;
	/**
	 * 担保人id
	 */
	private String voucher_id;
	/**
	 * 是否为借款人
	 */
	private int user_voucher;
	
	/***
	 * 特殊标识(还标专用), 该编号对应的用户，为还款人。如20140374
	 */
	private String special;
	
	//转换金额
	private DecimalFormat df=new DecimalFormat("0.00");
	
	/***
	 * 店铺购标时使用
	 * @param user_id
	 * @param amout
	 * @param fee
	 */
	public Action(String user_id,String userName,Double amout){
		this.user_id = user_id;
		this.amount = df.format(amout);
		this.user_name=userName;
	}
	
	/***
	 * 店铺满标时使用
	 * @param user_id
	 * @param amout
	 * @param is_voucher
	 */
	public Action(String user_id,Double amout, Integer is_voucher){
		this.user_id = user_id;
		this.amount = df.format(amout);
		this.is_voucher=is_voucher;
	}
	
	
	public Action(String user_id, String amount, String special, String fee) {
		super();
		this.user_id = user_id;
		this.amount = amount;
		this.special = special;
		this.fee = fee;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSpecial() {
		return special;
	}
	public void setSpecial(String special) {
		this.special = special;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public Double getVoucher_fee() {
		return voucher_fee;
	}

	public void setVoucher_fee(Double voucher_fee) {
		this.voucher_fee = voucher_fee;
	}

	public int getIs_voucher() {
		return is_voucher;
	}

	public void setIs_voucher(int is_voucher) {
		this.is_voucher = is_voucher;
	}

	public String getVoucher_id() {
		return voucher_id;
	}

	public void setVoucher_id(String voucher_id) {
		this.voucher_id = voucher_id;
	}

	public int getUser_voucher() {
		return user_voucher;
	}

	public void setUser_voucher(int user_voucher) {
		this.user_voucher = user_voucher;
	}
	
	
}
