package com.hc9.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.dao.entity.CommonProblems;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
import com.hc9.service.CommonProblemsService;

@Controller
@RequestMapping("commonproblem")
@CheckLogin(value=CheckLogin.ADMIN)
public class CommonProblemsController {

    /**
     * 注入HibernateSupport
     */
    @Resource
    HibernateSupport commondao;
    
    /*** 注入CommonProblemsService*/
    @Resource
    CommonProblemsService cpService;
    /**
     * 打开常见问题管理页面
     * 
     * @param page
     *            PageModel
     * @param request
     *            HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping("/open")
    public ModelAndView open(PageModel page, HttpServletRequest request,
            String title, String isShow, String type) {
        request.setAttribute("problems",
                cpService.queryCommonProblems(page, title, isShow, type));
        request.setAttribute("title", title);
        request.setAttribute("isShow", isShow == "" ? "-1" : isShow);
        request.setAttribute("type", type == "" ? "-1" : type);
        request.setAttribute("page", page);
        return new ModelAndView("WEB-INF/views/admin/column/commonProblems");
    }
    
    /**
     * 打开新增/修改常见问题页面
     * @param id long
     * @param operation String
     * @param request  HttpServletRequest
     * @return ModelAndView
     */
     @RequestMapping("/add_upt_problem")
     public ModelAndView forwardAddUptPage(long id, String operation,
             HttpServletRequest request) {
         return cpService.forwardAddUptPage(id, operation, request);
     }
     
     /**
      * 新增/修改常见问题
      * @param id long
      * @param operation String
      * @param problem CommonProblems
      * @param request HttpServletRequest
      * @return JSONObject
      */
     @RequestMapping("/addOrUpdateProblem")
     @ResponseBody
     public JSONObject addOrUpdateProblem(long id, String operation,
             CommonProblems problem, HttpServletRequest request) {
         return cpService.addOrUpdateProblem(id, operation, problem, request);
     }
     
     /**
      * 删除二级栏目
      * @param ids 要删除的二级栏目的id
      * @param request 请求
      * @return 返回数据
      */
      @RequestMapping("/deleteProblem")
      @ResponseBody
      public JSONObject deleteProblem(String ids, HttpServletRequest request) {
          return cpService.deleteProblem(ids, request);
      }
      
      /**
       * 根据id查询常见问题
       * @param id 常见问题id
       * @param request HttpServletRequest
       * @return String
       */
      @RequestMapping("/showDetails")
      public String showDetails(long id, HttpServletRequest request){
          request.setAttribute("problem", commondao.get(CommonProblems.class, id));
          return "WEB-INF/views/visitor/problemDetails";
      }
}
