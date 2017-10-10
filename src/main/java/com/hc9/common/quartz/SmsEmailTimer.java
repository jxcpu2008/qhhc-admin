package com.hc9.common.quartz;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.jfree.util.Log;
import org.springframework.stereotype.Service;

import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.commons.log.LOG;
import com.hc9.dao.SmsEmailTimerDao;
import com.hc9.model.SmsMessagePojo;
import com.hc9.service.EmailSendService;
import com.hc9.service.SmsSendService;
import com.hc9.service.SmsService;
import com.hc9.service.UserBasicStatisticsService;

/** 短信邮件定时任务 */
@Service("smsEmailTimer")
public class SmsEmailTimer {
	@Resource
	SmsEmailTimerDao smsEmailTimerDao;
	
	@Resource
	SmsService smsService;
	
	@Resource
	SmsSendService smsSendService;
	
	@Resource
	EmailSendService emailSendService;
	
	@Resource
	UserBasicStatisticsService userBasicStatisticsService;
	
	/** 还款日前短信邮件提醒：项目还款日前5天发送，每天发送一条。还款提醒内容分为还款日前、逾期后2种版本。 */
	public void smsAndEmailRemiandBeforRepayDate() {
		Log.error("smsAndEmailRemiandBeforRepayDate:项目还款日前短信邮件提醒！");
		try {
			Date now = new Date();
			Date endTime = DateFormatUtil.increaseDay(now, 4);
			String beginDate = DateFormatUtil.dateToString(now, "yyyy-MM-dd");
			String endDate = DateFormatUtil.dateToString(endTime, "yyyy-MM-dd");
			List<SmsMessagePojo> smsReminderList = smsEmailTimerDao.queryRemindListBeforRepayDate(beginDate, endDate);
			if(smsReminderList != null && smsReminderList.size() > 0) {
				Log.error("项目还款日前短信邮件提醒,共有：" + smsReminderList.size() + " 条还款记录需要提醒！");
				for(SmsMessagePojo vo : smsReminderList) {
					smsService.sendSmsForRemiandBeforRepayDate(vo);
				}
			} else {
				Log.error("项目还款日前短信邮件提醒,共有：0 条记录需要提醒！");
			}
			Log.error("项目还款日前短信邮件提醒执行完毕！");
		} catch (Exception e) {
			Log.error("项目还款日前短信邮件提醒报错:", e);
		}
	}
	
	/** 还款日后短信提醒:只至逾期5个工作日内 */
	public void smsAndEmailRemiandAfterRepayDate() {
		Log.error("smsAndEmailRemiandAfterRepayDate:项目逾期还款短信邮件提醒！");
		try {
			Date endTime = new Date();
			Date beforeTime = DateFormatUtil.increaseDay(endTime, -5);
			String beginDate = DateFormatUtil.dateToString(beforeTime, "yyyy-MM-dd");
			String endDate = DateFormatUtil.dateToString(endTime, "yyyy-MM-dd");
			List<SmsMessagePojo> smsReminderList = smsEmailTimerDao.queryRemindListAfterRepayDate(beginDate, endDate);
			if(smsReminderList != null && smsReminderList.size() > 0) {
				Log.error("项目逾期还款短信邮件提醒,共有：" + smsReminderList.size() + " 条还款记录需要提醒！");
				for(SmsMessagePojo vo : smsReminderList) {
					smsService.sendSmsForRemiandAfterRepayDate(vo);
				}
			} else {
				Log.error("项目逾期还款短信邮件提醒,共有：0 条记录需要提醒！");
			}
			Log.error("项目逾期还款短信邮件提醒执行完毕！");
		} catch (Exception e) {
			Log.error("项目逾期还款短信邮件提醒报错:", e);
		}
	}
	
	/** 邮件发送计划相关定时任务相关 */
	public void emailSendPlan() {
		emailSendService.handleEmailSendPlan();
	}
	
	/** 短信发送计划相关定时任务相关 */
	public void smsSendPlan() {
		smsSendService.handleSmsSendPlan();
	}
	
	/** 客户生日邮件 */
	public void birthdayWishesEmailToUser() {
		String status = SmsEmailCache.getSmsEmailSwitchStatus("customer_birthday_email");
		if("1".equals(status)) {
			emailSendService.sendBirthdayWishesEmailToUser();
		} else {
			LOG.error("客户生日邮件提醒邮件开关没启用！");
		}
	}
	
	/** 客户生日短信 */
	public void birthdayWishesSmsToUser() {
		String status = SmsEmailCache.getSmsEmailSwitchStatus("customer_birthday_sms");
		if("1".equals(status)) {
			smsSendService.sendBirthdayWishesSmsToUser();
		} else {
			LOG.error("客户生日短信提醒开关没启用！");
		}
	}
	
	/** 用户基础信息统计报表数据更新
	 *  包括：用户id、真实姓名、手机号码、邮箱、投资金额、首投时间、最近一次投资时间、投资次数、注册时间、最近登录时间  */
	public void updateUserBasicStatisticsInfo() {
		LOG.error("开始更新用户基本统计信息!");
		userBasicStatisticsService.syncNewAddUserInfo();
		
		userBasicStatisticsService.updateSyncedUserInfo();
		
		userBasicStatisticsService.updateInvestMoneyAndNUm();
		
		userBasicStatisticsService.updateLatestLoginTime();
		LOG.error("更新用户基本统计信息结束!");
	}
}