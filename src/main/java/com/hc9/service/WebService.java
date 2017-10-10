package com.hc9.service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.Map;

import org.tempuri.ServiceSoap;
import org.tempuri.ServiceSoapProxy;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.ParameterIps;
import com.hc9.common.util.XmlParsingBean;
import com.hc9.common.util.XmlTool;
import com.hc9.model.BalanceQueryInfo;
import com.hc9.model.BankList;
import com.hc9.model.MerUserInfo;
import com.hc9.model.RepaymentInfo;
import com.hc9.model.ReturnInfo;
import com.ips.security.utility.IpsCrypto;

/**
 * WebService请求并返回数据
 * @author frank 2014-07-14
 * 用getMercode()代替 getCert() 2014-7-24
 */
public class WebService {
	/**
	 * 查询平台或商户余额
	 * @param merchantNo 平台或商户账号
	 * @return 返回商户余额
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 * @throws RemoteException 
	 */
	public static BalanceQueryInfo accountBalance(String merchantNo) throws Exception{

		StringBuffer argSign = new StringBuffer(ParameterIps.getCert()).append(merchantNo) .append(ParameterIps.getMd5ccertificate());
		String md5argSign = IpsCrypto.md5Sign(argSign.toString());//进行Md5加密
		//获取返回数据
		ServiceSoap serviceSoap = new ServiceSoapProxy();
		String argXmlPara = serviceSoap.queryForAccBalance(ParameterIps.getCert(), merchantNo, md5argSign);
		BalanceQueryInfo bal = new BalanceQueryInfo();
		return (BalanceQueryInfo) XmlParsingBean.simplexml2Object(argXmlPara,bal);
		
	}
	/**
	 * 获取银行列表
	 * @return 返回银行信息列表
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 * @throws RemoteException 
	 */
	public static BankList bankList() throws Exception {
		StringBuffer argSign = new StringBuffer(ParameterIps.getCert()).append(ParameterIps.getMd5ccertificate());
		String md5 = IpsCrypto.md5Sign(argSign.toString());
		
		SoapProxy soapProxy = new SoapProxy();
		
		String argXmlPara = soapProxy.requestWs(Constant.GET_BANK_LIST,ParameterIps.getCert(),md5);
		XmlTool Tool = new XmlTool();
		Tool.SetDocument(argXmlPara);
		String xml = Tool.getNodeValue("GetBankListResult");
		BankList bank=new BankList();
		bank = (BankList) XmlParsingBean.simplexml2Object(xml,new BankList());
		
		return bank;
	}
	/**
	 * 审核投标
	 * @param map 
	 * @param argMerCode 商户号 
	 * @param arg3DesXmlPara 3des加密后的信息
	 * @param argSign md5加密后的报文
	 * @throws RemoteException 
	 * @return 返回放款处理后的信息
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public static ReturnInfo loans(Map<String, String> map) throws RemoteException, FileNotFoundException, UnsupportedEncodingException{
		String argMerCode = map.get("pMerCode");
		String arg3DesXmlPara = map.get("p3DesXmlPara");
		String argSign = map.get("pSign");
		SoapProxy soapProxy = new SoapProxy();
	    String info;
		try {
			info = soapProxy.requestWs(Constant.TRANSFER,argMerCode, arg3DesXmlPara, argSign);
	 	    XmlTool Tool = new XmlTool();
			Tool.SetDocument(info);
			String xml = Tool.getNodeValue("TransferResult");
			ReturnInfo returnInfo = (ReturnInfo) XmlParsingBean.simplexml2Object(xml,new ReturnInfo());
			return returnInfo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
	/**
	 * 将xml信息解析成对象
	 * @param xml xml信息
	 */
	public static RepaymentInfo repaymentXml(String xml){
	    return XmlParsingBean.parseXml(xml);
	}
	/**
	 * 查询ips用户信息
	 * @param identNo
	 * @return
	 */
	public static MerUserInfo QueryMerUserInfo(String identNo){
		String merCode=ParameterIps.getCert();
	 	StringBuffer argSign = new StringBuffer(merCode)
	 									.append(identNo)
	 									.append(ParameterIps.getMd5ccertificate());
		String md5 = IpsCrypto.md5Sign(argSign.toString());
		SoapProxy soapProxy = new SoapProxy();
		String info;
		try{
			info=soapProxy.requestQueryWs(Constant.QUERY_MER_USER_INFO,merCode,identNo,md5);
	 	    XmlTool Tool = new XmlTool();
			Tool.SetDocument(info);
			String xml=Tool.getNodeValue("QueryMerUserInfoResult");
			MerUserInfo merUserInfo=(MerUserInfo)XmlParsingBean.simplexml2Object(xml, new MerUserInfo());
			return merUserInfo;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	

}
