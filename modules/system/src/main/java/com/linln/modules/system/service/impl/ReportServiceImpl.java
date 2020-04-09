package com.linln.modules.system.service.impl;


import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.Common.EntityUtils;
import com.linln.modules.system.domain.AcquisitionInstrument;
import com.linln.modules.system.domain.MeasuringSpot;
import com.linln.modules.system.domain.Report;
import com.linln.modules.system.page.ReportDataInfo;
import com.linln.modules.system.page.ReportDataPage;
import com.linln.modules.system.repository.AcquisitionInstrumentRepository;
import com.linln.modules.system.repository.ReportRepository;
import com.linln.modules.system.service.AcquisitionInstrumentService;
import com.linln.modules.system.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public void save(Report report) {
        reportRepository.save(report);
    }

    @Override
    public void delete(Report report) {
        reportRepository.delete(report);
    }

    @Override
    public void update(Report report) {
        reportRepository.save(report);
    }

    @Override
    public List<Report> findByInputIdOrderByIdDesc(String id) {
        return reportRepository.findByInputIdOrderByIdDesc(id);
    }

    @Override
    public Report findByid(Long id) {
        return reportRepository.findByid(id);
    }

    @Override
    public List<Report> findAll() {
        List<Report> reportList = new ArrayList<>();
        Iterable<Report> all = reportRepository.findAll();
        for (Report report: all) {
            reportList.add(report);
        }
        return reportList;
    }

    @Override
    public Page<Report> findAll(Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        return reportRepository.findAll( pageable);
    }

    @Override
    public List<Report> findByWorkSpotId(Long workSpotId) {
        return reportRepository.findByWorkSpotId(workSpotId);
    }

    @Override
    public Page<Report> findByWorkSpotId(Long workSpotId, Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        return reportRepository.findByWorkSpotId( workSpotId,pageable);
    }

    @Override
    public Page<Report> findByNameOrDate(Integer page, Integer limit, Long workSpotId, String name, String startDate, String endDate) {
        Pageable pageable = new PageRequest(page - 1, limit);

        if (!StringUtils.isEmpty(name)) {
            return reportRepository.findByWorkSpotIdAndNameContaining(workSpotId,name,pageable);
        }else if (!StringUtils.isEmpty(startDate) || !StringUtils.isEmpty(endDate)){
            return reportRepository.findByWorkSpotIdAndInputTimeGreaterThanAndInputTimeLessThan(workSpotId,startDate,endDate,pageable);
        }
        return null;
    }

    @Override
    public List<ReportDataInfo> findByMeasuringIdAndDate(Long id, String startDate, String EndDate) {
        List<Object[]> byMeasuringIdAndDate = reportRepository.findByMeasuringIdAndDate(id, startDate, EndDate);


        return EntityUtils.castEntity(byMeasuringIdAndDate, ReportDataInfo.class, new ReportDataInfo());
    }

    @Override
    public List<Report> findAllByWorkSpotIdAndInputTimeLessThan(Long id, String date) {
        return reportRepository.findAllByWorkSpotIdAndInputTimeLessThan(id, date);
    }

    @Override
    public List<Report> findAllByInputIdOrderByInputTimeDesc(String id) {
        return reportRepository.findAllByInputIdOrderByInputTimeDesc(id);
    }

    @Override
    public List<Report> findAllByInputIdOrderByInputTimeDesc(String id,Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        return reportRepository.findAllByInputIdOrderByInputTimeDesc(id,pageable);
    }


}
