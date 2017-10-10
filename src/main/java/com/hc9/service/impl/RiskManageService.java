package com.hc9.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.hc9.common.util.StatisticsUtil;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.InadvanceRepayInfo;
import com.hc9.model.PageModel;
import com.hc9.service.IRiskManageService;

/**
 * 风控管理服务类
 * @author Jerry Wong
 *
 */
@Service
public class RiskManageService implements IRiskManageService {

	private static final Logger logger = Logger.getLogger(RiskManageService.class);

	@Resource
	private HibernateSupport dao;

	/**
	 * 根据查询条件分页查询提前还款记录列表
	 */
	@Override
	public List<InadvanceRepayInfo> queryInadvanceRepayApplyListByPage(Map<String, Object> queryCondition, PageModel page) {
		List<InadvanceRepayInfo> resultList = new ArrayList<InadvanceRepayInfo>();

		String selectSql = " select "
				+ "	loansign.id as loanId, "
				+ " loansign.`type` as loanType, "
				+ " userbasicsinfo.id as userId, "
				+ " userbasicsinfo.name as userName,  "
				+ " userrelationinfo.phone, "
				+ " loansign.name as loanName, "
				+ " loansign.remonth as loanPeriod, "
				+ " loansign.issueLoan as loanAmount, "
				+ " (repaymentrecord.realMoney + repaymentrecord.middleRealMoney + repaymentrecord.afterRealMoney) as repayAmount, "
				+ " repaymentrecord.id as repayRecordId, "
				+ " repaymentrecord.periods repayPeriod, "
				+ " userfundinfo.cashBalance as balance, "
				+ " repaymentrecord.repayTime as applyTime ";
		
		String fromSql = " from loansign "
				+ " left outer join repaymentrecord on repaymentrecord.loanSign_id = loansign.id "
				+ " left outer join userbasicsinfo on userbasicsinfo.id = loansign.userbasicinfo_id "
				+ " left outer join userrelationinfo on userrelationinfo.user_id = userbasicsinfo.id "
				+ " left outer join userfundinfo on userfundinfo.id = loansign.userbasicinfo_id ";
		
		String whereSql = " where repaymentrecord.repayState = 6 "
				+ " and (repaymentrecord.realMoney + repaymentrecord.middleRealMoney + repaymentrecord.afterRealMoney) > 0 ";
		
		StringBuffer condition = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		// 电话号码
		String phone = (String) queryCondition.get("phone");
		if (!StringUtils.isEmpty(phone)) {
			condition.append(" and userrelationinfo.phone =  ? ");
			params.add(phone);
		}
		
		// 真实姓名
		String name = (String) queryCondition.get("name");
		if (!StringUtils.isEmpty(name)) {
			condition.append(" and userbasicsinfo.name like ? ");
			params.add("%" + name + "%");
		}
		
		String conditionSql = condition.toString();
		String orderSql = " order by repaymentrecord.repayTime desc ";
		
		String querySql = selectSql + fromSql + whereSql + orderSql;
		if (conditionSql.length() > 0) {
			querySql = selectSql + fromSql + whereSql + conditionSql + orderSql;
		}
		
		String countSql = " select count(*) from (" + "select loansign.id " + fromSql + whereSql + conditionSql + ") t ";
		List list = dao.pageListBySql(page, countSql, querySql, null, params.toArray());
				
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] arr = (Object[]) obj;
				InadvanceRepayInfo vo = new InadvanceRepayInfo();
				vo.setLoanId(StatisticsUtil.getLongFromBigInteger(arr[0]));
				
				int loanType = StatisticsUtil.getIntegerFromObject(arr[1]);
				vo.setLoanType(loanType);
				
				int loanPeriod = StatisticsUtil.getIntegerFromObject(arr[6]);
				int repayPeriod = StatisticsUtil.getIntegerFromObject(arr[10]);
				if (loanType == 2) {
					vo.setLoanPeriodDisplay(loanPeriod + "个月");
					vo.setRepayPeriodDisplay(repayPeriod + "个月");
				} else if (loanType == 3) {
					vo.setLoanPeriodDisplay(loanPeriod + "天");
					vo.setRepayPeriodDisplay(repayPeriod + "天");
				}
				
				vo.setUserName(StatisticsUtil.getStringFromObject(arr[3]));
				vo.setPhone(StatisticsUtil.getStringFromObject(arr[4]));
				vo.setLoanName(StatisticsUtil.getStringFromObject(arr[5]));
				vo.setLoanPeriod(loanPeriod);
				vo.setLoanAmount(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal) arr[7]));
				vo.setRepayAmount(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal) arr[8]));
				vo.setRepayPeriod(repayPeriod);
				vo.setBalance(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal) arr[11]));
				vo.setApplyTime(StatisticsUtil.getStringFromObject(arr[12]));
				vo.setUserId(StatisticsUtil.getLongFromBigInteger(arr[2]));
				vo.setRepayRecordId(StatisticsUtil.getLongFromBigInteger(arr[9]));
				
				resultList.add(vo);
			}
		}
		
		return resultList;
	}
}