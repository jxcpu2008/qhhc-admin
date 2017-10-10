package com.hc9.model;


/**
 * 合同模板
 * 
 * @author frank
 * 
 */
public class LoanContract {

	/**
	 * 合同编号
	 */
	private String contractId;
	
	/***
	 * 项目编号
	 */
	private String loansignId;
	
	/***
	 * 项目名称
	 */
	private  String loansignName;

	/**
	 * 甲方（出借人）
	 */
	private String partyA;
	
	/***
	 * 出借人用户名
	 */
	private String partyAName;
	
	/**出借人证件类型*/
	private String partyACardType;
	
	/***
	 * 出借人证件号
	 */
    private String partyACardNo;
	
    /**出借人手机号*/
    private String partyAPhone;
	/**
	 * 乙方 （借款人）
	 */
	private String partyB;
	
	/***
	 * 借款人用户名
	 */
	private String partyBName;
	
	/**借款人证件类型*/
	private String partyBCardType;
	/***
	 * 借款人证件号
	 */
	private String partyBCardNo;
	
    /**借款人手机号*/
    private String partyBPhone;
	
	/***
	 * 第三方担保服务机构
	 */
	private String partyC;
	
	/***
	 * 第三方担保服务机构企业名
	 */
	private String partyCName;
	
	/***
	 * 第三方担保服务机构宝付ID
	 */
	private String partyCCusId;

	/**
	 * 甲方 借出的 金额（人民币 元） 阿拉伯数字
	 */
	private double loanMoney;

	/**
	 * 甲方 借出的 金额（人民币 元） 中文大写数字
	 */
	private String loanMoneyUpper;

	/**
	 * 借款期限(月份天数)
	 */
	private String borrowMonth;

	/**
	 * 借款年利润
	 */
	private double rate;
	/**
	 * 还款方式
	 */
	private String bonaType;

	/**
	 * 协议签订地点
	 */
	private String signedAddress;

	/**
	 * 放款日期
	 */
	private String creditYear;
	
	private String creditMonth;
	
	private String creditDay;
	

	/**
	 * 还款日期
	 */
	private String repayYear;
	
	private  String repayMonth;
	
	private String repayDay;
	
	/**
	 * 每月还款日
	 */
	private String monthBackDay;

	

	/**
	 * 还款本息
	 */
	private double repaymoney;

	/**
	 * 借款用途
	 */
	private String behoof;

	/**
	 * 月偿还本息
	 */
	private String repayMonthMoney;

	 /***
     * 利率
     */
    private String subTypeRate;
    
    /***
     *  投标类型: 1-优先，2-夹层，3-列后 4-vip众筹，5-股东众筹，
     */
    private String subType;
	
	/**
	 * contractId
	 * 
	 * @return contractId
	 */
	public String getContractId() {
		return contractId;
	}

	/**
	 * contractId
	 * 
	 * @param contractId
	 *            contractId
	 */
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	/**
	 * partyA
	 * 
	 * @return partyA
	 */
	public String getPartyA() {
		return partyA;
	}

	/**
	 * partyA
	 * 
	 * @param partyA
	 *            partyA
	 */
	public void setPartyA(String partyA) {
		this.partyA = partyA;
	}

	

	/**
	 * partyB
	 * 
	 * @return partyB
	 */
	public String getPartyB() {
		return partyB;
	}

	/**
	 * partyB
	 * 
	 * @param partyB
	 *            partyB
	 */
	public void setPartyB(String partyB) {
		this.partyB = partyB;
	}

	
	/**
	 * loanMoney
	 * 
	 * @return loanMoney
	 */
	public double getLoanMoney() {
		return loanMoney;
	}

	/**
	 * loanMoney
	 * 
	 * @param loanMoney
	 *            loanMoney
	 * @throws Exception
	 *             Exception
	 */
	public void setLoanMoney(double loanMoney) {
		this.loanMoney = loanMoney;
	}

	/**
	 * loanMoneyUpper
	 * 
	 * @return loanMoneyUpper
	 */
	public String getLoanMoneyUpper() {
		return loanMoneyUpper;
	}

	/**
	 * loanMoneyUpper
	 * 
	 * @param loanMoneyUpper
	 *            loanMoneyUpper
	 */
	public void setLoanMoneyUpper(String loanMoneyUpper) {
		this.loanMoneyUpper = loanMoneyUpper;
	}






	/**
	 * rate
	 * 
	 * @return rate
	 */
	public double getRate() {
		return rate;
	}

	/**
	 * rate
	 * 
	 * @param rate
	 *            rate
	 */
	public void setRate(double rate) {
		this.rate = rate;
	}

	/**
	 * bonaType
	 * 
	 * @return bonaType
	 */
	public String getBonaType() {
		return bonaType;
	}

	/**
	 * bonaType
	 * 
	 * @param bonaType
	 *            bonaType
	 */
	public void setBonaType(String bonaType) {
		this.bonaType = bonaType;
	}

	/**
	 * signedAddress
	 * 
	 * @return signedAddress
	 */
	public String getSignedAddress() {
		return signedAddress;
	}

	/**
	 * signedAddress
	 * 
	 * @param signedAddress
	 *            signedAddress
	 */
	public void setSignedAddress(String signedAddress) {
		this.signedAddress = signedAddress;
	}


	public double getRepaymoney() {
		return repaymoney;
	}

	public void setRepaymoney(double repaymoney) {
		this.repaymoney = repaymoney;
	}

	public String getBehoof() {
		return behoof;
	}

	public void setBehoof(String behoof) {
		this.behoof = behoof;
	}

	public String getRepayMonthMoney() {
		return repayMonthMoney;
	}

	public void setRepayMonthMoney(String repayMonthMoney) {
		this.repayMonthMoney = repayMonthMoney;
	}


	public String getMonthBackDay() {
		return monthBackDay;
	}

	public void setMonthBackDay(String monthBackDay) {
		this.monthBackDay = monthBackDay;
	}

	public String getPartyAName() {
		return partyAName;
	}

	public void setPartyAName(String partyAName) {
		this.partyAName = partyAName;
	}



	public String getPartyBName() {
		return partyBName;
	}

	public void setPartyBName(String partyBName) {
		this.partyBName = partyBName;
	}


	public String getPartyC() {
		return partyC;
	}

	public void setPartyC(String partyC) {
		this.partyC = partyC;
	}

	public String getPartyCName() {
		return partyCName;
	}

	public void setPartyCName(String partyCName) {
		this.partyCName = partyCName;
	}

	public String getPartyCCusId() {
		return partyCCusId;
	}

	public void setPartyCCusId(String partyCCusId) {
		this.partyCCusId = partyCCusId;
	}

	public String getLoansignId() {
		return loansignId;
	}

	public void setLoansignId(String loansignId) {
		this.loansignId = loansignId;
	}

	public String getLoansignName() {
		return loansignName;
	}

	public void setLoansignName(String loansignName) {
		this.loansignName = loansignName;
	}

	public String getBorrowMonth() {
		return borrowMonth;
	}

	public void setBorrowMonth(String borrowMonth) {
		this.borrowMonth = borrowMonth;
	}



	public String getCreditYear() {
		return creditYear;
	}

	public void setCreditYear(String creditYear) {
		this.creditYear = creditYear;
	}

	public String getCreditMonth() {
		return creditMonth;
	}

	public void setCreditMonth(String creditMonth) {
		this.creditMonth = creditMonth;
	}

	public String getCreditDay() {
		return creditDay;
	}

	public void setCreditDay(String creditDay) {
		this.creditDay = creditDay;
	}

	public String getRepayYear() {
		return repayYear;
	}

	public void setRepayYear(String repayYear) {
		this.repayYear = repayYear;
	}

	public String getRepayMonth() {
		return repayMonth;
	}

	public void setRepayMonth(String repayMonth) {
		this.repayMonth = repayMonth;
	}

	public String getRepayDay() {
		return repayDay;
	}

	public void setRepayDay(String repayDay) {
		this.repayDay = repayDay;
	}

	public String getSubTypeRate() {
		return subTypeRate;
	}

	public void setSubTypeRate(String subTypeRate) {
		this.subTypeRate = subTypeRate;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getPartyACardType() {
		return partyACardType;
	}

	public void setPartyACardType(String partyACardType) {
		this.partyACardType = partyACardType;
	}

	public String getPartyACardNo() {
		return partyACardNo;
	}

	public void setPartyACardNo(String partyACardNo) {
		this.partyACardNo = partyACardNo;
	}

	public String getPartyBCardType() {
		return partyBCardType;
	}

	public void setPartyBCardType(String partyBCardType) {
		this.partyBCardType = partyBCardType;
	}

	public String getPartyBCardNo() {
		return partyBCardNo;
	}

	public void setPartyBCardNo(String partyBCardNo) {
		this.partyBCardNo = partyBCardNo;
	}

	public String getPartyAPhone() {
		return partyAPhone;
	}

	public void setPartyAPhone(String partyAPhone) {
		this.partyAPhone = partyAPhone;
	}

	public String getPartyBPhone() {
		return partyBPhone;
	}

	public void setPartyBPhone(String partyBPhone) {
		this.partyBPhone = partyBPhone;
	}

	 

}
