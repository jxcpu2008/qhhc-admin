package com.hc9.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.jfree.util.Log;
import org.springframework.stereotype.Service;

import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;
import com.hc9.dao.SmsEmailTemplateDao;
import com.hc9.dao.entity.EmailSendBox;
import com.hc9.dao.entity.SearchUser;
import com.hc9.dao.entity.SmsEmailPlanTime;
import com.hc9.dao.entity.SmsEmailTemplate;
import com.hc9.dao.entity.SmsSendBox;
import com.hc9.dao.entity.SmsemailSendPlan;
import com.hc9.dao.entity.SwitchControl;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class MessageManageService {

	@Resource
	private HibernateSupport dao;
	
	@Resource
	private SmsEmailTemplateDao smsEmailTemplateDao;

	/** 获取全局设置列表信息 */
	public List<SwitchControl> getSwitchList() {
		String sql = "select * from switchcontrol where switchStatus <> -1 and swithchType = 'email_sms_tip'";
		List<SwitchControl> scList = dao.findBySql(sql, SwitchControl.class);
		return scList;
	}

	/** 通过id获取开关设置 */
	public SwitchControl getSwitchControl(Long id) {
		return dao.get(SwitchControl.class, id);
	}

	/** 修改开关设置信息 */
	public void updateSwitchStatus(SwitchControl sc) {
		dao.update(sc);
		SmsEmailCache.setSmsEmailSwitchStatus(sc.getSwitchEnName(),
				sc.getSwitchStatus());
	}

	/**  获取短信发送计划列表 */
	@SuppressWarnings("rawtypes")
	public List getSmsSendPlanList(PageModel page, String type) {
		StringBuffer sql = new StringBuffer("select p.id,p.msgTitle,p.templateEnName,p.predictSendBeginTime,");
		sql.append("p.predictSendEndTime,p.sendStatus,");
		if ("1".equals(type)) {   // 短信
			sql.append("(select count(1) from smssendbox where sendPlanId = p.id and sendStatus=3),");
			sql.append("(select count(1) from smssendbox where sendPlanId = p.id and sendStatus=1)");
		} else {
			sql.append("(select count(1) from emailsendbox where sendPlanId = p.id and sendStatus=3),");
			sql.append("(select count(1) from emailsendbox where sendPlanId = p.id and sendStatus=1)");
		}
		sql.append(" from smsemailsendplan p where p.sendType = "+ type + " and p.sendStatus <> 0 order by p.id");
		String sqlCount = "select count(1) from smsemailsendplan p where p.sendType = "
				+ type + " and p.sendStatus <> 0 ";
		page.setTotalCount(dao.queryNumberSql(sqlCount).intValue());
		sql.append(" limit ")
				.append((page.getPageNum() - 1) * page.getNumPerPage())
				.append(",").append(page.getNumPerPage());
		List list = dao.findBySql(sql.toString());
		return list;
	}

	/** 获取草稿箱计划列表 */
	@SuppressWarnings("rawtypes")
	public List getDraftBox(PageModel page) {
		StringBuffer sql = new StringBuffer(
				"select p.id,p.msgTitle,p.templateEnName,p.predictSendBeginTime,"
						+ "p.predictSendEndTime,p.sendStatus,"
						+ "p.createTime,p.sendType,case when p.sendType=1 then (select count(1) from smssendbox where sendPlanId = p.id and sendStatus=1)"
						+ " else (select count(1) from emailsendbox where sendPlanId = p.id and sendStatus=1) end"
						+ " from smsemailsendplan p where p.sendStatus = 0 order by p.id desc");
		String sqlCount = "select count(1) from smsemailsendplan p where p.sendStatus = 0";
		page.setTotalCount(dao.queryNumberSql(sqlCount).intValue());
		sql.append(" limit ")
				.append((page.getPageNum() - 1) * page.getNumPerPage())
				.append(",").append(page.getNumPerPage());
		List list = dao.findBySql(sql.toString());
		return list;
	}

	/** 获取发送计划信息 by 计划id */
	public Object getAddPlanInfo(Long plan_id) {
		StringBuffer sql = new StringBuffer(
				"select p.msgTitle,p.templateEnName,p.templateContent, ");
		sql.append("p.sendType,t.sendBeginTime,t.sendEndTime,t.intervalTime,t.msgNum ");
		sql.append("from smsemailsendplan p join smsemailplantime t on");
		sql.append(" p.id=t.sendPlanId where p.id=?");
		return dao.findObjectBySql(sql.toString(), plan_id);
	}

	/** 根据计划主键id查询短信、邮件计划相关对象  */
	public SmsemailSendPlan querySmsemailSendPlanByPlanId(Long planId) {
		String sql = "select * from smsemailsendplan where id=?";
		List<SmsemailSendPlan> list = dao.findBySql(sql, SmsemailSendPlan.class, planId);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/** 根据发送计划id查询发送计划对应的时间信息 */
	public SmsEmailPlanTime querySmsEmailPlanTimeByPlanId(Long planId) {
		String sql = "select * from smsemailplantime where sendPlanId=?";
		List <SmsEmailPlanTime> list = dao.findBySql(sql, SmsEmailPlanTime.class, planId);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/** 获取收件人信息 相关信息 */
	public String getSmsSendboxReceiveInfo(Long planId, HttpServletRequest request) {
		String receiveName = "";
		String sql = "select u.name from smssendbox s, userbasicsinfo u " 
					+ "where s.sendPlanId=? and s.receiverUserId=u.id";
		List list = dao.findBySql(sql, planId);
		if(list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i++) {
				if(i >=5) {
					break;
				} else {
					if(i < 4) {
						receiveName += list.get(i) + ";";
					} else {
						receiveName += list.get(i);
					}
				}
			}
			receiveName += "等" + list.size() + "人";
		}
		request.setAttribute("userNum", list.size());
		request.setAttribute("notEmailNum", 0);
		return receiveName;
	}
	
	/** 获取邮件收件人相关信息  */
	public String getEmailSendboxReceiveInfo(Long planId, HttpServletRequest request) {
		String receiveName = "";
		String sql = "select u.name from emailsendbox e, userbasicsinfo u "
					+ "where e.sendPlanId=? and e.receiverUserId=u.id";
		List list = dao.findBySql(sql, planId);
		if(list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i++) {
				if(i >=5) {
					break;
				} else {
					if(i < 4) {
						receiveName += list.get(i) + ";";
					} else {
						receiveName += list.get(i);
					}
				}
			}
			receiveName += "等" + list.size() + "人";
		}
		request.setAttribute("userNum", list.size());
		request.setAttribute("notEmailNum", 0);
		return receiveName;
	}

	/** 通过id获取发送计划 */
	public SmsemailSendPlan getSmsemailSendPlan(Long id) {
		return dao.get(SmsemailSendPlan.class, id);
	}

	/** 修改发送计划信息 */
	public void updateSmsemailSendPlan(SmsemailSendPlan plan) {
		dao.update(plan);
		SmsEmailCache.setSmsEmailSendPlanStatus(plan.getId(), plan.getSendStatus());
	}

	/** 查询收件人 */
	public Object[] getSearchUser(HttpServletRequest request, SearchUser searchUser) {
		StringBuffer querySql = new StringBuffer("select userid,mobilephone,name,email");
		StringBuffer countSql = new StringBuffer("select count(*)");
		StringBuffer sql = new StringBuffer(" from investorstasinfo where isLock=0 ");
		if (searchUser != null) {
			/** 投 资 额，开始：loanfloor，结束：loanup */
			if(StringUtil.isNotBlank(searchUser.getLoanfloor())) {
				sql.append(" and investMoney>=").append(searchUser.getLoanfloor());
			}
			
			if(StringUtil.isNotBlank(searchUser.getLoanup())) {
				sql.append(" and investMoney<=").append(searchUser.getLoanup());
			}
			
			/** 投资时间，开始：loanTimeFloor，结束：loanTimeup */
			if(StringUtil.isNotBlank(searchUser.getLoanTimeFloor()) || StringUtil.isNotBlank(searchUser.getLoanTimeup())) {
				if(StringUtil.isNotBlank(searchUser.getLoanTimeFloor())) {
					sql.append(" and userid in(select lr.userbasicinfo_id from loanrecord lr where lr.isSucceed=1 ")
					.append("and lr.tenderTime>='").append(searchUser.getLoanTimeFloor()).append("' ");
				}
				
				if(StringUtil.isNotBlank(searchUser.getLoanTimeup())) {
					sql.append("and lr.tenderTime<='").append(searchUser.getLoanTimeup()).append("' ");
				}
				sql.append(")");
			}
			
			/** 投资次数：开始：loanCountFloor，结束：loanCountUp */
			if(searchUser.getLoanCountFloor() != null) {
				sql.append(" and investNum>=").append(searchUser.getLoanCountFloor().intValue());
			}
			
			if(searchUser.getLoanCountUp() != null) {
				sql.append(" and investNum<=").append(searchUser.getLoanCountUp().intValue());
			}
			
			/** 注册时间，开始：regTimeFoolr，结束：regTimeUp */
			if(StringUtil.isNotBlank(searchUser.getRegTimeFoolr())) {
				sql.append(" and registTime>='").append(searchUser.getRegTimeFoolr()).append("'");
			}
			
			if(StringUtil.isNotBlank(searchUser.getRegTimeUp())) {
				sql.append(" and registTime<='").append(searchUser.getRegTimeUp()).append("'");
			}
			
			/** 活跃度:1、近一周内登录过2、 近一周内没有登录过 3、近一个月内登录过  4、近一个月内没有登录过 */
			if(searchUser.getLiveness() != null) {
				Integer liveness = searchUser.getLiveness();
				Date now = new Date();
				String currentTime = DateFormatUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss");
				String currentDate = currentTime.substring(0, 10);
				Date endDate = DateFormatUtil.stringToDate(currentDate, "yyyy-MM-dd");//结束59
				String endDateTime = currentDate + " 23:59:59";
				if(liveness.intValue() == 1) {//一周内登录过
					Date beginDate = DateFormatUtil.increaseDay(endDate, -6);//开始00；
					String beginDateTime = DateFormatUtil.dateToString(beginDate, "yyyy-MM-dd") + " 00:00:00";
					sql.append(" and latestLoginTime>='").append(beginDateTime).append("'");
					sql.append(" and latestLoginTime<='").append(endDateTime).append("'");
				}
				
				if(liveness.intValue() == 2) {//近一周内没有登录过
					Date beginDate = DateFormatUtil.increaseDay(endDate, -6);//开始00；
					String beginDateTime = DateFormatUtil.dateToString(beginDate, "yyyy-MM-dd") + " 00:00:00";
					sql.append(" and latestLoginTime<'").append(beginDateTime).append("'");
				}
				
				if(liveness.intValue() == 3) {//近30内登录过
					Date beginDate = DateFormatUtil.increaseDay(endDate, -29);//开始00；
					String beginDateTime = DateFormatUtil.dateToString(beginDate, "yyyy-MM-dd") + " 00:00:00";
					sql.append(" and latestLoginTime>='").append(beginDateTime).append("'");
					sql.append(" and latestLoginTime<='").append(endDateTime).append("'");
				}
				
				if(liveness.intValue() == 4) {//近一个月内没有登录过
					Date beginDate = DateFormatUtil.increaseDay(endDate, -29);//开始00；
					String beginDateTime = DateFormatUtil.dateToString(beginDate, "yyyy-MM-dd") + " 00:00:00";
					sql.append(" and latestLoginTime<'").append(beginDateTime).append("'");
				}
			}
		}

		request.getSession().setAttribute("searchUserUrl", querySql.append(sql).toString());
		
		Object[] objarray = new Object[3];
		Object totalNum = null;
		String totalNumSql = countSql.append(sql).toString();
		totalNum = dao.findObjectBySql(totalNumSql);//总记录数

		/** 计算没有邮箱的记录数*/
		String emailNullSql = totalNumSql + " and email is null";
		Object emailNull = dao.findObjectBySql(emailNullSql);
		
		String phoneNullSql = totalNumSql + " and mobilephone is null";
		Object phoneNull = dao.findObjectBySql(phoneNullSql);
		objarray[0] = totalNum;
		objarray[1] = emailNull;
		objarray[2] = phoneNull;
		return objarray;
	}

	public List getSearchUserList(String sql) {
		List list = dao.findBySql(sql);
		return list;
	}

	/** 保存计划
	 *  */
	public Serializable saveplan(SmsemailSendPlan sp) {
		Serializable seria = dao.save(sp);
		return seria;
	}

	public boolean saveplanTime(SmsEmailPlanTime st) {
		boolean b = false;
		try {
			dao.save(st);
			b = true;
		} catch (Exception e) {
			Log.error("保存发送计划时报错！", e);
			b = false;
		}
		return b;
	}

	/** 保存邮件或短信收件箱列表信息 */
	public void saveSmsEmailBoxList(SmsemailSendPlan sp, List list) {
		for (int i = 0; i < list.size(); i++) {
			Object[] obj = (Object[]) list.get(i);
			if(sp.getSendType() == 1) {
				SmsSendBox smsSendBox = new SmsSendBox();
				smsSendBox.setSendPlanId(sp.getId());
				smsSendBox.setReceiverUserId(Long.valueOf(String.valueOf(obj[0])));
				smsSendBox.setReceiverPhone(String.valueOf(obj[1]));
				smsSendBox.setSmsContent(sp.getTemplateContent());
				smsSendBox.setSendStatus(1);
				smsSendBox.setCreateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
				dao.save(smsSendBox);
			} else if (sp.getSendType() == 2) {
				EmailSendBox emailSendBox = new EmailSendBox();
				emailSendBox.setSendPlanId(sp.getId());
				emailSendBox.setReceiverUserId(Long.valueOf(String.valueOf(obj[0])));
				emailSendBox.setReceiverEmail(String.valueOf(obj[3]));
				emailSendBox.setEmailContent(sp.getTemplateContent());
				emailSendBox.setSendStatus(1);
				emailSendBox.setCreateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
				dao.save(emailSendBox);
			}
		}
	}
	
	/** 修改短信邮件发送计划
	 *  @param planType 保存的类型：1、保存为发送计划；2、保存为发送计划的草稿；
	 *   */
	public Map<String, String> updateSmsemailSendPlanRelInfo(HttpServletRequest request, Long planId, 
			SmsemailSendPlan sp, Long planTimeId, 
			SmsEmailPlanTime st, Integer planType, String userChangeFlag) {
		Map<String, String> resultMap = new HashMap<String, String> ();
		String code = "-1";
		String msg = "后台异常，修改失败！";
		try{
			SmsEmailTemplate smsEmailTemplate = smsEmailTemplateDao.querySmsEmailTemplate(
					sp.getSendType(), sp.getTemplateType(), sp.getTemplateEnName());
			if(smsEmailTemplate != null) {
				// 保存计划
				if (planType == 1) {
					// 提交
					sp.setSendStatus(1);
				} else if (planType == 2) {
					// 草稿
					sp.setSendStatus(0);
				}

				String sendBeginTime = st.getSendBeginTime().substring(0, 13) + ":00:00";
				String sendEndTime = st.getSendEndTime().substring(0, 13) + ":59:59";
				sp.setPredictSendBeginTime(sendBeginTime);
				sp.setPredictSendEndTime(sendEndTime);
				sp.setMsgTitle(smsEmailTemplate.getTemplateTitle());
				sp.setTemplateContent(smsEmailTemplate.getTemplateContent());
				sp.setId(planId);
				dao.update(sp);
				
				// 计划时间
				st.setId(planTimeId);
				st.setSendPlanId(Long.valueOf(planId));
				st.setCreateTime(sp.getCreateTime());
				st.setSendBeginTime(sendBeginTime);
				st.setSendEndTime(sendEndTime);
				dao.update(st);

				if("1".equals(userChangeFlag)) {//如果人员信息没有修改，则不需要查询用户列表
					// 查询收件人
					String url = (String) request.getSession().getAttribute("sureUrl");
					if(StringUtil.isNotBlank(url)) {
						if(sp.getSendType() == 1) {//短信
							url = url + " and mobilephone is not null";
						} else if (sp.getSendType() == 2) {
							url += " and email is not null";
						}
						String sql = "delete from smssendbox where sendPlanId=?";
						dao.executeSql(sql, planId);
						sql = "delete from emailsendbox where sendPlanId=?";
						dao.executeSql(sql, planId);
						List list = getSearchUserList(url);
						saveSmsEmailBoxList(sp, list);
					}
				}
				SmsEmailCache.setSmsEmailSendPlanStatus(sp.getId(), sp.getSendStatus());
				code = "0";
				msg = "修改计划成功！";
			} else {
				msg = "当前所选择的模板尚未为添加！";
			}
		} catch(Exception e) {
			LOG.error("修改异常失败！", e);
		}
		resultMap.put("code", code);
		resultMap.put("msg", msg);
		return resultMap;
	}
	
	/** 复制消息发送计划 */
	public void copyMsgSendplan(HttpServletRequest request, Long planId) {
		SmsemailSendPlan plan = querySmsemailSendPlanByPlanId(planId);
		SmsEmailPlanTime time = querySmsEmailPlanTimeByPlanId(planId);
		String createTime = DateUtils.format("yyyy-MM-dd HH:mm:ss");
		SmsemailSendPlan newSmsemailSendPlan = new SmsemailSendPlan();
		newSmsemailSendPlan.setCreateTime(createTime);
		newSmsemailSendPlan.setMsgTitle(plan.getMsgTitle());
		newSmsemailSendPlan.setPredictSendBeginTime(plan.getPredictSendBeginTime());
		newSmsemailSendPlan.setPredictSendEndTime(plan.getPredictSendEndTime());
		newSmsemailSendPlan.setSendStatus(0);
		newSmsemailSendPlan.setSendType(plan.getSendType());
		newSmsemailSendPlan.setTemplateContent(plan.getTemplateContent());
		newSmsemailSendPlan.setTemplateEnName(plan.getTemplateEnName());
		newSmsemailSendPlan.setTemplateType(plan.getTemplateType());
		Long newPlanId = (Long)dao.save(newSmsemailSendPlan);
		
		SmsEmailPlanTime newSmsEmailPlanTime = new SmsEmailPlanTime();
		newSmsEmailPlanTime.setCreateTime(createTime);
		newSmsEmailPlanTime.setIntervalTime(time.getIntervalTime());
		newSmsEmailPlanTime.setMsgNum(time.getMsgNum());
		newSmsEmailPlanTime.setSendBeginTime(time.getSendBeginTime());
		newSmsEmailPlanTime.setSendEndTime(time.getSendEndTime());
		newSmsEmailPlanTime.setSendPlanId(newPlanId);
		dao.save(newSmsEmailPlanTime);
		
		if("1".equals("" + newSmsemailSendPlan.getSendType())) {
			copySmsReceiverList(newPlanId, planId, createTime);
		} else if("2".equals("" + newSmsemailSendPlan.getSendType())) {
			copyEmailReceiverList(newPlanId, planId, createTime);
		}
		
		String receiveName = "";
		if("1".equals("" + newSmsemailSendPlan.getSendType())) {
			receiveName = getSmsSendboxReceiveInfo(newPlanId, request);
		} else if("2".equals("" + newSmsemailSendPlan.getSendType())) {
			receiveName = getEmailSendboxReceiveInfo(newPlanId, request);
		}
		List<SwitchControl> oneLevelTypeList = smsEmailTemplateDao.queryOneLevelSwitchList("sms_email_template_type");
		/** 二级类型下拉框  */
		List<SwitchControl> twoLevelTypeList = smsEmailTemplateDao.
				queryTemplateListByUpSwitchEnName(newSmsemailSendPlan.getTemplateType());
		request.setAttribute("oneLevelTypeList", oneLevelTypeList);
		request.setAttribute("twoLevelTypeList", twoLevelTypeList);
		request.setAttribute("smsemailSendPlan", newSmsemailSendPlan);
		request.setAttribute("smsEmailPlanTime", newSmsEmailPlanTime);
		// 获取发送计划信息
		request.setAttribute("receiveName", receiveName);
		request.setAttribute("plan", getAddPlanInfo(planId));
	}
	
	/** 复制短信接收人列表 */
	public void copySmsReceiverList(Long planId, Long oldPlanId, String createTime) {
		String sql = "select * from smssendbox where sendPlanId=?";
		List<SmsSendBox> list = dao.findBySql(sql, SmsSendBox.class, oldPlanId);
		if(list != null && list.size() > 0) {
			for(SmsSendBox vo : list) {
				SmsSendBox box = new SmsSendBox();
				box.setCreateTime(createTime);
				box.setReceiverPhone(vo.getReceiverPhone());
				box.setReceiverUserId(vo.getReceiverUserId());
				box.setRemark(vo.getRemark());
				box.setSendPlanId(planId);
				box.setSendStatus(1);
				box.setSmsContent(vo.getSmsContent());
				dao.save(box);
			}
		}
	}
	
	/** 复制邮件接收人列表 */
	public void copyEmailReceiverList(Long planId, Long oldPlanId, String createTime) {
		String sql = "select * from emailsendbox where sendPlanId=?";
		List<EmailSendBox> list = dao.findBySql(sql, EmailSendBox.class, oldPlanId);
		if(list != null && list.size() > 0) {
			for(EmailSendBox vo : list) {
				EmailSendBox box = new EmailSendBox();
				box.setCreateTime(createTime);
				box.setEmailContent(vo.getEmailContent());
				box.setReceiverEmail(vo.getReceiverEmail());
				box.setReceiverUserId(vo.getReceiverUserId());
				box.setSendPlanId(planId);
				box.setSendStatus(1);
				dao.save(box);
			}
		}
	}
}