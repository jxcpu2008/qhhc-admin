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
public class CustomerWithdrawService {

	@Resource
	private HibernateSupport dao;

	public List<Object> getWithdrawList(PageModel page, Userbasicsinfo user) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"select count(r.id) from userbasicsinfo u ,withdraw r ,userrelationinfo i  where  r.user_id=u.id and u.id=i.user_id  ");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"select r.id,u.userName,u.`name`,i.phone,i.cardId,r.amount,r.withdrawAmount,r.state,r.time,r.applytime,r.fee,r.mer_fee,r.fee_taken_on,r.strNum from userbasicsinfo u ,withdraw r ,userrelationinfo i  where  r.user_id=u.id and u.id=i.user_id ");
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
				sqlbuffer.append(" and u.userName like '%")
						.append(StringUtil.replaceAll(userName)).append("%'");
				countsql.append(" and u.userName like '%")
						.append(StringUtil.replaceAll(userName)).append("%'");
			}
			if (StringUtil.isNotBlank(user.getStaffNo())) {
				sqlbuffer.append(" and i.phone = ").append(
						StringUtil.replaceAll(user.getStaffNo()));
				countsql.append(" and i.phone = ").append(
						StringUtil.replaceAll(user.getStaffNo()));
			}
			
			// 提现时间查询
			if (StringUtil.isNotBlank(user.getCreateTime())) {
				sqlbuffer.append(" and r.time >= '")
						.append(user.getCreateTime()).append(" 00:00:00'");
			}

			if (StringUtil.isNotBlank(user.getFailTime())) {
				sqlbuffer.append(" and r.time <= '").append(user.getFailTime())
						.append(" 23:59:59'");
			}

			if (user.getUserType() != null) {
				sqlbuffer.append(" and r.state =").append(user.getUserType());
				countsql.append(" and r.state =").append(user.getUserType());
			}
		}
		sqlbuffer.append(" order by r.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);
		return datalist;
	}

	public List<Object> getTableWithdrawList(String userName,String name,String staffNo,String userType,String createTime,String failTime) {
		List datalist = new ArrayList();
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"select r.id,u.`name`,u.userName,i.phone,i.cardId,r.amount,r.withdrawAmount,"
						+ "r.fee,r.mer_fee,CASE WHEN r.fee_taken_on=2 THEN '用户支付' WHEN r.fee_taken_on=1 THEN '平台支付' END,r.time,r.applytime,r.strNum,"
						+ "CASE WHEN r.`state`=0 THEN '待确认' WHEN r.`state`=1 THEN '提现成功' WHEN r.`state`=2 THEN '宝付已受理' WHEN r.`state`=5 THEN '转账处理中' ELSE '提现失败' END "
						+ "from userbasicsinfo u ,withdraw r ,userrelationinfo i  where  r.user_id=u.id and u.id=i.user_id ");
		// 查询数据sql拼接
		if (StringUtil.isNotBlank(name)) {
			String nameOne = "";
			try {
				nameOne = java.net.URLDecoder.decode(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and u.`name` like '%")
					.append(StringUtil.replaceAll(nameOne)).append("%'");
		}

		if (StringUtil.isNotBlank(userName)) {
			String userNameOne = "";
			try {
				userNameOne = java.net.URLDecoder.decode(userName,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlbuffer.append(" and u.userName like '%")
					.append(StringUtil.replaceAll(userNameOne)).append("%'");
		}
		if (StringUtil.isNotBlank(staffNo)) {
			sqlbuffer.append(" and i.phone = ").append(StringUtil.replaceAll(staffNo));
		}
		
		// 提现时间查询
		if (StringUtil.isNotBlank(createTime)) {
			sqlbuffer.append(" and r.time >= '").append(createTime).append(" 00:00:00'");
		}

		if (StringUtil.isNotBlank(failTime)) {
			sqlbuffer.append(" and r.time <= '").append(failTime)
					.append(" 23:59:59'");
		}

		if (StringUtil.isNotBlank(userType)) {
			sqlbuffer.append(" and r.state =").append(userType);
		}
		sqlbuffer.append(" order by r.id desc");
		datalist = dao.findBySql(sqlbuffer.toString(), null);
		return datalist;
	}
}
