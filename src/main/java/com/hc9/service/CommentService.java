package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.LoansignComment;

/**
 * 借款标评论service
 * 
 * @author Administrator
 * 
 */
@Service
public class CommentService {
    /** dao*/
    @Resource
    private HibernateSupportTemplate dao;

    /** loanSignQuery*/
    @Resource
    private LoanSignQuery loanSignQuery;

    /**
    * <p>Title: getCommentCount</p>
    * <p>Description: 借款标为id的评论的条数</p>
    * @param loansignId 借款标号
    * @return 天数
    */
    public int getCommentCount(int loansignId) {
        StringBuffer sb = new StringBuffer(
                "SELECT COUNT(1) from comment where loansign_id=");
        return loanSignQuery.queryCount(sb.append(loansignId).toString());
    }

    /**
    * <p>Title: queryCommentList</p>
    * <p>Description: 通过借款标标号查询到该借款标的评论信息</p>
    * @param start start
    * @param limit limit
    * @param loanSignId 借款标id
    * @return list
    */
    public List<LoansignComment> queryCommentList(int start, int limit, int loanSignId) {
        List list = new ArrayList();
        StringBuffer sb = new StringBuffer(
                "SELECT COMMENT .id, COMMENT .cmtcontent, userbasicsinfo. NAME, COMMENT .cmtReply, ");
        sb.append(" CASE WHEN COMMENT .cmtIsShow = 1 THEN '显示' ELSE '不显示' END FROM `comment` INNER JOIN ");
        sb.append(
                " userbasicsinfo ON `comment`.commentator_id = userbasicsinfo.id WHERE loanSign_id = ")
                .append(loanSignId);
        sb.append(" LIMIT ").append(start).append(" , ").append(limit);
        list = dao.findBySql(sb.toString());
        return list;
    }

    /**
     * <p>
     * Title: updateComment
     * </p>
     * <p>
     * Description: 回复评论
     * </p>
     * 
     * @param comment
     *            评论对象
     * @param request
     *            请求
     * @return true or false
     */
    public boolean updateComment(LoansignComment comment, HttpServletRequest request) {
        LoansignComment comMent = dao.get(LoansignComment.class, comment.getId());
        Adminuser adminuser = (Adminuser) request.getSession().getAttribute(
                Constant.ADMINLOGIN_SUCCESS);
        // 回复人
        comment.setAdminuser(adminuser);
        // 回复时间
        comMent.setReplyTime(DateUtils.format(null));
        // 回复类容
        comMent.setCmtReply(comment.getCmtReply());
        // 是否显示
        comMent.setCmtIsShow(comment.getCmtIsShow());

        // 编辑
        dao.update(comMent);
        return true;

    }
}
