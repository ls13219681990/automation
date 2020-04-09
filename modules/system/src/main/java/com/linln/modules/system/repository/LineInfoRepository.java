package com.linln.modules.system.repository;

import com.linln.common.constant.StatusConst;
import com.linln.modules.system.domain.Dept;
import com.linln.modules.system.domain.LineInfo;
import com.linln.modules.system.domain.MeasuringSpotData;
import com.linln.modules.system.page.LineMeasuringSpotDataPage;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface LineInfoRepository extends JpaRepository<LineInfo, Long> {

    /**
     * 根据名称查询标段
     * @param name
     * @return
     */
    LineInfo findByName (String name);


    /**
     * 通过ID删除数据
     * @param id
     */
    @Transactional
    void removeById(Long id);


    /**
     * 根据线路名称查询线路下所有的测点数据  最后一个left无业务需求只为了方便返回好解析
     */
    @Query(value = "select sdata.id, sdata.bidSectionName, sdata.workSpotName, sdata.measuringSpotName, sdata.measuringSpotType, msd.measuring_spot_sensor_no measuringSpotSensorNo, msd.measuring_spot_original_value measuringSpotOriginalValue, msd.measuring_spot_value   measuringSpotValue, msd.measuring_spot_difference    measuringSpotDifference, msd.measuring_spot_accumulation_value measuringSpotAccumulationValue, msd.receive_time receiveTime,msd.day_rate_value dayRateValue,msd.status from (select MAX(msd.id) id, bs.name bidSectionName, ws.name workSpotName, ms.name measuringSpotName, ms.measuring_spot_type measuringSpotType from line_info li inner join bid_section bs on bs.line_info_id = li.id inner join work_spot ws on ws.bid_section_id = bs.id inner join measuring_spot ms on ms.work_spot_id = ws.id left join measuring_spot_data msd on msd.measuring_spot_id = ms.id WHERE li.NAME = ?1 GROUP BY bidSectionName,workSpotName,measuringSpotName,measuringSpotType)as sdata left join  measuring_spot_data msd on msd.id = sdata.id",nativeQuery = true)
    List<Object[]> findByNameGetMeasuringSpotData (String name);


    /*  之前的
    *//**
     * 根据线路名称查询线路下所有的测点数据
     *//*
    @Query(value = "select sdata.id,sdata.bidSectionName,sdata.workSpotName,sdata.measuringSpotName,sdata.measuringSpotType,sdata.sensorNmae,sd.data,sd.receive_time from( SELECT bs.name bidSectionName ,ws.name workSpotName ,ms.measuring_spot_type measuringSpotType,ms.name measuringSpotName ,sen.name sensorNmae,max(sd.id) id from line_info li inner join bid_section bs on bs.line_info_id = li.id inner join work_spot ws on ws.bid_section_id = bs.id inner join measuring_spot ms on ms.work_spot_id = ws.id inner join measuring_spot_sensor mss on mss.measuring_spot_id = ms.id left join acquisition_sensor ass on ass.id = mss.acquisition_sensor_id left join sensor sen on sen.id = ass.sensor_id left join sensor_data sd on sd.sensor_id = sen.id  where li.name =  ?1 GROUP BY  bidSectionName,workSpotName,measuringSpotName,sensorNmae,measuringSpotType) as sdata left join  sensor_data sd on sd.id = sdata.id ",nativeQuery = true)
    List<Object[]> findByNameGetMeasuringSpotData (String name);*/




/*@Query(value = "select new com.linln.modules.system.page.LineMeasuringSpotDataPage (sdata.bidSectionName,sdata.workSpotName,sdata.measuringSpotName,sdata.measuringSpotType,sdata.sensorNmae,sd.data,sd.receive_time) from  
            "select bs.name bidSectionName ,ws.name workSpotName ,ms.name measuringSpotName ,ms.measuring_spot_type measuringSpotType,sen.name sensorNmae,max(sd.id) id from line_info li 
            "inner join BidSection bs on bs.lineInfoId = li.id
            "inner join WorkSpot ws on ws.bidSectionId = bs.id
            "inner join MeasuringSpot ms on ms.workSpotId = ws.id
            "inner join MeasuringSpot_sensor mss on mss.measuringSpotId = ms.id
            "left join AcquisitionSensor ass on ass.id = mss.acquisitionSensorId 
            "left join Aensor sen on sen.id = ass.sensorId
            "left join AensorData sd on sd.sensor_id = sen.id  
            "
            "GROUP BY  bidSectionName,workSpotName,measuringSpotName,sensorNmae,measuringSpotType
            "
            ") sdata left join  SensorData sd on sd.id = sdata.id   ")*/







}

