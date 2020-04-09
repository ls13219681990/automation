package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.modules.system.domain.BidSection;
import com.linln.modules.system.domain.LineInfo;
import com.linln.modules.system.domain.MeasuringSpotData;
import com.linln.modules.system.domain.WorkSpot;
import com.linln.modules.system.repository.LineInfoRepository;
import com.linln.modules.system.repository.WorkSpotRepository;
import com.linln.modules.system.service.LineInfoService;
import com.linln.modules.system.service.WorkSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Service
public class WorkSpotServiceImpl implements WorkSpotService {

    @Autowired
    private WorkSpotRepository workSpotRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<WorkSpot> getPageList(Example<WorkSpot> example) {

        PageRequest page = PageSort.pageNoRequest();

        return workSpotRepository.findAll(example, page);
    }

    @Override
    public WorkSpot getById(Long id) {
        return workSpotRepository.getById(id);
    }

    @Override
    public WorkSpot save(WorkSpot workSpot) {
        return workSpotRepository.save(workSpot);
    }

    @Override
    public void saveAndFlush(WorkSpot workSpot) {
        workSpotRepository.saveAndFlush(workSpot);
    }

    @Override
    public WorkSpot findByName(String name) {
        return workSpotRepository.findByName(name);
    }

    @Override
    public List<WorkSpot> findAll() {
        return workSpotRepository.findAll();
    }

    @Override
    public WorkSpot findById(Long id) {
        return workSpotRepository.getById(id);
    }

    @Override
    public void removeById(Long id) {
        workSpotRepository.removeById(id);
    }

    @Override
    public List<Object[]> findByNameGetMeasuringSpotData(String name) {
        return workSpotRepository.findByNameGetMeasuringSpotData(name);
    }

    @Override
    public List<Object[]> findByNameGetMeasuringSpotData(String name, String startTime, String endTime) {
        return workSpotRepository.findByNameGetMeasuringSpotData(name, startTime, endTime);
    }

    @Override
    public void update(WorkSpot workSpot) {
        entityManager.merge(workSpot);
        entityManager.flush();
    }

    @Override
    public List<WorkSpot> findByBidSectionId(Long id) {
        return workSpotRepository.findByBidSectionId(id);
    }

    @Override
    public List<Object[]> findByWorkSpotIdAndStartTimeAndEndTime(Long id, String startTime, String endTime) {

        List<Object[]> data  =new ArrayList<>();
        if (startTime != null && endTime == null) {//从开始时间查询
            data =   workSpotRepository.findByWorkSpotIdAndStartTime(id, startTime);
        } else if (startTime == null && endTime != null) {//查结束时间之前的
            data =   workSpotRepository.findByWorkSpotIdAndEndTime(id, endTime);
        } else {
            data =   workSpotRepository.findByWorkSpotIdAndStartTimeAndEndTime(id, startTime, endTime);
        }
        return data;

    }
}
