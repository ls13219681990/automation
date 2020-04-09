package com.linln.admin.system.common.report;

import java.io.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.linln.component.thymeleaf.utility.DictUtil;
import com.linln.modules.system.domain.MeasuringSpot;
import com.linln.modules.system.domain.MeasuringSpotData;
import com.linln.modules.system.page.ReportDataInfo;
import com.linln.modules.system.page.ReportDataPage;
import com.linln.modules.system.service.MeasuringSpotService;
import fr.opensagres.xdocreport.core.utils.StringUtils;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.jfree.chart.util.ExportUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openxmlformats.schemas.drawingml.x2006.chart.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import static com.linln.admin.system.common.report.HtmlToElement.*;


public class WordUtil2007 {


    /**
     * 根据指定的参数值、模板，生成 word 文档
     *
     * @param param    需要替换的变量
     * @param template 模板
     */

    public static XWPFDocument generateWord(Map<String, Object> param,

                                            String template) {


        CustomXWPFDocument doc = null;
/*
        try {

            OPCPackage pack = POIXMLDocument.openPackage(template);

            doc = new CustomXWPFDocument(pack);

            if (param != null && param.size() > 0) {


// 处理段落

                List<XWPFParagraph> paragraphList = doc.getParagraphs();

                processParagraphs(paragraphList, param, doc, workSpotId);


            }

        } catch (Exception e) {

            e.printStackTrace();

        }*/

        return doc;

    }


    /**
     * 处理段落中文本，替换文本中定义的变量；
     *
     * @param paragraphList 段落列表
     * @param param         需要替换的变量及变量值
     * @param doc           需要替换的DOC
     */

    public static XWPFDocument processParagraphs(List<XWPFParagraph> paragraphList,

                                                 Map<String, Object> param, XWPFDocument doc) {

        if (paragraphList != null && paragraphList.size() > 0) {

            for (XWPFParagraph paragraph : paragraphList) {

                List<XWPFRun> runs = paragraph.getRuns();

                for (XWPFRun run : runs) {

                    String text = run.getText(0);

                    if (text != null) {

                        boolean isSetText = false;

                        for (Entry<String, Object> entry : param.entrySet()) {

                            String key = entry.getKey();

                            if (text.indexOf(key) != -1) {

                                isSetText = true;

                                Object value = entry.getValue();

                                if (value instanceof String) {// 文本替换

                                    text = text.replace(key, value.toString());

                                }

                            }

                        }

                        if (isSetText) {

                            run.setText(text, 0);

                        }

                    }

                }

            }

        }
        return doc;
    }


    /**
     * 在定位的位置插入表格；
     *
     * @param key              定位的变量值
     * @param doc2             需要替换的DOC
     * @param element          把html代码解析成element
     * @param pictureList
     * @param measuringSpotMap
     */


    public static void insertTab(String key, XWPFDocument doc2, Element element, List<HashMap<String, List>> pictureList, Map<String, List<ReportDataInfo>> measuringSpotMap) {


        List<XWPFParagraph> paragraphList = doc2.getParagraphs();//获取文档的所有段落

        if (paragraphList != null && paragraphList.size() > 0) {


            for (XWPFParagraph paragraph : paragraphList) {

                List<XWPFRun> runs = paragraph.getRuns();
                int tail = 0;

                for (XWPFRun run : runs) {

                    String text = run.getText(0);//拿到每个段落的占位符


                    if (text != null) {

                        if (text.indexOf(key) >= 0) {//如果占位符跟我们的key一致就在此位置输出表格
                            org.jsoup.nodes.Document tableDoc = element.ownerDocument();

                            Elements trList = tableDoc.getElementsByTag("tr");
                            Elements tdList = trList.first().getElementsByTag("td");
                            //获取游标，在当前游标创建表格
                            XmlCursor cursor = paragraph.getCTP().newCursor();
                            XWPFTable table = doc2.insertNewTbl(cursor);

                            XWPFTableRow tableRowTitle = table.getRow(0);
                            //设置表格宽度
                            CTTblPr tblPr = table.getCTTbl().getTblPr();
                            tblPr.getTblW().setType(STTblWidth.DXA);
                            tblPr.getTblW().setW(new BigInteger("9000"));


                            Integer tdLen = 0;
                            if (key.equals("${地下水位}") || key.equals("${桩顶竖向位移}")) {
                                tdLen = 5;
                            } else if (key.equals("${混凝土撑钢筋受力}")) {
                                tdLen = 4;
                            } else {
                                tdLen = tdList.size();
                            }
                            for (int m = 0; m < tdLen - 1; m++) {
                                tableRowTitle.addNewTableCell().setText("");
                            }

                            //用于存储合并行，合并列的记录
                            boolean[][] colspanFlag = new boolean[trList.size()][tdLen + 1];
                            boolean[][] rowspanFlag = new boolean[trList.size()][tdLen + 1];

                            for (int row = 0; row < trList.size(); row++) {
                                int index = 0;
                                int item = 0;
                                XWPFTableRow createRow = table.createRow();
                                Element tr = trList.get(row);
                                Elements tds = tr.getElementsByTag("td");
                                List<String> tdData = new ArrayList<>();//用于记录这一行需要填充的值
                                for (Element elements : tds) {
                                    if (!elements.toString().equals("<td></td>")) {
                                        tdData.add(elements.text());
                                    }
                                }
                                for (int col = 0; col < tds.size(); col++) {
                                    Element td = tds.get(col);

                                    String colspan = td.attr("colspan");
                                    String rowspan = td.attr("rowspan");
                                    String align = tr.attr("align");
                                    String widthStyle = td.attr("width");
                                    String style = td.attr("style");

                                    // 记录需要合并的列
                                    if (!StringUtils.isEmpty(colspan)) {
                                        int colCount = Integer.parseInt(colspan);
                                        /*index += colCount - 1;*/


                                        for (int i = 0; i < colCount; i++) {//业务需求代码
                                            if (key.equals("${锚索应力}") || key.equals("${桩顶沉降}") || key.equals("${桩顶水平位移}") || key.equals("${钢筋应力}") || key.equals("${建筑物沉降}") || key.equals("${桥墩沉降}")) {
                                                if (i > 0) {
                                                    colspanFlag[row][item] = true;
                                                }
                                            } else {//通用的记录行代码
                                                if (col != 0) {
                                                    if (i > 0) {
                                                        if (item + 1 >= colspanFlag[row].length) {
                                                            item -= 1;
                                                        }
                                                        colspanFlag[row][item + 1] = true;
                                                    }
                                                } else {
                                                    if (i > 0) {
                                                        if (item + 1 >= colspanFlag[row].length && colCount != colspanFlag[row].length) {
                                                            item -= 1;
                                                        }
                                                        colspanFlag[row][item] = true;
                                                    }
                                                }
                                            }
                                            ++item;
                                        }
                                    }
                                    // 记录需要合并的行
                                    if (!StringUtils.isEmpty(rowspan)) {
                                        int rowCount = Integer.parseInt(rowspan);
                                        for (int i = 0; i < rowCount - 1; i++) {
                                            if (key.equals("${桥墩倾斜}")) {
                                                if (i > 0) {
                                                    if (item + 1 >= rowspanFlag[row].length && rowCount != rowspanFlag[row].length) {
                                                        item -= 1;
                                                    }
                                                    rowspanFlag[row][item] = true;
                                                }
                                            } else {
                                                rowspanFlag[row + i + 1][col] = true;
                                            }
                                            ++item;
                                        }
                                    }

                                    // 处理合并
                                    XWPFTableCell cell = table.getRow(row + 1).getCell(col);
                                    if (cell != null) {
                                        if (colspanFlag[row][col]) {//读取当前的列 如果为true 说明就是要合并的行  直接返回就行了 不需要再这行写数据
                                            setColMerge(cell, STMerge.CONTINUE);
                                            continue;
                                        } else {
                                            setColMerge(cell, STMerge.RESTART);
                                        }
                                        if (rowspanFlag[row][col]) {
                                            setRowMerge(cell, STMerge.CONTINUE);
                                            continue;
                                        } else {
                                            setRowMerge(cell, STMerge.RESTART);
                                        }
                                        // 设置列宽
                                        if (!Strings.isNullOrEmpty(widthStyle) && !"0".equals(widthStyle)) {
                                            int width = Integer.parseInt(widthStyle);
                                            setWidth(cell, width);
                                        }


                                        int size = cell.getParagraphs().size();
                                        if (size == 0) {
                                            paragraph = cell.addParagraph();
                                        } else {
                                            paragraph = cell.getParagraphs().get(size - 1);
                                        }
                                        // 设置水平样式
                                        if ("CENTER".equalsIgnoreCase(align)) {
                                            paragraph.setAlignment(ParagraphAlignment.CENTER);
                                        } else if ("LEFT".equalsIgnoreCase(align)) {
                                            paragraph.setAlignment(ParagraphAlignment.LEFT);
                                        }
                                        // 设置垂直居中
                                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                                    }

                                    if (index <= tdData.size() - 1 && tdData.get(index) != null && col <= createRow.getTableCells().size() - 1) {
                                        createRow.getCell(col).setText(tdData.get(index));//在这单元格写入数据
                                    }


                                    ++index;

                                }
                            }
                            table.removeRow(0);
                            table.removeRow(table.getRows().size() - 1);
                            ++tail;
                            //在表格后面插入折线图
                            for (HashMap<String, List> map : pictureList) {

                                String str = "";
                                Pattern pattern = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
                                Matcher matcher = pattern.matcher(key);
                                while (matcher.find()) {
                                    str = matcher.group();
                                }
                                Iterator<Entry<String, List>> iterator = map.entrySet().iterator();
                                //装在这个类型下面的测点名称
                                Map<String,List<ReportDataInfo>> listMap = new HashMap<>();

                                while (iterator.hasNext()) {
                                    Entry<String, List> next = iterator.next();
                                    for (int i = 0; i < next.getValue().size(); i++) {
                                        LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) next.getValue().get(i);
                                        if (DictUtil.keyValue("MEASUREMENT_ITEMS", dataMap.get("type").toString()).equals(str)) {//如果是一个类型添加到一张图处理
                                            Iterator<Entry<String, List<ReportDataInfo>>> entryIterator = measuringSpotMap.entrySet().iterator();
                                            while (entryIterator.hasNext()){
                                                Entry<String, List<ReportDataInfo>> spotEntry = entryIterator.next();
                                                if(dataMap.get("title").toString().equals(spotEntry.getKey())){
                                                    listMap.put(spotEntry.getKey(),spotEntry.getValue());
                                                }
                                            }
                                        }
                                    }
                                }
                               if(!CollectionUtils.isEmpty(listMap)){
                                   InputStream inputStream = XDDFChart.XYSeriesLineTest(listMap);
                                   run.addBreak();
                                   try {
                                       run.addPicture(inputStream, XWPFDocument.PICTURE_TYPE_JPEG, "LineChart.jpeg", Units.toEMU(500), Units.toEMU(320));
                                   } catch (InvalidFormatException e) {
                                       e.printStackTrace();
                                   } catch (IOException e) {
                                       e.printStackTrace();
                                   }
                               }
                            }
                        }
                    }
                }
            }
        }
    }



    /**
     * @param typeChart:传递的图形类型
     * @param serList:传递的serList(数据)
     * @param dataList:显示的数据
     * @param fldNameArr:属性列
     * @param position:取数的列
     * @return boolean
     * @Description:刷新数据方法(str进行分类)
     * @author nieds
     * @date 2019年6月11日上午10:36:56
     */
    public static boolean refreshStrGraphContent(Object typeChart,
                                                 List<?> serList, List<Map<String, String>> dataList, List<String> fldNameArr, List<String> titleArr, List<String> showtailArr, List<String> ispercentArr, int position) {

        boolean result = true;
        //更新数据区域
        for (int i = 0; i < serList.size(); i++) {
            //CTSerTx tx=null;
            CTAxDataSource cat = null;
            CTNumDataSource val = null;
            CTBarSer ser = ((CTBarChart) typeChart).getSerArray(i);
            //tx= ser.getTx();
            // Category Axis Data
            cat = ser.getCat();
            // 获取图表的值
            val = ser.getVal();
            // strData.set
            CTStrData strData = cat.getStrRef().getStrCache();
            CTNumData numData = val.getNumRef().getNumCache();
            strData.setPtArray((CTStrVal[]) null); // unset old axis text
            numData.setPtArray((CTNumVal[]) null); // unset old values

            // set model
            long idx = 0;
            for (int j = 0; j < dataList.size(); j++) {
                //判断获取的值是否为空
                String value = "0";
                if (new BigDecimal(dataList.get(j).get(fldNameArr.get(i + position))) != null) {
                    value = new BigDecimal(dataList.get(j).get(fldNameArr.get(i + position))).toString();
                }
                if (!"0".equals(value)) {
                    CTNumVal numVal = numData.addNewPt();//序列值
                    numVal.setIdx(idx);
                    numVal.setV(value);
                }
                CTStrVal sVal = strData.addNewPt();//序列名称
                sVal.setIdx(idx);
                sVal.setV(dataList.get(j).get(fldNameArr.get(0)));
                idx++;
            }
            numData.getPtCount().setVal(idx);
            strData.getPtCount().setVal(idx);


            //赋值横坐标数据区域
            String axisDataRange = new CellRangeAddress(1, dataList.size(), 0, 0)
                    .formatAsString("Sheet1", true);
            cat.getStrRef().setF(axisDataRange);

            //数据区域
            String numDataRange = new CellRangeAddress(1, dataList.size(), i + position, i + position)
                    .formatAsString("Sheet1", true);
            val.getNumRef().setF(numDataRange);
            if ("1".equals(ispercentArr.get(i + position))) {//是否设置百分比
                // 设置Y轴的数字为百分比样式显示
                StringBuilder sb = new StringBuilder();

                if ("0".equals(showtailArr.get(i + position))) {//保留几位小数
                    sb.append("0");
                    if ("1".equals(ispercentArr.get(i + position))) {//是否百分比
                        sb.append("%");
                    }
                } else {
                    sb.append("0.");
                    for (int k = 0; k < Integer.parseInt(showtailArr.get(i + position)); k++) {
                        sb.append("0");
                    }
                    if ("1".equals(ispercentArr.get(i + position))) {//是否百分比
                        sb.append("%");
                    }
                }
                val.getNumRef().getNumCache().setFormatCode(sb.toString());
            } else {
                //是否设置百分比
                // 设置Y轴的数字为百分比样式显示
                StringBuilder sb = new StringBuilder();

                if ("0".equals(showtailArr.get(i + position))) {//保留几位小数
                    sb.append("0");
                } else {
                    sb.append("0.");
                    for (int k = 0; k < Integer.parseInt(showtailArr.get(i + position)); k++) {
                        sb.append("0");
                    }
                }
                val.getNumRef().getNumCache().setFormatCode(sb.toString());
            }

        }
        return result;
    }

    public static boolean refreshExcel(XWPFChart chart,
                                       List<Map<String, String>> dataList, List<String> fldNameArr, List<String> titleArr,
                                       List<String> showtailArr, List<String> ispercentArr) {
        boolean result = true;
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");
        //根据数据创建excel第一行标题行
        for (int i = 0; i < titleArr.size(); i++) {
            if (sheet.getRow(0) == null) {
                sheet.createRow(0).createCell(i).setCellValue(titleArr.get(i) == null ? "" : titleArr.get(i));
            } else {
                sheet.getRow(0).createCell(i).setCellValue(titleArr.get(i) == null ? "" : titleArr.get(i));
            }
        }

        //遍历数据行
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, String> baseFormMap = dataList.get(i);//数据行
            //fldNameArr字段属性
            for (int j = 0; j < fldNameArr.size(); j++) {
                if (sheet.getRow(i + 1) == null) {
                    if (j == 0) {
                        try {
                            sheet.createRow(i + 1).createCell(j).setCellValue(baseFormMap.get(fldNameArr.get(j)) == null ? "" : baseFormMap.get(fldNameArr.get(j)));
                        } catch (Exception e) {
                            if (baseFormMap.get(fldNameArr.get(j)) == null) {
                                sheet.createRow(i + 1).createCell(j).setCellValue("");
                            } else {
                                sheet.createRow(i + 1).createCell(j).setCellValue(baseFormMap.get(fldNameArr.get(j)));
                            }
                        }
                    }
                } else {
                    BigDecimal b = new BigDecimal(baseFormMap.get(fldNameArr.get(j)));
                    double value = 0d;
                    if (b != null) {
                        value = b.doubleValue();
                    }
                    if (value == 0) {
                        sheet.getRow(i + 1).createCell(j);
                    } else {
                        sheet.getRow(i + 1).createCell(j).setCellValue(b.doubleValue());
                    }
                    if ("1".equals(ispercentArr.get(j))) {//是否设置百分比
                        // 设置Y轴的数字为百分比样式显示
                        StringBuilder sb = new StringBuilder();

                        if ("0".equals(showtailArr.get(j))) {//保留几位小数
                            sb.append("0");
                            if ("1".equals(ispercentArr.get(j))) {//是否百分比
                                sb.append("%");
                            }
                        } else {
                            sb.append("0.");
                            for (int k = 0; k < Integer.parseInt(showtailArr.get(j)); k++) {
                                sb.append("0");
                            }
                            if ("1".equals(ispercentArr.get(j))) {//是否百分比
                                sb.append("%");
                            }
                        }
                        CellStyle cellStyle = wb.createCellStyle();
                        cellStyle.setDataFormat(wb.createDataFormat().getFormat(sb.toString()));
                        sheet.getRow(i + 1).getCell(j).setCellStyle(cellStyle);
                    } else {
                        //是否设置百分比
                        // 设置Y轴的数字为百分比样式显示
                        StringBuilder sb = new StringBuilder();

                        if ("0".equals(showtailArr.get(j))) {//保留几位小数
                            sb.append("0");
                        } else {
                            sb.append("0.");
                            for (int k = 0; k < Integer.parseInt(showtailArr.get(j)); k++) {
                                sb.append("0");
                            }
                        }
                        CellStyle cellStyle = wb.createCellStyle();
                        cellStyle.setDataFormat(wb.createDataFormat().getFormat(sb.toString()));
                        sheet.getRow(i + 1).getCell(j).setCellStyle(cellStyle);
                    }
                }
            }

        }
        // 更新嵌入的workbook
        POIXMLDocumentPart xlsPart = chart.getRelations().get(0);
        OutputStream xlsOut = xlsPart.getPackagePart().getOutputStream();

        try {
            wb.write(xlsOut);
            xlsOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = false;
                }
            }
        }
        return result;
    }

    private static final BigDecimal bd2 = new BigDecimal("2");

    private static void setWordCellSelfStyle(XWPFTableCell cell, String fontName, String fontSize, int fontBlod,
                                             String alignment, String vertical, String fontColor,
                                             String bgColor, long cellWidth, String content) {

        //poi对字体大小设置特殊，不支持小数，但对原word字体大小做了乘2处理
        BigInteger bFontSize = new BigInteger("24");
        if (fontSize != null && !fontSize.equals("")) {
            //poi对字体大小设置特殊，不支持小数，但对原word字体大小做了乘2处理
            BigDecimal fontSizeBD = new BigDecimal(fontSize);
            fontSizeBD = bd2.multiply(fontSizeBD);
            fontSizeBD = fontSizeBD.setScale(0, BigDecimal.ROUND_HALF_UP);//这里取整
            bFontSize = new BigInteger(fontSizeBD.toString());// 字体大小
        }
        //=====获取单元格
        CTTc tc = cell.getCTTc();
        //====tcPr开始====》》》》
        CTTcPr tcPr = tc.getTcPr();//获取单元格里的<w:tcPr>
        if (tcPr == null) {//没有<w:tcPr>，创建
            tcPr = tc.addNewTcPr();
        }

        //  --vjc开始-->>
        CTVerticalJc vjc = tcPr.getVAlign();//获取<w:tcPr>  的<w:vAlign w:val="center"/>
        if (vjc == null) {//没有<w:w:vAlign/>，创建
            vjc = tcPr.addNewVAlign();
        }
        //设置单元格对齐方式
        vjc.setVal(vertical.equals("top") ? STVerticalJc.TOP : vertical.equals("bottom") ? STVerticalJc.BOTTOM : STVerticalJc.CENTER); //垂直对齐

        CTShd shd = tcPr.getShd();//获取<w:tcPr>里的<w:shd w:val="clear" w:color="auto" w:fill="C00000"/>
        if (shd == null) {//没有<w:shd>，创建
            shd = tcPr.addNewShd();
        }
        // 设置背景颜色
        shd.setFill(bgColor.substring(1));
        //《《《《====tcPr结束====

        //====p开始====》》》》
        CTP p = tc.getPList().get(0);//获取单元格里的<w:p w:rsidR="00C36068" w:rsidRPr="00B705A0" w:rsidRDefault="00C36068" w:rsidP="00C36068">

        //---ppr开始--->>>
        CTPPr ppr = p.getPPr();//获取<w:p>里的<w:pPr>
        if (ppr == null) {//没有<w:pPr>，创建
            ppr = p.addNewPPr();
        }
        //  --jc开始-->>
        CTJc jc = ppr.getJc();//获取<w:pPr>里的<w:jc w:val="left"/>
        if (jc == null) {//没有<w:jc/>，创建
            jc = ppr.addNewJc();
        }
        //设置单元格对齐方式
        jc.setVal(alignment.equals("left") ? STJc.LEFT : alignment.equals("right") ? STJc.RIGHT : STJc.CENTER); //水平对齐
        //  <<--jc结束--
        //  --pRpr开始-->>
        CTParaRPr pRpr = ppr.getRPr(); //获取<w:pPr>里的<w:rPr>
        if (pRpr == null) {//没有<w:rPr>，创建
            pRpr = ppr.addNewRPr();
        }
        CTFonts pfont = pRpr.getRFonts();//获取<w:rPr>里的<w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
        if (pfont == null) {//没有<w:rPr>，创建
            pfont = pRpr.addNewRFonts();
        }
        //设置字体
        pfont.setAscii(fontName);
        pfont.setEastAsia(fontName);
        pfont.setHAnsi(fontName);

        CTOnOff pb = pRpr.getB();//获取<w:rPr>里的<w:b/>
        if (pb == null) {//没有<w:b/>，创建
            pb = pRpr.addNewB();
        }
        //设置字体是否加粗
        pb.setVal(fontBlod == 1 ? STOnOff.ON : STOnOff.OFF);

        CTHpsMeasure psz = pRpr.getSz();//获取<w:rPr>里的<w:sz w:val="32"/>
        if (psz == null) {//没有<w:sz w:val="32"/>，创建
            psz = pRpr.addNewSz();
        }
        // 设置单元格字体大小
        psz.setVal(bFontSize);
        CTHpsMeasure pszCs = pRpr.getSzCs();//获取<w:rPr>里的<w:szCs w:val="32"/>
        if (pszCs == null) {//没有<w:szCs w:val="32"/>，创建
            pszCs = pRpr.addNewSzCs();
        }
        // 设置单元格字体大小
        pszCs.setVal(bFontSize);
        //  <<--pRpr结束--
        //<<<---ppr结束---

        //---r开始--->>>
        List<CTR> rlist = p.getRList(); //获取<w:p>里的<w:r w:rsidRPr="00B705A0">
        CTR r = null;
        if (rlist != null && rlist.size() > 0) {//获取第一个<w:r>
            r = rlist.get(0);
        } else {//没有<w:r>，创建
            r = p.addNewR();
        }
        //--rpr开始-->>
        CTRPr rpr = r.getRPr();//获取<w:r w:rsidRPr="00B705A0">里的<w:rPr>
        if (rpr == null) {//没有<w:rPr>，创建
            rpr = r.addNewRPr();
        }
        //->-
        CTFonts font = rpr.getRFonts();//获取<w:rPr>里的<w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
        if (font == null) {//没有<w:rFonts>，创建
            font = rpr.addNewRFonts();
        }
        //设置字体
        font.setAscii(fontName);
        font.setEastAsia(fontName);
        font.setHAnsi(fontName);

        CTOnOff b = rpr.getB();//获取<w:rPr>里的<w:b/>
        if (b == null) {//没有<w:b/>，创建
            b = rpr.addNewB();
        }
        //设置字体是否加粗
        b.setVal(fontBlod == 1 ? STOnOff.ON : STOnOff.OFF);
        CTColor color = rpr.getColor();//获取<w:rPr>里的<w:color w:val="FFFFFF" w:themeColor="background1"/>
        if (color == null) {//没有<w:color>，创建
            color = rpr.addNewColor();
        }
        // 设置字体颜色
        if (content.contains("↓")) {
            color.setVal("43CD80");
        } else if (content.contains("↑")) {
            color.setVal("943634");
        } else {
            color.setVal(fontColor.substring(1));
        }
        CTHpsMeasure sz = rpr.getSz();
        if (sz == null) {
            sz = rpr.addNewSz();
        }
        sz.setVal(bFontSize);
        CTHpsMeasure szCs = rpr.getSzCs();
        if (szCs == null) {
            szCs = rpr.addNewSz();
        }
        szCs.setVal(bFontSize);
        //-<-
        //<<--rpr结束--
        List<CTText> tlist = r.getTList();
        CTText t = null;
        if (tlist != null && tlist.size() > 0) {//获取第一个<w:r>
            t = tlist.get(0);
        } else {//没有<w:r>，创建
            t = r.addNewT();
        }
        t.setStringValue(content);
        //<<<---r结束---
    }


    public static XWPFDocument generateDoc(String path, Map<String, Object> param) {
        //创建文档对象
        XWPFDocument doc = null;
        try {
            doc = new XWPFDocument(new FileInputStream(new File(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取所有段落
        List<XWPFParagraph> paragraphList = doc.getParagraphs();
        for (XWPFParagraph paragraph : paragraphList) {
            //遍历获取段落中所有的runs
            List<XWPFRun> runs = paragraph.getRuns();
            //合并逻辑
            for (Integer i = 0; i < runs.size(); i++) {
                String text0 = runs.get(i).getText(runs.get(i).getTextPosition());
                if (text0 != null && text0.indexOf("{") >= 0) {
                    //记录分隔符中间跨越的runs数量，用于字符串拼接和替换
                    int num = 0;
                    int j = i + 1;
                    for (; j < runs.size(); j++) {
                        String text1 = runs.get(j).getText(runs.get(j).getTextPosition());
                        if (text1 != null && text1.indexOf("}") >= 0) {
                            num = j - i;
                            break;
                        }
                    }
                    if (num != 0) {
                        //num!=0说明找到了{}配对，需要替换
                        StringBuilder newText = new StringBuilder();
                        for (int s = i; s <= i + num; s++) {
                            String text2 = runs.get(s).getText(runs.get(s).getTextPosition());
                            newText.append(text2);
                            runs.get(s).setText(null, 0);
                        }
                        runs.get(i).setText(newText.toString(), 0);

                        //重新定义遍历位置，跳过设置为null的位置
                        i = j + 1;
                    }
                }
            }

            //变量替换逻辑
            for (int i = 0; i < runs.size(); i++) {
                XWPFRun run = runs.get(i);
                String text = run.getText(runs.get(i).getTextPosition());
                if (text != null) {
                    boolean isSetText = false;
                    for (Map.Entry<String, Object> entry : param.entrySet()) {
                        String key = entry.getKey();
                        if (text.indexOf(key) != -1) {
                            isSetText = true;
                            Object value = entry.getValue();
                            if (value instanceof String) {//文本替换
                                text = text.replace(key, value.toString());
                            } else if (value instanceof Map) {//图片替换
                                //从map中获取图片的宽、高、位置和描述信息，编写图片定义xml 放入
                            }
                        }
                    }
                    if (isSetText) {
                        run.setText(text, 0);
                    }
                }
            }
        }
        return doc;
    }

    public static void insertImage(String key, XWPFDocument doc) {


        List<XWPFParagraph> paragraphList = doc.getParagraphs();


        try {


            if (paragraphList != null && paragraphList.size() > 0) {


                for (XWPFParagraph paragraph : paragraphList) {

                    List<XWPFRun> runs = paragraph.getRuns();


                    for (XWPFRun run : runs) {

                        String text = run.getText(0);


                        if (text != null) {

                            if (text.indexOf(key) >= 0) {


                                run.addBreak();

                                run.addPicture(

                                        new FileInputStream("c:/11.jpg"), Document.PICTURE_TYPE_JPEG, "c:/11.jpg", Units.toEMU(200), Units.toEMU(200)); // 200x200 pixels

                                run.addBreak(BreakType.PAGE);


                            }

                        }

                    }


                }

            }


        } catch (InvalidFormatException e) {

// TODO Auto-generated catch block

            e.printStackTrace();

        } catch (FileNotFoundException e) {

// TODO Auto-generated catch block

            e.printStackTrace();

        } catch (IOException e) {

// TODO Auto-generated catch block

            e.printStackTrace();

        }


    }


    /**
     * 测试用方法
     */


    public static void main(String[] args) throws Exception {


        Map<String, Object> param = new HashMap<String, Object>();

        param.put("${name}", "哈哈哈哈");
        param.put("${zhuanye}", "信息管理与信息系统");
        param.put("${sex}", "男");
        param.put("${school}", "大学");
        param.put("${date}", "2016-09-21");
        param.put("${桩顶水平位移}", "${桩顶水平位移}");

        XWPFDocument doc = WordUtil2007.generateDoc("d:\\2.docx", param);

        /*String tableContent = "<table style=\"width: 600px;margin:auto\" cellspacing=\"0\" cellpadding=\"3\" border=\"1\"><tbody>" +
                "<tr align=\"center\"><td>工程名称</td><td colspan=\"7\">成都轨道交通8号线一期工程永丰站～芳草街站盾构区间</td></tr>" +
                "<tr align=\"center\"><td>施工单位</td><td colspan=\"3\">北京施工单位</td><td colspan=\"2\">检测项目</td><td colspan=\"2\">分层沉降(右线)</td></tr>" +
                "</tbody></table>";*/
        String tableContent = "<table style=\"width: 600px;margin:auto\" cellspacing=\"0\" cellpadding=\"3\" border=\"1\"><tbody><tr align=\"center\"><td >工程名称</td><td colspan=\"7\">成都轨道交通8号线一期工程永丰站～芳草街站盾构区间</td></tr><tr align=\"center\"><td >施工单位</td><td colspan=\"3\">北京施工单位</td><td colspan=\"2\">检测项目</td><td colspan=\"2\"><font style=\"color:red;\">分层沉降(右线)</font></td></tr><tr align=\"center\"><td >监理单位</td><td colspan=\"3\">北京监理单位</td><td colspan=\"2\">上次观测日期</td><td colspan=\"2\">2019-12-06 15:22:08</td> </tr><tr align=\"center\"><td >仪器规格</td><td colspan=\"3\"></td> <td colspan=\"2\">本次观测日期</td><td colspan=\"2\">2019-12-11 10:03:27</td></tr><tr align=\"center\"><td rowspan=\"2\">里程</td><td rowspan=\"2\">测点编号</td><td>初始测试值</td><td>上次测试值</td><td>本次测试值</td><td>变形量</td><td>累计值</td><td rowspan=\"2\">初测日期</td></tr>\n" +
                "<tr align=\"center\"><td>(mm)</td><td>(mm)</td><td>(mm)</td><td>(mm)</td><td>(mm)</td></tr><tr align=\"center\"><td rowspan=\"1\" contenteditable=\"true\">K12+000</td><td contenteditable=\"true\">fccj</td><td contenteditable=\"true\">null</td><td contenteditable=\"true\">0</td><td contenteditable=\"true\">2</td><td contenteditable=\"true\">2</td><td contenteditable=\"true\">2</td><td rowspan=\"1\" contenteditable=\"true\">2019-11-06</td></tr><tr align=\"center\"><td colspan=\"5\">最大值</td><td><font style=\"color:red;\">2</font></td><td><font style=\"color:red;\">2</font></td><td></td></tr> <tr><td colspan=\"8\">备注：1.“+”值表示测点隆起，“-”值表示测点下沉，“/”表示测点被挡或破坏。</td> </tr> <tr align=\"center\"><td colspan=\"8\"><input type=\"file\" name=\"file\" id=\"file1\" accept=\"image/*\" onchange=\"imgChange(this);\"> <!--文件上传选择按钮--> <img id=\"imghead1\" src=\"\"> <!--图片显示位置--> </td></tr></tbody></table>";
        Element node = supplementTable(tableContent);


        /*WordUtil2007.insertTab("${桩顶水平位移}", doc, node, pictureList); // /----------创建表

// ------替换多余的标志位----//
        param = new HashMap<String, Object>();
        param.put("${桩顶水平位移}", "");
        WordUtil2007.processParagraphs(doc.getParagraphs(), param, doc, workSpotId);
        FileOutputStream fopts = new FileOutputStream("d:\\2007-2.docx");
        doc.write(fopts);*/
    }


}
