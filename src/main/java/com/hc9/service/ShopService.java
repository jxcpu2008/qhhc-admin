package com.hc9.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.FileUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Fhrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Shop;
import com.hc9.dao.entity.ShopAttention;
import com.hc9.dao.entity.ShopComment;
import com.hc9.dao.entity.ShopCommonProblem;
import com.hc9.dao.entity.ShopExamine;
import com.hc9.dao.entity.ShopFile;
import com.hc9.dao.entity.ShopFinanceDetail;
import com.hc9.dao.entity.ShopInterview;
import com.hc9.dao.entity.ShopPreheat;
import com.hc9.dao.entity.ShopRecord;
import com.hc9.dao.entity.ShopRewardOption;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Usermessage;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class ShopService {

	@Resource
	private HibernateSupport dao;

	@Resource
	private MyindexService myindexService;

	/**
	 * 登录用户session
	 * 
	 * @param request
	 *            请求
	 * @return 用户基本信息
	 */
	public Userbasicsinfo queryUser(HttpServletRequest request) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		return user;
	}

	/**
	 * 店铺封面上传
	 * 
	 * @param request
	 * @param response
	 * @param attach
	 * @return
	 * @throws IOException
	 */
	public String uploadFile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		// 上传的绝对路径
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		String matchPath = "";
		if (null != request.getSession().getAttribute(Constant.SESSION_USER)) {
			matchPath = "/upload/user/"
					+ ((Userbasicsinfo) request.getSession().getAttribute(
							Constant.SESSION_USER)).getId() + "/";
		} else {
			matchPath = "/upload/user/x/";
		}
		// 可上传类型
		String[] types = FileUtil.NORMAL_TYPES;
		// 执行上传操作
		File file = FileUtil.upload(request, realPath + matchPath, types,
				"file", DateUtils.format("yyyyMMddHHmmss"));
		String url = null;
		url = matchPath + file.getName();

		return url;

	}

	/**
	 * 视频文件上传
	 * 
	 * @param request
	 * @param response
	 * @param attach
	 * @return
	 * @throws IOException
	 */
	// public String videourlFile(HttpServletRequest request,
	// HttpServletResponse response) throws IOException {
	// // 上传的绝对路径
	// String realPath = request.getSession().getServletContext()
	// .getRealPath("");
	// String matchPath = "";
	// if (null != request.getSession().getAttribute(Constant.SESSION_USER)) {
	// matchPath = "/upload/user/"
	// + ((Userbasicsinfo) request.getSession().getAttribute(
	// Constant.SESSION_USER)).getId() + "/";
	//
	// } else {
	// matchPath = "/upload/user/x/";
	// }
	// // 可上传类型
	// String[] types = FileUtil.VIDE_TYPES;
	// // 执行上传操作
	// String url = FileUtil.upload(request, realPath + matchPath, types,
	// "file", DateUtils.format("yyyyMMddHHmmss"));
	// return url;
	// }
	// return null;
	// }

	/**
	 * 保存店铺
	 * 
	 * @param project
	 */
	public String saveShopSeria(Shop shop) {
		Serializable seria = dao.save(shop);
		return seria.toString();
	}

	/***
	 * 保存奖励信息
	 */
	public void saveShopRewardOption(ShopRewardOption shopRewardOption) {
		dao.save(shopRewardOption);
	}

	public Object queryIndexForShop(String shopId) {

		String sql = "select max(indexes) from shop_reward_option where shop_id=?";
		Object obj = dao.findObjectBySql(sql.toString(), shopId);
		return obj;

	}

	public void saveShop(Shop shop) {
		dao.getSession().clear();
		dao.saveOrUpdate(shop);
	}

	public Shop queryShop(Integer num) {
		String sql = " select * from shop where id=?";
		Shop base = (Shop) dao.findObjectBySql(sql, num);
		return base;
	}

	@SuppressWarnings("unchecked")
	public Shop queryShowById(String pId) {
		String sql = "select * from shop where id=?";
		Shop shop = dao.findObjectBySql(sql, Shop.class, pId);
		return shop;
	}

	/***
	 * 根据shopId查询所有的奖励信息
	 * 
	 * @param shopId
	 * @return
	 */
	public List<ShopRewardOption> getShopRewardOptionList(String shopId) {
		String sql = "select * from shop_reward_option where  type=1 and shop_id=?";
		List<ShopRewardOption> shopRewardOptionList = dao.findBySql(sql,
				ShopRewardOption.class, shopId);
		return shopRewardOptionList;
	}

	/**
	 * 根据shopId查询所有的股权信息
	 * 
	 * @param shopId
	 * @return
	 */
	public ShopRewardOption getShopRewardOption(String shopId) {
		String sql = "select * from shop_reward_option where  type=2 and shop_id=?";
		ShopRewardOption shopRewardOption = dao.findObjectBySql(sql,
				ShopRewardOption.class, shopId);
		return shopRewardOption;
	}

	/***
	 * 删除店铺
	 * 
	 * @param pId
	 * @return
	 */
	public boolean deleteShopById(String pId) {
		String sql = "select * from shop where id=?";
		Shop shop = dao.findObjectBySql(sql, Shop.class, pId);
		try {
			if (shop != null) {
				dao.delete(shop);
				dao.delete(shop.getShopFile());
				dao.delete(shop.getShopRewardOptions());
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/***
	 * 删除奖励
	 * 
	 * @param pId
	 * @return
	 */
	public boolean delRewardById(String shopRoId) {
		String sql = "select * from shop_reward_option where id=?";
		ShopRewardOption shopRewardOption = dao.findObjectBySql(sql,
				ShopRewardOption.class, shopRoId);
		try {
			if (shopRewardOption != null) {
				dao.delete(shopRewardOption);
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 根据id查奖励
	 * 
	 * @param shopRoId
	 * @return
	 */
	public ShopRewardOption queryRewardById(String shopRoId) {
		String sql = "select * from shop_reward_option where id=?";
		ShopRewardOption shopRewardOption = dao.findObjectBySql(sql,
				ShopRewardOption.class, shopRoId);
		return shopRewardOption;
	}

	public ShopRewardOption queryRewardByIndex(String shopId, String indexes) {
		String sql = "select * from shop_reward_option where type=1 and shop_id=? and indexes=?";
		ShopRewardOption shopRewardOption = dao.findObjectBySql(sql,
				ShopRewardOption.class, shopId, indexes);
		return shopRewardOption;
	}

	/**
	 * 查询店铺所有模式
	 * 
	 * @param shopId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ShopRewardOption> queryAllShopRo(String shopId) {
		String sql = "From ShopRewardOption Where shop_id=" + shopId
				+ " order by  type ";
		List<ShopRewardOption> list = dao.find(sql);
		return list;
	}

	@SuppressWarnings("unchecked")
	public ShopFile queryShopFileById(String id) {
		String sql = "From ShopFile Where id=" + id;
		List<ShopFile> list = dao.find(sql);
		return list.size() > 0 ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public ShopFile queryShopFileByShopId(String shopId) {
		String sql = "From ShopFile Where shop_id=" + shopId;
		List<ShopFile> list = dao.find(sql);
		return list.size() > 0 ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public Shop queryShopById(String pId) {
		String sql = "From Shop Where id=" + pId;
		List<Shop> list = dao.find(sql);
		return list.size() > 0 ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public ShopRewardOption queryShopRewardOptionById(String pId) {
		String hql = "from ShopRewardOption w where w.shop.id=" + pId
				+ " order by awardTime desc";
		List<ShopRewardOption> list = dao.find(hql);
		return list.size() > 0 ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public ShopRecord queryShopRecordByorderNum(String orderNum) {
		String sql = "select * From shop_record WHERE orderNum=?";
		return dao.findObjectBySql(sql, ShopRecord.class, orderNum);
	}

	@SuppressWarnings("unchecked")
	public List<ShopRecord> queryShopRecord(String pId) {

		String sql = "select sr.id,sr.isSucceed,sr.tenderMoney,sr.tenderTime,u.name from shop_record sr, userbasicsinfo u where sr.user_id=u.id and sr.shop_id="
				+ pId;
		List list = dao.findBySql(sql.toString());
		return list.size() > 0 ? list : null;
	}

	public List<ShopRecord> queryRecordList(String pId) {

		String hql = "from ShopRecord  s where s.isSucceed=1  and s.shop.id="
				+ pId;
		List<ShopRecord> list = dao.find(hql);
		return list;
	}

	public void updateShop(Shop p) {
		dao.update(p);
	}

	public void uptShopExamine(ShopExamine shopExamine) {
		dao.update(shopExamine);
	}

	/**
	 * 保存店铺审批
	 * 
	 * @param shopExamine
	 */
	public void saveShopExamine(ShopExamine shopExamine) {
		dao.save(shopExamine);
	}

	public ShopExamine queryShopExamineById(String shopId) {
		String sql = "select * from shop_examine where shop_id=?";
		ShopExamine shopExamine = dao.findObjectBySql(sql, ShopExamine.class,
				shopId);
		return shopExamine;
	}

	public void uptShopRewardOption(ShopRewardOption shopRewardOption) {
		dao.update(shopRewardOption);
	}

	public void saveShopRecord(ShopRecord sr) {
		dao.save(sr);
	}

	public void updateShopRecord(ShopRecord sr) {
		dao.update(sr);
	}

	public void saveprehRecord(ShopPreheat pr) {
		dao.save(pr);
	}

	public void saveShopAttention(ShopAttention ap) {
		dao.save(ap);
	}

	public void updateShopAttention(ShopAttention ap) {
		dao.update(ap);
	}

	public void saveShopCommonProblem(ShopCommonProblem pcp) {
		dao.save(pcp);
	}

	public ShopAttention getShopAttention(String pId, Long uId) {
		String sql = "select * from shop_attention where user_id=" + uId
				+ " AND shop_id=" + pId;
		ShopAttention atten = dao.findObjectBySql(sql, ShopAttention.class);
		return atten;
	}

	public ShopInterview getQuestionproject(String pId, Long uId) {
		String sql = "select * from shop_interview where to_user=" + uId
				+ " AND shop_id=" + pId;
		ShopInterview ques = dao.findObjectBySql(sql, ShopInterview.class);
		return ques;
	}

	public void saveMessage(Usermessage ap) {
		dao.save(ap);
	}

	public void saveShopInterview(ShopInterview ap) {
		dao.save(ap);
	}

	/**
	 * 保存第二步
	 * 
	 * @param projectfile
	 */
	public void saveShopFile(ShopFile shopFile, String shopFileId) {
		dao.getSession().clear();
		if (shopFileId == "") {
			dao.save(shopFile);
		} else {
			dao.update(shopFile);
		}
	}

	@SuppressWarnings("rawtypes")
	public List getProjectList() {
		String sql = "select s.id,s.shop_name,s.province,s.city,s.introduct,s.shop_cover_image,s.raise_funds,s.state,s.self_funds,"
				+ "s.user_id,s.real_funds,case state when 2 then (datediff(s.preheatend_time,now())) when 4 then (datediff(s.end_invest_time,now())) end, "
				+ " (SELECT count(id) from shop_attention where shop_id=s.id and success=1 ),"
				+ "(SELECT count(id) from shop_interview where shop_id=s.id and success=1 ),"
				+ "(SELECT count(id) from shop_record where shop_id=s.id and isSucceed=1 ),"
				+ "(SELECT sum(tenderMoney) from shop_record where shop_id=s.id and isSucceed=1 ),"
				+ "(SELECT money from shop_reward_option where shop_id=s.id and type=2 ),"
				+ "(DATE_ADD(s.preheatstar_time,INTERVAL s.preheat DAY)),(DATE_ADD(s.start_invest_time,INTERVAL s.financing DAY)) "
				+ " from shop s,shop_file f where s.id=f.shop_id and s.state in(2,4,5,6,7,8) and s.type=1 "
				+ "ORDER BY s.state DESC,(SELECT count(id) from shop_record where shop_id=s.id and isSucceed=1 ) DESC LIMIT 0,2";
		List list = dao.findBySql(sql.toString());
		return list;
	}

	/*
	 * @SuppressWarnings("rawtypes") public List getProjectList1() {
	 * 
	 * String sql =
	 * "select s.id,s.shop_name,s.province,s.city,s.introduct,s.shop_cover_image,s.raise_funds,s.state,"
	 * +
	 * "s.self_funds,s.user_id,s.real_funds,datediff(s.end_invest_time,now()),"
	 * +
	 * " (SELECT count(id) from shop_attention where shop_id=s.id and success=1 ),"
	 * +
	 * "(SELECT count(id) from shop_interview where shop_id=s.id and success=1 ),"
	 * +
	 * "(SELECT count(id) from shop_record where shop_id=s.id and isSucceed=1 ),  "
	 * +
	 * "(SELECT sum(tenderMoney) from shop_record where shop_id=s.id and isSucceed=1 ),"
	 * +
	 * "(SELECT money from shop_reward_option where shop_id=s.id and type=2 ) "
	 * +
	 * "from shop s,shop_file f where s.id=f.shop_id and s.state=4 and s.type=1 ORDER BY s.id DESC LIMIT 0,8"
	 * ;
	 * 
	 * List list = dao.findBySql(sql.toString());
	 * 
	 * return list; }
	 * 
	 * @SuppressWarnings("rawtypes") public List getProjectList2() {
	 * 
	 * String sql =
	 * "select s.id,s.shop_name,s.province,s.city,s.introduct,s.shop_cover_image,s.raise_funds,s.state,"
	 * + "s.self_funds,s.user_id,s.real_funds,s.preheat," +
	 * " (SELECT count(id) from shop_attention where shop_id=s.id and success=1 ),"
	 * +
	 * "(SELECT count(id) from shop_interview where shop_id=s.id and success=1 ),"
	 * +
	 * "(SELECT count(id) from shop_record where shop_id=s.id and isSucceed=1 ),  "
	 * +
	 * "(SELECT sum(tenderMoney) from shop_record where shop_id=s.id and isSucceed=1 ),"
	 * +
	 * "(SELECT money from shop_reward_option where shop_id=s.id and type=2 ) "
	 * +
	 * "from shop s,shop_file f where s.id=f.shop_id  and s.state=5 and s.type=1 ORDER BY s.id DESC LIMIT 0,8"
	 * ;
	 * 
	 * List list = dao.findBySql(sql.toString());
	 * 
	 * return list; }
	 */

	@SuppressWarnings("unchecked")
	public List<ShopComment> getCommentshopList(String shopId) {
		String sql = "select * from shop s,shop_comment c where cmtIsShow=1 and c.shop_Id=s.id and c.shop_Id="
				+ shopId + " order by c.commentTime desc ";

		List<ShopComment> list = dao.findBySql(sql.toString());
		return list;
	}

	// 删除店铺评论
	public boolean deleteShopComms(String ids) {
		try {
			String[] id = ids.split(",");
			// 删除消息
			for (int i = 0; i < id.length; i++) {
				dao.delete(Long.valueOf(id[i]), ShopComment.class);
			}
		} catch (Throwable e) {
			e.getMessage();
			return false;
		}
		return true;
	}

	// 评论分页
	@SuppressWarnings("unchecked")
	public PageModel getCommentshopList(String shopId, PageModel page) {
		StringBuffer sqlCount = new StringBuffer(
				"select count(id) from shop_comment where ref_comm_id is null and shop_Id=");
		sqlCount.append(shopId);
		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.cmtContent,c.commentTime,c.cmtReply,c.replyTime, ");
		sql.append(" s.user_id,(select userName from userbasicsinfo where id=c.commentator_id), ");
		sql.append("(select userName from userbasicsinfo where id=c.replyer_id), ");
		sql.append("(select i.imgUrl from userbasicsinfo u,userrelationinfo i where u.id=c.commentator_id and u.id=i.user_id), ");
		sql.append("(select i.imgUrl from userbasicsinfo u,userrelationinfo i where u.id=c.replyer_id and u.id=i.user_id) ");
		sql.append(" from shop s,shop_comment c where cmtIsShow=1 and c.shop_Id=s.id and c.shop_Id="
				+ shopId);
		sql.append(" and c.ref_comm_id is null");
		sql.append(" order by c.commentTime desc ");
		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());
		List<ShopComment> list = dao.findBySql(sql.toString());
		page.setList(list);
		return page;
	}

	public Object getShopCount(String pId) {
		String sql = "select (select count(id) from shop_attention where shop_id=s.id and success=1),"
				+ "(select count(id) from shop_interview where shop_id=s.id and success=1),"
				+ "(select count(id) from shop_record where shop_id=s.id and isSucceed=1 ), "
				+ "(select sum(tenderMoney) from shop_record r where shop_id=s.id and isSucceed =1),"
				+ "(select count(id) from shop_record r where shop_id=s.id and isSucceed =1 and type = 1),"
				+ "(select count(id) from shop_record r where shop_id=s.id and isSucceed in (1,0) and type = 2),"
				+ "(select sum(tenderMoney) from shop_record r where shop_id=s.id and isSucceed in (1,0) and type = 2),"
				+ "(select count(id) from shop_comment c where shop_id=s.id and cmtIsShow =1 )"
				+ "  from shop s where s.id=" + pId;
		Object obj = dao.findObjectBySql(sql.toString());
		return obj;
	}

	public Shop getShopById(String pId) {
		String sql = "select * from shop where id=" + pId;
		Shop shop = dao.findObjectBySql(sql, Shop.class);
		return shop;
	}

	/***
	 * 根据Id查询店铺购买信息
	 * 
	 * @param pId
	 * @return
	 */
	public ShopRecord getShopRecordById(String pId) {
		String sql = "select * from shop_record where id=" + pId;
		ShopRecord shopRecord = dao.findObjectBySql(sql, ShopRecord.class);
		return shopRecord;
	}

	public List<Shop> getShopByList() {
		String hql = "from Shop";
		List<Shop> pbList = dao.find(hql);
		return pbList;
	}

	public List<ShopComment> getCommentshopList() {

		String hql = "from ShopComment";
		List<ShopComment> list = dao.find(hql);

		return list;
	}

	/**
	 * 提交店铺
	 * 
	 * @param request
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel getMyShopList(HttpServletRequest request, PageModel page,
			Integer state, Integer type) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		StringBuffer sql = new StringBuffer(
				" SELECT * FROM shop s where s.user_id=? and s.type=" + type);
		StringBuffer sqlCount = new StringBuffer(
				"select count(s.id) from shop s where s.user_id=");
		sqlCount.append(user.getId()).append(" and s.type=" + type);
		if (state != null) {
			if (state == 0) {
				sql.append(" and (s.state=0 or s.state=1) and (select count(1) from shop_examine where shop_id=s.id) = 0 ");
				sqlCount.append(" and (s.state=0 or s.state=1) ");
			} else if (state == 1) {
				sql.setLength(0);
				sql.append("SELECT * FROM shop s,shop_examine se where s.id=se.shop_id and s.user_id=? and s.type="
						+ type);
				sql.append(" and (s.state=0 or s.state=1) and se.state is not NULL ");
				sqlCount.setLength(0);
				sqlCount.append("select count(s.id) from shop s,shop_examine se where s.id=se.shop_id and s.user_id=");
				sqlCount.append(user.getId()).append(" and s.type=" + type);
				sqlCount.append(" and (s.state=0 or s.state=1) and se.state is not NULL ");
			} else if (state == 2) {
				sql.append(" and (s.state=2 or s.state=3) ");
				sqlCount.append(" and (s.state=2 or s.state=3) ");
			} else if (state == 3) {
				sql.append(" and s.state=4 ");
				sqlCount.append(" and s.state=4 ");
			} else if (state == 4) {
				sql.append(" and  (s.state=5 or s.state=6) ");
				sqlCount.append(" and  (s.state=5 or s.state=6) ");
			}
		}
		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量
		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());
		List<Shop> list = dao.findBySql(sql.toString(), Shop.class,
				user.getId());
		page.setList(list);// project集合
		return page;
	}

	/**
	 * 通过店铺编号获取融资总额
	 * 
	 * @param shopId
	 * @return 融资总额
	 */
	public BigDecimal getTenderMoney(Long shopId) {
		String sql = "select sum(tenderMoney) from shop_record where isSucceed=1 and shop_id="
				+ shopId;
		List<BigDecimal> list = dao.findBySql(sql);
		if (list.get(0) != null) {
			return list.get(0);
		} else {
			return new BigDecimal(0.0);
		}
	}

	@SuppressWarnings("unchecked")
	public BigDecimal getTenderMoneyObj(Long shopId) {
		String sql = "select sum(tenderMoney) from shop_record where isSucceed=1 and shop_id="
				+ shopId;
		List<BigDecimal> list = dao.findBySql(sql);
		if (list.get(0) != null) {
			return list.get(0);
		} else {
			return new BigDecimal(0.0);
		}
	}

	/**
	 * 关注店铺
	 * 
	 * @param request
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel getrefer2(HttpServletRequest request, PageModel page) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		StringBuffer sql = new StringBuffer(
				"select * from shop s, shop_attention a where s.id=a.shop_id and a.success=1 and  a.user_id=? ");
		StringBuffer sqlCount = new StringBuffer(
				"select count(s.id) from shop s, shop_attention a where s.id=a.shop_id and a.success=1 and a.user_id=");
		sqlCount.append(user.getId());

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());

		List<Shop> list = dao.findBySql(sql.toString(), Shop.class,
				user.getId());
		page.setList(list);// project集合
		return page;
	}

	/**
	 * 约谈店铺
	 * 
	 * @param request
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel getTalkShop(HttpServletRequest request, PageModel page) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		StringBuffer sql = new StringBuffer(
				"select * from shop s, shop_interview i where s.id=i.shop_id and i.success=1 and i.user_id=? ");
		StringBuffer sqlCount = new StringBuffer(
				"select count(s.id) from shop s, shop_interview i where s.id=i.shop_id and i.success=1 and i.user_id=");
		sqlCount.append(user.getId());

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());

		List<Shop> list = dao.findBySql(sql.toString(), Shop.class,
				user.getId());
		page.setList(list);// project集合
		return page;
	}

	/**
	 * 预热店铺
	 * 
	 * @param request
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel getPreheatShop(HttpServletRequest request, PageModel page) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);
		StringBuffer sql = new StringBuffer(
				"select * from shop s, shop_preheat p where s.id=p.shop_id and p.isSucceed=1 and p.user_id=?  GROUP BY s.id");
		StringBuffer sqlCount = new StringBuffer(
				"select count(p.id) from shop s, shop_preheat p where s.id=p.shop_id and p.isSucceed=1 and p.user_id=");
		sqlCount.append(user.getId());

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());

		List<Shop> list = dao.findBySql(sql.toString(), Shop.class,
				user.getId());
		page.setList(list);// project集合
		return page;
	}

	/**
	 * 投资店铺
	 * 
	 * @param request
	 * @param page
	 * @return
	 */
	public PageModel getStorerecordlist(HttpServletRequest request,
			PageModel page, Integer state, Integer type) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);

		StringBuffer sql = new StringBuffer(
				"select * from shop_record r , shop s where s.id=r.shop_id and r.user_id=? and r.isSucceed=1");
		sql.append(" and s.type=" + type);
		StringBuffer sqlCount = new StringBuffer(
				"select count(s.id) from shop_record s,shop p where p.id=s.shop_id  and s.isSucceed=1 and s.user_id=");
		sqlCount.append(user.getId());
		sqlCount.append(" and s.type=" + type);
		if (state != null) {
			if (state == 1) {
				sql.append(" and s.state=4 ");
				sqlCount.append(" and p.state=4 ");

			} else if (state == 2) {

				sql.append(" and s.state=5 ");
				sqlCount.append(" and p.state=5 ");
			} else if (state == 3) {
				sql.append(" and s.state=6 ");
				sqlCount.append(" and p.state=6 ");

			}

		}

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());

		List<ShopRecord> list = dao.findBySql(sql.toString(), ShopRecord.class,
				user.getId());
		page.setList(list);// project集合
		return page;
	}

	/**
	 * 融资中的店铺
	 * 
	 * @param uId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel getFinancingShops(HttpServletRequest request,
			PageModel page) {
		Userbasicsinfo user = (Userbasicsinfo) request.getSession()
				.getAttribute(Constant.SESSION_USER);

		StringBuffer sql = new StringBuffer(
				"select * from shop  where state=4 and user_id=? ");

		StringBuffer sqlCount = new StringBuffer(
				"select count(id) from shop  where state=4 and user_id=");
		sqlCount.append(user.getId());

		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue());// 设置总条数量

		sql.append("  LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* Constant.STATUES_THERE).append(",")
				.append(page.getNumPerPage());

		List<Shop> list = dao.findBySql(sql.toString(), Shop.class,
				user.getId());
		page.setList(list);// project集合
		return page;
	}

	public List getStorerecordlist(String uId) {
		String sql = "select p.id, p.pName ,sum(s.tenderMoney),s.tenderTime, s.isSucceed ,s.isPrivilege "
				+ "from shop p , shop_record s where p.id=s.shop_id and user_id="
				+ uId + " GROUP BY p.id";
		List list = dao.findBySql(sql.toString());
		return list;
	}

	// 保存店铺评论
	@SuppressWarnings("unchecked")
	public void saveComments(ShopComment com) {
		dao.getSession().clear();
		dao.saveOrUpdate(com);
	}

	// 查询店铺评论
	public ShopComment getShopComment(Long id) {
		return dao.get(ShopComment.class, id);
	}

	// 修改店铺评论
	public void updateShopComment(ShopComment sc) {
		dao.getSession().clear();
		dao.update(sc);
	}

	public void saveFhRecord(Fhrecord fr) {
		dao.save(fr);
	}

	/**
	 * @param userid当前用户id
	 * @param state
	 *            店铺状态
	 * @return 返回结果
	 */
	@SuppressWarnings("rawtypes")
	public List findProjectNameByState(Long userid, int state) {
		StringBuffer sb = new StringBuffer(
				"SELECT id,shop_name FROM shop WHERE user_id=");
		sb.append(userid).append(" and state=").append(state);
		return dao.findBySql(sb.toString());

	}

	/**
	 * <p>
	 * Title: isFull
	 * </p>
	 * <p>
	 * Description: 判断该店铺是否融资成功
	 * </p>
	 * 
	 * @param proId
	 *            店铺编号
	 * @return boolean
	 */
	public boolean isFull(String proId) {
		StringBuffer sb = new StringBuffer(
				"SELECT  p.real_funds FROM shop p where  p.id=").append(proId);
		Object object = dao.findObjectBySql(sb.toString());
		return object != null ? (Double.valueOf(object.toString()) == 0 ? true
				: false) : false;
	}

	/***
	 * 查询是否存在值
	 * 
	 * @param projectId
	 * @return
	 */
	public ShopRewardOption getProjectRewardOption(String shopId) {
		String sql = "select * from  shop_reward_option  Where shop_id="
				+ shopId;
		List<ShopRewardOption> list = dao.findBySql(sql.toString());
		return list.size() > 0 ? list.get(0) : null;
	}

	/***
	 * 新增店铺奖励选项
	 * 
	 * @param shopRewardOption
	 */
	public void saveProjectRewardOption(ShopRewardOption shopRewardOption) {
		dao.save(shopRewardOption);
	}

	/***
	 * 根据店铺Id和用户Id查询购买记录是否存在
	 * 
	 * @param userId
	 * @param shopId
	 * @return
	 */
	public boolean getRewardCount(Long userId, Long shopId, Long shopRoId,
			String type) {
		try {
			String sql = "select count(*) from shop_record where shop_id=? and user_id=? and shopro_id=? and type=? and isSucceed in (0,1)";
			Object rewardCount = dao.findObjectBySql(sql, shopId, userId,
					shopRoId, type);
			if (Integer.valueOf(rewardCount.toString()) <= 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/***
	 * 根据id查询店铺股权奖励信息
	 * 
	 * @param shopRoId
	 * @return
	 */
	public ShopRewardOption queryShopRewardOption(String shopRoId) {
		String sql = "select * from shop_reward_option where id=" + shopRoId;
		ShopRewardOption shopRewardOption = dao.findObjectBySql(sql,
				ShopRewardOption.class);
		return shopRewardOption;
	}

	/***
	 * 根据店铺Id和股权奖励Id、类型查询购买总人数
	 * 
	 * @param shopId
	 * @param shopRoId
	 * @return
	 */
	public Integer getRecordCount(Long shopId, Long shopRoId, String type) {
		String sql = "select count(distinct s.id) from shop_record s   where s.shop_id=? and s.shopro_id=? and type=?";
		Object recordCount = dao.findObjectBySql(sql, shopId, shopRoId, type);
		return Integer.valueOf(recordCount.toString());
	}

	/**
	 * 店铺进度
	 * 
	 * @param id
	 * @return
	 */
	public List<ShopFinanceDetail> selshopFinanceDetailId(String id) {
		String sql = "SELECT * FROM shop_finance_detail WHERE shop_id=" + id;
		return dao.findBySql(sql, ShopFinanceDetail.class);
	}

	public PageModel getShopComList(String userId, PageModel page) {
		String sql = "SELECT * from shop_comment sc,shop s "
				+ "where s.id=sc.shop_Id and sc.commentator_id=? GROUP BY s.id";
		String sqlcount = "SELECT count(s.id) from shop_comment sc,shop s "
				+ "where s.id=sc.shop_Id and sc.commentator_id=? GROUP BY s.id";
		page.setTotalCount(dao.queryNumberSql(sqlcount.toString(), userId)
				.intValue());
		List<ShopComment> shopList = dao.findBySql(sql, ShopComment.class,
				userId);
		page.setList(shopList);
		return page;

	}

	public PageModel getShopComInfoList(PageModel page, String shopId) {
		String sql = "SELECT * from shop_comment " + "where shop_Id=?";
		String sqlcount = "SELECT count(id) from shop_comment "
				+ "where shop_Id=?";
		page.setTotalCount(dao.queryNumberSql(sqlcount.toString(), shopId)
				.intValue());
		List<ShopComment> shopList = dao.findBySql(sql, ShopComment.class,
				shopId);
		page.setList(shopList);
		return page;

	}

	/**
	 * 查询店铺list
	 * 
	 * @param loanType
	 * @param city
	 * @param money
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel getshopcrowdList(String type, String city, String money,
			String state, String order, PageModel page) {

		StringBuffer sqlsbshop = new StringBuffer(
				" select s.id,s.shop_name,s.introduct,s.raise_funds,s.real_funds,s.self_funds,s.BIS,s.shop_cover_image,s.province,s.city,s.county,"
						+ "(SELECT count(id) from shop_attention where shop_id=s.id and success=1 ) ,"
						+ "(SELECT count(id) from shop_interview where shop_id=s.id and success=1 ),"
						+ "(SELECT count(id) from shop_record where shop_id=s.id and isSucceed in (0,1) ),"
						+ "(SELECT sum(tenderMoney) from shop_record where shop_id=s.id and isSucceed=1 ),"
						+ "(select money from shop_reward_option where type=2 and shop_id=s.id),"
						+ " s.state,case state when 2 then (datediff(s.preheatend_time,now())) when 4 then (datediff(s.end_invest_time,now())) end ");
		StringBuffer sqlCountshop = new StringBuffer("select count(s.id) ");
		StringBuffer sqlshop = new StringBuffer(" from shop s  where 1=1 ");
		if (!"".equals(state) && null != state) {
			String[] strs = state.split(",");
			sqlshop.append(" and state in (");
			for (int i = 0; i < strs.length; i++) {
				sqlshop.append(strs[i]);
				if (i != strs.length - 1) {
					sqlshop.append(",");
				}
			}
			sqlshop.append(") ");
		} else {
			sqlshop.append(" and state in (2,4,6,7,8) ");
		}

		if (!"".equals(type) && null != type) {
			sqlshop.append(" and s.pIndustry1='").append(type).append("'");
		}
		if (!"".equals(city) && null != city) {
			sqlshop.append(" and s.city='").append(city).append("'");
		}
		if (!"".equals(money) && null != money) {
			if (Integer.parseInt(money) == 100) {
				sqlshop.append(" and s.raise_funds<=").append(1000000);
			}
			if (Integer.parseInt(money) == 200) {
				sqlshop.append(" and s.raise_funds>").append(1000000)
						.append(" and s.raise_funds<=").append(2000000);
			}
			if (Integer.parseInt(money) == 300) {
				sqlshop.append(" and s.raise_funds>=").append(2000000)
						.append(" and s.raise_funds<=").append(3000000);
			}

			if (Integer.parseInt(money) == 400) {
				sqlshop.append(" and s.raise_funds>").append(3000000)
						.append(" and s.raise_funds<=").append(4000000);

			}

			if (Integer.parseInt(money) == 500) {
				sqlshop.append(" and s.raise_funds>").append(4000000)
						.append(" and s.raise_funds<=").append(5000000);
			}
			if (Integer.parseInt(money) == 600) {
				sqlshop.append(" and s.raise_funds>").append(5000000);

			}
		}
		if (!"".equals(order) && null != order) {
			if ("time".equals(order)) { // 最新发布
				sqlshop.append(" order by create_time desc");
			}
			if ("atten".equals(order)) { // 关注
				sqlshop.append(" order by (SELECT count(id) from shop_attention where shop_id=s.id and success=1) desc");
			}
			if ("inte".equals(order)) { // 约谈
				sqlshop.append(" order by (SELECT count(id) from shop_interview where shop_id=s.id and success=1 ) desc");
			}
			if ("mone".equals(order)) { // 目标金额
				sqlshop.append(" order by raise_funds desc");
			}
		} else {
			sqlshop.append(" order by id desc ");
		}
		sqlCountshop.append(sqlshop.toString());
		sqlsbshop.append(sqlshop.toString());
		page.setTotalCount(dao.queryNumberSql(sqlCountshop.toString())
				.intValue());

		sqlsbshop
				.append(" LIMIT ")
				.append((page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",")
				.append(page.getNumPerPage());
		List<Shop> list = dao.findBySql(sqlsbshop.toString());
		page.setList(list);
		return page;
	}
	
	/**
	 * 查询被审被通过的标
	 * 
	 * @return
	 */
	public List getLoansignlist() {
		String sql = "select ls.id, ls.name,ls.loanUnit,ls.issueLoan,ls.priority,ls.middle,ls.after,lbs.remark,ls.remonth,ls.rest_money,ls.type,"
				+ "(SELECT count(id) from loanrecord where loanSign_id=ls.id and subType=1 and isSucceed in (0,1,2)),"
				+ "(SELECT count(id) from loanrecord where loanSign_id=ls.id and subType=2 and isSucceed in (0,1,2)),"
				+ "(SELECT count(id) from loanrecord where loanSign_id=ls.id and subType=3 and isSucceed in (0,1,2)),ls.status,ls.rest_money,u.staff_no,ls.prio_rate,"
				+ "ls.prio_aword_rate,lbs.loanimg,ls.publish_time,u.credit_rate, ifnull(e.name,''),ls.validity "
				+ " from loansign ls,loansignbasics lbs ,userbasicsinfo u ,escrow e "
				+ "where u.id=ls.userbasicinfo_id and ls.escrow_id=e.id and ls.id=lbs.id and ls.state=2 and (ls.status >0 and ls.status <9) and ls.recommend =1 "
				+ " ORDER BY ls.rest_money DESC,ls.publish_time DESC,ls.state LIMIT 0 ,5";
		List List = dao.findBySql(sql);
		return List;
	}

	/**
	 * 查询全部店铺的总数
	 * 
	 * @return
	 */
	public Object getShopCount() {
		StringBuffer sql = new StringBuffer();
		sql.append("select (select count(id) from shop where state in (2,4,6,7,8)),");
		sql.append("(select count(id) from shop where state=2),");
		sql.append("(select count(id) from shop where state=4),");
		sql.append("(select count(id) from shop where state in (5,6,7,8))");
		Object obj = dao.findObjectBySql(sql.toString());
		return obj;
	}

	/**
	 * 更新是否推荐首页
	 * 
	 * @param loan
	 * @param type
	 * @return 返回更新条数
	 */
	public int updateLoansign(Loansign loan, String state) {
		StringBuffer sql = new StringBuffer(
				"UPDATE loansign SET loansign.onIndex=");
		sql.append(state);
		sql.append(" WHERE loansign.id IN(" + loan.getId() + ")");
		int i = dao.executeSql(sql.toString());
		return i;
	}
	/**
	 *  更新是否热门推荐
	 * @param loan
	 * @param state
	 * @return
	 */
	public int updateLoansignRec(Loansign loan, String state) {
		StringBuffer sql = new StringBuffer(
				"UPDATE loansign SET loansign.recommend=");
		sql.append(state);
		sql.append(" WHERE loansign.id IN(" + loan.getId() + ")");
		int i = dao.executeSql(sql.toString());
		return i;
	}



	/**
	 * 查询该店铺的建立
	 * 
	 * @param id
	 *            店铺
	 * @return
	 */
	public Object getLotteryCount(Long id) {
		String sql = "SELECT COUNT(s.lottery) from shop_record s WHERE s.shop_id=? AND s.type=1";
		List list = dao.findBySql(sql, id);
		return list.get(0);
	}

	public Object getLotteryById(Long id) {
		String sql = "SELECT s.lottery from shop_record s WHERE s.id=?";
		return dao.findObjectBySql(sql, id);
	}

	public void updateShopRewardOption(ShopRewardOption shopRewardOption) {
		dao.update(shopRewardOption);
	}

	/**
	 * 推广链接
	 * 
	 * @return
	 */
	public String getPromoteLink(HttpServletRequest request, Long shopRecordId) {
		String link = Constant.WEBSERVER + "/to-regist?rec=";

		// String link = request.getScheme() + "://" + request.getServerName() +
		// ":" + request.getServerPort() + request.getContextPath() +
		// "/to-regist?rec=";
		link += StringUtil.generatePassword("lucky-" + shopRecordId);
		return link;
	}

	/**
	 * 店铺投资记录
	 * 
	 * @param rid
	 * @return
	 */
	public ShopRecord getShopRecord(Long rid) {
		String sql = "SELECT * FROM shop_record WHERE id=?";
		ShopRecord record = dao.findObjectBySql(sql, ShopRecord.class, rid);
		return record;
	}
}
