package com.hc9.service;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.dao.entity.Loansignflow;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;

@Service
public class AssignmentService {
	/** 引入log4j日志打印类 */
	private static final Logger LOGGER = Logger
			.getLogger(UserInfoServices.class);

	/** 注入数据库操作层 */
	@Resource
	private HibernateSupport dao;

	@Resource
	private BorrowService borrowService;

	@Resource
	private BaseLoansignService baseLoansignService;

	/***
	 * 审核修改状态的sql语句
	 * 
	 * @param id
	 * @param auditStatus
	 * @param auditResult
	 * @return
	 */
	public boolean updateSql(String id, int auditStatus, int auditResult) {

		boolean flag = false;
		if (StringUtil.isNotBlank(id)) {
			// 根据“，”拆分字符串
			String[] newids = id.split(",");
			// 要修改状态的编号
			String delstr = "";
			for (String idstr : newids) {
				// 将不是空格和非数字的字符拼接
				if (StringUtil.isNotBlank(idstr)
						&& StringUtil.isNumberString(idstr)) {
					delstr += idstr + ",";
				}
			}
			if (StringUtil.isNotBlank(delstr)) {
				String sql = "update loansignflow set auditStatus="
						+ auditStatus + ",auditResult=" + auditResult
						+ " where flowid="
						+ delstr.substring(0, delstr.length() - 1);
				// 修改状态
				if (dao.executeSql(sql) > 0) {
					flag = true;
				}
			}
		}
		return flag;
	}

	public void save(Loansignflow loansignflow) {
		dao.save(loansignflow);
	}

	public Loansignflow selLoansignFlow(String id) {
		try {
			id = id.substring(0, id.length() - 1);
			return dao.get(Loansignflow.class, Long.valueOf(id));
		} catch (DataAccessException e) {
			return null;
		}
	}

	// 债权转让的新增与发布
	public boolean getAssignmentPublish(Loansignflow loansignflow) {

		boolean bool = true;
		Loansign loansign = dao.get(Loansign.class, loansignflow.getLoanId());
		Loansignbasics loansignbasics = dao.get(Loansignbasics.class,
				loansignflow.getLoanId());
		Userbasicsinfo user = dao.get(Userbasicsinfo.class, loansignflow.getUserDebt());
		// 5.添加
		if (bool) {
			bool = baseLoansignService.saveAssignment(loansign, loansignbasics,
					user,loansignflow);
		}
		return bool;
	}

}
