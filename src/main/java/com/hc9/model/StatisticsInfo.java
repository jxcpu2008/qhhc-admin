package com.hc9.model;

/** 统计信息相关 */
public class StatisticsInfo {
	/** x轴标题 */
	private String lableName;
	
	/** 开始时间 */
	private String beginDate;
	
	/** 结束时间 */
	private String endDate;
	
	/** 基数器 */
	private Integer index;

	/** 注册人数 */
	private Long registerNum;
	
	/** 购买人数 */
	private Long registerBuyNum;
	
	/** 投资金额 */
	private Double investMoney;

	/** 百分比 */
	private double percentRate;
	
	public String getLableName() {
		return lableName;
	}

	public void setLableName(String lableName) {
		this.lableName = lableName;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Long getRegisterNum() {
		return registerNum;
	}

	public void setRegisterNum(Long registerNum) {
		this.registerNum = registerNum;
	}

	public Long getRegisterBuyNum() {
		return registerBuyNum;
	}

	public void setRegisterBuyNum(Long registerBuyNum) {
		this.registerBuyNum = registerBuyNum;
	}

	public Double getInvestMoney() {
		return investMoney;
	}

	public void setInvestMoney(Double investMoney) {
		this.investMoney = investMoney;
	}

	public double getPercentRate() {
		return percentRate;
	}

	public void setPercentRate(double percentRate) {
		this.percentRate = percentRate;
	}
}
