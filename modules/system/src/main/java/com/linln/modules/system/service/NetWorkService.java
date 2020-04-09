package com.linln.modules.system.service;

import com.linln.modules.system.domain.AcquisitionSensor;
import com.linln.modules.system.domain.NetWork;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NetWorkService {


    /**
     * 获取分页列表数据
     * @param example 实体对象
     * @return 返回分页数据
     */
    Page<NetWork> getPageList(Example<NetWork> example);







    /**
     * 根据4G网络ID查询4G网络数据
     *
     * @param id 4G网络ID
     */
    NetWork getById(Long id);


    /**
     * 保存4G网络
     *
     * @param actionLog 4G网络实体类
     */
    NetWork save(NetWork actionLog);

/**
 * 通过注册标识获取4G网络数据
 *
 * @param registerSSID
 * @return
 */

    NetWork getByRegisterSSID(String registerSSID);




    /*
     * 通过注册标识和采集仪编号获取采集仪传感器中间表
     * @param registerSSID
     * @return
     */

    List<AcquisitionSensor> findByRegisterSSIDAndAc(String registerSSID, String acquisitionNo);




    /**
     * 修改或者保存4G网络
     * @param netWork 4G网络实体类
     */
    void saveAndFlush(NetWork netWork);



    Page<NetWork> findAll(Integer page, Integer limit);
    List<NetWork> findAll();
    /**
     * 查询该用户的四G网络
     * @param userid
     * @param registerSSID
     * @param page
     * @param limit
     * @return
     */
    Page<NetWork> finUserNetWork(Long userid, String registerSSID, Integer page, Integer limit);


    /**
     * 查询用户对应的 4G模块
     * @param userid
     * @param ssid
     * @return
     */

    Page<NetWork> finUserNetWork(Long userid,Integer page, Integer limit);




    Page<NetWork> findAllByRegisterSSIDLike(String s,Integer page, Integer limit);



    /**
     * 通过标识查询4G模块
     * @param RegisterSSID
     * @return
     */
    NetWork findByRegisterSSID (String RegisterSSID);



    void deleteById(Long id);


}
