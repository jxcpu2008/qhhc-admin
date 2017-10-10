package com.hc9.model;

/**
 * 
 * @author add by xuyh
 * at 2015/09/01 
 * 渠道信息 实体
 *
 */
public class ChannelInfo {

	//注册时间
	private String createTime;
	//用户姓名
	private String name;
	//手机号码
	private String phone;
	//是否开通宝付
	private String isAuthIps;

	public String getIsAuthIps() {
		return isAuthIps;
	}

	public void setIsAuthIps(String isAuthIps) {
		this.isAuthIps = isAuthIps;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public ChannelInfo(String createTime,String name,String phone,String isAuthIps) {
		super();
		this.createTime = createTime;
		this.name = name;
		this.phone = phone;
		this.isAuthIps=isAuthIps;
	}

}
