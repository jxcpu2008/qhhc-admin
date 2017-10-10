package com.hc9.service.sms.wd;


import javax.annotation.Resource;

import com.hc9.common.util.XmlTool;

/***
 * 沃动短信触发service
 * @author lkl
 */
public class IndustryWdSms {
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
	
	/***
     * 触发短信发送
     * returnstatus 返回状态值：成功返回Success 失败返回：Faild
     * message 返回值：成功返回ok，失败会返回失败原因
     * @param content
     * @param telNos
     * @return
     */
    public Integer sendIndustrySMS(String content, String telNos) {
    	String sendSms=accessTool.sendSms(userid,account,password,CommonWdSms.sendIndustryUrl, telNos, content, CommonWdSms.sendAction, "");
    	int res=-1;
    	try {
    		System.out.println("沃动触发短信发送返回的xml：--->"+sendSms);
    		if(!sendSms.equals("false")){
    			XmlTool xml=new XmlTool();
    			xml.SetDocument(sendSms);
    			String returnstatus = xml.getNodeValue("returnstatus");
    			String message = xml.getNodeValue("message");
    	         if(returnstatus.toUpperCase().equals("SUCCESS")){
    	        	 res=1;
    	        	 System.out.println("--->沃动短信触发通道服务发送成功！");
    	         }else{
    	        	 System.out.println("--->沃动短信触发通道服务发送失败--->原因是："+message);
    	         }
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}  
    	return res;
    }
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
