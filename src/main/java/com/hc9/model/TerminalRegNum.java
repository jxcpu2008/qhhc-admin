package com.hc9.model;

/**
 * 各端口注册人数VO
 * @author Administrator
 *
 */
public class TerminalRegNum {
	
	// 各端口注册人数
	private Long terminalRegNum;
	
	// 端口
	private int registerSource;

	public Long getTerminalRegNum() {
		return terminalRegNum;
	}

	public void setTerminalRegNum(Long terminalRegNum) {
		this.terminalRegNum = terminalRegNum;
	}

	public int getRegisterSource() {
		return registerSource;
	}

	public void setRegisterSource(int registerSource) {
		this.registerSource = registerSource;
	}
}