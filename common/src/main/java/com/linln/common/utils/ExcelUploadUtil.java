package com.linln.common.utils;
 
import com.sun.media.sound.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/**
 * @Description: Excel文件解析工具类
 * @Author: zhaowang
 * @Date: 2018/9/25 11:16
 * @Version: 1.0
 */
@Slf4j
@Component
public class ExcelUploadUtil {
 
 
    /**
     *
     * @param file 上传的文件
     * @param path 文件保存路径
     * @return
     */
    // TODO 获取合并单元格的值
    public  Map<Integer,List<List<? super Object>>> upload(MultipartFile file,String path) {
        //创建读取excel的类(区分excel2003和2007文件)
        Workbook workbook = createWorkBook(file,path);
        //excel每一行看做一个List,作为value;sheet页数作为key
        Map<Integer,List<List<? super Object>>> map = new HashMap<>();
        // 得到sheet页
        for(int i = 0;i<workbook.getNumberOfSheets();i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 得到Excel的行数
            int totalRows = sheet.getPhysicalNumberOfRows();
            // 得到Excel的列数(前提是有行数)
            int totalCells = 0;
            if (totalRows > 1 && sheet.getRow(0) != null) {
                totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
            }
            // 循环Excel行数
            List<List<? super Object>> list = new ArrayList<>();
            for (int r = 1; r < totalRows; r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                // 循环Excel的列
                if (row != null) {
                    List<? super Object> valueList = new ArrayList<>();
                    for (int c = 0; c < totalCells; c++) {
                        Cell cell = row.getCell(c);
                        DecimalFormat df = new DecimalFormat("0");
                        try {
                            if (null != cell) {
                                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    String value = df.format(cell.getNumericCellValue());
                                    valueList.add(value);
                                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    if (!StringUtils.isEmpty(cell.getStringCellValue())) {
                                        valueList.add(cell.getStringCellValue());
                                    }
                                } else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
                                    valueList.add("");
                                } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                                    valueList.add(cell.getBooleanCellValue());
                                } else if(cell.getCellType() == Cell.CELL_TYPE_ERROR) {
                                    valueList.add("非法字符");
                                } else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                                    valueList.add(cell.getCellFormula());
                                };
                            }
                        } catch (Exception e) {
                            log.error("\n==========解析Excel单元格异常==========", e);
                        }
                    }
                    list.add(valueList);
                }
            }
            map.put(i, list);
        }
        return map;
    }
 
    public  Workbook createWorkBook(MultipartFile file, String path) {
        multipartToFile(file,path);
        File f = createNewFile(file,path);
        Workbook workbook = null;
        try {
            InputStream is = new FileInputStream(f);
            //workbook = WorkbookFactory.create(is);
            is.close();
        } catch ( IOException e) {
            log.error("\n==========文件流转换为Workbook对象异常============", e);
        }
        return workbook;
    }
 
    public  void multipartToFile(MultipartFile multfile, String path) {
        File file = createNewFile(multfile,path);
        try {
            multfile.transferTo(file);
        } catch (IOException e) {
            log.error("\n上传的文件保存失败");
        }
    }
 
    public  File createNewFile(MultipartFile multfile,String path) {
        String fileName = multfile.getOriginalFilename();
        File file = new File(path + fileName);
        File parentFile = file.getParentFile();
        if(!parentFile.exists()) parentFile.mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            log.error("\n新文件创建失败");
        }
        return file;
    }
}
 