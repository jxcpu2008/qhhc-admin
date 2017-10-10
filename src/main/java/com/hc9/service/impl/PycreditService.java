package com.hc9.service.impl;

import java.net.URL;

import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.util.Base64;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.hc9.common.util.CompressStringUtil;
import com.hc9.common.util.ParameterCredit;
import com.hc9.common.util.ParseXML;
import com.hc9.common.util.XmlParsingBean;
import com.hc9.commons.log.LOG;
import com.hc9.model.CardInfo;
import com.hc9.model.Result;
import com.hc9.service.IIDAuthentication;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 鹏元征信身份证验证
 * @author frank
 *
 */
@Service
public class PycreditService implements IIDAuthentication {
	
	
	public Object isRealIDCard(String iname, String initcard) {
		String queryInfo = "";
		CardInfo cardInfo = new CardInfo();
		cardInfo.setName(iname);
		cardInfo.setDocumentNo(initcard);
		cardInfo.setRefID(ParameterCredit.getRefId());
		cardInfo.setSubreportIDs(ParameterCredit.getSubReportId());
		
		try{
			queryInfo = ParseXML.cardXml(cardInfo);
			URL url=new URL(ParameterCredit.getCardUrl());

			Client client = new Client(url);
			Object[] results = client.invoke("queryReport", new Object[] {
					ParameterCredit.getUser(), ParameterCredit.getPassword(), queryInfo,"xml"
			});
			
			if (results[0] instanceof String) {
				// 返回字符串，解析处理字符串内容
				XStream xStream = new XStream(new DomDriver());
				String res=results[0].toString();
				xStream.alias("result", Result.class);
				Result reobj = (Result) xStream.fromXML(res,new Result());
				//解密返回的数据
				byte[] re = Base64.decode(reobj.getReturnValue());
				String xml = CompressStringUtil.decompress(re);
				Document doc= XmlParsingBean.readCreditResult(xml);
				//遍历XML节点
				Element cisReports = (Element) doc.getRootElement();
				Element cisReport = (Element) cisReports.element("cisReport");
				Element policeCheckInfo = (Element) cisReport.element("policeCheckInfo");
				Attribute treatResult=policeCheckInfo.attribute("treatResult");
				//如果查到结果，获取结果值
				if(ParameterCredit.ONE.equals(treatResult.getValue())){
					Element item = (Element) policeCheckInfo.element("item");
					Element result = (Element) item.element("result");
					return result.getText();
				}
				//如果查不到结果，直接返回
				return treatResult.getValue();
			} 
		}catch(Exception e){
			LOG.error(e);
			return ParameterCredit.TWO; 			
		}
		return ParameterCredit.TWO; 

	}
}
