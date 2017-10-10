package com.hc9.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.sms.SmsSendHelp;
import com.hc9.common.util.StringUtil;

/** 短信工具类服务 */
@Service
public class SmsUtilService {
	/** 注册时发短信验证码 */
	public void sendCodeForReg(HttpServletRequest request,String phone) {
		String numberCode = StringUtil.getvalidcode();
		SmsSendHelp.sendRegisterSmsValidCode(phone, numberCode);
		request.getSession().setAttribute("regCode", numberCode);
		request.getSession().setAttribute("againTime", System.currentTimeMillis() + Constant.MILLISECONDS);
	}
}