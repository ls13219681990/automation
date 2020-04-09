package com.linln.modules.system.repository;


import com.linln.modules.system.domain.Report;
import com.linln.modules.system.domain.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface TemplateRepository extends CrudRepository<Template, Long> {


    Page<Template> findAllByOrderByUploadDateDesc(org.springframework.data.domain.Pageable page);

    Template findByid(Long id);

    /**
     * 按照模板名称模糊屁匹配
     * @param name
     * @return
     */
    Page<Template>  findAllByNameContaining(String name,org.springframework.data.domain.Pageable page);

    /**
     * 根据模板的查询时间进行查询
     * @param startDate
     * @param EndDate
     * @return
     */
    Page<Template> findByUploadDateGreaterThanAndUploadDateLessThan(String startDate, String EndDate,org.springframework.data.domain.Pageable page);


}
