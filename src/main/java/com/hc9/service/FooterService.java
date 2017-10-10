package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.Footer;
import com.hc9.dao.impl.HibernateSupport;

/**
 * 系统消息设置
 * 
 * @author ransheng
 * 
 */
@Service
@SuppressWarnings(value = { "unchecked" })
public class FooterService {

    /**
     * 通用dao
     */
    @Resource
    private HibernateSupport commonDao;

    /**
     * 系统消息查询（该表只有一条数据）
     * 
     * @return 单条系统信息
     */
    public Footer queryFooter() {
        // 根据hql语句查询系统消息
        String hql = "from Footer";
        Footer footer = null;
        List<Footer> list = commonDao.find(hql);
        if (list != null && list.size() > 0) {
            // 取到第一条系统消息
            footer = list.get(0);
        }
        return footer;
    }

    /**
     * 新增（编辑）系统消息
     * 
     * @param footer
     *            系统信息对象
     */
    public void saveORupdate(Footer footer) {
        // 修改或调价一条系统信息
        commonDao.saveOrUpdate(footer);
    }
}
