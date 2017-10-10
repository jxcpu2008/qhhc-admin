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
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.RegBonus;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.BaoFuService;
import com.hc9.service.BonusService;
import com.hc9.service.LoanrecordService;
import com.hc9.service.PayLogService;
import com.hc9.service.PlankService;
import com.hc9.service.SmsService;
import com.hc9.service.UserbasicsinfoService;

/**
 * 推广奖励控制器
 * @author frank
 *
 */
@Controller
@RequestMapping(value ="/bonus")
@CheckLogin(value=CheckLogin.ADMIN)
public class BonusController {
	@Resource
	private PayLogService payLogService;
	
	@Resource
	private BonusService bonusService;
	
	@Resource
	private SmsService smsService;
	
	@Resource
	private LoanrecordService loanrecordService;
	
	@Resource
	private BaoFuService baoFuService;
	
	@Resource
	private PlankService plankService;
	
	@Resource
	private RechargeModel modelRecharge;
	
	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@RequestMapping(value = { "index", "/" })
	public ModelAndView index() {
	    return new ModelAndView("WEB-INF/views/admin/fund/bonusFund");
	}
	/***
	 * 奖励发放
	 * @param request
	 * @param response
	 * @param user
	 * @param status 1-被推荐人
	 *                          2-推荐人
	 * @return
	 */
	@ResponseBody
	@RequestMapping("releaseBonus.htm")
	public String  releaseBonus(HttpServletRequest request,HttpServletResponse response,Userbasicsinfo user,Integer status){
           return baoFuLoansignService.releaseBonusService(request, response, user,status);
	}
	
	/****
	 * 奖励发放查询sql
	 * @param limit
	 * @param start
	 * @param page
	 * @param user
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/queryPageAlready")
    public JSONObject queryPageAlready(String limit, String start, PageModel page,Userbasicsinfo user) {
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
        List datalist = bonusService.getbonusAlready(page, user);
        String titles = "id,user_id,user_name,user_phone,user_amount,user_state,user_date,user_releaseId,tenderMoney,tenderTime,genuser_id,genuser_name,genuser_amount,genuser_state,genuser_date,genuser_releaseId";
        // 将查询结果转换为json结果集
        ArrayToJson.arrayToJson(titles, datalist, jsonlist);
        resultjson.element("rows", jsonlist);

        resultjson.element("total", page.getTotalCount());

        return resultjson;
    }
	
	/***
	 * 奖励已发放记录
	 * @param request
	 * @param response
	 * @param user
	 */
	@RequestMapping("/outBonusFundAlready")
	public void outBonusFundAlready(HttpServletRequest request,HttpServletResponse response, Userbasicsinfo  user) {

		String headers = "ID,被推荐用户Id,被推荐真实姓名,被推荐手机,被推荐发放奖金,被推荐发放状态,被推荐发放时间,被推荐发放管理员,被推荐首次投资金额,被推荐首次投资日期,推荐用户Id,推荐真实姓名,推荐发放奖金,推荐发放状态,推荐发放时间,推荐发放管理员,";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;

		// 查询所有信息
		@SuppressWarnings("unchecked")
		List datalist =bonusService.queryBonusFundAlready(user);

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
		modelRecharge.downloadExcel("推荐奖励发放记录", null, header, content, response);
	}

	/***
	 * 注册奖励
	 * @return
	 */
	@RequestMapping(value = { "indexRegBonus", "/" })
	public ModelAndView indexRegBonus() {
	    return new ModelAndView("WEB-INF/views/admin/fund/regBonus");
	}
	
	/****
	 * 注册奖励
	 * @param limit
	 * @param start
	 * @param page
	 * @param user
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/queryRegBonus")
    public JSONObject queryRegBonus(String limit, String start, PageModel page,Userbasicsinfo user) {
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
        List datalist = bonusService.getRegBonus(page, user);
        String titles = "id,userName,name,createTime,pIpsAcctDate,isAuthIps,referrerUserName,referrerName,referrerIps,referrerPhone,bouns,releaseStatus,releaseTime,realname";
        // 将查询结果转换为json结果集
        ArrayToJson.arrayToJson(titles, datalist, jsonlist);
        resultjson.element("rows", jsonlist);

        resultjson.element("total", page.getTotalCount());

        return resultjson;
    }
	
	
	/***
	 * 注册奖励发放记录
	 * @param request
	 * @param response
	 * @param user
	 */
	@RequestMapping("/outRegBonus")
	public void outRegBonus(HttpServletRequest request,HttpServletResponse response, Userbasicsinfo  user) {

		String headers = "注册用户,注册真实姓名,注册时间,宝付注册时间,推荐人宝付授权状态,推荐用户名,推荐真实姓名,推荐人宝付号,推荐人手机,奖励金额,发放状态,奖励发放时间,奖励发放人员";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;

		// 查询所有信息
		@SuppressWarnings("unchecked")
		List datalist =bonusService.queryRegBonusAlready(user);

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
		modelRecharge.downloadExcel("注册奖励发放记录", null, header, content, response);
	}
	
	/***
	 * 注册奖励发放
	 * @param request
	 * @param response
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping("regBonus.htm")
	public String  regBonus(HttpServletRequest request,HttpServletResponse response,Userbasicsinfo user){
		  List<RegBonus> records=bonusService.queryRegBonus(user);
           return baoFuLoansignService.regBonusService(request, response,records);
	}
	
	/***
	 * 选中数据进行奖励发放
	 * @param request
	 * @param response
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("regBonusSelecte.htm")
	public String regBonusSelecte(HttpServletRequest request,HttpServletResponse response,String ids){
		List<RegBonus> records=bonusService.getRegBonus(ids);
		return baoFuLoansignService.regBonusService(request, response,records);
	}
	
	/***
	 * 获取注册用户的银行卡信息
	 * @param limit
	 * @param start
	 * @param userId
	 * @param request
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("userbankList")
	public JSONObject userbankListPage(String limit, String start,
			Long id, HttpServletRequest request, PageModel page) {
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

		// 分页数据源
		List datalist = bonusService.userbankPage(page, id);
		String titles = "id,bank_no,user_id,bank_name,pro_value,city_value,bank_address,validate_code,state";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}
	
	
}
