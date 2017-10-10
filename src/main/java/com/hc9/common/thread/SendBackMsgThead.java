package com.hc9.common.thread;

import com.hc9.dao.entity.Loansign;
import com.hc9.service.MessagesettingService;

/**
 * 借款标回款发送消息线程
 * 
 * @author frank
 * 
 */
public class SendBackMsgThead extends Thread {

    /** loansign 借款标对象 */
    private Loansign loansign;
    /** contant 内容 */
    private String contant;
    /** messagesettingService 服务层 */
    private MessagesettingService messagesettingService;

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param loansign
     *            借款标对象
     * @param contant
     *            内容
     * @param messagesettingService
     *            服务层
     */
    public SendBackMsgThead(Loansign loansign, String contant,
            MessagesettingService messagesettingService) {
        this.loansign = loansign;
        this.contant = contant;
        this.messagesettingService = messagesettingService;
    }

    @Override
    public void run() {
        try {
            messagesettingService.sendBackMessaget(loansign, contant, "回款通知");
        } catch (Exception e) {
        } finally {
            this.stop();
        }
    }
}
