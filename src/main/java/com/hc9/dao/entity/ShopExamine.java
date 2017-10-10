package com.hc9.dao.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/***
 * 店铺申请审批表
 * @author 
 *
 */
@Entity
@Table(name = "shop_examine")
public class ShopExamine {
	
	
	/**
	 * id
	 */
	private  Long  id;
	
	/***
	 *店铺
	 */
	private Shop shop;
	
	/***
	 * 审批Id
	 */
	private Long adminApprovalId;
	
	/***
	 * 审核ID
	 */
	private  Long   adminExamineId;
	
	/***
	 * 状态：1-待审批  2-已审批  3-审批不通过  4-待审核  5-已审核  6-审核不通过
	 */
	private Integer state;
	
	/***
	 * 审批时间
	 */
	private String approvalTime;
	
	/***
	 * 审批说明
	 */
	private String approvalExplain;
	
	/***
	 * 审核时间
	 */
	private  String  examineTime;
	
	/***
	 * 审核说明
	 */
	private String examineExplain;
	
	
	public ShopExamine() {
	}
	

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	@Column(name = "state")
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Column(name = "approval_time")
	public String getApprovalTime() {
		return approvalTime;
	}

	public void setApprovalTime(String approvalTime) {
		this.approvalTime = approvalTime;
	}

	@Column(name = "approval_explain")
	public String getApprovalExplain() {
		return approvalExplain;
	}

	public void setApprovalExplain(String approvalExplain) {
		this.approvalExplain = approvalExplain;
	}
	
	@Column(name = "examine_time")
	public String getExamineTime() {
		return examineTime;
	}

	public void setExamineTime(String examineTime) {
		this.examineTime = examineTime;
	}

	@Column(name = "examine_explain")
	public String getExamineExplain() {
		return examineExplain;
	}

	public void setExamineExplain(String examineExplain) {
		this.examineExplain = examineExplain;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	public Shop getShop() {
		return shop;
	}
	
	public void setShop(Shop shop) {
		this.shop = shop;
	}

	@Column(name = "adminapproval_id")
	public Long getAdminApprovalId() {
		return adminApprovalId;
	}


	public void setAdminApprovalId(Long adminApprovalId) {
		this.adminApprovalId = adminApprovalId;
	}


    @Column(name = "adminexamine_id")
	public Long getAdminExamineId() {
		return adminExamineId;
	}


	public void setAdminExamineId(Long adminExamineId) {
		this.adminExamineId = adminExamineId;
	}
	
     

	
	

}
