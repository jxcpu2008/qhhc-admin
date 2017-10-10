package com.hc9.model;

import com.hc9.common.constant.Constant;

/**
 * 注册平台账号对象
 * 
 * @author 
 * 
 */
public class RegisterInfo {

	/**
	 * 真实姓名
	 */
	private String name;

	/**
	 * 身份证号
	 */
	private String id_card;

	/**
	 * 用户编号
	 */
	private String user_id;

	/**
	 * 绑定成功回调地址，接收返回结果
	 */
	private String return_url=Constant.ASYNCHRONISMREGISTRATION;

	/**
	 * 页面自动跳转地址(不携带绑定结果信息)
	 */
	private String page_url=Constant.REGISTRATION;
	
	/**
	 * 手机号码
	 */
	private String bf_account;
	
	/***
	 * 是否已有宝付会员账号（0 无 、 1有）
	 */
	private String has_bf_account;
	
	/***
	 * 平台绑定码
        已有宝付账号的时候验证
        默认 0
	 */
	private String bind_code;
	
	/***
	 * 会员账户类型   1：手机注册(唯一支持)
	 */
	private String account_type;
	
	public RegisterInfo(){
	}
	/***
	 * 服务端注册的
	 * @param has_bf_account
	 * @param bf_account
	 * @param user_id
	 * @param name
	 * @param id_card
	 * @param bind_code
	 * @param account_type
	 */
	public RegisterInfo(String has_bf_account, String bf_account,String user_id,String name, String id_card, 
			String bind_code, String account_type) {
		this.name = name;
		this.id_card = id_card;
		this.user_id = user_id;
		this.bf_account = bf_account;
		this.has_bf_account = has_bf_account;
		this.bind_code = bind_code;
		this.account_type = account_type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getReturn_url() {
		return return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	public String getPage_url() {
		return page_url;
	}

	public void setPage_url(String page_url) {
		this.page_url = page_url;
	}

	public String getId_card() {
		return id_card;
	}

	public void setId_card(String id_card) {
		this.id_card = id_card;
	}

	public String getBf_account() {
		return bf_account;
	}

	public void setBf_account(String bf_account) {
		this.bf_account = bf_account;
	}

	public String getHas_bf_account() {
		return has_bf_account;
	}

	public void setHas_bf_account(String has_bf_account) {
		this.has_bf_account = has_bf_account;
	}

	public String getBind_code() {
		return bind_code;
	}

	public void setBind_code(String bind_code) {
		this.bind_code = bind_code;
	}

	public String getAccount_type() {
		return account_type;
	}

	public void setAccount_type(String account_type) {
		this.account_type = account_type;
	}
	

}
