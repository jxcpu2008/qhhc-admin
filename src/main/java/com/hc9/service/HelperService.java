package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.Helper;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class HelperService {
	@Resource
	private HibernateSupport dao;

	public List<Helper> queryHelp(Integer id) {

		String hql = " from Helper where level_Id=" + id;
		List<Helper> list = dao.find(hql.toString());
		return list;
	}

	public List<Helper> queryHelperList(PageModel page) {
		String hql = " from Helper";
		List list = dao.pageListByHql(page, hql.toString(), false);
		return list;
	}

	public List<Helper> queryHelpCloum() {
		String hql = " from Helper GROUP BY level_Id";
		List<Helper> list = dao.find(hql.toString());
		return list;
	}

	public Helper queryHelpDetil(Long id) {
		return dao.get(Helper.class, id);
	}

	public void updateHelp(Helper help) {

		dao.update(help);
	}

	public void saveHelp(Helper help) {

		dao.save(help);
	}

}
