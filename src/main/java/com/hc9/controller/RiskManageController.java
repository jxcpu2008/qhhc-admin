package com.hc9.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.RepaymentRecordDetail;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.InadvanceRepayInfo;
import com.hc9.model.PageModel;
import com.hc9.model.RepaymentRequest;
import com.hc9.service.IRepayService;
import com.hc9.service.IRiskManageService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.LoanSignService;
import com.hc9.service.UserbasicsinfoService;

/**
 * 和风控管理有关的业务处理
 * @author Jerry Wong
 *
 */
@Controller
@RequestMapping("/riskManage")
@CheckLogin(value = CheckLogin.ADMIN)
public class RiskManageController {

	private static final Logger logger = Logger.getLogger(RiskManageController.class);
	
	@Autowired
	private IRiskManageService riskManageService;
	
	@Autowired
	private IRepayService repayService;
	
	@Autowired
	private UserbasicsinfoService userService;
	
	@Autowired
	private LoanSignService loanService;
	
	@Autowired
	private LoanSignQuery loanQueryService;

	@ResponseBody
	@RequestMapping("/getInadvanceRepayApplyList")
	public String getInadvanceRepayApplyList(String start, String limit, String jsonParam, PageModel page) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}
		
		logger.debug("查询参数为：" + jsonParam);
		@SuppressWarnings("unchecked")
		Map<String, Object> queryCondition = JsonUtil.jsonToObject(jsonParam, Map.class);
		
		List<InadvanceRepayInfo> inadvanceRepayApplyList = riskManageService.queryInadvanceRepayApplyListByPage(queryCondition, page);
		resultMap.put("rows", inadvanceRepayApplyList);
		resultMap.put("total", page.getTotalCount());
		return JsonUtil.toJsonStr(resultMap);
	}	
	
	@ResponseBody
	@RequestMapping("/approveInadvanceApply")
	public String approveInadvanceApply(String jsonParam) {
		RepaymentRequest repaymentRequest = JsonUtil.jsonToObject(jsonParam, RepaymentRequest.class);
		// 查询融资用户
		Userbasicsinfo loanUser = userService.queryUserById(repaymentRequest.getUserId());
		repaymentRequest.setLoanUser(loanUser);
		
		// 费用比例记录
		Costratio costratio = loanService.queryCostratio();
		repaymentRequest.setDefaultFeeConfig(costratio);
		
		// 查询标的
		Loansign loan = loanQueryService.getLoansignById(String.valueOf(repaymentRequest.getLoanId()));
		repaymentRequest.setLoan(loan);
		
		repaymentRequest.setRepayAction("inadvanceRepay");
		
		List<RepaymentRecordDetail> repaymentRecordDetails = repayService.getApplyInadvanceRepayDetailRecords(repaymentRequest.getRepayRecordId());
		
		JSONObject json = null;
		try {
			json = repayService.repay(repaymentRequest, repaymentRecordDetails);
		} catch (Exception e) {
			logger.error("还款过程发生异常，请检查！", e);
//			json.element("code", 105);
//			json.element("msg", "还款过程发生异常，请检查！");
		}
		return json.toString();
	}
	
	@ResponseBody
	@RequestMapping("/refuseInadvanceApply")
	public String refuseInadvanceApply(String jsonParam) {
		RepaymentRequest repaymentRequest = JsonUtil.jsonToObject(jsonParam, RepaymentRequest.class);
		
		List<Repaymentrecord> repaymentRecords = repayService.getApplyInadvanceRepayRecords(repaymentRequest.getLoanId());
		List<RepaymentRecordDetail> repaymentRecordDetails = repayService.getApplyInadvanceRepayDetailRecords(repaymentRequest.getRepayRecordId());
		JSONObject json = null;
		
		try {
			json = repayService.reverse(repaymentRequest, repaymentRecords, repaymentRecordDetails);
		} catch (Exception e) {
			logger.error("重置提前还款申请过程发生异常，请检查！", e);
			
			json.element("code", 105);
			json.element("msg", "系统异常！");
		}
		return json.toString();
	}
}