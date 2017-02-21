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
	    
	    Pattern pattern =Pattern.compile("正则表达式");
	    Matcher matcher =pattern.matcher("正则表达式 HelloWorld,正则表达式 Hello World");
	    //替换第一个符合正则的数据
	    System.out.println(matcher.matches());

		long startTime = System.currentTimeMillis();
		File pdfFile = new File("D:/temp/report_temp/report1.pdf");
		//File pdfFile = new File("D:/report_temp/report1.pdf");
		//File pdfFile = new File("/Users/wanglei/Downloads/检测报告样本_client_import.pdf");
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
	        List<Result> rs = processOnThirdParagraph(paragraph, returnObj);
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
        stripper.setSortByPosition(true);
        stripper.setStartPage(1);
        stripper.setEndPage(lastPage);
        String allText = stripper.getText(pdfDocument);
        int sIndex = 0;
        int eIndex = 0;
        {
	        String end = "建筑消防设施检测报告";
	        eIndex = allText.indexOf(end);
	        String paragraph = allText.substring(0, eIndex);
	        Cover cover = null;
            try {
                cover = this.processOnCover(paragraph);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	        returnObj.setCover(cover);
	        returnObj.setReportNum(cover.getReportNum());
        	
        }
        {
	        String start = "单项评定结果";
	        String end = "检测结论说明";
	        sIndex = allText.indexOf(start)+start.length();
	        eIndex = allText.indexOf(end);
	        String paragraph = allText.substring(sIndex, eIndex);
	        List<Result> rs = null;
            try {
                rs = processOnFirstParagraph(paragraph);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	        returnObj.setFirstPart(rs);
        }
        {
	        String start = "检测结论说明";
	        String end = "检测情况统计表";
	        sIndex = allText.indexOf(start)+start.length();
	        eIndex = allText.indexOf(end);
	        String paragraph = allText.substring(sIndex, eIndex);
	        String rs = null;
            try {
                rs = processOnSecondParagraph(paragraph);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	        returnObj.setSecondPart(rs);
	        returnObj.getCover().setReportConclusion(rs);
        }
        {
	        String start = "检测情况统计表";
	        String end = "消防设施检测不符合规范要求项目";
	        sIndex = allText.indexOf(start)+start.length();
	        eIndex = allText.indexOf(end);
	        String paragraph = allText.substring(sIndex, eIndex);
	        List<Result> rs = null;
            try {
                rs = processOnThirdParagraph(paragraph, returnObj);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	        returnObj.setThirdPart(rs);
        }
        {
	        String start = "消防设施检测不符合规范要求项目";
	        String end = "消防设备登记";
	        sIndex = allText.indexOf(start)+start.length();
	        eIndex = allText.indexOf(end);
	        String paragraph = allText.substring(sIndex, eIndex);
	        List<ListResult> rs = null;
            try {
                rs = processOnForthParagraph(paragraph);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	        returnObj.setForthPart(rs);
        }
        {
	        String start = "消防设备登记";
	        sIndex = allText.indexOf(start)+start.length();
	        String paragraph = allText.substring(sIndex);
	        List<ListResult> rs = null;
            try {
                rs = processOnFifthParagraph(paragraph);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	        returnObj.setFifthPart(rs);
        }
        return returnObj;
    }
    private Cover processOnCover(String paragraph){
    	Cover cover = new Cover();
    	String[] lines = paragraph.split("\r\n");

		Pattern projectName = Pattern.compile("^项目名称:\\s*(.*)\\s*$");
		Pattern projectAddress = Pattern.compile("^项目地址:\\s*(.*)\\s*$");
		Pattern agentName = Pattern.compile("^委托单位:\\s*(.*)\\s*$");
		Pattern qaName = Pattern.compile("^检测单位:\\s*(.*)\\s*$");
		Pattern reportNum = Pattern.compile("^天消\\s*(.*)\\s*$");
		Pattern qaAddress = Pattern.compile("^检测单位地址:\\s*(.*)\\s*$");
		Pattern contactTel = Pattern.compile("^电\\s+话:\\s*(.*)\\s*$");
		Pattern contactFax = Pattern.compile("^传\\s+真:\\s*(.*)\\s*$");
		Pattern contactPostcode = Pattern.compile("^邮\\s+编:\\s*(.*)\\s*$");

		int projectNameLine = 0;
		int projectAddrLine = 0;
    	for(int i=0;i<lines.length;i++){
    		String line = lines[i];
    		if(isDebug) System.out.println("LINE=>"+line);
    		Matcher m = projectName.matcher(line);
    		if(m.find()){
    			cover.setProjectName(m.group(1));
    			projectNameLine = i;
    			continue;
    		}
    		m = projectAddress.matcher(line);
    		if(m.find()){
    			cover.setProjectAddress(m.group(1));
    			projectAddrLine = i;
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
    	// 修改项目名称
    	if(projectAddrLine - projectNameLine > 1){
    	    StringBuffer sb = new StringBuffer(cover.getProjectName());
    	    for(int j = projectNameLine + 1 ; j < projectAddrLine; j++){
    	        sb.append(lines[j].trim());
    	    }
    	    cover.setProjectName(sb.toString());
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
    /**
     * 
     * @param paragraph
     * @return
     * @template 
项目编号 检测项 重要等级 检测标准(规范要求) 检测点数 不合格点数
6 消防给水(消防水源)
6.2 消防水池
6.2.1 消防水池自动补水设施设置 应按设计要求设置，其补水设施应正常(应
B 设水泵自动启停装置或浮球阀等自动补水设
施)  1  0
6.2.2 消防水池有效容积、格数 应符合规范及设计的要求
B
 1  0
6.2.6 消防用水与其他用水共用水池的技术 应采取确保消防用水量不作他用的技术措施
措施 B
 1  0
6.2.7 消防水池出水管 应保证消防水池的有效容积能被全部利用
B
 1  0
6.2.9 消防水池的溢流水管、排水设施 消防水池应设置溢流水管和排水设施，并应
C 采用间接排水
 1  0
7 消火栓系统
7.1 消防供水设施
7.1.1 消防水泵设置及选型 应按设计要求设置，选型应满足消防给水系
A 统的流量和压力需求
 2  0
7.1.2 消防水泵备用泵的设置 消防水泵应设置备用泵(除建筑高度小
B 于54m的住宅和室外消防给水设计流
量≤25L/s的建筑、室内消防给水设计流  1  0
量≤10L/s的建筑外
7.1.3 水泵控制柜 消防水泵控制柜在平时应使消防水泵处于自
B 动启泵状态，应注明所属系统编号的标
志，按钮、指示灯及仪表应正常  1  0
广东建筑消防设施检测中心有限公司 2017年1月18日 16GJA153 第 6 页，共 44 页
天河区开展第三方消防设施检测项目技术咨询报告
项目编号 检测项 重要等级 检测标准(规范要求) 检测点数 不合格点数
7.1.4 主备泵的切换 主泵不能正常投入运行时,应自动切换启动
A 备用泵
 2  0
7.1.5 水泵外观质量及安装质量 泵及电机的外观表面不应有碰损，轴心不应
C 有偏心；水泵之间及其与墙或其他设备之间
的间距应满足安装、运行、维护管理要求  2  0
     */
    private List<Result> processOnThirdParagraph(String paragraph, PDFParserResult returnObj){
    	String level = "", label = "", name = "", value1 = "", value2 = "";
		Pattern valuePatt = Pattern.compile("\\s[\\d]{1,}\\s\\s[\\d]{1,}");
		
		List<Result> rs = new ArrayList<Result>();
		// 匹配换行+数字.组合
		Pattern linePat = Pattern.compile("\r\n[\\d]{1,}[\\.]{0,}");
		Matcher lineMatcher = linePat.matcher(paragraph);
		int start = 0;
		int end = 1;
		String tempStr = "";
		// 获取匹配index，截取字段，分别解析
		while(lineMatcher.find()){
		    start = lineMatcher.start();
		    if(lineMatcher.find()){
		        end = lineMatcher.start();
		    }else{
		        end = paragraph.length()-1;
		    }
		    tempStr = paragraph.substring(start,end);
		    //System.out.println(tempStr);
		    
		    if(null !=tempStr && "".equals(tempStr)){
		        continue;
		    }
		    // set label
		    label = tempStr.substring(0, tempStr.indexOf(" "));
		    tempStr = tempStr.replace(label, "");
		    String[] lines = tempStr.split("\r\n");
		    String line = null;
		    StringBuffer sb = new StringBuffer();
		    boolean flag = false;
		    for(int i=0;i<lines.length;i++){
		        line = lines[i];
		        
		        if(line == null || "".equals(line)){
		            continue;
		        }
		        
		        // 处理脏数据
		        if(line.contains("项目编号") || line.contains("天河区开展第三方消防设施检测项目技术咨询报告") || line.contains(returnObj.getCover().getReportNum())){
		            continue;
		        }
		        // set level
		        flag = false;
		        if(line.startsWith("A")||line.contains(" A")){
		           level = "A"; 
		           flag = true;
		        }else if(line.startsWith("B")||line.contains(" B")){
		            level = "B";
		            flag = true;
		        }else if(line.startsWith("C")||line.contains(" C")){
                    level = "C";
                    flag = true;
                }
		        if(flag){
		            line = line.replace(level, "");
		            line = line.trim();
		            sb.append(line);
		            continue;
		        }
		        // set value1,value2
		        Matcher m = valuePatt.matcher(line);
                if(m.find()){
                    value1 = String.valueOf(m.group().replaceAll(" ", "").charAt(0));// 获取第一位数字
                    value2 = String.valueOf(m.group().replaceAll(" ", "").charAt(1));// 获取第二位数字
                    line = line.replace(m.group(), "");
                    line = line.trim();
                    sb.append(line);
                    continue;
                }
                line = line.trim();
		        sb.append(line);
		    }
		    name = sb.toString();
		    System.out.println(name);
		    rs.add(new Result(label, name, level, value1, value2));
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
