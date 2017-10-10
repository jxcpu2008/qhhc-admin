package com.hc9.service.sms.hy;

import javax.annotation.Resource;

import com.hc9.commons.log.LOG;

/**
 * 互亿触发短信发送服务
 * 
 * @author frank
 * 
 */
public class HyTriggerSmsService {

    /** 序列号     */
    String pid;
    /** 密码     */
    String pwd;
    /**帐号*/
    String account;

    @Resource
    private HuYiSms huYiSms;

    /**
     * 构造函数
     */
    public HyTriggerSmsService() {
    }

    /**
     * 构造函数
     * 
     * @param username
     *            用户名
     * @param password
     *            密码
     * @param etc
     *            其他
     */
    public HyTriggerSmsService(String account,String pwd,String pid) {
        this.pid = pid;
        this.pwd = pwd;
        this.account=account;
    }

    /**
     * 初始化
     * @throws Exception    异常
     */
    public void init() throws Exception {
    	huYiSms.init(account, pwd,pid);
    	System.out.println("--->初始化互亿短信服务成功！");
        LOG.info("--->初始化互亿短信服务成功！");
    }
    
    /**
     * 短信发送接口
     * @param content
     * @param telNos 群发用英文逗号拼接手机号
     * @return
     */
    public Integer sendSMS(String content, String telNos){
    	String[] arr=telNos.split(",");
    	content=content.substring(content.indexOf("】")+1, content.length());
    	int res=0;
    	for(int i=0;i<arr.length;i++){
    		res+= huYiSms.sendSMS2(content, telNos);
    	}
    	return res;
    }
    
}
