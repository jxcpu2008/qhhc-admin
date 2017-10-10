package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
@Service
@SuppressWarnings("rawtypes")
public class MerUserInfoService {
	
    /**
     * 数据库接口
     */
    @Resource
    private HibernateSupport commonDao;
	
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
        String sql = "SELECT count(*) FROM  "
                + "userbasicsinfo a,userfundinfo b WHERE a.id=b.id  and b.pIdentNo is not null and b.pIdentNo<>'' "
                + connectionSql(beginDate, endDate, "a.pIpsAcctDate", userName);
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
        String sql = "SELECT a.id,a.name,a.userName,a.pMerBillNo,a.pIpsAcctDate,b.pIdentNo FROM "
                + " userbasicsinfo a,userfundinfo b WHERE a.id=b.id  and b.pIdentNo is not null and b.pIdentNo<>''  "
                + connectionSql(beginDate, endDate, "a.pIpsAcctDate", userName)
                + " ORDER BY a.pIpsAcctDate DESC LIMIT "
                + page.firstResult() + "," + page.getNumPerPage();
        List list = commonDao.findBySql(sql);
        return list;
    }
    
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
            sql = sql + " AND a.userName like '%" + userName + "%'";
        }
        return sql;
    }

}
