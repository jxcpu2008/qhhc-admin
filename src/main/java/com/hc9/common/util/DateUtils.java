
package com.hc9.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期操作类
 * @author frank
 *
 */
public class DateUtils
{

    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final int MILLISECOND_FORMAT = 0x5265c00;

    /**
     * 用给定的样式格式化给定的日期（字符串）
     * @param format 日期样式
     * @param dateStr 目标日期
     * @return
     * @throws ParseException
     */
    public static Date bornDate(String format, String dateStr) throws ParseException{
        return (new SimpleDateFormat(format)).parse(dateStr);
    }
    /**
     * 将给定的日期格式化为默认的样式
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date bornDateSimple(String dateStr) throws ParseException {
        return bornDate("yyyy-MM-dd HH:mm", dateStr);
    }
    /**
     * 格式化当前日期
     * @param format
     * @return
     */
    public static String format(String format){
        if(StringUtil.isBlank(format))
            format = "yyyy-MM-dd HH:mm:ss";
        return (new SimpleDateFormat(format)).format(new Date());
    }
    
   
    public static String formatDateAfter(){
    	Date date=new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String specifiedDay = sdf.format(date);  
        String afterday=getSpecifiedDayAfter(specifiedDay);
     
        return afterday;
    }
    /**
     * 获取当前日期后一天
     * @param specifiedDay
     * @return
     */
    
    public static String getSpecifiedDayAfter(String specifiedDay) {  
        Calendar c = Calendar.getInstance();  
        Date date = null;  
        try {  
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);  
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
        c.setTime(date);  
        int day = c.get(Calendar.DATE);  
        c.set(Calendar.DATE, day + 1);  
        String dayAfter = new SimpleDateFormat("yyyy-MM-dd")  
                .format(c.getTime());  
        return dayAfter;  
    }


    
    /**
     * 用给定的样式格式化给定的日期（日期格式）
     * @param date 日期
     * @param format 樣式
     * @return
     */
    public static String format(Date date, String format) {
        return (new SimpleDateFormat(format)).format(date);
    }
    /**
     * 用给定的样式格式化给定的日期
     * @param c
     * @param format
     * @return
     */
    public static String format(Calendar c, String format){
        return (new SimpleDateFormat(format)).format(c.getTime());
    }
    /**
     * 格式化当前日期 为"yyyy-MM-dd HH:mm"
     * @return
     */
    public static String formatSimple(){
        return format(new Date(), "yyyy-MM-dd HH:mm");
    }
    /**
     * 格式化给定日期 为"yyyy-MM-dd HH:mm"
     * @return
     */
    public static String formatSimple(Date date){
        return format(date, "yyyy-MM-dd HH:mm");
    }
    
    /**
     * 格式化给定日期
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static String formatSimple(String dateStr) throws ParseException {
        return formatSimple(bornDateSimple(dateStr));
    }

    public static String format(String format_old, String dateStr, String format_new) throws ParseException {
        return format(bornDate(format_old, dateStr), format_new);
    }

    public static Calendar bornCalendar(String format, String dateStr) throws ParseException {
        Calendar cd = Calendar.getInstance();
        cd.setTime(bornDate(format, dateStr));
        return cd;
    }

    public static String formatSimple_GMT(long milliseconds) {
        return formatSimple(new Date(milliseconds));
    }

    public static String formatSimple_GMT(String milliseconds) {
        return formatSimple_GMT(Long.parseLong(milliseconds));
    }

    public static String format_GMT(long milliseconds, String format) {
        return format(new Date(milliseconds), format);
    }

    public static String formatSimple_UNIX(long milliseconds)  {
        return formatSimple(new Date(milliseconds * 1000L));
    }

    public static String formatSimple_UNIX(String milliseconds) {
        return formatSimple_UNIX(Long.parseLong(milliseconds));
    }

    public static String format_UNIX(long milliseconds, String format) {
        return format(new Date(milliseconds * 1000L), format);
    }

    public static long born_UNIX()
    {
        return (new Date()).getTime() / 1000L;
    }
    /**
     * 某個月份的天數可能拥有的最大值
     * @param year
     * @param month
     * @return
     */
    public static int getDayOfMonth(int year, int month)
    {
        Calendar a = Calendar.getInstance();
        int maxDate = 0;
        if(year != 0 && month != 0)
        {
            a.clear();
            a.set(1, year);
            a.set(2, month - 1);
            maxDate = a.getActualMaximum(5);
        }
        return maxDate;
    }
    /**
     * 返回当前月份的天数
     * @return
     */
    public static int getMonthOfMaxDay(){
        Calendar a = Calendar.getInstance();
        a.set(Calendar.MONTH, 1);
        a.roll(Calendar.MONTH, -1);
        return a.get(5);
    }
    /**
     * 返回指定的年和月份的该月份的天数
     * @param year
     * @param month
     * @return
     */
    public static int getMonthOfMaxDay(int year, int month){
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        return a.getActualMaximum(5);
    }
    /**
     * 比较当前日期与给定日期前后
     * @param format
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static boolean isBefore(String format, String dateStr)throws ParseException{
        return (new Date()).before(bornDate(format, dateStr));
    }

    public static boolean isSimpleBefore(String dateStr)throws ParseException {
        return (new Date()).before(bornDate("yyyy-MM-dd HH:mm", dateStr));
    }

    public static boolean isBefore(String format_1, String dateStr_1, String format_2, String dateStr_2)throws ParseException {
        return bornDate(format_1, dateStr_1).before(bornDate(format_2, dateStr_2));
    }

    public static boolean isAfter(String format, String dateStr) throws ParseException {
        return (new Date()).after(bornDate(format, dateStr));
    }

    public static boolean isSimpleAfter(String dateStr) throws ParseException {
        return (new Date()).after(bornDate("yyyy-MM-dd HH:mm", dateStr));
    }

    public static boolean isAfter(String format_1, String dateStr_1, String format_2, String dateStr_2) throws ParseException {
        return bornDate(format_1, dateStr_1).after(bornDate(format_2, dateStr_2));
    }

    public static boolean isDuring(String format, String begin, String end, String current) throws ParseException {
        return isBefore(format, begin, format, current) && isAfter(format, end, format, current);
    }

    public static boolean isDuring(String format, String begin, String end) throws ParseException{
        Date date = new Date();
        return bornDate(format, begin).before(date) && bornDate(format, end).before(date);
    }

    public static int differenceDate(String format, String begin, String end) throws ParseException{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        long millisecond = simpleDateFormat.parse(end).getTime() - simpleDateFormat.parse(begin).getTime();
        return (int)(millisecond / 0x5265c00L);
    }

    public static int differenceDateSimple(String begin, String end) throws ParseException  {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return (int)((simpleDateFormat.parse(end).getTime() - simpleDateFormat.parse(begin).getTime()) / 0x5265c00L);
    }
    /**
     * 给定日期与当前日期的差值
     * @param date
     * @return
     * @throws ParseException
     */
    public static int differenceDateSimple(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return (int)((simpleDateFormat.parse(date).getTime() - (new Date()).getTime()) / 0x5265c00L);
    }

    public static int differenceMonth(Calendar start, Calendar end){
        Calendar temp = (Calendar)end.clone();
        temp.add(5, 1);
        int year = end.get(1) - start.get(1);
        int month = 0;
        if(start.get(5) <= temp.get(5))
            month = temp.get(2) - start.get(2);
        else
        if(getMonthOfMaxDay(temp.get(1), temp.get(2)) == temp.get(5))
        {
            month = temp.get(2) - start.get(2);
        } else
        {
            month = temp.get(2) - start.get(2);
            month = month <= 0 ? 0 : month - 1;
        }
        if(start.get(5) == 1 && temp.get(5) == 1)
            return year * 12 + month + 1;
        if(start.get(5) != 1 && temp.get(5) == 1)
            return year * 12 + month;
        if(start.get(5) == 1 && temp.get(5) != 1)
            return year * 12 + month;
        else
            return (year * 12 + month) - 1 >= 0 ? year * 12 + month : 0;
    }

    public static int differenceMonth(Date start, Date end){
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        Calendar endCal = Calendar.getInstance();
        startCal.setTime(end);
        return differenceMonth(startCal, endCal);
    }

    public static int differenceMonth(String format, String start, String end) throws ParseException {
        return differenceMonth(bornCalendar(format, start), bornCalendar(format, end));
    }

    public static int differenceMonthSimple(String start, String end) throws ParseException {
        return differenceMonth("yyyy-MM-dd", start, end);
    }
    /**
     * 修改日历字段
     * @param format
     * @param dateStr
     * @param field
     * @param amount
     * @return
     * @throws ParseException
     */
    public static String add(String format, String dateStr, int field, int amount) throws ParseException {
        Calendar c = bornCalendar(format, dateStr);
        c.add(field, amount);
        return format(c, format);
    }
    /**
     * 修改当前日历字段
     * 比如field 为 Calendar.MONTH，amount=1,
     * 则修改后，返回的结果是下一个月的今天，如果是Calendar.YEAR,则明年今日
     * @param format 日期格式 “YYYY-MM-DD”
     * @param field 日历字段
     * @param amount 数量
     * @return
     * @throws ParseException
     */
    public static String add(String format, int field, int amount) throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(field, amount);
        return format(c, format);
    }
    /**
     * 计算两个日期之间的小时数
     * @param start 其实日期
     * @param end 结束日期
     * @param format 格式化
     * @return
     * @throws ParseException
     */
    public static long differentHour(String start,String end,String format) throws ParseException{
    	 SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    	 long hour = 1000*60*60;//1小时=1000毫秒*60秒*60分
    	 long different = simpleDateFormat.parse(end).getTime() - simpleDateFormat.parse(start).getTime();
    	 return different/hour+1;
    }
    
    /**
     * 计算两个日期之间的分钟数
     * @param start 其实日期
     * @param end 结束日期
     * @param format 格式化
     * @return
     * @throws ParseException
     */
    public static long differentMinute(String start,String end,String format) throws ParseException{
   	 SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
   	 long minute = 1000*60;//1小时=1000毫秒*60秒*60分
   	 long different = simpleDateFormat.parse(end).getTime() - simpleDateFormat.parse(start).getTime();
   	 return different/minute+1;
   }
    
    /**
     * 当前时间时分秒
     * @return
     */
    public static String getHHMMSS(){
    	return format(new Date(), "HHmmss");
    }
    
    public static void main(String[] args){
    	System.out.println(getHHMMSS());
    }
}
