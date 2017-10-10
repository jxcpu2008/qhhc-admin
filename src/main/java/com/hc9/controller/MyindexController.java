package com.hc9.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pomo.web.page.model.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.constant.IntegralType;
import com.hc9.common.log.LOG;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.RedisUtil;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.redis.SysCacheManagerUtil;
import com.hc9.common.util.CSRFTokenManager;
import com.hc9.common.util.GenerateLinkUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.normal.Md5Util;
import com.hc9.commons.normal.Validate;
import com.hc9.dao.entity.Securityproblem;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Usermessage;
import com.hc9.dao.entity.Validcodeinfo;
import com.hc9.dao.entity.Verifyproblem;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.LoginRelVo;
import com.hc9.model.PageModel;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.CacheManagerService;
import com.hc9.service.EmailService;
import com.hc9.service.GeneralizeService;
import com.hc9.service.IIDAuthentication;
import com.hc9.service.IntegralSevice;
import com.hc9.service.LoanInfoService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.MemberCenterService;
import com.hc9.service.MyindexService;
import com.hc9.service.PayLogService;
import com.hc9.service.ProcessingService;
import com.hc9.service.SmsUtilService;
import com.hc9.service.UserBaseInfoService;
import com.hc9.service.UserInfoServices;
import com.hc9.service.UserbasicsinfoService;
import com.hc9.service.ValidcodeInfoService;
import com.hc9.service.VerificationService;
import com.hc9.service.VerifyService;
import com.hc9.service.WithdrawServices;

import freemarker.template.TemplateException;

/**
 * 个人中心
 * 
 * @author My_Ascii
 * 
 */

@Controller
@RequestMapping("/member")
@CheckLogin(value = CheckLogin.WEB)
public class MyindexController {

	@Resource
	private UserbasicsinfoService userbasicsinfoService;

	/**
	 * 会员中心首页接口
	 */
	@Resource
	private MemberCenterService memberCenterService;

	/**
	 * 注入EmailService
	 */
	@Resource
	private EmailService emailService;

	@Resource
	private SmsUtilService smsUtilService;

	@Resource
	private UserInfoServices infoServices;

	@Resource
	private MyindexService myindexService;

	@Resource
	private LoanSignQuery loanSignQuery;

	@Resource
	private ProcessingService processingservice;

	@Resource
	private LoanInfoService loanInfoService;

	@Resource
	private ValidcodeInfoService validcodeInfoService;

	/** 提现sercices **/
	@Resource
	private WithdrawServices withdrawServices;

	@Resource
	private VerifyService verfifyService;
	@Resource
	private PayLogService payLogService;

	/**
	 * 安全中心接口
	 */
	@Resource
	private VerificationService verificationService;

	/**
	 * HibernateSupport
	 */
	@Resource
	private HibernateSupport dao;

	@Resource
	private UserBaseInfoService userBaseInfoService;
	@Resource
	private IntegralSevice integralSevice;
	@Resource
	private IIDAuthentication pycreditService;

	@Resource
	private GeneralizeService generalizeService;

	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	
	@Resource
	private CacheManagerService cacheManagerService;
	
	/**
	 * 返回的提示状态
	 */
	private String result = "result";

	/**
	 * 是否通过验证
	 */
	String isPass = "false";

	/**
	 * 邮箱验证初始化
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return 返回邮箱验证页面
	 */
	@RequestMapping(value = "/mail", method = RequestMethod.GET)
	public String mailValidate(HttpServletRequest request,
			HttpServletResponse response, String method) {
		request.setAttribute("method", method);
		Userbasicsinfo userbasicsinfo = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.ATTRIBUTE_USER);
		Userbasicsinfo user = infoServices.queryBasicsInfoById(userbasicsinfo
				.getId().toString());
		request.setAttribute("user", user);
		return "WEB-INF/views/member/safetycenter/safetycenter";
	}

	/**
	 * 手机绑定
	 * 
	 */
	@RequestMapping("/bindPhone.htm")
	@ResponseBody
	public String bindPhone(HttpServletRequest request, String smscode,
			String newPhone,String tradePwd) {
		Userbasicsinfo userbasicsinfo = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		Userbasicsinfo user = infoServices.queryBasicsInfoById(userbasicsinfo
				.getId().toString());
		Validcodeinfo validcode = (Validcodeinfo) dao.findObject(
				"from Validcodeinfo v where v.userbasicsinfo.id=?",
				userbasicsinfo.getId());
		String regCode = (String)request.getSession().getAttribute("regCode");
		// 判断手机验证码是否正确
		if (smscode.equals(regCode)) {
			// 判断手机验证码是否超时
			/*Long time = new Date().getTime();
			Long endtime = validcode.getSmsoverTime();
			if (endtime < time) {*/
				tradePwd = Md5Util.execute(tradePwd);
				if (tradePwd.equals(user.getTransPassword())) {
					try {
						user.getUserrelationinfo().setPhone(newPhone);
						user.getUserrelationinfo().setPhonePass(1);
						userbasicsinfoService.update(user);
						// 绑定成功
						String numberCode = StringUtil.getvalidcode();
						validcode.setSmsCode(numberCode);
						validcodeInfoService.update(validcode);
						// 手机绑定加分
						integralSevice.phoneAuth(user, IntegralType.PHONE);
						request.getSession().removeAttribute("regCode");  // 清楚验证码
						
						LoginRelVo loginRelVo = SysCacheManagerUtil.getLoginRelVoById("" + user.getId());
						loginRelVo.setPhone(newPhone);
						cacheManagerService.updateLoginRelVoToRedis(loginRelVo);
						LOG.error("更新用户" + loginRelVo.getId() + "缓存相关信息成功！");
						return "5";
					} catch (Exception e) {// 绑定失败
						LOG.error("绑定手机号码失败！", e);
						return "2";
					}
				} else {
					return "3";   // 交易密码错误
				}
			/*} else {// 验证码超时
				return "0";
			}*/
		} else {  // 验证码错误
			return "1";
		}

	}

	/**
	 * 身份验证
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return 返回身份验证页面
	 */
	@RequestMapping("/identity")
	public String identityValidate(HttpServletRequest request) {
		return initIdentity(request);
	}

	/**
	 * 初始化身份验证页面
	 * 
	 * @param request
	 *            HttpServletRequest request
	 * @return String
	 */
	public String initIdentity(HttpServletRequest request) {
		Userbasicsinfo u = (Userbasicsinfo) request.getSession().getAttribute(
				Constant.SESSION_USER);
		u = userbasicsinfoService.queryUserById(u.getId());
		request.setAttribute("u", u);
		request.setAttribute("uname", u.getName());
		request.setAttribute("ucardId", u.getUserrelationinfo().getCardId());
		return "WEB-INF/views/member/safetycenter/verify_identity";
	}

	/**
	 * 手机验证
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return 返回手机验证页面
	 */
	@RequestMapping("/phone")
	public String phoneValidate(HttpServletRequest request,
			HttpServletResponse response) {
		Userbasicsinfo u = (Userbasicsinfo) request.getSession().getAttribute(
				Constant.SESSION_USER);
		request.setAttribute("u",
				userbasicsinfoService.queryUserById(u.getId()));
		return "WEB-INF/views/member/safetycenter/verify_phone";
	}

	/**
	 * 跳转到上传头像页面
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@RequestMapping("/fowardUploadPhoto")
	public String fowardUploadPhoto(HttpServletRequest request) {
		Userbasicsinfo u = (Userbasicsinfo) request.getSession().getAttribute(
				Constant.SESSION_USER);
		request.setAttribute("u",
				userbasicsinfoService.queryUserById(u.getId()));
		return "WEB-INF/views/member/personalinfo/upload_photo";
	}

	/**
	 * 
	 * @param page
	 *            Page
	 * @param request
	 *            HttpServletRequest
	 * @return Object
	 */
	@ResponseBody
	@RequestMapping("/loglist")
	public Object loglist(Page page, HttpServletRequest request) {
		Userbasicsinfo u = (Userbasicsinfo) request.getSession().getAttribute(
				Constant.SESSION_USER);
		page.setData(myindexService.queryLog(page, u.getId()));
		return page;
	}

	/**
	 * 跳转到登陆日志页面
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@RequestMapping("/fowardLogging")
	public String fowardLogging(HttpServletRequest request) {
		return "WEB-INF/views/member/systeminfo/logging";
	}

	/**
	 * 查询用户系统消息
	 * 
	 * @param page
	 *            分页对象
	 * @param id
	 *            消息id
	 * @param unRead
	 *            是否已读
	 * @param request
	 *            请求
	 * @return 返回.jsp
	 */
	@RequestMapping("/fowardSysteminfo")
	public String querySysteminfo(
			@ModelAttribute("PageModel") PageModel page,
			@RequestParam(value = "id", defaultValue = "", required = false) Long id,
			@RequestParam(value = "unRead", defaultValue = "", required = false) Integer unRead,
			HttpServletRequest request) {
		return querySystemMessage(page, id, unRead, request);
	}

	/**
	 * 查询用户系统消息
	 * 
	 * @param page
	 *            分页对象
	 * @param id
	 *            消息id
	 * @param unRead
	 *            是否已读
	 * @param request
	 *            请求
	 * @return 返回.jsp
	 */
	@SuppressWarnings("rawtypes")
	public String querySystemMessage(
			@ModelAttribute("PageModel") PageModel page,
			@RequestParam(value = "id", defaultValue = "", required = false) Long id,
			@RequestParam(value = "unRead", defaultValue = "", required = false) Integer unRead,
			HttpServletRequest request) {
		// 取到登录用户sesssion
		Userbasicsinfo user = queryUser(request);
		// 如果查看单条信息
		if (id != null && unRead != null && !id.toString().trim().equals("")
				&& !unRead.toString().trim().equals("")) {
			Usermessage message = memberCenterService.queryById(id, unRead);
			request.setAttribute("id", message.getId());
		}
		// 查询用户已读消息条数
		Object read = memberCenterService.queryIsReadCount(user.getId(), 1);
		// 查询用户系统消息条数
		Object obj = memberCenterService.queryUserMessageCount(user.getId());
		page.setTotalCount(Integer.parseInt(obj.toString()));
		// 查询用户系统消息
		List list = memberCenterService.queryUserMessage(user.getId(), page);
		request.setAttribute("list", list);
		request.setAttribute("page", page);
		request.setAttribute("count", Integer.parseInt(obj.toString()));
		request.setAttribute("read", read);
		request.setAttribute("unRead", Integer.parseInt(obj.toString())
				- Integer.parseInt(read.toString()));
		return "WEB-INF/views/member/systeminfo/systeminfo";
	}

	/**
	 * 登录用户session
	 * 
	 * @param request
	 *            请求
	 * @return 用户基本信息
	 */
	public Userbasicsinfo queryUser(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		return user;
	}

	/**
	 * 修改头像具体操作
	 * 
	 * @param imgurl
	 *            头像路径
	 * @param uid
	 *            会员id
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return String
	 */
	@RequestMapping("/updatePhoto")
	@ResponseBody
	public String updatePhoto(String imgurl, String uid,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Userbasicsinfo u = userbasicsinfoService.queryUserById(Long
					.parseLong(uid));
			u.getUserrelationinfo().setImgUrl(imgurl);
			userbasicsinfoService.update(u);
			request.getSession().setAttribute(Constant.SESSION_USER, u);
			result = "1";
		} catch (Exception e) {
			e.printStackTrace();
			result = "0";
		}

		return result;
	}

	/**
	 * 上传头像具体操作
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 * @throws IOException
	 *             异常
	 */
//	@RequestMapping("/uploadPhoto")
	public String uploadPhoto(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Userbasicsinfo u = (Userbasicsinfo) request.getSession().getAttribute(
				Constant.SESSION_USER);
		u.getUserrelationinfo().setImgUrl(
				myindexService.upload(request, response).get("imgurl")
						.toString());
		userbasicsinfoService.update(u);
		request.getSession().setAttribute(Constant.SESSION_USER, u);
		request.setAttribute("u",
				userbasicsinfoService.queryUserById(u.getId()));
		return "WEB-INF/views/member/personalinfo/upload_photo";
	}

	/**
	 * 发送邮箱激活邮件
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return boolean
	 */
	@RequestMapping("/replymail")
	@ResponseBody
	public String replyMail(HttpServletRequest request, String username) {

		return myindexService.replyMail(request, username);
	}

	/**
	 * 通过发送邮件重置邮箱
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@RequestMapping("/resetMail")
	@ResponseBody
	public String resetMail(HttpServletRequest request, String email) {
		Userbasicsinfo u = (Userbasicsinfo) request.getSession().getAttribute(
				Constant.SESSION_USER);
		u = userbasicsinfoService.queryUserById(u.getId());
		try {
			// 发送激活邮件
			try {
				myindexService.sendResetEmail(u, email, request);
				result = "1";
			} catch (Exception e) {
				e.printStackTrace();
				result = "0";
			}
			request.getSession().setAttribute("newEmail", email); // 保存需要需要写入的邮箱
		} catch (Exception e) {
			e.printStackTrace();
			result = "0";
		}
		return result;
	}

	/**
	 * 发送修改邮箱的邮件
	 * 
	 * @param u
	 *            用户
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException
	 *             异常
	 * @throws TemplateException
	 *             异常
	 */
	public void sendResetEmail(Userbasicsinfo u, HttpServletRequest request)
			throws IOException, TemplateException {
		// 收件人地址
		String address = u.getUserrelationinfo().getEmail();
		String userName = u.getName();

		String url = GenerateLinkUtils.generateUptEmailLink(u, request);
		Map<String, String> map = new HashMap<String, String>();
		if (userName == null || userName.equals("")) {
			map.put("name", "亲爱的用户");
		} else {
			map.put("name", userName);
		}

		map.put("emailActiveUrl", url);
		String[] msg = emailService.getEmailResources("uptemail-activate.ftl",
				map);
		// 发送邮件链接地址
		emailService.sendEmail(msg[0], msg[1], address);
	}

	/**
	 * 邮箱激活
	 * 
	 * @param id
	 *            用户id
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@RequestMapping("/emailValidate")
	public String emailValidate(String id, HttpServletRequest request) {
		if (Validate.emptyStringValidate(id)) {
			if ((id.indexOf("+") == -1 || id.indexOf("/") == -1)
					&& id.indexOf("=") == -1) {
				try {
					id = URLDecoder.decode(id, "UTF-8");
				} catch (UnsupportedEncodingException e) {
				}
			}
			Userbasicsinfo user = userbasicsinfoService.queryUserById(Long
					.parseLong(StringUtil.correctPassword(id)));
			user.getUserrelationinfo().setEmailisPass(1);
			request.setAttribute("u", user);
			try {
				userbasicsinfoService.update(user);
				request.getSession().setAttribute(Constant.SESSION_USER, user);
				return "WEB-INF/views/success";
			} catch (Exception e) {
				e.printStackTrace();
				return "WEB-INF/views/member/safetycenter/ev_failed";
			}
		} else {
			return "WEB-INF/views/member/safetycenter/ev_failed";
		}
	}

	/**
	 * 身份验证具体操作
	 * 
	 * @param name
	 *            会员真实姓名
	 * @param cardId
	 *            会员身份证号
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@RequestMapping("/identityValidateImpl")
	@ResponseBody
	public String identityValidateImpl(String name, String cardId,
			HttpServletRequest request) {
		return myindexService.identityValidateImpl(name, cardId, request);
	}

	/**
	 * 设置会员安全问题
	 * 
	 * @param id
	 *            用户id
	 * @param question01
	 *            问题1
	 * @param question02
	 *            问题2
	 * @param anwser01
	 *            答案1
	 * @param anwser02
	 *            答案2
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@RequestMapping("/toSafetyProblem.htm")
	public String toSafetyProblem(HttpServletRequest request) {
		List<Verifyproblem> list = verfifyService.queryVerify();
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		List<Securityproblem> spList = verificationService
				.querySecuProByUser(user);
		if (spList != null && spList.size() > 0) {
			request.setAttribute("security1", spList.get(0));
			request.setAttribute("security2", spList.get(1));
		}
		request.setAttribute("list", list);
		return "/WEB-INF/views/member/verification/security";
	}

	@RequestMapping("/setSafetyProblem.htm")
	public String setSafetyProblem(HttpServletRequest request, String id,
			String question01, String anwser01, String question02,
			String anwser02) {

		myindexService.verify_question(question01, anwser01, question02,
				anwser02, request);
		return null;
	}



	/**
	 * 注册发送短信
	 * 
	 * @param phone
	 * @param request
	 * @param urlcase
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/sendSMSForReg",method = RequestMethod.POST)
	@ResponseBody
	public String sendSMSForReg(@RequestParam String CSRFToken,String phone, HttpServletRequest request)throws Exception {
		if("18380599919".equals(phone)){
			return "error-404";
		}
		if(request.getSession().getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)==null){
			return "error-404";
		}
		if(CSRFToken == null ||!CSRFToken.equals(request.getSession().getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME).toString())){
			return "error-404";
		}
		try {
		 	//做临时切换校验功能
		 	//获取缓存里的数值，判断结果，如果为1，沿用校验，0，关闭校验
		 	String validateStatus=SmsEmailCache.getSmsValidateStatus();

		 	String smsResult="";
		 	if(null!=validateStatus && "1".equals(validateStatus)){
		 		smsResult=sendSmsWithValidate(request, phone);
		 	}else{
		 		smsResult=sendSmsWithoutValidate(request, phone);		 		

		 	}
		 	request.getSession().setAttribute("regPhone", phone);
		 	
		 	return smsResult;
		} catch (Exception e) {
			LOG.error("生成短信验证码出错！", e);
			return "0";
		}
	}
	/**
	 * 校验后发送短信
	 * @param request
	 * @param phone
	 * @param userInfo
	 * @return
	 * @throws Exception
	 */
	private String sendSmsWithValidate(HttpServletRequest request,String phone) throws Exception{
		String userInfo = "";
	  	Userbasicsinfo userbasic = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
	 	if(userbasic != null) {
	 		userInfo = "用户id:" + userbasic.getId() + ",用户名：" + userbasic.getName();
		}
	    String sessionId = request.getSession().getId();
	    String sessionIdkey = "INT:HC9:SMS:REG:CODE:GEN:NUM:" + sessionId;
	    LOG.error("-->本次session: "+sessionId);
	    if(RedisUtil.validCodeGenNum(sessionIdkey, phone)) {
	    	LOG.error(sessionId + " 校验通过！ ");
	    	String queryString = request.getQueryString();
		 	smsUtilService.sendCodeForReg(request, phone);
			LOG.info(userInfo + ",phone:" + phone + " 产生一个手机验证码, sessionId=" + sessionId 
				+ ",ip=" + request.getRemoteAddr() + ",port=" + request.getRemotePort() 
				+ ",queryString:" + queryString);
			RedisHelper.incrBy(sessionIdkey, 1);
			RedisUtil.increasePhoneValidCodeTotalNum(phone);
			return "1";
	   	} else {
	   		LOG.error(sessionId + " 校验不通过！ ");
	   		return "2";
	 	}
	}
	private String sendSmsWithoutValidate(HttpServletRequest request,String phone) throws Exception{
		LOG.error("-->校验关闭");
		smsUtilService.sendCodeForReg(request, phone);
		return "1";
	}
	/**
	 * 针对已经绑定的手机验证
	 * 
	 * @param phone
	 *            手机号码
	 * @param smscode
	 *            手机验证码
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@RequestMapping("/validatePhone")
	@ResponseBody
	public String validatePhone(String phone, String smscode,
			HttpServletRequest request) {
		Userbasicsinfo u = (Userbasicsinfo) request.getSession().getAttribute(
				Constant.SESSION_USER);
		Userbasicsinfo user = infoServices.queryBasicsInfoById(u.getId()
				.toString());
		// 可能出现用户长时间没处理而session过期
		if (user == null) {
			return "redirect:/visitor/to-login";
		}

		String checkCode = myindexService.verifyPhone(phone, smscode, request);
		if (checkCode != "1") {
			return "0";
		}

		userbasicsinfoService.update(user);
		return "1";
	}

	/**
	 * 身份验证上传附件
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 * @throws IOException
	 *             异常
	 */
	@RequestMapping("/uploadFile")
	public String uploadFile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		myindexService.upload(request, response);
		return initIdentity(request);
	}

	/**
	 * 注册宝付
	 * 
	 * @param userId
	 *            用户id
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@RequestMapping("/ipsRegistration")
	public String ipsRegistration(HttpServletRequest request,String cardId,String name, String type) {
		return baoFuLoansignService.ipsRegistrationService(request,cardId,name, type);
	}
	/**
	 * 用户注册成功
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	@RequestMapping("regis")
	@ResponseBody
	public void regis(HttpServletRequest request) {
		Userbasicsinfo userbasicsinfo = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		Userbasicsinfo user = userbasicsinfoService
				.queryUserById(userbasicsinfo.getId());
		request.setAttribute("u", user);
		request.setAttribute(Constant.SESSION_USER, user);

	}

	/**
	 * 身份证接口验证
	 * 
	 * @param request
	 * @param cardInfo
	 * @return
	 */
//	@RequestMapping("/checkCardId")
	@ResponseBody
	public String checkCardId(HttpServletRequest request, String initcard,
			String iname) {

		Userbasicsinfo userbasics = userbasicsinfoService
				.queryUserById(((Userbasicsinfo) request.getSession()
						.getAttribute(Constant.SESSION_USER)).getId());
		String result = (String) pycreditService.isRealIDCard(iname, initcard);
		if (Integer.parseInt(result) == 1) {
			boolean flag = userbasicsinfoService.queryUserByCardId(initcard);
			if (flag) {
				return "2";
			}
			userbasics.setCardStatus(2);
			userbasics.getUserrelationinfo().setCardId(initcard);
			userbasics.setName(iname);
			userbasics.getUserrelationinfo().setBirthDay(
					initcard.substring(6, 14)); // 获取出生日期
			userbasicsinfoService.update(userbasics);
			request.getSession()
					.setAttribute(Constant.SESSION_USER, userbasics);
			// 实名认证加10分
			integralSevice.realNameAuth(userbasics, IntegralType.REALNAME);
			// 是否有推广人，实名认证后，推广人可加20分
			Userbasicsinfo promoter = generalizeService
					.queryPromoterByPromotedId(userbasics.getId());
			if (promoter != null) {
				integralSevice.inviteFriend(promoter);
			}

			return "1";
		} else {
			return "0";

		}

	}

	/**
	 * 身份证接口验证 用于注册用户时使用
	 * 
	 * @param request
	 * @param cardInfo
	 * @return
	 */
//	@RequestMapping("/verifyID")
	@ResponseBody
	public String verifyID(HttpServletRequest request, String initcard,
			String iname) {

		String result = (String) pycreditService.isRealIDCard(iname, initcard);
		if (Integer.parseInt(result) == 1) {
			return "1";
		} else {
			return "0";

		}

	}

	/**
	 * 修改手机号
	 * 
	 * @param id
	 *            用户id
	 * @param newPhone
	 *            新手机号
	 * @param question01
	 *            问题1
	 * @param anwser01
	 *            答案1
	 * @param question02
	 *            问题2
	 * @param anwser02
	 *            答案2
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@RequestMapping("uptPhone")
	@ResponseBody
	public String uptPhone(String id, String newPhone, String question01,
			String anwser01, String question02, String anwser02,
			HttpServletRequest request) {
		boolean flag = myindexService.checkSafeQuestions(id, question01,
				anwser01, question02, anwser02);
		if (flag) {
			Userbasicsinfo user = userbasicsinfoService.queryUserById(Long
					.parseLong(id));
			user.getUserrelationinfo().setPhone(newPhone);
			userbasicsinfoService.update(user);
			request.getSession().setAttribute(Constant.SESSION_USER, user);
			integralSevice.phoneAuth(user, IntegralType.PHONE);
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 根据手机验证码和回答安全问题来修改邮箱
	 * 
	 * @param id
	 *            用户id
	 * @param smscode
	 *            手机验证码
	 * @param newemail
	 *            新邮箱
	 * @param question01
	 *            问题1
	 * @param anwser01
	 *            答案1
	 * @param question02
	 *            问题2
	 * @param anwser02
	 *            答案2
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@RequestMapping("uptEmail1")
	@ResponseBody
	public String uptEmail1(String id, String newemail, String question01,
			String anwser01, String question02, String anwser02,
			HttpServletRequest request) {
		Userbasicsinfo u = userbasicsinfoService.queryUserById(Long
				.parseLong(id));// 根据用户id查询用户
		boolean flag = myindexService.checkSafeQuestions(id, question01,
				anwser01, question02, anwser02);
		if (flag) {
			u.getUserrelationinfo().setEmail(newemail);
			u.getUserrelationinfo().setEmailisPass(0);
			userbasicsinfoService.update(u);
			request.getSession().setAttribute(Constant.SESSION_USER, u);
			return "1";
		} else {
			request.setAttribute("u",
					userbasicsinfoService.queryUserById(u.getId()));
			return "0";
		}
	}

	/**
	 * 根据编号删除系统消息
	 * 
	 * @param ids
	 *            要删除的系统消息的编号
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
//	@RequestMapping("/deletes")
	@ResponseBody
	public String deletes(
			@RequestParam(value = "ids", defaultValue = "", required = false) String ids,
			HttpServletRequest request) {
		try {
			// 删除系统消息
			memberCenterService.deleteInboxs(ids);
		} catch (Exception e) {
			LOG.info("删除系统消息错误" + e.getMessage());
			return "failed";
		}
		return "successed";
	}

	/**
	 * 读系统消息
	 * 
	 * @param id
	 *            消息id
	 * @param request
	 *            HttpServletRequest
	 * @return Map<String, Integer>
	 */
//	@RequestMapping("/readUMSG")
	@ResponseBody
	public Map<String, Integer> readUMSG(String id, HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		Map<String, Integer> map = new HashMap<String, Integer>();
		myindexService.read(Long.parseLong(id));
		// 查询用户已读消息条数
		Object read = memberCenterService.queryIsReadCount(user.getId(), 1);
		// 查询用户系统消息条数
		Object obj = memberCenterService.queryUserMessageCount(user.getId());
		map.put("count", Integer.parseInt(obj.toString()));
		map.put("read", Integer.parseInt(read.toString()));
		map.put("unRead",
				Integer.parseInt(obj.toString())
						- Integer.parseInt(read.toString()));
		return map;
	}

	/**
	 * 修改邮箱页面
	 * 
	 * @param id
	 *            用户id
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@RequestMapping("/forwardUptEmail")
	public String forwardUptEmail(String id, HttpServletRequest request) {
		if (Validate.emptyStringValidate(id)) {
			Userbasicsinfo user = userbasicsinfoService.queryUserById(Long
					.parseLong(StringUtil.correctPassword(id)));
			Object newEmail = request.getSession().getAttribute("newEmail");
			if (newEmail != null && !"".equals(newEmail)) {
				user.getUserrelationinfo().setEmail(newEmail.toString());
			}
			user.getUserrelationinfo().setEmailisPass(1);
			userbasicsinfoService.update(user);
			request.getSession().setAttribute("session_user", user);
			request.setAttribute("url", "/member_index/selfInfo.htm?index=0_4");
			return "WEB-INF/views/success";
		} else {
			return "WEB-INF/views/index";
		}
	}

	/**
	 * 安全认证
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return String
	 */
	@RequestMapping("safeVerify")
	public String safeVerify(HttpServletRequest request,
			HttpServletResponse response) {
		return "/WEB-INF/views/visitor/index";
	}

	/**
	 * 验证安全问题是否正确
	 * 
	 * @param id
	 *            用户id
	 * @param question01
	 *            安全问题1
	 * @param anwser01
	 *            答案1
	 * @param question02
	 *            安全问题2
	 * @param anwser02
	 *            答案2
	 */
	@RequestMapping("checkSafeQuestions")
	@ResponseBody
	public boolean checkSafeQuestions(String id, String question01,
			String anwser01, String question02, String anwser02) {
		return myindexService.checkSafeQuestions(id, question01, anwser01,
				question02, anwser02);
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

	/***
	 * 用户授权的
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("callcentralInAccredit.htm")
	public String callcentralInAccredit(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) request.getSession()
				.getAttribute("map");
		request.getSession().removeAttribute("map");
		request.setAttribute("map", map);
		return "WEB-INF/views/central_inAccredit";
	}

	/****
	 * 用户授权接口(页面)
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("ipsInAccreditUser.htm")
	@ResponseBody
	public String ipsInAccreditUser(HttpServletRequest request, String type) {
		return baoFuLoansignService.ipsInAccreditUserService(request, type);
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/toIpsRegister.htm")
	public String toIpsRegister(HttpServletRequest request) {

		Userbasicsinfo userbasics = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		Userbasicsinfo user = userbasicsinfoService.queryUserById(userbasics
				.getId());
		request.setAttribute("user", user);
		return "WEB-INF/views/member/IpsRegister";
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/toPayPassword")
	public String updatePayPwd(HttpServletRequest request) {
		return "WEB-INF/views/member/paypassword";
	}

}
