package com.hc9.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.util.Arith;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.dao.entity.LoansignType;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.AttachmentService;
import com.hc9.service.BaseLoansignService;
import com.hc9.service.CommentService;
import com.hc9.service.EmailService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.LoanrecordService;
import com.hc9.service.LoansignTypeService;
import com.hc9.service.ProcessingService;
import com.hc9.service.RepaymentrecordService;
import com.hc9.service.ShopService;
import com.hc9.service.UserInfoServices;
import com.hc9.service.UserbasicsinfoService;
import com.hc9.service.sms.ym.BaseSmsService;

/**
 * 通用标Controller
 * 
 * @author longyang
 * 
 */
@Controller
@RequestMapping("/baseLoanSign")
@CheckLogin(value=CheckLogin.ADMIN)
public class BaseLoanSignController {

	/** baseLoansignService 通用services */
	@Resource
	private BaseLoansignService baseLoansignService;

	/** loanSignQuery 借款标查询 */
	@Autowired
	private LoanSignQuery loanSignQuery;

	/** rechargeModel 导出实体 */
	@Resource
	private RechargeModel rechargeModel;

	/** loanrecordService 认购记录services */
	@Resource
	private LoanrecordService loanrecordService;

	/** repaymentrecordService 还款记录 */
	@Resource
	private RepaymentrecordService repaymentrecordService;

	/** attachmentService 借款标附件 */
	@Resource
	private AttachmentService attachmentService;

	/** commentService 评论Service */
	@Resource
	private CommentService commentService;

	@Resource
	private ProcessingService processingService;

	@Resource
	private UserInfoServices infoServices;

	@Resource
	private BaseSmsService baseSmsService;

	@Resource
	private EmailService emailService;

	@Resource
	private LoansignTypeService loansignTypeService;

	@Resource
	private ShopService projectService;

	@Resource
	private UserbasicsinfoService userbasicsinfoService;

	@Resource
	private ProcessingService processingservice;

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
	@SuppressWarnings("rawtypes")
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

	/**
	 * <p>
	 * Title: loanrecordAssigmentList
	 * </p>
	 * <p>
	 * Description: 债权转让认购记录列表
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
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping(value = { "assigmentrecordList", "/" })
	public JSONObject assigmentrecordList(
			@RequestParam(value = "start", defaultValue = "0", required = false) int start,
			@RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
			@RequestParam(value = "id", defaultValue = "", required = false) int id,
			HttpServletRequest request) {

		// 得到总条数
		Object count = loanrecordService.getAssignmentCount(id);
		// 分页数据源
		List list = loanrecordService.queryAssignmentList(start, limit, id, 1);
		JSONArray jsonlist = loanrecordService.getJSONArrayByList(list);

		JSONObject resultjson = new JSONObject();
		// 将数据源封装为json对象（命名必须rows）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", count);
		return resultjson;
	}

	/**
	 * <p>
	 * Title: repaymentRecordList
	 * </p>
	 * <p>
	 * Description: 还款记录列表
	 * </p>
	 * 
	 * @param start
	 *            开始
	 * @param limit
	 *            结束
	 * @param id
	 *            借款标编号
	 * @param request
	 *            请求
	 * @return 集合对象
	 */
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping(value = { "repaymentRecordList", "/" })
	public JSONObject repaymentRecordList(
			@RequestParam(value = "start", defaultValue = "0", required = false) int start,
			@RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
			@RequestParam(value = "id", defaultValue = "", required = false) int id,
			HttpServletRequest request) {

		// 得到总条数
		Object count = repaymentrecordService.getrepaymentRecordCount(id);
		// 分页数据源
		List list = repaymentrecordService.queryRepaymentrecordList(start,
				limit, id);
		JSONArray jsonlist = repaymentrecordService.getJSONArrayByList(list);

		JSONObject resultjson = new JSONObject();
		// 将数据源封装为json对象（命名必须rows）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", count);
		return resultjson;
	}

	/***
	 * 根据ID查询repaymentrecordparticulars
	 * 
	 * @param start
	 * @param limit
	 * @param id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "recordparticularsList", "/" })
	public JSONObject recordparticularsList(
			@RequestParam(value = "start", defaultValue = "0", required = false) int start,
			@RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
			@RequestParam(value = "id", defaultValue = "", required = false) int id,
			HttpServletRequest request) {

		// 得到总条数
		Object count = repaymentrecordService.getRepaymentrecordparticulars(id);
		// 分页数据源
		List list = repaymentrecordService.queryRecordparticularsList(start,
				limit, id);
		JSONArray jsonlist = repaymentrecordService
				.getJSONArrayByRecordList(list);

		JSONObject resultjson = new JSONObject();
		// 将数据源封装为json对象（命名必须rows）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", count);
		return resultjson;
	}

	/**
	 * <p>
	 * Title: AttachmentList
	 * </p>
	 * <p>
	 * Description: 附件信息列表
	 * </p>
	 * 
	 * @param start
	 *            开始
	 * @param limit
	 *            结束
	 * @param id
	 *            借款标编号
	 * @param request
	 *            请求
	 * @return 列表对象
	 */
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping(value = { "attachmentList", "/" })
	public JSONObject attachmentList(
			@RequestParam(value = "start", defaultValue = "0", required = false) int start,
			@RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
			@RequestParam(value = "id", defaultValue = "", required = false) int id,
			HttpServletRequest request) {

		JSONArray jsonlist = new JSONArray();

		// 得到总条数
		Object count = attachmentService.getAttachmentCount(id);
		// 分页数据源
		List list = attachmentService.queryAttachmentList(start, limit, id);
		String titles = "id,originalName,attachmentName,attachmentType,uploadTime,realname";
		ArrayToJson.arrayToJson(titles, list, jsonlist);

		JSONObject resultjson = new JSONObject();
		// 将数据源封装为json对象（命名必须rows）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", count);
		return resultjson;
	}

	/**
	 * <p>
	 * Title: CommentList
	 * </p>
	 * <p>
	 * Description: 评论列表
	 * </p>
	 * 
	 * @param start
	 *            开始
	 * @param limit
	 *            结束
	 * @param id
	 *            借款标编号
	 * @param request
	 *            请求
	 * @return 列表对象
	 */
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping(value = { "commentList", "/" })
	public JSONObject commentList(
			@RequestParam(value = "start", defaultValue = "0", required = false) int start,
			@RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
			@RequestParam(value = "id", defaultValue = "", required = false) int id,
			HttpServletRequest request) {

		JSONArray jsonlist = new JSONArray();
		// 得到总条数
		Object count = commentService.getCommentCount(id);
		// 分页数据源
		List list = commentService.queryCommentList(start, limit, id);
		String titles = "id,cmtcontent,name,cmtReply,cmtIsShow";
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		JSONObject resultjson = new JSONObject();
		// 将数据源封装为json对象（命名必须rows）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", count);
		return resultjson;
	}

	/**
	 * <p>
	 * Title: outPutLoanrecordExcel
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
	@SuppressWarnings("rawtypes")
	@RequestMapping("outPutLoanrecordExcel")
	public void outPutLoanrecordExcel(int id, HttpServletRequest request,
			HttpServletResponse response) {

		// 标题
		String[] header = new String[] { "项目名称", "用户名", "购买金额", "购买时间", "购买状态",
				"特权会员", "投资类型", "管理费" };
		/*
		 * // 行宽度 Integer[] column = new Integer[] { 12, 10, 12, 12, 8, 8, 8, 10
		 * };
		 */

		// 获取数据源
		List list = loanrecordService.queryLoanrecordList(0, 0, id, 2);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("项目名称", str[0] + "");
			map.put("用户名", str[1] + "");
			map.put("购买金额", Arith.round(new BigDecimal(str[2].toString()), 2)
					+ "元");
			map.put("购买时间", str[3] + "");
			map.put("购买状态", str[4] + "");
			map.put("特权会员", str[5] + "");
			map.put("投资类型", str[6] + "");
			map.put("管理费", str[7] + "");
			content.add(map);

		}
		// 下载excel
		rechargeModel.downloadExcel("出借记录", null, header, content, response,
				request);
	}

	/**
	 * @RequestMapping("gotoIPS.htm") public String publishToIps() { return
	 *                                "WEB-INF/views/regSub_news"; }
	 * 
	 * 
	 * 
	 *                                /** 即将到期的标
	 * 
	 * @param page
	 *            分页
	 * @param loanType
	 *            借款标类型
	 * @param request
	 *            请求
	 * @return 页面
	 */
	@RequestMapping("toLoanSignExpiring")
	public ModelAndView toLoanSignExpiring(PageModel page, int loanType,
			HttpServletRequest request) {
		request.setAttribute("loanSignExpir",
				baseLoansignService.findExpirLoanSign(page, loanType));
		request.setAttribute("loanType", loanType);
		request.setAttribute("page", page);
		return new ModelAndView("WEB-INF/views/admin/loansign/loansignexpiring");
	}

	/**
	 * 初始化发送信息页面
	 * 
	 * @return 返回发送信息页面
	 */
	@RequestMapping("openMessage")
	public String openMessage(Long loanId, HttpServletRequest request) {
		request.setAttribute("loan", baseLoansignService.get(loanId));
		return "WEB-INF/views/admin/loansign/add_remind";
	}

	/**
	 * 发送短信或邮件
	 * 
	 * @param fashion
	 *            发送方式0 表示发送短信 1表示发送邮件
	 * @param content
	 *            发送内容
	 * @return 发送是否成功
	 */
	@RequestMapping("sendSms.htm")
	@ResponseBody
	public JSONObject sendChatMessage(int fashion, String content,
			String phone, String email) {
		JSONObject json = new JSONObject();
		// 发送短信
		try {
			if (fashion == Constant.STATUES_ZERO) {
				int sms = baseSmsService.sendSMS(content, phone);
				if (sms==0) {
					return DwzResponseUtil.setJson(json, "200", "短信发送成功", null,
							"closeCurrent");
				} else {
					return DwzResponseUtil.setJson(json, "300", "短信发送失败", null,
							"closeCurrent");
				}
			} else {
				emailService.sendEmail("前海红筹标到期提醒", content, email);
				return DwzResponseUtil.setJson(json, "200", "邮件发送成功", null,
						"closeCurrent");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return DwzResponseUtil.setJson(json, "300", "发送失败", null,
					"closeCurrent");
		}
	}

	/**
	 * 获取标类型的数据
	 * 
	 * @return 返回一个json数组
	 */
	@RequestMapping("loanType.htm")
	@ResponseBody
	public JSONArray getLoanType() {
		JSONArray json = new JSONArray();
		List<LoansignType> listType = loansignTypeService.queryLoanType();
		for (int i = 0; i < listType.size(); i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("key", listType.get(i).getTypeKey());
			jsonObject.accumulate("value", listType.get(i).getTypeName());
			json.add(jsonObject);
		}
		return json;
	}

	/**
	 * 查询借款人带回
	 * 
	 * @param username
	 * @param cardno
	 * @param page
	 * @param conditions
	 * @param request
	 * @return
	 */
	@RequestMapping("borrowersbaseLists")
	public String queryBorrowersbaseLists(
			@RequestParam(value = "username", defaultValue = "", required = false) String username,
			@RequestParam(value = "cardno", defaultValue = "", required = false) String cardno,
			PageModel page, String conditions, HttpServletRequest request) {
		// 查询借款人条件
		Object count = baseLoansignService.queryBorrowersbasecounts(username,
				cardno);
		page.setTotalCount(Integer.parseInt(count.toString()));
		// 分页查询所有借款人
		Object obj = baseLoansignService.queryBorrowersbaseLists(page,
				username, cardno);
		request.setAttribute("list", obj);
		request.setAttribute("page", page);
		request.getSession().setAttribute("username", username);
		request.setAttribute("cardno", cardno);
		return "WEB-INF/views/admin/loansign/borrowerlists";

	}

}
