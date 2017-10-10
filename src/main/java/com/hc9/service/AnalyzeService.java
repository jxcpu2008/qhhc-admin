package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.impl.HibernateSupport;

/**
 * 信息分析服务层
 * @author Administrator
 *
 */
@Service
public class AnalyzeService {
	@Resource
	private HibernateSupport dao;
	
	/**
	 * 查询某一条标的投资者信息
	 * @param loanid
	 * @return
	 */
	public List getInvestorInfo(Long loanid) {
		String sql="SELECT "
				+ "u.`name`, lr.tenderMoney, lr.tenderTime, lr.subType,ul.ip,ul.address "
				+ "FROM loanrecord lr JOIN userbasicsinfo u ON lr.userbasicinfo_id=u.id JOIN userloginlog ul ON lr.userbasicinfo_id=ul.user_id "
				+ "WHERE lr.loanSign_id=? AND lr.isSucceed=1 GROUP BY lr.id";
		List list=dao.findBySql(sql, loanid);
		return list==null?null:list;
	}
	/**
	 * 标名称
	 * @param loanid
	 * @return
	 */
	public String getLoanName(Long loanid) {
		String sql="SELECT l.`name` FROM loansign l WHERE l.id=?";
		String name=dao.findObjectBySql(sql, loanid).toString();
		return name==null?null:name;
	}
}
