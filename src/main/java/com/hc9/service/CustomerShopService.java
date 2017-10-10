package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class CustomerShopService {
	

	@Resource
	private HibernateSupport dao;
	
	public List<Object> getCustomerShopList(PageModel page, Userbasicsinfo user) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer("select count(r.id) from userbasicsinfo u ,shop_record r ,userrelationinfo i  where  r.user_id=u.id and u.id=i.user_id ");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"select r.id,u.userName,u.`name`,i.phone,i.cardId,r.fee,r.isSucceed,r.tenderMoney,r.orderNum,r.tenderTime,r.uptTime,r.webOrApp,(select s.shop_name from shop s where s.id=r.shop_id) from userbasicsinfo u ,shop_record r ,userrelationinfo i  where  r.user_id=u.id and u.id=i.user_id  ");
		  if (null != user) {
	        	if (StringUtil.isNotBlank(user.getName())) {
	            	String name = "";
					try {
						name = java.net.URLDecoder.decode(user.getName(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
	                sqlbuffer.append(" and u.`name` like '%") .append(StringUtil.replaceAll(name)).append("%'");
	                countsql.append(" and u.`name` like '%").append(StringUtil.replaceAll(name)).append("%'");
	            }
	        	
	        	if (StringUtil.isNotBlank(user.getUserName())) {
	            	String userName = "";
					try {
						userName = java.net.URLDecoder.decode(user.getUserName(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
	                sqlbuffer.append(" and u.userName like '%").append(StringUtil.replaceAll(userName)).append("%'");
	                countsql.append(" and u.userName like '%") .append(StringUtil.replaceAll(userName)) .append("%'");
	            }
	        	
	          	if (StringUtil.isNotBlank(user.getStaffNo())) {
	                sqlbuffer.append(" and i.phone = ") .append(StringUtil.replaceAll(user.getStaffNo()));
	                countsql.append(" and i.phone = ") .append(StringUtil.replaceAll(user.getStaffNo()));
	            }
	          	
	        	if (StringUtil.isNotBlank(user.getCreateTime())) {
	        	      sqlbuffer.append(" and i.cardId like '") .append(StringUtil.replaceAll(user.getCreateTime())).append("%'");
		                countsql.append(" and i.cardId  like '") .append(StringUtil.replaceAll(user.getCreateTime())).append("%'");
	            }
	        	if (user.getUserType()!=null) {
	                sqlbuffer.append(" and r.isSucceed =") .append(user.getUserType());
	                countsql.append(" and r.isSucceed =") .append(user.getUserType());
	            }
	        }
		sqlbuffer.append(" order by id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}

}
