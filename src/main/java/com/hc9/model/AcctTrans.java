package com.hc9.model;


public class AcctTrans {
	private String merchant_id;
	private String order_id;
	private String payer_user_id;
	private String payee_user_id;
	private int payer_type;
	private int payee_type;
	private Double amount;
	private Double fee;
	private int fee_taken_on;
	private Long req_time;
	public String getMerchant_id() {
		return merchant_id;
	}
	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getPayer_user_id() {
		return payer_user_id;
	}
	public void setPayer_user_id(String payer_user_id) {
		this.payer_user_id = payer_user_id;
	}
	public String getPayee_user_id() {
		return payee_user_id;
	}
	public void setPayee_user_id(String payee_user_id) {
		this.payee_user_id = payee_user_id;
	}
	public int getPayer_type() {
		return payer_type;
	}
	public void setPayer_type(int payer_type) {
		this.payer_type = payer_type;
	}
	public int getPayee_type() {
		return payee_type;
	}
	public void setPayee_type(int payee_type) {
		this.payee_type = payee_type;
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
	public int getFee_taken_on() {
		return fee_taken_on;
	}
	public void setFee_taken_on(int fee_taken_on) {
		this.fee_taken_on = fee_taken_on;
	}
	public Long getReq_time() {
		return req_time;
	}
	public void setReq_time(Long req_time) {
		this.req_time = req_time;
	}
    
	
	
   
}
