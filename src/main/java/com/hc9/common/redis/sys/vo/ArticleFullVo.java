package com.hc9.common.redis.sys.vo;

/**
 * 
 *全字段文章实体
 */
public class ArticleFullVo {
	private Long id;
	private Long deputysection_id;
	private String title;
	private int isRecommend;
	private String context;
	private String createTime;
	private String url;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDeputysection_id() {
		return deputysection_id;
	}
	public void setDeputysection_id(Long deputysection_id) {
		this.deputysection_id = deputysection_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getIsRecommend() {
		return isRecommend;
	}
	public void setIsRecommend(int isRecommend) {
		this.isRecommend = isRecommend;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
