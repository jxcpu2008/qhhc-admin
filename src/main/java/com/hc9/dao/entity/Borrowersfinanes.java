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
 * Borrowersfinanes 借款人财务情况
 */
@Entity
@Table(name = "borrowersfinanes")
public class Borrowersfinanes implements java.io.Serializable {

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
     * 月均收入
     */
    private Double income;
    
    /**
     * 月收入构成描述
     */
    private String incomeRemark;
    
    /**
     * 月均支出
     */
    private Double pay;
    
    /**
     * 月支出构成描述
     */
    private String payRemark;
    
    /**
     * 住房条件
     */
    private String housecondition;
    
    /**
     * 房产价值
     */
    private Double propertyValue;
    
    /**
     * 是否购车
     */
    private String isbuycar;
    
    /**
     * 车辆价值
     */
    private Double carValue;
    
    /**
     * 参股企业出名称
     */
    private String companyName;
    
    /**
     * 参股企业出金额
     */
    private Double companyPayMoney;
    
    /**
     * 其他描述
     */
    private String otherPropertyDescribe;
    
    /**
     * 添加时间
     */
    private String addTime;

    // Constructors

    /** default constructor */
    public Borrowersfinanes() {
    }

    /**
     * minimal constructor
     * @param borrowersbase borrowersbase
     */
    public Borrowersfinanes(Borrowersbase borrowersbase) {
        this.borrowersbase = borrowersbase;
    }

    /**
     * full constructor
     * @param borrowersbase borrowersbase
     * @param income    income
     * @param incomeRemark  incomeRemark
     * @param pay   pay
     * @param payRemark payRemark
     * @param housecondition    housecondition
     * @param propertyValue propertyValue
     * @param isbuycar  isbuycar
     * @param carValue  carValue
     * @param companyName   companyName
     * @param companyPayMoney   companyPayMoney
     * @param otherPropertyDescribe otherPropertyDescribe
     * @param addTime   addTime
     */
    public Borrowersfinanes(Borrowersbase borrowersbase, Double income,
            String incomeRemark, Double pay, String payRemark,
            String housecondition, Double propertyValue, String isbuycar,
            Double carValue, String companyName, Double companyPayMoney,
            String otherPropertyDescribe, String addTime) {
        this.borrowersbase = borrowersbase;
        this.income = income;
        this.incomeRemark = incomeRemark;
        this.pay = pay;
        this.payRemark = payRemark;
        this.housecondition = housecondition;
        this.propertyValue = propertyValue;
        this.isbuycar = isbuycar;
        this.carValue = carValue;
        this.companyName = companyName;
        this.companyPayMoney = companyPayMoney;
        this.otherPropertyDescribe = otherPropertyDescribe;
        this.addTime = addTime;
    }

    /**
     * id
     * @return id
     */
//    @Id
//    @GeneratedValue(strategy = IDENTITY)
//    @Column(name = "id", unique = true, nullable = false)
    @Id
    @GenericGenerator(name = "finanes_id", strategy = "foreign", parameters = { @Parameter(name = "property", value = "borrowersbase") })
    @GeneratedValue(generator = "finanes_id")
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
     * id
     * @return  id
     */
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "borrowersfinanes")
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
     * income
     * @return  income
     */
    @Column(name = "income", precision = 18, scale = 4)
    public Double getIncome() {
        return this.income;
    }

    /**
     * income
     * @param income    income
     */
    public void setIncome(Double income) {
        this.income = income;
    }

    /**
     * incomeRemark
     * @return  incomeRemark
     */
    @Column(name = "incomeRemark", length = 400)
    public String getIncomeRemark() {
        return this.incomeRemark;
    }

    /**
     * incomeRemark
     * @param incomeRemark  incomeRemark
     */
    public void setIncomeRemark(String incomeRemark) {
        this.incomeRemark = incomeRemark;
    }

    /**
     * pay
     * @return  pay
     */
    @Column(name = "pay", precision = 18, scale = 4)
    public Double getPay() {
        return this.pay;
    }

    /**
     * pay
     * @param pay   pay
     */
    public void setPay(Double pay) {
        this.pay = pay;
    }

    /**
     * payRemark
     * @return  payRemark
     */
    @Column(name = "payRemark", length = 500)
    public String getPayRemark() {
        return this.payRemark;
    }

    /**
     * payRemark
     * @param payRemark payRemark
     */
    public void setPayRemark(String payRemark) {
        this.payRemark = payRemark;
    }

    /**
     * housecondition
     * @return  housecondition
     */
    @Column(name = "housecondition", length = 200)
    public String getHousecondition() {
        return this.housecondition;
    }

    /**
     * housecondition
     * @param housecondition    housecondition
     */
    public void setHousecondition(String housecondition) {
        this.housecondition = housecondition;
    }

    /**
     * propertyValue
     * @return  propertyValue
     */
    @Column(name = "propertyValue", precision = 18, scale = 4)
    public Double getPropertyValue() {
        return this.propertyValue;
    }

    /**
     * propertyValue
     * @param propertyValue propertyValue
     */
    public void setPropertyValue(Double propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * isbuycar
     * @return  isbuycar
     */
    @Column(name = "isbuycar")
    public String getIsbuycar() {
        return this.isbuycar;
    }

    /**
     * isbuycar
     * @param isbuycar  isbuycar
     */
    public void setIsbuycar(String isbuycar) {
        this.isbuycar = isbuycar;
    }

    /**
     * carValue
     * @return  carValue
     */
    @Column(name = "carValue", precision = 18, scale = 4)
    public Double getCarValue() {
        return this.carValue;
    }

    /**
     * carValue
     * @param carValue  carValue
     */
    public void setCarValue(Double carValue) {
        this.carValue = carValue;
    }

    /**
     * companyName
     * @return  companyName
     */
    @Column(name = "companyName", length = 200)
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
     * companyPayMoney
     * @return  companyPayMoney
     */
    @Column(name = "companyPayMoney", precision = 18, scale = 4)
    public Double getCompanyPayMoney() {
        return this.companyPayMoney;
    }

    /**
     * companyPayMoney
     * @param companyPayMoney   companyPayMoney
     */
    public void setCompanyPayMoney(Double companyPayMoney) {
        this.companyPayMoney = companyPayMoney;
    }

    /**
     * otherPropertyDescribe
     * @return  otherPropertyDescribe
     */
    @Column(name = "otherPropertyDescribe", length = 800)
    public String getOtherPropertyDescribe() {
        return this.otherPropertyDescribe;
    }

    /**
     * otherPropertyDescribe
     * @param otherPropertyDescribe otherPropertyDescribe
     */
    public void setOtherPropertyDescribe(String otherPropertyDescribe) {
        this.otherPropertyDescribe = otherPropertyDescribe;
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