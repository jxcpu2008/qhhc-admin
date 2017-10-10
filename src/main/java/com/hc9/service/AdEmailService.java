package com.hc9.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeUtility;

import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.EmailUtils;
import com.hc9.commons.log.LOG;

/**
 * 群发邮件服务 
 * 
 * @author frank
 * 
 */
@Service
public class AdEmailService {

    /**
     * Session
     */
    private Session session;
    
    /**
     * addressFrom
     */
    private String addressFrom;
    private String userName;
    private String password;
    public void setProperties() throws IOException {
        
        Properties props = PropertiesLoaderUtils.loadAllProperties("config/user/emailInfoPromote.properties");
        
        this.addressFrom = props.getProperty("mail.stmp.from");
        this.userName=props.getProperty("userName");
        this.password=props.getProperty("password");
        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(userName, password);
            }

        });
        this.session.setDebug(true);
        LOG.info("--->初始化广告邮件服务成功！");
    }
    

    /**
     * 发送邮件 支持邮件群发
     * 
     * @param date
     *            发送时间
     * @param subject
     *            主题(需要自行转码)
     * @param context
     *            内容
     * @param type
     *            内容类型
     * @param address
     *            接收者
     */
    public void sendEmail(final Date date, final String subject,
            final String context, final String type, final String... address) {

        new Thread(new Runnable() {

            public void run() {

                EmailUtils.sendEmail(session, date, addressFrom, subject,
                        context, type, null, null, address);

            }
        }).start();

    }

    /**
     * 发送邮件 支持邮件群发
     * 
     * @param subject
     *            主题
     * @param context
     *            内容
     * @param address
     *            接收者
     */
    public void sendEmail(String subject, String context, String... address) {
        try {
            subject = MimeUtility.encodeText(subject, Constant.CHARSET_DEFAULT,
                    "B");
        } catch (UnsupportedEncodingException e) {
            LOG.error("邮件主题转码失败！", e);
        }
        sendEmail(new Date(), subject, context, "text/html;charset="
                + Constant.CHARSET_DEFAULT, address);
    }

}
