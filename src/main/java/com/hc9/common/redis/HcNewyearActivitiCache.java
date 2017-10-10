package com.hc9.common.redis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.model.LotteryRank;
import com.hc9.model.PrizeInfo;

/** 新年抽奖活动相关 */
public class HcNewyearActivitiCache {
	/** 赠送永久抽奖 机会
	 * @param userId 被赠送人主键id
	 * @param chanceNum 赠送次数*/
	public static void increasePermanentLotteryChance(long userId, int chanceNum) {
		if(validCurrentDate(new Date()) >= 0) {
			String key = "INT:HC9:NEWYEAR:PERM:LOTTERY:NUM:" + userId;
			RedisHelper.incrBy(key, chanceNum);
		}
	}
	
	/** 减少赠送的永久抽奖机会 */
	public static void decreasePermanentLotteryChance(long userId) {
		String key = "INT:HC9:NEWYEAR:PERM:LOTTERY:NUM:" + userId;
		RedisHelper.decrBy(key, 1);
	}
	
	/** 获取用户赠送的永久抽奖次数 */
	public static int getPermanentLotteryChance(long userId) {
		String key = "INT:HC9:NEWYEAR:PERM:LOTTERY:NUM:" + userId;
		String grantNum = RedisHelper.get(key);
		if(StringUtil.isBlank(grantNum)) {
			grantNum = "0";
		}
		return Integer.valueOf(grantNum);
	}
	
	/** 赠送临时抽奖次数 */
	public static void increaseTemporaryLotteryNum(long userId, String currentDate) {
		String key = "INT:HC9:NEWYEAR:TEMP:LOTTERY:NUM:" + currentDate + ":" + userId;
		RedisHelper.incrBy(key, 1);
	}
	
	/** 减少赠送的临时抽奖机会 */
	public static void decreaseTemporaryLotteryChance(long userId, String currentDate) {
		String key = "INT:HC9:NEWYEAR:TEMP:LOTTERY:NUM:" + currentDate + ":" + userId;
		RedisHelper.decrBy(key, 1);
	}
	
	/** 获取用户赠送的临时抽奖次数 */
	public static int getTemporaryLotteryChance(long userId, String currentDate) {
		String key = "INT:HC9:NEWYEAR:TEMP:LOTTERY:NUM:" + currentDate + ":" + userId;
		String grantNum = RedisHelper.get(key);
		if(StringUtil.isBlank(grantNum)) {
			grantNum = "0";
		}
		return Integer.valueOf(grantNum);
	}
	
	/** 用户实物中奖标识：一个用户只能中一次VIP实物奖 */
	public static boolean isUserHaveVIPPrize(Long userId) {
		String key = "STR:HC9:NEWYEAR:VIP:LOTTERY:PRIZE:" + userId;
		return RedisHelper.isKeyExist(key);
	}
	
	/** 如果用户中奖，记录所获取的奖品信息 */
	public static void giveVIPPrizeToUser(Long userId, String prizeType) {
		String key = "STR:HC9:NEWYEAR:VIP:LOTTERY:PRIZE:" + userId;
		RedisHelper.set(key, prizeType);
	}
	
	/** 获取当前用户还有多少次抽奖机会 */
	public static int getLotteryChanceNumOfUser(long userId) {
		int resultNum = 0;
		Date now = new Date();
		if(validCurrentDate(now) >= 0) {
			String currentDate = DateFormatUtil.dateToString(now, "yyyy-MM-dd");
			int permanentNum = getPermanentLotteryChance(userId); // 永久抽奖次数
			int temporaryNum = getTemporaryLotteryChance(userId, currentDate);// 临时抽奖次数
			resultNum = permanentNum + temporaryNum;
		}
		return resultNum;
	}
	
	/** 投资赠送抽奖机会  
	 * @param middle */
	public static void giveLotteryChanceNumForInvest(long userId, int investType,Double priority,Double middle) {
		Date now = new Date();
		Double investMoney = investType == 1 ? priority : middle;
		if(investType == 1 || investType == 2) {   // 优先和夹层
			// 累计当前用户的优先投资额
			HcNewyearActivitiCache.incrNewyearInvestMoney(userId, investMoney);
			String currentDate = DateFormatUtil.dateToString(now, "yyyy-MM-dd");
			String lockKey = "STR:HC9:NEWYEAR:SMALL:MONEY:LOCK:" + currentDate + ":" + userId;
			if(!RedisHelper.isKeyExistSetWithExpire(lockKey, 24 * 60 * 60)) {
				increaseTemporaryLotteryNum(userId, currentDate);
			} else {
				if(investMoney >= 2000) {
					double num = investMoney / 2000.00;
					int chanceNum = (int)Math.floor(num);
					if(chanceNum > 0) {
						increasePermanentLotteryChance(userId, chanceNum);
					}
				}
			}
		}
	}
	
	/** 获取新年抽奖实物奖品列表 */
	public static List<PrizeInfo> getNewyearMaterialPrizeList() {
		/** 实物奖品列表相关:
		 * 奖品信息：1、IPad MINI；2、Kindle电子书；3、红筹台历  */
		String prizeListKey = "STR:HC9:MATERIAL:NEWYEAR:PRIZE:LIST";
		String prizeListInfo = RedisHelper.get(prizeListKey);
		if(StringUtil.isBlank(prizeListInfo)) {
			List<PrizeInfo> prizeList = compositeMaterialPrizeList();
			prizeListInfo = JsonUtil.toJsonStr(prizeList);
			RedisHelper.set(prizeListKey, prizeListInfo);
		}
		List<PrizeInfo> prizeList = JsonUtil.jsonToList(prizeListInfo, PrizeInfo.class);
		return prizeList;
	}
	
	/** 记录总共发放实物奖品的数量 */
	public static void increaseTotalLotteryNum() {
		String key = "INT:HC9:NEWYEAR:ALL:LOTTERY:NUM";
		RedisHelper.incrBy(key, 1);
	}
	
	/** 获取总共发放实物奖品的数量 */
	public static int getTotalLotteryNum() {
		String key = "INT:HC9:NEWYEAR:ALL:LOTTERY:NUM";
		String num = RedisHelper.get(key);
		if(StringUtil.isBlank(num)) {
			num = "0";
			RedisHelper.set(key, num);
		}
		return Integer.valueOf(num);
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
		String beginDateKey = "STR:HC9:MATERIAL:NEWYEAR:BEGIN:DATE";
		String beginDate = RedisHelper.get(beginDateKey);
		if(StringUtil.isBlank(beginDate)) {
			beginDate = "2015-12-29";
			RedisHelper.set(beginDateKey, beginDate);
		}
		return beginDate;
	}
	
	/** 获取活动结束时间 */
	public static String getActiveEndDate() {
		String endDateKey = "STR:HC9:MATERIAL:NEWYEAR:END:DATE";
		String endDate = RedisHelper.get(endDateKey);
		if(StringUtil.isBlank(endDate)) {
			endDate = "2016-02-29";
			RedisHelper.set(endDateKey, endDate);
		}
		return endDate;
	}
	
	/** 组装事务奖品相关信息
	 *  奖品信息：1、IPad MINI；2个 2、Kindle电子书；2个  3、红筹台历；  126个
	 **/
	private static List<PrizeInfo> compositeMaterialPrizeList() {
		List<PrizeInfo> prizeList = new ArrayList<PrizeInfo>();
		/** IPad mini (1988元) */
		PrizeInfo ipad_mini = new PrizeInfo();
		ipad_mini.setPrizeType("11");
		ipad_mini.setPrizeName("IPad mini");
		prizeList.add(ipad_mini);
		
		/** Kindle电子书阅读器（499元）*/
		PrizeInfo e_book = new PrizeInfo();
		e_book.setPrizeType("9");
		e_book.setPrizeName("Kindle电子书");
		prizeList.add(e_book);
		
		/** 红筹台历*/
		PrizeInfo desk_cale = new PrizeInfo();
		desk_cale.setPrizeType("12");
		desk_cale.setPrizeName("红筹台历");
		prizeList.add(desk_cale);
		return prizeList;
	}
	
	/** 将中奖列表保存至redis中  */
	public static void saveLotteryRankList(List list) {
		if(list != null) {
			List<String> redisList = new ArrayList<String>();
			for(Object obj : list) {
				//l.userid,l.prizetype,l.receivetime,u.phone,l.money
				Object[] arr = (Object[])obj;
				long prizeType = StatisticsUtil.getLongFromBigInteger(arr[1]);
				String mobilePhone = (String)arr[3];
				String prizeName = "";
				/**
				 * 奖品信息：1、IPad MINI；2、Kindle电子书；3、红筹台历
				 * 4、3元红包；5、5元红包 ； 6、10元嗒嗒代金券；7、20元嗒嗒代金券；8、50元嗒嗒代金券； 
				 * 0:系统后台异常；-1:抽奖活动尚未开始; -2:抽奖活动已经结束;-3:无抽奖机会;-4:没登录;
				 * */
				if(100 == prizeType) {   // 100表示红包
					int money = ((BigDecimal)arr[4]).intValue();
					prizeName = money + "元红包";
				} else if(80 == prizeType) {  // 80表示嗒嗒代金券
					int money = ((BigDecimal)arr[4]).intValue();
					prizeName = money + "元嗒嗒代金券";
				} else if(9 == prizeType) {
					prizeName = "Kindle电子书";
				} else if(11 == prizeType) {
					prizeName = "IPad mini";
				} else if(12 == prizeType) {
					prizeName = "红筹台历";
				} 
				LotteryRank lotteryRank = new LotteryRank();
				lotteryRank.setMobilePhone(mobilePhone);
				lotteryRank.setPrizeName(prizeName);;
				
				redisList.add(JsonUtil.toJsonStr(lotteryRank));
			}
			RedisHelper.setList("LIST:HC9:NEWYEAR:LOTTERY:RANK", redisList);
		}
	}
	
	/** 从redis中获取中奖列表记录 */
	public static List<LotteryRank> getLotteryRankList() {
		List<LotteryRank> investRankList = RedisHelper.getList("LIST:HC9:NEWYEAR:LOTTERY:RANK", LotteryRank.class);
		return investRankList;
	}
	
	/** 根据keyType记录已发放的实物数量 */
	public static void setOutPutPrizeNum(int keyType) {
		String key = null;
		if (0 == keyType) {  
			key = "INT:HC9:NEWYEAR:IPADMINI:NUM";// IPad mini
		} else if (1 == keyType) {
			key = "INT:HC9:NEWYEAR:EBOOK:NUM";// Kindle电子书
		} else {
			key = "INT:HC9:NEWYEAR:DESKCALD:NUM";// 红筹台历
		}
		RedisHelper.incrBy(key, 1);  // 记录已发放数
	}
	
	/** 根据keyType获取已发放的实物数量 */
	public static int getOutPutPrizeNum(int keyType) {
		String key = null;
		if (0 == keyType) {  
			key = "INT:HC9:NEWYEAR:IPADMINI:NUM";// IPad mini
		} else if (1 == keyType) {
			key = "INT:HC9:NEWYEAR:EBOOK:NUM";// Kindle电子书
		} else {
			key = "INT:HC9:NEWYEAR:DESKCALD:NUM";// 红筹台历
		}
		String num = RedisHelper.get(key);// 获取已发放数
		boolean flag = StringUtil.isBlank(num);
		if (flag) {
			RedisHelper.set(key, "0");
			return 0;
		} else {
			return Integer.parseInt(num);
		}
	}
	
	/** 根据keyType记录当天已发放的实物数量 */
	public static void setOutPutTodayPrizeNum(int keyType,String currentDate) {
		String key = null;
		if (0 == keyType) {  
			key = "INT:HC9:NEWYEAR:IPADMINI:NUM:TODAY:"+currentDate;// IPad mini
		} else if (1 == keyType) {
			key = "INT:HC9:NEWYEAR:EBOOK:NUM:TODAY:"+currentDate;// Kindle电子书
		} else {
			key = "INT:HC9:NEWYEAR:DESKCALD:NUM:TODAY:"+currentDate;// 红筹台历
		}
		RedisHelper.incrBy(key, 1);  // 记录已发放数
	}
	
	/** 根据keyType获取当天已发放的实物数量 */
	public static int getOutPutTodayPrizeNum(int keyType,String currentDate) {
		String key = null;
		if (0 == keyType) {  
			key = "INT:HC9:NEWYEAR:IPADMINI:NUM:TODAY:"+currentDate;// IPad mini
		} else if (1 == keyType) {
			key = "INT:HC9:NEWYEAR:EBOOK:NUM:TODAY:"+currentDate;// Kindle电子书
		} else {
			key = "INT:HC9:NEWYEAR:DESKCALD:NUM:TODAY:"+currentDate;// 红筹台历
		}
		String num = RedisHelper.get(key);// 获取已发放数
		boolean flag = StringUtil.isBlank(num);
		if (flag) {
			RedisHelper.set(key, "0");
			return 0;
		} else {
			return Integer.parseInt(num);
		}
	}

	/** 根据userId统计该用户在新年抽奖活动期间累计的投资额 */
	public static void incrNewyearInvestMoney(Long userId,Double money) {
		String key = "NEWYEAR:INVEST:MONEY:USER:"+userId; 
		RedisHelper.incrBy(key, money.intValue());  // 记录已发放数
	}
	
	/** 根据userId获取该用户在新年抽奖活动期间累计的投资额 */
	public static int getNewyearInvestMoney(Long userId) {
		String key = "NEWYEAR:INVEST:MONEY:USER:"+userId; 
		String totalMoney = RedisHelper.get(key);
		return StringUtil.isBlank(totalMoney) ? 0 : Integer.parseInt(totalMoney);
	}
	
	/** 记录当天发放的嗒嗒代金券或者红包金额 */
	public static void incrTapTapTodayNum(String currentDate,int money) {
		String key = "NEWYEAR:TAPTAP:TODAY:"+currentDate+":MONEY:"+money; 
		RedisHelper.incrBy(key, 1);  // 记录已发嗒嗒代金券的金额
	}
	
	/** 获取当天发放的嗒嗒代金券或者红包金额 */
	public static int getTapTapTodayNum(String currentDate,int money) {
		String key = "NEWYEAR:TAPTAP:TODAY:"+currentDate+":MONEY:"+money; 
		String str = RedisHelper.get(key);  // 获取已发嗒嗒代金券的金额
		return !StringUtil.isBlank(str) ? Integer.valueOf(str) : 0; 
	}
}