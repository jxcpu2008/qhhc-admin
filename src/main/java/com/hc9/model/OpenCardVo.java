package com.hc9.model;

/** 翻牌抽奖活动实体类 */
public class OpenCardVo {
	/** 投资金额 */
	private Double investMoney;
	
	/** 是否已进行翻牌抽奖活动:0未使用，1已使用 */
	private int useFlag;
	
	/** 中奖结果 */
	private long lotteryResult;

	public Double getInvestMoney() {
		return investMoney;
	}

	public void setInvestMoney(Double investMoney) {
		this.investMoney = investMoney;
	}

	public int getUseFlag() {
		return useFlag;
	}

	public void setUseFlag(int useFlag) {
		this.useFlag = useFlag;
	}

	public long getLotteryResult() {
		return lotteryResult;
	}

	public void setLotteryResult(long lotteryResult) {
		this.lotteryResult = lotteryResult;
	}

}