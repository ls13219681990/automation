package com.linln.modules.system.repository;

import com.linln.modules.system.domain.MeasuringSpot;
import com.linln.modules.system.domain.MeasuringSpotData;
import com.linln.modules.system.domain.Sensor;
import com.linln.modules.system.domain.SensorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface MeasuringSpotDataRepository extends JpaRepository<MeasuringSpotData, Long> {


    /**
     * 接收时间排序返回最新的一条
     *
     * @return
     */
    MeasuringSpotData findFirstByOrderByReceiveTimeDesc();

    /**
     * 接收时间排序返回最新的一条
     *
     * @return
     */

    MeasuringSpotData findFirstByMeasuringSpotIdOrderByReceiveTimeDesc(Long id);


    /**
     * 通过测点ID查找测点数据
     */
    @Query(value = "select * from measuring_spot_data msd where msd.measuring_spot_id = ?1 and msd.status <> '3'  ORDER BY  receive_time desc", nativeQuery = true)
    List<MeasuringSpotData> findByMeasuringSpotIdOrderByReceiveTime(Long id, Pageable pageable);


    /**
     * 通过测点ID查找测点数据
     */

    List<MeasuringSpotData> findByMeasuringSpotIdOrderByIdAsc(Long id);


    /**
     * 根据时间查询24小时之前的数据
     *
     * @param time
     * @return
     */
    @Query(value = "select msd.* from (\n" +
            "select MAX(msd.id) id  from measuring_spot_data msd where msd.receive_time < ?1\n" +
            ") datatime inner join measuring_spot_data msd on msd.id = datatime.id\n", nativeQuery = true)
    MeasuringSpotData findByReceiveTime(String time);


    /**
     * 批量冻结测点数据
     *
     * @param status
     * @param id
     */
    @Modifying
    @Query("update MeasuringSpotData a set a.status = ?1  where a.id in ?2")
    void updateMeasuringSpotDataList(Byte status, List<Long> id);


    /**
     * 查询时间之后的数据
     */
    List<MeasuringSpotData> findByReceiveTimeGreaterThan(String time);

    /**
     * 查询时间之前的第一条数据的数据
     */
    MeasuringSpotData findFirstByReceiveTimeLessThanOrderByIdDesc(String time);


    /**
     * 查询两条数据中间的数据
     *
     * @param startId
     * @param EndId
     * @return
     */
    List<MeasuringSpotData> findByIdGreaterThanAndIdLessThan(Long startId, Long EndId);


    /**
     * 查询做过对齐的数据 用于处理 先后对齐 再前对齐的这种
     *
     * @param time
     * @param whetherAlignment 0是对齐过的
     * @return
     */
    @Query(value = "SELECT ms.* FROM measuring_spot_data ms WHERE ms.whether_alignment = ?2 AND ms.receive_time > ?1", nativeQuery = true)
    MeasuringSpotData findFirstByWhetherAlignmentAndReceiveTimeGreaterThan(String time, String whetherAlignment);

    /**
     * 根据接收时间范围查询数据
     *
     * @param startDate
     * @param EndDate
     * @param id
     * @return
     */
    @Query(value = "SELECT *\n" +
            "FROM\n" +
            "\tmeasuring_spot_data msd \n" +
            "WHERE\n" +
            "\tmsd.measuring_spot_id = ?1\n" +
            "\tAND msd.receive_time > ?2\n" +
            "\tAND msd.receive_time < ?3", nativeQuery = true)
    List<MeasuringSpotData> findByMeasuringSpotIdAndReceiveTimeGreaterThanAndReceiveTimeLessThan(Long id, String startDate, String EndDate);



    /**
     * 获取测点数据最早的一条   （用于时间取范围）
     */
    @Query(value = "SELECT * FROM measuring_spot_data msd where msd.measuring_spot_id= ?1 order by msd.receive_time ASC LIMIT 1 ",nativeQuery = true)
    MeasuringSpotData findByMeasuringSpotIdOrderByReceiveTimeAsc (Long id);

    /**
     * 获取测点数据最新的一条   （用于时间取范围）
     */
    @Query(value = "SELECT * FROM measuring_spot_data msd where msd.measuring_spot_id= ?1 order by msd.receive_time desc LIMIT 1 ",nativeQuery = true)
    MeasuringSpotData  findByMeasuringSpotIdOrderByReceiveTimeDesc (Long id);


    /**
     * 根据开始时间和ID查询测点数据
     * @param id
     * @param startDate
     * @return
     */
    List<MeasuringSpotData>  findByMeasuringSpotIdAndReceiveTimeGreaterThan (Long id,String startDate);



    /**
     * 通过工点ID查询 所有测点数据最早的一条
     * @param workSpotId
     * @return
     */
    @Query(value = "select msd.* from measuring_spot_data msd \n" +
            "inner join measuring_spot ms on msd.measuring_spot_id = ms.id\n" +
            "inner join work_spot ws  on ms.work_spot_id = ws.id and  ws.id= ?1 \n" +
            "order by msd.receive_time ASC limit 1\n" +
            "\n",nativeQuery = true)
    MeasuringSpotData  findByworkSpotIdMeasuringDataDateMin(Long workSpotId);




    /**
     * 通过工点ID查询 所有测点数据最晚的一条
     * @param workSpotId
     * @return
     */
    @Query(value = "select msd.* from measuring_spot_data msd \n" +
            "inner join measuring_spot ms on msd.measuring_spot_id = ms.id\n" +
            "inner join work_spot ws  on ms.work_spot_id = ws.id and  ws.id= ?1 \n" +
            "order by msd.receive_time DESC  limit 1\n" +
            "\n",nativeQuery = true)
    MeasuringSpotData  findByworkSpotIdMeasuringDataDateMax(Long workSpotId);



    /**
     * 根据结束时间和ID查询测点数据
     * @param id
     * @param endDate 结束时间
     * @return
     */
    List<MeasuringSpotData> findByMeasuringSpotIdAndReceiveTimeLessThan(Long id, String endDate);


    /**
     * 通过工点ID  开始时间和结束时间 查询工点下面所有工点的 数据
     * @param workSpotID
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(value = "select msd.* from measuring_spot_data msd \n" +
            "inner join measuring_spot ms on ms.id = msd.measuring_spot_id\n" +
            "inner join work_spot  ws on ws.id = ms.work_spot_id\n" +
            "where ws.id = ?1 and  msd.receive_time  BETWEEN ?2 and ?3 ORDER BY msd.receive_time ASC\n",nativeQuery = true)
    List<MeasuringSpotData>findByWorkSpotIAndStartDateAndEndDate(Long workSpotId,String startDate,String endDate);


}

