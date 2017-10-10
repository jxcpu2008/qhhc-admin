package com.hc9.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.log.LOG;
import com.hc9.dao.entity.Deputysection;
import com.hc9.model.PageModel;
import com.hc9.service.ColumnManageService;
import com.hc9.service.DeputysectionService;

@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping(value = { "deputysection" })
public class DeputysectionController {

	/**
	 * 引用ColumnManageService
	 */
	@Resource
	ColumnManageService columnservice;

	/**
	 * 引用DeputysectionService
	 */
	@Resource
	DeputysectionService deputyService;

	/**
	 * topicsList 用来存放topic的集合的名称
	 */
	private String topicsList = "topicsList";

	/**
	 * page 分页
	 */
	private String page = "page";

	/**
	 * 查询所有二级栏目
	 * 
	 * @param pageModel
	 *            分页
	 * @param topicId
	 *            一级栏目id
	 * @param request
	 *            请求
	 * @return 返回数据和页面
	 */
	@RequestMapping(value = { "queryAllDeputysections", "" })
	public ModelAndView queryAllDeputysections(PageModel pageModel,
			String topicId, HttpServletRequest request) {
		request.setAttribute("deputysections_list", deputyService.queryDeput(
				pageModel,
				topicId == null || topicId.equals("") ? 0 : Long
						.parseLong(topicId)));
		// 查询并保存所有一级栏目
		request.setAttribute(topicsList, columnservice.queryAllTopics());
		request.setAttribute(page, pageModel);
		request.setAttribute("topicId", topicId);
		return new ModelAndView("WEB-INF/views/admin/column/deputysectionlist");
	}

	/**
	 * 跳转到新增/修改二级栏目页面
	 * 
	 * @param id
	 *            二级栏目id
	 * @param operation
	 *            当前所做的操作（add:添加; upt:修改）
	 * @param request
	 *            请求
	 * @return 返回数据和页面
	 */
	@RequestMapping("/add_upt_deputy")
	public ModelAndView queryDeputysectionById(long id, String operation,
			HttpServletRequest request) {
		request.setAttribute("topics", columnservice.queryAllTopics());
		request.setAttribute("operation", operation);
		request.setAttribute("id", id);
		if (operation.equals("upt")) {
			Deputysection deputy = deputyService.queryDeputyById(id);
			request.setAttribute("deputysection", deputy);
			request.setAttribute("topicId", deputy.getTopic().getId());
		}
		return new ModelAndView("WEB-INF/views/admin/column/add_upt_deputy");
	}

	/**
	 * 新增/修改二级栏目
	 * 
	 * @param id
	 *            二级栏目id
	 * @param operation
	 *            operation 当前所做的操作（add:添加; upt:修改）
	 * @param deputysection
	 *            二级栏目
	 * @param request
	 *            请求
	 * @return 返回数据
	 */
	@RequestMapping("/addOrUpdateDeputysection")
	@ResponseBody
	public JSONObject addOrUpdateDeputysection(long id, String operation,
			Deputysection deputysection, HttpServletRequest request) {
		deputysection.setOrderNum(Integer.parseInt(deputysection.getId()
				.toString()));
		JSONObject json = new JSONObject();
		try {
			if (deputysection.getIsShow() == null) {
				deputysection.setIsShow(0);
			}
			if (deputysection.getIsfooter() == null) {
				deputysection.setIsfooter(0);
			}
			if (deputysection.getIsRecommend() == null) {
				deputysection.setIsRecommend(0);
			}
			if (deputysection.getIsfixed() == null) {
				deputysection.setIsfixed(0);
			}
			if (operation.equals("upt")) {
				if (deputysection.getIsfixed() == 2) {
					return columnservice.setJson(json, "300", "该二级栏目不能被修改！",
							"main10", "closeCurrent");
				} else {
					String url = deputysection.getUrl();
					boolean b = url.startsWith("/to/");
					// 如果类似“to/loaninfo/openLoan.htm”开头，则进行下面的操作，否则直接保存
					if (b) {
						String param = url.substring(3, url.indexOf(".htm"));// 截取参数部分
						String[] params = param.split("-");// 用“-”将参数分割【param1：类型；param2：一级栏目id；param3：二级栏目id；param4：文章id】
						url = "/to/" + params[0] + "-"
								+ deputysection.getTopic().getId() + "-"
								+ params[2] + ".htm";
					}
					deputysection.setUrl(url);
					deputyService.updateDeputysection(deputysection);
				}
			} else if (operation.equals("add")) {
				deputyService.addDeputy(deputysection);
			}
			// 重置application
			columnservice.resetTopicDeputysectionArticle();
			return columnservice.setJson(json, "200", "更新成功", "main10",
					"closeCurrent");
		} catch (Exception e) {
			LOG.error(e);
			return columnservice.setJson(json, "300", "更新失败", "main10",
					"closeCurrent");
		}
	}

	/**
	 * 删除二级栏目
	 * 
	 * @param ids
	 *            要删除的二级栏目的id
	 * @param request
	 *            请求
	 * @return 返回数据
	 */
	@RequestMapping("/deleteDeputysection")
	@ResponseBody
	public JSONObject deleteDeputysection(String ids, HttpServletRequest request) {
		JSONObject json = new JSONObject();
		try {
			boolean isSuccess = columnservice.deleteMany(ids);
			if (isSuccess) {
				// 重置application
				columnservice.resetTopicDeputysectionArticle();
				return columnservice.setJson(json, "200", "更新成功", "main10", "");
			} else {
				return columnservice.setJson(json, "300",
						"您选择的栏目中含有不能删除的栏目，请重新选择要删除的栏目！", "main10", "");
			}
		} catch (Exception e) {
			LOG.error(e);
			return columnservice.setJson(json, "300", "更新失败", "main8", "");
		}
	}
}
