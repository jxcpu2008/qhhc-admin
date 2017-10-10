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
import com.hc9.service.CustomerWithdrawService;

/***
 * 客服提现流水查询
 * 
 * @author LKL
 *
 */
@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping(value = { "/customerWithdraw" })
public class CustomerWithdrawController {

	@Resource
	private CustomerWithdrawService customerWithdrawService;

	@Resource
	private RechargeModel modelRecharge;

	@RequestMapping(value = { "openWithdraw" })
	public ModelAndView Withdraw() {
		return new ModelAndView("WEB-INF/views/admin/customer/customerWithdraw");
	}

	@ResponseBody
	@RequestMapping("customerWithdrawList")
	public JSONObject customerWithdrawList(String limit, String start,
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
		// 分页数据源
		List list = customerWithdrawService.getWithdrawList(page, user);
		JSONArray jsonlist = new JSONArray();
		String titles = "id,userName,name,phone,cardId,amount,withdrawAmount,state,applytime,time,fee,mer_fee,fee_taken_on,strNum";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}

	@RequestMapping(value = { "openFinaWithdraw" })
	public ModelAndView openWithdraw() {
		return new ModelAndView(
				"WEB-INF/views/admin/financial/finalWithdrawList");
	}

	@ResponseBody
	@RequestMapping("finalWithdrawList")
	public JSONObject finalWithdrawList(String limit, String start,
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
		// 分页数据源
		List list = customerWithdrawService.getWithdrawList(page, user);
		JSONArray jsonlist = new JSONArray();
		String titles = "id,userName,name,phone,cardId,amount,withdrawAmount,state,applytime,time,fee,mer_fee,fee_taken_on,strNum";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}

	/**
	 * 导出充值记录
	 * 
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping("/withdraw-table-to-excel")
	public void dataToExceloansign(HttpServletRequest request,HttpServletResponse response, String userName,String name,String staffNo,String userType,String createTime,String failTime) {

		String headers = "提现ID,真实用户名,登录用户名,手机号码,身份证号,提现金额,实际到账金额,宝付收取手续费,商户收取手续费,费用承担方,提现时间,宝付提现成功时间,充值流水号,状态";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;

		// 查询所有信息
		List<Object> datalist = customerWithdrawService.getTableWithdrawList(userName, name, staffNo, userType, createTime, failTime);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : datalist) {

			Object[] result = (Object[]) obj;
			map = new HashMap<String, String>();

			// 将对应的值存放在map中
			for (int i = 0; i < headerLength; i++) {
				map.put(header[i],
						result[i] == null ? " " : result[i].toString());
			}

			content.add(map);
		}

		// 导出提现信息
		modelRecharge.downloadExcel("用户提现记录", null, header, content, response);
	}

}
