package com.hc9.common.quartz;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hc9.common.util.StatisticsUtil;
import com.hc9.dao.entity.AppMessagePush;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.service.IMessagePushManageService;

/**
 * 消息推送定时器任务
 * @author Administrator
 *
 */
@Service("pushMessageTimer")
public class PushMessageTimer {
	
	private static final Logger logger = Logger.getLogger(PushMessageTimer.class);
	
//	private final static String HONGBAO_INSERT_SQL = " insert into usermessage(context, isPush, receivetime, expireTime, money, title, user_id) values (?, ?, ?, ?, ?, ?, ?) ";
	private final static String HONGBAO_INSERT_SQL = " insert into usermessage(context, isPush, receivetime, expireTime, money, title, user_id) values ";
//	private final static String INSERT_SQL = " insert into usermessage(context, isPush, receivetime, title, user_id) values (?, ?, ?, ?, ?) ";
	private final static String TICKET_INSERT_SQL = " insert into usermessage(context, isPush, receivetime, title, user_id) values ";
	
	@Value("${push.bonusExpire.notification}")
	private String bonusNotification;
	
	@Value("${push.ticketExpire.notification}")
	private String ticketNotification;
	
	@Value("${push.aliasType}")
	private String aliasType;
	
	@Autowired
	private IMessagePushManageService messagePushService;
	
	@Autowired
	private HibernateSupport dao;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public void interestTicketExpiredNotification(int day, Map customizedField) {
		interestTicketExpiredRemind(day, customizedField);
	}
	
	public void hongBaoExpiredNotification(int day, Map customizedField) {
		hongBaoExpiredRemind(day, customizedField);
	}
	
	private void hongBaoExpiredRemind(int days, Map customizedField) {
		
		logger.debug("提前" + days + "天的红包到期提醒消息推送任务开始！");
		
		StringBuffer querySql = new StringBuffer();
		querySql.append(" select distinct userrelationinfo.phone, ");
		querySql.append(" redenvelopedetail.money, ");
		querySql.append(" redenvelopedetail.endTime, ");
		querySql.append(" redenvelopedetail.userId ");
		querySql.append(" from redenvelopedetail ");
		querySql.append(" left join userrelationinfo on redenvelopedetail.userId = userrelationinfo.user_id ");
		querySql.append(" where redenvelopedetail.useFlag = 0 ");
		querySql.append(" and redenvelopedetail.beginTime is not null ");
		querySql.append(" and redenvelopedetail.endTime is not null ");
		querySql.append(" and date(str_to_date(redenvelopedetail.endTime,'%Y-%m-%d')) = curdate() + ? ");
		
		List list = dao.findBySql(querySql.toString(), days);
		
		String phone = null;
		Double money = null;
		String endTime = null;
		Long userId = null;
		String msgContent = null;
		StringBuffer sb = new StringBuffer();
		sb.append(HONGBAO_INSERT_SQL);
		sb.append("\r\n");
		int counter = 0;
		
		if (list != null && list.size() > 0) {
			logger.info("今天需要提醒红包到期的用户数是：" + list.size());
			
			String[] msg = bonusNotification.split("：");
			for (Object obj : list) {
				counter++;
				
				Object[] arr = (Object[]) obj;
				phone = StatisticsUtil.getStringFromObject(arr[0]);
				money = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal) arr[1]);
				endTime = StatisticsUtil.getStringFromObject(arr[2]);
				userId = StatisticsUtil.getLongFromBigInteger(arr[3]);
				msgContent = MessageFormat.format(msg[1], money, endTime);
				
//				Usermessage userMessage = new Usermessage();
//				userMessage.setContext(msgContent);
//				userMessage.setIsPush(1);
//				userMessage.setReceivetime(sdf.format(new Date()));
//				userMessage.setExpireTime(endTime);
//				userMessage.setMoney(String.valueOf(money));
//				userMessage.setTitle(msg[0]);
//				userMessage.setUserId(userId);
//				userMessage.setIsread(0);
				
				// 用户的每条红包到期消息都需要保存至数据库
//				dao.save(userMessage);
				sb.append("(");
				sb.append("'" + msgContent + "'");
				sb.append(",");
				sb.append(1);
				sb.append(",");
				sb.append("'" + sdf.format(new Date()) + "'");
				sb.append(",");
				sb.append("'" + endTime + "'");
				sb.append(",");
				sb.append("'" + money + "'");
				sb.append(",");
				sb.append("'" + msg[0] + "'");
				sb.append(",");
				sb.append(userId);
				sb.append(")");
				
				if (counter <= list.size() - 1) {
					sb.append(",");
					sb.append("\r\n");
				}
				
				AppMessagePush message = new AppMessagePush();
				// customizedcast-alias
				message.setTitle(msg[0]);
				message.setPushType(6);
				message.setPushNow(1);
				message.setContent(msgContent);
				message.setDescription(msg[0]);
				message.setOperator("定时器任务");
				
				try {
					messagePushService.pushMessage(message, aliasType, phone, customizedField);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.debug("红包到期消息推送失败！");
					logger.debug(e.getMessage());
				}
			}
			
			String insertSql = sb.toString();
//			logger.debug("插入的sql语句：" + insertSql);
			
			dao.executeSql(insertSql);
		} else {
			logger.info("今天没有需要提醒红包到期的用户！");
		}
	}
	
	private void interestTicketExpiredRemind(int days, Map customizedField) {
		
		logger.debug("提前" + days + "天的加息券到期提醒消息推送任务开始！");
		
		StringBuffer querySql = new StringBuffer();
		querySql.append(" select distinct userrelationinfo.phone, ");
		querySql.append(" interestincreasecard.interestRate, ");
		querySql.append(" left(interestincreasecard.endTime, 10) as endTime, ");
		querySql.append(" interestincreasecard.userId ");
		querySql.append(" from interestincreasecard ");
		querySql.append(" left join userrelationinfo on userrelationinfo.user_id = interestincreasecard.userId ");
		querySql.append(" where interestincreasecard.useFlag = 0 ");
		querySql.append(" and interestincreasecard.beginTime is not null ");
		querySql.append(" and interestincreasecard.endTime is not null ");
		querySql.append(" and date(str_to_date(interestincreasecard.endTime,'%Y-%m-%d')) = curdate() + ? ");
		
		List list = dao.findBySql(querySql.toString(), days);
		
		String phone = null;
		Double interestRate = null;
		String endTime = null;
		Long userId = null;
		String msgContent = null;
		StringBuffer sb = new StringBuffer();
		sb.append(TICKET_INSERT_SQL);
		sb.append("\r\n");
		int counter = 0;
		
		if (list != null && list.size() > 0) {
			logger.info("今天需要提醒加息券到期的用户数是：" + list.size());
			
			String[] msg = ticketNotification.split("：");
			for (Object obj : list) {
				counter++;
				
				Object[] arr = (Object[]) obj;
				phone = StatisticsUtil.getStringFromObject(arr[0]);
				interestRate = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal) arr[1]);
				endTime = StatisticsUtil.getStringFromObject(arr[2]);
				userId = StatisticsUtil.getLongFromBigInteger(arr[3]);
				msgContent = MessageFormat.format(msg[1], percentFormat(interestRate), endTime);
				
				// 用户的每条红包到期消息都需要保存至数据库
//				dao.executeSql(PushMessageTimer.INSERT_SQL, msgContent, 1, sdf.format(new Date()), msg[0], userId);
				
				sb.append("(");
				sb.append("'" + msgContent + "'");
				sb.append(",");
				sb.append(1);
				sb.append(",");
				sb.append("'" + sdf.format(new Date()) + "'");
				sb.append(",");
				sb.append("'" + msg[0] + "'");
				sb.append(",");
				sb.append(userId);
				sb.append(")");
				
				if (counter <= list.size() - 1) {
					sb.append(",");
					sb.append("\r\n");
				}
				
				AppMessagePush message = new AppMessagePush();
				// customizedcast-alias
				message.setTitle(msg[0]);
				message.setPushType(6);
				message.setPushNow(1);
				message.setContent(msgContent);
				message.setDescription(msg[0]);
				message.setOperator("定时器任务");
				
				try {
					messagePushService.pushMessage(message, aliasType, phone, customizedField);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.debug("加息券到期消息推送失败！");
					logger.debug(e.getMessage());
				}
			}
			
			String insertSql = sb.toString();
//			logger.debug("插入的sql语句：" + insertSql);
			
			dao.executeSql(insertSql);
		} else {
			logger.info("今天没有需要提醒加息券到期的用户！");
		}
	}
	
	public String percentFormat(Double value) {
		DecimalFormat df = null;
		
		if (value * 100 >= 1) {
			df = new DecimalFormat("0%");
			return df.format(value);
		} else {
			df = new DecimalFormat("0.0%");
			return df.format(value);
		}
	}
}