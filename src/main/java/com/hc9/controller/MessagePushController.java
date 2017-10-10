package com.hc9.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.AppMessagePush;
import com.hc9.model.PageModel;
import com.hc9.service.IMessagePushManageService;

/**
 * 消息推送管理（定向推送）
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/msgPush")
@CheckLogin(value=CheckLogin.ADMIN)
public class MessagePushController {
	
	private static final Logger logger = Logger.getLogger(MessagePushController.class);

    @Autowired
    private IMessagePushManageService msgPushService;
    
    @RequestMapping("/msgPushedList")
    public ModelAndView getMsgPushedList(AppMessagePush msgPushed, PageModel pageModel, HttpServletRequest request) {
    	
    	String createTimeFrom = request.getParameter("timeFrom");
    	logger.debug("创建时间开始日期：" + createTimeFrom);
    	String createTimeTo = request.getParameter("timeTo");
    	logger.debug("创建时间结束日期：" + createTimeTo);
    	
    	Map<Integer, String> map = new HashMap<Integer, String>();
    	map.put(0, "");
    	map.put(1, "全部注册用户");
    	map.put(2, "已认购用户");
    	map.put(3, "已宝付授权未认购");
    	map.put(4, "女性用户");
    	map.put(5, "男性用户");
    	map.put(6, "当日生日客户");
    	
    	try {
	        request.setAttribute("msgPushedList", msgPushService.queryPushedMessage(pageModel, msgPushed, createTimeFrom, createTimeTo));
	        request.setAttribute("page", pageModel);
	        request.setAttribute("message", msgPushed);
	        request.setAttribute("timeFrom", createTimeFrom);
	        request.setAttribute("timeTo", createTimeTo);
	        request.setAttribute("map", map);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
        return new ModelAndView("WEB-INF/views/admin/messagepush/msgPushedList");
    }
    
    @RequestMapping("/input")
    public ModelAndView inputPushMessage(HttpServletRequest request) {
    	
        return new ModelAndView("WEB-INF/views/admin/messagepush/msgPushInput");
    }
    
    @RequestMapping("/pushMsg")
    @ResponseBody
    public JSONObject pushMsg(AppMessagePush msgPush, HttpServletRequest request) {
    	
    	// 获取后台登录用户的真实姓名
    	Adminuser adminUser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
    	msgPush.setOperator(adminUser.getRealname());
    	msgPush.setDescription("消息定向推送：" + msgPush.getTitle());
    	
    	JSONObject json = new JSONObject();
    	
    	try {
    		msgPushService.pushMessage(msgPush);
    		
    		request.getServletContext().setAttribute("msgPushedList", msgPushService.queryMessageList());
    		return setJson(json, "200", "操作成功", "main135", "closeCurrent");
    	} catch (Exception e) {
    		e.printStackTrace();
    		
    		return setJson(json, "300", "操作失败", "main135", "closeCurrent");
    	}
    }
    
    @RequestMapping("/deleteMsg")
    @ResponseBody
    public JSONObject deleteMsg(String ids, HttpServletRequest request) {
    	
    	JSONObject json = new JSONObject();
    	try {
    		msgPushService.deletePushedMessage(AppMessagePush.class, ids);
    		return setJson(json, "200", "删除成功", "main12", "");
    	} catch (Exception e) {
    		e.printStackTrace();
    		return setJson(json, "300", "删除失败", "main12", "");
    	}
    }
    
	private JSONObject setJson(JSONObject json, String statusCode, String message, String navTabId, String callbackType) {
		json.element("statusCode", statusCode);
		json.element("message", message);
		json.element("navTabId", navTabId);
		if (!callbackType.equals("")) {
			json.element("callbackType", callbackType);
		}
		return json;
	}
}