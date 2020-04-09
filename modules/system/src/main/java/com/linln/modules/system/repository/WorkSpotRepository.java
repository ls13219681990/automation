package com.linln.modules.system.repository;

import com.linln.modules.system.domain.BidSection;
import com.linln.modules.system.domain.LineInfo;
import com.linln.modules.system.domain.MeasuringSpotData;
import com.linln.modules.system.domain.WorkSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface WorkSpotRepository extends JpaRepository<WorkSpot, Long> {

    /**
     * 根据名称查询工点
     * @param name
     * @return
     */
    WorkSpot findByName (String name);


    /**
     * 通过ID删除数据
     * @param id
     */
    @Transactional
    void removeById(Long id);

    /**
     * 根据ID进行查询
     */

    /**
     * 通过ID删除数据
     * @param id
     */
    @Transactional
    WorkSpot getById(Long id);
    /**
     * 根据工点名称查询线路下所有的测点数据   最后一个left无业务需求只为了方便返回好解析
     */
    @Query(value = "select \n" +
            "sdata.msId id,\n" +
            "sdata.measuringSpotName,\n" +
            "sdata.measuringSpotType,\n" +
            "msd.measuring_spot_sensor_no measuringSpotSensorNo,\n" +
            "msd.measuring_spot_original_value measuringSpotOriginalValue,\n" +
            "msd.measuring_spot_value   measuringSpotValue,\n" +
            "msd.measuring_spot_difference    measuringSpotDifference,\n" +
            "msd.measuring_spot_accumulation_value measuringSpotAccumulationValue,\n" +
            "msd.receive_time receiveTime,msd.day_rate_value dayRateValue,sdata.initial_value\n" +
            "from (select \n" +
            "ms.id msId, \n" +
            "\tMAX( msd.id ) id,\n" +
            "ms.name measuringSpotName,ms.initial_value,\n" +
            "ms.measuring_spot_type measuringSpotType\n" +
            "from line_info li \n" +
            "inner join bid_section bs on bs.line_info_id = li.id\n" +
            "inner join work_spot ws on ws.bid_section_id = bs.id\n" +
            "inner join measuring_spot ms on ms.work_spot_id = ws.id\n" +
            "left join measuring_spot_data msd on msd.measuring_spot_id = ms.id\n" +
            "where ws.name = ?1 \n" +
            "GROUP BY measuringSpotName,msId,measuringSpotType,ms.initial_value\n" +
            ")as sdata left join  measuring_spot_data msd on msd.id = sdata.id ",nativeQuery = true)
    List<Object[]> findByNameGetMeasuringSpotData (String name);


    @Query(value = "select data.* from (\n" +
            "select \n" +
            "sdata.msId id,\n" +
            "sdata.measuringSpotName,\n" +
            "sdata.measuringSpotType,\n" +
            "msd.measuring_spot_sensor_no measuringSpotSensorNo,\n" +
            "msd.measuring_spot_original_value measuringSpotOriginalValue,\n" +
            "msd.measuring_spot_value   measuringSpotValue,\n" +
            "msd.measuring_spot_difference    measuringSpotDifference,\n" +
            "msd.measuring_spot_accumulation_value measuringSpotAccumulationValue,\n" +
            "msd.receive_time receiveTime,msd.day_rate_value dayRateValue,sdata.initial_value\n" +
            "from (select \n" +
            "ms.id msId, \n" +
            "\tMAX( msd.id ) id,\n" +
            "ms.name measuringSpotName,ms.initial_value,\n" +
            "ms.measuring_spot_type measuringSpotType\n" +
            "from line_info li \n" +
            "inner join bid_section bs on bs.line_info_id = li.id\n" +
            "inner join work_spot ws on ws.bid_section_id = bs.id\n" +
            "inner join measuring_spot ms on ms.work_spot_id = ws.id\n" +
            "left join measuring_spot_data msd on msd.measuring_spot_id = ms.id\n" +
            "where ws.name = ?1 \n" +
            "GROUP BY measuringSpotName,msId,measuringSpotType,ms.initial_value\n" +
            ")as sdata left join  measuring_spot_data msd on msd.id = sdata.id\n" +
            ") data where\tDATA.receiveTime between ?2 and ?3\n",nativeQuery = true)
    List<Object[]> findByNameGetMeasuringSpotData (String name,String startTime, String endTime);

    /**
     * 根据标段ID获取对应工点
     * @param id
     * @return
     */
    List<WorkSpot> findByBidSectionId(Long id);


    /**
     * 查询开始和结束时间的
     * @param id
     * @param startTime
     * @param endTime
     * @return
     */
    @Query(value = "select data.* from (\n" +
            "select \n" +
            "sdata.msId id,\n" +
            "sdata.measuringSpotName,\n" +
            "sdata.measuringSpotType,\n" +
            "msd.measuring_spot_sensor_no measuringSpotSensorNo,\n" +
            "msd.measuring_spot_original_value measuringSpotOriginalValue,\n" +
            "msd.measuring_spot_value   measuringSpotValue,\n" +
            "msd.measuring_spot_difference    measuringSpotDifference,\n" +
            "msd.measuring_spot_accumulation_value measuringSpotAccumulationValue,\n" +
            "msd.receive_time receiveTime,msd.day_rate_value dayRateValue,sdata.initial_value\n" +
            "from (select \n" +
            "ms.id msId, \n" +
            "\tMAX( msd.id ) id,\n" +
            "ms.name measuringSpotName,ms.initial_value,\n" +
            "ms.measuring_spot_type measuringSpotType\n" +
            "from line_info li \n" +
            "inner join bid_section bs on bs.line_info_id = li.id\n" +
            "inner join work_spot ws on ws.bid_section_id = bs.id\n" +
            "inner join measuring_spot ms on ms.work_spot_id = ws.id\n" +
            "left join measuring_spot_data msd on msd.measuring_spot_id = ms.id\n" +
            "where ws.id = 65 \n" +
            "GROUP BY measuringSpotName,msId,measuringSpotType,ms.initial_value\n" +
            ")as sdata left join  measuring_spot_data msd on msd.id = sdata.id" +
            ") data where data.receiveTime between ?2 and ?3\n ",nativeQuery = true)
    List<Object[]> findByWorkSpotIdAndStartTimeAndEndTime(Long id, String startTime, String endTime);


    /**
     * 查询开始时间
     * @param id
     * @param startTime
     * @return
     */
    @Query(value = "select data.* from (\n" +
            "select \n" +
            "sdata.msId id,\n" +
            "sdata.measuringSpotName,\n" +
            "sdata.measuringSpotType,\n" +
            "msd.measuring_spot_sensor_no measuringSpotSensorNo,\n" +
            "msd.measuring_spot_original_value measuringSpotOriginalValue,\n" +
            "msd.measuring_spot_value   measuringSpotValue,\n" +
            "msd.measuring_spot_difference    measuringSpotDifference,\n" +
            "msd.measuring_spot_accumulation_value measuringSpotAccumulationValue,\n" +
            "msd.receive_time receiveTime,msd.day_rate_value dayRateValue,sdata.initial_value\n" +
            "from (select \n" +
            "ms.id msId, \n" +
            "\tMAX( msd.id ) id,\n" +
            "ms.name measuringSpotName,ms.initial_value,\n" +
            "ms.measuring_spot_type measuringSpotType\n" +
            "from line_info li \n" +
            "inner join bid_section bs on bs.line_info_id = li.id\n" +
            "inner join work_spot ws on ws.bid_section_id = bs.id\n" +
            "inner join measuring_spot ms on ms.work_spot_id = ws.id\n" +
            "left join measuring_spot_data msd on msd.measuring_spot_id = ms.id\n" +
            "where ws.id = 65 \n" +
            "GROUP BY measuringSpotName,msId,measuringSpotType,ms.initial_value\n" +
            ")as sdata left join  measuring_spot_data msd on msd.id = sdata.id\n" +
            ") data where data.receiveTime  >=?2\n ",nativeQuery = true)
    List<Object[]> findByWorkSpotIdAndStartTime(Long id, String startTime);



    /**
     * 查询结束时间的
     * @param id
     * @param endTime
     * @return
     */
    @Query(value = "select data.* from (\n" +
            "select \n" +
            "sdata.msId id,\n" +
            "sdata.measuringSpotName,\n" +
            "sdata.measuringSpotType,\n" +
            "msd.measuring_spot_sensor_no measuringSpotSensorNo,\n" +
            "msd.measuring_spot_original_value measuringSpotOriginalValue,\n" +
            "msd.measuring_spot_value   measuringSpotValue,\n" +
            "msd.measuring_spot_difference    measuringSpotDifference,\n" +
            "msd.measuring_spot_accumulation_value measuringSpotAccumulationValue,\n" +
            "msd.receive_time receiveTime,msd.day_rate_value dayRateValue,sdata.initial_value\n" +
            "from (select \n" +
            "ms.id msId, \n" +
            "\tMAX( msd.id ) id,\n" +
            "ms.name measuringSpotName,ms.initial_value,\n" +
            "ms.measuring_spot_type measuringSpotType\n" +
            "from line_info li \n" +
            "inner join bid_section bs on bs.line_info_id = li.id\n" +
            "inner join work_spot ws on ws.bid_section_id = bs.id\n" +
            "inner join measuring_spot ms on ms.work_spot_id = ws.id\n" +
            "left join measuring_spot_data msd on msd.measuring_spot_id = ms.id\n" +
            "where ws.id = 65 \n" +
            "GROUP BY measuringSpotName,msId,measuringSpotType,ms.initial_value\n" +
            ")as sdata left join  measuring_spot_data msd on msd.id = sdata.id\n" +
            ") data where data.receiveTime  <=?2\n ",nativeQuery = true)
    List<Object[]> findByWorkSpotIdAndEndTime(Long id, String endTime);

}

