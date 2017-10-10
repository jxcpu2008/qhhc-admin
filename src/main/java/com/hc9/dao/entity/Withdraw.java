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

import com.hc9.common.constant.Constant;

/**
 * Withdraw
 */
@Entity
@Table(name = "withdraw")
public class Withdraw implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Fields
    /**
     * 主键id
     */
    private Long id;
    /**
     * 用户基本信息
     */
    private Userbasicsinfo userbasicsinfo;
    // private Userbank userbank;
    /**
     * 提现平台手续费
     */
    private Double mer_fee;
    
    /**
     * 提现宝付手续费
     */
    private Double fee;
    
    /**
     * 提现返回消息
     */
    private String msg;
    
    
    /**
     * 提现返回状态值-1失败 1成功 0-已提交  5-转账处理中
     */
    private int state;
    
    /**
     * 手续支付方式
     */
    private int fee_taken_on;
    /**
     * 实际提现金额
     */
    private Double withdrawAmount;
    
    /**
     * 申请提现金额
     */
    private Double amount;
    
    /**
     * 提现处理结果
     */
    private String code;
    /**
     * applytime
     */
    private String applytime;
    /**
     * 商户提现订单号
     */
    private String strNum;
    /**
     * 备注
     */
    private String remark;
    /**
     * 提现时间
     */
    private String time;
    /**
     * IPS提现订单号
     */
    private String pIpsBillNo;
    
    /***
     * 记录此笔提现是否收费 1-收取 2-不收取
     */
    private  int feeState=Constant.STATUES_TWO;

    // Constructors

    /** default constructor */
    public Withdraw() {
    }

    /**
     * full constructor
     * 
     * @param userbasicsinfo
     *            用户基础信息
     * @param withdrawAmount
     *            提现金额
     * @param applytime
     *            applytime
     * @param withdrawstate
     *            是否提现成功
     * @param strNum
     *            商户提现订单号
     * @param remark
     *            备注
     * @param time
     *            提现时间
     * @param pIpsBillNo
     *            IPS提现订单号
     */
    public Withdraw(Userbasicsinfo userbasicsinfo, Double withdrawAmount,
            Double deposit, String applytime,
            String strNum, String remark, String time, String pIpsBillNo) {
        this.userbasicsinfo = userbasicsinfo;
        this.withdrawAmount = withdrawAmount;
        this.applytime = applytime;
        this.strNum = strNum;
        this.remark = remark;
        this.time = time;
        this.pIpsBillNo = pIpsBillNo;
    }

    // Property accessors
    /**
     * @return Id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    
    
    
    @Column(name = "msg")
    public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Column(name = "state")
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Column(name = "amount")
    public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "code")
    public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "mer_fee")
    public Double getMer_fee() {
		return mer_fee;
	}

	public void setMer_fee(Double mer_fee) {
		this.mer_fee = mer_fee;
	}

	@Column(name = "fee_taken_on")
    public int getFee_taken_on() {
		return fee_taken_on;
	}

	public void setFee_taken_on(int fee_taken_on) {
		this.fee_taken_on = fee_taken_on;
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
     * @return  Userbasicsinfo
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public Userbasicsinfo getUserbasicsinfo() {
        return this.userbasicsinfo;
    }

    /**
     * 
     * @param userbasicsinfo 用户基本信息
     */
    public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }


    /**
     * 
     * @return Double
     */
    @Column(name = "withdrawAmount", precision = 18, scale = 4)
    public Double getWithdrawAmount() {
        return this.withdrawAmount;
    }

    /**
     * 
     * @param withdrawAmount 提现金额
     */
    public void setWithdrawAmount(Double withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "applytime", length = 30)
    public String getApplytime() {
        return this.applytime;
    }

    /**
     * 
     * @param applytime applytime
     */
    public void setApplytime(String applytime) {
        this.applytime = applytime;
    }


    /**
     * 
     * @return String
     */
    @Column(name = "strNum", length = 50)
    public String getStrNum() {
        return this.strNum;
    }

    /**
     * 
     * @param strNum 打款流水号
     */
    public void setStrNum(String strNum) {
        this.strNum = strNum;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "remark", length = 200)
    public String getRemark() {
        return this.remark;
    }

    /**
     * 
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "time", length = 30)
    public String getTime() {
        return this.time;
    }

    /**
     * 
     * @param time 提现审核时间
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "pIpsBillNo", length = 30)
    public String getpIpsBillNo() {
        return pIpsBillNo;
    }

    /**
     * 
     * @param pIpsBillNo ips提现编号
     */
    public void setpIpsBillNo(String pIpsBillNo) {
        this.pIpsBillNo = pIpsBillNo;
    }

    @Column(name = "fee")
	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	@Column(name = "feeState")
	public int getFeeState() {
		return feeState;
	}

	public void setFeeState(int feeState) {
		this.feeState = feeState;
	}

	
}