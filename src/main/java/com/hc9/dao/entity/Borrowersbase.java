package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
* <p>Title:Borrowersbase</p>
* <p>Description: Borrowersbase 借款人基础信息表</p>
* <p>Company: 前海红筹</p>

*/
@Entity
@Table(name = "borrowersbase")
public class Borrowersbase implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 主键
     */
    private Long id;
    /**
     * 用户
     */
    private Userbasicsinfo userbasicsinfo;
    /**
     * 提交时间
     */
    private String addTime;
   
    
    /**
     * 年龄
     */
    private String age;
    /**
     * 审核结果 1通过 0不通过
     */
    private Integer auditResult;
    /**
     * //审核状态  1"未提交",2"正在审核",3"已审核"
     */
    private Integer auditStatus;
    /**
     * 审核时间
     */
    private String releaseTime;
    /**
     * 月收入
     */
    private String income;
    /**
     * 身份证号
     */
    private String isCard;
    /**
     * 婚姻状态
     */
    private String marryStatus;
    /**
     * 申请金额
     */
    private Double money;
    /**
     * 电话
     */
    private String phone;
    /**
     * 最高学历
     */
    private String qualifications;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 个人描述
     */
    private String remark;
    /**
     * 性别
     */
    private String sex;
    /**
     * 授信额度
     */
    private Integer credit;
    /**
     * 用户积分总和
     * 
     */
    private Integer suminte;
    
    /**
     * 联系方式
     */
    private Borrowerscontact borrowerscontact;
    
    /**
     * 单位资料
     */
    private Borrowerscompany borrowerscompany;
    
    /**
     * 财务状况
     */
    private Borrowersfinanes borrowersfinanes;
    
    /**
     * 联保信息
     */
    private Borrowersothercontact borrowersothercontact;

    // Constructors

    /** default constructor */
    public Borrowersbase() {
    }

    /** full constructor */
    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param userbasicsinfo
     *            userbasicsinfo
     * @param addTime
     *            添加时间
     * @param age
     *            年龄
     * @param auditResult
     *            审核结果 1通过 0不通过
     * @param auditStatus
     *            审核状态
     * @param releaseTime
     *            审核时间
     * @param income
     *            月收入
     * @param isCard
     *            身份证号
     * @param marryStatus
     *            婚姻状态
     * @param money
     *            申请的借款金额
     * @param phone
     *            phone 电话
     * @param qualifications
     *            最高学历
     * @param realName
     *            真实姓名
     * @param remark
     *            remark
     * @param sex
     *            性别
     * @param credit
     *            credit
     * @param suminte
     *            suminte
     * @param Borrowerscontact     
     *            Borrowerscontact      
     * @param borrowersfinaneses
     *            borrowersfinaneses
     * @param borrowerscompanies
     *            borrowerscompanies
     * @param borrowersothercontacts
     *            borrowersothercontacts
     */
    public Borrowersbase(Userbasicsinfo userbasicsinfo, String addTime,
            String age, Integer auditResult, Integer auditStatus,
            String releaseTime, String income, String isCard,
            String marryStatus, Double money, String phone,
            String qualifications, String realName, String remark, String sex,
            Integer credit, Integer suminte,Double applyMoney, Borrowerscontact borrowerscontact,
            Borrowersfinanes borrowersfinanes,
            Borrowerscompany borrowerscompany,
            Borrowersothercontact borrowersothercontact) {
        this.userbasicsinfo = userbasicsinfo;
        this.addTime = addTime;
        this.age = age;
        this.auditResult = auditResult;
        this.auditStatus = auditStatus;
        this.releaseTime = releaseTime;
        this.income = income;
        this.isCard = isCard;
        this.marryStatus = marryStatus;
        this.money = money;
        this.phone = phone;
        this.qualifications = qualifications;
        this.realName = realName;
        this.remark = remark;
        this.sex = sex;
        this.credit = credit;
        this.suminte = suminte;
        this.borrowerscontact = borrowerscontact;
        this.borrowersfinanes = borrowersfinanes;
        this.borrowerscompany = borrowerscompany;
        this.borrowersothercontact = borrowersothercontact;
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
     * @return Long
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
     * Title: getUserbasicsinfo
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return Userbasicsinfo
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userbasicinfo_id")
    public Userbasicsinfo getUserbasicsinfo() {
        return this.userbasicsinfo;
    }

    /**
     * <p>
     * Title: setUserbasicsinfo
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param userbasicsinfo
     *            userbasicsinfo
     */
    public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }


    /**
     * <p>
     * Title: getAddTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return String
     */
    @Column(name = "addTime", length = 50)
    public String getAddTime() {
        return this.addTime;
    }

    /**
     * <p>
     * Title: setAddTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param addTime
     *            addTime
     */
    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    /**
     * <p>
     * Title: getAge
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return String
     */
    @Column(name = "age", length = 10)
    public String getAge() {
        return this.age;
    }

    /**
     * <p>
     * Title: setAge
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param age
     *            age
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     * <p>
     * Title: getAuditResult
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return auditResult
     */
    @Column(name = "auditResult")
    public Integer getAuditResult() {
        return this.auditResult;
    }

    /**
     * <p>
     * Title: setAuditResult
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param auditResult
     *            auditResult
     */
    public void setAuditResult(Integer auditResult) {
        this.auditResult = auditResult;
    }

    /**
     * <p>
     * Title: getAuditStatus
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return Integer
     */
    @Column(name = "auditStatus")
    public Integer getAuditStatus() {
        return this.auditStatus;
    }

    /**
     * <p>
     * Title: setAuditStatus
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param auditStatus
     *            auditStatus
     */
    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    /**
     * <p>
     * Title: getreleaseTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return String
     */
    @Column(name = "committime", length = 50)
    public String getreleaseTime() {
        return this.releaseTime;
    }

    /**
     * <p>
     * Title: setreleaseTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param releaseTime
     *            releaseTime
     */
    public void setreleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    /**
     * <p>
     * Title: getIncome
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return String
     */
    @Column(name = "income")
    public String getIncome() {
        return this.income;
    }

    /**
     * <p>
     * Title: setIncome
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param income
     *            income
     */
    public void setIncome(String income) {
        this.income = income;
    }

    /**
     * <p>
     * Title: getIsCard
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return isCard
     */
    @Column(name = "isCard", length = 30)
    public String getIsCard() {
        return this.isCard;
    }

    /**
     * <p>
     * Title: setIsCard
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param isCard
     *            isCard
     */
    public void setIsCard(String isCard) {
        this.isCard = isCard;
    }

    /**
     * <p>
     * Title: getMarryStatus
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return String
     */
    @Column(name = "marryStatus", length = 10)
    public String getMarryStatus() {
        return this.marryStatus;
    }

    /**
     * <p>
     * Title: setMarryStatus
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param marryStatus
     *            marryStatus
     */
    public void setMarryStatus(String marryStatus) {
        this.marryStatus = marryStatus;
    }

    /**
     * <p>
     * Title: getMoney
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return Double
     */
    @Column(name = "money", precision = 18, scale = 4)
    public Double getMoney() {
        return this.money;
    }

    /**
     * <p>
     * Title: setMoney
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param money
     *            money
     */
    public void setMoney(Double money) {
        this.money = money;
    }

    /**
     * <p>
     * Title: getPhone
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return String
     */
    @Column(name = "phone", length = 20)
    public String getPhone() {
        return this.phone;
    }

    /**
     * <p>
     * Title: setPhone
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param phone
     *            phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * <p>
     * Title: getQualifications
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return String
     */
    @Column(name = "qualifications", length = 50)
    public String getQualifications() {
        return this.qualifications;
    }

    /**
     * <p>
     * Title: setQualifications
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param qualifications qualifications
     */
    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    /**
     * <p>
     * Title: getRealName
     * </p>
     * <p>
     * Description:  realName
     * </p>
     * 
     * @return String
     */
    @Column(name = "realName", length = 20)
    public String getRealName() {
        return this.realName;
    }

    /**
    * <p>Title: setRealName</p>
    * <p>Description: </p>
    * @param realName 真实姓名
    */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * <p>
     * Title: getRemark
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return String
     */
    @Column(name = "remark")
    public String getRemark() {
        return this.remark;
    }

    /**
     * <p>
     * Title: setRemark
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param remark remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * <p>
     * Title: getSex
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return Integer
     */
    @Column(name = "sex")
    public String getSex() {
        return this.sex;
    }

    /**
     * <p>
     * Title: setSex
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param sex
     *            sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * <p>
     * Title: getCredit
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return Integer
     */
    @Column(name = "credit")
    public Integer getCredit() {
        return this.credit;
    }

    /**
     * <p>
     * Title: setCredit
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param credit
     *            credit
     */
    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    /**
     * <p>
     * Title: getSuminte
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return suminte
     */
    @Column(name = "suminte")
    public Integer getSuminte() {
        return this.suminte;
    }

    /**
     * <p>
     * Title: setSuminte
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param suminte
     *            suminte
     */
    public void setSuminte(Integer suminte) {
        this.suminte = suminte;
    }

    /**
     * 
     * @return Borrowerscontact
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public Borrowerscontact getBorrowerscontact() {
        return borrowerscontact;
    }

    /**
     * 
     * @param borrowerscontact Borrowerscontact
     */
    public void setBorrowerscontact(Borrowerscontact borrowerscontact) {
        this.borrowerscontact = borrowerscontact;
    }

    /**
     * 
     * @return Borrowerscompany
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public Borrowerscompany getBorrowerscompany() {
        return borrowerscompany;
    }

    /**
     * 
     * @param borrowerscontact Borrowerscompany
     */
    public void setBorrowerscompany(Borrowerscompany borrowerscompany) {
        this.borrowerscompany = borrowerscompany;
    }

    /**
     * 
     * @return Borrowersfinanes
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public Borrowersfinanes getBorrowersfinanes() {
        return borrowersfinanes;
    }

    /**
     * 
     * @param borrowersfinanes borrowersfinanes
     */
    public void setBorrowersfinanes(Borrowersfinanes borrowersfinanes) {
        this.borrowersfinanes = borrowersfinanes;
    }

    /**
     * 
     * @return Borrowersothercontact
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public Borrowersothercontact getBorrowersothercontact() {
        return borrowersothercontact;
    }

    /**
     * 
     * @param borrowersothercontact Borrowersothercontact
     */
    public void setBorrowersothercontact(Borrowersothercontact borrowersothercontact) {
        this.borrowersothercontact = borrowersothercontact;
    }

}