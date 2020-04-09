package com.linln.modules.system.repository;

import com.linln.modules.system.domain.SensorData;
import com.linln.modules.system.domain.SensorParameter;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface SensorParameterRepository extends JpaRepository<SensorParameter, Long> {


    /**
     * 通过传感器ID查找传感器参数
     */

    public  SensorParameter findBySensourId(Long id);



}
