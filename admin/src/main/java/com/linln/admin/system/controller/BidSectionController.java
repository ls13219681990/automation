package com.linln.admin.system.controller;

import com.linln.admin.system.common.CommonMethod;
import com.linln.common.constant.AdminConst;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.Common.EntityUtils;
import com.linln.modules.system.domain.*;
import com.linln.modules.system.page.BidSectionMeasuringSpotDataPage;
import com.linln.modules.system.page.TablePage;
import com.linln.modules.system.service.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.linln.admin.system.common.CommonMethod.addHour;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/bidSection")
public class BidSectionController {


    @Autowired
    private BidSectionService bidSectionService;

    @Autowired
    private WorkSpotService workSpotService;

    @Autowired
    private MeasuringSpotDataService measuringSpotDataService;


    @Autowired
    private MenuService menuService;

    @Autowired
    private LineInfoService lineInfoService;


    /**
     * 跳转到列表页面
     */
    @GetMapping("/index")
    //@RequiresPermissions("system:bidSection:index")
    public String index(Model model, String nodeName, WorkSpot workSpot) {
        if (nodeName != null && !"".equals(nodeName)) {
            BidSection bidSection = bidSectionService.findByName(nodeName);
            workSpot.setBidSectionId(bidSection.getId());
            // 创建匹配器，进行动态查询匹配
            ExampleMatcher matcher = ExampleMatcher.matching().
                    withMatcher("bidSectionId", ExampleMatcher.GenericPropertyMatchers.contains());
            Example<WorkSpot> example = Example.of(workSpot, matcher);
            Page<WorkSpot> list = workSpotService.getPageList(example);

            // 封装数据
            model.addAttribute("list", list.getContent());
            model.addAttribute("page", list);
            model.addAttribute("bidSection", bidSection);
        }

        return "/system/bidSection/index";
    }

    /**
     * 跳转到列表页面
     */
    @GetMapping("/indexManage")
    //@RequiresPermissions("system:bidSection:indexManage")
    public String indexManage(Model model, WorkSpot workSpot, String nodeName) {
        if (nodeName != null && !"".equals(nodeName)) {
            BidSection bidSection = bidSectionService.findByName(nodeName);
            workSpot.setBidSectionId(bidSection.getId());
            // 创建匹配器，进行动态查询匹配
            ExampleMatcher matcher = ExampleMatcher.matching().
                    withMatcher("bidSectionId", ExampleMatcher.GenericPropertyMatchers.contains());
            Example<WorkSpot> example = Example.of(workSpot, matcher);
            Page<WorkSpot> list = workSpotService.getPageList(example);
            // 封装数据
            model.addAttribute("list", list.getContent());
            model.addAttribute("page", list);
            model.addAttribute("bidSection", bidSection);
        }
        return "/system/bidSection/indexManage";
    }

    @GetMapping("/userBidSectionData")
    @ResponseBody
    public TablePage lineInfoAllData(Long lineInfoId, Integer page, Integer limit, Model model) {


        //查出線路對應的标段
        Page<BidSection> bidSectionPage = bidSectionService.findByLineInfoId(lineInfoId, page, limit);


        //用户对应的所有标段
        List<BidSection> userBidSectionData = new ArrayList<>();


        //获取所有角色对应的标段
        List<BidSection> RoleBidSectionData = new ArrayList<>();
        User user = ShiroUtil.getSubject();
        List<Menu> list = new ArrayList<>();
        if (user.getId().equals(AdminConst.ADMIN_ID)) {
            userBidSectionData = bidSectionService.findByLineInfoId(lineInfoId, page, limit).getContent();
        } else {
            list = menuService.findEngineeringMenu(user.getId());
            if (list.size() > 0 && !CollectionUtils.isEmpty(list)) {
                for (Menu menu : list) {
                    BidSection bidSection = bidSectionService.findByName(menu.getTitle());
                    if (bidSection != null) {
                        RoleBidSectionData.add(bidSection);
                    }

                }
            }
            //匹配得出最终用户的标段
            for (BidSection bidSection : bidSectionPage.getContent()) {
                for (BidSection userBidSection : RoleBidSectionData) {
                    if (bidSection.getId() == userBidSection.getId()) {
                        userBidSectionData.add(bidSection);
                    }
                }
            }
        }


        List data = CommonMethod.removeDuplicateWithOrder(userBidSectionData);

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(Integer.valueOf(String.valueOf(bidSectionPage.getTotalElements())));
        tablePage.setData(data);
        return tablePage;

    }


    /**
     * 查询线路下面的所有标段
     *
     * @param lineInfoId 线路ID
     * @return
     */
    @GetMapping("/findByLineId")
    @ResponseBody
    public TablePage findByLineId(Long lineInfoId) {

        List<BidSection> userBidSectionData = new ArrayList<>();
        User user = ShiroUtil.getSubject();
        List<Menu> list = new ArrayList<>();

        if (lineInfoId != 0) {
            //查出線路對應的标段
            List<BidSection> bidSections = bidSectionService.findByLineInfoId(lineInfoId);
            //用户对应的所有标段

            //获取所有角色对应的标段
            List<BidSection> RoleBidSectionData = new ArrayList<>();

            if (user.getId().equals(AdminConst.ADMIN_ID)) {
                userBidSectionData = bidSectionService.findByLineInfoId(lineInfoId);
            } else {
                list = menuService.findEngineeringMenu(user.getId());
                if (list.size() > 0 && !CollectionUtils.isEmpty(list)) {
                    for (Menu menu : list) {
                        BidSection bidSection = bidSectionService.findByName(menu.getTitle());
                        if (bidSection != null) {
                            RoleBidSectionData.add(bidSection);
                        }

                    }
                }

                //匹配得出最终用户的标段
                for (BidSection bidSection : bidSections) {
                    for (BidSection userBidSection : RoleBidSectionData) {
                        if (bidSection.getId() == userBidSection.getId()) {
                            userBidSectionData.add(bidSection);
                        }
                    }
                }


            }
        } else {
            if (user.getId().equals(AdminConst.ADMIN_ID)) {
                userBidSectionData = bidSectionService.findAll();
            } else {
                list = menuService.findEngineeringMenu(user.getId());
                if (list.size() > 0 && !CollectionUtils.isEmpty(list)) {
                    for (Menu menu : list) {
                        BidSection bidSection = bidSectionService.findByName(menu.getTitle());
                        if (bidSection != null) {
                            userBidSectionData.add(bidSection);
                        }

                    }
                }
            }
        }
        BidSection bidSection = new BidSection();
        bidSection.setName("全部");
        bidSection.setId(0L);
        userBidSectionData.add(bidSection);
        List data = CommonMethod.removeDuplicateWithOrder(userBidSectionData);

        Collections.sort(data, new Comparator<BidSection>() {
            @Override
            public int compare(BidSection o1, BidSection o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(userBidSectionData.size());
        tablePage.setData(data);
        return tablePage;

    }


    /**
     * 查询角色对应的所有标段
     *
     * @return
     */
    @GetMapping("/findByLineIdSelect")
    @ResponseBody
    public TablePage findByLineIdSelect() {

        List<BidSection> userBidSectionData = new ArrayList<>();
        List<Menu> list = new ArrayList<>();

        User user = ShiroUtil.getSubject();
        if (user.getId().equals(AdminConst.ADMIN_ID)) {
            userBidSectionData = bidSectionService.findAll();
        } else {
            list = menuService.findEngineeringMenu(user.getId());
            if (list.size() > 0 && !CollectionUtils.isEmpty(list)) {
                for (Menu menu : list) {
                    BidSection bidSection = bidSectionService.findByName(menu.getTitle());
                    if (bidSection != null) {
                        userBidSectionData.add(bidSection);
                    }

                }
            }
        }


        BidSection bidSection = new BidSection();
        bidSection.setName("全部");
        bidSection.setId(0L);
        userBidSectionData.add(bidSection);
        List data = CommonMethod.removeDuplicateWithOrder(userBidSectionData);
        Collections.sort(data, new Comparator<BidSection>() {

            @Override
            public int compare(BidSection o1, BidSection o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(userBidSectionData.size());
        tablePage.setData(data);
        return tablePage;

    }


    @GetMapping("/data")
    @RequiresPermissions("system:bidSection:data")
    @ResponseBody
    public TablePage data(String nodeName) {
        List<BidSectionMeasuringSpotDataPage> sensorData = new ArrayList<>();
        if (nodeName != null && !"".equals(nodeName)) {
            //List<LineMeasuringSpotDataPage> lineMeasuringSpotDataPages = lineInfoService.findByNameGetMeasuringSpotData(nodeName);
            List<Object[]> byNameGetMeasuringSpotData = bidSectionService.findByNameGetMeasuringSpotData(nodeName);
            Map<String, List<BidSectionMeasuringSpotDataPage>> sensorDataGroupMap = new LinkedHashMap<>();
            sensorData = EntityUtils.castEntity(byNameGetMeasuringSpotData, BidSectionMeasuringSpotDataPage.class, new BidSectionMeasuringSpotDataPage());

        }
        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(sensorData.size());
        tablePage.setData(sensorData);
        return tablePage;


    }


    /**
     * 跳转到列表页面
     */
    @GetMapping("/info")
    @RequiresPermissions("system:bidSection:info")
    public String info(Model model, String nodeName) {
        if (nodeName != null && !"".equals(nodeName)) {
            BidSection bidSection = bidSectionService.findByName(nodeName);
            model.addAttribute("nodeData", bidSection);
        }
        return "/system/bidSection/info";
    }


    @PostMapping("/updatebidSection")
    //@RequiresPermissions("system:bidSection:updatebidSection")
    @ResponseBody
    public ResultVo updateLineInfo(BidSection bidSection) {


        BidSection used = bidSectionService.getById(bidSection.getId());
        if (!used.getName().equals(bidSection.getName())) {
            List<Menu> menuList = menuService.findAllByTitleContaining(used.getName());
            for (Menu menu : menuList) {
                if (menu.getUrl().indexOf("index") != -1) {
                    menu.setUrl("/system/bidSection/index?nodeName=" + bidSection.getName());
                } else {
                    menu.setUrl("/system/bidSection/indexManage?nodeName=" + bidSection.getName());
                }

                menuService.save(menu);
            }
        }
        if (!used.getName().equals(bidSection.getName())) {
            List<Menu> menuList = menuService.findAllByTitleContaining(used.getName());
            for (Menu menu : menuList) {
                menu.setTitle(bidSection.getName());
            }
            if (menuList.size() > 0 && !CollectionUtils.isEmpty(menuList)) {
                menuService.updateBatch(menuList);
            }
        }
        bidSectionService.update(bidSection);
        return ResultVoUtil.success("修改成功");

    }


    /**
     * 跳转到列表页面
     */
    @GetMapping("/toExportExcel")
    //@RequiresPermissions("system:bidSection:index")
    public String toExportExcel(Model model, Long bidSectionId) {
        // 封装数据
        model.addAttribute("bidSectionId", bidSectionId);
        return "/system/bidSection/exportExcel";
    }


    @RequestMapping("/exportExcel")
    public void export(HttpServletResponse response, Long bidSectionId, String startTime, String endTime) {

        BidSection bidSection = bidSectionService.getById(bidSectionId);
        List<Object[]> byNameGetMeasuringSpotData = bidSectionService.findByNameGetMeasuringSpotData(bidSection.getName(), startTime, endTime + " 23:59:59");
        Map<String, List<BidSectionMeasuringSpotDataPage>> sensorDataGroupMap = new LinkedHashMap<>();

        List<BidSectionMeasuringSpotDataPage> measuringSpotData = EntityUtils.castEntity(byNameGetMeasuringSpotData, BidSectionMeasuringSpotDataPage.class, new BidSectionMeasuringSpotDataPage());


        //从数据库查询出数据

        // 创建excel
        HSSFWorkbook wk = new HSSFWorkbook();
        // 创建一张工作表
        HSSFSheet sheet = wk.createSheet("工点数据表");
        // 设置工作表中的1-3列的宽度
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);

        //创建第一行
        HSSFRow row1 = sheet.createRow(0);
        // 创建第一行的第一个单元格
        // 向单元格写值
        HSSFCell cell = row1.createCell(0);
        cell.setCellValue("工点数据表");
        //合并单元格CellRangeAddress构造参数依次表示起始行，截止行，起始列，截至列。
        //0表示 第一行第一列
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        //创建第二行
        HSSFRow row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue("工点名称");
        row2.createCell(1).setCellValue("测点名称");
        row2.createCell(2).setCellValue("测点初值");
        row2.createCell(3).setCellValue("本次测值");
        row2.createCell(4).setCellValue("累加值");

        // 创建第一行
        for (int i = 0; i < measuringSpotData.size(); i++) {
            //创建行    一条数据一行
            HSSFRow row = sheet.createRow(i + 2);
            row.createCell(0).setCellValue(measuringSpotData.get(i).getWorkSpotName());
            row.createCell(1).setCellValue(measuringSpotData.get(i).getMeasuringSpotName());
            row.createCell(2).setCellValue(measuringSpotData.get(i).getInitial_value());
            row.createCell(3).setCellValue(measuringSpotData.get(i).getMeasuringSpotValue());
            row.createCell(4).setCellValue(measuringSpotData.get(i).getMeasuringSpotAccumulationValue());
        }
        try {
            /**
             * 弹出下载选择路径框
             */
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(bidSection.getName().getBytes("gb2312"), "ISO8859-1") + ":" + startTime + "-" + endTime + ".xls");
            response.flushBuffer();
            wk.write(response.getOutputStream());
            // wk.write(new FileOutputStream(new File("D://daochu")));
            //wk.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @GetMapping("/title")
    //@RequiresPermissions("system:bidSection:updatebidSection")
    @ResponseBody
    public ResultVo title(Long id) {


        String title = "";

        BidSection bidSection = bidSectionService.getById(id);
        LineInfo lineInfo = new LineInfo();

        if (bidSection != null) {
            lineInfo = lineInfoService.getById(bidSection.getLineInfoId());
            title = lineInfo.getName() + "  -  " + bidSection.getName();
        }


        return ResultVoUtil.success(title);

    }


}









/*

 */
/**
 * 跳转到线路编辑页面
 *//*

    @GetMapping("/lineInfoedit/{id}")
    @RequiresPermissions("system:menu:edit")
    public String tolineInfoEdit(@PathVariable("id") Menu menu, Model model) {
        Menu pMenu = menuService.getById(menu.getPid());
        model.addAttribute("menu", menu);
        model.addAttribute("lineMenu", pMenu);
        return "/system/addLineInfo/add";
    }
*/














