package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**   
 * Filename:    AccountInfo.java   
 * Company:     前海红筹  
 * @version:    3.0   
 * @since:  JDK 1.7.0_25  
 * Description:  流水账操作服务层

 */

@Service
public class AccountInfoService {

	/** 注入数据库操作层 */
	@Resource
	private HibernateSupport dao;

	/**
	 * <p>
	 * Title: queryPageByUser
	 * </p>
	 * <p>
	 * Description: 后台查询会员的流水账明细
	 * </p>
	 * 
	 * @param ids
	 *            会员编号
	 * @param page
	 *            分页信息
	 * @return 返回查询结果
	 */
	public List queryPageByUser(String ids, PageModel page) {

		List accountList = new ArrayList();

		// 判断会员主键是否为数字
		if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {
			StringBuffer countsql = new StringBuffer(
					"SELECT count(accountinfo.id)  FROM accountinfo");
			StringBuffer sqlBuffer = new StringBuffer(
					"SELECT accountinfo.time,accounttype.`name`,expenditure,income,money,ipsNumber,explan,fee  FROM accountinfo");

			sqlBuffer
					.append(" INNER JOIN accounttype ON accounttype_id=accounttype.id WHERE accountinfo.userbasic_id="
							+ ids);
			countsql.append(" INNER JOIN accounttype ON accounttype_id=accounttype.id WHERE accountinfo.userbasic_id="
					+ ids);
			sqlBuffer.append(" order by accountinfo.time desc ");
			accountList = dao.pageListBySql(page, countsql.toString(),
					sqlBuffer.toString(), null);
		}

		return accountList;
	}
	
	public List queryPageByCompanyUser(String loanId, PageModel page) {
		List accountList = new ArrayList();
		// 判断会员主键是否为数字
		if (StringUtil.isNotBlank(loanId) && StringUtil.isNumberString(loanId)) {
			StringBuffer countsql = new StringBuffer(
					"SELECT count(id)  FROM accountinfo");
			StringBuffer sqlBuffer = new StringBuffer(
					"SELECT time,expenditure,income,money,ipsNumber,explan,fee  FROM accountinfo ");
			countsql.append("  WHERE accounttype_id=17 and  loansign_id="+ loanId);
			sqlBuffer.append("  WHERE accounttype_id=17 and  loansign_id="+ loanId);
			sqlBuffer.append(" order by time desc ");
			accountList = dao.pageListBySql(page, countsql.toString(),sqlBuffer.toString(), null);
		}

		return accountList;
	}

}
