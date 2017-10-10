package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.entity.Bonus;
import com.hc9.dao.entity.Generalize;
import com.hc9.dao.entity.RegBonus;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Usermessage;
import com.hc9.model.PageModel;
/**
 * 活动奖励service
 * @author Administrator
 *
 */
@Service
public class BonusService {
	
    @Resource
    private HibernateSupportTemplate dao;
    /**
     * Save up首笔投资奖励
     * @param bonus
     */
    public void save(Bonus bonus){
    	dao.save(bonus);
    }
    /**
     * UPDATE 首笔投资奖励
     * @param bonus
     */
    public void update(Bonus bonus){
    	dao.update(bonus);
    }
    /**
     * Save注册奖励
     * @param regBonus
     */
    public void saveReg(RegBonus regBonus){
    	dao.save(regBonus);
    }
    /**
     * Update注册奖励
     * @param regBonus
     */
    public void updateReg(RegBonus regBonus){
    	dao.update(regBonus);
    }
    /**
     * 查询注册奖励表，返回记录
     * @param id
     * @return
     */
    public RegBonus queryRegBonus(Long id){
    	String sql="SELECT * from reg_bonus rb WHERE rb.user_id=?";
    	RegBonus bonus=dao.findObjectBySql(sql, RegBonus.class, id);
    	return bonus;
    }
    /**
     * 查找推荐记录
     * @param id
     * @return
     */
    public Generalize queryGeneralize(Long id){
    	String sql="SELECT * from generalize g WHERE g.uid=?";
    	Generalize generalize=dao.findObjectBySql(sql, Generalize.class, id);
    	return generalize;
    }
    
    public boolean checkOnlyOne(Long uid){
    	String sql="SELECT COUNT(b.id) from bonus b WHERE b.user_id=?";
    	Object counts=dao.findObjectBySql(sql, uid);
    	if(Integer.valueOf(counts.toString())>0){
    		return false;
    	}
    	return true;
    }
    
    /***
     * 保存现金
     * @param usermessage
     */
    public void saveUserMesssage(Usermessage usermessage){
    	dao.save(usermessage);
    }
    
	/***
	 * 奖励已发放查询
	 * @param page
	 * @param user
	 * @return
	 */
	public List getbonusAlready(PageModel page,Userbasicsinfo user){
		StringBuffer sqlBuffer=new StringBuffer("select s.id,s.user_id,s.user_name,s.user_phone,s.user_amount,s.user_state,s.user_date,s.user_releaseId,s.tenderMoney,s.tenderTime,s.genuser_id,s.genuser_name,s.genuser_amount,s.genuser_state,s.genuser_date,s.genuser_releaseId from bonus s where 1=1 ");
		if(StringUtil.isNotBlank(user.getAuthIpsTime())&&StringUtil.isNotBlank(user.getCreateTime())){
			sqlBuffer.append( "AND DATE(s.tenderTime)>=DATE('"+user.getAuthIpsTime()+"') AND DATE(s.tenderTime)<=DATE('"+user.getCreateTime()+"') ");
		}
		
		if(user.getIsAuthIps()!=null){
			sqlBuffer.append("  AND s.user_state ="+user.getIsAuthIps());
		}
		
		if(user.getHasIpsAccount()!=null){
			sqlBuffer.append("  AND s.genuser_state ="+user.getHasIpsAccount());
		}
		StringBuffer sqlCount=new StringBuffer("select count(s.id) from bonus s where 1=1 ");
		if(StringUtil.isNotBlank(user.getAuthIpsTime())&&StringUtil.isNotBlank(user.getCreateTime())){
			sqlCount.append( "AND DATE(s.tenderTime)>=DATE('"+user.getAuthIpsTime()+"') AND DATE(s.tenderTime)<=DATE('"+user.getCreateTime()+"') ");
		}
		if(user.getIsAuthIps()!=null){
			sqlCount.append("  AND s.user_state ="+user.getIsAuthIps());
		}
		
		if(user.getHasIpsAccount()!=null){
			sqlCount.append("  AND s.genuser_state ="+user.getHasIpsAccount());
		}		
		return  dao.pageListBySql(page, sqlCount.toString(), sqlBuffer.toString(),null);
	}
	

	/***
	 * 奖励已发放查询导出Excel
	 * @param user
	 * @return
	 */
	public List queryBonusFundAlready(Userbasicsinfo user) {
		List list = new ArrayList();
		StringBuffer sqlBuffer=new StringBuffer("select s.id,s.user_id,s.user_name,s.user_phone,s.user_amount,CASE WHEN s.user_state=0 THEN '待发放' WHEN   s.user_state=1 THEN '已发放' WHEN   s.user_state=-1 THEN '发放失败' WHEN   s.user_state=2 THEN '奖励为0' WHEN   s.user_state=3 THEN '无推荐人' END as  user_state,"
				+ "s.user_date,s.user_releaseId,s.tenderMoney,s.tenderTime,s.genuser_id,s.genuser_name,s.genuser_amount,CASE WHEN s.genuser_state=0 THEN '待发放' WHEN   s.genuser_state=1 THEN '已发放' WHEN   s.genuser_state=-1 THEN '发放失败' WHEN   s.genuser_state=2 THEN '奖励为0' WHEN   s.genuser_state=3 THEN '无推荐人' END, s.genuser_date,s.genuser_releaseId from bonus s where 1=1 ");
		if(StringUtil.isNotBlank(user.getAuthIpsTime())&&StringUtil.isNotBlank(user.getCreateTime())){
			sqlBuffer.append( "AND DATE(s.tenderTime)>=DATE('"+user.getAuthIpsTime()+"') AND DATE(s.tenderTime)<=DATE('"+user.getCreateTime()+"') ");
		}
		if(user.getIsAuthIps()!=null){
			sqlBuffer.append("  AND s.user_state ="+user.getIsAuthIps());
		}
		
		if(user.getHasIpsAccount()!=null){
			sqlBuffer.append("  AND s.genuser_state ="+user.getHasIpsAccount());
		}	
		list = dao.findBySql(sqlBuffer.toString());
		return list;
	}
	
	/***
	 * 查询需要待发放的推荐人与被推荐人奖励
	 * @param user
	 * @param status
	 * @return
	 */
	public List<Bonus> queryBonus(Userbasicsinfo user,Integer status){
		StringBuffer sqlBuffer =new StringBuffer("select * from bonus b where 1=1 ");
		if(status==1){
			sqlBuffer.append(" and b.user_state=0");
		}else{
			sqlBuffer.append(" and b.genuser_state=0 ");
		}
		if(StringUtil.isNotBlank(user.getAuthIpsTime())&&StringUtil.isNotBlank(user.getCreateTime())){
			sqlBuffer.append( "  AND DATE(b.tenderTime)>=DATE(?) AND DATE(b.tenderTime)<=DATE(?) ");
		}
		List<Bonus>  listboBonus=dao.findBySql(sqlBuffer.toString(), Bonus.class, user.getAuthIpsTime(),user.getCreateTime());
		return listboBonus;
	}
	
	/***
	 * 查询注册奖励发放查询
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RegBonus> queryRegBonus(Userbasicsinfo user){
		StringBuffer sqlBuffer=new StringBuffer("From RegBonus r where r.releaseStatus=0 and r.referrer.isLock=0 ");
		if(StringUtil.isNotBlank(user.getAuthIpsTime())&&StringUtil.isNotBlank(user.getCreateTime())){
			sqlBuffer.append( "  AND DATE(r.userbasicsinfo.pIpsAcctDate)>=DATE('"+user.getAuthIpsTime()+"') AND DATE(r.userbasicsinfo.pIpsAcctDate)<=DATE('"+user.getCreateTime()+"') ");
		}
		List<RegBonus> listRegBonus= dao.find(sqlBuffer.toString());
		return listRegBonus;
	}
	
	/***
	 * 注册奖励查询
	 * @param page
	 * @param user
	 * @return
	 */
	public List getRegBonus(PageModel page,Userbasicsinfo user){
		StringBuffer sqlBuffer=new StringBuffer("select r.id ,u.userName,u.name, u.createTime,u.pIpsAcctDate,u.isAuthIps,(select uu.userName from userbasicsinfo uu where r.referrer_id=uu.id),(select c.`name` from userbasicsinfo c where r.referrer_id=c.id), ");
		           sqlBuffer.append("reu.pMerBillNo,uf.phone,r.bouns,r.release_status,r.release_time,(select a.realname from adminuser a where a.id=r.admin_id) as realname from reg_bonus r JOIN userbasicsinfo u  on  u.id=r.user_id JOIN userbasicsinfo reu ON r.referrer_id=reu.id join userrelationinfo uf on uf.id=reu.id and reu.isLock=0 ");
		if(StringUtil.isNotBlank(user.getAuthIpsTime())&&StringUtil.isNotBlank(user.getCreateTime())){
			sqlBuffer.append( "AND DATE(u.pIpsAcctDate)>=DATE('"+user.getAuthIpsTime()+"') AND DATE(u.pIpsAcctDate)<=DATE('"+user.getCreateTime()+"') ");
		}
		
		if(user.getIsAuthIps()!=null){
			sqlBuffer.append("  AND r.release_status ="+user.getIsAuthIps());
		}
		
		StringBuffer sqlCount=new StringBuffer("select count(r.id) from reg_bonus r JOIN userbasicsinfo u on  u.id=r.user_id  JOIN userbasicsinfo reu ON r.referrer_id=reu.id  and reu.isLock=0 ");
		
		if(StringUtil.isNotBlank(user.getAuthIpsTime())&&StringUtil.isNotBlank(user.getCreateTime())){
			sqlCount.append( "AND DATE(u.pIpsAcctDate)>=DATE('"+user.getAuthIpsTime()+"') AND DATE(u.pIpsAcctDate)<=DATE('"+user.getCreateTime()+"') ");
		}
		if(user.getIsAuthIps()!=null){
			sqlCount.append("  AND r.release_status="+user.getIsAuthIps());
		}
		return  dao.pageListBySql(page, sqlCount.toString(), sqlBuffer.toString(),null);
	}
	
	/***
	 * 注册奖励导出excel
	 * @param user
	 * @return
	 */
	public List queryRegBonusAlready(Userbasicsinfo user) {
		List list = new ArrayList();
		StringBuffer sqlBuffer=new StringBuffer("select u.userName,u.name, u.createTime,u.pIpsAcctDate,CASE WHEN u.isAuthIps=1 THEN '宝付已授权' ELSE '宝付未授权' END ,(select uu.userName from userbasicsinfo uu where r.referrer_id=uu.id),(select c.`name` from userbasicsinfo c where r.referrer_id=c.id), ");
        sqlBuffer.append("reu.pMerBillNo,uf.phone,r.bouns,CASE WHEN r.release_status=0 THEN '待发放' WHEN  r.release_status=1 THEN '已发放' WHEN   r.release_status=-1 THEN '发放失败'  END as release_status,r.release_time,(select a.realname from adminuser a where a.id=r.admin_id) as realname from reg_bonus r JOIN userbasicsinfo u  on  u.id=r.user_id JOIN userbasicsinfo reu ON r.referrer_id=reu.id join userrelationinfo uf on uf.id=reu.id and reu.isLock=0 ");
		if(StringUtil.isNotBlank(user.getAuthIpsTime())&&StringUtil.isNotBlank(user.getCreateTime())){
			sqlBuffer.append( "AND DATE(u.pIpsAcctDate)>=DATE('"+user.getAuthIpsTime()+"') AND DATE(u.pIpsAcctDate)<=DATE('"+user.getCreateTime()+"') ");
		}
		
		if(user.getIsAuthIps()!=null){
			sqlBuffer.append("  AND r.release_status ="+user.getIsAuthIps());
		}
		list = dao.findBySql(sqlBuffer.toString());
		return list;
	}
	
	/**
	 * 推荐人数排名
	 * @return
	 */
	public List getRegist() {
		String sql="SELECT u.name,ur.phone,COUNT(r.referrer_id) "
				+ "FROM reg_bonus r "
				+ "JOIN userbasicsinfo u ON r.referrer_id=u.id "
				+ "JOIN userrelationinfo ur ON r.referrer_id=ur.user_id "
				+ "WHERE u.cardStatus=2 AND u.isLock!=1 "
				+ "GROUP BY r.referrer_id "
				+ "ORDER BY COUNT(r.referrer_id) DESC "
				+ "LIMIT 0,10";
		List list = dao.findBySql(sql.toString());
		return list;
	}
	/**
	 * 推荐人的推荐客户累计投资总额排名
	 * @param start
	 * @param end
	 * @return
	 */
	public List getInvest(String start,String end) {
		String sql="SELECT u1.name,ur.phone,SUM(lr.tenderMoney) "
				+ "FROM generalize g JOIN loanrecord lr ON g.uid=lr.userbasicinfo_id JOIN userbasicsinfo u1 ON g.genuid=u1.id JOIN userrelationinfo ur ON u1.id=ur.user_id "
				+ "WHERE DATE(lr.tenderTime)>=DATE(?) AND DATE(lr.tenderTime)<=DATE(?) "
				+ "GROUP BY g.genuid "
				+ "ORDER BY SUM(lr.tenderMoney) "
				+ "DESC LIMIT 0,10";
		List list = dao.findBySql(sql, start,end);
		return list;
	}
	
	/***
	 * 根据Id查询
	 * @param ids
	 * @return
	 */
	public  List<RegBonus> getRegBonus(String ids){
		List<RegBonus> listRegBonus=new ArrayList<RegBonus>();
			if (StringUtil.isNotBlank(ids)) {
				// 根据“，”拆分字符串
				String[] newids = ids.split(",");
				// 确认查询的编号
				String selstr = "";
				for (String idstr : newids) {
					// 将不是空格和非数字的字符拼接
					if (StringUtil.isNotBlank(idstr)
							&& StringUtil.isNumberString(idstr)) {
						selstr += idstr + ",";
					}
				}
				if (selstr.length() > 0) {
					StringBuffer sqlBuffer=new StringBuffer("From RegBonus r where r.releaseStatus=0 and r.id in ("
							+ selstr.substring(0, selstr.length() - 1) +")");
					 listRegBonus= dao.find(sqlBuffer.toString());
				}
		  }
		  return listRegBonus;
	}
	
	/***
	 * 根据用户Id查询
	 * @param id
	 * @return
	 */
    public RegBonus selRegBonus(Long id){
    	String sql="SELECT * from reg_bonus rb WHERE rb.id=?";
    	RegBonus bonus=dao.findObjectBySql(sql, RegBonus.class, id);
    	return bonus;
    }
	
	/***
	 * 获取注册用户的银行卡信息
	 * @param page
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List userbankPage(PageModel page, Long id) {
		RegBonus regBonus=selRegBonus(id);
		List datalist = new ArrayList();
		StringBuffer countsql = new StringBuffer("select count(*) from userbank where user_id="+ regBonus.getUserbasicsinfo().getId());
		StringBuffer sqlbuffer = new StringBuffer("SELECT id,bank_no,user_id,bank_name,pro_value,city_value,bank_address,validate_code,state from userbank where user_id=" + regBonus.getUserbasicsinfo().getId());

		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);

		return datalist;
	}
	
}
