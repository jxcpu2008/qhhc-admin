package com.hc9.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.ChannelInvNum;
import com.hc9.model.ChannelRegNum;
import com.hc9.model.PageModel;
import com.hc9.model.RegUserInvInfo;
import com.hc9.model.StatisticsInfo;
import com.hc9.model.TerminalInvNum;
import com.hc9.model.TerminalRegNum;
import com.hc9.model.UserInfo;

/** 统计服务相关 */
@Service
@SuppressWarnings("rawtypes")
public class RegisterStatisticsService {
	@Resource
	private HibernateSupport dao;
	
	/**
	 * 获取各端口注册人数图标数据
	 */
	public List<TerminalRegNum> getTerminalRegNumChartData(String regTerminal, String beginTime, String endTime) {
		List<TerminalRegNum> records = new ArrayList<TerminalRegNum>();
		List<Object> params = new ArrayList<Object>();
		
		String selectSql = " select count(userbasicsinfo.id) num, userbasicsinfo.registerSource from userbasicsinfo ";
		
		String whereSql = " where userbasicsinfo.registerSource is not null "
				+ " and userbasicsinfo.registerSource != 0 ";
		
		String groupSql = " group by userbasicsinfo.registerSource ";
		
		StringBuffer condition = new StringBuffer();
		// 注册端口
		if (regTerminal != null && regTerminal.length() > 0 && !regTerminal.equals("all")) {
			condition.append(" and userbasicsinfo.registerSource = ? ");
			params.add(Integer.parseInt(regTerminal));
		}
		
		// 注册开始时间
		if (beginTime != null && beginTime.length() > 0) {
			condition.append(" and date(str_to_date(userbasicsinfo.createTime, '%Y-%m-%d')) >= ? ");
			params.add(beginTime);
		}
		
		// 注册结束时间
		if (endTime != null && endTime.length() > 0) {
			condition.append(" and date(str_to_date(userbasicsinfo.createTime, '%Y-%m-%d')) <= ? ");
			params.add(endTime);
		}
		
		String conditionSql = condition.toString();
		String querySql = selectSql + whereSql + groupSql;
		if (conditionSql.length() > 0) {
			querySql = selectSql + whereSql + conditionSql + groupSql;
		}
		
		List list = dao.findBySql(querySql, params.toArray());
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] arr = (Object[]) obj;
				
				TerminalRegNum vo = new TerminalRegNum();
				vo.setTerminalRegNum(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setRegisterSource(StatisticsUtil.getIntegerFromObject(arr[1]));
				
				records.add(vo);
			}
		} else {
			TerminalRegNum vo = new TerminalRegNum();
			vo.setRegisterSource(Integer.parseInt(regTerminal));
			vo.setTerminalRegNum(0L);
			records.add(vo);
		}
		
		return records;
	}

	/**
	 * 获取各渠道注册人数图标数据
	 * @return
	 */
	public List<ChannelRegNum> getChannelRegNumChartData(String regChannel, String regChannelName, String beginTime, String endTime) {
		List<ChannelRegNum> records = new ArrayList<ChannelRegNum>();
		List<Object> params = new ArrayList<Object>();
		
		String selectSql = " select count(userbasicsinfo.id) num, channelspread.name from channelspreaddetail "
				+ " left join userbasicsinfo on userbasicsinfo.userName = channelspreaddetail.regUserName "
				+ " left join channelspread on channelspread.spreadId = channelspreaddetail.spreadId ";
		
		String whereSql = " where channelspreaddetail.regStatus = 1 ";
		String groupSql = " group by channelspreaddetail.spreadId ";
		
		StringBuffer condition = new StringBuffer();
		if (regChannel != null && regChannel.length() > 0 && !regChannel.equals("all")) {
			condition.append(" and channelspreaddetail.spreadId = ? ");
			params.add(regChannel);
		}
		
		// 注册开始时间
		if (beginTime != null && beginTime.length() > 0) {
			condition.append(" and date(str_to_date(userbasicsinfo.createTime, '%Y-%m-%d')) >= ? ");
			params.add(beginTime);
		}
		
		// 注册结束时间
		if (endTime != null && endTime.length() > 0) {
			condition.append(" and date(str_to_date(userbasicsinfo.createTime, '%Y-%m-%d')) <= ? ");
			params.add(endTime);
		}
		
		String conditionSql = condition.toString();
		String querySql = selectSql + whereSql + groupSql;
		if (conditionSql.length() > 0) {
			querySql = selectSql + whereSql + conditionSql + groupSql;
		}
		
		List list = dao.findBySql(querySql, params.toArray());
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] arr = (Object[]) obj;
				
				ChannelRegNum vo = new ChannelRegNum();
				vo.setChannelRegNum(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setChannelName(StatisticsUtil.getStringFromObject(arr[1]));
				
				records.add(vo);
			}
		} else {
			ChannelRegNum vo = new ChannelRegNum();
			vo.setChannelName(regChannelName);
			vo.setChannelRegNum(0L);
			records.add(vo);
		}
		
		return records;
	}
	
	public List<TerminalInvNum> getTerminalInvNumChartData(String regTerminal, String beginTime, String endTime) {
		List<TerminalInvNum> records = new ArrayList<TerminalInvNum>();
		List<Object> params = new ArrayList<Object>();
		
		String selectSql = " select count(distinct userbasicinfo_id) num, sum(tenderMoney) money, loanrecord.webOrApp from loanrecord "
				+ " left join userbasicsinfo on userbasicsinfo.id = loanrecord.userbasicinfo_id ";
		
		String whereSql = " where loanrecord.isSucceed = 1 and loanrecord.webOrApp != 5 ";
		
		String groupSql = " group by loanrecord.webOrApp ";
		
		StringBuffer condition = new StringBuffer();
		// 注册端口
		if (regTerminal != null && regTerminal.length() > 0 && !regTerminal.equals("all")) {
			condition.append(" and loanrecord.webOrApp = ? ");
			params.add(Integer.parseInt(regTerminal));
		}
		
		// 注册开始时间
		if (beginTime != null && beginTime.length() > 0) {
			condition.append(" and date(str_to_date(userbasicsinfo.createTime, '%Y-%m-%d')) >= ? ");
			params.add(beginTime);
		}
		
		// 注册结束时间
		if (endTime != null && endTime.length() > 0) {
			condition.append(" and date(str_to_date(userbasicsinfo.createTime, '%Y-%m-%d')) <= ? ");
			params.add(endTime);
		}
		
		String conditionSql = condition.toString();
		String querySql = selectSql + whereSql + groupSql;
		if (conditionSql.length() > 0) {
			querySql = selectSql + whereSql + conditionSql + groupSql;
		}
		
		List list = dao.findBySql(querySql, params.toArray());
		
		double totalInvestNum = 0.0000;
		
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				
				Object[] arr = (Object[]) obj;
				
				Double money = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal) arr[1]);
				
				TerminalInvNum vo = new TerminalInvNum();
				vo.setTerminalInvNum(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setInvestMoney(money);
				totalInvestNum += money;
				vo.setRegisterSource(StatisticsUtil.getIntegerFromObject(arr[2]));
				
				records.add(vo);
			}
			
			computeTerminalInvestPercent(records, totalInvestNum);
		} else {
			TerminalInvNum vo = new TerminalInvNum();
			vo.setTerminalInvNum(0L);
			vo.setInvestMoney(0.0);
			vo.setPercentage(0.0);
			vo.setRegisterSource(Integer.parseInt(regTerminal));
			records.add(vo);
		}
		
		return records;
	}
	
	public List<ChannelInvNum> getChannelInvNumChartData(String regChannel, String regChannelName, String beginTime, String endTime) {
		List<ChannelInvNum> records = new ArrayList<ChannelInvNum>();
		List<Object> params = new ArrayList<Object>();
		
		String selectSql = " select count(loanrecord.userbasicinfo_id) num, sum(loanrecord.tenderMoney) money, channelspread.name from loanrecord "
				+ " left join userbasicsinfo on userbasicsinfo.id = loanrecord.userbasicinfo_id "
				+ " left join channelspreaddetail on channelspreaddetail.regUserName = userbasicsinfo.userName "
				+ " left join channelspread on channelspread.spreadId = channelspreaddetail.spreadId ";
		
		String whereSql = " where channelspreaddetail.regStatus = 1 ";
		String groupSql = " group by channelspreaddetail.spreadId ";
		
		StringBuffer condition = new StringBuffer();
		if (regChannel != null && regChannel.length() > 0 && !regChannel.equals("all")) {
			condition.append(" and channelspreaddetail.spreadId = ? ");
			params.add(regChannel);
		}
		
		// 注册开始时间
		if (beginTime != null && beginTime.length() > 0) {
			condition.append(" and date(str_to_date(userbasicsinfo.createTime, '%Y-%m-%d')) >= ? ");
			params.add(beginTime);
		}
		
		// 注册结束时间
		if (endTime != null && endTime.length() > 0) {
			condition.append(" and date(str_to_date(userbasicsinfo.createTime, '%Y-%m-%d')) <= ? ");
			params.add(endTime);
		}
		
		String conditionSql = condition.toString();
		String querySql = selectSql + whereSql + groupSql;
		if (conditionSql.length() > 0) {
			querySql = selectSql + whereSql + conditionSql + groupSql;
		}
		
		List list = dao.findBySql(querySql, params.toArray());
		
		double totalInvestNum = 0.0000;
		
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				
				Object[] arr = (Object[]) obj;
				
				Double money = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal) arr[1]);
				
				ChannelInvNum vo = new ChannelInvNum();
				vo.setChannelInvNum(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setInvestMoney(money);
				totalInvestNum += money;
				vo.setChannelName(StatisticsUtil.getStringFromObject(arr[2]));
				
				records.add(vo);
			}
			
			computeChannelInvestPercent(records, totalInvestNum);
		} else {
			ChannelInvNum vo = new ChannelInvNum();
			vo.setChannelInvNum(0L);
			vo.setChannelName(regChannelName);
			vo.setInvestMoney(0.0);
			vo.setPercentage(0.0);
			records.add(vo);
		}
		
		return records;
	}
	
	/** 根据开始时间和结束时间查询注册人数信息 */
	public Long queryTotalRegisterNum(String beginTime, String endTime) {
		/** 注册人数 */
		return queryRegisterNum(beginTime, endTime);
	}
	
	/**总注册人数*/
	public Long queryTotalRegisterCount(){
		/** 注册人数 */
		return queryRegisterCount();
	}
	
	
	/** 查询总注册人数信息 */
	public Long queryRegisterCount() {
		/** 注册人数 */
		String sql = "select count(*) from userbasicsinfo";
		return StatisticsUtil.queryCountNum(sql, dao);		
	}
	
	/** 获取某一天的注册人数柱状数据 */
	public List<StatisticsInfo> queryRegisterBarGraphListByDate(String dateTime) {
		List<StatisticsInfo> barGraphList = new ArrayList<StatisticsInfo>();
		//时间轴的时间字段分别为：0-6点，6-12点，12-18点，18-24点
		Long zeroSixNum = queryRegisterNum(dateTime + " 00:00:00",  dateTime + " 05:59:59");
		Long sixTwelveNum = queryRegisterNum(dateTime + " 06:00:00",  dateTime + " 11:59:59");
		Long twelveEighteenNum = queryRegisterNum(dateTime + " 12:00:00",  dateTime + " 17:59:59");
		Long eighteenTwentyFourNum = queryRegisterNum(dateTime + " 18:00:00",  dateTime + " 23:59:59");
		StatisticsInfo zeroSixVo = new StatisticsInfo();
		StatisticsInfo sixTwelveVo = new StatisticsInfo();
		StatisticsInfo twelveEighteenVo = new StatisticsInfo();
		StatisticsInfo eighteenTwentyFourVo = new StatisticsInfo();
		zeroSixVo.setLableName("0-6点");
		zeroSixVo.setRegisterNum(zeroSixNum);
		
		sixTwelveVo.setLableName("6-12点");
		sixTwelveVo.setRegisterNum(sixTwelveNum);
		
		twelveEighteenVo.setLableName("12-18点");
		twelveEighteenVo.setRegisterNum(twelveEighteenNum);
		
		eighteenTwentyFourVo.setLableName("18-24点");
		eighteenTwentyFourVo.setRegisterNum(eighteenTwentyFourNum);
		
		barGraphList.add(zeroSixVo);
		barGraphList.add(sixTwelveVo);
		barGraphList.add(twelveEighteenVo);
		barGraphList.add(eighteenTwentyFourVo);
		return barGraphList;
	}
	
	/** 查询本周注册人数柱状图数据 */
	public List<StatisticsInfo> queryRegisterBarGraphListByWeek(String dateTime) {
		List<StatisticsInfo> barGraphList = StatisticsUtil.weekDayOfDatetime(dateTime);
		for(StatisticsInfo vo : barGraphList) {   
		     String date = vo.getBeginDate();
		     Long countNum = queryRegisterNum(date + " 00:00:00",  date + " 23:59:59");
		     vo.setRegisterNum(countNum);
		}   
		return barGraphList;
	}
	
	/** 按星期显示每月的注册人数统计信息 */
	public List<StatisticsInfo> queryRegisterBarGraphListByMonth(String currentDate) {
		List<StatisticsInfo> statisList = StatisticsUtil.getWeekInfoListInMonth(currentDate);
		for(StatisticsInfo vo : statisList) {
			Long countNum = queryRegisterNum(vo.getBeginDate() + " 00:00:00",  vo.getEndDate() + " 23:59:59");
			vo.setRegisterNum(countNum);
		}
		return statisList;
	}
	
	/** 根据开始时间和结束时间查询注册人数信息 */
	public Long queryRegisterNum(String beginTime, String endTime) {
		/** 注册人数 */
		String sql = "select count(*) from userbasicsinfo where "
				+ " DATE_FORMAT(createTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(createTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') ";
		return StatisticsUtil.queryCountNum(sql, dao);		
	}
	
	/** 获取某一天的认购比柱状数据 */
	public List<StatisticsInfo> queryRegisterBuyBarGraphListByDate(String dateTime, List<StatisticsInfo> registerBarGraphList) {
		List<StatisticsInfo> barGraphList = new ArrayList<StatisticsInfo>();
		//时间轴的时间字段分别为：0-6点，6-12点，12-18点，18-24点
		/** 注册人数 */
		Map<String, Long> registerNumMap =  StatisticsUtil.queryRegisterMap(registerBarGraphList);
		Long zeroSixRegisterNum = registerNumMap.get("0-6点");
		Long sixTwelveRegisterNum = registerNumMap.get("6-12点");
		Long twelveEighteenRegisterNum = registerNumMap.get("12-18点");
		Long eighteenTwentyFourRegisterNum = registerNumMap.get("18-24点");
		
		/** 购买人数 */
		Long zeroSixBuyNum = queryRegisterBuyNum(dateTime + " 00:00:00",  dateTime + " 05:59:59");
		Long sixTwelveBuyNum = queryRegisterBuyNum(dateTime + " 06:00:00",  dateTime + " 11:59:59");
		Long twelveEighteenBuyNum = queryRegisterBuyNum(dateTime + " 12:00:00",  dateTime + " 17:59:59");
		Long eighteenTwentyFourBuyNum = queryRegisterBuyNum(dateTime + " 18:00:00",  dateTime + " 23:59:59");
		StatisticsInfo zeroSixVo = new StatisticsInfo();
		StatisticsInfo sixTwelveVo = new StatisticsInfo();
		StatisticsInfo twelveEighteenVo = new StatisticsInfo();
		StatisticsInfo eighteenTwentyFourVo = new StatisticsInfo();
		zeroSixVo.setLableName("0-6点");
		zeroSixVo.setRegisterNum(zeroSixRegisterNum);
		zeroSixVo.setRegisterBuyNum(zeroSixBuyNum);
		
		sixTwelveVo.setLableName("6-12点");
		sixTwelveVo.setRegisterBuyNum(sixTwelveBuyNum);
		sixTwelveVo.setRegisterNum(sixTwelveRegisterNum);
		
		twelveEighteenVo.setLableName("12-18点");
		twelveEighteenVo.setRegisterNum(twelveEighteenRegisterNum);
		twelveEighteenVo.setRegisterBuyNum(twelveEighteenBuyNum);
		
		eighteenTwentyFourVo.setLableName("18-24点");
		eighteenTwentyFourVo.setRegisterNum(eighteenTwentyFourRegisterNum);
		eighteenTwentyFourVo.setRegisterBuyNum(eighteenTwentyFourBuyNum);
		
		barGraphList.add(zeroSixVo);
		barGraphList.add(sixTwelveVo);
		barGraphList.add(twelveEighteenVo);
		barGraphList.add(eighteenTwentyFourVo);
		return barGraphList;
	}
	
	/** 查询本周注册人数柱状图数据 */
	public List<StatisticsInfo> queryRegisterBuyBarGraphListByWeek(String dateTime, List<StatisticsInfo> registerBarGraphList) {
		List<StatisticsInfo> barGraphList = StatisticsUtil.weekDayOfDatetime(dateTime);
		Map<String, Long> registerNumMap =  StatisticsUtil.queryRegisterMap(registerBarGraphList);
		for(StatisticsInfo vo : barGraphList) {   
		     String date = vo.getBeginDate();
		     /** 注册人数 */
		     Long registerNum = registerNumMap.get(vo.getLableName());
		     /** 认购人数 */
		     Long registerBuyNum = queryRegisterBuyNum(date + " 00:00:00",  date + " 23:59:59");
		     vo.setRegisterNum(registerNum);
		     vo.setRegisterBuyNum(registerBuyNum);
		}   
		return barGraphList;
	}
	
	/** 按星期显示每月的注册人数统计信息 */
	public List<StatisticsInfo> queryRegisterBuyBarGraphListByMonth(String currentDate, List<StatisticsInfo> registerBarGraphList) {
		List<StatisticsInfo> statisList = StatisticsUtil.getWeekInfoListInMonth(currentDate);
		Map<String, Long> registerNumMap =  StatisticsUtil.queryRegisterMap(registerBarGraphList);
		for(StatisticsInfo vo : statisList) {
			/** 注册人数 */
			Long registerNum = registerNumMap.get(vo.getLableName());
			
			/** 认购人数 */
			Long registerBuyNum = queryRegisterBuyNum(vo.getBeginDate() + " 00:00:00",  vo.getEndDate() + " 23:59:59");
			vo.setRegisterNum(registerNum);
			vo.setRegisterBuyNum(registerBuyNum);
		}
		return statisList;
	}
	
	/** 根据开始时间和结束时间查询购买人数信息 */
	public Long queryRegisterBuyNum(String beginTime, String endTime) {
		/** 购买人数 */
		String sql = "select count(distinct u.id) from userbasicsinfo u, loanrecord l "
				+ "where u.id=l.userbasicinfo_id and l.isSucceed=1 and "
				+ " DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(l.tenderTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') " 
				+ " and DATE_FORMAT(u.createTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(u.createTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') ";
		return StatisticsUtil.queryCountNum(sql, dao);
	}
	
	/** 根据开始时间和结束时间查询注册量 */
	public List<UserInfo> queryRegisterStatisticsByPage(String beginTime, String endTime, PageModel page, String downLoadFlag) {
		List<UserInfo> resultList = new ArrayList<UserInfo>();
		
		String select = "select u.userName,u.name,u.user_type,u.hasIpsAccount,u.loginTime,u.id,u.createTime ";
		String fromSql = "from userbasicsinfo u "
				+ "where DATE_FORMAT(u.createTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('" + beginTime + "', '%Y-%m-%d %H:%i:%s') " 
				+ "and DATE_FORMAT(u.createTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('" + endTime + "', '%Y-%m-%d %H:%i:%s')";
		String orderBy = " order by u.createTime desc";
		
		String querySql = select + fromSql + orderBy;
		
		
		List list = new ArrayList();
		if("1".equals(downLoadFlag)) {
			list = dao.findBySql(querySql);
		} else {
			String countSql = "select count(u.id) " + fromSql;
			list = dao.pageListBySql(page, countSql, querySql, null);
		}
				
		List<Long> userIdList = new ArrayList<Long>();
		Map<Long, UserInfo> map = new HashMap<Long, UserInfo>();
		if(list != null && list.size() > 0) {
			for(Object obj : list) {
				Object[] arr = (Object[])obj;
				UserInfo vo = new UserInfo();
				vo.setUserName(StatisticsUtil.getStringFromObject(arr[0]));
				vo.setName(StatisticsUtil.getStringFromObject(arr[1]));
				vo.setUserType((Integer)arr[2]);
				vo.setCashBalance(0.0);
				vo.setIpsAccountStatus(StatisticsUtil.getIntegerFromObject(arr[3]));
				vo.setLastLoginTime(StatisticsUtil.getStringFromObject(arr[4]));
				vo.setCreateTime(StatisticsUtil.getStringFromObject(arr[6]));
				
				userIdList.add(StatisticsUtil.getLongFromBigInteger(arr[5]));
				map.put(StatisticsUtil.getLongFromBigInteger(arr[5]), vo);
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
			
			/** 处理资金可用余额 */
			handleCashBalance(ids, map);
			
			/** 获取推荐人相关名称和所在部门 */
			handleGeneralizerNameAndDept(ids, map);
			
			/** 处理用户登录时间 */
			handleUserLastLoginTime(ids, map);
		}
		
		for(Map.Entry<Long, UserInfo> entry : map.entrySet()) {
			UserInfo vo = entry.getValue();
			resultList.add(vo);
	    }
		return resultList;
	}
	
	public List<RegUserInvInfo> queryRegUserInvInfosByPage(Map<String, String> queryParam, PageModel page, boolean download) {
		List<RegUserInvInfo> resultList = new ArrayList<RegUserInvInfo>();
		List<Object> params = new ArrayList<Object>();
		
		String selectSql = " select userbasicsinfo.userName, "
				+ " userbasicsinfo.name, "
				+" userbasicsinfo.isAuthIps, "
				+ " userrelationinfo.phone, "
				+ " userbasicsinfo.createTime, "
				+ " userbasicsinfo.registerSource, "
				+ " channelspread.name as registerChannel, "
				+ " loansign.name as loanSignName, "
				+ " loanrecord.tenderMoney, "
				+ " loanrecord.tenderTime, "
				+ " generalize.genuid, "
				+ " otherUserTable.name as inviteName ";
		String fromSql = " from userbasicsinfo "
				+ " left outer join userrelationinfo on userrelationinfo.user_id = userbasicsinfo.id "
				+ " left outer join channelspreaddetail on channelspreaddetail.regUserName = userbasicsinfo.userName "
				+ " left outer join channelspread on channelspread.spreadId = channelspreaddetail.spreadId "
				+ " left outer join loanrecord on loanrecord.userbasicinfo_id = userbasicsinfo.id "
				+ " left outer join loansign on loansign.id = loanrecord.loanSign_id "
				+ " left outer join generalize on generalize.uid = userbasicsinfo.id "
				+ " left outer join userbasicsinfo otherUserTable on otherUserTable.id = generalize.genuid ";
//		String whereSql = " where (generalize.state = 1 or generalize.state = 2) ";
		String whereSql = " where 1 = 1 ";
		
		StringBuffer condition = new StringBuffer();
		String beginTime = queryParam.get("regStartTime");
		String endTime = queryParam.get("regEndTime");
		String userName = queryParam.get("userName");
		String phoneNum = queryParam.get("phoneNum");
		String regChannel = queryParam.get("regChannel");
		String regTerminal = queryParam.get("regTerminal");
		String investStartTime = queryParam.get("investStartTime");
		String investEndTime = queryParam.get("investEndTime");
		// 注册开始时间
		if (beginTime != null && beginTime.length() > 0) {
			condition.append(" and date(str_to_date(userbasicsinfo.createTime, '%Y-%m-%d')) >= ? ");
			params.add(beginTime);
		}
		
		// 注册结束时间
		if (endTime != null && endTime.length() > 0) {
			condition.append(" and date(str_to_date(userbasicsinfo.createTime, '%Y-%m-%d')) <= ? ");
			params.add(endTime);
		}
		
		// 用户名称
		if (userName != null && userName.length() > 0) {
			condition.append(" and userbasicsinfo.userName like ? ");
			params.add("%" + userName + "%");
		}
		
		// 手机号
		if (phoneNum != null && phoneNum.length() > 0) {
			condition.append(" and userrelationinfo.phone like ? ");
			params.add("%" + phoneNum + "%");
		}
		
		// 注册渠道
		if (regChannel != null && regChannel.length() > 0 && !regChannel.equals("all")) {
			condition.append(" and channelspreaddetail.spreadId = ? ");
			params.add(regChannel);
		}
		
		// 注册端口
		if (regTerminal != null && regTerminal.length() > 0 && !regTerminal.equals("all")) {
			condition.append(" and userbasicsinfo.registerSource = ? ");
			params.add(regTerminal);
		}
		
		// 认购开始时间
		if (investStartTime != null && investStartTime.length() > 0) {
			condition.append(" and date(str_to_date(loanrecord.tenderTime, '%Y-%m-%d')) >= ? ");
			params.add(investStartTime);
		}
		
		// 认购结束时间
		if (investEndTime != null && investEndTime.length() > 0) {
			condition.append(" and date(str_to_date(loanrecord.tenderTime, '%Y-%m-%d')) <= ? ");
			params.add(investEndTime);
		}
		
		String conditionSql = condition.toString();
		String orderSql = " order by userbasicsinfo.createTime desc ";
		
		String querySql = selectSql + fromSql + whereSql + orderSql;
		if (conditionSql.length() > 0) {
			querySql = selectSql + fromSql + whereSql + conditionSql + orderSql;
		}
		
		List list = null;
		if (download) {
			list = dao.findBySql(querySql, params.toArray());
		} else {
			String countSql = "select count(userbasicsinfo.id) " + fromSql + whereSql + conditionSql;
			list = dao.pageListBySql(page, countSql, querySql, null, params.toArray());
		}
				
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] arr = (Object[]) obj;
				RegUserInvInfo vo = new RegUserInvInfo();
				vo.setUserName(StatisticsUtil.getStringFromObject(arr[0]));
				vo.setName(StatisticsUtil.getStringFromObject(arr[1]));
				vo.setIsAuthIps(StatisticsUtil.getIntegerFromObject(arr[2]));
				vo.setPhone(StatisticsUtil.getStringFromObject(arr[3]));
				vo.setRegTime(StatisticsUtil.getStringFromObject(arr[4]));
				vo.setRegisterSource(StatisticsUtil.getIntegerFromObject(arr[5]));
				vo.setRegisterChannel(StatisticsUtil.getStringFromObject(arr[6]));
				vo.setLoanSignName(StatisticsUtil.getStringFromObject(arr[7]));
				vo.setInvestMoney(StatisticsUtil.getDoubleFromBigdecimal((BigDecimal) arr[8]));
				vo.setInvestTime(StatisticsUtil.getStringFromObject(arr[9]));
				vo.setInviteName(StatisticsUtil.getStringFromObject(arr[11]));
				
				resultList.add(vo);
			}
		}
		
		return resultList;
	}
	
	/** 处理资金可用余额 */
	private void handleCashBalance(String ids, Map<Long, UserInfo> map) {
		String sql = "select t.id,t.cashBalance from userfundinfo t where t.id in(" + ids + ")";
		List genList = dao.findBySql(sql);
		if(genList != null && genList.size() > 0) {
			for(Object obj : genList) {
				Object[] arr = (Object[])obj;
				Long uid = StatisticsUtil.getLongFromBigInteger(arr[0]);//用户id
				if(map.containsKey(uid)) {
					Double cashBalance = StatisticsUtil.getDoubleFromBigdecimal((BigDecimal)arr[1]);
					UserInfo vo = map.get(uid);
					vo.setCashBalance(cashBalance);
					map.put(uid, vo);
				}
			}
		}
	}
	
	/** 处理推广人名称及其所在部门信息 */
	private void handleGeneralizerNameAndDept(String ids, Map<Long, UserInfo> map) {
		String sql = "select distinct u.id,g.uid,u.name,u.department ,u.user_type " 
				   + " from userbasicsinfo u, generalize g "
				   + " where u.id=g.genuid and g.state in (1,2) and g.uid in(" + ids + ")";
		List genList = dao.findBySql(sql);
		if(genList != null && genList.size() > 0) {
			for(Object obj : genList) {
				Object[] arr = (Object[])obj;
				Long uid = StatisticsUtil.getLongFromBigInteger(arr[1]);//用户id
				if(map.containsKey(uid)) {
					String generalizerName = StatisticsUtil.getStringFromObject(arr[2]);
					Integer generalizerDepartment = StatisticsUtil.getIntegerFromObject(arr[3]);
					Integer generalizeUserType = StatisticsUtil.getIntegerFromObject(arr[4]);
					UserInfo vo = map.get(uid);
					vo.setGeneralizerName(generalizerName);
					vo.setGeneralizerDepartment(generalizerDepartment);
					vo.setGeneralizeUserType(generalizeUserType);
					map.put(uid, vo);
				}
			}
		}
	}
	
	/** 处理用户登录时间 */
	private void handleUserLastLoginTime(String ids, Map<Long, UserInfo> map) {
		String sql = "select max(id),logintime,user_id from userloginlog where user_id in(" + ids + ") group by user_id";
		List loginList = dao.findBySql(sql);
		if(loginList != null && loginList.size() > 0) {
			for(Object obj : loginList) {
				Object[] arr = (Object[])obj;
				String lastLoginTime = StatisticsUtil.getStringFromObject(arr[1]);
				Long uid = StatisticsUtil.getLongFromBigInteger(arr[2]);
				if(map.containsKey(uid)) {
					UserInfo vo = map.get(uid);
					vo.setLastLoginTime(lastLoginTime);
					map.put(uid, vo);
				}
			}
		}
	}
	
	/** 用户类型比例相关 */
	public List<StatisticsInfo> queryUserTypeRateMap(String beginTime, String endTime) {
		List<StatisticsInfo> resultList = new ArrayList<StatisticsInfo>();
		
		/** 员工人数 */
		String sql = "select count(*) from userbasicsinfo where user_type=2 "
				+ " and DATE_FORMAT(createTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(createTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') ";
		Long employeeNum = StatisticsUtil.queryCountNum(sql, dao);		
		
		/** 员工推荐 */
		sql = "select count(t.id) from userbasicsinfo t where t.id in "
			 + " (select g.uid from generalize g,userbasicsinfo u where u.id=g.genuid and u.user_type=2) "
				+ "and DATE_FORMAT(t.createTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(t.createTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') ";
		Long employeeGenNum = StatisticsUtil.queryCountNum(sql, dao);
		
		/** 游客 */
		sql = "select count(*) from userbasicsinfo where user_type=1 and id not in "
				+ "(select g.uid from generalize g,userbasicsinfo u where g.genuid=u.id)" //去掉推介的
				+ " and DATE_FORMAT(createTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(createTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s') ";
		Long visitorNum = StatisticsUtil.queryCountNum(sql, dao);
		
		/** 游客推荐 */
		sql = "select count(t.id) from userbasicsinfo t where t.id in "
		    + "(select g.uid from generalize g,userbasicsinfo u where u.user_type=1 and g.genuid=u.id) "
		    + "and DATE_FORMAT(t.createTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('" + beginTime + "', '%Y-%m-%d %H:%i:%s') "
		    + "AND DATE_FORMAT(t.createTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('" + endTime + "', '%Y-%m-%d %H:%i:%s')";
		Long visitorGenNum = StatisticsUtil.queryCountNum(sql, dao);
		
		StatisticsInfo employee = new StatisticsInfo();
		employee.setLableName("员工");
		employee.setRegisterNum(employeeNum);
		
		StatisticsInfo employeeGen = new StatisticsInfo();
		employeeGen.setLableName("员工推荐");
		employeeGen.setRegisterNum(employeeGenNum);
		
		StatisticsInfo visitor = new StatisticsInfo();
		visitor.setLableName("游客");
		visitor.setRegisterNum(visitorNum);
		
		StatisticsInfo visitorGen = new StatisticsInfo();
		visitorGen.setLableName("游客推荐");
		visitorGen.setRegisterNum(visitorGenNum);
		
		resultList.add(employee);
		resultList.add(employeeGen);
		resultList.add(visitor);
		resultList.add(visitorGen);
		
		long totalNewRegister = employeeNum + employeeGenNum + visitorNum + visitorGenNum;
		computeRegisterPercent(resultList, totalNewRegister);
		return resultList;
	}
	
	/** 推广注册会员比例相关 */
	public List<StatisticsInfo> queryChannelMemberRateMap(String beginTime, String endTime) {
		/** 渠道推广相关信息 */
		List<StatisticsInfo> resultList = new ArrayList<StatisticsInfo>();
		Map<String, StatisticsInfo> oneLevelChannelMap = new HashMap<String, StatisticsInfo>();//一级
		Map<String, String> twoLevelChannelMap = new HashMap<String, String>();//二级：广告位
		Map<String, String> threeLevelChannelMap = new HashMap<String, String>();//三级：广告位
		String sql = "select id,name,type,upSpreadId from channelspread";
		List channelList = dao.findBySql(sql);
		if(channelList != null && channelList.size() > 0) {
			for(Object obj : channelList) {
				Object[] arr = (Object[])obj;
				Integer id = StatisticsUtil.getIntegerFromObject(arr[0]);
				String name = StatisticsUtil.getStringFromObject(arr[1]);
				Integer type = StatisticsUtil.getIntegerFromObject(arr[2]);//类型：1、渠道；2、广告位；3、广告；
				String upId = StatisticsUtil.getStringFromObject(arr[3]);//上级广告位id
				if(type == 1) {
					StatisticsInfo vo = new StatisticsInfo();
					vo.setLableName(name);
					vo.setRegisterNum(0L);
					oneLevelChannelMap.put("" + id, vo);
				} else if(type == 2) {
					twoLevelChannelMap.put("" + id, upId);
				} else if(type == 3) {
					threeLevelChannelMap.put("" + id, upId);
				}
			}
		}
		sql = "select count(d.regUserName) as registerNum,c.upSpreadId,c.type,c.id from channelspreaddetail d,channelspread c " 
				+ " where c.spreadId=d.spreadId and d.regStatus=1 "
				+ " and DATE_FORMAT(d.createTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
						+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(d.createTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
						+ endTime + "', '%Y-%m-%d %H:%i:%s') group by d.spreadId";
		List registerNumList = dao.findBySql(sql);
		if(registerNumList != null && registerNumList.size() > 0) {
			for(Object obj : registerNumList) {
				Object[] arr = (Object[])obj;
				long registerNum = StatisticsUtil.getLongFromBigInteger(arr[0]); 
				/** 上级推广id */
				String spreadId = StatisticsUtil.getStringFromObject(arr[1]);
				Integer type = StatisticsUtil.getIntegerFromObject(arr[2]);
				Integer id = StatisticsUtil.getIntegerFromObject(arr[3]);
				if(type == 1) {
					updateChannelRegisterNum(oneLevelChannelMap, "" + id, registerNum);
				} else if(type == 2) {
					updateChannelRegisterNum(oneLevelChannelMap, spreadId, registerNum);
				} else if(type == 3) {
					String upId = twoLevelChannelMap.get(spreadId);//根据二级id获取一级id
					updateChannelRegisterNum(oneLevelChannelMap, upId, registerNum);
				}
			}
		}
		
		for(Map.Entry<String, StatisticsInfo> entry : oneLevelChannelMap.entrySet()) {
			StatisticsInfo vo = entry.getValue();
			resultList.add(vo);
	    }
		
		long totalNewRegister = 0;
		for(StatisticsInfo vo : resultList) {
			totalNewRegister += vo.getRegisterNum();
		}
		computeRegisterPercent(resultList, totalNewRegister);
		return resultList;
	}
	
	/** 更新一级渠道的注册数量信息 */
	private void updateChannelRegisterNum(Map<String, StatisticsInfo> oneLevelChannelMap, String spreadId, long registerNum) {
		StatisticsInfo vo = oneLevelChannelMap.get(spreadId);
		if(vo != null) {
			vo.setRegisterNum(vo.getRegisterNum() + registerNum);
			oneLevelChannelMap.put(spreadId, vo);
		}
	}
	
	/** 注册用户认购占比 */
	public List<StatisticsInfo> queryRegisterBuyRate(String beginTime, String endTime) {
		List<StatisticsInfo> registerBuyRateList = new ArrayList<StatisticsInfo>();
		/** 注册用户已认购数量 */
		String sql = "select count(distinct u.id) from userbasicsinfo u, loanrecord l "
				+ "where u.id=l.userbasicinfo_id and l.isSucceed=1"
				+ " and DATE_FORMAT(u.createTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(u.createTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s')";
		Long registerBuyNum  = StatisticsUtil.queryCountNum(sql, dao);
		
		/** 注册用户未认购数量 */
		sql = "select count(distinct u.id) from userbasicsinfo u "
				+ " where u.id not in(select l.userbasicinfo_id from loanrecord l where l.isSucceed=1)"
				+ " and DATE_FORMAT(u.createTime, '%Y-%m-%d %H:%i:%s')>=DATE_FORMAT('"
				+ beginTime + "', '%Y-%m-%d %H:%i:%s')  AND DATE_FORMAT(u.createTime, '%Y-%m-%d %H:%i:%s')<=DATE_FORMAT('"
				+ endTime + "', '%Y-%m-%d %H:%i:%s')";
		Long registerUnBuyNum  = StatisticsUtil.queryCountNum(sql, dao);
		
		StatisticsInfo buyVo = new StatisticsInfo();
		buyVo.setLableName("已认购");
		buyVo.setRegisterNum(registerBuyNum);
		
		StatisticsInfo unBuyVo = new StatisticsInfo();
		unBuyVo.setLableName("未认购");
		unBuyVo.setRegisterNum(registerUnBuyNum);
		
		registerBuyRateList.add(buyVo);
		registerBuyRateList.add(unBuyVo);
		
		long totalNewRegister = registerBuyNum + registerUnBuyNum;
		computeRegisterPercent(registerBuyRateList, totalNewRegister);
		return registerBuyRateList;
	}
	
	/** 查询注册相关柱状图 */
	public Map<String, Object> queryRegisterBarGraphListAndTime(String type, Date queryDate,String beginTimeQuarter, String endTimeQuarter) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		/** 柱状图数据信息 */
		List<StatisticsInfo> registerBarGraphList = new ArrayList<StatisticsInfo>();
		/** 注册图购买人数相关 */
		List<StatisticsInfo> registerBuyBarGraphList = new ArrayList<StatisticsInfo>();
		String beginTime = "";
		String endTime = "";
		String currentTime = DateFormatUtil.dateToString(queryDate, "yyyy-MM-dd HH:mm:ss");
		String currentDate = currentTime.substring(0, 10);
		if("1".equals(type)) {//1、今日注册量；
			beginTime = currentDate + " 00:00:00";
			endTime = currentDate + " 23:59:59";
			registerBarGraphList = queryRegisterBarGraphListByDate(currentDate);
			registerBuyBarGraphList = queryRegisterBuyBarGraphListByDate(currentDate, registerBarGraphList);
		} else if("2".equals(type)) {//2、昨日注册量；
			Date yesterday = DateFormatUtil.increaseDay(queryDate, -1);
			String yesterdayTime = DateFormatUtil.dateToString(yesterday, "yyyy-MM-dd HH:mm:ss");
			String yesterdayDate = yesterdayTime.substring(0, 10);
			beginTime = yesterdayDate + " 00:00:00";
			endTime = yesterdayDate + " 23:59:59";
			registerBarGraphList = queryRegisterBarGraphListByDate(yesterdayDate);
			registerBuyBarGraphList = queryRegisterBuyBarGraphListByDate(yesterdayDate, registerBarGraphList);
		} else if("3".equals(type)) {//3、一周注册量；
			beginTime = DateFormatUtil.getMondayOfWeekByDate(currentDate) + " 00:00:00";;
			endTime = DateFormatUtil.getSundayOfWeekByDate(currentDate) + " 23:59:59";
			registerBarGraphList = queryRegisterBarGraphListByWeek(currentDate);
			registerBuyBarGraphList = queryRegisterBuyBarGraphListByWeek(currentDate, registerBarGraphList);
		} else if("4".equals(type)){//4、本月注册量
			beginTime = DateFormatUtil.getFirstDayOfMonthByDate(currentDate) + " 00:00:00";
			endTime = DateFormatUtil.getLastDayOfMonthByDate(currentDate) + " 23:59:59";
			registerBarGraphList = queryRegisterBarGraphListByMonth(currentDate);
			registerBuyBarGraphList = queryRegisterBuyBarGraphListByMonth(currentDate, registerBarGraphList);
		}else{
			beginTime=beginTimeQuarter+ " 00:00:00";
			endTime=endTimeQuarter+ " 23:59:59";
			registerBarGraphList= queryRegisterBarGraphListByQuarter(beginTimeQuarter, endTimeQuarter);
			registerBuyBarGraphList=queryRegisterBuyBarGraphListByQuarter(beginTimeQuarter, endTimeQuarter, registerBarGraphList);
			type="5";
		}
		resultMap.put("type", type);
		resultMap.put("registerBarGraphList", registerBarGraphList);//柱状图数据信息
		resultMap.put("registerBuyBarGraphList", registerBuyBarGraphList);//柱状图数据信息
		resultMap.put("beginTime", beginTime);
		resultMap.put("endTime", endTime);
		return resultMap;
	}
	
	/***按季度
	 * @param currentDate
	 * @return
	 */
	public List<StatisticsInfo> queryRegisterBarGraphListByQuarter( String beginTime, String endTime) {
		List<StatisticsInfo> statisList = StatisticsUtil.getWeekInfoListInQuarter(beginTime, endTime);
		for(StatisticsInfo vo : statisList) {
			Long countNum = queryRegisterNum(vo.getBeginDate() + " 00:00:00",  vo.getEndDate() + " 23:59:59");
			vo.setRegisterNum(countNum);
		}
		return statisList;
	}
	
	/**根据时间查询显示*/
	public List<StatisticsInfo> queryRegisterBuyBarGraphListByQuarter(String beginTime, String endTime, List<StatisticsInfo> registerBarGraphList) {
		List<StatisticsInfo> statisList = StatisticsUtil.getWeekInfoListInQuarter(beginTime, endTime);
		Map<String, Long> registerNumMap =  StatisticsUtil.queryRegisterMap(registerBarGraphList);
		for(StatisticsInfo vo : statisList) {
			/** 注册人数 */
			Long registerNum = registerNumMap.get(vo.getLableName());
			/** 认购人数 */
			Long registerBuyNum = queryRegisterBuyNum(vo.getBeginDate() + " 00:00:00",  vo.getEndDate() + " 23:59:59");
			vo.setRegisterNum(registerNum);
			vo.setRegisterBuyNum(registerBuyNum);
		}
		return statisList;
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
		/** 注册量柱状图相关统计信息 */
		String oneLine = generateOneLineStr(varDate, resultMap);
		/** 用户属性饼图统计信息 */
		String twoLine = generateTwoLineStr(varDate, resultMap);
		/** 推广注册会员比例 */
		String threeLine = generateThreeLineStr(varDate, resultMap);
		/** 注册会员中认购柱状图 */
		String fourLine = generateFourLineStr(varDate, resultMap);
		/** 注册会员认购比 **/
		String fiveLine = generateFiveLineStr(varDate, resultMap);
		String staticsMsg = oneLine + twoLine + threeLine + fourLine + fiveLine;
		return staticsMsg;
	}
	
	/** 生成第一条记录 */
	@SuppressWarnings("unchecked")
	private String generateOneLineStr(String varDate, Map<String, Object> resultMap) {
		/** 总注册人数 */
		long totalRegisterNum = (Long)resultMap.get("totalRegisterNum");
		/** 注册用户柱状图数据信息 */
		List<StatisticsInfo> registerBarGraphList = (List<StatisticsInfo>)resultMap.get("registerBarGraphList");
		String oneLine = "1、" + varDate + "注册量统计：总注册量为" + totalRegisterNum + " 人，";
		for(int i = 0; i < registerBarGraphList.size(); i++) {
			StatisticsInfo vo = registerBarGraphList.get(i);
			if(i < (registerBarGraphList.size() - 1)) {
				oneLine += vo.getLableName() + "为" + vo.getRegisterNum() + "人，";
			} else {
				oneLine += vo.getLableName() + "为" + vo.getRegisterNum() + "人;<br/>";
			}
		}
		return oneLine;
	}
	
	/** 生成第二条记录 */
	@SuppressWarnings("unchecked")
	private String generateTwoLineStr(String varDate, Map<String, Object> resultMap) {
		/** 用户类型比例饼图 */
		List<StatisticsInfo> userRegisterTypeList = (List<StatisticsInfo>)resultMap.get("userRegisterTypeArea");
		String twoLine = "2、" + varDate + "注册用户属性统计：";
		for(int i = 0; i < userRegisterTypeList.size(); i++) {
			StatisticsInfo vo = userRegisterTypeList.get(i);
			if(i < (userRegisterTypeList.size() - 1)) {
				twoLine += vo.getLableName() + "为" + vo.getPercentRate() + "%，";
			} else {
				twoLine += vo.getLableName() + "为" + vo.getPercentRate() + "%;<br/>";
			}
		}
		return twoLine;
	}
	
	/** 生成第三条记录 */
	@SuppressWarnings("unchecked")
	private String generateThreeLineStr(String varDate, Map<String, Object> resultMap) {
		/** 推广注册会员饼图 */
		List<StatisticsInfo> channelRegisterList = (List<StatisticsInfo>)resultMap.get("channelMemberArea");
		String threeLine = "3、" + varDate + "推广注册会员比例：";
		for(int i = 0; i < channelRegisterList.size(); i++) {
			StatisticsInfo vo = channelRegisterList.get(i);
			if(i < (channelRegisterList.size() - 1)) {
				threeLine += vo.getPercentRate() + "%来源于" + vo.getLableName() + "，"; 
			} else {
				threeLine += vo.getPercentRate() + "%来源于" + vo.getLableName() + ";<br/>"; 
			}
		}
		return threeLine;
	}
	
	/** 生成第四条记录 */
	@SuppressWarnings("unchecked")
	private String generateFourLineStr(String varDate, Map<String, Object> resultMap) {
		/** 已购买的柱状图数据信息 */
		List<StatisticsInfo> registerBuyBarGraphList = (List<StatisticsInfo>)resultMap.get("registerBuyBarGraphList");
		String fourLine = "4、" + varDate + "新注册会员中认购比：";
		for(int i = 0; i < registerBuyBarGraphList.size(); i++) {
			StatisticsInfo vo = registerBuyBarGraphList.get(i);
			fourLine += vo.getLableName() + "注册会员为" + vo.getRegisterNum() + "，认购会员为" + vo.getRegisterBuyNum() + "；";
			if(i == (registerBuyBarGraphList.size() - 1)) {
				fourLine += "<br/>";
			}
		}
		return fourLine;
	}
	
	/** 生成第5条记录 */
	@SuppressWarnings("unchecked")
	private String generateFiveLineStr(String varDate, Map<String, Object> resultMap) {
		/** 注册用户购买信息 */
		List<StatisticsInfo> registerBuyRateList = (List<StatisticsInfo>)resultMap.get("registerBuyRate");
		
		String fiveLine = "5、" + varDate + "注册用户认购占比：";
		for(int i = 0; i < registerBuyRateList.size(); i++) {
			StatisticsInfo vo = registerBuyRateList.get(i);
			if(i < (registerBuyRateList.size() - 1)) {
				fiveLine += vo.getLableName() + "的占" + vo.getPercentRate() + "%,"; 
			} else {
				fiveLine += vo.getLableName() + "的占" + vo.getPercentRate() + "%;"; 
			}
		}
		return fiveLine;
	}
	
	/** 处理注册人数报表下载相关统计数据 */
	public List<Map<String, String>> handleRegisterNumList(List<UserInfo> registerUserList) {
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		if(registerUserList != null && registerUserList.size() > 0) {
			for(UserInfo vo : registerUserList) {
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
				userMap.put("可用余额 ", "" + vo.getCashBalance());
				Integer ipsAccountStatus = vo.getIpsAccountStatus();
				String ipsAccountStatusStr = "暂无";
				//注册宝付状态：-1失败，0待确认，1成功
				if(ipsAccountStatus != null) {
					if(ipsAccountStatus == -1) {
						ipsAccountStatusStr = "失败";
					} else if(ipsAccountStatus == 0) {
						ipsAccountStatusStr = "待确认";
					} else if(ipsAccountStatus == 1) {
						ipsAccountStatusStr = "成功";
					}
				}
				userMap.put("注册宝付状态", ipsAccountStatusStr);
				String generalizerName = vo.getGeneralizerName();
				if(StringUtil.isBlank(generalizerName)) {
					generalizerName = "无";
				}
				userMap.put("推荐人", generalizerName);
				userMap.put("推荐人部门", StatisticsUtil.queryDepartmentNameById(vo.getGeneralizerDepartment()));
				Integer generalizeUserType = vo.getGeneralizeUserType();
				String generalizeUserTypeName = "暂无";		
				if(generalizeUserType != null) {
					if(generalizeUserType == 1) {
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
				String lastLoginTime = vo.getLastLoginTime();
				if(StringUtil.isBlank(lastLoginTime)) {
					lastLoginTime = "";
				}
				userMap.put("上次登陆时间", lastLoginTime);
				userMap.put("注册时间", vo.getCreateTime());
				content.add(userMap);
			} 
		}
		return content;
	}
	
	public List<Map<String, String>> handleInvUserList(List<RegUserInvInfo> regUserInvInfos) {
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		if (regUserInvInfos != null && regUserInvInfos.size() > 0) {
			for (RegUserInvInfo vo : regUserInvInfos) {
				Map<String, String> invUserMap = new HashMap<String, String>();
				invUserMap.put("用户名称", vo.getUserName());
				invUserMap.put("真实姓名", vo.getName());
				invUserMap.put("手机号", vo.getPhone());
				invUserMap.put("注册时间", vo.getRegTime());
				
				Integer regSource = vo.getRegisterSource();
				String regSourceName = "";
				if (regSource != null) {
					if (regSource == 1) {
						regSourceName = "PC";
					} else if (regSource == 2) {
						regSourceName = "H5";
					} else if (regSource == 8) {
						regSourceName = "安卓";
					} else if (regSource == 9){
						regSourceName = "IOS";
					} else {
						// do nothing
					}
				}
				invUserMap.put("注册端口", regSourceName);
				invUserMap.put("注册渠道", vo.getRegisterChannel());
				
				Integer isAuthIps = vo.getIsAuthIps();
				String isAuthIpsStr = "未授权";
				if (isAuthIps != null) {
					if (isAuthIps == 0) {
						isAuthIpsStr = "待确认";
					} else if (isAuthIps == 1) {
						isAuthIpsStr = "已授权";
					} else {
//						isAuthIpsStr = "未授权";
					}
				}
				invUserMap.put("宝付授权状态", isAuthIpsStr);
				
				invUserMap.put("投资项目", vo.getLoanSignName());
				invUserMap.put("认购金额", String.valueOf(vo.getInvestMoney()));
				invUserMap.put("认购日期", vo.getInvestTime());
				invUserMap.put("被推荐人", vo.getInviteName());

				content.add(invUserMap);
			} 
		}
		
		return content;
	}
	
	/** 处理注册量百分比 */
	public void computeRegisterPercent(List<StatisticsInfo> registerList, Long totalRegisterNum) {
		for(StatisticsInfo vo : registerList) {
			Long registerNum = vo.getRegisterNum();
			if(totalRegisterNum != 0) {
				double rate = StatisticsUtil.div(registerNum * 100, totalRegisterNum, 2);
				vo.setPercentRate(rate);
			}
		}
	}
	
	private void computeTerminalInvestPercent(List<TerminalInvNum> termInvList, double totalInvestNum) {
		for (TerminalInvNum vo : termInvList) {
			double investMoney = vo.getInvestMoney();
			if (investMoney != 0) {
				double percentage = StatisticsUtil.div(investMoney * 100, totalInvestNum, 2);
				vo.setPercentage(percentage);
			}
		}
	}
	
	private void computeChannelInvestPercent(List<ChannelInvNum> chnInvList, double totalInvestNum) {
		for (ChannelInvNum vo : chnInvList) {
			double investMoney = vo.getInvestMoney();
			if (investMoney != 0) {
				double percentage = StatisticsUtil.div(investMoney * 100, totalInvestNum, 2);
				vo.setPercentage(percentage);
			}
		}
	}
}