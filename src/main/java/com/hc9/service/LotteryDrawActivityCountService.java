package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.redis.activity.year2016.month04.HcOpenCardActivityCache;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/***
 *  抽奖排位活动
 * @author lkl
 *
 */
@Service
public class LotteryDrawActivityCountService {
	
	@Resource
	private HibernateSupport dao;  
	
	public  List  lotteryDrawActivityPage(PageModel page,ActivityMonkey activityMonkey) {
		List datalist = new ArrayList();
		StringBuffer sqlCount = new StringBuffer("select count(userName) from  ");
		
		StringBuffer sqlbuffer = new StringBuffer("select userName,realName,phone,type, rewardMoney,loanName,createTime from   ");

		StringBuffer sqlsb = new StringBuffer("(select userName,realName,phone,case when rewardMoney <1 THEN type=5 ELSE type END as type , rewardMoney,loanName,createTime from "
				+ "(select u.`name` as userName,u.userName as realName,a.mobilePhone as phone,a.type as type ,a.rewardMoney as rewardMoney ,a.loanName as loanName,a.createTime as createTime ");
		sqlsb.append(" from activity_monkey a ,userbasicsinfo u  where  a.userId=u.id and type in (11,12,13) union all  "
				+ "select u.`name` as userName,u.userName as realName,us.phone as phone,r.sourceType as type,r.money as rewardMoney ,CASE WHEN r.sourceType=4 THEN '' ELSE '' END as loanName,r.receiveTime as createTime from ");
		sqlsb.append(" redenvelopedetail r,userbasicsinfo u, userrelationinfo  us where r.sourceType=4 and r.userId=u.id and u.id=us.id and DATE_FORMAT(r.receiveTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcOpenCardActivityCache.getOpenCardActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(r.receiveTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcOpenCardActivityCache.getOpenCardActivityEndDate()+"','%Y-%m-%d') ");
		sqlsb.append(" union ALL select u.`name` as userName,u.userName as realName,us.phone as phone,r.sourceType as type,r.interestRate as rewardMoney ,CASE WHEN r.sourceType=4 THEN '' ELSE '' END as loanName,r.receiveTime as createTime from ");
		sqlsb.append("  interestincreasecard r,userbasicsinfo u, userrelationinfo  us where r.sourceType=4 and r.userId=u.id and u.id=us.id and DATE_FORMAT(r.receiveTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcOpenCardActivityCache.getOpenCardActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(r.receiveTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcOpenCardActivityCache.getOpenCardActivityEndDate()+"','%Y-%m-%d') ");
		sqlsb.append("  )t)y  where  1=1 " );
		     
		if (activityMonkey.getMobilePhone()!= null && activityMonkey.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(activityMonkey.getMobilePhone(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlsb.append(" and (y.userName  like '%").append(name).append("%'  or y.phone like '%").append(activityMonkey.getMobilePhone()).append("%')");
		}
		
		if(activityMonkey.getType() == null) {
			activityMonkey.setType(2);
		}
		if (activityMonkey.getType()!=null&&activityMonkey.getType()!=0) {
			 if(activityMonkey.getType()==1){
				 sqlsb.append(" AND y.type in (4,11) ");
			 }else if(activityMonkey.getType()==2){
				 sqlsb.append(" AND y.type in (12,13) ");
			 }else if(activityMonkey.getType()==12){
				 sqlsb.append(" AND y.type in (12) ");
			 }else if(activityMonkey.getType()==13){
				 sqlsb.append(" AND y.type in (13) ");
			 }
		 }
		sqlsb.append(connectionSql(activityMonkey.getCreateTime(), activityMonkey.getExamineTime()));
		sqlsb.append(" ORDER BY y.createTime DESC ");
		datalist =  dao.pageListBySql(page, sqlCount.append(sqlsb).toString(),sqlbuffer.append(sqlsb).toString(), null);
	    return datalist;
		
	}
	
	/***
	 * 获奖时间
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
    public String connectionSql(String beginDate, String endDate) {
        String sql = "";
        if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql + " AND DATE_FORMAT(y.createTime, '%Y-%m-%d')>=DATE_FORMAT('" + beginDate
                    + "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql + " AND DATE_FORMAT(y.createTime, '%Y-%m-%d')<=DATE_FORMAT('" + endDate
                    + "', '%Y-%m-%d') ";
        }
        return sql;
    }
    
    
	@SuppressWarnings("rawtypes")
	public List querylotteryDrawActivity(ActivityMonkey activityMonkey) {
		List list = new ArrayList();
		StringBuffer sqlsb = new StringBuffer("select userName,phone,type, rewardMoney,loanName,createTime from (select userName,phone,type, rewardMoney,loanName,createTime from (select u.`name` as userName,a.mobilePhone as phone,a.type as type ,a.rewardMoney as rewardMoney ,a.loanName as loanName,a.createTime as createTime ");
		sqlsb.append(" from activity_monkey a ,userbasicsinfo u  where  a.userId=u.id and type in (11,12,13) union all  select u.`name` as userName,us.phone as phone, r.sourceType as type,r.money as rewardMoney ,CASE WHEN r.sourceType=4 THEN '' ELSE '' END as loanName,r.receiveTime as createTime from ");
		sqlsb.append(" redenvelopedetail r,userbasicsinfo u, userrelationinfo  us where r.sourceType=4 and r.userId=u.id and u.id=us.id and DATE_FORMAT(r.receiveTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcOpenCardActivityCache.getOpenCardActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(r.receiveTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcOpenCardActivityCache.getOpenCardActivityEndDate()+"','%Y-%m-%d') ");
		sqlsb.append(" union ALL select u.`name` as userName,us.phone as phone,r.sourceType as type,r.interestRate as rewardMoney ,CASE WHEN r.sourceType=4 THEN '' ELSE '' END as loanName,r.receiveTime as createTime from ");
		sqlsb.append("  interestincreasecard r,userbasicsinfo u, userrelationinfo  us where r.sourceType=4 and r.userId=u.id and u.id=us.id and DATE_FORMAT(r.receiveTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcOpenCardActivityCache.getOpenCardActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(r.receiveTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcOpenCardActivityCache.getOpenCardActivityEndDate()+"','%Y-%m-%d') ");
		sqlsb.append("  )t)y  where  1=1 " );
		     
		if (activityMonkey.getMobilePhone()!= null && activityMonkey.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(activityMonkey.getMobilePhone(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlsb.append(" and (y.userName  like '%").append(name).append("%'  or y.phone like '%").append(activityMonkey.getMobilePhone()).append("%')");
		}
		
		if (activityMonkey.getType()!=null&&activityMonkey.getType()!=0) {
			 if(activityMonkey.getType()==1){
				 sqlsb.append(" AND y.type in (4,11) ");
			 }else if(activityMonkey.getType()==2){
				 sqlsb.append(" AND y.type in (12,13) ");
			 }
		 }
		sqlsb.append(connectionSql(activityMonkey.getCreateTime(), activityMonkey.getExamineTime()));
		sqlsb.append(" ORDER BY y.createTime DESC ");
		list = dao.findBySql(sqlsb.toString());
		return list;
	}
}
