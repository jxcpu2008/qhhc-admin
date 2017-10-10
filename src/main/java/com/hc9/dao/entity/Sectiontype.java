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
 * Sectiontype
 */
@Entity
@Table(name = "sectiontype")
public class Sectiontype implements java.io.Serializable {

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
     * 栏目类型名称
     */
    private String name;
    /**
     * 二级栏目
     */
    private Set<Deputysection> deputysections = new HashSet<Deputysection>(0);

    // Constructors

    /** default constructor */
    public Sectiontype() {
    }

    /** full constructor */
    /**
     * 
     * @param name 栏目类型名称
     * @param deputysections 二级栏目
     */
    public Sectiontype(String name, Set<Deputysection> deputysections) {
        this.name = name;
        this.deputysections = deputysections;
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
    @Column(name = "name", length = 50)
    public String getName() {
        return this.name;
    }

    /**
     * 
     * @param name 栏目类型名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *  
     * @return Set<Deputysection>
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sectiontype")
    public Set<Deputysection> getDeputysections() {
        return this.deputysections;
    }

    /**
     * 
     * @param deputysections 二级栏目
     */
    public void setDeputysections(Set<Deputysection> deputysections) {
        this.deputysections = deputysections;
    }

}