package com.hc9.dao.entity;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * City 城市表
 */
@Entity
@Table(name = "city")
public class City implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * id
     */
    private Long id;

    /**
     * 省
     */
    private Province province;

    /**
     * id
     */
    private Long cityId;

    /**
     * 市名
     */
    private String name;

    // private Set<Userbank> userbanks = new HashSet<Userbank>(0);

    // Constructors

    /** default constructor */
    public City() {
    }

    /**
     * full constructor
     * @param province  province
     * @param cityId    cityId
     * @param name      name
     * @param userbanks userbanks
     */
    public City(Province province, Long cityId, String name,
            Set<UserBank> userbanks) {
        this.province = province;
        this.cityId = cityId;
        this.name = name;
        // this.userbanks = userbanks;
    }

    /**
     * Property accessors
     * @return  id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * id
     * @param id    id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * province
     * @return province
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    public Province getProvince() {
        return this.province;
    }

    /**
     * province
     * @param province  province
     */
    public void setProvince(Province province) {
        this.province = province;
    }

    /**
     * cityId
     * @return  cityId
     */
    @Column(name = "city_id")
    public Long getCityId() {
        return this.cityId;
    }

    /**
     * cityId
     * @param cityId    cityId
     */
    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    /**
     * name
     * @return  name
     */
    @Column(name = "name", length = 60)
    public String getName() {
        return this.name;
    }

    /**
     * name
     * @param name  name
     */
    public void setName(String name) {
        this.name = name;
    }

    // @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy =
    // "city")
    // public Set<Userbank> getUserbanks() {
    // return this.userbanks;
    // }
    //
    // public void setUserbanks(Set<Userbank> userbanks) {
    // this.userbanks = userbanks;
    // }

}