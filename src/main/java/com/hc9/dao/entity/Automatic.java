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

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 自动投标
 */
@Entity
@Table(name = "automatic")
public class Automatic implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 主键 */
	private Long id;

	/** 用户 */
	private Userbasicsinfo userbasicsinfo;

	/**
	 * 自动投标名称
	 */
	private String autoName;

	/** 状态：1：启用 2：停用 */
	private Integer state;

	/** 录入时间 */
	private String entrytime;

	/** 修改时间 */
	private String updatetime;

	/**
	 * 投资金额类型 : 1余额投资 2 普通
	 */
	private Integer autoMoneyType;

	/**
	 * 期限类型：1 限定 2不限定
	 */
	private Integer autoVildType;

	/**
	 * 投资类型 : 1 优先 2 夹层
	 */
	private Integer autoLoanType;

	/**
	 * 借款周期最小值 借款周期值，根据pTrdCycleType传入限制 D：以天计算有效期，数值范围在1-1800任选
	 * M：以月计算有效期，数据范围在1-60任选
	 */
	private Integer pSTrdCycleValue;

	/**
	 * 借款周期最大值 借款周期值，根据pTrdCycleType传入限制 D：以天计算有效期，数值范围在1-1800任选
	 * M：以月计算有效期，数据范围在1-60任选 pSTrdCycleValue不能大于pETrdCycleValue
	 */
	private Integer pETrdCycleValue;

	/**
	 * 标的借款额度最小值 设置投资者认同标的借款额度范围。 值都必须>=1.00 格式要求如：整数104.00, 带小数104.23
	 */
	private String pSAmtQuota;

	/**
	 * 标的借款额度最大值 设置投资者认同标的借款额度范围。 值都必须>=1.00 格式要求如：整数104.00, 带小数104.23
	 * pSAmtQuota不能大于pEamtQuota
	 */
	private String pEAmtQuota;

	/**
	 * 标的利率限额最小值 百分比，设置标的回报利率范围 值都必须>=1.00只需传入数值，%号不传入 如：23.12% 只传如23.12
	 */
	private String pSIRQuota;

	/**
	 * 标的利率限额最大值 百分比，设置标的回报利率范围 值都必须>=1.00只需传入数值，%号不传入 如：23.12% 只传如23.12
	 * pSIRQuota不能大于pEIRQuota
	 */
	private String pEIRQuota;

	// Constructors

	/** default constructor */
	public Automatic() {
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
	 * Title: getUserbasicsinfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return userbasicsinfo
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userbasicinfo_id")
	@JsonIgnore
	public Userbasicsinfo getUserbasicsinfo() {
		return this.userbasicsinfo;
	}

	/**
	 * <p>
	 * Title: setUserbasicsinfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param userbasicsinfo
	 *            userbasicsinfo
	 */
	public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
		this.userbasicsinfo = userbasicsinfo;
	}

	/**
	 * <p>
	 * Title: getState
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return state
	 */
	@Column(name = "state")
	public Integer getState() {
		return this.state;
	}

	/**
	 * <p>
	 * Title: setState
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param state
	 *            state
	 */
	public void setState(Integer state) {
		this.state = state;
	}

	/**
	 * <p>
	 * Title: getEntrytime
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return entrytime
	 */
	@Column(name = "entrytime", length = 30)
	public String getEntrytime() {
		return this.entrytime;
	}

	/**
	 * <p>
	 * Title: setEntrytime
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param entrytime
	 *            entrytime
	 */
	public void setEntrytime(String entrytime) {
		this.entrytime = entrytime;
	}

	/**
	 * <p>
	 * Title: getUpdatetime
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return updatetime
	 */
	@Column(name = "updatetime", length = 30)
	public String getUpdatetime() {
		return this.updatetime;
	}

	/**
	 * <p>
	 * Title: setUpdatetime
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param updatetime
	 *            updatetime
	 */
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	@Column(name = "pSTrdCycleValue", length = 5)
	public Integer getpSTrdCycleValue() {
		return pSTrdCycleValue;
	}

	public void setpSTrdCycleValue(Integer pSTrdCycleValue) {
		this.pSTrdCycleValue = pSTrdCycleValue;
	}

	@Column(name = "pETrdCycleValue", length = 5)
	public Integer getpETrdCycleValue() {
		return pETrdCycleValue;
	}

	public void setpETrdCycleValue(Integer pETrdCycleValue) {
		this.pETrdCycleValue = pETrdCycleValue;
	}

	@Column(name = "pSAmtQuota", length = 12)
	public String getpSAmtQuota() {
		return pSAmtQuota;
	}

	public void setpSAmtQuota(String pSAmtQuota) {
		this.pSAmtQuota = pSAmtQuota;
	}

	@Column(name = "pEamtQuota", length = 12)
	public String getpEAmtQuota() {
		return pEAmtQuota;
	}

	public void setpEAmtQuota(String pEAmtQuota) {
		this.pEAmtQuota = pEAmtQuota;
	}

	@Column(name = "pSIRQuota", length = 12)
	public String getpSIRQuota() {
		return pSIRQuota;
	}

	public void setpSIRQuota(String pSIRQuota) {
		this.pSIRQuota = pSIRQuota;
	}

	@Column(name = "pEIRQuota", length = 12)
	public String getpEIRQuota() {
		return pEIRQuota;
	}

	public void setpEIRQuota(String pEIRQuota) {
		this.pEIRQuota = pEIRQuota;
	}

	@Column(name = "name", length = 225)
	public String getAutoName() {
		return autoName;
	}

	public void setAutoName(String autoName) {
		this.autoName = autoName;
	}

	@Column(name = "autoMoneyType", length = 10)
	public Integer getAutoMoneyType() {
		return autoMoneyType;
	}

	public void setAutoMoneyType(Integer autoMoneyType) {
		this.autoMoneyType = autoMoneyType;
	}

	@Column(name = "autoVildType", length = 10)
	public Integer getAutoVildType() {
		return autoVildType;
	}

	public void setAutoVildType(Integer autoVildType) {
		this.autoVildType = autoVildType;
	}

	@Column(name = "autoLoanType", length = 10)
	public Integer getAutoLoanType() {
		return autoLoanType;
	}

	public void setAutoLoanType(Integer autoLoanType) {
		this.autoLoanType = autoLoanType;
	}

}