package com.hc9.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.dao.entity.LoansignComment;
import com.hc9.service.CommentService;

/**
 * 评论人回复
 * 
 * @author ransheng
 * 
 */
@Controller
@RequestMapping(value = { "comment" })
@CheckLogin(value=CheckLogin.ADMIN)
public class CommentConntroller {

    /** commentService */
    @Resource
    private CommentService commentService;

    /**
    * <p>Title: updateComment</p>
    * <p>Description:  回复评论</p>
    * @param comment 评论信息
    * @param request 请求
    * @return 是否回复成功
    */
    @ResponseBody
    @RequestMapping(value = { "updateComment", "/" })
    public boolean updateComment(
            @ModelAttribute(value = "Comment") LoansignComment comment,
            HttpServletRequest request) {
        commentService.updateComment(comment, request);
        return true;

    }
}
