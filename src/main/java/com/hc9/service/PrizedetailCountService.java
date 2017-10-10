package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.redis.activity.year2016.month04.HcClimbTopActivityCache;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class PrizedetailCountService {
	@Resource
	private HibernateSupport dao;  
	
	public  List  PrizedetailCountPage(PageModel page,Userbasicsinfo user) {
		List datalist = new ArrayList();
		StringBuffer sqlCount = new StringBuffer("select count(name) from  ");
		
		StringBuffer sqlbuffer = new StringBuffer("select name,phone,tenderMoney, prizeType,receiveTime from  ");

		StringBuffer sqlsb = new StringBuffer("  (select name,phone,tenderMoney, prizeType,receiveTime from  (select u.`name` as name,us.phone as phone ,ifnull((select totalMoney from  (select userbasicinfo_id as userId,sum(investMoney) as totalMoney from ");
		sqlsb.append(" (select (l.tenderMoney * s.remonth) as investMoney,l.userbasicinfo_id from loanrecord l, loansign s  where l.isSucceed=1 and DATE_FORMAT(l.tenderTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(l.tenderTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityEndDate()+"','%Y-%m-%d')");
		sqlsb.append(" and s.id=l.loanSign_id and s.`type` != 3 and l.subType in(1,2) union all select l.tenderMoney as investMoney,l.userbasicinfo_id from loanrecord l, loansign s  where l.isSucceed=1 and DATE_FORMAT(l.tenderTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(l.tenderTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityEndDate()+"','%Y-%m-%d')");
		sqlsb.append(" and s.id=l.loanSign_id and s.`type` = 3 and l.subType in(1,2)) t group by userId) a where a.userId=p.userId),0) as tenderMoney,p.prizeType as prizeType,p.receiveTime as receiveTime from prizedetail p,userbasicsinfo u,userrelationinfo us where p.userId=u.id and u.id=us.id  and p.prizeType in (13,14,15,16)  union all ");
		sqlsb.append(" select u.name as name ,us.phone as phone,ifnull((select totalMoney from 	(select userbasicinfo_id as userId,sum(investMoney) as totalMoney from (select (l.tenderMoney * s.remonth) as investMoney,l.userbasicinfo_id from loanrecord l, loansign s  ");
		sqlsb.append(" where l.isSucceed=1 and DATE_FORMAT(l.tenderTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(l.tenderTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityEndDate()+"','%Y-%m-%d')  and s.id=l.loanSign_id and s.`type` != 3 and l.subType in(1,2)  union all   ");
		sqlsb.append(" select l.tenderMoney as investMoney,l.userbasicinfo_id from loanrecord l, loansign s  where l.isSucceed=1 and DATE_FORMAT(l.tenderTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(l.tenderTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityEndDate()+"','%Y-%m-%d') ");
		sqlsb.append(" 	and s.id=l.loanSign_id and s.`type` = 3 and l.subType in(1,2)) t group by userId) a where a.userId=r.userId),0)as tenderMoney,  r.sourceType as prizeType ,r.receiveTime as  receiveTime from redenvelopedetail r, userbasicsinfo u, userrelationinfo us where r.userId=u.id and r.userId=us.id and r.sourceType=8 ");
		sqlsb.append(" ) s ) b  where 1=1 ");
		if (user.getName()!= null && user.getName() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(user.getName(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlsb.append(" and (b.name  like '%").append(name).append("%'  or b.phone like '%").append(user.getName()).append("%')");
		}
		sqlsb.append(connectionSql(user.getAuthIpsTime(), user.getLoginTime()));
		sqlsb.append(" ORDER BY b.tenderMoney DESC ");
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
            sql = sql + " AND DATE_FORMAT(b.receiveTime, '%Y-%m-%d')>=DATE_FORMAT('" + beginDate
                    + "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql + " AND DATE_FORMAT(b.receiveTime, '%Y-%m-%d')<=DATE_FORMAT('" + endDate
                    + "', '%Y-%m-%d') ";
        }
        return sql;
    }
    
	@SuppressWarnings("rawtypes")
	public List queryPrizedetail(Userbasicsinfo user) {
		List list = new ArrayList();
		StringBuffer sqlsb = new StringBuffer(" select name,phone,tenderMoney, prizeType,receiveTime from (select name,phone,tenderMoney, prizeType,receiveTime from  (select u.`name` as name,us.phone as phone ,ifnull((select totalMoney from  (select userbasicinfo_id as userId,sum(investMoney) as totalMoney from ");
		sqlsb.append(" (select (l.tenderMoney * s.remonth) as investMoney,l.userbasicinfo_id from loanrecord l, loansign s  where l.isSucceed=1 and DATE_FORMAT(l.tenderTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(l.tenderTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityEndDate()+"','%Y-%m-%d')");
		sqlsb.append(" and s.id=l.loanSign_id and s.`type` != 3 and l.subType in(1,2) union all select l.tenderMoney as investMoney,l.userbasicinfo_id from loanrecord l, loansign s  where l.isSucceed=1 and DATE_FORMAT(l.tenderTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(l.tenderTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityEndDate()+"','%Y-%m-%d')");
		sqlsb.append(" and s.id=l.loanSign_id and s.`type` = 3 and l.subType in(1,2)) t group by userId) a where a.userId=p.userId),0) as tenderMoney,p.prizeType as prizeType,p.receiveTime as receiveTime from prizedetail p,userbasicsinfo u,userrelationinfo us where p.userId=u.id and u.id=us.id  and p.prizeType in (13,14,15,16)  union all ");
		sqlsb.append(" select u.name as name ,us.phone as phone,ifnull((select totalMoney from 	(select userbasicinfo_id as userId,sum(investMoney) as totalMoney from (select (l.tenderMoney * s.remonth) as investMoney,l.userbasicinfo_id from loanrecord l, loansign s  ");
		sqlsb.append(" where l.isSucceed=1 and DATE_FORMAT(l.tenderTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(l.tenderTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityEndDate()+"','%Y-%m-%d')  and s.id=l.loanSign_id and s.`type` != 3 and l.subType in(1,2)  union all   ");
		sqlsb.append(" select l.tenderMoney as investMoney,l.userbasicinfo_id from loanrecord l, loansign s  where l.isSucceed=1 and DATE_FORMAT(l.tenderTime, '%Y-%m-%d')>=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityBeginDate()+"','%Y-%m-%d')  and  DATE_FORMAT(l.tenderTime, '%Y-%m-%d')<=DATE_FORMAT('"+HcClimbTopActivityCache.getClimbTopActivityEndDate()+"','%Y-%m-%d') ");
		sqlsb.append(" 	and s.id=l.loanSign_id and s.`type` = 3 and l.subType in(1,2)) t group by userId) a where a.userId=r.userId),0)as tenderMoney,  r.sourceType as prizeType ,r.receiveTime as  receiveTime from redenvelopedetail r, userbasicsinfo u, userrelationinfo us where r.userId=u.id and r.userId=us.id and r.sourceType=8 ");
		sqlsb.append(" ) s ) b  where 1=1 ");
		if (user.getName()!= null && user.getName() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(user.getName(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlsb.append(" and (b.name  like '%").append(name).append("%'  or b.phone like '%").append(user.getName()).append("%')");
		}
		sqlsb.append(connectionSql(user.getAuthIpsTime(), user.getLoginTime()));
		sqlsb.append(" ORDER BY b.tenderMoney DESC ");
		list = dao.findBySql(sqlsb.toString());
		return list;
	}

}
