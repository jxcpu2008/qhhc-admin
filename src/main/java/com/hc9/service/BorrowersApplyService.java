package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.BorrowersApply;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**   
 * Filename:    BorrowersApplyService.java   
 * Company:     前海红筹  
 * @version:    1.0   
 * @since:  JDK 1.7.0_25  
 * Description:  借款人申请记录
 *   
 * Modification History:   
 * 时间    			作者   	   	版本     		描述 
 * ----------------------------------------------------------------- 
 * 2014年4月14日 	LiNing      1.0     	1.0Version   
 */

@Service
public class BorrowersApplyService {

    @Resource
    private HibernateSupport dao;
    
    /**
    * <p>Title: queryPage</p>
    * <p>Description: 查询借款申请列表</p>
    * @param pageModel 分页模型
    * @return 查询结果
    */
    public List<Object> queryPage(PageModel pageModel,BorrowersApply borr){
    	  List datalist = new ArrayList();
          
          
    	     

          // 统计数据条数sql拼接
          StringBuffer countsql = new StringBuffer(
                  "SELECT count(1) FROM  ");    
          countsql.append(" (userbasicsinfo  INNER JOIN borrowers_apply  on borrowers_apply.user_id=userbasicsinfo.id) INNER JOIN userrelationinfo  ON userbasicsinfo.id=userrelationinfo.user_id  ");
          countsql.append(" WHERE 1=1 ");
          
         
          // 查询数据sql拼接
          StringBuffer sqlbuffer = new StringBuffer(
        		  "SELECT borrowers_apply.id,userbasicsinfo.`name`,userrelationinfo.phone,borrowers_apply.money,borrowers_apply.time,borrowers_apply.`status`,"
        		  + " borrowers_apply.behoof,borrowers_apply.borrowmonth,borrowers_apply.corporatename,borrowers_apply.telphone,(select username from adminuser where id=borrowers_apply.adminuser_idborr) as adminuser "
        		 +" from (userbasicsinfo   INNER JOIN borrowers_apply  on userbasicsinfo.id=borrowers_apply.user_id) INNER JOIN userrelationinfo  ON userbasicsinfo.id=userrelationinfo.user_id where 1=1");
                  
          if (null != borr.getStatus()) {
          	sqlbuffer.append("  and borrowers_apply.`status`="+borr.getStatus());
          	countsql.append(" and borrowers_apply.`status`="+borr.getStatus());         
          }else{
          	 sqlbuffer.append(" and borrowers_apply.`status`>0  ");
          	 countsql.append(" and borrowers_apply.`status`>0 ");
          }
          
          sqlbuffer.append(" ORDER BY borrowers_apply.`status` ASC");

     
          datalist = dao.pageListBySql(pageModel, countsql.toString(),sqlbuffer.toString(), null);

          return datalist;       
    }
    
    /**
    * <p>Title: updateApplyStatus</p>
    * <p>Description: 修改借款申请状态</p>
    * @param ids 主键
    * @param status 状态
    * @return 修改结果 true、false
    */
    public boolean updateApplyStatus(String ids,String status,String remark){
        
        int result=0;
        boolean flag=true;
        
        String sql="UPDATE borrowers_apply SET `status`="+status+",remark='"+remark+"' WHERE id="+ids;
        
        result=dao.executeSql(sql);
        
        if(result<=0){
            flag=false;
        }
        return flag;
        
    }
    
    /**
    * <p>Title: updatesApply</p>
    * <p>Description: 批量修改借款申请状态</p>
    * @param ids 要修改状态的申请编号
    * @param status 要修改成的状态
    * @return 修改结果 true false
    */
    public boolean updatesApply(String ids,String status,HttpServletRequest request){
        
    	Adminuser loginuser = (Adminuser) request.getSession()
                .getAttribute(Constant.ADMINLOGIN_SUCCESS);
    	boolean flag=false;
    	if(StringUtil.isNotBlank(ids)&&StringUtil.isNotBlank(status)&&StringUtil.isNumberString(status)){
            // 根据“，”拆分字符串
               String[] newids = ids.split(",");
               // 要修改状态的编号
               String delstr = "";
               for (String idstr : newids) {
                   // 将不是空格和非数字的字符拼接
                   if (StringUtil.isNotBlank(idstr)
                           && StringUtil.isNumberString(idstr)) {
                       delstr += idstr + ",";
                   }
               }
                                        
               if(StringUtil.isNotBlank(delstr)){
                   String sql="UPDATE borrowers_apply SET borrowers_apply.`status`="+status
                   		+ ",borrowers_apply.adminuser_idborr="+loginuser.getId()
                           +" WHERE borrowers_apply.id IN ("+
                           delstr.substring(0,delstr.length()-1)+")";
                   //修改状态 
                  dao.executeSql(sql);
                  flag=true;
                  
                  if(status.equals("2")){
            		  // 先判断是否全部都能审核
                      StringBuffer updatesqls = new StringBuffer(
                              " UPDATE  userbasicsinfo set userbasicsinfo.isCrowdfundingPerson=1  , userbasicsinfo.isCreditor=2 WHERE (userbasicsinfo.id= (SELECT borrowers_apply.user_id from borrowers_apply WHERE borrowers_apply.id IN (" )
                              .append(ids.substring(0, ids.length() - 1)+")))");
                      if(dao.executeSql(updatesqls.toString()) <= 0){
                    	  return false;
                      } 
            	  }
            	 
               }       
           }
           
           return flag;
        
    }
    /**
     * <p>Title: updatesApply</p>
     * <p>Description: 融资申请</p>
     * @param borrowersApply 融资申请信息
     * @param nickname 昵称
     * @return 修改结果 true false
     */
     public void borrowersApply(BorrowersApply borrowersApply){
         try {
        	 if(borrowersApply.getId()!=null){
        		 dao.update(borrowersApply);
        	 }else{
        	 dao.save(borrowersApply);   	 
        	 }
        	 
		} catch (DataAccessException e) {
			System.out.println("融资申请提交失败");
		}
         
         
     }
    
}
