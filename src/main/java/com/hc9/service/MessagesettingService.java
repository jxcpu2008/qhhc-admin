package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.AdminMessage;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Messagesetting;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Usermessage;
import com.hc9.dao.entity.Userrelationinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * 消息设置Service
 * 
 * @author frank 2014-9-5
 */
@Service
@SuppressWarnings("rawtypes")
public class MessagesettingService {

	/** dao */
	@Resource
	private HibernateSupport dao;

	/** emailService email服务层 */
	@Resource
	private EmailService emailService;

	@Resource
	private SmsService sendSMSUtil;

	/**
	 * <p>
	 * Title: sendMessagetting
	 * </p>
	 * <p>
	 * Description: 发送消息
	 * </p>
	 * 
	 * @param userrelationinfo
	 *            用户
	 * @param typeId
	 *            消息类型
	 * @param content
	 *            消息类容
	 * @param title
	 *            站内消息标题
	 * @return 是否成功
	 */
	public boolean sendMessagetting(Userrelationinfo userrelationinfo,
			Long typeId, String content, String title) {
		// SendSMSUtil sendSMSUtil=new SendSMSUtil();
		StringBuffer sb = new StringBuffer(
				"SELECT * from  messagesetting where messagetype_id=")
				.append(typeId).append(" and user_id=")
				.append(userrelationinfo.getUserbasicsinfo().getId());
		List<Messagesetting> list = dao.findBySql(sb.toString(),
				Messagesetting.class, null);
		if (list != null && list.size() > 0) {
			Messagesetting mes = list.get(0);
			// 判断是否需要发送邮件
			if (mes.getEmailIsEnable()) {
				// 发送邮件
				emailService.sendEmail(title, content,
						userrelationinfo.getEmail());
			}
			if (mes.getSmsIsEnable()) {
				// 发送短信
				try {
					sendSMSUtil.sendSMS(content, userrelationinfo.getPhone());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (mes.getSysIsEnable()) {
				// 添加站内消息
				Usermessage umes = new Usermessage();
				umes.setContext(content);
				umes.setIsread(0);
				umes.setUserbasicsinfo(userrelationinfo.getUserbasicsinfo());
				umes.setTitle(title);
				umes.setReceivetime(DateUtils
						.format(Constant.DEFAULT_TIME_FORMAT));
				dao.save(umes);
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Title: sendBackMessaget
	 * </p>
	 * <p>
	 * Description: 回款时给投资者发送消息
	 * </p>
	 * 
	 * @param loansign
	 *            借款标
	 * @param content
	 *            内容
	 * @param title
	 *            主题
	 * @return 是否成功
	 */
	public boolean sendBackMessaget(Loansign loansign, String content,
			String title) {

		StringBuffer sb = new StringBuffer(
				"SELECT * from userrelationinfo where user_id in(SELECT DISTINCT userbasicinfo_id from loanrecord where loanSign_id=")
				.append(loansign.getId()).append(" )");
		List<Userrelationinfo> list = dao.findBySql(sb.toString(),
				Userrelationinfo.class, null);
		if (list != null && list.size() > 0) {
			Userrelationinfo userrelationinfo = list.get(0);
			// 发送邮件
			emailService.sendEmail(title, content, userrelationinfo.getEmail());
			// 发送短信
			// sendSMSUtil.sendSMS(client, ext, content, telNos)
			// 添加站内消息
			Usermessage umes = new Usermessage();
			umes.setContext(content);
			umes.setIsread(0);
			umes.setUserbasicsinfo(userrelationinfo.getUserbasicsinfo());
			umes.setTitle(title);
			umes.setReceivetime(DateUtils.format(Constant.DEFAULT_TIME_FORMAT));
			dao.save(umes);
		}
		return true;
	}

	/**
	 * 提交申请时，通知客服人员
	 */
	public void alertAdminuser(Userbasicsinfo userbasics, String context,
			String title) {
		String sql = "SELECT adminuser_id from userrelationinfo where user_id="
				+ userbasics.getId();
		Object id=this.dao.findBySql(sql);
		String adminids=id.toString().substring(1, id.toString().length()-1);
		String adminuser = adminids.equals(null) ? "2" : adminids + ",2";
		AdminMessage message = new AdminMessage();
		message.setAdminuserId(adminuser);
		message.setContext(context);
		message.setTitle(title);
		message.setIsread(0);
		message.setReceivetime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		dao.save(message);
	}

	/**a
	 * 获取当前客服用户的消息
	 * 
	 * @param userid
	 *            客服id
	 * @param limit
	 * @param start
	 * @return
	 */
	public List getMessages(String userid, PageModel page) {
		String sql = "select a.id,a.title,a.context,a.receivetime,a.isread from adminmessage a where a.adminuser_id like '%"
				+ userid + "%' ORDER BY a.receivetime DESC";

		String sqlCount = "select count(a.id) from adminmessage a where a.adminuser_id like '%"
				+ userid + "%'";
		List list = dao.pageListBySql(page, sqlCount, sql, null);
		return list;
	}

	/**
	 * 查询消息是否已读
	 * 
	 * @param ids
	 *            消息编号
	 * @return 查询结果
	 */
	public boolean notRead(String ids) {
		StringBuffer sqlcount = new StringBuffer(
				"SELECT count(1) from adminmessage where isread=1 and id in (")
				.append(ids.substring(0, ids.length() - 1)).append(")");
		Object obj = dao.findObjectBySql(sqlcount.toString());
		if (Integer.parseInt(obj.toString()) > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 标记消息为已读
	 * 
	 * @param ids
	 *            消息id
	 * @return
	 */
	public boolean read(String ids) {
		if ("all".equals(ids)) {
			StringBuffer updatesql = new StringBuffer(
					"update adminmessage set isread=1");
			if (dao.executeSql(updatesql.toString()) <= 0) {
				return false;
			}
		} else if (ids.length() > 0) {
			StringBuffer updatesql = new StringBuffer(
					"update adminmessage set isread=1 where id in (").append(
					ids.substring(0, ids.length() - 1)).append(")");
			if (dao.executeSql(updatesql.toString()) <= 0) {
				return false;
			}
		}
		return true;
	}
}
