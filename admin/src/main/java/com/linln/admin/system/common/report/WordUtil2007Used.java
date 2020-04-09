package com.linln.admin.system.common.report;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class WordUtil2007Used {



    /**

     * 根据指定的参数值、模板，生成 word 文档

     *

     * @param param

     *            需要替换的变量

     * @param template

     *            模板

     */

    public static XWPFDocument generateWord(Map<String, Object> param,

                                            String template) {

        CustomXWPFDocument doc = null;

        /*try {

            OPCPackage pack = POIXMLDocument.openPackage(template);

            doc = new CustomXWPFDocument(pack);

            if (param != null && param.size() > 0) {



// 处理段落

                List<XWPFParagraph> paragraphList = doc.getParagraphs();

                processParagraphs(paragraphList, param, doc);



            }

        } catch (Exception e) {

            e.printStackTrace();

        }*/

        return doc;

    }



    /**

     * 处理段落中文本，替换文本中定义的变量；

     *

     * @param paragraphList

     *            段落列表

     * @param param

     *            需要替换的变量及变量值

     * @param doc

     *            需要替换的DOC

     */

    public static void processParagraphs(List<XWPFParagraph> paragraphList,

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

    }



    /**

     * 在定位的位置插入表格；

     *

     * @param key

     *            定位的变量值

     * @param doc2

     *            需要替换的DOC

     */



    public static void insertTab(String key, XWPFDocument doc2) {



        List<XWPFParagraph> paragraphList = doc2.getParagraphs();

        if (paragraphList != null && paragraphList.size() > 0) {



            for (XWPFParagraph paragraph : paragraphList) {

                List<XWPFRun> runs = paragraph.getRuns();



                for (XWPFRun run : runs) {

                    String text = run.getText(0);



                    if (text != null) {

                        if (text.indexOf(key) >= 0) {



                            XmlCursor cursor = paragraph.getCTP().newCursor();



                            XWPFTable tableOne = doc2.insertNewTbl(cursor);// ---这个是关键



// XWPFTable tableOne =

// paragraph.getDocument().createTable();



                            XWPFTableRow tableOneRowOne = tableOne.getRow(0);

                            tableOneRowOne.getCell(0).setText("一行一列");

                            XWPFTableCell cell12 = tableOneRowOne.createCell();

                            cell12.setText("一行二列");

// tableOneRowOne.addNewTableCell().setText("第1行第2列");

// tableOneRowOne.addNewTableCell().setText("第1行第3列");

// tableOneRowOne.addNewTableCell().setText("第1行第4列");



                            XWPFTableRow tableOneRowTwo = tableOne.createRow();

                            tableOneRowTwo.getCell(0).setText("第二行第一列");

                            tableOneRowTwo.getCell(1).setText("第二行第二列");

// tableOneRowTwo.getCell(2).setText("第2行第3列");



                            XWPFTableRow tableOneRow3 = tableOne.createRow();

//---顺序增加行后，忽略第1、2单元格，直接插入3、4

                            tableOneRow3.addNewTableCell().setText("第三行第3列");

                            tableOneRow3.addNewTableCell().setText("第三行第4列");

                           /* for (boolean[] booleans : colspanFlag) {
                                for (boolean b : booleans) {
                                    System.out.print(b + "、");
                                }
                                System.out.println();
                            }
                            for (boolean[] booleans : rowspanFlag) {
                                for (boolean b : booleans) {
                                    System.out.print(b + "、");
                                }
                                System.out.println();
                            }*/

                        }

                    }

                }



            }

        }


    }

    public static XWPFDocument generateDoc(String path, Map<String, Object> param){
        //创建文档对象
        XWPFDocument doc = null;
        try {
            doc = new XWPFDocument(new FileInputStream(new File(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取所有段落
        List<XWPFParagraph> paragraphList = doc.getParagraphs();
        for(XWPFParagraph paragraph:paragraphList){
            //遍历获取段落中所有的runs
            List<XWPFRun> runs = paragraph.getRuns();
            //合并逻辑
            for(Integer i = 0; i < runs.size(); i++){
                String text0 = runs.get(i).getText(runs.get(i).getTextPosition());
                if(text0!=null && text0.indexOf("{") >= 0){
                    //记录分隔符中间跨越的runs数量，用于字符串拼接和替换
                    int num=0;
                    int j = i+1;
                    for(; j < runs.size(); j++){
                        String text1 = runs.get(j).getText(runs.get(j).getTextPosition());
                        if(text1!=null && text1.indexOf("}") >= 0){
                            num=j-i;
                            break;
                        }
                    }
                    if(num!=0) {
                        //num!=0说明找到了[]配对，需要替换
                        StringBuilder newText = new StringBuilder();
                        for (int s = i; s <= i+num; s++) {
                            String text2 = runs.get(s).getText(runs.get(s).getTextPosition());
                            newText.append(text2);
                            runs.get(s).setText(null, 0);
                        }
                        runs.get(i).setText(newText.toString(),0);

                        //重新定义遍历位置，跳过设置为null的位置
                        i=j+1;
                    }
                }
            }

            //变量替换逻辑
            for(int i = 0; i < runs.size(); i++){
                XWPFRun run = runs.get(i);
                String text = run.getText(runs.get(i).getTextPosition());
                if(text != null){
                    boolean isSetText = false;
                    for (Map.Entry<String, Object> entry : param.entrySet()) {
                        String key = entry.getKey();
                        if(text.indexOf(key) != -1){
                            isSetText = true;
                            Object value = entry.getValue();
                            if (value instanceof String) {//文本替换
                                text = text.replace(key, value.toString());
                            } else if (value instanceof Map) {//图片替换
                                //从map中获取图片的宽、高、位置和描述信息，编写图片定义xml 放入
                            }
                        }
                    }
                    if(isSetText){
                        run.setText(text,0);
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

                                        new FileInputStream("c:/11.jpg"),Document.PICTURE_TYPE_JPEG,"c:/11.jpg", Units.toEMU(200),Units.toEMU(200)); // 200x200 pixels

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

        XWPFDocument doc = WordUtil2007Used.generateWord(param, "d:\\2.docx");
        XWPFDocument xwpfDocument = generateDoc("d:\\2.docx", param);



        WordUtil2007Used.insertTab("${桩顶水平位移}", xwpfDocument); // /----------创建表

        WordUtil2007Used.insertImage("${image}", xwpfDocument); // /----------创建图



// ------替换多余的标志位----//



        param = new HashMap<String, Object>();

        param.put("${test}", "下一个段落");

        param.put("${桩顶水平位移}", "");

        param.put("${image}", "");



        WordUtil2007Used.processParagraphs(doc.getParagraphs(), param, doc);



        FileOutputStream fopts = new FileOutputStream("d:\\2007-2.docx");

        doc.write(fopts);



    }

}

