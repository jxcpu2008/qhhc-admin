package com.hc9.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.Arith;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
import com.hc9.model.StatisticsInfo;
import com.hc9.model.UserInfo;

/** 投资统计服务相关 */
@Service
public class InvestStatisticsService {
	@Resource
	private HibernateSupport dao;
	
	/** 根据开始时间和结束时间查询注册人数信息 */
	public Double queryTotalInvestMoney(String beginTime, String endTime) {
		/** 注册人数 */
		String sql = "select sum(tendermoney) from loanrecord where isSucceed=1 and "
				+ " DATE_FORMAT(tenderTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(tenderTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') ";
		return StatisticsUtil.querySumNum(sql, dao);
	}
	
	/**总投资金额*/
	public Double queryTotalInvestMoneyCount() {
		/** 总投资金额 */
		String sql = "select sum(tendermoney) from loanrecord where isSucceed=1 ";
		return StatisticsUtil.querySumNum(sql, dao);
	}
	
	/**总投资人数*/
	public Long queryTotalInvestCount() {
		/** 总投资人数 */
		String sql = "select count(id) from loanrecord where isSucceed=1 ";
		return StatisticsUtil.queryCountNum(sql, dao);
	}
	
	/** 根据日期获取当日投资额柱状图数据  */
	public List<StatisticsInfo> queryInvestBarGraphListByDate(String dateTime) {
		List<StatisticsInfo> resultList = new ArrayList<StatisticsInfo>();
		//时间轴的时间字段分别为：0-6点，6-12点，12-18点，18-24点
		Double zeroSixNum = queryTotalInvestMoney(dateTime + " 00:00:00",  dateTime + " 05:59:59");
		Double sixTwelveNum = queryTotalInvestMoney(dateTime + " 06:00:00",  dateTime + " 11:59:59");
		Double twelveEighteenNum = queryTotalInvestMoney(dateTime + " 12:00:00",  dateTime + " 17:59:59");
		Double eighteenTwentyFourNum = queryTotalInvestMoney(dateTime + " 18:00:00",  dateTime + " 23:59:59");
		StatisticsInfo zeroSixVo = new StatisticsInfo();
		StatisticsInfo sixTwelveVo = new StatisticsInfo();
		StatisticsInfo twelveEighteenVo = new StatisticsInfo();
		StatisticsInfo eighteenTwentyFourVo = new StatisticsInfo();
		zeroSixVo.setLableName("0-6点");
		zeroSixVo.setInvestMoney(zeroSixNum);
				
		sixTwelveVo.setLableName("6-12点");
		sixTwelveVo.setInvestMoney(sixTwelveNum);
				
		twelveEighteenVo.setLableName("12-18点");
		twelveEighteenVo.setInvestMoney(twelveEighteenNum);
				
		eighteenTwentyFourVo.setLableName("18-24点");
		eighteenTwentyFourVo.setInvestMoney(eighteenTwentyFourNum);
				
		resultList.add(zeroSixVo);
		resultList.add(sixTwelveVo);
		resultList.add(twelveEighteenVo);
		resultList.add(eighteenTwentyFourVo);
		return resultList;
	}
	
	/** 查询本周注册人数柱状图数据 */
	public List<StatisticsInfo> queryBarGraphListByWeek(String dateTime) {
		List<StatisticsInfo> barGraphList = StatisticsUtil.weekDayOfDatetime(dateTime);
		for(StatisticsInfo vo : barGraphList) {
		     String date = vo.getBeginDate();
		     Double investMoney = queryTotalInvestMoney(date + " 00:00:00",  date + " 23:59:59");
		     vo.setInvestMoney(investMoney);
		}
		return barGraphList;
	}
	
	/** 按星期显示每月的注册人数统计信息 */
	public List<StatisticsInfo> queryBarGraphListByMonth(String currentDate) {
		List<StatisticsInfo> statisList = StatisticsUtil.getWeekInfoListInMonth(currentDate);
		for(StatisticsInfo vo : statisList) {
			Double investMoney = queryTotalInvestMoney(vo.getBeginDate() + " 00:00:00",  vo.getEndDate() + " 23:59:59");
			vo.setInvestMoney(investMoney);
		}
		return statisList;
	}
	
	/** 认购业绩来源占比饼图 */
	public List<StatisticsInfo> queryInvestAreaList(String beginTime, String endTime) {
		List<StatisticsInfo> resultList = new ArrayList<StatisticsInfo>();
		/** 员工认购金额 */
		String sql = "select sum(l.tenderMoney) from userbasicsinfo u, loanrecord l "
				+ "where u.id=l.userbasicinfo_id and u.user_type=2 and l.isSucceed=1 and "
				+ " DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') ";
		Double employeeInvestMoney = StatisticsUtil.querySumNum(sql, dao);
		
		/** 员工推荐认购金额 */
		sql = "select sum(l.tenderMoney) from userbasicsinfo u, loanrecord l "
				+ "where u.id=l.userbasicinfo_id and u.user_type=1 and l.isSucceed=1 and u.id in "
				+ "(select g.uid from generalize g,userbasicsinfo a where a.user_type=2 and g.genuid=a.id) "
				+ " and DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') ";
		Double employeeGenInvestMoney = StatisticsUtil.querySumNum(sql, dao);
		
		/** 游客认购 */
		sql = "select sum(l.tenderMoney) from userbasicsinfo u, loanrecord l "
				+ "where u.id=l.userbasicinfo_id and u.user_type=1 and l.isSucceed=1 and l.userbasicinfo_id not in "
				+ "(select g.uid from generalize g,userbasicsinfo a where g.genuid=a.id) "
				+ " and DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') ";
		Double visitorInvestMoney = StatisticsUtil.querySumNum(sql, dao);
		
		/** 游客推荐 */
		sql = "select sum(l.tenderMoney) from userbasicsinfo u, loanrecord l "
				+ "where u.id=l.userbasicinfo_id and u.user_type=1 and l.isSucceed=1 and u.id in "
				 + "(select g.uid from generalize g,userbasicsinfo a where a.user_type=1 and g.genuid=a.id) "
				+ " and DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') ";
		Double visitorGenInvestMoney = StatisticsUtil.querySumNum(sql, dao);
		
		StatisticsInfo employee = new StatisticsInfo();
		employee.setLableName("员工认购");
		employee.setInvestMoney(employeeInvestMoney);
		
		StatisticsInfo employeeGen = new StatisticsInfo();
		employeeGen.setLableName("员工推荐");
		employeeGen.setInvestMoney(employeeGenInvestMoney);
		
		StatisticsInfo visitor = new StatisticsInfo();
		visitor.setLableName("游客认购");
		visitor.setInvestMoney(visitorInvestMoney);
		
		StatisticsInfo visitorGen = new StatisticsInfo();
		visitorGen.setLableName("游客推荐");
		visitorGen.setInvestMoney(visitorGenInvestMoney);
		resultList.add(employee);
		resultList.add(employeeGen);
		resultList.add(visitor);
		resultList.add(visitorGen);
		
		Double totalInvestMoney = Arith.add(employeeInvestMoney , employeeGenInvestMoney);
		totalInvestMoney = Arith.add(totalInvestMoney , visitorInvestMoney);
		totalInvestMoney = Arith.add(totalInvestMoney , visitorGenInvestMoney);
		computeRegisterPercent(resultList, totalInvestMoney);
		return resultList;
	}
	
	/** 认购期限占比饼图 */
	@SuppressWarnings("rawtypes")
	public List<StatisticsInfo> queryInvestPeriodAreaList(String beginTime, String endTime) {
		List<StatisticsInfo> resultList = new ArrayList<StatisticsInfo>();
		String sql = "select sum(lr.tenderMoney),ls.remonth from loansign ls,loanrecord lr "
					+ "where ls.id=lr.loanSign_id and lr.isSucceed=1 and ls.loanPeriods is not null and "
					+ " DATE_FORMAT(lr.tenderTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
					+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(lr.tenderTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
					+ endTime + "', '%Y-%m-%d %H:%i:%s') "
					+ " group by ls.remonth order by ls.remonth asc";
		List list = dao.findBySql(sql);
		if(list != null && list.size() > 0) {
			for(Object obj :list) {
				Object[] arr = (Object[])obj;
				Double investMoney = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)arr[0]);
				Integer remonth = StatisticsUtil.getIntegerFromObject(arr[1]);
				StatisticsInfo vo = new StatisticsInfo();
				vo.setInvestMoney(investMoney);
				vo.setLableName(remonth + "个月");
				resultList.add(vo);
			}
		} else {
			StatisticsInfo oneVo = new StatisticsInfo();
			oneVo.setInvestMoney(0.0);
			oneVo.setLableName("1个月");
			StatisticsInfo threeVo = new StatisticsInfo();
			threeVo.setInvestMoney(0.0);
			threeVo.setLableName("3个月");
			StatisticsInfo sixVo = new StatisticsInfo();
			sixVo.setInvestMoney(0.0);
			sixVo.setLableName("6个月");
			StatisticsInfo nineVo = new StatisticsInfo();
			nineVo.setInvestMoney(0.0);
			nineVo.setLableName("9个月");
			StatisticsInfo twelveVo = new StatisticsInfo();
			twelveVo.setInvestMoney(0.0);
			twelveVo.setLableName("12个月");
			
			resultList.add(oneVo);
			resultList.add(threeVo);
			resultList.add(sixVo);
			resultList.add(nineVo);
			resultList.add(twelveVo);
		}
		
		Double totalInvestMoney = 0.0;
		for(StatisticsInfo vo : resultList) {
			totalInvestMoney = Arith. add(totalInvestMoney , vo.getInvestMoney());
		}
		computeRegisterPercent(resultList, totalInvestMoney);
		return resultList;
	}
	
	/** 认购类型占比饼图 */
	@SuppressWarnings("rawtypes")
	public List<StatisticsInfo> queryInvestTypeAreaList(String beginTime, String endTime) {
		List<StatisticsInfo> investTypeAreaList = new ArrayList<StatisticsInfo>();
		String sql = "select sum(tenderMoney),subType from loanrecord " 
				+ " where isSucceed=1 and DATE_FORMAT(tenderTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(tenderTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') "	
				+ " group by subType";
		List list = dao.findBySql(sql);
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				BigDecimal investMoney = (BigDecimal)arr[0];
				Integer loanType = (Integer)arr[1];
				map.put(loanType, investMoney.doubleValue());
			}
		}
		
		/** 投资类型 1-优先，2-夹层，3-劣后 4-vip众筹，5-股东众筹，*/
		for(int i = 1; i <= 5; i++) {
			String xLableName = "";
			if(i == 1) {
				xLableName = "优先";
			}
			if(i == 2) {
				xLableName = "夹层";
			}
			if(i == 3) {
				xLableName = "劣后";
			}
			if(i == 4) {
				xLableName = "vip众筹";
			}
			if(i == 5) {
				xLableName = "股东众筹";
			}
			Double investMoney = 0.0;
			if(map.containsKey(i)) {
				investMoney = map.get(i);
			}
			StatisticsInfo vo = new StatisticsInfo();
			vo.setLableName(xLableName);
			vo.setInvestMoney(investMoney);
			investTypeAreaList.add(vo);
		}
		Double totalInvestMoney = 0.0;
		for(StatisticsInfo vo : investTypeAreaList) {
			totalInvestMoney = Arith. add(totalInvestMoney , vo.getInvestMoney());
		}
		computeRegisterPercent(investTypeAreaList, totalInvestMoney);
		return investTypeAreaList;
	}
	
	/** 认购业绩明细分页列表 */
	@SuppressWarnings("rawtypes")
	public List<UserInfo> queryInvestUserList(String beginTime, String endTime, PageModel page, String downLoadFlag) {
		List<UserInfo> resultList = new ArrayList<UserInfo>();
		
		String selectSql = "select u.userName,u.name,IFNULL(ls.name,'') as loanSignName,ls.`status`,l.tenderMoney,l.tenderTime,l.subType,u.loginTime,u.id,IFNULL(l.id,-1),u.user_type  ";
		String fromSql = " from userbasicsinfo u,loanrecord l,loansign ls "
				+ " where u.id=l.userbasicinfo_id and l.isSucceed=1 and l.loanSign_id=ls.id "
				+ " and DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') " ;
				
		String orderBy = " order by l.tenderTime desc";
		String querySql = selectSql + fromSql + orderBy;
		List list = new ArrayList();
		if("1".equals(downLoadFlag)) {
			list = dao.findBySql(querySql);
		} else {
			String countSql = "select count(u.id) " + fromSql;
			list = dao.pageListBySql(page, countSql, querySql, null);
		}
		List<Long> userIdList = new ArrayList<Long>();
		List<Long> loanIdList = new ArrayList<Long>();
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				UserInfo vo = new UserInfo();
				vo.setUserName(StatisticsUtil.getStringFromObject(arr[0]));
				vo.setName(StatisticsUtil.getStringFromObject(arr[1]));
				vo.setLoanSignName(StatisticsUtil.getStringFromObject(arr[2]));
				vo.setLoanSignStatus(StatisticsUtil.getIntegerFromObject(arr[3]));
				vo.setInvestMoney(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)arr[4]));
				vo.setInvestTime(StatisticsUtil.getStringFromObject(arr[5]));
				vo.setLoanType(StatisticsUtil.getIntegerFromObject(arr[6]));
				vo.setLastLoginTime(StatisticsUtil.getStringFromObject(arr[7]));
				vo.setUserId(StatisticsUtil.getLongFromBigInteger(arr[8]));
				vo.setLoanId(StatisticsUtil.getLongFromBigInteger(arr[9]));
				vo.setCommissionMoney(0.0);
				vo.setUserType(StatisticsUtil.getIntegerFromObject(arr[10]));
				
				userIdList.add(StatisticsUtil.getLongFromBigInteger(arr[8]));
				loanIdList.add(StatisticsUtil.getLongFromBigInteger(arr[9]));
				resultList.add(vo);
			}
		}
		
		if(userIdList != null && userIdList.size() > 0) {
			String ids = "";
			for(int i = 0; i < userIdList.size(); i++) {
				ids += userIdList.get(i);
				if(i != (userIdList.size() - 1)) {
					ids += ",";
				}
			}
			
			/** 获取推荐人相关名称和所在部门 */
			String sql = "select distinct u.id,g.uid,u.name,u.department,u.user_type " 
					   + " from userbasicsinfo u, generalize g "
					   + " where u.id=g.genuid and g.uid in(" + ids + ")";
			List genList = dao.findBySql(sql);
			if(genList != null && genList.size() > 0) {
				for(Object obj : genList) {
					Object[] arr = (Object[])obj;
					Long uid = StatisticsUtil.getLongFromBigInteger(arr[1]);//用户id
					String generalizerName = StatisticsUtil.getStringFromObject(arr[2]);
					Integer generalizerDepartment = StatisticsUtil.getIntegerFromObject(arr[3]);
					Integer generalizeUserType=StatisticsUtil.getIntegerFromObject(arr[4]);
					for(UserInfo vo : resultList) {
						if(uid.longValue() == vo.getUserId().longValue()) {
							vo.setGeneralizerName(generalizerName);
							vo.setGeneralizerDepartment(generalizerDepartment);
							vo.setGeneralizeUserType(generalizeUserType);
						}
					}
				}
			}
			
			/** 处理用户登录时间 */
			sql = "select max(id),logintime,user_id from userloginlog where user_id in(" + ids + ") group by user_id";
			List loginList = dao.findBySql(sql);
			if(loginList != null && loginList.size() > 0) {
				for(Object obj : loginList) {
					Object[] arr = (Object[])obj;
					String lastLoginTime = StatisticsUtil.getStringFromObject(arr[1]);
					Long uid = StatisticsUtil.getLongFromBigInteger(arr[2]);
					for(UserInfo vo : resultList) {
						if(uid.longValue() == vo.getUserId().longValue()) {
							vo.setLastLoginTime(lastLoginTime);
						}
					}
				}
			}
			
			/** 处理佣金信息 */
			ids = "";
			for(int i = 0; i < loanIdList.size(); i++) {
				ids += loanIdList.get(i);
				if(i != (loanIdList.size() - 1)) {
					ids += ",";
				}
			}
			sql = "select sum(g.bonuses),g.loanrecord_id from generalizemoney g where g.loanrecord_id in(" + ids + ") group by loanrecord_id";
			List bonusesList = dao.findBySql(sql);
			if(bonusesList != null && bonusesList.size() > 0) {
				for(Object obj : bonusesList) {
					Object[] arr = (Object[])obj;
					Double commissionMoney = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)arr[0]);
					Long loanId = StatisticsUtil.getLongFromBigInteger(arr[1]);
					for(UserInfo vo : resultList) {
						if(loanId.longValue() == vo.getLoanId().longValue()) {
							vo.setCommissionMoney(commissionMoney);
						}
					}
				}
			}
		}
		return resultList;
	}
	
	/** 投资柱状图列表数据 */
	public Map<String, Object> queryInvestBarGrapListAndTime(String type, Date queryDate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<StatisticsInfo> investBarGrapList = new ArrayList<StatisticsInfo>();//投资金额柱状图
		String beginTime = "";
		String endTime = "";
		String currentTime = DateFormatUtil.dateToString(queryDate, "yyyy-MM-dd HH:mm:ss");
		String currentDate = currentTime.substring(0, 10);
		if("1".equals(type)) {//1、今日注册量；
			beginTime = currentDate + " 00:00:00";
			endTime = currentDate + " 23:59:59";
			investBarGrapList = queryInvestBarGraphListByDate(currentDate);
		} else if("2".equals(type)) {//2、昨日投资额；
			Date yesterday = DateFormatUtil.increaseDay(queryDate, -1);
			String yesterdayTime = DateFormatUtil.dateToString(yesterday, "yyyy-MM-dd HH:mm:ss");
			String yesterdayDate = yesterdayTime.substring(0, 10);
			beginTime = yesterdayDate + " 00:00:00";
			endTime = yesterdayDate + " 23:59:59";
			investBarGrapList = queryInvestBarGraphListByDate(yesterdayDate);
		} else if("3".equals(type)) {//3、一周投资额量，默认本周
			beginTime = DateFormatUtil.getMondayOfWeekByDate(currentDate) + " 00:00:00";;
			endTime = DateFormatUtil.getSundayOfWeekByDate(currentDate) + " 23:59:59";
			investBarGrapList = queryBarGraphListByWeek(currentDate);
		} else {//4、默认本月投资总额
			beginTime = DateFormatUtil.getFirstDayOfMonthByDate(currentDate) + " 00:00:00";
			endTime = DateFormatUtil.getLastDayOfMonthByDate(currentDate) + " 23:59:59";
			investBarGrapList = queryBarGraphListByMonth(currentDate);
		}
		resultMap.put("investBarGrapList", investBarGrapList);//投资柱状图列表数据
		resultMap.put("beginTime", beginTime);
		resultMap.put("endTime", endTime);
		return resultMap;
	}
	
	/** 组装统计信息 */
	public String compositeStaticsMsg(Map<String, Object> resultMap) {
		String varDate = "今日";
		String type = (String)resultMap.get("type");
		if("3".equals(type)) {
			varDate = "一周";
		} else if("4".equals(type)) {
			varDate = "一月";
		}
		/** 投资金额柱状图相关统计信息 */
		String oneLine = generateOneLineStr(varDate, resultMap);
		/** 认购来源饼图统计信息 */
		String twoLine = generateTwoLineStr(varDate, resultMap);
		/** 认购期限比例 */
		String threeLine = generateThreeLineStr(varDate, resultMap);
		/** 认购类型饼图相关 */
		String fourLine = generateFourLineStr(varDate, resultMap);
		String staticsMsg = oneLine + twoLine + threeLine + fourLine;
		return staticsMsg;
	}
	
	/** 生成第一条记录 */
	@SuppressWarnings("unchecked")
	private String generateOneLineStr(String varDate, Map<String, Object> resultMap) {
		/** 总注册人数 */
		Double totalInvestMoney = (Double)resultMap.get("totalInvestMoney");
		/** 投资柱状图列表数据 */
		List<StatisticsInfo> investBarGrapList = (List<StatisticsInfo>)resultMap.get("investBarGrapList");
		String oneLine = "1、" + varDate + "投资统计：总投资金额为" + totalInvestMoney + " 元，";
		for(int i = 0; i < investBarGrapList.size(); i++) {
			StatisticsInfo vo = investBarGrapList.get(i);
			if(i < (investBarGrapList.size() - 1)) {
				oneLine += vo.getLableName() + "为" + vo.getInvestMoney() + "元，";
			} else {
				oneLine += vo.getLableName() + "为" + vo.getInvestMoney() + "元;<br/>";
			}
		}
		return oneLine;
	}
	
	/** 生成第二条记录 */
	@SuppressWarnings("unchecked")
	private String generateTwoLineStr(String varDate, Map<String, Object> resultMap) {
		/** 用户类型比例饼图 */
		List<StatisticsInfo> investSourceAreaList = (List<StatisticsInfo>)resultMap.get("investSourceAreaList");
		String twoLine = "2、" + varDate + "认购业绩来源占比：";
		return generateDetailInfoStr(twoLine, investSourceAreaList);
	}
	
	/** 生成第三条记录 */
	@SuppressWarnings("unchecked")
	private String generateThreeLineStr(String varDate, Map<String, Object> resultMap) {
		/** 推广注册会员饼图 */
		List<StatisticsInfo> investPeriodAreaList = (List<StatisticsInfo>)resultMap.get("investPeriodAreaList");
		String threeLine = "3、" + varDate + "认购期间占比：";
		return generateDetailInfoStr(threeLine, investPeriodAreaList);
	}
	
	/** 生成第四条记录 */
	@SuppressWarnings("unchecked")
	private String generateFourLineStr(String varDate, Map<String, Object> resultMap) {
		/** 注册用户购买信息 */
		List<StatisticsInfo> investTypeAreaList = (List<StatisticsInfo>)resultMap.get("investTypeAreaList");
		String fourLine = "4、" + varDate + "认购类型占比：";
		return generateDetailInfoStr(fourLine, investTypeAreaList);
	}
	
	/** 生成百分比相关信息 */
	private String generateDetailInfoStr(String line, List<StatisticsInfo> list) {
		for(int i = 0; i < list.size(); i++) {
			StatisticsInfo vo = list.get(i);
			if(i < (list.size() - 1)) {
				line += vo.getLableName() + "为" + vo.getPercentRate() + "%，";
			} else {
				line += vo.getLableName() + "为" + vo.getPercentRate() + "%;<br/>";
			}
		}
		return line;
	}
	
	/** 处理投资统计报表的相关下载数据 */
	public List<Map<String, String>> handleInvestUserList(List<UserInfo> investUserList) {
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		if(investUserList != null && investUserList.size() > 0) {
			for(UserInfo vo : investUserList) {
				Map<String, String> userMap = new HashMap<String, String>();
				userMap.put("登录账号", vo.getUserName());
				userMap.put("姓名", vo.getName());
				Integer userType = vo.getUserType();
				String userTypeName = "暂无";		
				if(userType != null) {
					if(userType == 1) {//1普通用户 2员工 3特别企业
						userTypeName = "普通用户";
					} else if(userType == 2) {
						userTypeName = "员工";
					} else if(userType == 3) {
						userTypeName = "特别企业";
					}else if(userType==4){
						userTypeName = "居间人";
					}else if(userType==6){
						userTypeName = "理财师";
					}
				}
				
				userMap.put("用户类型", userTypeName);
				userMap.put("项目名称", vo.getLoanSignName());
				/** 认购产品状态:-1 清盘成功 0未发布 1 进行中 2 融资成功 3-申请审批 4-已审批(待审核)   5-财务审核   6-已放款  7-还款中 8 已完成   9-流标 */
				int i = vo.getLoanSignStatus();
				String loanSignStatusStr = "暂无";
				if(i == -1) {
					loanSignStatusStr = "清盘成功";
				} else if(i == 0) {
					loanSignStatusStr = "未发布 ";
				} else if(i == 1) {
					loanSignStatusStr = "进行中";
				} else if(i == 2) {
					loanSignStatusStr = "融资成功";
				} else if(i == 3) {
					loanSignStatusStr = "申请审批";
				} else if(i == 4) {
					loanSignStatusStr = "已审批(待审核)";
				} else if(i == 5) {
					loanSignStatusStr = "财务审核";
				} else if(i == 6) {
					loanSignStatusStr = "已放款";
				} else if(i == 7) {
					loanSignStatusStr = "还款中";
				} else if(i == 8) {
					loanSignStatusStr = "已完成";
				} else if(i == 9) {
					loanSignStatusStr = "流标";
				}
				userMap.put("项目状态", loanSignStatusStr);
				userMap.put("认购金额", "" + vo.getInvestMoney());
				userMap.put("认购时间", vo.getInvestTime());
				int loanType = vo.getLoanType();//投资类型： 1 优先 2 夹层 3劣后
				String loanTypeStr = "";
				if(loanType == 1) {//1-优先，2-夹层，3-劣后 4-vip众筹，5-股东众筹，
					loanTypeStr = "优先";
				} else if(loanType == 2) {
					loanTypeStr = "夹层";
				} else if(loanType == 3) {
					loanTypeStr = "劣后";
				} else if(loanType == 4) {
					loanTypeStr = "vip众筹";
				} else if(loanType == 5) {
					loanTypeStr = "股东众筹";
				}
				userMap.put("投资类型", loanTypeStr);
				String generalizerName = vo.getGeneralizerName();
				if(StringUtil.isBlank(generalizerName)) {
					generalizerName = "无";
				}
				userMap.put("推荐人", generalizerName);
				userMap.put("推荐人部门", StatisticsUtil.queryDepartmentNameById(vo.getGeneralizerDepartment()));
				Integer generalizeUserType = vo.getGeneralizeUserType();
				String generalizeUserTypeName = "暂无";		
				if(generalizeUserType != null) {
					if(generalizeUserType == 1) {//1普通用户 2员工 3特别企业
						generalizeUserTypeName = "普通用户";
					} else if(generalizeUserType == 2) {
						generalizeUserTypeName = "员工";
					} else if(generalizeUserType == 3) {
						generalizeUserTypeName = "特别企业";
					}else if(generalizeUserType==4){
						generalizeUserTypeName = "居间人";
					}else if(generalizeUserType==6){
						generalizeUserTypeName = "理财师";
					}
				}
				
				userMap.put("推荐人用户类型", generalizeUserTypeName);
				userMap.put("此单佣金", "" + vo.getCommissionMoney());
				userMap.put("上次登陆时间", vo.getLastLoginTime());
				content.add(userMap);
			}
		}
		return content;
	}
	
	/** 处理注册量百分比 */
	public void computeRegisterPercent(List<StatisticsInfo> registerList, Double totalInvestMoney) {
		for(StatisticsInfo vo : registerList) {
			Double investMoney = vo.getInvestMoney();
			if(totalInvestMoney > 0) {
				double rate = Arith. div(investMoney * 100, totalInvestMoney, 2);
				vo.setPercentRate(rate);
			}
		}
	}
}