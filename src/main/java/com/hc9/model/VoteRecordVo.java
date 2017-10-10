package com.hc9.model;

/** 投票信息表 */
public class VoteRecordVo {
	/** 主键id，自增 */
	private Long id;
	
	/** 投票人用户id */
	private Long voterId;
	
	/** 被投票方标识id */
	private String votedId;
	
	/** 总投票数（活动结束后统一更新一次，默认为1） */
	private Long totalNum;
	
	/** 状态：1、被投票人胜利；0、被投票人没胜利；（默认0） */
	private Integer status;
	
	/** 创建时间 */
	private String createTime;
	
	/** 投票人姓名 */
	private String voterName;
	
	/** 投资项目id */
	private Long loanSignId;
	
	/** 投资记录id */
	private Long loanRecordId;
	
	/** 投资金额 */
	private Double tenderMoney;
	
	/**回购期限*/
	private Integer remonth;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVoterId() {
		return voterId;
	}

	public void setVoterId(Long voterId) {
		this.voterId = voterId;
	}

	public String getVotedId() {
		return votedId;
	}

	public void setVotedId(String votedId) {
		this.votedId = votedId;
	}

	public Long getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Long totalNum) {
		this.totalNum = totalNum;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getVoterName() {
		return voterName;
	}

	public void setVoterName(String voterName) {
		this.voterName = voterName;
	}

	public Long getLoanSignId() {
		return loanSignId;
	}

	public void setLoanSignId(Long loanSignId) {
		this.loanSignId = loanSignId;
	}

	public Long getLoanRecordId() {
		return loanRecordId;
	}

	public void setLoanRecordId(Long loanRecordId) {
		this.loanRecordId = loanRecordId;
	}

	public Double getTenderMoney() {
		return tenderMoney;
	}

	public void setTenderMoney(Double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}

	public Integer getRemonth() {
		return remonth;
	}

	public void setRemonth(Integer remonth) {
		this.remonth = remonth;
	}
}