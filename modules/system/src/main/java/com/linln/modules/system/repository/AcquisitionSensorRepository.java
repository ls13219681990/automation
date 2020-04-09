package com.linln.modules.system.repository;

import com.linln.modules.system.domain.AcquisitionInstrument;
import com.linln.modules.system.domain.AcquisitionSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface AcquisitionSensorRepository extends JpaRepository<AcquisitionSensor, Long> {
/*
    *//**
     * 根据用户名查询用户数据
     * @param No 用户名
     * @return 用户数据
     *//*
    public AcquisitionInstrument findByNo(String No);*/

/*

    */
/**
     * 根据ID查询采集仪数据
     * @param id     ID列表
     *//*

    public AcquisitionInstrument getById(Long id);
*/


/*
    *//**
     * 删除多条数据
     * @param ids     ID列表
     *//*
    public Integer deleteByIdIn(List<Long> ids);*/





    /**
     *  通过采集仪ID查询中间表信息
     * @param id
     * @return
     */
    public List<AcquisitionSensor> getByacquisitionId(Long id);



    /**
     *  通过采集仪ID和通道查询中间表信息
     * @param acquisitionId
     * @return
     */
    @Query("select ai from AcquisitionSensor ai where  ai.acquisitionId = :acquisitionId and ai.passagewayId = :passagewayId ")
    public AcquisitionSensor getByacquisitionIdAndPassagewayId(@Param("acquisitionId")Long acquisitionId,@Param("passagewayId")String passagewayId);


    /**
     * 根据主键ID删除数据
     * @param id
     */
    void removeAcquisitionSensorById (Long id);


    /**
     * 根据传感器ID查询传感器关联表
     */

    AcquisitionSensor findBySensorId(Long id);




}
