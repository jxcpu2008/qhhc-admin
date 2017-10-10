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

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 银行信息
 */
@Entity
@Table(name = "userbank")
public class UserBank implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 会员基本信息
     */
    @JsonIgnore
    private Userbasicsinfo userbasicsinfo;
    
    /***
     * 银行账户
     */
    private String bank_no;

    /**
     * 开户名称
     */
    private String bank_name;

    /**
     * 开户省
     */
    private String pro_value;

    /**
     * 开户市
     */
    private String city_value;

    /**
     * 开户支行
     */
    private String bank_address;
    
    /***
     * 手机验证码
     */
    private String validate_code;
    
    /***
     *  状态：1-成功  0-待确认 -1失败
     */
    private Integer state;
    
    /** default constructor */
    public UserBank() {
    }
    
    public UserBank(Long id, Userbasicsinfo userbasicsinfo, String bank_no,
			String bank_name, String pro_value, String city_value,
			String bank_address) {
		super();
		this.id = id;
		this.userbasicsinfo = userbasicsinfo;
		this.bank_no = bank_no;
		this.bank_name = bank_name;
		this.pro_value = pro_value;
		this.city_value = city_value;
		this.bank_address = bank_address;
	}








	/**
     * 主键
     * 
     * @return 主键
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
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
     * 会员基本信息
     * 
     * @return 会员基本信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public Userbasicsinfo getUserbasicsinfo() {
        return this.userbasicsinfo;
    }

    /**
     * 会员基本信息
     * 
     * @param userbasicsinfo
     *            会员基本信息
     */
    public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }


    @Column(name = "bank_no")
	public String getBank_no() {
		return bank_no;
	}

	public void setBank_no(String bank_no) {
		this.bank_no = bank_no;
	}


	 @Column(name = "bank_name")
	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	 @Column(name = "pro_value")
	public String getPro_value() {
		return pro_value;
	}
	
	public void setPro_value(String pro_value) {
		this.pro_value = pro_value;
	}

	 @Column(name = "city_value")
	public String getCity_value() {
		return city_value;
	}

	public void setCity_value(String city_value) {
		this.city_value = city_value;
	}

	@Column(name = "bank_address")
	public String getBank_address() {
		return bank_address;
	}


	public void setBank_address(String bank_address) {
		this.bank_address = bank_address;
	}
	@Column(name = "validate_code")
	public String getValidate_code() {
		return validate_code;
	}

	public void setValidate_code(String validate_code) {
		this.validate_code = validate_code;
	}
	@Column(name = "state")
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

    
    
}