package com.hc9.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.Arith;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Accounttype;
import com.hc9.dao.entity.Adminuseraccountinfo;
import com.hc9.dao.entity.Adminuserfundinfo;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Vipinfo;
import com.hc9.dao.entity.Viptype;
import com.hc9.dao.impl.HibernateSupport;

/**
 * 特权会员
 * 
 * @author ransheng
 * 
 */
@Service
@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class VipInfoService {

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
     * 判断会员是否是特权会员
     * 
     * @param id
     *            会员id
     * @return null 普通会员，否则为特权会员并且返回特权会员到期时间
     */
    public Object isVip(Long id) {
        String sql = "SELECT endtime FROM vipinfo v WHERE DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')< "
                + " DATE_FORMAT((SELECT endtime FROM vipinfo info WHERE info.user_id = ? ORDER BY "
                + " info.endtime DESC LIMIT 1), '%Y-%m-%d %H:%i:%s' ) AND v.user_id=? ORDER BY "
                + " v.endtime DESC LIMIT 1";
        Object obj = commonDao.findObjectBySql(sql, id, id);
        return obj;
    }

    /**
     * 查询特权会员类型
     * 
     * @return 特权会员类型
     */
    public List<Viptype> queryVipType() {
        String hql = "from Viptype";
        return commonDao.find(hql);
    }

    /**
     * 根据特权会员类型id查询单条特权会员类型
     * 
     * @param id
     *            特权会员类型编号
     * @return 特权会员类型
     */
    public Viptype typeById(Long id) {
        Viptype vipType = commonDao.get(Viptype.class, id);
        return vipType;
    }

    /**
     * 添加特权会员
     * 
     * @param user
     *            用户信息
     */
    public void addVip(Userbasicsinfo user, Viptype type) {
        // 会员升级记录
        Vipinfo vipinfo = new Vipinfo();
        // 判断当前会员是否已经是特权会员sql
        String sql = "SELECT v.id,v.endtime FROM vipinfo v WHERE DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s') < "
                + "DATE_FORMAT((SELECT endtime FROM vipinfo info WHERE info.user_id = ? ORDER BY "
                + "info.endtime DESC LIMIT 1), '%Y-%m-%d %H:%i:%s' ) and v.user_id = ? "
                + "ORDER BY v.endtime DESC LIMIT 1";
        // 取当前时间
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date());

        // 查询当前会员是否已经是特权会员
        Object[] obj = (Object[]) commonDao.findObjectBySql(sql, user.getId(),
                user.getId());
        // 如果当前会员是特殊会员
        if (obj != null && obj.length > 0) {
            // 给会员结束时间赋值
            Calendar ca = Calendar.getInstance();
            Timestamp tp = Timestamp.valueOf(obj[1].toString());
            ca.setTime(tp);
            ca.add(Calendar.MONTH, type.getMonth());
            String endtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(ca.getTime());
            String beginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(tp);
            // 会员开始时间
            vipinfo.setBegintime(beginTime);
            // 特权会员本来的到期时间+现在升级时间
            vipinfo.setEndtime(endtime);
            // 会员缴费时间
            vipinfo.setTime(time);
            // 会员
            vipinfo.setUserbasicsinfo(user);
            // 会员类型
            vipinfo.setViptype(type);
        } else {
            // 如果会员不是特权会员
            // 给会员结束时间赋值
            Calendar ca = Calendar.getInstance();
            ca.setTime(Timestamp.valueOf(time));
            ca.add(Calendar.MONTH, type.getMonth());
            String endtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(ca.getTime());
            // 会员开始时间
            vipinfo.setBegintime(time);
            // 特权到期时间
            vipinfo.setEndtime(endtime);
            // 会员缴费时间
            vipinfo.setTime(time);
            // 会员
            vipinfo.setUserbasicsinfo(user);
            // 会员类型
            vipinfo.setViptype(type);
        }

        // 保存会员升级历史记录
        commonDao.save(vipinfo);

        // 像流水账号表里插入数据
        Accountinfo info = new Accountinfo();
        // 流水号类型（会员升级用 1 表示）
        info.setAccounttype(new Accounttype(Long.valueOf(1)));
        // 当前用户可用余额
        info.setMoney(user.getUserfundinfo().getCashBalance() - type.getMoney());
        // 支出
        info.setExpenditure(type.getMoney());
        // 用户id
        info.setUserbasicsinfo(user);
        // 插入该条记录的当前时间
        info.setTime(time);
        // 说明
        info.setExplan("会员缴费");
        // 像流水账号插入数据
        commonDao.save(info);

        Adminuserfundinfo adminuser = commonDao.get(Adminuserfundinfo.class,
                Long.valueOf(1));
        // 修改账户
        adminuser.setCashBalance(adminuser.getCashBalance() + type.getMoney());
        commonDao.update(adminuser);

        // 像平台流水账号表里插入数据
        Adminuseraccountinfo adminAccountInfo = new Adminuseraccountinfo();
        adminAccountInfo.setAccounttype(new Accounttype(Long.valueOf(1)));
        // adminAccountInfo.setExpenditure(0.00);
        adminAccountInfo.setExplan("会员缴费");
        adminAccountInfo.setIncome(type.getMoney());
        adminAccountInfo.setMoney(adminuser.getCashBalance());
        adminAccountInfo.setTime(time);
        adminAccountInfo.setWithdraw(null);
        commonDao.save(adminAccountInfo);

        // 修改用户余额
        user.getUserfundinfo().setCashBalance(
                user.getUserfundinfo().getCashBalance() - type.getMoney());

        // 发送站内消息
        userBaseInfoService.sendMessagetting(user, 8L,
                "尊敬的" + user.getUserName() + "用户您好：您在前海红筹成功升级特权会员，特权会员费用为"
                        + Arith.round(BigDecimal.valueOf(type.getMoney()), 2)
                        + "，请核实。如有疑问，请致电前海红筹客服028-27228100", "会员升级");
        commonDao.update(user);
    }

    /**
     * 查询特权会员信息
     * 
     * @param id
     *            用户编号
     * @return 特权会员信息
     */
    public List queryVip(Long id) {
        String sql = "SELECT begintime,endtime,time FROM vipinfo WHERE user_id=? ORDER BY time LIMIT 10";
        List list = commonDao.findBySql(sql, id);
        return list;
    }
}
