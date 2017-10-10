package com.hc9.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.redis.RedisHelper;
import com.hc9.common.redis.sys.vo.LoanDynamicVo;
import com.hc9.common.redis.sys.web.WebCacheManagerUtil;
import com.hc9.common.util.Arith;
import com.hc9.common.util.MoneyUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.UserInfoQuery;
import com.hc9.dao.entity.Attachment;
import com.hc9.dao.entity.Automatic;
import com.hc9.dao.entity.Borrowersbase;
import com.hc9.dao.entity.City;
import com.hc9.dao.entity.Industry;
import com.hc9.dao.entity.Loandynamic;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.LoansignCollected;
import com.hc9.dao.entity.Loansignbasics;
import com.hc9.dao.entity.Province;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.LoanContract;
import com.hc9.model.LoanRecommendVo;
import com.hc9.model.LoanlistVo;
import com.hc9.model.PageModel;

/**
 * 借款标详细信息
 * 
 * @author frank
 * 
 */
@Service
public class LoanInfoService {

	@Resource
	private HibernateSupport dao;

	@Resource
	private UserInfoQuery userInfoQuery;

	@Resource
	private BaseLoansignService baseLoansignService;

	@Resource
	private LoanSignQuery loanSignQuery;
	/**
	 * 查询并判断用户的信用等级
	 * 
	 * @param userid
	 *            用户编号
	 * @return 返回用户信用等级
	 */
	public Integer getCreditRating(Long userid) {
		Integer creditRating = 0;
		// 查询用户的总积分
		String hql = "from Borrowersbase b where b.userbasicsinfo.id=?";
		Borrowersbase base = (Borrowersbase) dao.findObject(hql, userid);
		// 得到用户的总积分
		Integer suminte = 0;
		if (null != base.getSuminte()) {
			suminte = base.getSuminte();
		}
		// 判断用户的信用等级
		if (Constant.STATUES_ONE <= suminte && Constant.SRSRUES_TEN >= suminte) {
			creditRating = Constant.STATUES_ONE; // 表示半颗星
		} else if (11 <= suminte && suminte <= 20) {
			creditRating = Constant.STATUES_TWO; // 表示一颗星
		} else if (21 <= suminte && suminte <= 30) {
			creditRating = Constant.STATUES_THERE; // 表示一颗半星
		} else if (31 <= suminte && suminte <= 40) {
			creditRating = Constant.STATUES_FOUR; // 表示二颗星
		} else if (41 <= suminte && suminte <= 50) {
			creditRating = Constant.STATUES_FIVE; // 表示二颗半星
		} else if (51 <= suminte && suminte <= 60) {
			creditRating = Constant.STATUES_SIX; // 表示三颗星
		} else if (61 <= suminte && suminte <= 80) {
			creditRating = Constant.STATUES_SEVEN; // 表示三颗半星
		} else if (81 <= suminte && suminte <= 110) {
			creditRating = Constant.STATUES_EIGHT; // 表示四颗星
		} else if (111 <= suminte && suminte <= 180) {
			creditRating = Constant.STATUES_NINE; // 表示四颗半星
		} else if (suminte > 180) {
			creditRating = Constant.SRSRUES_TEN; // 表示五颗星
		} else {
			creditRating = Constant.STATUES_ZERO; // 表示零颗星
		}

		return creditRating;
	}

	/**
	 * 查询一个标的所有认购记录
	 * 
	 * @param id
	 *            标编号
	 * @return 返回查询结果
	 */
	public PageModel getLoanRecord(Long id, PageModel page) {
		String sql = "SELECT u.`name`,l.rate,r.tenderMoney,CASE WHEN r.isSucceed=1 THEN '成功' else '失败' END,date_format(r.tenderTime,'%Y-%m-%d'),u.userName,l.loanType from loanrecord r,userbasicsinfo u,loansign l where r.userbasicinfo_id=u.id AND r.loanSign_id=l.id AND r.loanSign_id=?";
		String sqls = "select count(r.id) from loanrecord r,userbasicsinfo u,loansign l where r.userbasicinfo_id=u.id AND r.loanSign_id=l.id AND r.loanSign_id=?";
		page.setTotalCount(dao.queryNumberSql(sqls, id).intValue());
		sql = sql + " LIMIT " + (page.getPageNum() - 1) * 10 + ","
				+ page.getNumPerPage() + "";
		List<Object[]> list = dao.findBySql(sql, id);
		page.setList(list);
		return page;
	}

	/**
	 * 获取当前借款标的借款人的所有借款记录
	 * 
	 * @param id
	 *            借款标编号
	 * @param pageNo
	 *            当前页数
	 * @return 返回分页内容
	 */
	public PageModel getLoanSignRecord(Long id, PageModel page) {
		// 获取当前标的信息
		Loansign loan = (Loansign) dao.findObject(
				"from Loansign l where l.id=?", id);
		String sql = "SELECT b.loanNumber,b.loanTitle,l.issueLoan,l.rate,l.refundWay,l.`month`,l.useDay FROM loansign l,loansignbasics b WHERE l.id=b.id AND l.userbasicinfo_id=? AND l.id!=?";
		String sqlCount = "SELECT count(l.id) FROM loansign l,loansignbasics b WHERE l.id=b.id AND l.userbasicinfo_id=? AND l.id!=?";
		page.setTotalCount(dao.queryNumberSql(sqlCount,
				loan.getUserbasicsinfo().getId(), id).intValue());
		sql = sql + " LIMIT " + (page.getPageNum() - 1) * 10 + ","
				+ page.getNumPerPage() + "";
		List<Object[]> list = dao.findBySql(sql, loan.getUserbasicsinfo()
				.getId(), id);
		page.setList(list);
		return page;
	}

	/**
	 * 查询该标所有的附件信息
	 * 
	 * @param id
	 *            标编号
	 * @return 返回所有标信息
	 */
	public List<Attachment> getAttachment(long id) {
		String hql = "from Attachment a where a.loansign.id=? and a.attachmentType=2";
		List<Attachment> list = dao.find(hql, id);
		return list;
	}

	/**
	 * 获取用户还能购买多少份
	 * 
	 * @param loan
	 *            标信息
	 * @param userinfo
	 *            用户信息
	 * @return 返回最大购买份数
	 */
	public Integer getCount(Loansign loan, Userbasicsinfo userinfo) {
		int maxcount = 0;
		boolean bool = userInfoQuery.isPrivilege(userinfo);
		int vip = 0;
		if (bool) {
			// vip = loan.getVipCounterparts();
		} else {
			// vip = loan.getCounterparts();
		}
		// 获取当前标的剩余份数
		double moneyLoan = baseLoansignService.sumLoanMoney(loan.getId());
		double myloan = baseLoansignService.sumMyLoanMoney(loan.getId(),
				userinfo.getId());
		if (loan.getIssueLoan() - moneyLoan > vip * loan.getLoanUnit() - myloan) {
			/*
			 * if(userinfo.getUserfundinfo().getMoney()>=vip*loan.getLoanUnit()){
			 * maxcount=vip; }else{ maxcount =
			 * Arith.div(userinfo.getUserfundinfo().getMoney(),
			 * loan.getLoanUnit()).intValue(); //maxcount=vip; }
			 */
			maxcount = (int) Arith.div(vip * loan.getLoanUnit() - myloan, loan.getLoanUnit());
		} else {
			/*
			 * if(loan.getIssueLoan()-moneyLoan>userinfo.getUserfundinfo().getMoney
			 * ()){ maxcount = Arith.div(userinfo.getUserfundinfo().getMoney(),
			 * loan.getLoanUnit()).intValue(); }else{ maxcount =
			 * Arith.div(loan.getIssueLoan()-moneyLoan,
			 * loan.getLoanUnit()).intValue(); }
			 */
			maxcount = (int) Arith.div(loan.getIssueLoan() - moneyLoan, loan.getLoanUnit());
		}
		return maxcount;
	}

	/**
	 * 记录浏览次数
	 * 
	 * @param loansign
	 */
	public void updateLoansign(Loansign loansign) {
		Long l = loansign.getLoansignbasics().getViews();
		if (l == null) {
			l = (long) 0;
			l++;
		} else {
			l++;
		}
		loansign.getLoansignbasics().setViews(l);
		dao.update(loansign);
	}

	/**
	 * 统计购买人数
	 */
	public Object getTenderCount(String pId) {
		String sql = "select count(1) from loanrecord where isSucceed=1 and  loanSign_id="
				+ pId;
		Object obj = dao.findObjectBySql(sql.toString());
		return obj;
	}

	/**
	 * 检查是否是vip
	 * 
	 * @param userinfo
	 * @return
	 */
	public boolean isVip(Userbasicsinfo userinfo) {
		boolean bool = userInfoQuery.isPrivilege(userinfo);
		return bool;
	}

	/**
	 * 查询用户是否收藏过项目
	 */
	public LoansignCollected getColloancord(String pId, String userId) {
		String sql = "select * from loansign_collected where loanSign_id="
				+ pId + " and userbasicinfo_id=" + userId;
		LoansignCollected collect = dao.findObjectBySql(sql.toString(),
				LoansignCollected.class);
		return collect;
	}

	public LoansignCollected getColloanById(String id) {
		String sql = "select * from loansign_collected where id=" + id;
		LoansignCollected collect = dao.findObjectBySql(sql.toString(),
				LoansignCollected.class);
		return collect;
	}

	/**
	 * 查询投资人数
	 * 
	 * @return
	 */
	public int getloanRecordCount() {
		String sql = "select count(*) from (select * from loanrecord l group by l.userbasicinfo_id)aa";
		int count = dao.queryNumberSql(sql).intValue();
		return count;
	}

	/**
	 * 添加收藏
	 */
	public void saveCollectRecord(LoansignCollected record) {
		dao.save(record);
	}

	/**
	 * 取消收藏
	 * 
	 * @param record
	 */
	public void deleCollectRecord(LoansignCollected record) {
		dao.delete(record);
	}

	/**
	 * 查询借款人数
	 * 
	 * @return
	 */
	public int getloanBorrowCount() {
		String sql = "select count(*) from (select * from loansign l group by l.userbasicinfo_id)aa";
		int count = dao.queryNumberSql(sql).intValue();
		return count;
	}

	public Object getloanrecord(String pId) {
		String sql = "select (select count(id) from loanrecord where loansign_id=l.id and loanType=1 and isSucceed=1), "
				+ "	(select count(id) from loanrecord where loansign_id=l.id and loanType=2 and isSucceed=1), "
				+ "	(select count(id) from loanrecord where loansign_id=l.id and loanType=3 and isSucceed=1)  "
				+ "	from loansign l where  l.id=" + pId;
		Object obj = dao.findObjectBySql(sql.toString());
		return obj;
	}

	/**
	 * 查询成交金额
	 * 
	 * @return
	 */
	public Double getloanRecordSum() {
		String sql = "select sum(tenderMoney) from loanrecord";
		Double money = dao.queryNumberSql(sql);
		return money;
	}

	/**
	 * 根据编号查询成交金额
	 * 
	 * @return
	 */
	public Double getloanRecordMoney(Long id) {
		String sql = "select sum(tenderMoney) from loanrecord where isSucceed = 1 and loanSign_id=?";
		Double money = dao.queryNumberSql(sql, id);
		return money;
	}

	public String saveLoansign(Loansign loansign, Loansignbasics lb) {
		Serializable seria = dao.save(loansign);
		loansign.setId(Long.valueOf(seria.toString()));
		loansign.setState(0);
		lb.setId(Long.valueOf(seria.toString()));
		dao.save(lb);
		return seria.toString();
	}

	public String upDateLoan(Loansign loansign) {
		dao.getSession().clear();
		dao.update(loansign);
		return loansign.getId().toString();

	}

	public Loansign queryloansign(String id) {
		return dao.get(Loansign.class, Long.parseLong(id));
	}

	public void upDateLoansign(Loansign ls) {
		dao.getSession().clear();
		dao.update(ls);
	}

	public Loansignbasics queryLoanSignBasics(String loanbasiId) {
		return dao.get(Loansignbasics.class, Long.parseLong(loanbasiId));
	}

	public void upDateLoansignBasics(Loansignbasics lb) {
		dao.getSession().clear();
		dao.update(lb);
	}

	public PageModel queryLoansignList(HttpServletRequest request,
			PageModel page, String loanName, String begin, String end,
			Integer search, Integer state) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		StringBuffer sql = new StringBuffer(
				"select ls.id,ls.name,ls.status,ls.issueloan,ls.publish_time,");
		sql.append("IFNULL((select sum(money)+sum(middleMoney)+sum(afterMoney)+sum(preRepayMoney)");
		sql.append("+sum(middlePreRepayMoney)+sum(afterPreRepayMoney) from repaymentrecord where loanSign_id = ls.id),0),");
		sql.append("IFNULL((select sum(tenderMoney) from loanrecord where isSucceed = 1 and loanSign_id=ls.id),0) ");
		sql.append("from loansign ls where ls.state=2 and ls.userbasicinfo_id=?");

		StringBuffer sqlCount = new StringBuffer(
				"select count(ls.id) from loansign ls where ls.state=2 and ls.userbasicinfo_id=");
		sqlCount.append(user.getId());
		if (StringUtil.isNotBlank(loanName)) { // 项目名称
			sql.append(" and ls.name like '%" + loanName + "%'");
			sqlCount.append(" and ls.name like '%" + loanName + "%'");
		}
		if (StringUtil.isNotBlank(begin)) { // 开始时间
			sql.append(" and date_format(ls.publish_time,'%Y-%m-%d') >= '"
					+ begin + "'");
			sqlCount.append(" and date_format(ls.publish_time,'%Y-%m-%d') >= '"
					+ begin + "'");
		}
		if (StringUtil.isNotBlank(end)) { // 结束时间
			sql.append(" and date_format(ls.publish_time,'%Y-%m-%d') <= '"
					+ end + "'");
			sqlCount.append(" and date_format(ls.publish_time,'%Y-%m-%d') <= '"
					+ end + "'");
		}
		if (search != null && !"".equals(search)) {
			sql.append(" and DATE_SUB(CURDATE(), INTERVAL " + search
					+ " MONTH) <= ls.publish_time ");
			sqlCount.append(" and DATE_SUB(CURDATE(), INTERVAL " + search
					+ " MONTH) <= ls.publish_time ");
		}
		if (state != null) {
			if (state == 1) {
				sql.append(" and ls.status=1"); // 进行中
				sqlCount.append(" and ls.status=1");
			} else if (state == 2) {
				sql.append(" and ls.status in (2,3,4,5)");// 满标中
				sqlCount.append(" and ls.status in (2,3,4,5)");
			} else if (state == 3) {
				sql.append(" and ls.status in (6,7)");// 还款中
				sqlCount.append(" and ls.status in (6,7)");
			} else if (state == 4) {
				sql.append(" and ls.status=8");// 已完成
				sqlCount.append(" and ls.status=8");
			}
		}
		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量
		sql.append(" order by ls.id desc  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());

		List list = dao.findBySql(sql.toString(), user.getId());
		page.setList(list);// Loansign集合
		return page;
	}

	public PageModel getpromanaList(PageModel page, HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		StringBuffer sql = new StringBuffer("select * ");

		StringBuffer sqlCount = new StringBuffer("select count(ls.id) ");

		StringBuffer sqlsb = new StringBuffer(
				" from loansign ls where ls.status!=0 and ls.userbasicinfo_id="
						+ user.getId());

		sqlsb.append(" order by ls.id desc,ls.state asc");

		page.setTotalCount(dao
				.queryNumberSql(sqlCount.append(sqlsb).toString()).intValue());

		sqlsb.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.STATUES_THERE).append(",")
				.append(Constant.STATUES_THERE);
		List<Loansign> list = dao.findBySql(sql.append(sqlsb).toString());

		page.setList(list);
		return page;
	}

	/**
	 * 项目动态列表
	 * 
	 */
	public List getloandynaemiclist() {
		String sql = "select id,remark, publishTime  from loandynamic o order by publishTime desc LIMIT 0,8 ";
		List<Loandynamic> list = dao.findBySql(sql.toString());
		return list;
	}

	/**
	 * 获得项目数量-根据status
	 **/
	public Object getLoanCount() {
		StringBuffer buf = new StringBuffer();
		buf.append("select (select count(1) from loansign where status!=0 and  status!=9 ),");
		buf.append("(select count(1) from loansign where status in (1,2,3,4,5)),");
		buf.append("(select count(1) from loansign where status=8),");
		buf.append("(select count(1) from loansign where status in (6,7)) from dual");
		return dao.findObjectBySql(buf.toString());
	}

	public Loansign getloansign(HttpServletRequest request, String loanid) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		String sql = " select * from loansign where id=?";
		List list = dao.findBySql(sql, Loansign.class, loanid);
		Loansign ls = (Loansign) (list.size() > 0 ? list.get(0) : null);
		return ls;
	}

	public Loansignbasics getLoanBasics(Long id) {

		return dao.get(Loansignbasics.class, id) != null ? dao.get(
				Loansignbasics.class, id) : null;
	}

	/**
	 * 根据标ID 删除标信息
	 * 
	 * @param id
	 * @return
	 */
	public int deleteLoansignByloansignId(String id) {
		try {
			dao.delete(Long.valueOf(id), Loansign.class);
		} catch (Exception e) {
			throw e;
		}
		return 1;
	}

	/**
	 * 删除项目投资记录 by id
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteLoanRecordById(Long id) {
		try {
			dao.delete(id, Loanrecord.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public PageModel getCollectLoan(PageModel page, String userId) {
		StringBuffer sql = new StringBuffer(
				"select lc.id,lc.loanSign_id,ls.`name`,ls.create_time,lbs.loanimg "
						+ "from loansign_collected lc ,loansign ls,loansignbasics lbs "
						+ "where lc.loanSign_id=ls.id and lc.loanSign_id=lbs.id and   lc.userbasicinfo_id=?");
		StringBuffer sqlCount = new StringBuffer(
				"select count(*) from loansign_collected lc ,loansign ls,loansignbasics lbs "
						+ "where lc.loanSign_id=ls.id and lc.loanSign_id=lbs.id and   lc.userbasicinfo_id=?");
		sql.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(',')
				.append(page.getNumPerPage());
		page.setTotalCount(dao.queryNumberSql(sqlCount.toString(), userId)
				.intValue());// 设置总条数量
		List<Loansign> loanlist = dao.findBySql(sql.toString(), userId);
		page.setList(loanlist);
		return page;
	}

	// 删除收藏

	public void deleCollect(LoansignCollected lc) {

		dao.delete(lc);

	}

	public List<Province> getProvinceList() {
		String sql = "from Province";
		List<Province> list = dao.find(sql);
		return list;
	}

	public List<City> getProvinceList2(String pId) {

		String sql = "from City as c where  c.province.id=" + pId;
		List<City> list = dao.find(sql);
		return list;
	}

	// 项目行业
	public List<Industry> getIndustryList() {
		String sql = "from Industry";
		List<Industry> list = dao.find(sql);
		return list;
	}

	public Loansign getLoansignIsTopOne() {
		String sql = "select * from loansign where status = 1 order by recommend desc,publish_time desc limit 0,1";
		List<Loansign> loanList = dao.findBySql(sql, Loansign.class);
		return loanList.size() == 0 ? null : loanList.get(0);
	}

	// 获得自动投标列表
	public PageModel getAutoBidList(PageModel page, Long userId) {
		StringBuffer sql = new StringBuffer(
				"select * from automatic where userbasicinfo_id=?");
		StringBuffer sqlCount = new StringBuffer(
				"select count(*) from automatic where userbasicinfo_id=?");
		sql.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(',')
				.append(page.getNumPerPage());
		page.setTotalCount(dao.queryNumberSql(sqlCount.toString(), userId)
				.intValue());// 设置总条数量
		List<Automatic> autoList = dao.findBySql(sql.toString(),
				Automatic.class, userId);
		page.setList(autoList);
		return page;
	}

	// 获得自动投标 by Id
	public Automatic getAutomatic(Long id) {
		return dao.load(Automatic.class, id);
	}

	public void updateAutomatic(Automatic auto) {
		dao.update(auto);
	}

	/**
	 * 展示在首页的标信息
	 * 
	 * @return
	 */
	public List<Object[]> getLoansignlist() {
		String sql = "select ls.id, ls.name,ls.remonth,ls.issueLoan,ls.loanUnit,ls.rest_money,ls.prio_rate,ls.prio_aword_rate,ls.type,ls.status,ls.loansignType_id,ls.activityStatus  "
				+ " from loansign ls where ls.state=2 and (ls.status >0 and ls.status <9) and (ls.onIndex =1 or ls.recommend =1) "
				+ " ORDER BY ls.rest_money DESC, ls.publish_time DESC,ls.state LIMIT 0 ,8";
		List<Object[]> list = dao.findBySql(sql);
		return list;
	}
	
	/**
	 * 查询首页的标列表信息 并缓存
	 * @param key
	 * @return
	 */
	public List<LoanlistVo> updateLoanlist() {
		List<LoanlistVo> loanList = new ArrayList<>();
		List<Object[]> list = getLoansignlist();
		
		if(list == null || list.size() <= 0){
			return loanList;
		}
		
		for(int i=0;i<list.size();i++){
			LoanlistVo vo=new LoanlistVo();
			vo.setId(list.get(i)[0].toString());
			vo.setName(list.get(i)[1].toString());
			vo.setRemonth(list.get(i)[2].toString());
			vo.setIssueLoan(list.get(i)[3].toString());
			vo.setLoanUnit(list.get(i)[4].toString());
			vo.setRestMoney(list.get(i)[5].toString());
			vo.setPrioRate(list.get(i)[6].toString());
			vo.setPrioAwordRate(list.get(i)[7].toString());
			vo.setType(list.get(i)[8].toString());
			vo.setStatus(list.get(i)[9].toString());
			vo.setTypeId(list.get(i)[10].toString());
			vo.setActivityStatus(list.get(i)[11]==null?"0":list.get(i)[11].toString());
			loanList.add(vo);
		}
		WebCacheManagerUtil.setWebIndexLoanListToRedis(loanList);
		return loanList;
	}

	/**
	 * 项目动态
	 * 
	 * @return
	 */
	public List<Loandynamic> getLoanLoandynamic() {
		String sql = "SELECT * FROM loandynamic ld order by ld.publishTime desc ";
		List<Loandynamic> list = dao.findBySql(sql, Loandynamic.class);
		if(list != null && list.size() > 0) {
			List<LoanDynamicVo> dynamicList = new ArrayList<LoanDynamicVo>();
			for(Loandynamic dynamic : list) {
				LoanDynamicVo vo = new LoanDynamicVo();
				vo.setLoanId(dynamic.getLoanId());
				vo.setTitle(dynamic.getTitle());
				dynamicList.add(vo);
			}
			WebCacheManagerUtil.setWebLoanDynamicListToRedis(dynamicList);
		}
		return list;
	}

	/**
	 * 热门推荐
	 * 
	 * @return
	 */
	public List<LoanRecommendVo> getRecommand() {
		List<LoanRecommendVo> recommandList = new ArrayList<LoanRecommendVo>();
		String sql = "SELECT ls.id,ls.`name`,lb.recommandReason,lb.riskAdvice,ls.loansignType_id FROM loansign ls JOIN loansignbasics lb ON ls.id=lb.id WHERE ls.state=2 and (ls.status >0 and ls.status <9) and ls.recommend=1 LIMIT 0,2";
		List<Object[]> list = dao.findBySql(sql);
		if(null == list || list.size() <= 0){
			return recommandList;
		}
		for(int i=0;i<list.size();i++){
			LoanRecommendVo vo=new LoanRecommendVo();
			vo.setId(list.get(i)[0].toString());
			vo.setName(list.get(i)[1].toString());
			vo.setReason(list.get(i)[2]!=null?list.get(i)[2].toString():"");
			vo.setAdvice(list.get(i)[3]!=null?(list.get(i)[3].toString()):"");
			vo.setTypeId(list.get(i)[4]!=null?(list.get(i)[4].toString()):"");
			recommandList.add(vo);
		}
		WebCacheManagerUtil.setWebRecommandLoanListToRedis(recommandList);
		return recommandList;
	}

	public Double queryFinancSumByUser(Long user_id) {
		String sql = "select sum(issueLoan) from loansign where userbasicinfo_id = ? and state = 2";
		Object sum_mon = dao.findObjectBySql(sql, user_id);
		return sum_mon != null ? Double.valueOf(sum_mon.toString()) : 0D;
	}
	/**
	 * 累计投资金额
	 * @return
	 */
	public String gettotalInvestment(String key) {
		String sql="SELECT SUM(tenderMoney) FROM loanrecord WHERE isSucceed=1";
		String value=String.valueOf(dao.findObjectBySql(sql, null));
		RedisHelper.set(key, value);
		return value;
	}
	
	/**
	 * 累计投资收益金额
	 * @return
	 */
	public String gettotalIncome(String key) {
		String sql = "select sum((select SUM(ifnull(rmp.realMoney,0)-IFNULL(rmp.money,0))+SUM(IFNULL(rmp.middleRealMoney,0)-IFNULL(rmp.middleMoney,0))+SUM(IFNULL(rmp.afterRealMoney,0)-IFNULL(rmp.afterMoney,0))"
				+ "+IFNULL((select sum(IFNULL(incomeMoney,0)) from voteincome where status= 0),0) "
				+ " from repaymentrecord repm,repaymentrecordparticulars rmp where repm.id=rmp.repaymentrecordId and rmp.loanrecordId=lr.id and rmp.repState=1)) "
				+ "from loanrecord lr ,loansign ls where ls.id=lr.loanSign_id and lr.isSucceed=1 and ls.`status`in(6,7,8)";
		String value=String.valueOf(dao.findObjectBySql(sql, null));
		RedisHelper.set(key, value);
		return value;
	}
	
	/**
	 * H5累计投资收益金额
	 * @return
	 */
	public String getH5TotalIncome(String key) {
		String sql = "select (select sum(case when repayState in (1,3) then preRepayMoney when realMoney >= money then realMoney-money else realMoney end) "
					+ "+sum(case when repayState in (1,3) then middlePreRepayMoney when middleRealMoney >= middleMoney then middleRealMoney-middleMoney else middleRealMoney end)"
					+ "+sum(case when repayState in (1,3) then afterPreRepayMoney when afterRealMoney >= afterMoney then afterRealMoney-afterMoney else afterRealMoney end) from repaymentrecord) "
					+ "+(select sum(incomeMoney) FROM voteincome)";
		String value=String.valueOf(dao.findObjectBySql(sql, null));
		RedisHelper.set(key, value);
		return value;
	}
	
	/**
	 * 累计投资金额
	 * @return
	 */
	public String gettotalIncome() {
		String sql = "select sum((select SUM(ifnull(rmp.realMoney,0)-IFNULL(rmp.money,0))+SUM(IFNULL(rmp.middleRealMoney,0)-IFNULL(rmp.middleMoney,0))+SUM(IFNULL(rmp.afterRealMoney,0)-IFNULL(rmp.afterMoney,0))"
				+ "+IFNULL((select sum(IFNULL(incomeMoney,0)) from voteincome where status= 0),0) "
				+ " from repaymentrecord repm,repaymentrecordparticulars rmp where repm.id=rmp.repaymentrecordId and rmp.loanrecordId=lr.id and rmp.repState=1)) "
				+ "from loanrecord lr ,loansign ls where ls.id=lr.loanSign_id and lr.isSucceed=1 and ls.`status`in(6,7,8)";
		return String.valueOf(dao.findObjectBySql(sql, null));
	}

	/**
	 * 合同数据封装
	 * @param user
	 * @param loansign
	 * @param loanrecord
	 * @return
	 */
	public LoanContract packageContactData(Userbasicsinfo user,Loansign loansign, Loanrecord loanrecord) {
		LoanContract loanContract=new LoanContract();
		// 出借人
		loanContract.setPartyAName(user.getName());
		loanContract.setPartyACardType("身份证");
		loanContract.setPartyACardNo(user.getUserrelationinfo().getCardId());
		loanContract.setPartyAPhone(user.getUserrelationinfo().getPhone());
		// 借款人
		Userbasicsinfo jkUser=loansign.getUserbasicsinfo();
		loanContract.setPartyBName(jkUser.getName());
		loanContract.setPartyBCardType("身份证");
		loanContract.setPartyBCardNo(jkUser.getUserrelationinfo().getCardId());
		loanContract.setPartyBPhone(jkUser.getUserrelationinfo().getPhone());

		// 投资类型
		double typeRate=0.0;
		if (loanrecord.getSubType() == 1) {
			loanContract.setSubType("优先");
			typeRate=Arith.round((loansign.getPrioRate()+loansign.getPrioAwordRate()), 3);
			if (loansign.getRefunway() == 1) {
				loanContract.setBonaType("按月付息");
			} else {
				loanContract.setBonaType("按季度");
			}
			loanContract.setBonaType(loanContract.getBonaType()+"，到期还本");
		} else if (loanrecord.getSubType() == 2) {
			loanContract.setSubType("夹层");
			typeRate=Arith.round(loansign.getMidRate(), 3);
			loanContract.setBonaType("到期还本付息");
		} else if (loanrecord.getSubType() == 3) {
			loanContract.setSubType("劣后");
			typeRate=Arith.round(loansign.getAfterRate(), 3);
			loanContract.setBonaType("到期还本付息");
		}
		loanContract.setSubTypeRate(Arith.round(Arith.mul(typeRate, 100), 2)+ "%");
		loanContract.setLoanMoney(loanrecord.getTenderMoney());
		loanContract.setLoansignId(loansign.getContractNo());
		loanContract.setContractId(loanrecord.getpContractNo());
		loanContract.setLoansignName(loansign.getName());

		String preRepayDate = loanSignQuery.getPreRepayDate(loansign.getId().toString());
		if (preRepayDate != null) {
			loanContract.setRepayYear(preRepayDate.substring(0, 4));
			loanContract.setRepayMonth(preRepayDate.substring(5, 7));
			loanContract.setRepayDay(preRepayDate.substring(8, 10));
		}
		loanContract.setCreditYear(loansign.getCreditTime().substring(0, 4));
		loanContract.setCreditMonth(loansign.getCreditTime().substring(5, 7));
		loanContract.setCreditDay(loansign.getCreditTime().substring(8, 10));
		if (loansign.getType() == 2) {
			loanContract.setBorrowMonth(loansign.getRemonth().toString()
					+ "个月(项目)");
		} else if (loansign.getType() == 3) {
			loanContract.setBorrowMonth(loansign.getRemonth().toString()
					+ "天(天标)");
		}
		loanContract.setLoanMoneyUpper(MoneyUtil.digitUppercase(loanrecord
				.getTenderMoney()));
		if (loansign.getLoansignbasics().getBehoof() != null) {
			loanContract.setBehoof(loansign.getLoansignbasics().getBehoof());
		} else {
			loanContract.setBehoof("");
		}
		return loanContract;
	}
}
