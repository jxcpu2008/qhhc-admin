package com.hc9.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.Arith;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.BorrowersApply;
import com.hc9.dao.entity.Borrowersbase;
import com.hc9.dao.entity.Generalize;
import com.hc9.dao.entity.Loansignflow;
import com.hc9.dao.entity.Manualintegral;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Userrelationinfo;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.model.UnnormalUserInfo;
import com.hc9.service.AssignmentService;
import com.hc9.service.AutointegralQuery;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.BorrowService;
import com.hc9.service.BorrowersApplyService;
import com.hc9.service.IntegralService;
import com.hc9.service.LoanrecordService;
import com.hc9.service.MessagesettingService;
import com.hc9.service.PayLogService;
import com.hc9.service.RegistrationService;
import com.hc9.service.UserInfoServices;
import com.hc9.service.UserbasicsinfoService;
import com.hc9.service.VipInfoService;

/**
 * <p>
 * Title:UserInfoController
 * </p>
 * <p>
 * Description: 会员/借款人控制层
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author LiNing date 2014年1月24日
 */
@Controller
@RequestMapping("/userinfo")
@SuppressWarnings("rawtypes")
@CheckLogin(value = CheckLogin.ADMIN)
public class UserInfoController {

	/** 引入log4j日志打印类 */
	private static final Logger LOGGER = Logger.getLogger(UserInfoController.class);

	/** 注入会员服务层 */
	@Resource
	private UserInfoServices userInfoServices;

	/** 注入excel文件生成工具 */
	@Resource
	private RechargeModel modelRecharge;

	@Resource
	private AutointegralQuery autointegralQuery;

	@Resource
	private BorrowService borrowService;

	/** vipinfoservice 特权会员 */
	@Resource
	private VipInfoService vipinfoservice;

	/** integralService 借款人积分 */
	@Resource
	private IntegralService integralService;

	@Resource
	private LoanrecordService loanrecordService;

	/** borrowersApplyService 借款申请 */
	@Resource
	private BorrowersApplyService borrowersApplyService;

	@Resource
	private AssignmentService assginService;

	@Resource
	private PayLogService payLogService;

	@Resource
	private UserbasicsinfoService userbasicsinfoService;

	List<NameValuePair> nvps;

	/**
	 * 用户注册接口
	 */
	@Resource
	private RegistrationService registrationService;

	@Resource
	private MessagesettingService messagesettingService;

	/** 操作执行成功后要刷新的页面 */
	private String pageId = "main26";

	/** 操作执行成功后要刷新的页面 借款人管理 */
	private String borrowpageId = "main37";

	/** 操作执行成功后要刷新的页面 债权转让 */
	private String assignmentId = "main59";

	@Autowired
	private BaoFuLoansignService baoFuLoansignService;
	
	@ResponseBody
	@RequestMapping("/getUnnormalUserList")
	public String getUnnormalUserList(String start, String limit, String phone, PageModel page) {
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
		
		List<UnnormalUserInfo> userList = userInfoServices.queryUnnormalUserInfosByPage(phone, page);
		resultMap.put("rows", userList);
		resultMap.put("total", page.getTotalCount());
		return JsonUtil.toJsonStr(resultMap);
	}
	
	@ResponseBody
	@RequestMapping("/ipsUserInfo")
	public String ipsUserInfo(String userIds, String billNos) {
		LOGGER.info("userIds = " + userIds);
		LOGGER.info("billNos = " + billNos);
		
		String result = "7";
		String adminIdConcurrentLock = "STR:IPSUSERINFO:ADMINUSER:CONCURRENT:LOCK";
		if (!RedisHelper.isKeyExist(adminIdConcurrentLock)) {
			RedisHelper.setWithExpireTime(adminIdConcurrentLock, adminIdConcurrentLock, 60);
			result = baoFuLoansignService.ipsUserInfo(userIds, billNos);
			RedisHelper.del(adminIdConcurrentLock);
		}
		return result;
   }

	/**
	 * <p>
	 * Title: queryPage
	 * </p>
	 * <p>
	 * Description: 分页查询会员信息
	 * </p>
	 * 
	 * @param userinfo
	 *            查询条件
	 * @param page
	 *            分页对象
	 * @param request
	 *            HttpServletRequest
	 * @param limit
	 *            每页查询条数
	 * @param start
	 *            从第几行开始查询
	 * @param isbrow
	 *            是否是借款人
	 * @return 查询结果转换后的json对象
	 */
	@ResponseBody
	@RequestMapping("/querypage")
	public JSONObject queryPage(Userbasicsinfo userinfo, PageModel page,
			HttpServletRequest request, String limit, String start,
			String isbrow, String isloan, String isVip) {

		if (StringUtil.isNotBlank(isbrow) && StringUtil.isNumberString(isbrow)) {
			userinfo.setLockTime(isbrow);
		}
		if (StringUtil.isNotBlank(isloan) && StringUtil.isNumberString(isloan)) {
			userinfo.setLoginTime(isloan);
		}
		JSONObject resultjson = new JSONObject();

		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		List datalist = userInfoServices.queryUserPage(page, userinfo);
		// endtime,vipendtime,isborrower,备用
		String titles = "id,username,realname,phone,userintegral,credit,createTime,lasttime,lastaddress,logincount,vipendtime,isborr,isLock,adminname,hasIpsAccount,isAuthIps,orderSn,isorgperson,iscrowdfundingperson,iscrowdhold,pMerBillNo,user_type,staff_no,genName,genUserName,earnings,loanmoney,department,cashBalance";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryPage(Userbasicsinfo, PageModel, HttpServletRequest, String)方法结束OUTPARAM=" + resultjson); //$NON-NLS-1$
		}
		return resultjson;

	}
	
	/**
	 * 机构投资申请列表
	 * 
	 * @param page
	 * @param request
	 * @param limit
	 * @param start
	 * @param isbrow
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/insinveplease")
	public JSONObject insinveplease(PageModel page, Userrelationinfo userla,
			HttpServletRequest request, String limit, String start,
			String isbrow) {
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

		List datalist = userInfoServices.queryinsinveplease(page, userla);
		// endtime,vipendtime,isborrower,备用
		String titles = "id,username,realname,cardId,mechofattachment1,mechofattachment2,mechofattachment3,mechofattachment4,certSubtime,manacerttime,audit,adminame";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());

		return resultjson;

	}

	/**
	 * 身份证认证
	 * 
	 * @param page
	 * @param request
	 * @param limit
	 * @param start
	 * @param isbrow
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/identityauth")
	public JSONObject identityauth(PageModel page, Userbasicsinfo user,
			HttpServletRequest request, String limit, String start,
			String isbrow) {
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

		List<Object> datalist = userInfoServices.querycardidentity(page, user);
		// endtime,vipendtime,isborrower,备用
		String titles = "id,username,realname,cardId,cardone,cardtwo,userSubtimer,manatoverifyuser,cardStatus,adminame";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());
		return resultjson;

	}

	/**
	 * <p>
	 * Title: queryAssignmentPage
	 * </p>
	 * <p>
	 * Description: 查询债权转让数据
	 * </p>
	 * 
	 * @param userinfo
	 *            搜索条件
	 * @param page
	 *            分页参数
	 * @param request
	 *            HttpServletRequest
	 * @param limit
	 *            每页显示条数
	 * @param start
	 *            从几条数据开始查询
	 * @return 查询结果转换的json对象
	 */
	@ResponseBody
	@RequestMapping("/queryAssignmentPage")
	public JSONObject queryAssignmentPage(Userbasicsinfo userinfo,
			PageModel page, HttpServletRequest request, String limit,
			String start) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryAssignmentPage(Userbasicsinfo userinfo=" + userinfo + ", PageModel page=" + page + ", HttpServletRequest request=" + request + ", String limit=" + limit + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}
		JSONObject resultjson = new JSONObject();

		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : page.getNumPerPage());
		}

		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		List datalist = userInfoServices.queryPage(page, userinfo);

		// json对象的键
		String titles = "id,flowid,username,name,loanTitle,loanNumber,loanState,auditStatus,auditResult,tenderMoney,userbasicinfo_id,loan_id";

		// 数组集合转成josnarray
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);
		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryAssignmentPage(Userbasicsinfo, PageModel, HttpServletRequest, String)方法结束OUTPARAM=" + resultjson); //$NON-NLS-1$
		}
		return resultjson;
	}

	/**
	 * <p>
	 * Title: queryBasinfo
	 * </p>
	 * <p>
	 * Description: 查询债权转让基本信息
	 * </p>
	 * 
	 * @param ids
	 * @param request
	 *            HttpServletRequest
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/query_assignment_baseinfo")
	public String queryBasinfo(String userId, HttpServletRequest request) {
		if (StringUtil.isNotBlank(userId) && StringUtil.isNumberString(userId)) {
			List<Object> objlist = userInfoServices.queryBasinfo(userId);
			if (null != objlist && !objlist.isEmpty()) {
				request.setAttribute("userinfo", objlist.get(0));
			}
		}
		return "/WEB-INF/views/admin/usermanager/assignment_base_info";
	}

	/**
	 * <p>
	 * Title: dataToExcel
	 * </p>
	 * <p>
	 * Description: 将会员信息导出excel
	 * </p>
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param ids
	 *            要导出会员的编号
	 */
	@RequestMapping("/table-to-excel")
	public void dataToExcel(HttpServletRequest request,
			HttpServletResponse response, String ids, Userbasicsinfo userinfo,
			String isbrow, String isloan, String isVip) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("dataToExcel(HttpServletRequest request=" + request + ", HttpServletResponse response=" + response + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		if (StringUtil.isNotBlank(isbrow) && StringUtil.isNumberString(isbrow)) {
			userinfo.setLockTime(isbrow);
		}
		if (StringUtil.isNotBlank(isloan) && StringUtil.isNumberString(isloan)) {
			userinfo.setLoginTime(isloan);
		}
		String headers = "序号,用户名,真实姓名,手机号码,用户类别,员工工号,积分,注册时间,上次登录时间,上次登录地址,登录次数,会员类型（普通/特权）,会员期限,是否宝付会员,是否是融资人,是否禁用";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;

		// 查询所有信息
		@SuppressWarnings("unchecked")
		List<Object> datalist = userInfoServices.queryAll(ids, userinfo);

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
		modelRecharge.downloadExcel("会员信息", null, header, content, response);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("dataToExcel(HttpServletRequest, HttpServletResponse)方法结束"); //$NON-NLS-1$
		}
	}

	/**
	 * <p>
	 * Title: queryBorrowPage
	 * </p>
	 * <p>
	 * Description: 查询借款人列表
	 * </p>
	 * 
	 * @param userinfo
	 *            搜索条件
	 * @param page
	 *            分页参数
	 * @param request
	 *            HttpServletRequest
	 * @param limit
	 *            每页显示条数
	 * @param start
	 *            从几条数据开始查询
	 * @return 查询结果转换的json对象
	 */
	@ResponseBody
	@RequestMapping("/queryborrowpage")
	public JSONObject queryBorrowPage(Userbasicsinfo userinfo, PageModel page,
			HttpServletRequest request, String limit, String start) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryBorrowPage(Userbasicsinfo userinfo=" + userinfo + ", PageModel page=" + page + ", HttpServletRequest request=" + request + ", String limit=" + limit + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}

		JSONObject resultjson = new JSONObject();

		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : page.getNumPerPage());
		}

		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		List datalist = userInfoServices.queryBrrowPage(page, userinfo);

		// json对象的键
		String titles = "id,username,suminte,addtime,credit,createtime,lasttime,logincount,endtime,vipendtime,ispass,credit_rate";

		// 数组集合转成josnarray
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);
		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryBorrowPage(Userbasicsinfo, PageModel, HttpServletRequest, String)方法结束OUTPARAM=" + resultjson); //$NON-NLS-1$
		}
		return resultjson;
	}

	/**
	 * <p>
	 * Title: queryAssignmentPage
	 * </p>
	 * <p>
	 * Description: 查询推广记录
	 * </p>
	 * 
	 * @param page
	 *            分页参数
	 * @param request
	 *            HttpServletRequest
	 * @param limit
	 *            每页显示条数
	 * @param start
	 *            从几条数据开始查询
	 * @return 查询结果转换的json对象
	 */
	@ResponseBody
	@RequestMapping("/queryPromotelistPage")
	public JSONObject queryPromotelistPage(Generalize generalize,
			PageModel page, HttpServletRequest request, String limit,
			String start, Integer userType) {

		JSONObject resultjson = new JSONObject();

		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : page.getNumPerPage());
		}

		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		List datalist = userInfoServices.GeneralizePage(page, generalize,
				userType);

		// json对象的键
		String titles = "id,genuid,adddate,uanme,uid";

		// 数组集合转成josnarray
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);
		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}

	@RequestMapping("GeneralizeExcel")
	public void outGeneralizeExcel(int id, HttpServletRequest request,
			HttpServletResponse response) {

		// 标题
		String[] header = new String[] { "编号", "被推广人用户名", "被推广人真实姓名", "推广时间",
				"被推广人状态" };
         Userbasicsinfo user=userInfoServices.queryBasicsInfoById(String.valueOf(id));
		// 获取数据源
		List list = userInfoServices.GeneralizeList(id);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("编号", str[0] + "");
			map.put("被推广人用户名", str[1] + "");
			map.put("被推广人真实姓名", str[3] + "");
			map.put("推广时间", str[2] + "");
			map.put("被推广人状态", str[4] + "");
			content.add(map);
		}
		String name = user.getName() + "--推广记录";

		// 下载excel
		modelRecharge.downloadExcel(name, null, header, content, response,
				request);
	}

	/**
	 * <p>
	 * Title: queryBorrowBasinfo
	 * </p>
	 * <p>
	 * Description: 查询借款人基本信息
	 * </p>
	 * 
	 * @param ids
	 *            借款人编号
	 * @param request
	 *            HttpServletRequest
	 * @return 借款人基本信息页面
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/query_borrow_baseinfo")
	public String queryBorrowBasinfo(String ids, HttpServletRequest request) {

		if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {
			List<Object> objlist = userInfoServices.queryBorrowBasinfo(ids);

			if (null != objlist && !objlist.isEmpty()) {
				request.setAttribute("borrowBaseinfo", objlist.get(0));
			}
		}

		return "/WEB-INF/views/admin/usermanager/borrow_base_info";

	}

	/**
	 * <p>
	 * Title: queryPersonal
	 * </p>
	 * <p>
	 * Description: 查询借款人个人资料
	 * </p>
	 * 
	 * @param ids
	 *            借款人编号
	 * @param request
	 *            HttpServletRequest
	 * @return 借款人个人资料页面
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/query_personal")
	public String queryPersonal(String ids, HttpServletRequest request) {

		if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {

			List<Object> objlist = userInfoServices.queryPersonal(ids);

			if (null != objlist && !objlist.isEmpty()) {
				request.setAttribute("borrowPersonal", objlist.get(0));
			}
		}

		return "/WEB-INF/views/admin/usermanager/borrow_personal_info";
	}

	/**
	 * <p>
	 * Title: queryPersonal
	 * </p>
	 * <p>
	 * Description: 查询借款标信息
	 * </p>
	 * 
	 * @param ids
	 *            借款人编号
	 * @param request
	 *            HttpServletRequest
	 * @return 借款人个人资料页面
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/query_assignmentLoanSign")
	public String queryAssignmentLoanSign(String loanId,
			HttpServletRequest request) {

		if (StringUtil.isNotBlank(loanId) && StringUtil.isNumberString(loanId)) {

			List<Object> objlist = userInfoServices
					.queryAssignmentLoanSign(loanId);

			if (null != objlist && !objlist.isEmpty()) {
				request.setAttribute("loanSign", objlist.get(0));
			}
		}

		return "/WEB-INF/views/admin/usermanager/assignment_base_loanSign";
	}

	/**
	 * 
	 * 审批债权转让
	 * 
	 * @param id
	 *            会员编号
	 * @param num
	 *            是否通过
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateStatus")
	public JSONObject updateStatus(String id, int num) {
		System.out.println(id);
		JSONObject json = new JSONObject();
		try {
			// TODO 正在审核、已经审核、审核通过、审核不通过、待定等判断
			if (num == 0) {
				// 已经审核，通过
				// auditStatus=3 已审核
				// AuditResult=1 通过
				boolean bool = assginService.updateSql(id, 3, 1);
				if (bool) {
					Loansignflow loansignflow = assginService
							.selLoansignFlow(id);
					bool = assginService.getAssignmentPublish(loansignflow);// 新增债权转让的标
					if (bool) {
						return DwzResponseUtil.setJson(json,
								Constant.HTTP_STATUSCODE_SUCCESS, "审批成功",
								assignmentId, null);
					} else {
						assginService.updateSql(id, 1, 2);
						return DwzResponseUtil.setJson(json,
								Constant.HTTP_STATUSCODE_ERROR, "新增债权转让与发布失败",
								assignmentId, null);
					}
				} else {
					return DwzResponseUtil.setJson(json,
							Constant.HTTP_STATUSCODE_ERROR, "审批失败",
							assignmentId, null);
				}
			} else if (num == 1) {
				// 已经审核，不通过
				// auditStatus=3 已审核
				// AuditResult=0 不 通过
				if (assginService.updateSql(id, 3, 0)) {
					return DwzResponseUtil.setJson(json,
							Constant.HTTP_STATUSCODE_SUCCESS, "审批成功",
							assignmentId, null);
				} else {
					return DwzResponseUtil.setJson(json,
							Constant.HTTP_STATUSCODE_ERROR, "审批失败",
							assignmentId, null);
				}
			} else {
				// auditStatus=2正在审核
				// AuditResult=2 通过
				if (assginService.updateSql(id, 2, 1)) {
					return DwzResponseUtil.setJson(json,
							Constant.HTTP_STATUSCODE_SUCCESS, "审批成功",
							assignmentId, null);
				} else {
					return DwzResponseUtil.setJson(json,
							Constant.HTTP_STATUSCODE_ERROR, "审批失败",
							assignmentId, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return DwzResponseUtil.setJson(json,
					Constant.HTTP_STATUSCODE_ERROR, "审批失败", assignmentId, null);
		}
	}

	/**
	 * <p>
	 * Title: queryContact
	 * </p>
	 * <p>
	 * Description: 查询借款人联系信息
	 * </p>
	 * 
	 * @param ids
	 *            借款人编号
	 * @param request
	 *            HttpServletRequest
	 * @return 返回页面
	 */
	@RequestMapping("/query_contact")
	public String queryContact(String ids, HttpServletRequest request) {

		if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {
			request.setAttribute("queryContact",
					userInfoServices.queryContact(ids));
		}

		return "/WEB-INF/views/admin/usermanager/borrow_contact_info";
	}

	/**
	 * <p>
	 * Title: queryCompany
	 * </p>
	 * <p>
	 * Description: 借款人单位资料查询
	 * </p>
	 * 
	 * @param ids
	 *            借款人编号
	 * @param request
	 *            HttpServletRequest
	 * @return 借款人单位资料页面
	 */
	@RequestMapping("/query_company")
	public String queryCompany(String ids, HttpServletRequest request) {
		if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {
			request.setAttribute("queryCompany",
					userInfoServices.queryCompany(ids));
		}

		return "/WEB-INF/views/admin/usermanager/borrow_company_info";
	}

	/**
	 * <p>
	 * Title: queryFinanes
	 * </p>
	 * <p>
	 * Description: 查询借款人财务状况
	 * </p>
	 * 
	 * @param ids
	 *            借款人主键
	 * @param request
	 *            HttpServletRequest
	 * @return 现在借款人财务状况的页面
	 */
	@RequestMapping("/query_finanes")
	public String queryFinanes(String ids, HttpServletRequest request) {
		if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {
			request.setAttribute("queryFinanes",
					userInfoServices.queryFinanes(ids));
		}

		return "/WEB-INF/views/admin/usermanager/borrow_finanes_info";
	}

	/**
	 * <p>
	 * Title: queryOtherContact
	 * </p>
	 * <p>
	 * Description: 借款人联保情况
	 * </p>
	 * 
	 * @param ids
	 *            借款人主键编号
	 * @param request
	 *            HttpServletRequest
	 * @return 借款人联保情况展示页面
	 */
	@RequestMapping("/query_other_contact")
	public String queryOtherContact(String ids, HttpServletRequest request) {
		if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {
			request.setAttribute("queryOtherContact",
					userInfoServices.queryOtherContact(ids));
		}

		return "/WEB-INF/views/admin/usermanager/borrow_other_contact_info";
	}

	/**
	 * <p>
	 * Title: disUser
	 * </p>
	 * <p>
	 * Description: 禁用会员
	 * </p>
	 * 
	 * @param ids
	 *            要禁用的会员
	 * @return 操作结果
	 */
	@ResponseBody
	@RequestMapping("/disuser")
	public JSONObject disUser(String ids) {

		JSONObject json = new JSONObject();

		if (StringUtil.isNotBlank(ids)) {
			userInfoServices.updateUserStatus(ids, "1");
		}
		return DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
				"禁用会员成功", pageId, null);
	}

	/**
	 * 
	 * 用户解锁
	 * 
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/unlockUser")
	public JSONObject unlockUser(String ids) {

		JSONObject json = new JSONObject();

		if (StringUtil.isNotBlank(ids)) {
			userInfoServices.updateUserLock(ids, "0");
		}
		return DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
				"解锁会员成功", "main79", null);
	}

	/**
	 * 机构投资审核通过
	 * 
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/mechanpasstou")
	public String passtou(String ids, HttpServletRequest request) {
		String num = "0";

		if (StringUtil.isNotBlank(ids)) {
			num = userInfoServices.updateUserresultsofinvestment(ids, 2,
					request);
		}
		return num;
	}

	/**
	 * 机构投资审核不通过
	 * 
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/mecherrortou")
	public String errortou(String ids, HttpServletRequest request) {
		String num = "0";

		if (StringUtil.isNotBlank(ids)) {
			num = userInfoServices.updateUserresultsofinvestment(ids, 3,
					request);
		}

		return num;
	}

	/**
	 * 身份认证审核通过
	 * 
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/percardpasstou")
	public String percardpasstou(String ids, HttpServletRequest request) {
		String num = "0";

		if (StringUtil.isNotBlank(ids)) {
			num = userInfoServices.updateUserbasicardstatus(ids, 2, request);

		}

		return num;
	}

	/**
	 * 身份认证审核不通过
	 * 
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/percarderrortou")
	public String percarderrortou(String ids, HttpServletRequest request) {
		String num = "0";

		if (StringUtil.isNotBlank(ids)) {
			num = userInfoServices.updateUserbasicardstatus(ids, 3, request);
		}
		return num;
	}

	/**
	 * <p>
	 * Title: enUser
	 * </p>
	 * <p>
	 * Description: 启用会员
	 * </p>
	 * 
	 * @param ids
	 *            要启用会员的编号
	 * @return 启用结果
	 */
	@ResponseBody
	@RequestMapping("/enuser")
	public JSONObject enUser(String ids) {
		JSONObject json = new JSONObject();

		if (StringUtil.isNotBlank(ids)) {
			userInfoServices.updateUserStatus(ids, "0");
		}

		return DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
				"启用会员成功", pageId, null);
	}

	public void addBlack(String ids) {

	}

	/**
	 * <p>
	 * Title: updateTime
	 * </p>
	 * <p>
	 * Description: 跳转到修改会员期限的页面
	 * </p>
	 * 
	 * @param ids
	 *            要修改会员期限的编号
	 * @param request
	 *            HttpServletRequest
	 * @return 返回修改结果
	 */
	@RequestMapping("/update_user_date")
	public String updateTime(String ids, HttpServletRequest request) {

		request.setAttribute("ids", ids);

		return "/WEB-INF/views/admin/usermanager/update_user_date_page";
	}

	/**
	 * <p>
	 * Title: query
	 * </p>
	 * <p>
	 * Description: 修改特权会员期限
	 * </p>
	 * 
	 * @param ids
	 *            要修改会员编号
	 * @param endtime
	 *            特权会员结束日期
	 * @return 修改结果
	 */
	@ResponseBody
	@RequestMapping("/update_member_date")
	public JSONObject updateMemberDate(String ids, String endtime) {

		JSONObject json = new JSONObject();

		if (userInfoServices.updateMemberDate(endtime, ids)) {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
					"修改会员期限成功", pageId, "closeCurrent");
		} else {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
					"修改会员期限失败", pageId, "closeCurrent");
		}

		return json;

	}

	/**
	 * <p>
	 * Title: queryById
	 * </p>
	 * <p>
	 * Description: 修改会员信息前根据编号查询
	 * </p>
	 * 
	 * @param ids
	 *            要修改会员基本信息编号
	 * @param request
	 *            HttpServletRequest
	 * @return 修改页面
	 */
	@RequestMapping("/query_relation")
	public String queryById(String ids, HttpServletRequest request) {
		request.setAttribute("relation",
				userInfoServices.queryRelationById(ids));
		request.setAttribute("baseinfo",
				userInfoServices.queryBasicsInfoById(ids));
		return "/WEB-INF/views/admin/usermanager/update_user_relation_page";
	}

	/***
	 * 查询要授权的会员基本信息
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryByAuthorization")
	public String queryByAuthorization(String id, HttpServletRequest request) {
		Userbasicsinfo user = userInfoServices.queryBasicsInfoById(id);
		request.setAttribute("user", user);
		return "/WEB-INF/views/admin/usermanager/update_authorization";
	}

	/***
	 * 查询要修改为借款人的会员基本信息
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryByIsCreditor")
	public String queryByIsCreditor(String id, HttpServletRequest request) {
		Userbasicsinfo user = userInfoServices.queryBasicsInfoById(id);
		request.setAttribute("user", user);
		return "/WEB-INF/views/admin/usermanager/updata_isCreditor";
	}
	
	/***
	 * 分配工号
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryByUserType")
	public String queryByUserType(String id, String type,
			HttpServletRequest request) {
		Userbasicsinfo user = userInfoServices.queryBasicsInfoById(id);
		request.setAttribute("user", user);
		request.setAttribute("type", type);
		return "/WEB-INF/views/admin/usermanager/update_userType";
	}

	/***
	 * 分配企业编号
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryByCompanyNo")
	public String queryByCompanyNo(String id, HttpServletRequest request) {
		Userbasicsinfo user = userInfoServices.queryBasicsInfoById(id);
		request.setAttribute("user", user);
		return "/WEB-INF/views/admin/usermanager/update_companyNo";
	}

	/****
	 * 设置信用等级
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryByCreditRate")
	public String queryByCreditRate(long bid, HttpServletRequest request) {
		Borrowersbase borrow = borrowService.queryByUserInfo(bid);
		request.setAttribute("user", borrow.getUserbasicsinfo());
		return "/WEB-INF/views/admin/usermanager/update_CreditRate";
	}

	/***
	 * 设置信用等级
	 * 
	 * @param user
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateCreditRate")
	public JSONObject updateCreditRate(Userbasicsinfo user, String ids) {
		JSONObject json = new JSONObject();
		// 设置信用等级
		userInfoServices.updateCreditRate(ids, user);
		DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS, "修改成功",
				borrowpageId, "closeCurrent");
		return json;
	}

	/***
	 * 修改企业编号
	 * 
	 * @param user
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateCompanyNo")
	public JSONObject updateCompanyNo(Userbasicsinfo user, String ids) {
		JSONObject json = new JSONObject();
		// 修改授权
		Integer msg = userInfoServices.updateCompanyNo(ids, user);
		if (msg == 1) {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
					"修改成功", pageId, "closeCurrent");
		} else {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
					"企业编号已存在", pageId, "closeCurrent");
		}

		return json;
	}

	/***
	 * 根据用户id授权
	 * 
	 * @param user
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateAuthorization")
	public JSONObject updateAuthorization(Userbasicsinfo user, String ids) {
		JSONObject json = new JSONObject();
		
		Userbasicsinfo userbasicsinfo = userInfoServices.queryBasicsInfoById(ids);
		int isOrgPerson = userbasicsinfo.getIsorgperson();
		if(isOrgPerson == 1 || isOrgPerson == 2 || isOrgPerson == 3) {
			if(user.getIsorgperson() != 0) {
				DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR, "当前状态下只能选择“否”！",
						pageId, "closeCurrent");
				return json;
			}
		}
		
		if(isOrgPerson == 0) {
			if(user.getIsorgperson() != 2) {
				DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR, "当前状态下只能选择“待确认”！",
						pageId, "closeCurrent");
				return json;
			}
		}
		// 修改授权
		userInfoServices.updateAuthorization(ids, user);
		DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS, "修改成功",
				pageId, "closeCurrent");
		return json;
	}

	/***
	 * 根据用户id分配工号
	 * 
	 * @param user
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateUserType")
	public JSONObject updateUserType(Userbasicsinfo user, String ids,
			String type) {
		JSONObject json = new JSONObject();
		// 修改授权
		userInfoServices.updateUserType(ids, user);
		if (type.equals("1")) {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
					"修改成功", "main79", "closeCurrent");
		} else {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
					"修改成功", pageId, "closeCurrent");
		}

		return json;
	}

	@RequestMapping("/to_bigpic")
	public String to_bigpic(String pic, HttpServletRequest request) {
		request.setAttribute("pic", pic);
		return "/WEB-INF/views/admin/usermanager/pic_big";
	}

	/**
	 * <p>
	 * Title: update
	 * </p>
	 * <p>
	 * Description: 修改会员信息
	 * </p>
	 * 
	 * @param relation
	 *            会员联系信息
	 * @param baseinfo
	 *            会员基本信息
	 * @param ids
	 *            会员基本信息主键
	 * @return 修改结果 json对象
	 */
	@ResponseBody
	@RequestMapping("/update_user")
	public JSONObject update(Userrelationinfo relation,
			Userbasicsinfo baseinfo, String ids) {

		JSONObject json = new JSONObject();

		// 修改联系信息
		userInfoServices.updateRelation(ids, relation);
		// 修改基本信息
		userInfoServices.udapteBasic(ids, baseinfo);

		DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS, "修改成功",
				pageId, "closeCurrent");

		return json;

	}

	/**
	 * <p>
	 * Title: updateCredit
	 * </p>
	 * <p>
	 * Description: 修改借款人授信额度
	 * </p>
	 * 
	 * @param ids
	 *            借款人基本信息主键
	 * @param credit
	 *            授信额度
	 * @return 返回成功或者失败 ‘n’表示失败，‘y’表示成功
	 */
	@ResponseBody
	@RequestMapping("/update_credit")
	public String updateCredit(String ids, String credit) {

		String result = "n";

		if (userInfoServices.updateCredit(ids, credit) > 0) {
			result = "y";
		}
		return result;

	}

	/**
	 * <p>
	 * Title: checkUserName
	 * </p>
	 * <p>
	 * Description: 验证用户名的唯一性
	 * </p>
	 * 
	 * @param userName
	 *            延验证的字符
	 * @return 验证结果
	 */
	@ResponseBody
	@RequestMapping("/check_name")
	public boolean checkUserName(String userName) {

		boolean flag = true;

		if (StringUtil.isNotBlank(userName)) {
			flag = registrationService.checkUserName(userName);
		}
		return flag;
	}

    /**修改为借款人*/
    @ResponseBody
    @RequestMapping("/updateIsCreditor")
	public JSONObject updateIsCreditor(Userbasicsinfo user, String ids) {
		JSONObject json = new JSONObject();
		// 修改借款状态
		userInfoServices.updateIsCreditor(ids, user);
		DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS, "修改借款状态成功",
				pageId, "closeCurrent");
		return json;
		}
	
	
	/**
	 * <p>
	 * Title: checkEmail
	 * </p>
	 * <p>
	 * Description: 验证会员邮箱的唯一性
	 * </p>
	 * 
	 * @param email
	 *            要验证的字符
	 * @return 验证结果
	 */
	@ResponseBody
	@RequestMapping("/chcke_email")
	public boolean checkEmail(String email) {

		boolean flag = true;

		if (StringUtil.isNotBlank(email)) {
			flag = registrationService.checkEmail("", email);
		}

		return flag;

	}

	/**
	 * <p>
	 * Title: getUserMoney
	 * </p>
	 * <p>
	 * Description: 后台会员资金统计
	 * </p>
	 * 
	 * @param ids
	 *            会员编号
	 * @param request
	 *            HttpServletRequest
	 * @return 数据展示页面
	 */
	@RequestMapping("/get_user_money")
	public String getUserMoney(String ids, HttpServletRequest request) {

		if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {
			userInfoServices.getUserMoney(ids, request);
		}

		return "/WEB-INF/views/admin/usermanager/moneystatistics";
	}

	/**
	 * <p>
	 * Title: openCustomer
	 * </p>
	 * <p>
	 * Description: 保存参数，跳转到分配客服页面
	 * </p>
	 * 
	 * @param ids
	 *            会员编号
	 * @param username
	 *            会员用户名
	 * @param request
	 *            HttpServletRequest
	 * @return 分配客服页面
	 */
	@RequestMapping("/open_customer")
	public String openCustomer(String ids, String username,
			HttpServletRequest request) {

		request.setAttribute("ids", ids);

		request.setAttribute("username", username);

		return "/WEB-INF/views/admin/usermanager/customer";

	}

	/**
	 * <p>
	 * Title: updateCustomer
	 * </p>
	 * <p>
	 * Description: 分配客服
	 * </p>
	 * 
	 * @param uid
	 *            会员编号
	 * @param adminid
	 *            客服编号
	 * @return 修改结果
	 */
	@ResponseBody
	@RequestMapping("/update_random")
	public JSONObject updateCustomer(String uid, String adminid) {

		JSONObject json = new JSONObject();

		// 判断参数是否为空或非数字
		if (StringUtil.isNotBlank(uid) && StringUtil.isNotBlank(adminid)
				&& StringUtil.isNumberString(uid + adminid)) {
			userInfoServices.updateCustomer(uid, adminid);
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
					"客服分配成功", pageId, "closeCurrent");
		} else {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
					"参数错误，分配客服失败", pageId, "closeCurrent");
		}

		return json;
	}

	/**
	 * <p>
	 * Title: getUsercount
	 * </p>
	 * <p>
	 * Description: 统计会员数量
	 * </p>
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/user_count")
	public String getUsercount(HttpServletRequest request) {
		Adminuser user = (Adminuser) request.getSession().getAttribute(
				Constant.ADMINLOGIN_SUCCESS);
		
		if (null != user) {
			return userInfoServices.getUsercount(user.getId().toString());
		} else {
			LOGGER.debug("后台用户未登录，请先登录！");
			return "后台用户未登录！";
		}
	}

	/****** 会员管理修改会员等级 *****/
	/**
	 * <p>
	 * Title: openisvip
	 * </p>
	 * <p>
	 * Description:打开会员修改页面
	 * </p>
	 * 
	 * @param id
	 *            标号
	 * @param request
	 * @return 要返回的页面
	 */
	@RequestMapping("/openisvip")
	public String openisvip(String id, String username,
			HttpServletRequest request) {

		request.setAttribute("vid", id);
		request.setAttribute("vusername", username);
		// 查询到用户当前的会员等级
		Object object = vipinfoservice.isVip(Long.valueOf(id));
		request.setAttribute("vipendTime", object);
		return "/WEB-INF/views/admin/usermanager/vipinfo";

	}

	/**
	 * <p>
	 * Title: updatevipinfo
	 * </p>
	 * <p>
	 * Description:修改会员等级
	 * </p>
	 * 
	 * @param uid
	 *            用户编号
	 * @param vipTime
	 *            结束时间
	 * @return 结果
	 */
	@ResponseBody
	@RequestMapping("/update_vip")
	public JSONObject updatevipinfo(String uid, String vipTime) {

		JSONObject json = new JSONObject();
		try {
			if (StringUtil.isNotBlank(uid) && StringUtil.isNotBlank(vipTime)) {
				Userbasicsinfo user = userInfoServices.queryBasicsInfoById(uid);
				Object object = vipinfoservice.isVip(Long.valueOf(uid));
				if (object != null
						&& DateUtils.isBefore(Constant.DEFAULT_DATE_FORMAT,
								vipTime, Constant.DEFAULT_TIME_FORMAT,
								object.toString())) {
					DwzResponseUtil.setJson(json,
							Constant.HTTP_STATUSCODE_ERROR, "会员期限选择错误", pageId,
							"closeCurrent");
					return json;
				}
				userInfoServices.updatevip(user, vipTime);
				DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
						"会员等级", pageId, "closeCurrent");
			} else {
				DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
						"参数错误，会员等级修改失败", pageId, "closeCurrent");
			}
			return json;
		} catch (ParseException e) {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
					"参数错误，会员等级修改失败", pageId, "closeCurrent");
			return json;
		}

	}

	/**
	 * <p>
	 * Title: openUserRecord
	 * </p>
	 * <p>
	 * Description: 借出记录
	 * </p>
	 * 
	 * @param id
	 *            编号
	 * @param request
	 *            请求
	 * @return 页面
	 */
	@RequestMapping("/openuserrecord")
	public String openUserRecord(String id, HttpServletRequest request) {
		request.setAttribute("uid", id);
		return "/WEB-INF/views/admin/usermanager/user_record_list";
	}

	/**
	 * <p>
	 * Title: queryUserLoanlist
	 * </p>
	 * <p>
	 * Description: 借出记录的查询
	 * </p>
	 * 
	 * @param limit
	 *            每页多少
	 * @param start
	 *            开始
	 * @param page
	 *            分页
	 * @param uid
	 *            用户编号
	 * @param state
	 *            查询的状态 2 进行中 3 回款中 4 已经完成的
	 * @return 数据
	 */
	@ResponseBody
	@RequestMapping("/queryuserlist")
	public JSONObject queryUserLoanlist(String limit, String start,
			PageModel page, String uid, int state) {

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

		List datalist = userInfoServices.queryuserrelistjxByuId(page, uid,
				state);
		String titles = "lrid,lsid,loanNumber,name,tenderTime,tenderMoney,rate,month,useDay,loanType,issueLoan";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}

	/**
	 * 资料上传
	 * 
	 * @param ids
	 *            编号
	 * @param request
	 *            request
	 * @return 资料上传
	 */
	@RequestMapping("/query_data_upload")
	public String queryDataUpload(String ids, HttpServletRequest request) {
		request.setAttribute("list", userInfoServices.queryDataUpload(ids));
		return "/WEB-INF/views/admin/usermanager/borrow_data_upload";
	}

	/**
	 * 借款人商业图片
	 * 
	 * @param ids
	 *            编号
	 * @param request
	 *            request
	 * @return 借款人商业图片
	 */
	@RequestMapping("/query_stock_photo")
	public String queryStockPhoto(String ids, HttpServletRequest request) {
		request.setAttribute("list", userInfoServices.queryStockPhoto(ids));
		return "/WEB-INF/views/admin/usermanager/borrow_stock_photo";
	}

	/**
	 * <p>
	 * Title: openBorrowRecord
	 * </p>
	 * <p>
	 * Description:借入记录
	 * </p>
	 * 
	 * @param id
	 *            编号
	 * @param request
	 *            响应
	 * @return 页面
	 */
	@RequestMapping("/openbborrowrecord")
	public String openBborrowRecord(long bid, HttpServletRequest request) {
		Borrowersbase borrow = borrowService.queryByUserInfo(bid);
		request.setAttribute("id", borrow.getUserbasicsinfo().getId());
		borrowService.queryBorrowrecord(borrow.getUserbasicsinfo(), request);
		return "/WEB-INF/views/admin/usermanager/borrow_record_list";
	}

	/**
	 * <p>
	 * Title: getUserManualIntegration
	 * </p>
	 * <p>
	 * Description:设置积分
	 * </p>
	 * 
	 * @param id
	 *            用户编号
	 * @param request
	 *            响应
	 * @return 信息
	 */
	@RequestMapping("/getusermanualintegr")
	public String getUserManualIntegration(Long id, HttpServletRequest request) {

		Borrowersbase borrowersbase = borrowService.queryBorrowerbase(id);

		Manualintegral manualintegral = autointegralQuery
				.queryManuaByuser(borrowersbase.getUserbasicsinfo());

		request.setAttribute("uid", borrowersbase.getUserbasicsinfo().getId());
		if (null != manualintegral && null != manualintegral.getAmountPoints()) {
			request.setAttribute("amounts", manualintegral.getAmountPoints());
			request.setAttribute("tgPoints", manualintegral.getTgPoints());
		} else {
			request.setAttribute("amounts", 0);
			request.setAttribute("tgPoints", 0);
		}
		request.setAttribute("manualintegral", manualintegral);

		return "/WEB-INF/views/admin/usermanager/manualIntegration";
	}

	/**
	 * <p>
	 * Title: openAutoinit
	 * </p>
	 * <p>
	 * Description:跳转到自动积分页面
	 * </p>
	 * 
	 * @param bid
	 *            借款人编号
	 * @param request
	 *            响应
	 * @return 页面
	 */
	@RequestMapping("/openautoinit")
	public ModelAndView openAutoinit(String bid, HttpServletRequest request) {

		request.setAttribute("bid", bid);

		Borrowersbase borrower = borrowService.queryBorrowerbase(Long
				.valueOf(bid));
		// 自动积分和
		request.setAttribute("sumautoint", autointegralQuery
				.queryAutoSUMIntegral(borrower.getUserbasicsinfo()));

		return new ModelAndView(
				"WEB-INF/views/admin/usermanager/borrow_integrat");
	}

	/**
	 * <p>
	 * Title: queryBorrowLoanlistjx
	 * </p>
	 * <p>
	 * Description: 查询竟标中的借入记录
	 * </p>
	 * 
	 * @param limit
	 * @param start
	 * @param page
	 * @param id
	 * @param state
	 * @return 数据
	 */
	@ResponseBody
	@RequestMapping("/queryborrowlist")
	public JSONObject queryBorrowLoanlist(String limit, String start,
			PageModel page, String uid, int state) {

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

		List datalist = userInfoServices.querybljxByuserId(page, uid, state);
		String titles = "id,loantitle,issueLoan,publishTime,rate,creditTime";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}

	/**
	 * <p>
	 * Title: queryautointe
	 * </p>
	 * <p>
	 * Description: 用户自动积分查询
	 * </p>
	 * 
	 * @param limit
	 *            limit
	 * @param start
	 *            开始
	 * @param page
	 *            分页
	 * @param bid
	 *            借款人id
	 * @return 数据
	 */
	@ResponseBody
	@RequestMapping("/queryautointe")
	public JSONObject queryautointe(String limit, String start, PageModel page,
			String bid) {

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

		List datalist = integralService.queryByuserId(page, bid);
		String titles = "id,loansignid,periods,realityintegral,Isover,preRepayMoney,predictintegral,loanNumber";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}

	/**
	 * <p>
	 * Title: queryApply
	 * </p>
	 * <p>
	 * Description: 借款申请查询
	 * </p>
	 * 
	 * @param limit
	 *            每页查询条数
	 * @param start
	 *            开始的位置
	 * @param page
	 *            分页模型
	 * @return 查询结果
	 */
	@ResponseBody
	@RequestMapping("/apply_page")
	public JSONObject queryApply(String limit, String start, PageModel page,
			BorrowersApply borr) {
		JSONObject resultjson = new JSONObject();

		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		List<Object> datalist = borrowersApplyService.queryPage(page, borr);

		String titles = "id,realname,phone,money,time,status,behoof,borrowmonth,corporatename,telphone,adminuser";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		/*
		 * if(null != datalist && !datalist.isEmpty()){ JSONObject json=null;
		 * for (BorrowersApply apply : datalist) { json = new JSONObject();
		 * 
		 * json.element("id", apply.getId()); json.element("realname",
		 * apply.getUserbasicsinfo().getName()); json.element("phone",
		 * apply.getPhone()); json.element("money", apply.getMoney());
		 * json.element("time", apply.getTime()); json.element("refunway",
		 * apply.getRefunway()); json.element("status", apply.getStatus());
		 * json.element("behoof", apply.getBehoof());
		 * json.element("borrowmonth", apply.getBorrowmonth());
		 * json.element("corporatename", apply.getCorporatename());
		 * json.element("city", apply.getCity()); json.element("telphone",
		 * apply.getTelphone()); jsonlist.add(json); } }
		 */

		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}

	/**
	 * <p>
	 * Title: saveOrUpdatemanualIntegration
	 * </p>
	 * <p>
	 * Description: 保存或修改手动积分信息
	 * </p>
	 * 
	 * @param manualin
	 *            手动积分信息
	 * @return 结果
	 */
	@ResponseBody
	@RequestMapping("/saveOrUpdateman")
	public JSONObject saveOrUpdatemanualIntegration(Manualintegral manualin) {
		JSONObject json = new JSONObject();

		// 首先通过后台传过来的进行计算该用户的总和
		int amountpoints = autointegralQuery.getALLBYOneSerch(manualin);
		Manualintegral manualintegral = autointegralQuery
				.queryManuaByuser(manualin.getUserbasicsinfo());
		if (manualintegral != null) {
			amountpoints += manualintegral.getTgPoints() != null ? manualintegral
					.getTgPoints() : 0;
		}
		boolean bool = integralService.saveOrUpdateManualinte(manualin,
				amountpoints);
		if (bool) {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
					"手动积分设置成功", borrowpageId, "closeCurrent");
		} else {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
					"手动积分设置失败", borrowpageId, "closeCurrent");
		}
		return json;
	}

	/**
	 * <p>
	 * Title: updateApplyStatus
	 * </p>
	 * <p>
	 * Description: 审核借款人申请
	 * </p>
	 * 
	 * @param ids
	 *            申请编号
	 * @param status
	 *            状态
	 * @param remark
	 *            备注
	 * @return 修改结果
	 */
	@ResponseBody
	@RequestMapping("/apply_update")
	public JSONObject updateApplyStatus(String ids, String status, String remark) {
		JSONObject json = new JSONObject();

		if (StringUtil.isNotBlank(ids) && StringUtil.isNotBlank(status)
				&& StringUtil.isNumberString(ids + status)) {
			try {
				remark = new String(remark.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (borrowersApplyService.updateApplyStatus(ids, status, remark)) {
				DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
						"修改成功", "main54", null);
			} else {
				DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
						"修改失败", "main54", null);
			}
		} else {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
					"参数错误", "main54", null);
		}

		return json;
	}

	/**
	 * <p>
	 * Title: passBorrow
	 * </p>
	 * <p>
	 * Description: 审核通过和不通过
	 * </p>
	 * 
	 * @param ids
	 *            审核通过
	 * @param state
	 *            状态
	 * @return 结果
	 */
	@ResponseBody
	@RequestMapping("/pass")
	public JSONObject passBorrow(HttpServletRequest request, int state,
			String ids) {

		JSONObject json = new JSONObject();

		if (StringUtil.isNotBlank(ids)) {
			if (!userInfoServices.ispassture(ids)) {

				return DwzResponseUtil.setJson(json,
						Constant.HTTP_STATUSCODE_ERROR, "只有未审核的借款人才能审核",
						borrowpageId, "closeCurrent");
			}
			boolean bool = userInfoServices.updateborrowState(request, ids,
					state);
			if (!bool) {
				return DwzResponseUtil.setJson(json,
						Constant.HTTP_STATUSCODE_ERROR, "审核失败", borrowpageId,
						"closeCurrent");
			}
		}

		return DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
				"借款人审核成功", borrowpageId, "closeCurrent");
	}

	/**
	 * <p>
	 * Title: updateBorrowApplyStatus
	 * </p>
	 * <p>
	 * Description: 批量修改借款申请
	 * </p>
	 * 
	 * @param ids
	 *            要修改的id
	 * @param status
	 *            要修改的状态
	 * @return 修改结果
	 */
	@ResponseBody
	@RequestMapping("/update_applys")
	public JSONObject updateBorrowApplyStatus(String ids, String status,
			HttpServletRequest request) {

		JSONObject json = new JSONObject();

		if (borrowersApplyService.updatesApply(ids, status, request)) {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS,
					"修改成功", "main54", "closeCurrent");
		} else {
			DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR,
					"修改失败", "main54", "closeCurrent");
		}

		return json;
	}

	/**
	 * 列出所有站内消息
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/list_messages")
	public JSONObject adminuserMessages(String start, String limit,
			PageModel page, HttpServletRequest request) {
		Adminuser user = (Adminuser) request.getSession().getAttribute(
				Constant.ADMINLOGIN_SUCCESS);

		JSONObject resultjson = new JSONObject();

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
		List list = messagesettingService.getMessages(user.getId().toString(),
				page);

		JSONArray jsonlist = new JSONArray();
		String titles = "id,title,context,time,isread";

		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}

	/**
	 * 消息列表页面
	 * 
	 * @return
	 */
	@RequestMapping("/messagelist")
	public String messageList(HttpServletRequest request) {
		return "WEB-INF/views/admin/messagelist";
	}

	/**
	 * 标记信息为已读
	 * 
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/readmessage")
	public String read(String ids) {
		if (!"all".equals(ids) && !messagesettingService.notRead(ids)) {
			return "error";
		}
		messagesettingService.read(ids);
		return "success";
	}

	@RequestMapping("/guanzhucishu.htm")
	@ResponseBody
	public String getGuanzhu(String uId, HttpServletResponse response,
			HttpServletRequest request) {
		List list = userInfoServices.getGuanzhu(uId);
		StringBuffer sb = new StringBuffer();
		try {
			response.setCharacterEncoding("UTF-8");
			if (true) {
				sb.append("<b>" + list.get(0) + "</b>");
			}
			response.setContentType("text/html");
			response.getWriter().print(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 查询用户信息
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("ipsSelUser.htm")
	public String ipsSelUser(String id, HttpServletRequest request) {
		return baoFuLoansignService.ipsSelUserService(id, request);
	}

	/***
	 * 查询要查询宝付ID的会员基本信息
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryByTimeState")
	public String queryByTimeState(HttpServletRequest request) {
		return "/WEB-INF/views/admin/usermanager/update_timeState";
	}

	/***
	 * 查询用户信息
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("ipsSelTimeUser.htm")
	public JSONObject ipsSelTimeUser(String startTimeState,
			String endTimeState, HttpServletRequest request) {
		return baoFuLoansignService.ipsSelTimeUserService(startTimeState,
				endTimeState, request);
	}

	/**
	 * 员工推广
	 * 
	 * @param userinfo
	 * @param page
	 * @param request
	 * @param limit
	 * @param start
	 * @param isbrow
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryGenlizePage")
	public JSONObject queryGenlizePage(Userbasicsinfo userinfo, PageModel page,
			HttpServletRequest request, String limit, String start,
			String isbrow) {

		if (StringUtil.isNotBlank(isbrow) && StringUtil.isNumberString(isbrow)) {
			userinfo.setLockTime(isbrow);
		}
		JSONObject resultjson = new JSONObject();

		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		List datalist = userInfoServices.queryUserGenlizePage(page, userinfo);
		// endtime,vipendtime,isborrower,备用
		String titles = "id,username,realname,createTime,staff_no,userintegral,userType,islock,gencount,genAcoumt";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());
		return resultjson;

	}

	/**
	 * 根据用户查询推广记录
	 * 
	 * @param id
	 * @param page
	 * @param limit
	 * @param start
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryGenlizeList")
	public JSONObject queryGenlizeList(String id, PageModel page, String limit,
			String start, HttpServletRequest request) {
		JSONObject resultjson = new JSONObject();

		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		List datalist = userInfoServices.queryGenlizeListPage(id, page);
		// endtime,vipendtime,isborrower,备用
		String titles = "id,name,adddate,uanme,state";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());
		return resultjson;

	}

	/**
	 * 推广人投资记录
	 * 
	 * @param id
	 * @param page
	 * @param limit
	 * @param start
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryGenlizeRecord")
	public JSONObject queryGenlizeRecord(String id, PageModel page,
			String limit, String start, Userbasicsinfo userinfo,
			HttpServletRequest request) {
		JSONObject resultjson = new JSONObject();

		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		List datalist = userInfoServices.queryGenlizeRecord(id, userinfo, page);
		jsonlist = loanrecordService.getJSONArrayByList(datalist);
		// 将数据源封装为json对象（命名必须rows）
		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());
		return resultjson;

	}

	/**
	 * 会员推广
	 * 
	 * @param userinfo
	 * @param page
	 * @param request
	 * @param limit
	 * @param start
	 * @param isbrow
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryGenlizeUserPage")
	public JSONObject queryGenlizeUserPage(Userbasicsinfo userinfo,
			PageModel page, HttpServletRequest request, String limit,
			String start, String isbrow) {

		if (StringUtil.isNotBlank(isbrow) && StringUtil.isNumberString(isbrow)) {
			userinfo.setLockTime(isbrow);
		}
		JSONObject resultjson = new JSONObject();

		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		List datalist = userInfoServices.queryUserGenlizeList(page, userinfo);
		// endtime,vipendtime,isborrower,备用
		String titles = "id,username,realname,createTime,staff_no,userintegral,userType,islock,gencount,genAcoumt";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());
		return resultjson;

	}

	/**
	 * 导出投资记录
	 * 
	 * @param id
	 * @param request
	 * @param response
	 */
	@RequestMapping("outLoanrecordExcel")
	public void outPutLoanrecordExcel(int id, HttpServletRequest request,
			HttpServletResponse response) {

		// 标题
		String[] header = new String[] { "项目名称", "用户名", "购买金额", "购买时间", "购买状态",
				"特权会员", "投资类型", "管理费" };

		// 获取数据源
		List list = userInfoServices.queryLoanrecordList(0, 0, id, 2);

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
			content.add(map);

		}
		Object[] strone = (Object[]) list.get(0);
		String name = strone[2] + "---出借记录";
		// 下载excel
		modelRecharge.downloadExcel(name, null, header, content, response,
				request);
	}

	/**
	 * 查看用户详情
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/seeCutUserDetailes")
	public String seeCutUserDetailes(HttpServletRequest request, String id) {
		List cutuserList = userInfoServices.querySeeUserDetailes(id);
		request.setAttribute("cutuser", cutuserList);
		return "/WEB-INF/views/admin/customer/customerSeeUserDetales";
	}
	
	/***
	 * 客服-会员管理
	 * @param userinfo
	 * @param page
	 * @param request
	 * @param limit
	 * @param start
	 * @param isbrow
	 * @param isloan
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/querypageCustomer")
	public JSONObject querypageCustomer(Userbasicsinfo userinfo, PageModel page,
			HttpServletRequest request, String limit, String start,String isbrow, String isloan,String isPurchase,String isRecommend) {

		if (StringUtil.isNotBlank(isbrow) && StringUtil.isNumberString(isbrow)) {
			userinfo.setLockTime(isbrow);
		}
		if (StringUtil.isNotBlank(isloan) && StringUtil.isNumberString(isloan)) {
			userinfo.setLoginTime(isloan);
		}
		JSONObject resultjson = new JSONObject();

		JSONArray jsonlist = new JSONArray();

		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}

		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}

		List datalist = userInfoServices.queryUserPageCustomer(page, userinfo,isPurchase,isRecommend);
		// endtime,vipendtime,isborrower,备用
		String titles = "id,username,realname,phone,createTime,hasIpsAccount,isAuthIps,pMerBillNo,user_type,staff_no,cashBalance,genName,genUserName,department,channelName,sumTenderMoney";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryPage(Userbasicsinfo, PageModel, HttpServletRequest, String)方法结束OUTPARAM=" + resultjson); //$NON-NLS-1$
		}
		return resultjson;

	}
	
	
	private static Map<Integer,String> OPER;
	
	static{
		OPER=new HashMap<>();
		OPER.put(1, "总裁办");
		OPER.put(2, "财务部");
		OPER.put(3, "行政部");
		OPER.put(4, "副总办");
		OPER.put(5, "运营中心");
		OPER.put(6, "培训部");
		OPER.put(7, "风控部");
		OPER.put(8, "IT部");
		OPER.put(9, "摄影部");
		OPER.put(10, "推广部");
		OPER.put(11, "项目部");
		OPER.put(12, "客服部");
		OPER.put(13, "事业一部");
		OPER.put(14, "事业二部");
		OPER.put(15, "离职员工");
	} 
	
	/***
	 * 客服-会员管理
	 * @param request
	 * @param response
	 * @param ids
	 * @param userinfo
	 * @param isbrow
	 * @param isloan
	 */
	@RequestMapping("/dataToExcelCustomer")
	public void dataToExcelCustomer(HttpServletRequest request,HttpServletResponse response, String ids, Userbasicsinfo userinfo,String isbrow, String isloan,String isPurchase,String isRecommend) {
		if (StringUtil.isNotBlank(isbrow) && StringUtil.isNumberString(isbrow)) {
			userinfo.setLockTime(isbrow);
		}
		if (StringUtil.isNotBlank(isloan) && StringUtil.isNumberString(isloan)) {
			userinfo.setLoginTime(isloan);
		}
		String headers = "序号,用户名,真实姓名,手机号码,注册时间,宝付授权状态,用户类型,推荐人用户名,推荐人姓名,推荐人部门,渠道来源,认购金额";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;

		// 查询所有信息
		@SuppressWarnings("unchecked")
		List<Object> datalist = userInfoServices.queryAllCustomer(ids, userinfo,isPurchase,isRecommend);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : datalist) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("序号", str[0] + "");
			map.put("用户名", str[1] + "");
			map.put("真实姓名", str[2] + "");
			map.put("手机号码", str[3] + "");
			map.put("注册时间", str[4] + "");
			map.put("宝付授权状态", str[5] + "");
			map.put("用户类型", str[6] + "");
			map.put("推荐人用户名", str[7] == null && str[7] == "" ? "" : str[7] + "");
			map.put("推荐人姓名", str[8] == null && str[8] == "" ? "" : str[8] + "");
			map.put("推荐人部门", OPER.get(str[9]));
			map.put("渠道来源", str[10] == null && str[10] == "" ? "" : str[10] + "");
			map.put("认购金额", str[11] == null && str[11] == "" ? "" : Arith.round(new BigDecimal(str[11].toString()), 2) + "元");
			content.add(map);
		}

		// 导出会员信息
		modelRecharge.downloadExcel("会员信息", null, header, content, response);
	}
}
