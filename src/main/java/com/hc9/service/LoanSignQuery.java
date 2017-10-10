package com.hc9.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Attachment;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Loandynamic;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.LoansignType;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.dao.entity.Loansignflow;
import com.hc9.dao.entity.MsgReminder;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.InvestSmsVo;
import com.hc9.model.PageModel;

/**
 * <p>
 * Title:LoanSignQuery
 * </p>
 * <p>
 * Description: 标的通用查询
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 */
@Service
public class LoanSignQuery {

	/**
	 * 通用dao
	 */
	@Resource
	HibernateSupport dao;

	/**
	 * 通用标查询
	 */
	@Autowired
	private LoanSignFund loanSignFund;

	/**
	 * 根据sql语句查询出条数
	 * 
	 * @param sql
	 *            执行的sql语句
	 * @return 执行结果
	 */
	public int queryCount(String sql) {
		Object object = dao.findObjectBySql(sql, null);
		return object != null ? Integer.parseInt(object.toString()) : 0;
	}

	/**
	 * 查询到该标的剩余份数
	 * 
	 * @param loansign
	 *            借款标对象
	 * @return 剩余份数
	 */
	public int queryCopies(Loansign loansign) {
		StringBuffer sb = new StringBuffer(
				"SELECT (ls.issueLoan-(SELECT IFNULL(sum(tenderMoney),0) from loanrecord where isSucceed=1 and  loanSign_id=ls.id))/ls.loanUnit from loansign ls where ls.id=")
				.append(loansign.getId());
		Object obje = dao.findObjectBySql(sb.toString(), null);
		return obje != null ? Integer.parseInt(obje.toString()) : 0;
	}

	/**
	 * 求到该用户这期该收多少钱=所得利息
	 * 
	 * @param repaymentRecord
	 *            还款记录对象
	 * @param tenderMoney
	 *            借款总额
	 * @param loansign
	 *            借款标对象
	 * @param reallyDay
	 *            实际使用天数
	 * @return 所得利息
	 */
	public BigDecimal queryInterest(Repaymentrecord repaymentRecord,
			double tenderMoney, Loansign loansign, Integer reallyDay) {
		BigDecimal interest = new BigDecimal(0.00);
		/*
		 * if (loansign.getLoanType() == Constant.STATUES_ONE &&
		 * loansign.getRefundWay() == Constant.STATUES_ONE) { interest =
		 * Arith.div(repaymentRecord.getPreRepayMoney() tenderMoney,
		 * loansign.getIssueLoan()); } else if (loansign.getLoanType() ==
		 * Constant.STATUES_ONE && loansign.getRefundWay() ==
		 * Constant.STATUES_THERE) { if (repaymentRecord.getRepayState() ==
		 * Constant.STATUES_FIVE) { interest = loanSignFund.advanceInterest(new
		 * BigDecimal( tenderMoney), loansign.getRate(), reallyDay, loansign
		 * .getLoanType()); } else { interest =
		 * loanSignFund.instalmentInterest(new BigDecimal( tenderMoney),
		 * loansign.getRate(), loansign.getMonth(), loansign.getLoanType()); } }
		 * else { interest = loanSignFund.instalmentInterest(new BigDecimal(
		 * tenderMoney), loansign.getRate(), reallyDay, loansign
		 * .getLoanType()); }
		 */
		return interest;
	}

	/**
	 * 求到该用户这期该收多少钱=认购金额+所得利息
	 * 
	 * @param repaymentRecord
	 *            回款记录对象
	 * @param tenderMoney
	 *            借款金额
	 * @param loansign
	 *            借款标对象
	 * @param reallyDay
	 *            实际使用天数
	 * @return 本息
	 */
	public BigDecimal queryMoney(Repaymentrecord repaymentRecord,
			double tenderMoney, Loansign loansign, Integer reallyDay) {
		BigDecimal money = new BigDecimal(0.00);
		/*
		 * if (loansign.getLoanType() == Constant.STATUES_ONE &&
		 * loansign.getRefundWay() == Constant.STATUES_ONE) { money =
		 * loanSignFund.total(new BigDecimal(tenderMoney), loansign.getRate(),
		 * loansign.getMonth()); } else { money = queryInterest(repaymentRecord,
		 * tenderMoney, loansign, reallyDay); if (repaymentRecord.getPeriods()
		 * == loansign.getMonth() || loansign.getLoanType() ==
		 * Constant.STATUES_TWO || loansign.getLoanType() ==
		 * Constant.STATUES_THERE) { money = money.add(new
		 * BigDecimal(tenderMoney)); } }
		 */
		return money;
	}

	/** 根据主键id获取标的对象信息 */
	public Loansign getLoansignById(String id) {
		dao.getSession().clear();
		
		try {
			String sql = "select * from loansign where id=?";
			Loansign loansign = dao.findObjectBySql(sql, Loansign.class, id);
			return loansign;
		} catch (DataAccessException e) {
			return null;
		}
	}
	
//	public Loansign queryLoanSignById(String id) {
//		String hql = " from Loansign loan fetch all properties where loan.id = ? ";
//		Loansign loansign = (Loansign) dao.findObject(hql, id);
//		return loansign;
//	}
	
	/***
	 * 20150731-lkl
	 * 根据loansignId查询发短信人 
	 * @param loansignId
	 * @return
	 */
	public List<MsgReminder>  getListMsgreminder(String loansignId){
		String sql="select * from msgreminder where loansignId=?  order by id desc";
		List<MsgReminder> listMsgReminders=dao.findBySql(sql, MsgReminder.class, loansignId);
		return listMsgReminders;
	}

	/***
	 * 查询Loansign
	 * 
	 * @param id
	 * @return
	 */
	public Loansign getLoansign(String id) {
		try {
			String sql = "select * from loansign where id=?";
			Loansign loansign = dao.findObjectBySql(sql, Loansign.class, id);
			return loansign;
		} catch (DataAccessException e) {
			return null;
		}
	}

	/***
	 * 根据用户Id，项目ID、投标类型
	 * 
	 * @param loansignId
	 * @param userId
	 * @param subType
	 * @return
	 */
	public Loanrecord getLoanRecordID(String loanrecordId) {
		try {
			String sql = "select  * from loanrecord where  id=? ";
			Loanrecord loanrecord = dao.findObjectBySql(sql, Loanrecord.class,
					loanrecordId);
			return loanrecord;
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * 获取最后一期还款日期
	 * 
	 * @param loansignId
	 * @return
	 */
	public String getPreRepayDate(String loansignId) {
		String sql = "select MAX(l.preRepayDate) from repaymentrecord l where l.loanSign_id=? ";
		Object preRepayDate = dao.findObjectBySql(sql, loansignId);
		if (preRepayDate != null) {
			return preRepayDate.toString();
		} else {
			return null;
		}
	}

	/**
	 * <p>
	 * Title: getLoansignById
	 * </p>
	 * <p>
	 * Description: 通过loansign编号查询Loansign的债券转让标
	 * </p>
	 * 
	 * @param loanSignId
	 *            借款编号
	 * @return 借款标对象
	 */
	public Loansign getLoansignIsdetById(String loanSignId) {
		String sql = "select * from loansign where isdet=1 and id="
				+ loanSignId;
		Loansign loan = dao.findObjectBySql(sql, Loansign.class);
		return loan;
	}

	/**
	 * 查询所有的借款标类型
	 * 
	 * @return 借款标集合
	 */
	public List<LoansignType> queryLoanType() {
		List<LoansignType> list = dao.find("from LoansignType");
		return list;
	}

	/**
	 * 统计购买记录
	 * 
	 * @param loanSignId
	 * @return
	 */
	public Integer getBuyCount(String loanSignId) {
		StringBuffer sql = new StringBuffer(
				"select count(id) from (SELECT * from loanrecord l where l.loanSign_id=")
				.append(loanSignId).append(" GROUP BY l.userbasicinfo_id)aa");
		Object count = dao.findObjectBySql(sql.toString()).toString();
		return Integer.parseInt(count.toString());
	}

	/**
	 * 通过借款状态查询
	 * 
	 * @param state
	 * @return
	 */
	public Loansign getLoansignByState(String state) {
		try {
			return dao.get(Loansign.class, Long.valueOf(state));
		} catch (DataAccessException e) {
			return null;
		}
	}

	/**
	 * 查询优金理财计划详情
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Loansign getYouxuan() {
		StringBuffer sb = new StringBuffer(
				"From Loansign l where l.loanType =7 AND l.loanstate =2 ");
		List<Loansign> list = dao.find(sb.toString());
		return list.size() > 0 ? list.get(0) : null;
	}

	/**
	 * 查询优金当前期
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Loansign getyouxuaning() {
		StringBuffer sb = new StringBuffer(
				"From Loansign l where l.loanType =7 AND l.loanstate=2 ");
		List<Loansign> list = dao.find(sb.toString());
		return list.size() > 0 ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public Loansign getyouxuanById(Long loanid) {
		StringBuffer sb = new StringBuffer(
				"From Loansign l where l.loanType =7 AND l.loanstate =2 and l.id=")
				.append(loanid);
		List<Loansign> list = dao.find(sb.toString());
		return list.size() > 0 ? list.get(0) : null;
	}

	/**
	 * <p>
	 * Title: getcreditTime
	 * </p>
	 * <p>
	 * Description: 得到该标的放款时间
	 * </p>
	 * 
	 * @param loansignId
	 *            借款id
	 * @return 放款时间
	 */
	public String getcreditTime(Long loansignId) {
		StringBuffer sb = new StringBuffer(
				"SELECT creditTime from loansignbasics where loansign_id=")
				.append(loansignId);
		return dao.findObjectBySql(sb.toString()).toString();
	}

	/**
	 * <p>
	 * Title: getLoansignbasicsById
	 * </p>
	 * <p>
	 * Description: 通过loansign编号查询Loansignbasics
	 * </p>
	 * 
	 * @param loanSignId
	 *            借款id
	 * @return 借款标基础信息
	 */
	public Loansignbasics getLoansignbasicsById(String loanSignId) {
		StringBuffer sb = new StringBuffer(
				"From Loansignbasics where loansign.id=").append(loanSignId);
		List<Loansignbasics> lsbList = dao.find(sb.toString());
		return lsbList.size() > 0 ? lsbList.get(0) : null;
	}

	/**
	 * <p>
	 * Title: getRepaymentByLSId
	 * </p>
	 * <p>
	 * Description: 通过loansign编号查询回款计划（适用于天标 ，秒标和到期一次性还款）
	 * </p>
	 * 
	 * @param loanSignId
	 *            借款标号
	 * @return 还款计划对象
	 */
	public Repaymentrecord getRepaymentByLSId(String loanSignId) {
		StringBuffer sb = new StringBuffer(
				"From Repaymentrecord where loansign.id=").append(loanSignId);
		List<Repaymentrecord> rmList = dao.find(sb.toString());
		return rmList.size() > 0 ? rmList.get(0) : null;
	};

	public Repaymentrecord getRepaymentByLSIdLast(String loanSignId) {
		StringBuffer sb = new StringBuffer(
				"From Repaymentrecord where loansign.id=").append(loanSignId);
		List<Repaymentrecord> rmList = dao.find(sb.toString());
		return rmList.size() > 0 ? rmList.get(rmList.size() - 1) : null;
	};

	/**
	 * <p>
	 * Title: checkRepayOrder
	 * </p>
	 * <p>
	 * Description: 判断是否按期数还款，若未按期数依次还款，返回true，否则返回false
	 * </p>
	 * 
	 * @param repay
	 *            还款记录
	 * @return 成功或失败
	 */
	public boolean checkRepayOrder(Repaymentrecord repay) {
		StringBuffer sb = new StringBuffer(
				"select count(1) from repaymentrecord where periods<")
				.append(repay.getPeriods())
				.append(" and repayState=1 and  loanSign_id=")
				.append(repay.getLoansign().getId());
		Object object = dao.findObjectBySql(sb.toString()).toString();
		return Integer.parseInt(object.toString()) > 0 ? true : false;
	}

	/**
	 * <p>
	 * Title: isFull
	 * </p>
	 * <p>
	 * Description: 判断该借款标是否满标
	 * </p>
	 * 
	 * @param loansignId
	 *            借款编号
	 * @return boolean
	 */
	public boolean isFull(String loansignId) {
		StringBuffer sb = new StringBuffer(
				"SELECT  ls.remoney FROM loansign ls where  ls.id=")
				.append(loansignId);
		Object object = dao.findObjectBySql(sb.toString());
		return object != null ? (Double.valueOf(object.toString()) == 0 ? true
				: false) : false;
	}

	/**
	 * <p>
	 * Title: isFullAssigment
	 * </p>
	 * <p>
	 * Description: 判断该债权转让标是否满标
	 * </p>
	 * 
	 * @param loansignId
	 *            借款编号
	 * @return boolean
	 */
	public boolean isFullAssigment(String loansignId) {
		StringBuffer sb = new StringBuffer(
				"SELECT  ls.issueLoan-(SELECT SUM(tenderMoney) from  loanrecord where isSucceed=1 and loan_id=ls.id) FROM loansign ls where  ls.id=")
				.append(loansignId);
		Object object = dao.findObjectBySql(sb.toString());
		return object != null ? (Double.valueOf(object.toString()) == 0 ? true
				: false) : false;
	}

	/**
	 * <p>
	 * Title: queryCostratio
	 * </p>
	 * <p>
	 * Description: 查询到平台当前的费用比例
	 * </p>
	 * 
	 * @return Costratio对象
	 */
	public Costratio queryCostratio() {
		List<Costratio> costratioList = (List<Costratio>) dao
				.find("From Costratio");
		return costratioList.size() == 1 ? costratioList.get(0) : null;
	};

	/**
	 * <p>
	 * Title: isExceed
	 * </p>
	 * <p>
	 * Description: 判断该标是否逾期
	 * </p>
	 * 
	 * @param loansignId
	 *            借款编号
	 * @return true or false
	 */
	public boolean isExceed(long loansignId) {
		StringBuffer sb = new StringBuffer(
				"SELECT COUNT(1) from repaymentrecord where (repayState=3||repayState=4) and  loanSign_id=")
				.append(loansignId);
		Object object = dao.findObjectBySql(sb.toString());
		return object != null ? (Integer.parseInt(object.toString()) > 0 ? true
				: false) : false;
	}

	/**
	 * 获取该标的购买金额的总和
	 * 
	 * @param id
	 *            标号
	 * @return 借款金额
	 */
	public Double getLoanrecordMoneySum(Long id) {
		String sql = "select SUM(tenderMoney) from loanrecord where loanSign_id=?";
		Double money = dao.queryNumberSql(sql, id);
		if (null != money) {
			return money;
		}
		return 0.00;
	}

	/**
	 * 根据id获取name
	 * 
	 * @param id
	 * @return
	 */
	public String getName(String id) {
		if (!id.equals("") && id != null) {
			String sql = "select name from userbasicsinfo u where u.id=(select l.user_debt from loansignflow l where l.loan_id="
					+ id + ")";
			Object name = this.dao.findBySql(sql);
			return name.toString();
		} else {
			return null;
		}

	}

	/**
	 * 根据loanId获取Loansignflow数据
	 * 
	 * @param loanId
	 * @return
	 */
	public String getTenderMoney(String id, String userId) {
		if (!id.equals("") && id != null) {
			String sql = "select tenderMoney from loansignflow where loan_id="
					+ id + " and user_debt=" + userId;
			Object tenderMoney = this.dao.findBySql(sql);
			return tenderMoney.toString();
		} else {
			return null;
		}
	}

	/**
	 * 根据loanId获得userinfo的id
	 * 
	 * @param id
	 * @return
	 */
	public String getUserInfo(String id) {
		if (!id.equals("") && id != null) {
			String sql = "select id from userbasicsinfo u where u.id=(select l.user_debt from loansignflow l where l.loan_id="
					+ id + ")";
			Object userId = this.dao.findBySql(sql);
			return userId.toString().substring(1,
					userId.toString().length() - 1);
		} else {
			return null;
		}
	}

	/**
	 * 根据name获得userinfo的id
	 * 
	 * @param id
	 * @return
	 */
	public String getID(String userName) {
		if (!userName.equals("") && userName != null) {
			String sql = "select id from userbasicsinfo  where userName like'%"
					+ userName.toString() + "%'";
			Object id = this.dao.findBySql(sql);
			return id.toString().substring(1, id.toString().length() - 1);
		} else {
			return null;
		}
	}

	/**
	 * 根据name获得userinfo的id
	 * 
	 * @param id
	 * @return
	 */
	public String getnameID(String name) {
		if (!name.equals("") && name != null) {
			String sql = "select id from userbasicsinfo  where name like'%"
					+ name.toString() + "%'";
			Object id = this.dao.findBySql(sql);
			return id.toString().substring(1, id.toString().length() - 1);
		} else {
			return null;
		}
	}

	/**
	 * 判断loansignFlow是否存在
	 * 
	 * @param id
	 * @return
	 */
	public String getLoansignId(String id, String userId) {
		if (!id.equals("") && id != null) {
			String sql = "select loansign_id from loansignflow where loan_id="
					+ id + " and user_debt=" + userId;
			Object loansign_id = this.dao.findBySql(sql);
			return loansign_id.toString().substring(1,
					loansign_id.toString().length() - 1);
		} else {
			return null;
		}
	}

	/**
	 * 获得原标Id
	 * 
	 * @param loanSignId
	 * @return
	 */
	public String getLoanId(String loanSignId) {
		if (!loanSignId.equals("") && loanSignId != null) {
			String sql = "select loan_id from loansignflow where loansign_id="
					+ loanSignId;
			Object loan_id = this.dao.findBySql(sql);
			return loan_id.toString().substring(1,
					loan_id.toString().length() - 1);
		} else {
			return null;
		}
	}

	/**
	 * 获得原标Id的用户ID
	 * 
	 * @param loanSignId
	 * @return
	 */
	public String getUserDebt(String loanSignId) {
		if (!loanSignId.equals("") && loanSignId != null) {
			String sql = "select user_debt from loansignflow where loansign_id="
					+ loanSignId;
			Object user_debt = this.dao.findBySql(sql);
			return user_debt.toString().substring(1,
					user_debt.toString().length() - 1);
		} else {
			return null;
		}
	}

	/**
	 * 根据转让标的ID查询到原标的用户
	 */
	public Loansignflow getLoanUserId(String loan_id) {
		String sql = "select * from loansignflow where loanSign_id=" + loan_id;
		Loansignflow flow = dao.findObjectBySql(sql, Loansignflow.class);
		return flow;
	}

	/**
	 * 获得原标Id的pMerBillNo
	 * 
	 * @param loanSignId
	 * @return
	 */
	public String getpMerBillNo(String loanSignId, String userId) {
		if (!loanSignId.equals("") && loanSignId != null) {
			String sql = "select pMerBillNo from loanrecord where loanSign_id ="
					+ loanSignId + "  and userbasicinfo_id=" + userId;
			Object pMerBillNo = this.dao.findBySql(sql);
			return pMerBillNo.toString().substring(1,
					pMerBillNo.toString().length() - 1);
		} else {
			return null;
		}
	}

	/**
	 * 获得loansignflow
	 * 
	 * @param Id
	 * @return
	 */
	public String getflowId(String loanSignId, String userId) {
		if (!loanSignId.equals("") && loanSignId != null) {
			String sql = "select flowid from loansignflow where loanSign_id="
					+ loanSignId + "  and user_debt=" + userId;
			Object flowid = this.dao.findBySql(sql);
			return flowid.toString().substring(1,
					flowid.toString().length() - 1);
		} else {
			return null;
		}
	}

	/**
	 * 通过flowid
	 * 
	 * @param flowid
	 * @return
	 */
	public Loansignflow getLoansignflow(String flowid) {
		try {
			return dao.get(Loansignflow.class, Long.valueOf(flowid));
		} catch (DataAccessException e) {
			return null;
		}
	}

	/**
	 * 获得loansignflow
	 * 
	 * @param Id
	 * @return
	 */
	public String getflowId(String loanSignId) {
		if (!loanSignId.equals("") && loanSignId != null) {
			String sql = "select flowid from loansignflow where loanSign_id="
					+ loanSignId;
			Object flowid = this.dao.findBySql(sql);
			return flowid.toString().substring(1,
					flowid.toString().length() - 1);
		} else {
			return null;
		}
	}

	public void updateflowId(Loansignflow loansignflow) {
		dao.update(loansignflow);
	}

	/**
	 * 更新投资记录
	 * 
	 * @param loanrecord
	 */
	public void updateLoanRecord(Loanrecord loanrecord, Loansign loansign) {
		dao.update(loanrecord);
		dao.update(loansign);
	}

	/**
	 * 更新投资记录
	 * 
	 * @param loanrecord
	 */
	public void saveOrUpdateLoanRecord(Loanrecord loanrecord, Loansign loansign) {
		dao.getSession().clear();
		dao.save(loanrecord);
		dao.update(loansign);
	}

	public void saveRecord(Loanrecord loanrecord) {
		dao.save(loanrecord);
	}

	/**
	 * 获得loanrecord
	 * 
	 * @param Id
	 * @return
	 */
	public String getloanRecordId(Loansignflow loansignflow) {
		if (!loansignflow.getId().equals("") && loansignflow.getId() != null) {
			String sql = "select id from loanrecord where loanSign_id ="
					+ loansignflow.getLoanId() + " and userbasicinfo_id ="
					+ loansignflow.getUserDebt();
			Object id = this.dao.findBySql(sql);
			return id.toString().substring(1, id.toString().length() - 1);
		} else {
			return null;
		}
	}

	/**
	 * 叠加投资
	 * 
	 * @param Id
	 * @param userId
	 * @param loanType
	 * @return
	 */
	public Loanrecord getLoanrecordIds(String Id, Long userId, Integer loanType) {
		String sql = "select * from loanrecord where loanSign_id=" + Id
				+ " and userbasicinfo_id=" + userId + " and loanType="
				+ loanType;
		Loanrecord loan = dao.findObjectBySql(sql, Loanrecord.class);
		return loan;
	}

	/**
	 * 通过id
	 * 
	 * @param id
	 * @return
	 */
	public Loanrecord getLoanRecord(String id) {
		try {
			return dao.get(Loanrecord.class, Long.valueOf(id));
		} catch (DataAccessException e) {
			return null;
		}
	}

	/**
	 * ordernum查loanrecord
	 * 
	 * @param id
	 * @return
	 */
	public Loanrecord getLoanRecordbyOrdernum(String ordernum) {
		try {
			String sql = "SELECT * FROM loanrecord WHERE order_id=?";
			return dao.findObjectBySql(sql, Loanrecord.class, ordernum);
		} catch (DataAccessException e) {
			return null;
		}
	}

	/**
	 * 通过id
	 * 
	 * @param id
	 * @return
	 */
	public Userbasicsinfo getUserbasicsinfo(String id) {
		try {
			return dao.get(Userbasicsinfo.class, Long.valueOf(id));
		} catch (DataAccessException e) {
			return null;
		}
	}

	/**
	 * 查询记录
	 * 
	 * @return
	 */
	public Loanrecord getLoanrecordAssignment(String id, String userID) {
		String hql = "from Loanrecord l where l.isSucceed=1 and loansign.id="
				+ id.toString() + " and userbasicsinfo.id=" + userID.toString();
		List ls = dao.find(hql);
		if (ls.size() > 0) {
			Loanrecord loanrecord = (Loanrecord) ls.get(0);
			return loanrecord;
		} else {
			return null;
		}
	}

	/**
	 * 查询记录
	 * 
	 * @return
	 */
	public Loanrecord getLoanrecordLoanID(String id) {
		String hql = "from Loanrecord l where l.isSucceed=1 and l.loanId="
				+ id.toString();
		List ls = dao.find(hql);
		if (ls.size() > 0) {
			Loanrecord loanrecord = (Loanrecord) ls.get(0);
			return loanrecord;
		} else {
			return null;
		}
	}

	/**
	 * 投资记录
	 * 
	 * @param uId
	 * @return
	 */
	public PageModel getloanrecord(HttpServletRequest request, PageModel page,
			Integer state, String beginTime, String endTime, Integer timeno,
			Userbasicsinfo user) {
		page.setNumPerPage(5);

		String status = "";
		if (state != null) {
			if (state == 1) {
//				sql.append(" and ls.status in (1,2,3,4,5) ");
//				sqlCount.append(" and ls.status in (1,2,3,4,5)");
				status = " and ls.status in (1,2,3,4,5)";
			} else if (state == 2) {
//				sql.append(" and ls.status in (6,7) ");
//				sqlCount.append(" and ls.status in (6,7)");
				status = " and ls.status in (6,7)";
			} else if (state == 3) {
//				sql.append(" and  ls.status=8 ");
//				sqlCount.append(" and  ls.status=8 ");
				status = " and  ls.status=8 ";
			}
		} else {
//			sql.append(" and ls.state = 2 ");
//			sql.append(" and ls.status in (1,2,3,4,5,6,7,8) ");
//			sqlCount.append(" and ls.state = 2 ");
//			sqlCount.append(" and ls.status in (1,2,3,4,5,6,7,8)");
			status = " and ls.state = 2 and ls.status in (1,2,3,4,5,6,7,8)";
		}
		
		StringBuffer sql = new StringBuffer("select ls.id,ls.name,lr.tenderMoney,");
		sql.append("lr.tenderTime,lr.subType,ls.prio_rate,lr.isSucceed,ls.prio_aword_rate,lr.pContractNo,");
		sql.append("IF(ls.status = 8,(select sum(ifnull(rmp.realMoney,0)+IFNULL(rmp.middleRealMoney,0)+IFNULL(rmp.afterRealMoney,0))-lr.tenderMoney");
		sql.append(" from repaymentrecordparticulars rmp where rmp.loanrecordId=lr.id and rmp.repState = 1), ");
		sql.append(" IF(loansignType_id = 5,(select sum(ifnull(rmp.realMoney,0)-ifnull(rmp.money,0)) from repaymentrecordparticulars rmp where rmp.loanrecordId=lr.id and rmp.repState = 1),");
		sql.append(" (select sum(ifnull(rmp.realMoney,0)+IFNULL(rmp.middleRealMoney,0)+IFNULL(rmp.afterRealMoney,0))");
		sql.append("from repaymentrecordparticulars rmp where rmp.loanrecordId=lr.id and rmp.repState = 1)))+");
		sql.append("IFNULL((select incomeMoney from voteincome where loanRecordId=lr.id and status = 1),0)");
		sql.append(",IFNULL(lr.id,0),IF(lr.redEnvelopeMoney > 0,lr.redEnvelopeMoney,''), ");
		sql.append("(select interestRate from interestincreasecard where loanrecordId = lr.id LIMIT 1),ls.loansignType_id");
		sql.append(",ls.status,ls.type,ls.remonth,ls.credit_time,DATE_ADD(STR_TO_DATE(ls.credit_time,'%Y-%m-%d %H:%i:%s'),INTERVAL ls.remonth month) AS repayment_time");
		sql.append(",(SELECT sum(IFNULL(rmp.preRepayMoney,0)+IFNULL(rmp.middlePreRepayMoney,0)+IFNULL(rmp.afterPreRepayMoney,0)) from repaymentrecordparticulars rmp,repaymentrecord rp where rmp.repaymentrecordId=rp.id and rmp.loanrecordId=lr.id )");
		sql.append("+IFNULL((select incomeMoney from voteincome where loanRecordId=lr.id),0)");
		sql.append(",t.number");
		sql.append(",DATE_ADD(STR_TO_DATE(ls.credit_time,'%Y-%m-%d %H:%i:%s'),INTERVAL ls.remonth day) AS repayment_add_time");
		sql.append(" from loansign ls ,loanrecord lr ");
		sql.append(" ,(select id,number,tenderTime from (select ls.id,count(*) number,MAX(lr.tenderTime) tenderTime from loansign ls ,loanrecord lr where  lr.loanSign_id=ls.id and lr.isSucceed=1 and lr.userbasicinfo_id=");
		sql.append(user.getId());
		sql.append(" and ls.state = 2 ");
		sql.append(status);
		sql.append(" group by ls.id order by  lr.tenderTime desc LIMIT ");
		sql.append((page.getPageNum() - Constant.STATUES_ONE)* page.getNumPerPage());
		sql.append(",");
		sql.append(page.getNumPerPage());
		sql.append(" ) tt) t");
		sql.append(" where lr.loanSign_id=ls.id and lr.isSucceed=1 and lr.userbasicinfo_id="+user.getId());
		sql.append(" and t.id =ls.id");

//		StringBuffer sqlCount = new StringBuffer(
//				"select count(ls.id) from loansign ls ,loanrecord lr "
//						+ "where  lr.loanSign_id=ls.id and lr.isSucceed=1 and lr.userbasicinfo_id=");
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("select count(id) from (");
		sqlCount.append("select ls.id from loansign ls ,loanrecord lr ");
		sqlCount.append("where  lr.loanSign_id=ls.id and lr.isSucceed=1 and lr.userbasicinfo_id=");
		sqlCount.append(user.getId());
		
		sql.append(status);
		sqlCount.append(status);

		if (StringUtil.isNotBlank(beginTime)) {
			sql.append(" and DATE_FORMAT(lr.tenderTime,'%y-%m-%d') >= ").
			append("DATE_FORMAT('"+beginTime).append("','%y-%m-%d')");
			sqlCount.append(" and DATE_FORMAT(lr.tenderTime,'%y-%m-%d') >= ").
			append("DATE_FORMAT('"+beginTime).append("','%y-%m-%d')");
		}

		if (StringUtil.isNotBlank(endTime)) {
			sql.append(" and DATE_FORMAT(lr.tenderTime,'%y-%m-%d') <= ").
			append("DATE_FORMAT('"+endTime).append("','%y-%m-%d')");
			sqlCount.append(" and DATE_FORMAT(lr.tenderTime,'%y-%m-%d') <= ").
			append("DATE_FORMAT('"+endTime).append("','%y-%m-%d')");
		}

		if (timeno != null && !"".equals(timeno)) {
			sql.append(" and DATE_SUB(CURDATE(), INTERVAL " + timeno
					+ " MONTH) <= DATE_FORMAT(lr.tenderTime,'%y-%m-%d')");
			sqlCount.append(" and DATE_SUB(CURDATE(), INTERVAL " + timeno
					+ " MONTH) <= DATE_FORMAT(lr.tenderTime,'%y-%m-%d')");
		}
		sqlCount.append(" group by ls.id");
		sqlCount.append(") tt");

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量
		sql.append("  order by t.tenderTime desc,ls.id desc, lr.tenderTime desc  ");
//		sql.append("  LIMIT ")
//				.append((page.getPageNum() - Constant.STATUES_ONE)
//						* page.getNumPerPage()).append(",")
//				.append(page.getNumPerPage());

		List<Loanrecord> list = dao.findBySql(sql.toString());
		page.setList(list);// Loansign集合
		return page;
	}

	/**
	 * H5投资记录
	 * 
	 * @param uId
	 * @return
	 */
	public PageModel getloanrecord(Long userId, Integer state, int all, PageModel page) {
		
		StringBuffer sql = new StringBuffer("select ls.id,ls.name,lr.tenderMoney,lr.tenderTime,lr.subType,ls.prio_rate,ls.prio_aword_rate,ls.type,ls.remonth ");
		sql.append(",(SELECT sum(IFNULL(rmp.realMoney,0)+IFNULL(rmp.middleRealMoney,0)+IFNULL(rmp.afterRealMoney,0)) from repaymentrecordparticulars rmp,repaymentrecord rp where rmp.repaymentrecordId=rp.id and rmp.loanrecordId=lr.id and rmp.repState = 1 )");
		sql.append("+IFNULL((select incomeMoney from voteincome where loanRecordId=lr.id and status = 1),0)");
		sql.append(",IFNULL(ii.interestRate,0) ");
		sql.append(" from loansign ls ,loanrecord lr ");
		sql.append("LEFT JOIN interestincreasecard ii ON ii.loanrecordId = lr.id AND ii.useFlag = 1 ");
		sql.append("where lr.loanSign_id=ls.id and lr.isSucceed=1 and lr.userbasicinfo_id="+userId);

		StringBuffer sqlCount = new StringBuffer("select count(1) ");
		sqlCount.append(" from loansign ls ,loanrecord lr where lr.loanSign_id=ls.id and lr.isSucceed=1 and lr.userbasicinfo_id="+userId);
		
		
		if (state == 1) {
			sql.append(" and ls.status in (1,2,3,4,5) ");
			sqlCount.append(" and ls.status in (1,2,3,4,5) ");
		} else if (state == 2) {
			sql.append(" and ls.status in (6,7) ");
			sqlCount.append(" and ls.status in (6,7) ");
		} else if (state == 3) {
			sql.append(" and  ls.status=8 ");
			sqlCount.append(" and  ls.status=8 ");
		}
		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		sql.append("  order by  lr.tenderTime desc  ");
		if(all == 0){
			sql.append(" limit 0,11");
		}else{
			sql.append(" LIMIT ");
			sql.append((page.getPageNum() - Constant.STATUES_ONE) * page.getNumPerPage());
			sql.append(",");
			sql.append(page.getNumPerPage());
		}
		List list = dao.findBySql(sql.toString() );
		page.setList(list);// project集合
		return page;
	}

	/**
	 * 债权投资记录
	 * 
	 * @param uId
	 * @return
	 */
	public PageModel getloanrecord2(HttpServletRequest request, PageModel page,
			String Id) {

		StringBuffer sql = new StringBuffer(
				"select * from loanrecord where loanSign_id=" + Id);

		StringBuffer sqlCount = new StringBuffer(
				"select count(id) from loanrecord where loanSign_id=");
		sqlCount.append(Id);

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		int num = dao.queryNumberSql(sqlCount.toString()).intValue();
		System.out.println(num);
		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());

		List<Loanrecord> list = dao.findBySql(sql.toString(), Loanrecord.class);
		page.setList(list);// Loansign集合
		return page;
	}

	// /**
	// * 积分统计
	// * @param userId
	// * @return
	// */
	// public List getAutointegral(String userId){
	// String
	// sql="select sum(realityintegral) from autointegral where user_id="+userId;
	// List jifen=dao.findBySql(sql);
	// return jifen;
	// }

	/**
	 * 项目动态
	 * 
	 * @param uId
	 * @return
	 */
	public PageModel getLoandynamic(HttpServletRequest request, PageModel page) {

		StringBuffer sql = new StringBuffer(
				"select d.title, ls.name, d.remark , d.publishTime,u.userName from  loandynamic d,loansign ls, userbasicsinfo u where d.user_id=u.id and d.loan_id=ls.id ORDER BY d.publishTime desc ");

		StringBuffer sqlCount = new StringBuffer(
				"select count(d.id) from  loandynamic d,loansign ls, userbasicsinfo u where d.user_id=u.id and d.loan_id=ls.id");

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.STATUES_TWO).append(",")
				.append(page.getNumPerPage());

		List<Loandynamic> list = dao.findBySql(sql.toString());
		page.setList(list);// Loansign集合
		return page;
	}

	public List getloanSignListByUid(String uId) {

		String sql = "select ls.id,sum(lr.tenderMoney),ls.`name`, ls.remoney,ls.loanType,ls.status,lr.tenderTime,lr.loanType "
				+ "from loansign ls ,loanrecord lr,userbasicsinfo u where lr.userbasicinfo_id=u.id and lr.loanSign_id=ls.id and ls.status=1 and u.id=? GROUP BY ls.id";

		List list = dao.findBySql(sql, uId);
		return list.size() > 0 ? list : null;
	}

	public List getloanListByUid(String uId) {

		String sql = "select ls.id,sum(lr.tenderMoney),ls.`name`,ls.remoney,ls.loanType,ls.status,lr.tenderTime "
				+ "from loansign ls ,loanrecord lr,userbasicsinfo u where lr.userbasicinfo_id=u.id and lr.loanSign_id=ls.id and ls.status=3 and u.id=? GROUP BY ls.id";

		List list = dao.findBySql(sql, uId);
		return list.size() > 0 ? list : null;
	}

	public List getListByUid(String uId) {

		String sql = "select ls.id,sum(lr.tenderMoney),ls.`name`,ls.remoney ,ls.loanType,ls.status,lr.tenderTime"
				+ "from loansign ls ,loanrecord lr,userbasicsinfo u where lr.userbasicinfo_id=u.id and lr.loanSign_id=ls.id and ls.status=4 and u.id=? GROUP BY ls.id";

		List list = dao.findBySql(sql, uId);
		return list.size() > 0 ? list : null;
	}

	/***
	 * 修改loansign表
	 * 
	 * @param loansign
	 */
	public void updateLoansign(Loansign loansign) {
		dao.update(loansign);
	}

	/**
	 * 删除loanrecord
	 * 
	 * @param loanrecord
	 */
	public void deleteLoanrecord(Loanrecord loanrecord) {
		dao.delete(loanrecord);
	}

	/**
	 * 
	 * 根据还款计划任务表ID 查询还款记录详情
	 * 
	 * @param id
	 * @return
	 */
	public Repaymentrecord getrepaymentrecordByid(Long id) {
		String sql = "SELECT * from repaymentrecord where id=?";
		List<Repaymentrecord> list = dao.findBySql(sql, Repaymentrecord.class,
				id);
		return list.size() > 0 ? list.get(list.size() - 1) : null;
	}

	/****
	 * 查询购买成功的
	 * 
	 * @param shopId
	 * @return
	 */
	public Double getSumLoanTenderMoney(String loansignId) {
		if (StringUtil.isNotBlank(loansignId)) {
			String sql = "select sum(tenderMoney) from loanrecord where loanSign_id=? and isSucceed=1";
			Object tenderMoney = dao.findObjectBySql(sql, loansignId);
			if (tenderMoney != null) {
				return Double.valueOf(tenderMoney.toString());
			} else {
				return 0.00;
			}
		} else {
			return 0.00;
		}
	}

	/***
	 * 查询待确认的loanrecord
	 * 
	 * @return
	 */
	public List<Loanrecord> getLoanRecordList() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = "select * from loanrecord where isSucceed=0 and DATE_ADD(tenderTime, INTERVAL 10 MINUTE)<?";
		List<Loanrecord> listLoanrecord = dao.findBySql(sql, Loanrecord.class,
				df.format(new Date()));
		return listLoanrecord;
	}

	/***
	 * 根据loanId查询投标成功的
	 * 
	 * @param id
	 * @return
	 */
	public List<Loanrecord> selLoanrecordList(Long loanId) {
		String sql = "select * from loanrecord l,loansign s  where l.loanSign_id=s.id and isSucceed=1 and l.loanSign_id=?";
		List<Loanrecord> listLoanrecord = dao.findBySql(sql, Loanrecord.class,
				loanId);
		return listLoanrecord;
	}

	/***
	 * 根据loanID删除repaymentrecord
	 * 
	 * @param loansign
	 */
	public void deleteRepaymentrecord(Loansign loansign) {
		String sql = "delete from repaymentrecord where loanSign_id=?";
		dao.executeSql(sql, loansign.getId());
	}

	/**
	 * 收益明细-投资记录 
	 */
	@SuppressWarnings("rawtypes")
	public PageModel getLoanIncome(PageModel page, Long userId,
			String beginTime, String endTime, Integer timeno) {

		StringBuffer sql = new StringBuffer(
				"SELECT ls.name,lr.subType,lr.tenderMoney,lr.tenderTime,lr.order_id,lr.id,ifnull(ls.id,0),ls.loansignType_id,ls.type,ls.remonth,"
						+ "(select rr.preRepayDate from repaymentrecordparticulars rp,repaymentrecord rr where rp.repaymentrecordId=rr.id and rp.loanrecordId=lr.id order by rr.periods desc limit 0,1)"
						+ " from loanrecord lr,loansign ls "
						+ " where lr.userbasicinfo_id=? and lr.isSucceed=1 and lr.loanSign_id = ls.id and ls.status >= 6 and ls.status <= 8 ");
		StringBuffer sqlCount = new StringBuffer(
				"SELECT count(1) from loanrecord lr,loansign ls "
						+ " where lr.userbasicinfo_id=?  and lr.isSucceed=1 and lr.loanSign_id = ls.id and ls.status >= 6 and ls.status <= 8 ");

		if (beginTime != null) {
			sql.append(" and lr.tenderTime >= '").append(beginTime)
					.append(" ' ");
			sqlCount.append(" and lr.tenderTime >= '").append(beginTime)
					.append(" ' ");
		}

		if (endTime != null) {
			sql.append(" and lr.tenderTime <= '").append(endTime)
					.append(" ' ");
			sqlCount.append(" and lr.tenderTime <= '").append(endTime)
					.append(" ' ");
		}
		if (timeno != null) {
			sql.append(" and lr.tenderTime<=now() and lr.tenderTime>=DATE_SUB(now(),INTERVAL "+timeno+" MONTH) ");
			sqlCount.append(" and lr.tenderTime<=now() and lr.tenderTime>=DATE_SUB(now(),INTERVAL "+timeno+" MONTH) ");
		}

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString(), userId)
				.intValue());
		sql.append(" order by lr.tenderTime desc LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());
		List list = dao.findBySql(sql.toString(), userId);
		page.setList(list);
		return page;
	}
	
	// 通过投资信息查询还款信息
	public List findDetailBylrId(Long lrId) {
		StringBuffer buf = new StringBuffer("SELECT rmp.id,rp.periods, ");
		buf.append("(IFNULL(rmp.money,0)+IFNULL(rmp.middleMoney,0)+IFNULL(rmp.afterMoney,0)),");
		buf.append("(IFNULL(rmp.preRepayMoney,0)+IFNULL(rmp.middlePreRepayMoney,0)+IFNULL(rmp.afterPreRepayMoney,0)),");
		buf.append("rp.preRepayDate,");
		buf.append("(IFNULL(rmp.realMoney,0)+IFNULL(rmp.middleRealMoney,0)+IFNULL(rmp.afterRealMoney,0)),rmp.repState, ");
		buf.append("(select incomeMoney from voteincome where loanRecordId=lr.id),(select incomeMoney from voteincome where loanRecordId=lr.id and status = 1) ");
		buf.append(",ls.prio_rate,ls.prio_aword_rate,(select interestRate from interestincreasecard where loanrecordId = lr.id) ");
		buf.append("from loanrecord lr,repaymentrecordparticulars rmp,repaymentrecord rp,loansign ls ");
		buf.append("where rmp.repaymentrecordId=rp.id and rmp.loanrecordId=lr.id and lr.id=? and lr.loanSign_id=ls.id order by rp.preRepayDate desc ");
		List list = dao.findBySql(buf.toString(), lrId);
		return list;
	}
	
	// 查询未还款的记录总数
	public Integer notRepaymentCount(Long lrId) {
		StringBuffer buf = new StringBuffer("SELECT count(1) "); 
		buf.append("from loanrecord lr,repaymentrecordparticulars rmp,repaymentrecord rp ");
		buf.append("where rmp.repaymentrecordId=rp.id and rmp.loanrecordId=lr.id and lr.id=? and repState<>1 ");
		Object obj = dao.findObjectBySql(buf.toString(), lrId);
		return obj != null ? Integer.valueOf(obj.toString()) : 0;
	}
	
	/**
	 * 查询推荐首页的数据
	 * @param loanrecordId
	 * @return
	 */
	public boolean hasOnIndex() {
		String sql = "select count(id) from loansign where OnIndex=1 LIMIT 0,8";
		Object obj = dao.findObjectBySql(sql.toString());
		return Integer.valueOf(obj.toString()) < 8 ? true : false;
	}
	
	/**
	 * 查询热门推荐的数据
	 * @param loanrecordId
	 * @return
	 */
	public boolean hasRecommend(){
		String sql = "select count(id) from loansign where recommend=1 LIMIT 0,2";
		Object obj = dao.findObjectBySql(sql.toString());
		return Integer.valueOf(obj.toString()) < 2 ? true : false;
	}
	
	/** 根据项目基本信息id复制项目信息 */
	public void copyLoansingById(String loanSignId, HttpServletRequest request) {
		Loansign oldLoansign = getLoansignById(loanSignId);
		Loansignbasics oldLoansignbasics = getLoansignbasicsById(loanSignId);
		
		Loansign newLoansign = copyLoanSign(oldLoansign);
		Loansignbasics newLoansignbasics = copyLoansignbasics(oldLoansignbasics);
		
		dao.save(newLoansign);
		dao.save(newLoansignbasics);
		
//		Serializable seria = dao.save(newLoansign);
//		Long myId = newLoansign.getId();
//		newLoansign.setId(Long.valueOf(seria.toString()));
//		newLoansignbasics.setId(Long.valueOf(seria.toString()));
//		dao.flush();
//		dao.save(newLoansignbasics);
//		dao.flush();
		
		copyMsgReminderList(loanSignId, newLoansign.getId(), request);
		
		/** 复制附件相关信息 */
		copyAttchmentList(loanSignId, newLoansign);
		
		/** 保存复制时原始项目所使用的标题 */
		String loansignName = oldLoansign.getName();
		String key = "STR:LOANSIGN:COPY:NAME:" + newLoansign.getId();
		RedisHelper.setWithExpireTime(key, loansignName, 30 * 24 * 60 * 60);//30天过期
		request.setAttribute("loansign", newLoansign);
		request.setAttribute("loansignbasics", newLoansignbasics);
	}
	
	/** 复制项目信息并生成一个新的项目对象 */
	private Loansign copyLoanSign(Loansign oldLoansign) {
		Loansign newLoansign = new Loansign();
		newLoansign.setCus_id(StringUtil.getCurId(2)); // 根据类型生成宝付Id);
		newLoansign.setUserbasicsinfo(oldLoansign.getUserbasicsinfo());
		newLoansign.setLoansignType(oldLoansign.getLoansignType());
		newLoansign.setName(oldLoansign.getName() + "_复制");
		newLoansign.setIssueLoan(oldLoansign.getIssueLoan());
		newLoansign.setPriority(oldLoansign.getPriority());
		newLoansign.setMiddle(oldLoansign.getMiddle());
		newLoansign.setAfter(oldLoansign.getAfter());
		newLoansign.setLoanUnit(oldLoansign.getLoanUnit());
		newLoansign.setType(oldLoansign.getType());
		newLoansign.setPrioRate(oldLoansign.getPrioRate());
		newLoansign.setPrioAwordRate(oldLoansign.getPrioAwordRate());
		newLoansign.setAfterRate(oldLoansign.getAfterRate());
		newLoansign.setRemonth(oldLoansign.getRemonth());
		newLoansign.setOutDay(oldLoansign.getOutDay());
		newLoansign.setRefunway(oldLoansign.getRefunway());
		newLoansign.setEscrow(oldLoansign.getEscrow());
		newLoansign.setValidity(oldLoansign.getValidity());
		newLoansign.setLoansignType(oldLoansign.getLoansignType());
		newLoansign.setProjectNumber(oldLoansign.getProjectNumber());
		newLoansign.setLoanPeriods(oldLoansign.getLoanPeriods());
		newLoansign.setActivityStatus(oldLoansign.getActivityStatus());
		
		newLoansign.setState(1);
		newLoansign.setStatus(0);
		newLoansign.setIsdet(0);
		newLoansign.setRecommend(0);
		newLoansign.setOnIndex(0);
		newLoansign.setFeeState(2); // 未收取平台手续费
		newLoansign.setCreateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		return newLoansign;
	}
	
	/** 复制项目基本信息并生成一个新的项目基本信息对象 */
	private Loansignbasics copyLoansignbasics(Loansignbasics oldLoansignbasics) {
		Loansignbasics newLoansignbasics = new Loansignbasics();
		newLoansignbasics.setEnteruptime1(oldLoansignbasics.getEnteruptime1());
		newLoansignbasics.setEnteruptime2(oldLoansignbasics.getEnteruptime2());
		newLoansignbasics.setProvince(oldLoansignbasics.getProvince());
		newLoansignbasics.setCity(oldLoansignbasics.getCity());
		newLoansignbasics.setAddress(oldLoansignbasics.getAddress());
		newLoansignbasics.setBehoof(oldLoansignbasics.getBehoof());
		newLoansignbasics.setRemark(oldLoansignbasics.getRemark());
		newLoansignbasics.setHistory(oldLoansignbasics.getHistory());
		newLoansignbasics.setFuturePlan(oldLoansignbasics.getFuturePlan());
		return newLoansignbasics;
	}
	
	/** 复制联系人信息 */
	private void copyMsgReminderList(String loanSignId, Long newId, HttpServletRequest request) {
		String sql = "From MsgReminder where status=1 and loansignId=" + loanSignId;
		List<MsgReminder> list = dao.find(sql);
		List<MsgReminder> employeeReminderList = new ArrayList<MsgReminder>();
		if(list != null && list.size() > 0) {
			List<MsgReminder> voList = new ArrayList<MsgReminder>();
			MsgReminder customerReminder = new MsgReminder();
			for(MsgReminder vo : list) {
				MsgReminder newVo = new MsgReminder();
				newVo.setLoansignId(newId);
				newVo.setReceiverName(vo.getReceiverName());
				newVo.setReceiverPhone(vo.getReceiverPhone());
				newVo.setReceiverEmail(vo.getReceiverEmail());
				newVo.setType(vo.getType());
				newVo.setStatus(vo.getStatus());
				newVo.setCreateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
				voList.add(newVo);
				if(newVo.getType() == 1) {//客户
					customerReminder = newVo;
				} else {
					employeeReminderList.add(newVo);
				}
				request.setAttribute("customerReminder", customerReminder);
				request.setAttribute("employeeReminderList", employeeReminderList);
			}
			dao.saveOrUpdateAll(voList);
		}
	}
	
	/** 复制附件相关信息 */
	private void copyAttchmentList(String loanSignId, Loansign newLoansign) {
		List<Attachment> resultList = new ArrayList<Attachment>();
		String sql = "select  * from attachment where loansign_id=?";
		List<Attachment> list = dao.findBySql(sql, Attachment.class, loanSignId);
		if(list != null && list.size() > 0) {
			for(Attachment vo : list) {
				Attachment newVo = new Attachment();
				newVo.setOriginalName(vo.getOriginalName());
				newVo.setAttachmentName(vo.getAttachmentName());
				newVo.setAttachmentType(vo.getAttachmentType());
				newVo.setUploadTime(vo.getUploadTime());
				newVo.setAdminuser(vo.getAdminuser());
				newVo.setLoansign(newLoansign);
				resultList.add(newVo);
			}
			dao.saveOrUpdateAll(resultList);
		}
	}
	
	/** 根据项目id获取消息接收人相关列表 */
	@SuppressWarnings("unchecked")
	public List<MsgReminder> queryMsgReminderListByLoanSignId(Long loansignId, Integer type) {
		String sql = "From MsgReminder where status=1 and loansignId=" + loansignId + " and type=" + type;
		List<MsgReminder> list = dao.find(sql);
		return list;
	}
	
	/**
	 * 根据标Id查询投资人
	 * @param loanId
	 * @return
	 */
	public  List<InvestSmsVo> queryMsgUser(Long loanId){
		List<InvestSmsVo> userList = new ArrayList<InvestSmsVo>();
		String sql = "select u.id, u.name, r.phone, r.email, t.tenderMoney from " + 
				"(select sum(tenderMoney) as tenderMoney,userbasicinfo_id " + 
				"from loanrecord where loanSign_id=? and isSucceed=1 group by userbasicinfo_id) t, " + 
				"userbasicsinfo u, userrelationinfo r " + 
				"where t.userbasicinfo_id=u.id and u.id=r.user_id";
		List list = dao.findBySql(sql, loanId);
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				InvestSmsVo vo = new InvestSmsVo();
				vo.setUserId(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setCustomerName(StatisticsUtil.getStringFromObject(arr[1]));
				vo.setMobilePhone(StatisticsUtil.getStringFromObject(arr[2]));
				vo.setEmail(StatisticsUtil.getStringFromObject(arr[3]));
				vo.setTenderMoney(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)arr[4]));
				userList.add(vo);
			}
		}
		return userList;
	}
	
	
	/***
	 * 查询标的还款日期
	 * @param loanId
	 * @return
	 */
	public  String queryPreRepayDate(Long loanId){
		String sql="select MAX(preRepayDate) from repaymentrecord where loanSign_id=?";
		Object preRepayDate=dao.findObjectBySql(sql, loanId);
		return preRepayDate.toString();
	}
	
	
	/***
	 * 查询是否发送过红包
	 * @param loansign
	 * @return
	 */
	public  boolean queryRed(Loansign loansign){
		String sql="select * from accountinfo where loansign_id =?   and userbasic_id=?  and accounttype_id=22";
		List<Accountinfo>  list=dao.findBySql(sql, Accountinfo.class,loansign.getId(),loansign.getUserbasicsinfo().getId());
		if(list.size()>0){
			return false;
		}else{
			return true;
		}
	}
}
