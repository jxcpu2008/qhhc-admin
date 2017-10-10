package com.hc9.common.redis.activity.year2016.month06;

import java.util.ArrayList;
import java.util.List;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.StringUtil;
import com.hc9.model.activity.LoanPrizeVo;

/** 一鸣惊人、一锤定音活动相关缓存 */
public class HcMaxLatestCache {
	/** 设置一鸣惊人、一锤定音相关中奖记录信息 */
	public static void setMaxLatestPrizeInfo(int type, LoanPrizeVo loanPrizeVo) {
		if(type == 1) {
			String maxKey = "STR:HC9:LOAN:PRIZE:MAX:RECORD";
			String json = JsonUtil.toJsonStr(loanPrizeVo);
			RedisHelper.set(maxKey, json);
		}
		
		if(type == 0) {
			String minKey = "STR:HC9:LOAN:PRIZE:LATEST:RECORD";
			String json = JsonUtil.toJsonStr(loanPrizeVo);
			RedisHelper.set(minKey, json);
		}
		String listKey = "STR:HC9:LOAN:PRIZE:LIST:RECORD";
		String json = RedisHelper.get(listKey);
		List<LoanPrizeVo> resultList = new ArrayList<LoanPrizeVo>();
		resultList.add(loanPrizeVo);
		if(StringUtil.isNotBlank(json)) {
			List<LoanPrizeVo> prizeList = JsonUtil.jsonToList(json, LoanPrizeVo.class);
			resultList.addAll(prizeList);
		}
		RedisHelper.set(listKey, JsonUtil.toJsonStr(resultList));
	}
}

