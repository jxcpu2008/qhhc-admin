package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * app消息推送记录表
 * @author Administrator
 *
 */
@Entity
@Table(name = "app_message_push")
public class AppMessagePush implements java.io.Serializable {
	
	private static final long serialVersionUID = -2360675365941701300L;

	// 主键
	private int id;
	
	// 消息推送标题
	private String title;
	
	// 消息推送内容
	private String content;
	
	// 消息推送目标，1-全部注册用户，2-已认购用户，3-已宝付授权未认购，4-女性用户，5-男性用户，6-当日生日客户
	private int pushTo;
	
	// 消息推送类型，1(unicast-单播)，2(listcast-列播)，3(filecast-文件播)，4(broadcast-广播)，5(groupcast-组播)，6(customizedcast-通过开发者自有的alias进行推送)
	private int pushType;
	
	// 消息推送方式，1-立即推送
	private int pushNow;
	
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
	
	// 消息推送记录是否删除，0(正常)，1(删除)
	private int isDelete;

	public AppMessagePush() {
	}
	
    /**
	 * @param id
	 * @param content
	 * @param pushTo
	 * @param pushType
	 * @param pushNow
	 * @param status
	 * @param createTime
	 * @param updateTime
	 * @param operator
	 * @param description
	 * @param isDelete
	 */
	public AppMessagePush(int id, String title, String content, int pushTo, int pushType,
			int pushNow, int status, Date createTime, Date updateTime,
			String operator, String description, int isDelete) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.pushTo = pushTo;
		this.pushType = pushType;
		this.pushNow = pushNow;
		this.status = status;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.operator = operator;
		this.description = description;
		this.isDelete = isDelete;
	}

	@Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
		this.id = id;
	}

    @Column(name = "title", length = 100)
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "content", length = 100)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "pushTo", nullable = false, length = 1)
	public int getPushTo() {
		return pushTo;
	}

	public void setPushTo(int pushTo) {
		this.pushTo = pushTo;
	}

	@Column(name = "status", nullable = false, length = 1)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "createTime", insertable = false)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "updateTime")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "operator")
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "isDelete")
	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	@Column(name = "pushType", nullable = false, length = 1)
	public int getPushType() {
		return pushType;
	}

	public void setPushType(int pushType) {
		this.pushType = pushType;
	}

	@Column(name = "pushNow", nullable = false, length = 1)
	public int getPushNow() {
		return pushNow;
	}

	public void setPushNow(int pushNow) {
		this.pushNow = pushNow;
	}
}