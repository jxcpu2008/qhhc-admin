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
 * 站内消息
 */
@Entity
@Table(name = "usermessage")
public class Usermessage implements java.io.Serializable {

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
     * 消息类容
     */
    private String context;

    /**
     * 是否已读（0未读、1已读）
     */
    private Integer isread;
    
    // 是否是推送消息（0-不是，1-是）
    private Integer isPush;

    /**
     * 消息接收时间
     */
    private String receivetime;
    
    private String expireTime;
    
    private String money;

    private Long userId;
    
    /**
     * 消息标题
     */
    private String title;
    
    private Userbasicsinfo sendUser;

    /** default constructor */
    public Usermessage() {
    }

    /**
     * 构造方法
     * 
     * @param userbasicsinfo
     *            会员基本信息
     * @param context
     *            消息类容
     * @param isread
     *            是否已读
     * @param receivetime
     *            接受时间
     * @param title
     *            标题
     */
    public Usermessage(Long id, Userbasicsinfo userbasicsinfo, String context,
			Integer isread, Integer isPush, String receivetime, String title,
			Userbasicsinfo sendUser) {
		this.id = id;
		this.userbasicsinfo = userbasicsinfo;
		this.context = context;
		this.isread = isread;
		this.isPush = isPush;
		this.receivetime = receivetime;
		this.title = title;
		this.sendUser = sendUser;
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
     * 消息类容
     * 
     * @return 消息类容
     */
    @Column(name = "context", length = 300)
    public String getContext() {
        return this.context;
    }

    /**
     * 消息类容
     * 
     * @param context
     *            消息类容
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * 是否已读
     * 
     * @return 是否已读
     */
    @Column(name = "isread")
    public Integer getIsread() {
        return this.isread;
    }

    /**
     * 是否已读
     * 
     * @param isread
     *            是否已读
     */
    public void setIsread(Integer isread) {
        this.isread = isread;
    }

    @Column(name = "isPush")
    public Integer getIsPush() {
		return isPush;
	}

	public void setIsPush(Integer isPush) {
		this.isPush = isPush;
	}

	/**
     * 接受时间
     * 
     * @return 接受时间
     */
    @Column(name = "receivetime", length = 30)
    public String getReceivetime() {
        return this.receivetime;
    }

    /**
     * 接受时间
     * 
     * @param receivetime
     *            接受时间
     */
    public void setReceivetime(String receivetime) {
        this.receivetime = receivetime;
    }

    /**
     * 标题
     * 
     * @return 标题
     */
    @Column(name = "title", length = 256)
    public String getTitle() {
        return this.title;
    }

    /**
     * 标题
     * 
     * @param title
     *            标题
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_id")
	public Userbasicsinfo getSendUser() {
		return sendUser;
	}

	public void setSendUser(Userbasicsinfo sendUser) {
		this.sendUser = sendUser;
	}

	@Column(name = "expireTime", length = 10)
	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}

	@Column(name = "money", length = 10)
	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	@Column(name = "user_id", length = 20, insertable=false, updatable=false)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}