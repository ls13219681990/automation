package com.linln.modules.system.service;

import com.linln.modules.system.domain.Report;
import com.linln.modules.system.domain.Template;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface TemplateService {





    void save(Template template);

    void delete(Template template);

    void update(Template template);




    Template findByid(Long id);


    List<Template> findAll();

    Page<Template> findAll(Integer page, Integer limit);


    Page<Template> findByNameOrDate(Integer page, Integer limit,String name,String startDate,String endDate);

}
