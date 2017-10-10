package com.hc9.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.model.ExpensesInfo;
import com.hc9.model.RepaymentInvestor;

/**
 * 对还款信息资金进行封装(针对借款人)
 * 
 * @author Administrator 2018-8-26
 * 
 */
@Service
public class BorrowerFundService {

	@Resource
	private LoanSignFund loanSignFund;

	@Resource
	private LoanManageService loanManageService;

	@Resource
	private LoanSignQuery loanSignQuery;

	@Resource
	private UserInfoQuery userInfoQuery;

	private DecimalFormat df = new DecimalFormat("0.00");

	@Resource
	private RepaymentrecordService repaymentrecordService;

	/**
	 * 计算得到借款人还款的本金、利息、违约金
	 * 
	 * @param repaymentInfo
	 *            还款对象
	 * @return 返回资金对象
	 */
	public ExpensesInfo getBorrowerFund(Repaymentrecord repaymentInfo,
			int isPrivilege) {

		return this.getMonthlyInterest(repaymentInfo, isPrivilege);

	}
	

	/**
	 * 每月付息到期还本
	 * 
	 * @param repaymentInfo
	 *            还款对象
	 * @return 返回资金对象
	 */
	public ExpensesInfo getMonthlyInterest(Repaymentrecord repaymentInfo,
			int isPrivilege) {
		ExpensesInfo expensesInfo = new ExpensesInfo();
//		 年利率
//		Double interestRate = repaymentInfo.getLoansign().getPrioAwordRate() + repaymentInfo.getLoansign().getPrioAwordRate();
		// 违约金
		Double penalty = 0.00;
		// 当前距离还款日期的天数
		int timeNum = 0;
		try {
			timeNum = DateUtils.differenceDate("yyyy-MM-dd", DateUtils.format("yyyy-MM-dd"), repaymentInfo.getPreRepayDate());
			// 日期格式，放款时间，当前日期
			if (timeNum < 0) { // 逾期还款
				// 逾期违约的金额
				penalty = loanManageService.overdueRepayment(repaymentInfo .getLoansign().getIssueLoan(), Math.abs(timeNum));
				expensesInfo.setState(Constant.STATUES_FOUR);
			}else if(timeNum >0){
				expensesInfo.setState(Constant.STATUES_FIVE);
			}else { // 按时还款
				expensesInfo.setState(Constant.STATUES_TWO);
			}
			expensesInfo.setInterest(repaymentInfo.getPreRepayMoney());
			expensesInfo.setMoney(repaymentInfo.getMoney());
			expensesInfo.setIpsNumber(repaymentInfo.getLoansign() .getUserbasicsinfo().getUserfundinfo().getpIdentNo());
			expensesInfo.setLoanid(repaymentInfo.getLoansign().getId());
			expensesInfo.setManagement(0.00);
			expensesInfo.setPenalty(penalty);
			expensesInfo.setUserId(repaymentInfo.getLoansign() .getUserbasicsinfo().getId());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return expensesInfo;
	}

	
	

	/**
	 * 计算每笔借款借款者需要给平台的管理费
	 * 
	 * @param money
	 *            投资金额
	 * @param loan
	 *            借款标
	 * @return 返回管理费金额
	 */
	public Double getManagement(Double money, Loansign loan) {
		// 获取用户是否为vip会员
		boolean bool = userInfoQuery.isPrivilege(loan.getUserbasicsinfo());
		int isPrivilege = Constant.STATUES_ZERO;
		if (bool) {
			isPrivilege = Constant.STATUES_ONE;
		}
		// 得到管理费
		Double managementCost = loanSignFund.managementFee(
				new BigDecimal(money), loan, isPrivilege).doubleValue();
		return managementCost;
	}

	/**
	 * 得到借款人还款总额
	 * 
	 * @param repaymentInvestorInfos
	 * @return
	 */
	public String getRepmentSumMoney(List<RepaymentInvestor> infoList) {
		Double money = 0.0000;
		for (RepaymentInvestor info : infoList) {
			money += Double.parseDouble(info.getpInAmt());
		}
		return df.format(money);
	}

	/**
	 * 得到借款人总手续费
	 * 
	 * @param investorList
	 * @return
	 */
	public String getRepmentSumFee(List<RepaymentInvestor> investorList) {
		Double money = 0.0000;
		for (RepaymentInvestor info : investorList)
			for (int i = 0; i < investorList.size(); i++) {
				money += Double.parseDouble(info.getpOutInfoFee());
			}
		return df.format(money);
	}


}
