package com.hc9.common.redis.sys.vo;

/** 项目基本信息辅助对象 */
public class LoansignbasicsVo {
	/**项目简介*/
	private String remark;
	
	/***借款用途*/
	private String behoof;
	
	/**历史情况*/
	private String history;
	
	/** 风控意见 */
	private String riskAdvice;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBehoof() {
		return behoof;
	}

	public void setBehoof(String behoof) {
		this.behoof = behoof;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getRiskAdvice() {
		return riskAdvice;
	}

	public void setRiskAdvice(String riskAdvice) {
		this.riskAdvice = riskAdvice;
	}
	
}
