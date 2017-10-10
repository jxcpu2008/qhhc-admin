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
 * Messagetype
 */
@Entity
@Table(name = "messagetype")
public class Messagetype implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 主键 */
    private Long id;
    /** 类型名 */
    private String name;
    /** 备注 */
    private String explan;
    /** 消息设置 */
    private Set<Messagesetting> messagesettings = new HashSet<Messagesetting>(0);

    // Constructors

    /** default constructor */
    public Messagetype() {
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param name
     *            类型名
     * @param explan
     *            备注
     * @param messagesettings
     *            消息设置
     */
    public Messagetype(String name, String explan,
            Set<Messagesetting> messagesettings) {
        this.name = name;
        this.explan = explan;
        this.messagesettings = messagesettings;
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
    @Column(name = "name", length = 256)
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
     * Title: getExplan
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return explan
     */
    @Column(name = "explan", length = 1024)
    public String getExplan() {
        return this.explan;
    }

    /**
     * <p>
     * Title: setExplan
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param explan
     *            explan
     */
    public void setExplan(String explan) {
        this.explan = explan;
    }

    /**
     * <p>
     * Title: getMessagesettings
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return messagesettings
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "messagetype")
    public Set<Messagesetting> getMessagesettings() {
        return this.messagesettings;
    }

    /**
     * <p>
     * Title: setMessagesettings
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param messagesettings
     *            messagesettings
     */
    public void setMessagesettings(Set<Messagesetting> messagesettings) {
        this.messagesettings = messagesettings;
    }

}