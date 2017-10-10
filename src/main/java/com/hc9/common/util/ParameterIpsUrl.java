package com.hc9.common.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * 获取ips资金托管参数
 * @author RanQiBing
 * 2014-04-18
 *
 */
public class ParameterIpsUrl {
	
	private static Properties pro = null;
	static{
		try {
			 pro = PropertiesLoaderUtils.loadAllProperties("config/context/pay/payurl.properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	/**
	 * 用户注册
	 * @return
	 */
	public static String getRegistrationurl() {
		return pro.getProperty("REGISTRATIONTESTURL");
	}
	
	/***
	 * 查询银行账户信息
	 * @return
	 */
	public static String getBankCarUrl(){
		return pro.getProperty("BANKCARDTESTURL");
	}
	
	/**
	 * 充值地址
	 * @return
	 */
	public static String getRechargeurl() {
		return pro.getProperty("RECHARGETESTURL");
	}
	
	/***
	 * 绑定验证码
	 * @return
	 */
	public static String getSendBindCodeUrl(){
		return pro.getProperty("SENDBINDCODEURL");
	}
	
	
	/***
	 * 授权
	 * @return
	 */
	public static String getInaccreditURL(){
		return pro.getProperty("INACCREDITURL");
	}
	
	/***
	 * 绑定银行卡
	 * @return
	 */
	public static String getOpBankCardURL(){
		return pro.getProperty("OPBANKCARDURL");
	}
	
	/***
	 * 服务端注册用户
	 * @return
	 */
	public static String getQuickRegisterUrl(){
		return pro.getProperty("QUICKREGISTERURL");
	}
	
	/**
	 * 投标页面
	 * @return
	 */
	public static String getBidurl() {
		return pro.getProperty("BIDTESTURL");
	}
	
	/**
	 * 投标服务器
	 * @return
	 */
	public static String getserverBidurl() {
		return pro.getProperty("REPAYMENTTESTURL");
	}
	
	/**
	 * 业务查询
	 * @return
	 */
	public static String getP2pQueryUrl() {
		return pro.getProperty("P2PQUERYTESTURL");
	}
	
	
	/**
	 * 自动投标
	 * @return
	 */
	public static String getAutomaticbidurl() {
		return pro.getProperty("AUTOMATICBIDTESTURL");
	}
	
	/**
	 * 还款
	 * @return
	 */
	public static String getRepaymenturl() {
		return pro.getProperty("REPAYMENTTESTURL");
	}
	
	/**
	 * 提现
	 * @return
	 */
	public static String getWithdrawalurl() {
		return pro.getProperty("WITHDRAWALTESTURL");
	}
	
	/***
	 * 服务端提现
	 * @return
	 */
	public static String getWithdrawServceurl() {
		return pro.getProperty("WITHDRAWSERVERTERURL");
	}
	
	/**
	 * 账户余额查询
	 * @return
	 */
	public static String getBalabceinquiryurl() {
		return pro.getProperty("BALABCEINQUIRYTESTURL");
	}
	
	/**
	 * 获取银行列表
	 * @return
	 */
	public static String getBanklistqeryurl() {
		return pro.getProperty("BANKLISTQUERYTESTURL");
	}
	
	/**
	 * 发布标同时到环讯登记
	 * @return
	 */
	public static String getRegSub() {
		return pro.getProperty("REGSUB");
	}
	

	/**
	 * 转账地址
	 * @return
	 */
	public static String getTransferUrl(){
		return pro.getProperty("TRANSFER");
	}

	/**
	 *债权转让投标
	 * @return
	 */
	public static String getBidAssignment(){
		return pro.getProperty("BIDASSIGNMENTTESTURL");
	}
	/**
	 * 查询交易及用户信息地址
	 */
	public static String getCreditWSQuery(){
		return pro.getProperty("CREDITWSQUERYURL");
	}
	/**
	 * 自动投标规则地址
	 * @return
	 */
	public static String getAutomatic(){
		return pro.getProperty("AUTOMATICTESTURL");
	}
	
	/**
	 * 自动还款签约
	 */
	public static String getRepaymentSignUrl(){
		return pro.getProperty("REPAYMENTSIGNURL");
	}
	
}
