package com.linln.admin.system.controller;


import com.linln.admin.system.common.CommonMethod;
import com.linln.admin.system.common.socket.ChannelInfo;
import com.linln.admin.system.common.socket.HandlerHandlerSelectionKeyImpl;
import com.linln.admin.system.validator.SendCodeValid;
import com.linln.common.constant.AdminConst;
import com.linln.common.enums.StatusEnum;

import com.linln.common.utils.CommonUtil;

import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;

import com.linln.component.actionLog.annotation.EntityParam;

import com.linln.component.shiro.ShiroUtil;
import com.linln.component.thymeleaf.utility.DictUtil;
import com.linln.devtools.generate.utils.jAngel.utils.StringUtil;
import com.linln.modules.system.Common.MapEntity;
import com.linln.modules.system.Common.MapUpgradeEntity;
import com.linln.modules.system.page.TablePage;
import com.linln.modules.system.domain.*;

import com.linln.modules.system.service.*;

import fr.opensagres.xdocreport.core.utils.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Action;
import java.util.*;

import static com.linln.admin.system.common.CommonMethod.getCode;
import static com.linln.admin.system.common.socket.HandlerHandlerSelectionKeyImpl.channelList;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/acquisitionInstrument")
public class AcquisitionInstrumentController {


    @Autowired
    private AcquisitionInstrumentService acquisitionService;


    @Autowired
    private AcquisitionSensorService acquisitionSensorService;


    @Autowired
    private SensorService sensorService;

    @Autowired
    private MeasuringSpotSensorService measuringSpotSensorService;

    @Autowired
    private MeasuringSpotService measuringSpotService;

    @Autowired
    private NetWorkService netWorkService;

    @Autowired
    private WorkSpotService workSpotService;


    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:acquisitionInstrument:index")
    public String index(Model model, @EntityParam AcquisitionInstrument acquisitionInstrument) {
        Page<AcquisitionInstrument> pageList = acquisitionService.getPageList();
        // 封装数据
        model.addAttribute("list", pageList.getContent());
        model.addAttribute("page", pageList);

        return "/system/acquisitionInstrument/index";
    }


    /**
     * 查询用户的采集仪
     */
    @RequestMapping("/userAcquisition")
    /* @RequiresPermissions("system:acquisitionInstrument:userAcquisition")*/
    @ResponseBody
    public TablePage userAcquisition(String no, Integer page, Integer limit) {

        List<AcquisitionInstrument> dataList = new ArrayList<>();
        int total = 0;
        //获取当前用户
        User user = ShiroUtil.getSubject();
        //判断是不是超级管理员 是超级管理员那么久可以看到全部,不是就只能看到自己的和没有关联工点的采集仪


        if (user.getId().equals(AdminConst.ADMIN_ID)) {
            if (no != null && !"".equals(no)) {
                Page<AcquisitionInstrument> acquisitionInstrumentPage = acquisitionService.findByNoLike(no, page, limit);
                total = Integer.valueOf(String.valueOf(acquisitionInstrumentPage.getTotalElements()));
                dataList = acquisitionInstrumentPage.getContent();
            } else {
                Page<AcquisitionInstrument> acquisitionInstrumentPage = acquisitionService.findAll(page, limit);
                total = Integer.valueOf(String.valueOf(acquisitionInstrumentPage.getTotalElements()));
                dataList = acquisitionInstrumentPage.getContent();
            }

        } else {
            if (no != null && !"".equals(no)) {
                Page<AcquisitionInstrument> acquisitionInstrumentPage = acquisitionService.findUserAcquisitionByNo(user.getId(), no, page, limit);
                total = Integer.valueOf(String.valueOf(acquisitionInstrumentPage.getTotalElements()));
                dataList = acquisitionInstrumentPage.getContent();
            } else {
                Page<AcquisitionInstrument> acquisitionInstrumentPage = acquisitionService.findUserAcquisitionByNo(user.getId(), no, page, limit);
                total = Integer.valueOf(String.valueOf(acquisitionInstrumentPage.getTotalElements()));
                dataList = acquisitionInstrumentPage.getContent();
            }
        }
        for (AcquisitionInstrument ai : dataList) {
            if (ai.getNetWorkId() != null && !"".equals(ai.getNetWorkId())) {
                ai.setNetWorkId(netWorkService.getById(Long.valueOf(ai.getNetWorkId())).getRegisterSSID());
            }
        }
// TODO: 2019/2/25 菜单类型处理方案
        for (AcquisitionInstrument acquisitionInstrument : dataList) {
            if (acquisitionInstrument.getCommunicationProtocol() != null && !"".equals(acquisitionInstrument.getCommunicationProtocol())) {
                acquisitionInstrument.setCommunicationProtocol(DictUtil.keyValue("COMMUNICATION_PROTOCOL", acquisitionInstrument.getCommunicationProtocol()));
            }
        }


        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(total);
        tablePage.setData(dataList);
        return tablePage;
    }


    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    /*@RequiresPermissions("system:acquisitionInstrument:add")*/
    public String toAdd() {
        return "/system/acquisitionInstrument/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    /*@RequiresPermissions("system:acquisitionInstrument:edit")*/
    public String toEdit(@PathVariable("id") AcquisitionInstrument acquisitionInstrument, Model model) {
        acquisitionInstrument.setPassagewayNumber(acquisitionInstrument.getPassagewayNumber().substring(acquisitionInstrument.getPassagewayNumber().length() - 2));
        /*if (acquisitionInstrument.getCommunicationProtocol() != null && !"".equals(acquisitionInstrument.getCommunicationProtocol())) {
            acquisitionInstrument.setCommunicationProtocol(DictUtil.keyValue("COMMUNICATION_PROTOCOL", acquisitionInstrument.getCommunicationProtocol()));
        }*/
        model.addAttribute("edit", "edit");
        model.addAttribute("acquisition", acquisitionInstrument);
        return "/system/acquisitionInstrument/add";
    }


    /**
     * 保存添加/修改的数据
     */
    @PostMapping({"/add", "/edit"})
    /*@RequiresPermissions({"system:acquisitionInstrument:add", "system:acquisitionInstrument:edit"})*/
    @ResponseBody
    public ResultVo save(/*@Validated DictValid valid,*/ @EntityParam AcquisitionInstrument acquisition) {

        if (acquisition.getId() != null && acquisition.getId() > 0) {//修改
            AcquisitionInstrument send = acquisitionService.findByid(acquisition.getId());
            acquisition.setWorkSpotId(send.getWorkSpotId());

            if (acquisition.getPassagewayNumber().length() > 4) {
                acquisition.setPassagewayNumber(acquisition.getPassagewayNumber().substring(acquisition.getPassagewayNumber().length() - 2, acquisition.getPassagewayNumber().length()));
            }
            acquisition.setPassagewayNumber(CommonUtil.passagewayData(Integer.valueOf(acquisition.getPassagewayNumber())));
            //没有就保存有就刷新
            acquisitionService.saveAndFlush(acquisition);
        } else {
            acquisition.setPassagewayNumber(CommonUtil.passagewayData(Integer.valueOf(acquisition.getPassagewayNumber())));
            acquisition.setIntervalstate(0);
            //没有就保存有就刷新
            acquisitionService.save(acquisition);

        }
        return ResultVoUtil.SAVE_SUCCESS;

    }


    /**
     * 修改的数据
     */
    @PostMapping("/editInterval")
    /*@RequiresPermissions({"system:acquisitionInstrument:add", "system:acquisitionInstrument:edit"})*/
    @ResponseBody
    public ResultVo editInterval(/*@Validated DictValid valid,*/ @EntityParam AcquisitionInstrument acquisition) {
        //没有就保存有就刷新
        acquisitionService.updateInterval(acquisition.getId(), acquisition.getIntervalTime());
        return ResultVoUtil.SAVE_SUCCESS;

    }


    /**
     *
     */
    @GetMapping("/detail/{id}")
    /*@RequiresPermissions("system:acquisitionInstrument:detail")*/
    public String toDetail(@PathVariable("id") Long id, Model model) {
        AcquisitionInstrument acquisition = acquisitionService.findByid(id);
        AcquisitionInstrument ai = new AcquisitionInstrument();
        BeanUtils.copyProperties(acquisition, ai);
        model.addAttribute("acquisition", ai);
        return "/system/acquisitionInstrument/detail";
    }


    /**
     *
     */
    @GetMapping("/detail")
    /*@RequiresPermissions("system:acquisitionInstrument:detail")*/
    public String detail(String acquisitionNo, Model model) {
        AcquisitionInstrument acquisition = acquisitionService.findByNo(acquisitionNo);
        model.addAttribute("acquisition", acquisition);
        return "/system/acquisitionInstrument/detail";
    }


    @GetMapping("/sensorData/")
    /*@RequiresPermissions("system:acquisitionInstrument:detail")*/
    public String toDetail(Model model) {
        List<Sensor> all = sensorService.findAll();
        model.addAttribute("acquisition", all);
        return "/system/acquisitionInstrument/detail";
    }


    /**
     * 设置一条或者多条数据的状态
     */
    @GetMapping("/delete/{id}")
    /*@RequiresPermissions("system:acquisitionInstrument:detail")*/
    @ResponseBody
    public ResultVo delete(@PathVariable("id") AcquisitionInstrument acquisition) {


        if (acquisition.getId() != null && acquisition.getId() > 0) {


            List<AcquisitionSensor> acquisitionSensors = acquisitionSensorService.getByacquisitionId(acquisition.getId());
            List<MeasuringSpotSensor> measuringSpotSensorList = new ArrayList<>();
            for (AcquisitionSensor acquisitionSensor : acquisitionSensors) {

                MeasuringSpotSensor measuringSpotSensor = measuringSpotSensorService.findByAcquisitionSensorId(acquisitionSensor.getId());
                if (measuringSpotSensor != null) {
                    measuringSpotSensorList.add(measuringSpotSensor);
                }

            }
            //删除测点对应的传感器
            if (measuringSpotSensorList.size() > 0 && !CollectionUtils.isEmpty(measuringSpotSensorList)) {
                measuringSpotSensorService.deleteBatch(measuringSpotSensorList);
            }
            //删除采集仪对应的传感器
            if (acquisitionSensors.size() > 0 && !CollectionUtils.isEmpty(acquisitionSensors)) {
                acquisitionSensorService.deleteAll(acquisitionSensors);
            }

            //删除采集仪
            acquisitionService.deleteById(acquisition.getId());


        }


        return ResultVoUtil.success("删除成功");


    }


    /**
     * 查询全部采集仪
     */
    @GetMapping("/findAll")
    /*@RequiresPermissions("system:acquisitionInstrument:findAll")*/
    @ResponseBody
    public List<AcquisitionInstrument> findAll() {
        List<AcquisitionInstrument> all = acquisitionService.findAll();
        return all;
    }


    /**
     * 查询全部采集仪
     */
    @GetMapping("/findAllTable")
    /*@RequiresPermissions("system:acquisitionInstrument:findAll")*/
    @ResponseBody
    public TablePage findAllTable(Long id) {//采集仪编号

        List<MapUpgradeEntity> map = new ArrayList<>();
        if (id != null && !"".equals(id)) {
            //采集仪
            AcquisitionInstrument acquisitionInstrument = acquisitionService.findByid(id);
            //中间表
            List<AcquisitionSensor> acquisitionSensor = acquisitionSensorService.getByacquisitionId(acquisitionInstrument.getId());
            String[] split = acquisitionInstrument.getPassagewayNumber().split(",");
            List<String> nameList = Arrays.asList(split);

            for (int i = 0; i < nameList.size(); i++) {
                MapUpgradeEntity mapEntity = new MapUpgradeEntity();
                Integer y = i + 1;
                mapEntity.setId(y);
                mapEntity.setEntityId(acquisitionInstrument.getId());
                mapEntity.setName(y.toString());
                map.add(mapEntity);
            }
            if (acquisitionSensor != null && !acquisitionSensor.isEmpty()) {
                for (AcquisitionSensor as : acquisitionSensor) {
                    //查询传感器编号
                    Sensor sensor = sensorService.findByid(as.getSensorId());
                    map.get(Integer.valueOf(as.getPassagewayId()) - 1).setValue(sensor.getNo());
                }
            }
        }
        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(map.size());
        tablePage.setData(map);
        return tablePage;
    }


    /**
     * 查询测点下的采集仪通道
     */
    @GetMapping("/findAacquisition")
    //@RequiresPermissions("system:acquisitionInstrument:findAll")
    @ResponseBody
    public TablePage findAacquisition(String acquisitionNo, String measuringSpotName) {//采集仪编号
        List<MapUpgradeEntity> data = new ArrayList<>();//其他绑定了的
        if (acquisitionNo != null && measuringSpotName != null && !"".equals(measuringSpotName) && !"".equals(acquisitionNo)) {

            //采集仪
            AcquisitionInstrument acquisitionInstrument = acquisitionService.findByNo(acquisitionNo);
            //中间表
            List<AcquisitionSensor> acquisitionSensor = acquisitionSensorService.getByacquisitionId(acquisitionInstrument.getId());

            String[] split = acquisitionInstrument.getPassagewayNumber().split(",");

            List<String> nameList = Arrays.asList(split);

            List<MapUpgradeEntity> map = new ArrayList<>();

            List<MapUpgradeEntity> bindingOther = new ArrayList<>();//其他绑定了的
            List<MapUpgradeEntity> unbound = new ArrayList<>();//未绑定
            List<MapUpgradeEntity> bindingOneself = new ArrayList<>();//绑定自己的

            for (int i = 0; i < nameList.size(); i++) {
                MapUpgradeEntity mapEntity = new MapUpgradeEntity();
                Integer y = i + 1;
                mapEntity.setId(y);
                mapEntity.setEntityId(acquisitionInstrument.getId());
                mapEntity.setName(y.toString());
                map.add(mapEntity);
            }

            if (acquisitionSensor != null && !acquisitionSensor.isEmpty()) {
                for (AcquisitionSensor as : acquisitionSensor) {
                    //查询传感器编号
                    Sensor sensor = sensorService.findByid(as.getSensorId());

                    MapUpgradeEntity mapUpgradeEntity = map.get(Integer.valueOf(as.getPassagewayId()) - 1);
                    mapUpgradeEntity.setValue(sensor.getNo());


                    MeasuringSpotSensor mss = measuringSpotSensorService.findByAcquisitionSensorId(as.getId());

                    if (mss != null) {
                        mapUpgradeEntity.setAcquisitionNumber(mss.getAcquisitionNumber());
                        MeasuringSpot measuringSpot = measuringSpotService.findById(mss.getMeasuringSpotId());
                        if (measuringSpot != null) {
                            mapUpgradeEntity.setMeasuringSpotName(measuringSpot.getName());
                        }
                        mapUpgradeEntity.setIsBinding(1);
                        if (measuringSpot.getName().equals(measuringSpotName)) {
                            bindingOneself.add(mapUpgradeEntity);
                        } else {
                            bindingOther.add(mapUpgradeEntity);
                        }
                    } else {
                        unbound.add(mapUpgradeEntity);
                        mapUpgradeEntity.setIsBinding(0);
                    }
                }
            }
            data.addAll(bindingOneself);
            data.addAll(unbound);
            data.addAll(bindingOther);

        }

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(data.size());
        tablePage.setData(data);
        return tablePage;
    }


    /**
     * 根据ID获取采集仪
     */
    @GetMapping("/findOne")
    /*@RequiresPermissions("system:acquisitionInstrument:findOne")*/
    @ResponseBody
    public List<MapEntity> findOne(Long id) {


        List<MapEntity> list = new ArrayList<>();
        if (id != null && id > 0) {
            AcquisitionInstrument all = acquisitionService.findByid(id);
            //中间表信息
            List<AcquisitionSensor> acquisitionSensor = acquisitionSensorService.getByacquisitionId(id);
            String[] split = all.getPassagewayNumber().split(",");
            List<String> nameList = Arrays.asList(split);
            for (int i = 0; i < nameList.size(); i++) {
                MapEntity mapEntity = new MapEntity();
                int y = i + 1;
                mapEntity.setId(y);
                mapEntity.setName("通道" + y + "==>未使用");
                list.add(mapEntity);
            }
            if (acquisitionSensor != null && !acquisitionSensor.isEmpty()) {
                for (AcquisitionSensor as : acquisitionSensor) {
                    //查询传感器编号
                    Sensor sensor = sensorService.findByid(as.getSensorId());
                    list.get(Integer.valueOf(as.getPassagewayId())).setName("通道" + Integer.valueOf(as.getPassagewayId()) + "==>" + sensor.getNo());
                }
            }
        }
        return list;
    }


    @RequestMapping("/status/{param}")
    /* @RequiresPermissions("system:acquisitionInstrument:status")*/
    @ResponseBody
    /*@ActionLog(name = "字典状态", action = StatusAction.class)*/
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (acquisitionService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }


    /**
     * 采集仪绑定传感器
     */
    @PostMapping("/saveAcquisition")
    /*    @RequiresPermissions("system:acquisitionInstrument:saveAcquisition")*/
    @ResponseBody
    public ResultVo saveAcquisition(@EntityParam AcquisitionInstrument acquisitionInstrument, String sensorNo) {


        //创建中间表关系达到关联
        /*sensorService*/
        if (sensorNo != null && !"".equals(sensorNo)) {
            Sensor sen = sensorService.getByNo(sensorNo);

            AcquisitionSensor acquisitionSensor = new AcquisitionSensor();

            acquisitionSensor.setSensorId(sen.getId());
            acquisitionSensor.setPassagewayId(acquisitionInstrument.getPassagewayNumber());
            acquisitionSensor.setAcquisitionId(acquisitionInstrument.getId());

            acquisitionSensorService.save(acquisitionSensor);
        }
        /*return ResultVoUtil.success("传感器绑定完成");*/
        return ResultVoUtil.SAVE_SUCCESS;

    }


    /**
     * 跳转到添加传感器的页面
     */
    @GetMapping("/toAcquisitionSensor")
    /*@RequiresPermissions("system:acquisitionInstrument:toAcquisitionSensor")*/

    public String toAcquisitionSensor(Long cquisitionId, String passagewayNumber, Model model) {
        //创建中间表关系达到关联
        /*sensorService*/
        model.addAttribute("cquisitionId", cquisitionId);
        model.addAttribute("passagewayNumber", passagewayNumber);

        /*return ResultVoUtil.success("传感器绑定完成");*/
        return "/system/acquisitionInstrument/distributionSensor";

    }


    /**
     * 采集仪添加传感器
     */
    @PostMapping("/saveAcquisitionSensor")
    /*    @RequiresPermissions("system:acquisitionInstrument:saveAcquisitionSensor")*/
    @ResponseBody
    public ResultVo saveAcquisitionSensor(Long cquisitionId, String passagewayNumber, String sensorNo) {
        if (sensorNo != null && !"".equals(sensorNo)) {
            Sensor sensor = sensorService.getByNo(sensorNo);
            //创建中间表关系达到关联
            AcquisitionSensor acquisitionSensor = new AcquisitionSensor();
            acquisitionSensor.setAcquisitionId(cquisitionId);
            acquisitionSensor.setPassagewayId(passagewayNumber);
            acquisitionSensor.setSensorId(sensor.getId());
            acquisitionSensorService.save(acquisitionSensor);
        }
        return ResultVoUtil.SAVE_SUCCESS;

    }


    /**
     * 查询全部采集仪
     */
    @GetMapping("/findWorkSpotAcquisition")
    // @RequiresPermissions("system:acquisitionInstrument:findWorkSpotAcquisition")
    @ResponseBody
    public List<AcquisitionInstrument> findWorkSpotAcquisition(Long nodeId, Long measuringSpotId) {//nodeId 工点ID

        List<AcquisitionInstrument> acquisitionInstruments = new ArrayList<>();


        List<MeasuringSpotSensor> measuringSpotSensorList = measuringSpotSensorService.getByMeasuringSpotId(measuringSpotId);
        if (measuringSpotSensorList.size() > 0) {

            //通过测点ID 查询测点关联的采集仪

            acquisitionInstruments = acquisitionService.findByWorkSpotIdAll(nodeId, measuringSpotId);
        } else {
            acquisitionInstruments = acquisitionService.findByWorkSpotId(nodeId);
        }
        return acquisitionInstruments;
    }


    /**
     * 删除关联
     */
    @GetMapping("/delAcquisitionSensor")
    /*@RequiresPermissions("system:acquisitionInstrument:delAcquisitionSensor")*/
    @ResponseBody
    public ResultVo delAcquisitionSensor(Long cquisitionId, String passagewayNumber) {

        if (cquisitionId != null && cquisitionId > 0 && passagewayNumber != null && !"".equals(passagewayNumber)) {
            AcquisitionSensor ac = acquisitionSensorService.getByacquisitionIdAndPassagewayId(cquisitionId, passagewayNumber);
            //删除测点和传感器的关联
            measuringSpotSensorService.removeMeasuringSpotSensorByAcquisitionSensorId(ac.getId());
            //删除采集仪传感器关联
            acquisitionSensorService.removeAcquisitionSensorById(ac.getId());
        }

        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 查询采集仪对应的工点信息和对应的传感器信息
     */
    @GetMapping("/findWorkSpotAcquisitionAndSensor")
    // @RequiresPermissions("system:acquisitionInstrument:findWorkSpotAcquisition")
    @ResponseBody
    public ResultVo findWorkSpotAcquisitionAndSensor(Long id) {
        String msg = "";
        AcquisitionInstrument acquisitionInstrument = acquisitionService.findByid(id);
        if (acquisitionInstrument != null) {
            WorkSpot workSpot = workSpotService.findById(acquisitionInstrument.getWorkSpotId());
            List<AcquisitionSensor> acquisitionSensors = acquisitionSensorService.getByacquisitionId(id);
            if (workSpot != null && acquisitionSensors != null && !CollectionUtils.isEmpty(acquisitionSensors)) {
                msg = "该采集仪已经关联工点名为[" + workSpot.getName() + "]==>并且已绑定传感器是否确认删除";
            } else if (workSpot != null) {
                msg = "该采集仪已经关联工点名为[" + workSpot.getName() + "]是否确认删除";
            } else if (acquisitionSensors != null && !CollectionUtils.isEmpty(acquisitionSensors)) {
                msg = "该采集仪下已绑定传感器是否确认删除";
            } else {
                msg = "是否确认删除?";
            }

        }

        return ResultVoUtil.success(msg);
    }


    /**
     * 发送命令
     */
    @GetMapping("/sendCommand")
    /*@RequiresPermissions("system:acquisitionInstrument:findAll")*/
    @ResponseBody
    public ResultVo sendCommand(Integer code, Long id, Integer openingOrClos) {

        AcquisitionInstrument acquisitionInstrument = acquisitionService.findByid(id);

        NetWork netWork = netWorkService.getById(Long.valueOf(acquisitionInstrument.getNetWorkId()));

        HandlerHandlerSelectionKeyImpl.sendData(netWork.getRegisterSSID(), getCode(code, CommonMethod.intToHex(Integer.valueOf(acquisitionInstrument.getNo())), acquisitionInstrument.getIntervalTime(), openingOrClos));

        acquisitionInstrument.setIntervalstate(openingOrClos);
        acquisitionService.saveAndFlush(acquisitionInstrument);
        //BB1081C41F01010000000359
        return ResultVoUtil.success("操作成功");
    }


    /**
     * 发送命令
     */
    @GetMapping("/getOnlineState")
    /*@RequiresPermissions("system:acquisitionInstrument:findAll")*/
    @ResponseBody
    public ResultVo getOnlineState(Long id) {
        AcquisitionInstrument acquisitionInstrument = acquisitionService.findByid(id);
        if(StringUtils.isEmpty(acquisitionInstrument.getNetWorkId())){
            return ResultVoUtil.error("该采集仪没有关联4G模块");
        }
        NetWork netWork = netWorkService.getById(Long.valueOf(acquisitionInstrument.getNetWorkId()));

        int flag = 0;
        for (ChannelInfo channelInfo : channelList) {
            if (netWork.getRegisterSSID().equals(channelInfo.getNetWork())) {
                flag = 1;
            }
        }

        return ResultVoUtil.success(flag);
    }


}
