package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hc9.dao.entity.WithdrawApply;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * 提现申请
 * 
 * @author Administrator 2014-09-05
 *
 */
@Service
@SuppressWarnings("rawtypes")
public class WithdrawApplyAdminService {

	/**
	 * 数据库接口
	 */
	@Resource
	private HibernateSupport commonDao;

	public String connectionSql(String beginDate, String endDate) {
		String sql = "";
		if (beginDate != null && !"".equals(beginDate.trim())) {
			sql = sql
					+ " AND DATE_FORMAT(a.apply_time, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
					+ beginDate + "', '%Y-%m-%d %H:%i:%s') ";
		}
		if (endDate != null && !"".equals(endDate.trim())) {
			sql = sql
					+ " AND DATE_FORMAT(a.apply_time, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
					+ endDate + "', '%Y-%m-%d %H:%i:%s') ";
		}
		return sql;
	}

	/**
	 * 查询申请提现条数
	 * 
	 * @param beginDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return
	 */
	public Integer queryCount(String beginDate, String endDate) {
		String sql = "SELECT count(*)  FROM withdraw_apply a " + "WHERE 1=1"
				+ connectionSql(beginDate, endDate);
		Object obj = commonDao.findObjectBySql(sql);
		return Integer.parseInt(obj.toString());
	}

	/**
	 * 查询提现申请记录
	 * 
	 * @param page
	 *            分页对象
	 * @param beginDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return 申请提现记录
	 */
	public List queryPage(PageModel page, String beginDate, String endDate) {
		// String sql = "SELECT a.id,a.cash,a.userbasic_id,a.apply_num,"
		// + "a.result,a.status,a.apply_time,a.answer_time "
		// + "FROM withdraw_apply a WHERE "
		// + "1=1"
		// + connectionSql(beginDate, endDate) +
		// " ORDER BY a.apply_time DESC LIMIT "
		// + page.firstResult() + "," + page.getNumPerPage();
		// List list = commonDao.findBySql(sql);
		// return list;
		String sql = "SELECT a.id,a.cash,b.userName,b.name,a.apply_num,"
				+ "a.result,a.status,a.apply_time,a.answer_time ,a.userbasic_id,a.fee,ifnull(au.username,'')  "
				+ "FROM withdraw_apply a left join userbasicsinfo b on a.userbasic_id=b.id left JOIN adminuser au on a.admin_userId=au.id WHERE "
				+ "1=1" + connectionSql(beginDate, endDate)
				+ " ORDER BY a.apply_time DESC ";

		String sqlCount = "select count(a.id) from withdraw_apply a";
		List list = commonDao.pageListBySql(page, sqlCount, sql, null);
		return list;
	}

	/**
	 * 根据编号查询申请提现记录
	 * 
	 * @param ids
	 *            编号
	 * @return 申请提现记录
	 */
	public List queryById(String ids) {
		StringBuffer sql = new StringBuffer(
				"SELECT a.id,a.cash,b.userName,b.name,a.apply_num,"
						+ "a.result,a.status,a.apply_time,a.answer_time "
						+ "FROM withdraw_apply a left join userbasicsinfo b on a.userbasic_id=b.id WHERE "
						+ "1=1");
		if (ids != null && !ids.trim().equals("")) {
			ids = ids.substring(0, ids.lastIndexOf(","));
			sql.append(" AND a.id in (" + ids + ")");
		}
		List list = commonDao.findBySql(sql.toString());
		return list;
	}

	/**
	 * 查询会员申请是否已经审核
	 * 
	 * @param ids
	 *            会员编号
	 * 
	 * @return 查询结果
	 */
	public boolean ispassture(String ids) {
		StringBuffer sqlcount = new StringBuffer(
				"SELECT count(1) from withdraw_apply where status=0 and id in (")
				.append(ids.substring(0, ids.length() - 1)).append(")");
		Object obj = commonDao.findObjectBySql(sqlcount.toString());
		if (Integer.parseInt(obj.toString()) > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 审核提现申请
	 * 
	 * @param ids
	 *            申请人编号
	 * @param state
	 *            0:不通过,1:通过
	 * @return
	 */
	public boolean updateResult(String ids, int state, String adminId) {

		// 如果确认修改的字符串不为空
		if (ids.length() > 0) {
			// 先判断是否全部都能审核
			StringBuffer updatesql = new StringBuffer(
					"update withdraw_apply set status=1, result=")
					.append(state).append(",admin_userId=").append(adminId)
					.append(",answer_time=now() where id in (")
					.append(ids.substring(0, ids.length() - 1)).append(")");
			// 批量修改
			if (commonDao.executeSql(updatesql.toString()) <= 0) {
				return false;
			}
		}
		return true;
	}

	public boolean updateResult(String id, int state, double fee, String adminId) {
		// 如果确认修改的字符串不为空
		if (id.length() > 0) {
			// 先判断是否全部都能审核
			StringBuffer updatesql = new StringBuffer(
					"update withdraw_apply set status=1, result=")
					.append(state).append(",admin_userId=").append(adminId)
					.append(",fee=").append(fee)
					.append(",answer_time=now() where id in (").append(id)
					.append(")");
			// 批量修改
			if (commonDao.executeSql(updatesql.toString()) <= 0) {
				return false;
			}
		}
		return true;
	}

	/***
	 * 根据ID查询WithdrawApply
	 * 
	 * @param id
	 * @return
	 */
	public WithdrawApply getWithdrawApplyById(String id) {
		try {
			return commonDao.get(WithdrawApply.class, Long.valueOf(id));
		} catch (DataAccessException e) {
			return null;
		}
	}
}
