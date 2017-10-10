package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.FirstInvenstStaticsDetail;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/** 首投活动列表查询服务层 */
@Service
public class activityManageService {
	@Resource
	private HibernateSupport dao;

	/** 首投活动列表查询 */
	@SuppressWarnings("rawtypes")
	public List activityManagePage(PageModel page, FirstInvenstStaticsDetail firstInvenstStaticDetail) {
		List datalist = new ArrayList();
		StringBuffer countSql = new StringBuffer("select count(u.id) from userbasicsinfo u,loanrecord l,"
				+ "activity_monkey a,loansign lo,userrelationinfo r where u.id=a.userId and  u.id=r.user_id and a.loanId=lo.id and a.loanrecordId=l.id "
				+ "and lo.id=l.loanSign_id and a.type=14 ");

		StringBuffer sqlBuffer = new StringBuffer("select u.userName,u.name,r.phone,u.createTime,l.tenderTime,"
				+ "l.tenderMoney,IFNULL(lo.name,'未知') AS loanSignName,l.subType,a.rewardMoney,a.type from userbasicsinfo u,loanrecord l,"
				+ "activity_monkey a,loansign lo,userrelationinfo r where u.id=a.userId and u.id=r.user_id and a.loanId=lo.id and a.loanrecordId=l.id "
				+ "and lo.id=l.loanSign_id and a.type=14");
		/** 按奖励进行筛选 */
		if (firstInvenstStaticDetail.getRewardMoney() != null && firstInvenstStaticDetail.getRewardMoney() != 0) {
			countSql.append("  and a.rewardMoney=" + firstInvenstStaticDetail.getRewardMoney());
			sqlBuffer.append("  and a.rewardMoney=" + firstInvenstStaticDetail.getRewardMoney());
		}
		/** 按类型进行筛选 */
		if (firstInvenstStaticDetail.getType() != null && firstInvenstStaticDetail.getType() != 0) {
			countSql.append("  and a.type=" + firstInvenstStaticDetail.getType());
			sqlBuffer.append("  and a.type=" + firstInvenstStaticDetail.getType());
		}
		/** 按名字或手机号筛选 */
		if (firstInvenstStaticDetail.getMobilePhone() != null && firstInvenstStaticDetail.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(firstInvenstStaticDetail.getMobilePhone(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			countSql.append("  and (u.`name` like '%").append(name).append("%' or r.phone like '%")
					.append(firstInvenstStaticDetail.getMobilePhone()).append("%')");
			sqlBuffer.append(" and (u.`name` like '%").append(name).append("%' or r.phone like '%")
					.append(firstInvenstStaticDetail.getMobilePhone()).append("%')");
		}
		/** 按时间进行筛选 */
		countSql.append(
				connectionSql(firstInvenstStaticDetail.getCreateTime(), firstInvenstStaticDetail.getTenderTime()));
		sqlBuffer.append(
				connectionSql(firstInvenstStaticDetail.getCreateTime(), firstInvenstStaticDetail.getTenderTime()));
		sqlBuffer.append("  order by l.tenderTime desc");
		datalist = dao.pageListBySql(page, countSql.toString(), sqlBuffer.toString(), null);
		return datalist;
	}

	/***
	 * 认购时间
	 * 
	 * @param beginDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 */
	public String connectionSql(String beginDate, String endDate) {
		String sql = "";
		if (beginDate != null && !"".equals(beginDate.trim())) {
			sql = sql + " AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d')>=DATE_FORMAT('" + beginDate + "', '%Y-%m-%d') ";
		}
		if (endDate != null && !"".equals(endDate.trim())) {
			sql = sql + " AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d')<=DATE_FORMAT('" + endDate + "', '%Y-%m-%d') ";
		}
		return sql;
	}

	/** 导出首投活动列表查询 */
	@SuppressWarnings("rawtypes")
	public List QueryActivityManage(FirstInvenstStaticsDetail firstInvenstStaticDetail) {
		List datalist = new ArrayList();
		StringBuffer sqlBuffer = new StringBuffer("select u.userName,u.name,r.phone,u.createTime,l.tenderTime,"
				+ "l.tenderMoney,IFNULL(lo.name,'未知') AS loanSignName,l.subType,a.rewardMoney,a.type from userbasicsinfo u,loanrecord l,"
				+ "activity_monkey a,loansign lo,userrelationinfo r where u.id=a.userId and u.id=r.user_id and a.loanId=lo.id and a.loanrecordId=l.id "
				+ "and lo.id=l.loanSign_id and a.type=14");
		/** 按奖励进行筛选 */
		if (firstInvenstStaticDetail.getRewardMoney() != null && firstInvenstStaticDetail.getRewardMoney() != 0) {
			sqlBuffer.append("  and a.rewardMoney=" + firstInvenstStaticDetail.getRewardMoney());
		}
		/** 按类型进行筛选 */
		if (firstInvenstStaticDetail.getType() != null && firstInvenstStaticDetail.getType() != 0) {
			sqlBuffer.append("  and a.type=" + firstInvenstStaticDetail.getType());
		}
		/** 按名字或手机号筛选 */
		if (firstInvenstStaticDetail.getMobilePhone() != null && firstInvenstStaticDetail.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(firstInvenstStaticDetail.getMobilePhone(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sqlBuffer.append(" and (u.`name` like '%").append(name).append("%' or r.phone like '%")
					.append(firstInvenstStaticDetail.getMobilePhone()).append("%')");
		}
		/** 按时间进行筛选 */
		sqlBuffer.append(
				connectionSql(firstInvenstStaticDetail.getCreateTime(), firstInvenstStaticDetail.getTenderTime()));
		sqlBuffer.append("  order by l.tenderTime desc");
		datalist = dao.findBySql(sqlBuffer.toString());
		return datalist;
	}
}