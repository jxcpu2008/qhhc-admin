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
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.CustomerDateService;


@Controller
@RequestMapping(value = { "/customerDate" })
@CheckLogin(value=CheckLogin.ADMIN)
public class CustomerDateController {
	
	@Resource
	private CustomerDateService  customerDateService;
	
	@Resource
	private RechargeModel modelRecharge;
	
	/***
	 * 投资信息
	 * @return
	 */
	@RequestMapping(value = { "openPlank" })
	public ModelAndView openPlank() {
		return new ModelAndView("WEB-INF/views/admin/customer/customerPlank");
	}
	
	/***
	 * 推荐投资信息
	 * @return
	 */
	@RequestMapping(value = { "openGPlank" })
	public ModelAndView openGeneralizePlank() {
		return new ModelAndView("WEB-INF/views/admin/customer/customerGPlank");
	}
	
	/***
	 * 注册信息
	 * @return
	 */
	@RequestMapping(value = { "openRegister" })
	public ModelAndView openRegister() {
		return new ModelAndView("WEB-INF/views/admin/customer/customerRegister");
	}
	
	/***
	 * 推荐注册信息
	 * @return
	 */
	@RequestMapping(value = { "openGRegister" })
	public ModelAndView openGeneralizeRegister() {
		return new ModelAndView("WEB-INF/views/admin/customer/customerGRegister");
	}
	/***
	 * 投资明细
	 * @param limit
	 * @param start
	 * @param request
	 * @param page
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping("customerPlankList")
	public JSONObject   customerPlankList(String limit, String start,HttpServletRequest request, PageModel page, Userbasicsinfo user) {
		JSONObject resultjson = new JSONObject();
		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}
		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}
		if(user.getUserType()==null){
			user.setUserType(2);
		}
		// 分页数据源
		List list =  new ArrayList();
		if(user.getUserType()==1){ //会员投资明细
			  list=customerDateService.getCustomerPlank(page, user.getAuthIpsTime(), user.getCreateTime(), 1);
	    }else if(user.getUserType()==2){ //员工投资明细
			  list=customerDateService.getCustomerPlank(page, user.getAuthIpsTime(), user.getCreateTime(), 2);
		}else if(user.getUserType()==4){ //居间人投资明细
			list=customerDateService.getCustomerPlank(page, user.getAuthIpsTime(), user.getCreateTime(), 4);
		}
		JSONArray jsonlist = new JSONArray();
		String titles = "userName,name,tenderTime,tenderMoney,channelName";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}
	
	
	/***
	 * 推荐投资明细
	 * @param limit
	 * @param start
	 * @param request
	 * @param page
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping("customerGPlankList")
	public JSONObject   customerGPlankList(String limit, String start,HttpServletRequest request, PageModel page, Userbasicsinfo user) {
		JSONObject resultjson = new JSONObject();
		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}
		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}
		if(user.getUserType()==null){
			user.setUserType(2);
		}
		// 分页数据源
		List list =  new ArrayList();
		 if(user.getUserType()==1){//会员推荐人投资明细
			 list=customerDateService.getCustomerPlankGeneralize(page, user.getAuthIpsTime(), user.getCreateTime(), 1);
		}else if(user.getUserType()==2){//员工推荐人投资明细
			  list=customerDateService.getCustomerPlankGeneralize(page, user.getAuthIpsTime(), user.getCreateTime(), 2);
		}else if(user.getUserType()==4){//居间人推荐投资明细
			  list=customerDateService.getCustomerPlankGeneralize(page, user.getAuthIpsTime(), user.getCreateTime(), 4);
		}
		JSONArray jsonlist = new JSONArray();
		String titles = "name,userName,tenderMoney,tenderTime,channelName";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}
	
	/**
	 * 注册信息
	 * @param limit
	 * @param start
	 * @param request
	 * @param page
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping("customerRegisterList")
	public JSONObject   userbasicsinfoList(String limit, String start,HttpServletRequest request, PageModel page, Userbasicsinfo user) {
		JSONObject resultjson = new JSONObject();
		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}
		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}
		if(user.getUserType()==null){
			user.setUserType(2);
		}
		// 分页数据源
		List list =  new ArrayList();
	    if(user.getUserType()==1){ //会员注册明细
			list=customerDateService.getUserbasicsinfo(page, user.getAuthIpsTime(), user.getCreateTime(), 1,true);
		}else if(user.getUserType()==2){ //员工注册明细
			  list=customerDateService.getUserbasicsinfo(page, user.getAuthIpsTime(), user.getCreateTime(), 2,false);
		}else if(user.getUserType()==4){ //居间人注册明细
			  list=customerDateService.getUserbasicsinfo(page, user.getAuthIpsTime(), user.getCreateTime(), 4,false);
		}
		JSONArray jsonlist = new JSONArray();
		String titles = "userName,name,createTime,channelName";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}
	
	
	/**
	 * 推荐注册信息
	 * @param limit
	 * @param start
	 * @param request
	 * @param page
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping("customerGRegisterList")
	public JSONObject   userbasicsinfoGList(String limit, String start,HttpServletRequest request, PageModel page, Userbasicsinfo user) {
		JSONObject resultjson = new JSONObject();
		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
					.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}
		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}
		if(user.getUserType()==null){
			user.setUserType(2);
		}
		// 分页数据源
		List list =  new ArrayList();
	   if(user.getUserType()==1){//会员推荐人注册明细
			 list=customerDateService.getUserbasicsinfoGeneralize(page, user.getAuthIpsTime(), user.getCreateTime(), 1);
		}else if(user.getUserType()==2){//员工推荐人注册明细
			  list=customerDateService.getUserbasicsinfoGeneralize(page, user.getAuthIpsTime(), user.getCreateTime(), 2);
		}else if(user.getUserType()==4){//居间人推荐注册明细
			  list=customerDateService.getUserbasicsinfoGeneralize(page, user.getAuthIpsTime(), user.getCreateTime(), 4);
		}
		JSONArray jsonlist = new JSONArray();
		String titles = "name,userName,createTime,channelName";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, list, jsonlist);
		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}
	
	
	/***
	 * 投资导出
	 * @param request
	 * @param response
	 * @param user
	 */
	@RequestMapping("/toExcelCustomer")
	public void dataToExcelCustomer(HttpServletRequest request,HttpServletResponse response, Userbasicsinfo user) {
		String headers = "用户名,真实姓名,投资明细,投资金额,渠道来源";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;
		String name="";
		if(user.getUserType()==null){
			user.setUserType(2);
		}
		// 查询所有信息
		List<Object> list = new ArrayList<Object>();
		 if(user.getUserType()==1){ //会员投资明细
			list=customerDateService.getCustomerPlankOut(user.getAuthIpsTime(), user.getCreateTime(), 1);
			 name="会员投资明细";
		} else if(user.getUserType()==2){ //员工投资明细
			  list=customerDateService.getCustomerPlankOut( user.getAuthIpsTime(), user.getCreateTime(), 2);
			  name="员工投资明细";
		}else if(user.getUserType()==4){ //居间人投资明细
			  list=customerDateService.getCustomerPlankOut( user.getAuthIpsTime(), user.getCreateTime(), 4);
			  name="居间人投资明细";
		}
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
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
		modelRecharge.downloadExcel(name, null, header, content, response);

	}
	
	
	/***
	 * 推荐投资导出
	 * @param request
	 * @param response
	 * @param user
	 */
	@RequestMapping("/toExcelGCustomer")
	public void dataToExcelGCustomer(HttpServletRequest request,HttpServletResponse response, Userbasicsinfo user) {
		String headers = "推荐人,被推荐人,投资明细,投资金额,渠道来源";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;
		String name="";
		if(user.getUserType()==null){
			user.setUserType(2);
		}
		// 查询所有信息
		List<Object> list = new ArrayList<Object>();
		 if(user.getUserType()==1){//会员推荐人投资明细
			 list=customerDateService.getCustomerPlankGeneralizeOut( user.getAuthIpsTime(), user.getCreateTime(), 1);
			 name="会员推荐人投资明细";
		}else if(user.getUserType()==2){//员工推荐人投资明细
			  list=customerDateService.getCustomerPlankGeneralizeOut(user.getAuthIpsTime(), user.getCreateTime(), 2);
			  name="员工推荐人投资明细";
		}else if(user.getUserType()==4){//居间人推荐投资明细
			  list=customerDateService.getCustomerPlankGeneralizeOut(user.getAuthIpsTime(), user.getCreateTime(), 4);
			  name="居间人推荐投资明细";
		}
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
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
		modelRecharge.downloadExcel(name, null, header, content, response);

	}
	
	/***
	 * 注册导出
	 * @param request
	 * @param response
	 * @param user
	 */
	@RequestMapping("/toExcelUserbasicsinfo")
	public void dataToExcelUserbasicsinfo(HttpServletRequest request,HttpServletResponse response, Userbasicsinfo user) {
		String headers = "用户名,真实姓名,注册时间,渠道来源";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;
		String name="";
		if(user.getUserType()==null){
			user.setUserType(1);
		}
		// 查询所有信息
		List<Object> list = new ArrayList<Object>();
		 if(user.getUserType()==1){ //会员注册明细
			list=customerDateService.getUserbasicsinfoOut(user.getAuthIpsTime(), user.getCreateTime(), 1,true);
			 name="会员注册明细";
		}else if(user.getUserType()==2){ //员工注册明细
			  list=customerDateService.getUserbasicsinfoOut( user.getAuthIpsTime(), user.getCreateTime(), 2,false);
			  name="员工注册明细";
		}else if(user.getUserType()==4){ //居间人注册明细
			  list=customerDateService.getUserbasicsinfoOut( user.getAuthIpsTime(), user.getCreateTime(), 4,false);
			  name="居间人注册明细";
		}
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
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
		modelRecharge.downloadExcel(name, null, header, content, response);
	}
	
	/***
	 * 推荐注册导出
	 * @param request
	 * @param response
	 * @param user
	 */
	@RequestMapping("/toExcelGUserbasicsinfo")
	public void dataToExcelGUserbasicsinfo(HttpServletRequest request,HttpServletResponse response, Userbasicsinfo user) {
		String headers = "用户名,真实姓名,注册时间,渠道来源";
		// 标题
		String[] header = StringUtil.sliptTitle(headers, null);
		int headerLength = header.length;
		String name="";
		if(user.getUserType()==null){
			user.setUserType(1);
		}
		// 查询所有信息
		List<Object> list = new ArrayList<Object>();
	    if(user.getUserType()==1){//会员推荐人注册明细
			 list=customerDateService.getUserbasicsinfoGeneralizeOut( user.getAuthIpsTime(), user.getCreateTime(), 1);
			 name="会员推荐人注册明细";
		}else  if(user.getUserType()==2){//员工推荐人注册明细
			  list=customerDateService.getUserbasicsinfoGeneralizeOut(user.getAuthIpsTime(), user.getCreateTime(), 2);
			  name="员工推荐人注册明细";
		}else  if(user.getUserType()==4){//员工推荐人注册明细
			  list=customerDateService.getUserbasicsinfoGeneralizeOut(user.getAuthIpsTime(), user.getCreateTime(), 4);
			  name="居间人推荐注册明细";
		}
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
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
		modelRecharge.downloadExcel(name, null, header, content, response);
	}
	
}
