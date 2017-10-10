package com.hc9.common.listener;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import com.hc9.common.util.StringUtil;

/**   
 * Filename:    UserList.java   
 * Company:     前海红筹  
 * @version:    3.0   
 * @since:  JDK 1.7.0_25  
 * Description:  统计当前登录会员

 */

public class UserList {
    private static final UserList userList = new UserList();
    private Vector v = new Vector();
    private int maxUser;
   
    private UserList()
    {
        //v = new Vector();
    }
    
    public static UserList getInstance()
    {
        return userList;
    }
    //将用户登陆身份证保存到Vector中
    public void addUser(String sfz) throws Exception
    {
        try{
           if (StringUtil.isNotBlank(sfz))
           {
               if (  v.indexOf(sfz) >= 0)//判断是否已经存在
                   return ;                 
               //可能的操作
               //添加登录ID
               v.addElement(sfz);
               if(getUserCount()>maxUser){
                maxUser=getUserCount();
               }
           }
        }
         catch(Exception ex)
        {
            ex.printStackTrace();  
        }
        finally{
        }
    }
   
    public boolean IsExist(String sfz)throws Exception
    {
        try{
             if (  v.indexOf(sfz) >= 0)
                   return true;              
            return false;
        }
        catch(Exception ex)
        {
           ex.printStackTrace();
            return false;
        }
    }
   
    //删除用户登录ID
    public void RemoveUser(String sfz)throws Exception
    {
        try{
           if (StringUtil.isNotBlank(sfz))
           { 
              //修改数据库
              //移除用户登录ID
               v.removeElement(sfz);
           }
        }
        catch(Exception ex)
        {    
            ex.printStackTrace(); //写日志
        }
        finally{
        }
    }
    //返回Vector枚举
    public Enumeration getUserList()
    {
        return v.elements();
    }
    //返回迭代器
    public Iterator getUserListItera(){
     return v.iterator();
    }
    //返回在线人数
    public int getUserCount()
    {
        return v.size();
    }
    //返回在线人数峰值
    public int getMaxUser(){
     return maxUser;
    }
}
