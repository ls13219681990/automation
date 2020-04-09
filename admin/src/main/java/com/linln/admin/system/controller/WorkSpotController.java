package com.linln.admin.system.controller;

import com.linln.admin.system.common.CommonMethod;
import com.linln.common.constant.AdminConst;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.component.shiro.ShiroUtil;
import com.linln.component.thymeleaf.utility.DictUtil;
import com.linln.modules.system.Common.EntityUtils;
import com.linln.modules.system.domain.*;
import com.linln.modules.system.page.*;
import com.linln.modules.system.page.WorkSpotMeasuringSpotDataPage;
import com.linln.modules.system.service.*;
import fr.opensagres.xdocreport.core.utils.StringUtils;
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
import java.text.SimpleDateFormat;
import java.util.*;

import static com.linln.admin.system.common.CommonMethod.addHour;
import static com.linln.admin.system.common.CommonMethod.removeDuplicateWithOrderNoVoid;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/workSpot")
public class WorkSpotController {


    @Autowired
    private WorkSpotService workSpotService;


    @Autowired
    private AcquisitionInstrumentService acquisitionService;


    @Autowired
    private MeasuringSpotSensorService measuringSpotSensorService;

    @Autowired
    private MeasuringSpotService measuringSpotService;


    @Autowired
    private MenuService menuService;

    @Autowired
    private LineInfoService lineInfoService;

    @Autowired
    private BidSectionService bidSectionService;

    @Autowired
    private MeasuringSpotDataService measuringSpotDataService;

    @Autowired
    private ReportService reportService;

    /**
     * 跳转到列表页面
     */
    @GetMapping("/index")
    //@RequiresPermissions("system:workSpot:index")
    public String index(Model model, String nodeName) {

        WorkSpot workSpot = workSpotService.findByName(nodeName);
        if(workSpot != null){
            //查询工点下面所有测点里面 最早的一条记录  用于时间限制
            MeasuringSpotData measuringSpotDataMin = measuringSpotDataService.findByworkSpotIdMeasuringDataDateMin(workSpot.getId());
            //查询工点下面所有测点里面 最晚的一条记录  用于时间限制
            MeasuringSpotData measuringDataDateMax = measuringSpotDataService.findByworkSpotIdMeasuringDataDateMax(workSpot.getId());

            model.addAttribute("workData", workSpot);
            if (measuringSpotDataMin != null) {
                model.addAttribute("min", measuringSpotDataMin.getReceiveTime().substring(0, 10));
            }
            if (measuringDataDateMax != null) {
                model.addAttribute("max", measuringDataDateMax.getReceiveTime().substring(0, 10));
            }
        }
        return "/system/workSpot/index";
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:workSpot:detail")

    public String toDetail(@PathVariable("id") WorkSpot workSpot, Model model) {

        model.addAttribute("list", workSpot);
        return "/system/workSpot/detail";
    }
    /**
     * 跳转生成报告页面
     */
    @GetMapping("/editReport")
    @RequiresPermissions("system:workSpot:detail")

    public String editReport(Long  id, Model model,Long dateType) {

        WorkSpot workSpot = workSpotService.findById(id);
        model.addAttribute("workSpot", workSpot);
        model.addAttribute("dateType", dateType);
        return "/system/workSpot/editReport";
    }
    /**
     * 列表页面
     */
    @GetMapping("/indexManage")
    //@RequiresPermissions("system:workSpot:indexManage")
    public String index(Model model, @EntityParam MeasuringSpot measuringSpot, String nodeName) {


        WorkSpot nodeWorkSpot = workSpotService.findByName(nodeName);

        measuringSpot.setWorkSpotId(nodeWorkSpot.getId());
        // 创建匹配器，进行动态查询匹配
      /*  ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("No", match -> match.contains());*/


        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("workSpotId", ExampleMatcher.GenericPropertyMatchers.contains());

        // 获取采集仪列表
        Example<MeasuringSpot> example = Example.of(measuringSpot, matcher);


        Page<MeasuringSpot> pageList = measuringSpotService.getPageList(example);

        // 封装数据
        model.addAttribute("list", pageList.getContent());
        model.addAttribute("page", pageList);
        model.addAttribute("workSpot", nodeWorkSpot);
        return "/system/workSpot/indexManage";
    }

    @GetMapping("/templateIndex")
    public String templateIndex() {
        return "/system/workSpot/template";
    }

    /**
     * 查询角色对应工点/根据传到标段ID查工点数据
     *
     * @param bidSectionId
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/userWorkSpotData")
    @ResponseBody
    public TablePage lineInfoAllData(Long bidSectionId, Integer page, Integer limit) {
        //用户对应的所有工点
        List<WorkSpot> userWorkSpotData = new ArrayList<>();

        //获取所有角色对应的工点
        List<WorkSpot> RoleWorkSpot = new ArrayList<>();
        User user = ShiroUtil.getSubject();
        List<Menu> list = new ArrayList<>();


        if (bidSectionId != null && bidSectionId != 0) {
            List<WorkSpot> workSpotData = workSpotService.findByBidSectionId(bidSectionId);


            if (user.getId().equals(AdminConst.ADMIN_ID)) {
                userWorkSpotData = workSpotService.findByBidSectionId(bidSectionId);
            } else {
                list = menuService.findEngineeringMenu(user.getId());
                if (list.size() > 0 && !CollectionUtils.isEmpty(list)) {
                    for (Menu menu : list) {
                        WorkSpot workSpot = workSpotService.findByName(menu.getTitle());
                        if (workSpot != null) {
                            RoleWorkSpot.add(workSpot);
                        }

                    }
                }
                //匹配得出最终用户的工点
                for (WorkSpot workSpot : workSpotData) {
                    for (WorkSpot userWorkSpot : RoleWorkSpot) {
                        if (workSpot.getId() == userWorkSpot.getId()) {
                            userWorkSpotData.add(workSpot);
                        }
                    }
                }

            }
        } else {
            if (user.getId().equals(AdminConst.ADMIN_ID)) {
                userWorkSpotData = workSpotService.findAll();
            } else {
                list = menuService.findEngineeringMenu(user.getId());
                if (list.size() > 0 && !CollectionUtils.isEmpty(list)) {
                    for (Menu menu : list) {
                        WorkSpot workSpot = workSpotService.findByName(menu.getTitle());
                        if (workSpot != null) {
                            userWorkSpotData.add(workSpot);
                        }

                    }
                }
            }
        }
        List data = CommonMethod.removeDuplicateWithOrder(userWorkSpotData);

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(data.size());
        tablePage.setData(data);
        return tablePage;

    }



    /**
     * 查询角色对应工点/根据传到标段ID查工点数据
     *
     * @param bidSectionId
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/userWorkSpotDataSelect")
    @ResponseBody
    public TablePage userWorkSpotDataSelect(Long bidSectionId, Integer page, Integer limit) {
        //用户对应的所有工点
        List<WorkSpot> userWorkSpotData = new ArrayList<>();

        //获取所有角色对应的工点
        List<WorkSpot> RoleWorkSpot = new ArrayList<>();
        User user = ShiroUtil.getSubject();
        List<Menu> list = new ArrayList<>();


        if (bidSectionId != null && bidSectionId != 0) {
            List<WorkSpot> workSpotData = workSpotService.findByBidSectionId(bidSectionId);


            if (user.getId().equals(AdminConst.ADMIN_ID)) {
                userWorkSpotData = workSpotService.findByBidSectionId(bidSectionId);
            } else {
                list = menuService.findEngineeringMenu(user.getId());
                if (list.size() > 0 && !CollectionUtils.isEmpty(list)) {
                    for (Menu menu : list) {
                        WorkSpot workSpot = workSpotService.findByName(menu.getTitle());
                        if (workSpot != null) {
                            RoleWorkSpot.add(workSpot);
                        }

                    }
                }
                //匹配得出最终用户的工点
                for (WorkSpot workSpot : workSpotData) {
                    for (WorkSpot userWorkSpot : RoleWorkSpot) {
                        if (workSpot.getId() == userWorkSpot.getId()) {
                            userWorkSpotData.add(workSpot);
                        }
                    }
                }

            }
        } else {
            if (user.getId().equals(AdminConst.ADMIN_ID)) {
                userWorkSpotData = workSpotService.findAll();
            } else {
                list = menuService.findEngineeringMenu(user.getId());
                if (list.size() > 0 && !CollectionUtils.isEmpty(list)) {
                    for (Menu menu : list) {
                        WorkSpot workSpot = workSpotService.findByName(menu.getTitle());
                        if (workSpot != null) {
                            userWorkSpotData.add(workSpot);
                        }

                    }
                }
            }
        }
        WorkSpot workSpot = new WorkSpot();
        workSpot.setName("全部");
        workSpot.setId(0L);
        userWorkSpotData.add(workSpot);

        List data = CommonMethod.removeDuplicateWithOrder(userWorkSpotData);
        Collections.sort(data, new Comparator<WorkSpot>() {

            @Override
            public int compare(WorkSpot o1, WorkSpot o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(data.size());
        tablePage.setData(data);
        return tablePage;

    }



    /**
     *
     */
    @GetMapping("/distributionCollection/{id}")
    @RequiresPermissions("system:workSpot:distributionCollection")
    public String toDetail(@PathVariable("id") Long id, Model model) {

        WorkSpot workSpot = workSpotService.findById(id);

        model.addAttribute("workSpot", workSpot);
        return "/system/workSpot/distributionCollection";
    }


    @GetMapping("/addCollection/{id}")
    //@RequiresPermissions("system:workSpot:distributionCollection")
    public String addCollection(@PathVariable("id") Long id, Model model) {

        WorkSpot ws = workSpotService.findById(id);

        model.addAttribute("workSpot", ws);
        return "/system/workSpot/addCollection";
    }


    /**
     * 绑定采集仪
     *
     * @param id
     * @param acquisitionNo
     * @return
     */
    @RequestMapping("/saveWorkSpotCollection")
    //@RequiresPermissions("system:workSpot:saveWorkSpotCollection")
    @ResponseBody
    public ResultVo saveWorkSpotCollection(Long id, String acquisitionNo) {

        //工点分配采集仪
        AcquisitionInstrument byNo = acquisitionService.findByNo(acquisitionNo);
        byNo.setWorkSpotId(id);
        acquisitionService.saveAndFlush(byNo);

        /*

        model.addAttribute("workSpot", ws);*/
        return ResultVoUtil.SAVE_SUCCESS;
    }


    /**
     * 移除采集仪
     *
     * @param acquisitionNo
     * @return
     */
    @RequestMapping("/unbound")
    //@RequiresPermissions("system:workSpot:unbound")
    @ResponseBody
    public ResultVo unbound(String acquisitionNo) {

        //工点分配采集仪
        AcquisitionInstrument acquisitionInstrument = acquisitionService.findByNo(acquisitionNo);
        acquisitionInstrument.setWorkSpotId(null);
        acquisitionService.saveAndFlush(acquisitionInstrument);
        //同时删除测点传感器信息
        List<MeasuringSpotSensor> measuringSpotSensorList = measuringSpotSensorService.findByAcquisitionId(acquisitionInstrument.getId());
        if (measuringSpotSensorList.size() > 0 && !CollectionUtils.isEmpty(measuringSpotSensorList)) {
            measuringSpotSensorService.deleteBatch(measuringSpotSensorList);
        }


        return ResultVoUtil.success("已解除绑定");
    }


    @GetMapping("/data")
    //@RequiresPermissions("system:workSpot:data")
    @ResponseBody
    public TablePage data(String nodeName) {


        //List<WorkSpotMeasuringSpotDataPage> lineMeasuringSpotDataPages = lineInfoService.findByNameGetMeasuringSpotData(nodeName);
        List<Object[]> byNameGetMeasuringSpotData = workSpotService.findByNameGetMeasuringSpotData(nodeName);
        Map<String, List<WorkSpotMeasuringSpotDataPage>> sensorDataGroupMap = new LinkedHashMap<>();
        List<WorkSpotMeasuringSpotDataPage> sensorData = EntityUtils.castEntity(byNameGetMeasuringSpotData, WorkSpotMeasuringSpotDataPage.class, new WorkSpotMeasuringSpotDataPage());


        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(sensorData.size());
        tablePage.setData(sensorData);
        return tablePage;


    }


    /**
     * 通过工点ID和时间区间进行查询测点数据
     *
     * @param id
     * @param startTime
     * @param endTime
     * @return
     */
    @RequestMapping("/findByWorkSpotIdAndStartDateAndEndDate")
    //@RequiresPermissions("system:workSpot:findWorkSpotAcquisition")
    @ResponseBody
    public TablePage findByWorkSpotIdAndStartDateAndEndDate(Long id, String startTime, String endTime) {

        List<Object[]> workSpotIdAndStartDateAndEndDate = workSpotService.findByWorkSpotIdAndStartTimeAndEndTime(id, startTime, endTime + "23:59:59");
        Map<String, List<WorkSpotMeasuringSpotDataPage>> sensorDataGroupMap = new LinkedHashMap<>();
        List<WorkSpotMeasuringSpotDataPage> sensorData = EntityUtils.castEntity(workSpotIdAndStartDateAndEndDate, WorkSpotMeasuringSpotDataPage.class, new WorkSpotMeasuringSpotDataPage());


        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(sensorData.size());
        tablePage.setData(sensorData);
        return tablePage;


    }


    @GetMapping("/workSpotMeasuringInfo")
    //@RequiresPermissions("system:workSpot:workSpotMeasuringInfo")
    @ResponseBody
    public TablePage workSpotMeasuringInfo(String nodeName, Integer page, Integer limit) {


        WorkSpot workSpot = workSpotService.findByName(nodeName);


        Page<MeasuringSpot> measuringSpotPage = measuringSpotService.findByWorkSpotId(workSpot.getId(), page, limit);
        for (MeasuringSpot measuringSpot : measuringSpotPage) {
            measuringSpot.setDirection(DictUtil.keyValue("DIRECTION", measuringSpot.getDirection()));
            measuringSpot.setMeasuringSpotType(DictUtil.keyValue("MEASUREMENT_ITEMS", measuringSpot.getMeasuringSpotType()));
            measuringSpot.setMeasurePattern(DictUtil.keyValue("MEASUREMENT_MODE", measuringSpot.getMeasurePattern()));
        }

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(Integer.valueOf(String.valueOf(measuringSpotPage.getTotalElements())));
        tablePage.setData(measuringSpotPage.getContent());
        return tablePage;


    }


    @RequestMapping("/findWorkSpotAcquisition")
    //@RequiresPermissions("system:workSpot:findWorkSpotAcquisition")
    @ResponseBody
    public TablePage findWorkSpotAcquisition(Long id) {

        WorkSpot workSpot = workSpotService.findById(id);

        List<AcquisitionInstrument> acquisitionInstrumentList = acquisitionService.findByWorkSpotId(workSpot.getId());

        List<AcquisitionInstrument> acquisitionInstruments = acquisitionService.findByWorkSpotIdIsNull();
        acquisitionInstruments.addAll(acquisitionInstrumentList);

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(acquisitionInstruments.size());
        tablePage.setData(acquisitionInstruments);
        return tablePage;


    }


    @PostMapping("/updateWorkSpot")
    //@RequiresPermissions("system:workSpot:updateWorkSpot")
    @ResponseBody
    public ResultVo updateLineInfo(WorkSpot workSpot) {

        WorkSpot used = workSpotService.findById(workSpot.getId());
        if (!used.getName().equals(workSpot.getName())) {
            List<Menu> menuList = menuService.findAllByTitleContaining(used.getName());
            for (Menu menu : menuList) {
                if (menu.getUrl().indexOf("index") != -1) {
                    menu.setUrl("/system/workSpot/index?nodeName=" + workSpot.getName());
                } else {
                    menu.setUrl("/system/workSpot/indexManage?nodeName=" + workSpot.getName());
                }

                menuService.save(menu);
            }
        }

        if (!used.getName().equals(workSpot.getName())) {
            List<Menu> menuList = menuService.findAllByTitleContaining(used.getName());
            for (Menu menu : menuList) {
                menu.setTitle(workSpot.getName());
            }
            if (menuList.size() > 0 && !CollectionUtils.isEmpty(menuList)) {
                menuService.updateBatch(menuList);
            }
        }

        workSpotService.update(workSpot);
        return ResultVoUtil.success("修改成功");
    }

    /**
     * 跳转到列表页面
     */
    @GetMapping("/toExportExcel")
    //@RequiresPermissions("system:bidSection:index")
    public String toExportExcel(Model model, Long workSpotId) {
        // 封装数据
        model.addAttribute("workSpotId", workSpotId);
        return "/system/workSpot/exportExcel";
    }

    @RequestMapping("/exportExcel")
    public void export(HttpServletResponse response, Long workSpotId, String startTime, String endTime) {

        WorkSpot workSpot = workSpotService.findById(workSpotId);
        List<Object[]> byNameGetMeasuringSpotData = workSpotService.findByNameGetMeasuringSpotData(workSpot.getName(), startTime, endTime + " 23:59:59");
        Map<String, List<WorkSpotMeasuringSpotDataPage>> sensorDataGroupMap = new LinkedHashMap<>();
        List<WorkSpotMeasuringSpotDataPage> measuringSpotData = EntityUtils.castEntity(byNameGetMeasuringSpotData, WorkSpotMeasuringSpotDataPage.class, new WorkSpotMeasuringSpotDataPage());


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

        row2.createCell(0).setCellValue("测点名称");
        row2.createCell(1).setCellValue("测点初值");
        row2.createCell(2).setCellValue("本次测值");
        row2.createCell(3).setCellValue("累加值");

        // 创建第一行
        for (int i = 0; i < measuringSpotData.size(); i++) {
            //创建行    一条数据一行
            HSSFRow row = sheet.createRow(i + 2);
            row.createCell(0).setCellValue(measuringSpotData.get(i).getMeasuringSpotName());
            row.createCell(1).setCellValue(measuringSpotData.get(i).getInitial_value());
            row.createCell(2).setCellValue(measuringSpotData.get(i).getMeasuringSpotValue());
            row.createCell(3).setCellValue(measuringSpotData.get(i).getMeasuringSpotAccumulationValue());
        }
        try {
            /**
             * 弹出下载选择路径框
             */
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(workSpot.getName().getBytes("gb2312"), "ISO8859-1") + ":" + startTime + "-" + endTime + ".xls");
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
        WorkSpot workSpot = workSpotService.findById(id);
        if (workSpot != null) {
            BidSection bidSection = bidSectionService.getById(workSpot.getBidSectionId());
            if (bidSection != null) {
                LineInfo lineInfo = lineInfoService.getById(bidSection.getLineInfoId());
                if (lineInfo != null) {
                    title = lineInfo.getName() + "  -  " + bidSection.getName() + "  -  " + workSpot.getName();
                }
            }
        }


        return ResultVoUtil.success(title);

    }


    @RequestMapping("/exportWorkSpotData")
    public void exportWorkSpotData(HttpServletResponse response, @RequestParam(value = "measuringSpotId") Long[] measuringSpotId, Long workSpotId, String startTime, String endTime) {
        Map<String, List<MeasuringSpotData>> measuringSpotMap = new HashMap<>();

        List<MeasuringSpotData> measuringSpotDataList = new ArrayList<>();
        //没有传ID就是导出全部
        if (measuringSpotId.length <= 0) {
            List<MeasuringSpot> measuringSpots = measuringSpotService.findByWorkSpotId(workSpotId);
            List<Long> id = new ArrayList<>();
            for (MeasuringSpot measuringSpot : measuringSpots) {
                id.add(measuringSpot.getId());
            }
            Long[] idAll = new Long[id.size()];
            id.toArray(idAll);
            measuringSpotId = idAll;
        }

        for (int i = 0; i < measuringSpotId.length; i++) {
            if (StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {//没有传时间
                measuringSpotMap.put(measuringSpotService.getById(measuringSpotId[i]).getName(), measuringSpotDataService.findByMeasuringSpotId(measuringSpotId[i]));
            } else if (!StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {//传了开始时间
                measuringSpotMap.put(measuringSpotService.getById(measuringSpotId[i]).getName(), measuringSpotDataService.findByMeasuringSpotIdAndReceiveTimeGreaterThan(measuringSpotId[i], startTime));
            } else {//根据传了时间来查询
                measuringSpotMap.put(measuringSpotService.getById(measuringSpotId[i]).getName(), measuringSpotDataService.findByMeasuringSpotIdAndReceiveTimeGreaterThanAndReceiveTimeLessThan(measuringSpotId[i], startTime, endTime + " 23:59:59"));
            }

        }
        // 创建excel
        HSSFWorkbook wk = new HSSFWorkbook();
        Iterator<Map.Entry<String, List<MeasuringSpotData>>> iterator = measuringSpotMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<MeasuringSpotData>> next = iterator.next();
            // 创建一张工作表
            HSSFSheet sheet = wk.createSheet(next.getKey());
            // 设置工作表中的1-3列的宽度
            sheet.setColumnWidth(0, 5000);
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 5000);

            //创建第一行
            HSSFRow row1 = sheet.createRow(0);
            // 创建第一行的第一个单元格
            // 向单元格写值
            HSSFCell cell = row1.createCell(0);
            cell.setCellValue(next.getKey() + "数据表");
            //合并单元格CellRangeAddress构造参数依次表示起始行，截止行，起始列，截至列。
            //0表示 第一行第一列
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
            //创建第二行
            HSSFRow row2 = sheet.createRow(1);
            row2.createCell(0).setCellValue("传感器编号");
            row2.createCell(1).setCellValue("测点原始值");
            row2.createCell(2).setCellValue("测点值");
            row2.createCell(3).setCellValue("日速率值");
            row2.createCell(4).setCellValue("测点差额");
            row2.createCell(5).setCellValue("测点累加值");
            row2.createCell(6).setCellValue("接收时间");
            // 创建第一行
            for (int i = 0; i < next.getValue().size(); i++) {
                //创建行    一条数据一行
                HSSFRow row = sheet.createRow(i + 2);
                row.createCell(0).setCellValue(next.getValue().get(i).getMeasuringSpotSensorNo());
                row.createCell(1).setCellValue(next.getValue().get(i).getMeasuringSpotOriginalValue());
                row.createCell(2).setCellValue(next.getValue().get(i).getMeasuringSpotValue());
                row.createCell(3).setCellValue(next.getValue().get(i).getDayRateValue());
                row.createCell(4).setCellValue(next.getValue().get(i).getMeasuringSpotDifference());
                row.createCell(5).setCellValue(next.getValue().get(i).getMeasuringSpotAccumulationValue());
                row.createCell(6).setCellValue(next.getValue().get(i).getReceiveTime());
            }
        }
        try {
            /**
             * 弹出下载选择路径框
             */
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String str = sdf.format(date);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String("ISO8859-1") + ":" + startTime + "-" + endTime + ".xls");
            response.flushBuffer();
            wk.write(response.getOutputStream());
            // wk.write(new FileOutputStream(new File("D://daochu")));
            //wk.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @RequestMapping("/toEcharts")
    public String toEcharts(String startTime, String endTime, @RequestParam(value = "measuringSpotId") Long[] measuringSpotId, Long workSpotId, Model model) {

        String id = "";
        if (measuringSpotId.length > 0) {
            for (Long l : measuringSpotId) {
                id += String.valueOf(l) + ",";
            }
        } else {
            List<MeasuringSpot> measuringSpots = measuringSpotService.findByWorkSpotId(workSpotId);
            for (MeasuringSpot measuringSpot : measuringSpots) {
                id += String.valueOf(measuringSpot.getId()) + ",";
            }
        }

        model.addAttribute("id", id.substring(0, id.length() - 1));
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        return "/system/workSpot/echarts";
    }


    @RequestMapping("/echartsData")
    @ResponseBody
    public Map<String, List<MeasuringSpotData>> echartsData(String startTime, String endTime, @RequestParam(value = "id") Long[] measuringSpotId) {


        Map<String, List<MeasuringSpotData>> map = new HashMap<>();

        //装Y轴数据
        for (int i = 0; i < measuringSpotId.length; i++) {

            List<MeasuringSpotData> measuringSpotDataListY = new ArrayList<>();
            //传了时间  传了开始时间
            MeasuringSpot measuringSpot = measuringSpotService.findById(measuringSpotId[i]);
            if (measuringSpot != null) {
                if (StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {//没有传时间
                    measuringSpotDataListY = measuringSpotDataService.findByMeasuringSpotId(measuringSpot.getId());
                } else if (!StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {//传了结束时间
                    measuringSpotDataListY = measuringSpotDataService.findByMeasuringSpotIdAndReceiveTimeGreaterThan(measuringSpot.getId(), startTime);
                } else if (StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {//传了结束时间
                    measuringSpotDataListY = measuringSpotDataService.findByMeasuringSpotIdAndReceiveTimeLessThan(measuringSpot.getId(), endTime + " 23:59:59");
                } else {
                    measuringSpotDataListY = measuringSpotDataService.findByMeasuringSpotIdAndReceiveTimeGreaterThanAndReceiveTimeLessThan(measuringSpot.getId(), startTime, endTime + " 23:59:59");
                }
                map.put(measuringSpot.getName(), measuringSpotDataListY);
            }
        }

        return map;
    }








    @GetMapping("/getData")
    @ResponseBody
    public ResultVo list(Long id, Long dateTyep) {

        String date = CommonMethod.getDate();//当前时间
        Map<String, SurfaceSettlementPage> data = new HashMap<>();
        List<String> classification = new ArrayList<>(); //获取分类
        try {

            //1.获取所有测点
            List<MeasuringSpot> measuringSpotList = new ArrayList<>();
            WorkSpot workSpotInfo = workSpotService.findById(id);

            List<MeasuringSpot> measuringSpotInfo = measuringSpotService.findByWorkSpotId(workSpotInfo.getId());
            for (MeasuringSpot ms : measuringSpotInfo) {

                classification.add(DictUtil.keyValue("MEASUREMENT_ITEMS", ms.getMeasuringSpotType()));
            }
            measuringSpotList.addAll(measuringSpotInfo);

            removeDuplicateWithOrderNoVoid(classification);


            //2装表相应的数据
            for (String type : classification) {
                WorkSpot workSpot = workSpotService.findById(measuringSpotList.get(0).getWorkSpotId());
                BidSection bidSection = bidSectionService.getById(workSpot.getBidSectionId());
                User user = ShiroUtil.getSubject();
                List<Report> reportList = reportService.findByInputIdOrderByIdDesc(String.valueOf(user.getId()));
                SurfaceSettlementPage surfaceSettlementPage = new SurfaceSettlementPage();

                String Remarks = "";

                surfaceSettlementPage.setWorkSpotId(workSpot.getId());
                surfaceSettlementPage.setProjectName(workSpot.getName());//工程名称（工点名称取）
                surfaceSettlementPage.setConstructionCompany(bidSection.getConstructionCompany());//施工单位（标段里面取）
                surfaceSettlementPage.setMeasuringSpotType(type); //监测项目（测点类型）
                surfaceSettlementPage.setSupervisorCompany(bidSection.getSupervisorCompany());     //监理单位(标段里面取)

                String dateType = String.valueOf(dateTyep);// 1:日  2：周 3：月

                if ("1".equals(dateType)) {//日报就是昨天的最后一条数据
                    if (!CollectionUtils.isEmpty(reportList) && reportList.size() > 2) {
                        surfaceSettlementPage.setLastTimeDate(reportList.get(reportList.size() - 1).getInputTime());
                    }

                    surfaceSettlementPage.setThisTimeDate(date);
                } else if ("2".equals(dateType)) {
                    List<Report> weekReports = reportService.findAllByWorkSpotIdAndInputTimeLessThan(workSpot.getId(), CommonMethod.getWeekStart());
                    if (!CollectionUtils.isEmpty(weekReports)) {
                        surfaceSettlementPage.setThisTimeDate(weekReports.get(0).getInputTime());
                    } else {
                        surfaceSettlementPage.setThisTimeDate(CommonMethod.getWeekStart());
                    }
                    surfaceSettlementPage.setLastTimeDate(CommonMethod.getWeekEnd());
                } else {//月报的观测时间 是当前月的第一天和  最后一天
                    List<Report> weekReports = reportService.findAllByWorkSpotIdAndInputTimeLessThan(workSpot.getId(), CommonMethod.getMonthStart());
                    if (!CollectionUtils.isEmpty(weekReports)) {
                        surfaceSettlementPage.setThisTimeDate(weekReports.get(0).getInputTime());
                    } else {
                        surfaceSettlementPage.setThisTimeDate(CommonMethod.getMonthStart());
                    }
                    surfaceSettlementPage.setLastTimeDate(CommonMethod.getMonthEnd());
                }

                surfaceSettlementPage.setInstrumentSpecifications("");
                //装测量项目对应的测点数据
                Map<String, Object> dataMap = new LinkedHashMap<>();
                List<MeasuringSpot> measuringSpots = new ArrayList<>();

                for (MeasuringSpot measuringSpot : measuringSpotList) {//获取类型对应的测点

                    if (type.equals(DictUtil.keyValue("MEASUREMENT_ITEMS", measuringSpot.getMeasuringSpotType()))) {
                        if(measuringSpot.getStuts() == 2){//测点状态为3 代表停测
                            List<MeasuringSpotData> measuringSpotDataList = measuringSpotDataService.findByMeasuringSpotId(measuringSpot.getId());
                            if(!CollectionUtils.isEmpty(measuringSpotDataList)){

                                if ("1".equals(dateType)) {//如果最新的数据是昨天 那么还是会正常显示该
                                    if(CommonMethod.judgeYesterday(measuringSpotDataList.get(measuringSpotDataList.size()-1).getReceiveTime())){
                                        measuringSpots.add(measuringSpot);
                                    }
                                } else if ("2".equals(dateType)) {
                                    if(CommonMethod.LastWeek(measuringSpotDataList.get(measuringSpotDataList.size()-1).getReceiveTime())){
                                        measuringSpots.add(measuringSpot);
                                    }
                                } else {//月报的观测时间 是当前月的第一天和  最后一天

                                    if(CommonMethod.LastMonth(measuringSpotDataList.get(measuringSpotDataList.size()-1).getReceiveTime())){
                                        measuringSpots.add(measuringSpot);
                                    }
                                }
                            }
                            surfaceSettlementPage.setRemarks("测点编号为: "+measuringSpot.getNo()+" 已停测");
                        }else{
                            measuringSpots.add(measuringSpot);
                        }
                    }
                }

                Map<String, Object> map = new LinkedHashMap<>();
                for (MeasuringSpot measuringSpot : measuringSpots) {
                    List<MeasuringSpotData> measuringSpotDataList = measuringSpotDataService.findByMeasuringSpotId(measuringSpot.getId());
                    //使用Map来装数据
                    //第一步装所有测点的初测日期
                    if (!CollectionUtils.isEmpty(measuringSpotDataList)) {
                        map.put(measuringSpotDataList.get(0).getReceiveTime().substring(0, 10), null);
                    }
                }


                Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> next = iterator.next();
                    List<ReportDataPage> reportDataPages = new ArrayList<>();//装同一个时间的数据
                    for (MeasuringSpot measuringSpot : measuringSpots) {

                        String firstTime = "";
                        if (type.equals(DictUtil.keyValue("MEASUREMENT_ITEMS", measuringSpot.getMeasuringSpotType()))) {

                            List<MeasuringSpotData> measuringSpotDataList = measuringSpotDataService.findByMeasuringSpotId(measuringSpot.getId());
                            if (!CollectionUtils.isEmpty(measuringSpotDataList)) {
                                if (next.getKey().equals(measuringSpotDataList.get(0).getReceiveTime().substring(0, 10))) {
                                    ReportDataPage reportDataPage = new ReportDataPage();
                                    reportDataPage.setMeasuringSpotSensorNo(measuringSpot.getNo());//编号
                                    reportDataPage.setDeformationAmount(measuringSpotDataList.get(measuringSpotDataList.size() - 1).getMeasuringSpotDifference());
                                    reportDataPage.setCumulative(measuringSpotDataList.get(measuringSpotDataList.size() - 1).getMeasuringSpotAccumulationValue());
                                    if (measuringSpotDataList.size() >= 2) {
                                        reportDataPage.setLastVlaue(measuringSpotDataList.get(measuringSpotDataList.size() - 2).getMeasuringSpotValue());
                                    }
                                    reportDataPage.setThisVlaue(measuringSpotDataList.get(measuringSpotDataList.size() - 1).getMeasuringSpotValue());
                                    reportDataPages.add(reportDataPage);
                                }
                            }

                        }
                    }
                    dataMap.put(next.getKey(), reportDataPages);
                }
                surfaceSettlementPage.getMeasuringSpotData().add(dataMap);
                if (org.springframework.util.StringUtils.isEmpty(surfaceSettlementPage.getLastTimeDate())) {
                    surfaceSettlementPage.setLastTimeDate("");
                }
                data.put(type, surfaceSettlementPage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return ResultVoUtil.success(data);
    }





}

























