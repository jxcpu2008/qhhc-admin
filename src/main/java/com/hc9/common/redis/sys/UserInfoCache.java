package com.hc9.common.redis.sys;

import com.hc9.common.redis.RedisHelper;

/** 用户相关信息缓存 */
public class UserInfoCache {
	/** 用户开通宝付时更新用户的实名信息至redis中 */
	public static void setNameToRedis(long userId, String name) {
		String key = "STR:HC9:USER:INFO:NAME:" + userId;
		RedisHelper.set(key, name);
	}
	
	/** 用户开通宝付时更新用户的实名信息至redis中 */
	public static void setCardIdToRedis(long userId, String cardId) {
		String key = "STR:HC9:USER:INFO:CARDID:" + userId;
		RedisHelper.set(key, cardId);
	}
	
	/** 从缓存中获取用户开通宝付时使用的用户名 */
	public static String getNameFromRedis(long userId) {
		String key = "STR:HC9:USER:INFO:NAME:" + userId;
		String name = RedisHelper.get(key);
		return name;
	}
	
	/** 用户注册时更新用户的实名信息至redis中 */
	public static String getCardIdFromRedis(long userId) {
		String key = "STR:HC9:USER:INFO:CARDID:" + userId;
		String cardId = RedisHelper.get(key);
		return cardId;
	}
}
