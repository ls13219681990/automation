package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.modules.system.domain.TemperatureResistance;
import com.linln.modules.system.domain.TemperatureResistanceData;
import com.linln.modules.system.repository.TemperatureResistanceDataRepository;
import com.linln.modules.system.repository.TemperatureResistanceRepository;
import com.linln.modules.system.service.TemperatureResistanceDataService;
import com.linln.modules.system.service.TemperatureResistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class TemperatureResistanceDataServiceImpl implements TemperatureResistanceDataService {

    @Autowired
    private TemperatureResistanceDataRepository temperatureResistanceDataRepository;

    @Autowired
    EntityManager entityManager;



    @Override
    public Page<TemperatureResistanceData> getPageList(Example<TemperatureResistanceData> example) {
        PageRequest page = PageSort.pageSensorRequest();
        return  temperatureResistanceDataRepository.findAll(example, page);
    }

    @Override
    public void save(TemperatureResistanceData temperatureResistanceData) {
        temperatureResistanceDataRepository.save(temperatureResistanceData);
    }

    @Override
    public void saveAll(List<TemperatureResistanceData> temperatureResistanceData) {
        temperatureResistanceDataRepository.saveAll(temperatureResistanceData);
    }

    @Override
    public void update(TemperatureResistanceData temperatureResistanceData) {
        entityManager.merge(temperatureResistanceData);
        entityManager.flush();
    }

    @Override
    public List<TemperatureResistanceData> findByTemperatureResistanceId(Long temperatureResistanceId) {
        return temperatureResistanceDataRepository.findByTemperatureResistanceId(temperatureResistanceId);
    }
}
