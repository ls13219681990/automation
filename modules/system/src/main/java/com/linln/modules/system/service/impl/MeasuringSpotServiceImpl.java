package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.modules.system.domain.*;
import com.linln.modules.system.repository.MeasuringSpotRepository;
import com.linln.modules.system.service.MeasuringSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Service
public class MeasuringSpotServiceImpl implements MeasuringSpotService {

    @Autowired
    private MeasuringSpotRepository measuringSpotRepository;


    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<MeasuringSpot> getPageList(Example<MeasuringSpot> example) {
        PageRequest page = PageSort.pageNoRequest();
        return measuringSpotRepository.findAll(example, page);
    }

    @Override
    public MeasuringSpot getById(Long id) {
        return measuringSpotRepository.getOne(id);
    }

    @Override
    public MeasuringSpot save(MeasuringSpot measuringSpot) {
        return measuringSpotRepository.save(measuringSpot);
    }

    @Override
    public void saveAndFlush(MeasuringSpot measuringSpot) {
        measuringSpotRepository.saveAndFlush(measuringSpot);
    }

    @Override
    public MeasuringSpot findByName(String name) {
        return measuringSpotRepository.findByName(name);
    }

    @Override
    public List<SensorData> findSensorDataByNamePage(String name, Integer page, Integer total) {


        PageRequest pageable = PageRequest.of(page - 1, total);

        List<SensorData> sensorDataByName = measuringSpotRepository.findSensorDataByNamePage(name, pageable);

        entityManager.clear();
        return sensorDataByName;
    }

    @Override
    public List<SensorData> findSensorDataByName(String name) {
        return measuringSpotRepository.findSensorDataByName(name);
    }

    @Override
    public Sensor findMeasuringSpotSensorAuxiliarySensorByName(String name) {
        return measuringSpotRepository.findMeasuringSpotSensorAuxiliarySensorByName(name);
    }

    @Override
    public void removeById(Long id) {
        measuringSpotRepository.removeById(id);
    }

    @Override
    public List<Object[]> findByAcquisitionNoGetMeasuringSpotSensor(String no,String registerSSID) {
        return measuringSpotRepository.findByAcquisitionNoGetMeasuringSpotSensor(no,registerSSID);
    }

    @Override
    public List<MeasuringSpot> findByAcquisitionNo(String no) {
        return measuringSpotRepository.findByAcquisitionNo(no);
    }

    @Override
    public void update(MeasuringSpot measuringSpot) {
        entityManager.merge(measuringSpot);
        entityManager.flush();
    }

    @Override
    public Page<MeasuringSpot> findByWorkSpotId(Long id, Integer page, Integer limit) {

        Pageable pageable = new PageRequest(page-1,limit);
        return measuringSpotRepository.findByWorkSpotId(id,pageable);
    }

    @Override
    public List<MeasuringSpot> findByWorkSpotId(Long id) {
        return measuringSpotRepository.findByWorkSpotId(id);
    }

    @Override
    public MeasuringSpot findById(Long id) {
        return measuringSpotRepository.getById(id);
    }
}
