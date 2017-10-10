package com.hc9.service.sms.wd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.stereotype.Service;

/***
 * 沃动短信处理的公共方法
 * @author lkl
 */
@Service
public class SmsClientAccessTool {

	private static SmsClientAccessTool smsClientToolInstance;

	/**
	 * 采用单列方式来访问操作
	 * 
	 * @return
	 */
	public static synchronized SmsClientAccessTool getInstance() {

		if (smsClientToolInstance == null) {
			smsClientToolInstance = new SmsClientAccessTool();
		}
		return smsClientToolInstance;
	}
	
	/**
	 * <span>发送信息最终的组合形如：http://118.145.30.35/sms.aspx?action=send</span>
	 * @param url
	 *            ：必填--发送连接地址URL--比如>http://118.145.30.35/sms.aspx
	 * @param userid
	 *            ：必填--用户ID，为数字
	 * @param account
	 *            ：必填--用户帐号
	 * @param password
	 *            ：必填--用户密码
	 * @param mobile
	 *            ：必填--发送的手机号码，多个可以用逗号隔比如>13512345678,13612345678
	 * @param content
	 *            ：必填--实际发送内容，
	 * @param action
	 *            ：选填--访问的事件，默认为send
	 * @param sendTime
	 *            ：选填--定时发送时间，不填则为立即发送，时间格式如>2011-11-11 11:11:11
	 */
	public  String sendSms(String userid,String account,String password,String url, String mobile, String content, String action,String sendTime) {
		try {
			StringBuffer send = new StringBuffer();
			if (action != null && !action.equals("")) {
				send.append("action=").append(action);
			} else {
				send.append("action=send");
			}
			send.append("&userid=").append(userid);
			send.append("&account=").append(URLEncoder.encode(account, "UTF-8"));
			send.append("&password=").append(URLEncoder.encode(password, "UTF-8"));
			send.append("&mobile=").append(mobile);
			send.append("&content=").append(URLEncoder.encode(content, "UTF-8"));
			if (sendTime != null && !sendTime.equals("")) {
				send.append("&sendTime=").append(URLEncoder.encode(sendTime, "UTF-8"));
			}
			send.append("&extno=").append("");
			System.out.println("沃动短信发送："+send);
			return SmsClientAccessTool.getInstance().doAccessHTTPPost(url,send.toString(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("沃动短信未发送，编码异常");
			return "false";
		}
	}
	
	/**
	 * 其一：发送方式，默认为POST<br/>
	 * 其二：发送内容编码方式，默认为UTF-8
	 * @param  action
	 *             :任务名称 
	 * @param url
	 *            ：必填--发送连接地址URL
	 * @param userid
	 *            ：必填--用户ID，为数字
	 * @param account
	 *            ：必填--用户帐号
	 * @param password
	 *            ：必填--用户密码
	 * @param checkWord
	 *              :检测发送内容(根据不同的action判断是否必填)
	 * @param isContent
	 *             ：=true 是非法关键字查询  
	 * @return 返回状态报告
	 */
	public  String queryStatusReport(String userid,String account,String password,String action,String url,String checkWord,boolean isContent) {
		try {
			StringBuffer sendParam = new StringBuffer();
			sendParam.append("action=").append(URLEncoder.encode(action, "UTF-8"));
			sendParam.append("&userid=").append(userid);
			sendParam.append("&account=").append(URLEncoder.encode(account, "UTF-8"));
			sendParam.append("&password=").append(URLEncoder.encode(password, "UTF-8"));
			if(isContent){
					if (checkWord != null && !checkWord.equals("")) {
						sendParam.append("&content=").append(
								URLEncoder.encode(checkWord, "UTF-8"));
					} else {
						System.out.println("需要检查的字符串不能为空");
						return "false";
					}
			}
			System.out.println("沃动短信查询发送："+sendParam);
			return SmsClientAccessTool.getInstance().doAccessHTTPPost(url,sendParam.toString(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("沃动短信查询未发送，异常-->" + e.getMessage());
			return "false";
		}
	}

	/**
	 * <p>
	 * POST方法
	 * </p>
	 * 
	 * @param sendUrl
	 *            ：访问URL
	 * @param paramStr
	 *            ：参数串
	 * @param backEncodType
	 *            ：返回的编码
	 * @return
	 */
	public String doAccessHTTPPost(String sendUrl, String sendParam,
			String backEncodType) {

		StringBuffer receive = new StringBuffer();
		BufferedWriter wr = null;
		try {
			if (backEncodType == null || backEncodType.equals("")) {
				backEncodType = "UTF-8";
			}

			URL url = new URL(sendUrl);
			HttpURLConnection URLConn = (HttpURLConnection) url.openConnection();
			URLConn.setDoOutput(true);
			URLConn.setDoInput(true);
			((HttpURLConnection) URLConn).setRequestMethod("POST");
			URLConn.setUseCaches(false);
			URLConn.setAllowUserInteraction(true);
			HttpURLConnection.setFollowRedirects(true);
			URLConn.setInstanceFollowRedirects(true);

			URLConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			URLConn.setRequestProperty("Content-Length", String
					.valueOf(sendParam.getBytes().length));

			DataOutputStream dos = new DataOutputStream(URLConn
					.getOutputStream());
			dos.writeBytes(sendParam);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					URLConn.getInputStream(), backEncodType));
			String line;
			while ((line = rd.readLine()) != null) {
				receive.append(line).append("\r\n");
			}
			rd.close();
		} catch (java.io.IOException e) {
			receive.append("沃动短信发送访问产生了异常-->").append(e.getMessage());
			e.printStackTrace();
		} finally {
			if (wr != null) {
				try {
					wr.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				wr = null;
			}
		}

		return receive.toString();
	}

	public String doAccessHTTPGet(String sendUrl, String backEncodType) {

		StringBuffer receive = new StringBuffer();
		BufferedReader in = null;
		try {
			if (backEncodType == null || backEncodType.equals("")) {
				backEncodType = "UTF-8";
			}

			URL url = new URL(sendUrl);
			HttpURLConnection URLConn = (HttpURLConnection) url
					.openConnection();

			URLConn.setDoInput(true);
			URLConn.setDoOutput(true);
			URLConn.connect();
			URLConn.getOutputStream().flush();
			in = new BufferedReader(new InputStreamReader(URLConn
					.getInputStream(), backEncodType));

			String line;
			while ((line = in.readLine()) != null) {
				receive.append(line).append("\r\n");
			}

		} catch (IOException e) {
			receive.append("沃动短信发送访问产生了异常-->").append(e.getMessage());
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (java.io.IOException ex) {
					ex.printStackTrace();
				}
				in = null;

			}
		}
		return receive.toString();
	}
}
