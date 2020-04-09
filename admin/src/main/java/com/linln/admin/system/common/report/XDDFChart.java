package com.linln.admin.system.common.report;

import com.linln.modules.system.domain.MeasuringSpotData;
import com.linln.modules.system.page.ReportDataInfo;
import com.linln.modules.system.page.ReportDataPage;
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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.util.ExportUtils;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XDDFChart {
    public static void main(String[] args) throws IOException, InvalidFormatException {
        //设置JFreeChart主题，不设置会出现中文乱码情况
        setTheme();
        //XY折线图演示
        /*XYSeriesLineTest(listMap);*/
    }

    public static InputStream XYSeriesLineTest(Map<String, List<ReportDataInfo>> listMap) {
        setTheme();

        XWPFDocument doc = new XWPFDocument();
        InputStream is = null;


        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
        Iterator<Map.Entry<String, List<ReportDataInfo>>> iterator = listMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<ReportDataInfo>> next = iterator.next();
            for (ReportDataInfo measuringSpotData : next.getValue()) {
                mDataset.addValue(measuringSpotData.getValue(), next.getKey(), measuringSpotData.getDate().substring(0,10));
            }

        }
        try {
            //生成图形
            JFreeChart mChart = ChartFactory.createLineChart(
                    "折线图",//图名字
                    "",//横坐标
                    "测点值",//纵坐标
                    mDataset,//数据集
                    PlotOrientation.VERTICAL,
                    true, // 显示图例
                    true, // 采用标准生成器
                    false);// 是否生成超链接

            // 设置面板字体
            Font labelFont = new Font("SansSerif", Font.TRUETYPE_FONT, 12);

            mChart.setBackgroundPaint(Color.WHITE);

            CategoryPlot categoryplot = (CategoryPlot) mChart.getPlot();
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

            String path = "D:\\barChart.jpeg";
            String wPath = "D:\\arChart.docx";
            File chartFile = new File(path);


            if (chartFile.exists()) chartFile.delete();
            ExportUtils.writeAsJPEG(mChart, 500, 320, chartFile);
            is = new FileInputStream(chartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return is;

    }

    //保存图形到word文档中，这里是先生成image文件，然后又用输入流将文件导入，感觉很傻，
    //如果您有更好的方法，请在评论区留言
    private static void SaveDoc(String wpath, String path, JFreeChart chart, XWPFDocument doc) throws IOException, InvalidFormatException {
        File chartFile = new File(path);
        if (chartFile.exists()) chartFile.delete();
        ExportUtils.writeAsJPEG(chart, 600, 600, chartFile);
        InputStream is = new FileInputStream(chartFile);

        XWPFParagraph paragraph = doc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setSpacingBetween(1.5);
        XWPFRun run = paragraph.createRun();

        run.addBreak();
        run.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, "LineChart.jpeg", Units.toEMU(600), Units.toEMU(600));
        OutputStream os1 = new FileOutputStream(new File(wpath));
        doc.write(os1);
    }

    public static void setTheme() {
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        //设置标题字体jfree
        standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
        //设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
        //设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
        //应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);
    }
}