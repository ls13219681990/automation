package com.linln.modules.system.service;

import com.linln.modules.system.domain.BidSection;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/12/02
 */
public interface BidSectionService {



    /**
     * 获取分页列表数据
     * @param example 实体对象
     * @return 返回分页数据
     */
    Page<BidSection> getPageList(Example<BidSection> example);
 

    

    /**
     * 根据标段ID查询标段数据
     * @param id 标段ID
     */
    BidSection getById(Long id);

  
    /**
     * 保存标段
     * @param bidSection 标段实体类
     */
    BidSection save(BidSection bidSection);


    /**
     * 修改或者保存标段
     * @param bidSection 标段实体类
     */
    void saveAndFlush(BidSection bidSection);


    /**
     * 根据名称查询标段
     * @param name
     * @return
     */
    BidSection findByName (String name);


    /**
     * 通过ID删除数据
     * @param id
     */
    void removeById(Long id);




    /**
     * 根据标段名称查询线路下所有的测点数据
     */
    List<Object[]> findByNameGetMeasuringSpotData(String bidSectionName);
    List<Object[]> findByNameGetMeasuringSpotData(String bidSectionName, String startTime, String endTime);


    @Transactional
    void update(BidSection bidSection);


    /**
     * 根据线路ID查询对应标段
     * @param id
     * @param page
     * @param limit
     * @return
     */
    Page<BidSection> findByLineInfoId(Long id, Integer page, Integer limit);



    /**
     * 根据线路ID查询对应标段
     * @param id
     * @return
     */
    List<BidSection> findByLineInfoId(Long id);

    /**
     * 查询全部标段
     * @return
     */
    List<BidSection> findAll();


}

