package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.RedEnvelopeDetail;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/***
 * 红包统计查询
 * @author lkl
 *
 */
@Service
public class RedenvelopeDetailCountService {
	
	@Resource
	private HibernateSupport dao;  
	
	
	/***
	 *  列表查询
	 * @param page
	 * @param loansign
	 * @return
	 */
	public  List redEnvelopeDetailPage(PageModel page, RedEnvelopeDetail redEnvelopeDetail) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("select count(r.id)  from redenvelopedetail r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id  ");
		StringBuffer sqlbuffer = new StringBuffer("select r.id,u.userName,u.`name`,ur.phone,u.createTime,u.registerSource,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 ");
	    sqlbuffer.append("  and c.regUserName=u.userName  LIMIT 0,1) as channelName ,r.sourceType,r.money,r.receiveTime,r.useFlag,IFNULL((select l.tenderMoney from loanrecord l where l.id=r.loanrecord_id and l.isSucceed=1 ),0) as tenderMoney,r.consumeTime,r.endTime from redenvelopedetail r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id ");
	    if (redEnvelopeDetail.getSourceType() != null && redEnvelopeDetail.getSourceType() != 0) {
			sqlbuffer.append(" AND r.sourceType=" + redEnvelopeDetail.getSourceType());
			countsql.append(" AND  r.sourceType=" +  redEnvelopeDetail.getSourceType());
		}
	    if(redEnvelopeDetail.getUseFlag()!=null&&  redEnvelopeDetail.getUseFlag() != 0) {
			sqlbuffer.append(" AND u.registerSource=" + redEnvelopeDetail.getUseFlag());
			countsql.append(" AND  u.registerSource=" +  redEnvelopeDetail.getUseFlag());
		}
	    sqlbuffer.append(connectionSql(redEnvelopeDetail.getBeginTime(),redEnvelopeDetail.getEndTime()));
	    countsql.append(connectionSql(redEnvelopeDetail.getBeginTime(),redEnvelopeDetail.getEndTime()));
	    
	    if (redEnvelopeDetail.getMoney() != null && redEnvelopeDetail.getMoney() != 0) {
				sqlbuffer.append(" AND r.money=" + redEnvelopeDetail.getMoney());
				countsql.append(" AND  r.money=" +  redEnvelopeDetail.getMoney());
		}
	    
	    if (redEnvelopeDetail.getCreateTime() != null && redEnvelopeDetail.getCreateTime() != "") {
			sqlbuffer.append(" AND ur.phone=" + redEnvelopeDetail.getCreateTime());
			countsql.append(" AND  ur.phone=" +  redEnvelopeDetail.getCreateTime());
	    }
	    
	    sqlbuffer.append(connectionTimeSql(redEnvelopeDetail.getReceiveTime(),redEnvelopeDetail.getConsumeTime()));
	    countsql.append(connectionTimeSql(redEnvelopeDetail.getReceiveTime(),redEnvelopeDetail.getConsumeTime()));
	    
	    sqlbuffer.append(" ORDER BY r.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
	    return datalist;
	}
	
	/***
	 * 发放时间
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
    public String connectionSql(String beginDate, String endDate) {
        String sql = "";
        if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql + " AND DATE_FORMAT(r.receiveTime, '%Y-%m-%d')>=DATE_FORMAT('" + beginDate
                    + "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql + " AND DATE_FORMAT(r.receiveTime, '%Y-%m-%d')<=DATE_FORMAT('" + endDate
                    + "', '%Y-%m-%d') ";
        }
        return sql;
    }
    
    /***
     * 使用时间
     * @param beginDate
     * @param endDate
     * @return
     */
    public String connectionTimeSql(String beginDate, String endDate) {
        String sql = "";
        if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql + " AND DATE_FORMAT(r.consumeTime, '%Y-%m-%d')>=DATE_FORMAT('" + beginDate
                    + "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql + " AND DATE_FORMAT(r.consumeTime, '%Y-%m-%d')<=DATE_FORMAT('" + endDate
                    + "', '%Y-%m-%d') ";
        }
        return sql;
    }
    
	@SuppressWarnings("rawtypes")
	public List queryRedEnvelopeDetail(RedEnvelopeDetail redEnvelopeDetail) {
		List list = new ArrayList();
		StringBuffer sqlbuffer = new StringBuffer("select r.id,u.userName,u.`name`,ur.phone,u.createTime,u.registerSource,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 ");
	    sqlbuffer.append("  and c.regUserName=u.userName   LIMIT 0,1 ) as channelName ,r.sourceType,r.money,r.receiveTime,r.useFlag,IFNULL((select l.tenderMoney from loanrecord l where l.id=r.loanrecord_id and l.isSucceed=1 ),0) as tenderMoney,r.consumeTime,r.endTime from redenvelopedetail r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id ");
	    if (redEnvelopeDetail.getSourceType() != null && redEnvelopeDetail.getSourceType() != 0) {
			sqlbuffer.append(" AND r.sourceType=" + redEnvelopeDetail.getSourceType());
		}
	    if(redEnvelopeDetail.getUseFlag()!=null&&  redEnvelopeDetail.getUseFlag() != 0) {
			sqlbuffer.append(" AND u.registerSource=" + redEnvelopeDetail.getUseFlag());
		}
	    sqlbuffer.append(connectionSql(redEnvelopeDetail.getBeginTime(),redEnvelopeDetail.getEndTime()));
	    
	    if (redEnvelopeDetail.getMoney() != null && redEnvelopeDetail.getMoney() != 0) {
				sqlbuffer.append(" AND r.money=" + redEnvelopeDetail.getMoney());
		}
	    
	    if (redEnvelopeDetail.getCreateTime() != null && redEnvelopeDetail.getCreateTime() != "") {
			sqlbuffer.append(" AND ur.phone=" + redEnvelopeDetail.getCreateTime());
	    }
	    sqlbuffer.append(connectionTimeSql(redEnvelopeDetail.getReceiveTime(),redEnvelopeDetail.getConsumeTime()));
	    
	    sqlbuffer.append(" ORDER BY r.id desc");
		list = dao.findBySql(sqlbuffer.toString());
		return list;
	}
	
	 public String returnRedEnvelopeDetailSql(RedEnvelopeDetail redEnvelopeDetail){
		    String sql = "";
		    if (redEnvelopeDetail.getSourceType() != null && redEnvelopeDetail.getSourceType() != 0) {
		    	sql+=" AND r.sourceType=" + redEnvelopeDetail.getSourceType();
			}
		    if(redEnvelopeDetail.getUseFlag()!=null&&  redEnvelopeDetail.getUseFlag() != 0) {
		    	sql+=" AND u.registerSource=" + redEnvelopeDetail.getUseFlag();
			}
		    
		    sql+=connectionSql(redEnvelopeDetail.getBeginTime(),redEnvelopeDetail.getEndTime());
		    
		    if (redEnvelopeDetail.getMoney() != null && redEnvelopeDetail.getMoney() != 0) {
		    	sql+=" AND r.money=" + redEnvelopeDetail.getMoney();
			}
		    
		    if (redEnvelopeDetail.getCreateTime() != null && redEnvelopeDetail.getCreateTime() != "") {
		    	sql+=" AND ur.phone=" + redEnvelopeDetail.getCreateTime();
		    }
		    sql+=connectionTimeSql(redEnvelopeDetail.getReceiveTime(),redEnvelopeDetail.getConsumeTime());
		    return sql;
	 }
	
	   
	   /***
	    * 发放红包总人数
	    * @param 
	    * @return
	    */
	   public Integer getDistinctRedCount(Integer userflag,RedEnvelopeDetail redEnvelopeDetail,Integer timeState){
		   String sql="select count(distinct(r.userId))  from redenvelopedetail r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id ";
		   if(userflag==1){
			   sql+=" and r.useFlag=1 ";
		   }
		   if(timeState==1){
			   sql+=returnRedEnvelopeDetailSql(redEnvelopeDetail);
		   }else{
			   sql+=connectionTimeSql(redEnvelopeDetail.getReceiveTime(),redEnvelopeDetail.getConsumeTime());
		   }
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Integer.valueOf(obj.toString()) : 0;
	   }
	
	   /**
	    * 发放红包总金额
	    * @param 
	    * @return
	    */
	   public Double getRedSumMoney(Integer userflag,RedEnvelopeDetail redEnvelopeDetail){
		   String sql="select SUM(r.money)  from redenvelopedetail r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id ";
		   if(userflag==1){
			   sql+=" and r.useFlag=1 ";
		   }
		   sql+=returnRedEnvelopeDetailSql(redEnvelopeDetail);
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	   }
	   
	   /***
	    * 发放红包总张数
	    * @param userflag 1-使用
	    * @return
	    */
	   public Integer getRedCount(Integer userflag,RedEnvelopeDetail redEnvelopeDetail,Integer timeState){
		   String sql="select count(r.id)  from redenvelopedetail r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id ";
		   if(userflag==1){
			   sql+=" and r.useFlag=1 ";
		   }
		   if(timeState==1){
			   sql+=returnRedEnvelopeDetailSql(redEnvelopeDetail);
		   }else{
			   sql+=connectionTimeSql(redEnvelopeDetail.getReceiveTime(),redEnvelopeDetail.getConsumeTime());
		   }
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Integer.valueOf(obj.toString()) : 0;
	   }
	   
	   /***
	    * 使用红包总投资金额
	    * @return
	    */
	   public Double getRedSumTenderMoney(RedEnvelopeDetail redEnvelopeDetail,Integer timeState){
		   String sql="select sum(l.tenderMoney) from redenvelopedetail r,userbasicsinfo u,userrelationinfo ur ,loanrecord l where r.userId=u.id and r.userId=ur.id and r.loanrecord_id=l.id  and l.isSucceed=1 and r.useFlag=1 ";
		   if(timeState==1){
			   sql+=returnRedEnvelopeDetailSql(redEnvelopeDetail);
		   }else{
			   sql+=connectionTimeSql(redEnvelopeDetail.getReceiveTime(),redEnvelopeDetail.getConsumeTime());
		   }
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	   }
	   
	   
	   /***
	    * 该时间段内的所有投资金额
	    * @return
	    */
	   public Double getSumTenderMoney(RedEnvelopeDetail redEnvelopeDetail){
		   String sql="select sum(l.tenderMoney) from loanrecord l, userbasicsinfo u,userrelationinfo ur where l.userbasicinfo_id=u.id and l.userbasicinfo_id=ur.id  and l.isSucceed=1  ";
		   sql+=connectionLoanSql(redEnvelopeDetail.getReceiveTime(),redEnvelopeDetail.getConsumeTime());
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	   }
	
	   /***
	    * 该时间段的认购人数
	    * @return
	    */
	   public Integer getDistinctLoanrecordCount(RedEnvelopeDetail redEnvelopeDetail){
		   String sql="select count(distinct(l.userbasicinfo_id))  from loanrecord l, userbasicsinfo u,userrelationinfo ur where l.userbasicinfo_id=u.id and l.userbasicinfo_id=ur.id and l.isSucceed=1  ";
		   sql+=connectionLoanSql(redEnvelopeDetail.getReceiveTime(),redEnvelopeDetail.getConsumeTime());
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Integer.valueOf(obj.toString()) : 0;
	   }
	   /***
	    * 该时间段内所有的订单数
	    * @return
	    */
	   public Integer getLoanrecordIdCount(RedEnvelopeDetail redEnvelopeDetail){
		   String sql="select count(l.id)  from loanrecord l, userbasicsinfo u,userrelationinfo ur where l.userbasicinfo_id=u.id and l.userbasicinfo_id=ur.id  and l.isSucceed=1  ";
		   sql+=connectionLoanSql(redEnvelopeDetail.getReceiveTime(),redEnvelopeDetail.getConsumeTime());
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Integer.valueOf(obj.toString()) : 0;
	   }
	
	   /***
	    * 购买时间
	    * @param beginDate
	    * @param endDate
	    * @return
	    */
	    public String connectionLoanSql(String beginDate, String endDate) {
	        String sql = "";
	        if (beginDate != null && !"".equals(beginDate.trim())) {
	            sql = sql + " AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d')>=DATE_FORMAT('" + beginDate
	                    + "', '%Y-%m-%d') ";
	        }
	        if (endDate != null && !"".equals(endDate.trim())) {
	            sql = sql + " AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d')<=DATE_FORMAT('" + endDate
	                    + "', '%Y-%m-%d') ";
	        }
	        return sql;
	    }

}
