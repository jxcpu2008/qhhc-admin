package com.hc9.model;
/**
 * 商户端获取银行列表查询
 * @author RanQiBing 2014-01-03
 *
 */
public class BankList {
	
    /**“平台”账号*/
    private String pMerCode; 
    /**返回状态 */
    private String pErrCode;    
    /**返回信息 */
    private String pErrMsg;    
    /**开通银行 */
    private String pBankList;  
    /**MD5 摘要 */
    private String pSign;    
    
    /**
     * 
     * @return pMerCode
     */
    public String getpMerCode() {
    	return pMerCode;
    }
    /**
     * 
     * @param pMerCode pMerCode
     */
    public void setpMerCode(String pMerCode) {
    	this.pMerCode = pMerCode;
    }
    /**
     * 
     * @return pErrCode
     */
    public String getpErrCode() {
    	return pErrCode;
    }
    /**
     * 
     * @param pErrCode pErrCode
     */
    public void setpErrCode(String pErrCode) {
    	this.pErrCode = pErrCode;
    }
    /**
     * 
     * @return pErrMsg
     */
    public String getpErrMsg() {
    	return pErrMsg;
    }
    /**
     * 
     * @param pErrMsg pErrMsg
     */
    public void setpErrMsg(String pErrMsg) {
    	this.pErrMsg = pErrMsg;
    }
    /**
     * 
     * @return pBankList
     */
    public String getpBankList() {
    	return pBankList;
    }
    /**
     * 
     * @param pBankList pBankList
     */
    public void setpBankList(String pBankList) {
    	this.pBankList = pBankList;
    }
    /**
     * 
     * @return pSign
     */
    public String getpSign() {
    	return pSign;
    }
    /**
     * pSign
     * @param pSign pSign
     */
    public void setpSign(String pSign) {
    	this.pSign = pSign;
    }
	
}
