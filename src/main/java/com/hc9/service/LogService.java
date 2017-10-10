package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.Log;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * 后台用户登陆日志
 * 
 * @author ransheng
 * 
 */
@Service
@SuppressWarnings(value = { "rawtypes" })
public class LogService {

    /**
     * 通用dao
     */
    @Resource
    private HibernateSupport commonDao;

    /**
     * 查询 登录日志条数
     * 
     * @return 登录日志条数
     */
    public Object getCount() {
        String sql = "select count(1) from log";
        Object obj = commonDao.findObjectBySql(sql);
        return obj;
    }

    /**
     * 分页查询后台用户登录日志
     * 
     * @param page 分页对象
     * @return 登录日志集合
     */
    public List logPage(PageModel page) {
        List list = new ArrayList();
        String sql = "SELECT id,logTime,ip,loginId,userName,remark FROM log ORDER BY id DESC LIMIT "
                + page.firstResult() + "," + page.getNumPerPage();
        list = commonDao.findBySql(sql);
        return list;
    }

    /**
     * 批量删除后台用户登录日志
     * 
     * @param ids
     *            多个id信息拼接的字符串
     */
    public void deleteLog(String ids) {
        String[] id = ids.split(",");
        for (int i = 0; i < id.length; i++) {
            commonDao.delete(Long.valueOf(id[i]), Log.class);
        }
    }
}
