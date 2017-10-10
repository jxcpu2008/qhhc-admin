package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/** 短信邮件模板信息表对应实体类 */
@Entity
@Table(name="smsemailtemplate")
public class SmsEmailTemplate {

	/** 主键id，自增 */
	private Long id;
	
	/** 模板一级类型(用于模板的分类，英文表示) */
	private String templateType;
	
	/** 模板类型对应中文名称 */
	private String templateTypeName;
	
	/** 模板英文名称 */
	private String templateEnName;
	
	/** 模板中文名称 */
	private String templateZhName;
	
	/** 模板标题 */
	private String templateTitle;
	
	/** 模板内容 */
	private String templateContent;
	
	/** 开关状态：1、开启； -1、软删除； */
	private Integer templateStatus;
	
	/** 消息类型：1、短信；3、邮件； */
	private Integer msgType;
	
	/** 模板中文备注 */
	private String remark;
	
	/** 创建时间 */
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

	@Column(name = "templateType", nullable = false)
	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	@Column(name = "templateTypeName", nullable = false)
	public String getTemplateTypeName() {
		return templateTypeName;
	}

	public void setTemplateTypeName(String templateTypeName) {
		this.templateTypeName = templateTypeName;
	}

	@Column(name = "templateEnName", nullable = false)
	public String getTemplateEnName() {
		return templateEnName;
	}

	public void setTemplateEnName(String templateEnName) {
		this.templateEnName = templateEnName;
	}

	@Column(name = "templateZhName", nullable = false)
	public String getTemplateZhName() {
		return templateZhName;
	}

	public void setTemplateZhName(String templateZhName) {
		this.templateZhName = templateZhName;
	}

	@Column(name = "templateTitle", nullable = false)
	public String getTemplateTitle() {
		return templateTitle;
	}

	public void setTemplateTitle(String templateTitle) {
		this.templateTitle = templateTitle;
	}

	@Column(name = "templateContent", nullable = false)
	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}

	@Column(name = "templateStatus", nullable = false)
	public Integer getTemplateStatus() {
		return templateStatus;
	}

	public void setTemplateStatus(Integer templateStatus) {
		this.templateStatus = templateStatus;
	}

	@Column(name = "msgType", nullable = false)
	public Integer getMsgType() {
		return msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	@Column(name = "remark")
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