package com.hc9.service;
	
import java.io.File;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.hc9.controller.MyindexController;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Accountinfo;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Loandynamic;
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
 * 还款方法
 * @author lkl
 *
 */
@Service
public class LoanSignRepayMentRecordService {
	
	@Resource
	private HibernateSupport dao;
	
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
	
	/**分批还款相加金额*/
	private static double sumHkMoney=0d;
		
	
	/***
	 * 还款方法
	 * @param request
	 * @param repayId  还款记录Id
	 * @param repayMoney  实际还款金额
	 * @param adminuser   后台还款用户
	 * @return
	 */
	public  String shareLoanRepayMent(HttpServletRequest request,Long repayId, Double repayMoney){
			//得到还款期数信息；
	 	Repaymentrecord repaymentrecord = baseLoansignService .getRepaymentId(repayId);
	 	  // 得到利率信息;
		Costratio costratio = loanSignQuery.queryCostratio();
		double needMoney =0;
		// 得到滞纳金, 滞纳天数
		Map<String, String> map = loanManageService.getupPeridosDateAndMoney(repaymentrecord, costratio);
		double sumOverdueMoney = Double.parseDouble(map.get("total").toString());
		// 需要金额
		 needMoney = repaymentrecord.getMoney() + repaymentrecord.getMiddleMoney()
				+ repaymentrecord.getAfterMoney() + repaymentrecord.getPreRepayMoney()
				+ repaymentrecord.getMiddlePreRepayMoney()
				+ repaymentrecord.getAfterPreRepayMoney() + sumOverdueMoney;
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
			request.setAttribute("fmoney", Double.valueOf(df.format(repayMoney)));
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
	 		   sumHkMoney=0d;
	 			boolean  repayMent=false;
	 			for (int i = 0; i < iCount; i++) {
	 				if (i == iCount-1) {
	 					repayMent=true;
	 					if ( rcp.size() % costratio.getRepayNum() != 0) {
	 						  jCount =  rcp.size() % costratio.getRepayNum();
	 					}
	 				}
	 			  msg =	getRepayMentRecord(request,repaymentrecord,costratio,rcp,jCount,repayMent);
	 			  if(msg.equals("1")){
	 				  break;
	 			  }
		 		}
		}
		if(msg.equals("1")){
				//得到标信息
				Loansign loan = repaymentrecord.getLoansign();
				// 修改标的状态(最后一期)
				if(loan.getType()==2){  //项目
					 if(loan.getRefunway()==1){
						 if (repaymentrecord.getPeriods().equals( repaymentrecord.getLoansign().getRemonth())) {
								loan.setStatus(8);
								processingService.updateLoan(loan);
							}
					 }else if(loan.getRefunway()==2){
						 if (repaymentrecord.getPeriods().equals( repaymentrecord.getLoansign().getRemonth()/3)) {
								loan.setStatus(8);
								processingService.updateLoan(loan);
							}
					 }
						
				}else if(loan.getType()==3){  //天标
						loan.setStatus(8);
						processingService.updateLoan(loan);
				}
			return "WEB-INF/views/cardForword";
		}else if(msg.equals("2")){
		   return "WEB-INF/views/failure";	
		}else{
			return "WEB-INF/views/failure";
		}
	}
	
	public  String  getRepayMentRecord(HttpServletRequest request,Repaymentrecord repaymentrecord,Costratio costratio,List<Repaymentrecordparticulars> rcp, Integer jCount,boolean  repayMent){
		List<Payuser> payuser = new ArrayList<Payuser>();
		double outMoney = 0.00;
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
	     double sumOutMoney=0.00; //还款金额累积
			outMoney=Arith.round(Arith.add(upPeridosMoney,repaymentrecord.getMoney()+repaymentrecord.getPreRepayMoney()+repaymentrecord.getMiddleMoney()+repaymentrecord.getMiddlePreRepayMoney()+repaymentrecord.getAfterMoney()+repaymentrecord.getAfterPreRepayMoney()),2); //总计支出
		if (repaymentrecord.getRepayState() == 1) {
			String ordernum = "HB" +repaymentrecord.getLoansign().getId()+ "_"+new Date().getTime();// 还款订单号
			int j = 0;
			/** 用于保存待更新的还款明细记录信息 */
			List<Repaymentrecordparticulars> repayDetailList = new ArrayList<Repaymentrecordparticulars>();
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
						sumOutMoney=Arith.round(Arith.add(myMoney,sumOutMoney),2);
					}
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
						sumOutMoney=Arith.round(Arith.add(realMoney2,sumOutMoney),2);
					}
					info.setMiddleRealMoney(realMoney2);
					info.setFee(fee);
					if(repaymentrecord.getMiddleRealMoney()==null){
						repaymentrecord.setMiddleRealMoney(0.00);
					}
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
						sumOutMoney=Arith.round(Arith.add(realMoney3,sumOutMoney),2);
					}
					info.setAfterRealMoney(realMoney3);
					info.setFee(fee);
					if(repaymentrecord.getAfterRealMoney()==null){
						repaymentrecord.setAfterRealMoney(0.00);
					}
					repaymentrecord.setAfterRealMoney(Arith.round(Arith.add(repaymentrecord.getAfterRealMoney(),realMoney3),2)); //劣后实际利息=本期利息+上期预期滞纳金
					afterNum--;
				}
				info.setRepState(0);
				repayDetailList.add(info);
				number++;
				j++;
			}
			sumHkMoney=sumHkMoney+sumOutMoney;
			repayMentServices.updateRepayDetailList(repayDetailList);
			repayDetailList.clear();
			if(sumOutMoney==0){
				return "5";
			}
			if(sumOutMoney > outMoney){
				LOG.error("还款金额累积 " + sumOutMoney + " 大于 " + outMoney);
				return "4";
			}
			//分批还款时，判断还款金额累积等于总还款金额
			if(sumHkMoney==outMoney){
				repayMent=true;
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
										accountOne.setIncome(repayParticulars.getRealMoney());
									}else if(repayParticulars.getLoanType()==2){
										accountOne.setIncome(repayParticulars.getMiddleRealMoney());
									}else{
										accountOne.setIncome(repayParticulars.getAfterRealMoney());
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
									updateParticularsId(repayParticulars.getId());
								}
								//分批还款时
								if(!repayMent){
									/** 回款发送短信通知 */
									smsSendService.sendReturnMoneySmsNotify(payuser,loan.getName());
									emailSendService.sendReturnMoneyEmailNotify(payuser, loan.getName());
								}
								if(repayMent){
									//修改还款金额=0
									updateParticulars(repaymentrecord.getId());
									Double companyFee=repaymentrecord.getCompanyPreFee();
									repaymentrecord.setCompanyRealFee(companyFee); 
									repaymentrecord.setRepayTime(DateUtils .format("yyyy-MM-dd HH:mm:ss"));
									repaymentrecord.setRepayState(expensesInfo.getState());// 提前，按时，逾期
									repaymentrecord.setpIpsBillNo(ordernum);
									repaymentrecord.setpIpsTime2(DateUtils .format("yyyy-MM-dd"));
									repaymentrecord.setOverdueInterest(expensesInfo.getPenalty());// 逾期利息(本期逾期滞纳)
									processingService.updateRayment(repaymentrecord);
									//平台收取服务费
									if(loan.getFeeState()==2){
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
										 if(loan.getRefunway()==1){
											 if (repaymentrecord.getPeriods().equals( repaymentrecord.getLoansign().getRemonth())) {
												 baoFuLoansignService.ipsVoteIncomeZZ(request, loan.getId().toString());
												}
										 }else if(loan.getRefunway()==2){
											 if (repaymentrecord.getPeriods().equals( repaymentrecord.getLoansign().getRemonth()/3)) {
												 baoFuLoansignService.ipsVoteIncomeZZ(request, loan.getId().toString());
												}
										 }
									}else if(loan.getType()==3){  //天标
										 baoFuLoansignService.ipsVoteIncomeZZ(request, loan.getId().toString());
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
						accountUser.setUserbasicsinfo(loansign.getUserbasicsinfo());
						plankService.saveAccount(accountUser);// 添加流水账余额
						
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
    * 根据标id查询未还款
    * @param loanId
    * @return
    */
    public  List<Repaymentrecord> getRepayMent(Long loanId){
        String sql="select * from repaymentrecord where repayState in (1,3,5) and loanSign_id=?";
        List<Repaymentrecord> list=dao.findBySql(sql, Repaymentrecord.class, loanId);
        return list;
    }
    
    /***
     * 获取投资用户
     * @param loanId
     * @return
     */
    public List<Repaymentrecord> getreRepayMentRecord(Long loanId){
    	String sql="select *  from loanrecord where loanSign_id =?  and isSucceed=1 GROUP BY userbasicinfo_id ";
    	List<Repaymentrecord> list=dao.findBySql(sql, Repaymentrecord.class,loanId);
    	return list;
    }
    
	/***
	 * 当还款金额为0时，状态=0时，进行更改状态=1
	 * @param repaymentrecordId
	 */
	public void updateParticulars(Long repaymentrecordId){
		String sql="update  repaymentrecordparticulars set repState=1  where repaymentrecordId=? ";
		dao.executeSql(sql, repaymentrecordId);
	}
	
	/***
	 * 当还款金额为0时，状态=0时，进行更改状态=1
	 * @param rId
	 */
	public void updateParticularsId(Long rId){
		String sql="update  repaymentrecordparticulars set repState=1  where id=? and repState=0 ";
		dao.executeSql(sql, rId);
	}
    
	/** 还款后更新redis缓存相关信息 */
	public void updateUserRedisDataAfterRepayMent(Long loanId, Long repayId) {
		if(loanId != null) {
			String sql = "select distinct userid from repaymentrecordparticulars rcp where rcp.repaymentrecordId=?";
			List list = dao.findBySql(sql, repayId);
			if(list != null && list.size() > 0) {
				for(Object obj : list) {
					BigInteger bigObj = (BigInteger)obj;
					long userId = bigObj.longValue();
					baoFuLoansignService.updateRedisInfoAfterInvest(userId, loanId);
					System.out.println("还款记录" + repayId + "更新用户" + userId + "对应回款信息成功！");
					LOG.error("还款记录" + repayId + "更新用户" + userId + "对应回款信息成功！");
				}
			}
		}
	}
}
