package com.hc9.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.log.LOG;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.ChannelSpread;
import com.hc9.dao.entity.ChannelSpreadDetail;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.ChannelInfo;

/**
 * 渠道推广：
 * <ul><li>ChannelSpread,</li><li>ChannelSpreadDetail</li></ul>
 * 两个表的相关数据操作
 */
@Service
public class ChannelSpreadDao {
	@Resource
	private HibernateSupport dao;
	
	/**保存ChannelSpreadDetail*/
	public void saveChannelSpreadDetail(ChannelSpreadDetail entity){
		dao.save(entity);
	}
	/**保存ChannelSpread*/
	public void saveChannelSpread(ChannelSpread entity){
		dao.save(entity);
	}
	
	/**
	 * 通过渠道id反查渠道名称
	 * @param spreadId
	 * @return
	 */
	public String getChannelName(String spreadId){
		String sql="SELECT name FROM channelspread WHERE spreadId=?";
		List results=dao.findBySql(sql, spreadId);
		if (results.size()>0) {
			return (String) results.get(0);
		}else {
			return null;
		}
	}
	/**
	 * 通过spreadId查寻投资记录
	 * @param spreadId
	 * @param endTime 起始时间
	 * @param startTime 结束时间
	 * @return
	 */
	public List<Object[]> getLoanrecordsBySpreadId(String spreadId, String startTime, String endTime) {
		String sql="SELECT loanRecordId,cookieValue FROM `channelspreaddetail` WHERE spreadId=? AND loanRecordId is NOT NULL";
		if(startTime!=null && !"".equals(startTime)){
			sql+=" AND DATE(createTime)>=DATE(?) ";
		}
		if(startTime!=null && !"".equals(endTime)){
			sql+=" AND DATE(createTime)<=DATE(?)  ";
		}
		List results=dao.findBySql(sql, spreadId,startTime,endTime);
		if (results.size()>0) {
			return results;
		}else {
			return null;
		}
	}
	public Object[] getUserInfoByRecordId(Object objs) {
		String sql="SELECT lr.tenderTime,lr.tenderMoney FROM loanrecord lr ,loansign l  WHERE lr.userbasicinfo_id=u.id AND lr.userbasicinfo_id=u2.id AND lr.id=?";
		List results=dao.findBySql(sql, objs);
		if (results.size()>0) {
			return (Object[]) results.get(0);
		}else {
			return null;
		}
	}

	public Object[] getLoanInfoByRecordId(Object objs) {
		long id=Long.parseLong(objs.toString()); 
		String sql="SELECT lr.tenderTime,lr.tenderMoney,lr.loanSign_id,lr.order_id,l.`name`,l.loanUnit FROM loanrecord lr ,loansign l  WHERE lr.loanSign_id=l.id AND  lr.id=?";
		List<Object[]> results=dao.findBySql(sql, objs);
		if (results.size()>0) {
			return (Object[]) results.get(0);
		}else {
			return null;
		}
	}

	
	/**
	 * add by xuyh 
	 * at 2015/08/31
	 * 验证渠道 是否存在
	 * @param spreadId
	 * @param password
	 * @return
	 */
	public String findChannelUserByLoginName(String loginName,String password){
		String sql="select spreadId from channeluser where userName=? and password=?";
		if(StringUtil.isNotBlank(loginName)&&StringUtil.isNotBlank(password)){
			List results=dao.findBySql(sql,loginName,password);
			if(results!=null&&!results.isEmpty()){
				return results.get(0).toString();
			}
			return "";
		}
		return "";
	}
	
	
	/**
	 * 查询 搜集注册,投资 等 渠道需要的用户信息 
	 * add by xuyh 
	 * at 2015/08/31
	 */
	public List<ChannelInfo> getChannelInfoList(String spreadId,String beginTime,String endTime,Long minPage,Long maxPage){
		String sql="select t2.createTime,case when t2.name='您好，您还没填写真实姓名' or t2.name is null or t2.name='' then '未实名认证' else t2.name end name,"
				+ " case when t2.phone is not null then t2.phone else '' end phone,case when t2.isAuthIps=1 then '是' else '否' end isAuthIps from"
				+ " (select spreadId,regUserName from channelspreaddetail where regStatus=1";
		String pageSql="";
		if(StringUtil.isNotBlank(spreadId)){
			sql+=" and spreadId="+"'"+spreadId+"'";
		}
		sql+=" ) t1,(select u1.id,u1.createTime,u1.userName,u1.name,u2.phone,u1.isAuthIps "
				+" from userbasicsinfo u1 inner join userrelationinfo u2 on u1.id=u2.user_id";
		if(StringUtil.isNotBlank(beginTime)){
			sql+=" where date_format(createTime,'%Y%m%d')>="+"'"+beginTime+"'";
		}
		if(StringUtil.isNotBlank(endTime)){
			sql+=" and date_format(createTime,'%Y%m%d')<="+"'"+endTime+"'";
		}
		sql+=" ) t2 where t1.regUserName=t2.userName order by t2.createTime"; 
		
		if(minPage>=0){
			sql+=" ) t limit "+minPage;
		}
		if(maxPage>0){
			sql+=","+maxPage;
		}
		pageSql="select t.* from ("+sql;
		List results=dao.findBySql(pageSql, null);
		
		if(results!=null&&!results.isEmpty()){
			return (List<ChannelInfo>)results;
		}
		return null;
	}
	
	/**
	 * 查询 搜集注册,投资 等 渠道需要的用户信息 
	 * add by xuyh 
	 * at 2015/08/31
	 */
	public Long getChannelInfoCount(String spreadId,String beginTime,String endTime){
		String sql="select t2.createTime,case when t2.name='您好，您还没填写真实姓名' or t2.name is null or t2.name='' then '未实名认证' else t2.name end name,"
				+ " case when t2.phone is not null then t2.phone else '' end phone,case when t2.isAuthIps=1 then '是' else '否' end isAuthIps from"
				+ " (select spreadId,regUserName from channelspreaddetail where regStatus=1";
		String sqlCount="";
		if(StringUtil.isNotBlank(spreadId)){
			sql+=" and spreadId="+"'"+spreadId+"'";
		}
		sql+=" ) t1,(select u1.id,u1.createTime,u1.userName,u1.name,u2.phone,u1.isAuthIps "
				+" from userbasicsinfo u1 inner join userrelationinfo u2 on u1.id=u2.user_id";
		if(StringUtil.isNotBlank(beginTime)){
			sql+=" where date_format(createTime,'%Y%m%d')>="+"'"+beginTime+"'";
		}
		if(StringUtil.isNotBlank(endTime)){
			sql+=" and date_format(createTime,'%Y%m%d')<="+"'"+endTime+"'";
		}
		sql+=" ) t2 where t1.regUserName=t2.userName "; 
		
		sqlCount="select count(*) from ("+sql+") c";
		List results=dao.findBySql(sqlCount, null);
		if(results!=null&&!results.isEmpty()){
			return Long.parseLong(results.get(0).toString());
		}
		return (long) 0;
	}
	
	/**
	 * 返回标的类型：3月一下0.5%....
	 * @param lid
	 * @return
	 */
	public String getLoanRemonthType(String lid){
		String sql="SELECT type,remonth FROM loansign WHERE id=?";
		Object[] result=(Object[]) dao.findObjectBySql(sql, lid);
		String str="";
		if(null==result){
			return str;
		}
		int type=(int) result[0];
		int month=(int) result[1];
		
		if(type==2){//2-项目 
			if(month<=3){
				str= "1";
			}
			if(month>3 && month<=6){
				str= "2";
			}
			if(month>6 && month<=12){
				str= "3";
			}
			if(month>12){
				str= "4";
			}
		}else{//3-天标
			str= "1";
		}
		return str;
	}
	
	public List<Object[]> getYrtRegData(String spreadId, String startTime,
			String endTime) {
		String sql="SELECT regUserName,regStatus,cookieValue FROM `channelspreaddetail` WHERE spreadId=? AND regUserName IS NOT NULL ";
		if(startTime!=null && !"".equals(startTime)){
			sql+=" AND DATE(createTime)>=DATE(?) ";
		}
		if(startTime!=null && !"".equals(endTime)){
			sql+=" AND DATE(createTime)<=DATE(?)  ";
		}
		List results=dao.findBySql(sql, spreadId,startTime,endTime);
		if (results.size()>0) {
			return results;
		}else {
			return null;
		}
	}
	public List<Object[]> getUserInfoByUserName(Object objs) throws Exception {
		String sql="SELECT u.createTime,r.emailisPass,r.phonepass,u.isAuthIps FROM userbasicsinfo u JOIN userrelationinfo r ON u.id=r.id  WHERE u.userName=?";
		List<Object[]> info=dao.findBySql(sql, objs);
		return info;
	}
	/**
	 * 用户首投金额  ，11-24补充标的期限
	 * @param object
	 * @return
	 */
	public Object[] getUserFirstInvestByUserName(Object object) throws Exception {
		LOG.info("----->"+object);
		String sql="select id from userbasicsinfo where userName=? LIMIT 1";
		Object oUid=dao.findObjectBySql(sql, object.toString());
		sql="SELECT lr.tenderMoney,l.remonth FROM loanrecord lr JOIN loansign l ON lr.loanSign_id=l.id WHERE lr.userbasicinfo_id=? ORDER BY lr.tenderTime LIMIT 1 ";
		List<Object[]> objs=dao.findBySql(sql, oUid) ;
		if(null!=objs && objs.size()>0){
			return objs.get(0);
		}
		return null;
	}

}
