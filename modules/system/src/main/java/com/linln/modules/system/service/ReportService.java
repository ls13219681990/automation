package com.linln.modules.system.service;

import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.AcquisitionInstrument;
import com.linln.modules.system.domain.MeasuringSpot;
import com.linln.modules.system.domain.Report;
import com.linln.modules.system.page.ReportDataInfo;
import com.linln.modules.system.page.ReportDataPage;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface ReportService {

    void save(Report report);

    void delete(Report report);

    void update(Report report);


    /**
     * 根据用户ID查询 报告记录
     * @param id
     * @return
     */
    List<Report> findByInputIdOrderByIdDesc(String id);


    Report findByid(Long id );


    List<Report> findAll();

    Page<Report> findAll(Integer page, Integer limit);

    List<Report> findByWorkSpotId(Long workSpotId);

    Page<Report> findByWorkSpotId(Long workSpotId,Integer page, Integer limit);


    Page<Report> findByNameOrDate(Integer page, Integer limit,Long workSpotId,String name,String startDate,String endDate);

    /**
     * 根据id、开始时间和结束时间 获取折线图数据
     * @param id
     * @param startDate
     * @param EndDate
     * @return
     */
    List<ReportDataInfo> findByMeasuringIdAndDate(Long id, String startDate, String EndDate);


    List<Report> findAllByWorkSpotIdAndInputTimeLessThan(Long id,String date);

    /**
     *通过录入人ID查询报告
     * @param id
     * @return
     */
    List<Report> findAllByInputIdOrderByInputTimeDesc(String id);

    List<Report> findAllByInputIdOrderByInputTimeDesc(String id,Integer page, Integer limit);
}
