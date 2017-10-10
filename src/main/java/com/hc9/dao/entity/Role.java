package com.hc9.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Role
 */
@Entity
@Table(name = "role")
@JsonIgnoreProperties(value = {"adminusers","menuroles","userrelationinfos"})
public class Role implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 主键id
     */
    private Long id;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 备注
     */
    private String roleRemark;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 角色代码
     */
    private String roleCode;
    /**
     * 客服
     */
    private Set<Adminuser> adminusers = new HashSet<Adminuser>(0);
    /**
     * 菜单角色
     */
    private Set<Menurole> menuroles = new HashSet<Menurole>(0);

    // Constructors

    /** default constructor */
    public Role() {
    }

    /** full constructor */
    /**
     * 
     * @param roleName 角色名称
     * @param roleRemark 备注
     * @param createTime 创建时间
     * @param createUser 创建人
     * @param roleCode 角色代码
     * @param adminusers 后台用户信息
     * @param menuroles 菜单角色
     */
    public Role(String roleName, String roleRemark, String createTime,
            String createUser, String roleCode, Set<Adminuser> adminusers,
            Set<Menurole> menuroles) {
        this.roleName = roleName;
        this.roleRemark = roleRemark;
        this.createTime = createTime;
        this.createUser = createUser;
        this.roleCode = roleCode;
        this.adminusers = adminusers;
        this.menuroles = menuroles;
    }

    // Property accessors
    /**
     * 
     * @return Long
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * 
     * @param id 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "roleName", length = 100)
    public String getRoleName() {
        return this.roleName;
    }

    /**
     * 
     * @param roleName 角色名称
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    /**
     * 
     * @return String
     */
    @Column(name = "roleRemark", length = 300)
    public String getRoleRemark() {
        return this.roleRemark;
    }

    /**
     * 
     * @param roleRemark 备注
     */
    public void setRoleRemark(String roleRemark) {
        this.roleRemark = roleRemark;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "createTime", length = 50)
    public String getCreateTime() {
        return this.createTime;
    }

    /**
     * 
     * @param createTime 创建时间
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "createUser", length = 50)
    public String getCreateUser() {
        return this.createUser;
    }

    /**
     * 
     * @param createUser 创建人
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "roleCode", length = 20)
    public String getRoleCode() {
        return this.roleCode;
    }
    
    /**
     * 
     * @param roleCode 角色代码
     */
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    /**
     * 
     * @return  Set<Adminuser>
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "role")
    public Set<Adminuser> getAdminusers() {
        return this.adminusers;
    }

    /**
     * 
     * @param adminusers 后台用户信息
     */
    public void setAdminusers(Set<Adminuser> adminusers) {
        this.adminusers = adminusers;
    }

    /**
     * 
     * @return Set<Menurole>
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "role")
    public Set<Menurole> getMenuroles() {
        return this.menuroles;
    }

    /**
     * 
     * @param menuroles 菜单角色
     */
    public void setMenuroles(Set<Menurole> menuroles) {
        this.menuroles = menuroles;
    }

}