package com.hc9.model;
/**
 * 转账明细
 * @author frank
 *
 */
public class PRow {
	/**
	 * 原商户订单号
	 * 当转账类型为投资时，登记债权人 当转账类型为投资时，登记债权人 时提交的商户订单号
	 * 当转账类型为代偿时，登记债权人提交的商户订单号 当转账类型为代偿时，登记债权人提交的商户订单号
	 * 当转账类型为代偿还款时，为代偿时提交的商户订单号
	 * 当转账类型为 债权让时，当转账类型为 债权让时，登记债权转让 时提交的商户订单号
	 * 当转账类型为结算担保收益时，登记 当转账类型为结算担保收益时，登记 担保 人时提交的商户订单号
	 */
	private String pOriMerBillNo;
	
	/**
	 * 转账金额
	 * 转账类型： 1：投资 ，转账金额 =债权面额 ；
	 * 转账类型， 2：代偿， 转账金额 =代偿金额 ；
	 * 转账类型， 3：代偿还款 ，转账金额 =代偿 还款 金额 ；
	 * 转账类型， 4：债权转让 ：债权转让 ，转账金额 转账金额 =登记债权转让时的 登记债权转让时的 登记债权转让时的支付金额
	 * 转账类型， 5：结算担保收益 ：结算担保收益 ，累计 转账金额 <= 登记担保方时的担保收益
	 */
	private String pTrdAmt;
	
	/**转出方帐户类型
	 * 0# 机构 ；1# 个
	 * */
	private String pFAcctType;
	
	/**
	 * 转出方 IPS托管账户号
	 * 转账类型， 1：投资 ，此为转出方（投资人）；
	 * 转账类型， 2：代偿，此为转出方（担保）；
	 * 转账类型， 3：代偿还款 ，此为转出方（借款人）；
	 * 转账类型， 4：债权转让 ，此为转出方（ 受让方 ）；
	 * 转账类型， 5：结算担保收益 ，此为转出方（借款人）； 
	 */
	private String pFIpsAcctNo;
	
	/**
	 * 转出方明细 手续 费
	 */
	private String pFTrdFee;
	
	/**
	 * 转入方 账户类型
	 */
	private String pTAcctType;
	
	/**
	 * 转入方 IPSIPSIPS托管 账户号
	 */
	private String pTIpsAcctNo;
	
	/**
	 * 转入方明细 手续 费
	 */
	private String pTTrdFee;

	/**IPS明细 订单号 */
	private String pIpsDetailBillNo;
	
	/**IPS明细处理时间*/
	private String pIpsDetailTime;
	
	/**IPS手续费*/
	private String pIpsFee;
	
	/**
	 * 转账状态
	 * Y 转账成功； N#转账失败
	 */
	private String pStatus;
	
	/**转账备注*/
	private String pMessage;

	public PRow() {
	}

	public String getpOriMerBillNo() {
		return pOriMerBillNo;
	}

	public void setpOriMerBillNo(String pOriMerBillNo) {
		this.pOriMerBillNo = pOriMerBillNo;
	}

	public String getpTrdAmt() {
		return pTrdAmt;
	}

	public void setpTrdAmt(String pTrdAmt) {
		this.pTrdAmt = pTrdAmt;
	}

	public String getpFAcctType() {
		return pFAcctType;
	}

	public void setpFAcctType(String pFAcctType) {
		this.pFAcctType = pFAcctType;
	}

	public String getpFIpsAcctNo() {
		return pFIpsAcctNo;
	}

	public void setpFIpsAcctNo(String pFIpsAcctNo) {
		this.pFIpsAcctNo = pFIpsAcctNo;
	}

	public String getpFTrdFee() {
		return pFTrdFee;
	}

	public void setpFTrdFee(String pFTrdFee) {
		this.pFTrdFee = pFTrdFee;
	}

	public String getpTAcctType() {
		return pTAcctType;
	}

	public void setpTAcctType(String pTAcctType) {
		this.pTAcctType = pTAcctType;
	}

	public String getpTIpsAcctNo() {
		return pTIpsAcctNo;
	}

	public void setpTIpsAcctNo(String pTIpsAcctNo) {
		this.pTIpsAcctNo = pTIpsAcctNo;
	}

	public String getpTTrdFee() {
		return pTTrdFee;
	}

	public void setpTTrdFee(String pTTrdFee) {
		this.pTTrdFee = pTTrdFee;
	}

	public String getpIpsDetailBillNo() {
		return pIpsDetailBillNo;
	}

	public void setpIpsDetailBillNo(String pIpsDetailBillNo) {
		this.pIpsDetailBillNo = pIpsDetailBillNo;
	}

	public String getpIpsDetailTime() {
		return pIpsDetailTime;
	}

	public void setpIpsDetailTime(String pIpsDetailTime) {
		this.pIpsDetailTime = pIpsDetailTime;
	}

	public String getpIpsFee() {
		return pIpsFee;
	}

	public void setpIpsFee(String pIpsFee) {
		this.pIpsFee = pIpsFee;
	}

	public String getpStatus() {
		return pStatus;
	}

	public void setpStatus(String pStatus) {
		this.pStatus = pStatus;
	}

	public String getpMessage() {
		return pMessage;
	}

	public void setpMessage(String pMessage) {
		this.pMessage = pMessage;
	}
	
	
}
