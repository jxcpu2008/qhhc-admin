package com.hc9.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.log.LOG;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.SmsEmailPlanTime;
import com.hc9.dao.entity.SmsemailSendPlan;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.EmaiLoansignInfo;
import com.hc9.model.MsgSendInfo;
import com.hc9.model.MsgSendPlanInfo;
import com.hc9.model.SmsMessagePojo;
import com.hc9.model.UserBirthdayLinkInfo;
import com.hc9.service.LoanManageService;

/** 短信邮件定时任务数据访问层 */

@Service
public class SmsEmailTimerDao {

	/** 注入数据库操作层 */
	@Resource
	private HibernateSupport dao;
	
	@Resource
	private LoanManageService loanManageService;
	
	/** 查询提前还款日期(预计还款日期5日之内的相关数据) */
	public List<SmsMessagePojo> queryRemindListBeforRepayDate(String beginDate, String endDate) {
		List<SmsMessagePojo> resultList = new ArrayList<SmsMessagePojo>();
		String sql = "select l.id,l.name,l.issueLoan,l.remonth,r.preRepayDate," + 
			"(r.money+r.preRepayMoney+r.middleMoney+r.middlePreRepayMoney+r.afterMoney+r.afterPreRepayMoney) as repayMoney " + 
			",l.userbasicinfo_id,IFNULL(r.id,-1) " + 
			"from repaymentrecord r,loansign l " + 
			"WHERE r.loanSign_id=l.id and r.repayState in(1,3) and l.status in(6,7) " + 
			"and r.preRepayDate>='" + beginDate + "' and r.preRepayDate<='" + endDate + "' order BY r.preRepayDate";
		List list = dao.findBySql(sql);
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				SmsMessagePojo vo = new SmsMessagePojo();
				vo.setLoansignId(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setLoansignName(StatisticsUtil.getStringFromObject(arr[1]));
				vo.setIssueLoan(Arith.round((BigDecimal)(arr[2]), 2).doubleValue());
				vo.setRemonth(StatisticsUtil.getIntegerFromObject(arr[3]));
				vo.setPreRepayDate(StatisticsUtil.getStringFromObject(arr[4]));
				vo.setRepayMoney(Arith.round((BigDecimal)(arr[5]), 2).doubleValue());
				vo.setLoanUserId(StatisticsUtil.getLongFromBigInteger(arr[6]));
				vo.setRepaymentrecordId(StatisticsUtil.getLongFromBigInteger(arr[7]));
				resultList.add(vo);
			}
		}
		return resultList;
	}
	
	/** 查询逾期还款日期相关数据(预计还款日期5日之内的相关数据) */
	public List<SmsMessagePojo> queryRemindListAfterRepayDate(String beginDate, String endDate) {
		List<SmsMessagePojo> resultList = new ArrayList<SmsMessagePojo>();
		String sql = "select l.id,l.name,l.issueLoan,l.remonth,r.preRepayDate," + 
			"(r.money+r.preRepayMoney+r.middleMoney+r.middlePreRepayMoney+r.afterMoney+r.afterPreRepayMoney) as repayMoney " + 
			",l.userbasicinfo_id,IFNULL(r.id,-1) " + 
			"from repaymentrecord r,loansign l " + 
			"WHERE r.loanSign_id=l.id and r.repayState in(1,3) and l.status in(6,7) " + 
			"and r.preRepayDate>='" + beginDate + "' and r.preRepayDate<'" + endDate + "' order BY r.preRepayDate";
		List list = dao.findBySql(sql);
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				SmsMessagePojo vo = new SmsMessagePojo();
				vo.setLoansignId(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setLoansignName(StatisticsUtil.getStringFromObject(arr[1]));
				vo.setIssueLoan(Arith.round((BigDecimal)(arr[2]), 2).doubleValue());
				vo.setRemonth(StatisticsUtil.getIntegerFromObject(arr[3]));
				vo.setPreRepayDate(StatisticsUtil.getStringFromObject(arr[4]));
				Double repayMoney = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)arr[5]);
				vo.setLoanUserId(StatisticsUtil.getLongFromBigInteger(arr[6]));
				vo.setRepaymentrecordId(StatisticsUtil.getLongFromBigInteger(arr[7]));
				Double penalty = 0.0;
				try {
					int timeNum = DateUtils.differenceDate("yyyy-MM-dd", DateUtils.format("yyyy-MM-dd"), vo.getPreRepayDate());
					penalty = loanManageService.overdueRepayment(repayMoney, Math.abs(timeNum));
				} catch (ParseException e) {
					LOG.error("日期转换异常：", e);
				}//滞纳金
				repayMoney = Arith.round(Arith.add(repayMoney, penalty), 2);
				vo.setRepayMoney(repayMoney);
				resultList.add(vo);
			}
		}
		return resultList;
	}
	
	/** 查询邮件发送计划相关
	 *  @param sendType 发送类型：1、短信；2、邮件；
	 *  */
	public List<MsgSendPlanInfo> querySmsEmailSendPlanList(int sendType) {
		List<MsgSendPlanInfo> sendPlanList = new ArrayList<MsgSendPlanInfo>();
		String sql = "select id,templateContent,msgTitle from smsemailsendplan where sendStatus in(1,2) and sendType=" + sendType;
		List list = dao.findBySql(sql);
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				Long sendPlanId = StatisticsUtil.getLongFromBigInteger(arr[0]);
				String templateContent = StatisticsUtil.getStringFromObject(arr[1]);
				String msgTitle = StatisticsUtil.getStringFromObject(arr[2]);
				if(StringUtil.isNotBlank(templateContent)) {
					MsgSendPlanInfo plan = new MsgSendPlanInfo();
					plan.setId(sendPlanId);
					plan.setTemplateContent(templateContent);
					plan.setMsgTitle(msgTitle);
					sendPlanList.add(plan);
				} else {
					LOG.error("发送计划 " + sendPlanId + " 对应的模板内容为空！");
				}
			}
		}
		return sendPlanList;
	}
	
	/** 查询短信、邮件发送计划下对应的收件人列表
	 *  @param sendPlanId 发送计划表主键id
	 *  @param sendType 发送类型：1、短信；2、邮件；
	 *   */
	public List<MsgSendInfo> queryReceiveUserListOfSendPlan(Long sendPlanId, int sendType) {
		List<MsgSendInfo> resultList = new ArrayList<MsgSendInfo>();
		String sql = "";
		if(sendType == 1) {
			sql = "select id,receiverPhone from smssendbox where sendStatus=1 and sendPlanId=?";
		} else if(sendType == 2) {
			sql = "select id,receiverEmail from emailsendbox where sendStatus=1 and sendPlanId=?";
		}
		if(StringUtil.isNotBlank(sql)) {
			SmsEmailPlanTime planTime = querySmsEmailPlanTimeListByPlanId(sendPlanId);
			if(planTime != null && planTime.getMsgNum() > 0) {
				sql += " limit 0," + planTime.getMsgNum();
			}
			List list = dao.findBySql(sql, sendPlanId);
			if(list != null && list.size() > 0) {
				for(Object obj : list) {
					Object[] arr = (Object[])obj;
					MsgSendInfo msgSendInfo = new MsgSendInfo();
					Long id = StatisticsUtil.getLongFromBigInteger(arr[0]);
					String smsEmailNo = StatisticsUtil.getStringFromObject(arr[1]);
					if(StringUtil.isNotBlank(smsEmailNo)) {
						msgSendInfo.setId(id);
						msgSendInfo.setSmsEmailNo(smsEmailNo);
						resultList.add(msgSendInfo);
					}
				}
			}
		}
		return resultList;
	}
	
	/** 修改邮件的发送状态为已发送 */
	public void updateEmailSendStatus(Long id, String successTime) {
		String sql = "update emailsendbox set sendStatus=3,successTime=? where id=?";
		dao.executeSql(sql, successTime, id);
	}
	
	/** 修改短信的发送状态为已发送 */
	public void updateSmsSendStatus(Long id, String successTime) {
		String sql = "update smssendbox set sendStatus=3,successTime=? where id=?";
		dao.executeSql(sql, successTime, id);
	}
	
	/** 检查发送的状态是否需要修改为发送成功 */
	public void checkAndUpdateSendPlanStatus(int type, Long planId) {
		String sql = "";
		if(1 == type) {
			sql = "select count(*) from smssendbox where sendStatus=1 and sendPlanId=?";
		} else if(2 == type) {
			sql = "select count(*) from emailsendbox where sendStatus=1 and sendPlanId=?";
		}
		
		/** 更新状态为发送完毕 */
		BigInteger total = (BigInteger)dao.findObjectBySql(sql, planId);
		if(total.longValue() < 1) {
			sql = "update smsemailsendplan set sendStatus=4 where id=?";
			dao.executeSql(sql, planId);
			SmsEmailCache.setSmsEmailSendPlanStatus(planId, 4);
		} else {//正在发送状态
			sql = "update smsemailsendplan set sendStatus=2 where id=?";
			dao.executeSql(sql, planId);
			SmsEmailCache.setSmsEmailSendPlanStatus(planId, 2);
		}
	}
	
	/** 根据发送计划的主键id获取发送计划信息 */
	public SmsemailSendPlan querySmsemailSendPlanByPlanId(Long planId) {
		String sql = "select * from smsemailsendplan where id=?";
		List<SmsemailSendPlan> planList = dao.findBySql(sql, SmsemailSendPlan.class, planId);
		if(planList != null && planList.size() > 0) {
			return planList.get(0);
		}
		return null;
	}
	
	/** 根据发送计划获取发送计划相关的时间信息:目前一个发送计划对应一个时间 */
	public SmsEmailPlanTime querySmsEmailPlanTimeListByPlanId(Long planId) {
		String sql = "select * from smsemailplantime where sendPlanId=?";
		List<SmsEmailPlanTime> planList = dao.findBySql(sql, SmsEmailPlanTime.class, planId);
		if(planList != null && planList.size() > 0) {
			return planList.get(0);
		}
		return null;
	}
	
	/** 查询数据库中所有用户的手机号列表 */
	public List<String> queryAllUserMobileList() {
		List<String> mobilePhoneList = new ArrayList<String>();
		String sql = "select r.phone from userbasicsinfo u, userrelationinfo r "
					+ " where u.isLock=0 and u.id=r.id and r.phonepass=1";
		List list = dao.findBySql(sql);
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				String phone = (String)obj;
				if(StringUtil.isNotBlank(phone)) {
					mobilePhoneList.add(phone.trim());
				}
			}
		}
		return mobilePhoneList;
	}
	
	/** 根据标id查询表记录信息 */
	public Loansign queryLoansignById(String id) {
		Loansign loansign = null;
		String sql = "select * from loansign where id=?";
		List<Loansign> loansignList = dao.findBySql(sql, Loansign.class, id);
		if(loansignList != null && loansignList.size() > 0) {
			loansign = loansignList.get(0);
		}
		return loansign;
	}
	
	/** 查询最新的三个新标 */
	public List<EmaiLoansignInfo> queryNewestThreeLoansign() {
		List<EmaiLoansignInfo> resultList = new ArrayList<EmaiLoansignInfo>();
		String sql = "select id,name,prio_rate,prio_aword_rate from " + 
				" (select * from loansign where status=1 and rest_money>0 order by id desc) t limit 0,3";
		List list = dao.findBySql(sql);
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				EmaiLoansignInfo loan = new EmaiLoansignInfo();
				loan.setLoansignId(StatisticsUtil.getLongFromBigInteger(arr[0]));
				loan.setLoansignName(StatisticsUtil.getStringFromObject(arr[1]));
				loan.setPrioRate(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)arr[2]));
				loan.setPrioAwordRate(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)arr[3]));
				double totalRate = Arith.round(Arith.add(loan.getPrioRate(),loan.getPrioAwordRate()), 2);
				loan.setTotalRate(Arith.mul(totalRate, 100));
				resultList.add(loan);
			}
		}
		return resultList;
	}
	
	/** 查询用户生日相关的手机号码列表 */
	public List<UserBirthdayLinkInfo> queryToNotifyMobilePhoeList() {
		String today = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd");
		List<UserBirthdayLinkInfo> resultList = new ArrayList<UserBirthdayLinkInfo>();
		String sql = "select t.id,t.phone,t.birth_day,u.name,t.cardId "
					+ " from userrelationinfo t, userbasicsinfo u "
					+ " where u.id=t.id and t.phonepass=1";
		List list = dao.findBySql(sql);
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				String cardId = StatisticsUtil.getStringFromObject(arr[4]);
				String birthday = StatisticsUtil.getStringFromObject(arr[2]);
				if(StringUtil.isBlank(birthday)) {
					birthday = getBirthdayFromCardId(cardId);
				}
				if(StringUtil.isNotBlank(birthday)) {
					if(birthday.substring(5, 10).equals(today.substring(5, 10))) {
						String mobilePhoe = StatisticsUtil.getStringFromObject(arr[1]);
						String userName = StatisticsUtil.getStringFromObject(arr[3]);
						if(StringUtil.isNotBlank(mobilePhoe) 
								&& StringUtil.isNotBlank(userName)) {
							UserBirthdayLinkInfo vo = new UserBirthdayLinkInfo();
							vo.setUserId(StatisticsUtil.getLongFromBigInteger(arr[0]));
							vo.setMobilePhoe(mobilePhoe);
							vo.setBirthday(birthday);
							vo.setUserName(userName);
							resultList.add(vo);
						}
					}
				}
			}
		}
	return resultList;
	}
	
	/** 查询用户生日相关的邮箱列表 */
	public List<UserBirthdayLinkInfo> queryToNotifyEmailList() {
		String today = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd");
		List<UserBirthdayLinkInfo> resultList = new ArrayList<UserBirthdayLinkInfo>();
		String sql = "select t.id,t.email,t.birth_day,u.name,t.cardId "
					+ " from userrelationinfo t, userbasicsinfo u "
					+ " where u.id=t.id and t.emailisPass=1";
			List list = dao.findBySql(sql);
			if(list != null && list.size() > 0) {
				for(Object obj : list) {
					Object[] arr = (Object[])obj;
					String cardId = StatisticsUtil.getStringFromObject(arr[4]);
					String birthday = StatisticsUtil.getStringFromObject(arr[2]);
					if(StringUtil.isBlank(birthday)) {
						birthday = getBirthdayFromCardId(cardId);
					}
					if(StringUtil.isNotBlank(birthday)) {
						if(birthday.substring(5, 10).equals(today.substring(5, 10))) {
							String email = StatisticsUtil.getStringFromObject(arr[1]);
							String userName = StatisticsUtil.getStringFromObject(arr[3]);
							if(StringUtil.isNotBlank(email) 
									&& StringUtil.isNotBlank(userName)) {
								UserBirthdayLinkInfo vo = new UserBirthdayLinkInfo();
								vo.setUserId(StatisticsUtil.getLongFromBigInteger(arr[0]));
								vo.setEmail(email);
								vo.setBirthday(birthday);
								vo.setUserName(userName);
								resultList.add(vo);
							}
						}
					}
				}
			}
		return resultList;
	}
	
	/** 从身份证号码中获取生日信息 */
	private String getBirthdayFromCardId(String cardId) {
		if(StringUtil.isNotBlank(cardId) && cardId.length() > 14) {
			try {
				String birthday = cardId.substring(6, 14);
				Date date = ymdsdf.parse(birthday);
				return sdf.format(date);
			} catch (ParseException e) {
				LOG.error("日期格式转换抛出异常" + cardId, e);
				return "";
			}
		} else {
			return "";
		}
	}
	
	SimpleDateFormat ymdsdf = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
}