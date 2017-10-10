package com.hc9.service.sms.wd;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.XmlTool;


/***
 * 营销沃动短信Service
 * @author lkl
 */
@Service
public class MarketWdSms {
	/***
	 * 企业id
	 */
	private String userid;
	/***
	 * 用户帐号
	 */
	private String account;
	
	/***
	 * 帐号密码
	 */
	private String password;
	
	@Resource
	private SmsClientAccessTool accessTool;
	
	public void init(String userid, String account, String password) {
		this.userid = userid;
		this.account = account;
		this.password = password;
	}
	
	 /***
     * 营销通道要先调用查询关键字，再进行发送
     * @param content
     * @param telNos
     * @return
     */
	 public Integer sendMarketSMS(String content, String telNos) {
	    	String querySms=accessTool.queryStatusReport(userid,account,password,CommonWdSms.checkkeyWordAction, CommonWdSms.sendMarketUrl, content, true);
	    	int res=-1;
			try {
					System.out.println("沃动营销短信查询返回的xml：--->"+querySms);
					if(!querySms.equals("false")){
							XmlTool xml=new XmlTool();
							xml.SetDocument(querySms);
							String returnstatus = xml.getNodeValue("returnstatus");
							String message = xml.getNodeValue("message");
							if(returnstatus.toUpperCase().trim().equals("SUCCESS")){
					        	   String sendSms=accessTool.sendSms(userid,account,password,CommonWdSms.sendMarketUrl, telNos, content, CommonWdSms.sendAction, "");
					        	   if(!sendSms.equals("false")){
						        		   System.out.println("沃动营销短信发送返回的xml：--->"+sendSms);
							        	   xml.SetDocument(sendSms);
							        	   String returnstatusSend = xml.getNodeValue("returnstatus");
										   String messageSend = xml.getNodeValue("message");
										   if(returnstatusSend.toUpperCase().equals("SUCCESS")){
								 	        	 res=1;
								 	        	 System.out.println("--->沃动短信行业通道服务发送成功！");
								 	         }else{
								 	        	 System.out.println("--->沃动短信行业通道服务发送失败--->原因是："+messageSend);
								 	         }
					        	   }
					         }else{
					        	 System.out.println("--->沃动短信行业通道服务关键字查询发送失败--->原因是："+message);
						    }
					}
			} catch (Exception e) {
				e.printStackTrace();
			}  
			return res;
	    }
	
	
}
