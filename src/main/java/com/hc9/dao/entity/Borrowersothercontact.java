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
 * Borrowersothercontact 借款人联保信息
 */
@Entity
@Table(name = "borrowersothercontact")
public class Borrowersothercontact implements java.io.Serializable {

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
     * 借款人基本信息
     */
    private Borrowersbase borrowersbase;

    /**
     * 第一联保人
     */
    private String linkman1;
    /**
     * 和第一联保人关系
     */
    private String relation1;

    /**
     * 第一联保人手机
     */
    private String phone1;

    /**
     * 第二联保人
     */
    private String linkman2;

    /**
     * 和第二联保人关系
     */
    private String relation2;

    /**
     * 第二联保人手机
     */
    private String phone2;

    /**
     * 第三联保人
     */
    private String linkman3;

    /**
     * 和第三联保人关系
     */
    private String relation3;

    /**
     * 第三联保人手机
     */
    private String phone3;

    /**
     * 添加时间
     */
    private String addTime;

    /**
     * 第四联保人
     */
    private String linkman4;

    /**
     * 第四联保人手机
     */
    private String phone4;

    /**
     * 和第四联保人关系
     */
    private String relation4;

    // Constructors

    /** default constructor */
    public Borrowersothercontact() {
    }

    /**
     * minimal constructor
     * @param borrowersbase borrowersbase
     */ 
    public Borrowersothercontact(Borrowersbase borrowersbase) {
        this.borrowersbase = borrowersbase;
    }

    /**
     * full constructor
     * @param borrowersbase borrowersbase
     * @param linkman1  linkman1
     * @param relation1 relation1
     * @param phone1    phone1
     * @param linkman2  linkman2
     * @param relation2 relation2
     * @param phone2    phone2
     * @param linkman3  linkman3
     * @param relation3 relation3
     * @param phone3    phone3
     * @param addTime   addTime
     * @param linkman4  linkman4
     * @param phone4    phone4
     * @param relation4 relation4
     */
    public Borrowersothercontact(Borrowersbase borrowersbase, String linkman1,
            String relation1, String phone1, String linkman2, String relation2,
            String phone2, String linkman3, String relation3, String phone3,
            String addTime, String linkman4, String phone4, String relation4) {
        this.borrowersbase = borrowersbase;
        this.linkman1 = linkman1;
        this.relation1 = relation1;
        this.phone1 = phone1;
        this.linkman2 = linkman2;
        this.relation2 = relation2;
        this.phone2 = phone2;
        this.linkman3 = linkman3;
        this.relation3 = relation3;
        this.phone3 = phone3;
        this.addTime = addTime;
        this.linkman4 = linkman4;
        this.phone4 = phone4;
        this.relation4 = relation4;
    }

    /**
     * Property accessors
     * @return id
     */
//    @Id
//    @GeneratedValue(strategy = IDENTITY)
//    @Column(name = "id", unique = true, nullable = false)
    @Id
    @GenericGenerator(name = "othercontact_id", strategy = "foreign", parameters = { @Parameter(name = "property", value = "borrowersbase") })
    @GeneratedValue(generator = "othercontact_id")
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * id
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * borrowersbase
     * @return  borrowersbase
     */
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "borrowersothercontact")
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
     * linkman1
     * @return  linkman1
     */
    @Column(name = "linkman1", length = 50)
    public String getLinkman1() {
        return this.linkman1;
    }

    /**
     * linkman1
     * @param linkman1  linkman1
     */
    public void setLinkman1(String linkman1) {
        this.linkman1 = linkman1;
    }

    /**
     * relation1
     * @return  relation1
     */
    @Column(name = "relation1", length = 50)
    public String getRelation1() {
        return this.relation1;
    }

    /**
     * relation1
     * @param relation1 relation1
     */
    public void setRelation1(String relation1) {
        this.relation1 = relation1;
    }

    /**
     * phone1
     * @return  phone1
     */
    @Column(name = "phone1", length = 20)
    public String getPhone1() {
        return this.phone1;
    }

    /**
     * phone1
     * @param phone1    phone1
     */
    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    /**
     * linkman2
     * @return  linkman2
     */
    @Column(name = "linkman2", length = 50)
    public String getLinkman2() {
        return this.linkman2;
    }

    /**
     * linkman2
     * @param linkman2  linkman2
     */
    public void setLinkman2(String linkman2) {
        this.linkman2 = linkman2;
    }

    /**
     * relation2
     * @return  relation2
     */
    @Column(name = "relation2", length = 50)
    public String getRelation2() {
        return this.relation2;
    }

    /**
     * relation2
     * @param relation2 relation2
     */
    public void setRelation2(String relation2) {
        this.relation2 = relation2;
    }

    /**
     * phone2
     * @return  phone2
     */
    @Column(name = "phone2", length = 20)
    public String getPhone2() {
        return this.phone2;
    }

    /**
     * phone2
     * @param phone2    phone2
     */
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    /**
     * linkman3
     * @return  linkman3
     */
    @Column(name = "linkman3", length = 50)
    public String getLinkman3() {
        return this.linkman3;
    }

    /**
     * linkman3
     * @param linkman3  linkman3
     */
    public void setLinkman3(String linkman3) {
        this.linkman3 = linkman3;
    }

    /**
     * relation3
     * @return  relation3
     */
    @Column(name = "relation3", length = 50)
    public String getRelation3() {
        return this.relation3;
    }

    /**
     * relation3
     * @param relation3 relation3
     */
    public void setRelation3(String relation3) {
        this.relation3 = relation3;
    }

    /**
     * phone3
     * @return  phone3
     */
    @Column(name = "phone3", length = 20)
    public String getPhone3() {
        return this.phone3;
    }

    /**
     * phone3
     * @param phone3    phone3
     */
    public void setPhone3(String phone3) {
        this.phone3 = phone3;
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

    /**
     * linkman4
     * @return  linkman4
     */
    @Column(name = "linkman4", length = 50)
    public String getLinkman4() {
        return this.linkman4;
    }

    
    /**
     * linkman4
     * @param linkman4  linkman4
     */
    public void setLinkman4(String linkman4) {
        this.linkman4 = linkman4;
    }

    /**
     * phone4
     * @return  phone4
     */
    @Column(name = "phone4", length = 20)
    public String getPhone4() {
        return this.phone4;
    }

    /**
     * phone4
     * @param phone4    phone4
     */
    public void setPhone4(String phone4) {
        this.phone4 = phone4;
    }

    /**
     * relation4
     * @return  relation4
     */
    @Column(name = "relation4", length = 50)
    public String getRelation4() {
        return this.relation4;
    }

    /**
     * relation4
     * @param relation4 relation4
     */
    public void setRelation4(String relation4) {
        this.relation4 = relation4;
    }

}