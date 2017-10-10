package com.hc9.common.redis;

import com.hc9.common.log.LOG;
import com.hc9.common.util.StringUtil;

/** 短信邮件缓存相关 */
public class SmsEmailCache {
	/** 设置短信邮件模板中文名称
	 *  @param upSwitchEnName 模板大类型
	 *  @param switchEnName 模板英文名称
	 *  @param switchZhName 模板中文名称 
	 *  */
	public static void setSmsMsgTemplateZhName(String upSwitchEnName, String switchEnName, String switchZhName) {
		String key = "sms_email_template_type:";//邮件短信模板
		if(StringUtil.isNotBlank(upSwitchEnName)) {//设置具体模板
			key += "STR:HC9:" + upSwitchEnName + ":" + switchEnName;
		} else {
			key += "STR:HC9:" + switchEnName;
		}
		RedisHelper.set(key, switchZhName);
	}
	
	/** 获取短信邮件一级模板中文名称
	 *  @param upSwitchEnName 模板大类型
	 *  @param switchEnName 模板英文名称
	 *  */
	public static String getSmsMsgOneLevelTemplateZhName(String switchEnName) {
		String key = "sms_email_template_type:STR:HC9:" + switchEnName;
		return RedisHelper.get(key);
	}
	
	/** 获取短信邮件二级模板中文名称
	 *  @param upSwitchEnName 模板大类型
	 *  @param switchEnName 模板英文名称
	 *  */
	public static String getSmsMsgTwoLevelTemplateZhName(String upSwitchEnName, String switchEnName) {
		String key = "sms_email_template_type:STR:HC9:" + upSwitchEnName + ":" + switchEnName;
		return RedisHelper.get(key);
	}
	
	/** 将发送计划的状态设置到redis中，如果没有设置到reids中，默认为不发送，状态为1的发送计划才会发送
	 * 	@param sendPlanId 发送计划对应的主键id
	 *  @param sendStatus 发送计划状态：0、草稿箱 1、等待发送；2、正在发送；3、终止发送；4、发送成功；5、发送失败；6、暂停；
	 *  */
	public static void setSmsEmailSendPlanStatus(Long sendPlanId, int sendStatus) {
		String key = "STR:HC9:SMS:EMAIL:SEND:PLAN:" + sendPlanId;
		RedisHelper.set(key, "" + sendStatus);
	}
	
	/** 获取短信邮件发送计划相关信息 */
	public static String getSmsEmailSendPlanStatus(Long sendPlanId) {
		String key = "STR:HC9:SMS:EMAIL:SEND:PLAN:" + sendPlanId;
		return RedisHelper.get(key);
	}
	
	/** 邮件短信相关开关设置时传入redis
	 *  @param switchEnName 开关英文名称
	 *  @param switchStatus 开关状态：1、开启；0、关闭；-1、软删除；
	 *  */
	public static void setSmsEmailSwitchStatus(String switchEnName, int switchStatus) {
		String key = "STR:HC9:SMS:EMAIL:SWITCH:" + switchEnName;
		RedisHelper.set(key, "" + switchStatus);
	}
	
	/** 获取邮件短息开关相关状态 */
	public static String getSmsEmailSwitchStatus(String switchEnName) {
		try {
			String key = "STR:HC9:SMS:EMAIL:SWITCH:" + switchEnName;
			return RedisHelper.get(key);
		} catch (Exception e) {
			LOG.error("获取开关 " + switchEnName + " 状态失败！");
		}
		return "";
	}
	
	/**获取触发短信通道状态*/
	public static String getSmsTriggerChannel(){
		try{
			String key="STR:HC9:SMS:TRIGGER:CHANNEL";
			String value = RedisHelper.get(key);
			if(StringUtil.isBlank(value)) {
				/**  亿美短信通道 */
				RedisHelper.set(key, value);
				value = "1";
			}
			return value;
		}catch (Exception e) {
			LOG.error("获取短信通道状态 " + " 状态失败！");
		}
		return "";
	}
	
	/**设置触发短信通道状态*/
	public static void setSmsTriggerSwitch(int switchStatus){
		String key="STR:HC9:SMS:TRIGGER:CHANNEL";
		RedisHelper.set(key, "" + switchStatus);
	}
	
	/**获取营销短信通道状态*/
	public static String getSmsMarketingChannel(){
		try{
			String key="STR:HC9:SMS:MARKETING:CHANNEL";
			return RedisHelper.get(key);
		}catch (Exception e) {
			LOG.error("获取短信通道状态 " + " 状态失败！");
		}
		return "";
	}
	
	/**设置营销短信通道状态*/
	public static void setSmsMarketingSwitch(int switchStatus){
		String key="STR:HC9:SMS:MARKETING:CHANNEL";
		RedisHelper.set(key, "" + switchStatus);
	}
	
	/**获取注册短信校验状态*/
	public static String getSmsValidateStatus(){
		try{
			String key="STR:HC9:SMS:VALIDATE:STATUS";
			return RedisHelper.get(key);
		}catch (Exception e) {
			LOG.error("获取短信通道状态 " + " 状态失败！");
		}
		return "";
	}
	
	/**设置注册短信校验状态*/
	public static void setSmsValidateStatus(int status){
		String key="STR:HC9:SMS:VALIDATE:STATUS";
		RedisHelper.set(key, "" + status);
	}
	
	/** 90秒内同一个手机号只能发送一次 */
	public static boolean isAbleGetRegisterCode(String phone) {
		String concurrentLock = "STR:REGISTER:CODE:CONCURRENT:LOCK:" + phone;
		if(!RedisHelper.isKeyExistSetWithExpire(concurrentLock, 90)) {
			return true;
		} else {
			return false;
		}
	}
	
	/** 单个手机号码一天内获取注册短信的机会，默认为3次 */
	public static int getMaxRgeisterSmsCodeNumOneDay() {
		String key = "INT:HC9:SMS:REGISTER:CODE:MAX:NUM:PHONE";
		String num = RedisHelper.get(key);
		if(StringUtil.isBlank(num)) {
			num = "3";
			RedisHelper.set(key, num);
		}
		return Integer.valueOf(num);
	}
	
	/** 单个手机号码一天内已经获取注册短信验证码的次数 */
	public static int getTodayRgeisterSmsCodeNum(String today, String phone) {
		String key = "INT:HC9:SMS:REGISTER:CODE:NUM:" + today + ":" + phone;
		String num = RedisHelper.get(key);
		if(StringUtil.isBlank(num)) {
			num = "0";
		}
		return Integer.valueOf(num);
	}
	
	/** 单个手机号码一天内已经获取注册短信验证码的次数 */
	public static void increaseTodayRgeisterSmsCodeNum(String today, String phone) {
		String key = "INT:HC9:SMS:REGISTER:CODE:NUM:" + today + ":" + phone;
		RedisHelper.incrBy(key, 1);
		RedisHelper.expireByKey(key, 60 * 60 * 24);
	}
	
	/** 黑名单最大次数 */
	public static int getMaxBlackListNum() {
		String key = "INT:HC9:SMS:REGISTER:BLACK:LIST:MAX:NUM";
		String num = RedisHelper.get(key);
		if(StringUtil.isBlank(num)) {
			num = "9";
			RedisHelper.set(key, num);
		}
		return Integer.valueOf(num);
	}
	
	/** 根据手机号码新增黑名单的次数 */
	public static void increaseRegisterBlackListSmsNum(String phone) {
		String key = "INT:HC9:SMS:REGISTER:BLACK:LIST:MAX:NUM:" + phone;
		RedisHelper.incrBy(key, 1);
	}
	
	/** 根据手机号码获取手机黑名单的总次数 */
	public static int getRegisterBlackListSmsNum(String phone) {
		String key = "INT:HC9:SMS:REGISTER:BLACK:LIST:MAX:NUM:" + phone;
		String num = RedisHelper.get(key);
		if(StringUtil.isBlank(num)) {
			num = "0";
		}
		return Integer.valueOf(num);
	}
}
