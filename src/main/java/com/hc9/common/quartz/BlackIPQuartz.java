package com.hc9.common.quartz;

import javax.annotation.Resource;

import com.hc9.service.BlackIPService;

/**
 * 黑名单刷新定时器（测试）
 * 
 * @author frank
 * 
 */
public class BlackIPQuartz {

    /**
     * 注入黑名单服务
     */
    @Resource
    private BlackIPService serviceBlackIps;

    /**
     * 运行
     */
    public void run() {

        serviceBlackIps.init();

    }

}
