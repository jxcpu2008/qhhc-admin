package com.hc9.common.sms;

import java.util.HashMap;
import java.util.Map;

import com.hc9.common.util.SMSHttpRequestUtil;

/** 短信发送相关辅助类 */
public class SmsSendHelp {
	/** 注册时发送短信验证码相关 */
	public static void sendRegisterSmsValidCode(String phone, String code) {
		String methodName = "standardSingleSmsWay";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("smsType", "registerValidCode");
		paramMap.put("phone", phone);
		paramMap.put("code", code);
		SMSHttpRequestUtil.sendSingleSms(methodName, paramMap);
	}
}
