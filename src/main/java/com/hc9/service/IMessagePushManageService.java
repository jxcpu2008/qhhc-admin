package com.hc9.service;

import java.util.List;
import java.util.Map;

import com.hc9.dao.entity.AppMessagePush;
import com.hc9.dao.entity.Loansign;
import com.hc9.model.PageModel;
import com.hc9.model.Payuser;

public interface IMessagePushManageService {
	
	/**
	 * 满标放款后推送消息
	 * @param loan
	 */
	public void pushMessageAfterIssueLoan(Loansign loan);
	
	/**
	 * 在投资人的回款金额到达宝付账户时进行消息推送
	 * @param userList
	 * @param loansignName
	 */
	public void pushReturnMoneyMessage(List<Payuser> userList, String loansignName);
	
	/**
	 * 推送消息
	 * @param message
	 * @throws Exception
	 */
	public void pushMessage(AppMessagePush message) throws Exception;
	
	/**
	 * 推送消息-broadcast
	 * @param message
	 * @param customizedFields
	 * @throws Exception
	 */
	public void pushMessage(AppMessagePush message, Map<String, String> customizedFields) throws Exception;
	
	/**
	 * 推送消息-customizedcast
	 * @param message
	 * @param aliasType
	 * @param phoneList
	 */
	public void pushMessage(AppMessagePush message, String aliasType, String phone, Map<String, String> customizedFields) throws Exception;
	
	/**
	 * 推送消息-customizedcast
	 * @param message
	 * @param aliasType
	 * @param phoneList
	 */
	public void pushMessage(AppMessagePush message, String aliasType, List<String> phoneList, Map<String, String> customizedFields) throws Exception;
	
	/**
	 * 查询已经推送过的消息列表
	 * @throws Exception
	 */
	public List queryPushedMessage(PageModel page, AppMessagePush msgPushed, String fromTime, String toTime) throws Exception;
	
	/**
	 * 分页查询消息推送列表
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List queryMessageList() throws Exception;
	
	/**
	 * 删除推送过的消息
	 * @throws Exception
	 */
	public void deletePushedMessage(Class clazz, String ids) throws Exception;
}