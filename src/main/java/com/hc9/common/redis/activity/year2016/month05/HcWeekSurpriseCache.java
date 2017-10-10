package com.hc9.common.redis.activity.year2016.month05;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.redis.Arith;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;

/** 周周惊喜大放送 */
public class HcWeekSurpriseCache {
	
	/** 周周惊喜大放活动累计投资人的投资金额
	 * 	@param loanType 标类型：1-店铺  2-项目 3-天标 4-债权转让
	 *  @param remonth 回购期限:如果是天标的话为天数，项目的话为月数
	 *  @param investType 投资类型：投资类型 1 优先 2 夹层 3劣后
	 *  @param investMoney 投资金额
	 */
	public static void increaseWeekInvestMoneyOfUser(long userId, int loanType, int remonth, int investType, 
			double investMoney, String phone) {
		try {
			Date currentDate = new Date();
			if(isWeekSurpriseActivity(currentDate)) {
				int weekNum = getWeekSurpriseWeekNum(currentDate);
				if(weekNum > 0) {
					if(investType == 1 || investType == 2) {
						/** 年化金额计算方式为：月标：投资金额/12*投资月份数；
						 * 天标：投资金额/360*投资天数 */
						double yearMoney = 0;
						double totalMoney = Arith.mul(investMoney, remonth).doubleValue();
						if(loanType == 3) {
							yearMoney = Arith.div(totalMoney, 360, 2).doubleValue();
						} else {
							yearMoney = Arith.div(totalMoney, 12, 2).doubleValue();
						}
						long totalYearInvestMoney = Arith.mul(yearMoney, 100).longValue();
						/** 周投资累计金额 */
						String weekKey = "STR:HC9:WEEK:SURPRISE:TOTAL:MONEY:" 
								+ weekNum + ":" + userId;
						long weekMoney = RedisHelper.incrBy(weekKey, 
								new BigDecimal(investMoney).longValue());
						
						/** 周累计年化金额 */
						String weekYearKey = "STR:HC9:WEEK:SURPRISE:TOTAL:YEAR:MONEY:" 
								+ weekNum + ":" + userId;
						long weekYearMoney = RedisHelper.incrBy(weekYearKey, totalYearInvestMoney);
						double finalWeekYearMoney = Arith.div(weekYearMoney, 100).doubleValue();
						saveUserInfoToWeekRankList(userId, weekMoney, finalWeekYearMoney, weekNum, phone);
					}
				}
			}
		} catch(Exception e) {
			LOG.error("周周惊喜大放活动新增用户活动期间累计投资报错！", e);
		}
	}
	
	/** 获取用户指定周数的累计投资金额 */
	public static long getWeekInvestMoneyOfUser(long userId, int weekNum) {
		/** 周投资累计金额 */
		String weekKey = "STR:HC9:WEEK:SURPRISE:TOTAL:MONEY:" 
				+ weekNum + ":" + userId;
		String weekMoney = RedisHelper.get(weekKey);
		if(StringUtil.isNotBlank(weekMoney)) {
			return Long.valueOf(weekMoney).longValue();
		} else {
			return 0;
		}
	}
	
	/** 获取用户指定周数的累计年化投资金额 */
	public static double getWeekYearInvestMoneyOfUser(long userId, int weekNum) {
		/** 周累计年化金额 */
		String weekYearKey = "STR:HC9:WEEK:SURPRISE:TOTAL:YEAR:MONEY:" 
				+ weekNum + ":" + userId;
		String weekYearMoney = RedisHelper.get(weekYearKey);
		if(StringUtil.isNotBlank(weekYearMoney)) {
			double resultYearMoney = new BigDecimal(weekYearMoney).doubleValue();
			return Arith.div(resultYearMoney, 100).doubleValue();
		} else {
			return 0;
		}
	}
	
	/** 将用户投资信息保存至周榜中 */
	private static void saveUserInfoToWeekRankList(long userId, long weekMoney, 
			double weekYearMoney, int weekNum, String phone) {
		WeekVo weekVo = new WeekVo();
		weekVo.setUserId(userId);
		weekVo.setPhone(phone);
		weekVo.setWeekMoney(weekMoney);
		weekVo.setWeekYearMoney(weekYearMoney);
		String key = "STR:HC9:WEEK:SURPRISE:RANKLIST:" + weekNum;
		String json = "";
		List<WeekVo> list = getWeekRankList(weekNum);
		if(list != null && list.size() > 0) {
			/** 用于标识在redis中是否已存在：true标识不存在 */
			boolean flag = true;
			for(WeekVo vo : list ) {
				if(userId == vo.getUserId()) {
					flag = false;
					vo.setWeekMoney(weekVo.getWeekMoney());
					vo.setWeekYearMoney(weekVo.getWeekYearMoney());
					break;
				}
			}
			if(flag) {
				boolean addFlag = false;
				for(int i = 0; i < list.size(); i++) {
					WeekVo vo = list.get(i);
					if(vo.getWeekYearMoney() < weekYearMoney) {
						list.add(i, weekVo);
						addFlag = true;
						break;
					}
				}
				if(!addFlag) {
					list.add(weekVo);
				}
				List<WeekVo> finalList = new ArrayList<WeekVo>();
				if(list.size() > 10) {
					for(int i = 0; i < 10; i++) {
						finalList.add(list.get(i));
					}
				} else {
					finalList = list;
				}
				json = JsonUtil.toJsonStr(finalList);
			} else {
				List<WeekVo> finalList = new ArrayList<WeekVo>();
				for(WeekVo vo : list) {
					if(finalList.size() < 1) {
						finalList.add(vo);
					} else {
						boolean sortFlag = true;
						for(int i = 0; i < finalList.size(); i++) {
							WeekVo finalVo = finalList.get(i);
							if(finalVo.getWeekYearMoney() < vo.getWeekYearMoney()) {
								finalList.add(i, vo);
								sortFlag = false;
								break;
							}
						}
						if(sortFlag) {
							finalList.add(vo);
						}
					}
				}
				json = JsonUtil.toJsonStr(finalList);
			}
		} else {
			list.add(weekVo);
			json = JsonUtil.toJsonStr(list);
		}
		RedisHelper.set(key, json);
	}
	
	/** 周周有惊喜排行榜 */
	public static List<WeekVo> getWeekRankList(int weekNum) {
		String key = "STR:HC9:WEEK:SURPRISE:RANKLIST:" + weekNum;
		List<WeekVo> list = new ArrayList<WeekVo>();
		String str = RedisHelper.get(key);
		if(StringUtil.isNotBlank(str)) {
			list = JsonUtil.jsonToList(str, WeekVo.class);
		}
		return list;
	}
	
	/** 判断当前是周周惊喜大放送的第几周 */
	public static int getWeekSurpriseWeekNum(Date currentDate){
		Date beginDate8 = DateFormatUtil.stringToDate("2016-06-20 23:59:59", "yyyy-MM-dd HH:mm:ss");
		Date beginDate7 = DateFormatUtil.stringToDate("2016-06-13 23:59:59", "yyyy-MM-dd HH:mm:ss");
		Date beginDate6 = DateFormatUtil.stringToDate("2016-06-06 23:59:59", "yyyy-MM-dd HH:mm:ss");
		Date beginDate5 = DateFormatUtil.stringToDate("2016-05-30 23:59:59", "yyyy-MM-dd HH:mm:ss");
		Date beginDate4 = DateFormatUtil.stringToDate("2016-05-23 23:59:59", "yyyy-MM-dd HH:mm:ss");
		Date beginDate3 = DateFormatUtil.stringToDate("2016-05-16 23:59:59", "yyyy-MM-dd HH:mm:ss");
		Date beginDate2 = DateFormatUtil.stringToDate("2016-05-09 23:59:59", "yyyy-MM-dd HH:mm:ss");
		Date beginDate1 = DateFormatUtil.stringToDate("2016-05-02 23:59:59", "yyyy-MM-dd HH:mm:ss");
		
		if(beginDate8.before(currentDate)){
			return 8;
		} else if(beginDate7.before(currentDate)){
			return 6;
		} else if(beginDate6.before(currentDate)){
			return 6;
		} else if(beginDate5.before(currentDate)){
			return 5;
		} else if(beginDate4.before(currentDate)){
			return 4;
		} else if(beginDate3.before(currentDate)){
			return 3;
		} else if(beginDate2.before(currentDate)){
			return 2;
		} else if(beginDate1.before(currentDate)){
			return 1;
		} else {
			return 0;
		}
	}

	/** 获取周周惊喜大放送活动016年5月3日-2016年6月27日活动开始时间 */
	public static String getWeekSurpriseActivityBeginDate() {
		String beginDateKey = "STR:HC9:WEEK:SURPRISE:BEGIN:DATE";
		String beginDate = "";
		try {
			beginDate = RedisHelper.get(beginDateKey);
			if(StringUtil.isBlank(beginDate)) {
				beginDate = "2016-05-03 00:00:00";
				RedisHelper.set(beginDateKey, beginDate);
			}
		} catch(Exception e) {
			LOG.error("获取周周惊喜大放送活动开始时间报错,使用默认开始时间2016-05-03 00:00:00！", e);
			beginDate = "2016-05-03 00:00:00";
		}
		return beginDate;
	}
	
	/** 获取周周惊喜大放送活动2016年5月3日-2016年6月27日活动结束时间 */
	public static String getWeekSurpriseActivityEndDate() {
		String endDateKey = "STR:HC9:WEEK:SURPRISE:END:DATE";
		String endDate = "";
		try {
			endDate = RedisHelper.get(endDateKey);
			if(StringUtil.isBlank(endDate)) {
				endDate = "2016-06-27 23:59:59";
				RedisHelper.set(endDateKey, endDate);
			}
		} catch(Exception e) {
			LOG.error("获取周周惊喜大放送活动的结束时间报错,使用默认结束时间2016-06-27 23:59:59！", e);
			endDate = "2016-06-27 23:59:59";
		}
		return endDate;
	}
	
	/** 判断是否是平台首投送现金活动 2016年5月3日-2016年6月27日活动期间 */
	public static boolean isWeekSurpriseActivity(Date currentDate) {
		boolean result = true;
		/** 活动开始时间 */
		String beginDateStr = getWeekSurpriseActivityBeginDate();
		
		/** 活动结束时间 */
		String endDateStr = getWeekSurpriseActivityEndDate();

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
