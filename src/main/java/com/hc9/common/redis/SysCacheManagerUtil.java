package com.hc9.common.redis;

import java.util.Date;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.model.LoginRelVo;
import com.hc9.model.PageModel;
import com.jubaopen.commons.LOG;

/** 系统缓存管理相关工具类：用于操作redis */
public class SysCacheManagerUtil {
	/** 根据登录账号设置用户登录信息 */
	public static void setLoginRelVoByUserName(String userName, String userInfo) {
		String userNameKey = "STR:HC9:USR:LOGING:REL:USERNAME:" + userName;
		RedisHelper.set(userNameKey, userInfo);
	}
	
	/** 根据手机号设置用户登录信息 */
	public static void setLoginRelVoByhone(String phone, String userInfo) {
		String phoneKey = "STR:HC9:USR:LOGING:REL:PHONE:" + phone;
		RedisHelper.set(phoneKey, userInfo);
	}
	
	/** 根据用户id设置用户登录信息 */
	public static void setLoginRelVoById(Long id, String userInfo) {
		String idKey = "STR:HC9:USR:LOGING:REL:ID:" + id;
		RedisHelper.set(idKey, userInfo);
	}
	
	/** 根据员工编号设置用户登录信息 */
	public static void setLoginRelVoByStaffNo(String staffNo, String userInfo) {
		String staffNoKey = "STR:HC9:USR:LOGING:REL:STAFF:NO:" + staffNo;
		RedisHelper.set(staffNoKey, userInfo);
	}

	/** 根据用户登录账号查找用户信息 */
	public static LoginRelVo getLoginRelVoByUserName(String userName) {
		String userNameKey = "STR:HC9:USR:LOGING:REL:USERNAME:" + userName;
		String jsonData = RedisHelper.get(userNameKey);
		if(StringUtil.isBlank(jsonData)) {
			return null;
		}
		LoginRelVo loginRelVo = JsonUtil.jsonToObject(jsonData, LoginRelVo.class);
		return loginRelVo;
	}
	
	/** 根据用户手机号查找用户信息 */
	public static LoginRelVo getLoginRelVoByPhone(String phone) {
		String phoneKey = "STR:HC9:USR:LOGING:REL:PHONE:" + phone;
		String jsonData = RedisHelper.get(phoneKey);
		if(StringUtil.isBlank(jsonData)) {
			return null;
		}
		LoginRelVo loginRelVo = JsonUtil.jsonToObject(jsonData, LoginRelVo.class);
		return loginRelVo;
	}
	
	/** 根据用户id查找用户信息 */
	public static LoginRelVo getLoginRelVoById(String id) {
		String idKey = "STR:HC9:USR:LOGING:REL:ID:" + id;
		String jsonData = RedisHelper.get(idKey);
		if(StringUtil.isBlank(jsonData)) {
			return null;
		}
		LoginRelVo loginRelVo = JsonUtil.jsonToObject(jsonData, LoginRelVo.class);
		return loginRelVo;
	}
	
	/** 根据员工编号查找用户信息 */
	public static LoginRelVo getLoginRelVoByStaffNo(String staffNo) {
		String staffNoKey = "STR:HC9:USR:LOGING:REL:STAFF:NO:" + staffNo;
		String jsonData = RedisHelper.get(staffNoKey);
		if(StringUtil.isBlank(jsonData)) {
			return null;
		}
		LoginRelVo loginRelVo = JsonUtil.jsonToObject(jsonData, LoginRelVo.class);
		return loginRelVo;
	}
	
	/** 根据用户登录账号从redis中查询用户登录信息 */
	public static LoginRelVo getLoginRelVoByLoginNo(String loginNo) {
		LoginRelVo loginRelVo = SysCacheManagerUtil.getLoginRelVoByUserName(loginNo);
		if(loginRelVo == null) {
			loginRelVo = SysCacheManagerUtil.getLoginRelVoByPhone(loginNo);
		}
		if(loginRelVo != null) {
			return loginRelVo;
		}
		return null;
	}
	
	/** 根据账号判断是否已经存在 */
	public static boolean isUserExistInRedis(String loginNo) {
		LoginRelVo vo = getLoginRelVoByLoginNo(loginNo);
		if(vo != null) {
			return true;
		}
		return false;
	}
	
	/** 记录密码登录错误次数 */
	public static int increaseLoginErrorNumToday(String userName) {
		String today = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd");
		String key = "INT:HC9:USR:LOGIN:ERROR:NUM:" + userName + ":" + today;
		int value=getLoginErrorNumToday(userName);
		if(value<5){
			RedisHelper.incrBy(key, 1);
		}
		RedisHelper.expireByKey(key, 24 * 60 * 60);
		return getLoginErrorNumToday(userName);
	}
	
	/** 删除登录失败次数key */
	public static void delLoginErrorNumToday(String userName) {
		String today = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd");
		String key = "INT:HC9:USR:LOGIN:ERROR:NUM:" + userName + ":" + today;
		RedisHelper.del(key);
	}
	
	/** 获取密码登录错误次数 */
	public static int getLoginErrorNumToday(String userName) {
		String today = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd");
		String key = "INT:HC9:USR:LOGIN:ERROR:NUM:" + userName + ":" + today;
		String num = RedisHelper.get(key);
		if(StringUtil.isBlank(num)) {
			num = "0";
		}
		return Integer.valueOf(num);
	}
	
	/** 判断用户密码试错是否锁定过24小时 */
	public static boolean isLockTimeOut(String userName) {
		int num = getLoginErrorNumToday(userName);
		if(num > 5) {
			return false;
		}
		return true;
	}
	
	/** 注册成功后注册人数加一 */
	public static void increaseTotalRegisterNum() {
		try {
			String key = "INT:HC9:USR:REGISTER:TOTAL:NUMS";
			RedisHelper.incrBy(key, 1);
		} catch(Exception e) {
			LOG.error("注册成功后注册人数加一相关数据缓存失败！", e);
		}
	}
	
	/** 我要众持列表首页相关数据处理 */
	public static void setBuyPayLoanListCache(PageModel page) {
		try {
			String buyPayLoanListStr = JsonUtil.toJsonStr(page);
			RedisHelper.set("LST:HC9:LOAN:LIST:BUY:FIRST:PAGE", buyPayLoanListStr);
		} catch(Exception e) {
			LOG.error("我要众持列表首页相关数据缓存失败！", e);
		}
	}
	
	/** 获取我要众持列表首页相关数据列表 */
	public static PageModel getBuyPayLoanListFromRedis() {
		try {
			String buyPayLoanListStr = RedisHelper.get("LST:HC9:LOAN:LIST:BUY:FIRST:PAGE");
			if(StringUtil.isNotBlank(buyPayLoanListStr)) {
				PageModel page = JsonUtil.jsonToObject(buyPayLoanListStr, PageModel.class);
				return page;
			}
		} catch(Exception e) {
			LOG.error("我要众持列表首页相关数据缓存失败！", e);
		}
		return null;
	}
	
	/** H5我要众持列表首页相关数据处理 */
	public static void setH5BuyPayLoanListCache(PageModel page) {
		try {
			String buyPayLoanListStr = JsonUtil.toJsonStr(page);
			RedisHelper.set("LST:HC9:LOAN:LIST:H5:BUY:FIRST:PAGE", buyPayLoanListStr);
		} catch(Exception e) {
			LOG.error("H5我要众持列表首页相关数据缓存失败！", e);
		}
	}
	
	/** H5获取我要众持列表首页相关数据列表 */
	public static PageModel getH5BuyPayLoanListFromRedis() {
		try {
			String buyPayLoanListStr = RedisHelper.get("LST:HC9:LOAN:LIST:H5:BUY:FIRST:PAGE");
			if(StringUtil.isNotBlank(buyPayLoanListStr)) {
				PageModel page = JsonUtil.jsonToObject(buyPayLoanListStr, PageModel.class);
				return page;
			}
		} catch(Exception e) {
			LOG.error("H5我要众持列表首页相关数据缓存失败！", e);
		}
		return null;
	}
}