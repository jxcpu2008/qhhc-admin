package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.OnlineApplyInfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * <p>
 * Title:OnlineApplyService
 * </p>
 * <p>
 * Description: 在线申请服务层
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author frank date 2014年2月13日
 */
@Service
public class OnlineApplyService {

    /** 注入数据库服务层 */
    @Resource
    private HibernateSupport dao;

    /**
     * <p>
     * Title: queryonlinePage
     * </p>
     * <p>
     * Description:查询在线申请
     * </p>
     * 
     * @param page
     *            分页
     * @param onlineApplyInfo
     *            onlineApplyInfo对象
     * 
     * @return list
     */
    @SuppressWarnings("rawtypes")
    public List queryonlinePage(PageModel page, OnlineApplyInfo onlineApplyInfo) {
        List datalist = new ArrayList();
        StringBuffer countsql = new StringBuffer(
                "SELECT count(1) from  online_apply_info oai INNER JOIN province pr on oai.provinceId=pr.id INNER JOIN city cit on cit.id=oai.cityId ");
        StringBuffer listsql = new StringBuffer(
                "SELECT oai.id,oai.name,pr.name,cit.name,oai.phone,oai.money,oai.content,case WHEN oai.state=1 THEN '已联系' ELSE '未联系' END ");
        listsql.append("from  online_apply_info oai INNER JOIN province pr on oai.provinceId=pr.id INNER JOIN city cit on cit.id=oai.cityId ");
        if (StringUtil.isNotBlank(onlineApplyInfo.getState() + "")
                && StringUtil.isNumberString(onlineApplyInfo.getState() + "")) {
            countsql.append(" where oai.state=").append(
                    onlineApplyInfo.getState());
            listsql.append(" where oai.state=").append(
                    onlineApplyInfo.getState());
        }
        listsql.append(" ORDER BY oai.state asc,oai.id desc");
        datalist = dao.pageListBySql(page, countsql.toString(),
                listsql.toString(), null);
        return datalist;
    }

    /**
     * <p>
     * Title: deleteOnline
     * </p>
     * <p>
     * Description: 删除在线设计
     * </p>
     * 
     * @param ids
     *            删除的集合
     * @return 成功
     */
    public boolean deleteOnline(String ids) {
            if (ids.length() > 1) {
                ids = ids.substring(0, ids.length() - 1);
            }
            StringBuffer sb = new StringBuffer(
                    "delete from online_apply_info where id in (").append(ids)
                    .append(")");
            int result = dao.executeSql(sb.toString());
            return result > 0 ? true : false;
    }

    /**
     * <p>
     * Title: isContacted
     * </p>
     * <p>
     * Description: 已联系
     * </p>
     * 
     * @param id
     *            编号
     * @return 是否成功
     */
    public int isContacted(String id) {
        StringBuffer sb = new StringBuffer(
                "update online_apply_info set state=1 where id=");
        sb.append(id);
        return dao.executeSql(sb.toString());
    }

    /**
     * <p>
     * Title: save
     * </p>
     * <p>
     * Description: 保存
     * </p>
     * 
     * @param onlineApplyInfo
     *            对象
     * @return 是否成功
     */
    public boolean save(OnlineApplyInfo onlineApplyInfo) {
        try {
            dao.save(onlineApplyInfo);
            return true;
        } catch (DataAccessException e) {
            return false;
        }

    }
}
