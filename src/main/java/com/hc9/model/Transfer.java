package com.hc9.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;

/**
 * 转帐的实体类
 * @author frank
 *
 */
public class Transfer {
	
	/**商户订单号*/
	private String pMerBillNo;
	
	/**标的号*/
	private String pBidNo;
	
	/**
	 * 商户日期
	 * 格式： YYYYMMDD
	 * */
	private String pDate;
	
	/**
	 * 转账类型
	 * 1：投资 （报文提交关系，转出方 ：入=N ：1）
	 * 2：代偿（报文提交关系，转出方 ：入=1 ：N）
	 * 3：代偿还款 ：代偿还款 （报文提交关系，转出方 ：入（报文提交关系，转出方 ：入（报文提交关系，转出方 ：入=1 ：1）
	 * 4：债权转让 ：债权转让 （报文提交关系，转出方 ：入（报文提交关系，转出方 ：入（报文提交关系，转出方 ：入=1 ：1）
	 * 5：结算担保收益 ：结算担保收益 （报文提 交关系，转出方 ：入交关系，转出方 ：入交关系，转出方 ：入=1 ： 1）
	 */
	private String pTransferType;
	
	/**
	 * 转账方式
	 * 1：逐笔入账：不将转款项汇总，而是按明细交易一笔计入 账户
	 * 2：批量入帐：针对投资，将明细交易按 1笔汇总本金和 1笔汇总手续费记入借款人帐户
	 * 当转账类型为“ 1：投资”时，可选择 1或 2。其余交 易只能选 1
	 */
	private String pTransferMode;
	
	/**IPS处理时间*/
	private String pIpsTime;
	
	/**返回地址*/
	private String pS2SUrl=Constant.SHOP;
	
	/**环讯返回的订单号*/
	private String pIpsBillNo;
	
	private List<PRow> pRows;
	/**备注1，借款金额*/
	private String pMemo1;
	/**备注2 存放款日期*/
	private String pMemo2;
	/**备注3，存标的id和后台人员id loanId:adminId*/
	private String pMemo3;
	
	
	
	/**
	 * 
	 * @param loan 借款标的
	 * @param pTransferType	转账类型
	 * @param pTransferMode	转账方式
	 * @param adminId	管理员id
	 * @param vip	借款人是否vip
	 */
	public Transfer(Loansign loan, String pTransferType, String pTransferMode, Long adminId,boolean vip) {
		//格式化金额为xxx.xx
		DecimalFormat df=new DecimalFormat("0.00");
		this.pMerBillNo = "ZZ"+StringUtil.pMerBillNo();;
		this.pBidNo = loan.getLoansignbasics().getLoanNumber();
		this.pDate = DateUtils.format("yyyyMMdd");;
		this.pTransferType = pTransferType;
		this.pTransferMode = pTransferMode;
		this.pRows = new ArrayList<>();
		List<Loanrecord> lr=new ArrayList<>(loan.getLoanrecords());
		//转账不计手续费
		for(Loanrecord item:lr){
			PRow pr=new PRow();
			pr.setpOriMerBillNo(item.getOrder_id().toString());
			pr.setpTrdAmt(String.valueOf(df.format(item.getTenderMoney())));
			//设置转出方账户类型为1：个人账号
			pr.setpFAcctType("1");
			//转出方--投标人的ips账号
			pr.setpFIpsAcctNo(item.getUserbasicsinfo().getUserfundinfo().getpIdentNo());
			//转账暂不计手续费
			//投标人的手续费
			Double tbfee=0.00;
			//计算投资者手续费
		//	tbfee=item.getIsPrivilege()==1?(item.getTenderMoney()*loan.getVipMfeeratio()>loan.getVipMfeetop()?loan.getVipMfeetop():item.getTenderMoney()*loan.getVipMfeeratio()):item.getTenderMoney()*loan.getMfeeratio();
			pr.setpFTrdFee(String.valueOf(df.format(tbfee)));
			//设置转入方的咱户类型
			pr.setpTAcctType("1");
			//转入方--借款人的ips
			pr.setpTIpsAcctNo(loan.getUserbasicsinfo().getUserfundinfo().getpIdentNo());
			//手续费
			Double pttrdfee=0.00;
			//计算借款者手续费
		//	pttrdfee=vip?(item.getTenderMoney()*loan.getVipPmfeeratio()>loan.getVipPmfeetop()?loan.getVipPmfeetop():item.getTenderMoney()*loan.getVipPmfeeratio()):item.getTenderMoney()*loan.getPmfeeratio();
			pr.setpTTrdFee(String.valueOf(df.format(0.00)));
			this.pRows.add(pr);
		}
	//	this.pMemo1 = String.valueOf(loan.getIssueLoan());
		this.pMemo2 = DateUtils.format("yyyy-MM-dd hh:mm:ss");
		this.pMemo3 = String.valueOf(loan.getId())+":"+String.valueOf(adminId);
	}
	/**
	 * 
	 * @param loan 
	 * @param pTransferType	转账类型
	 * @param pTransferMode	转账方式
	 */
	public Transfer(Loansign loan, String pTransferType, String pTransferMode, Long adminId,Loanrecord loanrecord) {
		//格式化金额为xxx.xx
		DecimalFormat df=new DecimalFormat("0.00");
		this.pMerBillNo = "ZR"+StringUtil.pMerBillNo();;
		this.pBidNo = loan.getLoansignbasics().getLoanNumber();
		this.pDate = DateUtils.format("yyyyMMdd");;
		this.pTransferType = pTransferType;
		this.pTransferMode = pTransferMode;
		this.pRows = new ArrayList<>();
		PRow pr=new PRow();
	//	pr.setpOriMerBillNo(loanrecord.getpMerBillNo());
		pr.setpTrdAmt(String.valueOf(df.format(loanrecord.getTenderMoney())));
		//转账不计手续费
		//设置转出方账户类型为1：个人账号
		pr.setpFAcctType("1");
		//投标人的手续费
		Double tbfee=0.00;
		//转出方--投标人的ips账号
		pr.setpFIpsAcctNo(loanrecord.getUserbasicsinfo().getUserfundinfo().getpIdentNo());
		//TODO 先设定手续费为0.00
		pr.setpFTrdFee(String.valueOf(df.format(tbfee)));
		//设置转入方的咱户类型
		pr.setpTAcctType("1");
		//转入方--借款人的ips
		pr.setpTIpsAcctNo(loan.getUserbasicsinfo().getUserfundinfo().getpIdentNo());
		pr.setpTTrdFee(String.valueOf("0.00"));
	//	pr.setpTTrdFee(String.valueOf(df.format(loan.getShouldPmfee())));
		this.pRows.add(pr);
		this.pMemo1 = String.valueOf(loan.getIssueLoan());
		this.pMemo2 = DateUtils.format("yyyy-MM-dd hh:mm:ss");
		this.pMemo3 = String.valueOf(loan.getId())+":"+String.valueOf(adminId);
		this.pS2SUrl=Constant.LOANSASSIGNMENT;
	}
	public Transfer(){}
	public String getpMerBillNo() {
		return pMerBillNo;
	}
	public void setpMerBillNo(String pMerBillNo) {
		this.pMerBillNo = pMerBillNo;
	}
	public String getpBidNo() {
		return pBidNo;
	}
	public void setpBidNo(String pBidNo) {
		this.pBidNo = pBidNo;
	}
	public String getpDate() {
		return pDate;
	}
	public void setpDate(String pDate) {
		this.pDate = pDate;
	}
	public String getpTransferType() {
		return pTransferType;
	}
	public void setpTransferType(String pTransferType) {
		this.pTransferType = pTransferType;
	}
	public String getpTransferMode() {
		return pTransferMode;
	}
	public void setpTransferMode(String pTransferMode) {
		this.pTransferMode = pTransferMode;
	}
	public String getpS2SUrl() {
		return pS2SUrl;
	}
	public void setpS2SUrl(String pS2SUrl) {
		this.pS2SUrl = pS2SUrl;
	}
	public List<PRow> getpRows() {
		return pRows;
	}
	public void setpRows(List<PRow> pRows) {
		this.pRows = pRows;
	}
	/**存标的借款金额*/
	public String getpMemo1() {
		return pMemo1;
	}
	/**存标的借款金额*/
	public void setpMemo1(String pMemo1) {
		this.pMemo1 = pMemo1;
	}
	/**存放款日期*/
	public String getpMemo2() {
		return pMemo2;
	}
	/**存放款日期*/
	public void setpMemo2(String pMemo2) {
		this.pMemo2 = pMemo2;
	}
	/**存标的id*/
	public String getpMemo3() {
		return pMemo3;
	}
	/**存标的id*/
	public void setpMemo3(String pMemo3) {
		this.pMemo3 = pMemo3;
	}
	public String getpIpsTime() {
		return pIpsTime;
	}
	public void setpIpsTime(String pIpsTime) {
		this.pIpsTime = pIpsTime;
	}
	public String getpIpsBillNo() {
		return pIpsBillNo;
	}
	public void setpIpsBillNo(String pIpsBillNo) {
		this.pIpsBillNo = pIpsBillNo;
	}

	
	
}
