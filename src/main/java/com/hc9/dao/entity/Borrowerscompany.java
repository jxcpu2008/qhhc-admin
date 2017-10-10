package com.hc9.dao.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Borrowerscompany借款人单位资料
 */
@Entity
@Table(name = "borrowerscompany")
public class Borrowerscompany implements java.io.Serializable {

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
     * 借款人基础信息
     */
    private Borrowersbase borrowersbase;
    /**
     * 单位名称
     */
    private String companyName;
    /**
     * 单位电话
     */
    private String companyPhone;
    /**
     * 地址
     */
    private String address;
    /**
     * 工作年限
     */
    private String workYear;
    /**
     * 证明人
     */
    private String referenceMan;
    /**
     * 证明人手机
     */
    private String referencePhone;

    /**
     * 提交时间
     */
    private String addTime;

    // Constructors

    /** default constructor */
    public Borrowerscompany() {
    }

    /**
     * constructor
     * @param borrowersbase borrowersbase
     */
    public Borrowerscompany(Borrowersbase borrowersbase) {
        this.borrowersbase = borrowersbase;
    }

    /**
     * full constructor
     * @param borrowersbase borrowersbase
     * @param companyName   companyName
     * @param companyPhone  companyPhone
     * @param address       address
     * @param workYear      workYear
     * @param referenceMan  referenceMan
     * @param referencePhone    referencePhone
     * @param addTime   addTime
     */
    public Borrowerscompany(Borrowersbase borrowersbase, String companyName,
            String companyPhone, String address, String workYear,
            String referenceMan, String referencePhone, String addTime) {
        this.borrowersbase = borrowersbase;
        this.companyName = companyName;
        this.companyPhone = companyPhone;
        this.address = address;
        this.workYear = workYear;
        this.referenceMan = referenceMan;
        this.referencePhone = referencePhone;
        this.addTime = addTime;
    }

    /**
     * id
     * @return  id
     */
//    @Id
//    @GeneratedValue(strategy = IDENTITY)
//    @Column(name = "id", unique = true, nullable = false)
    @Id
    @GenericGenerator(name = "company_id", strategy = "foreign", parameters = { @Parameter(name = "property", value = "borrowersbase") })
    @GeneratedValue(generator = "company_id")
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
     * borrowersbase
     * @return  borrowersbase
     */
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "borrowerscompany")
    @JoinColumn(name = "id", unique = true, nullable = false)
    public Borrowersbase getBorrowersbase() {
        return this.borrowersbase;
    }

    /**
     * borrowersbase
     * @param borrowersbase borrowersbase
     */
    public void setBorrowersbase(Borrowersbase borrowersbase) {
        this.borrowersbase = borrowersbase;
    }

    /**
     * companyName
     * @return  companyName
     */
    @Column(name = "companyName", length = 60)
    public String getCompanyName() {
        return this.companyName;
    }

    /**
     * companyName
     * @param companyName   companyName
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * companyPhone
     * @return  companyPhone
     */
    @Column(name = "companyPhone", length = 20)
    public String getCompanyPhone() {
        return this.companyPhone;
    }

    /**
     * companyPhone
     * @param companyPhone  companyPhone
     */
    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    /**
     * address  
     * @return  address
     */
    @Column(name = "address", length = 200)
    public String getAddress() {
        return this.address;
    }

    /**
     * address
     * @param address   address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * workYear
     * @return  workYear
     */
    @Column(name = "workYear", length = 50)
    public String getWorkYear() {
        return this.workYear;
    }

    /**
     * workYear
     * @param workYear  workYear
     */
    public void setWorkYear(String workYear) {
        this.workYear = workYear;
    }

    /**
     * referenceMan
     * @return  referenceMan
     */
    @Column(name = "referenceMan", length = 50)
    public String getReferenceMan() {
        return this.referenceMan;
    }

    /**
     * referenceMan
     * @param referenceMan  referenceMan
     */
    public void setReferenceMan(String referenceMan) {
        this.referenceMan = referenceMan;
    }

    /**
     * referencePhone
     * @return  referencePhone
     */
    @Column(name = "referencePhone", length = 20)
    public String getReferencePhone() {
        return this.referencePhone;
    }

    /**
     * referencePhone
     * @param referencePhone    referencePhone
     */
    public void setReferencePhone(String referencePhone) {
        this.referencePhone = referencePhone;
    }

    /**
     * addTime
     * @return  addTime
     */
    @Column(name = "addTime", length = 50)
    public String getAddTime() {
        return this.addTime;
    }

    /**
     * addTime
     * @param addTime   addTime
     */
    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

}