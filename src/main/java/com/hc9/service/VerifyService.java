package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.Securityproblem;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Validcodeinfo;
import com.hc9.dao.entity.Verifyproblem;
import com.hc9.dao.impl.HibernateSupport;

/**
 * 安全中心
 * 
 * @author ransheng
 * 
 */

@Service
@SuppressWarnings("unchecked")
public class VerifyService {

    /**
     * 数据库接口
     */
    @Resource
    private HibernateSupport commonDao;

    /**
     * 接口
     */
    @Resource
    private UserBaseInfoService userBaseInfoService;

    /**
     * 修改邮箱
     * 
     * @param user
     *            用户
     * @param email
     *            邮箱
     */
//    public void updateEmail(Userbasicsinfo user, String email) {
//        user.getUserrelationinfo().setEmail(email);
//        user.getUserrelationinfo().setEmailisPass(0);
//        // 发送站内消息
//        userBaseInfoService.sendMessagetting(user, 6L,
//                "尊敬的" + user.getUserName()
//                        + "用户您好：您的邮箱已经修改 ，请核实。如有疑问，请致电前海红筹客服400-0888-913",
//                "修改邮箱");
//        commonDao.update(user);
//    }

    /**
     * 查询安全问题类型
     * 
     * @return 安全问题类型
     */
    public List<Verifyproblem> queryVerify() {
        String hql = "from Verifyproblem";
        List<Verifyproblem> list = commonDao.find(hql);
        return list;
    }

    /**
     * 修改用户安全问题
     * 
     * @param id
     *            用户编号
     * @param id1
     *            安全问题编号1
     * @param id2
     *            安全问题编号2
     * @param answer1
     *            安全问题答案
     * @param answer2
     *            安全问题答案
     * @return 是否设置成功
     */
    public boolean updateSafe(Long id, Long id1, Long id2, String answer1,
            String answer2) {
        try {
            String hql = "from Securityproblem where userbasicsinfo.id=?";
            List<Securityproblem> list = commonDao.find(hql, id);
            if (list.size() > 1) {
                // 修改安全问题
                Securityproblem security = list.get(0);
                security.setVerifyproblem(new Verifyproblem(id1));
                security.setAnswer(answer1);
                commonDao.update(security);
                security = list.get(1);
                security.setVerifyproblem(new Verifyproblem(id2));
                security.setAnswer(answer2);
                commonDao.update(security);

                // 发送站内消息
//                userBaseInfoService
//                        .sendMessagetting(
//                                security.getUserbasicsinfo(),
//                                4L,
//                                "尊敬的"
//                                        + security.getUserbasicsinfo()
//                                                .getUserName()
//                                        + "用户您好：您的安全问题已经修改 ，请核实。如有疑问，请致电前海红筹客服400-0888-913",
//                                "修改安全问题");
                return true;
            } else {
                return false;
            }
        } catch (Throwable e) {
            LOG.info("修改安全问题出错：" + e);
            return false;
        }
    }

    /**
     * 修改电话号码
     * 
     * @param user
     *            用户基本信息
     * @param phone
     *            手机号码
     * @param vail
     *            验证码表
     */
    public void updatePhone(Userbasicsinfo user, String phone,
            Validcodeinfo vail) {
        if (vail != null) {
            // 清空手机验证码
//            userBaseInfoService.clearPhone(vail);
        }
        user.getUserrelationinfo().setPhone(phone);
        commonDao.update(user);

        // 发送站内消息
//        userBaseInfoService.sendMessagetting(user, 5L,
//                "尊敬的" + user.getUserName()
//                        + "用户您好：您的手机号码已经修改 ，请核实。如有疑问，请致电前海红筹客服400-0888-913",
//                "修改手机号码");
    }
}
