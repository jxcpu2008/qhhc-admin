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
 * Province
 */
@Entity
@Table(name = "province")
public class Province implements java.io.Serializable {

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
     * 名称
     */
    private String name;
    // private Set<Userbank> userbanks = new HashSet<Userbank>(0);
    /**
     *城市
     */
    private Set<City> cities = new HashSet<City>(0);

    // Constructors

    /** default constructor */
    public Province() {
    }

    /** full constructor */
    /**
     * 
     * @param name 名称
     * @param userbanks 用户银行卡账号信息
     * @param cities 城市
     */
    public Province(String name, Set<UserBank> userbanks, Set<City> cities) {
        this.name = name;
        // this.userbanks = userbanks;
        this.cities = cities;
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
     * @return  String
     */ 
    @Column(name = "name", length = 60)
    public String getName() {
        return this.name;
    }

    /**
     * 
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    // @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy =
    // "province")
    // public Set<Userbank> getUserbanks() {
    // return this.userbanks;
    // }
    //
    // public void setUserbanks(Set<Userbank> userbanks) {
    // this.userbanks = userbanks;
    // }

    /**
     * 
     * @return Set<City>
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "province")
    public Set<City> getCities() {
        return this.cities;
    }

    /**
     * 
     * @param cities 城市
     */
    public void setCities(Set<City> cities) {
        this.cities = cities;
    }

}