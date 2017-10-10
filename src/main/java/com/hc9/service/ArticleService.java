package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.commons.normal.Validate;
import com.hc9.dao.entity.Article;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

@Service
public class ArticleService {

    /**
     * 注入HibernateSupport
     */
    @Resource
    HibernateSupport commondao;
    
    /**
     * 按条件分页查询文章
     * 
     * @param page
     * @param title
     *            (标题)
     * @param showStatus
     *            (是否显示)
     * @param recommendStatus
     *            (是否推荐)
     * @param deputyId
     *            (二级栏目名称)
     * @return List
     */
    public List queryAllArticles(PageModel page, String title,
            String showStatus, String recommendStatus, String deputyId) {
        StringBuilder sb = new StringBuilder(
                "from Article where 1 = 1");
        List<Object> param = new ArrayList<Object>();
        if (Validate.emptyStringValidate(title)) {
            sb.append(" and title like  ?");
            param.add("%" + title + "%");
        }
        if (Validate.emptyStringValidate(showStatus)
                && !showStatus.equals("-1")) {
            sb.append(" and isShow = ?");
            param.add(showStatus);
        }
        if (Validate.emptyStringValidate(recommendStatus)
                && !recommendStatus.equals("-1")) {
            sb.append(" and isRecommend = ?");
            param.add(recommendStatus);
        }
        if (Validate.emptyStringValidate(deputyId)) {
            sb.append(" and deputysection.id = ?");
            param.add(deputyId);
        }
        sb.append(" order by id desc");
        Object[] params = null;
        if (param.size() > 0) {
            params = param.toArray();
        }
        List list= commondao.pageListByHql(page, sb.toString(), false, params);
        return list;
    }
    
    /**
     * 根据id查询文章详情
     * 
     * @param id
     *            (文章编号)
     * @return 返回文章
     */
    public Article queryArticleById(long id) {
        return commondao.get(Article.class, id);
    }
    
    /**
     * 修改文章
     * 
     * @param article
     *            文章
     */
    public void updateArticle(Article article) {
        commondao.update(article);
    }
    
    /**
     * 新增文章
     * 
     * @param article
     *            文章
     */
    public void addArticle(Article article,String id) {
        Article art = null;
        long did = (Long) commondao.save(article);
        art = commondao.get(Article.class, did);
        art.getDeputysection().setId(Long.parseLong(id));
        art.setUrl("to/article-"
                + article.getDeputysection().getTopic().getId() + "-"
                + article.getDeputysection().getId() + "-" + article.getId()
                + ".htm");
        commondao.update(article);
    }
}
