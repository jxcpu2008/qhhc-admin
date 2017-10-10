package com.hc9.common.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.log.LOG;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.ActivityFinancial;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.service.ActivityFinancialQueryService;

/** 理财师活动 **/
@Service
public class HcFinancialActivitiCache {

	@Resource
	private ActivityFinancialQueryService activityFinancialQueryService;
	
	/** 现金奖励
	 * @param middle */
	public void activityFinancial(Userbasicsinfo user, Double priority) {
		String userId = user.getId().toString();
		String key = "NEWYEAR:INVEST:FINANCIAL:LIST";
		
		String keyUser = "NEWYEAR:INVEST:FINANCIAL:USER" + userId;
		Map<String, String> mapUser = IndexDataCache.getObject(keyUser);
		
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
					
					if(getRewardMoney(Double.parseDouble(map.get("money"))) > Double.parseDouble(mapUser.get("money"))){
						mapUser.put("money", getRewardMoney(Double.parseDouble(map.get("money")))+"");
						mapUser.put("static", "1");
						IndexDataCache.set(keyUser, mapUser);
					}
					
					ActivityFinancial activityFinancial = activityFinancialQueryService.getActivityFinancial(userId);
					if(activityFinancial == null){
						activityFinancial = new ActivityFinancial();
						activityFinancial.setUserId(user.getId());
						activityFinancial.setUserName(user.getName());
						activityFinancial.setMobilePhone(user.getUserrelationinfo().getPhone());
						activityFinancial.setCreateTime(user.getCreateTime());
						activityFinancial.setRegisterSource(user.getRegisterSource());
					}
					activityFinancial.setMoney(Double.parseDouble(map.get("money")));
					activityFinancialQueryService.addActivityFinancial(activityFinancial);
					
					break;
				}
			}
			if(bo){
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", userId);
				map.put("money", priority+"");
				map.put("phone", user.getUserrelationinfo().getPhone());
				map.put("time", System.currentTimeMillis()+"");
				list.add(map);
				
				mapUser = new HashMap<String, String>();
				mapUser.put("money", getRewardMoney(priority)+"");
				mapUser.put("static", "1");
				IndexDataCache.set(keyUser, mapUser);
				
				ActivityFinancial activityFinancial = activityFinancialQueryService.getActivityFinancial(userId);
				if(activityFinancial == null){
					activityFinancial = new ActivityFinancial();
					activityFinancial.setUserId(user.getId());
					activityFinancial.setUserName(user.getName());
					activityFinancial.setMobilePhone(user.getUserrelationinfo().getPhone());
					activityFinancial.setCreateTime(user.getCreateTime());
					activityFinancial.setRegisterSource(user.getRegisterSource());
				}
				activityFinancial.setMoney(Double.parseDouble(map.get("money")));
				activityFinancialQueryService.addActivityFinancial(activityFinancial);
			}
		} else {
			list = new ArrayList<>();
			Map<String, String> map = new HashMap<String, String>();
			map.put("userId", userId);
			map.put("money", priority+"");
			map.put("phone", user.getUserrelationinfo().getPhone());
			map.put("time", System.currentTimeMillis()+"");
			list.add(map);
			
			mapUser = new HashMap<String, String>();
			mapUser.put("money", getRewardMoney(priority)+"");
			mapUser.put("static", "1");
			IndexDataCache.set(keyUser, mapUser);
			
			ActivityFinancial activityFinancial = activityFinancialQueryService.getActivityFinancial(userId);
			if(activityFinancial == null){
				activityFinancial = new ActivityFinancial();
				activityFinancial.setUserId(user.getId());
				activityFinancial.setUserName(user.getName());
				activityFinancial.setMobilePhone(user.getUserrelationinfo().getPhone());
				activityFinancial.setCreateTime(user.getCreateTime());
				activityFinancial.setRegisterSource(user.getRegisterSource());
			}
			activityFinancial.setMoney(Double.parseDouble(map.get("money")));
			activityFinancialQueryService.addActivityFinancial(activityFinancial);
		}
		Collections.sort(list, new MyFinancial());
		LOG.error("H5理财师活动 日志更新：现金奖励");
		IndexDataCache.set(key, list);
	}
	
	public static double getRewardMoney(double money){
		double rewardMoney = 0d;
		if(money >= 100000){
			if(money >= 1000000){
				rewardMoney = 1888d;
			}else if(money >= 500000){
				rewardMoney = 888d;
			}else if(money >= 200000){
				rewardMoney = 388d;
			}else if(money >= 100000){
				rewardMoney = 188d;
			}
		}
		return rewardMoney;
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
		String beginDateKey = "STR:HC9:MATERIAL:FINANCIAL:BEGIN:DATE";
		String beginDate = RedisHelper.get(beginDateKey);
		if(StringUtil.isBlank(beginDate)) {
			beginDate = "2016-03-01";
			RedisHelper.set(beginDateKey, beginDate);
		}
		return beginDate;
	}
	
	/** 获取活动结束时间 */
	public static String getActiveEndDate() {
		String endDateKey = "STR:HC9:MATERIAL:FINANCIAL:END:DATE";
		String endDate = RedisHelper.get(endDateKey);
		if(StringUtil.isBlank(endDate)) {
			endDate = "2016-04-30";
			RedisHelper.set(endDateKey, endDate);
		}
		return endDate;
	}
}

class MyFinancial implements Comparator {
	public int compare(Object o1, Object o2) {
		Map<String, String> s1 = (Map<String, String>) o1;
		Map<String, String> s2 = (Map<String, String>) o2;
		if (Double.parseDouble(s1.get("money")) - Double.parseDouble(s2.get("money")) != 0)
			return (int)(Double.parseDouble(s2.get("money")) - Double.parseDouble(s1.get("money")));
		else
			return (int)(Long.parseLong(s1.get("time")) - Long.parseLong(s2.get("time")));
	}
}