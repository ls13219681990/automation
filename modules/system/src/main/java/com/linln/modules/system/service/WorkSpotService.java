package com.linln.modules.system.service;

import com.linln.modules.system.domain.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface WorkSpotService {




    /**
     * 获取分页列表数据
     * @param example 实体对象
     * @return 返回分页数据
     */
    Page<WorkSpot> getPageList(Example<WorkSpot> example);

    /**
     * 根据工点ID查询工点数据
     * @param id 工点ID
     */
    WorkSpot getById(Long id);

  
    /**
     * 保存工点
     * @param workSpot 工点实体类
     */
    WorkSpot save(WorkSpot workSpot);


    /**
     * 修改或者保存工点
     * @param workSpot 工点实体类
     */
    void saveAndFlush(WorkSpot workSpot);

    /**
     * 根据名称查询工点
     * @param name
     * @return
     */
    WorkSpot findByName (String name);



    /**
     * 查询所有工点
     * @return
     */
    List<WorkSpot> findAll ();



    /**
     * 通过ID查询工点
     * @return
     */
    WorkSpot findById (Long id);



    /**
     * 通过ID删除数据
     * @param id
     */
    void removeById(Long id);





    /**
     * 根据工点名称查询线路下所有的测点数据
     */

    List<Object[]> findByNameGetMeasuringSpotData (String name);

    /**
     * 根据工点名称查询线路下所有的测点数据
     */

    List<Object[]> findByNameGetMeasuringSpotData (String name,String startTime, String endTime);



    @Transactional
    void update(WorkSpot workSpot);


    /**
     * 根据标段ID获取对应工点
     * @param id
     * @return
     */
    List<WorkSpot> findByBidSectionId(Long id);


    /**
     * 通过工点ID和时间区间进行查询测点数据
     * @param id
     * @param startTime
     * @param endTime
     * @return
     */
    List<Object[]> findByWorkSpotIdAndStartTimeAndEndTime (Long id,String startTime, String endTime);


}

