package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.Messagesetting;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Usermessage;
import com.hc9.dao.entity.Validcodeinfo;
import com.hc9.dao.impl.HibernateSupport;

/**
 * 用户基本信息修改
 * 
 * @author RanSheng
 * 
 */
@Service
@SuppressWarnings(value = { "unchecked", "rawtypes" })
public class UserBaseInfoService {

    /**
     * 数据库操作通用接口
     */
    @Resource
    private HibernateSupport commonDao;

    /**
     * 发送短信接口
     */
    @Resource
    private SmsService smsService;

    /**
     * 邮件接口
     */
    @Resource
    private EmailService emailService;



    
    

    
    
    
    /**
     * <p>
     * Title: getEmailByUid
     * </p>
     * <p>
     * Description: 通过编号得到用户的邮箱
     * </p>
     * 
     * @param id
     *            编号
     * @return 邮箱
     */
    public String getEmailByUid(long id) {
        StringBuffer sb = new StringBuffer(
                "SELECT email from userrelationinfo where user_id=").append(id);
        Object object = commonDao.findObjectBySql(sb.toString());
        return object != null ? object.toString() : "";
    }




    /**
     * 修改登录密码
     * 
     * @param user
     *            用户基本信息
     * @param pwd
     *            登录密码
     */
    public void updatePwd(Userbasicsinfo user, String pwd) {
        // 发送站内消息
        sendMessagetting(user, 3L, "尊敬的" + user.getUserName()
                + "用户您好：您的登录密码已经修改，请核实。如有疑问，请致电前海红筹客服400-400-4000。【前海红筹】", "修改密码");
        // 修改登录密码
        user.setPassword(pwd);
        commonDao.update(user);
    }

    /**
     * 判断手机验证码是否失失效
     * 
     * @param id
     *            用户 id
     * @return 短信对象
     */
    public Validcodeinfo phoneValidcodeinfo(Long id) {
        List<Validcodeinfo> list = commonDao
                .find("from Validcodeinfo validate where validate.userbasicsinfo.id="
                        + id);
        if (list != null && list.size() > 0) {
            Long time = System.currentTimeMillis();
            Validcodeinfo val = list.get(0);
            // 如果验证码时间已经过期
            if (val.getSmsoverTime() == null || time > val.getSmsoverTime()) {
                return null;
            }
            return val;
        }
        return null;
    }



    /**
     * 发送站内消息
     * 
     * @param u
     *            用户
     * @param typeId
     *            消息类型
     * @param content
     *            消息类容
     * @param title
     *            站内消息标题
     */
    public void sendMessagetting(Userbasicsinfo u, Long typeId, String content,
            String title) {
        try {
            String sql = "FROM Messagesetting mes WHERE mes.messagetype.id="
                    + typeId + " AND mes.userbasicsinfo.id=" + u.getId();
            List<Messagesetting> list = commonDao.find(sql);
            if (list != null && list.size() > 0) {
                Messagesetting mes = list.get(0);
                // 判断是否需要发送邮件
                if (mes.getEmailIsEnable()) {
                    // 发送邮件
                    emailService.sendEmail(title, content, u
                            .getUserrelationinfo().getEmail());
                }
                if (mes.getSmsIsEnable()) {
                    // 发送短信
                    smsService.sendSMS(content, u.getUserrelationinfo()
                            .getPhone());
                }
                if (mes.getSysIsEnable()) {
                    // 添加站内消息
                    Usermessage umes = new Usermessage();
                    umes.setContext(content);
                    umes.setIsread(0);
                    umes.setUserbasicsinfo(u);
                    umes.setTitle(title);
                    umes.setReceivetime(DateUtils
                            .format(Constant.DEFAULT_TIME_FORMAT));
                    commonDao.save(umes);
                }
            }
        } catch (Throwable e) {
            LOG.error("发送站内消息出错：" + e);
        }

    }
 



}
