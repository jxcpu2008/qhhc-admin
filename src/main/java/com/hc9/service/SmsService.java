package com.hc9.service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.log.LOG;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.FreeMarkerUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.MsgReminder;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Validcodeinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.InvestSmsVo;
import com.hc9.model.SmsMessagePojo;
import com.hc9.service.sms.hy.HyTriggerSmsService;
import com.hc9.service.sms.mw.MwMarketingSmsService;
import com.hc9.service.sms.mw.MwTriggerSmsService;
import com.hc9.service.sms.wd.IndustryWdSms;
import com.hc9.service.sms.ym.BaseSmsService;
import com.hc9.service.sms.ym.EmayMarketingSmsService;

import freemarker.template.TemplateException;

/**
 * 短信服务
 * 
 * @author frank
 * 
 */
@Service
public class SmsService {
	
	private static final Logger logger = Logger.getLogger(SmsService.class);

	/**
	 * BaseSmsService
	 */
	@Resource
	BaseSmsService baseSmsService;
	
	@Resource
	MwTriggerSmsService mwTriggerSmsService;
	
	@Resource
	HyTriggerSmsService hyTriggerSmsService;
	
	@Resource
	IndustryWdSms industryWdSmsService;
	
	@Resource
	EmayMarketingSmsService emayMarketingSmsService;
	
	@Resource
	MwMarketingSmsService mwMarketingSmsService;
	/**
	 * 注入EmailService
	 */
	@Resource
	private EmailService emailService;

	@Resource
	ValidcodeInfoService validcodeInfoService;
	
	@Autowired
	private LoanSignQuery loanSignQuery;

	@Resource
	private HibernateSupport dao;
	/**
	 * 得到短信资源
	 * 
	 * @param modelName
	 *            资源名称
	 * @param map
	 *            待填充字符
	 * @return 填充后的短信文本
	 * @throws IOException
	 *             文件读取异常
	 * @throws TemplateException
	 *             文件解析异常
	 */
	public String getSmsResources(String modelName, Map map)
			throws IOException, TemplateException {

		return FreeMarkerUtil.execute("config/marker/sms/" + modelName,
				Constant.CHARSET_DEFAULT, map);

	}
	
	
	/**
	 * 得到短信资源 手机APP
	 * 
	 * @param modelName
	 *            资源名称
	 * @param map
	 *            待填充字符
	 * @return 填充后的短信文本
	 * @throws IOException
	 *             文件读取异常
	 * @throws TemplateException
	 *             文件解析异常
	 */
	public String getAppSmsResources(String modelName, Map map)
			throws IOException, TemplateException {
		return FreeMarkerUtil.execute("config/marker/appsms/" + modelName,
				Constant.CHARSET_DEFAULT, map);

	}
	

	/**
	 * 发送短信 支持短信群发
	 * 
	 * @param content
	 *            内容
	 * @param telNos
	 *            接收号码
	 * @return 短信发送状态[是否成功，返回值，失败信息]
	 * @throws Exception
	 *             异常
	 */
	public Integer sendSMS(String content, String... telNos) throws Exception {
		return baseSmsService.sendSMS(content, telNos);
	}

	/**
	 * 发送验证码
	 * 
	 * @param userbasicsinfo
	 *            当前操作用户信息
	 * @return 短信发送状态[是否成功，返回值，失败信息]
	 * @throws Exception
	 */
	public Integer sendCode(Userbasicsinfo userbasicsinfo,
			Validcodeinfo validcode, String urlcase, String phone)
			throws Exception {
		// 获取验证码
		String numberCode = StringUtil.getvalidcode();
		Map<String, String> map = new HashMap<String, String>();

		if (null == userbasicsinfo.getUserName()) {
			map.put("user", userbasicsinfo.getNickname());
		} else {
			map.put("user", userbasicsinfo.getUserName());
		}
		map.put("code", numberCode);
		String content = this.getSmsResources("check-code.ftl", map);

		// 判断当前操作用户是否发送过短信
		if (null != validcode) {
			if (null == validcode.getSmsCode()) {
				int sms = baseSmsService.sendSMS(content, phone);
				if (sms==0) {
					validcode.setSmsCode(numberCode);
					validcode.setSmsPhone(phone);
					validcode.setSmsagainTime(System.currentTimeMillis()
							+ Constant.MILLISECONDS);
					validcode.setSmsoverTime(Constant.MILLISECONDS);
					validcodeInfoService.update(validcode);
				}
				return sms;
			} else {
				// 再次发送短信的时间是否小于当前时间
				if (validcode.getSmsagainTime() > System.currentTimeMillis()
						+ Constant.MILLISECONDS) {
					return 1;
				} else {
					int sms;
					if (urlcase.equals("bid")) {
						sms = baseSmsService.sendSMS(content, phone);
						if (sms==0) {
							validcode.setSmsCode(numberCode);
							validcode.setSmsPhone(phone);
							validcode.setSmsagainTime(System
									.currentTimeMillis()
									+ Constant.MILLISECONDS);
							validcode.setSmsoverTime(Constant.MILLISECONDS);
							validcodeInfoService.update(validcode);
						}
					} else {
						sms = baseSmsService.sendSMS(content, userbasicsinfo
								.getUserrelationinfo().getPhone());
						if (sms==0) {
							validcode.setSmsCode(numberCode);
							validcode.setSmsPhone(userbasicsinfo
									.getUserrelationinfo().getPhone());
							validcode.setSmsagainTime(System
									.currentTimeMillis()
									+ Constant.MILLISECONDS);
							validcode.setSmsoverTime(Constant.MILLISECONDS);
							validcodeInfoService.update(validcode);
						}
					}

					return sms;
				}
			}
		} else {
			int sms = baseSmsService.sendSMS(content, userbasicsinfo
					.getUserrelationinfo().getPhone());
			if (sms==0) {
				Validcodeinfo vali = new Validcodeinfo();
				vali.setSmsCode(numberCode);
				vali.setSmsPhone(userbasicsinfo.getUserrelationinfo()
						.getPhone());
				vali.setUserbasicsinfo(userbasicsinfo);
				vali.setSmsagainTime(System.currentTimeMillis()
						+ Constant.MILLISECONDS);
				vali.setSmsoverTime(Constant.MILLISECONDS);
				validcodeInfoService.save(vali);
			}
			return sms;
		}
	}
	
	/** 满标放款后立即提醒 
	 *  @param loansignId 项目id
	 *   */
	public void sendSmsForFullBidGrantMoney(final Loansign loan) {
		new Thread(
				new Runnable() {
					public void run() {
						int trigger=Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
						String smsSwitchStatus = SmsEmailCache.getSmsEmailSwitchStatus("borrow_money_arrive_sms");
						String emailSwitchStatus = SmsEmailCache.getSmsEmailSwitchStatus("borrow_money_arrive_email");
						Long loansignId = loan.getId();
						Map<String, String> map = new HashMap<String, String>();
						map.put("loansignName", loan.getName());
						map.put("issueLoan", "" + loan.getIssueLoan());
						/**1-店铺  2-项目 3-天标 4-债权转让*/
						Integer type = loan.getType();
						if(type.intValue() == 3) {//天标
							map.put("reMonth", "" + loan.getRemonth() + "天");
						} else {
							map.put("reMonth", "" + loan.getRemonth() + "个月");
						}
						//预计还款日期2015-8-24-lkl
						String preRepayDate=loanSignQuery.queryPreRepayDate(loan.getId());
						map.put("preRepayDate", preRepayDate);
						
						/** 客户的手机邮箱信息 */
						MsgReminder customerReminder = new MsgReminder();
						List<MsgReminder> customerReminderList = loanSignQuery.queryMsgReminderListByLoanSignId(loansignId, 1);
						if(customerReminderList != null && customerReminderList.size() > 0) {
							customerReminder = customerReminderList.get(0);
							LOG.error("满标放款正给客户 ：" 
									+ customerReminder.getReceiverName() + " 的 " + customerReminder.getReceiverPhone() 
									+ " 发送短信，所属项目id:" + loan.getId());
							/** 发短息 */
							if("1".equals(smsSwitchStatus)) {
								String receiverPhone = customerReminder.getReceiverPhone();
								if(StringUtil.isNotBlank(receiverPhone)) {
									String key = "STR:FULL:BID:REMIND:" + loan.getId() + ":" + receiverPhone;
									if(!RedisHelper.isKeyExist(key)) {//满标只发送一次提示短信
										try {
											map.put("customerName", customerReminder.getReceiverName());
											String content = getSmsResources("fullBid-grantMoney-customer.ftl", map);
											LOG.error("待发短信内容：" + content);
											chooseSmsChannel(trigger, content, customerReminder.getReceiverPhone());
											RedisHelper.setWithExpireTime(key, "1", 2 * 24 * 60 * 60);
										} catch (Exception e) {
											LOG.error("给客户发送项目  " + loansignId + " 的短信通知时出现异常！", e);
										}
									}
								}
							}
							
							/** 发邮件 */
							if("1".equals(emailSwitchStatus)) {
								String receiverEmail = customerReminder.getReceiverEmail();
								if(StringUtil.isNotBlank(receiverEmail)) {
									try {
										map.put("customerName", customerReminder.getReceiverName());
										String[] msg = emailService.getEmailResources("fullBid-email-customer.ftl",
												map);
										LOG.error("待发邮箱标题："+msg[0] +"，待发邮箱内容：" + msg[1]);
										emailService.sendEmail(msg[0], msg[1], receiverEmail);
									} catch (Exception e) {
										LOG.error("给客户发送项目  " + loansignId + " 的邮箱通知时出现异常！", e);
									}
								}
							}
						} else {
							LOG.error("满标放款无符合条件的客户需要发送短息！");
						}
						
						/** 内部员工的短信和邮件的联系方式 */
						List<MsgReminder> employeeReminderList = loanSignQuery.queryMsgReminderListByLoanSignId(loansignId, 2);
						if(employeeReminderList != null && employeeReminderList.size() > 0) {
							boolean flag = false;
							for(MsgReminder vo : employeeReminderList) {
								if(StringUtil.isNotBlank(vo.getReceiverPhone())) {
									flag = true;
								}
							}
							if(flag) {
								try {
									String key = "STR:FULL:BID:REMIND:EMPLOYEE:" + loan.getId();
									if(!RedisHelper.isKeyExist(key)) {//满标只发送一次提示短信
										if("1".equals(smsSwitchStatus)) {
											String content = getSmsResources("fullBid-grantMoney-employee.ftl", map);
											
											for(MsgReminder mr : employeeReminderList) {//短信群发接口不稳定，暂时一个手机号发一次
												Log.error("满标放款正给员工 ：" + mr.getReceiverPhone() + " 发送短信，所属项目id:" + loan.getId()
												+ "，待发短信内容：" + content);		
												
												if(trigger==1){
													baseSmsService.sendSMS(content, mr.getReceiverPhone());
												}
												if(trigger==2){
													mwTriggerSmsService.sendSMSBySameMessage(mr.getReceiverPhone(), content);
												}
												if(trigger==3){
													hyTriggerSmsService.sendSMS(content, mr.getReceiverPhone());
												}
											}
										}
										
										// 给员工发邮件
										if("1".equals(emailSwitchStatus)) {
											for(MsgReminder mr : employeeReminderList) {//短信群发接口不稳定，暂时一个手机号发一次
												map.put("customerName", mr.getReceiverName());
												String[] msg = emailService.getEmailResources("fullBid-email-employee.ftl", map);
												LOG.error("待发邮箱标题："+msg[0] +"，待发邮箱内容：" + msg[1]);
												emailService.sendEmail(msg[0], msg[1], mr.getReceiverEmail());
											}
										}
										RedisHelper.setWithExpireTime(key, "1", 2 * 24 * 60 * 60);
									}
								} catch (Exception e) {
									LOG.error("给客户发送项目  " + loansignId + " 的短信通知时出现异常！", e);
								}
							}
						} else {
							LOG.error("满标放款无符合条件的员工需要发送短息！");
						}
						
						//给投资人的发送短信和邮件2015-8-24-lkl
						List<InvestSmsVo> investSmsVoList = loanSignQuery.queryMsgUser(loan.getId());
						if(investSmsVoList !=null && investSmsVoList.size()>0){
								try {
									String key = "STR:FULL:BID:LOANRECORD:INVESTOR:" + loan.getId();
									if(!RedisHelper.isKeyExist(key)) {//满标只发送一次提示短信
										if("1".equals(smsSwitchStatus)) {
											for(InvestSmsVo investSmsVo : investSmsVoList) {//短信群发接口不稳定，暂时一个手机号发一次
												String mobilePhone = investSmsVo.getMobilePhone();
												if(StringUtil.isNotBlank(mobilePhone)) {
													map.put("customerName", investSmsVo.getCustomerName());
													map.put("tenderMoney", investSmsVo.getTenderMoney() + "");
													String content = getSmsResources("fullBid-grantMoney-investor.ftl", map);
													LOG.error("满标放款正给投资人 ：" + investSmsVo.getMobilePhone() + 
															" 发送短信，所属项目id:" + loan.getId() + "，待发短信内容：" + content);
													if(trigger==1){
														baseSmsService.sendSMS(content, mobilePhone);
													}
													if(trigger==2){
														mwTriggerSmsService.sendSMSBySameMessage(mobilePhone, content);
													}
													if(trigger==3){
														hyTriggerSmsService.sendSMS(content, mobilePhone);
													}
												}
											}
										}
										
										// 给员工发邮件
										if("1".equals(emailSwitchStatus)) {
											for(InvestSmsVo investSmsVo : investSmsVoList) {//短信群发接口不稳定，暂时一个手机号发一次
												if(StringUtil.isNotBlank(investSmsVo.getEmail())) {
													map.put("customerName", investSmsVo.getCustomerName());
													map.put("tenderMoney", investSmsVo.getTenderMoney() + "");
													String[] msg = emailService.getEmailResources("fullBid-email-investor.ftl", map);
													LOG.error("待发邮箱标题："+msg[0] +"，待发邮箱内容：" + msg[1]);
													emailService.sendEmail(msg[0], msg[1], investSmsVo.getEmail());
												}
											}
										}
										RedisHelper.setWithExpireTime(key, "1", 2 * 24 * 60 * 60);
									}
								} catch (Exception e) {
									LOG.error("给投资人发送项目  " + loansignId + " 的短信通知时出现异常！", e);
							}
						}else {
							LOG.error("满标放款中投资人无符合需要发送短信！");
						}
					} 
				}
        ).start();	
	}
	
	/** 还款前5天提前提醒
	 *  @param loansignId 项目id
	 *   */
	public void sendSmsForRemiandBeforRepayDate(final SmsMessagePojo smsMessagePojo) {
		new Thread(
				new Runnable() {
					public void run() {
						int trigger=Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
						SmsService smsService=new SmsService();
						String smsSwitchStatus = SmsEmailCache.getSmsEmailSwitchStatus("repay_money_remind_sms");
						
						String emailSwitchStatus = SmsEmailCache.getSmsEmailSwitchStatus("repay_money_remind_email");
						Long loansignId = smsMessagePojo.getLoansignId();
						String preRepayDate = smsMessagePojo.getPreRepayDate();//预还款日期
						Map<String, String> map = new HashMap<String, String>();
						map.put("loansignName", smsMessagePojo.getLoansignName());
						map.put("issueLoan", "" + smsMessagePojo.getIssueLoan());
						map.put("reMonth", "" + smsMessagePojo.getRemonth());
						map.put("preRepayDate", preRepayDate);
						map.put("repayMoney", "" + smsMessagePojo.getRepayMoney());
						
						/** 客户的手机邮箱信息 */
						MsgReminder customerReminder = new MsgReminder();
						List<MsgReminder> customerReminderList = loanSignQuery.queryMsgReminderListByLoanSignId(loansignId, 1);
						if(customerReminderList != null && customerReminderList.size() > 0 && smsMessagePojo.getRepayMoney() != 0.0) {
							customerReminder = customerReminderList.get(0);
							Log.error("正给客户 ：" 
							+ customerReminder.getReceiverName() + " 的 " + customerReminder.getReceiverPhone() 
							+ " 发送短信，所属项目id:" + smsMessagePojo.getLoansignId() 
							+ ",还款记录id为：" + smsMessagePojo.getRepaymentrecordId());
							/** 发短息 */
							if("1".equals(smsSwitchStatus)) {
								String receiverPhone = customerReminder.getReceiverPhone();
								if(StringUtil.isNotBlank(receiverPhone)) {
									try {
										map.put("customerName", customerReminder.getReceiverName());
										String content = getSmsResources("repayBefore-remind-customer.ftl", map);
										LOG.error("待发短信内容：" + content);
										
										chooseSmsChannel(trigger, content, customerReminder.getReceiverPhone());
										/** 记录短息提醒次数 */
										updateRemindSMSCount(smsMessagePojo);
									} catch (Exception e) {
										LOG.error("给客户发送项目  " + loansignId + " 的短信通知时出现异常！", e);
									}
								}
							}
							/** 发邮件 */
							if("1".equals(emailSwitchStatus)) {
							String receiverEmail = customerReminder.getReceiverEmail();
								if(StringUtil.isNotBlank(receiverEmail)) {
									try {
										map.put("customerName", customerReminder.getReceiverName());
										String[] msg = emailService.getEmailResources("beforeRepayment-custEmail.ftl",
												map);
										LOG.error("待发邮箱标题："+msg[0] +"，待发邮箱内容：" + msg[1]);
										emailService.sendEmail(msg[0], msg[1], receiverEmail);
										/** 记录邮箱提醒次数 */
										updateRemindEmailCount(smsMessagePojo);
									} catch (Exception e) {
										LOG.error("给客户发送项目  " + loansignId + " 的邮箱通知时出现异常！", e);
									}
								}
							}
						} else {
							Log.error("提前通知无符合条件的客户需要发送短息！");
						}
						
						/** 内部员工的短信和邮件的联系方式 */
						List<MsgReminder> employeeReminderList = loanSignQuery.queryMsgReminderListByLoanSignId(loansignId, 2);
						if(employeeReminderList != null && employeeReminderList.size() > 0 && smsMessagePojo.getRepayMoney() != 0.0) {
							boolean flag = false;
							for(MsgReminder vo : employeeReminderList) {
								if(StringUtil.isNotBlank(vo.getReceiverPhone())) {
									flag = true;
								}
							}
							if(flag) {
								try {
									if("1".equals(smsSwitchStatus)) {
										map.put("customerName", customerReminder.getReceiverName());
										String content = getSmsResources("repayBefore-remind-employee.ftl", map);
										for(MsgReminder mr : employeeReminderList) {
											Log.error("正给员工：" + mr.getReceiverPhone() 
											+ " 发送短信，所属项目id:" + smsMessagePojo.getLoansignId() 
											+ ",还款记录id为：" + smsMessagePojo.getRepaymentrecordId()
											+ "，待发短信内容：" + content);
											if(trigger==1){
												baseSmsService.sendSMS(content, mr.getReceiverPhone());
											}
											if(trigger==2){
												mwTriggerSmsService.sendSMSBySameMessage(mr.getReceiverPhone(), content);
											}
											if(trigger==3){
												hyTriggerSmsService.sendSMS(content, mr.getReceiverPhone());
											}
										}
									}
									
									if("1".equals(emailSwitchStatus)) {
										map.put("customerName", customerReminder.getReceiverName());
										for(MsgReminder mr : employeeReminderList) {
											map.put("employeeName", mr.getReceiverName());
											// 给员工发邮件
											String[] msg = emailService.getEmailResources("beforeRepayment-empEmail.ftl", map);
											LOG.error("待发邮箱标题："+msg[0] +"，待发邮箱内容：" + msg[1]);
											emailService.sendEmail(msg[0], msg[1], mr.getReceiverEmail());
										}
									}
								} catch (Exception e) {
									LOG.error("给员工发送项目  " + loansignId + " 的通知时出现异常！", e);
								}
							}
						} else {
							LOG.error("提前通知无符合条件的员工需要发送短息！");
						}
					} 
				}
        ).start();	
	}
	
	/** 逾期还款提醒
	 *  @param loansignId 项目id
	 *   */
	public void sendSmsForRemiandAfterRepayDate(final SmsMessagePojo smsMessagePojo) {
		new Thread(
				new Runnable() {
					public void run() {
						int trigger=Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
						String smsSwitchStatus = SmsEmailCache.getSmsEmailSwitchStatus("repay_money_remind_sms");
						String emailSwitchStatus = SmsEmailCache.getSmsEmailSwitchStatus("repay_money_remind_email");
						Long loansignId = smsMessagePojo.getLoansignId();
						String preRepayDate = smsMessagePojo.getPreRepayDate();//预还款日期
						Map<String, String> map = new HashMap<String, String>();
						map.put("loansignName", smsMessagePojo.getLoansignName());
						map.put("issueLoan", "" + smsMessagePojo.getIssueLoan());
						map.put("reMonth", "" + smsMessagePojo.getRemonth());
						map.put("preRepayDate", preRepayDate);
						map.put("repayMoney", "" + smsMessagePojo.getRepayMoney());
						
						/** 客户的手机邮箱信息 */
						MsgReminder customerReminder = new MsgReminder();
						List<MsgReminder> customerReminderList = loanSignQuery.queryMsgReminderListByLoanSignId(loansignId, 1);
						if(customerReminderList != null && customerReminderList.size() > 0 && smsMessagePojo.getRepayMoney() != 0.0) {
							customerReminder = customerReminderList.get(0);
							/** 发短息 */
							if("1".equals(smsSwitchStatus)) {
								String receiverPhone = customerReminder.getReceiverPhone();
								if(StringUtil.isNotBlank(receiverPhone)) {
									try {
										map.put("customerName", customerReminder.getReceiverName());
										String content = getSmsResources("repayAfter-remind-customer.ftl", map);
										LOG.error("待发短信内容：" + content);
										chooseSmsChannel(trigger, content, customerReminder.getReceiverPhone());
										/** 记录短息提醒次数 */
										updateRemindSMSCount(smsMessagePojo);
									} catch (Exception e) {
										LOG.error("给客户发送项目  " + loansignId + " 的短信通知时出现异常！", e);
									}
								}
							}
							
							/** 发邮件 */
							if("1".equals(emailSwitchStatus)) {
								String receiverEmail = customerReminder.getReceiverEmail();
								if(StringUtil.isNotBlank(receiverEmail)) {
									try {
										map.put("customerName", customerReminder.getReceiverName());
										String[] msg = emailService.getEmailResources("afterRepayment-custEmail.ftl",
												map);
										LOG.error("待发邮箱标题："+msg[0] +"，待发邮箱内容：" + msg[1]);
										emailService.sendEmail(msg[0], msg[1], receiverEmail);
										/** 记录邮箱提醒次数 */
										updateRemindEmailCount(smsMessagePojo);
									} catch (Exception e) {
										LOG.error("给客户发送项目  " + loansignId + " 的邮箱通知时出现异常！", e);
									}
								}
							}
						} else {
							Log.error("逾期还款通知无符合条件的客户需要发送短息！");
						}
						
						/** 内部员工的短信和邮件的联系方式 */
						List<MsgReminder> employeeReminderList = loanSignQuery.queryMsgReminderListByLoanSignId(loansignId, 2);
						if(employeeReminderList != null && employeeReminderList.size() > 0&& smsMessagePojo.getRepayMoney() != 0.0) {
							boolean flag = false;
							for(MsgReminder vo : employeeReminderList) {
								if(StringUtil.isNotBlank(vo.getReceiverPhone())) {
									flag = true;
								}
							}
							if(flag) {
								try {
									if("1".equals(smsSwitchStatus)) {
										map.put("customerName", customerReminder.getReceiverName());
										String content = getSmsResources("repayAfter-remind-employee.ftl", map);
										for(MsgReminder mr : employeeReminderList) {
											LOG.error("待发短信内容：" + content + ",待接收人号码：" + mr.getReceiverPhone());
											
											if(trigger==1){
												baseSmsService.sendSMS(content, mr.getReceiverPhone());
											}
											if(trigger==2){
												mwTriggerSmsService.sendSMSBySameMessage(mr.getReceiverPhone(), content);
											}
											if(trigger==3){
												hyTriggerSmsService.sendSMS(content, mr.getReceiverPhone());
											}
										}
									}
									
									// 给员工发邮件
									if("1".equals(emailSwitchStatus)) {
										map.put("customerName", customerReminder.getReceiverName());
										for(MsgReminder mr : employeeReminderList) {
											map.put("employeeName", mr.getReceiverName());
											String[] msg = emailService.getEmailResources("afterRepayment-empEmail.ftl", map);
											LOG.error("待发邮箱标题："+msg[0] +"，待发邮箱内容：" + msg[1]);
											emailService.sendEmail(msg[0], msg[1], mr.getReceiverEmail());
										}
									}
								} catch (Exception e) {
									LOG.error("给员工发送项目  " + loansignId + " 的短信通知时出现异常！", e);
								}
							}
						} else {
							LOG.error("逾期还款通知无符合条件的员工需要发送短息！");
						}
					} 
				}
        ).start();
	}
	
	private void updateRemindSMSCount(SmsMessagePojo smsMessagePojo) {
		/** 记录短息提醒次数 */
		Long repaymentrecordId = smsMessagePojo.getRepaymentrecordId();
		String sql = "update repaymentrecord set remindSMSCount=remindSMSCount+1 where id=" + repaymentrecordId;
		dao.executeSql(sql);
	}
	
	private void updateRemindEmailCount(SmsMessagePojo smsMessagePojo) {
		/** 记录短息提醒次数 */
		Long repaymentrecordId = smsMessagePojo.getRepaymentrecordId();
		String sql = "update repaymentrecord set remindEmailCount=remindEmailCount+1 where id=" + repaymentrecordId;
		dao.executeSql(sql);
	}
	
	/**
	 * 选择触发短信通道
	 * @param type
	 * @param content
	 * @param phone
	 * @return
	 */
	public  Integer chooseSmsChannel(int type,String content,String phone){
		LOG.error(" 开始准备发送短信，所使用的渠道类型为： " + type);
		int res=-1;
//		long beginTime = System.currentTimeMillis();
		try {
			if(type == 1) {//亿美
				res=baseSmsService.sendSMS(content, phone);
			} else if(type == 2) {//梦网
				res=mwTriggerSmsService.sendSMSBySameMessage(phone, content);
			} else if(type == 3) {//互亿
				res=hyTriggerSmsService.sendSMS(content, phone);
			} else if(type == 4) {//沃动
				res = industryWdSmsService.sendIndustrySMS(content, phone);
			}
			
		} catch(Exception e) {
			LOG.error("给 " + phone + " 发送短信报错！", e);
		}
//		long sendSeconds = (System.currentTimeMillis() - beginTime) / 1000;
//		String[] arr = phone.split(",");
//		if(sendSeconds > 3 * arr.length) {
//			if(type == 1) {//如果当前短信通道为亿美的话，需要切换成互亿
//				LOG.error("--------短信发送通道从亿美切换成互亿--------");
//				SmsEmailCache.setSmsTriggerSwitch(3);//切换成互亿
//			} else if(type == 3) {//如果当前短信通道为互亿的话，需要切换成亿美
//				LOG.error("--------短信发送通道从互亿切换成亿美--------");
//				SmsEmailCache.setSmsTriggerSwitch(1);//切换成亿美
//			}
//		}
		return res;
	}
	/**
	 * 营销通道选择
	 * @param type
	 * @param content
	 * @param phone
	 * @return
	 */
	public  Integer chooseMarketingSmsChannel(int type,String content,String phone){
		int res=-1;
		if(type==1){
			res=emayMarketingSmsService.sendSMS(content, phone);
		}
		if(type==2){
			res=mwMarketingSmsService.sendSMSBySameMessage(phone, content);
		}
	
		return res;
	}
	
	public void sendSms4UserFirstInvest(final String realName, final String typeName, final String phone, final double cash) {
		final DecimalFormat moneyFormat = new DecimalFormat("#.00");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int trigger = Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
				Map<String, String> map = new HashMap<String, String>();
				map.put("realName", realName);
				map.put("activityTypeName", typeName);
				map.put("bonusCash", moneyFormat.format(cash));
					
				try {
					String content = getSmsResources("grant-cash.ftl", map);
					logger.info("现金发放成功，即将给手机号为：" + phone + "的用户发送短信，短信内容为：" + content);
					chooseSmsChannel(trigger, content, phone);
				} catch (TemplateException e) {
					// TODO Auto-generated catch block
					logger.error("加载首投返现短信模板发生异常！", e);
				} catch (IOException e) {
					logger.error("读取首投返现短信模板发生异常！", e);
				}
			}
			
		}).start();
	}
}
