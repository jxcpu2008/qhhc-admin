package com.hc9.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.activity.year2016.month04.HcClimbTopActivityCache;
import com.hc9.common.redis.activity.year2016.month04.HcOpenCardActivityCache;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.DateUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.entity.InterestIncreaseCard;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.ClimbVo;

/** 红筹四月活动相关业务逻辑 */
@Service
public class AprilActivityService {
	@Resource
	private HibernateSupport dao;
	
	/** 一级抽奖中奖后相关逻辑处理 */
	public void oneLevelLottery(Userbasicsinfo user, String phone, long lotteryResult) {
		if(lotteryResult == 1) {
			saveAcivityMoney(user.getId(), phone, 1);
		} else if(lotteryResult == 2) {
			saveRedEnvelope(user.getId(), 3, 100, 4);
		} else if(lotteryResult == 3) {
			saveInterestIncreaseCard(user.getId(), 0.001, 1000);
		}
		HcOpenCardActivityCache.increaseUsedOpenCardLotterNum(user.getId());
	}
	
	/** 二级抽奖中奖后相关逻辑处理 */
	public void twoLevelLottery(Userbasicsinfo user, String phone, long lotteryResult) {
		if(lotteryResult == 1) {
			saveAcivityMoney(user.getId(), phone, 5);
		} else if(lotteryResult == 2) {
			saveRedEnvelope(user.getId(), 10, 1000, 4);
		} else if(lotteryResult == 3) {
			saveInterestIncreaseCard(user.getId(), 0.003, 3000);
		}
		HcOpenCardActivityCache.increaseUsedOpenCardLotterNum(user.getId());
	}
	
	/** 三级抽奖中奖后相关逻辑处理 */
	public void threeLevelLottery(Userbasicsinfo user, String phone, long lotteryResult) {
		if(lotteryResult == 1) {
			saveAcivityMoney(user.getId(), phone, 20);
		} else if(lotteryResult == 2) {
			saveRedEnvelope(user.getId(), 60, 8000, 4);
		} else if(lotteryResult == 3) {
			saveInterestIncreaseCard(user.getId(), 0.008, 8000);
		}
		HcOpenCardActivityCache.increaseUsedOpenCardLotterNum(user.getId());
	}
	
	/** 四级抽奖中奖后相关逻辑处理 */
	public void fourLevelLottery(Userbasicsinfo user, String phone, long lotteryResult) {
		if(lotteryResult == 1) {
			saveAcivityMoney(user.getId(), phone, 100);
		} else if(lotteryResult == 2) {
			saveRedEnvelope(user.getId(), 10, 1000, 4);
			saveRedEnvelope(user.getId(), 10, 1000, 4);
			saveRedEnvelope(user.getId(), 30, 3000, 4);
			saveRedEnvelope(user.getId(), 30, 3000, 4);
			saveRedEnvelope(user.getId(), 60, 8000, 4);
			saveRedEnvelope(user.getId(), 60, 8000, 4);
		} else if(lotteryResult == 3) {
			saveInterestIncreaseCard(user.getId(), 0.015, 20000);
		}
		HcOpenCardActivityCache.increaseUsedOpenCardLotterNum(user.getId());
	}
	
	/** 保存奖品一：现金券 */
	public void saveAcivityMoney(long userId, String phone, double rewardMoney) {
		Date date = new Date();
		String createTime = DateFormatUtil.dateToString(date, "yyyy-MM-dd HH:mm:ss");
		
		ActivityMonkey activityMonkey = new ActivityMonkey();
		activityMonkey.setUserId(userId);
		activityMonkey.setMobilePhone(phone);
		activityMonkey.setMoney(0.0);
		activityMonkey.setType(11);
		activityMonkey.setLoanId(0L);
		activityMonkey.setLoanName("");
		activityMonkey.setLoanRecordId(0L);
		activityMonkey.setRewardMoney(rewardMoney);
		activityMonkey.setCreateTime(createTime);
		activityMonkey.setWeek(0);
		activityMonkey.setStatus(0);
		activityMonkey.setExamineStatus(0);
		dao.save(activityMonkey);
	}
	
	/** 保存奖品二：红包 */
	public void saveRedEnvelope(long userId, double bonusMoney, double lowestUseMoney, int sourceType) {
		String receiveTime = DateUtils.format("yyyy-MM-dd HH:mm:ss");
		String beginTime = DateUtils.format("yyyy-MM-dd");
		String endTime = DateUtil.getSpecifiedMonthAfter(beginTime, 1);
		String sql = "insert into redenvelopedetail" + 
				"(userId,money,lowestUseMoney,receiveTime,beginTime,endTime,useFlag,sourceType) " + 
				"values(?,?,?,?,?,?,0,?)";
		dao.executeSql(sql, userId, bonusMoney, lowestUseMoney, receiveTime, 
				beginTime, endTime, sourceType);
	}
	
	/** 保存奖品三：加息券 */
	public void saveInterestIncreaseCard(long userId, double interestRate, double lowestUseMoney) {
		String time = DateUtils.format("yyyy-MM-dd HH:mm:ss");
		InterestIncreaseCard interest= new InterestIncreaseCard();
		interest.setUserId(userId);
		interest.setLoanrecordId(0l);
		interest.setLowestUseMoney(lowestUseMoney);
		interest.setInterestRate(interestRate);
		interest.setReceiveTime(time);
		String beginTime = DateUtils.format("yyyy-MM-dd");
		interest.setBeginTime(beginTime);
		interest.setEndTime(DateUtil.getSpecifiedMonthAfter(beginTime, 1));
		interest.setUseFlag(0);
		interest.setSourceType(4);
		dao.save(interest);
	}
	
	/** 爬山活动奖励结果一键生成 */
	public Map<String, String> generateClimbTopResult() {
		Map<String, String> resultMap = new HashMap<String, String>();
		/** 校验是否已经生成过爬山活动相关数据 */
		String endDateStr = HcClimbTopActivityCache.getClimbTopActivityEndDate();
		Date endDate = DateFormatUtil.stringToDate(endDateStr, "yyyy-MM-dd HH:mm:ss");
		Date currentDate = new Date();
		if(currentDate.before(endDate)) {
			resultMap.put("msg", "活动尚未结束，不能进行该项操作！");
			resultMap.put("code", "0");
			return resultMap;
		}
		
		String generateClimbKey = "STR:HC9:CLIMB:TOP:GENERATE:RESULT";
		if(RedisHelper.isKeyExist(generateClimbKey)) {
			resultMap.put("msg", "中奖结果不能重复生成！");
			resultMap.put("code", "0");
			return resultMap;
		}
		
		if(isExistClimbRecord()) {
			resultMap.put("msg", "数据库中上存在本次攀爬活动的中奖记录，不能重复生成！");
			resultMap.put("code", "0");
			return resultMap;
		}
		String genResultKey = "STR:HC9:APRIL:CLIMB:GEN:RESULT:FLAG";
		if(RedisHelper.isKeyExist(genResultKey)) {
			resultMap.put("msg", "已经生成过相关中奖记录，不能重复生成！");
			resultMap.put("code", "0");
			return resultMap;
		}
		String genResultConcurrentLock = "STR:HC9:APRIL:CLIMB:GEN:RESULT:LOCK";
		if(RedisHelper.isKeyExist(genResultConcurrentLock)) {
			resultMap.put("msg", "后台正在处理，请耐心等待！");
			resultMap.put("code", "0");
			return resultMap;
		} else {
			List<ClimbVo> userList = queryClimbVoList();
			if(userList.size() > 0) {
				for(ClimbVo vo : userList) {
					long userId = vo.getUserId();
					double investMoney = vo.getInvestMoney();
					
					String key = "STR:HC9:CLIMB:TOP:LOGIN:TIP:" + userId;
					int result = 0;
					if(investMoney >= 1000 && investMoney < 10000) {
						/** 1000-9900: 10元红包 */
						saveRedEnvelope(userId, 10, 1000, 8);
						result = 10;
					} else if(investMoney >= 10000 && investMoney < 100000) {
						/** 10000-99900:50元京东购物卡 */
						giveGrizeDetail(userId, 13, "50元京东购物卡");
						result = 13;
					} else if(investMoney >= 100000 && investMoney < 500000) {
						/** 100000-499900:  */
						giveGrizeDetail(userId, 14, "飞利浦电动牙刷");
						result = 14;
					} else if(investMoney >= 500000 && investMoney < 2000000) {
						/** 500000-1999900:  */
						giveGrizeDetail(userId, 15, "富士相机");
						result = 15;
					} else if(investMoney >= 2000000) {
						/** 2000000及以上: */
						giveGrizeDetail(userId, 16, "小米套装");
						result = 16;
					}
					RedisHelper.set(key, "" + result);
				}
			}
			RedisHelper.set(genResultKey, "1");
			RedisHelper.del(genResultConcurrentLock);
			resultMap.put("msg", "爬山活动奖励结果一键生成！");
			resultMap.put("code", "0");
			return resultMap;
		}
	}
	
	/** 查询活动期间所有投资人的投资信息 */
	public List<ClimbVo> queryClimbVoList() {
		List<ClimbVo> resultList = new ArrayList<ClimbVo>();
		String beginDate = HcClimbTopActivityCache.getClimbTopActivityBeginDate();
		String endDate = HcClimbTopActivityCache.getClimbTopActivityEndDate();
		String sql = "select userId,totalMoney from " + 
				"(select userbasicinfo_id as userId,sum(investMoney) as totalMoney from " + 
				"(select (l.tenderMoney * s.remonth) as investMoney,l.userbasicinfo_id from loanrecord l, loansign s " + 
				"where l.isSucceed=1 and l.tenderTime>=? and l.tenderTime <=? " +
				"and s.id=l.loanSign_id and s.`type` != 3 and l.subType in(1,2) " + 
				"union all " + 
				"select (l.tenderMoney * 1) as investMoney,l.userbasicinfo_id from loanrecord l, loansign s " + 
				"where l.isSucceed=1 and l.tenderTime>=? and l.tenderTime <=? " + 
				"and s.id=l.loanSign_id and s.`type` = 3 and l.subType in(1,2)) t group by userId) a " + 
				"where totalMoney>=1000 order by totalMoney asc";
		List list = dao.findBySql(sql, beginDate, endDate, beginDate, endDate);
		if(list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[]) list.get(i);
				ClimbVo vo = new ClimbVo();
				vo.setUserId(StatisticsUtil.getLongFromBigInteger(obj[0]));
				vo.setInvestMoney(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)obj[1]));
				resultList.add(vo);
			}
		}
		return resultList;
	}
	
	/** 实物奖品发放 */
	public void giveGrizeDetail(long userId, int prizetype, String prizeName) {
		String receiveTime = DateUtils.format("yyyy-MM-dd HH:mm:ss");
		String sql = "insert into prizedetail(userId, prizetype,receivetime,prizename) values(?,?,?,?)";
		dao.executeSql(sql, userId, prizetype, receiveTime, prizeName);
	}
	
	/** 判断是否存在登山活动的相关记录 */
	public boolean isExistClimbRecord() {
		String sql = "select count(id) from prizedetail where prizeType in (13,14,15,16)";
		BigInteger total = (BigInteger)dao.findObjectBySql(sql);
		if(total.longValue() > 0) {
			return true;
		}
		return false;
	}
}