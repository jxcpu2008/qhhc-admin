package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 银行利率
 */
@Entity
@Table(name = "bank_rate", uniqueConstraints = @UniqueConstraint(columnNames = "during"))
@JsonIgnoreProperties(value = {"hibernateLazyInitializer","products"})
public class BankRate implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 主键
     */
    private Long id;
    /**
     * 期限
     */
    private String during;
    /**
     * 利率
     */
    private Double rate;
    /**
     * 修改时间
     */
    private String timeUpdate;


    // Constructors

    /** default constructor */
    public BankRate() {
    }



    /** full constructor */
    /**
    * <p>Title: </p>
    * <p>Description: </p>
    * @param during  期限
    * @param rate 利率
    * @param timeUpdate 修改时间
    * @param products 产品
    */
    public BankRate(String during, Double rate, String timeUpdate ) {
        this.during = during;
        this.rate = rate;
        this.timeUpdate = timeUpdate;

    }

    // Property accessors
    /**
    * <p>Title: getId</p>
    * <p>Description: </p>
    * @return Long
    */
    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
    * <p>Title: setId</p>
    * <p>Description: </p>
    * @param id 主键
    */
    public void setId(Long id) {
        this.id = id;
    }

    /**
    * <p>Title: getDuring</p>
    * <p>Description: </p>
    * @return String 
    */
    @Column(name = "during", nullable = false, length = 30)
    public String getDuring() {
        return this.during;
    }

    /**
    * <p>Title: setDuring</p>
    * <p>Description: </p>
    * @param during 期限
    */
    public void setDuring(String during) {
        this.during = during;
    }

    /**
    * <p>Title: getRate</p>
    * <p>Description: </p>
    * @return Double
    */
    @Column(name = "rate", nullable = false, precision = 20, scale = 4)
    public Double getRate() {
        return this.rate;
    }

    /**
    * <p>Title: setRate</p>
    * <p>Description: 设置利率</p>
    * @param rate 利率
    */
    public void setRate(Double rate) {
        this.rate = rate;
    }

    /**
    * <p>Title: getTimeUpdate</p>
    * <p>Description: </p>
    * @return String
    */
    @Column(name = "time_update", nullable = false, length = 20)
    public String getTimeUpdate() {
        return this.timeUpdate;
    }

    /**
    * <p>Title: setTimeUpdate</p>
    * <p>Description: </p>
    * @param timeUpdate  修改时间
    */
    public void setTimeUpdate(String timeUpdate) {
        this.timeUpdate = timeUpdate;
    }



}