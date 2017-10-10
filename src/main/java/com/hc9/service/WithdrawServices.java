package com.hc9.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.pomo.web.page.model.Page;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.PayURL;
import com.hc9.common.util.Arith;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Costratio;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Withdraw;
import com.hc9.dao.entity.WithdrawApply;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
import com.hc9.model.WithdrawalInfo;
import com.ips.security.utility.IpsCrypto;

import freemarker.template.TemplateException;

/**
 * 提现业务处理
 * 
 * @author RanQiBing 2014-02-13
 *
 */
@Service
public class WithdrawServices {

	@Resource
	private HibernateSupport dao;
	
	@Resource
	private LoanSignQuery loanSignQuery;
	/**
	 * 查询用户所有的提现信息
	 * 
	 * @return 返回一个提现信息的集合
	 */
	public List<Withdraw> queryList(Page page, Long id) {
		String hql = "select w.id,w.withdrawAmount,w.strNum,w.pIpsBillNo,w.time from Withdraw w where w.userbasicsinfo.id=?";
		List<Withdraw> list = dao.pageListByHql(page, hql, false, id);
		return list;
	}

	/**
	 * 添加用户提现信息
	 * 
	 * @param withdraw
	 *            用户提现信息
	 */
	public void save(Withdraw withdraw) {
		dao.save(withdraw);
	}

	/**
	 * 保存提现申请
	 * 
	 * @param apply
	 */
	public void saveWithdrawApply(WithdrawApply apply) {
		dao.save(apply);
	}

	/**
	 * 查询是否存在待确认的提现记录
	 * 
	 * @param user_id
	 * @return bool
	 */
	public boolean isExists(Long user_id) {
		return Integer
				.valueOf(dao
						.findObjectBySql(
								"select count(1) from withdraw where user_id=? and state in (0,2,5)",
								user_id).toString()) > 0 ? true : false;
	}

	/**
	 * 对信息进行加密处理
	 * 
	 * @param widthdrawal
	 *            提现信息
	 * @return 返回加密后的提现信息
	 * @throws TemplateException
	 * @throws IOException
	 */
	public Map<String, String> encryption(WithdrawalInfo widthdrawal)
			throws IOException, TemplateException {
		// 将充值信息转换成xml文件
		String registerCall = ParseXML.withdrawalXml(widthdrawal);
		System.out.println(registerCall);
		// 加密后的信息
		Map<String, String> map = registerCall(registerCall);
		map.put("url", PayURL.WITHDRAWALTESTURL);

		return map;
	}

	public static Map<String, String> registerCall(String registerCall) {
		// 生成xml文件字符串
		// String = ParseXML.registration(entity);
		// 将生成的xml文件进行3des加密
		String desede = IpsCrypto
				.triDesEncrypt(registerCall, ParameterIps.getDes_algorithm(),
						ParameterIps.getDesedevector());
		// 将加密后的字符串不换行
		desede = desede.replaceAll("\r\n", "");
		// 将“ 平台 ”账号 、用户注册信息、证书拼接成一个字符串
		StringBuffer argSign = new StringBuffer(ParameterIps.getCert()).append(
				desede).append(ParameterIps.getMd5ccertificate());
		// 将argSign进行MD5加密
		String md5 = IpsCrypto.md5Sign(argSign.toString());
		// 将参数装进map里面
		Map<String, String> map = new HashMap<String, String>();
		map.put("pMerCode", ParameterIps.getCert());
		map.put("p3DesXmlPara", desede);
		map.put("pSign", md5);
		return map;
	}

	/**
	 * 根据ips提现编号查询提现信息
	 * 
	 * @param ipsNo
	 * @return 返回提现对象
	 */
	public Withdraw withdrawIps(String ipsNo) {
		String hql = "from Withdraw w where w.pIpsBillNo=?";
		List<Withdraw> with = dao.find(hql, ipsNo);
		if (with.size() > 0) {
			return with.get(0);
		}
		return null;
	}

	/**
	 * 用户的提现信息
	 * 
	 * @param id
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public List<Withdraw> withdrawList(Long id, String beginTime,
			String endTime, Integer search, PageModel page) {

		StringBuffer sql = new StringBuffer(
				"SELECT * FROM withdraw w where w.user_id=" + id);
		StringBuffer sqlCount = new StringBuffer(
				"SELECT count(1) FROM withdraw w where w.user_id=" + id);

		if (StringUtil.isNotBlank(beginTime)) { // 开始时间
			sql.append(" and date_format(w.time,'%Y-%m-%d')>='").append(
					beginTime + "'");
			sqlCount.append(" and date_format(w.time,'%Y-%m-%d')>='")
					.append(beginTime + "'");
		}
		if (StringUtil.isNotBlank(endTime)) { // 结束时间
			sql.append(" and date_format(w.time,'%Y-%m-%d')<='").append(
					endTime + "'");
			sqlCount.append(" and date_format(w.time,'%Y-%m-%d')<='")
					.append(endTime + "'");
		}
		if (search != null && !"".equals(search)) { // 最近几个月
			sql.append(" and DATE_SUB(now(),INT	ERVAL " + search + " MONTH) <= w.time");
			sqlCount.append(" and DATE_SUB(now(),INTERVAL " + search + " MONTH) <= w.time");
		}
		page.setTotalCount(dao.queryNumberSql(sqlCount.toString()).intValue()); // 设置总记录数
		sql.append(" order by w.id desc LIMIT " + (page.getPageNum() - 1)
				* page.getNumPerPage() + "," + page.getNumPerPage());
		List<Withdraw> list = dao.findBySql(sql.toString(), Withdraw.class);
		page.setList(list);
		return list;
	}

	/**
	 * 根据id和时间查询提现记录的总条数
	 * 
	 * @param id
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public Object count(Long id, String beginTime, String endTime) {
		StringBuffer hql = new StringBuffer(
				"SELECT COUNT(w.id) FROM withdraw w WHERE w.user_id=?");
		if (null != beginTime && !"".equals(beginTime)) {
			hql.append(" and date_format(w.applytime,'%Y-%m-%d')>='").append(
					beginTime + "'");
		}

		if (null != endTime && !"".equals(endTime)) {
			hql.append(" and date_format(w.applytime,'%Y-%m-%d')<='").append(
					endTime + "'");
		}
		Object obj = dao.findObjectBySql(hql.toString(), id);
		return obj;
	}

	/**
	 * 查询apply表
	 * 
	 * @param u
	 *            用户
	 */
	public List<WithdrawApply> queryWithdrawApp(Userbasicsinfo u) {
		String hql = "from WithdrawApply w where w.userbasicsinfo.id="
				+ u.getId() + " order by apply_time desc";
		List<WithdrawApply> applys = (List<WithdrawApply>) dao
				.query(hql, false);
		return applys;
	}

	/**
	 * 查询apply表
	 * 
	 * @param u
	 *            用户
	 * @param page
	 *            分页对象
	 */
	public List<WithdrawApply> queryWithdrawApplyPage(Userbasicsinfo u,
			PageModel page) {
		String hql = "FROM WithdrawApply w where w.userbasicsinfo.id="
				+ u.getId() + " order by apply_time desc";
		return (List<WithdrawApply>) dao.pageListByHql(page, hql, true);
	}

	/**
	 * 查询apply表
	 * 
	 * @param id
	 *            applyId
	 * @return
	 */
	public List<WithdrawApply> queryWithdrawApply(String id) {
		String hql = "from WithdrawApply w where w.id=" + id
				+ " order by apply_time desc";
		List<WithdrawApply> applys = (List<WithdrawApply>) dao
				.query(hql, false);
		return applys;
	}

	/**
	 * 查询apply表
	 * 
	 * @return
	 */
	public List<WithdrawApply> listAllApply() {
		String hql = "from WithdrawApply";
		List<WithdrawApply> applies = dao.find(hql);
		return applies;
	}

	/***
	 * 更新提现信息
	 * 
	 * @param withdraw
	 */
	public void uptWithdraw(Withdraw withdraw) {
		dao.update(withdraw);
	}

	/***
	 * 更新提现申请信息
	 * 
	 * @param withdrawApply
	 */
	public void uptWithdrawApply(WithdrawApply withdrawApply) {
		dao.update(withdrawApply);
	}

	/***
	 * 根据id查询提现信息
	 * 
	 * @param wId
	 * @return
	 */
	public Withdraw selWithdraw(String wId) {
		String sql = "select * from withdraw where id=?";
		Withdraw withdraw = dao.findObjectBySql(sql, Withdraw.class, wId);
		return withdraw;
	}

	/** 修改提现券的状态为待确认
	 * 	@param userId 用户id
	 *  @param ordernum 订单号
	 *  @param useFlag 是否使用：0、未使用，1、已使用；2、待确认；默认为0
	 *  */
	public void updateWithdrawCardStatusAfterSuccess(Long userId, String ordernum) {
		String sql = "update withdrawcard set useFlag=1 where userId=? and useOrderNo=? and useFlag=2";
		dao.executeSql(sql, userId, ordernum);
	}
	
	/** 修改提现券的状态为待确认
	 * 	@param userId 用户id
	 *  @param ordernum 订单号
	 *  @param useFlag 是否使用：0、未使用，1、已使用；2、待确认；默认为0
	 *  */
	public void updateWithdrawCardStatusAfterFailure(Long userId, String ordernum) {
		String sql = "update withdrawcard set useFlag=0,consumeTime='',useOrderNo='' where userId=? and useOrderNo=? and useFlag=2";
		dao.executeSql(sql, userId, ordernum);
	}
	
	/***
	 * 是否计算提现手续费 用户充值金额-用户累积投资金额>0收，<0则不收
	 * 
	 * @param userID
	 * @return
	 */
	public Double selWithdrawMoney(String userID, String timeDate) {
		Double result = 0.0;
		if (StringUtil.isNotBlank(userID)) {
			/** 计算用户充值金额 */
			String sql = "select IFNULL(SUM(income),0) from accountinfo " 
					+ "where accounttype_id=6 and userbasic_id=? and " 
					+ "DATE_FORMAT(time, '%Y-%m-%d')>=DATE_FORMAT(?, '%Y-%m-%d') ";
			Object rechargeObject = dao.findObjectBySql(sql, userID, timeDate);
			double rechargeMoney = Double.valueOf(rechargeObject.toString());
			
			/**  计算用户累计投资金额 */
			sql = "select sum(IFNULL(tendermoney,0)) from loanrecord " 
					+ "where issucceed=1 and userbasicinfo_id=? and " 
					+ "DATE_FORMAT(tenderTime, '%Y-%m-%d')>=DATE_FORMAT(?, '%Y-%m-%d') ";
			Object investObject = dao.findObjectBySql(sql, userID, timeDate);
			double investMoney = 0.0;
			if(investObject!=null){
				investMoney = Double.valueOf(investObject.toString());
			}
			result = Arith.round(Arith.sub(rechargeMoney, investMoney), 2);
		}
		return result;
	}

	/***
	 * 查询借款人是否收取服务费
	 * 
	 * @param userId
	 * @return
	 */
	public int getFeeMoney(Long userId) {
		StringBuffer sb = new StringBuffer(
				"select count(id) from loansign where feeState=1 and userbasicinfo_id =")
				.append(userId);
		Object obje = dao.findObjectBySql(sb.toString(), null);
		return obje != null ? Integer.parseInt(obje.toString()) : 0;
	}

	/**
	 * 查询会员成功提现商户收取的手续费总和
	 * @param uid 用户id
	 * @return
	 */
	public Double getTotalMerFee(Long uid) {
		String sql="SELECT SUM(w.mer_fee) FROM withdraw w WHERE w.user_id=? AND w.state=1";
		Object totalMerFee = dao.findObjectBySql(sql, uid);
		if(totalMerFee != null){
			return Double.parseDouble(totalMerFee.toString());
		}else{
			return 0.0;
		}
	}
	/**
	 * 收取的手续费
	 * 如果客户充值-投资差额-平台已收手续费>0,手续费计算:提现金*比例.替换原先的站岗金额*比例
	 * @param user 
	 * @param money 提现金额
	 * @return result 手续费
	 */
	public double takeWithdrawFee(Userbasicsinfo user,double money){
		double result=0;
		Costratio costratio = loanSignQuery.queryCostratio();
		//商户 已扣除手续费相对应的金额
		Double tookFee = queryRelativeMoneyOfFee(user.getId());
		
		//充值-投资差额-平台已收手续费>0? 收：不收
		Double difference = selWithdrawMoney(user.getId().toString(), costratio.getTimeDate());
		difference = Arith.sub(difference, tookFee);//目标金额
		if(user.getIsCreditor() == 2) {  //借款人
			int count = getFeeMoney(user.getId());
			if(count < 1) {
				result = computeChargeFee(difference, money, costratio.getWithdrawRate());
			}
		} else {
			result = computeChargeFee(difference, money, costratio.getWithdrawRate());
		}
		return result;
	}
	
	/**
	 * 查询会员 已扣除手续费相对应的金额
	 * @param uid 用户id
	 * @return
	 */
	public Double queryRelativeMoneyOfFee(Long uid) {
		String sql="select SUM(IFNULL(amount,0)) from withdraw WHERE user_id=? AND state=1 and mer_fee>0";
		Object totalMerFee = dao.findObjectBySql(sql, uid);
		if(totalMerFee != null){
			return Double.parseDouble(totalMerFee.toString());
		}else{
			return 0.0;
		}
	}
	
	/** 
	 *  @param difference 目标金额：累计充值-累计认购-已扣除手续费相对应的金额
	 *  @param withDrawMoney 本次提现的金额 
	 *  @param withdrawRate 提现手续费
	 *  手续费收取逻辑如下：
	 *  1、目标金额大于0，则收取手续费；目标金额小于等于0，则不收取手续费。
	 *  1、当提现金额大于等于A，本次收取手续费金额为：A*1%+2元
	 *  2、当提现金额小于A，本次收取手续费金额为：提现金额*1%+2元 */
	private double computeChargeFee(Double difference, Double withDrawMoney, Double withdrawRate) {
		double result = 0;
		if(difference > 0) {
			if(withDrawMoney >= difference) {
				result = Arith.round(Arith.mul(difference, withdrawRate),2);
			} else {
				result = Arith.round(Arith.mul(withDrawMoney, withdrawRate),2);
			}
		}
		return result;
	}
}