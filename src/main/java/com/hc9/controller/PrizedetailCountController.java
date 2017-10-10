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
import com.hc9.common.util.Arith;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.PrizedetailCountService;

/***
 * 实物奖品
 * @author lkl
 *
 */
@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping("/prizedetailCount")
public class PrizedetailCountController {
	
	@Resource
	private PrizedetailCountService prizedetailCount;
	
	@Resource
	private RechargeModel rechargeModel;
	
	
	/**使用状态*/
	private static Map<Integer,String> PRIZETYPE;
	
	
	static{
		PRIZETYPE=new HashMap<>();
		PRIZETYPE.put(8, "10元红包");
		PRIZETYPE.put(13, "50元京东购物卡");
		PRIZETYPE.put(14, "飞利浦电动牙刷");
		PRIZETYPE.put(15, "富士相机");
		PRIZETYPE.put(16, "小米套装");
	} 
	
	@RequestMapping(value = { "index", "/" })
	public ModelAndView index() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/prizedetailCount");
		return returnModelAndView;
	}

	@ResponseBody
	@RequestMapping("prizedetailCountQuery")
	public JSONObject prizedetailCountQuery(String limit, String start,Userbasicsinfo user, HttpServletRequest request,PageModel page) {
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
		List datalist =prizedetailCount.PrizedetailCountPage(page,user);
		String titles = "name,phone,tenderMoney,prizeType,receiveTime";
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
	 * @param request
	 * @param response
	 */
	@RequestMapping("outPrizedetailCountExcel")
	public void outPrizedetailCountExcel(Userbasicsinfo  user, HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "真实姓名","手机号码", "投资累计金额","奖品/奖金", "获奖时间"};
		// 获取数据源
		List list =prizedetailCount.queryPrizedetail(user);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("真实姓名", str[0] + "");
			map.put("手机号码", str[1] + "");
			map.put("投资累计金额", str[2] == null && str[2] == "" ? "" : Arith.round(new BigDecimal(str[2].toString()), 2) + "元" );
			map.put("奖品/奖金", PRIZETYPE.get(str[3]));
			map.put("获奖时间", str[4]+"");
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("攀爬活动统计", null, header, content, response,request);
	}
}
