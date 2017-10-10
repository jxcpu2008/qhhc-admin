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
 * Uploadfile
 */
@Entity
@Table(name = "uploadfile")
public class Uploadfile implements java.io.Serializable {

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
     * 上传文件名
     */
    private String name;
    /**
     * 保存路径
     */
    private String savePath;
    /**
     * 保存的文件名
     */
    private String saveName;
    /**
     * 上传时间
     */
    private String addTime;
    /**
     * 邮件反馈信息
     */
    private Set<Feedbackinfo> feedbackinfos = new HashSet<Feedbackinfo>(0);

    // Constructors

    /** default constructor */
    public Uploadfile() {
    }

    /** full constructor */
    /**
     * 
     * @param name 反馈人姓名
     * @param savePath 保存的路径
     * @param saveName 保存名
     * @param addTime 添加时间
     * @param feedbackinfos 邮件反馈信息
     */
    public Uploadfile(String name, String savePath, String saveName,
            String addTime, Set<Feedbackinfo> feedbackinfos) {
        this.name = name;
        this.savePath = savePath;
        this.saveName = saveName;
        this.addTime = addTime;
        this.feedbackinfos = feedbackinfos;
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
    @Column(name = "name", length = 500)
    public String getName() {
        return this.name;
    }

    /**
     * 
     * @param name 反馈人姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "savePath", length = 400)
    public String getSavePath() {
        return this.savePath;
    }

    /**
     * 
     * @param savePath 保存的路径
     */
    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "saveName", length = 500)
    public String getSaveName() {
        return this.saveName;
    }

    /**
     * 
     * @param saveName 保存的名称
     */
    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "addTime", length = 100)
    public String getAddTime() {
        return this.addTime;
    }

    /**
     * 
     * @param addTime 添加时间
     */
    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    /**
     * 
     * @return Set<Feedbackinfo>
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "uploadfile")
    public Set<Feedbackinfo> getFeedbackinfos() {
        return this.feedbackinfos;
    }

    /**
     * 
     * @param feedbackinfos 邮件反馈信息
     */
    public void setFeedbackinfos(Set<Feedbackinfo> feedbackinfos) {
        this.feedbackinfos = feedbackinfos;
    }

}