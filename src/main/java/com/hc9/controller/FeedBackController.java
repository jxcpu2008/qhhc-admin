package com.hc9.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.util.DateUtils;
import com.hc9.common.util.FileUtil;
import com.hc9.common.util.StringUtil;
import com.hc9.commons.normal.Validate;
import com.hc9.dao.entity.Feedbackinfo;
import com.hc9.dao.entity.Feedbacktype;
import com.hc9.dao.entity.Uploadfile;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;
import com.hc9.model.RechargeModel;
import com.hc9.service.ColumnManageService;
import com.hc9.service.EmailService;
import com.hc9.service.MyindexService;

import freemarker.template.TemplateException;

/**
 * <p>
 * Title:FeedBackController
 * </p>
 * <p>
 * Description: 邮件反馈
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author frank
 *         <p>
 *         date 2014年2月26日
 *         </p>
 */
@Controller
@RequestMapping("feedback")
@CheckLogin(value=CheckLogin.ADMIN)
public class FeedBackController {
    /**
     * 注入HibernateSupport
     */
    @Resource
    HibernateSupport commondao;
    /**
     * 注入ColumnManageService
     */
    @Resource
    ColumnManageService columnservice;

    /**
     * 注入MyindexController
     */
    @Resource
    MyindexService myindexservice;
    /**
     * 注入ColumnManageService
     */
    @Resource
    EmailService emailService;
    /**
     * 注入PageUrlController
     */
    @Resource
    PageUrlController pageUrlController;

    /** 引入log4j日志打印类 */
    private static final Logger LOGGER = Logger
            .getLogger(UserInfoController.class);

    /** 注入excel文件生成工具 */
    @Resource
    private RechargeModel modelRecharge;

    /**
     * 时间格式化
     */
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 打开邮件反馈管理页面
     * 
     * @param page
     *            PageModel
     * @param request
     *            HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping("/open")
    public ModelAndView open(PageModel page, HttpServletRequest request,
            String replyType, String replyStatus) {
        List<Feedbacktype> feedbacktypes = commondao
                .find("from Feedbacktype f where f.isShow=1");
        request.setAttribute("feedbacktypes", feedbacktypes);
        request.setAttribute("replyType", replyType);
        request.setAttribute("replyStatus", replyStatus == "" ? "-1"
                : replyStatus);
        request.setAttribute("emails",
                columnservice.queryAllFeedback(page, replyType, replyStatus));
        request.setAttribute("page", page);
        return new ModelAndView("WEB-INF/views/admin/column/feedback");
    }

    /**
     * 下载文件
     * 
     * @param id
     *            编号
     * @param model
     *            Model
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return JSONObject
     */
    @RequestMapping("/downFile")
    @ResponseBody
    public void downFile(Long id, Model model,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            Uploadfile f = columnservice.queryUploadFileById(id);
            String realPath = request.getSession().getServletContext()
                    .getRealPath("");
            FileUtil.downFile(realPath + "/upload/pdf/" + f.getSavePath().substring(f.getSavePath().lastIndexOf("/")),
                    f.getSaveName()+".pdf", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在线留言
     * 
     * @param feedbackinfo
     *            Feedbackinfo
     * @param request
     *            HttpServletRequest
     * @param imgupname
     *            附件上传的名称
     * @return String
     * @throws IOException
     *             异常
     */
    @RequestMapping("addMessage")
    public String addMessage(Feedbackinfo feedbackinfo,
            HttpServletRequest request, HttpServletResponse response,String imgupname) throws IOException {
        if (Validate.emptyStringValidate(imgupname)) {
            Map<String, Object> imgMap = myindexservice.upload(request,response);
            Uploadfile upfile = new Uploadfile();
            upfile.setAddTime(imgMap.get("uploadTime").toString());
            upfile.setName(imgupname);
            upfile.setSavePath(imgMap.get("imgurl").toString());
            upfile.setSaveName(imgMap.get("saveName").toString());
            columnservice.addObj(upfile);
            feedbackinfo.setUploadfile(upfile);
        }
        feedbackinfo.setAddTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
        feedbackinfo.setContext(StringUtil.replaceAll(feedbackinfo.getContext()));
        feedbackinfo.setReplyStatus(0);
        columnservice.addObj(feedbackinfo);
        request.setAttribute("msg", "我们已收到您的留言，感谢您提出宝贵的意见和建议！");
        return pageUrlController.forward(request,null, "to/single-7-69.htm",null);
    }

    /**
     * 将债权人信息导出excel
     * 
     * @param ids
     *            选中的债权人的id
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     */
    @RequestMapping("/table-to-excel")
    public void dataToExcel(String ids, HttpServletRequest request,
            HttpServletResponse response) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dataToExcel(HttpServletRequest request=" + request + ", HttpServletResponse response=" + response + ")方法开始"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        String headers = "留言人姓名,留言人邮件,留言人电话,留言内容,留言类型,留言时间";
        // 标题
        String[] header = this.sliptTitle(headers, null);
        int headerLength = header.length;
        // 查询所有信息
        List<Object[]> datalist = columnservice.queryAllFeedback();

        List<Map<String, String>> content = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;
        for (Object[] obj : datalist) {
            map = new HashMap<String, String>();
            // 将对应的值存放在map中
            for (int i = 0; i < headerLength; i++) {
                map.put(header[i], obj[i] == null ? " " : obj[i].toString());
            }

            content.add(map);
        }

        // 导出会员信息
        modelRecharge.downloadExcel("邮件反馈信息", null, header, content, response,request);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dataToExcel(HttpServletRequest, HttpServletResponse)方法结束"); //$NON-NLS-1$
        }
    }

    /**
     * <p>
     * Title: sliptTitle
     * </p>
     * <p>
     * Description: 分割字符串
     * </p>
     * 
     * @param titles
     *            要分割的字符串
     * @param regex
     *            根据什么分割
     * @return 分割后的数组
     */
    private String[] sliptTitle(String titles, String regex) {

        if (StringUtil.isNotBlank(regex)) {
            return titles.split(regex);
        } else {
            return titles.split(",");
        }
    }
    
    /**
     * 打开回复留言页面
     * @param fid 反馈信息id
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping("/forwardReply")
    public ModelAndView forwardReply(long fid, HttpServletRequest request) {
        request.setAttribute("feedbackinfo",
                commondao.get(Feedbackinfo.class, fid));
        return new ModelAndView("WEB-INF/views/admin/column/emailReply");
    }
    
    /**
     * 邮件反馈回复
     * @param replyContext 回复的内容
     * @param address 收件人地址
     * @param request HttpServletRequest
     * @return JSONObject
     * @throws IOException 异常
     * @throws TemplateException 异常
     */
    @RequestMapping("/reply")
    @ResponseBody
    public JSONObject sendResetEmail(String replyContext,long fid,HttpServletRequest request)
            throws IOException, TemplateException {
        JSONObject json = new JSONObject();
        try {
            Feedbackinfo feedbackinfo = commondao.get(Feedbackinfo.class, fid);
            feedbackinfo.setReplyStatus(1);
         // 发送邮件链接地址
            emailService.sendEmail("前海红筹邮件反馈回复", replyContext, feedbackinfo.getEmail());
            columnservice.updateObj(feedbackinfo);
            return columnservice.setJson(json, "200", "邮件发送成功", "main12", "closeCurrent");
        } catch (Exception e) {
            e.printStackTrace();
            return columnservice.setJson(json, "300", "邮件发送失败", "main12", "closeCurrent");
        }
    }
}
