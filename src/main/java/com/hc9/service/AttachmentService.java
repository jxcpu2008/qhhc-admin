package com.hc9.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hc9.common.constant.Constant;
import com.hc9.common.log.LOG;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.FileUtil;
import com.hc9.common.util.OSSUtil;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.Attachment;
import com.hc9.dao.entity.AttachmentType;
import com.hc9.dao.entity.Loansign;

/**
 * 借款标附件service
 * 
 * @author Administrator
 * 
 */
@Service
public class AttachmentService {
	/** dao */
	@Resource
	HibernateSupportTemplate dao;

	/** loanSignQuery */
	@Resource
	private LoanSignQuery loanSignQuery;

	/** filedata */
	private String filedata = "Filedata";
	/** loansign */
	private String loansign = "loanSign";

	/**
	 * <p>
	 * Title: getAttachmentCount
	 * </p>
	 * <p>
	 * Description: 借款标为id的附件信息的条数
	 * </p>
	 * 
	 * @param loansignId
	 *            借款编号
	 * @return 条数
	 */
	public int getAttachmentCount(int loansignId) {
		StringBuffer sb = new StringBuffer("SELECT COUNT(1) from attachment where loansign_id=");
		return loanSignQuery.queryCount(sb.append(loansignId).toString());
	}

	/**
	 * 通过借款标标号查询到该借款标的附件信息
	 * 
	 * @param page
	 * @param loanSignId
	 *            借款标id
	 * @param start
	 *            start
	 * @param limit
	 *            limit
	 * @return 附件信息
	 */
	public List<Attachment> queryAttachmentList(int start, int limit, int loanSignId) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT attachment.id,originalName,attachmentName,(select attachment_name from attachment_type a where a.id=attachment.attachmentType ) as attachmentType,");
		sb.append(
				" uploadTime, adminuser.realname FROM attachment INNER JOIN adminuser ON attachment.adminuser_id = adminuser.id WHERE loansign_id = ")
				.append(loanSignId);
		sb.append(" order by attachment.id desc LIMIT ").append(start).append(" , ").append(limit);
		list = dao.findBySql(sb.toString());
		return list;
	}

	/**
	 * 上传附件
	 * 
	 * @param id
	 *            标号
	 * @param type
	 *            上传类型（1标图 2借款者资料）
	 * @param request
	 *            请求
	 * @return 1上传附件为空 2上传的不是图类型
	 */
	public Integer uploadAttchment(String loanId, String attachmentTypeID, HttpServletRequest request) {
		// 文件夹名称
		String folder = "attachment";

		// 上传图片
		String imgurl = null;
		imgurl = FileUtil.upload(request, "fileurl", folder);

		// 上传附件为空
		if (imgurl == null || imgurl.equals("1")) {
			return 1;
		}
		if (imgurl != null && imgurl.equals("2")) {
			return 2;
		}

		Loansign loansign = loanSignQuery.getLoansign(loanId);

		// 取到当前登录管理员
		Adminuser adminUser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
		if (null == adminUser) {
			adminUser = new Adminuser(Long.valueOf("2"));
		}
		MultipartFile file = null;
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		file = multipartRequest.getFile("fileurl");
		String name = file.getOriginalFilename();
		// 取到附件文件名
		name = name.substring(0, name.lastIndexOf("."));
		Attachment attachmently = new Attachment();
		AttachmentType attachmentType = getAttachmentTypeInfo(Long.valueOf(attachmentTypeID));
		attachmently.setAdminuser(adminUser);// 上传人
		attachmently.setAttachmentName(imgurl);// 附件保存地址
		attachmently.setOriginalName(name);// 附件原始名称
		attachmently.setAttachmentType(attachmentType);
		attachmently.setUploadTime(DateUtils.format(Constant.DEFAULT_TIME_FORMAT));
		attachmently.setLoansign(loansign);// 标号
		// 保存附件
		dao.save(attachmently);
		return 3;
	}

	/**
	 * 上传附件到图片服务器
	 * 
	 * @param id
	 *            标号
	 * @param type
	 *            上传类型（1标图 2借款者资料）
	 * @param request
	 *            请求
	 * @return 1上传附件为空 2上传的不是图类型
	 */
	public Integer uploadAttchmentToOSS(String loanId, String attachmentTypeID,Adminuser adminUser, HttpServletRequest request) {
		Map<String,String> result=null;
		String folder = "attachment";
		try {
			result = OSSUtil.uploadToOss(request,folder);
		} catch (IOException e) {
			LOG.error("文件上传出错:" + e);
		}

		// 上传附件为空
		if (result == null) {
			return 1;
		}

		Loansign loansign = loanSignQuery.getLoansign(loanId);



		Attachment attachmently = new Attachment();
		AttachmentType attachmentType = getAttachmentTypeInfo(Long.valueOf(attachmentTypeID));
		attachmently.setAdminuser(adminUser);// 上传人
		attachmently.setAttachmentName(result.get("fileDir"));// 附件保存地址
		attachmently.setOriginalName(result.get("fileName"));// 附件原始名称
		attachmently.setAttachmentType(attachmentType);
		attachmently.setUploadTime(DateUtils.format(Constant.DEFAULT_TIME_FORMAT));
		attachmently.setLoansign(loansign);// 标号
		attachmently.setEtag(result.get("eTag"));
		// 保存附件
		dao.save(attachmently);
		return 3;
	}

	/**
	 * 删除附件
	 * 
	 * @param id
	 *            附件id
	 * @param request
	 *            请求
	 * @return 是否
	 */
	public boolean delAttachment(String id, HttpServletRequest request) {
		try {
			Attachment attachment = dao.get(Attachment.class, Long.valueOf(id));
			// 删除附件
			FileUtil.deleteFile(attachment.getAttachmentName(), loansign, request);
			// 从数据库删除附件
			dao.delete(attachment);
			return true;
		} catch (DataAccessException e) {
			return false;
		}

	}

	/***
	 * 获取上传的类型
	 * 
	 * @return
	 */
	public List<AttachmentType> getAttachmentType() {
		String sql = "select * from attachment_type";
		List<AttachmentType> attachmentTypes = dao.findBySql(sql, AttachmentType.class);
		return attachmentTypes;
	}

	/***
	 * 根据Id获取attachmentType
	 * 
	 * @param attachmentTypeId
	 * @return
	 */
	public AttachmentType getAttachmentTypeInfo(Long id) {
		AttachmentType attachmentType = dao.get(AttachmentType.class, id);
		return attachmentType;
	}

	public Adminuser getAdminUser(String ooxx) {
		String sql="SELECT * FROM adminuser WHERE id=?";
		Adminuser adminuser=dao.findObjectBySql(sql, Adminuser.class, ooxx);
		return adminuser;
	}

}
