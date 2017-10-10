package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 店铺相关文件
 */
@Entity
@Table(name = "shop_file")
public class ShopFile implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private Userbasicsinfo userbasicsinfo;

	private Shop shop;
	/**
	 * 法人代表
	 */
	private String facardimg;
	/**
	 * 营业执照
	 */
	private String yyzz;

	/**
	 * 税务登记
	 */
	private String swdj;

	/**
	 * 组织机构代码
	 */
	private String zzjg;

	/**
	 * 公司照片
	 */
	private String companyimg;
	/**
	 * 场地租赁
	 */
	private String contrantimg;
	/**
	 * 卫生许可
	 */
	private String wsxk;
	/**
	 * 视频地址
	 */
	private String videourl;

	// Constructors

	/** default constructor */
	public ShopFile() {
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userbasicinfo_id")
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	@Column(name = "facardimg", length = 128)
	public String getFacardimg() {
		return this.facardimg;
	}

	public void setFacardimg(String facardimg) {
		this.facardimg = facardimg;
	}

	@Column(name = "yyzz", length = 128)
	public String getYyzz() {
		return this.yyzz;
	}

	public void setYyzz(String yyzz) {
		this.yyzz = yyzz;
	}

	@Column(name = "swdj", length = 128)
	public String getSwdj() {
		return this.swdj;
	}

	public void setSwdj(String swdj) {
		this.swdj = swdj;
	}

	@Column(name = "zzjg", length = 128)
	public String getZzjg() {
		return this.zzjg;
	}

	public void setZzjg(String zzjg) {
		this.zzjg = zzjg;
	}

	@Column(name = "companyimg", length = 128)
	public String getCompanyimg() {
		return this.companyimg;
	}

	public void setCompanyimg(String companyimg) {
		this.companyimg = companyimg;
	}

	@Column(name = "contrantimg")
	public String getContrantimg() {
		return this.contrantimg;
	}

	public void setContrantimg(String contrantimg) {
		this.contrantimg = contrantimg;
	}

	@Column(name = "wsxk")
	public String getWsxk() {
		return this.wsxk;
	}

	public void setWsxk(String wsxk) {
		this.wsxk = wsxk;
	}

	@Column(name = "videourl", length = 500)
	public String getVideourl() {
		return this.videourl;
	}

	public void setVideourl(String videourl) {
		this.videourl = videourl;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

}