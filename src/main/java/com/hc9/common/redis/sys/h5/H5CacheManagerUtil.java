package com.hc9.common.redis.sys.h5;

import java.util.ArrayList;
import java.util.List;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.sys.vo.BannerVo;

/** H5专用缓存相关 */
public class H5CacheManagerUtil {
	/** 更新h5首页导航条列表相关缓存 */
	public static void setH5BannerListToRedis(List<BannerVo> bannerVoList) {
		if(bannerVoList != null && bannerVoList.size() > 0) {
			String key = "LIST:HC9:INDEX:H5:BANNER";
			String json = JsonUtil.toJsonStr(bannerVoList);
			RedisHelper.set(key, json);
		}
	}
	
	/** 从缓存中获取h5首页导航条列表相关数据 */
	public static List<BannerVo> getH5BannerListFromRedis() {
		List<BannerVo> bannerVoList = new ArrayList<BannerVo>();
		String key = "LIST:HC9:INDEX:H5:BANNER";
		String json = RedisHelper.get(key);
		if(json != null && json.trim().length() > 0) {
			bannerVoList = JsonUtil.jsonToList(json, BannerVo.class);
		}
		return bannerVoList;
	}
}
