package com.linln.admin.system.common.word;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.util.ExportUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.*;

public class XDDFChart {
    public static void main(String[] args) throws IOException, InvalidFormatException {
        //设置JFreeChart主题，不设置会出现中文乱码情况
        setTheme();
        //XY折线图演示
        XYSeriesLineTest();
    }
    public static void XYSeriesLineTest() throws IOException, InvalidFormatException {
        XWPFDocument doc=new XWPFDocument();



        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
        mDataset.addValue(1, "First", "2013");
        mDataset.addValue(3, "First", "2014");
        mDataset.addValue(2, "First", "2015");
        mDataset.addValue(6, "First", "2016");
        mDataset.addValue(5, "First", "2017");
        mDataset.addValue(12, "First", "2018");
        mDataset.addValue(14, "Second", "2013");
        mDataset.addValue(13, "Second", "2014");
        mDataset.addValue(12, "Second", "2015");
        mDataset.addValue(9, "Second", "2016");
        mDataset.addValue(5, "Second", "2017");
        mDataset.addValue(7, "Second", "2018");

        //生成图形
        JFreeChart mChart = ChartFactory.createLineChart(
                "折线图",//图名字
                "年份",//横坐标
                "数量",//纵坐标
                mDataset,//数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 采用标准生成器
                false);// 是否生成超链接
        String path="D:\\barChart.jpeg";
        String wPath="D:\\arChart.docx";
        SaveDoc(wPath,path,mChart,doc);

    }

    //保存图形到word文档中，这里是先生成image文件，然后又用输入流将文件导入，感觉很傻，
    //如果您有更好的方法，请在评论区留言
    private static void SaveDoc(String wpath, String path, JFreeChart chart, XWPFDocument doc) throws IOException, InvalidFormatException {
        File chartFile = new File(path);
        if(chartFile.exists())chartFile.delete();
        ExportUtils.writeAsJPEG(chart,400,280,chartFile);
        InputStream is=new FileInputStream(chartFile);

        XWPFParagraph paragraph = doc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setSpacingBetween(1.5);
        XWPFRun run = paragraph.createRun();

        run.addBreak();
        run.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, "LineChart.jpeg", Units.toEMU(400), Units.toEMU(280));
        OutputStream os1=new FileOutputStream(new File(wpath));
        doc.write(os1);
    }
    public static void  setTheme(){
        StandardChartTheme standardChartTheme=new StandardChartTheme("CN");
        //设置标题字体jfree
        standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20));
        //设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋书",Font.PLAIN,15));
        //设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋书",Font.PLAIN,15));
        //应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);
    }
}