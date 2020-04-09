package com.linln.modules.system.repository;

import com.linln.modules.system.domain.Sensor;
import com.linln.modules.system.domain.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {


    /**
     * 通过传感器ID和时间查询传感器数据
     */
    SensorData findBySensorIdAndReceiveTime(Long sensorId,String time);

    /**
     * 通过传感器ID查询传感器数据
     * @param sensorId
     * @return
     */
    SensorData findBySensorId(Long sensorId);


}
