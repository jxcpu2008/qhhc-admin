package com.hc9.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.GeneralizeMoneyServices;
import com.hc9.service.GeneralizeService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.UserbasicsinfoService;


/**   
 * Filename:    GeneralizeMoneyController.java   
 * Company:     前海红筹  
 * @version:    1.0   
 * @since:  JDK 1.7.0_25  
 * Create at:   2014年2月11日 下午5:09:35   
 * Description:  后台会员推广信息查询控制层
 *   
 * Modification History:   
 * 时间    			作者   	   	版本     		描述 
 * ----------------------------------------------------------------- 
 * 2014年2月11日 	LiNing      1.0     	1.0Version   
 */

/**
 * <p>
 * Title:GeneralizeMoneyController
 * </p>
 * <p>
 * Description: 后台会员推广信息查询控制层
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author LiNing
 *         <p>
 *         date 2014年2月11日
 *         </p>
 */
@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping("/generalizemoney")
public class GeneralizeMoneyController {

    /** 注入服务层 */
    @Resource
    private GeneralizeMoneyServices generalizeMoneyServices;
    
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	private GeneralizeService generalizeService;
	
	
	@Resource
	private  LoanSignQuery loanSignQuery;
	
	@Resource
	private RechargeModel modelRecharge;
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	

    /**
     * <p>
     * Title: queryPage
     * </p>
     * <p>
     * Description: 后台查询会员推广记录
     * </p>
     * 
     * @param limit
     *            每页查询多少条
     * @param start
     *            从第几条开始查询
     * @param page
     *            分页模型
     * @param ids
     *            会员编号
     * @return 推广信息结果
     */
    @ResponseBody
    @RequestMapping("/querypage")
    public JSONObject queryPage(String limit, String start, PageModel page,
            String ids) {

        JSONObject resultjson = new JSONObject();

        JSONArray jsonlist = new JSONArray();

        // 每页显示条数
        if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
            page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
                    .parseInt(limit) : 10);
        } else {
            page.setNumPerPage(10);
        }

        // 计算当前页
        if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
            page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
        }

        @SuppressWarnings("rawtypes")
        List datalist = generalizeMoneyServices.queryByUser(page, ids);
        String titles = "id,adddate,userName,umoney,bonuses";
        // 将查询结果转换为json结果集
        ArrayToJson.arrayToJson(titles, datalist, jsonlist);

        resultjson.element("rows", jsonlist);
        resultjson.element("total", page.getTotalCount());

        return resultjson;
    }
    
    
    @ResponseBody
    @RequestMapping("/queryPageMoney")
    public JSONObject queryPageGeneralizeMoney(String limit, String start, PageModel page,
            String id) {

        JSONObject resultjson = new JSONObject();

        JSONArray jsonlist = new JSONArray();

        // 每页显示条数
        if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
            page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
                    .parseInt(limit) : 10);
        } else {
            page.setNumPerPage(10);
        }

        // 计算当前页
        if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
            page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
        }

        @SuppressWarnings("rawtypes")
        List datalist = generalizeMoneyServices.queryByMoney(page, id);
        String titles = "id,refered_userid,refer_userid,referUseridName,tender_money,tender_time,bonuses,release_status,release_time,loanrecord_id,order_no,trans_order_no,bonu_type,paid_bonuses,pay_state";
        // 将查询结果转换为json结果集
        ArrayToJson.arrayToJson(titles, datalist, jsonlist);

        resultjson.element("rows", jsonlist);
        resultjson.element("total", page.getTotalCount());

        return resultjson;
    }
    
    /**
     * 页面跳转方法
     * @param url 要跳转的页面
     * @param ids 附带参数
     * @param request HttpServletRequest
     * @return 传入的页面
     */
    @RequestMapping("/jume")
    public ModelAndView jumePage(String url, String ids,
            HttpServletRequest request) {

        request.setAttribute("ids", ids);
        return new ModelAndView("WEB-INF/views/admin/usermanager/" + url);
    }
    
    /***
     * 佣金转账
     * @param request
     * @param loanId
     * @return
     */
	@ResponseBody
	@RequestMapping("ipsTransBonuses")
    public  String ipsTransBonuses(HttpServletRequest request,String loanId){
		 String result = "5";
		 String adminIdConcurrentLock = "STR:TRANSBONUSES:ADMINUSER:CONCURRENT:LOCK";
		 if(!RedisHelper.isKeyExist(adminIdConcurrentLock)) {
			 RedisHelper.set(adminIdConcurrentLock, adminIdConcurrentLock);
			 result=baoFuLoansignService.ipsTransBonuses(request, loanId);
			 RedisHelper.del(adminIdConcurrentLock);
		 }
		 return result;
    }
    
 
    
	@RequestMapping("/generationBonuses")
    public  String generationBonuses(HttpServletRequest request,String loanId){
    	  List<Loanrecord>  listLoanrecord=loanSignQuery.selLoanrecordList(Long.valueOf(loanId));
    	  if(listLoanrecord.size()>0){
    		   for (int i = 0; i < listLoanrecord.size(); i++) {
				    Loanrecord  loanrecord=listLoanrecord.get(i);
				  //保存佣金
					generalizeService.saveGeneralizeMoney(loanrecord);
			}
    	  }
    	return "WEB-INF/views/success";
    }
    
	/***
	 * 导出佣金转账记录
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/outGeneralizeMoney")
	public void outGeneralizeMoney(HttpServletRequest request,HttpServletResponse response, String id) {

		String headers = "被推荐人,推荐人Id,推荐人,被推广人投资金额,被推广人投资时间,推广人应得佣金,佣金发放状态,佣金发放时间,项目名称,出借订单号,转帐订单号,奖金类型,实收佣金,支付状态";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;

		// 查询所有信息
		@SuppressWarnings("unchecked")
		List datalist =generalizeMoneyServices.queryByMoneyList(id);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : datalist) {

			Object[] result = (Object[]) obj;
			map = new HashMap<String, String>();

			// 将对应的值存放在map中
			for (int i = 0; i < headerLength; i++) {
				map.put(header[i],
						result[i] == null ? " " : result[i].toString());
			}

			content.add(map);
		}

		// 导出会员信息
		modelRecharge.downloadExcel("项目佣金转账记录", null, header, content, response);
	}
}
