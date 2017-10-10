package com.hc9.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.json.JsonUtil;
import com.hc9.service.activity.year2016.Month05ActivityService;

/**所有活动的微信发放红包类*/
@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping(value = { "/allActivity"})
public class AllActivityController {
	@Resource
	private Month05ActivityService month05ActivityService;
	
	@RequestMapping("/redPayment")
	public ModelAndView noviceTaskStatisticsDetail() {
		ModelAndView noticeTaskview = new ModelAndView("WEB-INF/views/admin/quickquery/allActivity");
		return noticeTaskview;
	}
	
	
	/** 2016年5月新手任务相关关注微信根据手机号标注领取 */
	@RequestMapping("/newWebChatAttentionReceive.htm")
	@ResponseBody
	public String webChatAttentionReceive(String phone) {
		Map<String, String> resultMap = month05ActivityService.webChatAttentionReceive(phone);
		return JsonUtil.toJsonStr(resultMap);
	}
}
