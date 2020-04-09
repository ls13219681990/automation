package com.linln.modules.system.service;


import com.linln.modules.system.domain.MeasuringSpotData;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface MeasuringSpotDataService {



    /**
     * 获取分页列表数据
     * @param example 实体对象
     * @return 返回分页数据
     */
    Page<MeasuringSpotData> getPageList(Example<MeasuringSpotData> example);



    void saveAll(List<MeasuringSpotData> measuringSpotDataList);




    /**
     * 根据线路ID查询线路数据
     * @param id 线路ID
     */
    MeasuringSpotData getById(Long id);

  
    /**
     * 保存线路
     * @param measuringSpot 线路实体类
     */
    void save(MeasuringSpotData measuringSpot);


    /**
     * 修改或者保存线路
     * @param measuringSpot 线路实体类
     */
    void saveAndFlush(MeasuringSpotData measuringSpot);




    /**
     * 接收时间排序返回最新的一条
     * @return
     */
    MeasuringSpotData findFirstByOrderByReceiveTimeDesc();



    /**
     * 接收时间排序返回最新的一条
     * @return
     */
    MeasuringSpotData findFirstByMeasuringSpotIdByOrderByReceiveTimeDesc(Long id);



    /**
     * 通过测点ID查找测点数据
     */

    List<MeasuringSpotData> findByMeasuringSpotIdOrderByReceiveTime(Long id,Integer page, Integer limit);

    /**
     * 通过测点ID查找测点数据
     */

    List<MeasuringSpotData> findByMeasuringSpotId(Long id);


    /**
     * 根据时间查询24小时之前的数据
     * @param time
     * @return
     */

    MeasuringSpotData findByReceiveTime(String time);



    /**
     * 批量冻结测点数据
     * @param status
     * @param measuringSpotDataList
     */
    @Transactional
    void updateMeasuringSpotDataList(Byte status,List<MeasuringSpotData> measuringSpotDataList);





    /**
     * 查询时间之后的数据
     */
    List<MeasuringSpotData> findByReceiveTimeGreaterThan(String time);


    /**
     * 查询时间之前的第一条数据的数据
     */
    MeasuringSpotData findFirstByReceiveTimeLessThanOrderByIdDesc(String time);


    @Transactional
    void updateAll(List<MeasuringSpotData> measuringSpotDataList);

    @Transactional
    void update (MeasuringSpotData measuringSpotDataList);
    /**
     * 查询两条数据中间的数据
     * @param startId
     * @param EndId
     * @return
     */
    List<MeasuringSpotData> findByIdGreaterThanAndIdLessThan(Long startId ,Long EndId);


    @Transactional
    void deleteAll(List<MeasuringSpotData> measuringSpotDataList);




    /**
     * 查询做过对齐的数据 用于处理 先后对齐 再前对齐的这种
     * @param time
     * @param whetherAlignment 0是对齐过的
     * @return
     */

    MeasuringSpotData findFirstByWhetherAlignmentAndReceiveTimeGreaterThan(String time,String whetherAlignment);


    /**
     * 根据接收时间范围查询数据
     * @param startDate
     * @param EndDate
     * @param id
     * @return
     */
    List<MeasuringSpotData> findByMeasuringSpotIdAndReceiveTimeGreaterThanAndReceiveTimeLessThan(Long id,String startDate ,String EndDate);


    /**
     * 获取测点数据最早的一条   （用于时间取范围）
     */
    MeasuringSpotData getDateMin (Long id);

    /**
     * 获取测点数据最新的一条   （用于时间取范围）
     */
    MeasuringSpotData  getDateMax (Long id);


    /**
     * 根据开始时间和ID查询测点数据
     * @param id
     * @param startDate 开始时间
     * @return
     */
    List<MeasuringSpotData>  findByMeasuringSpotIdAndReceiveTimeGreaterThan (Long id,String startDate);


    /**
     * 根据结束时间和ID查询测点数据
     * @param id
     * @param endDate 结束时间
     * @return
     */
    List<MeasuringSpotData>  findByMeasuringSpotIdAndReceiveTimeLessThan (Long id,String endDate);


    /**
     * 通过工点ID查询 所有测点数据最早的一条
     * @param workSpotId
     * @return
     */
    MeasuringSpotData  findByworkSpotIdMeasuringDataDateMin(Long workSpotId);


    /**
     * 通过工点ID查询 所有测点数据最晚的一条
     * @param workSpotId
     * @return
     */
    MeasuringSpotData  findByworkSpotIdMeasuringDataDateMax(Long workSpotId);


    List<MeasuringSpotData>findByWorkSpotIAndStartDateAndEndDate(Long workSpotId,String startDate,String endDate);






}

