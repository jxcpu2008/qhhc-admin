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
import com.hc9.model.report.NewerTaskVo;
import com.hc9.model.report.TaskDetailVo;
import com.hc9.service.NoviceTaskStaticService;
import com.hc9.service.activity.year2016.Month05ActivityService;

/** 新手任务统计表查询 */
@Controller
@RequestMapping(value = { "/noviceTaskStatistics"})
@CheckLogin(value=CheckLogin.ADMIN)
public class NoviceTaskStatisticsControll {
	/** 新手任务统计表查询服务层 */
	@Resource
	private NoviceTaskStaticService noviceTaskService;
	/** Excel导出 */
	@Resource
	private RechargeModel rechargeModel;
	
	@Resource
	private Month05ActivityService month05ActivityService;
	

	@RequestMapping(value = { "noviceTaskStatisticsDetail", "/" })
	public ModelAndView noviceTaskStatisticsDetail() {
		ModelAndView noticeTaskview = new ModelAndView("WEB-INF/views/admin/quickquery/noviceTaskStatistics");
		return noticeTaskview;
	}

	/**新手任务统计查询 */
	@ResponseBody
	@RequestMapping("noviceTaskStatisticsQuery")
	public String noviceTaskStatisticsQuery(String limit, String start, String beginTime, String endTime,Integer taskCompleteSchedule,
			NewerTaskVo noviceTaskStatistics) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		/** 获取数据源 */
		PageModel page = NoviceTaskStaticService.comsitePageModel(start, limit);
		List<NewerTaskVo> list = noviceTaskService.noviceTaskStatisticsPage(taskCompleteSchedule,beginTime, endTime, page, null,
				noviceTaskStatistics);
		resultMap.put("rows", list);
		resultMap.put("total", page.getTotalCount());
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	
	/**任务完成进度查询 */
	@ResponseBody
	@RequestMapping(value = { "taskTailList", "/" })
	public String taskTailList(Long id,PageModel page) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 获取数据源
		List<TaskDetailVo> list = noviceTaskService.queryTasktailList(id);
		/**显示条数*/
		page.setTotalCount(list.size());
		resultMap.put("rows", list);// 5月榜单明细列表
		resultMap.put("total",  page.getTotalCount());
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	
	/**导出新手任务表*/
	@RequestMapping("outNoviceTaskExcel")
	public void outNoviceTaskExcel(Integer taskCompleteSchedule, String beginTime, String endTime,
			NewerTaskVo noviceTaskStatistics,HttpServletResponse response){
		//获取数据源
		List<NewerTaskVo>  list=noviceTaskService.noviceTaskStatisticsPage(taskCompleteSchedule,beginTime, endTime, null,"1",noviceTaskStatistics);
		String[] header=new String[]{"用户ID","用户名","真实姓名","手机号","注册时间","宝付授权状态","推荐人用户名","推荐人真实姓名","推荐人部门","来源","任务完成进度"};
		List<Map<String, String>> content=noviceTaskService.handNewerTask(list);
		//下载excel
		rechargeModel.downloadExcel("5月榜单统计", null, header, content, response);
	}
}
