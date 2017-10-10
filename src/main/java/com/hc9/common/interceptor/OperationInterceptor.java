package com.hc9.common.interceptor;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.hc9.common.constant.Constant;
import com.hc9.dao.entity.Adminuser;
import com.hc9.service.AdminLoginLogService;

public class OperationInterceptor implements MethodInterceptor {
	
	private static final Logger logger = Logger.getLogger(OperationInterceptor.class);
	
	@Autowired
	private AdminLoginLogService logservice;

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		// TODO Auto-generated method stub
		Resource res = new ClassPathResource("config/user/operationInfo.properties");     
		EncodedResource encRes = new EncodedResource(res, "UTF-8"); 
     	Properties properties = PropertiesLoaderUtils.loadProperties(encRes);
		String methodName = mi.getMethod().getName();
		
		// 登录时获取校验码以及登录不需要记录操作日志
		if ("getCode".equals(methodName) || "adminlogin".equals(methodName)) {
			// do nothing
		} else {
			Object[] args = mi.getArguments();
			for (Object arg : args) {
				if (arg instanceof HttpServletRequest) {
					HttpServletRequest request = (HttpServletRequest) arg;
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
				}
			}
		}
		
		return mi.proceed();
	}
}
