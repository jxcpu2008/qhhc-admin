package com.hc9.model.report;

public class ConcertActivityVo {
	/**用户名*/
    private String userName;
    /**真实性*/
    private String name;
    /**手机号*/
    private String mobilePhone;
    /**注册时间*/
    private String createTime;
    /**认购金额*/
    private Double tenderMoney;
    /**认购时间*/
    private String tenderTime;
    /**认购产品*/
    private String loanSignName;
    /**门票张数*/
    private Integer ticketNumber;
    /**注册渠道*/
    private String registerSource;
    /**开始时间*/
    private String startTime;
    /**结束时间*/
    private String stopTime;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Double getTenderMoney() {
		return tenderMoney;
	}
	public void setTenderMoney(Double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}
	public String getTenderTime() {
		return tenderTime;
	}
	public void setTenderTime(String tenderTime) {
		this.tenderTime = tenderTime;
	}
	public String getLoanSignName() {
		return loanSignName;
	}
	public void setLoanSignName(String loanSignName) {
		this.loanSignName = loanSignName;
	}
	
	public Integer getTicketNumber() {
		return ticketNumber;
	}
	public void setTicketNumber(Integer ticketNumber) {
		this.ticketNumber = ticketNumber;
	}
	
	public String getRegisterSource() {
		return registerSource;
	}
	public void setRegisterSource(String registerSource) {
		this.registerSource = registerSource;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getStopTime() {
		return stopTime;
	}
	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}
    
}
