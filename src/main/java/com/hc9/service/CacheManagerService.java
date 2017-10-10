package com.hc9.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.redis.IndexDataCache;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.SysCacheManagerUtil;
import com.hc9.common.redis.activity.year2016.month05.GenInvestUserVo;
import com.hc9.common.redis.activity.year2016.month05.HcJuChengCache;
import com.hc9.common.redis.activity.year2016.month05.PrizeVo;
import com.hc9.common.redis.sys.vo.LoansignTypeVo;
import com.hc9.common.redis.sys.vo.LoansignVo;
import com.hc9.common.redis.sys.vo.LoansignbasicsVo;
import com.hc9.common.redis.sys.vo.RepaymentrecordVo;
import com.hc9.common.redis.sys.web.WebCacheManagerUtil;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.CacheManagerDao;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.dao.entity.PrizeDetail;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.LoginRelVo;
import com.hc9.model.PageModel;
import com.hc9.service.activity.ActivityAllInOneService;
import com.hc9.service.activity.ActivityCommonService;
import com.jubaopen.commons.LOG;

@Service
public class CacheManagerService {

	@Resource
	private CacheManagerDao cacheManagerDao;
	
	@Resource
	private BannerService bannerService;
	
	@Resource
	private LoanInfoService loanInfoService;
	
	@Resource
	private UserbasicsinfoService userbasicsinfoService;
	
	@Resource
	ColumnManageService columnServic;
	
	@Resource
	private LoanManageService loanManageService;
	
	@Resource
	private MemberCenterService memberCenterService;
	
	@Resource
	private LoanSignQuery loanSignQuery;
	
	@Resource
	private LoanSignService loanSignService;
	
	@Resource
	private LoanrecordService loanrecordService;
	
	@Resource
	private ActivityMonkeyQueryService activityMonkeyQueryService;
	
	@Resource
    AppCacheService appCacheService;
	
	@Resource
	private ActivityCommonService activityCommonService;
	
	@Resource
	private ActivityAllInOneService activityAllInOneService;
	
	@Resource
	HibernateSupport dao;
	
	public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            20,
            20,
            30,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new ThreadPoolExecutor.CallerRunsPolicy());
	
	/** 登录相关缓存信息更新 */
	public Map<String, String> updateLoginRelCache() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("用户登录相关缓存信息更新至redis开始！");
		long start = System.currentTimeMillis();
		List<LoginRelVo> list = cacheManagerDao.queryUserLoginRelCache();
		if(list != null && list.size() > 0) {
			for(LoginRelVo vo : list) {
				updateLoginRelVoToRedis(vo);
			}
		}
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("用户登录相关缓存信息更新至redis成功，共更新了" + list.size() + "条记录, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** 首页导航条相关缓存更新 */
	public Map<String, String> updateIndexBannerRelCache() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("首页导航条相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		bannerService.query();
		bannerService.queryH5();
		appCacheService.updateAppIndexBannerListCache();
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("首页导航条相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** 累计投资相关缓存更新 */
	public Map<String, String> updateTotalInvestMoney() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("累计投资相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		loanInfoService.gettotalInvestment("INT:HC9:INDEX:INVEST:TOTAL:NUMS");
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("累计投资相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** 总注册人数相关缓存更新 */
	public Map<String, String> updateTotalRegisterNum() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("总注册人数相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		userbasicsinfoService.getcurrentRegUsers("INT:HC9:USR:REGISTER:TOTAL:NUMS");
		
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("总注册人数相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** 文章列表相关缓存更新 */
	public Map<String, String> updateArticleList() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("文章列表相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		columnServic.getArticleList();
		
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("文章列表相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** 热门推荐列表相关缓存更新 */
	public Map<String, String> hotIntroduceLoanList() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("热门推荐相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		loanInfoService.getRecommand();
		appCacheService.updateAppIndexLoanListCache();
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("热门推荐相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** 首页标列表更新 */
	public Map<String, String> updateIndexLoanList() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("首页标列表相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		loanInfoService.updateLoanlist();
		appCacheService.updateAppIndexLoanListCache();
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("首页标列表相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** 我要众持页面标列表更新 */
	public Map<String, String> updateZhongChiPageLoanList() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("我要众持标列表相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		PageModel page = new PageModel();
		page.setPageNum(1);
		page.setNumPerPage(8);
		page = loanManageService.getLoanList(null, null, null, null, page);
		SysCacheManagerUtil.setBuyPayLoanListCache(page);
		appCacheService.updateAppInvestLoanListCache();//app我要投资列表
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("我要众持标列表相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** 待回款项目列表相关缓存更新 */
	public Map<String, String> updateToReturnLoanList() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("待回款项目列表相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		loanInfoService.getLoanLoandynamic();
		
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("待回款项目列表相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** 注册成功后更新用户相关缓存信息 */
	public void updateRegisterRelCache(String userName, String phone) {
		SysCacheManagerUtil.increaseTotalRegisterNum();
		LoginRelVo loginRelVo = cacheManagerDao.queryLoginRelVoBy(userName, phone);
		if(loginRelVo != null) {
			updateLoginRelVoToRedis(loginRelVo);
		}
	}
	
	/** 更新用户信息至redis缓存中 */
	public void updateLoginRelVoToRedis(LoginRelVo loginRelVo) {
		String userName = loginRelVo.getUserName();
		String phone = loginRelVo.getPhone();
		String staffNo = loginRelVo.getStaffNo();
		
		String jsonData = JsonUtil.toJsonStr(loginRelVo);
		/** 登录账号相关对应redis信息 */
		if(StringUtil.isNotBlank(userName)) {
			SysCacheManagerUtil.setLoginRelVoByUserName(userName, jsonData);
		}
		
		/** 手机号对应相关redis信息 */
		if(StringUtil.isNotBlank(phone)) {
			LoginRelVo oldLoginRelVo = SysCacheManagerUtil.getLoginRelVoById("" + loginRelVo.getId());
			if(oldLoginRelVo!=null){
				String oldPhone = oldLoginRelVo.getPhone();
				if(!phone.equals(oldPhone)) {
					String phoneKey = "STR:HC9:USR:LOGING:REL:PHONE:" + oldPhone;
					RedisHelper.del(phoneKey);
				}
			}
			SysCacheManagerUtil.setLoginRelVoByhone(phone, jsonData);
		}
		
		/** 用户id对应相关的redis信息 */
		SysCacheManagerUtil.setLoginRelVoById(loginRelVo.getId(), jsonData);
		
		/** 员工编号对应的相关信息 */
		if(StringUtil.isNotBlank(staffNo)) {
			SysCacheManagerUtil.setLoginRelVoByStaffNo(staffNo, jsonData);
		}
	}
	
	/** 满标放款根据项目id更新用户的回款记录 */
	public void updateUserRepaymentListByLoanSignId(Long loanSignId) {
		updateLoanDetailRelCache("" + loanSignId);
		List<Long> userIdList = cacheManagerDao.queryUserIdListByLoanSignId(loanSignId);
		if(userIdList != null && userIdList.size() > 0) {
			for(Long userId : userIdList) {
				memberCenterService.repaymentBackList(userId);//用户的还款和回款还款信息更新
				System.out.println("项目" + loanSignId + "满标放款更新用户" + userId + "对应回款列表信息成功！");
				LOG.error("项目" + loanSignId + "满标放款更新用户" + userId + "对应回款列表信息成功！");
			}
		}
	}
	
	/** 更新标详情缓存信息 */
	@SuppressWarnings("rawtypes")
	public Map<String, String> updateLoanDetailRelCache(String loanId) {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("更新标详情缓存信息至redis开始！");
		long start = System.currentTimeMillis();
		
		Loansign loan = loanSignQuery.getLoansignById(loanId);
//		Loansign loan = loanSignQuery.queryLoanSignById(loanId);
		if(loan != null) {
			String borrwerKey = "LST:HC9:LOANSIGN:BORROWER:LOANID:" + loanId;
			String borrwer = "暂无";
			
			if(StringUtil.isNotBlank(loan.getUserbasicsinfo().getStaffNo())) {
				borrwer = loan.getUserbasicsinfo().getStaffNo();
			}
			updateLoansignToRedis(loan);
			RedisHelper.set(borrwerKey, borrwer);
			
			// 统计购买人数
			String investNumKey = "INT:HC9:LOANSIGN:INVEST:NUM:" + loanId;
			// 统计购买人数
			Object investNum = loanInfoService.getTenderCount(loanId);
			
			// 查询众筹附件
			List attachList1 = null;
			List attachList2 = null;
			List attachList3 = null;
			List attachList4 = null;
			List attachList5 = null;
						
			String attachList1Key = "LST:HC9:LOANSIGN:ATTACH:LST:1:" + loanId;
			String attachList2Key = "LST:HC9:LOANSIGN:ATTACH:LST:2:" + loanId;
			String attachList3Key = "LST:HC9:LOANSIGN:ATTACH:LST:3:" + loanId;
			String attachList4Key = "LST:HC9:LOANSIGN:ATTACH:LST:4:" + loanId;
			String attachList5Key = "LST:HC9:LOANSIGN:ATTACH:LST:5:" + loanId;
						
			// 查询众筹附件
			attachList1 = loanSignService.getAttachMent(loanId, "1");// 项目证明
			attachList2 = loanSignService.getAttachMent(loanId, "2");// 资产包证明
			attachList3 = loanSignService.getAttachMent(loanId, "3");// 担保证明
			attachList4 = loanSignService.getAttachMent(loanId, "4");// 保障证明
			attachList5 = loanSignService.getAttachMent(loanId, "5");// 监管资金证明
			RedisHelper.set(investNumKey, "" + investNum);
			IndexDataCache.set(attachList1Key, attachList1);
			IndexDataCache.set(attachList2Key, attachList2);
			IndexDataCache.set(attachList3Key, attachList3);
			IndexDataCache.set(attachList4Key, attachList4);
			IndexDataCache.set(attachList5Key, attachList5);
			
			PageModel page = new PageModel();
			page.setPageNum(1);
			page.setNumPerPage(10);
			page = loanSignService.getLoanrecordList(Long.parseLong(loanId), page);
			
			String loanrecordListStr = JsonUtil.toJsonStr(page);
			String loanRecordkey = "LST:HC9:LOANSIGN:DETAIL:BUY:LIST:FIRST:PAGE:" + loanId;
			RedisHelper.set(loanRecordkey, loanrecordListStr);
			resultMap.put("msg", "更新成功！");
		} else {
			resultMap.put("msg", "所录入的标id在数据库中不存在！");
		}
		
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("更新标详情缓存信息至redis成功, 共花费 " + spendTime + " 秒！");
		
		appCacheService.updateAppLoanDetailCache(loanId);
		resultMap.put("code", "0");
		return resultMap;
	}
	
	/** 更新标详情页缓存至redis中 */
	public void updateLoansignToRedis(Loansign loansign) {
		/** 组装标的信息 */
		LoansignVo loansignVo = new LoansignVo();
		loansignVo.setId(loansign.getId());
		loansignVo.setName(loansign.getName());
		loansignVo.setIssueLoan(loansign.getIssueLoan());
		loansignVo.setRestMoney(loansign.getRestMoney());
		loansignVo.setPrioRate(loansign.getPrioRate());
		loansignVo.setPrioAwordRate(loansign.getPrioAwordRate());
		loansignVo.setPrioRestMoney(loansign.getPrioRestMoney());
		loansignVo.setMidRestMoney(loansign.getMidRestMoney());
		loansignVo.setAfterRestMoney(loansign.getAfterRestMoney());
		loansignVo.setLoanUnit(loansign.getLoanUnit());
		loansignVo.setPublishTime(loansign.getPublishTime());
		loansignVo.setCreditTime(loansign.getCreditTime());
		loansignVo.setStatus(loansign.getStatus());
		loansignVo.setType(loansign.getType());
		loansignVo.setRemonth(loansign.getRemonth());
		loansignVo.setValidity(loansign.getValidity());
		loansignVo.setActivityStatus(loansign.getActivityStatus());
		
		/** 更新项目基本信息 */
		updateLoansignbasicsVoToRedis(loansignVo, loansign);
		
		/** 项目类型 */
		LoansignTypeVo loansignTypeVo = new LoansignTypeVo();
		loansignTypeVo.setId(loansign.getLoansignType().getId());
		loansignVo.setLoansignType(loansignTypeVo);
		
		/** 更新还款记录信息 */
		updateLoansignVoDetailToRedis(loansignVo, loansign);
		
		WebCacheManagerUtil.setWebLoanSignDetailToRedis(loansignVo);
	}
	
	/** 组装项目基本信息 */
	private void updateLoansignbasicsVoToRedis(LoansignVo loansignVo, Loansign loansign) {
		Loansignbasics loansignbasics = loansign.getLoansignbasics();
		if(loansignbasics != null) {
			LoansignbasicsVo vo = new LoansignbasicsVo();
			vo.setRemark(loansignbasics.getRemark());
			vo.setBehoof(loansignbasics.getBehoof());
			vo.setHistory(loansignbasics.getHistory());
			vo.setRiskAdvice(loansignbasics.getRiskAdvice());
			loansignVo.setLoansignbasics(vo);
		}
	}
	
	/** 组装标详情信息 */
	private void updateLoansignVoDetailToRedis(LoansignVo loansignVo, Loansign loansign) {
		List<Repaymentrecord> repaymentrecords = loansign.getRepaymentrecords();
		if(repaymentrecords != null && repaymentrecords.size() > 0) {
			List<RepaymentrecordVo> repayList = new ArrayList<RepaymentrecordVo>();
			for(Repaymentrecord vo : repaymentrecords) {
				RepaymentrecordVo repayment = new RepaymentrecordVo();
				repayment.setRepayState(vo.getRepayState());
				repayment.setPeriods(vo.getPeriods());
				repayment.setRepayTime(vo.getRepayTime());
				repayList.add(repayment);
			}
			loansignVo.setRepaymentrecords(repayList);
		}
	}
	
	/** 更新所有标详细缓存信息 */
	public Map<String, String> updateAllLoanDetailRelCache() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("更新标详情缓存信息至redis开始！");
		long start = System.currentTimeMillis();
		
		List<Long> loanIdList = cacheManagerDao.queryAllLoanIdList();
		if(loanIdList.size() > 0) {
			for(Long loanId : loanIdList) {
				updateLoanDetailRelCache("" + loanId);
				appCacheService.updateAppLoanDetailCache("" + loanId);
			}
		}
			
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("更新标详情缓存信息至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** H5我要众持页面标列表更新 */
	public Map<String, String> updateH5ZhongChiPageLoanList() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("首页标列表相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		PageModel page = new PageModel();
		page.setPageNum(1);
		page.setNumPerPage(10);
		page = loanManageService.getLoanList(page);;
		SysCacheManagerUtil.setH5BuyPayLoanListCache(page);
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("首页标列表相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** H5热门推荐列表相关缓存更新 */
	public Map<String, String> updateH5HotIntroduceLoanList() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("H5热门推荐相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		String key="LIST:HC9:INDEX:LOAN:RECOMMAND2";
		loanManageService.getLoanRecommandList(key);
		
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("H5热门推荐相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** H5首页导航条相关缓存更新 */
	public Map<String, String> updateH5IndexBannerRelCache() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("H5首页导航条相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		bannerService.queryH5();
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("H5首页导航条相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** H5累计收益相关缓存更新 */
	public Map<String, String> updateH5TotalIncome() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("H5累计收益相关缓存更新至redis开始！");
		long start = System.currentTimeMillis();
		String key="INT:HC9:INDEX:PROFIT:INVEST:TOTAL:NUMS";
		loanInfoService.getH5TotalIncome(key);
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("H5累计收益相关缓存更新至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		resultMap.put("msg", "更新成功！");
		return resultMap;
	}
	
	/** 更新猴声大噪缓标存信息 */
	public Map<String, String> updateActivityMonkeyMax(String monkeyLoanSignId) {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("更新猴声大噪缓标存信息至redis开始！");
		long start = System.currentTimeMillis();
		
		Loansign loan = loanSignQuery.getLoansignById(monkeyLoanSignId);
		if(loan != null) {
			if(loan.getActivityStatus() == null || loan.getActivityStatus()!=1) {
				resultMap.put("msg", "更新失败!该标的未参与活动！");
				resultMap.put("code", "0");
				return resultMap;	
			}
			
			String maxKey = "NEWYEAR:INVEST:MONEY:LOANID:MAX:"+monkeyLoanSignId;
			String lastKey = "NEWYEAR:INVEST:MONEY:LOANID:LAST:"+monkeyLoanSignId;
			
			Loanrecord loanrecord = loanrecordService.getMaxLoanRecord(monkeyLoanSignId);
			Map<String, String> maxMap = RedisHelper.hgetall(maxKey);
			maxMap.put("userId", loanrecord.getUserbasicsinfo().getId()+"");
			maxMap.put("money", loanrecord.getTenderMoney()+"");
			maxMap.put("phone", loanrecord.getUserbasicsinfo().getUserrelationinfo().getPhone());
			maxMap.put("loanId", loan.getId()+"");
			maxMap.put("loanName", loan.getName());
			maxMap.put("loanRecordId", loanrecord.getId()+"");
			maxMap.put("nickname", loanrecord.getUserbasicsinfo().getUserName());
			RedisHelper.hmset(maxKey, maxMap);
			
			if (loan.getPrioRestMoney() == 0 && loan.getMidRestMoney() == 0) {
				if(loanrecordService.getIsSucceed(monkeyLoanSignId)==0){
					Loanrecord lastLoanrecord = loanrecordService.getLastLoanRecord(monkeyLoanSignId);
					Map<String, String> lastMap = RedisHelper.hgetall(maxKey);
					lastMap.put("userId", lastLoanrecord.getUserbasicsinfo().getId()+"");
					lastMap.put("money", lastLoanrecord.getTenderMoney()+"");
					lastMap.put("phone", lastLoanrecord.getUserbasicsinfo().getUserrelationinfo().getPhone());
					lastMap.put("loanId", loan.getId()+"");
					lastMap.put("loanName", loan.getName());
					lastMap.put("loanRecordId", lastLoanrecord.getId()+"");
					lastMap.put("nickname", lastLoanrecord.getUserbasicsinfo().getUserName());
					RedisHelper.hmset(lastKey, lastMap);
					
					if(activityMonkeyQueryService.getMonkeyLoanId(monkeyLoanSignId)==0){
						List<ActivityMonkey> activityMonkeyList = new ArrayList<ActivityMonkey>();
						activityMonkeyList.add(generateActivityMonkey(maxMap.get("userId"), maxMap.get("phone"), maxMap.get("money"), 12, maxMap.get("loanId"), maxMap.get("loanName"), maxMap.get("loanRecordId"), "168", 0));
						activityMonkeyList.add(generateActivityMonkey(lastMap.get("userId"), lastMap.get("phone"), lastMap.get("money"), 13, lastMap.get("loanId"), lastMap.get("loanName"), lastMap.get("loanRecordId"), "68", 0));
						
						activityMonkeyQueryService.delActivityMonkey(monkeyLoanSignId);
						activityMonkeyQueryService.addActivityMonkey(activityMonkeyList);
					}else{
						resultMap.put("msg", "更新失败!奖金已发放！");
						resultMap.put("code", "0");
						return resultMap;	
					}
				}else{
					resultMap.put("msg", "更新失败!有待确认的投资记录,请查询后再试！");
					resultMap.put("code", "0");
					return resultMap;	
				}
			}
			
			resultMap.put("msg", "更新成功！");
		} else {
			resultMap.put("msg", "所录入的标id在数据库中不存在！");
		}
		
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("更新猴声大噪缓标存信息至redis成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		return resultMap;
	}
	
	/** 新春猴给力活动 */
	public static ActivityMonkey generateActivityMonkey(String userId, String phone, String priority, int type, String loanId, 
			String loanName, String loanRecordId, String rewardMoney, int week) {
		Date date = new Date();
		String createTime = DateFormatUtil.dateToString(date, "yyyy-MM-dd HH:mm:ss");
		
		ActivityMonkey activityMonkey = new ActivityMonkey();
		activityMonkey.setUserId(Long.parseLong(userId));
		activityMonkey.setMobilePhone(phone);
		activityMonkey.setMoney(Double.parseDouble(priority));
		activityMonkey.setType(type);
		activityMonkey.setLoanId(Long.parseLong(loanId));
		activityMonkey.setLoanName(loanName);
		activityMonkey.setLoanRecordId(Long.parseLong(loanRecordId));
		activityMonkey.setRewardMoney(Double.parseDouble(rewardMoney));
		activityMonkey.setCreateTime(createTime);
		activityMonkey.setWeek(week);
		activityMonkey.setStatus(0);
		activityMonkey.setExamineStatus(0);
		return activityMonkey;
	}
	
	/** 更新猴声大噪周榜到数据库 */
	public Map<String, String> updateActivityMonkeyWEEK() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("更新猴声大噪周榜DB信息开始！");
		long start = System.currentTimeMillis();
		
		int week = week();
		if(week > 0){
			String key = "NEWYEAR:INVEST:MONKEY:WEEK:" + week;
			List<Map> list = IndexDataCache.getList(key);
			if (list != null) {
				// 插入库
				List<ActivityMonkey> activityMonkeyList = new ArrayList<ActivityMonkey>();
				for (int i = 0; i < list.size() && i < 3; i++) {
					Map<String, String> map = list.get(i);
					String rewardMoney = "0";
					if(i==0){
						rewardMoney = (Double.parseDouble(map.get("money"))*0.8/100)+"";
					}else if(i==1){
						rewardMoney = (Double.parseDouble(map.get("money"))*0.6/100)+"";
					}else if(i==2){
						rewardMoney = (Double.parseDouble(map.get("money"))*0.3/100)+"";
					}
					activityMonkeyList.add(generateActivityMonkey(map.get("userId"), map.get("phone"), map.get("money"), (3+i), "0", "", map.get("loanRecordId")+"", rewardMoney, week));
				}
				try {
					if(activityMonkeyList.size() > 0){
						activityMonkeyQueryService.addActivityMonkey(activityMonkeyList);
					}
				} catch (DataAccessException e) {
					resultMap.put("msg", "更新周榜失败!请查询后再试！");
					resultMap.put("code", "0");
					return resultMap;
				}
			}
			if(week == 6){
				key = "NEWYEAR:INVEST:MONKEY:TOTAL";
				list = IndexDataCache.getList(key);
				if (list != null) {
					// 插入库
					List<ActivityMonkey> activityMonkeyList = new ArrayList<ActivityMonkey>();
					for (int i = 0; i < list.size() && i < 3; i++) {
						Map<String, String> map = list.get(i);
						String rewardMoney = "0";
						activityMonkeyList.add(generateActivityMonkey(map.get("userId"), map.get("phone"), map.get("money"), (6+i), "0", "", map.get("loanRecordId")+"", rewardMoney, 0));
					}
					try {
						if(activityMonkeyList.size() > 0){
							activityMonkeyQueryService.addActivityMonkey(activityMonkeyList);
						}
					} catch (DataAccessException e) {
						resultMap.put("msg", "更新总榜失败!请查询后再试！");
						resultMap.put("code", "0");
						return resultMap;
					}
				}
			}
		}
		resultMap.put("msg", "更新成功!周榜已更新到数据库！");
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("更新猴声大噪周榜DB信息成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		return resultMap;
	}

	/** 更新理财师现金返利排行榜到数据库 */
	public Map<String, String> updateActivityFinancial() {
		Map<String, String> resultMap = new HashMap<String, String>();
		LOG.error("更新理财师现金返利排行榜到数据库开始！");
		long start = System.currentTimeMillis();
		
		String key = "NEWYEAR:INVEST:FINANCIAL:LIST";
		List<Map> list = IndexDataCache.getList(key);
		if (list != null) {
			// 插入库
			List<ActivityMonkey> activityMonkeyList = new ArrayList<ActivityMonkey>();
			for (int i = 0; i < list.size(); i++) {
				Map<String, String> map = list.get(i);
				double money = Double.parseDouble(map.get("money")); 
				if(money >= 100000){
					String rewardMoney = "0";
					if(money >= 1000000){
						rewardMoney = "1888";
					}else if(money >= 500000){
						rewardMoney = "888";
					}else if(money >= 200000){
						rewardMoney = "388";
					}else if(money >= 100000){
						rewardMoney = "188";
					}
					activityMonkeyList.add(generateActivityMonkey(map.get("userId"), map.get("phone"), map.get("money"), 9, "0", "", "0", rewardMoney, 0));
				}else{
					break;
				}
			}
			try {
				if(activityMonkeyList.size() > 0){
					activityMonkeyQueryService.addActivityMonkey(activityMonkeyList);
				}
			} catch (DataAccessException e) {
				resultMap.put("msg", "更新理财师现金返利排行榜到数据库失败!请查询后再试！");
				resultMap.put("code", "0");
				return resultMap;
			}
		}
		resultMap.put("msg", "更新成功!理财师现金返利排行榜到数据库！");
		long spendTime = (System.currentTimeMillis() - start) / 1000;
		LOG.error("更新理财师现金返利排行榜到数据库成功, 共花费 " + spendTime + " 秒！");
		resultMap.put("code", "0");
		return resultMap;
	}
	
	public int week(){
		Date beginDate0 = DateFormatUtil.stringToDate("2016-01-18", "yyyy-MM-dd");
		Date beginDate1 = DateFormatUtil.stringToDate("2016-01-25", "yyyy-MM-dd");
		Date beginDate2 = DateFormatUtil.stringToDate("2016-02-01", "yyyy-MM-dd");
		Date beginDate3 = DateFormatUtil.stringToDate("2016-02-08", "yyyy-MM-dd");
		Date beginDate4 = DateFormatUtil.stringToDate("2016-02-15", "yyyy-MM-dd");
		Date beginDate5 = DateFormatUtil.stringToDate("2016-02-22", "yyyy-MM-dd");
		Date beginDate6 = DateFormatUtil.stringToDate("2016-02-29", "yyyy-MM-dd");
		
		Date currentDate = new Date();
		if(DateFormatUtil.isBefore(beginDate6, currentDate)){
			return 6;
		}else if(DateFormatUtil.isBefore(beginDate5, currentDate)){
			return 5;
		}else if(DateFormatUtil.isBefore(beginDate4, currentDate)){
			return 4;
		}else if(DateFormatUtil.isBefore(beginDate3, currentDate)){
			return 3;
		}else if(DateFormatUtil.isBefore(beginDate2, currentDate)){
			return 2;
		}else if(DateFormatUtil.isBefore(beginDate1, currentDate)){
			return 1;
		}else if(DateFormatUtil.isBefore(beginDate0, currentDate)){
			return 0;
		}else{
			return -1;
		}
	}
	
	/**app启动画面开关*/
	public Map<String, String> appStartPictureSwitch(int pictureSwitch){
		Map<String, String> resultMap=new HashMap<String,String>();
		String appKey="STR:HC9:APP:START:IMAGE:URL:FLAG";
			if(pictureSwitch==0){
				RedisHelper.set(appKey,"0");
				resultMap.put("msg", "画面关闭");
			}else if(pictureSwitch==1){
				RedisHelper.set(appKey,"1");
				resultMap.put("msg", "画面开启");
			} else{
			resultMap.put("msg","请选择开关状态!");
		}
		return resultMap;
	}
	
	
	/**app启动画面图片地址*/
	public Map<String, String> updateAppStartImageAddress(String url){
		Map<String, String> resultMap=new HashMap<String,String>();
		String urlKey="STR:HC9:APP:START:IMAGE:URL";
		if(StringUtil.isBlank(url)){
			resultMap.put("msg", "图片地址不能为空!");
			return resultMap;
		}else {
			RedisHelper.set(urlKey, url);
			resultMap.put("msg", "缓存地址成功!");
		}
		return resultMap;
	}
	
	/** 投资有奖聚橙网门票发放 */
	public Map<String, String> juChengTicketInvestAward(String loanId){
		Map<String, String> resultMap = new HashMap<String,String>();
		if(StringUtil.isBlank(loanId)){
			resultMap.put("msg", "项目id不能为空!");
			return resultMap;
		}
		int leftNum = HcJuChengCache.getLeftTicketNumForNoneJuChengUser();
		if(leftNum <= 0) {
			resultMap.put("msg", "门票已发放完毕，不能继续发放!");
			return resultMap;
		}
		String sql = "select * from loanrecord where loansign_id=? and issucceed=1 and tendermoney>=200000 "
				+ " and subtype in(1,2) and id not in(select loanrecordid from prizedetail where prizetype=17) "
				+ " order by id asc";
		List<Loanrecord> list = dao.findBySql(sql, Loanrecord.class, loanId);
		if(list != null && list.size() > 0) {
			int totalTicketNum = 0;
			for(Loanrecord loanRecord : list) {
				Loansign loan = loanRecord.getLoansign();
				Userbasicsinfo user = loanRecord.getUserbasicsinfo();
				String userName = user.getUserName();
				/** 是否聚橙网活动期间的用户 */
				boolean juChengUserFlag = activityAllInOneService.isUserValidJuChengActivity(userName);
				if(!juChengUserFlag) {
					long userId = user.getId();
					String loanName = loan.getName();
					/** 投资类型：投资类型 1 优先 2 夹层 3劣后 */
					int investType = loanRecord.getSubType();
					double investMoney = loanRecord.getTenderMoney();//投资金额
					String phone = user.getUserrelationinfo().getPhone();//投资人电话号码
					/** 标类型：1-店铺  2-项目 3-天标 4-债权转让 */
					int loanType = loan.getType();
					/** 回购期限:如果是天标的话为天数，项目的话为月数 */
					int reMonth = loan.getRemonth();
					long loanRecordId = loanRecord.getId();
					int giveTicketNum = HcJuChengCache.giveTicketForNonJuChengUser(userId, investType, 
							investMoney, loanRecordId, loanType, reMonth, phone, 
							Long.valueOf(loanId), loanName, activityCommonService);
					if(giveTicketNum > 0) {
						totalTicketNum += giveTicketNum;
					}
				}
			}
			resultMap.put("msg", "操作成功（本次成功发放" + totalTicketNum + "张门票）!");
			return resultMap;
		} else {
			resultMap.put("msg", "未查询到符合要求的投资记录!");
			return resultMap;
		}
	}
	
	/** 聚橙网推荐用户门票发放 */
	public Map<String, String> juChengGenUserTcketAward(){
		Map<String, String> resultMap = new HashMap<String,String>();
		removeSpecialUserTicketInfo();
		
		resultMap = giveTicketToSpecialUser();
		return resultMap;
	}
	
	/** 清除聚橙网列表中詹奇红、庄文端、刘彦鸿三人的中奖数据，并增加对应总票数 */
	private void removeSpecialUserTicketInfo() {
		String key = "STR:HC9:GEN:USER:IDS:JUCHENG:TICKET";
		String userIds = RedisHelper.get(key);
		if(StringUtil.isNotBlank(userIds)) {
			String[] ids = userIds.split("_");
			List<PrizeDetail> prizeList = cacheManagerDao.queryPrizeDetailListByUserIds(ids);
			if(prizeList != null && prizeList.size() > 0) {
				List<PrizeVo> oldList = HcJuChengCache.getJuChengPrizeList();
				if(oldList.size() > 0) {
					List<PrizeVo> newList = new ArrayList<PrizeVo>();
					int prizeNum = 0;
					for(PrizeVo oldVo : oldList) {
						boolean flag = true;
						for(PrizeDetail detail : prizeList) {
							long userId = detail.getUserId();
							if(oldVo.getUserId() == userId) {
								flag = false;
								break;
							}
						}
						if(flag) {
							newList.add(oldVo);
						} else {
							prizeNum += oldVo.getPrizeNum();
						}
					}
					HcJuChengCache.increaseGivenTicktNum(-1 * prizeNum);
					key = "STR:HC9:JU:CHENG:PRIZE:LIST";
					String json = JsonUtil.toJsonStr(newList);
					RedisHelper.set(key, json);
					cacheManagerDao.deleteJuChengTicketForGenUser(ids);
				}
			}
		}
	}
	
	/** 发放指定用户的推介人的门票信息 */
	private Map<String, String> giveTicketToSpecialUser() {
		Map<String, String> resultMap = new HashMap<String,String>();
		int leftNum = HcJuChengCache.getLeftTicketNumForGenUserInvest();
		if(leftNum <= 0) {
			resultMap.put("msg", "门票已发放完毕，不能继续发放!");
			return resultMap;
		}
		List<GenInvestUserVo> userList = cacheManagerDao.queryGenInvestUserList();
		if(userList != null && userList.size() > 0) {
			int totalTicketNum = 0;
			for(GenInvestUserVo vo : userList) {
				long genUserId = vo.getGenUserId();
				String sql = "select * from loanrecord where id=?";
				Loanrecord loanRecord = dao.findObjectBySql(sql, Loanrecord.class, vo.getLoanRecordId());
				Loansign loan = loanRecord.getLoansign();
				String loanName = loan.getName();
				/** 投资类型：投资类型 1 优先 2 夹层 3劣后 */
				int investType = loanRecord.getSubType();
				double investMoney = loanRecord.getTenderMoney();//投资金额
				String phone = loanRecord.getUserbasicsinfo().getUserrelationinfo().getPhone();//投资人电话号码
				String genPhone = "";
				sql = "select * from userbasicsinfo where id=?";
				Userbasicsinfo genUser = dao.findObjectBySql(sql, Userbasicsinfo.class, genUserId);
				int genUserType = 0;
				if(genUser != null) {
					genUserType = genUser.getUserType();
					genPhone = genUser.getUserrelationinfo().getPhone();
				}
				/** 标类型：1-店铺  2-项目 3-天标 4-债权转让 */
				int loanType = loan.getType();
				/** 回购期限:如果是天标的话为天数，项目的话为月数 */
				int reMonth = loan.getRemonth();
				long loanRecordId = loanRecord.getId();
				/** 先保存推介人的门票信息 */
				int giveTicketNum = 0;
				int prizeNum = 1;
				if(genUserType != 2) {
					giveTicketNum = HcJuChengCache.giveTicketForGenUserInvest(genUserId, investType, 
							investMoney, loanRecordId, loanType, reMonth, genPhone, 
							loan.getId(), loanName, prizeNum, activityCommonService);
				}
				if(giveTicketNum > 0) {
					totalTicketNum += giveTicketNum;
				}
				if(vo.getInvestMoney() >= 50000) {
					long userId = vo.getUserId();
					if(genUserType == 2) {
						prizeNum = 2;
					}
					giveTicketNum = HcJuChengCache.giveTicketForGenUserInvest(userId, investType, 
							investMoney, loanRecordId, loanType, reMonth, phone, 
							loan.getId(), loanName, prizeNum, activityCommonService);
					if(giveTicketNum > 0) {
						totalTicketNum += giveTicketNum;
					}
				}
			}
			resultMap.put("msg", "操作成功，成功发放" + totalTicketNum + "张门票！");
			return resultMap;
		} else {
			resultMap.put("msg", "未查询到符合要求的投资用户!");
			return resultMap;
		}
	}
	
	/** 聚橙网推荐用户门票发放 */
	public Map<String, String> juChengWeiBoTcketAward(String phone){
		Map<String, String> resultMap = new HashMap<String,String>();
		if(StringUtil.isBlank(phone)) {
			resultMap.put("msg", "手机号码不能为空，发放失败!");
			return resultMap;
		}
		int leftNum = HcJuChengCache.getLeftTicketNumForNoneJuChengUser();
		if(leftNum <= 0) {
			resultMap.put("msg", "门票已发放完毕，不能继续发放!");
			return resultMap;
		}
		int totalTicketNum = 0;
		LoginRelVo loginRelVo = cacheManagerDao.queryLoginRelVoBy(phone, phone);
		if(loginRelVo != null) {
			long userId = loginRelVo.getId();
			String key = "STR:HC9:JUCHENG:WEIBO:TICKET:" + userId;
			if(!RedisHelper.isKeyExist(key)) {
				int giveTicketNum = HcJuChengCache.giveTicketForWeiBo(userId, phone, 
						activityCommonService);
				if(giveTicketNum > 0) {
					totalTicketNum += giveTicketNum;
				}
				resultMap.put("msg", "操作成功，成功发放" + totalTicketNum + "张门票！");
				return resultMap;
			} else {
				resultMap.put("msg", "操作失败，不能重复赠送门票!");
			}
		} else {
			resultMap.put("msg", "未查到对应用户信息!");
		}
		return resultMap;
	}
}
