package com.hc9.common.util;

/**
 * 短信发送文本
 * 
 * @author frank
 * 
 */
public class SMSText {

    /** 短信后缀名称p2Pname*/
    private String p2Pname = "前海红筹";
    /** publishContant*/
    public String publishContant = "尊敬的客户：有新的标上线了：#1#，更多投资信息，请到" + p2Pname
            + "查看。【" + p2Pname + "】";
    /** backContant*/
    public String backContant = "尊敬的客户：您好，您有新的回款，请注意查收。【" + p2Pname + "】";
    
    /**标结束*/
    public String endLoan = "尊敬的客户：您好，标号为#1#，已经结束，请您留意。【" + p2Pname + "】";
}
