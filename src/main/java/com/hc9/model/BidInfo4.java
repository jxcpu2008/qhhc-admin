package com.hc9.model;

import java.util.List;

 
/**
 * 还标
 * 投资人手续费用与是否vip相关，用属性注入
 * @author frank 2014-08-18
 *
 */
/**
 * @author Administrator
 *
 */
public class BidInfo4 {
	private String merchant_id;
	private int action_type;
	private String order_id;
	private Long cus_id;
	private String cus_name;
	private String brw_id;
	private String req_time;
	private String actions;
	private String action;
	private String user_id;
	private String user_name;
	private Double amount;
	
	
	private List<Payuser> payuser;
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
	private int special;
	private Double fee;
	
	
	
	
	
	
	public List<Payuser> getPayuser() {
		return payuser;
	}
	public void setPayuser(List<Payuser> payuser) {
		this.payuser = payuser;
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
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getMerchant_id() {
		return merchant_id;
	}
	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}
	
	/**
	 * 请求类型，投标为1，满标为2，流标为3，还标为4
	 * @return
	 */
	public int getAction_type() {
		return action_type;
	}
	public void setAction_type(int action_type) {
		this.action_type = action_type;
	}
	
	
	/**
	 * 订单号 （唯一不允许重复）允许字符
	 * @return
	 */
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	
	
	/**
	 * 标id
	 * @return
	 */
	public Long getCus_id() {
		return cus_id;
	}
	public void setCus_id(Long i) {
		this.cus_id = i;
	}
	
	
	/**
	 * 标名称
	 * @return
	 */
	public String getCus_name() {
		return cus_name;
	}
	public void setCus_name(String cus_name) {
		this.cus_name = cus_name;
	}
	/**
	 * 借款人user_id用户编号(唯一)
	 * @return
	 */
	public String getBrw_id() {
		return brw_id;
	}
	public void setBrw_id(String brw_id) {
		this.brw_id = brw_id;
	}
	
	
	/**
	 * 请求时间
	 * @return
	 */
	public String getReq_time() {
		return req_time;
	}
	public void setReq_time(String req_time) {
		this.req_time = req_time;
	}
	
	
	public String getActions() {
		return actions;
	}
	public void setActions(String actions) {
		this.actions = actions;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	
	/**
	 * 用户编号(唯一)
	 * @return
	 */
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String string) {
		this.user_id = string;
	}
	
	
	/**
	 * 金额，单位：元
	 * @return
	 */
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	/**
	 * 特殊标识(还标专用), 该编号对应的用户，为还款人。如20140374
	 * @return
	 */
	public int getSpecial() {
		return special;
	}
	public void setSpecial(int special) {
		this.special = special;
	}
	
	/**
	 * 手续费(涉及到满标、还款接口)
	 * @return
	 */
	public Double getFee() {
		return fee;
	}
	public void setFee(Double fee) {
		this.fee = fee;
	}


}
