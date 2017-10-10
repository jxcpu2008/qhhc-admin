package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.FileUtil;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Uploadfile;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
@SuppressWarnings(value = { "pdfManageService" })
public class PdfManageService {

	@Resource
	HibernateSupport dao;

	public Integer uploadPdf(HttpServletRequest request) {
		// 文件夹名称
		String folder = "pdf";

		// 上传图片
		String imgurl = FileUtil.uploadPdf(request, "fileurl", folder);

		// 上传附件为空
		if (imgurl == null || imgurl.equals("1")) {
			return 1;
		}
		if (imgurl != null && imgurl.equals("2")) {
			return 2;
		}

		// 取到当前登录管理员
		Adminuser adminUser = (Adminuser) request.getSession().getAttribute(
				Constant.ADMINLOGIN_SUCCESS);
		if (null == adminUser) {
			adminUser = new Adminuser(Long.valueOf("2"));
		}
		MultipartFile file = null;
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		file = multipartRequest.getFile("fileurl");
		String name = file.getOriginalFilename();
		// 取到附件文件名
		name = name.substring(0, name.indexOf("."));

		Uploadfile upload = new Uploadfile();
		upload.setName(adminUser.getUsername());// 上传人名称
		upload.setSaveName(name);// 原始名称
		upload.setSavePath(imgurl);// 文件路径
		upload.setAddTime(DateUtils.format(Constant.DEFAULT_TIME_FORMAT));// 上传时间

		dao.save(upload);
		return 3;
	}

	/**分页查询
	 * @param page
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List pdfPage(PageModel page,Uploadfile uploadfile) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"select id,name,savePath,saveName,addTime from uploadfile where 1=1 ");
		sb.append(this.getQueryConditions(uploadfile));
		StringBuffer sbl = new StringBuffer("SELECT count(id) FROM uploadfile where 1=1 ");
		sbl.append(this.getQueryConditions(uploadfile));
		list = dao.pageListBySql(page, sbl.toString(), sb.toString(), null);
		return list;
	}

	/**
	 * 删除pdf文件
	 * @param id
	 * @param request
	 * @return
	 */
	public boolean delPdf(String id, HttpServletRequest request) {
		try {

			Uploadfile uf = dao.get(Uploadfile.class, Long.valueOf(id));
			// 删除附件
			FileUtil.deleteFile(uf.getSaveName(), "pdf", request);
			// 从数据库删除附件
			dao.delete(uf);
			return true;
		} catch (DataAccessException e) {
			return false;
		}

	}

	public String getQueryConditions(Uploadfile uploadfile) {
		StringBuffer sb = new StringBuffer();
		if (null != uploadfile.getSaveName()
				&& !"".equals(uploadfile.getSaveName())) {
			sb.append(" and saveName like '%")
					.append(uploadfile.getSaveName()).append("%'");
		}
		if (null != uploadfile.getName()
				&& !"".equals(uploadfile.getName())) {
			sb.append(" and name like '%")
					.append(uploadfile.getName()).append("%'");
		}
		
		return sb.toString();
	}

}
