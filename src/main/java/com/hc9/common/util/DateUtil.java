package com.hc9.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.hc9.common.log.LOG;

/**   
 * Filename:    DateUtil.java   
 * Company:     前海红筹  
 * @version:    1.0   
 * @since:  JDK 1.7.0_25  
 * Description:  

 * 主要是增加   getSpecifiedMonthAfter方法，计算月份
 */

public class DateUtil extends DateUtils {
    
    
    /**
    * <p>Title: getSpecifiedMonthAfter</p>
    * <p>Description: 计算传入时间加上传入月份后的年月日</p>
    * @param specifiedDay 年月日
    * @param monthNum 要增加的月份数（传入正数相加，负数相减）
    * @return 传入时间加上传入月份后的时间如传入（2014-03-27,3） 返回的则是2014-06-27
    */
    public static String getSpecifiedMonthAfter(String specifiedDay, int monthNum) {
        
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        c.set(Calendar.MONTH, month + monthNum);

        String dayAfter = new SimpleDateFormat("yyyy-MM-dd")
                .format(c.getTime());
        return dayAfter;
    }
    
    public static String getSpecifiedMonthAfter(String specifiedDay, int monthNum,String format) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        c.set(Calendar.MONTH, month + monthNum);

        String dayAfter = new SimpleDateFormat(format)
                .format(c.getTime());
        return dayAfter;
    }
    
    /**
     * <p>Title: getSpecifiedMonthAfter</p>
     * <p>Description: 计算传入时间加上传入天数后的年月日</p>
     * @param specifiedDay 年月日
     * @param Date 要增加的天数（传入正数相加，负数相减）
     * @return 传入时间加上传入月份后的时间如传入（2014-03-27,2） 返回的则是2014-03-29
     */
     public static String getSpecifiedDateAfter(String specifiedDay, int Date) {
         
         Calendar c = Calendar.getInstance();
         Date date = null;
         try {
             date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
         } catch (ParseException e) {
             e.printStackTrace();
         }
         c.setTime(date);
         int dayMoth = c.get(Calendar.DAY_OF_MONTH);
         c.set(Calendar.DAY_OF_MONTH, dayMoth + Date);

         String dayAfter = new SimpleDateFormat("yyyy-MM-dd")
                 .format(c.getTime());
         return dayAfter;
     }
     
     public static String addDateMinut(String day, int x)//返回的是字符串型的时间，输入的
   //是String day, int x
    {   
           SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制  
   //引号里面个格式也可以是 HH:mm:ss或者HH:mm等等，很随意的，不过在主函数调用时，要和输入的变
   //量day格式一致
           Date date = null;   
           try {   
               date = format.parse(day);   
           } catch (Exception ex) {   
               ex.printStackTrace();   
           }   
           if (date == null)   
               return "";   
           //System.out.println("front:" + format.format(date)); //显示输入的日期  
           Calendar cal = Calendar.getInstance();   
           cal.setTime(date);   
           cal.add(Calendar.MINUTE, x);// 24小时制   
           date = cal.getTime();   
           //System.out.println("after:" + format.format(date));  //显示更新后的日期 
           cal = null;   
           return format.format(date);   
       }
     
     /***
      * 判断时间是否在某某之间
      * @param date
      * @param starTime
      * @param endTime
      * @return
      */
     public static boolean isStringDate(String date,String starTime,String endTime){
    	 int strDate = Integer.parseInt(date.substring(4, 8));
    	 int starDateM=Integer.parseInt(starTime.substring(4, 8));
    	 int endDate=Integer.parseInt(endTime.substring(4, 8));
    	 if ((strDate >= starDateM && strDate <= endDate)) {  
    		 return true;
    	 }else{
    		 return false;
    	 }
     }
     public static void main(String[] args) {
    	 System.out.println(getSpecifiedMonthAfter("2014-06-27",9));
    	 System.out.println(isStringDate("20151205","20151105","20151205"));
    	 int a = 10;
    	 int b = 20;
    	 //System.out.println(new Random().nextInt(11));
    	 
    	 int arr[] = {10,20,50};
    	 Random random = new Random();//创建随机对象
    	 int arrIdx = random.nextInt(arr.length);//随机数组索引
    	 System.out.println(arr[arrIdx]);
	}
    
     public static boolean isAfter(String date1,String date2) {
    	 SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    	 try {
			Date dDate1 = fmt.parse(date1);
			Date dDate2 = fmt.parse(date2);
			return dDate1.after(dDate2);
		} catch (ParseException e) {
			e.printStackTrace();
			LOG.error("-----时间比较时出现异常----");
			return false;
		}
     }
}
