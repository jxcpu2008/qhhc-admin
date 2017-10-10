package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.hc9.common.util.Arith;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.UserBank;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class FundsAccountService {

	@Resource
	private HibernateSupport dao;

	@Resource
	private LoanSignQuery loanSignQuery;

	/**
	 * <p>
	 * Title: queryUserPage
	 * </p>
	 * <p>
	 * Description: 分页查询会员信息
	 * </p>
	 * 
	 * @param page
	 *            分页参数
	 * @param userinfo
	 *            查询条件
	 * @return 查询结果集list
	 */
	@SuppressWarnings("rawtypes")
	public List queryUserPage(PageModel page, Userbasicsinfo userinfo) {
		List datalist = new ArrayList();

		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"SELECT count(1)FROM userbasicsinfo ");
		countsql.append(" LEFT JOIN userfundinfo ON userbasicsinfo.id = userfundinfo.id ");
		countsql.append(" LEFT JOIN userrelationinfo ON userrelationinfo.user_id = userbasicsinfo.id  ");
		countsql.append(" WHERE userbasicsinfo.userName is not null ");

		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT userbasicsinfo.id, userbasicsinfo.userName, userbasicsinfo.`name`,userrelationinfo.phone, ");
		sqlbuffer.append(" ROUND(userfundinfo.credit,2), ");
		sqlbuffer
				.append(" (SELECT  max(endtime) FROM vipinfo WHERE vipinfo.endtime > NOW() AND vipinfo.user_id = userbasicsinfo.id)  AS vipendtime,");
		sqlbuffer
				.append(" ( SELECT count(1) FROM borrowersbase WHERE auditResult = 1 AND userbasicinfo_id = userbasicsinfo.id ),");
		sqlbuffer
				.append("  CASE WHEN userbasicsinfo.isLock = 0 THEN '正常' ELSE '禁用' END, ");
		sqlbuffer
				.append(" (SELECT realname FROM adminuser WHERE id = userbasicsinfo.adminuser_id ), userbasicsinfo.pMerBillNo");
		sqlbuffer
				.append(" FROM userbasicsinfo LEFT JOIN userfundinfo ON userbasicsinfo.id = userfundinfo.id ");
		sqlbuffer
				.append(" LEFT JOIN userrelationinfo ON userrelationinfo.user_id = userbasicsinfo.id WHERE userbasicsinfo.userName is not null ");

		if (null != userinfo) {

			if (StringUtil.isNotBlank(userinfo.getUserName())) {
				sqlbuffer.append(" and userbasicsinfo.userName like '%")
						.append(StringUtil.replaceAll(userinfo.getUserName()))
						.append("%'");
				countsql.append(" and userbasicsinfo.userName like '%")
						.append(StringUtil.replaceAll(userinfo.getUserName()))
						.append("%'");
			}
			// 真实姓名模糊查询
			if (StringUtil.isNotBlank(userinfo.getName())) {
				String name = "";
				try {
					name = java.net.URLDecoder.decode(userinfo.getName(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" AND userbasicsinfo.name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
				countsql.append(" AND userbasicsinfo.name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
			}
			// 是否是特权会员，0是普通会员，1是特权会员
			if (null != userinfo.getErrorNum()) {
				if ("0".equals(userinfo.getErrorNum().intValue() + "")) {
					sqlbuffer
							.append(" and userbasicsinfo.id NOT IN ( SELECT vipinfo.user_id FROM vipinfo WHERE vipinfo.endtime > NOW())");
					countsql.append(" and  userbasicsinfo.id NOT IN ( SELECT vipinfo.user_id FROM vipinfo WHERE vipinfo.endtime > NOW())");
				} else {
					sqlbuffer
							.append(" and userbasicsinfo.id IN ( SELECT vipinfo.user_id FROM vipinfo WHERE vipinfo.endtime > NOW())");
					countsql.append(" and userbasicsinfo.id IN ( SELECT vipinfo.user_id FROM vipinfo WHERE vipinfo.endtime > NOW())");
				}
			}
			// 是否是借款人
			if (null != userinfo.getLockTime()
					&& !"".equals(userinfo.getLockTime())) {
				if ("0".equals(userinfo.getLockTime())) {
					sqlbuffer
							.append("AND userbasicsinfo.id NOT  IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
					countsql.append("AND userbasicsinfo.id NOT  IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
				} else {
					sqlbuffer
							.append("AND userbasicsinfo.id IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
					countsql.append("AND userbasicsinfo.id IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
				}

			}

			// 注册时间查询
			if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
				sqlbuffer.append(" and userbasicsinfo.createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
				countsql.append(" and userbasicsinfo.createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
			}

			if (StringUtil.isNotBlank(userinfo.getFailTime())) {
				sqlbuffer.append(" and userbasicsinfo.createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
				countsql.append(" and userbasicsinfo.createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
			}

			// 登录次数查询
			if (null != userinfo.getIsLock()) {
				sqlbuffer
						.append(" and  (SELECT count(0) FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id)>"
								+ userinfo.getIsLock().intValue());
				countsql.append(" and  (SELECT count(0) FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id)>"
						+ userinfo.getIsLock().intValue());
			}

			// 根据客服
			if (StringUtil.isNotBlank(userinfo.getNickname())) {
				sqlbuffer.append("AND userrelationinfo.adminuser_id = ")
						.append(userinfo.getNickname());
				countsql.append("AND userrelationinfo.adminuser_id = ").append(
						userinfo.getNickname());
			}

		}
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);

		return datalist;

	}

	/***
	 * 保存用户银行卡信息
	 * 
	 * @param userBank
	 */
	public void saveOrUpdateUserBank(UserBank userBank) {
		dao.saveOrUpdate(userBank);
	}
	
	/***
	 * 删除不存在绑定宝付的银行卡账户
	 * @param bankNo
	 */
	public void deleteUserBank(Long userId){
		String sql="delete from userbank where user_id="+userId;
		dao.executeSql(sql);
	}

	/***
	 * 根据用户ID查询银行卡信息
	 * 
	 * @param userId
	 * @return
	 */
	public UserBank getUserBank(String userId) {
		String sql = "select * from userbank where user_id=?";
		List<UserBank> userBankList = dao
				.findBySql(sql, UserBank.class, userId);
		return userBankList.size() > 0 ? (UserBank) userBankList.get(0) : null;
	}
	public List<UserBank> getUserBankList(String userId) {
		String sql = "select * from userbank where user_id=? and state=1";
		List<UserBank> userBankList = dao
				.findBySql(sql, UserBank.class, userId);
		return userBankList.size() > 0 ? userBankList : null;
	}

	public int getLoanrecordCount(int userId) {
		StringBuffer sb = new StringBuffer(
				"select count(1) from loanrecord l, userbasicsinfo u where l.userbasicinfo_id=u.id and l.isSucceed=1 and u.id=");
		return loanSignQuery.queryCount(sb.append(userId).toString());
	}

	@SuppressWarnings("rawtypes")
	public List queryLoanrecordList(int start, int limit, int userId, int num,
			Userbasicsinfo userinfo) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT loanrecord.id, IFNULL(loansign.name,''),IFNULL(userbasicsinfo.name,''), loanrecord.tenderMoney, loanrecord.tenderTime, ");
		sb.append("CASE WHEN loanrecord.isSucceed  =1 THEN '成功' ELSE '失败' END,");
		sb.append("  loanrecord.order_id,CASE WHEN loanrecord.subType  =1 THEN '优先'  WHEN loanrecord.subType  =2 THEN '夹层'   WHEN loanrecord.subType  =3 THEN '劣后' ELSE '类型' END,loanrecord.fee ");
		sb.append("  FROM loanrecord INNER JOIN loansign ON loanrecord.loanSign_id = loansign.id  ");
		sb.append(
				" INNER JOIN userbasicsinfo ON loanrecord.userbasicinfo_id = userbasicsinfo.id WHERE  userbasicsinfo.id =")
				.append(userId);
		sb.append(" and loanrecord.isSucceed = 1");
		// 注册时间查询
		if (userinfo != null) {
			if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
				sb.append(" and loanrecord.tenderTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
				sb.append(" and loanrecord.tenderTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
			}
			if (StringUtil.isNotBlank(userinfo.getFailTime())) {
				sb.append(" and loanrecord.tenderTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
				sb.append(" and loanrecord.tenderTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
			}
		}
		sb.append(" order by loanrecord.id desc  ");
		if (num == 1) {
			sb.append(" LIMIT ").append(start).append(" , ").append(limit);
		}
		list = dao.findBySql(sb.toString());
		return list;
	}

	@SuppressWarnings("rawtypes")
	public List queryLoanrecordListAll(Userbasicsinfo userinfo) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT loanrecord.id, IFNULL(loansign.name,''),IFNULL(userbasicsinfo.name,''), loanrecord.tenderMoney, loanrecord.tenderTime, ");
		sb.append("CASE WHEN loanrecord.isSucceed  =1 THEN '成功' ELSE '失败' END,");
		sb.append("  loanrecord.order_id,CASE WHEN loanrecord.subType  =1 THEN '优先'  WHEN loanrecord.subType  =2 THEN '夹层'   WHEN loanrecord.subType  =3 THEN '劣后' ELSE '类型' END,loanrecord.fee ");
		sb.append("  FROM loanrecord INNER JOIN loansign ON loanrecord.loanSign_id = loansign.id  ");
		sb.append(" INNER JOIN userbasicsinfo ON loanrecord.userbasicinfo_id = userbasicsinfo.id");
		sb.append(" and loanrecord.isSucceed = 1");
		// 注册时间查询
		if (userinfo != null) {
			if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
				sb.append(" and loanrecord.tenderTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
				sb.append(" and loanrecord.tenderTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
			}
			if (StringUtil.isNotBlank(userinfo.getFailTime())) {
				sb.append(" and loanrecord.tenderTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
				sb.append(" and loanrecord.tenderTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
			}
		}
		sb.append(" order by loanrecord.id desc  ");
		list = dao.findBySql(sb.toString());
		return list;
	}

	public JSONArray getJSONArrayByList(List list) {
		JSONObject json = null;
		JSONArray jsonlist = new JSONArray();

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
			json.element("orderId", str[6]);
			json.element("subType", str[7]);
			json.element("pMerBillNo", str[8]);
			jsonlist.add(json);
		}
		return jsonlist;
	}

	@SuppressWarnings("rawtypes")
	public List queryAccountinfo(String id, Userbasicsinfo userinfo) {

		List accountList = new ArrayList();

		// 判断会员主键是否为数字
		if (StringUtil.isNotBlank(id) && StringUtil.isNumberString(id)) {
			StringBuffer sqlBuffer = new StringBuffer(
					"SELECT userbasicsinfo.userName,userbasicsinfo.`name`,accountinfo.time,accounttype.`name`,ifnull(expenditure,'0.00'),ifnull(income,'0.00'),ifnull(money,'0.00') as money ,ifnull(accountinfo.fee,'0.00'),ipsNumber,explan  FROM accountinfo");
			sqlBuffer
					.append(" INNER JOIN accounttype ON accounttype_id=accounttype.id  INNER JOIN userbasicsinfo on accountinfo.userbasic_id=userbasicsinfo.id  WHERE accountinfo.userbasic_id="
							+ id);
			// 注册时间查询
			if (userinfo != null) {
				if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
					sqlBuffer.append(" and accountinfo.time >= '")
							.append(userinfo.getCreateTime())
							.append(" 00:00:00'");
					sqlBuffer.append(" and accountinfo.time >= '")
							.append(userinfo.getCreateTime())
							.append(" 00:00:00'");
				}
				if (StringUtil.isNotBlank(userinfo.getFailTime())) {
					sqlBuffer.append(" and accountinfo.time <= '")
							.append(userinfo.getFailTime())
							.append(" 23:59:59'");
					sqlBuffer.append(" and accountinfo.time <= '")
							.append(userinfo.getFailTime())
							.append(" 23:59:59'");
				}
			}
			sqlBuffer.append(" order by accountinfo.time desc ");

			accountList = dao.findBySql(sqlBuffer.toString());
		}

		return accountList;
	}

	@SuppressWarnings("rawtypes")
	public List queryAccountinfoAll(Userbasicsinfo userinfo) {

		List accountList = new ArrayList();

		StringBuffer sqlBuffer = new StringBuffer(
				"SELECT userbasicsinfo.userName,userbasicsinfo.`name`,accountinfo.time,accounttype.`name`,ifnull(expenditure,'0.00'),ifnull(income,'0.00'),ifnull(money,'0.00') as money ,ifnull(accountinfo.fee,'0.00'),ipsNumber,explan  FROM accountinfo");
		sqlBuffer
				.append(" INNER JOIN accounttype ON accounttype_id=accounttype.id INNER JOIN userbasicsinfo on accountinfo.userbasic_id=userbasicsinfo.id  WHERE 1=1 ");
		// 注册时间查询
		if (userinfo != null) {
			if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
				sqlBuffer.append(" and accountinfo.time >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
				sqlBuffer.append(" and accountinfo.time >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
			}
			if (StringUtil.isNotBlank(userinfo.getFailTime())) {
				sqlBuffer.append(" and accountinfo.time <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
				sqlBuffer.append(" and accountinfo.time <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
			}
		}
		sqlBuffer.append(" order by userbasicsinfo.userName desc ");
		accountList = dao.findBySql(sqlBuffer.toString());
		return accountList;
	}

}
