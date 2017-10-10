package com.hc9.common.redis.sys.vo;


/**
 * TopicVo
 */

public class TopicVo{


	/**
     * 主键id
     */
    private Long id;
    /**
     * 一级栏目名称
     */
    private String name;

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

    // Constructors

    /** default constructor */
    public TopicVo() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}



}