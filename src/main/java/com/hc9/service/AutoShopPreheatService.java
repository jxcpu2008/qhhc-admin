package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.hc9.common.util.DateUtil;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.entity.Shop;

@Service("autoShopPreheatService")
public class AutoShopPreheatService {
	
	
	@Resource
	private HibernateSupportTemplate dao;
	
	/***
	 * 根据预热开始时间加上预热天数与当前时间作比较
	 * @param state   =2   预热中的
	 * @return
	 */
	public  List<Shop> getShopList(){
		StringBuffer hql=new StringBuffer("select * from Shop p where p.state=2");
		hql.append(" and DATE_FORMAT(DATE_ADD(p.preheatstar_time,INTERVAL p.preheat DAY), '%Y-%m-%d')>='").append(DateUtil.format("yyyy-MM-dd")).append("'");
		hql.append(" ORDER BY p.id ASC");
		Session session = dao.getSession();
		List<Shop> shopList = (List<Shop>) dao.fillQuery(session.createQuery(hql.toString()), new Object[] {}).setFirstResult(0).setMaxResults(5).list();
		return shopList;
	}

	
}
