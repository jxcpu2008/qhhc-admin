package com.hc9.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.hc9.service.CustomerShopService;

@Controller
@RequestMapping(value = { "/customerShop" })
@CheckLogin(value=CheckLogin.ADMIN)
public class CustomerShopController {
	
	@Resource
	private CustomerShopService  customerShopService;
	
	@RequestMapping(value = { "openShop" })
	public ModelAndView Shop() {
		return new ModelAndView("WEB-INF/views/admin/customer/customerShop");
	}
 	
 	@ResponseBody
	@RequestMapping("customerShopList")
	public JSONObject customerShopList(String limit, String start, HttpServletRequest request, PageModel page, Userbasicsinfo user) {
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
		List list =customerShopService.getCustomerShopList(page, user);
		JSONArray jsonlist = new JSONArray();
		String titles = "id,userName,name,phone,cardId,fee,isSucceed,tenderMoney,orderNum,tenderTime,uptTime,webOrApp,shopName";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}
	
	
}
