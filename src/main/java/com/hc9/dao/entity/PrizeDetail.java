package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/** 奖品发放明细表 */
@Entity
@Table(name = "prizedetail")
public class PrizeDetail implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	/** 主键id */
	private Long id;
	/** 所属用户主键id */
	private Long userId;
	/** 实物奖品类型：1：运动相机；2：体脂秤；3：冰箱卫士；...| 7、蝙蝠侠抱枕（20元）；8、小米迷你风扇+移动电源；
	 * 9、Kindle电子书；10、智能航拍无人机  11、IPad MINI 12、红筹台历 13 聚橙网演唱会门票 */
	private Integer prizeType;
	
	/** 获奖时间 */
	private String receiveTime;
	
	/** 奖品名称 */
	private String prizeName;
	
	/** 奖品数量：默认为1 */
	private Integer prizeNum=1;
	
	/** 投资记录id */
	private Long loanRecordId;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "userId", length = 20)
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name = "prizeType")
	public Integer getPrizeType() {
		return prizeType;
	}
	
	public void setPrizeType(Integer prizeType) {
		this.prizeType = prizeType;
	}
	
	@Column(name = "prizeName")
	public String getPrizeName() {
		return prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	@Column(name = "receiveTime")
	public String getReceiveTime() {
		return receiveTime;
	}
	
	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	@Column(name = "prizeNum")
	public Integer getPrizeNum() {
		return prizeNum;
	}

	public void setPrizeNum(Integer prizeNum) {
		this.prizeNum = prizeNum;
	}

	@Column(name = "loanRecordId")
	public Long getLoanRecordId() {
		return loanRecordId;
	}

	public void setLoanRecordId(Long loanRecordId) {
		this.loanRecordId = loanRecordId;
	}
}
