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
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;

import org.jfree.chart.util.ExportUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
 
import java.awt.*;
import java.io.*;
 
public class XDDFChartTest {
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
        JFreeChart chart = ChartFactory.createLineChart(
                "折线图",//图名字
                "年份",//横坐标
                "数量",//纵坐标
                mDataset,//数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 采用标准生成器

                false);// 是否生成超链接


        chart.setTextAntiAlias(false);
        chart.setBackgroundPaint(Color.WHITE);
        // 设置图标题的字体重新设置title

        // 设置面板字体
        Font labelFont = new Font("SansSerif", Font.TRUETYPE_FONT, 12);

        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
        // x轴 // 分类轴网格是否可见
        categoryplot.setDomainGridlinesVisible(true);
        // y轴 //数据轴网格是否可见
        categoryplot.setRangeGridlinesVisible(true);

     /*   categoryplot.setRangeGridlinePaint(Color.WHITE);// 虚线色彩

        categoryplot.setDomainGridlinePaint(Color.WHITE);// 虚线色彩
*/
        categoryplot.setBackgroundPaint(Color.lightGray);

        // 设置轴和面板之间的距离
        // categoryplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

        CategoryAxis domainAxis = categoryplot.getDomainAxis();

        domainAxis.setLabelFont(labelFont);// 轴标题
        domainAxis.setTickLabelFont(labelFont);// 轴数值

        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // 横轴上的
        // Lable
        // 45度倾斜
        // 设置距离图片左端距离
        domainAxis.setLowerMargin(0.0);
        // 设置距离图片右端距离
        domainAxis.setUpperMargin(0.0);

        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        numberaxis.setAutoRangeIncludesZero(true);

        // 获得renderer 注意这里是下嗍造型到lineandshaperenderer！！
        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot
                .getRenderer();
        lineandshaperenderer.setBaseShapesVisible(true); // series 点（即数据点）可见
        lineandshaperenderer.setBaseLinesVisible(true); // series 点（即数据点）间有连线可见


        String path="D:\\barChart.jpeg";
        String wPath="D:\\arChart.docx";
        SaveDoc(wPath,path,chart,doc);
 
    }

    //保存图形到word文档中，这里是先生成image文件，然后又用输入流将文件导入，感觉很傻，
    //如果您有更好的方法，请在评论区留言
    private static void SaveDoc(String wpath, String path, JFreeChart chart, XWPFDocument doc) throws IOException, InvalidFormatException {
        File chartFile = new File(path);
        if(chartFile.exists())chartFile.delete();
        ExportUtils.writeAsJPEG(chart,400,400,chartFile);
        InputStream is=new FileInputStream(chartFile);
 
        XWPFParagraph paragraph = doc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setSpacingBetween(1.5);
        XWPFRun run = paragraph.createRun();
 
        run.addBreak();
        run.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, "LineChart.jpeg", Units.toEMU(400), Units.toEMU(400));
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