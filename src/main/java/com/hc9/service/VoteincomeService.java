package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.InterestIncreaseCard;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.VoteIncome;
import com.hc9.dao.impl.HibernateSupport;

@Service
public class VoteincomeService {
    /** 注入数据库底层操作层*/
    @Resource
    private HibernateSupport dao;
    
    @Resource
    private LoanSignQuery loanSignQuery;
    
	/***
	 * 20150817-lkl
	 * 加息卷
	 * 根据项目Id查询转账
	 * @param loanId
	 * @return
	 */
	public List<VoteIncome> getVoteIncomeList(String loanId){
		String sql="select * from voteIncome where status in(0,-1)  and   loanId=?";
		List<VoteIncome> voteIncomeList=dao.findBySql(sql, VoteIncome.class, loanId);
		return voteIncomeList;
	}
	
	/***
	 * 更新胜利方投票人参与投票所得收益信息表
	 * @param voteIncome
	 */
	  public void updateVoteIncome(VoteIncome voteIncome){
	    	dao.update(voteIncome);
	    }
	  
	  /***
	   * lkl-20150825
	   * 根据loanid生成加息记录
	   * @param loanId
	   */
	  public void saveVoteincome(String loanId){
		  Loansign loansign=loanSignQuery.getLoansign(loanId);
		   String sql="select * from interestincreasecard where loanrecordId in (select id from loanrecord where loanSign_id =?)";
		   List<InterestIncreaseCard> increaseCardList=dao.findBySql(sql, InterestIncreaseCard.class, loanId);
		   if(increaseCardList!=null&&increaseCardList.size()>0){
				 for (int i = 0; i < increaseCardList.size(); i++) {
					   InterestIncreaseCard increaseCard=increaseCardList.get(i);
					   Loanrecord loanrecord=loanSignQuery.getLoanRecord(increaseCard.getLoanrecordId().toString());
					   if(loanrecord!=null){
						    VoteIncome income=new VoteIncome();
						    income.setLoanId(loansign.getId());
						    income.setVoterId(loanrecord.getUserbasicsinfo().getId());
						    income.setLoanRecordId(loanrecord.getId());
						    income.setType(Constant.STATUES_TWO);
						    income.setStatus(Constant.STATUES_ZERO);
						    if(loansign.getType()==2){
						    	Double incomeMoney=Arith.round(Arith.mul(Arith.div(Arith.mul(loanrecord.getTenderMoney(), increaseCard.getInterestRate()), 12), loansign.getRemonth()), 2);
						    	income.setIncomeMoney(incomeMoney);
						    }else if(loansign.getType()==3){
						    	Double incomeMoney=Arith.round(Arith.mul(Arith.div(Arith.mul(loanrecord.getTenderMoney(), increaseCard.getInterestRate()), 360), loansign.getRemonth()), 2);
						    	income.setIncomeMoney(incomeMoney);
						    }
						    income.setCreateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
						    dao.save(income);
					   }
				}
		   }
	  }
	  
}
