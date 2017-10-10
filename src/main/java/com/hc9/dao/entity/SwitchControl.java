package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="switchcontrol")
public class SwitchControl {
	
	// ID
	private Long id;
	
	/**
	 * 开关类型，英文表示：如email_sms_tip代表邮件短信提醒控制开关
	 */
	private String swithchType;
	
	/**
	 * 开关英文名称
	 */
	private String switchEnName;
	
	/**
	 * 开关中文名称
	 */
	private String switchZhName;
	
	/** 所属上级开关 */
	private String upSwitchEnName;
	
	/**
	 * 开关状态：1、开启；0、关闭；-1、软删除；
	 */
	private Integer switchStatus;
	
	/**
	 * 开关类型中文备注
	 */
	private String remark;
	
	// 创建时间
	private String createTime;
	
	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "swithchType", nullable = false)
	public String getSwithchType() {
		return swithchType;
	}
	public void setSwithchType(String swithchType) {
		this.swithchType = swithchType;
	}
	
	@Column(name = "switchEnName", nullable = false)
	public String getSwitchEnName() {
		return switchEnName;
	}
	public void setSwitchEnName(String switchEnName) {
		this.switchEnName = switchEnName;
	}
	
	@Column(name = "switchZhName", nullable = false)
	public String getSwitchZhName() {
		return switchZhName;
	}
	public void setSwitchZhName(String switchZhName) {
		this.switchZhName = switchZhName;
	}
	
	@Column(name = "upSwitchEnName")
	public String getUpSwitchEnName() {
		return upSwitchEnName;
	}
	
	public void setUpSwitchEnName(String upSwitchEnName) {
		this.upSwitchEnName = upSwitchEnName;
	}
	
	@Column(name = "switchStatus", nullable = false)
	public Integer getSwitchStatus() {
		return switchStatus;
	}
	public void setSwitchStatus(Integer switchStatus) {
		this.switchStatus = switchStatus;
	}
	
	@Column(name = "remark", nullable = false)
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Column(name = "createTime", nullable = false)
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
