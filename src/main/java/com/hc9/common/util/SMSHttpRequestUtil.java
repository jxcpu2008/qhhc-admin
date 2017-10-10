package com.hc9.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.hc9.common.json.JsonUtil;
import com.hc9.common.log.LOG;
import com.hc9.model.RequestVo;

/** http请求通用工具类 */
public class SMSHttpRequestUtil {
	private static String requestUrl = "";
	
	/**
	 * 请求后台短信邮件网关接口，并获取返回json格式字符串
	 * 
	 * @param requestUrl 待发送的url请求地址
	 * @param nvps 待发送请求的参数信息
	 * @return String json格式的返回结果
	 */
	private static String sendMessageGatewayRequest(String jsonParam) {
		if(StringUtil.isBlank(requestUrl)) {
			try {
				Properties pro = PropertiesLoaderUtils.loadAllProperties("config/user/smsInfo.properties");
//				requestUrl = "http://192.168.10.249/smsemail/smsEmailService"; 
				requestUrl=pro.getProperty("hc9.smsemail.service.url");
			} catch (IOException e) {
				LOG.error("短信邮件后台服务配置错误！", e);
			}
		}
		LOG.error("开始调用后台消息网关服务接口，requestUrl：" + requestUrl);
		StringBuffer sb = new StringBuffer("");
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(requestUrl);
		try {
			/** 按照后台接口要求获取参数签名信息 */
			String signatureInfo = Md5Encrypt.signatureParamInMd5ForPayGateway(jsonParam);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("signInfo", signatureInfo));//MD5签名结果 
			nvps.add(new BasicNameValuePair("jsonParam", jsonParam));
			post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse response = client.execute(post);
			LOG.error("调用后台消息网关服务接口返回的状态码："+ response.getStatusLine().getStatusCode());
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				sb.append(temp);
			}
			reader.close();
		} catch (Exception e) {
			LOG.error("调用后台消息网关服务出现异常", e);
			throw new RuntimeException("调用后台消息网关服务出现异常");
		} finally {
			try {
				post.releaseConnection();
				client.getConnectionManager().closeExpiredConnections();
				client.getConnectionManager().closeIdleConnections(0, TimeUnit.SECONDS);
			} catch(Exception e) {
				LOG.error("发送短信相关释放http连接报错！", e);
			}
		}
		String result = sb.toString();
		LOG.error("调用后台消息网关服务返回结果为：" + result);
		return result;
	}
	
	/** 发送单条短信 */
	public static String sendSingleSms(String methodName, Map<String, String> paramMap) {
		RequestVo requestVo = new RequestVo();
		requestVo.setMethodName(methodName);
		requestVo.setParamMap(paramMap);
		String jsonParam = JsonUtil.toJsonStr(requestVo);
		return sendMessageGatewayRequest(jsonParam);
	}
}