package com.hc9.service;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.jfree.util.Log;
import org.springframework.stereotype.Service;

import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.dao.SmsEmailTimerDao;
import com.hc9.dao.entity.SmsEmailPlanTime;
import com.hc9.dao.entity.SmsemailSendPlan;

/** 短信邮件发送共用的相关逻辑  */
@Service
public class SmsEmailComonService {
	@Resource
	SmsEmailTimerDao smsEmailTimerDao;
	
	/** 检验发送计划是否需要发送邮件 */
	public boolean validSendPlan(Long sendPlanId) {
		boolean sendFlag = false;
		String sendStatus = SmsEmailCache.getSmsEmailSendPlanStatus(sendPlanId);//发送状态
		if("1".equals(sendStatus) || "2".equals(sendStatus)) {//1：待发送;2、正在发送(还有部分消息未发完)；
			/** 重大节日和特定活动 */
			SmsemailSendPlan smsemailSendPlan = smsEmailTimerDao.querySmsemailSendPlanByPlanId(sendPlanId);
			if(smsemailSendPlan != null) {
				/** 发送类型：1、短信；2、邮件； */
				int sendType = smsemailSendPlan.getSendType().intValue();
				String templateType = smsemailSendPlan.getTemplateType();
				/** 判断开关是否开启 */
				String swithchStatus = "";
				
				if(1== sendType) {//短信
					if("big_holiday".equals(templateType)) {//重大节日
						swithchStatus = SmsEmailCache.getSmsEmailSwitchStatus("customer_holiday_sms");
					} else if("specific_activity".equals(smsemailSendPlan.getTemplateType())) {//特定活动
						swithchStatus = SmsEmailCache.getSmsEmailSwitchStatus("special_activity_sms");
					}
				}
				
				if(2 == sendType) {//邮件
					if("big_holiday".equals(templateType)) {//重大节日
						swithchStatus = SmsEmailCache.getSmsEmailSwitchStatus("customer_holiday_email");
					} else if("specific_activity".equals(smsemailSendPlan.getTemplateType())) {//特定活动
						swithchStatus = SmsEmailCache.getSmsEmailSwitchStatus("special_activity_email");
					}
				}
				
				if("1".equals(swithchStatus)) {
					SmsEmailPlanTime planTime = smsEmailTimerDao.querySmsEmailPlanTimeListByPlanId(sendPlanId);
					if(planTime != null) {
						Date beginDate = DateFormatUtil.stringToDate(planTime.getSendBeginTime(), "yyyy-MM-dd HH:mm:ss");
						Date endDat = DateFormatUtil.stringToDate(planTime.getSendEndTime(), "yyyy-MM-dd HH:mm:ss");
						long beginTime = beginDate.getTime();
						long endTime = endDat.getTime();
						
						if(Calendar.getInstance().getTimeInMillis() >= beginTime 
								&& Calendar.getInstance().getTimeInMillis() <= endTime) {
							sendFlag = true;
						}
					} else {
						sendFlag = true;
					}
				} else {
					Log.error(sendPlanId + " 对应开关没启用！");
				}
			}
		}
		return sendFlag;
	}
}
