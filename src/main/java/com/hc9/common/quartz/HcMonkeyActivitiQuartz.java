package com.hc9.common.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;

import com.hc9.common.redis.IndexDataCache;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.impl.HibernateSupport;

public class HcMonkeyActivitiQuartz {

	@Resource
	private HibernateSupport dao;
    
	public void run(){
		int week = week();
		if(week > 0){
			String key = "NEWYEAR:INVEST:MONKEY:WEEK:" + week;
			List<Map> list = IndexDataCache.getList(key);
			if (list != null) {
				// 插入库
				List<ActivityMonkey> activityMonkeyList = new ArrayList<ActivityMonkey>();
				for (int i = 0; i < list.size() && i < 3; i++) {
					Map<String, String> map = list.get(i);
					String rewardMoney = "0";
					if(i==0){
						rewardMoney = (Double.parseDouble(map.get("money"))*0.8/100)+"";
					}else if(i==1){
						rewardMoney = (Double.parseDouble(map.get("money"))*0.6/100)+"";
					}else if(i==2){
						rewardMoney = (Double.parseDouble(map.get("money"))*0.3/100)+"";
					}
					activityMonkeyList.add(generateActivityMonkey(map.get("userId"), map.get("phone"), map.get("money"), (3+i), "0", "", map.get("loanRecordId")+"", rewardMoney, week));
				}
				if(activityMonkeyList.size() > 0){
					dao.saveOrUpdateAll(activityMonkeyList);
				}
			}
			if(week == 6){
				key = "NEWYEAR:INVEST:MONKEY:TOTAL";
				list = IndexDataCache.getList(key);
				if (list != null) {
					// 插入库
					List<ActivityMonkey> activityMonkeyList = new ArrayList<ActivityMonkey>();
					for (int i = 0; i < list.size() && i < 3; i++) {
						Map<String, String> map = list.get(i);
						String rewardMoney = "0";
						activityMonkeyList.add(generateActivityMonkey(map.get("userId"), map.get("phone"), map.get("money"), (6+i), "0", "", map.get("loanRecordId")+"", rewardMoney, 0));
					}
					try {
						dao.saveOrUpdateAll(activityMonkeyList);
					} catch (DataAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public int week(){
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
	
	/** 新春猴给力活动 */
	public ActivityMonkey generateActivityMonkey(String userId, String phone, String priority, Integer type, String loanId, 
			String loanName, String loanRecordId, String rewardMoney, Integer week) {
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
		return activityMonkey;
	}
	
}
