package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Menurole
 */
/**
 * <p>
 * Title:Menurole
 * </p>
 * <p>
 * Description: 角色菜单关联信息表
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>

 */
@Entity
@Table(name = "menurole")
public class Menurole implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键 */
    private Long id;
    /** 菜单 */
    private Menu menu;
    /** 角色 */
    private Role role;
    /** 创建时间 */
    private String createTime;

    // Constructors

    /** default constructor */
    public Menurole() {
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param menu
     *            菜单
     * @param role
     *            角色
     * @param createTime
     *            添加时间
     */
    public Menurole(Menu menu, Role role, String createTime) {
        this.menu = menu;
        this.role = role;
        this.createTime = createTime;
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
     * Title: getMenu
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return menu
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    public Menu getMenu() {
        return this.menu;
    }

    /**
     * <p>
     * Title: setMenu
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param menu
     *            menu
     */
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    /**
     * <p>
     * Title: getRole
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return role
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    public Role getRole() {
        return this.role;
    }

    /**
     * <p>
     * Title: setRole
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param role
     *            role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * <p>
     * Title: getCreateTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return createTime
     */
    @Column(name = "createTime", length = 50)
    public String getCreateTime() {
        return this.createTime;
    }

    /**
     * <p>
     * Title: setCreateTime
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param createTime
     *            createTime
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

}