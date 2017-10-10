package com.hc9.service.activity.year2016;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.redis.Arith;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.redis.activity.year2016.month05.HcNewerTaskCache;
import com.hc9.common.redis.activity.year2016.month05.HcWeekSurpriseCache;
import com.hc9.common.redis.activity.year2016.month05.WeekVo;
import com.hc9.common.util.AppValidator;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.service.SmsService;
import com.hc9.service.UserbasicsinfoService;
import com.hc9.service.activity.ActivityCommonService;
import com.jubaopen.commons.LOG;

/** 2016年5、6月活动 */
@Service
public class Month05ActivityService {
	
	@Resource
	private ActivityCommonService activityCommonService;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	private HibernateSupport dao;
	
	@Resource
	private SmsService smsService;
	
	/** 母亲节红包    @param phone 手机号码* @return*/
	public Map<String, String> monthersDay(String phone) {
		Map<String, String> resultMap = new HashMap<String, String>();
		//System.out.println("手机号码为："+phone);
		boolean p= AppValidator.isPhone(phone);
		if(p==false){
			resultMap.put("msg", "请输入正确的手机号码。");
		}else{
			List<Userbasicsinfo> userList = userbasicsinfoService.queryUserbasicsinfoByPhone(phone);
			if(userList == null || userList.size() < 1) {
				resultMap.put("msg", "未查到该手机号码对应的登录账号！");
			} else if(userList.size() > 1) {
				resultMap.put("msg", "根据该手机号查到多个登录账号！");
			} else {
				Userbasicsinfo user = userList.get(0);
				long userId = user.getId();
				String userSelfKey = "STR:HC9:WE:CHAT:MONTHERS:DAY:KEY:" + userId;
				if(!RedisHelper.isKeyExist(userSelfKey)) {					
					activityCommonService.saveRedEnvelope(userId, 10, 1000, 6, 1);
						
					RedisHelper.set(userSelfKey, "1");
	
					resultMap.put("msg", "母亲节红包发放奖励成功！");
					return resultMap;
				} else {
					resultMap.put("msg", "已发放过相关奖励，不能重复发放！");
				}
			}
		}
		return resultMap;
	}
	/** 2016年5月新手任务相关关注微信根据手机号标注领取 */
	public Map<String, String> webChatAttentionReceive(String phone) {
		Map<String, String> resultMap = new HashMap<String, String>();
		List<Userbasicsinfo> userList = userbasicsinfoService.queryUserbasicsinfoByPhone(phone);
		if(phone.trim().isEmpty()){
			resultMap.put("msg", "电话号码不能为空！");
			return resultMap;
		}else if(userList == null || userList.size() < 1) {
			resultMap.put("msg", "未查到该手机号码对应的登录账号！");
			return resultMap;
		} else if(userList.size() > 1) {
			resultMap.put("msg", "根据该手机号查到多个登录账号！");
			return resultMap;
		} else {
			Userbasicsinfo user = userList.get(0);
			long userId = user.getId();
			String userSelfKey = "STR:HC9:WE:CHAT:ATTENTION:RED:KEY:" + userId;
			if(!RedisHelper.isKeyExist(userSelfKey)) {
				HcNewerTaskCache.setTaskSixReceiveTime(userId);
				String receiveKey = "STR:HC9:TASK:SIX:RECEIVED:FLAG:" + userId;
				if(!RedisHelper.isKeyExist(receiveKey)) {
					activityCommonService.saveRedEnvelope(userId, 10, 1000, 9, 1);
					RedisHelper.set(receiveKey, "1");
						
					RedisHelper.set(userSelfKey, "1");
					
					/** 关注微信号送红包钥匙的总人数 */
					String sysTotalNumKey = "STR:HC9:WE:CHAT:ATTENTION:RED:TOTAL:KEY";
					RedisHelper.incrBy(sysTotalNumKey, 1);
					HcNewerTaskCache.setTaskSixCompleteTime(userId);
					
					sendRegisterRedPackageSms(phone);
					resultMap.put("msg", "关注微信发放奖励成功！");
					
					try {
						String content = smsService.getSmsResources("newConcernWeChatRed.ftl", null);
						int trigger=Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
						smsService.chooseSmsChannel(trigger, content, phone);
					} catch (Exception e) {
						LOG.error( "关注微信红包发送短信失败！", e);
					} 
					return resultMap;
				} else {
					resultMap.put("msg", "该用户已领取过相关奖励，不能重复发放！");
				}
			} else {
				resultMap.put("msg", "已发放过相关奖励，不能重复发放！");
			}
		}
		return resultMap;
	}
	
	/** 注册红包到账发送短信提醒 */
	public void sendRegisterRedPackageSms(String phone) {
		try {
			String content = smsService.getSmsResources("register-redpackge.flt", null);
			int trigger = Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
			smsService.chooseSmsChannel(trigger, content, phone);
		} catch(Exception e) {
			LOG.error("注册送红包提醒短信发送失败！", e);
		}
	}
	
	
	/** 后台手工发送“周周惊喜大放送”活动奖励 */
	public Map<String, String> weekSurprisePrizeGive(int weekNum) {
		Map<String, String> resultMap = new HashMap<String, String>();
		int nowWeekNum = HcWeekSurpriseCache.getWeekSurpriseWeekNum(new Date());
		if(nowWeekNum == 0 || weekNum > nowWeekNum) {
			resultMap.put("msg", "所在周活动尚未开始！");
		} else {
			if(weekNum == nowWeekNum && nowWeekNum != 8) {
				resultMap.put("msg", "所在周活动尚未结束！");
				return resultMap;
			}
			
			if(weekNum == nowWeekNum && nowWeekNum == 8) {
				Date currentDate = new Date();
				Date endDate = DateFormatUtil.stringToDate("2016-06-27 23:59:59", "yyyy-MM-dd HH:mm:ss"); // 结束时间
				if (currentDate.before(endDate)) {
					resultMap.put("msg", "所在周活动尚未结束！");
					return resultMap;
				}
			}
			
			/** 获取周榜单 */
			List<WeekVo> list = HcWeekSurpriseCache.getWeekRankList(weekNum);
			if(list == null || list.size() < 1) {
				resultMap.put("msg", "当前所在周没有相关中奖记录！");
			} else {
				String key = "STR:HC9:WEEK:PRIZE:GIVE:FLAG:" + weekNum;
				if(!RedisHelper.isKeyExist(key)) {
					if(activityCommonService.isGiveActivityMoney(15, weekNum)) {
						resultMap.put("msg", "当前所在周奖励已经发放，不能重复发放！");
					} else {
						/** 第一名：返当周累计投资年化金额*2% */
						giveWeekSurpriseFirstPrize(list, weekNum);
						
						/** 第二名:返当周累计投资年化金额*1.5% */
						giveWeekSurpriseSecondPrize(list, weekNum);
						
						/** 第三名:返当周累计投资年化金额*1% */
						giveWeekSurpriseThirdPrize(list, weekNum);
						
						/** 第四 - 十名 100元红包（2个5元，1个30元，1个60元）+加息券0.3%（投资3000元使用） */
						giveWeekSurpriseAfterThirdPrize(list);
						
						RedisHelper.set(key, "1");
						resultMap.put("msg", "当前所在成功发放" + list.size() + "位投资的中奖记录！");
					}
				} else {
					resultMap.put("msg", "当前所在周奖励已经发放，不能重复发放！");
				}
			}
		}
		return resultMap;
	}
	
	/** 第一名：返当周累计投资年化金额*2% */
	private void giveWeekSurpriseFirstPrize(List<WeekVo> list, int weekNum) {
		if(list != null && list.size() > 0) {
			WeekVo weekVo = list.get(0);
			long userId = weekVo.getUserId();
			String phone = weekVo.getPhone();
			double weekMoney = weekVo.getWeekMoney();
			double rewardMoney = Arith.round(Arith.mul(weekVo.getWeekYearMoney(), 0.02), 2).doubleValue();
			activityCommonService.saveAcivityMoney(userId, phone, rewardMoney, 15, weekMoney, 0, weekNum, 0, "");
		}
	}
	
	/** 第二名:返当周累计投资年化金额*1.5% */
	private void giveWeekSurpriseSecondPrize(List<WeekVo> list, int weekNum) {
		if(list != null && list.size() > 1) {
			WeekVo weekVo = list.get(1);
			long userId = weekVo.getUserId();
			String phone = weekVo.getPhone();
			double weekMoney = weekVo.getWeekMoney();
			double rewardMoney = Arith.round(Arith.mul(weekVo.getWeekYearMoney(), 0.015), 2).doubleValue();
			activityCommonService.saveAcivityMoney(userId, phone, rewardMoney, 15, weekMoney, 0, weekNum, 0, "");
		}
	}
	
	/** 第三名:返当周累计投资年化金额*1% */
	private void giveWeekSurpriseThirdPrize(List<WeekVo> list, int weekNum) {
		if(list != null && list.size() > 2) {
			WeekVo weekVo = list.get(2);
			long userId = weekVo.getUserId();
			String phone = weekVo.getPhone();
			double weekMoney = weekVo.getWeekMoney();
			double rewardMoney = Arith.round(Arith.mul(weekVo.getWeekYearMoney(), 0.01), 2).doubleValue();
			activityCommonService.saveAcivityMoney(userId, phone, rewardMoney, 15, weekMoney, 0, weekNum, 0, "");
		}
	}
	
	/** 第四 - 十名 100元红包（2个5元，1个30元，1个60元）+加息券0.3%（投资3000元使用） */
	private void giveWeekSurpriseAfterThirdPrize(List<WeekVo> list) {
		if(list != null && list.size() > 3) {
			for(int i = 3; i < list.size(); i++) {
				WeekVo weekVo = list.get(i);
				long userId = weekVo.getUserId();
				activityCommonService.saveRedEnvelope(userId, 5, 100, 12, 1);
				activityCommonService.saveRedEnvelope(userId, 5, 100, 12, 1);
				activityCommonService.saveRedEnvelope(userId, 30, 3000, 12, 1);
				activityCommonService.saveRedEnvelope(userId, 60, 8000, 12, 1);
				activityCommonService.saveInterestIncreaseCard(userId, 0.003, 3000, 1, 11);
			}
		}
	}
	
	/** 后台手工更新演唱会门票状态为已发放 */
	public Map<String, String> updateJuChengTicketStatus() {
		Map<String, String> resultMap = new HashMap<String, String>();
		String sql = "select * from activity_monkey where status=0 and type=16";
		List list = dao.findBySql(sql);
		if(list != null && list.size() > 0) {
			sql = "update activity_monkey set status=1 where type=16 and status=0";
			dao.executeSql(sql);
			resultMap.put("code", "0");
			resultMap.put("msg", "演唱会门票发放成功！");
		} else {
			resultMap.put("code", "1");
			resultMap.put("msg", "尚无演唱会门票需要发放！");
		}
		
		return resultMap;
	}
}
