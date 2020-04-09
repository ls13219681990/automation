package com.linln.modules.system.service;

import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.AcquisitionInstrument;
import com.linln.modules.system.domain.Dict;
import com.linln.modules.system.domain.Sensor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface SensorService {

    /**
     * 获取分页列表数据
     *
     * @return 返回分页数据
     */
    Page<Sensor> getPageList(Example<Sensor> example);

    /**
     * 保存传感器
     *
     * @param sensor 传感器实体类
     */
    void save(Sensor sensor);


    /**
     * 删除传感器
     *
     */
    void deleteById(Long id);




    /**
     * 保存传感器列表
     *
     * @param sensorsList 传感器实体类List
     */
    void save(List<Sensor> sensorsList);



    
    
    

    /**
     * 根据传感器ID查询传感器数据
     *
     * @param id 传感器ID
     */
    Sensor findByid(Long id);

    /**
     * 新增或者修改传感器
     *
     * @param sensor 传感器实体
     */
    void saveAndFlush(Sensor sensor);



    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);


    List<Sensor> findAll();
    Page<Sensor> findAll(Integer page, Integer limit);

    /**
     * 根据传感器编号查找传感器
     * @RETURN 传感器列表
     */

    public Sensor getByNo(String no);





    /**
     * 查询辅助传感器
     * @RETURN 传感器列表
     */

    List<Sensor> findByAuxiliarySensor();



    /**
     * 根据传感器名称查找传感器
     */
    Sensor findByName(String name);

    /**
     * 查询未绑定的传感器
     * @return
     */
    List<Sensor> findUnboundSensor();




    /**
     * 根据用户ID查询用户下面的传感器
     * @param userId
     * @return
     */
    Page<Sensor> findUserSensor(Long userId,Integer page, Integer limit);

    /**
     * 根据用户ID查询用户下面的传感器 (根据条件筛选)
     * @param userId
     * @return
     */
    Page<Sensor> findUserSensor(Long userId,String no,Integer page, Integer limit);


    Page<Sensor> findAllByNoContaining( String no,Integer page, Integer limit);

}
