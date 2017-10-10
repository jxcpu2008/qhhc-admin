package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="smsemailsendplan")
public class SmsemailSendPlan {
	/** 主键id，自增 */
	private Long id;
	
	/** 模板类型(用于模板的分类) */
	private String templateType;
	
	/** 所使用的模板（对应模板英文名称） */
	private String templateEnName;
	
	/** 消息标题（对应模板中文名称） */
	private String msgTitle;
	
	/** 模板内容（记录每次发送计划的内容，防止模板对应的内容修改后有据可查） */
	private String templateContent;
	
	/** 预计开始发送时间  */
	private String predictSendBeginTime;
	
	/** 预计发送完毕时间 */
	private String predictSendEndTime;
	
	/**  发送状态：0、草稿箱 1、等待发送；2、正在发送；3、终止发送；4、发送成功；5、发送失败；6、暂停；  */
	private Integer sendStatus;
	
	/** 发送类型：1、短信；2、邮件； */
	private Integer sendType;
	
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
	
	@Column(name = "templateType", nullable = false)
	public String getTemplateType() {
		return templateType;
	}
	
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	
	@Column(name = "msgTitle", nullable = false)
	public String getMsgTitle() {
		return msgTitle;
	}
	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}
	
	@Column(name = "templateEnName", nullable = false)
	public String getTemplateEnName() {
		return templateEnName;
	}
	public void setTemplateEnName(String templateEnName) {
		this.templateEnName = templateEnName;
	}
	
	public String getTemplateContent() {
		return templateContent;
	}
	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}
	@Column(name = "predictSendBeginTime", nullable = false)
	public String getPredictSendBeginTime() {
		return predictSendBeginTime;
	}
	public void setPredictSendBeginTime(String predictSendBeginTime) {
		this.predictSendBeginTime = predictSendBeginTime;
	}
	
	@Column(name = "predictSendEndTime")
	public String getPredictSendEndTime() {
		return predictSendEndTime;
	}
	
	public void setPredictSendEndTime(String predictSendEndTime) {
		this.predictSendEndTime = predictSendEndTime;
	}
	
	@Column(name = "sendStatus", nullable = false)
	public Integer getSendStatus() {
		return sendStatus;
	}
	public void setSendStatus(Integer sendStatus) {
		this.sendStatus = sendStatus;
	}
	
	@Column(name = "sendType", nullable = false)
	public Integer getSendType() {
		return sendType;
	}
	public void setSendType(Integer sendType) {
		this.sendType = sendType;
	}
	
	@Column(name = "createTime", nullable = false)
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
