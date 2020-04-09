package com.linln.modules.system.service;

import com.linln.modules.system.domain.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface MeasuringSpotService {



    /**
     * 获取分页列表数据
     * @param example 实体对象
     * @return 返回分页数据
     */
    Page<MeasuringSpot> getPageList(Example<MeasuringSpot> example);






    /**
     * 根据线路ID查询线路数据
     * @param id 线路ID
     */
    MeasuringSpot getById(Long id);

  
    /**
     * 保存线路
     * @param measuringSpot 线路实体类
     */
    MeasuringSpot save(MeasuringSpot measuringSpot);


    /**
     * 修改或者保存线路
     * @param measuringSpot 线路实体类
     */
    void saveAndFlush(MeasuringSpot measuringSpot);



    /**
     * 根据名称查询测点
     * @param name
     * @return
     */
    MeasuringSpot findByName (String name);




    /**
     * 根据测点名称 查询测点数据
     */

    List<SensorData> findSensorDataByNamePage(String name, Integer page, Integer total);



    /**
     * 根据测点名称 查询测点数据
     */

    List<SensorData> findSensorDataByName(String name);



    /**
     * 通过测点名称查询测点下面的辅助传感器
     * @param name
     * @return
     */
    Sensor findMeasuringSpotSensorAuxiliarySensorByName(String name);



    /**
     * 通过ID删除数据
     * @param id
     */
    void removeById(Long id);




    /**
     * 根据采集仪编号查找对应的测点传感器
     */

    List<Object[]> findByAcquisitionNoGetMeasuringSpotSensor(String no,String registerSSID);



    /**
     * 根据采集仪编号获取绑定的测点
     * @param no
     * @return
     */

    List<MeasuringSpot> findByAcquisitionNo(String no);

    @Transactional
    void update (MeasuringSpot measuringSpot);




    /**
     * 通过工点ID查找工点下面的所有测点
     * @param id
     * @param page
     * @param limit
     * @return
     */
    Page<MeasuringSpot> findByWorkSpotId(Long id, Integer page, Integer limit);

    List<MeasuringSpot> findByWorkSpotId(Long id);

    MeasuringSpot findById(Long id);










}

