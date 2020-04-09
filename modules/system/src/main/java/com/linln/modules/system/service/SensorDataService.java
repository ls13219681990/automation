package com.linln.modules.system.service;

import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.Sensor;
import com.linln.modules.system.domain.SensorData;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface SensorDataService {










    /**
     * 获取分页列表数据
     *
     * @return 返回分页数据
     */
    Page<SensorData> getPageList(Example<SensorData> example);

    /**
     * 保存传感器数据
     *
     * @param sensor 传感器数据实体类
     */
    void save(SensorData sensor);

    /**
     * 保存传感器数据列表
     *
     * @param sensorsList 传感器数据实体类List
     */
    void save(List<SensorData> sensorsList);




    /**
     * 根据传感器数据ID查询传感器数据
     *
     * @param id 传感器ID
     */
    Sensor findByid(Long id);

    /**
     * 新增或者修改传感器
     *
     * @param sensorData 传感器实体
     */
    void saveAndFlush(SensorData sensorData);



    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);



    List<SensorData> findAll();

    /**
     * 根据传感器编号查找传感器
     * @RETURN 传感器列表
     */

    public SensorData getByNo(String no);





    /**
     * 查询辅助传感器
     * @RETURN 传感器列表
     */

    List<SensorData> findByAuxiliarySensor();





    /**
     * 通过传感器ID和时间查询传感器数据
     */
    SensorData findBySensorIdAndReceiveTime(Long sensorId,String time);





    /**
     * 通过传感器ID查询传感器数据
     * @param sensorId
     * @return
     */
    SensorData findBySensorId(Long sensorId);
    
    
    
    
    
    
}
