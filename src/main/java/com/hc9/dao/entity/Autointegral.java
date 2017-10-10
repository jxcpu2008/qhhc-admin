package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 自动积分表
 */
@Entity
@Table(name = "autointegral")
public class Autointegral implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键 */
	private Long id;
	/** 用户 */
	private Userbasicsinfo userbasicsinfo;
	/** 实际积分 */
	private Integer realityintegral;
	/** 获取积分方式描述 */
	private String content;
	/** 获取时间 */
	private String getintegraltime;
	/**类型*/
	private int type;
	/**
	 	1, 登录
    	2,评论、咨询
    	3,约谈
    	4,投资
    	5,推荐好友
    	6,实名认证
    	7,手机认证
    	8,邮箱认证
    	9,上传图像
    	10,其它认证
    	11,投资优先
    	12,投资夹层
    	13,投资劣后
    	14,修改用户头像
	 */

	/** default constructor */
	public Autointegral() {
	}

	/**
	 * 
	 * @param userbasicsinfo
	 * @param realityintegral
	 */
	public Autointegral(Userbasicsinfo userbasicsinfo,
			Integer realityintegral, String content, String getintegraltime, int type) {
		this.userbasicsinfo = userbasicsinfo;
		this.realityintegral = realityintegral;
		this.content = content;
		this.getintegraltime = getintegraltime;
		this.type=type;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	@Column(name = "realityintegral")
	public Integer getRealityintegral() {
		return realityintegral;
	}

	public void setRealityintegral(Integer realityintegral) {
		this.realityintegral = realityintegral;
	}

	@Column(name = "content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "getintegraltime")
	public String getGetintegraltime() {
		return getintegraltime;
	}

	public void setGetintegraltime(String getintegraltime) {
		this.getintegraltime = getintegraltime;
	}
	@Column(name = "type")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}