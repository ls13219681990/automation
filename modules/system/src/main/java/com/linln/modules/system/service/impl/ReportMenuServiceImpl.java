package com.linln.modules.system.service.impl;


import com.linln.modules.system.domain.Report;
import com.linln.modules.system.domain.ReportMenu;
import com.linln.modules.system.repository.ReportMenuRepository;
import com.linln.modules.system.repository.ReportRepository;
import com.linln.modules.system.service.ReportMenuService;
import com.linln.modules.system.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class ReportMenuServiceImpl implements ReportMenuService {

    @Autowired
    private ReportMenuRepository reportMenuRepository;

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public List<ReportMenu> getListByExample(Example<ReportMenu> example, Sort sort) {
        return reportMenuRepository.findAll(example, sort);
    }

    @Override
    public void save(ReportMenu reportMenu) {
        reportMenuRepository.save(reportMenu);
    }

    @Override
    public void delete(ReportMenu reportMenu) {
        reportMenuRepository.delete(reportMenu);
    }

    @Override
    public void update(ReportMenu reportMenu) {
        reportMenuRepository.save(reportMenu);
    }
}
