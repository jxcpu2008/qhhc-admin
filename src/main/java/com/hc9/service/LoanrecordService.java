package com.hc9.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.hc9.common.util.Arith;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.model.PageModel;

/**
 * 认购记录service
 * 
 * @author Administrator
 * 
 */
@Service
public class LoanrecordService {
	/** dao */
	@Resource
	private HibernateSupportTemplate dao;

	/** loanSignQuery */
	@Resource
	private LoanSignQuery loanSignQuery;

	/**
	 * <p>
	 * Description: 用于支持投标管理投标记录
	 * </p>
	 * 
	 * @param page
	 *            分页
	 * @return 集合数据
	 */
	@SuppressWarnings("rawtypes")
	public List queryPage(PageModel page, String state, String loantype) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer(
				"SELECT count(1) FROM loanrecord INNER JOIN ");
		countsql.append(" userbasicsinfo ON loanrecord.userbasicinfo_id = userbasicsinfo.id INNER JOIN loansign ON  ");
		countsql.append(" loanrecord.loanSign_id = loansign.id INNER JOIN loansignbasics ON loanrecord.loanSign_id = loansignbasics.id");

		StringBuffer listsql = new StringBuffer(
				"SELECT loanrecord.id, loansignbasics.loanNumber, loansignbasics.loanTitle, loansign.loanType, ");
		listsql.append(" (SELECT NAME FROM userbasicsinfo WHERE id = loansign.userbasicinfo_id ), loanrecord.tenderTime, ");
		listsql.append(" userbasicsinfo. NAME, loanrecord.tenderMoney, loanrecord.recordway,loansignbasics.loanCategory FROM loanrecord INNER JOIN ");
		listsql.append(" userbasicsinfo ON loanrecord.userbasicinfo_id = userbasicsinfo.id INNER JOIN loansign ON  ");
		listsql.append(" loanrecord.loanSign_id = loansign.id INNER JOIN loansignbasics ON loanrecord.loanSign_id = loansignbasics.id");

		if (null != loantype && loantype != "" && "4".equals(loantype)) {
			countsql.append(" and loansign.loanType=4");
			listsql.append(" and loansign.loanType=4");
		} else if (null != loantype
				&& ("1".equals(loantype) || "2".equals(loantype))) {
			countsql.append(" and loansignbasics.loanCategory=").append(
					loantype);
			listsql.append(" and loansignbasics.loanCategory=")
					.append(loantype);
		}
		if (null != state && state != "") {
			countsql.append(" and loanrecord.recordway=").append(state);
			listsql.append(" and loanrecord.recordway=").append(state);
		}
		listsql.append(" order by loanrecord.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),
				listsql.toString(), null);
		return datalist;
	}

	/**
	 * <p>
	 * Title: getLoanrecordCount
	 * </p>
	 * <p>
	 * Description: 借款标为id的认购的条数
	 * </p>
	 * 
	 * @param loansignId
	 *            借款标号
	 * @return 条数
	 */
	public int getLoanrecordCount(int loansignId) {
		StringBuffer sb = new StringBuffer(
				"SELECT COUNT(1) from loanrecord where  loanSign_id=");
		return loanSignQuery.queryCount(sb.append(loansignId).toString());
	}

	/**
	 * <p>
	 * Title: getLoanrecordCount
	 * </p>
	 * <p>
	 * Description: 债权转让购买标为id的认购的条数
	 * </p>
	 * 
	 * @param loansignId
	 *            借款标号
	 * @return 条数
	 */
	public int getAssignmentCount(int loansignId) {
		StringBuffer sb = new StringBuffer(
				"SELECT COUNT(1) from loanrecord where  isSucceed=1 and loan_id=");
		return loanSignQuery.queryCount(sb.append(loansignId).toString());
	}

	/**
	 * <p>
	 * Title: getLoanrecordSum
	 * </p>
	 * <p>
	 * Description:认购的总金额
	 * </p>
	 * 
	 * @param loansignId
	 *            借款标
	 * @return 金额
	 */
	public int getLoanrecordSum(Long loansignId) {
		StringBuffer sb = new StringBuffer(
				"SELECT sum(tenderMoney) from loanrecord where  isSucceed=1 and loanSign_id=")
				.append(loansignId);

		return dao.queryNumberSql(sb.toString()).intValue();
	}

	/***
	 * 
	 * @param loanid
	 * @return
	 */
	public List<Loanrecord> findLoanRecordList(Long loanid) {
		StringBuffer sb = new StringBuffer(
				"SELECT * from loanrecord l where l.isSucceed=1 AND l.loanSign_id=")
				.append(loanid);
		return dao.findBySql(sb.toString(), Loanrecord.class);
	}

	/**
	 * <p>
	 * Title: queryLoanrecordList
	 * </p>
	 * <p>
	 * Description: 通过借款标标号查询到该借款标的认购记录
	 * </p>
	 * 
	 * @param start
	 *            start
	 * @param limit
	 *            limit
	 * @param loanSignId
	 *            借款标id
	 * @param num
	 *            如果=1 就加limit 如果!=1就不加
	 * @return List
	 */
	@SuppressWarnings("rawtypes")
	public List queryLoanrecordList(int start, int limit, int loanSignId,
			int num) {

		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT loanrecord.id, IFNULL(loansign.name,''),IFNULL(userbasicsinfo.name,''), loanrecord.tenderMoney, loanrecord.tenderTime, ");
		sb.append("CASE WHEN loanrecord.isSucceed=1 THEN '成功' WHEN loanrecord.isSucceed=-1 THEN '失败' WHEN loanrecord.isSucceed=0 THEN '待确认' END  , "
				+ "CASE WHEN loanrecord.isPrivilege=1 THEN '是' WHEN loanrecord.isPrivilege=0 THEN '否' END,"
				+ "CASE WHEN loanrecord.subType=1 THEN '优先'WHEN loanrecord.subType=2 THEN '夹层' WHEN loanrecord.subType=3 THEN '劣后' END,loanrecord.fee, loanrecord.redEnvelopeMoney");
		sb.append("  FROM loanrecord INNER JOIN loansign ON loanrecord.loanSign_id = loansign.id  ");
		sb.append(
				" INNER JOIN userbasicsinfo ON loanrecord.userbasicinfo_id = userbasicsinfo.id WHERE  loansign.id =")
				.append(loanSignId);
		/* .append("   and loanrecord.isSucceed = 0"); */
		sb.append(" order by loanrecord.id desc  ");
		if (num == 1) {
			sb.append(" LIMIT ").append(start).append(" , ").append(limit);
		}
		list = dao.findBySql(sb.toString());
		return list;
	}

	/**
	 * <p>
	 * Title: queryLoanrecordList
	 * </p>
	 * <p>
	 * Description: 通过借款标标号查询到该债权转让标的认购记录
	 * </p>
	 * 
	 * @param start
	 *            start
	 * @param limit
	 *            limit
	 * @param loanSignId
	 *            借款标id
	 * @param num
	 *            如果=1 就加limit 如果!=1就不加
	 * @return List
	 */
	@SuppressWarnings("rawtypes")
	public List queryAssignmentList(int start, int limit, int loanSignId,
			int num) {

		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT userbasicsinfo.`name`, loansign.rate*100, loanrecord.tenderMoney,  ");
		sb.append(" CASE WHEN loanrecord.isSucceed = 1 THEN '支付成功' ELSE '支付失败' END, loanrecord.tenderTime ");
		sb.append("  FROM loanrecord INNER JOIN loansign ON loanrecord.loan_id = loansign.id  ");
		sb.append(
				" INNER JOIN userbasicsinfo ON loanrecord.userbasicinfo_id = userbasicsinfo.id WHERE loansign.id =")
				.append(loanSignId);
		if (num == 1) {
			sb.append(" LIMIT ").append(start).append(" , ").append(limit);
		}
		list = dao.findBySql(sb.toString());
		return list;
	}

	/**
	 * 认购记录list 转JSONArray
	 * 
	 * @param list
	 *            集合
	 * @return JSONArray集合
	 */
	@SuppressWarnings("rawtypes")
	public JSONArray getJSONArrayByList(List list) {
		JSONObject json = null;
		JSONArray jsonlist = new JSONArray();

		if (list != null) {
			// 给每条数据添加标题
			for (Object obj : list) {
				json = new JSONObject();
				Object[] str = (Object[]) obj;
				json.element("id", str[0]);
				json.element("xname", str[1]);
				json.element("uname", str[2]);
				json.element("tenderMoney",
						Arith.round(new BigDecimal(str[3].toString()), 2) + "元");
				json.element("tenderTime", str[4]);
				json.element("isSucceed", str[5]);
				json.element("isPrivilege", str[6]);
				json.element("subType", str[7]);
				json.element("pMerBillNo", str[8]);
				json.element("redEnvelope", str[9]);
				jsonlist.add(json);
			}
		}
		return jsonlist;
	}

	/**
	 * 根据标的编号查询标的借款信息
	 * 
	 * @param id
	 *            标编号
	 * @return 返回标的借款信息
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getRecord(Long id) {
		String sql = "select u.name,s.rate,r.tenderMoney,r.isSucceed,r.tenderTime from loanrecord r,loansign s,userbasicsinfo u where r.loanSign_id=s.id and r.userbasicinfo_id=u.id and r.loanSign_id=? ORDER BY r.id LIMIT 0,10";
		List<Object[]> listRecord = (List<Object[]>) dao.findBySql(sql, id);
		return listRecord;
	}

	/**
	 * 根据订单信息查询购记录
	 * 
	 * @param ordernum
	 *            订单号
	 * @return
	 */
	public List<Loanrecord> getRecordByOrderNum(String ordernum) {
		StringBuffer sb = new StringBuffer(
				"SELECT * from loanrecord l where l.order_id=")
				.append(ordernum);
		return dao.findBySql(sb.toString(), Loanrecord.class);
	}

	/***
	 * 根据订单号查询购买记录信息
	 * 
	 * @param orderNum
	 * @return
	 */
	public Loanrecord getLoanRecordOrderNum(String orderNum) {
		String sql = "select * from loanrecord where order_id=?";
		Loanrecord loanrecord = dao.findObjectBySql(sql, Loanrecord.class,
				orderNum);
		return loanrecord;
	}

	public List getLoanRecordList(long loanid) {
		String sql = "SELECT sum(l.tenderMoney),u.`name`,u.id,u.pMerBillNo,r.phone "
				+ "FROM loanrecord l JOIN userbasicsinfo u ON l.userbasicinfo_id=u.id JOIN userrelationinfo r ON u.id=r.id "
				+ "WHERE l.loanSign_id=? AND l.isSucceed=1 GROUP BY l.userbasicinfo_id";
		List list = dao.findBySql(sql, loanid);
		return list;
	}
	
	/** 更新猴声大噪缓标存信息 */
	public Loanrecord getMaxLoanRecord(String LoansignId){
		String sql = "select * from loanrecord l where loanSign_id=? and isSucceed = 1 and subType in (1,2) ORDER BY l.tenderMoney desc,l.tenderTime asc limit 1";
		Loanrecord loanrecord = dao.findObjectBySql(sql, Loanrecord.class, LoansignId);
		return loanrecord;
	}
	
	/** 更新猴声大噪缓标存信息 */
	public Loanrecord getLastLoanRecord(String LoansignId){
		String sql = "select * from loanrecord l where loanSign_id=? and isSucceed = 1 and subType in (1,2) ORDER BY l.tenderTime desc limit 1";
		Loanrecord loanrecord = dao.findObjectBySql(sql, Loanrecord.class, LoansignId);
		return loanrecord;
	}
	
	/** 更新猴声大噪缓标存信息 */
	public int getIsSucceed(String LoansignId){
		String sql = "select count(id) from loanrecord l where loanSign_id=? and isSucceed = 0 and subType in (1,2)";
		return dao.queryNumberSql(sql, LoansignId).intValue();
	}

}
