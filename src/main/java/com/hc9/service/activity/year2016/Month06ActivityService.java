package com.hc9.service.activity.year2016;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.activity.year2016.month06.HcMaxLatestCache;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.activity.LoanPrizeVo;

/** 2016年6月活动：满标放款时一鸣惊人、一锤定音活动相关 */
@Service
public class Month06ActivityService {
	
	@Resource
	private HibernateSupport dao;
	
	/** 生成一鸣惊人奖励信息 */
	public void generateMaxRecord(String loanRecordId) {
		String sql = "select * from loanrecord where id=?";
		Loanrecord loanRecord = dao.findObjectBySql(sql, Loanrecord.class, loanRecordId);
		Loansign loan = loanRecord.getLoansign();
		String key = "STR:HC9:MONTH06:LOAN:MAX:FLAG:" + loan.getId();
		if(!RedisHelper.isKeyExistSetWithExpire(key, 3600)) {
			String phone = loanRecord.getUserbasicsinfo().getUserrelationinfo().getPhone();
			double rewardMoney = 168;
			String loanName = loan.getName();
			
			LoanPrizeVo loanPrizeVo = new LoanPrizeVo();
			loanPrizeVo.setLoanName(loanName);
			loanPrizeVo.setPhone(phone);
			loanPrizeVo.setPrizeName("一鸣惊人");
			loanPrizeVo.setRewardMoney(rewardMoney);
			HcMaxLatestCache.setMaxLatestPrizeInfo(1, loanPrizeVo);
		}
	}
	
	/** 生成一锤定音奖励信息 */
	public void generateLatestRecord(String loanRecordId) {
		String sql = "select * from loanrecord where id=?";
		Loanrecord loanRecord = dao.findObjectBySql(sql, Loanrecord.class, loanRecordId);
		Loansign loan = loanRecord.getLoansign();
		String key = "STR:HC9:MONTH06:LOAN:LATEST:FLAG:" + loan.getId();
		if(!RedisHelper.isKeyExistSetWithExpire(key, 3600)) {
			String phone = loanRecord.getUserbasicsinfo().getUserrelationinfo().getPhone();
			double rewardMoney = 68;
			String loanName = loan.getName();
			
			LoanPrizeVo loanPrizeVo = new LoanPrizeVo();
			loanPrizeVo.setLoanName(loanName);
			loanPrizeVo.setPhone(phone);
			loanPrizeVo.setPrizeName("一锤定音");
			loanPrizeVo.setRewardMoney(rewardMoney);
			HcMaxLatestCache.setMaxLatestPrizeInfo(0, loanPrizeVo);
		}
	}
}
