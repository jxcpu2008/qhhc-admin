package com.hc9.common.redis.activity.year2016.month05;

import java.util.Date;

import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.Userbasicsinfo;

/** 新手任务相关缓存:活动时间：2016年5月3日开始，为常态化活动 */
public class HcNewerTaskCache {
	
	/** 新手注册送100元红包钥匙 */
	public static void giveNewerRegisterRedenvelopeKey(long userId) {
		try {
			/** 用户自己的注册送100元红包钥匙标识 */
			if(isNewerTaskActivity(new Date())) {
				String userSelfKey = "STR:HC9:NEWER:REGISTER:RED:KEY:" + userId;
				if(!RedisHelper.isKeyExist(userSelfKey)) {
					RedisHelper.set(userSelfKey, "1");
				}
			}
		} catch(Exception e) {
			LOG.error("新手注册送100元红包过程更新缓存过程中出错！" , e);
		}
	}
	
	/** 判断用户是否拥有领取任务一（注册送红包）的资格 */
	public static String getUserTaskOneStatus(long userId) {
		if(isNewerRegisterInActivityArea(userId)) {
			/** 是否已经领取过 */
			String receiveKey = "STR:HC9:TASK:ONE:RECEIVED:FLAG:" + userId;
			if(!RedisHelper.isKeyExist(receiveKey)) {
				/** 尚未领取 */
				return "1";
			} else {
				/** 已经领取过 */
				return "3";
			}
		} else {
			/** 不是活动期间新注册用户 */
			return "2";
		}
	}
	
	/** 获取任务一已经成功领取到钥匙的人数 */
	public static long getTaskOneReceivedNum() {
		/** 系统所有已经获取注册送100元红包钥匙的总人数 */
		String sysTotalNumKey = "STR:HC9:NEWER:REGISTER:RED:TOTAL:KEY";
		String totalNum = RedisHelper.get(sysTotalNumKey);
		if(StringUtil.isNotBlank(totalNum)) {
			return Long.valueOf(totalNum);
		} else {
			return 0;
		}
	}
	
	/** 判断是否为活动期间新注册的用户 */
	public static boolean isNewerRegisterInActivityArea(long userId) {
		String userSelfKey = "STR:HC9:NEWER:REGISTER:RED:KEY:" + userId;
		if(RedisHelper.isKeyExist(userSelfKey)) {
			return true;
		} else {
			return false;
		}
	}
	
	/** 首次充值送5元红包钥匙 */
	public static void giveFirstRechargeRedenvelopeKey(long userId) {
		try {
			if(isNewerRegisterInActivityArea(userId)) {
				/** 用户自己的首次充值送5元红包钥匙标识 */
				String userSelfKey = "STR:HC9:FIRST:RECHARGE:RED:KEY:" + userId;
				if(!RedisHelper.isKeyExist(userSelfKey)) {
					RedisHelper.set(userSelfKey, "1");
					setTaskTwoReceiveTime(userId);
				}
			}
		} catch(Exception e) {
			LOG.error("首次充值送5元红包过程更新缓存过程中出错！", e);
		}
	}
	
	/** 判断用户是否拥有领取任务二（首次充值送红包）的资格 */
	public static String getUserTaskTwoStatus(long userId) {
		if(isNewerRegisterInActivityArea(userId)) {
			/** 用户自己的首次充值送5元红包钥匙标识 */
			String userSelfKey = "STR:HC9:FIRST:RECHARGE:RED:KEY:" + userId;
			if(RedisHelper.isKeyExist(userSelfKey)) {
				/** 是否已经领取过 */
				String receiveKey = "STR:HC9:TASK:TWO:RECEIVED:FLAG:" + userId;
				if(!RedisHelper.isKeyExist(receiveKey)) {
					/** 尚未领取 */
					return "1";
				} else {
					/** 已经领取过 */
					return "3";
				}
			} else {
				/** 无充值记录 */
				return "4";
			}
		} else {
			/** 不是活动期间新注册用户 */
			return "2";
		}
	}
	
	/** 获取任务二已经成功领取到钥匙的人数 */
	public static long getTaskTwoReceivedNum() {
		/** 系统所有已经获取首次充值送5元红包钥匙的总人数 */
		String sysTotalNumKey = "STR:HC9:FIRST:RECHARGE:RED:TOTAL:KEY";
		String totalNum = RedisHelper.get(sysTotalNumKey);
		if(StringUtil.isNotBlank(totalNum)) {
			return Long.valueOf(totalNum);
		} else {
			return 0;
		}
	}
	
	/** 投资赠送红包相关逻辑
	 *  @param userId 用户id
	 *  @param investMoney 投资金额
	 *  @param investType 投资类型：投资类型 1 优先 2 夹层 3劣后
	 *   */
	public static void giveFirstInvestRedenvelopeKey(long userId, double investMoney, int investType) {
		try {
			boolean flag = false;
			if(investType == 1 || investType == 2) {
				flag = true;
			}
			if(isNewerRegisterInActivityArea(userId) && flag) {
				if(investMoney >= 3000) {
					String key = "STR:HC9:FIRST:INVEST:TASK:FIVE:" + userId;
					if(!RedisHelper.isKeyExist(key)) {
						RedisHelper.set(key, "1");
					}
				} else if(investMoney >= 1000) {
					String key = "STR:HC9:FIRST:INVEST:TASK:FOUR:" + userId;
					if(!RedisHelper.isKeyExist(key)) {
						RedisHelper.set(key, "1");
					}
				} else if(investMoney >= 100) {
					String key = "STR:HC9:FIRST:INVEST:TASK:THREE:" + userId;
					if(!RedisHelper.isKeyExist(key)) {
						RedisHelper.set(key, "1");
					}
				}
			}
		} catch(Exception e) {
			LOG.error("投资赠送红包过程更新缓存过程中出错！", e);
		}
	}
	
	/** 获取任务三已经成功领取到钥匙的人数 */
	public static long getTaskThreeReceivedNum() {
		/** 系统所有已经获取任务三对应钥匙的总人数 */
		String totalNumKey = "STR:HC9:FIRST:INVEST:TASK:THREE:TOTAL";
		String totalNum = RedisHelper.get(totalNumKey);
		if(StringUtil.isNotBlank(totalNum)) {
			return Long.valueOf(totalNum);
		} else {
			return 0;
		}
	}
	
	/** 判断用户是否拥有领取任务三（ 单笔认购满100元送红包）的资格 */
	public static String getUserTaskThreeStatus(long userId) {
		if(isNewerRegisterInActivityArea(userId)) {
			/** 任务三完成标识 */
			String key = "STR:HC9:FIRST:INVEST:TASK:THREE:" + userId;
			if(RedisHelper.isKeyExist(key)) {
				/** 是否已经领取过 */
				String receiveKey = "STR:HC9:TASK:THREE:RECEIVED:FLAG:" + userId;
				if(!RedisHelper.isKeyExist(receiveKey)) {
					/** 尚未领取 */
					return "1";
				} else {
					/** 已经领取过 */
					return "3";
				}
			} else {
				return "4";
			}
		} else {
			/** 不是活动期间新注册用户 */
			return "2";
		}
	}
	
	/** 获取任务四已经成功领取到钥匙的人数 */
	public static long getTaskFourReceivedNum() {
		/** 系统所有已经获取任务四对应钥匙的总人数 */
		String totalNumKey = "STR:HC9:FIRST:INVEST:TASK:FOUR:TOTAL";
		String totalNum = RedisHelper.get(totalNumKey);
		if(StringUtil.isNotBlank(totalNum)) {
			return Long.valueOf(totalNum);
		} else {
			return 0;
		}
	}
	
	/** 判断用户是否拥有领取任务四（单笔认购满1000元送红包）的资格 */
	public static String getUserTaskFourStatus(long userId) {
		if(isNewerRegisterInActivityArea(userId)) {
			/** 任务四完成标识 */
			String key = "STR:HC9:FIRST:INVEST:TASK:FOUR:" + userId;
			if(RedisHelper.isKeyExist(key)) {
				/** 是否已经领取过 */
				String receiveKey = "STR:HC9:TASK:FOUR:RECEIVED:FLAG:" + userId;
				if(!RedisHelper.isKeyExist(receiveKey)) {
					/** 尚未领取 */
					return "1";
				} else {
					/** 已经领取过 */
					return "3";
				}
			} else {
				return "4";
			}
		} else {
			/** 不是活动期间新注册用户 */
			return "2";
		}
	}
	
	/** 获取任务五已经成功领取到钥匙的人数 */
	public static long getTaskFiveReceivedNum() {
		/** 系统所有已经获取任务五对应钥匙的总人数 */
		String totalNumKey = "STR:HC9:FIRST:INVEST:TASK:FIVE:TOTAL";
		String totalNum = RedisHelper.get(totalNumKey);
		if(StringUtil.isNotBlank(totalNum)) {
			return Long.valueOf(totalNum);
		} else {
			return 0;
		}
	}
	
	/** 判断用户是否拥有领取任务五（单笔认购满3000元送红包）的资格 */
	public static String getUserTaskFiveStatus(long userId) {
		if(isNewerRegisterInActivityArea(userId)) {
			/** 任务五完成标识 */
			String key = "STR:HC9:FIRST:INVEST:TASK:FIVE:" + userId;
			if(RedisHelper.isKeyExist(key)) {
				/** 是否已经领取过 */
				String receiveKey = "STR:HC9:TASK:FIVE:RECEIVED:FLAG:" + userId;
				if(!RedisHelper.isKeyExist(receiveKey)) {
					/** 尚未领取 */
					return "1";
				} else {
					/** 已经领取过 */
					return "3";
				}
			} else {
				return "4";
			}			
		} else {
			/** 不是活动期间新注册用户 */
			return "2";
		}
	}
	
	/** 关注微信号赠送红包相关逻辑 */
	public static void giveWeChatAtentionRedenvelopeKey(long userId) {
		try {
			if(isNewerRegisterInActivityArea(userId)) {
				/** 关注微信号送红包钥匙标识 */
				String userSelfKey = "STR:HC9:WE:CHAT:ATTENTION:RED:KEY:" + userId;
				if(!RedisHelper.isKeyExist(userSelfKey)) {
					RedisHelper.set(userSelfKey, "1");
				}
			}
		} catch(Exception e) {
			LOG.error("关注微信号送红包钥匙过程更新缓存过程中出错！", e);
		}
	}
	
	/** 获取任务六已经成功领取到钥匙的人数 */
	public static long getTaskSixReceivedNum() {
		/** 关注微信号送红包钥匙的总人数 */
		String totalNumKey = "STR:HC9:WE:CHAT:ATTENTION:RED:TOTAL:KEY";
		String totalNum = RedisHelper.get(totalNumKey);
		if(StringUtil.isNotBlank(totalNum)) {
			return Long.valueOf(totalNum);
		} else {
			return 0;
		}
	}
	
	/** 判断用户是否拥有领取任务六（关注微信号送红包）的资格 */
	public static String getUserTaskSixStatus(long userId) {
		if(isNewerRegisterInActivityArea(userId)) {
			/** 关注微信号送红包钥匙标识 */
			String key = "STR:HC9:WE:CHAT:ATTENTION:RED:KEY:" + userId;
			if(RedisHelper.isKeyExist(key)) {
				/** 是否已经领取过 */
				String receiveKey = "STR:HC9:TASK:SIX:RECEIVED:FLAG:" + userId;
				if(!RedisHelper.isKeyExist(receiveKey)) {
					/** 尚未领取 */
					return "1";
				} else {
					/** 已经领取过 */
					return "3";
				}
			} else {
				return "4";
			}			
		} else {
			/** 不是活动期间新注册用户 */
			return "2";
		}
	}
	
	/** 判断用户是否完成了所有任务 */
	public static int getUserTaskCompleteInfo(long userId) {
		int totalNum = 0;
		if(isNewerRegisterInActivityArea(userId)) {
			/** 任务一是否已完成 */
			String key = "STR:HC9:TASK:ONE:RECEIVED:FLAG:" + userId;
			if(RedisHelper.isKeyExist(key)) {
				totalNum += 1;
			}
			
			/** 任务二是否需完成 */
			key = "STR:HC9:TASK:TWO:RECEIVED:FLAG:" + userId;
			if(RedisHelper.isKeyExist(key)) {
				totalNum += 1;
			}
			
			/** 任务三是否需完成 */
			key = "STR:HC9:TASK:THREE:RECEIVED:FLAG:" + userId;
			if(RedisHelper.isKeyExist(key)) {
				totalNum += 1;
			}
			
			/** 任务四是否需完成 */
			key = "STR:HC9:TASK:FOUR:RECEIVED:FLAG:" + userId;
			if(RedisHelper.isKeyExist(key)) {
				totalNum += 1;
			}
			
			/** 任务五是否需完成 */
			key = "STR:HC9:TASK:FIVE:RECEIVED:FLAG:" + userId;
			if(RedisHelper.isKeyExist(key)) {
				totalNum += 1;
			}
			
			/** 任务六是否需完成 */
			key = "STR:HC9:TASK:SIX:RECEIVED:FLAG:" + userId;
			if(RedisHelper.isKeyExist(key)) {
				totalNum += 1;
			}
		}
		return totalNum;
	}
	
	/** 判断用户是否完成了所有任务 */
	public static boolean isUserFinishAllTask(long userId) {
		boolean flag = true;
		if(isNewerRegisterInActivityArea(userId)) {
			/** 任务一是否已完成 */
			String key = "STR:HC9:TASK:ONE:RECEIVED:FLAG:" + userId;
			if(!RedisHelper.isKeyExist(key)) {
				flag = false;
			}
			
			/** 任务二是否需完成 */
			key = "STR:HC9:TASK:TWO:RECEIVED:FLAG:" + userId;
			if(!RedisHelper.isKeyExist(key)) {
				flag = false;
			}
			
			/** 任务三是否需完成 */
			key = "STR:HC9:TASK:THREE:RECEIVED:FLAG:" + userId;
			if(!RedisHelper.isKeyExist(key)) {
				flag = false;
			}
			
			/** 任务四是否需完成 */
			key = "STR:HC9:TASK:FOUR:RECEIVED:FLAG:" + userId;
			if(!RedisHelper.isKeyExist(key)) {
				flag = false;
			}
			
			/** 任务五是否需完成 */
			key = "STR:HC9:TASK:FIVE:RECEIVED:FLAG:" + userId;
			if(!RedisHelper.isKeyExist(key)) {
				flag = false;
			}
			
			/** 任务六是否需完成 */
			key = "STR:HC9:TASK:SIX:RECEIVED:FLAG:" + userId;
			if(!RedisHelper.isKeyExist(key)) {
				flag = false;
			}
		} else {
			flag = false;
		}
		return flag;
	}
	
	/** 寻到所有钥匙的新手人数 */
	public static void updateCollectAllKeyNum(long userId) {
		if(isUserFinishAllTask(userId)) {
			String cloolecAllKey = "STR:HC9:NEWER:COLLECT:ALL:NUM";
			RedisHelper.incrBy(cloolecAllKey, 1);
		}
	}
	
	/** 获取寻到所有钥匙的新手人数 */
	public static long getCollectAllKeyNum() {
		if(isNewerTaskActivity(new Date())) {
			String key = "STR:HC9:NEWER:COLLECT:ALL:NUM";
			String str = RedisHelper.get(key);
			if(StringUtil.isNotBlank(str)) {
				return Long.valueOf(str);
			}
		}
		return 0;
	}
	
	/** 用户生日标识提醒标识 */
	public static String isNeedBirthdayTips(Userbasicsinfo user) {
		if(isNewerTaskActivity(new Date())) {
			if(user.getIsAuthIps() == 1) {
				String cardNo = user.getUserrelationinfo().getCardId();
				if(isUserBirthdayToday(cardNo)) {
					long userId = user.getId();
					String key = "STR:HC9:RECEIVE:BIRTHDAY:GIFT:FLAG:"  + userId;
					if(!RedisHelper.isKeyExist(key)) {
						return "1";
					}
				}
			}
		}
		return "0";
	}
	
	/** 根据身份证判断是否是用户生日 */
	public static boolean isUserBirthdayToday(String cardNo) {
		boolean isUserBirthday = false;
		try {
			String cardDay = cardNo.substring(10, 14);
			String today = DateFormatUtil.dateToString(new Date(), "MMdd");
			if(today.equals(cardDay)) {
				isUserBirthday = true;
			}
		} catch (Exception e) {
			LOG.error("获取用户生日标识过程中出错！", e);
		}
		return isUserBirthday;
	}
	
	/** 是否有新的优惠券入账 */
	public static String isNewCouponInCome(long userId) {
		String key = "STR:HC9:NEW:COUPON:IN:COME:" + userId;
		if(RedisHelper.isKeyExist(key)) {
			String str = RedisHelper.get(key);
			if(StringUtil.isNotBlank(str)) {
				if("1".equals(str)) {
					return "1";
				}
			}
		}
		return "0"; 
	}
	
	/** 发放新的红包、提现券、加息券后设置提醒标识 */
	public static void setCouponTipFlag(long userId) {
		String key = "STR:HC9:NEW:COUPON:IN:COME:" + userId;
		RedisHelper.set(key, "1");
	}
	
	/** 更新优惠券提醒相关标识为已读 */
	public static void updateCouponTipFlag(long userId) {
		String key = "STR:HC9:NEW:COUPON:IN:COME:" + userId;
		RedisHelper.del(key);
	}
	
	/** 获取新手任务活动2016年5月3日 活动开始时间 */
	public static String getNewerTaskActivityBeginDate() {
		String beginDateKey = "STR:HC9:NEWER:TASK:BEGIN:DATE";
		String beginDate = "";
		try {
			beginDate = RedisHelper.get(beginDateKey);
			if(StringUtil.isBlank(beginDate)) {
				beginDate = "2016-05-03 00:00:00";
				RedisHelper.set(beginDateKey, beginDate);
			}
		} catch(Exception e) {
			LOG.error("获取新手任务金活动开始时间报错,使用默认开始时间2016-05-03 00:00:00！", e);
			beginDate = "2016-05-03 00:00:00";
		}
		return beginDate;
	}
	
	/** 获取新手任务活动2016年5月3日-长期 活动结束时间 */
	public static String getNewerTaskActivityEndDate() {
		String endDateKey = "STR:HC9:NEWER:TASK:END:DATE";
		String endDate = "";
		try {
			endDate = RedisHelper.get(endDateKey);
		} catch(Exception e) {
			LOG.error("获取新手任务活动的结束时间报错！", e);
			endDate = "2016-06-30 23:59:59";
		}
		return endDate;
	}
	
	/** 判断是否是新手任务活动 2016年5月3日-长期活动期间 */
	public static boolean isNewerTaskActivity(Date currentDate) {
		boolean result = true;
		/** 活动开始时间 */
		String beginDateStr = getNewerTaskActivityBeginDate();
		
		/** 活动结束时间 */
		String endDateStr = getNewerTaskActivityEndDate();

		Date beginDate = DateFormatUtil.stringToDate(beginDateStr, "yyyy-MM-dd HH:mm:ss");
		
		/** 当前时间早于活动开始时间 */
		if(currentDate.before(beginDate)) {
			result = false;
		}
		if(StringUtil.isNotBlank(endDateStr)) {
			Date endDate = DateFormatUtil.stringToDate(endDateStr, "yyyy-MM-dd HH:mm:ss");
			if(endDate.before(currentDate)) {
				result = false;
			}
		}
		return result;
	}
	
	/** 获取任务一的领取时间 */
	public static String getTaskOneReceiveTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:ONE:RECEIVE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 获取任务二的领取时间 */
	public static String getTaskTwoReceiveTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:TWO:RECEIVE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 获取任务三的领取时间 */
	public static String getTaskThreeReceiveTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:THREE:RECEIVE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 获取任务四的领取时间 */
	public static String getTaskFourReceiveTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:FOUR:RECEIVE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 获取任务五的领取时间 */
	public static String getTaskFiveReceiveTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:FIVE:RECEIVE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 设置任务六的领取时间 */
	public static void setTaskSixReceiveTime(long userId) {
		String nowTime = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		String key = "STR:HC9:NEWER:TASK:SIX:RECEIVE:TIME:" + userId;
		RedisHelper.set(key, nowTime);
	}
	
	/** 获取任务六的领取时间 */
	public static String getTaskSixReceiveTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:SIX:RECEIVE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 获取任务七的领取时间 */
	public static String getTaskSevenReceiveTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:SEVEN:RECEIVE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 获取任务一的领取时间 */
	public static String getTaskOneCompleteTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:ONE:COMPLETE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 获取任务二的领取时间 */
	public static String getTaskTwoCompleteTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:TWO:COMPLETE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 获取任务三的领取时间 */
	public static String getTaskThreeCompleteTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:THREE:COMPLETE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 获取任务四的领取时间 */
	public static String getTaskFourCompleteTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:FOUR:COMPLETE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 获取任务五的领取时间 */
	public static String getTaskFiveCompleteTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:FIVE:COMPLETE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 设置任务六的领取时间 */
	public static void setTaskSixCompleteTime(long userId) {
		String nowTime = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		String key = "STR:HC9:NEWER:TASK:SIX:COMPLETE:TIME:" + userId;
		RedisHelper.set(key, nowTime);
	}
	
	/** 获取任务六的领取时间 */
	public static String getTaskSixCompleteTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:SIX:COMPLETE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 获取任务七的领取时间 */
	public static String getTaskSevenCompleteTime(long userId) {
		String key = "STR:HC9:NEWER:TASK:SEVEN:COMPLETE:TIME:" + userId;
		return RedisHelper.get(key);
	}
	
	/** 设置任务二的领取时间 */
	public static void setTaskTwoReceiveTime(long userId) {
		String nowTime = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		String key = "STR:HC9:NEWER:TASK:TWO:RECEIVE:TIME:" + userId;
		RedisHelper.set(key, nowTime);
	}
}
