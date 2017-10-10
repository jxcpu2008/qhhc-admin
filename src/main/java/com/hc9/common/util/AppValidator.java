package com.hc9.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * 校验器：正则表达式 常用验证
 * @author xuyh
 *
 */
public class AppValidator {

	/**
     * 正则表达式：验证用户名
     */
    private static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";
  
    /**
     * 正则表达式：验证密码
     */
    private static final String REGEX_PASSWORD = "^([`~!@#$%^&*()_\\-+=|{}':;'\\\\,\\[\\].<>/?]|[a-zA-Z0-9]){6,18}$";
    
    /**
     * 正则表达式：验证手机号
     */
    private static final String REGEX_PHONE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9])|17[0-9])\\d{8}$";
  
    /**
     * 正则表达式：验证邮箱
     */
    private static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
  
    /**
     * 正则表达式：验证汉字
     */
    private static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";
  
    /**
     * 正则表达式：验证身份证
     */
    private static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";
  
    /**
     * 正则表达式：验证URL
     */
    private static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
  
    /**
     * 正则表达式：验证IP地址
     */
    private static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
    
    /**
     * 校验用户名
     * 
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
		if (isNotNullAndEmpty(username)) {
			return Pattern.matches(REGEX_USERNAME, username);
		}
		return false;
    }
  
    /**
     * 校验密码
     * 
     * @param password
     * @return 校验通过返回true，否则返回false
     */
	public static boolean isPassword(String password) {
		if (isNotNullAndEmpty(password)) {
			return Pattern.matches(REGEX_PASSWORD, password);
		}
		return false;
    }
  
    /**
     * 校验手机号
     * 
     * @param phone
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPhone(String phone) {
		if (isNotNullAndEmpty(phone)) {

		}
		return Pattern.matches(REGEX_PHONE, phone);
    }
  
    /**
     * 校验邮箱
     * 
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
		if (isNotNullAndEmpty(email)) {

		}
		return Pattern.matches(REGEX_EMAIL, email);
    }
  
    /**
     * 校验汉字
     * 
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
		if (isNotNullAndEmpty(chinese)) {

		}
		return Pattern.matches(REGEX_CHINESE, chinese);
    }
  
    /**
     * 校验身份证
     * 
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
		if (isNotNullAndEmpty(idCard)) {
			return Pattern.matches(REGEX_ID_CARD, idCard);
		}
		return false;
       
    }
  
    /**
     * 校验URL
     * 
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
		if (isNotNullAndEmpty(url)) {
			return Pattern.matches(REGEX_URL, url);
		}
		return false;
    }
  
    /**
     * 校验IP地址
     * 
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
		if (isNotNullAndEmpty(ipAddr)) {
			return Pattern.matches(REGEX_IP_ADDR, ipAddr);
		}
		return false;
    }
    
    /**
     * 其他验证
     * @param args
     */
    
    /**
     * 验证 字符串 非空
     * @param str
     * @return 非空 true
     */
    public static boolean isNotNullAndEmpty(String str){
		if (str != null && !str.isEmpty() && !"".equals(str)) {
			return true;
		}
		return false;
    }
    /**
     * 验证 字符串 非空
     * @param str
     * @return 空 true
     */
    public static boolean isNullOrEmpty(String str){
		if (str == null || str.isEmpty() || "".equals(str)) {
			return true;
		}
		return false;
    }
    
    /**
     * 验证 集合是否为空
     * @param collection
     * @return 空 true
     */
    public static boolean isNullOrEmpty(Collection<?> collection) {
        if (collection==null || collection.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证 集合是否为空
     * @param collection
     * @return 非空 true
     */
    public static boolean isNotNullAndEmpty(Collection<?> collection) {
    	 if (collection!=null && !collection.isEmpty()) {
             return true;
         } 
    	 return false;
    }
    
    /**
     * 验证map集合 是否为空
     * @param map 
     * @return 空 true
     */
    public static boolean isNullOrEmpty(Map<?, ?> map) {
        if (map==null || map.isEmpty()) {
            return true;
        } 
        return false;
    }

    /**
     *  验证map集合 是否为空
     * @param map
     * @return 非空 true
     */
    public static boolean isNotNullAndEmpty(Map<?, ?> map) {
    	 if (map!=null&&!map.isEmpty()) {
             return true;
         } 
         return false;
    }

    /**
     * 验证数组 是否为空
     * @param objects
     * @return 空 true
     */
    public static boolean isNullOrEmpty(Object[] objects) {
        if(objects==null||objects.length==0){
        	return true;
        }
    	return false;
    }

    /**
     *  验证数组 是否为空
     * @param objects
     * @return 非空 true
     */
    public static boolean isNotNullAndEmpty(Object[] objects) {
    	  if(objects!=null&&objects.length>0){
          	return true;
          }
      	return false;
    }

    /**
     * 验证对象 是否为空
     * @param object
     * @return 空 true
     */
    public static boolean isNull(Object object) {
        if (object==null) {
            return true;
        } 
        return false;
    }

    /**
     * 验证对象 是否为空
     * @param Object
     * @return 非空 true
     */
    public static boolean isNotNull(Object object) {
    	if(object!=null){
    		return true;	
    	}
        return false;
    }
    
    
    
  
    public static void main(String[] args) {
//        String phone = "18959263020";
//        String password="?~!@#$0aD";
    	String email="xuyuanhao@hc9.com";
        System.out.println(AppValidator.isEmail(email));
        String str="zhangzhang";
        
        System.out.println(str.substring(0,2).concat("***").concat(str.substring(str.length()-2,str.length())));
        
        
        
        
//        System.out.println(AppValidator.isPassword(password));
      
//        String regEx="^([`~!@#$%^&*()_\\-+=|{}':;'\\\\,\\[\\].<>/?]|[a-zA-Z0-9]){6,18}$";
//        String regEx1="[\\\\]+";
//        System.out.println(Pattern.matches(regEx, password));
        
    }
}
