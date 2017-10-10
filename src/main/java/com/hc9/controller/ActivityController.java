package com.hc9.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.json.JsonUtil;
import com.hc9.common.redis.HcPeachActivitiCache;
import com.hc9.common.redis.HcSeptemberActivitiCache;
import com.hc9.common.redis.IndexDataCache;
import com.hc9.common.redis.QiXiActivitiCache;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.RedisUtil;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.GenerateLinkUtils;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.LotteryRank;
import com.hc9.model.PageModel;
import com.hc9.model.VoteRecordVo;
import com.hc9.service.ActivityService;
import com.hc9.service.BonusService;
import com.hc9.service.HcMonkeyActivitiCache;
import com.hc9.service.LotteryService;
import com.hc9.service.VisitorService;

/** 活动相关请求入口类 */
@RequestMapping({ "activity", "/" })
@Controller
@CheckLogin(value=CheckLogin.ADMIN)
public class ActivityController {
	
	@Resource
	private ActivityService activityService;
	
	@Resource
	private BonusService bonusService;
	
	@Resource
	private LotteryService lotteryService;
	
	@Resource
	private VisitorService visitorservice;
	
	/** 什么是众持页面 */
//	@RequestMapping("/whatIsHold.htm")
	public String whatHold(HttpServletRequest request) {
		return "WEB-INF/views/hc9/activity/whatis";
	}

	/** 红筹学堂  */
//	@RequestMapping("/hcschool.htm")
	public String hcschool(HttpServletRequest request, String tab) {
		return "WEB-INF/views/hc9/hcschool/hcschool";
	}
	
	/** 小九带你游红筹 */
//	@RequestMapping("/travelHc9.htm")
	public String toTravel(HttpServletRequest request) {
		return "WEB-INF/views/hc9/activity/travel";
	}
	
	/** 活动 */
//	@RequestMapping("/toOpenActivity.htm")
	public String toOpenActivity(HttpServletRequest request) {
		List invest = bonusService.getInvest("2015-06-01", "2015-06-15");
		List regist = bonusService.getRegist();
		request.setAttribute("regist", regist);
		request.setAttribute("invest", invest);
		return "WEB-INF/views/hc9/activity/sixMonth";
	}

	/** 6月活动 */
//	@RequestMapping("/june.htm")
	public String toNewActivity(HttpServletRequest request) {
		Userbasicsinfo userbasic = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
		if (userbasic != null) {
			String promoteNo = "";
			int flag = userbasic.getUserType();
			if (flag == 1) {
				promoteNo = userbasic.getId().toString();
			} else if (flag == 2) {
				promoteNo = userbasic.getStaffNo();
			}
			request.getSession().setAttribute("promoteLikn",
					GenerateLinkUtils.getServiceHostnew(request) + "visitor/to-regist?member=" + promoteNo);
			request.setAttribute("promoteNo", promoteNo);
		}
		return "WEB-INF/views/hc9/activity/june";
	}
	
	/** 8月8号至8月31日活动相关  */
	@RequestMapping("/august.htm")
	public String augustActivity(HttpServletRequest request, String tab) {
		return "WEB-INF/views/hc9/activity/august";
	}
	
	/** 进入抽奖页面 */
	@RequestMapping("/july.htm")
	public String toOpenJuly(HttpServletRequest request) {
		Userbasicsinfo userbasic = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
		/** 投资排行榜相关数据 */
		List<LotteryRank> loanList = visitorservice.queryInvestRankList();
		
		/** 推荐排行榜 */
		List<LotteryRank> recommendInvestList = visitorservice.queryRecommendRankList();
		
		/** 实物奖品 */
		List<LotteryRank> prizeDetail = visitorservice.queryMaterialLotteryRecord();
		
		/** 红包奖品 */
		List<LotteryRank> redEnvel = visitorservice.queryRedEnvelopeLotterRecord();
		
		request.setAttribute("loanList", loanList);
		request.setAttribute("recommendInvestList", recommendInvestList);
		request.setAttribute("prizeDetail", prizeDetail);
		request.setAttribute("redEnvel", redEnvel);
		if(userbasic != null) {
			request.setAttribute("lotteryNum", RedisUtil.getLotteryChanceNumOfUser(userbasic.getId()));
		} else {
			request.setAttribute("lotteryNum", "0");
		}
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if ("userName".equals(cookie.getName())) {
					request.setAttribute("userName", cookie.getValue());
				}
			}
		}
		return "WEB-INF/views/hc9/activity/luckyDraw";
	}

	@RequestMapping("/toluckyDraw.htm")
	@ResponseBody
	public JSONObject luckyDraw(HttpServletRequest request) {
		Userbasicsinfo userbasic = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
		int prizeId = getLotteryResult(userbasic.getId());
		
		Object[][] prizeArr = new Object[][] {
				// id,min,max，prize【奖项】
				{ 0, 250, 265, "未中奖" }, { 1, 320, 335, "运动相机" },
				{ 2, 175, 195, "体脂秤" }, { 3, 95, 115, "冰箱卫士" },
				{ 4, 285, 300, "￥5红包" }, { 5, 215, 230, "￥7红包" },
				{ 6, -10, 10, "￥9红包" }, { 7, 60, 80, "￥19红包" },
				{ 8, 25, 45, "￥29红包" }, { 9, 135, 155, "￥50红包" } };
		JSONObject json = new JSONObject();
		json.put("prize", prizeId);
		json.put("lotteryNum", RedisUtil.getLotteryChanceNumOfUser(userbasic.getId()));
		if (prizeId >= 0) {
			int angle = new Random().nextInt((Integer) prizeArr[prizeId][2]
					- (Integer) prizeArr[prizeId][1])
					+ (Integer) prizeArr[prizeId][1];
			json.put("angle", angle);
			json.put("msg", prizeArr[prizeId][3]);
		}

		return json;
	}
	
	/** 获取抽奖结果 */
	private int getLotteryResult(Long userId) {
		int prizeId = 0;
		try {
			Date now = new Date();
			if(RedisUtil.validCurrentDate(now) >= 0) {
				String currentTime = DateFormatUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss");
				String currentDate = currentTime.substring(0, 10);
				/** 记录用户抽奖次数 */
				RedisUtil.increaseUserLotteryNum(userId, currentDate);
				/** 休息时间不发放实物奖励 */
				int userLotteryNum = RedisUtil.getUserLotteryNum(userId, currentDate);
				int grantChanceNum = RedisUtil.getGrantLotteryChance(userId);
				if(Integer.valueOf(userLotteryNum) > 3 && grantChanceNum <= 0) {
					prizeId = -3;
				} else {
					if(Integer.valueOf(userLotteryNum) > 3) {
						RedisUtil.decreaseLotteryChance(userId);
					}
					prizeId = lotteryService.getLotteryResult(now, userId);
				}
			}
		} catch(Exception e) {
			LOG.error(userId + "抽奖错误!", e);
		}
		return prizeId;
	}

	/** 加息券列表 */
	public String plusInterest(HttpServletRequest request,Integer no) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute("session_user");
		PageModel page = new PageModel();
		if (no == null) {
			page.setPageNum(1);
		} else {
			page.setPageNum(no);
		}
		page.setNumPerPage(15);
		page = activityService.getPlusInterestList(page, user.getId());
		request.setAttribute("page", page);
		return "/WEB-INF/views/hc9/member/redenvelope_list";
	}
	
	/** 进入七夕活动页面 */
	@RequestMapping("/tosevenmoon.htm")
	public String toOpenActive(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if ("userName".equals(cookie.getName())) {
					request.setAttribute("userName", cookie.getValue());
				}
			}
		}
		String sessionId = request.getSession().getId();
		String key = "STR:HC9:QIXI:VOTE:USER:SESSIONID:" + sessionId;
		if(!RedisHelper.isKeyExistSetWithExpire(key, 1)) {//1秒钟之内不容许连续访问
			Userbasicsinfo userbasic = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
			if(userbasic != null) {
				/** 投票人剩余的投票次数 */
				request.setAttribute("voteNum", QiXiActivitiCache.getVoteChanceNumOfUser(userbasic.getId()));
			} else {
				request.setAttribute("voteNum", "0");
			}
			request.setAttribute("sigiNum", QiXiActivitiCache.getVoteNumByVotedId("sigi"));//sigi所获得的投票数
			request.setAttribute("kelvinNum", QiXiActivitiCache.getVoteNumByVotedId("kelvin"));//kelvin所获得的投票数
			request.setAttribute("jessicaNum", QiXiActivitiCache.getVoteNumByVotedId("jessica"));//jessica所获得的投票数
			request.setAttribute("japserNum", QiXiActivitiCache.getVoteNumByVotedId("japser"));//japser所获得的投票数
			request.setAttribute("elenaNum", QiXiActivitiCache.getVoteNumByVotedId("elena"));//elena所获得的投票数
			return "/WEB-INF/views/hc9/activity/tanabata";
		} else {
			return "/WEB-INF/views/tooFrequent";
		}
	}
	
	/** 为某人投票接口
	 *  @param votedId 被投票的唯一表示id，目前以英文姓名标识
	 *   */
	@RequestMapping("/voteForSomeBody.htm")
	@ResponseBody
	public String voteForSomeBody(String votedId, HttpServletRequest request) {
		Map<String, String> resultMap = new HashMap<String, String>();
		if(QiXiActivitiCache.isQiXiActivity(new Date())) {
			resultMap = activityService.voteForSomeBody(votedId, request);
		} else {
			resultMap.put("code", "-1");
			resultMap.put("msg", "当前时间不在活动期间内！");
		}
		Userbasicsinfo userbasic = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
		String voteNum = "0";
		if(userbasic != null) {
			/** 投票人剩余的投票次数 */
			voteNum = "" + QiXiActivitiCache.getVoteChanceNumOfUser(userbasic.getId());
		}
		resultMap.put("sigiNum", QiXiActivitiCache.getVoteNumByVotedId("sigi"));//sigi所获得的投票数
		resultMap.put("kelvinNum", QiXiActivitiCache.getVoteNumByVotedId("kelvin"));//kelvin所获得的投票数
		resultMap.put("jessicaNum", QiXiActivitiCache.getVoteNumByVotedId("jessica"));//jessica所获得的投票数
		resultMap.put("japserNum", QiXiActivitiCache.getVoteNumByVotedId("japser"));//japser所获得的投票数
		resultMap.put("elenaNum", QiXiActivitiCache.getVoteNumByVotedId("elena"));//elena所获得的投票数
		resultMap.put("voteNum", voteNum);//elena所获得的投票数
		String jsonStr = JsonUtil.toJsonStr(resultMap);
		return jsonStr;
	}
	
	/** 七夕活动后台管理页面 */
	@RequestMapping("/toSevenMoonActivityList.htm")
	public String toSevenMoonActivityList() {
		return "/WEB-INF/views/admin/activity/sevenMoonActivityList";
	}
	
	/** 分页查询七夕活动相关投票人列表 */
	@ResponseBody
	@RequestMapping("/querySevenMoonList.htm")
	public String querySevenMoonList(String start, String limit, String votedId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		PageModel page = StatisticsUtil.comsitePageModel(start, limit);

		List<VoteRecordVo> voteRecordList = activityService.querySevenMoonList(page, votedId);
		resultMap.put("rows", voteRecordList);// 短信邮件模板列表
		resultMap.put("total", page.getTotalCount());// 总注册量
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	
	/** 更新投票相关统计信息 */
	@ResponseBody
	@RequestMapping("/updateVoteStatisticsInfo.htm")
	public String updateVoteStatisticsInfo() {
		Map<String, Object> resultMap = activityService.updateVoteStatisticsInfo();
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	
	/** 查看排行榜 */
	@RequestMapping("/viewVoteRankList.htm")
	public String viewVoteRankList(HttpServletRequest request) {
		List<VoteRecordVo> voteRecordList = activityService.queryVoteRankList();
		request.setAttribute("voteRecordList", voteRecordList);
		return "/WEB-INF/views/admin/activity/voteRankListDetail";
	}
	
	/** 七夕活动利息项目项目汇总 */
	@ResponseBody
	@RequestMapping("/voteWinIncomeSummary.htm")
	public String voteWinIncomeSummary() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap = activityService.voteWinIncomeSummary();
		} catch(Exception e) {
			LOG.error("更新投票人活动收益出现异常！", e);
			resultMap.put("code", "-1");
			resultMap.put("code", "后台异常，请联系管理员！");
		}
		String resultStr = JsonUtil.toJsonStr(resultMap);
		return resultStr;
	}
	
	/** 前海红筹周年庆活动页面 */
	@RequestMapping("/lotterySept.htm")
	public String hc9oneyear(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession().getAttribute("session_user");
		if(user != null) {
			request.setAttribute("lotteryNum", HcSeptemberActivitiCache.getLotteryChanceNumOfUser(user.getId()));
		} else {
			request.setAttribute("lotteryNum", "0");
		}
		/** 查询中奖列表相关数据 */
		List<LotteryRank> lotteryList = activityService.queryLotteryRankList();
		request.setAttribute("lotteryList", lotteryList);
		return "/WEB-INF/views/hc9/activity/lotterySept";
	}
	
	/** 前海红筹周年庆幸运轮抽奖 */
	@RequestMapping("/oneYearLuckyDraw.htm")
	@ResponseBody
	public JSONObject oneYearLuckyDraw(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
		/**
		 * 奖品信息：1、往返机票(1)；2、月饼（价值75元）（20）；3、乐心血压计（5）；4、萤石智能家居套装（5）；5、麦开水杯（5）；6、体脂称（5）；
		   7、5元红包（不限量，不中其他奖时必中）；8、10元红包（100）
		   0:系统后台异常；-1:抽奖活动尚未开始; -2:抽奖活动已经结束;-3:无抽奖机会;-4:没登录;-5:首次抽奖且没有抽奖次数
		 * */
		JSONObject json = new JSONObject();
		int prizeId = 0;
		int lotteryNum = 0;
		try {
			if(user != null) {
				Integer isFristLottery = activityService.isFirstLottery(user.getId());
				lotteryNum = HcSeptemberActivitiCache.getLotteryChanceNumOfUser(user.getId());
				if (isFristLottery <= 0 && lotteryNum == 0) {
					prizeId = -5;  // 表示首次抽奖且没有抽奖次数
				} else {
					prizeId = getOneYearLotteryResult(user.getId());
				}
			}else{
				prizeId = -4;  // 表示没登录
			}
		} catch(Exception e) {
			LOG.error("抽奖后台报错！", e);
		}
		json.put("prize", prizeId);
		json.put("lotteryNum", lotteryNum);
		return json;
	}

	/** 获取周年庆抽奖结果 */
	private int getOneYearLotteryResult(Long userId) {
		int prizeId = 0;
		try {
			Date now = new Date();
			prizeId = HcSeptemberActivitiCache.validCurrentDate(now);
			if(prizeId >= 0) {
				String currentTime = DateFormatUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss");
				String currentDate = currentTime.substring(0, 10);
				/** 休息时间不发放实物奖励 */
				int userLotteryNum = HcSeptemberActivitiCache.getLotteryChanceNumOfUser(userId);
				if(userLotteryNum <= 0) {
					prizeId = -3;
				} else {
					prizeId = activityService.getLotteryResult(now, userId);
					if(prizeId > 0) {
						int tempNum = HcSeptemberActivitiCache.getTemporaryLotteryChance(userId, currentDate);
						if(tempNum > 0) {
							HcSeptemberActivitiCache.decreaseTemporaryLotteryChance(userId, currentDate);
						} else {
							HcSeptemberActivitiCache.decreasePermanentLotteryChance(userId);
						}
					}
				}
			}
		} catch(Exception e) {
			LOG.error(userId + "抽奖错误!", e);
		}
		return prizeId;
	}

	/** 
	 * 新春猴给力活动页
	 * */
	@RequestMapping("/monkeyActivity.htm")
	public String monkeyActivity(HttpServletRequest request) {
		int week = HcMonkeyActivitiCache.week() + 1;
		List<Map<String,String>> list = IndexDataCache.getList("NEWYEAR:INVEST:MONKEY:TOTAL");
		if (week == 7) {
			Object obj = activityService.queryHongChouEredarTopThree();
			request.setAttribute("eredar", obj);
		}
		request.setAttribute("week", week);
		request.setAttribute("eredarList", list);
		return "/WEB-INF/views/hc9/activity/monkeyActivity";
	}
	
	@RequestMapping("/monkeyData.htm")
	public String monkeyData(HttpServletRequest request,String weekNum,String week) {
		String listKey = "NEWYEAR:INVEST:MONKEY:WEEK:" + weekNum;
		List<Map<String,String>> list = IndexDataCache.getList(listKey);
		request.setAttribute("list", list);
		request.setAttribute("week", week);
		request.setAttribute("weekNum", weekNum);
		return "/WEB-INF/views/hc9/activity/monkeyActivityData";
	}
	
	/** 
	 * “红筹理财师”活动页
	 * */
	@RequestMapping("/hcPlannerActivity.htm")
	public String plannerActivity(HttpServletRequest request) {
		String plannerKey = "NEWYEAR:INVEST:FINANCIAL:LIST";
		List<Map<String,String>> list = IndexDataCache.getList(plannerKey);
		request.setAttribute("recommendTenders", list);
		return "/WEB-INF/views/hc9/activity/redChipPlannerActivity";
	}
	
	/** 
	 * “春风迎三月，金桃朵朵开”活动页
	 * */
	@RequestMapping("/peachActivity.htm")
	public String peack(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession().getAttribute("session_user");
		List<Object[]> billBoards = null;  // 获取推荐排行榜
		Integer lotteryNum = 0;  // 剩余抽奖次数
		Integer billNum = 0; // 排名
		Integer reffCount = 0; // 累计推荐人数
		billBoards = activityService.queryGoldPeachBillBoard(null);
		if (user != null && billBoards != null) {
			// 获取金桃抽奖次数
			lotteryNum = HcPeachActivitiCache.getPermanentLotteryChance(user.getId());
			// 获取当前用户的“排名”以及“累计推荐注册人数”
			Object[] personalBillBoard = activityService.queryGoldPeachBillBoard(user.getId()).get(0);
			for (Object[] board : billBoards) {
				if (personalBillBoard[0] != null) {
					billNum++;
					if (board[0].equals(personalBillBoard[0])) {
						reffCount = Integer.valueOf(personalBillBoard[1].toString());
						break;
					}
				}
			}
		}
		request.setAttribute("goldPeachBillBoards", billBoards);
		request.setAttribute("billNum", billNum);
		request.setAttribute("lotteryNum", lotteryNum);
		request.setAttribute("reffCount", reffCount);
		return "/WEB-INF/views/hc9/activity/goldPeachActivity";
	}
	/**
	 * 金桃抽奖
	 * @param request
	 * code : 0、抽奖成功-1、活动未开始或已结束-2、抽奖机会用完
	 * flagMsg ： 0、表示自己注册时送的抽奖次数1、表示好友注册时送的抽奖次数
	 * @return
	 */
	@RequestMapping("/lotteryPeach.htm")
	@ResponseBody
	public String lotteryPeach(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession().getAttribute("session_user");
		Map<String, String> resultMap = new HashMap<String, String>();
		if (user != null) {
			try {
				if (HcPeachActivitiCache.validCurrentDate(new Date()) == 0) {
					Long userId = user.getId();
					Integer lotteryNum = HcPeachActivitiCache.getPermanentLotteryChance(userId);
					if (lotteryNum > 0) {
						ActivityMonkey goldPech = activityService.getLotteryPech(userId);
						if (goldPech != null) {
							String createTime = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
							activityService.updateLotteryPech(goldPech.getId(),createTime); 
							lotteryNum = HcPeachActivitiCache.getPermanentLotteryChance(userId);
							String idStr = goldPech.getUserId().toString();
							String byIdStr = goldPech.getByUser().getId().toString();
							if (idStr.equals(byIdStr)) {
								resultMap.put("flagMsg", "0");
							} else {
								resultMap.put("flagMsg", "1");
							}
							HcPeachActivitiCache.decreasePermanentLotteryChance(userId);
							resultMap.put("code", "0");  // 抽奖成功
						}
						resultMap.put("num", lotteryNum.toString());
					} else {
						resultMap.put("code", "-2");  // 抽奖机会用完
					}
				} else {
					resultMap.put("code", "-1"); // 活动未开始或已结束
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("金桃抽奖出现问题："+e.getMessage());
				resultMap.put("code", "-3");
			}
		}
		String jsonStr = JsonUtil.toJsonStr(resultMap);
		return jsonStr;
	}

	/** 嗒嗒巴士春节抢票banner活动介绍页  */
	@RequestMapping("/taptapActivity.htm")
	public String taptapActivity(HttpServletRequest request) {
		return "WEB-INF/views/hc9/activity/tapTapActivity";
	}
	
	/**
	 * 街拍活动页
	 * */
	@RequestMapping("/streetBeatActivity.htm")
	public String streetBeatActivity(HttpServletRequest request) {
		return "WEB-INF/views/hc9/activity/streetBeat";
	}
}