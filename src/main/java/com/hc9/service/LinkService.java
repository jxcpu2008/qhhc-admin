package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hc9.common.util.FileUtil;
import com.hc9.dao.entity.Link;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * 友情链接
 * 
 * @author ransheng
 * 
 */
@Service
@SuppressWarnings(value = { "rawtypes" })
public class LinkService {

    /**
     * 通用dao
     */
    @Resource
    private HibernateSupport commonDao;

    /**
     * 查询友情链接条数
     * 
     * @param name
     *            名称
     * @return 友情链接条数
     */
    public Object getCount(String name) {
        String sql = "select count(1) from link WHERE name LIKE '%"
                + name.trim() + "%'";
        Object obj = commonDao.findObjectBySql(sql);
        return obj;
    }

    /**
     * 分页查询友情链接信息
     * 
     * @param page
     *            分页对象
     * @param name
     *            名称
     * @return 友情链接集合
     */
    public List linkPage(PageModel page, String name) {
        List list = new ArrayList();
        String sql = "select id,name,url,remark,isShow from link WHERE name LIKE '%"
                + name.trim()
                + "%'"
                + " ORDER BY id DESC LIMIT "
                + page.firstResult() + "," + page.getNumPerPage();
        list = commonDao.findBySql(sql);
        return list;
    }

    /**
     * 根据id查询友情链接信息
     * 
     * @param id
     *            编号
     * @return 单条友情链接信息
     */
    public Link queryOnly(String id) {
        return commonDao.get(Link.class, Long.valueOf(id));
    }

    /**
     * 新增（ 编辑）友情链接信息
     * 
     * @param link
     *            友情链接对象
     */
    public Boolean saveORupdatelinke(Link link, HttpServletRequest request) {
        // 文件夹名称
        String folder = "verifyimg";
        // 上传图片
        String imgurl = FileUtil.upload(request, "fileurl", folder);
        // 如果有图片上传
        if (imgurl != null && !"1".equals(imgurl.trim())) {
            // 删除图片
            FileUtil.deleteFile(link.getVerifyImg(), folder, request);
            link.setVerifyImg(imgurl);
        }
        // 如果上传的不是图类型
        if (imgurl != null && imgurl.equals("2")) {
            return false;
        } else {
            commonDao.saveOrUpdate(link);
            return true;
        }
    }

    /**
     * 根据id删除友情链接信息
     * 
     * @param ids
     *            编号
     */
    @Transactional
    public void deleteLink(String ids) {
        String[] id = ids.split(",");
        for (int i = 0; i < id.length; i++) {
            commonDao.delete(Long.valueOf(id[i]), Link.class);
        }

    }

    /**
     * 前台显示
     * 
     * @return link
     */
    public List<Link> query() {
        String hql = "from Link where isShow=1";
        return commonDao.find(hql);
    }
    
    /**
     * 重置application中的banner图片
     * @param application ServletContext
     */
    public void resetAppLink(ServletContext application){
        application.setAttribute("application_link", query());
    }
}
