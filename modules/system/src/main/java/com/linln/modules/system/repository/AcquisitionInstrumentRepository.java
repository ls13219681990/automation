package com.linln.modules.system.repository;


import com.linln.modules.system.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface AcquisitionInstrumentRepository extends CrudRepository<AcquisitionInstrument, Long> {
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
     * 根据采集仪名称
     *
     * @param name
     * @return
     */
    public AcquisitionInstrument getByName(String name);


    /**
     * 根据采集仪编号
     *
     * @param no
     * @return
     */
    @Query("select ai from AcquisitionInstrument ai where  ai.no = :no")
    public AcquisitionInstrument getByNo(@Param("no") String no);


    /* public void update (AcquisitionInstrument acquisitionInstrument);*/


    /**
     * 通过工点ID查询工点关联的采集仪 查全部的
     */

    @Query(value = "select * from acquisition_instrument ai where  ai.id  not in (SELECT DISTINCT\n" +
            "\tai.id\n" +
            "FROM\n" +
            "\tacquisition_instrument ai\n" +
            "\tINNER JOIN acquisition_sensor sen ON sen.acquisition_id = ai.id\n" +
            "\tINNER JOIN measuring_spot_sensor mss ON mss.acquisition_sensor_id = sen.id \n" +
            "\t\n" +
            ") and \n" +
            "\tai.work_spot_id = ?1",nativeQuery = true)
    List<AcquisitionInstrument> findByWorkSpotId( Long id);


    /**
     * 通过工点ID查询工点关联的采集仪  查出来有关联的
     */

    @Query(value = "SELECT distinct ai.* FROM acquisition_instrument ai inner join acquisition_sensor sen on sen.acquisition_id = ai.id inner join measuring_spot_sensor ms on ms.acquisition_sensor_id = sen.id and ms.measuring_spot_id = ?2 where ai.work_spot_id = ?1 ", nativeQuery = true)
    List<AcquisitionInstrument> findByWorkSpotIdAll(Long id,Long measuringSpotId);

    /**
     * 查询四G模块下的采集仪
     *
     * @param id
     * @return
     */
    List<AcquisitionInstrument> getByNetWorkId(Long id);


    /**
     * 通过ID删除数据
     *
     * @param id
     */
    @Transactional
    void removeById(Long id);

    /**
     * @param ids
     * @function 自定义JPQL
     */
    // 注意更新和删除是需要加事务的， 并且要加上 @Modify的注解
    @Modifying
    @Transactional
    @Query("delete from AcquisitionInstrument ai where ai.id in (?1)")
    void deleteBatch(List<Long> ids);


    /**
     * 取消工点和采集仪的关联
     *
     * @param id
     */
    @Modifying
    @Transactional
    @Query("update AcquisitionInstrument ai set ai.workSpotId = null where ai.id = ?1")
    void updateAcquisitionInstrument(Long id);


    /**
     * 获取未绑定的采集仪
     */
    List<AcquisitionInstrument> findByWorkSpotIdIsNull();


    /**
     * 通过ID查询传感器是否绑定测点返回测定信息
     *
     * @param id
     * @return
     */
    @Query(value = "select ms.* from  acquisition_instrument ai \n" +
            "inner join acquisition_sensor aisen on aisen.acquisition_id = ai.id\n" +
            "inner join measuring_spot_sensor mss on mss.acquisition_sensor_id = aisen.id \n" +
            "inner join measuring_spot ms on ms.id = mss.measuring_spot_id\n" +
            " where ai.id = ?1 LIMIT 1", nativeQuery = true)
    MeasuringSpot findByAcquisitionIDGetMeasuringSpot(Long id);


    /**
     * 查询用户的采集仪(资源权限 == > 查看未关联和关联了自己的)
     *
     * @param userId
     * @return
     */
    @Query(value = "select ai.* from acquisition_instrument  ai where ai.work_spot_id is null or ai.work_spot_id in (\n" +
            "select ws.id from work_spot ws \n" +
            "inner join role_work_spot rws on rws.work_spot_id =ws.id\n" +
            "inner join sys_user_role sur on sur.role_id = rws.role_id\n" +
            "inner join sys_user su on su.id = sur.user_id \n" +
            "where su.id = ?1\n" +
            ")", nativeQuery = true)
    Page<AcquisitionInstrument> findUserAcquisition(Long userId,org.springframework.data.domain.Pageable pageable);


    /**
     * 查询用户的采集仪(资源权限 == > 查看未关联和关联了自己的)
     *
     * @param userId
     * @return
     */
    @Query(value = " select ai.* from (select ai.* from acquisition_instrument  ai where  ai.no like %?2%  and  ai.work_spot_id is null   or ai.work_spot_id in (\n" +
            "select ws.id from work_spot ws \n" +
            "inner join role_work_spot rws on rws.work_spot_id =ws.id\n" +
            "inner join sys_user_role sur on sur.role_id = rws.role_id\n" +
            "inner join sys_user su on su.id = sur.user_id \n" +
            "where su.id = ?1 ) ) ai where  ai.no like %?2% ", nativeQuery = true)
    Page<AcquisitionInstrument> findUserAcquisitionByNo(Long userId, String no,org.springframework.data.domain.Pageable pageable);


    //@Query("select ai from AcquisitionInstrument ai where ai.no like %?1%")
    Page<AcquisitionInstrument> findAllByNoContaining(String no,org.springframework.data.domain.Pageable pageable);





    /**
     * 修改间隔时间
     *
     * @param id
     */
    @Modifying
    @Transactional
    @Query(value = "update acquisition_instrument ai  set ai.interval_time = ?2 where ai.id = ?1",nativeQuery = true)
    void updateInterval(Long id,Integer interval);


    /**
     * 获取网络模块下面的所有的采集仪
     * @param id
     * @return
     */
    List<AcquisitionInstrument> findByNetWorkId(String id);


    Page<AcquisitionInstrument> findAll(org.springframework.data.domain.Pageable page);


}
