package com.hc9.common.util;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hc9.common.constant.Certificate;
import com.hc9.dao.entity.Automatic;
import com.hc9.model.PRow;
import com.hc9.model.RepaymentAsyn;
import com.hc9.model.RepaymentInfo;
import com.hc9.model.RepaymentInvestor;
import com.hc9.model.RepaymentInvestorInfo;
import com.hc9.model.Transfer;
import com.ips.security.utility.IpsCrypto;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
 * 解析xml并转换成对象
 * @author RanQiBing 2014-01-08
 *
 */
public class XmlParsingBean {
	/**
	 * 将xml文件解析转换成一个对象
	 * @param xml	xml文件
	 * @param obj	对象名称
	 * @return	返回一个对象
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public static Object simplexml1Object(String xml,Object obj) throws FileNotFoundException, UnsupportedEncodingException {
			//解析成xml文件
		   String xmlResolve = IpsCrypto.triDesDecrypt(xml, ParameterIps.getDes_algorithm(),ParameterIps.getDesedevector());
		   //去掉xml文件头
		   xmlResolve = xmlResolve.substring(Certificate.XMLTOP_INDEX);
		   XStream xStream = new XStream(new DomDriver());
		   //将 pReq替换成对象名称
		   xStream.alias("pReq", obj.getClass());
		   //将xml文件转换成一个对象
		   Object reobj = xStream.fromXML(xmlResolve,obj);
		   return reobj;
		}
	/**
	 * 返回的p3DesXmlPara解析为xml string
	 * @param xml
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static String md52Xml(String xml) throws FileNotFoundException, UnsupportedEncodingException {
		//解析成xml文件
	   String xmlResolve = IpsCrypto.triDesDecrypt(xml, ParameterIps.getDes_algorithm(),ParameterIps.getDesedevector());
	   //去掉xml文件头
	   xmlResolve = xmlResolve.substring(Certificate.XMLTOP_INDEX);
	   return xmlResolve;
	}
	/**
	 * 返回的p3DesXmlPara解析为xml string
	 * 保留头文件
	 * @param xml
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static String md52Xml4Assign(String xml) throws FileNotFoundException, UnsupportedEncodingException {
		//解析成xml文件
	   String xmlResolve = IpsCrypto.triDesDecrypt(xml, ParameterIps.getDes_algorithm(),ParameterIps.getDesedevector());
	   return xmlResolve;
	}
	/**
	 * 将webserver返回回的xml文件解析转换成一个对象
	 * @param xml	xml文件
	 * @param obj	对象名称
	 * @return	返回一个对象
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public static Object simplexml2Object(String xml,Object obj) throws FileNotFoundException, UnsupportedEncodingException {
			//解析成xml文件
		   //String xmlResolve = IpsCrypto.triDesDecrypt(xml, Certificate.DES_ALGORITHM05,Certificate.DESEDEVECTOR05);
		   //去掉xml文件头
		   xml = xml.substring(Certificate.XMLTOP_INDEXTWO);
		   XStream xStream = new XStream(new DomDriver());
		   //将 pReq替换成对象名称
		   xStream.alias("pReq", obj.getClass());
		   //将xml文件转换成一个对象
		   Object reobj = xStream.fromXML(xml,obj);
		   return reobj;
		}
	/**
     * 解析提现异步返回信息的处理
     * @param strXml 返回加密后的xml文件
     * @return
     */
    public static RepaymentInfo parseXml(String xml){  
  
        RepaymentInfo repaymentInfo = new RepaymentInfo();  
            try {  
             // 解析成xml文件
                String strXml = IpsCrypto.triDesDecrypt(xml,ParameterIps.getDes_algorithm(),ParameterIps.getDesedevector());
                strXml = strXml.substring(1);
                
                //读入文档流  
                Document document = DocumentHelper.parseText(strXml.toString()); // 将字符串转  
                //获取根节点  
                Element root = document.getRootElement();  
                //将根节点下的信息放入还款对象中
                repaymentInfo.setpBidNo(root.elementTextTrim("pBidNo"));
                repaymentInfo.setpContractNo(root.elementTextTrim("pContractNo"));
                repaymentInfo.setpRepaymentDate(root.elementTextTrim("pRepaymentDate"));
                repaymentInfo.setpMerBillNo(root.elementTextTrim("pMerBillNo"));
                repaymentInfo.setpIpsBillNo(root.elementTextTrim("pIpsBillNo"));
                repaymentInfo.setpMemo1(root.elementTextTrim("pMemo1"));
                repaymentInfo.setpMemo2(root.elementTextTrim("pMemo2"));
                repaymentInfo.setpMemo3(root.elementTextTrim("pMemo3"));
                //解析repaymentInvestorInfosList节点  
                List<RepaymentInvestorInfo> repaymentInvestorInfosList = new ArrayList<RepaymentInvestorInfo>();   
                Element pDetails = root.element("pDetails");
                //遍历pDetails节点下的投资人信息
                for(Iterator iterator = pDetails.elementIterator("pRow");iterator.hasNext();){  
                    Element eStudent = (Element) iterator.next();  
                    RepaymentInvestorInfo repaymentInvestorInfo = new RepaymentInvestorInfo(); 
                    repaymentInvestorInfo.setpTTrdFee(eStudent.elementTextTrim("pTAcctType"));
                    repaymentInvestorInfo.setpTIpsAcctNo(eStudent.elementTextTrim("pFIpsAcctNo"));
                    repaymentInvestorInfo.setpStatus(eStudent.elementTextTrim("pStatus"));
                    repaymentInvestorInfo.setpMessage(eStudent.elementTextTrim("pMessage"));
                    repaymentInvestorInfosList.add(repaymentInvestorInfo); 
                }  
                
                repaymentInfo.setRepaymentInvestorInfoList(repaymentInvestorInfosList);
            } catch (DocumentException e) {  
                e.printStackTrace();  
            }  
            return repaymentInfo;  
    }  
    /**
     * 将返回的信息转为Transfer
     * @param xmlString
     * @return
     */
	public static Transfer xml2Transfer(String xmlString){
		Transfer tf=new Transfer();
		Document ipsDoc=null;
		try {
			ipsDoc = read(xmlString);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element pReq = ipsDoc.getRootElement();
		tf.setpMerBillNo(pReq.elementText("pMerBillNo"));
		tf.setpBidNo(pReq.elementText("pBidNo"));
		tf.setpDate(pReq.elementText("pDate"));
		tf.setpTransferType(pReq.elementText("pTransferType"));
		tf.setpTransferMode(pReq.elementText("pTransferMode"));
		tf.setpIpsBillNo(pReq.elementText("pIpsBillNo"));
		tf.setpIpsTime(pReq.elementText("pIpsTime"));
		tf.setpMemo1(pReq.elementText("pMemo1"));
		tf.setpMemo2(pReq.elementText("pMemo2"));
		tf.setpMemo3(pReq.elementText("pMemo3"));
		
		Element pDetails=pReq.element("pDetails");
		List<Element> pRows=pDetails.elements("pRow");
		List<PRow> rows=new ArrayList<>();
		for(Element el:pRows){
			PRow prow=new PRow(); 
			prow.setpOriMerBillNo(el.elementText("pOriMerBillNo"));
			prow.setpTrdAmt(el.elementText("pTrdAmt"));
			prow.setpFIpsAcctNo(el.elementText("pFIpsAcctNo"));
			prow.setpFTrdFee(el.elementText("pFTrdFee"));
			prow.setpTAcctType(el.elementText("pTAcctType"));
			prow.setpTIpsAcctNo(el.elementText("pTIpsAcctNo"));
			prow.setpTTrdFee(el.elementText("pTTrdFee"));
			prow.setpIpsDetailBillNo(el.elementText("pIpsDetailBillNo"));
			prow.setpIpsDetailTime(el.elementText("pIpsDetailTime"));
			prow.setpIpsFee(el.elementText("pIpsFee"));
			prow.setpStatus(el.elementText("pStatus"));
			prow.setpMessage(el.elementText("pMessage"));
			rows.add(prow);
		}
		tf.setpRows(rows);
		return tf;
	}
	/**
	 * 还款处理异步返回的数据
	 * @param xmlString
	 * @return
	 */
	public static RepaymentAsyn asynXml2Repayment(String xmlString){
		RepaymentAsyn repay=new RepaymentAsyn();
		Document ipsDoc=null;
		try {
			ipsDoc = read(xmlString);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element pReq = ipsDoc.getRootElement();
		repay.setpBidNo(pReq.elementText("pBidNo"));
		repay.setpRepaymentDate(pReq.elementText("pRepaymentDate"));
		repay.setpMerBillNo(pReq.elementText("pMerBillNo"));
		repay.setpIpsBillNo(pReq.elementText("pIpsBillNo"));
		repay.setpIpsDate(pReq.elementText("pIpsDate"));
		repay.setpMemo1(pReq.elementText("pMemo1"));
		repay.setpMemo2(pReq.elementText("pMemo2"));
		repay.setpMemo3(pReq.elementText("pMemo3"));
		Element pDetails=pReq.element("pDetails");
		if(pDetails!=null){
			List<Element> pRows=pDetails.elements("pRow");
			List<RepaymentInvestor> rows=new ArrayList<>();
			for(Element el:pRows){
				RepaymentInvestor investor=new RepaymentInvestor(); 
				investor.setpCreMerBillNo(el.elementText("pCreMerBillNo"));
				investor.setpInAcctNo(el.elementText("pInAcctNo"));
				investor.setpInFee(el.elementText("pInFee"));
				investor.setpStatus(el.elementText("pStatus"));
				investor.setpMessage(el.elementText("pMessage"));
				rows.add(investor);
			}
			repay.setInvestors(rows);
		}
		return repay;
	}
	/**
	 * 读取Xml操作
	 * @param xmlString
	 * @return
	 * @throws DocumentException
	 */
	public static Document read(String xmlString) throws DocumentException{
		SAXReader reader = new SAXReader();
		Document document = reader.read(new ByteArrayInputStream(xmlString.getBytes()));
		return document;
	}
	
	/**
	 * 读取Xml操作
	 * @param xmlString
	 * @return
	 * @throws DocumentException
	 */
	public static Document readOne(String xmlString) throws Exception{
		SAXReader reader = new SAXReader();
		Document document = reader.read(new ByteArrayInputStream(xmlString.getBytes("utf-8")));
		return document;
	}
	/**
	 * 读取彭元征信返回的结果
	 * @param xmlString
	 * @return
	 * @throws Exception
	 */
	public static Document readCreditResult(String xmlString) throws Exception{
		SAXReader reader = new SAXReader();
		Document document = reader.read(new ByteArrayInputStream(xmlString.getBytes("GBK")));
		return document;
	}
	/**
     * 将返回的信息转为Transfer
     * @param xmlString
     * @return
     */
	public static Transfer xml2TransferOne(String xmlString)throws Exception{
		Transfer tf=new Transfer();
		Document ipsDoc=null;
		try {
			ipsDoc = readOne(xmlString);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element pReq = ipsDoc.getRootElement();
		tf.setpMerBillNo(pReq.elementText("pMerBillNo"));
		tf.setpBidNo(pReq.elementText("pBidNo"));
		tf.setpDate(pReq.elementText("pDate"));
		tf.setpTransferType(pReq.elementText("pTransferType"));
		tf.setpTransferMode(pReq.elementText("pTransferMode"));
		tf.setpIpsBillNo(pReq.elementText("pIpsBillNo"));
		tf.setpIpsTime(pReq.elementText("pIpsTime"));
		tf.setpMemo1(pReq.elementText("pMemo1"));
		tf.setpMemo2(pReq.elementText("pMemo2"));
		tf.setpMemo3(pReq.elementText("pMemo3"));
		
		Element pDetails=pReq.element("pDetails");
		List<Element> pRows=pDetails.elements("pRow");
		List<PRow> rows=new ArrayList<>();
		for(Element el:pRows){
			PRow prow=new PRow(); 
			prow.setpOriMerBillNo(el.elementText("pOriMerBillNo"));
			prow.setpTrdAmt(el.elementText("pTrdAmt"));
			prow.setpFIpsAcctNo(el.elementText("pFIpsAcctNo"));
			prow.setpFTrdFee(el.elementText("pFTrdFee"));
			prow.setpTAcctType(el.elementText("pTAcctType"));
			prow.setpTIpsAcctNo(el.elementText("pTIpsAcctNo"));
			prow.setpTTrdFee(el.elementText("pTTrdFee"));
			prow.setpIpsDetailBillNo(el.elementText("pIpsDetailBillNo"));
			prow.setpIpsDetailTime(el.elementText("pIpsDetailTime"));
			prow.setpIpsFee(el.elementText("pIpsFee"));
			prow.setpStatus(el.elementText("pStatus"));
			prow.setpMessage(el.elementText("pMessage"));
			rows.add(prow);
		}
		tf.setpRows(rows);
		return tf;
	}
	
	/**自动投标规则
     * 将返回的信息转为Automatic
     * @param xmlString
     * @return
     */
	public static Automatic xml2Automatic(String xmlString){
		Automatic automatic=new Automatic();
		Document ipsDoc=null;
		try {
			ipsDoc = read(xmlString);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element pReq = ipsDoc.getRootElement();
		/*automatic.setpMerBillNo(pReq.elementText("pMerBillNo"));
		automatic.setpSigningDate(pReq.elementText("pSigningDate"));
		automatic.setpP2PBillNo(pReq.elementText("pP2PBillNo"));
		automatic.setpIpsAuthNo(pReq.elementText("pIpsAuthNo"));
		automatic.setpValidDate(pReq.elementText("pValidDate"));
		automatic.setpSAmtQuota(pReq.elementText("pSAmtQuota"));
		automatic.setpEAmtQuota(pReq.elementText("pEAmtQuota"));
		automatic.setpSIRQuota(pReq.elementText("pSIRQuota"));
		automatic.setpEIRQuota(pReq.elementText("pEIRQuota"));
		automatic.setpIpsTime(pReq.elementText("pIpsTime"));
		automatic.setpMemo1(pReq.elementText("pMemo1"));
		automatic.setpMemo2(pReq.elementText("pMemo2"));
		automatic.setpMemo3(pReq.elementText("pMemo3"));*/
		return null;
	}
}
