package com.linln.modules.system.repository;

import com.linln.modules.system.domain.Sensor;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface SensorRepository extends CrudRepository<Sensor, Long> {

/*
    *//**
     * 查找多个角色
     * @RETURN 传感器列表
     *//*
    public 1 findBy();*/

    /**
     * 查找多个角色
     * @RETURN 传感器列表
     */
    public Sensor getById(Long id);



    /**
     * 根据传感器编号查找传感器
     * @RETURN 传感器列表
     */
    @Query("select s from Sensor s where  s.no = :no")
    public Sensor getByNo(@Param("no")String no);

    /**
     * 根据传感器编号查找传感器
     * @RETURN 传感器列表
     */
    @Query("select s from Sensor s where  s.isAuxiliarySensor = 1")
    List<Sensor> findByAuxiliarySensor();


    /**
     * 根据传感器名称查找传感器
     */
    Sensor findByName(String name);






    /**
     * 查询未绑定的传感器
     * @return List<Sensor>
     */
    @Query(value = "select sen.* from sensor sen where sen.id not in (select sen.id from sensor sen inner join acquisition_sensor ac on ac.sensor_id = sen.id)",nativeQuery = true)
    List<Sensor> findUnboundSensor();


    /**
     * 根据用户ID查询用户下面的传感器
     * @param userId
     * @return
     */
    @Query(value = "select sen.* from sensor sen  where  sen.id in (\n" +
            "SELECT\n" +
            "\tasen.id\n" +
            "FROM\n" +
            "\t( SELECT sen.id, asen.passageway_id \n" +
            "\tFROM sensor sen \n" +
            "\tLEFT JOIN acquisition_sensor asen ON sen.id = asen.sensor_id ) AS asen \n" +
            "WHERE\n" +
            "\tasen.passageway_id IS NULL\n" +
            ") or sen.id = (\n" +
            "select sen.id from sensor sen \n" +
            "inner join acquisition_sensor asen on sen.id = asen.sensor_id\n" +
            "inner join acquisition_instrument ai on ai.id = asen.acquisition_id\n" +
            "inner join  work_spot  ws on ws.id = ai.work_spot_id\n" +
            "inner join role_work_spot rws on rws.work_spot_id =ws.id\n" +
            "inner join sys_user_role sur on sur.role_id = rws.role_id\n" +
            "inner join sys_user su on su.id = sur.user_id \n" +
            "where su.id = ?1\n" +
            ") ",nativeQuery = true)
    Page<Sensor> findUserSensor(Long userId,org.springframework.data.domain.Pageable pageable);


    /**
     * 根据用户ID查询用户下面的传感器(条件筛选)
     * @param userId
     * @return
     */
    @Query(value = "select sen.* from sensor sen  where  sen.no like %?2% and sen.id in (\n" +
            "SELECT\n" +
            "\tasen.id\n" +
            "FROM\n" +
            "\t( SELECT sen.id, asen.passageway_id \n" +
            "\tFROM sensor sen \n" +
            "\tLEFT JOIN acquisition_sensor asen ON sen.id = asen.sensor_id ) AS asen \n" +
            "WHERE\n" +
            "\tasen.passageway_id IS NULL\n" +
            ") or sen.id = (\n" +
            "select sen.id from sensor sen \n" +
            "inner join acquisition_sensor asen on sen.id = asen.sensor_id\n" +
            "inner join acquisition_instrument ai on ai.id = asen.acquisition_id\n" +
            "inner join  work_spot  ws on ws.id = ai.work_spot_id\n" +
            "inner join role_work_spot rws on rws.work_spot_id =ws.id\n" +
            "inner join sys_user_role sur on sur.role_id = rws.role_id\n" +
            "inner join sys_user su on su.id = sur.user_id \n" +
            "where su.id = ?1\n" +
            ") ",nativeQuery = true)
    Page<Sensor> findUserSensor(Long userId,String no,org.springframework.data.domain.Pageable pageable);


    @Query(value = "select * from sensor where no like '%?1%' ",nativeQuery = true)
    Page<Sensor> findAllByNoLike(String no, org.springframework.data.domain.Pageable pageable);

    //@Query(value = "select * from sensor limit ?1,?2     ",nativeQuery = true)
    Page<Sensor> findAll(  org.springframework.data.domain.Pageable pageable);

}
