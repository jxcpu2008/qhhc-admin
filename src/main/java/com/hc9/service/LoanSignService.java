package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Industry;
import com.hc9.dao.entity.Loandynamic;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * <p>
 * Title:LoanSignService
 * </p>
 * <p>
 * Description: 普通标服务层
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author LongYang
 *         <p>
 *         date 2014年2月14日
 *         </p>
 */
@Service
public class LoanSignService {
	private static final Logger LOGGER = Logger.getLogger(LoanSignService.class);
	
	/** dao */
	@Resource
	private HibernateSupport dao;

	/** loanSignQuery */
	@Resource
	private LoanSignQuery loanSignQuery;

	/** baseLoansignService */
	@Resource
	private BaseLoansignService baseLoansignService;
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;

	/** loansignQueryConditions */
	private String loansignQueryConditions = "";

	/**
	 * 自动投标
	 */
	@Resource
	private AutomaticService automaticService;
	
	@Resource
	private VoteincomeService voteincomeService;
	
	@Resource
	private MatchingInterestService matchingInterestService;

	/**
	 * 普通标条数
	 * 
	 * @param loansignbasics
	 *            借款标查询对象
	 * @return 条数
	 */
	public int getLoansignCount(Loansignbasics loansignbasics, String loanType) {
		StringBuffer sb = new StringBuffer(
				"SELECT COUNT(1) FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.loansign_id ");
		sb.append(" INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id WHERE loansign.loanType = 1");
		sb.append(baseLoansignService.getQueryConditions(loansignbasics,
				loanType));
		return loanSignQuery.queryCount(sb.toString());
	}

	/**
	 * 普通标列表
	 * 
	 * @param start
	 *            start
	 * @param limit
	 *            limit
	 * @param loansignbasics
	 *            借款标基础信息
	 * @return list
	 */
	@SuppressWarnings("rawtypes")
	public List loanSignPage(PageModel page, Loansign loansign) {
		List datalist = new ArrayList();

		StringBuffer countsql = new StringBuffer("SELECT count(1)   ");
		countsql.append(" FROM loansign ls,userbasicsinfo u,loansignbasics lbs  ");
		countsql.append(" where u.id=ls.userbasicinfo_id and ls.id=lbs.id   and ls.state=2 and ls.isdet!=1");

		StringBuffer sqlbuffer = new StringBuffer("SELECT\n" + "	ls.id,\n"
				+ "	ls.prio_rate,\n" + "	ls.prio_aword_rate,\n"
				+ "	ls.issueLoan,\n" + "	ls.priority,\n" + "	ls.middle,\n"
				+ "	ls.`after`,\n" + "	ls.prio_rest_money,\n"
				+ "	ls.mid_rest_money,\n" + "	ls.after_rest_money,\n"
				+ "	ls.loanUnit,\n" + "	IFNULL(ls.`name`, ''),\n"
				+ "	lbs.proindustry,\n" + "	ifnull(u.`name`, ''),\n"
				+ "	lbs.projectState,\n" + "	lbs.haveOther,\n"
				+ "	ls.publish_time,\n" + "	ls.appropriation,\n"
				+ "	lbs.remark,\n" + "	ls.status,\n" + "	ls.recommend,\n"
				+ "	ls.rest_money,\n" + "	ls.remonth,\n"
				+ "	ls.type,ls.mid_rate,ls.after_rate,ls.real_rate,ls.feeState,ls.feeMoney,ls.fee,ls.companyFee,IFNULL(ls.real_rate,0.00)-IFNULL(ls.companyFee,0.00),ls.redEnvelopeMoney,ls.activityStatus   \n"
				+ "FROM\n" + "	loansign ls,\n" + "	userbasicsinfo u,\n"
				+ "	loansignbasics lbs\n" + "WHERE\n"
				+ "	u.id = ls.userbasicinfo_id\n" + "AND lbs.id = ls.id\n"
				+ "AND ls.state = 2\n" + "AND ls.isdet != 1");

		if (loansign.getName() != null && loansign.getName() != "") {
			String loanname = "";
			try {
				loanname = java.net.URLDecoder.decode(loansign.getName(),
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" AND ls.name like '%").append(loanname)
					.append("%'");
			countsql.append(" AND ls.name like '%").append(loanname)
					.append("%'");
		}

		if (loansign.getUserbasicsinfo() != null) {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(loansign.getUserbasicsinfo()
						.getName(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (loansign.getUserbasicsinfo().getName() != "") {
				sqlbuffer.append(" AND u.name like '%").append(name)
						.append("%'");
				countsql.append(" AND u.name like '%").append(name)
						.append("%'");
			}
		}

		if (loansign.getType() != null && loansign.getType() != 0) {
			sqlbuffer.append(" AND ls.type=" + loansign.getType());
			countsql.append(" AND ls.type=" + loansign.getType());
		}
		// TODO 此处判断getIssueLoan()==2是什么意思
		if (loansign.getIssueLoan() != null && loansign.getIssueLoan() != 0) {
			if (loansign.getIssueLoan() == 1) {
				sqlbuffer.append(" AND ls.issueLoan<100000 ");
				countsql.append(" AND ls.issueLoan<100000 ");
			} else if (loansign.getIssueLoan() == 2) {
				sqlbuffer
						.append(" AND ls.issueLoan>=100000 AND ls.issueLoan<=1000000 ");
				countsql.append(" AND ls.issueLoan>=100000 AND ls.issueLoan<=1000000 ");
			} else {
				sqlbuffer.append(" AND ls.issueLoan>1000000 ");
				countsql.append(" AND ls.issueLoan>1000000 ");
			}
		}

		sqlbuffer.append(" AND ls.status in (1,2)");
		countsql.append(" AND ls.status in (1,2)");

		if (loansign.getRecommend() != null && loansign.getRecommend() != 2) {
			sqlbuffer.append(" AND ls.recommend=" + loansign.getRecommend());
			countsql.append(" AND ls.recommend=" + loansign.getRecommend());
		}
		sqlbuffer.append(" ORDER BY ls.id desc");

		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);

		return datalist;
	}
	
	
	/**
	 * 普通标列表
	 * 
	 * @param start
	 *            start
	 * @param limit
	 *            limit
	 * @param loansignbasics
	 *            推荐
	 * @return list
	 */
	@SuppressWarnings("rawtypes")
	public List loanSignPageOnIndexRecommend(PageModel page, Loansign loansign) {
		List datalist = new ArrayList();

		StringBuffer countsql = new StringBuffer("SELECT count(1)   ");
		countsql.append(" FROM loansign ls,userbasicsinfo u,loansignbasics lbs  ");
		countsql.append(" where u.id=ls.userbasicinfo_id and ls.id=lbs.id   and ls.state=2 and ls.isdet!=1  ");

		StringBuffer sqlbuffer = new StringBuffer("SELECT\n" + "	ls.id,\n"
				+ "	ls.prio_rate,\n" + "	ls.prio_aword_rate,\n"
				+ "	ls.issueLoan,\n" + "	ls.priority,\n" + "	ls.middle,\n"
				+ "	ls.`after`,\n" + "	ls.prio_rest_money,\n"
				+ "	ls.mid_rest_money,\n" + "	ls.after_rest_money,\n"
				+ "	ls.loanUnit,\n" + "	IFNULL(ls.`name`, ''),\n"
				+ "	lbs.proindustry,\n" + "	ifnull(u.`name`, ''),\n"
				+ "	lbs.projectState,\n" + "	lbs.haveOther,\n"
				+ "	ls.publish_time,\n" + "	ls.appropriation,\n"
				+ "	lbs.remark,\n" + "	ls.status,\n" + "	ls.recommend,\n"
				+ "	ls.rest_money,\n" + "	ls.remonth,\n"
				+ "	ls.type,ls.mid_rate,ls.after_rate,ls.real_rate,ls.onIndex   \n"
				+ "FROM\n" + "	loansign ls,\n" + "	userbasicsinfo u,\n"
				+ "	loansignbasics lbs\n" + "WHERE\n"
				+ "	u.id = ls.userbasicinfo_id\n" + "AND lbs.id = ls.id\n"
				+ "AND ls.state = 2\n" + "AND ls.isdet != 1 ");

		if (loansign.getName() != null && loansign.getName() != "") {
			String loanname = "";
			try {
				loanname = java.net.URLDecoder.decode(loansign.getName(),
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" AND ls.name like '%").append(loanname)
					.append("%'");
			countsql.append(" AND ls.name like '%").append(loanname)
					.append("%'");
		}

		if (loansign.getUserbasicsinfo() != null) {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(loansign.getUserbasicsinfo()
						.getName(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (loansign.getUserbasicsinfo().getName() != "") {
				sqlbuffer.append(" AND u.name like '%").append(name)
						.append("%'");
				countsql.append(" AND u.name like '%").append(name)
						.append("%'");
			}
		}

		if (loansign.getType() != null && loansign.getType() != 0) {
			sqlbuffer.append(" AND ls.type=" + loansign.getType());
			countsql.append(" AND ls.type=" + loansign.getType());
		}
		// TODO 此处判断getIssueLoan()==2是什么意思
		if (loansign.getIssueLoan() != null && loansign.getIssueLoan() != 0) {
			if (loansign.getIssueLoan() == 1) {
				sqlbuffer.append(" AND ls.issueLoan<100000 ");
				countsql.append(" AND ls.issueLoan<100000 ");
			} else if (loansign.getIssueLoan() == 2) {
				sqlbuffer
						.append(" AND ls.issueLoan>=100000 AND ls.issueLoan<=1000000 ");
				countsql.append(" AND ls.issueLoan>=100000 AND ls.issueLoan<=1000000 ");
			} else {
				sqlbuffer.append(" AND ls.issueLoan>1000000 ");
				countsql.append(" AND ls.issueLoan>1000000 ");
			}
		}
		
		if (loansign.getRecommend() != null && loansign.getRecommend() != 2) {
			sqlbuffer.append(" AND ls.recommend=" + loansign.getRecommend());
			countsql.append(" AND ls.recommend=" + loansign.getRecommend());
		}
		
		if(loansign.getOnIndex()!=null&&loansign.getOnIndex()!=2){
			sqlbuffer.append(" AND ls.onIndex=" + loansign.getOnIndex());
			countsql.append(" AND ls.onIndex=" + loansign.getOnIndex());
		}

		sqlbuffer.append(" ORDER BY ls.id desc");

		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);

		return datalist;
	}

	
	@SuppressWarnings("rawtypes")
	public List loanSignPageOnIndexNot(PageModel page, Loansign loansign) {
		List datalist = new ArrayList();

		StringBuffer countsql = new StringBuffer("SELECT count(1)   ");
		countsql.append(" FROM loansign ls,userbasicsinfo u,loansignbasics lbs  ");
		countsql.append(" where u.id=ls.userbasicinfo_id and ls.id=lbs.id   and ls.state=2 and ls.isdet!=1 and  (ls.recommend=1  or  ls.onIndex=1) ");

		StringBuffer sqlbuffer = new StringBuffer("SELECT\n" + "	ls.id,\n"
				+ "	ls.prio_rate,\n" + "	ls.prio_aword_rate,\n"
				+ "	ls.issueLoan,\n" + "	ls.priority,\n" + "	ls.middle,\n"
				+ "	ls.`after`,\n" + "	ls.prio_rest_money,\n"
				+ "	ls.mid_rest_money,\n" + "	ls.after_rest_money,\n"
				+ "	ls.loanUnit,\n" + "	IFNULL(ls.`name`, ''),\n"
				+ "	lbs.proindustry,\n" + "	ifnull(u.`name`, ''),\n"
				+ "	lbs.projectState,\n" + "	lbs.haveOther,\n"
				+ "	ls.publish_time,\n" + "	ls.appropriation,\n"
				+ "	lbs.remark,\n" + "	ls.status,\n" + "	ls.recommend,\n"
				+ "	ls.rest_money,\n" + "	ls.remonth,\n"
				+ "	ls.type,ls.mid_rate,ls.after_rate,ls.real_rate,ls.onIndex   \n"
				+ "FROM\n" + "	loansign ls,\n" + "	userbasicsinfo u,\n"
				+ "	loansignbasics lbs\n" + "WHERE\n"
				+ "	u.id = ls.userbasicinfo_id\n" + "AND lbs.id = ls.id\n"
				+ "AND ls.state = 2\n" + "AND ls.isdet != 1 and  (ls.recommend=1  or  ls.onIndex=1)");

		sqlbuffer.append(" ORDER BY ls.id desc");

		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);

		return datalist;
	}
	/**
	 * 审核的
	 * 
	 * @param page
	 * @param loansignbasics
	 * @param loanType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getLoansignExamine(PageModel page, String beginDate,
			String endDate, String loanType) {
		List datalist = new ArrayList();

		StringBuffer countsql = new StringBuffer("SELECT count(1) FROM  ");
		countsql.append(" loansign ls,userbasicsinfo u,loansignbasics lbs  ");
		countsql.append(" where u.id=ls.userbasicinfo_id and lbs.id=ls.id  and  ls.isdet!=1 and ls.state=1 ");
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT ls.id, ls.prio_rate, "
						+ // 利率\n
						"ls.prio_aword_rate,"
						+ // 优先奖励\n
						"ls.priority, "
						+ // 优先\n
						"ls.middle, "
						+ // 夹层\n
						"ls.`after`,"
						+ // 劣后\n
						"	ls.prio_rest_money," + "	ls.mid_rest_money,"
						+ "	ls.after_rest_money,"
						+ "	ls.loanUnit,"
						+ "ifnull(ls. NAME, ''), loansignbasics.proindustry,"
						+ // 所属行业\n
						"loansignbasics.projectState, "
						+ // 项目状态\n
						"loansignbasics.haveOther, "
						+ // 是否有其他项目：1是，2否'\n
						"ls.publish_time, "
						+ // 发布时间\n
						"ls.appropriation,"
						+ // 拨款方式\n
						"loansignbasics.remark,"
						+ // 项目简介\n
						"ls.state, "
						+ // 初审状态 0未提交1审核中 2审核通过 3审核不通过',\n
						"ifnull(userbasicsinfo. NAME, ''), ls.issueLoan, "
						+ // 购买金额\n
						"ls.remonth,"
						+ // 回购期限\n
						"ls.create_time,"
						+ // 创建时间\n
						"( SELECT username FROM adminuser WHERE id = ls.adminuser_id ) AS adminuser, ls.type,ls.mid_rate,ls.after_rate  FROM loansign ls, userbasicsinfo, loansignbasics WHERE userbasicsinfo.id = ls.userbasicinfo_id AND loansignbasics.id = ls.id AND ls.isdet!=1  and ls.state=1 ");
		sqlbuffer.append(connectionSql(beginDate, endDate));
		countsql.append(connectionSql(beginDate, endDate));
		sqlbuffer.append(" ORDER BY ls.state ASC");
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);

		return datalist;
	}

	public String connectionSql(String beginDate, String endDate) {
		String sql = "";
		if (beginDate != null && !"".equals(beginDate.trim())) {
			sql = sql
					+ " AND DATE_FORMAT(ls.create_time, '%Y-%m-%d')>=DATE_FORMAT('"
					+ beginDate + "', '%Y-%m-%d') ";
		}
		if (endDate != null && !"".equals(endDate.trim())) {
			sql = sql
					+ " AND DATE_FORMAT(ls.create_time, '%Y-%m-%d')<=DATE_FORMAT('"
					+ endDate + "', '%Y-%m-%d') ";
		}
		return sql;
	}

	/**
	 * 普通标列表
	 * 
	 * @param start
	 *            start
	 * @param limit
	 *            limit
	 * @param loansignbasics
	 *            借款标基础信息
	 * @return list
	 */
	@SuppressWarnings("rawtypes")
	public List auditListPage(PageModel page, Loansign loansign, String stateNum) {
		List datalist = new ArrayList();

		StringBuffer countsql = new StringBuffer("SELECT count(1)   ");
		countsql.append(" FROM loansign ls,userbasicsinfo u,loansignbasics lbs  ");
		countsql.append(" where u.id=ls.userbasicinfo_id and ls.id=lbs.id   and ls.state=2 and ls.isdet!=1");

		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT\n"
						+ "	ls.id,\n"
						+ "	ls.prio_rate,\n"
						+ "	ls.prio_aword_rate,\n"
						+ "	ls.issueLoan,\n"
						+ "	ls.priority,\n"
						+ "	ls.middle,\n"
						+ "	ls.`after`,\n"
						+ "	ls.prio_rest_money,\n"
						+ "	ls.mid_rest_money,\n"
						+ "	ls.after_rest_money,\n"
						+ "	ls.loanUnit,\n"
						+ "	IFNULL(ls.`name`, ''),\n"
						+ "	lbs.proindustry,\n"
						+ "	ifnull(u.`name`, ''),\n"
						+ "	lbs.projectState,\n"
						+ "	lbs.haveOther,\n"
						+ "	ls.publish_time,\n"
						+ "	ls.appropriation,\n"
						+ "	lbs.remark,\n"
						+ "	ls.status,\n"
						+ "	ls.recommend,\n"
						+ "	ls.rest_money,\n"
						+ "	ls.remonth,\n"
						+ "	ls.type,ls.mid_rate,ls.after_rate,ls.real_rate,ls.feeState,ls.feeMoney,ls.fee,ls.companyFee,IFNULL(ls.real_rate,0.00)-IFNULL(ls.companyFee,0.00) ,ls.redEnvelopeMoney,ls.refunway,ls.feeMethod  \n"
						+ " FROM\n" + "	loansign ls,\n" + "	userbasicsinfo u,\n"
						+ "	loansignbasics lbs\n" + "WHERE\n"
						+ "	u.id = ls.userbasicinfo_id\n"
						+ "AND lbs.id = ls.id\n" + "AND ls.state = 2\n"
						+ "AND ls.isdet !=1");

		if (loansign.getName() != null && loansign.getName() != "") {
			String loanname = "";
			try {
				loanname = java.net.URLDecoder.decode(loansign.getName(),
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and ls.name like '%").append(loanname)
					.append("%'");
			countsql.append(" and ls.name like '%").append(loanname)
					.append("%'");
		}

		if (loansign.getUserbasicsinfo() != null) {
			if (loansign.getUserbasicsinfo().getName() != "") {
				String name = "";
				try {
					name = java.net.URLDecoder.decode(loansign.getUserbasicsinfo().getName(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and u.name like '%").append(name).append("%'");
				countsql.append(" and u.name like '%").append(name).append("%'");
			}
		}

		if (loansign.getType() != null && loansign.getType() != 0) {
			sqlbuffer.append(" and ls.type=" + loansign.getType());
			countsql.append(" and ls.type=" + loansign.getType());
		}
		// TODO 此处判断getIssueLoan()==2是什么意思
		if (loansign.getLoansignbasics() != null) {
			if (loansign.getIssueLoan() != null && loansign.getIssueLoan() != 0) {
				if (loansign.getIssueLoan() == 1) {
					sqlbuffer.append(" and lbs.issueLoan<100000 ");
					countsql.append(" and lbs.issueLoan<100000 ");
				} else if (loansign.getIssueLoan() == 2) {
					sqlbuffer
							.append(" and lbs.issueLoan>=100000 and lbs.issueLoan<=1000000 ");
					countsql.append(" and lbs.issueLoan>=100000 and lbs.issueLoan<=1000000 ");
				} else {
					sqlbuffer.append(" and lbs.issueLoan>1000000 ");
					countsql.append(" and lbs.issueLoan>1000000 ");
				}
			}
		}
		if (loansign.getRecommend() != null && loansign.getRecommend() != 2) {
			sqlbuffer.append(" and ls.recommend=" + loansign.getRecommend());
			countsql.append(" and ls.recommend=" + loansign.getRecommend());
		}
		sqlbuffer.append(" and ls.status=" + stateNum);
		countsql.append(" and ls.status=" + stateNum);
		sqlbuffer.append(" order by ls.status desc");
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);
		return datalist;
	}

	public void updateLoanContractNo(Loansign loansign) {
		Loansign loan = dao.get(Loansign.class, loansign.getId());
		loan.setContractNo(loansign.getContractNo());
		dao.update(loan);

	}

	public String updateLoansignState(String ids, Integer state,Loansignbasics loansignbasics,
			final HttpServletRequest request, Double realRate,Double companyFee,Integer feeMethod) {
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		if (StringUtil.isNotBlank(ids)) {
			Loansign loan = loanSignQuery.getLoansignById(ids);
				if (state == 2) {
					Double feeMoney=0.00;
					if(loan.getType() == 2){    //按项目
					    feeMoney =Arith.round(Arith.mul(Arith.div(Arith.mul(loan.getIssueLoan(), Arith.div(Double.valueOf(companyFee),100,4)), Double.valueOf(12)),loan.getRemonth()), 2);
					}else if(loan.getType() == 3){ //按天标
						feeMoney=Arith.round(Arith.mul(Arith.div(Arith.mul(loan.getIssueLoan(), Arith.div(Double.valueOf(companyFee),100,4)), Double.valueOf(360)), loan.getRemonth()), 2); 
					}
					StringBuffer sql = new StringBuffer("update loansign SET loansign.state=2, loansign.status=1 ");
					sql.append(",loansign.adminuser_id=" + loginuser.getId());
					sql.append(",loansign.contractNo='").append(getContractNo(loan)+"'");
					sql.append(",loansign.real_rate=").append(Arith.div(realRate, 100, 4));
					sql.append(",loansign.companyFee=").append(Arith.div(Double.valueOf(companyFee),100,4));
					sql.append(",loansign.feeMoney=").append(feeMoney);
					sql.append(",loansign.feeMethod=").append(feeMethod);
					sql.append(",loansign.publish_time='").append(DateUtils.format("yyyy-MM-dd HH:mm:ss")+"'");
					sql.append("  WHERE loansign.id ="+ ids);
					dao.executeSql(sql.toString());
					String sqlLoan="update  loansignbasics  set recommandReason='"+loansignbasics.getRecommandReason()+"' WHERE id ="+ ids;
					dao.executeSql(sqlLoan.toString());
					
//					dao.getSession().flush();
					
					return "2";// 审核通过
				} else {
					StringBuffer sql = new StringBuffer("UPDATE loansign SET loansign.state=");
					sql.append(state);
					sql.append(",loansign.adminuser_id=" + loginuser.getId());
					sql.append(",loansign.status=0,loansign.state=3,loansign.examine_time='"+ DateUtils.format("yyyy-MM-dd HH:mm:ss") + "'");
					sql.append("  WHERE loansign.id ="	+ ids );
					if (dao.executeSql(sql.toString()) > 0) {
						return "2";// 审核不通过
					}
			}
		}
		return "1";
	}

	/***
	 * 项目申请放款审批
	 * 
	 * @param ids
	 * @param request
	 * @return
	 */
	public String auditLoansignCredit(String ids, HttpServletRequest request) {
		if (StringUtil.isNotBlank(ids)) {
			// 根据“，”拆分字符串
			String[] newids = ids.split(",");
			// 要修改状态的编号
			String delstr = "";
			for (String idstr : newids) {
				// 将不是空格和非数字的字符拼接
				if (StringUtil.isNotBlank(idstr)
						&& StringUtil.isNumberString(idstr)) {
					delstr += idstr + ",";
				}
			}
			if (delstr.length() > 0) {
				StringBuffer sql = new StringBuffer(
						"UPDATE loansign SET loansign.status=3");
				sql.append(" WHERE loansign.id IN("
						+ delstr.substring(0, delstr.length() - 1) + ")");
				// 批量修改
				if (dao.executeSql(sql.toString()) > 0) {
					return "2";
				}
			}
		}
		return "1";
	}

	public String updateloansignrecommend(String ids, Integer recommend,
			HttpServletRequest request) {
		if (StringUtil.isNotBlank(ids)) {
			// 根据“，”拆分字符串
			String[] newids = ids.split(",");
			// 要修改状态的编号
			String delstr = "";
			for (String idstr : newids) {
				// 将不是空格和非数字的字符拼接
				if (StringUtil.isNotBlank(idstr)
						&& StringUtil.isNumberString(idstr)) {
					delstr += idstr + ",";
				}
			}
			if (delstr.length() > 0) {
				StringBuffer sql = new StringBuffer(
						"UPDATE loansign SET loansign.recommend=");
				sql.append(recommend);

				sql.append(" WHERE loansign.id IN("
						+ delstr.substring(0, delstr.length() - 1) + ")");
				// 批量修改
				if (dao.executeSql(sql.toString()) > 0) {
					return "2";
				}
			}
		}

		return "1";
	}

	/**
	 * 普通标列表转为JSONArray
	 * 
	 * @param list
	 *            集合
	 * @return JSONArray 对象
	 */
	public JSONArray queryJSONByList(List list) {
		JSONObject json = null;
		JSONArray jsonlist = new JSONArray();

		// 给每条数据添加标题
		for (Object obj : list) {
			json = new JSONObject();
			Object[] str = (Object[]) obj;
			json.element("id", str[0]);
			json.element("loanNumber", str[1]);
			json.element("loanTitle", str[2]);
			json.element("name", str[3]);
			json.element("loanUnit", str[4]);
			json.element("issueLoan",
					Arith.round(new BigDecimal(str[5].toString()), 2) + "元");
			json.element("month", str[6] + "个月");
			json.element("loancategory", str[7]);
			// json.element("mgtMoneyScale",
			// Arith.round(new BigDecimal(str[8].toString()), 2) + "%");
			json.element("mgtMoney", str[8]);
			json.element("publishTime", str[9]);
			json.element("rate",
					Arith.round(new BigDecimal(str[10].toString()), 2) + "%");
			json.element("reward",
					Arith.round(new BigDecimal(str[11].toString()), 2) + "%");
			json.element("successfulLending", str[12]);
			json.element("remainingCopies",
					Double.valueOf(str[13].toString()) > 0 ? str[13] : "满标");
			json.element("refundWay", str[14]);
			json.element("loanstate", str[15]);
			json.element("iscredit", str[16]);
			json.element("creditTime", str[17]);
			json.element("isShow", str[18]);
			json.element("isRecommand", str[19]);
			jsonlist.add(json);
		}
		return jsonlist;
	}

	/**
	 * 债权转让列表
	 * 
	 * @param start
	 *            start
	 * @param limit
	 *            limit
	 * @param loansignbasics
	 *            借款标基础信息
	 * @return list
	 */
	@SuppressWarnings("rawtypes")
	public List loanSignflowPage(PageModel page, Loansignbasics loansignbasics,
			String loanType) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				" SELECT ls.id,ls.`name`,ls.proindustry,u.userName  ,ls.loanstate,  "
						+ " ls.haveOther,ls.releaseTime,ls.getMoneyWay,ls.remark,ls.status,ls.recommend,ls.remoney  "
						+ "	from loansign ls,userbasicsinfo u where u.id=ls.userbasicinfo_id and ls.isdet=1");

		System.out.println("债权转让:" + sb);
		loansignQueryConditions = sb.toString();
		/*
		 * sb.append(" LIMIT ").append(page.getPageNum()).append(" , ").append(page
		 * .getNumPerPage());
		 */
		/* list = dao.findBySql(sb.toString()); */
		StringBuffer sbl = new StringBuffer(
				"select COUNT(ls.id) from loansign ls,userbasicsinfo u where u.id=ls.userbasicinfo_id  and ls.isdet=1");
		list = dao.pageListBySql(page, sbl.toString(), sb.toString(), null);
		return list;
	}

	/**
	 * 优金理财json
	 * 
	 * @param list
	 * @return
	 */
	public JSONArray queryJSONByLists(List list) {
		JSONObject json = null;
		JSONArray jsonlist = new JSONArray();

		// 给每条数据添加标题
		for (Object obj : list) {
			json = new JSONObject();
			Object[] str = (Object[]) obj;
			json.element("id", str[0]);
			json.element("loanNumber", str[1]);
			json.element("loanTitle", str[2]);
			json.element("name", str[3]);
			json.element("loanUnit", str[4]);
			json.element("issueLoan",
					Arith.round(new BigDecimal(str[5].toString()), 2) + "元");
			json.element("month", str[6] + "个月");
			json.element("loancategory", "前海红筹理财");
			// json.element("mgtMoneyScale",
			// Arith.round(new BigDecimal(str[8].toString()), 2) + "%");
			json.element("mgtMoney", str[7]);
			json.element("publishTime", str[8]);
			json.element("rate",
					Arith.round(new BigDecimal(str[9].toString()), 2) + "%");
			if (str[10] == null) {
				json.element("reward", 0);
			} else {
				json.element("reward",
						Arith.round(new BigDecimal(str[10].toString()), 2)
								+ "%");
			}
			json.element("successfulLending", str[11]);
			json.element("remainingCopies",
					Double.valueOf(str[12].toString()) > 0 ? str[12] : "满标");
			json.element("refundWay", str[13]);
			json.element("loanstate", str[14]);
			json.element("iscredit", str[15]);
			json.element("creditTime", str[16]);
			json.element("isShow", str[17]);
			json.element("isRecommand", str[18]);
			jsonlist.add(json);
		}
		return jsonlist;
	}

	/**
	 * 优金标总条数
	 * 
	 * @param loansignbasics
	 * @param loanType
	 * @return
	 */
	public int getLoansignCount1(Loansignbasics loansignbasics, String loanType) {
		StringBuffer sb = new StringBuffer(
				"SELECT COUNT(1) FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.loansign_id ");
		sb.append(" INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id WHERE loansign.loanType = 7");
		sb.append(baseLoansignService.getQueryConditions(loansignbasics,
				loanType));
		return loanSignQuery.queryCount(sb.toString());
	}

	/**
	 * 优金标列表
	 * 
	 * @param start
	 * @param limit
	 * @param loansignbasics
	 * @param loanType
	 * @return
	 */
	public List loanSignPage1(PageModel page, Loansignbasics loansignbasics,
			String loanType) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT loansign.id, loansignbasics.loanNumber, loansignbasics.loanTitle, ");
		sb.append(" userbasicsinfo. NAME, loansign.loanUnit, loansign.issueLoan, loansign.`month`, ");
		sb.append(" loansignbasics.mgtMoney, loansign.publishTime, loansign.rate * 100, loansignbasics.reward * 100 ,");
		sb.append(" round ( IFNULL(( SELECT SUM(tenderMoney) / loansign.loanUnit FROM loanrecord WHERE isSucceed = 1 AND loanrecord.loanSign_id = loansign.id ), 0 )), ");
		sb.append(" round ((SELECT ( loansign.issueLoan - IFNULL(SUM(tenderMoney), 0)) / loansign.loanUnit FROM loanrecord WHERE isSucceed = 1 AND loanrecord.loanSign_id = loansign.id)),");
		sb.append(" CASE WHEN loansign.refundWay = 1 THEN '按月等额本息' WHEN loansign.refundWay = 2 THEN '按月付息到期还本' ELSE '到期一次性还本息' END,");
		sb.append(" CASE WHEN loansign.loanstate = 1 THEN '未发布' WHEN loansign.loanstate = 2 THEN '进行中' WHEN loansign.loanstate = 3 THEN '回款中' ELSE '已完成' END, ");
		sb.append(" CASE WHEN loansign.loanstate = 3 OR loansign.loanstate = 4 THEN '已放款' ELSE '未放款' END, ");
		sb.append(" loansignbasics.creditTime, CASE WHEN loansign.isShow = 1 THEN '显示' ELSE '不显示' END, CASE WHEN loansign.isRecommand = 1 THEN '推荐' ELSE '不推荐' END");
		sb.append(" FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.id ");
		sb.append(" INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id  WHERE loansign.loanType = 7");
		sb.append(baseLoansignService.getQueryConditions(loansignbasics,
				loanType));
		sb.append("   ORDER BY loansign.loanstate asc ,loansign.id DESC ");

		loansignQueryConditions = sb.toString();
		/*
		 * System.out.println(sb.toString());
		 * sb.append(" LIMIT ").append(start).append(" , ").append(limit); list
		 * = dao.findBySql(sb.toString());
		 */
		StringBuffer sbl = new StringBuffer(
				"SELECT count(loansign.id) FROM loansign INNER JOIN loansignbasics ON loansign.id = loansignbasics.id  INNER JOIN userbasicsinfo ON loansign.userbasicinfo_id = userbasicsinfo.id WHERE loansign.loanType = 7 ");
		list = dao.pageListBySql(page, sbl.toString(), sb.toString(), null);
		return list;
	}

	/**
	 * 优金计划申请条件
	 * 
	 * @param username
	 * @param cardno
	 * @return
	 */
	public Object queryBorroweryouxuancount(String username, String cardno) {
		StringBuffer sb = new StringBuffer(
				"SELECT count(u.id) FROM borrowers_apply b, borrowersbase s, userbasicsinfo u, userrelationinfo f WHERE b.base_id = s.id AND s.userbasicinfo_id = u.id AND u.id = f.id AND b.`status` = 1 AND b.state = 0 AND b.type=7");
		if (!username.trim().equals("")) {
			sb.append(" and u.name like '%").append(username).append("%'");
		}
		if (!cardno.equals("")) {
			sb.append(" and f.cardId like '%").append(cardno).append("%'");
		}
		return dao.findObjectBySql(sb.toString());

	}

	/**
	 * 优金借款人
	 * 
	 * @param page
	 * @param username
	 * @param cardno
	 * @return
	 */
	public Object queryBorroweryouxuanList(PageModel page, String username,
			String cardno) {
		StringBuffer sb = new StringBuffer(
				"SELECT b.id,u.name, u.userName, f.cardId FROM borrowers_apply b, borrowersbase s, userbasicsinfo u, userrelationinfo f WHERE b.base_id = s.id AND s.userbasicinfo_id = u.id AND u.id = f.id AND b.`status` = 1 AND b.state = 0 AND b.type =7");
		if (!username.trim().equals("")) {
			sb.append(" and u.name like '%").append(username).append("%'");
		}
		if (!cardno.equals("")) {
			sb.append(" and f.cardId like '%").append(cardno).append("%'");
		}
		sb.append(" LIMIT ").append(page.firstResult()).append(",")
				.append(page.getNumPerPage());
		return dao.findBySql(sb.toString(), null);

	}

	/***
	 * 要导出的借款列表数据
	 * 
	 * @return List
	 */
	public List outPutList() {
		return dao.findBySql(loansignQueryConditions.toString());
	}

	/**
	 * <p>
	 * Title: updateRepaymentRecord
	 * </p>
	 * <p>
	 * Description: 逾期已还款的时候调用改信息
	 * </p>
	 * 
	 * @param repaymentrecord
	 *            还款记录
	 * @param repayTime
	 *            还款时间
	 * @return 是否成功
	 */
	public boolean updateRepaymentRecord(Repaymentrecord repaymentrecord,
			String repayTime) {
		// try {
		// 写入实际还款时间和状态=4,并计算出实际的还款金额=本该还的利息和逾期的手续费
		repaymentrecord.setRepayState(4);
		repaymentrecord.setRepayTime(repayTime);
		repaymentrecord.setRealMoney(repaymentrecord.getPreRepayMoney()
				+ (repaymentrecord.getMoney() + repaymentrecord
						.getPreRepayMoney()) * Constant.OVERDUE_INTEREST);
		dao.update(repaymentrecord);
		return true;
		// } catch (Exception e) {
		// return false;
		// }
	}

	/**
	 * 根据用户编号查询用户在当前时间以前购买的所有标的本息和
	 * 
	 * @param id
	 *            用户编号
	 * @return 返回所有本息和
	 */
	public Double getLoanRecordMoney(Long id) {
		String sql = "SELECT l.issueLoan, loan.tenderMoney, SUM(DISTINCT r.money), SUM(DISTINCT r.preRepayMoney), loan.tenderMoney / l.issueLoan * SUM(DISTINCT r.preRepayMoney), loan.tenderMoney / l.issueLoan * SUM(DISTINCT r.money) + loan.tenderMoney / l.issueLoan * SUM(DISTINCT r.preRepayMoney) FROM loanrecord loan INNER JOIN loansign l ON loan.loanSign_id = l.id INNER JOIN repaymentrecord r ON l.id = r.loanSign_id WHERE loan.userbasicinfo_id IN(SELECT b.userbasicinfo_id from borrowersbase b , borrowers_apply a WHERE b.id = a.base_id AND a.id=?) AND l.loanstate = 3 AND loan.tenderTime < ? AND r.repayState = 1 GROUP BY r.loanSign_id";
		List<Object[]> loanList = (List<Object[]>) dao.findBySql(sql, id,
				DateUtils.format("yyyy-MM-dd"));
		// 得到当前用户当前时间之前为完成标的本息和
		Double moneyAndInterest = 0.00;
		if (loanList.size() > 0 && null != loanList) {
			for (int i = 0; i < loanList.size(); i++) {
				Object[] obj = loanList.get(0);
				moneyAndInterest += Double.parseDouble(obj[5].toString());
			}
		}
		return moneyAndInterest;
	}

	/**
	 * 查询该用户是否还有未完成的净值标
	 * 
	 * @param id
	 *            用户编号
	 * @return 返回标的个数
	 */
	public Integer getNetLabel(Long id) {
		String sql = "select count(*) from loansign l where l.loansignType_id=4 and l.loanType=1 and l.userbasicinfo_id=? and l.loanstate!=4";
		int num = dao.queryNumberSql(sql, id).intValue();
		return num;
	}

	/**
	 * 返回指定的借款申请BorrowersApply数据
	 * 
	 * @param uid
	 * @return
	 */
	public Long getBorrowersApply(Long uid) {
		String sql = "select id from borrowers_apply as ba where user_id="
				+ uid;
		List<BigInteger> list = (List<BigInteger>) dao.findBySql(sql);
		return (Long) list.get(0).longValue();
	}

	public void savaLoanign(Loansign loan) {
		dao.getSession().clear();
		dao.saveOrUpdate(loan);
	}

	/**
	 * 查询用户相关的项目
	 */
	public List<Loansign> getLoanSignlist(String uId) {
		String hql = "from Loansign as a inner join fetch a.userbasicsinfo where  a.isDet!=1  and a.userbasicsinfo.id="
				+ uId;
		List<Loansign> loanlist = dao.find(hql);
		return loanlist;
	}

	/**
	 * 添加项目动态发布
	 */
	public void savaloandynamic(Loandynamic loan) {
		dao.save(loan);
	}

	/**
	 * 动态项目查询
	 */
	public List getloandynamic(String loanId) {
		String sql = "select l.name,lb.loanimg, lr.title,lr.remark, lr.publishTime from loansign l,loandynamic lr ,loansignbasics lb where l.id=lr.loan_id  and l.id=lb.id and lr.loan_id="
				+ loanId + " ORDER BY lr.publishTime DESC LIMIT 0,4";
		List list = dao.findBySql(sql.toString());
		return list;
	}

	/**
	 * 更新项目的信息
	 */
	public void updateLoanbasescics(Loansignbasics loansignbasics) {
		dao.update(loansignbasics);
	}

	public Object getLoansignByIds() {
		String sql = "select count(*) from loansign where recommend=1 ";
		Object obj = dao.findObjectBySql(sql.toString());
		return obj;
	}

	/**
	 * 分页查询行业信息
	 * 
	 * @param page
	 *            分页对象
	 * 
	 */
	public List IndustryPage(PageModel page) {
		List list = new ArrayList();
		String hql = "select new Industry (id, name, industryType) from Industry";
		list = dao.pageListByHql(page, hql, true);
		return list;
	}

	/**
	 * 根据id查询行业详情
	 * 
	 * @param Id
	 *            (行业编号)
	 * @return 返回行业管理
	 */
	public Industry queryIndustryById(Integer id) {
		return dao.get(Industry.class, id);
	}

	/**
	 * 修改行业
	 * 
	 * @param topic
	 *            行业
	 */
	public void updateIndustry(Industry industry) {
		dao.update(industry);
	}

	/**
	 * 新增行业
	 * 
	 * @param topic
	 *            行业
	 */
	public void addIndustry(Industry industry) {
		dao.save(industry);
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 */
	@Transactional
	public void deleteIndustry(String ids) {
		String[] id = ids.split(",");
		for (int i = 0; i < id.length; i++) {
			dao.delete(Integer.valueOf(id[i]), Industry.class);
		}

	}

	/**
	 * 项目投资记录
	 */
	public PageModel getLoanrecordList(Long Id, PageModel page) {
		StringBuffer sql = new StringBuffer(
				"select lr.tenderMoney,lr.tenderTime,lr.subType,lr.webOrApp,u.userName,lr.loanType "
						+ "from loanrecord lr,userbasicsinfo u  where u.id=lr.userbasicinfo_id and  lr.isSucceed =1 and  lr.loanSign_id=?");

		StringBuffer sqlCount = new StringBuffer(
				"select count(lr.id) from loanrecord lr,userbasicsinfo u  where u.id=lr.userbasicinfo_id and  lr.isSucceed =1 and  lr.loanSign_id="
						+ Id);

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());

		sql.append(" order by lr.tenderTime desc  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.SRSRUES_TEN).append(",")
				.append(page.getNumPerPage());

		List list = dao.findBySql(sql.toString(), Id);
		page.setList(list);// project集合
		return page;
	}

	public List queryAll(String ids, Loansign loansign) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryAll()方法开始"); //$NON-NLS-1$
		}

		StringBuffer sqlbuffer = new StringBuffer(

				"SELECT \n"
						+ "ls.id,IFNULL(ls.`name`,''),\n"
						+ "CASE WHEN ls.type=2 THEN '普通众持' WHEN ls.type=3 THEN '短期众持' END,ifnull(u.`name`,''),\n"
						+ "ls.issueLoan,\n"
						+ "ls.priority,\n"
						+ "ls.middle,\n"
						+ "ls.`after`,\n"
						+ "ls.prio_rate,\n"
						+ "ls.prio_aword_rate,\n"
						+ "ls.real_rate,"
						+ "ls.companyFee,"
						+ "ls.remonth,\n"
						+ "ls.publish_time,\n"
						+ "'一次性全额到帐',\n"
						+ "CASE WHEN ls.rest_money=0 THEN '已满标' ELSE '未满标' END,\n"
						+ "CASE  WHEN ls.status=1 THEN '进行中' WHEN ls.status=2 THEN '融资成功' END,\n"
						+ "CASE WHEN ls.recommend=0 THEN '不推荐' ELSE '推荐' END,ls.redEnvelopeMoney,CASE WHEN ls.activityStatus=1 THEN '不参与' ELSE '参与' END,ls.redEnvelopeMoney\n"
						+ "from loansign ls,userbasicsinfo u,loansignbasics lbs where u.id=ls.userbasicinfo_id and ls.id=lbs.id  and ls.state=2 and ls.isdet!=1 ");

		if (loansign.getName() != null && loansign.getName() != "") {
			String loanname = "";
			try {
				loanname = java.net.URLDecoder.decode(loansign.getName(),
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" AND ls.name like '%").append(loanname)
					.append("%'");

		}

		if (loansign.getUserbasicsinfo() != null) {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(loansign.getUserbasicsinfo()
						.getName(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (loansign.getUserbasicsinfo().getName() != "") {
				sqlbuffer.append(" AND u.name like '%").append(name)
						.append("%'");

			}
		}

		if (loansign.getType() != null && loansign.getType() != 0) {
			sqlbuffer.append(" AND ls.type=" + loansign.getType());
		}
		// TODO 此处判断getIssueLoan()==2是什么意思
		if (loansign.getLoansignbasics() != null) {
			if (loansign.getIssueLoan() != null && loansign.getIssueLoan() != 0) {
				if (loansign.getIssueLoan() == 1) {
					sqlbuffer.append(" AND lbs.issueLoan<100000 ");
				} else if (loansign.getIssueLoan() == 2) {
					sqlbuffer
							.append(" AND lbs.issueLoan>=100000 AND lbs.issueLoan<=1000000 ");
				} else {
					sqlbuffer.append(" AND lbs.issueLoan>1000000 ");
				}
			}

		}

		if (loansign.getRecommend() != null && loansign.getRecommend() != 2) {
			sqlbuffer.append(" AND ls.recommend=" + loansign.getRecommend());
		}
		sqlbuffer.append(" AND ls.status in (1,2)");
		sqlbuffer.append(" ORDER BY ls.id desc");

		List returnList = dao.findBySql(sqlbuffer.toString());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryAll()方法结束OUTPARAM=" + returnList); //$NON-NLS-1$
		}
		return returnList;

	}

	/**
	 * 根据标ID 删除标
	 * 
	 * @param loanid
	 * @return
	 */
	public int deleteLoansignByloansignId(String loanid) {
		// 审核通过的标不能被删除
		dao.delete(Long.valueOf(loanid), Loansign.class);
		return 0;
	}

	/**
	 * 更新标信息
	 * 
	 * @param ls
	 */
	public void updateLoangn(Loansign ls) {
		// dao.getSession().clear();
		dao.update(ls);
	}

	/***
	 * 根据项目ID查询实际集资总额
	 * 
	 * @param shopId
	 * @return
	 */
	public Double getSumTenderMoney(String id) {
		if (StringUtil.isNotBlank(id)) {
			String sql = "select sum(tenderMoney) from loanrecord where loanSign_id=? and isSucceed=1";
			Object tenderMoney = dao.findObjectBySql(sql, id);
			return Double.valueOf(tenderMoney.toString());
		} else {
			return 0.00;
		}
	}
	
	/***
	 * 根据项目Id查询实际集资总额=购买金额-奖励红包总额
	 * @param id
	 * @return
	 */
	public Double getSumRedenvelopedetail(String id){
		if (StringUtil.isNotBlank(id)) {
			String sql = "select sum(tenderMoney)-SUM(IFNULL(redEnvelopeMoney,0.00)) from loanrecord where loanSign_id=? and isSucceed=1";
			Object tenderMoney = dao.findObjectBySql(sql, id);
			return Double.valueOf(tenderMoney.toString());
		} else {
			return 0.00;
		}
	}

	/***
	 * 根据项目Id查询所有的购买记录
	 * 
	 * @param id
	 * @return
	 */
	public List<Loanrecord> getLoanRecordList(String id) {
		String hql = "from Loanrecord  l where l.isSucceed=1 and l.loansign.id="
				+ id;
		List<Loanrecord> list = dao.find(hql);
		return list;
	}

	/**
	 * 根据标ID得到最后一期还款数据
	 * 
	 * @param lonid
	 */
	public List<Repaymentrecord> getFinallyPeriodsRepaymentrecord(String lonid,
			String periods) {
		String sql = "SELECT * from repaymentrecord  r where r.loanSign_id=? and r.periods=? ";
		List<Repaymentrecord> list = dao.findBySql(sql, Repaymentrecord.class,
				lonid, periods);
		return list;

	}

	/***
	 * 审核
	 * @param ids
	 * @param state
	 * @param request
	 * @return
	 */
	public String updateLoansignState(String id, String stateNum,Integer status, HttpServletRequest request) {
		try{
			Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
			if (StringUtil.isNotBlank(id)) {
				// 根据“，”拆分字符串
				StringBuffer sql = new StringBuffer(
						"update loansign  s set s.status=" + status);
				if (stateNum.equals("1")) { // 审批
					sql.append(", s.approval_time='"+ DateUtils.format("yyyy-MM-dd HH:mm:ss")+ "' , approval_admin=" + loginuser.getId());
				} else { // 审核
					sql.append(", s.audit_time='"+ DateUtils.format("yyyy-MM-dd HH:mm:ss")+ "' , audit_admin=" + loginuser.getId());
				}
				sql.append(" WHERE  id=" + id);
				// 批量修改
				dao.executeSql(sql.toString());
			}
			if (stateNum.equals("1")) {
				// 生成还款计划
				boolean repayMentRecord = getRepaymentrecord(id);
				Loansign loan=loanSignQuery.getLoansign(id);
				if (!repayMentRecord) {
					  if(loan.getRefunway()==4){  //等额等息
						   matchingInterestService.repayMentRecord(loan);
					   }else{
						   baoFuLoansignService.repayMentRecordLast(loan, true);
					   }
					//lkl-20150825生成加息数据
					voteincomeService.saveVoteincome(id);
				}
			} else {
				if (status == 3) {
					String delSql = "delete from repaymentrecord where loanSign_id =?";
					dao.executeSql(delSql, id);
					
					String delVSql="delete  from  voteIncome where loanId=?";
					dao.executeSql(delVSql, id);
				}
			}
			return "2";
		}catch(Exception e){
			LOGGER.debug("满标审批操作发生异常！", e);
			return "1";
		}
	}

	public boolean getRepaymentrecord(String id) {
		String sql = "select * from  repaymentrecord  where loansign_id=?";
		List list = dao.findBySql(sql, id);
		return list.size() > 0 ? true : false;
	}

	/**
	 * 分业查询清盘数据
	 * 
	 * @param page
	 * @param loan
	 * @param loanType
	 * @return
	 */
	public List getLoansignLiquidation(PageModel page, Loansign loan,
			String loanType, Integer windingNum) {
		List datalist = new ArrayList();

		StringBuffer countsql = new StringBuffer(
				" SELECT count(distinct(ls.id)) FROM  ");
		countsql.append(" repaymentrecord r  LEFT JOIN loansign ls on r.loanSign_id=ls.id ");
		countsql.append(" WHERE ( r.preRepayDate < (select current_date()) ) "
				+ " and (repayState =1 or repayState=-1) "
				+ " and ( SELECT TIMESTAMPDIFF(DAY,r.preRepayDate,(select date_format(now(),'%Y-%c-%d'))))>"
				+ windingNum + "");
		// 当前日期之前，没有有还款的，且有大于5天的
		StringBuffer sqlbuffer = new StringBuffer(
				" SELECT "
						+ " ls.id, "
						+ " ls.`name`,"
						+ " ls.issueLoan,"
						+ " ls.priority,"
						+ " ls.middle,"
						+ " ls.`after`,"
						+ " ls.loanUnit,"
						+ " ls.publish_time,"
						+ " IFNULL(ls.create_time,''),"
						+ " ls.refunway,"
						+ "	IFNULL(ls.escrow_id,''),  "
						+ " ls.`status`,"
						+ " r.periods ,ls.real_rate"
						+ " from  repaymentrecord r  LEFT JOIN loansign ls on r.loanSign_id=ls.id  "
						+ " WHERE ( r.preRepayDate < (select current_date()) )  "
						+ " and (repayState =1 or repayState=-1)  "
						+ " and ( SELECT TIMESTAMPDIFF(DAY,r.preRepayDate,(select date_format(now(),'%Y-%c-%d'))))>"
						+ windingNum + "" + " GROUP BY ls.id");
		// sqlbuffer.append(connectionSql(loan.getCreateTime(),loan.getCreditTime()));
		// countsql.append(connectionSql(loan.getCreateTime(),loan.getCreditTime()));
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);

		return datalist;
	}

	/***
	 * 获取项目编号
	 * 
	 * @param createTime
	 * @return
	 */
	public String getContractNo(String createTime) {
		String sql = "select max(right(l.contractNo,3)) from loansign l where  DATE_FORMAT(l.create_time, '%Y-%m-%d')>=DATE_FORMAT('"
				+ createTime + "', '%Y-%m-%d') ";
		Object number = dao.findObjectBySql(sql);
		if (number == null || number == "") {
			number = 1;
		} else {
			number = Integer.parseInt(number.toString()) + 1;
		}

		String contractNo = "HC" + DateUtils.format("yyyyMMdd")
				+ StringUtil.addZeroForNum(number.toString(), 3);
		return contractNo;
	}
	
	/***
	 * 项目编号：项目类型名称+年份+项目号+期数
	 * @param loansign
	 * @return
	 */
	public String getContractNo(Loansign loansign){
		String contractNo=loansign.getLoansignType().getTypeName()+DateUtils.format("yyyy").substring(2, 4)+StringUtil.addZeroForNum(loansign.getProjectNumber(), 4)+loansign.getLoanPeriods();
		return contractNo;
	}

	/***
	 * 确认是否存在未处理的
	 * 
	 * @param loanrecordId
	 * @return
	 */
	public boolean hasWaitforConfirm(Long loanrecordId) {
		String sql = "select lr.id from loanrecord lr where lr.loanSign_id=? and lr.isSucceed in(0,2)";
		List list = dao.findBySql(sql, loanrecordId);
		return list.size() > 0 ? true : false;
	}

	/**
	 * 满标 生成还款计划
	 * 
	 * @param id
	 *            还款标ID
	 * @return 1生成还款计划成功 0生成还款计划失败
	 */
	public void fullBid(String id) {
		try {
			Loansign loan = loanSignQuery.getLoansignById(id);
			baoFuLoansignService.repayMentRecordLast(loan, true);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 清盘项目还款详情
	 * 
	 * @param page
	 * @param loan
	 * @param loanType
	 * @return
	 */
	public List getLoansignLiquidationDetails(PageModel page, String loanid) {
		List datalist = new ArrayList();

		StringBuffer countsql = new StringBuffer(
				"SELECT  count(*) from liquidation  where liquidation.loanId="
						+ loanid + "");
		// 当前日期之前，没有有还款的，且有大于6天的

		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT id, ( SELECT userbasicsinfo.`name` FROM userbasicsinfo WHERE userbasicsinfo.id = liquidation.payeeUserId ), ( SELECT escrow.staff_name FROM escrow WHERE escrow.id = liquidation.payerUserId ), liquidation.orderId, liquidation.amount, liquidation.fee, liquidation.reqTime+'', liquidation.loanId, liquidation.liquidationState FROM liquidation WHERE liquidation.loanId = "
						+ loanid + "");
		// sqlbuffer.append(connectionSql(loan.getCreateTime(),loan.getCreditTime()));
		// countsql.append(connectionSql(loan.getCreateTime(),loan.getCreditTime()));
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);
		return datalist;
	}

	public List getAttachMent(String loanId, String type) {

		String sql = "select at.id,a.originalName,a.attachmentType,a.attachmentName,a.adminuser_id,`at`.attachment_name "
				+ "from attachment a,attachment_type at where a.attachmentType=`at`.id "
				+ " and a.attachmentType="
				+ type
				+ " and a.loansign_id="
				+ loanId;

		List list = dao.findBySql(sql.toString());
		return list;
	}
	/***
	 * 申请活动状态
	 * @param id
	 * @param request
	 * @return
	 */
	public String activityLoansign(String id, HttpServletRequest request) {
		if (StringUtil.isNotBlank(id)) {
			 StringBuffer sql = new StringBuffer("UPDATE loansign SET loansign.activityStatus=1   WHERE loansign.id ="+id);
			// 批量修改
			if (dao.executeSql(sql.toString()) > 0) {
				return "1";
			}
		}
		return "2";
	}
	

	public Object getLoanRecordIsSucceed(String loanId) {
		StringBuffer sql = new StringBuffer("select sum(lr.tenderMoney) from loanrecord lr where lr.subType in (1,2) and isSucceed=1 and lr.loanSign_id="+loanId);
		Object obj = dao.findObjectBySql(sql.toString());
		return obj != null ? Double.valueOf(obj.toString()) : 0;
	}

	/** 查询到平台当前的费用比例 */
	public Costratio queryCostratio() {
		 List<Costratio> list = dao.find("from Costratio");
	        if(list != null && list.size() > 0){
	            return list.get(0);
	        }
	        return null;
	}
}