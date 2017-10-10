package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 流水账类型
 */
@Entity
@Table(name = "accounttype")
public class Accounttype implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 主键 */
    private Long id;

    /** 类型名 */
    private String name;
    /** 备注 */
    private String remark;

    /** accountinfos */
    private Set<Accountinfo> accountinfos = new HashSet<Accountinfo>(0);
    /** adminuseraccountinfos */
    private Set<Adminuseraccountinfo> adminuseraccountinfos = new HashSet<Adminuseraccountinfo>(
            0);

    // Constructors

    /** default constructor */
    public Accounttype() {
    }
    
    /**
     * 构造方法
     * 
     * @param id
     *            编号
     */
    public Accounttype(Long id) {
        this.id = id;
    }
    
    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description: 有参构造方法
     * </p>
     * 
     * @param name
     *            类型名
     * @param remark
     *            备注
     * @param accountinfos
     *            流水账信息
     * @param adminuseraccountinfos
     *            后台用户流水账信息
     */
    public Accounttype(String name, String remark,
            Set<Accountinfo> accountinfos,
            Set<Adminuseraccountinfo> adminuseraccountinfos) {
        this.name = name;
        this.remark = remark;
        this.accountinfos = accountinfos;
        this.adminuseraccountinfos = adminuseraccountinfos;
    }

    // Property accessors
    /**
     * <p>
     * Title: getId
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * <p>
     * Title: setId
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * <p>
     * Title: getName
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return name
     */
    @Column(name = "name", length = 60)
    public String getName() {
        return this.name;
    }

    /**
     * <p>
     * Title: setName
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>
     * Title: getRemark
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return remark
     */
    @Column(name = "remark", length = 1024)
    public String getRemark() {
        return this.remark;
    }

    /**
     * <p>
     * Title: setRemark
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param remark
     *            remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * <p>
     * Title: getAccountinfos
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return accountinfos
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "accounttype")
    public Set<Accountinfo> getAccountinfos() {
        return this.accountinfos;
    }

    /**
     * <p>
     * Title: setAccountinfos
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param accountinfos
     *            accountinfos
     */
    public void setAccountinfos(Set<Accountinfo> accountinfos) {
        this.accountinfos = accountinfos;
    }

    /**
     * <p>
     * Title: getAdminuseraccountinfos
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return adminuseraccountinfos
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "accounttype")
    public Set<Adminuseraccountinfo> getAdminuseraccountinfos() {
        return this.adminuseraccountinfos;
    }

    /**
     * <p>
     * Title: setAdminuseraccountinfos
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param adminuseraccountinfos
     *            adminuseraccountinfos
     */
    public void setAdminuseraccountinfos(
            Set<Adminuseraccountinfo> adminuseraccountinfos) {
        this.adminuseraccountinfos = adminuseraccountinfos;
    }

}