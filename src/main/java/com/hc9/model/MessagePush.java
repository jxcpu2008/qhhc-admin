package com.hc9.model;

import java.util.Date;

public class MessagePush {
	
	// 主键
	private int id;
	
	// 消息推送内容
	private String content;
	
	// 消息推送类型，1(unicast-单播)，2(listcast-列播)，3(filecast-文件播)，4(broadcast-广播)，5(groupcast-组播)，6(customizedcast-通过开发者自有的alias进行推送)
	private int pushTo;
	
	// 消息推送状态，0(成功)，1(失败)
	private int status;
	
	// 消息推送记录创建时间
	private Date createTime;
	
	// 消息推送记录更新时间
	private Date updateTime;
	
	// 消息推送记录操作者
	private String operator;
	
	// 消息推送描述
	private String description;
	
	// 消息推送接收平台，取值范围是ios和android
	private String platform;
	
	// 消息推送记录是否删除，0(正常)，1(删除)
	private int isDelete;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPushTo() {
		return pushTo;
	}

	public void setPushTo(int pushTo) {
		this.pushTo = pushTo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}
}