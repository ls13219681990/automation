package com.linln.modules.system.repository;

import com.linln.modules.system.domain.MeasuringSpot;
import com.linln.modules.system.domain.MeasuringSpotSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface MeasuringSpotSensorRepository extends JpaRepository<MeasuringSpotSensor, Long> {


    /**
     * 根据测点ID查询中间表信息
     * @return
     * @param id
     */
    @Query("select mss from MeasuringSpotSensor mss where  mss.measuringSpotId = :id")
    List<MeasuringSpotSensor> getByMeasuringSpotId(@Param("id")Long id);


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
    MeasuringSpotSensor  findDistinctByAcquisitionSensorId (Long id);




    /**
     * @function 自定义JPQL
     * @param mss
     */
    // 注意更新和删除是需要加事务的， 并且要加上 @Modify的注解
    @Modifying
    @Transactional
    @Query("delete from MeasuringSpotSensor mss where mss.id in (?1)")
    void deleteBatch(List<Long> mss);


    /**
     * 根据采集仪ID查询采集仪对应的测点传感器数据
     * @param id 采集仪ID
     * @return
     */
    @Query(value = "SELECT mss.* FROM acquisition_instrument ai inner join  acquisition_sensor ac on ai.id = ac.acquisition_id inner join  measuring_spot_sensor mss on mss.acquisition_sensor_id = ac.id where ai.id = ?1",nativeQuery = true)
    List<MeasuringSpotSensor> findByAcquisitionId(Long id);





}

