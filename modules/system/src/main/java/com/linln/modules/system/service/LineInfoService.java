package com.linln.modules.system.service;


import com.linln.modules.system.domain.LineInfo;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface LineInfoService {


    /**
     * 获取分页列表数据
     *
     * @param example 实体对象
     * @return 返回分页数据
     */
    Page<LineInfo> getPageList(Example<LineInfo> example);


    List<LineInfo> findAll();


    /**
     * 根据线路ID查询线路数据
     *
     * @param id 线路ID
     */
    LineInfo getById(Long id);


    /**
     * 保存线路
     *
     * @param lineInfo 线路实体类
     */
    LineInfo save(LineInfo lineInfo);


    /**
     * 修改或者保存线路
     *
     * @param lineInfo 线路实体类
     */
    void saveAndFlush(LineInfo lineInfo);


    /**
     * 根据名称查询线路
     *
     * @param name
     * @return
     */
    LineInfo findByName(String name);


    /**
     * 通过ID删除数据
     *
     * @param id
     */
    void removeById(Long id);


    /**
     * 根据线路名称查询线路下所有的测点数据
     */

    List<Object[]> findByNameGetMeasuringSpotData(String name);


    @Transactional
    void update(LineInfo lineInfo);


}

