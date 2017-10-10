package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Generalizemoney;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**   
 * Filename:    GeneralizeMoneyServices.java   
 * Company:     前海红筹  
 * @version:    3.0   
 * @since:  JDK 1.7.0_25  
 * Description:  后台会员推广金额服务层

 */
@Service
public class GeneralizeMoneyServices {
    
    /** 注入数据库底层操作层*/
    @Resource
    private HibernateSupport dao;
    
    
    /**
    * <p>Title: queryByUser</p>
    * <p>Description: 根据会员查询推广奖金信息</p>
    * @param page 分页信息
    * @param ids 会员编号
    * @return 返回查询结果 数组
    */
    public List queryByUser(PageModel page,String ids){
        
        StringBuffer sqlbuffer=new StringBuffer("SELECT generalize.id,generalize.adddate,");
        sqlbuffer.append("(SELECT userbasicsinfo.userName FROM userbasicsinfo WHERE userbasicsinfo.id = generalize.uid),");
        sqlbuffer.append(" generalizemoney.umoney,generalizemoney.bonuses FROM generalizemoney");
        sqlbuffer.append(" INNER JOIN userbasicsinfo ON generalizemoney.genuid=userbasicsinfo.id ");
        sqlbuffer.append(" INNER JOIN generalize ON generalize.uid=userbasicsinfo.id  WHERE userbasicsinfo.id="+ids);
    
        StringBuffer sqlcount=new StringBuffer("SELECT COUNT(generalizemoney.id) FROM generalizemoney ");
        sqlcount.append(" INNER JOIN userbasicsinfo ON generalizemoney.genuid=userbasicsinfo.id");
        sqlcount.append(" INNER JOIN generalize ON generalize.uid=userbasicsinfo.id where userbasicsinfo.id="+ids);
        
       return  dao.pageListBySql(page, sqlcount.toString(), sqlbuffer.toString(),null);
    }
    
    /***
     * 查询放款后佣金转账记录
     * @param page
     * @param ids
     * @return
     */
    public List queryByMoney(PageModel page,String ids){
    	StringBuffer sqlBuffer=new StringBuffer("select g.id,(select u.name from userbasicsinfo u where u.id=g.refered_userid)as refered_userid,g.refer_userid,(select u.name from userbasicsinfo u where u.id=g.refer_userid)as referUseridName,g.tender_money,g.tender_time,");
    			sqlBuffer.append("g.bonuses,CASE  WHEN g.release_status=1 THEN '已支付' WHEN g.release_status=-1 THEN '支付失败'   ELSE '待支付' END,g.release_time,g.loanrecord_id,g.order_no,g.trans_order_no,CASE  WHEN g.bonu_type=1 THEN '理财经理自己投的'   ELSE '被推荐人产生的' END,g.paid_bonuses,");
    			sqlBuffer.append( " CASE  WHEN g.pay_state=1 THEN '已支付' WHEN g.pay_state=-1 THEN '支付失败'   ELSE '待支付' END from generalizemoney g ,loanrecord l ,userbasicsinfo u where g.loanrecord_id=l.id and g.refer_userid=u.id    and l.loanSign_id="+ids);
    			
       StringBuffer sqlcount=new StringBuffer("select count(g.id)  from generalizemoney g ,loanrecord l,userbasicsinfo u  where g.loanrecord_id=l.id and g.refer_userid=u.id   and l.loanSign_id="+ids);
    return  dao.pageListBySql(page, sqlcount.toString(), sqlBuffer.toString(),null);
    }
    
	/***
	 * 导出excel
	 * @param loanSignId
	 * @return
	 */
	public List queryByMoneyList(String loanSignId) {
		List list = new ArrayList();
		StringBuffer sqlBuffer=new StringBuffer("select (select u.name from userbasicsinfo u where u.id=g.refered_userid)as refered_userid,g.refer_userid,(select u.name from userbasicsinfo u where u.id=g.refer_userid)as referUseridName,g.tender_money,g.tender_time,");
		sqlBuffer.append("g.bonuses,CASE  WHEN g.release_status=1 THEN '已支付' WHEN g.release_status=-1 THEN '支付失败'   ELSE '待支付' END,g.release_time,s.name,g.order_no,g.trans_order_no,CASE  WHEN g.bonu_type=1 THEN '理财经理自己投的'   ELSE '被推荐人产生的' END,g.paid_bonuses,");
		sqlBuffer.append( " CASE  WHEN g.pay_state=1 THEN '已支付' WHEN g.pay_state=-1 THEN '支付失败'   ELSE '待支付' END from generalizemoney g ,loanrecord l,loansign s ,userbasicsinfo u  where g.loanrecord_id=l.id and l.loanSign_id=s.id and g.refer_userid=u.id   ");
		if(StringUtil.isNotBlank(loanSignId)){
			sqlBuffer.append("  and l.loanSign_id = ").append(loanSignId);
		}
		sqlBuffer.append(" ORDER BY l.loanSign_id asc");
		list = dao.findBySql(sqlBuffer.toString());
		return list;
	}
    
    /***
     * 保存推广资金记录
     * @param generalizemoney
     */
    public  void saveGeneralizeMoney(Generalizemoney generalizemoney){
    	dao.save(generalizemoney);
    }
    
    /***
     * 修改推广资金记录
     * @param generalizemoney
     */
    public void updateGeneralizeMoney(Generalizemoney generalizemoney){
    	dao.update(generalizemoney);
    }
    
    /***
     * 根据Id进行查询Generalizemoney
     * @param id
     * @return
     */
	public Generalizemoney getGeneralizemoneyById(String id) {
		try {
			return dao.get(Generalizemoney.class, Long.valueOf(id));
		} catch (DataAccessException e) {
			return null;
		}
	}
	
	/***
	 * 根据转账订单号查询Generalizemoney
	 * @param transOrderNo
	 * @return
	 */
	public  Generalizemoney  getGeneralizemoneyByTransOrderNo(String transOrderNo){
		String sql="select * from  generalizemoney where trans_order_no=?";
		Generalizemoney generalizemoney=dao.findObjectBySql(sql,Generalizemoney.class,transOrderNo);
		return generalizemoney;
	}
	
	/***
	 * 根据购买订单号查询Generalizemoney
	 * @param orderNo
	 * @return
	 */
	public Generalizemoney getGeneralizemoneyByOrderNo(String orderNo){
		String sql="select * from  generalizemoney where order_no=?";
		Generalizemoney generalizemoney=dao.findObjectBySql(sql,Generalizemoney.class,orderNo);
		return generalizemoney;
	}
	
	/***
	 * 根据loanrecordId修改推广资金记录
	 * @param loanrecordId
	 * @param state
	 */
	public  void  updateGeneralizemoney(String loanrecordId,Integer state){
		String sql="select * from generalizemoney where loanrecord_id=?";
		List<Generalizemoney> generalizemoneyList=dao.findBySql(sql, Generalizemoney.class, loanrecordId);
		if(generalizemoneyList!=null){
			for (int i = 0; i < generalizemoneyList.size(); i++) {
					Generalizemoney generalizemoney=generalizemoneyList.get(i);
					generalizemoney.setReleaseStatus(state);
					generalizemoney.setPayState(state);
					dao.update(generalizemoney);
			}
		}
	}
	
	/***
	 * 根据loanId查询所有的佣金记录信息
	 * @param loanId
	 * @return
	 */
	public  List<Generalizemoney> getGeneralizemoneyList(String loanId ){
		String sql="select * from generalizemoney g ,loanrecord l,userbasicsinfo u where g.loanrecord_id=l.id and g.refer_userid=u.id and l.loanSign_id=? and g.pay_state in (0,-1)  ";
		List<Generalizemoney> generalizemoneyList=dao.findBySql(sql, Generalizemoney.class, loanId);
		return generalizemoneyList;
	}
	
	 /***
		 * 保存Generalizemoney
		 * @param loanrecord
		 * @param userId
		 * @param bonuses
		 */
		public void saveGeneralizemoney(Loanrecord loanrecord,Double costratioMoney,Long userId,Integer state){
			Double bonuses=0.00;
			 if(loanrecord.getLoansign().getType()==2){ //项目
	    		//推荐金额项目=年利率/12*loansign.remonth(月份)*购买金额
		    	Double	 business=Arith.mul(Arith.div(costratioMoney, 12), loanrecord.getLoansign().getRemonth());
		    	bonuses=Arith.round(Arith.mul(business, loanrecord.getTenderMoney()),2);
		    }else if(loanrecord.getLoansign().getType()==3){ //天标
		    	//推荐金额天标=年利率/360*loansign.remonth(天数)*购买金额
		    	Double	 business=Arith.mul(Arith.div(costratioMoney, 360), loanrecord.getLoansign().getRemonth());
		    	bonuses=Arith.round(Arith.mul(business, loanrecord.getTenderMoney()),2);
		    }
		   	 Generalizemoney generalizemoney=new Generalizemoney();
			 generalizemoney.setBonuses(bonuses);
			 generalizemoney.setReferedUserid(loanrecord.getUserbasicsinfo().getId());
			 if(userId!=null){
				 generalizemoney.setReferUserid(userId);
			 }else{
				 generalizemoney.setReferUserid(loanrecord.getUserbasicsinfo().getId());
			 }
			 generalizemoney.setTenderMoney(loanrecord.getTenderMoney());
			 generalizemoney.setTenderTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
			 generalizemoney.setLoanrecordId(loanrecord.getId());
			 generalizemoney.setOrderNo(loanrecord.getOrder_id());
			 generalizemoney.setReleaseStatus(Constant.STATUES_ZERO);
			 generalizemoney.setBonuType(state);
			 generalizemoney.setPayState(Constant.STATUES_ZERO);
			 dao.save(generalizemoney);
		}
}
