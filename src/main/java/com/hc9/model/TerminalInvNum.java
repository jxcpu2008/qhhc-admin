package com.hc9.model;

/**
 * 各端口注册人数VO
 * @author Administrator
 *
 */
public class TerminalInvNum {
	
	// 各端口认购人数
	private Long terminalInvNum;
	
	// 各端口认购金额
	private Double InvestMoney;
	
	// 端口
	private int registerSource;
	
	// 百分比
	private double percentage;

	public Long getTerminalInvNum() {
		return terminalInvNum;
	}

	public void setTerminalInvNum(Long terminalInvNum) {
		this.terminalInvNum = terminalInvNum;
	}

	public Double getInvestMoney() {
		return InvestMoney;
	}

	public void setInvestMoney(Double investMoney) {
		InvestMoney = investMoney;
	}

	public int getRegisterSource() {
		return registerSource;
	}

	public void setRegisterSource(int registerSource) {
		this.registerSource = registerSource;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
}