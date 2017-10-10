package com.hc9.common.exception;

public class ServicesException extends RuntimeException {
 
    private String retCd ;  //异常对应的返回码
    private String msgDes;  //异常对应的描述信息
     
    public ServicesException() {
        super();
    }
 
    public ServicesException(String message) {
        super(message);
        msgDes = message;
    }
 
    public ServicesException(PafaAppException ee) {
        super(ee);
    }
 
    public ServicesException(String retCd, String msgDes) {
        super();
        this.retCd = retCd;
        this.msgDes = msgDes;
    }
 
    public String getRetCd() {
        return retCd;
    }
 
    public String getMsgDes() {
        return msgDes;
    }
}
