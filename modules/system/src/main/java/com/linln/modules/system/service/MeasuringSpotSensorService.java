package com.linln.modules.system.service;

import com.linln.modules.system.domain.MeasuringSpotSensor;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface MeasuringSpotSensorService {







    /**
     * 根据测点ID查询中间表信息
     * @return
     */
    List<MeasuringSpotSensor> getByMeasuringSpotId(Long id);



    /**
     * 根据测点ID查询测点数据
     * @param id 测点ID
     */
    MeasuringSpotSensor getById(Long id);


    /**
     * 保存测点
     * @param measuringSpot 测点实体类
     */
    void save(MeasuringSpotSensor measuringSpot);


    /**
     * 修改或者保存测点
     * @param measuringSpot 测点实体类
     */
    void saveAndFlush(MeasuringSpotSensor measuringSpot);



    /**
     * 根据采集仪传感器ID删除测点传感器关联表
     * @param acquisitionSensorId
     */

    void removeMeasuringSpotSensorByAcquisitionSensorId(Long acquisitionSensorId);




    /**
     * 通过采集仪传感器中间表ID 查找测点传感器中间表数据
     * @param id
     * @return
     */
    MeasuringSpotSensor  findByAcquisitionSensorId (Long id);




    /**
     * 批量删除
     * @param mss 批量数据ID
     */
    void deleteBatch(List<MeasuringSpotSensor> mss);




    /**
     * 根据采集仪ID查询采集仪对应的测点传感器数据
     * @param id 采集仪ID
     * @return
     */
    List<MeasuringSpotSensor> findByAcquisitionId(Long id);



}

