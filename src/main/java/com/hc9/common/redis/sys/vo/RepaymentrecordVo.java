package com.hc9.common.redis.sys.vo;

/** 还款记录相关辅助值对象 */
public class RepaymentrecordVo {
	/** 还款状态：1未还款、2按时还款、3逾期未还款、4逾期已还款、5提前还款 */
    private Integer repayState;
    
    /** 期数 */
    private Integer periods;
    
    /** 实际还款时间 */
    private String repayTime;

	public Integer getRepayState() {
		return repayState;
	}

	public void setRepayState(Integer repayState) {
		this.repayState = repayState;
	}

	public Integer getPeriods() {
		return periods;
	}

	public void setPeriods(Integer periods) {
		this.periods = periods;
	}

	public String getRepayTime() {
		return repayTime;
	}

	public void setRepayTime(String repayTime) {
		this.repayTime = repayTime;
	}    
}