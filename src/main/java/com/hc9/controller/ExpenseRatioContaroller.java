package com.hc9.controller;

import java.text.DecimalFormat;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.redis.RedisUtil;
import com.hc9.common.util.Arith;
import com.hc9.common.util.DwzResponseUtil;
import com.hc9.dao.entity.Costratio;
import com.hc9.service.ExpenseRatioService;

/**
 * 费用比例设置
 * @author frank 2014-10-01
 *
 */
@Controller
@RequestMapping("/expenseRatio")
@CheckLogin(value=CheckLogin.ADMIN)
public class ExpenseRatioContaroller {
    
    @Resource
    private ExpenseRatioService expenseRatioService;
    
    /**
     * 打开费用比例设置页面
     * @param request request
     * @return 返回费用比例设置路径
     */
    @RequestMapping("openRatio.htm")
    public String openRatio(HttpServletRequest request){
        request.setAttribute("costratio", expenseRatioService.findCostratio());
        return "WEB-INF/views/admin/fund/expense_ratio";
    }
    /**
     * 添加或修改
     * @return 返回处理信息
     */ 
    @RequestMapping("add.htm")
    @ResponseBody
    public JSONObject addCostratio(Costratio costratio){
        JSONObject json = new JSONObject();
        DecimalFormat df=new DecimalFormat("0.00");
        
        try {
        	if(null!=costratio.getId()){          
			        //违约金(借款人)
			        costratio.setOverdueRepayment(Arith.div(costratio.getOverdueRepayment(), 100, 4));
			        
			        //充值提现
			        costratio.setRecharge(Arith.div(costratio.getRecharge(), 100, 4));
			        costratio.setWithdraw(costratio.getWithdraw());
			        costratio.setRechargeMethod(costratio.getRechargeMethod());  
			        costratio.setWithdrawMethod(costratio.getWithdrawMethod());

			        //充值Vip
			        costratio.setVipUpgrade(Double.parseDouble(df.format(costratio.getVipUpgrade())));
			        //投资手续费
			        costratio.setLoanInvestment(Arith.div( costratio.getLoanInvestment(),100,4));
			        //实名认证费用
			        costratio.setNameAuth(costratio.getNameAuth());

			        
			    	//众持借款人手续费
			        costratio.setBorrowerFee(Arith.div(costratio.getBorrowerFee(),100,4));
			        
			    	//优先利息管理费
			        costratio.setPrioInvest(Arith.div(costratio.getPrioInvest(),100,4));
			    	
			    	//夹层分红管理费
			    	costratio.setMidInvest(Arith.div(costratio.getMidInvest(),100,4));
			    	
			    	//劣后分红管理费
			    	costratio.setAfterInvest(Arith.div(costratio.getAfterInvest(),100,4));
			    	
			    	//店铺融资服务费
			    	costratio.setShopOwerFee(Arith.div(costratio.getShopOwerFee(),100,4));
			    	
			    	//店铺分红
			    	costratio.setShopDividends(Arith.div(costratio.getShopDividends(),100,4));
			    	//股权转让费
			    	costratio.setConveyFee(Arith.div(costratio.getConveyFee(),100,4));
			    	//基金经理推广年利率提成
			    	costratio.setBusiness(Arith.div(costratio.getBusiness(),100,4));
			    	//普通会员推广年利率提成
			    	costratio.setMember(Arith.div(costratio.getMember(),100,4));
			    	//每次还款人数
			    	costratio.setRepayNum(costratio.getRepayNum());
			    	//第三方担保管理费
			    	costratio.setVoucherFee(Arith.div(costratio.getVoucherFee(),100,4));
			    	
			    	//第三方担保充值
                    costratio.setEscrowRecharge(Arith.div(costratio.getEscrowRecharge(),100,4));	
                    //第三方担保提现
                    costratio.setEscrowWithdraw(Arith.div(costratio.getEscrowWithdraw(), 100, 4));
                    costratio.setEscrowRechargeMethod(costratio.getEscrowRechargeMethod());
                    costratio.setWithdrawMethod(costratio.getWithdrawMethod());
                    
                    //清盘天数
                    costratio.setWindingNum(costratio.getWindingNum());
                    
                    //满标短信通知
                    costratio.setBidPhone(costratio.getBidPhone());
                    
                    //夹层利率
			    	costratio.setMiddleRate(Arith.div(costratio.getMiddleRate(),100,4));
			        //提现平台收取手续费
			    	costratio.setWithdrawRate(Arith.div(costratio.getWithdrawRate(),100,4));
			    	
			    	//提现收费起始时间
			    	costratio.setTimeDate(costratio.getTimeDate());
			    	
			    	//普通用户佣金开关
			    	costratio.setGeneralizeState(costratio.getGeneralizeState());
			    	
			    	//奖励开关
			    	costratio.setBonusState(costratio.getBonusState());
			    	//活动起始时间
			    	costratio.setStarTime(costratio.getStarTime());
			    	//活动结束时间
			    	costratio.setEndTime(costratio.getEndTime());
			    	//奖励红包开关
			    	costratio.setRedState(costratio.getRedState());
			    	//红筹币利率
			    	costratio.setHccoinRate(Arith.div(costratio.getHccoinRate(),100,4));
			    	
			    	//等本等息逾期利率
			    	costratio.setMatchingInterest(Arith.div(costratio.getMatchingInterest(), 100));
			    	//红筹理财师佣金年利率
			    	costratio.setFinancial(Arith.div(costratio.getFinancial(), 100));
        		expenseRatioService.update(costratio);
        	}else{
        		expenseRatioService.save(costratio);
        	}
        	RedisUtil.setCostratioInfo(costratio);
        	return DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_SUCCESS, "保存成功","#main31",null);
		} catch (Exception e) {
			return DwzResponseUtil.setJson(json, Constant.HTTP_STATUSCODE_ERROR, "更新失败","#main31",null);
		}	
    }
}
