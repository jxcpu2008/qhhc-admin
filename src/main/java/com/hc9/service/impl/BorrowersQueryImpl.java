package com.hc9.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.Borrowersbase;
import com.hc9.dao.entity.Borrowerscontact;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.service.BorrowersQuery;

/**
 * 借款人查询接口
 * 
 * @author Administrator
 * 
 */
@Service
public class BorrowersQueryImpl implements BorrowersQuery {

    /** dao */
    @Resource
    private HibernateSupport dao;

    /**
     * 判断改会员是否是接款人
     * 
     * @param userbasicsinfo
     *            会员对象
     * @return 是 否
     */
    public boolean isBorrowsByUser(Userbasicsinfo userbasicsinfo) {
        StringBuffer sb = new StringBuffer(
                "select count(1) from borrowersbase b where b.auditResult=1 and b.userbasicinfo_id=")
                .append(userbasicsinfo.getId());
        Object object = dao.findObjectBySql(sb.toString());
        return Integer.parseInt(object.toString()) > 0 ? true : false;
    }

    /***
     * 通过用户编号查询出改用户关联的借款人信息
     * 
     * @param userbasicsId
     *            用户编号
     * @return 借款人信息
     */
    public Borrowersbase queryBorrowersbaseByUserfundId(Long userbasicsId) {
        StringBuffer sb = new StringBuffer(
                "select * from borrowersbase where userbasicinfo_id=")
                .append(userbasicsId);
        List<Borrowersbase> listBase = dao.findBySql(sb.toString(),
                Borrowersbase.class, null);
        return listBase.size() > 0 ? listBase.get(0) : null;
    }

    /**
     * 查询个人资料
     * 
     * @param ids
     *            会员主键编号
     * @return list
     */
    @SuppressWarnings("unchecked")
    public List queryPersonal(String ids) {
        StringBuffer sqlbuffer = new StringBuffer(
                "SELECT borrowersbase.realName,borrowersbase.addTime,borrowersbase.money, ");
        sqlbuffer
                .append("borrowersbase.money,borrowersbase.isCard,borrowersbase.income,borrowersbase.age,borrowersbase.phone");
        sqlbuffer
                .append("CASE WHEN borrowersbase.marryStatus =1 THEN '已婚' ELSE '未婚' END,");
        sqlbuffer
                .append("CASE WHEN borrowersbase.sex =1 THEN '男' ELSE '女' END,");
        sqlbuffer
                .append("borrowersbase.remark FROM borrowersbase WHERE borrowersbase.userbasicinfo_id="
                        + Long.parseLong(ids));

        return dao.findBySql(sqlbuffer.toString());

    }

    /**
     * 根据编号查询借款人基本信息
     * 
     * @param ids
     *            主键id
     * @return list
     */
    @SuppressWarnings("unchecked")
    public List queryBorrowBasinfo(String ids) {

        StringBuffer sqlbuffer = new StringBuffer(
                "SELECT userbasicsinfo.userName,borrowersbase.suminte, borrowersbase.credituserbasicsinfo.`name`");
        sqlbuffer
                .append(", CASE WHEN vipinfo.endtime > (SELECT NOW()) THEN '特权会员' ELSE '普通会员' END, ");
        sqlbuffer.append("userrelationinfo.phone,userbasicsinfo.createTime,");
        sqlbuffer
                .append("CASE WHEN vipinfo.endtime > (SELECT NOW()) THEN vipinfo.endtime ELSE '永久' END,");
        sqlbuffer
                .append("userrelationinfo.cardId,userrelationinfo.email FROM userbasicsinfo ");
        sqlbuffer
                .append("INNER JOIN borrowersbase ON userbasicsinfo.id=borrowersbase.userbasicinfo_id");
        sqlbuffer
                .append("INNER JOIN userrelationinfo ON userbasicsinfo.id=userrelationinfo.user_id");
        sqlbuffer
                .append("INNER JOIN vipinfo ON userbasicsinfo.id=vipinfo.user_id where userbasicsinfo.id="
                        + Long.parseLong(ids));

        return dao.findBySql(sqlbuffer.toString());

    }

    /**
     * 联系方式
     * 
     * @param id
     *            借款人基本信息表主键
     * @return 对象
     */
    public Borrowerscontact queryContact(long id) {

        Borrowersbase borrowers = new Borrowersbase();
        borrowers.setId(id);
        Borrowerscontact contact = new Borrowerscontact();

        contact.setBorrowersbase(borrowers);

        List<Borrowerscontact> contactlist = dao.findByExample(contact);

        if (contactlist != null && !contactlist.isEmpty()) {
            return contactlist.get(0);
        } else {
            return null;
        }

    }

}
