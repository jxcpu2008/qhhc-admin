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

/** 双亲感恩大回馈 */
public class HcParentCache {
	/** 周周惊喜大放活动累计投资人的投资金额
	 * 	@param loanType 标类型：1-店铺  2-项目 3-天标 4-债权转让
	 *  @param remonth 回购期限:如果是天标的话为天数，项目的话为月数
	 *  @param investType 投资类型：投资类型 1 优先 2 夹层 3劣后
	 *  @param investMoney 投资金额
	 */
	public static void increaseParentInvestMoneyOfUser(long userId, int loanType, int remonth, int investType, 
			double investMoney, String phone) {
		try {
			Date currentDate = new Date();
			if(isParentActivity(currentDate) >= 0) {
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
					/** 月度投资累计金额 */
					String monthKey = "STR:HC9:PARENET:FEEDBACK:TOTAL:MONEY:" + userId;
					long monthMoney = RedisHelper.incrBy(monthKey, 
							new BigDecimal(investMoney).longValue());
					
					/** 月度累计年化金额 */
					String monthYearKey = "STR:HC9:PARENET:FEEDBACK:TOTAL:YEAR:MONEY:" + userId;
					long monthYearMoney = RedisHelper.incrBy(monthYearKey, totalYearInvestMoney);
					double finalMonthYearMoney = Arith.div(monthYearMoney, 100).doubleValue();
					saveUserInfoToParentRankList(userId, monthMoney, finalMonthYearMoney, phone);
				}
			}
		} catch(Exception e) {
			LOG.error("周周惊喜大放活动新增用户活动期间累计投资报错！", e);
		}
	}
	
	/** 获取用户的累计投资金额 */
	public static long getMonthInvestMoneyOfUser(long userId) {
		/** 月度投资累计金额 */
		String monthKey = "STR:HC9:PARENET:FEEDBACK:TOTAL:MONEY:" + userId;
		String monthMoney = RedisHelper.get(monthKey);
		if(StringUtil.isNotBlank(monthMoney)) {
			return Long.valueOf(monthMoney).longValue();
		} else {
			return 0;
		}
	}
	
	/** 获取用户的累计年化投资金额 */
	public static double getMonthYearInvestMoneyOfUser(long userId) {
		/** 月度累计年化金额 */
		String monthYearKey = "STR:HC9:PARENET:FEEDBACK:TOTAL:YEAR:MONEY:" + userId;
		String monthYearMoney = RedisHelper.get(monthYearKey);
		if(StringUtil.isNotBlank(monthYearMoney)) {
			double resultYearMoney = new BigDecimal(monthYearMoney).doubleValue();
			return Arith.div(resultYearMoney, 100).doubleValue();
		} else {
			return 0;
		}
	}
	
	/** 将用户投资信息保存至周榜中 */
	private static void saveUserInfoToParentRankList(long userId, long monthMoney, 
			double monthYearMoney, String phone) {
		WeekVo weekVo = new WeekVo();
		weekVo.setUserId(userId);
		weekVo.setPhone(phone);
		weekVo.setWeekMoney(monthMoney);
		weekVo.setWeekYearMoney(monthYearMoney);
		String key = "STR:HC9:PARENET:FEEDBACK:RANKLIST";
		String json = "";
		List<WeekVo> list = getParentFeedBackRankList();
		if(list != null && list.size() > 0) {
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
					if(vo.getWeekYearMoney() < monthYearMoney) {
						list.add(i, weekVo);
						addFlag = true;
						break;
					}
				}
				if(!addFlag) {
					list.add(weekVo);
				}
				List<WeekVo> finalList = new ArrayList<WeekVo>();
				if(list.size() > 3) {
					for(int i = 0; i < 3; i++) {
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
	
	/** 双亲反馈活动排行榜 */
	public static List<WeekVo> getParentFeedBackRankList() {
		String key = "STR:HC9:PARENET:FEEDBACK:RANKLIST";
		List<WeekVo> list = new ArrayList<WeekVo>();
		String str = RedisHelper.get(key);
		if(StringUtil.isNotBlank(str)) {
			list = JsonUtil.jsonToList(str, WeekVo.class);
		}
		return list;
	}
	
	/** 获取双亲感恩大回馈活动016年5月3日-2016年6月30日活动开始时间 */
	public static String getParentActivityBeginDate() {
		String beginDateKey = "STR:HC9:PARENET:FEEDBACK:BEGIN:DATE";
		String beginDate = "";
		try {
			beginDate = RedisHelper.get(beginDateKey);
			if(StringUtil.isBlank(beginDate)) {
				beginDate = "2016-05-03 00:00:00";
				RedisHelper.set(beginDateKey, beginDate);
			}
		} catch(Exception e) {
			LOG.error("获取双亲感恩大回馈活动开始时间报错,使用默认开始时间2016-05-03 00:00:00！", e);
			beginDate = "2016-05-03 00:00:00";
		}
		return beginDate;
	}
	
	/** 获取双亲感恩大回馈活动2016年5月3日-2016年6月30日活动结束时间 */
	public static String getParentActivityEndDate() {
		String endDateKey = "STR:HC9:PARENET:FEEDBACK:END:DATE";
		String endDate = "";
		try {
			endDate = RedisHelper.get(endDateKey);
			if(StringUtil.isBlank(endDate)) {
				endDate = "2016-06-30 23:59:59";
				RedisHelper.set(endDateKey, endDate);
			}
		} catch(Exception e) {
			LOG.error("获取双亲感恩大回馈活动的结束时间报错,使用默认结束时间2016-06-30 23:59:59！", e);
			endDate = "2016-06-30 23:59:59";
		}
		return endDate;
	}
	
	/** 判断是否是双亲感恩大回馈活动 2016年5月3日-2016年6月30日活动期间 */
	public static int isParentActivity(Date currentDate) {
		int result = 0;
		/** 活动开始时间 */
		String beginDateStr = getParentActivityBeginDate();
		
		/** 活动结束时间 */
		String endDateStr = getParentActivityEndDate();

		Date beginDate = DateFormatUtil.stringToDate(beginDateStr, "yyyy-MM-dd HH:mm:ss");
		Date endDate = DateFormatUtil.stringToDate(endDateStr, "yyyy-MM-dd HH:mm:ss");
		
		/** 当前时间早于活动开始时间 */
		if(currentDate.before(beginDate)) {
			result = -1;
		}
		
		if(endDate.before(currentDate)) {
			result = -2;
		}
		return result;
	}
}
