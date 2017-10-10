package com.hc9.common.quartz;

import java.util.List;

import javax.annotation.Resource;

import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.PlankService;

public class AutoLoanRecordQuartz {
	
	@Resource
	private PlankService plankService;
	
	private Loanrecord loanrecord;
	
	@Resource
	private LoanSignQuery loanSignQuery;
	
	/***
	 * 根据loanrecordId删除购买标状态=0待确认的loanrecord
	 */
	public void  run(){
		LOG.info("清洁工出发.....");
		List<Loanrecord>  loanrecordList=loanSignQuery.getLoanRecordList();
		if(loanrecordList.size()>0){
			for (int i = 0; i < loanrecordList.size(); i++) {
				loanrecord=loanrecordList.get(i);
				plankService.getLoanInfoRecord(loanrecord.getOrder_id());

			}
		}
 	}
}
