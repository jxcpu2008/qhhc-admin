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
 * 店铺抽奖结果
 */
@Entity
@Table(name="shop_lottery")

public class ShopLottery  implements java.io.Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields    
	
     private Long id;
     /**中奖人*/
     private Userbasicsinfo userbasicsinfo;
     
     /**抽奖的店铺*/
     private Shop shop;
     
     /**上证指数*/
     private Double number1;
     
     /**深圳成指*/
     private Double number2;
     
     /**本次参与抽奖的人数*/
     private Integer quantity;
     
     /**中奖号码*/
     private String winNum;
     
     /**生成时间*/
     private String genTime;
     
     /**开奖与否:0未开奖,1开奖*/
     private Integer isLaunched;

    // Constructors

    /** default constructor */
    public ShopLottery() {
    }

    
    /** full constructor */
    public ShopLottery(Userbasicsinfo userbasicsinfo, Shop shop, Double number1, Double number2, Integer quantity, String winNum) {
        this.userbasicsinfo = userbasicsinfo;
        this.shop = shop;
        this.number1 = number1;
        this.number2 = number2;
        this.quantity = quantity;
        this.winNum = winNum;
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
    
    @Column(name="number1", precision=22, scale=0)

    public Double getNumber1() {
        return this.number1;
    }
    
    public void setNumber1(Double number1) {
        this.number1 = number1;
    }
    
    @Column(name="number2", precision=22, scale=0)

    public Double getNumber2() {
        return this.number2;
    }
    
    public void setNumber2(Double number2) {
        this.number2 = number2;
    }
    
    @Column(name="quantity")

    public Integer getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @Column(name="win_num")

    public String getWinNum() {
        return this.winNum;
    }
    
    public void setWinNum(String winNum) {
        this.winNum = winNum;
    }

    
    @Column(name="gen_time")
	public String getGenTime() {
		return genTime;
	}


	public void setGenTime(String genTime) {
		this.genTime = genTime;
	}

	@Column(name="is_launched")
	public Integer getIsLaunched() {
		return isLaunched;
	}


	public void setIsLaunched(Integer isLaunched) {
		this.isLaunched = isLaunched;
	}
   








}