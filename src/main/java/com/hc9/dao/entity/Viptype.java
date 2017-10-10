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
 * Viptype
 */
@Entity
@Table(name = "viptype")
public class Viptype implements java.io.Serializable {

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
     * 钱
     */
    private Double money;
    /**
     * 月份
     */
    private Integer month;
    /**
     * vip信息
     */
    private Set<Vipinfo> vipinfos = new HashSet<Vipinfo>(0);

    // Constructors

    /** default constructor */
    public Viptype() {
    }

    /**
     * full constructor
     * @param money 钱
     * @param month 月份
     * @param vipinfos vip信息
     */
    public Viptype(Double money, Integer month, Set<Vipinfo> vipinfos) {
        this.money = money;
        this.month = month;
        this.vipinfos = vipinfos;
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
     * @return Double
     */
    @Column(name = "money", precision = 18, scale = 4)
    public Double getMoney() {
        return this.money;
    }

    /**
     * 
     * @param money 钱
     */
    public void setMoney(Double money) {
        this.money = money;
    }

    /**
     * 
     * @return Integer
     */
    @Column(name = "month")
    public Integer getMonth() {
        return this.month;
    }

    /**
     * 
     * @param month 月份
     */
    public void setMonth(Integer month) {
        this.month = month;
    }

    /**
     * 
     * @return Set<Vipinfo>
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "viptype")
    public Set<Vipinfo> getVipinfos() {
        return this.vipinfos;
    }

    /**
     * 
     * @param vipinfos vip信息
     */
    public void setVipinfos(Set<Vipinfo> vipinfos) {
        this.vipinfos = vipinfos;
    }

}