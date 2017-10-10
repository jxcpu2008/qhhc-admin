package com.hc9.common.exception;

public class PafaAppException extends RuntimeException {
 
    private String retCd ;  //异常对应的返回码
    private String msgDes;  //异常对应的描述信息
     
    public PafaAppException() {
        super();
    }
 
    public PafaAppException(String message) {
        super(message);
        msgDes = message;
    }
 
    public PafaAppException(String retCd, String msgDes) {
        super();
        this.retCd = retCd;
        this.msgDes = msgDes;
    }
 
    public PafaAppException(String retCd, Exception e) {
        super();
        this.retCd = retCd;
    }
 
    public String getRetCd() {
        return retCd;
    }
 
    public String getMsgDes() {
        return msgDes;
    }
}
