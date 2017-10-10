package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hc9.common.util.FileUtil;
import com.hc9.dao.entity.Videobroadcast;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * 视频播报信息
 * 
 * @author ransheng
 * 
 */

@SuppressWarnings(value = { "rawtypes" })
@Service
public class VideobroadcastService {

    /**
     * 通用dao
     */
    @Resource
    private HibernateSupport commonDao;

    /**
     * 查询视频总条数
     * 
     * @return 视频条数
     */
    public Object getCount() {
        String sql = "select count(1) from videobroadcast";
        return commonDao.findObjectBySql(sql);
    }

    /**
     * 分页查询视频信息
     * 
     * @param page
     *            分页对象
     * @return 视频集合
     */
    public List videoPage(PageModel page) {
        List list = new ArrayList();
        String sql = "select id,title,filepath,isshow,shownum,remark,addtime from videobroadcast ORDER BY shownum ASC LIMIT "
                + page.firstResult() + "," + page.getNumPerPage();
        list = commonDao.findBySql(sql);
        return list;
    }

    /**
     * 根据编号查询视频播报单条信息
     * 
     * @param id
     *            编号
     * @return 单条视频信息
     */
    public Videobroadcast getOnly(String id) {
        // 查询单条信息
        Videobroadcast video = commonDao.get(Videobroadcast.class,
                Long.valueOf(id));
        return video;
    }

    /**
     * 新增修改视频信息
     * 
     * @param video
     *            视频信息对象
     * @param request
     *            请求对象
     * @return 成功true，失败false
     */
    public Boolean saveORupdateVideo(Videobroadcast video,
            HttpServletRequest request) {
        // 删除图片
        FileUtil.deleteFile(video.getFilePath(), "video", request);
        // 上传图片
        String imgurl = FileUtil.upload(request, "imgurl", "video");
        // 保存图片路径
        if (imgurl != null && !"1".equals(imgurl.trim())) {
            video.setFilePath(imgurl);
        }
        // 如果上传不是图片类型
        if (imgurl != null && "2".equals(imgurl.trim())) {
            return false;
        } else {
            commonDao.saveOrUpdate(video);
            return true;
        }
    }

    /**
     * 删除多条视频播报信息
     * 
     * @param ids
     *            视频播报信息id
     */
    @Transactional
    public void deleteVideo(String ids) {
        String[] id = ids.split(",");
        for (int i = 0; i < id.length; i++) {
            commonDao.delete(Long.valueOf(id[i]), Videobroadcast.class);
        }
    }
}
