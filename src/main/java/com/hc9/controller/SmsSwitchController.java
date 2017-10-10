package com.hc9.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.dao.entity.Smsswitch;
import com.hc9.service.SmsSwitchService;

@Controller
@RequestMapping("/smsSwitch")
@CheckLogin(value = CheckLogin.ADMIN)
public class SmsSwitchController {
	
	@Resource
	private SmsSwitchService service;
	
    @RequestMapping("open.htm")
    public String openSmsSwitch(HttpServletRequest request){
    	Smsswitch smsswitch=service.getSwitch();
        request.setAttribute("smsswitch",smsswitch);
        return "WEB-INF/views/admin/customer/smsswitch";
    }
    
    /**
     * 添加或修改
     * @return 返回处理信息
     */ 
    @RequestMapping("update.htm")
    @ResponseBody
    public JSONObject updateSmsSwitch(Smsswitch smsswitch){
    	JSONObject json = new JSONObject();
    	service.saveSwitch(smsswitch);
    	SmsEmailCache.setSmsTriggerSwitch(smsswitch.getTriger());
    	SmsEmailCache.setSmsMarketingSwitch(smsswitch.getMarketing());
    	return DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS, "保存成功","#main56",null);
    }
}
