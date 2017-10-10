package com.hc9.controller;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.dao.entity.Footer;
import com.hc9.service.FooterService;

/**
 * 系统消息设置
 * 
 * @author ransheng
 * 
 */
@Controller
@RequestMapping(value = { "footer" })
@CheckLogin(value=CheckLogin.ADMIN)
public class FooterController {

    /**
     * 系统消息接口
     */
    @Resource
    private FooterService footerservice;

    /**
     * application
     */
    @Resource
    private ServletContext application;

    /**
     * 
     * @param request
     *            请求对象
     * @return 跳转路径.jsp
     */
    @RequestMapping(value = { "getfooter", "/" })
    public ModelAndView queryFooter(HttpServletRequest request) {
        Footer footer = footerservice.queryFooter();
        request.setAttribute("footer", footer);
        // 保存在application
        if (application.getAttribute("application_footer") == null) {
            application.setAttribute("application_footer", footer);
        }
        return new ModelAndView("WEB-INF/views/admin/footer/footerlist");
    }

    /**
     * 更新系统信息表
     * 
     * @param footer
     *            系统信息对象
     * @param request
     *            秦秋对象
     * @return dwz json对象
     */
    @RequestMapping(value = { "updatefooter", "/" })
    @ResponseBody
    public JSONObject saveORupdate(
            @ModelAttribute(value = "Footer") Footer footer,
            HttpServletRequest request) {
        JSONObject json = new JSONObject();
        try {
            // 更新系统表
            footerservice.saveORupdate(footer);
            // 删除application
            application.setAttribute("footer", footerservice.queryFooter());
            json.element("statusCode", "200");
            json.element("message", "更新成功");
            json.element("navTabId", "main28");
            return json;
        } catch (Throwable e) {
            e.getMessage();
            json.element("statusCode", "300");
            json.element("message", "更新失败");
            json.element("navTabId", "main28");
            return json;
        }

    }

}
