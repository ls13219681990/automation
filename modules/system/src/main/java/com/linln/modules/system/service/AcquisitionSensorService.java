package com.linln.modules.system.service;

import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.AcquisitionSensor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface AcquisitionSensorService {

    /**
     *  通过采集仪传感器中间表ID和通道查询中间表信息
     * @param acquisitionId
     * @return
     */

    public AcquisitionSensor getByacquisitionIdAndPassagewayId(Long acquisitionId, String passagewayId);


    /**
     * 获取分页列表数据
     * @param example 实体对象
     * @return 返回分页数据
     */
    Page<AcquisitionSensor> getPageList(Example<AcquisitionSensor> example);

    /**
     * 保存
     *
     *
     *
     * @param AcquisitionSensor 采集仪传感器中间表传感器中间表实体类
     */
    void save(AcquisitionSensor AcquisitionSensor);

    /**
     * 保存采集仪传感器中间表传感器中间表列表
     * @param acquisitionList 采集仪传感器中间表传感器中间表实体类
     */
    void save(List<AcquisitionSensor> acquisitionList);



    /**
     * 根据采集仪传感器中间表传感器中间表ID查询采集仪传感器中间表数据
     * @param id 采集仪传感器中间表ID
     */
     AcquisitionSensor findByid(Long id);



    /**
     * 保存/修改采集仪传感器中间表
     * @param AcquisitionSensor 采集仪传感器中间表实体
     */
    void saveAndFlush(AcquisitionSensor AcquisitionSensor);



    /**
     * 删除采集仪传感器中间表
     * @param id 采集仪传感器中间表实体ID
     */
    void deleteById(Long id);

    /**
     * 删除采集仪传感器中间表
     */
    @Transactional
    void deleteAll(List<AcquisitionSensor> acquisitionSensors);



    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);

    /**
     * 查询全部采集仪传感器中间表
     *
     */
    List<AcquisitionSensor> findAll();


    /**
     *  通过采集仪传感器中间表ID查询中间表信息
     * @param id
     * @return
     */
    public List<AcquisitionSensor> getByacquisitionId(Long id);


    /**
     * 根据主键ID删除数据
     * @param id
     */
    @Transactional
    void removeAcquisitionSensorById (Long id);


    /**
     * 根据传感器ID查询传感器关联表
     */

    AcquisitionSensor findBySensorId(Long id);



}
