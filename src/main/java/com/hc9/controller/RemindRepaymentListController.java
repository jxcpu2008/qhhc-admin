package com.hc9.controller;

import java.io.UnsupportedEncodingException;
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
import com.hc9.common.constant.Constant;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.Arith;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.DateUtil;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.form.RemindRepaymentListForm;
import com.hc9.model.PageModel;
import com.hc9.service.EmailService;
import com.hc9.service.RepaymentrecordService;
import com.hc9.service.SmsService;

@Controller
@RequestMapping("/remindRepaymentList")
@CheckLogin(value=CheckLogin.ADMIN)
public class RemindRepaymentListController {

	/** repaymentrecordService 还款记录 */
	@Resource
	private RepaymentrecordService repaymentrecordService;

	@Resource
	private SmsService smsService;

	@Resource
	private EmailService emailService;

	/**
	 * <p>
	 * Title: index
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return 后台普通标展示页面
	 */
	@RequestMapping(value = { "open", "/" })
	public ModelAndView index(HttpServletRequest request) {
		request.setAttribute("preRepayDateEnd", DateUtil.getSpecifiedDateAfter(
				DateUtil.format("yyyy-MM-dd"), -3));
		ModelAndView returnModelAndView = new ModelAndView(
				"WEB-INF/views/admin/remindrepayment/remind_repayment_list");
		return returnModelAndView;
	}

	/**
	 * 获取日期
	 * 
	 * @param timerMor
	 * @return
	 */
	@RequestMapping(value = { "getpreRepayDateEnd", "/" })
	@ResponseBody
	public JSONObject getpreRepayDateEnd(String timerMor) {
		JSONObject resultjson = new JSONObject();
		resultjson.element("preRepayDateEnd", DateUtil.getSpecifiedDateAfter(
				DateUtil.format("yyyy-MM-dd"), Integer.parseInt(timerMor)));
		return resultjson;
	}

	@ResponseBody
	@RequestMapping(value = { "queryRemindRepayment", "/" })
	public JSONObject queryRemindRepayment(String limit, String start,
			RemindRepaymentListForm remindRepayForm,
			HttpServletRequest request, PageModel page)
			throws UnsupportedEncodingException {
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
		List list = repaymentrecordService.remindRepaymentPage(page,
				remindRepayForm);
		JSONArray jsonlist = new JSONArray();
		String titles = "repayId,loanNumber,loanTitle,loancategory,loanproducttype,name,issueLoan,month,rate,refundWay,publishTime,iscredit,"
				+ "creditTime,periods,preRepayDate,preRepayMoney,isRepay,repayTime,repayState,realRepayMoney,remindEmailCount,remindSMSCount";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}

	/**
	 * 发送短信或邮件
	 * 
	 * @param fashion
	 *            发送方式0 表示发送短信 1表示发送邮件
	 * @param content
	 *            发送内容
	 * @return 发送是否成功
	 */
	@RequestMapping("sendSms.htm")
	@ResponseBody
	public JSONObject sendChatMessage(int fashion, String content,
			String phone, String email, Long repayId) {
		JSONObject json = new JSONObject();
		// 发送短信
		try {
			String state = "";
			String info = "";
			if (fashion == Constant.STATUES_ZERO) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("content", content);
				content = smsService.getSmsResources("check-remind.flt", map);
				int trigger=Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
				int result=smsService.chooseSmsChannel(trigger, content, phone);
				if (result==0) {
					state = "200";
					info = "发送短信成功";
				} else {
					state = "300";
					info = "发送短信失败";
				}
			} else {
				emailService.sendEmail("前海红筹还款到期提醒", content, email);
				state = "200";
				info = "邮件发送成功";
			}
			// 更新发送记录
			if (state.equals("200")) {
				repaymentrecordService.updateSendTimes(fashion, repayId);
			}
			return DwzResponseUtil.setJson(json, state, info, null, "forward");
		} catch (Exception e) {
			e.printStackTrace();
			return DwzResponseUtil
					.setJson(json, "300", "发送失败", null, "forward");
		}
	}

	@RequestMapping("openSendRemind")
	public String openSendRemind(Long repayId, HttpServletRequest request) {
		request.setAttribute("contactInfo",
				repaymentrecordService.getContact(repayId));
		request.setAttribute("repayId", repayId);
		return "WEB-INF/views/admin/remindrepayment/add_remind";
	}

	/**
	 * 导出催收列表
	 * 
	 * @param remindRepayForm
	 * @param request
	 * @param response
	 */
	@RequestMapping("exportRepayList")
	public void exportRepayList(RemindRepaymentListForm remindRepayForm,
			HttpServletRequest request, HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "借款标号", "标题", "借款标类型", "借款标产品类型",
				"借款人", "借款金额", "还款期限", "年化利率", "还款方式", "发布时间", "是否放款", "放款时间",
				"期数", "预还款日期", "预还款金额", "是否还款", "实际还款时间", "实际还款金额", "催收邮件发送次数",
				"催收短信发送次数" };
		List list = repaymentrecordService.remindRepaymentPage(null,
				remindRepayForm);
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("借款标号", str[1]==null?"":str[1].toString());
			map.put("标题", str[2]==null?"":str[2].toString());
			map.put("借款标类型", str[3]==null?"":str[3].toString());
			map.put("借款标产品类型", str[4]==null?"":str[4].toString());
			map.put("借款人", str[5].toString());
			map.put("借款金额", Arith.round(new BigDecimal(str[6].toString()), 2)
					+ "元");
			map.put("还款期限", str[7].toString() + "个月");
			map.put("年化利率", Arith.round(new BigDecimal(str[8].toString()), 2)
					+ "%");
			map.put("还款方式", str[9]==null?"":str[9].toString());
			map.put("发布时间", str[10]==null?"":str[10].toString());
			map.put("是否放款", str[11]==null?"":str[11].toString());
			map.put("放款时间", str[12] != null ? str[12].toString() : "");
			map.put("期数", str[13]==null?"":str[13].toString());
			map.put("预还款日期", str[14]==null?"":str[14].toString());
			map.put("预还款金额", str[15]==null?"":str[15].toString());
			map.put("是否还款", str[16]==null?"":str[16].toString());
			map.put("实际还款时间", str[17] != null ? str[17].toString() : "");
			map.put("实际还款金额", str[19] != null ? str[19].toString() : "");
			map.put("催收邮件发送次数", str[20]==null?"":str[20].toString());
			map.put("催收短信发送次数", str[21]==null?"":str[21].toString());
			content.add(map);
		}
		repaymentrecordService.downloadExcel("催收列表", null, header, content,
				response);
	}
}
