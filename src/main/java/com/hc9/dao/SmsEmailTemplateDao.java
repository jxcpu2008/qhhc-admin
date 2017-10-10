package com.hc9.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.redis.SmsEmailCache;
import com.hc9.common.util.DateFormatUtil;
import com.hc9.common.util.StatisticsUtil;
import com.hc9.dao.entity.SmsEmailTemplate;
import com.hc9.dao.entity.SwitchControl;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/** 短信邮件模板Dao */
@Service
public class SmsEmailTemplateDao {
	/** 注入数据库操作层 */
	@Resource
	private HibernateSupport dao;

	/** 保持模板记录 */
	public void saveSmsEmailTemplate(SmsEmailTemplate smsEmailTemplate) {
		String currentTime = DateFormatUtil.dateToString(new Date(),
				"yyyy-MM-dd HH:mm:ss");
		String currentDate = currentTime.substring(0, 10);
		String upSwitchEnName = smsEmailTemplate.getTemplateType();
		String switchEnName = smsEmailTemplate.getTemplateEnName();
		String oneLeveName = SmsEmailCache
				.getSmsMsgOneLevelTemplateZhName(upSwitchEnName);
		String twoLeveName = SmsEmailCache.getSmsMsgTwoLevelTemplateZhName(
				upSwitchEnName, switchEnName);
		smsEmailTemplate.setTemplateTypeName(oneLeveName);
		smsEmailTemplate.setTemplateZhName(twoLeveName);
		smsEmailTemplate.setTemplateStatus(1);
		smsEmailTemplate.setCreateTime(currentDate);
		dao.save(smsEmailTemplate);
	}

	/** 删除短息邮件模板记录 */
	public Map<String, Object> deleteSmsEmailTemplate(String id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String sql = "delete from smsemailtemplate where id=?";
		dao.executeSql(sql, id);
		resultMap.put("code", "0");
		resultMap.put("msg", "删除记录成功!");
		return resultMap;
	}

	/** 修改短信邮件模板对象 */
	public Map<String, Object> doUpdateSmsEmailUpdateTemplate(SmsEmailTemplate vo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String upSwitchEnName = vo.getTemplateType();
		String switchEnName = vo.getTemplateEnName();
		String oneLeveName = SmsEmailCache
				.getSmsMsgOneLevelTemplateZhName(upSwitchEnName);
		String twoLeveName = SmsEmailCache.getSmsMsgTwoLevelTemplateZhName(
				upSwitchEnName, switchEnName);
		vo.setTemplateTypeName(oneLeveName);
		vo.setTemplateZhName(twoLeveName);
		String sql = "update smsemailtemplate set templateType=?,templateTypeName=?,templateEnName=?,templateZhName=?,"
				+ "templateTitle=?,templateContent=?,msgType=? where id=?";
		dao.executeSql(sql, vo.getTemplateType(), vo.getTemplateTypeName(),
				vo.getTemplateEnName(), vo.getTemplateZhName(),
				vo.getTemplateTitle(), vo.getTemplateContent(),
				vo.getMsgType(), vo.getId());
		resultMap.put("code", "0");
		resultMap.put("msg", "修改模板成功!");
		return resultMap;
	}

	/** 查询一级开关信息列表 */
	public List<SwitchControl> queryOneLevelSwitchList(String swithchType) {
		String sql = "select * from switchcontrol where swithchType=? and upSwitchEnName is null and switchStatus=1";
		List<SwitchControl> scList = dao.findBySql(sql, SwitchControl.class, swithchType);
		return scList;
	}

	/** 新增模板根据一级大类型查询改类型下面的相关模板列表信息 */
	public List<SwitchControl> queryTemplateListByUpSwitchEnName(String upSwitchEnName) {
		String sql = "select * from switchcontrol where swithchType='sms_email_template_type' "
				+ " and switchStatus=1 and upSwitchEnName=?";
		List<SwitchControl> smsEmailTemplateList = dao.findBySql(sql,
				SwitchControl.class, upSwitchEnName);
		return smsEmailTemplateList;
	}

	/** 根据模板类型和模板英文名称获取模板记录 */
	public SwitchControl querySmsEmailTemplateByCondition(String upSwitchEnName, String switchEnName) {
		String sql = "select * from switchcontrol where swithchType='sms_email_template_type' and switchStatus=1 and upSwitchEnName=? and switchEnName=?";
		List<SwitchControl> smsEmailTemplateList = dao.findBySql(sql,
				SwitchControl.class, upSwitchEnName, switchEnName);
		if (smsEmailTemplateList != null && smsEmailTemplateList.size() > 0) {
			return smsEmailTemplateList.get(0);
		}
		return null;
	}

	/** 根据主键id查询短信邮件模板id */
	public SmsEmailTemplate findSmsEmailTemplateById(String id) {
		String sql = "select * from smsemailtemplate where id=?";
		List<SmsEmailTemplate> list = dao.findBySql(sql,
				SmsEmailTemplate.class, id);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/** 根据模板类型、模板英文名称、消息类型查询模板信息 */
	public SmsEmailTemplate querySmsEmailTemplate(Integer msgType, String templateType, String templateEnName) {
		String sql = "select * from smsemailtemplate t where t.msgType=? and templateType=? and templateEnName=?";
		List<SmsEmailTemplate> list = dao.findBySql(sql, SmsEmailTemplate.class, msgType, templateType, templateEnName);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/** 判断对应的模板类型及模板是否已经存在 */
	public boolean isExistByTypeAndTemplateEnName(SmsEmailTemplate smsEmailTemplate) {
		boolean flag = false;
		String sql = "select * from smsemailtemplate where msgType=? and templateType=? and templateEnName=?";

		List<SmsEmailTemplate> list = dao.findBySql(sql,
				SmsEmailTemplate.class, smsEmailTemplate.getMsgType(),
				smsEmailTemplate.getTemplateType(),
				smsEmailTemplate.getTemplateEnName());
		if (list != null && list.size() > 0) {
			flag = true;
		}
		return flag;
	}

	/** 判断对应的模板类型及模板是否已经存在 */
	public boolean isExistByTypeAndTemplateEnNameForUpdate(
			SmsEmailTemplate smsEmailTemplate) {
		boolean flag = false;
		String sql = "select * from smsemailtemplate where msgType=? and templateType=? "
				+ "and templateEnName=? and id!=?";
		List<SmsEmailTemplate> list = dao.findBySql(sql,
				SmsEmailTemplate.class, smsEmailTemplate.getMsgType(),
				smsEmailTemplate.getTemplateType(),
				smsEmailTemplate.getTemplateEnName(), smsEmailTemplate.getId());
		if (list != null && list.size() > 0) {
			flag = true;
		}
		return flag;
	}

	/** 分页查询邮件短信模板列表相关数据 */
	public List<SmsEmailTemplate> querySmsEmailTemplateList(PageModel page,
			String msgType) {
		List<SmsEmailTemplate> smsEmailTemplateList = new ArrayList<SmsEmailTemplate>();

		String select = "select id,templateType,templateTypeName,templateEnName,templateZhName,"
				+ "templateTitle,templateStatus,msgType,createTime ";
		String fromSql = " from smsemailtemplate where templateStatus=1 ";

		if ("1".equals(msgType) || "2".equals(msgType)) {
			fromSql += "and msgType=" + msgType;
		}
		String orderBy = " order by id desc";

		String querySql = select + fromSql + orderBy;

		String countSql = "select count(id) " + fromSql;
		List list = dao.pageListBySql(page, countSql, querySql, null);
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] arr = (Object[]) obj;
				SmsEmailTemplate vo = new SmsEmailTemplate();
				vo.setId(StatisticsUtil.getLongFromBigInteger(arr[0]));
				vo.setTemplateType(StatisticsUtil.getStringFromObject(arr[1]));
				String templateTypeName = StatisticsUtil
						.getStringFromObject(arr[2]);
				String templateZhName = StatisticsUtil
						.getStringFromObject(arr[4]);
				vo.setTemplateTypeName(templateTypeName + " - "
						+ templateZhName);
				vo.setTemplateEnName(StatisticsUtil.getStringFromObject(arr[3]));
				vo.setTemplateZhName(templateZhName);
				vo.setTemplateTitle(StatisticsUtil.getStringFromObject(arr[5]));
				vo.setTemplateStatus(StatisticsUtil
						.getIntegerFromObject(arr[6]));
				vo.setMsgType(StatisticsUtil.getIntegerFromObject(arr[7]));
				vo.setCreateTime(StatisticsUtil.getStringFromObject(arr[8]));
				smsEmailTemplateList.add(vo);
			}
		}
		return smsEmailTemplateList;
	}

}
