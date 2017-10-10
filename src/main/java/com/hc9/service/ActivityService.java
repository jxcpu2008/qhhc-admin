package com.hc9.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.redis.HcPeachActivitiCache;
import com.hc9.common.redis.HcSeptemberActivitiCache;
import com.hc9.common.redis.IndexDataCache;
import com.hc9.common.redis.QiXiActivitiCache;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;
import com.hc9.dao.ActivityDao;
import com.hc9.dao.entity.ActivityMonkey;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.PrizeDetail;
import com.hc9.dao.entity.RedEnvelopeDetail;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.VoteRecord;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.LotteryRank;
import com.hc9.model.PageModel;
import com.hc9.model.PrizeInfo;
import com.hc9.model.VoteRecordVo;

/** 活动相关服务类 */
@Service
public class ActivityService {

	@Resource
	private ActivityDao activityDao;
	
	@Resource
	private HibernateSupport dao;
	
	public Map<String, String> voteForSomeBody(String votedId, HttpServletRequest request) {
		Map<String, String> resultMap = new HashMap<String, String>();
		String code = "-1";
		String msg = "当前时间不在活动期间内！";
		Userbasicsinfo userbasic = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
		if(userbasic != null) {
			Long userId = userbasic.getId();
			Integer isAuthIps = userbasic.getIsAuthIps();
			if(isAuthIps != null && isAuthIps.intValue() == 1) {
				int voteChance = QiXiActivitiCache.getVoteChanceNumOfUser(userId);
				if(voteChance > 0) {
					try {
						String key = "STR:HC9:QIXI:VOTE:NUM:VOTEDID:DAO:" + userId + ":" + votedId;
						if(!RedisHelper.isKeyExistSetWithExpire(key, 0)) {
							activityDao.saveVoteRecord(userId, votedId);
						}
						String currentDate = DateFormatUtil.dateToString(new Date(), "yyyy-MM-dd");
						QiXiActivitiCache.increaseUserVoteNumOfVotedId(userId, votedId);//记录投票人所支持的选手票数
						QiXiActivitiCache.increaseVoteNumByVotedId(votedId);//新增被投票人所获得的票数
						QiXiActivitiCache.increaseUserVoteNum(userId, currentDate);//新增投票人的当天的投票次数
						int userVoteNum = QiXiActivitiCache.getUserVoteNumByDate(userId, currentDate);
						if(Integer.valueOf(userVoteNum) > 3) {//如果总送的投票机会用完，则需要减去赠送的投票次数
							QiXiActivitiCache.decreaseVoteChance(userId);
						}
						code = "0";
						msg = "投票成功！";
					} catch(Exception e) {
						code = "-2";
						msg = "投票失败！";
					}
				} else {
					code = "-3";
					msg = "HI，亲！你的机会已经用完了哦~！";
				}
			} else {
				code = "-4";
				msg = "HI,亲！你还没有绑定宝付哦，只有绑定宝付才可以投票！";
			}			
		} else {
			code = "-5";//尚未登录
			msg = "抱歉，您还未登录哦！";
		}
		resultMap.put("code", code);
		resultMap.put("msg", msg);
		return resultMap;
	}
	
	/** 用户投资送投票次数 */
	public void giveVoteNumOfQiXiActivity(Loanrecord loanRecord) {
		try {
			/** 首先判断是否七夕活动期间： */
			if(QiXiActivitiCache.isQiXiActivity(new Date())) {
				long userId = loanRecord.getUserbasicsinfo().getId();
				Double investMoney = loanRecord.getTenderMoney();
				QiXiActivitiCache.giveVoteChanceNumForInvest(userId, investMoney);
			}
		} catch(Exception e) {
			LOG.error("七夕活动投资送投票次数出现异常，对应的投资id为： " + loanRecord.getId(), e);
		}
	}
	
	/** 分页查询七夕活动相关投票人列表 */
	public List<VoteRecordVo> querySevenMoonList(PageModel page, String votedId) {
		List<VoteRecordVo> voteRecordList = activityDao.querySevenMoonList(page, votedId);
		return voteRecordList;
	}
	
	/** 更新投票相关统计信息 */
	public Map<String, Object> updateVoteStatisticsInfo() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String code = "0";
		String msg = "更新七夕活动投票统计信息成功！";
		List<VoteRecord> voteRecordList = activityDao.queryAllVoteRecordList();
		if(voteRecordList != null && voteRecordList.size() > 0) {
			for(VoteRecord voteRecord : voteRecordList) {
				activityDao.updateVoteStatisticsInfo(voteRecord);
			}
		}
		resultMap.put("code", code);
		resultMap.put("msg", msg);
		return resultMap;
	}
	
	/** 查询投票排行榜列表 */
	public List<VoteRecordVo> queryVoteRankList() {
		List<String> votedIdList = new ArrayList<String>();
		votedIdList.add("sigi");
		votedIdList.add("kelvin");
		votedIdList.add("jessica");
		votedIdList.add("elena");
		votedIdList.add("japser");
		List<VoteRecordVo> voteRecordList = activityDao.queryVoteRankList();
		List<String> resultList = new ArrayList<String>();
		for(String votedId : votedIdList) {
			if(!resultList.contains(votedId)) {
				VoteRecordVo vo = new VoteRecordVo();
				vo.setVotedId(votedId);
				vo.setTotalNum(0L);
			}
		}
		return voteRecordList;
	}
	
	/** 七夕活动利息项目项目汇总 */
	public Map<String, Object> voteWinIncomeSummary() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String code = "-1";
		String msg = "更新活动收益失败！";
		if(QiXiActivitiCache.isQixiActivityEend(new Date())) {
			/** 获取赢家信息 */
			String winSideId = queryQixiActivityWinSideId();
			
			if(StringUtil.isNotBlank(winSideId)) {
				/** 查询赢家下的投票人列表 */
				List<VoteRecordVo> voteRecordList = activityDao.queryVoteRecordListByVotedId(winSideId);
				if(activityDao.isSavedVoteIncome()) {
					msg = "活动收益已经计算过一次，不能重复计算！";
				} else {
					/** 计算赢家下投票人的七夕活动加息券收益并保存入数据库 */
					boolean updateFlag = activityDao.computeAndSaveInComeOfQixi(voteRecordList);
					if(updateFlag) {
						code = "0";
						msg = "活动收益计算成功！";
					} else {
						msg = "没有查询到合适的数据进行处理！";
					}
				}
			} else {
				msg = "未查询到获赞胜利方相关数据！";
			}
		} else {
			msg = "活动尚未结束，暂时不能发放活动相关奖励！";
		}
		resultMap.put("code", code);
		resultMap.put("msg", msg);
		return resultMap;
	}
	
	/** 获取七夕活动赢家唯一表示id */
	private String queryQixiActivityWinSideId() {
		String winSideId = "";
		List<VoteRecordVo> list = activityDao.queryVoteRankList();
		if(list != null && list.size() > 0) {
			VoteRecordVo vo = list.get(0);
			winSideId = vo.getVotedId();
			String sql = "update voterecord set status=1 where votedId=?";
			dao.executeSql(sql, winSideId);
		}
		return winSideId;
	}
	
	public PageModel getPlusInterestList(PageModel page, Long id) {
		StringBuffer sql = new StringBuffer(
				"select id,money,beginTime,endTime,lowestUseMoney,"
				+ "case when (date_format(now(),'%Y-%m-%d') > date_format(endTime,'%Y-%m-%d') and useFlag = 0) then '3' when useFlag = 0 then '0' when useFlag = 1 then '1' when useFlag = 2 then '2' end as i"
				+ ",consumeTime from redenvelopedetail where userId=? " 
						+ " order by i,endTime,money desc");
		String sqlCount = "select count(1) from redenvelopedetail where userId=? ";
		page.setTotalCount(dao.queryNumberSql(sqlCount, id).intValue()); // 设置总记录数
		sql.append(" limit " + page.getNumPerPage() * (page.getPageNum() - 1))
				.append(",").append(page.getNumPerPage());
		List list = dao.findBySql(sql.toString(), id);
		page.setList(list);
		return null;
	}
	
	/** 2015.9.8-2015.10.8活动抽奖相关逻辑 
	 *  奖品信息：1、往返机票(1)；2、月饼（价值75元）（20）；3、乐心血压计（5）；4、萤石智能家居套装（5）；5、麦开水杯（5）；6、体脂称（5）；
		7、5元红包（不限量，不中其他奖时必中）；8、10元红包（100）
		0:未中奖；-1:抽奖活动尚未开始; -2:抽奖活动已经结束;-3:无抽奖机会； */
	public int getLotteryResult(Date now, long userId) {
		int result = 0;
		String currentTime = DateFormatUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss");
		String currentDate = currentTime.substring(0, 10);
		/** 先抽实物奖品，再抽红包 */
		String hour = currentTime.substring(11, 13);//小时
		int nowHour = Integer.valueOf(hour);
		if(nowHour >= 6 && nowHour <= 22) {
		/** 随机设置实物奖品发放的时间 */
			if(nowHour >= HcSeptemberActivitiCache.getSeptemberMaterialGiveTime(currentTime)) {
				result = materialLottery(userId, currentDate);
			}
		}
				
		/** 没抽中实物奖品再抽红包 */
		if(result < 1) {
			result = tenRedEnvelopeLottery(userId, now);
		}
		
		if(result < 1) {
			result = fiveRedEnvelopeLottery(userId, now);
		}
		return result;
	}
	
	/** 10元红包抽奖逻辑 */
	private int tenRedEnvelopeLottery(long userId, Date now) {
		int prizeType = 0;
		String currentTime = DateFormatUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss");
		String currentDate = currentTime.substring(0, 10);
		
		boolean giveFlag = false;
		/** 1天至少发出3个百元红包 */
		int todayNum = HcSeptemberActivitiCache.getTodayOneHounderdRedNum(currentDate);
		String tenConcurrentLock = "STR:RED:TEN:ENVELOP:CONCURRENT:LOCK";
		if(!RedisHelper.isKeyExistSetWithExpire(tenConcurrentLock, 1 * 60)) {
			if(currentDate.equals(HcSeptemberActivitiCache.getActiveEndDate())){
				giveFlag = true;
			} else {
				if(todayNum < 3) {
					giveFlag = true;
				}
			}
		}
		
		if(giveFlag) {
			int haveGiveNum = HcSeptemberActivitiCache.getOneHounderdRedNum();
			if(haveGiveNum < 100) {//最多发放100个10元红包
				String concurrentLock = "STR:RED:ONE:HOUNDERD:CONCURRENT:LOCK:" + userId;
				int lockSeconds = 60 * 60 * 5;
				if(!RedisHelper.isKeyExistSetWithExpire(concurrentLock, lockSeconds)) {
					saveRedEnvelopeDetail(userId, now, 10.0);
					prizeType = 8;
					HcSeptemberActivitiCache.increaneOneHounderdRedNum();
					HcSeptemberActivitiCache.increaseTodayOneHounderdRedNum(currentDate);
				}
			}
		}
		return prizeType;
	}
	
	/** 5元红包抽奖逻辑 */
	private int fiveRedEnvelopeLottery(long userId, Date now) {
		saveRedEnvelopeDetail(userId, now, 5.0);
		return 7;
	}
	
	/** 保存红包信息至数据库 */
	private void saveRedEnvelopeDetail(long userId, Date now, Double money) {
		/** 最近一次发放红包奖品的时间 */
		String lotteryTime = DateFormatUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss");
		
		Userbasicsinfo userbasicsinfo = new Userbasicsinfo();
		userbasicsinfo.setId(userId);
		RedEnvelopeDetail redEnvelopeDetail = new RedEnvelopeDetail();
		redEnvelopeDetail.setUserbasicsinfo(userbasicsinfo);
		redEnvelopeDetail.setMoney(money);
		redEnvelopeDetail.setLowestUseMoney(0.0);
		redEnvelopeDetail.setReceiveTime(lotteryTime);
		redEnvelopeDetail.setBeginTime(lotteryTime.substring(0, 10));
		Date endTime = DateFormatUtil.increaseDay(now, 30);
		redEnvelopeDetail.setEndTime(DateFormatUtil.dateToString(endTime, "yyyy-MM-dd"));
		redEnvelopeDetail.setUseFlag(0);
		redEnvelopeDetail.setSourceType(4);
		dao.save(redEnvelopeDetail);
	}
	
	/** 实物抽奖逻辑:3天发放4个实物奖品，最后一天发来回机票奖 */
	private int materialLottery(long userId, String currentDate) {
		int result = 0;
		/** 每人中奖机会，实物奖品最高1次:已获奖的不再参与实物奖品抽奖 */
		if(!HcSeptemberActivitiCache.isUserHaveGetMaterialPrize(userId)) {
			/** 总共发放实物奖品数量 */
			int totalNum = HcSeptemberActivitiCache.getTotalLotteryNum();
			if(totalNum < 41) {
				/** 最后一天发出所有剩余的奖品 */
				if(currentDate.equals(HcSeptemberActivitiCache.getActiveEndDate())) {
					result = lockMaterialLotter(userId, currentDate);
				} else {
					int haveGiveNum = HcSeptemberActivitiCache.getTodayMaterialGiveNum(currentDate);
					
					/** ，第十一天发机票一张，其他1天至少发出去一台，没发完的最后一天全部发完 */
					if("2015-09-21".equals(currentDate)) {
						if(totalNum < 20) {
							LOG.error("目前已经发放月饼 " + totalNum + " 盒月饼，还剩 " + (20 - totalNum) + " 盒不发放！");
							HcSeptemberActivitiCache.setHaveGiveMaterialPrizeNum();
							totalNum = HcSeptemberActivitiCache.getTotalLotteryNum();
						}
					}
					/** 月饼发放：前面10天每天发两盒月饼，如果未发完过了2015-09-20号不 */
					if(totalNum < 20) {
						if(haveGiveNum < 2) {
							result = lockMaterialLotter(userId, currentDate);
						}
					} else {
						if(haveGiveNum < 1) {
							result = lockMaterialLotter(userId, currentDate);
						}
					}
				}
			}
		}
		return result;
	}
	
	/** 实物奖品锁定逻辑
	 * 实物奖品：1：运动相机；2：体脂秤；3：冰箱卫士； */
	private int lockMaterialLotter(long userId, String currentDate) {
		/** 实物奖品并发逻辑处理 */
		String concurrentLock = "STR:MATERIAL:ONE:YEAR:CONCURRENT:LOCK";
		if(!RedisHelper.isKeyExistSetWithExpire(concurrentLock, 300)) {
			Date now = new Date();
			PrizeInfo prizeInfo = grantMaterialLottery(userId);
			if(prizeInfo != null) {
				int prizeType = Integer.valueOf(prizeInfo.getPrizeType());
				
				if(prizeType > 0) {
					PrizeDetail prizeDetail = new PrizeDetail();
					prizeDetail.setUserId(userId);
					prizeDetail.setPrizeType(prizeType);
					prizeDetail.setReceiveTime(DateFormatUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss"));
					prizeDetail.setPrizeName(prizeInfo.getPrizeName());
					dao.save(prizeDetail);
					/** 实物奖品中奖 */
					HcSeptemberActivitiCache.giveMaterialPrizeToUser(userId, "" + prizeType);
					/** 更新总实物奖品发放数量 */
					HcSeptemberActivitiCache.increaseTotalLotteryNum();
					HcSeptemberActivitiCache.increaseTodayMaterialGiveNum(currentDate);
					return prizeType;
				}
			}
		}
		
		return 0;
	}
	
	/** 实物奖品发放 */
	private PrizeInfo grantMaterialLottery(long userId) {
		/** 总共发放实物奖品数量 */
		int totalNum = HcSeptemberActivitiCache.getTotalLotteryNum();
		if(totalNum < 41) {//最多发放41个实物奖品
			List<PrizeInfo> prizeList = HcSeptemberActivitiCache.getSeptemberMaterialPrizeList();
			if(prizeList != null && prizeList.size() > 0) {
				PrizeInfo prizeInfo = prizeList.get(totalNum);
				return prizeInfo;
			}
		}
		return null;
	}
	
	/** 中奖列表相关数据 */
	public List<LotteryRank> queryLotteryRankList() {
		List<LotteryRank> resultList = HcSeptemberActivitiCache.getLotteryRankList();
		String beginDate = HcSeptemberActivitiCache.getActiveBeginDate() + " 00:00:00";
		String endDate = HcSeptemberActivitiCache.getActiveEndDate() + " 23:59:59";
		String sql = "select l.userid,l.prizetype,l.receivetime,u.phone,l.money from ";
		sql += "(select userid,prizetype,receivetime,0 as money from prizedetail union ";
		sql += "select userid,100 as prizetype,receivetime,money from redenvelopedetail where sourceType=4) as l, userrelationinfo u ";
		sql += "where l.userid=u.id ";
		sql += "and l.receivetime>='" + beginDate + "' and l.receivetime<='" + endDate + "' ";
		sql += "order by receivetime desc limit 0,100";
		if(resultList != null && resultList.size() > 20) {
			String key = "QUERY:LOTTERY:SEPTEMBER:RANK:FLAG";
			if(!RedisHelper.isKeyExistSetWithExpire(key, 5 * 60)) {
				List list = dao.findBySql(sql);
				HcSeptemberActivitiCache.saveLotteryRankList(list);
				resultList = HcSeptemberActivitiCache.getLotteryRankList();
			}
		} else {
			List list = dao.findBySql(sql);
			HcSeptemberActivitiCache.saveLotteryRankList(list);
			resultList = HcSeptemberActivitiCache.getLotteryRankList();
		}
		return resultList;
	}
	
	/**
	 * 查询当前用户的抽奖次数，如果为0，表示该用户还没有抽过奖
	 * @param userId
	 * @return
	 */
	public Integer isFirstLottery(Long userId) {
		/** 活动开始时间 */
		String beginDateStr = HcSeptemberActivitiCache.getActiveBeginDate();
		/** 活动结束时间 */
		String endDateStr = HcSeptemberActivitiCache.getActiveEndDate();
		StringBuffer buf = new StringBuffer();
		buf.append("select (select count(1) from redenvelopedetail where userId=? and sourceType=4 and ");
		buf.append("receivetime >= '"+beginDateStr+"' and receivetime <= '"+endDateStr+"'),");
		buf.append("(select count(1) from prizedetail where userId=? and receivetime >= '"+beginDateStr+"' and receivetime <= '"+endDateStr+"') from dual");
		Object obj = dao.findObjectBySql(buf.toString(), userId , userId);
		Object[] objs = obj != null ? (Object[])obj : null;
		return objs != null ? Integer.valueOf(objs[0].toString()) + Integer.valueOf(objs[1].toString()) : 0;
	}

	public PageModel queryNewYearMonkeyRecord(PageModel page, Long userId) {
		StringBuffer sql = new StringBuffer("select type,rewardMoney,createTime,status,userId,byUserId");
		sql.append(" from activity_monkey a where a.userId=? and examineStatus!=9");
		String sqlCount = "select count(1) from activity_monkey where userId=? and examineStatus!=9";
		page.setTotalCount(dao.queryNumberSql(sqlCount,userId).intValue()); // 设置总记录数
		sql.append(" order by createTime desc limit " + page.getNumPerPage() * (page.getPageNum() - 1))
				.append(",").append(page.getNumPerPage());
		List list = dao.findBySql(sql.toString(),userId);
		page.setList(list);
		return page;
	}

	public List queryH5NewYearMonkeyRecord(Long userId) {
		StringBuffer sql = new StringBuffer("select type,rewardMoney,createTime,status,loanName from activity_monkey a where a.type != 9 and a.userId=? order by createTime desc");
		return dao.findBySql(sql.toString(),userId);
	}
	
	/**
	 * 获取红筹达人1-3名的手机号
	 * @return obj
	 */
	public Object queryHongChouEredarTopThree() {
		StringBuffer sql = new StringBuffer("select ");
		sql.append("(select mobilePhone from activity_monkey where type =6),");
		sql.append("(select mobilePhone from activity_monkey where type =7),");
		sql.append("(select mobilePhone from activity_monkey where type =8)");
		sql.append(" from dual");
		Object obj = null;
		try {
			obj = dao.findObjectBySql(sql.toString());
		} catch (Exception e) {
			LOG.error("获取红筹达人信息时出现异常");
			e.printStackTrace();
		}
		return obj;
	}
	
	/**
	 * 查询金桃朵朵开推荐排行信息
	 * @return list
	 */
	public List<Object[]> queryGoldPeachBillBoard(Long userId) {
		StringBuffer sql = new StringBuffer("SELECT (SELECT phone FROM userrelationinfo WHERE user_id=g.genuid) as phone,COUNT(1) as num,max(u2.createTime) as time");
		sql.append(" FROM generalize g,userbasicsinfo u2,cardimgaudit ca WHERE g.uid=u2.id AND u2.id=ca.userId AND g.genuid in");
		sql.append(" (SELECT id FROM userbasicsinfo) AND ca.cardImgState=1 AND date_format(u2.createTime,'%Y-%m-%d') >='"+HcPeachActivitiCache.getActiveBeginDate()+"'");
		sql.append(" AND date_format(u2.createTime,'%Y-%m-%d') <='"+HcPeachActivitiCache.getActiveEndDate()+"' AND g.state=2 ");
		if (userId != null) {
			sql.append(" AND g.genuid="+userId);
		} else {
			sql.append(" GROUP BY g.genuid ORDER BY num desc,time limit 0,10");
		}
		List<Object[]> billboards = null;
		try {
			billboards = dao.findBySql(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return billboards;
	}
	
	/**
	 * 查询当前用户是否满足现金奖励活动，满足返回相应的活动规则提示结果
	 * @param userId
	 * @return message
	 */
	public String cashRewardStandard(Long userId) {
		String message = "";
		String keyUser = "NEWYEAR:INVEST:FINANCIAL:USER" + userId;
		Map<String,String> recommend = IndexDataCache.getObject(keyUser);
		if (recommend != null) {
			Double tenderMoney = 0D;
			Integer state = 0; 
			for (int i = 0; i < recommend.size(); i++) {
				tenderMoney = Double.valueOf(recommend.get("money"));
				state = Integer.valueOf(recommend.get("static"));
			}
			if (tenderMoney != 0 && tenderMoney != null && state != 0) {
				if (tenderMoney == 188) { // 累计推荐好友投资10万及以上可得188
					message = "10万送188元现金奖励的标准";
				} else if (tenderMoney == 388) { // 累计推荐好友投资20万及以上可得388
					message = "20万送388元现金奖励的标准";
				} else if (tenderMoney == 888) { // 累计推荐好友投资50万及以上可得888
					message = "50万送888元现金奖励的标准";
				} else if (tenderMoney == 1888) { // 累计推荐好友投资100万及以上可得1888
					message = "100万送1888元现金奖励的标准";
				}
				Map<String,String> map = new HashMap<String, String>();
				map.put("money", tenderMoney.toString());
				map.put("static", "0");  // 将map中的static设置为0表示已提醒过用户
				IndexDataCache.set(keyUser, map);
			}
		}
		return message;
	}

	/**
	 * 插入抽奖记录
	 * @param user 推荐人
	 * @param byUser 被推荐人(可以是自己)
	 * @return
	 */
	public void lotteryPeach(Userbasicsinfo user,Userbasicsinfo byUser) {
		Long userId = user.getId();
		String phone = user.getUserrelationinfo().getPhone();
		String createTime=DateUtils.format("yyyy-MM-dd HH:mm:ss");
		ActivityMonkey activityMonkey = new ActivityMonkey();
		try {
			activityMonkey.setUserId(userId);
			activityMonkey.setMobilePhone(phone);
			activityMonkey.setType(10);
			activityMonkey.setMoney(0D);
			activityMonkey.setRewardMoney(2D);
			activityMonkey.setCreateTime(createTime);
			activityMonkey.setStatus(0); // 待发放
			activityMonkey.setExamineStatus(9); // 未抽奖
			activityMonkey.setByUser(byUser);  // 被推荐人
			dao.save(activityMonkey);
		} catch (Exception e) {
			LOG.error("插入抽奖记录时出现异常-->"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询金桃抽奖记录
	 * @param user 推荐人
	 */
	public ActivityMonkey getLotteryPech(Long userId) {
		String sql = "select * from activity_monkey where userId=? and type=10 and examineStatus=9 order by id limit 0,1";
		return dao.findObjectBySql(sql,ActivityMonkey.class, userId);
	}
	
	/**
	 * 更新金桃抽奖记录
	 */
	public void updateLotteryPech(Long id,String createTime) {
		String sql = "update activity_monkey set examineStatus=0,createTime='"+createTime+"' where id="+id;
		dao.executeSql(sql);
	}
	
	public boolean remindGoldPech(Long userId) {
		String sql = "select count(1) from activity_monkey where type=10 and userId=byUserId and userId=? and examineStatus!=9";
		Object obj = dao.findObjectBySql(sql, userId);
		if (obj != null) {
			if (Integer.valueOf(obj.toString()) > 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 通过“2元中奖记录以及推荐人数”判断是否存在自己注册中的奖（金桃朵朵开）
	 * @param userId
	 * @return 0：表示没有、非0：表示自己中奖记录的id
	 */
	/*public Long queryIsSelfOrRecomendData(Long userId) {
		StringBuffer buf = new StringBuffer("select ");
		buf.append("case when (select count(1) from userbasicsinfo u,userrelationinfo i where u.id=i.user_id and date_format(createTime,'%Y-%m-%d') >='"+HcPeachActivitiCache.getActiveBeginDate()+"'");
		buf.append(" and date_format(createTime,'%Y-%m-%d') <='"+HcPeachActivitiCache.getActiveEndDate()+"' and (i.cardImg is null or i.cardImg = '') and u.id="+userId+") > 0 then ");
		buf.append("(select id from activity_monkey where userId="+userId+" and type = 10 order by createTime limit 0,1) else '0' end from dual");
		Object obj = dao.findObjectBySql(buf.toString());
		Long lg = 0L;
		if (obj != null) {
			lg = Long.valueOf(obj.toString());
		}
		return lg;
	}*/
	
}
