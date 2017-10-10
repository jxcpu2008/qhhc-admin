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
import com.hc9.model.RechargeQueryVo;

@Service
public class CustomerRechargeService {

	@Resource
	private HibernateSupport dao;

	public List<Object> getRechargeList(PageModel page, RechargeQueryVo vo) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"select count(r.id) from userbasicsinfo u ,recharge r ,userrelationinfo i  where  r.user_id=u.id and u.id=i.user_id ");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"select r.id,ifnull(u.id,''),u.userName,u.`name`,i.phone,i.cardId,r.rechargeAmount,r.reAccount,r.`status`,r.time,r.succ_time,r.fee,r.merfee,r.feetakenon,r.orderNum,r.fromSrc from userbasicsinfo u ,recharge r ,userrelationinfo i  where  r.user_id=u.id and u.id=i.user_id ");
		if (null != vo) {
			if (StringUtil.isNotBlank(vo.getName())) {
				String name = "";
				try {
					name = java.net.URLDecoder.decode(vo.getName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and u.`name` like '%")
						.append(StringUtil.replaceAll(name)).append("%'");
				countsql.append(" and u.`name` like '%")
						.append(StringUtil.replaceAll(name)).append("%'");
			}

			if (StringUtil.isNotBlank(vo.getUserName())) {
				String userName = "";
				try {
					userName = java.net.URLDecoder.decode(vo.getUserName(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and u.userName like '%")
						.append(StringUtil.replaceAll(userName)).append("%'");
				countsql.append(" and u.userName like '%")
						.append(StringUtil.replaceAll(userName)).append("%'");
			}

			// 充值时间查询
			if (StringUtil.isNotBlank(vo.getStartTime())) {
				sqlbuffer.append(" and r.time >= '")
						.append(vo.getStartTime()).append(" 00:00:00'");
				countsql.append(" and r.time >= '")
						.append(vo.getStartTime()).append(" 00:00:00'");
			}

			if (StringUtil.isNotBlank(vo.getEndTime())) {
				sqlbuffer.append(" and r.time <= '").append(vo.getEndTime())
						.append(" 23:59:59'");
				countsql.append(" and r.time <= '").append(vo.getEndTime())
						.append(" 23:59:59'");
			}

			if (vo.getRechargeStatus() != null && !"".equals(vo.getRechargeStatus())) {
				sqlbuffer.append(" and r.`status` =")
						.append(vo.getRechargeStatus());
				countsql.append(" and r.`status`=").append(vo.getRechargeStatus());
			}
		}
		sqlbuffer.append(" order by r.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);
		return datalist;
	}

	public List<Object> getTableRechargeList(String userName,String name,String userType,String createTime,String failTime) {
		List datalist = new ArrayList();
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer("select ");
		sqlbuffer.append(" r.id,u.`name`,u.userName,i.phone,i.cardId,r.rechargeAmount,r.reAccount,r.time,r.fee,r.merfee,");
		sqlbuffer.append("CASE WHEN r.feetakenon=2 THEN '用户支付' WHEN r.feetakenon=1 THEN '平台支付' END,");
		sqlbuffer.append("r.succ_time,r.orderNum,CASE WHEN r.`status`=0 THEN '待确认' WHEN r.`status`=1 THEN '充值成功' ELSE '充值失败' END,");
		sqlbuffer.append("CASE WHEN r.fromSrc=8 THEN 'android' WHEN r.fromSrc=9 THEN 'WAP' WHEN r.fromSrc=6 THEN 'ios' ELSE 'PC' END");
		sqlbuffer.append(" from userbasicsinfo u ,recharge r ,userrelationinfo i  where  r.user_id=u.id and u.id=i.user_id ");
		
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

		// 充值时间查询
		if (StringUtil.isNotBlank(createTime)) {
			sqlbuffer.append(" and r.time >= '")
					.append(createTime).append(" 00:00:00'");
		}

		if (StringUtil.isNotBlank(failTime)) {
			sqlbuffer.append(" and r.time <= '").append(failTime)
					.append(" 23:59:59'");
		}

		if (StringUtil.isNotBlank(userType)) {
			sqlbuffer.append(" and r.`status` =").append(userType);
		}
		sqlbuffer.append(" order by r.id desc");
		datalist = dao.findBySql(sqlbuffer.toString(), null);
		return datalist;
	}

}
