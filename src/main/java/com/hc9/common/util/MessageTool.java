package com.hc9.common.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * 站短工具
 * @author frank
 *
 */
public class MessageTool {
	private static Properties pro = null;
	/**
	 * 提现申请的通知标题
	 */
	public static String Withdraw_Title;
	
	/**
	 * 提现申请的通知内容
	 */
	public static String Withdraw_Context;
	
	/**
	 * 借款人申请标题
	 */
	public static String Borrower_Title;
	
	/**
	 * 借款人申请内容
	 */
	public static String Borrower_Context;
	
	
	/**
	 * 借款申请标题
	 */
	public static String Borrow_Title;
	
	/**
	 * 借款申请内容
	 */
	public static String Borrow_Context;
	
	static{
		try {
			 pro = PropertiesLoaderUtils.loadAllProperties("config/context/msg/messages.properties");
			 Withdraw_Title= new String(pro.getProperty("wdTitle").getBytes("ISO-8859-1"),"UTF-8");
			 Withdraw_Context= new String(pro.getProperty("wdContext").getBytes("ISO-8859-1"),"UTF-8");
			 Borrower_Title= new String(pro.getProperty("borrowerTitle").getBytes("ISO-8859-1"),"UTF-8");
			 Borrower_Context= new String(pro.getProperty("borrowerContext").getBytes("ISO-8859-1"),"UTF-8");
			 Borrow_Title= new String(pro.getProperty("borrowTitle").getBytes("ISO-8859-1"),"UTF-8");
			 Borrow_Context= new String(pro.getProperty("borrowContext").getBytes("ISO-8859-1"),"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
}
