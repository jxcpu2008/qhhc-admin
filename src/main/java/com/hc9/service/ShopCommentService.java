package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.ShopComment;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class ShopCommentService {
	
	@Resource
	private HibernateSupport  dao;
	
	
	/***
	 * 查询店铺评论管理
	 * @param page
	 * @param shop
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Object> getShopComment(PageModel page, ShopComment comment) {
		List datalist = new ArrayList();
		// 统计数据条数sql拼接
		StringBuffer countsql = new StringBuffer("select count(c.id) from shop_comment c  WHERE 1=1 ");
		// 查询数据sql拼接
		StringBuffer sqlbuffer = new StringBuffer(
				" select c.id,c.cmtContent,c.cmtIsShow,c.commentTime,c.cmtReply,c.replyTime,(select u.userName from userbasicsinfo u where u.id=c.commentator_id) "
				+ "as cmtUserName,(select a.username from adminuser a where a.id=c.adminuser_id) as adminUserName ,(select u.userName from userbasicsinfo u where u.id=c.replyer_id) "
				+ "as replyerUserName,(select s.shop_name from shop s where s.id=c.shop_Id) as shopName  from shop_comment c  where 1=1 ");
		sqlbuffer.append(connectionSql(comment.getCommentTime(),comment.getReplyTime()));
		countsql.append(connectionSql(comment.getCommentTime(),comment.getReplyTime()));
		sqlbuffer.append(" order by c.id desc");
		datalist = dao.pageListBySql(page, countsql.toString(),sqlbuffer.toString(), null);
		return datalist;
	}
	
	public String connectionSql(String beginDate,String endDate){
		String sql = "";
		if (beginDate != null && !"".equals(beginDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(c.commentTime, '%Y-%m-%d')>=DATE_FORMAT('"+ beginDate+ "', '%Y-%m-%d') ";
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            sql = sql
                    + " AND DATE_FORMAT(c.commentTime, '%Y-%m-%d')<=DATE_FORMAT('" + endDate + "', '%Y-%m-%d') ";
        }
		return sql;
	}
	
	/***
	 * 查询店铺评论信息
	 * @param id
	 * @return
	 */
	public ShopComment queryShopComment(String id) {
		String sql = "select * From shop_comment WHERE id=?";
		return dao.findObjectBySql(sql, ShopComment.class, id);
	}
	
    /**
     * 批量删除
     * 
     * @param ids
     *            选中的id
     * @return boolean
     */
    public String delShopComment(String ids) {
        String isSuccess = "1";
        try{
        	 if (StringUtil.isNotBlank(ids)) {
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
     			if (delstr.length() > 0) {
     				  String hql = " from ShopComment p where p.id  in("+delstr.substring(0, delstr.length() - 1)+")";
     				  dao.deleteAll(dao.find(hql));
     			}
        	 }else{
        		 isSuccess="2";
        	 }
        }catch(Exception e){
        	 e.printStackTrace();
        	  isSuccess="2";
        }
        return isSuccess;
    }  
    
    /***
     * 修改客服回复信息
     * @param request
     * @param shopComment
     * @param ids
     * @return
     */
    public boolean uptShopComment(HttpServletRequest request,ShopComment shopComment,String ids){
    	boolean isSuccess=true;
    	Adminuser loginuser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
    	try{
    		if (StringUtil.isNotBlank(ids)) {
    			// 根据“，”拆分字符串
    			String[] newids = ids.split(",");
    			// 要修改状态的编号
    			String uptStr = "";
    			for (String idstr : newids) {
    				// 将不是空格和非数字的字符拼接
    				if (StringUtil.isNotBlank(idstr)
    						&& StringUtil.isNumberString(idstr)) {
    					uptStr += idstr + ",";
    				}
    			}
    			StringBuffer sql=new StringBuffer("update shop_comment c set c.cmtReply='"+shopComment.getCmtReply()+"'");
    			sql.append(", c.replyTime='"+DateUtils.format("yyyy-MM-dd HH:mm:ss")+"'");
    			sql.append(", c.adminuser_id="+loginuser.getId());
    			sql.append(" where c.id  ="+uptStr.substring(0, uptStr.length() - 1));
    			 dao.executeSql(sql.toString());
    		}else{
    			isSuccess=false;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		isSuccess=false;
    	}
    	return isSuccess;
    }
    
    
}
