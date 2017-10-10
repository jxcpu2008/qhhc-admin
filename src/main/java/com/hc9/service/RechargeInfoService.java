package com.hc9.service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.BankInfoListUtil;
import com.hc9.common.util.CommonUtil;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.XmlParsingBean;
import com.hc9.model.BalanceQueryInfo;
import com.hc9.model.BankInfo;
import com.hc9.model.BankList;
import com.hc9.model.RepaymentAsyn;
import com.hc9.model.RepaymentInfo;
import com.hc9.model.ReturnInfo;

/**
 * 
 * 根据用户传入的数据访问环讯
 * 
 * @author frank 2014-07-03 用getMercode()代替 getCert() 2014-7-24
 */
public class RechargeInfoService {
	/**
	 * 
	 * 转账
	 * 加密信息并返回加密后的信息
	 * 
	 * @param rechargeCall
	 *            提交信息的xml文件
	 * @return 返回加密后的文件集合
	 */
	public static Map<String, String> transferCall(String rechargeCall,String key,Long userid,Double amount) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("page_url", Constant.TransferUrl);
		map.put("service_url", Constant.TransferUrl_SIGN+"?userId="+userid+"&amount="+amount);
		
		return map;
	}
	
	
	/**
	 * 
	 * 充值 XML
	 * @param rechargeCall
	 * @param key
	 * @param userid
	 * @param amount
	 * @return
	 */
	public static Map<String, String> rechargeCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("page_url", Constant.RECHARGEURL);
		map.put("service_url", Constant.ASYNCHRONISMRECHARGE);
		return map;
	}
	
	/**
	 * 
	 * 第三方担保充值 XML
	 * @param rechargeCall
	 * @param key
	 * @param userid
	 * @param amount
	 * @return
	 */
	public static Map<String, String> escrowRechargeCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("page_url", Constant.ESCROWRECHARGE);
		map.put("service_url", Constant.ASYNESCROWRECHARGE);
		return map;
	}
	
	/**
	 * 
	 * APP充值 XML
	 * @param rechargeCall
	 * @param key
	 * @param userid
	 * @param amount
	 * @return
	 */
	public static Map<String, String> appRechargeCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("page_url", Constant.APP_RECHARGEURL);
		map.put("service_url", Constant.APP_ASYNCHRONISMRECHARGE);
		
		return map;
	}
	
	/**
	 * 提现XML
	 * @param rechargeCall
	 * @param key
	 * @param userid
	 * @param amount
	 * @return
	 */
	public static Map<String, String> withdrawalCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("page_url", Constant.WITHDRAWAL);
		map.put("service_url", Constant.WITHDRAWASYNCHRONOUS);
		return map;
	}
	
	/**
	 * 第三方担保提现XML
	 * @param rechargeCall
	 * @param key
	 * @param userid
	 * @param amount
	 * @return
	 */
	public static Map<String, String> escorwWithdrawCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("page_url", Constant.ESCROWWITHDRAWAL);
		map.put("service_url", Constant.ASYNESCROWWITHDRAW);
		return map;
	}
	
	/**
	 * 投资店铺提交信息的xml文件
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> BidShopCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("service_url", Constant.PROASYNCHRONISMBID);
		map.put("page_url", Constant.PROBID);
		return map;
	}
	
	/**
	 * 投资店铺提交信息的xml文件 APP
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> BidShopCallApp(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("service_url", Constant.PROASYNCHRONISMBID_APP);
		map.put("page_url", Constant.PROBID_APP);
		return map;
	}
	
	
	/***
	 * 授权协议（页面接口）
	 * @param userId
	 * @return
	 */
	public static Map<String, String> inAccreditCall(String userId){
		Map<String, String> map = new HashMap<String, String>();
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("user_id", userId);
		map.put("service_url", Constant.ASYNCHRONISMINACCREDIT);
		map.put("page_url", Constant.INACCREDIT);
		return map;
	}
	
	/****
	 * 用户注册宝付授权
	 * @param userId
	 * @return
	 */
	public static Map<String, String> inAccreditUserCall(String userId){
		Map<String, String> map = new HashMap<String, String>();
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("user_id", userId);
		map.put("service_url", Constant.ASYNCHRONISMINACCREDITUSER);
		map.put("page_url", Constant.INACCREDITUSRE);
		return map;
	}
	
	
	/**
	 * 店铺满标 xml 提交
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> bidShopFullCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("service_url", Constant.ASYNCRONISSHOP);
		map.put("page_url", Constant.SHOP);
		return map;
	}
	
	/**
	 * 店铺流标xml
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> bidShopFlowCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("service_url", Constant.SYNCHRONIZEDBIDSHOPFLOW);
		map.put("page_url", Constant.BIDSHOPFLOW);
		return map;
	}
	
	/***
	 * 项目购买xml
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> loanBidCall(String rechargeCall,String key,String orderNum) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("service_url", Constant.ASYNCHRONISMBID);
		map.put("page_url", Constant.BID+"?orderNum="+orderNum);
		return map;
	}
	
	
	/***
	 * 项目购买xml
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> loanBidCallApp(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("service_url", Constant.ASYNCHRONISMBID_APP);
		map.put("page_url", Constant.BID_APP);
		return map;
	}
	
	
	/**
	 * 项目满标放款 xml 提交
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> bidLoanFullCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("service_url", Constant.ASYNCHRONISMLOANCREDIT);
		map.put("page_url", Constant.LOANCREDIT);
		return map;
	}
	
	/**
	 * 项目流标xml
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> bidLoanFlowCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("service_url", Constant.ASYNCHRONISMLOANFLOW);
		map.put("page_url", Constant.RETURNLOANFLOW);
		return map;
	}
	
	/**
	 * 项目还款
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> loanhuanCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("service_url", Constant.REPAYMENT_SIGN_ASYNCHRONOUS);
		map.put("page_url", Constant.REPAYMENT_SIGN);
		return map;
	}
	
	/**
	 * 项目还款
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> projecthuanCall(String rechargeCall,String key,String pId,Double fhmoney) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("service_url", Constant.ProREPAYMENT_SIGN_ASYNCHRONOUS+"?pId="+pId+"&fhmoney="+fhmoney);
		map.put("page_url", Constant.ProREPAYMENT_SIGN);
		return map;
	}
	
	
	
	/**
	 * 债权投标提交信息的xml文件
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> BidAssCall(String rechargeCall,String key) {
		// 将参数装进map里面
		String bfsign = CommonUtil.MD5( rechargeCall +"~|~"+ key);
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", rechargeCall);
		map.put("sign", bfsign);
		map.put("merchant_id", ParameterIps.getCert());
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("service_url", Constant.ASYNCHRONISMBIDASSIGNMENT);
		map.put("page_url", Constant.BIDASSIGNMENT);
		return map;
	}
	
	
	
	/**
	 * 提交身份证xml
	 * @param rechargeCall
	 * @param key
	 * @return
	 */
	public static Map<String, String> cardCall(String rechargeCall,
			String key) {
		// 将参数装进map里面
		Map<String, String> map = new HashMap<String, String>();
		map.put("userID", "qhhcwsquery");
		map.put("password", "zII5QNlUgpE7V22KSZOVhQ==");
		map.put("pMerCode", rechargeCall);
		return map;
	}

	/**
	 * 解析xml文件
	 * 
	 * @param registerCall
	 *            环讯返回信息的xml文件
	 * @param obj
	 *            需要解析成的对象
	 * @return 返回实体对象
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static Object decryption(String registerCall, Object obj)
			throws FileNotFoundException, UnsupportedEncodingException {
		return XmlParsingBean.simplexml1Object(registerCall, obj);
	}

	/**
	 * 得到银行信息集合
	 * 
	 * @return 返回银行信息集合对象
	 */
	public static List<BankInfo> bankList() {
		List<BankInfo> bankList = new ArrayList<BankInfo>();
		BankList bank = null;
		try {
			bank = WebService.bankList();
			bankList = BankInfoListUtil.dismantling(bank);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bankList;
	}

	/**
	 * 
	 * 获取平台或商户余额
	 * 
	 * @param merchantNo
	 *            平台或商户账号
	 * @return 返回商户余额信息
	 */
	public static BalanceQueryInfo accountBalance(String merchantNo) {
		try {
			return WebService.accountBalance(merchantNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 投资审核 ,放款
	 * 
	 * @param map
	 *            加密后的集合信息
	 * @return 返回放款处理后的信息
	 * @throws RemoteException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static ReturnInfo transfer(Map<String, String> map)
			throws RemoteException, FileNotFoundException,
			UnsupportedEncodingException {
		return WebService.loans(map);
	}

	/**
	 * 将xml信息解析成对象
	 * 
	 * @param xml
	 *            xml信息
	 */
	public static RepaymentInfo repaymentXml(String xml) {
		return XmlParsingBean.parseXml(xml);
	}

	public static RepaymentAsyn repaymentAsynXml(String xml) {
		return XmlParsingBean.asynXml2Repayment(xml);
	}

}
