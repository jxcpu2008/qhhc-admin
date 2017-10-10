package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.Deputysection;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class DeputysectionService {

    /**
     * 注入HibernateSupport
     */
    @Resource
    HibernateSupport commondao;
    
    /**
     * 分页查询所有二级栏目
     * 
     * @param page
     *            PageModel
     * @param topicId
     *            一级栏目id (一级栏目编号)
     * @return 返回存放二级栏目的集合
     */
    public List queryDeput(PageModel page, long topicId) {
        List list = new ArrayList();
        StringBuilder sb = new StringBuilder("from Deputysection where 1 = 1");
        List<Object> param = new ArrayList<Object>();
        if (topicId != 0) {
            sb.append(" and topic.id = ?");
            param.add(topicId);
        }
        Object[] params = null;
        if (param.size() > 0) {
            params = param.toArray();
        }
        if (page != null) {
            return commondao.pageListByHql(page, sb.toString(), false, params);
        } else {
            return commondao.query(sb.toString(), false, params);
        }
    }
    
    /**
     * 根据id查询二级栏目详情
     * 
     * @param id
     *            二级栏目id (二级栏目编号)
     * @return 返回二级栏目
     */
    public Deputysection queryDeputyById(long id) {
        return commondao.get(Deputysection.class, id);
    }
    
    /**
     * 修改二级栏目
     * 
     * @param deputysection
     *            二级栏目
     */
    public void updateDeputysection(Deputysection deputysection) {
        commondao.update(deputysection);
    }
    
    /**
     * 新增二级栏目
     * 
     * @param deputy
     *            二级栏目
     */
    public void addDeputy(Deputysection deputy) {
        Deputysection deputysection = null;
        long did = (Long) commondao.save(deputy);
        deputysection = commondao.get(Deputysection.class, did);

        switch (deputysection.getSectiontype().getId().intValue()) {
        // 单页
        case 1:
            deputysection.setUrl("/to/single-"
                    + deputysection.getTopic().getId() + "-"
                    + deputysection.getId() + ".htm");
            commondao.update(deputysection);
            break;

        // 列表
        case 2:
            deputysection.setUrl("/to/list-" + deputysection.getTopic().getId()
                    + "-" + deputysection.getId() + ".htm");
            commondao.update(deputysection);
            break;
        }
    }
}
