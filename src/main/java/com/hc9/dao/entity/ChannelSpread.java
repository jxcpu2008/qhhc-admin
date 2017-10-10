package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "channelspread")
public class ChannelSpread {

	private Integer id;
	private String spreadId;// 8为随机大小写字母

	private String name;

	private Integer type;

	private String upSpreadId;

	private String createTime;

	private String updateTime;

	private String url;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "spreadId")
	public String getSpreadId() {
		return spreadId;
	}

	public void setSpreadId(String spreadId) {
		this.spreadId = spreadId;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "upSpreadId")
	public String getUpSpreadId() {
		return upSpreadId;
	}

	public void setUpSpreadId(String upSpreadId) {
		this.upSpreadId = upSpreadId;
	}

	@Column(name = "createTime")
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "updateTime")
	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
