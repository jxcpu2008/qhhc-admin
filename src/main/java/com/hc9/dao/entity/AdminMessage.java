package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 后台管理员站短
 * @author Administrator
 *
 */
@Entity
@Table(name="adminmessage")
public class AdminMessage implements java.io.Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	private Long id;
	/**
	 * 发送内容
	 */
	private String context;
	/**
	 * 是否已读（0未读 1已读）
	 */
	private int isread;
	/**
	 * 发送时间
	 */
	private String receivetime;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 接收人
	 */
	private String adminuserId;
	
	
	public AdminMessage(){
//		this.receivetime=DateUtils.format("yyyy-MM-dd HH:mm:ss");
	}
	
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "context")
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	@Column(name = "isread")
	public int getIsread() {
		return isread;
	}
	public void setIsread(int isread) {
		this.isread = isread;
	}
	@Column(name = "receivetime")
	public String getReceivetime() {
		return receivetime;
	}
	public void setReceivetime(String receivetime) {
		this.receivetime = receivetime;
	}
	@Column(name = "title")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name = "adminuser_id")
	public String getAdminuserId() {
		return adminuserId;
	}
	public void setAdminuserId(String adminuserId) {
		this.adminuserId = adminuserId;
	}
	
	
}
