package com.hc9.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.util.ArrayToJson;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.OnlineApplyInfo;
import com.hc9.model.PageModel;
import com.hc9.service.OnlineApplyService;

/**
 * <p>
 * Title:OnlineApplyInfoConntroller
 * </p>
 * <p>
 * Description: 在线申请控制层
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>
 * 
 * @author longyang
 *         <p>
 *         date 2014年2月13日
 *         </p>
 */
@Controller
@RequestMapping("/onlineapply")
@CheckLogin(value=CheckLogin.ADMIN)
public class OnlineApplyInfoConntroller {

    /** onlineApplyService */
    @Resource
    private OnlineApplyService onlineApplyService;

    /**
     * <p>
     * Title: addOnlineApply
     * </p>
     * <p>
     * Description: 前台在线申请提交
     * </p>
     * 
     * @param name
     *            真实姓名
     * @param provinceId
     *            省
     * @param cityId
     *            市
     * @param money
     *            投资金额
     * @param phone
     *            电话
     * @return 是否成功
     */
    @RequestMapping("/add")
    @ResponseBody
    public boolean addOnlineApply(String name, String provinceId,
            String cityId, String money, String phone) {
        OnlineApplyInfo onlineApplyInfo = new OnlineApplyInfo();
        onlineApplyInfo.setName(name);
        onlineApplyInfo.setProvinceId(Long.valueOf(provinceId));
        onlineApplyInfo.setCityId(Long.valueOf(cityId));
        onlineApplyInfo.setPhone(phone);
        onlineApplyInfo.setMoney(Double.valueOf(money));
        onlineApplyInfo.setState(0);
        onlineApplyInfo.setContent("前海红筹");
        return onlineApplyService.save(onlineApplyInfo);
    }

    /**
     * <p>
     * Title: index
     * </p>
     * <p>
     * Description:进入在线申请管理
     * </p>
     * 
     * @return 在线申请管理展示页面
     */
    @RequestMapping(value = { "index", "/" })
    public ModelAndView index() {
        return new ModelAndView("WEB-INF/views/admin/online_apply_list");
    }

    /**
     * <p>
     * Title: queryPage
     * </p>
     * <p>
     * Description: 列表
     * </p>
     * 
     * @param page
     *            分页
     * @param request
     *            请求
     * @param onlineApplyInfo
     *            查询条件
     * @param limit
     *            limit
     * @param start
     *            start
     * @return 集合
     */
    @ResponseBody
    @RequestMapping("/querypage")
    @SuppressWarnings("rawtypes")
    public JSONObject queryPage(PageModel page, HttpServletRequest request,
            OnlineApplyInfo onlineApplyInfo, String limit, String start) {

        JSONObject resultjson = new JSONObject();

        JSONArray jsonlist = new JSONArray();

        // 每页显示条数
        if (StringUtil.isNotBlank(limit) && StringUtil.isNumberString(limit)) {
            page.setNumPerPage(Integer.parseInt(limit) > 0 ? Integer
                    .parseInt(limit) : 10);
        } else {
            page.setNumPerPage(10);
        }
        // 计算当前页
        if (StringUtil.isNotBlank(start) && StringUtil.isNumberString(start)) {
            page.setPageNum(Integer.parseInt(start) / page.getNumPerPage() + 1);
        }
        List datalist = onlineApplyService.queryonlinePage(page,
                onlineApplyInfo);
        String titles = "id,usename,proname,cityname,phone,money,content,state";

        // 将查询结果转换为json结果集
        ArrayToJson.arrayToJson(titles, datalist, jsonlist);

        resultjson.element("rows", jsonlist);
        resultjson.element("total", page.getTotalCount());
        return resultjson;

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
    @ResponseBody
    @RequestMapping(value = { "iscontacted", "/" })
    public boolean isContacted(String id) {
        onlineApplyService.isContacted(id);
        return true;
    }

    /**
     * <p>
     * Title: deleteOnlineApply
     * </p>
     * <p>
     * Description: 删除在线申请
     * </p>
     * 
     * @param ids
     *            集合
     * @param request
     *            请求
     * @return 是否成功
     */
    @ResponseBody
    @RequestMapping(value = { "delete", "/" })
    public boolean deleteOnlineApply(String ids,
            HttpServletRequest request) {
        return onlineApplyService.deleteOnline(ids);
    }

}
