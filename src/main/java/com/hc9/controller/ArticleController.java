package com.hc9.controller;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.constant.Constant;
import com.hc9.common.log.LOG;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.FileUtil;
import com.hc9.commons.normal.Validate;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.AppMessagePush;
import com.hc9.dao.entity.Article;
import com.hc9.model.PageModel;
import com.hc9.service.ArticleService;
import com.hc9.service.ColumnManageService;
import com.hc9.service.IMessagePushManageService;

@Controller
@RequestMapping(value = { "article" })
@CheckLogin(value=CheckLogin.ADMIN)
public class ArticleController {
	
	@Autowired
	private IMessagePushManageService messagePushService;
    
    /*** 引用ColumnManageService*/
    @Resource
    ColumnManageService columnservice;
    
    /*** 引用ArticleService*/
    @Resource
    ArticleService articleService;
    
    /*** page 分页*/
    private String page = "page";

    /**
     * 打开文章管理页面
     * 
     * @param pageModel 分页
     * @param title 文章标题
     * @param showStatus 是否显示
     * @param recommendStatus 是否推荐
     * @param deputyId 二级栏目名称
     * @param request 请求
     * @return 返回数据和页面
     * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = { "openArticles", "" })
    public ModelAndView openArticles(PageModel pageModel, String title,
            String showStatus, String recommendStatus, String deputyId,
            HttpServletRequest request) throws UnsupportedEncodingException {
        //对get提交的参数乱码进行处理
        if(request.getMethod().equals("GET")){
            if(Validate.emptyStringValidate(title)){
                title = new String(title.getBytes("iso-8859-1"),"UTF-8");
            }
            if(Validate.emptyStringValidate(deputyId)){
                deputyId = new String(deputyId.getBytes("iso-8859-1"),"UTF-8");
            }
        }
        request.setAttribute("articleList", articleService.queryAllArticles(pageModel,
                title, showStatus, recommendStatus, deputyId));
        request.setAttribute(page, pageModel);
        request.setAttribute("deputysections",
                columnservice.queryAllDeputysections());
        request.setAttribute("articleTitle", title);
        request.setAttribute("showStatus", showStatus == "" ? "-1" : showStatus);
        request.setAttribute("recommendStatus", recommendStatus == "" ? "-1"
                : recommendStatus);
        request.setAttribute("deputyId", deputyId);
        return new ModelAndView("WEB-INF/views/admin/column/deputylist2");
    }
    
    /**
     *  根据id查询文章详情
     * @param id 文章id
     * @param operation 当前所做的操作（add:添加; upt:修改）
     * @param request 请求
     * @return 返回数据和页面
     */
    @RequestMapping("/queryArticleById")
    public ModelAndView queryArticleById(long id, String operation,
            HttpServletRequest request) {
        request.setAttribute("topics", columnservice.queryAllTopics());
        request.setAttribute("operation", operation);
        if (operation.equals("upt")) {
            Article article = articleService.queryArticleById(id);
            request.setAttribute("article", article);
            request.setAttribute("topicId", article.getDeputysection()
                    .getTopic().getId());
        }
        return new ModelAndView("WEB-INF/views/admin/column/add_upt_article");
    }

    /**
     * 添加/修改文章
     * @param id 文章id
     * @param operation operation 当前所做的操作（add:添加; upt:修改）
     * @param article 文章
     * @param request 请求
     * @return 返回数据
     */
    @RequestMapping("/addOrUpdateArticle")
    @ResponseBody
    public JSONObject addOrUpdateArticle(@ModelAttribute("Article") Article article,long id,String deyId,
    		String operation, HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        try {
            if (article.getIsShow() == null) {
                article.setIsShow(0);
            }
            if (article.getIsRecommend() == null) {
                article.setIsRecommend(0);
            }
            if (article.getOrderNum() == null) {
                article.setOrderNum(0);
            }
           // 文件夹名称
            String folder = "article";
            // 上传图片
            String filePath = FileUtil.upload(request, "fileurl", folder);
            // 如果有图片上传
            if (filePath != null && !"1".equals(filePath.trim())) {
                // 删除图片
                FileUtil.deleteFile(article.getFilePath(), folder, request);
            }
            article.setFilePath(filePath);
            if (operation.equals("upt")) {
                articleService.updateArticle(article);
            } else if (operation.equals("add")) {
                article.setCreateTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
                articleService.addArticle(article,deyId);
            }
            
            // 需要推送公告
            Integer pushFlag = article.getIsPush();
            if (pushFlag != null && pushFlag.intValue() > 0) {
            	// 获取后台登录用户的真实姓名
            	Adminuser adminUser = (Adminuser) request.getSession().getAttribute(Constant.ADMINLOGIN_SUCCESS);
            	AppMessagePush message = new AppMessagePush();
            	message.setTitle("公告消息：" + article.getTitle());
            	// 操作者
            	message.setOperator(adminUser.getRealname());
            	message.setContent(article.getTitle());
            	// 广播推送，即全部注册用户
            	message.setPushTo(1);
            	message.setPushType(4);
            	// 立即推送
            	message.setPushNow(1);
            	message.setDescription("公告消息：" + article.getTitle());
            	
            	// 使用消息推送服务的统一推送入口
            	messagePushService.pushMessage(message);
            }
            
            //重置application
            columnservice.resetTopicDeputysectionArticle();
            return columnservice.setJson(json, "200", "更新成功", "main12", "closeCurrent");

        } catch (Exception e) {
        	LOG.error(e);
            return columnservice.setJson(json, "300", "更新失败", "main12", "closeCurrent");
        }
    }

    
    /**
     * 删除文章
     * @param ids 要删除的文章的id
     * @param request 请求
     * @return 返回数据
     */
    @RequestMapping("/deleteArticle")
    @ResponseBody
    public JSONObject deleteArticle(String ids, HttpServletRequest request) {
        JSONObject json = new JSONObject();
        try {
            columnservice.deleteMany(Article.class, ids);
            columnservice.resetTopicDeputysectionArticle();
            return columnservice.setJson(json, "200", "更新成功", "main12", "");
        } catch (Exception e) {
        	LOG.error(e);
            return columnservice.setJson(json, "300", "更新失败", "main12", "");
        }
    }
    
}
