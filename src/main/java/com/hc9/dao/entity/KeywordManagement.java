package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 关键字
 * 
 * @author frank
 * 
 */
@Entity
@Table(name = "keyword_management")
public class KeywordManagement implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private Long id;

    /**
     * 关键字
     */
    private String keywords;

    /**
     * 描述
     */
    private String description;

    /**
     * 构造方法
     */
    public KeywordManagement() {
        super();
    }

    /**
     * 构造方法
     * 
     * @param id
     *            编号
     * @param key
     *            关键字
     * @param description
     *            描述
     */
    public KeywordManagement(Long id, String keywords, String description) {
        super();
        this.id = id;
        this.keywords = keywords;
        this.description = description;
    }

    /**
     * 编号
     * 
     * @return 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    public Long getId() {
        return id;
    }

    /**
     * 编号
     * 
     * @param id
     *            编号
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 关键字
     * 
     * @return 关键字
     */
    @Column(name = "keywords", length = 80)
    public String getKeywords() {
        return keywords;
    }

    /**
     * 关键字
     * 
     * @param key
     *            关键字
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * 描述
     * 
     * @return 描述
     */
    @Column(name = "description", length = 200)
    public String getDescription() {
        return description;
    }

    /**
     * 描述
     * 
     * @param description
     *            描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
