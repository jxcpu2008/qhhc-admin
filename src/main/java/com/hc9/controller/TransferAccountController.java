package com.hc9.controller;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.json.JsonUtil;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.UserbasicsinfoService;


/**
 * 转账
 * @author lkl
 *
 */
@Controller
@RequestMapping("/transferAccount")
@CheckLogin(value=CheckLogin.ADMIN)
public class TransferAccountController {
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@RequestMapping("/openTransferPage")
	public String addPlanPage(HttpServletRequest request) {
		return "/WEB-INF/views/admin/fund/transferAccount";
	}
	
	/***
	 * 进行转账
	 * @param request
	 * @param pMerBillNo
	 * @param status
	 * @param money
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ipsTranCertInfo")
	public String ipsTranCertInfo(HttpServletRequest request, String pMerBillNo,Integer status,Double money){
		return baoFuLoansignService.ipsTranCertInfo(request, pMerBillNo, status, money);
	}
	
	
	/***
	 * 根据宝付账号进行查询
	 * @param request
	 * @param pMerBillNo
	 */
	@ResponseBody
	@RequestMapping("/pMerBillNoName")
	public String pMerBillNoName(HttpServletRequest request, String pMerBillNo){
		Map<String, String> resultMap = new HashMap<String, String>();
		String code = "-1";
		String msg = "此用户不存在，请重新输入！";
		Userbasicsinfo user=userbasicsinfoService.getUser(pMerBillNo);
		if(user!=null){
			code="1";
			msg=user.getName();
		}
		resultMap.put("code", code);
		resultMap.put("msg", msg);
		String jsonStr = JsonUtil.toJsonStr(resultMap);
		return jsonStr;
	}
	
	

}
