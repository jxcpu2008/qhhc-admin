package com.hc9.common.listener;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.log4j.Logger;

import com.hc9.common.constant.Constant;
import com.hc9.dao.entity.Userbasicsinfo;

/**   
 * Filename:    UserListener.java   
 * Company:     前海红筹  
 * @version:    3.0   
 * @since:  JDK 1.7.0_25  
 * Create at:   2014年4月8日 上午10:42:49   
 * Description:  统计在线会员工具

 */

public class UserListener implements HttpSessionAttributeListener {
    
    /** 引入log4j日志打印类*/
    private static final Logger logger = Logger.getLogger(UserListener.class);

  //用户登录身份证
    private String USERNAME;
    private UserList u1 = UserList.getInstance(); 
    
    
    /**
    * <p>Title: IsExist</p>
    * <p>Description: 判断用户是否已经登录</p>
    * @param sfz 用户名
    * @return true false
    * @throws Exception
    */
    public boolean IsExist(String sfz)throws Exception
    {
        if (logger.isDebugEnabled()) {
            logger.debug("IsExist(String)方法开始"); //$NON-NLS-1$
        }

        try
        {
           
            boolean returnboolean = u1.IsExist(sfz);
            if (logger.isDebugEnabled()) {
                logger.debug("IsExist(String)方法结束OUTPARAM=" + returnboolean); //$NON-NLS-1$
            }
          return returnboolean;
        }
        catch(Exception ex)
        {
            logger.error("IsExist(String sfz=" + sfz + ")", ex); //$NON-NLS-1$ //$NON-NLS-2$

            ex.printStackTrace();

            if (logger.isDebugEnabled()) {
                logger.debug("IsExist(String)方法结束OUTPARAM=" + false); //$NON-NLS-1$
            }
            return false;
        }
    }
  
     public String getUSERNAME() {
      return USERNAME;
     }
     public void setUSERNAME(String username) {
      USERNAME = username;
     }
 
    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("attributeAdded(HttpSessionBindingEvent)方法开始"); //$NON-NLS-1$
        }

        try{
            if(Constant.SESSION_USER.equals(event.getName())){
             u1.addUser(((Userbasicsinfo)event.getValue()).getUserName());
            }
            }catch(Exception e){
                logger.error("添加当前登录会员到集合失败!attributeAdded(HttpSessionBindingEvent event=" + event + ")", e); //$NON-NLS-1$ //$NON-NLS-2$
            }

        if (logger.isDebugEnabled()) {
            logger.debug("attributeAdded(HttpSessionBindingEvent)方法结束"); //$NON-NLS-1$
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("attributeRemoved(HttpSessionBindingEvent)方法开始"); //$NON-NLS-1$
        }

        try{
            if(Constant.SESSION_USER.equals(event.getName())){
             u1.RemoveUser(((Userbasicsinfo)event.getValue()).getUserName());
            }
            }catch(Exception e){
                logger.error("从集合中移除当前退出登录的用户失败!!!!attributeRemoved(HttpSessionBindingEvent event=" + event + ")", e); //$NON-NLS-1$ //$NON-NLS-2$
            }

        if (logger.isDebugEnabled()) {
            logger.debug("attributeRemoved(HttpSessionBindingEvent)方法结束"); //$NON-NLS-1$
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        // TODO Auto-generated method stub

    }

}
