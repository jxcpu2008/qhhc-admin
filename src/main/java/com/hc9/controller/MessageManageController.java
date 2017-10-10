package com.hc9.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.util.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.dao.SmsEmailTemplateDao;
import com.hc9.dao.entity.SearchUser;
import com.hc9.dao.entity.SmsEmailPlanTime;
import com.hc9.dao.entity.SmsEmailTemplate;
import com.hc9.dao.entity.SmsemailSendPlan;
import com.hc9.dao.entity.SwitchControl;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.MessageManageService;

@Controller
@RequestMapping("/messageManager")
@CheckLogin(value=CheckLogin.ADMIN)
public class MessageManageController {

	@Resource
	private MessageManageService messageManageService;

	@Resource
	private SmsEmailTemplateDao smsEmailTemplateDao;
	
	@Resource
	private RechargeModel rechargeModel;

	/** 添加计划页面 type : 1、短信 2、邮件 oper : 1、复制或者编辑 */
	@RequestMapping("/addPlanPage")
	public String addPlanPage(HttpServletRequest request, String type) {
		List<SwitchControl> oneLevelTypeList = smsEmailTemplateDao.
				queryOneLevelSwitchList("sms_email_template_type");
		request.setAttribute("oneLevelTypeList", oneLevelTypeList);
		request.setAttribute("type", type);
		return "/WEB-INF/views/admin/message/addplan";
	}

	/** 跳转到邮件短信修改页面 */
	@RequestMapping("/toModifyPlanPage")
	public String toModifyPlanPage(HttpServletRequest request, Long planId, String msgType) {
		SmsemailSendPlan smsemailSendPlan = messageManageService.querySmsemailSendPlanByPlanId(planId);
		SmsEmailPlanTime smsEmailPlanTime = messageManageService.querySmsEmailPlanTimeByPlanId(planId);
		String receiveName = "";
		if("1".equals(msgType)) {
			receiveName = messageManageService.getSmsSendboxReceiveInfo(planId, request);
		} else if("2".equals(msgType)) {
			receiveName = messageManageService.getEmailSendboxReceiveInfo(planId, request);
		}
		List<SwitchControl> oneLevelTypeList = smsEmailTemplateDao.queryOneLevelSwitchList("sms_email_template_type");
		/** 二级类型下拉框  */
		List<SwitchControl> twoLevelTypeList = smsEmailTemplateDao.
				queryTemplateListByUpSwitchEnName(smsemailSendPlan.getTemplateType());
		request.setAttribute("oneLevelTypeList", oneLevelTypeList);
		request.setAttribute("twoLevelTypeList", twoLevelTypeList);
		request.setAttribute("smsemailSendPlan", smsemailSendPlan);
		request.setAttribute("smsEmailPlanTime", smsEmailPlanTime);
		// 获取发送计划信息
		request.setAttribute("receiveName", receiveName);
		request.setAttribute("plan", messageManageService.getAddPlanInfo(planId));
		return "/WEB-INF/views/admin/message/editplan";
	}
	
	/** 确认修改按钮
	 *  @param planType 保存的类型：1、保存为发送计划；2、保存为发送计划的草稿；
	 *  */
	@RequestMapping("/doModifyPlanPage.htm")
	@ResponseBody
	public String doModifyPlanPage(HttpServletRequest request, Long planId, SmsemailSendPlan sp,
			Long planTimeId, SmsEmailPlanTime st, Integer planType, String userChangeFlag) {
		Map<String, String> resultMap = messageManageService.
				updateSmsemailSendPlanRelInfo(request, planId, sp, planTimeId, st, planType, userChangeFlag);
		String jsonStr = JsonUtil.toJsonStr(resultMap);
		return jsonStr;
	}
	
	/** 全局设置页面 */
	@RequestMapping("/emailSetting")
	public String emailSetting(HttpServletRequest request) {
		request.setAttribute("scList", messageManageService.getSwitchList());
		return "/WEB-INF/views/admin/message/emailSetting";
	}

	/** type 1、短信列表页 2、邮件列表页 */
	@RequestMapping("/smsList")
	public String smsList(HttpServletRequest request, Integer no, String type) {
		PageModel page = new PageModel();
		if (no != null) {
			page.setPageNum(no);
		} else {
			page.setPageNum(1);
		}
		page.setList(messageManageService.getSmsSendPlanList(page, type));
		request.setAttribute("page", page);
		request.setAttribute("type", type);
		return "/WEB-INF/views/admin/message/smsList";
	}

	/** 草稿箱页面 */
	@RequestMapping("/draftBox")
	public String draftBox(HttpServletRequest request, Integer no) {
		PageModel page = new PageModel();
		if (no != null) {
			page.setPageNum(no);
		} else {
			page.setPageNum(1);
		}
		page.setList(messageManageService.getDraftBox(page));
		request.setAttribute("page", page);
		return "/WEB-INF/views/admin/message/draftBox";
	}

	@RequestMapping("/statusSend")
	@ResponseBody
	public String StatusSend(HttpServletRequest request, Long id,
			Integer sendStatus, String scroll) {
		try {
			SmsemailSendPlan plan = messageManageService.getSmsemailSendPlan(id);
			plan.setSendStatus(sendStatus);
			messageManageService.updateSmsemailSendPlan(plan);
			return scroll;
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	/** 开关设置操作 */
	@RequestMapping("/switchSend")
	@ResponseBody
	public String switchSend(HttpServletRequest request, Long id) {
		try {
			SwitchControl sc = messageManageService.getSwitchControl(id);
			if (sc.getSwitchStatus() == 0) {
				sc.setSwitchStatus(1); // 开启
			} else {
				sc.setSwitchStatus(0); // 关闭
			}
			messageManageService.updateSwitchStatus(sc);
			return sc.getSwitchStatus().toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	/** 查询收件人信息 */
	@RequestMapping("/getSearchUser.htm")
	@ResponseBody
	public Object[] getSearchUser(HttpServletRequest request, SearchUser searchUser) {
		Object[] obj = messageManageService.getSearchUser(request, searchUser);
		return obj;
	}

	/** 获取收件人数据 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/sureSearchUser.htm")
	@ResponseBody
	public List sureSearchUser(HttpServletRequest request) {
		String sql = (String) request.getSession().getAttribute("searchUserUrl");
		List list = null;
		if (sql != null) {
			request.getSession().setAttribute("sureUrl", sql);
			list = messageManageService.getSearchUserList(sql);
		}
		return list;
	}

	/** 导出收件人名单 */
	@ResponseBody
	@RequestMapping("/exportReceiverList.htm")
	public boolean exportReceiverList(HttpServletRequest request, HttpServletResponse response) {
		boolean result = false;
		String sql = (String) request.getSession().getAttribute("searchUserUrl");
		List list = new ArrayList();
		if (sql != null) {
			request.getSession().setAttribute("sureUrl", sql);
			list = messageManageService.getSearchUserList(sql);
		}
		String title = "信息接收人列表";
		String[] header = {"用户id", "姓名", "手机号", "邮箱"};
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				Map<String, String> userMap = new HashMap<String, String>();
				userMap.put("用户id", "" + StatisticsUtil.getLongFromBigInteger(arr[0]));
				userMap.put("姓名", StatisticsUtil.getStringFromObject(arr[2]));
				userMap.put("手机号", StatisticsUtil.getStringFromObject(arr[1]));
				userMap.put("邮箱", StatisticsUtil.getStringFromObject(arr[3]));
				content.add(userMap);
			}
		}
		result = rechargeModel.downloadExcel(title, null, header, content, response);
		return result;
	}
	
	/** 邮件模板下拉信息 */
	@RequestMapping("/queryTemplateList")
	@ResponseBody
	public List queryTemplateList(String upSwitchEnName, HttpServletResponse response) {
		List<SwitchControl> list = smsEmailTemplateDao.queryTemplateListByUpSwitchEnName(upSwitchEnName);
		return list;
	}

	/** 保存计划 
	 * @param planType 保存的类型：1、保存为发送计划；2、保存为发送计划的草稿；
	 * @return
	 */
	@RequestMapping("/savePlan.htm")
	@ResponseBody
	public String savePlan(HttpServletRequest request, SmsemailSendPlan sp,
			SmsEmailPlanTime st, Integer planType) {
		Map<String, String> resultMap = new HashMap<String, String>();
		String code = "-1";
		String msg = "后台保存出错，新增计划失败！";
		try {
			SmsEmailTemplate smsEmailTemplate = smsEmailTemplateDao.querySmsEmailTemplate(
					sp.getSendType(), sp.getTemplateType(), sp.getTemplateEnName());
			if(smsEmailTemplate != null) {
				// 保存计划
				if (planType == 1) {
					// 提交
					sp.setSendStatus(1);
				} else if (planType == 2) {
					// 草稿
					sp.setSendStatus(0);
				}
				
				String sendBeginTime = st.getSendBeginTime().substring(0, 13) + ":00:00";
				String sendEndTime = st.getSendEndTime().substring(0, 13) + ":59:59";
				String createTime = DateUtils.format("yyyy-MM-dd HH:mm:ss");
				sp.setCreateTime(createTime);
				sp.setPredictSendBeginTime(sendBeginTime);
				sp.setPredictSendEndTime(sendEndTime);
				sp.setMsgTitle(smsEmailTemplate.getTemplateTitle());
				sp.setTemplateContent(smsEmailTemplate.getTemplateContent());
				Serializable seria = messageManageService.saveplan(sp);
				// 计划时间
				st.setSendPlanId(Long.valueOf(seria.toString()));
				st.setCreateTime(createTime);
				st.setSendBeginTime(sendBeginTime);
				st.setSendEndTime(sendEndTime);
				messageManageService.saveplanTime(st);

				// 查询收件人
				String url = (String) request.getSession().getAttribute("sureUrl");
				if(sp.getSendType().intValue() == 1) {
					url = url + " and mobilephone is not null";
				} else {
					url += " and email is not null";
				}
				List list = messageManageService.getSearchUserList(url);
				messageManageService.saveSmsEmailBoxList(sp, list);
				SmsEmailCache.setSmsEmailSendPlanStatus(sp.getId(), sp.getSendStatus());
				code = "0";
				msg = "保存计划成功！";
			} else {
				msg = "当前所选择的模板尚未为添加！";
			}
		} catch(Exception e) {
			Log.error("后台异常，新增消息计划失败！", e);
		}
		resultMap.put("code", code);
		resultMap.put("msg", msg);
		String jsonStr = JsonUtil.toJsonStr(resultMap);
		return jsonStr;
	}
	
	/** 复制计划  */
	@RequestMapping("/copyMsgSendplan.htm")
	public String copyMsgSendplan(HttpServletRequest request, Long planId) {
		messageManageService.copyMsgSendplan(request, planId);
		return "/WEB-INF/views/admin/message/editplan";
	}
	
	/** 判断模板是否存在 */
	@RequestMapping("/isTemplateExist.htm")
	@ResponseBody
	public String isTemplateExist(String msgType, String templateType, String templateEnName) {
		Map<String, String> resultMap = new HashMap<String, String>();
		String code = "-1";
		String msg = "当前模板不存在，不能进行预览操作！";
		try {
			SmsEmailTemplate smsEmailTemplate = smsEmailTemplateDao.querySmsEmailTemplate(
					Integer.valueOf(msgType), templateType, templateEnName);
			if(smsEmailTemplate != null) {
				code = "0";
				msg = "模板存在，可以进行预览操作！";
			}
		} catch(Exception e) {
			Log.error("预览模板，后台系统出现异常！", e);
			msg = "后台系统异常，请联系管理员！";
		}
		resultMap.put("code", code);
		resultMap.put("msg", msg);
		String jsonStr = JsonUtil.toJsonStr(resultMap);
		return jsonStr;
	}
	
	/** 预览发送计划对应的短信邮件模板 */
	@RequestMapping("/viewSmsEmailTemplateOfPlan")
	public String viewSmsEmailTemplateOfPlan(String msgType, String templateType, String templateEnName, HttpServletRequest request) {
		SmsEmailTemplate smsEmailTemplate = smsEmailTemplateDao.querySmsEmailTemplate(
				Integer.valueOf(msgType), templateType, templateEnName);
		/** 一级大类型 */
		List<SwitchControl> oneLevelTypeList = smsEmailTemplateDao.queryOneLevelSwitchList("sms_email_template_type");
		/** 二级类型下拉框  */
		List<SwitchControl> twoLevelTypeList = smsEmailTemplateDao.queryTemplateListByUpSwitchEnName(smsEmailTemplate.getTemplateType());
		request.setAttribute("oneLevelTypeList", oneLevelTypeList);
		request.setAttribute("twoLevelTypeList", twoLevelTypeList);
		request.setAttribute("smsEmailTemplate", smsEmailTemplate);
		return "/WEB-INF/views/admin/message/template/smsEmailTemplateDetail";
	}
}