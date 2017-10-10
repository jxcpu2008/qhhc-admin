package com.hc9.common.redis;

import java.util.Date;

import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.jubaopen.commons.LOG;

/** 七夕活动相关缓存
 * 2015年8月20日 10:00-2015年8月28日18:00活动期间
 *  */
public class QiXiActivitiCache {
	
	/** 赠送投票 机会
	 * @param userId 被赠送人主键id
	 * @param chanceNum 赠送次数*/
	public static void increaseVoteChance(long userId, long chanceNum) {
		if(isQiXiActivity(new Date())) {
			String key = "INT:HC9:GIVE:QIXI:VOTE:NUM:" + userId;
			RedisHelper.incrBy(key, chanceNum);
		}
	}
	
	/** 获取用户赠送的投票次数 */
	public static int getGrantVoteChance(long userId) {
		String key = "INT:HC9:GIVE:QIXI:VOTE:NUM:" + userId;
		String grantNum = RedisHelper.get(key);
		if(StringUtil.isBlank(grantNum)) {
			grantNum = "0";
		}
		return Integer.valueOf(grantNum);
	}
	
	/** 减少赠送的投票机会 */
	public static void decreaseVoteChance(long userId) {
		String key = "INT:HC9:GIVE:QIXI:VOTE:NUM:" + userId;
		RedisHelper.decrBy(key, 1);
	}
	
	/** 记录用户当天的投票 */
	public static void increaseUserVoteNum(long userId, String currentDate) {
		String userLotteryNumKey = "STR:HC9:USED:QIXI:VOTE:NUM:" + currentDate + ":" + userId;
		RedisHelper.incrBy(userLotteryNumKey, 1);
	}
	
	/** 获取用户当天已使用的投票次数 */
	public static int getUserVoteNumByDate(long userId, String currentDate) {
		String userLotteryNumKey = "STR:HC9:USED:QIXI:VOTE:NUM:" + currentDate + ":" + userId;
		String userLotteryNum = RedisHelper.get(userLotteryNumKey);
		if(StringUtil.isBlank(userLotteryNum)) {
			userLotteryNum = "0";
		}
		return Integer.valueOf(userLotteryNum);
	}
	
	/** 记录用户支持某选手的票数 */
	public static void increaseUserVoteNumOfVotedId(long userId, String votedId) {
		String userLotteryNumKey = "STR:HC9:QIXI:VOTE:NUM:VOTEDID:" + userId + ":" + votedId;
		RedisHelper.incrBy(userLotteryNumKey, 1);
	}
	
	/** 查询用户支持某选手的票数 */
	public static long getUserVoteNumOfVotedId(long userId, String votedId) {
		String voteNumOfVotedIdKey = "STR:HC9:QIXI:VOTE:NUM:VOTEDID:" + userId + ":" + votedId;
		String voteNumOfVotedId = RedisHelper.get(voteNumOfVotedIdKey);
		if(StringUtil.isBlank(voteNumOfVotedId)) {
			voteNumOfVotedId = "0";
		}
		return Long.valueOf(voteNumOfVotedId);
	}
	
	/** 为被投票人投票:新增被投票人票数 */
	public static void increaseVoteNumByVotedId(String votedId) {
		String votedNumKey = "STR:HC9:QIXI:VOTED:NUM:VOTEDID:" + votedId;
		RedisHelper.incrBy(votedNumKey, 1);
	}
	
	/** 重新设置：被投票人票数 */
	public static void setVoteNumByVotedId(String votedId, Long voteNum) {
		String votedNumKey = "STR:HC9:QIXI:VOTED:NUM:VOTEDID:" + votedId;
		RedisHelper.set(votedNumKey, "" + voteNum);
	}
	
	/** 获取被投票人所获得的投票数  */
	public static String getVoteNumByVotedId(String votedId) {
		String key = "STR:HC9:QIXI:VOTED:NUM:VOTEDID:" + votedId;
		String votedNum = RedisHelper.get(key);
		if(StringUtil.isBlank(votedNum)) {
			votedNum = "0";
		}
		return votedNum;
	}
	
	/** 判断是否是2015年8月20日 10:00-2015年8月28日18:00活动期间 */
	public static boolean isQiXiActivity(Date currentDate) {
		boolean result = true;
		/** 活动开始时间 */
		String beginDateStr = getQiXiBeginDate();
		
		/** 活动结束时间 */
		String endDateStr = getQiXiEndDate();

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
	
	/** 判断七夕活动是否结束 */
	public static boolean isQixiActivityEend(Date currentDate) {
		boolean result = false;
		/** 活动结束时间 */
		String endDateStr = getQiXiEndDate();
		Date endDate = DateFormatUtil.stringToDate(endDateStr, "yyyy-MM-dd HH:mm:ss");
		if(endDate.before(currentDate)) {
			result = true;
		}
		return result;
	}
	
	/** 获取2015年8月20日 10:00-2015年8月28日18:00活动开始时间 */
	public static String getQiXiBeginDate() {
		String beginDateKey = "STR:HC9:QIXI:VOTE:BEGIN:DATE";
		String beginDate = "";
		try {
			beginDate = RedisHelper.get(beginDateKey);
			if(StringUtil.isBlank(beginDate)) {
				beginDate = "2015-08-20 10:00:00";
				RedisHelper.set(beginDateKey, beginDate);
			}
		} catch(Exception e) {
			LOG.error("获取七夕投票活动开始时间报错,使用默认开始时间2015-08-20 10:00:00！", e);
			beginDate = "2015-08-20 10:00:00";
		}
		return beginDate;
	}
	
	/** 获取2015年8月20日 10:00-2015年8月28日18:00活动结束时间 */
	public static String getQiXiEndDate() {
		String endDateKey = "STR:HC9:QIXI:VOTE:END:DATE";
		String endDate = "";
		try {
			endDate = RedisHelper.get(endDateKey);
			if(StringUtil.isBlank(endDate)) {
				endDate = "2015-08-28 18:00:00";
				RedisHelper.set(endDateKey, endDate);
			}
		} catch(Exception e) {
			LOG.error("获取七夕活动的结束时间报错,使用默认结束时间2015-08-28 18:00:00！", e);
			endDate = "2015-08-28 18:00:00";
		}
		return endDate;
	}
	
	/** 投资赠送投票机会  */
	public static void giveVoteChanceNumForInvest(long userId, Double investMoney) {
		if(investMoney >= 100) {
			long chanceNum = Arith.div(investMoney, 100.00).longValue();
			increaseVoteChance(userId, chanceNum);
		}
	}
	
	/** 获取用户当天还有多少投票机会 */
	public static int getVoteChanceNumOfUser(long userId) {
		int resultNum = 0;
		if(isQiXiActivity(new Date())) {
			int grantChanceNum = getGrantVoteChance(userId);
			resultNum = grantChanceNum;
			String currentDate = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd");
			int userLotteryNum = getUserVoteNumByDate(userId, currentDate);
			if(userLotteryNum < 3) {
				resultNum = grantChanceNum + 3 - userLotteryNum;
			}
		}
		return resultNum;
	}
	
	/** 设置是否已经计算过七夕活动收益标识 */
	public static void setComputeAndSaveIncomeOfQixiFlag() {
		String computeFlagKey = "STR:HC9:QIXI:VOTE:INCOME:FLAG";
		RedisHelper.set(computeFlagKey, "1");
	}
	
	
}
