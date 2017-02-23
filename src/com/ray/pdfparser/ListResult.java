package com.ray.pdfparser;

import java.util.List;

public class ListResult {
	private String reportNum;
	private List<String> nums;
	private List<String> strings;
	private String message;// 错误信息记录
	
	private String testItem;
	private String importantGrade;
	private String requirements;
	private List<String> nonstandardItems;
	
	public ListResult(String reportNum, List<String> nums, List<String> strings){
		this.reportNum = reportNum;
		this.nums = nums;
		this.strings = strings;
	}
	public ListResult(String reportNum, List<String> strings){
		this.reportNum = reportNum;
		this.strings = strings;
	}
	
	public ListResult(String reportNum,String testItem,String importantGrade,String requirements, List<String> nonstandardItems){
        this.reportNum = reportNum;
        this.testItem = testItem;
        this.importantGrade = importantGrade;
        this.requirements = requirements;
        this.nonstandardItems = nonstandardItems;
    }
	
	public String getReportNum() {
		return reportNum;
	}
	public List<String> getStrings() {
		return this.strings;
	}
	public List<String> getNums(){
		return this.nums;
	}
	public String toString(){
		return "{reportNum:'"+this.reportNum+"', testItem:"+this.testItem+"', importantGrade:"+this.importantGrade+"', requirements:"+this.requirements+"', nonstandardItems:"+this.nonstandardItems+"}";
	}
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public List<String> getNonstandardItems() {
        return nonstandardItems;
    }
    public void setNonstandardItems(List<String> nonstandardItems) {
        this.nonstandardItems = nonstandardItems;
    }
    public String getRequirements() {
        return requirements;
    }
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
    public String getImportantGrade() {
        return importantGrade;
    }
    public void setImportantGrade(String importantGrade) {
        this.importantGrade = importantGrade;
    }
    public String getTestItem() {
        return testItem;
    }
    public void setTestItem(String testItem) {
        this.testItem = testItem;
    }
}
