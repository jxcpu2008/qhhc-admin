package com.hc9.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Topic
 */
@Entity
@Table(name = "topic")
public class Topic implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 主键id
     */
    private Long id;
    /**
     * 一级栏目名称
     */
    private String name;
    /**
     * 是否显示
     */
    private Integer isShow;
    /**
     * 是否显示在页脚
     */
    private Integer isfooter;
    /**
     * 显示顺序
     */
    private Integer orderNum;
    /**
     * 路径
     */
    private String url;
    /**
     * 网页标题
     */
    private String pageTitle;
    /**
     * 英文名
     */
    private String engName;
    /**
     * 二级栏目
     */
    private Set<Deputysection> deputysections = new HashSet<Deputysection>(0);// 二级栏目

    // Constructors

    /** default constructor */
    public Topic() {
    }

    /**
     * 
     * @param id 主键id
     * @param name 栏目名称
     * @param url 栏目路径
     */
    public Topic(Long id, String name, String url) {
        super();
        this.id = id;
        this.name = name;
        this.url = url;
    }
    
    /**
     * 
     * @param id 栏目id
     * @param name 栏目名称
     * @param isShow 是否显示
     * @param isfooter 是否显示在页脚
     * @param pageTitle 网页标题
     */
    public Topic(Long id, String name, Integer isShow, Integer isfooter, String pageTitle) {
        super();
        this.id = id;
        this.name = name;
        this.isShow = isShow;
        this.isfooter = isfooter;
        this.pageTitle = pageTitle;
    }

    /** full constructor */
    /**
     * 
     * @param name 栏目名称
     * @param isShow 是否显示
     * @param isfooter 是否显示在页脚
     * @param orderNum 排序
     * @param url 路径
     * @param pageTitle 网页标题
     * @param deputysections 二级栏目
     */
    public Topic(String name, Integer isShow, Integer isfooter, Integer orderNum, String url,
            String pageTitle, Set<Deputysection> deputysections) {
        this.name = name;
        this.isShow = isShow;
        this.isfooter = isfooter;
        this.orderNum = orderNum;
        this.url = url;
        this.pageTitle = pageTitle;
        this.deputysections = deputysections;
    }

    // Property accessors
    /**
     * 
     * @return Long
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * 
     * @param id 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "name", length = 50)
    public String getName() {
        return this.name;
    }

    /**
     * 
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return Integer
     */
    @Column(name = "isShow")
    public Integer getIsShow() {
        return this.isShow;
    }

    /**
     * 
     * @param isShow 是否显示
     */
    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    /**
     * 
     * @return Integer
     */
    @Column(name = "isfooter")
    public Integer getIsfooter() {
        return isfooter;
    }

    /**
     * 
     * @param isfooter 是否显示在页脚
     */
    public void setIsfooter(Integer isfooter) {
        this.isfooter = isfooter;
    }

    /**
     * 
     * @return Integer
     */
    @Column(name = "orderNum")
    public Integer getOrderNum() {
        return this.orderNum;
    }

    /**
     *  
     * @param orderNum 排序
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    
    /**
     * 
     * @return String
     */
    @Column(name = "url", length = 200)
    public String getUrl() {
        return this.url;
    }

    /**
     * 
     * @param url 路径
     */
    public void setUrl(String url) {
        this.url = url;
    }

    
    /**
     * 
     * @return String
     */
    @Column(name = "pageTitle", length = 30)
    public String getPageTitle() {
        return this.pageTitle;
    }

    /**
     * 
     * @param pageTitle 网页标题
     */
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    /**
     * 
     * @return Set<Deputysection>
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "topic")
    public Set<Deputysection> getDeputysections() {
        return this.deputysections;
    }

    /**
     * 
     * @param deputysections 二级栏目
     */
    public void setDeputysections(Set<Deputysection> deputysections) {
        this.deputysections = deputysections;
    }
    @Column(name = "engName", length = 30)
	public String getEngName() {
		return engName;
	}

	public void setEngName(String engName) {
		this.engName = engName;
	}

}