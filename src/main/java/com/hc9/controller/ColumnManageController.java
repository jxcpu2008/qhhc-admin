package com.hc9.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.util.OSSUtil;
import com.hc9.dao.entity.Deputysection;
import com.hc9.service.ColumnManageService;
import com.hc9.service.DeputysectionService;

/**
 * 栏目管理
 * 
 * @author My_Ascii
 * 
 */
@Controller
@RequestMapping(value = { "admincolum" })
@CheckLogin(value=CheckLogin.ADMIN)
public class ColumnManageController {

    /**
     * 引用ColumnManageService
     */
    @Resource
    ColumnManageService columnservice;
    
    /**
     * 引用DeputysectionService
     */
    @Resource
    DeputysectionService deputyService;
    
    /**
     * topicsList 用来存放topic的集合的名称
     */
    private String topicsList = "topicsList";
    
    /**
     * page 分页
     */
    private String page = "page";
    
   /**
    * 当存放一级栏目的下拉列表值改变时查询该一级栏目下栏目类型为‘列表’的二级栏目
    * @param id 文章id
    * @param request 请求
    * @return 返回数据和页面
    */
    @RequestMapping("/topicSelectChange")
    @ResponseBody
    public List<Deputysection> topicSelectChange(String id, HttpServletRequest request,HttpServletResponse response) {
        List<Deputysection> list = columnservice.queryDeputyByTid(id.equals("") ? 0 : Long.parseLong(id));
        StringBuffer sb=new StringBuffer();
      try{  
    	  response.setCharacterEncoding("UTF-8");
    	  if (list.size() > 0) {
				sb.append("<option value='-1'>" + "请选择" + "</option>");
			} else {
				sb.append("<option value='-1'>" + "请选择" + "</option>");
			}
        for (int i = 0; i < list.size(); i++) {
        	Deputysection dept=list.get(i);
            sb.append( "<option  value='"+dept.getId()+"'>"+dept.getName()+"</option>");
	     }
	        response.setContentType("text/html");
			response.getWriter().print(sb.toString());
      }catch (IOException e) {
			e.printStackTrace();
		} 
        return null;
    }

    /**
     * 上传图片
     * @param request 请求
     * @return 返回数据
     * @throws IOException 文件相关操作抛出的异常
     */
    @RequestMapping("/uploadFile")
    @ResponseBody
    public Map<String, String> uploadFile(HttpServletRequest request) throws IOException {
        String folder = "default/columnimg";
        // 定义一个map来存放要返回的结果
        Map<String, String> imgMap = OSSUtil.uploadToOss(request, folder);
        imgMap.put("err", "");// 操作失败,返回失败消息
        imgMap.put("msg", imgMap.get("fileDir"));// 操作成功，返回文件路径
        return imgMap;
    }

}
