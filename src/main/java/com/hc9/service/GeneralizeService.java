package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Generalize;
import com.hc9.dao.entity.Generalizemoney;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**   
 * Filename:    GeneralizeService.java   
 * Company:     前海红筹  
 * @version:    1.0   
 * @since:  JDK 1.7.0_25  
 * Create at:   2014年2月20日 上午8:38:17   
 * Description:  前台会员推广信息查询
 *   
 * Modification History:   
 * 时间    			作者   	   	版本     		描述 
 * ----------------------------------------------------------------- 
 * 2014年2月20日 	LiNing      1.0     	1.0Version   
 */

/**
 * <p>
 * Title:GeneralizeService
 * </p>
 * <p>
 * Description: 会员推广服务层
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author LiNing
 *         <p>
 *         date 2014年2月20日
 *         </p>
 */
@Service
public class GeneralizeService {

	/** 数据库操作层 */
	@Resource
	private HibernateSupport dao;

	@Resource
	private UserbasicsinfoService service;

	@Resource
	private LoanSignQuery loanSignQuery;

	@Resource
	private GeneralizeMoneyServices generalizeMoneyServices;

	/**
	 * <p>
	 * Title: queryGenlizePage
	 * </p>
	 * <p>
	 * Description: 查询会员的推广信息
	 * </p>
	 * 
	 * @param uid
	 *            会员编号
	 * @param page
	 *            分页信息
	 * @return 查询到的结果集
	 */
	public List queryGenlizePage(String uid, PageModel page) {

		String dataSql = "FROM Generalize where genuid=" + uid + " order by adddate desc";

		return dao.pageListByHql(page, dataSql, true);

	}

	/**
	 * <p>
	 * Title: queryGenlizePage
	 * </p>
	 * <p>
	 * Description: 查询会员的推广信息
	 * </p>
	 * 
	 * @param uid
	 *            会员编号
	 * @param page
	 *            分页信息
	 * @return 查询到的结果集
	 */
	public PageModel queryH5GenlizePage(PageModel page, Long id) {
		StringBuffer sql = new StringBuffer("select u.id,u.`name`,date_format(u.createTime,'%Y-%m-%d %H:%i'),");
		sql.append(" (select SUM(gm.bonuses) from generalizemoney gm where gm.refer_userid=g.genuid and gm.refered_userid = g.uid )");
		sql.append(" ,u.isAuthIps,ui.phone");
		sql.append(" from generalize g,userbasicsinfo u,userrelationinfo ui");
		sql.append(" where g.uid=u.id and g.genuid=? and u.id=ui.user_id");
		String sqlCount = "select count(1) from generalize g,userbasicsinfo u where g.uid=u.id and g.genuid=?";
		page.setTotalCount(dao.queryNumberSql(sqlCount, id).intValue());
		sql.append(" order by g.adddate desc limit " + page.getNumPerPage() * (page.getPageNum() - 1))
				.append(",").append(page.getNumPerPage());
		List list = dao.findBySql(sql.toString(), id);
		page.setList(list);
		return page;
	}

	/**
	 * <p>
	 * Title: queryGenlizePage
	 * </p>
	 * <p>
	 * Description: 查询会员的推广信息
	 * </p>
	 * 
	 * @param uid
	 *            会员编号
	 * @param page
	 *            分页信息
	 * @return 查询到的结果集
	 */
	public PageModel querygenH5MoenyPage(PageModel page, String id, String uid, int type) {
		if(type == 0){
			StringBuffer sql = new StringBuffer("select date_format(tender_time,'%Y-%m-%d %H:%i'),bonuses,release_status");
			sql.append(" from generalizemoney where release_status in (0,1) and refered_userid=? and refer_userid=?");
			String sqlCount = "select count(1) from generalizemoney where release_status in (0,1) and refered_userid=? and refer_userid=?";
			page.setTotalCount(dao.queryNumberSql(sqlCount, id, uid).intValue());
			sql.append(" order by tender_time desc limit " + page.getNumPerPage() * (page.getPageNum() - 1))
					.append(",").append(page.getNumPerPage());
			List list = dao.findBySql(sql.toString(), id, uid);
			page.setList(list);
		}else{
			StringBuffer sql = new StringBuffer("select date_format(tender_time,'%Y-%m-%d %H:%i'),bonuses,release_status");
			sql.append(" from generalizemoney where release_status in (0,1) and refer_userid=?");
			String sqlCount = "select count(1) from generalizemoney where release_status in (0,1) and refer_userid=?";
			page.setTotalCount(dao.queryNumberSql(sqlCount, id).intValue());
			sql.append(" order by tender_time desc limit " + page.getNumPerPage() * (page.getPageNum() - 1))
					.append(",").append(page.getNumPerPage());
			List list = dao.findBySql(sql.toString(), id);
			page.setList(list);
		}
		return page;
	}

	/**
	 * <p>
	 * Title: querygenMoenyPage
	 * </p>
	 * <p>
	 * Description: 查询会员奖金获得记录
	 * </p>
	 * 
	 * @param uid
	 *            会员编号
	 * @param page
	 *            分页信息
	 * @return
	 */
	public List<Generalizemoney> querygenMoenyPage(String uid, PageModel page) {

		String dataSql = "FROM Manualintegral where user_id=" + uid;

		return (List<Generalizemoney>) dao.pageListByHql(page, dataSql, true);
	}

	/**
	 * 通过被推广人id反查推广人
	 * 
	 * @param PromotedId
	 */
	public Userbasicsinfo queryPromoterByPromotedId(Long PromotedId) {
		String dataSql = "FROM Generalize where uid=?";
		List list = dao.find(dataSql, PromotedId);
		Userbasicsinfo user = null;
		Generalize gen = null;
		if (list.size() > 0) {
			gen = (Generalize) list.get(0);
			user = service.queryUserById(gen.getGenuid());
		}
		return user;
	}
	
	/** 投资过程中通过被推广人id查询推广人信息 */
	public Userbasicsinfo queryPromoterByUidForInvest(Long uid) {
		String sql = "select * from generalize where uid=? and state in(1,2)";
		List<Generalize> list = dao.findBySql(sql, Generalize.class, uid);
		Userbasicsinfo user = null;
		if(list != null && list.size() > 0) {
			Generalize gen = list.get(0);
			user = service.queryUserById(gen.getGenuid());
		}
		return user;
	}
	
	/**
	 * 根据推广人查询推广记录
	 * 
	 * @param genuid
	 * @param uid
	 * @return
	 */
	public boolean getGeneralizeIsAuthIps(Long genuid) {
		return Integer.parseInt(dao.findObjectBySql(
				"select count(1) from generalize g, userbasicsinfo u where g.uid=u.id and u.isAuthIps = 1 AND g.genuid = ?", genuid).toString()) > 0;
	}

	/**
	 * 通过推广id查询推广信息
	 * @param genuid 推广人id
	 * @param uid 被推广人id
	 * @param type 3、审核未通过
	 * @return
	 */
	public Generalize getGeneralizeByState(Long genuid,Long uid,String type) {
		String sql = "select * from generalize where genuid=? and uid=? and state in ("+type+")";
		return dao.findObjectBySql(sql,Generalize.class,genuid,uid);
	}
	
	/***
	 * 根据判断保存GeneralizeMoney
	 * 
	 * @param loanrecord
	 */
	public void saveGeneralizeMoney(Loanrecord loanrecord) {
		// 根据购买人查询Userbasicsinfo推广人
		Userbasicsinfo user = queryPromoterByPromotedId(loanrecord
				.getUserbasicsinfo().getId());
		Costratio costratio = loanSignQuery.queryCostratio();
		if (user != null) {
			if (user.getUserType() == 1) {
				generalizeMoneyServices.saveGeneralizemoney(loanrecord,
						costratio.getMember(), user.getId(),
						Constant.STATUES_TWO);
			} else if (user.getUserType() == 2) {
				generalizeMoneyServices.saveGeneralizemoney(loanrecord,
						costratio.getBusiness(), user.getId(),
						Constant.STATUES_TWO);
			}else if(user.getUserType()==4){
				generalizeMoneyServices.saveGeneralizemoney(loanrecord,
						0.015, user.getId(),Constant.STATUES_TWO);
			}
		}
		if(loanrecord.getUserbasicsinfo().getUserType()==1){
			generalizeMoneyServices.saveGeneralizemoney(loanrecord,
					costratio.getMember(), null, Constant.STATUES_ONE);
		}else if(loanrecord.getUserbasicsinfo().getUserType()==2){
			generalizeMoneyServices.saveGeneralizemoney(loanrecord,
					costratio.getBusiness(), null, Constant.STATUES_ONE);
		}else if(loanrecord.getUserbasicsinfo().getUserType()==4){
			generalizeMoneyServices.saveGeneralizemoney(loanrecord,
					0.015, null, Constant.STATUES_ONE);
		}
	}

	/**
	 * 宝付注册成功更新推广记录状态
	 * 
	 * @param user
	 */
	public void updateGeneralize(Userbasicsinfo user) {
		String sql = "SELECT * FROM generalize g WHERE g.uid=?";
		Generalize generalize = dao.findObjectBySql(sql, Generalize.class,
				user.getId());
		if (generalize != null) {
			generalize.setState(2);
			dao.update(generalize);
		}
	}
	
	/**
	 * 删除推广记录状态
	 * 
	 * @param user
	 */
	public void delGeneralize(Userbasicsinfo user) {
		String sql = "SELECT * FROM generalize g WHERE g.uid=?";
		Generalize generalize = dao.findObjectBySql(sql, Generalize.class,
				user.getId());
		if (generalize != null) {
			generalize.setState(3);
			dao.update(generalize);
		}
	}

	/**
	 * 新增推广记录
	 */
	public void addGeneralized(Generalize gen) {
		dao.save(gen);
	}
	
	/**
	 * 修改推广记录
	 */
	public void updateGeneralized(Generalize gen) {
		dao.update(gen);
	}

	/**
	 * 根据推广人和被推广人编号查询推广记录
	 * 
	 * @param genuid
	 * @param uid
	 * @return
	 */
	public boolean getGeneralize(Long uid) {
		return Integer.parseInt(dao.findObjectBySql(
				"select count(1) from generalize where uid=? and state!=3", uid).toString()) > 0;
	}

	/**
	 * 根据当前用户和被推广人编号查询是否存在推广记录
	 * 
	 * @param user_id
	 * @param uid
	 * @return
	 */
	public boolean isMutualRecommend(Long user_id, Long uid) {
		return Integer.parseInt(dao.findObjectBySql(
				"select count(1) from generalize where (genuid=? and uid=?)",
				uid, user_id).toString()) > 0;
	}

	public PageModel queryPromoteReward(String uid, PageModel page,
			String beginTime, String endTime, String search) {

		StringBuffer dataSql = new StringBuffer(
				"SELECT g.id,u.name,g.refer_userid,g.tender_money,g.bonuses,g.release_status,g.bonu_type,lr.tenderTime,ifnull(ls.`name`,'' ),g.paid_bonuses,u.name,i.phone "
						+ "from generalizemoney g, userbasicsinfo u,userrelationinfo i,loanrecord lr,loansign ls "
						+ "where u.id=g.refered_userid and u.id=i.user_id and lr.id=g.loanrecord_id and lr.loanSign_id=ls.id and g.refer_userid=? and g.release_status <> -1 ");
		StringBuffer countSql = new StringBuffer(
				"SELECT count(g.id) from generalizemoney g, userbasicsinfo u,loanrecord lr,loansign ls "
						+ "where u.id=g.refer_userid and lr.id=g.loanrecord_id and lr.loanSign_id=ls.id and g.refer_userid=? and g.release_status <> -1 ");

		if (null != beginTime && !"".equals(beginTime)) {
			dataSql.append(" and date_format(lr.tenderTime,'%Y-%m-%d')>='")
					.append(beginTime + "'");
			countSql.append(" and date_format(lr.tenderTime,'%Y-%m-%d')>='")
					.append(beginTime + "'");
		}

		if (null != endTime && !"".equals(endTime)) {
			dataSql.append(" and date_format(lr.tenderTime,'%Y-%m-%d')<='")
					.append(endTime + "'");
			countSql.append(" and date_format(lr.tenderTime,'%Y-%m-%d')<='")
					.append(endTime + "'");
		}

		if (search != null && !"".equals(search)) {
			dataSql.append(" and DATE_SUB(CURDATE(), INTERVAL "+search+" MONTH) <= lr.tenderTime ");
			countSql.append(" and DATE_SUB(CURDATE(), INTERVAL "+search+" MONTH) <= lr.tenderTime ");
		}
		page.setTotalCount(dao.queryNumberSql(countSql.toString(), uid)
				.intValue());// 设置总条数量
		dataSql.append(" order by lr.tenderTime desc LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());
		List list = dao.findBySql(dataSql.toString(), uid);

		page.setList(list);

		return page;

	}
	
	/***
	 * 佣金  0-待收  1-已收
	 * @param uid 推广人id
	 * @param beginTime
	 * @param endTime
	 * @param search
	 * @return
	 */
	public Double getPromoteReward(String uid, String beginTime, String endTime, String search,Integer payState){
		StringBuffer dataSql = new StringBuffer("select sum(IFNULL(bonuses,0)) from generalizemoney gm,loanrecord lr where gm.loanrecord_id=lr.id and pay_state=?  and refer_userid= ?");
		if (null != beginTime && !"".equals(beginTime)) {
			dataSql.append(" and date_format(lr.tenderTime,'%Y-%m-%d')>='")
					.append(beginTime + "'");
		}
		if (null != endTime && !"".equals(endTime)) {
			dataSql.append(" and date_format(lr.tenderTime,'%Y-%m-%d')<='")
					.append(endTime + "'");
		}
		if (search != null && !"".equals(search)) {
			dataSql.append(" and DATE_SUB(CURDATE(), INTERVAL "+search+" MONTH) <= lr.tenderTime ");
		}
		Double bonuses = dao.queryNumberSql(dataSql.toString(),payState, uid);
		return bonuses;
	}
	
	/**
	 * 通过通广人查询推荐人投资额和推荐人数
	 * @param uid 推广人id
	 * @param beginTime
	 * @param endTime
	 * @param search
	 * @return obj
	 */
	public Object getPromoteMoneyCount(String uid, String beginTime, String endTime, String search){
		StringBuffer dataSql = new StringBuffer("select (select count(1) from generalize where state in (1,2) and genuid="+uid);
		if (null != beginTime && !"".equals(beginTime)) {
			dataSql.append(" and date_format(adddate,'%Y-%m-%d')>='")
			.append(beginTime + "'");
		}
		if (null != endTime && !"".equals(endTime)) {
			dataSql.append(" and date_format(adddate,'%Y-%m-%d')<='")
			.append(endTime + "'");
		}
		if (search != null && !"".equals(search)) {
			dataSql.append(" and DATE_SUB(CURDATE(), INTERVAL "+search+" MONTH) <= adddate ");
		}
		dataSql.append("),(select IFNULL(sum(tender_money),0) from generalizemoney where release_status in (0,1) and refered_userid!="+uid);
		if (null != beginTime && !"".equals(beginTime)) {
			dataSql.append(" and date_format(tender_time,'%Y-%m-%d')>='")
			.append(beginTime + "'");
		}
		if (null != endTime && !"".equals(endTime)) {
			dataSql.append(" and date_format(tender_time,'%Y-%m-%d')<='")
			.append(endTime + "'");
		}
		if (search != null && !"".equals(search)) {
			dataSql.append(" and DATE_SUB(CURDATE(), INTERVAL "+search+" MONTH) <= tender_time ");
		}
		dataSql.append(" and refer_userid="+uid+")  from dual");
		return dao.findObjectBySql(dataSql.toString());
	}

	/**
	 * 通过通广人查询推荐人投资额和推荐人数
	 * @param uid 推广人id
	 * @param beginTime
	 * @param endTime
	 * @param search
	 * @return obj
	 */
	public Object getPromoteMoneyCountByGenuid(String uid, String id, int type){
		if(type == 0){
			StringBuffer dataSql = new StringBuffer("select (select count(1) from generalizemoney where release_status in (0,1) and refered_userid="+uid);
			dataSql.append(" and refer_userid="+id);
			dataSql.append(" ),(select IFNULL(sum(bonuses),0) as time from generalizemoney g");
			dataSql.append(" where release_status in (0,1) and g.refered_userid="+uid+" and refer_userid="+id+")  from dual");
			return dao.findObjectBySql(dataSql.toString());
		}else{
			StringBuffer dataSql = new StringBuffer("select (select count(1) from generalizemoney where release_status in (0,1) and refer_userid="+uid);
			//dataSql.append(" and refer_userid!="+id);
			dataSql.append(" ),(select IFNULL(sum(bonuses),0) as time from generalizemoney g");
			dataSql.append(" where release_status in (0,1) and g.refer_userid="+uid+")  from dual");
			return dao.findObjectBySql(dataSql.toString());
		}
	}

	/**
	 * 待收佣金
	 * @param request
	 * @return
	 */
	public Double getstayBonuses(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		String sql = "SELECT sum(bonuses) from generalizemoney where refer_userid=? and pay_state=0";
		Double stayBonuses = dao.queryNumberSql(sql, user.getId());
		return stayBonuses;
	}

	/**
	 * 已收佣金
	 * @param request
	 * @return
	 */
	public Double getpaidBonuses(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		String sql = "SELECT sum(paid_bonuses) from generalizemoney where refer_userid=? and pay_state=1";
		Double paidBonuses = dao.queryNumberSql(sql, user.getId());
		if (paidBonuses != null) {
			return paidBonuses;
		} else {
			return 0.00;
		}

	}

}
