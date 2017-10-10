package com.hc9.service.sms.ym;

/**
 * 短信代理类
 * 
 * @author Mr.Po
 * 
 */
public interface SmsProxy {

    /**
     * 初始化代理
     * 
     * @param username
     *            用户名
     * @param password
     *            密码
     * @param etc
     *            其他信息
     * @throws Exception
     *             异常
     */
    void init(String sn, String pwd, String key) throws Exception;

    /**
     * 
     * 
     * @param content
     *            短信正文
     * @param telNos
     *            发送的号码
     * @return 
     */
    /**
     * 发送短信
     * @param content   短信正文
     * @param telNos    发送的号码
     * @return          短信发送状态[是否成功，返回值，失败信息]
     * @throws Exception    异常
     */
    Integer sendSMS(String content, String... telNos) throws Exception;

}
