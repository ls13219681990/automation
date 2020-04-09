package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.Sensor;
import com.linln.modules.system.domain.SensorParameter;
import com.linln.modules.system.repository.SensorParameterRepository;
import com.linln.modules.system.repository.SensorRepository;
import com.linln.modules.system.service.SensorParameterService;
import com.linln.modules.system.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorParameterServiceImpl implements SensorParameterService {

    @Autowired
    private SensorParameterRepository sensorParameterRepository;


    @Override
    public Page<SensorParameter> getPageList(Example<SensorParameter> example) {
        return null;
    }

    @Override
    public void save(SensorParameter sensor) {
        sensorParameterRepository.save(sensor);
    }

    @Override
    public void save(List<SensorParameter> sensorsList) {
        sensorParameterRepository.saveAll(sensorsList);
    }

    @Override
    public Sensor findByid(Long id) {
        return null;
    }

    @Override
    public void saveAndFlush(SensorParameter sensor) {
        sensorParameterRepository.saveAndFlush(sensor);
    }

    @Override
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return null;
    }

    @Override
    public List<SensorParameter> findAll() {
        return null;
    }

    @Override
    public SensorParameter findBySensourId(Long id) {
        return sensorParameterRepository.findBySensourId(id);
    }
}
