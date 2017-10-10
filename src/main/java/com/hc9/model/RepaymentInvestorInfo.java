package com.hc9.model;


/**
 * 还款信息分表
 * @author RanQiBing 2014-02-14
 *
 */
public class RepaymentInvestorInfo{
    
    /** 投资人账户类型 */
	private String pTAcctType = "1";  
	 /**投资人 IPS 账户号 */
	private String pTIpsAcctNo; 
	 /**投资人还款费用 */
	private String pTTrdFee = "0";  
	 /**还款金额 */
	private String pTTrdAmt;  
	
	private String pStatus;
	
	private String pMessage;
	
	/**
	 * @return pTAcctType
	 */
	public String getpTAcctType() {
		return pTAcctType;
	}
	/**
	 * @param pTAcctType pTAcctType
	 */
	public void setpTAcctType(String pTAcctType) {
		this.pTAcctType = pTAcctType;
	}
	/**
	 * @return pTIpsAcctNo
	 */
	public String getpTIpsAcctNo() {
		return pTIpsAcctNo;
	}
	/**
	 * @param pTIpsAcctNo pTIpsAcctNo
	 */
	public void setpTIpsAcctNo(String pTIpsAcctNo) {
		this.pTIpsAcctNo = pTIpsAcctNo;
	}
	/**
	 * @return pTTrdFee
	 */
	public String getpTTrdFee() {
		return pTTrdFee;
	}
	/**
	 * @param pTTrdFee pTTrdFee
	 */
	public void setpTTrdFee(String pTTrdFee) {
		this.pTTrdFee = pTTrdFee;
	}
	/**
	 * @return pTTrdAmt
	 */
	public String getpTTrdAmt() {
		return pTTrdAmt;
	}
	/**
	 * @param pTTrdAmt pTTrdAmt
	 */
	public void setpTTrdAmt(String pTTrdAmt) {
		this.pTTrdAmt = pTTrdAmt;
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
