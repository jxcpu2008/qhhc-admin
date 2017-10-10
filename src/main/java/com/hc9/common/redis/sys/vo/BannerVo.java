package com.hc9.common.redis.sys.vo;

/** 导航条相关缓存辅助对象 */
public class BannerVo {
	/** 链接地址 */
    private String url;
    
    /** 图片路径 */
    private String imgurl;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
}