package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 用户登录日志
 */
@Entity
@Table(name = "userloginlog")
public class Userloginlog implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 会员基本信息
     */
    private Userbasicsinfo userbasicsinfo;

    /**
     * 登录时间
     */
    private String logintime;

    /**
     * 登录ip
     */
    private String ip;

    /**
     * 登录地址
     */
    private String address;

    /** default constructor */
    public Userloginlog() {
    }

    /**
     * 构造方法
     * 
     * @param userbasicsinfo
     *            会员基本信息
     */
    public Userloginlog(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }

    /**
     * 构造方法
     * 
     * @param userbasicsinfo
     *            会员基本信息
     * @param logintime
     *            登陆了时间
     * @param ip
     *            登陆了ip
     * @param address
     *            登录地址
     */
    public Userloginlog(Userbasicsinfo userbasicsinfo, String logintime,
            String ip, String address) {
        this.userbasicsinfo = userbasicsinfo;
        this.logintime = logintime;
        this.ip = ip;
        this.address = address;
    }

    /**
     * 主键
     * 
     * @return 主键
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * 主键
     * 
     * @param id
     *            主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 会员基本信息
     * 
     * @return 会员基本信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public Userbasicsinfo getUserbasicsinfo() {
        return this.userbasicsinfo;
    }

    /**
     * 会员基本信息
     * 
     * @param userbasicsinfo
     *            会员基本信息
     */
    public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }

    /**
     * 登录时间
     * 
     * @return 登录时间
     */
    @Column(name = "logintime", length = 50)
    public String getLogintime() {
        return this.logintime;
    }

    /**
     * 登陆了时间
     * 
     * @param logintime
     *            登录时间
     */
    public void setLogintime(String logintime) {
        this.logintime = logintime;
    }

    /**
     * 登录ip
     * 
     * @return 登录ip
     */
    @Column(name = "ip", length = 50)
    public String getIp() {
        return this.ip;
    }

    /**
     * 登陆了ip
     * 
     * @param ip
     *            登录ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 登陆地址
     * 
     * @return 登录地址
     */
    @Column(name = "address", length = 50)
    public String getAddress() {
        return this.address;
    }

    /**
     * 登录地址
     * 
     * @param address
     *            登陆地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

}