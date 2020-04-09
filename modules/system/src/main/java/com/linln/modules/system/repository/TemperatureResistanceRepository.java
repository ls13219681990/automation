package com.linln.modules.system.repository;

import com.linln.modules.system.domain.SensorParameter;
import com.linln.modules.system.domain.TemperatureResistance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface TemperatureResistanceRepository extends JpaRepository<TemperatureResistance, Long> {


    /**
     * 查询所有温阻
     */
    List<TemperatureResistance> findAll ();



    /**
     * 根据温阻表名称 查询温阻数据
     */
    TemperatureResistance findByName (String name);



}
