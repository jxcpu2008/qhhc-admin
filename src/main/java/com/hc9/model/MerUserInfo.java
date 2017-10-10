package com.hc9.model;

public class MerUserInfo {
	/**平台账号*/
	private String pMerCode;
	/**ips账户号*/
	private String pIpsAcctNo;
	/**注册邮箱*/
	private String pEmail;
	/**账号状态
	 * 01 未开户； 02 开户成功未激活账； 03 开户失败； 04 已激活账户； 05 已删除
	 * */
	private String pStatus;
	/**身份证验状态
	 * 01 未验证； 02 验证通过， 03 验证不通过
	 * */
	private String pUCardStatus;
	/** 提现银行名称 */
	private String pBankName;
	/**银行卡号后四位*/
	private String pBankCard;
	/**
	 * 银行卡状态 
	 * 01 未登记 ；02 已登记 03 登记失败
	 * */
	private String pBCardStatus;
	/**
	 * 代扣签约状态
	 * 01签约申请中 02 签约处理中 03 签约失败(签约拒绝)
	 * */
	private String pSignStatus;
	
	/** MD5摘要 */
	private String pSign;
	
	/**备注*/
	private String pMemo;
	/**
	 * 查询是否成功
	 * 0000 成功；01 查询不到 **** 商户号 ,02 查询不到 *** 账户号
	 * */
	private String pErrCode;
	
	public String getpMerCode() {
		return pMerCode;
	}
	public void setpMerCode(String pMerCode) {
		this.pMerCode = pMerCode;
	}
	public String getpIpsAcctNo() {
		return pIpsAcctNo;
	}
	public void setpIpsAcctNo(String pIpsAcctNo) {
		this.pIpsAcctNo = pIpsAcctNo;
	}
	public String getpEmail() {
		return pEmail;
	}
	public void setpEmail(String pEmail) {
		this.pEmail = pEmail;
	}
	public String getpStatus() {
		return pStatus;
	}
	public void setpStatus(String pStatus) {
		this.pStatus = pStatus;
	}
	public String getpUCardStatus() {
		return pUCardStatus;
	}
	public void setpUCardStatus(String pUCardStatus) {
		this.pUCardStatus = pUCardStatus;
	}
	public String getpBankName() {
		return pBankName;
	}
	public void setpBankName(String pBankName) {
		this.pBankName = pBankName;
	}
	public String getpBankCard() {
		return pBankCard;
	}
	public void setpBankCard(String pBankCard) {
		this.pBankCard = pBankCard;
	}
	public String getpBCardStatus() {
		return pBCardStatus;
	}
	public void setpBCardStatus(String pBCardStatus) {
		this.pBCardStatus = pBCardStatus;
	}
	public String getpSignStatus() {
		return pSignStatus;
	}
	public void setpSignStatus(String pSignStatus) {
		this.pSignStatus = pSignStatus;
	}
	public String getpSign() {
		return pSign;
	}
	public void setpSign(String pSign) {
		this.pSign = pSign;
	}
	public String getpMemo() {
		return pMemo;
	}
	public void setpMemo(String pMemo) {
		this.pMemo = pMemo;
	}
	public String getpErrCode() {
		return pErrCode;
	}
	public void setpErrCode(String pErrCode) {
		this.pErrCode = pErrCode;
	}
	
}
