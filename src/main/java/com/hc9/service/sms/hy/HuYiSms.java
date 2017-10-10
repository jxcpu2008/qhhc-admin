package com.hc9.service.sms.hy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.hc9.common.log.LOG;
import com.hc9.common.util.XmlTool;
@Service
public class HuYiSms{

	private final String Url="http://10658.cc/webservice/api?method=SendSms"; 
	
	private String account;
	private String pwd;
	private String pid;
	public void init(String account, String pwd,String pid) {
		this.account=account;
		this.pwd=pwd;
		this.pid=pid;
	}
	
	/**
	 * 
	 * @param content 信息内容,通常为70汉字以内，超过限制字数会被分拆，同时扣费会被累计
	 * @param telNos
	 * @return
	 */
	public Integer sendSMS(String content, String telNos) {
		HttpClient client = new HttpClient(); 
		PostMethod method = new PostMethod(Url); 
			
		client.getParams().setContentCharset("UTF-8");
		method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
	    
		//多个手机号码请用英文,号隔开		
		
		NameValuePair[] data = {//提交短信
			    new NameValuePair("account", account), 
			    new NameValuePair("password", pwd), 			    
			    new NameValuePair("mobile", telNos),
			    new NameValuePair("pid", pid),					
			    new NameValuePair("time", null),
				new NameValuePair("content", content),
		};
		method.setRequestBody(data);		
		
		int result=0;
			
			//TODO	挡板
			try {
				client.executeMethod(method);
				System.out.println(method.getResponseBodyAsString());
				LOG.info(method.getResponseBodyAsString());
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				LOG.error("-->互亿网络请求失败: "+e.getMessage());
			} catch (IOException e) {
				LOG.error("-->互亿IO出错: "+e.getMessage());
			}		
//		System.out.println("---->互亿短信提交成功: "+content);
	
		return result;
	}
	//HttpGet httpGet=new HttpGet(uri);
	//HttpResponse response;
	public Integer sendSMSPost(String content, String telNos){
		String url="http://106.ihuyi.cn/webservice/sms.php?method=Submit";

		url+="&account="+account+"&password="+pwd+"&mobile="+telNos+"&content="+content;
		HttpPost httpPost=new HttpPost(url);
		HttpResponse response;
		try {
			//TODO 挡板
			response = new DefaultHttpClient().execute(httpPost);
			HttpEntity entity=response.getEntity();
//			LOG.error(EntityUtils.toString(entity));
			String resultXml=EntityUtils.toString(entity);
//			System.out.println(resultXml);
		} catch (ClientProtocolException e) {
			LOG.error("-->互亿网络请求失败: "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("-->互亿IO出错: "+e.getMessage());
		}
		LOG.error("-->互亿短信: "+content);
		return 0;
	}
	
	public Integer sendSMS2(String content,String tel){
		LOG.error("-->互亿短信: "+tel+content);
		if(true){
			return 0;
		}
		String address="http://106.ihuyi.cn/webservice/sms.php?method=Submit";
		URL url;
		HttpURLConnection connection;
		try {
			url=new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);//允许连接提交信息
			connection.setRequestMethod("POST");//
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);
			StringBuffer sb = new StringBuffer();
			sb.append("account="+account);
			sb.append("&password="+pwd);
			sb.append("&mobile="+tel);
			sb.append("&content="+content);
			OutputStream os = connection.getOutputStream();
			os.write(sb.toString().getBytes());
			os.close();

			String line= "";
			String result = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			if(null==in.readLine()){
				return -1;
			}
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			
			XmlTool xml=new XmlTool();
			xml.SetDocument(result);
			String value=xml.getNodeValue("code");
			int res=0;
			if("2".equals(value)){
				res=0;
			}
			LOG.error("-->互亿短信: "+result);
			return res;
		} catch (IOException e) {
			LOG.error("获取返回信息失败"+e.getMessage());
			return -1;
		}
	}
	public static void main(String[] args){
		HuYiSms sms=new HuYiSms();
		sms.sendSMS2("您的验证码是：1234。请不要把验证码泄露给其他人。", "13632621634");

	}
}
