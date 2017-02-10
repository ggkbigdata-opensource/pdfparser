package com.ray.pdfparser;

import java.util.List;

public class ListResult {
	private String label;
	private List<String> nums;
	private List<String> strings;
	public ListResult(String label, List<String> nums, List<String> strings){
		this.label = label;
		this.nums = nums;
		this.strings = strings;
	}
	public ListResult(String label, List<String> strings){
		this.label = label;
		this.strings = strings;
	}
	public String getLabel() {
		return label;
	}
	public List<String> getStrings() {
		return this.strings;
	}
	public List<String> getNums(){
		return this.nums;
	}
	public String toString(){
		return "{label:'"+this.label+"', num:"+this.nums+", strings: "+this.strings+"}";
	}
}
