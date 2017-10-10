package com.hc9.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hc9.common.redis.IndexDataCache;
import com.hc9.commons.normal.Validate;
import com.hc9.dao.entity.Article;
import com.hc9.dao.entity.Deputysection;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
import com.hc9.service.ColumnManageService;
import com.hc9.service.CommonProblemsService;

/**
 * 前台栏目管理
 * 
 * @author My_Ascii
 * 
 */
@Controller
@RequestMapping("to")
public class PageUrlController {

	/**
	 * 注入ColumnManageService
	 */
	@Resource
	ColumnManageService columnservice;
	/**
	 * 注入CommonProblemsService
	 */
	@Resource
	CommonProblemsService problemservice;
	/**
	 * 注入HibernateSupport
	 */
	@Resource
	HibernateSupport commondao;

	/**
	 * @param page
	 *            PageModel
	 * @param request
	 *            HttpServletRequest
	 * @return 返回页面
	 */
	@RequestMapping("*.htm")
	public String queryByUrl(PageModel page, HttpServletRequest request,
			Integer no) {
		return forward(request, page, null, no);
	}

	/**
	 * 媒体报道
	 * 
	 * @param request
	 * @return 新闻媒体页面
	 */
	@RequestMapping("toList-1-*")
	public String mediaList(HttpServletRequest request) {
		String[] params = request.getServletPath().split("-");
		if ("3".equals(params[2])) {
			request.setAttribute("tab", 3);
		} else {
			request.setAttribute("tab", 4);
		}
		return "WEB-INF/views/visitor/communal/list_page";
	}

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param url
	 *            栏目路径
	 * @return String
	 */
	@SuppressWarnings("all")
	public String forward(HttpServletRequest request, PageModel page,
			String url, Integer no) {
		String requestURI = "";
		if (Validate.emptyStringValidate(url)) {
			requestURI = "/" + url;// 获取请求的路径
		} else {
			requestURI = request.getServletPath();// 获取请求的路径
		}
		String param = requestURI.substring(4, requestURI.indexOf(".htm"));// 截取参数部分
		String[] params = param.split("-");// 用“-”将参数分割【param1：类似；param2：一级栏目id；param3：二级栏目id；param4：文章id】
		Deputysection deputy = columnservice.queryDeputyById(Long
				.parseLong(params[2]));// 根据id查询二级栏目
		request.setAttribute("topicId", params[1]);// 一级栏目id
		request.setAttribute("deputyId", params[2]);// 二级栏目id
		request.setAttribute("deputy", deputy);
		List<Deputysection> listDeputys = columnservice.queryDecou(Long
				.valueOf(params[1]));
		if (params[0].equals("single")) {
			request.setAttribute("deputys", listDeputys);
			request.setAttribute("type", "single");
		}

		if (params[0].equals("list")) {
			if (no != null) {
				page.setPageNum(no);
			}
			page.setList(columnservice.queryAllArticle(params[2], page));
			
			//左侧菜单缓存
			String key="LIST:HC9:NEWS:NAVICATER:DEPUTY";
//			List<Deputysection> deputysections=IndexDataCache.getList(key);
			List<Deputysection> deputysections = IndexDataCache.getList(key, Deputysection.class);
			if(null==deputysections){
				deputysections=columnservice.getDeputysectionList();
			}
			
			// 列表
			request.setAttribute("appDeputys", deputysections);
			request.setAttribute("deputyName", deputy!=null?deputy.getName():"");
			request.setAttribute("type", "list");
			request.setAttribute("type", "list");
			request.setAttribute("page", page);
			return "WEB-INF/views/hc9/common/list_page";
		}
		if (params[0].equals("article")) {
			// 文章
			Article article = columnservice.queryArticleById(Long
					.parseLong(params[3]));
			request.setAttribute("article", article);
			request.setAttribute("type", "article");
			request.setAttribute("deputys", listDeputys);
		}
		return "WEB-INF/views/hc9/common/single_page";

	}
}
