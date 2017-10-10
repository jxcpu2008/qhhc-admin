package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Deputysection
 */
@Entity
@Table(name = "deputysection")
public class Deputysection implements java.io.Serializable {

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
     * 一级栏目
     */
    private Topic topic;
    /**
     * 栏目类型
     */
    private Sectiontype sectiontype;
    /**
     * 是否推荐
     */
    private Integer isRecommend;
    /**
     * 是否显示
     */
    private Integer isShow;
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
    /**
     * 1表示是不可以删除 2表示不能修改 0表示可以删除
     */
    private Integer isfixed;

    /**
     * 是否显示在页脚
     */
    private Integer isfooter;
    /**
     * 文章
     */
    private Set<Article> articles = new HashSet<Article>(0);

    // Constructors

    /** default constructor */
    public Deputysection() {
    }

    /** minimal constructor */
    /**
     * 
     * @param topic
     *            一级栏目
     * @param orderNum
     *            排序
     */
    public Deputysection(Topic topic, Integer orderNum) {
        this.topic = topic;
        this.orderNum = orderNum;
    }

    /**
     * 
     * @param id
     *            d
     * @param name
     *            名称
     */
    public Deputysection(Long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /** full constructor */
    /**
     * 
     * @param topic
     *            一级栏目
     * @param sectiontype
     *            栏目类型
     * @param isRecommend
     *            是否推荐
     * @param isShow
     *            是否显示
     * @param name
     *            名称
     * @param orderNum
     *            排序
     * @param pageHtml
     *            网页内容
     * @param pageTitile
     *            网页标题
     * @param url
     *            路径
     * @param isfixed
     *            是否可删除修改
     * @param isfooter
     *            是否显示在页脚
     * @param articles
     *            文章
     */
    public Deputysection(Topic topic, Sectiontype sectiontype,
            Integer isRecommend, Integer isShow, String name, Integer orderNum,
            String pageHtml, String pageTitile, String url, Integer isfixed,
            Integer isfooter, Set<Article> articles) {
        this.topic = topic;
        this.sectiontype = sectiontype;
        this.isRecommend = isRecommend;
        this.isShow = isShow;
        this.name = name;
        this.orderNum = orderNum;
        this.pageHtml = pageHtml;
        this.pageTitile = pageTitile;
        this.url = url;
        this.isfixed = isfixed;
        this.isfooter = isfooter;
        this.articles = articles;
    }

    /**
     * 
     * @param id
     *            id
     * @param topic
     *            一级栏目
     * @param sectiontype
     *            栏目类型
     * @param isShow
     *            是否显示
     * @param name
     *            名称
     * @param pageTitile
     *            网页标题
     */
    public Deputysection(Long id, Topic topic, Sectiontype sectiontype,
            Integer isShow, String name, String pageTitile) {
        super();
        this.id = id;
        this.topic = topic;
        this.sectiontype = sectiontype;
        this.isShow = isShow;
        this.name = name;
        this.pageTitile = pageTitile;
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
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * @return Topic
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    public Topic getTopic() {
        return this.topic;
    }

    /**
     * 
     * @param topic
     *            topic
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * 
     * @return Sectiontype
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sectiontype_id")
    public Sectiontype getSectiontype() {
        return this.sectiontype;
    }

    /**
     * 
     * @param sectiontype
     *            sectiontype
     */
    public void setSectiontype(Sectiontype sectiontype) {
        this.sectiontype = sectiontype;
    }

    /**
     * 
     * @return Integer
     */
    @Column(name = "isRecommend")
    public Integer getIsRecommend() {
        return this.isRecommend;
    }

    /**
     * 
     * @param isRecommend
     *            isRecommend
     */
    public void setIsRecommend(Integer isRecommend) {
        this.isRecommend = isRecommend;
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
     * @param isShow
     *            isShow
     */
    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    /**
     * 
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return Integer
     */
    @Column(name = "orderNum", nullable = false)
    public Integer getOrderNum() {
        return this.orderNum;
    }

    /**
     * 
     * @param orderNum
     *            orderNum
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "pageHTML")
    public String getPageHtml() {
        return this.pageHtml;
    }

    /**
     * 
     * @param pageHtml
     *            pageHtml
     */
    public void setPageHtml(String pageHtml) {
        this.pageHtml = pageHtml;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "pageTitile")
    public String getPageTitile() {
        return this.pageTitile;
    }

    /**
     * 
     * @param pageTitile
     *            pageTitile
     */
    public void setPageTitile(String pageTitile) {
        this.pageTitile = pageTitile;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "url")
    public String getUrl() {
        return this.url;
    }

    /**
     * 
     * @param url
     *            url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return Integer
     */
    @Column(name = "isfixed")
    public Integer getIsfixed() {
        return this.isfixed;
    }

    /**
     * 
     * @param isfixed
     *            isfixed
     */
    public void setIsfixed(Integer isfixed) {
        this.isfixed = isfixed;
    }

    /**
     * 
     * @return Integer
     */
    @Column(name = "isfooter")
    public Integer getIsfooter() {
        return this.isfooter;
    }

    /**
     * 
     * @param isfooter
     *            isfooter
     */
    public void setIsfooter(Integer isfooter) {
        this.isfooter = isfooter;
    }

    /**
     * 
     * @return Set<Article>
     */
    @OneToMany( fetch = FetchType.EAGER, mappedBy = "deputysection")
    public Set<Article> getArticles() {
        return this.articles;
    }

    /**
     * 
     * @param articles
     *            articles
     */
    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }

}