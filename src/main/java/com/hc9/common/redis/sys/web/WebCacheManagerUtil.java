package com.hc9.common.redis.sys.web;

import java.util.ArrayList;
import java.util.List;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.sys.vo.ArticleFullVo;
import com.hc9.common.redis.sys.vo.ArticleVo;
import com.hc9.common.redis.sys.vo.BannerVo;
import com.hc9.common.redis.sys.vo.DeputysectionVo;
import com.hc9.common.redis.sys.vo.LoanDynamicVo;
import com.hc9.common.redis.sys.vo.LoansignVo;
import com.hc9.common.redis.sys.vo.TopicVo;
import com.hc9.model.LoanRecommendVo;
import com.hc9.model.LoanlistVo;

/** web专用缓存相关 */
public class WebCacheManagerUtil {
	/** 更新pc首页导航条列表相关缓存 */
	public static void setWebBannerListToRedis(List<BannerVo> bannerVoList) {
		if(bannerVoList != null && bannerVoList.size() > 0) {
			String key = "LIST:HC9:INDEX:BANNER";
			String json = JsonUtil.toJsonStr(bannerVoList);
			RedisHelper.set(key, json);
		}
	}
	
	/** 从缓存中获取pc首页导航条列表相关数据 */
	public static List<BannerVo> getWebBannerListFromRedis() {
		List<BannerVo> bannerVoList = new ArrayList<BannerVo>();
		String key="LIST:HC9:INDEX:BANNER";
		String json = RedisHelper.get(key);
		if(json != null && json.trim().length() > 0) {
			bannerVoList = JsonUtil.jsonToList(json, BannerVo.class);
		}
		return bannerVoList;
	}
	
	/** 更新pc首页相关文章列表至redis缓存 */
	public static void setWebArticleListToRedis(List<ArticleVo> articleList) {
		if(articleList != null && articleList.size() > 0) {
			String key="LIST:HC9:INDEX:ARTICLE";
			String json = JsonUtil.toJsonStr(articleList);
			RedisHelper.set(key, json);
		}
	}
	
	/** 从缓存中获取首页文章列表 */
	public static List<ArticleVo> getWebArticleListFromRedis() {
		List<ArticleVo> articleList = new ArrayList<ArticleVo>();
		String key="LIST:HC9:INDEX:ARTICLE";
		String json = RedisHelper.get(key);
		if(json != null && json.trim().length() > 0) {
			articleList = JsonUtil.jsonToList(json, ArticleVo.class);
		}
		return articleList;
	}
	
	/** 更新首页项目动态列表至redis缓存 */
	public static void setWebLoanDynamicListToRedis(List<LoanDynamicVo> loanDynamicVoList) {
		if(loanDynamicVoList != null && loanDynamicVoList.size() > 0) {
			String key="LIST:HC9:INDEX:LOAN:REPAYINFO";
			String json = JsonUtil.toJsonStr(loanDynamicVoList);
			RedisHelper.set(key, json);
		}
	}
	
	/** 从缓存中获取首页项目动态列表 */
	public static List<LoanDynamicVo> getWebLoanDynamicVoListFromRedis() {
		List<LoanDynamicVo> dynamicList = new ArrayList<LoanDynamicVo>();
		String key="LIST:HC9:INDEX:LOAN:REPAYINFO";
		String json = RedisHelper.get(key);
		if(json != null && json.trim().length() > 0) {
			dynamicList = JsonUtil.jsonToList(json, LoanDynamicVo.class);
		}
		return dynamicList;
	}
	
	/** 设置热门推荐列表至redis缓存 */
	public static void setWebRecommandLoanListToRedis(List<LoanRecommendVo> recommandList) {
		if(recommandList != null && recommandList.size() > 0) {
			String key="LIST:HC9:INDEX:LOAN:RECOMMAND";
			String json = JsonUtil.toJsonStr(recommandList);
			RedisHelper.set(key, json);
		}
	}
	
	/** 从缓存中获取热门推荐列表 */
	public static List<LoanRecommendVo> getWebRecommandLoanListFromRedis() {
		List<LoanRecommendVo> recommandList = new ArrayList<LoanRecommendVo>();
		String key = "LIST:HC9:INDEX:LOAN:RECOMMAND";
		String json = RedisHelper.get(key);
		if(json != null && json.trim().length() > 0) {
			recommandList = JsonUtil.jsonToList(json, LoanRecommendVo.class);
		}
		return recommandList;
	}
	
	/** 更新首页标列表至redis缓存 */
	public static void setWebIndexLoanListToRedis(List<LoanlistVo> loanList) {
		if(loanList != null && loanList.size() > 0) {
			String key="LIST:HC9:INDEX:LOAN:LIST";
			String json = JsonUtil.toJsonStr(loanList);
			RedisHelper.set(key, json);
		}
	}
	
	/** 从缓存中获取首页标列表 */
	public static List<LoanlistVo> getWebIndexLoanListListFromRedis() {
		List<LoanlistVo> loanList = new ArrayList<LoanlistVo>();
		String key = "LIST:HC9:INDEX:LOAN:LIST";
		String json = RedisHelper.get(key);
		if(json != null && json.trim().length() > 0) {
			loanList = JsonUtil.jsonToList(json, LoanlistVo.class);
		}
		return loanList;
	}
	
	/** 更新标详情信息至redis中 */
	public static void setWebLoanSignDetailToRedis(LoansignVo loansignVo) {
		if(loansignVo != null) {
			long loanId = loansignVo.getId();
			String key = "LST:HC9:LOANSIGN:DETAIL:LOANID:" + loanId;
			String json = JsonUtil.toJsonStr(loansignVo);
			RedisHelper.set(key, json);
		}
	}
	
	/** 从redis中获取标详情信息 */
	public static LoansignVo getWebLoanSignDetailFromRedis(String loanId) {
		LoansignVo loansignVo = null;
		String key = "LST:HC9:LOANSIGN:DETAIL:LOANID:" + loanId;
		String json = RedisHelper.get(key);
		if(json != null && json.trim().length() > 0) {
			loansignVo = JsonUtil.jsonToObject(json, LoansignVo.class);
		}
		return loansignVo;
	}
	/**
	 * 把topic存到缓存
	 * @param topicVos
	 */
	public static void setTopicsToRedis(List<TopicVo> topicVos) {
		if(topicVos != null) {
			String key="LIST:HC9:NEWS:TOPICS";
			String json = JsonUtil.toJsonStr(topicVos);
			RedisHelper.set(key, json);
		}
	}

	public static void setDeputysectionToRedis(List<DeputysectionVo> deputysectionVos) {
		if(deputysectionVos != null) {
			String key="LIST:HC9:NEWS:DEPUTYSECTION";
			String json = JsonUtil.toJsonStr(deputysectionVos);
			RedisHelper.set(key, json);
		}
	}

	public static void setArticlesToRedis(List<ArticleFullVo> articles) {
		if(articles != null) {
			String key="LIST:HC9:NEWS:ARTICLE";
			String json = JsonUtil.toJsonStr(articles);
			RedisHelper.set(key, json);
		}
	}
}
