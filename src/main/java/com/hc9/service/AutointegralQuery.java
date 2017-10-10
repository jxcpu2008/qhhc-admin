package com.hc9.service;

import com.hc9.dao.entity.Manualintegral;
import com.hc9.dao.entity.Userbasicsinfo;

/**
 * 积分的通用接口
 * 
 * @author Administrator
 * 
 */
public interface AutointegralQuery {

    /**
     * 通过还款金额求出对应的积分值
     * 
     * @param money  还款金额
     * @return 积分
     */
    int calculationIntegral(double money);

    /**
     * 通过用户找到他的手动积分记录
     * 
     * @param userinfo 用户对象
     * @return 积分
     */
    Manualintegral queryManuaByuser(Userbasicsinfo userinfo);

    /**
     * 通过会员编号查询到会员的积分总额 =自动积分总和+手动积分
     * 
     * @param userinfo 用户对象
     * @return 积分总额
     */
    int queryAllIntegral(Userbasicsinfo userinfo);

    /**
     * 求到该用户的自动积分总和
     * 
     * @param userinfo 用户对象
     * @return 积分总和
     */
    int queryAutoSUMIntegral(Userbasicsinfo userinfo);

    /*****
     * 通过管理员勾选的信息，进行计算手动积分合计
     * 
     * @param manualin  手动积分对象
     * @return 积分合计
     */
    int getALLBYOneSerch(Manualintegral manualin);

}
