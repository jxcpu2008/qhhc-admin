package com.hc9.common.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * 获取ips资金托管参数
 * 
 * @author RanQiBing 2014-04-18
 * 
 */
public class ParameterIps {

	private static Properties pro = null;
	static {
		try {
			pro = PropertiesLoaderUtils
					.loadAllProperties("config/context/pay/paymentips.properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getMd5ccertificate() {
		return pro.getProperty("MD5CCERTIFICATE05");
	}

	public static String getPublickey() {
		String strs = pro.getProperty("PUBLICKEY05");
		String[] sb = strs.split("\n");
		String crlf = System.getProperty("line.separator");
		StringBuffer str = new StringBuffer(sb[0]).append(crlf);
		str.append(sb[1]).append(crlf);
		str.append(sb[2]).append(crlf);
		str.append(sb[3]).append(crlf);
		str.append(sb[4]).append(crlf);
		str.append(sb[5]).append(crlf);
		return str.toString();
	}

	public static String getDes_algorithm() {
		return pro.getProperty("DES_ALGORITHM05");
	}

	public static String getDesedevector() {
		return pro.getProperty("DESEDEVECTOR05");
	}

	public static String getCert() {
		return pro.getProperty("CERT02");
	}
	
	/**
	 * 商户密钥
	 * @return
	 */
	public static String getmerchantKey() {
		return pro.getProperty("DES_ALGORITHM05");
	}

	public static String getCertificatem() {
		return pro.getProperty("CERTIFICATEMD5");
	}

	public static String getMercode() {
		return pro.getProperty("MERCODE");
	}

	public static String getUrl() {
		return pro.getProperty("URL");
	}

	public static String getAsynchronismurl() {
		return pro.getProperty("ASYNCHRONISMURL");
	}

	public static String getWeburl() {
		return pro.getProperty("WEB_URL");
	}

	public static String getTerminalnuMber() {
		return pro.getProperty("TERMINALNUMBER");
	}

	/*public static boolean pianText(ReturnInfo returnInfo) {
		StringBuffer text = new StringBuffer(returnInfo.getpMerCode());
		text.append(returnInfo.getpErrCode()).append(returnInfo.getpErrMsg())
				.append(returnInfo.getP3DesXmlPara());
		text.append(getMd5ccertificate());

		return IpsCrypto.md5WithRSAVerify(text.toString(),
				returnInfo.getpSign(), getPublickey());

	}*/

}
