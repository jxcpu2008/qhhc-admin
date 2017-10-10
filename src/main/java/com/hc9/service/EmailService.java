package com.hc9.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.FreeMarkerUtil;
import com.hc9.common.util.GenerateLinkUtils;
import com.hc9.dao.entity.Userbasicsinfo;

import freemarker.template.TemplateException;

/**
 * 邮件服务
 * 
 * @author frank
 * 
 */
@Service
public class EmailService {

    /**
     * BaseEmailService
     */
    @Resource
    BaseEmailService baseEmailService;
    
    @Resource
    AdEmailService adEmailService;

    /**
     * 得到邮件资源
     * 
     * @param modelName
     *            资源名称
     * @param map
     *            待填充字符
     * @return 数组[主题][内容]
     * @throws IOException
     *             文件读取异常
     * @throws TemplateException
     *             文件解析异常
     */
    public String[] getEmailResources(String modelName, Map map)
            throws IOException, TemplateException {

        String msg = FreeMarkerUtil.execute("config/marker/email/" + modelName,
                Constant.CHARSET_DEFAULT, map);

        int index = msg.indexOf("\n");

        return new String[] { msg.substring(0, index - 1),
                msg.substring(index + 1) };
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
    public void sendEmail(Date date, String subject, final String context,
            String type, String... address) {
        baseEmailService.sendEmail(date, subject, context, type, address);
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
        baseEmailService.sendEmail(subject, context, address);
    }
    
    

    /**
     * <p>
     * Title: sendResetPasswordEmail
     * </p>
     * <p>
     * Description:发送重置密码链接
     * </p>
     * 
     * @param user
     *            需要找回密码的用户
     * @param emial
     *            邮箱
     * @param request
     *            请求
     */
    public void sendResetPasswordEmail(Userbasicsinfo user, String email,
            HttpServletRequest request) {
        try {
            // 收件人地址
            String userName = user.getUserName();

            String url = GenerateLinkUtils.generateResetPwdLink(user, request);
            Map<String, String> map = new HashMap<String, String>();
            if (userName == null || userName.equals("")) {
                map.put("name", "用户");
            } else {
                map.put("name", userName);
            }
            map.put("emailActiveUrl", url);
            map.put("herf", url.substring(0, url.indexOf("/find_password/")));
            map.put("newdate", DateUtils.format(Constant.DEFAULT_TIME_FORMAT));
            String[] msg = getEmailResources("find-password.ftl", map);
            // 发送邮件链接地址
            sendEmail(msg[0], msg[1], email);

        } catch (IOException e) {
        } catch (TemplateException e) {
        }

    }
    /**
     * 广告邮件群发
     * @param subject
     * @param context
     * @param address
     * @throws IOException 
     */
    public void sendAdEmail(String subject, String context, String... address) throws IOException {
    	adEmailService.setProperties();
        adEmailService.sendEmail(subject, context, address);
    }
}
