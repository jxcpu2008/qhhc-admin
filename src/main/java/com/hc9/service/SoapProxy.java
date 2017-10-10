package com.hc9.service;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import com.hc9.common.constant.Constant;
import com.hc9.common.constant.PayURL;
/**
 * 处理IPSWebService方式的请求
 * 转账，银行列表，账户余额
 * @author Frank
 *
 */
public class SoapProxy {
	private static final String URL=PayURL.TRANSFERURL;
	private static final String QUERY_URL=PayURL.CREDITWSQUERYURL;
	public SoapProxy(){
		
	}
	
	/**
	 * 处理WebService请求
	 * @param param 参数
	 * @return
	 * @throws Exception
	 */
	public String requestWs(String...param) throws Exception{
		String soap="";
		if(param[0].equals(Constant.TRANSFER)){
			soap=soapTransfer(param[1], param[2], param[3]);
		}else if(param[0].equals(Constant.GET_BANK_LIST)){
			soap=soapBank(param[1], param[2]);
		}else{
			soap=soapQueryForAccBalance(param[1], param[2], param[3]);
		}
		URL url=new URL(URL);
		URLConnection connection=url.openConnection();
		connection.setUseCaches(false);//不要缓存
		connection.setDoInput(true);//输入
		connection.setDoOutput(true);//输出
		connection.setRequestProperty("Content-Length", Integer.toString(soap.length()));
		connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		connection.setRequestProperty("SOAPAction", "http://tempuri.org/"+param[0]);
		
		OutputStream os = connection.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
		osw.write(soap);
        osw.flush();  
        osw.close();
        
        InputStream is = connection.getInputStream();  
        
        DataInputStream dis=new DataInputStream(is);
 	   	byte d[]=new byte[dis.available()];
 	   	dis.read(d);
 	    String data=new String(d,"UTF-8");
 	    
 	    return data;
	}
	
	public String requestQueryWs(String...param) throws Exception{
		String soap="";
		if(param[0].equals(Constant.QUERY_MER_USER_INFO)){
			soap=soapQueryMerUserInfo(param[1], param[2], param[3]);
		}
		URL url=new URL(QUERY_URL);
		URLConnection connection=url.openConnection();
		connection.setUseCaches(false);//不要缓存
		connection.setDoInput(true);//输入
		connection.setDoOutput(true);//输出
		connection.setRequestProperty("Content-Length", Integer.toString(soap.length()));
		connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		connection.setRequestProperty("SOAPAction", "http://tempuri.org/"+param[0]);
		OutputStream os = connection.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
		osw.write(soap);
        osw.flush();  
        osw.close();    
        InputStream is = connection.getInputStream();  
         BufferedInputStream bis=new BufferedInputStream(is);
     //  DataInputStream dis=new DataInputStream(is);
 	   	byte d[]=new byte[bis.available()];
         bis.read(d);
 	    String data=new String(d,"UTF-8");
 	    
 	    return data;
	}
	/**
	 * 处理SOAP
	 * @param argMerCode “平台”账号
	 * @param arg3DesXmlPara 请求信息
	 * @param argSign MD5 摘要
	 * @return
	 */
	private String soapTransfer(String argMerCode, String arg3DesXmlPara, String argSign){
		String xmlString="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>"
				+ "<Transfer xmlns=\"http://tempuri.org/\">"
				+ "<pMerCode>"+argMerCode+"</pMerCode>"
				+ "<p3DesXmlPara>"+arg3DesXmlPara+"</p3DesXmlPara>"
				+ "<pSign>"+argSign+"</pSign>"
				+ "</Transfer>"
				+ "</soap:Body>"
				+ "</soap:Envelope>";
		return xmlString;
	}
	private String soapBank(String arg3DesXmlPara, String argSign){
		String xmlString="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>"
				+"<GetBankList xmlns=\"http://tempuri.org/\">"
			    +"<argMerCode>"+arg3DesXmlPara+"</argMerCode>"
			    +"<argSign>"+argSign+"</argSign>"
			    +"</GetBankList>"
				+ "</soap:Body>"
				+ "</soap:Envelope>";
		return xmlString;
	}
	private String soapQueryForAccBalance(String argIpsAccount,String arg3DesXmlPara, String argSign){
		String xmlString="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>"
				+"<QueryForAccBalance xmlns=\"http://tempuri.org/\">"
			    +"<argMerCode>"+argIpsAccount+"</argMerCode>"
			    +"<argMerCode>"+arg3DesXmlPara+"</argMerCode>"
			    +"<argSign>"+argSign+"</argSign>"
			    +"</QueryForAccBalance>"
				+ "</soap:Body>"
				+ "</soap:Envelope>";
		return xmlString;
	}	
	
//	private String soapGuaranteeUnfreeze(String argIpsAccount,String arg3DesXmlPara, String argSign){
//		String xmlString="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
//				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
//				+ "<soap:Body>"
//				+"<QueryForAccBalance xmlns=\"http://tempuri.org/\">"
//			    +"<argMerCode>"+argIpsAccount+"</argMerCode>"
//			    +"<argMerCode>"+arg3DesXmlPara+"</argMerCode>"
//			    +"<argSign>"+argSign+"</argSign>"
//			    +"</QueryForAccBalance>"
//				+ "</soap:Body>"
//				+ "</soap:Envelope>";
//		return xmlString;
//	}	
	
	/**
	 * 处理SOAP
	 * @param argMerCode “平台”账号
	 * @param argIpsAccount 用户账号
	 * @param argSign MD5 摘要
	 * @return
	 */
	private String soapQueryMerUserInfo(String argMerCode,String argIpsAccount,String argSign){
		StringBuilder xmlStrBuilder=new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		xmlStrBuilder.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		xmlStrBuilder.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		xmlStrBuilder.append("<soap:Body>");
		xmlStrBuilder.append("<QueryMerUserInfo xmlns=\"http://tempuri.org/\">");
		xmlStrBuilder.append("<argMerCode>").append(argMerCode).append("</argMerCode>");
		xmlStrBuilder.append("<argIpsAccount>").append(argIpsAccount).append("</argIpsAccount>");
		xmlStrBuilder.append("<argSign>").append(argSign).append("</argSign>");
		xmlStrBuilder.append("<argMemo>").append("ok").append("</argMemo>");
		xmlStrBuilder.append("</QueryMerUserInfo>");
		xmlStrBuilder.append("</soap:Body>");
		xmlStrBuilder.append("</soap:Envelope>");
		return xmlStrBuilder.toString();
	}
}
