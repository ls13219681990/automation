package com.linln.modules.system.service.impl;


import com.linln.modules.system.domain.Report;
import com.linln.modules.system.domain.Template;
import com.linln.modules.system.repository.ReportRepository;
import com.linln.modules.system.repository.TemplateRepository;
import com.linln.modules.system.service.ReportService;
import com.linln.modules.system.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public void save(Template template) {
        templateRepository.save(template);
    }

    @Override
    public void delete(Template template) {
        templateRepository.delete(template);
    }

    @Override
    public void update(Template template) {
        templateRepository.save(template);
    }

    @Override
    public Template findByid(Long id) {
        return templateRepository.findByid(id);
    }

    @Override
    public List<Template> findAll() {
        List<Template> templates = new ArrayList<>();
        Iterable<Template> all = templateRepository.findAll();
        for (Template template : all) {
            templates.add(template);
        }
        return templates;
    }

    @Override
    public Page<Template> findAll(Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page - 1, limit);
        return templateRepository.findAllByOrderByUploadDateDesc(pageable);
    }

    @Override
    public Page<Template> findByNameOrDate(Integer page, Integer limit, String name, String startDate, String endDate) {
        Pageable pageable = new PageRequest(page - 1, limit);

        if (!StringUtils.isEmpty(name)) {
            return templateRepository.findAllByNameContaining(name,pageable);
        }else if (!StringUtils.isEmpty(startDate) || !StringUtils.isEmpty(endDate)){
            return templateRepository.findByUploadDateGreaterThanAndUploadDateLessThan(startDate,endDate,pageable);
        }
        return null;
    }


}
