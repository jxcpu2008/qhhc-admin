package com.hc9.service;

import com.hc9.dao.entity.Borrowersbase;
import com.hc9.dao.entity.Userbasicsinfo;

/**
 * 接款人通用查询
 * 
 * @author Administrator
 * 
 */
public interface BorrowersQuery {

    /**
     * 判断改会员是否是接款人
     * 
     * @param userbasicsinfo 会员对象
     * @return 是 否
     */
    boolean isBorrowsByUser(Userbasicsinfo userbasicsinfo);

    /***
     * 通过用户编号查询出改用户关联的借款人信息
     * @param userbasicsId   用户编号
     * @return 借款人信息
     */
    Borrowersbase queryBorrowersbaseByUserfundId(Long userbasicsId);

}
