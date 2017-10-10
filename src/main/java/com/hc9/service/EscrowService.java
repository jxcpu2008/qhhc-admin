package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.commons.normal.Md5Util;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Escrow;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * 第三方担保服务 
 * 
 * @author  2015-01-20 
 *  
 */
@Service
public class EscrowService {

	@Resource
	private HibernateSupport dao;

	@Resource
	private UserInfoQuery userInfoQuery;

	@Resource
	private BaseLoansignService baseLoansignService;
	
	@Resource
	private RoleService roleService;

 

	/***
	 * 查询
	 * @param page
	 * @param shop
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Object> getEscrowList(PageModel page, Escrow escrow,Adminuser adminuser) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer("select count(id) from escrow e  where 1=1");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"select id,name,brief,history,team,mission,phone,address,staff_name, staff_phone,staff_IDcard,staff_email,staff_baofu,staff_baofu_create_time,in_accredit,accredit_time,in_baofu,staff_username,staff_money from  escrow  where 1=1 ");
		  if (null != escrow) {
	        	if (StringUtil.isNotBlank(escrow.getName())) {
	            	String name = "";
					try {
						name = java.net.URLDecoder.decode(escrow.getName(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
	                sqlbuffer.append(" and name like '%")
	                        .append(StringUtil.replaceAll(name))
	                        .append("%'");
	                countsql.append(" and e.name like '%")
	                        .append(StringUtil.replaceAll(name))
	                        .append("%'");
	            }
	         	if (StringUtil.isNotBlank(escrow.getStaffPhone())) {
	                sqlbuffer.append(" and staff_phone = ") .append(StringUtil.replaceAll(escrow.getStaffPhone()));
	                countsql.append(" and e.staff_phone = ") .append(StringUtil.replaceAll(escrow.getStaffPhone()));
	            }
	          	
	        	if (StringUtil.isNotBlank(escrow.getStaffIdcard())) {
	        	      sqlbuffer.append(" and staff_IDcard like '") .append(StringUtil.replaceAll(escrow.getStaffIdcard())).append("%'");
		              countsql.append(" and i.staff_IDcard  like '") .append(StringUtil.replaceAll(escrow.getStaffIdcard())).append("%'");
	            }
	        }
		 if(!adminuser.getUsername().equals("admin")){
			 sqlbuffer.append(" and staff_username='").append(adminuser.getUsername().trim()+"'");
			 countsql.append(" and e.staff_username='").append(adminuser.getUsername().trim()+"'");
		 }
		sqlbuffer.append(" order by id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}
	
	/***
	 * 查询所有的第三方担保信息
	 * @return
	 */
	public List<Escrow>  getAllEscrow(){
		String sql="select * from Escrow";
		List<Escrow>  listEscrow=dao.findBySql(sql, Escrow.class);
		return listEscrow;
	}
 

	 /***
	  *  根据ID进行查询
	  * @param id	
	  * @return
	  */
	public Escrow queryEscrowByid(Long id) {
		Escrow e =dao.get(Escrow.class,  id);
		return e;
	}
	
	
	/****
	 * 第三方担保根据宝付ID查询
	 * @param staffBaofu
	 * @return
	 */
	public Escrow queryEscrowStaffBaofu(String staffBaofu){
		String hql=" from Escrow where staffBaofu='"+staffBaofu+"'";
		return dao.find(hql).size()>0?(Escrow) dao.find(hql).get(0):null;
	}
	
	/***
	 * 根据第三方担保注册用户名查询登陆用户信息
	 * @param staffUserName
	 * @return
	 */
	public Adminuser queryAdminuser(String staffUserName){
		String hql=" from Adminuser a where a.username='"+staffUserName+"'";
		return dao.find(hql).size()>0?(Adminuser) dao.find(hql).get(0):null;
	}
	
	/***
	 * 根据登陆用户信息查询第三方担保信息
	 * @param staffUserName
	 * @return
	 */
	public Escrow queryEscrowName(String staffUserName){
		String hql=" from Escrow e where e.staffUserName='"+staffUserName+"'";
		return dao.find(hql).size()>0?(Escrow) dao.find(hql).get(0):null;
	}
	
	/***
	 * 新增或修改
	 * @param escrow
	 */
    public void udapteEscrow(Escrow escrow) {
    	if(escrow.getId()==null){
    		Adminuser adminuser=new Adminuser();
        	adminuser.setUsername(escrow.getStaffUserName());
        	adminuser.setRealname(escrow.getStaffName());
        	adminuser.setPhone(escrow.getStaffPhone());
        	adminuser.setEmail(escrow.getStaffEmail());
        	adminuser.setAddress(escrow.getAddress());
        	adminuser.setStatus(1);
        	adminuser.setPassword(Md5Util.execute("888888"));
        	adminuser.setRole(roleService.findById("18"));
        	dao.save(adminuser);
        	escrow.setInBaofu(0);   //是否注册宝付
        	escrow.setInAccredit(0);  //是否授权宝付
    	}
    	dao.saveOrUpdate(escrow);
    }
	
	/**
     * 批量删除
     * 
     * @param ids
     *            选中的id
     * @return boolean
     */
    public String delEscrow(String ids) {
        String isSuccess = "1";
        try{
        	 if (StringUtil.isNotBlank(ids)) {
     			// 根据“，”拆分字符串
     			String[] newids = ids.split(",");
     			// 要修改状态的编号
     			String delstr = "";
     			for (String idstr : newids) {
     				// 将不是空格和非数字的字符拼接
     				if (StringUtil.isNotBlank(idstr)
     						&& StringUtil.isNumberString(idstr)) {
     					delstr += idstr + ",";
     				}
     			}
     			if (delstr.length() > 0) {
     				  Escrow escrow=this.queryEscrowByid(Long.valueOf(delstr.substring(0, delstr.length() - 1)));
     				  Adminuser adminuser=this.queryAdminuser(escrow.getStaffUserName().trim());
     				  dao.delete(escrow);
     				  dao.delete(adminuser);
     			}
        	 }else{
        		 isSuccess="2";
        	 }
        }catch(Exception e){
        	 e.printStackTrace();
        	  isSuccess="2";
        }
        return isSuccess;
    }

	 
	/**
	 * 
	 * 根据担保人ID找  担保人信息
	 * @param threeId
	 * @return
	 */
	public Escrow queryEscrowInfoById(String escrowId) {
		String sql="select * from Escrow e where e.id=? ";
		Escrow escrow = dao.findObjectBySql(sql, Escrow.class, escrowId);
		return  escrow;
		}  


   
}
