package com.hc9.controller;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.json.JsonUtil;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.model.report.ConcertActivityVo;
import com.hc9.service.ConcertActivitiesService;

@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping(value={"/concertActivities"})
public class ConcertActivitiesController {

	@Resource
	private ConcertActivitiesService concertActivitiesService;
	
	/** Excel导出 */
	@Resource
	private RechargeModel rechargeModel;
	
	@RequestMapping(value={"/concertActivitiesDetail"})
	public ModelAndView concertActivitiesDetail(){
		ModelAndView concertView=new ModelAndView("WEB-INF/views/admin/quickquery/concertActivities");
		return concertView;
	}
	
	/**演唱会活动统计查询*/
	@ResponseBody
	@RequestMapping("concertActivitiesDetailQuery")
	public String concertActivitiesDetailQuery(String limit,String start,String startTime,String stopTime,PageModel page,
			Integer ticketNumber,ConcertActivityVo concertActivityVo){
		Map<String, Object> resultMap=new HashMap<String,Object>();
		List<ConcertActivityVo> list=concertActivitiesService.concertActivitiesPage(concertActivityVo,startTime,stopTime,ticketNumber);
		/**显示条数*/
		page.setTotalCount(list.size());
		resultMap.put("rows", list);
		resultMap.put("total",page.getTotalCount() );
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	
	/**导出演唱会活动统计表*/
	@RequestMapping("outConcertDetailExcel")
	public void outconcertDetail(String limit,String start,String startTime,String stopTime,ConcertActivityVo concertActivityVo,
			Integer ticketNumber,HttpServletResponse response){
		/**获取数据源*/
		List<ConcertActivityVo> list=concertActivitiesService.concertActivitiesPage(concertActivityVo,startTime,stopTime,ticketNumber);
		String[] header=new String[]{"用户名","真实姓名","手机号","注册时间","认购金额","认购时间","认购产品","门票张数","注册渠道"};
		/**数据处理 */
		List<Map<String, String>> content=concertActivitiesService.handConcertDetail(list);
		//下载excel
		rechargeModel.downloadExcel("演唱会活动统计", null, header, content, response);
	}
	
	
}
