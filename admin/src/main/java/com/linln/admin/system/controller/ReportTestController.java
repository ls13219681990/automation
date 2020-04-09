package com.linln.admin.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/report")
public class ReportTestController {
/*

    public static final String picture = "D://Program Files//projectPicture";

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

    */
/**
     * 列表页面
     *//*

    @GetMapping("/index")
    public String index() {
        return "/system/report/index";
    }


    */
/**
     * 菜单数据列表
     *//*

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


    @GetMapping("/measuringSpotTree")
    public String report() {


        return "/system/report/measuringSpotTree";
    }


    */
/**
     * 菜单数据列表
     *//*

    @GetMapping("/getData")
    @ResponseBody
    public ResultVo list(@RequestParam(value = "list") List<String> list) {

        String date = CommonMethod.getDate();//当前时间
        Map<String, SurfaceSettlementPage> data = new HashMap<>();
        List<String> classification = new ArrayList<>(); //获取分类

        //1.获取所有测点
        List<MeasuringSpot> measuringSpotList = new ArrayList<>();
        for (String name : list) {
            MeasuringSpot measuringSpot = measuringSpotService.findByName(name);
            String measurementItems = DictUtil.keyValue("MEASUREMENT_ITEMS", measuringSpot.getMeasuringSpotType());
            classification.add(measurementItems);
            measuringSpotList.add(measuringSpot);
        }
        removeDuplicateWithOrderNoVoid(classification);

        //2装表相应的数据
        for (String type : classification) {
            WorkSpot workSpot = workSpotService.findById(measuringSpotList.get(0).getWorkSpotId());
            BidSection bidSection = bidSectionService.getById(workSpot.getBidSectionId());
            User user = ShiroUtil.getSubject();
            List<Report> reportList = reportService.findByInputUserIdOrderByIdDesc(user.getId());
            SurfaceSettlementPage surfaceSettlementPage = new SurfaceSettlementPage();

            surfaceSettlementPage.setProjectName(workSpot.getName());//工程名称（工点名称取）
            surfaceSettlementPage.setConstructionCompany(bidSection.getConstructionCompany());//施工单位（标段里面取）
            surfaceSettlementPage.setMeasuringSpotType(type); //监测项目（测点类型）
            surfaceSettlementPage.setSupervisorCompany(bidSection.getSupervisorCompany());     //监理单位(标段里面取)
            if (!CollectionUtils.isEmpty(reportList)) {
                surfaceSettlementPage.setLastTimeDate(reportList.get(reportList.size()).getInputTime());
            }

            surfaceSettlementPage.setThisTimeDate(date);
            surfaceSettlementPage.setInstrumentSpecifications("");
            surfaceSettlementPage.setRemarks("1.“+”值表示测点隆起，“-”值表示测点下沉，“/”表示测点被挡或破坏。");
            //装测量项目对应的测点数据
            for (MeasuringSpot measuringSpot : measuringSpotList) {
                if (DictUtil.keyValue("MEASUREMENT_ITEMS", measuringSpot.getMeasuringSpotType()) == "地下水位") {
                    GroundwaterPage supportShaftPage = new GroundwaterPage();
                    supportShaftPage.setMeasuringSpotSensorNo(measuringSpot.getNo());//编号
                    //测点数据实体
                    List<MeasuringSpotData> measuringSpotDataList = measuringSpotDataService.findByMeasuringSpotId(measuringSpot.getId());
                    supportShaftPage.setMeasuringSpotValue(measuringSpotDataList.get(measuringSpotDataList.size() - 1).getMeasuringSpotValue());//本次测值
                    surfaceSettlementPage.getMeasuringSpotData().add(supportShaftPage);
                } else if (DictUtil.keyValue("MEASUREMENT_ITEMS", measuringSpot.getMeasuringSpotType()) == "支撑轴力") {//支撑轴力、

                    SupportShaftPage supportShaftPage = new SupportShaftPage();
                    supportShaftPage.setMeasuringSpotSensorNo(measuringSpot.getNo());//编号
                    //测点数据实体
                    List<MeasuringSpotData> measuringSpotDataList = measuringSpotDataService.findByMeasuringSpotId(measuringSpot.getId());
                    if (measuringSpotDataList.size() >= 2) {
                        supportShaftPage.setLastVlaue(measuringSpotDataList.get(measuringSpotDataList.size() - 2).getMeasuringSpotValue());//上次测值
                    }
                    supportShaftPage.setThisVlaue(measuringSpotDataList.get(measuringSpotDataList.size() - 1).getMeasuringSpotValue());//本次测值
                    surfaceSettlementPage.getMeasuringSpotData().add(supportShaftPage);


                } else if (DictUtil.keyValue("MEASUREMENT_ITEMS", measuringSpot.getMeasuringSpotType()) == "建筑物倾斜") {//建筑物倾斜、

                } else {
                    //地表沉降、污水管线沉降、燃气管线沉降、雨水管线沉降、通讯管线沉降、悬吊管线、桩顶沉降、建筑物沉降
                    if (type.equals(DictUtil.keyValue("MEASUREMENT_ITEMS", measuringSpot.getMeasuringSpotType()))) {
                        ReportMeasuringSpotDataPage reportMeasuringSpotDataPage = new ReportMeasuringSpotDataPage();
                        reportMeasuringSpotDataPage.setMeasuringSpotSensorNo(measuringSpot.getNo());//编号
                        reportMeasuringSpotDataPage.setMeasuringSpotOriginalValue(measuringSpot.getInitialValue());//测点初值
                        //测点数据实体
                        List<MeasuringSpotData> measuringSpotDataList = measuringSpotDataService.findByMeasuringSpotId(measuringSpot.getId());
                        if (measuringSpotDataList.size() >= 2) {
                            reportMeasuringSpotDataPage.setMeasuringSpotSensorLastTimeValue(measuringSpotDataList.get(measuringSpotDataList.size() - 2).getMeasuringSpotValue());//上次测值
                        }
                        reportMeasuringSpotDataPage.setMeasuringSpotValue(measuringSpotDataList.get(measuringSpotDataList.size() - 1).getMeasuringSpotValue());//本次测值
                        reportMeasuringSpotDataPage.setMeasuringSpotDifference(measuringSpotDataList.get(measuringSpotDataList.size() - 1).getMeasuringSpotDifference());
                        reportMeasuringSpotDataPage.setMeasuringSpotAccumulationValue(measuringSpotDataList.get(measuringSpotDataList.size() - 1).getMeasuringSpotAccumulationValue());
                        surfaceSettlementPage.getMeasuringSpotData().add(reportMeasuringSpotDataPage);
                    }
                }
            }
            //累计值最大值
            data.put(type, surfaceSettlementPage);

        }
        return ResultVoUtil.success(data);
    }


    */
/**
     * 菜单数据列表
     *//*

    @RequestMapping("/test")
    @ResponseBody

    public ResultVo test(String test, HttpServletRequest request, HttpServletResponse response) {
        //1：获取html字符串 生成html 然后通过 Poi 进行生成word   把每次选中的图片都上传


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
            test = new String(decoder.decodeBuffer(test), "UTF-8");
            out = new FileOutputStream(path);
            byte[] srtbyte = test.getBytes();
            out.write(srtbyte);
            bodyIs = new FileInputStream(path);
            //InputStream cssIs = new FileInputStream(cssPath);
            HtmlToWord hw = new HtmlToWord();
            String body = hw.getContent(bodyIs);

            String s = writeWordFile(body);//拿到返回的地址 返回到前台
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ResultVoUtil.SAVE_SUCCESS;// 创建匹配器，进行动态查询匹配

    }


    @ResponseBody
    @RequestMapping("/uploadImg.xhtml")
    public void uploadPicture(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {


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
*/


}
