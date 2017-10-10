package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;


@Service
public class ActivityMonkeyQueryService {
	
	@Resource
	private HibernateSupport dao;
	
	@SuppressWarnings("rawtypes")
	public List activityMonkeyQueryPage(PageModel page, ActivityMonkey activityMonkey) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("SELECT count(1)  from activity_monkey  a ,userbasicsinfo u where a.userId=u.id   AND a.type!=9");
		StringBuffer sqlbuffer = new StringBuffer("select a.id,u.`name`,a.mobilePhone,a.money,a.type,a.rewardMoney,a.loanName,a.createtime,a.status,a.examineStatus from activity_monkey a ,userbasicsinfo u where a.userId=u.id  AND a.type!=9");
		if (activityMonkey.getMobilePhone() != null && activityMonkey.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(activityMonkey.getMobilePhone(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
			countsql.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
		}
		
		if (activityMonkey.getLoanName() != null && activityMonkey.getLoanName() != "") {
			String loanName = "";
			try {
				loanName = java.net.URLDecoder.decode(activityMonkey.getLoanName(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and a.loanName like '%").append(loanName).append("%' ");
			countsql.append(" and a.loanName like '%").append(loanName).append("%' ");

		}
		if (activityMonkey.getType()!=null&&activityMonkey.getType()!=0) {
			 if(activityMonkey.getType()==1){
				   sqlbuffer.append(" AND a.type in (1,2) ");
					countsql.append(" AND a.type in (1,2) ");
			 }else if(activityMonkey.getType()==2){
				  sqlbuffer.append(" AND a.type in (3,4,5) ");
				  countsql.append(" AND a.type in (3,4,5) ");
			 }else{
				 sqlbuffer.append(" AND a.type in (6,7,8)");
				  countsql.append(" AND a.type in (6,7,8)");
			 }
		 }else{
			 sqlbuffer.append(" AND a.type in (1,2,3,4,5,6,7,8) ");
			 countsql.append(" AND a.type in (1,2,3,4,5,6,7,8) ");
		 }
		sqlbuffer.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"a.createTime"));
		countsql.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"a.createTime"));
	
		sqlbuffer.append(" ORDER BY a.createtime desc");

		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);

		return datalist;
	}
	
	@SuppressWarnings("rawtypes")
	public List queryactivityMonkeyList(ActivityMonkey activityMonkey) {
		List list = new ArrayList();
		StringBuffer sqlbuffer = new StringBuffer("select a.id,u.`name`,a.mobilePhone,a.money,a.type,a.rewardMoney,a.loanName,a.createtime,a.examineStatus,a.status from activity_monkey a ,userbasicsinfo u where a.userId=u.id AND a.type !=9 ");
		if (activityMonkey.getMobilePhone() != null && activityMonkey.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(activityMonkey.getMobilePhone(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
		}
		
		if (activityMonkey.getLoanName() != null && activityMonkey.getLoanName() != "") {
			String loanName = "";
			try {
				loanName = java.net.URLDecoder.decode(activityMonkey.getLoanName(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and a.loanName like '%").append(loanName).append("%' ");

		}
		if (activityMonkey.getType()!=null&&activityMonkey.getType()!=0) {
			 if(activityMonkey.getType()==1){
				   sqlbuffer.append(" AND a.type in (1,2) ");
			 }else if(activityMonkey.getType()==2){
				  sqlbuffer.append(" AND a.type in (3,4,5) ");
			 }else{
				 sqlbuffer.append(" AND a.type in (6,7,8)");
			 }
		 }else{
			 sqlbuffer.append(" AND a.type in (1,2,3,4,5,6,7,8) ");
		 }
		sqlbuffer.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"a.createTime"));
		list = dao.findBySql(sqlbuffer.toString());
		return list;
	}
	
	public List activityMonkeyInitPage(PageModel page, ActivityMonkey activityMonkey) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("SELECT count(1)  from activity_monkey  a ,userbasicsinfo u where a.userId=u.id  and  a.examineStatus=0 ");
		StringBuffer sqlbuffer = new StringBuffer("select a.id,u.`name`,a.mobilePhone,a.money,a.type,a.rewardMoney,a.loanName,a.createtime,a.examineStatus,(select c.realname from adminuser c where c.id=a.examineAdminId) as realname,  ");
		                   sqlbuffer.append("a.examineTime  from activity_monkey a ,userbasicsinfo u where a.userId=u.id and a.examineStatus=0  ");
		if (activityMonkey.getMobilePhone() != null && activityMonkey.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(activityMonkey.getMobilePhone(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
			countsql.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
		}
		
		if (activityMonkey.getLoanName() != null && activityMonkey.getLoanName() != "") {
			String loanName = "";
			try {
				loanName = java.net.URLDecoder.decode(activityMonkey.getLoanName(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and a.loanName like '%").append(loanName).append("%' ");
			countsql.append(" and a.loanName like '%").append(loanName).append("%' ");

		}
		if (activityMonkey.getType()!=null&&activityMonkey.getType()!=0) {
			 if(activityMonkey.getType()==1){
				   sqlbuffer.append(" AND a.type in (1,2) ");
					countsql.append(" AND a.type in (1,2) ");
			 }else if(activityMonkey.getType()==2){
				  sqlbuffer.append(" AND a.type in (3,4,5) ");
				  countsql.append(" AND a.type in (3,4,5) ");
			 }else if(activityMonkey.getType()==3){
				  sqlbuffer.append(" AND a.type =9 ");
				  countsql.append(" AND a.type = 9 ");
			 }else if(activityMonkey.getType()==4){
				  sqlbuffer.append(" AND a.type =11 ");
				  countsql.append(" AND a.type =11 ");
			 }else if(activityMonkey.getType()==5){
				  sqlbuffer.append(" AND a.type in (12,13) ");
				  countsql.append(" AND a.type in (12,13) ");
			 } else if (activityMonkey.getType() == 6) {
				 sqlbuffer.append(" AND a.type in (14, 15) ");
				 countsql.append(" AND a.type in (14, 15) ");
			 }
		 }else{
			 sqlbuffer.append(" AND a.type in (1,2,3,4,5,9,11,12,13, 14, 15) ");
			 countsql.append(" AND a.type in (1,2,3,4,5,9,11,12,13, 14, 15) ");
		 }
		
		//获奖时间
		sqlbuffer.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"a.createtime"));
		countsql.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"a.createtime"));

		sqlbuffer.append(" ORDER BY a.createtime desc");
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);

		return datalist;
	}
	
	public String connectionSql(String beginDate, String endDate,String value) {
		String sql = "";
		if (beginDate != null && !"".equals(beginDate.trim())) {
			sql = sql
					+ " AND DATE_FORMAT("+value+", '%Y-%m-%d')>=DATE_FORMAT('"
					+ beginDate + "', '%Y-%m-%d') ";
		}
		if (endDate != null && !"".equals(endDate.trim())) {
			sql = sql
					+ " AND DATE_FORMAT("+value+", '%Y-%m-%d')<=DATE_FORMAT('"
					+ endDate + "', '%Y-%m-%d') ";
		}
		return sql;
	}
	
	/***
	 * 现金奖励数据申请审批
	 * @param id
	 * @param request
	 * @return
	 */
	public String activityMonkeyExamine(String id, Long adminId) {
		if (StringUtil.isNotBlank(id)) {
			 StringBuffer sql = new StringBuffer("UPDATE activity_monkey ");
			 sql.append("SET activity_monkey.examineStatus=1  ") ;
			 sql.append(", activity_monkey.examineTime='");
			 sql.append(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
			 sql.append("', activity_monkey.examineAdminId=");
			 sql.append(adminId);
			 sql.append("  WHERE activity_monkey.id in (");
			 sql.append(id);
			 sql.append(")");
			// 批量修改
			if (dao.executeSql(sql.toString()) > 0) {
				return "1";
			}
		}
		return "2";
	}
	
	/***
	 * 获取现金奖励活动数据
	 * @param id
	 * @return
	 */
	public ActivityMonkey getActivityMonkeyById(String id) {
		try {
			String sql = "select * from activity_monkey where id=?";
			ActivityMonkey activityMonkey = dao.findObjectBySql(sql, ActivityMonkey.class, id);
			return activityMonkey;
		} catch (DataAccessException e) {
			return null;
		}
	}
	
	
	public List activityMonkeyGrantInitPage(PageModel page, ActivityMonkey activityMonkey) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("SELECT count(1)  from activity_monkey  a  ,userbasicsinfo u where a.userId=u.id  and  a.examineStatus=1 ");
		StringBuffer sqlbuffer = new StringBuffer("select a.id,u.`name`,a.mobilePhone,a.money,a.type,a.rewardMoney,a.loanName,a.status,(select c.realname from adminuser c where c.id=a.grantAdminId) as realname,  ");
		                   sqlbuffer.append("a.grantTime,a.failreason  from activity_monkey a ,userbasicsinfo u where a.userId=u.id and a.examineStatus=1  ");
		if (activityMonkey.getMobilePhone() != null && activityMonkey.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(activityMonkey.getMobilePhone(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
			countsql.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
		}
		
		if (activityMonkey.getLoanName() != null && activityMonkey.getLoanName() != "") {
			String loanName = "";
			try {
				loanName = java.net.URLDecoder.decode(activityMonkey.getLoanName(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and a.loanName like '%").append(loanName).append("%' ");
			countsql.append(" and a.loanName like '%").append(loanName).append("%' ");

		}
		if (activityMonkey.getType()!=null&& activityMonkey.getType()!=0) {
			 if(activityMonkey.getType()==1){
				   sqlbuffer.append(" AND a.type in (1,2) ");
					countsql.append(" AND a.type in (1,2) ");
			 }else if(activityMonkey.getType()==2){
				  sqlbuffer.append(" AND a.type in (3,4,5) ");
				  countsql.append(" AND a.type in (3,4,5) ");
			 }else if(activityMonkey.getType()==3){
				  sqlbuffer.append(" AND a.type =9 ");
				  countsql.append(" AND a.type =9 ");
			 }else if(activityMonkey.getType()==4){
				 sqlbuffer.append(" AND a.type =10 ");
				  countsql.append(" AND a.type =10 ");
			 }else if(activityMonkey.getType()==5){
				  sqlbuffer.append(" AND a.type =11 ");
				  countsql.append(" AND a.type =11 ");
			 }else if(activityMonkey.getType()==6){
				  sqlbuffer.append(" AND a.type in (12,13) ");
				  countsql.append(" AND a.type in (12,13) ");
			 } else if (activityMonkey.getType() == 7) {
				 sqlbuffer.append(" AND a.type in (14, 15) ");
				 countsql.append(" AND a.type in (14, 15) ");
			 }
		 }else{
			 sqlbuffer.append(" AND a.type in (1,2,3,4,5,9,10,11,12,13, 14, 15) ");
			 countsql.append(" AND a.type in (1,2,3,4,5,9,10,11,12,13, 14, 15) ");
		 }
		
		if (activityMonkey.getStatus()!=null&&activityMonkey.getStatus()!=0) {
			 if(activityMonkey.getStatus()==1){
				   sqlbuffer.append(" AND a.status=0 ");
					countsql.append(" AND a.status=0 ");
			 }else if(activityMonkey.getStatus()==2){
				  sqlbuffer.append(" AND a.status=1");
				  countsql.append(" AND a.status=1");
			 }else if(activityMonkey.getStatus()==3){
				 sqlbuffer.append(" AND a.status=2");
				  countsql.append(" AND a.status=2");
			 }else if(activityMonkey.getStatus()==4){
				 sqlbuffer.append(" AND a.status=-1");
				  countsql.append(" AND a.status=-1");
			 }
		 }
		
		//发放时间
		sqlbuffer.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"a.grantTime"));
		countsql.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"a.grantTime"));
		
		sqlbuffer.append(" ORDER BY a.examineTime desc");
		
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);

		return datalist;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List queryactivityMonkeyGanteList(ActivityMonkey activityMonkey) {
		List list = new ArrayList();
		StringBuffer sqlbuffer = new StringBuffer("select a.id,u.`name`,a.mobilePhone,a.money,a.type,a.rewardMoney,a.loanName,a.status,(select c.realname from adminuser c where c.id=a.grantAdminId) as realname,  ");
        sqlbuffer.append("a.grantTime,a.failreason  from activity_monkey a ,userbasicsinfo u where a.userId=u.id and a.examineStatus=1  ");
		if (activityMonkey.getMobilePhone() != null && activityMonkey.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(activityMonkey.getMobilePhone(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
		}
		
		if (activityMonkey.getLoanName() != null && activityMonkey.getLoanName() != "") {
			String loanName = "";
			try {
				loanName = java.net.URLDecoder.decode(activityMonkey.getLoanName(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and a.loanName like '%").append(loanName).append("%' ");

		}
		if (activityMonkey.getType()!=null&& activityMonkey.getType()!=0) {
			 if(activityMonkey.getType()==1){
				   sqlbuffer.append(" AND a.type in (1,2) ");
			 }else if(activityMonkey.getType()==2){
				  sqlbuffer.append(" AND a.type in (3,4,5) ");
			 }else if(activityMonkey.getType()==3){
				  sqlbuffer.append(" AND a.type =9 ");
			 }else if(activityMonkey.getType()==4){
				 sqlbuffer.append(" AND a.type =10 ");
			 }else if(activityMonkey.getType()==5){
				  sqlbuffer.append(" AND a.type =11 ");
			 }else if(activityMonkey.getType()==6){
				  sqlbuffer.append(" AND a.type in (12,13) ");
			 } else if (activityMonkey.getType() == 7) {
				 sqlbuffer.append(" AND a.type in (14, 15) ");
			 }
		 }else{
			 sqlbuffer.append(" AND a.type in (1,2,3,4,5,9,10,11,12,13, 14, 15) ");
		 }
		if (activityMonkey.getStatus()!=null&&activityMonkey.getStatus()!=0) {
			 if(activityMonkey.getStatus()==1){
				   sqlbuffer.append(" AND a.status=0 ");
			 }else if(activityMonkey.getStatus()==2){
				  sqlbuffer.append(" AND a.status=1");
			 }else if(activityMonkey.getStatus()==3){
				 sqlbuffer.append(" AND a.status=2");
			 }else if(activityMonkey.getStatus()==4){
				 sqlbuffer.append(" AND a.status=-1");
			 }
		 }
		//发放时间
		sqlbuffer.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"a.grantTime"));
		list = dao.findBySql(sqlbuffer.toString());
		return list;
	}
	
	public  List<ActivityMonkey> getActivityMonkeyList(String id ){
		String sql="select * from activity_monkey where `status` in (0,-1) and examineStatus=1 and id=?";
		List<ActivityMonkey> activityMonkeyList=dao.findBySql(sql, ActivityMonkey.class, id);
		return  activityMonkeyList;
	}
	
	/***
	 * 更新金猴统计奖励数据
	 * @param activityMonkey
	 */
    public void updateActivityMonkey(ActivityMonkey activityMonkey){
    	dao.update(activityMonkey);
    }
    
    /** 更新猴声大噪缓标存信息 */
	public int getMonkeyLoanId(String LoansignId) {
		String sql = "select COUNT(id) FROM activity_monkey where loanId = ? and type in (1,2) and `status` != 0";
		return dao.queryNumberSql(sql, LoansignId).intValue();
	}
	
	/** 更新猴声大噪缓标存信息 */
	public int delActivityMonkey(String LoansignId) {
		String sql = "DELETE from activity_monkey where loanId = ? and type in (1,2) and `status` = 0";
		return dao.executeSql(sql, LoansignId);
	}
	
	/** 更新猴声大噪缓标存信息 */
	public void addActivityMonkey(List<ActivityMonkey> activityMonkeyList){
		dao.saveOrUpdateAll(activityMonkeyList);
	}
	
	/** 更新猴声大噪缓标存信息 */
	public void addActivityMonkey(ActivityMonkey activityMonkey){
		dao.save(activityMonkey);
	}
	
	@SuppressWarnings("rawtypes")
	public List activityMonkeyExamine(PageModel page, ActivityMonkey activityMonkey) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("SELECT count(1)  from activity_monkey a LEFT JOIN  userbasicsinfo u on a.userId=u.id where type=9 ");
		StringBuffer sqlbuffer = new StringBuffer("select a.id,u.`name`,a.mobilePhone,u.isAuthIps,a.money,a.rewardMoney,u.createTime,(select `name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and regStatus=1 and ");
		                   sqlbuffer.append(" regUserName=u.userName)as channelName,a.examineStatus,a.status  from activity_monkey a , userbasicsinfo u where a.userId=u.id and type=9 ");
		if (activityMonkey.getMobilePhone() != null && activityMonkey.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(activityMonkey.getMobilePhone(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
			countsql.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
		}

		if (activityMonkey.getType()!=null&&activityMonkey.getType()!=0) {
			 if(activityMonkey.getType()==1){
				   sqlbuffer.append(" AND a.rewardMoney=188 ");
					countsql.append(" AND a.rewardMoney=188 ");
			 }else if(activityMonkey.getType()==2){
				  sqlbuffer.append("  AND a.rewardMoney=388 ");
				  countsql.append("  AND a.rewardMoney=388 ");
			 }else if(activityMonkey.getType()==3){
				  sqlbuffer.append("  AND a.rewardMoney=888 ");
				  countsql.append("  AND a.rewardMoney=888 ");
			 }else{
				 sqlbuffer.append(" AND a.rewardMoney=1888 ");
				  countsql.append(" AND a.rewardMoney=1888 ");
			 }
		 }
		sqlbuffer.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"u.createTime"));
		countsql.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"u.createTime"));
		sqlbuffer.append(" ORDER BY a.createtime desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}
	
	@SuppressWarnings("rawtypes")
	public List queryActivityMExamineList(ActivityMonkey activityMonkey) {
		List list = new ArrayList();
		StringBuffer sqlbuffer = new StringBuffer("select a.id,u.`name`,a.mobilePhone,u.isAuthIps,a.money,a.rewardMoney,u.createTime,(select `name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and regStatus=1 and ");
                            sqlbuffer.append(" regUserName=u.userName)as channelName,a.examineStatus,a.status  from activity_monkey a ,  userbasicsinfo u where a.userId=u.id and type=9 ");
		if (activityMonkey.getMobilePhone() != null && activityMonkey.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(activityMonkey.getMobilePhone(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
		}

		if (activityMonkey.getType()!=null&&activityMonkey.getType()!=0) {
			 if(activityMonkey.getType()==1){
				   sqlbuffer.append(" AND a.rewardMoney=188 ");
			 }else if(activityMonkey.getType()==2){
				  sqlbuffer.append("  AND a.rewardMoney=388 ");
			 }else if(activityMonkey.getType()==3){
				  sqlbuffer.append("  AND a.rewardMoney=888 ");
			 }else{
				 sqlbuffer.append(" AND a.rewardMoney=1888 ");
			 }
		 }
		sqlbuffer.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"u.createTime"));
		sqlbuffer.append(" ORDER BY a.createtime desc");
		list = dao.findBySql(sqlbuffer.toString());
		return list;
	}
	
	/***
	 * 批量20条审核
	 * @param request
	 * @return
	 */
	public   String updateBatchActivityMonkey(HttpServletRequest request){
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		String sqlList="select * from activity_monkey where  activity_monkey.examineStatus=0   AND  activity_monkey.type in (1,2,3,4,5,9,11,12,13,14,15)  ORDER BY  activity_monkey.createTime ASC  LIMIT 0,20 ";
		List<ActivityMonkey> activityMonkeyList=dao.findBySql(sqlList,ActivityMonkey.class);
		if(activityMonkeyList.size()>0){
			String batchId="";
			for (int i = 0; i < activityMonkeyList.size(); i++) {
				ActivityMonkey activityMonkey=activityMonkeyList.get(i);
				batchId+=activityMonkey.getId()+",";
			}
			StringBuffer sql = new StringBuffer("UPDATE activity_monkey SET activity_monkey.examineStatus=1  ") ;
			                     sql.append(" , activity_monkey.examineTime='").append(DateUtils.format("yyyy-MM-dd HH:mm:ss")+"'");
			                     sql.append(", activity_monkey.examineAdminId=").append(loginuser.getId());
			                     sql.append("  WHERE activity_monkey.id in (").append(batchId.substring(0, batchId.length() - 1)+" )");
			// 批量修改
			if (dao.executeSql(sql.toString()) > 0) {
				return "1";
			}
			return "2";
		}else{
			return "3";
		}
	}
	
	
	public  List<ActivityMonkey> getActivityMonkeyList( ){
		String sql="select * from activity_monkey where `status` in (0,-1) and examineStatus=1 order by  createTime desc  limit 0,20";
		List<ActivityMonkey> activityMonkeyList=dao.findBySql(sql, ActivityMonkey.class);
		return  activityMonkeyList;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List activityFinancialPage(PageModel page, ActivityMonkey activityMonkey) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("SELECT count(1)  from activity_financial a, userbasicsinfo u where  a.userId=u.id ");
		StringBuffer sqlbuffer = new StringBuffer("select a.id,a.userName,a.mobilePhone,a.money,u.createTime,(select `name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and regStatus=1 and regUserName=u.userName)as  ");
		                   sqlbuffer.append(" channelName,case when  a.money  >= 100000 and  a.money<200000 THEN 188 WHEN  a.money  >= 200000 and a.money<500000 THEN 388 WHEN  a.money  >= 500000 and a.money<1000000 THEN 888 WHEN  a.money>=1000000 ");
		                   	sqlbuffer.append(" THEN 1888 ELSE 0  END  as rewardMoney from activity_financial a , userbasicsinfo u where a.userId=u.id ");
		if (activityMonkey.getMobilePhone() != null && activityMonkey.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(activityMonkey.getMobilePhone(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
			countsql.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
		}

		if (activityMonkey.getType()!=null&&activityMonkey.getType()!=0) {
			 if(activityMonkey.getType()==1){
				   sqlbuffer.append(" AND a.money>=100000  and a.money <200000 ");
					countsql.append(" AND a.money>=100000  and a.money <200000 ");
			 }else if(activityMonkey.getType()==2){
				  sqlbuffer.append("  AND a.money>=200000  and a.money <500000 ");
				  countsql.append("  AND a.money>=200000  and a.money <500000 ");
			 }else if(activityMonkey.getType()==3){
				  sqlbuffer.append("  AND a.money>=500000  and a.money <1000000 ");
				  countsql.append("  AND a.money>=500000  and a.money <1000000 ");
			 }else{
				  sqlbuffer.append("  AND a.money>=1000000 ");
				  countsql.append("  AND a.money>=1000000 ");
			 }
		 }
		sqlbuffer.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"u.createTime"));
		countsql.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"u.createTime"));
		sqlbuffer.append(" ORDER BY u.createtime desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}
	
	@SuppressWarnings("rawtypes")
	public List queryActivityFinancialList(ActivityMonkey activityMonkey) {
			List list = new ArrayList();
			StringBuffer sqlbuffer = new StringBuffer("select a.id,a.userName,a.mobilePhone,a.money,u.createTime,(select `name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and regStatus=1 and regUserName=u.userName)as  ");
			sqlbuffer.append(" channelName,case when  a.money  >= 100000 and  a.money<200000 THEN 188 WHEN  a.money  >= 200000 and a.money<500000 THEN 388 WHEN  a.money  >= 500000 and a.money<1000000 THEN 888 WHEN  a.money>=1000000 ");
			sqlbuffer.append(" THEN 1888 ELSE 0 END  as rewardMoney from activity_financial a , userbasicsinfo u where a.userId=u.id ");
			if (activityMonkey.getMobilePhone() != null && activityMonkey.getMobilePhone() != "") {
			String name = "";
			try {
					name = java.net.URLDecoder.decode(activityMonkey.getMobilePhone(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
			}
			sqlbuffer.append(" and (u.`name` like '%").append(name).append("%'  or a.mobilePhone like '%").append(activityMonkey.getMobilePhone()).append("%')");
			}
			if (activityMonkey.getType()!=null&&activityMonkey.getType()!=0) {
				if(activityMonkey.getType()==1){
				sqlbuffer.append(" AND a.money>=100000  and a.money <200000 ");
				}else if(activityMonkey.getType()==2){
				sqlbuffer.append("  AND a.money>=200000  and a.money <500000 ");
				}else if(activityMonkey.getType()==3){
				sqlbuffer.append("  AND a.money>=500000  and a.money <1000000 ");
				}else{
				sqlbuffer.append("  AND a.money>=1000000 ");
				}
			}
			sqlbuffer.append(connectionSql(activityMonkey.getCreateTime(),activityMonkey.getExamineTime(),"u.createTime"));
			sqlbuffer.append(" ORDER BY u.createtime desc");
			list = dao.findBySql(sqlbuffer.toString());
			return list;
	}
	
	/***
	 * 
	 * @return
	 */
	public  List<ActivityMonkey> getBatchActivityMonkeyList(){
		String sql="select * from activity_monkey where `status` in (0,-1) and examineStatus=1 ORDER BY createTime ASC LIMIT 0,20 ";
		List<ActivityMonkey> activityMonkeyList=dao.findBySql(sql, ActivityMonkey.class);
		return  activityMonkeyList;
	}
	
	public List<ActivityMonkey> queryActivityMonkeyList(String recordIds) {
		String[] idArray = recordIds.split(",");
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for (int i = 0; i < idArray.length; i++) {
			sb.append(Integer.parseInt(idArray[i]));
			if (i < idArray.length -1) {
				sb.append(",");
			}
		}
		sb.append(")");
		String condition = sb.toString();
		String querySql = " select * from activity_monkey where `status` in (0, -1) and examineStatus = 1 and id in " + condition;
		return dao.findBySql(querySql, ActivityMonkey.class);
	}
	
	/***
	 * 计算总额
	 * @param begincreditTime
	 * @param endcreditTime
	 * @param sqlValue
	 * @return
	 */
	public Double getSumRewardMoney(String begincreditTime,String endcreditTime,Integer sqlValue){
		String sql="select  sum(rewardMoney)  from activity_monkey  where   examineStatus!=9  ";
		if (StringUtil.isNotBlank(begincreditTime)) {
			sql+=" and DATE_FORMAT(grantTime,'%y-%m-%d') >= DATE_FORMAT('"+begincreditTime+"','%y-%m-%d')";
		}
		if (StringUtil.isNotBlank(endcreditTime)) {
			sql+=" and DATE_FORMAT(grantTime,'%y-%m-%d') <= DATE_FORMAT('"+endcreditTime+"','%y-%m-%d')";
		}
		if(sqlValue==1){
			sql+=" and `status`=1";
		}else if(sqlValue==2){
			sql+=" and `status`!=1";
		}
		Object sumIssueLoan=dao.findObjectBySql(sql);
		return   sumIssueLoan != null ? Double.valueOf(sumIssueLoan.toString()) : 0.00;
	}
  
	
}
