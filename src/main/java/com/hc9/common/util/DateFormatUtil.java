package com.hc9.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.hc9.common.exception.RemoteCallException;


/**
 * @description 时间格式化工厂类
 * @author xuzhixiang
 * 
 */
public final class DateFormatUtil {
	//private static Logger log = Logger.getLogger(DateFormatUtil.class);

	/**
	 * @description 时间格式化
	 * @return 时间
	 * @throws ParseException
	 */
	public static Date getDateFormat() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date());
		Date date = sdf.parse(time);
		return date;
	}

	/**
	 * @description 时间格式化
	 * @param times
	 * @return 时间
	 * @throws ParseException
	 */
	public static Date getDateFormat(String times) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String time= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new
		// Date());
		Date date = sdf.parse(times);
		return date;
	}

	/**
	 * @description 时间格式化
	 * @param times
	 * @return 时间
	 * @throws ParseException
	 */
	public static Date getDateFormatForSelectTime(String times)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// String time= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new
		// Date());
		Date date = sdf.parse(times);
		return date;
	}

	/**
	 * @description 时间格式化
	 * @param times
	 * @return 时间加5年
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	public static Date getDateFormat(int times) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date());
		Date date = sdf.parse(time);
		int year = date.getYear();
		year = year + 5;
		date.setYear(year);
		return date;
	}

	public static Date getTime(String times) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// String time= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new
		// Date());
		Date date = null;
		try {
			date = sdf.parse(times);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			//log.error("日期格式化失败:" + e.getMessage());
		}
		return date;
	}

	/**
	 * 
	 * @description 比较2个日期的大小(他们的日期格式可以不同)
	 * @param
	 * @return boolean
	 */
	public static boolean isBefore(Date date1, Date date2)
			throws RemoteCallException {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		String sDate1 = fmt.format(date1);
		String sDate2 = fmt.format(date2);
		try {
			Date dDate1 = fmt.parse(sDate1);
			Date dDate2 = fmt.parse(sDate2);
			return dDate1.before(dDate2);

		} catch (ParseException e) {
			//log.error("日期转换失败:" + e.getMessage());
			throw new RemoteCallException(RemoteCallException.RCE_FAILED,
					"日期转换失败:" + e.getMessage());
		}
	}

	/**
	 * @description 将字符型转换为指定格式日期型
	 * 
	 * @param _date
	 *            需要转换成日期的字符串
	 * @param format
	 *            与需要转换成日期的字符串相匹配的格式
	 * @return
	 */
	public static Date stringToDate(String _date, String format)
			throws RemoteCallException {
		if (null == format || "".equals(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(_date);
		} catch (ParseException e) {
			System.out.println("日期转换失败");
			//log.error("日期转换失败");
			throw new RemoteCallException(RemoteCallException.RCE_FAILED,
					"日期转换失败:" + e.getMessage());
		}
		return date;
	}

	/**
	 * @description 将日期型转换为指定格式的字符串
	 * 
	 * @param date
	 *            日期
	 * @param format
	 *            格式
	 * @return
	 */
	public static String dateToString(Date date, String format) {
		if (null == format || "".equals(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 
	 * @description 获得当前日期前一天
	 * @author machunlin
	 * @date 2012-12-17
	 * @param
	 * @return Date
	 */
	public static Date getDayBefore(Date currentDate) {
		Calendar cal = Calendar.getInstance();// 使用默认时区和语言环境获得一个日历。
		cal.add(Calendar.DAY_OF_MONTH, -1);// 取当前日期的前一天.

		return cal.getTime();
	}

	/**
	 * 
	 * @description 获得当前日期下一天
	 * @author machunlin
	 * @date 2012-12-17
	 * @param
	 * @return Date
	 */
	public static Date getDayNext(Date currentDate) {
		Calendar cal = Calendar.getInstance();// 使用默认时区和语言环境获得一个日历。
		cal.add(Calendar.DAY_OF_MONTH, +1);// 取当前日期的后一天.

		return cal.getTime();
	}

	/**
	 * 获取指定日期的之前后之后的某一天日期
	 * @param currentDate 传入的特定日期
	 * @param incrBy 负数为之前几天；正数为之后几天
	 * */
	public static Date increaseDay(Date currentDate, int incrBy) {
		Calendar cal = Calendar.getInstance();// 使用默认时区和语言环境获得一个日历。
		cal.setTime(currentDate);
		cal.add(Calendar.DAY_OF_MONTH, incrBy);// 取当前日期的后一天.
		return cal.getTime();
	}
	
	/** 获取某一天所在周的周一 */
	public static String getMondayOfWeekByDate(String datetime) {
		String resultDate = "";
		Date date = stringToDate(datetime, "yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_WEEK);//获致是本周的第几天: 1代表星期天,7代表星期六
		int incrBy = 2 - day;
		if(day == 1) {
			incrBy = -6;
		}
		
    	//本周的周一
    	Date mondayDate = increaseDay(date, incrBy);
    	resultDate = DateFormatUtil.dateToString(mondayDate, "yyyy-MM-dd");
		return resultDate;
	}
	
	/** 获取某一天所在周的周日 */
	public static String getSundayOfWeekByDate(String datetime) {
		String resultDate = "";
		Date date = DateFormatUtil.stringToDate(datetime, "yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_WEEK);//获致是本周的第几天: 1代表星期天,7代表星期六
		if(day == 1) {
			resultDate = datetime;
		} else {
			int incrBy = 8 - day;
			//本周的周日
	    	Date sundayDate = DateFormatUtil.increaseDay(date, incrBy);
	    	resultDate = DateFormatUtil.dateToString(sundayDate, "yyyy-MM-dd");
		}
		return resultDate;
	}
	
	/** 获取本月的第一天 */
	public static String getFirstDayOfMonthByDate(String datetime) {
		Date date = DateFormatUtil.stringToDate(datetime, "yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		String resultDate = DateFormatUtil.dateToString(calendar.getTime(), "yyyy-MM-dd");
		return resultDate;
	}
	
	/** 获取本月的最后一天 */
	public static String getLastDayOfMonthByDate(String datetime) {
		Date date = DateFormatUtil.stringToDate(datetime, "yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));  
		String resultDate = DateFormatUtil.dateToString(calendar.getTime(), "yyyy-MM-dd");
		return resultDate;
	}
}
