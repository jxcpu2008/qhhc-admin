package com.hc9.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
import com.hc9.model.StatisticsInfo;

public class StatisticsUtil {
	/** 获取某天所在周的所有日期信息 */
	public static List<StatisticsInfo> weekDayOfDatetime(String dateTime) {
		List<StatisticsInfo> weekDayList = new ArrayList<StatisticsInfo>();
    	String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date mdate = DateFormatUtil.stringToDate(dateTime, "yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(mdate);

        int b = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (b == 0)
            b = 7;
        Long fTime = mdate.getTime() - b * 24 * 3600000;
        for (int a = 1; a <= 7; a++) {
        	Date fdate = new Date();
            fdate.setTime(fTime + (a * 24 * 3600000));
            StatisticsInfo vo = new StatisticsInfo();
            vo.setLableName(weekDays[a-1]);
            vo.setBeginDate(sdf.format(fdate));
            weekDayList.add(vo);
        }
        return weekDayList;
    }
	
	/** 获取本月的相关周信息 */
	public static List<StatisticsInfo> getWeekInfoListInMonth(String currentDate) {
		String[] weeks = {"一", "二", "三", "四", "五", "六"};
		List<StatisticsInfo> monthList = new ArrayList<StatisticsInfo>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date mdate = DateFormatUtil.stringToDate(currentDate, "yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mdate);
	    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
	    String firstDay = sdf.format(calendar.getTime());
	    
	    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));     
	    String lastDay = sdf.format(calendar.getTime());
	    
	    Date lastDate = DateFormatUtil.stringToDate(lastDay, "yyyy-MM-dd");
	    calendar.setTime(lastDate);
	    int maxWeek = calendar.get(Calendar.WEEK_OF_MONTH);//本月最大的周数
	    
	    Date handleDate = DateFormatUtil.stringToDate(firstDay, "yyyy-MM-dd");
	    for(int i = 1; i <= maxWeek; i++) {
	    	Date nowHandle = handleDate;
	    	calendar.setTime(handleDate);
	    	int week = calendar.get(Calendar.WEEK_OF_MONTH);//获取是本月的第几周
	    	int day = calendar.get(Calendar.DAY_OF_WEEK);//获致是本周的第几天地, 1代表星期天...7代表星期六
	    	int incrBy = 1;
	    	if(day > 0) {
	    		incrBy = 9 - day;
	    	}
	    	//下一周的周一
	    	handleDate = DateFormatUtil.increaseDay(nowHandle, incrBy);
	    	
	    	StatisticsInfo statisticsInfo = new StatisticsInfo();
	    	statisticsInfo.setIndex(i);
	    	statisticsInfo.setLableName("第" + weeks[week - 1] + "周");
	    	
	    	/** 开始时间 */
	    	String beginDate = DateFormatUtil.dateToString(nowHandle, "yyyy-MM-dd");
	    	
	    	/** 结束时间 */
	    	String endDate = lastDay;
	    	if(i != maxWeek) {
	    		endDate = DateFormatUtil.dateToString(DateFormatUtil.increaseDay(handleDate, -1), "yyyy-MM-dd");
	    	}
	    	statisticsInfo.setBeginDate(beginDate);
	    	statisticsInfo.setEndDate(endDate);
	    	monthList.add(statisticsInfo);
	    }
	    return monthList;
	}
	
	/** 获取本月的相关周信息 */
	public static List<StatisticsInfo> getWeekInfoListInQuarter(String beginTime, String endTime) {
		List<StatisticsInfo> quarterList = new ArrayList<StatisticsInfo>();
    	StatisticsInfo statisticsInfo = new StatisticsInfo();
    	statisticsInfo.setIndex(1);
    	statisticsInfo.setLableName(beginTime+"~"+endTime);
    	statisticsInfo.setBeginDate(beginTime);
    	statisticsInfo.setEndDate(endTime);
    	quarterList.add(statisticsInfo);
	    return quarterList;
	}
	
	/** 获取统计数 */
	@SuppressWarnings("rawtypes")
	public static Double querySumNum(String sql, HibernateSupport dao) {
		Double result = 0.0;
		List dataList = dao.findBySql(sql);
		if(dataList != null && dataList.size() > 0) {
			Object obj = dataList.get(0);
			if(obj != null) {
				BigDecimal sumNum = (BigDecimal)obj;
				result = sumNum.doubleValue();
			}
		}
		return result;
	}
	
	/** 获取记录数 */
	@SuppressWarnings("rawtypes")
	public static Long queryCountNum(String sql, HibernateSupport dao) {
		Long result = new Long(0);
		List dataList = dao.findBySql(sql);
		if(dataList != null && dataList.size() > 0) {
			Object obj = dataList.get(0);
			if(obj != null) {
				BigInteger sumNum = (BigInteger)obj;
				result = sumNum.longValue();
			}
		}
		return result;
	}
	
	/** 组装分页模型对象 */
	public static PageModel comsitePageModel(String start, String limit) {
		PageModel page = new PageModel();
		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}
		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}
		return page;
	}
	
	/** 根据开始时间和接受时间获取报表的查询类型 */
	public static String computeReportType(String beginTime, String endTime) {
		String type = "3";
		Date beginDate = DateFormatUtil.stringToDate(beginTime, "yyyy-MM-dd");
		Date enDate = DateFormatUtil.stringToDate(endTime, "yyyy-MM-dd");
		long sevenDays = 7 * 24 * 60 * 60 * 1000;
		if(enDate.getTime() - beginDate.getTime() >= sevenDays) {
			type = "4";
		}
		return type; 
	}
	
	/** 处理报表类型和开始时间 */
	public static Map<String, Object> handleReportTypeAndBeginDate(String type, String beginTime, String endTime) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Date queryDate = new Date();
		if(StringUtil.isNotBlank(beginTime) && StringUtil.isNotBlank(endTime)) {
			type = computeReportType(beginTime, endTime);
			queryDate = DateFormatUtil.stringToDate(beginTime, "yyyy-MM-dd");
		} else if(StringUtil.isNotBlank(beginTime)) {
			type = "1";
			queryDate = DateFormatUtil.stringToDate(beginTime, "yyyy-MM-dd");
		} else {
			if(StringUtil.isBlank(type)) {
				type = "1";
			}
		}
		resultMap.put("queryDate", queryDate);
		resultMap.put("type", type);
		return resultMap;
	}
	
	/** 根据报表类型和查询开始日期获取报表相关的开始时间和结束时间 */
	public static Map<String, Object> handleReportBeginAndEndTime(String type, String beginTime, String endTime) {
		Map<String, Object> resultMap = handleReportTypeAndBeginDate(type, beginTime, endTime);
		Date queryDate = (Date)resultMap.get("queryDate");
		type = (String)resultMap.get("type");
		String currentTime = DateFormatUtil.dateToString(queryDate, "yyyy-MM-dd HH:mm:ss");
		String currentDate = currentTime.substring(0, 10);
		if("1".equals(type)) {//1、今日注册量；
			beginTime = currentDate + " 00:00:00";
			endTime = currentDate + " 23:59:59";
		} else if("2".equals(type)) {//2、昨日注册量；
			Date yesterday = DateFormatUtil.increaseDay(queryDate, -1);
			String yesterdayTime = DateFormatUtil.dateToString(yesterday, "yyyy-MM-dd HH:mm:ss");
			String yesterdayDate = yesterdayTime.substring(0, 10);
			beginTime = yesterdayDate + " 00:00:00";
			endTime = yesterdayDate + " 23:59:59";
		} else if("3".equals(type)) {//3、一周注册量；
			beginTime = DateFormatUtil.getMondayOfWeekByDate(currentDate) + " 00:00:00";;
			endTime = DateFormatUtil.getSundayOfWeekByDate(currentDate) + " 23:59:59";
		} else {//4、本月注册量
			beginTime = DateFormatUtil.getFirstDayOfMonthByDate(currentDate) + " 00:00:00";
			endTime = DateFormatUtil.getLastDayOfMonthByDate(currentDate) + " 23:59:59";
		}
		resultMap.put("beginTime", beginTime);
		resultMap.put("endTime", endTime);
		return resultMap;
	}
	
	/** 从 对象获取字符串 */
	public static String getStringFromObject(Object obj) {
		if(obj == null) {
			return "";
		}
		return (String)obj;
	}
	
	/** 从bigdecimal获取double类型的值 */
	public static Double getDoubleFromBigdecimal(BigDecimal big) {
		Double result = 0.0;
		if(big != null) {
			result = big.doubleValue();
		}
		return result;
	}
	
	/** 从 对象获取字符串 */
	public static Integer getIntegerFromObject(Object obj) {
		if(obj == null) {
			return 0;
		}
		return (Integer)obj;
	}
	
	/** 从 对象获取字符串 */
	public static long getLongFromBigInteger(Object obj) {
		if(obj == null) {
			return 0L;
		}
		BigInteger result = (BigInteger)obj;
		return result.longValue();
	}
	
	/** 注册人数相关map */
	public static Map<String, Long> queryRegisterMap(List<StatisticsInfo> registerBarGraphList) {
		Map<String, Long> resultMap = new HashMap<String, Long>();
		for(StatisticsInfo vo : registerBarGraphList) {
			resultMap.put(vo.getLableName(), vo.getRegisterNum());
		}
		return resultMap;
	}
	
	/** 根据部门id获取部门名称 */
	public static String queryDepartmentNameById(Integer id) {
		String result = "暂无";
		if(id != null) {
			
			if (id == 1) {
				result = "总裁办";
			} else if (id == 2) {
				result = "财务部";
			} else if (id == 3) {
				result = "行政部";
			} else if (id == 4) {
				result = "副总办";
			} else if (id == 5) {
				result = "运营中心";
			} else if (id == 6) {
				result = "培训部";
			} else if (id == 7) {
				result = "风控部";
			} else if (id == 8) {
				result = "IT部";
			} else if (id == 9) {
				result = "摄影部";
			} else if (id == 10) {
				result = "推广部";
			} else if (id == 11) {
				result = "项目部";
			} else if (id == 12) {
				result = "客服部";
			} else if (id == 13) {
				result = "事业一部";
			} else if (id == 14) {
				result = "事业二部";
			}else if (id == 15) {
				result = "离职员工";
			}
		}
		return result;
	}
	
	/**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(Long v1,Long v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "要保留的小数位数必须是一个正整数或者0");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    public static double div(Double v1, Double v2, int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "要保留的小数位数必须是一个正整数或者0");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    /** 处理饼图百分比相关（标签加上百分比） */
    public static void handleAreaGraphLabelPercent(List<StatisticsInfo> list) {
    	for(StatisticsInfo vo : list) {
			vo.setLableName(vo.getLableName() + "(" + vo.getPercentRate() + "%)");
		}
    }
}
