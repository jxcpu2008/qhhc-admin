package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Vipinfo
 */
@Entity
@Table(name = "vipinfo")
public class Vipinfo implements java.io.Serializable {

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
     * 订单号
     */
    private String number;
    /**
     * 购买会员状态(1 未支付，2 购买成功 3 购买失败)
     */
    private Integer status;
    /**
     * 用户基本信息
     */
    private Userbasicsinfo userbasicsinfo;
    /**
     * 会员类型
     */
    private Viptype viptype; 
    /**
     * 开始时间
     */
    private String begintime; 
    /**
     * 结束时间
     */
    private String endtime; 
    /**
     * 缴费时间
     */
    private String time; 
    /**
     * ips订单号
     */
    private String ipsbillno;
    /**
     * 银行订单号
     */
    private String bankbillno;
    

    // Constructors

    /** default constructor */
    public Vipinfo() {
    }

    public Vipinfo(String number, Integer status,
			Userbasicsinfo userbasicsinfo, Viptype viptype) {
		super();
		this.number = number;
		this.status = status;
		this.userbasicsinfo = userbasicsinfo;
		this.viptype = viptype;
	}
	/**
     * full constructor
     * @param userbasicsinfo 用户基本信息
     * @param viptype 会员类型
     * @param begintime 开始时间
     * @param endtime 结束时间
     * @param time 缴费时间
     */
    public Vipinfo(String number,Integer status,Userbasicsinfo userbasicsinfo, Viptype viptype,
            String begintime, String endtime, String time,String ipsbillno,String bankbillno) {
    	this.number = number;
    	this.status = status;
        this.userbasicsinfo = userbasicsinfo;
        this.viptype = viptype;
        this.begintime = begintime;
        this.endtime = endtime;
        this.time = time;
        this.ipsbillno = ipsbillno;
        this.bankbillno = bankbillno;
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
     * 订单编号
     * @return number
     */
    @Column(name = "number",length=30)
    public String getNumber() {
		return number;
	}
    /**
     * @param number 订单编号
     */
	public void setNumber(String number) {
		this.number = number;
	}
	/**
	 * 支付情况
	 * @return status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status 支付情况
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
     *  
     * @return Userbasicsinfo
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
     * @return Viptype
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viptype_id")
    public Viptype getViptype() {
        return this.viptype;
    }

    /**
     * 
     * @param viptype 会员类型
     */
    public void setViptype(Viptype viptype) {
        this.viptype = viptype;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "begintime")
    public String getBegintime() {
        return this.begintime;
    }

    /**
     * 
     * @param begintime 开始时间
     */
    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "endtime")
    public String getEndtime() {
        return this.endtime;
    }

    /**
     * 
     * @param endtime 结束时间
     */
    public void setEndtime(String endtime) {
        this.endtime = endtime;
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
     * @param time 缴费时间
     */
    public void setTime(String time) {
        this.time = time;
    }
    /**
     * ips订单号
     * @return ipsbillno
     */
    @Column(name="ipsbillno",length=30)
	public String getIpsbillno() {
		return ipsbillno;
	}
	/**
	 * @param ipsbillno ips订单号
	 */
	public void setIpsbillno(String ipsbillno) {
		this.ipsbillno = ipsbillno;
	}
	/**
	 * 银行订单号
	 * @return bankbillno
	 */
	@Column(name="bankbillno",length=30)
	public String getBankbillno() {
		return bankbillno;
	}
	/**
	 * @param bankbillno 银行订单号
	 */
	public void setBankbillno(String bankbillno) {
		this.bankbillno = bankbillno;
	}

}