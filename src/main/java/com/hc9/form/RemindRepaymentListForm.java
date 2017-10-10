package com.hc9.form;
/**
 * 用于接收页面form中的参数
 * 
 * @author yu
 *
 */
public class RemindRepaymentListForm {
	/**
	 * 标题 
	 */
	private String loanTitle;
	/**
	 * 标号
	 */
	private String loanNumber;
	/**
	 * 借款人
	 */
	private String name;
	/**
	 *预还款时间开始 
	 */
	private String preRepayDateStart;
	
	/**
	 *预还款时间结束 
	 */
	private String preRepayDateEnd;
	
	/**
	 * 实际还款时间开始
	 */
	private String factRepayDateStart;
	
	/**
	 * 实际还款时间结束
	 */
	private String factRepayDateEnd;
	/**
	 *发布时间开始
	 */
	private String publishTimeStart;
	
	/**
	 * 发布时间结束
	 */
	private String publishTimeEnd;
	
	/**
	 * 借款标类型
	 */
	private String loanType;
	
	/**
	 * 产品类型
	 */
	private String loanProductType;
	/**
	 * 还款状态
	 */
	private String repayState;
	
	/**
	 * 催收邮件发送次数
	 */
	private String remindEmailCount;
	
	/**
	 * 催收短信发送次数
	 */
	private String remindSMSCount;

	public String getLoanTitle() {
		return loanTitle;
	}

	public void setLoanTitle(String loanTitle) {
		this.loanTitle = loanTitle;
	}

	public String getLoanNumber() {
		return loanNumber;
	}

	public void setLoanNumber(String loanNumber) {
		this.loanNumber = loanNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPreRepayDateStart() {
		return preRepayDateStart;
	}

	public void setPreRepayDateStart(String preRepayDateStart) {
		this.preRepayDateStart = preRepayDateStart;
	}

	public String getPreRepayDateEnd() {
		return preRepayDateEnd;
	}

	public void setPreRepayDateEnd(String preRepayDateEnd) {
		this.preRepayDateEnd = preRepayDateEnd;
	}

	public String getFactRepayDateStart() {
		return factRepayDateStart;
	}

	public void setFactRepayDateStart(String factRepayDateStart) {
		this.factRepayDateStart = factRepayDateStart;
	}

	public String getFactRepayDateEnd() {
		return factRepayDateEnd;
	}

	public void setFactRepayDateEnd(String factRepayDateEnd) {
		this.factRepayDateEnd = factRepayDateEnd;
	}

	public String getPublishTimeStart() {
		return publishTimeStart;
	}

	public void setPublishTimeStart(String publishTimeStart) {
		this.publishTimeStart = publishTimeStart;
	}

	public String getPublishTimeEnd() {
		return publishTimeEnd;
	}

	public void setPublishTimeEnd(String publishTimeEnd) {
		this.publishTimeEnd = publishTimeEnd;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getLoanProductType() {
		return loanProductType;
	}

	public void setLoanProductType(String loanProductType) {
		this.loanProductType = loanProductType;
	}

	public String getRepayState() {
		return repayState;
	}

	public void setRepayState(String repayState) {
		this.repayState = repayState;
	}

	public String getRemindEmailCount() {
		return remindEmailCount;
	}

	public void setRemindEmailCount(String remindEmailCount) {
		this.remindEmailCount = remindEmailCount;
	}

	public String getRemindSMSCount() {
		return remindSMSCount;
	}

	public void setRemindSMSCount(String remindSMSCount) {
		this.remindSMSCount = remindSMSCount;
	}
	
	
}
