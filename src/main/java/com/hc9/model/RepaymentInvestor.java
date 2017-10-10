package com.hc9.model;


/**
 * 还款信息分表
 * @author frank 2014-08-26
 *
 */
public class RepaymentInvestor{
    
    /** 登记债权人时提交的订单号 */
	private String pCreMerBillNo;  
	
	 /**转入方IPS 托管账户号 */
	private String pInAcctNo; 
	
	 /**转入方手续费，平台向投资方收取的费用 */
	private String pInFee;  
	
	 /**转出方手续费，平台向借款人收取的费用 */
	private String pOutInfoFee;  
	
	/**转入金额*/
	private String pInAmt;
	
	/**转入结果说明*/
	private String pMessage;
	
	/**转入状态*/
	private String pStatus;
	
	/**
	 * 登记债权人时提交的订单号
	 */
	public String getpCreMerBillNo() {
		return pCreMerBillNo;
	}
	/**
	 * 登记债权人时提交的订单号
	 */
	public void setpCreMerBillNo(String pCreMerBillNo) {
		this.pCreMerBillNo = pCreMerBillNo;
	}
	/**
	 * 转入方IPS 托管账户号
	 */
	public String getpInAcctNo() {
		return pInAcctNo;
	}
	/**
	 * 转入方IPS 托管账户号
	 */
	public void setpInAcctNo(String pInAcctNo) {
		this.pInAcctNo = pInAcctNo;
	}
	/**
	 * 转入方手续费
	 */
	public String getpInFee() {
		return pInFee;
	}
	/**
	 * 转入方手续费
	 */
	public void setpInFee(String pInFee) {
		this.pInFee = pInFee;
	}
	/**
	 * 转出方手续费
	 */
	public String getpOutInfoFee() {
		return pOutInfoFee;
	}
	/**
	 * 转出方手续费
	 */
	public void setpOutInfoFee(String pOutInfoFee) {
		this.pOutInfoFee = pOutInfoFee;
	}
	/**
	 * 转入金额
	 */
	public String getpInAmt() {
		return pInAmt;
	}
	/**
	 * 转入金额
	 */
	public void setpInAmt(String pInAmt) {
		this.pInAmt = pInAmt;
	}
	/**
	 * 转入结果说明
	 * @return
	 */
	public String getpMessage() {
		return pMessage;
	}
	/**
	 * 转入结果说明
	 */	
	public void setpMessage(String pMessage) {
		this.pMessage = pMessage;
	}
	/**
	 * 转入状态
	 * @return
	 */
	public String getpStatus() {
		return pStatus;
	}
	/**
	 * 转入状态
	 */
	public void setpStatus(String pStatus) {
		this.pStatus = pStatus;
	}
	

}
