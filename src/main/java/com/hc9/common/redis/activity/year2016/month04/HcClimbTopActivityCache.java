package com.hc9.common.redis.activity.year2016.month04;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.redis.Arith;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;

/** 红筹4月活动:登顶活动缓存 */
public class HcClimbTopActivityCache {
	/** 用户投资：新增用户活动期间的累计金额
	 *  @param loanType 标类型：1-店铺  2-项目 3-天标 4-债权转让
	 *  @param remonth 回购期限:如果是天标的话为天数，项目的话为月数
	 *  @param investType 投资类型：投资类型 1 优先 2 夹层 3劣后
	 *  @param investMoney 投资金额
	 *  
	 *  */
	public static void increaseUserTotalInvest(long userId, int loanType, int remonth, int investType, 
			double investMoney) {
		try {
			if(isClimbTopActivity(new Date())) {
				/**
				 * 奖励区间的金额为活动期间的累计金额，累计金额的计算公式为：
				 * 投资金额*月份数（如： 投资10000元三个月的标的， 则累计投资金额为10000元*3=30000元）
				 * 投资优先与夹层都算进当月投资累计额
				 * */
				if(investType == 1 || investType == 2) {
					if(loanType == 3) {
						remonth = 1;
					}
					String totalInvestKey = "STR:HC9:CLIMB:TOP:TOTAL:INVEST:" + userId;
					long totalInvestMoney = Arith.mul(remonth, investMoney).longValue();
					long totalInvest = RedisHelper.incrBy(totalInvestKey, totalInvestMoney);
					computeClimbLeveNum(userId, totalInvest);
				}
			}
		} catch(Exception e) {
			LOG.error("登顶活动新增用户活动期间累计投资报错！", e);
		}
	}
	
	/** 登顶活动各级别投资人数 */
	public static void computeClimbLeveNum(long userId, long totalInvest) {
		if(totalInvest >= 1000 && totalInvest < 10000) {
			oneLevelClimbPersonNum(userId);
		} else if(totalInvest >= 10000 && totalInvest < 100000) {
			twoLevelClimbPersonNum(userId);
		} else if(totalInvest >= 100000 && totalInvest < 500000) {
			threeLevelClimbPersonNum(userId);
		} else if(totalInvest >= 500000 && totalInvest < 2000000) {
			fourLevelClimbPersonNum(userId);
		} else if(totalInvest >= 2000000) {
			fiveLevelClimbPersonNum(userId);
		}
	}
	
	/** 登顶活动一级相关人数 */
	private static void oneLevelClimbPersonNum(long userId) {
		String oneLevelKey = "STR:HC9:CLIMB:PERSON:NUM:ONE:LEVEL";
		String personNum = RedisHelper.get(oneLevelKey);
		if(StringUtil.isNotBlank(personNum)) {
			List<Long> list = JsonUtil.jsonToList(personNum, Long.class);
			if(!list.contains(userId)) {
				list.add(userId);
				personNum = JsonUtil.toJsonStr(list);
				RedisHelper.set(oneLevelKey, personNum);
			}
		} else {
			List<Long> list = new ArrayList<Long>();
			list.add(userId);
			personNum = JsonUtil.toJsonStr(list);
			RedisHelper.set(oneLevelKey, personNum);
		}
	}
	
	/** 登顶活动二级相关人数 */
	private static void twoLevelClimbPersonNum(long userId) {
		String twoLevelKey = "STR:HC9:CLIMB:PERSON:NUM:TWO:LEVEL";
		String personNum = RedisHelper.get(twoLevelKey);
		if(StringUtil.isNotBlank(personNum)) {
			List<Long> list = JsonUtil.jsonToList(personNum, Long.class);
			if(!list.contains(userId)) {
				list.add(userId);
				personNum = JsonUtil.toJsonStr(list);
				RedisHelper.set(twoLevelKey, personNum);
			}
		} else {
			List<Long> list = new ArrayList<Long>();
			list.add(userId);
			personNum = JsonUtil.toJsonStr(list);
			RedisHelper.set(twoLevelKey, personNum);
		}
		removeUserIdFromLastLeve(userId, "STR:HC9:CLIMB:PERSON:NUM:ONE:LEVEL");
	}
	
	/** 登顶活动三级相关人数 */
	private static void threeLevelClimbPersonNum(long userId) {
		String threeLevelKey = "STR:HC9:CLIMB:PERSON:NUM:THREE:LEVEL";
		String personNum = RedisHelper.get(threeLevelKey);
		if(StringUtil.isNotBlank(personNum)) {
			List<Long> list = JsonUtil.jsonToList(personNum, Long.class);
			if(!list.contains(userId)) {
				list.add(userId);
				personNum = JsonUtil.toJsonStr(list);
				RedisHelper.set(threeLevelKey, personNum);
			}
		} else {
			List<Long> list = new ArrayList<Long>();
			list.add(userId);
			personNum = JsonUtil.toJsonStr(list);
			RedisHelper.set(threeLevelKey, personNum);
		}
		removeUserIdFromLastLeve(userId, "STR:HC9:CLIMB:PERSON:NUM:ONE:LEVEL");
		removeUserIdFromLastLeve(userId, "STR:HC9:CLIMB:PERSON:NUM:TWO:LEVEL");
	}
	
	/** 登顶活动四级相关人数 */
	private static void fourLevelClimbPersonNum(long userId) {
		String fourLevelKey = "STR:HC9:CLIMB:PERSON:NUM:FOUR:LEVEL";
		String personNum = RedisHelper.get(fourLevelKey);
		if(StringUtil.isNotBlank(personNum)) {
			List<Long> list = JsonUtil.jsonToList(personNum, Long.class);
			if(!list.contains(userId)) {
				list.add(userId);
				personNum = JsonUtil.toJsonStr(list);
				RedisHelper.set(fourLevelKey, personNum);
			}
		} else {
			List<Long> list = new ArrayList<Long>();
			list.add(userId);
			personNum = JsonUtil.toJsonStr(list);
			RedisHelper.set(fourLevelKey, personNum);
		}
		removeUserIdFromLastLeve(userId, "STR:HC9:CLIMB:PERSON:NUM:ONE:LEVEL");
		removeUserIdFromLastLeve(userId, "STR:HC9:CLIMB:PERSON:NUM:TWO:LEVEL");
		removeUserIdFromLastLeve(userId, "STR:HC9:CLIMB:PERSON:NUM:THREE:LEVEL");
	}
	
	/** 登顶活动五级相关人数 */
	private static void fiveLevelClimbPersonNum(long userId) {
		String fiveLevelKey = "STR:HC9:CLIMB:PERSON:NUM:FIVE:LEVEL";
		String personNum = RedisHelper.get(fiveLevelKey);
		if(StringUtil.isNotBlank(personNum)) {
			List<Long> list = JsonUtil.jsonToList(personNum, Long.class);
			if(!list.contains(userId)) {
				list.add(userId);
				personNum = JsonUtil.toJsonStr(list);
				RedisHelper.set(fiveLevelKey, personNum);
			}
		} else {
			List<Long> list = new ArrayList<Long>();
			list.add(userId);
			personNum = JsonUtil.toJsonStr(list);
			RedisHelper.set(fiveLevelKey, personNum);
		}
		removeUserIdFromLastLeve(userId, "STR:HC9:CLIMB:PERSON:NUM:ONE:LEVEL");
		removeUserIdFromLastLeve(userId, "STR:HC9:CLIMB:PERSON:NUM:TWO:LEVEL");
		removeUserIdFromLastLeve(userId, "STR:HC9:CLIMB:PERSON:NUM:THREE:LEVEL");
		removeUserIdFromLastLeve(userId, "STR:HC9:CLIMB:PERSON:NUM:FOUR:LEVEL");
	}
	
	/** 从上一级的登顶人数中删除相关人数 */
	private static void removeUserIdFromLastLeve(long userId, String levelKey) {
		String personNum = RedisHelper.get(levelKey);
		if(StringUtil.isNotBlank(personNum)) {
			List<Long> levelList = JsonUtil.jsonToList(personNum, Long.class);
			levelList.remove(userId);
			String json = JsonUtil.toJsonStr(levelList);
			RedisHelper.set(levelKey, json);
		}
	}
	
	/** 获取用户登顶活动期间的累计投资金额 */
	public static long getUserTotalInvest(long userId) {
		long result = 0;
		try {
			String totalInvestKey = "STR:HC9:CLIMB:TOP:TOTAL:INVEST:" + userId;
			String totalInvestMoney = RedisHelper.get(totalInvestKey);
			if(StringUtil.isNotBlank(totalInvestMoney)) {
				result = Long.valueOf(totalInvestMoney).longValue();
			}
		} catch(Exception e) {
			LOG.error("获取登顶活动新增用户活动期间累计投资报错！", e);
		}
		return result;
	}
	
	/** 获取登顶活动 2016年4月1日-2016年4月30日活动开始时间 */
	public static String getClimbTopActivityBeginDate() {
		String beginDateKey = "STR:HC9:CLIMB:TOP:BEGIN:DATE";
		String beginDate = "";
		try {
			beginDate = RedisHelper.get(beginDateKey);
			if(StringUtil.isBlank(beginDate)) {
				beginDate = "2016-04-01 00:00:00";
				RedisHelper.set(beginDateKey, beginDate);
			}
		} catch(Exception e) {
			LOG.error("获取登顶活动开始时间报错,使用默认开始时间2016-04-01 00:00:00！", e);
			beginDate = "2016-04-01 00:00:00";
		}
		return beginDate;
	}
	
	/** 获取登顶活动 2016年4月1日-2016年5月2日活动结束时间 */
	public static String getClimbTopActivityEndDate() {
		String endDateKey = "STR:HC9:CLIMB:TOP:END:DATE";
		String endDate = "";
		try {
			endDate = RedisHelper.get(endDateKey);
			if(StringUtil.isBlank(endDate)) {
				endDate = "2016-05-02 23:59:59";
				RedisHelper.set(endDateKey, endDate);
			}
		} catch(Exception e) {
			LOG.error("获取登顶活动的结束时间报错,使用默认结束时间2016-04-30 23:59:59！", e);
			endDate = "2016-04-30 23:59:59";
		}
		return endDate;
	}
	
	/** 判断是否是登顶活动 2016年4月1日-2016年4月30日活动期间 */
	public static boolean isClimbTopActivity(Date currentDate) {
		boolean result = true;
		/** 活动开始时间 */
		String beginDateStr = getClimbTopActivityBeginDate();
		
		/** 活动结束时间 */
		String endDateStr = getClimbTopActivityEndDate();

		Date beginDate = DateFormatUtil.stringToDate(beginDateStr, "yyyy-MM-dd HH:mm:ss");
		Date endDate = DateFormatUtil.stringToDate(endDateStr, "yyyy-MM-dd HH:mm:ss");
		
		/** 当前时间早于活动开始时间 */
		if(currentDate.before(beginDate)) {
			result = false;
		}
		
		if(endDate.before(currentDate)) {
			result = false;
		}
		return result;
	}
}