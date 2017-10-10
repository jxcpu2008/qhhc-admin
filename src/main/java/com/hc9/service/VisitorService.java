package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.RedisUtil;
import com.hc9.dao.entity.City;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.dao.entity.Province;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Userfundinfo;
import com.hc9.dao.entity.Userrelationinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.LotteryRank;

/** 前台首页service */
@Service
@SuppressWarnings(value = { "visitorservice" })
public class VisitorService {

	/** 注入HibernateSupport */
	@Resource
	HibernateSupport commondao;

	@Resource
	CityInfoService cityInfoService;

	/** 添加省 */
	public void putProductInfoRightCity(HttpServletRequest request) {
		// 查询到所有的省--frank
		List<Province> provinceList = cityInfoService.queryAllProvince();
		List<City> cityList = cityInfoService.queryCityByProvince(1);
		request.setAttribute("provinceList", provinceList);
		request.setAttribute("cityList", cityList);

	}

	/**
	 * 用户登陆 
	 * @param userName 用户名
	 * @param password 用户密码
	 * @return Userbasicsinfo
	 */
	public Userbasicsinfo login(String userName, String password) {
		String hql = "from Userbasicsinfo where userName = '" + userName + "' and password = '" + password + "'";
		List<Userbasicsinfo> userlist = commondao.find(hql);
		if (userlist.size() > 0) {
			return userlist.get(0);
		}
		return null;
	}

	/**
	 * 验证用户的登陆名是否重复 
	 * @param userName 用户名
	 * @return boolean
	 */
	public boolean checkUserName(String userName) {
		String hql = "from Userbasicsinfo where userName = '" + userName + "'";
		int size = commondao.find(hql).size();
		if (size > 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 验证用户注册邮箱是否重复 
	 * @param email  邮箱
	 * @return boolean
	 */
	public boolean checkUserEmail(String email) {
		String hql = "from Userrelationinfo where email = '" + email + "'";
		int size = commondao.find(hql).size();
		if (size > 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 根据会员身份证号码查询会员基本信息
	 * @param cardId 身份证号码
	 * @return Userbasicsinfo
	 */
	public Userbasicsinfo getUserbasicsinfo(String cardId) {
		StringBuffer hql = new StringBuffer("from Userbasicsinfo u where u.userrelationinfo=").append(cardId);
		List userbaseicsList = commondao.find(hql.toString());
		if (userbaseicsList.size() > 0) {
			return (Userbasicsinfo) userbaseicsList.get(0);
		}
		return null;
	}

	/**
	 * 根据基本信息编号查询用户的资金信息 
	 * @param userBasicsId 用户基本信息编号
	 * @return Userfundinfo
	 */
	public Userfundinfo getuserUserfundinfo(Long userBasicsId) {
		StringBuffer hqlUser = new StringBuffer(
				"from Userfundinfo u where u.userbasicsinfo=").append(userBasicsId);

		List<Userfundinfo> userFundInfo = (List<Userfundinfo>) commondao.find(hqlUser.toString());

		if (userFundInfo.size() > 0) {
			return userFundInfo.get(0);
		}
		return null;
	}

	public Loansign getloan(HttpServletRequest request, String loanid) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);
		String sql = " select * from loansign where   id=? and userbasicinfo_id=?";
		Loansign ls = commondao.findBySql(sql, Loansign.class, loanid,
				user.getId()).size() > 0 ? commondao.findBySql(sql,
				Loansign.class, loanid, user.getId()).get(0) : null;
		return ls != null ? ls : null;
	}

	public Loansignbasics getloanbasics(Long id) {
		return commondao.get(Loansignbasics.class, id) != null ? commondao.get(
				Loansignbasics.class, id) : null;
	}

	public boolean getPerfectState(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession().getAttribute(Constant.SESSION_USER);

		boolean isPer = false;
		String sql = " select * from loansignbasics lbs,loansign ls where lbs.id=ls.id and ls.plable is not NULL and ls.person is not null and ls.isUp is not null and ls.`month` is not null "
				+ " and ls.remark is not null and ls.address is not null and ls.haveOther is not null and lbs.issueLoan is not null and lbs.businessIntro is not null "
				+ " and lbs.teamsIntro is not NULL and lbs.history is not null and lbs.futurePlan is not null and lbs.projectAtt1 is not null  and lbs.projectAtt2 is not null "
				+ " and lbs.remonth is not null and lbs.validity is not null AND lbs.sandwich is not null and lbs.afterBad is not NULL and lbs.priority is not NULL "
				+ " and lbs.srate is not null and lbs.arate is not NULL and lbs.prate is not NULL and lbs.loanUnit is not null and lbs.outDay is not null "
				+ " and lbs.theEscrow is not null and lbs.yearate is not NULL and lbs.refunway is not NULL and ls.userbasicinfo_id=? and ls.state=0 ";
		isPer = commondao.findBySql(sql, Loansign.class, user.getId()).size() > 0 ? true : false;
		return isPer;
	}

	public void updateUserbasic(Userbasicsinfo user) {
		if (user.getId() != null) {
			commondao.update(user);
		}
	}

	/** 上传身份证图片路径  */
	public void updateUserIDCardImg(Userrelationinfo urinfo) {
		commondao.update(urinfo);
	}

	/** 服务协议 */
	public Object getAgreeMent() {
		String sql = "select pageHTML from deputysection where name='前海红筹网站服务协议'";
		Object obj = commondao.findObjectBySql(sql.toString());
		return obj;
	}

	public Object getPayProtocol() {
		String sql = "select pageHTML from deputysection where name='前海红筹网站支付协议'";
		Object obj = commondao.findObjectBySql(sql.toString());
		return obj;
	}

	public Object getContant() {
		String sql = "select pageHTML from deputysection where name='众持项目之借款及服务协议'";
		Object obj = commondao.findObjectBySql(sql.toString());
		return obj;
	}

	/** 投资排行榜相关数据 */
	public List<LotteryRank> queryInvestRankList() {
		/** 投资排行榜最迟10分钟更新一次 */
		String key = "QUERY:INVEST:RANK:FLAG";
		if(!RedisHelper.isKeyExistSetWithExpire(key, 10 * 60)) {
			String beginDate = RedisUtil.getActiveBeginDate() + " 00:00:00";
			String endDate = RedisUtil.getActiveEndDate() + " 23:59:59";
			
			String sql = "SELECT SUM(lr.tenderMoney),u.userName from loanrecord lr LEFT JOIN userbasicsinfo u ON lr.userbasicinfo_id =u.id where lr.isSucceed=1 and lr.tenderTime>='" + beginDate + "' and lr.tenderTime<='" + endDate + "' "
					+ " GROUP BY lr.userbasicinfo_id ORDER BY sum(lr.tenderMoney) DESC LIMIT 0,10 ";
			List list = commondao.findBySql(sql);
			RedisUtil.saveInvestRankList(list);
		}
		
		return RedisUtil.getInvestRankList();
	}

	/** 推荐排行榜 */
	public List<LotteryRank> queryRecommendRankList() {
		/** 推介排行榜最迟10分钟更新一次 */
		String key = "QUERY:RECOMMEND:RANK:FLAG";
		if(!RedisHelper.isKeyExistSetWithExpire(key, 10 * 60)) {
			String beginDate = RedisUtil.getActiveBeginDate() + " 00:00:00";
			String endDate = RedisUtil.getActiveEndDate() + " 23:59:59";
			String sql = "select sum(lr.tenderMoney),lr.userbasicinfo_id,u.userName "
					+ " from generalize g JOIN userbasicsinfo u ON u.id=g.genuid JOIN loanrecord lr ON g.uid=lr.userbasicinfo_id " 
					+ " where lr.isSucceed=1 "
					+ " and lr.tenderTime>='" + beginDate + "' and lr.tenderTime<='" + endDate + "' " 
					+ " GROUP BY u.id ORDER BY sum(lr.tenderMoney) DESC LIMIT 0,10";
			List list = commondao.findBySql(sql);
			RedisUtil.saveRecommendInvestList(list);
		}
		return RedisUtil.getRecommendInvestList();
	}
	
	/** 获取实物记录 */
	public List<LotteryRank> queryMaterialLotteryRecord() {
		/** 实物奖品最迟：3分钟更新一次 */
		String key = "QUERY:MATERIAL:LOTTERY:FLAG";
		if(!RedisHelper.isKeyExistSetWithExpire(key, 3 * 60)) {
			String sql = "select p.prizeType,u.userName,t.phone from prizedetail p,userbasicsinfo u,userrelationinfo t ";
			sql += "where p.userId=u.id and u.id=t.user_id";
			List list = commondao.findBySql(sql);
			RedisUtil.saveMaterialRankList(list);
		}
		
		return RedisUtil.getMaterialRankList();
	}
	
	/** 获取红包记录 */
	public List<LotteryRank> queryRedEnvelopeLotterRecord() {
		/** 红包奖品：1分钟更新一次 */
		String key = "QUERY:REDENVELOPE:LOTTERY:FLAG";
		if(!RedisHelper.isKeyExistSetWithExpire(key, 60)) {
			String sql = "select r.money,u.userName,t.phone "
					+ " from redenvelopedetail r,userbasicsinfo u,userrelationinfo t "
					+ " where r.userId=u.id and u.id=t.user_id and r.sourceType=4 order by r.id desc limit 0,100 ";
			List list = commondao.findBySql(sql);
			RedisUtil.saveRedEnvelopeRankList(list);
		}
		
		return RedisUtil.getRedEnvelopeRankList();
	}
}