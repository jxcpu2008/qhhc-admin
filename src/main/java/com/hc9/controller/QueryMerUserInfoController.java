package com.hc9.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.model.MerUserInfo;
import com.hc9.model.PageModel;
import com.hc9.service.MerUserInfoService;
import com.hc9.service.WebService;

/**
 * 充值记录
 * 
 * @author yu
 * 
 */
@Controller
@RequestMapping("/merUserInfo")
@CheckLogin(value = CheckLogin.ADMIN)
@SuppressWarnings("rawtypes")
public class QueryMerUserInfoController {
	@Resource
	private MerUserInfoService merUserInfoService;
    /**
     * 查询充值记录
     * 
     * @param page
     *            分页
     * @param beginDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param userName
     *            用户名
     * @param request
     *            request
     * @return jsp
     */
    @RequestMapping("/open")
    public String open(@ModelAttribute("PageModel") PageModel page,
            String beginDate, String endDate, String userName,
            String status, HttpServletRequest request) {
        Integer count = merUserInfoService.queryCount(beginDate, endDate,userName);
        page.setTotalCount(count);
        List list = merUserInfoService.queryPage(page, beginDate, endDate,userName);
        request.setAttribute("list", list);
        request.setAttribute("page", page);
        return "/WEB-INF/views/admin/meruserinfo/mer_userinfo_list";
    }
    /**
     * 查询ips用户信息
     * @param pIdentNo
     * @param request
     * @return
     * @throws Exception 
     */
    @RequestMapping("/queryIpsUserInfo")
    public ModelAndView queryIpsUserInfo(String pIdentNo,String userName,
           HttpServletRequest request) throws Exception {
    	MerUserInfo merUserInfo=WebService.QueryMerUserInfo(pIdentNo);
    	request.setAttribute("merUserInfo", merUserInfo);
    	request.setAttribute("userName", userName);
        ModelAndView returnModelAndView = new ModelAndView(
                "WEB-INF/views/admin/meruserinfo/get_ips_userinfo");
        return returnModelAndView;
    }
}
