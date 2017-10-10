package com.hc9.service.activity;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.redis.HcFinancialActivitiCache;
import com.hc9.common.redis.activity.year2016.month05.HcFirstInvestCache;
import com.hc9.common.redis.activity.year2016.month05.HcJuChengCache;
import com.hc9.common.redis.activity.year2016.month05.HcNewerTaskCache;
import com.hc9.common.redis.activity.year2016.month05.HcParentCache;
import com.hc9.common.redis.activity.year2016.month05.HcWeekSurpriseCache;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.service.HcMonkeyActivitiCache;

/** 活动入口类 */
@Service
public class ActivityAllInOneService {
	@Resource
	private ActivityCommonService activityCommonService;
	
	@Resource
	private HcMonkeyActivitiCache hcMonkeyActivitiCache;
	
	@Resource
	private HcFinancialActivitiCache hcFinancialActivitiCache;
	
	@Resource
	private HibernateSupport dao;
	
	/** 活动相关逻辑 -- 投资 */
	public void activityBusinessLogicForInvestQuery(Loansign loan, Loanrecord loanRecord, 
			boolean isDetail, boolean isCardId) {
		Userbasicsinfo user = loanRecord.getUserbasicsinfo();
		long userId = user.getId();
		String userName = user.getUserName();
		long loanId = loan.getId();
		String loanName = loan.getName();
		/** 投资类型：投资类型 1 优先 2 夹层 3劣后 */
		int investType = loanRecord.getSubType();
		double investMoney = loanRecord.getTenderMoney();//投资金额
		String phone = user.getUserrelationinfo().getPhone();//投资人电话号码
		/** 标类型：1-店铺  2-项目 3-天标 4-债权转让 */
		int loanType = loan.getType();
		/** 回购期限:如果是天标的话为天数，项目的话为月数 */
		int reMonth = loan.getRemonth();
		long loanRecordId = loanRecord.getId();
		
		/** 调用新春猴给力活动方法 */
		moneyYearTime(loanRecord.getUserbasicsinfo(), loanRecord.getSubType(), loanRecord.getTenderMoney(), 
				loanRecord.getTenderMoney(), loan, loanRecord.getId());
		
		/** 2016年05月活动相关逻辑 */
		activity201605Business(userName, userId, loanType, reMonth, investType, investMoney, 
				phone, loanRecordId, loanId, loanName, null);
	}
	
	/** 新春猴给力活动 */
	private void moneyYearTime(Userbasicsinfo user, int investType, Double priority, Double middle, Loansign loan, Long loanRecordId) {
		// 优先和夹层
		if(investType == 1 || investType == 2) {
			Double investMoney = investType == 1 ? priority : middle;
			// 活动一:一鸣惊人、一锤定音活动
			if(loan.getActivityStatus() != null && loan.getActivityStatus()==1) {
				hcMonkeyActivitiCache.activityMonkeyMax(loan, user, investMoney, loanRecordId);
			}
		}
	}
	
	/** 2016年五月活动 */
	private void activity201605Business(String userName, long userId, int loanType, int reMonth, int investType, 
			double investMoney, String phone, long loanRecordId, long loanId, String loanName, 
			String investOrderNum) {
		/** 是否聚橙网活动期间的用户 */
		boolean juChengUserFlag = isUserValidJuChengActivity(userName);
		if(!juChengUserFlag) {
			/** 平台首投送现金活动 */
			HcFirstInvestCache.giveMoneyForFirstTime(userId, investType, investMoney, 
					phone, loanRecordId, loanId, loanName, activityCommonService);
		}
		
		/** 周周惊喜大放送 */
		HcWeekSurpriseCache.increaseWeekInvestMoneyOfUser(userId, loanType, reMonth, investType, 
				investMoney, phone);
		
		/** 双亲感恩大回馈 */
		HcParentCache.increaseParentInvestMoneyOfUser(userId, loanType, reMonth, investType, investMoney, phone);
		
		/** 新手投资送红包活动 */
		HcNewerTaskCache.giveFirstInvestRedenvelopeKey(userId, investMoney, investType);
		
		if(juChengUserFlag) {
			/** 聚橙网活动相关业务逻辑 */
			juChengActivityBusiness(userName, userId, investType, investMoney, loanRecordId, loanType, reMonth, 
					phone, investOrderNum, loanId, loanName);
		}
	}
	
	/** 聚橙网活动相关业务逻辑 */
	private void juChengActivityBusiness(String userName, long userId, int investType, double investMoney, 
			long loanRecordId, int loanType, int remonth, String phone, String investOrderNum, 
			long loanId, String loanName) {
		HcJuChengCache.giveTicketForFirstTime(userId, investType, investMoney, loanRecordId, 
				loanType, remonth, phone, investOrderNum, loanId, loanName, activityCommonService);
	}

	/** 判断用户是否符合聚橙网活动的标准 */
	public boolean isUserValidJuChengActivity(String userName) {
		String spreadId = "NAIozGva";
		String beginTime = HcJuChengCache.getJuChengActivityBeginDate();
		String endTime = HcJuChengCache.getJuChengActivityEndDate();
		return activityCommonService.isUserFromSpecialChannelInActivityTime(spreadId, userName, 
				beginTime, endTime);
	}
}