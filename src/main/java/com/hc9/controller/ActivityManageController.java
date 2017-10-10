package com.hc9.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.util.Arith;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.FirstInvenstStaticsDetail;
import com.hc9.dao.entity.MayTopListStaticsDetail;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.MayTopListStaticsService;
import com.hc9.service.activityManageService;

/** 首投活动查询与5月榜单统计查询 */
@Controller
@RequestMapping(value = { "/activityManage" })
@CheckLogin(value=CheckLogin.ADMIN)
public class ActivityManageController {
	/** 首投活动列表查询服务层 */
	@Resource
	private activityManageService activityManage;
	/** 5月榜单列表查询服务层 */
	@Resource
	private MayTopListStaticsService mayTopListStatics;
	/** Excel导出 */
	@Resource
	private RechargeModel rechargeModel;
	/** 奖励金额 */
	private static Map<Integer, String> REWARDMONEY;
	/** 活动类型 */
	private static Map<Integer, String> TYPE;
	/** 产品属性 */
	private static Map<Integer, String> SUBTYPE;

	static {
		REWARDMONEY = new HashMap<>();
		REWARDMONEY.put(10, "10元");
		REWARDMONEY.put(30, "30元");
		REWARDMONEY.put(60, "60元");
	}

	static {
		TYPE = new HashMap<>();
		TYPE.put(14, "5月首投活动");
	}

	static {
		SUBTYPE = new HashMap<>();
		SUBTYPE.put(1, "优先");
		SUBTYPE.put(2, "夹层");
		SUBTYPE.put(3, "列后");
		SUBTYPE.put(4, "vip众筹");
		SUBTYPE.put(5, "股东众筹");
	}

	/** 分页查询首投活动统计表 */
	@RequestMapping(value = { "firstInvestStatics", "/" })
	public ModelAndView firstInvestStatics() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/activityManage");
		return returnModelAndView;
	}

	/** 分页查询5月榜单统计 */
	@RequestMapping(value = { "mayTopListStatics", "/" })
	public ModelAndView mayListStatistics() {
		ModelAndView mayListView = new ModelAndView("WEB-INF/views/admin/quickquery/mayTopListStatics");
		return mayListView;
	}

	/** 首投活动列表查询 */
	@ResponseBody
	@RequestMapping("firstInvestStaticsQuery")
	public JSONObject firstInvestStaticsQuery(String limit, String start,
			FirstInvenstStaticsDetail firstInvenstStaticDetail, HttpServletRequest request, PageModel page) {
		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer.parseInt(limit) : 40);
		} else {
			page.setNumPerPage(40);
		}
		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}
      
		// 分页数据源
		@SuppressWarnings("rawtypes")
		List datalist = activityManage.activityManagePage(page, firstInvenstStaticDetail);
		String titles = "userName,name,mobilePhone,createTime,tenderTime,tenderMoney,loanSignName,subType,rewardMoney,type";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}

	/** 5月榜单的列表查询 */
	@ResponseBody
	@RequestMapping("mayTopListStaticsQuery")
	public String mayTopListStaticsQuery(String name, Integer weekNum, Integer ranking,PageModel page) {
		if (weekNum == null || weekNum < 0) {
			weekNum = 1;
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<MayTopListStaticsDetail> investUserList=new ArrayList<MayTopListStaticsDetail>();
		if(weekNum==0){
			investUserList = mayTopListStatics.mayTopListStaticsWholePage(weekNum,name,ranking);
		}else {
		/**获取数据源*/
		 investUserList = mayTopListStatics.mayTopListStaticsPage(name, weekNum, ranking);
		}
		 /**显示条数*/
		page.setTotalCount(investUserList.size());
		resultMap.put("rows", investUserList);// 5月榜单明细列表
		resultMap.put("total", page.getTotalCount());
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}

	/** 导出首投活动列表的数据 */
	@RequestMapping("outActivityManageExcel")
	public void outActivityManageExcel(FirstInvenstStaticsDetail firstInvenstStaticDetail, HttpServletRequest request,
			HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "首投用户名", "真实姓名", "手机号码", "注册时间", "认购时间", "认购金额", "认购产品", "产品属性", "奖励金额","活动类型" };
		// 获取数据源
		@SuppressWarnings("rawtypes")
		List list = activityManage.QueryActivityManage(firstInvenstStaticDetail);
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("首投用户名", str[0] + "");
			map.put("真实姓名", str[1] + "");
			map.put("手机号码", str[2] + "");
			map.put("注册时间", str[3] + "");
			map.put("认购时间", str[4] + "");
			map.put("认购金额", str[5] + "");
			map.put("认购产品", str[6] + "");
			map.put("产品属性", SUBTYPE.get(str[7]));
			map.put("奖励金额", REWARDMONEY.get(Arith.round(new BigDecimal(str[8].toString()), 0).intValue()) + "");
			map.put("活动类型", TYPE.get(str[9]));
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("首投活动统计", null, header, content, response, request);
	}

	/** 导出5月榜单动列表的数据 */
	@RequestMapping("outMayTopExcel")
	public void outMayTopExcel(String name, Integer weekNum, Integer ranking, HttpServletResponse response) {
		if (weekNum == null || weekNum < 0) {
			weekNum = 1;
		}
		// 获取数据源
		List<MayTopListStaticsDetail> mayTList = new ArrayList<MayTopListStaticsDetail>();
		if(weekNum==0){
			mayTList = mayTopListStatics.mayTopListStaticsWholePage(weekNum,name,ranking);
		}else {
		/**获取数据源*/
			mayTList = mayTopListStatics.mayTopListStaticsPage(name, weekNum, ranking);
		}

		String[] header = new String[] { "名次", "用户名", "真实姓名", "手机号", "注册时间", "累计认购金额", "累计认购年化金额", "奖励", "获奖时间" };
		// 处理5月榜单相关下载数据
		List<Map<String, String>> content = mayTopListStatics.handMayTopList(mayTList);
		// 下载excel
		rechargeModel.downloadExcel("5月榜单统计", null, header, content, response);
	}
}
