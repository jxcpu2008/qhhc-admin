package com.hc9.controller;

import java.math.BigDecimal;
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
import com.hc9.common.json.JsonUtil;
import com.hc9.common.util.Arith;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.InterestIncreaseCard;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.InterestincreaseCountService;


/***
 * 加息劵统计
 * @author lkl
 *
 */
@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping("/interestincreaseCount")
public class InterestincreaseCountController {
	
	@Resource
	private RechargeModel rechargeModel;
	
	@Resource
	private InterestincreaseCountService interestincreaseCount;
	
	
	/**注册端口*/
	private static Map<Integer,String> REGISTERSOURCE;
	
	/**发放渠道*/
	private static Map<Integer,String> SOURCETYPE;
	
	/**使用状态*/
	private static Map<Integer,String> USEFLAG;
	
	static{
		REGISTERSOURCE=new HashMap<>();
		REGISTERSOURCE.put(1, "PC");
		REGISTERSOURCE.put(2, "H5");
		REGISTERSOURCE.put(8, "android");
		REGISTERSOURCE.put(9, "ios");
		REGISTERSOURCE.put(null, "PC");
	} 
	static{
		SOURCETYPE=new HashMap<>();
		SOURCETYPE.put(1, "投资");
		SOURCETYPE.put(2, "注册");
		SOURCETYPE.put(3, "奖励");
		SOURCETYPE.put(4, "抽奖");
	} 
	
	static{
		USEFLAG=new HashMap<>();
		USEFLAG.put(0, "未使用");
		USEFLAG.put(1, "已使用");
		USEFLAG.put(2, "待确认");
	} 
	
	
	@RequestMapping(value = { "index", "/" })
	public ModelAndView index() {
		ModelAndView returnModelAndView = new ModelAndView("WEB-INF/views/admin/quickquery/interestincreaseCount");
		return returnModelAndView;
	}
	
	
	/***
	 * 列表查询
	 * @param limit
	 * @param start
	 * @param redEnvelopeDetail
	 * @param request
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("interestincreaseQuery")
	public JSONObject interestincreaseQuery(String limit, String start,InterestIncreaseCard interestIncreaseCard, HttpServletRequest request, PageModel page) {

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
		List datalist = interestincreaseCount.interestincreaseCountPage(page, interestIncreaseCard);
		String titles = "id,userName,name,phone,createTime,registerSource,channelName,sourceType,interestRate,receiveTime,useFlag,tenderMoney,consumeTime,endTime";
		// 将查询结果转换为json结果集
		ArrayToJson.arrayToJson(titles, datalist, jsonlist);

		// 将数据源封装成json对象（命名必须row）
		resultjson.element("rows", jsonlist);
		// 总条数(命名必须total)
		resultjson.element("total", page.getTotalCount());

		return resultjson;
	}
	
	/***
	 * 导出数据
	 * @param request
	 * @param response
	 */
	@RequestMapping("outInterestincreaseExcel")
	public void outInterestincreaseExcel(InterestIncreaseCard interestIncreaseCard, HttpServletRequest request,HttpServletResponse response) {
		// 标题
		String[] header = new String[] { "用户名称", "真实姓名","手机号码", "注册时间","注册端口", "注册渠道","发放渠道","获得加息券点数","获得加息券日期","使用状态","使用加息券投资金额","使用加息券日期","加息券过期日期"};

		// 获取数据源
		List list = interestincreaseCount.queryInterestincreasecardl(interestIncreaseCard);

		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (Object obj : list) {
			Object[] str = (Object[]) obj;
			map = new HashMap<String, String>();
			map.put("用户名称", str[1] + "");
			map.put("真实姓名", str[2] + "");
			map.put("手机号码", str[3] + "");
			map.put("注册时间", str[4] + "" );
			map.put("注册端口", REGISTERSOURCE.get(str[5]));
			map.put("注册渠道", str[6]+"");
			map.put("发放渠道", SOURCETYPE.get(str[7]));
			map.put("获得加息券点数",str[8]+"");
			map.put("获得加息券日期", str[9]+"");
			map.put("使用状态", USEFLAG.get(str[10]));
			map.put("使用加息券投资金额", str[11] == null && str[11] == "" ? "" : Arith.round(new BigDecimal(str[11].toString()), 2) + "元");
			map.put("使用加息券日期", str[12] + "");
			map.put("加息券过期日期",str[13] + "" );
			content.add(map);
		}
		// 下载excel
		rechargeModel.downloadExcel("加息劵统计", null, header, content, response,request);
	}
	
	
	/***
	 * 加息卷统计计算
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/intCardCountMoney")
	public String intCardCountMoney(InterestIncreaseCard interestincreaseCard) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		/**发放加息卷总人数**/
		Integer distinctRedCount=interestincreaseCount.getDistinctIntCount(0,interestincreaseCard,1);
		resultMap.put("distinctIntCardCount", distinctRedCount);
		
		/**发放加息卷总金额**/
		Double redSumMoney=interestincreaseCount.getInterestRateSumMoney(0, interestincreaseCard);
		resultMap.put("intCardSumMoney", redSumMoney);

		/****使用加息卷总金额*/
		Double redSumMoneyOne=interestincreaseCount.getInterestRateSumMoney(1,interestincreaseCard);
		resultMap.put("intCardSumMoneyOne", redSumMoneyOne);
		
		/**发放加息卷总张数**/
		Integer redCount=interestincreaseCount.getIncreaseCardCount(0,interestincreaseCard,1);
		resultMap.put("intCardCount", redCount);
		
		/**使用加息卷总张数**/
		Integer redCountOne=interestincreaseCount.getIncreaseCardCount(1,interestincreaseCard,1);
		resultMap.put("intCardCountOne", redCountOne);
		
		/**使用加息卷总投资金额*/
		Double redSumTenderMoney=interestincreaseCount.getIntSumTenderMoney(interestincreaseCard,1);
		resultMap.put("intCardSumTenderMoney", redSumTenderMoney);
		
		/**使用加息卷投资总订单数**/
		resultMap.put("intCardCountTenderMoney", redCountOne);
		
		/**使用加息卷的认购人数*/
		Integer distinctLoanCount=interestincreaseCount.getDistinctIntCount(1,interestincreaseCard,1);
		resultMap.put("distinctLoanIntCardCount", distinctLoanCount);
		
		/**该时间段内的所有投资金额*/
		Double sumTenderMoney=interestincreaseCount.getSumTenderMoney(interestincreaseCard);
		/**
		 * 使用加息卷的投资金额占时间段内所有投资的占比
		 * 同一个时间段内，使用加息卷的投资金额/该时间段内的所有投资金额*100%
		 */
		Double redSumTenderMoneyTime=interestincreaseCount.getIntSumTenderMoney(interestincreaseCard,2);
		
		Double tenderMoneyProportion=Arith.mul(Arith.round(Arith.div(redSumTenderMoneyTime, sumTenderMoney),2), 100);
		resultMap.put("tenderMoneyIntCardProportion", tenderMoneyProportion);
		
		/**该时间段的认购人数*/
		Integer DistinctLoanrecordCount=interestincreaseCount.getDistinctLoanrecordCount(interestincreaseCard);

		/***
		 * 使用加息卷认购人数占时间段内认购人数的占比
           同一个时间段内，使用加息卷的认购人数/该时间段的认购人数*100% 
		 */
		Integer distinctLoanTime=interestincreaseCount.getDistinctIntCount(1,interestincreaseCard,2);
		Double loanrecordProportion=Arith.mul(Arith.round(Arith.div(distinctLoanTime, DistinctLoanrecordCount),2), 100);
		resultMap.put("loanrecordIntCardProportion", loanrecordProportion);
		
		/**该时间段内所有的订单数**/
		Integer LoanrecordIdCount=interestincreaseCount.getLoanrecordIdCount(interestincreaseCard);
		/***
		 * 使用加息卷的投资订单占时间段内投资订单的占比
		 * 同一个时间段内，使用加息卷投资的订单数/该时间段内所有的订单数*100%
		 */
		Integer redCountTime=interestincreaseCount.getIncreaseCardCount(1,interestincreaseCard,2);
		
		Double loanCountProportion=Arith.mul(Arith.round(Arith.div(redCountTime, LoanrecordIdCount),2), 100);
		resultMap.put("loanCountIntCardProportion", loanCountProportion);
		
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	

}
