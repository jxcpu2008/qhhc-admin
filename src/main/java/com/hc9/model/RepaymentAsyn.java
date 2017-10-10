package com.hc9.model;

import java.util.List;
/**
 * 还款异步返回数据
 * @author frank 2014-8-28
 *
 */
public class RepaymentAsyn {
	/**
	 * 标号（还款时提交的数据）
	 */
	private String pBidNo;
	
	/**
	 * 还款日期（还款时提交的数据）
	 */
	private String pRepaymentDate;
	
	/**
	 * 商户还款订单号（还款时提交的数据）
	 */
	private String pMerBillNo;
	
	/**
	 * IPS 还款订单号（还款时提交的数据）
	 */
	private String pIpsBillNo;
	
	/**
	 * 还款完成日期，IPS 还款处理完成日期
	 */
	private String pIpsDate;
	
	/**
	 * 转入方-批量
	 */
	private List<RepaymentInvestor> investors;
	private String pMemo1;
	private String pMemo2;
	private String pMemo3;
	
	
	
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
	 * IPS 还款订单号
	 * @return
	 */
	public String getpIpsBillNo() {
		return pIpsBillNo;
	}
	/**
	 * IPS 还款订单号
	 * @param pIpsBillNo
	 */
	public void setpIpsBillNo(String pIpsBillNo) {
		this.pIpsBillNo = pIpsBillNo;
	}
	/**
	 * ips处理日期
	 * @return
	 */
	public String getpIpsDate() {
		return pIpsDate;
	}
	/**
	 * ips处理日期
	 * @return
	 */
	public void setpIpsDate(String pIpsDate) {
		this.pIpsDate = pIpsDate;
	}
	public List<RepaymentInvestor> getInvestors() {
		return investors;
	}
	public void setInvestors(List<RepaymentInvestor> investors) {
		this.investors = investors;
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
	
}
