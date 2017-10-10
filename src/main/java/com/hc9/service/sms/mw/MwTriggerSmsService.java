package com.hc9.service.sms.mw;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.hc9.common.util.StringUtil;
import com.hc9.commons.log.LOG;
import com.hc9.model.MULTIX_MT;
import com.hc9.model.MwContext;

/**
 * 梦网触发短信发送服务
 * 
 * @author frank
 * 
 */
public class MwTriggerSmsService {

    /** 序列号     */
    String uid;
    /** 密码     */
    String pwd;
    /**通道,可填完整,可不填,可填*,可只填扩展*/
    String channel;

    @Resource
    private ISms mwSms;

    /**
     * 构造函数
     */
    public MwTriggerSmsService() {
    }

    /**
     * 构造函数
     * 
     * @param username
     *            用户名
     * @param password
     *            密码
     * @param etc
     *            其他
     */
    public MwTriggerSmsService(String uid, String pwd,String channel) {
        this.uid = uid;
        this.pwd = pwd;
        this.channel=channel;
    }

    /**
     * 初始化
     * @throws Exception    异常
     */
    public void init() throws Exception {
    	mwSms.init(uid, pwd);
    	System.out.println("--->初始化梦网短信服务成功！");
        LOG.info("--->初始化梦网短信服务成功！");
    }

    /**
     * 发送短信 相同内容群发
     * @param strMobiles 目标号码，用英文逗号(,)分隔，最大100个号码。
     * @param strMessage 短信内容， 内容长度不大于350个汉字
     * @param strSubPort 扩展子号
     * @param strUserMsgId 用户自编流水号
     * @param bKeepAlive 是否保持长连接
     * @param connection 连接对象,如果是短连接，传null
     * @return 0:成功 非0:返回webservice接口返回的错误代码
     * @throws Exception
     */
    public int sendSMSBySameMessage(String strMobiles,String strMessage)  {
    	StringBuffer strPtMsgId=new StringBuffer();
    	strPtMsgId.append(StringUtil.pMerBillNo());
    	String strSubPort=this.channel;//扩展子号，不需要扩展子号请填星号“*”，长度不能超过6位，通道号总长度不能超过20位。
    	String strUserMsgId=StringUtil.pMerBillNo();
    	boolean bKeepAlive=false;
    	Object connection=null;
    	LOG.error("梦网触发短信发送--->");
        return mwSms.SendSms(strPtMsgId,strMobiles,strMessage,strSubPort,strUserMsgId,bKeepAlive,connection);
    }
    
    /**
     * 发送短信 不同内容群发
     * @param mts 批量请求包
     * @param bKeepAlive
     * @param connection
     * @return
     */
    public int sendSMSByDifferentMessager(List<MwContext> contextList){
    	StringBuffer strPtMsgId=new StringBuffer();
    	strPtMsgId.append(StringUtil.pMerBillNo());
    	List<MULTIX_MT> mts=getMultixMt(contextList);
    	boolean bKeepAlive=false;
    	Object connection=null;
    	LOG.error("梦网触发短信发送--->");
    	return mwSms.SendMultixSms(strPtMsgId, mts, bKeepAlive, connection);
    }
    
    
    public List<MULTIX_MT> getMultixMt(List<MwContext> contextList){
    	List<MULTIX_MT> mts=new ArrayList<>(contextList.size()); 
    	
    	String userMsgId=StringUtil.pMerBillNo();//流水号
    	for(int i=0; i<contextList.size();i++){
    		MwContext item=contextList.get(i);
    		MULTIX_MT mt=new MULTIX_MT();
    		mt.setStrMobile(item.getPhone());
    		String strBase64Msg="";
			try {
				strBase64Msg = new String(item.getMessage().getBytes(),"GBK");
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				LOG.error("转码失败"+e.getMessage());
			}
			String strSpNumber = this.channel+i;
			String strUserMsgId=userMsgId+i;
			
    		mt.setStrBase64Msg(strBase64Msg);
    		mt.setStrSpNumber(strSpNumber.toString());
    		mt.setStrUserMsgId(strUserMsgId);//流水编号
    		mts.add(mt);
    	}
    	
    	return mts;
    }
    
}
