package com.hc9.model;

import java.util.ArrayList;
import java.util.List;

import com.hc9.common.constant.Constant;

/**
 * 访问者请求队列(测试)
 * 
 * @author 刘道冬
 * 
 */
public class Visitor {

    /**
     * 请求时间队列
     */
    private List<Long> list = new ArrayList<Long>();

    /**
     * 是否为快速请求
     * @return  是/否
     */
    public synchronized boolean isQuickRequest() {

        insert();
        int size = list.size();

        if (size >= Constant.NUMBER_MAX_QUICK_REQUEST_COUNT
                && (list.get(size - 1) - list.get(0)) < Constant.NUMBER_MAX_QUICK_REQUEST_TIME) {
            return true;
        }

        return false;

    }

    /**
     * 添加一次请求时间
     */
    private void insert() {

        if (list.size() >= Constant.NUMBER_MAX_QUICK_REQUEST_COUNT) {// 数组已经满了

            list.remove(0);
        }

        list.add(System.currentTimeMillis());
    }

}
