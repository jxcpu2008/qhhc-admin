package com.hc9.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.constant.PayURL;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.log.LOG;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.Arith;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.SmsEmailTimerDao;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.AppMessagePush;
import com.hc9.dao.entity.BorrowersApply;
import com.hc9.dao.entity.City;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Escrow;
import com.hc9.dao.entity.Industry;
import com.hc9.dao.entity.Liquidation;
import com.hc9.dao.entity.Loandynamic;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.LoansignType;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.dao.entity.MsgReminder;
import com.hc9.dao.entity.Province;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.AcctTrans;
import com.hc9.model.ExpensesInfo;
import com.hc9.model.P2pQuery;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.model.crs;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.BaoFuService;
import com.hc9.service.BaseLoansignService;
import com.hc9.service.CacheManagerService;
import com.hc9.service.ColumnManageService;
import com.hc9.service.EscrowService;
import com.hc9.service.IMessagePushManageService;
import com.hc9.service.LoanInfoService;
import com.hc9.service.LoanManageService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.LoanSignService;
import com.hc9.service.LoanrecordService;
import com.hc9.service.LoansignTypeService;
import com.hc9.service.PayLogService;
import com.hc9.service.PlankService;
import com.hc9.service.ShopService;
import com.hc9.service.SmsSendService;
import com.hc9.service.UserInfoServices;
import com.hc9.service.UserbasicsinfoService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**普通标Controller */
@Controller
@RequestMapping("/loanSign")
@CheckLogin(value=CheckLogin.ADMIN)
public class LoanSignController {
	private static final Logger LOGGER = Logger.getLogger(LoanSignController.class);

	@Autowired
	private IMessagePushManageService messagePushService;
	
	@Value("${push.newUnderlyingAssets.notification}")
	private String notification;
	
	/** 用于调用常用方法的dao */
	@Resource
	HibernateSupportTemplate dao;

	/** loanSignService:普通借款标的service */
	@Resource
	private LoanSignService loanSignService;

	/** baseLoansignService:借款标公用方法的service */
	@Resource
	private BaseLoansignService baseLoansignService;

	/** loanSignQuery 公用借款标的查询 */
	@Autowired
	private LoanSignQuery loanSignQuery;

	/** rechargeModel:导出数据的公用model */
	@Resource
	private RechargeModel rechargeModel;

	/** loanrecordService 认购记录services */
	@Resource
	private LoanrecordService loanrecordService;

	@Resource
	private LoansignTypeService loansignTypeService;

	@Resource
	private UserInfoQuery userInfoQuery;

	@Resource
	private ShopService projectService;

	@Resource
	private ServletContext application;

	/*** 引用ColumnManageService */
	@Resource
	ColumnManageService columnservice;

	/** 注入excel文件生成工具 */
	@Resource
	private RechargeModel modelRecharge;

	@Resource
	private PayLogService payLogService;

	/** 操作执行成功后要刷新的页面 */
	private String pageId = "main46";

	/** 注入会员服务层 */
	@Resource
	private UserInfoServices userInfoServices;

	@Resource
	private LoanManageService loanManageService;

	@Resource
	private EscrowService escrowService;

	@Resource
	private LoanInfoService loanInfoService;

	@Resource
	private PlankService plankService;
	@Resource
	private BaoFuService baoFuService;
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	
	@Resource
	SmsEmailTimerDao smsEmailTimerDao;
	
	@Resource
	SmsSendService smsSendService;
	
	@Resource
	private CacheManagerService cacheManagerService;
	
	private DecimalFormat df = new DecimalFormat("0.00");
	

	/*-----------------项目开始-------------------*/
	/**
	 * <p>
	 * Title: index
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return 后台普通标展示页面 拨款
	 */
	@RequestMapping(value = { "index", "/" })
	public ModelAndView index() {
		ModelAndView returnModelAndView = new ModelAndView(
				"WEB-INF/views/admin/loansign/loansign");
		return returnModelAndView;
	}

	/**
	 * 项目审核
	 * 
	 * @return
	 */
	@RequestMapping(value = { "openExamine", "/" })
	public ModelAndView openExamine() {
		ModelAndView returnModelAndView = new ModelAndView(
				"WEB-INF/views/admin/loansign/loanExamine");
		return returnModelAndView;
	}

	/**
	 * 添加合同编号
	 * 
	 * @param ids
	 * @param requestdfdfd
	 * @return
	 */
	@RequestMapping("/addContractNo")
	public String addContractNo(String ids, HttpServletRequest request) {
		request.setAttribute("loaninfo",
				dao.get(Loansign.class, Long.parseLong(ids)));
		return "/WEB-INF/views/addContractNo";
	}

	/**
	 * 确认添加合同编号
	 * 
	 * @param loansign
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/update_addContractNo")
	public JSONObject updateAddContractNo(Loansign loansign) {

		JSONObject json = new JSONObject();
		loanSignService.updateLoanContractNo(loansign);
		DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS, "修改成功",
				pageId, "closeCurrent");
		return json;
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
	@RequestMapping("loanSignList")
	public JSONObject loanSignList(String limit, String start,
			Loansign loansign, HttpServletRequest request, PageModel page) {

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
		List datalist = loanSignService.loanSignPage(page, loansign);
		// JSONArray jsonlist = loanSignService.queryJSONByList(list);

		String titles = "id,prate,yearate,issueLoan,priority,sandwich,afterBad,prioRestMoney,midRestMoney,afterRestMoney,loanUnit,xname,proindustry,uname,loanstate,haveOther,releaseTime,getMoneyWay,remark,status,recommend,remoney,remonth,type,midRate,afterRate,realRate,feeState,feeMoney,fee,companyFee,realRateFee,redEnvelopeMoney,activityStatus";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}

	/**
	 * 
	 * 项目审核列表
	 * 
	 * @param limit
	 * @param start
	 * @param loansignbasics
	 * @param loanType
	 * @param request
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("loansignExamine")
	public JSONObject loansignExamine(String limit, String start,
			String beginDate, String endDate, String loanType,
			Loansign loansign, HttpServletRequest request, PageModel page) {

		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();
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
		List datalist = loanSignService.getLoansignExamine(page, beginDate,
				endDate, loanType);

		String titles = "id,prate,yearate,priority,sandwich,afterBad,prioRestMoney,midRestMoney,afterRestMoney,loanUnit,xname,proindustry,loanstate,haveOther,releaseTime,getMoneyWay,remark,state,uname,loanmoney,month,createTime,adminuser,type,midRate,afterRate";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}

	/**
	 * 20150731-lkl
	 * 查看项目的详情信息
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping("getLoansignInfo")
	public String getLoansigns(HttpServletRequest request, String id) {
		Loansign loan = null;
		List<MsgReminder> listMsg=null;
		// 通过id查询到信息
		loan = loanSignQuery.getLoansignById(id);
		listMsg=loanSignQuery.getListMsgreminder(id);
		request.setAttribute("loan", loan);
		request.setAttribute("listMsg", listMsg);
		return "WEB-INF/views/admin/column/loansignInfo";
	}

	/**
	 * 修改项的详情信息
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping("getUpdateLoansigns")
	public String getUpdateLoansigns(HttpServletRequest request, String id) {
		Loansign loan = null;
		// 通过id查询到信息
		loan = loanSignQuery.getLoansignById(id);
		request.setAttribute("loan", loan);
		return "WEB-INF/views/admin/column/loansignInfoUpt";
	}

	/***
	 * 项目申请放款审批
	 * 
	 * @param ids
	 * @param request
	 * @param state
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/auditLoansignCredit")
	public String auditLoansignCredit(String ids, HttpServletRequest request) {
		Loansign loansign = loanSignQuery.getLoansignById(ids.substring(0,
				ids.length() - 1));
		// 判断是否有待确认的

		if (loanSignService.hasWaitforConfirm(Long.valueOf(ids.substring(0,
				ids.length() - 1)))) {
			return "3";
		}
		// 判断是否满标
		if (loansign.getRestMoney() != 0) {
			return "4";
		}
		String num = "";
		num = loanSignService.auditLoansignCredit(ids, request);
		return num;
	}

	/***
	 * 
	 * @param ids
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryLoanExamine")
	public String queryLoanExamine(String id, HttpServletRequest request) {
		Loansign loansign = loanSignQuery.getLoansignById(id.substring(0,
				id.length() - 1));
		request.setAttribute("loansign", loansign);
		return "/WEB-INF/views/admin/loansign/updateLoanExamine";
	}

	/***
	 * 审核通过
	 * 
	 * @param ids
	 * @param request
	 * @param state
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/auditloansign")
	public JSONObject percardPasstou(String id,
			String loanName,
			String refunway,
			Integer feeMethod,
			Integer type,
			Integer remonth,
			Double issueLoan, 
			Double realRate,
			Double companyFee,
			Double prioRate,
			Double prioAwordRate,
			Loansignbasics loansignbasics,
			HttpServletRequest request) {
		
		JSONObject json = new JSONObject();
		if(refunway.equals("4")){
			Double subPrio = Arith.div(Arith.sub(realRate, companyFee),100);
			Double addRate = Arith.add(prioRate,prioAwordRate);
			if(subPrio-addRate!=0){
			   DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
						"借款总利率不等于优先利率+平台服务费利率", "main45", "closeCurrent");
			   return json;
			}
		}
		String state = loanSignService.updateLoansignState(id, 2, loansignbasics,request,
				realRate,companyFee,feeMethod);
		if (state == "2") {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
					"审核成功", "main45", "closeCurrent");
			
			cacheManagerService.updateZhongChiPageLoanList();
			cacheManagerService.updateH5ZhongChiPageLoanList();
			cacheManagerService.updateLoanDetailRelCache(id);//投资更新标详情信息
			
			try {
				String reMonth = remonth + "个月";
				if (type.intValue() == 3) {	// 天标
					reMonth = remonth + "天";
				}
				
				// 众持项目审核通过后，推送消息
				String[] notificationMsg = notification.split("：");
				// 年化利率的格式化
				DecimalFormat prioRateFormatter = new DecimalFormat("0.##%");
				// 借款金额的格式化
				DecimalFormat issueLoanFormatter = new DecimalFormat("#.##");
				// 格式化推送消息，'issueLoan / 10000'是因为需求规定新标推送消息的借款金额是按照'万'来显示
				String messageContent = MessageFormat.format(notificationMsg[1], issueLoanFormatter.format(issueLoan / 10000), reMonth, prioRateFormatter.format(prioRate));
				
				Map<String, String> customizedFields = new HashMap<String, String>();
				customizedFields.put("page", "newUnderlying");
				customizedFields.put("bidName", loanName);
				customizedFields.put("bid", id);
				customizedFields.put("sender", "system");
				customizedFields.put("title", notificationMsg[0]);
				AppMessagePush message = new AppMessagePush();
				// 广播
				message.setTitle(notificationMsg[0]);
				message.setPushNow(1);
				message.setPushType(4);
				message.setContent(messageContent);
				message.setDescription(notificationMsg[0]);
				message.setOperator("system");
			
				messagePushService.pushMessage(message, customizedFields);
			} catch (Exception e) {	// 消息推送异常不影响正常的业务流程，只需要把异常信息记录到日志即可
				LOGGER.info("新标上线消息推送失败！", e);
			}
			
		} else {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
					"审核失败", "main45", "closeCurrent");
		}

		return json;
	}

	/***
	 * 审核不通过
	 * 
	 * @param ids
	 * @param request
	 * @param state
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/auditloansignot")
	public String auditloansignot(String ids, HttpServletRequest request) {
		String num = loanSignService.updateLoansignState(ids, 3, null,request, 0.00,0.00,0);
		return num;
	}

	/**
	 * 新增/修改行业
	 * 
	 * @param Industry
	 *            行业
	 * @param operation
	 *            add/upt
	 * @param request
	 *            HttpServletRequest
	 * @return JSONObject
	 */
	@RequestMapping("/add_upt_industry")
	@ResponseBody
	public JSONObject update(@ModelAttribute("Industry") Industry industry,
			String operation, HttpServletRequest request) {
		JSONObject json = new JSONObject();
		try {
			if (operation.equals("add")) {
				loanSignService.addIndustry(industry);
			} else if (operation.equals("upt")) {
				loanSignService.updateIndustry(industry);
			}
			// 重置application
			columnservice.resetApplaction(request);
			return columnservice.setJson(json, "200", "更新成功", "main66",
					"closeCurrent");
		} catch (Exception e) {
			e.printStackTrace();
			return columnservice.setJson(json, "300", "更新失败", "main66",
					"closeCurrent");
		}
	}

	/**
	 * 删除行业
	 * 
	 * @param ids
	 *            被选中行业的id
	 * @param request
	 *            HttpServletRequest
	 * @return JSONObject
	 */
	@RequestMapping("/deleteIndustry")
	@ResponseBody
	public JSONObject deleteTopics(String ids, HttpServletRequest request) {
		JSONObject json = new JSONObject();
		try {
			loanSignService.deleteIndustry(ids);
			// 重置application
			columnservice.resetApplaction(request);
			return columnservice.setJson(json, "200", "更新成功", "main66", "");
		} catch (Exception e) {
			return columnservice.setJson(json, "300", "更新失败", "main66", "");
		}
	}

	/**
	 * 跳转到添加/修改行业的页面
	 * 
	 * @param Id
	 *            行业 id
	 * @param operation
	 *            add/upt
	 * @param request
	 *            HttpServletRequest
	 * @return ModelAndView
	 */
	@RequestMapping("/forwardAddOrUpt")
	public ModelAndView queryTopicById(Integer Id, String operation,
			HttpServletRequest request) {
		if (operation.equals("upt")) {
			request.setAttribute("industry",
					loanSignService.queryIndustryById(Id));
		}
		request.setAttribute("operation", operation);
		return new ModelAndView("WEB-INF/views/admin/column/add_upt_industry");
	}

	@RequestMapping(value = { "dustrylists" })
	public ModelAndView IndustryPage(PageModel page, HttpServletRequest request) {
		request.setAttribute("dustrylist", loanSignService.IndustryPage(page));
		request.setAttribute("page", page);
		return new ModelAndView("WEB-INF/views/admin/loansign/loanproin");

	}

	/**
	 * 
	 * 项目发布
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("loanRelease")
	public String LoanRelease(HttpServletRequest request, String id) {
		Loansign loan = null;
		if (loan.getStatus() == 1) {
			return "0"; // 发布成功
		} else {
			loan.setPublishTime(DateUtils.format("yyyy-MM-dd HH:mm:ss")); // 发布时间
			loan.setStatus(1); // 发布中
			// projectService.updateLoansign(loan);
			return "1"; // 返回 1 表示进行中
		}
	}


	/**
	 * 项目动态发布
	 * 
	 * @param request
	 * @param Id
	 *            项目Id
	 * @param title
	 *            项目标题
	 * @param remark
	 *            项目内容
	 * @return
	 */
	@RequestMapping("savaloandynames")
	public String savaloandyname(HttpServletRequest request,
			Loandynamic loandynamic) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);

		if (user != null) {
			loandynamic.setUserId(user.getId());
			loandynamic.setPublishTime(DateUtils.format("yyyy-MM-dd HH:mm:ss")); // 发布时间
			loanSignService.savaloandynamic(loandynamic);

			return "WEB-INF/views/success";
		}
		return null;
	}

	/**
	 * 项目修改
	 * 
	 * @param request
	 * @param loanbasic
	 * @return
	 */
	@RequestMapping("updateLoangins")
	@ResponseBody
	public JSONObject getUpdateLoan(HttpServletRequest request,
			@ModelAttribute("Loansignbasics") Loansignbasics loanbasic,
			Loansign loansign) {
		JSONObject json = new JSONObject();
		// 通过id查询到信息
		Loansign ls = loanSignQuery
				.getLoansignById(loansign.getId().toString());
		/*
		 * if (ls.getType() == 3) { return columnservice.setJson(json, "300",
		 * "天标不允许更新", "main45", ""); }
		 */
		if (ls.getState() != 1) {
			return columnservice.setJson(json, "300", "进行中不允许更新", "main45", "");
		}
		try {
			ls.setPrioRate(loansign.getPrioRate() / 100);
			ls.setPrioAwordRate(loansign.getPrioAwordRate() / 100);
			ls.setMidRate(loansign.getMidRate() / 100);
			ls.setAfterRate(loansign.getAfterRate() / 100);
			ls.setMiddle(loansign.getMiddle());
			ls.setMidRestMoney(loansign.getMiddle());
			ls.setPriority(loansign.getPriority());
			ls.setPrioRestMoney(loansign.getPriority());
			ls.setAfter(loansign.getAfter());
			ls.setAfterRestMoney(loansign.getAfter());
			Double subMoney = Arith.add(loansign.getPriority(),
					loansign.getMiddle());
			ls.setRestMoney(Arith.add(subMoney, loansign.getAfter())
					);
			ls.setIssueLoan(Arith.add(subMoney, loansign.getAfter())
					);
			loanSignService.updateLoangn(ls);
			// 重置application
			columnservice.resetApplaction(request);
			return columnservice.setJson(json, "200", "更新成功", "main45", "");
		} catch (Exception e) {
			e.printStackTrace();
			return columnservice.setJson(json, "300", "更新失败", "main45", "");
		}
	}

	/*-----------------项目结束-------------------*/

	/**
	 * <p>
	 * Title: onTimeRepay
	 * </p>
	 * <p>
	 * Description: 按时还款
	 * </p>
	 * 
	 * @param repaymentRecordId
	 *            按时还款的记录编号
	 * @param repayTime
	 *            按时选择还款时间
	 * @return 数字 1.成功 2.还款失败,只能针对未还款记录还款,请尝试刷新页面！ 3.还款时间应该在预计还款时间之前！
	 *         4.还款失败,只能按期数顺序依次还款！ 5 异常
	 */
	@ResponseBody
	@RequestMapping("/onTimeRepay")
	public int onTimeRepay(String repaymentRecordId, String repayTime) {
		int state = 0;
		// 1.判断是否可以还款
		Repaymentrecord repaymentrecord = dao.get(Repaymentrecord.class,
				Long.valueOf(repaymentRecordId));
		if (repaymentrecord.getRepayState() != 1) {// 1.未还款
			state = 2;
		}
		try {
			// 按时还款的时间不能大于预计还款时间
			if (state > 0
					&& DateUtils.isBefore(Constant.DEFAULT_DATE_FORMAT,
							repaymentrecord.getPreRepayDate(),
							Constant.DEFAULT_DATE_FORMAT, repayTime)) {
				state = 3;
			}
		} catch (ParseException e) {
			return 5;
		}

		// 判断是否按期数还款
		if (loanSignQuery.checkRepayOrder(repaymentrecord)) {
			state = 4;
		}
		// 还款
		boolean bool = baseLoansignService.onTimeRepay(repaymentrecord,
				repayTime);
		state = bool ? 1 : 5;
		return state;
	}

	/**
	 * <p>
	 * Title: exceedTimeRepay
	 * </p>
	 * <p>
	 * Description: 逾期还款
	 * </p>
	 * 
	 * @param repaymentRecordId
	 *            逾期还款的编号
	 * @return 数字状态 1.成功 2.还款失败,只能针对未还款记录还款,请尝试刷新页面. 3.还款失败,只能按期数顺序依次还款！4.异常
	 */
	@ResponseBody
	@RequestMapping("/exceedTimeRepay")
	public int exceedTimeRepay(String repaymentRecordId) {

		JSONObject json = new JSONObject();
		// 1.判断是否可以还款
		Repaymentrecord repaymentrecord = dao.get(Repaymentrecord.class,
				Long.valueOf(repaymentRecordId));
		if (repaymentrecord.getRepayState() != 1) {// 1.未还款
			return 2;
		}
		// 判断是否按期数还款
		if (loanSignQuery.checkRepayOrder(repaymentrecord)) {
			return 3;
		}
		// 逾期还款
		boolean bool = baseLoansignService.exceedTimeRepay(repaymentrecord);
		return bool ? 1 : 4;
	}

	/**
	 * <p>
	 * Title: exceedTimeRepayed
	 * </p>
	 * <p>
	 * Description: 逾期已还款
	 * </p>
	 * 
	 * @param repaymentRecordId
	 *            逾期还款的编号
	 * @param repayTime
	 *            时间
	 * @return 数字状态 1.成功 2.还款失败 3.还款失败,只有平台垫付的标可以还款,请尝试刷新页面！
	 *         4.还款失败,实际还款时间必须大于预计还款时间！
	 */
	@ResponseBody
	@RequestMapping("/exceedTimeRepayed")
	public int exceedTimeRepayed(String repaymentRecordId, String repayTime) {

		// 1.判断是否可以还款
		Repaymentrecord repaymentrecord = dao.get(Repaymentrecord.class,
				Long.valueOf(repaymentRecordId));
		if (repaymentrecord.getRepayState() != 3) {// 3.逾期未还款
			return 3;
		}

		boolean flag = true;
		try {
			flag = DateUtils.isAfter(Constant.DEFAULT_DATE_FORMAT,
					repaymentrecord.getPreRepayDate(),
					Constant.DEFAULT_DATE_FORMAT, repayTime);
		} catch (ParseException e) {
		}

		if (flag) {
			return 4;
		}

		boolean bool = loanSignService.updateRepaymentRecord(repaymentrecord,
				repayTime);
		int returnint = bool ? 1 : 2;

		return returnint;
	}

	/**
	 * <p>
	 * Title: outPutLoanSignExcel
	 * </p>
	 * <p>
	 * Description: 导出普通标借款列表
	 * </p>
	 * 
	 * @param request
	 *            request
	 * @param response
	 *            response
	 */
	@RequestMapping("/outPutExcel")
	public void outPutLoanSignExcel(HttpServletRequest request,
			HttpServletResponse response) {

		// 标题
		String[] header = new String[] { "序号", "借款标号", "标题", "借款人", "最小出借单位",
				"借款金额", "还款期限", "借款标类型", "标种子类型", "借款管理费", "年化利率", "平台奖励",
				"成功借出份数", "剩余份数", "还款方式", "借款标状态", "是否放款", "放款时间", "发布时间",
				"是否显示", "推荐到首页" };
		// 行宽度
		Integer[] column = new Integer[] { 8, 10, 11, 12, 12, 10, 10, 12, 10,
				10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
		// 获取数据源
		List list = loanSignService.outPutList();

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;

		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("序号", str[0] + "");
			map.put("借款标号", str[1] + "");
			map.put("标题", str[2] + "");
			map.put("借款人", str[3] + "");
			map.put("最小出借单位", str[4] + "");
			map.put("借款金额", Arith.round(new BigDecimal(str[5].toString()), 2)
					+ "元");
			map.put("还款期限", str[6] + "个月");
			map.put("借款标类型", str[7] + "");
			map.put("标种子类型", str[8] + "");
			map.put("借款管理费", str[9] + "");
			map.put("年化利率", Arith.round(new BigDecimal(str[11].toString()), 2)
					+ "%");
			map.put("平台奖励", Arith.round(new BigDecimal(str[12].toString()), 2)
					+ "%");
			map.put("成功借出份数", Integer.parseInt(str[13].toString()) + "");
			map.put("剩余份数", Double.valueOf(str[14].toString()) > 0 ? str[14]
					+ "" : "满标");
			map.put("还款方式", str[15] + "");
			map.put("借款标状态", str[16] + "");
			map.put("是否放款", str[17] + "");
			map.put("发布时间", null != str[10] ? str[10].toString() : "");
			map.put("放款时间", null != str[18] ? str[18].toString() : "");
			map.put("是否显示", str[19] + "");
			map.put("推荐到首页", str[20] + "");
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("普通借款标", column, header, content, response);
	}

	/**
	 * 在发布净值标的时候判断用户的借款金额 (净值标借款金额 = 用户前期投资未回收的所有本息和 *70%) 用户发布的金额
	 * 
	 * @param id
	 *            用户编号
	 * @return 返回用户可发布的净值标金额
	 */
	public String judgeLoan(Double money, Long id, Integer typeId) {
		Userbasicsinfo user = new Userbasicsinfo(id);
		// user.setId(id); //TODO 构造方法注入
		BorrowersApply boor = userInfoQuery.getBorrowersApplys(id);
		/*
		 * if (!boor.getType().equals(typeId)) {// 发标的类型和申请的类型不一样 return "0"; }
		 * if (boor.getMoney() < money) {// 发标金额大于了借款金额 return "1"; } if
		 * (boor.getType() == 4) { Double moneyAndInterest =
		 * loanSignService.getLoanRecordMoney(id); Double moenys =
		 * Arith.mul(moneyAndInterest, 0.7); if (money > moenys) {
		 * return "2"; } }
		 */
		return "3";
	}

	/**
	 * <p>
	 * Title: loanrecordList
	 * </p>
	 * <p>
	 * Description: 认购记录列表
	 * </p>
	 * 
	 * @param start
	 *            开始
	 * @param limit
	 *            结束
	 * @param id
	 *            编号
	 * @param request
	 *            请求
	 * @return 结果集
	 */
	@ResponseBody
	@RequestMapping(value = { "loanrecordList", "/" })
	public JSONObject loanrecordList(
			@RequestParam(value = "start", defaultValue = "0", required = false) int start,
			@RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
			@RequestParam(value = "id", defaultValue = "", required = false) int id,
			HttpServletRequest request) {

		// 得到总条数
		Object count = loanrecordService.getLoanrecordCount(id);
		// 分页数据源
		List list = loanrecordService.queryLoanrecordList(start, limit, id, 1);
		JSONArray jsonlist = loanrecordService.getJSONArrayByList(list);

		JSONObject resultjson = new JSONObject();
		// 将数据源封装为json对象（命名必须rows）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", count);
		return resultjson;
	}

	@RequestMapping("/loansign-table-to-excel")
	public void dataToExceloansign(HttpServletRequest request,
			HttpServletResponse response, String ids, Loansign loansign) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("dataToExcel(HttpServletRequest request=" + request + ", HttpServletResponse response=" + response + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		String headers = "序号,项目名称,项目类型,借款人,借款金额,优先金额,夹层金额,劣后金额,优先年利率,优先奖励,借款利率,平台服务费利率,回购期限(普通/月;短期/天),发布时间,拨款方式,投资状态,项目状态,推荐首页,红包总额,活动状态";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;

		// 查询所有信息
		@SuppressWarnings("unchecked")
		List<Object> datalist = loanSignService.queryAll(ids, loansign);

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

		// 导出众持列表
		modelRecharge.downloadExcel("项目众持列表", null, header, content, response);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("dataToExcel(HttpServletRequest, HttpServletResponse)方法结束"); //$NON-NLS-1$
		}
	}

	/**
	 * <p>
	 * Title: outLoanrecordExcel
	 * </p>
	 * <p>
	 * Description: 导出出借记录
	 * </p>
	 * 
	 * @param id
	 *            编号
	 * @param response
	 *            response
	 */
	@RequestMapping("outLoanrecordExcel")
	public void outPutLoanrecordExcel(int id, HttpServletRequest request,
			HttpServletResponse response) {

		// 标题
		String[] header = new String[] { "项目名称", "用户名", "购买金额", "购买时间", "购买状态",
				"特权会员", "投资类型", "管理费","红包金额" };

		// 获取数据源
		List list = loanrecordService.queryLoanrecordList(0, 0, id, 2);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("项目名称", str[1] + "");
			map.put("用户名", str[2] + "");
			map.put("购买金额",
					str[3] == null && str[3] == "" ? "" : Arith.round(
							new BigDecimal(str[3].toString()), 2) + "元");
			map.put("购买时间", str[4] + "");
			map.put("购买状态", str[5] + "");
			map.put("特权会员", str[6] + "");
			map.put("投资类型", str[7] + "");
			map.put("管理费", str[8] + "");
			map.put("红包金额", str[9] == null && str[9] == "" ? "" : Arith.round(
					new BigDecimal(str[9].toString()), 2) + "元");
			content.add(map);

		}
		// 下载excel
		rechargeModel.downloadExcel("出借记录", null, header, content, response,
				request);
	}

	@RequestMapping("loanUploadFile")
	@ResponseBody
	public String uploadFile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String name = projectService.uploadFile(request, response);

		return name;
	}

	/**
	 * <p>
	 * Title: loanSignList
	 * </p>
	 * <p>
	 * Description: 债权转让列表
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
	@RequestMapping(value = { "loanflowList", "/" })
	public JSONObject loansignflowList(String limit, String start,
			Loansignbasics loansignbasics, String loanType,
			HttpServletRequest request, PageModel page) {

		JSONObject resultjson = new JSONObject();
		// 得到总条数
		/*
		 * int count =
		 * loanSignService.getLoansignCount(loansignbasics,loanType);
		 */

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
		List list = loanSignService.loanSignflowPage(page, loansignbasics,
				loanType);
		/* JSONArray jsonlist = loanSignService.queryJSONByList(list); */
		JSONArray jsonlist = new JSONArray();
		String titles = "id,xname,proindustry,userName,loanstate,haveOther,releaseTime,getMoneyWay,remark,status,recommend,remoney  ";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}

	/**
	 * 更新没有完成的记录重新生成还款计划
	 * 
	 * @param id
	 *            标的ID
	 */
	@RequestMapping("updateFullBid")
	@ResponseBody
	public String updatefullBid(String id) {
		try {
			Loansign loansign = loanSignQuery.getLoansignById(id);
			loanSignQuery.deleteRepaymentrecord(loansign);
			baoFuLoansignService.repayMentRecordLast(loansign, false);
			return "1";
		} catch (Exception e) {
			e.printStackTrace();
			return "2";
		}

	}



	/**
	 * 删除众筹列表
	 * 
	 * @return
	 */
	@RequestMapping("deleteLoansign")
	@ResponseBody
	public int deleteLoansignByloansignId(String id) {
		Loansign loan = null;
		loan = dao.get(Loansign.class, Long.parseLong(id));
		int i = 0; // 0失败，1成功，2，审核成功项目
		if (loan.getState().equals(2)) {
			i = 2;
		} else {
			i = loanSignService.deleteLoansignByloansignId(id);
		}
		return i;
	}

	/***
	 * 项目放款
	 * 
	 * @param request
	 * @param ids
	 * @return
	 */
	@RequestMapping("ipsLoanCredit")
	@ResponseBody
	public String ipsLoanCredit(HttpServletRequest request, String id,
			Long adminId) throws ParseException {
          return baoFuLoansignService.ipsLoanCreditService(request, id, adminId);
	}

	/***
	 * 项目流标
	 * 
	 * @param request
	 * @param id
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("ipsLoanFlow")
	@ResponseBody
	public String ipsLoanFlow(HttpServletRequest request, String id)throws ParseException {
		return baoFuLoansignService.ipsLoanFlowService(request, id);
	}

	/**
	 * 清盘计划 *清盘：由第三方机构 以专帐方式赔付到 投资 ； 定义：当用户无法偿还逾期大于6天以上的项目时，采取清盘措施 清盘金额生成:
	 * 正常还款额=优先本息+夹层本： 优先：保本，保利息(最后一期) 夹层：保本，不保息 劣后：不保本，不保息
	 * 
	 * 大于正常还款额，额度分配： 1）优先：保本，保利息(最后一期) 2）夹层：保本 3）劣后：本 （(大于正常还款额-正常还款额）） 4）夹层 利息
	 * （(大于正常还款额-正常还款额）*夹层比例） 5）劣后 利息 （(大于正常还款额-正常还款额）*劣后比例） 个人所的：
	 * 优先个人：本+息（最后一期） 夹层个人：本+（比例*夹层利息） 劣后个人：（比例*劣后本）+（比例*劣后利息）
	 * 
	 * 
	 * @param lonid
	 *            清盘标ID
	 * @param Money
	 *            清盘金额
	 * @param threeId
	 *            第三方还款人ID
	 * @return -1 清盘额小于 清盘金额 1 生成清盘数据成功 0 失败 2清盘计划以存在 3第三方人不存在
	 */
	@ResponseBody
	@RequestMapping("liquidationPlan")
	public String liquidationPlan(String lonid, String money, String escrowId,
			String periods) {
		Double periodsSum = 0.00; // 优先
		Double outMoney = 0.00; // 剩余金额
		Double middleSumMoney = 0.00; // 夹层
		// 当前标是否有生成 过清盘计划
		double count = loanManageService.getLoanginLiquidationPlanCount(lonid);
		if (count > 0) {
			return "2";
		}
		// 得到第三方人资料
		Escrow escrow = escrowService.queryEscrowInfoById(escrowId);
		if (escrow == null) {
			return "3";
		}
		// 得到优先还款数据；
		List<Repaymentrecord> repaymentrecord = loanSignService
				.getFinallyPeriodsRepaymentrecord(lonid, periods);
		try {
			if (repaymentrecord.size() > 0) {
				Repaymentrecord repayMentRecord = repaymentrecord.get(0); // 获取还款记录
				// 清盘标的信息
				Loansign loansign = repayMentRecord.getLoansign();
				// 优先本息
				periodsSum = repayMentRecord.getPreRepayMoney()
						+ loansign.getPriority();
				// 夹层本金
				middleSumMoney = loansign.getMiddle();
				// 清盘后还剩余的金额；
				outMoney = Double.parseDouble(money) - periodsSum
						- middleSumMoney;
				// 清盘金额是否大于优先本息+夹层本金
				if (outMoney < 0) {
					return "-1";
				}
				// 得到投资人还款的所有信息
				List<ExpensesInfo> expensList = loanManageService
						.investorInteest(repayMentRecord);
				for (int j = 0; j < expensList.size(); j++) {
					double mymoney = 0;
					ExpensesInfo info = expensList.get(j);
					// 写入清盘记录到表
					Liquidation ld = new Liquidation();
					// 当前收款人信息
					Userbasicsinfo us = userInfoServices
							.queryBasicsInfoById(info.getUserId().toString());
					if (info.getLoanType() == 1) {// 优先
						// 本金+利息
						mymoney = info.getMoney() + info.getInterest();
					} else if (info.getLoanType() == 2) {// 夹层
						// 本金
						mymoney = info.getLoanMoney();
					}
					// 更新到数据库
					mymoney = Double.valueOf(df.format(mymoney));
					if (mymoney > 0) {
						ld.setAmount(new BigDecimal(mymoney));
						ld.setFee(new BigDecimal(00));
						ld.setLiquidationState(0);
						ld.setLoansign(loansign);
						ld.setUserbasicsinfo(us);
						ld.setEscrow(escrow);
						loanManageService.saveLiquidation(ld);
					}
				}
			}
		} catch (Exception e) {
			return "0";
		}
		return "1";
	}

	/**
	 * 校验清盘是否成功
	 * 
	 * @param orderid
	 * @return
	 */

	public boolean liquidationState(String orderid, Liquidation ld, String lonid) {
		List<NameValuePair> nvps;
		P2pQuery p2pQuery = new P2pQuery(orderid, 7);
		nvps = new ArrayList<NameValuePair>();
		try {
			String shopFullxml = ParseXML.p2pQueryXml(p2pQuery);
			nvps.add(new BasicNameValuePair("requestParams", shopFullxml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(shopFullxml
					+ "~|~" + ParameterIps.getmerchantKey())));
			payLogService.savePayLog(shopFullxml, ld.getUserbasicsinfo()
					.getId(), Long.parseLong(lonid), 15, orderid, ld.getFee()
					.doubleValue(), ld.getFee().doubleValue(), ld.getAmount()
					.doubleValue());
			String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL,
					nvps);
			System.out.println("返回信息" + result);
			Document doc = DocumentHelper.parseText(result);
			Element rootElt = doc.getRootElement(); // 获取根节点
			// System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
			// 拿到crs节点下的子节点code值
			String code = rootElt.elementTextTrim("code");
			String msg = rootElt.elementTextTrim("msg");
			String sign = rootElt.elementTextTrim("sign");
			// 获取子节点crs下的子节点result
			Iterator iteratorResult = rootElt.elementIterator("result");
			String state = "0"; // 0-失败 1-成功
			while (iteratorResult.hasNext()) {
				Element itemEle = (Element) iteratorResult.next();
				Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
				while (iteratorOrder.hasNext()) {
					Element elementOrder = (Element) iteratorOrder.next();
					state = elementOrder.elementTextTrim("state");
					String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"
							+ ParameterIps.getDes_algorithm());
					if (sign.equals(Md5sign)) {
						if (code.equals("CSD000")) {
							if (state.equals("1")) {
								return true;
							}
						} else if (code.equals("CSD333")) {
							return false;
						} else {
							LOG.error("查询处理失败--" + result + "----->订单号----->"
									+ orderid);
							return false;
						}
					} else {
						LOG.error("查询返回数据--" + result + "----->订单号----->"
								+ orderid);
						return false;
					}
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("查询失败----->订单号----->" + orderid);
			return false;
		}
	}

	/**
	 * 清盘
	 * 
	 * @param lonid
	 * @return 1成功 0失败 -1没有数据
	 */
	@ResponseBody
	@RequestMapping("liquidation")
	public String liquidation(String lonid) {
		// 得到清列表
		List<Liquidation> lds = loanManageService.getLiquidationbyLoanid(lonid);

		if (lds.size() > 0) {
			for (int i = 0; i < lds.size(); i++) {
				Liquidation ld = lds.get(i);
				String ordernum = "ZZ" + new Date().getTime();
				AcctTrans at = new AcctTrans();
				at.setMerchant_id(ParameterIps.getCert());
				at.setOrder_id(ordernum);
				at.setPayer_user_id(ld.getEscrow().getStaffBaofu());
				at.setPayee_user_id(ld.getUserbasicsinfo().getpMerBillNo());// 收款
				at.setPayer_type(0);
				at.setPayee_type(0);// 收款
				at.setAmount(Double.valueOf(df.format(ld.getAmount())));
				at.setFee(0.00);
				at.setFee_taken_on(1);
				at.setReq_time(new Date().getTime());

				// 更新到数据库
				ld.setAmount(new BigDecimal(at.getAmount()));
				ld.setFee(new BigDecimal(at.getFee()));
				ld.setLiquidationState(-1);
				ld.setLoansign(ld.getLoansign());
				ld.setReqTime(new Date());
				ld.setOrderId(ordernum);
				ld.setReqTime(new Date());

				loanManageService.saveLiquidation(ld);

				try {
					String registerXml = ParseXML.accttrans(at);
					ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
					nvps.add(new BasicNameValuePair("requestParams",
							registerXml));
					nvps.add(new BasicNameValuePair("sign", CommonUtil
							.MD5(registerXml + "~|~"
									+ ParameterIps.getmerchantKey())));
					payLogService.savePayLog(registerXml, ld
							.getUserbasicsinfo().getId(),
							Long.parseLong(lonid), 14, ordernum, ld.getFee()
									.doubleValue(), ld.getFee().doubleValue(),
							ld.getAmount().doubleValue());
					String result = CommonUtil.excuteRequest(
							PayURL.TRANSFERURL, nvps);
					result = result.replace("\"", "\'");
					crs cr = new crs();
					XStream xss = new XStream(new DomDriver());
					xss.alias(cr.getClass().getSimpleName(), cr.getClass());
					cr = (crs) xss.fromXML(result);
					String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~"
							+ cr.getMsg() + "~|~"
							+ ParameterIps.getDes_algorithm());
					if (cr.getSign().equals(Md5sign)) {
						if (cr.getCode().equals("CSD000")) {
							// 校验是否成功
							if (liquidationState(ordernum, ld, lonid)) {
								ld.setLiquidationState(1);
								loanManageService.saveLiquidation(ld);
								// 更新收款人
								Userbasicsinfo userinfo2 = ld
										.getUserbasicsinfo();
								crs cr2 = baoFuService.getCasbalance(ld
										.getUserbasicsinfo().getpMerBillNo());

								// 添加流水
								Accountinfo account = new Accountinfo();
								account.setExpenditure(0.00);
								account.setExplan("项目清盘");
								account.setIncome(ld.getAmount().doubleValue());
								account.setIpsNumber(ld.getUserbasicsinfo()
										.getpMerBillNo());
								account.setLoansignId(ld.getLoansign().getId()
										.toString());// 标id（项目id）
								account.setTime(DateUtils
										.format("yyyy-MM-dd HH:mm:ss"));
								account.setUserbasicsinfo(ld
										.getUserbasicsinfo());
								account.setAccounttype(plankService
										.accounttype(12L));
								account.setMoney(cr2.getBalance());
								userinfo2.getUserfundinfo().setCashBalance(
										cr2.getBalance());
								userinfo2.getUserfundinfo().setOperationMoney(
										cr.getBalance());
								plankService.saveAccount(account);// 添加流水账余额
								userbasicsinfoService.update(userinfo2);
								System.out.println("成功");
							} else {
								System.out.println("失败");
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// 校验本期 是否全部清盘，更新标的信息，更新还款状态

			// 检查是有未完的条数
			double notCount = loanManageService
					.getNotLiquidationCountByLoanid(lonid);
			if (notCount == 0) {// 没有剩余清盘条数
				try {
					// 更新标表 状态 -1
					// 更新还款表 状态-1
					loanManageService
							.updateLoansignAndRepaymentrecordByLoanid(lonid);
					return "1";
				} catch (Exception e) {
					return "0";
				}
			}
		} else {
			return "-1";
		}
		return "0";

	}

	/**
	 * 打开后台清盘页面
	 * 
	 * @return
	 */
	@RequestMapping(value = { "openLiquidation" })
	public ModelAndView openLiquidation(HttpServletRequest request) {
		ModelAndView returnModelAndView = new ModelAndView(
				"WEB-INF/views/admin/loansign/liquidation");
		return returnModelAndView;

	}

	/**
	 * 清盘金额窗口
	 * 
	 * @param chattel
	 * @return
	 */
	@RequestMapping("inputLiquidationMoney")
	public ModelAndView add(String loanid, HttpServletRequest request,
			String esid, String periods) {
		// 得到优先还款数据；
		Loansign loanSign = loanSignQuery.getLoansignById(loanid);
		Double sumMoney = Arith.add(loanSign.getPriority(),
				loanSign.getMiddle());
		List<Repaymentrecord> repaymentrecord = loanSignService
				.getFinallyPeriodsRepaymentrecord(loanid, periods);
		sumMoney = Arith.add(sumMoney,
				repaymentrecord.get(0).getPreRepayMoney()); // 计算清盘优先本息+夹层本金
		ModelAndView returnModelAndView = new ModelAndView(
				"WEB-INF/views/admin/loansign/inputLiquidationMoney");
		List<Escrow> es = escrowService.getAllEscrow();
		request.getSession().setAttribute("lonid", loanid);
		request.getSession().setAttribute("defesid", esid);// 标默认第三方担保信息
		request.getSession().setAttribute("es", es);// 所有担保信息
		request.getSession().setAttribute("periodsOne", periods);// 所有担保信息
		request.getSession().setAttribute("sumMoney", sumMoney);
		return returnModelAndView;

	}

	/**
	 * 找到清盘数据
	 * 
	 * @param page
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("loansignLiquidation")
	public JSONObject loansignLiquidation(String limit, String start,
			Loansign loan, String loanType, HttpServletRequest request,
			PageModel page) {

		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();
		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 10);
		} else {
			page.setNumPerPage(10);
		}
		Costratio costratio = loanSignQuery.queryCostratio();
		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		// 分页数据源
		List datalist = loanSignService.getLoansignLiquidation(page, loan,
				loanType, costratio.getWindingNum());
		String titles = "id,name,issueLoan,priority,middle,after,loanUnit,publishTime,creditTime,refunway,esid,status,periods,realRate";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;

	}

	/**
	 * 清盘数据还款详情
	 * 
	 * @param limit
	 * @param start
	 * @param loan
	 * @param loanType
	 * @param request
	 * @param page
	 * @return
	 */
	@RequestMapping("loansignLiquidationDetails")
	@ResponseBody
	public JSONObject loansignLiquidationDetails(String limit, String start,
			String loanid, String loanType, HttpServletRequest request,
			PageModel page) {
		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();
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
		List datalist = loanSignService.getLoansignLiquidationDetails(page,
				loanid);
		String titles = "id,payeeuserid,payeruserid,orderid,amount,fee,reqtime,loanid,liquidationstate";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;

	}

	/************** 项目发布开始 **************/

	/**
	 * @param id
	 * @param operNumber
	 * @param request
	 * @return
	 */
	@RequestMapping("/seeDetails")
	public ModelAndView seeDetails(
			@RequestParam(value = "id", defaultValue = "", required = false) String id,
			int operNumber, HttpServletRequest request) {

		if (null != id && !id.trim().equals("") && operNumber != 1) {
			// 通过id查询到信息
			Loansign loansign = loanSignQuery.getLoansignById(id);
			Loansignbasics loansignbasics = loanSignQuery.getLoansignbasicsById(id);
			MsgReminder customerReminder = new MsgReminder();
			List<MsgReminder> customerReminderList = loanSignQuery.queryMsgReminderListByLoanSignId(loansign.getId(), 1);
			if(customerReminderList != null && customerReminderList.size() > 0) {
				customerReminder = customerReminderList.get(0);
			}
			List<MsgReminder> employeeReminderList = loanSignQuery.queryMsgReminderListByLoanSignId(loansign.getId(), 2);
			List<Escrow> escrowLlist = escrowService.getAllEscrow();
			List<LoansignType> loansignType = loanSignQuery.queryLoanType();
			List<Industry> Inlist = loanInfoService.getIndustryList();
			List<Province> list = loanInfoService.getProvinceList();
			request.setAttribute("customerReminder", customerReminder);
			request.setAttribute("employeeReminderList", employeeReminderList);
			request.setAttribute("proList", list);
			request.setAttribute("ecl", escrowLlist);
			request.setAttribute("Inlist", Inlist);
			request.setAttribute("loansignType", loansignType);
			request.setAttribute("loansign", loansign);
			request.setAttribute("loansignbasics", loansignbasics);
			
			String key = "STR:LOANSIGN:COPY:NAME:" + id;
			String orginalLoansignName = RedisHelper.get(key);
			request.setAttribute("orginalLoansignName", orginalLoansignName);//复制原始标所使用的标题
		} else {
			List<Escrow> escrowLlist = escrowService.getAllEscrow();
			List<LoansignType> loansignType = loanSignQuery.queryLoanType();
			request.setAttribute("ecl", escrowLlist);
			List<Industry> Inlist = loanInfoService.getIndustryList();
			List<Province> list = loanInfoService.getProvinceList();
			request.setAttribute("proList", list);
			request.setAttribute("Inlist", Inlist);
			request.setAttribute("loansignType", loansignType);
			request.setAttribute("loansign", new Loansign());
			request.setAttribute("loansignbasics", new Loansignbasics());
		}
		// 查询标类型
		request.setAttribute("loanType", loansignTypeService.queryLoanType());
		request.setAttribute("operNumber", operNumber);
		if (operNumber == 2 || operNumber == 3) {
			ModelAndView returnModelAndView = new ModelAndView(
					"WEB-INF/views/admin/loansign/editloansign");
			return returnModelAndView;
		} else {
			ModelAndView returnModelAndView = new ModelAndView(
					"WEB-INF/views/admin/loansign/saveloansign");
			return returnModelAndView;
		}

	}

	/**
	 * 众持发布
	 * 
	 * @param request
	 * @param loan
	 * @param loansignbasics
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveLoansign")
	public JSONObject saveLoansign(Loansign loanSign,
			Loansignbasics loansignbasics, Userbasicsinfo user, Integer type, String employeeReminderInfo,
			HttpServletRequest request) {
		Userbasicsinfo u = dao.get(Userbasicsinfo.class, loanSign.getUserbasicsinfo().getId());
		JSONObject json = new JSONObject();
		loanSign.setUserbasicsinfo(u);
		loanSign.setPrioRestMoney(loanSign.getPriority());
		loanSign.setMidRestMoney(loanSign.getMiddle());
		loanSign.setAfterRestMoney(loanSign.getAfter());
		loanSign.setPrioRate(loanSign.getPrioRate() / 100);
		loanSign.setAfterRate(loanSign.getAfterRate() / 100);
		loanSign.setPrioAwordRate(loanSign.getPrioAwordRate() / 100);
		loanSign.setLoanPeriods(loanSign.getLoanPeriods().toUpperCase().trim());
		loanSign.setState(1);
		loanSign.setStatus(0);
		loanSign.setRestMoney(loanSign.getIssueLoan());
		loanSign.setIsdet(0);
		loanSign.setRecommend(0);
		loanSign.setOnIndex(0);
		loanSign.setFeeState(2); // 未收取平台手续费
		loanSign.setCreateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		loanSign.setActivityStatus(loanSign.getActivityStatus());
		boolean bool;
		if (loanSign.getId() != null) {
			if (loanSign.getType() == 3) {
				loanSign.setRefunway(3);
			}
			bool = baseLoansignService.update(loanSign, loansignbasics);
		} else {
			loanSign.setCus_id(StringUtil.getCurId(2)); // 根据类型生成宝付Id
			if (loanSign.getType() == 3) {
				loanSign.setRefunway(3);
			}
			bool = baseLoansignService.save(loanSign, loansignbasics);
		}
		
		List<MsgReminder> reminderList = JSON.parseArray(employeeReminderInfo, MsgReminder.class);
		baseLoansignService.addMsgReminder(reminderList, loanSign.getId());

		if (bool) {
			// dwz返回json对象
			json.element("statusCode", "200");
			json.element("message", "更新成功");
			json.element("navTabId", "main45");
			json.element("callbackType", "closeCurrent");
			return json;
		} else {
			json.element("message", "更新失败");
			return json;
		}
	}

	/************** 项目发布结束 **************/
	/**
	 * 省市联动
	 * 
	 * @param request
	 * @param pId
	 * @return
	 */
	@RequestMapping("getProCityLists")
	public String getProCityList(HttpServletRequest request, String pId,
			HttpServletResponse response) {
		List list = loanInfoService.getProvinceList2(pId);
		StringBuffer sb = new StringBuffer();
		try {
			response.setCharacterEncoding("UTF-8");
			if (list.size() > 0) {
				sb.append("<option value='-1'>" + "请选择" + "</option>");
			} else {
				sb.append("<option value='-1'>" + "请选择" + "</option>");
			}
			for (int i = 0; i < list.size(); i++) {
				City ci = (City) list.get(i);
				sb.append("<option  value='" + ci.getName() + "'>"
						+ ci.getName() + "</option>");
			}
			response.setContentType("text/html");
			response.getWriter().print(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping("onIndexManage")
	public ModelAndView onIndexManage() {
		ModelAndView returnModelAndView = new ModelAndView(
				"WEB-INF/views/admin/loansign/onIndex");
		return returnModelAndView;
	} 
	
	/***
	 * 热门推荐
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("toHotRecommend")
	public String toHotRecommend(HttpServletRequest request, String id) {
		if(loanSignQuery.hasRecommend()){
			Loansign loan = null;
			loan = loanSignQuery.getLoansignById(id);
			loan.setRecommend(1);
			projectService.updateLoansign(loan, "1");
			cacheManagerService.hotIntroduceLoanList();
			cacheManagerService.updateIndexLoanList();			
			cacheManagerService.updateH5HotIntroduceLoanList();	
			return "2";
		}else{
			return "1";
		}
	}
	/***
	 * 取消热门推荐
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("toNotHotRecommend")
	public String toNotHotRecommend(HttpServletRequest request, String id) {
		Loansign loan = null;
		loan = loanSignQuery.getLoansignById(id);
		loan.setRecommend(0);
		projectService.updateLoansign(loan, "1");
		cacheManagerService.hotIntroduceLoanList();
		cacheManagerService.updateIndexLoanList();
		cacheManagerService.updateH5HotIntroduceLoanList();	
		return "2";
	}
	
	/**
	 * 首页推荐
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getOnIndex")
	public String getOnIndex(HttpServletRequest request, String id) {
		if(loanSignQuery.hasOnIndex()){
			Loansign loan = null;
			loan = loanSignQuery.getLoansignById(id);
			loan.setOnIndex(1);
			projectService.updateLoansign(loan, "1");
			cacheManagerService.updateIndexLoanList();			
			return "2";
		}else{
			return "1";
		}
	}

	/***
	 * 取消首页推荐
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getOnIndexNot")
	public String getOnIndexNot(HttpServletRequest request, String id) {
		Loansign loan = null;
		loan = loanSignQuery.getLoansignById(id);
		loan.setOnIndex(0);
		projectService.updateLoansign(loan, "0");
		cacheManagerService.updateIndexLoanList();	
		return "2"; // 1 推荐
	}
	
	@ResponseBody
	@RequestMapping("allLoanlistOnIndex")
	public JSONObject allLoanlistOnIndex(String limit, String start,
			Loansign loansign, HttpServletRequest request, PageModel page) {

		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();
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
		List datalist = loanSignService.loanSignPageOnIndexRecommend(page, loansign);
		// JSONArray jsonlist = loanSignService.queryJSONByList(list);

		String titles = "id,prate,yearate,issueLoan,priority,sandwich,afterBad,prioRestMoney,midRestMoney,afterRestMoney,loanUnit,xname,proindustry,uname,loanstate,haveOther,releaseTime,getMoneyWay,remark,status,recommend,remoney,remonth,type,midRate,afterRate,realRate,onIndex";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}
	
	
	@ResponseBody
	@RequestMapping("allLoanlistOnIndexNot")
	public JSONObject allLoanlistOnIndexNot(String limit, String start,
			Loansign loansign, HttpServletRequest request, PageModel page) {

		JSONObject resultjson = new JSONObject();
		JSONArray jsonlist = new JSONArray();
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
		List datalist = loanSignService.loanSignPageOnIndexNot(page, loansign);
		// JSONArray jsonlist = loanSignService.queryJSONByList(list);

		String titles = "id,prate,yearate,issueLoan,priority,sandwich,afterBad,prioRestMoney,midRestMoney,afterRestMoney,loanUnit,xname,proindustry,uname,loanstate,haveOther,releaseTime,getMoneyWay,remark,status,recommend,remoney,remonth,type,midRate,afterRate,realRate,onIndex";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}
	
	/** 项目复制 */
	@RequestMapping("/copyLoansign")
	public String copyLoansign(String id, HttpServletRequest request) {
		loanSignQuery.copyLoansingById(id, request);
		List<Escrow> escrowLlist = escrowService.getAllEscrow();
		List<LoansignType> loansignType = loanSignQuery.queryLoanType();
		List<Industry> Inlist = loanInfoService.getIndustryList();
		List<Province> list = loanInfoService.getProvinceList();
		
		request.setAttribute("proList", list);
		request.setAttribute("ecl", escrowLlist);
		request.setAttribute("Inlist", Inlist);
		request.setAttribute("loansignType", loansignType);
		
		// 查询标类型
		request.setAttribute("loanType", loansignTypeService.queryLoanType());
		request.setAttribute("operNumber", 2);
		return "WEB-INF/views/admin/loansign/editloansign";
	}
	
	/** 众持项目管理融资中项目的列表页增加“通知会员”的功能 */
	@RequestMapping("/newLoansignNotifyMember")
	public String newLoansignNotifyMember(String loansignId, HttpServletRequest request) {
		request.setAttribute("loansignId", loansignId);
		return "/WEB-INF/views/admin/loansign/newLoansignMemberNotify";
	}
	
	/** 新表确认通知会员
	 * :邮件由于目前每天最多只能发送3000封，暂时不支持*/
	@ResponseBody
	@RequestMapping("/doNewLoansignMemberNotify")
	public String doNewLoansignMemberNotify(String loansignId, String msgType) {
		String key = "STR:HC9:NEW:LOAN:NOTIFY:Flag:" + loansignId;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String code = "-1";
		String msg = "操作失败!";
		if(!RedisHelper.isKeyExistSetWithExpire(key, 60)) {
			try{
				String status = SmsEmailCache.getSmsEmailSwitchStatus("new_loansign_online_sms");
				if("1".equals(status)) {
					Loansign loansign = smsEmailTimerDao.queryLoansignById(loansignId);
					if(loansign != null) {
						if(loansign.getStatus().intValue() != 1) {
							msg = "当前项目的状态";
						} else {
							if(loansign.getRestMoney() > 0) {
								/** 如果当前项目有可用于投资的金额且项目状态为1则开始发送短信 */
								if(smsSendService.sendNewLoansignSmsNotify(loansign)) {
									code = "0";
									msg = "短信通知成功！";
								} else {
									msg = "短信通知发送失败！";
								}
							} else {
								msg = "当前项目可投标金额为零！";
							}
						}
					} else {
						msg = "没查到相关项目记录！";
					}
					msg = "短信通知已经开始发送！";
				} else {
					msg = "新标上线短信提醒开关未开启，不能发送短信提醒！";
				}
			} catch(Exception e) {
				msg = "后台出现异常，短信通知失败！";
				LOG.error("loansignId:" + loansignId + ",msgType:" + msgType + "发送通知出错！", e);
			}
			LOG.error("loansignId:" + loansignId + ",msgType:" + msgType + ",code:" + code + ",msg:" + msg);
		} else {
			msg = "同一个标一分钟内不能频繁操作'通知会员'按钮！";
		}
		resultMap.put("code", code);
		resultMap.put("msg", msg);
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	
	/***
	 * 申请参与活动
	 * @param id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/activityLoansign")
	public String activityLoansign(String id, HttpServletRequest request) {
		Loansign loansign = loanSignQuery.getLoansignById(id);
		// 判断是否满标
		if (loansign.getStatus()!= 1||loansign.getActivityStatus()!=0) {
			return "2";
		}
		String num = loanSignService.activityLoansign(id, request);
		cacheManagerService.updateIndexLoanList();//首页列表
		cacheManagerService.updateLoanDetailRelCache(id);//投资更新标详情信息
		cacheManagerService.updateZhongChiPageLoanList();//更新我要众持列表页面
		cacheManagerService.updateH5ZhongChiPageLoanList();//更新H5我要众持列表页面
		cacheManagerService.updateH5HotIntroduceLoanList();//H5热门推荐列表相关缓存更新
		return num;
	}
	
	@RequestMapping("central.htm")
	public String callcentral(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) request.getSession()
				.getAttribute("map");
		request.getSession().removeAttribute("map");
		request.setAttribute("map", map);
		return "WEB-INF/views/hc9/member/trade/central_news";
	}
}