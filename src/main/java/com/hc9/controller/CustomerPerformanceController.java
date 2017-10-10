package com.hc9.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.redis.IndexDataCache;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.model.Marketing;
import com.hc9.model.RechargeModel;
import com.hc9.service.ColumnManageService;
import com.hc9.service.PerformanceService;

@Controller
@RequestMapping("customerPerformance")
@CheckLogin(value=CheckLogin.ADMIN)
public class CustomerPerformanceController {
	
	@Resource
	private PerformanceService performanceService;
	
	@Resource
	private RechargeModel modelRecharge;
	
	@Resource
	ColumnManageService columnservice;
	
	@RequestMapping("/open")
	public String open(HttpServletRequest request) {
		return "/WEB-INF/views/admin/customer/customerPerformance";
	}
	
	@ResponseBody
    @RequestMapping("/addPerformance")
    public JSONObject addPerformance(HttpServletRequest request,HttpServletResponse response,String beginTenderTime,String endTenderTime){
    	JSONObject json = new JSONObject();
    	List<Marketing> list=performanceService.getDataFromExcel(request,beginTenderTime,endTenderTime);
    	if(list!=null&&list.size()>0){
    		    String key = "INT:HC9:CUSTOMERPERFORMANCE:NUM:"+ beginTenderTime+endTenderTime;
    		    if(beginTenderTime.equals("")&&endTenderTime.equals("")){
    		    	List<Marketing> markList=IndexDataCache.getList(key);
    				if(markList!=null&&markList.size()>0){
    		    	      RedisHelper.del(key);
    				}
    		    }
    		    if(!RedisHelper.isKeyExist(key)){
	    		    	IndexDataCache.set(key, list);
    		    }
	    		DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS, "查询成功，请下载！", "main115", "closeCurrent");
				return json;
    	}else{
	   		 DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR, "查询失败，请看温馨提示！", "main115", "closeCurrent");
	   		 return json;
    	}
	}
	
	
	@ResponseBody
    @RequestMapping("/returnMsg")
	public JSONObject returnMsg(){
		 JSONObject json = new JSONObject();
		 DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR, "查询失败，请看温馨提示！", "main115", "closeCurrent");
		 return json;
	}
    
    @RequestMapping("/dataToPerforman")
	public void dataToPerforman(HttpServletRequest request,HttpServletResponse response,String beginTenderTime,String endTenderTime) {
		String headers = "姓名,手机号码,注册手机号认购业绩,其推荐人认购业绩,注册手机号年化业绩,其推荐人年化业绩,其推荐注册人数,其推荐认购人数,注册手机号认购单数,其推荐人认购单数";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		String key="INT:HC9:CUSTOMERPERFORMANCE:NUM:";
		if(beginTenderTime!=null&&endTenderTime!=null){
			key=key + beginTenderTime+endTenderTime;
		}
		List<Marketing> list=IndexDataCache.getList(key);
		if(list!=null&&list.size()>0){
			for (Marketing marketing : list) {
				map = new HashMap<String, String>();
				// 将对应的值存放在map中
				map.put("姓名",marketing.getName());
				map.put("手机号码", marketing.getPhone());
				map.put("注册手机号认购业绩",String.valueOf(marketing.getLoanRecrodPerformance()));
				map.put("其推荐人认购业绩", String.valueOf(marketing.getGenergerPerformance()));
				map.put("注册手机号年化业绩",String.valueOf(marketing.getPhonePerformance()));
				map.put("其推荐人年化业绩", String.valueOf(marketing.getUidPerformance()));
				map.put("其推荐注册人数",String.valueOf(marketing.getGenergerCountUid()));
				map.put("其推荐认购人数", String.valueOf(marketing.getGenergerNumber()));
				map.put("注册手机号认购单数",String.valueOf(marketing.getLoanRecordNumber()));
				map.put("其推荐人认购单数", String.valueOf(marketing.getGenergerLoanRecordNumber()));
				content.add(map);
			}
		}
		// 导出众持列表
		modelRecharge.downloadExcel("销售客户业绩", null, header, content, response);
		RedisHelper.del(key);
	}
}
