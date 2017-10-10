package com.hc9.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.dao.entity.Helper;
import com.hc9.model.PageModel;
import com.hc9.service.ColumnManageService;
import com.hc9.service.HelperService;

@Controller
@RequestMapping("/helper")
@CheckLogin(value=CheckLogin.ADMIN)
public class HelperController {

	@Resource
	private HelperService helperService;
	@Resource
	ColumnManageService columnservice;
	private String page = "page";

	@RequestMapping("/queryHelp.htm")
	public String queryHelp(HttpServletRequest request, Integer id) {
		List<Helper> list = helperService.queryHelp(id);
		request.setAttribute("helplist", list);
		return "WEB-INF/views/hc9/help_right";
	}

	/**
	 * 帮助中心
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/toHelper.htm")
	public String aaaa(HttpServletRequest request, String sign) {

		request.setAttribute("sign", sign);

		return "WEB-INF/views/hc9/helper";
	}

	/**
	 * 打开帮助中心管理页面
	 * 
	 * @param pagemodel
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = { "openHelper", "" })
	public ModelAndView openHelper(PageModel pagemodel,
			HttpServletRequest request) throws UnsupportedEncodingException {
		List list = helperService.queryHelperList(pagemodel);
		request.setAttribute("helpList", list);
		request.setAttribute(page, pagemodel);
		return new ModelAndView("WEB-INF/views/admin/help/help_admin");
	}

	/**
	 * 修改帮助中心
	 * 
	 * @param id
	 * @param operation
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryHelpById")
	public ModelAndView queryHelpById(long id, String operation,
			HttpServletRequest request) {
		Helper help = helperService.queryHelpDetil(id);
		List<Helper> helpList = helperService.queryHelpCloum();
		request.setAttribute("help", help);
		request.setAttribute("helpList", helpList);
		request.setAttribute("operation", operation);
		return new ModelAndView("WEB-INF/views/admin/help/add_upt_help");
	}

	@RequestMapping("/deleteHelper")
	@ResponseBody
	public JSONObject deleteHelper(String ids, HttpServletRequest request) {
		JSONObject json = new JSONObject();
		try {
			columnservice.deleteMany(Helper.class, ids);
			return columnservice.setJson(json, "200", "更新成功", "main80", "");
		} catch (Exception e) {
			return columnservice.setJson(json, "300", "更新失败", "main80", "");
		}
	}

	@RequestMapping("/addOrUpdateHelp")
	@ResponseBody
	public JSONObject addOrUpdateArticle(
			@ModelAttribute("Helper") Helper helper, String id,
			String operation, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			if (operation.equals("add")) {
				helperService.saveHelp(helper);
			} else if (operation.equals("upt")) {
				helperService.updateHelp(helper);
			}
			// 重置application
			columnservice.resetApplaction(request);
			return columnservice.setJson(json, "200", "更新成功", "main80",
					"closeCurrent");

		} catch (Exception e) {
			e.printStackTrace();
			return columnservice.setJson(json, "300", "更新失败", "main80",
					"closeCurrent");
		}
	}

}
