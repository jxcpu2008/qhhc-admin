package com.hc9.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.dao.entity.Deputysection;
import com.hc9.model.PageModel;
import com.hc9.service.ColumnManageService;
import com.hc9.service.DeputysectionService;
import com.hc9.service.SingleService;

@Controller
@RequestMapping("single")
@CheckLogin(value=CheckLogin.ADMIN)
public class SingleController {

    /*** 引用ColumnManageService*/
    @Resource
    ColumnManageService columnservice;
    
    /*** 引用DeputysectionService*/
    @Resource
    DeputysectionService deputyService;
    
    /*** 引用SingleService*/
    @Resource
    SingleService singleService;
    
    /*** topicsList 用来存放topic的集合的名称*/
    private String topicsList = "topicsList";
    
    /*** page 分页*/
    private String page = "page";
    
    /**
     * 查询所有栏目类型为‘单页’的二级栏目
     * 
     * @param pageModel 分页
     * @param topicId 一级栏目id
     * @param request 请求
     * @return 返回数据和页面
     */
    @RequestMapping(value = { "queryAllDepuy1", "" })
    public ModelAndView queryAllDepuy1(PageModel pageModel, String topicId,
            HttpServletRequest request) {
        // 查询并保存所有一级栏目
        request.setAttribute(topicsList, columnservice.queryAllTopics());
        // 查询并保存指定一级栏目下面的所有栏目类型为‘单页’的二级栏目
        request.setAttribute(
                "deputy1_list",
                singleService.queryAllDeputy1(pageModel, topicId == null
                        || topicId.equals("") ? 0 : Long.parseLong(topicId)));
        // request.setAttribute("topicId",topicId);
        request.setAttribute(page, pageModel);
        request.setAttribute("topicId", topicId);
        return new ModelAndView("WEB-INF/views/admin/column/deputylist1");
    }
    
    /**
     * 打开编辑单页页面
     * @param id 二级栏目id
     * @param operation 操作
     * @param request HttpServletRequest
     * @return ModelAndView
     */
     @RequestMapping("/openEditeDanye")
     public ModelAndView openEditeDanye(long id, String operation, HttpServletRequest request) {
         request.setAttribute("topics", columnservice.queryAllTopics());
         request.setAttribute("operation", operation);
         request.setAttribute("id", id);
         if (operation.equals("upt")) {
             Deputysection deputy = deputyService.queryDeputyById(id);
             request.setAttribute("deputy1", deputy);
             request.setAttribute("topicId", deputy.getTopic().getId());
         }
         return new ModelAndView("WEB-INF/views/admin/column/add_upt_singlepage");
     }

     /**
      * 删除单页
      * @param ids 要删除的二级栏目的id
      * @param request 请求
      * @return 返回数据
      */
      @RequestMapping("/deleteSinglePage")
      @ResponseBody
      public JSONObject deleteSinglePage(String ids, HttpServletRequest request) {
          JSONObject json = new JSONObject();
          try {
              boolean isSuccess = columnservice.deleteMany(ids);
              if (isSuccess) {
                //重置application
                  columnservice.resetTopicDeputysectionArticle();
                  return columnservice.setJson(json, "200", "更新成功", "main11", "");
              } else {
                  return columnservice.setJson(json, "300", "您选择的栏目中含有不能删除的栏目，请重新选择要删除的栏目！",
                          "main8", "");
              }
          } catch (Exception e) {
              return columnservice.setJson(json, "300", "更新失败", "main11", "");
          }
      }
      
      /**
       * 新增/修改单页
       * @param deputysection 二级栏目
       * @param operation 操作
       * @param request HttpServletRequest
       * @return JSONObject
       */
       @RequestMapping("/add_upt_single")
       @ResponseBody
       public JSONObject updatedeputy1(Deputysection deputysection, String operation,
               HttpServletRequest request) {
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
                   singleService.updateDeputysection(deputysection);
               } else if (operation.equals("add")) {
                   singleService.addDeputy(deputysection);
               }
             //重置application
               columnservice.resetTopicDeputysectionArticle();
               return columnservice.setJson(json, "200", "更新成功", "main11", "closeCurrent");
           } catch (Exception e) {
               e.printStackTrace();
               return columnservice.setJson(json, "300", "更新失败", "main11", "closeCurrent");
           }
       }

}
