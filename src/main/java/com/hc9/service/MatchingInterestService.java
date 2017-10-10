package com.hc9.service;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;
import org.w3c.dom.NodeList;

import com.hc9.common.constant.Constant;
import com.hc9.common.constant.PayURL;
import com.hc9.common.log.LOG;
import com.hc9.common.util.Arith;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Loandynamic;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Repaymentrecordparticulars;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.AcctTrans;
import com.hc9.model.BidInfo4;
import com.hc9.model.ExpensesInfo;
import com.hc9.model.Payuser;
import com.hc9.model.crs;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


/***
 * 等额等息
 * @author lkl
 *
 */
@Service
public class MatchingInterestService {
	
	/** dao */
	@Resource
	private HibernateSupport dao;
	
	@Resource
	private BaseLoansignService baseLoansignService;
	
	@Resource
	private LoanSignQuery loansignQuery;
	
	@Resource
	private SmsSendService smsSendService;
	
	@Resource
	private RepayMentServices repayMentServices;
	
	@Resource
	private PayLogService payLogService;
	
	@Resource
	private LoanManageService loanManageService;
	
	private static int number=0;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	private PlankService plankService;
	
	@Resource
	private ProcessingService processingService;
	
	@Resource
	private EmailSendService emailSendService;
	
	@Resource
	private LoanSignQuery loanSignQuery;
	
	@Resource
	private LoanSignService loanSignService;
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	
	private DecimalFormat df = new DecimalFormat("0.00");
	
	private List<NameValuePair> nvps;
	/***
	 * 还款明细表
	 * @param record
	 * @return
	 */
	public List<Repaymentrecordparticulars> repaymentRecordsParticulars(Repaymentrecord record) {
		// 打开当前还款的标的信息；
		Loansign loan = record.getLoansign();
		Double preRepayMoney = record.getPreRepayMoney();// 优先预计还款利息
		Double sumMoney=record.getMoney();  //优先预计还款本金
		// 找到当前还款信息的 所有投资人；
		String sql = "SELECT * FROM loanrecord  l where  l.loanSign_id=? and l.isSucceed=1";
		List<Loanrecord> loanrecod = dao.findBySql(sql, Loanrecord.class,loan.getId());
		List<Repaymentrecordparticulars> repas = new ArrayList<>();
		for (int i = 0; i < loanrecod.size(); i++) {
			Loanrecord lc = loanrecod.get(i);
			Repaymentrecordparticulars repa = new Repaymentrecordparticulars();
			repa.setUserbasicsinfo(lc.getUserbasicsinfo());
			//投资人=投资金额/期数+投资本金*标的年化利率/12
			Double money=0.00; //预计还款本金
			Double interest=0.00;//预计还款利息
			if(i+1==loanrecod.size()){
				money=sumMoney;
				interest=preRepayMoney;
			}else{
				money=Arith.round(Arith.div(lc.getTenderMoney(), loan.getRemonth()), 2);
				sumMoney=sumMoney-money;
				interest=Arith.round(Arith.div(Arith.mul(lc.getTenderMoney(), loan.getPrioRate()+ loan.getPrioAwordRate()), 12), 2);
				preRepayMoney=preRepayMoney-interest;
			}
			repa.setMoney(money);
			repa.setPreRepayMoney(interest);
			repa.setRepaymentrecord(record);
			repa.setRepState(-1);
			repa.setLoanType(lc.getSubType());
			repa.setLoanrecord(lc);
			repa.setFee(0.00);
			dao.save(repa);
			repas.add(repa);
		}
		return repas;
	}
	
	/***
	 * 生成还款计划
	 * @param loansign
	 * @throws ParseException
	 */
	public void repayMentRecord(Loansign loansign) throws ParseException{
		LOG.error("项目标="+loansign.getId()+"_"+loansign.getName()+"开始生成还款记录");
		//总利率-平台服务
		double realRate = loansign.getRealRate() - loansign.getCompanyFee();  
		//项目还款总金额
		double sumMoney=loansign.getIssueLoan();
		// 总利息
		double sumInterest =Arith.round(Arith.mul(Arith.div(Arith.mul(loansign.getIssueLoan(), realRate), 12), loansign.getRemonth()), 2);
		//总平台服务费
		double sumCompanyFee=Arith.round(Arith.mul(Arith.div(Arith.mul(loansign.getIssueLoan(), loansign.getCompanyFee()), 12), loansign.getRemonth()), 2);
		for (int i = 0; i < loansign.getRemonth(); i++) {
				Repaymentrecord record = new Repaymentrecord();
				double companyFee = 0.00; // 应收公司服务费
				double money=0.00; //本金
				double interest=0.00; //利息
				if(i+1==loansign.getRemonth()){
					companyFee=sumCompanyFee;
					money=sumMoney;
					interest=sumInterest;
				}else{
					companyFee = Arith.round(Arith.div(Arith.mul(loansign.getIssueLoan(),loansign.getCompanyFee()), 12), 2); // 公司服务费=总投资本金*公司服务费年利率/12
					sumCompanyFee=sumCompanyFee-companyFee;
					money=Arith.round(Arith.div(loansign.getIssueLoan(), loansign.getRemonth()), 2);  //本金=总投资本金/期数
					sumMoney=sumMoney-money;
					interest=Arith.round(Arith.div(Arith.mul(loansign.getIssueLoan(), realRate), 12), 2); //利息=总投资本金*标的年化利率/12
					sumInterest=sumInterest-interest;
				}
				record.setPreRepayMoney(interest);
				record.setMoney(money);
				record.setCompanyPreFee(companyFee); // 新增公司服务费
				record.setPeriods(i + 1);
				record.setLoansign(loansign);
				record.setPreRepayDate(DateUtils.add(Constant.DEFAULT_DATE_FORMAT, Calendar.MONTH,i + 1));// 预计还款日期
				record.setRepayState(1);// 未还款
				record.setAutoRepayAdvice(0);
				record.setMiddleMoney(0.00);
				record.setMiddlePreRepayMoney(0.00);
				record.setAfterMoney(0.00);
				record.setAfterPreRepayMoney(0.00);
				dao.save(record);
				repaymentRecordsParticulars(record);
		}
		LOG.error("项目标="+loansign.getId()+"_"+loansign.getName()+"生成还款记录结束");
	}


	
	/**
	 * 普通标逾期还款违约利息 (逾期违约金额 = 借款金额*逾期利率*逾期天数)
	 * @param money  借款金额
	 * @param scale  逾期利率
	 * @param day 逾期天数
	 * @return 返回逾期违约金额
	 */
	public Double overdueRepayment(Double money, int day) {
		String hql = "from Costratio c";
		List<Costratio> list = dao.find(hql);
		Costratio costratio = list.get(0);
		return Arith.mul(Arith.mul(money, costratio.getMatchingInterest()), day);
	}
	
	/**
	 * 等本等息
	 * @param repaymentInfo   还款对象
	 * @return 返回资金对象
	 */
	public ExpensesInfo getMonthlyInterest(Repaymentrecord repaymentInfo) {
		ExpensesInfo expensesInfo = new ExpensesInfo();
		// 违约金
		Double penalty = 0.00;
		// 当前距离还款日期的天数
		int timeNum = 0;
		try {
			timeNum = DateUtils.differenceDate("yyyy-MM-dd", DateUtils.format("yyyy-MM-dd"), repaymentInfo.getPreRepayDate());
			// 日期格式，放款时间，当前日期
			if (timeNum < 0) { // 逾期还款
				// 逾期违约的金额
				penalty =overdueRepayment(repaymentInfo.getLoansign().getIssueLoan(), Math.abs(timeNum));
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
	 * 获取上期还款滞纳天数
	 * @param upPeriodsNum
	 * @param repaymentInfo
	 */
	public Map<String, String> getupPeridosDateAndMoney(Repaymentrecord repaymentInfo, Costratio cost) {
		Map<String, String> map = new HashMap<String, String>();
		Double priOut = 0.0; // 优先滞纳金
		int diffNum = 0;
		/**逾期滞纳金比例*/
		Double matchingInterest = cost.getMatchingInterest();
        if (repaymentInfo.getLoansign().getType() == 2) {
			Integer periods = repaymentInfo.getPeriods();
			String sql = "SELECT * from repaymentrecord  r WHERE r.loanSign_id=? and periods="+ periods + " ";
			List<Repaymentrecord> list = dao.findBySql(sql,Repaymentrecord.class, repaymentInfo.getLoansign().getId());
			try {
				if (list != null) {
					if (list.get(0).getRepayState() == 1) {
						diffNum = DateUtils.differenceDateSimple(list.get(0).getPreRepayDate(), DateUtils.formatSimple(new Date()));
						if (diffNum > 0) {
							System.out.println("出现滞纳");
							/** 需要计算滞纳金的优先金额 =总金额*0.3%*滞纳天数*/
							priOut=Arith.round(Arith.mul(Arith.mul(repaymentInfo.getLoansign().getIssueLoan(),matchingInterest), diffNum), 2);
							System.out.println("滞纳天数=" + diffNum + "优先滞纳金="+ priOut);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		map.put("priOut", String.valueOf(priOut));
		map.put("diffNum", String.valueOf(diffNum));
		return map;
	}

	/***
	 * 还款
	 * @param request
	 * @param repayId
	 * @param repayMoney
	 * @param admin
	 * @return
	 */
	public  String shareLoanRepayMent(HttpServletRequest request,Repaymentrecord repaymentrecord, Double repayMoney){
		   // 得到利率信息;
			Costratio costratio = loanSignQuery.queryCostratio();
			double needMoney =0;
			// 得到滞纳金, 滞纳天数
			Map<String, String> map = getupPeridosDateAndMoney(repaymentrecord, costratio);
			double sumOverdueMoney = Double.parseDouble(map.get("priOut").toString());
			// 需要金额
			needMoney = repaymentrecord.getMoney()  + repaymentrecord.getPreRepayMoney() + sumOverdueMoney+repaymentrecord.getCompanyPreFee();
			//融资人的账户资金
			if(repaymentrecord.getLoansign().getUserbasicsinfo().getUserfundinfo().getCashBalance()<needMoney){
				 LOG.error(repaymentrecord.getLoansign().getId()+"还款余额不足");
				 request.setAttribute("error", "还款余额不足");
				 return "WEB-INF/views/failure";
			}
		 	if(needMoney==0){
		 		repaymentrecord.setRepayState(Constant.STATUES_TWO);
		 		repaymentrecord.setRepayTime(DateUtils .format("yyyy-MM-dd HH:mm:ss"));
		 		processingService.updateRayment(repaymentrecord);
		 		loanManageService.uptRepaymentrecordparticulars(repaymentrecord.getId());
		 		request.setAttribute("lName", repaymentrecord .getLoansign().getName());
				request.setAttribute("periods", repaymentrecord.getPeriods());
				request.setAttribute("fmoney", Double.valueOf(df.format(needMoney)));
				return "WEB-INF/views/cardForword";
		 	}
			//更新userfundinfo表
		 	loanManageService.updateOperationMoney();
	 		//得到当期还款信息；
			List<Repaymentrecordparticulars> rcp  =loanManageService.getRepaymentrecordparticulars(repaymentrecord);
			//得到利率信息; 
			String msg="";
		    if (repaymentrecord.getRepayState() == 1) {
					//根据还款人数进行处理
		 			int iCount = rcp.size() % costratio.getRepayNum() == 0 ?  rcp.size() / costratio.getRepayNum() :  rcp.size() / costratio.getRepayNum() + 1;
		 			int jCount = costratio.getRepayNum();
		 		    number=0;
		 			boolean  repayMent=false;
		 			for (int i = 0; i < iCount; i++) {
		 				if (i == iCount-1) {
		 					repayMent=true;
		 					if ( rcp.size() % costratio.getRepayNum() != 0) {
		 						  jCount =  rcp.size() % costratio.getRepayNum();
		 					}
		 				}
		 			  msg =	getRepayMentRecord(request,repaymentrecord,costratio,rcp,jCount,repayMent);
			 		}
			}
			if(msg.equals("1")){
					//得到标信息
					Loansign loan = repaymentrecord.getLoansign();
					// 修改标的状态(最后一期)
					if(loan.getType()==2){  //项目
						 if (repaymentrecord.getPeriods().equals( repaymentrecord.getLoansign().getRemonth())) {
								loan.setStatus(8);
								processingService.updateLoan(loan);
							}
					}
					return "WEB-INF/views/cardForword";
		}else if(msg.equals("2")){
		   return "WEB-INF/views/failure";	
		}else{
			return "WEB-INF/views/failure";
		}
}
	
	
	/***
	 * 还款处理操作
	 * @param request
	 * @param repaymentrecord
	 * @param costratio
	 * @param rcp
	 * @param jCount
	 * @param repayMent
	 * @param fundAccountLoan
	 * @param admin
	 * @return
	 */
	public  String  getRepayMentRecord(HttpServletRequest request,Repaymentrecord repaymentrecord,Costratio costratio,List<Repaymentrecordparticulars> rcp, Integer jCount,boolean  repayMent){
		List<Payuser> payuser = new ArrayList<Payuser>();
		double outMoney = 0.00;
 		//计算得到借款人还款的本金、利息、违约（本期）; 
 		ExpensesInfo expensesInfo = getMonthlyInterest(repaymentrecord);
		//得到标信息
		Loansign loan = repaymentrecord.getLoansign();
		// 融资人
		Userbasicsinfo userinfo = repaymentrecord.getLoansign() .getUserbasicsinfo();
		//得到滞纳金,  滞纳天数（上期）
		Map map = getupPeridosDateAndMoney(repaymentrecord,costratio);
		
		//获得优先/夹层/劣后各还款人数
		int  priNum=loanManageService.getRecordparticularsCount(repaymentrecord.getId(), 1);
		//优先逾期还款金额
 		double sumPeridosMoney =Double.parseDouble(map.get("priOut").toString());
 		//优先逾期天数
		double sumdate = Double.parseDouble(map.get("diffNum").toString());
		
        double sumOutMoney=0.00; //还款金额累积
 		outMoney=Arith.round(Arith.add(sumPeridosMoney,repaymentrecord.getMoney()+repaymentrecord.getPreRepayMoney()),2); //总计支出
		if (repaymentrecord.getRepayState() == 1) {
			String ordernum = "HB" +repaymentrecord.getLoansign().getId()+ "_"+new Date().getTime();// 还款订单号
			int j = 0;
			/** 用于保存待更新的还款明细记录信息 */
			List<Repaymentrecordparticulars> repayDetailList = new ArrayList<Repaymentrecordparticulars>();
			while(j<jCount) {
				Repaymentrecordparticulars info = rcp.get(number);
				if (info.getLoanType() == 1) {// 优先
					// 手续费、管理费
					Double fee =Arith.mul(info.getPreRepayMoney(),costratio.getPrioInvest());
					//还款总金额=本金+本息
					Double myMoney = Arith.round(Arith.add(info.getPreRepayMoney(),info.getMoney()),2);
					Double bidPriMoney=0.00;
					if(sumdate>0){
						    //算出滞纳金
						bidPriMoney=Arith.round(Arith.mul(Arith.mul(info.getLoanrecord().getTenderMoney(),costratio.getMatchingInterest()), sumdate), 2);
							if(priNum<2){
								bidPriMoney=sumPeridosMoney;
							}else{
								sumPeridosMoney=sumPeridosMoney-bidPriMoney; //计算剩余优先滞纳金
							}
							myMoney =Arith.round(Arith.add(myMoney,bidPriMoney),2);//加上滞纳金
					}
					// 宝付接口、参数
					Userbasicsinfo getuser = userbasicsinfoService.queryUserById(info.getUserbasicsinfo().getId());
					if(myMoney>0){
						Payuser payu = new Payuser();
						payu.setAmount(myMoney);// 个人得到的钱
						payu.setUser_id(getuser.getpMerBillNo());// 用户宝付账号
						payu.setFee(fee);// 手续费、管理费
						payu.setId(getuser.getId().toString());
						payu.setrId(info.getId().toString());  //还款明细Id
						payuser.add(payu);
						sumOutMoney=Arith.round(Arith.add(myMoney,sumOutMoney),2);
					}
					info.setRealMoney(myMoney);
					info.setFee(fee);
					if(repaymentrecord.getRealMoney()==null){
						repaymentrecord.setRealMoney(0.00);
					}
					repaymentrecord.setRealMoney(Arith.round(Arith.add(repaymentrecord.getRealMoney(), myMoney),2));// 优先实际利息=本期利息+上期预期滞纳金
					priNum--;
				} 
				info.setRepState(0);
				repayDetailList.add(info);
				number++;
				j++;
			}
			repayMentServices.updateRepayDetailList(repayDetailList);
			repayDetailList.clear();
			if(sumOutMoney > outMoney){
				LOG.error("还款金额累积： " + sumOutMoney + " 大于实际还款金额： " + outMoney);
				return "4";
			}
			
			// 宝付还标接口
				BidInfo4 bi = new BidInfo4();
				bi.setCus_id(loan.getId());
				bi.setCus_name(loan.getName());
				bi.setBrw_id(userinfo.getpMerBillNo());
				bi.setReq_time(String.valueOf(new Date().getTime()));
				bi.setVoucher_id(userinfo.getpMerBillNo());
				bi.setVoucher_fee(costratio.getVoucherFee());
				bi.setSpecial(1);
				bi.setFee(0.00);// 手续费、管理费
				bi.setPayuser(payuser);
				bi.setMerchant_id(ParameterIps.getCert());
				bi.setAction_type(4);
				bi.setOrder_id(ordernum);
				try {
					String readxml = ParseXML.bidinfo4XML(bi);
					nvps = new ArrayList<NameValuePair>();
					nvps.add(new BasicNameValuePair("requestParams", readxml));
					nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(readxml + "~|~" + ParameterIps.getmerchantKey())));
					payLogService.savePayLog(readxml, userinfo.getId(), loan.getId(), 7, ordernum, bi.getFee(), bi.getFee(),sumOutMoney,repaymentrecord.getId());
					String result = CommonUtil .excuteRequest( PayURL.REPAYMRNTTESTURL, nvps);
					result = result.replace("\"", "\'");
					crs cr = new crs();
					XStream xss = new XStream(new DomDriver());
					xss.alias(cr.getClass().getSimpleName(), cr.getClass());
					cr = (crs) xss.fromXML(result);
					String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~" + cr.getMsg() + "~|~" + ParameterIps.getDes_algorithm());
					if (cr.getSign().equals(Md5sign)) {
						if (cr.getCode().equals("CSD000")) {
							String msg = repayMentServices.shareBonusState(ordernum,loan,userinfo);
							if(msg.equals("CSD000")){
								//更新还款明细
								/*		for(int i=0;i<jCount;i++){
											rcp.get(number-1).setRepState(1);
											loanManageService.updateRepaymentrecordParticulars(rcp.get(number-1));
										}*/
								// 更新收款人的用户余额
									for (Payuser payuserinfo : payuser) {
										Userbasicsinfo inuser = userbasicsinfoService .queryUserById(Long.valueOf(payuserinfo .getId()));
										Repaymentrecordparticulars repayParticulars=loanManageService.getParticulars(payuserinfo);
										// 流水账
										Accountinfo accountOne = new Accountinfo();
										accountOne.setExpenditure(0.00);
										accountOne.setExplan("投资回款");
										if(repayParticulars.getLoanType()==1){
											accountOne.setIncome(repayParticulars.getRealMoney().doubleValue());
										}else if(repayParticulars.getLoanType()==2){
											accountOne.setIncome(repayParticulars.getMiddleRealMoney().doubleValue());
										}else{
											accountOne.setIncome(repayParticulars.getAfterRealMoney().doubleValue());
										}
										accountOne.setFee(repayParticulars.getFee()); //还款手续费
										accountOne.setIpsNumber(ordernum);
										accountOne.setLoansignId(String.valueOf(loan.getId()));// 标id（项目id）
										accountOne.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
										accountOne.setUserbasicsinfo(inuser);
																	
										accountOne.setAccounttype(plankService.accounttype(20L));
										// 更新流水20150506
										inuser.getUserfundinfo().setOperationMoney(Arith.add(inuser.getUserfundinfo().getOperationMoney(), accountOne.getIncome()));
										accountOne.setMoney(inuser.getUserfundinfo().getOperationMoney());
																	
										userbasicsinfoService.update(inuser);
										plankService.saveAccount(accountOne);// 添加流水账余额
										
										updateParticulars(repayParticulars.getId());
									}
									//分批还款时
									if(!repayMent){
										/** 回款发送短信通知 */
										smsSendService.sendReturnMoneySmsNotify(payuser,loan.getName());
										emailSendService.sendReturnMoneyEmailNotify(payuser, loan.getName());
									}
									if(repayMent){
										Double companyFee=repaymentrecord.getCompanyPreFee();
										repaymentrecord.setCompanyRealFee(companyFee); 
										repaymentrecord.setRepayTime(DateUtils .format("yyyy-MM-dd HH:mm:ss"));
										repaymentrecord.setRepayState(expensesInfo.getState());// 提前，按时，逾期
										repaymentrecord.setpIpsBillNo(ordernum);
										repaymentrecord.setpIpsTime2(DateUtils .format("yyyy-MM-dd"));
										repaymentrecord.setOverdueInterest(expensesInfo.getPenalty());// 逾期利息(本期逾期滞纳)
										processingService.updateRayment(repaymentrecord);
										//平台收取服务费
										if(loan.getRefunway()==4){
											ipsCompanyFee(loan, companyFee);
											outMoney=Arith.add(outMoney, companyFee);
										}
										// 流水账
										Accountinfo account = new Accountinfo();
										account.setExpenditure(outMoney);
										account.setExplan("项目还款");
										account.setIncome(0.00);
										account.setIpsNumber(ordernum);
										account.setLoansignId(String.valueOf(loan.getId()));// 标id（项目id）
										account.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
										account.setUserbasicsinfo(userinfo);
										account.setAccounttype(plankService.accounttype(4L));
										account.setFee(0.00);
																
										if(userinfo.getUserfundinfo().getOperationMoney()==null&&userinfo.getUserfundinfo().getOperationMoney().equals("")){
											userinfo.getUserfundinfo().setOperationMoney(userinfo.getUserfundinfo().getMoney());
										}
										// 更新流水20150506
										userinfo.getUserfundinfo().setOperationMoney(Arith.sub(userinfo.getUserfundinfo().getOperationMoney(), outMoney));
										account.setMoney(userinfo.getUserfundinfo().getOperationMoney());
																
										userbasicsinfoService.update(userinfo);
										plankService.saveAccount(account);// 添加流水账余额
																
										String  title="#";
										String context="#";
										try {   
											File f = new File(request.getRealPath("/")+"WEB-INF/classes/config/context/msg/loandynamic.xml");   
											DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
											DocumentBuilder builder = factory.newDocumentBuilder();   
											org.w3c.dom.Document doc = builder.parse(f);   
											NodeList nl = doc.getElementsByTagName("loandynamic");   
											for (int i = 0; i < nl.getLength(); i++) {   
												title=doc.getElementsByTagName("title").item(i).getFirstChild().getNodeValue();
												context=doc.getElementsByTagName("context").item(i).getFirstChild().getNodeValue();
											}   
										} catch (Exception e) {   
											e.printStackTrace();   
										}   
										String periods="第"+repaymentrecord.getPeriods()+"期";
										if(loan.getType()==2){  //项目
												if (repaymentrecord.getPeriods().equals( repaymentrecord.getLoansign().getRemonth())) {
													periods="";
											}
										}
										title=title.replaceAll("#",loan.getName()+periods+"已于"+DateUtils.format("yyyy-MM-dd"));
										context=title.replaceAll("#",	loan.getName()+periods+"已于"+DateUtils.format("yyyy-MM-dd"));
										Loandynamic loandynamic=new Loandynamic();
										loandynamic.setLoanId(loan.getId());
										loandynamic.setUserId(loan.getUserbasicsinfo().getId());
										loandynamic.setTitle(title);
										loandynamic.setContext(context);
										loandynamic.setPublishTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
										loandynamic.setType(1);
										plankService.saveLoandynamic(loandynamic);
										
										//20150817-lkl加息转账
										if(loan.getType()==2){  //项目
											 if(loan.getRefunway()==4){
												 if (repaymentrecord.getPeriods().equals( repaymentrecord.getLoansign().getRemonth())) {
													      baoFuLoansignService.ipsVoteIncomeZZ(request, loan.getId().toString());
													}
											 }
										}
										request.setAttribute("lName", repaymentrecord .getLoansign().getName());
										request.setAttribute("periods", repaymentrecord.getPeriods());
										request.setAttribute("fmoney", Double.valueOf(df.format(outMoney)));
										/** 回款发送短信通知 */
										smsSendService.sendReturnMoneySmsNotify(payuser,loan.getName());
										emailSendService.sendReturnMoneyEmailNotify(payuser, loan.getName());
										return "1";
								} else {
									return "2";
								}
							}else if(msg.equals("CSD333")){
								for (Payuser payuserinfo : payuser) {
									Repaymentrecordparticulars repayParticulars=loanManageService.getParticulars(payuserinfo);
									repayParticulars.setRepState(-1);
									loanManageService.updateRepaymentrecordParticulars(repayParticulars);
								}
								return "3";
							}else{
								return "3";
							}
						} else {
							return "3";
						}
					} else {
						return "3";
					}
				} catch (Exception e) {
					LOG.error("还款失败！", e);
					return "WEB-INF/views/failure";
				}
			} else {
				return "3";
		  }
	}
	
	/***
	 * 平台收取服务费
	 * @param request
	 * @param response
	 * @param loansign
	 * @param companyFee
	 */
	public void  ipsCompanyFee(Loansign loansign,double companyFee){
		// 获取后台操作人员信息
		String orderNum = "FW" +  StringUtil.getDateTime(loansign.getUserbasicsinfo().getId(), loansign.getId());// 收取平台服务费转账订单号
		AcctTrans acctTran = new AcctTrans();
		acctTran.setMerchant_id(ParameterIps.getCert());
		acctTran.setOrder_id(orderNum);
		acctTran.setPayer_user_id(loansign.getUserbasicsinfo().getpMerBillNo());
		acctTran.setPayee_user_id(ParameterIps.getCert());// 收款
		acctTran.setPayer_type(0);
		acctTran.setPayee_type(1);// 收款
		acctTran.setAmount(companyFee);
		acctTran.setFee(0.00);
		acctTran.setFee_taken_on(1);
		acctTran.setReq_time(new Date().getTime());
		try {
			String registerXml = ParseXML.accttrans(acctTran);
			ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("requestParams",registerXml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(registerXml + "~|~"+ ParameterIps.getmerchantKey())));
			payLogService.savePayLog(registerXml, loansign.getUserbasicsinfo().getId(),loansign.getId(), 21, orderNum, 0.00, 0.00,companyFee);
			String result = CommonUtil.excuteRequest(PayURL.TRANSFERURL, nvps);
			result = result.replace("\"", "\'");
			crs cr = new crs();
			XStream xss = new XStream(new DomDriver());
			xss.alias(cr.getClass().getSimpleName(), cr.getClass());
			cr = (crs) xss.fromXML(result);
			String Md5sign = CommonUtil.MD5(cr.getCode() + "~|~"+ cr.getMsg() + "~|~"+ ParameterIps.getDes_algorithm());
			if (cr.getSign().equals(Md5sign)) {
				if (cr.getCode().equals("CSD000")) {
						Accountinfo accountUser = new Accountinfo();
					    accountUser.setExpenditure(companyFee);
					    accountUser.setExplan("平台收取服务费");
						accountUser.setIncome(0.00);
						accountUser.setIpsNumber(orderNum);
						accountUser.setLoansignId(String.valueOf(loansign.getId()));// 标id（项目id）
						accountUser.setTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
						accountUser.setAccounttype(plankService.accounttype(17L));
						plankService.saveAccount(accountUser);// 添加流水账余额
						
						if(loansign.getFee()==null){
							loansign.setFee(0.00);
						}
						loansign.setFee(Arith.add(loansign.getFee(), companyFee));
						
						if(loansign.getFeeMoney()-loansign.getFee()==0){
							loansign.setFeeState(Constant.STATUES_ONE);
						}
						
					    loanSignQuery.updateLoansign(loansign);
						
					    System.out.println("收取平台服务费="+companyFee);
						payLogService.updatePayLog(orderNum, Constant.STATUES_ONE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("平台收取服务费报错");
		} 
	} 
	
	/***
	 * 当还款金额为0时，状态=0时，进行更改状态=1
	 * @param rid
	 */
	public void updateParticulars(Long rid){
		String sql="update  repaymentrecordparticulars set repState=1  where id=? and repState=0 ";
		dao.executeSql(sql, rid);
	}

}
