package com.hc9.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hc9.common.log.LOG;
import com.hc9.common.redis.IndexDataCache;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.activity.year2016.month04.HcClimbTopActivityCache;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.InvestDao;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.service.activity.year2016.Month06ActivityService;

/** 新春猴给力活动相关 */
@Service
public class HcMonkeyActivitiCache {

	@Resource
	private HibernateSupport dao;
	
	@Resource
	private InvestDao investDao;
	
	@Resource
	private Month06ActivityService month06ActivityService;
	
	/** 活动一
	 * @param middle */
	public void activityMonkeyMax(Loansign loan, Userbasicsinfo user, Double priority, Long loanRecordId) {
		String loanId = loan.getId().toString();
		String userId = user.getId().toString();
		String maxKey = "NEWYEAR:INVEST:MONEY:LOANID:MAX:"+loanId;
		String lastKey = "NEWYEAR:INVEST:MONEY:LOANID:LAST:"+loanId;

		// 一鸣惊人
		Map<String, String> maxMap = null;
		if(RedisHelper.isKeyExist(maxKey)){
			maxMap = RedisHelper.hgetall(maxKey);
			Double money = Double.parseDouble(maxMap.get("money"));
			if(money < priority){
				maxMap.put("userId", userId);
				maxMap.put("money", priority+"");
				maxMap.put("phone", user.getUserrelationinfo().getPhone());
				maxMap.put("loanId", loanId);
				maxMap.put("loanName", loan.getName());
				maxMap.put("loanRecordId", loanRecordId+"");
				maxMap.put("nickname", user.getUserName());
				LOG.error("新春猴给力活动 日志更新：更新一鸣惊人");
			}
		}else{
			maxMap = new HashMap<String, String>();
			maxMap.put("userId", userId);
			maxMap.put("money", priority+"");
			maxMap.put("phone", user.getUserrelationinfo().getPhone());
			maxMap.put("loanId", loanId);
			maxMap.put("loanName", loan.getName());
			maxMap.put("loanRecordId", loanRecordId+"");
			maxMap.put("nickname", user.getUserName());
			LOG.error("新春猴给力活动 日志更新：更新一鸣惊人");
		}
		RedisHelper.hmset(maxKey, maxMap);
		
		double prioRestMoney = investDao.queryPrioRestMoneyByLoanId(loan.getId());
		double midRestMoney = investDao.queryMidRestMoneyByLoanId(loan.getId());
		// 一锤定音
		if (prioRestMoney <= 0 && midRestMoney <= 0) {
			Map<String, String> lastMap = new HashMap<String, String>();
			lastMap.put("userId", user.getId()+"");
			lastMap.put("money", priority+"");
			lastMap.put("phone", user.getUserrelationinfo().getPhone());
			lastMap.put("loanId", loanId);
			lastMap.put("loanName", loan.getName());
			lastMap.put("loanRecordId", loanRecordId+"");
			lastMap.put("nickname", user.getUserName());
			LOG.error("新春猴给力活动 日志更新：更新一锤定音");
			RedisHelper.hmset(lastKey, lastMap);
			// 插入库
			try {
				String maxLonRecordId = maxMap.get("loanRecordId");
				month06ActivityService.generateMaxRecord(maxLonRecordId);
				
				String lastLoanRecordId = lastMap.get("loanRecordId");
				month06ActivityService.generateLatestRecord(lastLoanRecordId);
				
				List<ActivityMonkey> activityMonkeyList = new ArrayList<ActivityMonkey>();
				activityMonkeyList.add(generateActivityMonkey(maxMap.get("userId"), maxMap.get("phone"), maxMap.get("money"), 12, maxMap.get("loanId"), maxMap.get("loanName"), maxMap.get("loanRecordId")+"", "168", 0));
				activityMonkeyList.add(generateActivityMonkey(user.getId()+"", user.getUserrelationinfo().getPhone(), priority+"", 13, loanId, loan.getName(), loanRecordId+"", "68", 0));
				dao.saveOrUpdateAll(activityMonkeyList);
			} catch (DataAccessException e) {
				LOG.error("一锤定音活动相关处理过程中出错！", e);
			}
		}
		
	}
	
	/** 活动二
	 * @param middle */
	public void activityMonkeyWeek(Loansign loan, Userbasicsinfo user, Double priority, Long loanRecordId) {
		String loanId = loan.getId().toString();
		String userId = user.getId().toString();
		String key = "NEWYEAR:INVEST:MONKEY:WEEK:" + (week()+1);
		
		List<Map> list = null;
		if (IndexDataCache.getList(key) != null) {
			list = IndexDataCache.getList(key);
			boolean bo = true;
			for (int i = 0; i < list.size(); i++) {
				Map<String, String> map = list.get(i);
				if(map.get("userId").equals(userId)){
					map.put("money", Arith.add(Double.parseDouble(map.get("money")), priority)+"");
					map.put("time", System.currentTimeMillis()+"");
					list.set(i, map);
					bo = false;
					break;
				}
			}
			if(bo){
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", userId);
				map.put("money", priority+"");
				map.put("phone", user.getUserrelationinfo().getPhone());
				map.put("loanId", loanId);
				map.put("loanName", loan.getName());
				map.put("loanRecordId", loanRecordId+"");
				map.put("time", System.currentTimeMillis()+"");
				list.add(map);
			}
		} else {
			list = new ArrayList<>();
			Map<String, String> map = new HashMap<String, String>();
			map.put("userId", userId);
			map.put("money", priority+"");
			map.put("phone", user.getUserrelationinfo().getPhone());
			map.put("loanId", loanId);
			map.put("loanName", loan.getName());
			map.put("loanRecordId", loanRecordId+"");
			map.put("time", System.currentTimeMillis()+"");
			list.add(map);
		}
		Collections.sort(list, new MyComparator());
		LOG.error("新春猴给力活动 日志更新：活动二");
		IndexDataCache.set(key, list);
	}
		
	/** 活动三
	 * @param middle */
	public void activityMonkeyTotal(Loansign loan, Userbasicsinfo user, Double priority, Long loanRecordId) {
		String loanId = loan.getId().toString();
		String userId = user.getId().toString();
		String key = "NEWYEAR:INVEST:MONKEY:TOTAL";
		
		List<Map> list = IndexDataCache.getList(key);
		if (list != null) {
			boolean bo = true;
			for (int i = 0; i < list.size(); i++) {
				Map<String, String> map = list.get(i);
				if(map.get("userId").equals(userId)){
					map.put("money", Arith.add(Double.parseDouble(map.get("money")), priority)+"");
					map.put("time", System.currentTimeMillis()+"");
					list.set(i, map);
					bo = false;
					break;
				}
			}
			if(bo){
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", userId);
				map.put("money", priority+"");
				map.put("phone", user.getUserrelationinfo().getPhone());
				map.put("loanId", loanId);
				map.put("loanName", loan.getName());
				map.put("loanRecordId", loanRecordId+"");
				map.put("time", System.currentTimeMillis()+"");
				list.add(map);
			}
		} else {
			list = new ArrayList<>();
			Map<String, String> map = new HashMap<String, String>();
			map.put("userId", userId);
			map.put("money", priority+"");
			map.put("phone", user.getUserrelationinfo().getPhone());
			map.put("loanId", loanId);
			map.put("loanName", loan.getName());
			map.put("loanRecordId", loanRecordId+"");
			map.put("time", System.currentTimeMillis()+"");
			list.add(map);
		}
		Collections.sort(list, new MyComparator());
		LOG.error("新春猴给力活动 日志更新：活动三");
		IndexDataCache.set(key, list);
	}
	
	public static int week(){
		Date beginDate0 = DateFormatUtil.stringToDate("2016-01-18", "yyyy-MM-dd");
		Date beginDate1 = DateFormatUtil.stringToDate("2016-01-25", "yyyy-MM-dd");
		Date beginDate2 = DateFormatUtil.stringToDate("2016-02-01", "yyyy-MM-dd");
		Date beginDate3 = DateFormatUtil.stringToDate("2016-02-08", "yyyy-MM-dd");
		Date beginDate4 = DateFormatUtil.stringToDate("2016-02-15", "yyyy-MM-dd");
		Date beginDate5 = DateFormatUtil.stringToDate("2016-02-22", "yyyy-MM-dd");
		Date beginDate6 = DateFormatUtil.stringToDate("2016-02-29", "yyyy-MM-dd");
		
		Date currentDate = new Date();
		if(DateFormatUtil.isBefore(beginDate6, currentDate)){
			return 6;
		}else if(DateFormatUtil.isBefore(beginDate5, currentDate)){
			return 5;
		}else if(DateFormatUtil.isBefore(beginDate4, currentDate)){
			return 4;
		}else if(DateFormatUtil.isBefore(beginDate3, currentDate)){
			return 3;
		}else if(DateFormatUtil.isBefore(beginDate2, currentDate)){
			return 2;
		}else if(DateFormatUtil.isBefore(beginDate1, currentDate)){
			return 1;
		}else if(DateFormatUtil.isBefore(beginDate0, currentDate)){
			return 0;
		}else{
			return -1;
		}
	}
	public static void main(String[] args) {
		week();
	}
	
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
		String beginDateKey = "STR:HC9:MATERIAL:MONKEY:BEGIN:DATE";
		String beginDate = RedisHelper.get(beginDateKey);
		if(StringUtil.isBlank(beginDate)) {
			beginDate = "2016-01-19";
			RedisHelper.set(beginDateKey, beginDate);
		}
		return beginDate;
	}
	
	/** 获取活动结束时间 */
	public static String getActiveEndDate() {
		String endDateKey = "STR:HC9:MATERIAL:MONKEY:END:DATE";
		String endDate = RedisHelper.get(endDateKey);
		if(StringUtil.isBlank(endDate)) {
			endDate = "2016-02-29";
			RedisHelper.set(endDateKey, endDate);
		}
		return endDate;
	}
	
	/** 新春猴给力活动 */
	public static ActivityMonkey generateActivityMonkey(String userId, String phone, String priority, int type, String loanId, 
			String loanName, String loanRecordId, String rewardMoney, int week) {
		Date date = new Date();
		String createTime = DateFormatUtil.dateToString(date, "yyyy-MM-dd HH:mm:ss");
		
		ActivityMonkey activityMonkey = new ActivityMonkey();
		activityMonkey.setUserId(Long.parseLong(userId));
		activityMonkey.setMobilePhone(phone);
		activityMonkey.setMoney(Double.parseDouble(priority));
		activityMonkey.setType(type);
		activityMonkey.setLoanId(Long.parseLong(loanId));
		activityMonkey.setLoanName(loanName);
		activityMonkey.setLoanRecordId(Long.parseLong(loanRecordId));
		activityMonkey.setRewardMoney(Double.parseDouble(rewardMoney));
		activityMonkey.setCreateTime(createTime);
		activityMonkey.setWeek(week);
		activityMonkey.setStatus(0);
		activityMonkey.setExamineStatus(0);
		return activityMonkey;
	}
	
	/**
	 * 用于判断抢标排位活动是之前活动(199、99)还是之后活动(168、68)
	 * @param publish_time 项目发布时间
	 * @return 0:之前  1:之后
	 */
	public static Integer loanBeforeStillAfterSignNum(String publish_time) {
		Date p_time = DateFormatUtil.stringToDate(publish_time, "yyyy-MM-dd");
		Date stage_time = DateFormatUtil.stringToDate(HcClimbTopActivityCache.getClimbTopActivityBeginDate(), "yyyy-MM-dd");
		if(DateFormatUtil.isBefore(p_time , stage_time)) {
			return 0;
		} else {
			return 1;
		}
	}
}

class MyComparator implements Comparator {
	public int compare(Object o1, Object o2) {
		Map<String, String> s1 = (Map<String, String>) o1;
		Map<String, String> s2 = (Map<String, String>) o2;
		if (Double.parseDouble(s1.get("money")) - Double.parseDouble(s2.get("money")) != 0)
			return (int)(Double.parseDouble(s2.get("money")) - Double.parseDouble(s1.get("money")));
		else
			return (int)(Long.parseLong(s1.get("time")) - Long.parseLong(s2.get("time")));
	}
}