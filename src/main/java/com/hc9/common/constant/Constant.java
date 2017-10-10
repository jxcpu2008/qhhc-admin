package com.hc9.common.constant;

import com.hc9.common.util.ParameterIps;

/**
 * 基础系统常量
 * 
 * @author Frank
 * 
 */
public interface Constant {

    /**
     * 默认编码格式
     */
    String CHARSET_DEFAULT = "UTF-8";
    
    /**
     * 一段时间内最大请求次数
     */
    int NUMBER_MAX_QUICK_REQUEST_COUNT = 20;
    
    /**
     * 一段时间
     */
    int NUMBER_MAX_QUICK_REQUEST_TIME = 10000;
    
    /**
     * 首页
     */
    String WEB_INDEX = "index";
    
    String SUCCESS="0000";
    
    /**
     * URL_ERROR_500
     */
    String URL_ERROR_500 = "/error-500.jsp";
    
    /**
     * 服务器域名
     */
    String WEBSERVER = "http://www.hc9.com";
    
    /**
     * URL_ERROR_500_MSG
     */
    String URL_ERROR_500_MSG = "msg";
    
    /**
     * URL_ERROR_500_MSG_VAL_0
     */
    String URL_ERROR_500_MSG_VAL_0 = "您请求的方式非法！";
    
    /**
     * URL_SUCCESS_REGIST
     */
    String URL_SUCCESS_REGIST = "/member_index/member_center.htm";

    /**URL_LOGIN*/
    String URL_LOGIN = "/visitor/to-login";
    
    /**
     * PATH_MARKER_MODEL
     */
    String PATH_MARKER_MODEL = "config/marker/html/";
    
    /**
     * PATH_DYNAMIC_VIEW
     */
    String PATH_DYNAMIC_VIEW = "views/framework/";
    
    /**
     * DEFAULT_TIME_FORMAT
     */
    String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * DEFAULT_DATE_FORMAT
     */
    String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 产品认购信息
     */
    String ATTRIBUTE_PRODUCT_PAY_INFO = "attribute_product_pay_info";
    
    
    /**
     * ATTRIBUTE_UPDATE_HEADER_TIME
     */
    String ATTRIBUTE_UPDATE_HEADER_TIME = "update_header_time";

    /**
     * ATTRIBUTE_ROOT_PATH
     */
    String ATTRIBUTE_ROOT_PATH = "root_path";

    /**
     * ATTRIBUTE_MSG
     */
    String ATTRIBUTE_MSG = "msg";
    
    /**
     * ATTRIBUTE_USER
     */
    String ATTRIBUTE_USER = "session_user";

    /**
     * ATTRIBUTE_REGIST_CHECK_CODE
     */
    String ATTRIBUTE_REGIST_CHECK_CODE = "regist_check_code";
    
    /**
     * ATTRIBUTE_LOGIN_CHECK_CODE
     */
    String ATTRIBUTE_LOGIN_CHECK_CODE = "login_check_code";

    /**
     * ATTRIBUTE_TOPIC
     */
    String ATTRIBUTE_TOPIC = "topics";
    
    /**
     * ATTRIBUTE_ACTIVE_TOPIC
     */
    String ATTRIBUTE_ACTIVE_TOPIC = "activetopic";

    /**
     * PROPERTIES_MSG_TITLE_WELCOME_REGIST
     */
    String PROPERTIES_MSG_TITLE_WELCOME_REGIST = "msg_title_welcome_regist";
    
    /**
     * PROPERTIES_MSG_CONTEXT_WELCOME_REGIST
     */
    String PROPERTIES_MSG_CONTEXT_WELCOME_REGIST = "msg_context_welcome_regist";

    /**
     * PROPERTIES_EMAIL_SUBJECT_ACCOUNT_ACTIVATE
     */
    String PROPERTIES_EMAIL_SUBJECT_ACCOUNT_ACTIVATE = "msg_subject_account_activate";
    
    /**
     * PROPERTIES_EMAIL_CONTEXT_ACCOUNT_ACTIVATE
     */
    String PROPERTIES_EMAIL_CONTEXT_ACCOUNT_ACTIVATE = "msg_context_account_activate";

    /**
     * NUMBER_MAX_ERROR_LOGIN
     */
    int NUMBER_MAX_ERROR_LOGIN = 5;

    /** 后台会员登录成功 */
    String ADMINLOGIN_SUCCESS = "adminuser";

    /** 前台会员登录成功 */
    String SESSION_USER = "session_user";
    
    /** 个人中心左菜单显示状态显示*/    
    String BORROW_APPLY="borrow_apply";
    /** 返回http状态码 请求成功 */
    String HTTP_STATUSCODE_SUCCESS = "200";

    /** 返回http状态码 请求错误 */
    String HTTP_STATUSCODE_ERROR = "300";

    /** 返回http状态码 session失效 */
    String HTTP_STATUSCODE_TIME_OUT = "301";

    /**
     * 有关平台的一些状态定义
     * 0*/
    Integer STATUES_ZERO = 0;
    
    /**
     * 有关平台的一些状态定义
     *  1*/
    Integer STATUES_ONE = 1;
    
    /**
     * 有关平台的一些状态定义
     *  2*/
    Integer STATUES_TWO = 2;
    
    /**
     * 有关平台的一些状态定义
     *  3*/
    Integer STATUES_THERE = 3;
   
    /**
     * 有关平台的一些状态定义
     * 4
     */
    Integer STATUES_FOUR = 4;
    
    /**
     * 有关平台的一些状态定义
     * 5
     */
    Integer STATUES_FIVE = 5;
    /**有关平台的一些状态定义
     * 6
     */
    Integer STATUES_SIX = 6;
    /**有关平台的一些状态定义
     * 7*/
    Integer STATUES_SEVEN = 7;
    /**有关平台的一些状态定义
     * 8
     * */
    Integer STATUES_EIGHT = 8;
    /**有关平台的一些状态定义
     * 9
     * */
    Integer STATUES_NINE = 9;
    /**有关平台的一些状态定义
     * 10
     * */
    Integer SRSRUES_TEN = 10;
    /**有关平台的一些状态定义
     * 11
     * */
    Integer SRSRUES_ELEVEN = 11;


    /** 逾期利息(该逾期利息由平台定义)*/
    Double OVERDUE_INTEREST = 0.02;


    /**产品的计算公式*/
    String FORMULA = "((投资金额*客户年化收益率)/365)*期限(天)";


    
    
    /**短信失效毫秒数*/
    Long MILLISECONDS = 2 * 60 * 1000l;
    
//    WebService请求
    /**转账*/
    String TRANSFER="Transfer";
    /**银行列表*/
    String GET_BANK_LIST="GetBankList";
    /**账户余额查询*/
    String QUERY_FOR_ACCBALANCE="QueryForAccBalance";
    /**解冻保证金*/
    String GUARANTEE_UNFREEZE="GuaranteeUnfreeze";
    /**查询托管用户信息**/
    String QUERY_MER_USER_INFO="QueryMerUserInfo";
    /**
     * 前台环讯返回地址
     */
    String WEB_URL = ParameterIps.getWeburl();
    /**充值*/
    String RECHARGEURL = WEB_URL+"processing/recharge.htm";
    String ASYNCHRONISMRECHARGE = WEB_URL+ "processing/asynchronismRecharge.htm";
    
    /**H5充值*/
    String H5RECHARGEURL = WEB_URL+"h5/rechargeSuccess.htm";
    String H5ASYNCHRONISMRECHARGE = WEB_URL+ "processing/h5asynchronismRecharge.htm";
    
    /**第三方担保充值同步*/
    String ESCROWRECHARGE = WEB_URL+"processing/escrowRecharge.htm";
    /**第三方担保充值异步*/
    String ASYNESCROWRECHARGE = WEB_URL+ "processing/asynEscrowRecharge.htm";
    /**APP充值回调方法地址*/
    String APP_RECHARGEURL = WEB_URL+"app/recharge";
    /**APP充值异步回调方法地址*/
    String APP_ASYNCHRONISMRECHARGE = WEB_URL+ "app/asynchronismRecharge";
    /**用户注册*/
    String REGISTRATION = WEB_URL+"processing/registration.htm";
    String ASYNCHRONISMREGISTRATION = WEB_URL+"processing/asynchronismRegistration.htm";
    /**H5用户注册*/
    String H5ASYNCHRONISMREGISTRATION = WEB_URL+"processing/h5asynchronismRegistration.htm";
    /**提现*/
    String WITHDRAWAL = WEB_URL+"processing/withdrawal.htm";
    String WITHDRAWASYNCHRONOUS = WEB_URL+"processing/withdrawAsynchronous.htm";
    /**H5提现*/
    String H5WITHDRAWAL = WEB_URL+"h5/withdrawCashSuccess.htm";
    String H5WITHDRAWASYNCHRONOUS = WEB_URL+"processing/h5withdrawAsynchronous.htm";
    
    /**第三方担保提现*/
    String ESCROWWITHDRAWAL = WEB_URL+"processing/escrowWithdrawal.htm";
    String ASYNESCROWWITHDRAW = WEB_URL+"processing/asynEscrowWithdraw.htm";
    
    /**绑定银行卡*/
    String OPBANKCARD = WEB_URL+"processing/opBankCard.htm";
    String ASYNOPBANKCARD = WEB_URL+"processing/asynOpBankCard.htm";
    
    /**APP提现回调方法地址*/
    String APP_WITHDRAWAL = WEB_URL+"app/withdrawal";
    String APP_WITHDRAWASYNCHRONOUS = WEB_URL+"app/withdrawAsynchronous";
    /**还款*/
    String REPAYMENT = WEB_URL+"processing/repayment.htm";
    String REPAYMENTASYNCHRONOUS=WEB_URL+"processing/repaymentAsynchronous.htm";
    
    /**店铺投标*/
    String PROBID = WEB_URL+"processing/returnShopBid.htm";
    String PROASYNCHRONISMBID = WEB_URL+"processing/asynchronismShopBid.htm";
    /**店铺投标 APP**/
    String PROBID_APP = WEB_URL+"app/returnShopBid";
    String PROASYNCHRONISMBID_APP = WEB_URL+"app/asynchronismShopBid";
    
    /**
     * 店铺满标页面跳转
     */
    String SHOP = WEB_URL+"processing/shopBidFull.htm";
    /**
     * 店铺满标异步处理
     */
    String ASYNCRONISSHOP=WEB_URL+"processing/asyncronisShopBidFull.htm";
    
    /**店铺流标*/
    String BIDSHOPFLOW = WEB_URL+"processing/returnBidShopFlow.htm";
    /**店铺流标异步*/
    String SYNCHRONIZEDBIDSHOPFLOW = WEB_URL+"processing/asynchronismBidShopFlow.htm";
    
    /**项目投标*/
    String BID = WEB_URL+"processing/returnLoanBid.htm";
    String ASYNCHRONISMBID = WEB_URL+"processing/asynchronismLoanBid.htm";
    
    String BID_APP = WEB_URL+"app/returnLoanBid";
    String ASYNCHRONISMBID_APP = WEB_URL+"app/asynchronismLoanBid";
    
    /**service第三方担保注册宝付授权**/
    String INACCREDIT=WEB_URL+"processing/returnInaccredit.htm";
    String ASYNCHRONISMINACCREDIT=WEB_URL+"processing/asynchronismInaccredit.htm";
    
    /***用户注册宝付授权**/
    String INACCREDITUSRE=WEB_URL+"processing/returnInaccreditUser.htm";
    String ASYNCHRONISMINACCREDITUSER=WEB_URL+"processing/asynchronismInaccreditUser.htm";
    /***用户注册宝付授权**/
    String H5INACCREDITUSRE=WEB_URL+"h5/baofooSuccess.htm";
    String H5ASYNCHRONISMINACCREDITUSER=WEB_URL+"processing/h5asynchronismInaccreditUser.htm";
    /**
     * 项目满标页面跳转
     */
    String LOANCREDIT = WEB_URL+"processing/returnLoanCredit.htm";
    /**
     * 项目满标异步处理
     */
    String ASYNCHRONISMLOANCREDIT=WEB_URL+"processing/asynchronismLoanCredit.htm";
    
    /**项目流标*/
    String RETURNLOANFLOW = WEB_URL+"processing/returnLoanFlow.htm";
    /**项目流标异步*/
    String ASYNCHRONISMLOANFLOW = WEB_URL+"processing/asynchronismLoanFlow.htm";
    
    
    /**项目还款page_url*/
    String REPAYMENT_SIGN=WEB_URL+"processing/returnRepaymentSign.htm";
    /**项目还款server_url*/
    String REPAYMENT_SIGN_ASYNCHRONOUS=WEB_URL+"processing/asynchronismRepaymentSign.htm";
    
    /**店铺还款page_url*/
    String ProREPAYMENT_SIGN=WEB_URL+"processing/returnProRepaymentSign.htm";
    /**店铺还款server_url*/
    String ProREPAYMENT_SIGN_ASYNCHRONOUS=WEB_URL+"processing/asynchronismProRepaymentSign.htm";
    
    /**会员升级page_url*/
    String TransferUrl=WEB_URL+"processing/returnTransferSign.htm";
    /**会员升级server_url*/
    String TransferUrl_SIGN=WEB_URL+"processing/asynchronismTransferSign.htm";
    
    /***** 下面的没用到***/
    
    
    
    /**债权转让投标*/
    String BIDASSIGNMENT=WEB_URL+"plank/returnBidAssignment.htm";
    String ASYNCHRONISMBIDASSIGNMENT = WEB_URL+"processing/asynchronismBidAssignment.htm";
    /**债权转让放款*/
    String LOANSASSIGNMENT = WEB_URL+"processing/loansAssignment.htm";    
    /**发布*/
    String REGISTER_SUBJECT=WEB_URL+"baseLoanSign/pubback.htm";
    String REGISTER_SUBJECT_ASYNCHRONOUS=WEB_URL+"processing/pubback.htm";
    
    /**自动投标规则*/
    String AUTOMATIC=WEB_URL+"plank/returnAutomatic.htm";
    String ASYNCHRONISMAUTOMATIC = WEB_URL+"processing/asynchronismAutomatic.htm";

    /**解冻保证金*/
    String GUARANTEE_UNFREEZE_ASYNCHRONOUS=WEB_URL+"processing/guaranteeUnfreezeAsynchronous";
    
    /**
     * 债权匹配锁定时间
     */
    int TIME_CREDITOR_LOCK_MATCH = 2;//分钟
    
    /**
     * 债权购买锁定时间
     */
    int TIME_CREDITOR_LOCK_PAY = 20;//分钟
    
    /**
     * WEBURL
     */
    String WEBURL = WEB_URL+"WEB-INF/operating.jsp";
    
    /**
     * PROJECT_NAME
     */
    String PROJECT_NAME = "前海红筹";
    /**
     * SYSTEM_EXCEPTION_RECEIVE
     */
    String[] SYSTEM_EXCEPTION_RECEIVE = {"chengyc@hc9.com"};
    
    /**
     * 后台环讯回调地址
     */
    /**后台债权人注册*/
    String REGISTRATIONBACKSTAGE = WEB_URL+"processing/ipsCallback.htm";
    /**后台债权人充值*/
    String RECHARGEURLBACKSTAGE = WEB_URL+"processing/rechargeProessing.htm";
    /**后台债权人提现*/
    String WITHDRAWALBACKSTAGE = WEB_URL+"processing/withdrlwalProessing.htm";
    /**
     * 用来判断是否通过安全验证
     */
    String SECURITY_VERIFIY = "security_verifiy";
    /**
     * 前台充值记录中的列表分页大小
     */
    public static final int PAGE_SIZE_RECHARGE_RECORD = 10;
    
    String NEW_BID="新标上线";
    String END_BID="标的结束";
    
    /**成功*/
    int RESULT_SUCCESSFUL = 0;
    /**失败*/
    int RESULT_FAIL = 1;
    
    /**每年按360天计算*/
    int YEAR=360;
    /**每月按30天计算*/
    int MONTH=30;
}
