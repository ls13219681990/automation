package com.linln.admin.system.common.report;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Iterator;


import fr.opensagres.xdocreport.core.utils.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import com.google.common.base.Strings;

/**
 * poi操作word向word中写入复杂的表格（合并行、合并列）
 * 
 * @author Christmas_G
 *
 */
public class HtmlToElement {

	public static void main(String[] args) throws Exception {
		String tableContent = "<table style=\"width: 600px;margin:auto\" cellspacing=\"0\" cellpadding=\"3\" border=\"1\"><tbody><tr align=\"center\"><td colspan=\"1\">工程名称</td><td colspan=\"7\">成都轨道交通8号线一期工程永丰站～芳草街站盾构区间</td></tr><tr align=\"center\"><td colspan=\"1\">施工单位</td><td colspan=\"3\">北京施工单位</td><td colspan=\"2\">检测项目</td><td colspan=\"2\"><font style=\"color:red;\">分层沉降(右线)</font></td></tr><tr align=\"center\"><td colspan=\"1\">监理单位</td><td colspan=\"3\">北京监理单位</td><td colspan=\"2\">上次观测日期</td><td colspan=\"2\">2019-12-06 15:22:08</td> </tr><tr align=\"center\"><td colspan=\"1\">仪器规格</td><td colspan=\"3\"></td> <td colspan=\"2\">本次观测日期</td><td colspan=\"2\">2019-12-11 10:03:27</td></tr><tr align=\"center\"><td rowspan=\"2\">里程</td><td rowspan=\"2\">测点编号</td><td>初始测试值</td><td>上次测试值</td><td>本次测试值</td><td>变形量</td><td>累计值</td><td rowspan=\"2\">初测日期</td></tr>\n" +
				"<tr align=\"center\"><td>(mm)</td><td>(mm)</td><td>(mm)</td><td>(mm)</td><td>(mm)</td></tr><tr align=\"center\"><td rowspan=\"1\" contenteditable=\"true\">K12+000</td><td contenteditable=\"true\">fccj</td><td contenteditable=\"true\">null</td><td contenteditable=\"true\">0</td><td contenteditable=\"true\">2</td><td contenteditable=\"true\">2</td><td contenteditable=\"true\">2</td><td rowspan=\"1\" contenteditable=\"true\">2019-11-06</td></tr><tr align=\"center\"><td colspan=\"5\">最大值</td><td><font style=\"color:red;\">2</font></td><td><font style=\"color:red;\">2</font></td><td></td></tr> <tr><td colspan=\"8\">备注：1.“+”值表示测点隆起，“-”值表示测点下沉，“/”表示测点被挡或破坏。</td> </tr> <tr align=\"center\"><td colspan=\"8\"><input type=\"file\" name=\"file\" id=\"file1\" accept=\"image/*\" onchange=\"imgChange(this);\"> <!--文件上传选择按钮--> <img id=\"imghead1\" src=\"\"> <!--图片显示位置--> </td></tr></tbody></table>";
		Element node = supplementTable(tableContent);

		XWPFDocument set = setTable(node);
		OutputStream stream = new FileOutputStream("D:\\cc.docx");
		set.write(stream);
		set.close();
	}

	/**
	 * 向word中写表格
	 * 
	 * @author Christmas_G
	 * @param element
	 * @return
	 * @throws Exception
	 */
	public static XWPFDocument setTable(Element element) throws Exception {
		Document tableDoc = element.ownerDocument();

		Elements trList = tableDoc.getElementsByTag("tr");
		Elements tdList = trList.first().getElementsByTag("td");

		XWPFDocument document = new XWPFDocument();
		XWPFTable table = document.createTable(trList.size(), tdList.size());

		boolean[][] colspanFlag = new boolean[trList.size()][tdList.size()];
		boolean[][] rowspanFlag = new boolean[trList.size()][tdList.size()];
		for (int row = 0; row < trList.size(); row++) {
			Element tr = trList.get(row);
			Elements tds = tr.getElementsByTag("td");

			for (int col = 0; col < tds.size(); col++) {
				Element td = tds.get(col);

				String colspan = td.attr("colspan");
				String rowspan = td.attr("rowspan");

				String align = td.attr("align");
				String widthStyle = td.attr("width");
				String style = td.attr("style");

				// 记录需要合并的列
				if (!StringUtils.isEmpty(colspan)) {
					int colCount = Integer.parseInt(colspan);
					for (int i = 0; i < colCount - 1; i++) {
						colspanFlag[row][col + i + 1] = true;
					}
				}
				// 记录需要合并的行
				if (!StringUtils.isEmpty(rowspan)) {
					int rowCount = Integer.parseInt(rowspan);
					for (int i = 0; i < rowCount - 1; i++) {
						rowspanFlag[row + i + 1][col] = true;
					}
				}
				// 处理合并
				XWPFTableCell cell = table.getRow(row).getCell(col);
				if (colspanFlag[row][col]) {
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

				XWPFParagraph paragraph = null;
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
				cell.setVerticalAlignment(XWPFVertAlign.CENTER);

				/*// 设置没有边框
				if (!style.contains("border:")) {
					setNotBorder(cell);
				}*/

				XWPFRun run = paragraph.createRun();
				run.setText(td.text());
			}
		}
		return document;
	}

	/**
	 * 设置行合并属性
	 * 
	 * @author Christmas_G
	 * @date 2019-05-31 14:08:02
	 * @param tableCell
	 * @param mergeVlaue
	 */
	public static void setRowMerge(XWPFTableCell tableCell, STMerge.Enum mergeVlaue) {
		CTTc ctTc = tableCell.getCTTc();
		CTTcPr cpr = ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();
		CTVMerge merge = cpr.isSetVMerge() ? cpr.getVMerge() : cpr.addNewVMerge();
		merge.setVal(mergeVlaue);
	}

	/**
	 * 设置列合并属性
	 * 
	 * @author Christmas_G
	 * @date 2019-05-31 14:07:50
	 * @param tableCell
	 * @param mergeVlaue
	 */
	public static void setColMerge(XWPFTableCell tableCell, STMerge.Enum mergeVlaue) {
		CTTc ctTc = tableCell.getCTTc();
		CTTcPr cpr = ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();
		CTHMerge merge = cpr.isSetHMerge() ? cpr.getHMerge() : cpr.addNewHMerge();
		merge.setVal(mergeVlaue);
	}

	/**
	 * 补全表格
	 * 
	 * @author Christmas_G
	 * @date 2019-05-31 13:32:52
	 * @param tableHtml
	 * @return
	 */
	public static Element supplementTable(String tableHtml) {
		Document tableDoc = Jsoup.parse(tableHtml);
		Elements trels = tableDoc.getElementsByTag("tr");
		// 补全合并的列
		supplementMergedColumns(trels);
		// 补全合并的行
		supplementMergedRows(trels);
		return tableDoc.getElementsByTag("table").first();
	}

	/**
	 * 补全合并的列
	 * 
	 * @author Christmas_G
	 * @date 2019-05-31 11:57:36
	 * @param trels
	 */
	public static void supplementMergedColumns(Elements trels) {
		// 所有tr
		Iterator<Element> trelIter = trels.iterator();
		while (trelIter.hasNext()) {
			Element trel = trelIter.next();
			// 获取所有td
			Elements tdels = trel.getElementsByTag("td");
			Iterator<Element> tdelIter = tdels.iterator();
			while (tdelIter.hasNext()) {
				Element tdel = tdelIter.next();
				// 删除样式
				tdel.removeAttr("class");
				// 取到合并的列数量
				String colspanIndex = tdel.attr("colspan");
				if (StringUtils.isEmpty(colspanIndex)) {
					continue;
				}
				Integer colspanVal = Integer.parseInt(colspanIndex);
				for (int i = 1; i < colspanVal; i++) {
					trel.appendElement("td");
				}
			}
		}
	}

	/**
	 * 补全合并的行（调用此方法前 需要调用 “补全合并的列”方法）
	 * 
	 * @author Christmas_G
	 * @date 2019-05-31 11:57:47
	 * @param trels
	 */
	public static void supplementMergedRows(Elements trels) {
		// 获取最大的列
		int tdSize = 0;
		Iterator<Element> iterator = trels.iterator();
		while (iterator.hasNext()) {
			Element element = iterator.next();
			int size = element.getElementsByTag("td").size();
			if (size > tdSize) {
				tdSize = size;
			}
		}

		for (int i = 0; i < trels.size(); i++) {
			Element currTr = trels.get(i);
			int currTrTds = currTr.getElementsByTag("td").size();
			if (currTrTds == tdSize) {
				continue;
			}

			int count = tdSize - currTrTds;
			for (int j = 0; j < count; j++) {
				currTr.appendElement("td");
			}
		}
	}

	/**
	 * 设置列宽
	 * 
	 * @author Christmas_G
	 * @date 2019-06-28 11:30:22
	 * @param cell
	 * @param width
	 */
	public static void setWidth(XWPFTableCell cell, int width) {
		CTTblWidth ctTblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
		// 此处乘以20是我以最接近A4上创建表格的宽度手动设置的
		// 目前没有找到将px转换为word里单位的方式
		ctTblWidth.setW(BigInteger.valueOf(width).multiply(BigInteger.valueOf(20)));
		ctTblWidth.setType(STTblWidth.DXA);
	}

	/**
	 * 设置表格为没有边框线
	 * 
	 * @author Christmas_G
	 * @date 2019-06-28 11:33:48
	 * @param cell
	 */
	public static void setNotBorder(XWPFTableCell cell) {
		CTTcBorders cTTcBorders = cell.getCTTc().getTcPr().addNewTcBorders();
		CTBorder clBorder = cTTcBorders.addNewLeft();
		clBorder.setVal(STBorder.Enum.forString("none"));
		clBorder.setShadow(STOnOff.ON);
		CTBorder crBorder = cTTcBorders.addNewRight();
		crBorder.setVal(STBorder.Enum.forString("none"));
		crBorder.setShadow(STOnOff.ON);
		CTBorder cbBorder = cTTcBorders.addNewBottom();
		cbBorder.setVal(STBorder.Enum.forString("none"));
		cbBorder.setShadow(STOnOff.ON);
		CTBorder ctBorder = cTTcBorders.addNewTop();
		ctBorder.setVal(STBorder.Enum.forString("none"));
		ctBorder.setShadow(STOnOff.ON);
	}

}

