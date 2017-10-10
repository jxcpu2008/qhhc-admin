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
 * 约谈
 */
@Entity
@Table(name = "shop_interview")
public class ShopInterview implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 约谈接收方
     */
    private Long toUser;
    
    /**
     * 约谈发起方
     */
    private Long fromUser;

    /**
     * 店铺
     */
    private Shop shop;
    
    /**
     * 约谈结果
     */
    private Integer success;


    /**
     * 约谈时间 
     */
    private String createTime;
    
    /**
     * 已读：0未读，1已读
     */
    private Integer isread;
    
    /**
     * 约谈内容
     */
    private String context;

    /**
     * 主键
     * 
     * @return 主键
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    


	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	@Column(name="success")
	public Integer getSuccess() {
		return success;
	}

	public void setSuccess(Integer success) {
		this.success = success;
	}

	@Column(name="createTime")
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
	@Column(name="isread")
	public Integer getIsread() {
		return isread;
	}

	public void setIsread(Integer isread) {
		this.isread = isread;
	}
	@Column(name="context")
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
	@Column(name="to_user")
	public Long getToUser() {
		return toUser;
	}

	public void setToUser(Long toUser) {
		this.toUser = toUser;
	}
	@Column(name="from_user")
	public Long getFromUser() {
		return fromUser;
	}

	public void setFromUser(Long fromUser) {
		this.fromUser = fromUser;
	}
    
    

    

    
}