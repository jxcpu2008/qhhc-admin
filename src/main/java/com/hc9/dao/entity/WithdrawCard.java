package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/** 提现券记录表 */
@Entity
@Table(name = "withdrawCard")
public class WithdrawCard {
	/** 主键id，自增 */
	private Long id;
	
	/** 所属用户主键id */
	private Long userId;
	
	/** 所免费用（提现券券的面值） */
	private Double freeMoney;
	
	/** 实际使用金额 */
	private Double realUseMoney;
	
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
	
	/** 提现券所使用的订单号 */
	private String useOrderNo;
	
	/** 来源类型：1、投资；2、注册；3、奖励；4、抽奖；5、生日福利； */
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

	@Column(name = "freeMoney", nullable = false)
	public Double getFreeMoney() {
		return freeMoney;
	}

	public void setFreeMoney(Double freeMoney) {
		this.freeMoney = freeMoney;
	}

	@Column(name = "realUseMoney")
	public Double getRealUseMoney() {
		return realUseMoney;
	}

	public void setRealUseMoney(Double realUseMoney) {
		this.realUseMoney = realUseMoney;
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
	
	@Column(name = "useOrderNo")
	public String getUseOrderNo() {
		return useOrderNo;
	}

	public void setUseOrderNo(String useOrderNo) {
		this.useOrderNo = useOrderNo;
	}

	@Column(name = "sourceType", nullable = false)
	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}
}
