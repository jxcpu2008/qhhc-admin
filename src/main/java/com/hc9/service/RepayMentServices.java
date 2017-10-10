package com.hc9.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.constant.PayURL;
import com.hc9.common.log.LOG;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Repaymentrecordparticulars;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.P2pQuery;
import com.hc9.model.PageModel;

/**
 * 还款业务处理
 * 
 * @author RanQiBing 2014-05-14
 *
 */
@Service
public class RepayMentServices {

	@Resource
	private HibernateSupport dao;
	@Resource
	private UserBaseInfoService userBaseInfoService;

	@Resource
	private PayLogService payLogService;
	
	/** 批量修改投资人收款记录表 **/
	public void updateRepayDetailList(List<Repaymentrecordparticulars> toUpdateRcpList) {
		LOG.error("待更新的还款明细记录： " + toUpdateRcpList.size() + " 条！");
		dao.saveOrUpdateAll(toUpdateRcpList);
		LOG.error("成功更新 " + toUpdateRcpList.size() + " 条投资人收益明细记录！");
	}
	
	/** 分红状态查询 */
	public String shareBonusState(String orderid ,Loansign loansig,Userbasicsinfo userinfo) {
		String resultStr = "";
		P2pQuery p2pQuery = new P2pQuery(orderid, 4);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		try {
			String shopFullxml = ParseXML.p2pQueryXml(p2pQuery);
			nvps.add(new BasicNameValuePair("requestParams", shopFullxml));
			nvps.add(new BasicNameValuePair("sign", CommonUtil.MD5(shopFullxml + "~|~" + ParameterIps.getmerchantKey())));
			payLogService.savePayLog(shopFullxml, userinfo.getId(), loansig.getId(), 13, orderid, 0.0, 0.0, 0.00);

			String result = CommonUtil.excuteRequest(PayURL.P2PQUERYTESTURL, nvps);
			Document doc = DocumentHelper.parseText(result);
			Element rootElt = doc.getRootElement(); // 获取根节点
			// 拿到crs节点下的子节点code值
			String code = rootElt.elementTextTrim("code");
			String msg = rootElt.elementTextTrim("msg");
			String sign = rootElt.elementTextTrim("sign");
			// 获取子节点crs下的子节点result
			Iterator iteratorResult = rootElt.elementIterator("result");
			boolean Judge = false; // 判断是否有值
			String state = "0"; // 0-失败 1-成功
			while (iteratorResult.hasNext()) {
				Element itemEle = (Element) iteratorResult.next();
				Iterator iteratorOrder = itemEle.elementIterator("order"); // 获取子节点result下的子节点order
				while (iteratorOrder.hasNext()) {
					Element elementOrder = (Element) iteratorOrder.next();
					state = elementOrder.elementTextTrim("state");
					String Md5sign = CommonUtil.MD5(code + "~|~" + msg + "~|~" + ParameterIps.getDes_algorithm());
					if (sign.equals(Md5sign)) {
						if (code.equals("CSD000")) {
							Judge = true;
							if (state.equals("1")) {
								return "CSD000";
							}
						} else if (code.equals("CSD333")) {
							return "CSD333";
						} else {
							return "CSD222";
						}
					} else {
						return "CSD222";
					}
				}
			}
			if (!Judge && state.equals("0")) {
				return "CSD333";
			}
			return "CSD222";
		} catch (Exception e) {
			LOG.error("订单号 " + orderid + " 查询宝付失败!", e);
			return "CSD222";
		}
	}
	
	/**
	 * 提前还款
	 * 
	 * @param userId
	 *            用户编号
	 * @return 返回还款记录
	 */
	public List<Repaymentrecord> advanceRepayment(Long userId) {
		String sql = "SELECT * from repaymentrecord,loansign WHERE repaymentrecord.loanSign_id=loansign.id AND loansign.loanstate=3 AND loansign.userbasicinfo_id=? AND repaymentrecord.preRepayDate>? AND repaymentrecord.repayState=1 GROUP BY loansign.id";
		// String hql =
		// "from Repaymentrecord r where r.preRepayDate>? and r.repayState=1 and r.loansign.userbasicsinfo.id=?";
		List<Repaymentrecord> list = dao.findBySql(sql, Repaymentrecord.class,
				userId, DateUtils.format("yyyy-MM-dd"));
		return list;
	}

	/**
	 * 按时还款
	 * 
	 * @param userId
	 *            用户编号
	 * @return 返回还款记录
	 */
	/*
	 * public List<Repaymentrecord> scheduleRepayment(Long userId){ String sql =
	 * "SELECT * from repaymentrecord,loansign WHERE repaymentrecord.loanSign_id=loansign.id AND loansign.`status`=3 AND loansign.userbasicinfo_id=? "
	 * ; // String hql =
	 * "from Repaymentrecord r where r.preRepayDate=? and r.repayState=1 and r.loansign.userbasicsinfo.id=?"
	 * ; List<Repaymentrecord> list = dao.findBySql(sql, Repaymentrecord.class,
	 * userId); return list; }
	 */

	/**
	 * 还款计划标
	 * 
	 * @param request
	 * @param page
	 * @return
	 */
	public PageModel scheduleRepaymentLoansig(HttpServletRequest request,
			PageModel page) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);

		StringBuffer sql = new StringBuffer(
				"SELECT distinct  loansign.* from repaymentrecord,loansign WHERE  "
						+ "repaymentrecord.loanSign_id=loansign.id AND (loansign.`status`=6 or loansign.status=7)  "
						+ "AND loansign.userbasicinfo_id=? ");

		StringBuffer sqlCount = new StringBuffer(
				"SELECT  COUNT(distinct (loansign.id)) from repaymentrecord,loansign "
						+ "WHERE repaymentrecord.loanSign_id=loansign.id AND (loansign.`status`=6 or loansign.status=7) "
						+ "AND loansign.userbasicinfo_id=");
		sqlCount.append(user.getId());

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());

		List<Loansign> list = dao.findBySql(sql.toString(), Loansign.class,
				user.getId());
		page.setList(list);// project集合
		return page;
	}

	/**
	 * 根据投资记录查询还款记录
	 * 
	 * @param request
	 * @param page
	 * @param loansignId
	 * @return
	 */
	public List getRepaymentShareRecord(String loanrecordId) {
		StringBuffer sql = new StringBuffer(
				"select r.id,case loanType when 1 then money when 2 then middleMoney else afterMoney end,"
				+ "case loanType when 1 then preRepayMoney when 2 then middlePreRepayMoney else afterPreRepayMoney end,"
				+ "(select periods from repaymentrecord where id=r.repaymentrecordId),(select repayState from repaymentrecord where id=r.repaymentrecordId),(select preRepayDate from repaymentrecord where id=r.repaymentrecordId),"
				+ "case loanType when 1 then '优先' when 2 then '夹层' else '劣后' end from repaymentrecordparticulars r where r.loanrecordId=" + loanrecordId);
		List list = dao.findBySql(sql.toString());
		return list;
	}
	
	/**
	 * 还款清单
	 * 
	 * @param request
	 * @param page
	 * @param loansignId
	 * @return
	 */
	public PageModel repaymentList(HttpServletRequest request,
			PageModel page,String loanName,String begin,String end,Integer search) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		StringBuffer sql = new StringBuffer("select rr.id,ls.name,rr.preRepayDate,rr.periods, ");
		sql.append("IFNULL((select sum(money)+sum(middleMoney)+sum(afterMoney)+sum(preRepayMoney)+sum(middlePreRepayMoney)+sum(afterPreRepayMoney) from repaymentrecord where id=rr.id)+IF(ls.loansignType_id=5,IFNULL(rr.companyPreFee,0),0),0),rr.repayState,");
		sql.append("rr.repayTime,(select count(1) from repaymentrecord where DATE_FORMAT(now(),'%Y-%m-%d') <= DATE_FORMAT(preRepayDate,'%Y-%m-%d') and repayState = 1 and id=rr.id),IFNULL(ls.id,null) ");
		sql.append("from repaymentrecord rr join loansign ls on rr.loanSign_id=ls.id where ls.status>=6 and ls.userbasicinfo_id="+user.getId());
		StringBuffer sqlCount = new StringBuffer("select count(1) from repaymentrecord rr join loansign ls on rr.loanSign_id=ls.id where ls.status>=6 and ls.userbasicinfo_id="+user.getId());

		if (StringUtil.isNotBlank(loanName)) { // 项目名称
			sql.append(" and ls.name like '%" + loanName + "%'");
			sqlCount.append(" and ls.name like '%" + loanName + "%'");
		}
		if (StringUtil.isNotBlank(begin)) { // 开始时间
			sql.append(" and date_format(rr.preRepayDate,'%Y-%m-%d') >= '"
					+ begin + "'");
			sqlCount.append(" and date_format(rr.preRepayDate,'%Y-%m-%d') >= '"
					+ begin + "'");
		}
		if (StringUtil.isNotBlank(end)) { // 结束时间
			sql.append(" and date_format(rr.preRepayDate,'%Y-%m-%d') <= '"
					+ end + "'");
			sqlCount.append(" and date_format(rr.preRepayDate,'%Y-%m-%d') <= '"
					+ end + "'");
		}
		if (search != null && !"".equals(search)) {
			sql.append(" and rr.preRepayDate<=now() and rr.preRepayDate>=DATE_SUB(now(),INTERVAL "+search+" MONTH) ");
			sqlCount.append(" and rr.preRepayDate<=now() and rr.preRepayDate>=DATE_SUB(now(),INTERVAL "+search+" MONTH) ");
		}
		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		sql.append(" order by ls.`name` desc,rr.repayState,rr.preRepayDate desc LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());

		List list = dao.findBySql(sql.toString());
		page.setList(list);// project集合
		return page;
	}
	
	public Long processRepaymentId(Long loan_id) {
		String sql = "select id from repaymentrecord where loanSign_id = ? and repayState in (1,3) order by periods limit 0,1 ";
		Object obj = dao.findObjectBySql(sql, loan_id);
		return obj != null ? Long.valueOf(obj.toString()) : 0;
	}
	
	/**
	 * 逾期还款
	 * 
	 * @param userId
	 *            用户编号
	 * @return 返回还款记录
	 */
	public List<Repaymentrecord> overdueRepayment(Long userId) {
		String sql = "SELECT * from repaymentrecord,loansign WHERE repaymentrecord.loanSign_id=loansign.id AND loansign.loanstate=3 AND loansign.userbasicinfo_id=? AND repaymentrecord.preRepayDate<? AND repaymentrecord.repayState=1 GROUP BY loansign.id";
		// String hql =
		// "from Repaymentrecord r where r.preRepayDate<? and r.repayState=1 and r.loansign.userbasicsinfo.id=?";
		List<Repaymentrecord> list = dao.findBySql(sql, Repaymentrecord.class,
				userId, DateUtils.format("yyyy-MM-dd"));
		return list;
	}

	/**
	 * 获取用户借款总额
	 * 
	 * @param userId
	 *            用户编号
	 * @return 用户借款总额
	 */
	public Double getMoney(Long userId) {
		String sql = "SELECT SUM(repaymentrecord.money) as k from repaymentrecord,loansign where repaymentrecord.loanSign_id=loansign.id AND repaymentrecord.repayState=1 and loansign.userbasicinfo_id=?";
		return dao.queryNumberSql(sql, userId);
	}

	/**
	 * 获取用户借款标信息
	 * 
	 * @param userId
	 *            用户编号
	 * @return 条数
	 */
	public int getNum(Long userId) {
		String sql = "select count(*) from loansign where loanstate=3 and userbasicinfo_id=?";
		return dao.queryNumberSql(sql, userId).intValue();
	}

	/***
	 * 查询还款记录
	 * 
	 * @param userId
	 * @param loansignId
	 * @return
	 */
	public List loansignRepaymentCount(Long userId, Long loansignId,
			Integer subType) {
		String sql = "select r.id,l.periods,l.repayState,l.preRepayDate,ifnull(r.money+r.preRepayMoney,0.00),IFNULL(r.middleMoney+r.middlePreRepayMoney,0.00),IFNULL(r.afterMoney+r.afterPreRepayMoney,0.00),IFNULL(r.money+r.realMoney,0.00),IFNULL(r.middleMoney+r.middleRealMoney,0.00),IFNULL(r.afterMoney+r.afterRealMoney,0.00) ,r.loanType from repaymentrecordparticulars  r ,repaymentrecord l where r.repaymentrecordId=l.id  and l.loanSign_id=?    and r.userId=?  and r.loanType=?";
		List list = dao.findBySql(sql, loansignId, userId, subType);
		return list;
	}

	public List loansignRepayment(Long userId, Long loansignId,
			Integer subType, int no) {
		String sql = "select r.id,l.periods,l.repayState,l.preRepayDate,ifnull(r.money+r.preRepayMoney,0.00),IFNULL(r.middleMoney+r.middlePreRepayMoney,0.00),IFNULL(r.afterMoney+r.afterPreRepayMoney,0.00),IFNULL(r.money+r.realMoney,0.00),IFNULL(r.middleMoney+r.middleRealMoney,0.00),IFNULL(r.afterMoney+r.afterRealMoney,0.00),r.loanType  from repaymentrecordparticulars  r ,repaymentrecord l where r.repaymentrecordId=l.id  and l.loanSign_id=?    and r.userId=? and r.loanType=?  LIMIT "
				+ no + ",10";
		List list = dao.findBySql(sql, loansignId, userId, subType);
		return list;
	}

	/**
	 * 交易记录-资金流水信息
	 * 
	 * @param request
	 * @param page
	 * @param loansignId
	 * @return
	 */
	public PageModel tradeRecord(HttpServletRequest request,
			PageModel page,String begin,String end,Integer search,String type) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		StringBuffer sql = new StringBuffer("select id,explan,income,money,(DATE_FORMAT(time,'%Y-%m-%d %H:%m:%s')),fee,expenditure from accountinfo where userbasic_id = "+user.getId());
		StringBuffer sqlCount = new StringBuffer("select count(1) from accountinfo where userbasic_id = "+user.getId());

		if (StringUtil.isNotBlank(type)) {  // 资金流水类型
			sql.append(" and accounttype_id in (").append(type).append(") ");
			sqlCount.append(" and accounttype_id in (").append(type).append(") ");
		}
		if (StringUtil.isNotBlank(begin)) { // 开始时间
			sql.append(" and date_format(time,'%Y-%m-%d') >= '"
					+ begin + "'");
			sqlCount.append(" and date_format(time,'%Y-%m-%d') >= '"
					+ begin + "'");
		}
		if (StringUtil.isNotBlank(end)) { // 结束时间
			sql.append(" and date_format(time,'%Y-%m-%d') <= '"
					+ end + "'");
			sqlCount.append(" and date_format(time,'%Y-%m-%d') <= '"
					+ end + "'");
		}
		if (search != null && !"".equals(search)) {
			sql.append(" and time<=now() and time>=DATE_SUB(now(),INTERVAL "+search+" MONTH) ");
			sqlCount.append(" and time<=now() and time>=DATE_SUB(now(),INTERVAL "+search+" MONTH) ");
		}
		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		sql.append(" order by id desc LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());

		List list = dao.findBySql(sql.toString() );
		page.setList(list);// project集合
		return page;
	}

	/**
	 * H5交易记录-资金流水信息
	 * 
	 * @param request
	 * @param page
	 * @param loansignId
	 * @return
	 */
	public PageModel getTradingRecord(Long userId, String type, int all, PageModel page) {
		if("6".equals(type)){
			// 充值记录
			StringBuffer sql = new StringBuffer();
			sql.append(" select id,income,(DATE_FORMAT(time,'%Y-%m-%d %H:%m')),ifnull(fee,0),expenditure ");
			sql.append(" from accountinfo where userbasic_id = ");
			sql.append(userId);
			sql.append(" and accounttype_id in (").append(type).append(") ");
			sql.append(" order by time desc ");
			
			StringBuffer sqlCount = new StringBuffer();
			sqlCount.append(" select count(1) from accountinfo where userbasic_id = ");
			sqlCount.append(userId);
			sqlCount.append(" and accounttype_id in (").append(type).append(") ");
			
			page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

			if(all == 0){
				sql.append(" limit 0,11");
			}else{
				sql.append(" LIMIT ");
				sql.append((page.getPageNum() - Constant.STATUES_ONE) * page.getNumPerPage());
				sql.append(",");
				sql.append(page.getNumPerPage());
			}
			List list = dao.findBySql(sql.toString() );
			page.setList(list);// project集合
			return page;
		}else if("5".equals(type) || "20".equals(type) || "16,18,21,24,25".equals(type)){
			type = "5".equals(type)?"5,15":type;
			// 认购记录 || 回款记录
			StringBuffer sql = new StringBuffer();
			sql.append(" select a.id,expenditure,(DATE_FORMAT(a.time,'%Y-%m-%d %H:%m')),l.name,a.income,explan ");
			sql.append(" from accountinfo a LEFT JOIN loansign l ON a.loansign_id = l.id ");
			sql.append(" where userbasic_id = ");
			sql.append(userId);
			sql.append(" and accounttype_id in (").append(type).append(") ");
			sql.append(" order by a.time desc ");
			
			StringBuffer sqlCount = new StringBuffer();
			sqlCount.append(" select count(1) from accountinfo a LEFT JOIN loansign l ON a.loansign_id = l.id ");
			sqlCount.append(" where userbasic_id = ");
			sqlCount.append(userId);
			sqlCount.append(" and accounttype_id in (").append(type).append(") ");
			
			page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量
			
			if(all == 0){
				sql.append(" limit 0,11");
			}else{
				sql.append(" LIMIT ");
				sql.append((page.getPageNum() - Constant.STATUES_ONE) * page.getNumPerPage());
				sql.append(",");
				sql.append(page.getNumPerPage());
			}
			List list = dao.findBySql(sql.toString() );
			page.setList(list);// project集合
			return page;
		}else if("7".equals(type)){
			// 提现记录
			StringBuffer sql = new StringBuffer();
			sql.append(" select id, amount, (DATE_FORMAT(time,'%Y-%m-%d %H:%m')), state, ifnull(fee,0), ifnull(mer_fee,0)");
			sql.append(" FROM withdraw w where w.user_id= ");
			sql.append(userId);
			sql.append(" order by time desc ");

			StringBuffer sqlCount = new StringBuffer();
			sqlCount.append(" select count(1) FROM withdraw w where w.user_id= ");
			sqlCount.append(userId);
			
			page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量
			
			if(all == 0){
				sql.append(" limit 0,11");
			}else{
				sql.append(" LIMIT ");
				sql.append((page.getPageNum() - Constant.STATUES_ONE) * page.getNumPerPage());
				sql.append(",");
				sql.append(page.getNumPerPage());
			}
			List list = dao.findBySql(sql.toString() );
			page.setList(list);// project集合
			return page;
		}else{
			return null;
		}
		
	}
}
