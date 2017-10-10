package com.hc9.service;

import java.math.BigDecimal;
import java.util.List;

import com.hc9.dao.entity.Loansign;
import com.hc9.model.CalculateLoan;

/**
 * 
 * 有关标的一些算法的通用接口
 * 
 * @author ranqibing 2013-12-17
 * 
 */
public interface LoanSignFund {

    /**
     * 计算天标和普通标（提前还款）的实际使用天数=还款时间-放款时间
     * 
     * @param creditTime
     *            放款时间
     * @param repayTime
     *            还款时间
     * @author frank 2013-12-27
     * @return 天数
     */
    int reallyUseDay(String creditTime, String repayTime);

    /**
     * 计算流转标该阶段的使用天数 天数<30
     * 
     * @param tenderTime
     *            认购时间
     * @param preRepayDate
     *            预计还款时间
     * @return 天数
     */
    int reallyDay(String tenderTime, String preRepayDate);

    /**
     * 
     * 有关标的管理费的算法(针对借款人的)
	 * (2个月内管理费 = 借款本金*该比例)
	 * (2个月以上管理费 =借款本金*2个月内的费用比例+借款本金*（借款月数-2）*该比例)
	 * (天标管理费 = 借款金额*该比例*借款天数)
     * 
     * @param money
     *            利息
     * @param loan
     *            标的基本信息
     * @param isPrivilege
     *            是否为特权会员 (1是特权会员 0不是特权会员)
     * @param (管理费=该标的所得利息*管理费用比例)
     * @return 管理费
     */
    BigDecimal managementFee(BigDecimal money, Loansign loan,
			int isPrivilege);

    /**
     * 有关标的每一期的利息的算法
     * 
     * @param money
     *            借款标的金额
     * @param rate
     *            标的利率或年利率
     * @param periods
     *            借款期数(天数或月数)
     * @param type
     *            标的类型()1.普通标2 天标 3秒标 4 流转标 普通标 (利息=借款金额*年利率/12)[普通标中的等额本息除外] 天标
     *            (利息=借款金额*天利率*借款天数) 秒标 (利息=借款金额*利率) 流转标
     *            (利息=借款金额*年利率/365*每阶段的天数)
     * 
     * @return 利息
     */
    BigDecimal instalmentInterest(BigDecimal money, Double rate, int periods,
            int type);

    /**
     * 标的逾期利息 (逾期利息=借款金额*逾期的天数*平台定义的逾期利率)
     * 
     * @param date
     *            逾期天数
     * @param money
     *            逾期的金额
     * @return 利息
     */
    BigDecimal overdueInterest(BigDecimal money, Integer date);

    /**
     * * 针对标提前还款的利息算法(只针对普通标的‘到期一次性还本息’、天标) 普通标(提前还款的利息=借款金额*年利率/365*借款天数)
     * 天标(提前还款的利息=借款金额*天利率*借款天数)
     * 
     * @param money
     *            借款金额
     * @param rate
     *            普通标是年利率 天标是天利率
     * @param date
     *            借款天数
     * @param type
     *            1.普通标2. 天标 =loansign.loanType
     * @return 利息
     */
    BigDecimal advanceInterest(BigDecimal money, double rate, Integer date,
            Integer type);

    /**
     * 
     * 向流水账表中写入数据和修改当前用户余额 (如果插入的金额是支出money就需要传入一个负的金额,收入传入的就为正的金额)
     * 
     * @param money
     *            金额
     * @param typeId
     *            操作类型编号
     * @param date
     *            操作时间
     * @param userId
     *            用户编号
     * @param explen
     *            操作说明
     * @param withdrawId
     *            withdrawId
     * @param loansignId
     *            loansignId
     * @return 是否
     */
    boolean updateMoney(BigDecimal money, Long typeId, String date,
            Long userId, String explen, Long withdrawId, Long loansignId);

    /**
     * 
     * 向平台流水账中插入数据和修改当前平台余额 (如果插入的金额是支出money就需要传入一个负的金额,收入传入的就为正的金额)
     * 
     * @param money
     *            金额
     * @param typeId
     *            操作类型编号
     * @param date
     *            操作时间
     * @param explen
     *            操作说明
     * @param withdrawId
     *            withdrawId
     * @return 是否
     */
    boolean updatePlatformMoney(BigDecimal money, Long typeId, String date,
            String explen, Long withdrawId);

    /**
     * 等额本息每一期需要还的金额
     * 
     * @param issueLoan
     *            标的借款金额
     * @param rate
     *            标的利率
     * @param periods
     *            借款期数
     * @return 金额
     */
    BigDecimal total(BigDecimal issueLoan, double rate, int periods);

    /**
     * 计算等额本息每期需要偿还的利息和本金
     * 
     * @param issueLoan
     *            该借款标的借款金额
     * @param rate
     *            利率
     * @param periods
     *            借款期数
     * @return 返回该借款标每月偿还的本金和利息集合
     */
    List<CalculateLoan> loanCalculate(BigDecimal issueLoan, double rate,
            int periods);
    /**
     * 
     * 有关标的管理费的算法(针对投资人的)
     * 
     * @param money
     *            利息
     * @param loan
     *            标的基本信息
     * @param isPrivilege
     *            是否为特权会员 (1是特权会员 0不是特权会员)
     * @param (管理费=该标的所得利息*管理费用比例)
     * @return 管理费
     */
    BigDecimal managementCost(BigDecimal money, Loansign loan, int isPrivilege);

}
