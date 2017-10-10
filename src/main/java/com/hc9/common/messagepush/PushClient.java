package com.hc9.common.messagepush;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
public class PushClient {
	
	private static final Logger logger = Logger.getLogger(PushClient.class);
	
	private HttpClient client = new DefaultHttpClient();
	
	@Value("${push.sendUrlPath}")
	private String sendUrlPath;
	
	@Value("${push.uploadUrlPath}")
	private String uploadUrlPath;
	
	public boolean send(UmengNotification msg) throws Exception {
//		String timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
		Long timestamp = System.currentTimeMillis();
		msg.setPredefinedKeyValue("timestamp", timestamp);
        String postBody = msg.getPostBody();
        String sign = DigestUtils.md5Hex(("POST" + sendUrlPath + postBody + msg.getAppMasterSecret()).getBytes("utf8"));
//        requestUrl = requestUrl + "?sign=" + sign;
        HttpPost post = new HttpPost(sendUrlPath + "?sign=" + sign);
        StringEntity se = new StringEntity(postBody, "UTF-8");
        post.setEntity(se);
        HttpResponse response = client.execute(post);
        int status = response.getStatusLine().getStatusCode();
        logger.debug("调用友盟消息推送API的响应代码为：" + status);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        logger.debug("调用友盟消息推送API的响应主体为：" + result.toString());
        
        if (status == 200) {
        	logger.debug("消息推送成功！");
        } else {
        	logger.debug("消息推送失败！");
        	return false;
        }
        
        return true;
    }

	public String uploadContents(String appkey, String appMasterSecret, String contents) throws Exception {
		JSONObject uploadJson = new JSONObject();
		uploadJson.put("appkey", appkey);
//		String timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
		Long timestamp = System.currentTimeMillis();
		uploadJson.put("timestamp", timestamp);
		uploadJson.put("content", contents);
		String postBody = uploadJson.toString();
		String sign = DigestUtils.md5Hex(("POST" + uploadUrlPath + postBody + appMasterSecret).getBytes("utf8"));
//		requestUrl = requestUrl + "?sign=" + sign;
		HttpPost post = new HttpPost(uploadUrlPath + "?sign=" + sign);
//		logger.debug("############################postUrl = " + uploadUrlPath + "?sign=" + sign);
		StringEntity se = new StringEntity(postBody, "UTF-8");
		post.setEntity(se);
		HttpResponse response = client.execute(post);
		int status = response.getStatusLine().getStatusCode();
        logger.debug("调用友盟文件上传API的响应代码为：" + status);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		logger.debug("调用友盟文件上传API的响应主体为：" + result.toString());
		JSONObject respJson = JSONObject.parseObject(result.toString());
		String ret = respJson.getString("ret");
		JSONObject data = respJson.getJSONObject("data");
		if (!ret.equals("SUCCESS")) {
			logger.debug("上传文件失败：错误代码 [" + data.getString("error_code") + "]");
			return "";
		}
		return data.getString("file_id");
	}
}