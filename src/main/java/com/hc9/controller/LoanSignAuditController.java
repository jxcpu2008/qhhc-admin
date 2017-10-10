package com.hc9.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.util.Arith;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Loansign;
import com.hc9.model.PageModel;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.BaoFuService;
import com.hc9.service.CacheManagerService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.LoanSignService;
import com.hc9.service.PayLogService;
import com.hc9.service.PlankService;
import com.hc9.service.UserbasicsinfoService;

/***
 * 融资成功审批、财务审核的项目
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/loanSignAudit")
@CheckLogin(value=CheckLogin.ADMIN)
public class LoanSignAuditController {

	@Resource
	 private  LoanSignService loanSignService;  
	
	@Resource
	 private LoanSignQuery loansignQuery;
	
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;

	@Resource
	private PayLogService payLogService;
	
	@Resource
	private BaoFuService baoFuService;
	
	@Resource
	private PlankService plankService;
	
	@Resource
	private BaoFuLoansignService  baoFuLoansignService;
	
	@Resource
	private CacheManagerService cacheManagerService;
	
	/***
	 * 审批跳转
	 * @return
	 */
	 @RequestMapping(value = { "openApproval" })
		public ModelAndView loansignApproval() {
			return new ModelAndView("WEB-INF/views/admin/loansign/loansignApproval");
		}
	 	
	 /***
		 * 财务审核跳转
		 * @return
		 */
	 
	 @RequestMapping(value = { "openAudit" })
		public ModelAndView loansignAudit() {
			return new ModelAndView("WEB-INF/views/admin/loansign/loansignAudit");
		}
	 
	    /***
	     * 新增平台服务费年利率
	     * @param id
	     * @param status
	     * @param stateNum
	     * @param request
	     * @return
	     */
		@RequestMapping("/queryCompanyFee")
		public String queryCompanyFee(String id, HttpServletRequest request) {
			if (StringUtil.isNotBlank(id) && StringUtil.isNumberString(id)) {
				Loansign loansign =loansignQuery.getLoansignById(id);
				request.setAttribute("loansign", loansign);
			}
			return "/WEB-INF/views/admin/loansign/updateCompanyFee";
		}
		
		/***
		 * 修改平台服务费年利率
		 * @param id
		 * @param companyFee
		 * @param request
		 * @return
		 */
		@ResponseBody
		@RequestMapping("/updateCompanyFee")
		public  JSONObject updateCompanyFee(String id,String companyFee,HttpServletRequest request){
			 JSONObject json = new JSONObject();
			Loansign loansign =loansignQuery.getLoansignById(id);
			loansign.setCompanyFee(Arith.div(Double.valueOf(companyFee),100,4));
			Double feeMoney=0.00;
			if(loansign.getType() == 2){    //按项目
				 if(loansign.getRefunway() == 1){  //按月份
					 feeMoney=Arith.round(Arith.mul(Arith.div(Arith.mul(loansign.getIssueLoan(), loansign.getCompanyFee()), Double.valueOf(12)),loansign.getRemonth()), 2);
				 }else if(loansign.getRefunway() == 2){ // 按季还款
					 feeMoney =Arith.round(Arith.mul(Arith.div(Arith.mul(loansign.getIssueLoan(), loansign.getCompanyFee()), Double.valueOf(12)),loansign.getRemonth()), 2);
				 }
			}else if(loansign.getType() == 3){ //按天标
				feeMoney=Arith.round(Arith.mul(Arith.div(Arith.mul(loansign.getIssueLoan(), loansign.getCompanyFee()), Double.valueOf(360)), loansign.getRemonth()), 2); 
			}
			loansign.setFeeMoney(feeMoney);
		    loanSignService.updateLoangn(loansign);
		    DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS, "新增平台年利率成功", "main59", "closeCurrent");
			return json;
		}
		
		/***
		 * 项目满标审批通过
		 * @param id
		 * @param stateNum
		 * @param status
		 * @param request
		 * @return
		 */
		@ResponseBody
		@RequestMapping("/loanAuditPass")
		public String loanAuditPass(String id, String stateNum,Integer status,
				HttpServletRequest request) {
			return  loanSignService.updateLoansignState(id, stateNum, status, request);
		}

		/**
		 * 查询修改借款人利率
		 * @param ids
		 * @param request
		 * @return
		 */
		@RequestMapping("/queryLoansignUpdate")
		public String queryLoansignUpdate(String id, HttpServletRequest request) {
			if (StringUtil.isNotBlank(id) && StringUtil.isNumberString(id)) {
				Loansign loansign =loansignQuery.getLoansignById(id);
				request.setAttribute("loansign", loansign);
			}
			return "/WEB-INF/views/admin/loansign/loansignUpdate";
		}
		
		/***
		 * 修改借款人利率
		 * @param id
		 * @param realRateOne
		 * @param request
		 * @return
		 */
		@ResponseBody
		@RequestMapping("/updateLoansign")
		public  JSONObject updateLoansign(String id,String realRateOne,HttpServletRequest request){
			 JSONObject json = new JSONObject();
			Loansign loansign =loansignQuery.getLoansignById(id);
			loansign.setRealRate(Arith.div(Double.valueOf(realRateOne),100,4));
		    loanSignService.updateLoangn(loansign);
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
	                "修改借款人利率成功", "main59", "closeCurrent");
			return json;
		}
		
		/***
		 * 查询要修改的夹层劣后百分比
		 * @param id
		 * @param request
		 * @return
		 */
		@RequestMapping("/queryLoansignUpdateRate")
		public String queryLoansignUpdateRate(String id, HttpServletRequest request) {
			if (StringUtil.isNotBlank(id) && StringUtil.isNumberString(id)) {
				Loansign loansign =loansignQuery.getLoansignById(id);
				request.setAttribute("loansign", loansign);
			}
			return "/WEB-INF/views/admin/loansign/loansignUpdateRate";
		}
		
		/***
		 * 修改夹层劣后百分比
		 * @param id
		 * @param realRateOne
		 * @param request
		 * @return
		 */
		@ResponseBody
		@RequestMapping("/updateLoansignRate")
		public  JSONObject updateLoansignRate(String id,String midRate,String afterRate, HttpServletRequest request){
			 JSONObject json = new JSONObject();
			Loansign loansign =loansignQuery.getLoansignById(id);
			loansign.setMidRate(Arith.div(Double.valueOf(midRate),100,4));
			loansign.setAfterRate(Arith.div(Double.valueOf(afterRate),100,4));
		    loanSignService.updateLoangn(loansign);
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
	                "修改夹层劣后百分比", "main59", "closeCurrent");
			return json;
		}
	 /***
	  * 审批/审核的列表
	  * @param limit
	  * @param start
	  * @param loansign
	  * @param stateNum
	  * @param request
	  * @param page
	  * @return
	  */
	@ResponseBody
	@RequestMapping("auditList")
	public JSONObject auditList(String limit, String start,
			Loansign loansign, String  stateNum, HttpServletRequest request,
			PageModel page) {

		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();
		// 得到总条数

		// int count =loanSignService.getLoansignCount(loansignbasics,loanType);

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 10);
		} else {
			page.setNumPerPage(10);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		// 分页数据源
		List datalist = loanSignService.auditListPage(page, loansign,stateNum);
		// JSONArray jsonlist = loanSignService.queryJSONByList(list);

		String titles = "id,prate,yearate,issueLoan,priority,sandwich,afterBad,prioRestMoney,midRestMoney,afterRestMoney,loanUnit,xname,proindustry,uname,loanstate,haveOther,releaseTime,getMoneyWay,remark,status,recommend,remoney,remonth,type,midRate,afterRate,realRate,feeState,feeMoney,fee,companyFee,realRateFee,redEnvelopeMoney,refunway,feeMethod";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}
	/***
	 * 收取平台服务费
	 * @param id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("loansignUpdateFee")
	public String loansignUpdateFee(String id,HttpServletRequest request){
		 return baoFuLoansignService.loansignUpdateFeeService(id, request);
	} 
		
	
	/***
	 * 审批/审核
	 * @param id
	 * @param request
	 * @param stateNum
	 * @return
	 */
	@ResponseBody
	@RequestMapping("approvalOrAudit")
	public  String  loanSignAudit(String id,HttpServletRequest request,String stateNum,Integer status){
	    String result = baoFuLoansignService.loanSignAuditService(id, request, stateNum, status);
	    cacheManagerService.updateIndexLoanList();//更新首页表列表
	    cacheManagerService.updateZhongChiPageLoanList();//更新我要众持列表页面
	    cacheManagerService.updateLoanDetailRelCache(id);//投资更新标详情信息
//	    cacheManagerService.updateUserRepaymentListByLoanSignId(Long.valueOf(id));//更新用户回款还款列表
	    return result;
    }
	
	/**
	 * 查询是否收取平台服务费
	 * @param id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("selLoanFeeState")
	public String selLoanFeeState(String id,HttpServletRequest request){
		return baoFuLoansignService.selLoanFeeState(id, request);
	}
}
