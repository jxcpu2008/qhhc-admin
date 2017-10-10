package com.hc9.common.redis;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.model.LotteryRank;
import com.hc9.model.PrizeInfo;

/** 红筹九月活动相关 */
public class HcSeptemberActivitiCache {
	/** 赠送永久抽奖 机会
	 * @param userId 被赠送人主键id
	 * @param chanceNum 赠送次数*/
	public static void increasePermanentLotteryChance(long userId, int chanceNum) {
		if(validCurrentDate(new Date()) >= 0) {
			String key = "INT:HC9:SEPTEMBER:PERM:LOTTERY:NUM:" + userId;
			RedisHelper.incrBy(key, chanceNum);
		}
	}
	
	/** 减少赠送的永久抽奖机会 */
	public static void decreasePermanentLotteryChance(long userId) {
		String key = "INT:HC9:SEPTEMBER:PERM:LOTTERY:NUM:" + userId;
		RedisHelper.decrBy(key, 1);
	}
	
	/** 获取用户赠送的永久抽奖次数 */
	public static int getPermanentLotteryChance(long userId) {
		String key = "INT:HC9:SEPTEMBER:PERM:LOTTERY:NUM:" + userId;
		String grantNum = RedisHelper.get(key);
		if(StringUtil.isBlank(grantNum)) {
			grantNum = "0";
		}
		return Integer.valueOf(grantNum);
	}
	
	/** 赠送临时抽奖次数 */
	public static void increaseTemporaryLotteryNum(long userId, int chanceNum, String currentDate) {
		String key = "INT:HC9:SEPTEMBER:TEMP:LOTTERY:NUM:" + currentDate + ":" + userId;
		RedisHelper.incrBy(key, 1);
	}
	
	/** 减少赠送的临时抽奖机会 */
	public static void decreaseTemporaryLotteryChance(long userId, String currentDate) {
		String key = "INT:HC9:SEPTEMBER:TEMP:LOTTERY:NUM:" + currentDate + ":" + userId;
		RedisHelper.decrBy(key, 1);
	}
	
	/** 获取用户赠送的临时抽奖次数 */
	public static int getTemporaryLotteryChance(long userId, String currentDate) {
		String key = "INT:HC9:SEPTEMBER:TEMP:LOTTERY:NUM:" + currentDate + ":" + userId;
		String grantNum = RedisHelper.get(key);
		if(StringUtil.isBlank(grantNum)) {
			grantNum = "0";
		}
		return Integer.valueOf(grantNum);
	}
	
	/** 获取每天实物奖品发放的时间点 */
	public static int getSeptemberMaterialGiveTime(String currentTime) {
		String currentDate = currentTime.substring(0, 10);
		String key = "STR:HC9:MATERIAL:SEPTEMBER:GIVE:TIME:" + currentDate;
		String materialLotteryGiveTime = RedisHelper.get(key);
		if(StringUtil.isBlank(materialLotteryGiveTime)) {
			materialLotteryGiveTime = "10";
			RedisHelper.setWithExpireTime(key, materialLotteryGiveTime, 24 * 60 * 60);
		}
		return Integer.valueOf(materialLotteryGiveTime);
	}
	
	/** 用户实物中奖标识：一个用户只能中一次实物奖品 */
	public static boolean isUserHaveGetMaterialPrize(Long userId) {
		String key = "STR:HC9:MATERIAL:SEPTEMBER:LOTTERY:PRIZE:" + userId;
		return RedisHelper.isKeyExist(key);
	}
	
	/** 如果用户中奖，记录所获取的奖品信息 */
	public static void giveMaterialPrizeToUser(Long userId, String prizeType) {
		String key = "STR:HC9:MATERIAL:SEPTEMBER:LOTTERY:PRIZE:" + userId;
		RedisHelper.set(key, prizeType);
	}
	
	/** 获取用户当天还有多少次抽奖机会 */
	public static int getLotteryChanceNumOfUser(long userId) {
		int resultNum = 0;
		Date now = new Date();
		if(validCurrentDate(now) >= 0) {
			String currentDate = DateFormatUtil.dateToString(now, "yyyy-MM-dd");
			int permanentNum = getPermanentLotteryChance(userId);//总送的临时抽奖次数
			int temporaryNum = getTemporaryLotteryChance(userId, currentDate);//永久生效的抽奖次数
			resultNum = permanentNum + temporaryNum;
		}
		return resultNum;
	}
	
	/** 投资赠送抽奖机会  */
	public static void giveLotteryChanceNumForInvest(long userId, int investType, Double investMoney) {
		Date now = new Date();
		if(validCurrentDate(now) >= 0) {
			if(investType == 1) {
				if(investMoney < 2000 && investMoney > 0) {
					String currentTime = DateFormatUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss");
					String currentDate = currentTime.substring(0, 10);
					String lockKey = "STR:HC9:ONE:YEAR:SMALL:MONEY:LOCK:" + currentDate + ":" + userId;
					if(!RedisHelper.isKeyExistSetWithExpire(lockKey, 24 * 60 * 60)) {
						increaseTemporaryLotteryNum(userId, 1, currentDate);
					}
				} else {
					double num = investMoney / 2000.00;
					int chanceNum = (int)Math.floor(num);
					if(chanceNum > 0) {
						increasePermanentLotteryChance(userId, chanceNum);
					}
				}
			}
		}
	}
	
	/** 获取九月实物奖品列表 */
	public static List<PrizeInfo> getSeptemberMaterialPrizeList() {
		/** 实物奖品列表相关:
		 * 奖品信息：1、往返机票(1)；2、月饼（价值75元）（20）；3、乐心血压计（5）；4、萤石智能家居套装（5）；5、麦开水杯（5）；6、体脂称（5）； */
		String prizeListKey = "STR:HC9:MATERIAL:SEPTEMBER:PRIZE:LIST";
		String prizeListInfo = RedisHelper.get(prizeListKey);
		if(StringUtil.isBlank(prizeListInfo)) {
			List<PrizeInfo> prizeList = compositeMaterialPrizeList();
			prizeListInfo = JsonUtil.toJsonStr(prizeList);
			RedisHelper.set(prizeListKey, prizeListInfo);
		}
		List<PrizeInfo> prizeList = JsonUtil.jsonToList(prizeListInfo, PrizeInfo.class);
		return prizeList;
	}
	
	/** 实物奖品发放计数器:没发放一个实物奖品增一 */
	public static void increaseWinNumber() {
		String key = "STR:HC9:MATERIAL:SEPTEMBER:WIN:NUM";
		RedisHelper.incrBy(key, 1);
	}
	
	/** 获取已经发放的实物奖品的数量 */
	public static int getWinNumber() {
		String key = "STR:HC9:MATERIAL:SEPTEMBER:WIN:NUM";
		String winNum = RedisHelper.get(key);
		if(StringUtil.isBlank(winNum)) {
			winNum = "0";
			RedisHelper.set(key, winNum);
		}
		return Integer.valueOf(winNum);
	}
	
	/** 记录当天的抽奖次数 */
	public static void increaseTodayMaterialGiveNum(String currentDate) {
		String key = "STR:HC9:MATERIAL:SEPTEMBER:TODAY:NUM:" + currentDate;
		RedisHelper.incrBy(key, 1);
	}
	
	/** 获取当天已发放的实物奖品数量 */
	public static int getTodayMaterialGiveNum(String currentDate) {
		String key = "STR:HC9:MATERIAL:SEPTEMBER:TODAY:NUM:" + currentDate;
		String num = RedisHelper.get(key);
		if(StringUtil.isBlank(num)) {
			num = "0";
			RedisHelper.set(key, num);
		}
		return Integer.valueOf(num);
	}
	
	/** 记录总共发放实物奖品的数量 */
	public static void increaseTotalLotteryNum() {
		String key = "INT:HC9:ONE:YEAR:ALL:LOTTERY:NUM";
		RedisHelper.incrBy(key, 1);
	}
	
	/** 获取总共发放实物奖品的数量 */
	public static int getTotalLotteryNum() {
		String key = "INT:HC9:ONE:YEAR:ALL:LOTTERY:NUM";
		String num = RedisHelper.get(key);
		if(StringUtil.isBlank(num)) {
			num = "0";
			RedisHelper.set(key, num);
		}
		return Integer.valueOf(num);
	}
	
	/** 设置已发放多少实物奖品数量 */
	public static void setHaveGiveMaterialPrizeNum() {
		String key = "INT:HC9:ONE:YEAR:ALL:LOTTERY:NUM";
		RedisHelper.set(key, "20");
	}
	
	/** 记录当天100元红包的抽奖次数 */
	public static void increaseTodayOneHounderdRedNum(String currentDate) {
		String key = "STR:HC9:ONE:HOUNDER:TODAY:NUM:" + currentDate;
		RedisHelper.incrBy(key, 1);
	}
	
	/** 获取当天100元红包的抽奖数量 */
	public static int getTodayOneHounderdRedNum(String currentDate) {
		String key = "STR:HC9:ONE:HOUNDER:TODAY:NUM:" + currentDate;
		String num = RedisHelper.get(key);
		if(StringUtil.isBlank(num)) {
			num = "0";
			RedisHelper.set(key, num);
		}
		return Integer.valueOf(num);
	}
	
	/** 记录总共发放100红包的数量 */
	public static void increaneOneHounderdRedNum() {
		String key = "INT:HC9:ONE:YEAR:ONE:HOUNDERD:NUM";
		RedisHelper.incrBy(key, 1);
	}
	
	/** 获取总共发放100红包的数量 */
	public static int getOneHounderdRedNum() {
		String key = "INT:HC9:ONE:YEAR:ONE:HOUNDERD:NUM";
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
	
	/** 判断当前时间是否在月饼发放时间范围内 */
	public static boolean isMooncakeGiveTime(String currentDate) {
		Date nowDate = DateFormatUtil.stringToDate(currentDate, "yyyy-MM-dd");
		Date maxDate = DateFormatUtil.stringToDate("2015-09-21", "yyyy-MM-dd");
		return DateFormatUtil.isBefore(nowDate, maxDate);
	}
	
	/** 获取活动开始时间 */
	public static String getActiveBeginDate() {
		String beginDateKey = "STR:HC9:MATERIAL:SEPTEMBER:BEGIN:DATE";
		String beginDate = RedisHelper.get(beginDateKey);
		if(StringUtil.isBlank(beginDate)) {
			beginDate = "2015-09-10";
			RedisHelper.set(beginDateKey, beginDate);
		}
		return beginDate;
	}
	
	/** 获取活动结束时间 */
	public static String getActiveEndDate() {
		String endDateKey = "STR:HC9:MATERIAL:SEPTEMBER:END:DATE";
		String endDate = RedisHelper.get(endDateKey);
		if(StringUtil.isBlank(endDate)) {
			endDate = "2015-10-08";
			RedisHelper.set(endDateKey, endDate);
		}
		return endDate;
	}
	
	/** 组装事务奖品相关信息
	 *  奖品信息：1、往返机票(1)；2、月饼（价值75元）（20）；3、乐心血压计（5）；4、萤石智能家居套装（5）；5、麦开水杯（5）；6、体脂称（5）； */
	private static List<PrizeInfo> compositeMaterialPrizeList() {
		List<PrizeInfo> prizeList = new ArrayList<PrizeInfo>();
		
		/** 2、月饼（价值75元）（20） -- 2 */
		for(int j = 0; j < 20; j++) {
			PrizeInfo moonCake = new PrizeInfo();
			moonCake.setPrizeType("2");
			moonCake.setPrizeName("月饼");
			prizeList.add(moonCake);
		}
		
		/** 往返机票  -- 1 */
		PrizeInfo planeTicket = new PrizeInfo();
		planeTicket.setPrizeType("1");
		planeTicket.setPrizeName("往返机票");
		prizeList.add(planeTicket);
		
		for(int i = 0; i < 5; i++) {
			/** 3、乐心血压计（5） */
			PrizeInfo bloodPressureMeter = new PrizeInfo();
			bloodPressureMeter.setPrizeType("3");
			bloodPressureMeter.setPrizeName("乐心血压计");
			prizeList.add(bloodPressureMeter);
			
			/** 4、萤石智能家居套装（5） */
			PrizeInfo homeSet = new PrizeInfo();
			homeSet.setPrizeType("4");
			homeSet.setPrizeName("萤石智能家居套装");
			prizeList.add(homeSet);
			
			/** 5、麦开水杯（5） */
			PrizeInfo boilingWaterCup = new PrizeInfo();
			boilingWaterCup.setPrizeType("5");
			boilingWaterCup.setPrizeName("麦开水杯");
			prizeList.add(boilingWaterCup);
			
			/** 6、体脂称（5） */
			PrizeInfo bodyFat = new PrizeInfo();
			bodyFat.setPrizeType("6");
			bodyFat.setPrizeName("体脂称");
			prizeList.add(bodyFat);
		}
		
		return prizeList;
	}
	
	 /**  计算两个日期之间相差的天数   */  
	 public static int getDaysAfterBeginDays(String currentDate) {
	 	try {
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	 	 	Calendar cal = Calendar.getInstance();    
	 	  	cal.setTime(sdf.parse(getActiveBeginDate()));
	 	 	long time1 = cal.getTimeInMillis();                 
	 	  	cal.setTime(sdf.parse(currentDate));    
	 	  	long time2 = cal.getTimeInMillis();         
	 	   	long between_days = (time2 - time1)/(1000*3600*24);  
	 	            
	 	 	return Integer.parseInt(String.valueOf(between_days));     
	 	} catch(Exception e) {
	    	throw new RuntimeException("计算两个日期之间相差的天数出错！", e);
	 	}
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
				 * 奖品信息：1、往返机票(1)；2、月饼（价值75元）（20）；3、乐心血压计（5）；4、萤石智能家居套装（5）；5、麦开水杯（5）；6、体脂称（5）；
				   7、5元红包（不限量，不中其他奖时必中）；8、10元红包（100）
				   0:未中奖；-1:抽奖活动尚未开始; -2:抽奖活动已经结束;-3:无抽奖机会；
				 * */
				if(1 == prizeType) {
					prizeName = "往返机票";
				} else if(2 == prizeType) {
					prizeName = "月饼";
				} else if(3 == prizeType) {
					prizeName = "乐心血压计";
				} else if(4 == prizeType) {
					prizeName = "萤石智能家居套装";
				} else if(5 == prizeType) {
					prizeName = "麦开水杯";
				} else if(6 == prizeType) {
					prizeName = "体脂称";
				} else if(100 == prizeType) {
					int money = ((BigDecimal)arr[4]).intValue();
					prizeName = money + "元红包";
				}
				
				LotteryRank lotteryRank = new LotteryRank();
				lotteryRank.setMobilePhone(mobilePhone);
				lotteryRank.setPrizeName(prizeName);;
				
				redisList.add(JsonUtil.toJsonStr(lotteryRank));
			}
			RedisHelper.setList("LIST:HC9:ONE:YEAR:LOTTERY:RANK", redisList);
		}
	}
	
	/** 从redis中获取中奖列表记录 */
	public static List<LotteryRank> getLotteryRankList() {
		List<LotteryRank> investRankList = RedisHelper.getList("LIST:HC9:ONE:YEAR:LOTTERY:RANK", LotteryRank.class);
		return investRankList;
	}
}