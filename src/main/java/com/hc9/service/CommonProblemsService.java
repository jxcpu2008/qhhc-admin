package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.commons.normal.Validate;
import com.hc9.dao.entity.CommonProblems;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
@SuppressWarnings(value = { "cpservice" })
public class CommonProblemsService {

    /*** 注入HibernateSupport */
    @Resource
    HibernateSupport commondao;
    /**
     * 引用ColumnManageService
     */
    @Resource
    ColumnManageService columnservice;

    /**
     * 查询所有反馈邮件
     * 
     * @param page
     *            PageModel
     * @return List
     */
    public List queryCommonProblems(PageModel page, String title,
            String isShow, String type) {
        List list = new ArrayList();
        List<Object> param = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("from CommonProblems where 1 = 1");
        if (Validate.emptyStringValidate(title)) {
            hql.append(" and title like ?");
            param.add("%" + title.trim() + "%");
        }
        if (Validate.emptyStringValidate(isShow) && !isShow.equals("-1")) {
            hql.append(" and isShow = ?");
            param.add(Integer.valueOf(isShow));
        }
        if (Validate.emptyStringValidate(type) && !type.equals("-1")) {
            hql.append(" and type = ?");
            param.add(Integer.valueOf(type));
        }
        hql.append(" order by id desc");
        Object[] params = null;
        if (param.size() > 0) {
            params = param.toArray();
        }
        if(null == page){
            list = commondao.query(hql.toString(), false, params);
        }else{
            list = commondao.pageListByHql(page, hql.toString(), false, params);
        }
        return list;
    }

    /**
     * 打开新增/修改常见问题页面
     * 
     * @param id
     *            long
     * @param operation
     *            String
     * @param request
     *            HttpServletRequest
     * @return ModelAndView
     */
    public ModelAndView forwardAddUptPage(long id, String operation,
            HttpServletRequest request) {
        request.setAttribute("operation", operation);
        request.setAttribute("id", id);
        if (operation.equals("upt")) {
            CommonProblems problem = commondao.get(CommonProblems.class, id);
            request.setAttribute("problem", problem);
        }
        return new ModelAndView("WEB-INF/views/admin/column/add_upt_problem");
    }

    /**
     * 新增/修改常见问题
     * 
     * @param id
     *            long
     * @param operation
     *            String
     * @param problem
     *            CommonProblems
     * @param request
     *            HttpServletRequest
     * @return JSONObject
     */
    public JSONObject addOrUpdateProblem(long id, String operation,
            CommonProblems problem, HttpServletRequest request) {
        JSONObject json = new JSONObject();
        if (!Validate.emptyStringValidate(problem.getReplyContent())
                || !Validate.emptyStringValidate(problem.getTitle())
                || problem.getType() == null || problem.getIsShow() == null) {
            return columnservice.setJson(json, "300", "您还有未填写的内容!", "main56",
                    "closeCurrent");
        }else{
            try {
                if (operation.equals("upt")) {
                    commondao.update(problem);
                } else if (operation.equals("add")) {
                    commondao.save(problem);
                }
                return columnservice.setJson(json, "200", "更新成功", "main56",
                        "closeCurrent");
            } catch (Exception e) {
                return columnservice.setJson(json, "300", "更新失败", "main56",
                        "closeCurrent");
            }
        }
    }

    /**
     * 删除常见问题
     * 
     * @param ids
     *            要删除的常见问题的id
     * @param request
     *            HttpServletRequest
     * @return JSONObject
     */
    public JSONObject deleteProblem(String ids, HttpServletRequest request) {
        JSONObject json = new JSONObject();
        try {
            boolean isSuccess = deleteMany(ids);
            if (isSuccess) {
                return columnservice.setJson(json, "200", "操作成功", "main56", "");
            } else {
                return columnservice.setJson(json, "300", "操作失败", "main56", "");
            }
        } catch (Exception e) {
            return columnservice.setJson(json, "300", "操作失败", "main56", "");
        }
    }
    
    /**
     * 批量删除
     * 
     * @param ids
     *            选中的id
     * @return boolean
     */
    public boolean deleteMany(String ids) {
        CommonProblems problem = null;
        boolean isSuccess = true;
        String[] id = ids.split(",");
        for (int i = 0; i < id.length; i++) {
            Long did = Long.valueOf(id[i]);
            problem = commondao.get(CommonProblems.class, did);
            commondao.delete(problem);
        }
        return isSuccess;
    }
}
