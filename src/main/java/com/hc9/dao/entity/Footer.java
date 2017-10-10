package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Footer
 */
@Entity
@Table(name = "footer")
public class Footer implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private Long id;

    /**
     * 网址
     */
    private String url;

    /**
     * 公司名称
     */
    private String name;

    /**
     * 公司地址
     */
    private String address;

    /**
     * 公司地址
     */
    private String phone;

    /**
     * 公司邮箱
     */
    private String email;

    /**
     * 版权所有
     */
    private String copyright;

    /**
     * 备案号
     */
    private String records;

    /**
     * 其他
     */
    private String context;

    /**
     * 400电话
     */
    private String phone400;
    /**
     * 工作时间
     */
    private String workTime;
    /**
     * qq群号
     */
    private String  qqGroupNumber; 
    
    /**
     * 新浪url
     * 
     */
    private String xlurl;
    
    /**
     * 腾讯url
     */
    private String txurl;
    
    /** default constructor */
    public Footer() {
    }

    /**
     * 构造方法
     * 
     * @param url
     *            网址
     * @param name
     *            公司名称
     * @param address
     *            地址
     * @param phone
     *            手机号码
     * @param email
     *            邮箱
     * @param copyright
     *            版权所有
     * @param records
     *            备案号
     * @param context
     *            其他
     */
    public Footer(String url, String name, String address, String phone,
            String email, String copyright, String records, String context) {
        this.url = url;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.copyright = copyright;
        this.records = records;
        this.context = context;
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
     * 网址
     * 
     * @return 网址
     */
    @Column(name = "url", length = 2048)
    public String getUrl() {
        return this.url;
    }

    /**
     * 网址
     * 
     * @param url
     *            网址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 公司名称
     * 
     * @return 公司名称
     */
    @Column(name = "name", length = 128)
    public String getName() {
        return this.name;
    }

    /**
     * 公司名称
     * 
     * @param name
     *            公司名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 地址
     * 
     * @return 地址
     */
    @Column(name = "address", length = 128)
    public String getAddress() {
        return this.address;
    }

    /**
     * 地址
     * 
     * @param address
     *            地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 电话号码
     * 
     * @return 电话号码
     */
    @Column(name = "phone", length = 20)
    public String getPhone() {
        return this.phone;
    }

    /**
     * 电话号码
     * 
     * @param phone
     *            电话号码
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 邮箱
     * 
     * @return 邮箱
     */
    @Column(name = "email", length = 128)
    public String getEmail() {
        return this.email;
    }

    /**
     * 邮箱
     * 
     * @param email
     *            邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 版权所有
     * 
     * @return 版权所有
     */
    @Column(name = "copyright", length = 128)
    public String getCopyright() {
        return this.copyright;
    }

    /**
     * 版权所有
     * 
     * @param copyright
     *            版权所有
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * 备案号
     * 
     * @return 备案号
     */
    @Column(name = "records", length = 128)
    public String getRecords() {
        return this.records;
    }

    /**
     * 备案号
     * 
     * @param records
     *            备案号
     */
    public void setRecords(String records) {
        this.records = records;
    }

    /**
     * 其他
     * 
     * @return 其他
     */
    @Column(name = "context", length = 256)
    public String getContext() {
        return this.context;
    }

    /**
     * 其他
     * 
     * @param context
     *            其他
     */
    public void setContext(String context) {
        this.context = context;
    }
    /**
     * 400电话
     * @return phone400
     */
    @Column(name="phone400",length=30)
	public String getPhone400() {
		return phone400;
	}
    /**
     * @param phone400 400电话
     */
	public void setPhone400(String phone400) {
		this.phone400 = phone400;
	}
	/**
	 * 工作时间
	 * @return workTime
	 */
	@Column(name="workTime",length=30)
	public String getWorkTime() {
		return workTime;
	}
	/**
	 * @param workTime 工作时间
	 */
	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}
	/**
	 * qq群号
	 * @return qqGroupNumber
	 */
	@Column(name = "qqGroupNumber")
	public String getQqGroupNumber() {
		return qqGroupNumber;
	}
	/**
	 * @param qqGroupNumber qq群号
	 */
	public void setQqGroupNumber(String qqGroupNumber) {
		this.qqGroupNumber = qqGroupNumber;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getXlurl() {
		return xlurl;
	}

	public void setXlurl(String xlurl) {
		this.xlurl = xlurl;
	}

	public String getTxurl() {
		return txurl;
	}

	public void setTxurl(String txurl) {
		this.txurl = txurl;
	}
    

}