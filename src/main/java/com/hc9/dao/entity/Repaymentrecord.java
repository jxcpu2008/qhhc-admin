package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Repaymentrecord
 */
@Entity
@Table(name = "repaymentrecord")
public class Repaymentrecord implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 主键id
     */
    private Long id;
    /**
     * 借款标信息
     */
    private Loansign loansign;
    /**
     * 期数
     */
    private Integer periods;
    /**
     * 预计还款日期
     */
    private String preRepayDate;
    /**
     * 优先本金
     */
    private Double money;
    /**
     *优先 预计还款利息
     */
    private Double preRepayMoney;
    /**
     * 还款状态：1未还款、2按时还款、3逾期未还款、4逾期已还款、5提前还款
     */
    private Integer repayState;
    /**
     * 实际还款时间
     */
    private String repayTime;
    /**
     * 优先 实际还款利息
     */
    private Double realMoney;
    /**
     * 逾期利息
     */
    private Double overdueInterest;
    /**
     * 商户还款订单号
     */
    private String pMerBillNo;
    /**
     * IPS 还款受理订单号
     */
    private String pIpsBillNo;
    
    /**
     * IPS受理时间
     */
    private String pIpsTime1;
    
    /**
     * IPS处理时间
     */
    private String pIpsTime2;
    
    /**
     * 催收邮件发送次数
     */
    private Integer remindEmailCount=0;
    
    /**
     * 催收短信发送次数
     */
    private Integer remindSMSCount=0;
    
    /**
     * 自动还款结果通知;0:未通知，1:已通知
     */
    private Integer autoRepayAdvice;
    
    
	 /**
	 * 夹层本金
	 */
	private Double  middleMoney ;
	 /**
	 * 夹层预计还款利息
	 */
	private Double  middlePreRepayMoney;
	 /**
	 * 夹层实际利息
	 */
	private Double  middleRealMoney;
	 /**
	 * 劣后金额
	 */
	private Double  afterMoney;
	 /**
	 * 劣后预期利息
	 */
	private Double  afterPreRepayMoney;
	 /**
	 * 劣后实际利息
	 */
	private Double  afterRealMoney;
	
	/***
	 * 公司应收服务费
	 */
	private Double companyPreFee;
	
	/***
	 * 公司实收服务费
	 */
	private Double companyRealFee;
    
    /**
     * 自动积分表信息
     */
   /* private Set<Autointegral> autointegrals = new HashSet<Autointegral>(0);*/
	private Set<Autointegral> autointegrals;
	
	
	private Set<Repaymentrecordparticulars> repaymentrecordparticulars;
    
    // Constructors

    /** default constructor */
    public Repaymentrecord() {
    }

    /** full constructor */
    /**
     * 
     * @param loansign 借款标信息
     * @param periods 期数
     * @param preRepayDate 预计还款天数
     * @param money 本金
     * @param preRepayMoney 预计还款金额
     * @param repayState 还款状态
     * @param repayTime 预计还款时间
     * @param realMoney 时间还款金额
     * @param autointegrals 自动积分信息
     */
    public Repaymentrecord(Loansign loansign, Integer periods,
            String preRepayDate, Double money, Double preRepayMoney,
            Integer repayState, String repayTime, Double realMoney,
			Set<Autointegral> autointegrals, String pMerBillNo,
			String pIpsBillNo, Double overdueInterest, Double middleMoney,
			Double middlePreRepayMoney, Double middleRealMoney,
			Double afterMoney, Double afterPreRepayMoney, Double afterRealMoney) {
		this.loansign = loansign;
		this.periods = periods;
		this.preRepayDate = preRepayDate;
		this.money = money;
		this.preRepayMoney = preRepayMoney;
		this.repayState = repayState;
		this.repayTime = repayTime;
		this.realMoney = realMoney;
		this.autointegrals = autointegrals;
		this.pMerBillNo = pMerBillNo;
		this.pIpsBillNo = pIpsBillNo;
		this.overdueInterest = overdueInterest;
		this.middleMoney = middleMoney;
		this.middlePreRepayMoney = middlePreRepayMoney;
		this.middleRealMoney = middleRealMoney;
		this.afterMoney = afterMoney;
		this.afterPreRepayMoney = afterPreRepayMoney;
		this.afterRealMoney = afterRealMoney;
	}

    // Property accessors
    /**
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
     * 
     * @param id 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * @return Loansign
     */
    @ManyToOne
    @JoinColumn(name = "loanSign_id")
    public Loansign getLoansign() {
        return this.loansign;
    }

    /**
     * 
     * @param loansign 借款标信息
     */
    public void setLoansign(Loansign loansign) {
        this.loansign = loansign;
    }

    /**
     *  
     * @return Integer
     */
    @Column(name = "periods")
    public Integer getPeriods() {
        return this.periods;
    }

    /**
     * 
     * @param periods 期数
     */
    public void setPeriods(Integer periods) {
        this.periods = periods;
    }

    /**
     * 预计还款天数
     * @return String
     */
    @Column(name = "preRepayDate")
    public String getPreRepayDate() {
        return this.preRepayDate;
    }

    /**
     * 
     * @param preRepayDate 预计还款天数
     */
    public void setPreRepayDate(String preRepayDate) {
        this.preRepayDate = preRepayDate;
    }

    /**
     * money 优先本金
     * @return  Double
     */
    @Column(name = "money", precision = 18, scale = 4)
    public Double getMoney() {
        return this.money;
    }

    /**
     * 
     * @param money 优先本金
     */
    public void setMoney(Double money) {
        this.money = money;
    }

    /**
     * 优先预计还款利息
     * @return Double
     */
    @Column(name = "preRepayMoney", precision = 18, scale = 4)
    public Double getPreRepayMoney() {
        return this.preRepayMoney;
    }

    /**
     *优先 预计还款利息
     * @param preRepayMoney 预计还款利息
     */
    public void setPreRepayMoney(Double preRepayMoney) {
        this.preRepayMoney = preRepayMoney;
    }

    /**
     *  还款状态：1未还款、2按时还款、3逾期未还款、4逾期已还款、5提前还款
     * @return Integer
     */
    @Column(name = "repayState")
    public Integer getRepayState() {
        return this.repayState;
    }

    /**
     * 还款状态：1未还款、2按时还款、3逾期未还款、4逾期已还款、5提前还款
     * @param repayState 还款状态
     */
    public void setRepayState(Integer repayState) {
        this.repayState = repayState;
    }

    /**
     * 实际还款时间
     * @return String
     */
    @Column(name = "repayTime")
    public String getRepayTime() {
        return this.repayTime;
    }

    /**
     * 
     * @param repayTime 实际还款时间
     */
    public void setRepayTime(String repayTime) {
        this.repayTime = repayTime;
    }

    /**
     * 实际还款利息
     * @return Double
     */
    @Column(name = "realMoney", precision = 18, scale = 4)
    public Double getRealMoney() {
        return this.realMoney;
    }

    /**
     * 
     * @param realMoney 实际还款利息
     */
    public void setRealMoney(Double realMoney) {
        this.realMoney = realMoney;
    }
    
    /**
     * 逾期利息
     * @return
     */
    @Column(name="overdueInterest",precision = 18, scale = 4)
    public Double getOverdueInterest() {
		return overdueInterest;
	}
    /**
     * 逾期利息
     * @param overdueInterest 逾期利息
     */
	public void setOverdueInterest(Double overdueInterest) {
		this.overdueInterest = overdueInterest;
	}

	/**
     * 还款编号
     * @return pMerBillNo
     */
    @Column(name = "pMerBillNo",length = 30)
    public String getpMerBillNo() {
		return pMerBillNo;
	}
    /**
     * @param pMerBillNo 还款编号
     */
	public void setpMerBillNo(String pMerBillNo) {
		this.pMerBillNo = pMerBillNo;
	}
	/**
	 * IPS 还款订单号
	 * @return pIpsBillNo
	 */
	@Column(name = "pIpsBillNo",length = 30)
	public String getpIpsBillNo() {
		return pIpsBillNo;
	}
	/**
	 * @param pIpsBillNo IPS 还款订单号
	 */
	public void setpIpsBillNo(String pIpsBillNo) {
		this.pIpsBillNo = pIpsBillNo;
	}

/*	*//**
     * 
     * @return Set<Autointegral>
     *//*
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "repaymentrecord")
    public Set<Autointegral> getAutointegrals() {
        return this.autointegrals;
    }

    *//**
     * 
     * @param autointegrals 自动积分信息
     *//*
    public void setAutointegrals(Set<Autointegral> autointegrals) {
        this.autointegrals = autointegrals;
    }*/
	
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "repaymentrecord")
	public Set<Repaymentrecordparticulars> getRepaymentrecordparticulars() {
		return repaymentrecordparticulars;
	}

	public void setRepaymentrecordparticulars(
			Set<Repaymentrecordparticulars> repaymentrecordparticulars) {
		this.repaymentrecordparticulars = repaymentrecordparticulars;
	}
    
    /**
     * IPS受理日期
     * @return
     */
    @Column(name = "pIpsTime1")
	public String getpIpsTime1() {
		return pIpsTime1;
	}
    /**
     * IPS受理日期
     * @return
     */
	public void setpIpsTime1(String pIpsTime) {
		this.pIpsTime1 = pIpsTime;
	}
    /**
     * IPS处理日期
     * @return
     */
    @Column(name = "pIpsTime2")
	public String getpIpsTime2() {
		return pIpsTime2;
	}
    /**
     * IPS处理日期
     * @return
     */
	public void setpIpsTime2(String pIpsTime) {
		this.pIpsTime2 = pIpsTime;
	}
	@Column(name="remindEmailCount")
	public Integer getRemindEmailCount() {
		return remindEmailCount;
	}
	
	public void setRemindEmailCount(Integer remindEmailCount) {
		this.remindEmailCount = remindEmailCount;
	}
	@Column(name="remindSMSCount")
	public Integer getRemindSMSCount() {
		return remindSMSCount;
	}

	public void setRemindSMSCount(Integer remindSMSCount) {
		this.remindSMSCount = remindSMSCount;
	}
	
	@Column(name="autoRepayAdvice")
	public Integer getAutoRepayAdvice() {
		return autoRepayAdvice;
	}

	public void setAutoRepayAdvice(Integer autoRepayAdvice) {
		this.autoRepayAdvice = autoRepayAdvice;
	}

	
	@Column(name = "middleMoney", precision = 18, scale = 4)
	public Double getMiddleMoney() {
		return middleMoney;
	}

	@Column(name = "middlePreRepayMoney", precision = 18, scale = 4)
	public Double getMiddlePreRepayMoney() {
		return middlePreRepayMoney;
	}

	@Column(name = "middleRealMoney", precision = 18, scale = 4)
	public Double getMiddleRealMoney() {
		return middleRealMoney;
	}

	@Column(name = "afterMoney", precision = 18, scale = 4)
	public Double getAfterMoney() {
		return afterMoney;
	}

	@Column(name = "afterPreRepayMoney", precision = 18, scale = 4)
	public Double getAfterPreRepayMoney() {
		return afterPreRepayMoney;
	}

	@Column(name = "afterRealMoney", precision = 18, scale = 4)
	public Double getAfterRealMoney() {
		return afterRealMoney;
	}

	public void setMiddleMoney(Double middleMoney) {
		this.middleMoney = middleMoney;
	}

	public void setMiddlePreRepayMoney(Double middlePreRepayMoney) {
		this.middlePreRepayMoney = middlePreRepayMoney;
	}

	public void setMiddleRealMoney(Double middleRealMoney) {
		this.middleRealMoney = middleRealMoney;
	}

	public void setAfterMoney(Double afterMoney) {
		this.afterMoney = afterMoney;
	}

	public void setAfterPreRepayMoney(Double afterPreRepayMoney) {
		this.afterPreRepayMoney = afterPreRepayMoney;
	}

	public void setAfterRealMoney(Double afterRealMoney) {
		this.afterRealMoney = afterRealMoney;
	}
	@Column(name = "companyPreFee", precision = 18, scale = 4)
	public Double getCompanyPreFee() {
		return companyPreFee;
	}

	public void setCompanyPreFee(Double companyPreFee) {
		this.companyPreFee = companyPreFee;
	}
	@Column(name = "companyRealFee", precision = 18, scale = 4)
	public Double getCompanyRealFee() {
		return companyRealFee;
	}

	public void setCompanyRealFee(Double companyRealFee) {
		this.companyRealFee = companyRealFee;
	}

	
	
}