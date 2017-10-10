package com.hc9.common.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.hc9.commons.log.LOG;

public class ParameterCredit {
	
	/**查得结果*/
	public static final String ONE="1";
	
	/**未查得 */
	public static final String TWO="2";
	
	/** 其他原因未查得 */
	public static final String THREE="3";
	
	private static Properties pro;
	
	static{
		try {
			 pro = PropertiesLoaderUtils.loadAllProperties("config/user/credit.properties");
		} catch (IOException e) {
			LOG.error("文件读取失败");
		}
	} 
	
	/**
	 * 用户名
	 * @return
	 */
	public static String getUser() {
		return pro.getProperty("USER");
	}
	
	/**
	 * 密码
	 * @return
	 */
	public static String getPassword() {
		return pro.getProperty("PWD");
	}
	/**
	 * 查询地址
	 * @return
	 */
	public static String getCardUrl() {
		return pro.getProperty("CARDURL");
	}
	/**
	 * 引用ID，查询者提交的用于识别本次查询的流水号
	 * @return
	 */
	public static String getRefId() {
		return pro.getProperty("RefId");
	}
	/**
	 * 查询的收费子报告类型ID
	 * @return
	 */
	public static String getSubReportId() {
		return pro.getProperty("SubReportId");
	}
}
