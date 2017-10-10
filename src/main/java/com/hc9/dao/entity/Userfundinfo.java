package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;

import javax.persistence.ManyToOne;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * 用户资金信息
 */
@Entity
@Table(name = "userfundinfo")
@JsonIgnoreProperties(value = {"userbasicsinfo"})
public class Userfundinfo implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户基本信息
     */
    private Userbasicsinfo userbasicsinfo;

    /**
     * 可用余额
     */
    private Double cashBalance=0d;

    /**
     * 奖金余额
     */
    private Double bonusBalance;

    /**
     * 授信额度
     */
    private Double credit;
    /**
     * 冻结金额
     */
    private Double frozenAmtN;
    
    /**
     * 用户可用金额
     */
    private Double money;
    
    /**
     * 环讯账号
     */
    private String pIdentNo;
    
    
    /***
     * 操作金额
     */
    private Double operationMoney;
    
    /***
     * 收取提现金额
     */
    private Double withdrawMoney=0d;

    /** default constructor */
    public Userfundinfo() {
    }

    /**
     * 构造方法
     * 
     * @param userbasicsinfo
     *            用户基本信息
     * @param cashBalance
     *            可用余额
     * @param bonusBalance
     *            奖金月
     * @param credit
     *            授信额度
     * @param pIdentNo
     *            环迅账号
     */
    public Userfundinfo(Userbasicsinfo userbasicsinfo, Double cashBalance,
            Double bonusBalance, Double credit, String pIdentNo,Double frozenAmtN,Double money) {
        this.userbasicsinfo = userbasicsinfo;
        this.cashBalance = cashBalance;
        this.bonusBalance = bonusBalance;
        this.credit = credit;
        this.pIdentNo = pIdentNo;
        this.frozenAmtN = frozenAmtN;
        this.money = money;
    }

    /**
     * 主键
     * 
     * @return 主键
     */
    @Id
    @GenericGenerator(name = "fund_id", strategy = "foreign", parameters = { @Parameter(name = "property", value = "userbasicsinfo") })
    @GeneratedValue(generator = "fund_id")
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * 主键
     * 
     * @param id
     *            主键
     */
    public void setId(Long id) {
        this.id = id;
    }


    /**
     * 购买产品
     * @param money money
     */
    public void payProduct(double money){
        this.cashBalance-=money;
    }

    /**
     * 用户基本信息
     * 
     * @return 用户基本信息
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id", unique = true, nullable = false, insertable = false, updatable = false)
    public Userbasicsinfo getUserbasicsinfo() {
        return this.userbasicsinfo;
    }

    /**
     * 用户基本信息
     * 
     * @param userbasicsinfo
     *            用户基本信息
     */
    public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }

    /**
     * 可用余额
     * 
     * @return 可用余额
     */
    @Column(name = "cashBalance", precision = 18, scale = 4)
    public Double getCashBalance() {
        return this.cashBalance;
    }

    /**
     * 可用余额
     * 
     * @param cashBalance
     *            可用余额
     */
    public void setCashBalance(Double cashBalance) {
        this.cashBalance = cashBalance;
    }

    /**
     * 奖金余额
     * 
     * @return 奖金余额
     */
    @Column(name = "bonusBalance", precision = 18, scale = 4)
    public Double getBonusBalance() {
        return this.bonusBalance;
    }

    /**
     * 奖金余额
     * 
     * @param bonusBalance
     *            奖金余额
     */
    public void setBonusBalance(Double bonusBalance) {
        this.bonusBalance = bonusBalance;
    }

    /**
     * 授信额度
     * 
     * @return 手续额度
     */
    @Column(name = "credit", precision = 18, scale = 4)
    public Double getCredit() {
        return this.credit;
    }

    /**
     * 授信额度
     * 
     * @param credit
     *            授信额度
     */
    public void setCredit(Double credit) {
        this.credit = credit;
    }

    /**
     * 环迅账号
     * 
     * @return 环迅账号
     */
    @Column(name = "pIdentNo", length = 50)
    public String getpIdentNo() {
        return pIdentNo;
    }

    /**
     * 环迅账号
     * 
     * @param pIdentNo
     *            环迅账号
     */
    public void setpIdentNo(String pIdentNo) {
        this.pIdentNo = pIdentNo;
    }
    /**
     * 冻结金额
     * @return frozenAmtN
     */
    @Column(name = "frozenAmtN")
	public Double getFrozenAmtN() {
		return frozenAmtN;
	}
	/**
	 * @param frozenAmtN 冻结金额
	 */
	public void setFrozenAmtN(Double frozenAmtN) {
		this.frozenAmtN = frozenAmtN;
	}
	/**
	 * 用户可用余额
	 * @return money
	 */
	@Column(name = "money")
	public Double getMoney() {
		return money;
	}
	/**
	 * @param money 用户可用余额
	 */
	public void setMoney(Double money) {
		this.money = money;
	}

	@Column(name = "operation_money")
	public Double getOperationMoney() {
		return operationMoney;
	}

	public void setOperationMoney(Double operationMoney) {
		this.operationMoney = operationMoney;
	}

	@Column(name = "withdraw_money")
	public Double getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(Double withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}
	
	
	
	
}