package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.Sensor;
import com.linln.modules.system.domain.SensorData;
import com.linln.modules.system.repository.SensorDataRepository;
import com.linln.modules.system.repository.SensorRepository;
import com.linln.modules.system.service.SensorDataService;
import com.linln.modules.system.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorDataServiceImpl implements SensorDataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;


    @Override
    public Page<SensorData> getPageList(Example<SensorData> example) {

        PageRequest page = PageSort.pageSensorDataDESC();
        return  sensorDataRepository.findAll(example, page);



    }

    @Override
    public void save(SensorData sensor) {
        sensorDataRepository.save(sensor);
    }

    @Override
    public void save(List<SensorData> sensorsList) {
        sensorDataRepository.saveAll(sensorsList);
    }

    @Override
    public Sensor findByid(Long id) {
        return null;
    }

    @Override
    public void saveAndFlush(SensorData sensorData) {

    }

    @Override
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return null;
    }

    @Override
    public List<SensorData> findAll() {
        return sensorDataRepository.findAll();
    }

    @Override
    public SensorData getByNo(String no) {
        return null;
    }

    @Override
    public List<SensorData> findByAuxiliarySensor() {
        return null;
    }

    @Override
    public SensorData findBySensorIdAndReceiveTime(Long sensorId, String time) {
        return sensorDataRepository.findBySensorIdAndReceiveTime(sensorId,time);
    }

    @Override
    public SensorData findBySensorId(Long sensorId) {
        return sensorDataRepository.findBySensorId(sensorId);
    }
}
