package com.hc9.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.log.LOG;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.service.DispenseRedEnvelopeService;
import com.hc9.service.UserbasicsinfoService;

/**
 * 红包补发处理
 *
 */
@Controller
@RequestMapping("/dispenseRedEnvelope")
@CheckLogin(value=CheckLogin.ADMIN)
public class DispenseRedEnvelopeController {
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;

	
	@Resource
	private DispenseRedEnvelopeService envelopeService;
	
	
	
	
	@RequestMapping("/open")
	public String addPlanPage(HttpServletRequest request) {
		return "/WEB-INF/views/admin/fund/dispenseRedEnvelope";
	}

	/***
	 * 根据手机查询用户
	 * @param request
	 * @param phone
	 */
	@ResponseBody
	@RequestMapping("/queryUserByPhone")
	public String pMerBillNoName(HttpServletRequest request, String phone){
		Map<String, String> resultMap = new HashMap<String, String>();
		String code = "-1";
		String userName = "";
		String name = "";
		String uid = "";
		String msg="此用户不存在，请重新输入！";
		List<Userbasicsinfo> users=userbasicsinfoService.queryUserbasicsinfoByPhone(phone);
		
		if(users!=null && users.size()>0){
			code="1";
			Userbasicsinfo user=users.get(0);
			name=user.getName();
			userName=user.getUserName();
			uid=user.getId().toString();
			msg="查询成功！";
		}
		resultMap.put("code", code);
		resultMap.put("name", name);
		resultMap.put("userName", userName);
		resultMap.put("uid", uid);
		resultMap.put("msg", msg);
		String jsonStr = JsonUtil.toJsonStr(resultMap);
		return jsonStr;
	}
	
	@ResponseBody
	@RequestMapping("/dispense")
	public String dispenseRedEnvelopeToUser(HttpServletRequest request,String uid, String phone,String money){
		Adminuser admin = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		String msg="";
		String jsonStr="";
		Map<String, String> resultMap = new HashMap<String, String>();
		if(uid==null || uid.equals("")){
			msg="请输入手机号，点击查询";
			resultMap.put("msg", msg);
			jsonStr = JsonUtil.toJsonStr(resultMap);
			return jsonStr;
		}
		long userId=Long.valueOf(uid);
		if(phone==null || phone.equals("")){
			msg="请输入手机号";
			resultMap.put("msg", msg);
			jsonStr = JsonUtil.toJsonStr(resultMap);
			return jsonStr;
		}
		if(money==null || money.equals("")){
			msg="请选择金额";
			resultMap.put("msg", msg);
			jsonStr = JsonUtil.toJsonStr(resultMap);
			return jsonStr;
		}
		double bonusMoney=0;
		if(money.equals("1")){
			bonusMoney=5;
		}else if(money.equals("2")){
			bonusMoney=10;
		}else{
			msg="金额不正确";
			resultMap.put("msg", msg);
			jsonStr = JsonUtil.toJsonStr(resultMap);
			return jsonStr;
		}
		List<Userbasicsinfo> users=userbasicsinfoService.queryUserbasicsinfoByPhone(phone);
		Userbasicsinfo user;
		if(users!=null && users.size()>0){
			user=users.get(0);
			if(user.getId()!=userId){
				msg="当前的手机号与用户名不一致，请从新输入手机号，然后点击查询";
				resultMap.put("msg", msg);
				jsonStr = JsonUtil.toJsonStr(resultMap);
				return jsonStr;
			}
		}else{
			msg="查询不到该用户！请重新输入手机号后点击查询";
			resultMap.put("msg", msg);
			jsonStr = JsonUtil.toJsonStr(resultMap);
			return jsonStr;
		}
		try{
			envelopeService.dispenseRedEnvelopeProgress(user, phone, admin.getRealname(), bonusMoney, 0, 3, 1);;
			msg="发放成功！";
		}catch (Exception e){
			msg="数据保存失败";
			LOG.error("红包发放失败",e);
		}
		resultMap.put("msg", msg);
		jsonStr = JsonUtil.toJsonStr(resultMap);
		return jsonStr;
	} 
	


}
