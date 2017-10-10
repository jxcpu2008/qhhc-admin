package com.hc9.service;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hc9.dao.entity.ChannelSpread;
import com.hc9.dao.entity.ChannelSpreadDetail;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class ChannelSpreadService {
	@Resource
	HibernateSupport dao;
	
	public List getPromoteChannelList() {
		String hql = "from ChannelSpread";
		return dao.find(hql);
	}

	public void saveChannel(ChannelSpread channelSpread) {
		try {
			dao.save(channelSpread);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Serializable saveSpreadDetail(ChannelSpreadDetail csd) {
		try {
			return dao.save(csd);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void updateSpreadDetail(ChannelSpreadDetail csd) {
		try {
			dao.update(csd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ChannelSpreadDetail getSpreadDetail(Serializable id) {
		try {
			return dao.get(ChannelSpreadDetail.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void updateChannel(ChannelSpread channelSpread) {
		try {
			dao.update(channelSpread);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 查询渠道list
	 * 
	 * @param page
	 * @return
	 */
	public PageModel queryChannel(PageModel page) {

		String sql = "SELECT * from channelspread c where c.type=1";

		String sqlcount = "SELECT count(*) from channelspread c where c.type=1";

		sql += " LIMIT " + (page.getPageNum() - 1) * page.getNumPerPage() + ","
				+ page.getNumPerPage() + "";
		page.setTotalCount(dao.queryNumberSql(sqlcount).intValue());
		List list = dao.findBySql(sql);
		page.setList(list);
		return page;
	}

	/**
	 * 查询广告位
	 * 
	 * @param page
	 * @return
	 */
	public List queryadvert() {
		String sql = "SELECT * from channelspread c where c.type=2";
		List list = dao.findBySql(sql);
		return list;
	}

	/**
	 * 查询广告位
	 * 
	 * @param page
	 * @return
	 */
	public PageModel queryadvList(PageModel page, Integer sid) {

		String sql = "SELECT * from channelspread where type=2 and upSpreadId=?";

		String sqlcount = "SELECT count(*) from channelspread where type=2 and upSpreadId=?";

		sql += " LIMIT " + (page.getPageNum() - 1) * page.getNumPerPage() + ","
				+ page.getNumPerPage() + "";
		page.setTotalCount(dao.queryNumberSql(sqlcount, sid).intValue());
		List list = dao.findBySql(sql, sid);
		page.setList(list);
		return page;
	}

	/**
	 * 查询广告
	 * 
	 * @param page
	 * @return
	 */
	public PageModel queryadvvList(PageModel page, Integer sid) {

		String sql = "SELECT * from channelspread where type=3 and upSpreadId=?";

		String sqlcount = "SELECT count(*) from channelspread where type=3 and upSpreadId=?";

		sql += " LIMIT " + (page.getPageNum() - 1) * page.getNumPerPage() + ","
				+ page.getNumPerPage() + "";
		page.setTotalCount(dao.queryNumberSql(sqlcount, sid).intValue());
		List list = dao.findBySql(sql, sid);
		page.setList(list);
		return page;
	}

	public ChannelSpread queryChannelById(Integer id) {
		ChannelSpread channelSpread = dao.get(ChannelSpread.class, id);
		return channelSpread;
	}

	public ChannelSpread queryChanAdvById(Integer id) {
		String sql = "SELECT * from channelspread where type=2 and upSpreadId=?";

		List list = dao.findBySql(sql, ChannelSpread.class, id);
		ChannelSpread chan = (ChannelSpread) (list.size() > 0 ? list.get(0)
				: null);
		return chan;
	}
}
