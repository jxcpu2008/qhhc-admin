package com.hc9.service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class RegisterService {
	/**
	 * 加密信息并返回加密后的信息
	 * 
	 * @param registerCall
	 *            提交信息的xml文件
	 * @return 返回加密后的文件集合
	 */
	public static Map<String, String> registerCall(String registerCall) {
		// 将参数装进map里面
		String bfsign = CommonUtil.aesEncryptKey16(registerCall,
				ParameterIps.getDes_algorithm());
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestParams", registerCall);
		map.put("sign", bfsign);
		map.put("terminal_id", ParameterIps.getTerminalnuMber());
		map.put("merchant_id", ParameterIps.getCert());
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
