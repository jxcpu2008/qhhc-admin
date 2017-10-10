package com.hc9.service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.util.DateUtils;
import com.hc9.common.util.GetIpAddress;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Log;
import com.hc9.dao.impl.HibernateSupport;

@Service
public class AdminLoginLogService {
	
	@Resource
	private HibernateSupport dao;
	
	//记录后台管理人员登录日志
	public void addlog_TRAN(Adminuser adminuser,HttpServletRequest request){
		
		Log loginlog=new Log();
		
		loginlog.setIp(GetIpAddress.getIp(request));
		loginlog.setLoginId(adminuser.getId()+"");
		loginlog.setUserName(adminuser.getRealname());
		loginlog.setLogTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		
		//保存登录日志
		dao.save(loginlog);
	}
	
	//记录后台管理人员登录日志
	public void addlog_TRAN(Adminuser adminuser, String operationUri, String operationDesc, HttpServletRequest request){
		
		Log loginlog = new Log();
		
		loginlog.setIp(GetIpAddress.getIp(request));
		loginlog.setLoginId(adminuser.getId()+"");
		loginlog.setOperationUri(operationUri);
		loginlog.setRemark(operationDesc);
		loginlog.setUserName(adminuser.getRealname());
		loginlog.setLogTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		
		//保存登录日志
		dao.save(loginlog);
	}
}