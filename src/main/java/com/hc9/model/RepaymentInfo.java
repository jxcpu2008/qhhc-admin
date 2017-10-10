package com.hc9.model;

import java.util.List;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;

/**
 * 还款
 * @author RanQiBing 2014-01-03
 *
 */
public class RepaymentInfo {
	
    /**标号*/
	private String pBidNo;  
	/**合同号*/
	private String pContractNo;
	/**还款日期 */
	private String pRepaymentDate=DateUtils.format("yyyyMMdd");
	/**商户还款订单号 */
	private String pMerBillNo = "HK"+StringUtil.pMerBillNo(); 
	/**商户放款订单号 */
	private String pMerchantBillNo = "";
	/**借款人账户类型*/
	private String pFAcctType ="1";   
	/**借款人证件号码 */
	private String pFIdentNo;  
	/**借款人姓名 */
	private String pFRealName;   
	/**借款人 IPS账户号 */
	private String pFIpsAcctNo;   
	/**借款人还款手续费 */
	private String pFTrdFee = "0";    
	/**还款总金额*/
	private String pFTrdAmt;  
	/**状态返回地址1 */
	private String pWebUrl = Constant.REPAYMENT;   
	/**状态返回地址2 */
	private String pS2SUrl = Constant.REPAYMENTASYNCHRONOUS;
	
	/**投资人信息*/
	private RepaymentInvestorInfo repaymentInvestorInfo; 
	/**备注 */
	private String pMemo1 = ""; 
	/**备注 */
	private String pMemo2 = "";
	/**备注 */
	private String pMemo3 = "";
	
	/** RepaymentInvestorInfo*/
	private List<RepaymentInvestorInfo> repaymentInvestorInfoList;
	
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
	 * @return pRepaymentDate
	 */
	public String getpRepaymentDate() {
		return pRepaymentDate;
	}
	/**
	 * @param pRepaymentDate pRepaymentDate
	 */
	public void setpRepaymentDate(String pRepaymentDate) {
		this.pRepaymentDate = pRepaymentDate;
	}
	/**
	 * @return pMerBillNo
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
	 * @return pMerchantBillNo
	 */
	public String getpMerchantBillNo() {
		return pMerchantBillNo;
	}
	/**
	 * @param pMerchantBillNo pMerchantBillNo
	 */
	public void setpMerchantBillNo(String pMerchantBillNo) {
		this.pMerchantBillNo = pMerchantBillNo;
	}
	/**
	 * @return pFAcctType
	 */
	public String getpFAcctType() {
		return pFAcctType;
	}
	/**
	 * @param pFAcctType pFAcctType
	 */
	public void setpFAcctType(String pFAcctType) {
		this.pFAcctType = pFAcctType;
	}
	/**
	 * @return pFIdentNo
	 */
	public String getpFIdentNo() {
		return pFIdentNo;
	}
	/**
	 * @param pFIdentNo pFIdentNo
	 */
	public void setpFIdentNo(String pFIdentNo) {
		this.pFIdentNo = pFIdentNo;
	}
	/**
	 * @return pFRealName
	 */
	public String getpFRealName() {
		return pFRealName;
	}
	/**
	 * @param pFRealName pFRealName
	 */
	public void setpFRealName(String pFRealName) {
		this.pFRealName = pFRealName;
	}
	/**
	 * @return pFIpsAcctNo
	 */
	public String getpFIpsAcctNo() {
		return pFIpsAcctNo;
	}
	/**
	 * @param pFIpsAcctNo pFIpsAcctNo
	 */
	public void setpFIpsAcctNo(String pFIpsAcctNo) {
		this.pFIpsAcctNo = pFIpsAcctNo;
	}
	/**
	 * @return pFTrdFee
	 */
	public String getpFTrdFee() {
		return pFTrdFee;
	}
	/**
	 * @param pFTrdFee pFTrdFee
	 */
	public void setpFTrdFee(String pFTrdFee) {
		this.pFTrdFee = pFTrdFee;
	}
	/**
	 * @return pFTrdAmt
	 */
	public String getpFTrdAmt() {
		return pFTrdAmt;
	}
	/**
	 * @param pFTrdAmt pFTrdAmt
	 */
	public void setpFTrdAmt(String pFTrdAmt) {
		this.pFTrdAmt = pFTrdAmt;
	}
	/**
	 * @return pWebUrl
	 */
	public String getpWebUrl() {
		return pWebUrl;
	}
	/**
	 * @param pWebUrl pWebUrl
	 */
	public void setpWebUrl(String pWebUrl) {
		this.pWebUrl = pWebUrl;
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
	 * @return repaymentInvestorInfo
	 */
    public RepaymentInvestorInfo getRepaymentInvestorInfo() {
        return repaymentInvestorInfo;
    }
    /**
     * @param repaymentInvestorInfo repaymentInvestorInfo
     */
    public void setRepaymentInvestorInfo(
            RepaymentInvestorInfo repaymentInvestorInfo) {
        this.repaymentInvestorInfo = repaymentInvestorInfo;
    }
    /**
     * @return repaymentInvestorInfoList
     */
    public List<RepaymentInvestorInfo> getRepaymentInvestorInfoList() {
        return repaymentInvestorInfoList;
    }
    /**
     * @param repaymentInvestorInfoList repaymentInvestorInfoList
     */
    public void setRepaymentInvestorInfoList(
            List<RepaymentInvestorInfo> repaymentInvestorInfoList) {
        this.repaymentInvestorInfoList = repaymentInvestorInfoList;
    }
    public String getpIpsBillNo() {
        return pIpsBillNo;
    }
    public void setpIpsBillNo(String pIpsBillNo) {
        this.pIpsBillNo = pIpsBillNo;
    }
    
    
}
