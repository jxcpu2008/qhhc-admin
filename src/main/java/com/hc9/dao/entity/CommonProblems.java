package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "common_problems")
public class CommonProblems implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键 */
    private Long id;
    /** 问题标题 */
    private String title;
    /** 回复内容 */
    private String replyContent;
    /** 是否显示 */
    private Integer isShow;
    /** 问题类型 */
    private Integer type;
    
    /** default constructor */
    public CommonProblems() {}

    /**
     * 
     * @param id 主键
     * @param title 问题标题
     * @param replyContent 回复内容
     * @param isShow 是否显示
     * @param type 问题类型
     */
    public CommonProblems(Long id, String title, String replyContent,
            Integer isShow, Integer type) {
        super();
        this.id = id;
        this.title = title;
        this.replyContent = replyContent;
        this.isShow = isShow;
        this.type = type;
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

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "replyContent")
    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    @Column(name = "isShow")
    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    @Column(name = "type")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    
    
    
}
