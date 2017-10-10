package com.hc9.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hc9.common.constant.Constant;
import com.hc9.common.listener.UserList;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.SQLUtils;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Borrowersbase;
import com.hc9.dao.entity.Borrowerscompany;
import com.hc9.dao.entity.Borrowerscontact;
import com.hc9.dao.entity.Borrowersfiles;
import com.hc9.dao.entity.Borrowersfinanes;
import com.hc9.dao.entity.Borrowersothercontact;
import com.hc9.dao.entity.Generalize;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Userfundinfo;
import com.hc9.dao.entity.Usermessage;
import com.hc9.dao.entity.Userrelationinfo;
import com.hc9.dao.entity.Vipinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
import com.hc9.model.UnnormalUserInfo;

/**
 * <p>
 * Title:UserInfoServices *
 * </p>
 * <p>
 * Description: 前台会员服务层
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author LiNing date 2014年1月24日
 */
@Service
@Transactional
public class UserInfoServices {

	/** 引入log4j日志打印类 */
	private static final Logger LOGGER = Logger.getLogger(UserInfoServices.class);

	/** 注入数据库操作层 */
	@Resource
	private HibernateSupport dao;

	/** ul当前登录会员数量、历史最高数量 */
	private UserList ul = UserList.getInstance();

	@Resource
	private MyMoneyService myMoneyService;

	@Resource
	private MemberCenterService memberCenterService;

	@Resource
	private RegistrationService registrationService;

	public List<UnnormalUserInfo> queryUnnormalUserInfosByPage(String phone, PageModel page) {
		List<UnnormalUserInfo> resultList = new ArrayList<UnnormalUserInfo>();
		List<Object> params = new ArrayList<Object>();
		
		String selectSql = " select "
				+ "	paylog.id, "
				+ " paylog.message,  "
				+ " paylog.`action`, "
				+ " paylog.pay_status, "
				+ " userbasicsinfo.userName, "
				+ " userbasicsinfo.name, "
				+ " userbasicsinfo.pMerBillNo, "
				+ " userbasicsinfo.hasIpsAccount, "
				+ " userbasicsinfo.isAuthIps, "
				+ " userbasicsinfo.createTime, "
				+ " userbasicsinfo.id as userId, "
				+ " userrelationinfo.phone, "
				+ " count(userbasicsinfo.userName) as num ";
		
		String fromSql = " from paylog "
				+ " left outer join userbasicsinfo on userbasicsinfo.pMerBillNo = paylog.order_sn "
				+ " left outer join userrelationinfo on userrelationinfo.user_id = userbasicsinfo.id ";
		
		String whereSql = " where (paylog.`action` = '绑定用户' or paylog.`action` = '用户授权') "
				+ " and (userbasicsinfo.isAuthIps != 1 or userbasicsinfo.isAuthIps is null) "
				+ " and userbasicsinfo.pMerBillNo is not null "
				+ " and userbasicsinfo.pMerBillNo != '' ";
		
		String groupSql = " group by userbasicsinfo.pMerBillNo ";
		
		StringBuffer condition = new StringBuffer();
		// 电话号码
		if (phone != null && phone.length() > 0) {
			condition.append(" and userrelationinfo.phone =  ? ");
			params.add(phone);
		}
		
		String conditionSql = condition.toString();
		String orderSql = " order by userbasicsinfo.createTime desc ";
		
		String querySql = selectSql + fromSql + whereSql + groupSql + orderSql;
		if (conditionSql.length() > 0) {
			querySql = selectSql + fromSql + whereSql + conditionSql + groupSql + orderSql;
		}
		
		String countSql = " select count(*) from (" + "select paylog.id " + fromSql + whereSql + conditionSql + groupSql + ") t ";
		List list = dao.pageListBySql(page, countSql, querySql, null, params.toArray());
				
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] arr = (Object[]) obj;
				UnnormalUserInfo vo = new UnnormalUserInfo();
				vo.setPayLogId(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setAction(StatisticsUtil.getStringFromObject(arr[2]));
				vo.setUserName(StatisticsUtil.getStringFromObject(arr[4]));
				vo.setName(StatisticsUtil.getStringFromObject(arr[5]));
				vo.setBillNo(StatisticsUtil.getStringFromObject(arr[6]));
				vo.setHasIpsAccount(StatisticsUtil.getIntegerFromObject(arr[7]));
				vo.setIsAuthIps(StatisticsUtil.getIntegerFromObject(arr[8]));
				vo.setRegTime(StatisticsUtil.getStringFromObject(arr[9]));
				vo.setUserId(StatisticsUtil.getLongFromBigInteger(arr[10]));
				vo.setPhone(StatisticsUtil.getStringFromObject(arr[11]));
				vo.setOperResult(" ");
				
				resultList.add(vo);
			}
		}
		
		return resultList;
	}
	
	/**
	 * <p>
	 * Title: queryUserPage
	 * </p>
	 * <p>
	 * Description: 分页查询会员信息
	 * </p>
	 * 
	 * @param page
	 *            分页参数
	 * @param userinfo
	 *            查询条件
	 * @return 查询结果集list
	 */
	@SuppressWarnings("rawtypes")
	public List queryUserPage(PageModel page, Userbasicsinfo userinfo) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryUserPage(PageModel page=" + page + ", Userbasicsinfo userinfo=" + userinfo + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		List datalist = new ArrayList();

		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"SELECT count(1)FROM userbasicsinfo ");
		countsql.append(" LEFT JOIN userfundinfo ON userbasicsinfo.id = userfundinfo.id ");
		countsql.append(" LEFT JOIN userrelationinfo ON userrelationinfo.user_id = userbasicsinfo.id  ");
		countsql.append(" WHERE 1=1 ");

		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT userbasicsinfo.id, userbasicsinfo.userName, userbasicsinfo.`name`,userrelationinfo.phone,(select sum(realityintegral) from autointegral where user_id=userbasicsinfo.id),");
		sqlbuffer
				.append(" ROUND(userfundinfo.credit,2), userbasicsinfo.createTime, ( SELECT max(userloginlog.logintime) ");
		sqlbuffer
				.append(" FROM userloginlog WHERE userloginlog.user_id = userbasicsinfo.id),");
		sqlbuffer
				.append(" ( SELECT userloginlog.address FROM userloginlog WHERE userloginlog.user_id = userbasicsinfo.id ORDER BY userloginlog.id DESC LIMIT 0, 1 ), ");
		sqlbuffer
				.append(" ( SELECT count(1) FROM userloginlog WHERE userloginlog.user_id = userbasicsinfo.id ), ");
		sqlbuffer
				.append(" (SELECT  max(endtime) FROM vipinfo WHERE vipinfo.endtime > NOW() AND vipinfo.user_id = userbasicsinfo.id)  AS vipendtime,");
		sqlbuffer
				.append(" ( SELECT count(1) FROM borrowersbase WHERE auditResult = 1 AND userbasicinfo_id = userbasicsinfo.id ),");
		sqlbuffer
				.append("  CASE WHEN userbasicsinfo.isLock = 0 THEN '正常' ELSE '禁用' END, ");
		sqlbuffer
				.append(" (SELECT realname FROM adminuser WHERE id = userbasicsinfo.adminuser_id ), userbasicsinfo.hasIpsAccount,userbasicsinfo.isAuthIps,userbasicsinfo.orderSn,  ");
		sqlbuffer
				.append(" userbasicsinfo.isorgperson, userbasicsinfo.iscrowdfundingperson,userbasicsinfo.iscrowdhold, userbasicsinfo.pMerBillNo, userbasicsinfo.user_type,userbasicsinfo.staff_no,"
						+ "(SELECT u.name from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid),(SELECT u.userName from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid), ");
		sqlbuffer
				.append(" (SELECT FORMAT(sum(IFNULL(rpp.preRepayMoney,0)+IFNULL(rpp.middlePreRepayMoney,0)+IFNULL(rpp.afterPreRepayMoney,0)),2)  "
						+ "from repaymentrecord rp JOIN repaymentrecordparticulars rpp on rp.id=rpp.repaymentrecordId where rp.repayState!=1 AND rp.repayState!=3 AND rpp.userId=userfundinfo.id), ");
		sqlbuffer
				.append(" (SELECT sum(tenderMoney) from loanrecord where userbasicinfo_id=userfundinfo.id and isSucceed=1),(SELECT u.department from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid),userfundinfo.cashBalance ");
		sqlbuffer
				.append(" FROM userbasicsinfo LEFT JOIN userfundinfo ON userbasicsinfo.id = userfundinfo.id ");
		sqlbuffer
				.append(" LEFT JOIN userrelationinfo ON userrelationinfo.user_id = userbasicsinfo.id WHERE 1=1 ");

		if (null != userinfo) {

			if (StringUtil.isNotBlank(userinfo.getUserName())) {
				String username = "";
				try {
					username = java.net.URLDecoder.decode(
							userinfo.getUserName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and userbasicsinfo.userName like '%")
						.append(StringUtil.replaceAll(username)).append("%'");
				countsql.append(" and userbasicsinfo.userName like '%")
						.append(StringUtil.replaceAll(username)).append("%'");
			}
			// 真实姓名查询
			if (StringUtil.isNotBlank(userinfo.getName())) {
				String name = "";
				try {
					name = java.net.URLDecoder.decode(userinfo.getName(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" AND userbasicsinfo.name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
				countsql.append(" AND userbasicsinfo.name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
			}
			// 手机号码
			if (null != userinfo.getRepayAuthNo()) {
				sqlbuffer
						.append(" AND userrelationinfo.phone LIKE '%")
						.append(StringUtil.replaceAll(userinfo.getRepayAuthNo()))
						.append("%'");
				countsql.append(" AND userrelationinfo.phone LIKE '%")
						.append(StringUtil.replaceAll(userinfo.getRepayAuthNo()))
						.append("%'");
			}
			// 是否是借款人
			if (null != userinfo.getLockTime()
					&& !"".equals(userinfo.getLockTime())) {
				if ("0".equals(userinfo.getLockTime())) {
					sqlbuffer
							.append("AND userbasicsinfo.id NOT  IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
					countsql.append("AND userbasicsinfo.id NOT  IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
				} else {
					sqlbuffer
							.append("AND userbasicsinfo.id IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
					countsql.append("AND userbasicsinfo.id IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
				}

			}

			// 注册时间查询
			if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
				sqlbuffer.append(" and userbasicsinfo.createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
				countsql.append(" and userbasicsinfo.createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
			}

			if (StringUtil.isNotBlank(userinfo.getFailTime())) {
				sqlbuffer.append(" and userbasicsinfo.createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
				countsql.append(" and userbasicsinfo.createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
			}

			// 根据客服
			if (StringUtil.isNotBlank(userinfo.getNickname())) {
				sqlbuffer.append("AND userrelationinfo.adminuser_id = ")
						.append(userinfo.getNickname());
				countsql.append("AND userrelationinfo.adminuser_id = ").append(
						userinfo.getNickname());
			}

			// 登录次数查询
			if (null != userinfo.getIsLock()) {
				sqlbuffer
						.append(" and  (SELECT count(0) FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id)>"
								+ userinfo.getIsLock().intValue());
				countsql.append(" and  (SELECT count(0) FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id)>"
						+ userinfo.getIsLock().intValue());
			}

			if (StringUtil.isNotBlank(userinfo.getLoginTime())) {
				sqlbuffer.append("AND userbasicsinfo.isOrgPerson = ").append(
						userinfo.getLoginTime());
				countsql.append("AND userbasicsinfo.isOrgPerson = ").append(
						userinfo.getLoginTime());
			}

			if (null != userinfo.getUserType()) {
				sqlbuffer.append("AND userbasicsinfo.user_type = ").append(
						userinfo.getUserType());
				countsql.append("AND userbasicsinfo.user_type = ").append(
						userinfo.getUserType());
			}

		}
		sqlbuffer.append(" order by userbasicsinfo.createTime desc ");
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryUserPage(PageModel, Userbasicsinfo)方法结束OUTPARAM="
					+ datalist);
		}
		return datalist;

	}

	/**
	 * <p>
	 * Title: queryUserPage
	 * </p>
	 * <p>
	 * Description: 分页查询会员信息
	 * </p>
	 * 
	 * @param page
	 *            分页参数
	 * @param userinfo
	 *            查询条件
	 * @return 查询结果集list
	 */
	@SuppressWarnings("rawtypes")
	public List queryUserPageCustomer(PageModel page, Userbasicsinfo userinfo,String isPurchase,String isRecommend) {
		List datalist = new ArrayList();

		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"SELECT count(1)FROM userbasicsinfo ");
		countsql.append(" LEFT JOIN userfundinfo ON userbasicsinfo.id = userfundinfo.id ");
		countsql.append(" LEFT JOIN userrelationinfo ON userrelationinfo.user_id = userbasicsinfo.id  ");
		countsql.append(" WHERE 1=1 ");

		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer("SELECT userbasicsinfo.id, userbasicsinfo.userName, userbasicsinfo.`name`,userrelationinfo.phone, userbasicsinfo.createTime,");
		sqlbuffer.append(" userbasicsinfo.hasIpsAccount,userbasicsinfo.isAuthIps, userbasicsinfo.pMerBillNo, userbasicsinfo.user_type,userbasicsinfo.staff_no,userfundinfo.cashBalance,");
		sqlbuffer.append(" (SELECT u.name from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid),(SELECT u.userName from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid), ");
		sqlbuffer.append(" (SELECT u.department from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid), (select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=userbasicsinfo.userName   LIMIT 0,1 ), ");
		sqlbuffer.append(" ( select IFNULL(sum(l.tenderMoney),0) from loanrecord l where l.userbasicinfo_id=userbasicsinfo.id and l.isSucceed=1 ) ");
		sqlbuffer.append(" FROM userbasicsinfo LEFT JOIN userfundinfo ON userbasicsinfo.id = userfundinfo.id  LEFT JOIN userrelationinfo ON userrelationinfo.user_id = userbasicsinfo.id WHERE 1=1 ");

		if (null != userinfo) {

			if (StringUtil.isNotBlank(userinfo.getUserName())) {
				String username = "";
				try {
					username = java.net.URLDecoder.decode(
							userinfo.getUserName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and userbasicsinfo.userName like '%")
						.append(StringUtil.replaceAll(username)).append("%'");
				countsql.append(" and userbasicsinfo.userName like '%")
						.append(StringUtil.replaceAll(username)).append("%'");
			}
			// 真实姓名查询
			if (StringUtil.isNotBlank(userinfo.getName())) {
				String name = "";
				try {
					name = java.net.URLDecoder.decode(userinfo.getName(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" AND userbasicsinfo.name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
				countsql.append(" AND userbasicsinfo.name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
			}
			// 手机号码
			if (null != userinfo.getRepayAuthNo()) {
				sqlbuffer
						.append(" AND userrelationinfo.phone LIKE '%")
						.append(StringUtil.replaceAll(userinfo.getRepayAuthNo()))
						.append("%'");
				countsql.append(" AND userrelationinfo.phone LIKE '%")
						.append(StringUtil.replaceAll(userinfo.getRepayAuthNo()))
						.append("%'");
			}
			// 是否是借款人
			if (null != userinfo.getLockTime()
					&& !"".equals(userinfo.getLockTime())) {
				if ("0".equals(userinfo.getLockTime())) {
					sqlbuffer
							.append("AND userbasicsinfo.id NOT  IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
					countsql.append("AND userbasicsinfo.id NOT  IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
				} else {
					sqlbuffer
							.append("AND userbasicsinfo.id IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
					countsql.append("AND userbasicsinfo.id IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
				}

			}
			
			//认购
			if(StringUtil.isNotBlank(isPurchase)){
				if(isPurchase.equals("1")){
					sqlbuffer.append(" and ( select IFNULL(sum(l.tenderMoney),0) from loanrecord l where l.userbasicinfo_id=userbasicsinfo.id and l.isSucceed=1 ) >0  ");
					countsql.append(" and ( select IFNULL(sum(l.tenderMoney),0) from loanrecord l where l.userbasicinfo_id=userbasicsinfo.id and l.isSucceed=1 ) >0  ");
				}else{
					sqlbuffer.append(" and ( select IFNULL(sum(l.tenderMoney),0) from loanrecord l where l.userbasicinfo_id=userbasicsinfo.id and l.isSucceed=1 ) =0  ");
					countsql.append(" and ( select IFNULL(sum(l.tenderMoney),0) from loanrecord l where l.userbasicinfo_id=userbasicsinfo.id and l.isSucceed=1 ) =0  ");
				}
			}
			
			//推荐人
			if(StringUtil.isNotBlank(isRecommend)){
				if(isRecommend.equals("1")){
					sqlbuffer.append(" and (SELECT COUNT(g.id) from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid)>0  ");
					countsql.append(" and (SELECT COUNT(g.id) from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid)>0 ");
				}else{
					sqlbuffer.append(" and (SELECT COUNT(g.id) from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid)=0  ");
					countsql.append(" and (SELECT COUNT(g.id) from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid)=0 ");
				}
			}

			// 注册时间查询
			if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
				sqlbuffer.append(" and userbasicsinfo.createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
				countsql.append(" and userbasicsinfo.createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
			}

			if (StringUtil.isNotBlank(userinfo.getFailTime())) {
				sqlbuffer.append(" and userbasicsinfo.createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
				countsql.append(" and userbasicsinfo.createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
			}

			// 根据客服
			if (StringUtil.isNotBlank(userinfo.getNickname())) {
				sqlbuffer.append("AND userrelationinfo.adminuser_id = ")
						.append(userinfo.getNickname());
				countsql.append("AND userrelationinfo.adminuser_id = ").append(
						userinfo.getNickname());
			}

			// 登录次数查询
			if (null != userinfo.getIsLock()) {
				sqlbuffer
						.append(" and  (SELECT count(0) FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id)>"
								+ userinfo.getIsLock().intValue());
				countsql.append(" and  (SELECT count(0) FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id)>"
						+ userinfo.getIsLock().intValue());
			}

			if (StringUtil.isNotBlank(userinfo.getLoginTime())) {
				sqlbuffer.append("AND userbasicsinfo.isOrgPerson = ").append(
						userinfo.getLoginTime());
				countsql.append("AND userbasicsinfo.isOrgPerson = ").append(
						userinfo.getLoginTime());
			}

			if (null != userinfo.getUserType()) {
				sqlbuffer.append("AND userbasicsinfo.user_type = ").append(
						userinfo.getUserType());
				countsql.append("AND userbasicsinfo.user_type = ").append(
						userinfo.getUserType());
			}

		}
		sqlbuffer.append(" order by userbasicsinfo.createTime desc ");
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);
		return datalist;

	}

	/** 处理会员渠道相关信息 */
	public Map<String, String> handleMemberChannelInfo(List<String> memberIdList) {
		Map<String, String> userChannelMap = new HashMap<String, String>();
		if(memberIdList != null && memberIdList.size() > 0) {
			Map<String, String> oneLevelChannelMap = new HashMap<String, String>();//一级
			Map<String, String> twoLevelChannelMap = new HashMap<String, String>();//二级：广告位
			Map<String, String> threeLevelChannelMap = new HashMap<String, String>();//三级：广告位
			String sql = "select id,name,type,upSpreadId from channelspread";
			List channelList = dao.findBySql(sql);
			if(channelList != null && channelList.size() > 0) {
				for(Object obj : channelList) {
					Object[] arr = (Object[])obj;
					Integer id = StatisticsUtil.getIntegerFromObject(arr[0]);
					String name = StatisticsUtil.getStringFromObject(arr[1]);
					Integer type = StatisticsUtil.getIntegerFromObject(arr[2]);
					String upSpreadId = StatisticsUtil.getStringFromObject(arr[3]);//上级广告位id
					if(type == 1) {
						oneLevelChannelMap.put("" + id, name);
					} else if(type == 2) {
						twoLevelChannelMap.put("" + id, upSpreadId);
					} else if(type == 3) {
						threeLevelChannelMap.put("" + id, upSpreadId);
					}
				}
			}
			sql = "select c.id,d.regUserName,c.type,c.upSpreadId from channelspreaddetail d,channelspread c ";
			sql += " where d.regStatus=1 and d.spreadId=c.spreadId and d.regUserName in(";
			for(int i = 0; i < memberIdList.size(); i++) {
				if(i < (memberIdList.size() -1)) {
					sql += "'" + memberIdList.get(i) + "',";
				} else {
					sql += "'" + memberIdList.get(i) + "'";
				}
			}
			sql += ")";
			List memberChannelList = dao.findBySql(sql);
			if(memberChannelList != null && memberChannelList.size() > 0) {
				for(Object obj : memberChannelList) {
					Object[] arr = (Object[])obj;
					Integer id = StatisticsUtil.getIntegerFromObject(arr[0]);
					String regUserName = StatisticsUtil.getStringFromObject(arr[1]);
					Integer type = StatisticsUtil.getIntegerFromObject(arr[2]);
					/** 上级推广id */
					String upSpreadId = StatisticsUtil.getStringFromObject(arr[3]);
					if(type == 1) {
						userChannelMap.put(regUserName, oneLevelChannelMap.get(id));
					} else if(type == 2) {
						userChannelMap.put(regUserName, oneLevelChannelMap.get(upSpreadId));
					} else if(type == 3) {
						String upId = twoLevelChannelMap.get(upSpreadId);
						userChannelMap.put(regUserName, oneLevelChannelMap.get(upId));
					}
				}
			}
		}
		
		return userChannelMap;
	}
	
	public List queryinsinveplease(PageModel page, Userrelationinfo userla) {

		List datalist = new ArrayList();

		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"SELECT count(1) FROM userbasicsinfo ");
		countsql.append(" LEFT JOIN userrelationinfo ON userrelationinfo.user_id = userbasicsinfo.id  ");
		countsql.append(" WHERE 1=1");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT userbasicsinfo.id,userbasicsinfo.userName,userbasicsinfo.`name`,userrelationinfo.cardId,userrelationinfo.mechofattachment1,"
						+ "userrelationinfo.mechofattachment2,userrelationinfo.mechofattachment3,userrelationinfo.mechofattachment4,"
						+ "userrelationinfo.certSubtime,userrelationinfo.manacerttime,userrelationinfo.audit,(select username from adminuser where id=userrelationinfo.adminuser_idauch) as adminame "
						+ " FROM userbasicsinfo "
						+ " LEFT JOIN userrelationinfo ON userrelationinfo.user_id = userbasicsinfo.id "
						+ " WHERE 1=1 ");

		if (null != userla.getAudit()) {
			sqlbuffer.append(" and  userrelationinfo.audit="
					+ userla.getAudit());
			countsql.append(" and  userrelationinfo.audit=" + userla.getAudit());
		} else {
			sqlbuffer.append(" and  userrelationinfo.audit>0 ");
			countsql.append(" and  userrelationinfo.audit>0");
		}
		sqlbuffer.append(" ORDER BY userrelationinfo.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);

		return datalist;

	}

	public List<Object> querycardidentity(PageModel page, Userbasicsinfo user) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"SELECT count(1) FROM userbasicsinfo ");
		countsql.append(" LEFT JOIN userrelationinfo ON userrelationinfo.user_id = userbasicsinfo.id  ");
		countsql.append(" WHERE 1=1");

		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT userbasicsinfo.id,userbasicsinfo.userName,userbasicsinfo.`name`,userrelationinfo.cardId,"
						+ "userrelationinfo.cardone,userrelationinfo.cardtwo,userbasicsinfo.userSubtimer,userbasicsinfo.manatoverifyuser,"
						+ "userbasicsinfo.cardStatus,(select username from adminuser where id=userbasicsinfo.adminuser_id) as adminame"
						+ " FROM userbasicsinfo "
						+ " LEFT JOIN userrelationinfo ON userrelationinfo.user_id = userbasicsinfo.id "
						+ " WHERE 1=1");

		if (null != user.getCardStatus()) {
			sqlbuffer.append(" and userbasicsinfo.cardStatus="
					+ user.getCardStatus());
			countsql.append(" and userbasicsinfo.cardStatus="
					+ user.getCardStatus());
		} else {
			sqlbuffer.append(" and userbasicsinfo.cardStatus>0 ");
			countsql.append(" and userbasicsinfo.cardStatus>0");
		}
		sqlbuffer.append(" ORDER BY userbasicsinfo.id desc");

		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);

		return datalist;

	}

	/**
	 * <p>
	 * Title: queryAll
	 * </p>
	 * <p>
	 * Description: 查询所有会员信息或根据编号查询编号为null则查询全部
	 * </p>
	 * 
	 * @param ids
	 *            根据编号查询，多个编号用逗号隔开
	 * @return 返回查询结果集list
	 */
	@SuppressWarnings("rawtypes")
	public List queryAll(String ids, Userbasicsinfo userinfo) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryAll()方法开始"); //$NON-NLS-1$
		}

		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT userbasicsinfo.id,userbasicsinfo.userName,");
		sqlbuffer
				.append("userbasicsinfo.`name`,userrelationinfo.phone,CASE WHEN userbasicsinfo.user_type=1 THEN '会员' WHEN userbasicsinfo.user_type=2 THEN '员工 ' ELSE '企业' END,userbasicsinfo.staff_no,manualintegral.amountPoints,");
		sqlbuffer.append(" userbasicsinfo.createTime,");
		sqlbuffer
				.append("(SELECT userloginlog.logintime FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id ORDER BY userloginlog.id DESC LIMIT 0,1),");
		sqlbuffer
				.append("(SELECT userloginlog.address FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id ORDER BY userloginlog.id DESC LIMIT 0,1),");
		sqlbuffer
				.append("(SELECT count(userloginlog.id) FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id),");
		sqlbuffer
				.append(" CASE WHEN (SELECT vipinfo.endtime FROM vipinfo WHERE vipinfo.user_id=userbasicsinfo.id ORDER BY id DESC LIMIT 1) > (SELECT NOW()) THEN '特权会员' ELSE '普通会员' END,");
		sqlbuffer
				.append(" CASE WHEN (SELECT vipinfo.endtime FROM vipinfo WHERE vipinfo.user_id=userbasicsinfo.id ORDER BY id DESC LIMIT 1) > (SELECT NOW()) THEN (SELECT vipinfo.endtime FROM vipinfo WHERE vipinfo.user_id=userbasicsinfo.id ORDER BY id DESC LIMIT 1) ELSE '永久' END,");
		sqlbuffer
				.append(" CASE WHEN  LENGTH(trim(pMerBillNo))>1 THEN '是' ELSE '否' END,");
		sqlbuffer
				.append(" CASE WHEN (SELECT COUNT(borrowersbase.id) FROM borrowersbase WHERE borrowersbase.auditResult=1 AND borrowersbase.userbasicinfo_id=userbasicsinfo.id) > 0 THEN '是' ELSE '否' END,");
		sqlbuffer
				.append(" CASE WHEN userbasicsinfo.isLock = 0 THEN '正常' ELSE '禁用' END,");
		sqlbuffer.append("userbasicsinfo.id ");

		sqlbuffer
				.append(" FROM userbasicsinfo LEFT JOIN userfundinfo ON userbasicsinfo.id = userfundinfo.userbasic_id");
		sqlbuffer
				.append(" LEFT JOIN manualintegral ON manualintegral.user_id = userbasicsinfo.id");
		sqlbuffer
				.append(" LEFT JOIN userrelationinfo ON userrelationinfo.user_id=userbasicsinfo.id WHERE userbasicsinfo.userName is not null ");

		if (null != userinfo) {

			if (StringUtil.isNotBlank(userinfo.getUserName())) {
				String username = "";
				try {
					username = java.net.URLDecoder.decode(
							userinfo.getUserName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and userbasicsinfo.userName like '%")
						.append(StringUtil.replaceAll(username)).append("%'");
			}
			// 真实姓名查询
			if (StringUtil.isNotBlank(userinfo.getName())) {
				String name = "";
				try {
					name = java.net.URLDecoder.decode(userinfo.getName(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" AND userbasicsinfo.name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
			}
			// 手机号码
			if (null != userinfo.getRepayAuthNo()) {
				sqlbuffer
						.append(" AND userrelationinfo.phone LIKE '%")
						.append(StringUtil.replaceAll(userinfo.getRepayAuthNo()))
						.append("%'");
			}
			// 是否是借款人
			if (null != userinfo.getLockTime()
					&& !"".equals(userinfo.getLockTime())) {
				if ("0".equals(userinfo.getLockTime())) {
					sqlbuffer
							.append("AND userbasicsinfo.id NOT  IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");

				} else {
					sqlbuffer
							.append("AND userbasicsinfo.id IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
				}

			}

			// 注册时间查询
			if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
				sqlbuffer.append(" and userbasicsinfo.createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
			}

			if (StringUtil.isNotBlank(userinfo.getFailTime())) {
				sqlbuffer.append(" and userbasicsinfo.createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
			}

			// 根据客服
			if (StringUtil.isNotBlank(userinfo.getNickname())) {
				sqlbuffer.append("AND userrelationinfo.adminuser_id = ")
						.append(userinfo.getNickname());
			}

			// 登录次数查询
			if (null != userinfo.getIsLock()) {
				sqlbuffer
						.append(" and  (SELECT count(0) FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id)>"
								+ userinfo.getIsLock().intValue());
			}

			if (StringUtil.isNotBlank(userinfo.getLoginTime())) {
				sqlbuffer.append("AND userbasicsinfo.isOrgPerson = ").append(
						userinfo.getLoginTime());
			}

			if (null != userinfo.getUserType()) {
				sqlbuffer.append("AND userbasicsinfo.user_type = ").append(
						userinfo.getUserType());
			}

		}

		List returnList = dao.findBySql(sqlbuffer.toString());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryAll()方法结束OUTPARAM=" + returnList); //$NON-NLS-1$
		}
		return returnList;

	}

	/**
	 * <p>
	 * Title: queryAll
	 * </p>
	 * <p>
	 * Description: 查询所有会员信息或根据编号查询编号为null则查询全部
	 * </p>
	 * 
	 * @param ids
	 *            根据编号查询，多个编号用逗号隔开
	 * @return 返回查询结果集list
	 */
	@SuppressWarnings("rawtypes")
	public List queryAllCustomer(String ids, Userbasicsinfo userinfo,String isPurchase,String isRecommend) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryAll()方法开始"); //$NON-NLS-1$
		}

		StringBuffer sqlbuffer = new StringBuffer("SELECT userbasicsinfo.id, userbasicsinfo.userName, userbasicsinfo.`name`,userrelationinfo.phone, userbasicsinfo.createTime, CASE WHEN userbasicsinfo.isAuthIps = 0 THEN '待确认' WHEN userbasicsinfo.isAuthIps = 1 THEN '已授权' ELSE '未授权' END as isAuthIps, CASE WHEN userbasicsinfo.user_type = 1 THEN '会员'  WHEN userbasicsinfo.user_type = 2 THEN '员工' WHEN userbasicsinfo.user_type = 3 THEN '企业'  WHEN userbasicsinfo.user_type = 4 THEN '居间人'  ELSE '未知' END,");
		sqlbuffer.append(" (SELECT u.name from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid),(SELECT u.userName from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid), ");
		sqlbuffer.append(" (SELECT u.department from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid), (select h.`name` from channelspreaddetail c,channelspread h where c.spreadId=h.spreadId and c.regStatus=1 and c.regUserName=userbasicsinfo.userName LIMIT 0,1 )");
		sqlbuffer.append(" , ( select IFNULL(sum(l.tenderMoney),0) from loanrecord l where l.userbasicinfo_id=userbasicsinfo.id and l.isSucceed=1 ) ");
		sqlbuffer.append(" FROM userbasicsinfo LEFT JOIN userfundinfo ON userbasicsinfo.id = userfundinfo.id  LEFT JOIN userrelationinfo ON userrelationinfo.user_id = userbasicsinfo.id WHERE 1=1 ");
		if (null != userinfo) {

			if (StringUtil.isNotBlank(userinfo.getUserName())) {
				String username = "";
				try {
					username = java.net.URLDecoder.decode(
							userinfo.getUserName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" and userbasicsinfo.userName like '%")
						.append(StringUtil.replaceAll(username)).append("%'");
			}
			// 真实姓名查询
			if (StringUtil.isNotBlank(userinfo.getName())) {
				String name = "";
				try {
					name = java.net.URLDecoder.decode(userinfo.getName(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" AND userbasicsinfo.name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
			}
			// 手机号码
			if (null != userinfo.getRepayAuthNo()) {
				sqlbuffer
						.append(" AND userrelationinfo.phone LIKE '%")
						.append(StringUtil.replaceAll(userinfo.getRepayAuthNo()))
						.append("%'");
			}
			// 是否是借款人
			if (null != userinfo.getLockTime()
					&& !"".equals(userinfo.getLockTime())) {
				if ("0".equals(userinfo.getLockTime())) {
					sqlbuffer
							.append("AND userbasicsinfo.id NOT  IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");

				} else {
					sqlbuffer
							.append("AND userbasicsinfo.id IN ( SELECT userbasicinfo_id FROM borrowersbase WHERE auditResult = 1 )");
				}

			}

			// 注册时间查询
			if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
				sqlbuffer.append(" and userbasicsinfo.createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
			}
			if (StringUtil.isNotBlank(userinfo.getFailTime())) {
				sqlbuffer.append(" and userbasicsinfo.createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
			}
			
			//认购
			if(StringUtil.isNotBlank(isPurchase)){
				if(isPurchase.equals("1")){
					sqlbuffer.append(" and ( select IFNULL(sum(l.tenderMoney),0) from loanrecord l where l.userbasicinfo_id=userbasicsinfo.id and l.isSucceed=1 ) >0  ");
				}else{
					sqlbuffer.append(" and ( select IFNULL(sum(l.tenderMoney),0) from loanrecord l where l.userbasicinfo_id=userbasicsinfo.id and l.isSucceed=1 ) =0  ");
				}
			}
			
			//推荐人
			if(StringUtil.isNotBlank(isRecommend)){
				if(isRecommend.equals("1")){
					sqlbuffer.append(" and (SELECT COUNT(g.id) from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid)>0  ");
				}else{
					sqlbuffer.append(" and (SELECT COUNT(g.id) from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid)=0  ");
				}
			}

			// 根据客服
			if (StringUtil.isNotBlank(userinfo.getNickname())) {
				sqlbuffer.append("AND userrelationinfo.adminuser_id = ")
						.append(userinfo.getNickname());
			}
			// 登录次数查询
			if (null != userinfo.getIsLock()) {
				sqlbuffer
						.append(" and  (SELECT count(0) FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id)>"
								+ userinfo.getIsLock().intValue());
			}
			if (StringUtil.isNotBlank(userinfo.getLoginTime())) {
				sqlbuffer.append("AND userbasicsinfo.isOrgPerson = ").append(
						userinfo.getLoginTime());
			}
			if (null != userinfo.getUserType()) {
				sqlbuffer.append("AND userbasicsinfo.user_type = ").append(
						userinfo.getUserType());
			}
		}
		List returnList = dao.findBySql(sqlbuffer.toString());
		return returnList;

	}

	/**
	 * <p>
	 * Title: queryBrrowPage
	 * </p>
	 * <p>
	 * Description: 借款人查询
	 * </p>
	 * 
	 * @param page
	 *            分页参数
	 * @param userinfo
	 *            查询条件
	 * @return 返回查询结果集list
	 */
	@SuppressWarnings("rawtypes")
	public List queryBrrowPage(PageModel page, Userbasicsinfo userinfo) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryBrrowPage(PageModel page=" + page + ", Userbasicsinfo userinfo=" + userinfo + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		// 查询数据sql
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT borrowersbase.id, userbasicsinfo.userName, IFNULL(borrowersbase.suminte, 0),");
		sqlbuffer
				.append("  borrowersbase.addTime, IFNULL(borrowersbase.credit, 0), userbasicsinfo.createTime, ");
		sqlbuffer
				.append(" ( SELECT max(userloginlog.logintime) FROM userloginlog WHERE userloginlog.user_id = userbasicsinfo.id ),");
		sqlbuffer
				.append(" ( SELECT count(userloginlog.id) FROM userloginlog WHERE userloginlog.user_id = userbasicsinfo.id ),");
		sqlbuffer
				.append(" CASE WHEN ( SELECT vipinfo.endtime FROM vipinfo WHERE vipinfo.user_id = userbasicsinfo.id ORDER BY id DESC LIMIT 1 ) > (SELECT NOW()) THEN '特权会员' ELSE '普通会员' END, ");
		sqlbuffer
				.append(" CASE WHEN ( SELECT vipinfo.endtime FROM vipinfo WHERE vipinfo.user_id = userbasicsinfo.id ORDER BY id DESC LIMIT 1 ) > (SELECT NOW()) THEN ( SELECT vipinfo.endtime FROM vipinfo WHERE vipinfo.user_id = userbasicsinfo.id ORDER BY id DESC LIMIT 1 ) ELSE '永久' END,");
		sqlbuffer
				.append(" CASE WHEN borrowersbase.auditStatus = 2 THEN '1' WHEN borrowersbase.auditStatus = 3 AND borrowersbase.auditResult = 1 THEN '2' WHEN borrowersbase.auditStatus = 3 AND borrowersbase.auditResult = 0 THEN '3' ELSE '' END,userbasicsinfo.credit_rate  ");
		sqlbuffer
				.append(" FROM borrowersbase LEFT JOIN userbasicsinfo ON borrowersbase.userbasicinfo_id = userbasicsinfo.id WHERE borrowersbase.auditStatus > 1 ");

		// 统计数据条数
		StringBuffer countsql = new StringBuffer(
				"SELECT COUNT(1) FROM borrowersbase ");
		countsql.append("INNER JOIN userbasicsinfo ON borrowersbase.userbasicinfo_id=userbasicsinfo.id where borrowersbase.auditStatus>1 ");
		String chaxun = getBorrowQueryLi(userinfo);

		List returnList = dao.pageListBySql(page, countsql.toString() + chaxun,
				sqlbuffer.toString() + chaxun, null);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryBrrowPage(PageModel, Userbasicsinfo)方法结束OUTPARAM=" + returnList); //$NON-NLS-1$
		}
		return returnList;
	}

	/**
	 * <p>
	 * Title: getBorrowQueryLi
	 * </p>
	 * <p>
	 * Description:查询条件
	 * </p>
	 * 
	 * @param userinfo
	 * @return
	 */
	public String getBorrowQueryLi(Userbasicsinfo userinfo) {
		StringBuffer sbsql = new StringBuffer();
		if (StringUtil.isNotBlank(userinfo.getUserName())) {
			sbsql.append(" and userbasicsinfo.userName like '%")
					.append(StringUtil.replaceAll(userinfo.getUserName()))
					.append("%'");
		}
		// 身份证号模糊查询
		if (StringUtil.isNotBlank(userinfo.getName())) {
			sbsql.append(
					"AND userbasicsinfo.id in (SELECT id FROM userrelationinfo where cardId like '%")
					.append(StringUtil.replaceAll(userinfo.getName()))
					.append("%')");
		}
		// 注册时间查询
		if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
			sbsql.append(" and userbasicsinfo.createTime >= '")
					.append(userinfo.getCreateTime()).append(" 00:00:00'");
		}

		if (StringUtil.isNotBlank(userinfo.getFailTime())) {
			sbsql.append(" and userbasicsinfo.createTime <= '")
					.append(userinfo.getFailTime()).append(" 23:59:59'");
		}
		// 是否通过
		if (null != userinfo.getIsLock()) {
			if (userinfo.getIsLock() == 1) {
				sbsql.append(" and borrowersbase.auditStatus=2");
			}
			if (userinfo.getIsLock() == 2) {
				sbsql.append(" and borrowersbase.auditStatus=4 and borrowersbase.auditStatus=1");
			}
			if (userinfo.getIsLock() == 3) {
				sbsql.append(" and borrowersbase.auditStatus=4 and borrowersbase.auditStatus=0");
			}
		}
		// 积分
		if (null != userinfo.getErrorNum()) {
			sbsql.append(" and borrowersbase.suminte>=").append(
					userinfo.getErrorNum());
		}
		// 申请时间
		if (StringUtil.isNotBlank(userinfo.getpIpsAcctDate())) {
			sbsql.append(" and borrowersbase.addTime >= '")
					.append(userinfo.getpIpsAcctDate()).append(" 00:00:00'");
		}
		if (StringUtil.isNotBlank(userinfo.getpMerBillNo())) {
			sbsql.append(" and borrowersbase.addTime <= '")
					.append(userinfo.getpMerBillNo()).append(" 23:59:59'");
		}
		// 授信额度
		if (null != userinfo.getPassword()
				&& !StringUtil.isBlank(userinfo.getPassword())) {
			sbsql.append(" and borrowersbase.credit>=").append(
					userinfo.getPassword());
		}
		// 登陆次数
		if (null != userinfo.getId()
				&& StringUtil.isNotBlank(userinfo.getId().toString())) {
			sbsql.append(
					" and  (SELECT count(0) FROM userloginlog WHERE userloginlog.user_id=userbasicsinfo.id)>")
					.append(userinfo.getId());
		}
		return sbsql.toString();
	}

	/**
	 * <p>
	 * Title: queryBorrowBasinfo
	 * </p>
	 * <p>
	 * Description: 根据编号查询借款人基本信息
	 * </p>
	 * 
	 * @param ids
	 *            借款人主键编号
	 * @return 返回查询结果集list
	 */
	@SuppressWarnings("rawtypes")
	public List queryBorrowBasinfo(String ids) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryBorrowBasinfo(String ids=" + ids + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT userbasicsinfo.userName,borrowersbase.suminte,borrowersbase.credit,userbasicsinfo.name");
		sqlbuffer
				.append(", CASE WHEN (SELECT vipinfo.endtime FROM vipinfo WHERE vipinfo.user_id=userbasicsinfo.id ORDER BY id DESC LIMIT 1) > (SELECT NOW()) THEN '特权会员' ELSE '普通会员' END, ");
		sqlbuffer.append("userrelationinfo.phone,userbasicsinfo.createTime,");
		sqlbuffer
				.append("CASE WHEN (SELECT vipinfo.endtime FROM vipinfo WHERE vipinfo.user_id=userbasicsinfo.id ORDER BY id DESC LIMIT 1) > (SELECT NOW()) THEN (SELECT vipinfo.endtime FROM vipinfo WHERE vipinfo.user_id=userbasicsinfo.id ORDER BY id DESC LIMIT 1) ELSE '永久' END,");
		sqlbuffer
				.append("userrelationinfo.cardId,userrelationinfo.email FROM borrowersbase ");
		sqlbuffer
				.append(" LEFT JOIN userbasicsinfo ON userbasicsinfo.id=borrowersbase.userbasicinfo_id");
		sqlbuffer
				.append(" LEFT JOIN userrelationinfo ON userbasicsinfo.id=userrelationinfo.user_id");
		sqlbuffer.append(" where borrowersbase.id=" + Long.parseLong(ids));

		List returnList = dao.findBySql(sqlbuffer.toString());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryBorrowBasinfo(String)方法结束OUTPARAM=" + returnList); //$NON-NLS-1$
		}
		return returnList;

	}

	/**
	 * <p>
	 * Title: queryPersonal
	 * </p>
	 * <p>
	 * Description: 查询借款人个人资料
	 * </p>
	 * 
	 * @param ids
	 *            会员主键编号
	 * @return 返回查询的结果集list
	 */
	@SuppressWarnings("rawtypes")
	public List queryPersonal(String ids) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryPersonal(String ids=" + ids + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT (select name from userbasicsinfo where id=b.userbasicinfo_id),b.addTime,b.money, ");
		sqlbuffer
				.append("b.qualifications,(select cardId from userrelationinfo where user_id=b.userbasicinfo_id),b.income,b.age,b.phone, ");
		sqlbuffer.append("CASE WHEN b.marryStatus =1 THEN '已婚' ELSE '未婚' END,");
		sqlbuffer.append("CASE WHEN b.sex =1 THEN '男' ELSE '女' END,");
		sqlbuffer.append("b.remark FROM borrowersbase b WHERE b.id="
				+ Long.parseLong(ids));

		List returnList = dao.findBySql(sqlbuffer.toString());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryPersonal(String)方法结束OUTPARAM=" + returnList); //$NON-NLS-1$
		}
		return returnList;

	}

	/**
	 * <p>
	 * Title: queryAssignmentLoanSign
	 * </p>
	 * <p>
	 * Description: 查询债权转让标的信息
	 * </p>
	 * 
	 * @param ids
	 *            会员主键编号
	 * @return 返回查询的结果集list
	 */
	@SuppressWarnings("rawtypes")
	public List queryAssignmentLoanSign(String ids) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryAssignmentLoanSign(String ids=" + ids + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT b.name,c.rate*100 rate,a.loanNumber,a.loanTitle ,a.reward*100 reward,a.assure,a.unassureWay,");
		sqlbuffer
				.append("c.loanUnit,CASE WHEN a.riskAssess=1 THEN '低' WHEN a.riskAssess=2 THEN '中' ELSE '高' END  , CASE WHEN c.isShow=1 THEN '显示' ELSE '不显示' END ,CASE WHEN c.isRecommand=1 THEN '推荐' ELSE '不推荐' END  ,c.month,CASE WHEN c.refundWay =1 THEN '按月等额本息'WHEN c.refundWay =2 THEN '按月付息到期还本' ELSE '到期一次性还本息' END, c.counterparts,c.vipCounterparts,a.pBidNo,a.pContractNo,a.bidTime   from loansignbasics a,userbasicsinfo b, loansign c ");
		sqlbuffer
				.append("where a.id=c.id and c.userbasicinfo_id=b.id and a.id="
						+ Long.parseLong(ids));

		List returnList = dao.findBySql(sqlbuffer.toString());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryAssignmentLoanSign(String)方法结束OUTPARAM=" + returnList); //$NON-NLS-1$
		}
		return returnList;

	}

	/**
	 * <p>
	 * Title: queryContact
	 * </p>
	 * <p>
	 * Description: 查询借款人联系方式
	 * </p>
	 * 
	 * @param ids
	 *            借款人主键编号
	 * @return 返回Borrowerscontact对象
	 */
	public Borrowerscontact queryContact(String ids) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryContact(String ids=" + ids + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		String hql = "from Borrowerscontact where borrowersbase.id=" + ids;

		Borrowerscontact returnBorrowerscontact = (Borrowerscontact) dao
				.findObject(hql);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryContact(String)方法结束OUTPARAM=" + returnBorrowerscontact); //$NON-NLS-1$
		}
		return returnBorrowerscontact;
	}

	/**
	 * <p>
	 * Title: queryCompany
	 * </p>
	 * <p>
	 * Description: 查询借款人单位资料
	 * </p>
	 * 
	 * @param ids
	 *            借款人主键
	 * @return 返回 Borrowerscompany对象
	 */
	public Borrowerscompany queryCompany(String ids) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryCompany(String ids=" + ids + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		String hql = "from Borrowerscompany where borrowersbase.id=" + ids;

		Borrowerscompany returnBorrowerscompany = (Borrowerscompany) dao
				.findObject(hql);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryCompany(String)方法结束OUTPARAM=" + returnBorrowerscompany); //$NON-NLS-1$
		}
		return returnBorrowerscompany;
	}

	/**
	 * <p>
	 * Title: queryFinanes
	 * </p>
	 * <p>
	 * Description: 查询借款人财务状况
	 * </p>
	 * 
	 * @param ids
	 *            借款人主键
	 * @return 返回Borrowersfinanes对象
	 */
	public Borrowersfinanes queryFinanes(String ids) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryFinanes(String ids=" + ids + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		String hql = "from Borrowersfinanes where borrowersbase.id=" + ids;

		Borrowersfinanes returnBorrowersfinanes = (Borrowersfinanes) dao
				.findObject(hql);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryFinanes(String)方法结束OUTPARAM=" + returnBorrowersfinanes); //$NON-NLS-1$
		}
		return returnBorrowersfinanes;

	}

	/**
	 * <p>
	 * Title: queryOtherContact
	 * </p>
	 * <p>
	 * Description: 查询借款人联保信息
	 * </p>
	 * 
	 * @param ids
	 *            借款人主键
	 * @return 返回Borrowersothercontact对象
	 */
	public Borrowersothercontact queryOtherContact(String ids) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryOtherContact(String ids=" + ids + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		String hql = "from Borrowersothercontact where borrowersbase.id=" + ids;

		Borrowersothercontact returnBorrowersothercontact = (Borrowersothercontact) dao
				.findObject(hql);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryOtherContact(String)方法结束OUTPARAM=" + returnBorrowersothercontact); //$NON-NLS-1$
		}
		return returnBorrowersothercontact;
	}

	/**
	 * <p>
	 * Title: queryFiles
	 * </p>
	 * <p>
	 * Description: 查询借款人上传的文件
	 * </p>
	 * 
	 * @param ids
	 *            借款人主键
	 * @param typeid
	 *            查询文件类型 1是图片，2是其它文件
	 * @return 返回查询结果集list
	 */
	@SuppressWarnings("unchecked")
	public List<Borrowersfiles> queryFiles(String ids, String typeid) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryFiles(String ids=" + ids + ", String typeid=" + typeid + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		String hql = "from Borrowersfiles where borrowersbase.id=" + ids
				+ " and fileType=" + typeid;

		List<Borrowersfiles> returnList = (List<Borrowersfiles>) dao.query(hql,
				true);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryFiles(String, String)方法结束OUTPARAM=" + returnList); //$NON-NLS-1$
		}
		return returnList;

	}

	/**
	 * <p>
	 * Title: disUser
	 * </p>
	 * <p>
	 * Description: 根据传入的会员编号及状态修改会员状态
	 * </p>
	 * 
	 * @param ids
	 *            要修改状态的会员
	 * @param status
	 *            要修改的状态
	 * @return 返回修改的结果，boolean
	 */
	public boolean updateUserStatus(String ids, String status) {
		boolean flag = true;
		if (StringUtil.isNotBlank(ids)) {
			// 根据“，”拆分字符串
			String[] newids = ids.split(",");
			// 要修改状态的编号
			String delstr = "";
			for (String idstr : newids) {
				// 将不是空格和非数字的字符拼接
				if (StringUtil.isNotBlank(idstr)
						&& StringUtil.isNumberString(idstr)) {
					delstr += idstr + ",";
				}
			}
			// 如果确认修改的字符串不为空
			if (delstr.length() > 0) {
				StringBuffer sql = new StringBuffer(
						"UPDATE userbasicsinfo SET userbasicsinfo.isLock=");
				if ("1".equals(status)) {
					sql.append(status + ",userbasicsinfo.lockTime='"
							+ DateUtils.format("yyyy-MM-dd HH:mm:ss") + "'");
				} else {
					sql.append(status + ",userbasicsinfo.lockTime= NULL ");
				}
				sql.append(" WHERE userbasicsinfo.id IN("
						+ delstr.substring(0, delstr.length() - 1) + ")");
				// 批量修改
				if (dao.executeSql(sql.toString()) <= 0) {
					flag = false;
				}
			}
		}
		return flag;
	}

	/**
	 * 解锁会员
	 * 
	 * @param ids
	 * @param status
	 * @return
	 */
	public boolean updateUserLock(String ids, String status) {
		boolean flag = true;
		if (StringUtil.isNotBlank(ids)) {
			// 根据“，”拆分字符串
			String[] newids = ids.split(",");
			// 要修改状态的编号
			String delstr = "";
			for (String idstr : newids) {
				// 将不是空格和非数字的字符拼接
				if (StringUtil.isNotBlank(idstr)
						&& StringUtil.isNumberString(idstr)) {
					delstr += idstr + ",";
				}
			}
			// 如果确认修改的字符串不为空
			if (delstr.length() > 0) {
				StringBuffer sql = new StringBuffer(
						"update userbasicsinfo set userbasicsinfo.failTime='',userbasicsinfo.errorNum="
								+ status);
				sql.append(" WHERE userbasicsinfo.id IN("
						+ delstr.substring(0, delstr.length() - 1) + ")");
				// 批量修改
				if (dao.executeSql(sql.toString()) <= 0) {
					flag = false;
				}
			}
		}
		return flag;
	}

	/**
	 * 机构投资审请修改
	 * 
	 * @param ids
	 * @param audit
	 * @return
	 */
	public String updateUserresultsofinvestment(String ids, int audit,
			HttpServletRequest request) {
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(
				Constant.ADMINLOGIN_SUCCESS);
		if (StringUtil.isNotBlank(ids)) {
			// 根据“，”拆分字符串
			String[] newids = ids.split(",");
			// 要修改状态的编号
			String delstr = "";
			for (String idstr : newids) {
				// 将不是空格和非数字的字符拼接
				if (StringUtil.isNotBlank(idstr)
						&& StringUtil.isNumberString(idstr)) {
					delstr += idstr + ",";
				}
			}

			String sqluser = "select * from userrelationinfo u where u.user_id in("
					+ delstr.substring(0, delstr.length() - 1)
					+ ") and u.audit!=1";
			List<Userrelationinfo> userinfo = dao.findBySql(sqluser,
					Userrelationinfo.class);
			if (userinfo.size() > 0) {
				return "1";
			}
			if (delstr.length() > 0) {
				StringBuffer sql = new StringBuffer(
						"UPDATE userrelationinfo SET userrelationinfo.audit=");

				sql.append(audit);
				sql.append(
						",userrelationinfo.manacerttime='"
								+ DateUtils.format("yyyy-MM-dd HH:mm:ss"))
						.append("'");
				sql.append(",userrelationinfo.adminuser_idauch="
						+ loginuser.getId());
				sql.append(" WHERE userrelationinfo.user_id IN("
						+ delstr.substring(0, delstr.length() - 1) + ")");
				// 批量修改
				dao.executeSql(sql.toString());

				if (audit == 2) {
					// 先判断是否全部都能审核
					StringBuffer updatesqls = new StringBuffer(
							" UPDATE  userbasicsinfo set userbasicsinfo.isOrgPerson=1 WHERE userbasicsinfo.id=")
							.append(ids.substring(0, ids.length() - 1) + "");
					if (dao.executeSql(updatesqls.toString()) <= 0) {
						return "1";
					}
				}

			}
		}
		return "2";
	}

	/**
	 * 身份认证资格审请修改
	 * 
	 * @param ids
	 * @param audit
	 * @return
	 */
	public String updateUserbasicardstatus(String ids, int audit,
			HttpServletRequest request) {
		Adminuser loginuser = (Adminuser) request.getSession().getAttribute(
				Constant.ADMINLOGIN_SUCCESS);
		if (StringUtil.isNotBlank(ids)) {
			// 根据“，”拆分字符串
			String[] newids = ids.split(",");
			// 要修改状态的编号
			String delstr = "";
			for (String idstr : newids) {
				// 将不是空格和非数字的字符拼接
				if (StringUtil.isNotBlank(idstr)
						&& StringUtil.isNumberString(idstr)) {
					delstr += idstr + ",";
				}
			}
			System.out.println(delstr.substring(0, delstr.length() - 1));
			String sqluser = "select * from userbasicsinfo u where u.id in("
					+ delstr.substring(0, delstr.length() - 1)
					+ ") and u.cardStatus!=1";
			List<Userbasicsinfo> users = dao.findBySql(sqluser,
					Userbasicsinfo.class);
			if (users.size() > 0) {
				return "1";
			}

			if (delstr.length() > 0) {
				StringBuffer sql = new StringBuffer(
						"UPDATE userbasicsinfo SET userbasicsinfo.cardStatus=");

				sql.append(audit);
				sql.append(
						",userbasicsinfo.manatoverifyuser='"
								+ DateUtils.format("yyyy-MM-dd HH:mm:ss"))
						.append("' ");
				sql.append(",userbasicsinfo.adminuser_id=" + loginuser.getId());

				sql.append(" WHERE userbasicsinfo.id IN("
						+ delstr.substring(0, delstr.length() - 1) + ")");
				// 批量修改
				dao.executeSql(sql.toString());

			}
		}

		return "2";
	}

	/*
	 * public boolean updateUserStatus(String ids, String status) { boolean flag
	 * = true; if (StringUtil.isNotBlank(ids)) { // 根据“，”拆分字符串 String[] newids =
	 * ids.split(","); // 要修改状态的编号 String delstr = ""; for (String idstr :
	 * newids) { // 将不是空格和非数字的字符拼接 if (StringUtil.isNotBlank(idstr) &&
	 * StringUtil.isNumberString(idstr)) { delstr += idstr + ","; } } //
	 * 如果确认修改的字符串不为空 if (delstr.length() > 0) { StringBuffer sql = new
	 * StringBuffer( "UPDATE userbasicsinfo SET userbasicsinfo.isLock="); if
	 * ("1".equals(status)) { sql.append(status + ",userbasicsinfo.lockTime='" +
	 * DateUtils.format("yyyy-MM-dd HH:mm:ss") + "'"); } else {
	 * sql.append(status + ",userbasicsinfo.lockTime= NULL "); }
	 * sql.append(" WHERE userbasicsinfo.id IN(" + delstr.substring(0,
	 * delstr.length() - 1) + ")"); // 批量修改 if (dao.executeSql(sql.toString())
	 * <= 0) { flag = false; } } } return flag; }
	 */

	/**
	 * <p>
	 * Title: updateMemberDate
	 * </p>
	 * <p>
	 * Description: 修改会员特权期限
	 * </p>
	 * 
	 * @param endtime
	 *            特权结束时间
	 * @param ids
	 *            要修改会员编号
	 * @return 修改结果 ，返回boolean
	 */
	public boolean updateMemberDate(String endtime, String ids) {

		boolean flag = false;

		// 判断传入编号是否是纯数字
		if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {
			// 根据传入编号查询会员基本信息
			Userbasicsinfo userbaseinfo = dao.get(Userbasicsinfo.class,
					Long.parseLong(ids));
			// 判断会员是否存在
			if (null != userbaseinfo) {
				Vipinfo newvip = new Vipinfo();

				newvip.setBegintime(DateUtils.format(null));
				newvip.setEndtime(endtime + " 23:59:59");
				newvip.setUserbasicsinfo(userbaseinfo);
				newvip.setTime(DateUtils.format(null));
				// 保存特权会员信息
				dao.save(newvip);
				flag = true;
			}

		}
		return flag;
	}

	/**
	 * <p>
	 * Title: queryRelationById
	 * </p>
	 * <p>
	 * Description: 根据会员基本信息编号查询会员联系信息
	 * </p>
	 * 
	 * @param ids
	 *            会员基本信息编号
	 * @return 返回会员联系信息
	 */
	@SuppressWarnings("unchecked")
	public Userrelationinfo queryRelationById(String ids) {

		List<Userrelationinfo> relationList = new ArrayList<Userrelationinfo>();
		// 判断传入ids是否为纯数字
		if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {

			String hql = "from Userrelationinfo where userbasicsinfo.id=" + ids;

			relationList = dao.find(hql);

		}
		// 判断是否查询到数据并返回
		if (null != relationList && !relationList.isEmpty()) {

			return relationList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * <p>
	 * Title: queryBasicsInfoById
	 * </p>
	 * <p>
	 * Description: 根据编号查询会员基本信息
	 * </p>
	 * 
	 * @param ids
	 *            会员编号
	 * @return 返回查询会员基本信息
	 */
	public Userbasicsinfo queryBasicsInfoById(String ids) {

		Userbasicsinfo basicsInfo = null;

		// 判断传入编号是否是纯数字
		if (StringUtil.isNotBlank(ids) && StringUtil.isNumberString(ids)) {

			basicsInfo = dao.get(Userbasicsinfo.class, Long.parseLong(ids));
		}

		return basicsInfo;
	}

	/***
	 * 修改授权
	 * 
	 * @param ids
	 * @param user
	 */
	public void updateAuthorization(String ids, Userbasicsinfo user) {
		Userbasicsinfo userbasicsinfo = this.queryBasicsInfoById(ids);
		// 判断会员信息是否存在
		if (null != userbasicsinfo) {
			userbasicsinfo.setIscrowdfundingperson(user
					.getIscrowdfundingperson()); // 众筹融资人
			userbasicsinfo.setIsorgperson(user.getIsorgperson()); // 机构投资人
			userbasicsinfo.setIscrowdhold(user.getIscrowdhold()); // 众持融资人
			if (1 == user.getIscrowdhold()) {
				userbasicsinfo.setIsCreditor(2);
			} else if (0 == user.getIscrowdhold()) {
				userbasicsinfo.setIsCreditor(1);
			}
			if (user.getLoanlines() == null) {
				userbasicsinfo.setLoanlines(0.00); // 机构投资额度
			} else {
				userbasicsinfo.setLoanlines(user.getLoanlines()); // 机构投资额度
			}

			dao.update(userbasicsinfo);
		}
	}

	/**修改为借款人*/
	public void updateIsCreditor(String ids, Userbasicsinfo user){
		Userbasicsinfo userbasicsinfo=this.queryBasicsInfoById(ids);
		if(userbasicsinfo!=null){
			if(2==user.getIsCreditor()){
				userbasicsinfo.setIsCreditor(2);
			} else{
				userbasicsinfo.setIsCreditor(1);
			}
		}
		dao.update(userbasicsinfo);
	}
	
	/***
	 * 分配工号
	 * 
	 * @param ids
	 * @param user
	 */
	public void updateUserType(String ids, Userbasicsinfo user) {
		Userbasicsinfo userbasicsinfo = this.queryBasicsInfoById(ids);
		// 判断会员信息是否存在
		if (null != userbasicsinfo) {

			if (user.getUserType() == 2) {// 设置成员工
				if (userbasicsinfo.getUserType() == 2) {// 已是员工
					userbasicsinfo.setDepartment(user.getDepartment());
				} else {// 还不是员工
					userbasicsinfo.setUserType(user.getUserType());
					userbasicsinfo.setDepartment(user.getDepartment());
					userbasicsinfo.setStaffNo(getStaffNo());
					// 判断是否被老员工推荐
					String sql = "SELECT * FROM generalize g WHERE "
							+ "g.genuid IN (SELECT u.id FROM userbasicsinfo u) and g.uid="
							+ ids;
					Generalize generUser = dao.findObjectBySql(sql,
							Generalize.class);
					if (generUser != null) {// 存在，则删除
						dao.delete(generUser);
					}
				}

			} else {
				// 不是员工
				if(user.getUserType() == 4){
					// 判断是否被推荐
					String sql = "SELECT * FROM generalize g WHERE "
							+ "g.genuid IN (SELECT u.id FROM userbasicsinfo u where u.user_type!=2) and g.uid="
							+ ids;
					Generalize generUser = dao.findObjectBySql(sql,
							Generalize.class);
					if (generUser != null) {// 存在，则删除
						dao.delete(generUser);
					}
				}
				userbasicsinfo.setUserType(user.getUserType());
				userbasicsinfo.setDepartment(null);
				userbasicsinfo.setStaffNo(null);
			}

			dao.update(userbasicsinfo);
		}
	}

	/***
	 * 分配企业编号
	 * 
	 * @param ids
	 * @param user
	 */
	public Integer updateCompanyNo(String ids, Userbasicsinfo user) {
		Userbasicsinfo userbasicsinfo = this.queryBasicsInfoById(ids);
		// 判断会员信息是否存在
		if (null != userbasicsinfo && selStaffNo(user.getStaffNo())) {
			userbasicsinfo.setUserType(3);
			userbasicsinfo.setStaffNo(user.getStaffNo());
			dao.update(userbasicsinfo);
			return 1;
		} else {
			return 2;
		}
	}

	/***
	 * 判断企业编号是否存在
	 * 
	 * @param staffNo
	 * @return
	 */
	public boolean selStaffNo(String staffNo) {
		String sql = "select * from userbasicsinfo where staff_no like '"
				+ staffNo + "%'";
		Object staff_no = dao.findObjectBySql(sql);
		if (staff_no == null) {
			return true;
		} else {
			return false;
		}
	}

	/***
	 * 设置信用等级
	 * 
	 * @param ids
	 * @param user
	 */
	public void updateCreditRate(String ids, Userbasicsinfo user) {
		Userbasicsinfo userbasicsinfo = this.queryBasicsInfoById(ids);
		// 判断会员信息是否存在
		if (null != userbasicsinfo) {
			userbasicsinfo.setCreditRate(user.getCreditRate());
			dao.update(userbasicsinfo);
		}
	}

	/***
	 * 获取员工工号5位，不足补零
	 * 
	 * @return
	 */
	public String getStaffNo() {
		String sql = "select MAX(u.staff_no) from userbasicsinfo  u where user_Type!=3";
		Object number = dao.findObjectBySql(sql);
		if (number == null || number == "") {
			number = 1;
		} else {
			number = Integer.parseInt(number.toString()) + 1;
		}
		String StaffNo = StringUtil.addZeroForNum(number.toString(), 5);
		return StaffNo;
	}

	/**
	 * <p>
	 * Title: updateRelation
	 * </p>
	 * <p>
	 * Description: 修改会员联系信息
	 * </p>
	 * 
	 * @param ids
	 *            会员基本信息编号
	 * @param relation
	 *            要修改的联系信息
	 */
	public void updateRelation(String ids, Userrelationinfo relation) {

		Userrelationinfo loadRelation = this.queryRelationById(ids);

		// 判断会员联系信息是否存在
		if (null != loadRelation) {
			loadRelation.setCardId(relation.getCardId());
			loadRelation.setEmail(relation.getEmail());
			loadRelation.setPhone(relation.getPhone());

			// 修改会员的联系信息
			dao.update(loadRelation);
		}

	}

	/**
	 * <p>
	 * Title: udapteBasic
	 * </p>
	 * <p>
	 * Description: 修改会员基本信息
	 * </p>
	 * 
	 * @param ids
	 *            基本信息的主键
	 * @param basicinfo
	 *            要修改的基本信息
	 */
	public void udapteBasic(String ids, Userbasicsinfo basicinfo) {

		Userbasicsinfo loadBasic = this.queryBasicsInfoById(ids);

		// 判断会员信息是否存在
		if (null != loadBasic) {
			loadBasic.setNickname(basicinfo.getNickname());
			loadBasic.setName(basicinfo.getName());

			dao.update(loadBasic);
		}
	}

	/**
	 * <p>
	 * Title: updateCredit
	 * </p>
	 * <p>
	 * Description: 修改借款人授信额度
	 * </p>
	 * 
	 * @param ids
	 *            借款人主键
	 * @param credit
	 *            要修改成的授信额度
	 * @return 数据库受影响的行数
	 */
	public int updateCredit(String ids, String credit) {

		int result = 0;

		// 授信额度是否为空
		if (StringUtil.isNotBlank(credit)) {
			// 判断授信额度中是否有小数点
			if (credit.indexOf(".") != -1) {
				credit = credit.substring(0, credit.indexOf("."));
			}
		}

		// 修改授信额度
		if (StringUtil.isNotBlank(ids)
				&& StringUtil.isNumberString(ids + credit)) {
			String sql = "UPDATE borrowersbase SET credit="
					+ credit
					+ " WHERE auditStatus=3 AND auditResult=1 AND borrowersbase.id="
					+ ids;

			result = dao.executeSql(sql);

			if (result > 0) {
				Borrowersbase borrowersbase = dao.get(Borrowersbase.class,
						Long.parseLong(ids));

				sql = "UPDATE userfundinfo SET credit=" + credit + " WHERE id="
						+ borrowersbase.getUserbasicsinfo().getId();

				result = dao.executeSql(sql);
			}
		}

		// 返回受影响的行数
		return result;
	}

	/**
	 * 
	 * <p>
	 * Title: addUser
	 * </p>
	 * <p>
	 * Description: 添加会员调用的方法，保存手机号码和身份证号码
	 * </p>
	 * 
	 * @param basic
	 *            会员基本信息
	 * @param cardId
	 *            身份证号码
	 * @param phone
	 *            手机号码
	 * @return 数据库受影响的行数
	 */
	public int addUser(Userbasicsinfo basic, String cardId, String phone) {

		String sql = "UPDATE userrelationinfo SET userrelationinfo.cardId="
				+ cardId + ",userrelationinfo.phone=" + phone
				+ " WHERE userrelationinfo.user_id=" + basic.getId();

		int result = 0;

		result = dao.executeSql(sql);

		return result;

	}

	/**
	 * <p>
	 * Title: getUserMoney
	 * </p>
	 * <p>
	 * Description: 后台会员资金统计
	 * </p>
	 * 
	 * @param ids
	 *            会员编号
	 * @param request
	 *            HttpServletRequest
	 */
	public void getUserMoney(String ids, HttpServletRequest request) {

		Userbasicsinfo user = dao.get(Userbasicsinfo.class, Long.valueOf(ids));
		Userfundinfo userfund = dao.get(Userfundinfo.class, user.getId());
		request.setAttribute("sumMoney", userfund.getCashBalance());
		// 待收本金金额
		Object toBeClosed = myMoneyService.toBeClosed(user.getId());
		request.setAttribute("toBeClosed", toBeClosed);
		// 待付本息金额
		Object colltionPrinInterest = myMoneyService.colltionPrinInterest(user
				.getId());
		request.setAttribute("colltionPrinInterest", colltionPrinInterest);

		// 待确认投标
		Object lentBid = memberCenterService.investmentRecords(user.getId(), 2);
		request.setAttribute("lentBid", lentBid);
		// 待确认提现
		Object withdrawTobe = myMoneyService.withdrawTobe(user.getId());
		request.setAttribute("withdrawTobe", withdrawTobe);
		// 待确认充值
		Object rechargeTobe = myMoneyService.rechargeTobe(user.getId());
		request.setAttribute("rechargeTobe", rechargeTobe);
		// 累计奖励
		Object accumulative = myMoneyService.accumulative(user.getId());
		request.setAttribute("accumulative", accumulative);
		// 平台累计支付
		Object adminAccumulative = myMoneyService.adminAccumulative(user
				.getId());
		request.setAttribute("adminAccumulative", adminAccumulative);
		// 账户资产总额=可用现金金额 + 待确认投标+待确认提现 – 平台累计支付

		// 净赚利息
		Object netInterest = myMoneyService.netInterest(user.getId());
		request.setAttribute("netInterest", netInterest);
		// 累计支付会员费
		Object vipSum = myMoneyService.vipSum(user.getId());
		request.setAttribute("vipSum", vipSum);
		// 累计提现手续费
		Object witharwDeposit = myMoneyService.witharwDeposit(user.getId());
		request.setAttribute("witharwDeposit", witharwDeposit);
		// 累计盈亏总额= 净赚利息 - 累计支付会员费 - 累计提现手续费

		// 累计投资金额
		Object investmentRecords = myMoneyService.investmentRecords(user
				.getId());
		request.setAttribute("investmentRecords", investmentRecords);
		// 累计借入金额
		Object borrowing = myMoneyService.borrowing(user.getId());
		request.setAttribute("borrowing", borrowing);
		// 累计充值金额
		Object rechargeSuccess = myMoneyService.rechargeSuccess(user.getId());
		request.setAttribute("rechargeSuccess", rechargeSuccess);
		// 累计提现金额
		Object withdrawSucess = myMoneyService.withdrawSucess(user.getId());
		request.setAttribute("withdrawSucess", withdrawSucess);
		// 累计支付佣金
		Object commission = myMoneyService.commission(user.getId());
		request.setAttribute("commission", commission);
		// 待收利息总额
		Object interestToBe = myMoneyService.interestToBe(user.getId());
		request.setAttribute("interestToBe", interestToBe);

		// 待付利息总额
		Object colltionInterest = myMoneyService.colltionInterest(user.getId());
		request.setAttribute("colltionInterest", colltionInterest);
	}

	/**
	 * <p>
	 * Title: updateCustomer
	 * </p>
	 * <p>
	 * Description: 分配客服
	 * </p>
	 * 
	 * @param uid
	 *            会员编号
	 * @param adminid
	 *            客服人员
	 */
	public void updateCustomer(String uid, String adminid) {
		String hql = "From Userbasicsinfo u where u.id=?";
		Userbasicsinfo user = (Userbasicsinfo) dao.findObject(hql,
				Long.valueOf(uid));
		Adminuser admin = dao.get(Adminuser.class, Long.parseLong(adminid));
		if (null != user && null != admin) {
			user.setAdminuser(admin);
			user.getUserrelationinfo().setAdminuser(admin);
			dao.update(user);
		}
	}

	/**
	 * <p>
	 * Title: getUsercount
	 * </p>
	 * <p>
	 * Description: 未读短信数量、统计会员总数、特权会员数量、普通会员数量
	 * </p>
	 * 
	 * @param adminid
	 * @return 会员总数,特权会员数量,普通会员数量、在线会员数量
	 */
	public String getUsercount(String adminid) {
		// 查询该后台服务人员相关的短信条数
		String sql = "SELECT COUNT(id) FROM adminmessage WHERE adminuser_id ="
				+ adminid + " and isread=0";
		Object unReadMsgs = dao.findObjectBySql(sql);
		int unRead_msgs = Integer.parseInt(unReadMsgs.toString());

		Object allcount = dao.findObjectBySql(SQLUtils.USER_COUNT);

		int sum_count = 0;

		if (null != allcount) {
			sum_count = Integer.parseInt(allcount.toString());
		}

		Object vipcount = dao.findObjectBySql(SQLUtils.VIP_COUNT);

		int vip_count = 0;

		if (null != vipcount) {
			vip_count = Integer.parseInt(vipcount.toString());
		}

		return unRead_msgs + "," + sum_count + "," + vip_count + ","
				+ (sum_count - vip_count) + "," + ul.getUserCount();

	}

	/**
	 * <p>
	 * Title: queryuserrelistjxByuId
	 * </p>
	 * <p>
	 * Description:竞标中的投资列表
	 * </p>
	 * 
	 * @param page
	 * @param uid
	 * @return 查询结果集
	 */
	public List queryuserrelistjxByuId(PageModel page, String uid, int state) {

		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT dlr.id, ls.id, lsb.loanNumber, (SELECT name from userbasicsinfo where id=ls.userbasicinfo_id), dlr.tenderTime, dlr.tenderMoney, ");
		sqlbuffer
				.append(" ROUND(ls.rate*100,2), ls.`month`, ls.useDay, ls.loanType, ls.issueLoan FROM loanrecord dlr INNER JOIN");
		sqlbuffer
				.append(" loansign ls ON dlr.loanSign_id = ls.id INNER JOIN loansignbasics lsb ON ls.id = lsb.id");

		StringBuffer sqlcount = new StringBuffer(
				"SELECT count(0) FROM loanrecord dlr INNER JOIN");
		sqlcount.append(" loansign ls ON dlr.loanSign_id = ls.id INNER JOIN loansignbasics lsb ON ls.id = lsb.id");

		if (state == 2) {
			sqlbuffer
					.append(" WHERE ( ls.state = 2 OR ( ls.loanType = 4 AND ls.state IN (2, 3) ");
			sqlbuffer
					.append(" AND lsb.creditTime IS NULL )) AND dlr.userbasicinfo_id =")
					.append(uid);

			sqlcount.append(" WHERE ( ls.state = 2 OR ( ls.loanType = 4 AND ls.state IN (2, 3) ");
			sqlcount.append(
					" AND lsb.creditTime IS NULL )) AND dlr.userbasicinfo_id =")
					.append(uid);
		} else if (state == 3) {
			sqlbuffer
					.append(" WHERE ls.state = 3 AND ( ls.loanType < 3 OR ( ls.loanType = 4 AND lsb.creditTime IS NOT NULL )) AND dlr.userbasicinfo_id =")
					.append(uid);
			sqlcount.append(
					" WHERE ls.state = 3 AND ( ls.loanType < 3 OR ( ls.loanType = 4 AND lsb.creditTime IS NOT NULL )) AND dlr.userbasicinfo_id =")
					.append(uid);
		} else if (state == 4) {
			sqlbuffer.append(" WHERE ls.state = 4 AND dlr.userbasicinfo_id =")
					.append(uid);
			sqlcount.append(" WHERE ls.state = 4 AND dlr.userbasicinfo_id =")
					.append(uid);
		} else {// 查询所有
			sqlbuffer.append(" WHERE dlr.userbasicinfo_id =").append(uid);
			sqlcount.append(" WHERE dlr.userbasicinfo_id =").append(uid);
		}
		return dao.pageListBySql(page, sqlcount.toString(),
				sqlbuffer.toString(), null);
	}

	/**
	 * 资料上传
	 * 
	 * @param ids
	 *            编号
	 * @return 资料上传
	 */
	@SuppressWarnings("rawtypes")
	public List queryDataUpload(String ids) {
		String sql = "SELECT b.fileName,b.fileRemark,b.addTime,b.fileType,b.id FROM "
				+ "borrowersfiles b WHERE b.fileType!='商业图片' "
				+ "AND b.base_id=" + ids;
		List list = dao.findBySql(sql);
		return list;
	}

	/**
	 * 借款人商业图片
	 * 
	 * @param ids
	 *            编号
	 * @return 借款人商业图片
	 */
	@SuppressWarnings("rawtypes")
	public List queryStockPhoto(String ids) {
		String sql = "SELECT b.filePath,b.fileName,b.addTime FROM "
				+ "borrowersfiles b WHERE b.fileType='商业图片' "
				+ "AND b.base_id=" + ids;
		List list = dao.findBySql(sql);
		return list;
	}

	/**
	 * <p>
	 * Title: querybljxByuserId
	 * </p>
	 * <p>
	 * Description: 借入记录
	 * </p>
	 * 
	 * @param page
	 * @param uid
	 * @return 数据
	 */
	public List querybljxByuserId(PageModel page, String uid, int state) {

		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT ls.id,lsb.loanNumber,ROUND(ls.issueLoan,0),ls.publishTime,ROUND(ls.rate*100,2),lsb.creditTime from loansign ls");
		sqlbuffer
				.append(" INNER JOIN loansignbasics lsb on lsb.id=ls.id  where ls.state=")
				.append(state).append(" and ls.userbasicinfo_id=").append(uid);

		StringBuffer sqlcount = new StringBuffer(
				"SELECT count(0) from loansign ls");
		sqlcount.append(
				" INNER JOIN loansignbasics lsb on lsb.id=ls.id  where ls.state=")
				.append(state).append(" and ls.userbasicinfo_id=").append(uid);

		return dao.pageListBySql(page, sqlcount.toString(),
				sqlbuffer.toString(), null);
	}

	/**
	 * <p>
	 * Title: updateborrowState
	 * </p>
	 * <p>
	 * Description:审核借款人
	 * </p>
	 * 
	 * @param ids
	 *            编号
	 * @param state
	 *            通过和不通过
	 * @return 是否成功
	 */
	public boolean updateborrowState(HttpServletRequest request, String ids,
			int state) {
		// 如果确认修改的字符串不为空
		if (ids.length() > 0) {
			// 先判断是否全部都能审核
			StringBuffer updatesql = new StringBuffer(
					"update borrowersbase set auditStatus=3,auditResult=")
					.append(state).append(" where auditStatus=2 and id in (")
					.append(ids.substring(0, ids.length() - 1)).append(")");
			// 批量修改
			if (dao.executeSql(updatesql.toString()) <= 0) {
				return false;
			} else {
				if (state == 1) {
					// 先判断是否全部都能审核
					StringBuffer updatesqls = new StringBuffer(
							" UPDATE  userbasicsinfo set userbasicsinfo.isCrowdHold=1 , userbasicsinfo.isCreditor=2 WHERE (userbasicsinfo.id= (SELECT  bs.userbasicinfo_id from borrowersbase bs where bs.id=")
							.append(ids.substring(0, ids.length() - 1) + "))");
					if (dao.executeSql(updatesqls.toString()) <= 0) {
						return false;
					}
				}

			}

		}
		return true;
	}

	/**
	 * <p>
	 * Title: ispassture
	 * </p>
	 * <p>
	 * Description: 查询会员申请是否已经审核
	 * </p>
	 * 
	 * @param ids
	 *            会员编号
	 * @return 查询结果
	 */
	public boolean ispassture(String ids) {
		StringBuffer sqlcount = new StringBuffer(
				"SELECT count(1) from borrowersbase where auditStatus!=2 and id in (")
				.append(ids.substring(0, ids.length() - 1)).append(")");
		Object obj = dao.findObjectBySql(sqlcount.toString());
		if (Integer.parseInt(obj.toString()) > 0) {
			return false;
		}
		return true;
	}

	/**
	 * <p>
	 * Title: updatevip
	 * </p>
	 * <p>
	 * Description: 修改会员等级
	 * </p>
	 * 
	 * @param user
	 *            会员信息
	 * @param endtime
	 *            结束时间
	 * @return 是否成功
	 */
	public void updatevip(Userbasicsinfo user, String endtime) {
		Vipinfo vipinfo = new Vipinfo();
		vipinfo.setBegintime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		vipinfo.setEndtime(endtime + DateUtils.format(" HH:mm:ss"));
		vipinfo.setUserbasicsinfo(user);
		Object obj = dao.save(vipinfo);

		if (null != obj) {
			Usermessage message = new Usermessage();
			message.setContext("尊敬的会员:" + user.getUserName()
					+ "后台管理员已将你设置为特权会员，特权会员到期时间为:" + vipinfo.getEndtime()
					+ ";会员到期时间已最大的时间为准！若" + vipinfo.getEndtime()
					+ "小于以前设置的时间，则以上次到期时间为准！如有疑问，请联系客服！");
			message.setTitle("特权会员升级通知");
			message.setIsread(0);
			message.setReceivetime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
			message.setUserbasicsinfo(user);
			dao.save(message);
		}
	}

	/**
	 * <p>
	 * Title: queryPage
	 * </p>
	 * <p>
	 * Description: 债权转让
	 * </p>
	 * 
	 * @param page
	 *            分页参数
	 * @param userinfo
	 *            查询条件
	 * @return 返回查询结果集list
	 */
	@SuppressWarnings("rawtypes")
	public List queryPage(PageModel page, Userbasicsinfo userinfo) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryPage(PageModel page=" + page + ", Userbasicsinfo userinfo=" + userinfo + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		// 查询数据sql
		StringBuffer sqlbuffer = new StringBuffer(
				"select c.id,b.flowid ,e.userName ,e.name,c.name as loanTitle,d.loanNumber,c.state ,b.auditStatus,b.auditResult,b.tenderMoney ,e.id ,b.loan_id,b.shift_time,b.user_auth,b.user_debt ");
		sqlbuffer
				.append(" from userbasicsinfo e,loansign c, loansignflow b,loansignbasics d where  c.id=b.loan_id and e.id=b.user_debt and c.id=d.id order by c.id desc ");

		// 统计数据条数
		StringBuffer countsql = new StringBuffer(
				"SELECT COUNT(1) from userbasicsinfo e,loansign c, loansignflow b,loansignbasics d where  c.id=b.loan_id and e.id=b.user_debt and c.id=d.id");
		String chaxun = getQueryAssignmentLi(userinfo);

		List returnList = dao.pageListBySql(page, countsql.toString() + chaxun,
				sqlbuffer.toString() + chaxun, null);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryPage(PageModel, Userbasicsinfo)方法结束OUTPARAM=" + returnList); //$NON-NLS-1$
		}
		return returnList;
	}

	/**
	 * <p>
	 * Title: getBorrowQueryLi
	 * </p>
	 * <p>
	 * Description:查询条件
	 * </p>
	 * 
	 * @param userinfo
	 * @return
	 */
	public String getQueryAssignmentLi(Userbasicsinfo userinfo) {
		StringBuffer sbsql = new StringBuffer();
		if (StringUtil.isNotBlank(userinfo.getUserName())) {
			sbsql.append(" and e.userName like '%")
					.append(StringUtil.replaceAll(userinfo.getUserName()))
					.append("%'");
		}
		if (StringUtil.isNotBlank(userinfo.getNickname())) {
			sbsql.append(" and d.loanNumber like '%")
					.append(StringUtil.replaceAll(userinfo.getNickname()))
					.append("%'");
		}

		// 是否通过
		if (null != userinfo.getIsLock()) {
			if (userinfo.getIsLock() == 1) {
				sbsql.append(" and b.auditStatus=1");
			}
			if (userinfo.getIsLock() == 2) { // 通过
				sbsql.append(" and b.auditStatus=3 and b.auditResult=1");
			}
			if (userinfo.getIsLock() == 3) { // 不通过
				sbsql.append(" and b.auditStatus=3 and b.auditResult=0");
			}
		}
		return sbsql.toString();
	}

	/**
	 * <p>
	 * Title: queryBorrowBasinfo
	 * </p>
	 * <p>
	 * Description: 根据债权转让的username和name得到基本信息
	 * </p>
	 * 
	 * @param ids
	 * 
	 * @return 返回查询结果集list
	 */
	@SuppressWarnings("rawtypes")
	public List queryBasinfo(String userId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryBasinfo(String userId=" + userId + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT userbasicsinfo.userName,userbasicsinfo.name,CASE WHEN (SELECT vipinfo.endtime FROM vipinfo WHERE ");
		sqlbuffer
				.append("vipinfo.user_id=userbasicsinfo.id ORDER BY id DESC LIMIT 1) > (SELECT NOW()) THEN '特权会员' ELSE '普通会员' END,userrelationinfo.phone,userbasicsinfo.createTime,CASE ");
		sqlbuffer
				.append("WHEN (SELECT vipinfo.endtime FROM vipinfo WHERE vipinfo.user_id=userbasicsinfo.id ORDER BY id DESC LIMIT 1)");
		sqlbuffer
				.append(" > (SELECT NOW()) THEN (SELECT vipinfo.endtime FROM vipinfo WHERE vipinfo.user_id=userbasicsinfo.id ORDER BY id DESC LIMIT 1) ELSE '永久' END,userrelationinfo.cardId,userrelationinfo.email ,");
		sqlbuffer
				.append("userrelationinfo.industry,userrelationinfo.scale,userrelationinfo.post FROM  userbasicsinfo  LEFT JOIN userrelationinfo ON userbasicsinfo.id=userrelationinfo.user_id ");
		sqlbuffer.append(" where userbasicsinfo.id='" + userId + "' ");

		List returnList = dao.findBySql(sqlbuffer.toString());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryBasinfo(String,String)方法结束OUTPARAM=" + returnList); //$NON-NLS-1$
		}
		return returnList;

	}

	/**
	 * 推广记录 分页查询
	 * 
	 * @param page
	 * @param loansignbasics
	 * @param loanType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List GeneralizePage(PageModel page, Generalize generalize,
			Integer userType) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				" select g.id,u.name,g.adddate,g.uanme ,g.uid from generalize g, userbasicsinfo u where u.id=g.uid  ");
		StringBuffer sbl = new StringBuffer(
				"select count(g.id) from generalize g, userbasicsinfo u where u.id=g.genuid");
		if (userType == 1) {
			sb.append(" and u.user_type=1");
			sbl.append(" and u.user_type=1");
		} else if (userType == 2) { // 员工
			sb.append(" and u.user_type=2");
			sbl.append(" and u.user_type=2");
		}
		list = dao.pageListBySql(page, sbl.toString(), sb.toString(), null);
		return list;
	}

	/**
	 * 推广记录
	 */
	@SuppressWarnings("rawtypes")
	public List GeneralizeList(int id) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"  SELECT g.uid,us.userName,g.adddate,us.`name`,CASE WHEN g.state=1 THEN '初始注册' ELSE '已注册宝付' END from userbasicsinfo u,userbasicsinfo us,generalize g where u.id=g.genuid and g.uid=us.id and g.genuid="
						+ id);
		list = dao.findBySql(sb.toString());
		return list;
	}

	/**
	 * 根据真实姓名和电话以及身份证号查询被推广人是否存在
	 * 
	 * @param realName
	 * @param phone
	 * @param identity
	 * @return flag
	 */
	public Userbasicsinfo checkUserIsExist(String realName, String phone,
			String identity) {
		String sql = "select u.* from userbasicsinfo u join userrelationinfo i "
				+ "on i.user_id=u.id where u.name=? and i.phone=? and i.cardId=? ";
		Userbasicsinfo u = dao.findObjectBySql(sql, Userbasicsinfo.class,
				realName, phone, identity);
		return u;
	}

	public static void main(String[] args) {
		System.out.println(new UserInfoServices().checkUserIsExist("gfsdg",
				"435423543", "543253425"));
	}

	/**
	 * 用户投资次数
	 */
	public List getGuanzhu(String uId) {
		String sql = "select count(id) from loanrecord where userbasicinfo_id="
				+ uId;
		List list = dao.findBySql(sql.toString());
		return list;
	}

	@SuppressWarnings({"rawtypes", "unused"})
	public List queryUserGenlizePage(PageModel page, Userbasicsinfo userinfo) {

		List datalist = new ArrayList();

		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"SELECT count(1) FROM userbasicsinfo ");
		countsql.append(" WHERE ");

		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT  id, userName,`name`,createTime,staff_no,userintegral,user_type,");
		sqlbuffer
				.append("  CASE WHEN isLock = 0 THEN '正常' ELSE '禁用' END,"
						+ "(SELECT COUNT(*) from generalize where genuid =userbasicsinfo.id),"
						+ "(SELECT ifnull(sum(r.tenderMoney),0) from loanrecord r,generalize g WHERE genuid =userbasicsinfo.id and r.userbasicinfo_id=g.uid and r.isSucceed=1) ");
		sqlbuffer.append(" FROM userbasicsinfo WHERE ");
		
		if (null != userinfo) {
			if(userinfo.getUserType()==null){
				countsql.append(" user_type in (2,4)");
				sqlbuffer.append(" user_type in (2,4)");
			}else{
				if(userinfo.getUserType()==2){
					countsql.append(" user_type=2");
					sqlbuffer.append(" user_type=2");
				}else if(userinfo.getUserType()==4){
					countsql.append(" user_type=4");
					sqlbuffer.append("user_type=4");
				}else if(userinfo.getUserType()==0){
					countsql.append(" user_type in (2,4)");
					sqlbuffer.append(" user_type in (2,4)");
				}
			}
			if (StringUtil.isNotBlank(userinfo.getUserName())) {
				sqlbuffer.append(" and userName like '%")
						.append(StringUtil.replaceAll(userinfo.getUserName()))
						.append("%'");
				countsql.append(" and userName like '%")
						.append(StringUtil.replaceAll(userinfo.getUserName()))
						.append("%'");
			}
			// 真实姓名查询
			if (StringUtil.isNotBlank(userinfo.getName())) {
				String name = "";
				try {
					name = java.net.URLDecoder.decode(userinfo.getName(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" AND name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
				countsql.append(" AND name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
			}
			// 注册时间查询
			if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
				sqlbuffer.append(" and createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
				countsql.append(" and createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
			}

			if (StringUtil.isNotBlank(userinfo.getFailTime())) {
				sqlbuffer.append(" and createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
				countsql.append(" and createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
			}
		}

		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);
		return datalist;

	}

	@SuppressWarnings("rawtypes")
	public List queryUserGenlizeList(PageModel page, Userbasicsinfo userinfo) {

		List datalist = new ArrayList();

		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"SELECT count(1) FROM userbasicsinfo ");
		countsql.append(" WHERE user_type=1");

		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT  id, userName,`name`,createTime,staff_no,userintegral,user_type,");
		sqlbuffer
				.append("  CASE WHEN isLock = 0 THEN '正常' ELSE '禁用' END, "
						+ "(SELECT COUNT(*) from generalize where genuid =userbasicsinfo.id),"
						+ "(SELECT ifnull(sum(r.tenderMoney),0) from loanrecord r,generalize g WHERE genuid =userbasicsinfo.id and r.userbasicinfo_id=g.uid and r.isSucceed=1) ");
		sqlbuffer.append(" FROM userbasicsinfo WHERE user_type=1");

		if (null != userinfo) {
			if (StringUtil.isNotBlank(userinfo.getUserName())) {
				sqlbuffer.append(" and userName like '%")
						.append(StringUtil.replaceAll(userinfo.getUserName()))
						.append("%'");
				countsql.append(" and userName like '%")
						.append(StringUtil.replaceAll(userinfo.getUserName()))
						.append("%'");
			}
			// 真实姓名查询
			if (StringUtil.isNotBlank(userinfo.getName())) {
				String name = "";
				try {
					name = java.net.URLDecoder.decode(userinfo.getName(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sqlbuffer.append(" AND name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
				countsql.append(" AND name LIKE '%")
						.append(StringUtil.replaceAll(name)).append("%'");
			}
			// 注册时间查询
			if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
				sqlbuffer.append(" and createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
				countsql.append(" and createTime >= '")
						.append(userinfo.getCreateTime()).append(" 00:00:00'");
			}

			if (StringUtil.isNotBlank(userinfo.getFailTime())) {
				sqlbuffer.append(" and createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
				countsql.append(" and createTime <= '")
						.append(userinfo.getFailTime()).append(" 23:59:59'");
			}
		}
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);
		return datalist;

	}

	@SuppressWarnings("rawtypes")
	public List queryGenlizeListPage(String id, PageModel page) {

		List datalist = new ArrayList();

		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"SELECT count(1)  from userbasicsinfo u,generalize g where u.id=g.genuid and g.genuid="
						+ id);

		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				" SELECT g.uid,us.`name`,g.adddate,us.userName,g.state from userbasicsinfo u,userbasicsinfo us,generalize g where u.id=g.genuid and g.uid=us.id and g.genuid="+ id);
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);
		return datalist;

	}

	@SuppressWarnings("rawtypes")
	public List queryGenlizeRecord(String id, Userbasicsinfo userinfo,
			PageModel page) {

		List datalist = new ArrayList();

		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer(
				"SELECT count(1)  FROM loanrecord INNER JOIN loansign ON loanrecord.loanSign_id = loansign.id"
						+ " INNER JOIN userbasicsinfo ON loanrecord.userbasicinfo_id = userbasicsinfo.id "
						+ "WHERE  loanrecord.isSucceed=1 and userbasicsinfo.id="
						+ id);

		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				"SELECT loanrecord.id, IFNULL(loansign.name,''),IFNULL(userbasicsinfo.name,''), loanrecord.tenderMoney,loanrecord.tenderTime,"
						+ "loanrecord.isSucceed , loanrecord.isPrivilege,loanrecord.subType,loanrecord.fee");
		sqlbuffer
				.append(" FROM loanrecord INNER JOIN loansign ON loanrecord.loanSign_id = loansign.id "
						+ " INNER JOIN userbasicsinfo ON loanrecord.userbasicinfo_id = userbasicsinfo.id WHERE  loanrecord.isSucceed=1 and userbasicsinfo.id="
						+ id);
		// 投资时间查询
		if (StringUtil.isNotBlank(userinfo.getCreateTime())) {
			sqlbuffer.append(" and loanrecord.tenderTime >= '")
					.append(userinfo.getCreateTime()).append(" 00:00:00'");
			countsql.append(" and loanrecord.tenderTime >= '")
					.append(userinfo.getCreateTime()).append(" 00:00:00'");
		}

		if (StringUtil.isNotBlank(userinfo.getFailTime())) {
			sqlbuffer.append(" and loanrecord.tenderTime <= '")
					.append(userinfo.getFailTime()).append(" 23:59:59'");
			countsql.append(" and loanrecord.tenderTime <= '")
					.append(userinfo.getFailTime()).append(" 23:59:59'");
		}
		sqlbuffer.append(" order by loanrecord.id desc  ");
		datalist = dao.pageListBySql(page, countsql.toString(),
				sqlbuffer.toString(), null);
		return datalist;

	}

	@SuppressWarnings("rawtypes")
	public List queryLoanrecordList(int start, int limit, int userId, int num) {

		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT loanrecord.id, IFNULL(loansign.name,''),IFNULL(userbasicsinfo.name,''), loanrecord.tenderMoney, loanrecord.tenderTime, ");
		sb.append("CASE WHEN loanrecord.isSucceed=1 THEN '成功' WHEN loanrecord.isSucceed=0 THEN '失败' END , CASE WHEN loanrecord.isPrivilege=1 THEN '是' ELSE '否' END,"
				+ "CASE WHEN loanrecord.subType=1 THEN '优先' WHEN loanrecord.subType=2 THEN '夹层' WHEN loanrecord.subType=3 THEN '劣后' END ,loanrecord.fee ");
		sb.append("  FROM loanrecord INNER JOIN loansign ON loanrecord.loanSign_id = loansign.id  ");
		sb.append(
				" INNER JOIN userbasicsinfo ON loanrecord.userbasicinfo_id = userbasicsinfo.id WHERE  userbasicsinfo.id =")
				.append(userId);
		sb.append("   and loanrecord.isSucceed = 1");
		sb.append(" order by loanrecord.id desc  ");
		if (num == 1) {
			sb.append(" LIMIT ").append(start).append(" , ").append(limit);
		}
		list = dao.findBySql(sb.toString());
		return list;
	}

	@SuppressWarnings("rawtypes")
	public List querySeeUserDetailes(String id) {
		StringBuffer sb = new StringBuffer(
				"SELECT userbasicsinfo.id, userbasicsinfo.userName, userbasicsinfo.`name`,userrelationinfo.phone,"
						+ "(select sum(realityintegral) from autointegral where user_id=userbasicsinfo.id), userbasicsinfo.createTime,"
						+ "( SELECT max(userloginlog.logintime)  FROM userloginlog WHERE userloginlog.user_id = userbasicsinfo.id),"
						+ "( SELECT userloginlog.address FROM userloginlog WHERE userloginlog.user_id = userbasicsinfo.id ORDER BY userloginlog.id DESC LIMIT 0, 1 ),"
						+ "( SELECT count(1) FROM userloginlog WHERE userloginlog.user_id = userbasicsinfo.id ),"
						+ "(SELECT  max(endtime) FROM vipinfo WHERE vipinfo.endtime > NOW() AND vipinfo.user_id = userbasicsinfo.id)  AS vipendtime,"
						+ "( SELECT count(1) FROM borrowersbase WHERE auditResult = 1 AND userbasicinfo_id = userbasicsinfo.id ),"
						+ "CASE WHEN userbasicsinfo.isLock = 0 THEN '正常' ELSE '禁用' END, (SELECT realname FROM adminuser WHERE id = userbasicsinfo.adminuser_id ),"
						+ "userbasicsinfo.hasIpsAccount,userbasicsinfo.isAuthIps,userbasicsinfo.orderSn,userbasicsinfo.isorgperson, "
						+ "userbasicsinfo.iscrowdfundingperson,userbasicsinfo.iscrowdhold,userbasicsinfo.pMerBillNo, userbasicsinfo.user_type,userbasicsinfo.staff_no,"
						+ "(SELECT u.name from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid),"
						+ "(SELECT u.userName from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid),"
						+ "(select sum((select SUM(ifnull(rmp.realMoney,0)-IFNULL(rmp.money,0))+SUM(IFNULL(rmp.middleRealMoney,0)-IFNULL(rmp.middleMoney,0))+SUM(IFNULL(rmp.afterRealMoney,0)-IFNULL(rmp.afterMoney,0))"
						+ " from repaymentrecord repm,repaymentrecordparticulars rmp where repm.id=rmp.repaymentrecordId and rmp.loanrecordId=lr.id and rmp.repState=1)) from loanrecord lr ,loansign ls where ls.id=lr.loanSign_id "
						+ " and lr.userbasicinfo_id=userbasicsinfo.id and lr.isSucceed=1 and ls.`status`in(6,7,8)),"
						+ " (SELECT sum(tenderMoney) from loanrecord where userbasicinfo_id=userfundinfo.id and isSucceed=1),"
						+ "(SELECT u.department from generalize g,userbasicsinfo u where g.uid=userbasicsinfo.id and u.id=genuid),userfundinfo.cashBalance "
						+ " FROM userbasicsinfo LEFT JOIN userfundinfo ON userbasicsinfo.id = userfundinfo.id  LEFT JOIN userrelationinfo ON "
						+ " userrelationinfo.user_id = userbasicsinfo.id WHERE userbasicsinfo.userName is not null and userbasicsinfo.id="
						+ id);
		List list = dao.findBySql(sb.toString());
		return list;
	}
}
