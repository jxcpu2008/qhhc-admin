package com.hc9.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Loansign;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.GeneralizeMoneyServices;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.LoanSignService;
import com.hc9.service.LoanrecordService;
import com.hc9.service.LoansignManageService;
import com.hc9.service.UserInfoServices;
import com.hc9.service.UserbasicsinfoService;

/***
 * 放款后/流标后的项目/结束后的项目
 * @author LKL
 */
@Controller
@RequestMapping("/loansignManage")
@CheckLogin(value=CheckLogin.ADMIN)
public class LoansignManageController {
	
	private static final Logger LOGGER = Logger.getLogger(UserInfoController.class);
	
	@Resource
	private LoansignManageService  loansignManageService;
	
	@Resource
	private LoanSignQuery  loanSignQuery;
	
	@Resource
	private LoanSignService loanSignService;
	
	@Resource
	private RechargeModel modelRecharge;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	private UserInfoServices userInfoServices;

	@Resource
	private LoanrecordService loanrecordService;
	
	@Resource
	private GeneralizeMoneyServices generalizeMoneyServices;
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	
	
	/***
	 * 流标的项目
	 * @return
	 */
	@RequestMapping(value = { "openFlowInfo", "/" })
	public ModelAndView index() {
		ModelAndView returnModelAndView = new ModelAndView(
				"WEB-INF/views/admin/loansign/loansignFlowInfo");
		return returnModelAndView;
	}

	/**
	 * 放款后的项目 
	 * @return
	 */
	@RequestMapping(value = { "openCreditInfo", "/" })
	public ModelAndView openCreditInfo() {
		ModelAndView returnModelAndView = new ModelAndView(
				"WEB-INF/views/admin/loansign/loansignCreditInfo");
		return returnModelAndView;
	}
	
	/***
	 * 完成后的项目
	 * @return
	 */
	@RequestMapping(value = { "openCompleteInfo", "/" })
	public ModelAndView openCompleteInfo() {
		ModelAndView returnModelAndView = new ModelAndView(
				"WEB-INF/views/admin/loansign/loansignCompleteInfo");
		return returnModelAndView;
	}
	
	/**
	 * <p>
	 * Title: loanSignList
	 * </p>
	 * <p>
	 * Description: 項目列表
	 * </p>
	 * 
	 * @param start
	 *            开始
	 * @param limit
	 *            结束
	 * @param loansignbasics
	 *            借款标基础信息表
	 * @param request
	 *            请求的request
	 * @return 结果 JSONObject
	 */
	@ResponseBody
	@RequestMapping("loansignList")
	public JSONObject loansignManageList(String limit, String start,
			Loansign loansign, HttpServletRequest request, PageModel page,String status,String beginpublishTime,String endpublishTime,String beginfullTime,String endfullTime,String begincreditTime,String endcreditTime) {

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
		List datalist = loansignManageService.loanSignPage(page, loansign, status, beginpublishTime, endpublishTime, beginfullTime, endfullTime, begincreditTime, endcreditTime);
		// JSONArray jsonlist = loanSignService.queryJSONByList(list);

		String titles = "id,prate,yearate,issueLoan,priority,sandwich,afterBad,prioRestMoney,midRestMoney,afterRestMoney,loanUnit,xname,proindustry,uname,loanstate,haveOther,releaseTime,getMoneyWay,remark,status,recommend,remoney,remonth,type,payState,fee,companyFee,midRate,afterRate,realRate,feeState,feeMoney,fee,creditTime,realRateFee,redEnvelopeMoney,pMerBillNo";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}
	
	/***
	 * 根据loansign表查询放款是否成功
	 * @param id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("ipsFullLoanNum")
	public  String ipsFullLoanNum(String id,HttpServletRequest request){
		return baoFuLoansignService.ipsFullLoanNumService(id, request);
	}
	
	/***
	 * 项目流标查询
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("ipsLoanFlowNum")
	public  String ipsLoanFlowNum(HttpServletRequest request, String id){
		return baoFuLoansignService.ipsLoanFlowNumService(request, id);
	}
	
	/***
	 * 导出项目信息
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/loansign-table-to-excel")
	public void dataToExceloansign(HttpServletRequest request,HttpServletResponse response, String id) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("dataToExcel(HttpServletRequest request=" + request + ", HttpServletResponse response=" + response + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		String headers = "序号,项目名称,项目类型,借款人,借款金额,优先金额,夹层金额,劣后金额,年利率,优先奖励,回购期限,项目所属行业,发布时间,拨款方式,投资状态,项目状态,放款时间,推荐首页";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;

		// 查询所有信息
		@SuppressWarnings("unchecked")
		List<Object> datalist =loansignManageService.queryAll(id);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : datalist) {

			Object[] result = (Object[]) obj;
			map = new HashMap<String, String>();

			// 将对应的值存放在map中
			for (int i = 0; i < headerLength; i++) {
				map.put(header[i],
						result[i] == null ? " " : result[i].toString());
			}

			content.add(map);
		}

		// 导出会员信息
		modelRecharge.downloadExcel("项目众持列表", null, header, content, response);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("dataToExcel(HttpServletRequest, HttpServletResponse)方法结束"); //$NON-NLS-1$
		}
	}
	
	/***
	 * 导出还款记录
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/outRepaymentrecord")
	public void outRepaymentrecord(HttpServletRequest request,HttpServletResponse response, String id) {

		String headers = "项目名称,期数,预计还款时间,优先预计还款金额,优先预计还款利息,夹层预计还款金额,夹层预计还款利息,劣后预计还款金额,劣后预计还款利息,应收还款金额,应收平台服务费,还款状态,实际还款时间,优先实际还款金额,夹层实际还款金额,劣后实际还款金额,实收还款金额,实收平台服务费";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;

		// 查询所有信息
		@SuppressWarnings("unchecked")
		List datalist =loansignManageService.getRepaymentrecordList(id);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : datalist) {

			Object[] result = (Object[]) obj;
			map = new HashMap<String, String>();

			// 将对应的值存放在map中
			for (int i = 0; i < headerLength; i++) {
				map.put(header[i],
						result[i] == null ? " " : result[i].toString());
			}

			content.add(map);
		}

		// 导出会员信息
		modelRecharge.downloadExcel("项目众持还款记录信息", null, header, content, response);
	}
	
	
	@RequestMapping("/outParticulars")
	public void outParticulars(HttpServletRequest request,HttpServletResponse response, String id) {

		String headers = "用户Id,用户名,真实姓名,项目名称,期数,预计还款时间,优先预计还款金额,优先预计还款利息,夹层预计还款金额,夹层预计还款利息,劣后预计还款金额,劣后预计还款利息,优先实际还款金额,夹层实际还款金额,劣后实际还款金额,还款状态,实际还款时间";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;

		// 查询所有信息
		@SuppressWarnings("unchecked")
		List datalist =loansignManageService.queryRePayMentParticularsList(id);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : datalist) {

			Object[] result = (Object[]) obj;
			map = new HashMap<String, String>();

			// 将对应的值存放在map中
			for (int i = 0; i < headerLength; i++) {
				map.put(header[i],
						result[i] == null ? " " : result[i].toString());
			}

			content.add(map);
		}

		// 导出会员信息
		modelRecharge.downloadExcel("项目众持还款明细记录信息", null, header, content, response);
	}
	
	/**
	 * 统计总金额
	 * @param request
	 * @param begincreditTime
	 * @param endcreditTime
	 * @return
	 */
	@RequestMapping("/sumMoney")
	public String sumMoney(HttpServletRequest request,String begincreditTime,String endcreditTime) {
		//总放款金额
		Double sumIssueLoan=loansignManageService.getSumIssueLoan(begincreditTime, endcreditTime,"SUM(issueLoan)");
		//优先总金额
		Double sumPriority=loansignManageService.getSumIssueLoan(begincreditTime, endcreditTime,"SUM(priority)");
		//夹层总金额
		Double sumMiddle=loansignManageService.getSumIssueLoan(begincreditTime, endcreditTime,"SUM(middle)");
		//劣后总金额
		Double sumAfter=loansignManageService.getSumIssueLoan(begincreditTime, endcreditTime,"SUM(after)");
		//平台服务费总金额
		Double sumfeeMoney=loansignManageService.getSumIssueLoan(begincreditTime, endcreditTime,"SUM(feeMoney)");
		//红包总额
		Double sumRedEnvelopeMoney=loansignManageService.getSumIssueLoan(begincreditTime, endcreditTime,"SUM(redEnvelopeMoney)");
		//总放款利息
		Double sumPreRepay=loansignManageService.getSumPreRepay(begincreditTime, endcreditTime, "");
		//待还款金额
		Double sumIssueLoanOne=loansignManageService.getSumMoney(begincreditTime, endcreditTime, " and  r.repayState in (1,3) ");
		//待还款利息
		Double sumPreRepayOne=loansignManageService.getSumPreRepay(begincreditTime, endcreditTime, " and  r.repayState in (1,3)  ");
		//已还款金额
		Double sumIssueLoanTwo=loansignManageService.getSumMoney(begincreditTime, endcreditTime, " and  r.repayState in (2,4,5) ");
		//已还款利息
		Double sumPreRepayTwo=loansignManageService.getSumPreRepay(begincreditTime, endcreditTime, " and  r.repayState in (2,4,5) ");
		//逾期利息
		Double sumOverdueInterest=loansignManageService.getSumOverdueInterest(begincreditTime, endcreditTime);
		request.setAttribute("sumIssueLoan", sumIssueLoan);
		request.setAttribute("sumfeeMoney", sumfeeMoney);
		request.setAttribute("sumPriority", sumPriority);
		request.setAttribute("sumMiddle", sumMiddle);
		request.setAttribute("sumAfter", sumAfter);
		request.setAttribute("sumRedEnvelopeMoney", sumRedEnvelopeMoney);
		request.setAttribute("sumPreRepay", sumPreRepay);
		request.setAttribute("sumIssueLoanOne", sumIssueLoanOne);
		request.setAttribute("sumPreRepayOne", sumPreRepayOne);
		request.setAttribute("sumIssueLoanTwo", sumIssueLoanTwo);
		request.setAttribute("sumPreRepayTwo", sumPreRepayTwo);
		request.setAttribute("sumOverdueInterest", sumOverdueInterest);
		return "/WEB-INF/views/admin/loansign/loansignSumMoney";
	}
	
	


}
