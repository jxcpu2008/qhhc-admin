package com.hc9.dao;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.Arith;
import com.hc9.dao.impl.HibernateSupport;

/** 投资相关dao */
@Service
public class InvestDao {
	@Resource
	private HibernateSupport commonDao;

	/** 根据标的id查询优先剩余金额 */
	public double queryPrioRestMoneyByLoanId(Long loanId) {
		String sql = "select prio_rest_money from loansign where id=?";
		Object obj = commonDao.findObjectBySql(sql, loanId);
		if(obj == null) {
			return 0.0;
		} else {
			return Arith.round(new BigDecimal(Double.valueOf(obj.toString())).doubleValue(), 2);
		}
	}

	/** 根据标的id查询夹层剩余金额 */
	public double queryMidRestMoneyByLoanId(Long loanId) {
		String sql = "select mid_rest_money from loansign where id=?";
		Object obj = commonDao.findObjectBySql(sql, loanId);
		if(obj == null) {
			return 0.0;
		} else {
			return Arith.round(new BigDecimal(Double.valueOf(obj.toString())).doubleValue(), 2);
		}
	}
}
