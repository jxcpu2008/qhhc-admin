package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Gathering
 */
@Entity
@Table(name = "gathering")
public class Gathering implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private Long id;

    /**
     * 借款标号
     */
    private String loansignname;

    /**
     * 收款金额
     */
    private Double money;

    /**
     * 收款方式
     */
    private String pmethod;

    /**
     * 说明
     */
    private String state;

    /**
     * 收款日期
     */
    private String time;

    /**
     * 反冲数据关联的原始数据id
     */
    private Long recoilId;

    /** default constructor */
    public Gathering() {
    }

    /**
     * 构造方法
     * 
     * @param loansignname
     *            借款标号
     * @param money
     *            收款金额
     * @param pmethod
     *            收款方式
     * @param state
     *            说明
     * @param time
     *            收款日期
     * @param recoilId
     *            反冲数据关联的原始数据id
     */
    public Gathering(String loansignname, Double money, String pmethod,
            String state, String time, Long recoilId) {
        this.loansignname = loansignname;
        this.money = money;
        this.pmethod = pmethod;
        this.state = state;
        this.time = time;
        this.recoilId = recoilId;
    }

    /**
     * 编号
     * 
     * @return 编号
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * 编号
     * 
     * @param id
     *            编号
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 借款标号
     * 
     * @return 借款标号
     */
    @Column(name = "loansignname", length = 20)
    public String getLoansignname() {
        return this.loansignname;
    }

    /**
     * 借款标号
     * 
     * @param loansignname
     *            借款标号
     */
    public void setLoansignname(String loansignname) {
        this.loansignname = loansignname;
    }

    /**
     * 收款金额
     * 
     * @return 收款金额
     */
    @Column(name = "money", precision = 18, scale = 4)
    public Double getMoney() {
        return this.money;
    }

    /**
     * 收款金额
     * 
     * @param money
     *            收款金额
     */
    public void setMoney(Double money) {
        this.money = money;
    }

    /**
     * 收款方式
     * 
     * @return 收款方式
     */
    @Column(name = "pmethod", length = 20)
    public String getPmethod() {
        return this.pmethod;
    }

    /**
     * 收款方式
     * 
     * @param pmethod
     *            收款方式
     */
    public void setPmethod(String pmethod) {
        this.pmethod = pmethod;
    }

    /**
     * 说明
     * 
     * @return 说明
     */
    @Column(name = "state", length = 100)
    public String getState() {
        return this.state;
    }

    /**
     * 说明
     * 
     * @param state
     *            说明
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 收款日期
     * 
     * @return 收款日期
     */
    @Column(name = "time", length = 60)
    public String getTime() {
        return this.time;
    }

    /**
     * 收款日期
     * 
     * @param time
     *            收款日期
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 反冲数据关联的原始数据id
     * 
     * @return 反冲数据关联的原始数据id
     */
    @Column(name = "recoil_id")
    public Long getRecoilId() {
        return this.recoilId;
    }

    /**
     * 反冲数据关联的原始数据id
     * 
     * @param recoilId
     *            反冲数据关联的原始数据id
     */
    public void setRecoilId(Long recoilId) {
        this.recoilId = recoilId;
    }

}