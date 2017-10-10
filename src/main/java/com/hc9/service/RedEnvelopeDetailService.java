package com.hc9.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.log.LOG;
import com.hc9.common.redis.RedisUtil;
import com.hc9.common.util.DateUtil;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.MoneyUtil;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.RedEnvelopeDetail;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class RedEnvelopeDetailService {

	@Resource
	private HibernateSupport dao;
	
	@Resource
	private RegistrationService registrationService;
	
	@Resource
	private LoanSignQuery loanSignQuery;
	

	/***
	 * 添加投资红包
	 * 
	 * @param loanrecord
	 * @param sourceType
	 */
	public void saveRedEnvelopeDetail(Loanrecord loanrecord,Userbasicsinfo user, Integer sourceType) {
		String time = DateUtils.format("yyyy-MM-dd");
		RedEnvelopeDetail redEnvelopeDetail = new RedEnvelopeDetail();
		redEnvelopeDetail.setUserbasicsinfo(user);
		// 红包金额
		Double bonusMoney = 0.00;
		//使用限额
		Double lowestUseMoney=0.00;
		if (loanrecord.getTenderMoney() >= 5000&& loanrecord.getTenderMoney() < 20000) {
			bonusMoney = 5.00;
			lowestUseMoney=1000.00;
		} else if (loanrecord.getTenderMoney() >= 20000&& loanrecord.getTenderMoney() < 50000) {
			bonusMoney = 25.00;
			lowestUseMoney=25000.00;
		} else if (loanrecord.getTenderMoney() >= 50000&& loanrecord.getTenderMoney() < 80000) {
			bonusMoney = 60.00;
			lowestUseMoney=80000.00;
		} else if (loanrecord.getTenderMoney() >= 80000) {
			bonusMoney = 100.00;
			lowestUseMoney=150000.00;
		}
		redEnvelopeDetail.setMoney(bonusMoney);
		redEnvelopeDetail.setLoanrecord_id(loanrecord.getId());
		redEnvelopeDetail.setBeginTime(time);
		redEnvelopeDetail.setReceiveTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
		redEnvelopeDetail.setEndTime(DateUtil.getSpecifiedDateAfter(time, 90));
		redEnvelopeDetail.setUseFlag(Constant.STATUES_ZERO);
		redEnvelopeDetail.setSourceType(sourceType);
		redEnvelopeDetail.setLowestUseMoney(lowestUseMoney);
		dao.save(redEnvelopeDetail);
	}
	

	/***
	 * 根据Id获取奖励红包明细表
	 * 
	 * @param id
	 * @return
	 */
	public RedEnvelopeDetail getRedEnvelopeDetail(Long id) {
		RedEnvelopeDetail redEnvelopeDetail = dao.get(RedEnvelopeDetail.class,id);
		return redEnvelopeDetail;
	}

	/***
	 * 判断首次购买用户是否在在活动期间
	 * 
	 * @param userId
	 * @return
	 */
	public boolean getRedEnvelopeDetails(Long userId, Costratio costratio) {
		String sql = "select * from userbasicsinfo where  id=? and DATE_FORMAT(createTime, '%Y-%m-%d')>=DATE_FORMAT('"
				+ costratio.getStarTime().trim()
				+ "', '%Y-%m-%d')  AND DATE_FORMAT(createTime, '%Y-%m-%d')<=DATE_FORMAT('"
				+ costratio.getEndTime().trim() + "', '%Y-%m-%d') ";
		List<Userbasicsinfo> list = dao.findBySql(sql, Userbasicsinfo.class,
				userId);
		if (list.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/***
	 * 判断注册之后是否在活动范围内进行授权操作的
	 * 
	 * @param userId
	 * @return
	 */
	public boolean sign(Userbasicsinfo user, Costratio costratio) {
		String sql = "select count(1) from userbasicsinfo where id=? and DATE_FORMAT('"+user.getAuthIpsTime()+"', '%Y-%m-%d')>=DATE_FORMAT('"
				+ costratio.getStarTime().trim()
				+ "', '%Y-%m-%d')  AND DATE_FORMAT('"+user.getAuthIpsTime()+"', '%Y-%m-%d')<=DATE_FORMAT('"
				+ costratio.getEndTime().trim() + "', '%Y-%m-%d') ";
		Object obj = dao.findObjectBySql(sql,user.getId());
		return obj != null && Integer.valueOf(obj.toString()) > 0 ? true : false; 
	}
	

	/***
	 * 判断每笔投资是否在活动期间
	 * 
	 * @param userId
	 * @param costratio
	 * @return
	 */
	public boolean getUserLoanRecordRed(Long userId, Costratio costratio) {
		String sql = "select * from loanrecord where  id=? and DATE_FORMAT(tenderTime, '%Y-%m-%d')>=DATE_FORMAT('"
				+ costratio.getStarTime().trim()
				+ "', '%Y-%m-%d')  AND DATE_FORMAT(tenderTime, '%Y-%m-%d')<=DATE_FORMAT('"
				+ costratio.getEndTime().trim() + "', '%Y-%m-%d') ";
		List<Loanrecord> list = dao.findBySql(sql, Loanrecord.class, userId);
		if (list.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取个人红包列表
	 * @param page
	 * @param id
	 * @return
	 */
	public PageModel getRedEnvelopeList(PageModel page, Long id) {
		StringBuffer sql = new StringBuffer(
				"select id,money,beginTime,endTime,lowestUseMoney,"
				+ "case when (date_format(now(),'%Y-%m-%d') > date_format(endTime,'%Y-%m-%d') and useFlag = 0) then '3' when useFlag = 0 then '0' when useFlag = 1 then '1' when useFlag = 2 then '2' end as i"
				+ ",consumeTime,sourceType from redenvelopedetail where userId=? " 
						+ " order by i,endTime,money desc");
		String sqlCount = "select count(1) from redenvelopedetail where userId=? ";
		page.setTotalCount(dao.queryNumberSql(sqlCount, id).intValue()); // 设置总记录数
		sql.append(" limit " + page.getNumPerPage() * (page.getPageNum() - 1))
				.append(",").append(page.getNumPerPage());
		List list = dao.findBySql(sql.toString(), id);
		page.setList(list);
		return page;
	}
	
	/**
	 * 获取当前用户且失效日期最近、金额最大的红包金额且红包门槛金额应该 <= 优先投资额
	 * @param id
	 * @return
	 */
	public String getRedEnveByCon(Long id,Double priority) {
		String sql = "select id,money from redenvelopedetail where userId=? and lowestUseMoney <= ? and date_format(beginTime,'%Y-%m-%d') <= date_format(now(),'%Y-%m-%d') and "
				+ "date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and useFlag = 0 order by money desc,endTime,id limit 0,1";
		Object obj = dao.findObjectBySql(sql,id.toString(),priority);
		String str = "";
		if (obj != null) {
			Object[] strs = (Object[]) obj;
			str = strs[0] + "," + MoneyUtil.toFixedBothBit(Double.valueOf(strs[1].toString()));
		}
		return str;
	}
	
	/**
	 * 获取当前用户且失效日期最近、金额最大的加息券且门槛金额应该 <= 优先投资额
	 * @param id
	 * @return
	 */
	public String getInterestByUser(Long id,Double priority) {
		String sql = "select id,interestRate*100 from interestincreasecard where userId=? and lowestUseMoney <= ? and date_format(beginTime,'%Y-%m-%d') <= date_format(now(),'%Y-%m-%d') and "
				+ "date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and useFlag = 0 order by interestRate desc,endTime,id limit 0,1";
		Object obj = dao.findObjectBySql(sql,id.toString(),priority);
		String str = "";
		if (obj != null) {
			Object[] strs = (Object[]) obj;
			str = strs[0] + "," + Double.valueOf(strs[1].toString()) + "%";
		}
		return str;
	}
	
	/***
	 * 修改红包
	 * @param redEnvelopeDetails
	 */
	public void updateRedEnvelopeDetails(RedEnvelopeDetail redEnvelopeDetail){
		System.out.println(redEnvelopeDetail);
		dao.update(redEnvelopeDetail);
	}

	/***
	 * 更新奖励红包
	 * @param loanId
	 */
	public  void listRedEnvelopeDetails(Long loanId){
		String sql="select * from redenvelopedetail where loanrecord_id in (select id from loanrecord  where subType=1 and redEnvelopeMoney!=0  and isSucceed=1 and loanSign_id=?)";
		List<RedEnvelopeDetail> list=dao.findBySql(sql, RedEnvelopeDetail.class, loanId);
		for (int i = 0; i < list.size(); i++) {
			RedEnvelopeDetail redEnvelopeDetail=list.get(i);
			redEnvelopeDetail.setUseFlag(Constant.STATUES_ZERO);
			redEnvelopeDetail.setConsumeTime("");
			redEnvelopeDetail.setLoanrecord_id(null);
			dao.update(redEnvelopeDetail);
		}
	}
	
	/***
	 * 更新奖励红包
	 * @param loanrecordId
	 */
	public  void updateRed(Loanrecord loanrecord,Integer statues){
		String sql="select * from redenvelopedetail where loanrecord_id =?";
		List<RedEnvelopeDetail> list=dao.findBySql(sql, RedEnvelopeDetail.class, loanrecord.getId());
		if(list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				RedEnvelopeDetail redEnvelopeDetail=list.get(i);
				redEnvelopeDetail.setUseFlag(statues);
				if(statues==1){
					redEnvelopeDetail.setLoanrecord_id(loanrecord.getId());
					redEnvelopeDetail.setConsumeTime(loanrecord.getTenderTime());
				}else{
					redEnvelopeDetail.setLoanrecord_id(null);
				}
				dao.update(redEnvelopeDetail);
			}
		}
	}
	
	/***
	 * lkl-20150825-添加开始时间判断
	 * 判断购买时间是否符合
	 * @param id
	 * @return
	 */
	public RedEnvelopeDetail getRedEnvelope(Long id,Userbasicsinfo user){
		String sql="select * from  redenvelopedetail  where id=? and userId=? and  date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and date_format(beginTime,'%Y-%m-%d')<=date_format(now(),'%Y-%m-%d') and useFlag = 0";
		List<RedEnvelopeDetail> reList=dao.findBySql(sql, RedEnvelopeDetail.class, id,user.getId());
		if(reList.size()>0){
			return reList.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 更换红包列表
	 * @param page
	 * @param id
	 * @return
	 */
	public PageModel changeReList(PageModel page, Long id, Double priority) {
		StringBuffer sql = new StringBuffer(
				"select id,money,beginTime,endTime,lowestUseMoney "
				+ " from redenvelopedetail where userId=? and lowestUseMoney <= ? and "
				+ " date_format(beginTime,'%Y-%m-%d') <= date_format(now(),'%Y-%m-%d') and "
				+ " date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and useFlag = 0 "
				+ " order by money desc,endTime,id");
		String sqlCount = "select count(1) from redenvelopedetail where userId=? and lowestUseMoney <= ? and date_format(beginTime,'%Y-%m-%d') <= date_format(now(),'%Y-%m-%d') and date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and useFlag = 0";
		try {
			page.setTotalCount(dao.queryNumberSql(sqlCount, id, priority).intValue()); // 设置总记录数
			sql.append(" limit " + page.getNumPerPage() * (page.getPageNum() - 1))
					.append(",").append(page.getNumPerPage());
			List list = dao.findBySql(sql.toString(), id, priority);
			page.setList(list);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("红包或者加息券列表查询出现异常："+e.getMessage());
		}
		return page;
	}

	/**
	 * H5更换红包列表
	 * @param page
	 * @param id
	 * @return
	 */
	public List changeReList(Long id, Double priority) {
		StringBuffer sql = new StringBuffer(
				"select id,money,beginTime,endTime,lowestUseMoney "
				+ " from redenvelopedetail where userId=? and lowestUseMoney <= ? and "
				+ " date_format(beginTime,'%Y-%m-%d') <= date_format(now(),'%Y-%m-%d') and "
				+ " date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and useFlag = 0 "
				+ " order by money desc,endTime,id");
		return dao.findBySql(sql.toString(), id, priority);
	}
	
	/**
	 * 加息券列表
	 * @param page
	 * @param id
	 * @param priority 
	 * @return
	 */
	public PageModel interestList(PageModel page, Long id, Double priority) {
		StringBuffer sql = new StringBuffer(
				"select id,interestRate,beginTime,endTime,lowestUseMoney "
				+ " from interestincreasecard where userId=? and lowestUseMoney <= ? and "
				+ " date_format(beginTime,'%Y-%m-%d') <= date_format(now(),'%Y-%m-%d') and "
				+ " date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and useFlag = 0 "
				+ " order by interestRate desc,endTime,id");
		String sqlCount = "select count(1) from interestincreasecard where userId=? and lowestUseMoney <= ? and date_format(beginTime,'%Y-%m-%d') <= date_format(now(),'%Y-%m-%d') and date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and useFlag = 0";
		page.setTotalCount(dao.queryNumberSql(sqlCount, id,priority).intValue()); // 设置总记录数
		sql.append(" limit " + page.getNumPerPage() * (page.getPageNum() - 1))
				.append(",").append(page.getNumPerPage());
		List list = dao.findBySql(sql.toString(), id,priority);
		page.setList(list);
		return page;
	}
	
	/**
	 * H5加息券列表
	 * @param page
	 * @param id
	 * @param priority 
	 * @return
	 */
	public List interestList(Long id, Double priority) {
		StringBuffer sql = new StringBuffer(
				"select id,interestRate,beginTime,endTime,lowestUseMoney "
				+ " from interestincreasecard where userId=? and lowestUseMoney <= ? and "
				+ " date_format(beginTime,'%Y-%m-%d') <= date_format(now(),'%Y-%m-%d') and "
				+ " date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and useFlag = 0 "
				+ " order by interestRate desc,endTime,id");
		return dao.findBySql(sql.toString(), id,priority);
	}
	
	/***
	 * 直接执行sql
	 * @param detail
	 */
	public  void uptRedEnvelope(RedEnvelopeDetail detail){
		 if(detail.getUseFlag()==2){
			 String sql="update redenvelopedetail set useFlag="+detail.getUseFlag()+" ,loanrecord_id="+detail.getLoanrecord_id()+" where id="+detail.getId();
			 dao.executeSql(sql);
		 }else{
			 String sql="update redenvelopedetail set useFlag="+detail.getUseFlag()+" ,consumeTime='"+detail.getConsumeTime().trim()+"' where id="+detail.getId();
			 dao.executeSql(sql);
		 }
	}
	
	/**
	 * 判断用户是否为首次投资
	 * @param user_id 用户ID
	 * @return true：是首次投资 false：不是首次投资
	 */
	public boolean isFirstInvest(Long user_id) {
		String sql = "select count(1) from loanrecord where isSucceed = 1 and userbasicinfo_id = ?";
		Object obj = dao.findObjectBySql(sql,user_id);
		Integer count = obj != null ? Integer.valueOf(obj.toString()) : 0;
		return count > 0 ? false : true;
	}
	
	/**
	 * 查询用户是否存在可使用的红包信息
	 * @param user_id 用户ID
	 * @return true：是首次投资 false：不是首次投资
	 */
	public Integer isExistRedEnvelope(Long user_id,Double proMoney) {
		String sql = "select count(1) from redenvelopedetail where userId = ? and lowestUseMoney <= ? and date_format(endTime,'%Y-%m-%d') >= date_format(now(),'%Y-%m-%d') and useFlag = 0 ";
		Object obj = dao.findObjectBySql(sql,user_id,proMoney);
		Integer count = obj != null ? Integer.valueOf(obj.toString()) : 0;
		return count;
	}
	
	/**
	 * 获取加息券列表
	 * @param page
	 * @param id
	 * @return
	 */
	public PageModel getCouponList(PageModel page, Long id) {
		StringBuffer sql = new StringBuffer(
				"select id,interestRate,beginTime,endTime,lowestUseMoney,"
				+ "case when (date_format(now(),'%Y-%m-%d') > date_format(endTime,'%Y-%m-%d') and useFlag = 0) then '3' when useFlag = 0 then '0' when useFlag = 1 then '1' when useFlag = 2 then '2' end as i"
				+ ",consumeTime,sourceType from interestincreasecard where userId=? " 
						+ " order by i,endTime,interestRate desc");
		String sqlCount = "select count(1) from interestincreasecard where userId=? ";
		page.setTotalCount(dao.queryNumberSql(sqlCount, id).intValue()); // 设置总记录数
		sql.append(" limit " + page.getNumPerPage() * (page.getPageNum() - 1))
				.append(",").append(page.getNumPerPage());
		List list = dao.findBySql(sql.toString(), id);
		page.setList(list);
		return page;
	}
	
	/**
	 * 获取嗒嗒巴士数据列表
	 * @param page
	 * @param id
	 * @return
	 */
	public PageModel getTapBusList(PageModel page, Long id,String status) {
		StringBuffer sql = new StringBuffer("select ");
		sql.append("money,date_format(startTime,'%Y-%m-%d'),date_format(endTime,'%Y-%m-%d'),case when date_format(now(),'%Y-%m-%d') >= date_format(startTime,'%Y-%m-%d') and date_format(now(),'%Y-%m-%d') <= date_format(endTime,'%Y-%m-%d') then '1' else '0' end as st ");
		sql.append(",couponCode from dadabuscashcertificate where userId=? and status=3");
		StringBuffer sqlCount = new StringBuffer("select count(1) from dadabuscashcertificate where userId=? and status=3");
		if ("0".equals(status)) { // 已过期
			sql.append(" and date_format(now(),'%Y-%m-%d') > date_format(endTime,'%Y-%m-%d')");
			sqlCount.append(" and date_format(now(),'%Y-%m-%d') > date_format(endTime,'%Y-%m-%d')");
		} else {   // 可使用
			sql.append(" and date_format(now(),'%Y-%m-%d') >= date_format(startTime,'%Y-%m-%d') and date_format(now(),'%Y-%m-%d') <= date_format(endTime,'%Y-%m-%d')");
			sqlCount.append(" and date_format(now(),'%Y-%m-%d') >= date_format(startTime,'%Y-%m-%d') and date_format(now(),'%Y-%m-%d') <= date_format(endTime,'%Y-%m-%d')");
		}
		page.setTotalCount(dao.queryNumberSql(sqlCount.toString(), id).intValue()); // 设置总记录数
		sql.append(" order by st desc limit " + page.getNumPerPage() * (page.getPageNum() - 1))
				.append(",").append(page.getNumPerPage());
		List list = dao.findBySql(sql.toString(), id);
		page.setList(list);
		return page;
	}
	
	/**
	 * 获取嗒嗒巴士数据列表 - h5
	 * @param page
	 * @param id
	 * @return
	 */
	public PageModel getTapBusList(PageModel page, Long id) {
		StringBuffer sql = new StringBuffer("select ");
		sql.append("money,date_format(startTime,'%Y-%m-%d'),date_format(endTime,'%Y-%m-%d'),case when date_format(now(),'%Y-%m-%d') >= date_format(startTime,'%Y-%m-%d') and date_format(now(),'%Y-%m-%d') <= date_format(endTime,'%Y-%m-%d') then '1' else '0' end as st ");
		sql.append(",couponCode from dadabuscashcertificate where userId=? and status=3");
		String sqlCount = "select count(1) from dadabuscashcertificate where userId=? and status=3";
		page.setTotalCount(dao.queryNumberSql(sqlCount, id).intValue()); // 璁剧疆鎬昏褰曟暟
		sql.append(" order by st desc limit " + page.getNumPerPage() * (page.getPageNum() - 1))
				.append(",").append(page.getNumPerPage());
		List list = dao.findBySql(sql.toString(), id);
		page.setList(list);
		return page;
	}
	
	/**
	 * 获取嗒嗒巴士车票数据列表 - h5
	 * @param page
	 * @param id
	 * @return
	 */
	public List getBusHomeList(String line, int type) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ub.userName,df.ticketTime from dadafreeticket df,userbasicsinfo ub where df.userId=ub.id and df.receiveTime is not null and line = ? order by df.receiveTime desc ");
		if(type == 0){
			sql.append(" limit 0,4 ");
		}
		return dao.findBySql(sql.toString(), line);
	}
	
	/**
	 * 获取嗒嗒巴士车票数据列表 - h5
	 * @param page
	 * @param id
	 * @return
	 */
	public List getBusHomeDateList(String time, String timeArea) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ub.userName,df.status,df.serialNo ");
		sql.append(",(select d.minSerialNo from ");
		sql.append("(select MIN(serialNo) minSerialNo,DATE_FORMAT(df.createTime,'%Y-%m-%d') createTime,timeArea from dadafreeticket df where df.serialNo LIKE '%9'  GROUP BY DATE_FORMAT(df.createTime,'%Y-%m-%d'),timeArea) d ");
		sql.append("where d.createTime = DATE_FORMAT(df.createTime,'%Y-%m-%d') and d.timeArea = df.timeArea and d.minSerialNo = df.serialNo) ");
		sql.append(",(select d.maxSerialNo from ");
		sql.append("(select MAX(serialNo) maxSerialNo,DATE_FORMAT(df.createTime,'%Y-%m-%d') createTime,timeArea from dadafreeticket df where df.serialNo LIKE '%9'  GROUP BY DATE_FORMAT(df.createTime,'%Y-%m-%d'),timeArea) d ");
		sql.append("where d.createTime = DATE_FORMAT(df.createTime,'%Y-%m-%d') and d.timeArea = df.timeArea and d.maxSerialNo = df.serialNo) ");
		sql.append("from dadafreeticket df,userbasicsinfo ub where df.userId=ub.id and DATE_FORMAT(df.createTime,'%Y-%m-%d')=? and df.timeArea = ? order by df.receiveTime desc ");
		return dao.findBySql(sql.toString(), time, timeArea);
	}
	
	/**
	 * 注册送红包360元
	 * @param userinfo 用户
	 */
	public void add_redenvelope(Userbasicsinfo userinfo) {
		// 获取费用表的信息
		Costratio costratio = loanSignQuery.queryCostratio();
		boolean bool = getRedEnvelopeDetails(userinfo.getId(),costratio);
		Calendar c = Calendar.getInstance();
		boolean giveRedEnvFlag = false;
		// 添加红包信息
		List<RedEnvelopeDetail> redList= new ArrayList<RedEnvelopeDetail>();
		Date date = new Date();
		String time = DateUtil.format(date, "yyyy-MM-dd");
		// 判断活动时间   并且是启用状态
		if(bool && costratio.getRedState() == 1 && sign(userinfo, costratio)) {
			for (int i = 1; i <= 36; i++) {
				RedEnvelopeDetail red = new RedEnvelopeDetail();
				if (i <= 9) {   // 添加5元红包
					red.setMoney(5D);
					red.setLowestUseMoney(1000D);
				} else if (i > 9 && i <= 18) {   // 7元
					red.setMoney(7D);
					red.setLowestUseMoney(5000D);
				} else if (i > 18 && i <= 27) {   // 9元
					red.setMoney(9D);
					red.setLowestUseMoney(10000D);
				} else if (i > 27 && i <= 36) {   // 19元
					red.setMoney(19D);
					red.setLowestUseMoney(20000D);
				}
				red.setUserbasicsinfo(userinfo);
				red.setReceiveTime(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
				red.setUseFlag(0);       // 未使用
				red.setBeginTime(time);  // 开始时间
				c.setTime(date);
				c.add(Calendar.MONTH,6);
				red.setEndTime(DateUtil.format(c.getTime(), "yyyy-MM-dd"));   // 有效时间半年
				red.setSourceType(2);  // 表示注册送的红包
				redList.add(red); // 添加红包
			}
			
			giveRedEnvFlag = true;
		}
		
		/** 红包活动及8月份的活动 */
		if(giveRedEnvFlag || RedisUtil.isAugustActivity(date)) {
			// 3元红包
			RedEnvelopeDetail red_enve = new RedEnvelopeDetail();
			red_enve.setMoney(3D);
			red_enve.setLowestUseMoney(0D);
			red_enve.setReceiveTime(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
			red_enve.setBeginTime(time);
			red_enve.setUserbasicsinfo(userinfo);
			c.setTime(date);
			c.add(Calendar.MONTH,1);
			red_enve.setEndTime(DateUtil.format(c.getTime(), "yyyy-MM-dd"));   // 有效时间一个月
			red_enve.setSourceType(2);   // 表示注册送的红包
			red_enve.setUseFlag(0);  // 未使用
			redList.add(red_enve);
			registrationService.addRedPacket(redList);  // 保存
			
			if(giveRedEnvFlag) {//如果是红包活动期间则另送2次抽奖机会
				RedisUtil.increaseLotteryChance(userinfo.getId().intValue(), 2);   // 赠送转盘抽奖机会2次
			}
		}
	}
	
	/***
	 * lkl-20150827
	 * 注册送红包100元
	 * @param userinfo
	 */
	public void saveRedEnvelopeDetail(Userbasicsinfo userinfo){
		Calendar c = Calendar.getInstance();
		// 添加红包信息
		List<RedEnvelopeDetail> redList= new ArrayList<RedEnvelopeDetail>();
		Date date = new Date();
		String time = DateUtil.format(date, "yyyy-MM-dd");
		// 判断活动时间   并且是启用状态
		for (int i = 1; i <4; i++) {
			RedEnvelopeDetail red = new RedEnvelopeDetail();
			if (i==1) {   // 添加10元红包
				red.setMoney(10D);
				red.setLowestUseMoney(100D);
			} else if (i==2) {  
				red.setMoney(30D);
				red.setLowestUseMoney(3000D);
			} else if (i ==3) {  
				red.setMoney(60D);
				red.setLowestUseMoney(8000D);
			}
			red.setUserbasicsinfo(userinfo);
			red.setReceiveTime(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
			red.setUseFlag(0);       // 未使用
			red.setBeginTime(time);  // 开始时间
			c.setTime(date);
			c.add(Calendar.MONTH,2);
			red.setEndTime(DateUtil.format(c.getTime(), "yyyy-MM-dd"));   // 有效时间2个月
			red.setSourceType(2);  // 表示注册送的红包
			redList.add(red); // 添加红包
		}
		registrationService.addRedPacket(redList);  // 保存
	 }
}
