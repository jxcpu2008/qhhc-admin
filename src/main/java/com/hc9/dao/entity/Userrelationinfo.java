package com.hc9.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * 用户关联信息
 */
@Entity
@Table(name = "userrelationinfo")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "userbasicsinfo" })
public class Userrelationinfo implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 会员基础信息
     */
    private Userbasicsinfo userbasicsinfo;

    /**
     * 客服
     */
    private Adminuser adminuser;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 手机验证
     */
    private Integer phonePass;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 身份证号码
     */
    private String cardId;

    /**
     * 个人头像路径
     */
    private String imgUrl;

    /**
     * 是否通过验证
     */
    private Integer emailisPass;

    /**
     * 身份证图片
     */
    private String cardImg;

    /**
     * QQ号码
     */
    private String qqNum;

    /**
     * 性别
     */
    private String sex;

    /**
     * 出身日期
     */
    private String birthDay;

    /**
     * 最高学历
     */
    private String qualifications;

    /**
     * 毕业院校
     */
    private String institutions;

    /**
     * 婚姻状况
     */
    public String marriage;

    /**
     * 居住地址
     */
    public String newaddress;

    /**
     * 公司行业
     */
    private String industry;

    /**
     * 公司规模
     */
    private String scale;

    /**
     * 职位
     */
    private String post;

    /**
     * 月收入
     */
    private String income;
    /**
     * 推荐人
     */
    private String recommend;
    
    
    /**
     * 身份证正面
     */
    private String cardone;
    /**
     * 身份证反面
     */
    private String cardtwo;
    /**
     * 机构附件1
     */
    private String mechofattachment1;
    /**
     * 机构附件2
     */
    private String mechofattachment2;
    /**
     * 机构附件3
     */
    private String mechofattachment3;
    /**
     * 机构附件4
     */
    private String mechofattachment4;
    /**
     * 机构投资申请：0.默认 1.提交申请(未审核) 2.申请通过 3.申请失败
     */
    private Integer audit;
    /**
     * 机构投资申请时间
     */
    private String certSubtime;
    /**
     * 管理员审核机构投资申请时间
     */
    private String manacerttime;
    
    
    
    private Adminuser adminuser_idauch;
    
    
    /** default constructor */
    public Userrelationinfo() {
    }

    /**
     * 
     * @param userbasicsinfo
     *            会员基本信息
     * @param adminuser
     *            客服
     * @param companyName
     *            公司名称
     * @param business
     *            主要业务
     * @param phone
     *            手机号码
     * @param email
     *            邮箱
     * @param cardId
     *            身份证号码
     * @param imgUrl
     *            头像路径
     * @param emailisPass
     *            邮箱是否通过验证
     * @param cardImg
     *            身份证附件
     * @param qqNum
     *            QQ号码
     * @param sex
     *            性别
     * @param birthDay
     *            出身日期
     * @param institutions
     *            毕业院校
     * @param industry
     *            公司行业
     * @param scale
     *            公司规模
     * @param post
     *            职位
     */
    public Userrelationinfo(Long id, Userbasicsinfo userbasicsinfo,
            Adminuser adminuser, String companyName, Integer phonePass,
            String phone, String email, String cardId, String imgUrl,
            Integer emailisPass, String cardImg, String qqNum, String sex,
            String birthDay, String qualifications, String institutions,
            String marriage, String newaddress, String industry, String scale,
            String post, String income,String cardone,String cardtwo,String mechofattachment1,
            String mechofattachment2,String mechofattachment3,String mechofattachment4,
            String certSubtime,String manacerttime,Integer audit,Adminuser adminuser_idauch) {
        super();
        this.id = id;
        this.userbasicsinfo = userbasicsinfo;
        this.adminuser = adminuser;
        this.companyName = companyName;
        this.phonePass = phonePass;
        this.phone = phone;
        this.email = email;
        this.cardId = cardId;
        this.imgUrl = imgUrl;
        this.emailisPass = emailisPass;
        this.cardImg = cardImg;
        this.qqNum = qqNum;
        this.sex = sex;
        this.birthDay = birthDay;
        this.qualifications = qualifications;
        this.institutions = institutions;
        this.marriage = marriage;
        this.newaddress = newaddress;
        this.industry = industry;
        this.scale = scale;
        this.post = post;
        this.income = income;
        this.cardone=cardone;
        this.cardtwo=cardtwo;
        this.mechofattachment1=mechofattachment1;
        this.mechofattachment2=mechofattachment2;
        this.mechofattachment3=mechofattachment3;
        this.mechofattachment4=mechofattachment4;
        this.certSubtime=certSubtime;
        this.manacerttime=manacerttime;
        this.audit=audit;
        this.adminuser_idauch=adminuser_idauch;
    }

    /**
     * 主键
     * 
     * @return 主键
     */
    @Id
    @GenericGenerator(name = "user_id", strategy = "foreign", parameters = { @Parameter(name = "property", value = "userbasicsinfo") })
    @GeneratedValue(generator = "user_id")
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
     * 会员基础信息
     * 
     * @return 会员基础信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    public Userbasicsinfo getUserbasicsinfo() {
        return this.userbasicsinfo;
    }

    /**
     * 会员基础信息
     * 
     * @param userbasicsinfo
     *            会员基础信息
     */
    public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }

    /**
     * 客服
     * 
     * @return 客服
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminuser_id")
    public Adminuser getAdminuser() {
        return this.adminuser;
    }

    /**
     * 客服
     * 
     * @param adminuser
     *            客服
     */
    public void setAdminuser(Adminuser adminuser) {
        this.adminuser = adminuser;
    }

    /**
     * 公司名称
     * 
     * @return 公司名称
     */
    @Column(name = "companyName")
    public String getCompanyName() {
        return this.companyName;
    }

    /**
     * 公司名称
     * 
     * @param companyName
     *            公司名称
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * 手机验证
     * 
     * @return 主要业务
     */
    @Column(name = "phonepass")
    public Integer getPhonePass() {
		return phonePass;
	}

	public void setPhonePass(Integer phonePass) {
		this.phonePass = phonePass;
	}

    /**
     * 电话号码
     * 
     * @return 电话号码
     */
    @Column(name = "phone", length = 20)
    public String getPhone() {
        return this.phone;
    }



	/**
     * 电话号码
     * 
     * @param phone
     *            电话号码
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 邮箱
     * 
     * @return 邮箱
     */
    @Column(name = "email", length = 50)
    public String getEmail() {
        return this.email;
    }

    /**
     * 邮箱
     * 
     * @param email
     *            邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 身份证号码
     * 
     * @return 身份证号码
     */
    @Column(name = "cardId", length = 50)
    public String getCardId() {
        return this.cardId;
    }

    /**
     * 身份证号码
     * 
     * @param cardId
     *            身份证号码
     */
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    /**
     * 头像路径
     * 
     * @return 头像路径
     */
    @Column(name = "imgUrl")
    public String getImgUrl() {
        return this.imgUrl;
    }

    /**
     * 头像路径
     * 
     * @param imgUrl
     *            头像路径
     */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    /**
     * 邮箱是否通过验证
     * 
     * @return 是否通过验证
     */
    @Column(name = "emailisPass")
    public Integer getEmailisPass() {
        return this.emailisPass;
    }

    /**
     * 邮箱是否通过验证
     * 
     * @param emailisPass
     *            是否通过验证
     */
    public void setEmailisPass(Integer emailisPass) {
        this.emailisPass = emailisPass;
    }

    /**
     * 身份证附件
     * 
     * @return 身份证附件
     */
    @Column(name = "cardImg")
    public String getCardImg() {
        return this.cardImg;
    }

    /**
     * 身份证附件
     * 
     * @param cardImg
     *            身份证附件
     */
    public void setCardImg(String cardImg) {
        this.cardImg = cardImg;
    }

    /**
     * QQ号码
     * 
     * @return QQ号码
     */
    @Column(name = "qqNum")
    public String getQqNum() {
        return this.qqNum;
    }

    /**
     * QQ号码
     * 
     * @param qqNum
     *            QQ号码
     */
    public void setQqNum(String qqNum) {
        this.qqNum = qqNum;
    }

    /**
     * 性别
     * 
     * @return 性别
     */
    @Column(name = "sex", length = 2)
    public String getSex() {
        return sex;
    }

    /**
     * 性别
     * 
     * @param sex
     *            性别
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 出身日期
     * 
     * @return 出身日期
     */
    @Column(name = "birth_day", length = 15)
    public String getBirthDay() {
        return birthDay;
    }

    /**
     * 出身日期
     * 
     * @param birthDay
     *            出身日期
     */
    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    /**
     * 毕业院校
     * 
     * @return 毕业院校
     */
    @Column(name = "institutions", length = 30)
    public String getInstitutions() {
        return institutions;
    }

    /**
     * 毕业院校
     * 
     * @param institutions
     *            毕业院校
     */
    public void setInstitutions(String institutions) {
        this.institutions = institutions;
    }

    /**
     * 公司行业
     * 
     * @return 公司行业
     */
    @Column(name = "industry", length = 30)
    public String getIndustry() {
        return industry;
    }

    /**
     * 公司行业
     * 
     * @param industry
     *            公司行业
     */
    public void setIndustry(String industry) {
        this.industry = industry;
    }

    /**
     * 公司规模
     * 
     * @return 公司规模
     */
    @Column(name = "scale", length = 30)
    public String getScale() {
        return scale;
    }

    /**
     * 公司规模
     * 
     * @param scale
     *            公司规模
     */
    public void setScale(String scale) {
        this.scale = scale;
    }

    /**
     * 职位
     * 
     * @return 职位
     */
    @Column(name = "post", length = 20)
    public String getPost() {
        return post;
    }

    /**
     * 职位
     * 
     * @param post
     *            职位
     */
    public void setPost(String post) {
        this.post = post;
    }

    /**
     * 最高学历
     * 
     * @return 最高学历
     */
    @Column(name = "qualifications", length = 20)
    public String getQualifications() {
        return qualifications;
    }

    /**
     * 最高学历
     * 
     * @param qualifications
     *            最高学历
     */
    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    /**
     * 婚姻状况
     * 
     * @return 婚姻状况
     */
    @Column(name = "marriage", length = 10)
    public String getMarriage() {
        return marriage;
    }

    /**
     * 婚姻状况
     * 
     * @param marriage
     *            婚姻状况
     */
    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    /**
     * 居住地址
     * 
     * @return 居住地址
     */
    @Column(name = "newaddress", length = 30)
    public String getNewaddress() {
        return newaddress;
    }

    /**
     * 居住地址
     * 
     * @param newaddress
     *            居住地址
     */
    public void setNewaddress(String newaddress) {
        this.newaddress = newaddress;
    }

    /**
     * 月收入
     * 
     * @return 月收入
     */
    @Column(name = "ncome", length = 20)
    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

	public String getRecommend() {
		return recommend;
	}
	 @Column(name = "recommend")
	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}
	 
	 
	@Column(name="cardone") 
	public String getCardone() {
		return cardone;
	}

	public void setCardone(String cardone) {
		this.cardone = cardone;
	}
	
	@Column(name="cardtwo")
	public String getCardtwo() {
		return cardtwo;
	}

	public void setCardtwo(String cardtwo) {
		this.cardtwo = cardtwo;
	}
	
	
	@Column(name="mechofattachment1")
	public String getMechofattachment1() {
		return mechofattachment1;
	}

	public void setMechofattachment1(String mechofattachment1) {
		this.mechofattachment1 = mechofattachment1;
	}
	
	@Column(name="mechofattachment2")
	public String getMechofattachment2() {
		return mechofattachment2;
	}

	public void setMechofattachment2(String mechofattachment2) {
		this.mechofattachment2 = mechofattachment2;
	}
	
	@Column(name="mechofattachment3")
	public String getMechofattachment3() {
		return mechofattachment3;
	}

	public void setMechofattachment3(String mechofattachment3) {
		this.mechofattachment3 = mechofattachment3;
	}
	
	@Column(name="mechofattachment4")
	public String getMechofattachment4() {
		return mechofattachment4;
	}

	public void setMechofattachment4(String mechofattachment4) {
		this.mechofattachment4 = mechofattachment4;
	}
	
	
	
	@Column(name="certSubtime")
	public String getCertSubtime() {
		return certSubtime;
	}

	public void setCertSubtime(String certSubtime) {
		this.certSubtime = certSubtime;
	}
	
	@Column(name="manacerttime")
	public String getManacerttime() {
		return manacerttime;
	}

	public void setManacerttime(String manacerttime) {
		this.manacerttime = manacerttime;
	}

	@Column(name="audit")
	public Integer getAudit() {
		return audit;
	}

	public void setAudit(Integer audit) {
		this.audit = audit;
	}
	
	
	@ManyToOne()
	@JoinColumn(name = "adminuser_idauch")
	public Adminuser getAdminuser_idauch() {
		return adminuser_idauch;
	}

	public void setAdminuser_idauch(Adminuser adminuser_idauch) {
		this.adminuser_idauch = adminuser_idauch;
	}

	
	 

}