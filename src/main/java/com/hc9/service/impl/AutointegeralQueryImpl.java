package com.hc9.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.dao.entity.Autointegral;
import com.hc9.dao.entity.Manualintegral;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
import com.hc9.service.AutointegralQuery;
import com.hc9.service.BorrowersQuery;

/**
 * 
 * 积分通用接口的查询的实现
 * 
 * @author Administrator
 * 
 */
@Service
public class AutointegeralQueryImpl implements AutointegralQuery {

	/** dao */
	@Resource
	private HibernateSupport dao;

	/** borrowersQuery */
	@Autowired
	private BorrowersQuery borrowersQuery;

	/**
	 * 通过还款金额求出对应的积分值
	 * 
	 * @param money
	 *            还款金额
	 * @return 积分
	 */
	public int calculationIntegral(double money) {
		return (int) money / 100;
	}

	/*****
	 * 通过管理员勾选的信息，进行计算手动积分合计
	 * 
	 * @param manualin
	 *            手动积分对象
	 * @return 积分合计
	 */
	public int getALLBYOneSerch(Manualintegral manualin) {
		int amountpoints = 0;
		if (manualin.getCkVaule() != null
				&& !"".endsWith(manualin.getCkVaule())) {
			String[] mastring = manualin.getCkVaule().split(",");
			// String[] lmastring = null;
			for (int i = 0; i < mastring.length; i++) {
				/*
				 * lmastring = mastring[i].split("-"); amountpoints +=
				 * lmastring[1] != null ? Integer .parseInt(lmastring[1] + "") :
				 * 0;
				 */
				amountpoints += mastring[i] != null ? Integer
						.parseInt(mastring[i] + "") : 0;
			}
		}
		amountpoints += manualin.getHouseCardPoints() != null ? manualin
				.getHouseCardPoints() : 0;
		amountpoints += manualin.getBankWaterPoints() != null ? manualin
				.getBankWaterPoints() : 0;
		amountpoints += manualin.getSocialPoints() != null ? manualin
				.getSocialPoints() : 0;
		amountpoints += manualin.getCreditCardPoints() != null ? manualin
				.getCreditCardPoints() : 0;
		amountpoints += manualin.getSalesContractInvoicePoints() != null ? manualin
				.getSalesContractInvoicePoints() : 0;
		return amountpoints;
	}

	/**
	 * 通过会员编号查询到会员的积分总额 =自动积分总和+手动积分
	 * 
	 * @param userbasicsinfo
	 *            用户对象
	 * @return 积分总额
	 */
	public int queryAllIntegral(Userbasicsinfo userbasicsinfo) {
		int allIntegral = 0;

		if (borrowersQuery.isBorrowsByUser(userbasicsinfo)) {
			allIntegral = queryAutoSUMIntegral(userbasicsinfo);
			StringBuffer sb = new StringBuffer(
					"SELECT amountPoints  from manualintegral ate where ate.user_id=")
					.append(userbasicsinfo.getId());
			Object object1 = dao.findObjectBySql(sb.toString());
			allIntegral += object1 != null ? Integer.parseInt(object1
					.toString()) : 0;
		}
		return allIntegral;
	}

	/**
	 * 求到该用户的自动积分总和
	 * 
	 * @param userbasicsinfo
	 *            用户对象
	 * @return 积分总和
	 */
	public int queryAutoSUMIntegral(Userbasicsinfo userbasicsinfo) {
		StringBuffer sb = new StringBuffer(
				"select SUM(realityintegral) from autointegral where user_id=")
				.append(userbasicsinfo.getId());
		Object object = dao.findObjectBySql(sb.toString());
		return object != null ? Integer.parseInt(object.toString()) : 0;
	}

	/**
	 * 通过用户找到他的手动积分记录
	 * 
	 * @param userbasicsinfo
	 *            用户对象
	 * @return 积分
	 */
	public Manualintegral queryManuaByuser(Userbasicsinfo userbasicsinfo) {

		StringBuffer sb = new StringBuffer(
				"select * from manualintegral where user_id=")
				.append(userbasicsinfo.getId());
		List<Manualintegral> mlist = dao.findBySql(sb.toString(),
				Manualintegral.class, null);
		if (mlist.size() > 0) {
			return mlist.get(0);
		}
		return null;
	}

	/**
	 * 通过userId查询积分列表
	 * 
	 * @param id
	 * @return
	 */
	public List<Autointegral> queryAutointegralByuserId(Long id) {
		System.out.println(id);
		StringBuffer sb = new StringBuffer(
				"select * from autointegral where user_id=").append(id);

		List<Autointegral> autointegrals = dao.findBySql(sb.toString(),
				Autointegral.class, null);

		if (autointegrals.size() > 0) {
			return autointegrals;
		} else {
			return null;
		}
	}

	public PageModel queryAutointegralByuserId(HttpServletRequest request,
			PageModel page) {

		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);

		StringBuffer sql = new StringBuffer(
				"select * from autointegral where user_id=");
		sql.append(user.getId());
		StringBuffer sqlCount = new StringBuffer(
				"select count(id) from autointegral where user_id=");
		sqlCount.append(user.getId());

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());

		List<Autointegral> list = dao.findBySql(sql.toString(),
				Autointegral.class);
		page.setList(list);// Loansign集合
		return page;

	}

	public Double getCreaitCount(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		String sql = "SELECT sum(realityintegral) from autointegral where user_id=?";
		Double creait = dao.queryNumberSql(sql, user.getId());
		if (creait != null) {
			return creait;
		} else {
			return 0.00;
		}
	}

}
