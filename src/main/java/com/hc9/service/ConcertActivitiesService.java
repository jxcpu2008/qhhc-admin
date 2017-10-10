package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.StatisticsUtil;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.report.ConcertActivityVo;


@Service
public class ConcertActivitiesService {
	@Resource
	private HibernateSupport dao;
	
	/**演唱会活动查询*/
	@SuppressWarnings("rawtypes")
	public List<ConcertActivityVo> concertActivitiesPage(ConcertActivityVo concertActivityVo,String startTime,String stopTime,Integer ticketNumber){
		List<ConcertActivityVo> resultList=new ArrayList<ConcertActivityVo>();
		
		String selectSql="select u.userName,u.name,r.phone,u.createTime,l.tenderTime,l.tenderMoney,IFNULL(lo.name,'未知') AS loanSignName,p.prizeNum,p.prizeType";
		String fromSql="  from userbasicsinfo u,loanrecord l,userrelationinfo r,loansign lo,prizeDetail p "
				+ "where u.id=r.user_id and u.id=l.userbasicinfo_id and u.id=p.userId and lo.id=l.loanSign_id  and l.id=p.loanrecordid and p.prizeType=17";
		/**按时间查询*/
		fromSql = connection(startTime, stopTime, fromSql);
		/** 按电话或真实姓名筛选 */
		if (concertActivityVo.getMobilePhone() != null && concertActivityVo.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(concertActivityVo.getMobilePhone(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			fromSql += "  and (u.`name` like '%" + name + "%' or r.phone like '%"
					+ concertActivityVo.getMobilePhone() + "%')";
		}
		String orderBy="  ORDER BY u.createTime DESC";
		String querySql=selectSql+fromSql+ orderBy; 
		List list=dao.findBySql(querySql);
		
		if(list!=null && list.size()>0){
			for(Object obj:list){
				Object[] arr=(Object[]) obj;
				ConcertActivityVo vo=new ConcertActivityVo();
				vo.setUserName(StatisticsUtil.getStringFromObject(arr[0]));
				vo.setName(StatisticsUtil.getStringFromObject(arr[1]));
				vo.setMobilePhone(StatisticsUtil.getStringFromObject(arr[2]));
				vo.setCreateTime(StatisticsUtil.getStringFromObject(arr[3]));
				vo.setTenderTime(StatisticsUtil.getStringFromObject(arr[4]));
				vo.setTenderMoney(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)arr[5]));
				vo.setLoanSignName(StatisticsUtil.getStringFromObject(arr[6]));
				vo.setTicketNumber(StatisticsUtil.getIntegerFromObject(arr[7]));
				vo.setRegisterSource("聚橙网");
				resultList.add(vo);
			}
		}
		/**按门票进行筛选*/
		resultList=filterByTicket( resultList, ticketNumber);
		return resultList;
	}
	
	/**按门票筛选*/
	public List<ConcertActivityVo> filterByTicket(List<ConcertActivityVo> resultList,Integer ticketNumber){
		List<ConcertActivityVo> finalList=new ArrayList<ConcertActivityVo>();
		if(resultList!=null && resultList.size()>0){
			if(ticketNumber!=null && ticketNumber>0){
				for(int i=0;i<resultList.size();i++){
					ConcertActivityVo vo=resultList.get(i);
					boolean flag=false;
					int ticket=vo.getTicketNumber();
					if(ticketNumber.intValue()==ticket){
						flag=true;
					}
					if(flag){
						finalList.add(vo);
					}
				}
			}else{
				return resultList;
			}
		}
		return finalList;
	}
	
	
	/** 注册时间 */
	public String connection(String beginTime, String endTime, String fromSql) {
		if (beginTime != null && !"".equals(beginTime.trim())) {
			fromSql += " AND DATE_FORMAT(u.createTime, '%Y-%m-%d')>=DATE_FORMAT('" + beginTime + "', '%Y-%m-%d') ";
		}

		if (endTime != null && !"".equals(endTime.trim())) {
			fromSql += " AND DATE_FORMAT(u.createTime, '%Y-%m-%d')<=DATE_FORMAT('" + endTime + "', '%Y-%m-%d') ";
		}
		return fromSql;
	}

	
	
	/**演唱会活动统计数据处理*/
	public List<Map<String, String>> handConcertDetail(List<ConcertActivityVo> list){
		List<Map<String, String>> content=new ArrayList<Map<String, String>>();
		if(list!=null && list.size()>0){
			for(ConcertActivityVo vo:list){
				Map<String, String> map=new HashMap<String,String>();
				map.put("用户名", vo.getUserName());
				map.put("真实姓名", vo.getName());
				map.put("手机号", vo.getMobilePhone());
				map.put("注册时间", vo.getCreateTime());
				map.put("认购金额", vo.getTenderMoney()+"");
				map.put("认购时间", vo.getTenderTime());
				map.put("认购产品", vo.getLoanSignName());
				int ticketNumber=vo.getTicketNumber();
				String ticketName="无";
				if(ticketNumber==1){
					ticketName="1张";
				}else if(ticketNumber==2){
					ticketName="2张";
				}
				map.put("门票张数", ticketName);
				map.put("注册渠道", vo.getRegisterSource()+"");
				content.add(map);
			}
			return content;
		}
		return content;
	}
}
