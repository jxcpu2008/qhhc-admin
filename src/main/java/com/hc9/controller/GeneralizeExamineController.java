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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Generalize;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.GeneralizeExamineService;

/***
 * 客户关联审核列表
 * @author lkl
 *
 */
@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping("/generalizeExamine")
public class GeneralizeExamineController {
	
	@Resource
	private GeneralizeExamineService generalizeExamineService; 
	
	@Resource
	private RechargeModel rechargeModel;
	
	private static Map<Integer,String> OPER;

	
	static{
		OPER=new HashMap<>();
		OPER.put(0, "待审核");
		OPER.put(1, "关联成功");
		OPER.put(2, "关联成功");
		OPER.put(3, "审核不通过");
	} 
	
	
	
	
	@RequestMapping(value = { "indexQuery", "/" })
	public ModelAndView indexQuery() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/generalizeExamineQuery");
		return returnModelAndView;
	}
	
	@ResponseBody
	@RequestMapping("generalizeExamineQuery")
	public JSONObject GeneralizeExamineQuery(String limit, String start,Generalize generalize, HttpServletRequest request, PageModel page) {

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
		List datalist = generalizeExamineService.generalizeExaminePage(page, generalize);
		String titles = "id,name,phone,uuname,uphone,adddate,state,realname,auditTime,remark";
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
	 * @param outLoanrecordExcel
	 * @param request
	 * @param response
	 */
	@RequestMapping("outGeneralizeExamineExcel")
	public void outPutGeneralizeExamineExcel(Generalize generalize, HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "申请人", "申请人手机号", "被关联客户", "客户手机号", "申请时间","状态","备注", "操作时间","操作人"};
		
		// 获取数据源
		List list = generalizeExamineService.queryGeneralizeExamineList(generalize);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("申请人", str[1] + "");
			map.put("申请人手机号", str[2] + "");
			map.put("被关联客户",str[3] + "");
			map.put("客户手机号", str[4] + "");
			map.put("申请时间", str[5] + "");
			map.put("状态", OPER.get(str[6]));
			map.put("备注", str[9] + "");
			map.put("操作时间", str[8] + "");
			map.put("操作人", str[7] + "");
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("客户关联审核列表", null, header, content, response,
				request);
	}
	

	/***
	 * 申请审核
	 * @param ids
	 * @param examine  1-审核成功  2-审核失败
	 * @param state   
	 * @param remark
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/auditGeneralizeExamine")
	public String auditGeneralizeExamine(String ids,Integer  examine,Integer state, String remark,HttpServletRequest request) {
		String num = generalizeExamineService.updateGeneralizeExamineState(ids, examine, state, remark, request);
		return num;
	}
	
	/***
	 * 审核不通过
	 * @param id
	 * @param examine
	 * @param state
	 * @param remark
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/auditGeneralizeNotExamine")
	public JSONObject percardPasstou(String id,Integer  examine,Integer state, String remark,HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String num = generalizeExamineService.updateGeneralizeExamineState(id, examine, state, remark, request);
		if(num.equals("1")){
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,"审核成功", "main122", "closeCurrent");
		}else{
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,"审核失败", "main122", "closeCurrent");
		}
		return json;
	}
	
	@RequestMapping("/queryGeneralizeExamine")
	public String queryLoanExamine(String id, HttpServletRequest request) {
		request.setAttribute("id", id);
		return "/WEB-INF/views/admin/quickquery/updateGeneralizeExamine";
	}

}
