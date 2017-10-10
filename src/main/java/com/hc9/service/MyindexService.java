package com.hc9.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pomo.web.page.model.Page;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.FileUtil;
import com.hc9.common.util.GenerateLinkUtils;
import com.hc9.dao.entity.Securityproblem;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Usermessage;
import com.hc9.dao.entity.Verifyproblem;
import com.hc9.dao.impl.HibernateSupport;

import freemarker.template.TemplateException;

/**
 * 会员中心
 * 
 * @author My_Ascii
 * 
 */
@Service
public class MyindexService {

	/**
	 * 注入HibernateSupport
	 */
	@Resource
	private HibernateSupport dao;

	/**
	 * 注入EmailService
	 */
	@Resource
	private EmailService emailService;

	private UserbasicsinfoService userbasicsinfoService;

	/**
	 * 保存会员的安全问题
	 * 
	 * @param securityproblem
	 *            Securityproblem
	 */
	public void saveProblem(Securityproblem securityproblem) {
		dao.save(securityproblem);
	}

	/**
	 * 查询安全问题
	 * 
	 * @param id
	 *            安全问题的id
	 * @return Verifyproblem
	 */
	public Verifyproblem queryVerifyproblem(Long id) {
		return dao.get(Verifyproblem.class, id);
	}

	/**
	 * 根据会员id查询该会员的安全问题
	 * 
	 * @param id
	 *            会员id
	 * @return List<Securityproblem>
	 */
	public List<Securityproblem> querySecurityproblem(Long id) {
		return dao.find("from Securityproblem where userbasicsinfo.id = " + id);
	}

	/**
	 * 根据会员id查询该会员的安全问题
	 * 
	 * @param id
	 *            会员id
	 * @return List<Securityproblem>
	 */
	public List<Object[]> appquerySecurityproblem(Long id) {
		return dao
				.find("from Securityproblem s  left join s.verifyproblem left join  s.userbasicsinfo u where u.id = "
						+ id);
	}

	/**
	 * 
	 * @param id
	 *            用户id
	 * @param vid
	 *            问题id
	 * @param answer
	 *            答案
	 * @return List<Securityproblem>
	 */
	public List<Securityproblem> querySecurityproblem(long id, long vid,
			String answer) {
		return dao.find("from Securityproblem where userbasicsinfo.id = " + id
				+ " and verifyproblem.id = " + vid + " and answer = '" + answer
				+ "'");
	}

	/**
	 * 根据用户id查询该用户的短信验证码
	 * 
	 * @param uid
	 *            用户id
	 * @return List
	 */
	public List queryValicodeByUserId(long uid) {
		return dao
				.findBySql(
						"SELECT smsoverTime, smsCode from validcodeinfo where user_id = ?",
						uid);
	}

	/**
	 * 登陆日志查询
	 * 
	 * @param page
	 *            Page
	 * @param uid
	 *            会员id
	 * @return List
	 */
	public List queryLog(Page page, long uid) {
		return dao
				.pageListByHql(
						page,
						"SELECT a.id,a.logintime,a.ip FROM Userloginlog a WHERE a.userbasicsinfo.id=?",
						false, uid);
	}

	/**
	 * 修改
	 * 
	 * @param obj
	 *            Object
	 */
	public void updateObject(Object obj) {
		dao.update(obj);
	}

	/**
	 * 读消息后修改该消息的状态为已读（1）
	 * 
	 * @param id
	 *            消息编号
	 */
	public void read(long id) {
		Usermessage umsg = dao.get(Usermessage.class, id);
		umsg.setIsread(1);
		dao.save(umsg);
	}

	/**
	 * 手机认证--重载，针对手机app
	 * 
	 * @param phone
	 *            手机号码添加用户id手机端没有session
	 * @param smscode
	 *            手机验证码
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public String verifyPhone(String phone, String smscode, Long uid,
			HttpServletRequest request) {
		/*
		 * Userbasicsinfo u = queryUserinfo(((Userbasicsinfo)
		 * request.getSession() .getAttribute(Constant.SESSION_USER)).getId());
		 */
		Userbasicsinfo u = userbasicsinfoService.queryUserById(uid);

		List valicode = queryValicodeByUserId(u.getId());
		Object[] valicodes = (Object[]) ((List) valicode).get(0);
		if (smscode.equals(valicodes[1])) {
			u.getUserrelationinfo().setPhone(phone);
			userbasicsinfoService.update(u);
			request.getSession().setAttribute(Constant.SESSION_USER, u);
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 手机认证
	 * 
	 * @param phone
	 *            手机号码添加用户id手机端没有session
	 * @param smscode
	 *            手机验证码
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public String verifyPhone(String phone, String smscode,
			HttpServletRequest request) {
		Userbasicsinfo u = userbasicsinfoService
				.queryUserById(((Userbasicsinfo) request.getSession()
						.getAttribute(Constant.SESSION_USER)).getId());
		List valicode = queryValicodeByUserId(u.getId());
		Object[] valicodes = (Object[]) ((List) valicode).get(0);
		if (smscode.equals(valicodes[1])) {
			u.getUserrelationinfo().setPhone(phone);
			userbasicsinfoService.update(u);
			request.getSession().setAttribute(Constant.SESSION_USER, u);
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 安全问题认证/修改安全问题
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
	public String verify_question(String question01, String anwser01,
			String question02, String anwser02, HttpServletRequest request) {

		Userbasicsinfo u = userbasicsinfoService
				.queryUserById(((Userbasicsinfo) request.getSession()
						.getAttribute(Constant.SESSION_USER)).getId());

		request.setAttribute("u", u);
		List<Securityproblem> list = querySecurityproblem(u.getId());

		// 第一个问题
		Securityproblem s1 = new Securityproblem();
		// 第二个问题
		Securityproblem s2 = new Securityproblem();
		try {
			if (list.size() > 0) {
				s1 = list.get(0);
				s2 = list.get(1);
				// 修改第一个问题和答案
				s1.setAnswer(anwser01);
				s1.setVerifyproblem(queryVerifyproblem(Long
						.parseLong(question01)));
				updateObject(s1);
				// 修改第二个问题和答案
				s2.setAnswer(anwser02);
				s2.setVerifyproblem(queryVerifyproblem(Long
						.parseLong(question02)));
				updateObject(s2);
				return "1";
			} else {
				// 保存第一个问题和答案
				s1.setAnswer(anwser01);
				s1.setUserbasicsinfo(u);
				s1.setVerifyproblem(queryVerifyproblem(Long
						.parseLong(question01)));
				saveProblem(s1);
				// 保存第二个问题和答案
				s2.setAnswer(anwser02);
				s2.setUserbasicsinfo(u);
				s2.setVerifyproblem(queryVerifyproblem(Long
						.parseLong(question02)));
				saveProblem(s2);
				return "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}

	}

	/**
	 * 安全问题认证/修改安全问题
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

	public String appverify_question(String question01, String anwser01,
			String question02, String anwser02, Long uid,
			HttpServletRequest request) {

		/*
		 * Userbasicsinfo u = queryUserinfo(((Userbasicsinfo)
		 * request.getSession() .getAttribute(Constant.SESSION_USER)).getId());
		 */
		Userbasicsinfo u = userbasicsinfoService.queryUserById(uid);
		request.setAttribute("u", u);
		List<Securityproblem> list = querySecurityproblem(u.getId());
		// 第一个问题
		Securityproblem s1 = new Securityproblem();
		// 第二个问题
		Securityproblem s2 = new Securityproblem();
		try {
			if (list.size() > 0) {
				s1 = list.get(0);
				s2 = list.get(1);
				// 修改第一个问题和答案
				s1.setAnswer(anwser01);
				s1.setVerifyproblem(queryVerifyproblem(Long
						.parseLong(question01)));
				updateObject(s1);
				// 修改第二个问题和答案
				s2.setAnswer(anwser02);
				s2.setVerifyproblem(queryVerifyproblem(Long
						.parseLong(question02)));
				updateObject(s2);
				return "1";
			} else {
				// 保存第一个问题和答案
				s1.setAnswer(anwser01);
				s1.setUserbasicsinfo(u);
				s1.setVerifyproblem(queryVerifyproblem(Long
						.parseLong(question01)));
				saveProblem(s1);
				// 保存第二个问题和答案
				s2.setAnswer(anwser02);
				s2.setUserbasicsinfo(u);
				s2.setVerifyproblem(queryVerifyproblem(Long
						.parseLong(question02)));
				saveProblem(s2);
				return "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}

	}

	/**
	 * 安全问题认证/修改安全问题
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
	 * @return String 需要的是问题的值验证 备用手机端的自定义问题验证 上传的参数为问题
	 */

	public String appverify_question1(String question01, String anwser01,
			String question02, String anwser02, Long uid,
			HttpServletRequest request) {

		/*
		 * Userbasicsinfo u = queryUserinfo(((Userbasicsinfo)
		 * request.getSession() .getAttribute(Constant.SESSION_USER)).getId());
		 */
		Userbasicsinfo u = userbasicsinfoService.queryUserById(uid);
		request.setAttribute("u", u);
		List<Securityproblem> list = querySecurityproblem(u.getId());
		List<Verifyproblem> res;
		res = (List<Verifyproblem>) dao.find(
				"from bean.Verifyproblem v where v.problem=?", question01);
		Verifyproblem verifyproblem1 = new Verifyproblem();
		Verifyproblem verifyproblem2 = new Verifyproblem();
		if (null == res) {
			verifyproblem1.setProblem(question01);
			dao.save(verifyproblem1.getClass());
		}
		res = (List<Verifyproblem>) dao.find(
				"from bean.Verifyproblem v where v.problem=?", question02);
		if (null == res) {
			verifyproblem2.setProblem(question02);
			dao.save(verifyproblem2.getClass());

		}
		// 第一个问题
		Securityproblem s1 = new Securityproblem();
		// 第二个问题
		Securityproblem s2 = new Securityproblem();
		try {
			if (list.size() > 0) {
				s1 = list.get(0);
				s2 = list.get(1);
				// 修改第一个问题和答案
				s1.setAnswer(anwser01);
				s1.setVerifyproblem(queryVerifyproblem(verifyproblem1.getId()));
				updateObject(s1);
				// 修改第二个问题和答案
				s2.setAnswer(anwser02);
				s2.setVerifyproblem(queryVerifyproblem(verifyproblem2.getId()));
				updateObject(s2);
				return "1";
			} else {
				// 保存第一个问题和答案
				s1.setAnswer(anwser01);
				s1.setUserbasicsinfo(u);
				s1.setVerifyproblem(queryVerifyproblem(verifyproblem1.getId()));
				saveProblem(s1);
				// 保存第二个问题和答案
				s2.setAnswer(anwser02);
				s2.setUserbasicsinfo(u);
				s2.setVerifyproblem(queryVerifyproblem(verifyproblem2.getId()));
				saveProblem(s2);
				return "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}

	}

	/**
	 * 上传文件图片
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException
	 *             异常
	 */
	public Map upload(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// 定义一个map来存放要返回的结果
		Map<String, Object> imgMap = new HashMap<String, Object>();
		// 上传的绝对路径
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		String matchPath = "";
		if (null != request.getSession().getAttribute(Constant.SESSION_USER)) {
			matchPath = "/upload/user/"
					+ ((Userbasicsinfo) request.getSession().getAttribute(
							Constant.SESSION_USER)).getId() + "/";
		} else {
			matchPath = "/upload/user/x/";
		}
		// 可上传类型
		String[] types = FileUtil.NORMAL_TYPES;
		// 执行上传操作
		File file = FileUtil.upload(request, realPath + matchPath, types,
				"file", DateUtils.format("yyyyMMddHHmmss"));
		if (file == null) {
			imgMap.put("msg", "上传失败！");// 操作失败,返回失败消息
			imgMap.put("res", "0");
		} else {
			imgMap.put("uploadTime", DateUtils.format("yyyy-MM-dd HH:mm:ss"));// 文件上传时间
			imgMap.put("msg", "上传成功！");// 操作成功,返回成功消息
			imgMap.put("imgurl", matchPath.substring(7) + file.getName());// 操作成功，返回文件路径
			imgMap.put("res", "1");// 操作状态 0-失败 1-成功
			imgMap.put("saveName", file.getName());// 文件保存名
		}
		request.setAttribute("imgmap", imgMap);
		return imgMap;
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
	public void sendResetEmail(Userbasicsinfo u, String email,
			HttpServletRequest request) throws IOException, TemplateException {
		String emailActiveUrl = GenerateLinkUtils.generateUptEmailLink(u, request);
		Map<String, String> map = new HashMap<String, String>();
		if (u.getCardStatus() == 2) {
			map.put("name", u.getName());
		} else {
			map.put("name", u.getUserName());
		}
		map.put("emailActiveUrl", emailActiveUrl);
		String[] msg = emailService.getEmailResources("bind-email.ftl",
				map);
		// 发送邮件链接地址
		emailService.sendEmail(msg[0], msg[1], email);
	}

	/**
	 * 发送激活邮件
	 * 
	 * @param u
	 *            用户基本信息
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException
	 *             异常
	 * @throws TemplateException
	 *             异常
	 */
	public void sendEmail(Userbasicsinfo u, HttpServletRequest request)
			throws IOException, TemplateException {
		// 收件人地址
		String address = u.getUserrelationinfo().getEmail();
		String userName = u.getName();

		String url = GenerateLinkUtils.generateActivateLink(u, request);
		Map<String, String> map = new HashMap<String, String>();
		if (userName == null || userName.equals("")) {
			map.put("name", "亲爱的用户");
		} else {
			map.put("name", userName);
		}

		map.put("emailActiveUrl", url);
		String[] msg = emailService.getEmailResources("account-activate.ftl",
				map);
		// 发送邮件链接地址
		emailService.sendEmail(msg[0], msg[1], address);
	}

	public void sendEmailYue(Userbasicsinfo u, HttpServletRequest request,
			String msgc) throws IOException, TemplateException {
		// 收件人地址
		String address = u.getUserrelationinfo().getEmail();
		String userName = u.getName();

		// String url = GenerateLinkUtils.generateActivateLink(u, request);
		Map<String, String> map = new HashMap<String, String>();
		/*
		 * if (userName == null || userName.equals("")) { map.put("name",
		 * "亲爱的用户"); } else { map.put("name", userName); }
		 */
		map.put("tan1", "消息：");
		map.put("tan2", msgc);
		// map.put("emailActiveUrl", url);
		String[] msg = emailService.getEmailResources("yuetan.ftl", map);
		// 发送邮件链接地址
		emailService.sendEmail("约谈", msg[1], address);
	}

	public Userbasicsinfo queryUserinfoemail(String username) {
		String sql = "from Userbasicsinfo u where u.userName=?";
		return (Userbasicsinfo) dao.findObject(sql, username);

	}

	/**
	 * 发送激活邮件
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public String replyMail(HttpServletRequest request, String username) {
		// Userbasicsinfo u = (Userbasicsinfo)
		// request.getSession().getAttribute(
		// Constant.SESSION_USER);
		// request.setAttribute("u", queryUserinfo(u.getId()));
		// u = queryUserinfo(u.getId());
		Userbasicsinfo u = queryUserinfoemail(username);
		try {
			// 取得邮箱激活链接再次发送的时间
			// Validcodeinfo validcode = (Validcodeinfo) dao.findObject(
			// "FROM Validcodeinfo v WHERE v.userbasicsinfo.id=?",
			// u.getId());
			Long time = System.currentTimeMillis();
			/*
			 * if (null != validcode.getEmailagaintime() && time <
			 * validcode.getEmailagaintime()) { return "2"; }
			 */
			// 发送激活邮件 并更新链接再次发送时间和失效时间
			sendEmail(u, request);
			// validcode.setEmailagaintime(time + 2 * 60 * 1000L);
			// validcode.setEmailovertime(time + 24 * 60 * 60 * 1000L);
			// dao.update(validcode);
			return "1";
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	public String sendMailYue(HttpServletRequest request, String email,
			String msg) {

		Userbasicsinfo u = queryUserinfoemail(email);
		try {

			sendEmailYue(u, request, msg);

			return "1";
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	/**
	 * 完成实名认证
	 * 
	 * @param name
	 *            用户名
	 * @param cardId
	 *            用户身份证
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public String identityValidateImpl(String name, String cardId,
			HttpServletRequest request) {
		Userbasicsinfo u = (Userbasicsinfo) request.getSession().getAttribute(
				Constant.SESSION_USER);
		u.setName(name);
		u.getUserrelationinfo().setCardId(cardId);
		try {
			userbasicsinfoService.update(u);
			request.getSession().setAttribute(Constant.SESSION_USER, u);
			return "1";
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	/**
	 * App完成实名认证
	 * 
	 * @param name
	 *            用户名
	 * @param cardId
	 *            用户身份证
	 * @param request
	 *            HttpServletRequest
	 * @param uid
	 *            用户编号
	 * @return String
	 */
	public String identityValidateImpl(String name, String cardId, Long uid,
			HttpServletRequest request) {
		Userbasicsinfo u = (Userbasicsinfo) request.getSession().getAttribute(
				Constant.SESSION_USER);
		u = userbasicsinfoService.queryUserById(uid);
		u.setName(name);
		u.getUserrelationinfo().setCardId(cardId);
		try {
			userbasicsinfoService.update(u);
			request.getSession().setAttribute(Constant.SESSION_USER, u);
			return "1";
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
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
	public boolean checkSafeQuestions(String id, String question01,
			String anwser01, String question02, String anwser02) {
		// 获取该用户的手机验证码
		List valicode = queryValicodeByUserId(Long.parseLong(id));
		Object[] valicodes = (Object[]) ((List) valicode).get(0);
		// 验证安全问题是否正确
		List list1 = querySecurityproblem(Long.parseLong(id),
				Long.parseLong(question01), anwser01);
		List list2 = querySecurityproblem(Long.parseLong(id),
				Long.parseLong(question02), anwser02);
		if (list1.size() > 0 && list2.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
