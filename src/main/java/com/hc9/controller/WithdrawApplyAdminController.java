package com.hc9.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.WithdrawApply;
import com.hc9.model.PageModel;
import com.hc9.service.WithdrawApplyAdminService;

/**
 * 申请提现记录
 * 
 * @author Administrator 2014-09-05
 * 
 */
@Controller
@RequestMapping("/withdraw_apply")
@CheckLogin(value = CheckLogin.ADMIN)
@SuppressWarnings("rawtypes")
public class WithdrawApplyAdminController {

	/**
	 * 申请提现记录接口
	 */
	@Resource
	private WithdrawApplyAdminService withdrawApplyAdminService;

	/**
	 * 操作后要刷新的页面
	 */
	private String borrowpageId = "main41";

	/**
	 * 查询提现申请记录
	 * 
	 * @param page
	 *            分页对象
	 * @param beginDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param request
	 *            request
	 * @return 申请提现记录jsp
	 */
	@RequestMapping("/open")
	public String queryPage(@ModelAttribute("PageModel") PageModel page,
			String beginDate, String endDate, HttpServletRequest request) {
		Integer count = withdrawApplyAdminService
				.queryCount(beginDate, endDate);
		page.setTotalCount(count);
		List list = withdrawApplyAdminService.queryPage(page, beginDate,
				endDate);
		request.setAttribute("list", list);
		request.setAttribute("page", page);
		return "/WEB-INF/views/admin/withdraw/withdrawApplyList";
	}

	/**
	 * 申请提现列表
	 * 
	 * @param beginDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param request
	 *            请求
	 * @param page
	 *            分页对象
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryWithdrawApply")
	public JSONObject queryWithdrawApply(String start, String limit,
			String beginDate, String endDate, HttpServletRequest request,
			PageModel page) {

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
		List list = withdrawApplyAdminService.queryPage(page, beginDate,
				endDate);

		JSONArray jsonlist = new JSONArray();
		String titles = "id,cash,user_name,real_name,apply_num,result,status,apply_time,answer_time,userbasic_id,fee,adminUser";

		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}

	/***
	 * 
	 * @param ids
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryApply")
	public String queryLoanExamine(String ids, HttpServletRequest request) {
		WithdrawApply withdrawApply = withdrawApplyAdminService
				.getWithdrawApplyById(ids.substring(0, ids.length() - 1));
		request.setAttribute("withdrawApply", withdrawApply);
		return "/WEB-INF/views/admin/withdraw/uptWithdrawApply";
	}

	/**
	 * <p>
	 * Title: passBorrow
	 * </p>
	 * <p>
	 * Description: 审核通过和不通过
	 * </p>
	 * 
	 * @param ids
	 *            审核通过
	 * @param state
	 *            状态
	 * @return 结果
	 */
	@ResponseBody
	@RequestMapping("/pass")
	public JSONObject passBorrow(HttpServletRequest request, int state,
			String ids) {
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(
				Constant.ADMINLOGIN_SUCCESS);
		JSONObject json = new JSONObject();

		if (StringUtil.isNotBlank(ids)) {
			if (withdrawApplyAdminService.ispassture(ids)) {
				return DwzResponseUtil.setJson(json,
						Constant.HTTP_STATUSCODE_ERROR, "只有未审核的提现申请才能审核",
						borrowpageId, null);
			}
			boolean bool = withdrawApplyAdminService.updateResult(ids, state,
					loginuser.getId().toString());
			if (!bool) {
				return DwzResponseUtil.setJson(json,
						Constant.HTTP_STATUSCODE_ERROR, "审核失败", borrowpageId,
						null);
			}
		}

		return DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
				"提现申请审核成功", borrowpageId, null);
	}

	/***
	 * 审核通过
	 * 
	 * @param ids
	 * @param request
	 * @param state
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/auditFee")
	public JSONObject auditFee(String id, Double fee, HttpServletRequest request) {
		JSONObject json = new JSONObject();
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(
				Constant.ADMINLOGIN_SUCCESS);
		withdrawApplyAdminService.updateResult(id, 1, fee, loginuser.getId()
				.toString());
		DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS, "审核成功",
				borrowpageId, "closeCurrent");
		return json;
	}
}
