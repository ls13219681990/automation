package com.linln.admin.system.controller;

import com.linln.admin.system.common.report.WordUtil2007;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.shiro.ShiroUtil;
import com.linln.component.thymeleaf.utility.DictUtil;
import com.linln.modules.system.Common.Server;
import com.linln.modules.system.domain.*;
import com.linln.modules.system.page.*;
import com.linln.modules.system.service.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static com.linln.admin.system.common.CommonMethod.removeDuplicateWithOrderNoVoid;
import static com.linln.admin.system.common.report.HtmlToElement.supplementTable;

import com.linln.admin.system.common.CommonMethod;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/report")
public class ReportController {

    public static final String picture = "C://projectPicture";

    public static final String imgPath = "D://img//";

    @Autowired
    private ReportMenuService reportMenuService;

    @Autowired
    private MeasuringSpotService measuringSpotService;

    @Autowired
    private MeasuringSpotDataService measuringSpotDataService;

    @Autowired
    private WorkSpotService workSpotService;

    @Autowired
    private BidSectionService bidSectionService;


    @Autowired
    private ReportService reportService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private Server serverPath;

    @Autowired
    private UserService userService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    public String index() {
        return "/system/report/index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") Long id) {
        model.addAttribute("templateId", id);
        return "/system/report/editReport";
    }

    @GetMapping("/templateIndex")
    public String templateIndex() {
        return "/system/report/template";
    }

    @GetMapping("/uploadTemplate")
    public String uploadTemplate() {
        return "/system/report/uploadTemplate";
    }

    @GetMapping("/replaceTemplate/{id}")
    public String replace(Model model, @PathVariable("id") Template template) {
        model.addAttribute("templateId", template.getId());
        return "/system/report/replaceTemplate";
    }

    @GetMapping("/editMeasuringSpot")
    public String editMeasuringSpot(Model model, Long id) {
        model.addAttribute("templateId", id);
        return "/system/report/editMeasuringSpot";
    }


    /**
     * 菜单数据列表
     */
    @GetMapping("/list")
    @ResponseBody
    public ResultVo list(ReportMenu menu) {
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("name", match -> match.contains());

        // 获取菜单列表
        Example<ReportMenu> example = Example.of(menu, matcher);
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        List<ReportMenu> list = reportMenuService.getListByExample(example, sort);


        List<Object> objects = MenuTreeUtil.reportMenuList(list);

        return ResultVoUtil.success(objects);
    }


    @RequestMapping("/findReportByNameOrDate")
    @ResponseBody
    public TablePage findReportByNameOrDate(Integer page, Integer limit, Long workSpotId, String name, String startDate, String endDate) {

        Page<Report> all = reportService.findByNameOrDate(page, limit, workSpotId, name, startDate, endDate);

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(reportService.findAll().size());
        if (all.getContent() != null) {
            tablePage.setData(all.getContent());
        }

        return tablePage;


    }

    /**
     * 查询所有报告
     */
    @GetMapping("/findAll")
    @ResponseBody
    public TablePage findAll(Integer page, Integer limit) {


        Page<Report> all = reportService.findAll(page, limit);

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(reportService.findAll().size());
        tablePage.setData(all.getContent());
        return tablePage;

    }

    /**
     * 工点数据
     */
    @RequestMapping("/findByWorkSpotId")
    @ResponseBody
    public TablePage findByWorkSpotId(Long workSpotId, Long bidSectionId, Integer page, Integer limit) {

        List<Report> reportList = new ArrayList<>();

        //总条数
        Integer total = 0;

        //如果没传ID 那说明是看当前用户的所有报告
        if (workSpotId == null || (bidSectionId == 0 && workSpotId == 0)) {
            reportList = reportService.findAllByInputIdOrderByInputTimeDesc(String.valueOf(ShiroUtil.getSubject().getId()), page, limit);
            total = reportService.findAllByInputIdOrderByInputTimeDesc(String.valueOf(ShiroUtil.getSubject().getId())).size();
        } else {
            if (workSpotId == 0) {//点击工点全部  查询用户对应的所有工点  再通过工点去查看报告
                List<WorkSpot> byBidSectionId = workSpotService.findByBidSectionId(bidSectionId);
                for (WorkSpot workSpot : byBidSectionId) {
                    reportList.addAll(reportService.findByWorkSpotId(workSpot.getId()));

                }
                total = reportList.size();
                List<List<Report>> lists = CommonMethod.splitList(reportList, limit);
                if (!CollectionUtils.isEmpty(lists)) {
                    reportList.clear();

                    reportList.addAll(lists.get(page - 1));
                }


            } else {
                Page<Report> all = reportService.findByWorkSpotId(workSpotId, page, limit);
                if (!CollectionUtils.isEmpty(all.getContent())) {
                    reportList = all.getContent();
                    total = Integer.valueOf(String.valueOf(all.getTotalElements()));
                }
            }
        }
        for (Report report : reportList) {
            report.setInputId(userService.getById(Long.valueOf(report.getInputId())).getUsername());
        }

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(total);
        tablePage.setData(reportList);
        return tablePage;

    }


    /**
     * 工点数据 用于下拉框
     */
    @RequestMapping("/findByWorkSpotIdSelect")
    @ResponseBody
    public TablePage findByWorkSpotIdSelect(Long workSpotId, Integer page, Integer limit) {

        List<Report> reportList = new ArrayList<>();

        //总条数
        Integer total = 0;

        //如果没传ID 那说明是看当前用户的所有报告
        if (workSpotId == null) {
            reportList = reportService.findAllByInputIdOrderByInputTimeDesc(String.valueOf(ShiroUtil.getSubject().getId()), page, limit);
            total = reportService.findAllByInputIdOrderByInputTimeDesc(String.valueOf(ShiroUtil.getSubject().getId())).size();
        } else {
            Page<Report> all = reportService.findByWorkSpotId(workSpotId, page, limit);
            reportList = all.getContent();
            total = Integer.valueOf(String.valueOf(all.getTotalElements()));
        }
        for (Report report : reportList) {
            report.setInputId(userService.getById(Long.valueOf(report.getInputId())).getUsername());
        }

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(total);
        tablePage.setData(reportList);
        return tablePage;

    }

    @GetMapping("/measuringSpotTree")
    public String report() {
        return "/system/report/measuringSpotTree";
    }


    /**
     * 报告数据
     * 总体思路 ：   业务==》  前台传送工点进来   需要返回工点对应的所有测点数据
     * 解决方案：  1、拿到传送的工点查询所有测点  并且拿到所有测点对应的测点类型
     * 2、循环所有类型 根据类型装数据
     * 3、关于时间的 处理  类型对应的测点数据  取出初测日期  用初测日期进行循环 装填数据
     */
    @GetMapping("/getData")
    @ResponseBody
    public ResultVo list(@RequestParam(value = "list") List<Long> list) {
        if (list.size() > 2) {        ///////////////////////////////////////////////注意先做单工点，如果是多工点就返回到前台一个提醒
            return ResultVoUtil.error("只能单工点生成word");
        }
        String date = CommonMethod.getDate();//当前时间
        Map<String, SurfaceSettlementPage> data = new HashMap<>();
        List<String> classification = new ArrayList<>(); //获取分类
        try {

            //1.获取所有测点
            List<MeasuringSpot> measuringSpotList = new ArrayList<>();

            Menu menu = menuService.getById(list.get(1));

            WorkSpot workSpotInfo = workSpotService.findByName(menu.getTitle());

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
                surfaceSettlementPage.setWorkSpotId(workSpot.getId());
                surfaceSettlementPage.setProjectName(workSpot.getName());//工程名称（工点名称取）
                surfaceSettlementPage.setConstructionCompany(bidSection.getConstructionCompany());//施工单位（标段里面取）
                surfaceSettlementPage.setMeasuringSpotType(type); //监测项目（测点类型）
                surfaceSettlementPage.setSupervisorCompany(bidSection.getSupervisorCompany());     //监理单位(标段里面取)

                String dateType = String.valueOf(list.get(0));// 1:日  2：周 3：月

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
                        if (measuringSpot.getStuts() == 2) {//测点状态为3 代表停测
                            List<MeasuringSpotData> measuringSpotDataList = measuringSpotDataService.findByMeasuringSpotId(measuringSpot.getId());
                            if (!CollectionUtils.isEmpty(measuringSpotDataList)) {

                                if ("1".equals(dateType)) {//如果最新的数据是昨天 那么还是会正常显示该
                                    if (CommonMethod.judgeYesterday(measuringSpotDataList.get(measuringSpotDataList.size() - 1).getReceiveTime())) {
                                        measuringSpots.add(measuringSpot);
                                    }
                                } else if ("2".equals(dateType)) {
                                    if (CommonMethod.LastWeek(measuringSpotDataList.get(measuringSpotDataList.size() - 1).getReceiveTime())) {
                                        measuringSpots.add(measuringSpot);
                                    }
                                } else {//月报的观测时间 是当前月的第一天和  最后一天
                                    if (CommonMethod.LastMonth(measuringSpotDataList.get(measuringSpotDataList.size() - 1).getReceiveTime())) {
                                        measuringSpots.add(measuringSpot);
                                    }
                                }
                            }
                            surfaceSettlementPage.setRemarks("测点编号为: " + measuringSpot.getNo() + " 已停测");
                        } else {
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
                if (StringUtils.isEmpty(surfaceSettlementPage.getLastTimeDate())) {
                    surfaceSettlementPage.setLastTimeDate("");
                }
                data.put(type, surfaceSettlementPage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return ResultVoUtil.success(data);
    }


    /**
     * 保存报告
     */
    @RequestMapping("/saveReport")
    @ResponseBody
    public ResultVo test(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> map) {
        FileOutputStream fopts = null;

        try {
            Map<String, List<ReportDataInfo>> measuringSpotMap = new HashMap<>();
            String templateId = (String) map.get("templateId");
            String dateType = (String) map.get("dateType");
            WorkSpot workSpot = workSpotService.findById(Long.valueOf(map.get("workSpotId").toString()));

            Map<String, Object> dataMap = CommonMethod.removeBykey(map, "templateId");

            List<HashMap<String, List>> pictureList = (List<HashMap<String, List>>) map.get("pictureList");
            for (HashMap<String, List> stringListHashMap : pictureList) {
                Iterator<Map.Entry<String, List>> iterator = stringListHashMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, List> next = iterator.next();
                    for (LinkedHashMap maps : (List<LinkedHashMap>) next.getValue()) {

                        List<ReportDataInfo> reportDataInfos = reportService.findByMeasuringIdAndDate(measuringSpotService.findByName(maps.get("title").toString()).getId(), map.get("startTime").toString(), map.get("endTime").toString());
                        measuringSpotMap.put(maps.get("title").toString(), reportDataInfos);
                    }
                }
            }

            //1.获取模板
            Template temData = templateService.findByid(Long.valueOf(templateId));
            //2替换数据
            Iterator<Map.Entry<String, Object>> iterator = dataMap.entrySet().iterator();
            XWPFDocument doc = new XWPFDocument();
            String path = "";
            int index = 0;
            List<String> delFile = new ArrayList<>();
            while (iterator.hasNext()) {
                Map<String, Object> param = new HashMap<String, Object>();
                Map.Entry<String, Object> next = iterator.next();
                String key = "${" + next.getKey() + "}";
                param.put(key, key);
                switch (dateType) {
                    case "月":
                        param.put("${startDate}", "2019-02-01 ");
                        param.put("${endDate}", " 2019-02-28");
                        break;
                    case "周":
                        param.put("${startDate}", "2019-03-01 ");
                        param.put("${endDate}", " 2019-03-07");
                        break;

                    default:
                        param.put("${startDate}", "2019-03-01 ");
                        param.put("${endDate}", " 2019-03-02");
                }
                if (index == 0) {
                    doc = WordUtil2007.generateDoc(serverPath.getReportPath() + temData.getAddress(), param);
                } else {
                    doc = WordUtil2007.generateDoc(path, param);
                }

                if (next.getValue() instanceof String) {
                    Element node = supplementTable((String) next.getValue());
                    WordUtil2007.insertTab(key, doc, node, pictureList, measuringSpotMap); // /----------创建表
                    //替换掉多余的占位符
                    param = new HashMap<String, Object>();
                    param.put(key, "");
                    WordUtil2007.processParagraphs(doc.getParagraphs(), param, doc);

                    String filePath = serverPath.getReportPath() + "\\roport\\" + UUID.randomUUID().toString().replaceAll("-", "") + ".docx";
                    File file = new File(filePath);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();// 新建文件夹
                    }
                    fopts = new FileOutputStream(filePath);
                    doc.write(fopts);

                    delFile.add(filePath);
                    path = filePath;
                    ++index;
                }
            }

            //保存报告信息   路径存相对路径
            Report report = new Report();
            report.setAddress(path.substring(serverPath.getReportPath().length(), path.length()));
            report.setInputTime(CommonMethod.getDate());
            report.setInputId(String.valueOf(ShiroUtil.getSubject().getId()));
            report.setName(workSpot.getName());
            report.setWorkSpotId(workSpot.getId());
            reportService.save(report);


            //删除多余文件
            delFile.remove(delFile.size() - 1);
            System.out.println();
            for (String d : delFile) {
                CommonMethod.deleteFile(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!StringUtils.isEmpty(fopts)) {
                    fopts.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 删除报告
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/edit/{id}")
    public ResultVo deleteReport(Long id) {

        Report report = reportService.findByid(id);
        String path = serverPath.getReportPath() + report.getAddress();

        try {
            File file = new File(path);
            if (file.exists()) {  // 不存在返回 false
                file.delete();
            }
            if (report != null) {
                reportService.delete(report);
            }
        } catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();
        }
        return ResultVoUtil.success("报告已删除");

    }






    /*    */

    /**
     * 保存报告
     *//*
    @RequestMapping("/saveReport")
    @ResponseBody

    public ResultVo test(String html, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "list[]") List<Long> list) {
        //1：获取html字符串 生成html 然后通过 Poi 进行生成word   把每次选中的图片都上传
        String currentTime = CommonMethod.getDate();
        WorkSpot workSpot = workSpotService.findByName(menuService.findById(list.get(0)).getTitle());


        InputStream inputStream = null;
        //1、生成网页
        OutputStream out = null;
        InputStream bodyIs;

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        try {
            //首先创建目录
            File file = new File(imgPath);
            //如果文件夹不存在则创建
            //判断是否存在
            if (!file.exists() && !file.isDirectory()) {
                file.mkdir();
            } else {
                file.mkdirs();
            }
            String path = imgPath + uuid + ".html";

            BASE64Decoder decoder = new BASE64Decoder();
            html = new String(decoder.decodeBuffer(html), "UTF-8");
            out = new FileOutputStream(path);
            byte[] srtbyte = html.getBytes();
            out.write(srtbyte);
            bodyIs = new FileInputStream(path);
            //InputStream cssIs = new FileInputStream(cssPath);
            HtmlToWord hw = new HtmlToWord();
            String body = hw.getContent(bodyIs);

            String sddress = writeWordFile(body);//拿到返回的地址 返回到前台

            //  保存报告信息
            Report report = new Report();
            report.setAddress(sddress);
            report.setInputTime(currentTime);
            report.setInputName(ShiroUtil.getSubject().getUsername());
            report.setName(workSpot.getName());
            report.setWorkSpotId(workSpot.getId());
            reportService.save(report);

           *//* response.reset();
            response.setContentType("bin");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String("currentTime.docx".getBytes(), "ISO-8859-1"));

            ServletOutputStream outputStream = response.getOutputStream();

            inputStream = new FileInputStream(new File(sddress));
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, length);
            }
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }*//*

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                *//* inputStream.close();*//*
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResultVoUtil.SAVE_SUCCESS;
    }*/

    /**
     * 没用了的方法：用于每次用户选择图标就直接保存该照片
     *
     * @param file
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping("/uploadImg.xhtml")
    public void uploadPicture(@RequestParam(value = "file", required = false) MultipartFile
                                      file, HttpServletRequest request, HttpServletResponse response) {


        File targetFile = null;
        String url = "";//返回存储路径
        int code = 1;
        System.out.println(file);
        String fileName = file.getOriginalFilename();//获取文件名加后缀
        if (fileName != null && fileName != "") {
            String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/upload/imgs/";//存储路径
            File file1 = new File(picture);
            //如果文件夹不存在则创建
            //判断是否存在
            if (!file1.exists() && !file1.isDirectory()) {
                file1.mkdir();
            } else {
                file1.mkdirs();
            }
            //将图片存入文件夹
            targetFile = new File(file1, fileName);
            try {
                //将上传的文件写到服务器上指定的文件。
                file.transferTo(targetFile);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * 文档的下载方法：用于导出文档
     *
     * @param response
     * @param id
     */
    @GetMapping("/downWordFile/{id}")
    @ResponseBody
    public void downWordFile(HttpServletResponse response, @PathVariable("id") Long id) {
        InputStream inputStream = null;
        try {


            Report report = reportService.findByid(id);

            String fileName = report.getName() + ".docx";

            response.reset();
            response.setContentType("bin");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
            ServletOutputStream outputStream = response.getOutputStream();

            inputStream = new FileInputStream(new File(serverPath.getReportPath() + report.getAddress()));
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, length);
            }
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 模板的下载方法：用于导出模板
     *
     * @param response
     * @param id
     */
    @GetMapping("/downTemplate/{id}")
    @ResponseBody
    public void downTemplate(HttpServletResponse response, @PathVariable("id") Long id) {
        InputStream inputStream = null;
        try {

            Template report = templateService.findByid(id);
            String fileName = report.getName() + ".docx";
            response.reset();
            response.setContentType("bin");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
            ServletOutputStream outputStream = response.getOutputStream();
            inputStream = new FileInputStream(new File(serverPath.getReportPath() + report.getAddress()));
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, length);
            }
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 通过工点ID查询工点下面的所有测点类型
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getMeasuringSpotType")
    public ResultVo getMeasuringSpotType(Long id) {

        Map<String, List<MeasuringSpot>> map = new HashMap<>();

        List<MeasuringSpot> measuringSpots = measuringSpotService.findByWorkSpotId(id);

        for (MeasuringSpot measuringSpot : measuringSpots) {
            map.put(DictUtil.keyValue("MEASUREMENT_ITEMS", measuringSpot.getMeasuringSpotType()), null);
        }
        Iterator<Map.Entry<String, List<MeasuringSpot>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            List<MeasuringSpot> measuringSpotList = new ArrayList<>();
            Map.Entry<String, List<MeasuringSpot>> next = iterator.next();
            for (MeasuringSpot measuringSpot : measuringSpots) {
                if (next.getKey().equals(DictUtil.keyValue("MEASUREMENT_ITEMS", measuringSpot.getMeasuringSpotType()))) {
                    measuringSpotList.add(measuringSpot);
                }
            }
            map.put(next.getKey(), measuringSpotList);

        }


        return ResultVoUtil.success(map);

    }


    @GetMapping("/templateData")
    @ResponseBody
    public TablePage templateData(Integer page, Integer limit) {

        Page<Template> all = templateService.findAll(page, limit);
       /* for (Template t: all.getContent()) {
            t.setAddress(serverPath.getReportPath()+t.getAddress());
        }
*/
        List<Template> templateList = all.getContent();
        for (Template template : templateList) {
            template.setUserId(userService.getById(Long.valueOf(template.getUserId())).getUsername());
        }


        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(templateService.findAll().size());
        tablePage.setData(all.getContent());
        return tablePage;

    }

    @RequestMapping("/findTemplate")
    @ResponseBody
    public TablePage findTemplate(Integer page, Integer limit, String name, String startDate, String endDate) {

        Page<Template> all = templateService.findByNameOrDate(page, limit, name, startDate, endDate);

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(templateService.findAll().size());
        if (all.getContent() != null) {
            tablePage.setData(all.getContent());
        }

        return tablePage;

    }

    /**
     * 上传模板
     *
     * @param file
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping("/uploadTemplate")
    public ResultVo uploadTemplate(@RequestParam(value = "file", required = false) MultipartFile
                                           file, HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = ShiroUtil.getSubject();//获取当前用户
            if (file.isEmpty()) {
                ResultVoUtil.error("文件为空");
            }
            // 获取文件名
            String fileName = file.getOriginalFilename();
            String name = request.getParameter("name");
            String remarks = request.getParameter("remarks");
            // 设置文件存储路径
            String path = "\\template\\" + fileName;
            File dest = new File(serverPath.getReportPath() + path);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();// 新建文件夹
            }
            file.transferTo(dest);// 文件写入

            Template template = new Template();
            template.setName(name);
            template.setRemarks(remarks);
            template.setUploadDate(CommonMethod.getDate());
            template.setAddress(path);
            template.setUserId(String.valueOf(user.getId()));
            templateService.save(template);

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultVoUtil.SAVE_SUCCESS;

    }

    /**
     * 删除模板
     *
     * @param file     文件
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping("/delTemplate/{id}")
    public ResultVo delTemplate(@RequestParam(value = "file", required = false) MultipartFile
                                        file, HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Template temp) {


        try {

            Template template = templateService.findByid(temp.getId());

            File dest = new File(serverPath.getReportPath() + template.getAddress());
            if (!dest.exists()) {
                // 文件不存在
                throw new RuntimeException("文件不存在");
            } else {
                dest.delete();
            }
            //删除模板信息
            if (template != null) {
                templateService.delete(template);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return ResultVoUtil.SAVE_SUCCESS;

    }


    /**
     * 上传模板（替换模板）
     *
     * @param file     文件
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping("/replace")
    public ResultVo replaceTemplate(@RequestParam(value = "file", required = false) MultipartFile
                                            file, HttpServletRequest request, HttpServletResponse response) {


        try {

            String id = request.getParameter("id");
            Template template = templateService.findByid(Long.valueOf(id));
            //1.先删除以前的
            File delFile = new File(serverPath.getReportPath() + template.getAddress());
            if (!delFile.exists()) {
                // 文件不存在
                throw new RuntimeException("文件不存在");
            } else {
                delFile.delete();
            }
            if (file.isEmpty()) {
                ResultVoUtil.error("文件为空");
            }
            //2.再新增
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 设置文件存储路径
            String path = "\\template\\" + fileName;

            File dest = new File(serverPath.getReportPath() + path);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();// 新建文件夹
            }
            file.transferTo(dest);// 文件写入

            template.setAddress(path);
            templateService.update(template);

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultVoUtil.SAVE_SUCCESS;

    }


}
