package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Shop;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class ShopManageService {

	/** 注入数据库操作层 */
	@Resource
	private HibernateSupport dao;
	
	@Resource
	private ShopService  shopService;
	
	
	
	/***
	 * 查询店铺列表的数据
	 * @param page
	 * @param shop
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Object> getShopList(PageModel page, Shop shop) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer("select count(p.id) from shop p   where 1=1 ");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				" select p.id,p.shop_name,p.founder,p.raise_funds,p.self_funds,p.proIncomeProportion,p.real_funds,p.pShopNum,p.pIndustry1,p.pIndustry2,p.founder_email,p.state,"
				+ "p.preheat,p.create_time,(select username from adminuser where id=adminuser_idshop) as adminame , (select username from userbasicsinfo  where id=p.user_id) as userName from shop p  where 1=1  ");
			if(shop.getState() != null && !"".equals(shop.getState())){
			    	sqlbuffer.append(" and p.state = "+shop.getState());
				    countsql.append(" and p.state  ="+shop.getState());
			}else{
					 sqlbuffer.append(" and p.state in (4,5) ");
					 countsql.append("  and p.state in (4,5) ");
		    }
		sqlbuffer.append(connectionFinancingTimeSql(shop.getCreateTime(),shop.getTeamProfiles()));
		countsql.append(connectionFinancingTimeSql(shop.getCreateTime(),shop.getTeamProfiles()));
		sqlbuffer.append(" order by p.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}
	
	public String connectionFinancingTimeSql(String beginDate,String endDate){
		String sql = "";
		if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(p.financing_time, '%Y-%m-%d')>=DATE_FORMAT('"+ beginDate+ "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(p.financing_time, '%Y-%m-%d')<=DATE_FORMAT('" + endDate + "', '%Y-%m-%d') ";
        }
		return sql;
	}
	
	
	/***
	 * 查询店铺放款/流标列表的数据
	 * @param page
	 * @param shop
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Object> getshopCreditList(PageModel page, Shop shop,String numShop) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer("select count(p.id) from shop p , paylog l where p.order_sn=l.order_sn ");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				" select p.id,p.shop_name,p.founder,p.raise_funds,p.self_funds,p.proIncomeProportion,p.real_funds,p.pShopNum,p.pIndustry1,p.pIndustry2,p.founder_email,p.state, l.pay_status,"
				+ "p.preheat,p.create_time,(select username from adminuser where id=adminuser_idshop) as adminame , (select username from userbasicsinfo  where id=p.user_id) as userName,p.fee   from  shop p , paylog l where p.order_sn=l.order_sn ");
			if(shop.getState() != null && !"".equals(shop.getState())){
			    	sqlbuffer.append(" and p.state = "+shop.getState());
				    countsql.append(" and p.state  ="+shop.getState());
			}else{
				 if(numShop.equals("1")){
					 sqlbuffer.append(" and p.state in (6,7,8) ");
					 countsql.append("  and p.state in (6,7,8) ");
				 }else if(numShop.equals("2")){
					 sqlbuffer.append(" and p.state =9");
					 countsql.append("  and p.state =9");
				 }
		    }
		sqlbuffer.append(connectionSql(shop.getCreateTime(),shop.getTeamProfiles()));
		countsql.append(connectionSql(shop.getCreateTime(),shop.getTeamProfiles()));
		sqlbuffer.append(" order by p.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}

	
	/***
	 * 查询店铺审核的数据
	 * @param page
	 * @param shop
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Object> getShopListExamine(PageModel page, Shop shop,String stateNum) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer("select count(p.id) from shop p, shop_examine e  WHERE p.id=e.shop_id ");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				" select p.id,p.shop_name,p.founder,p.raise_funds,p.self_funds,p.proIncomeProportion,p.real_funds,p.pShopNum,p.pIndustry1,p.pIndustry2,p.founder_email,e.state,"
				+ "p.preheat,p.create_time,(select username from adminuser where id=adminuser_idshop) as adminame  , (select username from userbasicsinfo  where id=p.user_id) as userName  from shop p, shop_examine e where  p.id=e.shop_id");
		if(stateNum.equals("1")||stateNum.equals("4")){
			sqlbuffer.append(" and e.state= "+stateNum);
			countsql.append(" and e.state="+stateNum);
		}
		
		if(stateNum.equals("1")){
			sqlbuffer.append(connectionApprovalSql(shop.getCreateTime(),shop.getTeamProfiles()));
			countsql.append(connectionApprovalSql(shop.getCreateTime(),shop.getTeamProfiles()));
		}else if(stateNum.equals("4")){
			sqlbuffer.append(connectionExamineSql(shop.getCreateTime(),shop.getTeamProfiles()));
			countsql.append(connectionExamineSql(shop.getCreateTime(),shop.getTeamProfiles()));
		}
		sqlbuffer.append(" order by p.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}
	
	/***
	 * 待审批查询
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public String connectionApprovalSql(String beginDate,String endDate){
		String sql = "";
		if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(e.approval_time, '%Y-%m-%d')>=DATE_FORMAT('"+ beginDate+ "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(e.approval_time, '%Y-%m-%d')<=DATE_FORMAT('" + endDate + "', '%Y-%m-%d') ";
        }
		return sql;
	}
	
	/***
	 * 待审核查询
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public String connectionExamineSql(String beginDate,String endDate){
		String sql = "";
		if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(e.examine_time, '%Y-%m-%d')>=DATE_FORMAT('"+ beginDate+ "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(e.examine_time, '%Y-%m-%d')<=DATE_FORMAT('" + endDate + "', '%Y-%m-%d') ";
        }
		return sql;
	}
	
	
	
	
	/***
	 * 查询店铺状态的数据
	 * @param page
	 * @param shop
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Object> getShopListState(PageModel page, Shop shop) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer("select count(p.id) from shop p  WHERE 1=1");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				" select p.id,p.shop_name,p.founder,p.raise_funds,p.self_funds,p.proIncomeProportion,p.real_funds,p.pShopNum,p.pIndustry1,p.pIndustry2,p.founder_email,p.state,"
				+ "p.preheat,p.create_time,(select username from adminuser where id=adminuser_idshop) as adminame , (select username from userbasicsinfo  where id=p.user_id) as userName   from shop p where  1=1 ");
	
		sqlbuffer.append(connectionSql(shop.getCreateTime(),shop.getTeamProfiles()));
		countsql.append(connectionSql(shop.getCreateTime(),shop.getTeamProfiles()));
		sqlbuffer.append(" order by p.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}
	
	public String connectionSql(String beginDate,String endDate){
		String sql = "";
		if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(p.create_time, '%Y-%m-%d')>=DATE_FORMAT('"+ beginDate+ "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(p.create_time, '%Y-%m-%d')<=DATE_FORMAT('" + endDate + "', '%Y-%m-%d') ";
        }
		return sql;
	}
	
	/***
	 * 店铺记录查询
	 * @param page
	 * @param pId
	 * @return
	 */
	public List<Object> getShopRecord(PageModel page, String pId) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer("select count(sr.id) from shop_record sr, userbasicsinfo u where sr.user_id=u.id  ");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				" select sr.id,sr.isSucceed,sr.tenderMoney,sr.tenderTime,u.name from shop_record sr, userbasicsinfo u where sr.user_id=u.id ");
		if (StringUtil.isNotBlank(pId)){
			sqlbuffer.append("and sr.shop_id="+pId);
		}
		sqlbuffer.append(" order by sr.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}

	/***
	 * 审核店铺
	 * @param ids
	 * @param state
	 * @param request
	 * @return
	 */
	public String updateShopState(String ids, Integer examineState,Integer shopState,String approvalExplain,
			HttpServletRequest request) {
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(
				Constant.ADMINLOGIN_SUCCESS);
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
			String uptTime=DateUtils.format("yyyy-MM-dd HH:mm:ss");
			if (delstr.length() > 0) {
				StringBuffer sql = new StringBuffer(
						"update shop_examine  shop SET shop.state=");
				sql.append(examineState);
				if(examineState==2||examineState==3){  //审批
					sql.append(", shop.approval_time='"+uptTime+"'");
					sql.append(", shop.approval_explain='"+approvalExplain+"'");
					sql.append(",shop.adminapproval_id=" + loginuser.getId());
				}else if(examineState==5||examineState==6){   //审核
					sql.append(", shop.examine_time='"+uptTime+"'");
					sql.append(", shop.examine_explain='"+approvalExplain+"'");
					sql.append(",shop.adminexamine_id=" + loginuser.getId());
				}
				sql.append(" WHERE shop.shop_id IN("
						+ delstr.substring(0, delstr.length() - 1) + ")");
				StringBuffer  shopSql=new StringBuffer("update  shop  s  set  s.state=");
				shopSql.append(shopState);
				Shop shop = shopService.queryShopById( delstr.substring(0, delstr.length() - 1));
				if(shopState==2){
					shopSql.append(", s.preheatstar_time='"+uptTime+"'");
					shopSql.append(", s.preheatend_time='"+DateUtil.getSpecifiedDateAfter(uptTime, shop.getPreheat())+"'");
				}else if(shopState==4){
					String shopNumber=getShopNumber(shop.getCity());
					shopSql.append(", s.shopNumber='"+shopNumber+"' ");
					shopSql.append(" , s.start_invest_time='"+uptTime+"'");
					shopSql.append(", s.end_invest_time='"+DateUtil.getSpecifiedDateAfter(uptTime, shop.getFinancing())+"'");
				}
				shopSql.append(" WHERE s.id IN("
						+ delstr.substring(0, delstr.length() - 1) + ")");
				// 批量修改
				dao.executeSql(sql.toString());
				dao.executeSql(shopSql.toString());
			}
		}
		return "2";
	}
	
	
	/***
	 * 根据店铺ID查询实际集资总额
	 * @param shopId
	 * @return
	 */
	public  Double getSumTenderMoney(String shopId){
		if(StringUtil.isNotBlank(shopId)){
			 String sql="select sum(tenderMoney) from shop_record where shop_id=? and isSucceed=1";
			 Object tenderMoney=dao.findObjectBySql(sql, shopId);
			 if(tenderMoney != null){
				 return Double.valueOf(tenderMoney.toString());
			 }else{
				 return 0.00;
			 }
		}else{
			return 0.00;
		}
	}
	
	/***
	 * 根据店铺ID查询实际集资总额
	 * @param shopId
	 * @return
	 */
	public  Double getSumshopRecordMoney(String shopId){
		if(StringUtil.isNotBlank(shopId)){
			 String sql="select sum(tenderMoney) from shop_record where shop_id=? and isSucceed in (0,1)";
			 Object tenderMoney=dao.findObjectBySql(sql, shopId);
			 if(tenderMoney != null){
				 return Double.valueOf(tenderMoney.toString());
			 }else{
				 return 0.00;
			 }
		}else{
			return 0.00;
		}
	}
	
	public Object getRecordByShopRoId(String shopRoId){
		String sql = "select (select sum(tenderMoney) from shop_record where shopro_id=o.id and isSucceed in (0,1)),"
				+ "(select count(1) from shop_record where shopro_id=o.id and isSucceed = 0) from shop_reward_option o where o.id="+shopRoId;
		return dao.findObjectBySql(sql);
	}
	
	/***
	 * 得到店铺编号city+6位时间
	 * @param cityName
	 * @return
	 */
	public String  getShopNumber(String cityName){
		String sql="select short from  city where name like '%"+cityName+"%'";
		Object name=dao.findObjectBySql(sql);
		String shopSql="select  max(right(shopNumber,6)) from shop";
		Object shopNumber=dao.findObjectBySql(shopSql);
		if(shopNumber==null){
			shopNumber=1;
		}else{
			shopNumber=Integer.parseInt(shopNumber.toString())+1;
		}
		if(name==null||name.equals("")){
			name="HC"+DateUtils.format("yyyy")+StringUtil.addZeroForNum(shopNumber.toString(), 6);
		}else{
			name+=DateUtils.format("yyyy")+StringUtil.addZeroForNum(shopNumber.toString(), 6);
		}
		return name.toString();
	}
	/***
	 * 确认是否存在未处理的
	 * @param shopId
	 * @return
	 */
	   public boolean hasWaitforConfirm(Long shopId){
	    	String sql = "select lr.id from shop_record lr where lr.shop_id=? and lr.isSucceed = 0";
	    	List  list=dao.findBySql(sql, shopId);
			return list.size()>0?true:false; 
	    }

}
