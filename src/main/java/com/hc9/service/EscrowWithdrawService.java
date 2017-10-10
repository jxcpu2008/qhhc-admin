package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Escrow;
import com.hc9.dao.entity.EscrowWithdraw;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class EscrowWithdrawService {
	
	@Resource
	private HibernateSupport dao;
	
	/**
	 * 保存第三方担保提现记录
	 * @param escrowRecharge
	 */
	public void saveEscrowWithdraw(EscrowWithdraw escrowWithdraw){
		dao.save(escrowWithdraw);
	}
	
	/***
	 * 更新第三放担保提现记录
	 * @param escrowWithdraw
	 */
	public void uptEscrowWithdraw(EscrowWithdraw escrowWithdraw){
		dao.update(escrowWithdraw);
	}
	
	
	/**
	 * 根据ID查询第三方担保提现记录
	 * @param id
	 * @return
	 */
	public EscrowWithdraw queryEscrowWithdraw(Long id){
		String sql = "select * from escrow_withdraw where id=?";
		EscrowWithdraw escrowWithdraw = dao.findObjectBySql(sql, EscrowWithdraw.class, id);
		return escrowWithdraw;
	}
	
	/**
	 * 宝付第三方担保提现记录orderid
	 */
	public EscrowWithdraw escrowWithdrawByOrderId(String orderid) {
		StringBuffer sb = new StringBuffer(
				"SELECT * FROM escrow_withdraw WHERE pIpsBillNo=?");
		return dao.findObjectBySql(sb.toString(), EscrowWithdraw.class, orderid);
	}
	
	@SuppressWarnings("rawtypes")
	public List escrowWithdrawPage(PageModel page, Escrow escrow,Adminuser adminuser) {
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("SELECT count(1)  from escrow_withdraw e ,escrow w where e.escrow_id=w.id");
		StringBuffer sqlbuffer = new StringBuffer("select e.id,w.name,w.staff_username,e.amount,e.withdrawAmount,e.remark,e.time,e.pIpsBillNo,e.mer_fee,e.fee_taken_on,e.state,e.applytime,e.fee  from escrow_withdraw e ,escrow w where e.escrow_id=w.id");
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
	                sqlbuffer.append(" and e.state=") .append(escrow.getInAccredit());
	                countsql.append(" and e.state =") .append(escrow.getInAccredit());
	            } 
		  }
		if(!adminuser.getUsername().equals("admin")){
			 sqlbuffer.append(" and w.staff_username='").append(adminuser.getUsername().trim()+"'");
			 countsql.append(" and w.staff_username='").append(adminuser.getUsername().trim()+"'");
		 }
		sqlbuffer.append(" order by e.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}
}
