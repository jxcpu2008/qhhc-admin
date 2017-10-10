package com.hc9.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.hc9.dao.entity.Loansign;
import com.hc9.model.CalculateLoan;

/**
 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精
 * 确的浮点数运算，包括加减乘除和四舍五入。
 */
public class Arith{
    //默认除法运算精度
    private static final int DEF_DIV_SCALE = 10;
    //这个类不能实例化
    private Arith(){
    }
 
    /**
     * 提供精确的加法运算。
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }
    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    } 
    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }
    
    /**
     * 精确计算一个数字的N次方
     * @param v1 底数
     * @param count 指数
     * @return 底数的指数次方
     */
    private static BigDecimal muchmul(BigDecimal b1,int count){
    	BigDecimal b2 = new BigDecimal(Double.toString(1.00));
    	if(count==0){
    		return b2;
    	}else if(count==1){
    		return b1;
    	}else if(count>1){
    		
    		for (int i = 0; i < count; i++) {
				b2=b2.multiply(b1);
			}
    		return b2;
    	}
    	
    	return b1;
    	
    }
    
    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1,double v2){
        return div(v1,v2,DEF_DIV_SCALE);
    }
 
    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1,double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "要保留的小数位数必须是一个正整数或者0");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
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
     * 提供精确的小数位四舍五入处理。
     * 
     * @param v
     *            需要四舍五入的数字
     * @param scale
     *            小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static BigDecimal round(BigDecimal v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("要保留的小数位数必须是一个正整数或者0");
        }
        BigDecimal one = new BigDecimal("1");
        return v.divide(one, scale, RoundingMode.HALF_EVEN);
    }
    
    /**
     * 等额本息计算
     * @param loansign 借款标
     * @param month 分几期还
     * @return
     */
    public static double reimbursement(Loansign loansign,int month){
    	//"[贷款本金×月利率×（1+月利率）^还款月数]÷[（1+月利率）^还款月数—1]"
    	
    /*	//1
    	BigDecimal one = new BigDecimal("1");
    	
    	//月份
    	BigDecimal bmonth = new BigDecimal(month);
		
		//年利率
		BigDecimal yearrate=new BigDecimal(Double.toString(loansign.getRate()));
		
		BigDecimal sumMonth=new BigDecimal(12);
		
		//月利率
		BigDecimal monthrate=yearrate.divide(sumMonth,DEF_DIV_SCALE,BigDecimal.ROUND_HALF_UP);
		
		//借款总金额
		BigDecimal issueloan=new BigDecimal(Double.toString(loansign.getIssueLoan()));
		
		//贷款本金*月利率
		BigDecimal benmonth= issueloan.multiply(monthrate);
		
		//月利率+1
		BigDecimal bigrate= one.add(monthrate);
		
		//被除数
		BigDecimal beichushu =benmonth.multiply(Arith.muchmul(bigrate, month));
		
		//除数
		BigDecimal chushu=Arith.muchmul(bigrate, month);
		chushu=chushu.subtract(one);
		
		//计算结果
		return beichushu.divide(chushu,DEF_DIV_SCALE,BigDecimal.ROUND_HALF_UP).doubleValue();
  */
    	return 0.00;
    	}
    
    
    /**
     * 计算等额本息每一期本金和利息各还了多少
     * @param loansign
     * @return
     */
    public static List<CalculateLoan> loanCalculate(Loansign loansign){
    	
    	List<CalculateLoan> numlist=new ArrayList<CalculateLoan>();
    	//月份
    	/*BigDecimal bmonth = new BigDecimal(loansign.getMonth());
    	
    	BigDecimal sumMonth=new BigDecimal(12);
		
		//年利率
		BigDecimal yearrate=new BigDecimal(Double.toString(loansign.getRate()));
		
		//月利率
		BigDecimal monthrate=yearrate.divide(sumMonth,DEF_DIV_SCALE,BigDecimal.ROUND_HALF_UP);
		
		//借款总金额
		BigDecimal issueloan=new BigDecimal(Double.toString(loansign.getIssueLoan()));
		
    	
    	
    	//每一期还款金额
    	double huankuan=Arith.reimbursement(loansign, loansign.getMonth());
    	
    	for (int i = 1; i <= loansign.getMonth(); i++) {
    		
    		CalculateLoan calculoan=new CalculateLoan();
    		
    		//计算利息
			calculoan.setLixi(Arith.round(issueloan.multiply(monthrate).doubleValue(), 2));
			
			//计算本期所还本金
			calculoan.setBenjin(Arith.round(Arith.sub(huankuan, calculoan.getLixi()), 2));
			
			//还款期数
			calculoan.setCount(i);
			
			
			//计算未还本金
			calculoan.setNotReturn(Arith.round(Arith.sub(issueloan.doubleValue(), calculoan.getBenjin()), 2));

			//将本期未还本金，作为下一期的本金
			issueloan=new BigDecimal(Double.toString(calculoan.getNotReturn()));
			
			//将本期还款信息放入集合中
			numlist.add(calculoan);
		}
    	
    	
    	if(numlist.get(numlist.size()-1).getNotReturn()<0){
    		numlist.get(numlist.size()-1).setNotReturn(0.00);
    	}*/
    	
    	return numlist;
    }
    

}