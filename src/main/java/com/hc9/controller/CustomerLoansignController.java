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
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.CustomerLoansignService;

@Controller
@RequestMapping(value = { "/customerLoansign" })
@CheckLogin(value=CheckLogin.ADMIN)
public class CustomerLoansignController {

	@Resource
	private CustomerLoansignService customerLoansignService;

	/** 注入excel文件生成工具 */
	@Resource
	private RechargeModel modelRecharge;

	@RequestMapping(value = { "openLoansign" })
	public ModelAndView Loansign() {
		return new ModelAndView("WEB-INF/views/admin/customer/customerLoansign");
	}

	@ResponseBody
	@RequestMapping("customerLoansignList")
	public JSONObject customerLoansignList(String limit, String start,
			HttpServletRequest request, PageModel page, Userbasicsinfo user) {
		JSONObject resultjson = new JSONObject();
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
		// repaystate,repayTime,
		// 分页数据源
		List list = customerLoansignService.getCustomerLoansignList(page, user);
		List newlist = new ArrayList<Object[]>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[]) list.get(i);
				if (Integer.parseInt(obj[13].toString()) == 2) {

					obj[11] = obj[11] + "个月";
				} else if (Integer.parseInt(obj[13].toString()) == 3) {
					obj[11] = obj[11] + "天";
				}
				newlist.add(obj);
			}
		}

		JSONArray jsonlist = new JSONArray();
		String titles = "id,userid,loansignName,userName,name,tenderMoney,order_id,tenderTime,isSucceed,subType,status,remonth,creaitTime,type,createTime";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, newlist, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}

	// 导出投资记录
	/**
	 * @param request
	 * @param response
	 * @param ids
	 * @param userinfo
	 * @param isbrow
	 */
	@RequestMapping("/table-to-excel")
	public void dataToExcel(HttpServletRequest request,
			HttpServletResponse response, String ids, Userbasicsinfo user) {
		String headers = "序号,项目名称,投资者真实户名,投资者用户名,购买金额,订单号,购买时间,购买是否成功,投资类型,投资方式,用户注册时间";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;

		// 查询所有信息
		List<Object> list = customerLoansignService
				.outCustomerLoansignList(user);

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

		// 导出会员信息
		modelRecharge.downloadExcel("众持投资记录", null, header, content, response);

	}

	/**
	 * @param request
	 * @param loanId
	 * @return
	 */
	@RequestMapping("/seeLoanDetailes")
	public String seeLoanDetailes(HttpServletRequest request, String lrId) {
		List list = customerLoansignService.getSeeLoanDetailes(lrId);
		request.setAttribute("loanDetailes", list);
		return "/WEB-INF/views/admin/customer/customerSeeLoanDetailes";
	}

}
