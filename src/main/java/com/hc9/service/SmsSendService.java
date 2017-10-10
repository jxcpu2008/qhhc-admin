package com.hc9.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.log.LOG;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.SmsEmailTimerDao;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.MsgSendInfo;
import com.hc9.model.MsgSendPlanInfo;
import com.hc9.model.Payuser;
import com.hc9.model.UserBirthdayLinkInfo;
import com.hc9.service.sms.hy.HyTriggerSmsService;
import com.hc9.service.sms.mw.MwMarketingSmsService;
import com.hc9.service.sms.mw.MwTriggerSmsService;
import com.hc9.service.sms.ym.BaseSmsService;

/** 短信发送服务 */
@Service
public class SmsSendService {
	@Resource
	SmsService smsService;
	
	@Resource
	BaseSmsService baseSmsService;
	
	@Resource
	MwMarketingSmsService marketingSmsService;
	
	@Resource
	MwTriggerSmsService mwTriggerSmsService;
	
	@Resource
	HyTriggerSmsService hyTriggerSmsService;
	
	@Resource
	SmsEmailTimerDao smsEmailTimerDao;
	
	@Resource
	UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	SmsEmailComonService smsEmailComonService;
	
	
	/** 处理短信发送计划相关 */
	public void handleSmsSendPlan() {
		List<MsgSendPlanInfo> sendPlanList = smsEmailTimerDao.querySmsEmailSendPlanList(1);
		for(MsgSendPlanInfo plan : sendPlanList) {
			Long sendPlanId = plan.getId();
			if(smsEmailComonService.validSendPlan(sendPlanId)) {
				String templateContent = plan.getTemplateContent();
				List<MsgSendInfo> userList = smsEmailTimerDao.queryReceiveUserListOfSendPlan(sendPlanId, 1);
				if(userList != null && userList.size() > 0) {
					String phoneArr[] = new String[userList.size()];
					for(int i = 0; i < userList.size(); i++) {
						phoneArr[i] = userList.get(i).getSmsEmailNo();
					}
					LOG.error("待发短信内容：" + templateContent);
					//获取营销短信状态
					int marketing=Integer.valueOf(SmsEmailCache.getSmsMarketingChannel());
					try {
						if(marketing==1){
							baseSmsService.sendSMS(templateContent, phoneArr);
						}
						if(marketing==2){
							String phones="";
							for(int i=0;i<phoneArr.length;i++){
								phones+=phoneArr[i]+",";
							}
							phones=phones.substring(0, phones.length()-1);
							marketingSmsService.sendSMSBySameMessage(phones, templateContent);
						}
						LOG.error("短信发送计划 " + sendPlanId + " 对应的模板内容发送成功！");
						String successTime = DateUtils.format("yyyy-MM-dd HH:mm:ss");
						/** 更新已发送的邮件的状态为已发送 */
						for(MsgSendInfo vo : userList) {
							smsEmailTimerDao.updateSmsSendStatus(vo.getId(), successTime);
						}
						/** 修改发送计划的状态 */
						smsEmailTimerDao.checkAndUpdateSendPlanStatus(1, sendPlanId);
					} catch (Exception e) {
						LOG.error("短信发送计划 " + sendPlanId + " 对应的模板内容发送失败！", e);
					}
				} else {
					LOG.error("发送计划 " + sendPlanId + " 下没有短信收件人信息！");
				}
			}
		}
	}
	
	/** 新标短信通知 */
	public boolean sendNewLoansignSmsNotify(Loansign loansign) {
		boolean result = false;
		List<String> mobilePhoneList = smsEmailTimerDao.queryAllUserMobileList();
		if(mobilePhoneList != null && mobilePhoneList.size() > 0) {
			double yearRate = Arith.round(Arith.add(loansign.getPrioRate(), loansign.getPrioAwordRate()), 2); //总计支出
			Map<String, String> map = new HashMap<String, String>();
			map.put("yearRate", "" + (int) Arith.mul(yearRate, 100));
			map.put("loanUnit", "" + loansign.getLoanUnit());
			String phoneArr[] = new String[mobilePhoneList.size()];
			for(int i = 0; i < mobilePhoneList.size(); i++) {
				phoneArr[i] = mobilePhoneList.get(i);
			}
			//获取营销短信状态
			int marketing=Integer.valueOf(SmsEmailCache.getSmsMarketingChannel());

			try {
				String content = smsService.getSmsResources("newloansign/new-loansign-notify.flt", map);
				LOG.error("待发短信内容：" + content);
				if(marketing==1){
					baseSmsService.sendSMS(content, phoneArr);
				}
				if(marketing==2){
					int loop=phoneArr.length/100;
					for(int j=0;j<loop;j++){
//						marketingSmsService.sendSMSBySameMessage(phones, content);
						String phones="";
						for(int i=0;i<phoneArr.length;i++){
							phones+=phoneArr[i]+",";
						}
						phones=phones.substring(0, phones.length()-1);
						if(marketing==1){
							baseSmsService.sendSMS(content, phones);
						}
						if(marketing==2){

							marketingSmsService.sendSMSBySameMessage(phones, content);
						}
					}


				}
//				baseSmsService.sendSMS(content, phoneArr);
				result = true;
			} catch (Exception e) {
				LOG.error("新标短信提醒报错，标d：" + loansign.getId(), e);
			}
		}
		return result;
	}
	
	/** 在投资人的回款金额到达宝付账户时进行短信通知 */
	public void sendReturnMoneySmsNotify(final List<Payuser> userList,final String loansignName) {
		if(userList != null && userList.size() > 0) {
			String status = SmsEmailCache.getSmsEmailSwitchStatus("invest_return_money_sms");
			if("1".equals(status)) {
				new Thread(
					new Runnable() {
						public void run() {
							Map<String, String> map = new HashMap<String, String>();
							int trigger=Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
							Map<String , Double> repaymentUserMap = new HashMap<String, Double>();
							for(Payuser payuser : userList) {
								String userId = payuser.getId();//投资人的用户id
								Double returnmoney = payuser.getAmount();//个人得到的钱
								if(!repaymentUserMap.containsKey(userId)) {
									repaymentUserMap.put(userId, returnmoney);
								} else {
									Double investMoney = repaymentUserMap.get(userId);
									Double totalMoney = Arith.add(investMoney, returnmoney);
									repaymentUserMap.put(userId, totalMoney);
								}
							}
							for(Map.Entry<String , Double> entry : repaymentUserMap.entrySet()) {
								String userId = entry.getKey();//投资人的用户id
								Double returnmoney = entry.getValue();//个人得到的钱
								map.clear();
								try {
									Userbasicsinfo investUser = userbasicsinfoService.queryUserById(Long.valueOf(userId));
									String userName = investUser.getName();
									if(StringUtil.isNotBlank(userName)) {
										String phone = investUser.getUserrelationinfo().getPhone();
										if(StringUtil.isNotBlank(userName)) {
											map.put("userName", userName);
											map.put("returnMoney", "" + returnmoney);
											map.put("loansignName", loansignName);
											String content = smsService.getSmsResources("returnmoney/return-money-notify.flt", map);
											LOG.error("回款通知待发送短息内容：" + content);
											smsService.chooseSmsChannel(trigger, content, phone);
										}
									}
								} catch(Exception e) {
									LOG.error("给用户发送回款失败：", e);
								}
							}
						} 
					}
				).start();
			}
		}
	}
	
	/** 客户生日祝福短信：读取客户的身份证信息中的第7-14位数字为出生日期 */
	public void sendBirthdayWishesSmsToUser() {
		List<UserBirthdayLinkInfo> userList = smsEmailTimerDao.queryToNotifyMobilePhoeList();
		Map<String, String> map = new HashMap<String, String>();
		int marketing=Integer.valueOf(SmsEmailCache.getSmsMarketingChannel());
		if(userList != null && userList.size() > 0) {
			for(UserBirthdayLinkInfo user : userList) {
				try {
					map.clear();
					map.put("userName", user.getUserName());
					// 5、6月份活动用户生日提醒不需要这些字段
//					map.put("month", user.getBirthday().substring(5, 7));
//					map.put("day", user.getBirthday().substring(8, 10));
					String content = smsService.getSmsResources("birthday/user-birtaday-notify.flt", map);
					LOG.error("待发生用户生日祝福短信内容：" + content);
					smsService.chooseMarketingSmsChannel(marketing, content, user.getMobilePhoe());
				} catch(Exception e) {
					LOG.error("给用户发送生日祝福失败：", e);
				}
			}
		} else {
			LOG.error("未查到需要发送生日短信祝福的相关用户信息！");
		}
	}
}
