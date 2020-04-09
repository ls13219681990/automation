package com.linln.modules.system.service;

import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.Sensor;
import com.linln.modules.system.domain.SensorParameter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface SensorParameterService {










    /**
     * 获取分页列表数据
     *
     * @return 返回分页数据
     */
    Page<SensorParameter> getPageList(Example<SensorParameter> example);

    /**
     * 保存用户
     *
     * @param sensor 用户实体类
     */
    void save(SensorParameter sensor);

    /**
     * 保存用户列表
     *
     * @param sensorsList 用户实体类List
     */
    void save(List<SensorParameter> sensorsList);




    /**
     * 根据用户ID查询传感器数据
     *
     * @param id 传感器ID
     */
    Sensor findByid(Long id);

    /**
     * 新增或者修改传感器
     *
     * @param sensor 传感器实体
     */
    void saveAndFlush(SensorParameter sensor);



    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);



    List<SensorParameter> findAll();




    /**
     * 通过传感器ID查找传感器参数
     */

    public  SensorParameter findBySensourId(Long id);







}
