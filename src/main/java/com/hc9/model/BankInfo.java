package com.hc9.model;

/**
 * 银行卡信息对象
 * 
 * @author RanQiBing 2014-01-23
 * 
 */
public class BankInfo {

    /** 银行卡名称 */
    private String bankName;
    /** 银行卡别名 */
    private String bankAliases;
    /** 银行卡编号 */
    private String bankNumber;

    /**
     * 无参构造
     */
    public BankInfo() {
        // TODO Auto-generated constructor stub
    }
    /**
     * 有参构造函数
     * @param bankName   银行卡名称
     * @param bankAliases 银行卡别名
     * @param bankNumber 银行卡编号
     */
    public BankInfo(String bankName, String bankAliases, String bankNumber) {

        this.bankName = bankName;
        this.bankAliases = bankAliases;
        this.bankNumber = bankNumber;
    }
    /**
     * 
     * @return BankName
     */
    public String getBankName() {
        return bankName;
    }
    /**
     * 
     * @param bankName BankName
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    /**
     * 
     * @return BankAliases
     */
    public String getBankAliases() {
        return bankAliases;
    }
    /**
     * 
     * @param bankAliases BankAliases
     */
    public void setBankAliases(String bankAliases) {
        this.bankAliases = bankAliases;
    }
    /**
     * 
     * @return BankNumber
     */
    public String getBankNumber() {
        return bankNumber;
    }
    /**
     * 
     * @param bankNumber BankNumber
     */
    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

}
