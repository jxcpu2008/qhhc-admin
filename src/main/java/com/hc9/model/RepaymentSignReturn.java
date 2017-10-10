package com.hc9.model;
/**
 * 自动签约还回结果
 * @author yu
 *
 */
public class RepaymentSignReturn {
	/**
	 * 商户签约订单号
	 */
	private String pMerBillNo;
	/**
	 * 商户签约日期
	 */
	private String pSigningDate;
	/**
	 * ips订单号
	 */
	private String pP2PBillNo;
	/**
	 * 证件类型
	 */
	private String pIdentType;
	/**
	 * 借款人证件号码
	 */
	private String pIdentNo;
	/**
	 * 借款人姓名
	 */
	private String pRealName;
	/**
	 * 借款人IPS账号
	 */
	private String pIpsAcctNo;
	/**
	 * 授权号
	 */
	private String pIpsAuthNo;
	/**
	 * 有效期
	 */
	private String pValidDate;
	/**
	 * IPS处理时间
	 */
	private String pIpsTime;
	/**
	 * 备注
	 */
	private String pMemo1;
	/**
	 * 备注
	 */
	private String pMemo2;
	/**
	 * 备注
	 */
	private String pMemo3;
	public String getpMerBillNo() {
		return pMerBillNo;
	}
	public void setpMerBillNo(String pMerBillNo) {
		this.pMerBillNo = pMerBillNo;
	}
	public String getpSigningDate() {
		return pSigningDate;
	}
	public void setpSigningDate(String pSigningDate) {
		this.pSigningDate = pSigningDate;
	}
	public String getpIdentType() {
		return pIdentType;
	}
	public void setpIdentType(String pIdentType) {
		this.pIdentType = pIdentType;
	}
	public String getpIdentNo() {
		return pIdentNo;
	}
	public void setpIdentNo(String pIdentNo) {
		this.pIdentNo = pIdentNo;
	}
	public String getpRealName() {
		return pRealName;
	}
	public void setpRealName(String pRealName) {
		this.pRealName = pRealName;
	}
	public String getpIpsAcctNo() {
		return pIpsAcctNo;
	}
	public void setpIpsAcctNo(String pIpsAcctNo) {
		this.pIpsAcctNo = pIpsAcctNo;
	}
	public String getpIpsAuthNo() {
		return pIpsAuthNo;
	}
	public void setpIpsAuthNo(String pIpsAuthNo) {
		this.pIpsAuthNo = pIpsAuthNo;
	}
	public String getpValidDate() {
		return pValidDate;
	}
	public void setpValidDate(String pValidDate) {
		this.pValidDate = pValidDate;
	}
	public String getpIpsTime() {
		return pIpsTime;
	}
	public void setpIpsTime(String pIpsTime) {
		this.pIpsTime = pIpsTime;
	}
	public String getpMemo1() {
		return pMemo1;
	}
	public void setpMemo1(String pMemo1) {
		this.pMemo1 = pMemo1;
	}
	public String getpMemo2() {
		return pMemo2;
	}
	public void setpMemo2(String pMemo2) {
		this.pMemo2 = pMemo2;
	}
	public String getpMemo3() {
		return pMemo3;
	}
	public void setpMemo3(String pMemo3) {
		this.pMemo3 = pMemo3;
	}
	public String getpP2PBillNo() {
		return pP2PBillNo;
	}
	public void setpP2PBillNo(String pP2PBillNo) {
		this.pP2PBillNo = pP2PBillNo;
	}
	
	
}
