package com.hc9.model;

/**
 * 用户账户余额查询
 * 
 * @author frank 
 * 
 */
public class crs {
	
	private String order_id;
	
	private int state;
	
	private Double succ_amount;
	
	private Double baofoo_fee;
	
	private Double amount;
    /**
     * 返回状态码
     */
    private String code;
    
    private String succ_time;
    /**
     * 商户收取的手续费
     */
    private Double mer_fee;
    /**
     * 实充金额
     */
    private Double incash_money;
    /**
     * 宝付余额
     */
    private Double balance;
    /**
     * 签名码
     */
    private String sign;
    /**
     * 返回宝付账号id
     */
    private Long user_id;
    /**
     * 返回msg为String类型
     */
    private String msg;
    
    private Double fee;
    /***
     * 宝付收取的手续费，承担方
     * 1平台2个人承担
     */
    private Integer fee_taken_on;
    /***
     * 同提交请求时候的参数值一致
     */
    private String additional_info;
    
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Double getSucc_amount() {
		return succ_amount;
	}
	public void setSucc_amount(Double succ_amount) {
		this.succ_amount = succ_amount;
	}
	public Double getBaofoo_fee() {
		return baofoo_fee;
	}
	public void setBaofoo_fee(Double baofoo_fee) {
		this.baofoo_fee = baofoo_fee;
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
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getSucc_time() {
		return succ_time;
	}
	public void setSucc_time(String succ_time) {
		this.succ_time = succ_time;
	}
	public Double getMer_fee() {
		return mer_fee;
	}
	public void setMer_fee(Double mer_fee) {
		this.mer_fee = mer_fee;
	}
	public Double getIncash_money() {
		return incash_money;
	}
	public void setIncash_money(Double incash_money) {
		this.incash_money = incash_money;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public Integer getFee_taken_on() {
		return fee_taken_on;
	}
	public void setFee_taken_on(Integer fee_taken_on) {
		this.fee_taken_on = fee_taken_on;
	}
	public String getAdditional_info() {
		return additional_info;
	}
	public void setAdditional_info(String additional_info) {
		this.additional_info = additional_info;
	}
}
