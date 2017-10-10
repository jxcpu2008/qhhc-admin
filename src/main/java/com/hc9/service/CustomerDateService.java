package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class CustomerDateService {
	
	@Resource
	private HibernateSupport dao;
	
	/***
	 * 投资查询
	 * @param beginDate
	 * @param endDate
	 * @param userType
	 * @param notIn
	 * @return
	 */
    public String connectionCustomerSql(String beginDate, String endDate,Integer userType) {
        String sql = "";
        if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql
                    + " AND DATE(lr.tenderTime)>=DATE('"
                    + beginDate + "') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql
            		+ " AND DATE(lr.tenderTime)<=DATE('" + endDate + "') ";
        }
        if(userType!=null&& !userType.equals("")){
        	if(userType==1){
        		sql += " and lr.userbasicinfo_id IN (SELECT u0.id FROM userbasicsinfo u0 WHERE u0.user_type=1 AND u0.id NOT IN (SELECT g.uid FROM generalize g )) ";
        	}else{
        		sql += " AND u.user_type="+userType;
        	}

        	 
        }
        return sql;
    }
    
    /***
     * 推荐投资查询
     * @param beginDate
     * @param endDate
     * @param userType
     * @param notIn
     * @return
     */
    public String connectionGCustomerSql(String beginDate, String endDate,Integer userType) {
        String sql = "";
        if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql
                    + " AND DATE(lr.tenderTime)>=DATE('" + beginDate + "') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql
            		+ " AND DATE(lr.tenderTime)<=DATE('" + endDate + "') ";
        }
        if(userType!=null&& !userType.equals("")){
        	 sql = sql +" AND u1.user_type="+userType;
        }
        return sql;
    }
    
    public String connectionUserSql(String beginDate, String endDate,Integer userType,boolean notIn) {
        String sql = "";
        if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(u.createTime, '%Y-%m-%d')>=DATE_FORMAT('"
                    + beginDate + "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql
            		+ " AND DATE_FORMAT(u.createTime, '%Y-%m-%d')<=DATE_FORMAT('"
                    + endDate + "', '%Y-%m-%d') ";
        }
        if(userType!=null&& !userType.equals("")){
        	 sql = sql +" AND u.user_type="+userType;
        }
        if(notIn){
        	sql=sql+" AND u.id NOT IN (SELECT g.uid FROM generalize g)";
        }
        return sql;
    }
    
    public String connectionGUserSql(String beginDate, String endDate,Integer userType) {
        String sql = "";
        if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(u2.createTime, '%Y-%m-%d')>=DATE_FORMAT('"
                    + beginDate + "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql
            		+ " AND DATE_FORMAT(u2.createTime, '%Y-%m-%d')<=DATE_FORMAT('"
                    + endDate + "', '%Y-%m-%d') ";
        }
        if(userType!=null&& !userType.equals("")){
        	 sql = sql +" AND u1.user_type="+userType;
        }
        return sql;
    }
	
    /***
     * 员工投资明细/会员投资明细
     * @param page
     * @param beginDate
     * @param endDate
     * @return
     */
	public  List  getCustomerPlank(PageModel page,String beginDate, String endDate,Integer userType){
		StringBuffer countsql = new StringBuffer("select count(lr.id) from  loanrecord lr JOIN userbasicsinfo u ON lr.userbasicinfo_id=u.id   ");
		        countsql.append( "WHERE  lr.isSucceed=1 "+connectionCustomerSql(beginDate,endDate,userType));
		        
		StringBuffer sqlbuffer = new StringBuffer("select u.userName,u.`name`,lr.tenderTime,lr.tenderMoney,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=u.userName  LIMIT 0,1 ) as channelName from  loanrecord lr JOIN userbasicsinfo u ON lr.userbasicinfo_id=u.id   ");
			    sqlbuffer.append( "WHERE  lr.isSucceed=1 "+connectionCustomerSql(beginDate,endDate,userType));
				sqlbuffer.append(" ORDER BY lr.tenderTime DESC  ");
		List datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return  datalist;
	}
	
    /***
     * 员工投资明细/会员投资明细导出
     * @param page
     * @param beginDate
     * @param endDate
     * @return
     */
	public  List<Object>  getCustomerPlankOut(String beginDate, String endDate,Integer userType){
		StringBuffer sqlbuffer = new StringBuffer("select u.userName,u.`name`,lr.tenderTime,lr.tenderMoney,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=u.userName  LIMIT 0,1 ) as channelName from  loanrecord lr JOIN userbasicsinfo u ON lr.userbasicinfo_id=u.id   ");
			    sqlbuffer.append( "WHERE  lr.isSucceed=1 "+connectionCustomerSql(beginDate,endDate,userType));
				sqlbuffer.append(" ORDER BY lr.tenderTime DESC  ");
		List datalist = dao.findBySql(sqlbuffer.toString());
		return  datalist;
	}
	
	/***
	 * 员工推荐人投资明细/会员推荐人投资明细
	 * @param page
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List getCustomerPlankGeneralize (PageModel page,String beginDate, String endDate,Integer userType){
		StringBuffer countsql = new StringBuffer("SELECT count(g.id) FROM generalize g LEFT JOIN userbasicsinfo u1 ON g.genuid=u1.id LEFT JOIN userbasicsinfo u2 ON g.uid=u2.id LEFT JOIN loanrecord lr ON u2.id=lr.userbasicinfo_id  ");
		         countsql.append( "WHERE lr.isSucceed=1 AND u2.user_type=1 "+connectionGCustomerSql(beginDate,endDate,userType));
        
		StringBuffer sqlbuffer = new StringBuffer("SELECT u1.`name` , (select u3.`name` from userbasicsinfo u3 where u3.id=u2.id) as userName ,lr.tenderMoney,lr.tenderTime,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=u2.userName  LIMIT 0,1 ) as channelName FROM generalize g LEFT JOIN userbasicsinfo u1 ON g.genuid=u1.id LEFT JOIN userbasicsinfo u2 ON g.uid=u2.id LEFT JOIN loanrecord lr ON u2.id=lr.userbasicinfo_id  ");
			    sqlbuffer.append( "WHERE lr.isSucceed=1 AND u2.user_type=1 "+connectionGCustomerSql(beginDate,endDate,userType));
				sqlbuffer.append(" ORDER BY lr.tenderTime DESC  ");
		List datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return  datalist;
	}
	
	/***
	 * 员工推荐人投资明细/会员推荐人投资明细导出
	 * @param page
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List<Object>  getCustomerPlankGeneralizeOut (String beginDate, String endDate,Integer userType){
		StringBuffer sqlbuffer = new StringBuffer("SELECT u1.`name` , (select u3.`name` from userbasicsinfo u3 where u3.id=u2.id) as userName ,lr.tenderMoney,lr.tenderTime,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=u2.userName  LIMIT 0,1 ) as channelName FROM generalize g LEFT JOIN userbasicsinfo u1 ON g.genuid=u1.id LEFT JOIN userbasicsinfo u2 ON g.uid=u2.id LEFT JOIN loanrecord lr ON u2.id=lr.userbasicinfo_id  ");
			    sqlbuffer.append( "WHERE lr.isSucceed=1 AND u2.user_type=1 "+connectionGCustomerSql(beginDate,endDate,userType));
				sqlbuffer.append(" ORDER BY lr.tenderTime DESC  ");
	    List datalist = dao.findBySql(sqlbuffer.toString());
		return  datalist;
	}
	
	/***
	 * 员工注册明细/会员注册明细
	 * @param page
	 * @param beginDate
	 * @param endDate
	 * @param userType
	 * @return
	 */
	public  List   getUserbasicsinfo(PageModel page,String beginDate, String endDate,Integer userType,boolean notIn){
		StringBuffer countsql = new StringBuffer("select count(u.id) from   userbasicsinfo u   ");
					countsql.append( "WHERE  1=1 "+connectionUserSql(beginDate,endDate,userType,notIn));
        
        StringBuffer sqlbuffer = new StringBuffer("select u.userName,u.`name`,u.createTime,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=u.userName  LIMIT 0,1 ) as channelName from  userbasicsinfo u   ");
				    sqlbuffer.append( "WHERE 1=1 "+connectionUserSql(beginDate,endDate,userType,notIn));
					sqlbuffer.append(" ORDER BY u.createTime DESC  ");
        List datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
        return datalist;
	}
	
	/***
	 * 员工注册明细/会员注册明细导出
	 * @param page
	 * @param beginDate
	 * @param endDate
	 * @param userType
	 * @return
	 */
	public  List<Object>   getUserbasicsinfoOut(String beginDate, String endDate,Integer userType,boolean notIn){
		 StringBuffer sqlbuffer = new StringBuffer("select u.userName,u.`name`,u.createTime ,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=u.userName  LIMIT 0,1 ) as channelName from  userbasicsinfo u   ");
				    sqlbuffer.append( "WHERE 1=1 "+connectionUserSql(beginDate,endDate,userType,notIn));
					sqlbuffer.append(" ORDER BY u.createTime DESC  ");
        List datalist =dao.findBySql(sqlbuffer.toString());
        return datalist;
	}
	
	
	/***
	 * 员工推荐人注册明细/会员推荐人注册明细
	 * @param page
	 * @param beginDate
	 * @param endDate
	 * @param userType
	 * @return
	 */
	public  List   getUserbasicsinfoGeneralize(PageModel page,String beginDate, String endDate,Integer userType){
		StringBuffer countsql = new StringBuffer("select count(g.id) FROM generalize g JOIN userbasicsinfo u1 ON g.genuid=u1.id JOIN userbasicsinfo u2 ON g.uid=u2.id ");
					countsql.append( "WHERE  1=1 "+connectionGUserSql(beginDate,endDate,userType));
        
        StringBuffer sqlbuffer = new StringBuffer("SELECT u1.`name`,(select u3.`name` from userbasicsinfo u3 where u3.id=u2.id),u2.createTime,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=u2.userName  LIMIT 0,1 ) as channelName  FROM generalize g JOIN userbasicsinfo u1 ON g.genuid=u1.id JOIN userbasicsinfo u2 ON g.uid=u2.id ");
				    sqlbuffer.append( "WHERE 1=1  "+connectionGUserSql(beginDate,endDate,userType));
					sqlbuffer.append(" ORDER BY u2.createTime DESC  ");
        List datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
        return datalist;
	}
	
	/***
	 * 员工推荐人注册明细/会员推荐人注册明细导出
	 * @param page
	 * @param beginDate
	 * @param endDate
	 * @param userType
	 * @return
	 */
	public  List<Object>   getUserbasicsinfoGeneralizeOut(String beginDate, String endDate,Integer userType){
		StringBuffer sqlbuffer = new StringBuffer("SELECT u1.`name`,(select u3.`name` from userbasicsinfo u3 where u3.id=u2.id),u2.createTime,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=u2.userName  LIMIT 0,1 ) as channelName  FROM generalize g JOIN userbasicsinfo u1 ON g.genuid=u1.id JOIN userbasicsinfo u2 ON g.uid=u2.id ");
			    sqlbuffer.append( "WHERE 1=1  "+connectionGUserSql(beginDate,endDate,userType));
				sqlbuffer.append(" ORDER BY u2.createTime DESC  ");
        List datalist  =dao.findBySql(sqlbuffer.toString());
        return datalist;
	}
	

}
