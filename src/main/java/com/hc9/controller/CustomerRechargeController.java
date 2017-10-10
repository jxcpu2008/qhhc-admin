package com.hc9.controller;

import java.io.UnsupportedEncodingException;
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
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.model.RechargeQueryVo;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.BaoFuService;
import com.hc9.service.CustomerRechargeService;
import com.hc9.service.PayLogService;
import com.hc9.service.PlankService;
import com.hc9.service.ProcessingService;
import com.hc9.service.RechargesService;
import com.hc9.service.UserbasicsinfoService;

/***
 * 客服充值记录查询
 * 
 * @author LKL
 *
 */
@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping(value = { "/customerRecharge" })
public class CustomerRechargeController {

	@Resource
	private CustomerRechargeService customerRechargeService;

	@Resource
	private RechargesService rechargesService;

	@Resource
	private UserbasicsinfoService userbasicsinfoService;

	@Resource
	private PayLogService payLog;

	@Resource
	private ProcessingService processingservice;

	@Resource
	private BaoFuService baoFuService;

	@Resource
	private PayLogService payLogService;

	@Resource
	private PlankService plankService;

	@Resource
	private RechargeModel modelRecharge;
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;


	@RequestMapping(value = { "openRecharge" })
	public ModelAndView Recharge() {
		return new ModelAndView("WEB-INF/views/admin/customer/customerRecharge");
	}

	@ResponseBody
	@RequestMapping("customerRechargeList")
	public JSONObject customerRechargeList(String limit, String start,
			HttpServletRequest request, PageModel page, RechargeQueryVo vo) {
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
		List list = customerRechargeService.getRechargeList(page, vo);
		JSONArray jsonlist = new JSONArray();
		String titles = "id,uid,userName,name,phone,cardId,rechargeAmount,reAccount,status,time,succ_time,fee,merfee,feetakenon,orderNum,fromSrc";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}

	@RequestMapping(value = { "openFinacialRecharge" })
	public ModelAndView openFinacialRecharge() {
		return new ModelAndView("WEB-INF/views/admin/financial/finacialRecharge");
	}



	/***
	 * 充值查询
	 * 
	 * @param request
	 * @param rId
	 * @return
	 */
	@RequestMapping("ipsCustomerRechargeNum.htm")
	@ResponseBody
	public String ipsCustomerRechargeNum(HttpServletRequest request, String rId)
			throws Exception {
		return baoFuLoansignService.ipsCustomerRechargeNum(request, rId);
	}

	/**
	 * 导出充值记录
	 * 
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping("/rechange-table-to-excel")
	public void dataToExceloansign(HttpServletResponse response, String paramJsonStr) {
		Map<String, String> paramsMap = JsonUtil.jsonToObject(paramJsonStr, Map.class);
		String headers = "充值ID,真实用户名,登录用户名,手机号码,身份证号,充值金额,实际到账金额,充值时间,宝付收取手续费,商户收取手续费,费用承担方,宝付充值成功时间,充值流水号,状态,渠道";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;
		String name="";
		try {
			name=new String(paramsMap.get("name").getBytes("iso-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 查询所有信息
		@SuppressWarnings("unchecked")
		List<Object> datalist = customerRechargeService.getTableRechargeList(paramsMap.get("userName"), name, paramsMap.get("rechargeStatus"), paramsMap.get("startTime"), paramsMap.get("endTime"));

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

		// 导出充值信息
		modelRecharge.downloadExcel("用户充值记录", null, header, content, response);
	}

}
