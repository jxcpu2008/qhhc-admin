package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * 提现记录
 * 
 * @author Administrator
 * 
 */
@Service
@SuppressWarnings("rawtypes")
public class WithdrawAdminService {

    /**
     * 数据库接口
     */
    @Resource
    private HibernateSupport commonDao;

    @Resource
    private UserBaseInfoService userBaseInfoService;
    
    /**
     * sql拼接
     * @param beginDate
     * @param endDate
     * @param userName
     * @return
     */
    public String connectionSql(String beginDate, String endDate,String userName,Integer isCreditor,Integer state) {
        String sql = "";
        if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(a.applytime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
                    + beginDate + "', '%Y-%m-%d %H:%i:%s') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(a.applytime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
                    + endDate + "', '%Y-%m-%d %H:%i:%s') ";
        }
        if (userName != null && !userName.trim().equals("")) {
            sql = sql + " AND b.userName LIKE '%" + userName + "%'";
        }
        if (isCreditor != null && !isCreditor.equals("")&&isCreditor!=0) {
            sql = sql + " AND b.isCreditor = " + isCreditor ;
        }
        if (state != null && !state.equals("")&&state!=-2) {
            sql = sql + " AND a.state = " + state ;
        }
        return sql;
    }

    /**
     * 查询提现条数
     * 
     * @param beginDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param userName
     *            用户名
     * @param strNum
     *            打款流水号
     * @param withdrawstate
     *            状态
     * @return 提现条数
     */
    public Integer queryCount(String beginDate, String endDate, String userName,Integer isCreditor,Integer state) {
        String sql = "SELECT count(*)  FROM withdraw a,userbasicsinfo b "
                + "WHERE a.user_id=b.id"
                + connectionSql(beginDate, endDate, userName,isCreditor,state);
        Object obj = commonDao.findObjectBySql(sql);
        return Integer.parseInt(obj.toString());
    }

    /**
     * 查询提现记录
     * 
     * @param page
     *            分页对象
     * @param beginDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param userName
     *            用户名
     * @return 提现记录
     */
    public List queryPage(PageModel page, String beginDate, String endDate, String userName,Integer isCreditor,Integer state) {
        String sql = "SELECT a.id,b.name,b.userName,a.amount,"
                + "a.fee,a.mer_fee,a.strNum,a.state,a.time,a.remark,b.isCreditor,a.withdrawAmount,a.fee_taken_on  "
                + "FROM withdraw a,userbasicsinfo b WHERE "
                + "a.user_id=b.id"
                + connectionSql(beginDate, endDate, userName,isCreditor,state) +" ORDER BY a.applytime DESC LIMIT "
                + page.firstResult() + "," + page.getNumPerPage();
        List list = commonDao.findBySql(sql);
        return list;
    }


    /**
     * 根据编号查询提现记录
     * 
     * @param ids
     *            编号
     * @return 提现记录
     */
    public List queryById(String ids,String beginDate, String endDate, String userName,Integer isCreditor,Integer state) {
        StringBuffer sql = new StringBuffer("SELECT a.id,b.name,b.userName,ifnull(ROUND(a.amount,2),0.00),"
                + "ifnull(ROUND(a.fee,2),0.00),ifnull(ROUND(a.mer_fee,2),0.00),a.strNum,  CASE WHEN a.state =0  THEN  '待处理 '    WHEN a.state =1  THEN  '提现成功 '  WHEN  a.state =2 THEN  '宝付已受理' "
                + " WHEN  a.state =5 THEN  '转账处理中' ELSE '提现失败' END , CASE WHEN b.isCreditor=1  THEN  '投资人'  WHEN  b.isCreditor =2 THEN  '借款人' END, a.time,a.remark,ifnull(ROUND(a.withdrawAmount,2),0.00),CASE WHEN a.fee_taken_on=1  THEN  '平台支付'  WHEN a.fee_taken_on=2  THEN  '用户支付' END "
                + "FROM withdraw a,userbasicsinfo b WHERE "
                + "a.user_id=b.id");
        if (ids != null && !ids.trim().equals("")) {
            ids = ids.substring(0, ids.lastIndexOf(","));
            sql.append(" AND a.id in (" + ids + ")");
        }
        sql.append(connectionSql(beginDate, endDate, userName,isCreditor,state));
        List list = commonDao.findBySql(sql.toString());
        return list;
    }
}
