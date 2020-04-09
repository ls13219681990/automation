package com.linln.modules.system.service;

import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.AcquisitionInstrument;
import com.linln.modules.system.domain.ActionLog;
import com.linln.modules.system.domain.MeasuringSpot;
import com.linln.modules.system.domain.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface AcquisitionInstrumentService {

    /**
     * 获取分页列表数据
     * @param example 实体对象
     * @return 返回分页数据
     */
    Page<AcquisitionInstrument> getPageList( );


    /**
     * 保存用户
     *
     *
     *
     * @param acquisitionInstrument 用户实体类
     */
    void save(AcquisitionInstrument acquisitionInstrument);

    /**
     * 保存用户列表
     * @param acquisitionList 用户实体类
     */
    void save(List<AcquisitionInstrument> acquisitionList);

    /**
     * 根据用户名查询用户数据
     * @param acquisitionNo 采集仪编号
     * @return 采集仪数据
     */
    AcquisitionInstrument getByAcquisitionNo(String acquisitionNo);

    /**
     * 根据用户ID查询采集仪数据
     * @param id 采集仪ID
     */
     AcquisitionInstrument findByid(Long id);

    /**
     * 保存/修改采集仪
     * @param acquisitionInstrument 采集仪实体
     */
    void saveAndFlush(AcquisitionInstrument acquisitionInstrument);

    /**
     * 删除采集仪
     * @param id 采集仪实体ID
     */
    void deleteById(Long id);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);

    /**
     * 查询全部采集仪
     *
     */
    Page<AcquisitionInstrument> findAll(Integer page, Integer limit);

    List<AcquisitionInstrument> findAll();
    /**
     * 根据采集仪编号查询
     * @param no 采集仪编号
     */
    AcquisitionInstrument findByNo(String  no);

    /**
     * 更新采集仪
     *
     *
     *
     * @param acquisitionInstrument 用户实体类
     */
    @Transactional
    void update(AcquisitionInstrument acquisitionInstrument);


    /**
     * 通过工点ID查询工点关联的采集仪
     */

    List<AcquisitionInstrument> findByWorkSpotId(Long id );




    /**
     * 查询四G模块下的采集仪
     * @param id
     * @return
     */
    List<AcquisitionInstrument> getByNetWorkId(Long id);




    /**
     * 通过ID删除数据
     * @param id
     */
    void removeById(Long id);



    /**
     * 批量删除
     * @param menus 批量数据ID
     */
    void deleteBatch(List<AcquisitionInstrument> menus);




    /**
     * 获取未绑定的采集仪
     */
    List<AcquisitionInstrument> findByWorkSpotIdIsNull();



    /**
     * 通过工点ID查询工点关联的采集仪  查出来有关联的
     */
    List<AcquisitionInstrument> findByWorkSpotIdAll(Long id,Long measuringSpotId );



    /**
     * 通过ID查询传感器是否绑定测点返回测定信息
     * @param id
     * @return
     */
    MeasuringSpot findByAcquisitionIDGetMeasuringSpot(Long id );



    /**
     * 查询用户的采集仪(资源权限 == > 查看未关联和关联了自己的)
     * @param userId
     * @return
     */
    Page<AcquisitionInstrument> findUserAcquisition(Long userId,Integer page, Integer limit);

    /**
     *  模糊查询
     * @param userId
     * @param no
     * @return
     */
    Page<AcquisitionInstrument> findUserAcquisitionByNo(Long userId,String no,Integer page, Integer limit);


    /**
     * 通过 编号模糊查询
     * @param no
     * @return
     */
    Page<AcquisitionInstrument> findByNoLike(String no,Integer page, Integer limit);


    /**
     * 修改间隔时间
     *
     * @param id
     */
    void updateInterval(Long id,Integer interval);



    /**
     * 获取网络模块下面的所有的采集仪
     * @param id
     * @return
     */
    List<AcquisitionInstrument> findByNetWorkId( String id);



}
