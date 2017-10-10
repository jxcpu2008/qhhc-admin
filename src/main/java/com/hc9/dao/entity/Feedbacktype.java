package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Feedbacktype
 */
@Entity
@Table(name = "feedbacktype")
public class Feedbacktype implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private Long id;

    /**
     * 类型名
     */
    private String typeName;

    /**
     * 是否显示
     */
    private Integer isShow;

    /** default constructor */
    public Feedbacktype() {
    }

    /**
     * 构造方法
     * 
     * @param typeName
     *            类型名
     * @param isShow
     *            是否显示
     */
    public Feedbacktype(String typeName, Integer isShow) {
        this.typeName = typeName;
        this.isShow = isShow;
    }

    /**
     * 编号
     * 
     * @return 编号
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
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
     * 类型名
     * 
     * @return 类型名
     */
    @Column(name = "typeName", length = 60)
    public String getTypeName() {
        return this.typeName;
    }

    /**
     * 类型名
     * 
     * @param typeName
     *            类型名
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 是否显示
     * 
     * @return 是否显示
     */
    @Column(name = "isShow")
    public Integer getIsShow() {
        return this.isShow;
    }

    /**
     * 是否显示
     * 
     * @param isShow
     *            是否显示
     */
    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }
}