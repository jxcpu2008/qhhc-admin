package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ExceptionNoteInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "exception_note_info")
public class ExceptionNoteInfo implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * id
     */
    private Long id;
    /**
     * 当前时间
     */
    private String curTime;
    /**
     * 登录账户
     */
    private String userLoginName;
    /**
     * 真实姓名
     */
    private String userRealName;
    /**
     * 环讯账户
     */
    private String userIps;
    /**
     * 用户当前余额
     */
    private String userCurBalance;
    /**
     * 用户目标余额
     */
    private String userAimBalance;
    /**
     * 流动资金
     */
    private String moneyExchange;
    /**
     * 错误类型
     */
    private String errorType;
    /**
     * 备注
     */
    private String remark;

    /**
     * 状态[0：未处理；1：已处理]
     */
    private Integer status = 0;

    // Constructors

    /** default constructor */
    public ExceptionNoteInfo() {
    }

    /**
     * minimal constructor
     * @param curTime   当前时间
     * @param status    状态
     */
    public ExceptionNoteInfo(String curTime, Integer status) {
        this.curTime = curTime;
        this.status = status;
    }

    /**
     * 
     * @param id    id
     * @param curTime   curTime
     * @param userLoginName userLoginName
     * @param userRealName  userRealName
     * @param userIps   userIps
     * @param userCurBalance    userCurBalance
     * @param userAimBalance    userAimBalance
     * @param moneyExchange moneyExchange
     * @param errorType errorType
     * @param remark    remark
     * @param status    status
     */
    public ExceptionNoteInfo(Long id, String curTime, String userLoginName,
            String userRealName, String userIps, String userCurBalance,
            String userAimBalance, String moneyExchange, String errorType,
            String remark, Integer status) {
        super();
        this.id = id;
        this.curTime = curTime;
        this.userLoginName = userLoginName;
        this.userRealName = userRealName;
        this.userIps = userIps;
        this.userCurBalance = userCurBalance;
        this.userAimBalance = userAimBalance;
        this.moneyExchange = moneyExchange;
        this.errorType = errorType;
        this.remark = remark;
        this.status = status;
    }

    /**
     * id
     * @return  id
     */
    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * id
     * @param id    id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * curTime
     * @return  curTime
     */
    @Column(name = "cur_time", nullable = false, length = 20)
    public String getCurTime() {
        return this.curTime;
    }

    /**
     * curTime
     * @param curTime   curTime
     */
    public void setCurTime(String curTime) {
        this.curTime = curTime;
    }

    /**
     * userLoginName
     * @return  userLoginName
     */
    @Column(name = "user_login_name", length = 20)
    public String getUserLoginName() {
        return this.userLoginName;
    }

    /**
     * userLoginName
     * @param userLoginName userLoginName
     */
    public void setUserLoginName(String userLoginName) {
        this.userLoginName = userLoginName;
    }

    /**
     * userRealName
     * @return  userRealName
     */
    @Column(name = "user_real_name", length = 20)
    public String getUserRealName() {
        return this.userRealName;
    }

    /**
     * userRealName
     * @param userRealName  userRealName
     */
    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    /**
     * userIps
     * @return  userIps
     */
    @Column(name = "user_ips", length = 32)
    public String getUserIps() {
        return this.userIps;
    }

    /**
     * userIps
     * @param userIps   userIps
     */
    public void setUserIps(String userIps) {
        this.userIps = userIps;
    }

    /**
     * userCurBalance
     * @return  userCurBalance
     */
    @Column(name = "user_cur_balance")
    public String getUserCurBalance() {
        return this.userCurBalance;
    }

    /**
     * userCurBalance
     * @param userCurBalance    userCurBalance
     */
    public void setUserCurBalance(String userCurBalance) {
        this.userCurBalance = userCurBalance;
    }

    /**
     * userAimBalance
     * @return  userAimBalance
     */
    @Column(name = "user_aim_balance")
    public String getUserAimBalance() {
        return userAimBalance;
    }

    /**
     * userAimBalance
     * @param userAimBalance    userAimBalance
     */
    public void setUserAimBalance(String userAimBalance) {
        this.userAimBalance = userAimBalance;
    }

    /**
     * moneyExchange
     * @return  moneyExchange
     */
    @Column(name = "money_exchange")
    public String getMoneyExchange() {
        return this.moneyExchange;
    }

    /**
     * moneyExchange
     * @param moneyExchange moneyExchange
     */
    public void setMoneyExchange(String moneyExchange) {
        this.moneyExchange = moneyExchange;
    }

    /**
     * errorType
     * @return  errorType
     */
    @Column(name = "error_type", length = 20)
    public String getErrorType() {
        return this.errorType;
    }

    /**
     * errorType
     * @param errorType errorType
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    /**
     * remark
     * @return  remark
     */
    @Column(name = "remark", length = 65535)
    public String getRemark() {
        return this.remark;
    }

    /**
     * remark
     * @param remark    remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * status
     * @return  status
     */
    @Column(name = "status", nullable = false)
    public Integer getStatus() {
        return this.status;
    }

    /**
     * status
     * @param status    status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ExceptionNoteInfo [id=" + id + ", curTime=" + curTime
                + ", userLoginName=" + userLoginName + ", userRealName="
                + userRealName + ", userIps=" + userIps + ", userCurBalance="
                + userCurBalance + ", userAimBalance=" + userAimBalance
                + ", moneyExchange=" + moneyExchange + ", errorType="
                + errorType + ", remark=" + remark + ", status=" + status + "]";
    }

}