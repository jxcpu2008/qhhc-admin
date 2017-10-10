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

/**
 * 文章
 */
@Entity
@Table(name = "article")
public class Article implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键 */
    private Long id;
    /** 所属二级栏目 */
    private Deputysection deputysection;
    /** 标题 */
    private String title;
    
    /** 是否推送，1-推送 0-不推送 */
    private Integer isPush;
    
    /** 是否显示 1 显示 0不显示 */
    private Integer isShow;
    /** 是否推荐1推荐 0不推荐 */
    private Integer isRecommend;
    /** 内容 */
    private String context;
    /** 创建时间 */
    private String createTime;
    /** 显示顺序 */
    private Integer orderNum;
    /** 文章路径 */
    private String filePath;
    /** 文件路径 */
    private String url;

    // Constructors

    /** default constructor */
    public Article() {
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param deputysection
     *            所属二级栏目
     * @param title
     *            标题
     * @param isShow
     *            是否显示 1 显示 0不显示
     * @param isRecommend
     *            是否推荐1推荐 0不推荐
     * @param context
     *            内容
     * @param createTime
     *            创建时间
     * @param orderNum
     *            显示顺序
     * @param filePath
     *            文章路径
     * @param url
     *            文件路径
     */
    public Article(Long id, Deputysection deputysection, String title,
			Integer isPush, Integer isShow, Integer isRecommend,
			String context, String createTime, Integer orderNum,
			String filePath, String url) {
		this.id = id;
		this.deputysection = deputysection;
		this.title = title;
		this.isPush = isPush;
		this.isShow = isShow;
		this.isRecommend = isRecommend;
		this.context = context;
		this.createTime = createTime;
		this.orderNum = orderNum;
		this.filePath = filePath;
		this.url = url;
	}

    /**
     * <p>
     * Title: getId
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * <p>
     * Title: setId
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * <p>
     * Title: getDeputysection
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return deputysection
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deputysection_id")
    public Deputysection getDeputysection() {
        return this.deputysection;
    }

    /**
     * <p>
     * Title: setDeputysection
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param deputysection
     *            deputysection
     */
    public void setDeputysection(Deputysection deputysection) {
        this.deputysection = deputysection;
    }

    /**
     * <p>
     * Title: getTitle
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return title
     */
    @Column(name = "title", length = 100)
    public String getTitle() {
        return this.title;
    }

    /**
     * <p>
     * Title: setTitle
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param title
     *            title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Column(name = "isPush")
    public Integer getIsPush() {
		return isPush;
	}

	public void setIsPush(Integer isPush) {
		this.isPush = isPush;
	}

	/**
     * <p>
     * Title: getIsShow
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return isShow
     */
    @Column(name = "isShow")
    public Integer getIsShow() {
        return this.isShow;
    }

    /**
     * <p>
     * Title: setIsShow
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param isShow
     *            isShow
     */
    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    /**
     * <p>
     * Title: getIsRecommend
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return isRecommend
     */
    @Column(name = "isRecommend")
    public Integer getIsRecommend() {
        return this.isRecommend;
    }

    /**
     * <p>
     * Title: setIsRecommend
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param isRecommend
     *            isRecommend
     */
    public void setIsRecommend(Integer isRecommend) {
        this.isRecommend = isRecommend;
    }

    /**
     * <p>
     * Title: getContext
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return context
     */
    @Column(name = "context")
    public String getContext() {
        return this.context;
    }

    /**
     * <p>
     * Title: setContext
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param context
     *            context
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * <p>
     * Title: getCreateTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return createTime
     */
    @Column(name = "createTime", length = 80)
    public String getCreateTime() {
        return this.createTime;
    }

    /**
     * <p>
     * Title: setCreateTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param createTime
     *            createTime
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * <p>
     * Title: getOrderNum
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return orderNum
     */
    @Column(name = "orderNum")
    public Integer getOrderNum() {
        return this.orderNum;
    }

    /**
     * <p>
     * Title: setOrderNum
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param orderNum
     *            orderNum
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * <p>
     * Title: getFilePath
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return filePath
     */
    @Column(name = "filePath")
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * <p>
     * Title: setFilePath
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param filePath
     *            filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * <p>
     * Title: getUrl
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return url
     */
    @Column(name = "url")
    public String getUrl() {
        return this.url;
    }

    /**
     * <p>
     * Title: setUrl
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param url
     *            url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}