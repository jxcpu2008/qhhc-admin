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

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.Arith;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.entity.Adminuser;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.ActivityMonkeyQueryService;
import com.hc9.service.BaoFuLoansignService;


/***
 *   后台管理金猴活动及审批
 * @author lkl  20160115
 */
@Controller
@RequestMapping("/activityMonkey")
@CheckLogin(value = CheckLogin.ADMIN)
public class ActivityMonkeyQueryController {
	
	private static final Logger logger = Logger.getLogger(BaoFuLoansignService.class);
	
	@Resource
	private ActivityMonkeyQueryService activityMonkeyQuery;
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	
	@Resource
	private RechargeModel rechargeModel;
	
	private static Map<Integer,String> OPER;
	
	private static Map<Integer,String> OPERSTATUS;
	
	private static Map<Integer,String> OPEREXAMINESTATUS;
	
	private static Map<Integer,String> OPERISAUTHIPS;
	
	static{
		OPER=new HashMap<>();
		OPER.put(1, "猴声大噪(一鸣惊人)");
		OPER.put(2, "猴声大噪(一锤定音)");
		OPER.put(3, "金袍加身(周第一名)");
		OPER.put(4, "金袍加身(周第二名)");
		OPER.put(5, "金袍加身(周第三名)");
		OPER.put(6, "达人第一名(iPhone 6s)");
		OPER.put(7, "达人第二名(Apple Watch Sport)");
		OPER.put(8, "达人第三名(Kindle 电子书)");
		OPER.put(9, "红筹理财师");
		OPER.put(10, "桃花朵朵开");
		OPER.put(11, "翻牌抽奖");
		OPER.put(12, "一鸣惊人");
		OPER.put(13, "一锤定音");
		OPER.put(14, "5月首投活动");
		OPER.put(15, "5月榜单统计");
	} 
	
	static{
		OPERSTATUS=new HashMap<>();
		OPERSTATUS.put(0, "待审核");
		OPERSTATUS.put(1, "已审核");
	} 
	
	static{
		OPERISAUTHIPS=new HashMap<>(); 
		OPERISAUTHIPS.put(0, "待授权");
		OPERISAUTHIPS.put(1, "已授权");
		OPERISAUTHIPS.put(2, "授权失败");
	} 
	
	static{
		OPEREXAMINESTATUS=new HashMap<>();
		OPEREXAMINESTATUS.put(0, "待发放");
		OPEREXAMINESTATUS.put(1, "已发放");
		OPEREXAMINESTATUS.put(2, "发放待确认");
		OPEREXAMINESTATUS.put(-1, "发放失败");
	} 
	
	
	
	@RequestMapping(value = { "indexQuery", "/" })
	public ModelAndView indexQuery() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/activityMonkeyQuery");
		return returnModelAndView;
	}
	
	@RequestMapping(value = { "examineInit", "/" })
	public ModelAndView examineInit() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/activityMonkeyExamine");
		return returnModelAndView;
	}
	

	@RequestMapping(value = { "grantInit", "/" })
	public ModelAndView grantInit() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/activityMonkeyGrant");
		return returnModelAndView;
	}
	
	/***
	 * 红筹理财师活动数据统计
	 * @return
	 */
	@RequestMapping(value = { "indexExamineQuery", "/" })
	public ModelAndView indexExamineQuery() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/activityMonkeyExamineQuery");
		return returnModelAndView;
	}
	
	
	/***
	 * 实时红筹理财师活动数据统计
	 * @return
	 */
	@RequestMapping(value = { "indexFinancial", "/" })
	public ModelAndView indexFinancialQuery() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/activityFinancialQuery");
		return returnModelAndView;
	}
	
	@ResponseBody
	@RequestMapping("activityMonkeyQuery")
	public JSONObject activityMonkeyQuery(String limit, String start,ActivityMonkey activityMonkey, HttpServletRequest request, PageModel page) {

		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		// 分页数据源
		List datalist = activityMonkeyQuery.activityMonkeyQueryPage(page, activityMonkey);
		String titles = "id,name,mobilePhone,money,type,rewardMoney,loanName,createTime,status,examineStatus";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}
	
	/***
	 * 导出数据
	 * @param activityMonkey
	 * @param request
	 * @param response
	 */
	@RequestMapping("outLoanrecordExcel")
	public void outPutLoanrecordExcel(ActivityMonkey activityMonkey, HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "真实姓名", "手机号码", "投资金额", "奖励类型", "奖品/奖金","项目名称", "获奖时间","审核状态","发放状态" };

		// 获取数据源
		List list = activityMonkeyQuery.queryactivityMonkeyList(activityMonkey);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("真实姓名", str[1] + "");
			map.put("手机号码", str[2] + "");
			map.put("投资金额",str[3] == null && str[3] == "" ? "" : Arith.round(new BigDecimal(str[3].toString()), 2) + "元");
			map.put("奖励类型", OPER.get(str[4]));
			map.put("奖品/奖金", str[5] + "");
			map.put("项目名称", str[6] + "");
			map.put("获奖时间", str[7] + "");
			map.put("审核状态",OPERSTATUS.get(str[8]) );
			map.put("发放状态", OPERSTATUS.get(str[9]));
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("金猴活动统计", null, header, content, response,
				request);
	}
	
	/***
	 * 查询
	 * @param limit
	 * @param start
	 * @param activityMonkey
	 * @param request
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("activityMonkeyInit")
	public JSONObject activityMonkeyInit(String limit, String start,ActivityMonkey activityMonkey, HttpServletRequest request, PageModel page) {

		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		// 分页数据源
		List datalist = activityMonkeyQuery.activityMonkeyInitPage(page, activityMonkey);
		String titles = "id,name,mobilePhone,money,type,rewardMoney,loanName,createTime,examineStatus,realname,examineTime";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}
	
	
	/***
	 * 现金奖励数据申请审批
	 * @param id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/activityMonkeyExamine")
	public String activityMonkeyExamine(String id, HttpServletRequest request) {
//		String[] ids=id.split(",");
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		
		String num = activityMonkeyQuery.activityMonkeyExamine(id, loginuser.getId());
		return num;
	}
	
	@ResponseBody
	@RequestMapping("activityMonkeyGrantInit")
	public JSONObject activityMonkeyGrantInit(String limit, String start,ActivityMonkey activityMonkey, HttpServletRequest request, PageModel page) {

		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		// 分页数据源
		List datalist = activityMonkeyQuery.activityMonkeyGrantInitPage(page, activityMonkey);
		String titles = "id,name,mobilePhone,money,type,rewardMoney,loanName,status,realname,grantTime,failreason";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}
	
	
	/***
	 * 导出数据
	 * @param activityMonkey
	 * @param request
	 * @param response
	 */
	@RequestMapping("outPutGrantExcel")
	public void outPutGrantExcel(ActivityMonkey activityMonkey, HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "真实姓名", "手机号码", "投资金额", "奖励类型", "奖品/奖金","项目名称", "发放状态","操作人","发放时间","转账失败原因" };

		// 获取数据源
		List list = activityMonkeyQuery.queryactivityMonkeyGanteList(activityMonkey);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("真实姓名", str[1] + "");
			map.put("手机号码", str[2] + "");
			map.put("投资金额",str[3] == null && str[3] == "" ? "" : Arith.round(new BigDecimal(str[3].toString()), 2) + "元");
			map.put("奖励类型", OPER.get(str[4]));
			map.put("奖品/奖金", str[5] + "");
			map.put("项目名称", str[6] + "");
			map.put("发放状态", OPERSTATUS.get(str[7]));
			map.put("操作人", str[8] + "");
			map.put("发放时间",str[9]+"");
			
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("发放现金奖励", null, header, content, response,
				request);
	}
	
    /***
     * 现金转账
     * @param request
     * @param id
     * @return
     */
	@ResponseBody
	@RequestMapping("ipsTransActivtiyMonkey")
    public  String ipsTransActivtiyMonkey(HttpServletRequest request,String id){
		 String result = "7";
		 String adminIdConcurrentLock = "STR:IPSTRANSBATCHACTIVTIYMONKEY:ADMINUSER:CONCURRENT:LOCK";
		 if(!RedisHelper.isKeyExist(adminIdConcurrentLock)) {
			 RedisHelper.set(adminIdConcurrentLock, adminIdConcurrentLock);
			 result=baoFuLoansignService.ipsTransActivtiyMonkey(request, id);
			 RedisHelper.del(adminIdConcurrentLock);
		 }
		 return result;
    }
	
	/***
	 * 查询发放状态
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("ipsActivtiyMonkeyInfo.htm")
	public synchronized String ipsActivtiyMonkeyInfo(HttpServletRequest request,HttpServletResponse response, String id) throws Exception {
		return baoFuLoansignService.ipsActivtiyMonkeyInfo(request, id);
	}
	
	
	
	/***
	 * 红筹理财师活动数据统计
	 * @param limit
	 * @param start
	 * @param activityMonkey
	 * @param request
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("activityMExamineQuery")
	public JSONObject activityMonkeyExamineQuery(String limit, String start,ActivityMonkey activityMonkey, HttpServletRequest request, PageModel page) {

		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		// 分页数据源
		List datalist = activityMonkeyQuery.activityMonkeyExamine(page, activityMonkey);
		String titles = "id,name,mobilePhone,isAuthIps,money,rewardMoney,createTime,channelName,examineStatus,status";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}
	
	/***
	 * 导出数据
	 * @param activityMonkey
	 * @param request
	 * @param response
	 */
	@RequestMapping("outActivityMExamineExcel")
	public void outActivityMExamineExcel(ActivityMonkey activityMonkey, HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "注册用户", "注册手机号", "宝付授权状态","推荐投资金额", "已达现金奖励标准","注册时间", "渠道来源","审核状态","发放状态" };

		// 获取数据源
		List list = activityMonkeyQuery.queryActivityMExamineList(activityMonkey);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("注册用户", str[1] + "");
			map.put("注册手机号", str[2] + "");
			map.put("宝付授权状态",OPERISAUTHIPS.get(str[3]));
			map.put("推荐投资金额", str[4] == null && str[4] == "" ? "" : Arith.round(new BigDecimal(str[4].toString()), 2) + "元");
			map.put("已达现金奖励标准", str[5] == null && str[5] == "" ? "" : Arith.round(new BigDecimal(str[5].toString()), 2) + "元");
			map.put("注册时间", str[6] + "");
			map.put("渠道来源", str[7] + "");
			map.put("审核状态",OPERSTATUS.get(str[8]) );
			map.put("发放状态", OPERSTATUS.get(str[9]));
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("红筹理财师活动数据统计", null, header, content, response,
				request);
	}
	
	
	/***
	 * 批量现金奖励数据申请审批
	 * @param id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateBatchActivityMonkey")
	public String updateBatchActivityMonkey(String id, HttpServletRequest request) {
		 String result = "4";
		 String adminIdConcurrentLock = "STR:UPDATEBATCHACTIVITYMONKEY:ADMINUSER:CONCURRENT:LOCK";
		 if(!RedisHelper.isKeyExist(adminIdConcurrentLock)) {
			 RedisHelper.set(adminIdConcurrentLock, adminIdConcurrentLock);
			 result=activityMonkeyQuery.updateBatchActivityMonkey(request);
			 RedisHelper.del(adminIdConcurrentLock);
		 }
		 return result;
	}
	
	
	/***
	 * 实时查询筹理财师活动数据
	 * @param limit
	 * @param start
	 * @param activityMonkey
	 * @param request
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("activityFinancialQuery")
	public JSONObject activityFinancialPage(String limit, String start,ActivityMonkey activityMonkey, HttpServletRequest request, PageModel page) {

		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		// 分页数据源
		List datalist = activityMonkeyQuery.activityFinancialPage(page, activityMonkey);
		String titles = "id,name,mobilePhone,money,createTime,channelName,rewardMoney";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}
	
	/***
	 * 导出数据
	 * @param ActivityFinancial
	 * @param request
	 * @param response
	 */
	@RequestMapping("outActivityFinancialExcel")
	public void outActivityFinancialExcel(ActivityMonkey activityMonkey, HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "注册用户", "注册手机号", "推荐投资金额", "已达现金奖励标准","注册时间", "渠道来源" };

		// 获取数据源
		List list = activityMonkeyQuery.queryActivityFinancialList(activityMonkey);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("注册用户", str[1] + "");
			map.put("注册手机号", str[2] + "");
			map.put("推荐投资金额", str[3] == null && str[3] == "" ? "" : Arith.round(new BigDecimal(str[3].toString()), 2) + "元");
			map.put("已达现金奖励标准", str[6] == null && str[6] == "" ? "" : Arith.round(new BigDecimal(str[6].toString()), 2) + "元");
			map.put("注册时间", str[4] + "");
			map.put("渠道来源", str[5] + "");
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("实时红筹理财师活动数据统计", null, header, content, response,
				request);
	}
	
	/**
	 * 现金批量转账
	 * @param request
	 * @param recordIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("ipsTransBatchActivtiyMonkey")
    public String ipsTransBatchActivtiyMonkey(HttpServletRequest request, String recordIds) {
		logger.info("recordIds = " + recordIds);
		String result = "7";
		String adminIdConcurrentLock = "STR:IPSTRANSBATCHACTIVTIYMONKEY:ADMINUSER:CONCURRENT:LOCK";
		if (!RedisHelper.isKeyExist(adminIdConcurrentLock)) {
			RedisHelper.setWithExpireTime(adminIdConcurrentLock, adminIdConcurrentLock, 45);
			result = baoFuLoansignService.ipsTransBatchActivtiyMonkey(request, recordIds);
			RedisHelper.del(adminIdConcurrentLock);
		}
		return result;
    }
	
	/**
	 * 统计总金额
	 * @param request
	 * @param begincreditTime
	 * @param endcreditTime
	 * @return
	 */
	@RequestMapping("/sumActivityMonkey")
	public String sumActivityMonkey(HttpServletRequest request,String begincreditTime,String endcreditTime) {
		//发放总额
        Double sumActivityMonkeyOne=activityMonkeyQuery.getSumRewardMoney(begincreditTime, endcreditTime, 1);
        //待发放
        Double sumActivityMonkeyTwo=activityMonkeyQuery.getSumRewardMoney(begincreditTime, endcreditTime, 2);
        //总额
        Double sumActivityMonkey=activityMonkeyQuery.getSumRewardMoney(begincreditTime, endcreditTime, 0);
        //总额
		request.setAttribute("sumActivityMonkeyOne", sumActivityMonkeyOne);
		request.setAttribute("sumActivityMonkeyTwo", sumActivityMonkeyTwo);
		request.setAttribute("sumActivityMonkey", sumActivityMonkey);
		return "/WEB-INF/views/admin/quickquery/sumActivityMonkey";
	}

	
}
