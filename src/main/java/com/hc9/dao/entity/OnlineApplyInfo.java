package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 在线申请 BankRate OnlineApplyInfo.
 */
@Entity
@Table(name = "online_apply_info")
public class OnlineApplyInfo implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键 */
    private Long id;
    /** 真实姓名 */
    private String name;
    /** 省 */
    private long provinceId;
    /** 市 */
    private long cityId;
    /** 电话号码 */
    private String phone;
    /** 投资金额 */
    private Double money;
    /** 服务内容 */
    private String content;
    /** 状态 */
    private Integer state;

    // Constructors

    /** default constructor */
    public OnlineApplyInfo() {
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param name
     *            真实姓名
     * @param provinceId
     *            省
     * @param cityId
     *            市
     * @param phone
     *            电话
     * @param money
     *            投资金额
     * @param content
     *            服务内容
     * @param state
     *            状态
     */
    public OnlineApplyInfo(String name, long provinceId, long cityId,
            String phone, Double money, String content, Integer state) {
        this.name = name;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.phone = phone;
        this.money = money;
        this.content = content;
        this.state = state;
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
     * @return id
     */
    @Id
    @GeneratedValue
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
     * Title: getName
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return name
     */
    @Column(name = "name", nullable = false, length = 20)
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Title: setName
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>
     * Title: getProvinceId
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return provinceId
     */
    @Column(name = "provinceId", nullable = false)
    public long getProvinceId() {
        return provinceId;
    }

    /**
     * <p>
     * Title: setProvinceId
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param provinceId
     *            provinceId
     */
    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
    }

    /**
     * <p>
     * Title: getCityId
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return cityId
     */
    @Column(name = "cityId", nullable = false)
    public long getCityId() {
        return cityId;
    }

    /**
     * <p>
     * Title: setCityId
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param cityId
     *            cityId
     */
    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    /**
     * <p>
     * Title: getPhone
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return phone
     */
    @Column(name = "phone", nullable = false, length = 15)
    public String getPhone() {
        return phone;
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
        return money;
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
     * Title: getContent
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return content
     */
    @Column(name = "content", nullable = false, length = 15)
    public String getContent() {
        return content;
    }

    /**
     * <p>
     * Title: setContent
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param content
     *            content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * <p>
     * Title: getState
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return state
     */
    @Column(name = "state")
    public Integer getState() {
        return state;
    }

    /**
     * <p>
     * Title: setState
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param state
     *            state
     */
    public void setState(Integer state) {
        this.state = state;
    }

}