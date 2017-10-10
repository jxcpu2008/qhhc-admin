package com.hc9.service.sms.wd;

/***
 * 沃动短信url、action
 * @author lkl
 *
 */
public interface CommonWdSms {
	
	//发送接口
	String sendAction="send";
	
	//余额及已发送量查询接口
	String overageAction="overage";
	
	//非法关键字查询
	String checkkeyWordAction="checkkeyword";
	
	//状态报告接口/上行接口
	String queryAction="query";
	
	//营销中发送接口、余额及已发送量查询接口、非法关键字查询的url
	String sendMarketUrl="http://218.244.136.70:8888/sms.aspx";
	
	//营销中状态报告接口/上行接口
	String queryMarketUrl="http://218.244.136.70:8888/statusApi.aspx";
	
	//行业中发送接口、余额及已发送量查询接口、非法关键字查询的url
	String sendIndustryUrl="http://115.29.242.32:8888/sms.aspx";
	
	//行业中状态报告接口/上行接口
	String queryIndustryUrl="http://115.29.242.32:8888/statusApi.aspx";

}
