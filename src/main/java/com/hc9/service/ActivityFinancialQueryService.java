package com.hc9.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.ActivityFinancial;
import com.hc9.dao.impl.HibernateSupport;


@Service
public class ActivityFinancialQueryService {
	
	@Resource
	private HibernateSupport dao;
	
	public ActivityFinancial getActivityFinancial(String userId ){
		String sql="select * from activity_financial where userId=?";
		return dao.findObjectBySql(sql, ActivityFinancial.class, userId);
	}
    
	public void addActivityFinancial(ActivityFinancial activityFinancial){
		dao.saveOrUpdate(activityFinancial);
	}
	
}
