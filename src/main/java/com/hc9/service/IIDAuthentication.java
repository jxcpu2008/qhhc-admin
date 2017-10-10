package com.hc9.service;


/**
 * 身份证验证接口
 * @author Administrator
 *
 */
public interface IIDAuthentication {
	/**
	 * 身份证验证
	 * @param userName 姓名
	 * @param idNumber 身份证号
	 */
	Object isRealIDCard(String userName,String idNumber);
}
