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
		File pdfFile = new File("D:/temp/report_temp/report_origin.pdf");
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
    	Pattern reportNumPat = Pattern.compile("工程编号:\\s*(\\d+)");
    	String reportNum = "";
    	String[] lineListt = paragraph.split("\r\n");
    	for(int i=0;i<lineListt.length;i++){
            String line = lineListt[i];
            Matcher reportNumMat = reportNumPat.matcher(line);
            if(reportNumMat.find()){
                reportNum = line.split(":")[1].trim();
                break;
            }
        }
    	
    	Pattern jcxPat = Pattern.compile("\r\n检\\s测\\s项:\\s");
    	Matcher partMat = jcxPat.matcher(paragraph);
        String tempStr = null;
        int start = 0,end = 0;
        if(partMat.find()){
            start = partMat.start();// 设置第一个部分开始位置
        }
        while(partMat.find()){
            end = partMat.start();
            tempStr = paragraph.substring(start,end);
            //System.out.println(tempStr);
            
            if(null !=tempStr && "".equals(tempStr)){
                continue;
            }
            rs.add(this.parseFourthPart(tempStr, reportNum));
            start = end;
        }
        tempStr = paragraph.substring(start,paragraph.length()-1);
        rs.add(this.parseFourthPart(tempStr, reportNum));
    	return rs;
    }
    
    /**
     * 检 测 项: 7.1.14(消火栓系统、消防供水设施、水泵故障信号反馈)
            重要等级: B
            规范要求: 水泵发生故障时,应有信号反馈回消防控制室
            以下是不符合规范要求的检测点：
      00000D2101 地下室2层、第101号检测点 (1#)
      00000D2102 地下室2层、第102号检测点 (2#)
     * @param src
     * @param reportNum
     * @return
     */
    private ListResult parseFourthPart(String src, String reportNum){
        String[] lines = src.split("\r\n");
        String line = null;
        String testItem = null;
        String importantGrade = null;
        String requirements = null;
        List<String> nonstandardItems = new ArrayList<String>();
        
        int position = 0;
        for(int i=0;i<lines.length;i++){
            line = lines[i];
            if(null == line || "".equals(line) || line.contains(reportNum) || line.contains("天河区开展第三方消防设施检测项目技术咨询报告")){
                continue;
            }
            
            if(position == 0){
                if(line.contains("检 测 项:")){
                    testItem = line.split(":")[1].trim();
                    position = 1;
                }
            }else if(position == 1){
                if(line.contains("重要等级:")){
                    importantGrade = line.split(":")[1].trim();
                    position = 2;
                }else{
                    testItem += line.trim();
                }
            }else if(position == 2){
                if(line.contains("规范要求:")){
                    requirements = line.split(":")[1].trim();
                    position = 3;
                }else{
                    importantGrade += line.trim();
                }
            }else if(position == 3){
                if(line.contains("以下是不符合规范要求的检测点")){
                    position = 4;
                }else{
                    requirements += line.trim();
                }
            }else if(position == 4){
                nonstandardItems.add(line);
            }
        }
        
        return new ListResult(reportNum, testItem, importantGrade, requirements, nonstandardItems);
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
		List<Result> rs = new ArrayList<Result>();
		// 匹配换行+数字.组合
		Pattern linePat = Pattern.compile("\r\n[\\d]{1,}[\\.]{0,}");
		Matcher lineMatcher = linePat.matcher(paragraph);
		int start = 0;
		int end = 1;
		String tempStr = "";
		// 获取匹配index，截取字段，分别解析
		if(lineMatcher.find()){
		    start = lineMatcher.start();  
		}
		while(lineMatcher.find()){
		    end = lineMatcher.start();
		    tempStr = paragraph.substring(start,end);
		    //System.out.println(tempStr);
		    
		    if(null !=tempStr && "".equals(tempStr)){
		        continue;
		    }
		    rs.add(this.parseThirdPart(tempStr,returnObj.getCover().getReportNum()));
		    start = end;
		}
		tempStr = paragraph.substring(start,paragraph.length()-1);
		rs.add(this.parseThirdPart(tempStr,returnObj.getCover().getReportNum()));
    	return rs;
    }
    private Result parseThirdPart(String tempStr, String reportNum){
        if(null == reportNum){
            reportNum = "none report num";
        }
     // set label
        String label = tempStr.substring(0, tempStr.indexOf(" "));
        tempStr = tempStr.replace(label, "");
        String[] lines = tempStr.split("\r\n");
        String line = null;
        String level = null;
        String value1 = null;
        String value2 = null;
        String name = null;
        StringBuffer sb = new StringBuffer();
        boolean flag = false;
        for(int i=0;i<lines.length;i++){
            line = lines[i];
            
            if(line == null || "".equals(line)){
                continue;
            }
            
            // 处理脏数据
            if(line.contains("项目编号") || line.contains("天河区开展第三方消防设施检测项目技术咨询报告") || line.contains(reportNum)){
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
            Pattern valuePatt = Pattern.compile("\\s[\\d]{1,}\\s\\s[\\d]{1,}");
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
        String desription = sb.toString();
        String solution = null;
        if(null!=desription){
            String[] des = desription.split("\\s");
            if(null!=des && des.length ==2){
                name = des[0];
                solution = des[1];
            }
        }
        return new Result(label, name,solution, level, value1, value2); 
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
        List<Result> rs = new ArrayList<Result>();
        Pattern linePat = Pattern.compile("\r\n[\\d]{1,}[\\s]{1}");
        Matcher lineMatcher = linePat.matcher(paragraph);
        int start = 0;
        int end = 1;
        String tempStr = "";
        Result result = null;
        List<String> strs = null;
        Pattern labelPat = Pattern.compile("\r\n[\\d]{1,}[\\s]{1}");
        Pattern namePat = Pattern.compile("[A|B|C]{1}[\\s]{2}[\\d]{1,}[\\s]{2}[\\d]{1,}");
        Matcher tempMatcher = null;
        String tempValue = "";
        String label = "";
        String name = "";
        // 获取匹配index，截取字段，分别解析
        while(lineMatcher.find()){
            strs = new ArrayList<String>();
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
            tempMatcher = labelPat.matcher(tempStr);
            while(tempMatcher.find()){
                tempValue = tempMatcher.group();
                tempStr.replace(tempValue, "");
                tempValue.replace("\r\n", "");
                label = tempValue.trim();
                tempValue = "";
            }
            // set level value1 value2
            tempMatcher = namePat.matcher(tempStr);
            while(tempMatcher.find()){
                tempValue = tempMatcher.group();
                tempStr.replace(tempValue, "");
                strs.add(tempValue);
            }
            // set name
            name = tempStr.trim();
            for(String str:strs){
                result = new Result();
                result.setName(name);
                result.setLabel(label);
                String[] arr = str.split("\\s\\s");
                result.setLevel(arr[0]);
                result.setValue1(arr[1]);
                result.setValue2(arr[2]);
                rs.add(result);
            }
        }
    	return rs;
    }

	
	
}
