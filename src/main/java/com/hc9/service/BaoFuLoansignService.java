package com.hc9.service;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.hc9.common.constant.Constant;
import com.hc9.common.constant.PayURL;
import com.hc9.common.log.LOG;
import com.hc9.common.redis.HcFinancialActivitiCache;
import com.hc9.common.redis.HcNewyearActivitiCache;
import com.hc9.common.redis.HcPeachActivitiCache;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.RedisUtil;
import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.redis.activity.year2016.month05.HcNewerTaskCache;
import com.hc9.common.redis.sys.UserInfoCache;
import com.hc9.common.util.Arith;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.DateUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Bonus;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Generalize;
import com.hc9.dao.entity.Generalizemoney;
import com.hc9.dao.entity.InterestIncreaseCard;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Paylog;
import com.hc9.dao.entity.Recharge;
import com.hc9.dao.entity.RegBonus;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Repaymentrecordparticulars;
import com.hc9.dao.entity.Role;
import com.hc9.dao.entity.UserBank;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Usermessage;
import com.hc9.dao.entity.Userrelationinfo;
import com.hc9.dao.entity.VoteIncome;
import com.hc9.dao.entity.Withdraw;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.AcctTrans;
import com.hc9.model.Action;
import com.hc9.model.BidInfo;
import com.hc9.model.P2pQuery;
import com.hc9.model.P2pUserInfoQuery;
import com.hc9.model.RechargeInfo;
import com.hc9.model.RegisterInfo;
import com.hc9.model.WithdrawalInfo;
import com.hc9.model.crs;
import com.hc9.service.activity.ActivityAllInOneService;
import com.hc9.service.sms.ym.BaseSmsService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import freemarker.template.TemplateException;

@Service
public class BaoFuLoansignService {
	
	private static final Logger logger = Logger.getLogger(BaoFuLoansignService.class);
	
	@Resource
	private HibernateSupport dao;

	@Resource
	private PlankService plankService;

	@Resource
	private PayLogService payLogService;

	@Resource
	private UserInfoQuery userInfoQuery;

	@Resource
	private LoanSignQuery loanSignQuery;

	@Resource
	private UserInfoServices userInfoServices;

	@Resource
	private UserbasicsinfoService userbasicsinfoService;

	@Resource
	private BaoFuService baoFuService;

	@Resource
	private ProcessingService processingService;

	@Resource
	private LoanrecordService loanrecordService;

	@Resource
	private GeneralizeService generalizeService;

	@Resource
	private BonusService bonusService;

	@Resource
	private RegistrationService registrationService;
	
	@Resource
	private 	BaseSmsService baseSmsService;
	
	@Resource
	 private  LoanSignService loanSignService;  
	
	@Resource
	private LoanSignQuery loansignQuery;
	
	@Autowired
	private LoanSignFund loanSignFund;
	
	@Resource
	private BaseLoansignService baseLoansignService;
	
	@Resource
	private WithdrawServices withdrawServices;

	@Resource
	private ProcessingService processingservice;
	
	@Resource
	private RechargesService rechargesService;
	
	@Resource
	private GeneralizeMoneyServices generalizeMoneyServices;
	
	@Resource
	private UserBankService userBankService;
	
	@Resource
	private LoanManageService loanManageService;
	
	@Resource
	private BorrowerFundService borrowerFundService;
	
	@Resource
	private SmsService smsService;
	
	@Resource
	private ExpenseRatioService expenseRatioService;

	@Resource
	private RedEnvelopeDetailService redEnvelopeDetailService;
	
	@Resource
	private HccoindetailService hccoindetailService;
	
	@Resource
	private VoteincomeService voteincomeService;
	
	@Resource
	private ActivityService activityService;
	
	@Resource
	private DspService dspService;
	
	@Resource
	private InterestIncreaseCardService increaseCardService;
	
	@Resource
	private LoanInfoService loanInfoService;
	
	@Resource
	private MemberCenterService memberCenterService;
	
	@Resource
	private CacheManagerService cacheManagerService;
	
	@Resource
	private AppCacheService appCacheService;
	
	@Resource
	private HcMonkeyActivitiCache hcMonkeyActivitiCache;
	
	@Resource
	private HcFinancialActivitiCache hcFinancialActivitiCache;
	
	@Resource
	private ActivityMonkeyQueryService activityMonkeyQueryService;
	
	@Resource
	private BaoFuInvestService baoFuInvestService;
	
	@Resource
	private ActivityAllInOneService activityAllInOneService;
	
	/***
	 * 生成还款计划
	 * @param loansign
	 * @throws ParseException
	 */
	public void repayMentRecordLast(Loansign loansign, boolean isUpdate)throws ParseException {
		boolean isLastPeriod = false;// 最后一期
		double realRate = loansign.getRealRate() - loansign.getCompanyFee();
		Costratio costratio = loansignQuery.queryCostratio();
		// 总利息
		double sumMoney = Arith.round(Arith.mul(loansign.getIssueLoan(), realRate), 2);
		// 优先总利息
		double prioMoney = Arith.round(Arith.mul(loansign.getPriority(), loansign.getPrioRate()+ loansign.getPrioAwordRate()), 2);
		// 劣后总利息
		double afterMoney = Arith.round(Arith.mul(loansign.getAfter(), loansign.getAfterRate()), 2);
		// 夹层总利息
		double middleMoeny = Arith.sub(sumMoney,Arith.add(prioMoney, afterMoney));
		double middleRate = 0.00; // 夹层利率
		double afterRate = 0.00; // 劣后利率
		if (loansign.getMiddle() != 0) {
			// 计算夹层利率
			middleRate = Arith.round(Arith.div(middleMoeny, loansign.getMiddle()), 4);
		}
		// 判断是否大于23%
		if (middleRate > costratio.getMiddleRate()) {
			if (loansign.getType() == 2) {// '/**1-店铺 2-项目 3-天标 4-债权转让*/',
				// 按月付息到期还本
				if (loansign.getRefunway() == 1) {
					for (int i = 0; i < loansign.getRemonth(); i++) {
						Repaymentrecord record = new Repaymentrecord();
						// 实际利息
						double realityRate = Arith.round(Arith.div(sumMoney, Double.valueOf(12)), 2); // 每期实际利息 =总额 * 实际利率 /12
						// 应收公司服务费
						double companyFee = Arith.round(Arith.div(Arith.mul(loansign.getIssueLoan(),loansign.getCompanyFee()), Double.valueOf(12)), 2); // 公司服务费=总额*公司服务费年利率/12
						record.setPeriods(i + 1);
						record.setLoansign(loansign);
						record.setPreRepayDate(DateUtils.add(Constant.DEFAULT_DATE_FORMAT, Calendar.MONTH,i + 1));// 预计还款日期
						// TODO加入全部为优先或者全部为夹层的判断
						// 年利率+奖励年利率
						if (loansign.getPriority() != 0) {
							record.setPreRepayMoney(Arith.round(loanSignFund.instalmentInterest(new BigDecimal(loansign.getPriority()),loansign.getPrioRate()+ loansign.getPrioAwordRate(),loansign.getRemonth(), 1).doubleValue(), 2));// 预计还款利息
						} else {
							record.setPreRepayMoney(0.00);
						}
						if (i + 1 == loansign.getRemonth()) {	// 最后一期
							record.setMoney(loansign.getPriority());
							record.setMiddleMoney(loansign.getMiddle());
							record.setAfterMoney(loansign.getAfter());
							isLastPeriod = true;
							if (loansign.getMiddle() != 0.0) {
								double middlePrePay = Arith.round(Arith.div(Arith.mul(loansign.getMiddle(),costratio.getMiddleRate()), Double.valueOf(12)), 2);
								double afterPrePay = Arith.sub(realityRate,Arith.add(record.getPreRepayMoney(),middlePrePay));
								record.setMiddlePreRepayMoney(Arith.mul(middlePrePay, loansign.getRemonth()));// 预计夹层还款利息=夹层金额*0.23/12
								record.setAfterPreRepayMoney(Arith.mul(afterPrePay, loansign.getRemonth()));// 预计劣后还款利息=总利息-优先-夹层
							} else {
								record.setMiddlePreRepayMoney(0.0);
								record.setAfterPreRepayMoney(Arith.mul(Arith.sub(realityRate,record.getPreRepayMoney()),loansign.getRemonth()));// 预计劣后还款利息=总利息-优先-夹层
							}
							// 劣后利率=总利息/劣后总金额*12/loansign.getRemonth()
							afterRate = Arith.round(Arith.div(Arith.mul(Arith.div(record.getAfterPreRepayMoney(),loansign.getAfter()), 12), loansign.getRemonth()), 4);
						} else {
							record.setMoney(0.00);
							record.setMiddleMoney(0.00);
							record.setAfterMoney(0.00);
							record.setMiddlePreRepayMoney(0.00);
							record.setAfterPreRepayMoney(0.00);
						}
						record.setCompanyPreFee(companyFee); // 新增公司服务费
						record.setRepayState(1);// 未还款
						record.setAutoRepayAdvice(0);
						dao.save(record);
						List<Repaymentrecordparticulars> list = baseLoansignService.repaymentRecordsParticulars(record, isLastPeriod);
						baseLoansignService.verifyFundAllocated(record, list);
					}
				} else if (loansign.getRefunway() == 2) { // 按季还款

					Integer j = loansign.getRemonth() / 3;// 有几个季度
					for (int i = 1; i <= j; i++) {
						Repaymentrecord record = new Repaymentrecord();
						// 实际利息
						double realityRate = Arith.round(Arith.div(sumMoney, Double.valueOf(4)), 2); // 每期实际利息 =总额 * 实际利率 /4
						// 应收公司服务费
						double companyFee = Arith.round(Arith.div(Arith.mul(loansign.getIssueLoan(),loansign.getCompanyFee()), Double.valueOf(4)), 2); // 公司服务费=总额*公司服务费年利率/4
						record.setPeriods(i);
						record.setLoansign(loansign);
						record.setPreRepayDate(DateUtils.add(Constant.DEFAULT_DATE_FORMAT, Calendar.MONTH,i * 3));// 预计还款日期
						if (loansign.getPriority() != 0) {
							record.setPreRepayMoney(Arith.round(loanSignFund.instalmentInterest(new BigDecimal(loansign.getPriority()),loansign.getPrioRate()+ loansign.getPrioAwordRate(),loansign.getRemonth(), 2).doubleValue(), 2));// 预计还款利息
						} else {
							record.setPreRepayMoney(0.00);
						}
						if (i == j) {// 最后一个季度
							record.setMoney(loansign.getPriority());
							record.setMiddleMoney(loansign.getMiddle());
							record.setAfterMoney(loansign.getAfter());
							isLastPeriod = true;
							if (loansign.getMiddle() != 0.0) {
								double middlePrePay = Arith.round(Arith.div(Arith.mul(loansign.getMiddle(),costratio.getMiddleRate()), Double.valueOf(4)), 2);
								double afterPrePay = Arith.sub(realityRate,Arith.add(record.getPreRepayMoney(),middlePrePay));
								record.setMiddlePreRepayMoney(Arith.mul(middlePrePay, j));// 预计夹层还款利息=夹层金额*0.23/4
								record.setAfterPreRepayMoney(Arith.mul(afterPrePay, j));// 预计劣后还款利息=总利息-优先-夹层
							} else {
								record.setMiddlePreRepayMoney(0.0);
								record.setAfterPreRepayMoney(Arith.mul(Arith.sub(realityRate,record.getPreRepayMoney()), j));// 预计劣后还款利息=总利息-优先-夹层
							}
							// 劣后利率=总利息/劣后总金额*4/loansign.getRemonth()
							afterRate = Arith.round(Arith.div(Arith.mul(Arith.div(record.getAfterPreRepayMoney(),loansign.getAfter()), 4), loansign.getRemonth()), 4);
						} else {
							record.setMoney(0.00);
							record.setMiddleMoney(0.00);
							record.setAfterMoney(0.00);
							record.setMiddlePreRepayMoney(0.00);
							record.setAfterPreRepayMoney(0.00);
						}
						record.setCompanyPreFee(companyFee); // 新增公司服务费
						record.setRepayState(1);// 未还款
						record.setAutoRepayAdvice(0);
						dao.save(record);
						List<Repaymentrecordparticulars> list = baseLoansignService.repaymentRecordsParticulars(record, isLastPeriod);
						baseLoansignService.verifyFundAllocated(record, list);
					}
				}
			} else if (loansign.getType() == 3) {// 3-天标
				Repaymentrecord record = new Repaymentrecord();
				isLastPeriod = true;
				// 年化利息，乘以借款期限
				double realityRate = Arith.round(Arith.mul(Arith.div(sumMoney, Double.valueOf(360)), loansign.getRemonth()), 2); // 每期实际利息 =总额 * 实际利率 /4
				// 应收公司服务费，也是按照服务费率的年化利息计算，乘以借款期限
				double companyFee = Arith.round(Arith.mul(Arith.div(Arith.mul(loansign.getIssueLoan(),loansign.getCompanyFee()),Double.valueOf(360)),loansign.getRemonth()), 2);
				record.setPeriods(1);
				record.setLoansign(loansign);
				// 从满标审批之后开始计算，加上借款期限，就是预计还款日期
				record.setPreRepayDate(DateUtils.add(Constant.DEFAULT_DATE_FORMAT, Calendar.DAY_OF_MONTH,loansign.getRemonth()));// 预计还款日期
				record.setMoney(loansign.getPriority());
				record.setMiddleMoney(loansign.getMiddle());
				record.setAfterMoney(loansign.getAfter());
				if (loansign.getPriority() != 0) {
					record.setPreRepayMoney(Arith.round(loanSignFund.instalmentInterest(new BigDecimal(loansign.getPriority()),loansign.getPrioRate()+ loansign.getPrioAwordRate(),loansign.getRemonth(), 4).doubleValue(), 2));// 预计还款利息
				} else {
					record.setPreRepayMoney(0.00);
				}
				if (loansign.getMiddle() != 0.0) {
					double middlePrePay = Arith.round(Arith.mul(Arith.div(Arith.mul(loansign.getMiddle(),costratio.getMiddleRate()),Double.valueOf(360)),loansign.getRemonth()), 2);
					double afterPrePay = Arith.sub(realityRate,Arith.add(record.getPreRepayMoney(), middlePrePay));
					record.setMiddlePreRepayMoney(middlePrePay);// 预计夹层还款利息=夹层金额*0.23/360
					record.setAfterPreRepayMoney(afterPrePay);// 预计劣后还款利息=总利息-优先-夹层
				} else {
					record.setMiddlePreRepayMoney(0.0);
					record.setAfterPreRepayMoney(Arith.sub(realityRate,record.getPreRepayMoney()));// 预计劣后还款利息=总利息-优先-夹层
				}
				// 劣后利率=总利息/劣后总金额*360/loansign.getRemonth()
				afterRate = Arith.round(Arith.div(Arith.mul(Arith.div(record.getAfterPreRepayMoney(),loansign.getAfter()), 360), loansign.getRemonth()),4);
				record.setCompanyPreFee(companyFee); // 新增公司服务费
				record.setRepayState(1);// 未还款
				record.setAutoRepayAdvice(0);
				dao.save(record);
				List<Repaymentrecordparticulars> list = baseLoansignService.repaymentRecordsParticulars(record, isLastPeriod);
				baseLoansignService.verifyFundAllocated(record, list);
			}
			loansign.setMidRate(costratio.getMiddleRate());
			loansign.setAfterRate(afterRate);
			if (isUpdate) {
				loansign.setStatus(4);
			}
			dao.update(loansign); // 修改夹层、劣后利率
		} else {
			if (loansign.getType() == 2) {// /**1-店铺 2-项目 3-天标 4-债权转让*/',
				// 按月付息到期还本
				if (loansign.getRefunway() == 1) {
					for (int i = 0; i < loansign.getRemonth(); i++) {
						Repaymentrecord record = new Repaymentrecord();
						// 实际利息
						double realityRate = Arith.round(Arith.div(sumMoney, Double.valueOf(12)), 2); // 每期实际利息 =总额 * 实际利率 /12
						// 应收公司服务费
						double companyFee = Arith.round(Arith.div(Arith.mul(loansign.getIssueLoan(),loansign.getCompanyFee()), Double.valueOf(12)), 2); // 公司服务费=总额*公司服务费年利率/12
						record.setPeriods(i + 1);
						record.setLoansign(loansign);
						record.setPreRepayDate(DateUtils.add(Constant.DEFAULT_DATE_FORMAT, Calendar.MONTH,i + 1));// 预计还款日期
						// TODO加入全部为优先或者全部为夹层的判断
						// 年利率+奖励年利率
						if (loansign.getPriority() != 0) {
							record.setPreRepayMoney(Arith.round(loanSignFund.instalmentInterest(new BigDecimal(loansign.getPriority()),loansign.getPrioRate()+ loansign.getPrioAwordRate(),loansign.getRemonth(), 1).doubleValue(), 2));// 预计还款利息
						} else {
							record.setPreRepayMoney(0.00);
						}
						if (i + 1 == loansign.getRemonth()) {
							record.setMoney(loansign.getPriority());
							record.setMiddleMoney(loansign.getMiddle());
							record.setAfterMoney(loansign.getAfter());
							isLastPeriod = true;
							if (loansign.getMiddle() != 0.0) {
								double afterPrePay = Arith.round(Arith.div(afterMoney,Double.valueOf(12)), 2);
								double middlePrePay = Arith.sub(realityRate,Arith.add(record.getPreRepayMoney(),afterPrePay));
								record.setMiddlePreRepayMoney(Arith.mul(middlePrePay, loansign.getRemonth()));// 预计夹层还款利息=剩余夹层总利息/12
								record.setAfterPreRepayMoney(Arith.mul(afterPrePay, loansign.getRemonth()));// 预计劣后还款利息=劣后总利息/12
							} else {
								record.setMiddlePreRepayMoney(0.0);
								record.setAfterPreRepayMoney(Arith.mul(Arith.sub(realityRate,record.getPreRepayMoney()),loansign.getRemonth()));// 预计劣后还款利息=总利息-优先-夹层
							}
						} else {
							record.setMoney(0.00);
							record.setMiddleMoney(0.00);
							record.setAfterMoney(0.00);
							record.setMiddlePreRepayMoney(0.00);
							record.setAfterPreRepayMoney(0.00);
						}
						record.setCompanyPreFee(companyFee); // 新增公司服务费
						record.setRepayState(1);// 未还款
						record.setAutoRepayAdvice(0);
						dao.save(record);
						List<Repaymentrecordparticulars> list = baseLoansignService.repaymentRecordsParticulars(record, isLastPeriod);
						baseLoansignService.verifyFundAllocated(record, list);
					}
				} else if (loansign.getRefunway() == 2) { // 按季还款
					Integer j = loansign.getRemonth() / 3;// 有几个季度
					for (int i = 1; i <= j; i++) {
						Repaymentrecord record = new Repaymentrecord();
						// 实际利息
						double realityRate = Arith.round(Arith.div(sumMoney, Double.valueOf(4)), 2); // 每期实际利息 =总额 * 实际利率 /4
						// 应收公司服务费
						double companyFee = Arith.round(Arith.div(Arith.mul(loansign.getIssueLoan(),loansign.getCompanyFee()), Double.valueOf(4)), 2); // 公司服务费=总额*公司服务费年利率/4
						record.setPeriods(i);
						record.setLoansign(loansign);
						record.setPreRepayDate(DateUtils.add(
								Constant.DEFAULT_DATE_FORMAT, Calendar.MONTH,
								i * 3));// 预计还款日期
						if (loansign.getPriority() != 0) {
							record.setPreRepayMoney(Arith.round(loanSignFund.instalmentInterest(new BigDecimal(loansign.getPriority()),loansign.getPrioRate()+ loansign.getPrioAwordRate(),loansign.getRemonth(), 2).doubleValue(), 2));// 预计还款利息
						} else {
							record.setPreRepayMoney(0.00);
						}
						if (i == j) {// 最后一个季度
							record.setMoney(loansign.getPriority());
							record.setMiddleMoney(loansign.getMiddle());
							record.setAfterMoney(loansign.getAfter());
							isLastPeriod = true;
							if (loansign.getMiddle() != 0.0) {
								double afterPrePay = Arith.round(Arith.div(afterMoney,Double.valueOf(4)), 2);
								double middlePrePay = Arith.sub(realityRate,Arith.add(record.getPreRepayMoney(),afterPrePay));
								record.setMiddlePreRepayMoney(Arith.mul(middlePrePay, j));// 预计夹层还款利息=剩余夹层总利息/4
								record.setAfterPreRepayMoney(Arith.mul(afterPrePay, j));// 预计劣后还款利息=劣后总利息/4
							} else {
								record.setMiddlePreRepayMoney(0.0);
								record.setAfterPreRepayMoney(Arith.mul(Arith.sub(realityRate,record.getPreRepayMoney()), j));// 预计劣后还款利息=总利息-优先-夹层
							}
						} else {
							record.setMoney(0.00);
							record.setMiddleMoney(0.00);
							record.setAfterMoney(0.00);
							record.setMiddlePreRepayMoney(0.00);
							record.setAfterPreRepayMoney(0.00);
						}
						record.setCompanyPreFee(companyFee); // 新增公司服务费
						record.setRepayState(1);// 未还款
						record.setAutoRepayAdvice(0);
						dao.save(record);
						List<Repaymentrecordparticulars> list = baseLoansignService.repaymentRecordsParticulars(record, isLastPeriod);
						baseLoansignService.verifyFundAllocated(record, list);
					}
				}
			} else if (loansign.getType() == 3) {// 3-天标
				Repaymentrecord record = new Repaymentrecord();
				isLastPeriod = true;
				// 年化利息，乘以借款期限
				double realityRate = Arith.round(Arith.mul(Arith.div(sumMoney, Double.valueOf(360)), loansign.getRemonth()), 2); // 每期实际利息 =总额 * 实际利率 /4
				// 应收公司服务费，也是按照服务费率的年化利息计算，乘以借款期限
				double companyFee = Arith.round(Arith.mul(Arith.div(Arith.mul(loansign.getIssueLoan(),loansign.getCompanyFee()),Double.valueOf(360)),loansign.getRemonth()), 2);
				record.setPeriods(1);
				record.setLoansign(loansign);
				record.setPreRepayDate(DateUtils.add(Constant.DEFAULT_DATE_FORMAT, Calendar.DAY_OF_MONTH,loansign.getRemonth()));// 预计还款日期
				record.setMoney(loansign.getPriority());
				record.setMiddleMoney(loansign.getMiddle());
				record.setAfterMoney(loansign.getAfter());

				if (loansign.getPriority() != 0) {
					record.setPreRepayMoney(Arith.round(loanSignFund.instalmentInterest(new BigDecimal(loansign.getPriority()),loansign.getPrioRate()+ loansign.getPrioAwordRate(),loansign.getRemonth(), 4).doubleValue(), 2));// 预计还款利息
				} else {
					record.setPreRepayMoney(0.00);
				}
				if (loansign.getMiddle() != 0.0) {
					double afterPrePay = Arith.round(Arith.mul(Arith.div(afterMoney, Double.valueOf(360)), loansign.getRemonth()), 2);
					double middlePrePay = Arith.sub(realityRate,Arith.add(record.getPreRepayMoney(), afterPrePay));
					record.setMiddlePreRepayMoney(middlePrePay);// 预计夹层还款利息=剩余夹层总利息/360*天数
					record.setAfterPreRepayMoney(afterPrePay);// 预计劣后还款利息=劣后总利息/360
				} else {
					record.setMiddlePreRepayMoney(0.0);
					record.setAfterPreRepayMoney(Arith.sub(realityRate,record.getPreRepayMoney()));// 预计劣后还款利息=总利息-优先-夹层
				}
				record.setCompanyPreFee(companyFee); // 新增公司服务费
				record.setRepayState(1);// 未还款
				record.setAutoRepayAdvice(0);
				dao.save(record);
				List<Repaymentrecordparticulars> list = baseLoansignService.repaymentRecordsParticulars(record, isLastPeriod);
				baseLoansignService.verifyFundAllocated(record, list);
			}
			loansign.setMidRate(middleRate); // 夹层利率
			if (isUpdate) {
				loansign.setStatus(4);
			}
			dao.update(loansign); // 修改夹层利率loansign
		}
	}
	
	/***
	 * 本地循环投标
	 * @param user
	 * @param loanId
	 * @param money
	 * @param subType
	 * @param request
	 * @param response
	 * @return
	 */
	public synchronized String ipsGetLoanInfoUser(Userbasicsinfo user,Long loanId, Double money, Integer subType,HttpServletRequest request, HttpServletResponse response) {
		
		// 查询loansign表数据
		Loansign loan = loanSignQuery.getLoansignById(loanId.toString());
		// 判断剩余金额是否等0
		if (loan.getRestMoney() == 0) {
			return "6";
		}
		// 判断是否投自己发的项目
		if (loan.getUserbasicsinfo().getId().equals(user.getId())) {
			return "8";
		}
		// 剩余金额-购买金额
		Double restMoney = Arith.sub(loan.getRestMoney(), money);
		Double subMoney = 0.00;
		loan.setRestMoney(restMoney); // 借款剩余金额
		Integer isType = 0; // 0-默认 1-优先转夹层 2-夹层转优先
		// 根据投标类型进行处理
		if (subType == 3) { // 劣后
			if (user.getIsorgperson() != 1) { // 判断是否是机构投资人
				return "5";
			}
			// 判断劣后的金额是否等于0
			if (loan.getAfterRestMoney() < money) {
				return "7";
			} else {
				loan.setAfterRestMoney(Arith.sub(loan.getAfterRestMoney(),money));
			}
		} else {
			// 判断优先剩余金额+夹层剩余金额是否>=购买金额
			Double sumPrioRestAddMiddle = Arith.add(loan.getPrioRestMoney(),loan.getMidRestMoney());
			if (sumPrioRestAddMiddle >= money) {
				if (subType == 1) { // 优先
					if (loan.getPrioRestMoney() < money) { // 夹层转优先
						subMoney = Arith.sub(money, loan.getPrioRestMoney()); // 购买金额-优先剩余金额=差额
						loan.setMiddle(Arith.sub(loan.getMiddle(), subMoney)); // 夹层总金额-差额
						loan.setMidRestMoney(Arith.sub(loan.getMidRestMoney(),subMoney)); // 夹层剩余金额-差额
						loan.setPriority(Arith.add(loan.getPriority(), subMoney)); // 优先总金额+差额
						loan.setPrioRestMoney(0.00); // 优先剩余金额=0
						isType = 2;
					}
					if (loan.getPrioRestMoney() >= money) {
						loan.setPrioRestMoney(Arith.sub(loan.getPrioRestMoney(), money));
					}
				} else if (subType == 2) { // 夹层
					if (loan.getMidRestMoney() < money) { // 优先转夹层
						subMoney = Arith.sub(money, loan.getMidRestMoney()); // 购买金额-夹层剩余金额=差额
						loan.setPrioRestMoney(Arith.sub(loan.getPrioRestMoney(), subMoney)); // 优先剩余金额-差额
						loan.setPriority(Arith.sub(loan.getPriority(), subMoney)); // 优先剩余金额-差额
						loan.setMiddle(Arith.add(loan.getMiddle(), subMoney)); // 夹层总金额+差额
						loan.setMidRestMoney(0.00); // 夹层剩余金额=0
						isType = 1;
					}
					if (loan.getMidRestMoney() >= money) {
						loan.setMidRestMoney(Arith.sub(loan.getMidRestMoney(),money));
					}
				}
			} else {
				return "2";
			}
		}
		// 投标流水号
		String orderNum = "TB" + StringUtil.getDateTime(user.getId(), loan.getId());
		// 获取费用表的信息
		Costratio costratio = loanSignQuery.queryCostratio();
		// 计算服务费
		Double fee = Arith.mul(money, costratio.getLoanInvestment());
		// 保存购买记录信息
		Loanrecord loanrecord = new Loanrecord();
		loanrecord.setIsPrivilege(userInfoQuery.isPrivilege(user) ? Constant.STATUES_ONE: Constant.STATUES_ZERO); // 投标时，记录该投资者是否vip
		loanrecord.setIsSucceed(Constant.STATUES_ZERO); // 预购信息为 0 购买成功为 1
		loanrecord.setLoansign(loan);
		loanrecord.setFee(fee);
		loanrecord.setSubType(subType);
		loanrecord.setIsType(isType); // 0-默认 1-优先转夹层 2-夹层转优先
		loanrecord.setTenderMoney(money);
		loanrecord.setTenderTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		loanrecord.setUserbasicsinfo(user);
		loanrecord.setLoanType(loan.getType());
		loanrecord.setWebOrApp(1); // 1-web 2-app
		loanrecord.setOrder_id(orderNum);
		loanrecord.setSubMoney(subMoney);// 差额

		// 添加个人购买信息
		List<Action> listAction = new ArrayList<Action>();
		Action action = new Action(user.getpMerBillNo(), user.getName(), money);
		listAction.add(action);

		// 添加项目的信息
		BidInfo bidInfo = new BidInfo(loan, orderNum, fee, "1", listAction);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		try {
			loanSignQuery.saveOrUpdateLoanRecord(loanrecord, loan);
			String bidinfoXml = ParseXML.bidInfoXML(bidInfo);
			// 添加日志
			payLogService.savePayLog(bidinfoXml, user.getId(), loan.getId(), 4,orderNum, Double.valueOf(fee), 0.00, money); // 保存xml报文
			nvps.add(new BasicNameValuePair("requestParams", bidinfoXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(bidinfoXml+ "~|~" + ParameterIps.getmerchantKey())));
			String result = CommonUtil.excuteRequest(PayURL.REPAYMRNTTESTURL,
					nvps);
			System.out.println("项目投资业务查询=" + result);
			Document doc = DocumentHelper.parseText(result);
			Element rootElt = doc.getRootElement(); // 获取根节点
			System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
			// 拿到crs节点下的子节点code值
			String code = rootElt.elementTextTrim("code");
			String msg = rootElt.elementTextTrim("msg");
			String sign = rootElt.elementTextTrim("sign");
			String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
			if (sign.equals(Md5sign)) {
				// 获取投标信息
				Loanrecord loanRecordNum = loanrecordService.getLoanRecordOrderNum(orderNum);
				Paylog payLog = payLogService.queryPaylogByOrderSn(orderNum);
				if (code.equals("CSD000")) {
					try {
						
						if (loanRecordNum.getIsSucceed() == 0) {
							loanRecordNum.setIsSucceed(Constant.STATUES_ONE);
							loanRecordNum.setUpdateTime(DateUtils
									.format("yyyy-MM-dd HH:mm:ss"));
							// 投资按100元计1分
							Integer product = (int) (payLog.getAmount() / 100);
							plankService.saveAutointegralBuyProject(loanRecordNum.getUserbasicsinfo(),payLog.getAmount(),loanRecordNum.getSubType()); // 保存积分记录
							// 余额查询
							crs cr = baoFuService.getCasbalance(loanRecordNum.getUserbasicsinfo().getpMerBillNo());
							loanRecordNum.getUserbasicsinfo().getUserfundinfo().setCashBalance(cr.getBalance()); // 宝付的余额
							loanRecordNum.getUserbasicsinfo().getUserfundinfo().setOperationMoney(cr.getBalance());
							loanRecordNum.getUserbasicsinfo().setUserintegral(loanRecordNum.getUserbasicsinfo().getUserintegral() + product); // 积分计算
							// userbasicsinfoService.update(loanRecordNum.getUserbasicsinfo());
							// 银行流水
							Accountinfo account = new Accountinfo();
							account.setExpenditure(payLog.getAmount());
							if (loanRecordNum.getLoanType() == 2) {
								account.setExplan("项目购买");
							} else if (loanRecordNum.getLoanType() == 3) {
								account.setExplan("天标购买");
							}
							account.setIncome(0.00);
							account.setIpsNumber(orderNum);
							account.setLoansignId(loan.getId().toString());
							account.setFee(payLog.getFee());
							account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
							account.setUserbasicsinfo(loanRecordNum.getUserbasicsinfo());
							if (loanRecordNum.getLoanType() == 2) {
								account.setAccounttype(plankService.accounttype(5L));
							} else if (loanRecordNum.getLoanType() == 3) {
								account.setAccounttype(plankService.accounttype(15L));
							}
							account.setMoney(cr.getBalance());// 流水记录表
							plankService.update(loanRecordNum, account,loanRecordNum.getUserbasicsinfo(), loan);
							// 保存佣金
							generalizeService.saveGeneralizeMoney(loanRecordNum);
							// 判断是否融资成功
							Double tendMoney = loanSignQuery.getSumLoanTenderMoney(loan.getId().toString());
							Double subMoneyNum = Arith.sub(loan.getIssueLoan(),tendMoney);
							if (subMoneyNum == 0) {
								loan.setStatus(2); // 融资成功
								loan.setFullTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
								processingService.updateLoan(loan);
								Map<String, String> map = new HashMap<String, String>();
								map.put("loanNum", loan.getName());
								String content = smsService.getSmsResources("check-fullBid.ftl", map);
								int trigger=Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
								String[] phones=costratio.getBidPhone().split(",");
								for(int i=0;i<phones.length;i++){
									smsService.chooseSmsChannel(trigger, content, phones[i]);
								}
//								baseSmsService.sendSMS(content,costratio.getBidPhone());
							}
							// 更新支付报文信息
							payLogService.updatePayLog(orderNum,Constant.STATUES_ONE);
						}
						LOG.error("宝付项目购买处理成功");
						request.getSession().removeAttribute("loanMap");
						
						
						//TODO:此处插入cps
						
						return "110";
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("宝付项目购买处理成功---->平台数据处理失败-------->订单号----->"
								+ orderNum);
						return "WEB-INF/views/failure";
					}
				} else if (code.equals("CSD333")) {
					if (loanRecordNum.getIsSucceed() == 0) {
						// 剩余金额
						loan.setRestMoney(Arith.add(loan.getRestMoney(),loanRecordNum.getTenderMoney()));
						if (loanRecordNum.getSubType() == 1) { // 优先
							if (loanRecordNum.getIsType() == 0) { // 默认
								loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(),loanRecordNum.getTenderMoney()));
							} else if (loanRecordNum.getIsType() == 2) { // 夹层转优先
								Double moneyNum = Arith.sub(loanRecordNum.getTenderMoney(),loanRecordNum.getSubMoney()); // 购买金额-差额=优先剩余金额
								loan.setMiddle(Arith.add(loan.getMiddle(),loanRecordNum.getSubMoney())); // 夹层总额+差额
								loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(),loanRecordNum.getSubMoney())); // 夹层剩余金额+差额
								loan.setPriority(Arith.sub(loan.getPriority(),loanRecordNum.getSubMoney())); // 优先总额-差额
								loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(), moneyNum)); // 优先剩余金额
							}
						} else if (loanRecordNum.getSubType() == 2) { // 夹层
							if (loanRecordNum.getIsType() == 0) {
								loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(),loanRecordNum.getTenderMoney()));
							} else if (loanRecordNum.getIsType() == 1) { // 优先转夹层
								Double moneyNum = Arith.sub(loanRecordNum.getTenderMoney(),loanRecordNum.getSubMoney()); // 购买金额-差额=夹层剩余金额
								loan.setPriority(Arith.add(loan.getPriority(),loanRecordNum.getSubMoney())); // 优先总金额+差额
								loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(),loanRecordNum.getSubMoney())); // 优先剩余总金额+差额
								loan.setMiddle(Arith.sub(loan.getMiddle(),loanRecordNum.getSubMoney())); // 夹层总额-差额
								loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(), moneyNum)); // 夹层剩余金额
							}
						} else if (loanRecordNum.getSubType() == 3) { // 劣后
							loan.setAfterRestMoney(Arith.add(loan.getAfterRestMoney(),loanRecordNum.getTenderMoney()));
						}
						loanRecordNum.setIsSucceed(-1);
						loanRecordNum.setUpdateTime(DateUtils
								.format("yyyy-MM-dd HH:mm:ss"));
						loanSignQuery.updateLoanRecord(loanRecordNum, loan);
						// 更新支付报文信息
						payLogService.updatePayLog(orderNum, -1);
						LOG.error("宝付项目购买处理失败");
					}
					return "WEB-INF/views/failure";
				} else {
					LOG.error("宝付项目购买处理失败--订单号----->" + orderNum);
					return "WEB-INF/views/failure";
				}
			} else {
				LOG.error("非宝付项目购买返回数据---->订单号----->" + orderNum);
				return "WEB-INF/views/failure";
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("处理失败--订单号----->" + orderNum);
			return "WEB-INF/views/failure";
		}
	}
	
	/** 投资后更新用户相关缓存信息 */
	public void updateRedisInfoAfterInvest(Long userId, Long loanId) {
        final Long userIdFinal = userId;
        final String loanSignId = "" + loanId;
        try {
        	CacheManagerService.threadPoolExecutor.submit(
                	new Thread() {
                		public void run() {
                			LOG.error("投资更新缓存开始：" + Thread.currentThread());
                			cacheManagerService.updateIndexLoanList();//更新首页众持列表
                			cacheManagerService.updateTotalInvestMoney();//更新累计投资金额
                			memberCenterService.updateInvestStatisticInfo(userIdFinal);//投资概况
                			memberCenterService.updateBackMoneyStatisticInfo(userIdFinal);//更新用户待回款相关信息缓存
                			memberCenterService.repaymentBackList(userIdFinal);//用户的还款和回款还款信息更新
                			cacheManagerService.updateZhongChiPageLoanList();//更新我要众持列表页面
                			cacheManagerService.updateLoanDetailRelCache(loanSignId);//投资更新标详情信息
                			cacheManagerService.updateH5ZhongChiPageLoanList();//更新H5我要众持列表页面
                			cacheManagerService.updateH5HotIntroduceLoanList();//H5热门推荐列表相关缓存更新
                			LOG.error("投资更新缓存结束：" + Thread.currentThread());
                		}
                	}
                );
        	LOG.error("投资成功后更新缓存已请求处理，返回页面！");
        } catch(Exception e) {
        	LOG.error("投资成功后更新缓存出错！", e);
        }
	}
	
	/***
	 * 查询购买记录
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String ipsLoanInfoLoanHandle(Loanrecord loanRecord ) throws Exception {
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = df.format(new Date());
		Date d1 = df.parse(date);
		Date d2 = df.parse(DateUtil.addDateMinut(loanRecord.getTenderTime(), 10));
		if (d1.getTime() < d2.getTime()) {
			return "5";
		}
		// 获得项目记录
		Loansign loan = loanSignQuery.getLoansignById(loanRecord.getLoansign().getId().toString());
		Accountinfo account=getAccountinfo(loanRecord.getOrder_id().trim());
		if(account!=null){
			loanRecord.setIsSucceed(Constant.STATUES_ONE);
			loanRecord.setUpdateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
			updateLoanRecord(loanRecord);
			payLogService.updatePayLog(loanRecord.getOrder_id(),Constant.STATUES_ONE);
			return "1";
		}
		P2pQuery p2pQuery = new P2pQuery(loanRecord.getOrder_id(), 1);
		// 获取费用表的信息
		Costratio costratio = loanSignQuery.queryCostratio();
		InterestIncreaseCard increaseCard=increaseCardService.getLoanRecordCard(loanRecord.getId());
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		try {
			String loanXml = ParseXML.p2pQueryXml(p2pQuery);
			nvps.add(new BasicNameValuePair("requestParams", loanXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(loanXml+ "~|~" + ParameterIps.getmerchantKey())));
			String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL,nvps);
			LOG.error("项目投资业务查询=" + result);
			Document doc = DocumentHelper.parseText(result);
			Element rootElt = doc.getRootElement(); // 获取根节点
			// 拿到crs节点下的子节点code值
			String code = rootElt.elementTextTrim("code");
			String msg = rootElt.elementTextTrim("msg");
			String sign = rootElt.elementTextTrim("sign");
			// 获取子节点crs下的子节点result
			Iterator iteratorResult = rootElt.elementIterator("result");
			boolean Judge = false; // 判断是否有值
			String state = "0"; // 0-失败 1-成功
			while (iteratorResult.hasNext()) {
				Element itemEle = (Element) iteratorResult.next();
				Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
				while (iteratorOrder.hasNext()) {
					Element elementOrder = (Element) iteratorOrder.next();
					state = elementOrder.elementTextTrim("state");
					String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
					if (sign.equals(Md5sign)) {
						if (code.equals("CSD000")) {
							Judge = true;
							if (state.equals("1")) {
								loanRecord.setIsSucceed(Constant.STATUES_ONE);
								loanRecord.setUpdateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
								// 投资按100元计1分
								Integer product = (int) (loanRecord.getTenderMoney() / 100);
								plankService.saveAutointegralBuyProject(loanRecord.getUserbasicsinfo(),loanRecord.getTenderMoney(),loanRecord.getSubType()); // 保存积分记录
								// 余额查询
								crs cr = baoFuService.getCasbalance(loanRecord.getUserbasicsinfo().getpMerBillNo());
								loanRecord.getUserbasicsinfo().getUserfundinfo().setCashBalance(cr.getBalance()); // 宝付的余额
								loanRecord.getUserbasicsinfo().getUserfundinfo().setOperationMoney(cr.getBalance()); // 宝付的余额
								loanRecord.getUserbasicsinfo().setUserintegral(loanRecord.getUserbasicsinfo().getUserintegral() + product); // 积分计算

								if(account==null){
									// 银行流水
								     account = new Accountinfo();
								     if(loanRecord.getRedEnvelopeMoney()>0){
								    	     account.setExpenditure(Arith.sub(loanRecord.getTenderMoney(), loanRecord.getRedEnvelopeMoney()));
								     }else{
								    		account.setExpenditure(loanRecord.getTenderMoney());
								     }
									if (loanRecord.getLoanType() == 2) {
										account.setExplan("项目购买");
									} else if (loanRecord.getLoanType() == 3) {
										account.setExplan("天标购买");
									}
									account.setIncome(0.00);
									account.setIpsNumber(loanRecord.getOrder_id());
									account.setLoansignId(loan.getId().toString());
									account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
									account.setUserbasicsinfo(loanRecord.getUserbasicsinfo());
									if (loanRecord.getLoanType() == 2) {
										account.setAccounttype(plankService.accounttype(5L));
									} else if (loanRecord.getLoanType() == 3) {
										account.setAccounttype(plankService.accounttype(15L));
									}
									account.setMoney(cr.getBalance());// 流水记录表
								}
								
								boolean isDetail = false;
								//更新红包
								if(loanRecord.getRedEnvelopeMoney() > 0){
									isDetail = true;
									loan.setRedEnvelopeMoney(Arith.add(loan.getRedEnvelopeMoney(), loanRecord.getRedEnvelopeMoney()));
									redEnvelopeDetailService.updateRed(loanRecord, Constant.STATUES_ONE);
								}
								// 判断是否融资成功
								Double tendMoney = loanSignQuery.getSumLoanTenderMoney(loan.getId().toString());
								Double subMoney = Arith.sub(loan.getIssueLoan(), tendMoney);
								boolean sendFullSmsFlag = false;//是否发送满标短信
								if (subMoney == 0) {
									loan.setStatus(2); // 融资成功
									loan.setFullTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
									sendFullSmsFlag = true;
								}
								dao.update(loanRecord);
								dao.save(account);
								dao.update(loan);
							    dao.saveOrUpdate(loanRecord.getUserbasicsinfo());
								
								Userbasicsinfo userGen = generalizeService.queryPromoterByPromotedId(loanRecord.getUserbasicsinfo().getId());
								
								//lkl-20150811-添加员工推荐投资红筹币
								if(userGen!=null){
									 if (userGen.getUserType() == 2) {
										   hccoindetailService.saveHccoindetail(loanRecord, costratio.getHccoinRate(), userGen);
									}
								}
								boolean isCardId = false;
								//查询加息劵-lkl-20150825
								if(increaseCard != null){
									isCardId = true;
									increaseCard.setUseFlag(Constant.STATUES_ONE);
									increaseCard.setConsumeTime(loanRecord.getTenderTime());
									increaseCardService.uptIncreaseCard(increaseCard);
								}
								// 保存佣金
								if(userGen!=null){
									 if (userGen.getUserType() == 2) {
											generalizeMoneyServices.saveGeneralizemoney(loanRecord,costratio.getBusiness(), userGen.getId(),Constant.STATUES_TWO);
									 }else if(userGen.getUserType() == 4){
										 generalizeMoneyServices.saveGeneralizemoney(loanRecord,0.015, userGen.getId(),Constant.STATUES_TWO);
									 }else if(userGen.getUserType() == 6){
										 if(userGen.getIsAuthIps() == 1 && loanRecord.getUserbasicsinfo().getIsAuthIps() == 1){
											 // 理财师
											 generalizeMoneyServices.saveGeneralizemoney(loanRecord,costratio.getFinancial(), userGen.getId(),Constant.STATUES_SIX);
											 financial(userGen,loanRecord.getTenderMoney());
										 }
									 }
								}
								if(loanRecord.getUserbasicsinfo().getUserType()==2){
									  generalizeMoneyServices.saveGeneralizemoney(loanRecord,costratio.getBusiness(), loanRecord.getUserbasicsinfo().getId(),Constant.STATUES_TWO);
								}else if(loanRecord.getUserbasicsinfo().getUserType()==4){
									generalizeMoneyServices.saveGeneralizemoney(loanRecord,0.015, loanRecord.getUserbasicsinfo().getId(),Constant.STATUES_TWO);
								}
								
								//首次注册投资送加息卷，活动时间：2015.11.5-2015.12.5
								if(loanRecord.getSubType()==1){
									increaseCardService.saveInterestIncreaseCard(loanRecord);
					        	}
								// 判断是否融资成功
								if (sendFullSmsFlag) {
									Map<String, String> map = new HashMap<String, String>();
									map.put("loanNum", loan.getName());
									String content = smsService.getSmsResources("check-fullBid.ftl", map);
									int trigger=Integer.valueOf(SmsEmailCache.getSmsTriggerChannel());
									String[] phones=costratio.getBidPhone().split(",");
									for(int i=0;i<phones.length;i++){
										smsService.chooseSmsChannel(trigger, content, phones[i]);
									}
								}
								
								/** 投资成功给投资人发送短信 */
	                			baoFuInvestService.sendSmsToInvestorAfterSuccess(loanRecord.getUserbasicsinfo(), loan, loanRecord);
								// 更新支付报文信息
								payLogService.updatePayLog(loanRecord.getOrder_id(),Constant.STATUES_ONE);
								LOG.error("宝付项目投资查询处理成功--" + result+ "----->订单号----->"+ loanRecord.getOrder_id());
								
								/** 活动相关逻辑 */
								activityAllInOneService.activityBusinessLogicForInvestQuery(loan, loanRecord, 
										isDetail, isCardId);
							} else if (state.equals("0")) {
								// 剩余金额
								loan.setRestMoney(Arith.add(loan.getRestMoney(),loanRecord.getTenderMoney()));
								if (loanRecord.getSubType() == 1) { // 优先
									if (loanRecord.getIsType() == 0) { // 默认
										loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(),loanRecord.getTenderMoney()));
									} else if (loanRecord.getIsType() == 2) { // 夹层转优先
										Double money = Arith.sub(loanRecord.getTenderMoney(),	loanRecord.getSubMoney()); // 购买金额-差额=优先剩余金额
										loan.setMiddle(Arith.add(loan.getMiddle(),loanRecord.getSubMoney())); // 夹层总额+差额
										loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(),loanRecord.getSubMoney())); // 夹层剩余金额+差额
										loan.setPriority(Arith.sub(loan.getPriority(),loanRecord.getSubMoney())); // 优先总额-差额
										loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(), money)); // 优先剩余金额
									}
								} else if (loanRecord.getSubType() == 2) { // 夹层
									if (loanRecord.getIsType() == 0) {
										loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(),loanRecord.getTenderMoney()));
									} else if (loanRecord.getIsType() == 1) { // 优先转夹层
										Double money = Arith.sub(loanRecord.getTenderMoney(),loanRecord.getSubMoney()); // 购买金额-差额=夹层剩余金额
										loan.setPriority(Arith.add(loan.getPriority(),loanRecord.getSubMoney())); // 优先总金额+差额
										loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(),loanRecord.getSubMoney())); // 优先剩余总金额+差额
										loan.setMiddle(Arith.sub(loan.getMiddle(),loanRecord.getSubMoney())); // 夹层总额-差额
										loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(), money)); // 夹层剩余金额
									}
								} else if (loanRecord.getSubType() == 3) { // 劣后
									loan.setAfterRestMoney(Arith.add(loan.getAfterRestMoney(),loanRecord.getTenderMoney()));
								}
								loanRecord.setIsSucceed(-1);
								loanRecord.setUpdateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
								if(loanRecord.getRedEnvelopeMoney()>0){
									//更新红包
									redEnvelopeDetailService.updateRed(loanRecord, Constant.STATUES_ZERO);
								}
								
								//更新加息劵-lkl-20150825
								if(increaseCard!=null){
									 increaseCard.setUseFlag(Constant.STATUES_ZERO);
									 increaseCard.setLoanrecordId(null);
									 increaseCardService.uptIncreaseCard(increaseCard);
								}
								dao.update(loanRecord);
								dao.update(loan);
								// 更新支付报文信息
								payLogService.updatePayLog(loanRecord.getOrder_id(), -1);
								LOG.error("宝付项目购买处理失败");
							}
						} else if (code.equals("CSD333")) {
							// 更新支付报文信息
							return "-1";
						} else {
							LOG.error("宝付项目投资查询处理失败--" + result+ "----->订单号----->"+ loanRecord.getOrder_id());
							return "4";
						}
					} else {
						LOG.error("非宝付项目投资查询返回数据--" + result+ "----->订单号----->" + loanRecord.getOrder_id());
						return "3";
					}
				}
			}
			if (!Judge && state.equals("0")) {
				if (loanRecord.getIsSucceed() != -1) {
					// 剩余金额
					loan.setRestMoney(Arith.add(loan.getRestMoney(),loanRecord.getTenderMoney()));
					if (loanRecord.getSubType() == 1) { // 优先
						if (loanRecord.getIsType() == 0) { // 默认
							loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(),loanRecord.getTenderMoney()));
						} else if (loanRecord.getIsType() == 2) { // 夹层转优先
							Double money = Arith.sub(loanRecord.getTenderMoney(),loanRecord.getSubMoney()); // 购买金额-差额=优先剩余金额
							loan.setMiddle(Arith.add(loan.getMiddle(),loanRecord.getSubMoney())); // 夹层总额+差额
							loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(),loanRecord.getSubMoney())); // 夹层剩余金额+差额
							loan.setPriority(Arith.sub(loan.getPriority(),loanRecord.getSubMoney())); // 优先总额-差额
							loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(), money)); // 优先剩余金额
						}
					} else if (loanRecord.getSubType() == 2) { // 夹层
						if (loanRecord.getIsType() == 0) {
							loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(),loanRecord.getTenderMoney()));
						} else if (loanRecord.getIsType() == 1) { // 优先转夹层
							Double money = Arith.sub(loanRecord.getTenderMoney(),loanRecord.getSubMoney()); // 购买金额-差额=夹层剩余金额
							loan.setPriority(Arith.add(loan.getPriority(),loanRecord.getSubMoney())); // 优先总金额+差额
							loan.setPrioRestMoney(Arith.add(loan.getPrioRestMoney(),loanRecord.getSubMoney())); // 优先剩余总金额+差额
							loan.setMiddle(Arith.sub(loan.getMiddle(),loanRecord.getSubMoney())); // 夹层总额-差额
							loan.setMidRestMoney(Arith.add(loan.getMidRestMoney(), money)); // 夹层剩余金额
						}
					} else if (loanRecord.getSubType() == 3) { // 劣后
						loan.setAfterRestMoney(Arith.add(loan.getAfterRestMoney(),loanRecord.getTenderMoney()));
					}
					loanRecord.setIsSucceed(-1);
					loanRecord.setUpdateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
					if(loanRecord.getRedEnvelopeMoney()>0){
						//更新红包
						redEnvelopeDetailService.updateRed(loanRecord, Constant.STATUES_ZERO);
					}
					//更新加息劵-lkl-20150825
					if(increaseCard!=null){
						 increaseCard.setUseFlag(Constant.STATUES_ZERO);
						 increaseCard.setLoanrecordId(null);
						 increaseCardService.uptIncreaseCard(increaseCard);
					}
					
					loanSignQuery.updateLoanRecord(loanRecord, loan);
					// 更新支付报文信息
					payLogService.updatePayLog(loanRecord.getOrder_id(), -1);
					LOG.error("宝付项目购买处理失败");
				}
				return "-1";
			}
			return "1";
		} catch (Exception e) {
			LOG.error("宝付项目投资查询失败----->订单号----->" + loanRecord.getOrder_id()+" 错误： "+e);
			return "2";
		}
	}
	
	/** 新年抽奖活动按规则送抽奖次数 */
	private void lotteryGiveTime(long userId, int investType, Double priority,Double middle, 
			Userbasicsinfo referrer) {
		if(HcNewyearActivitiCache.validCurrentDate(new Date()) >= 0) {
			// 赠送投资人抽奖次数
			HcNewyearActivitiCache.giveLotteryChanceNumForInvest(userId, investType, priority,middle);
			// 活动期间注册并产生首投总送推介人一次抽奖机会
			if(referrer != null) {
				// 是否活动期间注册
				String beginTime = HcNewyearActivitiCache.getActiveBeginDate();
				String endTime = HcNewyearActivitiCache.getActiveEndDate();
				if(isUserRegisterdInActivityTimeArea(userId, beginTime, endTime)) {
					// 是否是首次投资
					boolean isTrue = getLoanRecord(userId);
					if(isTrue) {
						HcNewyearActivitiCache.increasePermanentLotteryChance(referrer.getId(), 1);
					}
				}
			}
		}
	}
	
	/** 新春猴给力活动 */
	private void moneyYearTime(Userbasicsinfo user, int investType, Double priority, Double middle, Loansign loan, Long loanRecordId) {
		// 优先和夹层
		if(investType == 1 || investType == 2) {
			Double investMoney = investType == 1 ? priority : middle;
			// 活动一
			if(loan.getActivityStatus() != null && loan.getActivityStatus()==1) {
				hcMonkeyActivitiCache.activityMonkeyMax(loan, user, investMoney, loanRecordId);
			}
	
			// 活动二/三
//			if(HcMonkeyActivitiCache.validCurrentDate(new Date()) >= 0) {
//				hcMonkeyActivitiCache.activityMonkeyWeek(loan, user, investMoney, loanRecordId);
//				hcMonkeyActivitiCache.activityMonkeyTotal(loan, user, investMoney, loanRecordId);
//			}
		}
	}
	
	/** 理财师活动 **/
	private void financial(Userbasicsinfo user, Double priority) {
		// 现金奖励
		if(HcFinancialActivitiCache.validCurrentDate(new Date()) >= 0) {
			try {
				hcFinancialActivitiCache.activityFinancial(user, priority);
			} catch (Exception e) {
				LOG.error("理财师活动异常----->",e);
			}
		}
	}
	
	/** 判断用户是否在活动期间注册 */
	public boolean isUserRegisterdInActivityTimeArea(Long userId, String beginTime, String endTime){
		String sql="select * from userbasicsinfo where  id=? and DATE_FORMAT(createTime, '%Y-%m-%d')>=DATE_FORMAT('" 
			+ beginTime + "', '%Y-%m-%d')  AND DATE_FORMAT(createTime, '%Y-%m-%d')<=DATE_FORMAT('" 
			+ endTime + "', '%Y-%m-%d') ";
		List<Userbasicsinfo> list = dao.findBySql(sql, Userbasicsinfo.class, userId);
		if(list.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/***
	 * 判断是否第一次投资，新增bonus奖励数据
	 * @param loanrecord
	 */
	public  void saveBonus(Loanrecord loanrecord){
		  boolean isTrue=getLoanRecord(loanrecord.getUserbasicsinfo().getId());
		  if(isTrue){
			     //得到推荐人
			    Userbasicsinfo user=generalizeService.queryPromoterByPromotedId(loanrecord.getUserbasicsinfo().getId());
			 	Bonus bonus=new Bonus();
				bonus.setUserId(loanrecord.getUserbasicsinfo().getId());
				bonus.setUserName(loanrecord.getUserbasicsinfo().getName());
				Double bonusMoney=0.00;  //被推荐人投资奖励
				Double genBonusMoney=0.00;//推荐人投资奖励
				if(loanrecord.getTenderMoney()>=100&&loanrecord.getTenderMoney()<1000){
					bonusMoney=5.00;
				}else if(loanrecord.getTenderMoney()>=1000&&loanrecord.getTenderMoney()<5000){
					bonusMoney=10.00;
					genBonusMoney=5.00;
				}else if(loanrecord.getTenderMoney()>=5000&&loanrecord.getTenderMoney()<10000){
					bonusMoney=25.00;
					genBonusMoney=15.00;
				}else if(loanrecord.getTenderMoney()>=10000&&loanrecord.getTenderMoney()<30000){
					bonusMoney=50.00;
					genBonusMoney=25.00;
				}else if(loanrecord.getTenderMoney()>=30000){
					bonusMoney=100.00;
					genBonusMoney=50.00;
				}
				bonus.setUserAmount(bonusMoney);
				bonus.setUserPhone(loanrecord.getUserbasicsinfo().getUserrelationinfo().getPhone());
				bonus.setUserState(Constant.STATUES_ZERO);
				bonus.setTenderMoney(loanrecord.getTenderMoney());
				bonus.setTenderTime(loanrecord.getTenderTime());
				if(user!=null){
					bonus.setGenUserId(user.getId());
					bonus.setGenUserName(user.getName());
					bonus.setGenUserAmount(genBonusMoney);
					if(genBonusMoney==0){
						//奖励为0
						bonus.setGenUserState(Constant.STATUES_TWO);
					}else{
						bonus.setGenUserState(Constant.STATUES_ZERO);
					}
				}else{
					//无推荐人
					bonus.setGenUserState(Constant.STATUES_THERE);
				}
				bonusService.save(bonus);
		  }
		  
	}
	
	/***
	 * 添加红包奖励
	 * isRed-是否使用红包 true不使用 false 使用
	 * 1、首次注册投资 必须注册时间在活动期间，投资人与推荐人才同时送红包
	 * 2、单笔投资如果已使用红包则不再赠送红包
	 * @param loanrecord
	 */
	public  void saveRedenvelopedetail(Loanrecord loanrecord,boolean isRed,Costratio costratio){
		  //是否首次注册
		  boolean isTrue=getLoanRecord(loanrecord.getUserbasicsinfo().getId());
		  if(isTrue){
			    boolean userRed=redEnvelopeDetailService.getRedEnvelopeDetails(loanrecord.getUserbasicsinfo().getId(),costratio);
			    if(userRed){
			    	//得到推荐人
					  Userbasicsinfo user=generalizeService.queryPromoterByPromotedId(loanrecord.getUserbasicsinfo().getId());
					  //首次投资红包
					  redEnvelopeDetailService.saveRedEnvelopeDetail(loanrecord, loanrecord.getUserbasicsinfo(), Constant.STATUES_ONE);
					  //推荐人红包
					  if(user!=null){
						  redEnvelopeDetailService.saveRedEnvelopeDetail(loanrecord, user, Constant.STATUES_THERE);
					  }
			    }
		  }else{
			  if(isRed){
				  boolean loanRecordRed=redEnvelopeDetailService.getUserLoanRecordRed(loanrecord.getId(), costratio);
				  if(loanRecordRed){
					  redEnvelopeDetailService.saveRedEnvelopeDetail(loanrecord, loanrecord.getUserbasicsinfo(), Constant.STATUES_ONE);
				  }
			  }
		  }
	}
	
	
	
	/***
	 * 判断是否第一次投资，新增bonus奖励数据
	 * @param loanrecord
	 */
	public  void saveUserBonus(Loanrecord loanrecord){
		  boolean isTrue=getLoanRecord(loanrecord.getUserbasicsinfo().getId());
		  if(isTrue){
			  boolean isBonus=getUserBonus(loanrecord.getUserbasicsinfo().getId());
			     //得到推荐人
			    Userbasicsinfo user=generalizeService.queryPromoterByPromotedId(loanrecord.getUserbasicsinfo().getId());
			 	Bonus bonus=new Bonus();
				bonus.setUserId(loanrecord.getUserbasicsinfo().getId());
				bonus.setUserName(loanrecord.getUserbasicsinfo().getName());
				bonus.setUserPhone(loanrecord.getUserbasicsinfo().getUserrelationinfo().getPhone());
				bonus.setUserState(Constant.STATUES_ZERO);
				bonus.setTenderMoney(loanrecord.getTenderMoney());
				bonus.setTenderTime(loanrecord.getTenderTime());
				Double bonusMoney=20.00;  //被推荐人投资奖励
				Double genBonusMoney=10.00;//推荐人投资奖励
				bonus.setUserAmount(bonusMoney);
				if(isBonus){
					if(user!=null){
						bonus.setGenUserId(user.getId());
						bonus.setGenUserName(user.getName());
						bonus.setGenUserAmount(genBonusMoney);
						bonus.setGenUserState(Constant.STATUES_ZERO);
					}else{
						//无推荐人
						bonus.setGenUserState(Constant.STATUES_THERE);
						bonus.setGenUserAmount(0.00);
					}
				}else{
					if(user!=null){
						bonus.setGenUserId(user.getId());
						bonus.setGenUserName(user.getName());
					}
					bonus.setGenUserState(Constant.STATUES_TWO);
					bonus.setGenUserAmount(0.00);
				}
				bonusService.save(bonus);
		  }
	}
	
	/** 8月8日至8月31日活动送现金规则
	 *  1、首笔投资500元或上送实时到账现金5元
	 *  2、在8月13、14日单笔投资1400元或以上，再送实时到账现金5元。
	 *  */
	public void saveUserBonusOfAuguest(Loanrecord loanrecord){
		Date currentDate = new Date();
		if(RedisUtil.isAugustActivity(currentDate)) {
			/** 手头送现金标识 */
			boolean firstInvestGgiveBonusFlag = false;
			boolean giveBonusFlag = false;
			/** 判断是否是首投 */
			boolean isTrue = getLoanRecord(loanrecord.getUserbasicsinfo().getId());
			if(isTrue) {
				/** 新注册用户首笔投资500元或上送实时到账现金5元 */
				String firstInvestBonusKey = "STR:HC9:AUG:ACT:FIR:INV:CNT:" + loanrecord.getUserbasicsinfo().getId();
				if(!RedisHelper.isKeyExist(firstInvestBonusKey)) {
					if(loanrecord.getTenderMoney() >= 500) {
						Long userId = loanrecord.getUserbasicsinfo().getId();
						String beginDate = RedisUtil.getAugustBeginDate();
						String endDate = RedisUtil.getAugustEndDate();
						if(userInfoQuery.isUserRegistedInTimeArea(userId, beginDate, endDate)) {
							firstInvestGgiveBonusFlag = true;
						}
					}
				}
			} else {
				/** 在8月13、14日单笔投资1400元或以上，再送实时到账现金5元。 */
				String nowStr = DateFormatUtil.dateToString(currentDate, "yyyy-MM-dd");
				if("2015-08-13".equals(nowStr) || "2015-08-14".equals(nowStr)) {
					/** 在8月13、14日新老用户单笔投资1400元或以上，额外再送现金5元到宝付账户，只限一次 */
					String giveBonus1400Key = "STR:HC9:AUG:ACT:1400:INV:CNT:" + loanrecord.getUserbasicsinfo().getId();
					if(!RedisHelper.isKeyExist(giveBonus1400Key)) {
						if(loanrecord.getTenderMoney() >= 1400) {
							giveBonusFlag = true;
						}
					}
				}
			}
			if(firstInvestGgiveBonusFlag || giveBonusFlag) {
				//得到推荐人
				Userbasicsinfo user=generalizeService.queryPromoterByPromotedId(loanrecord.getUserbasicsinfo().getId());
				Bonus bonus=new Bonus();
				bonus.setUserId(loanrecord.getUserbasicsinfo().getId());
				bonus.setUserName(loanrecord.getUserbasicsinfo().getName());
				bonus.setUserPhone(loanrecord.getUserbasicsinfo().getUserrelationinfo().getPhone());
				bonus.setUserState(Constant.STATUES_ZERO);
				bonus.setTenderMoney(loanrecord.getTenderMoney());
				bonus.setTenderTime(loanrecord.getTenderTime());
				bonus.setUserAmount(5.00);//投资人投资奖励
				if(user!=null){
					bonus.setGenUserId(user.getId());
					bonus.setGenUserName(user.getName());
				}
				bonus.setGenUserState(Constant.STATUES_TWO);
				bonus.setGenUserAmount(0.00);
				bonusService.save(bonus);
				if(firstInvestGgiveBonusFlag) {
					/** 首投投资计数 */
					String firstInvestBonusKey = "STR:HC9:AUG:ACT:FIR:INV:CNT:" + loanrecord.getUserbasicsinfo().getId();
					RedisHelper.incrBy(firstInvestBonusKey, 1);
				}
				if(giveBonusFlag) {
					String giveBonus1400Key = "STR:HC9:AUG:ACT:1400:INV:CNT:" + loanrecord.getUserbasicsinfo().getId();
					RedisHelper.incrBy(giveBonus1400Key, 1);
				}
			}
		}
	}
	
	/***
	 * 判断购买用户是否在在活动期间
	 * @param userId
	 * @return
	 */
	public boolean getUserBonus(Long userId){
		  String sql="select * from userbasicsinfo where  id=? and DATE_FORMAT(createTime, '%Y-%m-%d')>=DATE_FORMAT('20150623', '%Y-%m-%d')  AND DATE_FORMAT(createTime, '%Y-%m-%d')<=DATE_FORMAT('20150731', '%Y-%m-%d') ";
	      List<Userbasicsinfo> list=dao.findBySql(sql, Userbasicsinfo.class, userId);
		  	if(list.size()>0){
				return true;
			}else{
				return false;
			}
	}
	
	/***
	 * 判断是否已购买过
	 * @param userId
	 * @return
	 */
	public boolean getLoanRecord(Long userId){
		String sql="select * from loanrecord where userbasicinfo_id=? and isSucceed=1";
		List<Loanrecord> listLoanrecord=dao.findBySql(sql, Loanrecord.class, userId);
		if(listLoanrecord.size()==1){
			return true;
		}else{
			return false;
		}
	}
	
	/***
	 * 页面端投标
	 * @param loanId
	 * @param money
	 * @param subType
	 * @param request
	 * @param response
	 * @return
	 */
	public synchronized String getLoanInfoService(Long loanId, Double money,Integer subType, HttpServletRequest request,HttpServletResponse response) {
		// 获取当前用户
		Userbasicsinfo userbasicsinfo = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
		Userbasicsinfo user = userInfoServices.queryBasicsInfoById(userbasicsinfo.getId().toString());
		// 判断当前用户个人资金账户>购买金额
		if (user.getUserfundinfo().getCashBalance() < money) {
			return "1";
		}
		// 查询loansign表数据
		Loansign loan = loanSignQuery.getLoansignById(loanId.toString());
		// 判断剩余金额是否等0
		if (loan.getRestMoney() == 0) {
			return "6";
		}
		// 判断是否投自己发的项目
		if (loan.getUserbasicsinfo().getId().equals(user.getId())) {
			return "8";
		}
		// 剩余金额-购买金额
		Double restMoney = Arith.sub(loan.getRestMoney(), money);
		Double subMoney = 0.00;
		loan.setRestMoney(restMoney); // 借款剩余金额
		Integer isType = 0; // 0-默认 1-优先转夹层 2-夹层转优先
		// 根据投标类型进行处理
		if (subType == 3) { // 劣后
			if (user.getIsorgperson() != 1) { // 判断是否是机构投资人
				return "5";
			}
			// 判断劣后的金额是否等于0
			if (loan.getAfterRestMoney() < money) {
				return "7";
			} else {
				loan.setAfterRestMoney(Arith.sub(loan.getAfterRestMoney(),money));
			}
		} else {
			// 判断优先剩余金额+夹层剩余金额是否>=购买金额
			Double sumPrioRestAddMiddle = Arith.add(loan.getPrioRestMoney(),loan.getMidRestMoney());
			if (sumPrioRestAddMiddle >= money) {
				if (subType == 1) { // 优先
					if (loan.getPrioRestMoney() < money) { // 夹层转优先
						subMoney = Arith.sub(money, loan.getPrioRestMoney()); // 购买金额-优先剩余金额=差额
						loan.setMiddle(Arith.sub(loan.getMiddle(), subMoney)); // 夹层总金额-差额
						loan.setMidRestMoney(Arith.sub(loan.getMidRestMoney(),subMoney)); // 夹层剩余金额-差额
						loan.setPriority(Arith.add(loan.getPriority(), subMoney)); // 优先总金额+差额
						loan.setPrioRestMoney(0.00); // 优先剩余金额=0
						isType = 2;
					}
					if (loan.getPrioRestMoney() >= money) {
						loan.setPrioRestMoney(Arith.sub(loan.getPrioRestMoney(), money));
					}
				} else if (subType == 2) { // 夹层
					if (loan.getMidRestMoney() < money) { // 优先转夹层
						subMoney = Arith.sub(money, loan.getMidRestMoney()); // 购买金额-夹层剩余金额=差额
						loan.setPrioRestMoney(Arith.sub(loan.getPrioRestMoney(), subMoney)); // 优先剩余金额-差额
						loan.setPriority(Arith.sub(loan.getPriority(), subMoney)); // 优先剩余金额-差额
						loan.setMiddle(Arith.add(loan.getMiddle(), subMoney)); // 夹层总金额+差额
						loan.setMidRestMoney(0.00); // 夹层剩余金额=0
						isType = 1;
					}
					if (loan.getMidRestMoney() >= money) {
						loan.setMidRestMoney(Arith.sub(loan.getMidRestMoney(),money));
					}
				}
			} else {
				return "2";
			}
		}
		// 投标流水号
		String orderNum = "TB"  +StringUtil.getDateTime(user.getId(), loan.getId());
		// 获取费用表的信息
		Costratio costratio = loanSignQuery.queryCostratio();
		// 计算服务费
		Double fee = Arith.mul(money, costratio.getLoanInvestment());
		// 保存购买记录信息
		Loanrecord loanrecord = new Loanrecord();
		loanrecord.setIsPrivilege(userInfoQuery.isPrivilege(user) ? Constant.STATUES_ONE: Constant.STATUES_ZERO); // 投标时，记录该投资者是否vip
		loanrecord.setIsSucceed(Constant.STATUES_ZERO); // 预购信息为 0 购买成功为 1
		loanrecord.setLoansign(loan);
		loanrecord.setFee(fee);
		loanrecord.setSubType(subType);
		loanrecord.setIsType(isType); // 0-默认 1-优先转夹层 2-夹层转优先
		loanrecord.setTenderMoney(money);
		loanrecord.setTenderTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		loanrecord.setUserbasicsinfo(user);
		loanrecord.setLoanType(loan.getType());
		loanrecord.setWebOrApp(1); // 1-web 2-app
		loanrecord.setOrder_id(orderNum);
		loanrecord.setSubMoney(subMoney);// 差额

		// 添加个人购买信息
		List<Action> listAction = new ArrayList<Action>();
		Action action = new Action(user.getpMerBillNo(), user.getName(), money);
		listAction.add(action);

		// 添加项目的信息
		BidInfo bidInfo = new BidInfo(loan, orderNum, fee, "1", listAction);
		Map<String, String> map = null;
		try {
			loanSignQuery.saveOrUpdateLoanRecord(loanrecord, loan);
			String bidinfoXml = ParseXML.bidInfoXML(bidInfo);
			map = RechargeInfoService.loanBidCall(bidinfoXml,ParameterIps.getmerchantKey(), orderNum);
			// 添加日志
			payLogService.savePayLog(bidinfoXml, user.getId(), loan.getId(), 4,orderNum, Double.valueOf(fee), 0.00, money); // 保存xml报文
			map.put("url", PayURL.BIDTESTURL);
			request.getSession().setAttribute("map", map);
			return "3"; // 处理成功
		} catch (Exception e) {
			LOG.error(e);
			return "4"; // 处理失败
		}
	}
	
	/**
	 * 项目放款
	 * @param request
	 * @param id
	 * @param adminId
	 * @return
	 * @throws ParseException
	 */
	public String ipsLoanCreditService(HttpServletRequest request, String id,Long adminId) throws ParseException {
		// 获取后台操作人员信息
		if (adminId.equals("") && adminId.equals(null)) {
			Adminuser admin = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
			adminId = admin.getId();
		}
		// 获得项目信息
		Loansign loan = loanSignQuery.getLoansign(id);
		// 判断是否有待确认的
		if (loanSignService.hasWaitforConfirm(loan.getId())) {
			return "2";
		}
		// 获得购买总金额
		Double tenderMoney = loanSignService.getSumTenderMoney(loan.getId().toString());
		if (loan.getRestMoney() == 0) {
			Costratio costratio = loanSignQuery.queryCostratio();
			String orderNum = "MB" + StringUtil.getDateTime(loan.getUserbasicsinfo().getId(), loan.getId());
			// 计算费用
			Double fee =0.00;
			if (loan.getType() == 2) {
				if(loan.getRefunway()==4){  //等本等息
					fee=0.00;
				}else{
					Double borrowerFee= Arith.mul(tenderMoney, costratio.getBorrowerFee());
					fee = Arith.mul(borrowerFee, Double.valueOf(loan.getRemonth()));
				}
			}
			Action action = new Action(loan.getUserbasicsinfo().getpMerBillNo(), tenderMoney, 0);
			List<Action> listAction = new ArrayList<Action>();
			listAction.add(action);
			BidInfo bidInfo = new BidInfo(loan, orderNum, fee, "2", listAction);
			Map<String, String> map = null;
			try {
				loan.setState(6); // 已放款
				loan.setOrderSn(orderNum);
				String registerXml = ParseXML.bidInfoFull(bidInfo);
				loanSignService.updateLoangn(loan);
				payLogService.savePayLog(registerXml, adminId, loan.getId(), 5,orderNum, fee, 0.00, tenderMoney); // 保存xml报文
				map = RechargeInfoService.bidLoanFullCall(registerXml,ParameterIps.getmerchantKey());
				map.put("url", PayURL.BIDTESTURL);
				request.getSession().setAttribute("map", map);
				return "member/central.htm";
			} catch (Exception e) {
				e.printStackTrace();
				return "WEB-INF/views/failure";
			}
		} else {
			return "1";
		}
	}
	
	/***
	 *  项目流标
	 * @param request
	 * @param id
	 * @return
	 * @throws ParseException
	 */
	public String ipsLoanFlowService(HttpServletRequest request, String id)throws ParseException {
		// 获取后台操作人员信息
		Adminuser admin = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		// 获得项目信息
		Loansign loan = loanSignQuery.getLoansignById(id);
		// 判断是否有待确认的
		if (loanSignService.hasWaitforConfirm(loan.getId())) {
			return "1";
		}
		String orderNum = "LB" + StringUtil.getDateTime(loan.getUserbasicsinfo().getId(), loan.getId());
		List<Loanrecord> loanRecordList = loanSignService
				.getLoanRecordList(loan.getId().toString());
		if (loanRecordList.size() == 0) {
			loan.setStatus(9); // 已流标
			loan.setOrderSn(orderNum);
			loanSignService.updateLoangn(loan);
			payLogService.savePayLog("此项目无融资数据", admin.getId(), 6, orderNum,loan.getId()); // 保存xml报文
			return "2";
		}
		List<Action> listAction = new ArrayList<Action>();
		Double sumMoney = 0.00;
		for (int i = 0; i < loanRecordList.size(); i++) {
			Loanrecord loanRecord = loanRecordList.get(i);
			Double TenderMoney=loanRecord.getTenderMoney();
			if(loanRecord.getRedEnvelopeMoney()>0){
				TenderMoney=Arith.sub(loanRecord.getTenderMoney(), loanRecord.getRedEnvelopeMoney());
			}
			Action action = new Action(loanRecord.getUserbasicsinfo().getpMerBillNo(), loanRecord.getUserbasicsinfo().getName(),TenderMoney);
			listAction.add(action);
			sumMoney += TenderMoney;
		}
		BidInfo bidInfo = new BidInfo(loan, orderNum, 0.00, "3", listAction);
		Map<String, String> map = null;
		try {
			loan.setStatus(9); // 已流标
			loan.setOrderSn(orderNum);
			String registerXml = ParseXML.bidFullXML(bidInfo);
			loanSignService.updateLoangn(loan);
			payLogService.savePayLog(registerXml, admin.getId(), loan.getId(),6, orderNum, 0.00, 0.00, sumMoney);
			map = RechargeInfoService.bidLoanFlowCall(registerXml,ParameterIps.getmerchantKey());
			map.put("url", PayURL.BIDTESTURL);
			request.getSession().setAttribute("map", map);
			return "loanSign/central.htm";
		} catch (Exception e) {
			LOG.error("流标处理失败："+id,e);
			return "WEB-INF/views/failure";
		}
	}
	
	/***
	 * 项目审批/审核
	 * @param id
	 * @param request
	 * @param stateNum
	 * @param status
	 * @return
	 */
	public  String  loanSignAuditService(String id,HttpServletRequest request,String stateNum,Integer status){
		// 获取后台操作人员信息
		Adminuser admin = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		Loansign loan=loansignQuery.getLoansignById(id);
		try{
				    if(loan.getFeeMethod()==2&&loan.getFeeState()==2&&status==5){
		                 return "4";
					}
					if (StringUtil.isNotBlank(id)) {
					     loanSignService.updateLoansignState(id, stateNum, status, request);
				    }
					if(status==5){
						//判断是否有待确认的
						if(loanSignService.hasWaitforConfirm(loan.getId())){
							return "2";
						}
						// 获得购买总金额
						Double tenderMoney=loanSignService.getSumRedenvelopedetail(loan.getId().toString());
						//总额-奖励红包=实际购买
						Double redEnvelopeMoney=Arith.sub(loan.getIssueLoan(), loan.getRedEnvelopeMoney());
						if(!redEnvelopeMoney.equals(tenderMoney)){
							loan.setStatus(4);// 已放款
							loanSignService.updateLoangn(loan);
							return "5";
						}
						Double feeMoney=0.00;
						if(loan.getFeeMethod()==1){
							if(loan.getRefunway()!=4){
								feeMoney= Arith.round(loan.getFeeMoney(), 2);
							}
						}
						if (loan.getRestMoney() == 0) {
							String orderNum = "MB" + StringUtil.getDateTime(loan.getUserbasicsinfo().getId(), loan.getId());
							// 计算费用
							Action action = new Action(loan.getUserbasicsinfo().getpMerBillNo(), tenderMoney, 0);
							List<Action> listAction = new ArrayList<Action>();
							listAction.add(action);
							BidInfo bidInfo = new BidInfo(loan, orderNum,feeMoney, "2", listAction);
							Map<String, String> map = null;
							try {
								String time=DateUtil.format("yyyy-MM-dd HH:mm:ss");
								loan.setStatus(6);// 已放款
								loan.setAuditAdmin(admin.getId().toString());
								loan.setAuditTime(time);
								loan.setCreditTime(time);
								loan.setOrderSn(orderNum);
								String registerXml = ParseXML.bidInfoFull(bidInfo);
								logger.debug("满标放款，registerXml = ");
								logger.debug(registerXml);
								payLogService.savePayLog(registerXml, admin.getId(),loan.getId(), 5, orderNum,feeMoney, 0.00,tenderMoney); // 保存xml报文
								loanSignService.updateLoangn(loan);
								map = RechargeInfoService.bidLoanFullCall(registerXml,ParameterIps.getmerchantKey());
								map.put("url", PayURL.BIDTESTURL);
								request.getSession().setAttribute("map", map);
								return "loanSign/central.htm";
							} catch (Exception e) {
								LOG.error("满标放款读写模版失败："+id,e);
								return "WEB-INF/views/failure";
							}
						} else { 
							return "1";
						}
					}else{
						return "2";
					}
			}catch(Exception e){
				LOG.error("满标放款失败："+id,e);
				return "3";
	    }
	}
	
	/***
	 * 收取平台服务费
	 * @param id
	 * @param request
	 * @return
	 */
	public String loansignUpdateFeeService(String id,HttpServletRequest request){
		  // 获取后台操作人员信息
		    Adminuser admin = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		    Loansign loan=loansignQuery.getLoansignById(id);
			if (loan.getUserbasicsinfo().getUserfundinfo().getCashBalance() < loan.getFeeMoney()) {
				  return "3";   //可用金额不足
			}
			if(loan.getFeeState()!=2){
				return "4";
			}
			String orderNum = "FW" + StringUtil.getDateTime(loan.getUserbasicsinfo().getId(), loan.getId());// 收取平台服务费转账订单号
			AcctTrans acctTran = new AcctTrans();
			acctTran.setMerchant_id(ParameterIps.getCert());
			acctTran.setOrder_id(orderNum);
			acctTran.setPayer_user_id(loan.getUserbasicsinfo().getpMerBillNo());
			acctTran.setPayee_user_id(ParameterIps.getCert());// 收款
			acctTran.setPayer_type(0);
			acctTran.setPayee_type(1);// 收款
			acctTran.setAmount(loan.getFeeMoney());
			acctTran.setFee(0.00);
			acctTran.setFee_taken_on(1);
			acctTran.setReq_time(new Date().getTime());
			//修改平台服务费状态
			getLoanFeeStateSql(id);
			try {
				String registerXml = ParseXML.accttrans(acctTran);
				ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("requestParams",registerXml));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml + "~|~"+ ParameterIps.getmerchantKey())));
				payLogService.savePayLog(registerXml, loan.getUserbasicsinfo().getId(),loan.getId(), 21, orderNum, 0.00, 0.00,loan.getFeeMoney());
				String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
				result = result.replace("\"", "\'");
				crs cr = new crs();
				XStream xss = new XStream(new DomDriver());
				xss.alias(cr.getClass().getSimpleName(), cr.getClass());
				cr = (crs) xss.fromXML(result);
				String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~"+ cr.getMsg() + "~|~"+ ParameterIps.getDes_algorithm());
				if (cr.getSign().equals(Md5sign)) {
					if (cr.getCode().equals("CSD000")) {
							// 余额查询
						    Accountinfo accountOne = new Accountinfo();
						    accountOne.setExpenditure(loan.getFeeMoney());
						    accountOne.setExplan("平台收取服务费");
						    accountOne.setIncome(0.00);
						    accountOne.setIpsNumber(orderNum);
						    accountOne.setLoansignId(String.valueOf(loan.getId()));// 标id（项目id）
						    accountOne.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
						    accountOne.setAccounttype(plankService.accounttype(17L));
						    accountOne.setUserbasicsinfo(loan.getUserbasicsinfo());
						    accountOne.setMoney(0.00);
						    accountOne.setFee(0.00);
						    plankService.saveAccount(accountOne);// 添加流水账余额
							loan.setFeeState(Constant.STATUES_ONE);  //收取状态
							loan.setAdminFee(admin.getId());   //操作Id
							loanSignService.updateLoangn(loan);
							
							payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
					}
				}
			} catch (Exception e) {
				LOG.error("平台收取服务费报错"+id,e);
				return "2";
			} 
			return "1";
		} 
	
	/***
	 * 公共处理提现业务查询
	 * @param request
	 * @param wId
	 * @return
	 */
	public String returnWithdrawNumService(HttpServletRequest request, String wId){
		Withdraw withdraw = withdrawServices.selWithdraw(wId);
		P2pQuery p2pQuery = new P2pQuery(withdraw.getStrNum(), 6);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		try {
			String withdrawXml = ParseXML.p2pQueryXml(p2pQuery);
			nvps.add(new BasicNameValuePair("requestParams", withdrawXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(withdrawXml+ "~|~" + ParameterIps.getmerchantKey())));
			String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL,nvps);
			System.out.println("提现业务查询=" + result);
			Document doc = DocumentHelper.parseText(result);
			Element rootElt = doc.getRootElement(); // 获取根节点
			System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
			// 拿到crs节点下的子节点code值
			String code = rootElt.elementTextTrim("code");
			String msg = rootElt.elementTextTrim("msg");
			String sign = rootElt.elementTextTrim("sign");
			// 获取子节点crs下的子节点result
			Iterator iteratorResult = rootElt.elementIterator("result");
			boolean Judge = false; // 判断是否有值
			String state = "-1";
			// 遍历result节点下的Response节点
			while (iteratorResult.hasNext()) {
				Element itemEle = (Element) iteratorResult.next();
				Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
				while (iteratorOrder.hasNext()) {
					Element elementOrder = (Element) iteratorOrder.next();
					String order_id = elementOrder.elementTextTrim("order_id");
					state = elementOrder.elementTextTrim("state");
					String succ_amount = elementOrder.elementTextTrim("succ_amount");
					String succ_time = elementOrder.elementTextTrim("succ_time");
					String fee = elementOrder.elementTextTrim("fee");
					String baofoo_fee = elementOrder.elementTextTrim("baofoo_fee");
					String fee_taken_on = elementOrder.elementTextTrim("fee_taken_on");
					String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
					Double addFee=0.00;
					if (sign.equals(Md5sign)) {
						if (code.equals("CSD000")) {
							if (state.equals("1")) { // 转账成功
								withdraw.setState(Constant.STATUES_ONE);
								withdraw.setFee(Double.valueOf(baofoo_fee) != null ? Double.valueOf(baofoo_fee) :0.00);
								withdraw.setMer_fee(Double.valueOf(fee) != null ? Double.valueOf(fee) : 0.00);
								if(succ_amount==null){
									succ_amount="0";
								}
								withdraw.setWithdrawAmount(Double.valueOf(succ_amount) != null ? Double.valueOf(succ_amount)  : 0.00);
								withdraw.setApplytime(succ_time);
								withdraw.setCode(code);
								addFee=Arith.add(withdraw.getFee(),withdraw.getMer_fee());
								//withdraw.setFee_taken_on(Integer.valueOf(fee_taken_on));
								withdrawServices.updateWithdrawCardStatusAfterSuccess(
										withdraw.getUserbasicsinfo().getId(), withdraw.getStrNum());
							} else if (state.equals("5")) { // 转账处理中
								withdraw.setState(Constant.STATUES_FIVE);
							} else if (state.equals("0")) { // 初始化
								withdraw.setState(Constant.STATUES_ZERO);
							}
							if (state.equals("1")) {
								Userbasicsinfo obj = userbasicsinfoService.queryUserById(String.valueOf(withdraw.getUserbasicsinfo().getId()));
								// 余额查询
								crs cr = baoFuService.getCasbalance(obj.getpMerBillNo());
								obj.getUserfundinfo().setCashBalance(cr.getBalance());
								obj.getUserfundinfo().setOperationMoney(cr.getBalance());
								// 流水账
								Accountinfo account = new Accountinfo();
								account.setExpenditure(Double.valueOf(succ_amount));
								account.setExplan("提现");
								account.setIncome(0.00);
								account.setIpsNumber(order_id);
								account.setTime(succ_time);
								account.setUserbasicsinfo(obj);
								account.setAccounttype(plankService.accounttype(7L));
								account.setMoney(cr.getBalance());
								account.setFee(addFee);
								plankService.saveAccount(account);// 保存流水账余额
								userbasicsinfoService.update(obj);
								Judge = true;
							}
							// 更新支付报文信息
							payLogService.updatePayLog(order_id,Integer.valueOf(state),withdraw.getWithdrawAmount(),addFee);

							withdrawServices.uptWithdraw(withdraw);
							LOG.error("宝付支付提现查询处理成功");
						} else {
							LOG.error("宝付提现查询处理失败--" + result
									+ "----->订单号----->" + withdraw.getStrNum());
							return "0"; // 查询失败
						} 
					} else {
						LOG.error("非宝付提现查询返回数据--" + result + "----->订单号----->"
								+ withdraw.getStrNum());
						return "0"; // 查询失败
					}
				}
			}
			if (!Judge && state.equals("-1")) {
				if (withdraw.getState() != -1) {
					withdraw.setFee(0.00);
					withdraw.setState(-1);
					if(withdraw.getFeeState()==1){
						withdraw.getUserbasicsinfo().getUserfundinfo().setWithdrawMoney(Arith.sub(withdraw.getUserbasicsinfo().getUserfundinfo().getWithdrawMoney(), withdraw.getAmount()));
					}
					withdrawServices.uptWithdraw(withdraw);
					// 更新支付报文信息
					payLogService.updatePayLog(withdraw.getStrNum(),Integer.valueOf(state),withdraw.getWithdrawAmount(), withdraw.getFee());
					
					withdrawServices.updateWithdrawCardStatusAfterFailure(
							withdraw.getUserbasicsinfo().getId(), withdraw.getStrNum());
				}
			}
			return "1"; // 提现成功
		} catch (Exception e) {
			LOG.error("宝付提现查询失败----->订单号----->" + withdraw.getStrNum(),e);
			return "0"; // 查询失败
		}
	}
	
	/***
	 * 宝付提现处理
	 * @param request
	 * @param money
	 * @return
	 */
	public String ipsWithdrawService(HttpServletRequest request, Double money, String type) {
		// 得到当前用户信息
		Userbasicsinfo userbasics = userbasicsinfoService.queryUserById(((Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER)).getId());
		Costratio costratio = loanSignQuery.queryCostratio();
		Withdraw withdraw = new Withdraw();
		String ordernum = "TX" + StringUtil.getDateTime(userbasics.getId());
		// 手续费
		double fee=0;
		//默认不收取
		withdraw.setFeeState(Constant.STATUES_TWO);
		fee=withdrawServices.takeWithdrawFee(userbasics, money);
		if(fee>0){
			withdraw.setFeeState(Constant.STATUES_ONE);
		}
		withdraw.setStrNum(ordernum);
		withdraw.setAmount(money);
		withdraw.setUserbasicsinfo(userbasics);
		withdraw.setMer_fee(fee);
		withdraw.setState(Constant.STATUES_ZERO);
		withdraw.setFee_taken_on(costratio.getWithdrawMethod());
		if(HcPeachActivitiCache.validCurrentDate(new Date()) >= 0 && userbasics.getFee() == 0){
			withdraw.setFee_taken_on(1);
		}
		withdraw.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		processingservice.Forchargelogsave(withdraw);

		WithdrawalInfo withrawlInfo = new WithdrawalInfo();
		withrawlInfo.setOrder_id(ordernum);
		withrawlInfo.setUser_id(userbasics.getpMerBillNo());
		withrawlInfo.setAmount(money);
		withrawlInfo.setFee(fee);
		withrawlInfo.setFee_taken_on(costratio.getWithdrawMethod().toString());
		if(HcPeachActivitiCache.validCurrentDate(new Date()) >= 0 && userbasics.getFee() == 0){
			withrawlInfo.setFee_taken_on("1");
		}
		withrawlInfo.setMerchant_id(ParameterIps.getCert());
		Map<String, String> map = null;
		try {
			if(type != null){
				withrawlInfo.setPage_url(Constant.H5WITHDRAWAL);
				withrawlInfo.setReturn_url(Constant.H5WITHDRAWASYNCHRONOUS);
			}
			String registerXml = ParseXML.withdrawalXml(withrawlInfo);
			payLogService.savePayLog(registerXml, userbasics.getId(), 3, ordernum, 0,fee, 0.00, money);
			map = RechargeInfoService.withdrawalCall(registerXml,ParameterIps.getmerchantKey());
			if(type != null){
				map.put("page_url", Constant.H5WITHDRAWAL);
				map.put("service_url", Constant.H5WITHDRAWASYNCHRONOUS);
			}
			map.put("url", PayURL.WITHDRAWALTESTURL);
			request.getSession().setAttribute("map", map);
			return "1";
		} catch (Exception e) {
			LOG.error("宝付提现处理失败",e);
			return "2";
		}
	}
	
	/***
	 * 服务端提现
	 * @param request
	 * @param money
	 * @param bankId
	 * @return
	 * @throws Exception
	 */
	public  String ipsWithdrawPayServer(HttpServletRequest request, Double money,Long bankId)throws Exception{
		// 得到当前用户信息
		Userbasicsinfo userbasics = userbasicsinfoService.queryUserById(((Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER)).getId());
		if(userbasics==null){
			return "1"; //用户不存在
		}
		UserBank userBank=userBankService.getUserBankById(bankId);
		if(userBank==null){
			return "2"; //银行账户不存在
		}
		Costratio costratio = loanSignQuery.queryCostratio();
		Withdraw withdraw = new Withdraw();
		String ordernum = "TX" + StringUtil.getDateTime(userbasics.getId());
		// 手续费
		double fee=0;
		//默认不收取
		withdraw.setFeeState(Constant.STATUES_TWO);
		fee=withdrawServices.takeWithdrawFee(userbasics, money);
		if(fee>0){
			withdraw.setFeeState(Constant.STATUES_ONE);
		}
		withdraw.setStrNum(ordernum);
		withdraw.setAmount(money);
		withdraw.setUserbasicsinfo(userbasics);
		withdraw.setMer_fee(fee);
		withdraw.setState(Constant.STATUES_ZERO);
		withdraw.setFee_taken_on(costratio.getWithdrawMethod());
		if(HcPeachActivitiCache.validCurrentDate(new Date()) >= 0 && userbasics.getFee() == 0){
			withdraw.setFee_taken_on(1);
		}
		withdraw.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		processingservice.Forchargelogsave(withdraw);

		WithdrawalInfo withrawlInfo = new WithdrawalInfo();
		withrawlInfo.setOrder_id(ordernum);
		withrawlInfo.setUser_id(userbasics.getpMerBillNo());
		withrawlInfo.setAmount(money);
		withrawlInfo.setFee(fee);
		withrawlInfo.setFee_taken_on(costratio.getWithdrawMethod().toString());
		if(HcPeachActivitiCache.validCurrentDate(new Date()) >= 0 && userbasics.getFee() == 0){
			withrawlInfo.setFee_taken_on("1");
			userbasics.setFee(1);
		}
		withrawlInfo.setBank_no(userBank.getBank_no());
		String withrawlInfoXml = ParseXML.withdrawServceXml(withrawlInfo);
		payLogService.savePayLog(withrawlInfoXml, userbasics.getId(), 3, ordernum, withdraw.getId(),fee, 0.00, money);
		ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("requestParams",withrawlInfoXml));
		nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(withrawlInfoXml + "~|~"+ ParameterIps.getmerchantKey())));
		String result = CommonUtil.excuteRequest(PayURL.WITHDRAWSERVERTERURL, nvps);
    	LOG.error("提现业务查询="+result);
    	Document doc = DocumentHelper.parseText(result);
    	Element rootElt = doc.getRootElement(); // 获取根节点
        // 拿到crs节点下的子节点code值
        String code = rootElt.elementTextTrim("code"); 
        String sign =  rootElt.elementTextTrim("sign"); 
        String msg =  rootElt.elementTextTrim("msg"); 
        if(code.equals("CSD000")){
		        // 获取子节点crs下的子节点result
		        Iterator iteratorResult = rootElt.elementIterator("msg"); 
		        while (iteratorResult.hasNext()) {
		            Element elementOrder = (Element) iteratorResult.next();
		            String order_id = elementOrder.elementTextTrim("order_id"); 
		            String feeWith=elementOrder.elementTextTrim("fee");
		            String amount=elementOrder.elementTextTrim("amount");
		            String mer_fee=elementOrder.elementTextTrim("mer_fee");
		            String Md5sign = CommonUtil.MD5(code+ "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
		            Double addFee=0.00;
					if (sign.equals(Md5sign)) {
						    Withdraw withdrawInfo = withdrawServices.selWithdraw(withdraw.getId().toString());
						    String succ_time=DateUtils.format("yyyy-MM-dd HH:mm:ss");
						    withdrawInfo.setState(Constant.STATUES_ONE);
						    withdrawInfo.setFee(Double.valueOf(feeWith) != null ? Double.valueOf(feeWith) :0.00);
						    withdrawInfo.setMer_fee(Double.valueOf(mer_fee) != null ? Double.valueOf(mer_fee) : 0.00);
							if(amount==null){
								amount="0";
							}
							withdrawInfo.setWithdrawAmount(Double.valueOf(amount) != null ? Double.valueOf(amount)  : 0.00);
							withdrawInfo.setApplytime(succ_time);
							withdrawInfo.setCode(code);
							addFee=Arith.add(withdrawInfo.getFee(),withdrawInfo.getMer_fee());
							// 余额查询
							crs cr = baoFuService.getCasbalance(userbasics.getpMerBillNo());
							userbasics.getUserfundinfo().setCashBalance(cr.getBalance());
							userbasics.getUserfundinfo().setOperationMoney(cr.getBalance());
							// 流水账
							Accountinfo account = new Accountinfo();
							account.setExpenditure(withdrawInfo.getWithdrawAmount());
							account.setExplan("提现");
							account.setIncome(0.00);
							account.setIpsNumber(order_id);
							account.setTime(succ_time);
							account.setUserbasicsinfo(userbasics);
							account.setAccounttype(plankService.accounttype(7L));
							account.setMoney(cr.getBalance());
							account.setFee(addFee);
							plankService.saveAccount(account);// 保存流水账余额
							userbasicsinfoService.update(userbasics);
					}else{
						LOG.error("非宝付充值查询返回数据--" +result+ "----->订单号----->"+ order_id);
    					return "3";
					}
		        }
        }
        return "3";
	}
	
	/***
	 * 宝付充值查询
	 * @param request
	 * @param rId
	 * @param no
	 * @return
	 * @throws Exception
	 */
	public  String ipsRechargeNumService(HttpServletRequest request,String rId,Integer no)throws Exception{
		Recharge  recharge=rechargesService.selRecharge(rId);
	     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     String date=df.format(new Date());
		 Date d1 = df.parse(date);
		 Date d2=df.parse(DateUtil.addDateMinut(recharge.getTime(), 10));
	     if(d1.getTime()<d2.getTime()){
	    	 return "redirect:/recharge/rechargeRecord.htm?no="+no;
	     }
		P2pQuery p2pQuery=new  P2pQuery(recharge.getOrderNum(), 5);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		try{
				String withdrawXml = ParseXML.p2pQueryXml(p2pQuery);
		    	nvps.add(new BasicNameValuePair("requestParams", withdrawXml));
		    	nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(withdrawXml + "~|~" + ParameterIps.getmerchantKey())));
		    	String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL, nvps);
		    	LOG.error("充值业务查询="+result);
		    	Document doc = DocumentHelper.parseText(result);
		    	Element rootElt = doc.getRootElement(); // 获取根节点
	            // 拿到crs节点下的子节点code值
	            String code = rootElt.elementTextTrim("code"); 
	            String msg =  rootElt.elementTextTrim("msg"); 
	            String sign =  rootElt.elementTextTrim("sign"); 
	            // 获取子节点crs下的子节点result
	            Iterator iteratorResult = rootElt.elementIterator("result"); 
	            boolean Judge=false;  //判断是否有值
	            String state="0";
	            // 遍历result节点下的Response节点
                while (iteratorResult.hasNext()) {
                    Element itemEle = (Element) iteratorResult.next();
                    Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
                    while (iteratorOrder.hasNext()) {
	                        Element elementOrder = (Element) iteratorOrder.next();
	                        String order_id = elementOrder.elementTextTrim("order_id"); 
	                        state = elementOrder.elementTextTrim("state");  //1-成功  0-处理中
	                        String succ_amount=elementOrder.elementTextTrim("succ_amount");
	                        String succ_time=elementOrder.elementTextTrim("succ_time");
	                        String fee=elementOrder.elementTextTrim("fee");
	                        String baofoo_fee=elementOrder.elementTextTrim("baofoo_fee");
	                        String fee_taken_on=elementOrder.elementTextTrim("fee_taken_on");
	                        String Md5sign = CommonUtil.MD5(code+ "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
	        				if (sign.equals(Md5sign)) {
	        					   if(code.equals("CSD000")){
										if(state.equals("1")){
		        						    recharge.setFee(Double.valueOf(baofoo_fee)); // 宝付收取费用
			        						recharge.setMer_fee(Double.valueOf(fee)); // 商户收取的手续费
			        						recharge.setIncash_money(Arith.sub(Double.valueOf(succ_amount), Double.valueOf(baofoo_fee))); // 充值结算金额(实际到账)
			        						recharge.setCode(code);
			        						recharge.setStatus(Integer.parseInt(state));
			        						recharge.setSuccTime(succ_time);
			        						recharge.setFee_taken_on(Integer.valueOf(fee_taken_on));
											processingservice.updaterecharge(recharge);
											Userbasicsinfo user = userbasicsinfoService.queryUserById(recharge.getUserbasicsinfo().getId());
											// 流水账
											Accountinfo account = new Accountinfo();
											account.setExpenditure(0.00);
											account.setExplan("充值");
											account.setIncome(Double.valueOf(succ_amount));
											account.setIpsNumber(order_id);
											account.setTime(succ_time);
											account.setUserbasicsinfo(user);
											account.setAccounttype(plankService.accounttype(6L));
											account.setFee(recharge.getFee());
											// 余额查询
											crs cr = baoFuService.getCasbalance(user.getpMerBillNo());
											user.getUserfundinfo().setCashBalance(cr.getBalance());
											user.getUserfundinfo().setOperationMoney(cr.getBalance());
											account.setMoney(cr.getBalance());
											userbasicsinfoService.update(user);
											plankService.saveAccount(account);// 保存流水账余额
											// 更新支付报文信息
											payLogService.updatePayLog(recharge.getOrderNum(),Integer.parseInt(state),recharge.getIncash_money(), recharge.getFee());
											Judge=true;
											LOG.error("宝付充值查询处理成功");
										}
	        					   }
	        				}else{
	        					LOG.error("非宝付充值查询返回数据--" +result+ "----->订单号----->"+ recharge.getOrderNum());
	        					return "-1";
	        				}
	                 }
	            }
                if(!Judge&&state.equals("0")){
					   recharge.setStatus(-1);
					   recharge.setFee(0.00);
					   processingservice.updaterecharge(recharge);
					   payLogService.updatePayLog(recharge.getOrderNum(),-1,recharge.getIncash_money(), recharge.getFee());
                }
                return "redirect:/recharge/rechargeRecord.htm?no="+no;
	    }catch (Exception e) {
			  LOG.error("宝付充值查询失败----->订单号----->" +  recharge.getOrderNum(),e);
			 return "-2";
		 }
	}
	
	/***
	 * 充值处理方法
	 * @param request
	 * @param amount
	 * @param additional_info
	 * @return
	 */
	public String ipsRechargeService(HttpServletRequest request, Double amount,String additional_info, String type) {
		// 得到当前用户信息
		Userbasicsinfo userbasics = userbasicsinfoService.queryUserById(((Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER)).getId());
		if(userbasics.getpMerBillNo().equals("")||userbasics.getpMerBillNo()==null){
			return "3";
		}
		Costratio costratio = loanSignQuery.queryCostratio();
		String ordernum = "CZ" +  StringUtil.getDateTime(userbasics.getId());
		Double fee=Arith.mul(amount, costratio.getRecharge()) ;
		RechargeInfo rechargeInfo = new RechargeInfo();
		rechargeInfo.setMerchant_id(ParameterIps.getCert());
		rechargeInfo.setAmount(amount);
		rechargeInfo.setUser_id(userbasics.getpMerBillNo());
		rechargeInfo.setFee_taken_on(costratio.getRechargeMethod().toString());
		rechargeInfo.setFee(fee);
		rechargeInfo.setAdditional_info(additional_info);
		rechargeInfo.setOrder_id(ordernum);
		
		//生成初始化充值记录
		Recharge recharge =new Recharge();
		recharge.setRechargeAmount(amount);
		recharge.setUserbasicsinfo(userbasics);
		recharge.setOrderNum(ordernum);
		recharge.setStatus(Constant.STATUES_ZERO);
		if(costratio.getRechargeMethod()==1){
			recharge.setMer_fee(0.00);
		}else{
			recharge.setMer_fee(fee);
		}
		recharge.setFee_taken_on(costratio.getRechargeMethod());
		recharge.setAdditional_info(additional_info);
		recharge.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		processingservice.rechargelogsave(recharge);
		
		Map<String, String> map = null;
		try {
			if(type != null){
				rechargeInfo.setPage_url(Constant.H5RECHARGEURL);
				rechargeInfo.setReturn_url(Constant.H5ASYNCHRONISMRECHARGE);
			}
			String registerXml = ParseXML.rechargeXml(rechargeInfo);
			payLogService.savePayLog(registerXml, userbasics.getId(), 2, ordernum,0,fee,0.00,amount);
			map = RechargeInfoService.rechargeCall(registerXml, ParameterIps.getmerchantKey());
			if(type != null){
				map.put("page_url", Constant.H5RECHARGEURL);
				map.put("service_url", Constant.H5ASYNCHRONISMRECHARGE);
			}
			map.put("url", PayURL.RECHARGETESTURL);
			request.getSession().setAttribute("map", map);
			return "1";
		} catch (Exception e) {
			LOG.error("宝付充值模版加载报错----->订单号----->" +  recharge.getOrderNum(),e);
			return "2";
		}
	}
	/***
	 * 佣金转账
	 * @param request
	 * @param loanId
	 * @return
	 */
	  public   String ipsTransBonuses(HttpServletRequest request,String loanId){
			Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
			String sql="SELECT r.roleName FROM adminuser a JOIN role r ON a.role_id=r.id where a.id=?";
			String roleName=(String) dao.findObjectBySql(sql, loginuser.getId());
			if(roleName.indexOf("财务")<0 && roleName.indexOf("管理员")<0){
				return "4";
			}
//			if(loginuser.getId()!=2 && loginuser.getId()!=46){ //判断是否admin和财务人员
//				
//			}
	    	//根据标Id查询有关的佣金记录信息
	    	List<Generalizemoney>  listGeneralizeMoney=generalizeMoneyServices.getGeneralizemoneyList(loanId);
	    	if(listGeneralizeMoney.size()>0){
	    		for (int i = 0; i < listGeneralizeMoney.size(); i++) {
	    			Generalizemoney generalizemoney=listGeneralizeMoney.get(i);
	    			Userbasicsinfo userbasicsinfo = userbasicsinfoService.queryUserById(generalizemoney.getReferUserid());
	    			String orderNum = "YJ" +StringUtil.getDateTime(userbasicsinfo.getId(),Long.valueOf(loanId));// 佣金转账订单号
	    			AcctTrans acctTran = new AcctTrans();
					acctTran.setMerchant_id(ParameterIps.getCert());
					acctTran.setOrder_id(orderNum);
					acctTran.setPayer_user_id(ParameterIps.getCert());
					acctTran.setPayee_user_id(userbasicsinfo.getpMerBillNo());// 收款
					acctTran.setPayer_type(1);
					acctTran.setPayee_type(0);// 收款
					acctTran.setAmount(Double.valueOf(generalizemoney.getBonuses()));
					acctTran.setFee(0.00);
					acctTran.setFee_taken_on(1);
					acctTran.setReq_time(new Date().getTime());
					try {
						String registerXml = ParseXML.accttrans(acctTran);
						ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
						nvps.add(new BasicNameValuePair("requestParams",registerXml));
						nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml + "~|~"+ ParameterIps.getmerchantKey())));
						LOG.error("财务："+loginuser.getId()+"；项目ID："+loanId+"；正在发佣金~");
						payLogService.savePayLog(registerXml, userbasicsinfo.getId(),Long.parseLong(loanId), 20, orderNum, 0.00, 0.00,generalizemoney.getBonuses());
						String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
						result = result.replace("\"", "\'");
						generalizemoney.setTransOrderNo(orderNum);
						generalizemoney.setPayState(Constant.STATUES_TWO);
						generalizeMoneyServices.updateGeneralizeMoney(generalizemoney);
						crs cr = new crs();
						XStream xss = new XStream(new DomDriver());
						xss.alias(cr.getClass().getSimpleName(), cr.getClass());
						cr = (crs) xss.fromXML(result);
						String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~"+ cr.getMsg() + "~|~"+ ParameterIps.getDes_algorithm());
						if (cr.getSign().equals(Md5sign)) {
							if (cr.getCode().equals("CSD000")) {
								  if(transBonusesState(orderNum,loanId,userbasicsinfo.getId(),generalizemoney.getBonuses(),26)){
									  	generalizemoney.setPayState(Constant.STATUES_ONE);
									  	generalizemoney.setReleaseStatus(Constant.STATUES_ONE);
									  	generalizemoney.setReleaseTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
									  	generalizemoney.setPaidBonuses(generalizemoney.getBonuses());
									  	generalizemoney.setTransOrderNo(orderNum);
									  	generalizeMoneyServices.updateGeneralizeMoney(generalizemoney);
									    // 添加流水
										Accountinfo account = new Accountinfo();
										account.setExpenditure(0.00);
										account.setExplan("佣金转账");
										account.setIncome(generalizemoney.getBonuses());
										account.setIpsNumber(orderNum);
										account.setLoansignId(loanId);// 标id（项目id）
										account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
										account.setUserbasicsinfo(userbasicsinfo);
										account.setAccounttype(plankService.accounttype(16L));
										account.setMoney(0.00);
										payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
										plankService.saveAccount(account);// 添加流水账余额
										userbasicsinfoService.update(userbasicsinfo);
										System.out.println("佣金转账成功");
								  }else{
									  	generalizemoney.setPayState(-1);
									  	generalizeMoneyServices.updateGeneralizeMoney(generalizemoney);
									    System.out.println("佣金转账失败");
									    return "0";
								  }
							}else{
								generalizemoney.setPayState(-1);
								generalizemoney.setTransOrderNo("");
								generalizeMoneyServices.updateGeneralizeMoney(generalizemoney);
								return "3";
							}
						}
					} catch (Exception e) {
						LOG.error("佣金转账模版加载报错",e);
						return "-1";
					} 
				}
	    	}else{
	    		return "2";
	    	}
	    	return "1";
	    }
	  
	   public boolean transBonusesState(String orderNum,String loanId,Long userId,Double money,int action){
	    	P2pQuery p2pQuery = new P2pQuery(orderNum, 7);
	    	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			try {
				String transBonuesXml = ParseXML.p2pQueryXml(p2pQuery);
				nvps.add(new BasicNameValuePair("requestParams", transBonuesXml));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(transBonuesXml	+ "~|~" + ParameterIps.getmerchantKey())));
				payLogService.savePayLog(transBonuesXml, userId, Long.parseLong(loanId), action, orderNum,0.00,0.00, money);
				String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL,nvps);
				LOG.error("返回信息" + result);
				Document doc = DocumentHelper.parseText(result);
				Element rootElt = doc.getRootElement(); // 获取根节点
				String code = rootElt.elementTextTrim("code");
				String msg = rootElt.elementTextTrim("msg");
				String sign = rootElt.elementTextTrim("sign");
				// 获取子节点crs下的子节点result
				Iterator iteratorResult = rootElt.elementIterator("result");
				String state = "0"; // 0-失败 1-成功
				while (iteratorResult.hasNext()) {
					Element itemEle = (Element) iteratorResult.next();
					Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
					while (iteratorOrder.hasNext()) {
						Element elementOrder = (Element) iteratorOrder.next();
						state = elementOrder.elementTextTrim("state");
						String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
						if (sign.equals(Md5sign)) {
							if (code.equals("CSD000")) {
								if (state.equals("1")) {
									return true;
								}
							} else if (code.equals("CSD333")) {
								return false;
							} else {
								LOG.error("查询处理失败--" + result + "----->订单号----->"+ orderNum);
								return false;
							}
						} else {
							LOG.error("查询返回数据--" + result + "----->订单号----->"+ orderNum);
							return false;
						}
					}
				}
				return false;
			} catch (Exception e) {
				LOG.error("查询失败----->订单号----->" + orderNum,e);
				return false;
			}
	    }
	   
	   /***
		 * 根据loansign表查询放款是否成功
		 * @param id
		 * @param request
		 * @return
		 */
	   public  String ipsFullLoanNumService(String id,HttpServletRequest request){
			Loansign  loan=loanSignQuery.getLoansignById(id);
			Paylog payLog=payLogService.queryPaylogByOrderSn(loan.getOrderSn());
			P2pQuery p2pQuery=new  P2pQuery(loan.getOrderSn(), 2);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			    try{
			    	String shopFullxml = ParseXML.p2pQueryXml(p2pQuery);
			    	nvps.add(new BasicNameValuePair("requestParams", shopFullxml));
			    	nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(shopFullxml + "~|~" + ParameterIps.getmerchantKey())));
			    	String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL, nvps);
			    	LOG.error("项目放款业务查询="+result);
			    	Document doc = DocumentHelper.parseText(result);
			    	Element rootElt = doc.getRootElement(); // 获取根节点
		            // 拿到crs节点下的子节点code值
		            String code = rootElt.elementTextTrim("code"); 
	                String msg =  rootElt.elementTextTrim("msg"); 
	                String sign =  rootElt.elementTextTrim("sign"); 
	                // 获取子节点crs下的子节点result
		             Iterator iteratorResult = rootElt.elementIterator("result"); 
		             boolean Judge=false;  //判断是否有值
			         String state="0";  //0-失败   1-成功
		             while (iteratorResult.hasNext()) {
		                 Element itemEle = (Element) iteratorResult.next();
		                 Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
		                 while (iteratorOrder.hasNext()) {
			                        Element elementOrder = (Element) iteratorOrder.next();
			                        state = elementOrder.elementTextTrim("state");
			                        /* String order_id = elementOrder.elementTextTrim("order_id"); 
			                        String succ_amount=elementOrder.elementTextTrim("succ_amount");
			                        String succ_time=elementOrder.elementTextTrim("succ_time");
			                        String fee=elementOrder.elementTextTrim("fee");
			                        String baofoo_fee=elementOrder.elementTextTrim("baofoo_fee");
			                        String fee_taken_on=elementOrder.elementTextTrim("fee_taken_on");*/
			        	            String Md5sign = CommonUtil.MD5(code+ "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
			        				if (sign.equals(Md5sign)) {
				        					if (code.equals("CSD000")) {
				        						    Judge=true;
				        						    if(state.equals("1")){
															//查询余额
															crs cr = baoFuService.getCasbalance(loan.getUserbasicsinfo().getpMerBillNo());
															loan.getUserbasicsinfo().getUserfundinfo().setCashBalance(cr.getBalance());
															loan.getUserbasicsinfo().getUserfundinfo().setOperationMoney(cr.getBalance());
											                 //流水账
															getSaveAccount(loan, payLog, loan.getUserbasicsinfo(), loan.getOrderSn(), cr.getBalance());
															
														    userbasicsinfoService.update(loan.getUserbasicsinfo());
														    
														    //更新放款服务费
														    loan.setFee(payLog.getFee());
														    loan.setFeeState(Constant.STATUES_ONE);
														    loanSignQuery.updateLoansign(loan);
														    
														    payLogService.updatePayLog(loan.getOrderSn(), Constant.STATUES_ONE);
														    
															//红包转账
										            		if(loan.getRedEnvelopeMoney()>0){
										            			ipsTranCertHBZZ(loan);
											            	 }
														    
											            	//修改合同号
											            	List<Loanrecord> listLoanrecord=processingService.getLoanRecord(loan.getId());
											            	int count=processingService.getLoanRecordContractNoCount(loan);
											            	for(int i=0;i<listLoanrecord.size();i++){
											            		Loanrecord loanrecord=listLoanrecord.get(i);
											            		String number=StringUtil.addZeroForNum(String.valueOf(i+count+1),4);
											            		loanrecord.setpContractNo(loan.getLoansignType().getTypeName()+DateUtils.format("yyyy").substring(2, 4)+StringUtil.addZeroForNum(loan.getProjectNumber(), 4)+"-"+number);
											            		processingService.updateLoanrecord(loanrecord);
											            	}
											            	
											            	
						        							LOG.error("宝付支付项目放款查询处理成功");
				        						    }else{
				        								loan.setStatus(4);
				        								loanSignQuery.updateLoansign(loan);
				        							  	//更新支付报文信息
				        						    	payLogService.updatePayLog(loan.getOrderSn(),-1);
				        								LOG.error("宝付项目满标放款处理失败");
														return "-1";
				        						    }
				        					}else{
				        						LOG.error("宝付项目放款查询处理失败--" + result+ "----->订单号----->" + loan.getOrderSn());
				        						return "4";
				        					}
			        				}else{
			        					LOG.error("非宝付项目放款查询返回数据--" +result+ "----->订单号----->"+ loan.getOrderSn());
			        					return "3";
			                   }
		                 }
		             }
		             if(!Judge&&state.equals("0")){
			            	 if(payLog.getStatus()!=-1){
									loan.setStatus(4);
									loanSignQuery.updateLoansign(loan);
									//更新支付报文信息
									payLogService.updatePayLog(loan.getOrderSn(),-1);
									LOG.error("宝付项目满标放款处理失败");
									return "-1";
			            	 }
		             }
	                return "1";
		    }catch (Exception e) {
				  LOG.error("宝付项目放款查询失败----->订单号----->" + loan.getOrderSn(),e);
				 return "2";
			 }
		}
	   
	   /****
	    * 项目流标查询
	    * @param request
	    * @param id
	    * @return
	    */
	   public  String ipsLoanFlowNumService(HttpServletRequest request, String id){
			Loansign  loan=loanSignQuery.getLoansignById(id);
			Paylog payLog=payLogService.queryPaylogByOrderSn(loan.getOrderSn());
			P2pQuery p2pQuery=new  P2pQuery(loan.getOrderSn(), 3);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			    try{
			    	String shopFullxml = ParseXML.p2pQueryXml(p2pQuery);
			    	nvps.add(new BasicNameValuePair("requestParams", shopFullxml));
			    	nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(shopFullxml + "~|~" + ParameterIps.getmerchantKey())));
			    	String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL, nvps);
			    	LOG.error("项目流标业务查询="+result);
			    	Document doc = DocumentHelper.parseText(result);
			    	Element rootElt = doc.getRootElement(); // 获取根节点
		            // 拿到crs节点下的子节点code值
		             String code = rootElt.elementTextTrim("code"); 
		             String msg =  rootElt.elementTextTrim("msg"); 
		             String sign =  rootElt.elementTextTrim("sign"); 
		             // 获取子节点crs下的子节点result
		             Iterator iteratorResult = rootElt.elementIterator("result"); 
		             boolean Judge=false;  //判断是否有值
			         String state="0";  //0-失败   1-成功
		             while (iteratorResult.hasNext()) {
		                 Element itemEle = (Element) iteratorResult.next();
		                 Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
		                 while (iteratorOrder.hasNext()) {
			                        Element elementOrder = (Element) iteratorOrder.next();
			                        state = elementOrder.elementTextTrim("state");
			                        /* String order_id = elementOrder.elementTextTrim("order_id"); 
			                        String succ_amount=elementOrder.elementTextTrim("succ_amount");
			                        String succ_time=elementOrder.elementTextTrim("succ_time");
			                        String fee=elementOrder.elementTextTrim("fee");
			                        String baofoo_fee=elementOrder.elementTextTrim("baofoo_fee");
			                        String fee_taken_on=elementOrder.elementTextTrim("fee_taken_on");*/
			                        String Md5sign = CommonUtil.MD5(code+ "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
				           			 if (sign.equals(Md5sign)) {
					               					if (code.equals("CSD000")) {
					               						   Judge=true;
					               						   if(state.equals("1")){
					               							Userbasicsinfo userbasicsinfo = userInfoServices.queryBasicsInfoById(loan.getUserbasicsinfo().getId().toString());
															List<Loanrecord> loancordlist = loanrecordService.findLoanRecordList(loan.getId());
															for (Loanrecord loancord : loancordlist) {
																// 对流标的收款人操作
																Userbasicsinfo user = userInfoServices.queryBasicsInfoById(loancord.getUserbasicsinfo().getId().toString());
																crs cr = baoFuService.getCasbalance(user.getpMerBillNo());
																user.getUserfundinfo().setCashBalance(cr.getBalance());
																user.getUserfundinfo().setOperationMoney(cr.getBalance());
																Accountinfo accountOne = new Accountinfo();
																accountOne.setExpenditure(0.00);
																accountOne.setExplan("项目流标");
																accountOne.setIncome(loancord.getTenderMoney());
																accountOne.setIpsNumber(loan.getOrderSn());
																accountOne.setLoansignId(loan.getId().toString()); // 标id（项目id）
																accountOne.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
																accountOne.setUserbasicsinfo(user);
																accountOne.setAccounttype(plankService.accounttype(3L));
																accountOne.setMoney(cr.getBalance());
																plankService.saveAccount(accountOne);// 添加流水账余额
																userbasicsinfoService.update(user);
																//修改推广资金记录
																generalizeMoneyServices.updateGeneralizemoney(loancord.getId().toString(), -1);
															}
															
																//更新红包奖励
																redEnvelopeDetailService.listRedEnvelopeDetails(loan.getId());
															
																//更新加息卷 lkl-20150825
																increaseCardService.uptIncreaseCard(loan.getId());
															
																// 流水账
															    Accountinfo account = new Accountinfo();
																account.setExpenditure(payLog.getAmount());
																account.setExplan("项目流标");
																account.setIncome(0.00);
																account.setIpsNumber(loan.getOrderSn());
																account.setLoansignId(loan.getId().toString());// 标id（项目id）
																account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
																account.setUserbasicsinfo(userbasicsinfo);
																account.setAccounttype(plankService.accounttype(3L));
																// 查询余额
																crs cr = baoFuService.getCasbalance(userbasicsinfo.getpMerBillNo());
																userbasicsinfo.getUserfundinfo().setCashBalance(cr.getBalance());
																userbasicsinfo.getUserfundinfo().setOperationMoney(cr.getBalance());
																account.setMoney(cr.getBalance());
																plankService.saveAccount(account);// 添加流水账余额
																userbasicsinfoService.update(userbasicsinfo);
																payLogService.updatePayLog(loan.getOrderSn(),Constant.STATUES_ONE);
						       									LOG.error("宝付支付项目流标查询处理成功");
					               						   }else{
						            							loan.setStatus(1);
						            							loanSignQuery.updateLoansign(loan);
						            							payLogService.updatePayLog(loan.getOrderSn(),-1);
						            							LOG.error("宝付项目流标查询处理失败");
					               						   }
					               					}else if (code.equals("CSD333")){
						        							loan.setStatus(1);
						        							loanSignQuery.updateLoansign(loan);
						        							payLogService.updatePayLog(loan.getOrderSn(),-1);
						        							LOG.error("宝付项目流标查询处理失败");
						               						return "-1";
					               					}else{
					               						LOG.error("宝付项目流标查询处理失败--" + result+ "----->订单号----->" + loan.getOrderSn());
					               						return "4";
					               					}
				           				}else{
				           					LOG.error("非宝付店铺流标查询返回数据--" +result+ "----->订单号----->"+ loan.getOrderSn());
				           					return "3";
				       	              }
		                   }
		              }
		             if(!Judge&&state.equals("0")){
		            	   if(payLog.getStatus()!=-1){
			            	    	//更新支付报文信息
								loan.setStatus(1);
								loanSignQuery.updateLoansign(loan);
								payLogService.updatePayLog(loan.getOrderSn(),-1);
								LOG.error("宝付项目流标查询处理失败");
		      					return "-1";
		            	   }
		             }
		             return "1";
		    }catch (Exception e) {
				  LOG.error("宝付项目流标查询失败----->订单号----->" + loan.getOrderSn(),e);
				 return "2";
			 }
		}
	   
	   /***
	    * 注册奖励
	    * @param request
	    * @param response
	    * @param user
	    * @return
	    */
	   public String regBonusService(HttpServletRequest request,HttpServletResponse response,List<RegBonus> records){
		   Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
			if(loginuser.getId()!=2&&loginuser.getId()!=18&&loginuser.getId()!=25){ //判断是否admin和财务人员
				return "3";
			}
			if(records.size()==0){
				return "4";
			}else{
					for(int i=0;i<records.size();i++){
						RegBonus regBonus=records.get(i);
					//获取推荐人宝付帐号
						if(regBonus.getReferrer().getpMerBillNo()==null||regBonus.getReferrer().getpMerBillNo().equals("")){
							continue;
						}
						
						if(!transferBonus(regBonus.getReferrer().getId(), regBonus.getBouns(),2,regBonus.getId())){
							continue;
						}
						
						String content="【前海红筹】亲爱的"
								+ regBonus.getReferrer().getName()
								+ "您好！感谢您参与前海红筹“首次推荐注册返现”活动，小9已将现金奖励"+regBonus.getBouns()+"元给您送到，快登陆“前海红筹-个人中心”查收一下吧！如有疑问，欢迎致电我们的客服mm~400-822-3499。";
						try {
							// 用户系统消息
							Usermessage userMessage = new Usermessage();
							userMessage.setUserbasicsinfo(regBonus.getReferrer());
							userMessage.setContext(content);
							// 未读
							userMessage.setIsread(0);
							// 发送时间
							userMessage.setReceivetime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
							// 发送主题
							userMessage.setTitle("首次注册返现活动");
							// 保存系统消息
							bonusService.saveUserMesssage(userMessage);
							regBonus.setReleaseStatus(Constant.STATUES_ONE);
							regBonus.setAdminuser(loginuser);
							regBonus.setReleaseTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
							bonusService.updateReg(regBonus);
							LOG.info("转帐成功");
						} catch (Exception e) {
							LOG.error("转帐失败",e);
							return "2";
						}
					}
			}
		   return "1";
	   }
	   
	   /***
	    * 推荐奖励发放
	    * @param request
	    * @param response
	    * @param user
	    * @return
	    */
		public String  releaseBonusService(HttpServletRequest request,HttpServletResponse response,Userbasicsinfo user,Integer status){
			Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
			if(loginuser.getId()!=2&&loginuser.getId()!=18){ //判断是否admin和财务人员
				return "3";
			}
			List<Bonus> records=bonusService.queryBonus(user, status);
			if(records.size()==0){
				return "4";
			}else{
					for(int i=0;i<records.size();i++){
						Bonus bonus=records.get(i);
						//被推荐人奖励
						Long userId=bonus.getUserId();
						String userName=bonus.getUserName();
						Double amount=bonus.getUserAmount();
						if(status==2){  //推荐人奖励
							 userId=bonus.getGenUserId();
							 userName=bonus.getGenUserName();
							 amount=bonus.getGenUserAmount();
						}
						Userbasicsinfo userInfo=userbasicsinfoService.queryUserById(userId);
						if(userInfo.getpMerBillNo()==null){
							continue;
						}
						if(!transferBonus(userId, amount,1,bonus.getId())){
							continue;
						}
						String content="【前海红筹】亲爱的"
								+ userName
								+ "您好！感谢您参与前海红筹“首次投资返现”活动，小9已将现金奖励"+amount+"元给您送到，快登陆“前海红筹-个人中心”查收一下吧！如有疑问，欢迎致电我们的客服mm~400-822-3499。";
						try {
							// 用户系统消息
							Usermessage userMessage = new Usermessage();
							userMessage.setUserbasicsinfo(userInfo);
							userMessage.setContext(content);
							// 未读
							userMessage.setIsread(0);
							// 发送时间
							userMessage.setReceivetime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
							// 发送主题
							userMessage.setTitle("首次投资返现活动");
							// 保存系统消息
							bonusService.saveUserMesssage(userMessage);
							if(status==1){
								bonus.setUserState(Constant.STATUES_ONE);
								bonus.setUserReleaseId(loginuser.getId());
								bonus.setUserDate(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
							}else{
								bonus.setGenUserState(Constant.STATUES_ONE);
								bonus.setGenUserReleaseId(loginuser.getId());
								bonus.setGenUserDate(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
							}
							bonusService.update(bonus);
							LOG.info("转帐成功");
						} catch (Exception e) {
							LOG.error("转帐失败",e);
							return "2";
						}
					}
			}
			return "1";
		}
		
		
		/***
		 * 奖励发放
		 * @param uid
		 * @param amount
		 * @param state 1-推荐奖励转账   2-注册奖励转账
		 * @return
		 */
		private boolean transferBonus(long uid,double amount,Integer state,long id){
			Userbasicsinfo userbasicsinfo = userbasicsinfoService.queryUserById(uid);
			String name="JL";
			String explan="推荐奖励发放";
			if(state==2){
				name="ZC";
				explan="注册奖励发放";
			}
			String orderNum = name+id +"_"+uid+"_"+ new Date().getTime();// 佣金转账订单号
			AcctTrans acctTran = new AcctTrans();
			acctTran.setMerchant_id(ParameterIps.getCert());
			acctTran.setOrder_id(orderNum);
			acctTran.setPayer_user_id(ParameterIps.getCert());//付款方，平台
			acctTran.setPayee_user_id(userbasicsinfo.getpMerBillNo());// 收款，客户
			acctTran.setPayer_type(1);//1，付款方商户
			acctTran.setPayee_type(0);// 收款
			acctTran.setAmount(amount);
			acctTran.setFee(0.00);
			acctTran.setFee_taken_on(1);
			acctTran.setReq_time(new Date().getTime());
			try {
				String registerXml = ParseXML.accttrans(acctTran);
				ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("requestParams",registerXml));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml + "~|~"+ ParameterIps.getmerchantKey())));
				payLogService.savePayLog(registerXml, userbasicsinfo.getId(),userbasicsinfo.getId(), 24, orderNum, 0.00, 0.00,amount);
				String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
				result = result.replace("\"", "\'");
				crs cr = new crs();
				XStream xss = new XStream(new DomDriver());
				xss.alias(cr.getClass().getSimpleName(), cr.getClass());
				cr = (crs) xss.fromXML(result);
				String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~"+ cr.getMsg() + "~|~"+ ParameterIps.getDes_algorithm());
				if (cr.getSign().equals(Md5sign)) {
					if (cr.getCode().equals("CSD000")) {
						// 更新收款人
					    crs cr2 = baoFuService.getCasbalance(userbasicsinfo.getpMerBillNo());
						 // 添加流水
						Accountinfo account = new Accountinfo();
						account.setExpenditure(0.00);
						account.setExplan(explan);
						account.setIncome(amount);
						account.setIpsNumber(orderNum);
						account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
						account.setUserbasicsinfo(userbasicsinfo);
						account.setAccounttype(plankService.accounttype(18L));
						userbasicsinfo.getUserfundinfo().setCashBalance(cr2.getBalance());
						userbasicsinfo.getUserfundinfo().setOperationMoney(cr.getBalance());
						account.setMoney(cr2.getBalance());
						userbasicsinfoService.update(userbasicsinfo);
						plankService.saveAccount(account);// 保存流水账余额
						payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
						return true;
					}
				}
				return false;
			} catch (Exception e) {
				LOG.error("奖励发放",e);
				return false;
			} 
		}
		
		/***
		 * 绑定宝付账户 若无宝付账户，直接注册 若有先绑定验证码，获得验证码进行注册
		 * @param request
		 * @param userId
		 * @param sendBindCode
		 * @param sendNum
		 *            0-无 1-有
		 * @return
		 */
		public String ipsRegisterService(HttpServletRequest request, String userId,String sendBindCode, String sendNum,String cardId,String name) {
			if(!StringUtil.isNotBlank(cardId)||!StringUtil.isNotBlank(name)){
				return "4"; //如果身份证和姓名为空则返回
			}
			 Pattern pattern = Pattern.compile("/^((\\d{18})|(\\d{17}[Xx]))$/");
			 Matcher matcher = pattern.matcher(cardId);
			if (matcher.matches()) {
				return "6";
			}
			String dates = cardId.substring(6, 10) + "-" + cardId.substring(10, 12) + "-" + cardId.substring(12, 14);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			Date d;
			try {
				d = sdf.parse(dates);
				d.setYear(d.getYear()+18);
				boolean flag = d.before(new Date());
				if(!flag){
					return "7";
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Userbasicsinfo user = userbasicsinfoService.queryUserById(userId);
			RegisterInfo registerInfo = new RegisterInfo();
			if (sendNum.equals("0")) {
				registerInfo.setHas_bf_account("0");
				registerInfo.setBind_code("0");
			} else if (sendNum.equals("1")) {
				registerInfo.setHas_bf_account("1");
				registerInfo.setBind_code(sendBindCode);
			}
			Long user_id = new Date().getTime();
			registerInfo.setBf_account(user.getUserrelationinfo().getPhone());
			registerInfo.setId_card(cardId.trim());
			registerInfo.setName(name.trim());
			registerInfo.setUser_id(String.valueOf(user_id));
			registerInfo.setAccount_type("1");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			try {
				String rechargeInfoxml = CommonUtil.aesEncryptKey16(ParseXML.registrationXml(registerInfo),ParameterIps.getmerchantKey());
				payLogService.savePayLog(rechargeInfoxml, user.getId(),1, user.getId()+"_"+user_id, user.getId(), 0.00,0.00, 0.00); // 保存xml报文
				user.getUserrelationinfo().setCardId(cardId.trim());
				user.setName(name);
				userbasicsinfoService.update(user);
				nvps.add(new BasicNameValuePair("requestParams",rechargeInfoxml));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(rechargeInfoxml + "~|~"+ ParameterIps.getmerchantKey())));
				String result = CommonUtil.excuteRequest(PayURL.QUICKREGISTERURL,nvps);
				LOG.error("用户注册=" + result);
				Document doc = DocumentHelper.parseText(result);
				Element rootElt = doc.getRootElement(); // 获取根节点
				// 拿到crs节点下的子节点code值
				String code = rootElt.elementTextTrim("code");
				String msg = rootElt.elementTextTrim("msg");
				String sign = rootElt.elementTextTrim("sign");
				String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
				if (sign.equals(Md5sign)) {
					if (code.equals("CSD000")) {
						user.setpIpsAcctDate(DateUtil.format("yyyy-MM-dd HH:mm:ss"));
						user.setpMerBillNo(String.valueOf(registerInfo.getUser_id()));
						user.setHasIpsAccount(1);
						user.setIsAuthIps(0);
						user.getUserrelationinfo().setCardId(cardId.trim());
						user.setCardStatus(2);
						user.setName(name);
						userbasicsinfoService.update(user);
						generalizeService.updateGeneralize(user);
						payLogService.updatePayLog(user.getId()+"_"+user_id,Constant.STATUES_ONE);
						LOG.error("宝付支付注册成功，用户Id=" + user.getId());
					} else {
						LOG.error("宝付注册处理失败，用户Id=" + user.getId());
						return code+"_"+msg;
					}
				} else {
					LOG.error("非宝付注册返回数据，用户Id=" + user.getId());
					return "3";
				}
			} catch (Exception e) {
				LOG.error("宝付注册处理失败，用户Id=" + user.getId()+"--",e);
			}
			return "1";
		}
		
		/***
		 * 绑定验证码服务端接口 若已注册宝付账户，先根据宝付账户绑定验证码，验证码会及时发送到手机上
		 * @param request
		 * @param bf_account
		 *            宝付账户
		 * @return
		 */
		public String ipsSendBindCodeService(HttpServletRequest request, String bf_account) {
			try {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("requestParams", bf_account));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(bf_account+ "~|~" + ParameterIps.getmerchantKey())));
				String result = CommonUtil.excuteRequest(PayURL.SENDBINDCODEURL,nvps);
				System.out.println("绑定验证码=" + result);
				Document doc = DocumentHelper.parseText(result);
				Element rootElt = doc.getRootElement(); // 获取根节点
				System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
				// 拿到crs节点下的子节点code值
				String code = rootElt.elementTextTrim("code");
				String msg = rootElt.elementTextTrim("msg");
				String sign = rootElt.elementTextTrim("sign");
				String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
				if (sign.equals(Md5sign)) {
					if (code.equals("CSD000")) {
						return "1";
					} else {
						return "2";
					}
				} else {
					return "2";
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "2";
			}
		}
		
		/***
		 * 获取手机验证码
		 * @param request
		 * @return
		 */
		public String ipsSendPhoneBindCodeService(HttpServletRequest request) {
			Userbasicsinfo userbasics = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
			Userbasicsinfo user = userbasicsinfoService.queryUserById(userbasics.getId());
			try {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("requestParams", user.getUserrelationinfo().getPhone()));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(user.getUserrelationinfo().getPhone()+ "~|~"+ ParameterIps.getmerchantKey())));
				payLogService.savePayLog(user.getUserName() + "发送手机验证码",user.getId(), 23);
				String result = CommonUtil.excuteRequest(PayURL.SENDBINDCODEURL,nvps);
				LOG.error("绑定验证码=" + result);
				Document doc = DocumentHelper.parseText(result);
				Element rootElt = doc.getRootElement(); // 获取根节点
				// 拿到crs节点下的子节点code值
				String code = rootElt.elementTextTrim("code");
				String msg = rootElt.elementTextTrim("msg");
				String sign = rootElt.elementTextTrim("sign");
				String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
				if (sign.equals(Md5sign)) {
					if (code.equals("CSD000")) {
						return "1";
					} else {
						return "2";
					}
				} else {
					return "2";
				}
			} catch (Exception e) {
				LOG.error("获取手机验证码:",e);
				return "2";
			}
		}
		
		/****
		 * 前端添加银行卡 type =0 删除 +id type =1 新增 +userbank
		 * @param id
		 * @param request
		 * @return
		 */
		public synchronized String ipsOpBankCardService(HttpServletRequest request,String type, UserBank userBank, String id,String validateCode) {
			Userbasicsinfo userbasics = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
			Userbasicsinfo user = userbasicsinfoService.queryUserById(userbasics.getId());
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			String udId = "";
			try {
				if (type.equals("0")) { // 删除
					udId = id;
					String vilicode=userBank.getValidate_code();
					userBank = userBankService.getUserBankById(Long.valueOf(id));
					userBank.setValidate_code(vilicode);
					if(userBank.getState()==-1){
						userBankService.delete(userBank);
						LOG.info("删除银行卡成功");
						return "1";
					}
				} else if (type.equals("1")) { // 新增
					boolean isbank=userBankService.getUserBank(userBank.getBank_no());
					if(isbank){
						userBank.setState(0);
						userBank.setUserbasicsinfo(user);
						userBank.setBank_no(userBank.getBank_no().trim());
						Serializable seria = userBankService.saveUserBankSeria(userBank); // 保存银行卡
						udId = seria.toString();
					}else{
						return "4";
					}
				
				}
				String opBankCardXml = ParseXML.opBankCardXml(userBank, type);
				String aesOpBankCardXml = CommonUtil.aesEncryptKey16(opBankCardXml,ParameterIps.getmerchantKey());
				payLogService.savePayLog(opBankCardXml, user.getId(),Long.valueOf(type), 22, udId + userBank.getBank_no(), 0.00,0.00, 0.00); // 保存xml报文
				nvps.add(new BasicNameValuePair("requestParams", aesOpBankCardXml));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(aesOpBankCardXml + "~|~"+ ParameterIps.getmerchantKey())));
				String result = CommonUtil.excuteRequest(PayURL.OPBANKCARDURL, nvps);
				LOG.error("绑定银行卡=" + result);
				Document doc = DocumentHelper.parseText(result);
				Element rootElt = doc.getRootElement(); // 获取根节点
				// 拿到crs节点下的子节点code值
				String code = rootElt.elementTextTrim("code");
				String msg = rootElt.elementTextTrim("msg");
				String sign = rootElt.elementTextTrim("sign");
				String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
				UserBank userbBank = userBankService.getUserBankById(Long.valueOf(udId));
				if (sign.equals(Md5sign)) {
					if (code.equals("CSD000")) {
						if (type.equals("1")) {
							if (userbBank.getState() != 1) {
								userbBank.setState(Constant.STATUES_ONE);
								userBankService.update(userbBank);
								payLogService.updatePayLog(udId + userbBank.getBank_no(),Constant.STATUES_ONE);
								// 添加注册奖励
								if (null == bonusService.queryRegBonus(user.getId())) {  
									Generalize generalize=bonusService.queryGeneralize(user.getId());
									if(generalize!=null){
										Userbasicsinfo referrer=userbasicsinfoService.queryUserById(generalize.getGenuid());
										RegBonus regBonus = new RegBonus();
										regBonus.setUserbasicsinfo(user);
										regBonus.setReferrer(referrer);
										regBonus.setReleaseStatus(0);
										Costratio cos = expenseRatioService.findCostratio();
										regBonus.setBouns(null != cos.getRegBonu()
												&& !"".equals(cos.getRegBonu()) ? cos.getRegBonu() : 0);
										bonusService.saveReg(regBonus); // 保存信息
									}
								}
								LOG.info("添加银行卡成功");
							}
						} else if (type.equals("0")) {
							userBankService.delete(userbBank);
							LOG.info("删除银行卡成功");
						}
					}else if(code.equals("CSD333")){
						return msg;
					}else if(code.equals("BD004")){
						return msg;
					} else {
						if (type.equals("1")) {
							if (userbBank.getState() != 1) {
								userbBank.setState(-1);
								userBankService.update(userbBank);
								payLogService.updatePayLog(udId, -1);
								LOG.error("添加银行卡失败");
							}
						} 
						return "2";
					}
				} else {
					return "2";
				}
				return "1";
			} catch (Exception e) {
				LOG.error("添加银行卡失败",e);
				e.printStackTrace();
				return "3";
			}
		}
		
		/****
		 * 用户授权接口(页面)
		 * @param id
		 * @param request
		 * @return
		 */
		public String ipsInAccreditUserService(HttpServletRequest request, String type) {
			Userbasicsinfo userbasics = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
			Userbasicsinfo user = userbasicsinfoService.queryUserById(userbasics.getId());
			Map<String, String> map = null;
			try {
				map = RechargeInfoService.inAccreditUserCall(user.getpMerBillNo());
				payLogService.savePayLog(user.getpMerBillNo() + "_" + user.getUserName(),user.getId(), user.getId(), 16, user.getpMerBillNo(), 0.00,0.00, 0.00); // 保存xml报文
				if(type != null){
					map.put("page_url", Constant.H5INACCREDITUSRE);
					map.put("service_url", Constant.H5ASYNCHRONISMINACCREDITUSER);
				}
				map.put("url", PayURL.INACCREDITURL);
				request.getSession().setAttribute("map", map);
				return "member/callcentralInAccredit.htm";
			} catch (Exception e) {
				LOG.error("用户授权数据封装失败",e);
				return "1";
			}
		}
		
		/**
		 * 注册宝付
		 * @param userId   用户id
		 * @param request   HttpServletRequest
		 * @return String
		 */
		public String ipsRegistrationService(HttpServletRequest request,String cardId,String name, String type) {

			// 得到当前用户信息
			Userbasicsinfo userbasics = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
			Userbasicsinfo user = userbasicsinfoService.queryUserById(userbasics.getId());
			RegisterInfo register = new RegisterInfo();
			Long user_id = new Date().getTime();
			register.setBf_account(user.getUserrelationinfo().getPhone());
			register.setId_card(cardId);
			register.setName(name);
			register.setUser_id(String.valueOf(user_id));
			Map<String, String> map = null;
			try {
				if(type != null){
					register.setPage_url(Constant.H5INACCREDITUSRE);
					register.setReturn_url(Constant.H5ASYNCHRONISMREGISTRATION);
				}
				String registerXml = ParseXML.registration(register,String.valueOf(user.getId()));
				map = RegisterService.registerCall(registerXml);
				// 新增用户注册宝付的单号
/*				user.setOrderSn(user_id.toString());
				user.setHasIpsAccount(0); // 待确认
				userbasicsinfoService.update(user);*/
				// 添加日志
				payLogService.savePayLog(registerXml, user.getId(), 1,user_id.toString(), user_id, 0.00, 0.00, 0.00);
				// System.out.println(PayURL.REGISTRATIONTESTURL);
				map.put("url", PayURL.REGISTRATIONTESTURL);
				request.setAttribute("map", map);
				return "WEB-INF/views/hc9/member/trade/central_news";
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/***
		 * 查询用户信息
		 * 
		 * @param id
		 * @param request
		 * @return
		 */
		public JSONObject ipsSelTimeUserService(String startTimeState,String endTimeState, HttpServletRequest request) {
			JSONObject json = new JSONObject();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			try {
				String startTime = String.valueOf(sdf.parse(startTimeState)
						.getTime());
				String endTime = String.valueOf(sdf.parse(endTimeState).getTime());
				P2pQuery p2pQuery = new P2pQuery(8, startTime, endTime);
				String userXml = ParseXML.p2pQueryTimeXml(p2pQuery);
				nvps.add(new BasicNameValuePair("requestParams", userXml));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(userXml
						+ "~|~" + ParameterIps.getmerchantKey())));
				String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL,
						nvps);
				LOG.error("用户信息业务查询=" + result);
				Document doc = DocumentHelper.parseText(result);
				Element rootElt = doc.getRootElement(); // 获取根节点
				// 拿到crs节点下的子节点code值
				String code = rootElt.elementTextTrim("code");
				String msg = rootElt.elementTextTrim("msg");
				String sign = rootElt.elementTextTrim("sign");
				String msgState = "无修改宝付用户数据";
				// 获取子节点crs下的子节点result
				Iterator iteratorResult = rootElt.elementIterator("result");
				String state = "0"; // 1 正常 0未授权 -1已注销
				while (iteratorResult.hasNext()) {
					Element itemEle = (Element) iteratorResult.next();
					Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
					while (iteratorOrder.hasNext()) {
						Element elementOrder = (Element) iteratorOrder.next();
						state = elementOrder.elementTextTrim("state");
						String user_id = elementOrder.elementTextTrim("user_id");
						String mobile = elementOrder.elementText("mobile");
						String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"
								+ ParameterIps.getDes_algorithm());
						Userrelationinfo userrelationinfo = userbasicsinfoService
								.queryUserByPhone(mobile);
						Userbasicsinfo user = userbasicsinfoService
								.queryUserById(userrelationinfo.getId());
						if (sign.equals(Md5sign)) {
							if (code.equals("CSD000")) {
								if (state.equals("1")) { // 正常
									if (user.getpMerBillNo().equals("")
											|| user.getpMerBillNo() == null) {
										user.setpMerBillNo(user_id);
										user.setHasIpsAccount(1); // 成功
										userbasicsinfoService.update(user);
										// payLogService.updatePayLog(user.getOrderSn(),
										// Constant.STATUES_ONE);
										msgState = "宝付用户查询成功";
									}
								} else if (state.equals("0")) { // 未授权
									if (user.getpMerBillNo().equals("")
											|| user.getpMerBillNo() == null) {
										user.setpMerBillNo(user_id);
										user.setIsAuthIps(0); // 未授权
										user.setHasIpsAccount(0); // 待确认
										userbasicsinfoService.update(user);
										// payLogService.updatePayLog(user.getOrderSn(),
										// Constant.STATUES_ONE);
										msgState = "该宝付用户未授权";
									}
								} else { // 注销
									// 更新支付报文信息
									if (user.getpMerBillNo().equals("")
											|| user.getpMerBillNo() == null) {
										user.setHasIpsAccount(2); // 注销
										userbasicsinfoService.update(user);
										// payLogService.updatePayLog(user.getOrderSn(),
										// Constant.STATUES_ONE);
										msgState = "该宝付用户已注销";
									}
								}
							} else {
								if (user.getpMerBillNo().equals("")
										|| user.getpMerBillNo() == null) {
									user.setHasIpsAccount(-1); // 失败
									userbasicsinfoService.update(user);
									// payLogService.updatePayLog(user.getOrderSn(),
									// -1);
									// LOG.error("宝付用户查询处理失败--" + result+
									// "----->订单号----->" + user.getOrderSn());
									msgState = "宝付用户查询处理失败";
								}
							}
						} else {
							// LOG.error("非宝付用户查询返回数据--" +result+ "----->订单号----->"+
							// user.getOrderSn());
							msgState = "非宝付用户查询返回数据";
						}
					}
				}
				return DwzResponseUtil.setJson(json,
						Constant.HTTP_STATUSCODE_SUCCESS, msgState, "main26",
						"closeCurrent");
			} catch (Exception e) {
				LOG.error("非宝付用户查询返回数据--"+e);
				return DwzResponseUtil.setJson(json,
						Constant.HTTP_STATUSCODE_ERROR, "查询报错", "main26",
						"closeCurrent");
			}
		}
		
		/***
		 * 查询用户信息
		 * 
		 * @param id
		 * @param request
		 * @return
		 */
		public String ipsSelUserService(String id, HttpServletRequest request) {
			Userbasicsinfo user = userbasicsinfoService.queryUserById(id);
			// 如果宝付ID为空，则取宝付订单号
			String orderSn = user.getpMerBillNo();
			if (orderSn == null) {
				orderSn = user.getOrderSn();
			}
			P2pQuery p2pQuery = new P2pQuery(orderSn, 8);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			try {
				String userXml = ParseXML.p2pQueryXml(p2pQuery);
				nvps.add(new BasicNameValuePair("requestParams", userXml));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(userXml
						+ "~|~" + ParameterIps.getmerchantKey())));
				String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL,
						nvps);
				LOG.error("用户信息业务查询=" + result);
				Document doc = DocumentHelper.parseText(result);
				Element rootElt = doc.getRootElement(); // 获取根节点
				// 拿到crs节点下的子节点code值
				String code = rootElt.elementTextTrim("code");
				String msg = rootElt.elementTextTrim("msg");
				String sign = rootElt.elementTextTrim("sign");
				String msgState = "0";
				// 获取子节点crs下的子节点result
				Iterator iteratorResult = rootElt.elementIterator("result");
				String state = "0"; // 1 正常 0未授权 -1已注销
				while (iteratorResult.hasNext()) {
					Element itemEle = (Element) iteratorResult.next();
					Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
					while (iteratorOrder.hasNext()) {
						Element elementOrder = (Element) iteratorOrder.next();
						state = elementOrder.elementTextTrim("state");
						String user_id = elementOrder.elementTextTrim("user_id");
						String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"
								+ ParameterIps.getDes_algorithm());
						if (sign.equals(Md5sign)) {
							if (code.equals("CSD000")) {
								if (state.equals("1")) { // 正常
									user.setpMerBillNo(user_id);
									user.setHasIpsAccount(1); // 成功
									user.setIsAuthIps(1); // 未授权
									userbasicsinfoService.update(user);
									payLogService.updatePayLog(user.getOrderSn(),
											Constant.STATUES_ONE);
									msgState = "1";
								} else if (state.equals("0")) { // 未授权
									user.setpMerBillNo(user_id);
									user.setIsAuthIps(0); // 未授权
									user.setHasIpsAccount(0); // 待确认
									userbasicsinfoService.update(user);
									payLogService.updatePayLog(user.getOrderSn(),
											Constant.STATUES_ONE);
									msgState = "2";
								} else { // 注销
									// 更新支付报文信息
									user.setHasIpsAccount(2); // 注销
									userbasicsinfoService.update(user);
									payLogService.updatePayLog(user.getOrderSn(),
											Constant.STATUES_ONE);
									msgState = "3";
								}
							} else {
								user.setHasIpsAccount(-1); // 失败
								userbasicsinfoService.update(user);
								payLogService.updatePayLog(user.getOrderSn(), -1);
								LOG.error("宝付用户查询处理失败--" + result
										+ "----->订单号----->" + user.getOrderSn());
								msgState = "4";
							}
						} else {
							LOG.error("非宝付用户查询返回数据--" + result + "----->订单号----->"
									+ user.getOrderSn());
							msgState = "5";
						}
					}
				}
				if (msgState.equals("0")) {
					// 更新支付报文信息
					payLogService.updatePayLog(user.getOrderSn(), -1);
					user.setIsAuthIps(0); // 未授权
					user.setHasIpsAccount(0); // 待确认
					userbasicsinfoService.update(user);
					msgState = "6";
				}
				return msgState;
			} catch (Exception e) {
				LOG.error("宝付用户查询失败----->订单号----->" + user.getOrderSn(),e);
				return "7";
			}
		}
		
		/***
		 * 后端充值业务查询
		 * @param request
		 * @param rId
		 * @return
		 * @throws Exception
		 */
		public String ipsCustomerRechargeNum(HttpServletRequest request, String rId)throws Exception {
			Recharge recharge = rechargesService.selRecharge(rId);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = df.format(new Date());
			Date d1 = df.parse(date);
			Date d2 = df.parse(DateUtil.addDateMinut(recharge.getTime(), 10));
			if (d1.getTime() < d2.getTime()) {
				return "2";
			}
			P2pQuery p2pQuery = new P2pQuery(recharge.getOrderNum(), 5);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			try {
				String withdrawXml = ParseXML.p2pQueryXml(p2pQuery);
				nvps.add(new BasicNameValuePair("requestParams", withdrawXml));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(withdrawXml+ "~|~" + ParameterIps.getmerchantKey())));
				String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL,nvps);
				LOG.error("充值业务查询=" + result);
				Document doc = DocumentHelper.parseText(result);
				Element rootElt = doc.getRootElement(); // 获取根节点
				// 拿到crs节点下的子节点code值
				String code = rootElt.elementTextTrim("code");
				String msg = rootElt.elementTextTrim("msg");
				String sign = rootElt.elementTextTrim("sign");
				// 获取子节点crs下的子节点result
				Iterator iteratorResult = rootElt.elementIterator("result");
				boolean Judge = false; // 判断是否有值
				String state = "-1";
				// 遍历result节点下的Response节点
				while (iteratorResult.hasNext()) {
					Element itemEle = (Element) iteratorResult.next();
					Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
					while (iteratorOrder.hasNext()) {
						Element elementOrder = (Element) iteratorOrder.next();
						String order_id = elementOrder.elementTextTrim("order_id");
						state = elementOrder.elementTextTrim("state"); // 1-成功 0-处理中
						String succ_amount = elementOrder.elementTextTrim("succ_amount");
						String succ_time = elementOrder.elementTextTrim("succ_time");
						String fee = elementOrder.elementTextTrim("fee");
						String baofoo_fee = elementOrder.elementTextTrim("baofoo_fee");
						String fee_taken_on = elementOrder.elementTextTrim("fee_taken_on");
						String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
						if (sign.equals(Md5sign)) {
							if (code.equals("CSD000")) {
								if (state.equals("1")) {
									recharge.setFee(Double.valueOf(baofoo_fee)); // 宝付收取费用
									recharge.setMer_fee(Double.valueOf(fee)); // 商户收取的手续费
									recharge.setIncash_money(Arith.sub(Double.valueOf(succ_amount),Double.valueOf(baofoo_fee))); // 充值结算金额(实际到账)
									recharge.setCode(code);
									recharge.setStatus(Integer.parseInt(state));
									recharge.setSuccTime(succ_time);
									recharge.setFee_taken_on(Integer.valueOf(fee_taken_on));
									processingservice.updaterecharge(recharge);
									Userbasicsinfo user = userbasicsinfoService.queryUserById(recharge.getUserbasicsinfo().getId());
									// 流水账
									Accountinfo account = new Accountinfo();
									account.setExpenditure(0.00);
									account.setExplan("充值");
									account.setIncome(Double.valueOf(succ_amount));
									account.setIpsNumber(order_id);
									account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
									account.setUserbasicsinfo(user);
									account.setAccounttype(plankService.accounttype(6L));
									// 余额查询
									crs cr = baoFuService.getCasbalance(user.getpMerBillNo());
									user.getUserfundinfo().setCashBalance(cr.getBalance());
									user.getUserfundinfo().setOperationMoney(	cr.getBalance());
									account.setMoney(cr.getBalance());
									userbasicsinfoService.update(user);
									plankService.saveAccount(account);// 保存流水账余额
									// 更新支付报文信息
									payLogService.updatePayLog(recharge.getOrderNum(),Integer.parseInt(state),recharge.getIncash_money(),recharge.getFee());
									Judge = true;
									LOG.error("宝付充值查询处理成功");
									long userId = recharge.getUserbasicsinfo().getId();
									HcNewerTaskCache.giveFirstRechargeRedenvelopeKey(userId);
								} else if (state.equals("0")) {
									recharge.setStatus(-1);
									recharge.setFee(0.00);
									recharge.setMer_fee(0.00);
									processingservice.updaterecharge(recharge);
									payLogService.updatePayLog(recharge.getOrderNum(), -1, 0.00, 0.00);
								}
							}
						} else {
							LOG.error("非宝付充值查询返回数据--" + result + "----->订单号----->"
									+ recharge.getOrderNum());
							return "-1";
						}
					}
				}
				if (!Judge && state.equals("-1")) {
					recharge.setStatus(-1);
					recharge.setFee(0.00);
					recharge.setMer_fee(0.00);
					processingservice.updaterecharge(recharge);
					payLogService.updatePayLog(recharge.getOrderNum(), -1, 0.00,0.00);
				}
				return "1";
			} catch (Exception e) {
				LOG.error("宝付充值查询失败----->订单号----->" + recharge.getOrderNum(),e);
				return "-1";
			}
		}
	
	 /***
	  * 根据loanId查询是否收取平台费
	  * @param id
	  * @param request
	  * @return
	  */
	  public   String selLoanFeeState(String id,HttpServletRequest request){
		    Adminuser admin = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		    Loansign loan=loansignQuery.getLoansignById(id);
		    if(loan.getFeeState()!=0){
		    	return "2";
		    }
		    Paylog paylog=payLogService.queryPaylog(loan.getId(), loan.getUserbasicsinfo().getId(), 21);
		    if(paylog!=null){
			    	  P2pQuery p2pQuery = new P2pQuery(paylog.getOrderSn(), 7);
			    		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
						try {
							String withdrawXml = ParseXML.p2pQueryXml(p2pQuery);
							nvps.add(new BasicNameValuePair("requestParams", withdrawXml));
							nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(withdrawXml+ "~|~" + ParameterIps.getmerchantKey())));
							String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL,nvps);
							LOG.error("收取平台服务费业务查询=" + result);
							Document doc = DocumentHelper.parseText(result);
							Element rootElt = doc.getRootElement(); // 获取根节点
							// 拿到crs节点下的子节点code值
							String code = rootElt.elementTextTrim("code");
							String msg = rootElt.elementTextTrim("msg");
							String sign = rootElt.elementTextTrim("sign");
							// 获取子节点crs下的子节点result
							Iterator iteratorResult = rootElt.elementIterator("result");
							boolean Judge = false; // 判断是否有值
							String state = "-1";
							// 遍历result节点下的Response节点
							while (iteratorResult.hasNext()) {
								Element itemEle = (Element) iteratorResult.next();
								Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
								while (iteratorOrder.hasNext()) {
									Element elementOrder = (Element) iteratorOrder.next();
									state = elementOrder.elementTextTrim("state"); // 1-成功 0-处理中
									String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
									if (sign.equals(Md5sign)) {
										if (code.equals("CSD000")) {
											if (state.equals("1")) {
												// 余额查询
											    Accountinfo accountOne = new Accountinfo();
											    accountOne.setExpenditure(loan.getFeeMoney());
											    accountOne.setExplan("平台收取服务费");
											    accountOne.setIncome(0.00);
											    accountOne.setIpsNumber(paylog.getOrderSn());
											    accountOne.setLoansignId(String.valueOf(loan.getId()));// 标id（项目id）
											    accountOne.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
											    accountOne.setAccounttype(plankService.accounttype(17L));
											    accountOne.setUserbasicsinfo(loan.getUserbasicsinfo());
											    accountOne.setMoney(0.00);
											    accountOne.setFee(0.00);
											    plankService.saveAccount(accountOne);// 添加流水账余额
												loan.setFeeState(Constant.STATUES_ONE);  //收取状态
												loan.setAdminFee(admin.getId());   //操作Id
												loanSignService.updateLoangn(loan);
												
												payLogService.updatePayLog(paylog.getOrderSn(), Constant.STATUES_ONE);
												Judge = true;
												LOG.error("宝付收取平台费查询成功");
											} else if (state.equals("0")) {
												loan.setFeeState(2);
										    	loanSignService.updateLoangn(loan);
												payLogService.updatePayLog(paylog.getOrderSn(), -1);
												return "-1";
											}
										}
									} else {
										LOG.error("非宝付收取平台费查询返回数据--" + result + "----->订单号----->"+ paylog.getOrderSn());
										return "-1";
									}
								}
							}
							if (!Judge && state.equals("-1")) {
								loan.setFeeState(2);
						    	loanSignService.updateLoangn(loan);
								payLogService.updatePayLog(paylog.getOrderSn(), -1);
								return "-1";
							}
							return "1";
						} catch (Exception e) {
							LOG.error("宝付收取平台费查询失败----->订单号----->" + paylog.getOrderSn(),e);
							return "-1";
						}
		    }else{
		    	loan.setFeeState(2);
		    	loanSignService.updateLoangn(loan);
		    	return "-1";
		    }
	  }
	  
	  /***
		 * 红包转账
		 * @param loansign
		 */
	public void ipsTranCertHBZZ(Loansign loansign) {
			String orderNum = "HBZZ" + loansign.getUserbasicsinfo().getId()+"_"+new Date().getTime();// 红包转账订单号
			AcctTrans acctTran = new AcctTrans();
			acctTran.setMerchant_id(ParameterIps.getCert());
			acctTran.setOrder_id(orderNum);
			acctTran.setPayer_user_id(ParameterIps.getCert());
			acctTran.setPayee_user_id(loansign.getUserbasicsinfo().getpMerBillNo());// 收款
			acctTran.setPayer_type(1);
			acctTran.setPayee_type(0);// 收款
			acctTran.setAmount(loansign.getRedEnvelopeMoney());
			acctTran.setFee(0.00);
			acctTran.setFee_taken_on(1);
			acctTran.setReq_time(new Date().getTime());
			try {
				String registerXml = ParseXML.accttrans(acctTran);
				ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("requestParams", registerXml));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml+ "~|~" + ParameterIps.getmerchantKey())));
				payLogService.savePayLog(registerXml, loansign.getUserbasicsinfo().getId(),loansign.getUserbasicsinfo().getId(), 27, orderNum, 0.00, 0.00, loansign.getRedEnvelopeMoney());
				String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
				result = result.replace("\"", "\'");
				crs cr = new crs();
				XStream xss = new XStream(new DomDriver());
				xss.alias(cr.getClass().getSimpleName(), cr.getClass());
				cr = (crs) xss.fromXML(result);
				String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~" + cr.getMsg()+ "~|~" + ParameterIps.getDes_algorithm());
				if (cr.getSign().equals(Md5sign)) {
					if (cr.getCode().equals("CSD000")) {
						// 流水账
						Accountinfo account = new Accountinfo();
						account.setExpenditure(0.00);
						account.setExplan("红包转账");
						account.setIncome(loansign.getRedEnvelopeMoney());
						account.setIpsNumber(orderNum);
						account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
						account.setUserbasicsinfo(loansign.getUserbasicsinfo());
						account.setAccounttype(plankService.accounttype(22L));
						account.setLoansignId(loansign.getId().toString());
						account.setFee(0.00);
						account.setMoney(0.00);
						plankService.saveAccount(account);// 保存流水账
						payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
					}
				}
			} catch (Exception e) {
				LOG.error("红包转账失败----->",e);
			}

		}
		
		
	  public List<Withdraw> getListWithdraw(){
		   String sql="select * from withdraw where state in (0,2,5) ";
		   List<Withdraw> list=dao.findBySql(sql, Withdraw.class);
		    return list;
	 }	
	  
	  public List<Recharge> getListRecharge(){
		  String sql="select * from recharge  where `status`=0";
		  List<Recharge> list=dao.findBySql(sql, Recharge.class);
		  return list;
	  }
	  
	 public List<Loanrecord> getLoanRecordList(Long loanId){
		 String sql="select * from loanrecord where isSucceed=1 and loanSign_id=? ";
		  List<Loanrecord> list=dao.findBySql(sql, Loanrecord.class,loanId);
		  return list;
	 }
	 
	 public List<Generalize> getGeneralizeList(Long loanId){
		 String sql="select * from generalize where genuid=? ";
		  List<Generalize> list=dao.findBySql(sql, Generalize.class,loanId);
		  return list;
	 }
	 
	public void updateLoanRecord(Loanrecord loanrecord){
		dao.update(loanrecord);
	}
	
	public void getLoanRecordSql(String tenderTime,Long loanRecordId) {
		String sql = "update  loanrecord set isSucceed=1,tenderTime='"+tenderTime+"' where id="+loanRecordId;
		dao.executeSql(sql);
	}
	
	/***
	 * 修改收取平台服务费状态
	 * @param loanId
	 */
	public void getLoanFeeStateSql(String loanId) {
		String sql = "update  loansign set feeState=0  where id="+loanId;
		dao.executeSql(sql);
	}
	 
	public Accountinfo getAccountinfo(String orderSn){
		String sql="select * from accountinfo where ipsNumber like '%"+orderSn+"%' ";
		Accountinfo accountinfo = dao.findObjectBySql(sql, Accountinfo.class);
		return accountinfo;
	}
	
	/**
	 * 放款流水
	 * @param loan
	 * @param payLog
	 * @param userinfo
	 * @param order_id
	 * @param money
	 */
	public  void getSaveAccount(Loansign loan,Paylog payLog,Userbasicsinfo userinfo,String order_id,Double money){
		
		// 流水账
	    Accountinfo accountTwo = new Accountinfo();
	    accountTwo.setExpenditure(0.00);
	    accountTwo.setExplan("融资总额");
	    accountTwo.setIncome(loan.getIssueLoan());
	    accountTwo.setIpsNumber(order_id);
	    accountTwo.setLoansignId(loan.getId().toString());// 标id（项目id）
	    accountTwo.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
	    accountTwo.setUserbasicsinfo(userinfo);
	    accountTwo.setFee(0.00);
	    accountTwo.setAccounttype(plankService.accounttype(23L));
	    accountTwo.setMoney(0.00);
		plankService.saveAccount(accountTwo);// 添加流水账余额
		
	   if(payLog.getFee()>0&&loan.getRefunway()!=4){
			// 流水账
		    Accountinfo accountOne = new Accountinfo();
		    accountOne.setExpenditure(payLog.getFee());
		    accountOne.setExplan("平台收取服务费");
		    accountOne.setIncome(0.00);
		    accountOne.setIpsNumber(order_id);
		    accountOne.setLoansignId(loan.getId().toString());// 标id（项目id）
		    accountOne.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		    accountOne.setUserbasicsinfo(userinfo);
		    accountOne.setFee(0.00);
		    accountOne.setAccounttype(plankService.accounttype(17L));
		    accountOne.setMoney(0.00);
			plankService.saveAccount(accountOne);// 添加流水账余额
		}
		// 流水账
	    Accountinfo account = new Accountinfo();
		account.setExpenditure(0.00);
		account.setExplan("项目放款");
		account.setIncome(payLog.getAmount());
		account.setIpsNumber(order_id);
		account.setLoansignId(loan.getId().toString());// 标id（项目id）
		account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		account.setUserbasicsinfo(userinfo);
		account.setFee(0.00);
		account.setAccounttype(plankService.accounttype(12L));
		account.setMoney(money);
		plankService.saveAccount(account);// 添加流水账余额
		
	}
	
	
	/***
	 * 根据标Id进行加息转账
	 * @param request
	 * @param loanId
	 * @return
	 */
	 public  String ipsVoteIncomeZZ(HttpServletRequest request,String loanId){
		   //根据标Id查询有关的佣金记录信息
		 List<VoteIncome> voteIncomeList=voteincomeService.getVoteIncomeList(loanId);
	    	if(voteIncomeList.size()>0){
	    		for (int i = 0; i < voteIncomeList.size(); i++) {
	    			VoteIncome voteIncome=voteIncomeList.get(i);
	    			Userbasicsinfo userbasicsinfo = userbasicsinfoService.queryUserById(voteIncome.getVoterId());
	    			String orderNum = "JX" +StringUtil.getDateTime(userbasicsinfo.getId(),Long.valueOf(loanId));// 佣金转账订单号
	    			AcctTrans acctTran = new AcctTrans();
					acctTran.setMerchant_id(ParameterIps.getCert());
					acctTran.setOrder_id(orderNum);
					acctTran.setPayer_user_id(ParameterIps.getCert());
					acctTran.setPayee_user_id(userbasicsinfo.getpMerBillNo());// 收款
					acctTran.setPayer_type(1);
					acctTran.setPayee_type(0);// 收款
					acctTran.setAmount(Double.valueOf(voteIncome.getIncomeMoney()));
					acctTran.setFee(0.00);
					acctTran.setFee_taken_on(1);
					acctTran.setReq_time(new Date().getTime());
					try {
						String registerXml = ParseXML.accttrans(acctTran);
						ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
						nvps.add(new BasicNameValuePair("requestParams",registerXml));
						nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml + "~|~"+ ParameterIps.getmerchantKey())));
						payLogService.savePayLog(registerXml, userbasicsinfo.getId(),Long.parseLong(loanId), 28, orderNum, 0.00, 0.00,voteIncome.getIncomeMoney());
						String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
						result = result.replace("\"", "\'");
						voteIncome.setStatus(Constant.STATUES_TWO);
						voteincomeService.updateVoteIncome(voteIncome);
						crs cr = new crs();
						XStream xss = new XStream(new DomDriver());
						xss.alias(cr.getClass().getSimpleName(), cr.getClass());
						cr = (crs) xss.fromXML(result);
						String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~"+ cr.getMsg() + "~|~"+ ParameterIps.getDes_algorithm());
						if (cr.getSign().equals(Md5sign)) {
							if (cr.getCode().equals("CSD000")) {
								  if(transBonusesState(orderNum,loanId,userbasicsinfo.getId(),voteIncome.getIncomeMoney(),28)){
									    voteIncome.setStatus(Constant.STATUES_ONE);
										voteincomeService.updateVoteIncome(voteIncome);
									    // 添加流水
										Accountinfo account = new Accountinfo();
										account.setExpenditure(0.00);
										account.setExplan("加息奖励");
										account.setIncome(voteIncome.getIncomeMoney());
										account.setIpsNumber(orderNum);
										account.setLoansignId(loanId);// 标id（项目id）
										account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
										account.setUserbasicsinfo(userbasicsinfo);
										account.setAccounttype(plankService.accounttype(24L));
										account.setMoney(0.00);
										account.setFee(0.00);
										payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
										plankService.saveAccount(account);// 添加流水账余额
										LOG.error("加息奖励发放成功");
//										System.out.println("加息奖励成功");
								  }else{
									    voteIncome.setStatus(-1);
									    voteincomeService.updateVoteIncome(voteIncome);
									    LOG.error("加息奖励发放失败：操作员+用户+应收金额+投资记录起码有一项不对");
//									    System.out.println("加息奖励失败");
									    return "0";
								  }
							}else{
							    voteIncome.setStatus(-1);
							    voteincomeService.updateVoteIncome(voteIncome);
							    LOG.error("加息奖励发放失败宝付返回失败"+cr.getCode());
//							    System.out.println("加息奖励失败");
								return "3";
							}
						}
					} catch (Exception e) {
						LOG.error("加息奖励发放失败",e);
//						e.printStackTrace();
						return "-1";
					} 
				}
	    	}else{
	    		return "2";
	    	}
	    	return "1";
	    }
	 
		/***
		 *  现金转账
		 * @param request
		 * @param loanId
		 * @return
		 */
		  public  String ipsTransActivtiyMonkey(HttpServletRequest request,String activtiyId){
				Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
				if(loginuser.getRole().getId()!=3 && loginuser.getRole().getId()!=21){ //判断是否财务总监和财务审核
					return "4";
				}
				List<ActivityMonkey>  listActivityMonkey=activityMonkeyQueryService.getActivityMonkeyList(activtiyId);
		    	if(listActivityMonkey.size()>0){
		    		for (int i = 0; i < listActivityMonkey.size(); i++) {
		    			ActivityMonkey activityMonkey=listActivityMonkey.get(i);
		    			Userbasicsinfo userbasicsinfo = userbasicsinfoService.queryUserById(activityMonkey.getUserId());
		    			if(userbasicsinfo!=null){
			    				if(userbasicsinfo.getpMerBillNo()!=null){
			    					String orderNum = "XJZZ" +StringUtil.getDateTime(userbasicsinfo.getId(),Long.valueOf(activtiyId));// 现金转账订单号
					    			AcctTrans acctTran = new AcctTrans();
									acctTran.setMerchant_id(ParameterIps.getCert());
									acctTran.setOrder_id(orderNum);
									acctTran.setPayer_user_id(ParameterIps.getCert());
									acctTran.setPayee_user_id(userbasicsinfo.getpMerBillNo());// 收款
									acctTran.setPayer_type(1);
									acctTran.setPayee_type(0);// 收款
									acctTran.setAmount(Double.valueOf(activityMonkey.getRewardMoney()));
									acctTran.setFee(0.00);
									acctTran.setFee_taken_on(1);
									acctTran.setReq_time(new Date().getTime());
									try {
										String registerXml = ParseXML.accttrans(acctTran);
										ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
										nvps.add(new BasicNameValuePair("requestParams",registerXml));
										nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml + "~|~"+ ParameterIps.getmerchantKey())));
										payLogService.savePayLog(registerXml, userbasicsinfo.getId(),Long.parseLong(activtiyId), 29, orderNum, 0.00, 0.00,activityMonkey.getRewardMoney());
										activityMonkey.setOrderNum(orderNum);
										activityMonkey.setStatus(Constant.STATUES_TWO);
										activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
										String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
										result = result.replace("\"", "\'");
										crs cr = new crs();
										XStream xss = new XStream(new DomDriver());
										xss.alias(cr.getClass().getSimpleName(), cr.getClass());
										cr = (crs) xss.fromXML(result);
										String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~"+ cr.getMsg() + "~|~"+ ParameterIps.getDes_algorithm());
										if (cr.getSign().equals(Md5sign)) {
											if (cr.getCode().equals("CSD000")) {
												int action=29;//代表paylog action 现金转账
												  if(transBonusesState(orderNum,activtiyId,userbasicsinfo.getId(),activityMonkey.getRewardMoney(),action)){
													    activityMonkey.setStatus(Constant.STATUES_ONE);
													    activityMonkey.setGrantTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
													    activityMonkey.setGrantAdminId(loginuser.getId());
													  	activityMonkey.setOrderNum(orderNum);
													  	activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
													  	
													  	// 首投返现的现金到达用户账户后，触发一条短信提醒
													  	int activityType = 0;
													  	if (activityMonkey.getType() != null ) {
													  		activityType = activityMonkey.getType().intValue();
													  	}
													  	// 5月活动涉及到现金发放的都需要发送短信
													  	if (activityType == 14 || activityType == 15) {
													  		String activityTypeName = "首投返现";
													  		if (activityType == 15) {
													  			activityTypeName = "周周惊喜大放送";
													  		}
														  	String realName = userbasicsinfo.getName();
														  	String phone = userbasicsinfo.getUserrelationinfo().getPhone();
														  	double money = activityMonkey.getRewardMoney();
														  	smsService.sendSms4UserFirstInvest(realName, activityTypeName, phone, money);
													  	}
													  	
													    // 添加流水
														Accountinfo account = new Accountinfo();
														account.setExpenditure(0.00);
														account.setExplan("现金转账");
														account.setIncome(activityMonkey.getRewardMoney());
														account.setIpsNumber(orderNum);
														account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
														account.setUserbasicsinfo(userbasicsinfo);
														account.setAccounttype(plankService.accounttype(25L));
														account.setMoney(0.00);
														payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
														plankService.saveAccount(account);// 添加流水账余额
														LOG.error("现金转账成功"+orderNum);
												  }else{
													    activityMonkey.setStatus(-1);
													    activityMonkey.setFailreason(cr.getCode()+cr.getMsg());
													    activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
													    LOG.error("现金转账失败"+orderNum);
	//													System.out.println("现金转账成功");
													    return "0";
												  }
											}else{
												    activityMonkey.setStatus(-1);
												    activityMonkey.setOrderNum("");
												    activityMonkey.setFailreason(cr.getCode()+cr.getMsg());
												    activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
												    return "3";
											}
										}
									} catch (Exception e) {
										LOG.error("现金转账失败"+orderNum,e);
	//									e.printStackTrace();
										return "-1";
									} 
			    				}else{
			    					return "6";
			    				}
			    			}else{
			    				return "5";
			    			}
				 	}
		    	}else{
		    		return "2";
		    	}
		    	return "1";
		    }
		  
		/***
		 * 现金发放业务查询
		 * @param request
		 * @param rId
		 * @return
		 * @throws Exception
		 */
		public String ipsActivtiyMonkeyInfo(HttpServletRequest request, String id)throws Exception {
			ActivityMonkey activityMonkey = activityMonkeyQueryService.getActivityMonkeyById(id);
			P2pQuery p2pQuery = new P2pQuery(activityMonkey.getOrderNum(), 7);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			try {
				String withdrawXml = ParseXML.p2pQueryXml(p2pQuery);
				nvps.add(new BasicNameValuePair("requestParams", withdrawXml));
				nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(withdrawXml+ "~|~" + ParameterIps.getmerchantKey())));
				String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL,nvps);
				LOG.error("现金发放业务查询=" + result);
				Document doc = DocumentHelper.parseText(result);
				Element rootElt = doc.getRootElement(); // 获取根节点
//				System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
				// 拿到crs节点下的子节点code值
				String code = rootElt.elementTextTrim("code");
				String msg = rootElt.elementTextTrim("msg");
				String sign = rootElt.elementTextTrim("sign");
				// 获取子节点crs下的子节点result
				Iterator iteratorResult = rootElt.elementIterator("result");
				boolean Judge = false; // 判断是否有值
				String state = "-1";
				// 遍历result节点下的Response节点
				while (iteratorResult.hasNext()) {
					Element itemEle = (Element) iteratorResult.next();
					Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
					while (iteratorOrder.hasNext()) {
						Element elementOrder = (Element) iteratorOrder.next();
						String order_id = elementOrder.elementTextTrim("order_id");
						state = elementOrder.elementTextTrim("state"); // 1-成功 0-处理中
						String succ_amount = elementOrder.elementTextTrim("succ_amount");
						String succ_time = elementOrder.elementTextTrim("succ_time");
						String fee = elementOrder.elementTextTrim("fee");
						String baofoo_fee = elementOrder.elementTextTrim("baofoo_fee");
						String fee_taken_on = elementOrder.elementTextTrim("fee_taken_on");
						String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"+ ParameterIps.getDes_algorithm());
						if (sign.equals(Md5sign)) {
							if (code.equals("CSD000")) {
								if (state.equals("1")) {
									Userbasicsinfo userbasicsinfo = userbasicsinfoService.queryUserById(activityMonkey.getUserId());
								    activityMonkey.setStatus(Constant.STATUES_ONE);
								    activityMonkey.setGrantTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
								  	activityMonkey.setOrderNum(order_id);
								  	activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
								    // 添加流水
									Accountinfo account = new Accountinfo();
									account.setExpenditure(0.00);
									account.setExplan("现金转账");
									account.setIncome(activityMonkey.getRewardMoney());
									account.setIpsNumber(order_id);
									account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
									account.setUserbasicsinfo(userbasicsinfo);
									account.setAccounttype(plankService.accounttype(25L));
									account.setMoney(0.00);
									payLogService.updatePayLog(order_id, Constant.STATUES_ONE);
									plankService.saveAccount(account);// 添加流水账余额
									System.out.println("现金转账成功");
									Judge = true;
									LOG.error("宝付现金发放业务查询处理成功");
								} else if (state.equals("0")) {
									activityMonkey.setStatus(-1);
								    activityMonkey.setOrderNum("");
								    activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
									payLogService.updatePayLog(activityMonkey.getOrderNum(), -1);
								}
							}
						} else {
							LOG.error("非宝付现金发放业务查询返回数据--" + result + "----->订单号----->"+ activityMonkey.getOrderNum());
							return "-1";
						}
					}
				}
				if (!Judge && state.equals("-1")) {
					activityMonkey.setStatus(-1);
				    activityMonkey.setOrderNum("");
				    activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
					payLogService.updatePayLog(activityMonkey.getOrderNum(), -1);
				}
				return "1";
			} catch (Exception e) {
				LOG.error("宝付现金发放业务查询失败----->订单号----->" + activityMonkey.getOrderNum(),e);
				return "-1";
			}
		}
	
	/***
	 * 根据宝付Id和金额转账
	 * @param request
	 * @param userId
	 * @param status
	 * @param money
	 * @return
	 * @throws Exception
	 */
	public String ipsTranCertInfo(HttpServletRequest request, String pMerBillNo,Integer status,Double money){
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		if(loginuser.getRole().getId()!=3 && loginuser.getRole().getId()!=21){ //判断是否财务总监和财务审核
			return "4";
		}
		Userbasicsinfo userbasicsinfo=userbasicsinfoService.getUser(pMerBillNo);
	     if(userbasicsinfo!=null){
	    	    String orderNum="";
	    	    String nameInfo="";
	    	    Integer action=30; 
	    	    AcctTrans acctTran = new AcctTrans();
	    		acctTran.setMerchant_id(ParameterIps.getCert());
	    	    if(status==1){  //退款
	    	    	orderNum="TK"+ userbasicsinfo.getId()+"_"+loginuser.getId()+"_"+new Date().getTime();// 转账订单号
		    		acctTran.setOrder_id(orderNum);
	    			acctTran.setPayer_user_id(userbasicsinfo.getpMerBillNo());
	    			acctTran.setPayee_user_id(ParameterIps.getCert());// 收款
	    			acctTran.setPayer_type(0);
	    			acctTran.setPayee_type(1);// 收款
	    			action=25;
	    			nameInfo="退款：";
	    	    }else if(status==2){  //收入
	    	    	orderNum="JLBT"+ userbasicsinfo.getId()+"_"+loginuser.getId()+"_"+new Date().getTime();// 转账订单号
	    	    	acctTran.setOrder_id(orderNum);
					acctTran.setPayer_user_id(ParameterIps.getCert());
					acctTran.setPayee_user_id(userbasicsinfo.getpMerBillNo());// 收款
					acctTran.setPayer_type(1);
					acctTran.setPayee_type(0);// 收款
					nameInfo="收入：";
	    	    }
	    		acctTran.setAmount(money);
	    		acctTran.setFee(0.00);
	    		acctTran.setFee_taken_on(1);
	    		acctTran.setReq_time(new Date().getTime());
	    		try {
	    			String registerXml = ParseXML.accttrans(acctTran);
	    			ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
	    			nvps.add(new BasicNameValuePair("requestParams", registerXml));
	    			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml+ "~|~" + ParameterIps.getmerchantKey())));
	    			payLogService.savePayLog(registerXml, userbasicsinfo.getId(),loginuser.getId(), action, orderNum, money);
	    			LOG.error(nameInfo+"用户："+userbasicsinfo.getName()+"；宝付账号："+userbasicsinfo.getpMerBillNo()+"；订单号："+orderNum+"；金额："+money);
	    			String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
	    			result = result.replace("\"", "\'");
	    			crs cr = new crs();
	    			XStream xss = new XStream(new DomDriver());
	    			xss.alias(cr.getClass().getSimpleName(), cr.getClass());
	    			cr = (crs) xss.fromXML(result);
	    			String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~" + cr.getMsg()+ "~|~" + ParameterIps.getDes_algorithm());
	    			if (cr.getSign().equals(Md5sign)) {
	    				if (cr.getCode().equals("CSD000")) {
	    					if(transBonusesState(orderNum,String.valueOf(loginuser.getId()),userbasicsinfo.getId(),money,action)){
	    						// 流水账
		    					Accountinfo account = new Accountinfo();
		    					account.setExpenditure(0.00);
		    					if(status==1){
		    						account.setExplan("客户退款");
		    						account.setExpenditure(money);
		    						account.setAccounttype(plankService.accounttype(27L));
		    					}else if(status==2){
		    						account.setExplan("奖励补贴");
		    						account.setIncome(money);
		    						account.setAccounttype(plankService.accounttype(26L));
		    					}
		    					account.setIpsNumber(orderNum);
		    					account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		    					account.setUserbasicsinfo(userbasicsinfo);
		    					account.setFee(0.00);
		    					account.setMoney(0.00);
		    					plankService.saveAccount(account);// 保存流水账余额
		    					payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
	    					}else{
	    						return "3";
	    					}
	    				}else{
	    					return "3";
	    				}
	    			}
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    			return "-1";
	    		}
	     }else{
	    	 return "2";
	     }
    	return "1";
    }
	
//	/***
//	 *  批量现金转账
//	 * @param request
//	 * @param loanId
//	 * @return
//	 */
//	  public  String ipsTransBatchActivtiyMonkey(HttpServletRequest request){
//			Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
//			JSONObject json = new JSONObject();
//			json.element("transStatus", "1");
//			
//			if(loginuser.getRole().getId()!=3 && loginuser.getRole().getId()!=21){ //判断是否财务总监和财务审核
//				json.element("transStatus", "4");
//			}
//			List<ActivityMonkey> listActivityMonkey=activityMonkeyQueryService.getBatchActivityMonkeyList();
//	    	if(listActivityMonkey.size()>0){
//	    		JSONArray returnMsg = new JSONArray();
//	    		
//	    		for (int i = 0; i < listActivityMonkey.size(); i++) {
//	    			JSONObject subJson = new JSONObject();
//	    			
//	    			ActivityMonkey activityMonkey=listActivityMonkey.get(i);
//	    			Userbasicsinfo userbasicsinfo = userbasicsinfoService.queryUserById(activityMonkey.getUserId());
//	    			if(userbasicsinfo!=null){
//		    				if(userbasicsinfo.getpMerBillNo()!=null&&!userbasicsinfo.getpMerBillNo().equals("")){
//		    					String orderNum = "XJZZ" +StringUtil.getDateTime(userbasicsinfo.getId(),activityMonkey.getId());// 现金转账订单号
//				    			AcctTrans acctTran = new AcctTrans();
//								acctTran.setMerchant_id(ParameterIps.getCert());
//								acctTran.setOrder_id(orderNum);
//								acctTran.setPayer_user_id(ParameterIps.getCert());
//								acctTran.setPayee_user_id(userbasicsinfo.getpMerBillNo());// 收款
//								acctTran.setPayer_type(1);
//								acctTran.setPayee_type(0);// 收款
//								acctTran.setAmount(Double.valueOf(activityMonkey.getRewardMoney()));
//								acctTran.setFee(0.00);
//								acctTran.setFee_taken_on(1);
//								acctTran.setReq_time(new Date().getTime());
//								try {
//									String registerXml = ParseXML.accttrans(acctTran);
//									ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
//									nvps.add(new BasicNameValuePair("requestParams",registerXml));
//									nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml + "~|~"+ ParameterIps.getmerchantKey())));
//									payLogService.savePayLog(registerXml, userbasicsinfo.getId(),activityMonkey.getId(), 29, orderNum, 0.00, 0.00,activityMonkey.getRewardMoney());
//									activityMonkey.setOrderNum(orderNum);
//									activityMonkey.setStatus(Constant.STATUES_TWO);
//									activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
//									String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
//									result = result.replace("\"", "\'");
//									crs cr = new crs();
//									XStream xss = new XStream(new DomDriver());
//									xss.alias(cr.getClass().getSimpleName(), cr.getClass());
//									cr = (crs) xss.fromXML(result);
//									String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~"+ cr.getMsg() + "~|~"+ ParameterIps.getDes_algorithm());
//									if (cr.getSign().equals(Md5sign)) {
//										if (cr.getCode().equals("CSD000")) {
//											int action=29;//代表paylog action 现金转账
//											  if(transBonusesState(orderNum,activityMonkey.getId().toString(),userbasicsinfo.getId(),activityMonkey.getRewardMoney(),action)){
//												    activityMonkey.setStatus(Constant.STATUES_ONE);
//												    activityMonkey.setGrantTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
//												    activityMonkey.setGrantAdminId(loginuser.getId());
//												  	activityMonkey.setOrderNum(orderNum);
//												  	activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
//												  	
//												  	// 首投返现的现金到达用户账户后，触发一条短信提醒
//												  	int activityType = 0;
//												  	if (activityMonkey.getType() != null ) {
//												  		activityType = activityMonkey.getType().intValue();
//												  	}
//												  	// 5月首投活动
//												  	if (activityType == 14 || activityType == 15) {
//												  		String activityTypeName = "首投返现";
//												  		if (activityType == 15) {
//												  			activityTypeName = "周周惊喜大放送";
//												  		}
//													  	String realName = userbasicsinfo.getName();
//													  	String phone = userbasicsinfo.getUserrelationinfo().getPhone();
//													  	double money = activityMonkey.getRewardMoney();
//													  	smsService.sendSms4UserFirstInvest(realName, activityTypeName, phone, money);
//												  	}
//												  	
//												    // 添加流水
//													Accountinfo account = new Accountinfo();
//													account.setExpenditure(0.00);
//													account.setExplan("现金转账");
//													account.setIncome(activityMonkey.getRewardMoney());
//													account.setIpsNumber(orderNum);
//													account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
//													account.setUserbasicsinfo(userbasicsinfo);
//													account.setAccounttype(plankService.accounttype(25L));
//													account.setMoney(0.00);
//													payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
//													plankService.saveAccount(account);// 添加流水账余额
//													LOG.error("现金转账成功"+orderNum);
//													
//													subJson.element("operationStatus", 1);
//											  } else {
//												    activityMonkey.setStatus(-1);
//												    activityMonkey.setFailreason(cr.getCode()+cr.getMsg());
//												    activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
//												    LOG.error("现金转账失败"+orderNum);
//												    
//												    subJson.element("operationStatus", -1);
//												    subJson.element("failReason", cr.getCode() + cr.getMsg());
//												    continue;
//											  }
//										} else {
//											    activityMonkey.setStatus(-1);
//											    activityMonkey.setOrderNum("");
//											    activityMonkey.setFailreason(cr.getCode()+cr.getMsg());
//											    activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
//											    
//											    subJson.element("operationStatus", -1);
//											    subJson.element("failReason", cr.getCode() + cr.getMsg());
//											    continue;
//										}
//									}
//								} catch (Exception e) {
//									LOG.error("现金转账失败"+orderNum,e);
//									continue;
//								} 
//		    				}else{
//		    					continue;
//		    				}
//	    			}else{
//	    				continue;
//	    			}
//	    			
//	    			returnMsg.add(subJson);
//			 	}
//	    	} else{
//	    		json.element("transStatus", "2");
//	    	}
//	    	
//	    	return json.toString();
//	    }
	
	public  String ipsTransBatchActivtiyMonkey(HttpServletRequest request, String recordIds) {
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		
		// 判断是否财务总监和财务审核
		if (loginuser.getRole().getId() != 3 && loginuser.getRole().getId() != 21) { 
			return "4";
		}

		List<ActivityMonkey> listActivityMonkey = activityMonkeyQueryService.queryActivityMonkeyList(recordIds);
    	if (listActivityMonkey.size() > 0) {
    		
    		for (int i = 0; i < listActivityMonkey.size(); i++) {
    			
    			ActivityMonkey activityMonkey=listActivityMonkey.get(i);
    			Userbasicsinfo userbasicsinfo = userbasicsinfoService.queryUserById(activityMonkey.getUserId());
    			if (userbasicsinfo != null) {
	    				if (userbasicsinfo.getpMerBillNo() !=null &&
	    						!userbasicsinfo.getpMerBillNo().equals("")) {
	    					String orderNum = "XJZZ" +StringUtil.getDateTime(userbasicsinfo.getId(),activityMonkey.getId());// 现金转账订单号
			    			AcctTrans acctTran = new AcctTrans();
							acctTran.setMerchant_id(ParameterIps.getCert());
							acctTran.setOrder_id(orderNum);
							acctTran.setPayer_user_id(ParameterIps.getCert());
							acctTran.setPayee_user_id(userbasicsinfo.getpMerBillNo());// 收款
							acctTran.setPayer_type(1);
							acctTran.setPayee_type(0);// 收款
							acctTran.setAmount(Double.valueOf(activityMonkey.getRewardMoney()));
							acctTran.setFee(0.00);
							acctTran.setFee_taken_on(1);
							acctTran.setReq_time(new Date().getTime());
							try {
								String registerXml = ParseXML.accttrans(acctTran);
								ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
								nvps.add(new BasicNameValuePair("requestParams",registerXml));
								nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml + "~|~"+ ParameterIps.getmerchantKey())));
								payLogService.savePayLog(registerXml, userbasicsinfo.getId(),activityMonkey.getId(), 29, orderNum, 0.00, 0.00,activityMonkey.getRewardMoney());
								activityMonkey.setOrderNum(orderNum);
								activityMonkey.setStatus(Constant.STATUES_TWO);
								activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
								String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
								result = result.replace("\"", "\'");
								crs cr = new crs();
								XStream xss = new XStream(new DomDriver());
								xss.alias(cr.getClass().getSimpleName(), cr.getClass());
								cr = (crs) xss.fromXML(result);
								String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~"+ cr.getMsg() + "~|~"+ ParameterIps.getDes_algorithm());
								if (cr.getSign().equals(Md5sign)) {
									if (cr.getCode().equals("CSD000")) {
										int action=29;//代表paylog action 现金转账
										  if(transBonusesState(orderNum,activityMonkey.getId().toString(),userbasicsinfo.getId(),activityMonkey.getRewardMoney(),action)){
											    activityMonkey.setStatus(Constant.STATUES_ONE);
											    activityMonkey.setGrantTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
											    activityMonkey.setGrantAdminId(loginuser.getId());
											  	activityMonkey.setOrderNum(orderNum);
											  	activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
											  	
											  	// 首投返现的现金到达用户账户后，触发一条短信提醒
											  	int activityType = 0;
											  	if (activityMonkey.getType() != null ) {
											  		activityType = activityMonkey.getType().intValue();
											  	}
											  	// 5月首投活动
											  	if (activityType == 14 || activityType == 15) {
											  		String activityTypeName = "首投返现";
											  		if (activityType == 15) {
											  			activityTypeName = "周周惊喜大放送";
											  		}
												  	String realName = userbasicsinfo.getName();
												  	String phone = userbasicsinfo.getUserrelationinfo().getPhone();
												  	double money = activityMonkey.getRewardMoney();
												  	smsService.sendSms4UserFirstInvest(realName, activityTypeName, phone, money);
											  	}
											  	
											    // 添加流水
												Accountinfo account = new Accountinfo();
												account.setExpenditure(0.00);
												account.setExplan("现金转账");
												account.setIncome(activityMonkey.getRewardMoney());
												account.setIpsNumber(orderNum);
												account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
												account.setUserbasicsinfo(userbasicsinfo);
												account.setAccounttype(plankService.accounttype(25L));
												account.setMoney(0.00);
												payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
												plankService.saveAccount(account);// 添加流水账余额
												LOG.error("现金转账成功"+orderNum);
										  } else {
											    activityMonkey.setStatus(-1);
											    activityMonkey.setFailreason(cr.getCode()+cr.getMsg());
											    activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
											    LOG.error("现金转账失败"+orderNum);
											    continue;
										  }
									} else {
										    activityMonkey.setStatus(-1);
										    activityMonkey.setOrderNum("");
										    activityMonkey.setFailreason(cr.getCode()+cr.getMsg());
										    activityMonkeyQueryService.updateActivityMonkey(activityMonkey);
										    continue;
									}
								}
							} catch (Exception e) {
								LOG.error("现金转账失败"+orderNum,e);
								continue;
							} 
	    				} else {
	    					continue;
	    				}
    			} else {
    				continue;
    			}
		 	}
    	} else {
    		return "2";
    	}
    	
    	return "1";
    }
	 
	/**
	 * 调用宝付接口查询用户信息  
	 * @param request
	 * @return
	 */
	public String ipsUserInfo(String userIds, String billNos) {
		String[] userIdArray = userIds.split(",");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		JSONObject json = new JSONObject();
		
		try {
			P2pUserInfoQuery p2pQuery = new P2pUserInfoQuery(8);
			p2pQuery.setBillNo(billNos);
			p2pQuery.setStart_time("");
			p2pQuery.setEnd_time("");
			String userXml = ParseXML.p2pUserInfoQueryXml(p2pQuery);
			logger.info("请求xml参数");
			logger.info(userXml);
			nvps.add(new BasicNameValuePair("requestParams", userXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(userXml + "~|~" + ParameterIps.getmerchantKey())));
			String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL, nvps);
			Document doc = DocumentHelper.parseText(result);
			Element rootElement = doc.getRootElement(); // 获取根节点
			String code = rootElement.elementTextTrim("code");
			String msg = rootElement.elementTextTrim("msg");
			String sign = rootElement.elementTextTrim("sign");
			String md5Sign = CommonUtil.MD5(code + "~|~" + msg + "~|~" + ParameterIps.getDes_algorithm());
			
			// 签名验证通过
			if (sign.equals(md5Sign)) {
				// 宝付处理成功
				if (code.equals("CSD000")) {
					// 用户真实姓名
					String realName = null;
					String cardId = null;
					
					// 获取子节点crs下的子节点result
					Iterator resultIt = rootElement.elementIterator("result");
					if (resultIt.hasNext()) {
						Element resultElement = (Element) resultIt.next();
						json.element("queryStatus", 1);
						JSONArray returnMsg = new JSONArray();
						
						Iterator orderIt = resultElement.elementIterator("order");
						int index = 0;
						
						while (orderIt.hasNext()) {
							Element orderElement = (Element) orderIt.next();
							
							String state = orderElement.elementTextTrim("state");
							// 宝付返回的会员信息创建时间
							String createTime = orderElement.elementTextTrim("create_time");

							if (state.equals("1")) {	// 用户正常
								String userId = userIdArray[index++];
								Userbasicsinfo user = userbasicsinfoService.queryUserById(userId);
								Integer isAuthIps = user.getIsAuthIps();
								if (isAuthIps != null && isAuthIps == 1) {
									logger.info("用户宝付已授权，不需要补录信息！");
								} else {
									user.setIsAuthIps(1);
									user.setHasIpsAccount(1);
									user.setCardStatus(2);
									
									if (!StringUtils.isEmpty(createTime)) {
										// 只截取'yyyy-MM-dd HH:mm:ss'部分
										createTime = createTime.substring(0, 19);
										user.setAuthIpsTime(createTime);
										// 开户时间
										user.setpIpsAcctDate(createTime);
									}
									
									realName = UserInfoCache.getNameFromRedis(Long.valueOf(userId));
									if (!StringUtils.isEmpty(realName)) {
										user.setName(realName);
									} else {
										logger.info("从redis无法获取用户[" + userId + "]的真实姓名！");
									}
									cardId = UserInfoCache.getCardIdFromRedis(Long.valueOf(userId));
									if (!StringUtils.isEmpty(cardId)) {
										user.getUserrelationinfo().setCardId(cardId);
									} else {
										logger.info("从redis无法获取用户[" + userId + "]的身份证号码！");
									}
									
									userbasicsinfoService.update(user);
									logger.info("用户宝付未授权，补录信息成功！");
								}
								
								JSONObject userIdJSONObject = new JSONObject();
								userIdJSONObject.element("userId", userId);
								returnMsg.add(userIdJSONObject);
							}
						}
						
						json.element("returnMsg", returnMsg);
					} else {
						json.element("queryStatus", 3);
					}
				} else {
					json.element("queryStatus", -1);
				}
			} else {
				json.element("queryStatus", -1);
			}

		} catch (TemplateException e) {
			logger.error("解析freemarker模板操作异常！", e);
			json.element("queryStatus", -1);
		} catch (IOException e) {
			logger.error("读取freemarker模板异常！", e);
			json.element("queryStatus", -1);
		} catch (Exception e) {
			logger.error("调用宝付接口查询用户信息操作异常！", e);
			json.element("queryStatus", -1);
		}
		
		return json.toString();
	}
}