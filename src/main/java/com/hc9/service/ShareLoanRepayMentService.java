package com.hc9.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.NameValuePair;
import org.springframework.stereotype.Service;

import com.hc9.common.util.Arith;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.ParameterIps;
import com.hc9.controller.MyindexController;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Loandynamic;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Repaymentrecordparticulars;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.BidInfo4;
import com.hc9.model.ExpensesInfo;
import com.hc9.model.Payuser;

@Service
public class ShareLoanRepayMentService {
	
	@Resource
	private LoanSignService loanSignService;

	@Resource
	private BaseLoansignService baseLoansignService;

	@Resource
	private LoanManageService loanManageService;

	@Resource
	private BorrowerFundService borrowerFundService;

	@Resource
	private ProcessingService processingService;

	/** loanSignQuery 公用借款标的查询 */
	@Resource
	private LoanSignQuery loanSignQuery;

	@Resource
	private UserInfoQuery userInfoQuery;

	@Resource
	private UserbasicsinfoService userbasicsinfoService;

	@Resource
	private MyindexController myindexController;

	private DecimalFormat df = new DecimalFormat("0.00");

	@Resource
	private PlankService plankService;

	@Resource
	private BaoFuService baoFuService;

	@Resource
	private PayLogService payLogService;
	
	@Resource
	private RepayMentServices repayMentServices;
	
	@Resource
	SmsSendService smsSendService;
	
	@Resource
	EmailSendService emailSendService;
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	
	private List<NameValuePair> nvps;
	
	private static int number=0;
	/***
	 * 还款(去掉去宝付的)
	 * @param repayId
	 * @param request
	 * @return
	 */
	public  String shareLoanRepayMentCopy(Long repayId){
		//修改还款数据
		loanManageService.updateRepaymen(repayId);
			//得到还款期数信息；
	 	Repaymentrecord repaymentrecord = baseLoansignService .getRepaymentId(repayId);
	    // 得到利率信息;
		Costratio costratio = loanSignQuery.queryCostratio();
	 // 得到滞纳金, 滞纳天数
		Map<String, String> map = loanManageService.getupPeridosDateAndMoney(repaymentrecord, costratio);
		double sumOverdueMoney = Double.parseDouble(map.get("total").toString());
		// 需要金额
		double needMoney = repaymentrecord.getMoney() + repaymentrecord.getMiddleMoney()
				+ repaymentrecord.getAfterMoney() + repaymentrecord.getPreRepayMoney()
				+ repaymentrecord.getMiddlePreRepayMoney()
				+ repaymentrecord.getAfterPreRepayMoney() + sumOverdueMoney;
	 	if(repaymentrecord.getLoansign().getUserbasicsinfo().getUserfundinfo().getCashBalance()<needMoney){
	 		 System.out.println(repaymentrecord.getLoansign().getId()+"_余额不足");
	 		 return "3";
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
	 			  msg=	getRepayMentRecordCopy(repaymentrecord,costratio,rcp,jCount,repayMent);
		 		}
		}
		if(msg.equals("1")){
				//得到标信息
				Loansign loan = repaymentrecord.getLoansign();
				// 修改标的状态(最后一期)
				if(loan.getType()==2){  //项目
						if (repaymentrecord.getLoansign().getRefunway() == 1) {
							if (repaymentrecord.getPeriods().equals( repaymentrecord.getLoansign().getRemonth())) {
								loan.setStatus(8);
								processingService.updateLoan(loan);
							}
						}
						if (repaymentrecord.getLoansign().getRefunway() == 2) {
							if (repaymentrecord .getPeriods() .equals(repaymentrecord.getLoansign().getRemonth() / 3)) {
								loan.setStatus(8);
								processingService.updateLoan(loan);
							}
						}
				}else if(loan.getType()==3){  //天标
						loan.setStatus(8);
						processingService.updateLoan(loan);
				}
			return "1";
		}else if(msg.equals("2")){
		   return "3";	
		}else{
			return "3";
		}
	}
	
	public  String  getRepayMentRecordCopy(Repaymentrecord repaymentrecord,Costratio costratio,List<Repaymentrecordparticulars> rcp, Integer jCount,boolean  repayMent){
		List<Map> listmap = new ArrayList<Map>();
		List<Payuser> payuser = new ArrayList<Payuser>();
		Double outMoney = null;
 		//计算得到借款人还款的本金、利息、违约（本期）; 
 		ExpensesInfo expensesInfo = borrowerFundService.getBorrowerFund(repaymentrecord, 0);
		//得到标信息
		Loansign loan = repaymentrecord.getLoansign();
		// 融资人
		Userbasicsinfo userinfo = repaymentrecord.getLoansign() .getUserbasicsinfo();
		
		//得到滞纳金,  滞纳天数（上期）
		Map map = loanManageService.getupPeridosDateAndMoney(repaymentrecord,costratio);
		
		//获得优先/夹层/劣后各还款人数
		int  priNum=loanManageService.getRecordparticularsCount(repaymentrecord.getId(), 1);
		int midNum=loanManageService.getRecordparticularsCount(repaymentrecord.getId(), 2);
		int afterNum=loanManageService.getRecordparticularsCount(repaymentrecord.getId(), 3);
		
		double sumPriOeroverdueMoney=0.00; //总优先滞纳金
		double sumMidOverdueMoney=0.00; //总夹层滞纳金
		double sumAfterOverdueMoney=0.00; //总劣后滞纳金
		
		double priOeroverdueMoney =0.00;
		double midOverdueMoney =  0.00;
		double afterOverdueMoney =0.00;
		
		//double companyFeeMoney=0.00;
		
 		double upPeridosMoney =Double.parseDouble(map.get("total").toString());
		double sumdate = Double.parseDouble(map.get("diffNum").toString());
         if(sumdate>0){
				  priOeroverdueMoney =Double.parseDouble(map.get("priOut").toString());//优先滞纳金=（优先总额*滞纳率*滞纳天数）
				  midOverdueMoney =   Double.parseDouble(map.get("midOut").toString());//夹层滞纳金=夹层百分比*剩余滞纳金
				  afterOverdueMoney = Double.parseDouble(map.get("aftOut").toString());//劣后滞纳金=劣后百分比*剩余滞纳金
				  
				  sumPriOeroverdueMoney=Double.parseDouble(map.get("priOut").toString());
				  sumMidOverdueMoney=Double.parseDouble(map.get("midOut").toString());
				  sumAfterOverdueMoney= Double.parseDouble(map.get("aftOut").toString());
				  //companyFeeMoney= Double.parseDouble(map.get("feeOut").toString());  //平台滞纳金
         }
         
 		outMoney=upPeridosMoney+repaymentrecord.getMoney()+repaymentrecord.getPreRepayMoney()+repaymentrecord.getMiddleMoney()+repaymentrecord.getMiddlePreRepayMoney()+repaymentrecord.getAfterMoney()+repaymentrecord.getAfterPreRepayMoney(); //总计支出
		if (repaymentrecord.getRepayState() == 1) {
			String ordernum = "HB" + new Date().getTime();// 还款订单号
			int j=0;
			while(j<jCount) {
				Repaymentrecordparticulars info = rcp.get(number);
				if (info.getLoanType() == 1) {// 优先
					Double fee =0.00;
 					//利息管理费
					fee=Arith.mul(info.getPreRepayMoney(),costratio.getPrioInvest());
					Double myMoney = Arith.round(Arith.add(info.getPreRepayMoney(),info.getMoney()),2);
					Double bidPriMoney=0.00;
					if(sumdate>0){
						    //算出滞纳金
							double bid = Arith.div(info.getLoanrecord().getTenderMoney(), repaymentrecord.getLoansign().getPriority(), 4);
							bidPriMoney=Arith.round(Arith.mul(bid,priOeroverdueMoney),2);
							if(priNum<2){
								bidPriMoney=sumPriOeroverdueMoney;
							}else{
								sumPriOeroverdueMoney=sumPriOeroverdueMoney-bidPriMoney; //计算剩余优先滞纳金
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
					}
					listmap.add(getListMap(repaymentrecord, info, ordernum, getuser.getUserfundinfo().getCashBalance(), info.getMoney(), info.getPreRepayMoney(),fee));
					info.setRealMoney(myMoney);
					info.setFee(fee);
					if(repaymentrecord.getRealMoney()==null){
						repaymentrecord.setRealMoney(0.00);
					}
					repaymentrecord.setRealMoney(Arith.round(Arith.add(repaymentrecord.getRealMoney(), myMoney),2));// 优先实际利息=本期利息+上期预期滞纳金
					priNum--;

				} else if (info.getLoanType() == 2) {// 夹层
					Double fee =0.00;
					Double bidMidMoney=0.00;
					//利息管理费
					fee=Arith.mul(info.getMiddlePreRepayMoney(),costratio.getMidInvest()); 
					Double realMoney2 =Arith.round(Arith.add(info.getMiddlePreRepayMoney(), info.getMiddleMoney()),2);
					if(sumdate>0){
						//算出滞纳金
						double bid = Arith.div(info.getLoanrecord().getTenderMoney(), repaymentrecord.getLoansign().getMiddle(), 4);
						bidMidMoney=Arith.round(Arith.mul(bid, midOverdueMoney),2);
						if(midNum<2){
							bidMidMoney=sumMidOverdueMoney;
						}else{
							sumMidOverdueMoney=sumMidOverdueMoney-bidMidMoney;  //计算剩余夹层滞纳金
						}
	 					realMoney2 = Arith.round(Arith.add(realMoney2,bidMidMoney),2);//加上滞纳金
					}
					// 宝付接口、参数
					Userbasicsinfo getuser = userbasicsinfoService.queryUserById(info.getUserbasicsinfo().getId());
					if(realMoney2>0){
						Payuser payu = new Payuser();
						payu.setAmount(realMoney2);// 个人得到的钱
						payu.setUser_id(getuser.getpMerBillNo());// 用户宝付账号
						payu.setId(getuser.getId().toString());
						payu.setFee(fee);// 手续费、管理费
						payu.setrId(info.getId().toString());  //还款明细Id
						payuser.add(payu);
					}
					listmap.add(getListMap(repaymentrecord, info, ordernum, getuser.getUserfundinfo().getCashBalance(), info.getMiddlePreRepayMoney(), info.getMiddlePreRepayMoney(),fee));
					info.setMiddleRealMoney(realMoney2);
					info.setFee(fee);
					repaymentrecord.setMiddleRealMoney(Arith.round(Arith.add(repaymentrecord.getMiddleRealMoney(),realMoney2),2));//夹层实际利息=本期利息+上期预期滞纳金
					midNum--;
				} else if (info.getLoanType() == 3) {// 劣后
					Double fee =0.00;
					//利息管理费
					fee=Arith.mul(info.getAfterPreRepayMoney(),costratio.getAfterInvest());//利息	
					Double realMoney3 = Arith.round(Arith.add(info.getAfterMoney() , info.getAfterPreRepayMoney()),2);
					Double bidAfterMoney=0.00;
					if(sumdate>0){
						//算出滞纳金
						double bid = Arith.div(info.getLoanrecord().getTenderMoney(), repaymentrecord.getLoansign().getAfter(), 4);
						bidAfterMoney=Arith.round(Arith.mul(bid, afterOverdueMoney),2);
						if(afterNum<2){
							bidAfterMoney=sumAfterOverdueMoney;
						}else{
							sumAfterOverdueMoney=sumAfterOverdueMoney-bidAfterMoney; //计算劣后滞纳金
						}
						realMoney3 = Arith.round(Arith.add(realMoney3,bidAfterMoney),2);//加上滞纳金
					}
					// 宝付接口、参数
					Userbasicsinfo getuser = userbasicsinfoService.queryUserById(info.getUserbasicsinfo().getId());
					if(realMoney3>0){
						Payuser payu = new Payuser();
						payu.setAmount(realMoney3);// 个人得到的钱
						payu.setUser_id(getuser.getpMerBillNo());// 用户宝付账号
						payu.setId(getuser.getId().toString());
						payu.setFee(fee);// 手续费、管理费
						payu.setrId(info.getId().toString());  //还款明细Id
						payuser.add(payu);
					}
					listmap.add(getListMap(repaymentrecord, info, ordernum, getuser.getUserfundinfo().getCashBalance(), info.getAfterMoney() , info.getAfterPreRepayMoney(),fee));
					info.setAfterRealMoney(realMoney3);
					info.setFee(fee);
					repaymentrecord.setAfterRealMoney(Arith.round(Arith.add(repaymentrecord.getAfterRealMoney(),realMoney3),2)); //劣后实际利息=本期利息+上期预期滞纳金
					afterNum--;
				}
				info.setRepState(0);
				loanManageService.updateRepaymentrecordParticulars(info);
				number++;
				j++;
				System.out.println(number);
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
				
							//更新还款明细
					/*		for(int i=0;i<jCount;i++){
								rcp.get(number-1).setRepState(1);
								loanManageService.updateRepaymentrecordParticulars(rcp.get(number-1));
							}*/
							// 更新收款人的用户余额
						if(repayMent){
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
											if(userinfo.getUserfundinfo().getOperationMoney()==null&&userinfo.getUserfundinfo().getOperationMoney().equals("")){
												userinfo.getUserfundinfo().setOperationMoney(userinfo.getUserfundinfo().getMoney());
											}
											inuser.getUserfundinfo().setOperationMoney(Arith.add(inuser.getUserfundinfo().getOperationMoney(), accountOne.getIncome()));
											accountOne.setMoney(inuser.getUserfundinfo().getOperationMoney());
											
											userbasicsinfoService.update(inuser);
											plankService.saveAccount(accountOne);// 添加流水账余额
											repayParticulars.setRepState(1);
											loanManageService.updateRepaymentrecordParticulars(repayParticulars);
									}
									Double companyFee=repaymentrecord.getCompanyPreFee();
									repaymentrecord.setCompanyRealFee(companyFee); 
									repaymentrecord.setRepayTime(DateUtils .format("yyyy-MM-dd HH:mm:ss"));
									repaymentrecord.setRepayState(expensesInfo.getState());// 提前，按时，逾期
									repaymentrecord.setpIpsBillNo(ordernum);
									repaymentrecord.setpIpsTime2(DateUtils .format("yyyy-MM-dd"));
									repaymentrecord.setOverdueInterest(expensesInfo.getPenalty());// 逾期利息(本期逾期滞纳)
									processingService.updateRayment(repaymentrecord);
									//平台收取服务费
							/*		if(loan.getFeeState()==2){
										ipsCompanyFee(loan, companyFee);
										outMoney=Arith.add(outMoney, companyFee).doubleValue();
									}*/
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
									
									// 更新流水20150506
									userinfo.getUserfundinfo().setOperationMoney(Arith.sub(userinfo.getUserfundinfo().getOperationMoney(), outMoney));
									account.setMoney(userinfo.getUserfundinfo().getOperationMoney());
									
									userbasicsinfoService.update(userinfo);
									plankService.saveAccount(account);// 添加流水账余额
									
									Loandynamic loandynamic=new Loandynamic();
									loandynamic.setLoanId(loan.getId());
									loandynamic.setUserId(loan.getUserbasicsinfo().getId());
									loandynamic.setTitle("还款"+loan.getName());
									loandynamic.setContext("还款"+loan.getName());
									loandynamic.setPublishTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
									loandynamic.setType(1);
									plankService.saveLoandynamic(loandynamic);
									return "1";
							}else{
								return "2";
							}
					
			
			} catch (Exception e) {
				e.printStackTrace();
				return "3";
			}
		} else {
			return "3";
	  }
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getListMap(Repaymentrecord repaymentrecord,
			Repaymentrecordparticulars info, String ordernum, Double rebalance,
			Double loanMoney, Double interest, Double manfee) {
		Map funcitonMap = new HashMap<String, Object>();
		funcitonMap.put("1", rebalance); // 用户余额
		funcitonMap.put("2", ordernum);// ipsnum
		if (info.getLoanType() == 1) {
			funcitonMap.put("3", loanMoney);// 本金
		} else if (info.getLoanType() == 2) {
			funcitonMap.put("3", loanMoney);// 本金
		} else if (info.getLoanType() == 3) {
			funcitonMap.put("3", loanMoney);// 本金
		}
		if (info.getLoanType() == 1) {
			funcitonMap.put("4", interest - manfee); // 利息
		} else if (info.getLoanType() == 2) {
			funcitonMap.put("4", interest - manfee); // 利息
		} else if (info.getLoanType() == 3) {
			funcitonMap.put("4", interest - manfee); // 利息
		}
		funcitonMap.put("5", DateUtils.format("yyyy-MM-dd HH:mm:ss")); // 时间
		funcitonMap.put("6", info.getUserbasicsinfo().getId());// 用户id
		funcitonMap.put("7", repaymentrecord.getLoansign().getId());// loanid
		funcitonMap.put("8", manfee); // 管理费
		return funcitonMap;
	}

}
