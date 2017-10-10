package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *Banktype 银行卡类型
 */
@Entity
@Table(name = "banktype")
public class Banktype implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键 */
    private Long id;
    /** 银行名称 */
    private String name;

    /** userbanks */

    // Constructors

    /** default constructor */
    public Banktype() {
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param name
     *            银行名称
     * @param userbanks
     *            用户银行卡信息
     */
    public Banktype(String name, Set<UserBank> userbanks) {
        this.name = name;
    }

    // Property accessors
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
     * Title: getName
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return name
     */
    @Column(name = "name", length = 256)
    public String getName() {
        return this.name;
    }

    /**
     * <p>
     * Title: setName
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }



}