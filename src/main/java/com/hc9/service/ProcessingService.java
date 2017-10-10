package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.Loansign;
import com.hc9.dao.entity.Recharge;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Shop;
import com.hc9.dao.entity.ShopRecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Withdraw;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.BalanceQueryInfo;
import com.hc9.model.ExpensesInfo;
import com.hc9.model.RegisterInfo;
import com.hc9.model.RepaymentAsyn;
import com.hc9.model.RepaymentInfo;
import com.hc9.model.RepaymentSignReturn;
import com.hc9.model.WithdrawalInfo;

/**
 * 支付接口返回信息的业务处理
 * 
 * @author frank 2015-01-3
 * 
 */
@Service
public class ProcessingService {
	/**
	 * 通用方法
	 */
	@Resource
	private HibernateSupport dao;

	/**
	 * 银行信息
	 */
	@Resource
	private RechargesService rechargeService;

	@Resource
	private LoanManageService loanManageService;

	@Resource
	private UserInfoServices userInfoServices;

	/**
	 * 宝付充值记录 userid与orderid保存
	 * 
	 * @param obj
	 */
	public void rechargelogsave(Recharge recharge) {
		dao.save(recharge);
	}

	/**
	 * 宝付提现记录 userid与orderid保存
	 * 
	 * @param obj
	 */
	public void Forchargelogsave(Withdraw withdraw) {
		dao.save(withdraw);
	}

	public void Bidinfologsave(Loanrecord lc) {
		dao.save(lc);
	}

	public void Bidinfologupdate(Loanrecord lc) {
		dao.update(lc);
	}

	/**
	 * 宝付充值记录orderid
	 * 
	 * @param obj
	 */
	public Recharge findRechargeByOrderId(String orderid) {
		StringBuffer sb = new StringBuffer(
				"SELECT * FROM recharge WHERE orderNum=?");
		return dao.findObjectBySql(sb.toString(), Recharge.class, orderid);
	}

	/**
	 * 宝付提现记录orderid
	 * 
	 * @param obj
	 */
	public Withdraw findForchargebyorderid(String orderid) {
		StringBuffer sb = new StringBuffer(
				"SELECT * FROM withdraw WHERE strNum=?");
		return dao.findObjectBySql(sb.toString(), Withdraw.class, orderid);
	}

	/**
	 * 充值记录，用orderid查询userid
	 * 
	 * @param orderid
	 * @return
	 */
	/*
	 * public Object finduseridbyorderid(String orderid){ StringBuffer sb=new
	 * StringBuffer("SELECT user_id FROM recharge WHERE orderNum=?"); return
	 * dao.findObjectBySql(sb.toString(), orderid); }
	 */

	/**
	 * 投标记录，用orderid查询userid
	 * 
	 * @param orderid
	 * @return
	 */
	public Loanrecord findBiduseridbyorderid(String orderid) {
		StringBuffer sb = new StringBuffer(
				"SELECT * FROM loanrecord WHERE order_id=?");
		return dao.findObjectBySql(sb.toString(), Loanrecord.class, orderid);
	}

	/**
	 * 查询流水账表
	 * 
	 * @param ips
	 *            ips唯一流水号
	 * @return 条数
	 */
	public Integer accountInfoNum(String ips) {
		String sql = "select count(id) from accountinfo a where a.ipsNumber=?";
		Object obj = dao.findObjectBySql(sql, ips);
		return Integer.parseInt(obj.toString());
	}

	/**
	 * 查询还款是否生成有还款记录
	 * 
	 * @param loanid
	 *            标号
	 * @return 条数
	 */
	public Integer repaymentNum(Long loanid) {
		String sql = "select count(id) from repaymentrecord a where a.loanSign_id=?";
		Object obj = dao.findObjectBySql(sql, loanid);
		return Integer.parseInt(obj.toString());
	}

	/**
	 * 修改标当前期的还款状态
	 * 
	 * @return
	 */
	public synchronized int updateRayment(Repaymentrecord repaymentrecord) {
		try {
			dao.update(repaymentrecord);
			return Constant.STATUES_ZERO;
		} catch (Exception e) {
			return Constant.STATUES_ONE;
		}
	}

	// public int getrepaymet
	/**
	 * 修改标的状态
	 * 
	 * @param loan
	 * @return
	 */
	public synchronized int updateLoan(Loansign loan) {
		try {
			dao.update(loan);
			return Constant.STATUES_ZERO;
		} catch (Exception e) {
			return Constant.STATUES_ONE;
		}
	}

	/**
	 * 用户注册
	 * 
	 * @param registerInfo
	 *            用户注册信息
	 * @param userbasics
	 *            用户基本信息
	 * @return <p>
	 *         true
	 *         </p>
	 *         成功
	 *         <p>
	 *         false
	 *         </p>
	 *         失败
	 */
	public Boolean registration(RegisterInfo registerInfo,
			Userbasicsinfo userbasics) {
		/** 调动存储过程 **/
		/*
		 * boolean bool = dao.callProcedureVoid(
		 * Enums.PROCEDURES.PROCEDURE_REGISTRATION_UPDATE.toString(),
		 * userbasics.getId(), registerInfo.getpIpsAcctDate(),
		 * registerInfo.getpMerBillNo(), registerInfo.getpIpsAcctNo()); return
		 * bool;
		 */
		return false;
	}

	/**
	 * 更新充值记录
	 * 
	 * @param recharge
	 * @return
	 */
	public Boolean updaterecharge(Recharge recharge) {
		dao.update(recharge);
		return false;
	}



	/**
	 * 提现返回信息异步处理
	 * 
	 * @param withdrawalInfo
	 *            提现信息
	 * @param userbasicsinfo
	 *            用户信息
	 * @return <p>
	 *         true
	 *         </p>
	 *         成功
	 *         <p>
	 *         false
	 *         </p>
	 *         失败
	 */
	public Boolean withdrlRecord(WithdrawalInfo withdrawalInfo,
			Userbasicsinfo userbasicsinfo) {
		// 获取当前用户账户余额
		BalanceQueryInfo money = RegisterService
				.accountBalance(userbasicsinfo.getUserfundinfo().getpIdentNo());
		/*
		 * Boolean bool = dao.callProcedureVoid(
		 * Enums.PROCEDURES.PROCEDURE_WITHDRAWAL_MONEY.toString(),
		 * userbasicsinfo.getId(), withdrawalInfo.getpTrdAmt(),
		 * withdrawalInfo.getpDwDate(), money.getpBalance(),
		 * withdrawalInfo.getpIpsBillNo()); return bool;
		 */
		return false;
	}

	/**
	 * 提现返回信息同步处理
	 * 
	 * @param withdrawalInfo
	 *            提现信息
	 * @param userbasicsinfo
	 *            用户信息
	 * @return <p>
	 *         true
	 *         </p>
	 *         成功
	 *         <p>
	 *         false
	 *         </p>
	 *         失败
	 */
	public Boolean withdrlwal(WithdrawalInfo withdrawalInfo,
			Userbasicsinfo userbasicsinfo) {

		/*
		 * Boolean bool = dao.callProcedureVoid(
		 * Enums.PROCEDURES.PROCEDURE_WITHDRAWAL_RECORD.toString(),
		 * userbasicsinfo.getId(), withdrawalInfo.getpTrdAmt(),
		 * withdrawalInfo.getpMerBillNo(), withdrawalInfo.getpIpsBillNo(),
		 * withdrawalInfo.getpDwDate()); return bool;
		 */
		return false;
	}




	/**
	 * 分红
	 * 
	 * @param userid
	 * @param loanid
	 * @param pIdentNo
	 * @param money
	 * @param interest
	 * @param penalty
	 * @param ips
	 * @param state
	 * @param management
	 * @param mymoney
	 * @return
	 */
	public Boolean shareMoney(Long userid, Double userMoney, Long loanid,
			Double smoney) {

		/*
		 * boolean bool = dao.callProcedureVoid(
		 * Enums.PROCEDURES.SHARE_MONEY_BONUS.toString(), userid, userMoney,
		 * loanid, smoney); return bool;
		 */
		return false;
	}

	/**
	 * 获取所有未完成的净值标
	 * 
	 * @return 返回所有的净值标
	 */
	public List<Loansign> getLoan() {
		String hql = "from Loansign l where l.loansignType.id=? and l.loanstate!=?";
		List<Loansign> list = dao.find(hql,	Constant.STATUES_FOUR,	Constant.STATUES_FOUR);
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	/**
	 * 环迅还款数据返回信息处理
	 * 
	 * @param repayment
	 * @return
	 */
	public void getrepayment(RepaymentInfo repayment,
			Repaymentrecord repaymentrecord, ExpensesInfo expensesInfo) {
		/*
		 * this.repaymentMoney(expensesInfo.getUserId(),
		 * expensesInfo.getLoanid(), expensesInfo.getIpsNumber(),
		 * -expensesInfo.getMoney(), -expensesInfo.getInterest(),
		 * -expensesInfo.getPenalty(), repayment.getpIpsBillNo(),
		 * expensesInfo.getIsLoanState(), 0.00); // 得到投资人还款的所有信息
		 * List<ExpensesInfo> expensList = loanManageService
		 * .investorInteest(repaymentrecord); for (int i = 0; i <
		 * repayment.getRepaymentInvestorInfoList().size(); i++) {
		 * RepaymentInvestorInfo reInfo = repayment
		 * .getRepaymentInvestorInfoList().get(i); for (int j = 0; j <
		 * expensList.size(); j++) { ExpensesInfo info = expensList.get(j); if
		 * (reInfo.getpTIpsAcctNo().equals(info.getIpsNumber())) { if
		 * (reInfo.getpStatus().equals("Y")) { Boolean bool =
		 * this.repaymentMoney(info.getUserId(),
		 * repaymentrecord.getLoansign().getId(), reInfo.getpTIpsAcctNo(),
		 * info.getMoney(), info.getInterest(), info.getPenalty(),
		 * repayment.getpIpsBillNo(), info.getIsLoanState(),
		 * info.getManagement()); break; } } } }
		 */
	}

	/**
	 * 环迅还款数据返回信息处理 IPS2.0接口 2014-8-28
	 * 
	 * @param repayment
	 * @param repaymentrecord
	 * @param expensesInfo
	 */
	public void getRepayment(RepaymentAsyn repayment,
			Repaymentrecord repaymentrecord, ExpensesInfo expensesInfo) {
		/*
		 * this.repaymentMoney(expensesInfo.getUserId(),
		 * expensesInfo.getLoanid(), expensesInfo.getIpsNumber(),
		 * -expensesInfo.getMoney(), -expensesInfo.getInterest(),
		 * -expensesInfo.getPenalty(), repayment.getpIpsBillNo(),
		 * expensesInfo.getIsLoanState(), 0.00); // 得到投资人还款的所有信息
		 * List<ExpensesInfo> expensList = loanManageService
		 * .investorInteest(repaymentrecord); List<RepaymentInvestor> investors
		 * = repayment.getInvestors(); for (RepaymentInvestor reInfo :
		 * investors) { for (int j = 0; j < expensList.size(); j++) {
		 * ExpensesInfo info = expensList.get(j); if
		 * (reInfo.getpInAcctNo().equals(info.getIpsNumber())) { if
		 * (reInfo.getpStatus().equals("Y")) { Boolean bool =
		 * this.repaymentMoney(info.getUserId(),
		 * repaymentrecord.getLoansign().getId(), reInfo.getpInAcctNo(),
		 * info.getMoney(), info.getInterest(), info.getPenalty(),
		 * repayment.getpIpsBillNo(), info.getIsLoanState(),
		 * info.getManagement()); break; } } } }
		 */
		// return null;
	}



	/**
	 * 更新自动还款签约状态
	 * 
	 * @param repaySign
	 * @return
	 */
	public boolean updateRepaySignState(RepaymentSignReturn repaySign) {
		int updateCount = dao
				.executeSql(
						"UPDATE userbasicsinfo SET  repaySignStatus =1,repayAuthNo=? WHERE id=(SELECT id FROM userfundinfo WHERE userfundinfo.pIdentNo=?)",
						repaySign.getpIpsAuthNo(), repaySign.getpIpsAcctNo());
		if (updateCount == 0)
			return false;
		else
			return true;
	}

	/**
	 * 更新自动还款签约状态
	 * 
	 * @param repaySign
	 * @return
	 */
	public boolean isRepaySignState(RepaymentSignReturn repaySign) {
		int isRepaySign = dao
				.queryNumberSql(
						"SELECT  repaySignStatus FROM userbasicsinfo,userfundinfo WHERE userbasicsinfo.id=userfundinfo.id AND userfundinfo.pIdentNo=?",
						repaySign.getpIpsAcctNo()).intValue();
		if (isRepaySign == 0)
			return false;
		else
			return true;
	}
	
	/***
	 * 更新loanrecord表
	 * @param loanrecord
	 */
	public void updateLoanrecord(Loanrecord loanrecord){
		dao.update(loanrecord);
	}
	
	/***
	 * 更新shopRecord表
	 * @param shopRecord
	 */
	public void updateShopRecord(ShopRecord shopRecord){
		dao.update(shopRecord);
	}
	
	/***
	 * 更新店铺表
	 * @param shop
	 */
	public void updateShop(Shop shop){
		dao.update(shop);
	}
	
	
	//获取项目购标记录
	public List<Loanrecord> getLoanRecord(Long loansignId){
		if(loansignId!=null&&!loansignId.equals("")){
			String sql="select * from loanrecord where isSucceed=1 and loanSign_id ="+loansignId;
			List<Loanrecord> listLoanrecord=dao.findBySql(sql, Loanrecord.class);
			if(listLoanrecord.size()>0){
				return listLoanrecord;
			}
		}
		return null;
	}
	
	/***
	 * 获取同期项目购买总数
	 * @param contractNo
	 * @return
	 */
	public int getLoanRecordContractNoCount(Loansign loansign){
		if(loansign.getContractNo()!=null&&!loansign.getContractNo().equals("")){
			System.out.println(loansign.getContractNo().length());
			String sql="select * from loansign where contractNo like '"+loansign.getContractNo().substring(0, loansign.getContractNo().length()-1)+"%' and status>=6";
			List<Loansign> listLoansign=dao.findBySql(sql, Loansign.class);
			if(listLoansign.size()>1){
                  String sqlLoanRecordId="select  MAX(l.id) from loanrecord l LEFT JOIN loansign s on l.loanSign_id=s.id where s.`status`>=6  and l.pContractNo!='' and  s.contractNo like '"+loansign.getContractNo().substring(0, loansign.getContractNo().length()-1)+"%' ";
				  Object loanRecordId = dao.findObjectBySql(sqlLoanRecordId);
				  if(loanRecordId==null){
					  return 0;
				  }else{
					  Loanrecord loanrecord=dao.get(Loanrecord.class, Long.valueOf(loanRecordId.toString()));
				      return Integer.parseInt(loanrecord.getpContractNo().substring(10, loanrecord.getpContractNo().length()));
				  }
			}else{
				 return 0;
			}
		}else{
			 return 0;
		}
	}
	
	//获取店铺购买记录
	public List<ShopRecord> getShopRecord(Long shopId){
		if(shopId!=null&&!shopId.equals("")){
			String sql="select * from shop_record where isSucceed=1 and shop_id ="+shopId;
			List<ShopRecord> listShopRecords=dao.findBySql(sql, ShopRecord.class);
			if(listShopRecords.size()>0){
				return listShopRecords;
			}
		}
		return null;
	}
}
