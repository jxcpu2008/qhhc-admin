package com.hc9.common.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.hc9.commons.log.LOG;

public class ParameterPaic {
	
	private static Properties pro;
	
	static{
		try {
			 pro = PropertiesLoaderUtils.loadAllProperties("config/paic/paic.properties");
		} catch (IOException e) {
			LOG.error("文件读取失败");
		}
	} 
	
	public static String getPublicKey() {
		return pro.getProperty("publicKey");
	}
	
	public static String getPrivateKey() {
		return pro.getProperty("privateKey");
	}
	
	public static String getUrlBlachlist() {
		return pro.getProperty("urlBlachlist");
	}
	
	public static String getUrlCredoo() {
		return pro.getProperty("urlCredoo");
	}
	
	public static String getOrgCode() {
		return pro.getProperty("orgCode");
	}
	
	public static String getAuthCode() {
		return pro.getProperty("authCode");
	}
	
	public static String getUserName() {
		return pro.getProperty("userName");
	}
	
	public static String getUserPassword() {
		return pro.getProperty("userPassword");
	}
	
	public static String getCheckKey() {
		return pro.getProperty("checkKey");
	}
	
}
