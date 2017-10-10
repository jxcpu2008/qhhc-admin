package com.hc9.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.model.RegUserInvInfo;
import com.hc9.model.StatisticsInfo;
import com.hc9.model.UserInfo;
import com.hc9.service.ChannelSpreadService;
import com.hc9.service.InvestStatisticsService;
import com.hc9.service.RegisterStatisticsService;

/** 统计报表相关 */
@Controller
@RequestMapping("/statistics")
@CheckLogin(value = CheckLogin.ADMIN)
public class StatisticsController {
	
	private static final Logger logger = Logger.getLogger(StatisticsController.class);
	
	@Autowired
	private ChannelSpreadService channelSpreadService;
	
	/** 注册会员服务层 */
	@Resource
	private RegisterStatisticsService registerStatisticsService;
	
	/** 投资额统计服务 */
	@Resource
	private InvestStatisticsService investStatisticsService;
	
	@Resource
	private RechargeModel rechargeModel;
	
	/** 跳转到后台其他注册统计页面 */
	@RequestMapping("/toOther.htm")
	public String otherRegistrationStatistics(HttpServletRequest request) {
		request.setAttribute("promoteChannelList", channelSpreadService.getPromoteChannelList());
		return "/WEB-INF/views/admin/otherRegistrationStatistics";
	}
	
	/** 跳转到后台注册统计页面 */
	@RequestMapping("/toRegCount.htm")
	public String regCount() {
		return "/WEB-INF/views/admin/regCount";
	}
	
	/** 跳转到后台投资统计页面 */
	@RequestMapping("/toInvCount.htm")
	public String InvCount() {
		return "/WEB-INF/views/admin/invCount";
	}
	
	/** 注册统计 
	 * @param type 1、今日注册量；2、昨日注册量；3、一周注册量；4、本月注册量 */
	@ResponseBody
	@RequestMapping("/registerNum")
	public String registerStatistics(String type, String start, String limit, String beginTime, String endTime) {
		Map<String, Object> resultMap = StatisticsUtil.handleReportTypeAndBeginDate(type, beginTime, endTime);
		Date queryDate = (Date)resultMap.get("queryDate");
		resultMap = registerStatisticsService.queryRegisterBarGraphListAndTime(type, queryDate, beginTime, endTime);
		/** 默认取本月的统计量信息 */
		beginTime = (String)resultMap.get("beginTime");
		endTime = (String)resultMap.get("endTime");
		type = (String)resultMap.get("type");
		/** 总注册人数 */
		Long totalRegisterNum = registerStatisticsService.queryTotalRegisterNum(beginTime, endTime);
		/**平台总注册人数**/
		Long totalRegisterCount=registerStatisticsService.queryTotalRegisterCount();
		/** 用户类型比例数据  */
		List<StatisticsInfo> userRegisterTypeList = registerStatisticsService.queryUserTypeRateMap(beginTime, endTime);
		/** 推广注册会员比例 */
		List<StatisticsInfo> channelRegisterList = registerStatisticsService.queryChannelMemberRateMap(beginTime, endTime);
		/** 注册用户转化情况 */
		List<StatisticsInfo> registerBuyRateList = registerStatisticsService.queryRegisterBuyRate(beginTime, endTime);
		
		resultMap.put("totalRegisterNum", totalRegisterNum);//注册人数
		resultMap.put("totalRegisterCount",totalRegisterCount);//总注册人数
		resultMap.put("userRegisterTypeArea", userRegisterTypeList);//用户类型比例饼图
		resultMap.put("channelMemberArea", channelRegisterList);//推广注册会员饼图
		resultMap.put("registerBuyRate", registerBuyRateList);//注册用户购买信息
		resultMap.put("type", type);
		
		/** 统计信息 */
		String staticsMsg = registerStatisticsService.compositeStaticsMsg(resultMap);
		resultMap.put("staticsMsg", staticsMsg);//统计信息
		
		/** 注册类型饼图 */
		StatisticsUtil.handleAreaGraphLabelPercent(userRegisterTypeList);
		/** 渠道推广饼图 */
		StatisticsUtil.handleAreaGraphLabelPercent(channelRegisterList);
		/** 注册用户购买信息 */
		StatisticsUtil.handleAreaGraphLabelPercent(registerBuyRateList);
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	
	/**  获取注册用户明细列表
	 * 	 @param type 1、今日注册量；2、昨日注册量；3、一周注册量；4、本月注册量
	 *   @param graphType 用于区分柱状图或饼状图的点击，默认为点击菜单进来的初始化注册用户
	 *  */
	@ResponseBody
	@RequestMapping("/queryRegisterUserList")
	public String queryRegisterUserList(String type, String graphType, String start, String limit, String beginTime, String endTime) {
		Map<String, Object> resultMap = StatisticsUtil.handleReportBeginAndEndTime(type, beginTime, endTime);
		beginTime = (String)resultMap.get("beginTime");
		endTime = (String)resultMap.get("endTime");
		resultMap.clear();
		/** 用户资料列表信息及总注册量信息 */
		PageModel page = StatisticsUtil.comsitePageModel(start, limit);
		List<UserInfo> registerUserList = registerStatisticsService.queryRegisterStatisticsByPage(beginTime, endTime, page, null);
		resultMap.put("rows", registerUserList);//注册用户列表
		resultMap.put("total", page.getTotalCount());//总注册量
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	
	@ResponseBody
	@RequestMapping("/queryRegisterUserInvestRecords")
	public String queryRegisterUserInvestRecords(String start, String limit, String paramJsonStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		PageModel page = StatisticsUtil.comsitePageModel(start, limit);
		
		logger.debug("请求参数json字符串：" + paramJsonStr);
		@SuppressWarnings("unchecked")
		Map<String, String> paramsMap = JsonUtil.jsonToObject(paramJsonStr, Map.class);
		
		List<RegUserInvInfo> RegUserInvInfos = registerStatisticsService.queryRegUserInvInfosByPage(paramsMap, page, false);
		resultMap.put("rows", RegUserInvInfos);
		resultMap.put("total", page.getTotalCount());
		return JsonUtil.toJsonStr(resultMap);
	}
	
	@ResponseBody
	@RequestMapping("/queryChartData")
	public String queryChartData(String paramJsonStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("name", "jerry");
		resultMap.put("hello", "world");
		
		logger.debug("请求参数json字符串：" + paramJsonStr);
		@SuppressWarnings("unchecked")
		Map<String, String> paramsMap = JsonUtil.jsonToObject(paramJsonStr, Map.class);
		
		String regTerminal = paramsMap.get("regTerminal");
		String regChannel = paramsMap.get("regChannel");
		String regBeginTime = paramsMap.get("regStartTime");
		String regEndTime = paramsMap.get("regEndTime");
		String regChannelName = paramsMap.get("regChannelName");
		
		// 端口注册人数
		resultMap.put("terminalRegNumRecords", registerStatisticsService.getTerminalRegNumChartData(regTerminal, regBeginTime, regEndTime));
		// 端口认购人数
		resultMap.put("terminalInvNumRecords", registerStatisticsService.getTerminalInvNumChartData(regTerminal, regBeginTime, regEndTime));
		// 渠道注册人数
		resultMap.put("channelRegNumRecords", registerStatisticsService.getChannelRegNumChartData(regChannel, regChannelName, regBeginTime, regEndTime));
		// 渠道认购人数
		resultMap.put("channelInvNumRecords", registerStatisticsService.getChannelInvNumChartData(regChannel, regChannelName, regBeginTime, regEndTime));
		return JsonUtil.toJsonStr(resultMap);
	}
	
	/** 注册用户明细列表下载 */
	@ResponseBody
	@RequestMapping("/downloadRegisterUser")
	public boolean downloadRegisterUser(String type, String graphType, String beginTime, String endTime, HttpServletResponse response) {
		Map<String, Object> resultMap = StatisticsUtil.handleReportBeginAndEndTime(type, beginTime, endTime);
		beginTime = (String)resultMap.get("beginTime");
		endTime = (String)resultMap.get("endTime");
		resultMap.clear();
		
		/** 用户资料列表信息及总注册量信息 */
		List<UserInfo> registerUserList = registerStatisticsService.queryRegisterStatisticsByPage(beginTime, endTime, null, "1");
		String title = "注册用户统计报表";
		String[] header = {"登录账号", "姓名", "用户类型", "可用余额 ", "注册宝付状态", "推荐人", "推荐人部门", "注册时间","渠道名称","推荐人用户类型"};
		List<Map<String, String>> content = registerStatisticsService.handleRegisterNumList(registerUserList);
		boolean result = rechargeModel.downloadExcel(title, null, header, content, response);
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/downloadInvestUser")
	public boolean downloadInvestUser(String paramJsonStr, HttpServletResponse response) {
		logger.debug("请求参数json字符串：" + paramJsonStr);
		@SuppressWarnings("unchecked")
		Map<String, String> paramsMap = JsonUtil.jsonToObject(paramJsonStr, Map.class);
		
		List<RegUserInvInfo> regUserInvInfos = registerStatisticsService.queryRegUserInvInfosByPage(paramsMap, null, true);
		String title = "注册用户认购统计报表";
		String[] header = {"用户名称", "真实姓名", "手机号", "注册时间", "注册端口", "注册渠道", "宝付授权状态", "投资项目", "认购金额", "认购日期", "被推荐人"};
		Integer[] cloumnWidth = {15, 15, 15, 20, 15, 20, 20, 30, 15, 20, 20};
		List<Map<String, String>> content = registerStatisticsService.handleInvUserList(regUserInvInfos);
		boolean result = rechargeModel.downloadExcel(title, cloumnWidth, header, content, response);
		return result;
	}
	
	/** 投资额统计
	 * @param type 1、今日投资金额；2、昨日投资金额；3、一周投资金额；4、本月投资金额
	 *  */
	@ResponseBody
	@RequestMapping("/investMoney")
	public String investMoneyStatistics(String type, String start, String limit, String beginTime, String endTime) {
		Map<String, Object> resultMap = StatisticsUtil.handleReportTypeAndBeginDate(type, beginTime, endTime);
		Date queryDate = (Date)resultMap.get("queryDate");
		type = (String)resultMap.get("type");
		
		resultMap = investStatisticsService.queryInvestBarGrapListAndTime(type, queryDate);//投资柱状图相关数据及时间段处理
		/** 给定时间段的开始时间和结束时间 */
		beginTime = (String)resultMap.get("beginTime");
		endTime = (String)resultMap.get("endTime");
		
		Double totalInvestMoney = investStatisticsService.queryTotalInvestMoney(beginTime, endTime);//投资金额
		Double totalInvestMoneyCount=investStatisticsService.queryTotalInvestMoneyCount(); //总投资金额
		Long totalInvestCount=investStatisticsService.queryTotalInvestCount(); //总投资人数
		/** 认购业绩来源饼图 */
		List<StatisticsInfo> investSourceAreaList = investStatisticsService.queryInvestAreaList(beginTime, endTime);
		/** 认购期限占比饼图 */
		List<StatisticsInfo> investPeriodAreaList = investStatisticsService.queryInvestPeriodAreaList(beginTime, endTime);
		/** 认购类型占比饼图 */
		List<StatisticsInfo> investTypeAreaList = investStatisticsService.queryInvestTypeAreaList(beginTime, endTime);
		
		
		resultMap.put("totalInvestMoney", totalInvestMoney);//投资金额
		resultMap.put("totalInvestMoneyCount", totalInvestMoneyCount);//总投资金额
		resultMap.put("totalInvestCount", totalInvestCount); //总投资人数
		resultMap.put("investSourceAreaList", investSourceAreaList);//认购业绩来源饼图
		resultMap.put("investPeriodAreaList", investPeriodAreaList);//投资期限占比饼图
		resultMap.put("investTypeAreaList", investTypeAreaList);//认购类型占比饼图
		resultMap.put("type", type);
		
		/** 统计信息 */
		String staticsMsg = investStatisticsService.compositeStaticsMsg(resultMap);
		resultMap.put("staticsMsg", staticsMsg);//统计信息
		
		/** 认购业绩来源饼图相关 */
		StatisticsUtil.handleAreaGraphLabelPercent(investSourceAreaList);
		/** 认购期限占比 */
		StatisticsUtil.handleAreaGraphLabelPercent(investPeriodAreaList);
		/** 认购类型饼图 */
		StatisticsUtil.handleAreaGraphLabelPercent(investTypeAreaList);
		
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	
	/**  获取投资用户明细列表
	 *   @param type 1、今日注册量；2、昨日注册量；3、一周注册量；4、本月注册量
	 *   @param graphType 用于区分柱状图或饼状图的点击，默认为点击菜单进来的初始化注册用户
	 *  */
	@ResponseBody
	@RequestMapping("/queryInvestUserList")
	public String queryInvestUserList(String type, String graphType, String start, String limit, String beginTime, String endTime) {
		Map<String, Object> resultMap = StatisticsUtil.handleReportBeginAndEndTime(type, beginTime, endTime);
		beginTime = (String)resultMap.get("beginTime");
		endTime = (String)resultMap.get("endTime");
		resultMap.clear();
		/** 用户资料列表信息及总数量信息 */
		PageModel page = StatisticsUtil.comsitePageModel(start, limit);
		List<UserInfo> investUserList =  investStatisticsService.queryInvestUserList(beginTime, endTime, page, null);
		resultMap.put("rows", investUserList);//投资用户明细列表
		resultMap.put("total", page.getTotalCount());//总投资用户记录数
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	
	/** 投资明细列表下载 */
	@ResponseBody
	@RequestMapping("/downInvestUserList")
	public boolean downInvestUserList(String type, String graphType, String beginTime, String endTime, HttpServletResponse response) {
		Map<String, Object> resultMap = StatisticsUtil.handleReportBeginAndEndTime(type, beginTime, endTime);
		beginTime = (String)resultMap.get("beginTime");
		endTime = (String)resultMap.get("endTime");
		resultMap.clear();
		List<UserInfo> investUserList =  investStatisticsService.queryInvestUserList(beginTime, endTime, null , "1");
		
		String title = "投资统计报表";
		String[] header = {"登录账号", "姓名", "用户类型","项目名称", "项目状态", "认购金额", "认购时间", "投资类型", "推荐人", "推荐人部门","推荐人用户类型", "此单佣金", "上次登陆时间"};
		List<Map<String, String>> content = investStatisticsService.handleInvestUserList(investUserList);
		boolean result = rechargeModel.downloadExcel(title, null, header, content, response);
		return result;
	}
}