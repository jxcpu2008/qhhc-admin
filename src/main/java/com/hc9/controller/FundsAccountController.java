package com.hc9.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.PayURL;
import com.hc9.common.util.Arith;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.UserBank;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.model.crs;
import com.hc9.service.BaoFuService;
import com.hc9.service.FundsAccountService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.UserbasicsinfoService;

/***
 * 会员资金账户信息
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("FundsAccount")
@CheckLogin(value=CheckLogin.ADMIN)
public class FundsAccountController {

	@Resource
	private FundsAccountService fundsAccountService;

	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	private LoanSignQuery loanSignQuery;

	@Resource
	private BaoFuService baoFuService;

	@Resource
	private RechargeModel rechargeModel;

	List<NameValuePair> nvps;

	@RequestMapping("openMenber")
	public String openMenber() {
		return "WEB-INF/views/admin/fund/memberFundsAccount";
	}

	/**
	 * <p>
	 * Title: queryPage
	 * </p>
	 * <p>
	 * Description: 分页查询会员信息
	 * </p>
	 * 
	 * @param userinfo
	 *            查询条件
	 * @param page
	 *            分页对象
	 * @param request
	 *            HttpServletRequest
	 * @param limit
	 *            每页查询条数
	 * @param start
	 *            从第几行开始查询
	 * @param isbrow
	 *            是否是借款人
	 * @return 查询结果转换后的json对象
	 */
	@ResponseBody
	@RequestMapping("/querList")
	public JSONObject queryPage(Userbasicsinfo userinfo, PageModel page,
			HttpServletRequest request, String limit, String start,
			String isbrow) {
		if (StringUtil.isNotBlank(isbrow) && StringUtil.isNumberString(isbrow)) {
			userinfo.setLockTime(isbrow);
		}
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
		List datalist = fundsAccountService.queryUserPage(page, userinfo);
		String titles = "id,username,realname,phone,credit,vipendtime,isborr,isLock,adminname,pMerBillNo";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);
		resultjson.element("rows", jsonlist);
		resultjson.element("total", page.getTotalCount());
		return resultjson;
	}

	/***
	 * 查询投资记录
	 * 
	 * @param start
	 * @param limit
	 * @param id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "loanrecordList", "/" })
	public JSONObject loanrecordList(
			@RequestParam(value = "start", defaultValue = "0", required = false) int start,
			@RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
			@RequestParam(value = "id", defaultValue = "", required = false) int id,
			HttpServletRequest request) {

		// 得到总条数
		Object count = fundsAccountService.getLoanrecordCount(id);
		Userbasicsinfo userinfo = new Userbasicsinfo();
		// 分页数据源
		List list = fundsAccountService.queryLoanrecordList(start, limit, id,
				1, userinfo);
		JSONArray jsonlist = fundsAccountService.getJSONArrayByList(list);

		JSONObject resultjson = new JSONObject();
		// 将数据源封装为json对象（命名必须rows）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", count);
		return resultjson;
	}

	/***
	 * 根据宝付个人账户查询余额
	 * 
	 * @param request
	 * @param userId
	 * @return
	 */
	@RequestMapping("/ipsSelFundsAccount")
	public String ipsSelFundsAccount(HttpServletRequest request, String userId,Integer status) {
		Userbasicsinfo user=null;
		if(status==2){
			Loansign loansign=loanSignQuery.getLoansign(userId);
			user=loansign.getUserbasicsinfo();
		}else{
			 user = userbasicsinfoService.queryUserById(userId);
		}
		try {
			crs cr = baoFuService.getCasbalance(user.getpMerBillNo());
			user.getUserfundinfo().setCashBalance(cr.getBalance());
			user.getUserfundinfo().setOperationMoney(cr.getBalance());
			userbasicsinfoService.update(user);
			request.setAttribute("user", user);
			return "/WEB-INF/views/admin/fund/SelFundsAccount";
		} catch (Exception e) {
			e.printStackTrace();
			return "WEB-INF/views/failure";
		}
	}

	/***
	 * 根据宝付个人账户查询银行卡信息
	 * 
	 * @param request
	 * @param userId
	 * @return
	 */
	@RequestMapping("/ipsSelUserBank")
	public String ipsSelUserBank(HttpServletRequest request, String userId) {
		Userbasicsinfo user = userbasicsinfoService.queryUserById(userId);
/*		UserBank userBank = fundsAccountService.getUserBank(user.getId().toString());
		if (userBank == null) {
			userBank = new UserBank();
		}*/
		List<UserBank> bankList=fundsAccountService.getUserBankList(user.getId().toString());
		nvps = new ArrayList<NameValuePair>();
		try {
			nvps.add(new BasicNameValuePair("requestParams", user
					.getpMerBillNo()));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(user
					.getpMerBillNo() + "~|~" + ParameterIps.getmerchantKey())));
			String result = CommonUtil
					.excuteRequest(PayURL.GETBANKCARURL, nvps);
			System.out.println("绑定验证码=" + result);
			Document doc = DocumentHelper.parseText(result);
			Element rootElt = doc.getRootElement(); // 获取根节点
			System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
			// 拿到crs节点下的子节点code值
			String code = rootElt.elementTextTrim("code");
			String msg = rootElt.elementTextTrim("msg");
			String sign = rootElt.elementTextTrim("sign");
			String infoXml = rootElt.elementTextTrim("info");
			String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~"
					+ ParameterIps.getDes_algorithm());
			if (sign.equals(Md5sign)) {
				if (code.equals("CSD000")) {
					String info = CommonUtil.aesDecryptKey16(infoXml,
							ParameterIps.getmerchantKey());
					System.out.println(info);
					Document infoDoc = DocumentHelper.parseText(info);
					Element rootInfoElt = infoDoc.getRootElement(); // 获取根节点
					System.out.println("根节点：" + rootInfoElt.getName()); // 拿到根节点的名称
					Iterator iteratorOrder = rootInfoElt.elementIterator("bank"); // 获取子节点result下的子节点order
					while (iteratorOrder.hasNext()) {
						Element elementOrder = (Element) iteratorOrder.next();
						String bank_no = elementOrder
								.elementTextTrim("bank_no");
						String bank_name = elementOrder
								.elementTextTrim("bank_name");
						String pro_value = elementOrder
								.elementTextTrim("pro_value");
						String city_value = elementOrder
								.elementTextTrim("city_value");
						String bank_address = elementOrder
								.elementTextTrim("bank_address");
						//比较返回的数据和本地数据
						int flag=-1;
						UserBank userBank;
						if(bankList!=null){
							for(int i=0;i<bankList.size();i++){
								//如果已经有银行卡号
								if(bankList.get(i).getBank_no().equals(bank_no)){
									flag=i;
									break;
								}
							}
						}else{
							bankList=new ArrayList<UserBank>();
						}
						if(flag==-1){
							userBank=new UserBank();
							userBank.setUserbasicsinfo(user);
							userBank.setBank_no(bank_no);
							userBank.setBank_name(bank_name);
							userBank.setPro_value(pro_value);
							userBank.setCity_value(city_value);
							userBank.setBank_address(bank_address);
							userBank.setState(1);
							bankList.add(userBank);
							fundsAccountService.saveOrUpdateUserBank(userBank);
						}else{
							userBank=bankList.get(flag);
							userBank.setUserbasicsinfo(user);
							userBank.setBank_no(bank_no);
							userBank.setBank_name(bank_name);
							userBank.setPro_value(pro_value);
							userBank.setCity_value(city_value);
							userBank.setBank_address(bank_address);
							userBank.setState(1);
							fundsAccountService.saveOrUpdateUserBank(userBank);
						}
						request.setAttribute("bankList", bankList);
					}
					return "/WEB-INF/views/admin/fund/SelUserBank";
				} else if (code.equals("CSD005")) {
					request.setAttribute("msg", msg);
					return "/WEB-INF/views/admin/fund/SelUserBankError";
				} else {
					request.setAttribute("msg", msg);
					return "/WEB-INF/views/admin/fund/SelUserBankError";
				}
			} else {
				request.setAttribute("msg", msg);
				return "/WEB-INF/views/admin/fund/SelUserBankError";
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("msg", "后台报异常错误");
			return "/WEB-INF/views/admin/fund/SelUserBankError";
		}
	}

	/***
	 * 导出投资记录
	 * 
	 * @param id
	 * @param userinfo
	 * @param request
	 * @param response
	 */
	@RequestMapping("/FundsLoanRecordExcel")
	public void percardPasstou(int id, Userbasicsinfo userinfo,
			HttpServletRequest request, HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "项目名称", "用户名", "购买金额", "购买时间", "购买状态",
				"订单号", "投资类型", "管理费" };
		// 获取数据源
		List list = fundsAccountService.queryLoanrecordList(0, 0, id, 2,
				userinfo);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("项目名称", str[1] + "");
			map.put("用户名", str[2] + "");
			map.put("购买金额",
					str[3] == null && str[3] == "" ? "" : Arith.round(
							new BigDecimal(str[3].toString()), 2) + "元");
			map.put("购买时间", str[4] + "");
			map.put("购买状态", str[5] + "");
			map.put("订单号", str[6] + "");
			map.put("投资类型", str[7] + "");
			map.put("管理费", str[8] + "");
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("投资记录", null, header, content, response,
				request);
	}

	/**
	 * 导出所有投资记录
	 * 
	 * @param id
	 * @param userinfo
	 * @param request
	 * @param response
	 */
	@RequestMapping("/FundsLoanAllRecordExcel")
	public void percardPasstouAll( Userbasicsinfo userinfo,HttpServletRequest request,
			HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "项目名称", "用户名", "购买金额", "购买时间", "购买状态",
				"订单号", "投资类型", "管理费" };
		// 获取数据源
		List list = fundsAccountService.queryLoanrecordListAll(userinfo);
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("项目名称", str[1] + "");
			map.put("用户名", str[2] + "");
			map.put("购买金额",
					str[3] == null && str[3] == "" ? "" : Arith.round(
							new BigDecimal(str[3].toString()), 2) + "元");
			map.put("购买时间", str[4] + "");
			map.put("购买状态", str[5] + "");
			map.put("订单号", str[6] + "");
			map.put("投资类型", str[7] + "");
			map.put("管理费", str[8] + "");
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("所有投资记录", null, header, content, response,
				request);
	}

	/***
	 * 导出资金明细
	 * 
	 * @param id
	 * @param userinfo
	 * @param request
	 * @param response
	 */
	@RequestMapping("/FundsAccountinfoExcel")
	public void FundsAccountinfoExcel(String id, Userbasicsinfo userinfo,HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "用户名","真实姓名","日期", "类型", "支出", "收入", "余额","费用",  "订单号"};
		// 获取数据源
		List list = fundsAccountService.queryAccountinfo(id, userinfo);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("用户名", str[0] + "");
			map.put("真实姓名", str[1] + "");
			map.put("日期", str[2] + "");
			map.put("类型", str[9] + "");
			map.put("支出",
					str[4] == null && str[4] == "" ? "" : Arith.round(
							new BigDecimal(str[4].toString()), 2) + "元");
			map.put("收入",
					str[5] == null && str[5] == "" ? "" : Arith.round(
							new BigDecimal(str[5].toString()), 2) + "元");
			map.put("余额",
					str[6] == null && str[6] == ""? "" : Arith.round(
							new BigDecimal(str[6].toString()), 2) + "元");
			map.put("费用",
					str[7] == null && str[7] == ""? "" : Arith.round(
							new BigDecimal(str[7].toString()), 2) + "元");
			map.put("订单号", str[8] + "");
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("资金明细记录", null, header, content, response,request);
	}
	
	

	/***
	 * 导出所有资金明细
	 * 
	 * @param id
	 * @param userinfo
	 * @param request
	 * @param response
	 */
	@RequestMapping("/FundsAccountinfoAllExcel")
	public void FundsAccountinfoAllExcel(Userbasicsinfo userinfo,HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "用户名","真实姓名","日期", "类型", "支出", "收入", "余额", "费用","订单号" };
		// 获取数据源
		List list = fundsAccountService.queryAccountinfoAll(userinfo);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("用户名", str[0] + "");
			map.put("真实姓名", str[1] + "");
			map.put("日期", str[2] + "");
			map.put("类型", str[9] + "");
			map.put("支出",
					str[4] == null && str[4] == "" ? "" : Arith.round(
							new BigDecimal(str[4].toString()), 2) + "元");
			map.put("收入",
					str[5] == null && str[5] == "" ? "" : Arith.round(
							new BigDecimal(str[5].toString()), 2) + "元");
			map.put("余额",
					str[6] == null && str[6] == ""? "" : Arith.round(
							new BigDecimal(str[6].toString()), 2) + "元");
			map.put("费用",
					str[7] == null && str[7] == ""? "" : Arith.round(
							new BigDecimal(str[7].toString()), 2) + "元");
			map.put("订单号", str[8] + "");
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("所有资金明细记录", null, header, content, response,request);
	}

}
