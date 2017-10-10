package com.hc9.model;

/**
 * 彭元征信请求实体
 * @author Administrator
 *
 */
public class CardInfo {
	
	/**被查询者姓名*/
	private String name;
	
	/**被查询者证件号码*/
	private String documentNo;
	
	/**查询的收费子报告类型ID，多个ID之间用逗号分隔。无论查询个人信息还是企业信息，都不能为空。*/
	private String subreportIDs;
	
	/**引用ID，查询者提交的用于识别本次查询的流水号，系统返回结果中会原样包含该流水号。可以为空。*/
	private String refID;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public String getSubreportIDs() {
		return subreportIDs;
	}

	public void setSubreportIDs(String subreportIDs) {
		this.subreportIDs = subreportIDs;
	}

	public String getRefID() {
		return refID;
	}

	public void setRefID(String refID) {
		this.refID = refID;
	}
	
	
	

}
