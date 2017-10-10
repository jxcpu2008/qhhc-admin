package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.Arith;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class QuickQueryService {
	
	@Resource
	private HibernateSupport dao;
	
	   /***
	    * 今日注册
	    * 显示10条数据
	    * @return
	    */
	   @SuppressWarnings("rawtypes")
        public List getTopTenUserInfo(){
		   String sql="select u.userName,u.`name`,o.phone,u.createTime,u.hasIpsAccount,u.id from userbasicsinfo u LEFT JOIN userrelationinfo o ON u.id =o.id order by u.createTime DESC LIMIT 0,10";
		   List datalist=dao.findBySql(sql);
           return datalist;
        }
	   
	   /***
	    * 今日充值
	    * 显示10条数据
	    * @return
	    */
	   @SuppressWarnings("rawtypes")
	   public  List getTopTenRecharge(){
		   String sql="select u.userName,u.`name`,r.rechargeAmount,r.time,r.succ_time, r.`status`,r.user_id  from recharge  r LEFT JOIN userbasicsinfo u ON r.user_id=u.id order by r.time desc LIMIT 0,10";
		   List datalist=dao.findBySql(sql);
           return datalist;
	   }
	   
	   /***
	    * 今日认购
	    * 显示10条数据
	    * @return
	    */
	   @SuppressWarnings("rawtypes")
	   public List getTopTenLoanRecord(){
		   String sql="select u.userName,u.`name`,l.tenderMoney,(select s.`name` from loansign s where s.id=l.loanSign_id),l.subType,l.tenderTime,l.isSucceed,l.userbasicinfo_id from loanrecord l  LEFT JOIN userbasicsinfo u  on l.userbasicinfo_id=u.id order by l.tenderTime DESC LIMIT 0,10";
		   List datalist=dao.findBySql(sql);
           return datalist;
	   }
	   
	   /***
	    * 今日提现
	    * 显示10条数据
	    * @return
	    */
	   @SuppressWarnings("rawtypes")
	   public List getTopTenWithdraw(){
		   String sql="select u.userName,u.`name`,w.amount,w.time,w.applytime,w.state,w.user_id from withdraw w LEFT JOIN userbasicsinfo u on w.user_id=u.id order by w.time desc LIMIT 0,10";
		   List datalist=dao.findBySql(sql);
           return datalist;
	   }
	   
	   /***
	    * 累计认购金额
	    * @return
	    */
	   public Double getLoanRecordSum(Long userId){
		   String sql="select SUM(tenderMoney) from loanrecord where userbasicinfo_id="+userId+"  and isSucceed=1 ";
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	   }
	   
	   /***
	    * 累积充值
	    * @param userId
	    * @return
	    */
	   public Double getRechargeSum(Long userId){
		   String sql="select SUM(income) from accountinfo where accounttype_id=6 and userbasic_id="+userId;
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	   }
	   
	   /**
	    * 累积提现
	    * @param userId
	    * @return
	    */
	   public Double getWithdrawSum(Long userId){
		   String sql="select SUM(expenditure) from accountinfo where accounttype_id=7 and userbasic_id="+userId;
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	   }
	   
	   /***
	    * 累积推荐人数
	    * @param userId
	    * @return
	    */
	   public Integer getGeneralizeCount(Long userId){
		   String sql="select COUNT(id) from generalize where genuid="+userId;
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Integer.valueOf(obj.toString()) : 0;
	   }
	   
	   /***
	    * 累积推荐佣金
	    * @param userId
	    * @return
	    */
	   public Double getGeneralizeMoneySum(Long userId){
		   String sql="select SUM(IFNULL(bonuses,0)) from generalizemoney where refer_userid="+userId;
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	   }
	   
		/**
		 * 累积待回款本金
		 * @param id
		 * @return
		 */
		public Double getTenderMoneyCollection(Long userId) {
			String sql = "SELECT sum(tenderMoney) from loansign ls LEFT JOIN loanrecord lr ON ls.id=lr.loanSign_id "
					+ "where lr.isSucceed=1 and lr.userbasicinfo_id="
					+ userId
					+ " and ls.`status` not in(0,-1,8,9)";
			Object obj = dao.findObjectBySql(sql);
			return  obj != null ? Double.valueOf(obj.toString()) : 0.00;
		}
		
		/***
		 * 累积已收回款本金
		 * @param userId
		 * @return
		 */
		public Double getTenderMoneyReceived(Long userId){
			String sql = "SELECT sum(tenderMoney) from loansign ls LEFT JOIN loanrecord lr ON ls.id=lr.loanSign_id "
					+ "where lr.isSucceed=1 and lr.userbasicinfo_id="
					+ userId
					+ " and ls.`status` =8 ";
			Object obj = dao.findObjectBySql(sql);
			return  obj != null ? Double.valueOf(obj.toString()) : 0.00;
		}

		/**
		 * 累积待收佣金
		 * @param id
		 * @return
		 */
		public Double getBonusCollection(Long userId) {
			String sql = "select sum(IFNULL(bonuses,0)) from generalizemoney  where pay_state!=1  and refer_userid= "
					+ userId;
			Object obj = dao.findObjectBySql(sql);
			return obj != null ? Double.valueOf(obj.toString()) : 0;
		}
		
		/**
		 * 累积已收佣金
		 * @param id
		 * @return
		 */
		public Double getBonusReceived(Long userId) {
			String sql = "select sum(IFNULL(bonuses,0)) from generalizemoney  where pay_state=1  and refer_userid= "
					+ userId;
			Object obj = dao.findObjectBySql(sql);
			return obj != null ? Double.valueOf(obj.toString()) : 0.00;
		}
		
		/**
		 * 累积待收收益
		 * @param id
		 * @return
		 */
		public Double getDueRepayCollection(Long userId) {
			String sql = "SELECT rpp.preRepayMoney,rpp.middlePreRepayMoney,rpp.afterPreRepayMoney "
					+ "from "
					+ "repaymentrecord rp JOIN repaymentrecordparticulars rpp on rp.id=rpp.repaymentrecordId "
					+ "where rp.repayState in (1,3) AND rpp.userId=?";
			double interest = 0.0;
			List list = dao.findBySql(sql, userId);
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[]) list.get(i);
				for (int j = 0; j < 3; j++) {
					if (obj[j] != null) {
						interest += Double.parseDouble(obj[j].toString());
					}
				}

			}
			return interest;
		}
		
		/**
		 * 累积已收收益
		 * @param id
		 * @return
		 */
		public Double getHostIncomeReceived(Long userId) {
			String sql = "select sum((select SUM(ifnull(rmp.realMoney,0)-IFNULL(rmp.money,0))+SUM(IFNULL(rmp.middleRealMoney,0)-IFNULL(rmp.middleMoney,0))+SUM(IFNULL(rmp.afterRealMoney,0)-IFNULL(rmp.afterMoney,0))"
					+ " from repaymentrecord repm,repaymentrecordparticulars rmp where repm.id=rmp.repaymentrecordId and rmp.loanrecordId=lr.id and rmp.repState=1)) "
					+ "from loanrecord lr ,loansign ls where ls.id=lr.loanSign_id  and lr.userbasicinfo_id="
					+ userId + " and lr.isSucceed=1 and ls.`status`in(6,7,8)";
			Object obj = dao.findObjectBySql(sql);
			return obj != null ? Double.valueOf(obj.toString()) : 0.00;
		}
		
		/***
		 * 用户基本信息
		 * @return
		 */
	   @SuppressWarnings("rawtypes")
	   public List getUserDepartment(Long userId){
		   String sql="select u.userName,u.`name`, i.phone,u.user_type,u.createTime,u.hasIpsAccount,(SELECT ug.name from generalize g,userbasicsinfo ug where g.uid=u.id and ug.id=g.genuid and g.state in (1,2) ),(SELECT ui.department from generalize g,userbasicsinfo ui where g.uid=u.id and ui.id=g.genuid and g.state in (1,2)),u.id,u.isAuthIps from userbasicsinfo u LEFT JOIN userrelationinfo i on u.id=i.id where u.id="+userId;
		   List datalist=dao.findBySql(sql);
           return datalist;
	   }
	   
	   /***
	    * 用户的认购流水
	    * @param userId
	    * @return
	    */
	   public List getUserLoanRecord(Long userId){
		   String sql="select l.tenderTime,s.`name`,s.refunway,s.remonth,l.subType,s.prio_rate+IFNULL(s.prio_aword_rate,0),l.tenderMoney,s.credit_time,(select SUM(IFNULL(r.preRepayMoney,0)+IFNULL(r.middlePreRepayMoney,0)+IFNULL(r.afterPreRepayMoney,0)) from repaymentrecordparticulars r where r.loanrecordId=l.id) ,g.bonuses,s.`status` from loanrecord l LEFT JOIN loansign s on l.loanSign_id=s.id  LEFT JOIN generalizemoney g on l.id=g.loanrecord_id  and g.refered_userid=g.refer_userid where  l.isSucceed=1 and l.userbasicinfo_id="+userId+"  ORDER BY l.tenderTime desc LIMIT 0,15";
		   List datalist=dao.findBySql(sql);
           return datalist;
	   }
	   
	   /***
	    * 用户的资金流水记录
	    * @param userId
	    * @return
	    */
	   public List getAccountInfo(Long userId){
		   String sql="select time,IFNULL(expenditure,0),IFNULL(income,0),explan,money,accounttype_id from accountinfo where userbasic_id="+userId+"  ORDER BY time DESC  LIMIT 0,15";
		   List datalist=dao.findBySql(sql);
           return datalist;
	   }
	   
	   /***
	    * 根据输入框查询数据
	    * @param name
	    * @return
	    */
	   public  List selUser(String name){
		   String username = "";
			try {
				username = java.net.URLDecoder.decode(
						name.trim(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String sql="select u.`name`,u.userName,us.phone,u.id from userbasicsinfo u LEFT JOIN userrelationinfo us on u.id=us.id where u.`name` like '%"+username+"%' or u.userName like '%"+username+"%'  or  us.phone like '%"+username+"%'LIMIT 0,10";
			List datalist=dao.findBySql(sql);
	         return datalist;
	   }
	   
	   
	   /***
	    * 查询所有
	    * @param userId
	    * @return
	    */
	   public List getUserLoanRecordList(PageModel page,Long userId,String beginDate, String endDate, Integer status){
		   String sql="select l.tenderTime,s.`name`,s.refunway,s.remonth,l.subType,s.prio_rate+IFNULL(s.prio_aword_rate,0),l.tenderMoney,s.credit_time,(select SUM(IFNULL(r.preRepayMoney,0)+IFNULL(r.middlePreRepayMoney,0)+IFNULL(r.afterPreRepayMoney,0)) from repaymentrecordparticulars r where r.loanrecordId=l.id) ,g.bonuses,s.`status` from loanrecord l LEFT JOIN loansign s on l.loanSign_id=s.id  LEFT JOIN generalizemoney g on l.id=g.loanrecord_id  and g.refered_userid=g.refer_userid where  l.isSucceed=1 and l.userbasicinfo_id="+userId
				   + connectionLoanSql(beginDate, endDate,  status)
				   +"   ORDER BY l.tenderTime desc LIMIT "
                   + page.firstResult() + "," + page.getNumPerPage();
		   List datalist=dao.findBySql(sql);
           return datalist;
	   }
	   
	    public Integer queryCount(String beginDate, String endDate, Integer status,Long userId) {
	        String sql = "select count(l.id) from loanrecord l LEFT JOIN loansign s on l.loanSign_id=s.id  LEFT JOIN generalizemoney g on l.id=g.loanrecord_id  and g.refered_userid=g.refer_userid where  l.isSucceed=1 and l.userbasicinfo_id="+userId;
	                 sql+=connectionLoanSql(beginDate, endDate,  status);
	        Object obj = dao.findObjectBySql(sql);
	        return Integer.parseInt(obj.toString());
	    }
	    
	    public String connectionLoanSql(String beginDate, String endDate, Integer status) {
	        String sql = "";
	        if (beginDate != null && !"".equals(beginDate.trim())) {
	            sql = sql + " AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('" + beginDate
	                    + "', '%Y-%m-%d %H:%i:%s') ";
	        }
	        if (endDate != null && !"".equals(endDate.trim())) {
	            sql = sql + " AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('" + endDate
	                    + "', '%Y-%m-%d %H:%i:%s') ";
	        }
			if (status != null) {
				if (status== 7) {
					sql = sql + " and  s.`status` in(7,8)";
				} else if (status == 10) {// 募集中
					sql = sql + " and  s.`status` in(1,2,3,4,5)";
				} else {
					sql = sql + " and  s.`status` ="+status;
				}
			}
	        return sql;
	    }
	    
	    
	    /***
	     * 查询所有流水记录
	     * @param userId
	     * @param beginDate
	     * @param endDate
	     * @param accountType
	     * @return
	     */
		   public List getAccountInfoList(PageModel page,Long userId,String beginDate, String endDate, Integer accountType){
			   String sql="select time,IFNULL(expenditure,0),IFNULL(income,0),explan,money,accounttype_id from accountinfo where userbasic_id="+userId
					   + connectionAccountSql(beginDate, endDate,  accountType)
					   +"  ORDER BY time DESC  LIMIT "
                       + page.firstResult() + "," + page.getNumPerPage();
			   List datalist=dao.findBySql(sql);
	           return datalist;
		   }
		   
		   /***
		    * 统计所有流水记录Count
		    * @param userId
		    * @param beginDate
		    * @param endDate
		    * @param accountType
		    * @return
		    */
		   public Integer getAccountInfoCount(Long userId,String beginDate, String endDate, Integer accountType){
			   String sql="select count(id) from accountinfo where userbasic_id="+userId
					   + connectionAccountSql(beginDate, endDate,  accountType);
			   Object obj = dao.findObjectBySql(sql);
		        return Integer.parseInt(obj.toString());
		   }
	    
	    
	    
	    public String connectionAccountSql(String beginDate, String endDate, Integer accountType) {
	        String sql = "";
	        if (beginDate != null && !"".equals(beginDate.trim())) {
	            sql = sql + " AND DATE_FORMAT(time, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('" + beginDate
	                    + "', '%Y-%m-%d %H:%i:%s') ";
	        }
	        if (endDate != null && !"".equals(endDate.trim())) {
	            sql = sql + " AND DATE_FORMAT(time, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('" + endDate
	                    + "', '%Y-%m-%d %H:%i:%s') ";
	        }
	        if (accountType!=null) {
	            if (accountType== 5) {
					sql = sql + " AND accounttype_id in (5,15)";
				} else if(accountType==18){
					sql = sql + " AND accounttype_id in (18,21)";
				} else {
					sql = sql + " AND  accounttype_id="+accountType;
				}
	        }else{
	        	sql = sql + " AND  accounttype_id  in (3,4,5,6,7,12,15,16,17,18,19,20,21)";
	        }
	        return sql;
	    }
	    
	    /***
	     * 金额合计
	     * @param userId
	     * @param beginDate
	     * @param endDate
	     * @param accountType
	     * @return
	     */
		public Double sumAccountInfo(Long userId,String beginDate, String endDate, Integer accountType) {
			String sql = "select * from accountinfo where userbasic_id="+userId;
			  sql=sql+ connectionAccountSql(beginDate, endDate,  accountType);
			double interest = 0.00;
			List<Accountinfo> list = dao.findBySql(sql, Accountinfo.class);
			for (int i = 0; i < list.size(); i++) {
				Accountinfo accountinfo=list.get(i);
				if(accountinfo.getAccounttype().getId()==4 || accountinfo.getAccounttype().getId()==5||accountinfo.getAccounttype().getId()==7||accountinfo.getAccounttype().getId()==15||accountinfo.getAccounttype().getId()==17||accountinfo.getAccounttype().getId()==19){
					interest=Arith.sub(interest,accountinfo.getExpenditure());
				}else{
					interest=Arith.add(interest,accountinfo.getIncome());
				}
			}
			return interest;
		}
	    
		/***
		 * 显示红包15条
		 * @param userId
		 * @return
		 */
		@SuppressWarnings("rawtypes")
		public List getRedenvelopedetail(Long userId){
			String sql="select money,beginTime,endTime,lowestUseMoney,useFlag,consumeTime from redenvelopedetail where userId="+userId+"  LIMIT 0,15";
			 List datalist=dao.findBySql(sql);
	         return datalist;
		}
		
		/***
		 * 统计多少条数
		 * @param beginDate
		 * @param endDate
		 * @param status
		 * @param userId
		 * @return
		 */
	    public Integer queryRedCount(String beginDate, String endDate, Integer status,Long userId) {
	        String sql = "select count(id) from redenvelopedetail where userId="+userId;
	                 sql+=connectionRedSql(beginDate, endDate,  status);
	        Object obj = dao.findObjectBySql(sql);
	        return Integer.parseInt(obj.toString());
	    }
	    
	    /***
	     * 20150817-lkl
	     * 获取红包总额度
	     * @param userId
	     * @return
	     */
	    public Double queryRedMoney(Long userId){
	    	String sql="select SUM(money) from redenvelopedetail where userId="+userId;
	    	Object obj = dao.findObjectBySql(sql);
			return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	    }
		
	    public String connectionRedSql(String beginDate, String endDate, Integer status) {
	        String sql = "";
	        if (beginDate != null && !"".equals(beginDate.trim())) {
	            sql = sql + " AND DATE_FORMAT(consumeTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('" + beginDate
	                    + "', '%Y-%m-%d %H:%i:%s') ";
	        }
	        if (endDate != null && !"".equals(endDate.trim())) {
	            sql = sql + " AND DATE_FORMAT(consumeTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('" + endDate
	                    + "', '%Y-%m-%d %H:%i:%s') ";
	        }
			if (status != null) {
					sql = sql + " and  useFlag ="+status;
			}
	        return sql;
	    }
	    
	    /***
	     * 查询所有红包记录
	     * @param page
	     * @param userId
	     * @param beginDate
	     * @param endDate
	     * @param status
	     * @return
	     */
	    public List getRedenvelopedetailList(PageModel page,Long userId,String beginDate, String endDate, Integer status){
			   String sql="select money,lowestUseMoney,loanrecord_id,receiveTime,consumeTime,beginTime,endTime,useFlag,sourceType from redenvelopedetail where userId="+userId
					   + connectionRedSql(beginDate, endDate,  status)
					   +" LIMIT "
	                   + page.firstResult() + "," + page.getNumPerPage();
			   List datalist=dao.findBySql(sql);
	           return datalist;
		   }
	    
	   /***
	    * 加息卷查询
	    * @param userId
	    * @return
	    */
	   public List getInterestincreasecard(Long userId){
		   String sql="select interestRate,beginTime,endTime,lowestUseMoney,useFlag,consumeTime from interestincreasecard  where userId ="+userId+"  ORDER BY consumeTime DESC  LIMIT 0,15";
		   List datalist=dao.findBySql(sql);
           return datalist;
	   }
	   
	   /***
	    * 查询渠道名称
	    * @param name
	    * @return
	    */
	   public String getName(String name){
		   String sql="select `name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and regStatus=1 and regUserName='"+name+"'  LIMIT 0,1 ";
		   Object obj = dao.findObjectBySql(sql);
		   return obj != null ? obj.toString():"" ;
	   }
	   
	   
	    public String connectionCardSql(String beginDate, String endDate, Integer status) {
	        String sql = "";
	        if (beginDate != null && !"".equals(beginDate.trim())) {
	            sql = sql + " AND DATE_FORMAT(consumeTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('" + beginDate
	                    + "', '%Y-%m-%d %H:%i:%s') ";
	        }
	        if (endDate != null && !"".equals(endDate.trim())) {
	            sql = sql + " AND DATE_FORMAT(consumeTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('" + endDate
	                    + "', '%Y-%m-%d %H:%i:%s') ";
	        }
			if (status != null) {
					sql = sql + " and  useFlag ="+status;
			}
	        return sql;
	    }
	   
	    /***
	     * 加息卷查询
	     * @param page
	     * @param userId
	     * @param beginDate
	     * @param endDate
	     * @param status
	     * @return
	     */
	    public List getCardList(PageModel page,Long userId,String beginDate, String endDate, Integer status){
			   String sql="select interestRate,beginTime,endTime,lowestUseMoney,receiveTime,consumeTime,useFlag,sourceType from interestincreasecard where userId="+userId
					   + connectionCardSql(beginDate, endDate,  status)
					   +" LIMIT "
	                   + page.firstResult() + "," + page.getNumPerPage();
			   List datalist=dao.findBySql(sql);
	           return datalist;
		   }
	    
	    /***
	     * 10150817-lkl
	     * 统计加息卷个数
	     * @return
	     */
	    public Integer queryInterestincreasecardCount(String beginDate, String endDate, Integer status,Long userId) {
	        String sql = "select count(id) from interestincreasecard where userId="+userId;
	                  sql+= connectionCardSql(beginDate, endDate,  status);
	        Object obj = dao.findObjectBySql(sql);
	        return Integer.parseInt(obj.toString());
	    }
	   
}
