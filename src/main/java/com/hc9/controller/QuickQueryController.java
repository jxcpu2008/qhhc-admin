package com.hc9.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc9.common.annotation.CheckLogin;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.model.PageModel;
import com.hc9.service.QuickQueryService;
import com.hc9.service.UserbasicsinfoService;

@Controller
@CheckLogin(value=CheckLogin.ADMIN)
@RequestMapping("/quickQuery")
public class QuickQueryController{
	
	@Resource
	private QuickQueryService quickQueryService;
	
	@Resource
	private  UserbasicsinfoService userbasicsinfoService;
	
	
	/***
	 * 快速查询
	 * @param request
	 * @return
	 */
    @RequestMapping("/index")
    public String open(HttpServletRequest request) {
    	//今日注册
    	List  topTenUserInfo=quickQueryService.getTopTenUserInfo();
    	//今日充值
    	List  topTenRecharge=quickQueryService.getTopTenRecharge();
    	//今日认购
    	List  topTenLoanRecord=quickQueryService.getTopTenLoanRecord();
    	//今日提现
    	List  topTenWithdraw=quickQueryService.getTopTenWithdraw();
    	request.setAttribute("topTenUserInfo", topTenUserInfo);
    	request.setAttribute("topTenRecharge", topTenRecharge);
    	request.setAttribute("topTenLoanRecord", topTenLoanRecord);
    	request.setAttribute("topTenWithdraw", topTenWithdraw);
       return "WEB-INF/views/admin/quickquery/quickquery";
    }
    
    /***
     * 根据输入框查询
     * @param request
     * @param name
     * @return
     */
    @RequestMapping("/selUser")
    @ResponseBody
    public List selUser(HttpServletRequest request,String name){
    	List selUser=quickQueryService.selUser(name);
    	return selUser;
    }
    
    /**
     * 显示用户详情数据
     * @param request
     * @return
     */
    @RequestMapping("/openUserQuickQuery")
    public String openUserQuickQuery(HttpServletRequest request,Long userId){
    	Userbasicsinfo user = userbasicsinfoService.queryUserById(userId);
    	//用户基本信息
    	List userInfoList=quickQueryService.getUserDepartment(user.getId());
    	//累积充值
    	Double rechargeSum=quickQueryService.getRechargeSum(user.getId());
    	//累计提现
    	Double withdrawSum=quickQueryService.getWithdrawSum(user.getId());
    	//累计认购金额
    	Double loanRecordSum=quickQueryService.getLoanRecordSum(user.getId());
    	//累计推荐人数
    	Integer generalizeCount=quickQueryService.getGeneralizeCount(user.getId());
    	//累计推荐佣金
    	Double generalizeSum=quickQueryService.getGeneralizeMoneySum(user.getId());
    	//待回款本金
    	Double moneyCollection=quickQueryService.getTenderMoneyCollection(user.getId());
    	//已回款本金
    	Double moneyReceived=quickQueryService.getTenderMoneyReceived(user.getId());
    	//待收收益
    	Double dueRepayCollection=quickQueryService.getDueRepayCollection(user.getId());
    	//已收收益
    	Double dueRepayReceived=quickQueryService.getHostIncomeReceived(user.getId());
    	//待收佣金
    	Double bonusCollection=quickQueryService.getBonusCollection(user.getId());
    	//已收佣金
    	Double bonusReceived=quickQueryService.getBonusReceived(user.getId());
    	//用户资金流水记录
    	List accountInfoList=quickQueryService.getAccountInfo(user.getId());
    	//用户认购流水
    	List loanRecordList=quickQueryService.getUserLoanRecord(user.getId());
    	//红包显示记录
    	List redenvelopeList=quickQueryService.getRedenvelopedetail(user.getId());
    	//红包个数20150817-lkl
    	Integer redCount = quickQueryService.queryRedCount(null, null, null, user.getId());
    	//红包总金额20150817-lkl
    	Double redMoney=quickQueryService.queryRedMoney(user.getId());
    	//加息卷个数20150817-lkl
    	Integer cardCount=quickQueryService.queryInterestincreasecardCount(null, null, null, user.getId());
    	//加息卷查询
    	List listCard=quickQueryService.getInterestincreasecard(user.getId());
    	//渠道名称
    	String chname=quickQueryService.getName(user.getUserName().trim());
    	request.setAttribute("money", user.getUserfundinfo().getCashBalance());
    	request.setAttribute("userInfoList", userInfoList);
    	request.setAttribute("rechargeSum", rechargeSum);
    	request.setAttribute("withdrawSum", withdrawSum);
    	request.setAttribute("loanRecordSum", loanRecordSum);
    	request.setAttribute("generalizeCount", generalizeCount);
    	request.setAttribute("generalizeSum", generalizeSum);
    	request.setAttribute("moneyCollection", moneyCollection);
    	request.setAttribute("moneyReceived", moneyReceived);
    	request.setAttribute("dueRepayCollection", dueRepayCollection);
    	request.setAttribute("dueRepayReceived", dueRepayReceived);
    	request.setAttribute("bonusCollection", bonusCollection);
    	request.setAttribute("bonusReceived", bonusReceived);
    	request.setAttribute("accountInfoList", accountInfoList);
    	request.setAttribute("loanRecordList", loanRecordList);
    	request.setAttribute("redenvelopeList", redenvelopeList);
    	request.setAttribute("redCount", redCount);
    	request.setAttribute("redMoney", redMoney);
    	request.setAttribute("cardCount", cardCount);
    	request.setAttribute("listCard", listCard);
    	request.setAttribute("chname", chname);
    	if(user.getCardStatus()==2){
    		request.setAttribute("name", user.getName());
    	}else{
    		request.setAttribute("name", "此用户未实名认证");
    	}
    	 return "WEB-INF/views/admin/quickquery/userQuickQuery";
    }
    
    /***
     * 查询所有的认购数据
     * @param page
     * @param beginDate
     * @param endDate
     * @param userId
     * @param status
     * @param request
     * @return
     */
   @RequestMapping("/openUserLoanRecordList")
    public String openUserLoanRecordList(@ModelAttribute("PageModel") PageModel page,
            String beginDate, String endDate, Long userId,Integer status, HttpServletRequest request) {
        Integer count = quickQueryService.queryCount(beginDate, endDate, status, userId);
        page.setTotalCount(count);
        List list = quickQueryService.getUserLoanRecordList(page,userId, beginDate, endDate, status);
        request.setAttribute("list", list);
        request.setAttribute("page", page);
        request.setAttribute("userId", userId);
        return "/WEB-INF/views/admin/quickquery/loanRecordList";
    }
    
    
    /***
     * 查询所有的资金流水
     * @param page
     * @param beginDate
     * @param endDate
     * @param userId
     * @param accountType
     * @param request
     * @return
     */
     @RequestMapping("/openAccountInfoList")
    public String openAccountInfoList(@ModelAttribute("PageModel") PageModel page,
            String beginDate, String endDate, Long userId,Integer accountType, HttpServletRequest request) {
        Integer count = quickQueryService.getAccountInfoCount(userId, beginDate, endDate, accountType);
        page.setTotalCount(count);
        List list = quickQueryService.getAccountInfoList(page,userId, beginDate, endDate, accountType);
        Double sumMoney=quickQueryService.sumAccountInfo(userId, beginDate, endDate, accountType);
        request.setAttribute("list", list);
        request.setAttribute("page", page);
        request.setAttribute("userId", userId);
        request.setAttribute("sumMoney", sumMoney);
        return "/WEB-INF/views/admin/quickquery/accountInfoList";
    }
     
     /***
      * 查询所有的红包
      * @param page
      * @param beginDate
      * @param endDate
      * @param userId
      * @param status
      * @param request
      * @return
      */
     @RequestMapping("/openRedenvelopedetailList")
     public String openRedenvelopedetailList(@ModelAttribute("PageModel") PageModel page,
             String beginDate, String endDate, Long userId,Integer status, HttpServletRequest request) {
    	 Integer count = quickQueryService.queryRedCount(beginDate, endDate, status, userId);
         page.setTotalCount(count);
         List list = quickQueryService.getRedenvelopedetailList(page, userId, beginDate, endDate, status);
         request.setAttribute("list", list);
         request.setAttribute("page", page);
         request.setAttribute("userId", userId);
         return "/WEB-INF/views/admin/quickquery/redenvelopedetailList";
     }
     
     /***
      * 加息卷查询
      * @param page
      * @param beginDate
      * @param endDate
      * @param userId
      * @param status
      * @param request
      * @return
      */
     @RequestMapping("/openInCardList")
     public String openInterestincreasecardList(@ModelAttribute("PageModel") PageModel page,
             String beginDateC, String endDateC, Long userId,Integer status, HttpServletRequest request) {
    	 Integer count = quickQueryService.queryInterestincreasecardCount(beginDateC, endDateC, status, userId);
         page.setTotalCount(count);
         List list = quickQueryService.getCardList(page, userId, beginDateC, endDateC, status);
         request.setAttribute("list", list);
         request.setAttribute("page", page);
         request.setAttribute("userId", userId);
         return "/WEB-INF/views/admin/quickquery/interestincreasecardList";
     }
    

}
