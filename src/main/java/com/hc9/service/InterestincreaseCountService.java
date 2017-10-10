package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.InterestIncreaseCard;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class InterestincreaseCountService {

	@Resource
	private HibernateSupport dao;  
	
	
	/***
	 *  列表查询
	 * @param page
	 * @param loansign
	 * @return
	 */
	public  List interestincreaseCountPage(PageModel page, InterestIncreaseCard interestIncreaseCard) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("select count(r.id)  from interestincreasecard r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id  ");
		StringBuffer sqlbuffer = new StringBuffer("select r.id,u.userName,u.`name`,ur.phone,u.createTime,u.registerSource,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=u.userName  LIMIT 0,1) as channelName, ");
	    sqlbuffer.append(" r.sourceType,r.interestRate,r.receiveTime,r.useFlag,IFNULL((select l.tenderMoney from loanrecord l where l.id=r.loanrecordId and l.isSucceed=1 ),0) as tenderMoney,r.consumeTime,r.endTime from interestincreasecard r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id ");
	    if (interestIncreaseCard.getSourceType() != null && interestIncreaseCard.getSourceType() != 0) {
			sqlbuffer.append(" AND r.sourceType=" + interestIncreaseCard.getSourceType());
			countsql.append(" AND  r.sourceType=" +  interestIncreaseCard.getSourceType());
		}
	    if(interestIncreaseCard.getUseFlag()!=null&&  interestIncreaseCard.getUseFlag() != 0) {
			sqlbuffer.append(" AND u.registerSource=" + interestIncreaseCard.getUseFlag());
			countsql.append(" AND  u.registerSource=" +  interestIncreaseCard.getUseFlag());
		}
	    sqlbuffer.append(connectionSql(interestIncreaseCard.getBeginTime(),interestIncreaseCard.getEndTime()));
	    countsql.append(connectionSql(interestIncreaseCard.getBeginTime(),interestIncreaseCard.getEndTime()));
	    
	    if (interestIncreaseCard.getInterestRate() != null && interestIncreaseCard.getInterestRate() != 0) {
				sqlbuffer.append(" AND r.interestRate=" + interestIncreaseCard.getInterestRate());
				countsql.append(" AND  r.interestRate=" +  interestIncreaseCard.getInterestRate());
		}
	    
	    if (interestIncreaseCard.getLoanrecordId() != null && interestIncreaseCard.getLoanrecordId() != 0) {
			sqlbuffer.append(" AND ur.phone=" + interestIncreaseCard.getLoanrecordId());
			countsql.append(" AND  ur.phone=" +  interestIncreaseCard.getLoanrecordId());
	    }
	    
	    sqlbuffer.append(connectionTimeSql(interestIncreaseCard.getReceiveTime(),interestIncreaseCard.getConsumeTime()));
	    countsql.append(connectionTimeSql(interestIncreaseCard.getReceiveTime(),interestIncreaseCard.getConsumeTime()));
	    
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
	public List queryInterestincreasecardl(InterestIncreaseCard interestIncreaseCard) {
		List list = new ArrayList();
		StringBuffer sqlbuffer = new StringBuffer("select r.id,u.userName,u.`name`,ur.phone,u.createTime,u.registerSource,(select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=u.userName  LIMIT 0,1 ) as channelName, ");
	    sqlbuffer.append(" r.sourceType,r.interestRate,r.receiveTime,r.useFlag,IFNULL((select l.tenderMoney from loanrecord l where l.id=r.loanrecordId and l.isSucceed=1 ),0) as tenderMoney,r.consumeTime,r.endTime from interestincreasecard r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id ");
	   if (interestIncreaseCard.getSourceType() != null && interestIncreaseCard.getSourceType() != 0) {
			sqlbuffer.append(" AND r.sourceType=" + interestIncreaseCard.getSourceType());
		}
	    if(interestIncreaseCard.getUseFlag()!=null&&  interestIncreaseCard.getUseFlag() != 0) {
			sqlbuffer.append(" AND u.registerSource=" + interestIncreaseCard.getUseFlag());
		}
	    sqlbuffer.append(connectionSql(interestIncreaseCard.getBeginTime(),interestIncreaseCard.getEndTime()));
	    
	    if (interestIncreaseCard.getInterestRate() != null && interestIncreaseCard.getInterestRate() != 0) {
				sqlbuffer.append(" AND r.interestRate=" + interestIncreaseCard.getInterestRate());
		}
	    
	    if (interestIncreaseCard.getLoanrecordId() != null && interestIncreaseCard.getLoanrecordId() != 0) {
			sqlbuffer.append(" AND ur.phone=" + interestIncreaseCard.getLoanrecordId());
	    }
	    sqlbuffer.append(connectionTimeSql(interestIncreaseCard.getReceiveTime(),interestIncreaseCard.getConsumeTime()));
	    
	    sqlbuffer.append(" ORDER BY r.id desc");
		list = dao.findBySql(sqlbuffer.toString());
		return list;
	}
	
	
	 public String returninterestincreaseSql(InterestIncreaseCard interestincreasecard){
		    String sql = "";
		    if (interestincreasecard.getSourceType() != null && interestincreasecard.getSourceType() != 0) {
		    	sql+=" AND r.sourceType=" + interestincreasecard.getSourceType();
			}
		    if(interestincreasecard.getUseFlag()!=null&&  interestincreasecard.getUseFlag() != 0) {
		    	sql+=" AND u.registerSource=" + interestincreasecard.getUseFlag();
			}
		    
		    sql+=connectionSql(interestincreasecard.getBeginTime(),interestincreasecard.getEndTime());
		    
		    if (interestincreasecard.getInterestRate() != null && interestincreasecard.getInterestRate() != 0) {
						sql+=" AND r.interestRate=" + interestincreasecard.getInterestRate();
			}
			    
		    if (interestincreasecard.getLoanrecordId() != null && interestincreasecard.getLoanrecordId() != 0) {
		    	sql+="  AND ur.phone=" + interestincreasecard.getLoanrecordId();
		    }
		    sql+=connectionTimeSql(interestincreasecard.getReceiveTime(),interestincreasecard.getConsumeTime());
		    return sql;
	 }
	
	   
	   /***
	    * 发放加息劵总人数
	    * @param 
	    * @return
	    */
	   public Integer getDistinctIntCount(Integer userflag,InterestIncreaseCard interestincreasecard,Integer timeState){
		   String sql="select count(distinct(r.userId))  from interestincreasecard r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id ";
		   if(userflag==1){
			   sql+=" and r.useFlag=1 ";
		   }
		   if(timeState==1){
			   sql+=returninterestincreaseSql(interestincreasecard);
		   }else{
			   sql+=connectionTimeSql(interestincreasecard.getReceiveTime(),interestincreasecard.getConsumeTime());
		   }
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Integer.valueOf(obj.toString()) : 0;
	   }
	
	   /**
	    * 发放加息劵总金额
	    * @param 
	    * @return
	    */
	   public Double getInterestRateSumMoney(Integer userflag,InterestIncreaseCard interestincreasecard){
		   String sql="select SUM(r.interestRate)  from interestincreasecard r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id ";
		   if(userflag==1){
			   sql+=" and r.useFlag=1 ";
		   }
		   sql+=returninterestincreaseSql(interestincreasecard);
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	   }
	   
	   /***
	    * 发放加息劵总张数
	    * @param userflag 1-使用
	    * @return
	    */
	   public Integer getIncreaseCardCount(Integer userflag,InterestIncreaseCard interestincreasecard,Integer timeState){
		   String sql="select count(r.id)  from interestincreasecard r,userbasicsinfo u,userrelationinfo ur where r.userId=u.id and r.userId=ur.id ";
		   if(userflag==1){
			   sql+=" and r.useFlag=1 ";
		   }
		   if(timeState==1){
			   sql+=returninterestincreaseSql(interestincreasecard);
		   }else{
			   sql+=connectionTimeSql(interestincreasecard.getReceiveTime(),interestincreasecard.getConsumeTime());
		   }
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Integer.valueOf(obj.toString()) : 0;
	   }
	   
	   /***
	    * 使用加息劵总投资金额
	    * @return
	    */
	   public Double getIntSumTenderMoney(InterestIncreaseCard interestincreasecard,Integer timeState){
		   String sql="select sum(l.tenderMoney) from interestincreasecard r,userbasicsinfo u,userrelationinfo ur ,loanrecord l where r.userId=u.id and r.userId=ur.id and r.loanrecordId=l.id  and l.isSucceed=1 and r.useFlag=1  ";
		   if(timeState==1){
			   sql+=returninterestincreaseSql(interestincreasecard);
		   }else{
			   sql+=connectionTimeSql(interestincreasecard.getReceiveTime(),interestincreasecard.getConsumeTime());
		   }
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	   }
	   
	   
	   /***
	    * 该时间段内的所有投资金额
	    * @return
	    */
	   public Double getSumTenderMoney(InterestIncreaseCard interestincreasecard){
		   String sql="select sum(l.tenderMoney) from loanrecord l, userbasicsinfo u,userrelationinfo ur where l.userbasicinfo_id=u.id and l.userbasicinfo_id=ur.id and l.isSucceed=1  ";
		   sql+=connectionLoanSql(interestincreasecard.getReceiveTime(),interestincreasecard.getConsumeTime());
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	   }
	
	   /***
	    * 该时间段的认购人数
	    * @return
	    */
	   public Integer getDistinctLoanrecordCount(InterestIncreaseCard interestincreasecard){
		   String sql="select count(distinct(l.userbasicinfo_id))  from loanrecord l, userbasicsinfo u,userrelationinfo ur where l.userbasicinfo_id=u.id and l.userbasicinfo_id=ur.id  and l.isSucceed=1 ";
		   sql+=connectionLoanSql(interestincreasecard.getReceiveTime(),interestincreasecard.getConsumeTime());
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Integer.valueOf(obj.toString()) : 0;
	   }
	   /***
	    * 该时间段内所有的订单数
	    * @return
	    */
	   public Integer getLoanrecordIdCount(InterestIncreaseCard interestincreasecard){
		   String sql="select count(l.id)  from loanrecord l, userbasicsinfo u,userrelationinfo ur where l.userbasicinfo_id=u.id and l.userbasicinfo_id=ur.id and l.isSucceed=1  ";
		   sql+=connectionLoanSql(interestincreasecard.getReceiveTime(),interestincreasecard.getConsumeTime());
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
