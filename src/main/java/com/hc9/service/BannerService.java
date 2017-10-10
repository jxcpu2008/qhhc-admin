package com.hc9.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.log.LOG;
import com.hc9.common.redis.sys.h5.H5CacheManagerUtil;
import com.hc9.common.redis.sys.vo.BannerVo;
import com.hc9.common.redis.sys.web.WebCacheManagerUtil;
import com.hc9.common.util.FileUtil;
import com.hc9.common.util.OSSUtil;
import com.hc9.dao.entity.Banner;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/** banner图片信息 */
@SuppressWarnings(value = { "rawtypes", "unchecked" })
@Service
public class BannerService {

    /** 通用dao */
    @Resource
    private HibernateSupport commonDao;

    @Resource
    AppCacheService appCacheService;
    
    /** 查询banner图片条数 */
    public Object getCount() {
        String sql = "select count(1) from banner";
        Object count = commonDao.findObjectBySql(sql);
        return count;
    }

    /**
     * 分页显示banner图片信息
     * 
     * @param page
     *            分页对象
     * @return 返回前10条banner图片信息
     */
    public List bannerPage(PageModel page) {
        List list = new ArrayList();
        String sql = "select id,number,picturename,url,type from banner ORDER BY type ,number LIMIT "
                + page.firstResult() + "," + page.getNumPerPage();
        list = commonDao.findBySql(sql);
        return list;
    }

    /**
     * 根据id查询banner图片信息
     * 
     * @param id
     *            图片编号
     * @return 返回单条图片信息
     */
    public Banner getOnly(String id) {
        // 返回单条banner图片信息
        return commonDao.get(Banner.class, Long.valueOf(id));
    }

    /**
     * 新增（编辑）banner图片信息
     * 
     * @param banner banner图片系对象
     * 
     * @param request 请求对象
     * @return true成功、false失败
     */
    public Boolean saveORupdateBanner(Banner banner, HttpServletRequest request) {
        // 文件夹名称
        String folder = "banner";
        // 上传图片
        String imgurl = FileUtil.upload(request, "fileurl", folder);
        // 如果有图片上传
        if (imgurl != null && !"1".equals(imgurl.trim())) {
            // 删除图片
            FileUtil.deleteFile(banner.getImgurl(), folder, request);
            banner.setImgurl(imgurl);
        }
        // 如果上传的不是图类型
        if (imgurl != null && imgurl.equals("2")) {
            return false;
        } else {
            // 如果选择的是修改
            if (banner.getId() != null
                    && !"".equals(banner.getId().toString().trim())) {
                commonDao.saveOrUpdate(banner);
            } else {
                List<Banner> b = commonDao
                        .find("from Banner b order by b.number desc");
                if (b != null && b.size() > 0) {
                    banner.setNumber(b.get(0).getNumber() + 1);
                } else {
                    banner.setNumber(1);
                }
                commonDao.saveOrUpdate(banner);
            }
            query();
            queryH5();
            appCacheService.updateAppIndexBannerListCache();
            return true;
        }
    }
    /**
     * 新增（编辑）banner图片信息
     * 
     * @param banner
     *            banner图片系对象
     * 
     * @param request
     *            请求对象
     * @return true成功、false失败
     */
    public Boolean saveORupdateBannerToOSS(Banner banner, HttpServletRequest request) {
        // 文件夹名称
        String folder = "banner";
        // 上传图片
        Map<String,String> result=null;
        try {
			result = OSSUtil.uploadToOss(request,folder);
		} catch (IOException e) {
			LOG.error("文件上传出错:" + e);
		}
       
        // 如果上传的不是图类型
        if (result == null ) {
            return false;
        } else {
        	 banner.setImgurl(result.get("fileDir"));
        	 banner.setEtag(result.get("eTag"));
            // 如果选择的是修改
			if (banner.getId() != null && !"".equals(banner.getId().toString().trim())) {
				
                commonDao.saveOrUpdate(banner);
            } else {
                List<Banner> b = commonDao
                        .find("from Banner b order by b.number desc");
                if (b != null && b.size() > 0) {
                    banner.setNumber(b.get(0).getNumber() + 1);
                } else {
                    banner.setNumber(1);
                }
                commonDao.saveOrUpdate(banner);
            }

            return true;
        }
    }
    /**
     * 根据id删除banner图片信息
     * 
     * @param ids
     *            多张图片id组合，以逗号隔开
     */
    public void deletebanner(String ids) {
        // 将多个图片编号id转换为数组
        String[] id = ids.split(",");
        // 循环id
        for (int i = 0; i < id.length; i++) {
            // 删除图片信息
            commonDao.delete(Long.valueOf(id[i]), Banner.class);
        }
    }

    /**
     * 根据排序编号查询（上移）
     * 
     * @param id
     *            id
     * @return 返回null表示该条记录已经是最前面一条 ,否则返回该条记录的上一条排序编号
     */
    public List queryByNumberUp(String id) {

        // 定义查询banner图片信息的集合
        List list = new ArrayList();
        // 根据id查询banner图片信息
        Banner banner = commonDao.get(Banner.class, Long.valueOf(id));
        list.add(banner);
        if (banner != null) {
            // 根据banner排序编号查询图片细信息
            String sql = "select MAX(number) from banner where number<"
                    + banner.getNumber();
            Object obj = commonDao.findObjectBySql(sql);
            list.add(obj);
        }
        return list;
    }

    /**
     * 根据排序编号查询（下移）
     * 
     * @param id
     *            编号
     * @return 返回null表示该条记录已经是最前面一条 ,否则返回该条记录的上一条排序编号
     */
    public List queryByNumberDown(String id) {
        // 定义查询banner图片信息的集合
        List list = new ArrayList();
        Banner banner = commonDao.get(Banner.class, Long.valueOf(id));
        list.add(banner);
        if (banner != null) {
            // 根据banner排序编号查询图片细信息
            String sql = "select min(number) from banner where number>"
                    + banner.getNumber();
            Object obj = commonDao.findObjectBySql(sql);
            list.add(obj);
        }
        return list;
    }

    /**
     * 根据排序号查询单条数据
     * 
     * @param number
     *            排序号
     * @return 当个图片信息
     */
    public Banner getBannerByNume(Integer number) {
        // 根据图片排序编号查询图片信息
        String hql = "from Banner where number=" + number;
        List<Banner> list = commonDao.find(hql);
        Banner banner = null;
        if (list != null && list.size() > 0) {
            // 得到当个图片系想你
            banner = list.get(0);
        }
        return banner;
    }

    /**
     * 修改图片信息
     * 
     * @param banner
     *            图片信息
     */
    public void update(Banner banner) {
        // 修改图片信息
        commonDao.update(banner);
    }

    /**
     * 图片信息
     * 
     * @return 图片
     */
    public List<Banner> query() {
        String hql = "from Banner where type=1 order by number asc";
        List<Banner> banners = commonDao.find(hql);
        if(banners != null && banners.size() > 0) {
        	List<BannerVo> bannerVoList = new ArrayList<BannerVo>();
        	for(Banner banner : banners) {
        		BannerVo vo = new BannerVo();
        		vo.setImgurl(banner.getImgurl());
        		vo.setUrl(banner.getUrl());
        		bannerVoList.add(vo);
        	}
        	WebCacheManagerUtil.setWebBannerListToRedis(bannerVoList);
        }
        return banners;
    }

    /**
     * H5图片信息
     * 
     * @return 图片
     */
    public List<Banner> queryH5() {
        String hql = "from Banner where type=3 order by number asc";
        List<Banner> banners = commonDao.find(hql);
        if(banners != null && banners.size() > 0) {
        	List<BannerVo> bannerVoList = new ArrayList<BannerVo>();
        	for(Banner banner : banners) {
        		BannerVo vo = new BannerVo();
        		vo.setImgurl(banner.getImgurl());
        		vo.setUrl(banner.getUrl());
        		bannerVoList.add(vo);
        	}
        	H5CacheManagerUtil.setH5BannerListToRedis(bannerVoList);
        }
        return banners;
    }
    
    /**
     * 重置application中的banner图片
     * @param application ServletContext
     */
    public void resetAppBanner(ServletContext application){
        application.setAttribute("application_banner", query());
        resetAppBanner();
    }
    public void resetAppBanner(){
    	query();
    	queryH5();
    	appCacheService.updateAppIndexBannerListCache();
    }

	public boolean isH5Banner(String id) {
		String sql="SELECT type FROM banner WHERE id=?";
		Object obj=commonDao.findObjectBySql(sql, id);
		boolean isH5=false;
		if(3==Integer.valueOf(obj.toString())) {
			isH5=true;
		}
		return isH5;
	}
	/**
	 * 复制某个id的记录
	 * @param id
	 */
	public void duplicateH5Banner(String id) {
		String sql="SELECT * FROM banner WHERE id=?";
		Banner bannerH5=commonDao.findObjectBySql(sql, Banner.class, id);

		Banner bannerApp=new Banner();
		bannerApp.setEtag(bannerH5.getEtag());
		bannerApp.setImgurl(bannerH5.getImgurl());
		bannerApp.setNumber(bannerH5.getNumber());
		bannerApp.setPicturename(bannerH5.getPicturename());
		bannerApp.setType(4);
		bannerApp.setUrl(bannerH5.getUrl()+"?srcfrom=app");
		commonDao.save(bannerApp);
	}
}
