package com.hc9.common.redis;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;

/** 桃花朵朵开活动 **/
@Service
public class HcPeachActivitiCache {

	@Resource
	private HibernateSupport dao;
	
	
	/** 判断当前时间是否在活动时间范围内 */
	public static int validCurrentDate(Date currentDate) {
		int result = 0;
		/** 活动开始时间 */
		String beginDateStr = getActiveBeginDate();
		
		/** 活动结束时间 */
		String endDateStr = getActiveEndDate();

		Date beginDate = DateFormatUtil.stringToDate(beginDateStr, "yyyy-MM-dd");
		Date endDate = DateFormatUtil.stringToDate(endDateStr, "yyyy-MM-dd");
		
		/** 当前时间早于活动开始时间 */
		if(DateFormatUtil.isBefore(currentDate, beginDate)) {
			result = -1;
		}
		
		if(DateFormatUtil.isBefore(endDate, currentDate)) {
			result = -2;
		}
		
		return result;
	}
	
	/** 获取活动开始时间 */
	public static String getActiveBeginDate() {
		String beginDateKey = "STR:HC9:MATERIAL:PEACH:BEGIN:DATE";
		String beginDate = RedisHelper.get(beginDateKey);
		if(StringUtil.isBlank(beginDate)) {
			beginDate = "2016-03-01";
			RedisHelper.set(beginDateKey, beginDate);
		}
		return beginDate;
	}
	
	/** 获取活动结束时间 */
	public static String getActiveEndDate() {
		String endDateKey = "STR:HC9:MATERIAL:PEACH:END:DATE";
		String endDate = RedisHelper.get(endDateKey);
		if(StringUtil.isBlank(endDate)) {
			endDate = "2016-03-31";
			RedisHelper.set(endDateKey, endDate);
		}
		return endDate;
	}
	
	/** 赠送永久抽奖 机会
	 * @param userId 被赠送人主键id
	 * @param chanceNum 赠送次数*/
	public static void increasePermanentLotteryChance(long userId, int chanceNum) {
		if(validCurrentDate(new Date()) >= 0) {
			String key = "INT:HC9:NEWYEAR:PERM:PEACH:NUM:" + userId;
			RedisHelper.incrBy(key, chanceNum);
		}
	}
	
	/** 减少赠送的永久抽奖机会 */
	public static void decreasePermanentLotteryChance(long userId) {
		String key = "INT:HC9:NEWYEAR:PERM:PEACH:NUM:" + userId;
		RedisHelper.decrBy(key, 1);
	}
	
	/** 获取用户赠送的永久抽奖次数 */
	public static int getPermanentLotteryChance(long userId) {
		String key = "INT:HC9:NEWYEAR:PERM:PEACH:NUM:" + userId;
		String grantNum = RedisHelper.get(key);
		if(StringUtil.isBlank(grantNum)) {
			grantNum = "0";
		}
		return Integer.valueOf(grantNum);
	}
	
	/**
	 * 获取是否提醒用户的标识
	 * @param userId
	 * @param currentDate
	 * @param type 1、抽奖次数2、实名认证、3、宝付授权
	 * @return
	 */
	public static int getUserRemind(long userId,String currentDate,String type) {
		String key = "INT:HC9:PERM:PEACH:DATE:"+ currentDate +":USERID:" + userId + ":TYPE:"+type;
		String grantNum = RedisHelper.get(key);
		if(StringUtil.isBlank(grantNum)) {
			grantNum = "0";
		}
		return Integer.valueOf(grantNum);
	}
	
	/**
	 * 记录是否提醒用户的标识
	 * @param userId
	 * @param currentDate
	 * @param type 1、抽奖次数2、实名认证、3、宝付授权
	 * @return
	 */
	public static void setUserRemind(long userId,String currentDate,String type) {
		String key = "INT:HC9:PERM:PEACH:DATE:"+ currentDate +":USERID:" + userId + ":TYPE:"+type;
		RedisHelper.set(key, "1");
	}
	
	
	
	/**
	 * 获取审核是否通过标识
	 * type:1、审核通过 2、审核不通过
	 */
	public static int getUserAuditRemind(long userId,Integer type) {
		String key = "INT:HC9:PERM:PEACH:AUDIT:TYPE:"+type+":USERID:" + userId;
		String grantNum = RedisHelper.get(key);
		if(StringUtil.isBlank(grantNum)) {
			grantNum = "1";
		}
		return Integer.valueOf(grantNum);
	}
	
	/**
	 * 记录审核是否通过标识
	 * type:1、审核通过 2、审核不通过 onOff :1：开0：关
	 */
	public static void setUserAuditRemind(long userId,Integer type,String onOff) {
		String key = "INT:HC9:PERM:PEACH:AUDIT:TYPE:"+type+":USERID:" + userId;
		RedisHelper.set(key, onOff);
	}
	
	public static ActivityMonkey generateActivityMonkey(String userId, String phone, Userbasicsinfo byUser) {
		Date date = new Date();
		String createTime = DateFormatUtil.dateToString(date, "yyyy-MM-dd HH:mm:ss");
		
		ActivityMonkey activityMonkey = new ActivityMonkey();
		activityMonkey.setUserId(Long.parseLong(userId));
		activityMonkey.setMobilePhone(phone);
		activityMonkey.setMoney(0D);
		activityMonkey.setType(10);
		activityMonkey.setLoanId(0l);
		activityMonkey.setLoanName("");
		activityMonkey.setLoanRecordId(0l);
		activityMonkey.setRewardMoney(2d);
		activityMonkey.setCreateTime(createTime);
		activityMonkey.setWeek(0);
		activityMonkey.setStatus(0);
		activityMonkey.setExamineStatus(9);
		activityMonkey.setByUser(byUser);
		return activityMonkey;
	}
	
}
