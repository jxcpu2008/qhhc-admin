package com.hc9.model;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.StringUtil;

/**
 * 投标审核(放款)
 * @author RanQiBing 2014-01-03
 *
 */
public class TenderAuditInfo {
	
    /**标号 */
    private String pBidNo;  
    /**合同号 */
    private String pContractNo;  
    /**商户放款订单号*/
    private String pMerBillNo = "FK"+StringUtil.pMerBillNo();  
    /**是否放款 */
    private String pBidStatus = Constant.SUCCESS;  
    /**状态返回地址 */
    private String pS2SUrl = Constant.SHOP;
    /** 备注 */
    private String pMemo1 = "";  
    /** 备注 */
    private String pMemo2 = ""; 
    /** 备注 loanid*/
    private String pMemo3 = "";  
    /**IPS放款订单号 */
    private String pIpsBillNo; 
	
    /**
     * @return pBidNo
     */
    public String getpBidNo() {
    	return pBidNo;
    }
    /**
     * @param pBidNo pBidNo
     */
    public void setpBidNo(String pBidNo) {
    	this.pBidNo = pBidNo;
    }
    /**
     * @return pContractNo
     */
    public String getpContractNo() {
    	return pContractNo;
    }
    /**
     * @param pContractNo pContractNo
     */
    public void setpContractNo(String pContractNo) {
    	this.pContractNo = pContractNo;
    }
    /**
     * @return pBidStatus
     */
    public String getpMerBillNo() {
    	return pMerBillNo;
    }
    /**
     * @param pMerBillNo pMerBillNo
     */
    public void setpMerBillNo(String pMerBillNo) {
    	this.pMerBillNo = pMerBillNo;
    }
    /**
     * @return pBidStatus
     */
    public String getpBidStatus() {
    	return pBidStatus;
    }
    /**
     * @param pBidStatus pBidStatus
     */
    public void setpBidStatus(String pBidStatus) {
    	this.pBidStatus = pBidStatus;
    }
    /**
     * @return pS2SUrl
     */
    public String getpS2SUrl() {
    	return pS2SUrl;
    }
    /**
     * @param pS2SUrl pS2SUrl 
     */
    public void setpS2SUrl(String pS2SUrl) {
    	this.pS2SUrl = pS2SUrl;
    }
    /**
     * @return pMemo1
     */
    public String getpMemo1() {
    	return pMemo1;
    }
    /**
     * @param pMemo1 pMemo1
     */
    public void setpMemo1(String pMemo1) {
    	this.pMemo1 = pMemo1;
    }
    /**
     * @return pMemo2
     */
    public String getpMemo2() {
    	return pMemo2;
    }
    /**
     * @param pMemo2 pMemo2
     */
    public void setpMemo2(String pMemo2) {
    	this.pMemo2 = pMemo2;
    }
    /**
     * @return pMemo3
     */
    public String getpMemo3() {
    	return pMemo3;
    }
    /**
     * @param pMemo3 pMemo3
     */
    public void setpMemo3(String pMemo3) {
    	this.pMemo3 = pMemo3;
    }
    /**
     * @return pIpsBillNo
     */
    public String getpIpsBillNo() {
    	return pIpsBillNo;
    }
    /**
     * @param pIpsBillNo pIpsBillNo
     */
    public void setpIpsBillNo(String pIpsBillNo) {
    	this.pIpsBillNo = pIpsBillNo;
    }
    
	public TenderAuditInfo(String pBidNo, String pContractNo,String pMemo1,String pMemo2,String pMemo3) {
		super();
		this.pBidNo = pBidNo;
		this.pContractNo = pContractNo;
		this.pMemo1 = pMemo1;
		this.pMemo2 = pMemo2;
		this.pMemo3 = pMemo3;
	}
	public TenderAuditInfo() {
	}

	
}
