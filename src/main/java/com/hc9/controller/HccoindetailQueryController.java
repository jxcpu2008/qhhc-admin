package com.hc9.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.HccoindetailQueryService;
import com.hc9.service.HccoindetailService;
import com.hc9.service.UserbasicsinfoService;

@RequestMapping("/hccoindetailQuery")
@Controller
@CheckLogin(value=CheckLogin.ADMIN)
public class HccoindetailQueryController {
	
	@Resource
	private HccoindetailQueryService   hccoindetailQueryService;
	
	@Resource
	private HccoindetailService hccoindetailService;
	
	@Resource
	private RechargeModel rechargeModel;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@RequestMapping(value = { "openIndex", "/" })
	public ModelAndView index() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/hccoindetailCount");
		return returnModelAndView;
	}

	@ResponseBody
	@RequestMapping(value = { "userHccoindetailList", "/" })
	public JSONObject userHccoindetailList(String limit, String start,String beginDateH,String endDateH,String name,HttpServletRequest request, PageModel page) {

		JSONObject resultjson = new JSONObject();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 10);
		} else {
			page.setNumPerPage(10);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		// 分页数据源
		List list = hccoindetailQueryService.getUserHccoindetailList(page, beginDateH, endDateH,name);
		
		JSONArray jsonlist = new JSONArray();
		
		String titles = "id,name,countgen,isAuthIpsCount,sumtendermoney,sumnumber,sumwkt,sumqqj,sumtsg,sumhdjl,sumpmh,sumqt,sumzs";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}
	
	
	/***
	 * 显示个人红筹币明细
	 * @param start
	 * @param limit
	 * @param userId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "hccoindetailList", "/" })
	public JSONObject hccoindetailList(
			@RequestParam(value = "start", defaultValue = "0", required = false) int start,
			@RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
			@RequestParam(value = "id", defaultValue = "", required = false) int userId,
			HttpServletRequest request) {
		
		// 得到总条数
		Object count = hccoindetailQueryService.getHccoindetailCount(userId);
		// 分页数据源
		List list =hccoindetailQueryService.queryHccoindetailList(start, limit, userId,1);
	    
	    JSONArray jsonlist = hccoindetailQueryService.getJSONArrayByList(list);

		JSONObject resultjson = new JSONObject();
		// 将数据源封装为json对象（命名必须rows）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", count);
		return resultjson;
	}
	
	/***
	 * 个人红筹币导出明细数据
	 * @param userId
	 * @param request
	 * @param response
	 */
	@RequestMapping("outPutHccoindetailExcel")
	public void outPutHccoindetailExcel(int userId, HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "真实用户名", "操作时间", "操作事由", "红筹币增减"};
		Userbasicsinfo user = userbasicsinfoService.queryUserById(String.valueOf(userId));
		// 获取数据源
		List list =hccoindetailQueryService.queryHccoindetailList(0, 0, userId,2);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("真实用户名", str[0] + "");
			map.put("操作时间", str[1] + "");
			map.put("操作事由", str[2] + "");
			map.put("红筹币增减", str[3] + "");
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel(user.getName()+"红筹币明细", null, header, content, response,request);
	}
	
	
	/***
	 * 导出所有员工的红筹币记录
	 * @param beginDate
	 * @param endDate
	 * @param request
	 * @param response
	 */
	@RequestMapping("outUserHccoindetail")
	public void outPutUserHccoindetailExcel(String beginDateH, String endDateH, HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "序号", "员工姓名", "推荐注册人数","已认证注册人数", "推荐总投资额", "所得红筹币数", "微课堂", "全勤奖", "图书馆", "活动奖励", "拍卖会", "其他","红筹币总数"};
		int headerLength = header.length;
		// 获取数据源
		List<Object> list =hccoindetailQueryService.queryUserHccoindetail(beginDateH, endDateH);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] result = (Object[]) obj;
			map = new HashMap<String, String>();
			// 将对应的值存放在map中
			for (int i = 0; i < headerLength; i++) {
				map.put(header[i],
						result[i] == null ? " " : result[i].toString());
			}
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("红筹币统计", null, header, content, response,request);
	}
	
	
	/***
	 * 根据用户显示编辑框
	 * @param userId
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryUser")
	public String queryUser(String userId, HttpServletRequest request) {
		Userbasicsinfo user = userbasicsinfoService.queryUserById(userId);
		request.setAttribute("user", user);
		return "/WEB-INF/views/admin/quickquery/saveHccoindetail";
	}
	
	/***
	 * 编辑红筹币
	 * @param userId
	 * @param jjstate
	 * @param state
	 * @param number
	 * @param remark
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveHccoindetailQT")
	public JSONObject percardPasstou(String userId, Integer jjstate,Integer state,Long number,String remark,
			HttpServletRequest request) {
		JSONObject json = new JSONObject();
		Userbasicsinfo user = userbasicsinfoService.queryUserById(userId);
		hccoindetailService.saveHccoindetailQT(user, jjstate, state, number, remark);
		DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,"编辑成功", "main109", "closeCurrent");
		return json;
	}
	
	
	
}
