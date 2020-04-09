package com.linln.modules.system.repository;


import com.linln.modules.system.domain.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface ReportRepository extends CrudRepository<Report, Long> {
    /**
     * 根据用户ID查询 报告记录
     * @param id
     * @return
     */
    List<Report> findByInputIdOrderByIdDesc(String id);



    Page<Report> findAll(org.springframework.data.domain.Pageable page);


    List<Report> findByWorkSpotId(Long workSpotId);

    Page<Report> findByWorkSpotId(Long workSpotId,org.springframework.data.domain.Pageable page);

    Report findByid(Long id );

    Page<Report> findByWorkSpotIdAndNameContaining(Long workSpotId,String name, org.springframework.data.domain.Pageable page);

    Page<Report> findByWorkSpotIdAndInputTimeGreaterThanAndInputTimeLessThan(Long workSpotId, String startDate, String endDate, org.springframework.data.domain.Pageable page);


    /**
     * 根据id、开始时间和结束时间 获取折线图数据
     * @param id
     * @param startDate
     * @param EndDate
     * @return
     */
    @Query(value = "SELECT date_format(od.RepDate,'%Y-%m-%d %H:%i:%s') date,msd.measuring_spot_value value \n" +
            "FROM\n" +
            "\tobj_date od\n" +
            "\tleft JOIN ( SELECT date_format(msd.receive_time,'%Y-%m-%d') date,msd.measuring_spot_value FROM measuring_spot_data msd where measuring_spot_id = ?1) msd ON msd.date = od.RepDate \n" +
            " WHERE od.RepDate >= ?2 and od.RepDate <=?3 "+
            "ORDER BY od.RepDate ", nativeQuery = true)
    List<Object[]> findByMeasuringIdAndDate(Long id,String startDate,String EndDate);

    /**
     * 通过工点ID和时间查询最后一条数据
     * @param id
     * @param date
     * @return
     */
    @Query(value = "SELECT * FROM report r where r.work_spot_id = ?1 and r.input_time < ?2 order by r.input_time desc",nativeQuery = true)
    List<Report> findAllByWorkSpotIdAndInputTimeLessThan(Long id,String date);

    /**
     *通过录入人ID查询报告
     * @param id
     * @return
     */
    List<Report> findAllByInputIdOrderByInputTimeDesc(String id,org.springframework.data.domain.Pageable page);

    List<Report> findAllByInputIdOrderByInputTimeDesc(String id);

}
