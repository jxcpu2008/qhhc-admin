package com.hc9.model;

/** 投资人短信辅助对象  */
public class InvestSmsVo {
	// 用户id
	private long userId;
	
	/** 客户姓名 */
	private String customerName;
	
	/** 累计认购金额 */
	private Double tenderMoney;
	
	/** 手机号码 */
	private String mobilePhone;
	
	/** 邮箱号码 */
	private String email;

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Double getTenderMoney() {
		return tenderMoney;
	}

	public void setTenderMoney(Double tenderMoney) {
		this.tenderMoney = tenderMoney;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
}