package com.hc9.controller;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.Videobroadcast;
import com.hc9.model.PageModel;
import com.hc9.service.VideobroadcastService;

/**
 * 视频播报信息
 * 
 * @author ransheng
 * 
 */
@Controller
@RequestMapping(value = { "videobroadcast" })
@SuppressWarnings(value = {"rawtypes" })
@CheckLogin(value=CheckLogin.ADMIN)
public class VideobroadcastController {

    /**
     * 视频接口
     */
    @Resource
    private VideobroadcastService videoservice;

    /**
     * 视频播报信息分页展示
     * 
     * @param page
     *            分页对象
     * @param request
     *            请求路径
     * @return 返回路径.jsp
     */
    @RequestMapping(value = { "videopage", "/" })
    public ModelAndView videoPage(
            @ModelAttribute(value = "page") PageModel page,
            HttpServletRequest request) {
        // 得到信息条数
        Object count = videoservice.getCount();
        page.setTotalCount(Integer.parseInt(count.toString()));
        // 查询视频信息
        List list = videoservice.videoPage(page);
        request.setAttribute("page", page);
        request.setAttribute("list", list);
        return new ModelAndView("WEB-INF/views/admin/video/videolist");
    }

    /**
     * 打开视频播报编辑页面
     * 
     * @param id
     *            视频id
     * @param request
     *            请求
     * @return 返回路径.jsp
     */
    @RequestMapping(value = { "openvideo", "/" })
    public ModelAndView queryOnly(
            @RequestParam(value = "id", defaultValue = "", required = false) String id,
            HttpServletRequest request) {
        // 如果编号存在（属于编辑信息）否则是为新增信息
        if (!"".equals(id.trim())) {
            // 根据ID查询单条数据
            Videobroadcast video = videoservice.getOnly(id);
            request.setAttribute("video", video);
        }
        return new ModelAndView("WEB-INF/views/admin/video/updatevideo");
    }

    /**
     * 新增（编辑）视频播报信息
     * 
     * @param video视频信息对象
     * @param request
     *            请求对象
     * @param video
     *            视频对象
     * @param num
     *            排序编号
     * @return dwz json对象
     */
    @RequestMapping(value = { "updatevideo", "/" }, method = RequestMethod.POST)
    @ResponseBody
    public JSONObject saveORupdateVideo(
            @ModelAttribute(value = "Videobroadcast") Videobroadcast video,
            @RequestParam(value = "num", defaultValue = "", required = false) String num,
            HttpServletRequest request) {
        JSONObject json = new JSONObject();
        try {
            // 得到当前时间
            video.setAddTime(DateUtils.format(null));
            if (!num.trim().equals("")) {
                // 排列顺序只能是正整数正则
                Pattern pattern = Pattern.compile("[1-9]{1}[0-9]*$");
                Matcher matcher = pattern.matcher(num);
                if (matcher.matches()) {
                    video.setShowNum(Integer.parseInt(num));
                } else {
                    json.element("message", "显示顺序为正整数");
                    json.element("statusCode", "300");
                    json.element("callbackType", "closeCurrent");
                    return json;
                }
            }

            // 新增（编辑）视频播报信息
            Boolean b = videoservice.saveORupdateVideo(video, request);
            if (b) {
                // dwz返回json对象
                json.element("statusCode", "200");
                json.element("message", "更新成功");
                json.element("navTabId", "main11");
                json.element("callbackType", "closeCurrent");
            } else {
                json.element("statusCode", "300");
                json.element("message", "请上传JPG、PNG、GIF图片类型");
            }
            return json;
        } catch (Throwable e) {
            json.element("message", "更新失败");
            json.element("statusCode", "300");
            json.element("callbackType", "closeCurrent");
            e.getMessage();
            return json;
        }
    }

    /**
     * 根据id删除多条播报信息
     * 
     * @param ids
     *            播报信息id
     * @return dwz json数据
     */
    @RequestMapping(value = { "deletevideo", "/" })
    @ResponseBody
    public JSONObject deleteVideo(
            @RequestParam(value = "ids", defaultValue = "", required = true) String ids) {
        JSONObject json = new JSONObject();
        try {
            // 删除视频播报信息
            videoservice.deleteVideo(ids);
            // dwz返回json对象
            json.element("statusCode", "200");
            json.element("message", "删除成功");
            json.element("navTabId", "main11");
            return json;
        } catch (Throwable e) {
            json.element("message", "删除失败");
            json.element("statusCode", "300");
            e.getMessage();
            return json;
        }
    }

}
