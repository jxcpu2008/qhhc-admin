package com.hc9.common.interceptor;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hc9.common.constant.Constant;
import com.hc9.dao.entity.Adminuser;
import com.hc9.service.AdminLoginLogService;

public class OperationLoggingInterceptor extends HandlerInterceptorAdapter {
	
	private static final Logger logger = Logger.getLogger(OperationLoggingInterceptor.class);
	
	private Properties properties;
	
	@Autowired
	private AdminLoginLogService logservice;
	
	public void init() {
		Resource res = new ClassPathResource("config/user/operationInfo.properties");     
		EncodedResource encRes = new EncodedResource(res, "UTF-8"); 
     	try {
			properties = PropertiesLoaderUtils.loadProperties(encRes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info("加载操作链接配置文件出错，请检查！");
			e.printStackTrace();
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		String requestUri = request.getRequestURI();
		logger.debug("操作请求执行的链接为：" + requestUri);
		
		// 忽略处理页面跳转的链接
		if (requestUri.indexOf("/jume") < 0) {
			String operationRemark = properties.getProperty(requestUri);
			Adminuser adminuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
			
			// session失效的话,则不记录操作日志
			if (adminuser != null) {
				logservice.addlog_TRAN(adminuser, requestUri, operationRemark, request);
			}
		}
		
		return true;
	}
}