package com.hc9.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.hc9.common.log.LOG;

/**
 * 宝付通用加密工具
 * 
 * @author frank
 * 
 */
public class CommonUtil {

	/**
	 * AES 加密算法
	 * 
	 * @param values
	 * @param key
	 * @return
	 */
	public static String aesEncryptKey16(String content, String key) {

		if (isEmpty(key) || key.length() != 16) {
			throw new RuntimeException("密钥长度为16位");
		}
		try {
			String iv = key;
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			int blockSize = cipher.getBlockSize();
			byte[] dataBytes = content.trim().getBytes("utf-8");
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength
						+ (blockSize - (plaintextLength % blockSize));
			}
			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);
			return byte2Hex(encrypted);

		} catch (Exception e) {
			throw new RuntimeException("aes加密发生错误", e);
		}
	}

	/***
	 * 解析aes
	 * @param encryptContent
	 * @param password
	 * @return
	 */
	  public static String aesDecryptKey16(String encryptContent, String password) {
	    if ((isEmpty(password)) || (password.length() != 16))
	      throw new RuntimeException("密钥长度为16位");
	    try
	    {
	      String key = password;
	      String iv = password;
	      byte[] encrypted1 = hex2Bytes(encryptContent);
	      Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
	      SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
	      IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
	      cipher.init(2, keyspec, ivspec);
	      byte[] original = cipher.doFinal(encrypted1);
	      return new String(original, "utf-8").trim();
	    } catch (Exception e) {
	      e.printStackTrace();
	      throw new RuntimeException("aes解密发生错误", e);
	    }
	  }

	/**
	 * MD5
	 * 
	 * @param values
	 * @return
	 */
	public static String MD5(String values) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(values.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
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

	/**
	 * 请求API，并获取返回结果
	 * 
	 * @param requestUrl
	 * @param nvps
	 * @return
	 */
	public static String excuteRequest(String requestUrl, List<NameValuePair> nvps) {
		LOG.error("开始调用宝付接口，requestUrl：" + requestUrl);
		StringBuffer sb = new StringBuffer("");
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(requestUrl);
		try {
			nvps.add(new BasicNameValuePair("terminal_id", ParameterIps.getTerminalnuMber())); // 测试-终端号
			nvps.add(new BasicNameValuePair("merchant_id", ParameterIps.getCert())); // 测试-商户号
			post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse response = client.execute(post);
			LOG.error("宝付接口返回的状态码："+ response.getStatusLine().getStatusCode());
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				sb.append(temp);
			}
			reader.close();
		} catch (Exception e) {
			LOG.error(" 调用保付接口出现异常", e);
			//TODO:发短信邮件
		} finally {
			try {
				post.releaseConnection();
				client.getConnectionManager().closeExpiredConnections();
				client.getConnectionManager().closeIdleConnections(0, TimeUnit.SECONDS);
			} catch(Exception e) {
				LOG.error("释放http连接报错！", e);
			}
		}
		String result = sb.toString();
		LOG.error("调用宝付接口返回结果为：" + result);
		return result;
	}

	/**
	 * 对象 是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(Object str) {
		return (str == null || "".equals(str));
	}

	/**
	 * 将byte[] 转换成字符串
	 */
	public static String byte2Hex(byte[] srcBytes) {
		StringBuilder hexRetSB = new StringBuilder();
		for (byte b : srcBytes) {
			String hexString = Integer.toHexString(0x00ff & b);
			hexRetSB.append(hexString.length() == 1 ? 0 : "").append(hexString);
		}
		return hexRetSB.toString();
	}


	/**
	 * 返回md5加密的字符串
	 * 
	 * @param returnResult
	 * @return
	 */
	public static String getMd5sign(String returnResult) {
		return MD5(returnResult + "~|~" + ParameterIps.getDes_algorithm());
	}

	/**
	 * 将字符串转换成byte[] 
	 */
	 public static byte[] hex2Bytes(String source) {
		    byte[] sourceBytes = new byte[source.length() / 2];
		    for (int i = 0; i < sourceBytes.length; ++i)
		      sourceBytes[i] = (byte)Integer.parseInt(
		        source.substring(i * 2, i * 2 + 2), 16);

		    return sourceBytes;
		  }
}
