package com.linln.modules.system.repository;

import com.linln.modules.system.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;

import java.util.List;

import static org.hibernate.jpa.QueryHints.HINT_COMMENT;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface MeasuringSpotRepository extends JpaRepository<MeasuringSpot, Long> {

    /**
     * 根据名称查询测点
     *
     * @param name
     * @return
     */
    MeasuringSpot findByName(String name);


    /**
     * 根据测点名称 查询测点数据
     */

    @Query(value = "select sd from MeasuringSpot ms inner join MeasuringSpotSensor mss on mss.measuringSpotId = ms.id inner join AcquisitionSensor asensor on asensor.id =mss.acquisitionSensorId  inner join Sensor sens on sens.id = asensor.sensorId  and sens.isAuxiliarySensor <> 1   inner join SensorData sd on sd.sensorId = asensor.sensorId    where ms.name = :name  ")
    public List<SensorData> findSensorDataByNamePage(@Param("name") String name, Pageable pageable);


    /**
     * 根据测点名称 查询测点数据
     */

    @Query(value = "select sd from MeasuringSpot ms inner join MeasuringSpotSensor mss on mss.measuringSpotId = ms.id inner join AcquisitionSensor asensor on asensor.id =mss.acquisitionSensorId  inner join Sensor sens on sens.id = asensor.sensorId  and sens.isAuxiliarySensor <> 1   inner join SensorData sd on sd.sensorId = asensor.sensorId    where ms.name = :name  ")
    public List<SensorData> findSensorDataByName(@Param("name") String name);


    /**
     * 通过测点名称查询测点下面的辅助传感器
     *
     * @param name
     * @return
     */
    @Query("select sor from MeasuringSpot ms inner join  MeasuringSpotSensor  mss on mss.measuringSpotId = ms.id inner join AcquisitionSensor asen on asen.id = mss.acquisitionSensorId  inner join Sensor sor on sor.id = asen.sensorId  where ms.name = :name and sor.isAuxiliarySensor = 1 ")
    Sensor findMeasuringSpotSensorAuxiliarySensorByName(@Param("name") String name);
    //


    /**
     * 通过ID删除数据
     *
     * @param id
     */
    @Transactional
    void removeById(Long id);


    /**
     * 根据采集仪编号查找对应的测点传感器
     */
    @Query(value = "select ass.* from (SELECT DISTINCT ms.* FROM acquisition_instrument ai inner join net_work nw on ai.net_work_id = nw.id inner join acquisition_sensor ass on ass.acquisition_id = ai.id inner join measuring_spot_sensor mss on ass.id = mss.acquisition_sensor_id inner join measuring_spot ms on mss.measuring_spot_id = ms.id  where ai.no = ?1 and nw.registerssid = ?2 ) ms inner join measuring_spot_sensor mss on ms.id = mss.measuring_spot_id inner join acquisition_sensor ass on ass.id = mss.acquisition_sensor_id", nativeQuery = true)
    List<Object[]> findByAcquisitionNoGetMeasuringSpotSensor(String no,String registerSSID);


    /**
     * 根据采集仪编号获取绑定的测点
     * @param no
     * @return
     */
    @Query(value = "SELECT DISTINCT ms.* FROM acquisition_instrument ai inner join acquisition_sensor ass on ass.acquisition_id = ai.id inner join measuring_spot_sensor mss on ass.id = mss.acquisition_sensor_id  inner join measuring_spot ms on mss.measuring_spot_id = ms.id where ai.no = ?1", nativeQuery = true)
    List<MeasuringSpot> findByAcquisitionNo(String no);


    /**
     * 通过工点ID查找工点下面的所有测点
     * @param id
     * @return
     */
    Page<MeasuringSpot> findByWorkSpotId(Long id, org.springframework.data.domain.Pageable pageable);


    List<MeasuringSpot> findByWorkSpotId(Long id);

    MeasuringSpot  getById(Long id);


}

