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
 * Securityproblem
 */
@Entity
@Table(name = "securityproblem")
public class Securityproblem implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 主键id
     */
    private Long id;
    /**
     * 用户基本信息
     */
    private Userbasicsinfo userbasicsinfo;
    /**
     * 问题库
     */
    private Verifyproblem verifyproblem;
    /**
     * 答案
     */
    private String answer;

    // Constructors

    /** default constructor */
    public Securityproblem() {
    }

    /** full constructor */
    /**
     * 
     * @param userbasicsinfo 用户信息
     * @param verifyproblem 问题
     * @param answer 答案
     */
    public Securityproblem(Userbasicsinfo userbasicsinfo,
            Verifyproblem verifyproblem, String answer) {
        this.userbasicsinfo = userbasicsinfo;
        this.verifyproblem = verifyproblem;
        this.answer = answer;
    }

    // Property accessors
    /**
     * 
     * @return Long
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * 
     * @param id 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * @return Userbasicsinfo
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public Userbasicsinfo getUserbasicsinfo() {
        return this.userbasicsinfo;
    }

    /**
     * 
     * @param userbasicsinfo 用户信息
     */
    public void setUserbasicsinfo(Userbasicsinfo userbasicsinfo) {
        this.userbasicsinfo = userbasicsinfo;
    }

    /**
     * 
     * @return Verifyproblem
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verifyproblem_id")
    public Verifyproblem getVerifyproblem() {
        return this.verifyproblem;
    }

    /**
     * 
     * @param verifyproblem 问题库
     */
    public void setVerifyproblem(Verifyproblem verifyproblem) {
        this.verifyproblem = verifyproblem;
    }

    /**
     * 
     * @return String
     */
    @Column(name = "answer", length = 256)
    public String getAnswer() {
        return this.answer;
    }

    /**
     * 
     * @param answer 答案
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

}