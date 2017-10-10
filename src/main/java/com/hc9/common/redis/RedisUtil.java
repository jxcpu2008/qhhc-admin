package com.hc9.common.redis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Costratio;
import com.hc9.model.LotteryRank;
import com.jubaopen.commons.LOG;

/** redis相关操作方法 */
public class RedisUtil {
	
	/** 赠送抽奖 机会
	 * @param userId 被赠送人主键id
	 * @param chanceNum 赠送次数*/
	public static void increaseLotteryChance(long userId, int chanceNum) {
		if(validCurrentDate(new Date()) >= 0) {
			String key = "INT:HC9:GIVE:LOTTERY:NUM:" + userId;
			RedisHelper.incrBy(key, chanceNum);
		}
	}
	
	/** 减少赠送的抽奖机会 */
	public static void decreaseLotteryChance(long userId) {
		String key = "INT:HC9:GIVE:LOTTERY:NUM:" + userId;
		RedisHelper.decrBy(key, 1);
	}
	
	/** 获取用户赠送的抽奖次数 */
	public static int getGrantLotteryChance(long userId) {
		String key = "INT:HC9:GIVE:LOTTERY:NUM:" + userId;
		String grantNum = RedisHelper.get(key);
		if(StringUtil.isBlank(grantNum)) {
			grantNum = "0";
		}
		return Integer.valueOf(grantNum);
	}
	
	/** 记录用户抽奖次数 */
	public static void increaseUserLotteryNum(long userId, String currentDate) {
		String userLotteryNumKey = "STR:HC9:USER:LOTTERY:NUM:" + currentDate + ":" + userId;
		RedisHelper.incrBy(userLotteryNumKey, 1);
	}
	
	/** 获取用户当天已使用的抽奖次数 */
	public static int getUserLotteryNum(long userId, String currentDate) {
		String userLotteryNumKey = "STR:HC9:USER:LOTTERY:NUM:" + currentDate + ":" + userId;
		String userLotteryNum = RedisHelper.get(userLotteryNumKey);
		if(StringUtil.isBlank(userLotteryNum)) {
			userLotteryNum = "0";
		}
		return Integer.valueOf(userLotteryNum);
	}
	
	/** 随机设置每天实物奖品的发放时间点 */
	public static void setRandomMaterialLotteryGiveTime(String currentTime) {
		String currentDate = currentTime.substring(0, 10);
		int giveTime = 8 + (int)(Math.random() * 14);//8点至晚上9点
		giveTime = 10;//统一设置成10点
		String key = "STR:HC9:MATERIAL:GIVE:TIME:" + currentDate;
		if(!RedisHelper.isKeyExist(key)) {
			RedisHelper.setWithExpireTime(key, "" + giveTime, 24 * 60 * 60);
		}
	}
	
	/** 获取每天实物奖品发放的时间点 */
	public static int getRandowMaterialLotteryGiveTime(String currentTime) {
		String currentDate = currentTime.substring(0, 10);
		String key = "STR:HC9:MATERIAL:GIVE:TIME:" + currentDate;
		return Integer.valueOf(RedisHelper.get(key));
	}
	
	/** 获取用户当天还有多少次抽奖机会 */
	public static int getLotteryChanceNumOfUser(long userId) {
		int resultNum = 0;
		if(validCurrentDate(new Date()) >= 0) {
			int grantChanceNum = RedisUtil.getGrantLotteryChance(userId);
			resultNum = grantChanceNum;
			String currentDate = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd");
			int userLotteryNum = RedisUtil.getUserLotteryNum(userId, currentDate);
			if(userLotteryNum < 3) {
				resultNum = grantChanceNum + 3 - userLotteryNum;
			}
		}
		return resultNum;
	}
	
	/** 初始化投资人的投资金额 */
	public static void setInvestMoneyOfUser(long userId, Double investMoney) {
		String key = "DOUBLE:HC9:INVEST:MONEY:" + userId;
		RedisHelper.set(key, "" + investMoney);
	}
	
	/** 新增用户的投资金额 */
	public static void increaseInvestMoneyOfUser(long userId, Double investMoney) {
		try {
			String key = "DOUBLE:HC9:INVEST:MONEY:" + userId;
			String value = RedisHelper.get(key);
			if(value == null || value.trim().length() < 1) {
				value = "0.0";
			}
			Double nowMoney = Double.valueOf(value);
			double totalMoney = Arith.add(nowMoney , investMoney).doubleValue();
			RedisHelper.set(key, "" + totalMoney);
		} catch(Exception e) {
			LOG.error("新增投资金额出错！", e);
		}
	}
	
	/** 获取用户的投资进行 */
	public static Double getInvestMoneyOfUser(long userId) {
		Double investMoney = 0.00;
		String key = "DOUBLE:HC9:INVEST:MONEY:" + userId;
		String value = RedisHelper.get(key);
		if(value != null && value.trim().length() > 0) {
			investMoney = Double.valueOf(value);
		}
		return investMoney;
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
		String beginDateKey = "STR:HC9:LOTTERY:BEGIN:DATE";
		String beginDate = RedisHelper.get(beginDateKey);
		if(StringUtil.isBlank(beginDate)) {
			beginDate = "2015-07-17";
			RedisHelper.set(beginDateKey, "2015-07-17");
		}
		return beginDate;
	}
	
	/** 获取活动结束时间 */
	public static String getActiveEndDate() {
		String endDateKey = "STR:HC9:LOTTERY:END:DATE";
		String endDate = RedisHelper.get(endDateKey);
		if(StringUtil.isBlank(endDate)) {
			endDate = "2015-08-16";
			RedisHelper.set(endDateKey, "2015-08-16");
		}
		return endDate;
	}
	
	/** 修改redis中的活动开始时间和活动结束时间 */
	public static void setCostratioInfo(Costratio costratio) {
		if(costratio != null) {
			String starTime = costratio.getStarTime();
			String endTime = costratio.getEndTime();
			System.out.println(starTime + " : " + endTime); 
			if(StringUtil.isNotBlank(starTime)) {
				String beginDateKey = "STR:HC9:LOTTERY:BEGIN:DATE";
				starTime = DateFormatUtil.dateToString(DateFormatUtil.stringToDate(starTime, "yyyyMMdd"), "yyyy-MM-dd");
				RedisHelper.set(beginDateKey, starTime);
			}
			if(StringUtil.isNotBlank(endTime)) {
				String endDateKey = "STR:HC9:LOTTERY:END:DATE";
				endTime = DateFormatUtil.dateToString(DateFormatUtil.stringToDate(endTime, "yyyyMMdd"), "yyyy-MM-dd");
				RedisHelper.set(endDateKey, endTime);
			}
		}
	}
	
	/** 投资赠送抽奖机会
	 *  @param investType 1、优先；2、夹层；3、劣后； */
	public static void giveLotteryChanceNumForInvest(long userId, int investType, Double priority, Double middle, Double after) {
		Double investMoney = 0.00;
		if(investType == 1) {
			investMoney = priority;
		} else if(investType == 2) {
			investMoney = middle;
		} else if(investMoney == 3) {
			investMoney = after;
		}
		giveLotteryChanceNumForInvest(userId, investMoney);
	}
	
	/** 投资赠送抽奖机会
	 *  @param investType 1、优先；2、夹层；3、劣后； */
	public static void giveLotteryChanceNumForInvest(long userId, Double investMoney) {
		increaseInvestMoneyOfUser(userId, investMoney);
		double num = investMoney / 5000.00;
		int chanceNum = (int)Math.floor(num);
		if(chanceNum > 0) {
			increaseLotteryChance(userId, chanceNum);
		}
	}
	
	/** 将推荐人排行榜信息保存至redis中 */
	public static void saveRecommendInvestList(List list) {
		if(list != null) {
			List<String> redisList = new ArrayList<String>();
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				BigDecimal money = (BigDecimal)arr[0];
				String userName = (String)arr[2];
				
				LotteryRank lotteryRank = new LotteryRank();
				lotteryRank.setInvestTotalMoney(money.doubleValue());
				lotteryRank.setUserName(userName);
				
				redisList.add(JsonUtil.toJsonStr(lotteryRank));
			}
			RedisHelper.setList("LIST:HC9:RECOMMEND:INVESTt", redisList);
		}
	}
	
	/** 从redis中获取推荐排行榜列表记录 */
	public static List<LotteryRank> getRecommendInvestList() {
		String key = "LIST:HC9:RECOMMEND:INVESTt";
		List<LotteryRank> recommendInvestList = RedisHelper.getList(key, LotteryRank.class);
		return recommendInvestList;
	}
	
	/** 将投资排行榜保存至redis中  */
	public static void saveInvestRankList(List list) {
		if(list != null) {
			List<String> redisList = new ArrayList<String>();
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				BigDecimal money = (BigDecimal)arr[0];
				String userName = (String)arr[1];
				
				LotteryRank lotteryRank = new LotteryRank();
				lotteryRank.setInvestTotalMoney(money.doubleValue());
				lotteryRank.setUserName(userName);
				
				redisList.add(JsonUtil.toJsonStr(lotteryRank));
			}
			RedisHelper.setList("LIST:HC9:INVEST:RANK", redisList);
		}
	}
	
	/** 从redis中获取投资排行榜列表记录 */
	public static List<LotteryRank> getInvestRankList() {
		List<LotteryRank> investRankList = RedisHelper.getList("LIST:HC9:INVEST:RANK", LotteryRank.class);
		return investRankList;
	}
	
	/** 将实物奖品排行榜保存至redis中  */
	public static void saveMaterialRankList(List list) {
		if(list != null) {
			List<String> redisList = new ArrayList<String>();
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				Integer prizeType = (Integer)arr[0];
				String userName = (String)arr[1];
				String mobilePhone = (String)arr[2];
				
				LotteryRank lotteryRank = new LotteryRank();
				lotteryRank.setPrizeType(prizeType);
				lotteryRank.setUserName(userName);
				lotteryRank.setMobilePhone(mobilePhone);
				
				redisList.add(JsonUtil.toJsonStr(lotteryRank));
			}
			RedisHelper.setList("LIST:HC9:MATERIAL:RANK", redisList);
		}
	}
	
	/** 从redis获取实物排行榜列表记录 */
	public static List<LotteryRank> getMaterialRankList() {
		List<LotteryRank> investRankList = RedisHelper.getList("LIST:HC9:MATERIAL:RANK", LotteryRank.class);
		return investRankList;
	}
	
	/** 将红包奖品排行榜保存至redis中  */
	public static void saveRedEnvelopeRankList(List list) {
		if(list != null) {
			List<String> redisList = new ArrayList<String>();
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				BigDecimal redEnvelopeMoney = (BigDecimal)arr[0];
				String userName = (String)arr[1];
				String mobilePhone = (String)arr[2];
				
				LotteryRank lotteryRank = new LotteryRank();
				lotteryRank.setRedEnvelopeMoney(redEnvelopeMoney.doubleValue());
				lotteryRank.setUserName(userName);
				lotteryRank.setMobilePhone(mobilePhone);
				
				redisList.add(JsonUtil.toJsonStr(lotteryRank));
			}
			RedisHelper.setList("LIST:HC9:REDENVELOPE:RANK", redisList);
		}
	}
	
	/** 从redis获取红包排行榜列表记录 */
	public static List<LotteryRank> getRedEnvelopeRankList() {
		List<LotteryRank> investRankList = RedisHelper.getList("LIST:HC9:REDENVELOPE:RANK", LotteryRank.class);
		return investRankList;
	}
	
	/** 判断是否是8月8日至8月31日活动期间 */
	public static boolean isAugustActivity(Date currentDate) {
		boolean result = true;
		/** 活动开始时间 */
		String beginDateStr = getAugustBeginDate();
		
		/** 活动结束时间 */
		String endDateStr = getAugustEndDate();

		Date beginDate = DateFormatUtil.stringToDate(beginDateStr, "yyyy-MM-dd");
		Date endDate = DateFormatUtil.stringToDate(endDateStr, "yyyy-MM-dd");
		
		/** 当前时间早于活动开始时间 */
		if(DateFormatUtil.isBefore(currentDate, beginDate)) {
			result = false;
		}
		
		if(DateFormatUtil.isBefore(endDate, currentDate)) {
			result = false;
		}
		return result;
	}
	
	/** 获取8月8日至8月31日活动开始时间 */
	public static String getAugustBeginDate() {
		String beginDateKey = "STR:HC9:AUGUST:ACTIVE:BEGIN:DATE";
		String beginDate = "";
		try {
			beginDate = RedisHelper.get(beginDateKey);
			if(StringUtil.isBlank(beginDate)) {
				beginDate = "2015-08-08";
				RedisHelper.set(beginDateKey, beginDate);
			}
		} catch(Exception e) {
			LOG.error("用户投标期间获取8月份活动的开始时间报错,使用默认开始时间2015-08-08！", e);
			beginDate = "2015-08-08";
		}
		return beginDate;
	}
	
	/** 获取8月8日至8月31日活动结束时间 */
	public static String getAugustEndDate() {
		String endDateKey = "STR:HC9:AUGUST:ACTIVE:END:DATE";
		String endDate = "";
		try {
			endDate = RedisHelper.get(endDateKey);
			if(StringUtil.isBlank(endDate)) {
				endDate = "2015-08-31";
				RedisHelper.set(endDateKey, endDate);
			}
		} catch(Exception e) {
			LOG.error("用户投标期间获取8月份活动的结束时间报错,使用默认结束时间2015-08-31！", e);
			endDate = "2015-08-31";
		}
		return endDate;
	}
	
	/** 获取系统容许频繁访问的次数上线 */
	public static int getSystemUpperLimit() {
		String key = "INT:HC9:SYSTEM:UPPER:LIMIT:NUM";
		String upperLimitNum = RedisHelper.get(key);
		if(StringUtil.isBlank(upperLimitNum)) {
			upperLimitNum = "5";
			RedisHelper.set(key, upperLimitNum);
		}
		return Integer.valueOf(upperLimitNum);
	}
	
	/** 生成验证码时锁定用户手机号码 */
	public static boolean isPhoneValidCodeGenInlockTime(String phone, int seconds) {
		String key = "STR:HC9:PHONE:VALID:CODE:GEN:" + phone;
		return RedisHelper.isKeyExistSetWithExpire(key, seconds);
	}
	
	/** 获取单个手机号码能生产验证码的最多个数 */
	public static Long getMaxValidCodeGenNumByPhone() {
		String key = "INT:HC9:MAX:VALID:CODE:GEN:NUM";
		String upperLimitNum = RedisHelper.get(key);
		if(StringUtil.isBlank(upperLimitNum)) {
			upperLimitNum = "5";
			RedisHelper.set(key, upperLimitNum);
		}
		return Long.valueOf(upperLimitNum);
	}
	
	/** 手机号每生成一个验证码，当天生成验证码总数加一 */
	public static void increasePhoneValidCodeTotalNum(String phone) {
		String currentDate = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd");
		String key = "INT:HC9:TOTAL:PHONE:NUM:" + currentDate + ":" + phone;
		RedisHelper.incrBy(key, 1);
	}
	
	/** 根据手机号码获取当前手机号今天所生成的验证码总数 */
	public static long getPhoneValidCodeTotalNum(String phone) {
		String currentDate = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd");
		String key = "INT:HC9:TOTAL:PHONE:NUM:" + currentDate + ":" + phone;
		String totalGenNum = RedisHelper.get(key);
		if(StringUtil.isBlank(totalGenNum)) {
			totalGenNum = "0";
			RedisHelper.set(key, totalGenNum);
		}
		return Long.valueOf(totalGenNum);
	}
	
	/** 将用户手机生成的验证信息保存至redis中 */
	public static void setCodeForRegToRedis(String phone, String numberCode) {
		String key = "STR:HC9:CODE:FOR:REG:PHONE:" + phone;
		RedisHelper.setWithExpireTime(key, numberCode, 60 * 30);
	}
	
	/** 验证码生成逻辑次数控制校验 */
	public static boolean validCodeGenNum(String sessionIdkey, String phone) {
		boolean result = false;
		String sessionIdCodeNum = RedisHelper.get(sessionIdkey);
        if(StringUtil.isBlank(sessionIdCodeNum)) {
           	sessionIdCodeNum = "0";
           	RedisHelper.set(sessionIdkey, "0");
        }
       	int sessionIdGenCodeNum = Integer.valueOf(sessionIdCodeNum);
       	LOG.error("手机号：" + phone + "-->当前的session次数:"+sessionIdGenCodeNum+"  -->最多的session次数:"+RedisUtil.getSystemUpperLimit());
       	if(sessionIdGenCodeNum < RedisUtil.getSystemUpperLimit()) {
       		
	    	/** 手机号码产生验证码，60秒内不容许重复产生 */
	    	if(!isPhoneValidCodeGenInlockTime(phone, 60)) {
	        	/** 同一个手机号码，一天最多不能生成超过5个手机验证码,默认5，可以通过redis调整 */
	        	long maxNumOfPhone = RedisUtil.getMaxValidCodeGenNumByPhone();
	        	long usedNumOfPhone = RedisUtil.getPhoneValidCodeTotalNum(phone);
	        	LOG.error("手机号：" + phone + "-->当前的手机次数:"+usedNumOfPhone+"  -->最多的手机次数:"+maxNumOfPhone);
	        	if(usedNumOfPhone < maxNumOfPhone) {
	        		result = true;
	        	}
	        } else {
	        	LOG.error("手机号 " + phone + " 60秒内被锁定！");
	        }
       	}
		return result;
	}
	/**
	 * 获取cps白名单
	 */
	public static String getCpsWhitelist(){
		try{
			String key = "RCS:HC9:REDIS:INIT:CPS:WHITELIST";
			return RedisHelper.get(key);
		}catch (Exception e) {
			LOG.error("获取IP白名单失败 " + " 状态失败！");
		}
		return "";
	}
	
	/**
	 * 设置cps白名单
	 * @param ips
	 */
	public static void setCpsWhitelist(String key,String ips){
		if(StringUtil.isNotBlank(ips)){
			RedisHelper.set(key, ips);
		}
	}
	
	public static void setPrepaymentProperties(String loanSignId, Map<String, String> properties) {
		String key = "HMSET:HC9:REDIS:PERPAYMENT:PROPERTIES:LOANSIGN:" + loanSignId;
		if (!RedisHelper.isKeyExist(key)) {
			RedisHelper.hmset(key, properties);
		}
	}
	
	public static Map<String, String> getPrepaymentProperties(String loanSignId) {
		String key = "HMSET:HC9:REDIS:PERPAYMENT:PROPERTIES:LOANSIGN:" + loanSignId;
		Map<String, String> prepaymentProperties = null;
		if (RedisHelper.isKeyExist(key)) {
			prepaymentProperties = RedisHelper.hgetall(key);
		}
		return prepaymentProperties;
	}
	
	public static void deletePrepaymentProperties(String loanSignId) {
		String key = "HMSET:HC9:REDIS:PERPAYMENT:PROPERTIES:LOANSIGN:" + loanSignId;
		if (RedisHelper.isKeyExist(key)) {
			RedisHelper.del(key);
		}
	}
}