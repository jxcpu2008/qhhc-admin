package com.hc9.service;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.impl.HibernateSupport;

/**
 * 户基础信息统计报表数据更新
 *  包括：用户id、真实姓名、手机号码、邮箱、投资金额、首投时间、最近一次投资时间、投资次数、注册时间、最近登录时间
 * */
@Service
public class UserBasicStatisticsService {
	@Resource
	private HibernateSupport dao;
	
	/** 用户基础信息统计报表数据更新
	 *  包括：用户id、真实姓名、手机号码、邮箱、投资金额、首投时间、最近一次投资时间、投资次数、注册时间、最近登录时间  */
	public void syncNewAddUserInfo() {
		/** 新增用户信息 */
		String sql = "select u.id,u.name,u.isLock,r.phone,r.email,u.createTime from userbasicsinfo u, userrelationinfo r "
					+ "where u.id=r.id and u.id not in(select userid from investorstasinfo) order by u.id asc";
		List list = dao.findBySql(sql);
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				Long userId = StatisticsUtil.getLongFromBigInteger(arr[0]);
				String name = StatisticsUtil.getStringFromObject(arr[1]);
				Integer isLock = StatisticsUtil.getIntegerFromObject(arr[2]);
				String phone = StatisticsUtil.getStringFromObject(arr[3]);
				if(StringUtil.isBlank(phone)) {
					phone = null;
				}
				String email = StatisticsUtil.getStringFromObject(arr[4]);
				if(StringUtil.isBlank(email)) {
					email = null;
				}
				String registTime = StatisticsUtil.getStringFromObject(arr[5]);
				String createTime = DateUtils.format("yyyy-MM-dd HH:mm:ss");
				sql = "insert into investorstasinfo(userId,name,isLock,mobilePhone,email,registTime,createTime) values(?,?,?,?,?,?,?)";
				dao.executeSql(sql, userId, name, isLock, phone, email, registTime, createTime);
			}
		}
	}
	
	/** 更细已同步过的用户信息  */
	 public void updateSyncedUserInfo() {
		 String sql = "select u.id,u.name,u.isLock,r.phone,r.email,u.createTime from userbasicsinfo u, userrelationinfo r "
					+ "where u.id=r.id order by u.id asc";
		List list = dao.findBySql(sql);
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				Long userId = StatisticsUtil.getLongFromBigInteger(arr[0]);
				String name = StatisticsUtil.getStringFromObject(arr[1]);
				if("您好，您还没填写真实姓名".equals(name)) {
					name = "";
				}
				Integer isLock = StatisticsUtil.getIntegerFromObject(arr[2]);
				String phone = StatisticsUtil.getStringFromObject(arr[3]);
				if(StringUtil.isBlank(phone)) {
					phone = null;
				}
				String email = StatisticsUtil.getStringFromObject(arr[4]);
				if(StringUtil.isBlank(email)) {
					email = null;
				}
				String registTime = StatisticsUtil.getStringFromObject(arr[5]);
				String createTime = DateUtils.format("yyyy-MM-dd HH:mm:ss");
				
				sql = "update investorstasinfo set name=?,isLock=?,mobilePhone=?,email=?,registTime=? where userId=?";
				dao.executeSql(sql, name, isLock, phone, email ,registTime, userId);
			}
		}
	 }
	 
	 /** 更新投资金额和投资次数 */
	 public void updateInvestMoneyAndNUm() {
		 String sql = "select lr.userbasicinfo_id,sum(lr.tenderMoney),count(lr.userbasicinfo_id) from loanrecord lr "
				 	+ "where lr.isSucceed=1 group by lr.userbasicinfo_id";
		 List list = dao.findBySql(sql);
		 if(list != null && list.size() > 0) {
			 for(Object obj : list) {
					Object[] arr = (Object[])obj;
					Long userId = StatisticsUtil.getLongFromBigInteger(arr[0]);
					Double investMoney = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)arr[1]);
					Long investNum = StatisticsUtil.getLongFromBigInteger(arr[2]);
					sql = "update investorstasinfo set investMoney=?,investNum=? where userId=?";
					dao.executeSql(sql, investMoney, investNum, userId);
			 }
		 }
	 }
	 
	 /** 更新最近登录时间 */
	 public void updateLatestLoginTime() {
		String sql = "select u.user_id,u.logintime from userloginlog u where u.id " 
					+ "in(select max(id) from userloginlog group by user_id)"; 
		List list = dao.findBySql(sql);
		 if(list != null && list.size() > 0) {
			 for(Object obj : list) {
				Object[] arr = (Object[])obj;
				Long userId = StatisticsUtil.getLongFromBigInteger(arr[0]);
				String loginTime = StatisticsUtil.getStringFromObject(arr[1]);
				sql = "update investorstasinfo set latestLoginTime=? where userId=?";
				dao.executeSql(sql, loginTime, userId);
			 }
		 }
	 }
}
