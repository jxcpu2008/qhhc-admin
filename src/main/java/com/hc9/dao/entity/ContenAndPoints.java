package com.hc9.dao.entity;
/**
 * 积分与内容对象
 * @author frank
 *
 */
public class ContenAndPoints implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String content;
	private int point;
	public ContenAndPoints(String content, int point) {
		this.content = content;
		this.point = point;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	
}
