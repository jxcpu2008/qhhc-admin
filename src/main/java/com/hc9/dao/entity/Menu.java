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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * <p>
 * Title:Menu
 * </p>
 * <p>
 * Description: 后台管理员菜单
 * </p>
 * <p>
 * Company: 前海红筹
 * </p>

 */
@Entity
@Table(name = "menu")
public class Menu implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键 */
    private Long id;
    /** 父级菜单 */
    private Menu menu;
    /** 访问资源路径 */
    private String resourceUrl;
    /** 标题 */
    private String smenCaption;
    /** 排列顺序 */
    private Integer smenIndex;
    /** 提示 */
    private String smenHint;
    /** 图标路径 */
    private String smenIcon;
    /** 菜单级别1(一级菜单)2(二级菜单)3(功能点) */
    private Integer mlevel;

    /** menuroles */
    private Set<Menurole> menuroles = new HashSet<Menurole>(0);
    /** menus */
    private Set<Menu> menus = new HashSet<Menu>(0);

    // Constructors

    /** default constructor */
    public Menu() {
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
     *            父级菜单
     * @param resourceUrl
     *            访问资源路径
     * @param smenCaption
     *            标题
     * @param smenIndex
     *            排列顺序
     * @param smenHint
     *            提示
     * @param smenIcon
     *            图标路径
     * @param mlevel
     *            菜单级别1(一级菜单)2(二级菜单)3(功能点)
     * @param menuroles
     *            menuroles
     * @param menus
     *            menus
     */
    public Menu(Menu menu, String resourceUrl, String smenCaption,
            Integer smenIndex, String smenHint, String smenIcon,
            Integer mlevel, Set<Menurole> menuroles, Set<Menu> menus) {
        this.menu = menu;
        this.resourceUrl = resourceUrl;
        this.smenCaption = smenCaption;
        this.smenIndex = smenIndex;
        this.smenHint = smenHint;
        this.smenIcon = smenIcon;
        this.mlevel = mlevel;
        this.menuroles = menuroles;
        this.menus = menus;
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
    @JoinColumn(name = "systemMenu")
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
     * Title: getResourceUrl
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return resourceUrl
     */
    @Column(name = "resourceURL", length = 100)
    public String getResourceUrl() {
        return this.resourceUrl;
    }

    /**
     * <p>
     * Title: setResourceUrl
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param resourceUrl
     *            resourceUrl
     */
    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    /**
     * <p>
     * Title: getSmenCaption
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return smenCaption
     */
    @Column(name = "smenCaption", length = 100)
    public String getSmenCaption() {
        return this.smenCaption;
    }

    /**
     * <p>
     * Title: setSmenCaption
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param smenCaption
     *            smenCaption
     */
    public void setSmenCaption(String smenCaption) {
        this.smenCaption = smenCaption;
    }

    /**
     * <p>
     * Title: getSmenIndex
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return smenIndex
     */
    @Column(name = "smenIndex")
    public Integer getSmenIndex() {
        return this.smenIndex;
    }

    /**
     * <p>
     * Title: setSmenIndex
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param smenIndex
     *            smenIndex
     */
    public void setSmenIndex(Integer smenIndex) {
        this.smenIndex = smenIndex;
    }

    /**
     * <p>
     * Title: getSmenHint
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return smenHint
     */
    @Column(name = "smenHint", length = 200)
    public String getSmenHint() {
        return this.smenHint;
    }

    /**
     * <p>
     * Title: setSmenHint
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param smenHint
     *            smenHint
     */
    public void setSmenHint(String smenHint) {
        this.smenHint = smenHint;
    }

    /**
     * <p>
     * Title: getSmenIcon
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return smenIcon
     */
    @Column(name = "smenIcon", length = 200)
    public String getSmenIcon() {
        return this.smenIcon;
    }

    /**
     * <p>
     * Title: setSmenIcon
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param smenIcon
     *            smenIcon
     */
    public void setSmenIcon(String smenIcon) {
        this.smenIcon = smenIcon;
    }

    /**
     * <p>
     * Title: getMlevel
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return mlevel
     */
    @Column(name = "mlevel")
    public Integer getMlevel() {
        return this.mlevel;
    }

    /**
     * <p>
     * Title: setMlevel
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param mlevel
     *            mlevel
     */
    public void setMlevel(Integer mlevel) {
        this.mlevel = mlevel;
    }

    /**
     * <p>
     * Title: getMenuroles
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return menuroles
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "menu")
    public Set<Menurole> getMenuroles() {
        return this.menuroles;
    }

    /**
     * <p>
     * Title: setMenuroles
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param menuroles
     *            menuroles
     */
    public void setMenuroles(Set<Menurole> menuroles) {
        this.menuroles = menuroles;
    }

    /**
     * <p>
     * Title: getMenus
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return menus
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "menu")
    public Set<Menu> getMenus() {
        return this.menus;
    }

    /**
     * <p>
     * Title: setMenus
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param menus
     *            menus
     */
    public void setMenus(Set<Menu> menus) {
        this.menus = menus;
    }

}