package com.hc9.service.sms.ym;

import javax.annotation.Resource;

import com.hc9.commons.log.LOG;
import com.hc9.service.sms.ym.impl.EmaySms;

/**
 * 基础发送短信服务 所有发送短信都需调用此service发送
 * 
 * @author frank
 * 
 */
public class EmayMarketingSmsService {

    /**
     * 序列号
     */
    String sn;
    /**
     * 密码
     */
    String pwd;
    /**
     * 关键字
     */
    String key;

    /**
     * SmsProxy
     */
    @Resource
    private EmaySms smsProxy;

    /**
     * 构造函数
     */
    public EmayMarketingSmsService() {
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
    public EmayMarketingSmsService(String sn, String pwd, String key) {
        this.sn = sn;
        this.pwd = pwd;
        this.key = key;
    }

    /**
     * 初始化
     * @throws Exception    异常
     */
    public void init() throws Exception {
        smsProxy.init(sn, pwd, key);
        System.out.println("--->初始化短信服务成功！");
        LOG.info("--->初始化短信服务成功！");
    }

    /**
     * 发送短信 支持短信群发
     * 
     * @param content
     *            内容
     * @param telNos
     *            接收号码
     * @return 短信发送状态[是否成功，返回值，失败信息]
     * @throws Exception
     *             异常
     */
    public Integer sendSMS(String content, String... telNos) {
    	int res=-1;
		try {
			res = smsProxy.sendSMS(content, telNos);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
        return res;
    }

}
