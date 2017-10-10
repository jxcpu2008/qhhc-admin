package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * 充值记录
 * 
 * @author Administrator
 * 
 */
@Service
@SuppressWarnings("rawtypes")
public class RechargeAdminService {

    /**
     * 数据库接口
     */
    @Resource
    private HibernateSupport commonDao;

    @Resource
    private UserBaseInfoService userBaseInfoService;

    /**
     * sql语句拼接
     * 
     * @param beginDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param fieldName
     *            字段名称
     * @return 拼接过后的sql语句
     */
    public String connectionSql(String beginDate, String endDate,
            String fieldName, String userName) {
        String sql = "";
        if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql + " AND DATE_FORMAT(" + fieldName
                    + ", '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('" + beginDate
                    + "', '%Y-%m-%d %H:%i:%s') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql + " AND DATE_FORMAT(" + fieldName
                    + ", '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('" + endDate
                    + "', '%Y-%m-%d %H:%i:%s') ";
        }
        if (StringUtil.isNotBlank(userName)) {
            sql = sql + " AND b.userName like '%" + userName + "%'";
        }
        return sql;
    }

    /**
     * 充值记录条数
     * 
     * @param beginDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param userName
     *            用户名
     * @return 记录条数
     */
    public Integer queryCount(String beginDate, String endDate,
            String userName) {
        String sql = "SELECT count(*) FROM  recharge a,"
                + "userbasicsinfo b WHERE a.user_id=b.id "
                + connectionSql(beginDate, endDate, "a.time", userName);
        Object obj = commonDao.findObjectBySql(sql);
        return Integer.parseInt(obj.toString());
    }

    /**
     * 充值记录
     * 
     * @param page
     *            分页对象
     * @param beginDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param userName
     *            用户名
     * @return 记录条数
     */
    public List queryPage(PageModel page, String beginDate, String endDate,String userName) {
        String sql = "SELECT a.id,b.name,b.userName,a.rechargeAmount,a.reAccount,a.orderNum,a.time,a.`status` FROM "
                + " recharge a,userbasicsinfo b WHERE a.user_id=b.id "
                + connectionSql(beginDate, endDate, "a.time", userName)
                + " ORDER BY a.time DESC LIMIT "
                + page.firstResult() + "," + page.getNumPerPage();
        List list = commonDao.findBySql(sql);
        return list;
    }
    
    /**
     *根据标号查询充值记录 
     * @param ids
     * @return
     */
    public List queryById(String ids){
    	StringBuffer sql=new StringBuffer(
    			"select"
    			+ " b.name,b.userName,a.rechargeAmount,a.reAccount,a.orderNum ,a.time"
    			+ " FROM recharge a,userbasicsinfo b"
    			+ " WHERE a.user_id=b.id");
    	if(ids!=null && !"".equals(ids.trim())){
    		ids=ids.substring(0,ids.lastIndexOf(","));
    		sql.append(" and a.id in("+ids+")");
    	}
    	List list=commonDao.findBySql(sql.toString());
    	return list;
    }
}