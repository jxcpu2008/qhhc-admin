package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.controller.UserInfoController;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class LoansignManageService {
	
	private static final Logger LOGGER = Logger.getLogger(UserInfoController.class);
	
	@Resource
	private HibernateSupport dao;
	
	@SuppressWarnings("rawtypes")
	public List loanSignPage(PageModel page, Loansign loansign,String status,String beginpublishTime,String endpublishTime,String beginfullTime,String endfullTime,String begincreditTime,String endcreditTime) {
		List datalist = new ArrayList();
	
		 StringBuffer countsql = new StringBuffer(
	                "SELECT count(1)   ");    
	        countsql.append(" FROM loansign ls,userbasicsinfo u,loansignbasics lbs,paylog p   ");
	        countsql.append(" where u.id=ls.userbasicinfo_id and ls.id=lbs.id   and ls.state=2 and ls.isdet!=1 and ls.order_sn=p.order_sn");   
	        
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT\n" +
				"	ls.id,\n" +
				"	ls.prio_rate,\n" +
				"	ls.prio_aword_rate,\n" +
				"	ls.issueLoan,\n" +
				"	ls.priority,\n" +
				"	ls.middle,\n" +
				"	ls.`after`,\n" +
				"	ls.prio_rest_money,\n" +
				"	ls.mid_rest_money,\n" +
				"	ls.after_rest_money,\n" +
				"	ls.loanUnit,\n" +
				"	IFNULL(ls.`name`, ''),\n" +
				"	lbs.proindustry,\n" +
				"	ifnull(u.`name`, ''),\n" +
				"	lbs.projectState,\n" +
				"	lbs.haveOther,\n" +
				"	ls.publish_time,\n" +
				"	ls.appropriation,\n" +
				"	lbs.remark,\n" +
				"	ls.status,\n" +
				"	ls.recommend,\n" +
				"	ls.rest_money,\n" +
				"	ls.remonth,\n" +
				"	ls.type,\n" +
				"	p.pay_status,\n" +
				"	ls.fee,ls.companyFee,ls.mid_rate,ls.after_rate,ls.real_rate,ls.feeState,ls.feeMoney,IFNULL(ls.fee,0.00),ls.credit_time,IFNULL(ls.real_rate,0.00)-IFNULL(ls.companyFee,0.00),ls.redEnvelopeMoney,u.pMerBillNo \n" +
				"FROM\n" +
				"	loansign ls,\n" +
				"	userbasicsinfo u,\n" +
				"	loansignbasics lbs,\n" +
				"	paylog p\n" +
				"WHERE\n" +
				"	u.id = ls.userbasicinfo_id\n" +
				"AND lbs.id = ls.id\n" +
				"AND ls.state = 2\n" +
				"AND ls.isdet != 1" +
				" and ls.order_sn=p.order_sn"
				);

		
		if(loansign.getName()!=null&&loansign.getName()!=""){
			String loanname = "";
			try {
				loanname = java.net.URLDecoder.decode(loansign.getName(),
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" AND ls.name like '%").append(loanname).append("%'");
			countsql.append(" AND ls.name like '%").append(loanname).append("%'");
		}
		
		if(loansign.getUserbasicsinfo()!=null){
			if(loansign.getUserbasicsinfo().getName()!=""){
				String name = "";
				try {
					name = java.net.URLDecoder.decode(loansign.getUserbasicsinfo().getName(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" AND u.name like '%").append(name).append("%'");
				countsql.append(" AND u.name like '%").append(name).append("%'");
			}
		}
		
		if(loansign.getType()!=null&&loansign.getType()!=0){
			sqlbuffer.append(" AND ls.type="+loansign.getType());
			countsql.append(" AND ls.type="+loansign.getType());
		}
		//TODO 此处判断getIssueLoan()==2是什么意思
		if(loansign.getLoansignbasics()!=null){
			if(loansign.getIssueLoan()!=null&&loansign.getIssueLoan()!=0){
				if(loansign.getIssueLoan()==1){
					sqlbuffer.append(" AND lbs.issueLoan<100000 ");
					countsql.append(" AND lbs.issueLoan<100000 ");
				}else if(loansign.getIssueLoan()==2){
					sqlbuffer.append(" AND lbs.issueLoan>=100000 AND lbs.issueLoan<=1000000 ");
					countsql.append(" AND lbs.issueLoan>=100000 AND lbs.issueLoan<=1000000 ");
				}else{
					sqlbuffer.append(" AND lbs.issueLoan>1000000 ");
					countsql.append(" AND lbs.issueLoan>1000000 ");
				}
			}
			
		}
		
		if(status!=null&&!status.equals("")){
			sqlbuffer.append(" AND ls.status="+status);
			countsql.append(" AND ls.status="+status);
		}
		
		if(loansign.getRecommend()!=null&&loansign.getRecommend()!=2){
			sqlbuffer.append(" AND ls.recommend="+loansign.getRecommend());
			countsql.append(" AND ls.recommend="+loansign.getRecommend());
		}
		
		if (StringUtil.isNotBlank(beginpublishTime)) {
			sqlbuffer.append(" and DATE_FORMAT(ls.publish_time,'%y-%m-%d') >= ").
			append("DATE_FORMAT('"+beginpublishTime).append("','%y-%m-%d')");
			countsql.append(" and DATE_FORMAT(ls.publish_time,'%y-%m-%d') >= ").
			append("DATE_FORMAT('"+beginpublishTime).append("','%y-%m-%d')");
		}

		if (StringUtil.isNotBlank(endpublishTime)) {
			sqlbuffer.append(" and DATE_FORMAT(ls.publish_time,'%y-%m-%d') <= ").
			append("DATE_FORMAT('"+endpublishTime).append("','%y-%m-%d')");
			countsql.append(" and DATE_FORMAT(ls.publish_time,'%y-%m-%d') <= ").
			append("DATE_FORMAT('"+endpublishTime).append("','%y-%m-%d')");
		}
		
		if (StringUtil.isNotBlank(beginfullTime)) {
			sqlbuffer.append(" and DATE_FORMAT(ls.full_time,'%y-%m-%d') >= ").
			append("DATE_FORMAT('"+beginfullTime).append("','%y-%m-%d')");
			countsql.append(" and DATE_FORMAT(ls.full_time,'%y-%m-%d') >= ").
			append("DATE_FORMAT('"+beginfullTime).append("','%y-%m-%d')");
		}

		if (StringUtil.isNotBlank(endfullTime)) {
			sqlbuffer.append(" and DATE_FORMAT(ls.full_time,'%y-%m-%d') <= ").
			append("DATE_FORMAT('"+endfullTime).append("','%y-%m-%d')");
			countsql.append(" and DATE_FORMAT(ls.full_time,'%y-%m-%d') <= ").
			append("DATE_FORMAT('"+endfullTime).append("','%y-%m-%d')");
		}
		
		if (StringUtil.isNotBlank(begincreditTime)) {
			sqlbuffer.append(" and DATE_FORMAT(ls.credit_time,'%y-%m-%d') >= ").
			append("DATE_FORMAT('"+begincreditTime).append("','%y-%m-%d')");
			countsql.append(" and DATE_FORMAT(ls.credit_time,'%y-%m-%d') >= ").
			append("DATE_FORMAT('"+begincreditTime).append("','%y-%m-%d')");
		}

		if (StringUtil.isNotBlank(endcreditTime)) {
			sqlbuffer.append(" and DATE_FORMAT(ls.credit_time,'%y-%m-%d') <= ").
			append("DATE_FORMAT('"+endcreditTime).append("','%y-%m-%d')");
			countsql.append(" and DATE_FORMAT(ls.credit_time,'%y-%m-%d') <= ").
			append("DATE_FORMAT('"+endcreditTime).append("','%y-%m-%d')");
		}
		
		sqlbuffer.append(" ORDER BY ls.credit_time DESC");
			
		datalist = dao.pageListBySql(page, countsql.toString(), sqlbuffer.toString(), null);

		return datalist;	
	}

	
	public List queryAll(String ids) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryAll()方法开始"); //$NON-NLS-1$
		}
		StringBuffer sqlbuffer = new StringBuffer(
		"SELECT \n"
						+ "ls.id,IFNULL(ls.`name`,''),\n"
						+ "CASE WHEN ls.type=1 THEN '众筹' else '天标' END,ifnull(u.`name`,''),\n"
						+ "ls.issueLoan,\n"
						+ "ls.priority,\n"
						+ "ls.middle,\n"
						+ "ls.`after`,\n"
						+ "ls.prio_rate,\n"
						+ "ls.prio_aword_rate,\n"
						+ "ls.remonth,\n"
						+ "lbs.proindustry,\n"
						+ "ls.publish_time,\n"
						+ "'一次性全额到帐',\n"
						+ "CASE WHEN ls.rest_money=0 THEN '已满标' ELSE '未满标' END,\n"
						+ "CASE  WHEN ls.status=1 THEN '进行中' WHEN ls.status=2 THEN '融资成功'  WHEN ls.status=3 THEN '还款中'  WHEN ls.status=4 THEN '已完成'  ELSE '还款中' END,ls.credit_time,\n"
						+ "CASE WHEN ls.recommend=0 THEN '不推荐' ELSE '推荐' END \n"
						+ "from loansign ls,userbasicsinfo u,loansignbasics lbs where u.id=ls.userbasicinfo_id and ls.id=lbs.id  and ls.state=2 and ls.isdet!=1 AND ls.status in (6 ,7) ");

		// 查询数据sql拼接
		if (StringUtil.isNotBlank(ids)) {
			sqlbuffer.append(" and  ls.id in (" + ids + ")");
		}

		List returnList = dao.findBySql(sqlbuffer.toString());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryAll()方法结束OUTPARAM=" + returnList); //$NON-NLS-1$
		}
		return returnList;

	}
	
	public List<Repaymentrecord> queryRepaymentrecordList(String loanSignId) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT l.name, r.periods, r.preRepayDate, (r.money + r.preRepayMoney),  (r.middleMoney+r.middlePreRepayMoney), (r.afterMoney+r.afterPreRepayMoney),IFNULL((r.companyPreFee ), 0.00 ),CASE  WHEN r.repayState=1 THEN '待还款' WHEN r.repayState=2 THEN '按时还款'  WHEN r.repayState=3 THEN '逾期未还款'  WHEN r.repayState=4 THEN '逾期已还款'  ELSE '提前还款' END ,r.repayTime, ");
		sb.append("  IFNULL((r.realMoney), 0.00), IFNULL(( r.middleRealMoney ), 0.00 ), IFNULL((r.afterRealMoney ), 0.00 ),IFNULL((r.companyRealFee ), 0.00 )");
		sb.append("  FROM repaymentrecord  r ,loansign l  where r.loanSign_id=l.id  "); 
		if(StringUtil.isNotBlank(loanSignId)){
                  sb.append(" and  r.loanSign_id = ").append(loanSignId);
		}
		sb.append(" ORDER BY r.loanSign_id, r.periods  asc");
		list = dao.findBySql(sb.toString());
		return list;
	}
	
	public List<Repaymentrecord> getRepaymentrecordList(String loanSignId) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT l.name, r.periods, r.preRepayDate, r.money , r.preRepayMoney,  r.middleMoney,r.middlePreRepayMoney, r.afterMoney,r.afterPreRepayMoney,(money + preRepayMoney+middleMoney+middlePreRepayMoney+afterMoney+afterPreRepayMoney),r.companyPreFee,CASE  WHEN r.repayState=1 THEN '待还款' WHEN r.repayState=2 THEN '按时还款'  WHEN r.repayState=3 THEN '逾期未还款'  WHEN r.repayState=4 THEN '逾期已还款'  ELSE '提前还款' END ,r.repayTime, ");
		sb.append("  IFNULL((r.realMoney), 0.00), IFNULL(( r.middleRealMoney ), 0.00 ), IFNULL((r.afterRealMoney ), 0.00 ),(IFNULL((realMoney),00)+IFNULL(( middleRealMoney ),0.00)+IFNULL((afterRealMoney ), 0.00 )),r.companyRealFee ");
		sb.append("  FROM repaymentrecord  r ,loansign l  where r.loanSign_id=l.id  "); 
		if(StringUtil.isNotBlank(loanSignId)){
                  sb.append(" and  r.loanSign_id = ").append(loanSignId);
		}
		sb.append(" ORDER BY r.loanSign_id, r.periods  asc");
		list = dao.findBySql(sb.toString());
		return list;
	}
	
	/***
	 * 查询还款明细
	 * @param loanSignId
	 * @return
	 */
	public List queryRePayMentParticularsList(String loanSignId){
		StringBuffer sb=new StringBuffer("select u.id,u.userName,u.`name`,(select l.name from loansign l where p.loanSign_id=l.id),p.periods,p.preRepayDate,r.money, r.preRepayMoney,r.middleMoney,r.middlePreRepayMoney, r.afterMoney,r.afterPreRepayMoney,r.realMoney, r.middleRealMoney,"
				+ " r.afterRealMoney,CASE  WHEN p.repayState=1 THEN '待还款' WHEN p.repayState=2 THEN '按时还款'  WHEN p.repayState=3 THEN '逾期未还款'  WHEN p.repayState=4 THEN '逾期已还款'  ELSE '提前还款' END ,p.repayTime "
				+ "from repaymentrecordparticulars r,repaymentrecord p ,userbasicsinfo u  where r.repaymentrecordId=p.id and r.userId=u.id    ");
		if(StringUtil.isNotBlank(loanSignId)){
            sb.append(" and  p.loanSign_id = ").append(loanSignId);
	     }
		sb.append(" ORDER BY p.loanSign_id,p.periods asc");
		return dao.findBySql(sb.toString());
	}
	
	/***
	 * 总放款金额
	 * @param begincreditTime
	 * @param endcreditTime
	 * @return
	 */
	public Double getSumIssueLoan(String begincreditTime,String endcreditTime,String sqlValue){
		String sql="select  "+sqlValue+"  from loansign l where   1=1  ";
		if (StringUtil.isNotBlank(begincreditTime)) {
			sql+=" and DATE_FORMAT(l.credit_time,'%y-%m-%d') >= DATE_FORMAT('"+begincreditTime+"','%y-%m-%d')";
		}
		if (StringUtil.isNotBlank(endcreditTime)) {
			sql+=" and DATE_FORMAT(l.credit_time,'%y-%m-%d') <= DATE_FORMAT('"+endcreditTime+"','%y-%m-%d')";
		}
		Object sumIssueLoan=dao.findObjectBySql(sql);
		return   sumIssueLoan != null ? Double.valueOf(sumIssueLoan.toString()) : 0.00;
	}
	
	/***
	 * 总利息/待还款利息
	 * @param begincreditTime
	 * @param endcreditTime
	 * @return
	 */
	public Double getSumPreRepay(String begincreditTime,String endcreditTime,String valueSql){
		String sql="select sum(IFNULL(r.preRepayMoney,0.00)+IFNULL(r.middlePreRepayMoney,0.00)+IFNULL(r.afterPreRepayMoney,0.00))  from repaymentrecord  r where r.loanSign_id in (select id from loansign l where 1=1 ";
		if (StringUtil.isNotBlank(begincreditTime)) {
			sql+=" and DATE_FORMAT(l.credit_time,'%y-%m-%d') >= DATE_FORMAT('"+begincreditTime+"','%y-%m-%d')";
		}
		if (StringUtil.isNotBlank(endcreditTime)) {
			sql+=" and DATE_FORMAT(l.credit_time,'%y-%m-%d') <= DATE_FORMAT('"+endcreditTime+"','%y-%m-%d')";
		}
		sql+=" )";
		if(StringUtil.isNotBlank(valueSql)){
			sql+=valueSql;
		}
		Object sumPreRepay=dao.findObjectBySql(sql);
		return   sumPreRepay != null ? Double.valueOf(sumPreRepay.toString()) : 0.00;
	}
	
	/***
	 * 逾期利息
	 * @param begincreditTime
	 * @param endcreditTime
	 * @return
	 */
	public Double getSumOverdueInterest(String begincreditTime,String endcreditTime){
		String sql="select sum(overdueInterest)  from repaymentrecord  r where r.loanSign_id in (select id from loansign l where 1=1 ";
		if (StringUtil.isNotBlank(begincreditTime)) {
			sql+=" and DATE_FORMAT(l.credit_time,'%y-%m-%d') >= DATE_FORMAT('"+begincreditTime+"','%y-%m-%d')";
		}
		if (StringUtil.isNotBlank(endcreditTime)) {
			sql+=" and DATE_FORMAT(l.credit_time,'%y-%m-%d') <= DATE_FORMAT('"+endcreditTime+"','%y-%m-%d')";
		}
		sql+=" )";
		Object sumPreRepay=dao.findObjectBySql(sql);
		return   sumPreRepay != null ? Double.valueOf(sumPreRepay.toString()) : 0.00;
	}
	
	/***
	 * 已还款总利息
	 * @param begincreditTime
	 * @param endcreditTime
	 * @return
	 */
	public Double getSumRepayMoney(String begincreditTime,String endcreditTime){
		String sql="select sum((IFNULL(r.realMoney,0.00)-IFNULL(r.money,0.00))+(IFNULL(r.middleRealMoney,0.00)-IFNULL(r.middleMoney,0.00))+(IFNULL(r.afterRealMoney,0.00)-IFNULL(r.afterMoney,0.00)))  from repaymentrecord  r where r.loanSign_id in (select id from loansign l where 1=1 ";
		if (StringUtil.isNotBlank(begincreditTime)) {
			sql+=" and DATE_FORMAT(l.credit_time,'%y-%m-%d') >= DATE_FORMAT('"+begincreditTime+"','%y-%m-%d')";
		}
		if (StringUtil.isNotBlank(endcreditTime)) {
			sql+=" and DATE_FORMAT(l.credit_time,'%y-%m-%d') <= DATE_FORMAT('"+endcreditTime+"','%y-%m-%d')";
		}
		sql+=" )  and  r.repayState in (2,4,5) ";
		Object sumPreRepay=dao.findObjectBySql(sql);
		return   sumPreRepay != null ? Double.valueOf(sumPreRepay.toString()) : 0.00;
	}
	
	/***
	 * 待还款总额/已还款总额
	 * @param begincreditTime
	 * @param endcreditTime
	 * @param valueSql
	 * @return
	 */
	public  Double getSumMoney(String begincreditTime,String endcreditTime,String valueSql){
		String sql="select sum(IFNULL(r.money,0.00)+IFNULL(r.middleMoney,0.00)+IFNULL(r.afterMoney,0.00))  from repaymentrecord  r where r.loanSign_id in (select id from loansign l where 1=1 ";
		if (StringUtil.isNotBlank(begincreditTime)) {
			sql+=" and DATE_FORMAT(l.credit_time,'%y-%m-%d') >= DATE_FORMAT('"+begincreditTime+"','%y-%m-%d')";
		}
		if (StringUtil.isNotBlank(endcreditTime)) {
			sql+=" and DATE_FORMAT(l.credit_time,'%y-%m-%d') <= DATE_FORMAT('"+endcreditTime+"','%y-%m-%d')";
		}
		sql+=" )";
		if(StringUtil.isNotBlank(valueSql)){
			sql+=valueSql;
		}
		Object sumPreRepay=dao.findObjectBySql(sql);
		return   sumPreRepay != null ? Double.valueOf(sumPreRepay.toString()) : 0.00;
	}
	
}
