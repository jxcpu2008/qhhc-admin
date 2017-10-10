package com.hc9.model;

import java.util.List;

import com.hc9.common.constant.Constant;

/**
 * 分页实体
 * 
 * @author frank
 * 
 * @param
 */
public class PageModel {

	/** 当前页数 */
	private int pageNum = Constant.STATUES_ONE;
	/** 每页显示数量 */
	private int numPerPage = Constant.SRSRUES_TEN;
	/** 总页数 */
	private int totalPage;
	/** 总数量 */
	private int totalCount;
	/**
	 * 分页数据
	 */
	private List list;

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description: 无参构造方法
	 * </p>
	 */
	public PageModel() {
		super();
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description: 有参构造方法
	 * </p>
	 * 
	 * @param pageNum
	 *            当前页数
	 * @param numPerPage
	 *            每页显示条数
	 */
	public PageModel(int pageNum, int numPerPage) {
		super();
		this.pageNum = pageNum;
		this.numPerPage = numPerPage;
	}

	/**
	 * <p>
	 * Title: getPageNum
	 * </p>
	 * <p>
	 * Description: getPageNum
	 * </p>
	 * 
	 * @return getPageNum
	 */
	public int getPageNum() {
		return pageNum;
	}

	/**
	 * <p>
	 * Title: setPageNum
	 * </p>
	 * <p>
	 * Description: 设置当前页
	 * </p>
	 * 
	 * @param pageNum
	 *            setPageNum
	 */
	public void setPageNum(int pageNum) {
			this.pageNum=pageNum;
	}

	/**
	 * <p>
	 * Title: getNumPerPage
	 * </p>
	 * <p>
	 * Description: 获取每页显示条数
	 * </p>
	 * 
	 * @return numPerPage
	 */
	public int getNumPerPage() {
		return numPerPage;
	}

	/**
	 * <p>
	 * Title: setNumPerPage
	 * </p>
	 * <p>
	 * Description: 设置每页显示条数
	 * </p>
	 * 
	 * @param numPerPage
	 *            每页显示条数
	 */
	public void setNumPerPage(int numPerPage) {
		this.numPerPage = numPerPage;
	}

	/**
	 * <p>
	 * Title: getTotalPage
	 * </p>
	 * <p>
	 * Description: 获取总页数
	 * </p>
	 * 
	 * @return 总页数
	 */
	public int getTotalPage() {
		return totalPage;
	}

	/**
	 * <p>
	 * Title: getTotalCount
	 * </p>
	 * <p>
	 * Description: 获取总条数
	 * </p>
	 * 
	 * @return 总条数
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * <p>
	 * Title: getTotalCount
	 * </p>
	 * <p>
	 * Description: 分页数据
	 * </p>
	 * 
	 * @return 分页数据
	 */
	public List getList() {
		return list;
	}

	/**
	 * <p>
	 * Title: getTotalCount
	 * </p>
	 * <p>
	 * Description: 分页数据
	 * </p>
	 * 
	 * @return 分页数据
	 */
	public void setList(List list) {
		this.list = list;
	}

	/**
	 * <p>
	 * Title: setTotalCount
	 * </p>
	 * <p>
	 * Description: 设置总条数，同时会计算出总页数
	 * </p>
	 * 
	 * @param totalCount
	 *            总条数
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;

		this.totalPage = Math.ceil(totalCount / (float) this.numPerPage) > 0 ? (int) Math
				.ceil(totalCount / (float) this.numPerPage) : 1;
		this.pageNum = this.pageNum > this.totalPage ? this.totalPage
				: this.pageNum;
	}

	/**
	 * <p>
	 * Title: firstResult
	 * </p>
	 * <p>
	 * Description: 计算当前页开始记录
	 * </p>
	 * 
	 * @return 开始记录数
	 */
	public int firstResult() {
		final int firstResult = numPerPage * (pageNum - 1);
		return firstResult;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numPerPage;
		result = prime * result + pageNum;
		result = prime * result + totalCount;
		result = prime * result + totalPage;
		return result;
	}

	@Override
	public String toString() {
		return "Page [numPerPage=" + numPerPage + ", pageNum=" + pageNum
				+ ", totalCount=" + totalCount + ", totalPage=" + totalPage
				+ ",list=" + list + "]";
	}

}
