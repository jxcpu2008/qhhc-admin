package com.hc9.service.sms.wd;


import javax.annotation.Resource;

import com.hc9.commons.log.LOG;

/***
 * 营销沃动短信Service
 * @author lkl
 *
 */
public class MarketWdSmsService {

	/***
	 * 企业id
	 */
	private String userid;
	/***
	 * 用户帐号
	 */
	private String account;
	
	/***
	 * 帐号密码
	 */
	private String password;
	
	@Resource
	private MarketWdSms marketWdSms;
	
    
    public MarketWdSmsService() {
	}
    
	public MarketWdSmsService(String userid, String account, String password) {
		this.userid = userid;
		this.account = account;
		this.password = password;
	}
	
	/**
     * 初始化
     * @throws Exception    异常
     */
    public void init() throws Exception {
    	marketWdSms.init(userid, account, password);
    	System.out.println("--->沃动短信营销通道服务初始化成功！");
        LOG.info("--->沃动短信营销通道服务初始化成功！");
    }
    
    /***
     * 营销通道要先调用查询关键字，再进行发送
     * @param content
     * @param telNos
     * @return
     */
    public Integer sendSMS(String content, String telNos) {
		return marketWdSms.sendMarketSMS(content, telNos);
    }
}
