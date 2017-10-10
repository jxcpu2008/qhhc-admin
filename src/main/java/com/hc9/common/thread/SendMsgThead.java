package com.hc9.common.thread;

import java.util.List;

import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.entity.Userrelationinfo;
import com.hc9.service.MessagesettingService;

/**
* <p>Title:SendMsgThead</p>
* <p>Description: 新标上线提醒发送消息</p>
* <p>Company: 前海红筹</p>
* @author Frank
* <p>date 2014年9月4日</p>
*/
public class SendMsgThead extends Thread {

    /** list 集合 */
    private List<Userrelationinfo> list;
    /** contant 内容 */
    private String contant;
    /** messagesettingService 服务层 */
    private MessagesettingService messagesettingService;
    /**信息标题*/
    private String title;
    /**信息类型*/
    private Long typeId;
    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param list  要发送的会员列表
     * @param contant 内容
     * @param messagesettingService 服务层
     */
    public SendMsgThead(List<Userrelationinfo> list, String contant,String title,Long typeId,
            MessagesettingService messagesettingService) {
        this.list = list;
        this.contant = contant;
        this.messagesettingService = messagesettingService;
        this.typeId=typeId;
        this.title=title;
    }
    
    public SendMsgThead(Userbasicsinfo user,String contant,MessagesettingService messagesettingService){
    	
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
	@Override
    public void run() {
        try {
            for (Userrelationinfo userrelationinfo : list) {
                messagesettingService.sendMessagetting(userrelationinfo, typeId, contant, title);
            }
        } catch (Exception e) {
        } finally {
            this.stop();
        }
    }
}
