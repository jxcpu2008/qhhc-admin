package com.hc9.model;

import java.text.DecimalFormat;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Loansignbasics;
/**
 * 新标发布，提交到IPS
 * @author frank
 *
 */
public class RegisterSubject {
	
	/**商户订单号*/
	private String pMerBillNo;
	
	/**标的号*/
	private String pBidNo;
	
	/**商户日期 格式：YYYYMMDD*/
	private String pRegDate=DateUtils.format("yyyyMMdd");
	
	/**借款金额 借款金额 <= 10000.00 万*/
	private String pLendAmt;
	
	/**借款保证金 
	 * 借款保证金，允许冻结的金额，金额单位，不能为负，允许为0；*/
	private String pGuaranteesAmt;
	
	/**借款利率 
	 * 借款利率 < 48%，例如：45.12%传入45.12*/
	private String pTrdLendRate;
	
	/**借款周期类型 
	 * 借款周期类型，1：天；3：月；借款周期 <= 5 年*/
	private String pTrdCycleType;
	
	/**借款周期值 
	 * 借款周期值借款周期 <= 5 年。
	 * 如果借款周期类型为天，则借款周期值<= 1800(360 *5)；
	 * 如果借款周期类型为月，则借款周期值<= 60(12 *5)*/
	private String pTrdCycleValue;
	
	/**借款用途*/
	private String pLendPurpose;
	
	/**还款方式 还款方式，
	 * 1：等额本息，
	 * 2：按月还息到期还本；
	 * 3：等额本金；99：其他；*/
	private String pRepayMode;
	
	/**标的操作类型 标的操作类型，
	 * 1：新增，
	 * 2：结束“新增”代表新增标的，“结束”代表标的正常还清、不需要再还款或者标的流标等情况。
	 * 标的“结束”后，投资人投标冻结金额、担保方保证金、借款人保证金均自动解冻。*/
	private String pOperationType;
	
	/**借款人手续费*/
	private String pLendFee;
	
	/**账户类型
	 * 0#机构（暂未开放）；
	 * 1#个人 
	 * */
	private String pAcctType="1";
	
	/**证件号码 
	 * 真实身份证（个人）/由IPS 颁发的商户号*/
	private String pIdentNo;
	
	/**姓名*/
	private String pRealName;
	
	/**IPS 账户号
	 * 账户类型为1 时，IPS 托管账户号（个人）
	 * 账户类型为0 时，由IPS 颁发的商户号
	 * */
	private String pIpsAcctNo;
	
	/**IPS P2P 订单号*/
	private String pIpsBillNo;
	
	/**IPS 处理时间
	 * 格式为：yyyyMMddHHmmss
	 */
	private String pIpsTime;
	
	/**标的状态
	 * 1：新增；2：募集中；3：进行中；8：结束处理中；9：失败；10：结束；
	 */
	private String pBidStatus;
	
	/**实际冻结金额*/
	private String pRealFreezenAmt;
	
	/**借款金额 ips返回提交的数据*/
	private String pTrdAmt;
	/**状态返回地址1*/
	private String pWebUrl=Constant.REGISTER_SUBJECT;
	
	/**状态返回地址2*/
	private String pS2SUrl=Constant.REGISTER_SUBJECT_ASYNCHRONOUS;
	
	/***/
	private String pMemo1;
	/***/
	private String pMemo2;
	/***/
	private String pMemo3;
	
	public RegisterSubject(){}
	
	/**
	 * 构造方法
	 * @param loansign 标
	 * @param pGuaranteesAmt  借款保证金
	 * @param pOperationType 操作方式 1：新增， 2：结束
	 */
	public RegisterSubject(Loansign loansign,String pGuaranteesAmt,String pOperationType) {
		//格式化金额为xxx.xx
		DecimalFormat df=new DecimalFormat("0.00");
		Loansignbasics loansignbasics=loansign.getLoansignbasics();
		//1新增，生成新的商户订单号；结束，取原有的商户订单号
		if(pOperationType.equals("1")){
			this.pMerBillNo = "DDH"+StringUtil.pMerBillNo();
		}else{
	//		this.pMerBillNo=loansign.getLoansignbasics().getpMerBillNo();
		}
		this.pBidNo = loansign.getLoansignbasics().getLoanNumber();
		
	//	this.pLendAmt = String.valueOf(df.format(loansign.getIssueLoan()));
//		if(null!=loansignbasics.getMgtMoney()){
//			this.pGuaranteesAmt = String.valueOf(df.format(loansignbasics.getGuaranteesAmt()));
	//	}else{
//			this.pGuaranteesAmt="0.00";
	//	}
//		this.pTrdLendRate = String.valueOf(df.format(loansign.getRate()*100));
		//普通标的周期类型为月
	/*	if(loansign.getLoanType()==1){
			this.pTrdCycleType = "3";
			this.pTrdCycleValue = String.valueOf(loansign.getMonth());
		}else if(loansign.getLoanType()==2||loansign.getLoanType()==3){ //天标和秒标标的周期类型为天
			this.pTrdCycleType = "1";
			this.pTrdCycleValue = String.valueOf(loansign.getUseDay());
		}
		this.pLendPurpose = loansign.getLoansignbasics().getBehoof();
		this.pRepayMode = String.valueOf(loansign.getRefundWay());
		this.pOperationType = pOperationType;
		//借款人手续费=借款金额*借款管理费率
		this.pLendFee = String.valueOf(df.format(loansign.getShouldPmfee()));*/
		this.pIdentNo = loansign.getUserbasicsinfo().getUserrelationinfo().getCardId();
		this.pRealName = loansign.getUserbasicsinfo().getName();
		this.pIpsAcctNo = loansign.getUserbasicsinfo().getUserfundinfo().getpIdentNo();
		this.pMemo1 = String.valueOf(loansign.getId());
		this.pMemo2 = "";
		this.pMemo3 = "";
	}
	/**
	 * 商户订单号
	 * @return
	 */
	public String getpMerBillNo() {
		return pMerBillNo;
	}
	/**
	 * 商户订单号
	 * @return
	 */
	public void setpMerBillNo(String pMerBillNo) {
		this.pMerBillNo = pMerBillNo;
	}
	/**
	 * 标的号
	 * @return
	 */
	public String getpBidNo() {
		return pBidNo;
	}
	/**
	 * 标的号
	 * @return
	 */
	public void setpBidNo(String pBidNo) {
		this.pBidNo = pBidNo;
	}
	/**
	 * 商户日期
	 * @return
	 */
	public String getpRegDate() {
		return pRegDate;
	}
	/**
	 * 商户日期
	 * @return
	 */
	public void setpRegDate(String pRegDate) {
		this.pRegDate = pRegDate;
	}
	/**
	 * 借款金额
	 * @return
	 */
	public String getpLendAmt() {
		return pLendAmt;
	}
	/**
	 * 借款金额
	 * @return
	 */
	public void setpLendAmt(String pLendAmt) {
		this.pLendAmt = pLendAmt;
	}
	/**
	 * 借款保证金
	 * @return
	 */
	public String getpGuaranteesAmt() {
		return pGuaranteesAmt;
	}
	/**
	 * 借款保证金
	 * @return
	 */
	public void setpGuaranteesAmt(String pGuaranteesAmt) {
		this.pGuaranteesAmt = pGuaranteesAmt;
	}
	/**
	 * 借款利率
	 * @return
	 */
	public String getpTrdLendRate() {
		return pTrdLendRate;
	}
	/**
	 * 借款利率
	 * @return
	 */
	public void setpTrdLendRate(String pTrdLendRate) {
		this.pTrdLendRate = pTrdLendRate;
	}
	/**
	 * 借款周期类型
	 * @return
	 */
	public String getpTrdCycleType() {
		return pTrdCycleType;
	}
	/**
	 * 借款周期类型
	 * @return
	 */
	public void setpTrdCycleType(String pTrdCycleType) {
		this.pTrdCycleType = pTrdCycleType;
	}

	/**
	 * 借款周期值
	 * @return
	 */
	public String getpTrdCycleValue() {
		return pTrdCycleValue;
	}
	/**
	 * 借款周期值
	 * @return
	 */
	public void setpTrdCycleValue(String pTrdCycleValue) {
		this.pTrdCycleValue = pTrdCycleValue;
	}

	/**
	 * 借款用途
	 * @return
	 */
	public String getpLendPurpose() {
		return pLendPurpose;
	}
	/**
	 * 借款用途
	 * @return
	 */
	public void setpLendPurpose(String pLendPurpose) {
		this.pLendPurpose = pLendPurpose;
	}

	/**
	 * 还款方式
	 * @return
	 */
	public String getpRepayMode() {
		return pRepayMode;
	}
	/**
	 * 还款方式
	 * @return
	 */
	public void setpRepayMode(String pRepayMode) {
		this.pRepayMode = pRepayMode;
	}

	/**
	 * 标的操作类型
	 * @return
	 */
	public String getpOperationType() {
		return pOperationType;
	}
	/**
	 * 标的操作类型
	 * @return
	 */
	public void setpOperationType(String pOperationType) {
		this.pOperationType = pOperationType;
	}

	/**
	 * 借款人手续费
	 * @return
	 */
	public String getpLendFee() {
		return pLendFee;
	}
	/**
	 * 借款人手续费
	 * @return
	 */
	public void setpLendFee(String pLendFee) {
		this.pLendFee = pLendFee;
	}
	
	/**
	 * 账户类型
	 * @return
	 */
	public String getpAcctType() {
		return pAcctType;
	}
	/**
	 * 账户类型
	 * @return
	 */
	public void setpAcctType(String pAcctType) {
		this.pAcctType = pAcctType;
	}

	/**
	 * 证件号码
	 * @return
	 */
	public String getpIdentNo() {
		return pIdentNo;
	}
	/**
	 * 证件号码
	 * @return
	 */
	public void setpIdentNo(String pIdentNo) {
		this.pIdentNo = pIdentNo;
	}

	/**
	 * 姓名
	 * @return
	 */
	public String getpRealName() {
		return pRealName;
	}
	/**
	 * 姓名
	 * @return
	 */
	public void setpRealName(String pRealName) {
		this.pRealName = pRealName;
	}

	/**
	 * @return
	 */
	public String getpIpsAcctNo() {
		return pIpsAcctNo;
	}

	public void setpIpsAcctNo(String pIpsAcctNo) {
		this.pIpsAcctNo = pIpsAcctNo;
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

	public String getpIpsTime() {
		return pIpsTime;
	}

	public void setpIpsTime(String pIpsTime) {
		this.pIpsTime = pIpsTime;
	}

	public String getpBidStatus() {
		return pBidStatus;
	}

	public void setpBidStatus(String pBidStatus) {
		this.pBidStatus = pBidStatus;
	}

	public String getpRealFreezenAmt() {
		return pRealFreezenAmt;
	}

	public void setpRealFreezenAmt(String pRealFreezenAmt) {
		this.pRealFreezenAmt = pRealFreezenAmt;
	}

	public String getpTrdAmt() {
		return pTrdAmt;
	}

	public void setpTrdAmt(String pTrdAmt) {
		this.pTrdAmt = pTrdAmt;
	}
	
	
}
