package com.hc9.common.redis.activity.year2016.month04;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;
import com.hc9.model.OpenCardVo;

/** 四月翻牌活动相关缓存 */
public class HcOpenCardActivityCache {
	
	/** 用户投资：新增用户活动期间当天的累计抽奖信息
	 *  @param userId 投资用户id
	 *  @param investType 投资类型：投资类型 1 优先 2 夹层 3劣后
	 *  @param isDetail 是否使用红包 
	 *  @param isCardId 是否使用加息券
	 *  @param investMoney 投资金额
	 *  @param loanReacordId 投资记录id
	 *  
	 *  */
	public static void incrOpenCardLotteryNum(long userId, int investType, boolean isDetail, 
			boolean isCardId, double investMoney, long loanReacordId) {
		try {
			if(isOpenCardActivity(new Date())) {
				/**
				 * 投资优先，夹层都可以参与，劣后不参与，如果用户使用红包，加息等优惠，将不参与此次活动
				 * */
				Date date = new Date();
				String currentDate = DateFormatUtil.dateToString(date, "yyyy-MM-dd");
				/** 当天中奖次数 */
				String totalLotteryNumKey = "STR:HC9:OPEN:CARD:TOTAL:NUM:" + userId + ":" + currentDate;
				String totalLotteryNum = RedisHelper.get(totalLotteryNumKey);
				long totalNum = 0;
				if(StringUtil.isNotBlank(totalLotteryNum)) {
					totalNum = Long.valueOf(totalLotteryNum);
				}
				if(totalNum < 5) {
					boolean giveFlag = true;
					if(investType == 1 || investType == 2) {
						String moneyKey = "STR:HC9:OPEN:CARD:FIFTH:RECORD:MONEY:" + userId;
						String midTenderMoney = RedisHelper.get(moneyKey);
						
						String midIdKey = "STR:HC9:OPEN:CARD:FIFTH:MID:RECORD:ID:" + userId;
						String midLoanRecordId = RedisHelper.get(midIdKey);
						if(!isDetail && !isCardId) {
							if(totalNum == 4) {
								if(StringUtil.isNotBlank(midTenderMoney) && 
										StringUtil.isNotBlank(midLoanRecordId)) {
									if(loanReacordId == Long.valueOf(midLoanRecordId).longValue()) {
										investMoney = Double.valueOf(midTenderMoney);
									} else {
										giveFlag = false;
									}
								}
							}
							if(giveFlag) {
								String investListKey = "STR:HC9:OPEN:CARD:INVEST:LIST:" + userId + ":" + currentDate;
								String json = RedisHelper.get(investListKey);
								List<OpenCardVo> investList = new ArrayList<OpenCardVo>();
								if(StringUtil.isNotBlank(json)) {
									investList = JsonUtil.jsonToList(json, OpenCardVo.class);
									if(investList.size() < 5) {
										OpenCardVo vo = new OpenCardVo();
										vo.setInvestMoney(investMoney);
										vo.setUseFlag(0);
										investList.add(vo);
									}
								} else {
									OpenCardVo vo = new OpenCardVo();
									vo.setInvestMoney(investMoney);
									vo.setUseFlag(0);
									investList.add(vo);
								}
								json = JsonUtil.toJsonStr(investList);
								RedisHelper.set(investListKey, json);
							}
						} else {
							if(totalNum == 4) {
								if(StringUtil.isNotBlank(midTenderMoney) && 
										StringUtil.isNotBlank(midLoanRecordId)) {
									giveFlag = false;
								}
							}
						}
					}
					if(giveFlag) {
						RedisHelper.incrBy(totalLotteryNumKey, 1);
					}
				}
			}
		} catch(Exception e) {
			LOG.error("翻牌抽奖活动新增用户当天翻牌抽奖次数报错！", e);
		}
	}
	
	/** 获取用户当天已经占用多少次数 */
	public static long getTotalInvestNum(long userId) {
		Date date = new Date();
		String currentDate = DateFormatUtil.dateToString(date, "yyyy-MM-dd");
		/** 当天中奖次数 */
		String totalLotteryNumKey = "STR:HC9:OPEN:CARD:TOTAL:NUM:" + userId + ":" + currentDate;
		String totalLotteryNum = RedisHelper.get(totalLotteryNumKey);
		if(totalLotteryNum != null && totalLotteryNum.trim().length() > 0) {
			return Long.valueOf(totalLotteryNum);
		} else {
			return 0;
		}
	}
	
	/** 获取用户前五次投资记录信息 */
	public static List<OpenCardVo> getOpenCardInvestList(long userId) {
		List<OpenCardVo> investList = new ArrayList<OpenCardVo>();
		try {
			Date date = new Date();
			String currentDate = DateFormatUtil.dateToString(date, "yyyy-MM-dd");
			String investListKey = "STR:HC9:OPEN:CARD:INVEST:LIST:" + userId + ":" + currentDate;
			String json = RedisHelper.get(investListKey);
			if(StringUtil.isNotBlank(json)) {
				investList = JsonUtil.jsonToList(json, OpenCardVo.class);
			}
		} catch(Exception e) {
			LOG.error("获取翻牌抽奖活动前五次投资列表报错！", e);
		}
		return investList;
	}
	
	/** 更新前五次投资记录信息列表 */
	public static void updateOpenCardInvestList(long userId, List<OpenCardVo> investList) {
		try {
			String json = JsonUtil.toJsonStr(investList);
			Date date = new Date();
			String currentDate = DateFormatUtil.dateToString(date, "yyyy-MM-dd");
			String investListKey = "STR:HC9:OPEN:CARD:INVEST:LIST:" + userId + ":" + currentDate;
			RedisHelper.set(investListKey, json);
		} catch(Exception e) {
			LOG.error("更新翻牌抽奖活动前五次投资列表报错！", e);
		}
	}
	
	/** 获取用户当天最大翻牌抽奖次数 */
	public static long getTotalOpenCardNUm(long userId) {
		long totalNum = 0;
		try {
			if(isOpenCardActivity(new Date())) {
				/**
				 * 奖励区间的金额为活动期间的累计金额，累计金额的计算公式为：
				 * 投资金额*月份数（如： 投资10000元三个月的标的， 则累计投资金额为10000元*3=30000元）
				 * 投资优先与夹层都算进当月投资累计额
				 * */
				Date date = new Date();
				String currentDate = DateFormatUtil.dateToString(date, "yyyy-MM-dd");
				String totalLotteryNumKey = "STR:HC9:OPEN:CARD:TOTAL:NUM:" + userId + ":" + currentDate;
				String totalLotteryNum = RedisHelper.get(totalLotteryNumKey);
				if(StringUtil.isNotBlank(totalLotteryNum)) {
					totalNum = Long.valueOf(totalLotteryNum);
				}
				/** 已使用过多少次 */
				String totalUsedLotteryNumKey = "STR:HC9:OPEN:CARD:USED:TOTAL:NUM:" + userId + ":" 
				+ currentDate;
				String totalUsedLotteryNum = RedisHelper.get(totalUsedLotteryNumKey);
				long totalUsed = 0;
				if(StringUtil.isNotBlank(totalUsedLotteryNum)) {
					totalUsed = Long.valueOf(totalUsedLotteryNum);
				}
				totalNum = totalNum - totalUsed;
				if(totalNum < 0) {
					totalNum = 0;
				}
			}
		} catch(Exception e) {
			LOG.error("翻牌抽奖活动新增用户活动期间累计投资报错！", e);
		}
		return totalNum;
	}

	/** 记录用户翻牌抽奖次数 */
	public static void increaseUsedOpenCardLotterNum(long userId) {
		Date date = new Date();
		String currentDate = DateFormatUtil.dateToString(date, "yyyy-MM-dd");
		/** 已使用过多少次 */
		String totalUsedLotteryNumKey = "STR:HC9:OPEN:CARD:USED:TOTAL:NUM:" + userId + ":" 
				+ currentDate;
		RedisHelper.incrBy(totalUsedLotteryNumKey, 1);
	}
	
	/** 一级抽奖 */
	public static long oneLevelLottery() {
		String oneLevelLotteryKey = "STR:HC9:OPEN:CARD:ONE:LEVEL:LOTTERY";
		return lotteryLogic(oneLevelLotteryKey);
	}
	
	/** 二级抽奖 */
	public static long twoLevelLottery() {
		String twoLevelLotteryKey = "STR:HC9:OPEN:CARD:TWO:LEVEL:LOTTERY";
		return lotteryLogic(twoLevelLotteryKey);
	}
	
	/** 三级抽奖 */
	public static long threeLevelLottery() {
		String threeLevelLotteryKey = "STR:HC9:OPEN:CARD:THREE:LEVEL:LOTTERY";
		return lotteryLogic(threeLevelLotteryKey);
	}
	
	/** 四级抽奖 */
	public static long fourLevelLottery() {
		String fourLevelLotteryKey = "STR:HC9:OPEN:CARD:FOUR:LEVEL:LOTTERY";
		long[] resultArr = {
				1, 2, 2, 3, 2, 2, 1, 2, 1, 3,
				2, 1, 2, 3, 2, 2, 1, 3, 2, 1,
				3, 1, 2, 2, 3, 2, 1, 2, 2, 1,
				2, 1, 2, 2, 3, 1, 2, 1, 2, 3,
				3, 2, 1, 2, 2, 1, 2, 1, 3, 2,
				1, 2, 3, 1, 2, 2, 2, 2, 1, 3,
				2, 3, 2, 1, 2, 2, 1, 2, 3, 1,
				2, 1, 2, 1, 3, 2, 3, 2, 1, 2,
				1, 3, 1, 2, 2, 2, 1, 2, 3, 2,
				2, 1, 2, 3, 1, 2, 3, 2, 1, 2
		};
		if(!RedisHelper.isKeyExist(fourLevelLotteryKey)) {
			long randam = ThreadLocalRandom.current().nextLong(0, 3) * 10 - 1;
			RedisHelper.set(fourLevelLotteryKey, "" + randam);
		}
		long result = RedisHelper.incrBy(fourLevelLotteryKey, 1);
		int length = resultArr.length;
		if(result > length - 1) {
			result = ThreadLocalRandom.current().nextLong(0, 6) * 10;
			RedisHelper.set(fourLevelLotteryKey, "" + result);
		}
		int index = Integer.valueOf("" + result);
		return resultArr[index];
	}
	
	/** 抽奖最终逻辑 */
	private static long lotteryLogic(String key) {
		long[] resultArr = {
				1, 2, 3, 1, 3, 2, 
				2, 1, 3, 2, 3, 1,
				3, 1, 2, 3, 2, 1,
				1, 2, 3, 1, 3, 2, 
				3, 1, 2, 3, 2, 1,
				2, 1, 3, 2, 3, 1,
				1, 2, 3, 1, 3, 2,
				3, 1, 2, 3, 2, 1,
				2, 3, 1, 2, 1, 3, 
				3, 2, 1, 3, 1, 2,
				1, 2, 3, 1, 3, 2, 
				2, 1, 3, 2, 3, 1
		};
		if(!RedisHelper.isKeyExist(key)) {
			long randam = ThreadLocalRandom.current().nextLong(0, 3) * 6 -1;
			RedisHelper.set(key, "" + randam);
		}
		long result = RedisHelper.incrBy(key, 1);
		int length = resultArr.length;
		if(result > length - 1) {
			result = ThreadLocalRandom.current().nextLong(0, 6) * 6;
			RedisHelper.set(key, "" + result);
		}
		int index = Integer.valueOf("" + result);
		return resultArr[index];
	}
	
	/** 获取翻牌抽奖活动 2016年4月1日-2016年4月30日活动开始时间 */
	public static String getOpenCardActivityBeginDate() {
		String beginDateKey = "STR:HC9:OPEN:CARD:BEGIN:DATE";
		String beginDate = "";
		try {
			beginDate = RedisHelper.get(beginDateKey);
			if(StringUtil.isBlank(beginDate)) {
				beginDate = "2016-04-01 00:00:00";
				RedisHelper.set(beginDateKey, beginDate);
			}
		} catch(Exception e) {
			LOG.error("获取翻牌抽奖活动开始时间报错,使用默认开始时间2016-04-01 00:00:00！", e);
			beginDate = "2016-04-01 00:00:00";
		}
		return beginDate;
	}
	
	/** 获取翻牌抽奖活动 2016年4月1日-2016年5月2日活动结束时间 */
	public static String getOpenCardActivityEndDate() {
		String endDateKey = "STR:HC9:OPEN:CARD:END:DATE";
		String endDate = "";
		try {
			endDate = RedisHelper.get(endDateKey);
			if(StringUtil.isBlank(endDate)) {
				endDate = "2016-05-02 23:59:59";
				RedisHelper.set(endDateKey, endDate);
			}
		} catch(Exception e) {
			LOG.error("获取翻牌抽奖活动的结束时间报错,使用默认结束时间2016-04-30 23:59:59！", e);
			endDate = "2016-04-30 23:59:59";
		}
		return endDate;
	}
	
	/** 判断是否是翻牌抽奖活动 2016年4月1日-2016年4月30日活动期间 */
	public static boolean isOpenCardActivity(Date currentDate) {
		boolean result = true;
		/** 活动开始时间 */
		String beginDateStr = getOpenCardActivityBeginDate();
		
		/** 活动结束时间 */
		String endDateStr = getOpenCardActivityEndDate();

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