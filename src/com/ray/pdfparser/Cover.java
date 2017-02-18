package com.ray.pdfparser;

public class Cover {
	private String reportNum = null;
	private String projectName = null;
	private String projectAddress = null;
	private String agentName = null;
	private String qaName = null;
	private String qaAddress = null;
	private String contactTel = null;
	private String contactFax = null;
	private String contactPostcode = null;
	private String message = null;
	public String getReportNum() {
		return reportNum;
	}
	public void setReportNum(String reportNum) {
		this.reportNum = reportNum;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectAddress() {
		return projectAddress;
	}
	public void setProjectAddress(String projectAddress) {
		this.projectAddress = projectAddress;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getQaName() {
		return qaName;
	}
	public void setQaName(String qaName) {
		this.qaName = qaName;
	}
	public String getQaAddress() {
		return qaAddress;
	}
	public void setQaAddress(String qaAddress) {
		this.qaAddress = qaAddress;
	}
	public String getContactTel() {
		return contactTel;
	}
	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}
	public String getContactFax() {
		return contactFax;
	}
	public void setContactFax(String contactFax) {
		this.contactFax = contactFax;
	}
	public String getContactPostcode() {
		return contactPostcode;
	}
	public void setContactPostcode(String contactPostcode) {
		this.contactPostcode = contactPostcode;
	}
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append("Cover{reportNum: '"+this.reportNum+"',");
		s.append("projectName: '"+this.projectName+"',");
		s.append("projectAddress: '"+this.projectAddress+"',");
		s.append("agentName: '"+this.agentName+"',");
		s.append("qaName: '"+this.qaName+"',");
		s.append("qaAddress: '"+this.qaAddress+"',");
		s.append("contactTel: '"+this.contactTel+"',");
		s.append("contactFax: '"+this.contactFax+"',");
		s.append("contactPostcode: '"+this.contactPostcode+"'}");
		return s.toString();
	}
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
