package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.InterestIncreaseCard;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.VoteIncome;
import com.hc9.dao.impl.HibernateSupport;

@Service
public class InterestIncreaseCardService {

	@Resource
	private HibernateSupport dao;
	
	/***
	 * 根据Id查询interestIncreaseCard
	 * @param id
	 * @return
	 */
	public  InterestIncreaseCard getInterestIncreaseCard(Long id){
		InterestIncreaseCard interestIncreaseCard = dao.get(InterestIncreaseCard.class,id);
		return interestIncreaseCard;
	}
	
	/***
	 * 判断购买时间是否符合
	 * @param id
	 * @return
	 */
	public InterestIncreaseCard getIncreaseCard(Long id,Long userId){
		String sql="select * from interestIncreaseCard where id=? and userId=?  and  date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and date_format(beginTime,'%Y-%m-%d')<=date_format(now(),'%Y-%m-%d') and useFlag = 0";
		List<InterestIncreaseCard> inList=dao.findBySql(sql, InterestIncreaseCard.class, id,userId);
		if(inList.size()>0){
			return inList.get(0);
		}else{
			return null;
		}
	}
	
	/***
	 * 更新加息券表
	 * @param increaseCard
	 */
	public void udtInterestIncreaseCard(InterestIncreaseCard increaseCard){
		dao.update(increaseCard);
	}
	
	/***
	 * 根据loanrecord进行查询
	 * @param loanrecordId
	 * @return
	 */
	public InterestIncreaseCard getLoanRecordCard(Long loanrecordId){
		String sql="select * from interestIncreaseCard where loanrecordId=?";
		List<InterestIncreaseCard> listIncreaseCard=dao.findBySql(sql, InterestIncreaseCard.class, loanrecordId);
		if(listIncreaseCard.size()>0){
			return listIncreaseCard.get(0);
		}else{
			return null;
		}
	}
	
	/***
	 * 根据状态进行更新加息券表
	 * @param increaseCard
	 */
	public  void uptIncreaseCard(InterestIncreaseCard increaseCard){
		 if(increaseCard.getUseFlag()==1){
			 String sql="update interestIncreaseCard set useFlag="+increaseCard.getUseFlag()+" ,consumeTime='"+increaseCard.getConsumeTime().trim()+"' where id="+increaseCard.getId();
			 dao.executeSql(sql);
		 }else{
			 String sql="update interestIncreaseCard set useFlag="+increaseCard.getUseFlag()+" ,loanrecordId="+increaseCard.getLoanrecordId()+" where id="+increaseCard.getId();
			 dao.executeSql(sql);
		 }
	}
	
	/***
	 * lkl-20150825
	 * 流标时进行更改
	 * @param loanId
	 */
	  public void uptIncreaseCard(Long loanId){
		  String sql="select * from interestincreasecard where loanrecordId in (select id from loanrecord where loanSign_id =?)";
		   List<InterestIncreaseCard> increaseCardList=dao.findBySql(sql, InterestIncreaseCard.class, loanId);
		   if(increaseCardList!=null&&increaseCardList.size()>0){
				 for (int i = 0; i < increaseCardList.size(); i++) {
					   InterestIncreaseCard increaseCard=increaseCardList.get(i);
					   increaseCard.setConsumeTime("");
					   increaseCard.setLoanrecordId(null);
					   increaseCard.setUseFlag(Constant.STATUES_ZERO);
					   dao.update(increaseCard);
				 }
		   }
	  }
	  
	/***
	 * 判断新用户注册是否在活动期间内20151105-20151205
	 * @param userId
	 * @return
	 */
	public boolean getUserInterest(Long userId){
		  String sql="select * from userbasicsinfo where  id=? and DATE_FORMAT(createTime, '%Y-%m-%d')>=DATE_FORMAT('20151105', '%Y-%m-%d')  AND DATE_FORMAT(createTime, '%Y-%m-%d')<=DATE_FORMAT('20151205', '%Y-%m-%d') ";
	      List<Userbasicsinfo> list=dao.findBySql(sql, Userbasicsinfo.class, userId);
		  	if(list.size()>0){
				return true;
			}else{
				return false;
			}
	}
	
	/***
	 * 判断是否首投
	 * @param userId
	 * @return
	 */
	public boolean getLoanRecord(Long userId){
		String sql="select * from loanrecord where userbasicinfo_id=? and  isSucceed=1";
	     List<Loanrecord> list=dao.findBySql(sql, Loanrecord.class, userId);
	  	if(list.size()==1){
			return true;
		}else{
			return false;
		}
	}
	
	/***
	 * 活动时间：2015.11.5-2015.12.5
	 * @param loanrecord
	 */
	public  Double saveInterestIncreaseCard(Loanrecord loanrecord){
		Double interestRate=0.00;  //所加利息
		String time=DateUtils.format("yyyy-MM-dd HH:mm:ss");
		if(getUserInterest(loanrecord.getUserbasicsinfo().getId())){
				if(DateUtil.isStringDate(DateUtils.format("yyyyMMdd"),"20151105","20151205")){
						if(getLoanRecord(loanrecord.getUserbasicsinfo().getId())){
							 if(loanrecord.getLoansign().getRefunway()==1||loanrecord.getLoansign().getRefunway()==4){ //按月
								  if(loanrecord.getLoansign().getRemonth()==1){ //1月标
										  if(loanrecord.getTenderMoney()>=200){
											  InterestIncreaseCard interest=new InterestIncreaseCard();
											  interest.setUserId(loanrecord.getUserbasicsinfo().getId());
											  interest.setLoanrecordId(loanrecord.getId());
											  interest.setLowestUseMoney(loanrecord.getTenderMoney());
											  if(loanrecord.getTenderMoney()>=200&&loanrecord.getTenderMoney()<5000){
												   interestRate=0.005;
											  }else if(loanrecord.getTenderMoney()>=5000&&loanrecord.getTenderMoney()<10000){
												  interestRate=0.008;
											  }else if(loanrecord.getTenderMoney()>=10000&&loanrecord.getTenderMoney()<50000){
												  interestRate=0.01;
											  }else if(loanrecord.getTenderMoney()>=50000){
												  interestRate=0.012;
											  }
											  interest.setInterestRate(interestRate);
											  interest.setReceiveTime(time);
											  interest.setConsumeTime(time);
											  String beginTime=DateUtils.format("yyyy-MM-dd");
											  interest.setBeginTime(beginTime);
											  interest.setEndTime(DateUtil.getSpecifiedMonthAfter(beginTime, 1));
											  interest.setUseFlag(Constant.STATUES_ONE);
											  interest.setSourceType(Constant.STATUES_ONE);
											  dao.save(interest);
										  }
									  }else{ 
										  InterestIncreaseCard interest=new InterestIncreaseCard();
										  interest.setUserId(loanrecord.getUserbasicsinfo().getId());
										  interest.setLoanrecordId(loanrecord.getId());
										  interest.setLowestUseMoney(loanrecord.getTenderMoney());
										  if(loanrecord.getTenderMoney()>=100&&loanrecord.getTenderMoney()<5000){
											   interestRate=0.008;
										  }else if(loanrecord.getTenderMoney()>=5000&&loanrecord.getTenderMoney()<10000){
											  interestRate=0.01;
										  }else if(loanrecord.getTenderMoney()>=10000&&loanrecord.getTenderMoney()<50000){
											  interestRate=0.012;
										  }else if(loanrecord.getTenderMoney()>=50000){
											  interestRate=0.015;
										  }
										  interest.setInterestRate(interestRate);
										  interest.setReceiveTime(time);
										  interest.setConsumeTime(time);
										  String beginTime=DateUtils.format("yyyy-MM-dd");
										  interest.setBeginTime(beginTime);
										  interest.setEndTime(DateUtil.getSpecifiedMonthAfter(beginTime, 1));
										  interest.setUseFlag(Constant.STATUES_ONE);
										  interest.setSourceType(Constant.STATUES_ONE);
										  dao.save(interest);
								  }
						      }
					}
			  }
		 }
		 return interestRate;
	}
	
	
	 /***
	  * 根据标Id生成加息数据
	  * @param loanId
	  * @return
	  */
	 public  String saveInterestincreasecard(){
		  String sql="select * from loanrecord where tenderTime in( select min(tenderTime) from loanrecord  where userbasicinfo_id in (select id from userbasicsinfo where DATE_FORMAT(createTime, '%Y-%m-%d')>=DATE_FORMAT('20151105', '%Y-%m-%d')  AND DATE_FORMAT(createTime, '%Y-%m-%d')<=DATE_FORMAT('20151205', '%Y-%m-%d') and hasIpsAccount=1) and isSucceed=1 GROUP BY userbasicinfo_id) and tenderMoney=100 and subType=1 and DATE_FORMAT(tenderTime, '%Y-%m-%d')<DATE_FORMAT('20151120', '%Y-%m-%d')";
		  List<Loanrecord> listLoanrecord=dao.findBySql(sql, Loanrecord.class);
		  if(listLoanrecord!=null&&listLoanrecord.size()>0){
			    for (int i = 0; i < listLoanrecord.size(); i++) {
					      Loanrecord loanrecord=listLoanrecord.get(i);
					      if(loanrecord.getLoansign().getRemonth()>=3){
					    	  if(loanrecord.getLoansign().getStatus()>=6){
					    		  InterestIncreaseCard interest=new InterestIncreaseCard();
								  interest.setUserId(loanrecord.getUserbasicsinfo().getId());
								  interest.setLoanrecordId(loanrecord.getId());
								  interest.setLowestUseMoney(loanrecord.getTenderMoney());
								  interest.setInterestRate(0.008);
								  String tenderTime=loanrecord.getTenderTime().substring(0, 10);
								  interest.setReceiveTime(tenderTime);
								  interest.setConsumeTime(tenderTime);
								  interest.setBeginTime(tenderTime);
								  interest.setEndTime(DateUtil.getSpecifiedMonthAfter(tenderTime, 1));
								  interest.setUseFlag(Constant.STATUES_ONE);
								  interest.setSourceType(Constant.STATUES_ONE);
								  dao.save(interest);
								  VoteIncome income=new VoteIncome();
								   income.setLoanId(loanrecord.getLoansign().getId());
								    income.setVoterId(loanrecord.getUserbasicsinfo().getId());
								    income.setLoanRecordId(loanrecord.getId());
								    income.setType(Constant.STATUES_TWO);
								    income.setStatus(Constant.STATUES_ZERO);
								 	Double incomeMoney=Arith.round(Arith.mul(Arith.div(Arith.mul(100, 0.008), 12), loanrecord.getLoansign().getRemonth()), 2);
							    	income.setIncomeMoney(incomeMoney);
								    income.setCreateTime(loanrecord.getLoansign().getCreditTime());
								    dao.save(income);
					    	  }else{
						    		  InterestIncreaseCard interest=new InterestIncreaseCard();
									  interest.setUserId(loanrecord.getUserbasicsinfo().getId());
									  interest.setLoanrecordId(loanrecord.getId());
									  interest.setLowestUseMoney(loanrecord.getTenderMoney());
									  interest.setInterestRate(0.008);
									  String tenderTime=loanrecord.getTenderTime().substring(0, 10);
									  interest.setReceiveTime(tenderTime);
									  interest.setConsumeTime(tenderTime);
									  interest.setBeginTime(tenderTime);
									  interest.setEndTime(DateUtil.getSpecifiedMonthAfter(tenderTime, 1));
									  interest.setUseFlag(Constant.STATUES_ONE);
									  interest.setSourceType(Constant.STATUES_ONE);
									  dao.save(interest);
					    	  }
					      }
				}
             return "1";			    
		  }else{
			  return "2";
		  }
	 }
	 
}
