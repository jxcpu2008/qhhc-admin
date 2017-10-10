package com.hc9.model;

/**
 * 短信结果
 * @author frank
 *
 */
public class SmsResult {

    /**
     * success
     */
    private boolean success;
    
    /**
     * result
     */
    private Object  result;
    
    /**
     * msg
     */
    private String  msg;
    
    /**
     * 构造函数
     * @param success   是否发送成功
     * @param result    结果对象
     * @param msg       消息
     */
    public SmsResult(boolean success,Object result,String  msg) {
        this.success = success;
        this.result = result;
        this.msg = msg;
    }
    
    /**
     * 判断是否成功
     * @return  是否成功
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * 得到结果对象
     * @return  对象
     */
    public Object getResult() {
        return result;
    }
    
    /**
     * 得到消息
     * @return  消息
     */
    public String getMsg() {
        return msg;
    }
    
}
