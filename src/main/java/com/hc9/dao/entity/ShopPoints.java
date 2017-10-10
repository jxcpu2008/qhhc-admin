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
 * ShopPoints entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="shop_points")

public class ShopPoints  implements java.io.Serializable {


    // Fields    

     private Long id;
     /**
      * 评分人
      */
     private Userbasicsinfo userbasicsinfo;
     /**
      * 被评分的店铺
      */
     private Shop shop;
     /**
      * 分数
      */
     private Integer points;
     /**
      * 评分时间
      */
     private String genTime;

    // Constructors

    /** default constructor */
    public ShopPoints() {
    }

    
    /** full constructor */
    public ShopPoints(Userbasicsinfo userbasicsinfo, Shop shop, Integer points) {
        this.userbasicsinfo = userbasicsinfo;
        this.shop = shop;
        this.points = points;
    }

   
    // Property accessors
    @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="id", unique=true, nullable=false)

    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="user_id")

    public Userbasicsinfo getUserbasicsinfo() {
        return this.userbasicsinfo;
    }
    
    public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="shop_id")

    public Shop getShop() {
        return this.shop;
    }
    
    public void setShop(Shop shop) {
        this.shop = shop;
    }
    
    @Column(name="points")

    public Integer getPoints() {
        return this.points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }

    @Column(name="gen_time")
	public String getGenTime() {
		return genTime;
	}


	public void setGenTime(String genTime) {
		this.genTime = genTime;
	}
   








}