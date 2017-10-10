package com.hc9.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Verifyproblem
 */
@Entity
@Table(name = "verifyproblem")
public class Verifyproblem implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

    /**
     * 主键id
     */
    private Long id;
    /**
     * 问题内容
     */
    private String problem;
    
    /**
     * 用户安全问题
     */
    private Set<Securityproblem> securityproblems = new HashSet<Securityproblem>(
            0);

    // Constructors

    /** default constructor */
    public Verifyproblem() {
    }

    /** full constructor */
    /**
     *  
     * @param problem 问题内容
     * @param securityproblems 用户安全问题
     */
    public Verifyproblem(String problem, Set<Securityproblem> securityproblems) {
        this.problem = problem;
        this.securityproblems = securityproblems;
    }

    /**
     * 构造方法
     * 
     * @param id
     *            编号
     */
    public Verifyproblem(Long id) {
        this.id = id;
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
     * @return String
     */
    @Column(name = "problem", length = 256)
    public String getProblem() {
        return this.problem;
    }

    /**
     * 
     * @param problem 问题内容
     */
    public void setProblem(String problem) {
        this.problem = problem;
    }

    /**
     * 
     * @return Set<Securityproblem>
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "verifyproblem")
    public Set<Securityproblem> getSecurityproblems() {
        return this.securityproblems;
    }

    /**
     * 
     * @param securityproblems 用户安全问题
     */
    public void setSecurityproblems(Set<Securityproblem> securityproblems) {
        this.securityproblems = securityproblems;
    }

}