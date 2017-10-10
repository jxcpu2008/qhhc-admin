package com.hc9.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.model.PageModel;
import com.hc9.service.LogService;

/**
 * 后台用户登陆日志
 * 
 * @author ransheng
 * 
 */
@Controller
@RequestMapping(value = { "log" })
@SuppressWarnings(value = { "rawtypes" })
@CheckLogin(value=CheckLogin.ADMIN)
public class LogController {

    /**
     * 日志接口
     */
    @Resource
    private LogService logService;

    /**
     * 分页查询登录日志条数
     * 
     * @param page
     *            分页对象
     * @param request
     *            请求
     * @return 放回路径.jsp
     */
    @RequestMapping(value = { "loglist", "/" })
    public ModelAndView logPage(
            @ModelAttribute(value = "PageModel") PageModel page,
            HttpServletRequest request) {

        // 查询登录日志条数
        Object count = logService.getCount();
        page.setTotalCount(Integer.parseInt(count.toString()));
        // 分页查询登录日志
        List list = logService.logPage(page);
        request.setAttribute("page", page);
        request.setAttribute("list", list);
        return new ModelAndView("WEB-INF/views/admin/log/loglist");
    }

    /**
     * 删除后台用户登陆日志
     * 
     * @param ids
     *            日志编号
     * @param request
     *            请求
     * @return dwz json 对象
     */
    @RequestMapping(value = { "deletelist", "/" })
    @ResponseBody
    public JSONObject deleteLog(
            @RequestParam(value = "ids", defaultValue = "", required = true) String ids,
            HttpServletRequest request) {
        JSONObject json = new JSONObject();
        try {
            // 删除后台用户登录日志
            logService.deleteLog(ids);
            json.element("statusCode", "200");
            json.element("message", "删除成功");
            json.element("navTabId", "main25");
            return json;
        } catch (Throwable e) {
            json.element("message", "删除失败");
            json.element("statusCode", "300");
            e.getMessage();
            return json;
        }
    }
}
