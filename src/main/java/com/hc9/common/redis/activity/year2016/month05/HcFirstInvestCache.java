package com.hc9.common.redis.activity.year2016.month05;

import java.util.Date;

import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;
import com.hc9.service.activity.ActivityCommonService;

/** 平台首投送现金 */
public class HcFirstInvestCache {
	
	/** 首投送现金活动
	 *  @param userId 投资用户id
	 *  @param investType 投资类型：投资类型 1 优先 2 夹层 3劣后
	 *  @param investMoney 投资金额
	 *  */
	public static void giveMoneyForFirstTime(long userId, int investType, double investMoney, 
			String phone, long loanRecordId, long loanId, String loanName,
			ActivityCommonService activityCommonService) {
		try {
			if(isFirstInvestActivity(new Date())) {
				String key = "STR:HC9:FIRST:INVEST:GIVE:MONEY:" + userId;
				if(!RedisHelper.isKeyExist(key)) {
					/** 首投用户投资优先与夹层都可以，投资劣后不参与，
					 * 如果用户首次投资了劣后，将失去获奖机会 */
					if(investType == 1 || investType == 2) {
						if(activityCommonService.isFirstInvestSuccess(userId, loanRecordId)) {
							double rewardMoney = 0;
							if(investMoney >= 10000) {
								rewardMoney = 60;
							} else if(investMoney >= 2000) {
								rewardMoney = 30;
							} else if(investMoney >= 500) {
								rewardMoney = 10;
							}
							if(rewardMoney > 0) {
								activityCommonService.saveAcivityMoney(userId, phone, rewardMoney, 14, 
										investMoney, loanRecordId, 0, loanId, loanName);
							}
						}
					}
					RedisHelper.setWithExpireTime(key, "1", 90 * 24 * 60 * 60);
				} else {}
			}
		} catch(Exception e) {
			LOG.error("首投送现金活动新增用户活动期间现金奖励报错！", e);
		}
	}
	
	/** 用户是否有参与首投回馈机会 */
	public static boolean isUserHasFirstFeedBackChance(long userId) {
		boolean chanceFlag = false;
		if(isFirstInvestActivity(new Date())) {
			String key = "STR:HC9:FIRST:INVEST:GIVE:MONEY:" + userId;
			if(!RedisHelper.isKeyExist(key)) {
				chanceFlag = true;
			}
		}
		return chanceFlag;
	}
	
	/** 获取平台首投送现金活动016年5月3日-2016年6月30日活动开始时间 */
	public static String getFirstInvestActivityBeginDate() {
		String beginDateKey = "STR:HC9:FIRST:INVEST:GIVE:MONEY:BEGIN:DATE";
		String beginDate = "";
		try {
			beginDate = RedisHelper.get(beginDateKey);
			if(StringUtil.isBlank(beginDate)) {
				beginDate = "2016-05-03 00:00:00";
				RedisHelper.set(beginDateKey, beginDate);
			}
		} catch(Exception e) {
			LOG.error("获取平台首投送现金活动开始时间报错,使用默认开始时间2016-05-03 00:00:00！", e);
			beginDate = "2016-05-03 00:00:00";
		}
		return beginDate;
	}
	
	/** 获取平台首投送现金活动2016年5月3日-2016年6月30日活动结束时间 */
	public static String getFirstInvestActivityEndDate() {
		String endDateKey = "STR:HC9:FIRST:INVEST:GIVE:MONEY:END:DATE";
		String endDate = "";
		try {
			endDate = RedisHelper.get(endDateKey);
			if(StringUtil.isBlank(endDate)) {
				endDate = "2016-06-30 23:59:59";
				RedisHelper.set(endDateKey, endDate);
			}
		} catch(Exception e) {
			LOG.error("获取首投送现金活动的结束时间报错,使用默认结束时间2016-06-30 23:59:59！", e);
			endDate = "2016-06-30 23:59:59";
		}
		return endDate;
	}
	
	/** 判断是否是平台首投送现金活动 2016年5月3日-2016年6月30日活动期间 */
	public static boolean isFirstInvestActivity(Date currentDate) {
		boolean result = true;
		/** 活动开始时间 */
		String beginDateStr = getFirstInvestActivityBeginDate();
		
		/** 活动结束时间 */
		String endDateStr = getFirstInvestActivityEndDate();

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