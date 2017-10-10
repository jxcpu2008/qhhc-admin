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
 * Borrowerscontact 借款人联系方式
 */
@Entity
@Table(name = "borrowerscontact")
public class Borrowerscontact implements java.io.Serializable {

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
     * 现居住地址
     */
    private String newaddress;

    /**
     * 住宅电话
     */
    private String addressPhone;

    /**
     * 联系人1
     */
    private String linkman1;

    /**
     * 关系1
     */
    private String relation1;

    /**
     * 手机号码1
     */
    private String phone1;

    /**
     * 第一联系人的其它信息
     */
    private String other1;

    /**
     * 第二联系人
     */
    private String linkman2;

    /**
     * 第二联系人关系
     */
    private String relation2;

    /**
     * 第二联系人电话
     */
    private String phone2;

    /**
     * 第二联系人其它信息
     */
    private String other2;

    /**
     * 提交时间
     */
    private String addTime;

    // Constructors

    /** default constructor */
    public Borrowerscontact() {
    }

    /**
     * minimal constructor
     * @param borrowersbase borrowersbase
     */
    public Borrowerscontact(Borrowersbase borrowersbase) {
        this.borrowersbase = borrowersbase;
    }

    /**
     * full constructor
     * @param borrowersbase borrowersbase
     * @param newaddress    newaddress
     * @param addressPhone  addressPhone
     * @param linkman1      linkman1
     * @param relation1     relation1
     * @param phone1        phone1
     * @param other1        other1
     * @param linkman2      linkman2
     * @param relation2     relation2
     * @param phone2        phone2
     * @param other2        other2
     * @param addTime       addTime
     */
    public Borrowerscontact(Borrowersbase borrowersbase, String newaddress,
            String addressPhone, String linkman1, String relation1,
            String phone1, String other1, String linkman2, String relation2,
            String phone2, String other2, String addTime) {
        this.borrowersbase = borrowersbase;
        this.newaddress = newaddress;
        this.addressPhone = addressPhone;
        this.linkman1 = linkman1;
        this.relation1 = relation1;
        this.phone1 = phone1;
        this.other1 = other1;
        this.linkman2 = linkman2;
        this.relation2 = relation2;
        this.phone2 = phone2;
        this.other2 = other2;
        this.addTime = addTime;
    }

    /**
     * id
     * @return  id
     */
    @Id
    @GenericGenerator(name = "contact_id", strategy = "foreign", parameters = { @Parameter(name = "property", value = "borrowersbase") })
    @GeneratedValue(generator = "contact_id")
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
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "borrowerscontact")
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
     * newaddress
     * @return  newaddress
     */
    @Column(name = "newaddress", length = 100)
    public String getNewaddress() {
        return this.newaddress;
    }

    /**
     * newaddress
     * @param newaddress    newaddress
     */
    public void setNewaddress(String newaddress) {
        this.newaddress = newaddress;
    }

    /**
     * addressPhone
     * @return  addressPhone
     */
    @Column(name = "addressPhone", length = 20)
    public String getAddressPhone() {
        return this.addressPhone;
    }

    /**
     * addressPhone
     * @param addressPhone  addressPhone
     */
    public void setAddressPhone(String addressPhone) {
        this.addressPhone = addressPhone;
    }

    /**
     * linkman1
     * @return  linkman1
     */
    @Column(name = "linkman1", length = 20)
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
    @Column(name = "phone1", length = 50)
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
     * other1
     * @return  other1
     */
    @Column(name = "other1", length = 400)
    public String getOther1() {
        return this.other1;
    }

    /**
     * other1
     * @param other1    other1
     */
    public void setOther1(String other1) {
        this.other1 = other1;
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
    @Column(name = "relation2", length = 20)
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
     * other2
     * @return  other2
     */
    @Column(name = "other2", length = 400)
    public String getOther2() {
        return this.other2;
    }

    /**
     * other2
     * @param other2    other2
     */
    public void setOther2(String other2) {
        this.other2 = other2;
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