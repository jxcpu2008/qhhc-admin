package com.hc9.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 后台登录
 * 
 * @author ransheng
 * 
 */
@Controller
@RequestMapping("/")
public class AdminLoginController {

    /**
     * 登录页面
     * 
     * @author ransheng
     * @return 登录页面
     */
	@RequestMapping(value = { "/","/admin"})
    public ModelAndView login() {
        return new ModelAndView("views/adminlogin");
    }

}
