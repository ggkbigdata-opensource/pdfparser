package com.ray.pdfparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFParser {

	static boolean isDebug = false;
	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();
		File pdfFile = new File("/Users/wanglei/Downloads/检测报告样本_client_import.pdf");
		PDFParser p = new PDFParser();
		PDFParserResult r = p.parse(pdfFile);
		System.out.println("Final Result:"+r);
		System.out.println("TOTAL TIME USED: "+((System.currentTimeMillis()-startTime)/1000.0)+" seconds");

	}

    public PDFParserResult parse_test(File pdfFile) throws IOException
    {
    	PDDocument pdfDocument = PDDocument.load(pdfFile);
    	PDFParserResult returnObj = new PDFParserResult();
    	PDFTextStripper stripper=new PDFTextStripper();
        stripper.setSortByPosition(false);
        stripper.setStartPage(126);
        stripper.setEndPage(126);
        String paragraph = stripper.getText(pdfDocument);
        {
        	System.out.println(paragraph);
        	System.out.println("------------------------------------------------------------");
	        List<Result> rs = processOnThirdParagraph(paragraph);
	        returnObj.setThirdPart(rs);
        }
        return returnObj;
    }
    public PDFParserResult parse(File pdfFile) throws IOException
    {
    	PDDocument pdfDocument = PDDocument.load(pdfFile);
    	PDFParserResult returnObj = new PDFParserResult();
    	int lastPage = pdfDocument.getNumberOfPages();
    	System.out.println("Last page="+lastPage);
    	PDFTextStripper stripper=new PDFTextStripper();
        stripper.setSortByPosition(false);
        stripper.setStartPage(1);
        stripper.setEndPage(lastPage);
        String allText = stripper.getText(pdfDocument);
        int sIndex = 0;
        int eIndex = 0;
        {
	        String end = "建筑消防设施检测报告";
	        eIndex = allText.indexOf(end);
	        String paragraph = allText.substring(0, eIndex);
	        Cover cover = this.processOnCover(paragraph);
	        returnObj.setCover(cover);
        	
        }
        {
	        String start = "单项评定结果";
	        String end = "检测结论说明";
	        sIndex = allText.indexOf(start)+start.length();
	        eIndex = allText.indexOf(end);
	        String paragraph = allText.substring(sIndex, eIndex);
	        List<Result> rs = processOnFirstParagraph(paragraph);
	        returnObj.setFirstPart(rs);
        }
        {
	        String start = "检测结论说明";
	        String end = "检测情况统计表";
	        sIndex = allText.indexOf(start)+start.length();
	        eIndex = allText.indexOf(end);
	        String paragraph = allText.substring(sIndex, eIndex);
	        String rs = processOnSecondParagraph(paragraph);
	        returnObj.setSecondPart(rs);
        }
        {
	        String start = "检测情况统计表";
	        String end = "消防设施检测不符合规范要求项目";
	        sIndex = allText.indexOf(start)+start.length();
	        eIndex = allText.indexOf(end);
	        String paragraph = allText.substring(sIndex, eIndex);
	        List<Result> rs = processOnThirdParagraph(paragraph);
	        returnObj.setThirdPart(rs);
        }
        {
	        String start = "消防设施检测不符合规范要求项目";
	        String end = "消防设备登记表";
	        sIndex = allText.indexOf(start)+start.length();
	        eIndex = allText.indexOf(end);
	        String paragraph = allText.substring(sIndex, eIndex);
	        List<ListResult> rs = processOnForthParagraph(paragraph);
	        returnObj.setForthPart(rs);
        }
        {
	        String start = "消防设备登记表";
	        sIndex = allText.indexOf(start)+start.length();
	        String paragraph = allText.substring(sIndex);
	        List<ListResult> rs = processOnFifthParagraph(paragraph);
	        returnObj.setFifthPart(rs);
        }
        return returnObj;
    }
    private Cover processOnCover(String paragraph){
    	Cover cover = new Cover();
    	String[] lines = paragraph.split("\n");

		Pattern projectName = Pattern.compile("^项目名称：\\s*(.*)\\s*$");
		Pattern projectAddress = Pattern.compile("^项目地址：\\s*(.*)\\s*$");
		Pattern agentName = Pattern.compile("^委托单位：\\s*(.*)\\s*$");
		Pattern qaName = Pattern.compile("^检测单位：\\s*(.*)\\s*$");
		Pattern reportNum = Pattern.compile("^粤消检\\s*(.*)\\s*$");
		Pattern qaAddress = Pattern.compile("^检测单位地址：\\s*(.*)\\s*$");
		Pattern contactTel = Pattern.compile("^电　     话：\\s*(.*)\\s*$");
		Pattern contactFax = Pattern.compile("^传　　   真：\\s*(.*)\\s*$");
		Pattern contactPostcode = Pattern.compile("^邮　　   编：\\s*(.*)\\s*$");

    	for(int i=0;i<lines.length;i++){
    		String line = lines[i];
    		if(isDebug) System.out.println("LINE=>"+line);
    		Matcher m = projectName.matcher(line);
    		if(m.find()){
    			cover.setProjectName(m.group(1));
    			continue;
    		}
    		m = projectAddress.matcher(line);
    		if(m.find()){
    			cover.setProjectAddress(m.group(1));
    			continue;
    		}
    		m = agentName.matcher(line);
    		if(m.find()){
    			cover.setAgentName(m.group(1));
    			continue;
    		}
    		m = qaName.matcher(line);
    		if(m.find()){
    			cover.setQaName(m.group(1));
    			continue;
    		}
    		m = reportNum.matcher(line);
    		if(m.find()){
    			cover.setReportNum(m.group(1));
    			continue;
    		}
    		m = qaAddress.matcher(line);
    		if(m.find()){
    			cover.setQaAddress(m.group(1));
    			continue;
    		}
    		m = contactTel.matcher(line);
    		if(m.find()){
    			cover.setContactTel(m.group(1));
    			continue;
    		}
    		m = contactFax.matcher(line);
    		if(m.find()){
    			cover.setContactFax(m.group(1));
    			continue;
    		}
    		m = contactPostcode.matcher(line);
    		if(m.find()){
    			cover.setContactPostcode(m.group(1));
    			continue;
    		}
    	}
    	return cover;
    }
    private List<ListResult> processOnFifthParagraph(String paragraph){
    	List<ListResult> rs = new ArrayList<ListResult>();
    	String[] lines = paragraph.split("\n");
    	String label = "";
    	List<String> nums = new ArrayList<String>();
    	List<String> strings = new ArrayList<String>();
		Pattern labpat = Pattern.compile("^\\d+  .*$");
		Pattern txtpat = Pattern.compile("^(\\d+) ([^ ].*)$");
		Pattern skppat = Pattern.compile("^\\s*第 \\d+ 页\\s*.*$");
    	int position = 1;//1=label line, 2=start text line, 3=continue text line.
		String num = "", text = "";

    	for(int i=0;i<lines.length;i++){
    		String line = lines[i];
    		if(isDebug) System.out.println("LINE["+position+"]=>"+line);
    		{
    			Matcher m = skppat.matcher(line);
    			if(m.find()){
    				if(isDebug) System.out.println("    跳过");
    				continue;
    			}
    		}
    		if(position==1){
    			Matcher m = labpat.matcher(line);
    			if(m.find()){
    				label = m.group(0);
    				position = 2;
    				if(isDebug) System.out.println("  编号=>"+label);
    			}
    		}else if(position==2){
    			Matcher m = txtpat.matcher(line);
    			if(m.find()){
    				num = m.group(1);
    				text = m.group(2);
    				position = 3;
    				if(isDebug) System.out.println("    S_F>>"+num+":"+text);
    			}
    		}else if(position==3){
    			Matcher m = txtpat.matcher(line);
    			if(m.find()){
    				if(!"".equals(text)){
        	    		nums.add(num);
        	    		strings.add(text);
        	    		if(isDebug) System.out.println("    END=>"+num+":"+text);
    				}
    				num = m.group(1);
    				text = m.group(2);
    				if(isDebug) System.out.println("    S_N>>"+num+":"+text);
    			}else{
        			Matcher m2 = labpat.matcher(line);
    				if(m2.find()){
        				if(!"".equals(text)){
            	    		nums.add(num);
            	    		strings.add(text);
            	    		if(isDebug) System.out.println("    END=>"+num+":"+text);
        				}
	    				position = 1;
	    				ListResult r = new ListResult(label, nums, strings);
	    				rs.add(r);
	    		    	nums = new ArrayList<String>();
	    		    	strings = new ArrayList<String>();
	    				label = m2.group(0);
	    				position = 2;
	    				if(isDebug) System.out.println("  编号=>"+label);
    				}else{
    					text += line;
    					if(isDebug) System.out.println("    MID>>"+num+":"+text);
    				}
    			}
    		}
    	}
		if(!"".equals(text)){
    		nums.add(num);
    		strings.add(text);
    		if(isDebug) System.out.println("    END=>"+num+":"+text);
			ListResult r = new ListResult(label, nums, strings);
			rs.add(r);
		}
    	return rs;
    }
    private List<ListResult> processOnForthParagraph(String paragraph){
    	List<ListResult> rs = new ArrayList<ListResult>();
    	String[] lines = paragraph.split("\n");
    	String engNum = "";
    	String label = "";
    	List<String> strings = new ArrayList<String>();
		Pattern engpat = Pattern.compile("工程编号\\s*:\\s*(\\d+)");
		Pattern labpat = Pattern.compile("^(\\d+\\.\\d+\\.\\d+).*$");
		Pattern chkpat = Pattern.compile("^\\d{10} .*$");
		Pattern skppat = Pattern.compile("^\\s*第 \\d+ 页\\s*.*$");
		String checkStr = "以下是不符合规范要求的检测点";
    	int position = 0;//0=engineering number, 1=label,2=start point, 3=repeat to retrieve data

    	for(int i=0;i<lines.length;i++){
    		String line = lines[i];
    		if(isDebug) System.out.println("LINE["+position+"]=>"+line);
    		{
    			Matcher m = skppat.matcher(line);
    			if(m.find()){
    				continue;
    			}
    		}
    		if(position==0){
    			Matcher m = engpat.matcher(line);
    			if(m.find()){
    				engNum = m.group(1);
    				position = 1;
    				if(isDebug) System.out.println("  工程编号=>"+engNum);
    			}
    		}else if(position==1){
    			Matcher m = labpat.matcher(line);
    			if(m.find()){
    				label = m.group(1);
    				position = 2;
    				if(isDebug) System.out.println("  项目编号=>"+label);
    			}
    		}else if(position==2){
    			if(line.indexOf(checkStr)>=0){
    				position = 3;
    			}
    		}else if(position==3){
    			Matcher m = chkpat.matcher(line);
    			if(m.find()){
    				String string = m.group(0);
    				strings.add(string);
    				if(isDebug) System.out.println("  ----->"+string);
    			}else{
    				ListResult r = new ListResult(label, strings);
    				rs.add(r);
    				position = 1;
    				strings = new ArrayList<String>();
    			}
    		}
    	}
    	
    	return rs;
    }
    private List<Result> processOnThirdParagraph(String paragraph){
    	int position = 1;//1=level,2=data,3=label,
    	String level = "", label = "", value1 = "", value2 = "";
		Pattern valuePatt = Pattern.compile(".*(\\d) (\\d)$");
		Pattern labelPatt = Pattern.compile("^(\\d+\\.\\d+\\.\\d+) .*$");
    	String[] lines = paragraph.split("\n");
    	if(isDebug) System.out.println("3rd PARA SIZE="+lines.length);
    	List<Result> rs = new ArrayList<Result>();
    	for(int i=0;i<lines.length;i++){
    		String line = lines[i];
        	if(isDebug) System.out.println("LINE["+i+"]="+line);
    		if(line!=null) line = line.trim();
    		if(position==1){
    			if("A".equals(line)||"B".equals(line)||"C".equals(line)){
    				level = line;
        			if(isDebug) System.out.println("    lv="+level);
    				position = 2;
    			}
    		}else if(position==2){
    			Matcher m = valuePatt.matcher(line);
    			if(m.find()){
    				value1 = m.group(1);
    				value2 = m.group(2);
        			if(isDebug) System.out.println("    v1="+value1+", v2="+value2);
    				position = 3;
    			}
    		}else if(position==3){
    			Matcher m = labelPatt.matcher(line);
    			if(m.find()){
    				if(isDebug) prtMacher(m);
    				label = m.group(1);
    				position = 1;
        			Result r = new Result(label, level, value1, value2);
        			rs.add(r);
        			if(isDebug) System.out.println("    "+r);
    			}
    		}
    	}
    	return rs;
    }
    private void prtMacher(Matcher m){
    	int max = m.groupCount();
		System.out.println("  LINE["+max+"]="+m.group(0));
    	for(int i=1;i<max;i++){
    		System.out.println("    ITEM="+m.group(i));
    	}
    }
    private String processOnSecondParagraph(String paragraph){
    	int lastIndex = paragraph.trim().lastIndexOf("\n");
    	if(lastIndex<1) return "";
    	return paragraph.substring(0, lastIndex);
    }
    private List<Result> processOnFirstParagraph(String paragraph){
    	String[] arr = paragraph.split("\n");
    	if(isDebug) System.out.println("size = "+arr.length);
    	String regex = "^\\s*(\\d+)\\s+\\S+\\s+([a-zA-Z])\\s+(\\d+)\\s+(\\d+)\\s*$";
		Pattern pattern = Pattern.compile(regex);
    	String subRegex = "^\\s*([a-zA-Z])\\s+(\\d+)\\s+(\\d+)\\s*$";
		Pattern subPattern = Pattern.compile(subRegex);
    	List<Result> rs = new ArrayList<Result>((arr.length+1)/2);
    	String preLabel = "";
    	for(String line: arr){
    		if(isDebug) System.out.print("LINE:["+line+"]=>");
    		Matcher matcher = pattern.matcher(line);
    		if(matcher.find()){
//    			int max = matcher.groupCount();
//    			for(int i=1;i<=max;i++){
//    				System.out.print(i+":"+matcher.group(i)+", ");
//    			}
    			Result r = new Result(matcher.group(1)+matcher.group(2), matcher.group(3), matcher.group(4));
    			rs.add(r);
    			preLabel = matcher.group(1);
    			if(isDebug) System.out.print(r);
    			if(isDebug) System.out.println("\n");
    		}else{
        		matcher = subPattern.matcher(line);
        		if(!matcher.find()){
        			if(isDebug) System.out.println("NOT Found...\n");
        			continue;
        		}
    			Result r = new Result(preLabel+matcher.group(1), matcher.group(2), matcher.group(3));
    			rs.add(r);
    			if(isDebug) System.out.print(r);
    			if(isDebug) System.out.println("\n");
    		}
    	}
    	return rs;
    }

	
	
}
