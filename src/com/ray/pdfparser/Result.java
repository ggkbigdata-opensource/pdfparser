package com.ray.pdfparser;

public class Result {
	private String label;
	private String level;
	private String value1;
	private String value2;
	public Result(String label, String level, String value1, String value2){
		this.label = label;
		this.level = level;
		this.value1 = value1;
		this.value2 = value2;
	}
	public Result(String label, String value1, String value2){
		this.label = label;
		this.value1 = value1;
		this.value2 = value2;
	}
	public String getLabel() {
		return label;
	}
	public String getValue1() {
		return value1;
	}
	public String getValue2() {
		return value2;
	}
	public String getLevel(){
		return level;
	}
	public String toString(){
		return "{label:'"+this.label+"', value1: '"+this.value1+"', value2: '"+this.value2+"', level: '"+this.level+"'}";
	}
}
