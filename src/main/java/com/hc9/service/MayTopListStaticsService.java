package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.redis.Arith;
import com.hc9.common.redis.activity.year2016.month05.HcParentCache;
import com.hc9.common.redis.activity.year2016.month05.HcWeekSurpriseCache;
import com.hc9.common.redis.activity.year2016.month05.WeekVo;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.dao.entity.MayTopListStaticsDetail;
import com.hc9.dao.impl.HibernateSupport;

/** 5月榜单列表查询服务层 */
@Service
public class MayTopListStaticsService {
	/** 注入数据库底层操作层 */
	@Resource
	private HibernateSupport dao;
	
	public List<MayTopListStaticsDetail> mayTopListStaticsPage(String name, Integer weekNum, Integer ranking) {
		List<MayTopListStaticsDetail> resultList = new ArrayList<MayTopListStaticsDetail>();
		List<WeekVo> redisList = new ArrayList<WeekVo>();
		if (weekNum == 9) {
			/** 获取总榜单 */
			redisList = HcParentCache.getParentFeedBackRankList();
		} else {
			/** 获取周榜单 */
			redisList = HcWeekSurpriseCache.getWeekRankList(weekNum);
		}

		resultList=mayTopList(redisList, weekNum, resultList);
		/** 按手机号或姓名过滤 */
		resultList = filterByNameAndPhone(resultList, name);
		/** 按名次过滤 */
		resultList = filterByRanKing(resultList, ranking);
		return resultList;
	}

	/** 按手机号或姓名过滤 */
	public List<MayTopListStaticsDetail> filterByNameAndPhone(List<MayTopListStaticsDetail> list, String nameOrPhone) {
		List<MayTopListStaticsDetail> finalList = new ArrayList<MayTopListStaticsDetail>();
		if (list != null && list.size() > 0) {
			if (nameOrPhone != null && nameOrPhone.trim().length() > 0) {
				/** 把中文按UTF-8解码 */
				String realName = "";
				try {
					realName = java.net.URLDecoder.decode(nameOrPhone, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < list.size(); i++) {
					MayTopListStaticsDetail vo = list.get(i);
					boolean flag = false;
					String name = vo.getName();
					if (name != null && name.trim().length() > 0) {
						if (name.contains(realName)) {
							flag = true;
						}
					}
					String phone = vo.getMobilePhone();
					if (phone != null && phone.trim().length() > 0) {
						if (phone.contains(nameOrPhone)) {
							flag = true;
						}
					}
					if (flag) {
						finalList.add(vo);
					}
				}
			} else {
				return list;
			}
		}
		return finalList;
	}

	/** 按名次过滤 */
	public List<MayTopListStaticsDetail> filterByRanKing(List<MayTopListStaticsDetail> list, Integer ranKing) {
		List<MayTopListStaticsDetail> finalList = new ArrayList<MayTopListStaticsDetail>();
		if (list != null && list.size() > 0) {
			if (ranKing != null && ranKing > 0) {
				for (int i = 0; i < list.size(); i++) {
					MayTopListStaticsDetail vo = list.get(i);
					boolean flag = false;
					int rank = vo.getRanking();
					if (rank == ranKing.intValue()) {
						flag = true;
					}
					if (flag) {
						finalList.add(vo);
					}
				}
			} else {
				return list;
			}
		}
		return finalList;
	}


	/** 获取全部 */
	public List<MayTopListStaticsDetail> mayTopListStaticsWholePage(Integer weekNum, String name, Integer ranking) {
		List<MayTopListStaticsDetail> resultList = new ArrayList<MayTopListStaticsDetail>();
		for (int i = 1; i <= 8; i++) {
				resultList=mayTopListStatics(i, resultList);
		}
		List<WeekVo> redisList = new ArrayList<WeekVo>();
		/** 获取总榜单 */
		redisList = HcParentCache.getParentFeedBackRankList();
		resultList=mayTopList(redisList, 9, resultList);
		/** 按手机号或姓名过滤 */
		resultList = filterByNameAndPhone(resultList, name);
		/** 按名次过滤 */
		resultList = filterByRanKing(resultList, ranking);
		return resultList;
	}
	
   /**获取每周的榜单数据*/
	public List<MayTopListStaticsDetail> mayTopListStatics(Integer weekNum, List<MayTopListStaticsDetail> resultList) {
		List<WeekVo> redisList = new ArrayList<WeekVo>();
		/** 获取周榜单 */
		redisList = HcWeekSurpriseCache.getWeekRankList(weekNum);
		resultList=mayTopList(redisList, weekNum, resultList);
		return resultList;
	}
    
	/**获取榜单数据*/
	@SuppressWarnings("rawtypes")
	public List<MayTopListStaticsDetail> mayTopList(List<WeekVo> redisList, Integer weekNum,
			List<MayTopListStaticsDetail> resultList) {
		if (redisList != null && redisList.size() > 0) {
			for (int i = 0; i < redisList.size(); i++) {
				WeekVo vo = redisList.get(i);
				MayTopListStaticsDetail detail = new MayTopListStaticsDetail();
				detail.setRanking(i + 1);
				detail.setMobilePhone(vo.getPhone());
				detail.setWeekMoney(vo.getWeekMoney());
				detail.setWeekYearMoney(vo.getWeekYearMoney());
				detail.setWeekNum(weekNum);
				if (detail.getRanking() == 1) {
					detail.setRewardMoney(Arith.round(Arith.mul(vo.getWeekYearMoney(), 0.02), 2).toString());
				} else if (detail.getRanking() == 2) {
					detail.setRewardMoney(Arith.round(Arith.mul(vo.getWeekYearMoney(), 0.015), 2).toString());
				} else if (detail.getRanking() == 3) {
					detail.setRewardMoney(Arith.round(Arith.mul(vo.getWeekYearMoney(), 0.01), 2).toString());
				} else {
					detail.setRewardMoney("100元红包（2个5元，1个30元，1个60元）+加息券0.3%（投资3000元使用）");
				}
				String sql = "SELECT u.userName,u.name,u.createTime from userbasicsinfo u where u.id=? ";
				List list = dao.findBySql(sql, vo.getUserId());
				if (list != null && list.size() > 0) {
					for (Object obj : list) {
						Object[] arr = (Object[]) obj;
						detail.setUserName(StatisticsUtil.getStringFromObject(arr[0]));
						detail.setName(StatisticsUtil.getStringFromObject(arr[1]));
						detail.setCreateTime(StatisticsUtil.getStringFromObject(arr[2]));
					}
				}
				resultList.add(detail);
			}
		}
		return resultList;
	}
	

	/** 处理5月榜单相关下载数据 */
	public List<Map<String, String>> handMayTopList(List<MayTopListStaticsDetail> mayTList) {
		List<Map<String, String>> content = new ArrayList<Map<String, String>>();
		if (mayTList != null && mayTList.size() > 0) {
			for (MayTopListStaticsDetail vo : mayTList) {
				Map<String, String> mayMap = new HashMap<String, String>();
				mayMap.put("名次", vo.getRanking() + "");
				mayMap.put("用户名", vo.getUserName());
				mayMap.put("真实姓名", vo.getName());
				mayMap.put("手机号", vo.getMobilePhone());
				mayMap.put("注册时间", vo.getCreateTime());
				mayMap.put("累计认购金额", vo.getWeekMoney() + "");
				mayMap.put("累计认购年化金额", vo.getWeekYearMoney() + "");
				mayMap.put("奖励", vo.getRewardMoney());
				int weekNum = vo.getWeekNum();
				String weekNumName = "暂无";
				if (weekNum == 1) {// 1-第一周 2-第二周 3-第三周 4-第三周 5-第五周 6-第六周 7-第七周
									// 8-第八周 9-总榜
					weekNumName = "第一周";
				} else if (weekNum == 2) {
					weekNumName = "第二周";
				} else if (weekNum == 3) {
					weekNumName = "第三周";
				}
				if (weekNum == 4) {
					weekNumName = "第三周";
				}
				if (weekNum == 5) {
					weekNumName = "第四周";
				}
				if (weekNum == 6) {
					weekNumName = "第五周";
				}
				if (weekNum == 7) {
					weekNumName = "第六周";
				}
				if (weekNum == 8) {
					weekNumName = "第八周";
				}
				if (weekNum == 9) {
					weekNumName = "总榜";
				}
				mayMap.put("获奖时间", weekNumName);
				content.add(mayMap);
			}
		}
		return content;
	}
}