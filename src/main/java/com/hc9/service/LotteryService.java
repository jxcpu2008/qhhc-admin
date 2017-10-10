package com.hc9.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.HongBaoConstant;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.RedisUtil;
import com.hc9.common.util.Arith;
import com.hc9.common.util.ComparatorImpl;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.PrizeDetail;
import com.hc9.dao.entity.RedEnvelopeDetail;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;

@Service
public class LotteryService {
	@Resource
	private HibernateSupport dao;
	
	
	/**
	 * 生成抽奖号码
	 * @param bound 限额
	 * @param shopId 店铺id
	 * @return 抽奖号
	 */
	public Integer generateLottery(int bound,long shopId){
		Random r=new Random(System.currentTimeMillis());
//		int length=String.valueOf(bound).length();
		int lottery=r.nextInt(bound)+1;
		String sql="SELECT distinct sr.lottery FROM shop_record sr WHERE sr.shop_id=? AND sr.type=1";
		List results=dao.findBySql(sql, shopId);
		ComparatorImpl impl=new ComparatorImpl();
		Collections.sort(results,impl);
		Integer obj;
		if(results.size()>0){
			int i=0;
			while(i<results.size()){
				obj=(Integer) results.get(i);
				if(obj==lottery){
					lottery=r.nextInt(bound)+1;
					i=0;
					continue;
				}
				i++;
			}
		}

		return lottery;
	}
	public Integer generateLottery(long shopId){
		String lottery;
		String sql="SELECT COUNT(sr.id) FROM shop_record sr WHERE sr.shop_id=? AND sr.type=1 AND sr.lottery IS NOT NULL";
		lottery=String.valueOf(dao.findObjectBySql(sql, shopId));
		
		return Integer.parseInt(lottery)+1;
	}
	/**
	 * 产生中奖号
	 * @param num1 上证指数
	 * @param num2 深圳成指
	 * @param num3 抽奖人数
	 * @return
	 */
	public String generateWinner(double num1,double num2,double num3){
		BigDecimal result=new BigDecimal(Arith.mul(num1, num2));
		result=result.multiply(new BigDecimal(10000));
		String str=result.toString();
		int flag=str.indexOf(".");
		if(flag>0){
			str=str.substring(0, flag);
		}
		str=invertNumber(str);
		double winner=Double.parseDouble(str);
		winner=winner % num3+1;
		str=String.valueOf(winner);
		return str.substring(0, str.indexOf("."));
	}
	/**
	 * 产生多人中奖纪录
	 * @param num1 上证指数
	 * @param num2 深圳成指
	 * @param num3 抽奖人数
	 * @param quantity 奖励人数
	 * @return
	 */
	public List<String> generateWinners(double num1,double num2,double num3,int quantity){
		String winNum=generateWinner(num1,num2,num3);
		List<String> winers=new ArrayList<String>(quantity);
		for(int i=1;i<=quantity;i++){
			int num=0;
			if(i==1){
				num=Integer.parseInt(winNum);
			}else{
				num=Integer.parseInt(winNum)+i*1000+11;
			}
			
			winers.add(String.valueOf(num));
		}
		return winers;
	}
	
	/**
	 * 反转数字
	 * @param str
	 * @return
	 */
	private String invertNumber(String str){
		String result="";
		for(int i=str.length();i>0;i--){
			result +=str.charAt(i-1);
		}
		while(String.valueOf(result.charAt(0)).equals("0")){
			result=result.substring(1);
		}
		return result;
	}
	
	/**
	 * 补足位数
	 * @param num 要补位的数字
	 * @param len 长度
	 * @return
	 */
	private String addNumBit(int num,int len){
		String s=String.valueOf(num);
		int gap=len-s.length();
		while(gap>0){
			s="0"+s;
			gap=len-s.length();
		}
		return s;
	}
	
	/** 7月17日-8月16日活动抽奖相关逻辑 
	 * u 奖池设实物奖品3件（运动相机、体脂秤、冰箱卫士），红包奖品6个（￥5、￥7、￥9、￥19、￥29、￥50），未中奖1个
	 * 
	 * u 每人中奖机会，实物奖品最高1次，红包奖品不限次数
u 中奖概率和总数：运动相机（活动期间1台，0.05%）、体脂秤（活动期间2台，0.1%）、冰箱卫士（活动期间2台，0.1%）、
￥5、7、9（每天最高不限制，15%）、￥19、29（每天最高不限制，3%）、￥50（每天最高不限制，1%）
u 实物奖品限投资额超过20W的用户才有机会获得，红包奖品不限
u 实物奖品从活动开始后第四天才开始可能产生，至少间隔3天才发出去一台
u 红包立即到账，立即有效，有效期一个月，实物奖品活动结束后两个工作日内客服主动联系发放 
	 * */
	public int getLotteryResult(Date now, long userId) {
		/** 0:未中奖；-1:抽奖活动尚未开始; -2:抽奖活动已经结束;-3:无抽奖机会；
		 *  设实物奖品：1：运动相机；2：体脂秤；3：冰箱卫士；
		 *  红包奖品：4:￥5; 5：￥7； 6：￥9；7：￥19；8：￥29；9：￥50；
		 * */
		int result = 0;
		String currentTime = DateFormatUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss");
		String currentDate = currentTime.substring(0, 10);
		/** 先抽实物奖品，再抽红包 */
		Double investMoney = RedisUtil.getInvestMoneyOfUser(userId);
		/** 实物奖品限投资额超过1W的用户才有机会获得 */
		if(investMoney >= 10000.00) {
			String hour = currentTime.substring(11, 13);//小时
			if(Integer.valueOf(hour) >= 6 && Integer.valueOf(hour) <= 22) {
			/** 随机设置实物奖品发放的时间 */
				RedisUtil.setRandomMaterialLotteryGiveTime(currentTime);
				if(Integer.valueOf(hour) >= RedisUtil.getRandowMaterialLotteryGiveTime(currentTime)) {
					result = materialLottery(userId, currentDate);
				}
			}
		}
				
		/** 没抽中实物奖品再抽红包 */
		if(result < 1) {
			result = redEnvelopeLottery(userId, now);
		}
		return result;
	}
	
	/** 红包抽奖逻辑
	 * 1、红包奖品不限投资额；
	 * 2、红包奖品不限制每人的中奖机会；
	 * 3、中奖概率和总数：￥5、7、9（每天最高各10张，15%）、￥19、29（每天最高各5张，3%）、￥50（每天最高1张，1%）；
	 * 4、红包立即到账，立即有效，有效期一个月；
	 * 红包奖品：4:￥5; 5：￥7； 6：￥9；7：￥19；8：￥29；9：￥50；
	 *  */
	private int redEnvelopeLottery(long userId, Date now) {
		int prizeType = 0;
		/** 同一个人在两小时内不再赠送红包 */
		String lastLotteryTimeKey = "STR:HC9:LAST:LOTTERY:TIME:" + userId;
		if(!RedisHelper.isKeyExist(lastLotteryTimeKey)) {
			/** 红包奖品并发逻辑处理 */
			String concurrentLock = "STR:REDENVELOPE:CONCURRENT:LOCK";
			/** 改成随机锁60至120秒:即一小时最多发送20至60个红包 */
			int lockSeconds = 60 + (int)(Math.random() * 120);
			if(!RedisHelper.isKeyExistSetWithExpire(concurrentLock, lockSeconds)) {
				prizeType = grantRedEnvelopeLottery();
				
				if(prizeType > 0) {
					/** 最近一次发放红包奖品的时间 */
					String lotteryTime = DateFormatUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss");
					
					Userbasicsinfo userbasicsinfo = new Userbasicsinfo();
					userbasicsinfo.setId(userId);
					RedEnvelopeDetail redEnvelopeDetail = new RedEnvelopeDetail();
					redEnvelopeDetail.setUserbasicsinfo(userbasicsinfo);
					redEnvelopeDetail.setMoney(HongBaoConstant.getRedEnvMoney(prizeType));
					redEnvelopeDetail.setLowestUseMoney(HongBaoConstant.getRedEnvLowestRequireMoney(redEnvelopeDetail.getMoney()));
					redEnvelopeDetail.setReceiveTime(lotteryTime);
					redEnvelopeDetail.setBeginTime(lotteryTime.substring(0, 10));
					Date endTime = DateFormatUtil.increaseDay(now, 30);
					redEnvelopeDetail.setEndTime(DateFormatUtil.dateToString(endTime, "yyyy-MM-dd"));
					redEnvelopeDetail.setUseFlag(0);
					redEnvelopeDetail.setSourceType(4);
					dao.save(redEnvelopeDetail);
					RedisHelper.setWithExpireTime(lastLotteryTimeKey, "1", 2 * 60 * 60);
				}
			}
		}
		
		return prizeType;
	}
	
	/** 红包奖品发放
	 * 红包奖品：4:￥5; 5：￥7； 6：￥9；7：￥19；8：￥29；9：￥50； */
	private int grantRedEnvelopeLottery() {
		int result = 0;
		/** 比例：15：3：1 */
		int[] redEnvelopeOneArr = new int[]{4, 5, 6, 7, 8, 9};
		int i = (int)(Math.random() * 6);
		result = redEnvelopeOneArr[i];
		String maxCountKey = "STR:HC9:REDENVELOPE:MAX:NUM";
		String middleCountKey = "STR:HC9:REDENVELOPE:MIDDLE:NUM";
		String minCountKey = "STR:HC9:REDENVELOPE:MIN:NUM";
		/** 第一种抽奖结果 */
		if(result == 9) {
			String maxCountStr = RedisHelper.get(maxCountKey);
			int maxCount = 0;
			if(StringUtil.isNotBlank(maxCountStr)) {
				maxCount = Integer.valueOf(maxCountStr);
			}
			if(maxCount >= 1) {//第一种抽奖结果已满
				result = handleMaxRedEnvelopeLottery(middleCountKey, minCountKey, result);
			}
		}
		
		/** 第二种抽奖结果 */
		if(result == 7 || result == 8) {
			String middleCountStr = RedisHelper.get(middleCountKey);
			int middleCount = 0;
			if(StringUtil.isNotBlank(middleCountStr)) {
				middleCount = Integer.valueOf(middleCountStr);
			}
			if(middleCount >= 3) {
				result = handleMiddleRedEnvelopeLottery(maxCountKey, minCountKey, result);
			}
		}
		
		/** 第三种抽奖结果 */
		if(result < 7) {
			String minCountStr = RedisHelper.get(minCountKey);
			int minCount = 0;
			if(StringUtil.isNotBlank(minCountStr)) {
				minCount = Integer.valueOf(minCountStr);
			}
			if(minCount >= 15) {
				result = handleMinRedEnvelopeLottery(maxCountKey, middleCountKey, result);
			}
		}
		
		if(result == 9) {
			RedisHelper.incrBy(maxCountKey, 1);
		}
		
		if(result == 7 || result == 8) {
			RedisHelper.incrBy(middleCountKey, 1);
		}
		
		if(result < 7) {
			RedisHelper.incrBy(minCountKey, 1);
		}
		/** 总数：每19个数字从头开始计数 */
		String countKey = "STR:HC9:REDENVELOPE:COUNT:NUM";
		RedisHelper.incrBy(countKey, 1);
		String countNumStr = RedisHelper.get(countKey);
		int countNum = Integer.valueOf(countNumStr);
		
		if(countNum >= 19) {
			/** 清零，重新计数 */
			RedisHelper.set(maxCountKey, "0");
			RedisHelper.set(middleCountKey, "0");
			RedisHelper.set(minCountKey, "0");
			RedisHelper.set(countKey, "0");
		}
		return result;
	}
	
	/** 第一种红包抽奖结果处理逻辑 */
	private int handleMaxRedEnvelopeLottery(String middleCountKey, String minCountKey, int result) {
		int[] redEnvelopeTwoArr = new int[]{4, 5, 6, 7, 8};
		int j = (int)(Math.random() * 5);
		result = redEnvelopeTwoArr[j];
		if(result == 7 || result == 8) {
			String middleCountStr = RedisHelper.get(middleCountKey);
			int middleCount = 0;
			if(StringUtil.isNotBlank(middleCountStr)) {
				middleCount = Integer.valueOf(middleCountStr);
			}
			if(middleCount >= 3) {//中等红包抽完抽小红包
				String minCountStr = RedisHelper.get(minCountKey);
				int minCount = 0;
				if(StringUtil.isNotBlank(minCountStr)) {
					minCount = Integer.valueOf(minCountStr);
				}
				if(minCount < 15) {
					int[] redEnvelopeArr = new int[]{4, 5, 6};
					int k = (int)(Math.random() * 3);
					result = redEnvelopeArr[k];
				}
			}
		}
		
		if(result < 7) {
			String minCountStr = RedisHelper.get(minCountKey);
			int minCount = 0;
			if(StringUtil.isNotBlank(minCountStr)) {
				minCount = Integer.valueOf(minCountStr);
			}
			if(minCount >= 15) {//小红包抽完抽中等红包
				String middleCountStr = RedisHelper.get(middleCountKey);
				int middleCount = 0;
				if(StringUtil.isNotBlank(middleCountStr)) {
					middleCount = Integer.valueOf(middleCountStr);
				}
				if(middleCount < 3) {
					int[] redEnvelopeFourArr = new int[]{7, 8};
					int k = (int)(Math.random() * 2);
					result = redEnvelopeFourArr[k];
				}
			}
		}
		return result;
	}
	
	/** 第二种红包抽奖结果处理逻辑 */
	private int handleMiddleRedEnvelopeLottery(String maxCountKey, String minCountKey, int result) {
		int[] redEnvelopeFiveArr = new int[]{4, 5, 6, 9};
		int j = (int)(Math.random() * 4);
		result = redEnvelopeFiveArr[j];
		if(result == 9) {
			String maxCountStr = RedisHelper.get(maxCountKey);
			int maxCount = 0;
			if(StringUtil.isNotBlank(maxCountStr)) {
				maxCount = Integer.valueOf(maxCountStr);
			}
			if(maxCount >= 1) {//大红包抽完抽小红包
				String minCountStr = RedisHelper.get(minCountKey);
				int minCount = 0;
				if(StringUtil.isNotBlank(minCountStr)) {
					minCount = Integer.valueOf(minCountStr);
				}
				if(minCount < 15) {
					int[] redEnvelopeArr = new int[]{4, 5, 6};
					int k = (int)(Math.random() * 3);
					result = redEnvelopeArr[k];
				}
			}
		}
		
		if(result < 7) {
			String minCountStr = RedisHelper.get(minCountKey);
			int minCount = 0;
			if(StringUtil.isNotBlank(minCountStr)) {
				minCount = Integer.valueOf(minCountStr);
			}
			if(minCount >= 15) {//小红包抽完抽大红包
				String maxCountStr = RedisHelper.get(maxCountKey);
				int maxCount = 0;
				if(StringUtil.isNotBlank(maxCountStr)) {
					maxCount = Integer.valueOf(maxCountStr);
				}
				if(maxCount < 1) {
					result = 9;
				}
			}
		}
		return result;
	}
	
	/** 第三种红包抽奖结果处理逻辑 */
	private int handleMinRedEnvelopeLottery(String maxCountKey, String middleCountKey, int result) {
		int[] redEnvelopeArr = new int[]{7, 8, 9};
		int j = (int)(Math.random() * 3);
		result = redEnvelopeArr[j];
		if(result == 9) {
			String maxCountStr = RedisHelper.get(maxCountKey);
			int maxCount = 0;
			if(StringUtil.isNotBlank(maxCountStr)) {
				maxCount = Integer.valueOf(maxCountStr);
			}
			if(maxCount >= 1) {//大红包抽完抽中等红包
				String middleCountStr = RedisHelper.get(middleCountKey);
				int middleCount = 0;
				if(StringUtil.isNotBlank(middleCountStr)) {
					middleCount = Integer.valueOf(middleCountStr);
				}
				if(middleCount >= 3) {
					redEnvelopeArr = new int[]{7, 8};
					int k = (int)(Math.random() * 2);
					result = redEnvelopeArr[k];
				}
			}
		}
		
		if(result == 7 || result == 8) {
			String middleCountStr = RedisHelper.get(middleCountKey);
			int middleCount = 0;
			if(StringUtil.isNotBlank(middleCountStr)) {
				middleCount = Integer.valueOf(middleCountStr);
			}
			if(middleCount >= 3) {//中等红包抽完抽大红包
				String maxCountStr = RedisHelper.get(maxCountKey);
				int maxCount = 0;
				if(StringUtil.isNotBlank(maxCountStr)) {
					maxCount = Integer.valueOf(maxCountStr);
				}
				if(maxCount < 1) {
					result = 9;
				}
			}
		}
		return result;
	}
	
	/** 实物抽奖逻辑
	 * 实物奖品限投资额超过20W的用户才有机会获得
	 * 实物奖品从活动开始后第3天才开始可能产生，至少间隔2天才发出去一台
	 * 每人中奖机会，实物奖品最高1次，
	 * */
	private int materialLottery(long userId, String currentDate) {
		int result = 0;
		String beginDateKey = "STR:HC9:LOTTERY:BEGIN:DATE";
		String beginDateStr = RedisHelper.get(beginDateKey);
		Date beginDate = DateFormatUtil.stringToDate(beginDateStr, "yyyy-MM-dd");
		Date firstEnableDate = DateFormatUtil.increaseDay(beginDate, 2);
		Date now = new Date();
		/** 实物奖品从活动开始后第3天才开始可能产生 */
		if(now.after(firstEnableDate)) {
			/** 实物奖品获奖的标识相关key */
			String userkey = "STR:HC9:MATERIAL:LOTTERY:FLAG:" + userId;
			String lotteryFlag = RedisHelper.get(userkey);
			/** 每人中奖机会，实物奖品最高1次:已获奖的不再参与实物奖品抽奖 */
			if(!"1".equals(lotteryFlag)) {
				/** 至少间隔2天才发出去一台 */
				String lastLotteryTimeKey = "STR:HC9:MATERIAL:LOTTERY:TIME";
				String lastLotteryTime = RedisHelper.get(lastLotteryTimeKey);//上次发放奖励时间
				if(StringUtil.isBlank(lastLotteryTime)) {
					/** 如果上次发放奖品时间为空则去第一次可发放奖品的时间 */
					lastLotteryTime = DateFormatUtil.dateToString(firstEnableDate, "yyyy-MM-dd");
					RedisHelper.set(lastLotteryTimeKey, lastLotteryTime);
				}
				Date lastLotteryDate = DateFormatUtil.stringToDate(lastLotteryTime, "yyyy-MM-dd");
				Date nextLotteryDate = DateFormatUtil.increaseDay(lastLotteryDate, 2);
				if(now.after(nextLotteryDate)) {
					result = lockMaterialLotter(userId, now);
				}
			}
		}
		return result;
	}
	
	/** 实物奖品锁定逻辑
	 * 实物奖品：1：运动相机；2：体脂秤；3：冰箱卫士； */
	private int lockMaterialLotter(long userId, Date now) {
		int prizeType = 0;
		/** 实物奖品并发逻辑处理 */
		String concurrentLock = "STR:MATERIAL:CONCURRENT:LOCK";
		if(!RedisHelper.isKeyExistSetWithExpire(concurrentLock, 60)) {
			prizeType = grantMaterialLottery(userId);
			
			if(prizeType > 0) {
				PrizeDetail prizeDetail = new PrizeDetail();
				prizeDetail.setUserId(userId);
				prizeDetail.setPrizeType(prizeType);
				prizeDetail.setReceiveTime(DateFormatUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss"));
				dao.save(prizeDetail);
				/** 实物奖品中奖 */
				String userkey = "STR:HC9:MATERIAL:LOTTERY:FLAG:" + userId ;
				RedisHelper.set(userkey, "1");
				/** 最近一次发放实物奖品的时间 */
				String lotteryTime = DateFormatUtil.dateToString(now, "yyyy-MM-dd");
				String lastLotteryTimeKey = "STR:HC9:MATERIAL:LOTTERY:TIME";
				RedisHelper.set(lastLotteryTimeKey, lotteryTime);
			}
		}
		
		return prizeType;
	}
	
	/** 实物奖品发放 */
	private int grantMaterialLottery(long userId) {
		int result = 0;
		/** 冰箱卫士（活动期间4台，0.1%） */
		String threeKey = "INT:HC9:MATERIAL:THREE:PRIZE:NUM";
		int threeNum = Integer.valueOf(RedisHelper.get(threeKey));
		if(threeNum > 0) {
			result = 3;
			RedisHelper.decrBy(threeKey, 1);
		}
			
		/** 体脂秤（活动期间4台，0.1%） */
		String twoKey = "INT:HC9:MATERIAL:TWO:PRIZE:NUM";
		int twoNum = Integer.valueOf(RedisHelper.get(twoKey));
		if(!(result > 0) && twoNum > 0) {
			result = 2;
			RedisHelper.decrBy(twoKey, 1);
		}
			
		/** 运动相机（活动期间2台，0.05%） */
		String oneKey = "INT:HC9:MATERIAL:ONE:PRIZE:NUM";
		int oneNum = Integer.valueOf(RedisHelper.get(oneKey));
		if(!(result > 0) && oneNum > 0) {
			result = 1;
			RedisHelper.decrBy(oneKey, 1);
		}
		return result;
	}
}