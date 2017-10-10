package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/** 加息券记录表 */
@Entity
@Table(name = "interestIncreaseCard")
public class InterestIncreaseCard {
	/** 主键id，自增 */
	private Long id;
	
	/** 所属用户主键id */
	private Long userId;
	
	/** 所加利息（加息券的面值） */
	private Double interestRate;
	
	/** 最低使用门槛 */
	private Double lowestUseMoney;
	
	/** 投资记录id */
	private Long loanrecordId;
	
	/** 领取时间 */
	private String receiveTime;
	
	/** 消费时间 */
	private String consumeTime;
	
	/** 生效开始时间 */
	private String beginTime;
	
	/** 生效结束时间 */
	private String endTime;
	
	/** 是否使用：0、未使用，1、已使用；2、待确认；默认为0 */
	private Integer useFlag;
	
	/** 来源类型：1、投资；2、注册；3、奖励；4、抽奖；9、生日福利; 10、新手任务；11、周周惊喜； */
	private Integer sourceType;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/** 不建议关联用户对象，会导致user对象越来越大 */
	@Column(name = "userId", nullable = false)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "interestRate", nullable = false)
	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	@Column(name = "lowestUseMoney", nullable = false)
	public Double getLowestUseMoney() {
		return lowestUseMoney;
	}

	public void setLowestUseMoney(Double lowestUseMoney) {
		this.lowestUseMoney = lowestUseMoney;
	}

	@Column(name = "loanrecordId")
	public Long getLoanrecordId() {
		return loanrecordId;
	}

	public void setLoanrecordId(Long loanrecordId) {
		this.loanrecordId = loanrecordId;
	}

	@Column(name = "receiveTime", nullable = false)
	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	@Column(name = "consumeTime")
	public String getConsumeTime() {
		return consumeTime;
	}

	public void setConsumeTime(String consumeTime) {
		this.consumeTime = consumeTime;
	}

	@Column(name = "beginTime", nullable = false)
	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	@Column(name = "endTime", nullable = false)
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Column(name = "useFlag", nullable = false)
	public Integer getUseFlag() {
		return useFlag;
	}

	public void setUseFlag(Integer useFlag) {
		this.useFlag = useFlag;
	}

	@Column(name = "sourceType", nullable = false)
	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}
}