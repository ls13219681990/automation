package com.linln.modules.system.repository;

import com.linln.modules.system.domain.AcquisitionSensor;
import com.linln.modules.system.domain.ActionLog;
import com.linln.modules.system.domain.NetWork;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NetWorkRepository extends CrudRepository<NetWork, Long> {


    /*
     * 通过注册标识获取4G网络数据
     * @param registerSSID
     * @return
     */
    NetWork getByRegisterSSID(String registerSSID);




    /*
     * 通过注册标识和采集仪编号获取采集仪传感器中间表
     * @param registerSSID
     * @return
     */

    @Query("SELECT sensor FROM NetWork nw  left join AcquisitionInstrument ai on  ai.netWorkId = nw.id left join AcquisitionSensor sensor on sensor.acquisitionId = ai.id where nw.registerSSID =:registerSSID and ai.no = :acquisitionNo")
    List<AcquisitionSensor> findByRegisterSSIDAndAc(@Param("registerSSID") String registerSSID, @Param("acquisitionNo") String acquisitionNo);

    /**
     * 通过ID获取4G网络模块数据
     * @param id
     * @return
     */
    NetWork getById(Long id);


    /**
     * 查询该用户的四G网络
     * @param userid
     * @return
     */
    @Query(value = "select * from net_work nw where nw.id in ( SELECT ai.net_work_id FROM acquisition_instrument ai \n" +
            "WHERE ai.work_spot_id IS NULL OR ai.work_spot_id in (\n" +
            "SELECT ws.id  FROM work_spot ws\n" +
            "INNER JOIN role_work_spot rws ON rws.work_spot_id = ws.id\n" +
            "INNER JOIN sys_user_role sur ON sur.role_id = rws.role_id\n" +
            "INNER JOIN sys_user su ON su.id = sur.user_id \n" +
            "WHERE su.id = ?1 ) \n" +
            "\t) \n" +
            "\tOR nw.id IN (SELECT ai.id FROM\n" +
            "\t\t( SELECT nw.id, ai.NAME FROM net_work nw LEFT JOIN acquisition_instrument ai ON ai.net_work_id = nw.id ) ai \n" +
            "\tWHERE ai.NAME IS NULL )",nativeQuery = true)
    Page<NetWork> finUserNetWork(Long userid,org.springframework.data.domain.Pageable page);


    /**
     * 查询该用户的四G网络(条件筛选)
     * @param userid
     * @return
     */
    @Query(value = "select * from net_work nw where nw.id in ( SELECT ai.net_work_id FROM acquisition_instrument ai \n" +
            "WHERE ai.work_spot_id IS NULL OR ai.work_spot_id in (\n" +
            "SELECT ws.id  FROM work_spot ws\n" +
            "INNER JOIN role_work_spot rws ON rws.work_spot_id = ws.id\n" +
            "INNER JOIN sys_user_role sur ON sur.role_id = rws.role_id\n" +
            "INNER JOIN sys_user su ON su.id = sur.user_id \n" +
            "WHERE su.id = ?1 ) \n" +
            "\t) \n" +
            "\tOR nw.id IN (SELECT ai.id FROM\n" +
            "\t\t( SELECT nw.id, ai.NAME FROM net_work nw LEFT JOIN acquisition_instrument ai ON ai.net_work_id = nw.id ) ai \n" +
            "\tWHERE ai.NAME IS NULL ) and nw.registerssid like %?2%",nativeQuery = true)
    Page<NetWork> finUserNetWork(Long userid,String ssid,org.springframework.data.domain.Pageable page);




    Page<NetWork> findAllByRegisterSSIDLike(String ssid, org.springframework.data.domain.Pageable page);


    Page<NetWork> findAll(org.springframework.data.domain.Pageable page);

    /**
     * 通过标识查询4G模块
     * @param RegisterSSID
     * @return
     */
    NetWork findByRegisterSSID (String RegisterSSID);



    void deleteById(Long id);


}
