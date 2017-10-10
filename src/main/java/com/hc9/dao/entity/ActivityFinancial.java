package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "activity_financial")
public class ActivityFinancial implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	/** 主键id，自增 */
	private Long id;
	
	/** 用户id */
	private Long userId;
	
	/** 用户姓名 */
	private String userName;
	
	/** 手机号 */
	private String mobilePhone;
	
	/** 投资金额 */
	private Double money;
	
	/** 注册时间 */
	private String createTime;
	
	/**渠道来源**/
	private Integer registerSource;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "userId", length = 20)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "userName", length = 50)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "mobilePhone", length = 32)
	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	@Column(name = "money", precision = 10, scale = 2)
	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	@Column(name = "createTime")
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "registerSource", length = 1)
	public Integer getRegisterSource() {
		return registerSource;
	}

	public void setRegisterSource(Integer registerSource) {
		this.registerSource = registerSource;
	}
	
	

}