package com.hc9.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;

/**
 * 借入记录
 * @author My_Ascii
 *
 */
@Service
public class BorrowRecordService {
    
    /**
     * 注入HibernateSupport
     */
    @Resource
    private HibernateSupport otherDao;
    
    public static String toJsonString(Object obj) {
        if (obj != null)
            return obj.toString();
        else
            return null;
    }

    private static SimpleDateFormat getDateParser(String pattern) {
        return new SimpleDateFormat(pattern);
    }
    public static String getDate(Date date,String format){
        return getDateParser(format).format(date);
    }
    
    /**
     * 格式化数字
     * @param val Object
     * @param groupingUsed boolean
     * @return String
     */
    public static String formateNumber(Object val,boolean groupingUsed){
        val = (null==val)?"0.00":val;
        NumberFormat f = DecimalFormat.getInstance(Locale.CHINA);
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);
        f.setGroupingUsed(groupingUsed);
        return f.format(val);
    }
    
    /**
     * 格式化数字，如大数值时以1.0000E方式时，转换为完整显示，不以科学计数法显示，并标以
     * @return 100,000,000.00
     * @author hulicheng
     * 2013-7-8
     * String
     */
    public static String formateNumber(Object val){
        try{
            return null==val?"0.00":formateNumber(val,true);
        }catch(Exception e){
            return "0.00";
        }
    }
    
    /**
     * 计算用户的(作为借款人)累计利息成本（在借入记录里面会调用）---》求每个标的总利息
     * @param user
     * @return
     * @throws ParseException 
     * @throws NumberFormatException 
     */
    public Double findDaiToBeAllPaidMoneyByUser(Userbasicsinfo user) {
        Double sum=0.00;
        StringBuffer sb=new StringBuffer("SELECT id,loanType,issueLoan,rate,useDay,publishTime,month,refundWay,loanstate from loansign where (loanstate=3 or  loanstate=4)  and  userbasicinfo_id=").append(user.getId());
        List<Object> list=otherDao.findBySql(sb.toString(), null);
        if(list.size()>0){
            Double oneFree=0.00;
            Object obje=null;
            Object[] obj=null;
            Object[] obj1=null;
            int day=0;
            for(Object object:list){
                    obj=(Object[])object;
                    oneFree=0.00;
                    if(null!=obj[1]&&Integer.parseInt(obj[1].toString())==1){//普通
                        if(null!=obj[7]&&Integer.parseInt(obj[7].toString())==3){//一次性还款
                                sb=new StringBuffer("SELECT rr.preRepayMoney,rr.realMoney FROM repaymentrecord  rr where rr.loanSign_id=").append(obj[0]);
                                obj1=(Object[])otherDao.findObjectBySql(sb.toString()); 
                            if(obj1[1]!=null){//实际还款有值
                                oneFree=Double.valueOf(obj1[1].toString())-Double.valueOf(obj[2].toString());
                            }else{
                                oneFree=Double.valueOf(obj1[0].toString())-Double.valueOf(obj[2].toString());
                            }
                        }else{
                            sb=new StringBuffer("SELECT SUM(rm.preRepayMoney) FROM repaymentrecord rm WHERE rm.loanSign_id =").append(obj[0]);
                            obje= otherDao.findObjectBySql(sb.toString());
                            oneFree=obje!=null?Double.valueOf(obje.toString()):0.00;
                        }
                        
                    }else if(null!=obj[1]&&Integer.parseInt(obj[1].toString())==2){//天标
                        if(null!=obj[8]&&Integer.parseInt(obj[8].toString())==4){//已经完成
                            sb=new StringBuffer("SELECT SUM(r.preRepayMoney) FROM repaymentrecord r where r.loanSign_id=").append(obj[0]);
                            obje=otherDao.findObjectBySql(sb.toString());
                            oneFree=obje!=null?Double.valueOf(obje.toString())-Double.valueOf(obj[2].toString()):0.00;
                        }else{
                            // money * rate * day;
                            oneFree=computeAccrual(Double.valueOf(obj[2].toString()), Double.valueOf(obj[3].toString()), 0, Integer.parseInt(obj[4].toString()), 2);
                        }
                    }else if(null!=obj[5]&&null!=obj[1]&&Integer.parseInt(obj[1].toString())==4){//流转标
                            sb=new StringBuffer("SELECT SUM(rm.preRepayMoney) FROM repaymentrecord rm WHERE  rm.loanSign_id =").append(obj[0]);
                            obje=otherDao.findObjectBySql(sb.toString());
                            oneFree=obje!=null?Double.valueOf(obje.toString()):0.00;                       
                    }
                    sum+=oneFree;
                }
            }
        return sum;
    }
    
    /**
     * 计算并返回1期的利息
     * 1、提前还款（针对普通标）
     * 2、天标还款
     * 3、秒标还款
     * 4、按时还款（每月还本息+每月还息到期还本）
     * 5、按时还款（一次性还本息）
     * 
     */
    public static Double computeAccrual(double money, double rate, int month, int day, int flag) {
        Double accrual = 0.0;
        switch(flag) {
            case 1:
                accrual = rate / 365 * money * day;
                break;
            case 2:
                accrual = money * rate * day;
                break;
            case 3:
                accrual = money * rate;
                break;
            case 4:
                accrual = rate / 12 * money;
                break;
            case 5:
                accrual = rate / 12 * money * month;
                break;
        }
        return accrual;
    }
    
    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "要保留的小数位数必须是一个正整数或者0");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    /**
     * 当前日期的日期+天数
     * @return
     * 2013-7-9
     * String
     */
    public static String getNowDateAddDays(int days){
        Calendar cd= Calendar.getInstance();
        cd.setTime(new Date());
        cd.add(Calendar.DATE, days);
        return getDate(cd.getTime(), "yyyy-MM-dd");
        
    }
    
}
