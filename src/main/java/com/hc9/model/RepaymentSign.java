package com.hc9.model;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Userbasicsinfo;

/**
 * 还款提交数据及同步返回数据
 * @author yu 2014-09-12
 *
 */
public class RepaymentSign {
	/**
	 * 商户签约订单号
	 */
	private String pMerBillNo;
	/**
	 * 签约类型
	 */
	private String pSigningDate;
	/**
	 * 证件类型
	 */
	private String pIdentType="1";
	/**
	 * 借款人证件号
	 */
	private String pIdentNo;
	/**
	 * 借款人姓名
	 */
	private String pRealName;
	/**
	 * ips账号
	 */
	private String pIpsAcctNo;
	/**
	 * 有效期类型
	 */
	private String pValidType="N";
	/**
	 * 有效期
	 */
	private String pValidDate="0";
	/**浏览器返回地址*/
	private String pWebUrl=Constant.REPAYMENT_SIGN;
	
	/**异步返回地址*/
	private String pS2SUrl=Constant.REPAYMENT_SIGN_ASYNCHRONOUS;
	/**备注1*/
	private String pMemo1="";
	/**备注2*/
	private String pMemo2="";
	/**备注3*/
	private String pMemo3="";
	
	
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
	public String getpValidType() {
		return pValidType;
	}
	public void setpValidType(String pValidType) {
		this.pValidType = pValidType;
	}
	public String getpValidDate() {
		return pValidDate;
	}
	public void setpValidDate(String pValidDate) {
		this.pValidDate = pValidDate;
	}
	public String getpWebUrl() {
		return pWebUrl;
	}
	public void setpWebUrl(String pWebUrl) {
		this.pWebUrl = pWebUrl;
	}
	public String getpS2SUrl() {
		return pS2SUrl;
	}
	public void setpS2SUrl(String pS2SUrl) {
		this.pS2SUrl = pS2SUrl;
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
	public RepaymentSign(){}
	public RepaymentSign(Userbasicsinfo user ) {
		this.pMerBillNo = "HKQY"+StringUtil.pMerBillNo(); 
		this.pSigningDate = DateUtils.format("yyyyMMdd");
		this.pIdentNo =user.getUserrelationinfo().getCardId();
		this.pRealName = user.getName();
		this.pIpsAcctNo =  user.getUserfundinfo().getpIdentNo();
		
	}
	
	
}
