package com.hc9.common.util;

import java.security.MessageDigest;

import org.jfree.util.Log;

/** MD5加密算法 */
public class Md5Encrypt {
	
	/** MD5 */
	private static String md5EncryptString(String values) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(values.getBytes("UTF-8"));
		} catch (Exception e) {
			Log.error(values + " 采用MD5加密算法进行加密时报错！", e);;
		}
		
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString();
	}
	
	/** 对参数按后台支付网关的格式要求进行MD5签名 */
	public static String signatureParamInMd5ForPayGateway(String jsonParam) {
		String toEncryptString = jsonParam;
		String signatureInfo = Md5Encrypt.md5EncryptString(toEncryptString);
		return signatureInfo;
	}
}