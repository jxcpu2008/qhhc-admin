package com.hc9.model;

import java.text.DecimalFormat;
import java.util.List;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Userbasicsinfo;

/**
 * 还款提交数据及同步返回数据
 * @author frank 2014-08-25
 *
 */
public class Repayment {
	/**标号*/
	private String pBidNo;
	
	/**还款日期*/
	private String pRepaymentDate;
	
	/**商户还款订单号*/
	private String pMerBillNo;
	
	/**
	 * 还款类型
	 * 还款类型，1#手动还款，2#自动还款
	 */
	private String pRepayType;
	
	/**
	 * 授权号
	 * 当还款类型为自动还款时不为空，为手动还款时为空
	 */
	private String pIpsAuthNo;
	
	/**转出方IPS 账号*/
	private String pOutAcctNo;
	
	/**
	 * 转出金额,此次还款总金额
	 * 转出金额=Sum(pInAmt)
	 * Sum(pInAmt)代表转入金额的合计，一个或多个投资人时的还款金额的累加。
	 * */
	String pOutAmt;
	
	/**
	 * 转出方总手续费
	 * 表示此次借款人或担保人所承担的还款手续费，此手续费由商户平台向用户收取。
	 * pOutFee = Sum(pOutInfoFee)
	 */
	private String pOutFee;
	
	private List<RepaymentInvestor> investorInfos;
	/**浏览器返回地址*/
	private String pWebUrl=Constant.REPAYMENT;
	
	/**异步返回地址*/
	private String pS2SUrl=Constant.REPAYMENTASYNCHRONOUS;

	private String pMemo1;
	private String pMemo2;
	private String pMemo3;
	
	/**
	 * 由IPS 系统生成的唯一流水号，此次还款的批次号
	 * */
	private String pIpsBillNo;
	
	/**
	 * IPS 受理日期
	 * yyyyMMdd
	 */
	private String pIpsDate;
	
	/**
	 * IPS 收取转出方手续费
	 * 此手续费由平台商户垫付给IPS的手续费
	 */
	private String pOutIpsFee;
	
	public Repayment(){
		
	}
	/**
	 * 
	 * @param repaymentrecord 还款记录
	 * @param user 借款人信息
	 * @param pRepayType 还款类型，1#手动还款，2#自动还款
	 * @param pOutAmt 转出金额=Sum(pInAmt)
	 * @param pOutFee pOutFee = Sum(pOutInfoFee)
	 * @param investorInfos 转入方列表
	 */
	public Repayment(Repaymentrecord repaymentrecord, Userbasicsinfo user, 
			String pRepayType, String pOutAmt, String pOutFee,List<RepaymentInvestor> investorInfos) {
		//格式化金额为xxx.xx
		DecimalFormat df=new DecimalFormat("0.00");
		
		this.pBidNo=repaymentrecord.getLoansign().getLoansignbasics().getLoanNumber();
		this.pRepaymentDate=DateUtils.format("yyyyMMdd");
		this.pMerBillNo="HK"+StringUtil.pMerBillNo(); 
		
		this.pRepayType=pRepayType;
		this.pIpsAuthNo="";
		this.pOutAcctNo=user.getUserfundinfo().getpIdentNo();
		
		this.pOutAmt=String.valueOf(df.format(Double.parseDouble(pOutAmt)));
		this.pOutFee=String.valueOf(df.format(Double.parseDouble(pOutFee)));
		this.investorInfos=investorInfos;
		this.pMemo1=String.valueOf(repaymentrecord.getId());
		this.pMemo2="";
		this.pMemo3="";
	}
	/**
	 * 标号
	 * @return
	 */
	public String getpBidNo() {
		return pBidNo;
	}
	/**
	 * 标号
	 * @return
	 */	
	public void setpBidNo(String pBidNo) {
		this.pBidNo = pBidNo;
	}
	/**
	 * 还款日期
	 * @return
	 */
	public String getpRepaymentDate() {
		return pRepaymentDate;
	}
	/**
	 * 还款日期
	 * @return
	 */	
	public void setpRepaymentDate(String pRepaymentDate) {
		this.pRepaymentDate = pRepaymentDate;
	}
	/**
	 * 商户还款订单号
	 * @return
	 */
	public String getpMerBillNo() {
		return pMerBillNo;
	}
	/**
	 * 商户还款订单号
	 * @return
	 */
	public void setpMerBillNo(String pMerBillNo) {
		this.pMerBillNo = pMerBillNo;
	}
	/**
	 * 还款类型
	 * @return
	 */
	public String getpRepayType() {
		return pRepayType;
	}
	/**
	 * 还款类型
	 * @return
	 */
	public void setpRepayType(String pRepayType) {
		this.pRepayType = pRepayType;
	}
	/**
	 * 授权号
	 * @return
	 */
	public String getpIpsAuthNo() {
		return pIpsAuthNo;
	}
	/**
	 * 授权号
	 * @return
	 */	
	public void setpIpsAuthNo(String pIpsAuthNo) {
		this.pIpsAuthNo = pIpsAuthNo;
	}
	/**
	 * 转出方IPS 账号
	 * @return
	 */
	public String getpOutAcctNo() {
		return pOutAcctNo;
	}
	/**
	 * 转出方IPS 账号
	 * @return
	 */	
	public void setpOutAcctNo(String pOutAcctNo) {
		this.pOutAcctNo = pOutAcctNo;
	}
	/**
	 * 转出金额,此次还款总金额
	 * @return
	 */
	public String getpOutAmt() {
		return pOutAmt;
	}
	/**
	 * 转出金额,此次还款总金额
	 * @return
	 */	
	public void setpOutAmt(String pOutAmt) {
		this.pOutAmt = pOutAmt;
	}
	/**
	 * 转出方总手续费
	 * @return
	 */
	public String getpOutFee() {
		return pOutFee;
	}
	/**
	 * 转出方总手续费
	 * @return
	 */	
	public void setpOutFee(String pOutFee) {
		this.pOutFee = pOutFee;
	}
	
	public List<RepaymentInvestor> getInvestorInfos() {
		return investorInfos;
	}
	public void setInvestorInfos(List<RepaymentInvestor> investorInfos) {
		this.investorInfos = investorInfos;
	}
	/**
	 * 浏览器返回地址
	 * @return
	 */
	public String getpWebUrl() {
		return pWebUrl;
	}
	/**
	 * 浏览器返回地址
	 * @return
	 */	
	public void setpWebUrl(String pWebUrl) {
		this.pWebUrl = pWebUrl;
	}
	/**
	 * 异步返回地址
	 * @return
	 */
	public String getpS2SUrl() {
		return pS2SUrl;
	}
	/**
	 * 异步返回地址
	 * @return
	 */	
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
	/**
	 *  由IPS 系统生成的唯一流水号，此次还款的批次号
	 * @return
	 */
	public String getpIpsBillNo() {
		return pIpsBillNo;
	}
	public void setpIpsBillNo(String pIpsBillNo) {
		this.pIpsBillNo = pIpsBillNo;
	}
	/**
	 * IPS 受理日期
	 * @return
	 */
	public String getpIpsDate() {
		return pIpsDate;
	}
	/**
	 * IPS 受理日期
	 * @return
	 */
	public void setpIpsDate(String pIpsDate) {
		this.pIpsDate = pIpsDate;
	}
	/**
	 * IPS 收取转出方手续费
	 * @return
	 */
	public String getpOutIpsFee() {
		return pOutIpsFee;
	}
	/**
	 * 	
	 * IPS 收取转出方手续费
	 * @return
	 */

	public void setpOutIpsFee(String pOutIpsFee) {
		this.pOutIpsFee = pOutIpsFee;
	}
	
	
}
