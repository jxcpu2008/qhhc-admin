package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 平台流水账
 */
@Entity
@Table(name = "adminuseraccountinfo")
public class Adminuseraccountinfo implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键 */
    private Long id;
    /** 流水账类型 */
    private Accounttype accounttype;
    /** 余额 */
    private Double money;
    /** 操作时间 */
    private String time;
    /** 支出 */
    private Double expenditure;
    /** 收入 */
    private Double income;
    /** 说明 */
    private String explan;
    /** 提现编号 */
    private String withdraw;

    /** default constructor */
    public Adminuseraccountinfo() {
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param accounttype
     *            流水账类型
     * @param money
     *            余额
     */
    public Adminuseraccountinfo(Accounttype accounttype, Double money) {
        this.accounttype = accounttype;
        this.money = money;
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param accounttype
     *            流水账类型
     * @param money
     *            余额
     * @param time
     *            操作时间
     * @param expenditure
     *            支出金额
     * @param income
     *            收入金额
     * @param explan
     *            备注
     * @param withdraw
     *            提现编号
     */
    public Adminuseraccountinfo(Accounttype accounttype, Double money,
            String time, Double expenditure, Double income, String explan,
            String withdraw) {
        this.accounttype = accounttype;
        this.money = money;
        this.time = time;
        this.expenditure = expenditure;
        this.income = income;
        this.explan = explan;
        this.withdraw = withdraw;
    }

    /**
     * <p>
     * Title: getId
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return id
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
     * Title: getAccounttype
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return accounttype
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounttype_id", nullable = false)
    public Accounttype getAccounttype() {
        return this.accounttype;
    }

    /**
     * <p>
     * Title: setAccounttype
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param accounttype
     *            accounttype
     */
    public void setAccounttype(Accounttype accounttype) {
        this.accounttype = accounttype;
    }

    /**
     * <p>
     * Title: getMoney
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return money
     */
    @Column(name = "money", nullable = false, precision = 18, scale = 4)
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
     * Title: getTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return time
     */
    @Column(name = "time", length = 30)
    public String getTime() {
        return this.time;
    }

    /**
     * <p>
     * Title: setTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param time
     *            time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * <p>
     * Title: getExpenditure
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return expenditure
     */
    @Column(name = "expenditure", precision = 18, scale = 4)
    public Double getExpenditure() {
        return this.expenditure;
    }

    /**
     * <p>
     * Title: setExpenditure
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param expenditure
     *            expenditure
     */
    public void setExpenditure(Double expenditure) {
        this.expenditure = expenditure;
    }

    /**
     * <p>
     * Title: getIncome
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return income
     */
    @Column(name = "income", precision = 18, scale = 4)
    public Double getIncome() {
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
    public void setIncome(Double income) {
        this.income = income;
    }

    /**
     * <p>
     * Title: getExplan
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return explan
     */
    @Column(name = "explan", length = 80)
    public String getExplan() {
        return this.explan;
    }

    /**
     * <p>
     * Title: setExplan
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param explan
     *            explan
     */
    public void setExplan(String explan) {
        this.explan = explan;
    }

    /**
     * <p>
     * Title: getWithdraw
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return withdraw
     */
    @Column(name = "withdraw", length = 30)
    public String getWithdraw() {
        return this.withdraw;
    }

    /**
     * <p>
     * Title: setWithdraw
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param withdraw
     *            withdraw
     */
    public void setWithdraw(String withdraw) {
        this.withdraw = withdraw;
    }

}