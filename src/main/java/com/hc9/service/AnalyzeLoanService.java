package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.impl.HibernateSupport;

@Service
public class AnalyzeLoanService {

	@Resource
	private HibernateSupport dao;
	
	/**
	 * 返回符合满标状态的标列表
	 * @param lowerBound 融资金额的下限
	 * @param uperBound 融资金额的上限
	 * @return
	 */
	public List<Loansign> getFinishedLoansign(double lowerBound,double uperBound) {
		String sql="SELECT * FROM loansign lr WHERE lr.issueLoan>=? AND lr.issueLoan<=? AND lr.`status`>2 AND lr.`status`<9 order by lr.publish_time";
		List<Loansign> list=dao.findBySql(sql, Loansign.class, lowerBound,uperBound);
		return list;
	}
	
	/**
	 * 返回投资记录
	 * @param id
	 * @return
	 */
	public List<Loanrecord> getLoanrecords(Long id) {
		String sql="SELECT * FROM loanrecord lr WHERE lr.isSucceed=1 AND lr.loanSign_id=? ORDER BY lr.id";
		List<Loanrecord> list=dao.findBySql(sql, Loanrecord.class, id);
		return list;
	}

}
