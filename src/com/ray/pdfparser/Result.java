package com.ray.pdfparser;

public class Result {
	private String label;
    private String name;
	private String level;
	private String value1;
	private String value2;
	
   public Result(String label, String name, String level, String value1, String value2){
        this.label = label;
        this.level = level;
        this.value1 = value1;
        this.value2 = value2;
        this.name = name;
    }
	public Result(String label, String level, String value1, String value2){
		this.label = label;
		this.level = level;
		this.value1 = value1;
		this.value2 = value2;
	}
	public void setLabel(String label) {
	    this.label = label;
	}
	public void setLevel(String level) {
	    this.level = level;
	}
	public void setValue1(String value1) {
	    this.value1 = value1;
	}
	public void setValue2(String value2) {
	    this.value2 = value2;
	}
	public Result(String label, String value1, String value2){
		this.label = label;
		this.value1 = value1;
		this.value2 = value2;
	}
	public Result() {
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
