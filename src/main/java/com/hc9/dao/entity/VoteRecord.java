package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/** 投票信息表 */
@Entity
@Table(name = "voterecord")
public class VoteRecord {
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

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "voterId", nullable = false)
	public Long getVoterId() {
		return voterId;
	}

	public void setVoterId(Long voterId) {
		this.voterId = voterId;
	}

	@Column(name = "votedId", nullable = false)
	public String getVotedId() {
		return votedId;
	}

	public void setVotedId(String votedId) {
		this.votedId = votedId;
	}

	@Column(name = "totalNum")
	public Long getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Long totalNum) {
		this.totalNum = totalNum;
	}

	@Column(name = "status", nullable = false)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "createTime", nullable = false)
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}