package com.hc9.service.impl;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hc9.common.messagepush.AndroidNotification;
import com.hc9.common.messagepush.PushClient;
import com.hc9.common.messagepush.android.AndroidBroadcast;
import com.hc9.common.messagepush.android.AndroidCustomizedcast;
import com.hc9.common.messagepush.ios.IOSBroadcast;
import com.hc9.common.messagepush.ios.IOSCustomizedcast;
import com.hc9.common.util.Arith;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.dao.entity.AppMessagePush;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.InvestSmsVo;
import com.hc9.model.PageModel;
import com.hc9.model.Payuser;
import com.hc9.service.IMessagePushManageService;
import com.hc9.service.LoanSignQuery;

/**
 * 消息推送管理service类
 * @author Jerry Wong
 * 
 */
@Service
public class MessagePushManageService implements IMessagePushManageService {
	
	private static final Logger logger = Logger.getLogger(MessagePushManageService.class);
	
	private final static String INSERT_SQL = " insert into usermessage(context, isPush, receivetime, title, user_id) values (?, ?, ?, ?, ?) ";
	
	@Value("${push.env.mode}")
	private String mode;
	
	@Value("${push.ios.appKey}")
	private String iosAppKey;
	
	@Value("${push.andorid.appKey}")
	private String andoridAppKey;
	
	@Value("${push.ios.appMasterSecret}")
	private String iosAppMasterSecret;
	
	@Value("${push.andorid.appMasterSecret}")
	private String andoridAppMasterSecret;
	
	@Value("${push.aliasType}")
	private String aliasType;
	
	@Value("${push.boughtUsers.sql}")
	private String boughtUsersSql;
	
	@Value("${push.authorizedUnboughtUsers.sql}")
	private String authorizedUnboughtUsersSql;
	
	@Value("${push.femaleUsers.sql}")
	private String femaleUsersSql;
	
	@Value("${push.maleUsers.sql}")
	private String maleUsersSql;
	
	@Value("${push.birthdayUsers.sql}")
	private String birthdayUsersSql;
	
	@Value("${push.env.test.phone}")
	private String testPhone;
	
	@Value("${push.issueLoan4Month.notification}")
	private String issueLoan4MonthMsg;
	
	@Value("${push.issueLoan4Day.notification}")
	private String issueLoan4DayMsg;
	
	@Value("${push.moneyReturn.notification}")
	private String moneyReturnMsg;
	
	@Autowired
	private HibernateSupport dao;
	
	@Autowired
	private PushClient client;
	
	@Autowired
	private LoanSignQuery loanSignQuery;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat mySdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
	
	@Override
	public void pushMessage(AppMessagePush message) throws Exception {
		// 消息推送自定义字段
		Map<String, String> myFields = new HashMap<String, String>();
		myFields.put("sender", message.getOperator());
		myFields.put("title", message.getTitle());
		
		List<String> phones = new ArrayList<String>();
		String phone = null;
		List queryResults = null;
		
		int pushTo = message.getPushTo();
		switch (pushTo) {
		case 1:	// 全部注册用户，相当于广播
			// 使用广播模式推送消息
			message.setPushType(4);
			if (mode.equals("product")) {
				pushMessage(message, myFields);
			} else {
				// 测试环境下，根据手机号码推送消息
				pushMessage(message, aliasType, testPhone, myFields);
			}
			break;
		case 2:	// 已认购用户
			// 使用自定义播(customizedcast)模式推送消息-alias存放到文件
			message.setPushType(6);
			
			if (mode.equals("product")) {
				queryResults = dao.findBySql(boughtUsersSql);
				if (queryResults != null && queryResults.size() > 0) {
					logger.debug("已认购用户数是：" + queryResults.size());
					
					for (Object obj : queryResults) {
						phone = StatisticsUtil.getStringFromObject(obj);
						phones.add(phone);
					}
				}
				
				pushMessage(message, aliasType, phones, myFields);
			} else {
				// 测试环境下，根据手机号码推送消息
				pushMessage(message, aliasType, testPhone, myFields);
			}
			break;
		case 3: // 已宝付授权未认购
			// 使用自定义播(customizedcast)模式推送消息-alias存放到文件
			message.setPushType(6);
			
			if (mode.equals("product")) {
				queryResults = dao.findBySql(authorizedUnboughtUsersSql);
				if (queryResults != null && queryResults.size() > 0) {
					logger.debug("已宝付授权未认购用户数是：" + queryResults.size());
					
					for (Object obj : queryResults) {
						phone = StatisticsUtil.getStringFromObject(obj);
						phones.add(phone);
					}
				}
				
				pushMessage(message, aliasType, phones, myFields);
			} else {
				// 测试环境下，根据手机号码推送消息
				pushMessage(message, aliasType, testPhone, myFields);
			}
			break;
		case 4: // 女性用户
			// 使用自定义播(customizedcast)模式推送消息-alias存放到文件
			message.setPushType(6);
			
			if (mode.equals("product")) {
				queryResults = dao.findBySql(femaleUsersSql);
				if (queryResults != null && queryResults.size() > 0) {
					logger.debug("女性用户数是：" + queryResults.size());
					
					for (Object obj : queryResults) {
						phone = StatisticsUtil.getStringFromObject(obj);
						phones.add(phone);
					}
				}
				
				pushMessage(message, aliasType, phones, myFields);
			} else {
				// 测试环境下，根据手机号码推送消息
				pushMessage(message, aliasType, testPhone, myFields);
			}
			break;
		case 5: // 男性用户
			// 使用自定义播(customizedcast)模式推送消息-alias存放到文件
			message.setPushType(6);
			
			if (mode.equals("product")) {
				queryResults = dao.findBySql(maleUsersSql);
				if (queryResults != null && queryResults.size() > 0) {
					logger.debug("男性用户数是：" + queryResults.size());
					
					for (Object obj : queryResults) {
						phone = StatisticsUtil.getStringFromObject(obj);
						phones.add(phone);
					}
				}
				
				pushMessage(message, aliasType, phones, myFields);
			} else {
				// 测试环境下，根据手机号码推送消息
				pushMessage(message, aliasType, testPhone, myFields);
			}
			break;
		case 6: // 当日生日客户
			// 使用自定义播(customizedcast)模式推送消息-alias存放到文件
			message.setPushType(6);
			
			if (mode.equals("product")) {
				queryResults = dao.findBySql(birthdayUsersSql);
				if (queryResults != null && queryResults.size() > 0) {
					logger.debug("当日生日用户数是：" + queryResults.size());
					
					for (Object obj : queryResults) {
						phone = StatisticsUtil.getStringFromObject(obj);
						phones.add(phone);
					}
				}
				
				pushMessage(message, aliasType, phones, myFields);
			} else {
				// 测试环境下，根据手机号码推送消息
				pushMessage(message, aliasType, testPhone, myFields);
			}
			break;
		default:
			logger.debug("消息推送目标未指定！");
			break;
		}
	}

	/**
	 * 广播(broadcast)
	 */
	@Override
	public void pushMessage(AppMessagePush message, Map<String, String> customizedFields) throws Exception {
		long current = System.currentTimeMillis();
 		current += 30 * 60 * 1000;
		Date dateAfter30Minutes = new Date(current);
		
		// 构建ios消息
     	IOSBroadcast iosMsg = new IOSBroadcast(iosAppKey, iosAppMasterSecret);
     	iosMsg.setAlert(message.getContent());
 		iosMsg.setBadge(0);
 		iosMsg.setSound("default");
 		
 		if (mode.equals("product")) {
 			iosMsg.setProductionMode();
 		} else {
 			iosMsg.setTestMode();
 		}
 		
 		for (Entry<String, String> entry : customizedFields.entrySet()) {
 			iosMsg.setCustomizedField(entry.getKey(), entry.getValue());
 		}
 		iosMsg.setContentAvailable(1);
 		iosMsg.setDescription(message.getDescription());
 		iosMsg.setExpireTime(mySdf.format(dateAfter30Minutes));
 		
 		// 构建andorid消息
     	AndroidBroadcast andoridMsg = new AndroidBroadcast(andoridAppKey, andoridAppMasterSecret);
 		// 通知栏提示文字
 		andoridMsg.setTicker(message.getContent());
 		// 通知标题
 		andoridMsg.setTitle(customizedFields.get("title"));
 		// 通知文字描述
 		andoridMsg.setText(message.getContent());
 		andoridMsg.goAppAfterOpen();
 		andoridMsg.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
 		
 		if (mode.equals("product")) {
 			andoridMsg.setProductionMode();
 		} else {
 			andoridMsg.setTestMode();
 		}
 		
 		for (Entry<String, String> entry : customizedFields.entrySet()) {
			andoridMsg.setExtraField(entry.getKey(), entry.getValue());
 		}
 		andoridMsg.setDescription(message.getDescription());
 		andoridMsg.setExpireTime(mySdf.format(dateAfter30Minutes));
     		
 		// 目前没有区分ios还是android，所以两个平台都推，可能存在推送失败的情况，只要有一个平台推送成功，则认为消息推送成功
		boolean ios = false;
		if (client.send(iosMsg)) {
			ios = true;
		}
		boolean andorid = false;
		if (client.send(andoridMsg)) {
			andorid = true;
		}
		if (ios || andorid) {	// 只要有一个平台推送成功，则认为消息推送成功
			message.setStatus(1);
		} else {	// 否则，消息推送失败
			message.setStatus(2);
		}
     		
 		// 消息推送记录持久化
		dao.save(message);
	}
	
	/**
	 * 自定义播(customizedcast)-alias
	 */
	@Override
	public void pushMessage(AppMessagePush message, String aliasType, String phone, Map<String, String> customizedFields) throws Exception {
		// 构建ios消息
		IOSCustomizedcast iosMsg = new IOSCustomizedcast(iosAppKey, iosAppMasterSecret);
		iosMsg.setAlias(phone, aliasType);
		iosMsg.setAlert(message.getContent());
 		iosMsg.setBadge(0);
 		iosMsg.setSound("default");
 		iosMsg.setContentAvailable(1);
 		
 		if (mode.equals("product")) {
 			logger.debug("ios消息推送生产模式");
 			iosMsg.setProductionMode();
 		} else {
 			logger.debug("ios消息推送测试模式");
 			iosMsg.setTestMode();
 		}
 		
 		for (Entry<String, String> entry : customizedFields.entrySet()) {
 			iosMsg.setCustomizedField(entry.getKey(), entry.getValue());
 		}
 		iosMsg.setDescription(message.getDescription());

 		// 构建andorid消息
		AndroidCustomizedcast andoridMsg = new AndroidCustomizedcast(andoridAppKey, andoridAppMasterSecret);
		andoridMsg.setAlias(phone, aliasType);
		// 通知栏提示文字
 		andoridMsg.setTicker(message.getContent());
 		// 通知标题
 		andoridMsg.setTitle(customizedFields.get("title"));
 		// 通知文字描述
 		andoridMsg.setText(message.getContent());
		andoridMsg.goAppAfterOpen();
		andoridMsg.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		
		if (mode.equals("product")) {
			logger.debug("andorid消息推送生产模式");
 			andoridMsg.setProductionMode();
 		} else {
 			logger.debug("andorid消息推送测试模式");
 			andoridMsg.setTestMode();
 		}
		
		for (Entry<String, String> entry : customizedFields.entrySet()) {
			andoridMsg.setExtraField(entry.getKey(), entry.getValue());
 		}
		andoridMsg.setDescription(message.getDescription());
		
		// 目前没有区分ios还是android，所以两个平台都推，可能存在推送失败的情况，只要有一个平台推送成功，则认为消息推送成功
		boolean ios = false;
		if (client.send(iosMsg)) {
			ios = true;
		}
		boolean andorid = false;
		if (client.send(andoridMsg)) {
			andorid = true;
		}
		if (ios || andorid) {	// 只要有一个平台推送成功，则认为消息推送成功
			message.setStatus(1);
		} else {	// 否则，消息推送失败
			message.setStatus(2);
		}
		
		// 消息推送记录持久化
		dao.save(message);
	}
	
	/**
	 * 自定义播(customizedcast)-alias存放到文件
	 */
	@Override
	public void pushMessage(AppMessagePush message, String aliasType, List<String> phoneList, Map<String, String> customizedFields) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		for (String phone : phoneList) {
			sb.append(phone);
			sb.append("\n");
		}
		
		// 构建ios消息
		String iosFileId = client.uploadContents(iosAppKey, iosAppMasterSecret, sb.toString());
		IOSCustomizedcast iosMsg = new IOSCustomizedcast(iosAppKey, iosAppMasterSecret);
		iosMsg.setFileId(iosFileId, aliasType);
		iosMsg.setAlert(message.getContent());
 		iosMsg.setBadge(0);
 		iosMsg.setContentAvailable(1);
 		iosMsg.setSound("default");
 		
 		if (mode.equals("product")) {
			logger.debug("ios消息推送生产模式");
			iosMsg.setProductionMode();
		} else {
			logger.debug("ios消息推送测试模式");
			iosMsg.setTestMode();
		}
 		
 		for (Entry<String, String> entry : customizedFields.entrySet()) {
 			iosMsg.setCustomizedField(entry.getKey(), entry.getValue());
 		}
 		iosMsg.setDescription(message.getDescription());

 		// 构建andorid消息
		String andoridFileId = client.uploadContents(andoridAppKey, andoridAppMasterSecret, sb.toString());
		AndroidCustomizedcast andoridMsg = new AndroidCustomizedcast(andoridAppKey, andoridAppMasterSecret);
		andoridMsg.setFileId(andoridFileId, aliasType);
		// 通知栏提示文字
 		andoridMsg.setTicker(message.getContent());
 		// 通知标题
 		andoridMsg.setTitle(customizedFields.get("title"));
 		// 通知文字描述
 		andoridMsg.setText(message.getContent());
		andoridMsg.goAppAfterOpen();
		andoridMsg.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		
		if (mode.equals("product")) {
			logger.debug("andorid消息推送生产模式");
 			andoridMsg.setProductionMode();
 		} else {
 			logger.debug("andorid消息推送测试模式");
 			andoridMsg.setTestMode();
 		}
		
		for (Entry<String, String> entry : customizedFields.entrySet()) {
			andoridMsg.setExtraField(entry.getKey(), entry.getValue());
 		}
		andoridMsg.setDescription(message.getDescription());

		// 目前没有区分ios还是android，所以两个平台都推，可能存在推送失败的情况，只要有一个平台推送成功，则认为消息推送成功
		boolean ios = false;
		if (client.send(iosMsg)) {
			ios = true;
		}
		boolean andorid = false;
		if (client.send(andoridMsg)) {
			andorid = true;
		}
		if (ios || andorid) {	// 只要有一个平台推送成功，则认为消息推送成功
			message.setStatus(1);
		} else {	// 否则，消息推送失败
			message.setStatus(2);
		}
		
		// 消息推送记录持久化
		dao.save(message);
	}

	@Override
	public List queryPushedMessage(PageModel page, AppMessagePush msgPushed, String fromTime, String toTime) throws Exception {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder("from AppMessagePush where 1 = 1");
        List<Object> param = new ArrayList<Object>();
        
        // 消息标题
        if (!StringUtils.isBlank(msgPushed.getTitle())) {
            sb.append(" and title like ? ");
            param.add("'%" + msgPushed.getTitle() + "%'");
        }
        
        // 消息推送目标
        if (msgPushed.getPushTo() > 0) {
	        sb.append(" and pushTo = ? ");
	        param.add(msgPushed.getPushTo());
        }
        
        // 创建时间
        if (!StringUtils.isBlank(fromTime)) {
        	sb.append(" and createTime >= ? ");
        	param.add("'" + fromTime + " 00:00:00'");
        }
        
        if (!StringUtils.isBlank(toTime)) {
        	sb.append(" and createTime <= ? ");
        	param.add("'" + toTime + " 23:59:59'");
        }
        
        // 排序
        sb.append(" order by createTime desc ");
        
//        if (msgPushed.getStatus() != 0) {
//            sb.append(" and status = ? ");
//            param.add(msgPushed.getStatus());
//        }
        
        Object[] params = null;
        if (param.size() > 0) {
            params = param.toArray();
        }
        
        if (page != null) {
            return dao.pageListByHql(page, sb.toString(), false, params);
        } else {
            return dao.query(sb.toString(), false, params);
        }
	}

	@Override
	public List queryMessageList() throws Exception {
		String queryStr = "from AppMessagePush order by createTime desc";
        return dao.find(queryStr);
	}

	@Override
	public void deletePushedMessage(Class clazz, String ids) throws Exception {
		// TODO Auto-generated method stub
		String[] id = ids.split(",");
		for (int i = 0; i < id.length; i++) {
			AppMessagePush msg = (AppMessagePush) dao.get(clazz, Integer.parseInt(id[i]));
			msg.setIsDelete(1);
			msg.setUpdateTime(new Date());
			dao.update(msg);
		}
	}

	@Override
	public void pushMessageAfterIssueLoan(Loansign loan) {
		
		String msg4MonthTitle = issueLoan4MonthMsg.substring(0, issueLoan4MonthMsg.indexOf("："));
		String msg4MonthContent = issueLoan4MonthMsg.substring(issueLoan4MonthMsg.indexOf("：") + 1);
		
		String msg4DayTitle = issueLoan4DayMsg.substring(0, issueLoan4DayMsg.indexOf("："));
		String msg4DayContent = issueLoan4DayMsg.substring(issueLoan4DayMsg.indexOf("：") + 1);
		
		Map<String, String> customizedField = new HashMap<String, String>();
		customizedField.put("page", "userMessage");
		customizedField.put("title", msg4MonthTitle);
		customizedField.put("sender", "system");
		
		DecimalFormat investMoneyFormat = new DecimalFormat("#.0");
		
		String notificationMsg = msg4MonthContent;
		// 1-店铺 2-项目 3-天标 4-债权转让
		Integer type = loan.getType();
		String reMonth = loan.getRemonth() + "个月";
		if (type.intValue() == 3) {	// 天标
			reMonth = loan.getRemonth() + "天";
			
			customizedField.put("title", msg4DayTitle);
			notificationMsg = msg4DayContent;
		}
		
		// 预计还款日期
		String preRepayDate = loanSignQuery.queryPreRepayDate(loan.getId());
		
		// 给投资人推送消息
		List<InvestSmsVo> list = loanSignQuery.queryMsgUser(loan.getId());
		if (list != null && list.size() > 0) {
			
			for (InvestSmsVo vo : list) {
				String phone = vo.getMobilePhone();
				String userName = vo.getCustomerName();
				
				try {
					// 插入用户站内消息表-usermessage
					dao.executeSql(MessagePushManageService.INSERT_SQL, 
							MessageFormat.format(notificationMsg, loan.getName(), investMoneyFormat.format(vo.getTenderMoney()), reMonth, preRepayDate),
							1,
							sdf.format(new Date()),
							customizedField.get("title"), 
							vo.getUserId());
					
					// 推送回款提醒消息
					AppMessagePush message = new AppMessagePush();
					// customizedcast-alias
					message.setPushType(6);
					message.setPushNow(1);
					message.setContent(MessageFormat.format(notificationMsg, loan.getName(), investMoneyFormat.format(vo.getTenderMoney()), reMonth, preRepayDate));
					message.setDescription(customizedField.get("title"));
					message.setOperator(customizedField.get("sender"));
					message.setTitle(customizedField.get("title"));
					
					pushMessage(message, aliasType, phone, customizedField);
				} catch(Exception e) {
					e.printStackTrace();
					logger.error("给投资人：[" + userName + "]，手机号码：[" + phone + "]的放款提醒消息推送失败！");
					logger.error(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 在投资人的回款金额到达宝付账户时进行消息推送
	 * @param userList
	 * @param loansignName
	 */
	@Override
	public void pushReturnMoneyMessage(final List<Payuser> userList, final String loansignName) {
		
		if (userList != null && userList.size() > 0) {
			
			final String[] msg = moneyReturnMsg.split("：");
			final Map<String, String> customizedField = new HashMap<String, String>();
			customizedField.put("page", "userMessage");
			customizedField.put("title", msg[0]);
			customizedField.put("sender", "system");
			
			new Thread(
				new Runnable() {
					public void run() {
						Map<String , Double> repaymentUserMap = new HashMap<String, Double>();
						for (Payuser payUser : userList) {
							String userId = payUser.getId();	// 投资人的用户id
							Double returnmoney = payUser.getAmount();	// 个人得到的钱
							if (!repaymentUserMap.containsKey(userId)) {
								repaymentUserMap.put(userId, returnmoney);
							} else {
								Double investMoney = repaymentUserMap.get(userId);
								Double totalMoney = Arith.add(investMoney, returnmoney);
								repaymentUserMap.put(userId, totalMoney);
							}
						}
						
						for (Map.Entry<String , Double> entry : repaymentUserMap.entrySet()) {
							String userId = entry.getKey();	// 投资人的用户id
							Double returnmoney = entry.getValue();	// 个人得到的钱
							
							Userbasicsinfo investUser = dao.get(Userbasicsinfo.class, Long.valueOf(userId));
							String phone = investUser.getUserrelationinfo().getPhone();
							String userName = investUser.getUserName();
							
							try {
								// 插入用户站内消息表-usermessage
								dao.executeSql(MessagePushManageService.INSERT_SQL, 
										MessageFormat.format(msg[1], loansignName, returnmoney), 
										1,
										sdf.format(new Date()), 
										msg[0], 
										userId);
								
								// 推送回款提醒消息
								AppMessagePush message = new AppMessagePush();
								// customizedcast-alias
								message.setPushType(6);
								message.setPushNow(1);
								message.setContent(MessageFormat.format(msg[1], loansignName, returnmoney));
								message.setDescription(msg[0]);
								message.setOperator(customizedField.get("sender"));
								message.setTitle(customizedField.get("title"));
								
								pushMessage(message, aliasType, phone, customizedField);
								
							} catch (Exception e) {
								logger.error("给投资人：[" + userName + "]，手机号码：[" + phone + "]的回款提醒消息推送失败！", e);
							}
						}
					} 
				}
			).start();
		}
	}
}