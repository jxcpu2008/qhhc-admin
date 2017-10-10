package com.hc9.common.redis;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hc9.common.log.LOG;
import com.hc9.dao.entity.Smsswitch;
import com.hc9.dao.entity.SwitchControl;
import com.hc9.dao.impl.HibernateSupport;

@Component
/** 缓存初始化相关类 */
public class CacheHelpServiceImpl {
	
	@Resource
	private HibernateSupport dao;
	
	/** 启动时将个人投资金额信息初始化至Redis缓存中 */
	@PostConstruct
	private void initRedisCacheData() {
		LOG.error("--------------- begin to init redis cache data! ---------------");
		
		/** 将短信邮件模板类型中文名称 及模板中文名称保存至Redis中 */
		String key = "RCS:HC9:REDIS:INIT:TEMPLATE:ZH:NAME";
		if(!RedisHelper.isKeyExistSetWithExpire(key, 0)) {
			String sql = "select * from switchcontrol where swithchType='sms_email_template_type'";
			List<SwitchControl> smsEmailTemplateList = dao.findBySql(sql , SwitchControl.class);
			if(smsEmailTemplateList != null && smsEmailTemplateList.size() > 0) {
				for(SwitchControl vo : smsEmailTemplateList) {
					String upSwitchEnName = vo.getUpSwitchEnName();//模板大类型
					String switchEnName = vo.getSwitchEnName();//模板
					String switchZhName = vo.getSwitchZhName();//模板中文名称
					SmsEmailCache.setSmsMsgTemplateZhName(upSwitchEnName, switchEnName, switchZhName);
				}
			}
		}
		
		/** 将短信邮件开关信息保存至redis中  */
		key = "RCS:HC9:REDIS:INIT:SMS:EMAIL:SWITCH";
		if(!RedisHelper.isKeyExistSetWithExpire(key, 0)) {
			String sql = "select * from switchcontrol where swithchType='email_sms_tip'";
			List<SwitchControl> smsSwithchList = dao.findBySql(sql , SwitchControl.class);
			if(smsSwithchList != null && smsSwithchList.size() > 0) {
				for(SwitchControl vo : smsSwithchList) {
					String switchEnName = vo.getSwitchEnName();//模板
					Integer switchStatus = vo.getSwitchStatus();//模板中文名称
					SmsEmailCache.setSmsEmailSwitchStatus(switchEnName, switchStatus);
				}
			}
		}
		
		/**将短信通道信息保存到reids*/
		key = "RCS:HC9:REDIS:INIT:SMS:TRIGGER:MARKETING:CHANNEL";
		if(!RedisHelper.isKeyExistSetWithExpire(key, 0)) {
			String sql = "select * from smsswitch where id=?";
			Smsswitch smsswitch=dao.findObjectBySql(sql, Smsswitch.class, 1);
			
			SmsEmailCache.setSmsTriggerSwitch(smsswitch.getTriger());
			SmsEmailCache.setSmsMarketingSwitch(smsswitch.getMarketing());
		}
		
		key = "RCS:HC9:REDIS:INIT:CPS:WHITELIST";
		if(!RedisHelper.isKeyExistSetWithExpire(key, 0)) {
			String ips="127.0.0.1,118.144.36.162,119.40.53.2,221.122.127.13,221.122.127.109";
			RedisUtil.setCpsWhitelist(key,ips);
		}
		
		key = "STR:HC9:SMS:VALIDATE:STATUS";
		if(!RedisHelper.isKeyExistSetWithExpire(key, 0)) {
			SmsEmailCache.setSmsValidateStatus(1);
		}
		LOG.error("--------------- init redis cache data ok! ---------------");
	}
	
}
