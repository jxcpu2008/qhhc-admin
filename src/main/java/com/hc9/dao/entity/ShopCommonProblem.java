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
 * 店铺-常见问题
 */
@Entity
@Table(name = "shop_common_problem")
public class ShopCommonProblem implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 2193355335895387767L;

	private Long id;

	private Shop shop;
	/**
	 * 问题
	 */
	private String question;
	/**
	 * 答案
	 */
	private String answer;

	// Constructors

	/** default constructor */
	public ShopCommonProblem() {
	}

	/** full constructor */
	public ShopCommonProblem(Shop shop, String question, String answer) {
		this.shop = shop;
		this.question = question;
		this.answer = answer;
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
	@JoinColumn(name = "shop_id")
	public Shop getShop() {
		return this.shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	@Column(name = "question")
	public String getQuestion() {
		return this.question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	@Column(name = "answer")
	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}