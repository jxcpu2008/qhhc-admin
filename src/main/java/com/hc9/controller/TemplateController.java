package com.hc9.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;
import com.hc9.dao.SmsEmailTemplateDao;
import com.hc9.dao.entity.SmsEmailTemplate;
import com.hc9.dao.entity.SwitchControl;
import com.hc9.model.PageModel;

/** 邮件模板管理相关控制器类 */
@Controller
@RequestMapping("/templateManager")
@CheckLogin(value=CheckLogin.ADMIN)
public class TemplateController {
	@Resource
	private SmsEmailTemplateDao smsEmailTemplateDao;
	
	/** 邮件短信模板管理相关页面 */
	@RequestMapping("/toSmsEmailTemplateList")
	public String augustActivity(HttpServletRequest request) {
		return "/WEB-INF/views/admin/message/template/smsEmailTemplateList";
	}

	/** 分页查询邮件短信模板列表相关数据 */
	@ResponseBody
	@RequestMapping("/querySmsEmailTemplateList")
	public String querySmsEmailTemplateList(String start, String limit, String msgType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		PageModel page = StatisticsUtil.comsitePageModel(start, limit);
		if(StringUtil.isBlank(msgType)) {
			msgType = "0";
		}
		List<SmsEmailTemplate> smsEmailTemplateList = smsEmailTemplateDao.querySmsEmailTemplateList(page, msgType);
		resultMap.put("rows", smsEmailTemplateList);// 短信邮件模板列表
		resultMap.put("total", page.getTotalCount());// 总注册量
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}

	/** 跳转到新增短信邮件模板页面 */
	@RequestMapping("/toAddSmsEmailTemplate")
	public String addSmsEmailTemplate(HttpServletRequest request) {
		List<SwitchControl> oneLevelTypeList = smsEmailTemplateDao.queryOneLevelSwitchList("sms_email_template_type");
		request.setAttribute("oneLevelTypeList", oneLevelTypeList);
		return "/WEB-INF/views/admin/message/template/addSmsEmailTemplate";
	}

	/** 新增模板根据一级大类型查询改类型下面的相关模板列表信息 */
	@RequestMapping("queryTemplateListOfUpSwitchEnName")
	public String queryTemplateListByUpSwitchEnName(String upSwitchEnName, HttpServletResponse response) {
		List<SwitchControl> list = smsEmailTemplateDao.queryTemplateListByUpSwitchEnName(upSwitchEnName);
		StringBuffer sb = new StringBuffer();
		sb.append("<option value='-1'>" + "请选择" + "</option>");
		try {
			response.setCharacterEncoding("UTF-8");
			for (int i = 0; i < list.size(); i++) {
				SwitchControl vo = (SwitchControl) list.get(i);
				sb.append("<option  value='" + vo.getSwitchEnName() + "'>"
						+ vo.getSwitchZhName() + "</option>");
			}
			response.setContentType("text/html");
			response.getWriter().print(sb.toString());
		} catch (IOException e) {
			LOG.error("queryTemplateListByUpSwitchEnName出错！", e);
		}
		return null;
	}

	/** 查询邮箱模板名称 */
	@RequestMapping("/queryTemplateList")
	@ResponseBody
	public List queryTemplateList(String upSwitchEnName, HttpServletResponse response) {
		List<SwitchControl> list = smsEmailTemplateDao.queryTemplateListByUpSwitchEnName(upSwitchEnName);
		return list;
	}

	/** 保持邮件模板 */
	@ResponseBody
	@RequestMapping("/addSmsEmailTemplate")
	public JSONObject addSmsEmailTemplate(HttpServletRequest request, SmsEmailTemplate smsEmailTemplate) {
		JSONObject json = new JSONObject();
		try{
			if(smsEmailTemplateDao.isExistByTypeAndTemplateEnName(smsEmailTemplate)) {
				json.element("message", "保存失败，该记录已存在");
			} else {
				smsEmailTemplateDao.saveSmsEmailTemplate(smsEmailTemplate);
				json.element("statusCode", "200");
				json.element("message", "更新成功");
				json.element("navTabId", "main108");
				json.element("callbackType", "closeCurrent");
			}
		} catch(Exception e) {
			json.element("message", "后台异常，保存失败");
		}
		return json;
	}

	/** 跳转到预览邮件短信模板页面 */
	@RequestMapping("/toSmsEmailTemplateDetail")
	public String toSmsEmailTemplateDetail(String id, HttpServletRequest request) {
		SmsEmailTemplate smsEmailTemplate = smsEmailTemplateDao.findSmsEmailTemplateById(id);
		/** 一级大类型 */
		List<SwitchControl> oneLevelTypeList = smsEmailTemplateDao.queryOneLevelSwitchList("sms_email_template_type");
		/** 二级类型下拉框  */
		List<SwitchControl> twoLevelTypeList = smsEmailTemplateDao.queryTemplateListByUpSwitchEnName(smsEmailTemplate.getTemplateType());
		request.setAttribute("oneLevelTypeList", oneLevelTypeList);
		request.setAttribute("twoLevelTypeList", twoLevelTypeList);
		request.setAttribute("smsEmailTemplate", smsEmailTemplate);
		return "/WEB-INF/views/admin/message/template/smsEmailTemplateDetail";
	}
	
	/** 调整到修改邮件短信模板页面 */
	@RequestMapping("/toSmsEmailUpdateTemplate")
	public String toSmsEmailUpdateTemplate(String id, HttpServletRequest request) {
		SmsEmailTemplate smsEmailTemplate = smsEmailTemplateDao.findSmsEmailTemplateById(id);
		/** 一级大类型 */
		List<SwitchControl> oneLevelTypeList = smsEmailTemplateDao.queryOneLevelSwitchList("sms_email_template_type");
		/** 二级类型下拉框  */
		List<SwitchControl> twoLevelTypeList = smsEmailTemplateDao.queryTemplateListByUpSwitchEnName(smsEmailTemplate.getTemplateType());
		request.setAttribute("oneLevelTypeList", oneLevelTypeList);
		request.setAttribute("twoLevelTypeList", twoLevelTypeList);
		request.setAttribute("smsEmailTemplate", smsEmailTemplate);
		return "/WEB-INF/views/admin/message/template/updateSmsEmailTemplate";
	}

	/** 修改短信邮件模板信息 */
	@ResponseBody
	@RequestMapping("/doUpdateSmsEmailUpdateTemplate")
	public JSONObject doUpdateSmsEmailUpdateTemplate(SmsEmailTemplate smsEmailTemplate) {
		JSONObject json = new JSONObject();
		try{
			if(smsEmailTemplateDao.isExistByTypeAndTemplateEnNameForUpdate(smsEmailTemplate)) {
				json.element("message", "修改失败，该记录已存在");
			} else {
				smsEmailTemplateDao.doUpdateSmsEmailUpdateTemplate(smsEmailTemplate);
				json.element("statusCode", "200");
				json.element("message", "更新成功");
				json.element("navTabId", "main108");
				json.element("callbackType", "closeCurrent");
			}
		} catch(Exception e) {
			json.element("message", "后台异常，修改失败");
		}
		return json;
	}

	/** 删除邮件短信模板页面 */
	@ResponseBody
	@RequestMapping("/deleteSmsEmailTemplate")
	public String deleteSmsEmailTemplate(String id) {
		Map<String, Object> resultMap = smsEmailTemplateDao.deleteSmsEmailTemplate(id);
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
}
