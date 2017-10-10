package com.hc9.service.sms.mw;

/**
 * 获取soap请求连接的接口类
 * @author Administrator
 *
 */
public interface ISmsSoapService extends javax.xml.rpc.Service {
    public java.lang.String getSmsSoapAddress();

    public ISmsSoap getSmsSoap() throws javax.xml.rpc.ServiceException;

    public ISmsSoap getSmsSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
