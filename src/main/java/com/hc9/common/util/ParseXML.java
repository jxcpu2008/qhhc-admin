package com.hc9.common.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.hc9.dao.entity.UserBank;
import com.hc9.model.AcctTrans;
import com.hc9.model.AutomaticBidInfo;
import com.hc9.model.BalanceQueryInfo;
import com.hc9.model.BidAssignment;
import com.hc9.model.BidInfo;
import com.hc9.model.BidInfo4;
import com.hc9.model.CardInfo;
import com.hc9.model.P2pQuery;
import com.hc9.model.P2pUserInfoQuery;
import com.hc9.model.Payuser;
import com.hc9.model.RechargeInfo;
import com.hc9.model.RegisterInfo;
import com.hc9.model.RegisterSubject;
import com.hc9.model.Repayment;
import com.hc9.model.RepaymentSign;
import com.hc9.model.TenderAuditInfo;
import com.hc9.model.Transfer;
import com.hc9.model.WithdrawalInfo;

import freemarker.template.TemplateException;

/**
 * 
 * 生成xml文件
 * 
 * @author RanQiBing 2014-01-03
 * 
 */
/**
 * @author Administrator
 *
 */
public class ParseXML {
	/**
	 * 用户注册信息生成xml文件
	 * 
	 * @param register
	 *            用户信息
	 * @return 返回一个xml文件
	 * @throws TemplateException
	 * @throws IOException
	 */
	public static String registration(RegisterInfo register,String param)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("bf_account", register.getBf_account().toString());
		map.put("name", register.getName());
		map.put("id_card", register.getId_card().toString());
		map.put("user_id", register.getUser_id());
		map.put("return_url", register.getReturn_url()+"?param="+param);
		map.put("page_url", register.getPage_url());
		String registrationxml = FreeMarkerUtil.execute(
				"config/pay/registration.flt", "UTF-8", map);
		return registrationxml;
	}
	
	/***
	 * 服务端注册宝付账户
	 * @param register
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String registrationXml(RegisterInfo register)
			throws IOException, TemplateException{
		Map<String, String> map = new HashMap<String, String>();
		map.put("has_bf_account", register.getHas_bf_account());
		map.put("bf_account", register.getBf_account().toString());
		map.put("user_id", register.getUser_id());
		map.put("real_name", register.getName());
		map.put("id_card", register.getId_card().toString());
		map.put("bind_code", register.getBind_code());
		map.put("account_type", register.getAccount_type());
		String registrationxml = FreeMarkerUtil.execute("config/pay/quickRegister.flt", "UTF-8", map);
		return registrationxml;
	}


	/**
	 * 充值信息转换成xml
	 * 
	 * @param recharge
	 *            充值信息
	 * @return 返回一个xml文件
	 * @throws TemplateException
	 * @throws IOException
	 */
	public static String rechargeXml(RechargeInfo recharge) throws IOException,
			TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("merchant_id", recharge.getMerchant_id());
		map.put("order_id", recharge.getOrder_id());
		map.put("user_id", recharge.getUser_id());
		map.put("amount", recharge.getAmount().toString());
		map.put("fee", recharge.getFee().toString());
		map.put("fee_taken_on", recharge.getFee_taken_on());
		map.put("additional_info", recharge.getAdditional_info());
		map.put("return_url", recharge.getReturn_url());
		map.put("page_url", recharge.getPage_url());
		String recharges = FreeMarkerUtil.execute("config/pay/recharge.flt","UTF-8", map);
		return recharges;
	}
	
	
	/**
	 * 余额查询
	 * @param entity
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String BalanceInquiryXml(BalanceQueryInfo entity) throws IOException,
	TemplateException {
			Map<String, String> map = new HashMap<String, String>();
			map.put("merchant_id", entity.getMerchant_id());
			map.put("user_id", entity.getUser_id());
			String recharges = FreeMarkerUtil.execute("config/pay/balance.flt","UTF-8", map);
			return recharges;
}

	

	/**
	 * 将自动投标信息转换成xml文件
	 * 
	 * @param automatic
	 *            自动投标信息
	 * @return 返回一个xml文件
	 * @throws TemplateException
	 * @throws IOException
	 */
	public static String automaticBidXml(AutomaticBidInfo automatic)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("pMerBillNo", automatic.getpMerBillNo());
		map.put("pSigningDate", automatic.getpSigningDate());
		map.put("pAcctType", automatic.getpAcctType());
		map.put("pIdentNo", automatic.getpIdentNo());
		map.put("pRealName", automatic.getpRealName());
		map.put("pIpsAcctNo", automatic.getpIpsAcctNo());
		map.put("pWebUrl", automatic.getpWebUrl());
		map.put("pS2SUrl", automatic.getpS2SUrl());
		map.put("pMemo1", automatic.getpMemo1());
		map.put("pMemo2", automatic.getpMemo2());
		map.put("pMemo3", automatic.getpMemo3());
		String automaticxml = FreeMarkerUtil.execute(
				"config/pay/automaticBid.flt", "UTF-8", map);
		return automaticxml;
	}

	/**
	 * 还款信息转换成xml文件
	 * 
	 * @param repayment
	 *            还款信息
	 * @return 返回一个xml文件
	 * @throws TemplateException
	 * @throws IOException
	 */
	public static String repaymentXml(Repayment repayment) throws IOException,
			TemplateException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pBidNo", repayment.getpBidNo());
		map.put("pRepaymentDate", repayment.getpRepaymentDate());
		map.put("pMerBillNo", repayment.getpMerBillNo());
		map.put("pRepayType", repayment.getpRepayType());
		map.put("pIpsAuthNo", repayment.getpIpsAuthNo());
		map.put("pOutAcctNo", repayment.getpOutAcctNo());
		map.put("pOutAmt", repayment.getpOutAmt());
		map.put("pOutFee", repayment.getpOutFee());
		map.put("pWebUrl", repayment.getpWebUrl());
		map.put("pS2SUrl", repayment.getpS2SUrl());
		map.put("list", repayment.getInvestorInfos());
		map.put("pMemo1", repayment.getpMemo1());
		map.put("pMemo2", repayment.getpMemo2());
		map.put("pMemo3", repayment.getpMemo3());
		String repaymentxml = FreeMarkerUtil.execute(
				"config/pay/repayment.flt", "UTF-8", map);
		System.out.println(repaymentxml);
		return repaymentxml;
	}

	/**
	 * 投标审核
	 * 
	 * @param tenderAudit
	 *            投标审核信息
	 * @return 返回一个xml文件
	 * @throws TemplateException
	 * @throws IOException
	 */
	public static String tenderAuditXml(TenderAuditInfo tenderAudit)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("pBidNo", tenderAudit.getpBidNo());
		map.put("pContractNo", tenderAudit.getpContractNo());
		map.put("pMerBillNo", tenderAudit.getpMerBillNo());
		map.put("pBidStatus", tenderAudit.getpBidStatus());
		map.put("pS2SUrl", tenderAudit.getpS2SUrl());
		map.put("pMemo1", tenderAudit.getpMemo1());
		map.put("pMemo2", tenderAudit.getpMemo2());
		map.put("pMemo3", tenderAudit.getpMemo3());
		String tenderAuditxml = FreeMarkerUtil.execute(
				"config/pay/tenderAudit.flt", "UTF-8", map);
		return tenderAuditxml;
	}

	/**
	 * 提现
	 * 
	 * @param withdrawal
	 *            提现信息
	 * @return 返回一个xml文件
	 * @throws TemplateException
	 * @throws IOException
	 */
	public static String withdrawalXml(WithdrawalInfo wi)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("order_id", wi.getOrder_id());
		map.put("user_id", wi.getUser_id());
		map.put("amount", String.valueOf(wi.getAmount()));
		map.put("fee", String.valueOf(wi.getFee()));
		map.put("merchant_id", wi.getMerchant_id());
		map.put("fee_taken_on", wi.getFee_taken_on());
		map.put("return_url", wi.getReturn_url());
		map.put("page_url", wi.getPage_url());
		String tenderAuditxml = FreeMarkerUtil.execute("config/pay/withdrawal.flt", "UTF-8", map);
		return tenderAuditxml;

	}
	
	/***
	 * 服务端提现
	 * @param wi
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String withdrawServceXml(WithdrawalInfo wi)throws IOException, TemplateException{
		Map<String, String> map = new HashMap<String, String>();
		map.put("order_id", wi.getOrder_id());
		map.put("user_id", wi.getUser_id());
		map.put("amount", String.valueOf(wi.getAmount()));
		map.put("fee", String.valueOf(wi.getFee()));
		map.put("fee_taken_on", wi.getFee_taken_on());
		map.put("bank_no", wi.getBank_no());
		String withdrawServceXml=FreeMarkerUtil.execute("config/pay/withdrawServer.flt", "UTF-8", map);
		return withdrawServceXml;
	}
	/**
	 * 转账
	 * @param at
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String accttrans(AcctTrans at)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("merchant_id", at.getMerchant_id());
		map.put("order_id", at.getOrder_id());
		map.put("payer_user_id", at.getPayer_user_id());
		map.put("payee_user_id", at.getPayee_user_id());
		map.put("payer_type", String.valueOf(at.getPayer_type()));
		map.put("payee_type", String.valueOf(at.getPayee_type()));
		map.put("amount", String.valueOf(at.getAmount()));
		map.put("fee", String.valueOf(at.getFee()));
		map.put("fee_taken_on", String.valueOf(at.getFee_taken_on()));
		map.put("req_time", String.valueOf(at.getReq_time()));
		String tenderAuditxml = FreeMarkerUtil.execute(
				"config/pay/acctTrans.flt", "UTF-8", map);
		return tenderAuditxml;
	}
	
	
	/**
	 * 组合投标信息为XML String
	 * @param BidInfo
	 * @return XML String
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String bidInfoXML(BidInfo bidInfo)
			throws IOException, TemplateException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("merchant_id", bidInfo.getMerchant_id());
		map.put("order_id", bidInfo.getOrder_id());
		map.put("action_type", String.valueOf(bidInfo.getAction_type()));
		map.put("cus_id", String.valueOf(bidInfo.getCus_id()));
		map.put("cus_name", bidInfo.getCus_name());
		map.put("brw_id", bidInfo.getBrw_id());
		map.put("req_time", bidInfo.getReq_time());
		map.put("user_id", bidInfo.getUser_id());
		map.put("user_name", bidInfo.getUser_name());
		map.put("amount", String.valueOf(bidInfo.getAmount()));
		map.put("actions",bidInfo.getActions());
		map.put("fee", String.valueOf(bidInfo.getFee()));
		String tenderAuditxml = FreeMarkerUtil.execute("config/pay/bid.flt", "UTF-8", map);
		return tenderAuditxml;
	}
	
	/**
	 * 绑定银行卡
	 * @param bidInfo
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String opBankCardXml(UserBank  bank,String type)throws IOException, TemplateException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", bank.getUserbasicsinfo().getpMerBillNo());
		map.put("type", type);
		map.put("bank_no",bank.getBank_no());
		map.put("pro_value", bank.getPro_value());
		map.put("city_value", bank.getCity_value());
		map.put("bank_name", bank.getBank_name());
		map.put("bank_address", bank.getBank_address());
		map.put("validate_code", bank.getValidate_code());
		String tenderAuditxml = FreeMarkerUtil.execute("config/pay/opBankCard.flt", "UTF-8", map);
		return tenderAuditxml;
	}
	
	/**
	 * 店铺满标
	 * @param bidInfo
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String bidInfoFull(BidInfo bidInfo)
			throws IOException, TemplateException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("merchant_id", bidInfo.getMerchant_id());
		map.put("action_type", String.valueOf(bidInfo.getAction_type()));
		map.put("order_id", bidInfo.getOrder_id());
		map.put("cus_id", String.valueOf(bidInfo.getCus_id()));
		map.put("cus_name", bidInfo.getCus_name());
		map.put("brw_id", bidInfo.getBrw_id());
		map.put("req_time", bidInfo.getReq_time());
		map.put("user_id", bidInfo.getUser_id());
		map.put("amount", String.valueOf(bidInfo.getAmount()));
		map.put("actions", bidInfo.getActions());
		map.put("fee", String.valueOf(bidInfo.getFee()));
		String tenderAuditxml = FreeMarkerUtil.execute("config/pay/bidFull.flt", "UTF-8", map);
		return tenderAuditxml;
	}
	
	/**
	 * 店铺流标
	 * @param bidInfo
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String bidFullXML(BidInfo bidInfo)
			throws IOException, TemplateException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("merchant_id", bidInfo.getMerchant_id());
		map.put("action_type", String.valueOf(bidInfo.getAction_type()));
		map.put("order_id", bidInfo.getOrder_id());
		map.put("cus_id", String.valueOf(bidInfo.getCus_id()));
		map.put("cus_name", bidInfo.getCus_name());
		map.put("brw_id", bidInfo.getBrw_id());
		map.put("req_time", bidInfo.getReq_time());
		map.put("actions", bidInfo.getActions());
		map.put("fee", String.valueOf(bidInfo.getFee()));
		String tenderAuditxml = FreeMarkerUtil.execute("config/pay/bidFull.flt", "UTF-8", map);
		return tenderAuditxml;
	}
	
	
	
	
	/**
	 * 还标
	 * @param bi
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String bidinfo4XML(BidInfo4 bi)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("merchant_id", bi.getMerchant_id());
		map.put("action_type", String.valueOf(bi.getAction_type()));
		map.put("order_id", bi.getOrder_id());
		map.put("cus_id", String.valueOf(bi.getCus_id()));
		map.put("cus_name", bi.getCus_name());
		map.put("brw_id", bi.getBrw_id());
		map.put("req_time", bi.getReq_time());
		String tenderAuditxml = FreeMarkerUtil.execute("config/pay/payment/bid_action_type4_1.flt", "UTF-8", map);
		
		for(Payuser payuser:bi.getPayuser()){
			map.put("user_id", payuser.getUser_id());
			map.put("amount", String.valueOf(payuser.getAmount()));
			map.put("fee", String.valueOf(payuser.getFee()));
			tenderAuditxml += FreeMarkerUtil.execute("config/pay/payment/bid_action_type4_2.flt", "UTF-8", map);
		}
		
		map.put("voucher_id", String.valueOf(bi.getVoucher_id()));
		map.put("voucher_fee", String.valueOf(bi.getVoucher_fee()));
		map.put("special", String.valueOf(bi.getSpecial()));
		tenderAuditxml += FreeMarkerUtil.execute("config/pay/payment/bid_action_type4_3.flt", "UTF-8", map);
	
		return tenderAuditxml;
	}
	
	
	/**
	 * 业务查询
	 * @param cardInfo
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String p2pQueryXml(P2pQuery p2pQuery)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("order_id", p2pQuery.getOrder_id());
		map.put("type", String.valueOf(p2pQuery.getType()));
		String tenderAuditxml = FreeMarkerUtil.execute("config/pay/p2pQuery.flt", "UTF-8", map);
		return tenderAuditxml;
	}
	
	/**
	 * 业务查询
	 * @param cardInfo
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String p2pQueryTimeXml(P2pQuery p2pQuery)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("type", String.valueOf(p2pQuery.getType()));
		map.put("start_time", p2pQuery.getStart_time());
		map.put("end_time", p2pQuery.getEnd_time());
		String tenderAuditxml = FreeMarkerUtil.execute("config/pay/p2pTimeQuery.flt", "UTF-8", map);
		return tenderAuditxml;
	}
	
	/**
	 * 用户信息查询
	 * @param p2pQuery
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String p2pUserInfoQueryXml(P2pUserInfoQuery p2pQuery) throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("userNo", p2pQuery.getBillNo());
		map.put("type", String.valueOf(p2pQuery.getType()));
		map.put("start_time", p2pQuery.getStart_time());
		map.put("end_time", p2pQuery.getEnd_time());
		return FreeMarkerUtil.execute("config/pay/p2pUserInfoQuery.flt", "UTF-8", map);
	}

	/**
	 * 将标的和借款人转为ips格式
	 * 
	 * @param rs
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String registerSubjectXml(RegisterSubject rs)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("pMerBillNo", rs.getpMerBillNo());
		map.put("pBidNo", rs.getpBidNo());
		map.put("pRegDate", rs.getpRegDate());
		map.put("pLendAmt", rs.getpLendAmt());
		map.put("pGuaranteesAmt", rs.getpGuaranteesAmt());
		map.put("pTrdLendRate", rs.getpTrdLendRate());
		map.put("pTrdCycleType", rs.getpTrdCycleType());
		map.put("pTrdCycleValue", rs.getpTrdCycleValue());
		map.put("pLendPurpose", rs.getpLendPurpose());
		map.put("pRepayMode", rs.getpRepayMode());
		map.put("pOperationType", rs.getpOperationType());
		map.put("pLendFee", rs.getpLendFee());
		map.put("pAcctType", rs.getpAcctType());
		map.put("pIdentNo", rs.getpIdentNo());
		map.put("pRealName", rs.getpRealName());
		map.put("pIpsAcctNo", rs.getpIpsAcctNo());
		map.put("pWebUrl", rs.getpWebUrl());
		map.put("pS2SUrl", rs.getpS2SUrl());
		map.put("pMemo1", rs.getpMemo1());
		map.put("pMemo2", rs.getpMemo2());
		map.put("pMemo3", rs.getpMemo3());
		String registerSubjectXml = FreeMarkerUtil.execute(
				"config/pay/registerSubject.flt", "UTF-8", map);
		return registerSubjectXml;
	}

	/**
	 * 
	 * @param transfer
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String transFerXml(Transfer transfer) throws IOException,
			TemplateException {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("pMerBillNo", transfer.getpMerBillNo());
		map.put("pBidNo", transfer.getpBidNo());
		map.put("pDate", transfer.getpDate());
		map.put("pTransferType", transfer.getpTransferType());
		map.put("pTransferMode", transfer.getpTransferMode());
		map.put("pS2SUrl", transfer.getpS2SUrl());

		map.put("pMemo1", transfer.getpMemo1());
		map.put("pMemo2", transfer.getpMemo2());
		map.put("pMemo3", transfer.getpMemo3());
		map.put("pRows", transfer.getpRows());

		String transferXml = FreeMarkerUtil.execute("config/pay/transfers.flt",
				"UTF-8", map);
		return transferXml;
	}

	/**
	 * 债权转让
	 * 
	 * @param assignment
	 * @return 返回xml文件
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String bidAssignmentXml(BidAssignment assignment)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("pMerBillNo", assignment.getpMerBillNo());
		map.put("pMerDate", assignment.getpMerDate());
		map.put("pBidNo", assignment.getpBidNo());
		map.put("pContractNo", assignment.getpContractNo());
		map.put("pFromAccountType", assignment.getpFromAccountType());
		map.put("pFromName", assignment.getpFromName());
		map.put("pFromAccount", assignment.getpFromAccount());
		map.put("pFromIdentType", assignment.getpFromIdentType());
		map.put("pFromIdentNo", assignment.getpFromIdentNo());
		map.put("pToAccountType", assignment.getpToAccountType());
		map.put("pToAccountName", assignment.getpToAccountName());
		map.put("pToAccount", assignment.getpToAccount());
		map.put("pToIdentType", assignment.getpToIdentType());
		map.put("pToIdentNo", assignment.getpToIdentNo());

		map.put("pCreMerBillNo", assignment.getpCreMerBillNo());
		map.put("pCretAmt", assignment.getpCretAmt());
		map.put("pPayAmt", assignment.getpPayAmt());
		map.put("pFromFee", assignment.getpFromFee());
		map.put("pToFee", assignment.getpToFee());
		map.put("pCretType", assignment.getpCretType());

		map.put("pWebUrl", assignment.getpWebUrl());
		map.put("pS2SUrl", assignment.getpS2SUrl());
		map.put("pMemo1", assignment.getpMemo1());
		map.put("pMemo2", assignment.getpMemo2());
		map.put("pMemo3", assignment.getpMemo3());

		String bidxml = FreeMarkerUtil.execute("config/pay/bidAssignment.flt",
				"UTF-8", map);
		return bidxml;
	}


	/**
	 * 自动还款签约
	 * 
	 * @throws TemplateException
	 * @throws IOException
	 * 
	 */
	public static String repaymentSignXml(RepaymentSign repaymentSign)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("pMerBillNo", repaymentSign.getpMerBillNo());
		map.put("pSigningDate", repaymentSign.getpSigningDate());
		map.put("pIdentType", repaymentSign.getpIdentType());
		map.put("pIdentNo", repaymentSign.getpIdentNo());
		map.put("pRealName", repaymentSign.getpRealName());
		map.put("pIpsAcctNo", repaymentSign.getpIpsAcctNo());
		map.put("pValidType", repaymentSign.getpValidType());
		map.put("pValidDate", repaymentSign.getpValidDate());
		map.put("pWebUrl", repaymentSign.getpWebUrl());
		map.put("pS2SUrl", repaymentSign.getpS2SUrl());
		map.put("pMemo1", repaymentSign.getpMemo1());
		map.put("pMemo2", repaymentSign.getpMemo2());
		map.put("pMemo3", repaymentSign.getpMemo3());

		String repaymentSignXml = FreeMarkerUtil.execute(
				"config/pay/repaymentSign.flt", "UTF-8", map);
		return repaymentSignXml;
	}
	
	
	/**
	 * @param cardInfo
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String cardXml(CardInfo cardInfo)
			throws IOException, TemplateException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", cardInfo.getName());
		map.put("documentNo", cardInfo.getDocumentNo());
		map.put("subreportIDs", cardInfo.getSubreportIDs());
		map.put("refID", cardInfo.getRefID());
		String registrationxml = FreeMarkerUtil.execute(
				"config/pay/queryxml.flt", "UTF-8", map);
		return registrationxml;
	}

}
