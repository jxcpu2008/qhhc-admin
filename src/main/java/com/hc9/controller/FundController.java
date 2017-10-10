package com.hc9.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.common.util.DateUtils;
import com.hc9.model.PageModel;
import com.hc9.service.FundService;

/**
 * 资金统计
 * 
 * @author ransheng
 * 
 */

@Controller
@RequestMapping(value = { "fund" })
@SuppressWarnings(value = { "rawtypes" })
@CheckLogin(value = CheckLogin.ADMIN)
public class FundController {

    /**
     * 资金统计接口
     */
    @Resource
    private FundService fundservice;

    /**
     * @param request
     *            请求i对象
     * @return 放回路径 .jsp
     */
    @RequestMapping(value = { "fundlist", "/" })
    public ModelAndView getFund(HttpServletRequest request) {

        return new ModelAndView("WEB-INF/views/admin/fund/fundlist");
    }

    /**
     * 资金明细
     * 
     * @param beginDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param request
     *            请求路径
     * @return 返回路径.jsp
     */
    @RequestMapping(value = { "fundbasis", "/" })
    public ModelAndView queryBasis(HttpServletRequest request) {
        // 统计资金明细统计
        Object[] obj = fundservice.queryBasis();
        request.setAttribute("obj", obj);
        // 1、助人贷
        Object[] type1 = fundservice.queryBasisType(1L);
        request.setAttribute("type1", type1);
        // 2、助企贷
        Object[] type2 = fundservice.queryBasisType(2L);
        request.setAttribute("type2", type2);
        // 3、企业群联保贷
        Object[] type3 = fundservice.queryBasisType(3L);
        request.setAttribute("type3", type3);
        // 4、投资人周转贷
        Object[] type4 = fundservice.queryBasisType(4L);
        request.setAttribute("type4", type4);

        // 秒标
       // Object[] second = fundservice.queryBasisSecond();
       // request.setAttribute("second", second);
        return new ModelAndView("/WEB-INF/views/admin/fund/fundbasis");
    }

    /**
     * 查询资金流动明细
     * 
     * @param page
     *            分页对象
     * @param typeid
     *            资金类型
     * @param beginDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param request
     *            请求
     * @return 返回路径 .jsp
     */
    @RequestMapping(value = { "queryaccountinfo", "/" })
    public ModelAndView queryAccountinfo(
            @ModelAttribute(value = "PageModel") PageModel page,
            @RequestParam(value = "typeid", defaultValue = "", required = false) String typeid,
            String beginDate, String endDate, HttpServletRequest request) {
        // 查询资金类型
        List type = fundservice.quryType();
        if (beginDate == null) {
            beginDate = DateUtils.format(null);
        }
        if (endDate == null) {
            endDate = DateUtils.format(null);
        }
        // 查询条数
        Object count = fundservice.getcount(beginDate, endDate, typeid);
        page.setTotalCount(Integer.parseInt(count.toString()));
        // 查询资金明细数据
        List list = fundservice.queryAccountinfo(beginDate, endDate, typeid,
                page);
        request.setAttribute("type", type);
        request.setAttribute("list", list);
        request.setAttribute("page", page);
        request.setAttribute("beginDate", beginDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("typeid", typeid);

        return new ModelAndView("WEB-INF/views/admin/fund/accountinfo");
    }
}
