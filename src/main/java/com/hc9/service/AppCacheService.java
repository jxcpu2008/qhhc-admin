package com.hc9.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.redis.Arith;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.AppValidator;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.commons.log.LOG;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.entity.Loansign;
import com.hc9.model.PageModel;

/** app缓存管理服务 */
@Service
public class AppCacheService {
	 @Resource
	 private HibernateSupportTemplate commonDao;
	 
	 /** app导航列表缓存更新 */
	 public List<Map<String, String>> updateAppIndexBannerListCache() {
		 LOG.error("开始更新app首页导航列表缓存!");
		 String key = "LIST:HC9:APP:INDEX:BANNER:LIST";
		 List<Map<String, String>> bannerList = new ArrayList<Map<String, String>>();
		 String sql = "select picturename,url,imgurl,type from banner where type=4 order by number asc";
		 List list = commonDao.findBySql(sql);
		 if(list != null && list.size() > 0) {
			 for(int i = 0; i < list.size(); i++) {
				 Object[] obj = (Object[]) list.get(i);
				 Map<String, String> map = new HashMap<String, String>();
				 map.put("pictureName", StatisticsUtil.getStringFromObject(obj[0]));
				 map.put("url", StatisticsUtil.getStringFromObject(obj[1]));
				 map.put("imgurl", StatisticsUtil.getStringFromObject(obj[2]));
				 map.put("type", "" + StatisticsUtil.getIntegerFromObject(obj[3]));
				 bannerList.add(map);
			 }
		 }
		 RedisHelper.set(key, JsonUtil.toJsonStr(bannerList));
		 LOG.error("更新app首页导航列表缓存成功!");
		 return bannerList;
	 }
	 
	 /** app首页列表缓存更新 */
	 public List<Map<String, Object>> updateAppIndexLoanListCache() {
		 LOG.error("开始更新app首页标列表缓存!");
		 String key = "LIST:HC9:APP:INDEX:LOAN:LIST";
		 List<Map<String, Object>> indexLoanList = new ArrayList<Map<String, Object>>();
		 String sql = "select ls.id, ls.name,ls.remonth,ls.issueLoan,ls.rest_money,ls.loanUnit,ls.prio_rate,ls.prio_aword_rate,ls.type,ls.status,ls.activityStatus  "
					+ " from loansign ls where ls.state=2 and loansignType_id!=5 and ls.status >0 and ls.status <9 and (ls.onIndex =1 or ls.recommend =1) "
					+ " ORDER BY ls.rest_money DESC, ls.publish_time DESC,ls.state LIMIT 0,12";
		 List list = commonDao.findBySql(sql);
		 if(list != null && list.size() > 0) {
			 for(int i = 0; i < list.size(); i++) {
				 Object[] loanobj = (Object[]) list.get(i);
				 Map<String, Object> map = new HashMap<String, Object>();
				 map.put("id", StatisticsUtil.getLongFromBigInteger(loanobj[0]));//主键id
				 map.put("name", StatisticsUtil.getStringFromObject(loanobj[1]));//标的名称
				 map.put("remonth", StatisticsUtil.getIntegerFromObject(loanobj[2]));//回购期限
				 
				 Double issueLoan = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)loanobj[3]);//总金额
				 Double restMoney = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)loanobj[4]);//剩余金额
				 Double investProgress = Arith.div(restMoney, issueLoan, 3).
						 multiply(new BigDecimal("100")).doubleValue();
				 String investProgressStr = Arith.div(restMoney, issueLoan, 3).
						multiply(new BigDecimal("100")).toString();
				 String[] arr = investProgressStr.split("/.");
				 Double intInvestProgress = Double.valueOf(arr[0]);
				 if(investProgress > intInvestProgress) {
					investProgress = Arith.sub(100, investProgress);
				 } else {
					 investProgress = Arith.sub(100, intInvestProgress);
				 }
				 map.put("issueLoan", issueLoan);//总金额
				 map.put("restMoney", restMoney);//剩余金额
				 map.put("investProgress", investProgress);//投资进度
				 map.put("loanUnit", StatisticsUtil.getIntegerFromObject(loanobj[5]));//最低投资金额
				 double prioRate = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)loanobj[6]).doubleValue();
				 double prioAwordRate = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)loanobj[7]).doubleValue();
				 double priority =Arith.add(prioRate, prioAwordRate).doubleValue();
				 map.put("priority", priority);//优先年华利率
				 map.put("type", StatisticsUtil.getIntegerFromObject(loanobj[8]));//1-店铺  2-项目 3-天标 4-债权转让
				 /** -1 清盘成功 0未发布 1 进行中 2 融资成功 3-申请审批 4-已审批(待审核)   5-财务审核   6-已放款  7-还款中 8 已完成   9-流标 */
				 map.put("status", StatisticsUtil.getIntegerFromObject(loanobj[9]));//
				 map.put("calculateInterestWay", "放款计息");//
				 map.put("activityStatus", StatisticsUtil.getIntegerFromObject(loanobj[10]));//活动状态 0-不参与  1-参与
				 map.put("activityFlag", "1");//活动是否结束标识:0、不在活动范围内，1、在活动期间
				 indexLoanList.add(map);
			 }
		 }
		 RedisHelper.set(key, JsonUtil.toJsonStr(indexLoanList));
		 LOG.error("更新app首页标列表缓存成功!");
		 return indexLoanList;
	 }
	 
	 /** app我要投资项目列表缓存更新 */
	 public PageModel updateAppInvestLoanListCache() {
		 LOG.error("开始更新app我要投资列表缓存!");
		 PageModel page = new PageModel();
		 page.setNumPerPage(5);
		 String key = "LIST:HC9:APP:INVEST:LOAN:LIST";
		 String sql = "select ls.id, ls.name,ls.remonth,ls.issueLoan,ls.rest_money,ls.loanUnit,ls.prio_rate,ls.prio_aword_rate,ls.type,ls.status,ls.activityStatus "
					+ " from loansign ls where ls.state=2 and loansignType_id!=5 and ls.status>0 and ls.status<9  ORDER BY ls.rest_money DESC,ls.publish_time DESC "
					+ " LIMIT "+(page.getPageNum() - 1)* page.getNumPerPage()+","+page.getNumPerPage();
		 String sqlCount = "select count(ls.id) from loansign ls where ls.state=2 and ls.status>0 and ls.status<9   ";
		 page.setTotalCount(commonDao.queryNumberSql(sqlCount).intValue());
		 List list = commonDao.findBySql(sql);
		 List<Map<String, Object>> investLoanList = new ArrayList<Map<String, Object>>();
		 if(list != null && list.size() > 0) {
			 for(int i = 0; i < list.size(); i++) {
				 Object[] loanobj = (Object[]) list.get(i);
				 Map<String, Object> map = new HashMap<String, Object>();
				 map.put("id", StatisticsUtil.getLongFromBigInteger(loanobj[0]));//主键id
				 map.put("name", StatisticsUtil.getStringFromObject(loanobj[1]));//标的名称
				 map.put("remonth", StatisticsUtil.getIntegerFromObject(loanobj[2]));//回购期限
				 Double issueLoan = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)loanobj[3]);//总金额
				 Double restMoney = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)loanobj[4]);//剩余金额
				 Double investProgress = Arith.div(restMoney, issueLoan, 3).
						 multiply(new BigDecimal("100")).doubleValue();
				 String investProgressStr = Arith.div(restMoney, issueLoan, 3).
						 multiply(new BigDecimal("100")).toString();
				 String[] arr = investProgressStr.split("/.");
				 Double intInvestProgress = Double.valueOf(arr[0]);
				 if(investProgress > intInvestProgress) {
					 investProgress = Arith.sub(100, investProgress);
				 } else {
					 investProgress = Arith.sub(100, intInvestProgress);
				 }
				 map.put("issueLoan", issueLoan);//总金额
				 map.put("restMoney", restMoney);//剩余金额
				 map.put("investProgress", investProgress);//投资进度
				 map.put("loanUnit", StatisticsUtil.getIntegerFromObject(loanobj[5]));//最低投资金额
				 double prioRate = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)loanobj[6]).doubleValue();
				 double prioAwordRate = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)loanobj[7]).doubleValue();
				 double priority = Arith.round(Arith.add(prioRate, prioAwordRate), 1).doubleValue();
				 map.put("priority", priority);//优先年华利率
				 /**1-店铺  2-项目 3-天标 4-债权转让*/
				 map.put("type", StatisticsUtil.getIntegerFromObject(loanobj[8]));
				 /** -1 清盘成功 0未发布 1 进行中 2 融资成功 3-申请审批 4-已审批(待审核)   5-财务审核   6-已放款  7-还款中 8 已完成   9-流标 */
				 map.put("status", StatisticsUtil.getIntegerFromObject(loanobj[9]));
				 map.put("calculateInterestWay", "放款计息");
				 /** 活动状态 0-不参与  1-参与 */
				 map.put("activityStatus", StatisticsUtil.getIntegerFromObject(loanobj[10]));
				 /** 活动是否结束标识:0、不在活动范围内，1、在活动期间 */
				 map.put("activityFlag", "1");
				 investLoanList.add(map);
			 }
		 }
		 page.setList(investLoanList);
		 RedisHelper.set(key, JsonUtil.toJsonStr(page));
		 LOG.error("更新app我要投资列表缓存成功!");
		 return page;
	 }
	 
	 /** app更新标详情页面缓存信息 */
	 public Map<String, Object> updateAppLoanDetailCache(String loansignId) {
		 LOG.error("开始更新app项目详情缓存，标id为：" + loansignId);
		 String key = "LIST:HC9:APP:LOANSIGN:DETAIL:" + loansignId;
		 Map<String, Object> map = new HashMap<String, Object>();
		 String sql = "select * from loansign where id=" + loansignId;
		 Loansign loansign = commonDao.findObjectBySql(sql, Loansign.class);
		 if(loansign != null) {
			 map.put("id", loansign.getId());//主键id
			 map.put("name", loansign.getName());//标的名称
			 map.put("issueLoan", loansign.getIssueLoan());//总金额
			 /** -1 清盘成功 0未发布 1 进行中 2 融资成功 3-申请审批 4-已审批(待审核)   5-财务审核   6-已放款  7-还款中 8 已完成   9-流标 */
			 map.put("status", loansign.getStatus());
			 map.put("remonth", loansign.getRemonth());//回购期限
			 /**1-店铺  2-项目 3-天标 4-债权转让*/
			 map.put("type", loansign.getType());
			 map.put("loanUnit", loansign.getLoanUnit());//最低投资金额
			 Double priority = Arith.add(loansign.getPrioRate(), loansign.getPrioAwordRate()).doubleValue();
			 /** 优先年华利率 */
			 map.put("priority", priority);
			 /** 剩余金额 */
			 map.put("restMoney", loansign.getRestMoney());
			 map.put("calculateInterestWay", "放款计息");
			 Double issueLoan = loansign.getIssueLoan();//总金额
			 Double restMoney = loansign.getRestMoney();//剩余金额
			 Double investProgress = Arith.div(restMoney, issueLoan, 3).
					 multiply(new BigDecimal("100")).doubleValue();
			 String investProgressStr = Arith.div(restMoney, issueLoan, 3).
					 multiply(new BigDecimal("100")).toString();
			 String[] arr = investProgressStr.split("/.");
			 Double intInvestProgress = Double.valueOf(arr[0]);
			 if(investProgress > intInvestProgress) {
				 investProgress = Arith.sub(100, investProgress);
			 } else {
				 investProgress = Arith.sub(100, intInvestProgress);
			 }
			 map.put("investProgress", investProgress);//投资进度
			 /** 活动是否结束标识:0、不在活动范围内，1、在活动期间 */
			 map.put("activityFlag", "1");
			 /** 活动状态 0-不参与  1-参与 */
			 map.put("activityStatus", loansign.getActivityStatus());
			 map.put("prioRestMoney", loansign.getPrioRestMoney());//优先剩余金额
			 map.put("midRestMoney", loansign.getMidRestMoney());//夹层剩余金额
			 map.put("afterRestMoney", loansign.getAfterRestMoney());//劣后剩余金额
			 map.put("publishTime", loansign.getPublishTime());
		 }
		 RedisHelper.set(key, JsonUtil.toJsonStr(map));
		 LOG.error("更新app项目详情缓存成功，标id为：" + loansignId);
		 updateInvestRecordList(loansignId);
		 return map;
	 }
	 
	 /** 更新投资记录列表缓存 */
	 private PageModel updateInvestRecordList(String loanSignId) {
		 LOG.error("开始更新app项目详情投资记录列表缓存，标id为：" + loanSignId);
		 PageModel page = new PageModel();
		 String key = "LIST:HC9:APP:INVEST:RECORD:LIST:" + loanSignId;
		 String sql = "select lr.tenderMoney,lr.tenderTime,lr.subType,lr.webOrApp,u.userName,lr.subType loanType "
				 + " from loanrecord lr,userbasicsinfo u where u.id=lr.userbasicinfo_id and lr.isSucceed =1 and  lr.loanSign_id=?";
		 String sqlCount = "select count(lr.id) from loanrecord lr,userbasicsinfo u where " + 
				 "u.id=lr.userbasicinfo_id and lr.isSucceed =1 and lr.loanSign_id=?";
		 page.setTotalCount(commonDao.queryNumberSql(sqlCount, loanSignId).intValue());
		 sql +=" order by lr.tenderTime desc LIMIT " + (page.getPageNum() - 1)* 10 + "," + page.getNumPerPage();
		 List list = commonDao.findBySql(sql.toString(), loanSignId);
		 List<Map<String, Object>> investLoanList = new ArrayList<Map<String, Object>>();
		 if(list != null && list.size() > 0) {
			 for (int i = 0; i < list.size(); i++) {
				 Map<String, Object> jobj = new HashMap<String, Object>();
				 Object[] obj =  (Object[]) list.get(i);
				 String str=obj[4].toString();
				 if(AppValidator.isNotNull(obj[4])){
					 if(AppValidator.isNotNullAndEmpty(str)){
						 jobj.put("name",str.substring(0,1).concat("***").concat(str.substring(str.length()-1,str.length())));
					 }else{
						 jobj.put("name","");
					 }
				 }else{
					 jobj.put("name","");
				 }
				 jobj.put("loanType",obj[5]);
				 jobj.put("tenderMoney", obj[0]);
				 jobj.put("tenderTime",obj[1]==null?"":obj[1]);
				 investLoanList.add(jobj);
			 }
		 }
		 page.setList(investLoanList);
		 RedisHelper.set(key, JsonUtil.toJsonStr(page));
		 LOG.error("更新app项目详情投资记录列表缓存成功，标id为：" + loanSignId);
		 return page;
	 }
}
