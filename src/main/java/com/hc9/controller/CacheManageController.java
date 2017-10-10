package com.hc9.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.quartz.SmsEmailTimer;
import com.hc9.service.AprilActivityService;
import com.hc9.service.CacheManagerService;
import com.hc9.service.activity.year2016.Month05ActivityService;

/** 换成管理相关入口 */
@Controller
@RequestMapping("/cachemanage")
@CheckLogin(value=CheckLogin.ADMIN)
public class CacheManageController {
	
	@Resource
	private CacheManagerService cacheManagerService;
	
	@Resource
	private AprilActivityService aprilActivityService;
	
	@Resource
	private SmsEmailTimer smsEmailTimer;
	
	@Resource
	private Month05ActivityService month05ActivityService;
	
	/** 跳转到缓存管理页面 */
	@RequestMapping("/displayPage")
	public String displayPage(HttpServletRequest request) {
		
		return "/WEB-INF/views/admin/cache/cachemanage";
	}
	
	/** 登录相关缓存信息更新 */
	@RequestMapping("/updateLoginRelCache.htm")
	@ResponseBody
	public String updateLoginRelCache() {
		Map<String, String> resultMap = cacheManagerService.updateLoginRelCache();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 首页导航条相关缓存更新 */
	@RequestMapping("/updateIndexBannerRelCache.htm")
	@ResponseBody
	public String updateIndexBannerRelCache() {
		Map<String, String> resultMap = cacheManagerService.updateIndexBannerRelCache();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 累计投资相关缓存更新 */
	@RequestMapping("/updateTotalInvestMoney.htm")
	@ResponseBody
	public String updateTotalInvestMoney() {
		Map<String, String> resultMap = cacheManagerService.updateTotalInvestMoney();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 总注册人数相关缓存更新 */
	@RequestMapping("/updateTotalRegisterNum.htm")
	@ResponseBody
	public String updateTotalRegisterNum() {
		Map<String, String> resultMap = cacheManagerService.updateTotalRegisterNum();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 文章列表相关缓存更新 */
	@RequestMapping("/updateArticleList.htm")
	@ResponseBody
	public String updateArticleList() {
		Map<String, String> resultMap = cacheManagerService.updateArticleList();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 待回款项目列表相关缓存更新 */
	@RequestMapping("/updateToReturnLoanList.htm")
	@ResponseBody
	public String updateToReturnLoanList() {
		Map<String, String> resultMap = cacheManagerService.updateArticleList();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 热门推荐列表相关缓存更新 */
	@RequestMapping("/hotIntroduceLoanList.htm")
	@ResponseBody
	public String hotIntroduceLoanList() {
		Map<String, String> resultMap = cacheManagerService.hotIntroduceLoanList();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 首页标列表更新 */
	@RequestMapping("/updateIndexLoanList.htm")
	@ResponseBody
	public String updateIndexLoanList() {
		Map<String, String> resultMap = cacheManagerService.updateIndexLoanList();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 众持页面首页标列表相关缓存更新 */
	@RequestMapping("/updateZhongChiPageLoanList.htm")
	@ResponseBody
	public String updateZhongChiPageLoanList() {
		Map<String, String> resultMap = cacheManagerService.updateZhongChiPageLoanList();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 更新标详情缓存信息 */
	@RequestMapping("/updateLoanDetailRelCache.htm")
	@ResponseBody
	public String updateLoanDetailRelCache(String loanSignId) {
		Map<String, String> resultMap = cacheManagerService.updateLoanDetailRelCache(loanSignId);
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 更新所有标详细缓存信息 */
	@RequestMapping("/updateAllLoanDetailRelCache.htm")
	@ResponseBody
	public String updateAllLoanDetailRelCache() {
		Map<String, String> resultMap = cacheManagerService.updateAllLoanDetailRelCache();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 邮件短信查询数据更新 */
	@RequestMapping("/updateInvestorstasinfo.htm")
	@ResponseBody
	public String updateInvestorstasinfo(){
		Map<String, String> resultMap =new HashMap<String, String>();
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		smsEmailTimer.updateUserBasicStatisticsInfo();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** H5众持页面首页标列表相关缓存更新 */
	@RequestMapping("/updateH5ZhongChiPageLoanList.htm")
	@ResponseBody
	public String updateH5ZhongChiPageLoanList() {
		Map<String, String> resultMap = cacheManagerService.updateH5ZhongChiPageLoanList();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** H5首页导航条相关缓存更新 */
	@RequestMapping("/updateH5IndexBannerRelCache.htm")
	@ResponseBody
	public String updateH5IndexBannerRelCache() {
		Map<String, String> resultMap = cacheManagerService.updateH5IndexBannerRelCache();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** H5累计收益相关缓存更新 */
	@RequestMapping("/updateH5TotalIncome.htm")
	@ResponseBody
	public String updateH5TotalIncome() {
		Map<String, String> resultMap = cacheManagerService.updateH5TotalIncome();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** H5热门推荐列表相关缓存更新 */
	@RequestMapping("/updateH5HotIntroduceLoanList.htm")
	@ResponseBody
	public String updateH5HotIntroduceLoanList() {
		Map<String, String> resultMap = cacheManagerService.updateH5HotIntroduceLoanList();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 更新猴声大噪缓标存信息 */
	@RequestMapping("/updateActivityMonkeyMax.htm")
	@ResponseBody
	public String updateActivityMonkeyMax(String monkeyLoanSignId) {
		Map<String, String> resultMap = cacheManagerService.updateActivityMonkeyMax(monkeyLoanSignId);
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 更新猴声大噪周榜到数据库 */
	@RequestMapping("/updateActivityMonkeyWEEK.htm")
	@ResponseBody
	public String updateActivityMonkeyWEEK() {
		Map<String, String> resultMap = cacheManagerService.updateActivityMonkeyWEEK();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 更新理财师现金返利排行榜到数据库 */
	@RequestMapping("/updateActivityFinancial.htm")
	@ResponseBody
	public String updateActivityFinancial() {
		Map<String, String> resultMap = cacheManagerService.updateActivityFinancial();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 爬山活动奖励结果一键生成 */
	@RequestMapping("/generateClimbTopResult.htm")
	@ResponseBody
	public String generateClimbTopResult() {
		Map<String, String> resultMap = aprilActivityService.generateClimbTopResult();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 2016年5月新手任务相关关注微信根据手机号标注领取 */
	@RequestMapping("/webChatAttentionReceive.htm")
	@ResponseBody
	public String webChatAttentionReceive(String phone) {
		Map<String, String> resultMap = month05ActivityService.webChatAttentionReceive(phone);
		return JsonUtil.toJsonStr(resultMap);
	}
	/** 母亲节活动红包发放 */
	@RequestMapping("/MonthersDayHb.htm")
	@ResponseBody
	public String MonthersDayHb(String phone) {
		Map<String, String> resultMap = month05ActivityService.monthersDay(phone);
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 后台手工发送“周周惊喜大放送”活动奖励 */
	@RequestMapping("/weekSurprisePrizeGive.htm")
	@ResponseBody
	public String weekSurprisePrizeGive(int weekNum) {
		Map<String, String> resultMap = month05ActivityService.weekSurprisePrizeGive(weekNum);
		return JsonUtil.toJsonStr(resultMap);
	}
	  
	
	/** 后台手工更新演唱会门票状态为已发放 */
	@RequestMapping("/updateJuChengTicketStatus.htm")
	@ResponseBody
	public String updateJuChengTicketStatus() {
		Map<String, String> resultMap = month05ActivityService.updateJuChengTicketStatus();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/**app启动画面开关*/
	@RequestMapping("appStartPictureSwitch.html")
	@ResponseBody
	public String appStartPictureSwitch(int pictureSwitch){
     Map<String,String>	resultMap=cacheManagerService.appStartPictureSwitch(pictureSwitch);	
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/**app启动画面图片地址*/
	@RequestMapping("updateAppStartImageAddress.html")
	@ResponseBody
	public String appStartImageAddress(String url){
		Map<String,String> resultMap=cacheManagerService.updateAppStartImageAddress(url);
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 投资有奖聚橙网门票发放 */
	@RequestMapping("juChengTicketInvestAward.htm")
	@ResponseBody
	public String juChengTicketInvestAward(String loanId){
		Map<String,String> resultMap = cacheManagerService.juChengTicketInvestAward(loanId);
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 聚橙网推荐用户门票发放 */
	@RequestMapping("juChengGenUserTcketAward.htm")
	@ResponseBody
	public String juChengGenUserTcketAward(){
		Map<String,String> resultMap = cacheManagerService.juChengGenUserTcketAward();
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 微博活动门票发放 */
	@RequestMapping("juChengWeiBoTcketAward.htm")
	@ResponseBody
	public String juChengWeiBoTcketAward(String phone){
		Map<String,String> resultMap = cacheManagerService.juChengWeiBoTcketAward(phone);
		return JsonUtil.toJsonStr(resultMap);
	}
}