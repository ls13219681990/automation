package com.linln.modules.system.service;

import com.linln.modules.system.domain.SensorData;
import com.linln.modules.system.domain.TemperatureResistance;
import com.linln.modules.system.domain.TemperatureResistanceData;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TemperatureResistanceService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<TemperatureResistance> getPageList(Example<TemperatureResistance> example);

    /**
     * 保存温阻
     *
     * @param temperatureResistance 温阻实体类
     */
    void save(TemperatureResistance temperatureResistance);


    /**
     * 保存温阻
     *
     * @param temperatureResistance 温阻实体类
     */
    void saveAll(List<TemperatureResistance> temperatureResistance);



    /**
     * 修改电阻信息
     * @param temperatureResistance
     */
    @Transactional
    void update(TemperatureResistance temperatureResistance);




    /**
     * 查询所有温阻
     */
    List<TemperatureResistance> findAll ();



    /**
     * 根据温阻表名称 查询温阻数据
     */
    TemperatureResistance findByName (String name);




    TemperatureResistance findById (Long id);



}
