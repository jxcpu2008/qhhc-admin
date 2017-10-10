package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Videobroadcast
 */
@Entity
@Table(name = "videobroadcast")
public class Videobroadcast implements java.io.Serializable {

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
     * 标题
     */
    private String title;
    /**
     * 栏目路径
     */
    private String url;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 是否显示
     */
    private Integer isShow;
    /**
     * 显示顺序
     */
    private Integer showNum;
    /**
     * 添加时间
     */
    private String addTime;
    /**
     * 备注
     */
    private String remark;

    // Constructors

    /** default constructor */
    public Videobroadcast() {
    }

    /** full constructor */
    /**
     * 
     * @param title 标题
     * @param url 栏目路径
     * @param filePath 文件路径
     * @param isShow 是否显示
     * @param showNum 显示顺序
     * @param addTime 添加时间
     * @param remark 备注
     */
    public Videobroadcast(String title, String url, String filePath,
            Integer isShow, Integer showNum, String addTime, String remark) {
        this.title = title;
        this.url = url;
        this.filePath = filePath;
        this.isShow = isShow;
        this.showNum = showNum;
        this.addTime = addTime;
        this.remark = remark;
    }

    // Property accessors
    /**
     * 
     * @return Long
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
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
    @Column(name = "Title", length = 50)
    public String getTitle() {
        return this.title;
    }

    /**
     * 
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "url", length = 500)
    public String getUrl() {
        return this.url;
    }

    /**
     * 
     * @param url 栏目路径
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "filePath", length = 500)
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * 
     * @param filePath 文件路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
    @Column(name = "showNum")
    public Integer getShowNum() {
        return this.showNum;
    }

    /**
     * 
     * @param showNum 显示顺序
     */
    public void setShowNum(Integer showNum) {
        this.showNum = showNum;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "addTime", length = 50)
    public String getAddTime() {
        return this.addTime;
    }

    /**
     *
     * @param addTime  添加时间
     */
    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "Remark", length = 5002)
    public String getRemark() {
        return this.remark;
    }

    /**
     * 
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}