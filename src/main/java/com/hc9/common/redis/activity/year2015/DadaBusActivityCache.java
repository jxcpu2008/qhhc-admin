package com.hc9.common.redis.activity.year2015;

import java.util.Date;

import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.jubaopen.commons.LOG;

/** 嗒嗒巴士抽奖活动相关缓存 */
public class DadaBusActivityCache {
	/** 获取嗒嗒巴士免费送票活动 2015年12月16日-2016年2月25日活动开始时间 */
	public static String getDadaBusFreeticketBeginDate() {
		String beginDateKey = "STR:HC9:DADABUS:FREE:TICKET:BEGIN:DATE";
		String beginDate = "";
		try {
			beginDate = RedisHelper.get(beginDateKey);
			if(StringUtil.isBlank(beginDate)) {
				beginDate = "2015-12-16 00:00:00";
				RedisHelper.set(beginDateKey, beginDate);
			}
		} catch(Exception e) {
			LOG.error("获取嗒嗒巴士免费送票活动开始时间报错,使用默认开始时间2015-12-16 00:00:00！", e);
			beginDate = "2015-12-16 00:00:00";
		}
		return beginDate;
	}
	
	/** 获取嗒嗒巴士免费送票活动 2015年12月16日-2016年2月25日活动结束时间 */
	public static String getDadaBusFreeticketEndDate() {
		String endDateKey = "STR:HC9:DADABUS:FREE:TICKET:END:DATE";
		String endDate = "";
		try {
			endDate = RedisHelper.get(endDateKey);
			if(StringUtil.isBlank(endDate)) {
				endDate = "2016-02-25 23:59:59";
				RedisHelper.set(endDateKey, endDate);
			}
		} catch(Exception e) {
			LOG.error("获取嗒嗒巴士免费送票活动的结束时间报错,使用默认结束时间2016-02-25 23:59:59！", e);
			endDate = "2016-02-25 23:59:59";
		}
		return endDate;
	}
	
	/** 判断是否是嗒嗒巴士免费送车票活动 2015年12月16日-2016年2月25日活动期间 */
	public static boolean isDadaBusCashActivity(Date currentDate) {
		boolean result = true;
		/** 活动开始时间 */
		String beginDateStr = getDadaBusFreeticketBeginDate();
		
		/** 活动结束时间 */
		String endDateStr = getDadaBusFreeticketEndDate();

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