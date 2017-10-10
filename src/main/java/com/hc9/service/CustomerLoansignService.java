package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class CustomerLoansignService {
	@Resource
	private HibernateSupport dao;

	public List<Object> getCustomerLoansignList(PageModel page,
			Userbasicsinfo user) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"select count(lr.id) from loanrecord lr JOIN userbasicsinfo u ON lr.userbasicinfo_id=u.id JOIN loansign l ON lr.loanSign_id=l.id"
						+ " where true ");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT lr.id,ifnull(ut.id,''),l.`name`,ifnull(ut.`name`,''),ut.userName,lr.tenderMoney,lr.order_id,lr.tenderTime,lr.isSucceed,lr.subType,l.`status`,l.remonth,l.credit_time,l.type,ut.createTime  "
						+ " FROM loanrecord lr "
						+ " JOIN loansign l ON lr.loanSign_id=l.id "
						+ " JOIN userbasicsinfo ut ON lr.userbasicinfo_id=ut.id "
						+ " where true ");
		if (null != user) {
			if (StringUtil.isNotBlank(user.getName())) {
				String name = "";
				try {
					name = java.net.URLDecoder.decode(user.getName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and ut.`name` like '%")
						.append(StringUtil.replaceAll(name)).append("%'");
				countsql.append(" and u.`name` like '%")
						.append(StringUtil.replaceAll(name)).append("%'");
			}

			if (StringUtil.isNotBlank(user.getUserName())) {
				String userName = "";
				try {
					userName = java.net.URLDecoder.decode(user.getUserName(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and ut.userName like '%")
						.append(StringUtil.replaceAll(userName)).append("%'");
				countsql.append(" and u.userName like '%")
						.append(StringUtil.replaceAll(userName)).append("%'");
			}

			if (user.getUserType() != null) {
				sqlbuffer.append(" and lr.isSucceed =").append(
						user.getUserType());
				countsql.append(" and lr.isSucceed =").append(
						user.getUserType());
			}

			if (user.getCardStatus() != null) {
				if (user.getCardStatus() == 7) {
					sqlbuffer.append(" and l.status in(7,8)");
					countsql.append(" and l.status in(7,8)");
				} else if (user.getCardStatus() == 10) {// 募集中
					sqlbuffer.append(" and l.status in(0,1,2,3,4,5,9)");
					countsql.append(" and l.status in(0,1,2,3,4,5,9)");
				} else {
					sqlbuffer.append(" and l.status =").append(
							user.getCardStatus());
					countsql.append(" and l.status =").append(
							user.getCardStatus());
				}
			}

			if (StringUtil.isNotBlank(user.getNickname())) {
				String loanName = "";
				try {
					loanName = java.net.URLDecoder.decode(user.getNickname(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and l.name  like '%")
						.append(StringUtil.replaceAll(loanName)).append("%'");
				countsql.append(" and l.name like '%")
						.append(StringUtil.replaceAll(loanName)).append("%'");
			}
			if (user.getUserintegral() != null) {
				sqlbuffer.append(" and lr.subType =").append(
						user.getUserintegral());
				countsql.append(" and lr.subType =").append(
						user.getUserintegral());
			}

			// 认购时间查询
			if (StringUtil.isNotBlank(user.getCreateTime())) {
				sqlbuffer.append(" and lr.tenderTime >= '")
						.append(user.getCreateTime()).append(" 00:00:00'");
				countsql.append(" and lr.tenderTime  >= '")
						.append(user.getCreateTime()).append(" 00:00:00'");
			}

			if (StringUtil.isNotBlank(user.getFailTime())) {
				sqlbuffer.append(" and lr.tenderTime  <= '")
						.append(user.getFailTime()).append(" 23:59:59'");
				countsql.append(" and lr.tenderTime  <= '")
						.append(user.getFailTime()).append(" 23:59:59'");
			}

			// 期限
			if (user.getDepartment() != null) {
				sqlbuffer.append(" and l.remonth =").append(
						user.getDepartment());
				countsql.append(" and l.remonth =")
						.append(user.getDepartment());
			}

			// 标的状态
			if (user.getHasIpsAccount() != null) {
				if (user.getHasIpsAccount() == 1) {
					sqlbuffer.append(" and l.status =").append(
							user.getHasIpsAccount());
					countsql.append(" and l.status =").append(
							user.getHasIpsAccount());
				} else if (user.getHasIpsAccount() == 2) {
					sqlbuffer.append(" and l.status in(2,3,4,5) ");
					countsql.append(" and l.status in(2,3,4,5) ");
				} else if (user.getHasIpsAccount() == 3) {
					sqlbuffer.append(" and l.status in(6,7) ");
					countsql.append(" and l.status in(6,7) ");
				} else if (user.getHasIpsAccount() == 4) {
					sqlbuffer.append(" and l.status in(8) ");
					countsql.append(" and l.status in(8) ");
				} else if (user.getHasIpsAccount() == 5) {
					sqlbuffer.append(" and l.status in(9) ");
					countsql.append(" and l.status in(9) ");
				}

			}
		}
		sqlbuffer.append(" order by lr.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);
		return datalist;
	}

	// 导出
	public List<Object> outCustomerLoansignList(Userbasicsinfo user) {
		List datalist = new ArrayList();
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"select r.id,(select l.`name` from loansign l where l.id=r.loanSign_id),u.userName,u.`name`,r.tenderMoney,r.order_id,r.tenderTime,"
						+ "CASE WHEN  r.isSucceed=1 THEN '成功' ELSE '失败' END ,CASE WHEN r.subType=1 THEN '优先' WHEN r.subType=2 THEN '夹层' WHEN r.subType=3 THEN '劣后' END ,"
						+ "CASE WHEN r.webOrApp=1 THEN 'pc端' WHEN r.webOrApp=2 THEN 'APP端' END ,u.createTime "
						+ "  from userbasicsinfo u ,loanrecord r ,loansign l  "
						+ "where  r.userbasicinfo_id=u.id  and r.loanSign_id=l.id  ");
		if (null != user) {
			if (StringUtil.isNotBlank(user.getName())) {
				String name = "";
				try {
					name = java.net.URLDecoder.decode(user.getName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and u.`name` like '%")
						.append(StringUtil.replaceAll(name)).append("%'");
			}

			if (StringUtil.isNotBlank(user.getUserName())) {
				String userName = "";
				try {
					userName = java.net.URLDecoder.decode(user.getUserName(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and u.userName like '%")
						.append(StringUtil.replaceAll(userName)).append("%'");
			}

			if (user.getUserType() != null) {
				sqlbuffer.append(" and r.isSucceed =").append(
						user.getUserType());
			}

			if (user.getCardStatus() != null) {
				sqlbuffer.append(" and rep.repayState =").append(
						user.getCardStatus());
			}

			if (user.getNickname() != null) {
				String loanName = "";
				try {
					loanName = java.net.URLDecoder.decode(user.getNickname(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and l.name  like '%")
						.append(StringUtil.replaceAll(loanName)).append("%'");
			}
			if (user.getUserintegral() != null) {
				sqlbuffer.append(" and r.subType =").append(
						user.getUserintegral());
			}

			// 认购时间查询
			if (StringUtil.isNotBlank(user.getCreateTime())) {
				sqlbuffer.append(" and r.tenderTime >= '")
						.append(user.getCreateTime()).append(" 00:00:00'");
			}

			if (StringUtil.isNotBlank(user.getFailTime())) {
				sqlbuffer.append(" and r.tenderTime  <= '")
						.append(user.getFailTime()).append(" 23:59:59'");
			}
		}
		sqlbuffer.append(" order by id desc");
		datalist = dao.findBySql(sqlbuffer.toString());
		return datalist;
	}

	public List getSeeLoanDetailes(String lrId) {

		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT lr.id,l.`name`,ifnull(ut.`name`,''),lr.tenderMoney,lr.order_id,lr.tenderTime,lr.isSucceed,lr.subType,l.`status`,l.remonth,l.credit_time,ifnull(ug.name,''),ug.department,"
						+ "(SELECT  sum(gm.bonuses) from generalizemoney gm where gm.refer_userid=lr.userbasicinfo_id and gm.loanrecord_id=lr.id and gm.refered_userid=lr.userbasicinfo_id),l.type "
						+ "FROM loanrecord lr  JOIN loansign l ON lr.loanSign_id=l.id  JOIN userbasicsinfo ut ON lr.userbasicinfo_id=ut.id "
						+ "LEFT JOIN generalize g ON g.uid=lr.userbasicinfo_id LEFT JOIN userbasicsinfo ug ON g.genuid=ug.id where  lr.id =? order by lr.id desc");
		List datalist = dao.findBySql(sqlbuffer.toString(), lrId);
		return datalist;
	}
}
