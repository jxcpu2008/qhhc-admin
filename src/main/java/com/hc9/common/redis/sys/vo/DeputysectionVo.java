package com.hc9.common.redis.sys.vo;



public class DeputysectionVo  {


	/**
     * 主键id
     */
    private Long id;
    /**
     * 一级栏目
     */
    private Long topic;
    /**
     * 栏目类型
     */
    private int sectiontype;
    /**
     * 是否推荐
     */
    private Integer isRecommend;

    /**
     * 名称
     */
    private String name;
    /**
     * 显示顺序
     */
    private Integer orderNum;
    /**
     * 页面内容
     */
    private String pageHtml;
    /**
     * 网页标题
     */
    private String pageTitile;
    /**
     * 路径
     */
    private String url;



    // Constructors

    /** default constructor */
    public DeputysectionVo() {
    }



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Long getTopic() {
		return topic;
	}



	public void setTopic(Long topic) {
		this.topic = topic;
	}



	public int getSectiontype() {
		return sectiontype;
	}



	public void setSectiontype(int sectiontype) {
		this.sectiontype = sectiontype;
	}



	public Integer getIsRecommend() {
		return isRecommend;
	}



	public void setIsRecommend(Integer isRecommend) {
		this.isRecommend = isRecommend;
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



	public String getPageHtml() {
		return pageHtml;
	}



	public void setPageHtml(String pageHtml) {
		this.pageHtml = pageHtml;
	}



	public String getPageTitile() {
		return pageTitile;
	}



	public void setPageTitile(String pageTitile) {
		this.pageTitile = pageTitile;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



}