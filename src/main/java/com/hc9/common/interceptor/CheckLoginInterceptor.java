package com.hc9.common.interceptor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.dao.entity.Adminuser;

/**
 * 登录、注册验证拦截器
 * 
 * @author frank
 * 
 */
public class CheckLoginInterceptor extends HandlerInterceptorAdapter {
	
	private static final Logger logger = Logger.getLogger(CheckLoginInterceptor.class);

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 定义返回值变量
        boolean bool = true;
        
        Class<?> clazz = handler.getClass();
        CheckLogin checkLogin = clazz.getAnnotation(CheckLogin.class);
        if (checkLogin != null) {
            // 判断后台登录
            bool = adminLogin(request, response);
        }
        

        // 禁止缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "No-cache");
        response.setDateHeader("Expires", 0);
        return bool;
    }



    /**
     * 判断后台用户是否登录
     * 
     * @param request
     *            request
     * @param response
     *            response
     * @return 是否登录
     * @throws ServletException
     *             ServletException
     * @throws IOException
     *             IOException
     */
    private boolean adminLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Adminuser user = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
        if (user == null) {
        	logger.info("没有登录，跳转到登录页面！");
        	String reqUrl = request.getRequestURL().toString();
//        	request.getRequestDispatcher("/views/adminlogin.jsp").forward(request, response);
        	response.sendRedirect(reqUrl.substring(0, reqUrl.indexOf("/")) + "/views/adminlogin.jsp");
            return false;
        }
        return true;
    }
}
