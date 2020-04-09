package com.linln.admin.system.controller;

import com.linln.admin.system.validator.DictValid;
import com.linln.common.constant.AdminConst;
import com.linln.common.enums.ResultEnum;
import com.linln.common.enums.StatusEnum;
import com.linln.common.exception.ResultException;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.action.SaveAction;
import com.linln.component.actionLog.action.StatusAction;
import com.linln.component.actionLog.annotation.ActionLog;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.component.shiro.ShiroUtil;
import com.linln.component.thymeleaf.utility.DictUtil;
import com.linln.modules.system.domain.*;
import com.linln.modules.system.page.TablePage;
import com.linln.modules.system.service.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/sensor")
public class SensorController {



    @Autowired
    private SensorService sensorService;


    @Autowired
    private SensorParameterService sensorParameterService;

    @Autowired
    private TemperatureResistanceService temperatureResistanceService;


    @Autowired
    private AcquisitionSensorService acquisitionSensorService;

    @Autowired
    private AcquisitionInstrumentService acquisitionInstrumentService;


    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:sensor:index")
    public String index(Model model, Sensor sensor,String nodeName){





        // 创建匹配器，进行动态查询匹配
      /*  ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());



        Example<Sensor> example = Example.of(sensor, matcher);
        Page<Sensor> list = sensorService.getPageList(example);*/
        List<Sensor> list = sensorService.findAll();
        list.forEach(editMenu -> {
            String type = String.valueOf(editMenu.getType());
            editMenu.setType(DictUtil.keyValue("SENSOR_TYPE", type));
        });
        // 封装数据
        model.addAttribute("list", list);
        model.addAttribute("page", list);

        return "/system/sensor/index";
    }


    /**
     * 查询用户的传感器
     */
    @RequestMapping("/userSensor")
    /* @RequiresPermissions("system:acquisitionInstrument:userAcquisition")*/
    @ResponseBody
    public TablePage userSensor(String no,Integer page, Integer limit ) {

         int total = 0;

        List<Sensor> dataList = new ArrayList<>();



        //获取当前用户
        User user = ShiroUtil.getSubject();
        //判断是不是超级管理员 是超级管理员那么久可以看到全部,不是就只能看到自己的和没有关联工点的采集仪
        if (user.getId().equals(AdminConst.ADMIN_ID)) {
            if(no != null && !"".equals(no)){
                Page<Sensor> userSensor = sensorService.findAllByNoContaining(no, page, limit);
                total =  Integer.parseInt(String.valueOf(userSensor.getTotalElements()));
                dataList.addAll(userSensor.getContent());
            }else{
                Page<Sensor> userSensor = sensorService.findAll(page, limit);
                total =  Integer.parseInt(String.valueOf(userSensor.getTotalElements()));
                dataList.addAll(userSensor.getContent());
            }
        }else{

            if(no != null && !"".equals(no)){
                Page<Sensor> userSensor = sensorService.findUserSensor(user.getId(), no, page, limit);
                total =  Integer.parseInt(String.valueOf(userSensor.getTotalElements()));
                dataList.addAll(userSensor.getContent());
            }else{
                Page<Sensor> userSensor = sensorService.findUserSensor(user.getId(), page, limit);
                total =  Integer.parseInt(String.valueOf(userSensor.getTotalElements()));
                dataList.addAll(userSensor.getContent());
            }
        }

        for (Sensor sensor : dataList) {
            if (sensor.getPhysicsUnit() != null && !"".equals(sensor.getPhysicsUnit())) {
                sensor.setPhysicsUnit(DictUtil.keyValue("UNIT_OF_PHYSICAL_QUANTITY",sensor.getPhysicsUnit()));
            }
            if (sensor.getType() != null && !"".equals(sensor.getType())) {
                sensor.setType(DictUtil.keyValue("SENSOR_TYPE",sensor.getType()));
            }
            if (sensor.getIsAuxiliarySensor() != null && !"".equals(sensor.getIsAuxiliarySensor())) {
                sensor.setIsAuxiliarySensor(DictUtil.keyValue("IS_AUXILIARY_SENSOR",sensor.getIsAuxiliarySensor()));
            }
            if(sensor.getTemperatureResistanceName() != null && !"".equals(sensor.getTemperatureResistanceName())){
                sensor.setTemperatureResistanceName(temperatureResistanceService.findById(Long.valueOf(sensor.getTemperatureResistanceName())).getName());
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
    /*@RequiresPermissions("system:sensor:add")*/
    public String toAdd(){
        return "/system/sensor/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    /*@RequiresPermissions("system:sensor:edit")*/
    public String toEdit(@PathVariable("id") Sensor sensor, Model model){

        SensorParameter parameter = sensorParameterService.findBySensourId(sensor.getId());
       /* if (sensor.getPhysicsUnit() != null && !"".equals(sensor.getPhysicsUnit())) {
            sensor.setPhysicsUnit(DictUtil.keyValue("UNIT_OF_PHYSICAL_QUANTITY",sensor.getPhysicsUnit()));
        }
        if (sensor.getType() != null && !"".equals(sensor.getType())) {
            sensor.setType(DictUtil.keyValue("SENSOR_TYPE",sensor.getType()));
        }*/
        model.addAttribute("edit", "edit");
        model.addAttribute("sensor", sensor);
        model.addAttribute("parameter", parameter);
        return "/system/sensor/add";
    }

    /**
     * 保存添加/修改的数据
     *add
     */
    @PostMapping({"/add","/edit"})
    @ResponseBody
    /*@ActionLog(name = "字典管理", message = "字典：${title}", action = SaveAction.class)*/
    public ResultVo save(/*@Validated DictValid valid*/ @EntityParam Sensor sensor,SensorParameter sensorParameter){

        sensorService.saveAndFlush(sensor);

        //如果是修改就不用再添加参数了
        if(  sensorParameterService.findBySensourId(sensor.getId()) == null){
            sensorParameter.setSensourId(sensor.getId());
            sensorParameterService.saveAndFlush(sensorParameter);
        }

        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    public String toDetail(@PathVariable("id") Sensor sensor, Model model){
        model.addAttribute("sensor",sensor);
        return "/system/sensor/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @ResponseBody
    @ActionLog(name = "字典状态", action = StatusAction.class)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids){
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (sensorService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    @GetMapping("/findAll")
    @ResponseBody
    public List<Sensor> findAll(){

        List<Sensor> sensors = sensorService.findUnboundSensor();

        return sensors;
    }


    @GetMapping("/findAuxiliary")
    @ResponseBody
    public List<Sensor> findAuxiliary(){

        List<Sensor> sensors = sensorService.findByAuxiliarySensor();

        return sensors;
    }

    /**
     * 添加参数
     */
    @GetMapping("/addParameter")
    public String addParameter(Long ids, Model model){

        SensorParameter parameter = sensorParameterService.findBySensourId(ids);



        model.addAttribute("SensourId", ids);
        model.addAttribute("parameter", parameter);
        return "/system/sensor/addParameter";
    }



    /**
     * 添加参数
     */
    @PostMapping("/saveParameter")
    @ResponseBody
    public ResultVo saveParameter(SensorParameter sensorParameter){
        sensorParameterService.save(sensorParameter);
        return ResultVoUtil.SAVE_SUCCESS;
    }




    /**
     * 查询传感器对应的关联
     */
    @GetMapping("/findSensorAcqiostion")
    @ResponseBody
    public ResultVo findSensorAcqiostion(Long  id){
        String msg = "";
        AcquisitionSensor acquisitionSensor = acquisitionSensorService.findBySensorId(id);


        if(acquisitionSensor != null){
            AcquisitionInstrument acquisitionInstrument = acquisitionInstrumentService.findByid(acquisitionSensor.getAcquisitionId());
            if(acquisitionInstrument != null){
                msg = "该传感器已经绑定到了编号为["+acquisitionInstrument.getNo()+"]的采集仪是否确认删除";
            }
        }else{
            msg = "是否确认删除?";
        }
        return ResultVoUtil.success(msg);
    }



    /**
     * 删除传感器并删除关联
     */
    @GetMapping("/delete")
    @ResponseBody
    public ResultVo delete(Long id){




        AcquisitionSensor acquisitionSensor = acquisitionSensorService.findBySensorId(id);
        if(acquisitionSensor != null){
            acquisitionSensorService.deleteById(acquisitionSensor.getId());
        }

        sensorService.deleteById(id);


        return ResultVoUtil.SAVE_SUCCESS;
    }












}
