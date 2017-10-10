package com.hc9.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLoginOnMethod;
import com.hc9.common.constant.Constant;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.activity.year2016.month04.HcOpenCardActivityCache;
import com.hc9.commons.log.LOG;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.InvestLoanRecordVo;
import com.hc9.service.BaoFuInvestService;
import com.hc9.service.BaoFuLoansignService;
import com.hc9.service.LoanInfoService;
import com.hc9.service.LoanSignQuery;
import com.hc9.service.MemberCenterService;
import com.hc9.service.activity.ActivityAllInOneService;

import freemarker.template.TemplateException;

/**
 * 用户购标
 * 
 * @author RanQiBing 2014-04-10
 * 
 */
@Controller
@RequestMapping("/plank")
public class PlankController {
	@Resource
	private BaoFuLoansignService baoFuLoansignService;
	
	@Resource
	private LoanSignQuery loanSignQuery;

	@Resource
	private LoanInfoService loanInfoService;
	
	@Resource
	private MemberCenterService memberCenterService;
	
	@Resource
	private BaoFuInvestService baoFuInvestService;

	@Resource
	private ActivityAllInOneService activityAllInOneService;
	
	/**
	 * 
	 * @param loanId
	 *            标id
	 * @param money
	 *            投标金额
	 * @type 1-店铺 2-项目 3-天标 4-债权转让
	 * @param subType
	 *            投标类型: 1-优先，2-夹层，3-列后 4-vip众筹，5-股东众筹，
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getLoaninfo.htm")
	@ResponseBody
	@CheckLoginOnMethod
	public synchronized String getLoanInfo(Long loanId, Double money,Integer subType, HttpServletRequest request,HttpServletResponse response) {
		return baoFuLoansignService.getLoanInfoService(loanId, money, subType, request, response);
	}

	/****
	 * 后端项目投标查询
	 * 
	 * @param request
	 * @param id
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("ipsLoanInfo.htm")
	public synchronized String ipsLoanInfo(HttpServletRequest request,HttpServletResponse response, String id) throws Exception {
		String concurrentLock = "STR:HC9:IPS:LOAN:QUERY:" + id;
		if(!RedisHelper.isKeyExistSetWithExpire(concurrentLock, 180)) {
			try {
				String result = queryLoanRecordStatus(id);
				RedisHelper.del(concurrentLock);
				return result;
			} catch(Exception e) {
				RedisHelper.del(concurrentLock);
				return "2";
			}
		} else {
			return "5";
		}
	}

	/***
	 * 前端项目投标查询
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@CheckLoginOnMethod
	@RequestMapping("ipsPostLoanInfo.htm")
	public synchronized String ipsPostLoanInfo(HttpServletRequest request, String id) {
		try {
			return queryLoanRecordStatus(id);
		} catch(Exception e) {
			LOG.error("投资记录查询过程中报错！", e);
			return "2";
		}
	}

	/** 投资记录查询方法 
	 * @throws Exception */
	private String queryLoanRecordStatus(String loanRecordId) throws Exception {
		// 查询购买记录
		Loanrecord loanRecord = loanSignQuery.getLoanRecord(loanRecordId);
		if(loanRecord.getIsSucceed()==1){
			return "1";
		}
		String result = "userLockFail";
		Long userId = loanRecord.getUserbasicsinfo().getId();
		/** 同一个用户90秒内不能并发投资 */
		String userIdConcurrentLock = "STR:INVEST:USER:CONCURRENT:LOCK:" + userId;
		if(!RedisHelper.isKeyExistSetWithExpire(userIdConcurrentLock, 90)) {
			/** 同一个项目用户60秒内不能并发投资 */
			 int seconds = 60;
			 Long loanId = loanRecord.getLoansign().getId();
			 String loanIdConcurrentLock = "STR:INVEST:LOANID:CONCURRENT:LOCK:" + loanId;
			 boolean lockFlag = RedisHelper.isKeyExistSetWithExpire(loanIdConcurrentLock, seconds);
			 for(int i = 0; i < 16; i++) {
				 if(!lockFlag) {//如果没被其他用户锁住，则获取锁，跳出循环，否则，循环获取锁16次
					 LOG.error("用户" + userId + "在标" + loanId + "投资记录" + loanRecordId + "查询过程中获取锁成功，当前为第" + (i + 1) + "次！");
					 break;
				 } else {
					 LOG.error("用户" + userId + "在标" + loanId + "投资记录" + loanRecordId + "查询过程中获取锁失败，当前为第" + i + "次！");
					 try {
						 Thread.sleep(500);
					 } catch (InterruptedException e) {
						 LOG.error("用户" + userId + "在标" + loanId + "投资记录" + loanRecordId + "查询过程中出现异常！", e);
					 }
					 lockFlag = RedisHelper.isKeyExistSetWithExpire(loanIdConcurrentLock, seconds);
				 }
			 }
			 if(!lockFlag) {
				 result = baoFuLoansignService.ipsLoanInfoLoanHandle(loanRecord);
				 RedisHelper.del(loanIdConcurrentLock);
				 baoFuLoansignService.updateRedisInfoAfterInvest(userId, loanId);
			 } else {
				 result = "loanLockFail";
			 }
			 RedisHelper.del(userIdConcurrentLock);
		}
		return result;
	}
	
	@RequestMapping("/checkCode")
	@ResponseBody
	public boolean checkCode(HttpServletRequest request,String input) {
		// 取验证码
		String validate = (String) request.getSession().getAttribute("user_tend");
		if (input.equalsIgnoreCase(validate)) {
			return true;
		} else {
			return false;
		}
	}

	
	/**
	 * 支付页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/toLoanPay.htm")
	@ResponseBody
	public String toLoanPay(HttpServletRequest request, Long loanId,
			Double money, Integer subType) {
		Map<String, String> loanmap = new HashMap<String, String>();
		loanmap.put("money", String.valueOf(money));
		loanmap.put("subType", subType.toString());
		loanmap.put("loanId", loanId.toString());
		request.getSession().setAttribute("loanMap", loanmap);
		return "1";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/toLoadLoanPay.htm")
	public String toLoadLoanPay(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
		Map<String, String> loanmap = (Map<String, String>) request.getSession().getAttribute("loanMap");
		Loansign loan = loanSignQuery.getLoansignById(loanmap.get("loanId"));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		request.setAttribute("time", df.format(new Date()));
		request.setAttribute("user", user);
		request.setAttribute("money", loanmap.get("money"));
		request.setAttribute("subType", loanmap.get("subType"));
		request.setAttribute("loan", loan);
		return "WEB-INF/views/member/loan/loanpay";
	}
	
	/** 翻牌抽奖活动处理送翻牌次数问题:第5笔优先和夹层的问题 */
	public void handleOpenCardFifthRecord(Loansign loan, List<InvestLoanRecordVo> loanRecordList) {
		if(HcOpenCardActivityCache.isOpenCardActivity(new Date())) {
			int loanType = loan.getType();
			int remonth = loan.getRemonth();
			boolean activityFlag = true;
			if(loanType == 3) {
				if(remonth < 25) {
					activityFlag = false;
				}
			}
			if(loanRecordList != null && loanRecordList.size() > 1 && activityFlag) {
				long userId = loanRecordList.get(0).getLoanrecord().getUserbasicsinfo().getId();
				long totalInvestNum = HcOpenCardActivityCache.getTotalInvestNum(userId);
				if(totalInvestNum == 4) {
					/** 获取优先金额和优先记录id */
					double priTenderMoney = 0;
					long midLoanRecordId = 0;
					double midTenderMoney = 0;
					
					/** 是否使用红包和加息券  */
					boolean isRed = false;
					for(InvestLoanRecordVo vo : loanRecordList) {
						/*** 投标类型: 1-优先，2-夹层，3-列后 4-vip众筹，5-股东众筹 */
						if(vo.getIncreaseCard() != null || vo.getRedEnvelopeDetail() != null) {
							isRed = true;
						}
						if(1 == vo.getLoanrecord().getSubType()) {
							priTenderMoney = vo.getLoanrecord().getTenderMoney();
						} else if(2 == vo.getLoanrecord().getSubType()) {
							midLoanRecordId = vo.getLoanrecord().getId();
							midTenderMoney = vo.getLoanrecord().getTenderMoney();
						}
					}
					
					if(priTenderMoney > 0 && midTenderMoney > 0) {
						if(midTenderMoney > priTenderMoney || isRed) {
							String moneyKey = "STR:HC9:OPEN:CARD:FIFTH:RECORD:MONEY:" + userId;
							RedisHelper.set(moneyKey, "" + midTenderMoney);
							RedisHelper.expireByKey(moneyKey, 3 * 60);
							
							String midIdKey = "STR:HC9:OPEN:CARD:FIFTH:MID:RECORD:ID:" + userId;
							RedisHelper.set(midIdKey, "" + midLoanRecordId);
							RedisHelper.expireByKey(midIdKey, 3 * 60);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 封装返回参数
	 * @param rcode 操作类型 0成功 1失败 -1失效
	 * @param msg 返回信息
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> generateMapData(String code, String msg) {
		Map<String, Object> result = new HashMap<>();
		result.put("code", code);
		result.put("msg", msg);
		return result;
	}
}
