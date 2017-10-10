package com.hc9.common.quartz;

import java.util.List;

import javax.annotation.Resource;

import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.Withdraw;
import com.hc9.service.AutoWithdrawService;

/****
 * 查询提现
 * @author lkl
 *
 */
public class AutoWithdrawQuartz {
	
	@Resource
	private AutoWithdrawService autoWithdrawService;
	
	public void run(){
		LOG.info("清洁工出发查询提现.....");
		List<Withdraw>  listWithdraw=autoWithdrawService.getWithdrawList();
		if(listWithdraw.size()>0){
			String orderNumber="";
			for (int i = 0; i < listWithdraw.size(); i++) {
					Withdraw withdraw=listWithdraw.get(i);
					orderNumber+=withdraw.getStrNum()+",";
			}
			autoWithdrawService.autoWithdrawQuery(orderNumber.substring(0,orderNumber.length() - 1));
		}
	}

}
