package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/***
 * 项目类型表
 * @author Administrator
 *
 */
@Entity
@Table(name = "loansign_type")
public class LoansignType implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * id
     */
    private Long id;

    /**项目类型key*/
    private String typeKey;
    
    /** 项目类型名称 */
    private String typeName;
    
    /***项目类型新增时间*/
    private String typeTime;
    
    /***项目类型新增Id*/
    private Long typeValue;
    
    

    /**
     * <p>
     * Title: LoansignType
     * </p>
     * <p>
     * Description:无参数构造
     * </p>
     */
    public LoansignType() {
        super();
        // TODO Auto-generated constructor stub
    }



    /**
     * <p>
     * Title: getId
     * </p>
     * <p>
     * Description: 序号
     * </p>
     * 
     * @return 序号
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    /**
     * <p>
     * Title: setId
     * </p>
     * <p>
     * Description: 序号
     * </p>
     * 
     * @param id
     *            序号
     */
    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "typeKey")
	public String getTypeKey() {
		return typeKey;
	}



	public void setTypeKey(String typeKey) {
		this.typeKey = typeKey;
	}


	 @Column(name = "typeName")
	public String getTypeName() {
		return typeName;
	}



	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}


	@Column(name = "typeTime")
	public String getTypeTime() {
		return typeTime;
	}



	public void setTypeTime(String typeTime) {
		this.typeTime = typeTime;
	}


	@Column(name = "typeValue")
	public Long getTypeValue() {
		return typeValue;
	}



	public void setTypeValue(Long typeValue) {
		this.typeValue = typeValue;
	}


}
