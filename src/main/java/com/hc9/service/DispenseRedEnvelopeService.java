package com.hc9.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc9.common.log.LOG;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.DateUtil;
import com.hc9.dao.entity.AppMessagePush;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;

import freemarker.template.TemplateException;

@Service
public class DispenseRedEnvelopeService {

	@Resource
	private SmsService smsService;
	@Resource
	private HibernateSupport dao;
	
	@Autowired
	private IMessagePushManageService messagePushService;
	
	/**
	 * 红包发放处理过程
	 * @param user 发放的用户
	 * @param phone 用户手机
	 * @param admin 操作人员姓名
	 * @param bonusMoney 红包金额
	 * @param lowestUseMoney 最低使用金额
	 * @param sourceType 红包来源
	 * @param effectMonth 有效期
	 */
	public void dispenseRedEnvelopeProgress(Userbasicsinfo user, String phone,String admin,double bonusMoney, double lowestUseMoney, int sourceType, int effectMonth){
		String receiveTime = DateUtil.format("yyyy-MM-dd HH:mm:ss");//接收时间
		String beginTime = DateUtil.format("yyyy-MM-dd");//红包开始时间
		String endTime = DateUtil.getSpecifiedMonthAfter(beginTime, effectMonth);//红包到期时间
		//保存红包
		saveRedEnvelope(user.getId(),bonusMoney,lowestUseMoney,sourceType,receiveTime,beginTime,endTime);
		//发送短信
		String content=sendSms(user.getName(),String.valueOf(bonusMoney), phone);
		content=(content.substring(content.indexOf("】")+1, content.length()-1));
		String title="红包到账";
		//保存站内信息
		saveUserMessage(content, receiveTime, endTime, bonusMoney, title, user.getId());
		//推送通知
		pushNotification(content, title, phone, admin);
	}
	/**
	 * 保存红包
	 * 
	 * @param userId
	 *            领取红包的用户id
	 * @param bonusMoney
	 *            红包金额
	 * @param lowestUseMoney
	 *            最低使用门槛
	 * @param sourceType
	 *            红包来源 来源类型：1、投资；2、注册；3、奖励；4、抽奖；5、老客户送红包； 6，38.以后作为节假日用,7.补发调整
	 *            8、四月活动 9、关注微信 10、充值；11、生日礼包； 12、周周惊喜；
	 * @param effectMonth
	 *            有效期
	 * @param
	 * */
	public void saveRedEnvelope(long userId, double bonusMoney, double lowestUseMoney, int sourceType, String receiveTime,String beginTime,String endTime) {
		String sql = "insert into redenvelopedetail"
				+ "(userId,money,lowestUseMoney,receiveTime,beginTime,endTime,useFlag,sourceType) "
				+ "values(?,?,?,?,?,?,0,?)";
		dao.executeSql(sql, userId, bonusMoney, lowestUseMoney, receiveTime, beginTime, endTime, sourceType);
	}

	/**
	 * 
	 * @param name
	 * @param money
	 * @param phone
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public String sendSms(String name,String money, String phone) {
		
		// 短信提醒
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		map.put("money", money);
		String content="";
		try {
			content = smsService.getSmsResources("dispense-redenvelope.ftl", map);
			content=content.substring(1, content.length()-1);
			int trigger = Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
			smsService.chooseSmsChannel(trigger, content, phone);
		} catch (Exception e) {
			LOG.error("红包发放提醒短信发送失败",e);
		} 

		return content;
	}
	/**
	 * 保存用户站内信息
	 * @param context
	 * @param receivetime
	 * @param expireTime
	 * @param money
	 * @param title
	 * @param user_id
	 */
	public void saveUserMessage(String context,String receivetime,String expireTime,double money,String title,Long user_id){
		String sql="insert into usermessage(context, isPush, receivetime, expireTime, money, title, user_id) values(?,?,?,?,?,?,?)";
		dao.executeSql(sql,context,1,receivetime,expireTime,money,title,user_id);
	}
	/**
	 * 推送通知
	 * @param content
	 * @param title
	 * @param phone
	 * @param sender
	 */
	public void pushNotification(String content,String title,String phone,String sender){
		Map<String, String> customizedField = new HashMap<String, String>();
		customizedField.put("page", "userMessage");
		customizedField.put("title", title);
		customizedField.put("sender", sender);
		AppMessagePush message = new AppMessagePush();
		message.setTitle(title);
		message.setPushType(6);
		message.setPushNow(1);
		message.setContent(content);
		message.setDescription(title);
		message.setOperator(customizedField.get("sender"));
		

		try {
			messagePushService.pushMessage(message, "QHHC", phone, customizedField);
		} catch (Exception e) {
			LOG.error("发放红包消息推送失败！",e);
		}
	}

}
