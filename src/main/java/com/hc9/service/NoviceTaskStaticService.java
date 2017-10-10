package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.activity.year2016.month05.HcNewerTaskCache;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
import com.hc9.model.report.NewerTaskVo;
import com.hc9.model.report.TaskDetailVo;

/** 新手任务查询服务层 */
@Service
public class NoviceTaskStaticService {
	@Resource
	private HibernateSupport dao;

	/** 通用标查询 */
	@Resource
	private LoanSignQuery loanSignQuery;

	/** 新手任务查询 */
	@SuppressWarnings("rawtypes")
	public List<NewerTaskVo> noviceTaskStatisticsPage(Integer taskCompleteSchedule, String beginTime, String endTime,
			PageModel page, String downLoadFlag, NewerTaskVo noviceTaskStatistics) {
		List<NewerTaskVo> resultList = new ArrayList<>();
		String beginDate = HcNewerTaskCache.getNewerTaskActivityBeginDate();
		String selectSql = "select u.id,u.userName,u.`name`,r.phone,u.createTime,u.isAuthIps,u.registerSource";
		String fromSql = " from userbasicsinfo u,userrelationinfo r where u.id = r.user_id and u.createTime >='" + beginDate + "' ";
		/** 按时间筛选 */
		fromSql = connection(beginTime, endTime, fromSql);
		/** 按电话或真实姓名筛选 */
		if (noviceTaskStatistics.getMobilePhone() != null && noviceTaskStatistics.getMobilePhone() != "") {
			String name = "";
			try {
				name = java.net.URLDecoder.decode(noviceTaskStatistics.getMobilePhone(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			fromSql += "  and (u.`name` like '%" + name + "%' or r.phone like '%"
					+ noviceTaskStatistics.getMobilePhone() + "%')";
		}
		
		String orderBy = "  order by u.createTime desc";
		String querySql = selectSql + fromSql + orderBy;
		List list = new ArrayList();
		if ("1".equals(downLoadFlag)) {
			list = dao.findBySql(querySql);
		} else {
			String countSql = "select count(u.id) " + fromSql;
			list = dao.pageListBySql(page, countSql, querySql, null);
		}
		
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] arr = (Object[]) obj;
				NewerTaskVo no = new NewerTaskVo();
				no.setId(StatisticsUtil.getLongFromBigInteger(arr[0]));
				no.setUserName(StatisticsUtil.getStringFromObject(arr[1]));
				no.setName(StatisticsUtil.getStringFromObject(arr[2]));
				no.setMobilePhone(StatisticsUtil.getStringFromObject(arr[3]));
				no.setCreateTime(StatisticsUtil.getStringFromObject(arr[4]));
				no.setIsAuthIps(StatisticsUtil.getIntegerFromObject(arr[5]));
				no.setRegisterSource(StatisticsUtil.getIntegerFromObject(arr[6]));
				String sql="select u.userName,u.`name`, u.department from generalize g, userbasicsinfo u where  g.uid=u.id and u.id=?";
				List li = dao.findBySql(sql, no.getId());
				if (li != null && li.size() > 0) {
					for (Object ob : li) {
						Object[] ar = (Object[]) ob;
						no.setRecommendUserName(StatisticsUtil.getStringFromObject(ar[0]));
						no.setRecommendName(StatisticsUtil.getStringFromObject(ar[1]));
						no.setDepartment(StatisticsUtil.getIntegerFromObject(ar[2]));
					}
				}
				if(StringUtil.isBlank(no.getRecommendName())) {
					no.setRecommendName("无");
				}
				if(StringUtil.isBlank(no.getRecommendUserName())){
				no.setRecommendUserName("无");
				}
				
				if (isUserFinishAllTask(no.getId())) {
					no.setTaskCompleteSchedule(1);
				} else {
					no.setTaskCompleteSchedule(2);
				}
				resultList.add(no);
			}
		}
		/** 按完成任务进度筛选 */
		resultList=ComplementTask(resultList, taskCompleteSchedule);
		return resultList;
	}

	/** 注册时间 */
	public String connection(String beginTime, String endTime, String fromSql) {
		if (beginTime != null && !"".equals(beginTime.trim())) {
			fromSql += " AND DATE_FORMAT(u.createTime, '%Y-%m-%d')>=DATE_FORMAT('" + beginTime + "', '%Y-%m-%d') ";
		}

		if (endTime != null && !"".equals(endTime.trim())) {
			fromSql += " AND DATE_FORMAT(u.createTime, '%Y-%m-%d')<=DATE_FORMAT('" + endTime + "', '%Y-%m-%d') ";
		}
		return fromSql;
	}

	/** 按完成任务进度筛选 */
	public List<NewerTaskVo> ComplementTask(List<NewerTaskVo> resultList, Integer task) {
		List<NewerTaskVo> list = new ArrayList<>();
		if (resultList != null && resultList.size() > 0) {
			if (task != null && task > 0) {
				for (int i = 0; i < resultList.size(); i++) {
					NewerTaskVo vo = resultList.get(i);
					boolean flag = false;
					int taskCompleteSchedule = vo.getTaskCompleteSchedule();
					if (taskCompleteSchedule == task.intValue()) {
						flag = true;
					}
					if (flag) {
						list.add(vo);
					}
				}
			} else {
				return resultList;
			}
		}
		return list;
	}

	/** 判断用户是否完成了所有任务 */
	public static boolean isUserFinishAllTask(long userId) {
		boolean flag = true;
		if (HcNewerTaskCache.isNewerRegisterInActivityArea(userId)) {
			/** 任务一是否已完成 */
			String key = "STR:HC9:TASK:ONE:RECEIVED:FLAG:" + userId;
			if (!RedisHelper.isKeyExist(key)) {
				flag = false;
			}

			/** 任务二是否需完成 */
			key = "STR:HC9:TASK:TWO:RECEIVED:FLAG:" + userId;
			if (!RedisHelper.isKeyExist(key)) {
				flag = false;
			}

			/** 任务三是否需完成 */
			key = "STR:HC9:TASK:THREE:RECEIVED:FLAG:" + userId;
			if (!RedisHelper.isKeyExist(key)) {
				flag = false;
			}

			/** 任务四是否需完成 */
			key = "STR:HC9:TASK:FOUR:RECEIVED:FLAG:" + userId;
			if (!RedisHelper.isKeyExist(key)) {
				flag = false;
			}

			/** 任务五是否需完成 */
			key = "STR:HC9:TASK:FIVE:RECEIVED:FLAG:" + userId;
			if (!RedisHelper.isKeyExist(key)) {
				flag = false;
			}

			/** 任务六是否需完成 */
			key = "STR:HC9:TASK:SIX:RECEIVED:FLAG:" + userId;
			if (!RedisHelper.isKeyExist(key)) {
				flag = false;
			}

			/** 终极大奖是否已完成 */
			key = "STR:HC9:BIG:PRIZE:FINAL:RED:KEY:" + userId;
			if (!RedisHelper.isKeyExist(key)) {
				flag = false;
			}
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 注册时间
	 * 
	 * @param beginDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return
	 */
	public String Connection(String beginTime, String endTime) {
		String sql = "";
		if (beginTime != null && !"".equals(beginTime.trim())) {
			sql = sql + " AND DATE_FORMAT(u.createTime, '%Y-%m-%d')>=DATE_FORMAT('" + beginTime + "', '%Y-%m-%d') ";
		}
		if (endTime != null && !"".equals(endTime.trim())) {
			sql = sql + " AND DATE_FORMAT(u.createTime, '%Y-%m-%d')<=DATE_FORMAT('" + endTime + "', '%Y-%m-%d') ";
		}
		return sql;
	}

	// 获取任务完成进度数据
	public List<TaskDetailVo> queryTasktailList(Long userId) {
		List<TaskDetailVo> resultList = new ArrayList<TaskDetailVo>();

		/** 任务一 */
		getTaskOneDetail(resultList, userId);

		/** 任务二 */
		getTaskTwoDetail(resultList, userId);

		/** 任务三 */
		getTaskThreeDetail(resultList, userId);

		/** 任务四 */
		getTaskFourDetail(resultList, userId);

		/** 任务五 */
		getTaskFiveDetail(resultList, userId);

		/** 任务六 */
		getTaskSixDetail(resultList, userId);

		/** 任务七 */
		getTaskSevenDetail(resultList, userId);
		return resultList;
	}

	/** 任务一是否完成 */
	private void getTaskOneDetail(List<TaskDetailVo> list, Long userId) {
		/** 任务一是否已完成 */
		String key = "STR:HC9:TASK:ONE:RECEIVED:FLAG:" + userId;
		if (RedisHelper.isKeyExist(key)) {
			TaskDetailVo taskDetail = new TaskDetailVo();
			taskDetail.setTaskProgress("完成注册");
			taskDetail.setCompleteTime(HcNewerTaskCache.getTaskOneReceiveTime(userId));
			taskDetail.setPrizeDetail("新手注册送100元红包");
			taskDetail.setReceiveStus("已领取");
			taskDetail.setReceiveTime(HcNewerTaskCache.getTaskOneCompleteTime(userId));
			if(taskDetail.getReceiveTime()!=null){
				taskDetail.setReceiveStus("已领取");
			}else{
				taskDetail.setReceiveStus("未领取");
			}
			list.add(taskDetail);
		}
	}

	/** 任务二是否完成 */
	private void getTaskTwoDetail(List<TaskDetailVo> list, Long userId) {
		/** 任务二是否需完成 */
		String key = "STR:HC9:TASK:TWO:RECEIVED:FLAG:" + userId;
		if (RedisHelper.isKeyExist(key)) {
			TaskDetailVo taskDetail = new TaskDetailVo();
			taskDetail.setTaskProgress("完成充值");
			taskDetail.setCompleteTime(HcNewerTaskCache.getTaskTwoReceiveTime(userId));
			taskDetail.setPrizeDetail("首次充值送5元红包");
			taskDetail.setReceiveTime(HcNewerTaskCache.getTaskTwoCompleteTime(userId));
			if(taskDetail.getReceiveTime()!=null){
				taskDetail.setReceiveStus("已领取");
			}else{
				taskDetail.setReceiveStus("未领取");
			}
			list.add(taskDetail);
		}
	}

	/** 任务三是否完成 */
	private void getTaskThreeDetail(List<TaskDetailVo> list, Long userId) {
		/** 任务三是否需完成 */
		String key = "STR:HC9:TASK:THREE:RECEIVED:FLAG:" + userId;
		if (RedisHelper.isKeyExist(key)) {
			TaskDetailVo taskDetail = new TaskDetailVo();
			taskDetail.setTaskProgress("完成认购100");
			taskDetail.setCompleteTime(HcNewerTaskCache.getTaskThreeReceiveTime(userId));
			taskDetail.setPrizeDetail("单笔认购100送15元红包");
			taskDetail.setReceiveTime(HcNewerTaskCache.getTaskThreeCompleteTime(userId));
			if(taskDetail.getReceiveTime()!=null){
				taskDetail.setReceiveStus("已领取");
			}else{
				taskDetail.setReceiveStus("未领取");
			}
			list.add(taskDetail);
		}
	}

	/** 任务四是否完成 */
	private void getTaskFourDetail(List<TaskDetailVo> list, Long userId) {
		/** 任务四是否需完成 */
		String key = "STR:HC9:TASK:FOUR:RECEIVED:FLAG:" + userId;
		if (RedisHelper.isKeyExist(key)) {
			TaskDetailVo taskDetail = new TaskDetailVo();
			taskDetail.setTaskProgress("完成认购1000");
			taskDetail.setCompleteTime(HcNewerTaskCache.getTaskFourReceiveTime(userId));
			taskDetail.setPrizeDetail("单笔认购满1000送30元红包");
			taskDetail.setReceiveTime(HcNewerTaskCache.getTaskFourCompleteTime(userId));
			if(taskDetail.getReceiveTime()!=null){
				taskDetail.setReceiveStus("已领取");
			}else{
				taskDetail.setReceiveStus("未领取");
			}
			list.add(taskDetail);
		}
	}

	/** 任务五是否完成 */
	private void getTaskFiveDetail(List<TaskDetailVo> list, Long userId) {
		/** 任务五是否需完成 */
		String key = "STR:HC9:TASK:FIVE:RECEIVED:FLAG:" + userId;
		if (RedisHelper.isKeyExist(key)) {
			TaskDetailVo taskDetail = new TaskDetailVo();
			taskDetail.setTaskProgress("完成认购3000");
			taskDetail.setCompleteTime(HcNewerTaskCache.getTaskFiveReceiveTime(userId));
			taskDetail.setPrizeDetail("单笔认购满3000送60元红包");
			taskDetail.setReceiveTime(HcNewerTaskCache.getTaskFiveCompleteTime(userId));
			if(taskDetail.getReceiveTime()!=null){
				taskDetail.setReceiveStus("已领取");
			}else{
				taskDetail.setReceiveStus("未领取");
			}
			list.add(taskDetail);
		}
	}

	/** 任务六是否完成 */
	private void getTaskSixDetail(List<TaskDetailVo> list, Long userId) {
		/** 任务六是否需完成 */
		String key = "STR:HC9:TASK:SIX:RECEIVED:FLAG:" + userId;
		if (RedisHelper.isKeyExist(key)) {
			TaskDetailVo taskDetail = new TaskDetailVo();
			taskDetail.setTaskProgress("完成微信关注");
			taskDetail.setCompleteTime(HcNewerTaskCache.getTaskSixReceiveTime(userId));
			taskDetail.setPrizeDetail("关注微信号送10元红包");
			taskDetail.setReceiveTime(HcNewerTaskCache.getTaskSixCompleteTime(userId));
			if(taskDetail.getReceiveTime()!=null){
				taskDetail.setReceiveStus("已领取");
			}else{
				taskDetail.setReceiveStus("未领取");
			}
			list.add(taskDetail);
		}
	}

	/** 任务七是否完成 */
	private void getTaskSevenDetail(List<TaskDetailVo> list, Long userId) {
		/** 任务七是否需完成 */
		String key = "STR:HC9:BIG:PRIZE:FINAL:RED:KEY:" + userId;
		if (RedisHelper.isKeyExist(key)) {
			TaskDetailVo taskDetail = new TaskDetailVo();
			taskDetail.setTaskProgress("领取终极大奖");
			taskDetail.setCompleteTime(HcNewerTaskCache.getTaskSevenReceiveTime(userId));
			taskDetail.setPrizeDetail("0.3%加息券1张,0.8%加息券1张,提现券1张,一对一VIP客户服务");
			taskDetail.setReceiveTime(HcNewerTaskCache.getTaskSevenCompleteTime(userId));
			if(taskDetail.getReceiveTime()!=null){
				taskDetail.setReceiveStus("已领取");
			}else{
				taskDetail.setReceiveStus("未领取");
			}
			list.add(taskDetail);
		}
	}

	/** 处理新手任务下载数据 */
	public List<Map<String, String>> handNewerTask(List<NewerTaskVo> list) {
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		if (list != null && list.size() > 0) {
			for (NewerTaskVo vo : list) {
				Map<String, String> newerTask = new HashMap<String, String>();
				newerTask.put("用户ID", vo.getId() + "");
				newerTask.put("用户名", vo.getUserName());
				newerTask.put("真实姓名", vo.getName());
				newerTask.put("手机号", vo.getMobilePhone());
				newerTask.put("注册时间", vo.getCreateTime());
				int isAuthIps = vo.getIsAuthIps();
				String isAuthIpsName = "暂无";
				if (isAuthIps == 0) {
					isAuthIpsName = "待确认";
				} else if (isAuthIps == 1) {
					isAuthIpsName = "是";
				} else if (isAuthIps == -1) {
					isAuthIpsName = "失败";
				}
				newerTask.put("宝付授权状态", isAuthIpsName);
				newerTask.put("推荐人用户名", vo.getRecommendUserName());
				newerTask.put("推荐人真实姓名", vo.getName());
				Integer department = vo.getDepartment();
				String departmentName = "暂无";
				if(department==null){
					departmentName = "暂无";
				}else if (department == 1) {
					departmentName = "总裁办";
				} else if (department == 2) {
					departmentName = "财务部";
				} else if (department == 3) {
					departmentName = "行政部";
				} else if (department == 4) {
					departmentName = "副总办";
				} else if (department == 5) {
					departmentName = "运营中心";
				} else if (department == 6) {
					departmentName = "培训部";
				} else if (department == 7) {
					departmentName = "风控部";
				} else if (department == 8) {
					departmentName = "IT部";
				} else if (department == 9) {
					departmentName = "摄影部";
				} else if (department == 10) {
					departmentName = "推广部";
				} else if (department == 11) {
					departmentName = "项目部";
				} else if (department == 12) {
					departmentName = "客服部";
				} else if (department == 13) {
					departmentName = "事业一部";
				} else if (department == 14) {
					departmentName = "事业二部";
				} else if (department == 15) {
					departmentName = " 离职员工";
				}
				newerTask.put("推荐人部门", departmentName);
				int registerSource = vo.getRegisterSource();
				String registerSourceName = "暂无";
				if (registerSource == 1) {
					registerSourceName = "PC";
				} else if (registerSource == 2) {
					registerSourceName = "H5";
				} else if (registerSource == 8) {
					registerSourceName = "android";
				} else if (registerSource == 9) {
					registerSourceName = "ios";
				}
				newerTask.put("来源", registerSourceName);
				int taskCompleteSchedule = vo.getTaskCompleteSchedule();
				String taskCompleteName = "无";
				if (taskCompleteSchedule == 1) {
					taskCompleteName = "已完成";
				} else if (taskCompleteSchedule == 2) {
					taskCompleteName = "未完成";
				}
				newerTask.put("任务完成进度", taskCompleteName);
				content.add(newerTask);
			}
		}
		return content;
	}

	/** 组装分页模型对象 */
	public static PageModel comsitePageModel(String start, String limit) {
		PageModel page = new PageModel();
		// 每页显示条数
		if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
			page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer.parseInt(limit) : 20);
		} else {
			page.setNumPerPage(20);
		}
		// 计算当前页
		if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
			page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
		}
		return page;
	}
}
