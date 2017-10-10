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
* <p>Title:MemberNumber</p>
* <p>Description: MemberNumber 会员编码表</p>
* <p>Company: 前海红筹</p>

*/
@Entity
@Table(name = "member_number")
public class MemberNumber implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 主键
     */
    private Long id;

    /**
     * 所属服务人员
     */
    private Adminuser adminuser;

    /**
     * 会员编码
     */
    private Long userId;

    /**
     * 是否使用
     */
    private Integer isuse;

    // Constructors

    /** default constructor */
    public MemberNumber() {
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param adminuser
     *            所属服务人员
     * @param number
     *            会员编码
     * @param isuse
     *            是否使用
     */
    public MemberNumber(Adminuser adminuser, Long userId, Integer isuse) {
        this.adminuser = adminuser;
        this.userId = userId;
        this.isuse = isuse;
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
     * Title: getAdminuser
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return adminuser
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminuser_id", nullable = false)
    public Adminuser getAdminuser() {
        return this.adminuser;
    }

    /**
     * <p>
     * Title: setAdminuser
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param adminuser
     *            adminuser
     */
    public void setAdminuser(Adminuser adminuser) {
        this.adminuser = adminuser;
    }

    /**
     * <p>
     * Title: getNumber
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return number
     */
    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    /**
     * <p>
     * Title: setNumber
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param number
     *            number
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * <p>
     * Title: getIsuse
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return isuse
     */
    @Column(name = "isuse")
    public Integer getIsuse() {
        return isuse;
    }

    /**
     * <p>
     * Title: setIsuse
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param isuse
     *            isuse
     */
    public void setIsuse(Integer isuse) {
        this.isuse = isuse;
    }

}