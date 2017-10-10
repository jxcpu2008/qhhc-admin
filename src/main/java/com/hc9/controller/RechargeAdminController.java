package com.hc9.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.RechargeAdminService;

/**
 * 充值记录
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/recharge")
@CheckLogin(value = CheckLogin.ADMIN)
@SuppressWarnings("rawtypes")
public class RechargeAdminController {

    /**
     * 接口
     */
    @Resource
    private RechargeAdminService rechargeAdminService;
    /**
     * excel导出接口
     */
    @Resource
    private RechargeModel rechargeModel;

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
        Integer count = rechargeAdminService.queryCount(beginDate, endDate,userName);
        page.setTotalCount(count);
        List list = rechargeAdminService.queryPage(page, beginDate, endDate,userName);
        request.setAttribute("list", list);
        request.setAttribute("page", page);
        return "/WEB-INF/views/admin/recharge/rechargelist";
    }
    
    /**
     * 导出充值记录的excel文件
     * @param ids 编号
     * @param request
     * @param response
     */
    @RequestMapping("/export_excel")
    public void exportExcel(String ids,HttpServletRequest request,
    		HttpServletResponse response){
    	//标题
    	String[] header=new String[]{"真实姓名","用户名","充值金额","到账金额","订单号","充值时间"};
    	//数据源
    	List list=rechargeAdminService.queryById(ids);
    	List<Map<String,String>> content=new ArrayList<Map<String,String>>();
    	Map<String,String> map=null;
    	for(Object obj:list){
    		Object[] str=(Object[])obj;
    		map=new HashMap<String,String>();
    		map.put("真实姓名", str[0] + "");
    		map.put("用户名", str[1] + "");
    		map.put("充值金额", str[2] + "");
    		map.put("到账金额", str[3] + "");
    		map.put("订单号", str[4] + "");
    		map.put("充值时间", str[5] + "");
    		content.add(map);
    	}
    	//导出充值记录
    	rechargeModel.downloadExcel("充值记录", null, header, content, response,request);
    }
}
