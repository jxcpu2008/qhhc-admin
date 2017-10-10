package com.hc9.model;

/**
 * 注册用户投资记录信息
 * @author Administrator
 *
 */
public class RegUserInvInfo {
	
	private String userName;
	
	private String name;
	
	private int isAuthIps;
	
	private String phone;
	
	private String regTime;
	
	private int registerSource;
	
	private String registerChannel;
	
	private String loanSignName;
	
	private Double investMoney;
	
	private String investTime;
	
	private String inviteName;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRegTime() {
		return regTime;
	}

	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}

	public int getRegisterSource() {
		return registerSource;
	}

	public void setRegisterSource(int registerSource) {
		this.registerSource = registerSource;
	}

	public String getRegisterChannel() {
		return registerChannel;
	}

	public void setRegisterChannel(String registerChannel) {
		this.registerChannel = registerChannel;
	}

	public String getLoanSignName() {
		return loanSignName;
	}

	public void setLoanSignName(String loanSignName) {
		this.loanSignName = loanSignName;
	}

	public Double getInvestMoney() {
		return investMoney;
	}

	public void setInvestMoney(Double investMoney) {
		this.investMoney = investMoney;
	}

	public String getInvestTime() {
		return investTime;
	}

	public void setInvestTime(String investTime) {
		this.investTime = investTime;
	}

	public String getInviteName() {
		return inviteName;
	}

	public void setInviteName(String inviteName) {
		this.inviteName = inviteName;
	}

	public int getIsAuthIps() {
		return isAuthIps;
	}

	public void setIsAuthIps(int isAuthIps) {
		this.isAuthIps = isAuthIps;
	}
}