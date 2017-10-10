package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/** 项目消息提醒信息表对应实体类 */
@Entity
@Table(name = "msgreminder")
public class MsgReminder implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	/** 主键id，自增 */
	private Long id;
	
	/** 项目id */
	private Long loansignId;
	
	/** 消息接收人名称 */
	private String receiverName;
	
	/** 接收人手机号 */
	private String receiverPhone;
	
	/** 接收人邮箱 */
	private String receiverEmail;
	
	/** 类型：1、客户；2、员工； */
	private Integer type;
	
	/** 状态：1、有效；-1、无效； */
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

	@Column(name = "loansignId")
	public Long getLoansignId() {
		return loansignId;
	}

	public void setLoansignId(Long loansignId) {
		this.loansignId = loansignId;
	}

	@Column(name = "receiverName")
	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	@Column(name = "receiverPhone")
	public String getReceiverPhone() {
		return receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	@Column(name = "receiverEmail")
	public String getReceiverEmail() {
		return receiverEmail;
	}

	public void setReceiverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "createTime")
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
} 
