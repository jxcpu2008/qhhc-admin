package com.hc9.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.WithdrawAdminService;

/**
 * 提现记录
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/withdraw")
@CheckLogin(value = CheckLogin.ADMIN)
@SuppressWarnings(value = { "rawtypes" })
public class WithdrawAdminController {

    /**
     * 提现接口
     */
    @Resource
    private WithdrawAdminService withdrawAdminService;

    /**
     * 导出excel
     */
    @Resource
    private RechargeModel rechargeModel;
    
    @Resource
    private BaoFuLoansignService baoFuLoansignService;
    /**
     * 查询提现记录
     * 
     * @param page
     *            分页对象
     * @param beginDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param userName
     *            用户名
     * @param request
     *            request
     * @return 提现记录jsp
     */
    @RequestMapping("/openRatio")
    public String queryPage(@ModelAttribute("PageModel") PageModel page,
            String beginDate, String endDate, String userName,Integer isCreditor,Integer state, HttpServletRequest request) {
        Integer count = withdrawAdminService.queryCount(beginDate, endDate,userName,isCreditor,state);
        page.setTotalCount(count);
        List list = withdrawAdminService.queryPage(page, beginDate, endDate, userName,isCreditor,state);
        request.setAttribute("list", list);
        request.setAttribute("page", page);
        return "/WEB-INF/views/admin/withdraw/withdrawlist";
    }
    @RequestMapping("/export_excel")
    public void exportExcel(String ids,String beginDate, String endDate, String userName,Integer isCreditor,Integer state, HttpServletRequest request,HttpServletResponse response){
    	String[] headers=new String[]{
    			"姓名","用户名","提现金额","实际到账金额","宝付手续费","商户手续费","ips订单号","提现状态","用户类型","费用承担方","提现时间","备注"};
    	//数据源
    	List list=withdrawAdminService.queryById(ids, beginDate, endDate, userName,isCreditor,state);
    	List<Map<String,String>> content=new ArrayList<Map<String,String>>();
    	Map<String,String> map=null;
    	for(Object obj:list){
    		Object[] str=(Object[])obj;
    		map=new HashMap<String,String>();
    		map.put("姓名", str[1]+"");
    		map.put("用户名", str[2] + "");
            map.put("提现金额", str[3] + "");
            map.put("实际到账金额", str[11] + "");
            map.put("宝付手续费", str[4]==null?"":str[4]+"");
            map.put("商户手续费", str[5]==null?"":str[5]+"");
            map.put("ips订单号", str[6] + "");
            map.put("提现状态", str[7] + "");
            map.put("用户类型", str[8] + "");
            map.put("费用承担方", str[12] + "");
            map.put("提现时间", str[9] + "");
            map.put("备注", str[10]==null?"":str[10]+"");
            content.add(map);
    	}
    	// 导出充值记录
        rechargeModel.downloadExcel("提现记录", null, headers, content, response, request);
    }
    
	/***
	 * 客服提现业务查询
	 * 
	 * @param request
	 * @param wId
	 * @return
	 */
	@RequestMapping("ipsCustomerWithdrawNum.htm")
	@ResponseBody
	public String ipsCustomerWithdrawNum(HttpServletRequest request, String wId) {
		return baoFuLoansignService.returnWithdrawNumService(request, wId);
	}
}