package com.linln.modules.system.repository;

import com.linln.modules.system.domain.BidSection;
import com.linln.modules.system.domain.LineInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface BidSectionRepository extends CrudRepository<BidSection, Long> {

    BidSection findByName(String name);



    /**
     * 通过ID删除数据
     * @param id
     */
    @Transactional
    void removeById(Long id);



    /**
     * 根据标段名称查询线路下所有的测点数据  最后一个left无业务需求只为了方便返回好解析
     */
    @Query(value = "select \n" +
            "sdata.id,\n" +
            "sdata.workSpotName,\n" +
            "sdata.measuringSpotName,\n" +
            "sdata.measuringSpotType,\n" +
            "msd.measuring_spot_sensor_no measuringSpotSensorNo,\n" +
            "msd.measuring_spot_original_value measuringSpotOriginalValue,\n" +
            "msd.measuring_spot_value   measuringSpotValue,\n" +
            "msd.measuring_spot_difference    measuringSpotDifference,\n" +
            "msd.measuring_spot_accumulation_value measuringSpotAccumulationValue,\n" +
            "msd.receive_time receiveTime,msd.day_rate_value dayRateValue,\n" +
            "sdata.initial_value\n" +
            "from (select \n" +
            "MAX(msd.id) id,\n" +
            "ws.name workSpotName,\n" +
            "ms.initial_value,\n" +
            "ms.name measuringSpotName,\n" +
            "ms.measuring_spot_type measuringSpotType\n" +
            "from line_info li \n" +
            "left join bid_section bs on bs.line_info_id = li.id\n" +
            "left join work_spot ws on ws.bid_section_id = bs.id\n" +
            "left join measuring_spot ms on ms.work_spot_id = ws.id\n" +
            "left join measuring_spot_data msd on msd.measuring_spot_id = ms.id\n" +
            "where bs.name = ?1 \n" +
            "GROUP BY measuringSpotName,measuringSpotType,workSpotName,ms.initial_value\n" +
            ")as sdata left join  measuring_spot_data msd on msd.id = sdata.id ",nativeQuery = true)
    List<Object[]> findByNameGetMeasuringSpotData (String name);


    /**
     * 根据标段名称查询线路下所有的测点数据  最后一个left无业务需求只为了方便返回好解析
     */
    @Query(value = "select \n" +
            "sdata.id,\n" +
            "sdata.workSpotName,\n" +
            "sdata.measuringSpotName,\n" +
            "sdata.measuringSpotType,\n" +
            "msd.measuring_spot_sensor_no measuringSpotSensorNo,\n" +
            "msd.measuring_spot_original_value measuringSpotOriginalValue,\n" +
            "msd.measuring_spot_value   measuringSpotValue,\n" +
            "msd.measuring_spot_difference    measuringSpotDifference,\n" +
            "msd.measuring_spot_accumulation_value measuringSpotAccumulationValue,\n" +
            "msd.receive_time receiveTime,msd.day_rate_value dayRateValue,\n" +
            "sdata.initial_value\n" +
            "from (select \n" +
            "MAX(msd.id) id,\n" +
            "ws.name workSpotName,\n" +
            "ms.initial_value,\n" +
            "ms.name measuringSpotName,\n" +
            "ms.measuring_spot_type measuringSpotType\n" +
            "from line_info li \n" +
            "left join bid_section bs on bs.line_info_id = li.id\n" +
            "left join work_spot ws on ws.bid_section_id = bs.id\n" +
            "left join measuring_spot ms on ms.work_spot_id = ws.id\n" +
            "left join measuring_spot_data msd on msd.measuring_spot_id = ms.id\n" +
            "where bs.name = ?1 and msd.receive_time >=?2 and msd.receive_time <=?3\n" +
            "GROUP BY measuringSpotName,measuringSpotType,workSpotName,ms.initial_value\n" +
            ")as sdata left join  measuring_spot_data msd on msd.id = sdata.id ",nativeQuery = true)
    List<Object[]> findByNameGetMeasuringSpotData (String name,String startTime,String endTime);




    /**
     * 根据线路ID查询对应标段
     * @param id
     * @return
     */
    Page<BidSection> findByLineInfoId(Long id , org.springframework.data.domain.Pageable page);


    List<BidSection> findByLineInfoId(Long id);

    Page<BidSection> findAll(org.springframework.data.domain.Pageable page);

}

