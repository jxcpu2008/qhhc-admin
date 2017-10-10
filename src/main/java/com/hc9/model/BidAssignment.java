package com.hc9.model;


import java.text.DecimalFormat;
import java.text.ParseException;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Userbasicsinfo;

/**
 * 环迅登记债权转让借口
 * @author lkl
 *
 */
public class BidAssignment {
	
	/**商户订单号*/
    private String pMerBillNo;
    
    /**商户日期 格式：YYYYMMDD*/
	private String pMerDate;
	
	/**原投资交易的标的号*/
	private String pBidNo;
	
	/**原投资交易的合同号*/
	private String pContractNo;
	
	/**出让方账号类型 0:机构(暂不支持) 1：个人*/
	private String pFromAccountType;
	
	/**出让方账号真实姓名*/
	private String pFromName;
	
	/**出让方账户
	 * 出让方账户类型为1 时，IPS 托管账户号（个人）
	 * 出让方账户类型为0 时，由IPS 颁发的商户号
	 */
	private String pFromAccount;
	
	/**出让方证件类型 1#身份证*/
	private String pFromIdentType;
	
	/**出让方证件号码 
	 * 真实身份证(个人)/S由IPS 颁发的商户号
	 */
	private String pFromIdentNo;
	
	/**受让方账号类型 1:个人  0:机构(暂不支持)*/
	private String pToAccountType;
	
	/**受让方账号真实姓名*/
	private String pToAccountName;
	
	/**受让方账号
	 * 受让方账类型为1时，IPS托管账户号(个人)
	 * 受让账号类型为0时，商户号*/
	private String pToAccount;
	
	/**受让方证件类型  1#身份证 默认*/
	private String pToIdentType;
	
	/**受让方证件号码
	 * 真实身份证(个人)/S由IPS 颁发的商户号
	 */
	private String pToIdentNo;
	
	/**登记债权人时提交的订单号*/
	private String pCreMerBillNo;
	
	/**债权面额  金额单位元，不能为负，不允许为0*/
	private String pCretAmt;
	
	/**支付金额  金额单位元，不能为负，不允许为0
	 * 债权面额(1=30%)<=支付金额<=债权面额(1+30%)*/
	private String pPayAmt;
	
	/**出让方手续费  金额单位为元，不能为负，允许为0*/
	private String pFromFee;
	
	/**受让方手续费  金额单位为元，不能为负，允许为0*/
	private String pToFee;
	
	/**转让类型  1：全部转让  2：部分转让*/
	private String pCretType;
	
	/**浏览器返回地址*/
	private String pWebUrl=Constant.BIDASSIGNMENT;
	
	/**异步返回地址*/
	private String pS2SUrl=Constant.ASYNCHRONISMBIDASSIGNMENT;
	
	private String pMemo1;
	
	private String pMemo2;
	
	private String pMemo3;
	
	/**IPS投标订单号 (由 IPS 系统生成的唯一流水号 )*/
	private String pIpsBillNo;	
	
	/**债权转让编号*/
	private String pP2PBillNo;
	
	/**IPS 处理时间,格式为：yyyyMMddHHmmss*/
	private String pIpsTime;
	
	/**业务类型  1：债权转让*/
	private String pBussType;
	
    /**转让状态 0:建设  1:进行中 10: 成功  9：失败*/
	private String pStatus;
	
	public BidAssignment() {
	}

	/***
	 * 
	 * @param user  出让方用户信息
	 * @param userinfo   受让方用户信息
	 * @param loan   原标信息
	 * @param pCretType  转让类型 1：全部转让  2：部分转让
	 * @param money 购标金额
	 */
	public BidAssignment(Userbasicsinfo user,Userbasicsinfo userinfo,Loansign loan,String id,String pCreMerBillNoBid,String pCretType, Double money,Double payMoney) throws ParseException{
		//格式化金额为xxx.xx
		DecimalFormat df=new DecimalFormat("0.00");
		this.pMerBillNo = "ZR"+StringUtil.pMerBillNo();
		this.pMerDate = DateUtils.format("yyyyMMdd");
		/**原始标信息*/
		this.pBidNo = loan.getLoansignbasics().getLoanNumber();
	//	this.pContractNo = loan.getLoansignbasics().getpContractNo();
		
		/**出让方信息*/
		this.pFromAccountType="1";
		this.pFromName=user.getName();
		this.pFromAccount=user.getUserfundinfo().getpIdentNo();
		this.pFromIdentType="1";
		this.pFromIdentNo=user.getUserrelationinfo().getCardId();
		
		/**受让方信息*/
		this.pToAccountType="1";
		this.pToAccountName=userinfo.getName();
		this.pToAccount=userinfo.getUserfundinfo().getpIdentNo();
		this.pToIdentType="1";
		this.pToIdentNo=userinfo.getUserrelationinfo().getCardId();
		
		this.pCreMerBillNo=pCreMerBillNoBid;
		this.pCretAmt=String.valueOf(df.format(money));
		this.pPayAmt=String.valueOf(df.format(payMoney));
		
		this.pCretType=pCretType;
		this.pMemo1 = id;
	    this.pMemo2 = userinfo.getId().toString();
	}

	public String getpMerBillNo() {
		return pMerBillNo;
	}

	public void setpMerBillNo(String pMerBillNo) {
		this.pMerBillNo = pMerBillNo;
	}

	public String getpMerDate() {
		return pMerDate;
	}

	public void setpRegDate(String pMerDate) {
		this.pMerDate = pMerDate;
	}

	public String getpBidNo() {
		return pBidNo;
	}

	public void setpBidNo(String pBidNo) {
		this.pBidNo = pBidNo;
	}

	public String getpContractNo() {
		return pContractNo;
	}

	public void setpContractNo(String pContractNo) {
		this.pContractNo = pContractNo;
	}

	public String getpFromAccountType() {
		return pFromAccountType;
	}

	public void setpFromAccountType(String pFromAccountType) {
		this.pFromAccountType = pFromAccountType;
	}

	public String getpFromName() {
		return pFromName;
	}

	public void setpFromName(String pFromName) {
		this.pFromName = pFromName;
	}

	public String getpFromAccount() {
		return pFromAccount;
	}

	public void setpFromAccount(String pFromAccount) {
		this.pFromAccount = pFromAccount;
	}

	public String getpFromIdentType() {
		return pFromIdentType;
	}

	public void setpFromIdType(String pFromIdentType) {
		this.pFromIdentType = pFromIdentType;
	}

	public String getpFromIdentNo() {
		return pFromIdentNo;
	}

	public void setpFromIdentNo(String pFromIdentNo) {
		this.pFromIdentNo = pFromIdentNo;
	}

	public String getpToAccountType() {
		return pToAccountType;
	}

	public void setpToAccountType(String pToAccountType) {
		this.pToAccountType = pToAccountType;
	}

	public String getpToAccountName() {
		return pToAccountName;
	}

	public void setpToAccountName(String pToAccountName) {
		this.pToAccountName = pToAccountName;
	}

	public String getpToAccount() {
		return pToAccount;
	}

	public void setpToAccount(String pToAccount) {
		this.pToAccount = pToAccount;
	}

	public String getpToIdentType() {
		return pToIdentType;
	}

	public void setpToIdentType(String pToIdentType) {
		this.pToIdentType = pToIdentType;
	}

	public String getpToIdentNo() {
		return pToIdentNo;
	}

	public void setpToIdentNo(String pToIdentNo) {
		this.pToIdentNo = pToIdentNo;
	}

	public String getpCreMerBillNo() {
		return pCreMerBillNo;
	}

	public void setpCreMerBillNo(String pCreMerBillNo) {
		this.pCreMerBillNo = pCreMerBillNo;
	}

	public String getpCretAmt() {
		return pCretAmt;
	}

	public void setpCretAmt(String pCretAmt) {
		this.pCretAmt = pCretAmt;
	}

	public String getpPayAmt() {
		return pPayAmt;
	}

	public void setpPayAmt(String pPayAmt) {
		this.pPayAmt = pPayAmt;
	}

	public String getpFromFee() {
		return pFromFee;
	}

	public void setpFromFee(String pFromFee) {
		this.pFromFee = pFromFee;
	}

	public String getpToFee() {
		return pToFee;
	}

	public void setpToFee(String pToFee) {
		this.pToFee = pToFee;
	}

	public String getpCretType() {
		return pCretType;
	}

	public void setpCretType(String pCretType) {
		this.pCretType = pCretType;
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

	public String getpIpsBillNo() {
		return pIpsBillNo;
	}

	public void setpIpsBillNo(String pIpsBillNo) {
		this.pIpsBillNo = pIpsBillNo;
	}

	public void setpMerDate(String pMerDate) {
		this.pMerDate = pMerDate;
	}

	public void setpFromIdentType(String pFromIdentType) {
		this.pFromIdentType = pFromIdentType;
	}

	public String getpP2PBillNo() {
		return pP2PBillNo;
	}

	public void setpP2PBillNo(String pP2PBillNo) {
		this.pP2PBillNo = pP2PBillNo;
	}

	public String getpIpsTime() {
		return pIpsTime;
	}

	public void setpIpsTime(String pIpsTime) {
		this.pIpsTime = pIpsTime;
	}

	public String getpBussType() {
		return pBussType;
	}

	public void setpBussType(String pBussType) {
		this.pBussType = pBussType;
	}

	public String getpStatus() {
		return pStatus;
	}

	public void setpStatus(String pStatus) {
		this.pStatus = pStatus;
	}
	
	
	
	
	
	
	
	
	
	
}
