package com.hc9.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.log.LOG;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.SmsEmailTimerDao;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.EmaiLoansignInfo;
import com.hc9.model.MsgSendInfo;
import com.hc9.model.MsgSendPlanInfo;
import com.hc9.model.Payuser;
import com.hc9.model.UserBirthdayLinkInfo;

/** 邮件发送服务 */
@Service
public class EmailSendService {
	@Resource
	private EmailService emailService;
	
	@Resource
	private SmsEmailTimerDao smsEmailTimerDao;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	SmsEmailComonService smsEmailComonService;
	
	/** 处理邮件发送计划相关 */
	public void handleEmailSendPlan() {
		List<MsgSendPlanInfo> sendPlanList = smsEmailTimerDao.querySmsEmailSendPlanList(2);
		for(MsgSendPlanInfo plan : sendPlanList) {
			Long sendPlanId = plan.getId();
			if(smsEmailComonService.validSendPlan(sendPlanId)) {
				String title = plan.getMsgTitle();
				String templateContent = plan.getTemplateContent();
				List<MsgSendInfo> userList = smsEmailTimerDao.queryReceiveUserListOfSendPlan(sendPlanId, 2);
				if(userList != null && userList.size() > 0) {
//					String emailArr[] = new String[userList.size()];
					for(int i = 0; i < userList.size(); i++) {
//						emailArr[i] = userList.get(i).getSmsEmailNo();
						try {
							emailService.sendAdEmail(title, templateContent, userList.get(i).getSmsEmailNo());
							LOG.error("邮件发送计划 " + sendPlanId + " 对应的模板内容发送成功！");
							String successTime = DateUtils.format("yyyy-MM-dd HH:mm:ss");
							/** 更新已发送的邮件的状态为已发送 */
							for(MsgSendInfo vo : userList) {
								smsEmailTimerDao.updateEmailSendStatus(vo.getId(), successTime);
							}
							/** 修改发送计划的状态 */
							smsEmailTimerDao.checkAndUpdateSendPlanStatus(2, sendPlanId);
						} catch (Exception e) {
							LOG.error("邮件发送计划 " + sendPlanId + " 对应的模板内容发送失败！", e);
						}
					}
					LOG.error("待发邮件内容：" + templateContent);

				} else {
					LOG.error("发送计划 " + sendPlanId + " 下没有短信收件人信息！");
				}
			}
		}
	}
	
	/** 在投资人的回款金额到达宝付账户时进行邮件通知 */
	public void sendReturnMoneyEmailNotify(final List<Payuser> userList, final String loansignName) {
			if(userList != null && userList.size() > 0) {
				String status = SmsEmailCache.getSmsEmailSwitchStatus("invest_return_money_email");
				if("1".equals(status)) {
					new Thread(
							new Runnable() {
								public void run() {
									Map<String, Object> map = new HashMap<String, Object>();
									for(Payuser payuser : userList) {
										String userId = payuser.getId();//投资人的用户id
										Double returnMoney = payuser.getAmount();//个人得到的钱
										map.clear();
										Userbasicsinfo investUser = userbasicsinfoService.queryUserById(Long.valueOf(userId));
										String userName = investUser.getName();
										if(StringUtil.isNotBlank(userName)) {
											List<EmaiLoansignInfo> loanList = smsEmailTimerDao.queryNewestThreeLoansign();
											String email = investUser.getUserrelationinfo().getEmail();
											if(StringUtil.isNotBlank(email)) {
												map.put("userName", userName);
												map.put("loansignName", loansignName);
												map.put("returnMoney", "" + returnMoney);
												map.put("loanList", loanList);
												try {//
													String[] msg = emailService.getEmailResources("returnmoney/return-money-notify.flt", map);
													LOG.error("回款通知待发邮箱标题："+msg[0] +"，待发邮箱内容：" + msg[1]);
													emailService.sendAdEmail(msg[0], msg[1], email);
												} catch(Exception e) {
													LOG.error("给用户" + userId + "发送还款通知邮件失败：", e);
												}
											}
										}
									}
								} 
							}
			        ).start();
				}
			}
		}
	
	/** 客户生日祝福邮件：读取客户的身份证信息中的第7-14位数字为出生日期 */
	public void sendBirthdayWishesEmailToUser() {
		List<UserBirthdayLinkInfo> userList = smsEmailTimerDao.queryToNotifyEmailList();
		Map<String, String> map = new HashMap<String, String>();
		if(userList != null && userList.size() > 0) {
			for(UserBirthdayLinkInfo user : userList) {
				try {
					if(StringUtil.isNotBlank(user.getEmail())) {
						map.clear();
						map.put("userName", user.getUserName());
						map.put("month", user.getBirthday().substring(5, 7));
						map.put("day", user.getBirthday().substring(8, 10));
						String[] msg = emailService.getEmailResources("birthday/user-birtaday-notify.flt", map);
						LOG.error("待发邮箱标题："+msg[0] +"，待发邮箱内容：" + msg[1]);
						emailService.sendAdEmail(msg[0], msg[1], user.getEmail());
					}
				} catch(Exception e) {
					LOG.error("给用户发送生日祝福失败：", e);
				}
			}
		} else {
			LOG.error("未查到需要生日邮件提醒的相关用户信息！");
		}
	}
}