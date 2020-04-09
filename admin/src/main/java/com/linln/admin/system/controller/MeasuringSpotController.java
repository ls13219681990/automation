package com.linln.admin.system.controller;

import com.linln.admin.system.common.CommonMethod;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.modules.system.page.SensorDataPage;
import com.linln.modules.system.page.TablePage;
import com.linln.modules.system.domain.*;
import com.linln.modules.system.service.*;
import fr.opensagres.xdocreport.core.utils.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.linln.admin.system.common.CommonMethod.addHour;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/measuringSpot")
public class MeasuringSpotController {


    @Autowired
    private MeasuringSpotService measuringSpotService;

    @Autowired
    private MeasuringSpotSensorService measuringSpotSensorService;


    @Autowired
    private SensorService sensorService;


    @Autowired
    private AcquisitionSensorService acquisitionSensorService;


    @Autowired
    private SensorDataService sensorDataService;


    @Autowired
    private SensorParameterService sensorParameterService;


    @Autowired
    private TemperatureResistanceService temperatureResistanceService;

    @Autowired
    private TemperatureResistanceDataService temperatureResistanceDataService;


    @Autowired
    private MenuService menuService;


    @Autowired
    private MeasuringSpotDataService measuringSpotDataService;

    @Autowired
    private WorkSpotService workSpotService;

    @Autowired
    private BidSectionService bidSectionService;

    @Autowired
    private LineInfoService lineInfoService;


    /**
     * 跳转到列表页面
     */
    @GetMapping("/index")
    //@RequiresPermissions("system:measuringSpot:index")
    public String index(Model model, String nodeName, @EntityParam SensorData SensorData) {

        MeasuringSpot measuringSpot = measuringSpotService.findByName(nodeName);
        String url = "/system/measuringSpot/index";
        List<MeasuringSpotSensor> measuringSpotSensorList = measuringSpotSensorService.getByMeasuringSpotId(measuringSpot.getId());
        for (MeasuringSpotSensor measuringSpotSensor : measuringSpotSensorList) {
            if (measuringSpotSensor.getDataGroup() != null && !"".equals(measuringSpotSensor.getDataGroup())) {
                url = "/system/measuringSpot/groupIndex";
            }
        }
        MeasuringSpotData measuringSpotDataMin = measuringSpotDataService.getDateMin(measuringSpot.getId());
        MeasuringSpotData measuringSpotDataMax = measuringSpotDataService.getDateMax(measuringSpot.getId());

        // 封装数据
        model.addAttribute("measuringSpot", measuringSpot);
        if (measuringSpotDataMin != null) {
            model.addAttribute("min", measuringSpotDataMin.getReceiveTime().substring(0, 10));
            model.addAttribute("max", measuringSpotDataMax.getReceiveTime().substring(0, 10));
        }

        return url;


    }


    /**
     * 跳转到列表页面
     */
    @RequestMapping("/findAll")
    /*@RequiresPermissions("system:measuringSpot:findAll")*/
    @ResponseBody
    public List<SensorDataPage> findAll() {

        List<SensorData> sensorData = sensorDataService.findAll();

        /*List<SensorData> sensorData = measuringSpotService.findSensorDataByName(nodeName);*/
        System.out.println();
        List<SensorDataPage> sdp = new ArrayList<>();
        for (SensorData sd : sensorData) {
            //拷贝属性
            SensorDataPage sensorDataPage = new SensorDataPage();
            BeanUtils.copyProperties(sd, sensorDataPage);
            Double calculationValue = 0d;

            //计算原始值
            String parameterData = sd.getData();
            Double value = CommonMethod.calculationParameter(parameterData);

            SensorParameter parameter = sensorParameterService.findBySensourId(sd.getSensorId());
            Sensor sensor = sensorService.findByid(parameter.getSensourId());
            //计算值
            if (parameter != null) {


                if (!"1".equals(sensor.getIsAuxiliarySensor())) {


                    Sensor auxiliarySensor = measuringSpotService.findMeasuringSpotSensorAuxiliarySensorByName("测点1");

                    SensorData auxiliarySensorData = sensorDataService.findBySensorIdAndReceiveTime(auxiliarySensor.getId(), sensorDataPage.getReceiveTime());


                    //没有辅助传感器就不加T0的公式
                    if (auxiliarySensor == null && auxiliarySensorData == null) {
                        calculationValue = parameter.getK() * ((value * value - parameter.getF0() * parameter.getF0()) / 1000) + parameter.getB();

                    } else {
                        //计算原始值
                        String sensorDatas = auxiliarySensorData.getData();
                        Double auxiliarySensorvalue = CommonMethod.calculationParameter(sensorDatas);

                        TemperatureResistance tr = temperatureResistanceService.findByName(auxiliarySensor.getTemperatureResistanceName());
                        //温阻表的电阻排序
                        List<TemperatureResistanceData> data = temperatureResistanceDataService.findByTemperatureResistanceId(tr.getId());
                        Collections.sort(data, new Comparator<TemperatureResistanceData>() {
                            public int compare(TemperatureResistanceData arg0, TemperatureResistanceData arg1) {
                                Double hits0 = Double.valueOf(arg0.getTempResist());
                                Double hits1 = Double.valueOf(arg1.getTempResist());
                                if (hits1 < hits0) {
                                    return 1;
                                } else if (hits1 == hits0) {
                                    return 0;
                                } else {
                                    return -1;
                                }
                            }
                        });


                        //温度查表法
                        Double T = 0.0d;
                        for (int i = 0; i < data.size(); i++) {
                            //查找到第一个大于他的数求差值
                            if (auxiliarySensorvalue / 1000 < Double.valueOf(data.get(i).getTempResist())) {
                                Double t = auxiliarySensorvalue / 1000;
                                Double t2 = Double.valueOf(data.get(i).getTempResist());
                                Double t1 = Double.valueOf(data.get(i + 1).getTempResist());
                                Double a2 = Double.valueOf(data.get(i).getTemp());
                                Double a1 = Double.valueOf(data.get(i + 1).getTemp());

                                T = a1 - ((t1 - t) / (t1 - t2)) * (a1 - a2);
                                break;
                            }
                        }


                        calculationValue = parameter.getK() * ((value * value - parameter.getF0() * parameter.getF0()) / 1000) + parameter.getKt() * (T - parameter.getT0()) + parameter.getB();
                    }

                    if (sensor.getDecimalPoint().equals("2")) {
                        calculationValue = Double.valueOf(new DecimalFormat("#.00").format(calculationValue));
                    }
                }

            }
            sensorDataPage.setSensorNo(sensor.getNo());
            sensorDataPage.setData(calculationValue.toString());
            sensorDataPage.setPrimitiveData(value);
            sdp.add(sensorDataPage);
        }


        return sdp;


    }


    @GetMapping("/sensorGroupData")
    //@RequiresPermissions("system:measuringSpot:sensorGroupData")
    public String groupData(Model model, String nodeName, @EntityParam SensorData SensorData) {

        MeasuringSpot measuringSpot = measuringSpotService.findByName(nodeName);
        // 封装数据

        model.addAttribute("nodeName", nodeName);

        return "/system/measuringSpot/sensorGroupData";


    }


    @GetMapping("/SensorData")
    /*@RequiresPermissions("system:measuringSpot:SensorData")*/
    @ResponseBody
    public TablePage SensorData(String nodeName, Integer page, Integer limit) {

        List<SensorData> sensorData = measuringSpotService.findSensorDataByNamePage(nodeName, page, limit);
        System.out.println();
        List<SensorDataPage> sdp = new ArrayList<>();
        for (SensorData sd : sensorData) {
            //拷贝属性
            SensorDataPage sensorDataPage = new SensorDataPage();
            BeanUtils.copyProperties(sd, sensorDataPage);
            Double calculationValue = 0d;

            //计算原始值
            String parameterData = sd.getData();
            Double value = CommonMethod.calculationParameter(parameterData);

            SensorParameter parameter = sensorParameterService.findBySensourId(sd.getSensorId());
            Sensor sensor = sensorService.findByid(parameter.getSensourId());
            //计算值
            if (parameter != null) {


                if (!"1".equals(sensor.getIsAuxiliarySensor())) {

                    SensorData auxiliarySensorData = new SensorData();
                    Sensor auxiliarySensor = measuringSpotService.findMeasuringSpotSensorAuxiliarySensorByName(nodeName);
                    if (auxiliarySensor != null) {
                        auxiliarySensorData = sensorDataService.findBySensorIdAndReceiveTime(auxiliarySensor.getId(), sensorDataPage.getReceiveTime());
                    }


                    //没有辅助传感器就不加T0的公式
                    if (auxiliarySensor == null) {
                        calculationValue = parameter.getK() * ((value * value - parameter.getF0() * parameter.getF0()) / 1000) + parameter.getB();

                    } else {
                        //计算原始值
                        String sensorDatas = auxiliarySensorData.getData();
                        Double auxiliarySensorvalue = CommonMethod.calculationParameter(sensorDatas);

                        TemperatureResistance tr = temperatureResistanceService.findByName(auxiliarySensor.getTemperatureResistanceName());
                        //温阻表的电阻排序
                        List<TemperatureResistanceData> data = temperatureResistanceDataService.findByTemperatureResistanceId(tr.getId());
                        Collections.sort(data, new Comparator<TemperatureResistanceData>() {
                            public int compare(TemperatureResistanceData arg0, TemperatureResistanceData arg1) {
                                Double hits0 = Double.valueOf(arg0.getTempResist());
                                Double hits1 = Double.valueOf(arg1.getTempResist());
                                if (hits1 < hits0) {
                                    return 1;
                                } else if (hits1 == hits0) {
                                    return 0;
                                } else {
                                    return -1;
                                }
                            }
                        });


                        //温度查表法
                        Double T = 0.0d;
                        for (int i = 0; i < data.size(); i++) {
                            //查找到第一个大于他的数求差值
                            if (auxiliarySensorvalue / 1000 < Double.valueOf(data.get(i).getTempResist())) {
                                Double t = auxiliarySensorvalue / 1000;
                                Double t2 = Double.valueOf(data.get(i).getTempResist());
                                Double t1 = Double.valueOf(data.get(i + 1).getTempResist());
                                Double a2 = Double.valueOf(data.get(i).getTemp());
                                Double a1 = Double.valueOf(data.get(i + 1).getTemp());

                                T = a1 - ((t1 - t) / (t1 - t2)) * (a1 - a2);
                                break;
                            }
                        }


                        calculationValue = parameter.getK() * ((value * value - parameter.getF0() * parameter.getF0()) / 1000) + parameter.getKt() * (T - parameter.getT0()) + parameter.getB();
                    }

                    if (sensor.getDecimalPoint().equals("2")) {
                        calculationValue = Double.valueOf(new DecimalFormat("#.00").format(calculationValue));
                    }
                }

            }
            sensorDataPage.setSensorNo(sensor.getNo());
            sensorDataPage.setData(calculationValue.toString());
            sensorDataPage.setPrimitiveData(value);
            sdp.add(sensorDataPage);
        }

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(measuringSpotService.findSensorDataByName(nodeName).size());
        tablePage.setData(sdp);
        return tablePage;


    }


    @GetMapping("/sensorDatasGroup")
    //@RequiresPermissions("system:measuringSpot:SensorDataGroup")
    @ResponseBody
    public TablePage sensorDataGroup(Long nodeId, Integer page, Integer limit) {


        MeasuringSpot ms = measuringSpotService.getById(nodeId);


        List<MeasuringSpotData> measuringSpotData = measuringSpotDataService.findByMeasuringSpotIdOrderByReceiveTime(ms.getId(), page, limit);


        /*List<MeasuringSpotDataPage> measuringSpotDataPages = new ArrayList<>();
        for (MeasuringSpotData msd: measuringSpotData) {
            MeasuringSpotDataPage measuringSpotDataPage = new MeasuringSpotDataPage();
            BeanUtils.copyProperties(measuringSpotData, measuringSpotDataPage);
            measuringSpotDataPage.setMeasurePattern(ms.getMeasurePattern());
            measuringSpotDataPages.add(measuringSpotDataPage);
        }*/


/*

        //把数据按照时间来分 达到分组的目的
        List<SensorData> sensorData = measuringSpotService.findSensorDataByNamePage(nodeName, page, limit * 2);
        Map<String, List<SensorData>> sensorDataGroupMap = new LinkedHashMap<>();

        for (SensorData s : sensorData) {

            if (sensorDataGroupMap.containsKey(s.getReceiveTime())) {
                sensorDataGroupMap.get(s.getReceiveTime()).add(s);
            } else {


                List<SensorData> dataArr = new ArrayList<>();
                dataArr.add(s);
                sensorDataGroupMap.put(s.getReceiveTime(), dataArr);

            }
        }

        List<SensorDataGroupPage> sdp = new ArrayList<>();
        Iterator<Map.Entry<String, List<SensorData>>> iterator = sensorDataGroupMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<SensorData>> next = iterator.next();

            List<SensorData> dataList = next.getValue();
            List<Double> valueArr = new ArrayList<>();
            SensorDataGroupPage sensorDataPage = new SensorDataGroupPage();
            SensorParameter parameter = null;
            for (int j = 0; j < dataList.size(); j++) {

                //拷贝属性

                BeanUtils.copyProperties(dataList.get(j), sensorDataPage);
                Double calculationValue = 0d;

                //计算原始值
                String parameterData = dataList.get(j).getData();
                Double value = CommonMethod.calculationParameter(parameterData);

                parameter = sensorParameterService.findBySensourId(dataList.get(j).getSensorId());
                Sensor sensor = sensorService.findByid(parameter.getSensourId());
                //计算值
                if (parameter != null) {


                    if (!"1".equals(sensor.getIsAuxiliarySensor())) {


                        Sensor auxiliarySensor = measuringSpotService.findMeasuringSpotSensorAuxiliarySensorByName(nodeName);


                        //没有辅助传感器就不加T0的公式
                        if (auxiliarySensor == null) {
                            calculationValue = parameter.getK() * ((value * value - parameter.getF0() * parameter.getF0()) / 1000) + parameter.getB();
                        } else {
                            SensorData auxiliarySensorData = sensorDataService.findBySensorIdAndReceiveTime(auxiliarySensor.getId(), sensorDataPage.getReceiveTime());
                            //计算原始值
                            String sensorDatas = auxiliarySensorData.getData();
                            Double auxiliarySensorvalue = CommonMethod.calculationParameter(sensorDatas);

                            TemperatureResistance tr = temperatureResistanceService.findByName(auxiliarySensor.getTemperatureResistanceName());
                            //温阻表的电阻排序
                            List<TemperatureResistanceData> data = temperatureResistanceDataService.findByTemperatureResistanceId(tr.getId());
                            Collections.sort(data, new Comparator<TemperatureResistanceData>() {
                                public int compare(TemperatureResistanceData arg0, TemperatureResistanceData arg1) {
                                    Double hits0 = Double.valueOf(arg0.getTempResist());
                                    Double hits1 = Double.valueOf(arg1.getTempResist());
                                    if (hits1 < hits0) {
                                        return 1;
                                    } else if (hits1 == hits0) {
                                        return 0;
                                    } else {
                                        return -1;
                                    }
                                }
                            });


                            //温度查表法
                            Double T = 0.0d;
                            for (int i = 0; i < data.size(); i++) {
                                //查找到第一个大于他的数求差值
                                if (auxiliarySensorvalue / 1000 < Double.valueOf(data.get(i).getTempResist())) {
                                    Double t = auxiliarySensorvalue / 1000;
                                    Double t2 = Double.valueOf(data.get(i).getTempResist());
                                    Double t1 = Double.valueOf(data.get(i + 1).getTempResist());
                                    Double a2 = Double.valueOf(data.get(i).getTemp());
                                    Double a1 = Double.valueOf(data.get(i + 1).getTemp());

                                    T = a1 - ((t1 - t) / (t1 - t2)) * (a1 - a2);
                                    break;
                                }
                            }


                            calculationValue = parameter.getK() * ((value * value - parameter.getF0() * parameter.getF0()) / 1000) + parameter.getKt() * (T - parameter.getT0()) + parameter.getB();
                        }

                       */
/* if (sensor.getDecimalPoint().equals("2")) {
                            calculationValue = Double.valueOf(new DecimalFormat("#.00").format(calculationValue));
                        }*//*

                    }

                }

                if (sensorDataPage.getSensorNo() == null) {
                    sensorDataPage.setSensorNo(sensor.getNo() + ",");
                } else {
                    sensorDataPage.setSensorNo(sensorDataPage.getSensorNo() + sensor.getNo() + ",");
                }

                if (sensorDataPage.getPrimitiveData() == null) {
                    sensorDataPage.setPrimitiveData(value + ",");
                } else {
                    sensorDataPage.setPrimitiveData(sensorDataPage.getPrimitiveData() + value + ",");
                }


                valueArr.add(calculationValue);
            }

            //分组计算值   //C*（A1+A2+...+An)/n
            Double finalValue = 0d;
            for (int i = 0; i < valueArr.size() - 1; i++) {

                finalValue += (parameter.getC() * (valueArr.get(i))) / 1;

            }
            if (ms.getMeasuringSpotSymbol().equals("-1")) {
                finalValue = Double.valueOf(new DecimalFormat("#.00").format(finalValue / valueArr.size())) * -1;
            } else {
                finalValue = Double.valueOf(new DecimalFormat("#.00").format(finalValue / valueArr.size()));
            }
            sensorDataPage.setData(finalValue.toString());
            sdp.add(sensorDataPage);


        }
        //计算速率的值   　v1 = 0  v2 = v2-v1
        for (int i = 1; i < sdp.size()-1; i++) {
            Double value = Double.valueOf(sdp.get(i + 1).getData()) - Double.valueOf(sdp.get(i).getData());
            if(value.equals(0.0d) ){
                sdp.get(i).setSpeed(value.toString());

            }else{
                sdp.get(i).setSpeed(value.toString().substring(0,4));
            }


        }
        sdp.get(0).setSpeed("0");
        sdp.get(sdp.size()-1).setSpeed("0");
*/


        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(measuringSpotDataService.findByMeasuringSpotId(ms.getId()).size());
        tablePage.setData(measuringSpotData);
        return tablePage;


    }


    /**
     * 跳转到列表页面
     */
    @GetMapping("/indexManage")
    /*@RequiresPermissions("system:measuringSpot:index")*/

    public String indexManage(Model model, @EntityParam MeasuringSpot measuringSpot, String nodeName) {

        // 创建匹配器，进行动态查询匹配
        /*ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("No", match -> match.contains());*/

        MeasuringSpot ms = measuringSpotService.findByName(nodeName);
        measuringSpot.setId(ms.getId());

        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("id", ExampleMatcher.GenericPropertyMatchers.contains());

        // 获取采集仪列表
        Example<MeasuringSpot> example = Example.of(measuringSpot, matcher);


        Page<MeasuringSpot> pageList = measuringSpotService.getPageList(example);


        for (MeasuringSpot measuringS : pageList) {
            //测点传感器中间表
            List<MeasuringSpotSensor> measuringSpotSensors = measuringSpotSensorService.getByMeasuringSpotId(measuringS.getId());
            if (measuringSpotSensors != null && !measuringSpotSensors.isEmpty()) {
                String sensorNo = "";
                for (MeasuringSpotSensor mss : measuringSpotSensors) {
                    AcquisitionSensor as = acquisitionSensorService.findByid(mss.getAcquisitionSensorId());
                    Sensor sensor = sensorService.findByid(as.getSensorId());
                    sensorNo += sensor.getNo() + ",";
                }
                measuringS.setSensorNo(sensorNo);
            }
        }


        // 封装数据

        model.addAttribute("list", pageList.getContent());
        model.addAttribute("page", pageList);
        model.addAttribute("node", ms);

        return "/system/measuringSpot/indexManage";
    }


    @GetMapping("/detail")
    //@RequiresPermissions("system:measuringSpot:detail")

    public String toDetail(Long nodeId, Long measuringSpotId, Model model) {
        MeasuringSpot ms = measuringSpotService.getById(measuringSpotId);
        model.addAttribute("ms", ms);
        model.addAttribute("nodeId", nodeId);
        model.addAttribute("measuringSpotId", measuringSpotId);
        return "/system/measuringSpot/detail";
    }


    @GetMapping("/saveMeasuringSpotSensor")
    //@RequiresPermissions("system:measuringSpot:saveMeasuringSpotSensor")
    @ResponseBody
    public ResultVo saveMeasuringSpotSensor(Long cquisitionId, String passagewayNumber, Long measuringSpotId) {

        MeasuringSpotSensor measuringSpotSensor = new MeasuringSpotSensor();


        AcquisitionSensor acquisitionSensor = acquisitionSensorService.getByacquisitionIdAndPassagewayId(cquisitionId, passagewayNumber);


        measuringSpotSensor.setMeasuringSpotId(measuringSpotId);
        measuringSpotSensor.setAcquisitionSensorId(acquisitionSensor.getId());

        List<MeasuringSpotSensor> byMeasuringSpotId = measuringSpotSensorService.getByMeasuringSpotId(measuringSpotId);

        /*if (byMeasuringSpotId.size() > 1) {
            measuringSpotSensor.setDataGroup(byMeasuringSpotId.get(0).getDataGroup());
        } else {
            measuringSpotSensor.setDataGroup(CommonMethod.getNewKey());
        }
*/

        measuringSpotSensorService.save(measuringSpotSensor);


        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 解除测点传感器关联
     *
     * @param cquisitionId
     * @param passagewayNumber
     * @param measuringSpotId
     * @return
     */
    @GetMapping("/delMeasuringSpotSensor")
    //@RequiresPermissions("system:measuringSpot:delMeasuringSpotSensor")
    @ResponseBody
    public ResultVo delMeasuringSpotSensor(Long cquisitionId, String passagewayNumber, Long measuringSpotId) {
        AcquisitionSensor acquisitionSensor = acquisitionSensorService.getByacquisitionIdAndPassagewayId(cquisitionId, passagewayNumber);
        measuringSpotSensorService.removeMeasuringSpotSensorByAcquisitionSensorId(acquisitionSensor.getId());
        return ResultVoUtil.SAVE_SUCCESS;
    }


    /**
     * 测点
     * 传进来一个LISt 先冻结该数据 到数据库查冻结数据前一条数据   和 冻结数据最大时间后面的所有数据
     */

    @PostMapping(value = "/dataAlignment")
    @RequiresPermissions("system:measuringSpot:dataAlignment")
    @ResponseBody

    public ResultVo dataAlignment(@RequestBody List<MeasuringSpotData> dataList) {


        String massage = "操作成功";


        MeasuringSpotData msData = dataList.get(dataList.size() - 1);

        MeasuringSpotData alignmentData = measuringSpotDataService.findFirstByWhetherAlignmentAndReceiveTimeGreaterThan(msData.getReceiveTime(), "0");
        if (alignmentData == null || dataList.size() == 2) {

            //查出两个区间的数据 进行冻结
            List<MeasuringSpotData> theFreezeDataList = measuringSpotDataService.findByIdGreaterThanAndIdLessThan(dataList.get(1).getId(), dataList.get(0).getId());
            //删除错误的数据
            measuringSpotDataService.deleteAll(theFreezeDataList);

            //获取计算值
            Double calculatedValue = dataList.get(1).getMeasuringSpotValue() - dataList.get(0).getMeasuringSpotValue();


            MeasuringSpot measuringSpot = measuringSpotService.getById(dataList.get(0).getMeasuringSpotId());
            measuringSpot.setCalculatedValue(calculatedValue);
            measuringSpotService.update(measuringSpot);


            //MeasuringSpotData msdSmall = dataList.get(dataList.size() - 1);
            //MeasuringSpotData FrontMeasuringSpotData = measuringSpotDataService.findFirstByReceiveTimeLessThanOrderByIdDesc(msdSmall.getReceiveTime());


            MeasuringSpotData FrontMeasuringSpotData = dataList.get(0);
            FrontMeasuringSpotData.setMeasuringSpotValue(dataList.get(1).getMeasuringSpotValue());
            FrontMeasuringSpotData.setMeasuringSpotAccumulationValue(dataList.get(1).getMeasuringSpotAccumulationValue());
            FrontMeasuringSpotData.setMeasuringSpotDifference(dataList.get(1).getMeasuringSpotDifference());

            MeasuringSpotData spotDate = measuringSpotDataService.findByReceiveTime(CommonMethod.getTimeMinuteAdditionAndSubtraction(FrontMeasuringSpotData.getReceiveTime(), 1438L));
            Long date = CommonMethod.getDatePoor(spotDate.getReceiveTime(), FrontMeasuringSpotData.getReceiveTime());
            double dayValue = Double.valueOf(new DecimalFormat("#.00").format((FrontMeasuringSpotData.getMeasuringSpotValue() - spotDate.getMeasuringSpotValue()) / (date / 1440)));

            FrontMeasuringSpotData.setDayRateValue(dayValue);

            FrontMeasuringSpotData.setWhetherAlignment("0");
            measuringSpotDataService.update(FrontMeasuringSpotData);

            List<MeasuringSpotData> measuringSpotData = measuringSpotDataService.findByReceiveTimeGreaterThan(FrontMeasuringSpotData.getReceiveTime());
            for (MeasuringSpotData msd : measuringSpotData) {
                //对齐后的所有数据都要加上这个计算值
                msd.setMeasuringSpotValue(Double.valueOf(new DecimalFormat("#.00").format(msd.getMeasuringSpotValue() + calculatedValue)));

                MeasuringSpotData spotData = measuringSpotDataService.findByReceiveTime(CommonMethod.getTimeMinuteAdditionAndSubtraction(msd.getReceiveTime(), 1438L));
                Long datePoor = CommonMethod.getDatePoor(spotData.getReceiveTime(), msd.getReceiveTime());
                double dayRateValue = Double.valueOf(new DecimalFormat("#.00").format((msd.getMeasuringSpotValue() - spotData.getMeasuringSpotValue()) / (datePoor / 1440)));

                msd.setDayRateValue(dayRateValue);
                measuringSpotDataService.update(msd);
            }


            for (int i = 0; i < measuringSpotData.size(); i++) {
                if (i == 0) {
                    //差值
                    double DifferenceValue = measuringSpotData.get(i).getMeasuringSpotValue() - FrontMeasuringSpotData.getMeasuringSpotValue();
                    //精确到两位 四舍五入
                    DifferenceValue = Double.valueOf(new DecimalFormat("#.00").format(DifferenceValue));
                    measuringSpotData.get(i).setMeasuringSpotDifference(DifferenceValue);
                    measuringSpotData.get(i).setMeasuringSpotAccumulationValue(Double.valueOf(new DecimalFormat("#.00").format(FrontMeasuringSpotData.getMeasuringSpotAccumulationValue() + DifferenceValue)));
                } else {

                    double DifferenceValue = measuringSpotData.get(i).getMeasuringSpotValue() - measuringSpotData.get(i - 1).getMeasuringSpotValue();
                    //精确到两位 四舍五入
                    DifferenceValue = Double.valueOf(new DecimalFormat("#.00").format(DifferenceValue));
                    measuringSpotData.get(i).setMeasuringSpotAccumulationValue(Double.valueOf(new DecimalFormat("#.00").format(measuringSpotData.get(i - 1).getMeasuringSpotAccumulationValue() + DifferenceValue)));
                }

            }
            measuringSpotDataService.updateAll(measuringSpotData);


        } else {
            massage = "不允许该操作";
        }


        return ResultVoUtil.success();
    }

    /**
     * 修改测点信息
     *
     * @param measuringSpot
     * @return
     */
    @RequestMapping("/updateMeasuringSpot")
    //@RequiresPermissions("system:measuringSpot:updateMeasuringSpot")
    @ResponseBody
    public ResultVo updateMeasuringSpot(MeasuringSpot measuringSpot) {


        MeasuringSpot used = measuringSpotService.getById(measuringSpot.getId());
        if (!used.getName().equals(measuringSpot.getName())) {
            List<Menu> menuList = menuService.findAllByTitleContaining(used.getName());
            for (Menu menu : menuList) {
                if (menu.getUrl().indexOf("index") != -1) {
                    menu.setUrl("/system/measuringSpot/index?nodeName=" + measuringSpot.getName());
                } else {
                    menu.setUrl("/system/measuringSpot/indexManage?nodeName=" + measuringSpot.getName());
                }

                menuService.save(menu);
            }
        }
        if (!used.getName().equals(measuringSpot.getName())) {
            List<Menu> menuList = menuService.findAllByTitleContaining(used.getName());
            for (Menu menu : menuList) {
                menu.setTitle(measuringSpot.getName());
            }
            if (menuList.size() > 0 && !CollectionUtils.isEmpty(menuList)) {
                menuService.updateBatch(menuList);
            }
        }
        measuringSpotService.update(measuringSpot);
        return ResultVoUtil.success("修改成功");
    }

    /**
     * 跳转到修改测点信息页面
     *
     * @param id
     * @return
     */
    @GetMapping("/toEditMeasuringSpot")
    //@RequiresPermissions("system:measuringSpot:toEditMeasuringSpot")
    public String toEditMeasuringSpot(Long id, Model model) {


        MeasuringSpot ms = measuringSpotService.findById(id);
        WorkSpot workSpot = workSpotService.getById(ms.getWorkSpotId());

        BidSection bidSection = bidSectionService.getById(workSpot.getBidSectionId());
        LineInfo lineInfo = lineInfoService.getById(bidSection.getLineInfoId());

        workSpot.setNo(lineInfo.getNo() + bidSection.getNo() + workSpot.getNo());


        if (ms != null) {
            model.addAttribute("ms", ms);
            model.addAttribute("workSpot", workSpot);
        }
        return "/system/menu/editMeasuringSpot";
    }


    @RequestMapping("/exportExcel")
    public void export(HttpServletResponse response, Long measuringSpotId, String startTime, String endTime) {
        List<MeasuringSpotData> measuringSpotData = new ArrayList<>();

        //没有传时间 就是导出全部（导出15天 这个待议）
        if (StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {
            measuringSpotData = measuringSpotDataService.findByMeasuringSpotId(measuringSpotId);
        } else if (!StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {//传了开始时间
            measuringSpotData = measuringSpotDataService.findByMeasuringSpotIdAndReceiveTimeGreaterThan(measuringSpotId, startTime);
        } else {//根据传了时间来
            measuringSpotData = measuringSpotDataService.findByMeasuringSpotIdAndReceiveTimeGreaterThanAndReceiveTimeLessThan(measuringSpotId, startTime, endTime + " 23:59:59");
        }

        MeasuringSpot measuringSpot = measuringSpotService.getById(measuringSpotId);
        //从数据库查询出数据

        // 创建excel
        HSSFWorkbook wk = new HSSFWorkbook();
        // 创建一张工作表
        HSSFSheet sheet = wk.createSheet("测点数据表");
        // 设置工作表中的1-3列的宽度
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);

        //创建第一行
        HSSFRow row1 = sheet.createRow(0);
        // 创建第一行的第一个单元格
        // 向单元格写值
        HSSFCell cell = row1.createCell(0);
        cell.setCellValue("测点数据表");
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
        for (int i = 0; i < measuringSpotData.size(); i++) {
            //创建行    一条数据一行
            HSSFRow row = sheet.createRow(i + 2);
            row.createCell(0).setCellValue(measuringSpotData.get(i).getMeasuringSpotSensorNo());
            row.createCell(1).setCellValue(measuringSpotData.get(i).getMeasuringSpotOriginalValue());
            row.createCell(2).setCellValue(measuringSpotData.get(i).getMeasuringSpotValue());
            row.createCell(3).setCellValue(measuringSpotData.get(i).getDayRateValue());
            row.createCell(4).setCellValue(measuringSpotData.get(i).getMeasuringSpotDifference());
            row.createCell(5).setCellValue(measuringSpotData.get(i).getMeasuringSpotAccumulationValue());
            row.createCell(6).setCellValue(measuringSpotData.get(i).getReceiveTime());
        }
        try {
            /**
             * 弹出下载选择路径框
             */
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String str = sdf.format(date);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(measuringSpot.getName().getBytes("gb2312"), "ISO8859-1") + ":" + startTime + "-" + endTime + ".xls");
            response.flushBuffer();
            wk.write(response.getOutputStream());
            // wk.write(new FileOutputStream(new File("D://daochu")));
            //wk.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @RequestMapping("/toEcharts")
    public String toEcharts(String startTime, String endTime, Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        return "/system/measuringSpot/echarts";
    }


    @RequestMapping("/echartsData")
    @ResponseBody
    public List<MeasuringSpotData> echartsData(String startTime, String endTime, Long id) {


        List<MeasuringSpotData> measuringSpotDataList = new ArrayList<>();

        //如果没有传时间那就是获取前十五天的数据
        //传了时间  传了开始时间
        if (StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {//没有传时间
            measuringSpotDataList = measuringSpotDataService.findByMeasuringSpotId(id);
        }else if(!StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)){//传了结束时间
            measuringSpotDataList = measuringSpotDataService.findByMeasuringSpotIdAndReceiveTimeGreaterThan(id, startTime);
        }else if(StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)){//传了结束时间
            measuringSpotDataList = measuringSpotDataService.findByMeasuringSpotIdAndReceiveTimeLessThan(id, endTime+ " 23:59:59");
        }else{
            measuringSpotDataList = measuringSpotDataService.findByMeasuringSpotIdAndReceiveTimeGreaterThanAndReceiveTimeLessThan(id, startTime, endTime+ " 23:59:59");
        }


        return measuringSpotDataList;
    }


    /**
     * @param entityId          采集仪ID
     * @param id                采集仪通道
     * @param acquisitionNumber 序号
     * @return
     */
    @RequestMapping("/group")
    @ResponseBody
    public ResultVo group(Long entityId, String id, Integer acquisitionNumber, String measuringSpotName) {

        AcquisitionSensor acquisitionSensor = acquisitionSensorService.getByacquisitionIdAndPassagewayId(entityId, id);

        MeasuringSpotSensor measuringSpotSensor = measuringSpotSensorService.findByAcquisitionSensorId(acquisitionSensor.getId());

        measuringSpotSensor.setAcquisitionNumber(acquisitionNumber);
        measuringSpotSensor.setDataGroup(measuringSpotName);
        measuringSpotSensorService.saveAndFlush(measuringSpotSensor);
        return ResultVoUtil.SAVE_SUCCESS;
    }







}

























