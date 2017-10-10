package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *Banner banner图片表
 */
@Entity
@Table(name = "banner")
public class Banner implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    /** 主键 */
    private Long id;
    /** 排序号 */
    private Integer number;
    /** 图片名称 */
    private String picturename;
    /** 链接地址 */
    private String url;
    /** 图片路径 */
    private String imgurl;
    /** 图片位置：1首页、2我要投资、3我要借款 */
    private Integer type;
    
    private String etag;
    // Constructors

    /** default constructor */
    public Banner() {
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param number
     *            排序号
     * @param picturename
     *            图片名字
     * @param url
     *            链接地址
     * @param imgurl
     *            图片路径
     * @param type
     *            图片位置
     */
    public Banner(Integer number, String picturename, String url,
            String imgurl, Integer type) {
        this.number = number;
        this.picturename = picturename;
        this.url = url;
        this.imgurl = imgurl;
        this.type = type;
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
     * Title: getNumber
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return number
     */
    @Column(name = "number")
    public Integer getNumber() {
        return this.number;
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
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * <p>
     * Title: getPicturename
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return picturename
     */
    @Column(name = "picturename", length = 30)
    public String getPicturename() {
        return this.picturename;
    }

    /**
     * <p>
     * Title: setPicturename
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param picturename
     *            picturename
     */
    public void setPicturename(String picturename) {
        this.picturename = picturename;
    }

    /**
     * <p>
     * Title: getUrl
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return url
     */
    @Column(name = "url", length = 50)
    public String getUrl() {
        return this.url;
    }

    /**
     * <p>
     * Title: setUrl
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param url
     *            url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * <p>
     * Title: getImgurl
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return imgurl
     */
    @Column(name = "imgurl", length = 100)
    public String getImgurl() {
        return this.imgurl;
    }

    /**
     * <p>
     * Title: setImgurl
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param imgurl
     *            imgurl
     */
    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    /**
     * <p>
     * Title: getType
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return type
     */
    @Column(name = "type", length = 2)
    public Integer getType() {
        return type;
    }

    /**
     * <p>
     * Title: setType
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param type
     *            type
     */
    public void setType(Integer type) {
        this.type = type;
    }

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

}