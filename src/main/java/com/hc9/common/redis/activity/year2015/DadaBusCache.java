package com.hc9.common.redis.activity.year2015;

import java.util.Date;

import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.jubaopen.commons.LOG;

/** 嗒嗒巴士代金券活动相关缓存 */
public class DadaBusCache {
	
	/** 记录用户活动期间首投的投资记录id */
	public static void setDadaBusCashLoanRecordId(Long userId, Long loanRecordId) {
		String key = "STR:HC9:DADABUS:CASH:FIRST:LOAN:RECORD:ID:" + userId;
		RedisHelper.set(key, "" + loanRecordId);
	}
	
	/** 判断用户是否已经存在过首笔投资记录 */
	public static boolean existFirstLoanRecord(Long userId) {
		String key = "STR:HC9:DADABUS:CASH:FIRST:LOAN:RECORD:ID:" + userId;
		return RedisHelper.isKeyExist(key);
	}
	
	/** 获取嗒嗒巴士代金券活动 2015年12月15日-2016年2月15日活动开始时间 */
	public static String getDadaBusCashBeginDate() {
		String beginDateKey = "STR:HC9:DADABUS:CASH:BEGIN:DATE";
		String beginDate = "";
		try {
			beginDate = RedisHelper.get(beginDateKey);
			if(StringUtil.isBlank(beginDate)) {
				beginDate = "2015-12-29 00:00:00";
				RedisHelper.set(beginDateKey, beginDate);
			}
		} catch(Exception e) {
			LOG.error("获取嗒嗒巴士代金券活动开始时间报错,使用默认开始时间2015-12-29 00:00:00！", e);
			beginDate = "2015-12-29 00:00:00";
		}
		return beginDate;
	}
	
	/** 获取嗒嗒巴士代金券活动 2015年12月15日-2016年2月15日活动结束时间 */
	public static String getDadaBusCashEndDate() {
		String endDateKey = "STR:HC9:DADABUS:CASH:END:DATE";
		String endDate = "";
		try {
			endDate = RedisHelper.get(endDateKey);
			if(StringUtil.isBlank(endDate)) {
				endDate = "2016-02-29 23:59:59";
				RedisHelper.set(endDateKey, endDate);
			}
		} catch(Exception e) {
			LOG.error("获取嗒嗒巴士代金券活动的结束时间报错,使用默认结束时间2016-02-29 23:59:59！", e);
			endDate = "2016-02-29 23:59:59";
		}
		return endDate;
	}
	
	/** 判断是否是嗒嗒巴士代金券活动 2015年12月15日-2016年2月15日活动期间 */
	public static boolean isDadaBusCashActivity(Date currentDate) {
		boolean result = true;
		/** 活动开始时间 */
		String beginDateStr = getDadaBusCashBeginDate();
		
		/** 活动结束时间 */
		String endDateStr = getDadaBusCashEndDate();

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
