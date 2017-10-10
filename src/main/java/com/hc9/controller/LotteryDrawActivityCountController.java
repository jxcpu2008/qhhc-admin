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
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.LotteryDrawActivityCountService;

/***
 * 抽奖排位活动
 * @author lkl
 */
@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping("/lotteryDrawActivity")
public class LotteryDrawActivityCountController {
	
	@Resource
	private LotteryDrawActivityCountService lotteryDrawActivityCount;
	
	@Resource
	private RechargeModel rechargeModel;
	
	
	/**使用状态*/
	private static Map<Integer,String> PRIZETYPE;
	
	
	static{
		PRIZETYPE=new HashMap<>();
		PRIZETYPE.put(4, "红包/加息");
		PRIZETYPE.put(11, "现金");
		PRIZETYPE.put(12, "一鸣惊人");
		PRIZETYPE.put(13, "一锤定音");
	} 
	
	@RequestMapping(value = { "index", "/" })
	public ModelAndView index() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/lotteryDrawActivity");
		return returnModelAndView;
	}

	@ResponseBody
	@RequestMapping("lotteryDrawActivityQuery")
	public JSONObject lotteryDrawActivityQuery(String limit, String start,ActivityMonkey activityMonkey, HttpServletRequest request,PageModel page) {
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
		List datalist =lotteryDrawActivityCount.lotteryDrawActivityPage(page,activityMonkey);
		String titles = "userName,realName,phone,type,rewardMoney,loanName,createTime";
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
	@RequestMapping("outLotteryDrawActivityExcel")
	public void outLotteryDrawActivityExcel(ActivityMonkey  activityMonkey, HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "真实姓名","手机号码", "奖励类型","奖品/奖金","项目名称", "获奖时间"};
		// 获取数据源
		List list =lotteryDrawActivityCount.querylotteryDrawActivity(activityMonkey);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("真实姓名", str[0] + "");
			map.put("手机号码", str[1] + "");
			map.put("奖励类型", returnMap(str[2]+"",Double.valueOf(str[3]+"")));
			map.put("奖品/奖金", str[3] == null && str[3] == "" ? "" : Arith.round(new BigDecimal(str[3].toString()), 3)+"");
			map.put("项目名称", str[4]+"");
			map.put("获奖时间", str[5]+"");
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("抽奖排位活动", null, header, content, response,request);
	}
	
	public String returnMap(String strValue,Double money){
		String returnValue="加息";
		if(strValue.equals("4")){
			if(money<1){
				returnValue="加息";
			}else{
				returnValue="红包";
			}
		}else if(strValue.equals("11")){	
			returnValue="现金";
		}else if(strValue.equals("12")){
			returnValue= "一鸣惊人";
		}else if(strValue.equals("13")){
			returnValue= "一锤定音";
		}
		return returnValue;
	}

}
