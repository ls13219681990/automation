package com.linln.modules.system.service;

import com.linln.modules.system.domain.TemperatureResistanceData;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TemperatureResistanceDataService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<TemperatureResistanceData> getPageList(Example<TemperatureResistanceData> example);

    /**
     * 保存电阻
     *
     * @param temperatureResistanceData 电阻实体类
     */
    void save(TemperatureResistanceData temperatureResistanceData);


    /**
     * 保存电阻
     *
     * @param temperatureResistanceData 电阻实体类
     */
    void saveAll(List<TemperatureResistanceData> temperatureResistanceData);


    /**
     * 修改电阻信息
     * @param temperatureResistanceData
     */
    @Transactional
    void update(TemperatureResistanceData temperatureResistanceData);


    /**
     * 根据温阻ID查询温阻数据
     *
     */
    List<TemperatureResistanceData>  findByTemperatureResistanceId(Long temperatureResistanceId);



}
