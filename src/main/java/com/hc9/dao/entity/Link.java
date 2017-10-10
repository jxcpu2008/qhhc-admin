package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Link
 */
@Entity
@Table(name = "link")
public class Link implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
    /**
     * 编号
     */
	private Long id;
	/**
	 * 连接名称
	 */
	private String name;
	/**
	 * 链接地址
	 */
	private String url;
	/**
	 * 是否显示
	 */
	private Integer isShow;
	/**
	 *备注
	 */
	private String remark;
	/** 图片路径 */
    private String verifyImg;
    /**
     * 类型：0、友情链接；1、网站认证
     */
    private Integer type;
	// Constructors

	/** default constructor */
	public Link() {
	}

	/** full constructor */
	/**
	 * 
	 * @param name 链接名称
	 * @param url 链接地址
	 * @param isShow 是否显示
	 * @param remark 备注
	 */
	public Link(String name, String url, Integer isShow, String remark, String verifyImg, Integer type) {
		this.name = name;
		this.url = url;
		this.isShow = isShow;
		this.remark = remark;
		this.verifyImg = verifyImg;
		this.type = type;
	}

	// Property accessors
	/**
	 * Property accessors
	 * @return  Id
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}
	/**
	 * 
	 * @param id id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 
	 * @return name
	 */
	@Column(name = "name", length = 128)
	public String getName() {
		return this.name;
	}
	/**
	 * 
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return url
	 */
	@Column(name = "url", length = 2048)
	public String getUrl() {
		return this.url;
	}
	/**
	 * 
	 * @param url url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 
	 * @return isShow 
	 */
	@Column(name = "isShow")
	public Integer getIsShow() {
		return this.isShow;
	}
	/**
	 * 
	 * @param isShow  isShow
	 */
	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}  
	/**
	 * 
	 * @return remark
	 */
	@Column(name = "remark", length = 1024)
	public String getRemark() {
		return this.remark;
	}
	/**
	 * 
	 * @param remark remark
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Column(name = "verifyImg", length = 100)
    public String getVerifyImg() {
        return verifyImg;
    }

    public void setVerifyImg(String verifyImg) {
        this.verifyImg = verifyImg;
    }

    @Column(name = "type", length = 5)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}