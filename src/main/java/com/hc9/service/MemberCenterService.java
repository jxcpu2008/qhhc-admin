package com.hc9.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.redis.IndexDataCache;
import com.hc9.common.redis.RedisHelper;
import com.hc9.dao.entity.Shop;
import com.hc9.dao.entity.ShopInterview;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Userfundinfo;
import com.hc9.dao.entity.Usermessage;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * 会员中心首页
 * 
 * @author ransheng
 * 
 */
@Service
@SuppressWarnings("rawtypes")
public class MemberCenterService {

	/**
	 * 通用dao
	 */
	@Resource
	private HibernateSupport commonDao;

	public List<Shop> queryProjectbase(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		String sql = "SELECT * FROM shop where user_id=?";
		List<Shop> probases = commonDao
				.findBySql(sql, Shop.class, user.getId());
		return probases.size() > 0 ? probases : null;
	}

	/**
	 * 根据id查询会员基本信息
	 * 
	 * @param id
	 *            会员编号
	 * @return 会员基本信息
	 */
	public Userbasicsinfo queryById(Long id) {
		Userbasicsinfo user = commonDao.get(Userbasicsinfo.class, id);
		return user;
	}

	/**
	 * 查询用户是否设置安全问题
	 * 
	 * @param id
	 *            用户编号
	 * @return 是否设置安全问题
	 */
	public boolean isSecurityproblem(Long id) {
		// 查询用户设置安全问题的个数
		String sql = "SELECT COUNT(*) FROM securityproblem b WHERE b.user_id="
				+ id;
		Object obj = commonDao.findObjectBySql(sql);
		// 如果条数小于1，代表用户还未设置安全问题。
		if (Integer.parseInt(obj + "") < 1) {
			return false;
		}
		return true;
	}

	/**
	 * 根据用户查询系统消息条数
	 * 
	 * @param id
	 *            用户编号
	 * @return 消息条数
	 */
	public Object queryUserMessageCount(Long id) {
		StringBuffer sql = new StringBuffer(
				"SELECT count(*) FROM usermessage a where a.user_id=")
				.append(id);
		return commonDao.findObjectBySql(sql.toString());
	}

	/**
	 * 私信、约谈成功条数
	 * 
	 * @param title
	 * @return
	 */
	public Object queryUserMessageCount(String title, Long id) {
		StringBuffer sql = new StringBuffer(
				"SELECT count(*) FROM usermessage a where a.title=")
				.append("'" + title + "'").append(" and a.user_id=").append(id);
		return commonDao.findObjectBySql(sql.toString());
	}

	/**
	 * 私信、约谈
	 * 
	 * @param title
	 *            消息类型
	 * @param page
	 *            分页
	 * @return
	 */
	public List queryMessage(String title, Long id, PageModel page) {
		StringBuffer sql = new StringBuffer(
				"SELECT a.id,a.context,a.isread,a.title,a.receivetime,(select userName from userbasicsinfo where id=a.send_id) FROM usermessage a where a.title not in (")
				.append("'" + title + "')").append("and a.user_id=").append(id)
				.append(" order by a.receivetime desc LIMIT ")
				.append(page.firstResult()).append(",")
				.append(page.getNumPerPage());
		return commonDao.findBySql(sql.toString());
	}

	public List queryInterviewtol(Long id, PageModel page) {
		StringBuffer sql = new StringBuffer(
				"SELECT i.id,i.context,i.isread,(select userName from userbasicsinfo where id=i.to_user),"
						+ "(select userName from userbasicsinfo where id=i.from_user), "
						+ "i.createTime,(select shop_name from shop s where id=i.shop_id) "
						+ " FROM shop_interview i, userbasicsinfo u where i.success=1 and u.id=i.to_user ")
				.append("and (i.to_user=" + id)
				.append(" or i.from_user=" + id + ")").append(" LIMIT ")
				.append(page.firstResult()).append(",")
				.append(page.getNumPerPage());
		return commonDao.findBySql(sql.toString());
	}

	/**
	 * 会员关注,约谈,投资统计
	 * 
	 * @param pId
	 * @return
	 */
	public Object getUserbasicinfoCount(String uId) {
		String sql = "select (select sum(realityintegral) from autointegral where user_id=u.id), " // 积分记录
		// +
		// "	(select count(id) from shop_interview where to_user=u.id and success=1 ), "
		// // 约谈记录
				+ "	(select count(id) from loanrecord where userbasicinfo_id=u.id and isSucceed=1 )  " // 投资记录
				+ "	from userbasicsinfo u where u.id=" + uId;
		Object obj = commonDao.findObjectBySql(sql.toString());
		return obj;
	}

	/**
	 * 查询私信、约谈已读或者未读消息
	 * 
	 * @param id
	 *            用户编号
	 * @param isRead
	 *            是否读取
	 * @return 消息条数
	 */
	public Object queryIsReadCount(Long id, Integer isRead) {
		StringBuffer sql = new StringBuffer(
				"SELECT count(u.id) FROM usermessage u where (u.user_id=")
				.append(id + ")").append(" AND u.isread=").append(isRead);
		return commonDao.findObjectBySql(sql.toString());
	}

	/**
	 * 系统信息条数
	 * 
	 * @param title
	 * @return
	 */
	public Object queryUserSystemMessageCount(String title, Long id) {
		StringBuffer sql = new StringBuffer(
				"SELECT count(*) FROM usermessage a where a.title not in(")
				.append("'" + title + "')").append(" and a.user_id=")
				.append(id);
		return commonDao.findObjectBySql(sql.toString());
	}

	public Object querytolInterviewCount(Long id) {
		StringBuffer sql = new StringBuffer(
				"SELECT count(*) FROM shop_interview i where ")
				.append(" (i.to_user=" + id).append(" or i.from_user=")
				.append(id + ")");
		return commonDao.findObjectBySql(sql.toString());
	}

	/**
	 * 系统信息
	 * 
	 * @param title
	 *            消息类型
	 * @param page
	 *            分页
	 * @return
	 */
	public List querySystemMessage(String title, String title1, Long id,
			PageModel page) {
		StringBuffer sql = new StringBuffer(
				"SELECT * FROM usermessage a where a.title not in(")
				.append("'" + title + "','" + title1 + "')")
				.append("and a.user_id=").append(id).append(" LIMIT ")
				.append(page.firstResult()).append(",")
				.append(page.getNumPerPage());
		return commonDao.findBySql(sql.toString());
	}

	/**
	 * 查询系统已读或者未读消息
	 * 
	 * @param id
	 *            用户编号
	 * @param isRead
	 *            是否读取
	 * @return 消息条数
	 */
	public Object querySystemIsReadCount(String title, String title1, Long id,
			Integer isRead) {
		StringBuffer sql = new StringBuffer(
				"SELECT count(*) FROM usermessage a where a.user_id=")
				.append(id).append(" AND a.title not in(")
				.append("'" + title + "','" + title1 + "')")
				.append(" AND a.isread=").append(isRead);
		return commonDao.findObjectBySql(sql.toString());
	}

	/**
	 * 查询约谈是否读取
	 * 
	 * @param id
	 * @param isRead
	 * @return
	 */
	public Object queryIsReadCount(String title, Long id, Integer isRead) {
		StringBuffer sql = new StringBuffer(
				"SELECT count(*) FROM usermessage a where a.user_id=")
				.append(id).append(" AND a.title=").append("'" + title + "'")
				.append(" AND a.isread=").append(isRead);
		return commonDao.findObjectBySql(sql.toString());
	}

	/**
	 * 更用用户编号查询用户系统消息
	 * 
	 * @param id
	 *            用户编号
	 * @param page
	 *            分页对象
	 * @return 系统消息结果集
	 */
	public List queryUserMessage(Long id, PageModel page) {
		StringBuffer sql = new StringBuffer(
				"SELECT id,context,isread,receivetime,title FROM usermessage a where a.user_id=")
				.append(id).append(" LIMIT ").append(page.firstResult())
				.append(",").append(page.getNumPerPage());
		return commonDao.findBySql(sql.toString());
	}

	/**
	 * 根据编号查询系统消息
	 * 
	 * @param id
	 *            消息编号
	 * @param unRead
	 *            是否已读
	 * @return 系统消息
	 */
	public Usermessage queryById(Long id, Integer unRead) {
		// 查询单条系统消息
		Usermessage message = commonDao.get(Usermessage.class, id);
		// 如果该条信息未读，则修改为已读
		if (unRead == 0) {
			message.setIsread(1);
			commonDao.update(message);
		}
		return message;
	}

	/**
	 * 根据编号查询约谈信息
	 * 
	 * @param id
	 *            消息编号
	 * @param unRead
	 *            是否已读
	 * @return 系统消息
	 */
	public ShopInterview setIsRead(Integer id, Integer unRead) {
		// 查询单条系统消息
		ShopInterview inter = commonDao.get(ShopInterview.class, id);
		// 如果该条信息未读，则修改为已读
		if (unRead == 0) {
			inter.setIsread(1);
			commonDao.update(inter);
		}
		return inter;
	}

	/**
	 * 查询用户登录日志
	 * 
	 * @param id
	 *            用户编号
	 * @return 登录日志
	 */
	public List queryLog(Long id) {
		List list = null;
		String sql = "SELECT id,logintime,ip,address FROM userloginlog "
				+ "WHERE user_id=? ORDER BY id DESC LIMIT 10";
		list = commonDao.findBySql(sql, id);
		return list;
	}

	/**
	 * 最后一次登陆地点
	 * 
	 * @param id
	 * @return
	 */
	public List queryuserloginLog(Long id) {
		String sql = "SELECT id,logintime,ip,address FROM userloginlog "
				+ "WHERE user_id=? ORDER BY id DESC LIMIT 1";
		List list = commonDao.findBySql(sql, id);
		return list;
	}

	/**
	 * 根据编号删除约谈消息
	 * 
	 * @param ids
	 *            系统消息编号，以逗号分开
	 * @return 删除成功true、失败false
	 */
	public boolean deleteInters(String ids) {
		try {
			String[] id = ids.split(",");
			// 删除消息
			for (int i = 0; i < id.length; i++) {
				commonDao.delete(Integer.valueOf(id[i]), ShopInterview.class);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			e.getMessage();
			return false;
		}
		return true;
	}

	/**
	 * 根据编号删除系统消息
	 * 
	 * @param ids
	 *            系统消息编号，以逗号分开
	 * @return 删除成功true、失败false
	 */
	public boolean deleteInboxs(String ids) {
		try {
			String[] id = ids.split(",");
			// 删除消息
			for (int i = 0; i < id.length; i++) {
				commonDao.delete(Long.valueOf(id[i]), Usermessage.class);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			e.getMessage();
			return false;
		}
		return true;
	}

	/**
	 * 累计利息成本
	 * 
	 * @param id
	 *            会员编号
	 * @return 累计利息成本
	 */
	public Object interest(Long id) {
		String sql = "SELECT id FROM loansign a  WHERE a.userbasicinfo_id="
				+ id;
		List list = commonDao.findBySql(sql);
		BigDecimal sum = new BigDecimal(0);
		for (int i = 0; i < list.size(); i++) {
			Object loanSignId = list.get(i);
			sql = "SELECT repayState,realMoney,preRepayMoney FROM "
					+ "repaymentrecord  WHERE loanSign_id=" + loanSignId;
			List repay = commonDao.findBySql(sql);
			for (int j = 0; j < repay.size(); j++) {
				Object[] obj = (Object[]) repay.get(j);
				if (Integer.parseInt(obj[0].toString()) == 1
						|| Integer.parseInt(obj[0].toString()) == 3) {
					sum = sum.add((BigDecimal) obj[2]);
				} else {
					sum = sum.add((BigDecimal) obj[1]);
				}
			}

		}

		return sum;
	}

	/**
	 * 累计支付
	 * 
	 * @param id
	 * @return
	 */
	public Object payment(Long id) {
		String sql = "SELECT IFNULL(SUM(b.money)+SUM(b.preRepayMoney),0) FROM "
				+ "loansign a INNER JOIN repaymentrecord b ON a.id=b.loanSign_id "
				+ "WHERE b.repayState=3 AND a.userbasicinfo_id=?";
		Object obj = commonDao.findObjectBySql(sql, id);
		return obj;
	}

	/**
	 * 待确认提现
	 * 
	 * @param id
	 * @return
	 */
	public Object noTransfer(Long id) {
		String sql = "SELECT IFNULL(SUM(withdrawAmount),0)+IFNULL(SUM(deposit),0) FROM Withdraw "
				+ "WHERE user_id=? AND withdrawstate=0";
		Object obj = commonDao.findObjectBySql(sql, id);
		return obj;
	}

	/**
	 * 待确认充值
	 * 
	 * @param id
	 * @return
	 */
	public Object rechargeTobe(Long id) {
		String sql = "SELECT IFNULL(SUM(r.rechargeAmount),0)"
				+ " FROM Recharge r" + " WHERE r.status=0 AND r.user_id=?";
		Object obj = commonDao.findObjectBySql(sql, id);
		return obj;
	}

	/**
	 * 投资记录
	 * 
	 * @param id
	 * @param loanstate
	 * @return
	 */
	public Object investmentRecords(Long id, Integer loanstate) {
		String sql = "SELECT IFNULL(SUM(b.tenderMoney),0) FROM "
				+ "Loansign a INNER JOIN loanrecord b ON a.id=b.loanSign_id "
				+ "WHERE a.state=? AND b.userbasicinfo_id=?";
		Object obj = commonDao.findObjectBySql(sql, loanstate, id);
		return obj;
	}

	/**
	 * 净赚利息
	 * 
	 * @param id
	 *            用户编号
	 * @return 净赚利息
	 */
	public Object netInterest(Long id) {
		String sql = "SELECT rpp.preRepayMoney,rpp.middlePreRepayMoney,rpp.afterPreRepayMoney "
				+ "from "
				+ "repaymentrecord rp JOIN repaymentrecordparticulars rpp on rp.id=rpp.repaymentrecordId "
				+ "where rp.repayState!=1 AND rp.repayState!=3 AND rpp.userId=?";
		List list = commonDao.findBySql(sql, id);

		double interest = 0.0;

		for (int i = 0; i < list.size(); i++) {
			Object[] obj = (Object[]) list.get(i);
			for (int j = 0; j < 3; j++) {
				if (obj[j] != null) {
					interest += Double.parseDouble(obj[j].toString());
				}
			}

		}
		return interest;
	}

	/**
	 * 借款记录
	 * 
	 * @param id
	 * @param loanstate
	 * @return
	 */
	public Object borrowing(Long id, Integer loanstate) {
		String sql = "SELECT IFNULL(SUM(a.issueLoan),0) FROM Loansign a "
				+ "WHERE a.userbasicinfo_id=? AND a.state=?";
		Object obj = commonDao.findObjectBySql(sql, id, loanstate);
		return obj;
	}

	/**
	 * 已用额度
	 * 
	 * @param id
	 *            会员编号
	 * @return 已用额度
	 */
	public Object usedAmount(Long id) {
		String sql = "SELECT IFNULL(SUM(a.issueLoan), 0) FROM "
				+ "Loansign a WHERE a.userbasicinfo_id=? "
				+ "AND a.state!=4 AND a.state!=1";
		Object obj = commonDao.findObjectBySql(sql, id);
		return obj;
	}

	/**
	 * 累计逾期
	 * 
	 * @param id
	 * @return
	 */
	public Object overRepayment(Long id) {
		String sql = "SELECT IFNULL(SUM(r.preRepayMoney),0)"
				+ " FROM repaymentrecord r,loansign b WHERE r.loanSign_id=b.id"
				+ " AND b.userbasicinfo_id=? AND r.repayState=4";
		Object obj = commonDao.findObjectBySql(sql, id);
		return obj;
	}

	/**
	 * 已得收益
	 * 
	 * @param id
	 * @return
	 */
	public Object realMoney(Long id) {
		String sql = "SELECT IFNULL(SUM(r.realMoney),0)"
				+ " FROM repaymentrecord r,loansign b WHERE r.loanSign_id=b.id"
				+ " AND b.userbasicinfo_id=? AND r.repayState IN(2,4,5)";
		Object obj = commonDao.findObjectBySql(sql, id);
		return obj;
	}

	/**
	 * 预计账户总收益
	 * 
	 * @param id
	 * @return
	 */
	public Object preRepayMoney(Long id) {
		String sql = "SELECT IFNULL(SUM(r.preRepayMoney),0)"
				+ " FROM repaymentrecord r,loansign b WHERE r.loanSign_id=b.id"
				+ " AND b.userbasicinfo_id=?";
		Object obj = commonDao.findObjectBySql(sql, id);
		return obj;
	}

	/**
	 * 用户资产总额
	 * 
	 * @param user
	 * @return
	 */
	public Double getTotalAssets(Userbasicsinfo user) {
		// 账户现金余额+投资店铺(未完成)+投资项目(未完成)
		String shopInvest = (totalShopInvest(user.getId()) == null ? "0.0"
				: totalShopInvest(user.getId())).toString();
		String loanInvest = (totalLoansignInvest(user.getId()) == null ? "0.0"
				: totalLoansignInvest(user.getId())).toString();
		double totalAssets = user.getUserfundinfo().getCashBalance()
				+ Double.parseDouble(shopInvest)
				+ Double.parseDouble(loanInvest);
		return totalAssets;
	}

	/**
	 * 用户投资店铺，类型非抽奖且该店铺状态进行中和已完成。店铺放款后，该用户拥有股权，属于资产
	 * 
	 * @param uid
	 * @return
	 */
	public Object totalShopInvest(Long uid) {
		String sql = "SELECT SUM(sr.tenderMoney) from shop_record sr JOIN shop s ON sr.shop_id=s.id WHERE (s.state>=4 AND s.state<8) AND sr.isSucceed=1 AND s.type=1 AND sr.user_id=?";
		Object obj = commonDao.findObjectBySql(sql, uid);
		return obj;
	}

	/**
	 * 用户项目投资成功，且该项目还未完成，未流标的投资总额
	 * 
	 * @param uid
	 * @return
	 */
	public Object totalLoansignInvest(Long uid) {
		String sql = "SELECT SUM(lr.tenderMoney) from loanrecord lr JOIN loansign l ON lr.loanSign_id=l.id WHERE (l.`status`>=1 AND l.`status`<8) AND lr.isSucceed=1 AND lr.userbasicinfo_id=?";
		Object obj = commonDao.findObjectBySql(sql, uid);
		return obj;
	}

	/**
	 * 待收本金
	 * 
	 * @param id
	 * @return
	 */
	public Double toMoney(Long userId) {
		String sql = "SELECT sum(tenderMoney) from loansign ls LEFT JOIN loanrecord lr ON ls.id=lr.loanSign_id "
				+ "where lr.isSucceed=1 and lr.userbasicinfo_id="
				+ userId
				+ " and ls.`status` not in(0,-1,8,9)";
		Object obj = commonDao.findObjectBySql(sql);
		return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	}
	
	/**
	 * 待收本金需要排除已回款的投资信息
	 * 
	 * @param id
	 * @return
	 */
	public Double backMoney(Long userId) {
		String sql = "select sum(IFNULL(money,0)) from repaymentrecordparticulars r,loanrecord lr,loansign ls where ls.id = lr.loanSign_id and lr.id=r.loanrecordId and lr.isSucceed=1 and lr.userbasicinfo_id=? and r.repState = 1 and ls.`status` in (6,7)";
		Object obj = commonDao.findObjectBySql(sql,userId);
		return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	}

	/**
	 * 待收佣金
	 * 
	 * @param id
	 * @return
	 */
	public Double toBonus(Long userId) {
		String sql = "select sum(IFNULL(bonuses,0)) from generalizemoney  where pay_state=0  and refer_userid= "
				+ userId;
		Object obj = commonDao.findObjectBySql(sql);
		return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	}

	/**
	 * 历史收益
	 * 
	 * @param id
	 * @return
	 */
	public Double hostIncome(Long userId) {
		String sql = "select sum((select SUM(ifnull(rmp.realMoney,0)-IFNULL(rmp.money,0))+SUM(IFNULL(rmp.middleRealMoney,0)-IFNULL(rmp.middleMoney,0))+SUM(IFNULL(rmp.afterRealMoney,0)-IFNULL(rmp.afterMoney,0))"
				+ "+(select IFNULL(sum(incomeMoney),0) from voteincome where loanRecordId=lr.id and status = 1)"
				+ " from repaymentrecord repm,repaymentrecordparticulars rmp where repm.id=rmp.repaymentrecordId and rmp.loanrecordId=lr.id and rmp.repState=1)) "
				+ "from loanrecord lr ,loansign ls where ls.id=lr.loanSign_id  and lr.userbasicinfo_id="
				+ userId + " and lr.isSucceed=1 and ls.`status`in(6,7,8)";
		Object obj = commonDao.findObjectBySql(sql);
		return obj != null ? Double.valueOf(obj.toString()) : 0.00;
	}

	// 回款条数
	public Integer backCount(Long userId) {
		String sql = "select count(*) from loanrecord r join loansign l where r.loanSign_id =l.id and r.isSucceed=1  and l.`status`  not in(0,-1,8,9) and r.userbasicinfo_id="
				+ userId;
		Object obj = commonDao.findObjectBySql(sql);
		return obj != null ? Integer.valueOf(obj.toString()) : 0;
	}

	// 投资收益条数
	public Integer loanCount(Long userId) {
		String sql = "select count(ls.id) from loanrecord lr ,loansign ls where ls.id=lr.loanSign_id  and lr.userbasicinfo_id="
				+ userId + " and lr.isSucceed=1 and ls.`status`=8";
		Object obj = commonDao.findObjectBySql(sql);
		return obj != null ? Integer.valueOf(obj.toString()) : 0;
	}

	/** 获取用户投资概况相关投资信息 */
	public List queryInvestStatisticInfo(Long userId) {
		// 投资概况 本月(1)，下一个月(2),本年(3)
		String key = "LST:INVEST:STATISTIC:INFO:USER:" + userId;
		List arrylist = IndexDataCache.getList(key);
		if(arrylist == null || arrylist.size() < 1) {
			arrylist = updateInvestStatisticInfo(userId);
		}
		
		return arrylist;
	}
	
	/** 更新用户投资概况缓存 */
	public List updateInvestStatisticInfo(Long userId) {
		String key = "LST:INVEST:STATISTIC:INFO:USER:" + userId;
		List arrylist = new ArrayList();
		for (int i = 1; i < 5; i++) {
			Object[] obj = loanHistory(userId, i);
			arrylist.add(obj);
		}
		IndexDataCache.set(key, arrylist);
		return arrylist;
	}
	
	// 投资概况
	public Object[] loanHistory(Long userId, Integer type) {
		StringBuffer sql = new StringBuffer(
				"SELECT COUNT(lr.id),sum(lr.tenderMoney) from loanrecord lr where  lr.isSucceed=1 ");
		if (type == 1) {
			sql.append(" and DATE_FORMAT( lr.tenderTime, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' ) ");

		} else if (type == 2) {
			sql.append(" and PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( lr.tenderTime, '%Y%m' ))=1 ");

		} else if (type == 3) {
			sql.append(" and YEAR(lr.tenderTime)=YEAR(NOW()) ");

		}
		sql.append("  and lr.userbasicinfo_id=" + userId);
		Object[] obj = (Object[]) commonDao.findObjectBySql(sql.toString());
		return obj;
	}

	public Object[] backHistory(Long userId, Integer type) {
		StringBuffer sql = new StringBuffer(
				"SELECT count(lr.id),sum(lr.tenderMoney) from loanrecord lr LEFT JOIN loansign ls ON lr.loanSign_id=ls.id "
						+ "WHERE   lr.isSucceed=1  ");
		if (type == 1) {
			sql.append(" and ls.`status`=1 ");
		} else if (type == 2) {
			sql.append(" and ls.`status`in(6,7) ");
		} else {
			sql.append(" and ls.`status`in(1,6,7) ");
		}
		sql.append("  and lr.userbasicinfo_id=" + userId);
		Object[] obj = (Object[]) commonDao.findObjectBySql(sql.toString());
		return obj;
	}
	
	
	/** 获取用户回款相关投资信息 */
	public List queryBackMoneyStatisticInfo(Long userId) {
		// 回款中和投资中
		String key = "LST:BACK:MONEY:STATISTIC:INFO:USER:" + userId;
		List arrylist = IndexDataCache.getList(key);
		if(arrylist == null || arrylist.size() < 1) {
			arrylist = updateBackMoneyStatisticInfo(userId);
		}
		
		return arrylist;
	}
	
	/** 更新用户回款相关缓存 */
	public List updateBackMoneyStatisticInfo(Long userId) {
		String key = "LST:BACK:MONEY:STATISTIC:INFO:USER:" + userId;
		List arrlist = new ArrayList();
		Double backMoney = backMoney(userId);
		for (int i = 1; i < 4; i++) {
			Object[] obj = backHistory(userId, i);
			if (i == 2 || i == 3) {
				if (obj[1] != null) {
					obj[1] = Double.valueOf(obj[1].toString()) - backMoney;
				}
			}
			arrlist.add(obj);
		}
		IndexDataCache.set(key, arrlist);
		return arrlist;
	}
	
	/** 获取用户还款和回款信息列表 */
	public List queryRepaymentBackList(Long userId) {
		List arrylist = new ArrayList();
//		String key = "LST:HC9:REPAYMENT:BACK:LIST:USER:" + userId;
//		String valueFlag = "LST:HC9:REPAYMENT:BACK:VALUE:FLAG:USER:" + userId;
//		String flag = RedisHelper.get(valueFlag);
//		if("1".equals(flag) ) {
//			arrylist = IndexDataCache.getList(key);
//		} else if(!"0".equals(flag)) {
			arrylist = repaymentBackList(userId);
//		}
		return arrylist;
	}
	
	/**
	 * 查询用户的还款和回款信息
	 * @param userId
	 * @return 还款和回款合并后的list
	 */
	public List repaymentBackList(Long userId) {
		StringBuffer sql = new StringBuffer("select ");
		sql.append("t.time,t.type,t.name,t.money from (");
		sql.append("select rr.preRepayDate as time,1 as type,ls.name as `name`,(ifnull(rr.money,0)+IFNULL(rr.preRepayMoney,0)+IFNULL(rr.middleMoney,0)+ifnull(rr.middlePreRepayMoney,0)+IFNULL(rr.afterMoney,0)+IFNULL(rr.afterPreRepayMoney,0)+IF(ls.loansignType_id=5,IFNULL(rr.companyPreFee,0),0)) as money ");
		sql.append(" from loansign ls left join repaymentrecord rr on rr.loanSign_id=ls.id");
		sql.append(" where ls.userbasicinfo_id = "+userId+" and ls.status in (6,7) and rr.repayState in (1,3)");
		sql.append(" union all ");
		sql.append("select rr.preRepayDate as time,2 as type,ls.name as `name`,(ifnull(rp.money,0)+IFNULL(rp.middleMoney,0)+IFNULL(rp.afterMoney,0)+ifnull(rp.preRepayMoney,0)+IFNULL(rp.middlePreRepayMoney,0)+IFNULL(rp.afterPreRepayMoney,0)) as money ");
		sql.append("from loanrecord lr left join repaymentrecordparticulars rp on lr.id=rp.loanrecordId ");
		sql.append("left join repaymentrecord rr on rr.id=rp.repaymentrecordId join loansign ls on ls.id=lr.loanSign_id ");
		sql.append("where isSucceed = 1 and lr.userbasicinfo_id = "+userId+" and ls.status in (6,7) and rr.repayState in (1,3) ");
		sql.append("and (ifnull(rp.money,0)+IFNULL(rp.middleMoney,0)+IFNULL(rp.afterMoney,0)+ifnull(rp.preRepayMoney,0)+IFNULL(rp.middlePreRepayMoney,0)+IFNULL(rp.afterPreRepayMoney,0)) > 0) t ");
		sql.append("order by t.time limit 0,10");
		List resultList = commonDao.findBySql(sql.toString());
//		String key = "LST:HC9:REPAYMENT:BACK:LIST:USER:" + userId;
//		String valueFlag = "LST:HC9:REPAYMENT:BACK:VALUE:FLAG:USER:" + userId;
//		if(resultList == null || resultList.size() < 1) {
//			RedisHelper.set(valueFlag, "0");
//		} else {
//			RedisHelper.set(valueFlag, "1");
//			IndexDataCache.set(key, resultList);
//		}
		return resultList;
	}
	
	public List repaymentBackListByType(Long userId, int type, int all) {
		if(type == 1){
			StringBuffer sql = new StringBuffer("select ");
			sql.append("rr.preRepayDate,2,ls.name,(ifnull(rp.money,0)+IFNULL(rp.middleMoney,0)+IFNULL(rp.afterMoney,0)+ifnull(rp.preRepayMoney,0)+IFNULL(rp.middlePreRepayMoney,0)+IFNULL(rp.afterPreRepayMoney,0)+(IFNULL((select sum(IFNULL(incomeMoney,0)) from voteincome where voterId=rp.userId and loanrecordId=lr.id and rr.periods=(select periods from repaymentrecord where loanSign_id=ls.id order by periods desc limit 0,1) and status= 0),0))) ");
			sql.append("from loanrecord lr left join repaymentrecordparticulars rp on lr.id=rp.loanrecordId ");
			sql.append("left join repaymentrecord rr on rr.id=rp.repaymentrecordId join loansign ls on ls.id=lr.loanSign_id ");
			sql.append("where isSucceed = 1 and lr.userbasicinfo_id = "+userId+" and ls.status in (6,7) and rr.repayState in (1,3) ");
			sql.append("and (ifnull(rp.money,0)+IFNULL(rp.middleMoney,0)+IFNULL(rp.afterMoney,0)+ifnull(rp.preRepayMoney,0)+IFNULL(rp.middlePreRepayMoney,0)+IFNULL(rp.afterPreRepayMoney,0)) > 0 ");
			sql.append("order by rr.preRepayDate ");
			if(all == 0){
				sql.append("limit 0,11");
			}
			return commonDao.findBySql(sql.toString());
		}else{
			StringBuffer sql = new StringBuffer("select ");
			sql.append(" rr.preRepayDate,1,ls.name,(ifnull(rr.money,0)+IFNULL(rr.preRepayMoney,0)+IFNULL(rr.middleMoney,0)+ifnull(rr.middlePreRepayMoney,0)+IFNULL(rr.afterMoney,0)+IFNULL(rr.afterPreRepayMoney,0)+IF(ls.loansignType_id=5,IFNULL(rr.companyPreFee,0),0))");
			sql.append(" from loansign ls left join repaymentrecord rr on rr.loanSign_id=ls.id");
			sql.append(" where ls.userbasicinfo_id = "+userId+" and ls.status in (6,7) and rr.repayState in (1,3)");
			sql.append(" order by rr.preRepayDate ");
			if(all == 0){
				sql.append("limit 0,11");
			}
			return commonDao.findBySql(sql.toString());
		}
	}

	public Map<String, List> queryDate(String date, Long userId) {
		String sql = "SELECT rp.preRepayDate from repaymentrecordparticulars rmp LEFT JOIN loanrecord lr ON rmp.loanrecordId=lr.id "
				+ "LEFT JOIN repaymentrecord rp ON rmp.repaymentrecordId=rp.id "
				+ "where lr.userbasicinfo_id="
				+ userId
				+ " and rmp.repState in(0,-1) and DATE_FORMAT( rp.preRepayDate, '%Y%m' ) = DATE_FORMAT( '"
				+ date + "', '%Y%m' ) and rp.preRepayDate >'" + date + "' and (ifnull(rmp.money,0)+IFNULL(rmp.middleMoney,0)+IFNULL(rmp.afterMoney,0)+ifnull(rmp.preRepayMoney,0)+IFNULL(rmp.middlePreRepayMoney,0)+IFNULL(rmp.afterPreRepayMoney,0)) > 0";

		String sqll = "SELECT rp.preRepayDate from repaymentrecord rp LEFT JOIN loansign ls ON rp.loanSign_id =ls.id "
				+ "where rp.repayState in (1,3) and ls.userbasicinfo_id="
				+ userId
				+ " and DATE_FORMAT( rp.preRepayDate, '%Y%m' ) = DATE_FORMAT( '"
				+ date + "', '%Y%m' ) and rp.preRepayDate >'" + date + "'";
		List list = commonDao.findBySql(sql.toString());
		List list1 = commonDao.findBySql(sqll.toString());
		Map<String, List> maplist = new HashMap<String, List>();
		maplist.put("loan", list1);
		maplist.put("borrow", list);

		return maplist;
	}

	public List nowDate(String date, Long userId) {
		String sql = "SELECT count(lr.id) from repaymentrecordparticulars rmp LEFT JOIN loanrecord lr ON rmp.loanrecordId=lr.id "
				+ "LEFT JOIN repaymentrecord rp ON rmp.repaymentrecordId=rp.id "
				+ "where lr.userbasicinfo_id="
				+ userId
				+ " and rmp.repState in(0,-1) and rp.preRepayDate LIKE '%"
				+ date + "%' and (ifnull(rmp.money,0)+IFNULL(rmp.middleMoney,0)+IFNULL(rmp.afterMoney,0)+ifnull(rmp.preRepayMoney,0)+IFNULL(rmp.middlePreRepayMoney,0)+IFNULL(rmp.afterPreRepayMoney,0)) > 0";
		List list = commonDao.findBySql(sql.toString());
		return list;
	}

	public List nowDateBorrow(String date, Long userId) {
		String sql = "SELECT count(rp.id) from repaymentrecord rp LEFT JOIN loansign ls ON rp.loanSign_id =ls.id "
				+ "where ls.userbasicinfo_id="
				+ userId
				+ " and rp.preRepayDate LIKE '%" + date + "%'";

		List list = commonDao.findBySql(sql.toString());
		return list;
	}

	/**
	 * 待收收益
	 * 
	 * @param id
	 * @return
	 */
	public Object dueRepay(Long id) {
		String sql = "SELECT sum(IFNULL(rpp.preRepayMoney,0))+sum(IFNULL(rpp.middlePreRepayMoney,0))+sum(IFNULL(rpp.afterPreRepayMoney,0))+IFNULL((select sum(IFNULL(incomeMoney,0)) from voteincome where voterId=rpp.userId and status= 0),0) "
				+ "from "
				+ "repaymentrecord rp JOIN repaymentrecordparticulars rpp on rp.id=rpp.repaymentrecordId "
				+ "where rp.repayState in (1,3) AND rpp.userId=?";
		Object obj = commonDao.findObjectBySql(sql, id);
		if(obj == null) {
			obj = 0.00;
		}
		return obj;
	}

	/**
	 * H5加息券张数
	 * 
	 * @param id
	 * @return
	 */
	public Object getCouponNumber(Long id) {
		String sql = "select count(1) from interestincreasecard where userId=? and date_format(now(),'%Y-%m-%d') < date_format(endTime,'%Y-%m-%d') and useFlag = 0 ";
		Object obj = commonDao.findObjectBySql(sql, id);
		if(obj == null) {
			obj = 0;
		}
		return obj;
	}
	
	/**
	 * 红包总额
	 * @param user_id 用户ID
	 */
	public Object getRedEnvelope(Long user_id) {
		String sql = "select sum(money) from redenvelopedetail where userId = ? and date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and useFlag = 0 ";
		Object obj = commonDao.findObjectBySql(sql,user_id);
		if(obj == null) {
			obj = 0;
		}
		return obj;
	}

	public Object score(Long id) {
		String sql = "SELECT IFNULL(b.suminte,0) FROM borrowersbase b WHERE b.userbasicinfo_id=?";
		Object obj = commonDao.findObjectBySql(sql, id);
		return obj;
	}

	/** 更新资金记录 */
	public void update(Userfundinfo fund) {
		commonDao.update(fund);
	}

	/**
	 * 邮箱加密
	 * 
	 * @param email
	 * @return
	 */
	public static String getEncryptionEmail(String email) {
		String prefix = email.substring(0, email.lastIndexOf("@"));
		if (prefix.length() > 2) {
			prefix = prefix.substring(0, 2);
		}
		prefix += "****";
		String host = email.substring(email.indexOf("@"));
		email = prefix + host;
		return email;
	}

	/**
	 * 手机加密
	 * 
	 * @param phone
	 * @param email
	 */
	public String getEncryptionPhone(String phone) {
		String mphone = phone.substring(3, phone.length() - 4);
		phone = phone.replace(mphone, "****");
		return phone;
	}

	/**
	 * 用户名加密
	 */
	public String getEncryptionName(String userName) {
		System.out.println(userName);
		if (userName.length() >= 6) {
			String name = userName.substring(2, userName.length() - 2);
			userName = userName.replace(name, "****");
			System.out.println(userName);
		}
		if (userName.length() <= 5) {
			String name = userName.substring(1, userName.length() - 1);
			userName = userName.replace(name, "***");
		}
		return userName;
	}

	/**
	 * 判断用户是否已经登录
	 * 
	 * @param request
	 * @param response
	 * @return true 登录
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean webLogin(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		if (user == null) {
			request.setAttribute("please_login", "请先登录...");
			return false;
		}
		return true;
	}

	public Object getFrozen(Long id) {
		String sql = "SELECT sum(lr.tenderMoney) FROM loanrecord lr JOIN loansign ls ON lr.loanSign_id=ls.id "
				+ "where lr.isSucceed=1 AND lr.userbasicinfo_id=? AND (ls.status>0 AND ls.status<6)";
		Object frozen = 0.0;
		List list = commonDao.findBySql(sql, id);
		if (list.size() > 0) {
			frozen = list.get(0);
		}

		return frozen;
	}
	/**
	 * 用户是否有充值记录
	 * @param user
	 * @return
	 */
	public boolean hasRecharged(Userbasicsinfo user) {
		if(user.getUserfundinfo().getCashBalance() > 0) {
			return true;
		}
		/**  */
		String key = "STR:HC9:RECHARGE:RECORD:USER:" + user.getId();
		String rechargeFlag = RedisHelper.get(key);
		if("1".equals(rechargeFlag)) {
			return true;
		} else if("0".equals(rechargeFlag)) {
			return false;
		}
		String sql="SELECT COUNT(1) FROM recharge r WHERE r.user_id=?";
		Object obj=commonDao.findObjectBySql(sql, user.getId());
		if(0==Long.parseLong(obj.toString())){
			RedisHelper.setWithExpireTime(key, "0", 10);
			return false;
		}else{
			RedisHelper.setWithExpireTime(key, "1", 10);
			return true;
		}
	}
	
	
}
