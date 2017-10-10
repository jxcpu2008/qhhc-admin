package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Escrow;
import com.hc9.dao.entity.EscrowAccountinfo;
import com.hc9.dao.entity.EscrowRecharge;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class EscrowRechargeService {
	
	@Resource
	private HibernateSupport dao;
	
	/**
	 * 保存第三方担保充值记录
	 * @param escrowRecharge
	 */
	public void saveEscrowRecharge(EscrowRecharge escrowRecharge){
		dao.save(escrowRecharge);
	}
	
	/**
	 * 更新第三放担保充值记录
	 * @param escrowRecharge
	 */
	public void uptEscrowRecharge(EscrowRecharge escrowRecharge){
		dao.update(escrowRecharge);
	}
	
	/***
	 * 保存第三方担保流水表信息
	 * @param escrowAccountinfo
	 */
	public void saveEscrowAccountinfo(EscrowAccountinfo escrowAccountinfo){
		dao.save(escrowAccountinfo);
	}
	
	/***
	 * 根据Id查询escrowRecharge
	 * @param id
	 * @return
	 */
	public EscrowRecharge queryEscrowRecharge(Long id){
		String sql = "select * from escrow_recharge where id=?";
		EscrowRecharge escrowRecharge = dao.findObjectBySql(sql, EscrowRecharge.class, id);
		return escrowRecharge;
	}
	
	
	/**
	 * 宝付第三方担保充值记录orderid
	 */
	public EscrowRecharge escrowRechargeByOrderId(String orderid) {
		StringBuffer sb = new StringBuffer(
				"SELECT * FROM escrow_recharge WHERE orderNum=?");
		return dao.findObjectBySql(sb.toString(), EscrowRecharge.class, orderid);
	}
	
	@SuppressWarnings("rawtypes")
	public List escrowRechargePage(PageModel page, Escrow escrow,Adminuser adminuser) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("SELECT count(1)  from escrow_recharge e,escrow w where e.escrow_id=w.id");
		StringBuffer sqlbuffer = new StringBuffer("select e.id,w.name,w.staff_username,e.time,e.fee,e.rechargeAmount,e.merfee,e.feetakenon,e.additional_info,e.reAccount,e.orderNum,e.`status`,e.succ_time  from escrow_recharge e ,escrow w where e.escrow_id=w.id ");
		  if (null != escrow) {
	        	if (StringUtil.isNotBlank(escrow.getName())) {
	            	String name = "";
					try {
						name = java.net.URLDecoder.decode(escrow.getName(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
	                sqlbuffer.append(" and w.name like '%")
	                        .append(StringUtil.replaceAll(name))
	                        .append("%'");
	                countsql.append(" and w.name like '%")
	                        .append(StringUtil.replaceAll(name))
	                        .append("%'");
	            }
	         	if (StringUtil.isNotBlank(escrow.getStaffPhone())) {
	                sqlbuffer.append(" and w.staff_phone = ") .append(StringUtil.replaceAll(escrow.getStaffPhone()));
	                countsql.append(" and w.staff_phone = ") .append(StringUtil.replaceAll(escrow.getStaffPhone()));
	            }
	          	
	        	if (StringUtil.isNotBlank(escrow.getStaffIdcard())) {
	        	      sqlbuffer.append(" and w.staff_IDcard like '") .append(StringUtil.replaceAll(escrow.getStaffIdcard())).append("%'");
		              countsql.append(" and w.staff_IDcard  like '") .append(StringUtil.replaceAll(escrow.getStaffIdcard())).append("%'");
	            }
	        	
	        	if (escrow.getInAccredit()!=null) {
	                sqlbuffer.append(" and e.`status` =") .append(escrow.getInAccredit());
	                countsql.append(" and e.`status`=") .append(escrow.getInAccredit());
	            }
	        }
		if(!adminuser.getUsername().equals("admin")){
			 sqlbuffer.append(" and w.staff_username='").append(adminuser.getUsername().trim()+"'");
			 countsql.append(" and w.staff_username='").append(adminuser.getUsername().trim()+"'");
		 }
		sqlbuffer.append(" ORDER BY e.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}
	
}
