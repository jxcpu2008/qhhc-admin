package com.hc9.common.redis.sys.vo;

/** 文章列表相关vo */
public class ArticleVo {
	/** 文件路径 */
    private String url;
    
    /** 标题 */
    private String title;
    
    /** 创建时间 */
    private String createTime;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
