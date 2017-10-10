package com.hc9.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.dao.entity.Link;
import com.hc9.model.PageModel;
import com.hc9.service.LinkService;

/**
 * 友情链接
 * 
 * @author ransheng
 */

@Controller
@SuppressWarnings(value = { "rawtypes" })
@RequestMapping(value = { "link" })
@CheckLogin(value = CheckLogin.ADMIN)
public class LinkController {

    /**
     * 友情链接接口
     */
    @Resource
    private LinkService linkservice;

    /**
     * application
     */
    @Resource
    private ServletContext application;

    /**
     * 分页查询友情链接信息
     * 
     * @param name
     *            链接名模糊查询
     * @param page
     *            分页对象
     * @param request
     *            请求
     * @return 返回路径.jsp
     */
    @RequestMapping(value = { "linkpage", "/" })
    public ModelAndView linkPage(
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @ModelAttribute(value = "PageModel") PageModel page,
            HttpServletRequest request) {
        // 获取友情链接条数
        Object count = linkservice.getCount(name);
        page.setTotalCount(Integer.parseInt(count.toString()));
        // 查询友情链接信息
        List list = linkservice.linkPage(page, name);
        request.setAttribute("page", page);
        request.setAttribute("list", list);
        if (application.getAttribute("application_link") == null) {
            application.setAttribute("application_link", linkservice.query());
        }
        return new ModelAndView("WEB-INF/views/admin/link/linklist");
    }

    /**
     * 打开新增（编辑）友情链接信息页面
     * 
     * @param id
     *            友情链接信息id
     * @param request
     *            请求路径
     * @return 返回路径.jsp
     */
    @RequestMapping(value = { "openlist", "/" })
    public ModelAndView openlink(
            @RequestParam(value = "id", defaultValue = "", required = false) String id,
            HttpServletRequest request) {
        // 如果编号存在则属于编辑信息
        if (!"".equals(id.trim())) {
            Link link = linkservice.queryOnly(id);
            request.setAttribute("link", link);
        }
        return new ModelAndView("WEB-INF/views/admin/link/updatelink");
    }

    /**
     * 新增（ 编辑）友情链接信息
     * 
     * @param link
     *            友情链接对象
     * @param request
     *            请求
     * @return dwz json 对象
     */
    
    @RequestMapping(value = { "updatelink", "/" })
    public void saveORupdatelink(@ModelAttribute(value = "Banner") Link link,
            HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        JSONObject json = new JSONObject();
        try {
            out = response.getWriter();
            // 新增（编辑）banner图片信息
            boolean b = linkservice.saveORupdatelinke(link, request);
            if (b) {
                linkservice.resetAppLink(application);
                json.element("statusCode", "200");
                json.element("message", "更新成功");
                json.element("navTabId", "main25");
                json.element("callbackType", "closeCurrent");
            } else {
                json.element("statusCode", "300");
                json.element("message", "请上传JPG、PNG、GIF图片类型");
            }
            out.print(json);
        } catch (Throwable e) {
            json.element("message", "更新失败");
            json.element("statusCode", "300");
            json.element("callbackType", "closeCurrent");
            e.getMessage();
            out.print(json);
        }

    }
    
    /**
     * 删除友情链接信息
     * 
     * @param ids
     *            编号
     * @param request
     *            请求
     * @return dwz json对象
     */
    @RequestMapping(value = { "deletelist", "/" })
    @ResponseBody
    public JSONObject deleteLink(
            @RequestParam(value = "ids", defaultValue = "", required = true) String ids,
            HttpServletRequest request) {
        JSONObject json = new JSONObject();
        try {
            // 新增（编辑）友情链接信息
            linkservice.deleteLink(ids);
            json.element("statusCode", "200");
            json.element("message", "删除成功");
            json.element("navTabId", "main25");
            application.removeAttribute("application_link");
            return json;
        } catch (Throwable e) {
            json.element("message", "删除失败");
            json.element("statusCode", "300");
            e.getMessage();
            return json;
        }
    }
}
