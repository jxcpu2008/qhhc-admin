package com.hc9.model;

/**
 * 首页推荐标模型
 * @author Administrator
 *
 */
public class LoanRecommendVo implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** id*/
	private String id;
	
	/**标名称*/
	private String name;
	
	/**推荐理由*/
	private String reason;
	
	/**风控建议*/
	private String advice;
	
	/**类型id*/
	private String typeId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	
}
