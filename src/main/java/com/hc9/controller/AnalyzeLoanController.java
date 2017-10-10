package com.hc9.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.log.LOG;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.model.RechargeModel;
import com.hc9.service.AnalyzeLoanService;
/**
 * 推广数据统计
 * @author frank
 *
 */
@Controller
@RequestMapping("/analyzeLoan")
@CheckLogin(value = CheckLogin.ADMIN)
public class AnalyzeLoanController {
	@Resource
	private AnalyzeLoanService analyzeLoanService;
	
	@Resource
	private RechargeModel modelRecharge;
	/**
	 * 获取标的完成时间数据
	 * @param request
	 */
	@RequestMapping("/finishedInfo")
	public void getFinishedLoanInfo(HttpServletRequest request,HttpServletResponse response){
		//1.获取标信息
		List<Loansign> loans=analyzeLoanService.getFinishedLoansign(50000.0,1000000.0);
		if(loans==null) return;
		List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
		Map<String,String> loanData;
		
		//2.获取标对应的投资信息
		for(Loansign ls:loans){
			loanData=new HashMap<>();
			loanData.put("标的名", ls.getName());
			String publishTime=ls.getPublishTime();
			//投资记录按id排序.第一条即开始投标时间，最后一条即完成时间
			List<Loanrecord> records=analyzeLoanService.getLoanrecords(ls.getId());
			
			if(records==null) continue; //没有投资记录继续下一个
			
			String firstInvestTime=records.get(0).getTenderTime();
			String lastInvestTime=records.get(records.size()-1).getTenderTime();
			List<Integer> prio=new ArrayList<>();//存放优先的id
			List<Integer> mid=new ArrayList<>();//存放夹层的id
			for(int i=0;i<records.size();i++){
				if(records.get(i).getSubType()==1){
					prio.add(i);
					continue;
				}
				if(records.get(i).getSubType()==2){
					mid.add(i);
					continue;
				}
			}
			long finishedTime=1;
			try {
				finishedTime = DateUtils.differentHour(firstInvestTime, lastInvestTime, "yyyy-MM-dd HH:mm:ss");
				double finished10WPerTime=100000.0*finishedTime/ls.getIssueLoan();//每10w完成时间
				loanData.put("每10W标的额完成时间", String.valueOf(finished10WPerTime));
				loanData.put("首笔投资时间",String.valueOf(DateUtils.differentHour(publishTime, firstInvestTime, "yyyy-MM-dd HH:mm:ss")));
				if(mid.size()>0){
					loanData.put("首笔夹层投资时间",String.valueOf(DateUtils.differentHour(publishTime, records.get(Integer.parseInt(mid.get(0).toString())).getTenderTime(), "yyyy-MM-dd HH:mm:ss")));
					
					loanData.put("夹层投完时间",String.valueOf(DateUtils.differentHour(publishTime, records.get(Integer.parseInt(mid.get(mid.size()-1).toString())).getTenderTime(), "yyyy-MM-dd HH:mm:ss")));
				}else{
					loanData.put("首笔夹层投资时间","无夹层投资记录");
					loanData.put("夹层投完时间","无夹层投资记录");
				}
				
				if(prio.size()>0){
					
					loanData.put("首笔优先投资时间",String.valueOf(DateUtils.differentHour(publishTime,records.get(Integer.parseInt(prio.get(0).toString())).getTenderTime(), "yyyy-MM-dd HH:mm:ss")));
					
					loanData.put("优先投完时间",String.valueOf(DateUtils.differentHour(publishTime,records.get(Integer.parseInt(prio.get(prio.size()-1).toString())).getTenderTime(), "yyyy-MM-dd HH:mm:ss")));
				}else{
					loanData.put("首笔优先投资时间","无优先投资记录");
					loanData.put("优先投完时间","无优先投资记录");
				}
				
				dataList.add(loanData);
			} catch (ParseException e) {
				LOG.error(e.getMessage());
			}

		}
		//4.输出xls
		String title="标数据汇总";
		Integer[] column={20,20,20,20,20,20,20};
		String[] header={"标的名","每10W标的额完成时间","首笔投资时间","首笔夹层投资时间","首笔优先投资时间","夹层投完时间","优先投完时间"};
		modelRecharge.downloadExcel(title, column, header, dataList, response);
		
	}
}
