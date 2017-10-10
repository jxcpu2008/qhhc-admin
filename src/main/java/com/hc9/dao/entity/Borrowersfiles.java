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

/**
 * 借款人上传文件图片信息
 */
@Entity
@Table(name = "borrowersfiles")
public class Borrowersfiles implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * id
     */
    private Long id;

    /**
     * 借款人基础信息表
     */
    private Borrowersbase borrowersbase;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型 1是图片，其它为doc,xls
     */
    private String fileType;

    /**
     * 文件说明
     */
    private String fileRemark;

    /**
     * 上传时间
     */
    private String addTime;

    // Constructors

    /** default constructor */
    public Borrowersfiles() {
    }

    /**
     * minimal constructor
     * @param borrowersbase borrowersbase
     */
    public Borrowersfiles(Borrowersbase borrowersbase) {
        this.borrowersbase = borrowersbase;
    }

    /**
     * full constructor
     * @param borrowersbase borrowersbase
     * @param fileName      fileName
     * @param filePath      filePath
     * @param fileType      fileType
     * @param fileRemark    fileRemark
     * @param addTime       addTime
     */
    public Borrowersfiles(Borrowersbase borrowersbase, String fileName,
            String filePath, String fileType, String fileRemark, String addTime) {
        this.borrowersbase = borrowersbase;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileRemark = fileRemark;
        this.addTime = addTime;
    }

    /**
     * id
     * @return  id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * id
     * @param id    id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * borrowersbase
     * @return  borrowersbase
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="base_id", nullable=false)
    public Borrowersbase getBorrowersbase() {
        return this.borrowersbase;
    }

    /**
     * borrowersbase
     * @param borrowersbase borrowersbase
     */
    public void setBorrowersbase(Borrowersbase borrowersbase) {
        this.borrowersbase = borrowersbase;
    }

    /**
     * fileName
     * @return  fileName
     */
    @Column(name = "fileName", length = 200)
    public String getFileName() {
        return this.fileName;
    }

    /**
     * fileName
     * @param fileName  fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * filePath
     * @return  filePath
     */
    @Column(name = "filePath", length = 500)
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * filePath
     * @param filePath  filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * fileType
     * @return  fileType
     */
    @Column(name = "fileType", length = 500)
    public String getFileType() {
        return this.fileType;
    }

    /**
     * fileType
     * @param fileType  fileType
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * fileRemark
     * @return  fileRemark
     */
    @Column(name = "fileRemark", length = 500)
    public String getFileRemark() {
        return this.fileRemark;
    }

    /**
     * fileRemark
     * @param fileRemark    fileRemark
     */
    public void setFileRemark(String fileRemark) {
        this.fileRemark = fileRemark;
    }

    /**
     * addTime
     * @return  addTime
     */
    @Column(name = "addTime", length = 50)
    public String getAddTime() {
        return this.addTime;
    }

    /**
     * addTime
     * @param addTime   addTime
     */
    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

}